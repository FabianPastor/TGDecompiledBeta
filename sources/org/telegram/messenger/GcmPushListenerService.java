package org.telegram.messenger;

import android.os.SystemClock;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
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
        AndroidUtilities.runOnUIThread(new -$$Lambda$GcmPushListenerService$kOADpO1n0oEPuZdWbViq4zvdHPc(this, data, sentTime));
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

    public /* synthetic */ void lambda$onMessageReceived$3$GcmPushListenerService(Map map, long j) {
        ApplicationLoader.postInitApplication();
        Utilities.stageQueue.postRunnable(new -$$Lambda$GcmPushListenerService$Rx2_f5qM692fiJk_z_Z7jm3r1zc(this, map, j));
    }

    /* JADX WARNING: Removed duplicated region for block: B:274:0x0413  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x03ec A:{SYNTHETIC, Splitter:B:272:0x03ec} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x088e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0883 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:561:0x0878 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x086d A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:555:0x0862 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x0857 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:549:0x084c A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:546:0x0841 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:543:0x0836 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:540:0x082a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:537:0x081e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0812 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:531:0x0807 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x07fc A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x07f1 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x07e5 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x07d9 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x07cd A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x07c1 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x07b5 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x07a9 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:504:0x079d A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x0791 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x0786 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x077a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x076e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x0762 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0756 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x074a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x073e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x0732 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x0726 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x071a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x070e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x0702 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x06f6 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x06ea A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x06de A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x06d2 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x06c6 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x06ba A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x06ae A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x06a3 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0697 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x068b A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x067f A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0673 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0667 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x065b A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x064f A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0643 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0637 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x062b A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x061f A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0613 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0608 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x05fc A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x05f0 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x05e4 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x05d8 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x05cc A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x05c0 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x05b4 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x05a8 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x059c A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x0590 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0584 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x0578 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x056c A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x0560 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:357:0x0555 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0549 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x053d A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x0531 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x0525 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x0519 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x050d A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x0501 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x04f5 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x04e9 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x04de A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x04d2 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x04c6 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x04ba A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x04ae A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x04a2 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x0496 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x048a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x047e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0472 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0466 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x045a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x044e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x0442 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x0436 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x042a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x041e A:{SYNTHETIC, Splitter:B:278:0x041e} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x039f  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x0380 A:{SYNTHETIC, Splitter:B:240:0x0380} */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x03c5 A:{SYNTHETIC, Splitter:B:258:0x03c5} */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x03b5  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x03ec A:{SYNTHETIC, Splitter:B:272:0x03ec} */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0413  */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x088e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0883 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:561:0x0878 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x086d A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:555:0x0862 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x0857 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:549:0x084c A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:546:0x0841 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:543:0x0836 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:540:0x082a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:537:0x081e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0812 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:531:0x0807 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x07fc A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x07f1 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x07e5 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x07d9 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x07cd A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x07c1 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x07b5 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x07a9 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:504:0x079d A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x0791 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x0786 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x077a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x076e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x0762 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0756 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x074a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x073e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x0732 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x0726 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x071a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x070e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x0702 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x06f6 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x06ea A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x06de A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x06d2 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x06c6 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x06ba A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x06ae A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x06a3 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0697 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x068b A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x067f A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0673 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0667 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x065b A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x064f A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0643 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0637 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x062b A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x061f A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0613 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0608 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x05fc A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x05f0 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x05e4 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x05d8 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x05cc A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x05c0 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x05b4 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x05a8 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x059c A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x0590 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0584 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x0578 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x056c A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x0560 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:357:0x0555 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0549 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x053d A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x0531 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x0525 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x0519 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x050d A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x0501 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x04f5 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x04e9 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x04de A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x04d2 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x04c6 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x04ba A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x04ae A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x04a2 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x0496 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x048a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x047e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0472 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0466 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x045a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x044e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x0442 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x0436 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x042a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x041e A:{SYNTHETIC, Splitter:B:278:0x041e} */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0363 A:{SYNTHETIC, Splitter:B:230:0x0363} */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x0380 A:{SYNTHETIC, Splitter:B:240:0x0380} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x039f  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x03b5  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x03c5 A:{SYNTHETIC, Splitter:B:258:0x03c5} */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0413  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x03ec A:{SYNTHETIC, Splitter:B:272:0x03ec} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x088e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0883 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:561:0x0878 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x086d A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:555:0x0862 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x0857 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:549:0x084c A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:546:0x0841 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:543:0x0836 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:540:0x082a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:537:0x081e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0812 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:531:0x0807 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x07fc A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x07f1 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x07e5 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x07d9 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x07cd A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x07c1 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x07b5 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x07a9 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:504:0x079d A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x0791 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x0786 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x077a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x076e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x0762 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0756 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x074a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x073e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x0732 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x0726 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x071a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x070e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x0702 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x06f6 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x06ea A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x06de A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x06d2 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x06c6 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x06ba A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x06ae A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x06a3 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0697 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x068b A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x067f A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0673 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0667 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x065b A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x064f A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0643 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0637 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x062b A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x061f A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0613 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0608 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x05fc A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x05f0 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x05e4 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x05d8 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x05cc A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x05c0 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x05b4 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x05a8 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x059c A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x0590 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0584 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x0578 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x056c A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x0560 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:357:0x0555 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0549 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x053d A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x0531 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x0525 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x0519 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x050d A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x0501 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x04f5 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x04e9 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x04de A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x04d2 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x04c6 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x04ba A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x04ae A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x04a2 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x0496 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x048a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x047e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0472 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0466 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x045a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x044e A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x0442 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x0436 A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x042a A:{Catch:{ Throwable -> 0x036e }} */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x041e A:{SYNTHETIC, Splitter:B:278:0x041e} */
    /* JADX WARNING: Removed duplicated region for block: B:811:0x1710 A:{Catch:{ Throwable -> 0x16bd, Throwable -> 0x174d }} */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x01e2  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x01e2  */
    /* JADX WARNING: Removed duplicated region for block: B:811:0x1710 A:{Catch:{ Throwable -> 0x16bd, Throwable -> 0x174d }} */
    /* JADX WARNING: Removed duplicated region for block: B:811:0x1710 A:{Catch:{ Throwable -> 0x16bd, Throwable -> 0x174d }} */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x01e2  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1773  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x178a  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1773  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x178a  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1773  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x178a  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1773  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x178a  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1773  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x178a  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1773  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x178a  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1773  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x178a  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1773  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x178a  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1773  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x178a  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1773  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x178a  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1773  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x178a  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1773  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x178a  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1773  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x178a  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1773  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x178a  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1773  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x178a  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1773  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x178a  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1773  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x178a  */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:571:0x089a, code skipped:
            r11 = "Photos";
            r18 = r3;
            r3 = "ChannelMessageFew";
            r31 = r2;
            r2 = " ";
            r32 = r12;
            r13 = "AttachSticker";
     */
    /* JADX WARNING: Missing block: B:572:0x08ab, code skipped:
            switch(r5) {
                case 0: goto L_0x1523;
                case 1: goto L_0x1505;
                case 2: goto L_0x14ea;
                case 3: goto L_0x14cf;
                case 4: goto L_0x14b4;
                case 5: goto L_0x1499;
                case 6: goto L_0x1485;
                case 7: goto L_0x1469;
                case 8: goto L_0x144d;
                case 9: goto L_0x13fa;
                case 10: goto L_0x13de;
                case 11: goto L_0x13bd;
                case 12: goto L_0x139c;
                case 13: goto L_0x1380;
                case 14: goto L_0x1364;
                case 15: goto L_0x1348;
                case 16: goto L_0x1327;
                case 17: goto L_0x130a;
                case 18: goto L_0x12e9;
                case 19: goto L_0x12c5;
                case 20: goto L_0x12a1;
                case 21: goto L_0x127b;
                case 22: goto L_0x1268;
                case 23: goto L_0x124e;
                case 24: goto L_0x1232;
                case 25: goto L_0x1216;
                case 26: goto L_0x11fa;
                case 27: goto L_0x11de;
                case 28: goto L_0x11c2;
                case 29: goto L_0x116f;
                case 30: goto L_0x1153;
                case 31: goto L_0x1132;
                case 32: goto L_0x1111;
                case 33: goto L_0x10f5;
                case 34: goto L_0x10d9;
                case 35: goto L_0x10bd;
                case 36: goto L_0x10a1;
                case 37: goto L_0x1084;
                case 38: goto L_0x105c;
                case 39: goto L_0x103a;
                case 40: goto L_0x1016;
                case 41: goto L_0x1003;
                case 42: goto L_0x0fe4;
                case 43: goto L_0x0fc3;
                case 44: goto L_0x0fa2;
                case 45: goto L_0x0var_;
                case 46: goto L_0x0var_;
                case 47: goto L_0x0f3f;
                case 48: goto L_0x0ecc;
                case 49: goto L_0x0eab;
                case 50: goto L_0x0e85;
                case 51: goto L_0x0e5f;
                case 52: goto L_0x0e3e;
                case 53: goto L_0x0e1d;
                case 54: goto L_0x0dfc;
                case 55: goto L_0x0dd6;
                case 56: goto L_0x0db4;
                case 57: goto L_0x0d8e;
                case 58: goto L_0x0d76;
                case 59: goto L_0x0d5e;
                case 60: goto L_0x0d46;
                case 61: goto L_0x0d29;
                case 62: goto L_0x0d11;
                case 63: goto L_0x0cf9;
                case 64: goto L_0x0ce1;
                case 65: goto L_0x0cc9;
                case 66: goto L_0x0cb1;
                case 67: goto L_0x0CLASSNAME;
                case 68: goto L_0x0CLASSNAME;
                case 69: goto L_0x0CLASSNAME;
                case 70: goto L_0x0c1c;
                case 71: goto L_0x0CLASSNAME;
                case 72: goto L_0x0bcd;
                case 73: goto L_0x0ba0;
                case 74: goto L_0x0b73;
                case 75: goto L_0x0b46;
                case 76: goto L_0x0b19;
                case 77: goto L_0x0aec;
                case 78: goto L_0x0a72;
                case 79: goto L_0x0a45;
                case 80: goto L_0x0a0e;
                case 81: goto L_0x09d7;
                case 82: goto L_0x09aa;
                case 83: goto L_0x097d;
                case 84: goto L_0x0950;
                case 85: goto L_0x0923;
                case 86: goto L_0x08f6;
                case 87: goto L_0x08c9;
                case 88: goto L_0x1552;
                case 89: goto L_0x1552;
                case 90: goto L_0x1552;
                case 91: goto L_0x08b2;
                case 92: goto L_0x1552;
                case 93: goto L_0x1552;
                case 94: goto L_0x1552;
                case 95: goto L_0x1552;
                case 96: goto L_0x1552;
                default: goto L_0x08ae;
            };
     */
    /* JADX WARNING: Missing block: B:576:?, code skipped:
            r2 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
            r25 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
     */
    /* JADX WARNING: Missing block: B:577:0x08c6, code skipped:
            r3 = true;
     */
    /* JADX WARNING: Missing block: B:578:0x08c9, code skipped:
            if (r14 == 0) goto L_0x08e3;
     */
    /* JADX WARNING: Missing block: B:579:0x08cb, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:580:0x08e3, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:581:0x08f6, code skipped:
            if (r14 == 0) goto L_0x0910;
     */
    /* JADX WARNING: Missing block: B:582:0x08f8, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:583:0x0910, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:584:0x0923, code skipped:
            if (r14 == 0) goto L_0x093d;
     */
    /* JADX WARNING: Missing block: B:585:0x0925, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:586:0x093d, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:587:0x0950, code skipped:
            if (r14 == 0) goto L_0x096a;
     */
    /* JADX WARNING: Missing block: B:588:0x0952, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:589:0x096a, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:590:0x097d, code skipped:
            if (r14 == 0) goto L_0x0997;
     */
    /* JADX WARNING: Missing block: B:591:0x097f, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:592:0x0997, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:593:0x09aa, code skipped:
            if (r14 == 0) goto L_0x09c4;
     */
    /* JADX WARNING: Missing block: B:594:0x09ac, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:595:0x09c4, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:596:0x09d7, code skipped:
            if (r14 == 0) goto L_0x09f6;
     */
    /* JADX WARNING: Missing block: B:597:0x09d9, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:598:0x09f6, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:599:0x0a0e, code skipped:
            if (r14 == 0) goto L_0x0a2d;
     */
    /* JADX WARNING: Missing block: B:600:0x0a10, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:601:0x0a2d, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:602:0x0a45, code skipped:
            if (r14 == 0) goto L_0x0a5f;
     */
    /* JADX WARNING: Missing block: B:603:0x0a47, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:604:0x0a5f, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:605:0x0a72, code skipped:
            if (r14 == 0) goto L_0x0ab5;
     */
    /* JADX WARNING: Missing block: B:607:0x0a76, code skipped:
            if (r15.length <= 2) goto L_0x0a9d;
     */
    /* JADX WARNING: Missing block: B:609:0x0a7e, code skipped:
            if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x0a9d;
     */
    /* JADX WARNING: Missing block: B:610:0x0a80, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:611:0x0a9d, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:613:0x0ab7, code skipped:
            if (r15.length <= 1) goto L_0x0ad9;
     */
    /* JADX WARNING: Missing block: B:615:0x0abf, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x0ad9;
     */
    /* JADX WARNING: Missing block: B:616:0x0ac1, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:617:0x0ad9, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:618:0x0aec, code skipped:
            if (r14 == 0) goto L_0x0b06;
     */
    /* JADX WARNING: Missing block: B:619:0x0aee, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:620:0x0b06, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:621:0x0b19, code skipped:
            if (r14 == 0) goto L_0x0b33;
     */
    /* JADX WARNING: Missing block: B:622:0x0b1b, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:623:0x0b33, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:624:0x0b46, code skipped:
            if (r14 == 0) goto L_0x0b60;
     */
    /* JADX WARNING: Missing block: B:625:0x0b48, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:626:0x0b60, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:627:0x0b73, code skipped:
            if (r14 == 0) goto L_0x0b8d;
     */
    /* JADX WARNING: Missing block: B:628:0x0b75, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:629:0x0b8d, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:630:0x0ba0, code skipped:
            if (r14 == 0) goto L_0x0bba;
     */
    /* JADX WARNING: Missing block: B:631:0x0ba2, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:632:0x0bba, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:633:0x0bcd, code skipped:
            if (r14 == 0) goto L_0x0bec;
     */
    /* JADX WARNING: Missing block: B:634:0x0bcf, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:635:0x0bec, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:636:0x0CLASSNAME, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:637:0x0c1c, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:638:0x0CLASSNAME, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString(r11, org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:639:0x0CLASSNAME, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString(r10, org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:640:0x0CLASSNAME, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:641:0x0cb1, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:642:0x0cc9, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:643:0x0ce1, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:644:0x0cf9, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:645:0x0d11, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:646:0x0d29, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:647:0x0d46, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:648:0x0d5e, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:649:0x0d76, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:650:0x0d8e, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r15[0], r15[1], r15[2]);
            r3 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Missing block: B:651:0x0db4, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r15[0], r15[1], r15[2], r15[3]);
     */
    /* JADX WARNING: Missing block: B:652:0x0dd6, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r15[0], r15[1], r15[2]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:653:0x0dfc, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:654:0x0e1d, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:655:0x0e3e, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:656:0x0e5f, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r15[0], r15[1], r15[2]);
            r3 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:657:0x0e85, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r15[0], r15[1], r15[2]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:658:0x0eab, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:660:0x0ece, code skipped:
            if (r15.length <= 2) goto L_0x0f0c;
     */
    /* JADX WARNING: Missing block: B:662:0x0ed6, code skipped:
            if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x0f0c;
     */
    /* JADX WARNING: Missing block: B:663:0x0ed8, code skipped:
            r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r15[0], r15[1], r15[2]);
            r5 = new java.lang.StringBuilder();
            r5.append(r15[2]);
            r5.append(r2);
            r5.append(org.telegram.messenger.LocaleController.getString(r13, NUM));
            r2 = r5.toString();
     */
    /* JADX WARNING: Missing block: B:664:0x0f0c, code skipped:
            r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r15[0], r15[1]);
            r5 = new java.lang.StringBuilder();
            r5.append(r15[1]);
            r5.append(r2);
            r5.append(org.telegram.messenger.LocaleController.getString(r13, NUM));
            r2 = r5.toString();
     */
    /* JADX WARNING: Missing block: B:665:0x0f3a, code skipped:
            r16 = r2;
            r2 = r3;
     */
    /* JADX WARNING: Missing block: B:666:0x0f3f, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:667:0x0var_, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:668:0x0var_, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:669:0x0fa2, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:670:0x0fc3, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:671:0x0fe4, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r15[0], r15[1], r15[2]);
            r3 = r15[2];
     */
    /* JADX WARNING: Missing block: B:672:0x1003, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:673:0x1016, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString(r3, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:674:0x103a, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString(r3, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString(r11, org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:675:0x105c, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString(r3, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Missing block: B:676:0x1084, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:677:0x10a1, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:678:0x10bd, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:679:0x10d9, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:680:0x10f5, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:681:0x1111, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:682:0x1132, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:683:0x1153, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:685:0x1171, code skipped:
            if (r15.length <= 1) goto L_0x11ab;
     */
    /* JADX WARNING: Missing block: B:687:0x1179, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x11ab;
     */
    /* JADX WARNING: Missing block: B:688:0x117b, code skipped:
            r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r15[0], r15[1]);
            r5 = new java.lang.StringBuilder();
            r5.append(r15[1]);
            r5.append(r2);
            r5.append(org.telegram.messenger.LocaleController.getString(r13, NUM));
            r2 = r5.toString();
     */
    /* JADX WARNING: Missing block: B:689:0x11ab, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString(r13, NUM);
     */
    /* JADX WARNING: Missing block: B:690:0x11c2, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:691:0x11de, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:692:0x11fa, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:693:0x1216, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:694:0x1232, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:695:0x124e, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r15[0], r15[1]);
            r3 = r15[1];
     */
    /* JADX WARNING: Missing block: B:696:0x1268, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:697:0x127b, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:698:0x12a1, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString(r11, org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:699:0x12c5, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString(r10, org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:700:0x12e9, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Missing block: B:701:0x130a, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:702:0x1327, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:703:0x1348, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:704:0x1364, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:705:0x1380, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:706:0x139c, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:707:0x13bd, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:708:0x13de, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:710:0x13fc, code skipped:
            if (r15.length <= 1) goto L_0x1436;
     */
    /* JADX WARNING: Missing block: B:712:0x1404, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x1436;
     */
    /* JADX WARNING: Missing block: B:713:0x1406, code skipped:
            r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r15[0], r15[1]);
            r5 = new java.lang.StringBuilder();
            r5.append(r15[1]);
            r5.append(r2);
            r5.append(org.telegram.messenger.LocaleController.getString(r13, NUM));
            r2 = r5.toString();
     */
    /* JADX WARNING: Missing block: B:714:0x1436, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString(r13, NUM);
     */
    /* JADX WARNING: Missing block: B:715:0x144d, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:716:0x1469, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:717:0x1485, code skipped:
            r2 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r15[0]);
     */
    /* JADX WARNING: Missing block: B:718:0x1499, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:719:0x14b4, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:720:0x14cf, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:721:0x14ea, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:722:0x1505, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:723:0x151f, code skipped:
            r16 = r3;
     */
    /* JADX WARNING: Missing block: B:724:0x1521, code skipped:
            r3 = false;
     */
    /* JADX WARNING: Missing block: B:725:0x1523, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r15[0], r15[1]);
            r3 = r15[1];
     */
    /* JADX WARNING: Missing block: B:726:0x153c, code skipped:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1552;
     */
    /* JADX WARNING: Missing block: B:727:0x153e, code skipped:
            r2 = new java.lang.StringBuilder();
            r2.append("unhandled loc_key = ");
            r2.append(r9);
            org.telegram.messenger.FileLog.w(r2.toString());
     */
    /* JADX WARNING: Missing block: B:728:0x1552, code skipped:
            r2 = null;
     */
    /* JADX WARNING: Missing block: B:729:0x1553, code skipped:
            r3 = false;
     */
    /* JADX WARNING: Missing block: B:730:0x1554, code skipped:
            r16 = null;
     */
    /* JADX WARNING: Missing block: B:731:0x1556, code skipped:
            if (r2 == null) goto L_0x15f0;
     */
    /* JADX WARNING: Missing block: B:733:?, code skipped:
            r5 = new org.telegram.tgnet.TLRPC.TL_message();
            r5.id = r4;
            r5.random_id = r6;
     */
    /* JADX WARNING: Missing block: B:734:0x1561, code skipped:
            if (r16 == null) goto L_0x1566;
     */
    /* JADX WARNING: Missing block: B:735:0x1563, code skipped:
            r4 = r16;
     */
    /* JADX WARNING: Missing block: B:736:0x1566, code skipped:
            r4 = r2;
     */
    /* JADX WARNING: Missing block: B:737:0x1567, code skipped:
            r5.message = r4;
            r5.date = (int) (r38 / 1000);
     */
    /* JADX WARNING: Missing block: B:738:0x1570, code skipped:
            if (r1 == null) goto L_0x1579;
     */
    /* JADX WARNING: Missing block: B:740:?, code skipped:
            r5.action = new org.telegram.tgnet.TLRPC.TL_messageActionPinMessage();
     */
    /* JADX WARNING: Missing block: B:741:0x1579, code skipped:
            if (r8 == null) goto L_0x1582;
     */
    /* JADX WARNING: Missing block: B:742:0x157b, code skipped:
            r5.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Missing block: B:745:?, code skipped:
            r5.dialog_id = r32;
     */
    /* JADX WARNING: Missing block: B:746:0x1586, code skipped:
            if (r31 == 0) goto L_0x1596;
     */
    /* JADX WARNING: Missing block: B:748:?, code skipped:
            r5.to_id = new org.telegram.tgnet.TLRPC.TL_peerChannel();
            r5.to_id.channel_id = r31;
     */
    /* JADX WARNING: Missing block: B:749:0x1596, code skipped:
            if (r18 == 0) goto L_0x15a6;
     */
    /* JADX WARNING: Missing block: B:750:0x1598, code skipped:
            r5.to_id = new org.telegram.tgnet.TLRPC.TL_peerChat();
            r5.to_id.chat_id = r18;
     */
    /* JADX WARNING: Missing block: B:752:?, code skipped:
            r5.to_id = new org.telegram.tgnet.TLRPC.TL_peerUser();
            r5.to_id.user_id = r23;
     */
    /* JADX WARNING: Missing block: B:753:0x15b3, code skipped:
            r5.flags |= 256;
            r5.from_id = r14;
     */
    /* JADX WARNING: Missing block: B:754:0x15bb, code skipped:
            if (r20 != null) goto L_0x15c2;
     */
    /* JADX WARNING: Missing block: B:755:0x15bd, code skipped:
            if (r1 == null) goto L_0x15c0;
     */
    /* JADX WARNING: Missing block: B:757:0x15c0, code skipped:
            r1 = false;
     */
    /* JADX WARNING: Missing block: B:758:0x15c2, code skipped:
            r1 = true;
     */
    /* JADX WARNING: Missing block: B:759:0x15c3, code skipped:
            r5.mentioned = r1;
            r5.silent = r19;
            r19 = new org.telegram.messenger.MessageObject(r28, r5, r2, r25, r24, r3, r26, r27);
            r2 = new java.util.ArrayList();
            r2.add(r19);
     */
    /* JADX WARNING: Missing block: B:760:0x15e6, code skipped:
            r3 = r36;
     */
    /* JADX WARNING: Missing block: B:762:?, code skipped:
            org.telegram.messenger.NotificationsController.getInstance(r28).processNewMessages(r2, true, true, r3.countDownLatch);
     */
    /* JADX WARNING: Missing block: B:763:0x15f0, code skipped:
            r36.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:766:0x15fe, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:767:0x15ff, code skipped:
            r3 = r36;
     */
    /* JADX WARNING: Missing block: B:831:0x1773, code skipped:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15);
            org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
            r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:832:0x1783, code skipped:
            onDecryptError();
     */
    /* JADX WARNING: Missing block: B:835:0x178a, code skipped:
            r2 = new java.lang.StringBuilder();
            r2.append("error in loc_key = ");
            r2.append(r9);
            r2.append(" json ");
            r2.append(r4);
            org.telegram.messenger.FileLog.e(r2.toString());
     */
    public /* synthetic */ void lambda$null$2$GcmPushListenerService(java.util.Map r37, long r38) {
        /*
        r36 = this;
        r1 = r36;
        r2 = r37;
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x000d;
    L_0x0008:
        r3 = "GCM START PROCESSING";
        org.telegram.messenger.FileLog.d(r3);
    L_0x000d:
        r5 = "p";
        r5 = r2.get(r5);	 Catch:{ Throwable -> 0x176a }
        r6 = r5 instanceof java.lang.String;	 Catch:{ Throwable -> 0x176a }
        if (r6 != 0) goto L_0x002d;
    L_0x0017:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x0024 }
        if (r2 == 0) goto L_0x0020;
    L_0x001b:
        r2 = "GCM DECRYPT ERROR 1";
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Throwable -> 0x0024 }
    L_0x0020:
        r36.onDecryptError();	 Catch:{ Throwable -> 0x0024 }
        return;
    L_0x0024:
        r0 = move-exception;
        r3 = r1;
        r2 = -1;
        r4 = 0;
    L_0x0028:
        r9 = 0;
    L_0x0029:
        r15 = -1;
    L_0x002a:
        r1 = r0;
        goto L_0x1771;
    L_0x002d:
        r5 = (java.lang.String) r5;	 Catch:{ Throwable -> 0x176a }
        r6 = 8;
        r5 = android.util.Base64.decode(r5, r6);	 Catch:{ Throwable -> 0x176a }
        r7 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Throwable -> 0x176a }
        r8 = r5.length;	 Catch:{ Throwable -> 0x176a }
        r7.<init>(r8);	 Catch:{ Throwable -> 0x176a }
        r7.writeBytes(r5);	 Catch:{ Throwable -> 0x176a }
        r8 = 0;
        r7.position(r8);	 Catch:{ Throwable -> 0x176a }
        r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ Throwable -> 0x176a }
        if (r9 != 0) goto L_0x0057;
    L_0x0046:
        r9 = new byte[r6];	 Catch:{ Throwable -> 0x0024 }
        org.telegram.messenger.SharedConfig.pushAuthKeyId = r9;	 Catch:{ Throwable -> 0x0024 }
        r9 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ Throwable -> 0x0024 }
        r9 = org.telegram.messenger.Utilities.computeSHA1(r9);	 Catch:{ Throwable -> 0x0024 }
        r10 = r9.length;	 Catch:{ Throwable -> 0x0024 }
        r10 = r10 - r6;
        r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ Throwable -> 0x0024 }
        java.lang.System.arraycopy(r9, r10, r11, r8, r6);	 Catch:{ Throwable -> 0x0024 }
    L_0x0057:
        r9 = new byte[r6];	 Catch:{ Throwable -> 0x176a }
        r10 = 1;
        r7.readBytes(r9, r10);	 Catch:{ Throwable -> 0x176a }
        r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ Throwable -> 0x176a }
        r11 = java.util.Arrays.equals(r11, r9);	 Catch:{ Throwable -> 0x176a }
        r12 = 3;
        r13 = 2;
        if (r11 != 0) goto L_0x0092;
    L_0x0067:
        r36.onDecryptError();	 Catch:{ Throwable -> 0x0024 }
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x0024 }
        if (r2 == 0) goto L_0x0091;
    L_0x006e:
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
    L_0x0091:
        return;
    L_0x0092:
        r9 = 16;
        r9 = new byte[r9];	 Catch:{ Throwable -> 0x176a }
        r7.readBytes(r9, r10);	 Catch:{ Throwable -> 0x176a }
        r11 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ Throwable -> 0x176a }
        r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13);	 Catch:{ Throwable -> 0x176a }
        r14 = r7.buffer;	 Catch:{ Throwable -> 0x176a }
        r15 = r11.aesKey;	 Catch:{ Throwable -> 0x176a }
        r11 = r11.aesIv;	 Catch:{ Throwable -> 0x176a }
        r17 = 0;
        r18 = 0;
        r19 = 24;
        r5 = r5.length;	 Catch:{ Throwable -> 0x176a }
        r20 = r5 + -24;
        r16 = r11;
        org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20);	 Catch:{ Throwable -> 0x176a }
        r21 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ Throwable -> 0x176a }
        r22 = 96;
        r23 = 32;
        r5 = r7.buffer;	 Catch:{ Throwable -> 0x176a }
        r25 = 24;
        r11 = r7.buffer;	 Catch:{ Throwable -> 0x176a }
        r26 = r11.limit();	 Catch:{ Throwable -> 0x176a }
        r24 = r5;
        r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26);	 Catch:{ Throwable -> 0x176a }
        r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6);	 Catch:{ Throwable -> 0x176a }
        if (r5 != 0) goto L_0x00ea;
    L_0x00cf:
        r36.onDecryptError();	 Catch:{ Throwable -> 0x0024 }
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x0024 }
        if (r2 == 0) goto L_0x00e9;
    L_0x00d6:
        r2 = "GCM DECRYPT ERROR 3, key = %s";
        r5 = new java.lang.Object[r10];	 Catch:{ Throwable -> 0x0024 }
        r6 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ Throwable -> 0x0024 }
        r6 = org.telegram.messenger.Utilities.bytesToHex(r6);	 Catch:{ Throwable -> 0x0024 }
        r5[r8] = r6;	 Catch:{ Throwable -> 0x0024 }
        r2 = java.lang.String.format(r2, r5);	 Catch:{ Throwable -> 0x0024 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Throwable -> 0x0024 }
    L_0x00e9:
        return;
    L_0x00ea:
        r5 = r7.readInt32(r10);	 Catch:{ Throwable -> 0x176a }
        r5 = new byte[r5];	 Catch:{ Throwable -> 0x176a }
        r7.readBytes(r5, r10);	 Catch:{ Throwable -> 0x176a }
        r7 = new java.lang.String;	 Catch:{ Throwable -> 0x176a }
        r7.<init>(r5);	 Catch:{ Throwable -> 0x176a }
        r5 = new org.json.JSONObject;	 Catch:{ Throwable -> 0x1761 }
        r5.<init>(r7);	 Catch:{ Throwable -> 0x1761 }
        r9 = "loc_key";
        r9 = r5.has(r9);	 Catch:{ Throwable -> 0x1761 }
        if (r9 == 0) goto L_0x0112;
    L_0x0105:
        r9 = "loc_key";
        r9 = r5.getString(r9);	 Catch:{ Throwable -> 0x010c }
        goto L_0x0114;
    L_0x010c:
        r0 = move-exception;
        r3 = r1;
        r4 = r7;
        r2 = -1;
        goto L_0x0028;
    L_0x0112:
        r9 = "";
    L_0x0114:
        r11 = "custom";
        r11 = r5.get(r11);	 Catch:{ Throwable -> 0x1758 }
        r11 = r11 instanceof org.json.JSONObject;	 Catch:{ Throwable -> 0x1758 }
        if (r11 == 0) goto L_0x012b;
    L_0x011e:
        r11 = "custom";
        r11 = r5.getJSONObject(r11);	 Catch:{ Throwable -> 0x0125 }
        goto L_0x0130;
    L_0x0125:
        r0 = move-exception;
        r3 = r1;
        r4 = r7;
        r2 = -1;
        goto L_0x0029;
    L_0x012b:
        r11 = new org.json.JSONObject;	 Catch:{ Throwable -> 0x1758 }
        r11.<init>();	 Catch:{ Throwable -> 0x1758 }
    L_0x0130:
        r14 = "user_id";
        r14 = r5.has(r14);	 Catch:{ Throwable -> 0x1758 }
        if (r14 == 0) goto L_0x0141;
    L_0x0139:
        r14 = "user_id";
        r14 = r5.get(r14);	 Catch:{ Throwable -> 0x0125 }
        goto L_0x0142;
    L_0x0141:
        r14 = 0;
    L_0x0142:
        if (r14 != 0) goto L_0x014f;
    L_0x0144:
        r14 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ Throwable -> 0x0125 }
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);	 Catch:{ Throwable -> 0x0125 }
        r14 = r14.getClientUserId();	 Catch:{ Throwable -> 0x0125 }
        goto L_0x0173;
    L_0x014f:
        r15 = r14 instanceof java.lang.Integer;	 Catch:{ Throwable -> 0x1758 }
        if (r15 == 0) goto L_0x015a;
    L_0x0153:
        r14 = (java.lang.Integer) r14;	 Catch:{ Throwable -> 0x0125 }
        r14 = r14.intValue();	 Catch:{ Throwable -> 0x0125 }
        goto L_0x0173;
    L_0x015a:
        r15 = r14 instanceof java.lang.String;	 Catch:{ Throwable -> 0x1758 }
        if (r15 == 0) goto L_0x0169;
    L_0x015e:
        r14 = (java.lang.String) r14;	 Catch:{ Throwable -> 0x0125 }
        r14 = org.telegram.messenger.Utilities.parseInt(r14);	 Catch:{ Throwable -> 0x0125 }
        r14 = r14.intValue();	 Catch:{ Throwable -> 0x0125 }
        goto L_0x0173;
    L_0x0169:
        r14 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ Throwable -> 0x1758 }
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);	 Catch:{ Throwable -> 0x1758 }
        r14 = r14.getClientUserId();	 Catch:{ Throwable -> 0x1758 }
    L_0x0173:
        r15 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ Throwable -> 0x1758 }
        r4 = 0;
    L_0x0176:
        if (r4 >= r12) goto L_0x0189;
    L_0x0178:
        r17 = org.telegram.messenger.UserConfig.getInstance(r4);	 Catch:{ Throwable -> 0x0125 }
        r6 = r17.getClientUserId();	 Catch:{ Throwable -> 0x0125 }
        if (r6 != r14) goto L_0x0184;
    L_0x0182:
        r15 = r4;
        goto L_0x0189;
    L_0x0184:
        r4 = r4 + 1;
        r6 = 8;
        goto L_0x0176;
    L_0x0189:
        r4 = org.telegram.messenger.UserConfig.getInstance(r15);	 Catch:{ Throwable -> 0x174f }
        r4 = r4.isClientActivated();	 Catch:{ Throwable -> 0x174f }
        if (r4 != 0) goto L_0x01a8;
    L_0x0193:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x01a2 }
        if (r2 == 0) goto L_0x019c;
    L_0x0197:
        r2 = "GCM ACCOUNT NOT ACTIVATED";
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Throwable -> 0x01a2 }
    L_0x019c:
        r2 = r1.countDownLatch;	 Catch:{ Throwable -> 0x01a2 }
        r2.countDown();	 Catch:{ Throwable -> 0x01a2 }
        return;
    L_0x01a2:
        r0 = move-exception;
        r3 = r1;
        r4 = r7;
    L_0x01a5:
        r2 = -1;
        goto L_0x002a;
    L_0x01a8:
        r4 = "google.sent_time";
        r2.get(r4);	 Catch:{ Throwable -> 0x174f }
        r2 = r9.hashCode();	 Catch:{ Throwable -> 0x174f }
        r4 = -NUM; // 0xffffffff8af4e06f float:-2.3580768E-32 double:NaN;
        if (r2 == r4) goto L_0x01d5;
    L_0x01b6:
        r4 = -NUM; // 0xffffffffCLASSNAMEvar_ float:-652872.56 double:NaN;
        if (r2 == r4) goto L_0x01cb;
    L_0x01bb:
        r4 = NUM; // 0x25bae29f float:3.241942E-16 double:3.127458774E-315;
        if (r2 == r4) goto L_0x01c1;
    L_0x01c0:
        goto L_0x01df;
    L_0x01c1:
        r2 = "MESSAGE_ANNOUNCEMENT";
        r2 = r9.equals(r2);	 Catch:{ Throwable -> 0x01a2 }
        if (r2 == 0) goto L_0x01df;
    L_0x01c9:
        r2 = 1;
        goto L_0x01e0;
    L_0x01cb:
        r2 = "DC_UPDATE";
        r2 = r9.equals(r2);	 Catch:{ Throwable -> 0x01a2 }
        if (r2 == 0) goto L_0x01df;
    L_0x01d3:
        r2 = 0;
        goto L_0x01e0;
    L_0x01d5:
        r2 = "SESSION_REVOKE";
        r2 = r9.equals(r2);	 Catch:{ Throwable -> 0x174f }
        if (r2 == 0) goto L_0x01df;
    L_0x01dd:
        r2 = 2;
        goto L_0x01e0;
    L_0x01df:
        r2 = -1;
    L_0x01e0:
        if (r2 == 0) goto L_0x1710;
    L_0x01e2:
        if (r2 == r10) goto L_0x16c5;
    L_0x01e4:
        if (r2 == r13) goto L_0x16af;
    L_0x01e6:
        r2 = "channel_id";
        r2 = r11.has(r2);	 Catch:{ Throwable -> 0x16a7 }
        r19 = 0;
        if (r2 == 0) goto L_0x01f9;
    L_0x01f0:
        r2 = "channel_id";
        r2 = r11.getInt(r2);	 Catch:{ Throwable -> 0x01a2 }
        r4 = -r2;
        r3 = (long) r4;
        goto L_0x01fc;
    L_0x01f9:
        r3 = r19;
        r2 = 0;
    L_0x01fc:
        r14 = "from_id";
        r14 = r11.has(r14);	 Catch:{ Throwable -> 0x16a7 }
        if (r14 == 0) goto L_0x0217;
    L_0x0204:
        r3 = "from_id";
        r3 = r11.getInt(r3);	 Catch:{ Throwable -> 0x0212 }
        r14 = r7;
        r6 = (long) r3;
        r34 = r6;
        r6 = r3;
        r3 = r34;
        goto L_0x0219;
    L_0x0212:
        r0 = move-exception;
        r14 = r7;
    L_0x0214:
        r3 = r1;
        r4 = r14;
        goto L_0x01a5;
    L_0x0217:
        r14 = r7;
        r6 = 0;
    L_0x0219:
        r7 = "chat_id";
        r7 = r11.has(r7);	 Catch:{ Throwable -> 0x16a2 }
        if (r7 == 0) goto L_0x022c;
    L_0x0221:
        r3 = "chat_id";
        r3 = r11.getInt(r3);	 Catch:{ Throwable -> 0x022a }
        r4 = -r3;
        r12 = (long) r4;
        goto L_0x022e;
    L_0x022a:
        r0 = move-exception;
        goto L_0x0214;
    L_0x022c:
        r12 = r3;
        r3 = 0;
    L_0x022e:
        r4 = "encryption_id";
        r4 = r11.has(r4);	 Catch:{ Throwable -> 0x16a2 }
        if (r4 == 0) goto L_0x0240;
    L_0x0236:
        r4 = "encryption_id";
        r4 = r11.getInt(r4);	 Catch:{ Throwable -> 0x022a }
        r12 = (long) r4;	 Catch:{ Throwable -> 0x022a }
        r4 = 32;
        r12 = r12 << r4;
    L_0x0240:
        r4 = (r12 > r19 ? 1 : (r12 == r19 ? 0 : -1));
        if (r4 != 0) goto L_0x0251;
    L_0x0244:
        r4 = "ENCRYPTED_MESSAGE";
        r4 = r4.equals(r9);	 Catch:{ Throwable -> 0x022a }
        if (r4 == 0) goto L_0x0251;
    L_0x024c:
        r12 = -NUM; // 0xfffffffvar_ float:0.0 double:NaN;
    L_0x0251:
        r4 = (r12 > r19 ? 1 : (r12 == r19 ? 0 : -1));
        if (r4 == 0) goto L_0x168b;
    L_0x0255:
        r4 = "MESSAGE_DELETED";
        r4 = r4.equals(r9);	 Catch:{ Throwable -> 0x16a2 }
        r7 = "messages";
        if (r4 == 0) goto L_0x0295;
    L_0x025f:
        r3 = r11.getString(r7);	 Catch:{ Throwable -> 0x022a }
        r4 = ",";
        r3 = r3.split(r4);	 Catch:{ Throwable -> 0x022a }
        r4 = new android.util.SparseArray;	 Catch:{ Throwable -> 0x022a }
        r4.<init>();	 Catch:{ Throwable -> 0x022a }
        r5 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x022a }
        r5.<init>();	 Catch:{ Throwable -> 0x022a }
    L_0x0273:
        r6 = r3.length;	 Catch:{ Throwable -> 0x022a }
        if (r8 >= r6) goto L_0x0282;
    L_0x0276:
        r6 = r3[r8];	 Catch:{ Throwable -> 0x022a }
        r6 = org.telegram.messenger.Utilities.parseInt(r6);	 Catch:{ Throwable -> 0x022a }
        r5.add(r6);	 Catch:{ Throwable -> 0x022a }
        r8 = r8 + 1;
        goto L_0x0273;
    L_0x0282:
        r4.put(r2, r5);	 Catch:{ Throwable -> 0x022a }
        r3 = org.telegram.messenger.NotificationsController.getInstance(r15);	 Catch:{ Throwable -> 0x022a }
        r3.removeDeletedMessagesFromNotifications(r4);	 Catch:{ Throwable -> 0x022a }
        r3 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ Throwable -> 0x022a }
        r3.deleteMessagesByPush(r12, r5, r2);	 Catch:{ Throwable -> 0x022a }
        goto L_0x168b;
    L_0x0295:
        r4 = android.text.TextUtils.isEmpty(r9);	 Catch:{ Throwable -> 0x16a2 }
        if (r4 != 0) goto L_0x1611;
    L_0x029b:
        r4 = "msg_id";
        r4 = r11.has(r4);	 Catch:{ Throwable -> 0x16a2 }
        if (r4 == 0) goto L_0x02aa;
    L_0x02a3:
        r4 = "msg_id";
        r4 = r11.getInt(r4);	 Catch:{ Throwable -> 0x022a }
        goto L_0x02ab;
    L_0x02aa:
        r4 = 0;
    L_0x02ab:
        r10 = "random_id";
        r10 = r11.has(r10);	 Catch:{ Throwable -> 0x16a2 }
        if (r10 == 0) goto L_0x02c4;
    L_0x02b3:
        r10 = "random_id";
        r10 = r11.getString(r10);	 Catch:{ Throwable -> 0x022a }
        r10 = org.telegram.messenger.Utilities.parseLong(r10);	 Catch:{ Throwable -> 0x022a }
        r22 = r10.longValue();	 Catch:{ Throwable -> 0x022a }
        r28 = r22;
        goto L_0x02c6;
    L_0x02c4:
        r28 = r19;
    L_0x02c6:
        if (r4 == 0) goto L_0x0310;
    L_0x02c8:
        r10 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ Throwable -> 0x0308 }
        r10 = r10.dialogs_read_inbox_max;	 Catch:{ Throwable -> 0x0308 }
        r8 = java.lang.Long.valueOf(r12);	 Catch:{ Throwable -> 0x0308 }
        r8 = r10.get(r8);	 Catch:{ Throwable -> 0x0308 }
        r8 = (java.lang.Integer) r8;	 Catch:{ Throwable -> 0x0308 }
        if (r8 != 0) goto L_0x02f7;
    L_0x02da:
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r15);	 Catch:{ Throwable -> 0x0308 }
        r10 = 0;
        r8 = r8.getDialogReadMax(r10, r12);	 Catch:{ Throwable -> 0x0308 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Throwable -> 0x0308 }
        r10 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ Throwable -> 0x0308 }
        r10 = r10.dialogs_read_inbox_max;	 Catch:{ Throwable -> 0x0308 }
        r30 = r14;
        r14 = java.lang.Long.valueOf(r12);	 Catch:{ Throwable -> 0x0326 }
        r10.put(r14, r8);	 Catch:{ Throwable -> 0x0326 }
        goto L_0x02f9;
    L_0x02f7:
        r30 = r14;
    L_0x02f9:
        r8 = r8.intValue();	 Catch:{ Throwable -> 0x0326 }
        if (r4 <= r8) goto L_0x0301;
    L_0x02ff:
        r8 = 1;
        goto L_0x0302;
    L_0x0301:
        r8 = 0;
    L_0x0302:
        r10 = r7;
        r14 = r8;
        r8 = r6;
        r6 = r28;
        goto L_0x0329;
    L_0x0308:
        r0 = move-exception;
        r30 = r14;
    L_0x030b:
        r3 = r1;
        r4 = r30;
        goto L_0x01a5;
    L_0x0310:
        r8 = r6;
        r10 = r7;
        r30 = r14;
        r6 = r28;
        r14 = (r6 > r19 ? 1 : (r6 == r19 ? 0 : -1));
        if (r14 == 0) goto L_0x0328;
    L_0x031a:
        r14 = org.telegram.messenger.MessagesStorage.getInstance(r15);	 Catch:{ Throwable -> 0x0326 }
        r14 = r14.checkMessageByRandomId(r6);	 Catch:{ Throwable -> 0x0326 }
        if (r14 != 0) goto L_0x0328;
    L_0x0324:
        r14 = 1;
        goto L_0x0329;
    L_0x0326:
        r0 = move-exception;
        goto L_0x030b;
    L_0x0328:
        r14 = 0;
    L_0x0329:
        if (r14 == 0) goto L_0x1607;
    L_0x032b:
        r14 = "chat_from_id";
        r14 = r11.has(r14);	 Catch:{ Throwable -> 0x1603 }
        if (r14 == 0) goto L_0x033a;
    L_0x0333:
        r14 = "chat_from_id";
        r14 = r11.getInt(r14);	 Catch:{ Throwable -> 0x0326 }
        goto L_0x033b;
    L_0x033a:
        r14 = 0;
    L_0x033b:
        r1 = "mention";
        r1 = r11.has(r1);	 Catch:{ Throwable -> 0x15fe }
        if (r1 == 0) goto L_0x0358;
    L_0x0343:
        r1 = "mention";
        r1 = r11.getInt(r1);	 Catch:{ Throwable -> 0x034f }
        if (r1 == 0) goto L_0x0358;
    L_0x034b:
        r28 = r15;
        r1 = 1;
        goto L_0x035b;
    L_0x034f:
        r0 = move-exception;
        r2 = -1;
        r3 = r36;
        r1 = r0;
    L_0x0354:
        r4 = r30;
        goto L_0x1771;
    L_0x0358:
        r28 = r15;
        r1 = 0;
    L_0x035b:
        r15 = "silent";
        r15 = r11.has(r15);	 Catch:{ Throwable -> 0x15f9 }
        if (r15 == 0) goto L_0x0376;
    L_0x0363:
        r15 = "silent";
        r15 = r11.getInt(r15);	 Catch:{ Throwable -> 0x036e }
        if (r15 == 0) goto L_0x0376;
    L_0x036b:
        r19 = 1;
        goto L_0x0378;
    L_0x036e:
        r0 = move-exception;
        r2 = -1;
        r3 = r36;
        r1 = r0;
        r15 = r28;
        goto L_0x0354;
    L_0x0376:
        r19 = 0;
    L_0x0378:
        r15 = "loc_args";
        r15 = r5.has(r15);	 Catch:{ Throwable -> 0x15f9 }
        if (r15 == 0) goto L_0x039f;
    L_0x0380:
        r15 = "loc_args";
        r5 = r5.getJSONArray(r15);	 Catch:{ Throwable -> 0x036e }
        r15 = r5.length();	 Catch:{ Throwable -> 0x036e }
        r15 = new java.lang.String[r15];	 Catch:{ Throwable -> 0x036e }
        r20 = r1;
        r23 = r8;
        r1 = 0;
    L_0x0391:
        r8 = r15.length;	 Catch:{ Throwable -> 0x036e }
        if (r1 >= r8) goto L_0x039d;
    L_0x0394:
        r8 = r5.getString(r1);	 Catch:{ Throwable -> 0x036e }
        r15[r1] = r8;	 Catch:{ Throwable -> 0x036e }
        r1 = r1 + 1;
        goto L_0x0391;
    L_0x039d:
        r1 = 0;
        goto L_0x03a5;
    L_0x039f:
        r20 = r1;
        r23 = r8;
        r1 = 0;
        r15 = 0;
    L_0x03a5:
        r5 = r15[r1];	 Catch:{ Throwable -> 0x15f9 }
        r1 = "edit_date";
        r27 = r11.has(r1);	 Catch:{ Throwable -> 0x15f9 }
        r1 = "CHAT_";
        r1 = r9.startsWith(r1);	 Catch:{ Throwable -> 0x15f9 }
        if (r1 == 0) goto L_0x03c5;
    L_0x03b5:
        if (r2 == 0) goto L_0x03ba;
    L_0x03b7:
        r1 = 1;
        r8 = 1;
        goto L_0x03bc;
    L_0x03ba:
        r1 = 1;
        r8 = 0;
    L_0x03bc:
        r11 = r15[r1];	 Catch:{ Throwable -> 0x036e }
        r24 = r5;
        r5 = r11;
        r1 = 0;
    L_0x03c2:
        r26 = 0;
        goto L_0x03e8;
    L_0x03c5:
        r1 = "PINNED_";
        r1 = r9.startsWith(r1);	 Catch:{ Throwable -> 0x15f9 }
        if (r1 == 0) goto L_0x03d4;
    L_0x03cd:
        if (r14 == 0) goto L_0x03d1;
    L_0x03cf:
        r8 = 1;
        goto L_0x03d2;
    L_0x03d1:
        r8 = 0;
    L_0x03d2:
        r1 = 1;
        goto L_0x03e5;
    L_0x03d4:
        r1 = "CHANNEL_";
        r1 = r9.startsWith(r1);	 Catch:{ Throwable -> 0x15f9 }
        if (r1 == 0) goto L_0x03e3;
    L_0x03dc:
        r1 = 0;
        r8 = 0;
        r24 = 0;
        r26 = 1;
        goto L_0x03e8;
    L_0x03e3:
        r1 = 0;
        r8 = 0;
    L_0x03e5:
        r24 = 0;
        goto L_0x03c2;
    L_0x03e8:
        r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x15f9 }
        if (r11 == 0) goto L_0x0413;
    L_0x03ec:
        r11 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x036e }
        r11.<init>();	 Catch:{ Throwable -> 0x036e }
        r25 = r5;
        r5 = "GCM received message notification ";
        r11.append(r5);	 Catch:{ Throwable -> 0x036e }
        r11.append(r9);	 Catch:{ Throwable -> 0x036e }
        r5 = " for dialogId = ";
        r11.append(r5);	 Catch:{ Throwable -> 0x036e }
        r11.append(r12);	 Catch:{ Throwable -> 0x036e }
        r5 = " mid = ";
        r11.append(r5);	 Catch:{ Throwable -> 0x036e }
        r11.append(r4);	 Catch:{ Throwable -> 0x036e }
        r5 = r11.toString();	 Catch:{ Throwable -> 0x036e }
        org.telegram.messenger.FileLog.d(r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x0415;
    L_0x0413:
        r25 = r5;
    L_0x0415:
        r5 = r9.hashCode();	 Catch:{ Throwable -> 0x15f9 }
        switch(r5) {
            case -2100047043: goto L_0x088e;
            case -2091498420: goto L_0x0883;
            case -2053872415: goto L_0x0878;
            case -2039746363: goto L_0x086d;
            case -2023218804: goto L_0x0862;
            case -1979538588: goto L_0x0857;
            case -1979536003: goto L_0x084c;
            case -1979535888: goto L_0x0841;
            case -1969004705: goto L_0x0836;
            case -1946699248: goto L_0x082a;
            case -1528047021: goto L_0x081e;
            case -1493579426: goto L_0x0812;
            case -1480102982: goto L_0x0807;
            case -1478041834: goto L_0x07fc;
            case -1474543101: goto L_0x07f1;
            case -1465695932: goto L_0x07e5;
            case -1374906292: goto L_0x07d9;
            case -1372940586: goto L_0x07cd;
            case -1264245338: goto L_0x07c1;
            case -1236086700: goto L_0x07b5;
            case -1236077786: goto L_0x07a9;
            case -1235796237: goto L_0x079d;
            case -1235686303: goto L_0x0791;
            case -1198046100: goto L_0x0786;
            case -1124254527: goto L_0x077a;
            case -1085137927: goto L_0x076e;
            case -1084856378: goto L_0x0762;
            case -1084746444: goto L_0x0756;
            case -819729482: goto L_0x074a;
            case -772141857: goto L_0x073e;
            case -638310039: goto L_0x0732;
            case -590403924: goto L_0x0726;
            case -589196239: goto L_0x071a;
            case -589193654: goto L_0x070e;
            case -589193539: goto L_0x0702;
            case -440169325: goto L_0x06f6;
            case -412748110: goto L_0x06ea;
            case -228518075: goto L_0x06de;
            case -213586509: goto L_0x06d2;
            case -115582002: goto L_0x06c6;
            case -112621464: goto L_0x06ba;
            case -108522133: goto L_0x06ae;
            case -107572034: goto L_0x06a3;
            case -40534265: goto L_0x0697;
            case 65254746: goto L_0x068b;
            case 141040782: goto L_0x067f;
            case 309993049: goto L_0x0673;
            case 309995634: goto L_0x0667;
            case 309995749: goto L_0x065b;
            case 320532812: goto L_0x064f;
            case 328933854: goto L_0x0643;
            case 331340546: goto L_0x0637;
            case 344816990: goto L_0x062b;
            case 346878138: goto L_0x061f;
            case 350376871: goto L_0x0613;
            case 615714517: goto L_0x0608;
            case 715508879: goto L_0x05fc;
            case 728985323: goto L_0x05f0;
            case 731046471: goto L_0x05e4;
            case 734545204: goto L_0x05d8;
            case 802032552: goto L_0x05cc;
            case 991498806: goto L_0x05c0;
            case 1007364121: goto L_0x05b4;
            case 1019917311: goto L_0x05a8;
            case 1019926225: goto L_0x059c;
            case 1020207774: goto L_0x0590;
            case 1020317708: goto L_0x0584;
            case 1060349560: goto L_0x0578;
            case 1060358474: goto L_0x056c;
            case 1060640023: goto L_0x0560;
            case 1060749957: goto L_0x0555;
            case 1073049781: goto L_0x0549;
            case 1078101399: goto L_0x053d;
            case 1110103437: goto L_0x0531;
            case 1160762272: goto L_0x0525;
            case 1172918249: goto L_0x0519;
            case 1234591620: goto L_0x050d;
            case 1281128640: goto L_0x0501;
            case 1281131225: goto L_0x04f5;
            case 1281131340: goto L_0x04e9;
            case 1310789062: goto L_0x04de;
            case 1333118583: goto L_0x04d2;
            case 1361447897: goto L_0x04c6;
            case 1498266155: goto L_0x04ba;
            case 1533804208: goto L_0x04ae;
            case 1547988151: goto L_0x04a2;
            case 1561464595: goto L_0x0496;
            case 1563525743: goto L_0x048a;
            case 1567024476: goto L_0x047e;
            case 1810705077: goto L_0x0472;
            case 1815177512: goto L_0x0466;
            case 1963241394: goto L_0x045a;
            case 2014789757: goto L_0x044e;
            case 2022049433: goto L_0x0442;
            case 2048733346: goto L_0x0436;
            case 2099392181: goto L_0x042a;
            case 2140162142: goto L_0x041e;
            default: goto L_0x041c;
        };
    L_0x041c:
        goto L_0x0899;
    L_0x041e:
        r5 = "CHAT_MESSAGE_GEOLIVE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0426:
        r5 = 53;
        goto L_0x089a;
    L_0x042a:
        r5 = "CHANNEL_MESSAGE_PHOTOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0432:
        r5 = 39;
        goto L_0x089a;
    L_0x0436:
        r5 = "CHANNEL_MESSAGE_NOTEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x043e:
        r5 = 24;
        goto L_0x089a;
    L_0x0442:
        r5 = "PINNED_CONTACT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x044a:
        r5 = 80;
        goto L_0x089a;
    L_0x044e:
        r5 = "CHAT_PHOTO_EDITED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0456:
        r5 = 60;
        goto L_0x089a;
    L_0x045a:
        r5 = "LOCKED_MESSAGE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0462:
        r5 = 94;
        goto L_0x089a;
    L_0x0466:
        r5 = "CHANNEL_MESSAGES";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x046e:
        r5 = 41;
        goto L_0x089a;
    L_0x0472:
        r5 = "MESSAGE_INVOICE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x047a:
        r5 = 18;
        goto L_0x089a;
    L_0x047e:
        r5 = "CHAT_MESSAGE_VIDEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0486:
        r5 = 45;
        goto L_0x089a;
    L_0x048a:
        r5 = "CHAT_MESSAGE_ROUND";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0492:
        r5 = 46;
        goto L_0x089a;
    L_0x0496:
        r5 = "CHAT_MESSAGE_PHOTO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x049e:
        r5 = 44;
        goto L_0x089a;
    L_0x04a2:
        r5 = "CHAT_MESSAGE_AUDIO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x04aa:
        r5 = 49;
        goto L_0x089a;
    L_0x04ae:
        r5 = "MESSAGE_VIDEOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x04b6:
        r5 = 21;
        goto L_0x089a;
    L_0x04ba:
        r5 = "PHONE_CALL_MISSED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x04c2:
        r5 = 96;
        goto L_0x089a;
    L_0x04c6:
        r5 = "MESSAGE_PHOTOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x04ce:
        r5 = 20;
        goto L_0x089a;
    L_0x04d2:
        r5 = "CHAT_MESSAGE_VIDEOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x04da:
        r5 = 70;
        goto L_0x089a;
    L_0x04de:
        r5 = "MESSAGE_NOTEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x04e6:
        r5 = 1;
        goto L_0x089a;
    L_0x04e9:
        r5 = "MESSAGE_GIF";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x04f1:
        r5 = 15;
        goto L_0x089a;
    L_0x04f5:
        r5 = "MESSAGE_GEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x04fd:
        r5 = 13;
        goto L_0x089a;
    L_0x0501:
        r5 = "MESSAGE_DOC";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0509:
        r5 = 8;
        goto L_0x089a;
    L_0x050d:
        r5 = "CHAT_MESSAGE_GAME_SCORE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0515:
        r5 = 56;
        goto L_0x089a;
    L_0x0519:
        r5 = "CHANNEL_MESSAGE_GEOLIVE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0521:
        r5 = 34;
        goto L_0x089a;
    L_0x0525:
        r5 = "CHAT_MESSAGE_PHOTOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x052d:
        r5 = 69;
        goto L_0x089a;
    L_0x0531:
        r5 = "CHAT_MESSAGE_NOTEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0539:
        r5 = 43;
        goto L_0x089a;
    L_0x053d:
        r5 = "CHAT_TITLE_EDITED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0545:
        r5 = 59;
        goto L_0x089a;
    L_0x0549:
        r5 = "PINNED_NOTEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0551:
        r5 = 73;
        goto L_0x089a;
    L_0x0555:
        r5 = "MESSAGE_TEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x055d:
        r5 = 0;
        goto L_0x089a;
    L_0x0560:
        r5 = "MESSAGE_POLL";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0568:
        r5 = 12;
        goto L_0x089a;
    L_0x056c:
        r5 = "MESSAGE_GAME";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0574:
        r5 = 16;
        goto L_0x089a;
    L_0x0578:
        r5 = "MESSAGE_FWDS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0580:
        r5 = 19;
        goto L_0x089a;
    L_0x0584:
        r5 = "CHAT_MESSAGE_TEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x058c:
        r5 = 42;
        goto L_0x089a;
    L_0x0590:
        r5 = "CHAT_MESSAGE_POLL";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0598:
        r5 = 51;
        goto L_0x089a;
    L_0x059c:
        r5 = "CHAT_MESSAGE_GAME";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x05a4:
        r5 = 55;
        goto L_0x089a;
    L_0x05a8:
        r5 = "CHAT_MESSAGE_FWDS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x05b0:
        r5 = 68;
        goto L_0x089a;
    L_0x05b4:
        r5 = "CHANNEL_MESSAGE_GAME_SCORE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x05bc:
        r5 = 37;
        goto L_0x089a;
    L_0x05c0:
        r5 = "PINNED_GEOLIVE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x05c8:
        r5 = 83;
        goto L_0x089a;
    L_0x05cc:
        r5 = "MESSAGE_CONTACT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x05d4:
        r5 = 11;
        goto L_0x089a;
    L_0x05d8:
        r5 = "PINNED_VIDEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x05e0:
        r5 = 75;
        goto L_0x089a;
    L_0x05e4:
        r5 = "PINNED_ROUND";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x05ec:
        r5 = 76;
        goto L_0x089a;
    L_0x05f0:
        r5 = "PINNED_PHOTO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x05f8:
        r5 = 74;
        goto L_0x089a;
    L_0x05fc:
        r5 = "PINNED_AUDIO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0604:
        r5 = 79;
        goto L_0x089a;
    L_0x0608:
        r5 = "MESSAGE_PHOTO_SECRET";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0610:
        r5 = 3;
        goto L_0x089a;
    L_0x0613:
        r5 = "CHANNEL_MESSAGE_VIDEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x061b:
        r5 = 26;
        goto L_0x089a;
    L_0x061f:
        r5 = "CHANNEL_MESSAGE_ROUND";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0627:
        r5 = 27;
        goto L_0x089a;
    L_0x062b:
        r5 = "CHANNEL_MESSAGE_PHOTO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0633:
        r5 = 25;
        goto L_0x089a;
    L_0x0637:
        r5 = "CHANNEL_MESSAGE_AUDIO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x063f:
        r5 = 30;
        goto L_0x089a;
    L_0x0643:
        r5 = "CHAT_MESSAGE_STICKER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x064b:
        r5 = 48;
        goto L_0x089a;
    L_0x064f:
        r5 = "MESSAGES";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0657:
        r5 = 22;
        goto L_0x089a;
    L_0x065b:
        r5 = "CHAT_MESSAGE_GIF";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0663:
        r5 = 54;
        goto L_0x089a;
    L_0x0667:
        r5 = "CHAT_MESSAGE_GEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x066f:
        r5 = 52;
        goto L_0x089a;
    L_0x0673:
        r5 = "CHAT_MESSAGE_DOC";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x067b:
        r5 = 47;
        goto L_0x089a;
    L_0x067f:
        r5 = "CHAT_LEFT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0687:
        r5 = 65;
        goto L_0x089a;
    L_0x068b:
        r5 = "CHAT_ADD_YOU";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0693:
        r5 = 62;
        goto L_0x089a;
    L_0x0697:
        r5 = "CHAT_DELETE_MEMBER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x069f:
        r5 = 63;
        goto L_0x089a;
    L_0x06a3:
        r5 = "MESSAGE_SCREENSHOT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x06ab:
        r5 = 6;
        goto L_0x089a;
    L_0x06ae:
        r5 = "AUTH_REGION";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x06b6:
        r5 = 90;
        goto L_0x089a;
    L_0x06ba:
        r5 = "CONTACT_JOINED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x06c2:
        r5 = 88;
        goto L_0x089a;
    L_0x06c6:
        r5 = "CHAT_MESSAGE_INVOICE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x06ce:
        r5 = 57;
        goto L_0x089a;
    L_0x06d2:
        r5 = "ENCRYPTION_REQUEST";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x06da:
        r5 = 92;
        goto L_0x089a;
    L_0x06de:
        r5 = "MESSAGE_GEOLIVE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x06e6:
        r5 = 14;
        goto L_0x089a;
    L_0x06ea:
        r5 = "CHAT_DELETE_YOU";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x06f2:
        r5 = 64;
        goto L_0x089a;
    L_0x06f6:
        r5 = "AUTH_UNKNOWN";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x06fe:
        r5 = 89;
        goto L_0x089a;
    L_0x0702:
        r5 = "PINNED_GIF";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x070a:
        r5 = 87;
        goto L_0x089a;
    L_0x070e:
        r5 = "PINNED_GEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0716:
        r5 = 82;
        goto L_0x089a;
    L_0x071a:
        r5 = "PINNED_DOC";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0722:
        r5 = 77;
        goto L_0x089a;
    L_0x0726:
        r5 = "PINNED_GAME_SCORE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x072e:
        r5 = 85;
        goto L_0x089a;
    L_0x0732:
        r5 = "CHANNEL_MESSAGE_STICKER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x073a:
        r5 = 29;
        goto L_0x089a;
    L_0x073e:
        r5 = "PHONE_CALL_REQUEST";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0746:
        r5 = 95;
        goto L_0x089a;
    L_0x074a:
        r5 = "PINNED_STICKER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0752:
        r5 = 78;
        goto L_0x089a;
    L_0x0756:
        r5 = "PINNED_TEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x075e:
        r5 = 72;
        goto L_0x089a;
    L_0x0762:
        r5 = "PINNED_POLL";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x076a:
        r5 = 81;
        goto L_0x089a;
    L_0x076e:
        r5 = "PINNED_GAME";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0776:
        r5 = 84;
        goto L_0x089a;
    L_0x077a:
        r5 = "CHAT_MESSAGE_CONTACT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0782:
        r5 = 50;
        goto L_0x089a;
    L_0x0786:
        r5 = "MESSAGE_VIDEO_SECRET";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x078e:
        r5 = 5;
        goto L_0x089a;
    L_0x0791:
        r5 = "CHANNEL_MESSAGE_TEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0799:
        r5 = 23;
        goto L_0x089a;
    L_0x079d:
        r5 = "CHANNEL_MESSAGE_POLL";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x07a5:
        r5 = 32;
        goto L_0x089a;
    L_0x07a9:
        r5 = "CHANNEL_MESSAGE_GAME";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x07b1:
        r5 = 36;
        goto L_0x089a;
    L_0x07b5:
        r5 = "CHANNEL_MESSAGE_FWDS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x07bd:
        r5 = 38;
        goto L_0x089a;
    L_0x07c1:
        r5 = "PINNED_INVOICE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x07c9:
        r5 = 86;
        goto L_0x089a;
    L_0x07cd:
        r5 = "CHAT_RETURNED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x07d5:
        r5 = 66;
        goto L_0x089a;
    L_0x07d9:
        r5 = "ENCRYPTED_MESSAGE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x07e1:
        r5 = 91;
        goto L_0x089a;
    L_0x07e5:
        r5 = "ENCRYPTION_ACCEPT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x07ed:
        r5 = 93;
        goto L_0x089a;
    L_0x07f1:
        r5 = "MESSAGE_VIDEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x07f9:
        r5 = 4;
        goto L_0x089a;
    L_0x07fc:
        r5 = "MESSAGE_ROUND";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0804:
        r5 = 7;
        goto L_0x089a;
    L_0x0807:
        r5 = "MESSAGE_PHOTO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x080f:
        r5 = 2;
        goto L_0x089a;
    L_0x0812:
        r5 = "MESSAGE_AUDIO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x081a:
        r5 = 10;
        goto L_0x089a;
    L_0x081e:
        r5 = "CHAT_MESSAGES";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0826:
        r5 = 71;
        goto L_0x089a;
    L_0x082a:
        r5 = "CHAT_JOINED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0832:
        r5 = 67;
        goto L_0x089a;
    L_0x0836:
        r5 = "CHAT_ADD_MEMBER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x083e:
        r5 = 61;
        goto L_0x089a;
    L_0x0841:
        r5 = "CHANNEL_MESSAGE_GIF";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0849:
        r5 = 35;
        goto L_0x089a;
    L_0x084c:
        r5 = "CHANNEL_MESSAGE_GEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0854:
        r5 = 33;
        goto L_0x089a;
    L_0x0857:
        r5 = "CHANNEL_MESSAGE_DOC";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x085f:
        r5 = 28;
        goto L_0x089a;
    L_0x0862:
        r5 = "CHANNEL_MESSAGE_VIDEOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x086a:
        r5 = 40;
        goto L_0x089a;
    L_0x086d:
        r5 = "MESSAGE_STICKER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0875:
        r5 = 9;
        goto L_0x089a;
    L_0x0878:
        r5 = "CHAT_CREATED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0880:
        r5 = 58;
        goto L_0x089a;
    L_0x0883:
        r5 = "CHANNEL_MESSAGE_CONTACT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x088b:
        r5 = 31;
        goto L_0x089a;
    L_0x088e:
        r5 = "MESSAGE_GAME_SCORE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 == 0) goto L_0x0899;
    L_0x0896:
        r5 = 17;
        goto L_0x089a;
    L_0x0899:
        r5 = -1;
    L_0x089a:
        r11 = "Photos";
        r18 = r3;
        r3 = "ChannelMessageFew";
        r31 = r2;
        r2 = " ";
        r32 = r12;
        r12 = NUM; // 0x7f0d014e float:1.8742792E38 double:1.0531299426E-314;
        r13 = "AttachSticker";
        switch(r5) {
            case 0: goto L_0x1523;
            case 1: goto L_0x1505;
            case 2: goto L_0x14ea;
            case 3: goto L_0x14cf;
            case 4: goto L_0x14b4;
            case 5: goto L_0x1499;
            case 6: goto L_0x1485;
            case 7: goto L_0x1469;
            case 8: goto L_0x144d;
            case 9: goto L_0x13fa;
            case 10: goto L_0x13de;
            case 11: goto L_0x13bd;
            case 12: goto L_0x139c;
            case 13: goto L_0x1380;
            case 14: goto L_0x1364;
            case 15: goto L_0x1348;
            case 16: goto L_0x1327;
            case 17: goto L_0x130a;
            case 18: goto L_0x12e9;
            case 19: goto L_0x12c5;
            case 20: goto L_0x12a1;
            case 21: goto L_0x127b;
            case 22: goto L_0x1268;
            case 23: goto L_0x124e;
            case 24: goto L_0x1232;
            case 25: goto L_0x1216;
            case 26: goto L_0x11fa;
            case 27: goto L_0x11de;
            case 28: goto L_0x11c2;
            case 29: goto L_0x116f;
            case 30: goto L_0x1153;
            case 31: goto L_0x1132;
            case 32: goto L_0x1111;
            case 33: goto L_0x10f5;
            case 34: goto L_0x10d9;
            case 35: goto L_0x10bd;
            case 36: goto L_0x10a1;
            case 37: goto L_0x1084;
            case 38: goto L_0x105c;
            case 39: goto L_0x103a;
            case 40: goto L_0x1016;
            case 41: goto L_0x1003;
            case 42: goto L_0x0fe4;
            case 43: goto L_0x0fc3;
            case 44: goto L_0x0fa2;
            case 45: goto L_0x0var_;
            case 46: goto L_0x0var_;
            case 47: goto L_0x0f3f;
            case 48: goto L_0x0ecc;
            case 49: goto L_0x0eab;
            case 50: goto L_0x0e85;
            case 51: goto L_0x0e5f;
            case 52: goto L_0x0e3e;
            case 53: goto L_0x0e1d;
            case 54: goto L_0x0dfc;
            case 55: goto L_0x0dd6;
            case 56: goto L_0x0db4;
            case 57: goto L_0x0d8e;
            case 58: goto L_0x0d76;
            case 59: goto L_0x0d5e;
            case 60: goto L_0x0d46;
            case 61: goto L_0x0d29;
            case 62: goto L_0x0d11;
            case 63: goto L_0x0cf9;
            case 64: goto L_0x0ce1;
            case 65: goto L_0x0cc9;
            case 66: goto L_0x0cb1;
            case 67: goto L_0x0CLASSNAME;
            case 68: goto L_0x0CLASSNAME;
            case 69: goto L_0x0CLASSNAME;
            case 70: goto L_0x0c1c;
            case 71: goto L_0x0CLASSNAME;
            case 72: goto L_0x0bcd;
            case 73: goto L_0x0ba0;
            case 74: goto L_0x0b73;
            case 75: goto L_0x0b46;
            case 76: goto L_0x0b19;
            case 77: goto L_0x0aec;
            case 78: goto L_0x0a72;
            case 79: goto L_0x0a45;
            case 80: goto L_0x0a0e;
            case 81: goto L_0x09d7;
            case 82: goto L_0x09aa;
            case 83: goto L_0x097d;
            case 84: goto L_0x0950;
            case 85: goto L_0x0923;
            case 86: goto L_0x08f6;
            case 87: goto L_0x08c9;
            case 88: goto L_0x1552;
            case 89: goto L_0x1552;
            case 90: goto L_0x1552;
            case 91: goto L_0x08b2;
            case 92: goto L_0x1552;
            case 93: goto L_0x1552;
            case 94: goto L_0x1552;
            case 95: goto L_0x1552;
            case 96: goto L_0x1552;
            default: goto L_0x08ae;
        };
    L_0x08ae:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x15f9 }
        goto L_0x153c;
    L_0x08b2:
        r2 = "YouHaveNewMessage";
        r3 = NUM; // 0x7f0d0afc float:1.8747818E38 double:1.053131167E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x036e }
        r3 = "SecretChatName";
        r5 = NUM; // 0x7f0d0922 float:1.8746857E38 double:1.0531309327E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        r25 = r3;
    L_0x08c6:
        r3 = 1;
        goto L_0x1554;
    L_0x08c9:
        if (r14 == 0) goto L_0x08e3;
    L_0x08cb:
        r2 = "NotificationActionPinnedGif";
        r3 = NUM; // 0x7f0d0652 float:1.8745396E38 double:1.053130577E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x08e3:
        r2 = "NotificationActionPinnedGifChannel";
        r3 = NUM; // 0x7f0d0653 float:1.8745398E38 double:1.0531305774E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x08f6:
        if (r14 == 0) goto L_0x0910;
    L_0x08f8:
        r2 = "NotificationActionPinnedInvoice";
        r3 = NUM; // 0x7f0d0654 float:1.87454E38 double:1.053130578E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0910:
        r2 = "NotificationActionPinnedInvoiceChannel";
        r3 = NUM; // 0x7f0d0655 float:1.8745403E38 double:1.0531305784E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0923:
        if (r14 == 0) goto L_0x093d;
    L_0x0925:
        r2 = "NotificationActionPinnedGameScore";
        r3 = NUM; // 0x7f0d064c float:1.8745384E38 double:1.053130574E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x093d:
        r2 = "NotificationActionPinnedGameScoreChannel";
        r3 = NUM; // 0x7f0d064d float:1.8745386E38 double:1.0531305745E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0950:
        if (r14 == 0) goto L_0x096a;
    L_0x0952:
        r2 = "NotificationActionPinnedGame";
        r3 = NUM; // 0x7f0d064a float:1.874538E38 double:1.053130573E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x096a:
        r2 = "NotificationActionPinnedGameChannel";
        r3 = NUM; // 0x7f0d064b float:1.8745382E38 double:1.0531305735E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x097d:
        if (r14 == 0) goto L_0x0997;
    L_0x097f:
        r2 = "NotificationActionPinnedGeoLive";
        r3 = NUM; // 0x7f0d0650 float:1.8745392E38 double:1.053130576E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0997:
        r2 = "NotificationActionPinnedGeoLiveChannel";
        r3 = NUM; // 0x7f0d0651 float:1.8745394E38 double:1.0531305764E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x09aa:
        if (r14 == 0) goto L_0x09c4;
    L_0x09ac:
        r2 = "NotificationActionPinnedGeo";
        r3 = NUM; // 0x7f0d064e float:1.8745388E38 double:1.053130575E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x09c4:
        r2 = "NotificationActionPinnedGeoChannel";
        r3 = NUM; // 0x7f0d064f float:1.874539E38 double:1.0531305755E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x09d7:
        if (r14 == 0) goto L_0x09f6;
    L_0x09d9:
        r2 = "NotificationActionPinnedPoll2";
        r3 = NUM; // 0x7f0d065c float:1.8745417E38 double:1.053130582E-314;
        r5 = 3;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x09f6:
        r2 = "NotificationActionPinnedPollChannel2";
        r3 = NUM; // 0x7f0d065d float:1.8745419E38 double:1.0531305824E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0a0e:
        if (r14 == 0) goto L_0x0a2d;
    L_0x0a10:
        r3 = "NotificationActionPinnedContact2";
        r5 = NUM; // 0x7f0d0646 float:1.8745372E38 double:1.053130571E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0a2d:
        r2 = "NotificationActionPinnedContactChannel2";
        r3 = NUM; // 0x7f0d0647 float:1.8745374E38 double:1.0531305715E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0a45:
        if (r14 == 0) goto L_0x0a5f;
    L_0x0a47:
        r2 = "NotificationActionPinnedVoice";
        r3 = NUM; // 0x7f0d0668 float:1.8745441E38 double:1.053130588E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0a5f:
        r2 = "NotificationActionPinnedVoiceChannel";
        r3 = NUM; // 0x7f0d0669 float:1.8745443E38 double:1.0531305883E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0a72:
        if (r14 == 0) goto L_0x0ab5;
    L_0x0a74:
        r3 = r15.length;	 Catch:{ Throwable -> 0x036e }
        r5 = 2;
        if (r3 <= r5) goto L_0x0a9d;
    L_0x0a78:
        r3 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r3 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Throwable -> 0x036e }
        if (r3 != 0) goto L_0x0a9d;
    L_0x0a80:
        r3 = "NotificationActionPinnedStickerEmoji";
        r5 = NUM; // 0x7f0d0662 float:1.8745429E38 double:1.053130585E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0a9d:
        r2 = "NotificationActionPinnedSticker";
        r3 = NUM; // 0x7f0d0660 float:1.8745425E38 double:1.053130584E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0ab5:
        r2 = r15.length;	 Catch:{ Throwable -> 0x036e }
        r3 = 1;
        if (r2 <= r3) goto L_0x0ad9;
    L_0x0ab9:
        r2 = r15[r3];	 Catch:{ Throwable -> 0x036e }
        r2 = android.text.TextUtils.isEmpty(r2);	 Catch:{ Throwable -> 0x036e }
        if (r2 != 0) goto L_0x0ad9;
    L_0x0ac1:
        r2 = "NotificationActionPinnedStickerEmojiChannel";
        r3 = NUM; // 0x7f0d0663 float:1.874543E38 double:1.0531305853E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0ad9:
        r2 = "NotificationActionPinnedStickerChannel";
        r3 = NUM; // 0x7f0d0661 float:1.8745427E38 double:1.0531305844E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0aec:
        if (r14 == 0) goto L_0x0b06;
    L_0x0aee:
        r2 = "NotificationActionPinnedFile";
        r3 = NUM; // 0x7f0d0648 float:1.8745376E38 double:1.053130572E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0b06:
        r2 = "NotificationActionPinnedFileChannel";
        r3 = NUM; // 0x7f0d0649 float:1.8745378E38 double:1.0531305725E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0b19:
        if (r14 == 0) goto L_0x0b33;
    L_0x0b1b:
        r2 = "NotificationActionPinnedRound";
        r3 = NUM; // 0x7f0d065e float:1.874542E38 double:1.053130583E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0b33:
        r2 = "NotificationActionPinnedRoundChannel";
        r3 = NUM; // 0x7f0d065f float:1.8745423E38 double:1.0531305834E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0b46:
        if (r14 == 0) goto L_0x0b60;
    L_0x0b48:
        r2 = "NotificationActionPinnedVideo";
        r3 = NUM; // 0x7f0d0666 float:1.8745437E38 double:1.053130587E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0b60:
        r2 = "NotificationActionPinnedVideoChannel";
        r3 = NUM; // 0x7f0d0667 float:1.874544E38 double:1.0531305873E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0b73:
        if (r14 == 0) goto L_0x0b8d;
    L_0x0b75:
        r2 = "NotificationActionPinnedPhoto";
        r3 = NUM; // 0x7f0d065a float:1.8745413E38 double:1.053130581E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0b8d:
        r2 = "NotificationActionPinnedPhotoChannel";
        r3 = NUM; // 0x7f0d065b float:1.8745415E38 double:1.0531305814E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0ba0:
        if (r14 == 0) goto L_0x0bba;
    L_0x0ba2:
        r2 = "NotificationActionPinnedNoText";
        r3 = NUM; // 0x7f0d0658 float:1.8745409E38 double:1.05313058E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0bba:
        r2 = "NotificationActionPinnedNoTextChannel";
        r3 = NUM; // 0x7f0d0659 float:1.874541E38 double:1.0531305804E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0bcd:
        if (r14 == 0) goto L_0x0bec;
    L_0x0bcf:
        r3 = "NotificationActionPinnedText";
        r5 = NUM; // 0x7f0d0664 float:1.8745433E38 double:1.053130586E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0bec:
        r2 = "NotificationActionPinnedTextChannel";
        r3 = NUM; // 0x7f0d0665 float:1.8745435E38 double:1.0531305863E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0CLASSNAME:
        r2 = "NotificationGroupAlbum";
        r3 = NUM; // 0x7f0d0671 float:1.874546E38 double:1.0531305923E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x08c6;
    L_0x0c1c:
        r3 = "NotificationGroupFew";
        r5 = NUM; // 0x7f0d0672 float:1.8745461E38 double:1.0531305928E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = "Videos";
        r11 = 2;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r12 = org.telegram.messenger.Utilities.parseInt(r12);	 Catch:{ Throwable -> 0x036e }
        r12 = r12.intValue();	 Catch:{ Throwable -> 0x036e }
        r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r12);	 Catch:{ Throwable -> 0x036e }
        r2[r11] = r10;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036e }
        goto L_0x08c6;
    L_0x0CLASSNAME:
        r3 = "NotificationGroupFew";
        r5 = NUM; // 0x7f0d0672 float:1.8745461E38 double:1.0531305928E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r12 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r12;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r12 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r12;	 Catch:{ Throwable -> 0x036e }
        r10 = 2;
        r12 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r12 = org.telegram.messenger.Utilities.parseInt(r12);	 Catch:{ Throwable -> 0x036e }
        r12 = r12.intValue();	 Catch:{ Throwable -> 0x036e }
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r12);	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036e }
        goto L_0x08c6;
    L_0x0CLASSNAME:
        r3 = "NotificationGroupForwardedFew";
        r5 = NUM; // 0x7f0d0673 float:1.8745463E38 double:1.053130593E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036e }
        r11 = 0;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r2[r11] = r12;	 Catch:{ Throwable -> 0x036e }
        r11 = 1;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r2[r11] = r12;	 Catch:{ Throwable -> 0x036e }
        r11 = 2;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r12 = org.telegram.messenger.Utilities.parseInt(r12);	 Catch:{ Throwable -> 0x036e }
        r12 = r12.intValue();	 Catch:{ Throwable -> 0x036e }
        r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r12);	 Catch:{ Throwable -> 0x036e }
        r2[r11] = r10;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036e }
        goto L_0x08c6;
    L_0x0CLASSNAME:
        r2 = "NotificationGroupAddSelfMega";
        r3 = NUM; // 0x7f0d0670 float:1.8745457E38 double:1.053130592E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0cb1:
        r2 = "NotificationGroupAddSelf";
        r3 = NUM; // 0x7f0d066f float:1.8745455E38 double:1.0531305913E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0cc9:
        r2 = "NotificationGroupLeftMember";
        r3 = NUM; // 0x7f0d0676 float:1.874547E38 double:1.0531305947E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0ce1:
        r2 = "NotificationGroupKickYou";
        r3 = NUM; // 0x7f0d0675 float:1.8745467E38 double:1.053130594E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0cf9:
        r2 = "NotificationGroupKickMember";
        r3 = NUM; // 0x7f0d0674 float:1.8745465E38 double:1.0531305937E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0d11:
        r2 = "NotificationInvitedToGroup";
        r3 = NUM; // 0x7f0d0677 float:1.8745471E38 double:1.053130595E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0d29:
        r3 = "NotificationGroupAddMember";
        r5 = NUM; // 0x7f0d066e float:1.8745453E38 double:1.053130591E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0d46:
        r2 = "NotificationEditedGroupPhoto";
        r3 = NUM; // 0x7f0d066d float:1.8745451E38 double:1.0531305903E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0d5e:
        r2 = "NotificationEditedGroupName";
        r3 = NUM; // 0x7f0d066c float:1.874545E38 double:1.05313059E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0d76:
        r2 = "NotificationInvitedToGroup";
        r3 = NUM; // 0x7f0d0677 float:1.8745471E38 double:1.053130595E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0d8e:
        r3 = "NotificationMessageGroupInvoice";
        r5 = NUM; // 0x7f0d0688 float:1.8745506E38 double:1.0531306036E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036e }
        r3 = "PaymentInvoice";
        r5 = NUM; // 0x7f0d07e8 float:1.874622E38 double:1.0531307775E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x0db4:
        r3 = "NotificationMessageGroupGameScored";
        r5 = NUM; // 0x7f0d0686 float:1.8745502E38 double:1.0531306026E-314;
        r10 = 4;
        r10 = new java.lang.Object[r10];	 Catch:{ Throwable -> 0x036e }
        r11 = 0;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r10[r11] = r12;	 Catch:{ Throwable -> 0x036e }
        r11 = 1;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r10[r11] = r12;	 Catch:{ Throwable -> 0x036e }
        r11 = 2;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r10[r11] = r12;	 Catch:{ Throwable -> 0x036e }
        r2 = 3;
        r11 = r15[r2];	 Catch:{ Throwable -> 0x036e }
        r10[r2] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r10);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x0dd6:
        r3 = "NotificationMessageGroupGame";
        r5 = NUM; // 0x7f0d0685 float:1.87455E38 double:1.053130602E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachGame";
        r5 = NUM; // 0x7f0d013f float:1.8742762E38 double:1.053129935E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x0dfc:
        r2 = "NotificationMessageGroupGif";
        r3 = NUM; // 0x7f0d0687 float:1.8745504E38 double:1.053130603E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachGif";
        r5 = NUM; // 0x7f0d0140 float:1.8742764E38 double:1.0531299356E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x0e1d:
        r2 = "NotificationMessageGroupLiveLocation";
        r3 = NUM; // 0x7f0d0689 float:1.8745508E38 double:1.053130604E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachLiveLocation";
        r5 = NUM; // 0x7f0d0145 float:1.8742774E38 double:1.053129938E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x0e3e:
        r2 = "NotificationMessageGroupMap";
        r3 = NUM; // 0x7f0d068a float:1.874551E38 double:1.0531306046E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachLocation";
        r5 = NUM; // 0x7f0d0147 float:1.8742778E38 double:1.053129939E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x0e5f:
        r3 = "NotificationMessageGroupPoll2";
        r5 = NUM; // 0x7f0d068e float:1.8745518E38 double:1.0531306066E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036e }
        r3 = "Poll";
        r5 = NUM; // 0x7f0d084f float:1.8746429E38 double:1.0531308284E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x0e85:
        r3 = "NotificationMessageGroupContact2";
        r5 = NUM; // 0x7f0d0683 float:1.8745496E38 double:1.053130601E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachContact";
        r5 = NUM; // 0x7f0d013b float:1.8742754E38 double:1.053129933E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x0eab:
        r2 = "NotificationMessageGroupAudio";
        r3 = NUM; // 0x7f0d0682 float:1.8745494E38 double:1.0531306007E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachAudio";
        r5 = NUM; // 0x7f0d0139 float:1.874275E38 double:1.053129932E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x0ecc:
        r5 = r15.length;	 Catch:{ Throwable -> 0x036e }
        r10 = 2;
        if (r5 <= r10) goto L_0x0f0c;
    L_0x0ed0:
        r5 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5 = android.text.TextUtils.isEmpty(r5);	 Catch:{ Throwable -> 0x036e }
        if (r5 != 0) goto L_0x0f0c;
    L_0x0ed8:
        r5 = "NotificationMessageGroupStickerEmoji";
        r10 = NUM; // 0x7f0d0691 float:1.8745524E38 double:1.053130608E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x036e }
        r11 = 0;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r3[r11] = r16;	 Catch:{ Throwable -> 0x036e }
        r11 = 1;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r3[r11] = r16;	 Catch:{ Throwable -> 0x036e }
        r11 = 2;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r3[r11] = r16;	 Catch:{ Throwable -> 0x036e }
        r3 = org.telegram.messenger.LocaleController.formatString(r5, r10, r3);	 Catch:{ Throwable -> 0x036e }
        r5 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x036e }
        r5.<init>();	 Catch:{ Throwable -> 0x036e }
        r10 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r5.append(r10);	 Catch:{ Throwable -> 0x036e }
        r5.append(r2);	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.getString(r13, r12);	 Catch:{ Throwable -> 0x036e }
        r5.append(r2);	 Catch:{ Throwable -> 0x036e }
        r2 = r5.toString();	 Catch:{ Throwable -> 0x036e }
        goto L_0x0f3a;
    L_0x0f0c:
        r3 = "NotificationMessageGroupSticker";
        r5 = NUM; // 0x7f0d0690 float:1.8745522E38 double:1.0531306076E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ Throwable -> 0x036e }
        r11 = 0;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r10[r11] = r16;	 Catch:{ Throwable -> 0x036e }
        r11 = 1;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r10[r11] = r16;	 Catch:{ Throwable -> 0x036e }
        r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r10);	 Catch:{ Throwable -> 0x036e }
        r5 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x036e }
        r5.<init>();	 Catch:{ Throwable -> 0x036e }
        r10 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r5.append(r10);	 Catch:{ Throwable -> 0x036e }
        r5.append(r2);	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.getString(r13, r12);	 Catch:{ Throwable -> 0x036e }
        r5.append(r2);	 Catch:{ Throwable -> 0x036e }
        r2 = r5.toString();	 Catch:{ Throwable -> 0x036e }
    L_0x0f3a:
        r16 = r2;
        r2 = r3;
        goto L_0x1521;
    L_0x0f3f:
        r2 = "NotificationMessageGroupDocument";
        r3 = NUM; // 0x7f0d0684 float:1.8745498E38 double:1.0531306016E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachDocument";
        r5 = NUM; // 0x7f0d013e float:1.874276E38 double:1.0531299347E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x0var_:
        r2 = "NotificationMessageGroupRound";
        r3 = NUM; // 0x7f0d068f float:1.874552E38 double:1.053130607E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachRound";
        r5 = NUM; // 0x7f0d014d float:1.874279E38 double:1.053129942E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x0var_:
        r2 = "NotificationMessageGroupVideo";
        r3 = NUM; // 0x7f0d0693 float:1.8745528E38 double:1.053130609E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachVideo";
        r5 = NUM; // 0x7f0d0151 float:1.8742798E38 double:1.053129944E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x0fa2:
        r2 = "NotificationMessageGroupPhoto";
        r3 = NUM; // 0x7f0d068d float:1.8745516E38 double:1.053130606E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachPhoto";
        r5 = NUM; // 0x7f0d014b float:1.8742786E38 double:1.053129941E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x0fc3:
        r2 = "NotificationMessageGroupNoText";
        r3 = NUM; // 0x7f0d068c float:1.8745514E38 double:1.0531306056E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        r3 = "Message";
        r5 = NUM; // 0x7f0d05ca float:1.874512E38 double:1.0531305097E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x0fe4:
        r3 = "NotificationMessageGroupText";
        r5 = NUM; // 0x7f0d0692 float:1.8745526E38 double:1.0531306086E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036e }
        r3 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x1003:
        r2 = "ChannelMessageAlbum";
        r3 = NUM; // 0x7f0d0241 float:1.8743285E38 double:1.0531300626E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        goto L_0x08c6;
    L_0x1016:
        r2 = 2;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r10 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r2[r5] = r10;	 Catch:{ Throwable -> 0x036e }
        r5 = "Videos";
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ Throwable -> 0x036e }
        r11 = r11.intValue();	 Catch:{ Throwable -> 0x036e }
        r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r11);	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r5;	 Catch:{ Throwable -> 0x036e }
        r5 = NUM; // 0x7f0d0245 float:1.8743293E38 double:1.0531300646E-314;
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036e }
        goto L_0x08c6;
    L_0x103a:
        r2 = 2;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r10 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r2[r5] = r10;	 Catch:{ Throwable -> 0x036e }
        r5 = 1;
        r10 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = org.telegram.messenger.Utilities.parseInt(r10);	 Catch:{ Throwable -> 0x036e }
        r10 = r10.intValue();	 Catch:{ Throwable -> 0x036e }
        r10 = org.telegram.messenger.LocaleController.formatPluralString(r11, r10);	 Catch:{ Throwable -> 0x036e }
        r2[r5] = r10;	 Catch:{ Throwable -> 0x036e }
        r5 = NUM; // 0x7f0d0245 float:1.8743293E38 double:1.0531300646E-314;
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036e }
        goto L_0x08c6;
    L_0x105c:
        r2 = 2;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r10 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r2[r5] = r10;	 Catch:{ Throwable -> 0x036e }
        r5 = "ForwardedMessageCount";
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ Throwable -> 0x036e }
        r11 = r11.intValue();	 Catch:{ Throwable -> 0x036e }
        r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r11);	 Catch:{ Throwable -> 0x036e }
        r5 = r5.toLowerCase();	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r5;	 Catch:{ Throwable -> 0x036e }
        r5 = NUM; // 0x7f0d0245 float:1.8743293E38 double:1.0531300646E-314;
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036e }
        goto L_0x08c6;
    L_0x1084:
        r3 = "NotificationMessageGameScored";
        r5 = NUM; // 0x7f0d0680 float:1.874549E38 double:1.0531305997E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x10a1:
        r2 = "NotificationMessageGame";
        r3 = NUM; // 0x7f0d067f float:1.8745488E38 double:1.053130599E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachGame";
        r5 = NUM; // 0x7f0d013f float:1.8742762E38 double:1.053129935E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x10bd:
        r2 = "ChannelMessageGIF";
        r3 = NUM; // 0x7f0d0246 float:1.8743295E38 double:1.053130065E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachGif";
        r5 = NUM; // 0x7f0d0140 float:1.8742764E38 double:1.0531299356E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x10d9:
        r2 = "ChannelMessageLiveLocation";
        r3 = NUM; // 0x7f0d0247 float:1.8743297E38 double:1.0531300656E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachLiveLocation";
        r5 = NUM; // 0x7f0d0145 float:1.8742774E38 double:1.053129938E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x10f5:
        r2 = "ChannelMessageMap";
        r3 = NUM; // 0x7f0d0248 float:1.87433E38 double:1.053130066E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachLocation";
        r5 = NUM; // 0x7f0d0147 float:1.8742778E38 double:1.053129939E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x1111:
        r2 = "ChannelMessagePoll2";
        r3 = NUM; // 0x7f0d024c float:1.8743307E38 double:1.053130068E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        r3 = "Poll";
        r5 = NUM; // 0x7f0d084f float:1.8746429E38 double:1.0531308284E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x1132:
        r2 = "ChannelMessageContact2";
        r3 = NUM; // 0x7f0d0243 float:1.874329E38 double:1.0531300636E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachContact";
        r5 = NUM; // 0x7f0d013b float:1.8742754E38 double:1.053129933E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x1153:
        r2 = "ChannelMessageAudio";
        r3 = NUM; // 0x7f0d0242 float:1.8743287E38 double:1.053130063E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachAudio";
        r5 = NUM; // 0x7f0d0139 float:1.874275E38 double:1.053129932E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x116f:
        r3 = r15.length;	 Catch:{ Throwable -> 0x036e }
        r5 = 1;
        if (r3 <= r5) goto L_0x11ab;
    L_0x1173:
        r3 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r3 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Throwable -> 0x036e }
        if (r3 != 0) goto L_0x11ab;
    L_0x117b:
        r3 = "ChannelMessageStickerEmoji";
        r5 = NUM; // 0x7f0d024f float:1.8743313E38 double:1.0531300695E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ Throwable -> 0x036e }
        r11 = 0;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r10[r11] = r16;	 Catch:{ Throwable -> 0x036e }
        r11 = 1;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r10[r11] = r16;	 Catch:{ Throwable -> 0x036e }
        r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r10);	 Catch:{ Throwable -> 0x036e }
        r5 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x036e }
        r5.<init>();	 Catch:{ Throwable -> 0x036e }
        r10 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r5.append(r10);	 Catch:{ Throwable -> 0x036e }
        r5.append(r2);	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.getString(r13, r12);	 Catch:{ Throwable -> 0x036e }
        r5.append(r2);	 Catch:{ Throwable -> 0x036e }
        r2 = r5.toString();	 Catch:{ Throwable -> 0x036e }
        goto L_0x0f3a;
    L_0x11ab:
        r2 = "ChannelMessageSticker";
        r3 = NUM; // 0x7f0d024e float:1.8743311E38 double:1.053130069E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = org.telegram.messenger.LocaleController.getString(r13, r12);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x11c2:
        r2 = "ChannelMessageDocument";
        r3 = NUM; // 0x7f0d0244 float:1.8743291E38 double:1.053130064E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachDocument";
        r5 = NUM; // 0x7f0d013e float:1.874276E38 double:1.0531299347E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x11de:
        r2 = "ChannelMessageRound";
        r3 = NUM; // 0x7f0d024d float:1.874331E38 double:1.0531300685E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachRound";
        r5 = NUM; // 0x7f0d014d float:1.874279E38 double:1.053129942E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x11fa:
        r2 = "ChannelMessageVideo";
        r3 = NUM; // 0x7f0d0250 float:1.8743315E38 double:1.05313007E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachVideo";
        r5 = NUM; // 0x7f0d0151 float:1.8742798E38 double:1.053129944E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x1216:
        r2 = "ChannelMessagePhoto";
        r3 = NUM; // 0x7f0d024b float:1.8743305E38 double:1.0531300676E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachPhoto";
        r5 = NUM; // 0x7f0d014b float:1.8742786E38 double:1.053129941E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x1232:
        r2 = "ChannelMessageNoText";
        r3 = NUM; // 0x7f0d024a float:1.8743303E38 double:1.053130067E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "Message";
        r5 = NUM; // 0x7f0d05ca float:1.874512E38 double:1.0531305097E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x124e:
        r2 = "NotificationMessageText";
        r3 = NUM; // 0x7f0d06a0 float:1.8745555E38 double:1.0531306155E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        r3 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x1268:
        r2 = "NotificationMessageAlbum";
        r3 = NUM; // 0x7f0d0679 float:1.8745476E38 double:1.053130596E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        goto L_0x08c6;
    L_0x127b:
        r2 = "NotificationMessageFew";
        r3 = NUM; // 0x7f0d067d float:1.8745484E38 double:1.053130598E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = "Videos";
        r11 = 1;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r12 = org.telegram.messenger.Utilities.parseInt(r12);	 Catch:{ Throwable -> 0x036e }
        r12 = r12.intValue();	 Catch:{ Throwable -> 0x036e }
        r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r12);	 Catch:{ Throwable -> 0x036e }
        r5[r11] = r10;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x08c6;
    L_0x12a1:
        r2 = "NotificationMessageFew";
        r3 = NUM; // 0x7f0d067d float:1.8745484E38 double:1.053130598E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r12 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r12;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r12 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r12 = org.telegram.messenger.Utilities.parseInt(r12);	 Catch:{ Throwable -> 0x036e }
        r12 = r12.intValue();	 Catch:{ Throwable -> 0x036e }
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r12);	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x08c6;
    L_0x12c5:
        r2 = "NotificationMessageForwardFew";
        r3 = NUM; // 0x7f0d067e float:1.8745486E38 double:1.0531305987E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r11 = 0;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r5[r11] = r12;	 Catch:{ Throwable -> 0x036e }
        r11 = 1;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r12 = org.telegram.messenger.Utilities.parseInt(r12);	 Catch:{ Throwable -> 0x036e }
        r12 = r12.intValue();	 Catch:{ Throwable -> 0x036e }
        r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r12);	 Catch:{ Throwable -> 0x036e }
        r5[r11] = r10;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x08c6;
    L_0x12e9:
        r2 = "NotificationMessageInvoice";
        r3 = NUM; // 0x7f0d0694 float:1.874553E38 double:1.0531306096E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        r3 = "PaymentInvoice";
        r5 = NUM; // 0x7f0d07e8 float:1.874622E38 double:1.0531307775E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x130a:
        r3 = "NotificationMessageGameScored";
        r5 = NUM; // 0x7f0d0680 float:1.874549E38 double:1.0531305997E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x1327:
        r2 = "NotificationMessageGame";
        r3 = NUM; // 0x7f0d067f float:1.8745488E38 double:1.053130599E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachGame";
        r5 = NUM; // 0x7f0d013f float:1.8742762E38 double:1.053129935E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x1348:
        r2 = "NotificationMessageGif";
        r3 = NUM; // 0x7f0d0681 float:1.8745492E38 double:1.0531306E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachGif";
        r5 = NUM; // 0x7f0d0140 float:1.8742764E38 double:1.0531299356E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x1364:
        r2 = "NotificationMessageLiveLocation";
        r3 = NUM; // 0x7f0d0695 float:1.8745532E38 double:1.05313061E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachLiveLocation";
        r5 = NUM; // 0x7f0d0145 float:1.8742774E38 double:1.053129938E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x1380:
        r2 = "NotificationMessageMap";
        r3 = NUM; // 0x7f0d0696 float:1.8745534E38 double:1.0531306105E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachLocation";
        r5 = NUM; // 0x7f0d0147 float:1.8742778E38 double:1.053129939E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x139c:
        r2 = "NotificationMessagePoll2";
        r3 = NUM; // 0x7f0d069a float:1.8745542E38 double:1.0531306125E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        r3 = "Poll";
        r5 = NUM; // 0x7f0d084f float:1.8746429E38 double:1.0531308284E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x13bd:
        r2 = "NotificationMessageContact2";
        r3 = NUM; // 0x7f0d067b float:1.874548E38 double:1.053130597E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachContact";
        r5 = NUM; // 0x7f0d013b float:1.8742754E38 double:1.053129933E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x13de:
        r2 = "NotificationMessageAudio";
        r3 = NUM; // 0x7f0d067a float:1.8745478E38 double:1.0531305967E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachAudio";
        r5 = NUM; // 0x7f0d0139 float:1.874275E38 double:1.053129932E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x13fa:
        r3 = r15.length;	 Catch:{ Throwable -> 0x036e }
        r5 = 1;
        if (r3 <= r5) goto L_0x1436;
    L_0x13fe:
        r3 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r3 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Throwable -> 0x036e }
        if (r3 != 0) goto L_0x1436;
    L_0x1406:
        r3 = "NotificationMessageStickerEmoji";
        r5 = NUM; // 0x7f0d069f float:1.8745553E38 double:1.053130615E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ Throwable -> 0x036e }
        r11 = 0;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r10[r11] = r16;	 Catch:{ Throwable -> 0x036e }
        r11 = 1;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r10[r11] = r16;	 Catch:{ Throwable -> 0x036e }
        r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r10);	 Catch:{ Throwable -> 0x036e }
        r5 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x036e }
        r5.<init>();	 Catch:{ Throwable -> 0x036e }
        r10 = r15[r11];	 Catch:{ Throwable -> 0x036e }
        r5.append(r10);	 Catch:{ Throwable -> 0x036e }
        r5.append(r2);	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.getString(r13, r12);	 Catch:{ Throwable -> 0x036e }
        r5.append(r2);	 Catch:{ Throwable -> 0x036e }
        r2 = r5.toString();	 Catch:{ Throwable -> 0x036e }
        goto L_0x0f3a;
    L_0x1436:
        r2 = "NotificationMessageSticker";
        r3 = NUM; // 0x7f0d069e float:1.874555E38 double:1.0531306145E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = org.telegram.messenger.LocaleController.getString(r13, r12);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x144d:
        r2 = "NotificationMessageDocument";
        r3 = NUM; // 0x7f0d067c float:1.8745482E38 double:1.0531305977E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachDocument";
        r5 = NUM; // 0x7f0d013e float:1.874276E38 double:1.0531299347E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x1469:
        r2 = "NotificationMessageRound";
        r3 = NUM; // 0x7f0d069b float:1.8745544E38 double:1.053130613E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachRound";
        r5 = NUM; // 0x7f0d014d float:1.874279E38 double:1.053129942E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x1485:
        r2 = "ActionTakeScreenshoot";
        r3 = NUM; // 0x7f0d008e float:1.8742403E38 double:1.0531298477E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x036e }
        r3 = "un1";
        r5 = 0;
        r10 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r2 = r2.replace(r3, r10);	 Catch:{ Throwable -> 0x036e }
        goto L_0x1553;
    L_0x1499:
        r2 = "NotificationMessageSDVideo";
        r3 = NUM; // 0x7f0d069d float:1.8745549E38 double:1.053130614E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachDestructingVideo";
        r5 = NUM; // 0x7f0d013d float:1.8742758E38 double:1.053129934E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x14b4:
        r2 = "NotificationMessageVideo";
        r3 = NUM; // 0x7f0d06a1 float:1.8745557E38 double:1.053130616E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachVideo";
        r5 = NUM; // 0x7f0d0151 float:1.8742798E38 double:1.053129944E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x14cf:
        r2 = "NotificationMessageSDPhoto";
        r3 = NUM; // 0x7f0d069c float:1.8745547E38 double:1.0531306135E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachDestructingPhoto";
        r5 = NUM; // 0x7f0d013c float:1.8742756E38 double:1.0531299337E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x14ea:
        r2 = "NotificationMessagePhoto";
        r3 = NUM; // 0x7f0d0699 float:1.874554E38 double:1.053130612E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "AttachPhoto";
        r5 = NUM; // 0x7f0d014b float:1.8742786E38 double:1.053129941E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x1505:
        r2 = "NotificationMessageNoText";
        r3 = NUM; // 0x7f0d0698 float:1.8745538E38 double:1.0531306115E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036e }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036e }
        r3 = "Message";
        r5 = NUM; // 0x7f0d05ca float:1.874512E38 double:1.0531305097E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036e }
    L_0x151f:
        r16 = r3;
    L_0x1521:
        r3 = 0;
        goto L_0x1556;
    L_0x1523:
        r2 = "NotificationMessageText";
        r3 = NUM; // 0x7f0d06a0 float:1.8745555E38 double:1.0531306155E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036e }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036e }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036e }
        r3 = r15[r10];	 Catch:{ Throwable -> 0x036e }
        goto L_0x151f;
    L_0x153c:
        if (r2 == 0) goto L_0x1552;
    L_0x153e:
        r2 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x036e }
        r2.<init>();	 Catch:{ Throwable -> 0x036e }
        r3 = "unhandled loc_key = ";
        r2.append(r3);	 Catch:{ Throwable -> 0x036e }
        r2.append(r9);	 Catch:{ Throwable -> 0x036e }
        r2 = r2.toString();	 Catch:{ Throwable -> 0x036e }
        org.telegram.messenger.FileLog.w(r2);	 Catch:{ Throwable -> 0x036e }
    L_0x1552:
        r2 = 0;
    L_0x1553:
        r3 = 0;
    L_0x1554:
        r16 = 0;
    L_0x1556:
        if (r2 == 0) goto L_0x15f0;
    L_0x1558:
        r5 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Throwable -> 0x15f9 }
        r5.<init>();	 Catch:{ Throwable -> 0x15f9 }
        r5.id = r4;	 Catch:{ Throwable -> 0x15f9 }
        r5.random_id = r6;	 Catch:{ Throwable -> 0x15f9 }
        if (r16 == 0) goto L_0x1566;
    L_0x1563:
        r4 = r16;
        goto L_0x1567;
    L_0x1566:
        r4 = r2;
    L_0x1567:
        r5.message = r4;	 Catch:{ Throwable -> 0x15f9 }
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6 = r38 / r6;
        r4 = (int) r6;	 Catch:{ Throwable -> 0x15f9 }
        r5.date = r4;	 Catch:{ Throwable -> 0x15f9 }
        if (r1 == 0) goto L_0x1579;
    L_0x1572:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage;	 Catch:{ Throwable -> 0x036e }
        r4.<init>();	 Catch:{ Throwable -> 0x036e }
        r5.action = r4;	 Catch:{ Throwable -> 0x036e }
    L_0x1579:
        if (r8 == 0) goto L_0x1582;
    L_0x157b:
        r4 = r5.flags;	 Catch:{ Throwable -> 0x036e }
        r6 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r4 = r4 | r6;
        r5.flags = r4;	 Catch:{ Throwable -> 0x036e }
    L_0x1582:
        r12 = r32;
        r5.dialog_id = r12;	 Catch:{ Throwable -> 0x15f9 }
        if (r31 == 0) goto L_0x1596;
    L_0x1588:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChannel;	 Catch:{ Throwable -> 0x036e }
        r4.<init>();	 Catch:{ Throwable -> 0x036e }
        r5.to_id = r4;	 Catch:{ Throwable -> 0x036e }
        r4 = r5.to_id;	 Catch:{ Throwable -> 0x036e }
        r8 = r31;
        r4.channel_id = r8;	 Catch:{ Throwable -> 0x036e }
        goto L_0x15b3;
    L_0x1596:
        if (r18 == 0) goto L_0x15a6;
    L_0x1598:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ Throwable -> 0x036e }
        r4.<init>();	 Catch:{ Throwable -> 0x036e }
        r5.to_id = r4;	 Catch:{ Throwable -> 0x036e }
        r4 = r5.to_id;	 Catch:{ Throwable -> 0x036e }
        r6 = r18;
        r4.chat_id = r6;	 Catch:{ Throwable -> 0x036e }
        goto L_0x15b3;
    L_0x15a6:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ Throwable -> 0x15f9 }
        r4.<init>();	 Catch:{ Throwable -> 0x15f9 }
        r5.to_id = r4;	 Catch:{ Throwable -> 0x15f9 }
        r4 = r5.to_id;	 Catch:{ Throwable -> 0x15f9 }
        r7 = r23;
        r4.user_id = r7;	 Catch:{ Throwable -> 0x15f9 }
    L_0x15b3:
        r4 = r5.flags;	 Catch:{ Throwable -> 0x15f9 }
        r4 = r4 | 256;
        r5.flags = r4;	 Catch:{ Throwable -> 0x15f9 }
        r5.from_id = r14;	 Catch:{ Throwable -> 0x15f9 }
        if (r20 != 0) goto L_0x15c2;
    L_0x15bd:
        if (r1 == 0) goto L_0x15c0;
    L_0x15bf:
        goto L_0x15c2;
    L_0x15c0:
        r1 = 0;
        goto L_0x15c3;
    L_0x15c2:
        r1 = 1;
    L_0x15c3:
        r5.mentioned = r1;	 Catch:{ Throwable -> 0x15f9 }
        r1 = r19;
        r5.silent = r1;	 Catch:{ Throwable -> 0x15f9 }
        r1 = new org.telegram.messenger.MessageObject;	 Catch:{ Throwable -> 0x15f9 }
        r19 = r1;
        r20 = r28;
        r21 = r5;
        r22 = r2;
        r23 = r25;
        r25 = r3;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27);	 Catch:{ Throwable -> 0x15f9 }
        r2 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x15f9 }
        r2.<init>();	 Catch:{ Throwable -> 0x15f9 }
        r2.add(r1);	 Catch:{ Throwable -> 0x15f9 }
        r1 = org.telegram.messenger.NotificationsController.getInstance(r28);	 Catch:{ Throwable -> 0x15f9 }
        r3 = r36;
        r4 = r3.countDownLatch;	 Catch:{ Throwable -> 0x169c }
        r5 = 1;
        r1.processNewMessages(r2, r5, r5, r4);	 Catch:{ Throwable -> 0x169c }
        goto L_0x1690;
    L_0x15f0:
        r3 = r36;
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x169c }
        r1.countDown();	 Catch:{ Throwable -> 0x169c }
        goto L_0x1690;
    L_0x15f9:
        r0 = move-exception;
        r3 = r36;
        goto L_0x169d;
    L_0x15fe:
        r0 = move-exception;
        r3 = r36;
        goto L_0x16ab;
    L_0x1603:
        r0 = move-exception;
        r3 = r1;
        goto L_0x16ab;
    L_0x1607:
        r3 = r1;
        r28 = r15;
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x169c }
        r1.countDown();	 Catch:{ Throwable -> 0x169c }
        goto L_0x1690;
    L_0x1611:
        r8 = r2;
        r7 = r6;
        r30 = r14;
        r28 = r15;
        r6 = r3;
        r3 = r1;
        r1 = "max_id";
        r1 = r11.getInt(r1);	 Catch:{ Throwable -> 0x169c }
        r15 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x169c }
        r15.<init>();	 Catch:{ Throwable -> 0x169c }
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x169c }
        if (r2 == 0) goto L_0x1644;
    L_0x1628:
        r2 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x169c }
        r2.<init>();	 Catch:{ Throwable -> 0x169c }
        r4 = "GCM received read notification max_id = ";
        r2.append(r4);	 Catch:{ Throwable -> 0x169c }
        r2.append(r1);	 Catch:{ Throwable -> 0x169c }
        r4 = " for dialogId = ";
        r2.append(r4);	 Catch:{ Throwable -> 0x169c }
        r2.append(r12);	 Catch:{ Throwable -> 0x169c }
        r2 = r2.toString();	 Catch:{ Throwable -> 0x169c }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Throwable -> 0x169c }
    L_0x1644:
        if (r8 == 0) goto L_0x1653;
    L_0x1646:
        r2 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox;	 Catch:{ Throwable -> 0x169c }
        r2.<init>();	 Catch:{ Throwable -> 0x169c }
        r2.channel_id = r8;	 Catch:{ Throwable -> 0x169c }
        r2.max_id = r1;	 Catch:{ Throwable -> 0x169c }
        r15.add(r2);	 Catch:{ Throwable -> 0x169c }
        goto L_0x1676;
    L_0x1653:
        r2 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox;	 Catch:{ Throwable -> 0x169c }
        r2.<init>();	 Catch:{ Throwable -> 0x169c }
        if (r7 == 0) goto L_0x1666;
    L_0x165a:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ Throwable -> 0x169c }
        r4.<init>();	 Catch:{ Throwable -> 0x169c }
        r2.peer = r4;	 Catch:{ Throwable -> 0x169c }
        r4 = r2.peer;	 Catch:{ Throwable -> 0x169c }
        r4.user_id = r7;	 Catch:{ Throwable -> 0x169c }
        goto L_0x1671;
    L_0x1666:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ Throwable -> 0x169c }
        r4.<init>();	 Catch:{ Throwable -> 0x169c }
        r2.peer = r4;	 Catch:{ Throwable -> 0x169c }
        r4 = r2.peer;	 Catch:{ Throwable -> 0x169c }
        r4.chat_id = r6;	 Catch:{ Throwable -> 0x169c }
    L_0x1671:
        r2.max_id = r1;	 Catch:{ Throwable -> 0x169c }
        r15.add(r2);	 Catch:{ Throwable -> 0x169c }
    L_0x1676:
        r14 = org.telegram.messenger.MessagesController.getInstance(r28);	 Catch:{ Throwable -> 0x169c }
        r16 = 0;
        r17 = 0;
        r18 = 0;
        r19 = 0;
        r14.processUpdateArray(r15, r16, r17, r18, r19);	 Catch:{ Throwable -> 0x169c }
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x169c }
        r1.countDown();	 Catch:{ Throwable -> 0x169c }
        goto L_0x1690;
    L_0x168b:
        r3 = r1;
        r30 = r14;
        r28 = r15;
    L_0x1690:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r28);	 Catch:{ Throwable -> 0x169c }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r28);	 Catch:{ Throwable -> 0x169c }
        r1.resumeNetworkMaybe();	 Catch:{ Throwable -> 0x169c }
        goto L_0x17a9;
    L_0x169c:
        r0 = move-exception;
    L_0x169d:
        r1 = r0;
        r15 = r28;
        goto L_0x1754;
    L_0x16a2:
        r0 = move-exception;
        r3 = r1;
        r30 = r14;
        goto L_0x16ab;
    L_0x16a7:
        r0 = move-exception;
        r3 = r1;
        r30 = r7;
    L_0x16ab:
        r28 = r15;
        goto L_0x1753;
    L_0x16af:
        r3 = r1;
        r30 = r7;
        r28 = r15;
        r1 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ;	 Catch:{ Throwable -> 0x16c0 }
        r1.<init>(r15);	 Catch:{ Throwable -> 0x16bd }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);	 Catch:{ Throwable -> 0x174d }
        return;
    L_0x16bd:
        r0 = move-exception;
        goto L_0x1753;
    L_0x16c0:
        r0 = move-exception;
        r15 = r28;
        goto L_0x1753;
    L_0x16c5:
        r3 = r1;
        r30 = r7;
        r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification;	 Catch:{ Throwable -> 0x174d }
        r1.<init>();	 Catch:{ Throwable -> 0x174d }
        r2 = 0;
        r1.popup = r2;	 Catch:{ Throwable -> 0x174d }
        r2 = 2;
        r1.flags = r2;	 Catch:{ Throwable -> 0x174d }
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6 = r38 / r6;
        r2 = (int) r6;	 Catch:{ Throwable -> 0x174d }
        r1.inbox_date = r2;	 Catch:{ Throwable -> 0x174d }
        r2 = "message";
        r2 = r5.getString(r2);	 Catch:{ Throwable -> 0x174d }
        r1.message = r2;	 Catch:{ Throwable -> 0x174d }
        r2 = "announcement";
        r1.type = r2;	 Catch:{ Throwable -> 0x174d }
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;	 Catch:{ Throwable -> 0x174d }
        r2.<init>();	 Catch:{ Throwable -> 0x174d }
        r1.media = r2;	 Catch:{ Throwable -> 0x174d }
        r2 = new org.telegram.tgnet.TLRPC$TL_updates;	 Catch:{ Throwable -> 0x174d }
        r2.<init>();	 Catch:{ Throwable -> 0x174d }
        r4 = r2.updates;	 Catch:{ Throwable -> 0x174d }
        r4.add(r1);	 Catch:{ Throwable -> 0x174d }
        r1 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ Throwable -> 0x174d }
        r4 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo;	 Catch:{ Throwable -> 0x170e }
        r4.<init>(r15, r2);	 Catch:{ Throwable -> 0x170e }
        r1.postRunnable(r4);	 Catch:{ Throwable -> 0x174d }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ Throwable -> 0x174d }
        r1.resumeNetworkMaybe();	 Catch:{ Throwable -> 0x174d }
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x174d }
        r1.countDown();	 Catch:{ Throwable -> 0x174d }
        return;
    L_0x170e:
        r0 = move-exception;
        goto L_0x1753;
    L_0x1710:
        r3 = r1;
        r30 = r7;
        r1 = "dc";
        r1 = r11.getInt(r1);	 Catch:{ Throwable -> 0x174d }
        r2 = "addr";
        r2 = r11.getString(r2);	 Catch:{ Throwable -> 0x174d }
        r4 = ":";
        r2 = r2.split(r4);	 Catch:{ Throwable -> 0x174d }
        r4 = r2.length;	 Catch:{ Throwable -> 0x174d }
        r5 = 2;
        if (r4 == r5) goto L_0x172f;
    L_0x1729:
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x174d }
        r1.countDown();	 Catch:{ Throwable -> 0x174d }
        return;
    L_0x172f:
        r4 = 0;
        r4 = r2[r4];	 Catch:{ Throwable -> 0x174d }
        r5 = 1;
        r2 = r2[r5];	 Catch:{ Throwable -> 0x174d }
        r2 = java.lang.Integer.parseInt(r2);	 Catch:{ Throwable -> 0x174d }
        r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ Throwable -> 0x174d }
        r5.applyDatacenterAddress(r1, r4, r2);	 Catch:{ Throwable -> 0x174d }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ Throwable -> 0x174d }
        r1.resumeNetworkMaybe();	 Catch:{ Throwable -> 0x174d }
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x174d }
        r1.countDown();	 Catch:{ Throwable -> 0x174d }
        return;
    L_0x174d:
        r0 = move-exception;
        goto L_0x1753;
    L_0x174f:
        r0 = move-exception;
        r3 = r1;
        r30 = r7;
    L_0x1753:
        r1 = r0;
    L_0x1754:
        r4 = r30;
        r2 = -1;
        goto L_0x1771;
    L_0x1758:
        r0 = move-exception;
        r3 = r1;
        r30 = r7;
        r1 = r0;
        r4 = r30;
        r2 = -1;
        goto L_0x1770;
    L_0x1761:
        r0 = move-exception;
        r3 = r1;
        r30 = r7;
        r1 = r0;
        r4 = r30;
        r2 = -1;
        goto L_0x176f;
    L_0x176a:
        r0 = move-exception;
        r3 = r1;
        r1 = r0;
        r2 = -1;
        r4 = 0;
    L_0x176f:
        r9 = 0;
    L_0x1770:
        r15 = -1;
    L_0x1771:
        if (r15 == r2) goto L_0x1783;
    L_0x1773:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15);
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);
        r2.resumeNetworkMaybe();
        r2 = r3.countDownLatch;
        r2.countDown();
        goto L_0x1786;
    L_0x1783:
        r36.onDecryptError();
    L_0x1786:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x17a6;
    L_0x178a:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r5 = "error in loc_key = ";
        r2.append(r5);
        r2.append(r9);
        r5 = " json ";
        r2.append(r5);
        r2.append(r4);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.e(r2);
    L_0x17a6:
        org.telegram.messenger.FileLog.e(r1);
    L_0x17a9:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.GcmPushListenerService.lambda$null$2$GcmPushListenerService(java.util.Map, long):void");
    }

    static /* synthetic */ void lambda$null$1(int i) {
        if (UserConfig.getInstance(i).getClientUserId() != 0) {
            UserConfig.getInstance(i).clearConfig();
            MessagesController.getInstance(i).performLogout(0);
        }
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
        AndroidUtilities.runOnUIThread(new -$$Lambda$GcmPushListenerService$ffUQ4nnRB_6w44XQMuFHn20mHhI(str));
    }

    static /* synthetic */ void lambda$onNewToken$4(String str) {
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$GcmPushListenerService$bwenO-DVO-aSNI2jCscsonfGN0A(str));
    }

    static /* synthetic */ void lambda$sendRegistrationToServer$6(String str) {
        SharedConfig.pushString = str;
        for (int i = 0; i < 3; i++) {
            UserConfig instance = UserConfig.getInstance(i);
            instance.registeredForPush = false;
            instance.saveConfig(false);
            if (instance.getClientUserId() != 0) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$GcmPushListenerService$H9wqOUi7FPrD-iS5PZz1TFLlitA(i, str));
            }
        }
    }
}
