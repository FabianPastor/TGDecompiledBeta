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

    /* JADX WARNING: Removed duplicated region for block: B:274:0x0411  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x03ea A:{SYNTHETIC, Splitter:B:272:0x03ea} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x088c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0881 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:561:0x0876 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x086b A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:555:0x0860 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x0855 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:549:0x084a A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:546:0x083f A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:543:0x0834 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:540:0x0828 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:537:0x081c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0810 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:531:0x0805 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x07fa A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x07ef A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x07e3 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x07d7 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x07cb A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x07bf A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x07b3 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x07a7 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:504:0x079b A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x078f A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x0784 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0778 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x076c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x0760 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0754 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0748 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x073c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x0730 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x0724 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x0718 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x070c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x0700 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x06f4 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x06e8 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x06dc A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x06d0 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x06c4 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x06b8 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x06ac A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x06a1 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0695 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0689 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x067d A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0671 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0665 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0659 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x064d A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0641 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0635 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x0629 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x061d A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0611 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0606 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x05fa A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x05ee A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x05e2 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x05d6 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x05ca A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x05be A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x05b2 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x05a6 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x059a A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x058e A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0582 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x0576 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x056a A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x055e A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:357:0x0553 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0547 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x053b A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x052f A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x0523 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x0517 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x050b A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x04ff A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x04f3 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x04e7 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x04dc A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x04d0 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x04c4 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x04b8 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x04ac A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x04a0 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x0494 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x0488 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x047c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0470 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0464 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x0458 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x044c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x0440 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x0434 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0428 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x041c A:{SYNTHETIC, Splitter:B:278:0x041c} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x039d  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x037e A:{SYNTHETIC, Splitter:B:240:0x037e} */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x03c3 A:{SYNTHETIC, Splitter:B:258:0x03c3} */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x03b3  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x03ea A:{SYNTHETIC, Splitter:B:272:0x03ea} */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0411  */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x088c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0881 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:561:0x0876 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x086b A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:555:0x0860 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x0855 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:549:0x084a A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:546:0x083f A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:543:0x0834 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:540:0x0828 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:537:0x081c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0810 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:531:0x0805 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x07fa A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x07ef A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x07e3 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x07d7 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x07cb A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x07bf A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x07b3 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x07a7 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:504:0x079b A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x078f A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x0784 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0778 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x076c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x0760 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0754 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0748 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x073c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x0730 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x0724 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x0718 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x070c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x0700 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x06f4 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x06e8 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x06dc A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x06d0 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x06c4 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x06b8 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x06ac A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x06a1 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0695 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0689 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x067d A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0671 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0665 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0659 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x064d A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0641 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0635 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x0629 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x061d A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0611 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0606 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x05fa A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x05ee A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x05e2 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x05d6 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x05ca A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x05be A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x05b2 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x05a6 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x059a A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x058e A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0582 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x0576 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x056a A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x055e A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:357:0x0553 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0547 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x053b A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x052f A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x0523 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x0517 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x050b A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x04ff A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x04f3 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x04e7 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x04dc A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x04d0 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x04c4 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x04b8 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x04ac A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x04a0 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x0494 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x0488 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x047c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0470 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0464 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x0458 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x044c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x0440 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x0434 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0428 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x041c A:{SYNTHETIC, Splitter:B:278:0x041c} */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0361 A:{SYNTHETIC, Splitter:B:230:0x0361} */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x037e A:{SYNTHETIC, Splitter:B:240:0x037e} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x039d  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x03b3  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x03c3 A:{SYNTHETIC, Splitter:B:258:0x03c3} */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0411  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x03ea A:{SYNTHETIC, Splitter:B:272:0x03ea} */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x088c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0881 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:561:0x0876 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x086b A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:555:0x0860 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x0855 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:549:0x084a A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:546:0x083f A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:543:0x0834 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:540:0x0828 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:537:0x081c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0810 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:531:0x0805 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x07fa A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x07ef A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x07e3 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x07d7 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x07cb A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x07bf A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x07b3 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x07a7 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:504:0x079b A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x078f A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x0784 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0778 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x076c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x0760 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0754 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0748 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x073c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x0730 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x0724 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x0718 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x070c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x0700 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x06f4 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x06e8 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x06dc A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x06d0 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x06c4 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x06b8 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x06ac A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x06a1 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0695 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0689 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x067d A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0671 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0665 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0659 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x064d A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0641 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0635 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x0629 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x061d A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0611 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0606 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x05fa A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x05ee A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x05e2 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x05d6 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x05ca A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x05be A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x05b2 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x05a6 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x059a A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x058e A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0582 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x0576 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x056a A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x055e A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:357:0x0553 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0547 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x053b A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x052f A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x0523 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x0517 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x050b A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x04ff A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x04f3 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x04e7 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x04dc A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x04d0 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x04c4 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x04b8 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x04ac A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x04a0 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x0494 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x0488 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x047c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0470 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0464 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x0458 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x044c A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x0440 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x0434 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0428 A:{Catch:{ Throwable -> 0x036c }} */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x041c A:{SYNTHETIC, Splitter:B:278:0x041c} */
    /* JADX WARNING: Removed duplicated region for block: B:811:0x170e A:{Catch:{ Throwable -> 0x16bb, Throwable -> 0x174b }} */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x01e0  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x01e0  */
    /* JADX WARNING: Removed duplicated region for block: B:811:0x170e A:{Catch:{ Throwable -> 0x16bb, Throwable -> 0x174b }} */
    /* JADX WARNING: Removed duplicated region for block: B:811:0x170e A:{Catch:{ Throwable -> 0x16bb, Throwable -> 0x174b }} */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x01e0  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1781  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1771  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1788  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1771  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1781  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1788  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1781  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1771  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1788  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1771  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1781  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1788  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1781  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1771  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1788  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1771  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1781  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1788  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1781  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1771  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1788  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1771  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1781  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1788  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1781  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1771  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1788  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1771  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1781  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1788  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1781  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1771  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1788  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1771  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1781  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1788  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1781  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1771  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1788  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1771  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1781  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1788  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1781  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1771  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1788  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1771  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1781  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1788  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1781  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1771  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1788  */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:571:0x0898, code skipped:
            r11 = "Photos";
            r18 = r3;
            r3 = "ChannelMessageFew";
            r31 = r2;
            r2 = " ";
            r32 = r12;
            r13 = "AttachSticker";
     */
    /* JADX WARNING: Missing block: B:572:0x08a9, code skipped:
            switch(r5) {
                case 0: goto L_0x1521;
                case 1: goto L_0x1503;
                case 2: goto L_0x14e8;
                case 3: goto L_0x14cd;
                case 4: goto L_0x14b2;
                case 5: goto L_0x1497;
                case 6: goto L_0x1483;
                case 7: goto L_0x1467;
                case 8: goto L_0x144b;
                case 9: goto L_0x13f8;
                case 10: goto L_0x13dc;
                case 11: goto L_0x13bb;
                case 12: goto L_0x139a;
                case 13: goto L_0x137e;
                case 14: goto L_0x1362;
                case 15: goto L_0x1346;
                case 16: goto L_0x1325;
                case 17: goto L_0x1308;
                case 18: goto L_0x12e7;
                case 19: goto L_0x12c3;
                case 20: goto L_0x129f;
                case 21: goto L_0x1279;
                case 22: goto L_0x1266;
                case 23: goto L_0x124c;
                case 24: goto L_0x1230;
                case 25: goto L_0x1214;
                case 26: goto L_0x11f8;
                case 27: goto L_0x11dc;
                case 28: goto L_0x11c0;
                case 29: goto L_0x116d;
                case 30: goto L_0x1151;
                case 31: goto L_0x1130;
                case 32: goto L_0x110f;
                case 33: goto L_0x10f3;
                case 34: goto L_0x10d7;
                case 35: goto L_0x10bb;
                case 36: goto L_0x109f;
                case 37: goto L_0x1082;
                case 38: goto L_0x105a;
                case 39: goto L_0x1038;
                case 40: goto L_0x1014;
                case 41: goto L_0x1001;
                case 42: goto L_0x0fe2;
                case 43: goto L_0x0fc1;
                case 44: goto L_0x0fa0;
                case 45: goto L_0x0f7f;
                case 46: goto L_0x0f5e;
                case 47: goto L_0x0f3d;
                case 48: goto L_0x0eca;
                case 49: goto L_0x0ea9;
                case 50: goto L_0x0e83;
                case 51: goto L_0x0e5d;
                case 52: goto L_0x0e3c;
                case 53: goto L_0x0e1b;
                case 54: goto L_0x0dfa;
                case 55: goto L_0x0dd4;
                case 56: goto L_0x0db2;
                case 57: goto L_0x0d8c;
                case 58: goto L_0x0d74;
                case 59: goto L_0x0d5c;
                case 60: goto L_0x0d44;
                case 61: goto L_0x0d27;
                case 62: goto L_0x0d0f;
                case 63: goto L_0x0cf7;
                case 64: goto L_0x0cdf;
                case 65: goto L_0x0cc7;
                case 66: goto L_0x0caf;
                case 67: goto L_0x0CLASSNAME;
                case 68: goto L_0x0c6e;
                case 69: goto L_0x0CLASSNAME;
                case 70: goto L_0x0c1a;
                case 71: goto L_0x0CLASSNAME;
                case 72: goto L_0x0bcb;
                case 73: goto L_0x0b9e;
                case 74: goto L_0x0b71;
                case 75: goto L_0x0b44;
                case 76: goto L_0x0b17;
                case 77: goto L_0x0aea;
                case 78: goto L_0x0a70;
                case 79: goto L_0x0a43;
                case 80: goto L_0x0a0c;
                case 81: goto L_0x09d5;
                case 82: goto L_0x09a8;
                case 83: goto L_0x097b;
                case 84: goto L_0x094e;
                case 85: goto L_0x0921;
                case 86: goto L_0x08f4;
                case 87: goto L_0x08c7;
                case 88: goto L_0x1550;
                case 89: goto L_0x1550;
                case 90: goto L_0x1550;
                case 91: goto L_0x08b0;
                case 92: goto L_0x1550;
                case 93: goto L_0x1550;
                case 94: goto L_0x1550;
                case 95: goto L_0x1550;
                case 96: goto L_0x1550;
                default: goto L_0x08ac;
            };
     */
    /* JADX WARNING: Missing block: B:576:?, code skipped:
            r2 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
            r25 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
     */
    /* JADX WARNING: Missing block: B:577:0x08c4, code skipped:
            r3 = true;
     */
    /* JADX WARNING: Missing block: B:578:0x08c7, code skipped:
            if (r14 == 0) goto L_0x08e1;
     */
    /* JADX WARNING: Missing block: B:579:0x08c9, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:580:0x08e1, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:581:0x08f4, code skipped:
            if (r14 == 0) goto L_0x090e;
     */
    /* JADX WARNING: Missing block: B:582:0x08f6, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:583:0x090e, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:584:0x0921, code skipped:
            if (r14 == 0) goto L_0x093b;
     */
    /* JADX WARNING: Missing block: B:585:0x0923, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:586:0x093b, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:587:0x094e, code skipped:
            if (r14 == 0) goto L_0x0968;
     */
    /* JADX WARNING: Missing block: B:588:0x0950, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:589:0x0968, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:590:0x097b, code skipped:
            if (r14 == 0) goto L_0x0995;
     */
    /* JADX WARNING: Missing block: B:591:0x097d, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:592:0x0995, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:593:0x09a8, code skipped:
            if (r14 == 0) goto L_0x09c2;
     */
    /* JADX WARNING: Missing block: B:594:0x09aa, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:595:0x09c2, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:596:0x09d5, code skipped:
            if (r14 == 0) goto L_0x09f4;
     */
    /* JADX WARNING: Missing block: B:597:0x09d7, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:598:0x09f4, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:599:0x0a0c, code skipped:
            if (r14 == 0) goto L_0x0a2b;
     */
    /* JADX WARNING: Missing block: B:600:0x0a0e, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:601:0x0a2b, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:602:0x0a43, code skipped:
            if (r14 == 0) goto L_0x0a5d;
     */
    /* JADX WARNING: Missing block: B:603:0x0a45, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:604:0x0a5d, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:605:0x0a70, code skipped:
            if (r14 == 0) goto L_0x0ab3;
     */
    /* JADX WARNING: Missing block: B:607:0x0a74, code skipped:
            if (r15.length <= 2) goto L_0x0a9b;
     */
    /* JADX WARNING: Missing block: B:609:0x0a7c, code skipped:
            if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x0a9b;
     */
    /* JADX WARNING: Missing block: B:610:0x0a7e, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:611:0x0a9b, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:613:0x0ab5, code skipped:
            if (r15.length <= 1) goto L_0x0ad7;
     */
    /* JADX WARNING: Missing block: B:615:0x0abd, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x0ad7;
     */
    /* JADX WARNING: Missing block: B:616:0x0abf, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:617:0x0ad7, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:618:0x0aea, code skipped:
            if (r14 == 0) goto L_0x0b04;
     */
    /* JADX WARNING: Missing block: B:619:0x0aec, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:620:0x0b04, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:621:0x0b17, code skipped:
            if (r14 == 0) goto L_0x0b31;
     */
    /* JADX WARNING: Missing block: B:622:0x0b19, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:623:0x0b31, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:624:0x0b44, code skipped:
            if (r14 == 0) goto L_0x0b5e;
     */
    /* JADX WARNING: Missing block: B:625:0x0b46, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:626:0x0b5e, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:627:0x0b71, code skipped:
            if (r14 == 0) goto L_0x0b8b;
     */
    /* JADX WARNING: Missing block: B:628:0x0b73, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:629:0x0b8b, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:630:0x0b9e, code skipped:
            if (r14 == 0) goto L_0x0bb8;
     */
    /* JADX WARNING: Missing block: B:631:0x0ba0, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:632:0x0bb8, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:633:0x0bcb, code skipped:
            if (r14 == 0) goto L_0x0bea;
     */
    /* JADX WARNING: Missing block: B:634:0x0bcd, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:635:0x0bea, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:636:0x0CLASSNAME, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:637:0x0c1a, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:638:0x0CLASSNAME, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString(r11, org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:639:0x0c6e, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString(r10, org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:640:0x0CLASSNAME, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:641:0x0caf, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:642:0x0cc7, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:643:0x0cdf, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:644:0x0cf7, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:645:0x0d0f, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:646:0x0d27, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:647:0x0d44, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:648:0x0d5c, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:649:0x0d74, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:650:0x0d8c, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r15[0], r15[1], r15[2]);
            r3 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Missing block: B:651:0x0db2, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r15[0], r15[1], r15[2], r15[3]);
     */
    /* JADX WARNING: Missing block: B:652:0x0dd4, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r15[0], r15[1], r15[2]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:653:0x0dfa, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:654:0x0e1b, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:655:0x0e3c, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:656:0x0e5d, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r15[0], r15[1], r15[2]);
            r3 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:657:0x0e83, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r15[0], r15[1], r15[2]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:658:0x0ea9, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:660:0x0ecc, code skipped:
            if (r15.length <= 2) goto L_0x0f0a;
     */
    /* JADX WARNING: Missing block: B:662:0x0ed4, code skipped:
            if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x0f0a;
     */
    /* JADX WARNING: Missing block: B:663:0x0ed6, code skipped:
            r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r15[0], r15[1], r15[2]);
            r5 = new java.lang.StringBuilder();
            r5.append(r15[2]);
            r5.append(r2);
            r5.append(org.telegram.messenger.LocaleController.getString(r13, NUM));
            r2 = r5.toString();
     */
    /* JADX WARNING: Missing block: B:664:0x0f0a, code skipped:
            r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r15[0], r15[1]);
            r5 = new java.lang.StringBuilder();
            r5.append(r15[1]);
            r5.append(r2);
            r5.append(org.telegram.messenger.LocaleController.getString(r13, NUM));
            r2 = r5.toString();
     */
    /* JADX WARNING: Missing block: B:665:0x0var_, code skipped:
            r16 = r2;
            r2 = r3;
     */
    /* JADX WARNING: Missing block: B:666:0x0f3d, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:667:0x0f5e, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:668:0x0f7f, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:669:0x0fa0, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:670:0x0fc1, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:671:0x0fe2, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r15[0], r15[1], r15[2]);
            r3 = r15[2];
     */
    /* JADX WARNING: Missing block: B:672:0x1001, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:673:0x1014, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString(r3, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:674:0x1038, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString(r3, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString(r11, org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:675:0x105a, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString(r3, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Missing block: B:676:0x1082, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:677:0x109f, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:678:0x10bb, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:679:0x10d7, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:680:0x10f3, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:681:0x110f, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:682:0x1130, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:683:0x1151, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:685:0x116f, code skipped:
            if (r15.length <= 1) goto L_0x11a9;
     */
    /* JADX WARNING: Missing block: B:687:0x1177, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x11a9;
     */
    /* JADX WARNING: Missing block: B:688:0x1179, code skipped:
            r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r15[0], r15[1]);
            r5 = new java.lang.StringBuilder();
            r5.append(r15[1]);
            r5.append(r2);
            r5.append(org.telegram.messenger.LocaleController.getString(r13, NUM));
            r2 = r5.toString();
     */
    /* JADX WARNING: Missing block: B:689:0x11a9, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString(r13, NUM);
     */
    /* JADX WARNING: Missing block: B:690:0x11c0, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:691:0x11dc, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:692:0x11f8, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:693:0x1214, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:694:0x1230, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:695:0x124c, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r15[0], r15[1]);
            r3 = r15[1];
     */
    /* JADX WARNING: Missing block: B:696:0x1266, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:697:0x1279, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:698:0x129f, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString(r11, org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:699:0x12c3, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString(r10, org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:700:0x12e7, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Missing block: B:701:0x1308, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:702:0x1325, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:703:0x1346, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:704:0x1362, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:705:0x137e, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:706:0x139a, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:707:0x13bb, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:708:0x13dc, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:710:0x13fa, code skipped:
            if (r15.length <= 1) goto L_0x1434;
     */
    /* JADX WARNING: Missing block: B:712:0x1402, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x1434;
     */
    /* JADX WARNING: Missing block: B:713:0x1404, code skipped:
            r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r15[0], r15[1]);
            r5 = new java.lang.StringBuilder();
            r5.append(r15[1]);
            r5.append(r2);
            r5.append(org.telegram.messenger.LocaleController.getString(r13, NUM));
            r2 = r5.toString();
     */
    /* JADX WARNING: Missing block: B:714:0x1434, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString(r13, NUM);
     */
    /* JADX WARNING: Missing block: B:715:0x144b, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:716:0x1467, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:717:0x1483, code skipped:
            r2 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r15[0]);
     */
    /* JADX WARNING: Missing block: B:718:0x1497, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:719:0x14b2, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:720:0x14cd, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:721:0x14e8, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:722:0x1503, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r15[0]);
            r3 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:723:0x151d, code skipped:
            r16 = r3;
     */
    /* JADX WARNING: Missing block: B:724:0x151f, code skipped:
            r3 = false;
     */
    /* JADX WARNING: Missing block: B:725:0x1521, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r15[0], r15[1]);
            r3 = r15[1];
     */
    /* JADX WARNING: Missing block: B:726:0x153a, code skipped:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1550;
     */
    /* JADX WARNING: Missing block: B:727:0x153c, code skipped:
            r2 = new java.lang.StringBuilder();
            r2.append("unhandled loc_key = ");
            r2.append(r9);
            org.telegram.messenger.FileLog.w(r2.toString());
     */
    /* JADX WARNING: Missing block: B:728:0x1550, code skipped:
            r2 = null;
     */
    /* JADX WARNING: Missing block: B:729:0x1551, code skipped:
            r3 = false;
     */
    /* JADX WARNING: Missing block: B:730:0x1552, code skipped:
            r16 = null;
     */
    /* JADX WARNING: Missing block: B:731:0x1554, code skipped:
            if (r2 == null) goto L_0x15ee;
     */
    /* JADX WARNING: Missing block: B:733:?, code skipped:
            r5 = new org.telegram.tgnet.TLRPC.TL_message();
            r5.id = r4;
            r5.random_id = r6;
     */
    /* JADX WARNING: Missing block: B:734:0x155f, code skipped:
            if (r16 == null) goto L_0x1564;
     */
    /* JADX WARNING: Missing block: B:735:0x1561, code skipped:
            r4 = r16;
     */
    /* JADX WARNING: Missing block: B:736:0x1564, code skipped:
            r4 = r2;
     */
    /* JADX WARNING: Missing block: B:737:0x1565, code skipped:
            r5.message = r4;
            r5.date = (int) (r38 / 1000);
     */
    /* JADX WARNING: Missing block: B:738:0x156e, code skipped:
            if (r1 == null) goto L_0x1577;
     */
    /* JADX WARNING: Missing block: B:740:?, code skipped:
            r5.action = new org.telegram.tgnet.TLRPC.TL_messageActionPinMessage();
     */
    /* JADX WARNING: Missing block: B:741:0x1577, code skipped:
            if (r8 == null) goto L_0x1580;
     */
    /* JADX WARNING: Missing block: B:742:0x1579, code skipped:
            r5.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Missing block: B:745:?, code skipped:
            r5.dialog_id = r32;
     */
    /* JADX WARNING: Missing block: B:746:0x1584, code skipped:
            if (r31 == 0) goto L_0x1594;
     */
    /* JADX WARNING: Missing block: B:748:?, code skipped:
            r5.to_id = new org.telegram.tgnet.TLRPC.TL_peerChannel();
            r5.to_id.channel_id = r31;
     */
    /* JADX WARNING: Missing block: B:749:0x1594, code skipped:
            if (r18 == 0) goto L_0x15a4;
     */
    /* JADX WARNING: Missing block: B:750:0x1596, code skipped:
            r5.to_id = new org.telegram.tgnet.TLRPC.TL_peerChat();
            r5.to_id.chat_id = r18;
     */
    /* JADX WARNING: Missing block: B:752:?, code skipped:
            r5.to_id = new org.telegram.tgnet.TLRPC.TL_peerUser();
            r5.to_id.user_id = r23;
     */
    /* JADX WARNING: Missing block: B:753:0x15b1, code skipped:
            r5.flags |= 256;
            r5.from_id = r14;
     */
    /* JADX WARNING: Missing block: B:754:0x15b9, code skipped:
            if (r20 != null) goto L_0x15c0;
     */
    /* JADX WARNING: Missing block: B:755:0x15bb, code skipped:
            if (r1 == null) goto L_0x15be;
     */
    /* JADX WARNING: Missing block: B:757:0x15be, code skipped:
            r1 = false;
     */
    /* JADX WARNING: Missing block: B:758:0x15c0, code skipped:
            r1 = true;
     */
    /* JADX WARNING: Missing block: B:759:0x15c1, code skipped:
            r5.mentioned = r1;
            r5.silent = r19;
            r19 = new org.telegram.messenger.MessageObject(r28, r5, r2, r25, r24, r3, r26, r27);
            r2 = new java.util.ArrayList();
            r2.add(r19);
     */
    /* JADX WARNING: Missing block: B:760:0x15e4, code skipped:
            r3 = r36;
     */
    /* JADX WARNING: Missing block: B:762:?, code skipped:
            org.telegram.messenger.NotificationsController.getInstance(r28).processNewMessages(r2, true, true, r3.countDownLatch);
     */
    /* JADX WARNING: Missing block: B:763:0x15ee, code skipped:
            r36.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:766:0x15fc, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:767:0x15fd, code skipped:
            r3 = r36;
     */
    /* JADX WARNING: Missing block: B:831:0x1771, code skipped:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15);
            org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
            r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:832:0x1781, code skipped:
            onDecryptError();
     */
    /* JADX WARNING: Missing block: B:835:0x1788, code skipped:
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
        r5 = r2.get(r5);	 Catch:{ Throwable -> 0x1768 }
        r6 = r5 instanceof java.lang.String;	 Catch:{ Throwable -> 0x1768 }
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
        goto L_0x176f;
    L_0x002d:
        r5 = (java.lang.String) r5;	 Catch:{ Throwable -> 0x1768 }
        r6 = 8;
        r5 = android.util.Base64.decode(r5, r6);	 Catch:{ Throwable -> 0x1768 }
        r7 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Throwable -> 0x1768 }
        r8 = r5.length;	 Catch:{ Throwable -> 0x1768 }
        r7.<init>(r8);	 Catch:{ Throwable -> 0x1768 }
        r7.writeBytes(r5);	 Catch:{ Throwable -> 0x1768 }
        r8 = 0;
        r7.position(r8);	 Catch:{ Throwable -> 0x1768 }
        r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ Throwable -> 0x1768 }
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
        r9 = new byte[r6];	 Catch:{ Throwable -> 0x1768 }
        r10 = 1;
        r7.readBytes(r9, r10);	 Catch:{ Throwable -> 0x1768 }
        r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ Throwable -> 0x1768 }
        r11 = java.util.Arrays.equals(r11, r9);	 Catch:{ Throwable -> 0x1768 }
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
        r9 = new byte[r9];	 Catch:{ Throwable -> 0x1768 }
        r7.readBytes(r9, r10);	 Catch:{ Throwable -> 0x1768 }
        r11 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ Throwable -> 0x1768 }
        r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13);	 Catch:{ Throwable -> 0x1768 }
        r14 = r7.buffer;	 Catch:{ Throwable -> 0x1768 }
        r15 = r11.aesKey;	 Catch:{ Throwable -> 0x1768 }
        r11 = r11.aesIv;	 Catch:{ Throwable -> 0x1768 }
        r17 = 0;
        r18 = 0;
        r19 = 24;
        r5 = r5.length;	 Catch:{ Throwable -> 0x1768 }
        r20 = r5 + -24;
        r16 = r11;
        org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20);	 Catch:{ Throwable -> 0x1768 }
        r21 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ Throwable -> 0x1768 }
        r22 = 96;
        r23 = 32;
        r5 = r7.buffer;	 Catch:{ Throwable -> 0x1768 }
        r25 = 24;
        r11 = r7.buffer;	 Catch:{ Throwable -> 0x1768 }
        r26 = r11.limit();	 Catch:{ Throwable -> 0x1768 }
        r24 = r5;
        r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26);	 Catch:{ Throwable -> 0x1768 }
        r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6);	 Catch:{ Throwable -> 0x1768 }
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
        r5 = r7.readInt32(r10);	 Catch:{ Throwable -> 0x1768 }
        r5 = new byte[r5];	 Catch:{ Throwable -> 0x1768 }
        r7.readBytes(r5, r10);	 Catch:{ Throwable -> 0x1768 }
        r7 = new java.lang.String;	 Catch:{ Throwable -> 0x1768 }
        r7.<init>(r5);	 Catch:{ Throwable -> 0x1768 }
        r5 = new org.json.JSONObject;	 Catch:{ Throwable -> 0x175f }
        r5.<init>(r7);	 Catch:{ Throwable -> 0x175f }
        r9 = "loc_key";
        r9 = r5.has(r9);	 Catch:{ Throwable -> 0x175f }
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
        r11 = r5.get(r11);	 Catch:{ Throwable -> 0x1756 }
        r11 = r11 instanceof org.json.JSONObject;	 Catch:{ Throwable -> 0x1756 }
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
        r11 = new org.json.JSONObject;	 Catch:{ Throwable -> 0x1756 }
        r11.<init>();	 Catch:{ Throwable -> 0x1756 }
    L_0x0130:
        r14 = "user_id";
        r14 = r5.has(r14);	 Catch:{ Throwable -> 0x1756 }
        if (r14 == 0) goto L_0x013f;
    L_0x0138:
        r14 = "user_id";
        r14 = r5.get(r14);	 Catch:{ Throwable -> 0x0125 }
        goto L_0x0140;
    L_0x013f:
        r14 = 0;
    L_0x0140:
        if (r14 != 0) goto L_0x014d;
    L_0x0142:
        r14 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ Throwable -> 0x0125 }
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);	 Catch:{ Throwable -> 0x0125 }
        r14 = r14.getClientUserId();	 Catch:{ Throwable -> 0x0125 }
        goto L_0x0171;
    L_0x014d:
        r15 = r14 instanceof java.lang.Integer;	 Catch:{ Throwable -> 0x1756 }
        if (r15 == 0) goto L_0x0158;
    L_0x0151:
        r14 = (java.lang.Integer) r14;	 Catch:{ Throwable -> 0x0125 }
        r14 = r14.intValue();	 Catch:{ Throwable -> 0x0125 }
        goto L_0x0171;
    L_0x0158:
        r15 = r14 instanceof java.lang.String;	 Catch:{ Throwable -> 0x1756 }
        if (r15 == 0) goto L_0x0167;
    L_0x015c:
        r14 = (java.lang.String) r14;	 Catch:{ Throwable -> 0x0125 }
        r14 = org.telegram.messenger.Utilities.parseInt(r14);	 Catch:{ Throwable -> 0x0125 }
        r14 = r14.intValue();	 Catch:{ Throwable -> 0x0125 }
        goto L_0x0171;
    L_0x0167:
        r14 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ Throwable -> 0x1756 }
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);	 Catch:{ Throwable -> 0x1756 }
        r14 = r14.getClientUserId();	 Catch:{ Throwable -> 0x1756 }
    L_0x0171:
        r15 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ Throwable -> 0x1756 }
        r4 = 0;
    L_0x0174:
        if (r4 >= r12) goto L_0x0187;
    L_0x0176:
        r17 = org.telegram.messenger.UserConfig.getInstance(r4);	 Catch:{ Throwable -> 0x0125 }
        r6 = r17.getClientUserId();	 Catch:{ Throwable -> 0x0125 }
        if (r6 != r14) goto L_0x0182;
    L_0x0180:
        r15 = r4;
        goto L_0x0187;
    L_0x0182:
        r4 = r4 + 1;
        r6 = 8;
        goto L_0x0174;
    L_0x0187:
        r4 = org.telegram.messenger.UserConfig.getInstance(r15);	 Catch:{ Throwable -> 0x174d }
        r4 = r4.isClientActivated();	 Catch:{ Throwable -> 0x174d }
        if (r4 != 0) goto L_0x01a6;
    L_0x0191:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x01a0 }
        if (r2 == 0) goto L_0x019a;
    L_0x0195:
        r2 = "GCM ACCOUNT NOT ACTIVATED";
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Throwable -> 0x01a0 }
    L_0x019a:
        r2 = r1.countDownLatch;	 Catch:{ Throwable -> 0x01a0 }
        r2.countDown();	 Catch:{ Throwable -> 0x01a0 }
        return;
    L_0x01a0:
        r0 = move-exception;
        r3 = r1;
        r4 = r7;
    L_0x01a3:
        r2 = -1;
        goto L_0x002a;
    L_0x01a6:
        r4 = "google.sent_time";
        r2.get(r4);	 Catch:{ Throwable -> 0x174d }
        r2 = r9.hashCode();	 Catch:{ Throwable -> 0x174d }
        r4 = -NUM; // 0xffffffff8af4e06f float:-2.3580768E-32 double:NaN;
        if (r2 == r4) goto L_0x01d3;
    L_0x01b4:
        r4 = -NUM; // 0xffffffffCLASSNAMEvar_ float:-652872.56 double:NaN;
        if (r2 == r4) goto L_0x01c9;
    L_0x01b9:
        r4 = NUM; // 0x25bae29f float:3.241942E-16 double:3.127458774E-315;
        if (r2 == r4) goto L_0x01bf;
    L_0x01be:
        goto L_0x01dd;
    L_0x01bf:
        r2 = "MESSAGE_ANNOUNCEMENT";
        r2 = r9.equals(r2);	 Catch:{ Throwable -> 0x01a0 }
        if (r2 == 0) goto L_0x01dd;
    L_0x01c7:
        r2 = 1;
        goto L_0x01de;
    L_0x01c9:
        r2 = "DC_UPDATE";
        r2 = r9.equals(r2);	 Catch:{ Throwable -> 0x01a0 }
        if (r2 == 0) goto L_0x01dd;
    L_0x01d1:
        r2 = 0;
        goto L_0x01de;
    L_0x01d3:
        r2 = "SESSION_REVOKE";
        r2 = r9.equals(r2);	 Catch:{ Throwable -> 0x174d }
        if (r2 == 0) goto L_0x01dd;
    L_0x01db:
        r2 = 2;
        goto L_0x01de;
    L_0x01dd:
        r2 = -1;
    L_0x01de:
        if (r2 == 0) goto L_0x170e;
    L_0x01e0:
        if (r2 == r10) goto L_0x16c3;
    L_0x01e2:
        if (r2 == r13) goto L_0x16ad;
    L_0x01e4:
        r2 = "channel_id";
        r2 = r11.has(r2);	 Catch:{ Throwable -> 0x16a5 }
        r19 = 0;
        if (r2 == 0) goto L_0x01f7;
    L_0x01ee:
        r2 = "channel_id";
        r2 = r11.getInt(r2);	 Catch:{ Throwable -> 0x01a0 }
        r4 = -r2;
        r3 = (long) r4;
        goto L_0x01fa;
    L_0x01f7:
        r3 = r19;
        r2 = 0;
    L_0x01fa:
        r14 = "from_id";
        r14 = r11.has(r14);	 Catch:{ Throwable -> 0x16a5 }
        if (r14 == 0) goto L_0x0215;
    L_0x0202:
        r3 = "from_id";
        r3 = r11.getInt(r3);	 Catch:{ Throwable -> 0x0210 }
        r14 = r7;
        r6 = (long) r3;
        r34 = r6;
        r6 = r3;
        r3 = r34;
        goto L_0x0217;
    L_0x0210:
        r0 = move-exception;
        r14 = r7;
    L_0x0212:
        r3 = r1;
        r4 = r14;
        goto L_0x01a3;
    L_0x0215:
        r14 = r7;
        r6 = 0;
    L_0x0217:
        r7 = "chat_id";
        r7 = r11.has(r7);	 Catch:{ Throwable -> 0x16a0 }
        if (r7 == 0) goto L_0x022a;
    L_0x021f:
        r3 = "chat_id";
        r3 = r11.getInt(r3);	 Catch:{ Throwable -> 0x0228 }
        r4 = -r3;
        r12 = (long) r4;
        goto L_0x022c;
    L_0x0228:
        r0 = move-exception;
        goto L_0x0212;
    L_0x022a:
        r12 = r3;
        r3 = 0;
    L_0x022c:
        r4 = "encryption_id";
        r4 = r11.has(r4);	 Catch:{ Throwable -> 0x16a0 }
        if (r4 == 0) goto L_0x023e;
    L_0x0234:
        r4 = "encryption_id";
        r4 = r11.getInt(r4);	 Catch:{ Throwable -> 0x0228 }
        r12 = (long) r4;	 Catch:{ Throwable -> 0x0228 }
        r4 = 32;
        r12 = r12 << r4;
    L_0x023e:
        r4 = (r12 > r19 ? 1 : (r12 == r19 ? 0 : -1));
        if (r4 != 0) goto L_0x024f;
    L_0x0242:
        r4 = "ENCRYPTED_MESSAGE";
        r4 = r4.equals(r9);	 Catch:{ Throwable -> 0x0228 }
        if (r4 == 0) goto L_0x024f;
    L_0x024a:
        r12 = -NUM; // 0xfffffffvar_ float:0.0 double:NaN;
    L_0x024f:
        r4 = (r12 > r19 ? 1 : (r12 == r19 ? 0 : -1));
        if (r4 == 0) goto L_0x1689;
    L_0x0253:
        r4 = "MESSAGE_DELETED";
        r4 = r4.equals(r9);	 Catch:{ Throwable -> 0x16a0 }
        r7 = "messages";
        if (r4 == 0) goto L_0x0293;
    L_0x025d:
        r3 = r11.getString(r7);	 Catch:{ Throwable -> 0x0228 }
        r4 = ",";
        r3 = r3.split(r4);	 Catch:{ Throwable -> 0x0228 }
        r4 = new android.util.SparseArray;	 Catch:{ Throwable -> 0x0228 }
        r4.<init>();	 Catch:{ Throwable -> 0x0228 }
        r5 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x0228 }
        r5.<init>();	 Catch:{ Throwable -> 0x0228 }
    L_0x0271:
        r6 = r3.length;	 Catch:{ Throwable -> 0x0228 }
        if (r8 >= r6) goto L_0x0280;
    L_0x0274:
        r6 = r3[r8];	 Catch:{ Throwable -> 0x0228 }
        r6 = org.telegram.messenger.Utilities.parseInt(r6);	 Catch:{ Throwable -> 0x0228 }
        r5.add(r6);	 Catch:{ Throwable -> 0x0228 }
        r8 = r8 + 1;
        goto L_0x0271;
    L_0x0280:
        r4.put(r2, r5);	 Catch:{ Throwable -> 0x0228 }
        r3 = org.telegram.messenger.NotificationsController.getInstance(r15);	 Catch:{ Throwable -> 0x0228 }
        r3.removeDeletedMessagesFromNotifications(r4);	 Catch:{ Throwable -> 0x0228 }
        r3 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ Throwable -> 0x0228 }
        r3.deleteMessagesByPush(r12, r5, r2);	 Catch:{ Throwable -> 0x0228 }
        goto L_0x1689;
    L_0x0293:
        r4 = android.text.TextUtils.isEmpty(r9);	 Catch:{ Throwable -> 0x16a0 }
        if (r4 != 0) goto L_0x160f;
    L_0x0299:
        r4 = "msg_id";
        r4 = r11.has(r4);	 Catch:{ Throwable -> 0x16a0 }
        if (r4 == 0) goto L_0x02a8;
    L_0x02a1:
        r4 = "msg_id";
        r4 = r11.getInt(r4);	 Catch:{ Throwable -> 0x0228 }
        goto L_0x02a9;
    L_0x02a8:
        r4 = 0;
    L_0x02a9:
        r10 = "random_id";
        r10 = r11.has(r10);	 Catch:{ Throwable -> 0x16a0 }
        if (r10 == 0) goto L_0x02c2;
    L_0x02b1:
        r10 = "random_id";
        r10 = r11.getString(r10);	 Catch:{ Throwable -> 0x0228 }
        r10 = org.telegram.messenger.Utilities.parseLong(r10);	 Catch:{ Throwable -> 0x0228 }
        r22 = r10.longValue();	 Catch:{ Throwable -> 0x0228 }
        r28 = r22;
        goto L_0x02c4;
    L_0x02c2:
        r28 = r19;
    L_0x02c4:
        if (r4 == 0) goto L_0x030e;
    L_0x02c6:
        r10 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ Throwable -> 0x0306 }
        r10 = r10.dialogs_read_inbox_max;	 Catch:{ Throwable -> 0x0306 }
        r8 = java.lang.Long.valueOf(r12);	 Catch:{ Throwable -> 0x0306 }
        r8 = r10.get(r8);	 Catch:{ Throwable -> 0x0306 }
        r8 = (java.lang.Integer) r8;	 Catch:{ Throwable -> 0x0306 }
        if (r8 != 0) goto L_0x02f5;
    L_0x02d8:
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r15);	 Catch:{ Throwable -> 0x0306 }
        r10 = 0;
        r8 = r8.getDialogReadMax(r10, r12);	 Catch:{ Throwable -> 0x0306 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Throwable -> 0x0306 }
        r10 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ Throwable -> 0x0306 }
        r10 = r10.dialogs_read_inbox_max;	 Catch:{ Throwable -> 0x0306 }
        r30 = r14;
        r14 = java.lang.Long.valueOf(r12);	 Catch:{ Throwable -> 0x0324 }
        r10.put(r14, r8);	 Catch:{ Throwable -> 0x0324 }
        goto L_0x02f7;
    L_0x02f5:
        r30 = r14;
    L_0x02f7:
        r8 = r8.intValue();	 Catch:{ Throwable -> 0x0324 }
        if (r4 <= r8) goto L_0x02ff;
    L_0x02fd:
        r8 = 1;
        goto L_0x0300;
    L_0x02ff:
        r8 = 0;
    L_0x0300:
        r10 = r7;
        r14 = r8;
        r8 = r6;
        r6 = r28;
        goto L_0x0327;
    L_0x0306:
        r0 = move-exception;
        r30 = r14;
    L_0x0309:
        r3 = r1;
        r4 = r30;
        goto L_0x01a3;
    L_0x030e:
        r8 = r6;
        r10 = r7;
        r30 = r14;
        r6 = r28;
        r14 = (r6 > r19 ? 1 : (r6 == r19 ? 0 : -1));
        if (r14 == 0) goto L_0x0326;
    L_0x0318:
        r14 = org.telegram.messenger.MessagesStorage.getInstance(r15);	 Catch:{ Throwable -> 0x0324 }
        r14 = r14.checkMessageByRandomId(r6);	 Catch:{ Throwable -> 0x0324 }
        if (r14 != 0) goto L_0x0326;
    L_0x0322:
        r14 = 1;
        goto L_0x0327;
    L_0x0324:
        r0 = move-exception;
        goto L_0x0309;
    L_0x0326:
        r14 = 0;
    L_0x0327:
        if (r14 == 0) goto L_0x1605;
    L_0x0329:
        r14 = "chat_from_id";
        r14 = r11.has(r14);	 Catch:{ Throwable -> 0x1601 }
        if (r14 == 0) goto L_0x0338;
    L_0x0331:
        r14 = "chat_from_id";
        r14 = r11.getInt(r14);	 Catch:{ Throwable -> 0x0324 }
        goto L_0x0339;
    L_0x0338:
        r14 = 0;
    L_0x0339:
        r1 = "mention";
        r1 = r11.has(r1);	 Catch:{ Throwable -> 0x15fc }
        if (r1 == 0) goto L_0x0356;
    L_0x0341:
        r1 = "mention";
        r1 = r11.getInt(r1);	 Catch:{ Throwable -> 0x034d }
        if (r1 == 0) goto L_0x0356;
    L_0x0349:
        r28 = r15;
        r1 = 1;
        goto L_0x0359;
    L_0x034d:
        r0 = move-exception;
        r2 = -1;
        r3 = r36;
        r1 = r0;
    L_0x0352:
        r4 = r30;
        goto L_0x176f;
    L_0x0356:
        r28 = r15;
        r1 = 0;
    L_0x0359:
        r15 = "silent";
        r15 = r11.has(r15);	 Catch:{ Throwable -> 0x15f7 }
        if (r15 == 0) goto L_0x0374;
    L_0x0361:
        r15 = "silent";
        r15 = r11.getInt(r15);	 Catch:{ Throwable -> 0x036c }
        if (r15 == 0) goto L_0x0374;
    L_0x0369:
        r19 = 1;
        goto L_0x0376;
    L_0x036c:
        r0 = move-exception;
        r2 = -1;
        r3 = r36;
        r1 = r0;
        r15 = r28;
        goto L_0x0352;
    L_0x0374:
        r19 = 0;
    L_0x0376:
        r15 = "loc_args";
        r15 = r5.has(r15);	 Catch:{ Throwable -> 0x15f7 }
        if (r15 == 0) goto L_0x039d;
    L_0x037e:
        r15 = "loc_args";
        r5 = r5.getJSONArray(r15);	 Catch:{ Throwable -> 0x036c }
        r15 = r5.length();	 Catch:{ Throwable -> 0x036c }
        r15 = new java.lang.String[r15];	 Catch:{ Throwable -> 0x036c }
        r20 = r1;
        r23 = r8;
        r1 = 0;
    L_0x038f:
        r8 = r15.length;	 Catch:{ Throwable -> 0x036c }
        if (r1 >= r8) goto L_0x039b;
    L_0x0392:
        r8 = r5.getString(r1);	 Catch:{ Throwable -> 0x036c }
        r15[r1] = r8;	 Catch:{ Throwable -> 0x036c }
        r1 = r1 + 1;
        goto L_0x038f;
    L_0x039b:
        r1 = 0;
        goto L_0x03a3;
    L_0x039d:
        r20 = r1;
        r23 = r8;
        r1 = 0;
        r15 = 0;
    L_0x03a3:
        r5 = r15[r1];	 Catch:{ Throwable -> 0x15f7 }
        r1 = "edit_date";
        r27 = r11.has(r1);	 Catch:{ Throwable -> 0x15f7 }
        r1 = "CHAT_";
        r1 = r9.startsWith(r1);	 Catch:{ Throwable -> 0x15f7 }
        if (r1 == 0) goto L_0x03c3;
    L_0x03b3:
        if (r2 == 0) goto L_0x03b8;
    L_0x03b5:
        r1 = 1;
        r8 = 1;
        goto L_0x03ba;
    L_0x03b8:
        r1 = 1;
        r8 = 0;
    L_0x03ba:
        r11 = r15[r1];	 Catch:{ Throwable -> 0x036c }
        r24 = r5;
        r5 = r11;
        r1 = 0;
    L_0x03c0:
        r26 = 0;
        goto L_0x03e6;
    L_0x03c3:
        r1 = "PINNED_";
        r1 = r9.startsWith(r1);	 Catch:{ Throwable -> 0x15f7 }
        if (r1 == 0) goto L_0x03d2;
    L_0x03cb:
        if (r14 == 0) goto L_0x03cf;
    L_0x03cd:
        r8 = 1;
        goto L_0x03d0;
    L_0x03cf:
        r8 = 0;
    L_0x03d0:
        r1 = 1;
        goto L_0x03e3;
    L_0x03d2:
        r1 = "CHANNEL_";
        r1 = r9.startsWith(r1);	 Catch:{ Throwable -> 0x15f7 }
        if (r1 == 0) goto L_0x03e1;
    L_0x03da:
        r1 = 0;
        r8 = 0;
        r24 = 0;
        r26 = 1;
        goto L_0x03e6;
    L_0x03e1:
        r1 = 0;
        r8 = 0;
    L_0x03e3:
        r24 = 0;
        goto L_0x03c0;
    L_0x03e6:
        r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x15f7 }
        if (r11 == 0) goto L_0x0411;
    L_0x03ea:
        r11 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x036c }
        r11.<init>();	 Catch:{ Throwable -> 0x036c }
        r25 = r5;
        r5 = "GCM received message notification ";
        r11.append(r5);	 Catch:{ Throwable -> 0x036c }
        r11.append(r9);	 Catch:{ Throwable -> 0x036c }
        r5 = " for dialogId = ";
        r11.append(r5);	 Catch:{ Throwable -> 0x036c }
        r11.append(r12);	 Catch:{ Throwable -> 0x036c }
        r5 = " mid = ";
        r11.append(r5);	 Catch:{ Throwable -> 0x036c }
        r11.append(r4);	 Catch:{ Throwable -> 0x036c }
        r5 = r11.toString();	 Catch:{ Throwable -> 0x036c }
        org.telegram.messenger.FileLog.d(r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x0413;
    L_0x0411:
        r25 = r5;
    L_0x0413:
        r5 = r9.hashCode();	 Catch:{ Throwable -> 0x15f7 }
        switch(r5) {
            case -2100047043: goto L_0x088c;
            case -2091498420: goto L_0x0881;
            case -2053872415: goto L_0x0876;
            case -2039746363: goto L_0x086b;
            case -2023218804: goto L_0x0860;
            case -1979538588: goto L_0x0855;
            case -1979536003: goto L_0x084a;
            case -1979535888: goto L_0x083f;
            case -1969004705: goto L_0x0834;
            case -1946699248: goto L_0x0828;
            case -1528047021: goto L_0x081c;
            case -1493579426: goto L_0x0810;
            case -1480102982: goto L_0x0805;
            case -1478041834: goto L_0x07fa;
            case -1474543101: goto L_0x07ef;
            case -1465695932: goto L_0x07e3;
            case -1374906292: goto L_0x07d7;
            case -1372940586: goto L_0x07cb;
            case -1264245338: goto L_0x07bf;
            case -1236086700: goto L_0x07b3;
            case -1236077786: goto L_0x07a7;
            case -1235796237: goto L_0x079b;
            case -1235686303: goto L_0x078f;
            case -1198046100: goto L_0x0784;
            case -1124254527: goto L_0x0778;
            case -1085137927: goto L_0x076c;
            case -1084856378: goto L_0x0760;
            case -1084746444: goto L_0x0754;
            case -819729482: goto L_0x0748;
            case -772141857: goto L_0x073c;
            case -638310039: goto L_0x0730;
            case -590403924: goto L_0x0724;
            case -589196239: goto L_0x0718;
            case -589193654: goto L_0x070c;
            case -589193539: goto L_0x0700;
            case -440169325: goto L_0x06f4;
            case -412748110: goto L_0x06e8;
            case -228518075: goto L_0x06dc;
            case -213586509: goto L_0x06d0;
            case -115582002: goto L_0x06c4;
            case -112621464: goto L_0x06b8;
            case -108522133: goto L_0x06ac;
            case -107572034: goto L_0x06a1;
            case -40534265: goto L_0x0695;
            case 65254746: goto L_0x0689;
            case 141040782: goto L_0x067d;
            case 309993049: goto L_0x0671;
            case 309995634: goto L_0x0665;
            case 309995749: goto L_0x0659;
            case 320532812: goto L_0x064d;
            case 328933854: goto L_0x0641;
            case 331340546: goto L_0x0635;
            case 344816990: goto L_0x0629;
            case 346878138: goto L_0x061d;
            case 350376871: goto L_0x0611;
            case 615714517: goto L_0x0606;
            case 715508879: goto L_0x05fa;
            case 728985323: goto L_0x05ee;
            case 731046471: goto L_0x05e2;
            case 734545204: goto L_0x05d6;
            case 802032552: goto L_0x05ca;
            case 991498806: goto L_0x05be;
            case 1007364121: goto L_0x05b2;
            case 1019917311: goto L_0x05a6;
            case 1019926225: goto L_0x059a;
            case 1020207774: goto L_0x058e;
            case 1020317708: goto L_0x0582;
            case 1060349560: goto L_0x0576;
            case 1060358474: goto L_0x056a;
            case 1060640023: goto L_0x055e;
            case 1060749957: goto L_0x0553;
            case 1073049781: goto L_0x0547;
            case 1078101399: goto L_0x053b;
            case 1110103437: goto L_0x052f;
            case 1160762272: goto L_0x0523;
            case 1172918249: goto L_0x0517;
            case 1234591620: goto L_0x050b;
            case 1281128640: goto L_0x04ff;
            case 1281131225: goto L_0x04f3;
            case 1281131340: goto L_0x04e7;
            case 1310789062: goto L_0x04dc;
            case 1333118583: goto L_0x04d0;
            case 1361447897: goto L_0x04c4;
            case 1498266155: goto L_0x04b8;
            case 1533804208: goto L_0x04ac;
            case 1547988151: goto L_0x04a0;
            case 1561464595: goto L_0x0494;
            case 1563525743: goto L_0x0488;
            case 1567024476: goto L_0x047c;
            case 1810705077: goto L_0x0470;
            case 1815177512: goto L_0x0464;
            case 1963241394: goto L_0x0458;
            case 2014789757: goto L_0x044c;
            case 2022049433: goto L_0x0440;
            case 2048733346: goto L_0x0434;
            case 2099392181: goto L_0x0428;
            case 2140162142: goto L_0x041c;
            default: goto L_0x041a;
        };
    L_0x041a:
        goto L_0x0897;
    L_0x041c:
        r5 = "CHAT_MESSAGE_GEOLIVE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0424:
        r5 = 53;
        goto L_0x0898;
    L_0x0428:
        r5 = "CHANNEL_MESSAGE_PHOTOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0430:
        r5 = 39;
        goto L_0x0898;
    L_0x0434:
        r5 = "CHANNEL_MESSAGE_NOTEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x043c:
        r5 = 24;
        goto L_0x0898;
    L_0x0440:
        r5 = "PINNED_CONTACT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0448:
        r5 = 80;
        goto L_0x0898;
    L_0x044c:
        r5 = "CHAT_PHOTO_EDITED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0454:
        r5 = 60;
        goto L_0x0898;
    L_0x0458:
        r5 = "LOCKED_MESSAGE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0460:
        r5 = 94;
        goto L_0x0898;
    L_0x0464:
        r5 = "CHANNEL_MESSAGES";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x046c:
        r5 = 41;
        goto L_0x0898;
    L_0x0470:
        r5 = "MESSAGE_INVOICE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0478:
        r5 = 18;
        goto L_0x0898;
    L_0x047c:
        r5 = "CHAT_MESSAGE_VIDEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0484:
        r5 = 45;
        goto L_0x0898;
    L_0x0488:
        r5 = "CHAT_MESSAGE_ROUND";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0490:
        r5 = 46;
        goto L_0x0898;
    L_0x0494:
        r5 = "CHAT_MESSAGE_PHOTO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x049c:
        r5 = 44;
        goto L_0x0898;
    L_0x04a0:
        r5 = "CHAT_MESSAGE_AUDIO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x04a8:
        r5 = 49;
        goto L_0x0898;
    L_0x04ac:
        r5 = "MESSAGE_VIDEOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x04b4:
        r5 = 21;
        goto L_0x0898;
    L_0x04b8:
        r5 = "PHONE_CALL_MISSED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x04c0:
        r5 = 96;
        goto L_0x0898;
    L_0x04c4:
        r5 = "MESSAGE_PHOTOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x04cc:
        r5 = 20;
        goto L_0x0898;
    L_0x04d0:
        r5 = "CHAT_MESSAGE_VIDEOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x04d8:
        r5 = 70;
        goto L_0x0898;
    L_0x04dc:
        r5 = "MESSAGE_NOTEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x04e4:
        r5 = 1;
        goto L_0x0898;
    L_0x04e7:
        r5 = "MESSAGE_GIF";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x04ef:
        r5 = 15;
        goto L_0x0898;
    L_0x04f3:
        r5 = "MESSAGE_GEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x04fb:
        r5 = 13;
        goto L_0x0898;
    L_0x04ff:
        r5 = "MESSAGE_DOC";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0507:
        r5 = 8;
        goto L_0x0898;
    L_0x050b:
        r5 = "CHAT_MESSAGE_GAME_SCORE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0513:
        r5 = 56;
        goto L_0x0898;
    L_0x0517:
        r5 = "CHANNEL_MESSAGE_GEOLIVE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x051f:
        r5 = 34;
        goto L_0x0898;
    L_0x0523:
        r5 = "CHAT_MESSAGE_PHOTOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x052b:
        r5 = 69;
        goto L_0x0898;
    L_0x052f:
        r5 = "CHAT_MESSAGE_NOTEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0537:
        r5 = 43;
        goto L_0x0898;
    L_0x053b:
        r5 = "CHAT_TITLE_EDITED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0543:
        r5 = 59;
        goto L_0x0898;
    L_0x0547:
        r5 = "PINNED_NOTEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x054f:
        r5 = 73;
        goto L_0x0898;
    L_0x0553:
        r5 = "MESSAGE_TEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x055b:
        r5 = 0;
        goto L_0x0898;
    L_0x055e:
        r5 = "MESSAGE_POLL";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0566:
        r5 = 12;
        goto L_0x0898;
    L_0x056a:
        r5 = "MESSAGE_GAME";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0572:
        r5 = 16;
        goto L_0x0898;
    L_0x0576:
        r5 = "MESSAGE_FWDS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x057e:
        r5 = 19;
        goto L_0x0898;
    L_0x0582:
        r5 = "CHAT_MESSAGE_TEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x058a:
        r5 = 42;
        goto L_0x0898;
    L_0x058e:
        r5 = "CHAT_MESSAGE_POLL";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0596:
        r5 = 51;
        goto L_0x0898;
    L_0x059a:
        r5 = "CHAT_MESSAGE_GAME";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x05a2:
        r5 = 55;
        goto L_0x0898;
    L_0x05a6:
        r5 = "CHAT_MESSAGE_FWDS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x05ae:
        r5 = 68;
        goto L_0x0898;
    L_0x05b2:
        r5 = "CHANNEL_MESSAGE_GAME_SCORE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x05ba:
        r5 = 37;
        goto L_0x0898;
    L_0x05be:
        r5 = "PINNED_GEOLIVE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x05c6:
        r5 = 83;
        goto L_0x0898;
    L_0x05ca:
        r5 = "MESSAGE_CONTACT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x05d2:
        r5 = 11;
        goto L_0x0898;
    L_0x05d6:
        r5 = "PINNED_VIDEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x05de:
        r5 = 75;
        goto L_0x0898;
    L_0x05e2:
        r5 = "PINNED_ROUND";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x05ea:
        r5 = 76;
        goto L_0x0898;
    L_0x05ee:
        r5 = "PINNED_PHOTO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x05f6:
        r5 = 74;
        goto L_0x0898;
    L_0x05fa:
        r5 = "PINNED_AUDIO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0602:
        r5 = 79;
        goto L_0x0898;
    L_0x0606:
        r5 = "MESSAGE_PHOTO_SECRET";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x060e:
        r5 = 3;
        goto L_0x0898;
    L_0x0611:
        r5 = "CHANNEL_MESSAGE_VIDEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0619:
        r5 = 26;
        goto L_0x0898;
    L_0x061d:
        r5 = "CHANNEL_MESSAGE_ROUND";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0625:
        r5 = 27;
        goto L_0x0898;
    L_0x0629:
        r5 = "CHANNEL_MESSAGE_PHOTO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0631:
        r5 = 25;
        goto L_0x0898;
    L_0x0635:
        r5 = "CHANNEL_MESSAGE_AUDIO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x063d:
        r5 = 30;
        goto L_0x0898;
    L_0x0641:
        r5 = "CHAT_MESSAGE_STICKER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0649:
        r5 = 48;
        goto L_0x0898;
    L_0x064d:
        r5 = "MESSAGES";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0655:
        r5 = 22;
        goto L_0x0898;
    L_0x0659:
        r5 = "CHAT_MESSAGE_GIF";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0661:
        r5 = 54;
        goto L_0x0898;
    L_0x0665:
        r5 = "CHAT_MESSAGE_GEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x066d:
        r5 = 52;
        goto L_0x0898;
    L_0x0671:
        r5 = "CHAT_MESSAGE_DOC";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0679:
        r5 = 47;
        goto L_0x0898;
    L_0x067d:
        r5 = "CHAT_LEFT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0685:
        r5 = 65;
        goto L_0x0898;
    L_0x0689:
        r5 = "CHAT_ADD_YOU";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0691:
        r5 = 62;
        goto L_0x0898;
    L_0x0695:
        r5 = "CHAT_DELETE_MEMBER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x069d:
        r5 = 63;
        goto L_0x0898;
    L_0x06a1:
        r5 = "MESSAGE_SCREENSHOT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x06a9:
        r5 = 6;
        goto L_0x0898;
    L_0x06ac:
        r5 = "AUTH_REGION";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x06b4:
        r5 = 90;
        goto L_0x0898;
    L_0x06b8:
        r5 = "CONTACT_JOINED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x06c0:
        r5 = 88;
        goto L_0x0898;
    L_0x06c4:
        r5 = "CHAT_MESSAGE_INVOICE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x06cc:
        r5 = 57;
        goto L_0x0898;
    L_0x06d0:
        r5 = "ENCRYPTION_REQUEST";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x06d8:
        r5 = 92;
        goto L_0x0898;
    L_0x06dc:
        r5 = "MESSAGE_GEOLIVE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x06e4:
        r5 = 14;
        goto L_0x0898;
    L_0x06e8:
        r5 = "CHAT_DELETE_YOU";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x06f0:
        r5 = 64;
        goto L_0x0898;
    L_0x06f4:
        r5 = "AUTH_UNKNOWN";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x06fc:
        r5 = 89;
        goto L_0x0898;
    L_0x0700:
        r5 = "PINNED_GIF";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0708:
        r5 = 87;
        goto L_0x0898;
    L_0x070c:
        r5 = "PINNED_GEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0714:
        r5 = 82;
        goto L_0x0898;
    L_0x0718:
        r5 = "PINNED_DOC";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0720:
        r5 = 77;
        goto L_0x0898;
    L_0x0724:
        r5 = "PINNED_GAME_SCORE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x072c:
        r5 = 85;
        goto L_0x0898;
    L_0x0730:
        r5 = "CHANNEL_MESSAGE_STICKER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0738:
        r5 = 29;
        goto L_0x0898;
    L_0x073c:
        r5 = "PHONE_CALL_REQUEST";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0744:
        r5 = 95;
        goto L_0x0898;
    L_0x0748:
        r5 = "PINNED_STICKER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0750:
        r5 = 78;
        goto L_0x0898;
    L_0x0754:
        r5 = "PINNED_TEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x075c:
        r5 = 72;
        goto L_0x0898;
    L_0x0760:
        r5 = "PINNED_POLL";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0768:
        r5 = 81;
        goto L_0x0898;
    L_0x076c:
        r5 = "PINNED_GAME";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0774:
        r5 = 84;
        goto L_0x0898;
    L_0x0778:
        r5 = "CHAT_MESSAGE_CONTACT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0780:
        r5 = 50;
        goto L_0x0898;
    L_0x0784:
        r5 = "MESSAGE_VIDEO_SECRET";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x078c:
        r5 = 5;
        goto L_0x0898;
    L_0x078f:
        r5 = "CHANNEL_MESSAGE_TEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0797:
        r5 = 23;
        goto L_0x0898;
    L_0x079b:
        r5 = "CHANNEL_MESSAGE_POLL";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x07a3:
        r5 = 32;
        goto L_0x0898;
    L_0x07a7:
        r5 = "CHANNEL_MESSAGE_GAME";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x07af:
        r5 = 36;
        goto L_0x0898;
    L_0x07b3:
        r5 = "CHANNEL_MESSAGE_FWDS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x07bb:
        r5 = 38;
        goto L_0x0898;
    L_0x07bf:
        r5 = "PINNED_INVOICE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x07c7:
        r5 = 86;
        goto L_0x0898;
    L_0x07cb:
        r5 = "CHAT_RETURNED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x07d3:
        r5 = 66;
        goto L_0x0898;
    L_0x07d7:
        r5 = "ENCRYPTED_MESSAGE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x07df:
        r5 = 91;
        goto L_0x0898;
    L_0x07e3:
        r5 = "ENCRYPTION_ACCEPT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x07eb:
        r5 = 93;
        goto L_0x0898;
    L_0x07ef:
        r5 = "MESSAGE_VIDEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x07f7:
        r5 = 4;
        goto L_0x0898;
    L_0x07fa:
        r5 = "MESSAGE_ROUND";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0802:
        r5 = 7;
        goto L_0x0898;
    L_0x0805:
        r5 = "MESSAGE_PHOTO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x080d:
        r5 = 2;
        goto L_0x0898;
    L_0x0810:
        r5 = "MESSAGE_AUDIO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0818:
        r5 = 10;
        goto L_0x0898;
    L_0x081c:
        r5 = "CHAT_MESSAGES";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0824:
        r5 = 71;
        goto L_0x0898;
    L_0x0828:
        r5 = "CHAT_JOINED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0830:
        r5 = 67;
        goto L_0x0898;
    L_0x0834:
        r5 = "CHAT_ADD_MEMBER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x083c:
        r5 = 61;
        goto L_0x0898;
    L_0x083f:
        r5 = "CHANNEL_MESSAGE_GIF";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0847:
        r5 = 35;
        goto L_0x0898;
    L_0x084a:
        r5 = "CHANNEL_MESSAGE_GEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0852:
        r5 = 33;
        goto L_0x0898;
    L_0x0855:
        r5 = "CHANNEL_MESSAGE_DOC";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x085d:
        r5 = 28;
        goto L_0x0898;
    L_0x0860:
        r5 = "CHANNEL_MESSAGE_VIDEOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0868:
        r5 = 40;
        goto L_0x0898;
    L_0x086b:
        r5 = "MESSAGE_STICKER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0873:
        r5 = 9;
        goto L_0x0898;
    L_0x0876:
        r5 = "CHAT_CREATED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x087e:
        r5 = 58;
        goto L_0x0898;
    L_0x0881:
        r5 = "CHANNEL_MESSAGE_CONTACT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0889:
        r5 = 31;
        goto L_0x0898;
    L_0x088c:
        r5 = "MESSAGE_GAME_SCORE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 == 0) goto L_0x0897;
    L_0x0894:
        r5 = 17;
        goto L_0x0898;
    L_0x0897:
        r5 = -1;
    L_0x0898:
        r11 = "Photos";
        r18 = r3;
        r3 = "ChannelMessageFew";
        r31 = r2;
        r2 = " ";
        r32 = r12;
        r12 = NUM; // 0x7f0d0150 float:1.8742796E38 double:1.0531299436E-314;
        r13 = "AttachSticker";
        switch(r5) {
            case 0: goto L_0x1521;
            case 1: goto L_0x1503;
            case 2: goto L_0x14e8;
            case 3: goto L_0x14cd;
            case 4: goto L_0x14b2;
            case 5: goto L_0x1497;
            case 6: goto L_0x1483;
            case 7: goto L_0x1467;
            case 8: goto L_0x144b;
            case 9: goto L_0x13f8;
            case 10: goto L_0x13dc;
            case 11: goto L_0x13bb;
            case 12: goto L_0x139a;
            case 13: goto L_0x137e;
            case 14: goto L_0x1362;
            case 15: goto L_0x1346;
            case 16: goto L_0x1325;
            case 17: goto L_0x1308;
            case 18: goto L_0x12e7;
            case 19: goto L_0x12c3;
            case 20: goto L_0x129f;
            case 21: goto L_0x1279;
            case 22: goto L_0x1266;
            case 23: goto L_0x124c;
            case 24: goto L_0x1230;
            case 25: goto L_0x1214;
            case 26: goto L_0x11f8;
            case 27: goto L_0x11dc;
            case 28: goto L_0x11c0;
            case 29: goto L_0x116d;
            case 30: goto L_0x1151;
            case 31: goto L_0x1130;
            case 32: goto L_0x110f;
            case 33: goto L_0x10f3;
            case 34: goto L_0x10d7;
            case 35: goto L_0x10bb;
            case 36: goto L_0x109f;
            case 37: goto L_0x1082;
            case 38: goto L_0x105a;
            case 39: goto L_0x1038;
            case 40: goto L_0x1014;
            case 41: goto L_0x1001;
            case 42: goto L_0x0fe2;
            case 43: goto L_0x0fc1;
            case 44: goto L_0x0fa0;
            case 45: goto L_0x0f7f;
            case 46: goto L_0x0f5e;
            case 47: goto L_0x0f3d;
            case 48: goto L_0x0eca;
            case 49: goto L_0x0ea9;
            case 50: goto L_0x0e83;
            case 51: goto L_0x0e5d;
            case 52: goto L_0x0e3c;
            case 53: goto L_0x0e1b;
            case 54: goto L_0x0dfa;
            case 55: goto L_0x0dd4;
            case 56: goto L_0x0db2;
            case 57: goto L_0x0d8c;
            case 58: goto L_0x0d74;
            case 59: goto L_0x0d5c;
            case 60: goto L_0x0d44;
            case 61: goto L_0x0d27;
            case 62: goto L_0x0d0f;
            case 63: goto L_0x0cf7;
            case 64: goto L_0x0cdf;
            case 65: goto L_0x0cc7;
            case 66: goto L_0x0caf;
            case 67: goto L_0x0CLASSNAME;
            case 68: goto L_0x0c6e;
            case 69: goto L_0x0CLASSNAME;
            case 70: goto L_0x0c1a;
            case 71: goto L_0x0CLASSNAME;
            case 72: goto L_0x0bcb;
            case 73: goto L_0x0b9e;
            case 74: goto L_0x0b71;
            case 75: goto L_0x0b44;
            case 76: goto L_0x0b17;
            case 77: goto L_0x0aea;
            case 78: goto L_0x0a70;
            case 79: goto L_0x0a43;
            case 80: goto L_0x0a0c;
            case 81: goto L_0x09d5;
            case 82: goto L_0x09a8;
            case 83: goto L_0x097b;
            case 84: goto L_0x094e;
            case 85: goto L_0x0921;
            case 86: goto L_0x08f4;
            case 87: goto L_0x08c7;
            case 88: goto L_0x1550;
            case 89: goto L_0x1550;
            case 90: goto L_0x1550;
            case 91: goto L_0x08b0;
            case 92: goto L_0x1550;
            case 93: goto L_0x1550;
            case 94: goto L_0x1550;
            case 95: goto L_0x1550;
            case 96: goto L_0x1550;
            default: goto L_0x08ac;
        };
    L_0x08ac:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x15f7 }
        goto L_0x153a;
    L_0x08b0:
        r2 = "YouHaveNewMessage";
        r3 = NUM; // 0x7f0d0b34 float:1.8747932E38 double:1.0531311945E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x036c }
        r3 = "SecretChatName";
        r5 = NUM; // 0x7f0d0944 float:1.8746926E38 double:1.0531309495E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        r25 = r3;
    L_0x08c4:
        r3 = 1;
        goto L_0x1552;
    L_0x08c7:
        if (r14 == 0) goto L_0x08e1;
    L_0x08c9:
        r2 = "NotificationActionPinnedGif";
        r3 = NUM; // 0x7f0d066e float:1.8745453E38 double:1.053130591E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x08e1:
        r2 = "NotificationActionPinnedGifChannel";
        r3 = NUM; // 0x7f0d066f float:1.8745455E38 double:1.0531305913E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x08f4:
        if (r14 == 0) goto L_0x090e;
    L_0x08f6:
        r2 = "NotificationActionPinnedInvoice";
        r3 = NUM; // 0x7f0d0670 float:1.8745457E38 double:1.053130592E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x090e:
        r2 = "NotificationActionPinnedInvoiceChannel";
        r3 = NUM; // 0x7f0d0671 float:1.874546E38 double:1.0531305923E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0921:
        if (r14 == 0) goto L_0x093b;
    L_0x0923:
        r2 = "NotificationActionPinnedGameScore";
        r3 = NUM; // 0x7f0d0668 float:1.8745441E38 double:1.053130588E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x093b:
        r2 = "NotificationActionPinnedGameScoreChannel";
        r3 = NUM; // 0x7f0d0669 float:1.8745443E38 double:1.0531305883E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x094e:
        if (r14 == 0) goto L_0x0968;
    L_0x0950:
        r2 = "NotificationActionPinnedGame";
        r3 = NUM; // 0x7f0d0666 float:1.8745437E38 double:1.053130587E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0968:
        r2 = "NotificationActionPinnedGameChannel";
        r3 = NUM; // 0x7f0d0667 float:1.874544E38 double:1.0531305873E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x097b:
        if (r14 == 0) goto L_0x0995;
    L_0x097d:
        r2 = "NotificationActionPinnedGeoLive";
        r3 = NUM; // 0x7f0d066c float:1.874545E38 double:1.05313059E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0995:
        r2 = "NotificationActionPinnedGeoLiveChannel";
        r3 = NUM; // 0x7f0d066d float:1.8745451E38 double:1.0531305903E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x09a8:
        if (r14 == 0) goto L_0x09c2;
    L_0x09aa:
        r2 = "NotificationActionPinnedGeo";
        r3 = NUM; // 0x7f0d066a float:1.8745445E38 double:1.053130589E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x09c2:
        r2 = "NotificationActionPinnedGeoChannel";
        r3 = NUM; // 0x7f0d066b float:1.8745447E38 double:1.0531305893E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x09d5:
        if (r14 == 0) goto L_0x09f4;
    L_0x09d7:
        r2 = "NotificationActionPinnedPoll2";
        r3 = NUM; // 0x7f0d0678 float:1.8745474E38 double:1.0531305957E-314;
        r5 = 3;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x09f4:
        r2 = "NotificationActionPinnedPollChannel2";
        r3 = NUM; // 0x7f0d0679 float:1.8745476E38 double:1.053130596E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0a0c:
        if (r14 == 0) goto L_0x0a2b;
    L_0x0a0e:
        r3 = "NotificationActionPinnedContact2";
        r5 = NUM; // 0x7f0d0662 float:1.8745429E38 double:1.053130585E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0a2b:
        r2 = "NotificationActionPinnedContactChannel2";
        r3 = NUM; // 0x7f0d0663 float:1.874543E38 double:1.0531305853E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0a43:
        if (r14 == 0) goto L_0x0a5d;
    L_0x0a45:
        r2 = "NotificationActionPinnedVoice";
        r3 = NUM; // 0x7f0d0684 float:1.8745498E38 double:1.0531306016E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0a5d:
        r2 = "NotificationActionPinnedVoiceChannel";
        r3 = NUM; // 0x7f0d0685 float:1.87455E38 double:1.053130602E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0a70:
        if (r14 == 0) goto L_0x0ab3;
    L_0x0a72:
        r3 = r15.length;	 Catch:{ Throwable -> 0x036c }
        r5 = 2;
        if (r3 <= r5) goto L_0x0a9b;
    L_0x0a76:
        r3 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r3 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Throwable -> 0x036c }
        if (r3 != 0) goto L_0x0a9b;
    L_0x0a7e:
        r3 = "NotificationActionPinnedStickerEmoji";
        r5 = NUM; // 0x7f0d067e float:1.8745486E38 double:1.0531305987E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0a9b:
        r2 = "NotificationActionPinnedSticker";
        r3 = NUM; // 0x7f0d067c float:1.8745482E38 double:1.0531305977E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0ab3:
        r2 = r15.length;	 Catch:{ Throwable -> 0x036c }
        r3 = 1;
        if (r2 <= r3) goto L_0x0ad7;
    L_0x0ab7:
        r2 = r15[r3];	 Catch:{ Throwable -> 0x036c }
        r2 = android.text.TextUtils.isEmpty(r2);	 Catch:{ Throwable -> 0x036c }
        if (r2 != 0) goto L_0x0ad7;
    L_0x0abf:
        r2 = "NotificationActionPinnedStickerEmojiChannel";
        r3 = NUM; // 0x7f0d067f float:1.8745488E38 double:1.053130599E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0ad7:
        r2 = "NotificationActionPinnedStickerChannel";
        r3 = NUM; // 0x7f0d067d float:1.8745484E38 double:1.053130598E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0aea:
        if (r14 == 0) goto L_0x0b04;
    L_0x0aec:
        r2 = "NotificationActionPinnedFile";
        r3 = NUM; // 0x7f0d0664 float:1.8745433E38 double:1.053130586E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0b04:
        r2 = "NotificationActionPinnedFileChannel";
        r3 = NUM; // 0x7f0d0665 float:1.8745435E38 double:1.0531305863E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0b17:
        if (r14 == 0) goto L_0x0b31;
    L_0x0b19:
        r2 = "NotificationActionPinnedRound";
        r3 = NUM; // 0x7f0d067a float:1.8745478E38 double:1.0531305967E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0b31:
        r2 = "NotificationActionPinnedRoundChannel";
        r3 = NUM; // 0x7f0d067b float:1.874548E38 double:1.053130597E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0b44:
        if (r14 == 0) goto L_0x0b5e;
    L_0x0b46:
        r2 = "NotificationActionPinnedVideo";
        r3 = NUM; // 0x7f0d0682 float:1.8745494E38 double:1.0531306007E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0b5e:
        r2 = "NotificationActionPinnedVideoChannel";
        r3 = NUM; // 0x7f0d0683 float:1.8745496E38 double:1.053130601E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0b71:
        if (r14 == 0) goto L_0x0b8b;
    L_0x0b73:
        r2 = "NotificationActionPinnedPhoto";
        r3 = NUM; // 0x7f0d0676 float:1.874547E38 double:1.0531305947E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0b8b:
        r2 = "NotificationActionPinnedPhotoChannel";
        r3 = NUM; // 0x7f0d0677 float:1.8745471E38 double:1.053130595E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0b9e:
        if (r14 == 0) goto L_0x0bb8;
    L_0x0ba0:
        r2 = "NotificationActionPinnedNoText";
        r3 = NUM; // 0x7f0d0674 float:1.8745465E38 double:1.0531305937E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0bb8:
        r2 = "NotificationActionPinnedNoTextChannel";
        r3 = NUM; // 0x7f0d0675 float:1.8745467E38 double:1.053130594E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0bcb:
        if (r14 == 0) goto L_0x0bea;
    L_0x0bcd:
        r3 = "NotificationActionPinnedText";
        r5 = NUM; // 0x7f0d0680 float:1.874549E38 double:1.0531305997E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0bea:
        r2 = "NotificationActionPinnedTextChannel";
        r3 = NUM; // 0x7f0d0681 float:1.8745492E38 double:1.0531306E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0CLASSNAME:
        r2 = "NotificationGroupAlbum";
        r3 = NUM; // 0x7f0d068d float:1.8745516E38 double:1.053130606E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x08c4;
    L_0x0c1a:
        r3 = "NotificationGroupFew";
        r5 = NUM; // 0x7f0d068e float:1.8745518E38 double:1.0531306066E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = "Videos";
        r11 = 2;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r12 = org.telegram.messenger.Utilities.parseInt(r12);	 Catch:{ Throwable -> 0x036c }
        r12 = r12.intValue();	 Catch:{ Throwable -> 0x036c }
        r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r12);	 Catch:{ Throwable -> 0x036c }
        r2[r11] = r10;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036c }
        goto L_0x08c4;
    L_0x0CLASSNAME:
        r3 = "NotificationGroupFew";
        r5 = NUM; // 0x7f0d068e float:1.8745518E38 double:1.0531306066E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r12 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r12;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r12 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r12;	 Catch:{ Throwable -> 0x036c }
        r10 = 2;
        r12 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r12 = org.telegram.messenger.Utilities.parseInt(r12);	 Catch:{ Throwable -> 0x036c }
        r12 = r12.intValue();	 Catch:{ Throwable -> 0x036c }
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r12);	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036c }
        goto L_0x08c4;
    L_0x0c6e:
        r3 = "NotificationGroupForwardedFew";
        r5 = NUM; // 0x7f0d068f float:1.874552E38 double:1.053130607E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036c }
        r11 = 0;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r2[r11] = r12;	 Catch:{ Throwable -> 0x036c }
        r11 = 1;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r2[r11] = r12;	 Catch:{ Throwable -> 0x036c }
        r11 = 2;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r12 = org.telegram.messenger.Utilities.parseInt(r12);	 Catch:{ Throwable -> 0x036c }
        r12 = r12.intValue();	 Catch:{ Throwable -> 0x036c }
        r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r12);	 Catch:{ Throwable -> 0x036c }
        r2[r11] = r10;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036c }
        goto L_0x08c4;
    L_0x0CLASSNAME:
        r2 = "NotificationGroupAddSelfMega";
        r3 = NUM; // 0x7f0d068c float:1.8745514E38 double:1.0531306056E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0caf:
        r2 = "NotificationGroupAddSelf";
        r3 = NUM; // 0x7f0d068b float:1.8745512E38 double:1.053130605E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0cc7:
        r2 = "NotificationGroupLeftMember";
        r3 = NUM; // 0x7f0d0692 float:1.8745526E38 double:1.0531306086E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0cdf:
        r2 = "NotificationGroupKickYou";
        r3 = NUM; // 0x7f0d0691 float:1.8745524E38 double:1.053130608E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0cf7:
        r2 = "NotificationGroupKickMember";
        r3 = NUM; // 0x7f0d0690 float:1.8745522E38 double:1.0531306076E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0d0f:
        r2 = "NotificationInvitedToGroup";
        r3 = NUM; // 0x7f0d0693 float:1.8745528E38 double:1.053130609E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0d27:
        r3 = "NotificationGroupAddMember";
        r5 = NUM; // 0x7f0d068a float:1.874551E38 double:1.0531306046E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0d44:
        r2 = "NotificationEditedGroupPhoto";
        r3 = NUM; // 0x7f0d0689 float:1.8745508E38 double:1.053130604E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0d5c:
        r2 = "NotificationEditedGroupName";
        r3 = NUM; // 0x7f0d0688 float:1.8745506E38 double:1.0531306036E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0d74:
        r2 = "NotificationInvitedToGroup";
        r3 = NUM; // 0x7f0d0693 float:1.8745528E38 double:1.053130609E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0d8c:
        r3 = "NotificationMessageGroupInvoice";
        r5 = NUM; // 0x7f0d06a4 float:1.8745563E38 double:1.0531306175E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036c }
        r3 = "PaymentInvoice";
        r5 = NUM; // 0x7f0d0804 float:1.8746277E38 double:1.0531307914E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x0db2:
        r3 = "NotificationMessageGroupGameScored";
        r5 = NUM; // 0x7f0d06a2 float:1.8745559E38 double:1.0531306165E-314;
        r10 = 4;
        r10 = new java.lang.Object[r10];	 Catch:{ Throwable -> 0x036c }
        r11 = 0;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r10[r11] = r12;	 Catch:{ Throwable -> 0x036c }
        r11 = 1;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r10[r11] = r12;	 Catch:{ Throwable -> 0x036c }
        r11 = 2;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r10[r11] = r12;	 Catch:{ Throwable -> 0x036c }
        r2 = 3;
        r11 = r15[r2];	 Catch:{ Throwable -> 0x036c }
        r10[r2] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r10);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x0dd4:
        r3 = "NotificationMessageGroupGame";
        r5 = NUM; // 0x7f0d06a1 float:1.8745557E38 double:1.053130616E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachGame";
        r5 = NUM; // 0x7f0d0141 float:1.8742766E38 double:1.053129936E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x0dfa:
        r2 = "NotificationMessageGroupGif";
        r3 = NUM; // 0x7f0d06a3 float:1.874556E38 double:1.053130617E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachGif";
        r5 = NUM; // 0x7f0d0142 float:1.8742768E38 double:1.0531299366E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x0e1b:
        r2 = "NotificationMessageGroupLiveLocation";
        r3 = NUM; // 0x7f0d06a5 float:1.8745565E38 double:1.053130618E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachLiveLocation";
        r5 = NUM; // 0x7f0d0147 float:1.8742778E38 double:1.053129939E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x0e3c:
        r2 = "NotificationMessageGroupMap";
        r3 = NUM; // 0x7f0d06a6 float:1.8745567E38 double:1.0531306184E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachLocation";
        r5 = NUM; // 0x7f0d0149 float:1.8742782E38 double:1.05312994E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x0e5d:
        r3 = "NotificationMessageGroupPoll2";
        r5 = NUM; // 0x7f0d06aa float:1.8745575E38 double:1.0531306204E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036c }
        r3 = "Poll";
        r5 = NUM; // 0x7f0d0871 float:1.8746498E38 double:1.053130845E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x0e83:
        r3 = "NotificationMessageGroupContact2";
        r5 = NUM; // 0x7f0d069f float:1.8745553E38 double:1.053130615E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachContact";
        r5 = NUM; // 0x7f0d013d float:1.8742758E38 double:1.053129934E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x0ea9:
        r2 = "NotificationMessageGroupAudio";
        r3 = NUM; // 0x7f0d069e float:1.874555E38 double:1.0531306145E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachAudio";
        r5 = NUM; // 0x7f0d013b float:1.8742754E38 double:1.053129933E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x0eca:
        r5 = r15.length;	 Catch:{ Throwable -> 0x036c }
        r10 = 2;
        if (r5 <= r10) goto L_0x0f0a;
    L_0x0ece:
        r5 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5 = android.text.TextUtils.isEmpty(r5);	 Catch:{ Throwable -> 0x036c }
        if (r5 != 0) goto L_0x0f0a;
    L_0x0ed6:
        r5 = "NotificationMessageGroupStickerEmoji";
        r10 = NUM; // 0x7f0d06ad float:1.874558E38 double:1.053130622E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x036c }
        r11 = 0;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r3[r11] = r16;	 Catch:{ Throwable -> 0x036c }
        r11 = 1;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r3[r11] = r16;	 Catch:{ Throwable -> 0x036c }
        r11 = 2;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r3[r11] = r16;	 Catch:{ Throwable -> 0x036c }
        r3 = org.telegram.messenger.LocaleController.formatString(r5, r10, r3);	 Catch:{ Throwable -> 0x036c }
        r5 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x036c }
        r5.<init>();	 Catch:{ Throwable -> 0x036c }
        r10 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r5.append(r10);	 Catch:{ Throwable -> 0x036c }
        r5.append(r2);	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.getString(r13, r12);	 Catch:{ Throwable -> 0x036c }
        r5.append(r2);	 Catch:{ Throwable -> 0x036c }
        r2 = r5.toString();	 Catch:{ Throwable -> 0x036c }
        goto L_0x0var_;
    L_0x0f0a:
        r3 = "NotificationMessageGroupSticker";
        r5 = NUM; // 0x7f0d06ac float:1.8745579E38 double:1.0531306214E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ Throwable -> 0x036c }
        r11 = 0;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r10[r11] = r16;	 Catch:{ Throwable -> 0x036c }
        r11 = 1;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r10[r11] = r16;	 Catch:{ Throwable -> 0x036c }
        r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r10);	 Catch:{ Throwable -> 0x036c }
        r5 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x036c }
        r5.<init>();	 Catch:{ Throwable -> 0x036c }
        r10 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r5.append(r10);	 Catch:{ Throwable -> 0x036c }
        r5.append(r2);	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.getString(r13, r12);	 Catch:{ Throwable -> 0x036c }
        r5.append(r2);	 Catch:{ Throwable -> 0x036c }
        r2 = r5.toString();	 Catch:{ Throwable -> 0x036c }
    L_0x0var_:
        r16 = r2;
        r2 = r3;
        goto L_0x151f;
    L_0x0f3d:
        r2 = "NotificationMessageGroupDocument";
        r3 = NUM; // 0x7f0d06a0 float:1.8745555E38 double:1.0531306155E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachDocument";
        r5 = NUM; // 0x7f0d0140 float:1.8742764E38 double:1.0531299356E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x0f5e:
        r2 = "NotificationMessageGroupRound";
        r3 = NUM; // 0x7f0d06ab float:1.8745577E38 double:1.053130621E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachRound";
        r5 = NUM; // 0x7f0d014f float:1.8742794E38 double:1.053129943E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x0f7f:
        r2 = "NotificationMessageGroupVideo";
        r3 = NUM; // 0x7f0d06af float:1.8745585E38 double:1.053130623E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachVideo";
        r5 = NUM; // 0x7f0d0153 float:1.8742802E38 double:1.053129945E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x0fa0:
        r2 = "NotificationMessageGroupPhoto";
        r3 = NUM; // 0x7f0d06a9 float:1.8745573E38 double:1.05313062E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachPhoto";
        r5 = NUM; // 0x7f0d014d float:1.874279E38 double:1.053129942E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x0fc1:
        r2 = "NotificationMessageGroupNoText";
        r3 = NUM; // 0x7f0d06a8 float:1.874557E38 double:1.0531306194E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        r3 = "Message";
        r5 = NUM; // 0x7f0d05e4 float:1.8745173E38 double:1.0531305226E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x0fe2:
        r3 = "NotificationMessageGroupText";
        r5 = NUM; // 0x7f0d06ae float:1.8745583E38 double:1.0531306224E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036c }
        r3 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x1001:
        r2 = "ChannelMessageAlbum";
        r3 = NUM; // 0x7f0d0244 float:1.8743291E38 double:1.053130064E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        goto L_0x08c4;
    L_0x1014:
        r2 = 2;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r10 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r2[r5] = r10;	 Catch:{ Throwable -> 0x036c }
        r5 = "Videos";
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ Throwable -> 0x036c }
        r11 = r11.intValue();	 Catch:{ Throwable -> 0x036c }
        r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r11);	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r5;	 Catch:{ Throwable -> 0x036c }
        r5 = NUM; // 0x7f0d0248 float:1.87433E38 double:1.053130066E-314;
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036c }
        goto L_0x08c4;
    L_0x1038:
        r2 = 2;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r10 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r2[r5] = r10;	 Catch:{ Throwable -> 0x036c }
        r5 = 1;
        r10 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = org.telegram.messenger.Utilities.parseInt(r10);	 Catch:{ Throwable -> 0x036c }
        r10 = r10.intValue();	 Catch:{ Throwable -> 0x036c }
        r10 = org.telegram.messenger.LocaleController.formatPluralString(r11, r10);	 Catch:{ Throwable -> 0x036c }
        r2[r5] = r10;	 Catch:{ Throwable -> 0x036c }
        r5 = NUM; // 0x7f0d0248 float:1.87433E38 double:1.053130066E-314;
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036c }
        goto L_0x08c4;
    L_0x105a:
        r2 = 2;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r10 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r2[r5] = r10;	 Catch:{ Throwable -> 0x036c }
        r5 = "ForwardedMessageCount";
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ Throwable -> 0x036c }
        r11 = r11.intValue();	 Catch:{ Throwable -> 0x036c }
        r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r11);	 Catch:{ Throwable -> 0x036c }
        r5 = r5.toLowerCase();	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r5;	 Catch:{ Throwable -> 0x036c }
        r5 = NUM; // 0x7f0d0248 float:1.87433E38 double:1.053130066E-314;
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036c }
        goto L_0x08c4;
    L_0x1082:
        r3 = "NotificationMessageGameScored";
        r5 = NUM; // 0x7f0d069c float:1.8745547E38 double:1.0531306135E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x109f:
        r2 = "NotificationMessageGame";
        r3 = NUM; // 0x7f0d069b float:1.8745544E38 double:1.053130613E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachGame";
        r5 = NUM; // 0x7f0d0141 float:1.8742766E38 double:1.053129936E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x10bb:
        r2 = "ChannelMessageGIF";
        r3 = NUM; // 0x7f0d0249 float:1.8743301E38 double:1.0531300666E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachGif";
        r5 = NUM; // 0x7f0d0142 float:1.8742768E38 double:1.0531299366E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x10d7:
        r2 = "ChannelMessageLiveLocation";
        r3 = NUM; // 0x7f0d024a float:1.8743303E38 double:1.053130067E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachLiveLocation";
        r5 = NUM; // 0x7f0d0147 float:1.8742778E38 double:1.053129939E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x10f3:
        r2 = "ChannelMessageMap";
        r3 = NUM; // 0x7f0d024b float:1.8743305E38 double:1.0531300676E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachLocation";
        r5 = NUM; // 0x7f0d0149 float:1.8742782E38 double:1.05312994E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x110f:
        r2 = "ChannelMessagePoll2";
        r3 = NUM; // 0x7f0d024f float:1.8743313E38 double:1.0531300695E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        r3 = "Poll";
        r5 = NUM; // 0x7f0d0871 float:1.8746498E38 double:1.053130845E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x1130:
        r2 = "ChannelMessageContact2";
        r3 = NUM; // 0x7f0d0246 float:1.8743295E38 double:1.053130065E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachContact";
        r5 = NUM; // 0x7f0d013d float:1.8742758E38 double:1.053129934E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x1151:
        r2 = "ChannelMessageAudio";
        r3 = NUM; // 0x7f0d0245 float:1.8743293E38 double:1.0531300646E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachAudio";
        r5 = NUM; // 0x7f0d013b float:1.8742754E38 double:1.053129933E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x116d:
        r3 = r15.length;	 Catch:{ Throwable -> 0x036c }
        r5 = 1;
        if (r3 <= r5) goto L_0x11a9;
    L_0x1171:
        r3 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r3 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Throwable -> 0x036c }
        if (r3 != 0) goto L_0x11a9;
    L_0x1179:
        r3 = "ChannelMessageStickerEmoji";
        r5 = NUM; // 0x7f0d0252 float:1.874332E38 double:1.053130071E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ Throwable -> 0x036c }
        r11 = 0;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r10[r11] = r16;	 Catch:{ Throwable -> 0x036c }
        r11 = 1;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r10[r11] = r16;	 Catch:{ Throwable -> 0x036c }
        r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r10);	 Catch:{ Throwable -> 0x036c }
        r5 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x036c }
        r5.<init>();	 Catch:{ Throwable -> 0x036c }
        r10 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r5.append(r10);	 Catch:{ Throwable -> 0x036c }
        r5.append(r2);	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.getString(r13, r12);	 Catch:{ Throwable -> 0x036c }
        r5.append(r2);	 Catch:{ Throwable -> 0x036c }
        r2 = r5.toString();	 Catch:{ Throwable -> 0x036c }
        goto L_0x0var_;
    L_0x11a9:
        r2 = "ChannelMessageSticker";
        r3 = NUM; // 0x7f0d0251 float:1.8743317E38 double:1.0531300705E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = org.telegram.messenger.LocaleController.getString(r13, r12);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x11c0:
        r2 = "ChannelMessageDocument";
        r3 = NUM; // 0x7f0d0247 float:1.8743297E38 double:1.0531300656E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachDocument";
        r5 = NUM; // 0x7f0d0140 float:1.8742764E38 double:1.0531299356E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x11dc:
        r2 = "ChannelMessageRound";
        r3 = NUM; // 0x7f0d0250 float:1.8743315E38 double:1.05313007E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachRound";
        r5 = NUM; // 0x7f0d014f float:1.8742794E38 double:1.053129943E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x11f8:
        r2 = "ChannelMessageVideo";
        r3 = NUM; // 0x7f0d0253 float:1.8743322E38 double:1.0531300715E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachVideo";
        r5 = NUM; // 0x7f0d0153 float:1.8742802E38 double:1.053129945E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x1214:
        r2 = "ChannelMessagePhoto";
        r3 = NUM; // 0x7f0d024e float:1.8743311E38 double:1.053130069E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachPhoto";
        r5 = NUM; // 0x7f0d014d float:1.874279E38 double:1.053129942E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x1230:
        r2 = "ChannelMessageNoText";
        r3 = NUM; // 0x7f0d024d float:1.874331E38 double:1.0531300685E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "Message";
        r5 = NUM; // 0x7f0d05e4 float:1.8745173E38 double:1.0531305226E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x124c:
        r2 = "NotificationMessageText";
        r3 = NUM; // 0x7f0d06bc float:1.8745611E38 double:1.0531306293E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        r3 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x1266:
        r2 = "NotificationMessageAlbum";
        r3 = NUM; // 0x7f0d0695 float:1.8745532E38 double:1.05313061E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        goto L_0x08c4;
    L_0x1279:
        r2 = "NotificationMessageFew";
        r3 = NUM; // 0x7f0d0699 float:1.874554E38 double:1.053130612E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = "Videos";
        r11 = 1;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r12 = org.telegram.messenger.Utilities.parseInt(r12);	 Catch:{ Throwable -> 0x036c }
        r12 = r12.intValue();	 Catch:{ Throwable -> 0x036c }
        r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r12);	 Catch:{ Throwable -> 0x036c }
        r5[r11] = r10;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x08c4;
    L_0x129f:
        r2 = "NotificationMessageFew";
        r3 = NUM; // 0x7f0d0699 float:1.874554E38 double:1.053130612E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r12 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r12;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r12 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r12 = org.telegram.messenger.Utilities.parseInt(r12);	 Catch:{ Throwable -> 0x036c }
        r12 = r12.intValue();	 Catch:{ Throwable -> 0x036c }
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r12);	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x08c4;
    L_0x12c3:
        r2 = "NotificationMessageForwardFew";
        r3 = NUM; // 0x7f0d069a float:1.8745542E38 double:1.0531306125E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r11 = 0;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r5[r11] = r12;	 Catch:{ Throwable -> 0x036c }
        r11 = 1;
        r12 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r12 = org.telegram.messenger.Utilities.parseInt(r12);	 Catch:{ Throwable -> 0x036c }
        r12 = r12.intValue();	 Catch:{ Throwable -> 0x036c }
        r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r12);	 Catch:{ Throwable -> 0x036c }
        r5[r11] = r10;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x08c4;
    L_0x12e7:
        r2 = "NotificationMessageInvoice";
        r3 = NUM; // 0x7f0d06b0 float:1.8745587E38 double:1.0531306234E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        r3 = "PaymentInvoice";
        r5 = NUM; // 0x7f0d0804 float:1.8746277E38 double:1.0531307914E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x1308:
        r3 = "NotificationMessageGameScored";
        r5 = NUM; // 0x7f0d069c float:1.8745547E38 double:1.0531306135E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r2[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r5, r2);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x1325:
        r2 = "NotificationMessageGame";
        r3 = NUM; // 0x7f0d069b float:1.8745544E38 double:1.053130613E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachGame";
        r5 = NUM; // 0x7f0d0141 float:1.8742766E38 double:1.053129936E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x1346:
        r2 = "NotificationMessageGif";
        r3 = NUM; // 0x7f0d069d float:1.8745549E38 double:1.053130614E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachGif";
        r5 = NUM; // 0x7f0d0142 float:1.8742768E38 double:1.0531299366E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x1362:
        r2 = "NotificationMessageLiveLocation";
        r3 = NUM; // 0x7f0d06b1 float:1.874559E38 double:1.053130624E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachLiveLocation";
        r5 = NUM; // 0x7f0d0147 float:1.8742778E38 double:1.053129939E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x137e:
        r2 = "NotificationMessageMap";
        r3 = NUM; // 0x7f0d06b2 float:1.8745591E38 double:1.0531306244E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachLocation";
        r5 = NUM; // 0x7f0d0149 float:1.8742782E38 double:1.05312994E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x139a:
        r2 = "NotificationMessagePoll2";
        r3 = NUM; // 0x7f0d06b6 float:1.87456E38 double:1.0531306263E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        r3 = "Poll";
        r5 = NUM; // 0x7f0d0871 float:1.8746498E38 double:1.053130845E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x13bb:
        r2 = "NotificationMessageContact2";
        r3 = NUM; // 0x7f0d0697 float:1.8745536E38 double:1.053130611E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachContact";
        r5 = NUM; // 0x7f0d013d float:1.8742758E38 double:1.053129934E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x13dc:
        r2 = "NotificationMessageAudio";
        r3 = NUM; // 0x7f0d0696 float:1.8745534E38 double:1.0531306105E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachAudio";
        r5 = NUM; // 0x7f0d013b float:1.8742754E38 double:1.053129933E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x13f8:
        r3 = r15.length;	 Catch:{ Throwable -> 0x036c }
        r5 = 1;
        if (r3 <= r5) goto L_0x1434;
    L_0x13fc:
        r3 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r3 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Throwable -> 0x036c }
        if (r3 != 0) goto L_0x1434;
    L_0x1404:
        r3 = "NotificationMessageStickerEmoji";
        r5 = NUM; // 0x7f0d06bb float:1.874561E38 double:1.053130629E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ Throwable -> 0x036c }
        r11 = 0;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r10[r11] = r16;	 Catch:{ Throwable -> 0x036c }
        r11 = 1;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r10[r11] = r16;	 Catch:{ Throwable -> 0x036c }
        r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r10);	 Catch:{ Throwable -> 0x036c }
        r5 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x036c }
        r5.<init>();	 Catch:{ Throwable -> 0x036c }
        r10 = r15[r11];	 Catch:{ Throwable -> 0x036c }
        r5.append(r10);	 Catch:{ Throwable -> 0x036c }
        r5.append(r2);	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.getString(r13, r12);	 Catch:{ Throwable -> 0x036c }
        r5.append(r2);	 Catch:{ Throwable -> 0x036c }
        r2 = r5.toString();	 Catch:{ Throwable -> 0x036c }
        goto L_0x0var_;
    L_0x1434:
        r2 = "NotificationMessageSticker";
        r3 = NUM; // 0x7f0d06ba float:1.8745607E38 double:1.0531306283E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = org.telegram.messenger.LocaleController.getString(r13, r12);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x144b:
        r2 = "NotificationMessageDocument";
        r3 = NUM; // 0x7f0d0698 float:1.8745538E38 double:1.0531306115E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachDocument";
        r5 = NUM; // 0x7f0d0140 float:1.8742764E38 double:1.0531299356E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x1467:
        r2 = "NotificationMessageRound";
        r3 = NUM; // 0x7f0d06b7 float:1.8745601E38 double:1.053130627E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachRound";
        r5 = NUM; // 0x7f0d014f float:1.8742794E38 double:1.053129943E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x1483:
        r2 = "ActionTakeScreenshoot";
        r3 = NUM; // 0x7f0d008e float:1.8742403E38 double:1.0531298477E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x036c }
        r3 = "un1";
        r5 = 0;
        r10 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r2 = r2.replace(r3, r10);	 Catch:{ Throwable -> 0x036c }
        goto L_0x1551;
    L_0x1497:
        r2 = "NotificationMessageSDVideo";
        r3 = NUM; // 0x7f0d06b9 float:1.8745605E38 double:1.053130628E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachDestructingVideo";
        r5 = NUM; // 0x7f0d013f float:1.8742762E38 double:1.053129935E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x14b2:
        r2 = "NotificationMessageVideo";
        r3 = NUM; // 0x7f0d06bd float:1.8745613E38 double:1.05313063E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachVideo";
        r5 = NUM; // 0x7f0d0153 float:1.8742802E38 double:1.053129945E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x14cd:
        r2 = "NotificationMessageSDPhoto";
        r3 = NUM; // 0x7f0d06b8 float:1.8745603E38 double:1.0531306273E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachDestructingPhoto";
        r5 = NUM; // 0x7f0d013e float:1.874276E38 double:1.0531299347E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x14e8:
        r2 = "NotificationMessagePhoto";
        r3 = NUM; // 0x7f0d06b5 float:1.8745597E38 double:1.053130626E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "AttachPhoto";
        r5 = NUM; // 0x7f0d014d float:1.874279E38 double:1.053129942E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x1503:
        r2 = "NotificationMessageNoText";
        r3 = NUM; // 0x7f0d06b4 float:1.8745595E38 double:1.0531306254E-314;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x036c }
        r10[r5] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x036c }
        r3 = "Message";
        r5 = NUM; // 0x7f0d05e4 float:1.8745173E38 double:1.0531305226E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x036c }
    L_0x151d:
        r16 = r3;
    L_0x151f:
        r3 = 0;
        goto L_0x1554;
    L_0x1521:
        r2 = "NotificationMessageText";
        r3 = NUM; // 0x7f0d06bc float:1.8745611E38 double:1.0531306293E-314;
        r5 = 2;
        r5 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x036c }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        r5[r10] = r11;	 Catch:{ Throwable -> 0x036c }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x036c }
        r3 = r15[r10];	 Catch:{ Throwable -> 0x036c }
        goto L_0x151d;
    L_0x153a:
        if (r2 == 0) goto L_0x1550;
    L_0x153c:
        r2 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x036c }
        r2.<init>();	 Catch:{ Throwable -> 0x036c }
        r3 = "unhandled loc_key = ";
        r2.append(r3);	 Catch:{ Throwable -> 0x036c }
        r2.append(r9);	 Catch:{ Throwable -> 0x036c }
        r2 = r2.toString();	 Catch:{ Throwable -> 0x036c }
        org.telegram.messenger.FileLog.w(r2);	 Catch:{ Throwable -> 0x036c }
    L_0x1550:
        r2 = 0;
    L_0x1551:
        r3 = 0;
    L_0x1552:
        r16 = 0;
    L_0x1554:
        if (r2 == 0) goto L_0x15ee;
    L_0x1556:
        r5 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Throwable -> 0x15f7 }
        r5.<init>();	 Catch:{ Throwable -> 0x15f7 }
        r5.id = r4;	 Catch:{ Throwable -> 0x15f7 }
        r5.random_id = r6;	 Catch:{ Throwable -> 0x15f7 }
        if (r16 == 0) goto L_0x1564;
    L_0x1561:
        r4 = r16;
        goto L_0x1565;
    L_0x1564:
        r4 = r2;
    L_0x1565:
        r5.message = r4;	 Catch:{ Throwable -> 0x15f7 }
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6 = r38 / r6;
        r4 = (int) r6;	 Catch:{ Throwable -> 0x15f7 }
        r5.date = r4;	 Catch:{ Throwable -> 0x15f7 }
        if (r1 == 0) goto L_0x1577;
    L_0x1570:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage;	 Catch:{ Throwable -> 0x036c }
        r4.<init>();	 Catch:{ Throwable -> 0x036c }
        r5.action = r4;	 Catch:{ Throwable -> 0x036c }
    L_0x1577:
        if (r8 == 0) goto L_0x1580;
    L_0x1579:
        r4 = r5.flags;	 Catch:{ Throwable -> 0x036c }
        r6 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r4 = r4 | r6;
        r5.flags = r4;	 Catch:{ Throwable -> 0x036c }
    L_0x1580:
        r12 = r32;
        r5.dialog_id = r12;	 Catch:{ Throwable -> 0x15f7 }
        if (r31 == 0) goto L_0x1594;
    L_0x1586:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChannel;	 Catch:{ Throwable -> 0x036c }
        r4.<init>();	 Catch:{ Throwable -> 0x036c }
        r5.to_id = r4;	 Catch:{ Throwable -> 0x036c }
        r4 = r5.to_id;	 Catch:{ Throwable -> 0x036c }
        r8 = r31;
        r4.channel_id = r8;	 Catch:{ Throwable -> 0x036c }
        goto L_0x15b1;
    L_0x1594:
        if (r18 == 0) goto L_0x15a4;
    L_0x1596:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ Throwable -> 0x036c }
        r4.<init>();	 Catch:{ Throwable -> 0x036c }
        r5.to_id = r4;	 Catch:{ Throwable -> 0x036c }
        r4 = r5.to_id;	 Catch:{ Throwable -> 0x036c }
        r6 = r18;
        r4.chat_id = r6;	 Catch:{ Throwable -> 0x036c }
        goto L_0x15b1;
    L_0x15a4:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ Throwable -> 0x15f7 }
        r4.<init>();	 Catch:{ Throwable -> 0x15f7 }
        r5.to_id = r4;	 Catch:{ Throwable -> 0x15f7 }
        r4 = r5.to_id;	 Catch:{ Throwable -> 0x15f7 }
        r7 = r23;
        r4.user_id = r7;	 Catch:{ Throwable -> 0x15f7 }
    L_0x15b1:
        r4 = r5.flags;	 Catch:{ Throwable -> 0x15f7 }
        r4 = r4 | 256;
        r5.flags = r4;	 Catch:{ Throwable -> 0x15f7 }
        r5.from_id = r14;	 Catch:{ Throwable -> 0x15f7 }
        if (r20 != 0) goto L_0x15c0;
    L_0x15bb:
        if (r1 == 0) goto L_0x15be;
    L_0x15bd:
        goto L_0x15c0;
    L_0x15be:
        r1 = 0;
        goto L_0x15c1;
    L_0x15c0:
        r1 = 1;
    L_0x15c1:
        r5.mentioned = r1;	 Catch:{ Throwable -> 0x15f7 }
        r1 = r19;
        r5.silent = r1;	 Catch:{ Throwable -> 0x15f7 }
        r1 = new org.telegram.messenger.MessageObject;	 Catch:{ Throwable -> 0x15f7 }
        r19 = r1;
        r20 = r28;
        r21 = r5;
        r22 = r2;
        r23 = r25;
        r25 = r3;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27);	 Catch:{ Throwable -> 0x15f7 }
        r2 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x15f7 }
        r2.<init>();	 Catch:{ Throwable -> 0x15f7 }
        r2.add(r1);	 Catch:{ Throwable -> 0x15f7 }
        r1 = org.telegram.messenger.NotificationsController.getInstance(r28);	 Catch:{ Throwable -> 0x15f7 }
        r3 = r36;
        r4 = r3.countDownLatch;	 Catch:{ Throwable -> 0x169a }
        r5 = 1;
        r1.processNewMessages(r2, r5, r5, r4);	 Catch:{ Throwable -> 0x169a }
        goto L_0x168e;
    L_0x15ee:
        r3 = r36;
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x169a }
        r1.countDown();	 Catch:{ Throwable -> 0x169a }
        goto L_0x168e;
    L_0x15f7:
        r0 = move-exception;
        r3 = r36;
        goto L_0x169b;
    L_0x15fc:
        r0 = move-exception;
        r3 = r36;
        goto L_0x16a9;
    L_0x1601:
        r0 = move-exception;
        r3 = r1;
        goto L_0x16a9;
    L_0x1605:
        r3 = r1;
        r28 = r15;
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x169a }
        r1.countDown();	 Catch:{ Throwable -> 0x169a }
        goto L_0x168e;
    L_0x160f:
        r8 = r2;
        r7 = r6;
        r30 = r14;
        r28 = r15;
        r6 = r3;
        r3 = r1;
        r1 = "max_id";
        r1 = r11.getInt(r1);	 Catch:{ Throwable -> 0x169a }
        r15 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x169a }
        r15.<init>();	 Catch:{ Throwable -> 0x169a }
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x169a }
        if (r2 == 0) goto L_0x1642;
    L_0x1626:
        r2 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x169a }
        r2.<init>();	 Catch:{ Throwable -> 0x169a }
        r4 = "GCM received read notification max_id = ";
        r2.append(r4);	 Catch:{ Throwable -> 0x169a }
        r2.append(r1);	 Catch:{ Throwable -> 0x169a }
        r4 = " for dialogId = ";
        r2.append(r4);	 Catch:{ Throwable -> 0x169a }
        r2.append(r12);	 Catch:{ Throwable -> 0x169a }
        r2 = r2.toString();	 Catch:{ Throwable -> 0x169a }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Throwable -> 0x169a }
    L_0x1642:
        if (r8 == 0) goto L_0x1651;
    L_0x1644:
        r2 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox;	 Catch:{ Throwable -> 0x169a }
        r2.<init>();	 Catch:{ Throwable -> 0x169a }
        r2.channel_id = r8;	 Catch:{ Throwable -> 0x169a }
        r2.max_id = r1;	 Catch:{ Throwable -> 0x169a }
        r15.add(r2);	 Catch:{ Throwable -> 0x169a }
        goto L_0x1674;
    L_0x1651:
        r2 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox;	 Catch:{ Throwable -> 0x169a }
        r2.<init>();	 Catch:{ Throwable -> 0x169a }
        if (r7 == 0) goto L_0x1664;
    L_0x1658:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ Throwable -> 0x169a }
        r4.<init>();	 Catch:{ Throwable -> 0x169a }
        r2.peer = r4;	 Catch:{ Throwable -> 0x169a }
        r4 = r2.peer;	 Catch:{ Throwable -> 0x169a }
        r4.user_id = r7;	 Catch:{ Throwable -> 0x169a }
        goto L_0x166f;
    L_0x1664:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ Throwable -> 0x169a }
        r4.<init>();	 Catch:{ Throwable -> 0x169a }
        r2.peer = r4;	 Catch:{ Throwable -> 0x169a }
        r4 = r2.peer;	 Catch:{ Throwable -> 0x169a }
        r4.chat_id = r6;	 Catch:{ Throwable -> 0x169a }
    L_0x166f:
        r2.max_id = r1;	 Catch:{ Throwable -> 0x169a }
        r15.add(r2);	 Catch:{ Throwable -> 0x169a }
    L_0x1674:
        r14 = org.telegram.messenger.MessagesController.getInstance(r28);	 Catch:{ Throwable -> 0x169a }
        r16 = 0;
        r17 = 0;
        r18 = 0;
        r19 = 0;
        r14.processUpdateArray(r15, r16, r17, r18, r19);	 Catch:{ Throwable -> 0x169a }
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x169a }
        r1.countDown();	 Catch:{ Throwable -> 0x169a }
        goto L_0x168e;
    L_0x1689:
        r3 = r1;
        r30 = r14;
        r28 = r15;
    L_0x168e:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r28);	 Catch:{ Throwable -> 0x169a }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r28);	 Catch:{ Throwable -> 0x169a }
        r1.resumeNetworkMaybe();	 Catch:{ Throwable -> 0x169a }
        goto L_0x17a7;
    L_0x169a:
        r0 = move-exception;
    L_0x169b:
        r1 = r0;
        r15 = r28;
        goto L_0x1752;
    L_0x16a0:
        r0 = move-exception;
        r3 = r1;
        r30 = r14;
        goto L_0x16a9;
    L_0x16a5:
        r0 = move-exception;
        r3 = r1;
        r30 = r7;
    L_0x16a9:
        r28 = r15;
        goto L_0x1751;
    L_0x16ad:
        r3 = r1;
        r30 = r7;
        r28 = r15;
        r1 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ;	 Catch:{ Throwable -> 0x16be }
        r1.<init>(r15);	 Catch:{ Throwable -> 0x16bb }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);	 Catch:{ Throwable -> 0x174b }
        return;
    L_0x16bb:
        r0 = move-exception;
        goto L_0x1751;
    L_0x16be:
        r0 = move-exception;
        r15 = r28;
        goto L_0x1751;
    L_0x16c3:
        r3 = r1;
        r30 = r7;
        r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification;	 Catch:{ Throwable -> 0x174b }
        r1.<init>();	 Catch:{ Throwable -> 0x174b }
        r2 = 0;
        r1.popup = r2;	 Catch:{ Throwable -> 0x174b }
        r2 = 2;
        r1.flags = r2;	 Catch:{ Throwable -> 0x174b }
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6 = r38 / r6;
        r2 = (int) r6;	 Catch:{ Throwable -> 0x174b }
        r1.inbox_date = r2;	 Catch:{ Throwable -> 0x174b }
        r2 = "message";
        r2 = r5.getString(r2);	 Catch:{ Throwable -> 0x174b }
        r1.message = r2;	 Catch:{ Throwable -> 0x174b }
        r2 = "announcement";
        r1.type = r2;	 Catch:{ Throwable -> 0x174b }
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;	 Catch:{ Throwable -> 0x174b }
        r2.<init>();	 Catch:{ Throwable -> 0x174b }
        r1.media = r2;	 Catch:{ Throwable -> 0x174b }
        r2 = new org.telegram.tgnet.TLRPC$TL_updates;	 Catch:{ Throwable -> 0x174b }
        r2.<init>();	 Catch:{ Throwable -> 0x174b }
        r4 = r2.updates;	 Catch:{ Throwable -> 0x174b }
        r4.add(r1);	 Catch:{ Throwable -> 0x174b }
        r1 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ Throwable -> 0x174b }
        r4 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo;	 Catch:{ Throwable -> 0x170c }
        r4.<init>(r15, r2);	 Catch:{ Throwable -> 0x170c }
        r1.postRunnable(r4);	 Catch:{ Throwable -> 0x174b }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ Throwable -> 0x174b }
        r1.resumeNetworkMaybe();	 Catch:{ Throwable -> 0x174b }
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x174b }
        r1.countDown();	 Catch:{ Throwable -> 0x174b }
        return;
    L_0x170c:
        r0 = move-exception;
        goto L_0x1751;
    L_0x170e:
        r3 = r1;
        r30 = r7;
        r1 = "dc";
        r1 = r11.getInt(r1);	 Catch:{ Throwable -> 0x174b }
        r2 = "addr";
        r2 = r11.getString(r2);	 Catch:{ Throwable -> 0x174b }
        r4 = ":";
        r2 = r2.split(r4);	 Catch:{ Throwable -> 0x174b }
        r4 = r2.length;	 Catch:{ Throwable -> 0x174b }
        r5 = 2;
        if (r4 == r5) goto L_0x172d;
    L_0x1727:
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x174b }
        r1.countDown();	 Catch:{ Throwable -> 0x174b }
        return;
    L_0x172d:
        r4 = 0;
        r4 = r2[r4];	 Catch:{ Throwable -> 0x174b }
        r5 = 1;
        r2 = r2[r5];	 Catch:{ Throwable -> 0x174b }
        r2 = java.lang.Integer.parseInt(r2);	 Catch:{ Throwable -> 0x174b }
        r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ Throwable -> 0x174b }
        r5.applyDatacenterAddress(r1, r4, r2);	 Catch:{ Throwable -> 0x174b }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ Throwable -> 0x174b }
        r1.resumeNetworkMaybe();	 Catch:{ Throwable -> 0x174b }
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x174b }
        r1.countDown();	 Catch:{ Throwable -> 0x174b }
        return;
    L_0x174b:
        r0 = move-exception;
        goto L_0x1751;
    L_0x174d:
        r0 = move-exception;
        r3 = r1;
        r30 = r7;
    L_0x1751:
        r1 = r0;
    L_0x1752:
        r4 = r30;
        r2 = -1;
        goto L_0x176f;
    L_0x1756:
        r0 = move-exception;
        r3 = r1;
        r30 = r7;
        r1 = r0;
        r4 = r30;
        r2 = -1;
        goto L_0x176e;
    L_0x175f:
        r0 = move-exception;
        r3 = r1;
        r30 = r7;
        r1 = r0;
        r4 = r30;
        r2 = -1;
        goto L_0x176d;
    L_0x1768:
        r0 = move-exception;
        r3 = r1;
        r1 = r0;
        r2 = -1;
        r4 = 0;
    L_0x176d:
        r9 = 0;
    L_0x176e:
        r15 = -1;
    L_0x176f:
        if (r15 == r2) goto L_0x1781;
    L_0x1771:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15);
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);
        r2.resumeNetworkMaybe();
        r2 = r3.countDownLatch;
        r2.countDown();
        goto L_0x1784;
    L_0x1781:
        r36.onDecryptError();
    L_0x1784:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x17a4;
    L_0x1788:
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
    L_0x17a4:
        org.telegram.messenger.FileLog.e(r1);
    L_0x17a7:
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
        ConnectionsManager.setRegId(str, SharedConfig.pushStringStatus);
        if (str != null) {
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
}
