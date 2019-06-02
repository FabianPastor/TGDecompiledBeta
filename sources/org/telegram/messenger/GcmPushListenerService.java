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

    /* JADX WARNING: Removed duplicated region for block: B:263:0x0414  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x03ed A:{SYNTHETIC, Splitter:B:261:0x03ed} */
    /* JADX WARNING: Removed duplicated region for block: B:556:0x088f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:553:0x0884 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:550:0x0879 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:547:0x086e A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:544:0x0863 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0858 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x084d A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:535:0x0842 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:532:0x0837 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x082b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:526:0x081f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x0813 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x0808 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x07fd A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:514:0x07f2 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:511:0x07e6 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x07da A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x07ce A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x07c2 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:499:0x07b6 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x07aa A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x079e A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:490:0x0792 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0787 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:484:0x077b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x076f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:478:0x0763 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:475:0x0757 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x074b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:469:0x073f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x0733 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:463:0x0727 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x071b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x070f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x0703 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x06f7 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x06eb A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:445:0x06df A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x06d3 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:439:0x06c7 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x06bb A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x06af A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x06a4 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0698 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x068c A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0680 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:418:0x0674 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0668 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:412:0x065c A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0650 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0644 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0638 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x062c A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:397:0x0620 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x0614 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0609 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x05fd A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x05f1 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:382:0x05e5 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x05d9 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x05cd A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x05c1 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:370:0x05b5 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x05a9 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x059d A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x0591 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:358:0x0585 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x0579 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:352:0x056d A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0561 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x0556 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x054a A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:340:0x053e A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x0532 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x0526 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x051a A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:328:0x050e A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x0502 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x04f6 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x04ea A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x04df A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x04d3 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x04c7 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x04bb A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x04af A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x04a3 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x0497 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x048b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x047f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x0473 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x0467 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x045b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x044f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x0443 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0437 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x042b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x041f A:{SYNTHETIC, Splitter:B:267:0x041f} */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x03a0  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0376 A:{SYNTHETIC, Splitter:B:227:0x0376} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x03c6 A:{SYNTHETIC, Splitter:B:247:0x03c6} */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x03b6  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x03ed A:{SYNTHETIC, Splitter:B:261:0x03ed} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0414  */
    /* JADX WARNING: Removed duplicated region for block: B:556:0x088f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:553:0x0884 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:550:0x0879 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:547:0x086e A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:544:0x0863 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0858 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x084d A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:535:0x0842 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:532:0x0837 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x082b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:526:0x081f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x0813 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x0808 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x07fd A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:514:0x07f2 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:511:0x07e6 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x07da A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x07ce A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x07c2 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:499:0x07b6 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x07aa A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x079e A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:490:0x0792 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0787 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:484:0x077b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x076f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:478:0x0763 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:475:0x0757 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x074b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:469:0x073f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x0733 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:463:0x0727 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x071b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x070f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x0703 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x06f7 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x06eb A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:445:0x06df A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x06d3 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:439:0x06c7 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x06bb A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x06af A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x06a4 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0698 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x068c A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0680 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:418:0x0674 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0668 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:412:0x065c A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0650 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0644 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0638 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x062c A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:397:0x0620 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x0614 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0609 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x05fd A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x05f1 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:382:0x05e5 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x05d9 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x05cd A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x05c1 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:370:0x05b5 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x05a9 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x059d A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x0591 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:358:0x0585 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x0579 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:352:0x056d A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0561 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x0556 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x054a A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:340:0x053e A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x0532 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x0526 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x051a A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:328:0x050e A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x0502 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x04f6 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x04ea A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x04df A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x04d3 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x04c7 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x04bb A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x04af A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x04a3 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x0497 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x048b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x047f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x0473 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x0467 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x045b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x044f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x0443 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0437 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x042b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x041f A:{SYNTHETIC, Splitter:B:267:0x041f} */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x0356 A:{SYNTHETIC, Splitter:B:217:0x0356} */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0376 A:{SYNTHETIC, Splitter:B:227:0x0376} */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x03a0  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x03b6  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x03c6 A:{SYNTHETIC, Splitter:B:247:0x03c6} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0414  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x03ed A:{SYNTHETIC, Splitter:B:261:0x03ed} */
    /* JADX WARNING: Removed duplicated region for block: B:556:0x088f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:553:0x0884 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:550:0x0879 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:547:0x086e A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:544:0x0863 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0858 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x084d A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:535:0x0842 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:532:0x0837 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x082b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:526:0x081f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x0813 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x0808 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x07fd A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:514:0x07f2 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:511:0x07e6 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x07da A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x07ce A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x07c2 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:499:0x07b6 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x07aa A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x079e A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:490:0x0792 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0787 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:484:0x077b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x076f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:478:0x0763 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:475:0x0757 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x074b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:469:0x073f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x0733 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:463:0x0727 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x071b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x070f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x0703 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x06f7 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x06eb A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:445:0x06df A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x06d3 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:439:0x06c7 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x06bb A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x06af A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x06a4 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0698 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x068c A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0680 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:418:0x0674 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0668 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:412:0x065c A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0650 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0644 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0638 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x062c A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:397:0x0620 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x0614 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0609 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x05fd A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x05f1 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:382:0x05e5 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x05d9 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x05cd A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x05c1 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:370:0x05b5 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x05a9 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x059d A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x0591 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:358:0x0585 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x0579 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:352:0x056d A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0561 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x0556 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x054a A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:340:0x053e A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x0532 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x0526 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x051a A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:328:0x050e A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x0502 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x04f6 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x04ea A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x04df A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x04d3 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x04c7 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x04bb A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x04af A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x04a3 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x0497 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x048b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x047f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x0473 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x0467 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x045b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x044f A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x0443 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0437 A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x042b A:{Catch:{ Throwable -> 0x0395 }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x041f A:{SYNTHETIC, Splitter:B:267:0x041f} */
    /* JADX WARNING: Removed duplicated region for block: B:828:0x1892 A:{Catch:{ Throwable -> 0x183f, Throwable -> 0x18cf }} */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x01db  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x01db  */
    /* JADX WARNING: Removed duplicated region for block: B:828:0x1892 A:{Catch:{ Throwable -> 0x183f, Throwable -> 0x18cf }} */
    /* JADX WARNING: Removed duplicated region for block: B:828:0x1892 A:{Catch:{ Throwable -> 0x183f, Throwable -> 0x18cf }} */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x01db  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1903  */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x18f3  */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x190a  */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x18f3  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1903  */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x190a  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1903  */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x18f3  */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x190a  */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x18f3  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1903  */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x190a  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1903  */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x18f3  */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x190a  */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x18f3  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1903  */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x190a  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1903  */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x18f3  */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x190a  */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x18f3  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1903  */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x190a  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1903  */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x18f3  */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x190a  */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x18f3  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1903  */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x190a  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1903  */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x18f3  */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x190a  */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x18f3  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1903  */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x190a  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1903  */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x18f3  */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x190a  */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x18f3  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1903  */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x190a  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1903  */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x18f3  */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x190a  */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x18f3  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1903  */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x190a  */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:560:0x089b, code skipped:
            r11 = "Photos";
            r16 = r14;
            r14 = "ChannelMessageFew";
            r30 = r3;
            r3 = " ";
            r31 = r2;
            r2 = "AttachSticker";
     */
    /* JADX WARNING: Missing block: B:561:0x08a9, code skipped:
            switch(r5) {
                case 0: goto L_0x16aa;
                case 1: goto L_0x1685;
                case 2: goto L_0x1664;
                case 3: goto L_0x1643;
                case 4: goto L_0x1622;
                case 5: goto L_0x1600;
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
                case 22: goto L_0x1370;
                case 23: goto L_0x1355;
                case 24: goto L_0x1331;
                case 25: goto L_0x1314;
                case 26: goto L_0x12f7;
                case 27: goto L_0x12da;
                case 28: goto L_0x12bc;
                case 29: goto L_0x125f;
                case 30: goto L_0x1241;
                case 31: goto L_0x121e;
                case 32: goto L_0x11fb;
                case 33: goto L_0x11dd;
                case 34: goto L_0x11bf;
                case 35: goto L_0x11a1;
                case 36: goto L_0x1183;
                case 37: goto L_0x1164;
                case 38: goto L_0x113a;
                case 39: goto L_0x1116;
                case 40: goto L_0x10f0;
                case 41: goto L_0x10db;
                case 42: goto L_0x10ba;
                case 43: goto L_0x1097;
                case 44: goto L_0x1074;
                case 45: goto L_0x1051;
                case 46: goto L_0x102e;
                case 47: goto L_0x100b;
                case 48: goto L_0x0var_;
                case 49: goto L_0x0f5f;
                case 50: goto L_0x0var_;
                case 51: goto L_0x0var_;
                case 52: goto L_0x0ee4;
                case 53: goto L_0x0ebf;
                case 54: goto L_0x0e9a;
                case 55: goto L_0x0e6e;
                case 56: goto L_0x0e46;
                case 57: goto L_0x0e1a;
                case 58: goto L_0x0dfe;
                case 59: goto L_0x0de2;
                case 60: goto L_0x0dc6;
                case 61: goto L_0x0da3;
                case 62: goto L_0x0d87;
                case 63: goto L_0x0d6b;
                case 64: goto L_0x0d4f;
                case 65: goto L_0x0d33;
                case 66: goto L_0x0d17;
                case 67: goto L_0x0cfb;
                case 68: goto L_0x0ccb;
                case 69: goto L_0x0c9c;
                case 70: goto L_0x0c6b;
                case 71: goto L_0x0c4c;
                case 72: goto L_0x0CLASSNAME;
                case 73: goto L_0x0bde;
                case 74: goto L_0x0baf;
                case 75: goto L_0x0b7f;
                case 76: goto L_0x0b4e;
                case 77: goto L_0x0b1d;
                case 78: goto L_0x0a9d;
                case 79: goto L_0x0a6c;
                case 80: goto L_0x0a2f;
                case 81: goto L_0x09f8;
                case 82: goto L_0x09c6;
                case 83: goto L_0x0999;
                case 84: goto L_0x096c;
                case 85: goto L_0x093d;
                case 86: goto L_0x090e;
                case 87: goto L_0x08de;
                case 88: goto L_0x08d6;
                case 89: goto L_0x08d6;
                case 90: goto L_0x08d6;
                case 91: goto L_0x08b6;
                case 92: goto L_0x08d6;
                case 93: goto L_0x08d6;
                case 94: goto L_0x08d6;
                case 95: goto L_0x08d6;
                case 96: goto L_0x08d6;
                default: goto L_0x08ac;
            };
     */
    /* JADX WARNING: Missing block: B:562:0x08ac, code skipped:
            r17 = r10;
            r5 = r16;
            r11 = r23;
     */
    /* JADX WARNING: Missing block: B:566:?, code skipped:
            r4 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
            r17 = r10;
            r5 = r16;
            r11 = r23;
            r21 = null;
            r25 = true;
            r23 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
     */
    /* JADX WARNING: Missing block: B:567:0x08d6, code skipped:
            r17 = r10;
            r5 = r16;
            r11 = r23;
     */
    /* JADX WARNING: Missing block: B:568:0x08de, code skipped:
            if (r10 == 0) goto L_0x08f7;
     */
    /* JADX WARNING: Missing block: B:569:0x08e0, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:570:0x08f7, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:571:0x0908, code skipped:
            r17 = r10;
            r5 = r16;
     */
    /* JADX WARNING: Missing block: B:572:0x090e, code skipped:
            r5 = r16;
     */
    /* JADX WARNING: Missing block: B:573:0x0910, code skipped:
            if (r10 == 0) goto L_0x092a;
     */
    /* JADX WARNING: Missing block: B:574:0x0912, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:575:0x092a, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:576:0x093d, code skipped:
            r5 = r16;
     */
    /* JADX WARNING: Missing block: B:577:0x093f, code skipped:
            if (r10 == 0) goto L_0x0959;
     */
    /* JADX WARNING: Missing block: B:578:0x0941, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:579:0x0959, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:580:0x096c, code skipped:
            r5 = r16;
     */
    /* JADX WARNING: Missing block: B:581:0x096e, code skipped:
            if (r10 == 0) goto L_0x0987;
     */
    /* JADX WARNING: Missing block: B:582:0x0970, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:583:0x0987, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:584:0x0999, code skipped:
            r5 = r16;
     */
    /* JADX WARNING: Missing block: B:585:0x099b, code skipped:
            if (r10 == 0) goto L_0x09b4;
     */
    /* JADX WARNING: Missing block: B:586:0x099d, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:587:0x09b4, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:588:0x09c6, code skipped:
            r5 = r16;
     */
    /* JADX WARNING: Missing block: B:589:0x09c8, code skipped:
            if (r10 == 0) goto L_0x09e1;
     */
    /* JADX WARNING: Missing block: B:590:0x09ca, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:591:0x09e1, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:592:0x09f2, code skipped:
            r17 = r10;
     */
    /* JADX WARNING: Missing block: B:593:0x09f4, code skipped:
            r11 = r23;
     */
    /* JADX WARNING: Missing block: B:594:0x09f8, code skipped:
            r5 = r16;
     */
    /* JADX WARNING: Missing block: B:595:0x09fa, code skipped:
            if (r10 == 0) goto L_0x0a18;
     */
    /* JADX WARNING: Missing block: B:596:0x09fc, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:597:0x0a18, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:598:0x0a2f, code skipped:
            r5 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:599:0x0a33, code skipped:
            if (r10 == 0) goto L_0x0a54;
     */
    /* JADX WARNING: Missing block: B:600:0x0a35, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:601:0x0a54, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:602:0x0a6c, code skipped:
            r5 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:603:0x0a70, code skipped:
            if (r10 == 0) goto L_0x0a8a;
     */
    /* JADX WARNING: Missing block: B:604:0x0a72, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:605:0x0a8a, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:606:0x0a9d, code skipped:
            r5 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:607:0x0aa1, code skipped:
            if (r10 == 0) goto L_0x0ae6;
     */
    /* JADX WARNING: Missing block: B:609:0x0aa5, code skipped:
            if (r15.length <= 2) goto L_0x0ace;
     */
    /* JADX WARNING: Missing block: B:611:0x0aad, code skipped:
            if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x0ace;
     */
    /* JADX WARNING: Missing block: B:612:0x0aaf, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:613:0x0ace, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:615:0x0ae8, code skipped:
            if (r15.length <= 1) goto L_0x0b0a;
     */
    /* JADX WARNING: Missing block: B:617:0x0af0, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x0b0a;
     */
    /* JADX WARNING: Missing block: B:618:0x0af2, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:619:0x0b0a, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:620:0x0b1d, code skipped:
            r5 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:621:0x0b21, code skipped:
            if (r10 == 0) goto L_0x0b3b;
     */
    /* JADX WARNING: Missing block: B:622:0x0b23, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:623:0x0b3b, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:624:0x0b4e, code skipped:
            r5 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:625:0x0b52, code skipped:
            if (r10 == 0) goto L_0x0b6c;
     */
    /* JADX WARNING: Missing block: B:626:0x0b54, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:627:0x0b6c, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:628:0x0b7f, code skipped:
            r5 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:629:0x0b83, code skipped:
            if (r10 == 0) goto L_0x0b9d;
     */
    /* JADX WARNING: Missing block: B:630:0x0b85, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:631:0x0b9d, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:632:0x0baf, code skipped:
            r5 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:633:0x0bb3, code skipped:
            if (r10 == 0) goto L_0x0bcc;
     */
    /* JADX WARNING: Missing block: B:634:0x0bb5, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:635:0x0bcc, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:636:0x0bde, code skipped:
            r5 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:637:0x0be2, code skipped:
            if (r10 == 0) goto L_0x0bfb;
     */
    /* JADX WARNING: Missing block: B:638:0x0be4, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:639:0x0bfb, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:640:0x0c0c, code skipped:
            r11 = r2;
     */
    /* JADX WARNING: Missing block: B:641:0x0c0d, code skipped:
            r17 = r10;
     */
    /* JADX WARNING: Missing block: B:642:0x0CLASSNAME, code skipped:
            r5 = r16;
            r2 = r23;
     */
    /* JADX WARNING: Missing block: B:643:0x0CLASSNAME, code skipped:
            if (r10 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Missing block: B:644:0x0CLASSNAME, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:645:0x0CLASSNAME, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:646:0x0c4c, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:647:0x0CLASSNAME, code skipped:
            r11 = r2;
     */
    /* JADX WARNING: Missing block: B:648:0x0CLASSNAME, code skipped:
            r17 = r10;
     */
    /* JADX WARNING: Missing block: B:649:0x0c6b, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:650:0x0c9c, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString(r11, org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:651:0x0ccb, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString(r4, org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:652:0x0cfb, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:653:0x0d17, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:654:0x0d33, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:655:0x0d4f, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:656:0x0d6b, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:657:0x0d87, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:658:0x0da3, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:659:0x0dc6, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:660:0x0de2, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:661:0x0dfe, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:662:0x0e1a, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r15[0], r15[1], r15[2]);
            r3 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Missing block: B:663:0x0e46, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r15[0], r15[1], r15[2], r15[3]);
     */
    /* JADX WARNING: Missing block: B:664:0x0e6e, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r15[0], r15[1], r15[2]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:665:0x0e9a, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:666:0x0ebf, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:667:0x0ee4, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:668:0x0var_, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r15[0], r15[1], r15[2]);
            r3 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:669:0x0var_, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r15[0], r15[1], r15[2]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:670:0x0f5f, code skipped:
            r5 = r16;
            r2 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r15[0], r15[1]);
            r3 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:671:0x0var_, code skipped:
            r11 = r2;
            r21 = r3;
     */
    /* JADX WARNING: Missing block: B:672:0x0var_, code skipped:
            r5 = r16;
            r4 = r23;
     */
    /* JADX WARNING: Missing block: B:673:0x0f8d, code skipped:
            if (r15.length <= 2) goto L_0x0fd4;
     */
    /* JADX WARNING: Missing block: B:675:0x0var_, code skipped:
            if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x0fd4;
     */
    /* JADX WARNING: Missing block: B:676:0x0var_, code skipped:
            r23 = r4;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r15[0], r15[1], r15[2]);
            r11 = new java.lang.StringBuilder();
            r11.append(r15[2]);
            r11.append(r3);
            r11.append(org.telegram.messenger.LocaleController.getString(r2, NUM));
            r2 = r11.toString();
     */
    /* JADX WARNING: Missing block: B:677:0x0fd4, code skipped:
            r23 = r4;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r15[0], r15[1]);
            r11 = new java.lang.StringBuilder();
            r11.append(r15[1]);
            r11.append(r3);
            r11.append(org.telegram.messenger.LocaleController.getString(r2, NUM));
            r2 = r11.toString();
     */
    /* JADX WARNING: Missing block: B:678:0x100b, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r15[0], r15[1]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:679:0x102e, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r15[0], r15[1]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:680:0x1051, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r15[0], r15[1]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:681:0x1074, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r15[0], r15[1]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:682:0x1097, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r15[0], r15[1]);
            r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:683:0x10ba, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r15[0], r15[1], r15[2]);
            r2 = r15[2];
     */
    /* JADX WARNING: Missing block: B:684:0x10db, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:685:0x10f0, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString(r14, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:686:0x1116, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString(r14, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString(r11, org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:687:0x113a, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString(r14, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Missing block: B:688:0x1164, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:689:0x1183, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:690:0x11a1, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:691:0x11bf, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:692:0x11dd, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:693:0x11fb, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r15[0], r15[1]);
            r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:694:0x121e, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r15[0], r15[1]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:695:0x1241, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:696:0x125f, code skipped:
            r5 = r16;
     */
    /* JADX WARNING: Missing block: B:697:0x1263, code skipped:
            if (r15.length <= 1) goto L_0x12a2;
     */
    /* JADX WARNING: Missing block: B:699:0x126b, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x12a2;
     */
    /* JADX WARNING: Missing block: B:700:0x126d, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r15[0], r15[1]);
            r11 = new java.lang.StringBuilder();
            r11.append(r15[1]);
            r11.append(r3);
            r11.append(org.telegram.messenger.LocaleController.getString(r2, NUM));
            r2 = r11.toString();
     */
    /* JADX WARNING: Missing block: B:701:0x12a2, code skipped:
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString(r2, NUM);
     */
    /* JADX WARNING: Missing block: B:702:0x12bc, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:703:0x12da, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:704:0x12f7, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:705:0x1314, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:706:0x1331, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:707:0x134d, code skipped:
            r21 = r2;
            r17 = r10;
            r11 = r23;
     */
    /* JADX WARNING: Missing block: B:708:0x1355, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r15[0], r15[1]);
            r2 = r15[1];
     */
    /* JADX WARNING: Missing block: B:709:0x1370, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:710:0x1383, code skipped:
            r17 = r10;
            r11 = r23;
     */
    /* JADX WARNING: Missing block: B:711:0x1387, code skipped:
            r23 = r25;
            r21 = null;
            r25 = true;
     */
    /* JADX WARNING: Missing block: B:712:0x138f, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:713:0x13b6, code skipped:
            r5 = r16;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString(r11, org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:714:0x13db, code skipped:
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString(r4, org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:715:0x1405, code skipped:
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r15[0], r15[1]);
            r2 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Missing block: B:716:0x142a, code skipped:
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:717:0x144d, code skipped:
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r15[0], r15[1]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:718:0x1472, code skipped:
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:719:0x1492, code skipped:
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:720:0x14b2, code skipped:
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:721:0x14d1, code skipped:
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r15[0], r15[1]);
            r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:722:0x14f5, code skipped:
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r15[0], r15[1]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:723:0x1519, code skipped:
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:724:0x1537, code skipped:
            r21 = r2;
     */
    /* JADX WARNING: Missing block: B:725:0x1539, code skipped:
            r17 = r10;
     */
    /* JADX WARNING: Missing block: B:726:0x153d, code skipped:
            r5 = r16;
            r11 = r23;
     */
    /* JADX WARNING: Missing block: B:727:0x1543, code skipped:
            if (r15.length <= 1) goto L_0x1584;
     */
    /* JADX WARNING: Missing block: B:729:0x154b, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x1584;
     */
    /* JADX WARNING: Missing block: B:730:0x154d, code skipped:
            r17 = r10;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r15[0], r15[1]);
            r10 = new java.lang.StringBuilder();
            r10.append(r15[1]);
            r10.append(r3);
            r10.append(org.telegram.messenger.LocaleController.getString(r2, NUM));
            r2 = r10.toString();
     */
    /* JADX WARNING: Missing block: B:731:0x1584, code skipped:
            r17 = r10;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString(r2, NUM);
     */
    /* JADX WARNING: Missing block: B:732:0x15a0, code skipped:
            r17 = r10;
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:733:0x15c2, code skipped:
            r17 = r10;
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:734:0x15e4, code skipped:
            r17 = r10;
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r15[0]);
     */
    /* JADX WARNING: Missing block: B:735:0x15fc, code skipped:
            r23 = r25;
     */
    /* JADX WARNING: Missing block: B:736:0x1600, code skipped:
            r17 = r10;
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:737:0x1622, code skipped:
            r17 = r10;
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:738:0x1643, code skipped:
            r17 = r10;
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:739:0x1664, code skipped:
            r17 = r10;
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:740:0x1685, code skipped:
            r17 = r10;
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:741:0x16a5, code skipped:
            r21 = r2;
     */
    /* JADX WARNING: Missing block: B:742:0x16a7, code skipped:
            r23 = r25;
     */
    /* JADX WARNING: Missing block: B:743:0x16aa, code skipped:
            r17 = r10;
            r5 = r16;
            r11 = r23;
            r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r15[0], r15[1]);
            r2 = r15[1];
     */
    /* JADX WARNING: Missing block: B:744:0x16c9, code skipped:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x16df;
     */
    /* JADX WARNING: Missing block: B:745:0x16cb, code skipped:
            r2 = new java.lang.StringBuilder();
            r2.append("unhandled loc_key = ");
            r2.append(r9);
            org.telegram.messenger.FileLog.w(r2.toString());
     */
    /* JADX WARNING: Missing block: B:746:0x16df, code skipped:
            r23 = r25;
            r4 = null;
     */
    /* JADX WARNING: Missing block: B:747:0x16e2, code skipped:
            r21 = null;
     */
    /* JADX WARNING: Missing block: B:748:0x16e4, code skipped:
            r25 = false;
     */
    /* JADX WARNING: Missing block: B:749:0x16e6, code skipped:
            if (r4 == null) goto L_0x1774;
     */
    /* JADX WARNING: Missing block: B:751:?, code skipped:
            r2 = new org.telegram.tgnet.TLRPC.TL_message();
            r2.id = r5;
            r2.random_id = r6;
     */
    /* JADX WARNING: Missing block: B:752:0x16f1, code skipped:
            if (r21 == null) goto L_0x16f6;
     */
    /* JADX WARNING: Missing block: B:753:0x16f3, code skipped:
            r3 = r21;
     */
    /* JADX WARNING: Missing block: B:754:0x16f6, code skipped:
            r3 = r4;
     */
    /* JADX WARNING: Missing block: B:755:0x16f7, code skipped:
            r2.message = r3;
            r2.date = (int) (r34 / 1000);
     */
    /* JADX WARNING: Missing block: B:756:0x1700, code skipped:
            if (r1 == null) goto L_0x1709;
     */
    /* JADX WARNING: Missing block: B:758:?, code skipped:
            r2.action = new org.telegram.tgnet.TLRPC.TL_messageActionPinMessage();
     */
    /* JADX WARNING: Missing block: B:759:0x1709, code skipped:
            if (r8 == null) goto L_0x1712;
     */
    /* JADX WARNING: Missing block: B:760:0x170b, code skipped:
            r2.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Missing block: B:762:?, code skipped:
            r2.dialog_id = r12;
     */
    /* JADX WARNING: Missing block: B:763:0x1714, code skipped:
            if (r31 == 0) goto L_0x1724;
     */
    /* JADX WARNING: Missing block: B:765:?, code skipped:
            r2.to_id = new org.telegram.tgnet.TLRPC.TL_peerChannel();
            r2.to_id.channel_id = r31;
     */
    /* JADX WARNING: Missing block: B:766:0x1724, code skipped:
            if (r11 == 0) goto L_0x1732;
     */
    /* JADX WARNING: Missing block: B:767:0x1726, code skipped:
            r2.to_id = new org.telegram.tgnet.TLRPC.TL_peerChat();
            r2.to_id.chat_id = r11;
     */
    /* JADX WARNING: Missing block: B:769:?, code skipped:
            r2.to_id = new org.telegram.tgnet.TLRPC.TL_peerUser();
            r2.to_id.user_id = r30;
     */
    /* JADX WARNING: Missing block: B:770:0x173f, code skipped:
            r2.from_id = r17;
     */
    /* JADX WARNING: Missing block: B:771:0x1743, code skipped:
            if (r20 != null) goto L_0x174a;
     */
    /* JADX WARNING: Missing block: B:772:0x1745, code skipped:
            if (r1 == null) goto L_0x1748;
     */
    /* JADX WARNING: Missing block: B:774:0x1748, code skipped:
            r1 = false;
     */
    /* JADX WARNING: Missing block: B:775:0x174a, code skipped:
            r1 = true;
     */
    /* JADX WARNING: Missing block: B:776:0x174b, code skipped:
            r2.mentioned = r1;
            r2.silent = r19;
            r19 = new org.telegram.messenger.MessageObject(r29, r2, r4, r23, r24, r25, r26, r27);
            r2 = new java.util.ArrayList();
            r2.add(r19);
     */
    /* JADX WARNING: Missing block: B:777:0x176a, code skipped:
            r3 = r32;
     */
    /* JADX WARNING: Missing block: B:779:?, code skipped:
            org.telegram.messenger.NotificationsController.getInstance(r29).processNewMessages(r2, true, true, r3.countDownLatch);
     */
    /* JADX WARNING: Missing block: B:780:0x1774, code skipped:
            r32.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:785:0x1786, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:786:0x1787, code skipped:
            r3 = r1;
     */
    public /* synthetic */ void lambda$null$3$GcmPushListenerService(java.util.Map r33, long r34) {
        /*
        r32 = this;
        r1 = r32;
        r2 = r33;
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x000d;
    L_0x0008:
        r3 = "GCM START PROCESSING";
        org.telegram.messenger.FileLog.d(r3);
    L_0x000d:
        r5 = "p";
        r5 = r2.get(r5);	 Catch:{ Throwable -> 0x18e9 }
        r6 = r5 instanceof java.lang.String;	 Catch:{ Throwable -> 0x18e9 }
        if (r6 != 0) goto L_0x002c;
    L_0x0017:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x0024 }
        if (r2 == 0) goto L_0x0020;
    L_0x001b:
        r2 = "GCM DECRYPT ERROR 1";
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Throwable -> 0x0024 }
    L_0x0020:
        r32.onDecryptError();	 Catch:{ Throwable -> 0x0024 }
        return;
    L_0x0024:
        r0 = move-exception;
        r2 = r0;
        r3 = r1;
        r1 = -1;
        r7 = 0;
    L_0x0029:
        r9 = 0;
        goto L_0x18f0;
    L_0x002c:
        r5 = (java.lang.String) r5;	 Catch:{ Throwable -> 0x18e9 }
        r6 = 8;
        r5 = android.util.Base64.decode(r5, r6);	 Catch:{ Throwable -> 0x18e9 }
        r7 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Throwable -> 0x18e9 }
        r8 = r5.length;	 Catch:{ Throwable -> 0x18e9 }
        r7.<init>(r8);	 Catch:{ Throwable -> 0x18e9 }
        r7.writeBytes(r5);	 Catch:{ Throwable -> 0x18e9 }
        r8 = 0;
        r7.position(r8);	 Catch:{ Throwable -> 0x18e9 }
        r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ Throwable -> 0x18e9 }
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
        r9 = new byte[r6];	 Catch:{ Throwable -> 0x18e9 }
        r10 = 1;
        r7.readBytes(r9, r10);	 Catch:{ Throwable -> 0x18e9 }
        r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ Throwable -> 0x18e9 }
        r11 = java.util.Arrays.equals(r11, r9);	 Catch:{ Throwable -> 0x18e9 }
        r12 = 3;
        r13 = 2;
        if (r11 != 0) goto L_0x0091;
    L_0x0066:
        r32.onDecryptError();	 Catch:{ Throwable -> 0x0024 }
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
        r9 = new byte[r9];	 Catch:{ Throwable -> 0x18e9 }
        r7.readBytes(r9, r10);	 Catch:{ Throwable -> 0x18e9 }
        r11 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ Throwable -> 0x18e9 }
        r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13);	 Catch:{ Throwable -> 0x18e9 }
        r14 = r7.buffer;	 Catch:{ Throwable -> 0x18e9 }
        r15 = r11.aesKey;	 Catch:{ Throwable -> 0x18e9 }
        r11 = r11.aesIv;	 Catch:{ Throwable -> 0x18e9 }
        r17 = 0;
        r18 = 0;
        r19 = 24;
        r5 = r5.length;	 Catch:{ Throwable -> 0x18e9 }
        r20 = r5 + -24;
        r16 = r11;
        org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20);	 Catch:{ Throwable -> 0x18e9 }
        r21 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ Throwable -> 0x18e9 }
        r22 = 96;
        r23 = 32;
        r5 = r7.buffer;	 Catch:{ Throwable -> 0x18e9 }
        r25 = 24;
        r11 = r7.buffer;	 Catch:{ Throwable -> 0x18e9 }
        r26 = r11.limit();	 Catch:{ Throwable -> 0x18e9 }
        r24 = r5;
        r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26);	 Catch:{ Throwable -> 0x18e9 }
        r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6);	 Catch:{ Throwable -> 0x18e9 }
        if (r5 != 0) goto L_0x00e9;
    L_0x00ce:
        r32.onDecryptError();	 Catch:{ Throwable -> 0x0024 }
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
        r5 = r7.readInt32(r10);	 Catch:{ Throwable -> 0x18e9 }
        r5 = new byte[r5];	 Catch:{ Throwable -> 0x18e9 }
        r7.readBytes(r5, r10);	 Catch:{ Throwable -> 0x18e9 }
        r7 = new java.lang.String;	 Catch:{ Throwable -> 0x18e9 }
        r7.<init>(r5);	 Catch:{ Throwable -> 0x18e9 }
        r5 = new org.json.JSONObject;	 Catch:{ Throwable -> 0x18e1 }
        r5.<init>(r7);	 Catch:{ Throwable -> 0x18e1 }
        r9 = "loc_key";
        r9 = r5.has(r9);	 Catch:{ Throwable -> 0x18e1 }
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
        r11 = r5.get(r11);	 Catch:{ Throwable -> 0x18db }
        r11 = r11 instanceof org.json.JSONObject;	 Catch:{ Throwable -> 0x18db }
        if (r11 == 0) goto L_0x0129;
    L_0x011d:
        r11 = "custom";
        r11 = r5.getJSONObject(r11);	 Catch:{ Throwable -> 0x0124 }
        goto L_0x012e;
    L_0x0124:
        r0 = move-exception;
        r2 = r0;
        r3 = r1;
        goto L_0x18ef;
    L_0x0129:
        r11 = new org.json.JSONObject;	 Catch:{ Throwable -> 0x18db }
        r11.<init>();	 Catch:{ Throwable -> 0x18db }
    L_0x012e:
        r14 = "user_id";
        r14 = r5.has(r14);	 Catch:{ Throwable -> 0x18db }
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
        r15 = r14 instanceof java.lang.Integer;	 Catch:{ Throwable -> 0x18db }
        if (r15 == 0) goto L_0x0156;
    L_0x014f:
        r14 = (java.lang.Integer) r14;	 Catch:{ Throwable -> 0x0124 }
        r14 = r14.intValue();	 Catch:{ Throwable -> 0x0124 }
        goto L_0x016f;
    L_0x0156:
        r15 = r14 instanceof java.lang.String;	 Catch:{ Throwable -> 0x18db }
        if (r15 == 0) goto L_0x0165;
    L_0x015a:
        r14 = (java.lang.String) r14;	 Catch:{ Throwable -> 0x0124 }
        r14 = org.telegram.messenger.Utilities.parseInt(r14);	 Catch:{ Throwable -> 0x0124 }
        r14 = r14.intValue();	 Catch:{ Throwable -> 0x0124 }
        goto L_0x016f;
    L_0x0165:
        r14 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ Throwable -> 0x18db }
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);	 Catch:{ Throwable -> 0x18db }
        r14 = r14.getClientUserId();	 Catch:{ Throwable -> 0x18db }
    L_0x016f:
        r15 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ Throwable -> 0x18db }
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
        r3 = org.telegram.messenger.UserConfig.getInstance(r15);	 Catch:{ Throwable -> 0x18d4 }
        r3 = r3.isClientActivated();	 Catch:{ Throwable -> 0x18d4 }
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
        goto L_0x18d9;
    L_0x01a1:
        r3 = "google.sent_time";
        r2.get(r3);	 Catch:{ Throwable -> 0x18d4 }
        r2 = r9.hashCode();	 Catch:{ Throwable -> 0x18d4 }
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
        r2 = r9.equals(r2);	 Catch:{ Throwable -> 0x18d4 }
        if (r2 == 0) goto L_0x01d8;
    L_0x01d6:
        r2 = 2;
        goto L_0x01d9;
    L_0x01d8:
        r2 = -1;
    L_0x01d9:
        if (r2 == 0) goto L_0x1892;
    L_0x01db:
        if (r2 == r10) goto L_0x1847;
    L_0x01dd:
        if (r2 == r13) goto L_0x1831;
    L_0x01df:
        r2 = "channel_id";
        r2 = r11.has(r2);	 Catch:{ Throwable -> 0x1829 }
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
        r3 = r11.has(r3);	 Catch:{ Throwable -> 0x1829 }
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
        r6 = r11.has(r6);	 Catch:{ Throwable -> 0x1829 }
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
        r14 = r11.has(r14);	 Catch:{ Throwable -> 0x1829 }
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
        if (r14 == 0) goto L_0x1810;
    L_0x023f:
        r14 = "MESSAGE_DELETED";
        r14 = r14.equals(r9);	 Catch:{ Throwable -> 0x1829 }
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
        goto L_0x1810;
    L_0x0292:
        r14 = android.text.TextUtils.isEmpty(r9);	 Catch:{ Throwable -> 0x1829 }
        if (r14 != 0) goto L_0x179d;
    L_0x0298:
        r14 = "msg_id";
        r14 = r11.has(r14);	 Catch:{ Throwable -> 0x1829 }
        if (r14 == 0) goto L_0x02a7;
    L_0x02a0:
        r14 = "msg_id";
        r14 = r11.getInt(r14);	 Catch:{ Throwable -> 0x019c }
        goto L_0x02a8;
    L_0x02a7:
        r14 = 0;
    L_0x02a8:
        r10 = "random_id";
        r10 = r11.has(r10);	 Catch:{ Throwable -> 0x1829 }
        if (r10 == 0) goto L_0x02c1;
    L_0x02b0:
        r10 = "random_id";
        r10 = r11.getString(r10);	 Catch:{ Throwable -> 0x019c }
        r10 = org.telegram.messenger.Utilities.parseLong(r10);	 Catch:{ Throwable -> 0x019c }
        r22 = r10.longValue();	 Catch:{ Throwable -> 0x019c }
        r28 = r22;
        goto L_0x02c3;
    L_0x02c1:
        r28 = r19;
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
        r6 = r28;
        goto L_0x0320;
    L_0x0304:
        r23 = r6;
        r8 = r7;
        r6 = r28;
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
        goto L_0x18d9;
    L_0x031f:
        r10 = 0;
    L_0x0320:
        if (r10 == 0) goto L_0x1791;
    L_0x0322:
        r10 = "chat_from_id";
        r10 = r11.has(r10);	 Catch:{ Throwable -> 0x1789 }
        if (r10 == 0) goto L_0x0333;
    L_0x032a:
        r10 = "chat_from_id";
        r10 = r11.getInt(r10);	 Catch:{ Throwable -> 0x0319 }
        r28 = r8;
        goto L_0x0336;
    L_0x0333:
        r28 = r8;
        r10 = 0;
    L_0x0336:
        r8 = "mention";
        r8 = r11.has(r8);	 Catch:{ Throwable -> 0x1786 }
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
        goto L_0x18d1;
    L_0x034d:
        r8 = 0;
    L_0x034e:
        r1 = "silent";
        r1 = r11.has(r1);	 Catch:{ Throwable -> 0x1782 }
        if (r1 == 0) goto L_0x036b;
    L_0x0356:
        r1 = "silent";
        r1 = r11.getInt(r1);	 Catch:{ Throwable -> 0x0362 }
        if (r1 == 0) goto L_0x036b;
    L_0x035e:
        r29 = r15;
        r1 = 1;
        goto L_0x036e;
    L_0x0362:
        r0 = move-exception;
        r1 = -1;
        r3 = r32;
        r2 = r0;
        r7 = r28;
        goto L_0x18f1;
    L_0x036b:
        r29 = r15;
        r1 = 0;
    L_0x036e:
        r15 = "loc_args";
        r15 = r5.has(r15);	 Catch:{ Throwable -> 0x177d }
        if (r15 == 0) goto L_0x03a0;
    L_0x0376:
        r15 = "loc_args";
        r5 = r5.getJSONArray(r15);	 Catch:{ Throwable -> 0x0395 }
        r15 = r5.length();	 Catch:{ Throwable -> 0x0395 }
        r15 = new java.lang.String[r15];	 Catch:{ Throwable -> 0x0395 }
        r19 = r1;
        r20 = r8;
        r1 = 0;
    L_0x0387:
        r8 = r15.length;	 Catch:{ Throwable -> 0x0395 }
        if (r1 >= r8) goto L_0x0393;
    L_0x038a:
        r8 = r5.getString(r1);	 Catch:{ Throwable -> 0x0395 }
        r15[r1] = r8;	 Catch:{ Throwable -> 0x0395 }
        r1 = r1 + 1;
        goto L_0x0387;
    L_0x0393:
        r1 = 0;
        goto L_0x03a6;
    L_0x0395:
        r0 = move-exception;
        r1 = -1;
        r3 = r32;
        r2 = r0;
        r7 = r28;
        r15 = r29;
        goto L_0x18f1;
    L_0x03a0:
        r19 = r1;
        r20 = r8;
        r1 = 0;
        r15 = 0;
    L_0x03a6:
        r5 = r15[r1];	 Catch:{ Throwable -> 0x177d }
        r1 = "edit_date";
        r27 = r11.has(r1);	 Catch:{ Throwable -> 0x177d }
        r1 = "CHAT_";
        r1 = r9.startsWith(r1);	 Catch:{ Throwable -> 0x177d }
        if (r1 == 0) goto L_0x03c6;
    L_0x03b6:
        if (r2 == 0) goto L_0x03bb;
    L_0x03b8:
        r1 = 1;
        r8 = 1;
        goto L_0x03bd;
    L_0x03bb:
        r1 = 1;
        r8 = 0;
    L_0x03bd:
        r11 = r15[r1];	 Catch:{ Throwable -> 0x0395 }
        r24 = r5;
        r5 = r11;
        r1 = 0;
    L_0x03c3:
        r26 = 0;
        goto L_0x03e9;
    L_0x03c6:
        r1 = "PINNED_";
        r1 = r9.startsWith(r1);	 Catch:{ Throwable -> 0x177d }
        if (r1 == 0) goto L_0x03d5;
    L_0x03ce:
        if (r10 == 0) goto L_0x03d2;
    L_0x03d0:
        r8 = 1;
        goto L_0x03d3;
    L_0x03d2:
        r8 = 0;
    L_0x03d3:
        r1 = 1;
        goto L_0x03e6;
    L_0x03d5:
        r1 = "CHANNEL_";
        r1 = r9.startsWith(r1);	 Catch:{ Throwable -> 0x177d }
        if (r1 == 0) goto L_0x03e4;
    L_0x03dd:
        r1 = 0;
        r8 = 0;
        r24 = 0;
        r26 = 1;
        goto L_0x03e9;
    L_0x03e4:
        r1 = 0;
        r8 = 0;
    L_0x03e6:
        r24 = 0;
        goto L_0x03c3;
    L_0x03e9:
        r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x177d }
        if (r11 == 0) goto L_0x0414;
    L_0x03ed:
        r11 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0395 }
        r11.<init>();	 Catch:{ Throwable -> 0x0395 }
        r25 = r5;
        r5 = "GCM received message notification ";
        r11.append(r5);	 Catch:{ Throwable -> 0x0395 }
        r11.append(r9);	 Catch:{ Throwable -> 0x0395 }
        r5 = " for dialogId = ";
        r11.append(r5);	 Catch:{ Throwable -> 0x0395 }
        r11.append(r12);	 Catch:{ Throwable -> 0x0395 }
        r5 = " mid = ";
        r11.append(r5);	 Catch:{ Throwable -> 0x0395 }
        r11.append(r14);	 Catch:{ Throwable -> 0x0395 }
        r5 = r11.toString();	 Catch:{ Throwable -> 0x0395 }
        org.telegram.messenger.FileLog.d(r5);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0416;
    L_0x0414:
        r25 = r5;
    L_0x0416:
        r5 = r9.hashCode();	 Catch:{ Throwable -> 0x177d }
        switch(r5) {
            case -2100047043: goto L_0x088f;
            case -2091498420: goto L_0x0884;
            case -2053872415: goto L_0x0879;
            case -2039746363: goto L_0x086e;
            case -2023218804: goto L_0x0863;
            case -1979538588: goto L_0x0858;
            case -1979536003: goto L_0x084d;
            case -1979535888: goto L_0x0842;
            case -1969004705: goto L_0x0837;
            case -1946699248: goto L_0x082b;
            case -1528047021: goto L_0x081f;
            case -1493579426: goto L_0x0813;
            case -1480102982: goto L_0x0808;
            case -1478041834: goto L_0x07fd;
            case -1474543101: goto L_0x07f2;
            case -1465695932: goto L_0x07e6;
            case -1374906292: goto L_0x07da;
            case -1372940586: goto L_0x07ce;
            case -1264245338: goto L_0x07c2;
            case -1236086700: goto L_0x07b6;
            case -1236077786: goto L_0x07aa;
            case -1235796237: goto L_0x079e;
            case -1235686303: goto L_0x0792;
            case -1198046100: goto L_0x0787;
            case -1124254527: goto L_0x077b;
            case -1085137927: goto L_0x076f;
            case -1084856378: goto L_0x0763;
            case -1084746444: goto L_0x0757;
            case -819729482: goto L_0x074b;
            case -772141857: goto L_0x073f;
            case -638310039: goto L_0x0733;
            case -590403924: goto L_0x0727;
            case -589196239: goto L_0x071b;
            case -589193654: goto L_0x070f;
            case -589193539: goto L_0x0703;
            case -440169325: goto L_0x06f7;
            case -412748110: goto L_0x06eb;
            case -228518075: goto L_0x06df;
            case -213586509: goto L_0x06d3;
            case -115582002: goto L_0x06c7;
            case -112621464: goto L_0x06bb;
            case -108522133: goto L_0x06af;
            case -107572034: goto L_0x06a4;
            case -40534265: goto L_0x0698;
            case 65254746: goto L_0x068c;
            case 141040782: goto L_0x0680;
            case 309993049: goto L_0x0674;
            case 309995634: goto L_0x0668;
            case 309995749: goto L_0x065c;
            case 320532812: goto L_0x0650;
            case 328933854: goto L_0x0644;
            case 331340546: goto L_0x0638;
            case 344816990: goto L_0x062c;
            case 346878138: goto L_0x0620;
            case 350376871: goto L_0x0614;
            case 615714517: goto L_0x0609;
            case 715508879: goto L_0x05fd;
            case 728985323: goto L_0x05f1;
            case 731046471: goto L_0x05e5;
            case 734545204: goto L_0x05d9;
            case 802032552: goto L_0x05cd;
            case 991498806: goto L_0x05c1;
            case 1007364121: goto L_0x05b5;
            case 1019917311: goto L_0x05a9;
            case 1019926225: goto L_0x059d;
            case 1020207774: goto L_0x0591;
            case 1020317708: goto L_0x0585;
            case 1060349560: goto L_0x0579;
            case 1060358474: goto L_0x056d;
            case 1060640023: goto L_0x0561;
            case 1060749957: goto L_0x0556;
            case 1073049781: goto L_0x054a;
            case 1078101399: goto L_0x053e;
            case 1110103437: goto L_0x0532;
            case 1160762272: goto L_0x0526;
            case 1172918249: goto L_0x051a;
            case 1234591620: goto L_0x050e;
            case 1281128640: goto L_0x0502;
            case 1281131225: goto L_0x04f6;
            case 1281131340: goto L_0x04ea;
            case 1310789062: goto L_0x04df;
            case 1333118583: goto L_0x04d3;
            case 1361447897: goto L_0x04c7;
            case 1498266155: goto L_0x04bb;
            case 1533804208: goto L_0x04af;
            case 1547988151: goto L_0x04a3;
            case 1561464595: goto L_0x0497;
            case 1563525743: goto L_0x048b;
            case 1567024476: goto L_0x047f;
            case 1810705077: goto L_0x0473;
            case 1815177512: goto L_0x0467;
            case 1963241394: goto L_0x045b;
            case 2014789757: goto L_0x044f;
            case 2022049433: goto L_0x0443;
            case 2048733346: goto L_0x0437;
            case 2099392181: goto L_0x042b;
            case 2140162142: goto L_0x041f;
            default: goto L_0x041d;
        };
    L_0x041d:
        goto L_0x089a;
    L_0x041f:
        r5 = "CHAT_MESSAGE_GEOLIVE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0427:
        r5 = 53;
        goto L_0x089b;
    L_0x042b:
        r5 = "CHANNEL_MESSAGE_PHOTOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0433:
        r5 = 39;
        goto L_0x089b;
    L_0x0437:
        r5 = "CHANNEL_MESSAGE_NOTEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x043f:
        r5 = 24;
        goto L_0x089b;
    L_0x0443:
        r5 = "PINNED_CONTACT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x044b:
        r5 = 80;
        goto L_0x089b;
    L_0x044f:
        r5 = "CHAT_PHOTO_EDITED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0457:
        r5 = 60;
        goto L_0x089b;
    L_0x045b:
        r5 = "LOCKED_MESSAGE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0463:
        r5 = 94;
        goto L_0x089b;
    L_0x0467:
        r5 = "CHANNEL_MESSAGES";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x046f:
        r5 = 41;
        goto L_0x089b;
    L_0x0473:
        r5 = "MESSAGE_INVOICE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x047b:
        r5 = 18;
        goto L_0x089b;
    L_0x047f:
        r5 = "CHAT_MESSAGE_VIDEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0487:
        r5 = 45;
        goto L_0x089b;
    L_0x048b:
        r5 = "CHAT_MESSAGE_ROUND";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0493:
        r5 = 46;
        goto L_0x089b;
    L_0x0497:
        r5 = "CHAT_MESSAGE_PHOTO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x049f:
        r5 = 44;
        goto L_0x089b;
    L_0x04a3:
        r5 = "CHAT_MESSAGE_AUDIO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x04ab:
        r5 = 49;
        goto L_0x089b;
    L_0x04af:
        r5 = "MESSAGE_VIDEOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x04b7:
        r5 = 21;
        goto L_0x089b;
    L_0x04bb:
        r5 = "PHONE_CALL_MISSED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x04c3:
        r5 = 96;
        goto L_0x089b;
    L_0x04c7:
        r5 = "MESSAGE_PHOTOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x04cf:
        r5 = 20;
        goto L_0x089b;
    L_0x04d3:
        r5 = "CHAT_MESSAGE_VIDEOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x04db:
        r5 = 70;
        goto L_0x089b;
    L_0x04df:
        r5 = "MESSAGE_NOTEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x04e7:
        r5 = 1;
        goto L_0x089b;
    L_0x04ea:
        r5 = "MESSAGE_GIF";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x04f2:
        r5 = 15;
        goto L_0x089b;
    L_0x04f6:
        r5 = "MESSAGE_GEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x04fe:
        r5 = 13;
        goto L_0x089b;
    L_0x0502:
        r5 = "MESSAGE_DOC";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x050a:
        r5 = 8;
        goto L_0x089b;
    L_0x050e:
        r5 = "CHAT_MESSAGE_GAME_SCORE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0516:
        r5 = 56;
        goto L_0x089b;
    L_0x051a:
        r5 = "CHANNEL_MESSAGE_GEOLIVE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0522:
        r5 = 34;
        goto L_0x089b;
    L_0x0526:
        r5 = "CHAT_MESSAGE_PHOTOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x052e:
        r5 = 69;
        goto L_0x089b;
    L_0x0532:
        r5 = "CHAT_MESSAGE_NOTEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x053a:
        r5 = 43;
        goto L_0x089b;
    L_0x053e:
        r5 = "CHAT_TITLE_EDITED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0546:
        r5 = 59;
        goto L_0x089b;
    L_0x054a:
        r5 = "PINNED_NOTEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0552:
        r5 = 73;
        goto L_0x089b;
    L_0x0556:
        r5 = "MESSAGE_TEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x055e:
        r5 = 0;
        goto L_0x089b;
    L_0x0561:
        r5 = "MESSAGE_POLL";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0569:
        r5 = 12;
        goto L_0x089b;
    L_0x056d:
        r5 = "MESSAGE_GAME";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0575:
        r5 = 16;
        goto L_0x089b;
    L_0x0579:
        r5 = "MESSAGE_FWDS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0581:
        r5 = 19;
        goto L_0x089b;
    L_0x0585:
        r5 = "CHAT_MESSAGE_TEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x058d:
        r5 = 42;
        goto L_0x089b;
    L_0x0591:
        r5 = "CHAT_MESSAGE_POLL";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0599:
        r5 = 51;
        goto L_0x089b;
    L_0x059d:
        r5 = "CHAT_MESSAGE_GAME";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x05a5:
        r5 = 55;
        goto L_0x089b;
    L_0x05a9:
        r5 = "CHAT_MESSAGE_FWDS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x05b1:
        r5 = 68;
        goto L_0x089b;
    L_0x05b5:
        r5 = "CHANNEL_MESSAGE_GAME_SCORE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x05bd:
        r5 = 37;
        goto L_0x089b;
    L_0x05c1:
        r5 = "PINNED_GEOLIVE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x05c9:
        r5 = 83;
        goto L_0x089b;
    L_0x05cd:
        r5 = "MESSAGE_CONTACT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x05d5:
        r5 = 11;
        goto L_0x089b;
    L_0x05d9:
        r5 = "PINNED_VIDEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x05e1:
        r5 = 75;
        goto L_0x089b;
    L_0x05e5:
        r5 = "PINNED_ROUND";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x05ed:
        r5 = 76;
        goto L_0x089b;
    L_0x05f1:
        r5 = "PINNED_PHOTO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x05f9:
        r5 = 74;
        goto L_0x089b;
    L_0x05fd:
        r5 = "PINNED_AUDIO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0605:
        r5 = 79;
        goto L_0x089b;
    L_0x0609:
        r5 = "MESSAGE_PHOTO_SECRET";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0611:
        r5 = 3;
        goto L_0x089b;
    L_0x0614:
        r5 = "CHANNEL_MESSAGE_VIDEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x061c:
        r5 = 26;
        goto L_0x089b;
    L_0x0620:
        r5 = "CHANNEL_MESSAGE_ROUND";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0628:
        r5 = 27;
        goto L_0x089b;
    L_0x062c:
        r5 = "CHANNEL_MESSAGE_PHOTO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0634:
        r5 = 25;
        goto L_0x089b;
    L_0x0638:
        r5 = "CHANNEL_MESSAGE_AUDIO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0640:
        r5 = 30;
        goto L_0x089b;
    L_0x0644:
        r5 = "CHAT_MESSAGE_STICKER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x064c:
        r5 = 48;
        goto L_0x089b;
    L_0x0650:
        r5 = "MESSAGES";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0658:
        r5 = 22;
        goto L_0x089b;
    L_0x065c:
        r5 = "CHAT_MESSAGE_GIF";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0664:
        r5 = 54;
        goto L_0x089b;
    L_0x0668:
        r5 = "CHAT_MESSAGE_GEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0670:
        r5 = 52;
        goto L_0x089b;
    L_0x0674:
        r5 = "CHAT_MESSAGE_DOC";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x067c:
        r5 = 47;
        goto L_0x089b;
    L_0x0680:
        r5 = "CHAT_LEFT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0688:
        r5 = 65;
        goto L_0x089b;
    L_0x068c:
        r5 = "CHAT_ADD_YOU";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0694:
        r5 = 62;
        goto L_0x089b;
    L_0x0698:
        r5 = "CHAT_DELETE_MEMBER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x06a0:
        r5 = 63;
        goto L_0x089b;
    L_0x06a4:
        r5 = "MESSAGE_SCREENSHOT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x06ac:
        r5 = 6;
        goto L_0x089b;
    L_0x06af:
        r5 = "AUTH_REGION";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x06b7:
        r5 = 90;
        goto L_0x089b;
    L_0x06bb:
        r5 = "CONTACT_JOINED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x06c3:
        r5 = 88;
        goto L_0x089b;
    L_0x06c7:
        r5 = "CHAT_MESSAGE_INVOICE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x06cf:
        r5 = 57;
        goto L_0x089b;
    L_0x06d3:
        r5 = "ENCRYPTION_REQUEST";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x06db:
        r5 = 92;
        goto L_0x089b;
    L_0x06df:
        r5 = "MESSAGE_GEOLIVE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x06e7:
        r5 = 14;
        goto L_0x089b;
    L_0x06eb:
        r5 = "CHAT_DELETE_YOU";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x06f3:
        r5 = 64;
        goto L_0x089b;
    L_0x06f7:
        r5 = "AUTH_UNKNOWN";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x06ff:
        r5 = 89;
        goto L_0x089b;
    L_0x0703:
        r5 = "PINNED_GIF";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x070b:
        r5 = 87;
        goto L_0x089b;
    L_0x070f:
        r5 = "PINNED_GEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0717:
        r5 = 82;
        goto L_0x089b;
    L_0x071b:
        r5 = "PINNED_DOC";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0723:
        r5 = 77;
        goto L_0x089b;
    L_0x0727:
        r5 = "PINNED_GAME_SCORE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x072f:
        r5 = 85;
        goto L_0x089b;
    L_0x0733:
        r5 = "CHANNEL_MESSAGE_STICKER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x073b:
        r5 = 29;
        goto L_0x089b;
    L_0x073f:
        r5 = "PHONE_CALL_REQUEST";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0747:
        r5 = 95;
        goto L_0x089b;
    L_0x074b:
        r5 = "PINNED_STICKER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0753:
        r5 = 78;
        goto L_0x089b;
    L_0x0757:
        r5 = "PINNED_TEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x075f:
        r5 = 72;
        goto L_0x089b;
    L_0x0763:
        r5 = "PINNED_POLL";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x076b:
        r5 = 81;
        goto L_0x089b;
    L_0x076f:
        r5 = "PINNED_GAME";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0777:
        r5 = 84;
        goto L_0x089b;
    L_0x077b:
        r5 = "CHAT_MESSAGE_CONTACT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0783:
        r5 = 50;
        goto L_0x089b;
    L_0x0787:
        r5 = "MESSAGE_VIDEO_SECRET";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x078f:
        r5 = 5;
        goto L_0x089b;
    L_0x0792:
        r5 = "CHANNEL_MESSAGE_TEXT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x079a:
        r5 = 23;
        goto L_0x089b;
    L_0x079e:
        r5 = "CHANNEL_MESSAGE_POLL";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x07a6:
        r5 = 32;
        goto L_0x089b;
    L_0x07aa:
        r5 = "CHANNEL_MESSAGE_GAME";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x07b2:
        r5 = 36;
        goto L_0x089b;
    L_0x07b6:
        r5 = "CHANNEL_MESSAGE_FWDS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x07be:
        r5 = 38;
        goto L_0x089b;
    L_0x07c2:
        r5 = "PINNED_INVOICE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x07ca:
        r5 = 86;
        goto L_0x089b;
    L_0x07ce:
        r5 = "CHAT_RETURNED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x07d6:
        r5 = 66;
        goto L_0x089b;
    L_0x07da:
        r5 = "ENCRYPTED_MESSAGE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x07e2:
        r5 = 91;
        goto L_0x089b;
    L_0x07e6:
        r5 = "ENCRYPTION_ACCEPT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x07ee:
        r5 = 93;
        goto L_0x089b;
    L_0x07f2:
        r5 = "MESSAGE_VIDEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x07fa:
        r5 = 4;
        goto L_0x089b;
    L_0x07fd:
        r5 = "MESSAGE_ROUND";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0805:
        r5 = 7;
        goto L_0x089b;
    L_0x0808:
        r5 = "MESSAGE_PHOTO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0810:
        r5 = 2;
        goto L_0x089b;
    L_0x0813:
        r5 = "MESSAGE_AUDIO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x081b:
        r5 = 10;
        goto L_0x089b;
    L_0x081f:
        r5 = "CHAT_MESSAGES";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0827:
        r5 = 71;
        goto L_0x089b;
    L_0x082b:
        r5 = "CHAT_JOINED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0833:
        r5 = 67;
        goto L_0x089b;
    L_0x0837:
        r5 = "CHAT_ADD_MEMBER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x083f:
        r5 = 61;
        goto L_0x089b;
    L_0x0842:
        r5 = "CHANNEL_MESSAGE_GIF";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x084a:
        r5 = 35;
        goto L_0x089b;
    L_0x084d:
        r5 = "CHANNEL_MESSAGE_GEO";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0855:
        r5 = 33;
        goto L_0x089b;
    L_0x0858:
        r5 = "CHANNEL_MESSAGE_DOC";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0860:
        r5 = 28;
        goto L_0x089b;
    L_0x0863:
        r5 = "CHANNEL_MESSAGE_VIDEOS";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x086b:
        r5 = 40;
        goto L_0x089b;
    L_0x086e:
        r5 = "MESSAGE_STICKER";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0876:
        r5 = 9;
        goto L_0x089b;
    L_0x0879:
        r5 = "CHAT_CREATED";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0881:
        r5 = 58;
        goto L_0x089b;
    L_0x0884:
        r5 = "CHANNEL_MESSAGE_CONTACT";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x088c:
        r5 = 31;
        goto L_0x089b;
    L_0x088f:
        r5 = "MESSAGE_GAME_SCORE";
        r5 = r9.equals(r5);	 Catch:{ Throwable -> 0x0395 }
        if (r5 == 0) goto L_0x089a;
    L_0x0897:
        r5 = 17;
        goto L_0x089b;
    L_0x089a:
        r5 = -1;
    L_0x089b:
        r11 = "Photos";
        r16 = r14;
        r14 = "ChannelMessageFew";
        r30 = r3;
        r3 = " ";
        r31 = r2;
        r2 = "AttachSticker";
        switch(r5) {
            case 0: goto L_0x16aa;
            case 1: goto L_0x1685;
            case 2: goto L_0x1664;
            case 3: goto L_0x1643;
            case 4: goto L_0x1622;
            case 5: goto L_0x1600;
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
            case 22: goto L_0x1370;
            case 23: goto L_0x1355;
            case 24: goto L_0x1331;
            case 25: goto L_0x1314;
            case 26: goto L_0x12f7;
            case 27: goto L_0x12da;
            case 28: goto L_0x12bc;
            case 29: goto L_0x125f;
            case 30: goto L_0x1241;
            case 31: goto L_0x121e;
            case 32: goto L_0x11fb;
            case 33: goto L_0x11dd;
            case 34: goto L_0x11bf;
            case 35: goto L_0x11a1;
            case 36: goto L_0x1183;
            case 37: goto L_0x1164;
            case 38: goto L_0x113a;
            case 39: goto L_0x1116;
            case 40: goto L_0x10f0;
            case 41: goto L_0x10db;
            case 42: goto L_0x10ba;
            case 43: goto L_0x1097;
            case 44: goto L_0x1074;
            case 45: goto L_0x1051;
            case 46: goto L_0x102e;
            case 47: goto L_0x100b;
            case 48: goto L_0x0var_;
            case 49: goto L_0x0f5f;
            case 50: goto L_0x0var_;
            case 51: goto L_0x0var_;
            case 52: goto L_0x0ee4;
            case 53: goto L_0x0ebf;
            case 54: goto L_0x0e9a;
            case 55: goto L_0x0e6e;
            case 56: goto L_0x0e46;
            case 57: goto L_0x0e1a;
            case 58: goto L_0x0dfe;
            case 59: goto L_0x0de2;
            case 60: goto L_0x0dc6;
            case 61: goto L_0x0da3;
            case 62: goto L_0x0d87;
            case 63: goto L_0x0d6b;
            case 64: goto L_0x0d4f;
            case 65: goto L_0x0d33;
            case 66: goto L_0x0d17;
            case 67: goto L_0x0cfb;
            case 68: goto L_0x0ccb;
            case 69: goto L_0x0c9c;
            case 70: goto L_0x0c6b;
            case 71: goto L_0x0c4c;
            case 72: goto L_0x0CLASSNAME;
            case 73: goto L_0x0bde;
            case 74: goto L_0x0baf;
            case 75: goto L_0x0b7f;
            case 76: goto L_0x0b4e;
            case 77: goto L_0x0b1d;
            case 78: goto L_0x0a9d;
            case 79: goto L_0x0a6c;
            case 80: goto L_0x0a2f;
            case 81: goto L_0x09f8;
            case 82: goto L_0x09c6;
            case 83: goto L_0x0999;
            case 84: goto L_0x096c;
            case 85: goto L_0x093d;
            case 86: goto L_0x090e;
            case 87: goto L_0x08de;
            case 88: goto L_0x08d6;
            case 89: goto L_0x08d6;
            case 90: goto L_0x08d6;
            case 91: goto L_0x08b6;
            case 92: goto L_0x08d6;
            case 93: goto L_0x08d6;
            case 94: goto L_0x08d6;
            case 95: goto L_0x08d6;
            case 96: goto L_0x08d6;
            default: goto L_0x08ac;
        };
    L_0x08ac:
        r17 = r10;
        r5 = r16;
        r11 = r23;
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x177d }
        goto L_0x16c9;
    L_0x08b6:
        r2 = "YouHaveNewMessage";
        r3 = NUM; // 0x7f0d0ab3 float:1.874767E38 double:1.053131131E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        r2 = "SecretChatName";
        r3 = NUM; // 0x7f0d08df float:1.874672E38 double:1.0531308996E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        r17 = r10;
        r5 = r16;
        r11 = r23;
        r21 = 0;
        r25 = 1;
        r23 = r2;
        goto L_0x16e6;
    L_0x08d6:
        r17 = r10;
        r5 = r16;
        r11 = r23;
        goto L_0x16df;
    L_0x08de:
        if (r10 == 0) goto L_0x08f7;
    L_0x08e0:
        r2 = "NotificationActionPinnedGif";
        r3 = NUM; // 0x7f0d0625 float:1.8745305E38 double:1.0531305547E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r5 = 0;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x0395 }
        r4[r5] = r11;	 Catch:{ Throwable -> 0x0395 }
        r5 = 1;
        r11 = r15[r5];	 Catch:{ Throwable -> 0x0395 }
        r4[r5] = r11;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0908;
    L_0x08f7:
        r2 = "NotificationActionPinnedGifChannel";
        r3 = NUM; // 0x7f0d0626 float:1.8745307E38 double:1.053130555E-314;
        r4 = 1;
        r5 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r11 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r5[r4] = r11;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);	 Catch:{ Throwable -> 0x0395 }
    L_0x0908:
        r17 = r10;
        r5 = r16;
        goto L_0x09f4;
    L_0x090e:
        r5 = r16;
        if (r10 == 0) goto L_0x092a;
    L_0x0912:
        r2 = "NotificationActionPinnedInvoice";
        r3 = NUM; // 0x7f0d0627 float:1.874531E38 double:1.0531305557E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x09f2;
    L_0x092a:
        r2 = "NotificationActionPinnedInvoiceChannel";
        r3 = NUM; // 0x7f0d0628 float:1.8745311E38 double:1.053130556E-314;
        r4 = 1;
        r11 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x09f2;
    L_0x093d:
        r5 = r16;
        if (r10 == 0) goto L_0x0959;
    L_0x0941:
        r2 = "NotificationActionPinnedGameScore";
        r3 = NUM; // 0x7f0d061f float:1.8745293E38 double:1.0531305517E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x09f2;
    L_0x0959:
        r2 = "NotificationActionPinnedGameScoreChannel";
        r3 = NUM; // 0x7f0d0620 float:1.8745295E38 double:1.053130552E-314;
        r4 = 1;
        r11 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x09f2;
    L_0x096c:
        r5 = r16;
        if (r10 == 0) goto L_0x0987;
    L_0x0970:
        r2 = "NotificationActionPinnedGame";
        r3 = NUM; // 0x7f0d061d float:1.8745289E38 double:1.053130551E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x09f2;
    L_0x0987:
        r2 = "NotificationActionPinnedGameChannel";
        r3 = NUM; // 0x7f0d061e float:1.874529E38 double:1.0531305513E-314;
        r4 = 1;
        r11 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x09f2;
    L_0x0999:
        r5 = r16;
        if (r10 == 0) goto L_0x09b4;
    L_0x099d:
        r2 = "NotificationActionPinnedGeoLive";
        r3 = NUM; // 0x7f0d0623 float:1.8745301E38 double:1.0531305537E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x09f2;
    L_0x09b4:
        r2 = "NotificationActionPinnedGeoLiveChannel";
        r3 = NUM; // 0x7f0d0624 float:1.8745303E38 double:1.053130554E-314;
        r4 = 1;
        r11 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x09f2;
    L_0x09c6:
        r5 = r16;
        if (r10 == 0) goto L_0x09e1;
    L_0x09ca:
        r2 = "NotificationActionPinnedGeo";
        r3 = NUM; // 0x7f0d0621 float:1.8745297E38 double:1.0531305527E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x09f2;
    L_0x09e1:
        r2 = "NotificationActionPinnedGeoChannel";
        r3 = NUM; // 0x7f0d0622 float:1.87453E38 double:1.053130553E-314;
        r4 = 1;
        r11 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11);	 Catch:{ Throwable -> 0x0395 }
    L_0x09f2:
        r17 = r10;
    L_0x09f4:
        r11 = r23;
        goto L_0x15fc;
    L_0x09f8:
        r5 = r16;
        if (r10 == 0) goto L_0x0a18;
    L_0x09fc:
        r2 = "NotificationActionPinnedPoll2";
        r3 = NUM; // 0x7f0d062f float:1.8745325E38 double:1.0531305597E-314;
        r4 = 3;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r16;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r16;	 Catch:{ Throwable -> 0x0395 }
        r11 = 2;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x09f2;
    L_0x0a18:
        r2 = "NotificationActionPinnedPollChannel2";
        r3 = NUM; // 0x7f0d0630 float:1.8745327E38 double:1.05313056E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x09f2;
    L_0x0a2f:
        r5 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0a54;
    L_0x0a35:
        r4 = "NotificationActionPinnedContact2";
        r11 = NUM; // 0x7f0d0619 float:1.874528E38 double:1.053130549E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0395 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r3[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r11, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0a54:
        r3 = "NotificationActionPinnedContactChannel2";
        r4 = NUM; // 0x7f0d061a float:1.8745283E38 double:1.0531305493E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0a6c:
        r5 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0a8a;
    L_0x0a72:
        r3 = "NotificationActionPinnedVoice";
        r4 = NUM; // 0x7f0d063b float:1.874535E38 double:1.0531305656E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0a8a:
        r3 = "NotificationActionPinnedVoiceChannel";
        r4 = NUM; // 0x7f0d063c float:1.8745352E38 double:1.053130566E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r14[r11] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0a9d:
        r5 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0ae6;
    L_0x0aa3:
        r4 = r15.length;	 Catch:{ Throwable -> 0x0395 }
        r11 = 2;
        if (r4 <= r11) goto L_0x0ace;
    L_0x0aa7:
        r4 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4 = android.text.TextUtils.isEmpty(r4);	 Catch:{ Throwable -> 0x0395 }
        if (r4 != 0) goto L_0x0ace;
    L_0x0aaf:
        r4 = "NotificationActionPinnedStickerEmoji";
        r11 = NUM; // 0x7f0d0635 float:1.8745338E38 double:1.0531305626E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0395 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r3[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r11, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0ace:
        r3 = "NotificationActionPinnedSticker";
        r4 = NUM; // 0x7f0d0633 float:1.8745334E38 double:1.0531305616E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0ae6:
        r3 = r15.length;	 Catch:{ Throwable -> 0x0395 }
        r4 = 1;
        if (r3 <= r4) goto L_0x0b0a;
    L_0x0aea:
        r3 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r3 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Throwable -> 0x0395 }
        if (r3 != 0) goto L_0x0b0a;
    L_0x0af2:
        r3 = "NotificationActionPinnedStickerEmojiChannel";
        r4 = NUM; // 0x7f0d0636 float:1.874534E38 double:1.053130563E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0b0a:
        r3 = "NotificationActionPinnedStickerChannel";
        r4 = NUM; // 0x7f0d0634 float:1.8745336E38 double:1.053130562E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r14[r11] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0b1d:
        r5 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0b3b;
    L_0x0b23:
        r3 = "NotificationActionPinnedFile";
        r4 = NUM; // 0x7f0d061b float:1.8745285E38 double:1.05313055E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0b3b:
        r3 = "NotificationActionPinnedFileChannel";
        r4 = NUM; // 0x7f0d061c float:1.8745287E38 double:1.0531305503E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r14[r11] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0b4e:
        r5 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0b6c;
    L_0x0b54:
        r3 = "NotificationActionPinnedRound";
        r4 = NUM; // 0x7f0d0631 float:1.874533E38 double:1.0531305606E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0b6c:
        r3 = "NotificationActionPinnedRoundChannel";
        r4 = NUM; // 0x7f0d0632 float:1.8745332E38 double:1.053130561E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r14[r11] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0b7f:
        r5 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0b9d;
    L_0x0b85:
        r3 = "NotificationActionPinnedVideo";
        r4 = NUM; // 0x7f0d0639 float:1.8745346E38 double:1.0531305646E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0b9d:
        r3 = "NotificationActionPinnedVideoChannel";
        r4 = NUM; // 0x7f0d063a float:1.8745348E38 double:1.053130565E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r14[r11] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0baf:
        r5 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0bcc;
    L_0x0bb5:
        r3 = "NotificationActionPinnedPhoto";
        r4 = NUM; // 0x7f0d062d float:1.8745321E38 double:1.0531305587E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0bcc:
        r3 = "NotificationActionPinnedPhotoChannel";
        r4 = NUM; // 0x7f0d062e float:1.8745323E38 double:1.053130559E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r14[r11] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0bde:
        r5 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0bfb;
    L_0x0be4:
        r3 = "NotificationActionPinnedNoText";
        r4 = NUM; // 0x7f0d062b float:1.8745317E38 double:1.0531305577E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0bfb:
        r3 = "NotificationActionPinnedNoTextChannel";
        r4 = NUM; // 0x7f0d062c float:1.874532E38 double:1.053130558E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r14[r11] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0395 }
    L_0x0c0c:
        r11 = r2;
    L_0x0c0d:
        r17 = r10;
        goto L_0x15fc;
    L_0x0CLASSNAME:
        r5 = r16;
        r2 = r23;
        if (r10 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r4 = "NotificationActionPinnedText";
        r11 = NUM; // 0x7f0d0637 float:1.8745342E38 double:1.0531305636E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0395 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r3[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r11, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0CLASSNAME:
        r3 = "NotificationActionPinnedTextChannel";
        r4 = NUM; // 0x7f0d0638 float:1.8745344E38 double:1.053130564E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0c4c:
        r5 = r16;
        r2 = r23;
        r3 = "NotificationGroupAlbum";
        r4 = NUM; // 0x7f0d0644 float:1.8745368E38 double:1.05313057E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
    L_0x0CLASSNAME:
        r11 = r2;
    L_0x0CLASSNAME:
        r17 = r10;
        goto L_0x1387;
    L_0x0c6b:
        r5 = r16;
        r2 = r23;
        r4 = "NotificationGroupFew";
        r11 = NUM; // 0x7f0d0645 float:1.874537E38 double:1.0531305705E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0395 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r14 = "Videos";
        r16 = 2;
        r15 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ Throwable -> 0x0395 }
        r15 = r15.intValue();	 Catch:{ Throwable -> 0x0395 }
        r14 = org.telegram.messenger.LocaleController.formatPluralString(r14, r15);	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r11, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0CLASSNAME;
    L_0x0c9c:
        r5 = r16;
        r2 = r23;
        r4 = "NotificationGroupFew";
        r3 = NUM; // 0x7f0d0645 float:1.874537E38 double:1.0531305705E-314;
        r14 = 3;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0395 }
        r16 = 0;
        r22 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r14[r16] = r22;	 Catch:{ Throwable -> 0x0395 }
        r16 = 1;
        r23 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r14[r16] = r23;	 Catch:{ Throwable -> 0x0395 }
        r16 = 2;
        r15 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ Throwable -> 0x0395 }
        r15 = r15.intValue();	 Catch:{ Throwable -> 0x0395 }
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r15);	 Catch:{ Throwable -> 0x0395 }
        r14[r16] = r11;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r3, r14);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0CLASSNAME;
    L_0x0ccb:
        r5 = r16;
        r2 = r23;
        r11 = "NotificationGroupForwardedFew";
        r3 = NUM; // 0x7f0d0646 float:1.8745372E38 double:1.053130571E-314;
        r14 = 3;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0395 }
        r16 = 0;
        r22 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r14[r16] = r22;	 Catch:{ Throwable -> 0x0395 }
        r16 = 1;
        r23 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r14[r16] = r23;	 Catch:{ Throwable -> 0x0395 }
        r16 = 2;
        r15 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ Throwable -> 0x0395 }
        r15 = r15.intValue();	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r15);	 Catch:{ Throwable -> 0x0395 }
        r14[r16] = r4;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r11, r3, r14);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0CLASSNAME;
    L_0x0cfb:
        r5 = r16;
        r2 = r23;
        r3 = "NotificationGroupAddSelfMega";
        r4 = NUM; // 0x7f0d0643 float:1.8745366E38 double:1.0531305695E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0d17:
        r5 = r16;
        r2 = r23;
        r3 = "NotificationGroupAddSelf";
        r4 = NUM; // 0x7f0d0642 float:1.8745364E38 double:1.053130569E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0d33:
        r5 = r16;
        r2 = r23;
        r3 = "NotificationGroupLeftMember";
        r4 = NUM; // 0x7f0d0649 float:1.8745378E38 double:1.0531305725E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0d4f:
        r5 = r16;
        r2 = r23;
        r3 = "NotificationGroupKickYou";
        r4 = NUM; // 0x7f0d0648 float:1.8745376E38 double:1.053130572E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0d6b:
        r5 = r16;
        r2 = r23;
        r3 = "NotificationGroupKickMember";
        r4 = NUM; // 0x7f0d0647 float:1.8745374E38 double:1.0531305715E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0d87:
        r5 = r16;
        r2 = r23;
        r3 = "NotificationInvitedToGroup";
        r4 = NUM; // 0x7f0d064a float:1.874538E38 double:1.053130573E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0da3:
        r5 = r16;
        r2 = r23;
        r4 = "NotificationGroupAddMember";
        r11 = NUM; // 0x7f0d0641 float:1.8745362E38 double:1.0531305685E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0395 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r3[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r11, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0dc6:
        r5 = r16;
        r2 = r23;
        r3 = "NotificationEditedGroupPhoto";
        r4 = NUM; // 0x7f0d0640 float:1.874536E38 double:1.053130568E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0de2:
        r5 = r16;
        r2 = r23;
        r3 = "NotificationEditedGroupName";
        r4 = NUM; // 0x7f0d063f float:1.8745358E38 double:1.0531305676E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0dfe:
        r5 = r16;
        r2 = r23;
        r3 = "NotificationInvitedToGroup";
        r4 = NUM; // 0x7f0d064a float:1.874538E38 double:1.053130573E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0e1a:
        r5 = r16;
        r2 = r23;
        r4 = "NotificationMessageGroupInvoice";
        r11 = NUM; // 0x7f0d065b float:1.8745415E38 double:1.0531305814E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0395 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r3[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r11, r3);	 Catch:{ Throwable -> 0x0395 }
        r3 = "PaymentInvoice";
        r11 = NUM; // 0x7f0d07b7 float:1.874612E38 double:1.0531307533E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0var_;
    L_0x0e46:
        r5 = r16;
        r2 = r23;
        r4 = "NotificationMessageGroupGameScored";
        r11 = NUM; // 0x7f0d0659 float:1.874541E38 double:1.0531305804E-314;
        r3 = 4;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0395 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r14 = 2;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r3[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 3;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r3[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r11, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0c;
    L_0x0e6e:
        r5 = r16;
        r2 = r23;
        r4 = "NotificationMessageGroupGame";
        r11 = NUM; // 0x7f0d0658 float:1.8745409E38 double:1.05313058E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0395 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r3[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r11, r3);	 Catch:{ Throwable -> 0x0395 }
        r3 = "AttachGame";
        r11 = NUM; // 0x7f0d013b float:1.8742754E38 double:1.053129933E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0var_;
    L_0x0e9a:
        r5 = r16;
        r2 = r23;
        r3 = "NotificationMessageGroupGif";
        r4 = NUM; // 0x7f0d065a float:1.8745413E38 double:1.053130581E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        r3 = "AttachGif";
        r11 = NUM; // 0x7f0d013c float:1.8742756E38 double:1.0531299337E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0var_;
    L_0x0ebf:
        r5 = r16;
        r2 = r23;
        r3 = "NotificationMessageGroupLiveLocation";
        r4 = NUM; // 0x7f0d065c float:1.8745417E38 double:1.053130582E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        r3 = "AttachLiveLocation";
        r11 = NUM; // 0x7f0d0141 float:1.8742766E38 double:1.053129936E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0var_;
    L_0x0ee4:
        r5 = r16;
        r2 = r23;
        r3 = "NotificationMessageGroupMap";
        r4 = NUM; // 0x7f0d065d float:1.8745419E38 double:1.0531305824E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        r3 = "AttachLocation";
        r11 = NUM; // 0x7f0d0143 float:1.874277E38 double:1.053129937E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0var_;
    L_0x0var_:
        r5 = r16;
        r2 = r23;
        r4 = "NotificationMessageGroupPoll2";
        r11 = NUM; // 0x7f0d0661 float:1.8745427E38 double:1.0531305844E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0395 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r3[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r11, r3);	 Catch:{ Throwable -> 0x0395 }
        r3 = "Poll";
        r11 = NUM; // 0x7f0d0813 float:1.8746307E38 double:1.053130799E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0var_;
    L_0x0var_:
        r5 = r16;
        r2 = r23;
        r4 = "NotificationMessageGroupContact2";
        r11 = NUM; // 0x7f0d0656 float:1.8745405E38 double:1.053130579E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0395 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r3[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r3[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r11, r3);	 Catch:{ Throwable -> 0x0395 }
        r3 = "AttachContact";
        r11 = NUM; // 0x7f0d0137 float:1.8742746E38 double:1.053129931E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0var_;
    L_0x0f5f:
        r5 = r16;
        r2 = r23;
        r3 = "NotificationMessageGroupAudio";
        r4 = NUM; // 0x7f0d0655 float:1.8745403E38 double:1.0531305784E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r11);	 Catch:{ Throwable -> 0x0395 }
        r3 = "AttachAudio";
        r11 = NUM; // 0x7f0d0135 float:1.8742741E38 double:1.05312993E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r11);	 Catch:{ Throwable -> 0x0395 }
    L_0x0var_:
        r11 = r2;
        r21 = r3;
        goto L_0x1539;
    L_0x0var_:
        r5 = r16;
        r4 = r23;
        r11 = r15.length;	 Catch:{ Throwable -> 0x0395 }
        r14 = 2;
        if (r11 <= r14) goto L_0x0fd4;
    L_0x0f8f:
        r11 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r11 = android.text.TextUtils.isEmpty(r11);	 Catch:{ Throwable -> 0x0395 }
        if (r11 != 0) goto L_0x0fd4;
    L_0x0var_:
        r11 = "NotificationMessageGroupStickerEmoji";
        r14 = 3;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0395 }
        r21 = 0;
        r22 = r15[r21];	 Catch:{ Throwable -> 0x0395 }
        r14[r21] = r22;	 Catch:{ Throwable -> 0x0395 }
        r21 = 1;
        r23 = r15[r21];	 Catch:{ Throwable -> 0x0395 }
        r14[r21] = r23;	 Catch:{ Throwable -> 0x0395 }
        r17 = 2;
        r21 = r15[r17];	 Catch:{ Throwable -> 0x0395 }
        r14[r17] = r21;	 Catch:{ Throwable -> 0x0395 }
        r23 = r4;
        r4 = NUM; // 0x7f0d0664 float:1.8745433E38 double:1.053130586E-314;
        r4 = org.telegram.messenger.LocaleController.formatString(r11, r4, r14);	 Catch:{ Throwable -> 0x0395 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0395 }
        r11.<init>();	 Catch:{ Throwable -> 0x0395 }
        r14 = r15[r17];	 Catch:{ Throwable -> 0x0395 }
        r11.append(r14);	 Catch:{ Throwable -> 0x0395 }
        r11.append(r3);	 Catch:{ Throwable -> 0x0395 }
        r3 = NUM; // 0x7f0d014a float:1.8742784E38 double:1.0531299406E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        r11.append(r2);	 Catch:{ Throwable -> 0x0395 }
        r2 = r11.toString();	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x0fd4:
        r23 = r4;
        r4 = "NotificationMessageGroupSticker";
        r11 = NUM; // 0x7f0d0663 float:1.874543E38 double:1.0531305853E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0395 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r14[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r14[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r11, r14);	 Catch:{ Throwable -> 0x0395 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0395 }
        r11.<init>();	 Catch:{ Throwable -> 0x0395 }
        r14 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r11.append(r14);	 Catch:{ Throwable -> 0x0395 }
        r11.append(r3);	 Catch:{ Throwable -> 0x0395 }
        r3 = NUM; // 0x7f0d014a float:1.8742784E38 double:1.0531299406E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        r11.append(r2);	 Catch:{ Throwable -> 0x0395 }
        r2 = r11.toString();	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x100b:
        r5 = r16;
        r2 = "NotificationMessageGroupDocument";
        r3 = NUM; // 0x7f0d0657 float:1.8745407E38 double:1.0531305794E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachDocument";
        r3 = NUM; // 0x7f0d013a float:1.8742752E38 double:1.0531299327E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x102e:
        r5 = r16;
        r2 = "NotificationMessageGroupRound";
        r3 = NUM; // 0x7f0d0662 float:1.8745429E38 double:1.053130585E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachRound";
        r3 = NUM; // 0x7f0d0149 float:1.8742782E38 double:1.05312994E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x1051:
        r5 = r16;
        r2 = "NotificationMessageGroupVideo";
        r3 = NUM; // 0x7f0d0666 float:1.8745437E38 double:1.053130587E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachVideo";
        r3 = NUM; // 0x7f0d014d float:1.874279E38 double:1.053129942E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x1074:
        r5 = r16;
        r2 = "NotificationMessageGroupPhoto";
        r3 = NUM; // 0x7f0d0660 float:1.8745425E38 double:1.053130584E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachPhoto";
        r3 = NUM; // 0x7f0d0147 float:1.8742778E38 double:1.053129939E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x1097:
        r5 = r16;
        r2 = "NotificationMessageGroupNoText";
        r3 = NUM; // 0x7f0d065f float:1.8745423E38 double:1.0531305834E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        r2 = "Message";
        r3 = NUM; // 0x7f0d05a5 float:1.8745046E38 double:1.0531304915E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x10ba:
        r5 = r16;
        r3 = "NotificationMessageGroupText";
        r4 = NUM; // 0x7f0d0665 float:1.8745435E38 double:1.0531305863E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r2[r11] = r16;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r2[r11] = r16;	 Catch:{ Throwable -> 0x0395 }
        r11 = 2;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r2[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r2);	 Catch:{ Throwable -> 0x0395 }
        r2 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x10db:
        r5 = r16;
        r2 = "ChannelMessageAlbum";
        r3 = NUM; // 0x7f0d0234 float:1.8743259E38 double:1.053130056E-314;
        r4 = 1;
        r11 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x1383;
    L_0x10f0:
        r5 = r16;
        r2 = 2;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0395 }
        r3 = 0;
        r4 = r15[r3];	 Catch:{ Throwable -> 0x0395 }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x0395 }
        r3 = "Videos";
        r4 = 1;
        r11 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ Throwable -> 0x0395 }
        r11 = r11.intValue();	 Catch:{ Throwable -> 0x0395 }
        r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r11);	 Catch:{ Throwable -> 0x0395 }
        r2[r4] = r3;	 Catch:{ Throwable -> 0x0395 }
        r3 = NUM; // 0x7f0d0238 float:1.8743267E38 double:1.053130058E-314;
        r4 = org.telegram.messenger.LocaleController.formatString(r14, r3, r2);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x1383;
    L_0x1116:
        r5 = r16;
        r2 = 2;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0395 }
        r3 = 0;
        r4 = r15[r3];	 Catch:{ Throwable -> 0x0395 }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x0395 }
        r3 = 1;
        r4 = r15[r3];	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.Utilities.parseInt(r4);	 Catch:{ Throwable -> 0x0395 }
        r4 = r4.intValue();	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatPluralString(r11, r4);	 Catch:{ Throwable -> 0x0395 }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x0395 }
        r3 = NUM; // 0x7f0d0238 float:1.8743267E38 double:1.053130058E-314;
        r4 = org.telegram.messenger.LocaleController.formatString(r14, r3, r2);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x1383;
    L_0x113a:
        r5 = r16;
        r2 = 2;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0395 }
        r3 = 0;
        r4 = r15[r3];	 Catch:{ Throwable -> 0x0395 }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x0395 }
        r3 = "ForwardedMessageCount";
        r4 = 1;
        r11 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ Throwable -> 0x0395 }
        r11 = r11.intValue();	 Catch:{ Throwable -> 0x0395 }
        r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r11);	 Catch:{ Throwable -> 0x0395 }
        r3 = r3.toLowerCase();	 Catch:{ Throwable -> 0x0395 }
        r2[r4] = r3;	 Catch:{ Throwable -> 0x0395 }
        r3 = NUM; // 0x7f0d0238 float:1.8743267E38 double:1.053130058E-314;
        r4 = org.telegram.messenger.LocaleController.formatString(r14, r3, r2);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x1383;
    L_0x1164:
        r5 = r16;
        r3 = "NotificationMessageGameScored";
        r4 = NUM; // 0x7f0d0653 float:1.8745398E38 double:1.0531305774E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r2[r11] = r16;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        r16 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r2[r11] = r16;	 Catch:{ Throwable -> 0x0395 }
        r11 = 2;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r2[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r2);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x09f2;
    L_0x1183:
        r5 = r16;
        r2 = "NotificationMessageGame";
        r3 = NUM; // 0x7f0d0652 float:1.8745396E38 double:1.053130577E-314;
        r4 = 1;
        r11 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachGame";
        r3 = NUM; // 0x7f0d013b float:1.8742754E38 double:1.053129933E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x11a1:
        r5 = r16;
        r2 = "ChannelMessageGIF";
        r3 = NUM; // 0x7f0d0239 float:1.8743269E38 double:1.0531300587E-314;
        r4 = 1;
        r11 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachGif";
        r3 = NUM; // 0x7f0d013c float:1.8742756E38 double:1.0531299337E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x11bf:
        r5 = r16;
        r2 = "ChannelMessageLiveLocation";
        r3 = NUM; // 0x7f0d023a float:1.874327E38 double:1.053130059E-314;
        r4 = 1;
        r11 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachLiveLocation";
        r3 = NUM; // 0x7f0d0141 float:1.8742766E38 double:1.053129936E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x11dd:
        r5 = r16;
        r2 = "ChannelMessageMap";
        r3 = NUM; // 0x7f0d023b float:1.8743273E38 double:1.0531300597E-314;
        r4 = 1;
        r11 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachLocation";
        r3 = NUM; // 0x7f0d0143 float:1.874277E38 double:1.053129937E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x11fb:
        r5 = r16;
        r2 = "ChannelMessagePoll2";
        r3 = NUM; // 0x7f0d023f float:1.874328E38 double:1.0531300616E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        r2 = "Poll";
        r3 = NUM; // 0x7f0d0813 float:1.8746307E38 double:1.053130799E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x121e:
        r5 = r16;
        r2 = "ChannelMessageContact2";
        r3 = NUM; // 0x7f0d0236 float:1.8743263E38 double:1.053130057E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachContact";
        r3 = NUM; // 0x7f0d0137 float:1.8742746E38 double:1.053129931E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x1241:
        r5 = r16;
        r2 = "ChannelMessageAudio";
        r3 = NUM; // 0x7f0d0235 float:1.874326E38 double:1.0531300567E-314;
        r4 = 1;
        r11 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachAudio";
        r3 = NUM; // 0x7f0d0135 float:1.8742741E38 double:1.05312993E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x125f:
        r5 = r16;
        r4 = r15.length;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        if (r4 <= r11) goto L_0x12a2;
    L_0x1265:
        r4 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4 = android.text.TextUtils.isEmpty(r4);	 Catch:{ Throwable -> 0x0395 }
        if (r4 != 0) goto L_0x12a2;
    L_0x126d:
        r4 = "ChannelMessageStickerEmoji";
        r11 = NUM; // 0x7f0d0242 float:1.8743287E38 double:1.053130063E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0395 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r14[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r14[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r11, r14);	 Catch:{ Throwable -> 0x0395 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0395 }
        r11.<init>();	 Catch:{ Throwable -> 0x0395 }
        r14 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r11.append(r14);	 Catch:{ Throwable -> 0x0395 }
        r11.append(r3);	 Catch:{ Throwable -> 0x0395 }
        r3 = NUM; // 0x7f0d014a float:1.8742784E38 double:1.0531299406E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        r11.append(r2);	 Catch:{ Throwable -> 0x0395 }
        r2 = r11.toString();	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x12a2:
        r3 = "ChannelMessageSticker";
        r4 = NUM; // 0x7f0d0241 float:1.8743285E38 double:1.0531300626E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r14[r11] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0395 }
        r3 = NUM; // 0x7f0d014a float:1.8742784E38 double:1.0531299406E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x12bc:
        r5 = r16;
        r2 = "ChannelMessageDocument";
        r3 = NUM; // 0x7f0d0237 float:1.8743265E38 double:1.0531300577E-314;
        r4 = 1;
        r11 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachDocument";
        r3 = NUM; // 0x7f0d013a float:1.8742752E38 double:1.0531299327E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x12da:
        r5 = r16;
        r2 = "ChannelMessageRound";
        r3 = NUM; // 0x7f0d0240 float:1.8743283E38 double:1.053130062E-314;
        r4 = 1;
        r11 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachRound";
        r3 = NUM; // 0x7f0d0149 float:1.8742782E38 double:1.05312994E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x12f7:
        r5 = r16;
        r2 = "ChannelMessageVideo";
        r3 = NUM; // 0x7f0d0243 float:1.874329E38 double:1.0531300636E-314;
        r4 = 1;
        r11 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachVideo";
        r3 = NUM; // 0x7f0d014d float:1.874279E38 double:1.053129942E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x1314:
        r5 = r16;
        r2 = "ChannelMessagePhoto";
        r3 = NUM; // 0x7f0d023e float:1.8743279E38 double:1.053130061E-314;
        r4 = 1;
        r11 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachPhoto";
        r3 = NUM; // 0x7f0d0147 float:1.8742778E38 double:1.053129939E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x1331:
        r5 = r16;
        r2 = "ChannelMessageNoText";
        r3 = NUM; // 0x7f0d023d float:1.8743277E38 double:1.0531300606E-314;
        r4 = 1;
        r11 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11);	 Catch:{ Throwable -> 0x0395 }
        r2 = "Message";
        r3 = NUM; // 0x7f0d05a5 float:1.8745046E38 double:1.0531304915E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
    L_0x134d:
        r21 = r2;
        r17 = r10;
        r11 = r23;
        goto L_0x16a7;
    L_0x1355:
        r5 = r16;
        r2 = "NotificationMessageText";
        r3 = NUM; // 0x7f0d0673 float:1.8745463E38 double:1.053130593E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        r2 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        goto L_0x134d;
    L_0x1370:
        r5 = r16;
        r2 = "NotificationMessageAlbum";
        r3 = NUM; // 0x7f0d064c float:1.8745384E38 double:1.053130574E-314;
        r4 = 1;
        r11 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r11[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11);	 Catch:{ Throwable -> 0x0395 }
    L_0x1383:
        r17 = r10;
        r11 = r23;
    L_0x1387:
        r23 = r25;
        r21 = 0;
        r25 = 1;
        goto L_0x16e6;
    L_0x138f:
        r5 = r16;
        r2 = "NotificationMessageFew";
        r3 = NUM; // 0x7f0d0650 float:1.8745392E38 double:1.053130576E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ Throwable -> 0x0395 }
        r4[r11] = r14;	 Catch:{ Throwable -> 0x0395 }
        r11 = "Videos";
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ Throwable -> 0x0395 }
        r15 = r15.intValue();	 Catch:{ Throwable -> 0x0395 }
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r15);	 Catch:{ Throwable -> 0x0395 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x1383;
    L_0x13b6:
        r5 = r16;
        r2 = "NotificationMessageFew";
        r3 = NUM; // 0x7f0d0650 float:1.8745392E38 double:1.053130576E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r4[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ Throwable -> 0x0395 }
        r15 = r15.intValue();	 Catch:{ Throwable -> 0x0395 }
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r15);	 Catch:{ Throwable -> 0x0395 }
        r4[r14] = r11;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x1383;
    L_0x13db:
        r5 = r16;
        r11 = r23;
        r2 = "NotificationMessageForwardFew";
        r3 = NUM; // 0x7f0d0651 float:1.8745394E38 double:1.0531305764E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0395 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r14[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r16 = 1;
        r15 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ Throwable -> 0x0395 }
        r15 = r15.intValue();	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r15);	 Catch:{ Throwable -> 0x0395 }
        r14[r16] = r4;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0CLASSNAME;
    L_0x1405:
        r5 = r16;
        r11 = r23;
        r2 = "NotificationMessageInvoice";
        r3 = NUM; // 0x7f0d0667 float:1.874544E38 double:1.0531305873E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r4[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        r2 = "PaymentInvoice";
        r3 = NUM; // 0x7f0d07b7 float:1.874612E38 double:1.0531307533E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x1537;
    L_0x142a:
        r5 = r16;
        r11 = r23;
        r3 = "NotificationMessageGameScored";
        r4 = NUM; // 0x7f0d0653 float:1.8745398E38 double:1.0531305774E-314;
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0395 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r2[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r2[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r2[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r2);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x0c0d;
    L_0x144d:
        r5 = r16;
        r11 = r23;
        r2 = "NotificationMessageGame";
        r3 = NUM; // 0x7f0d0652 float:1.8745396E38 double:1.053130577E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r4[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachGame";
        r3 = NUM; // 0x7f0d013b float:1.8742754E38 double:1.053129933E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x1537;
    L_0x1472:
        r5 = r16;
        r11 = r23;
        r2 = "NotificationMessageGif";
        r3 = NUM; // 0x7f0d0654 float:1.87454E38 double:1.053130578E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r15 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r14[r4] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachGif";
        r3 = NUM; // 0x7f0d013c float:1.8742756E38 double:1.0531299337E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x1537;
    L_0x1492:
        r5 = r16;
        r11 = r23;
        r2 = "NotificationMessageLiveLocation";
        r3 = NUM; // 0x7f0d0668 float:1.8745441E38 double:1.053130588E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r15 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r14[r4] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachLiveLocation";
        r3 = NUM; // 0x7f0d0141 float:1.8742766E38 double:1.053129936E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x1537;
    L_0x14b2:
        r5 = r16;
        r11 = r23;
        r2 = "NotificationMessageMap";
        r3 = NUM; // 0x7f0d0669 float:1.8745443E38 double:1.0531305883E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r15 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r14[r4] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachLocation";
        r3 = NUM; // 0x7f0d0143 float:1.874277E38 double:1.053129937E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x1537;
    L_0x14d1:
        r5 = r16;
        r11 = r23;
        r2 = "NotificationMessagePoll2";
        r3 = NUM; // 0x7f0d066d float:1.8745451E38 double:1.0531305903E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r4[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        r2 = "Poll";
        r3 = NUM; // 0x7f0d0813 float:1.8746307E38 double:1.053130799E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x1537;
    L_0x14f5:
        r5 = r16;
        r11 = r23;
        r2 = "NotificationMessageContact2";
        r3 = NUM; // 0x7f0d064e float:1.8745388E38 double:1.053130575E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r4[r14] = r16;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r4[r14] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachContact";
        r3 = NUM; // 0x7f0d0137 float:1.8742746E38 double:1.053129931E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x1537;
    L_0x1519:
        r5 = r16;
        r11 = r23;
        r2 = "NotificationMessageAudio";
        r3 = NUM; // 0x7f0d064d float:1.8745386E38 double:1.0531305745E-314;
        r4 = 1;
        r14 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r15 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r14[r4] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r14);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachAudio";
        r3 = NUM; // 0x7f0d0135 float:1.8742741E38 double:1.05312993E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
    L_0x1537:
        r21 = r2;
    L_0x1539:
        r17 = r10;
        goto L_0x16a7;
    L_0x153d:
        r5 = r16;
        r11 = r23;
        r4 = r15.length;	 Catch:{ Throwable -> 0x0395 }
        r14 = 1;
        if (r4 <= r14) goto L_0x1584;
    L_0x1545:
        r4 = r15[r14];	 Catch:{ Throwable -> 0x0395 }
        r4 = android.text.TextUtils.isEmpty(r4);	 Catch:{ Throwable -> 0x0395 }
        if (r4 != 0) goto L_0x1584;
    L_0x154d:
        r4 = "NotificationMessageStickerEmoji";
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ Throwable -> 0x0395 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r14[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r14[r16] = r17;	 Catch:{ Throwable -> 0x0395 }
        r17 = r10;
        r10 = NUM; // 0x7f0d0672 float:1.8745461E38 double:1.0531305928E-314;
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r10, r14);	 Catch:{ Throwable -> 0x0395 }
        r10 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0395 }
        r10.<init>();	 Catch:{ Throwable -> 0x0395 }
        r14 = r15[r16];	 Catch:{ Throwable -> 0x0395 }
        r10.append(r14);	 Catch:{ Throwable -> 0x0395 }
        r10.append(r3);	 Catch:{ Throwable -> 0x0395 }
        r3 = NUM; // 0x7f0d014a float:1.8742784E38 double:1.0531299406E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        r10.append(r2);	 Catch:{ Throwable -> 0x0395 }
        r2 = r10.toString();	 Catch:{ Throwable -> 0x0395 }
        goto L_0x16a5;
    L_0x1584:
        r17 = r10;
        r3 = "NotificationMessageSticker";
        r4 = NUM; // 0x7f0d0671 float:1.874546E38 double:1.0531305923E-314;
        r10 = 1;
        r14 = new java.lang.Object[r10];	 Catch:{ Throwable -> 0x0395 }
        r10 = 0;
        r15 = r15[r10];	 Catch:{ Throwable -> 0x0395 }
        r14[r10] = r15;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r14);	 Catch:{ Throwable -> 0x0395 }
        r3 = NUM; // 0x7f0d014a float:1.8742784E38 double:1.0531299406E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x16a5;
    L_0x15a0:
        r17 = r10;
        r5 = r16;
        r11 = r23;
        r2 = "NotificationMessageDocument";
        r3 = NUM; // 0x7f0d064f float:1.874539E38 double:1.0531305755E-314;
        r4 = 1;
        r10 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r10[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachDocument";
        r3 = NUM; // 0x7f0d013a float:1.8742752E38 double:1.0531299327E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x16a5;
    L_0x15c2:
        r17 = r10;
        r5 = r16;
        r11 = r23;
        r2 = "NotificationMessageRound";
        r3 = NUM; // 0x7f0d066e float:1.8745453E38 double:1.053130591E-314;
        r4 = 1;
        r10 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r10[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachRound";
        r3 = NUM; // 0x7f0d0149 float:1.8742782E38 double:1.05312994E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x16a5;
    L_0x15e4:
        r17 = r10;
        r5 = r16;
        r11 = r23;
        r2 = "ActionTakeScreenshoot";
        r3 = NUM; // 0x7f0d008e float:1.8742403E38 double:1.0531298477E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        r3 = "un1";
        r4 = 0;
        r10 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = r2.replace(r3, r10);	 Catch:{ Throwable -> 0x0395 }
    L_0x15fc:
        r23 = r25;
        goto L_0x16e2;
    L_0x1600:
        r17 = r10;
        r5 = r16;
        r11 = r23;
        r2 = "NotificationMessageSDVideo";
        r3 = NUM; // 0x7f0d0670 float:1.8745457E38 double:1.053130592E-314;
        r4 = 1;
        r10 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r10[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachDestructingVideo";
        r3 = NUM; // 0x7f0d0139 float:1.874275E38 double:1.053129932E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x16a5;
    L_0x1622:
        r17 = r10;
        r5 = r16;
        r11 = r23;
        r2 = "NotificationMessageVideo";
        r3 = NUM; // 0x7f0d0674 float:1.8745465E38 double:1.0531305937E-314;
        r4 = 1;
        r10 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r10[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachVideo";
        r3 = NUM; // 0x7f0d014d float:1.874279E38 double:1.053129942E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x16a5;
    L_0x1643:
        r17 = r10;
        r5 = r16;
        r11 = r23;
        r2 = "NotificationMessageSDPhoto";
        r3 = NUM; // 0x7f0d066f float:1.8745455E38 double:1.0531305913E-314;
        r4 = 1;
        r10 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r10[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachDestructingPhoto";
        r3 = NUM; // 0x7f0d0138 float:1.8742748E38 double:1.0531299317E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x16a5;
    L_0x1664:
        r17 = r10;
        r5 = r16;
        r11 = r23;
        r2 = "NotificationMessagePhoto";
        r3 = NUM; // 0x7f0d066c float:1.874545E38 double:1.05313059E-314;
        r4 = 1;
        r10 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r10[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x0395 }
        r2 = "AttachPhoto";
        r3 = NUM; // 0x7f0d0147 float:1.8742778E38 double:1.053129939E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
        goto L_0x16a5;
    L_0x1685:
        r17 = r10;
        r5 = r16;
        r11 = r23;
        r2 = "NotificationMessageNoText";
        r3 = NUM; // 0x7f0d066b float:1.8745447E38 double:1.0531305893E-314;
        r4 = 1;
        r10 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r4 = 0;
        r14 = r15[r4];	 Catch:{ Throwable -> 0x0395 }
        r10[r4] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10);	 Catch:{ Throwable -> 0x0395 }
        r2 = "Message";
        r3 = NUM; // 0x7f0d05a5 float:1.8745046E38 double:1.0531304915E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Throwable -> 0x0395 }
    L_0x16a5:
        r21 = r2;
    L_0x16a7:
        r23 = r25;
        goto L_0x16e4;
    L_0x16aa:
        r17 = r10;
        r5 = r16;
        r11 = r23;
        r2 = "NotificationMessageText";
        r3 = NUM; // 0x7f0d0673 float:1.8745463E38 double:1.053130593E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0395 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ Throwable -> 0x0395 }
        r4[r10] = r14;	 Catch:{ Throwable -> 0x0395 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ Throwable -> 0x0395 }
        r4[r10] = r14;	 Catch:{ Throwable -> 0x0395 }
        r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);	 Catch:{ Throwable -> 0x0395 }
        r2 = r15[r10];	 Catch:{ Throwable -> 0x0395 }
        goto L_0x16a5;
    L_0x16c9:
        if (r2 == 0) goto L_0x16df;
    L_0x16cb:
        r2 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0395 }
        r2.<init>();	 Catch:{ Throwable -> 0x0395 }
        r3 = "unhandled loc_key = ";
        r2.append(r3);	 Catch:{ Throwable -> 0x0395 }
        r2.append(r9);	 Catch:{ Throwable -> 0x0395 }
        r2 = r2.toString();	 Catch:{ Throwable -> 0x0395 }
        org.telegram.messenger.FileLog.w(r2);	 Catch:{ Throwable -> 0x0395 }
    L_0x16df:
        r23 = r25;
        r4 = 0;
    L_0x16e2:
        r21 = 0;
    L_0x16e4:
        r25 = 0;
    L_0x16e6:
        if (r4 == 0) goto L_0x1774;
    L_0x16e8:
        r2 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Throwable -> 0x177d }
        r2.<init>();	 Catch:{ Throwable -> 0x177d }
        r2.id = r5;	 Catch:{ Throwable -> 0x177d }
        r2.random_id = r6;	 Catch:{ Throwable -> 0x177d }
        if (r21 == 0) goto L_0x16f6;
    L_0x16f3:
        r3 = r21;
        goto L_0x16f7;
    L_0x16f6:
        r3 = r4;
    L_0x16f7:
        r2.message = r3;	 Catch:{ Throwable -> 0x177d }
        r5 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r5 = r34 / r5;
        r3 = (int) r5;	 Catch:{ Throwable -> 0x177d }
        r2.date = r3;	 Catch:{ Throwable -> 0x177d }
        if (r1 == 0) goto L_0x1709;
    L_0x1702:
        r3 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage;	 Catch:{ Throwable -> 0x0395 }
        r3.<init>();	 Catch:{ Throwable -> 0x0395 }
        r2.action = r3;	 Catch:{ Throwable -> 0x0395 }
    L_0x1709:
        if (r8 == 0) goto L_0x1712;
    L_0x170b:
        r3 = r2.flags;	 Catch:{ Throwable -> 0x0395 }
        r5 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r3 = r3 | r5;
        r2.flags = r3;	 Catch:{ Throwable -> 0x0395 }
    L_0x1712:
        r2.dialog_id = r12;	 Catch:{ Throwable -> 0x177d }
        if (r31 == 0) goto L_0x1724;
    L_0x1716:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChannel;	 Catch:{ Throwable -> 0x0395 }
        r3.<init>();	 Catch:{ Throwable -> 0x0395 }
        r2.to_id = r3;	 Catch:{ Throwable -> 0x0395 }
        r3 = r2.to_id;	 Catch:{ Throwable -> 0x0395 }
        r8 = r31;
        r3.channel_id = r8;	 Catch:{ Throwable -> 0x0395 }
        goto L_0x173f;
    L_0x1724:
        if (r11 == 0) goto L_0x1732;
    L_0x1726:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ Throwable -> 0x0395 }
        r3.<init>();	 Catch:{ Throwable -> 0x0395 }
        r2.to_id = r3;	 Catch:{ Throwable -> 0x0395 }
        r3 = r2.to_id;	 Catch:{ Throwable -> 0x0395 }
        r3.chat_id = r11;	 Catch:{ Throwable -> 0x0395 }
        goto L_0x173f;
    L_0x1732:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ Throwable -> 0x177d }
        r3.<init>();	 Catch:{ Throwable -> 0x177d }
        r2.to_id = r3;	 Catch:{ Throwable -> 0x177d }
        r3 = r2.to_id;	 Catch:{ Throwable -> 0x177d }
        r5 = r30;
        r3.user_id = r5;	 Catch:{ Throwable -> 0x177d }
    L_0x173f:
        r10 = r17;
        r2.from_id = r10;	 Catch:{ Throwable -> 0x177d }
        if (r20 != 0) goto L_0x174a;
    L_0x1745:
        if (r1 == 0) goto L_0x1748;
    L_0x1747:
        goto L_0x174a;
    L_0x1748:
        r1 = 0;
        goto L_0x174b;
    L_0x174a:
        r1 = 1;
    L_0x174b:
        r2.mentioned = r1;	 Catch:{ Throwable -> 0x177d }
        r1 = r19;
        r2.silent = r1;	 Catch:{ Throwable -> 0x177d }
        r1 = new org.telegram.messenger.MessageObject;	 Catch:{ Throwable -> 0x177d }
        r19 = r1;
        r20 = r29;
        r21 = r2;
        r22 = r4;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27);	 Catch:{ Throwable -> 0x177d }
        r2 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x177d }
        r2.<init>();	 Catch:{ Throwable -> 0x177d }
        r2.add(r1);	 Catch:{ Throwable -> 0x177d }
        r1 = org.telegram.messenger.NotificationsController.getInstance(r29);	 Catch:{ Throwable -> 0x177d }
        r3 = r32;
        r4 = r3.countDownLatch;	 Catch:{ Throwable -> 0x1821 }
        r5 = 1;
        r1.processNewMessages(r2, r5, r5, r4);	 Catch:{ Throwable -> 0x1821 }
        goto L_0x1815;
    L_0x1774:
        r3 = r32;
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x1821 }
        r1.countDown();	 Catch:{ Throwable -> 0x1821 }
        goto L_0x1815;
    L_0x177d:
        r0 = move-exception;
        r3 = r32;
        goto L_0x1822;
    L_0x1782:
        r0 = move-exception;
        r3 = r32;
        goto L_0x178d;
    L_0x1786:
        r0 = move-exception;
        r3 = r1;
        goto L_0x178d;
    L_0x1789:
        r0 = move-exception;
        r3 = r1;
        r28 = r8;
    L_0x178d:
        r29 = r15;
        goto L_0x18d0;
    L_0x1791:
        r3 = r1;
        r28 = r8;
        r29 = r15;
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x1821 }
        r1.countDown();	 Catch:{ Throwable -> 0x1821 }
        goto L_0x1815;
    L_0x179d:
        r8 = r2;
        r5 = r3;
        r28 = r7;
        r29 = r15;
        r3 = r1;
        r1 = "max_id";
        r1 = r11.getInt(r1);	 Catch:{ Throwable -> 0x1821 }
        r2 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x1821 }
        r2.<init>();	 Catch:{ Throwable -> 0x1821 }
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x1821 }
        if (r4 == 0) goto L_0x17cf;
    L_0x17b3:
        r4 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x1821 }
        r4.<init>();	 Catch:{ Throwable -> 0x1821 }
        r7 = "GCM received read notification max_id = ";
        r4.append(r7);	 Catch:{ Throwable -> 0x1821 }
        r4.append(r1);	 Catch:{ Throwable -> 0x1821 }
        r7 = " for dialogId = ";
        r4.append(r7);	 Catch:{ Throwable -> 0x1821 }
        r4.append(r12);	 Catch:{ Throwable -> 0x1821 }
        r4 = r4.toString();	 Catch:{ Throwable -> 0x1821 }
        org.telegram.messenger.FileLog.d(r4);	 Catch:{ Throwable -> 0x1821 }
    L_0x17cf:
        if (r8 == 0) goto L_0x17de;
    L_0x17d1:
        r4 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox;	 Catch:{ Throwable -> 0x1821 }
        r4.<init>();	 Catch:{ Throwable -> 0x1821 }
        r4.channel_id = r8;	 Catch:{ Throwable -> 0x1821 }
        r4.max_id = r1;	 Catch:{ Throwable -> 0x1821 }
        r2.add(r4);	 Catch:{ Throwable -> 0x1821 }
        goto L_0x1801;
    L_0x17de:
        r4 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox;	 Catch:{ Throwable -> 0x1821 }
        r4.<init>();	 Catch:{ Throwable -> 0x1821 }
        if (r5 == 0) goto L_0x17f1;
    L_0x17e5:
        r6 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ Throwable -> 0x1821 }
        r6.<init>();	 Catch:{ Throwable -> 0x1821 }
        r4.peer = r6;	 Catch:{ Throwable -> 0x1821 }
        r6 = r4.peer;	 Catch:{ Throwable -> 0x1821 }
        r6.user_id = r5;	 Catch:{ Throwable -> 0x1821 }
        goto L_0x17fc;
    L_0x17f1:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ Throwable -> 0x1821 }
        r5.<init>();	 Catch:{ Throwable -> 0x1821 }
        r4.peer = r5;	 Catch:{ Throwable -> 0x1821 }
        r5 = r4.peer;	 Catch:{ Throwable -> 0x1821 }
        r5.chat_id = r6;	 Catch:{ Throwable -> 0x1821 }
    L_0x17fc:
        r4.max_id = r1;	 Catch:{ Throwable -> 0x1821 }
        r2.add(r4);	 Catch:{ Throwable -> 0x1821 }
    L_0x1801:
        r1 = org.telegram.messenger.MessagesController.getInstance(r29);	 Catch:{ Throwable -> 0x1821 }
        r4 = 0;
        r8 = 0;
        r1.processUpdateArray(r2, r8, r8, r4);	 Catch:{ Throwable -> 0x1821 }
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x1821 }
        r1.countDown();	 Catch:{ Throwable -> 0x1821 }
        goto L_0x1815;
    L_0x1810:
        r3 = r1;
        r28 = r7;
        r29 = r15;
    L_0x1815:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r29);	 Catch:{ Throwable -> 0x1821 }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r29);	 Catch:{ Throwable -> 0x1821 }
        r1.resumeNetworkMaybe();	 Catch:{ Throwable -> 0x1821 }
        goto L_0x1929;
    L_0x1821:
        r0 = move-exception;
    L_0x1822:
        r2 = r0;
        r7 = r28;
        r15 = r29;
        goto L_0x18d9;
    L_0x1829:
        r0 = move-exception;
        r3 = r1;
        r28 = r7;
        r29 = r15;
        goto L_0x18d8;
    L_0x1831:
        r3 = r1;
        r28 = r7;
        r29 = r15;
        r1 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ;	 Catch:{ Throwable -> 0x1842 }
        r1.<init>(r15);	 Catch:{ Throwable -> 0x183f }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);	 Catch:{ Throwable -> 0x18cf }
        return;
    L_0x183f:
        r0 = move-exception;
        goto L_0x18d0;
    L_0x1842:
        r0 = move-exception;
        r15 = r29;
        goto L_0x18d0;
    L_0x1847:
        r3 = r1;
        r28 = r7;
        r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification;	 Catch:{ Throwable -> 0x18cf }
        r1.<init>();	 Catch:{ Throwable -> 0x18cf }
        r2 = 0;
        r1.popup = r2;	 Catch:{ Throwable -> 0x18cf }
        r2 = 2;
        r1.flags = r2;	 Catch:{ Throwable -> 0x18cf }
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6 = r34 / r6;
        r2 = (int) r6;	 Catch:{ Throwable -> 0x18cf }
        r1.inbox_date = r2;	 Catch:{ Throwable -> 0x18cf }
        r2 = "message";
        r2 = r5.getString(r2);	 Catch:{ Throwable -> 0x18cf }
        r1.message = r2;	 Catch:{ Throwable -> 0x18cf }
        r2 = "announcement";
        r1.type = r2;	 Catch:{ Throwable -> 0x18cf }
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;	 Catch:{ Throwable -> 0x18cf }
        r2.<init>();	 Catch:{ Throwable -> 0x18cf }
        r1.media = r2;	 Catch:{ Throwable -> 0x18cf }
        r2 = new org.telegram.tgnet.TLRPC$TL_updates;	 Catch:{ Throwable -> 0x18cf }
        r2.<init>();	 Catch:{ Throwable -> 0x18cf }
        r4 = r2.updates;	 Catch:{ Throwable -> 0x18cf }
        r4.add(r1);	 Catch:{ Throwable -> 0x18cf }
        r1 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ Throwable -> 0x18cf }
        r4 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo;	 Catch:{ Throwable -> 0x1890 }
        r4.<init>(r15, r2);	 Catch:{ Throwable -> 0x1890 }
        r1.postRunnable(r4);	 Catch:{ Throwable -> 0x18cf }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ Throwable -> 0x18cf }
        r1.resumeNetworkMaybe();	 Catch:{ Throwable -> 0x18cf }
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x18cf }
        r1.countDown();	 Catch:{ Throwable -> 0x18cf }
        return;
    L_0x1890:
        r0 = move-exception;
        goto L_0x18d0;
    L_0x1892:
        r3 = r1;
        r28 = r7;
        r1 = "dc";
        r1 = r11.getInt(r1);	 Catch:{ Throwable -> 0x18cf }
        r2 = "addr";
        r2 = r11.getString(r2);	 Catch:{ Throwable -> 0x18cf }
        r4 = ":";
        r2 = r2.split(r4);	 Catch:{ Throwable -> 0x18cf }
        r4 = r2.length;	 Catch:{ Throwable -> 0x18cf }
        r5 = 2;
        if (r4 == r5) goto L_0x18b1;
    L_0x18ab:
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x18cf }
        r1.countDown();	 Catch:{ Throwable -> 0x18cf }
        return;
    L_0x18b1:
        r4 = 0;
        r4 = r2[r4];	 Catch:{ Throwable -> 0x18cf }
        r5 = 1;
        r2 = r2[r5];	 Catch:{ Throwable -> 0x18cf }
        r2 = java.lang.Integer.parseInt(r2);	 Catch:{ Throwable -> 0x18cf }
        r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ Throwable -> 0x18cf }
        r5.applyDatacenterAddress(r1, r4, r2);	 Catch:{ Throwable -> 0x18cf }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ Throwable -> 0x18cf }
        r1.resumeNetworkMaybe();	 Catch:{ Throwable -> 0x18cf }
        r1 = r3.countDownLatch;	 Catch:{ Throwable -> 0x18cf }
        r1.countDown();	 Catch:{ Throwable -> 0x18cf }
        return;
    L_0x18cf:
        r0 = move-exception;
    L_0x18d0:
        r2 = r0;
    L_0x18d1:
        r7 = r28;
        goto L_0x18d9;
    L_0x18d4:
        r0 = move-exception;
        r3 = r1;
        r28 = r7;
    L_0x18d8:
        r2 = r0;
    L_0x18d9:
        r1 = -1;
        goto L_0x18f1;
    L_0x18db:
        r0 = move-exception;
        r3 = r1;
        r28 = r7;
        r2 = r0;
        goto L_0x18ef;
    L_0x18e1:
        r0 = move-exception;
        r3 = r1;
        r28 = r7;
        r8 = 0;
        r2 = r0;
        r9 = r8;
        goto L_0x18ef;
    L_0x18e9:
        r0 = move-exception;
        r3 = r1;
        r8 = 0;
        r2 = r0;
        r7 = r8;
        r9 = r7;
    L_0x18ef:
        r1 = -1;
    L_0x18f0:
        r15 = -1;
    L_0x18f1:
        if (r15 == r1) goto L_0x1903;
    L_0x18f3:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15);
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);
        r1.resumeNetworkMaybe();
        r1 = r3.countDownLatch;
        r1.countDown();
        goto L_0x1906;
    L_0x1903:
        r32.onDecryptError();
    L_0x1906:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x1926;
    L_0x190a:
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
    L_0x1926:
        org.telegram.messenger.FileLog.e(r2);
    L_0x1929:
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
