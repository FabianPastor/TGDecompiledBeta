package org.telegram.messenger;

import android.os.SystemClock;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.ConnectionsManager;

public class GcmPushListenerService extends FirebaseMessagingService {
    public static final int NOTIFICATION_ID = 1;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void onMessageReceived(RemoteMessage remoteMessage) {
        StringBuilder stringBuilder;
        String from = remoteMessage.getFrom();
        Map data = remoteMessage.getData();
        long sentTime = remoteMessage.getSentTime();
        long uptimeMillis = SystemClock.uptimeMillis();
        if (BuildVars.LOGS_ENABLED) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("GCM received data: ");
            stringBuilder.append(data);
            stringBuilder.append(" from: ");
            stringBuilder.append(from);
            FileLog.d(stringBuilder.toString());
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$GcmPushListenerService$nHUi11ZwPF6EFepQ1zn-R3cweVE(this, data, sentTime));
        try {
            this.countDownLatch.await();
        } catch (Throwable unused) {
        }
        if (BuildVars.DEBUG_VERSION) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("finished GCM service, time = ");
            stringBuilder.append(SystemClock.uptimeMillis() - uptimeMillis);
            FileLog.d(stringBuilder.toString());
        }
    }

    public /* synthetic */ void lambda$onMessageReceived$4$GcmPushListenerService(Map map, long j) {
        ApplicationLoader.postInitApplication();
        Utilities.stageQueue.postRunnable(new -$$Lambda$GcmPushListenerService$mgGQQF3kpbOfBT2rVoACkZ_x-2M(this, map, j));
    }

    /* JADX WARNING: Removed duplicated region for block: B:268:0x0412  */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x03eb A:{SYNTHETIC, Splitter:B:266:0x03eb} */
    /* JADX WARNING: Removed duplicated region for block: B:561:0x088d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x0882 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:555:0x0877 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x086c A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:549:0x0861 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:546:0x0856 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:543:0x084b A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:540:0x0840 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:537:0x0835 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0829 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:531:0x081d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x0811 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x0806 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x07fb A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x07f0 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x07e4 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x07d8 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x07cc A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x07c0 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:504:0x07b4 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x07a8 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x079c A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0790 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x0785 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x0779 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x076d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0761 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x0755 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x0749 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x073d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x0731 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0725 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x0719 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x070d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0701 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x06f5 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x06e9 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x06dd A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x06d1 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x06c5 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x06b9 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x06ad A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x06a2 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0696 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x068a A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x067e A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0672 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x0666 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x065a A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x064e A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x0642 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x0636 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x062a A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x061e A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0612 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x0607 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x05fb A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x05ef A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x05e3 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x05d7 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x05cb A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x05bf A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x05b3 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x05a7 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x059b A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x058f A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x0583 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x0577 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:357:0x056b A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x055f A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x0554 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x0548 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x053c A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x0530 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x0524 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x0518 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x050c A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x0500 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x04f4 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x04e8 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x04dd A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x04d1 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x04c5 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x04b9 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x04ad A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x04a1 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x0495 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0489 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x047d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x0471 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0465 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x0459 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x044d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0441 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x0435 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x0429 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x041d A:{SYNTHETIC, Splitter:B:272:0x041d} */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x03a0  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0372 A:{SYNTHETIC, Splitter:B:227:0x0372} */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x03be A:{SYNTHETIC, Splitter:B:252:0x03be} */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x03eb A:{SYNTHETIC, Splitter:B:266:0x03eb} */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0412  */
    /* JADX WARNING: Removed duplicated region for block: B:561:0x088d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x0882 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:555:0x0877 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x086c A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:549:0x0861 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:546:0x0856 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:543:0x084b A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:540:0x0840 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:537:0x0835 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0829 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:531:0x081d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x0811 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x0806 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x07fb A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x07f0 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x07e4 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x07d8 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x07cc A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x07c0 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:504:0x07b4 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x07a8 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x079c A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0790 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x0785 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x0779 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x076d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0761 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x0755 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x0749 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x073d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x0731 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0725 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x0719 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x070d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0701 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x06f5 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x06e9 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x06dd A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x06d1 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x06c5 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x06b9 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x06ad A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x06a2 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0696 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x068a A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x067e A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0672 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x0666 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x065a A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x064e A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x0642 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x0636 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x062a A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x061e A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0612 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x0607 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x05fb A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x05ef A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x05e3 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x05d7 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x05cb A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x05bf A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x05b3 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x05a7 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x059b A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x058f A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x0583 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x0577 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:357:0x056b A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x055f A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x0554 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x0548 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x053c A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x0530 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x0524 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x0518 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x050c A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x0500 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x04f4 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x04e8 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x04dd A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x04d1 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x04c5 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x04b9 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x04ad A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x04a1 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x0495 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0489 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x047d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x0471 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0465 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x0459 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x044d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0441 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x0435 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x0429 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x041d A:{SYNTHETIC, Splitter:B:272:0x041d} */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x0356 A:{SYNTHETIC, Splitter:B:217:0x0356} */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0372 A:{SYNTHETIC, Splitter:B:227:0x0372} */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x03a0  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x03be A:{SYNTHETIC, Splitter:B:252:0x03be} */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0412  */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x03eb A:{SYNTHETIC, Splitter:B:266:0x03eb} */
    /* JADX WARNING: Removed duplicated region for block: B:561:0x088d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x0882 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:555:0x0877 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x086c A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:549:0x0861 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:546:0x0856 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:543:0x084b A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:540:0x0840 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:537:0x0835 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0829 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:531:0x081d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x0811 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x0806 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x07fb A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x07f0 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x07e4 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x07d8 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x07cc A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x07c0 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:504:0x07b4 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x07a8 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x079c A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0790 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x0785 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x0779 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x076d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0761 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x0755 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x0749 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x073d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x0731 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0725 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x0719 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x070d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0701 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x06f5 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x06e9 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x06dd A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x06d1 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x06c5 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x06b9 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x06ad A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x06a2 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0696 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x068a A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x067e A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0672 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x0666 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x065a A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x064e A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x0642 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x0636 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x062a A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x061e A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0612 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x0607 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x05fb A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x05ef A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x05e3 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x05d7 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x05cb A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x05bf A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x05b3 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x05a7 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x059b A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x058f A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x0583 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x0577 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:357:0x056b A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x055f A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x0554 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x0548 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x053c A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x0530 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x0524 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x0518 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x050c A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x0500 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x04f4 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x04e8 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x04dd A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x04d1 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x04c5 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x04b9 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x04ad A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x04a1 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x0495 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0489 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x047d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x0471 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0465 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x0459 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x044d A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0441 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x0435 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x0429 A:{Catch:{ Throwable -> 0x0391 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x041d A:{SYNTHETIC, Splitter:B:272:0x041d} */
    /* JADX WARNING: Removed duplicated region for block: B:833:0x1896 A:{Catch:{ Throwable -> 0x1843, Throwable -> 0x18d3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x01db  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x01db  */
    /* JADX WARNING: Removed duplicated region for block: B:833:0x1896 A:{Catch:{ Throwable -> 0x1843, Throwable -> 0x18d3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:833:0x1896 A:{Catch:{ Throwable -> 0x1843, Throwable -> 0x18d3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x01db  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1907  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x190e  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1907  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x190e  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1907  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x190e  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1907  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x190e  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1907  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x190e  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1907  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x190e  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1907  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x190e  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1907  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x190e  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1907  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x190e  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1907  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x190e  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1907  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x190e  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1907  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x190e  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1907  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x190e  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1907  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x190e  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1907  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x190e  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1907  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x190e  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1907  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x190e  */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:565:0x0899, code skipped:
            r15 = "Photos";
            r16 = r14;
            r14 = "ChannelMessageFew";
            r29 = r3;
            r3 = " ";
            r30 = r2;
            r2 = "AttachSticker";
     */
    /* JADX WARNING: Missing block: B:566:0x08a7, code skipped:
            switch(r8) {
                case 0: goto L_0x16ac;
                case 1: goto L_0x1686;
                case 2: goto L_0x1665;
                case 3: goto L_0x1644;
                case 4: goto L_0x1623;
                case 5: goto L_0x1601;
                case 6: goto L_0x15e4;
                case 7: goto L_0x15c2;
                case 8: goto L_0x15a0;
                case 9: goto L_0x153d;
                case 10: goto L_0x1519;
                case 11: goto L_0x14f5;
                case 12: goto L_0x14d1;
                case 13: goto L_0x14b2;
                case 14: goto L_0x1492;
                case 15: goto L_0x1472;
                case 16: goto L_0x144d;
                case 17: goto L_0x142a;
                case 18: goto L_0x1405;
                case 19: goto L_0x13db;
                case 20: goto L_0x13b6;
                case 21: goto L_0x138f;
                case 22: goto L_0x1371;
                case 23: goto L_0x1356;
                case 24: goto L_0x1332;
                case 25: goto L_0x1315;
                case 26: goto L_0x12f8;
                case 27: goto L_0x12db;
                case 28: goto L_0x12bd;
                case 29: goto L_0x1260;
                case 30: goto L_0x1242;
                case 31: goto L_0x121f;
                case 32: goto L_0x11fc;
                case 33: goto L_0x11de;
                case 34: goto L_0x11c0;
                case 35: goto L_0x11a2;
                case 36: goto L_0x1184;
                case 37: goto L_0x1165;
                case 38: goto L_0x113b;
                case 39: goto L_0x1117;
                case 40: goto L_0x10f1;
                case 41: goto L_0x10dc;
                case 42: goto L_0x10bb;
                case 43: goto L_0x1098;
                case 44: goto L_0x1075;
                case 45: goto L_0x1052;
                case 46: goto L_0x102f;
                case 47: goto L_0x100c;
                case 48: goto L_0x0var_;
                case 49: goto L_0x0var_;
                case 50: goto L_0x0var_;
                case 51: goto L_0x0f0a;
                case 52: goto L_0x0ee5;
                case 53: goto L_0x0ec0;
                case 54: goto L_0x0e9b;
                case 55: goto L_0x0e6f;
                case 56: goto L_0x0e47;
                case 57: goto L_0x0e1b;
                case 58: goto L_0x0dff;
                case 59: goto L_0x0de3;
                case 60: goto L_0x0dc7;
                case 61: goto L_0x0da4;
                case 62: goto L_0x0d88;
                case 63: goto L_0x0d6c;
                case 64: goto L_0x0d50;
                case 65: goto L_0x0d34;
                case 66: goto L_0x0d18;
                case 67: goto L_0x0cfc;
                case 68: goto L_0x0ccc;
                case 69: goto L_0x0c9d;
                case 70: goto L_0x0c6c;
                case 71: goto L_0x0c4a;
                case 72: goto L_0x0c0f;
                case 73: goto L_0x0bdc;
                case 74: goto L_0x0bad;
                case 75: goto L_0x0b7d;
                case 76: goto L_0x0b4c;
                case 77: goto L_0x0b1b;
                case 78: goto L_0x0a9b;
                case 79: goto L_0x0a6a;
                case 80: goto L_0x0a2d;
                case 81: goto L_0x09f6;
                case 82: goto L_0x09c3;
                case 83: goto L_0x0996;
                case 84: goto L_0x0969;
                case 85: goto L_0x093a;
                case 86: goto L_0x090b;
                case 87: goto L_0x08db;
                case 88: goto L_0x08d3;
                case 89: goto L_0x08d3;
                case 90: goto L_0x08d3;
                case 91: goto L_0x08b4;
                case 92: goto L_0x08d3;
                case 93: goto L_0x08d3;
                case 94: goto L_0x08d3;
                case 95: goto L_0x08d3;
                case 96: goto L_0x08d3;
                default: goto L_0x08aa;
            };
     */
    /* JADX WARNING: Missing block: B:567:0x08aa, code skipped:
            r17 = r10;
            r8 = r16;
            r15 = r23;
     */
    /* JADX WARNING: Missing block: B:571:?, code skipped:
            r4 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
            r17 = r10;
            r8 = r16;
            r15 = r23;
            r21 = null;
            r23 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
            r2 = true;
     */
    /* JADX WARNING: Missing block: B:572:0x08d3, code skipped:
            r17 = r10;
            r8 = r16;
            r15 = r23;
     */
    /* JADX WARNING: Missing block: B:573:0x08db, code skipped:
            if (r10 == 0) goto L_0x08f4;
     */
    /* JADX WARNING: Missing block: B:574:0x08dd, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:575:0x08f4, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r11[0]);
     */
    /* JADX WARNING: Missing block: B:576:0x0905, code skipped:
            r17 = r10;
            r8 = r16;
     */
    /* JADX WARNING: Missing block: B:577:0x090b, code skipped:
            r8 = r16;
     */
    /* JADX WARNING: Missing block: B:578:0x090d, code skipped:
            if (r10 == 0) goto L_0x0927;
     */
    /* JADX WARNING: Missing block: B:579:0x090f, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:580:0x0927, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r11[0]);
     */
    /* JADX WARNING: Missing block: B:581:0x093a, code skipped:
            r8 = r16;
     */
    /* JADX WARNING: Missing block: B:582:0x093c, code skipped:
            if (r10 == 0) goto L_0x0956;
     */
    /* JADX WARNING: Missing block: B:583:0x093e, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:584:0x0956, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r11[0]);
     */
    /* JADX WARNING: Missing block: B:585:0x0969, code skipped:
            r8 = r16;
     */
    /* JADX WARNING: Missing block: B:586:0x096b, code skipped:
            if (r10 == 0) goto L_0x0984;
     */
    /* JADX WARNING: Missing block: B:587:0x096d, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:588:0x0984, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r11[0]);
     */
    /* JADX WARNING: Missing block: B:589:0x0996, code skipped:
            r8 = r16;
     */
    /* JADX WARNING: Missing block: B:590:0x0998, code skipped:
            if (r10 == 0) goto L_0x09b1;
     */
    /* JADX WARNING: Missing block: B:591:0x099a, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:592:0x09b1, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r11[0]);
     */
    /* JADX WARNING: Missing block: B:593:0x09c3, code skipped:
            r8 = r16;
     */
    /* JADX WARNING: Missing block: B:594:0x09c5, code skipped:
            if (r10 == 0) goto L_0x09de;
     */
    /* JADX WARNING: Missing block: B:595:0x09c7, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:596:0x09de, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r11[0]);
     */
    /* JADX WARNING: Missing block: B:597:0x09ef, code skipped:
            r17 = r10;
     */
    /* JADX WARNING: Missing block: B:598:0x09f1, code skipped:
            r15 = r23;
            r2 = false;
     */
    /* JADX WARNING: Missing block: B:599:0x09f6, code skipped:
            r8 = r16;
     */
    /* JADX WARNING: Missing block: B:600:0x09f8, code skipped:
            if (r10 == 0) goto L_0x0a16;
     */
    /* JADX WARNING: Missing block: B:601:0x09fa, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r11[0], r11[1], r11[2]);
     */
    /* JADX WARNING: Missing block: B:602:0x0a16, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:603:0x0a2d, code skipped:
            r8 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:604:0x0a31, code skipped:
            if (r10 == 0) goto L_0x0a52;
     */
    /* JADX WARNING: Missing block: B:605:0x0a33, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r11[0], r11[1], r11[2]);
     */
    /* JADX WARNING: Missing block: B:606:0x0a52, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:607:0x0a6a, code skipped:
            r8 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:608:0x0a6e, code skipped:
            if (r10 == 0) goto L_0x0a88;
     */
    /* JADX WARNING: Missing block: B:609:0x0a70, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:610:0x0a88, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r11[0]);
     */
    /* JADX WARNING: Missing block: B:611:0x0a9b, code skipped:
            r8 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:612:0x0a9f, code skipped:
            if (r10 == 0) goto L_0x0ae4;
     */
    /* JADX WARNING: Missing block: B:614:0x0aa3, code skipped:
            if (r11.length <= 2) goto L_0x0acc;
     */
    /* JADX WARNING: Missing block: B:616:0x0aab, code skipped:
            if (android.text.TextUtils.isEmpty(r11[2]) != false) goto L_0x0acc;
     */
    /* JADX WARNING: Missing block: B:617:0x0aad, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r11[0], r11[1], r11[2]);
     */
    /* JADX WARNING: Missing block: B:618:0x0acc, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:620:0x0ae6, code skipped:
            if (r11.length <= 1) goto L_0x0b08;
     */
    /* JADX WARNING: Missing block: B:622:0x0aee, code skipped:
            if (android.text.TextUtils.isEmpty(r11[1]) != false) goto L_0x0b08;
     */
    /* JADX WARNING: Missing block: B:623:0x0af0, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:624:0x0b08, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r11[0]);
     */
    /* JADX WARNING: Missing block: B:625:0x0b1b, code skipped:
            r8 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:626:0x0b1f, code skipped:
            if (r10 == 0) goto L_0x0b39;
     */
    /* JADX WARNING: Missing block: B:627:0x0b21, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:628:0x0b39, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r11[0]);
     */
    /* JADX WARNING: Missing block: B:629:0x0b4c, code skipped:
            r8 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:630:0x0b50, code skipped:
            if (r10 == 0) goto L_0x0b6a;
     */
    /* JADX WARNING: Missing block: B:631:0x0b52, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:632:0x0b6a, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r11[0]);
     */
    /* JADX WARNING: Missing block: B:633:0x0b7d, code skipped:
            r8 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:634:0x0b81, code skipped:
            if (r10 == 0) goto L_0x0b9b;
     */
    /* JADX WARNING: Missing block: B:635:0x0b83, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:636:0x0b9b, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r11[0]);
     */
    /* JADX WARNING: Missing block: B:637:0x0bad, code skipped:
            r8 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:638:0x0bb1, code skipped:
            if (r10 == 0) goto L_0x0bca;
     */
    /* JADX WARNING: Missing block: B:639:0x0bb3, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:640:0x0bca, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r11[0]);
     */
    /* JADX WARNING: Missing block: B:641:0x0bdc, code skipped:
            r8 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:642:0x0be0, code skipped:
            if (r10 == 0) goto L_0x0bf9;
     */
    /* JADX WARNING: Missing block: B:643:0x0be2, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:644:0x0bf9, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r11[0]);
     */
    /* JADX WARNING: Missing block: B:645:0x0c0a, code skipped:
            r15 = r2;
     */
    /* JADX WARNING: Missing block: B:646:0x0c0b, code skipped:
            r17 = r10;
     */
    /* JADX WARNING: Missing block: B:647:0x0c0f, code skipped:
            r8 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:648:0x0CLASSNAME, code skipped:
            if (r10 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Missing block: B:649:0x0CLASSNAME, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r11[0], r11[1], r11[2]);
     */
    /* JADX WARNING: Missing block: B:650:0x0CLASSNAME, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:651:0x0c4a, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:652:0x0CLASSNAME, code skipped:
            r15 = r2;
     */
    /* JADX WARNING: Missing block: B:653:0x0CLASSNAME, code skipped:
            r17 = r10;
            r23 = r20;
            r2 = true;
     */
    /* JADX WARNING: Missing block: B:654:0x0c6c, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r11[0], r11[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r11[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:655:0x0c9d, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r11[0], r11[1], org.telegram.messenger.LocaleController.formatPluralString(r15, org.telegram.messenger.Utilities.parseInt(r11[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:656:0x0ccc, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r11[0], r11[1], org.telegram.messenger.LocaleController.formatPluralString(r4, org.telegram.messenger.Utilities.parseInt(r11[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:657:0x0cfc, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:658:0x0d18, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:659:0x0d34, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:660:0x0d50, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:661:0x0d6c, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:662:0x0d88, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:663:0x0da4, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r11[0], r11[1], r11[2]);
     */
    /* JADX WARNING: Missing block: B:664:0x0dc7, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:665:0x0de3, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:666:0x0dff, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Missing block: B:667:0x0e1b, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r11[0], r11[1], r11[2]);
            r3 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Missing block: B:668:0x0e47, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r11[0], r11[1], r11[2], r11[3]);
     */
    /* JADX WARNING: Missing block: B:669:0x0e6f, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r11[0], r11[1], r11[2]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:670:0x0e9b, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r11[0], r11[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:671:0x0ec0, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r11[0], r11[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:672:0x0ee5, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r11[0], r11[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:673:0x0f0a, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r11[0], r11[1], r11[2]);
            r3 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:674:0x0var_, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r11[0], r11[1], r11[2]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:675:0x0var_, code skipped:
            r8 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r11[0], r11[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:676:0x0var_, code skipped:
            r15 = r2;
            r21 = r3;
     */
    /* JADX WARNING: Missing block: B:677:0x0var_, code skipped:
            r8 = r16;
            r4 = r23;
     */
    /* JADX WARNING: Missing block: B:678:0x0f8e, code skipped:
            if (r11.length <= 2) goto L_0x0fd5;
     */
    /* JADX WARNING: Missing block: B:680:0x0var_, code skipped:
            if (android.text.TextUtils.isEmpty(r11[2]) != false) goto L_0x0fd5;
     */
    /* JADX WARNING: Missing block: B:681:0x0var_, code skipped:
            r23 = r4;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r11[0], r11[1], r11[2]);
            r14 = new java.lang.StringBuilder();
            r14.append(r11[2]);
            r14.append(r3);
            r14.append(org.telegram.messenger.LocaleController.getString(r2, NUM));
            r2 = r14.toString();
     */
    /* JADX WARNING: Missing block: B:682:0x0fd5, code skipped:
            r23 = r4;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r11[0], r11[1]);
            r14 = new java.lang.StringBuilder();
            r14.append(r11[1]);
            r14.append(r3);
            r14.append(org.telegram.messenger.LocaleController.getString(r2, NUM));
            r2 = r14.toString();
     */
    /* JADX WARNING: Missing block: B:683:0x100c, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r11[0], r11[1]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:684:0x102f, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r11[0], r11[1]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:685:0x1052, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r11[0], r11[1]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:686:0x1075, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r11[0], r11[1]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:687:0x1098, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r11[0], r11[1]);
            r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:688:0x10bb, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r11[0], r11[1], r11[2]);
            r2 = r11[2];
     */
    /* JADX WARNING: Missing block: B:689:0x10dc, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r11[0]);
     */
    /* JADX WARNING: Missing block: B:690:0x10f1, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString(r14, NUM, r11[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r11[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:691:0x1117, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString(r14, NUM, r11[0], org.telegram.messenger.LocaleController.formatPluralString(r15, org.telegram.messenger.Utilities.parseInt(r11[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:692:0x113b, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString(r14, NUM, r11[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r11[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Missing block: B:693:0x1165, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r11[0], r11[1], r11[2]);
     */
    /* JADX WARNING: Missing block: B:694:0x1184, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:695:0x11a2, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:696:0x11c0, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:697:0x11de, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:698:0x11fc, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r11[0], r11[1]);
            r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:699:0x121f, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r11[0], r11[1]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:700:0x1242, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:701:0x1260, code skipped:
            r8 = r16;
     */
    /* JADX WARNING: Missing block: B:702:0x1264, code skipped:
            if (r11.length <= 1) goto L_0x12a3;
     */
    /* JADX WARNING: Missing block: B:704:0x126c, code skipped:
            if (android.text.TextUtils.isEmpty(r11[1]) != false) goto L_0x12a3;
     */
    /* JADX WARNING: Missing block: B:705:0x126e, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r11[0], r11[1]);
            r14 = new java.lang.StringBuilder();
            r14.append(r11[1]);
            r14.append(r3);
            r14.append(org.telegram.messenger.LocaleController.getString(r2, NUM));
            r2 = r14.toString();
     */
    /* JADX WARNING: Missing block: B:706:0x12a3, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString(r2, NUM);
     */
    /* JADX WARNING: Missing block: B:707:0x12bd, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:708:0x12db, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:709:0x12f8, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:710:0x1315, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:711:0x1332, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:712:0x134e, code skipped:
            r21 = r2;
            r17 = r10;
            r15 = r23;
            r2 = false;
     */
    /* JADX WARNING: Missing block: B:713:0x1356, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r11[0], r11[1]);
            r2 = r11[1];
     */
    /* JADX WARNING: Missing block: B:714:0x1371, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r11[0]);
     */
    /* JADX WARNING: Missing block: B:715:0x1384, code skipped:
            r17 = r10;
            r15 = r23;
            r2 = true;
     */
    /* JADX WARNING: Missing block: B:716:0x1389, code skipped:
            r21 = null;
     */
    /* JADX WARNING: Missing block: B:717:0x138b, code skipped:
            r23 = r20;
     */
    /* JADX WARNING: Missing block: B:718:0x138f, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r11[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r11[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:719:0x13b6, code skipped:
            r8 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r11[0], org.telegram.messenger.LocaleController.formatPluralString(r15, org.telegram.messenger.Utilities.parseInt(r11[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:720:0x13db, code skipped:
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r11[0], org.telegram.messenger.LocaleController.formatPluralString(r4, org.telegram.messenger.Utilities.parseInt(r11[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:721:0x1405, code skipped:
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r11[0], r11[1]);
            r2 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Missing block: B:722:0x142a, code skipped:
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r11[0], r11[1], r11[2]);
     */
    /* JADX WARNING: Missing block: B:723:0x144d, code skipped:
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r11[0], r11[1]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:724:0x1472, code skipped:
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:725:0x1492, code skipped:
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:726:0x14b2, code skipped:
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:727:0x14d1, code skipped:
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r11[0], r11[1]);
            r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:728:0x14f5, code skipped:
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r11[0], r11[1]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:729:0x1519, code skipped:
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:730:0x1537, code skipped:
            r21 = r2;
     */
    /* JADX WARNING: Missing block: B:731:0x1539, code skipped:
            r17 = r10;
     */
    /* JADX WARNING: Missing block: B:732:0x153d, code skipped:
            r8 = r16;
            r15 = r23;
     */
    /* JADX WARNING: Missing block: B:733:0x1543, code skipped:
            if (r11.length <= 1) goto L_0x1584;
     */
    /* JADX WARNING: Missing block: B:735:0x154b, code skipped:
            if (android.text.TextUtils.isEmpty(r11[1]) != false) goto L_0x1584;
     */
    /* JADX WARNING: Missing block: B:736:0x154d, code skipped:
            r17 = r10;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r11[0], r11[1]);
            r10 = new java.lang.StringBuilder();
            r10.append(r11[1]);
            r10.append(r3);
            r10.append(org.telegram.messenger.LocaleController.getString(r2, NUM));
            r2 = r10.toString();
     */
    /* JADX WARNING: Missing block: B:737:0x1584, code skipped:
            r17 = r10;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString(r2, NUM);
     */
    /* JADX WARNING: Missing block: B:738:0x15a0, code skipped:
            r17 = r10;
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:739:0x15c2, code skipped:
            r17 = r10;
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:740:0x15e4, code skipped:
            r17 = r10;
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r11[0]);
     */
    /* JADX WARNING: Missing block: B:741:0x15fc, code skipped:
            r23 = r20;
            r2 = false;
     */
    /* JADX WARNING: Missing block: B:742:0x1601, code skipped:
            r17 = r10;
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:743:0x1623, code skipped:
            r17 = r10;
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:744:0x1644, code skipped:
            r17 = r10;
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:745:0x1665, code skipped:
            r17 = r10;
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:746:0x1686, code skipped:
            r17 = r10;
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r11[0]);
            r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:747:0x16a6, code skipped:
            r21 = r2;
     */
    /* JADX WARNING: Missing block: B:748:0x16a8, code skipped:
            r23 = r20;
            r2 = false;
     */
    /* JADX WARNING: Missing block: B:749:0x16ac, code skipped:
            r17 = r10;
            r8 = r16;
            r15 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r11[0], r11[1]);
            r2 = r11[1];
     */
    /* JADX WARNING: Missing block: B:750:0x16cb, code skipped:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x16e1;
     */
    /* JADX WARNING: Missing block: B:751:0x16cd, code skipped:
            r2 = new java.lang.StringBuilder();
            r2.append("unhandled loc_key = ");
            r2.append(r9);
            org.telegram.messenger.FileLog.w(r2.toString());
     */
    /* JADX WARNING: Missing block: B:752:0x16e1, code skipped:
            r23 = r20;
            r2 = false;
            r4 = null;
     */
    /* JADX WARNING: Missing block: B:753:0x16e5, code skipped:
            r21 = null;
     */
    /* JADX WARNING: Missing block: B:754:0x16e7, code skipped:
            if (r4 == null) goto L_0x1777;
     */
    /* JADX WARNING: Missing block: B:756:?, code skipped:
            r3 = new org.telegram.tgnet.TLRPC.TL_message();
            r3.id = r8;
            r3.random_id = r6;
     */
    /* JADX WARNING: Missing block: B:757:0x16f2, code skipped:
            if (r21 == null) goto L_0x16f7;
     */
    /* JADX WARNING: Missing block: B:758:0x16f4, code skipped:
            r6 = r21;
     */
    /* JADX WARNING: Missing block: B:759:0x16f7, code skipped:
            r6 = r4;
     */
    /* JADX WARNING: Missing block: B:760:0x16f8, code skipped:
            r3.message = r6;
            r3.date = (int) (r33 / 1000);
     */
    /* JADX WARNING: Missing block: B:761:0x1701, code skipped:
            if (r5 == null) goto L_0x170a;
     */
    /* JADX WARNING: Missing block: B:763:?, code skipped:
            r3.action = new org.telegram.tgnet.TLRPC.TL_messageActionPinMessage();
     */
    /* JADX WARNING: Missing block: B:764:0x170a, code skipped:
            if (r1 == null) goto L_0x1713;
     */
    /* JADX WARNING: Missing block: B:765:0x170c, code skipped:
            r3.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Missing block: B:767:?, code skipped:
            r3.dialog_id = r12;
     */
    /* JADX WARNING: Missing block: B:768:0x1715, code skipped:
            if (r30 == 0) goto L_0x1725;
     */
    /* JADX WARNING: Missing block: B:770:?, code skipped:
            r3.to_id = new org.telegram.tgnet.TLRPC.TL_peerChannel();
            r3.to_id.channel_id = r30;
     */
    /* JADX WARNING: Missing block: B:771:0x1725, code skipped:
            if (r15 == 0) goto L_0x1733;
     */
    /* JADX WARNING: Missing block: B:772:0x1727, code skipped:
            r3.to_id = new org.telegram.tgnet.TLRPC.TL_peerChat();
            r3.to_id.chat_id = r15;
     */
    /* JADX WARNING: Missing block: B:774:?, code skipped:
            r3.to_id = new org.telegram.tgnet.TLRPC.TL_peerUser();
            r3.to_id.user_id = r29;
     */
    /* JADX WARNING: Missing block: B:775:0x1740, code skipped:
            r3.from_id = r17;
     */
    /* JADX WARNING: Missing block: B:776:0x1744, code skipped:
            if (r25 != null) goto L_0x174b;
     */
    /* JADX WARNING: Missing block: B:777:0x1746, code skipped:
            if (r5 == null) goto L_0x1749;
     */
    /* JADX WARNING: Missing block: B:779:0x1749, code skipped:
            r1 = false;
     */
    /* JADX WARNING: Missing block: B:780:0x174b, code skipped:
            r1 = true;
     */
    /* JADX WARNING: Missing block: B:781:0x174c, code skipped:
            r3.mentioned = r1;
            r3.silent = r19;
            r19 = new org.telegram.messenger.MessageObject(r28, r3, r4, r23, r24, r2, r26);
            r2 = new java.util.ArrayList();
            r2.add(r19);
     */
    /* JADX WARNING: Missing block: B:782:0x176d, code skipped:
            r3 = r31;
     */
    /* JADX WARNING: Missing block: B:784:?, code skipped:
            org.telegram.messenger.NotificationsController.getInstance(r28).processNewMessages(r2, true, true, r3.countDownLatch);
     */
    /* JADX WARNING: Missing block: B:785:0x1777, code skipped:
            r31.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:786:0x1780, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:787:0x1781, code skipped:
            r3 = r31;
     */
    /* JADX WARNING: Missing block: B:809:0x1825, code skipped:
            r0 = th;
     */
    public /* synthetic */ void lambda$null$3$GcmPushListenerService(java.util.Map r32, long r33) {
        /*
        r31 = this;
        r1 = r31;
        r2 = r32;
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x000d;
    L_0x0008:
        r3 = "GCM START PROCESSING";
        org.telegram.messenger.FileLog.d(r3);
    L_0x000d:
        r5 = "p";
        r5 = r2.get(r5);	 Catch:{ Throwable -> 0x18ed }
        r6 = r5 instanceof java.lang.String;	 Catch:{ Throwable -> 0x18ed }
        if (r6 != 0) goto L_0x002c;
    L_0x0017:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x0024 }
        if (r2 == 0) goto L_0x0020;
    L_0x001b:
        r2 = "GCM DECRYPT ERROR 1";
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Throwable -> 0x0024 }
    L_0x0020:
        r31.onDecryptError();	 Catch:{ Throwable -> 0x0024 }
        return;
    L_0x0024:
        r0 = move-exception;
        r2 = r0;
        r3 = r1;
        r1 = -1;
        r7 = 0;
    L_0x0029:
        r9 = 0;
        goto L_0x18f4;
    L_0x002c:
        r5 = (java.lang.String) r5;	 Catch:{ Throwable -> 0x18ed }
        r6 = 8;
        r5 = android.util.Base64.decode(r5, r6);	 Catch:{ Throwable -> 0x18ed }
        r7 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Throwable -> 0x18ed }
        r8 = r5.length;	 Catch:{ Throwable -> 0x18ed }
        r7.<init>(r8);	 Catch:{ Throwable -> 0x18ed }
        r7.writeBytes(r5);	 Catch:{ Throwable -> 0x18ed }
        r8 = 0;
        r7.position(r8);	 Catch:{ Throwable -> 0x18ed }
        r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ Throwable -> 0x18ed }
        if (r9 != 0) goto L_0x0056;
    L_0x0045:
        r9 = new byte[r6];	 Catch:{ Throwable -> 0x0024 }
        org.telegram.messenger.SharedConfig.pushAuthKeyId = r9;	 Catch:{ Throwable -> 0x0024 }
        r9 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ Throwable -> 0x0024 }
        r9 = org.telegram.messenger.Utilities.computeSHA1(r9);	 Catch:{ Throwable -> 0x0024 }
        r10 = r9.length;	 Catch:{ Throwable -> 0x0024 }
        r10 = r10 - r6;
        r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ Throwable -> 0x0024 }
        java.lang.System.arraycopy(r9, r10, r11, r8, r6);	 Catch:{ Throwable -> 0x0024 }
    L_0x0056:
        r9 = new byte[r6];	 Catch:{ Throwable -> 0x18ed }
        r10 = 1;
        r7.readBytes(r9, r10);	 Catch:{ Throwable -> 0x18ed }
        r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ Throwable -> 0x18ed }
        r11 = java.util.Arrays.equals(r11, r9);	 Catch:{ Throwable -> 0x18ed }
        r12 = 3;
        r13 = 2;
        if (r11 != 0) goto L_0x0091;
    L_0x0066:
        r31.onDecryptError();	 Catch:{ Throwable -> 0x0024 }
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x0024 }
        if (r2 == 0) goto L_0x0090;
    L_0x006d:
        r2 = java.util.Locale.US;	 Catch:{ Throwable -> 0x0024 }
        r5 = "GCM DECRYPT ERROR 2 k1=%s k2=%s, key=%s";
        r6 = new java.lang.Object[r12];	 Catch:{ Throwable -> 0x0024 }
        r7 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ Throwable -> 0x0024 }
        r7 = org.telegram.messenger.Utilities.bytesToHex(r7);	 Catch:{ Throwable -> 0x0024 }
        r6[r8] = r7;	 Catch:{ Throwable -> 0x0024 }
        r7 = org.telegram.messenger.Utilities.bytesToHex(r9);	 Catch:{ Throwable -> 0x0024 }
        r6[r10] = r7;	 Catch:{ Throwable -> 0x0024 }
        r7 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ Throwable -> 0x0024 }
        r7 = org.telegram.messenger.Utilities.bytesToHex(r7);	 Catch:{ Throwable -> 0x0024 }
        r6[r13] = r7;	 Catch:{ Throwable -> 0x0024 }
        r2 = java.lang.String.format(r2, r5, r6);	 Catch:{ Throwable -> 0x0024 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Throwable -> 0x0024 }
    L_0x0090:
        return;
    L_0x0091:
        r9 = 16;
        r9 = new byte[r9];	 Catch:{ Throwable -> 0x18ed }
        r7.readBytes(r9, r10);	 Catch:{ Throwable -> 0x18ed }
        r11 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ Throwable -> 0x18ed }
        r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13);	 Catch:{ Throwable -> 0x18ed }
        r14 = r7.buffer;	 Catch:{ Throwable -> 0x18ed }
        r15 = r11.aesKey;	 Catch:{ Throwable -> 0x18ed }
        r11 = r11.aesIv;	 Catch:{ Throwable -> 0x18ed }
        r17 = 0;
        r18 = 0;
        r19 = 24;
        r5 = r5.length;	 Catch:{ Throwable -> 0x18ed }
        r20 = r5 + -24;
        r16 = r11;
        org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20);	 Catch:{ Throwable -> 0x18ed }
        r21 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ Throwable -> 0x18ed }
        r22 = 96;
        r23 = 32;
        r5 = r7.buffer;	 Catch:{ Throwable -> 0x18ed }
        r25 = 24;
        r11 = r7.buffer;	 Catch:{ Throwable -> 0x18ed }
        r26 = r11.limit();	 Catch:{ Throwable -> 0x18ed }
        r24 = r5;
        r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26);	 Catch:{ Throwable -> 0x18ed }
        r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6);	 Catch:{ Throwable -> 0x18ed }
        if (r5 != 0) goto L_0x00e9;
    L_0x00ce:
        r31.onDecryptError();	 Catch:{ Throwable -> 0x0024 }
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x0024 }
        if (r2 == 0) goto L_0x00e8;
    L_0x00d5:
        r2 = "GCM DECRYPT ERROR 3, key = %s";
        r5 = new java.lang.Object[r10];	 Catch:{ Throwable -> 0x0024 }
        r6 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ Throwable -> 0x0024 }
        r6 = org.telegram.messenger.Utilities.bytesToHex(r6);	 Catch:{ Throwable -> 0x0024 }
        r5[r8] = r6;	 Catch:{ Throwable -> 0x0024 }
        r2 = java.lang.String.format(r2, r5);	 Catch:{ Throwable -> 0x0024 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Throwable -> 0x0024 }
    L_0x00e8:
        return;
    L_0x00e9:
        r5 = r7.readInt32(r10);	 Catch:{ Throwable -> 0x18ed }
        r5 = new byte[r5];	 Catch:{ Throwable -> 0x18ed }
        r7.readBytes(r5, r10);	 Catch:{ Throwable -> 0x18ed }
        r7 = new java.lang.String;	 Catch:{ Throwable -> 0x18ed }
        r7.<init>(r5);	 Catch:{ Throwable -> 0x18ed }
        r5 = new org.json.JSONObject;	 Catch:{ Throwable -> 0x18e5 }
        r5.<init>(r7);	 Catch:{ Throwable -> 0x18e5 }
        r9 = "loc_key";
        r9 = r5.has(r9);	 Catch:{ Throwable -> 0x18e5 }
        if (r9 == 0) goto L_0x0111;
    L_0x0104:
        r9 = "loc_key";
        r9 = r5.getString(r9);	 Catch:{ Throwable -> 0x010b }
        goto L_0x0113;
    L_0x010b:
        r0 = move-exception;
        r2 = r0;
        r3 = r1;
        r1 = -1;
        goto L_0x0029;
    L_0x0111:
        r9 = "";
    L_0x0113:
        r11 = "custom";
        r11 = r5.get(r11);	 Catch:{ Throwable -> 0x18df }
        r11 = r11 instanceof org.json.JSONObject;	 Catch:{ Throwable -> 0x18df }
        if (r11 == 0) goto L_0x0129;
    L_0x011d:
        r11 = "custom";
        r11 = r5.getJSONObject(r11);	 Catch:{ Throwable -> 0x0124 }
        goto L_0x012e;
    L_0x0124:
        r0 = move-exception;
        r2 = r0;
        r3 = r1;
        goto L_0x18f3;
    L_0x0129:
        r11 = new org.json.JSONObject;	 Catch:{ Throwable -> 0x18df }
        r11.<init>();	 Catch:{ Throwable -> 0x18df }
    L_0x012e:
        r14 = "user_id";
        r14 = r5.has(r14);	 Catch:{ Throwable -> 0x18df }
        if (r14 == 0) goto L_0x013d;
    L_0x0136:
        r14 = "user_id";
        r14 = r5.get(r14);	 Catch:{ Throwable -> 0x0124 }
        goto L_0x013e;
    L_0x013d:
        r14 = 0;
    L_0x013e:
        if (r14 != 0) goto L_0x014b;
    L_0x0140:
        r14 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ Throwable -> 0x0124 }
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);	 Catch:{ Throwable -> 0x0124 }
        r14 = r14.getClientUserId();	 Catch:{ Throwable -> 0x0124 }
        goto L_0x016f;
    L_0x014b:
        r15 = r14 instanceof java.lang.Integer;	 Catch:{ Throwable -> 0x18df }
        if (r15 == 0) goto L_0x0156;
    L_0x014f:
        r14 = (java.lang.Integer) r14;	 Catch:{ Throwable -> 0x0124 }
        r14 = r14.intValue();	 Catch:{ Throwable -> 0x0124 }
        goto L_0x016f;
    L_0x0156:
        r15 = r14 instanceof java.lang.String;	 Catch:{ Throwable -> 0x18df }
        if (r15 == 0) goto L_0x0165;
    L_0x015a:
        r14 = (java.lang.String) r14;	 Catch:{ Throwable -> 0x0124 }
        r14 = org.telegram.messenger.Utilities.parseInt(r14);	 Catch:{ Throwable -> 0x0124 }
        r14 = r14.intValue();	 Catch:{ Throwable -> 0x0124 }
        goto L_0x016f;
    L_0x0165:
        r14 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ Throwable -> 0x18df }
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);	 Catch:{ Throwable -> 0x18df }
        r14 = r14.getClientUserId();	 Catch:{ Throwable -> 0x18df }
    L_0x016f:
        r15 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ Throwable -> 0x18df }
        r6 = 0;
    L_0x0172:
        if (r6 >= r12) goto L_0x0183;
    L_0x0174:
        r17 = org.telegram.messenger.UserConfig.getInstance(r6);	 Catch:{ Throwable -> 0x0124 }
        r3 = r17.getClientUserId();	 Catch:{ Throwable -> 0x0124 }
        if (r3 != r14) goto L_0x0180;
    L_0x017e:
        r15 = r6;
        goto L_0x0183;
    L_0x0180:
        r6 = r6 + 1;
        goto L_0x0172;
    L_0x0183:
        r3 = org.telegram.messenger.UserConfig.getInstance(r15);	 Catch:{ Throwable -> 0x18d8 }
        r3 = r3.isClientActivated();	 Catch:{ Throwable -> 0x18d8 }
        if (r3 != 0) goto L_0x01a1;
    L_0x018d:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x019c }
        if (r2 == 0) goto L_0x0196;
    L_0x0191:
        r2 = "GCM ACCOUNT NOT ACTIVATED";
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Throwable -> 0x019c }
    L_0x0196:
        r2 = r1.countDownLatch;	 Catch:{ Throwable -> 0x019c }
        r2.countDown();	 Catch:{ Throwable -> 0x019c }
        return;
    L_0x019c:
        r0 = move-exception;
        r2 = r0;
        r3 = r1;
        goto L_0x18dd;
    L_0x01a1:
        r3 = "google.sent_time";
        r2.get(r3);	 Catch:{ Throwable -> 0x18d8 }
        r2 = r9.hashCode();	 Catch:{ Throwable -> 0x18d8 }
        r3 = -NUM; // 0xffffffff8af4e06f float:-2.3580768E-32 double:NaN;
        if (r2 == r3) goto L_0x01ce;
    L_0x01af:
        r3 = -NUM; // 0xffffffffCLASSNAMEvar_ float:-652872.56 double:NaN;
        if (r2 == r3) goto L_0x01c4;
    L_0x01b4:
        r3 = NUM; // 0x25bae29f float:3.241942E-16 double:3.127458774E-315;
        if (r2 == r3) goto L_0x01ba;
    L_0x01b9:
        goto L_0x01d8;
    L_0x01ba:
        r2 = "MESSAGE_ANNOUNCEMENT";
        r2 = r9.equals(r2);	 Catch:{ Throwable -> 0x019c }
        if (r2 == 0) goto L_0x01d8;
    L_0x01c2:
        r2 = 1;
        goto L_0x01d9;
    L_0x01c4:
        r2 = "DC_UPDATE";
        r2 = r9.equals(r2);	 Catch:{ Throwable -> 0x019c }
        if (r2 == 0) goto L_0x01d8;
    L_0x01cc:
        r2 = 0;
        goto L_0x01d9;
    L_0x01ce:
        r2 = "SESSION_REVOKE";
        r2 = r9.equals(r2);	 Catch:{ Throwable -> 0x18d8 }
        if (r2 == 0) goto L_0x01d8;
    L_0x01d6:
        r2 = 2;
        goto L_0x01d9;
    L_0x01d8:
        r2 = -1;
    L_0x01d9:
        if (r2 == 0) goto L_0x1896;
    L_0x01db:
        if (r2 == r10) goto L_0x184b;
    L_0x01dd:
        if (r2 == r13) goto L_0x1835;
    L_0x01df:
        r2 = "channel_id";
        r2 = r11.has(r2);	 Catch:{ Throwable -> 0x182d }
        r19 = 0;
        if (r2 == 0) goto L_0x01f2;
    L_0x01e9:
        r2 = "channel_id";
        r2 = r11.getInt(r2);	 Catch:{ Throwable -> 0x019c }
        r3 = -r2;
        r12 = (long) r3;
        goto L_0x01f5;
    L_0x01f2:
        r12 = r19;
        r2 = 0;
    L_0x01f5:
        r3 = "from_id";
        r3 = r11.has(r3);	 Catch:{ Throwable -> 0x182d }
        if (r3 == 0) goto L_0x0205;
    L_0x01fd:
        r3 = "from_id";
        r3 = r11.getInt(r3);	 Catch:{ Throwable -> 0x019c }
        r12 = (long) r3;
        goto L_0x0206;
    L_0x0205:
        r3 = 0;
    L_0x0206:
        r6 = "chat_id";
        r6 = r11.has(r6);	 Catch:{ Throwable -> 0x182d }
        if (r6 == 0) goto L_0x0217;
    L_0x020e:
        r6 = "chat_id";
        r6 = r11.getInt(r6);	 Catch:{ Throwable -> 0x019c }
        r12 = -r6;
        r12 = (long) r12;
        goto L_0x0218;
    L_0x0217:
        r6 = 0;
    L_0x0218:
        r14 = "encryption_id";
        r14 = r11.has(r14);	 Catch:{ Throwable -> 0x182d }
        if (r14 == 0) goto L_0x022a;
    L_0x0220:
        r12 = "encryption_id";
        r12 = r11.getInt(r12);	 Catch:{ Throwable -> 0x019c }
        r12 = (long) r12;	 Catch:{ Throwable -> 0x019c }
        r14 = 32;
        r12 = r12 << r14;
    L_0x022a:
        r14 = (r12 > r19 ? 1 : (r12 == r19 ? 0 : -1));
        if (r14 != 0) goto L_0x023b;
    L_0x022e:
        r14 = "ENCRYPTED_MESSAGE";
        r14 = r14.equals(r9);	 Catch:{ Throwable -> 0x019c }
        if (r14 == 0) goto L_0x023b;
    L_0x0236:
        r12 = -NUM; // 0xfffffffvar_ float:0.0 double:NaN;
    L_0x023b:
        r14 = (r12 > r19 ? 1 : (r12 == r19 ? 0 : -1));
        if (r14 == 0) goto L_0x1814;
    L_0x023f:
        r14 = "MESSAGE_DELETED";
        r14 = r14.equals(r9);	 Catch:{ Throwable -> 0x182d }
        r4 = "messages";
        if (r14 == 0) goto L_0x0292;
    L_0x0249:
        r3 = r11.getString(r4);	 Catch:{ Throwable -> 0x019c }
        r4 = ",";
        r3 = r3.split(r4);	 Catch:{ Throwable -> 0x019c }
        r4 = new android.util.SparseArray;	 Catch:{ Throwable -> 0x019c }
        r4.<init>();	 Catch:{ Throwable -> 0x019c }
        r5 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x019c }
        r5.<init>();	 Catch:{ Throwable -> 0x019c }
    L_0x025d:
        r6 = r3.length;	 Catch:{ Throwable -> 0x019c }
        if (r8 >= r6) goto L_0x026c;
    L_0x0260:
        r6 = r3[r8];	 Catch:{ Throwable -> 0x019c }
        r6 = org.telegram.messenger.Utilities.parseInt(r6);	 Catch:{ Throwable -> 0x019c }
        r5.add(r6);	 Catch:{ Throwable -> 0x019c }
        r8 = r8 + 1;
        goto L_0x025d;
    L_0x026c:
        r4.put(r2, r5);	 Catch:{ Throwable -> 0x019c }
        r3 = org.telegram.messenger.NotificationsController.getInstance(r15);	 Catch:{ Throwable -> 0x019c }
        r3.removeDeletedMessagesFromNotifications(r4);	 Catch:{ Throwable -> 0x019c }
        r3 = org.telegram.messenger.MessagesStorage.getInstance(r15);	 Catch:{ Throwable -> 0x019c }
        r3 = r3.getStorageQueue();	 Catch:{ Throwable -> 0x019c }
        r4 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$VBGYIeEArqxcLizZJlWbyOSQipg;	 Catch:{ Throwable -> 0x019c }
        r19 = r4;
        r20 = r15;
        r21 = r12;
        r23 = r5;
        r24 = r2;
        r19.<init>(r20, r21, r23, r24);	 Catch:{ Throwable -> 0x019c }
        r3.postRunnable(r4);	 Catch:{ Throwable -> 0x019c }
        goto L_0x1814;
    L_0x0292:
        r14 = android.text.TextUtils.isEmpty(r9);	 Catch:{ Throwable -> 0x182d }
        if (r14 != 0) goto L_0x17a0;
    L_0x0298:
        r14 = "msg_id";
        r14 = r11.has(r14);	 Catch:{ Throwable -> 0x182d }
        if (r14 == 0) goto L_0x02a7;
    L_0x02a0:
        r14 = "msg_id";
        r14 = r11.getInt(r14);	 Catch:{ Throwable -> 0x019c }
        goto L_0x02a8;
    L_0x02a7:
        r14 = 0;
    L_0x02a8:
        r10 = "random_id";
        r10 = r11.has(r10);	 Catch:{ Throwable -> 0x182d }
        if (r10 == 0) goto L_0x02c1;
    L_0x02b0:
        r10 = "random_id";
        r10 = r11.getString(r10);	 Catch:{ Throwable -> 0x019c }
        r10 = org.telegram.messenger.Utilities.parseLong(r10);	 Catch:{ Throwable -> 0x019c }
        r22 = r10.longValue();	 Catch:{ Throwable -> 0x019c }
        r27 = r22;
        goto L_0x02c3;
    L_0x02c1:
        r27 = r19;
    L_0x02c3:
        if (r14 == 0) goto L_0x0304;
    L_0x02c5:
        r10 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ Throwable -> 0x019c }
        r10 = r10.dialogs_read_inbox_max;	 Catch:{ Throwable -> 0x019c }
        r8 = java.lang.Long.valueOf(r12);	 Catch:{ Throwable -> 0x019c }
        r8 = r10.get(r8);	 Catch:{ Throwable -> 0x019c }
        r8 = (java.lang.Integer) r8;	 Catch:{ Throwable -> 0x019c }
        if (r8 != 0) goto L_0x02f4;
    L_0x02d7:
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r15);	 Catch:{ Throwable -> 0x019c }
        r10 = 0;
        r8 = r8.getDialogReadMax(r10, r12);	 Catch:{ Throwable -> 0x019c }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Throwable -> 0x019c }
        r10 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ Throwable -> 0x019c }
        r10 = r10.dialogs_read_inbox_max;	 Catch:{ Throwable -> 0x019c }
        r23 = r6;
        r6 = java.lang.Long.valueOf(r12);	 Catch:{ Throwable -> 0x019c }
        r10.put(r6, r8);	 Catch:{ Throwable -> 0x019c }
        goto L_0x02f6;
    L_0x02f4:
        r23 = r6;
    L_0x02f6:
        r6 = r8.intValue();	 Catch:{ Throwable -> 0x019c }
        if (r14 <= r6) goto L_0x02fe;
    L_0x02fc:
        r8 = 1;
        goto L_0x02ff;
    L_0x02fe:
        r8 = 0;
    L_0x02ff:
        r10 = r8;
        r8 = r7;
        r6 = r27;
        goto L_0x0320;
    L_0x0304:
        r23 = r6;
        r8 = r7;
        r6 = r27;
        r10 = (r6 > r19 ? 1 : (r6 == r19 ? 0 : -1));
        if (r10 == 0) goto L_0x031f;
    L_0x030d:
        r10 = org.telegram.messenger.MessagesStorage.getInstance(r15);	 Catch:{ Throwable -> 0x0319 }
        r10 = r10.checkMessageByRandomId(r6);	 Catch:{ Throwable -> 0x0319 }
        if (r10 != 0) goto L_0x031f;
    L_0x0317:
        r10 = 1;
        goto L_0x0320;
    L_0x0319:
        r0 = move-exception;
        r2 = r0;
        r3 = r1;
        r7 = r8;
        goto L_0x18dd;
    L_0x031f:
        r10 = 0;
    L_0x0320:
        if (r10 == 0) goto L_0x1794;
    L_0x0322:
        r10 = "chat_from_id";
        r10 = r11.has(r10);	 Catch:{ Throwable -> 0x178c }
        if (r10 == 0) goto L_0x0333;
    L_0x032a:
        r10 = "chat_from_id";
        r10 = r11.getInt(r10);	 Catch:{ Throwable -> 0x0319 }
        r27 = r8;
        goto L_0x0336;
    L_0x0333:
        r27 = r8;
        r10 = 0;
    L_0x0336:
        r8 = "mention";
        r8 = r11.has(r8);	 Catch:{ Throwable -> 0x1789 }
        if (r8 == 0) goto L_0x034d;
    L_0x033e:
        r8 = "mention";
        r8 = r11.getInt(r8);	 Catch:{ Throwable -> 0x0348 }
        if (r8 == 0) goto L_0x034d;
    L_0x0346:
        r8 = 1;
        goto L_0x034e;
    L_0x0348:
        r0 = move-exception;
        r2 = r0;
        r3 = r1;
        goto L_0x18d5;
    L_0x034d:
        r8 = 0;
    L_0x034e:
        r1 = "silent";
        r1 = r11.has(r1);	 Catch:{ Throwable -> 0x1785 }
        if (r1 == 0) goto L_0x0369;
    L_0x0356:
        r1 = "silent";
        r1 = r11.getInt(r1);	 Catch:{ Throwable -> 0x0360 }
        if (r1 == 0) goto L_0x0369;
    L_0x035e:
        r1 = 1;
        goto L_0x036a;
    L_0x0360:
        r0 = move-exception;
    L_0x0361:
        r1 = -1;
        r3 = r31;
        r2 = r0;
        r7 = r27;
        goto L_0x18f5;
    L_0x0369:
        r1 = 0;
    L_0x036a:
        r11 = "loc_args";
        r11 = r5.has(r11);	 Catch:{ Throwable -> 0x1785 }
        if (r11 == 0) goto L_0x03a0;
    L_0x0372:
        r11 = "loc_args";
        r5 = r5.getJSONArray(r11);	 Catch:{ Throwable -> 0x039c }
        r11 = r5.length();	 Catch:{ Throwable -> 0x039c }
        r11 = new java.lang.String[r11];	 Catch:{ Throwable -> 0x039c }
        r19 = r1;
        r28 = r15;
        r15 = 0;
    L_0x0383:
        r1 = r11.length;	 Catch:{ Throwable -> 0x0391 }
        if (r15 >= r1) goto L_0x038f;
    L_0x0386:
        r1 = r5.getString(r15);	 Catch:{ Throwable -> 0x0391 }
        r11[r15] = r1;	 Catch:{ Throwable -> 0x0391 }
        r15 = r15 + 1;
        goto L_0x0383;
    L_0x038f:
        r1 = 0;
        goto L_0x03a6;
    L_0x0391:
        r0 = move-exception;
        r1 = -1;
        r3 = r31;
        r2 = r0;
        r7 = r27;
        r15 = r28;
        goto L_0x18f5;
    L_0x039c:
        r0 = move-exception;
        r28 = r15;
        goto L_0x0361;
    L_0x03a0:
        r19 = r1;
        r28 = r15;
        r1 = 0;
        r11 = 0;
    L_0x03a6:
        r5 = r11[r1];	 Catch:{ Throwable -> 0x1780 }
        r1 = "CHAT_";
        r1 = r9.startsWith(r1);	 Catch:{ Throwable -> 0x1780 }
        if (r1 == 0) goto L_0x03be;
    L_0x03b0:
        if (r2 == 0) goto L_0x03b4;
    L_0x03b2:
        r1 = 1;
        goto L_0x03b5;
    L_0x03b4:
        r1 = 0;
    L_0x03b5:
        r15 = 1;
        r20 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r24 = r5;
        r5 = 0;
    L_0x03bb:
        r26 = 0;
        goto L_0x03e7;
    L_0x03be:
        r1 = "PINNED_";
        r1 = r9.startsWith(r1);	 Catch:{ Throwable -> 0x1780 }
        if (r1 == 0) goto L_0x03cf;
    L_0x03c6:
        if (r10 == 0) goto L_0x03ca;
    L_0x03c8:
        r1 = 1;
        goto L_0x03cb;
    L_0x03ca:
        r1 = 0;
    L_0x03cb:
        r20 = r5;
        r5 = 1;
        goto L_0x03e4;
    L_0x03cf:
        r1 = "CHANNEL_";
        r1 = r9.startsWith(r1);	 Catch:{ Throwable -> 0x1780 }
        if (r1 == 0) goto L_0x03e0;
    L_0x03d7:
        r20 = r5;
        r1 = 0;
        r5 = 0;
        r24 = 0;
        r26 = 1;
        goto L_0x03e7;
    L_0x03e0:
        r20 = r5;
        r1 = 0;
        r5 = 0;
    L_0x03e4:
        r24 = 0;
        goto L_0x03bb;
    L_0x03e7:
        r15 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x1780 }
        if (r15 == 0) goto L_0x0412;
    L_0x03eb:
        r15 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0391 }
        r15.<init>();	 Catch:{ Throwable -> 0x0391 }
        r25 = r8;
        r8 = "GCM received message notification ";
        r15.append(r8);	 Catch:{ Throwable -> 0x0391 }
        r15.append(r9);	 Catch:{ Throwable -> 0x0391 }
        r8 = " for dialogId = ";
        r15.append(r8);	 Catch:{ Throwable -> 0x0391 }
        r15.append(r12);	 Catch:{ Throwable -> 0x0391 }
        r8 = " mid = ";
        r15.append(r8);	 Catch:{ Throwable -> 0x0391 }
        r15.append(r14);	 Catch:{ Throwable -> 0x0391 }
        r8 = r15.toString();	 Catch:{ Throwable -> 0x0391 }
        org.telegram.messenger.FileLog.d(r8);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0414;
    L_0x0412:
        r25 = r8;
    L_0x0414:
        r8 = r9.hashCode();	 Catch:{ Throwable -> 0x1780 }
        switch(r8) {
            case -2100047043: goto L_0x088d;
            case -2091498420: goto L_0x0882;
            case -2053872415: goto L_0x0877;
            case -2039746363: goto L_0x086c;
            case -2023218804: goto L_0x0861;
            case -1979538588: goto L_0x0856;
            case -1979536003: goto L_0x084b;
            case -1979535888: goto L_0x0840;
            case -1969004705: goto L_0x0835;
            case -1946699248: goto L_0x0829;
            case -1528047021: goto L_0x081d;
            case -1493579426: goto L_0x0811;
            case -1480102982: goto L_0x0806;
            case -1478041834: goto L_0x07fb;
            case -1474543101: goto L_0x07f0;
            case -1465695932: goto L_0x07e4;
            case -1374906292: goto L_0x07d8;
            case -1372940586: goto L_0x07cc;
            case -1264245338: goto L_0x07c0;
            case -1236086700: goto L_0x07b4;
            case -1236077786: goto L_0x07a8;
            case -1235796237: goto L_0x079c;
            case -1235686303: goto L_0x0790;
            case -1198046100: goto L_0x0785;
            case -1124254527: goto L_0x0779;
            case -1085137927: goto L_0x076d;
            case -1084856378: goto L_0x0761;
            case -1084746444: goto L_0x0755;
            case -819729482: goto L_0x0749;
            case -772141857: goto L_0x073d;
            case -638310039: goto L_0x0731;
            case -590403924: goto L_0x0725;
            case -589196239: goto L_0x0719;
            case -589193654: goto L_0x070d;
            case -589193539: goto L_0x0701;
            case -440169325: goto L_0x06f5;
            case -412748110: goto L_0x06e9;
            case -228518075: goto L_0x06dd;
            case -213586509: goto L_0x06d1;
            case -115582002: goto L_0x06c5;
            case -112621464: goto L_0x06b9;
            case -108522133: goto L_0x06ad;
            case -107572034: goto L_0x06a2;
            case -40534265: goto L_0x0696;
            case 65254746: goto L_0x068a;
            case 141040782: goto L_0x067e;
            case 309993049: goto L_0x0672;
            case 309995634: goto L_0x0666;
            case 309995749: goto L_0x065a;
            case 320532812: goto L_0x064e;
            case 328933854: goto L_0x0642;
            case 331340546: goto L_0x0636;
            case 344816990: goto L_0x062a;
            case 346878138: goto L_0x061e;
            case 350376871: goto L_0x0612;
            case 615714517: goto L_0x0607;
            case 715508879: goto L_0x05fb;
            case 728985323: goto L_0x05ef;
            case 731046471: goto L_0x05e3;
            case 734545204: goto L_0x05d7;
            case 802032552: goto L_0x05cb;
            case 991498806: goto L_0x05bf;
            case 1007364121: goto L_0x05b3;
            case 1019917311: goto L_0x05a7;
            case 1019926225: goto L_0x059b;
            case 1020207774: goto L_0x058f;
            case 1020317708: goto L_0x0583;
            case 1060349560: goto L_0x0577;
            case 1060358474: goto L_0x056b;
            case 1060640023: goto L_0x055f;
            case 1060749957: goto L_0x0554;
            case 1073049781: goto L_0x0548;
            case 1078101399: goto L_0x053c;
            case 1110103437: goto L_0x0530;
            case 1160762272: goto L_0x0524;
            case 1172918249: goto L_0x0518;
            case 1234591620: goto L_0x050c;
            case 1281128640: goto L_0x0500;
            case 1281131225: goto L_0x04f4;
            case 1281131340: goto L_0x04e8;
            case 1310789062: goto L_0x04dd;
            case 1333118583: goto L_0x04d1;
            case 1361447897: goto L_0x04c5;
            case 1498266155: goto L_0x04b9;
            case 1533804208: goto L_0x04ad;
            case 1547988151: goto L_0x04a1;
            case 1561464595: goto L_0x0495;
            case 1563525743: goto L_0x0489;
            case 1567024476: goto L_0x047d;
            case 1810705077: goto L_0x0471;
            case 1815177512: goto L_0x0465;
            case 1963241394: goto L_0x0459;
            case 2014789757: goto L_0x044d;
            case 2022049433: goto L_0x0441;
            case 2048733346: goto L_0x0435;
            case 2099392181: goto L_0x0429;
            case 2140162142: goto L_0x041d;
            default: goto L_0x041b;
        };
    L_0x041b:
        goto L_0x0898;
    L_0x041d:
        r8 = "CHAT_MESSAGE_GEOLIVE";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0425:
        r8 = 53;
        goto L_0x0899;
    L_0x0429:
        r8 = "CHANNEL_MESSAGE_PHOTOS";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0431:
        r8 = 39;
        goto L_0x0899;
    L_0x0435:
        r8 = "CHANNEL_MESSAGE_NOTEXT";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x043d:
        r8 = 24;
        goto L_0x0899;
    L_0x0441:
        r8 = "PINNED_CONTACT";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0449:
        r8 = 80;
        goto L_0x0899;
    L_0x044d:
        r8 = "CHAT_PHOTO_EDITED";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0455:
        r8 = 60;
        goto L_0x0899;
    L_0x0459:
        r8 = "LOCKED_MESSAGE";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0461:
        r8 = 94;
        goto L_0x0899;
    L_0x0465:
        r8 = "CHANNEL_MESSAGES";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x046d:
        r8 = 41;
        goto L_0x0899;
    L_0x0471:
        r8 = "MESSAGE_INVOICE";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0479:
        r8 = 18;
        goto L_0x0899;
    L_0x047d:
        r8 = "CHAT_MESSAGE_VIDEO";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0485:
        r8 = 45;
        goto L_0x0899;
    L_0x0489:
        r8 = "CHAT_MESSAGE_ROUND";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0491:
        r8 = 46;
        goto L_0x0899;
    L_0x0495:
        r8 = "CHAT_MESSAGE_PHOTO";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x049d:
        r8 = 44;
        goto L_0x0899;
    L_0x04a1:
        r8 = "CHAT_MESSAGE_AUDIO";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x04a9:
        r8 = 49;
        goto L_0x0899;
    L_0x04ad:
        r8 = "MESSAGE_VIDEOS";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x04b5:
        r8 = 21;
        goto L_0x0899;
    L_0x04b9:
        r8 = "PHONE_CALL_MISSED";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x04c1:
        r8 = 96;
        goto L_0x0899;
    L_0x04c5:
        r8 = "MESSAGE_PHOTOS";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x04cd:
        r8 = 20;
        goto L_0x0899;
    L_0x04d1:
        r8 = "CHAT_MESSAGE_VIDEOS";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x04d9:
        r8 = 70;
        goto L_0x0899;
    L_0x04dd:
        r8 = "MESSAGE_NOTEXT";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x04e5:
        r8 = 1;
        goto L_0x0899;
    L_0x04e8:
        r8 = "MESSAGE_GIF";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x04f0:
        r8 = 15;
        goto L_0x0899;
    L_0x04f4:
        r8 = "MESSAGE_GEO";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x04fc:
        r8 = 13;
        goto L_0x0899;
    L_0x0500:
        r8 = "MESSAGE_DOC";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0508:
        r8 = 8;
        goto L_0x0899;
    L_0x050c:
        r8 = "CHAT_MESSAGE_GAME_SCORE";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0514:
        r8 = 56;
        goto L_0x0899;
    L_0x0518:
        r8 = "CHANNEL_MESSAGE_GEOLIVE";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0520:
        r8 = 34;
        goto L_0x0899;
    L_0x0524:
        r8 = "CHAT_MESSAGE_PHOTOS";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x052c:
        r8 = 69;
        goto L_0x0899;
    L_0x0530:
        r8 = "CHAT_MESSAGE_NOTEXT";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0538:
        r8 = 43;
        goto L_0x0899;
    L_0x053c:
        r8 = "CHAT_TITLE_EDITED";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0544:
        r8 = 59;
        goto L_0x0899;
    L_0x0548:
        r8 = "PINNED_NOTEXT";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0550:
        r8 = 73;
        goto L_0x0899;
    L_0x0554:
        r8 = "MESSAGE_TEXT";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x055c:
        r8 = 0;
        goto L_0x0899;
    L_0x055f:
        r8 = "MESSAGE_POLL";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0567:
        r8 = 12;
        goto L_0x0899;
    L_0x056b:
        r8 = "MESSAGE_GAME";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0573:
        r8 = 16;
        goto L_0x0899;
    L_0x0577:
        r8 = "MESSAGE_FWDS";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x057f:
        r8 = 19;
        goto L_0x0899;
    L_0x0583:
        r8 = "CHAT_MESSAGE_TEXT";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x058b:
        r8 = 42;
        goto L_0x0899;
    L_0x058f:
        r8 = "CHAT_MESSAGE_POLL";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0597:
        r8 = 51;
        goto L_0x0899;
    L_0x059b:
        r8 = "CHAT_MESSAGE_GAME";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x05a3:
        r8 = 55;
        goto L_0x0899;
    L_0x05a7:
        r8 = "CHAT_MESSAGE_FWDS";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x05af:
        r8 = 68;
        goto L_0x0899;
    L_0x05b3:
        r8 = "CHANNEL_MESSAGE_GAME_SCORE";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x05bb:
        r8 = 37;
        goto L_0x0899;
    L_0x05bf:
        r8 = "PINNED_GEOLIVE";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x05c7:
        r8 = 83;
        goto L_0x0899;
    L_0x05cb:
        r8 = "MESSAGE_CONTACT";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x05d3:
        r8 = 11;
        goto L_0x0899;
    L_0x05d7:
        r8 = "PINNED_VIDEO";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x05df:
        r8 = 75;
        goto L_0x0899;
    L_0x05e3:
        r8 = "PINNED_ROUND";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x05eb:
        r8 = 76;
        goto L_0x0899;
    L_0x05ef:
        r8 = "PINNED_PHOTO";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x05f7:
        r8 = 74;
        goto L_0x0899;
    L_0x05fb:
        r8 = "PINNED_AUDIO";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0603:
        r8 = 79;
        goto L_0x0899;
    L_0x0607:
        r8 = "MESSAGE_PHOTO_SECRET";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x060f:
        r8 = 3;
        goto L_0x0899;
    L_0x0612:
        r8 = "CHANNEL_MESSAGE_VIDEO";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x061a:
        r8 = 26;
        goto L_0x0899;
    L_0x061e:
        r8 = "CHANNEL_MESSAGE_ROUND";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0626:
        r8 = 27;
        goto L_0x0899;
    L_0x062a:
        r8 = "CHANNEL_MESSAGE_PHOTO";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0632:
        r8 = 25;
        goto L_0x0899;
    L_0x0636:
        r8 = "CHANNEL_MESSAGE_AUDIO";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x063e:
        r8 = 30;
        goto L_0x0899;
    L_0x0642:
        r8 = "CHAT_MESSAGE_STICKER";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x064a:
        r8 = 48;
        goto L_0x0899;
    L_0x064e:
        r8 = "MESSAGES";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0656:
        r8 = 22;
        goto L_0x0899;
    L_0x065a:
        r8 = "CHAT_MESSAGE_GIF";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0662:
        r8 = 54;
        goto L_0x0899;
    L_0x0666:
        r8 = "CHAT_MESSAGE_GEO";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x066e:
        r8 = 52;
        goto L_0x0899;
    L_0x0672:
        r8 = "CHAT_MESSAGE_DOC";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x067a:
        r8 = 47;
        goto L_0x0899;
    L_0x067e:
        r8 = "CHAT_LEFT";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0686:
        r8 = 65;
        goto L_0x0899;
    L_0x068a:
        r8 = "CHAT_ADD_YOU";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0692:
        r8 = 62;
        goto L_0x0899;
    L_0x0696:
        r8 = "CHAT_DELETE_MEMBER";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x069e:
        r8 = 63;
        goto L_0x0899;
    L_0x06a2:
        r8 = "MESSAGE_SCREENSHOT";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x06aa:
        r8 = 6;
        goto L_0x0899;
    L_0x06ad:
        r8 = "AUTH_REGION";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x06b5:
        r8 = 90;
        goto L_0x0899;
    L_0x06b9:
        r8 = "CONTACT_JOINED";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x06c1:
        r8 = 88;
        goto L_0x0899;
    L_0x06c5:
        r8 = "CHAT_MESSAGE_INVOICE";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x06cd:
        r8 = 57;
        goto L_0x0899;
    L_0x06d1:
        r8 = "ENCRYPTION_REQUEST";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x06d9:
        r8 = 92;
        goto L_0x0899;
    L_0x06dd:
        r8 = "MESSAGE_GEOLIVE";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x06e5:
        r8 = 14;
        goto L_0x0899;
    L_0x06e9:
        r8 = "CHAT_DELETE_YOU";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x06f1:
        r8 = 64;
        goto L_0x0899;
    L_0x06f5:
        r8 = "AUTH_UNKNOWN";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x06fd:
        r8 = 89;
        goto L_0x0899;
    L_0x0701:
        r8 = "PINNED_GIF";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0709:
        r8 = 87;
        goto L_0x0899;
    L_0x070d:
        r8 = "PINNED_GEO";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0715:
        r8 = 82;
        goto L_0x0899;
    L_0x0719:
        r8 = "PINNED_DOC";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0721:
        r8 = 77;
        goto L_0x0899;
    L_0x0725:
        r8 = "PINNED_GAME_SCORE";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x072d:
        r8 = 85;
        goto L_0x0899;
    L_0x0731:
        r8 = "CHANNEL_MESSAGE_STICKER";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0739:
        r8 = 29;
        goto L_0x0899;
    L_0x073d:
        r8 = "PHONE_CALL_REQUEST";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0745:
        r8 = 95;
        goto L_0x0899;
    L_0x0749:
        r8 = "PINNED_STICKER";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0751:
        r8 = 78;
        goto L_0x0899;
    L_0x0755:
        r8 = "PINNED_TEXT";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x075d:
        r8 = 72;
        goto L_0x0899;
    L_0x0761:
        r8 = "PINNED_POLL";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0769:
        r8 = 81;
        goto L_0x0899;
    L_0x076d:
        r8 = "PINNED_GAME";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0775:
        r8 = 84;
        goto L_0x0899;
    L_0x0779:
        r8 = "CHAT_MESSAGE_CONTACT";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0781:
        r8 = 50;
        goto L_0x0899;
    L_0x0785:
        r8 = "MESSAGE_VIDEO_SECRET";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x078d:
        r8 = 5;
        goto L_0x0899;
    L_0x0790:
        r8 = "CHANNEL_MESSAGE_TEXT";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0798:
        r8 = 23;
        goto L_0x0899;
    L_0x079c:
        r8 = "CHANNEL_MESSAGE_POLL";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x07a4:
        r8 = 32;
        goto L_0x0899;
    L_0x07a8:
        r8 = "CHANNEL_MESSAGE_GAME";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x07b0:
        r8 = 36;
        goto L_0x0899;
    L_0x07b4:
        r8 = "CHANNEL_MESSAGE_FWDS";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x07bc:
        r8 = 38;
        goto L_0x0899;
    L_0x07c0:
        r8 = "PINNED_INVOICE";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x07c8:
        r8 = 86;
        goto L_0x0899;
    L_0x07cc:
        r8 = "CHAT_RETURNED";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x07d4:
        r8 = 66;
        goto L_0x0899;
    L_0x07d8:
        r8 = "ENCRYPTED_MESSAGE";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x07e0:
        r8 = 91;
        goto L_0x0899;
    L_0x07e4:
        r8 = "ENCRYPTION_ACCEPT";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x07ec:
        r8 = 93;
        goto L_0x0899;
    L_0x07f0:
        r8 = "MESSAGE_VIDEO";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x07f8:
        r8 = 4;
        goto L_0x0899;
    L_0x07fb:
        r8 = "MESSAGE_ROUND";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0803:
        r8 = 7;
        goto L_0x0899;
    L_0x0806:
        r8 = "MESSAGE_PHOTO";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x080e:
        r8 = 2;
        goto L_0x0899;
    L_0x0811:
        r8 = "MESSAGE_AUDIO";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0819:
        r8 = 10;
        goto L_0x0899;
    L_0x081d:
        r8 = "CHAT_MESSAGES";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0825:
        r8 = 71;
        goto L_0x0899;
    L_0x0829:
        r8 = "CHAT_JOINED";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0831:
        r8 = 67;
        goto L_0x0899;
    L_0x0835:
        r8 = "CHAT_ADD_MEMBER";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x083d:
        r8 = 61;
        goto L_0x0899;
    L_0x0840:
        r8 = "CHANNEL_MESSAGE_GIF";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0848:
        r8 = 35;
        goto L_0x0899;
    L_0x084b:
        r8 = "CHANNEL_MESSAGE_GEO";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0853:
        r8 = 33;
        goto L_0x0899;
    L_0x0856:
        r8 = "CHANNEL_MESSAGE_DOC";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x085e:
        r8 = 28;
        goto L_0x0899;
    L_0x0861:
        r8 = "CHANNEL_MESSAGE_VIDEOS";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0869:
        r8 = 40;
        goto L_0x0899;
    L_0x086c:
        r8 = "MESSAGE_STICKER";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0874:
        r8 = 9;
        goto L_0x0899;
    L_0x0877:
        r8 = "CHAT_CREATED";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x087f:
        r8 = 58;
        goto L_0x0899;
    L_0x0882:
        r8 = "CHANNEL_MESSAGE_CONTACT";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x088a:
        r8 = 31;
        goto L_0x0899;
    L_0x088d:
        r8 = "MESSAGE_GAME_SCORE";
        r8 = r9.equals(r8);	 Catch:{ Throwable -> 0x0391 }
        if (r8 == 0) goto L_0x0898;
    L_0x0895:
        r8 = 17;
        goto L_0x0899;
    L_0x0898:
        r8 = -1;
    L_0x0899:
        r15 = "Photos";
        r16 = r14;
        r14 = "ChannelMessageFew";
        r29 = r3;
        r3 = " ";
        r30 = r2;
        r2 = "AttachSticker";
        switch(r8) {
            case 0: goto L_0x16ac;
            case 1: goto L_0x1686;
            case 2: goto L_0x1665;
            case 3: goto L_0x1644;
            case 4: goto L_0x1623;
            case 5: goto L_0x1601;
            case 6: goto L_0x15e4;
            case 7: goto L_0x15c2;
            case 8: goto L_0x15a0;
            case 9: goto L_0x153d;
            case 10: goto L_0x1519;
            case 11: goto L_0x14f5;
            case 12: goto L_0x14d1;
            case 13: goto L_0x14b2;
            case 14: goto L_0x1492;
            case 15: goto L_0x1472;
            case 16: goto L_0x144d;
            case 17: goto L_0x142a;
            case 18: goto L_0x1405;
            case 19: goto L_0x13db;
            case 20: goto L_0x13b6;
            case 21: goto L_0x138f;
            case 22: goto L_0x1371;
            case 23: goto L_0x1356;
            case 24: goto L_0x1332;
            case 25: goto L_0x1315;
            case 26: goto L_0x12f8;
            case 27: goto L_0x12db;
            case 28: goto L_0x12bd;
            case 29: goto L_0x1260;
            case 30: goto L_0x1242;
            case 31: goto L_0x121f;
            case 32: goto L_0x11fc;
            case 33: goto L_0x11de;
            case 34: goto L_0x11c0;
            case 35: goto L_0x11a2;
            case 36: goto L_0x1184;
            case 37: goto L_0x1165;
            case 38: goto L_0x113b;
            case 39: goto L_0x1117;
            case 40: goto L_0x10f1;
            case 41: goto L_0x10dc;
            case 42: goto L_0x10bb;
            case 43: goto L_0x1098;
            case 44: goto L_0x1075;
            case 45: goto L_0x1052;
            case 46: goto L_0x102f;
            case 47: goto L_0x100c;
            case 48: goto L_0x0var_;
            case 49: goto L_0x0var_;
            case 50: goto L_0x0var_;
            case 51: goto L_0x0f0a;
            case 52: goto L_0x0ee5;
            case 53: goto L_0x0ec0;
            case 54: goto L_0x0e9b;
            case 55: goto L_0x0e6f;
            case 56: goto L_0x0e47;
            case 57: goto L_0x0e1b;
            case 58: goto L_0x0dff;
            case 59: goto L_0x0de3;
            case 60: goto L_0x0dc7;
            case 61: goto L_0x0da4;
            case 62: goto L_0x0d88;
            case 63: goto L_0x0d6c;
            case 64: goto L_0x0d50;
            case 65: goto L_0x0d34;
            case 66: goto L_0x0d18;
            case 67: goto L_0x0cfc;
            case 68: goto L_0x0ccc;
            case 69: goto L_0x0c9d;
            case 70: goto L_0x0c6c;
            case 71: goto L_0x0c4a;
            case 72: goto L_0x0c0f;
            case 73: goto L_0x0bdc;
            case 74: goto L_0x0bad;
            case 75: goto L_0x0b7d;
            case 76: goto L_0x0b4c;
            case 77: goto L_0x0b1b;
            case 78: goto L_0x0a9b;
            case 79: goto L_0x0a6a;
            case 80: goto L_0x0a2d;
            case 81: goto L_0x09f6;
            case 82: goto L_0x09c3;
            case 83: goto L_0x0996;
            case 84: goto L_0x0969;
            case 85: goto L_0x093a;
            case 86: goto L_0x090b;
            case 87: goto L_0x08db;
            case 88: goto L_0x08d3;
            case 89: goto L_0x08d3;
            case 90: goto L_0x08d3;
            case 91: goto L_0x08b4;
            case 92: goto L_0x08d3;
            case 93: goto L_0x08d3;
            case 94: goto L_0x08d3;
            case 95: goto L_0x08d3;
            case 96: goto L_0x08d3;
            default: goto L_0x08aa;
        };
    L_0x08aa:
        r17 = r10;
        r8 = r16;
        r15 = r23;
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x1780 }
        goto L_0x16cb;
    L_0x08b4:
        r2 = "YouHaveNewMessage";
        r3 = NUM; // 0x7f0d0a75 float:1.8747544E38 double:1.0531311E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        r2 = "SecretChatName";
        r3 = NUM; // 0x7f0d08aa float:1.8746613E38 double:1.0531308734E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        r17 = r10;
        r8 = r16;
        r15 = r23;
        r21 = 0;
        r23 = r2;
        r2 = 1;
        goto L_0x16e7;
    L_0x08d3:
        r17 = r10;
        r8 = r16;
        r15 = r23;
        goto L_0x16e1;
    L_0x08db:
        if (r10 == 0) goto L_0x08f4;
    L_0x08dd:
        r2 = "NotificationActionPinnedGif";
        r3 = NUM; // 0x7f0d05fb float:1.874522E38 double:1.053130534E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r8 = 0;
        r14 = r11[r8];	 Catch:{ Throwable -> 0x0391 }
        r4[r8] = r14;	 Catch:{ Throwable -> 0x0391 }
        r8 = 1;
        r11 = r11[r8];	 Catch:{ Throwable -> 0x0391 }
        r4[r8] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0905;
    L_0x08f4:
        r2 = "NotificationActionPinnedGifChannel";
        r3 = NUM; // 0x7f0d05fc float:1.8745222E38 double:1.0531305345E-314;
        r4 = 1;
        r8 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r8[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r8);	 Catch:{ Throwable -> 0x0391 }
    L_0x0905:
        r17 = r10;
        r8 = r16;
        goto L_0x09f1;
    L_0x090b:
        r8 = r16;
        if (r10 == 0) goto L_0x0927;
    L_0x090f:
        r2 = "NotificationActionPinnedInvoice";
        r3 = NUM; // 0x7f0d05fd float:1.8745224E38 double:1.053130535E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r15 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x09ef;
    L_0x0927:
        r2 = "NotificationActionPinnedInvoiceChannel";
        r3 = NUM; // 0x7f0d05fe float:1.8745226E38 double:1.0531305354E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x09ef;
    L_0x093a:
        r8 = r16;
        if (r10 == 0) goto L_0x0956;
    L_0x093e:
        r2 = "NotificationActionPinnedGameScore";
        r3 = NUM; // 0x7f0d05f5 float:1.8745208E38 double:1.053130531E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r15 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x09ef;
    L_0x0956:
        r2 = "NotificationActionPinnedGameScoreChannel";
        r3 = NUM; // 0x7f0d05f6 float:1.874521E38 double:1.0531305315E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x09ef;
    L_0x0969:
        r8 = r16;
        if (r10 == 0) goto L_0x0984;
    L_0x096d:
        r2 = "NotificationActionPinnedGame";
        r3 = NUM; // 0x7f0d05f3 float:1.8745204E38 double:1.05313053E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r15 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x09ef;
    L_0x0984:
        r2 = "NotificationActionPinnedGameChannel";
        r3 = NUM; // 0x7f0d05f4 float:1.8745206E38 double:1.0531305305E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x09ef;
    L_0x0996:
        r8 = r16;
        if (r10 == 0) goto L_0x09b1;
    L_0x099a:
        r2 = "NotificationActionPinnedGeoLive";
        r3 = NUM; // 0x7f0d05f9 float:1.8745216E38 double:1.053130533E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r15 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x09ef;
    L_0x09b1:
        r2 = "NotificationActionPinnedGeoLiveChannel";
        r3 = NUM; // 0x7f0d05fa float:1.8745218E38 double:1.0531305335E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x09ef;
    L_0x09c3:
        r8 = r16;
        if (r10 == 0) goto L_0x09de;
    L_0x09c7:
        r2 = "NotificationActionPinnedGeo";
        r3 = NUM; // 0x7f0d05f7 float:1.8745212E38 double:1.053130532E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r15 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x09ef;
    L_0x09de:
        r2 = "NotificationActionPinnedGeoChannel";
        r3 = NUM; // 0x7f0d05f8 float:1.8745214E38 double:1.0531305325E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
    L_0x09ef:
        r17 = r10;
    L_0x09f1:
        r15 = r23;
        r2 = 0;
        goto L_0x1389;
    L_0x09f6:
        r8 = r16;
        if (r10 == 0) goto L_0x0a16;
    L_0x09fa:
        r2 = "NotificationActionPinnedPoll2";
        r3 = NUM; // 0x7f0d0606 float:1.8745242E38 double:1.0531305394E-314;
        r4 = 3;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r4[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r4[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r14 = 2;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x09ef;
    L_0x0a16:
        r2 = "NotificationActionPinnedPollChannel2";
        r3 = NUM; // 0x7f0d0608 float:1.8745246E38 double:1.0531305404E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r15 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x09ef;
    L_0x0a2d:
        r8 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0a52;
    L_0x0a33:
        r4 = "NotificationActionPinnedContact2";
        r15 = NUM; // 0x7f0d05ee float:1.8745194E38 double:1.0531305275E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0391 }
        r16 = 0;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r16 = 1;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r14 = 2;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r3[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r15, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0a52:
        r3 = "NotificationActionPinnedContactChannel2";
        r4 = NUM; // 0x7f0d05f0 float:1.8745198E38 double:1.0531305285E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0a6a:
        r8 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0a88;
    L_0x0a70:
        r3 = "NotificationActionPinnedVoice";
        r4 = NUM; // 0x7f0d0613 float:1.8745269E38 double:1.053130546E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0a88:
        r3 = "NotificationActionPinnedVoiceChannel";
        r4 = NUM; // 0x7f0d0614 float:1.874527E38 double:1.0531305463E-314;
        r14 = 1;
        r15 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r15[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r15);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0a9b:
        r8 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0ae4;
    L_0x0aa1:
        r4 = r11.length;	 Catch:{ Throwable -> 0x0391 }
        r14 = 2;
        if (r4 <= r14) goto L_0x0acc;
    L_0x0aa5:
        r4 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4 = android.text.TextUtils.isEmpty(r4);	 Catch:{ Throwable -> 0x0391 }
        if (r4 != 0) goto L_0x0acc;
    L_0x0aad:
        r4 = "NotificationActionPinnedStickerEmoji";
        r15 = NUM; // 0x7f0d060d float:1.8745256E38 double:1.053130543E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0391 }
        r16 = 0;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r16 = 1;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r14 = 2;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r3[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r15, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0acc:
        r3 = "NotificationActionPinnedSticker";
        r4 = NUM; // 0x7f0d060b float:1.8745252E38 double:1.053130542E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0ae4:
        r3 = r11.length;	 Catch:{ Throwable -> 0x0391 }
        r4 = 1;
        if (r3 <= r4) goto L_0x0b08;
    L_0x0ae8:
        r3 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r3 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Throwable -> 0x0391 }
        if (r3 != 0) goto L_0x0b08;
    L_0x0af0:
        r3 = "NotificationActionPinnedStickerEmojiChannel";
        r4 = NUM; // 0x7f0d060e float:1.8745259E38 double:1.0531305433E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0b08:
        r3 = "NotificationActionPinnedStickerChannel";
        r4 = NUM; // 0x7f0d060c float:1.8745254E38 double:1.0531305424E-314;
        r14 = 1;
        r15 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r15[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r15);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0b1b:
        r8 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0b39;
    L_0x0b21:
        r3 = "NotificationActionPinnedFile";
        r4 = NUM; // 0x7f0d05f1 float:1.87452E38 double:1.053130529E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0b39:
        r3 = "NotificationActionPinnedFileChannel";
        r4 = NUM; // 0x7f0d05f2 float:1.8745202E38 double:1.0531305295E-314;
        r14 = 1;
        r15 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r15[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r15);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0b4c:
        r8 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0b6a;
    L_0x0b52:
        r3 = "NotificationActionPinnedRound";
        r4 = NUM; // 0x7f0d0609 float:1.8745248E38 double:1.053130541E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0b6a:
        r3 = "NotificationActionPinnedRoundChannel";
        r4 = NUM; // 0x7f0d060a float:1.874525E38 double:1.0531305414E-314;
        r14 = 1;
        r15 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r15[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r15);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0b7d:
        r8 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0b9b;
    L_0x0b83:
        r3 = "NotificationActionPinnedVideo";
        r4 = NUM; // 0x7f0d0611 float:1.8745265E38 double:1.053130545E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0b9b:
        r3 = "NotificationActionPinnedVideoChannel";
        r4 = NUM; // 0x7f0d0612 float:1.8745267E38 double:1.0531305453E-314;
        r14 = 1;
        r15 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r15[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r15);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0bad:
        r8 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0bca;
    L_0x0bb3:
        r3 = "NotificationActionPinnedPhoto";
        r4 = NUM; // 0x7f0d0603 float:1.8745236E38 double:1.053130538E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0bca:
        r3 = "NotificationActionPinnedPhotoChannel";
        r4 = NUM; // 0x7f0d0604 float:1.8745238E38 double:1.0531305384E-314;
        r14 = 1;
        r15 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r15[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r15);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0bdc:
        r8 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0bf9;
    L_0x0be2:
        r3 = "NotificationActionPinnedNoText";
        r4 = NUM; // 0x7f0d0601 float:1.8745232E38 double:1.053130537E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0bf9:
        r3 = "NotificationActionPinnedNoTextChannel";
        r4 = NUM; // 0x7f0d0602 float:1.8745234E38 double:1.0531305374E-314;
        r14 = 1;
        r15 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r15[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r15);	 Catch:{ Throwable -> 0x0391 }
    L_0x0c0a:
        r15 = r2;
    L_0x0c0b:
        r17 = r10;
        goto L_0x15fc;
    L_0x0c0f:
        r8 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r4 = "NotificationActionPinnedText";
        r15 = NUM; // 0x7f0d060f float:1.874526E38 double:1.053130544E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0391 }
        r16 = 0;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r16 = 1;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r14 = 2;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r3[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r15, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0CLASSNAME:
        r3 = "NotificationActionPinnedTextChannel";
        r4 = NUM; // 0x7f0d0610 float:1.8745263E38 double:1.0531305443E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0c4a:
        r8 = r16;
        r2 = r23;
        r3 = "NotificationGroupAlbum";
        r4 = NUM; // 0x7f0d061c float:1.8745287E38 double:1.0531305503E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
    L_0x0CLASSNAME:
        r15 = r2;
    L_0x0CLASSNAME:
        r17 = r10;
        r23 = r20;
        r2 = 1;
        goto L_0x16e5;
    L_0x0c6c:
        r8 = r16;
        r2 = r23;
        r4 = "NotificationGroupFew";
        r15 = NUM; // 0x7f0d061d float:1.8745289E38 double:1.053130551E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0391 }
        r16 = 0;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r16 = 1;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r14 = "Videos";
        r16 = 2;
        r11 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ Throwable -> 0x0391 }
        r11 = r11.intValue();	 Catch:{ Throwable -> 0x0391 }
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r14, r11);	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r15, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0CLASSNAME;
    L_0x0c9d:
        r8 = r16;
        r2 = r23;
        r4 = "NotificationGroupFew";
        r3 = NUM; // 0x7f0d061d float:1.8745289E38 double:1.053130551E-314;
        r14 = 3;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r16 = 0;
        r22 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r14[r16] = r22;	 Catch:{ Throwable -> 0x0391 }
        r16 = 1;
        r23 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r14[r16] = r23;	 Catch:{ Throwable -> 0x0391 }
        r16 = 2;
        r11 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ Throwable -> 0x0391 }
        r11 = r11.intValue();	 Catch:{ Throwable -> 0x0391 }
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r15, r11);	 Catch:{ Throwable -> 0x0391 }
        r14[r16] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0CLASSNAME;
    L_0x0ccc:
        r8 = r16;
        r2 = r23;
        r15 = "NotificationGroupForwardedFew";
        r3 = NUM; // 0x7f0d061e float:1.874529E38 double:1.0531305513E-314;
        r14 = 3;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r16 = 0;
        r22 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r14[r16] = r22;	 Catch:{ Throwable -> 0x0391 }
        r16 = 1;
        r23 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r14[r16] = r23;	 Catch:{ Throwable -> 0x0391 }
        r16 = 2;
        r11 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ Throwable -> 0x0391 }
        r11 = r11.intValue();	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r11);	 Catch:{ Throwable -> 0x0391 }
        r14[r16] = r4;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r15, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0CLASSNAME;
    L_0x0cfc:
        r8 = r16;
        r2 = r23;
        r3 = "NotificationGroupAddSelfMega";
        r4 = NUM; // 0x7f0d061b float:1.8745285E38 double:1.05313055E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0d18:
        r8 = r16;
        r2 = r23;
        r3 = "NotificationGroupAddSelf";
        r4 = NUM; // 0x7f0d061a float:1.8745283E38 double:1.0531305493E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0d34:
        r8 = r16;
        r2 = r23;
        r3 = "NotificationGroupLeftMember";
        r4 = NUM; // 0x7f0d0621 float:1.8745297E38 double:1.0531305527E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0d50:
        r8 = r16;
        r2 = r23;
        r3 = "NotificationGroupKickYou";
        r4 = NUM; // 0x7f0d0620 float:1.8745295E38 double:1.053130552E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0d6c:
        r8 = r16;
        r2 = r23;
        r3 = "NotificationGroupKickMember";
        r4 = NUM; // 0x7f0d061f float:1.8745293E38 double:1.0531305517E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0d88:
        r8 = r16;
        r2 = r23;
        r3 = "NotificationInvitedToGroup";
        r4 = NUM; // 0x7f0d0622 float:1.87453E38 double:1.053130553E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0da4:
        r8 = r16;
        r2 = r23;
        r4 = "NotificationGroupAddMember";
        r15 = NUM; // 0x7f0d0619 float:1.874528E38 double:1.053130549E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0391 }
        r16 = 0;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r16 = 1;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r14 = 2;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r3[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r15, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0dc7:
        r8 = r16;
        r2 = r23;
        r3 = "NotificationEditedGroupPhoto";
        r4 = NUM; // 0x7f0d0618 float:1.8745279E38 double:1.0531305483E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0de3:
        r8 = r16;
        r2 = r23;
        r3 = "NotificationEditedGroupName";
        r4 = NUM; // 0x7f0d0617 float:1.8745277E38 double:1.053130548E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0dff:
        r8 = r16;
        r2 = r23;
        r3 = "NotificationInvitedToGroup";
        r4 = NUM; // 0x7f0d0622 float:1.87453E38 double:1.053130553E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0e1b:
        r8 = r16;
        r2 = r23;
        r4 = "NotificationMessageGroupInvoice";
        r15 = NUM; // 0x7f0d0635 float:1.8745338E38 double:1.0531305626E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0391 }
        r16 = 0;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r16 = 1;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r14 = 2;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r3[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r15, r3);	 Catch:{ Throwable -> 0x0391 }
        r3 = "PaymentInvoice";
        r11 = NUM; // 0x7f0d0790 float:1.8746041E38 double:1.053130734E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r11);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0var_;
    L_0x0e47:
        r8 = r16;
        r2 = r23;
        r4 = "NotificationMessageGroupGameScored";
        r15 = NUM; // 0x7f0d0633 float:1.8745334E38 double:1.0531305616E-314;
        r3 = 4;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0391 }
        r16 = 0;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r16 = 1;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r14 = 2;
        r16 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r3[r14] = r16;	 Catch:{ Throwable -> 0x0391 }
        r14 = 3;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r3[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r15, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0a;
    L_0x0e6f:
        r8 = r16;
        r2 = r23;
        r4 = "NotificationMessageGroupGame";
        r15 = NUM; // 0x7f0d0632 float:1.8745332E38 double:1.053130561E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0391 }
        r16 = 0;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r16 = 1;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r14 = 2;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r3[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r15, r3);	 Catch:{ Throwable -> 0x0391 }
        r3 = "AttachGame";
        r11 = NUM; // 0x7f0d0136 float:1.8742743E38 double:1.0531299307E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r11);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0var_;
    L_0x0e9b:
        r8 = r16;
        r2 = r23;
        r3 = "NotificationMessageGroupGif";
        r4 = NUM; // 0x7f0d0634 float:1.8745336E38 double:1.053130562E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        r3 = "AttachGif";
        r11 = NUM; // 0x7f0d0137 float:1.8742746E38 double:1.053129931E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r11);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0var_;
    L_0x0ec0:
        r8 = r16;
        r2 = r23;
        r3 = "NotificationMessageGroupLiveLocation";
        r4 = NUM; // 0x7f0d0636 float:1.874534E38 double:1.053130563E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        r3 = "AttachLiveLocation";
        r11 = NUM; // 0x7f0d013c float:1.8742756E38 double:1.0531299337E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r11);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0var_;
    L_0x0ee5:
        r8 = r16;
        r2 = r23;
        r3 = "NotificationMessageGroupMap";
        r4 = NUM; // 0x7f0d0637 float:1.8745342E38 double:1.0531305636E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        r3 = "AttachLocation";
        r11 = NUM; // 0x7f0d013e float:1.874276E38 double:1.0531299347E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r11);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0var_;
    L_0x0f0a:
        r8 = r16;
        r2 = r23;
        r4 = "NotificationMessageGroupPoll2";
        r15 = NUM; // 0x7f0d063c float:1.8745352E38 double:1.053130566E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0391 }
        r16 = 0;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r16 = 1;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r14 = 2;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r3[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r15, r3);	 Catch:{ Throwable -> 0x0391 }
        r3 = "Poll";
        r11 = NUM; // 0x7f0d07eb float:1.8746226E38 double:1.053130779E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r11);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0var_;
    L_0x0var_:
        r8 = r16;
        r2 = r23;
        r4 = "NotificationMessageGroupContact2";
        r15 = NUM; // 0x7f0d0630 float:1.8745327E38 double:1.05313056E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0391 }
        r16 = 0;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r16 = 1;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r14 = 2;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r3[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r15, r3);	 Catch:{ Throwable -> 0x0391 }
        r3 = "AttachContact";
        r11 = NUM; // 0x7f0d0132 float:1.8742735E38 double:1.0531299287E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r11);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0var_;
    L_0x0var_:
        r8 = r16;
        r2 = r23;
        r3 = "NotificationMessageGroupAudio";
        r4 = NUM; // 0x7f0d062e float:1.8745323E38 double:1.053130559E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r14[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        r3 = "AttachAudio";
        r11 = NUM; // 0x7f0d0130 float:1.8742731E38 double:1.0531299277E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r11);	 Catch:{ Throwable -> 0x0391 }
    L_0x0var_:
        r15 = r2;
        r21 = r3;
        goto L_0x1539;
    L_0x0var_:
        r8 = r16;
        r4 = r23;
        r14 = r11.length;	 Catch:{ Throwable -> 0x0391 }
        r15 = 2;
        if (r14 <= r15) goto L_0x0fd5;
    L_0x0var_:
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r15 = android.text.TextUtils.isEmpty(r16);	 Catch:{ Throwable -> 0x0391 }
        if (r15 != 0) goto L_0x0fd5;
    L_0x0var_:
        r15 = "NotificationMessageGroupStickerEmoji";
        r14 = 3;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r21 = 0;
        r22 = r11[r21];	 Catch:{ Throwable -> 0x0391 }
        r14[r21] = r22;	 Catch:{ Throwable -> 0x0391 }
        r21 = 1;
        r23 = r11[r21];	 Catch:{ Throwable -> 0x0391 }
        r14[r21] = r23;	 Catch:{ Throwable -> 0x0391 }
        r17 = 2;
        r21 = r11[r17];	 Catch:{ Throwable -> 0x0391 }
        r14[r17] = r21;	 Catch:{ Throwable -> 0x0391 }
        r23 = r4;
        r4 = NUM; // 0x7f0d063f float:1.8745358E38 double:1.0531305676E-314;
        r4 = org.telegram.messenger.LocaleController.formatString(r15, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        r14 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0391 }
        r14.<init>();	 Catch:{ Throwable -> 0x0391 }
        r11 = r11[r17];	 Catch:{ Throwable -> 0x0391 }
        r14.append(r11);	 Catch:{ Throwable -> 0x0391 }
        r14.append(r3);	 Catch:{ Throwable -> 0x0391 }
        r3 = NUM; // 0x7f0d0145 float:1.8742774E38 double:1.053129938E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        r14.append(r2);	 Catch:{ Throwable -> 0x0391 }
        r2 = r14.toString();	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x0fd5:
        r23 = r4;
        r4 = "NotificationMessageGroupSticker";
        r15 = NUM; // 0x7f0d063e float:1.8745356E38 double:1.053130567E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r16 = 0;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r14[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r16 = 1;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r14[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r15, r14);	 Catch:{ Throwable -> 0x0391 }
        r14 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0391 }
        r14.<init>();	 Catch:{ Throwable -> 0x0391 }
        r11 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r14.append(r11);	 Catch:{ Throwable -> 0x0391 }
        r14.append(r3);	 Catch:{ Throwable -> 0x0391 }
        r3 = NUM; // 0x7f0d0145 float:1.8742774E38 double:1.053129938E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        r14.append(r2);	 Catch:{ Throwable -> 0x0391 }
        r2 = r14.toString();	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x100c:
        r8 = r16;
        r2 = "NotificationMessageGroupDocument";
        r3 = NUM; // 0x7f0d0631 float:1.874533E38 double:1.0531305606E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r15 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachDocument";
        r3 = NUM; // 0x7f0d0135 float:1.8742741E38 double:1.05312993E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x102f:
        r8 = r16;
        r2 = "NotificationMessageGroupRound";
        r3 = NUM; // 0x7f0d063d float:1.8745354E38 double:1.0531305666E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r15 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachRound";
        r3 = NUM; // 0x7f0d0144 float:1.8742772E38 double:1.0531299376E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x1052:
        r8 = r16;
        r2 = "NotificationMessageGroupVideo";
        r3 = NUM; // 0x7f0d0641 float:1.8745362E38 double:1.0531305685E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r15 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachVideo";
        r3 = NUM; // 0x7f0d0148 float:1.874278E38 double:1.0531299396E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x1075:
        r8 = r16;
        r2 = "NotificationMessageGroupPhoto";
        r3 = NUM; // 0x7f0d063a float:1.8745348E38 double:1.053130565E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r15 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachPhoto";
        r3 = NUM; // 0x7f0d0142 float:1.8742768E38 double:1.0531299366E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x1098:
        r8 = r16;
        r2 = "NotificationMessageGroupNoText";
        r3 = NUM; // 0x7f0d0639 float:1.8745346E38 double:1.0531305646E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r15 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        r2 = "Message";
        r3 = NUM; // 0x7f0d0577 float:1.8744952E38 double:1.0531304687E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x10bb:
        r8 = r16;
        r3 = "NotificationMessageGroupText";
        r4 = NUM; // 0x7f0d0640 float:1.874536E38 double:1.053130568E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r2[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r2[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r14 = 2;
        r15 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r2[r14] = r15;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r2);	 Catch:{ Throwable -> 0x0391 }
        r2 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x10dc:
        r8 = r16;
        r2 = "ChannelMessageAlbum";
        r3 = NUM; // 0x7f0d0225 float:1.8743228E38 double:1.053130049E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x1384;
    L_0x10f1:
        r8 = r16;
        r2 = 2;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0391 }
        r3 = 0;
        r4 = r11[r3];	 Catch:{ Throwable -> 0x0391 }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x0391 }
        r3 = "Videos";
        r4 = 1;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ Throwable -> 0x0391 }
        r11 = r11.intValue();	 Catch:{ Throwable -> 0x0391 }
        r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r11);	 Catch:{ Throwable -> 0x0391 }
        r2[r4] = r3;	 Catch:{ Throwable -> 0x0391 }
        r3 = NUM; // 0x7f0d022a float:1.8743238E38 double:1.0531300513E-314;
        r4 = org.telegram.messenger.LocaleController.formatString(r14, r3, r2);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x1384;
    L_0x1117:
        r8 = r16;
        r2 = 2;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0391 }
        r3 = 0;
        r4 = r11[r3];	 Catch:{ Throwable -> 0x0391 }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x0391 }
        r3 = 1;
        r4 = r11[r3];	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.Utilities.parseInt(r4);	 Catch:{ Throwable -> 0x0391 }
        r4 = r4.intValue();	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatPluralString(r15, r4);	 Catch:{ Throwable -> 0x0391 }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x0391 }
        r3 = NUM; // 0x7f0d022a float:1.8743238E38 double:1.0531300513E-314;
        r4 = org.telegram.messenger.LocaleController.formatString(r14, r3, r2);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x1384;
    L_0x113b:
        r8 = r16;
        r2 = 2;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0391 }
        r3 = 0;
        r4 = r11[r3];	 Catch:{ Throwable -> 0x0391 }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x0391 }
        r3 = "ForwardedMessageCount";
        r4 = 1;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ Throwable -> 0x0391 }
        r11 = r11.intValue();	 Catch:{ Throwable -> 0x0391 }
        r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r11);	 Catch:{ Throwable -> 0x0391 }
        r3 = r3.toLowerCase();	 Catch:{ Throwable -> 0x0391 }
        r2[r4] = r3;	 Catch:{ Throwable -> 0x0391 }
        r3 = NUM; // 0x7f0d022a float:1.8743238E38 double:1.0531300513E-314;
        r4 = org.telegram.messenger.LocaleController.formatString(r14, r3, r2);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x1384;
    L_0x1165:
        r8 = r16;
        r3 = "NotificationMessageGameScored";
        r4 = NUM; // 0x7f0d062c float:1.874532E38 double:1.053130558E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0391 }
        r15 = 0;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r2[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        r16 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r2[r15] = r16;	 Catch:{ Throwable -> 0x0391 }
        r14 = 2;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r2[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r2);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x09ef;
    L_0x1184:
        r8 = r16;
        r2 = "NotificationMessageGame";
        r3 = NUM; // 0x7f0d062b float:1.8745317E38 double:1.0531305577E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachGame";
        r3 = NUM; // 0x7f0d0136 float:1.8742743E38 double:1.0531299307E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x11a2:
        r8 = r16;
        r2 = "ChannelMessageGIF";
        r3 = NUM; // 0x7f0d022b float:1.874324E38 double:1.0531300518E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachGif";
        r3 = NUM; // 0x7f0d0137 float:1.8742746E38 double:1.053129931E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x11c0:
        r8 = r16;
        r2 = "ChannelMessageLiveLocation";
        r3 = NUM; // 0x7f0d022c float:1.8743242E38 double:1.053130052E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachLiveLocation";
        r3 = NUM; // 0x7f0d013c float:1.8742756E38 double:1.0531299337E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x11de:
        r8 = r16;
        r2 = "ChannelMessageMap";
        r3 = NUM; // 0x7f0d022d float:1.8743244E38 double:1.0531300527E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachLocation";
        r3 = NUM; // 0x7f0d013e float:1.874276E38 double:1.0531299347E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x11fc:
        r8 = r16;
        r2 = "ChannelMessagePoll2";
        r3 = NUM; // 0x7f0d0232 float:1.8743255E38 double:1.053130055E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r15 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        r2 = "Poll";
        r3 = NUM; // 0x7f0d07eb float:1.8746226E38 double:1.053130779E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x121f:
        r8 = r16;
        r2 = "ChannelMessageContact2";
        r3 = NUM; // 0x7f0d0228 float:1.8743234E38 double:1.0531300503E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r15 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachContact";
        r3 = NUM; // 0x7f0d0132 float:1.8742735E38 double:1.0531299287E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x1242:
        r8 = r16;
        r2 = "ChannelMessageAudio";
        r3 = NUM; // 0x7f0d0226 float:1.874323E38 double:1.0531300493E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachAudio";
        r3 = NUM; // 0x7f0d0130 float:1.8742731E38 double:1.0531299277E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x1260:
        r8 = r16;
        r4 = r11.length;	 Catch:{ Throwable -> 0x0391 }
        r15 = 1;
        if (r4 <= r15) goto L_0x12a3;
    L_0x1266:
        r4 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r4 = android.text.TextUtils.isEmpty(r4);	 Catch:{ Throwable -> 0x0391 }
        if (r4 != 0) goto L_0x12a3;
    L_0x126e:
        r4 = "ChannelMessageStickerEmoji";
        r15 = NUM; // 0x7f0d0235 float:1.874326E38 double:1.0531300567E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r16 = 0;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r14[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r16 = 1;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r14[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r15, r14);	 Catch:{ Throwable -> 0x0391 }
        r14 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0391 }
        r14.<init>();	 Catch:{ Throwable -> 0x0391 }
        r11 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r14.append(r11);	 Catch:{ Throwable -> 0x0391 }
        r14.append(r3);	 Catch:{ Throwable -> 0x0391 }
        r3 = NUM; // 0x7f0d0145 float:1.8742774E38 double:1.053129938E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        r14.append(r2);	 Catch:{ Throwable -> 0x0391 }
        r2 = r14.toString();	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x12a3:
        r3 = "ChannelMessageSticker";
        r4 = NUM; // 0x7f0d0234 float:1.8743259E38 double:1.053130056E-314;
        r14 = 1;
        r15 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r15[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r15);	 Catch:{ Throwable -> 0x0391 }
        r3 = NUM; // 0x7f0d0145 float:1.8742774E38 double:1.053129938E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x12bd:
        r8 = r16;
        r2 = "ChannelMessageDocument";
        r3 = NUM; // 0x7f0d0229 float:1.8743236E38 double:1.053130051E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachDocument";
        r3 = NUM; // 0x7f0d0135 float:1.8742741E38 double:1.05312993E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x12db:
        r8 = r16;
        r2 = "ChannelMessageRound";
        r3 = NUM; // 0x7f0d0233 float:1.8743257E38 double:1.0531300557E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachRound";
        r3 = NUM; // 0x7f0d0144 float:1.8742772E38 double:1.0531299376E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x12f8:
        r8 = r16;
        r2 = "ChannelMessageVideo";
        r3 = NUM; // 0x7f0d0236 float:1.8743263E38 double:1.053130057E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachVideo";
        r3 = NUM; // 0x7f0d0148 float:1.874278E38 double:1.0531299396E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x1315:
        r8 = r16;
        r2 = "ChannelMessagePhoto";
        r3 = NUM; // 0x7f0d0230 float:1.874325E38 double:1.053130054E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachPhoto";
        r3 = NUM; // 0x7f0d0142 float:1.8742768E38 double:1.0531299366E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x1332:
        r8 = r16;
        r2 = "ChannelMessageNoText";
        r3 = NUM; // 0x7f0d022f float:1.8743249E38 double:1.0531300537E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        r2 = "Message";
        r3 = NUM; // 0x7f0d0577 float:1.8744952E38 double:1.0531304687E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
    L_0x134e:
        r21 = r2;
        r17 = r10;
        r15 = r23;
        r2 = 0;
        goto L_0x138b;
    L_0x1356:
        r8 = r16;
        r2 = "NotificationMessageText";
        r3 = NUM; // 0x7f0d064f float:1.874539E38 double:1.0531305755E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r15 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r15 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        r2 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        goto L_0x134e;
    L_0x1371:
        r8 = r16;
        r2 = "NotificationMessageAlbum";
        r3 = NUM; // 0x7f0d0624 float:1.8745303E38 double:1.053130554E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
    L_0x1384:
        r17 = r10;
        r15 = r23;
        r2 = 1;
    L_0x1389:
        r21 = 0;
    L_0x138b:
        r23 = r20;
        goto L_0x16e7;
    L_0x138f:
        r8 = r16;
        r2 = "NotificationMessageFew";
        r3 = NUM; // 0x7f0d0629 float:1.8745313E38 double:1.0531305567E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r15 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0391 }
        r14 = "Videos";
        r15 = 1;
        r11 = r11[r15];	 Catch:{ Throwable -> 0x0391 }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ Throwable -> 0x0391 }
        r11 = r11.intValue();	 Catch:{ Throwable -> 0x0391 }
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r14, r11);	 Catch:{ Throwable -> 0x0391 }
        r4[r15] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x1384;
    L_0x13b6:
        r8 = r16;
        r2 = "NotificationMessageFew";
        r3 = NUM; // 0x7f0d0629 float:1.8745313E38 double:1.0531305567E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r16 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r16;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ Throwable -> 0x0391 }
        r11 = r11.intValue();	 Catch:{ Throwable -> 0x0391 }
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r15, r11);	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x1384;
    L_0x13db:
        r8 = r16;
        r15 = r23;
        r2 = "NotificationMessageForwardFew";
        r3 = NUM; // 0x7f0d062a float:1.8745315E38 double:1.053130557E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r16 = 0;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r14[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r16 = 1;
        r11 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ Throwable -> 0x0391 }
        r11 = r11.intValue();	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r11);	 Catch:{ Throwable -> 0x0391 }
        r14[r16] = r4;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0CLASSNAME;
    L_0x1405:
        r8 = r16;
        r15 = r23;
        r2 = "NotificationMessageInvoice";
        r3 = NUM; // 0x7f0d0642 float:1.8745364E38 double:1.053130569E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r16 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r16;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        r2 = "PaymentInvoice";
        r3 = NUM; // 0x7f0d0790 float:1.8746041E38 double:1.053130734E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x1537;
    L_0x142a:
        r8 = r16;
        r15 = r23;
        r3 = "NotificationMessageGameScored";
        r4 = NUM; // 0x7f0d062c float:1.874532E38 double:1.053130558E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0391 }
        r16 = 0;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r2[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r16 = 1;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r2[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r14 = 2;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r2[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r2);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x0c0b;
    L_0x144d:
        r8 = r16;
        r15 = r23;
        r2 = "NotificationMessageGame";
        r3 = NUM; // 0x7f0d062b float:1.8745317E38 double:1.0531305577E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r16 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r16;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachGame";
        r3 = NUM; // 0x7f0d0136 float:1.8742743E38 double:1.0531299307E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x1537;
    L_0x1472:
        r8 = r16;
        r15 = r23;
        r2 = "NotificationMessageGif";
        r3 = NUM; // 0x7f0d062d float:1.8745321E38 double:1.0531305587E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachGif";
        r3 = NUM; // 0x7f0d0137 float:1.8742746E38 double:1.053129931E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x1537;
    L_0x1492:
        r8 = r16;
        r15 = r23;
        r2 = "NotificationMessageLiveLocation";
        r3 = NUM; // 0x7f0d0643 float:1.8745366E38 double:1.0531305695E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachLiveLocation";
        r3 = NUM; // 0x7f0d013c float:1.8742756E38 double:1.0531299337E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x1537;
    L_0x14b2:
        r8 = r16;
        r15 = r23;
        r2 = "NotificationMessageMap";
        r3 = NUM; // 0x7f0d0644 float:1.8745368E38 double:1.05313057E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachLocation";
        r3 = NUM; // 0x7f0d013e float:1.874276E38 double:1.0531299347E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x1537;
    L_0x14d1:
        r8 = r16;
        r15 = r23;
        r2 = "NotificationMessagePoll2";
        r3 = NUM; // 0x7f0d0649 float:1.8745378E38 double:1.0531305725E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r16 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r16;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        r2 = "Poll";
        r3 = NUM; // 0x7f0d07eb float:1.8746226E38 double:1.053130779E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x1537;
    L_0x14f5:
        r8 = r16;
        r15 = r23;
        r2 = "NotificationMessageContact2";
        r3 = NUM; // 0x7f0d0627 float:1.874531E38 double:1.0531305557E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r14 = 0;
        r16 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r16;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        r11 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachContact";
        r3 = NUM; // 0x7f0d0132 float:1.8742735E38 double:1.0531299287E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x1537;
    L_0x1519:
        r8 = r16;
        r15 = r23;
        r2 = "NotificationMessageAudio";
        r3 = NUM; // 0x7f0d0625 float:1.8745305E38 double:1.0531305547E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r14[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachAudio";
        r3 = NUM; // 0x7f0d0130 float:1.8742731E38 double:1.0531299277E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
    L_0x1537:
        r21 = r2;
    L_0x1539:
        r17 = r10;
        goto L_0x16a8;
    L_0x153d:
        r8 = r16;
        r15 = r23;
        r4 = r11.length;	 Catch:{ Throwable -> 0x0391 }
        r14 = 1;
        if (r4 <= r14) goto L_0x1584;
    L_0x1545:
        r4 = r11[r14];	 Catch:{ Throwable -> 0x0391 }
        r4 = android.text.TextUtils.isEmpty(r4);	 Catch:{ Throwable -> 0x0391 }
        if (r4 != 0) goto L_0x1584;
    L_0x154d:
        r4 = "NotificationMessageStickerEmoji";
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0391 }
        r16 = 0;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r14[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r16 = 1;
        r17 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r14[r16] = r17;	 Catch:{ Throwable -> 0x0391 }
        r17 = r10;
        r10 = NUM; // 0x7f0d064e float:1.8745388E38 double:1.053130575E-314;
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r10, r14);	 Catch:{ Throwable -> 0x0391 }
        r10 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0391 }
        r10.<init>();	 Catch:{ Throwable -> 0x0391 }
        r11 = r11[r16];	 Catch:{ Throwable -> 0x0391 }
        r10.append(r11);	 Catch:{ Throwable -> 0x0391 }
        r10.append(r3);	 Catch:{ Throwable -> 0x0391 }
        r3 = NUM; // 0x7f0d0145 float:1.8742774E38 double:1.053129938E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        r10.append(r2);	 Catch:{ Throwable -> 0x0391 }
        r2 = r10.toString();	 Catch:{ Throwable -> 0x0391 }
        goto L_0x16a6;
    L_0x1584:
        r17 = r10;
        r3 = "NotificationMessageSticker";
        r4 = NUM; // 0x7f0d064d float:1.8745386E38 double:1.0531305745E-314;
        r10 = 1;
        r14 = new java.lang.Object[r10];	 Catch:{ Throwable -> 0x0391 }
        r10 = 0;
        r11 = r11[r10];	 Catch:{ Throwable -> 0x0391 }
        r14[r10] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0391 }
        r3 = NUM; // 0x7f0d0145 float:1.8742774E38 double:1.053129938E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x16a6;
    L_0x15a0:
        r17 = r10;
        r8 = r16;
        r15 = r23;
        r2 = "NotificationMessageDocument";
        r3 = NUM; // 0x7f0d0628 float:1.8745311E38 double:1.053130556E-314;
        r4 = 1;
        r10 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r10[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachDocument";
        r3 = NUM; // 0x7f0d0135 float:1.8742741E38 double:1.05312993E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x16a6;
    L_0x15c2:
        r17 = r10;
        r8 = r16;
        r15 = r23;
        r2 = "NotificationMessageRound";
        r3 = NUM; // 0x7f0d064a float:1.874538E38 double:1.053130573E-314;
        r4 = 1;
        r10 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r10[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachRound";
        r3 = NUM; // 0x7f0d0144 float:1.8742772E38 double:1.0531299376E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x16a6;
    L_0x15e4:
        r17 = r10;
        r8 = r16;
        r15 = r23;
        r2 = "ActionTakeScreenshoot";
        r3 = NUM; // 0x7f0d008e float:1.8742403E38 double:1.0531298477E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        r3 = "un1";
        r4 = 0;
        r10 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = r2.replace(r3, r10);	 Catch:{ Throwable -> 0x0391 }
    L_0x15fc:
        r23 = r20;
        r2 = 0;
        goto L_0x16e5;
    L_0x1601:
        r17 = r10;
        r8 = r16;
        r15 = r23;
        r2 = "NotificationMessageSDVideo";
        r3 = NUM; // 0x7f0d064c float:1.8745384E38 double:1.053130574E-314;
        r4 = 1;
        r10 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r10[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachDestructingVideo";
        r3 = NUM; // 0x7f0d0134 float:1.874274E38 double:1.0531299297E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x16a6;
    L_0x1623:
        r17 = r10;
        r8 = r16;
        r15 = r23;
        r2 = "NotificationMessageVideo";
        r3 = NUM; // 0x7f0d0650 float:1.8745392E38 double:1.053130576E-314;
        r4 = 1;
        r10 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r10[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachVideo";
        r3 = NUM; // 0x7f0d0148 float:1.874278E38 double:1.0531299396E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x16a6;
    L_0x1644:
        r17 = r10;
        r8 = r16;
        r15 = r23;
        r2 = "NotificationMessageSDPhoto";
        r3 = NUM; // 0x7f0d064b float:1.8745382E38 double:1.0531305735E-314;
        r4 = 1;
        r10 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r10[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachDestructingPhoto";
        r3 = NUM; // 0x7f0d0133 float:1.8742737E38 double:1.053129929E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x16a6;
    L_0x1665:
        r17 = r10;
        r8 = r16;
        r15 = r23;
        r2 = "NotificationMessagePhoto";
        r3 = NUM; // 0x7f0d0647 float:1.8745374E38 double:1.0531305715E-314;
        r4 = 1;
        r10 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r10[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x0391 }
        r2 = "AttachPhoto";
        r3 = NUM; // 0x7f0d0142 float:1.8742768E38 double:1.0531299366E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
        goto L_0x16a6;
    L_0x1686:
        r17 = r10;
        r8 = r16;
        r15 = r23;
        r2 = "NotificationMessageNoText";
        r3 = NUM; // 0x7f0d0646 float:1.8745372E38 double:1.053130571E-314;
        r4 = 1;
        r10 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r4 = 0;
        r11 = r11[r4];	 Catch:{ Throwable -> 0x0391 }
        r10[r4] = r11;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x0391 }
        r2 = "Message";
        r3 = NUM; // 0x7f0d0577 float:1.8744952E38 double:1.0531304687E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0391 }
    L_0x16a6:
        r21 = r2;
    L_0x16a8:
        r23 = r20;
        r2 = 0;
        goto L_0x16e7;
    L_0x16ac:
        r17 = r10;
        r8 = r16;
        r15 = r23;
        r2 = "NotificationMessageText";
        r3 = NUM; // 0x7f0d064f float:1.874539E38 double:1.0531305755E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0391 }
        r10 = 0;
        r14 = r11[r10];	 Catch:{ Throwable -> 0x0391 }
        r4[r10] = r14;	 Catch:{ Throwable -> 0x0391 }
        r10 = 1;
        r14 = r11[r10];	 Catch:{ Throwable -> 0x0391 }
        r4[r10] = r14;	 Catch:{ Throwable -> 0x0391 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0391 }
        r2 = r11[r10];	 Catch:{ Throwable -> 0x0391 }
        goto L_0x16a6;
    L_0x16cb:
        if (r2 == 0) goto L_0x16e1;
    L_0x16cd:
        r2 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0391 }
        r2.<init>();	 Catch:{ Throwable -> 0x0391 }
        r3 = "unhandled loc_key = ";
        r2.append(r3);	 Catch:{ Throwable -> 0x0391 }
        r2.append(r9);	 Catch:{ Throwable -> 0x0391 }
        r2 = r2.toString();	 Catch:{ Throwable -> 0x0391 }
        org.telegram.messenger.FileLog.w(r2);	 Catch:{ Throwable -> 0x0391 }
    L_0x16e1:
        r23 = r20;
        r2 = 0;
        r4 = 0;
    L_0x16e5:
        r21 = 0;
    L_0x16e7:
        if (r4 == 0) goto L_0x1777;
    L_0x16e9:
        r3 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Throwable -> 0x1780 }
        r3.<init>();	 Catch:{ Throwable -> 0x1780 }
        r3.id = r8;	 Catch:{ Throwable -> 0x1780 }
        r3.random_id = r6;	 Catch:{ Throwable -> 0x1780 }
        if (r21 == 0) goto L_0x16f7;
    L_0x16f4:
        r6 = r21;
        goto L_0x16f8;
    L_0x16f7:
        r6 = r4;
    L_0x16f8:
        r3.message = r6;	 Catch:{ Throwable -> 0x1780 }
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6 = r33 / r6;
        r7 = (int) r6;	 Catch:{ Throwable -> 0x1780 }
        r3.date = r7;	 Catch:{ Throwable -> 0x1780 }
        if (r5 == 0) goto L_0x170a;
    L_0x1703:
        r6 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage;	 Catch:{ Throwable -> 0x0391 }
        r6.<init>();	 Catch:{ Throwable -> 0x0391 }
        r3.action = r6;	 Catch:{ Throwable -> 0x0391 }
    L_0x170a:
        if (r1 == 0) goto L_0x1713;
    L_0x170c:
        r1 = r3.flags;	 Catch:{ Throwable -> 0x0391 }
        r6 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r1 = r1 | r6;
        r3.flags = r1;	 Catch:{ Throwable -> 0x0391 }
    L_0x1713:
        r3.dialog_id = r12;	 Catch:{ Throwable -> 0x1780 }
        if (r30 == 0) goto L_0x1725;
    L_0x1717:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel;	 Catch:{ Throwable -> 0x0391 }
        r1.<init>();	 Catch:{ Throwable -> 0x0391 }
        r3.to_id = r1;	 Catch:{ Throwable -> 0x0391 }
        r1 = r3.to_id;	 Catch:{ Throwable -> 0x0391 }
        r8 = r30;
        r1.channel_id = r8;	 Catch:{ Throwable -> 0x0391 }
        goto L_0x1740;
    L_0x1725:
        if (r15 == 0) goto L_0x1733;
    L_0x1727:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ Throwable -> 0x0391 }
        r1.<init>();	 Catch:{ Throwable -> 0x0391 }
        r3.to_id = r1;	 Catch:{ Throwable -> 0x0391 }
        r1 = r3.to_id;	 Catch:{ Throwable -> 0x0391 }
        r1.chat_id = r15;	 Catch:{ Throwable -> 0x0391 }
        goto L_0x1740;
    L_0x1733:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ Throwable -> 0x1780 }
        r1.<init>();	 Catch:{ Throwable -> 0x1780 }
        r3.to_id = r1;	 Catch:{ Throwable -> 0x1780 }
        r1 = r3.to_id;	 Catch:{ Throwable -> 0x1780 }
        r6 = r29;
        r1.user_id = r6;	 Catch:{ Throwable -> 0x1780 }
    L_0x1740:
        r10 = r17;
        r3.from_id = r10;	 Catch:{ Throwable -> 0x1780 }
        if (r25 != 0) goto L_0x174b;
    L_0x1746:
        if (r5 == 0) goto L_0x1749;
    L_0x1748:
        goto L_0x174b;
    L_0x1749:
        r1 = 0;
        goto L_0x174c;
    L_0x174b:
        r1 = 1;
    L_0x174c:
        r3.mentioned = r1;	 Catch:{ Throwable -> 0x1780 }
        r1 = r19;
        r3.silent = r1;	 Catch:{ Throwable -> 0x1780 }
        r1 = new org.telegram.messenger.MessageObject;	 Catch:{ Throwable -> 0x1780 }
        r19 = r1;
        r20 = r28;
        r21 = r3;
        r22 = r4;
        r25 = r2;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26);	 Catch:{ Throwable -> 0x1780 }
        r2 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x1780 }
        r2.<init>();	 Catch:{ Throwable -> 0x1780 }
        r2.add(r1);	 Catch:{ Throwable -> 0x1780 }
        r1 = org.telegram.messenger.NotificationsController.getInstance(r28);	 Catch:{ Throwable -> 0x1780 }
        r3 = r31;
        r4 = r3.countDownLatch;	 Catch:{ Throwable -> 0x1825 }
        r5 = 1;
        r1.processNewMessages(r2, r5, r5, r4);	 Catch:{ Throwable -> 0x1825 }
        goto L_0x1819;
    L_0x1777:
        r3 = r31;
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x1825 }
        r1.countDown();	 Catch:{ Throwable -> 0x1825 }
        goto L_0x1819;
    L_0x1780:
        r0 = move-exception;
        r3 = r31;
        goto L_0x1826;
    L_0x1785:
        r0 = move-exception;
        r3 = r31;
        goto L_0x1790;
    L_0x1789:
        r0 = move-exception;
        r3 = r1;
        goto L_0x1790;
    L_0x178c:
        r0 = move-exception;
        r3 = r1;
        r27 = r8;
    L_0x1790:
        r28 = r15;
        goto L_0x18d4;
    L_0x1794:
        r3 = r1;
        r27 = r8;
        r28 = r15;
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x1825 }
        r1.countDown();	 Catch:{ Throwable -> 0x1825 }
        goto L_0x1819;
    L_0x17a0:
        r8 = r2;
        r27 = r7;
        r28 = r15;
        r15 = r6;
        r6 = r3;
        r3 = r1;
        r1 = "max_id";
        r1 = r11.getInt(r1);	 Catch:{ Throwable -> 0x1825 }
        r2 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x1825 }
        r2.<init>();	 Catch:{ Throwable -> 0x1825 }
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x1825 }
        if (r4 == 0) goto L_0x17d3;
    L_0x17b7:
        r4 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x1825 }
        r4.<init>();	 Catch:{ Throwable -> 0x1825 }
        r5 = "GCM received read notification max_id = ";
        r4.append(r5);	 Catch:{ Throwable -> 0x1825 }
        r4.append(r1);	 Catch:{ Throwable -> 0x1825 }
        r5 = " for dialogId = ";
        r4.append(r5);	 Catch:{ Throwable -> 0x1825 }
        r4.append(r12);	 Catch:{ Throwable -> 0x1825 }
        r4 = r4.toString();	 Catch:{ Throwable -> 0x1825 }
        org.telegram.messenger.FileLog.d(r4);	 Catch:{ Throwable -> 0x1825 }
    L_0x17d3:
        if (r8 == 0) goto L_0x17e2;
    L_0x17d5:
        r4 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox;	 Catch:{ Throwable -> 0x1825 }
        r4.<init>();	 Catch:{ Throwable -> 0x1825 }
        r4.channel_id = r8;	 Catch:{ Throwable -> 0x1825 }
        r4.max_id = r1;	 Catch:{ Throwable -> 0x1825 }
        r2.add(r4);	 Catch:{ Throwable -> 0x1825 }
        goto L_0x1805;
    L_0x17e2:
        r4 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox;	 Catch:{ Throwable -> 0x1825 }
        r4.<init>();	 Catch:{ Throwable -> 0x1825 }
        if (r6 == 0) goto L_0x17f5;
    L_0x17e9:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ Throwable -> 0x1825 }
        r5.<init>();	 Catch:{ Throwable -> 0x1825 }
        r4.peer = r5;	 Catch:{ Throwable -> 0x1825 }
        r5 = r4.peer;	 Catch:{ Throwable -> 0x1825 }
        r5.user_id = r6;	 Catch:{ Throwable -> 0x1825 }
        goto L_0x1800;
    L_0x17f5:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ Throwable -> 0x1825 }
        r5.<init>();	 Catch:{ Throwable -> 0x1825 }
        r4.peer = r5;	 Catch:{ Throwable -> 0x1825 }
        r5 = r4.peer;	 Catch:{ Throwable -> 0x1825 }
        r5.chat_id = r15;	 Catch:{ Throwable -> 0x1825 }
    L_0x1800:
        r4.max_id = r1;	 Catch:{ Throwable -> 0x1825 }
        r2.add(r4);	 Catch:{ Throwable -> 0x1825 }
    L_0x1805:
        r1 = org.telegram.messenger.MessagesController.getInstance(r28);	 Catch:{ Throwable -> 0x1825 }
        r4 = 0;
        r8 = 0;
        r1.processUpdateArray(r2, r8, r8, r4);	 Catch:{ Throwable -> 0x1825 }
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x1825 }
        r1.countDown();	 Catch:{ Throwable -> 0x1825 }
        goto L_0x1819;
    L_0x1814:
        r3 = r1;
        r27 = r7;
        r28 = r15;
    L_0x1819:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r28);	 Catch:{ Throwable -> 0x1825 }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r28);	 Catch:{ Throwable -> 0x1825 }
        r1.resumeNetworkMaybe();	 Catch:{ Throwable -> 0x1825 }
        goto L_0x192d;
    L_0x1825:
        r0 = move-exception;
    L_0x1826:
        r2 = r0;
        r7 = r27;
        r15 = r28;
        goto L_0x18dd;
    L_0x182d:
        r0 = move-exception;
        r3 = r1;
        r27 = r7;
        r28 = r15;
        goto L_0x18dc;
    L_0x1835:
        r3 = r1;
        r27 = r7;
        r28 = r15;
        r1 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ;	 Catch:{ Throwable -> 0x1846 }
        r1.<init>(r15);	 Catch:{ Throwable -> 0x1843 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);	 Catch:{ Throwable -> 0x18d3 }
        return;
    L_0x1843:
        r0 = move-exception;
        goto L_0x18d4;
    L_0x1846:
        r0 = move-exception;
        r15 = r28;
        goto L_0x18d4;
    L_0x184b:
        r3 = r1;
        r27 = r7;
        r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification;	 Catch:{ Throwable -> 0x18d3 }
        r1.<init>();	 Catch:{ Throwable -> 0x18d3 }
        r2 = 0;
        r1.popup = r2;	 Catch:{ Throwable -> 0x18d3 }
        r2 = 2;
        r1.flags = r2;	 Catch:{ Throwable -> 0x18d3 }
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6 = r33 / r6;
        r2 = (int) r6;	 Catch:{ Throwable -> 0x18d3 }
        r1.inbox_date = r2;	 Catch:{ Throwable -> 0x18d3 }
        r2 = "message";
        r2 = r5.getString(r2);	 Catch:{ Throwable -> 0x18d3 }
        r1.message = r2;	 Catch:{ Throwable -> 0x18d3 }
        r2 = "announcement";
        r1.type = r2;	 Catch:{ Throwable -> 0x18d3 }
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;	 Catch:{ Throwable -> 0x18d3 }
        r2.<init>();	 Catch:{ Throwable -> 0x18d3 }
        r1.media = r2;	 Catch:{ Throwable -> 0x18d3 }
        r2 = new org.telegram.tgnet.TLRPC$TL_updates;	 Catch:{ Throwable -> 0x18d3 }
        r2.<init>();	 Catch:{ Throwable -> 0x18d3 }
        r4 = r2.updates;	 Catch:{ Throwable -> 0x18d3 }
        r4.add(r1);	 Catch:{ Throwable -> 0x18d3 }
        r1 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ Throwable -> 0x18d3 }
        r4 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo;	 Catch:{ Throwable -> 0x1894 }
        r4.<init>(r15, r2);	 Catch:{ Throwable -> 0x1894 }
        r1.postRunnable(r4);	 Catch:{ Throwable -> 0x18d3 }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ Throwable -> 0x18d3 }
        r1.resumeNetworkMaybe();	 Catch:{ Throwable -> 0x18d3 }
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x18d3 }
        r1.countDown();	 Catch:{ Throwable -> 0x18d3 }
        return;
    L_0x1894:
        r0 = move-exception;
        goto L_0x18d4;
    L_0x1896:
        r3 = r1;
        r27 = r7;
        r1 = "dc";
        r1 = r11.getInt(r1);	 Catch:{ Throwable -> 0x18d3 }
        r2 = "addr";
        r2 = r11.getString(r2);	 Catch:{ Throwable -> 0x18d3 }
        r4 = ":";
        r2 = r2.split(r4);	 Catch:{ Throwable -> 0x18d3 }
        r4 = r2.length;	 Catch:{ Throwable -> 0x18d3 }
        r5 = 2;
        if (r4 == r5) goto L_0x18b5;
    L_0x18af:
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x18d3 }
        r1.countDown();	 Catch:{ Throwable -> 0x18d3 }
        return;
    L_0x18b5:
        r4 = 0;
        r4 = r2[r4];	 Catch:{ Throwable -> 0x18d3 }
        r5 = 1;
        r2 = r2[r5];	 Catch:{ Throwable -> 0x18d3 }
        r2 = java.lang.Integer.parseInt(r2);	 Catch:{ Throwable -> 0x18d3 }
        r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ Throwable -> 0x18d3 }
        r5.applyDatacenterAddress(r1, r4, r2);	 Catch:{ Throwable -> 0x18d3 }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ Throwable -> 0x18d3 }
        r1.resumeNetworkMaybe();	 Catch:{ Throwable -> 0x18d3 }
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x18d3 }
        r1.countDown();	 Catch:{ Throwable -> 0x18d3 }
        return;
    L_0x18d3:
        r0 = move-exception;
    L_0x18d4:
        r2 = r0;
    L_0x18d5:
        r7 = r27;
        goto L_0x18dd;
    L_0x18d8:
        r0 = move-exception;
        r3 = r1;
        r27 = r7;
    L_0x18dc:
        r2 = r0;
    L_0x18dd:
        r1 = -1;
        goto L_0x18f5;
    L_0x18df:
        r0 = move-exception;
        r3 = r1;
        r27 = r7;
        r2 = r0;
        goto L_0x18f3;
    L_0x18e5:
        r0 = move-exception;
        r3 = r1;
        r27 = r7;
        r8 = 0;
        r2 = r0;
        r9 = r8;
        goto L_0x18f3;
    L_0x18ed:
        r0 = move-exception;
        r3 = r1;
        r8 = 0;
        r2 = r0;
        r7 = r8;
        r9 = r7;
    L_0x18f3:
        r1 = -1;
    L_0x18f4:
        r15 = -1;
    L_0x18f5:
        if (r15 == r1) goto L_0x1907;
    L_0x18f7:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15);
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);
        r1.resumeNetworkMaybe();
        r1 = r3.countDownLatch;
        r1.countDown();
        goto L_0x190a;
    L_0x1907:
        r31.onDecryptError();
    L_0x190a:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x192a;
    L_0x190e:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r4 = "error in loc_key = ";
        r1.append(r4);
        r1.append(r9);
        r4 = " json ";
        r1.append(r4);
        r1.append(r7);
        r1 = r1.toString();
        org.telegram.messenger.FileLog.e(r1);
    L_0x192a:
        org.telegram.messenger.FileLog.e(r2);
    L_0x192d:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.GcmPushListenerService.lambda$null$3$GcmPushListenerService(java.util.Map, long):void");
    }

    static /* synthetic */ void lambda$null$1(int i) {
        if (UserConfig.getInstance(i).getClientUserId() != 0) {
            UserConfig.getInstance(i).clearConfig();
            MessagesController.getInstance(i).performLogout(0);
        }
    }

    static /* synthetic */ void lambda$null$2(int i, long j, ArrayList arrayList, int i2) {
        MessagesStorage.getInstance(i).deletePushMessages(j, arrayList);
        MessagesStorage.getInstance(i).updateDialogsWithDeletedMessages(arrayList, MessagesStorage.getInstance(i).markMessagesAsDeleted(arrayList, false, i2), false, i2);
    }

    private void onDecryptError() {
        for (int i = 0; i < 3; i++) {
            if (UserConfig.getInstance(i).isClientActivated()) {
                ConnectionsManager.onInternalPushReceived(i);
                ConnectionsManager.getInstance(i).resumeNetworkMaybe();
            }
        }
        this.countDownLatch.countDown();
    }

    public void onNewToken(String str) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$GcmPushListenerService$yAX-L4AmtPgnfoqai_KIyMdrl9c(str));
    }

    static /* synthetic */ void lambda$onNewToken$5(String str) {
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Refreshed token: ");
            stringBuilder.append(str);
            FileLog.d(stringBuilder.toString());
        }
        ApplicationLoader.postInitApplication();
        sendRegistrationToServer(str);
    }

    public static void sendRegistrationToServer(String str) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$GcmPushListenerService$oMpUElUKeVcspCfJjKdmR5Ds1PU(str));
    }

    static /* synthetic */ void lambda$sendRegistrationToServer$7(String str) {
        SharedConfig.pushString = str;
        for (int i = 0; i < 3; i++) {
            UserConfig instance = UserConfig.getInstance(i);
            instance.registeredForPush = false;
            instance.saveConfig(false);
            if (instance.getClientUserId() != 0) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$GcmPushListenerService$YWXR3JZImuShQ4_sLgJ0wneqQVQ(i, str));
            }
        }
    }
}
