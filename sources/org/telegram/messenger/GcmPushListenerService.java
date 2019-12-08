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

    /* JADX WARNING: Removed duplicated region for block: B:297:0x04d1  */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x04aa  */
    /* JADX WARNING: Removed duplicated region for block: B:593:0x095a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:590:0x094f A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0944 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0939 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:581:0x092e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:578:0x0923 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:575:0x0918 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x090d A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x0902 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x08f6 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x08ea A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x08de A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:557:0x08d2 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:554:0x08c7 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:551:0x08bb A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x08b0 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:545:0x08a4 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:542:0x0898 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:539:0x088c A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x0880 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0874 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:530:0x0868 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:527:0x085c A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:524:0x0851 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:521:0x0846 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:518:0x083a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:515:0x082e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:512:0x0822 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:509:0x0816 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x080a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:503:0x07fe A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:500:0x07f2 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x07e6 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:494:0x07da A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x07ce A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:488:0x07c2 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:485:0x07b6 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x07aa A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:479:0x079e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0792 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:473:0x0786 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:470:0x077a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:467:0x076e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0763 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0757 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x074b A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x073f A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:452:0x0733 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:449:0x0727 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x071b A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x070f A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0703 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:437:0x06f7 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x06eb A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x06df A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:428:0x06d3 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x06c8 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x06bc A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x06b0 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:416:0x06a4 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0698 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:410:0x068c A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:407:0x0680 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:404:0x0674 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:401:0x0668 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x065c A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:395:0x0650 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0644 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x0638 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:386:0x062c A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:383:0x0620 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x0615 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:377:0x0609 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x05fd A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x05f1 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x05e5 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x05d9 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:362:0x05cd A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x05c1 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x05b5 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:353:0x05a9 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:350:0x059e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:347:0x0592 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x0586 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x057a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x056e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:335:0x0562 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x0556 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:329:0x054a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x053e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x0532 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x0526 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x051a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x050e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x0502 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x04f6 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x04ea A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x04de A:{SYNTHETIC, Splitter:B:301:0x04de} */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0460  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0436 A:{SYNTHETIC, Splitter:B:259:0x0436} */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x0483 A:{SYNTHETIC, Splitter:B:280:0x0483} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0476  */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x04aa  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x04d1  */
    /* JADX WARNING: Removed duplicated region for block: B:593:0x095a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:590:0x094f A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0944 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0939 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:581:0x092e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:578:0x0923 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:575:0x0918 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x090d A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x0902 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x08f6 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x08ea A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x08de A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:557:0x08d2 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:554:0x08c7 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:551:0x08bb A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x08b0 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:545:0x08a4 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:542:0x0898 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:539:0x088c A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x0880 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0874 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:530:0x0868 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:527:0x085c A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:524:0x0851 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:521:0x0846 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:518:0x083a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:515:0x082e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:512:0x0822 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:509:0x0816 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x080a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:503:0x07fe A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:500:0x07f2 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x07e6 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:494:0x07da A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x07ce A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:488:0x07c2 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:485:0x07b6 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x07aa A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:479:0x079e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0792 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:473:0x0786 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:470:0x077a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:467:0x076e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0763 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0757 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x074b A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x073f A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:452:0x0733 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:449:0x0727 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x071b A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x070f A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0703 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:437:0x06f7 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x06eb A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x06df A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:428:0x06d3 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x06c8 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x06bc A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x06b0 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:416:0x06a4 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0698 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:410:0x068c A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:407:0x0680 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:404:0x0674 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:401:0x0668 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x065c A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:395:0x0650 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0644 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x0638 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:386:0x062c A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:383:0x0620 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x0615 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:377:0x0609 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x05fd A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x05f1 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x05e5 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x05d9 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:362:0x05cd A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x05c1 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x05b5 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:353:0x05a9 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:350:0x059e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:347:0x0592 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x0586 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x057a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x056e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:335:0x0562 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x0556 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:329:0x054a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x053e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x0532 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x0526 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x051a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x050e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x0502 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x04f6 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x04ea A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x04de A:{SYNTHETIC, Splitter:B:301:0x04de} */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x041f A:{SYNTHETIC, Splitter:B:251:0x041f} */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0436 A:{SYNTHETIC, Splitter:B:259:0x0436} */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0460  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0476  */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x0483 A:{SYNTHETIC, Splitter:B:280:0x0483} */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x04d1  */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x04aa  */
    /* JADX WARNING: Removed duplicated region for block: B:593:0x095a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:590:0x094f A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0944 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0939 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:581:0x092e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:578:0x0923 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:575:0x0918 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x090d A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x0902 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x08f6 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x08ea A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x08de A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:557:0x08d2 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:554:0x08c7 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:551:0x08bb A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x08b0 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:545:0x08a4 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:542:0x0898 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:539:0x088c A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x0880 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0874 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:530:0x0868 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:527:0x085c A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:524:0x0851 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:521:0x0846 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:518:0x083a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:515:0x082e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:512:0x0822 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:509:0x0816 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x080a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:503:0x07fe A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:500:0x07f2 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x07e6 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:494:0x07da A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x07ce A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:488:0x07c2 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:485:0x07b6 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x07aa A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:479:0x079e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0792 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:473:0x0786 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:470:0x077a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:467:0x076e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0763 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0757 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x074b A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x073f A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:452:0x0733 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:449:0x0727 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x071b A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x070f A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0703 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:437:0x06f7 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x06eb A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x06df A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:428:0x06d3 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x06c8 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x06bc A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x06b0 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:416:0x06a4 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0698 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:410:0x068c A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:407:0x0680 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:404:0x0674 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:401:0x0668 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x065c A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:395:0x0650 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0644 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x0638 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:386:0x062c A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:383:0x0620 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x0615 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:377:0x0609 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x05fd A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x05f1 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x05e5 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x05d9 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:362:0x05cd A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x05c1 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x05b5 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:353:0x05a9 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:350:0x059e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:347:0x0592 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x0586 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x057a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x056e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:335:0x0562 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x0556 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:329:0x054a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x053e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x0532 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x0526 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x051a A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x050e A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x0502 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x04f6 A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x04ea A:{Catch:{ all -> 0x0455 }} */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x04de A:{SYNTHETIC, Splitter:B:301:0x04de} */
    /* JADX WARNING: Removed duplicated region for block: B:806:0x17a3 A:{Catch:{ all -> 0x179b, all -> 0x17c1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:234:0x03f4 A:{SYNTHETIC, Splitter:B:234:0x03f4} */
    /* JADX WARNING: Removed duplicated region for block: B:234:0x03f4 A:{SYNTHETIC, Splitter:B:234:0x03f4} */
    /* JADX WARNING: Removed duplicated region for block: B:806:0x17a3 A:{Catch:{ all -> 0x179b, all -> 0x17c1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x0266 A:{SYNTHETIC, Splitter:B:168:0x0266} */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x1840 A:{Catch:{ all -> 0x17ed, all -> 0x187d }} */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x01e1  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x01e1  */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x1840 A:{Catch:{ all -> 0x17ed, all -> 0x187d }} */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x1840 A:{Catch:{ all -> 0x17ed, all -> 0x187d }} */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x01e1  */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x18b4  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x18a4  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x18bb  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x18a4  */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x18b4  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x18bb  */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x18b4  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x18a4  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x18bb  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x18a4  */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x18b4  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x18bb  */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x18b4  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x18a4  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x18bb  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x18a4  */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x18b4  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x18bb  */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x18b4  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x18a4  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x18bb  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x18a4  */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x18b4  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x18bb  */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x18b4  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x18a4  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x18bb  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x18a4  */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x18b4  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x18bb  */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x18b4  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x18a4  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x18bb  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x18a4  */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x18b4  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x18bb  */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x18b4  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x18a4  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x18bb  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x18a4  */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x18b4  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x18bb  */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x18b4  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x18a4  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x18bb  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x18a4  */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x18b4  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x18bb  */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x18b4  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x18a4  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x18bb  */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:230:0x03ed, code skipped:
            if (org.telegram.messenger.MessagesStorage.getInstance(r15).checkMessageByRandomId(r3) == false) goto L_0x03ef;
     */
    /* JADX WARNING: Missing block: B:597:0x0966, code skipped:
            r14 = "ChannelMessageFew";
            r11 = " ";
            r32 = r7;
            r7 = "AttachSticker";
     */
    /* JADX WARNING: Missing block: B:598:0x096e, code skipped:
            switch(r10) {
                case 0: goto L_0x16bf;
                case 1: goto L_0x16bf;
                case 2: goto L_0x169c;
                case 3: goto L_0x167d;
                case 4: goto L_0x165e;
                case 5: goto L_0x163f;
                case 6: goto L_0x161f;
                case 7: goto L_0x1605;
                case 8: goto L_0x15e5;
                case 9: goto L_0x15c5;
                case 10: goto L_0x1566;
                case 11: goto L_0x1546;
                case 12: goto L_0x1521;
                case 13: goto L_0x14fc;
                case 14: goto L_0x14dc;
                case 15: goto L_0x14bc;
                case 16: goto L_0x149c;
                case 17: goto L_0x1477;
                case 18: goto L_0x1456;
                case 19: goto L_0x1456;
                case 20: goto L_0x1431;
                case 21: goto L_0x140a;
                case 22: goto L_0x13e1;
                case 23: goto L_0x13b8;
                case 24: goto L_0x13a0;
                case 25: goto L_0x1380;
                case 26: goto L_0x1360;
                case 27: goto L_0x1340;
                case 28: goto L_0x1320;
                case 29: goto L_0x1300;
                case 30: goto L_0x12a1;
                case 31: goto L_0x1281;
                case 32: goto L_0x125c;
                case 33: goto L_0x1237;
                case 34: goto L_0x1217;
                case 35: goto L_0x11f7;
                case 36: goto L_0x11d7;
                case 37: goto L_0x11b7;
                case 38: goto L_0x118b;
                case 39: goto L_0x1163;
                case 40: goto L_0x113b;
                case 41: goto L_0x1124;
                case 42: goto L_0x1101;
                case 43: goto L_0x10dc;
                case 44: goto L_0x10b7;
                case 45: goto L_0x1092;
                case 46: goto L_0x106d;
                case 47: goto L_0x1048;
                case 48: goto L_0x0fc6;
                case 49: goto L_0x0f9f;
                case 50: goto L_0x0var_;
                case 51: goto L_0x0var_;
                case 52: goto L_0x0f2f;
                case 53: goto L_0x0f0c;
                case 54: goto L_0x0ee9;
                case 55: goto L_0x0ec1;
                case 56: goto L_0x0e9a;
                case 57: goto L_0x0e72;
                case 58: goto L_0x0e58;
                case 59: goto L_0x0e58;
                case 60: goto L_0x0e3e;
                case 61: goto L_0x0e24;
                case 62: goto L_0x0e05;
                case 63: goto L_0x0deb;
                case 64: goto L_0x0dd1;
                case 65: goto L_0x0db7;
                case 66: goto L_0x0d9d;
                case 67: goto L_0x0d83;
                case 68: goto L_0x0d55;
                case 69: goto L_0x0d28;
                case 70: goto L_0x0cfb;
                case 71: goto L_0x0cdf;
                case 72: goto L_0x0ca8;
                case 73: goto L_0x0CLASSNAME;
                case 74: goto L_0x0c4b;
                case 75: goto L_0x0c1e;
                case 76: goto L_0x0bef;
                case 77: goto L_0x0bc0;
                case 78: goto L_0x0b44;
                case 79: goto L_0x0b15;
                case 80: goto L_0x0adc;
                case 81: goto L_0x0aa7;
                case 82: goto L_0x0a77;
                case 83: goto L_0x0a4c;
                case 84: goto L_0x0a21;
                case 85: goto L_0x09f4;
                case 86: goto L_0x09c7;
                case 87: goto L_0x099a;
                case 88: goto L_0x097f;
                case 89: goto L_0x0979;
                case 90: goto L_0x0979;
                case 91: goto L_0x0979;
                case 92: goto L_0x0979;
                case 93: goto L_0x0979;
                case 94: goto L_0x0979;
                case 95: goto L_0x0979;
                case 96: goto L_0x0979;
                case 97: goto L_0x0979;
                default: goto L_0x0971;
            };
     */
    /* JADX WARNING: Missing block: B:599:0x0971, code skipped:
            r21 = r1;
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:602:0x0979, code skipped:
            r21 = r1;
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:604:?, code skipped:
            r7 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
            r21 = r1;
            r30 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:605:0x0997, code skipped:
            r1 = true;
     */
    /* JADX WARNING: Missing block: B:606:0x099a, code skipped:
            if (r1 == 0) goto L_0x09b4;
     */
    /* JADX WARNING: Missing block: B:607:0x099c, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:608:0x09b4, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:609:0x09c7, code skipped:
            if (r1 == 0) goto L_0x09e1;
     */
    /* JADX WARNING: Missing block: B:610:0x09c9, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:611:0x09e1, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:612:0x09f4, code skipped:
            if (r1 == 0) goto L_0x0a0e;
     */
    /* JADX WARNING: Missing block: B:613:0x09f6, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:614:0x0a0e, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:615:0x0a21, code skipped:
            if (r1 == 0) goto L_0x0a3a;
     */
    /* JADX WARNING: Missing block: B:616:0x0a23, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:617:0x0a3a, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:618:0x0a4c, code skipped:
            if (r1 == 0) goto L_0x0a65;
     */
    /* JADX WARNING: Missing block: B:619:0x0a4e, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:620:0x0a65, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:621:0x0a77, code skipped:
            if (r1 == 0) goto L_0x0a90;
     */
    /* JADX WARNING: Missing block: B:622:0x0a79, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:623:0x0a90, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:624:0x0aa1, code skipped:
            r21 = r1;
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:625:0x0aa7, code skipped:
            if (r1 == 0) goto L_0x0ac5;
     */
    /* JADX WARNING: Missing block: B:626:0x0aa9, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:627:0x0ac5, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:628:0x0adc, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:629:0x0ade, code skipped:
            if (r1 == 0) goto L_0x0afd;
     */
    /* JADX WARNING: Missing block: B:630:0x0ae0, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:631:0x0afd, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:632:0x0b15, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:633:0x0b17, code skipped:
            if (r1 == 0) goto L_0x0b31;
     */
    /* JADX WARNING: Missing block: B:634:0x0b19, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:635:0x0b31, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:636:0x0b44, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:637:0x0b46, code skipped:
            if (r1 == 0) goto L_0x0b89;
     */
    /* JADX WARNING: Missing block: B:639:0x0b4a, code skipped:
            if (r15.length <= 2) goto L_0x0b71;
     */
    /* JADX WARNING: Missing block: B:641:0x0b52, code skipped:
            if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x0b71;
     */
    /* JADX WARNING: Missing block: B:642:0x0b54, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:643:0x0b71, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:645:0x0b8b, code skipped:
            if (r15.length <= 1) goto L_0x0bad;
     */
    /* JADX WARNING: Missing block: B:647:0x0b93, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x0bad;
     */
    /* JADX WARNING: Missing block: B:648:0x0b95, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:649:0x0bad, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:650:0x0bc0, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:651:0x0bc2, code skipped:
            if (r1 == 0) goto L_0x0bdc;
     */
    /* JADX WARNING: Missing block: B:652:0x0bc4, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:653:0x0bdc, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:654:0x0bef, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:655:0x0bf1, code skipped:
            if (r1 == 0) goto L_0x0c0b;
     */
    /* JADX WARNING: Missing block: B:656:0x0bf3, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:657:0x0c0b, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:658:0x0c1e, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:659:0x0CLASSNAME, code skipped:
            if (r1 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Missing block: B:660:0x0CLASSNAME, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:661:0x0CLASSNAME, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:662:0x0c4b, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:663:0x0c4d, code skipped:
            if (r1 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Missing block: B:664:0x0c4f, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:665:0x0CLASSNAME, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:666:0x0CLASSNAME, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:667:0x0c7a, code skipped:
            if (r1 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Missing block: B:668:0x0c7c, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:669:0x0CLASSNAME, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:670:0x0ca4, code skipped:
            r21 = r1;
     */
    /* JADX WARNING: Missing block: B:671:0x0ca8, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:672:0x0caa, code skipped:
            if (r1 == 0) goto L_0x0cc8;
     */
    /* JADX WARNING: Missing block: B:673:0x0cac, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:674:0x0cc8, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:675:0x0cdf, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:676:0x0cf7, code skipped:
            r21 = r1;
     */
    /* JADX WARNING: Missing block: B:677:0x0cfb, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:678:0x0d28, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:679:0x0d55, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString(r8, org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:680:0x0d83, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:681:0x0d9d, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:682:0x0db7, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:683:0x0dd1, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:684:0x0deb, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:685:0x0e05, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:686:0x0e24, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:687:0x0e3e, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:688:0x0e58, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:689:0x0e72, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r15[0], r15[1], r15[2]);
            r8 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Missing block: B:690:0x0e9a, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r15[0], r15[1], r15[2], r15[3]);
     */
    /* JADX WARNING: Missing block: B:691:0x0ec1, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r15[0], r15[1], r15[2]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:692:0x0ee9, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r15[0], r15[1]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:693:0x0f0c, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r15[0], r15[1]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:694:0x0f2f, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r15[0], r15[1]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:695:0x0var_, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r15[0], r15[1], r15[2]);
            r8 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:696:0x0var_, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r15[0], r15[1], r15[2]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:697:0x0f9f, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r15[0], r15[1]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:698:0x0fc0, code skipped:
            r21 = r1;
            r16 = r8;
     */
    /* JADX WARNING: Missing block: B:699:0x0fc6, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:700:0x0fca, code skipped:
            if (r15.length <= 2) goto L_0x1011;
     */
    /* JADX WARNING: Missing block: B:702:0x0fd2, code skipped:
            if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x1011;
     */
    /* JADX WARNING: Missing block: B:703:0x0fd4, code skipped:
            r21 = r1;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r15[0], r15[1], r15[2]);
            r8 = new java.lang.StringBuilder();
            r8.append(r15[2]);
            r8.append(r11);
            r8.append(org.telegram.messenger.LocaleController.getString(r7, NUM));
            r7 = r8.toString();
     */
    /* JADX WARNING: Missing block: B:704:0x1011, code skipped:
            r21 = r1;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r15[0], r15[1]);
            r8 = new java.lang.StringBuilder();
            r8.append(r15[1]);
            r8.append(r11);
            r8.append(org.telegram.messenger.LocaleController.getString(r7, NUM));
            r7 = r8.toString();
     */
    /* JADX WARNING: Missing block: B:705:0x1048, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:706:0x106d, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:707:0x1092, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:708:0x10b7, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:709:0x10dc, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:710:0x1101, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r15[0], r15[1], r15[2]);
            r7 = r15[2];
     */
    /* JADX WARNING: Missing block: B:711:0x1124, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:712:0x113b, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString(r14, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:713:0x1163, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString(r14, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:714:0x118b, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString(r14, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Missing block: B:715:0x11b7, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:716:0x11d7, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:717:0x11f7, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:718:0x1217, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:719:0x1237, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:720:0x125c, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:721:0x1281, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:722:0x12a1, code skipped:
            r21 = r1;
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:723:0x12a7, code skipped:
            if (r15.length <= 1) goto L_0x12e6;
     */
    /* JADX WARNING: Missing block: B:725:0x12af, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x12e6;
     */
    /* JADX WARNING: Missing block: B:726:0x12b1, code skipped:
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r15[0], r15[1]);
            r8 = new java.lang.StringBuilder();
            r8.append(r15[1]);
            r8.append(r11);
            r8.append(org.telegram.messenger.LocaleController.getString(r7, NUM));
            r7 = r8.toString();
     */
    /* JADX WARNING: Missing block: B:727:0x12e6, code skipped:
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString(r7, NUM);
     */
    /* JADX WARNING: Missing block: B:728:0x1300, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:729:0x1320, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:730:0x1340, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:731:0x1360, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:732:0x1380, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:733:0x13a0, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:734:0x13b5, code skipped:
            r7 = r1;
     */
    /* JADX WARNING: Missing block: B:735:0x13b8, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:736:0x13e1, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:737:0x140a, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString(r8, org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:738:0x1431, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Missing block: B:739:0x1456, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:740:0x1477, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:741:0x149c, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:742:0x14bc, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:743:0x14dc, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:744:0x14fc, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:745:0x1521, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:746:0x1546, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:747:0x1566, code skipped:
            r21 = r1;
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:748:0x156c, code skipped:
            if (r15.length <= 1) goto L_0x15ab;
     */
    /* JADX WARNING: Missing block: B:750:0x1574, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x15ab;
     */
    /* JADX WARNING: Missing block: B:751:0x1576, code skipped:
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r15[0], r15[1]);
            r8 = new java.lang.StringBuilder();
            r8.append(r15[1]);
            r8.append(r11);
            r8.append(org.telegram.messenger.LocaleController.getString(r7, NUM));
            r7 = r8.toString();
     */
    /* JADX WARNING: Missing block: B:752:0x15ab, code skipped:
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString(r7, NUM);
     */
    /* JADX WARNING: Missing block: B:753:0x15c5, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:754:0x15e5, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:755:0x1605, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r15[0]);
     */
    /* JADX WARNING: Missing block: B:756:0x161b, code skipped:
            r7 = r1;
     */
    /* JADX WARNING: Missing block: B:757:0x161c, code skipped:
            r1 = false;
     */
    /* JADX WARNING: Missing block: B:758:0x161f, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:759:0x163f, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:760:0x165e, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:761:0x167d, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:762:0x169c, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:763:0x16ba, code skipped:
            r16 = r7;
            r7 = r1;
     */
    /* JADX WARNING: Missing block: B:764:0x16bd, code skipped:
            r1 = false;
     */
    /* JADX WARNING: Missing block: B:765:0x16bf, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r15[0], r15[1]);
            r7 = r15[1];
     */
    /* JADX WARNING: Missing block: B:766:0x16dc, code skipped:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x16f2;
     */
    /* JADX WARNING: Missing block: B:767:0x16de, code skipped:
            r1 = new java.lang.StringBuilder();
            r1.append("unhandled loc_key = ");
            r1.append(r9);
            org.telegram.messenger.FileLog.w(r1.toString());
     */
    /* JADX WARNING: Missing block: B:768:0x16f2, code skipped:
            r1 = false;
            r7 = null;
     */
    /* JADX WARNING: Missing block: B:769:0x16f4, code skipped:
            r16 = null;
     */
    /* JADX WARNING: Missing block: B:770:0x16f6, code skipped:
            if (r7 == null) goto L_0x1793;
     */
    /* JADX WARNING: Missing block: B:772:?, code skipped:
            r8 = new org.telegram.tgnet.TLRPC.TL_message();
            r8.id = r10;
            r8.random_id = r3;
     */
    /* JADX WARNING: Missing block: B:773:0x1701, code skipped:
            if (r16 == null) goto L_0x1706;
     */
    /* JADX WARNING: Missing block: B:774:0x1703, code skipped:
            r3 = r16;
     */
    /* JADX WARNING: Missing block: B:775:0x1706, code skipped:
            r3 = r7;
     */
    /* JADX WARNING: Missing block: B:776:0x1707, code skipped:
            r8.message = r3;
            r8.date = (int) (r37 / 1000);
     */
    /* JADX WARNING: Missing block: B:777:0x1710, code skipped:
            if (r6 == null) goto L_0x1719;
     */
    /* JADX WARNING: Missing block: B:779:?, code skipped:
            r8.action = new org.telegram.tgnet.TLRPC.TL_messageActionPinMessage();
     */
    /* JADX WARNING: Missing block: B:780:0x1719, code skipped:
            if (r5 == null) goto L_0x1722;
     */
    /* JADX WARNING: Missing block: B:781:0x171b, code skipped:
            r8.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Missing block: B:783:?, code skipped:
            r8.dialog_id = r12;
     */
    /* JADX WARNING: Missing block: B:784:0x1724, code skipped:
            if (r2 == 0) goto L_0x1732;
     */
    /* JADX WARNING: Missing block: B:786:?, code skipped:
            r8.to_id = new org.telegram.tgnet.TLRPC.TL_peerChannel();
            r8.to_id.channel_id = r2;
     */
    /* JADX WARNING: Missing block: B:787:0x1732, code skipped:
            if (r23 == 0) goto L_0x1742;
     */
    /* JADX WARNING: Missing block: B:788:0x1734, code skipped:
            r8.to_id = new org.telegram.tgnet.TLRPC.TL_peerChat();
            r8.to_id.chat_id = r23;
     */
    /* JADX WARNING: Missing block: B:790:?, code skipped:
            r8.to_id = new org.telegram.tgnet.TLRPC.TL_peerUser();
            r8.to_id.user_id = r25;
     */
    /* JADX WARNING: Missing block: B:791:0x174f, code skipped:
            r8.flags |= 256;
            r8.from_id = r21;
     */
    /* JADX WARNING: Missing block: B:792:0x1759, code skipped:
            if (r20 != null) goto L_0x1760;
     */
    /* JADX WARNING: Missing block: B:793:0x175b, code skipped:
            if (r6 == null) goto L_0x175e;
     */
    /* JADX WARNING: Missing block: B:795:0x175e, code skipped:
            r2 = false;
     */
    /* JADX WARNING: Missing block: B:796:0x1760, code skipped:
            r2 = true;
     */
    /* JADX WARNING: Missing block: B:797:0x1761, code skipped:
            r8.mentioned = r2;
            r8.silent = r19;
            r8.from_scheduled = r24;
            r19 = new org.telegram.messenger.MessageObject(r29, r8, r7, r30, r31, r1, r26, r27);
            r1 = new java.util.ArrayList();
            r1.add(r19);
     */
    /* JADX WARNING: Missing block: B:798:0x178a, code skipped:
            r3 = r35;
     */
    /* JADX WARNING: Missing block: B:800:?, code skipped:
            org.telegram.messenger.NotificationsController.getInstance(r29).processNewMessages(r1, true, true, r3.countDownLatch);
     */
    /* JADX WARNING: Missing block: B:801:0x1793, code skipped:
            r35.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:802:0x179b, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:803:0x179c, code skipped:
            r3 = r35;
     */
    /* JADX WARNING: Missing block: B:811:0x17c1, code skipped:
            r0 = th;
     */
    public /* synthetic */ void lambda$null$2$GcmPushListenerService(java.util.Map r36, long r37) {
        /*
        r35 = this;
        r1 = r35;
        r2 = r36;
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x000d;
    L_0x0008:
        r3 = "GCM START PROCESSING";
        org.telegram.messenger.FileLog.d(r3);
    L_0x000d:
        r5 = "p";
        r5 = r2.get(r5);	 Catch:{ all -> 0x189b }
        r6 = r5 instanceof java.lang.String;	 Catch:{ all -> 0x189b }
        if (r6 != 0) goto L_0x002d;
    L_0x0017:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x0024 }
        if (r2 == 0) goto L_0x0020;
    L_0x001b:
        r2 = "GCM DECRYPT ERROR 1";
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ all -> 0x0024 }
    L_0x0020:
        r35.onDecryptError();	 Catch:{ all -> 0x0024 }
        return;
    L_0x0024:
        r0 = move-exception;
        r3 = r1;
        r2 = -1;
        r9 = 0;
        r14 = 0;
    L_0x0029:
        r15 = -1;
    L_0x002a:
        r1 = r0;
        goto L_0x18a2;
    L_0x002d:
        r5 = (java.lang.String) r5;	 Catch:{ all -> 0x189b }
        r6 = 8;
        r5 = android.util.Base64.decode(r5, r6);	 Catch:{ all -> 0x189b }
        r7 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ all -> 0x189b }
        r8 = r5.length;	 Catch:{ all -> 0x189b }
        r7.<init>(r8);	 Catch:{ all -> 0x189b }
        r7.writeBytes(r5);	 Catch:{ all -> 0x189b }
        r8 = 0;
        r7.position(r8);	 Catch:{ all -> 0x189b }
        r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ all -> 0x189b }
        if (r9 != 0) goto L_0x0057;
    L_0x0046:
        r9 = new byte[r6];	 Catch:{ all -> 0x0024 }
        org.telegram.messenger.SharedConfig.pushAuthKeyId = r9;	 Catch:{ all -> 0x0024 }
        r9 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ all -> 0x0024 }
        r9 = org.telegram.messenger.Utilities.computeSHA1(r9);	 Catch:{ all -> 0x0024 }
        r10 = r9.length;	 Catch:{ all -> 0x0024 }
        r10 = r10 - r6;
        r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ all -> 0x0024 }
        java.lang.System.arraycopy(r9, r10, r11, r8, r6);	 Catch:{ all -> 0x0024 }
    L_0x0057:
        r9 = new byte[r6];	 Catch:{ all -> 0x189b }
        r10 = 1;
        r7.readBytes(r9, r10);	 Catch:{ all -> 0x189b }
        r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ all -> 0x189b }
        r11 = java.util.Arrays.equals(r11, r9);	 Catch:{ all -> 0x189b }
        r12 = 3;
        r13 = 2;
        if (r11 != 0) goto L_0x0092;
    L_0x0067:
        r35.onDecryptError();	 Catch:{ all -> 0x0024 }
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x0024 }
        if (r2 == 0) goto L_0x0091;
    L_0x006e:
        r2 = java.util.Locale.US;	 Catch:{ all -> 0x0024 }
        r5 = "GCM DECRYPT ERROR 2 k1=%s k2=%s, key=%s";
        r6 = new java.lang.Object[r12];	 Catch:{ all -> 0x0024 }
        r7 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ all -> 0x0024 }
        r7 = org.telegram.messenger.Utilities.bytesToHex(r7);	 Catch:{ all -> 0x0024 }
        r6[r8] = r7;	 Catch:{ all -> 0x0024 }
        r7 = org.telegram.messenger.Utilities.bytesToHex(r9);	 Catch:{ all -> 0x0024 }
        r6[r10] = r7;	 Catch:{ all -> 0x0024 }
        r7 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ all -> 0x0024 }
        r7 = org.telegram.messenger.Utilities.bytesToHex(r7);	 Catch:{ all -> 0x0024 }
        r6[r13] = r7;	 Catch:{ all -> 0x0024 }
        r2 = java.lang.String.format(r2, r5, r6);	 Catch:{ all -> 0x0024 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ all -> 0x0024 }
    L_0x0091:
        return;
    L_0x0092:
        r9 = 16;
        r9 = new byte[r9];	 Catch:{ all -> 0x189b }
        r7.readBytes(r9, r10);	 Catch:{ all -> 0x189b }
        r11 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ all -> 0x189b }
        r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13);	 Catch:{ all -> 0x189b }
        r14 = r7.buffer;	 Catch:{ all -> 0x189b }
        r15 = r11.aesKey;	 Catch:{ all -> 0x189b }
        r11 = r11.aesIv;	 Catch:{ all -> 0x189b }
        r17 = 0;
        r18 = 0;
        r19 = 24;
        r5 = r5.length;	 Catch:{ all -> 0x189b }
        r20 = r5 + -24;
        r16 = r11;
        org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20);	 Catch:{ all -> 0x189b }
        r21 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ all -> 0x189b }
        r22 = 96;
        r23 = 32;
        r5 = r7.buffer;	 Catch:{ all -> 0x189b }
        r25 = 24;
        r11 = r7.buffer;	 Catch:{ all -> 0x189b }
        r26 = r11.limit();	 Catch:{ all -> 0x189b }
        r24 = r5;
        r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26);	 Catch:{ all -> 0x189b }
        r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6);	 Catch:{ all -> 0x189b }
        if (r5 != 0) goto L_0x00ea;
    L_0x00cf:
        r35.onDecryptError();	 Catch:{ all -> 0x0024 }
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x0024 }
        if (r2 == 0) goto L_0x00e9;
    L_0x00d6:
        r2 = "GCM DECRYPT ERROR 3, key = %s";
        r5 = new java.lang.Object[r10];	 Catch:{ all -> 0x0024 }
        r6 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ all -> 0x0024 }
        r6 = org.telegram.messenger.Utilities.bytesToHex(r6);	 Catch:{ all -> 0x0024 }
        r5[r8] = r6;	 Catch:{ all -> 0x0024 }
        r2 = java.lang.String.format(r2, r5);	 Catch:{ all -> 0x0024 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ all -> 0x0024 }
    L_0x00e9:
        return;
    L_0x00ea:
        r5 = r7.readInt32(r10);	 Catch:{ all -> 0x189b }
        r5 = new byte[r5];	 Catch:{ all -> 0x189b }
        r7.readBytes(r5, r10);	 Catch:{ all -> 0x189b }
        r7 = new java.lang.String;	 Catch:{ all -> 0x189b }
        r7.<init>(r5);	 Catch:{ all -> 0x189b }
        r5 = new org.json.JSONObject;	 Catch:{ all -> 0x1891 }
        r5.<init>(r7);	 Catch:{ all -> 0x1891 }
        r9 = "loc_key";
        r9 = r5.has(r9);	 Catch:{ all -> 0x1891 }
        if (r9 == 0) goto L_0x0113;
    L_0x0105:
        r9 = "loc_key";
        r9 = r5.getString(r9);	 Catch:{ all -> 0x010c }
        goto L_0x0115;
    L_0x010c:
        r0 = move-exception;
        r3 = r1;
        r14 = r7;
        r2 = -1;
        r9 = 0;
        goto L_0x0029;
    L_0x0113:
        r9 = "";
    L_0x0115:
        r11 = "custom";
        r11 = r5.get(r11);	 Catch:{ all -> 0x1888 }
        r11 = r11 instanceof org.json.JSONObject;	 Catch:{ all -> 0x1888 }
        if (r11 == 0) goto L_0x012c;
    L_0x011f:
        r11 = "custom";
        r11 = r5.getJSONObject(r11);	 Catch:{ all -> 0x0126 }
        goto L_0x0131;
    L_0x0126:
        r0 = move-exception;
        r3 = r1;
        r14 = r7;
        r2 = -1;
        goto L_0x0029;
    L_0x012c:
        r11 = new org.json.JSONObject;	 Catch:{ all -> 0x1888 }
        r11.<init>();	 Catch:{ all -> 0x1888 }
    L_0x0131:
        r14 = "user_id";
        r14 = r5.has(r14);	 Catch:{ all -> 0x1888 }
        if (r14 == 0) goto L_0x0140;
    L_0x0139:
        r14 = "user_id";
        r14 = r5.get(r14);	 Catch:{ all -> 0x0126 }
        goto L_0x0141;
    L_0x0140:
        r14 = 0;
    L_0x0141:
        if (r14 != 0) goto L_0x014e;
    L_0x0143:
        r14 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ all -> 0x0126 }
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);	 Catch:{ all -> 0x0126 }
        r14 = r14.getClientUserId();	 Catch:{ all -> 0x0126 }
        goto L_0x0172;
    L_0x014e:
        r15 = r14 instanceof java.lang.Integer;	 Catch:{ all -> 0x1888 }
        if (r15 == 0) goto L_0x0159;
    L_0x0152:
        r14 = (java.lang.Integer) r14;	 Catch:{ all -> 0x0126 }
        r14 = r14.intValue();	 Catch:{ all -> 0x0126 }
        goto L_0x0172;
    L_0x0159:
        r15 = r14 instanceof java.lang.String;	 Catch:{ all -> 0x1888 }
        if (r15 == 0) goto L_0x0168;
    L_0x015d:
        r14 = (java.lang.String) r14;	 Catch:{ all -> 0x0126 }
        r14 = org.telegram.messenger.Utilities.parseInt(r14);	 Catch:{ all -> 0x0126 }
        r14 = r14.intValue();	 Catch:{ all -> 0x0126 }
        goto L_0x0172;
    L_0x0168:
        r14 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ all -> 0x1888 }
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);	 Catch:{ all -> 0x1888 }
        r14 = r14.getClientUserId();	 Catch:{ all -> 0x1888 }
    L_0x0172:
        r15 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ all -> 0x1888 }
        r4 = 0;
    L_0x0175:
        if (r4 >= r12) goto L_0x0188;
    L_0x0177:
        r17 = org.telegram.messenger.UserConfig.getInstance(r4);	 Catch:{ all -> 0x0126 }
        r6 = r17.getClientUserId();	 Catch:{ all -> 0x0126 }
        if (r6 != r14) goto L_0x0183;
    L_0x0181:
        r15 = r4;
        goto L_0x0188;
    L_0x0183:
        r4 = r4 + 1;
        r6 = 8;
        goto L_0x0175;
    L_0x0188:
        r4 = org.telegram.messenger.UserConfig.getInstance(r15);	 Catch:{ all -> 0x187f }
        r4 = r4.isClientActivated();	 Catch:{ all -> 0x187f }
        if (r4 != 0) goto L_0x01a7;
    L_0x0192:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x01a1 }
        if (r2 == 0) goto L_0x019b;
    L_0x0196:
        r2 = "GCM ACCOUNT NOT ACTIVATED";
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ all -> 0x01a1 }
    L_0x019b:
        r2 = r1.countDownLatch;	 Catch:{ all -> 0x01a1 }
        r2.countDown();	 Catch:{ all -> 0x01a1 }
        return;
    L_0x01a1:
        r0 = move-exception;
        r3 = r1;
        r14 = r7;
    L_0x01a4:
        r2 = -1;
        goto L_0x002a;
    L_0x01a7:
        r4 = "google.sent_time";
        r2.get(r4);	 Catch:{ all -> 0x187f }
        r2 = r9.hashCode();	 Catch:{ all -> 0x187f }
        r4 = -NUM; // 0xffffffff8af4e06f float:-2.3580768E-32 double:NaN;
        if (r2 == r4) goto L_0x01d4;
    L_0x01b5:
        r4 = -NUM; // 0xffffffffCLASSNAMEvar_ float:-652872.56 double:NaN;
        if (r2 == r4) goto L_0x01ca;
    L_0x01ba:
        r4 = NUM; // 0x25bae29f float:3.241942E-16 double:3.127458774E-315;
        if (r2 == r4) goto L_0x01c0;
    L_0x01bf:
        goto L_0x01de;
    L_0x01c0:
        r2 = "MESSAGE_ANNOUNCEMENT";
        r2 = r9.equals(r2);	 Catch:{ all -> 0x01a1 }
        if (r2 == 0) goto L_0x01de;
    L_0x01c8:
        r2 = 1;
        goto L_0x01df;
    L_0x01ca:
        r2 = "DC_UPDATE";
        r2 = r9.equals(r2);	 Catch:{ all -> 0x01a1 }
        if (r2 == 0) goto L_0x01de;
    L_0x01d2:
        r2 = 0;
        goto L_0x01df;
    L_0x01d4:
        r2 = "SESSION_REVOKE";
        r2 = r9.equals(r2);	 Catch:{ all -> 0x187f }
        if (r2 == 0) goto L_0x01de;
    L_0x01dc:
        r2 = 2;
        goto L_0x01df;
    L_0x01de:
        r2 = -1;
    L_0x01df:
        if (r2 == 0) goto L_0x1840;
    L_0x01e1:
        if (r2 == r10) goto L_0x17f5;
    L_0x01e3:
        if (r2 == r13) goto L_0x17da;
    L_0x01e5:
        r2 = "channel_id";
        r2 = r11.has(r2);	 Catch:{ all -> 0x17d2 }
        r19 = 0;
        if (r2 == 0) goto L_0x01f8;
    L_0x01ef:
        r2 = "channel_id";
        r2 = r11.getInt(r2);	 Catch:{ all -> 0x01a1 }
        r4 = -r2;
        r3 = (long) r4;
        goto L_0x01fb;
    L_0x01f8:
        r3 = r19;
        r2 = 0;
    L_0x01fb:
        r14 = "from_id";
        r14 = r11.has(r14);	 Catch:{ all -> 0x17d2 }
        if (r14 == 0) goto L_0x0215;
    L_0x0203:
        r3 = "from_id";
        r3 = r11.getInt(r3);	 Catch:{ all -> 0x0211 }
        r14 = r7;
        r6 = (long) r3;
        r33 = r6;
        r6 = r3;
        r3 = r33;
        goto L_0x0217;
    L_0x0211:
        r0 = move-exception;
        r14 = r7;
    L_0x0213:
        r3 = r1;
        goto L_0x01a4;
    L_0x0215:
        r14 = r7;
        r6 = 0;
    L_0x0217:
        r7 = "chat_id";
        r7 = r11.has(r7);	 Catch:{ all -> 0x17c9 }
        if (r7 == 0) goto L_0x022a;
    L_0x021f:
        r3 = "chat_id";
        r3 = r11.getInt(r3);	 Catch:{ all -> 0x0228 }
        r4 = -r3;
        r12 = (long) r4;
        goto L_0x022c;
    L_0x0228:
        r0 = move-exception;
        goto L_0x0213;
    L_0x022a:
        r12 = r3;
        r3 = 0;
    L_0x022c:
        r4 = "encryption_id";
        r4 = r11.has(r4);	 Catch:{ all -> 0x17c9 }
        if (r4 == 0) goto L_0x023e;
    L_0x0234:
        r4 = "encryption_id";
        r4 = r11.getInt(r4);	 Catch:{ all -> 0x0228 }
        r12 = (long) r4;
        r4 = 32;
        r12 = r12 << r4;
    L_0x023e:
        r4 = "schedule";
        r4 = r11.has(r4);	 Catch:{ all -> 0x17c9 }
        if (r4 == 0) goto L_0x0250;
    L_0x0246:
        r4 = "schedule";
        r4 = r11.getInt(r4);	 Catch:{ all -> 0x0228 }
        if (r4 != r10) goto L_0x0250;
    L_0x024e:
        r4 = 1;
        goto L_0x0251;
    L_0x0250:
        r4 = 0;
    L_0x0251:
        r21 = (r12 > r19 ? 1 : (r12 == r19 ? 0 : -1));
        if (r21 != 0) goto L_0x0262;
    L_0x0255:
        r7 = "ENCRYPTED_MESSAGE";
        r7 = r7.equals(r9);	 Catch:{ all -> 0x0228 }
        if (r7 == 0) goto L_0x0262;
    L_0x025d:
        r12 = -NUM; // 0xfffffffvar_ float:0.0 double:NaN;
    L_0x0262:
        r7 = (r12 > r19 ? 1 : (r12 == r19 ? 0 : -1));
        if (r7 == 0) goto L_0x17b0;
    L_0x0266:
        r7 = "READ_HISTORY";
        r7 = r7.equals(r9);	 Catch:{ all -> 0x17c9 }
        r10 = " for dialogId = ";
        if (r7 == 0) goto L_0x02e3;
    L_0x0270:
        r4 = "max_id";
        r4 = r11.getInt(r4);	 Catch:{ all -> 0x0228 }
        r5 = new java.util.ArrayList;	 Catch:{ all -> 0x0228 }
        r5.<init>();	 Catch:{ all -> 0x0228 }
        r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x0228 }
        if (r7 == 0) goto L_0x0299;
    L_0x027f:
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0228 }
        r7.<init>();	 Catch:{ all -> 0x0228 }
        r8 = "GCM received read notification max_id = ";
        r7.append(r8);	 Catch:{ all -> 0x0228 }
        r7.append(r4);	 Catch:{ all -> 0x0228 }
        r7.append(r10);	 Catch:{ all -> 0x0228 }
        r7.append(r12);	 Catch:{ all -> 0x0228 }
        r7 = r7.toString();	 Catch:{ all -> 0x0228 }
        org.telegram.messenger.FileLog.d(r7);	 Catch:{ all -> 0x0228 }
    L_0x0299:
        if (r2 == 0) goto L_0x02a8;
    L_0x029b:
        r3 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox;	 Catch:{ all -> 0x0228 }
        r3.<init>();	 Catch:{ all -> 0x0228 }
        r3.channel_id = r2;	 Catch:{ all -> 0x0228 }
        r3.max_id = r4;	 Catch:{ all -> 0x0228 }
        r5.add(r3);	 Catch:{ all -> 0x0228 }
        goto L_0x02cb;
    L_0x02a8:
        r2 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox;	 Catch:{ all -> 0x0228 }
        r2.<init>();	 Catch:{ all -> 0x0228 }
        if (r6 == 0) goto L_0x02bb;
    L_0x02af:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ all -> 0x0228 }
        r3.<init>();	 Catch:{ all -> 0x0228 }
        r2.peer = r3;	 Catch:{ all -> 0x0228 }
        r3 = r2.peer;	 Catch:{ all -> 0x0228 }
        r3.user_id = r6;	 Catch:{ all -> 0x0228 }
        goto L_0x02c6;
    L_0x02bb:
        r6 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ all -> 0x0228 }
        r6.<init>();	 Catch:{ all -> 0x0228 }
        r2.peer = r6;	 Catch:{ all -> 0x0228 }
        r6 = r2.peer;	 Catch:{ all -> 0x0228 }
        r6.chat_id = r3;	 Catch:{ all -> 0x0228 }
    L_0x02c6:
        r2.max_id = r4;	 Catch:{ all -> 0x0228 }
        r5.add(r2);	 Catch:{ all -> 0x0228 }
    L_0x02cb:
        r16 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ all -> 0x0228 }
        r18 = 0;
        r19 = 0;
        r20 = 0;
        r21 = 0;
        r17 = r5;
        r16.processUpdateArray(r17, r18, r19, r20, r21);	 Catch:{ all -> 0x0228 }
        r2 = r1.countDownLatch;	 Catch:{ all -> 0x0228 }
        r2.countDown();	 Catch:{ all -> 0x0228 }
        goto L_0x17b0;
    L_0x02e3:
        r7 = "MESSAGE_DELETED";
        r7 = r7.equals(r9);	 Catch:{ all -> 0x17c9 }
        r8 = "messages";
        if (r7 == 0) goto L_0x0355;
    L_0x02ed:
        r3 = r11.getString(r8);	 Catch:{ all -> 0x0228 }
        r4 = ",";
        r3 = r3.split(r4);	 Catch:{ all -> 0x0228 }
        r4 = new android.util.SparseArray;	 Catch:{ all -> 0x0228 }
        r4.<init>();	 Catch:{ all -> 0x0228 }
        r5 = new java.util.ArrayList;	 Catch:{ all -> 0x0228 }
        r5.<init>();	 Catch:{ all -> 0x0228 }
        r6 = 0;
    L_0x0302:
        r7 = r3.length;	 Catch:{ all -> 0x0228 }
        if (r6 >= r7) goto L_0x0311;
    L_0x0305:
        r7 = r3[r6];	 Catch:{ all -> 0x0228 }
        r7 = org.telegram.messenger.Utilities.parseInt(r7);	 Catch:{ all -> 0x0228 }
        r5.add(r7);	 Catch:{ all -> 0x0228 }
        r6 = r6 + 1;
        goto L_0x0302;
    L_0x0311:
        r4.put(r2, r5);	 Catch:{ all -> 0x0228 }
        r3 = org.telegram.messenger.NotificationsController.getInstance(r15);	 Catch:{ all -> 0x0228 }
        r3.removeDeletedMessagesFromNotifications(r4);	 Catch:{ all -> 0x0228 }
        r3 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ all -> 0x0228 }
        r3.deleteMessagesByPush(r12, r5, r2);	 Catch:{ all -> 0x0228 }
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x0228 }
        if (r2 == 0) goto L_0x034e;
    L_0x0326:
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0228 }
        r2.<init>();	 Catch:{ all -> 0x0228 }
        r3 = "GCM received ";
        r2.append(r3);	 Catch:{ all -> 0x0228 }
        r2.append(r9);	 Catch:{ all -> 0x0228 }
        r2.append(r10);	 Catch:{ all -> 0x0228 }
        r2.append(r12);	 Catch:{ all -> 0x0228 }
        r3 = " mids = ";
        r2.append(r3);	 Catch:{ all -> 0x0228 }
        r3 = ",";
        r3 = android.text.TextUtils.join(r3, r5);	 Catch:{ all -> 0x0228 }
        r2.append(r3);	 Catch:{ all -> 0x0228 }
        r2 = r2.toString();	 Catch:{ all -> 0x0228 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ all -> 0x0228 }
    L_0x034e:
        r2 = r1.countDownLatch;	 Catch:{ all -> 0x0228 }
        r2.countDown();	 Catch:{ all -> 0x0228 }
        goto L_0x17b0;
    L_0x0355:
        r7 = android.text.TextUtils.isEmpty(r9);	 Catch:{ all -> 0x17c9 }
        if (r7 != 0) goto L_0x17b0;
    L_0x035b:
        r7 = "msg_id";
        r7 = r11.has(r7);	 Catch:{ all -> 0x17c9 }
        if (r7 == 0) goto L_0x036c;
    L_0x0363:
        r7 = "msg_id";
        r7 = r11.getInt(r7);	 Catch:{ all -> 0x0228 }
        r28 = r14;
        goto L_0x036f;
    L_0x036c:
        r28 = r14;
        r7 = 0;
    L_0x036f:
        r14 = "random_id";
        r14 = r11.has(r14);	 Catch:{ all -> 0x17ad }
        if (r14 == 0) goto L_0x0393;
    L_0x0377:
        r14 = "random_id";
        r14 = r11.getString(r14);	 Catch:{ all -> 0x038d }
        r14 = org.telegram.messenger.Utilities.parseLong(r14);	 Catch:{ all -> 0x038d }
        r23 = r14.longValue();	 Catch:{ all -> 0x038d }
        r14 = r4;
        r33 = r23;
        r23 = r3;
        r3 = r33;
        goto L_0x0398;
    L_0x038d:
        r0 = move-exception;
        r3 = r1;
        r14 = r28;
        goto L_0x01a4;
    L_0x0393:
        r23 = r3;
        r14 = r4;
        r3 = r19;
    L_0x0398:
        if (r7 == 0) goto L_0x03dd;
    L_0x039a:
        r24 = r14;
        r14 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ all -> 0x03d4 }
        r14 = r14.dialogs_read_inbox_max;	 Catch:{ all -> 0x03d4 }
        r1 = java.lang.Long.valueOf(r12);	 Catch:{ all -> 0x03d4 }
        r1 = r14.get(r1);	 Catch:{ all -> 0x03d4 }
        r1 = (java.lang.Integer) r1;	 Catch:{ all -> 0x03d4 }
        if (r1 != 0) goto L_0x03cb;
    L_0x03ae:
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r15);	 Catch:{ all -> 0x03d4 }
        r14 = 0;
        r1 = r1.getDialogReadMax(r14, r12);	 Catch:{ all -> 0x03d4 }
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ all -> 0x03d4 }
        r14 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ all -> 0x03d4 }
        r14 = r14.dialogs_read_inbox_max;	 Catch:{ all -> 0x03d4 }
        r25 = r6;
        r6 = java.lang.Long.valueOf(r12);	 Catch:{ all -> 0x03d4 }
        r14.put(r6, r1);	 Catch:{ all -> 0x03d4 }
        goto L_0x03cd;
    L_0x03cb:
        r25 = r6;
    L_0x03cd:
        r1 = r1.intValue();	 Catch:{ all -> 0x03d4 }
        if (r7 <= r1) goto L_0x03f1;
    L_0x03d3:
        goto L_0x03ef;
    L_0x03d4:
        r0 = move-exception;
        r2 = -1;
        r3 = r35;
        r1 = r0;
        r14 = r28;
        goto L_0x18a2;
    L_0x03dd:
        r25 = r6;
        r24 = r14;
        r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1));
        if (r1 == 0) goto L_0x03f1;
    L_0x03e5:
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r15);	 Catch:{ all -> 0x03d4 }
        r1 = r1.checkMessageByRandomId(r3);	 Catch:{ all -> 0x03d4 }
        if (r1 != 0) goto L_0x03f1;
    L_0x03ef:
        r1 = 1;
        goto L_0x03f2;
    L_0x03f1:
        r1 = 0;
    L_0x03f2:
        if (r1 == 0) goto L_0x17a3;
    L_0x03f4:
        r1 = "chat_from_id";
        r1 = r11.has(r1);	 Catch:{ all -> 0x179f }
        if (r1 == 0) goto L_0x0403;
    L_0x03fc:
        r1 = "chat_from_id";
        r1 = r11.getInt(r1);	 Catch:{ all -> 0x03d4 }
        goto L_0x0404;
    L_0x0403:
        r1 = 0;
    L_0x0404:
        r6 = "mention";
        r6 = r11.has(r6);	 Catch:{ all -> 0x179f }
        if (r6 == 0) goto L_0x0416;
    L_0x040c:
        r6 = "mention";
        r6 = r11.getInt(r6);	 Catch:{ all -> 0x03d4 }
        if (r6 == 0) goto L_0x0416;
    L_0x0414:
        r6 = 1;
        goto L_0x0417;
    L_0x0416:
        r6 = 0;
    L_0x0417:
        r14 = "silent";
        r14 = r11.has(r14);	 Catch:{ all -> 0x179f }
        if (r14 == 0) goto L_0x042b;
    L_0x041f:
        r14 = "silent";
        r14 = r11.getInt(r14);	 Catch:{ all -> 0x03d4 }
        if (r14 == 0) goto L_0x042b;
    L_0x0427:
        r29 = r15;
        r14 = 1;
        goto L_0x042e;
    L_0x042b:
        r29 = r15;
        r14 = 0;
    L_0x042e:
        r15 = "loc_args";
        r15 = r5.has(r15);	 Catch:{ all -> 0x179b }
        if (r15 == 0) goto L_0x0460;
    L_0x0436:
        r15 = "loc_args";
        r5 = r5.getJSONArray(r15);	 Catch:{ all -> 0x0455 }
        r15 = r5.length();	 Catch:{ all -> 0x0455 }
        r15 = new java.lang.String[r15];	 Catch:{ all -> 0x0455 }
        r20 = r6;
        r19 = r14;
        r14 = 0;
    L_0x0447:
        r6 = r15.length;	 Catch:{ all -> 0x0455 }
        if (r14 >= r6) goto L_0x0453;
    L_0x044a:
        r6 = r5.getString(r14);	 Catch:{ all -> 0x0455 }
        r15[r14] = r6;	 Catch:{ all -> 0x0455 }
        r14 = r14 + 1;
        goto L_0x0447;
    L_0x0453:
        r5 = 0;
        goto L_0x0466;
    L_0x0455:
        r0 = move-exception;
        r2 = -1;
        r3 = r35;
        r1 = r0;
        r14 = r28;
        r15 = r29;
        goto L_0x18a2;
    L_0x0460:
        r20 = r6;
        r19 = r14;
        r5 = 0;
        r15 = 0;
    L_0x0466:
        r6 = r15[r5];	 Catch:{ all -> 0x179b }
        r5 = "edit_date";
        r27 = r11.has(r5);	 Catch:{ all -> 0x179b }
        r5 = "CHAT_";
        r5 = r9.startsWith(r5);	 Catch:{ all -> 0x179b }
        if (r5 == 0) goto L_0x0483;
    L_0x0476:
        if (r2 == 0) goto L_0x047a;
    L_0x0478:
        r5 = 1;
        goto L_0x047b;
    L_0x047a:
        r5 = 0;
    L_0x047b:
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r11 = r6;
        r6 = 0;
    L_0x0480:
        r26 = 0;
        goto L_0x04a6;
    L_0x0483:
        r5 = "PINNED_";
        r5 = r9.startsWith(r5);	 Catch:{ all -> 0x179b }
        if (r5 == 0) goto L_0x0493;
    L_0x048b:
        if (r1 == 0) goto L_0x048f;
    L_0x048d:
        r5 = 1;
        goto L_0x0490;
    L_0x048f:
        r5 = 0;
    L_0x0490:
        r14 = r6;
        r6 = 1;
        goto L_0x04a4;
    L_0x0493:
        r5 = "CHANNEL_";
        r5 = r9.startsWith(r5);	 Catch:{ all -> 0x179b }
        r14 = r6;
        if (r5 == 0) goto L_0x04a2;
    L_0x049c:
        r5 = 0;
        r6 = 0;
        r11 = 0;
        r26 = 1;
        goto L_0x04a6;
    L_0x04a2:
        r5 = 0;
        r6 = 0;
    L_0x04a4:
        r11 = 0;
        goto L_0x0480;
    L_0x04a6:
        r30 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x179b }
        if (r30 == 0) goto L_0x04d1;
    L_0x04aa:
        r30 = r14;
        r14 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0455 }
        r14.<init>();	 Catch:{ all -> 0x0455 }
        r31 = r11;
        r11 = "GCM received message notification ";
        r14.append(r11);	 Catch:{ all -> 0x0455 }
        r14.append(r9);	 Catch:{ all -> 0x0455 }
        r14.append(r10);	 Catch:{ all -> 0x0455 }
        r14.append(r12);	 Catch:{ all -> 0x0455 }
        r10 = " mid = ";
        r14.append(r10);	 Catch:{ all -> 0x0455 }
        r14.append(r7);	 Catch:{ all -> 0x0455 }
        r10 = r14.toString();	 Catch:{ all -> 0x0455 }
        org.telegram.messenger.FileLog.d(r10);	 Catch:{ all -> 0x0455 }
        goto L_0x04d5;
    L_0x04d1:
        r31 = r11;
        r30 = r14;
    L_0x04d5:
        r10 = r9.hashCode();	 Catch:{ all -> 0x179b }
        switch(r10) {
            case -2100047043: goto L_0x095a;
            case -2091498420: goto L_0x094f;
            case -2053872415: goto L_0x0944;
            case -2039746363: goto L_0x0939;
            case -2023218804: goto L_0x092e;
            case -1979538588: goto L_0x0923;
            case -1979536003: goto L_0x0918;
            case -1979535888: goto L_0x090d;
            case -1969004705: goto L_0x0902;
            case -1946699248: goto L_0x08f6;
            case -1528047021: goto L_0x08ea;
            case -1493579426: goto L_0x08de;
            case -1482481933: goto L_0x08d2;
            case -1480102982: goto L_0x08c7;
            case -1478041834: goto L_0x08bb;
            case -1474543101: goto L_0x08b0;
            case -1465695932: goto L_0x08a4;
            case -1374906292: goto L_0x0898;
            case -1372940586: goto L_0x088c;
            case -1264245338: goto L_0x0880;
            case -1236086700: goto L_0x0874;
            case -1236077786: goto L_0x0868;
            case -1235796237: goto L_0x085c;
            case -1235686303: goto L_0x0851;
            case -1198046100: goto L_0x0846;
            case -1124254527: goto L_0x083a;
            case -1085137927: goto L_0x082e;
            case -1084856378: goto L_0x0822;
            case -1084746444: goto L_0x0816;
            case -819729482: goto L_0x080a;
            case -772141857: goto L_0x07fe;
            case -638310039: goto L_0x07f2;
            case -590403924: goto L_0x07e6;
            case -589196239: goto L_0x07da;
            case -589193654: goto L_0x07ce;
            case -589193539: goto L_0x07c2;
            case -440169325: goto L_0x07b6;
            case -412748110: goto L_0x07aa;
            case -228518075: goto L_0x079e;
            case -213586509: goto L_0x0792;
            case -115582002: goto L_0x0786;
            case -112621464: goto L_0x077a;
            case -108522133: goto L_0x076e;
            case -107572034: goto L_0x0763;
            case -40534265: goto L_0x0757;
            case 65254746: goto L_0x074b;
            case 141040782: goto L_0x073f;
            case 309993049: goto L_0x0733;
            case 309995634: goto L_0x0727;
            case 309995749: goto L_0x071b;
            case 320532812: goto L_0x070f;
            case 328933854: goto L_0x0703;
            case 331340546: goto L_0x06f7;
            case 344816990: goto L_0x06eb;
            case 346878138: goto L_0x06df;
            case 350376871: goto L_0x06d3;
            case 615714517: goto L_0x06c8;
            case 715508879: goto L_0x06bc;
            case 728985323: goto L_0x06b0;
            case 731046471: goto L_0x06a4;
            case 734545204: goto L_0x0698;
            case 802032552: goto L_0x068c;
            case 991498806: goto L_0x0680;
            case 1007364121: goto L_0x0674;
            case 1019917311: goto L_0x0668;
            case 1019926225: goto L_0x065c;
            case 1020207774: goto L_0x0650;
            case 1020317708: goto L_0x0644;
            case 1060349560: goto L_0x0638;
            case 1060358474: goto L_0x062c;
            case 1060640023: goto L_0x0620;
            case 1060749957: goto L_0x0615;
            case 1073049781: goto L_0x0609;
            case 1078101399: goto L_0x05fd;
            case 1110103437: goto L_0x05f1;
            case 1160762272: goto L_0x05e5;
            case 1172918249: goto L_0x05d9;
            case 1234591620: goto L_0x05cd;
            case 1281128640: goto L_0x05c1;
            case 1281131225: goto L_0x05b5;
            case 1281131340: goto L_0x05a9;
            case 1310789062: goto L_0x059e;
            case 1333118583: goto L_0x0592;
            case 1361447897: goto L_0x0586;
            case 1498266155: goto L_0x057a;
            case 1533804208: goto L_0x056e;
            case 1547988151: goto L_0x0562;
            case 1561464595: goto L_0x0556;
            case 1563525743: goto L_0x054a;
            case 1567024476: goto L_0x053e;
            case 1810705077: goto L_0x0532;
            case 1815177512: goto L_0x0526;
            case 1963241394: goto L_0x051a;
            case 2014789757: goto L_0x050e;
            case 2022049433: goto L_0x0502;
            case 2048733346: goto L_0x04f6;
            case 2099392181: goto L_0x04ea;
            case 2140162142: goto L_0x04de;
            default: goto L_0x04dc;
        };
    L_0x04dc:
        goto L_0x0965;
    L_0x04de:
        r10 = "CHAT_MESSAGE_GEOLIVE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x04e6:
        r10 = 53;
        goto L_0x0966;
    L_0x04ea:
        r10 = "CHANNEL_MESSAGE_PHOTOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x04f2:
        r10 = 39;
        goto L_0x0966;
    L_0x04f6:
        r10 = "CHANNEL_MESSAGE_NOTEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x04fe:
        r10 = 25;
        goto L_0x0966;
    L_0x0502:
        r10 = "PINNED_CONTACT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x050a:
        r10 = 80;
        goto L_0x0966;
    L_0x050e:
        r10 = "CHAT_PHOTO_EDITED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0516:
        r10 = 61;
        goto L_0x0966;
    L_0x051a:
        r10 = "LOCKED_MESSAGE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0522:
        r10 = 92;
        goto L_0x0966;
    L_0x0526:
        r10 = "CHANNEL_MESSAGES";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x052e:
        r10 = 41;
        goto L_0x0966;
    L_0x0532:
        r10 = "MESSAGE_INVOICE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x053a:
        r10 = 20;
        goto L_0x0966;
    L_0x053e:
        r10 = "CHAT_MESSAGE_VIDEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0546:
        r10 = 45;
        goto L_0x0966;
    L_0x054a:
        r10 = "CHAT_MESSAGE_ROUND";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0552:
        r10 = 46;
        goto L_0x0966;
    L_0x0556:
        r10 = "CHAT_MESSAGE_PHOTO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x055e:
        r10 = 44;
        goto L_0x0966;
    L_0x0562:
        r10 = "CHAT_MESSAGE_AUDIO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x056a:
        r10 = 49;
        goto L_0x0966;
    L_0x056e:
        r10 = "MESSAGE_VIDEOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0576:
        r10 = 23;
        goto L_0x0966;
    L_0x057a:
        r10 = "PHONE_CALL_MISSED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0582:
        r10 = 97;
        goto L_0x0966;
    L_0x0586:
        r10 = "MESSAGE_PHOTOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x058e:
        r10 = 22;
        goto L_0x0966;
    L_0x0592:
        r10 = "CHAT_MESSAGE_VIDEOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x059a:
        r10 = 70;
        goto L_0x0966;
    L_0x059e:
        r10 = "MESSAGE_NOTEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x05a6:
        r10 = 2;
        goto L_0x0966;
    L_0x05a9:
        r10 = "MESSAGE_GIF";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x05b1:
        r10 = 16;
        goto L_0x0966;
    L_0x05b5:
        r10 = "MESSAGE_GEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x05bd:
        r10 = 14;
        goto L_0x0966;
    L_0x05c1:
        r10 = "MESSAGE_DOC";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x05c9:
        r10 = 9;
        goto L_0x0966;
    L_0x05cd:
        r10 = "CHAT_MESSAGE_GAME_SCORE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x05d5:
        r10 = 56;
        goto L_0x0966;
    L_0x05d9:
        r10 = "CHANNEL_MESSAGE_GEOLIVE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x05e1:
        r10 = 35;
        goto L_0x0966;
    L_0x05e5:
        r10 = "CHAT_MESSAGE_PHOTOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x05ed:
        r10 = 69;
        goto L_0x0966;
    L_0x05f1:
        r10 = "CHAT_MESSAGE_NOTEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x05f9:
        r10 = 43;
        goto L_0x0966;
    L_0x05fd:
        r10 = "CHAT_TITLE_EDITED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0605:
        r10 = 60;
        goto L_0x0966;
    L_0x0609:
        r10 = "PINNED_NOTEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0611:
        r10 = 73;
        goto L_0x0966;
    L_0x0615:
        r10 = "MESSAGE_TEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x061d:
        r10 = 0;
        goto L_0x0966;
    L_0x0620:
        r10 = "MESSAGE_POLL";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0628:
        r10 = 13;
        goto L_0x0966;
    L_0x062c:
        r10 = "MESSAGE_GAME";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0634:
        r10 = 17;
        goto L_0x0966;
    L_0x0638:
        r10 = "MESSAGE_FWDS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0640:
        r10 = 21;
        goto L_0x0966;
    L_0x0644:
        r10 = "CHAT_MESSAGE_TEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x064c:
        r10 = 42;
        goto L_0x0966;
    L_0x0650:
        r10 = "CHAT_MESSAGE_POLL";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0658:
        r10 = 51;
        goto L_0x0966;
    L_0x065c:
        r10 = "CHAT_MESSAGE_GAME";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0664:
        r10 = 55;
        goto L_0x0966;
    L_0x0668:
        r10 = "CHAT_MESSAGE_FWDS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0670:
        r10 = 68;
        goto L_0x0966;
    L_0x0674:
        r10 = "CHANNEL_MESSAGE_GAME_SCORE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x067c:
        r10 = 19;
        goto L_0x0966;
    L_0x0680:
        r10 = "PINNED_GEOLIVE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0688:
        r10 = 83;
        goto L_0x0966;
    L_0x068c:
        r10 = "MESSAGE_CONTACT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0694:
        r10 = 12;
        goto L_0x0966;
    L_0x0698:
        r10 = "PINNED_VIDEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x06a0:
        r10 = 75;
        goto L_0x0966;
    L_0x06a4:
        r10 = "PINNED_ROUND";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x06ac:
        r10 = 76;
        goto L_0x0966;
    L_0x06b0:
        r10 = "PINNED_PHOTO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x06b8:
        r10 = 74;
        goto L_0x0966;
    L_0x06bc:
        r10 = "PINNED_AUDIO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x06c4:
        r10 = 79;
        goto L_0x0966;
    L_0x06c8:
        r10 = "MESSAGE_PHOTO_SECRET";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x06d0:
        r10 = 4;
        goto L_0x0966;
    L_0x06d3:
        r10 = "CHANNEL_MESSAGE_VIDEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x06db:
        r10 = 27;
        goto L_0x0966;
    L_0x06df:
        r10 = "CHANNEL_MESSAGE_ROUND";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x06e7:
        r10 = 28;
        goto L_0x0966;
    L_0x06eb:
        r10 = "CHANNEL_MESSAGE_PHOTO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x06f3:
        r10 = 26;
        goto L_0x0966;
    L_0x06f7:
        r10 = "CHANNEL_MESSAGE_AUDIO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x06ff:
        r10 = 31;
        goto L_0x0966;
    L_0x0703:
        r10 = "CHAT_MESSAGE_STICKER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x070b:
        r10 = 48;
        goto L_0x0966;
    L_0x070f:
        r10 = "MESSAGES";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0717:
        r10 = 24;
        goto L_0x0966;
    L_0x071b:
        r10 = "CHAT_MESSAGE_GIF";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0723:
        r10 = 54;
        goto L_0x0966;
    L_0x0727:
        r10 = "CHAT_MESSAGE_GEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x072f:
        r10 = 52;
        goto L_0x0966;
    L_0x0733:
        r10 = "CHAT_MESSAGE_DOC";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x073b:
        r10 = 47;
        goto L_0x0966;
    L_0x073f:
        r10 = "CHAT_LEFT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0747:
        r10 = 65;
        goto L_0x0966;
    L_0x074b:
        r10 = "CHAT_ADD_YOU";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0753:
        r10 = 59;
        goto L_0x0966;
    L_0x0757:
        r10 = "CHAT_DELETE_MEMBER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x075f:
        r10 = 63;
        goto L_0x0966;
    L_0x0763:
        r10 = "MESSAGE_SCREENSHOT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x076b:
        r10 = 7;
        goto L_0x0966;
    L_0x076e:
        r10 = "AUTH_REGION";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0776:
        r10 = 91;
        goto L_0x0966;
    L_0x077a:
        r10 = "CONTACT_JOINED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0782:
        r10 = 89;
        goto L_0x0966;
    L_0x0786:
        r10 = "CHAT_MESSAGE_INVOICE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x078e:
        r10 = 57;
        goto L_0x0966;
    L_0x0792:
        r10 = "ENCRYPTION_REQUEST";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x079a:
        r10 = 93;
        goto L_0x0966;
    L_0x079e:
        r10 = "MESSAGE_GEOLIVE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x07a6:
        r10 = 15;
        goto L_0x0966;
    L_0x07aa:
        r10 = "CHAT_DELETE_YOU";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x07b2:
        r10 = 64;
        goto L_0x0966;
    L_0x07b6:
        r10 = "AUTH_UNKNOWN";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x07be:
        r10 = 90;
        goto L_0x0966;
    L_0x07c2:
        r10 = "PINNED_GIF";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x07ca:
        r10 = 87;
        goto L_0x0966;
    L_0x07ce:
        r10 = "PINNED_GEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x07d6:
        r10 = 82;
        goto L_0x0966;
    L_0x07da:
        r10 = "PINNED_DOC";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x07e2:
        r10 = 77;
        goto L_0x0966;
    L_0x07e6:
        r10 = "PINNED_GAME_SCORE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x07ee:
        r10 = 85;
        goto L_0x0966;
    L_0x07f2:
        r10 = "CHANNEL_MESSAGE_STICKER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x07fa:
        r10 = 30;
        goto L_0x0966;
    L_0x07fe:
        r10 = "PHONE_CALL_REQUEST";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0806:
        r10 = 95;
        goto L_0x0966;
    L_0x080a:
        r10 = "PINNED_STICKER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0812:
        r10 = 78;
        goto L_0x0966;
    L_0x0816:
        r10 = "PINNED_TEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x081e:
        r10 = 72;
        goto L_0x0966;
    L_0x0822:
        r10 = "PINNED_POLL";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x082a:
        r10 = 81;
        goto L_0x0966;
    L_0x082e:
        r10 = "PINNED_GAME";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0836:
        r10 = 84;
        goto L_0x0966;
    L_0x083a:
        r10 = "CHAT_MESSAGE_CONTACT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0842:
        r10 = 50;
        goto L_0x0966;
    L_0x0846:
        r10 = "MESSAGE_VIDEO_SECRET";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x084e:
        r10 = 6;
        goto L_0x0966;
    L_0x0851:
        r10 = "CHANNEL_MESSAGE_TEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0859:
        r10 = 1;
        goto L_0x0966;
    L_0x085c:
        r10 = "CHANNEL_MESSAGE_POLL";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0864:
        r10 = 33;
        goto L_0x0966;
    L_0x0868:
        r10 = "CHANNEL_MESSAGE_GAME";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0870:
        r10 = 37;
        goto L_0x0966;
    L_0x0874:
        r10 = "CHANNEL_MESSAGE_FWDS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x087c:
        r10 = 38;
        goto L_0x0966;
    L_0x0880:
        r10 = "PINNED_INVOICE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0888:
        r10 = 86;
        goto L_0x0966;
    L_0x088c:
        r10 = "CHAT_RETURNED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0894:
        r10 = 66;
        goto L_0x0966;
    L_0x0898:
        r10 = "ENCRYPTED_MESSAGE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x08a0:
        r10 = 88;
        goto L_0x0966;
    L_0x08a4:
        r10 = "ENCRYPTION_ACCEPT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x08ac:
        r10 = 94;
        goto L_0x0966;
    L_0x08b0:
        r10 = "MESSAGE_VIDEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x08b8:
        r10 = 5;
        goto L_0x0966;
    L_0x08bb:
        r10 = "MESSAGE_ROUND";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x08c3:
        r10 = 8;
        goto L_0x0966;
    L_0x08c7:
        r10 = "MESSAGE_PHOTO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x08cf:
        r10 = 3;
        goto L_0x0966;
    L_0x08d2:
        r10 = "MESSAGE_MUTED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x08da:
        r10 = 96;
        goto L_0x0966;
    L_0x08de:
        r10 = "MESSAGE_AUDIO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x08e6:
        r10 = 11;
        goto L_0x0966;
    L_0x08ea:
        r10 = "CHAT_MESSAGES";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x08f2:
        r10 = 71;
        goto L_0x0966;
    L_0x08f6:
        r10 = "CHAT_JOINED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x08fe:
        r10 = 67;
        goto L_0x0966;
    L_0x0902:
        r10 = "CHAT_ADD_MEMBER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x090a:
        r10 = 62;
        goto L_0x0966;
    L_0x090d:
        r10 = "CHANNEL_MESSAGE_GIF";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0915:
        r10 = 36;
        goto L_0x0966;
    L_0x0918:
        r10 = "CHANNEL_MESSAGE_GEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0920:
        r10 = 34;
        goto L_0x0966;
    L_0x0923:
        r10 = "CHANNEL_MESSAGE_DOC";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x092b:
        r10 = 29;
        goto L_0x0966;
    L_0x092e:
        r10 = "CHANNEL_MESSAGE_VIDEOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0936:
        r10 = 40;
        goto L_0x0966;
    L_0x0939:
        r10 = "MESSAGE_STICKER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0941:
        r10 = 10;
        goto L_0x0966;
    L_0x0944:
        r10 = "CHAT_CREATED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x094c:
        r10 = 58;
        goto L_0x0966;
    L_0x094f:
        r10 = "CHANNEL_MESSAGE_CONTACT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0957:
        r10 = 32;
        goto L_0x0966;
    L_0x095a:
        r10 = "MESSAGE_GAME_SCORE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x0455 }
        if (r10 == 0) goto L_0x0965;
    L_0x0962:
        r10 = 18;
        goto L_0x0966;
    L_0x0965:
        r10 = -1;
    L_0x0966:
        r14 = "ChannelMessageFew";
        r11 = " ";
        r32 = r7;
        r7 = "AttachSticker";
        switch(r10) {
            case 0: goto L_0x16bf;
            case 1: goto L_0x16bf;
            case 2: goto L_0x169c;
            case 3: goto L_0x167d;
            case 4: goto L_0x165e;
            case 5: goto L_0x163f;
            case 6: goto L_0x161f;
            case 7: goto L_0x1605;
            case 8: goto L_0x15e5;
            case 9: goto L_0x15c5;
            case 10: goto L_0x1566;
            case 11: goto L_0x1546;
            case 12: goto L_0x1521;
            case 13: goto L_0x14fc;
            case 14: goto L_0x14dc;
            case 15: goto L_0x14bc;
            case 16: goto L_0x149c;
            case 17: goto L_0x1477;
            case 18: goto L_0x1456;
            case 19: goto L_0x1456;
            case 20: goto L_0x1431;
            case 21: goto L_0x140a;
            case 22: goto L_0x13e1;
            case 23: goto L_0x13b8;
            case 24: goto L_0x13a0;
            case 25: goto L_0x1380;
            case 26: goto L_0x1360;
            case 27: goto L_0x1340;
            case 28: goto L_0x1320;
            case 29: goto L_0x1300;
            case 30: goto L_0x12a1;
            case 31: goto L_0x1281;
            case 32: goto L_0x125c;
            case 33: goto L_0x1237;
            case 34: goto L_0x1217;
            case 35: goto L_0x11f7;
            case 36: goto L_0x11d7;
            case 37: goto L_0x11b7;
            case 38: goto L_0x118b;
            case 39: goto L_0x1163;
            case 40: goto L_0x113b;
            case 41: goto L_0x1124;
            case 42: goto L_0x1101;
            case 43: goto L_0x10dc;
            case 44: goto L_0x10b7;
            case 45: goto L_0x1092;
            case 46: goto L_0x106d;
            case 47: goto L_0x1048;
            case 48: goto L_0x0fc6;
            case 49: goto L_0x0f9f;
            case 50: goto L_0x0var_;
            case 51: goto L_0x0var_;
            case 52: goto L_0x0f2f;
            case 53: goto L_0x0f0c;
            case 54: goto L_0x0ee9;
            case 55: goto L_0x0ec1;
            case 56: goto L_0x0e9a;
            case 57: goto L_0x0e72;
            case 58: goto L_0x0e58;
            case 59: goto L_0x0e58;
            case 60: goto L_0x0e3e;
            case 61: goto L_0x0e24;
            case 62: goto L_0x0e05;
            case 63: goto L_0x0deb;
            case 64: goto L_0x0dd1;
            case 65: goto L_0x0db7;
            case 66: goto L_0x0d9d;
            case 67: goto L_0x0d83;
            case 68: goto L_0x0d55;
            case 69: goto L_0x0d28;
            case 70: goto L_0x0cfb;
            case 71: goto L_0x0cdf;
            case 72: goto L_0x0ca8;
            case 73: goto L_0x0CLASSNAME;
            case 74: goto L_0x0c4b;
            case 75: goto L_0x0c1e;
            case 76: goto L_0x0bef;
            case 77: goto L_0x0bc0;
            case 78: goto L_0x0b44;
            case 79: goto L_0x0b15;
            case 80: goto L_0x0adc;
            case 81: goto L_0x0aa7;
            case 82: goto L_0x0a77;
            case 83: goto L_0x0a4c;
            case 84: goto L_0x0a21;
            case 85: goto L_0x09f4;
            case 86: goto L_0x09c7;
            case 87: goto L_0x099a;
            case 88: goto L_0x097f;
            case 89: goto L_0x0979;
            case 90: goto L_0x0979;
            case 91: goto L_0x0979;
            case 92: goto L_0x0979;
            case 93: goto L_0x0979;
            case 94: goto L_0x0979;
            case 95: goto L_0x0979;
            case 96: goto L_0x0979;
            case 97: goto L_0x0979;
            default: goto L_0x0971;
        };
    L_0x0971:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x179b }
        goto L_0x16dc;
    L_0x0979:
        r21 = r1;
        r10 = r32;
        goto L_0x16f2;
    L_0x097f:
        r7 = "YouHaveNewMessage";
        r8 = NUM; // 0x7f0e0ca1 float:1.8881595E38 double:1.053163754E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        r8 = "SecretChatName";
        r10 = NUM; // 0x7f0e09c6 float:1.8880112E38 double:1.053163393E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r10);	 Catch:{ all -> 0x0455 }
        r21 = r1;
        r30 = r8;
        r10 = r32;
    L_0x0997:
        r1 = 1;
        goto L_0x16f4;
    L_0x099a:
        if (r1 == 0) goto L_0x09b4;
    L_0x099c:
        r7 = "NotificationActionPinnedGif";
        r8 = NUM; // 0x7f0e06d6 float:1.8878587E38 double:1.053163021E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r10[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r10[r11] = r14;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x0455 }
        goto L_0x0aa1;
    L_0x09b4:
        r7 = "NotificationActionPinnedGifChannel";
        r8 = NUM; // 0x7f0e06d7 float:1.887859E38 double:1.0531630217E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x0455 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0455 }
        r11[r10] = r14;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0aa1;
    L_0x09c7:
        if (r1 == 0) goto L_0x09e1;
    L_0x09c9:
        r7 = "NotificationActionPinnedInvoice";
        r8 = NUM; // 0x7f0e06d8 float:1.8878591E38 double:1.053163022E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r10[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r10[r11] = r14;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x0455 }
        goto L_0x0aa1;
    L_0x09e1:
        r7 = "NotificationActionPinnedInvoiceChannel";
        r8 = NUM; // 0x7f0e06d9 float:1.8878593E38 double:1.0531630227E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x0455 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0455 }
        r11[r10] = r14;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0aa1;
    L_0x09f4:
        if (r1 == 0) goto L_0x0a0e;
    L_0x09f6:
        r7 = "NotificationActionPinnedGameScore";
        r8 = NUM; // 0x7f0e06d0 float:1.8878575E38 double:1.0531630183E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r10[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r10[r11] = r14;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x0455 }
        goto L_0x0aa1;
    L_0x0a0e:
        r7 = "NotificationActionPinnedGameScoreChannel";
        r8 = NUM; // 0x7f0e06d1 float:1.8878577E38 double:1.053163019E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x0455 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0455 }
        r11[r10] = r14;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0aa1;
    L_0x0a21:
        if (r1 == 0) goto L_0x0a3a;
    L_0x0a23:
        r7 = "NotificationActionPinnedGame";
        r8 = NUM; // 0x7f0e06ce float:1.887857E38 double:1.0531630173E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r10[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r10[r11] = r14;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x0455 }
        goto L_0x0aa1;
    L_0x0a3a:
        r7 = "NotificationActionPinnedGameChannel";
        r8 = NUM; // 0x7f0e06cf float:1.8878573E38 double:1.053163018E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x0455 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0455 }
        r11[r10] = r14;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0aa1;
    L_0x0a4c:
        if (r1 == 0) goto L_0x0a65;
    L_0x0a4e:
        r7 = "NotificationActionPinnedGeoLive";
        r8 = NUM; // 0x7f0e06d4 float:1.8878583E38 double:1.0531630203E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r10[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r10[r11] = r14;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x0455 }
        goto L_0x0aa1;
    L_0x0a65:
        r7 = "NotificationActionPinnedGeoLiveChannel";
        r8 = NUM; // 0x7f0e06d5 float:1.8878585E38 double:1.0531630208E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x0455 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0455 }
        r11[r10] = r14;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0aa1;
    L_0x0a77:
        if (r1 == 0) goto L_0x0a90;
    L_0x0a79:
        r7 = "NotificationActionPinnedGeo";
        r8 = NUM; // 0x7f0e06d2 float:1.8878579E38 double:1.0531630193E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r10[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r10[r11] = r14;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x0455 }
        goto L_0x0aa1;
    L_0x0a90:
        r7 = "NotificationActionPinnedGeoChannel";
        r8 = NUM; // 0x7f0e06d3 float:1.887858E38 double:1.05316302E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x0455 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0455 }
        r11[r10] = r14;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
    L_0x0aa1:
        r21 = r1;
        r10 = r32;
        goto L_0x161c;
    L_0x0aa7:
        if (r1 == 0) goto L_0x0ac5;
    L_0x0aa9:
        r7 = "NotificationActionPinnedPoll2";
        r8 = NUM; // 0x7f0e06e0 float:1.8878607E38 double:1.053163026E-314;
        r10 = 3;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r10[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r10[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 2;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r10[r11] = r14;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x0455 }
        goto L_0x0aa1;
    L_0x0ac5:
        r7 = "NotificationActionPinnedPollChannel2";
        r8 = NUM; // 0x7f0e06e1 float:1.887861E38 double:1.0531630267E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r10[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r10[r11] = r14;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x0455 }
        goto L_0x0aa1;
    L_0x0adc:
        r10 = r32;
        if (r1 == 0) goto L_0x0afd;
    L_0x0ae0:
        r8 = "NotificationActionPinnedContact2";
        r11 = NUM; // 0x7f0e06ca float:1.8878563E38 double:1.0531630153E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r18;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r18;	 Catch:{ all -> 0x0455 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0afd:
        r7 = "NotificationActionPinnedContactChannel2";
        r8 = NUM; // 0x7f0e06cb float:1.8878565E38 double:1.053163016E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0b15:
        r10 = r32;
        if (r1 == 0) goto L_0x0b31;
    L_0x0b19:
        r7 = "NotificationActionPinnedVoice";
        r8 = NUM; // 0x7f0e06ec float:1.8878632E38 double:1.053163032E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0b31:
        r7 = "NotificationActionPinnedVoiceChannel";
        r8 = NUM; // 0x7f0e06ed float:1.8878634E38 double:1.0531630326E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x0455 }
        r14[r11] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0b44:
        r10 = r32;
        if (r1 == 0) goto L_0x0b89;
    L_0x0b48:
        r8 = r15.length;	 Catch:{ all -> 0x0455 }
        r11 = 2;
        if (r8 <= r11) goto L_0x0b71;
    L_0x0b4c:
        r8 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8 = android.text.TextUtils.isEmpty(r8);	 Catch:{ all -> 0x0455 }
        if (r8 != 0) goto L_0x0b71;
    L_0x0b54:
        r8 = "NotificationActionPinnedStickerEmoji";
        r11 = NUM; // 0x7f0e06e6 float:1.887862E38 double:1.053163029E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r18;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r18;	 Catch:{ all -> 0x0455 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0b71:
        r7 = "NotificationActionPinnedSticker";
        r8 = NUM; // 0x7f0e06e4 float:1.8878615E38 double:1.053163028E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0b89:
        r7 = r15.length;	 Catch:{ all -> 0x0455 }
        r8 = 1;
        if (r7 <= r8) goto L_0x0bad;
    L_0x0b8d:
        r7 = r15[r8];	 Catch:{ all -> 0x0455 }
        r7 = android.text.TextUtils.isEmpty(r7);	 Catch:{ all -> 0x0455 }
        if (r7 != 0) goto L_0x0bad;
    L_0x0b95:
        r7 = "NotificationActionPinnedStickerEmojiChannel";
        r8 = NUM; // 0x7f0e06e7 float:1.8878621E38 double:1.0531630296E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0bad:
        r7 = "NotificationActionPinnedStickerChannel";
        r8 = NUM; // 0x7f0e06e5 float:1.8878617E38 double:1.0531630287E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x0455 }
        r14[r11] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0bc0:
        r10 = r32;
        if (r1 == 0) goto L_0x0bdc;
    L_0x0bc4:
        r7 = "NotificationActionPinnedFile";
        r8 = NUM; // 0x7f0e06cc float:1.8878567E38 double:1.0531630163E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0bdc:
        r7 = "NotificationActionPinnedFileChannel";
        r8 = NUM; // 0x7f0e06cd float:1.8878569E38 double:1.053163017E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x0455 }
        r14[r11] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0bef:
        r10 = r32;
        if (r1 == 0) goto L_0x0c0b;
    L_0x0bf3:
        r7 = "NotificationActionPinnedRound";
        r8 = NUM; // 0x7f0e06e2 float:1.8878611E38 double:1.053163027E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0c0b:
        r7 = "NotificationActionPinnedRoundChannel";
        r8 = NUM; // 0x7f0e06e3 float:1.8878613E38 double:1.0531630277E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x0455 }
        r14[r11] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0c1e:
        r10 = r32;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r7 = "NotificationActionPinnedVideo";
        r8 = NUM; // 0x7f0e06ea float:1.8878628E38 double:1.053163031E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0CLASSNAME:
        r7 = "NotificationActionPinnedVideoChannel";
        r8 = NUM; // 0x7f0e06eb float:1.887863E38 double:1.0531630316E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x0455 }
        r14[r11] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0c4b:
        r10 = r32;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0c4f:
        r7 = "NotificationActionPinnedPhoto";
        r8 = NUM; // 0x7f0e06de float:1.8878603E38 double:1.053163025E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0CLASSNAME:
        r7 = "NotificationActionPinnedPhotoChannel";
        r8 = NUM; // 0x7f0e06df float:1.8878605E38 double:1.0531630257E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x0455 }
        r14[r11] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0CLASSNAME:
        r10 = r32;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0c7c:
        r7 = "NotificationActionPinnedNoText";
        r8 = NUM; // 0x7f0e06dc float:1.88786E38 double:1.053163024E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0CLASSNAME:
        r7 = "NotificationActionPinnedNoTextChannel";
        r8 = NUM; // 0x7f0e06dd float:1.8878601E38 double:1.0531630247E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x0455 }
        r14[r11] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x0455 }
    L_0x0ca4:
        r21 = r1;
        goto L_0x161c;
    L_0x0ca8:
        r10 = r32;
        if (r1 == 0) goto L_0x0cc8;
    L_0x0cac:
        r8 = "NotificationActionPinnedText";
        r11 = NUM; // 0x7f0e06e8 float:1.8878623E38 double:1.05316303E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r18;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r18;	 Catch:{ all -> 0x0455 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0cc8:
        r7 = "NotificationActionPinnedTextChannel";
        r8 = NUM; // 0x7f0e06e9 float:1.8878625E38 double:1.0531630306E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0cdf:
        r10 = r32;
        r7 = "NotificationGroupAlbum";
        r8 = NUM; // 0x7f0e06f5 float:1.887865E38 double:1.0531630366E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
    L_0x0cf7:
        r21 = r1;
        goto L_0x0997;
    L_0x0cfb:
        r10 = r32;
        r8 = "NotificationGroupFew";
        r11 = NUM; // 0x7f0e06f6 float:1.8878652E38 double:1.053163037E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r18;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r18;	 Catch:{ all -> 0x0455 }
        r14 = "Videos";
        r17 = 2;
        r15 = r15[r17];	 Catch:{ all -> 0x0455 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x0455 }
        r15 = r15.intValue();	 Catch:{ all -> 0x0455 }
        r14 = org.telegram.messenger.LocaleController.formatPluralString(r14, r15);	 Catch:{ all -> 0x0455 }
        r7[r17] = r14;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x0455 }
        goto L_0x0cf7;
    L_0x0d28:
        r10 = r32;
        r8 = "NotificationGroupFew";
        r11 = NUM; // 0x7f0e06f6 float:1.8878652E38 double:1.053163037E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r18;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r18;	 Catch:{ all -> 0x0455 }
        r14 = "Photos";
        r17 = 2;
        r15 = r15[r17];	 Catch:{ all -> 0x0455 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x0455 }
        r15 = r15.intValue();	 Catch:{ all -> 0x0455 }
        r14 = org.telegram.messenger.LocaleController.formatPluralString(r14, r15);	 Catch:{ all -> 0x0455 }
        r7[r17] = r14;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x0455 }
        goto L_0x0cf7;
    L_0x0d55:
        r10 = r32;
        r11 = "NotificationGroupForwardedFew";
        r14 = NUM; // 0x7f0e06f7 float:1.8878654E38 double:1.0531630375E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0455 }
        r18 = 0;
        r21 = r15[r18];	 Catch:{ all -> 0x0455 }
        r7[r18] = r21;	 Catch:{ all -> 0x0455 }
        r18 = 1;
        r21 = r15[r18];	 Catch:{ all -> 0x0455 }
        r7[r18] = r21;	 Catch:{ all -> 0x0455 }
        r17 = 2;
        r15 = r15[r17];	 Catch:{ all -> 0x0455 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x0455 }
        r15 = r15.intValue();	 Catch:{ all -> 0x0455 }
        r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r15);	 Catch:{ all -> 0x0455 }
        r7[r17] = r8;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r11, r14, r7);	 Catch:{ all -> 0x0455 }
        goto L_0x0cf7;
    L_0x0d83:
        r10 = r32;
        r7 = "NotificationGroupAddSelfMega";
        r8 = NUM; // 0x7f0e06f4 float:1.8878648E38 double:1.053163036E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0d9d:
        r10 = r32;
        r7 = "NotificationGroupAddSelf";
        r8 = NUM; // 0x7f0e06f3 float:1.8878646E38 double:1.0531630356E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0db7:
        r10 = r32;
        r7 = "NotificationGroupLeftMember";
        r8 = NUM; // 0x7f0e06fa float:1.887866E38 double:1.053163039E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0dd1:
        r10 = r32;
        r7 = "NotificationGroupKickYou";
        r8 = NUM; // 0x7f0e06f9 float:1.8878658E38 double:1.0531630385E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0deb:
        r10 = r32;
        r7 = "NotificationGroupKickMember";
        r8 = NUM; // 0x7f0e06f8 float:1.8878656E38 double:1.053163038E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0e05:
        r10 = r32;
        r8 = "NotificationGroupAddMember";
        r11 = NUM; // 0x7f0e06f2 float:1.8878644E38 double:1.053163035E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r18;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r18;	 Catch:{ all -> 0x0455 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0e24:
        r10 = r32;
        r7 = "NotificationEditedGroupPhoto";
        r8 = NUM; // 0x7f0e06f1 float:1.8878642E38 double:1.0531630346E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0e3e:
        r10 = r32;
        r7 = "NotificationEditedGroupName";
        r8 = NUM; // 0x7f0e06f0 float:1.887864E38 double:1.053163034E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0e58:
        r10 = r32;
        r7 = "NotificationInvitedToGroup";
        r8 = NUM; // 0x7f0e06ff float:1.887867E38 double:1.0531630415E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0e72:
        r10 = r32;
        r8 = "NotificationMessageGroupInvoice";
        r11 = NUM; // 0x7f0e0710 float:1.8878705E38 double:1.05316305E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r16;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r16 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r16;	 Catch:{ all -> 0x0455 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x0455 }
        r8 = "PaymentInvoice";
        r11 = NUM; // 0x7f0e0875 float:1.8879429E38 double:1.0531632263E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0fc0;
    L_0x0e9a:
        r10 = r32;
        r8 = "NotificationMessageGroupGameScored";
        r11 = NUM; // 0x7f0e070e float:1.88787E38 double:1.053163049E-314;
        r14 = 4;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x0455 }
        r18 = 0;
        r21 = r15[r18];	 Catch:{ all -> 0x0455 }
        r14[r18] = r21;	 Catch:{ all -> 0x0455 }
        r18 = 1;
        r21 = r15[r18];	 Catch:{ all -> 0x0455 }
        r14[r18] = r21;	 Catch:{ all -> 0x0455 }
        r17 = 2;
        r18 = r15[r17];	 Catch:{ all -> 0x0455 }
        r14[r17] = r18;	 Catch:{ all -> 0x0455 }
        r7 = 3;
        r15 = r15[r7];	 Catch:{ all -> 0x0455 }
        r14[r7] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r14);	 Catch:{ all -> 0x0455 }
        goto L_0x0ca4;
    L_0x0ec1:
        r10 = r32;
        r8 = "NotificationMessageGroupGame";
        r11 = NUM; // 0x7f0e070d float:1.8878699E38 double:1.0531630484E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r16;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r16 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r16;	 Catch:{ all -> 0x0455 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x0455 }
        r8 = "AttachGame";
        r11 = NUM; // 0x7f0e014e float:1.8875715E38 double:1.0531623216E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0fc0;
    L_0x0ee9:
        r10 = r32;
        r7 = "NotificationMessageGroupGif";
        r8 = NUM; // 0x7f0e070f float:1.8878703E38 double:1.0531630494E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r16;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        r8 = "AttachGif";
        r11 = NUM; // 0x7f0e014f float:1.8875717E38 double:1.053162322E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0fc0;
    L_0x0f0c:
        r10 = r32;
        r7 = "NotificationMessageGroupLiveLocation";
        r8 = NUM; // 0x7f0e0711 float:1.8878707E38 double:1.0531630504E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r16;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        r8 = "AttachLiveLocation";
        r11 = NUM; // 0x7f0e0154 float:1.8875727E38 double:1.0531623246E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0fc0;
    L_0x0f2f:
        r10 = r32;
        r7 = "NotificationMessageGroupMap";
        r8 = NUM; // 0x7f0e0712 float:1.8878709E38 double:1.053163051E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r16;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        r8 = "AttachLocation";
        r11 = NUM; // 0x7f0e0156 float:1.8875731E38 double:1.0531623256E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0fc0;
    L_0x0var_:
        r10 = r32;
        r8 = "NotificationMessageGroupPoll2";
        r11 = NUM; // 0x7f0e0716 float:1.8878717E38 double:1.053163053E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r16;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r16 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r16;	 Catch:{ all -> 0x0455 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x0455 }
        r8 = "Poll";
        r11 = NUM; // 0x7f0e08e3 float:1.8879652E38 double:1.0531632806E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0fc0;
    L_0x0var_:
        r10 = r32;
        r8 = "NotificationMessageGroupContact2";
        r11 = NUM; // 0x7f0e070b float:1.8878694E38 double:1.0531630474E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r16;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r16 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r16;	 Catch:{ all -> 0x0455 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r7[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x0455 }
        r8 = "AttachContact";
        r11 = NUM; // 0x7f0e014a float:1.8875707E38 double:1.0531623197E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x0fc0;
    L_0x0f9f:
        r10 = r32;
        r7 = "NotificationMessageGroupAudio";
        r8 = NUM; // 0x7f0e070a float:1.8878692E38 double:1.053163047E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r16;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r15;	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x0455 }
        r8 = "AttachAudio";
        r11 = NUM; // 0x7f0e0148 float:1.8875703E38 double:1.0531623187E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x0455 }
    L_0x0fc0:
        r21 = r1;
        r16 = r8;
        goto L_0x16bd;
    L_0x0fc6:
        r10 = r32;
        r14 = r15.length;	 Catch:{ all -> 0x0455 }
        r8 = 2;
        if (r14 <= r8) goto L_0x1011;
    L_0x0fcc:
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r8 = android.text.TextUtils.isEmpty(r14);	 Catch:{ all -> 0x0455 }
        if (r8 != 0) goto L_0x1011;
    L_0x0fd4:
        r8 = "NotificationMessageGroupStickerEmoji";
        r14 = 3;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x0455 }
        r18 = 0;
        r21 = r15[r18];	 Catch:{ all -> 0x0455 }
        r14[r18] = r21;	 Catch:{ all -> 0x0455 }
        r18 = 1;
        r21 = r15[r18];	 Catch:{ all -> 0x0455 }
        r14[r18] = r21;	 Catch:{ all -> 0x0455 }
        r17 = 2;
        r18 = r15[r17];	 Catch:{ all -> 0x0455 }
        r14[r17] = r18;	 Catch:{ all -> 0x0455 }
        r21 = r1;
        r1 = NUM; // 0x7f0e0719 float:1.8878723E38 double:1.0531630543E-314;
        r1 = org.telegram.messenger.LocaleController.formatString(r8, r1, r14);	 Catch:{ all -> 0x0455 }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0455 }
        r8.<init>();	 Catch:{ all -> 0x0455 }
        r14 = r15[r17];	 Catch:{ all -> 0x0455 }
        r8.append(r14);	 Catch:{ all -> 0x0455 }
        r8.append(r11);	 Catch:{ all -> 0x0455 }
        r11 = NUM; // 0x7f0e015d float:1.8875745E38 double:1.053162329E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r11);	 Catch:{ all -> 0x0455 }
        r8.append(r7);	 Catch:{ all -> 0x0455 }
        r7 = r8.toString();	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1011:
        r21 = r1;
        r1 = "NotificationMessageGroupSticker";
        r8 = NUM; // 0x7f0e0718 float:1.887872E38 double:1.053163054E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x0455 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ all -> 0x0455 }
        r14[r16] = r17;	 Catch:{ all -> 0x0455 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ all -> 0x0455 }
        r14[r16] = r17;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14);	 Catch:{ all -> 0x0455 }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0455 }
        r8.<init>();	 Catch:{ all -> 0x0455 }
        r14 = r15[r16];	 Catch:{ all -> 0x0455 }
        r8.append(r14);	 Catch:{ all -> 0x0455 }
        r8.append(r11);	 Catch:{ all -> 0x0455 }
        r11 = NUM; // 0x7f0e015d float:1.8875745E38 double:1.053162329E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r11);	 Catch:{ all -> 0x0455 }
        r8.append(r7);	 Catch:{ all -> 0x0455 }
        r7 = r8.toString();	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1048:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupDocument";
        r7 = NUM; // 0x7f0e070c float:1.8878696E38 double:1.053163048E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x0455 }
        r7 = "AttachDocument";
        r8 = NUM; // 0x7f0e014d float:1.8875713E38 double:1.053162321E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x106d:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupRound";
        r7 = NUM; // 0x7f0e0717 float:1.8878719E38 double:1.0531630534E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x0455 }
        r7 = "AttachRound";
        r8 = NUM; // 0x7f0e015c float:1.8875743E38 double:1.0531623286E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1092:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupVideo";
        r7 = NUM; // 0x7f0e071b float:1.8878727E38 double:1.0531630553E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x0455 }
        r7 = "AttachVideo";
        r8 = NUM; // 0x7f0e0160 float:1.8875751E38 double:1.0531623305E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x10b7:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupPhoto";
        r7 = NUM; // 0x7f0e0715 float:1.8878715E38 double:1.0531630524E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x0455 }
        r7 = "AttachPhoto";
        r8 = NUM; // 0x7f0e015a float:1.887574E38 double:1.0531623276E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x10dc:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupNoText";
        r7 = NUM; // 0x7f0e0714 float:1.8878713E38 double:1.053163052E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x0455 }
        r7 = "Message";
        r8 = NUM; // 0x7f0e0632 float:1.8878254E38 double:1.05316294E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1101:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupText";
        r8 = NUM; // 0x7f0e071a float:1.8878725E38 double:1.053163055E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r7[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r7[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 2;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r7[r11] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r7);	 Catch:{ all -> 0x0455 }
        r7 = r15[r11];	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1124:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageAlbum";
        r7 = NUM; // 0x7f0e025e float:1.8876267E38 double:1.053162456E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x13b5;
    L_0x113b:
        r21 = r1;
        r10 = r32;
        r1 = 2;
        r1 = new java.lang.Object[r1];	 Catch:{ all -> 0x0455 }
        r7 = 0;
        r8 = r15[r7];	 Catch:{ all -> 0x0455 }
        r1[r7] = r8;	 Catch:{ all -> 0x0455 }
        r7 = "Videos";
        r8 = 1;
        r11 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ all -> 0x0455 }
        r11 = r11.intValue();	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r11);	 Catch:{ all -> 0x0455 }
        r1[r8] = r7;	 Catch:{ all -> 0x0455 }
        r7 = NUM; // 0x7f0e0262 float:1.8876275E38 double:1.053162458E-314;
        r1 = org.telegram.messenger.LocaleController.formatString(r14, r7, r1);	 Catch:{ all -> 0x0455 }
        goto L_0x13b5;
    L_0x1163:
        r21 = r1;
        r10 = r32;
        r1 = 2;
        r1 = new java.lang.Object[r1];	 Catch:{ all -> 0x0455 }
        r7 = 0;
        r8 = r15[r7];	 Catch:{ all -> 0x0455 }
        r1[r7] = r8;	 Catch:{ all -> 0x0455 }
        r7 = "Photos";
        r8 = 1;
        r11 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ all -> 0x0455 }
        r11 = r11.intValue();	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r11);	 Catch:{ all -> 0x0455 }
        r1[r8] = r7;	 Catch:{ all -> 0x0455 }
        r7 = NUM; // 0x7f0e0262 float:1.8876275E38 double:1.053162458E-314;
        r1 = org.telegram.messenger.LocaleController.formatString(r14, r7, r1);	 Catch:{ all -> 0x0455 }
        goto L_0x13b5;
    L_0x118b:
        r21 = r1;
        r10 = r32;
        r1 = 2;
        r1 = new java.lang.Object[r1];	 Catch:{ all -> 0x0455 }
        r7 = 0;
        r8 = r15[r7];	 Catch:{ all -> 0x0455 }
        r1[r7] = r8;	 Catch:{ all -> 0x0455 }
        r7 = "ForwardedMessageCount";
        r8 = 1;
        r11 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ all -> 0x0455 }
        r11 = r11.intValue();	 Catch:{ all -> 0x0455 }
        r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = r7.toLowerCase();	 Catch:{ all -> 0x0455 }
        r1[r8] = r7;	 Catch:{ all -> 0x0455 }
        r7 = NUM; // 0x7f0e0262 float:1.8876275E38 double:1.053162458E-314;
        r1 = org.telegram.messenger.LocaleController.formatString(r14, r7, r1);	 Catch:{ all -> 0x0455 }
        goto L_0x13b5;
    L_0x11b7:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGame";
        r7 = NUM; // 0x7f0e0707 float:1.8878686E38 double:1.0531630455E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachGame";
        r8 = NUM; // 0x7f0e014e float:1.8875715E38 double:1.0531623216E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x11d7:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageGIF";
        r7 = NUM; // 0x7f0e0263 float:1.8876277E38 double:1.0531624585E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachGif";
        r8 = NUM; // 0x7f0e014f float:1.8875717E38 double:1.053162322E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x11f7:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageLiveLocation";
        r7 = NUM; // 0x7f0e0264 float:1.8876279E38 double:1.053162459E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachLiveLocation";
        r8 = NUM; // 0x7f0e0154 float:1.8875727E38 double:1.0531623246E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1217:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageMap";
        r7 = NUM; // 0x7f0e0265 float:1.887628E38 double:1.0531624595E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachLocation";
        r8 = NUM; // 0x7f0e0156 float:1.8875731E38 double:1.0531623256E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1237:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessagePoll2";
        r7 = NUM; // 0x7f0e0269 float:1.8876289E38 double:1.0531624615E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x0455 }
        r7 = "Poll";
        r8 = NUM; // 0x7f0e08e3 float:1.8879652E38 double:1.0531632806E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x125c:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageContact2";
        r7 = NUM; // 0x7f0e0260 float:1.887627E38 double:1.053162457E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x0455 }
        r7 = "AttachContact";
        r8 = NUM; // 0x7f0e014a float:1.8875707E38 double:1.0531623197E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1281:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageAudio";
        r7 = NUM; // 0x7f0e025f float:1.8876269E38 double:1.0531624565E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachAudio";
        r8 = NUM; // 0x7f0e0148 float:1.8875703E38 double:1.0531623187E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x12a1:
        r21 = r1;
        r10 = r32;
        r1 = r15.length;	 Catch:{ all -> 0x0455 }
        r8 = 1;
        if (r1 <= r8) goto L_0x12e6;
    L_0x12a9:
        r1 = r15[r8];	 Catch:{ all -> 0x0455 }
        r1 = android.text.TextUtils.isEmpty(r1);	 Catch:{ all -> 0x0455 }
        if (r1 != 0) goto L_0x12e6;
    L_0x12b1:
        r1 = "ChannelMessageStickerEmoji";
        r8 = NUM; // 0x7f0e026c float:1.8876295E38 double:1.053162463E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x0455 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ all -> 0x0455 }
        r14[r16] = r17;	 Catch:{ all -> 0x0455 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ all -> 0x0455 }
        r14[r16] = r17;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14);	 Catch:{ all -> 0x0455 }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0455 }
        r8.<init>();	 Catch:{ all -> 0x0455 }
        r14 = r15[r16];	 Catch:{ all -> 0x0455 }
        r8.append(r14);	 Catch:{ all -> 0x0455 }
        r8.append(r11);	 Catch:{ all -> 0x0455 }
        r11 = NUM; // 0x7f0e015d float:1.8875745E38 double:1.053162329E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r11);	 Catch:{ all -> 0x0455 }
        r8.append(r7);	 Catch:{ all -> 0x0455 }
        r7 = r8.toString();	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x12e6:
        r1 = "ChannelMessageSticker";
        r8 = NUM; // 0x7f0e026b float:1.8876293E38 double:1.0531624625E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x0455 }
        r14[r11] = r15;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14);	 Catch:{ all -> 0x0455 }
        r8 = NUM; // 0x7f0e015d float:1.8875745E38 double:1.053162329E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1300:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageDocument";
        r7 = NUM; // 0x7f0e0261 float:1.8876273E38 double:1.0531624575E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachDocument";
        r8 = NUM; // 0x7f0e014d float:1.8875713E38 double:1.053162321E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1320:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageRound";
        r7 = NUM; // 0x7f0e026a float:1.887629E38 double:1.053162462E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachRound";
        r8 = NUM; // 0x7f0e015c float:1.8875743E38 double:1.0531623286E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1340:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageVideo";
        r7 = NUM; // 0x7f0e026d float:1.8876297E38 double:1.0531624634E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachVideo";
        r8 = NUM; // 0x7f0e0160 float:1.8875751E38 double:1.0531623305E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1360:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessagePhoto";
        r7 = NUM; // 0x7f0e0268 float:1.8876287E38 double:1.053162461E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachPhoto";
        r8 = NUM; // 0x7f0e015a float:1.887574E38 double:1.0531623276E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1380:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageNoText";
        r7 = NUM; // 0x7f0e0267 float:1.8876285E38 double:1.0531624605E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "Message";
        r8 = NUM; // 0x7f0e0632 float:1.8878254E38 double:1.05316294E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x13a0:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageAlbum";
        r7 = NUM; // 0x7f0e0701 float:1.8878674E38 double:1.0531630425E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
    L_0x13b5:
        r7 = r1;
        goto L_0x0997;
    L_0x13b8:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageFew";
        r7 = NUM; // 0x7f0e0705 float:1.8878682E38 double:1.0531630445E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = "Videos";
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x0455 }
        r15 = r15.intValue();	 Catch:{ all -> 0x0455 }
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r15);	 Catch:{ all -> 0x0455 }
        r8[r14] = r11;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x13b5;
    L_0x13e1:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageFew";
        r7 = NUM; // 0x7f0e0705 float:1.8878682E38 double:1.0531630445E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = "Photos";
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x0455 }
        r15 = r15.intValue();	 Catch:{ all -> 0x0455 }
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r15);	 Catch:{ all -> 0x0455 }
        r8[r14] = r11;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x13b5;
    L_0x140a:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageForwardFew";
        r7 = NUM; // 0x7f0e0706 float:1.8878684E38 double:1.053163045E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0455 }
        r11[r14] = r17;	 Catch:{ all -> 0x0455 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0455 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x0455 }
        r15 = r15.intValue();	 Catch:{ all -> 0x0455 }
        r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r15);	 Catch:{ all -> 0x0455 }
        r11[r14] = r8;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        goto L_0x13b5;
    L_0x1431:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageInvoice";
        r7 = NUM; // 0x7f0e071c float:1.8878729E38 double:1.053163056E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x0455 }
        r7 = "PaymentInvoice";
        r8 = NUM; // 0x7f0e0875 float:1.8879429E38 double:1.0531632263E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1456:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGameScored";
        r8 = NUM; // 0x7f0e0708 float:1.8878688E38 double:1.053163046E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r7[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r7[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 2;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r7[r11] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r7);	 Catch:{ all -> 0x0455 }
        goto L_0x161b;
    L_0x1477:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGame";
        r7 = NUM; // 0x7f0e0707 float:1.8878686E38 double:1.0531630455E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x0455 }
        r7 = "AttachGame";
        r8 = NUM; // 0x7f0e014e float:1.8875715E38 double:1.0531623216E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x149c:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGif";
        r7 = NUM; // 0x7f0e0709 float:1.887869E38 double:1.0531630464E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachGif";
        r8 = NUM; // 0x7f0e014f float:1.8875717E38 double:1.053162322E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x14bc:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageLiveLocation";
        r7 = NUM; // 0x7f0e071d float:1.887873E38 double:1.0531630563E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachLiveLocation";
        r8 = NUM; // 0x7f0e0154 float:1.8875727E38 double:1.0531623246E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x14dc:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageMap";
        r7 = NUM; // 0x7f0e071e float:1.8878733E38 double:1.053163057E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachLocation";
        r8 = NUM; // 0x7f0e0156 float:1.8875731E38 double:1.0531623256E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x14fc:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessagePoll2";
        r7 = NUM; // 0x7f0e0722 float:1.8878741E38 double:1.053163059E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x0455 }
        r7 = "Poll";
        r8 = NUM; // 0x7f0e08e3 float:1.8879652E38 double:1.0531632806E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1521:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageContact2";
        r7 = NUM; // 0x7f0e0703 float:1.8878678E38 double:1.0531630435E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x0455 }
        r7 = "AttachContact";
        r8 = NUM; // 0x7f0e014a float:1.8875707E38 double:1.0531623197E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1546:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageAudio";
        r7 = NUM; // 0x7f0e0702 float:1.8878676E38 double:1.053163043E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachAudio";
        r8 = NUM; // 0x7f0e0148 float:1.8875703E38 double:1.0531623187E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1566:
        r21 = r1;
        r10 = r32;
        r1 = r15.length;	 Catch:{ all -> 0x0455 }
        r8 = 1;
        if (r1 <= r8) goto L_0x15ab;
    L_0x156e:
        r1 = r15[r8];	 Catch:{ all -> 0x0455 }
        r1 = android.text.TextUtils.isEmpty(r1);	 Catch:{ all -> 0x0455 }
        if (r1 != 0) goto L_0x15ab;
    L_0x1576:
        r1 = "NotificationMessageStickerEmoji";
        r8 = NUM; // 0x7f0e0729 float:1.8878755E38 double:1.0531630623E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x0455 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ all -> 0x0455 }
        r14[r16] = r17;	 Catch:{ all -> 0x0455 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ all -> 0x0455 }
        r14[r16] = r17;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14);	 Catch:{ all -> 0x0455 }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0455 }
        r8.<init>();	 Catch:{ all -> 0x0455 }
        r14 = r15[r16];	 Catch:{ all -> 0x0455 }
        r8.append(r14);	 Catch:{ all -> 0x0455 }
        r8.append(r11);	 Catch:{ all -> 0x0455 }
        r11 = NUM; // 0x7f0e015d float:1.8875745E38 double:1.053162329E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r11);	 Catch:{ all -> 0x0455 }
        r8.append(r7);	 Catch:{ all -> 0x0455 }
        r7 = r8.toString();	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x15ab:
        r1 = "NotificationMessageSticker";
        r8 = NUM; // 0x7f0e0728 float:1.8878753E38 double:1.053163062E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x0455 }
        r14[r11] = r15;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14);	 Catch:{ all -> 0x0455 }
        r8 = NUM; // 0x7f0e015d float:1.8875745E38 double:1.053162329E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x15c5:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageDocument";
        r7 = NUM; // 0x7f0e0704 float:1.887868E38 double:1.053163044E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachDocument";
        r8 = NUM; // 0x7f0e014d float:1.8875713E38 double:1.053162321E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x15e5:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageRound";
        r7 = NUM; // 0x7f0e0723 float:1.8878743E38 double:1.0531630593E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachRound";
        r8 = NUM; // 0x7f0e015c float:1.8875743E38 double:1.0531623286E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x1605:
        r21 = r1;
        r10 = r32;
        r1 = "ActionTakeScreenshoot";
        r7 = NUM; // 0x7f0e0093 float:1.8875336E38 double:1.0531622293E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r7);	 Catch:{ all -> 0x0455 }
        r7 = "un1";
        r8 = 0;
        r11 = r15[r8];	 Catch:{ all -> 0x0455 }
        r1 = r1.replace(r7, r11);	 Catch:{ all -> 0x0455 }
    L_0x161b:
        r7 = r1;
    L_0x161c:
        r1 = 0;
        goto L_0x16f4;
    L_0x161f:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageSDVideo";
        r7 = NUM; // 0x7f0e0725 float:1.8878747E38 double:1.0531630603E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachDestructingVideo";
        r8 = NUM; // 0x7f0e014c float:1.887571E38 double:1.0531623207E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x163f:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageVideo";
        r7 = NUM; // 0x7f0e072b float:1.887876E38 double:1.053163063E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachVideo";
        r8 = NUM; // 0x7f0e0160 float:1.8875751E38 double:1.0531623305E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x165e:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageSDPhoto";
        r7 = NUM; // 0x7f0e0724 float:1.8878745E38 double:1.05316306E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachDestructingPhoto";
        r8 = NUM; // 0x7f0e014b float:1.8875709E38 double:1.05316232E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x167d:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessagePhoto";
        r7 = NUM; // 0x7f0e0721 float:1.887874E38 double:1.0531630583E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "AttachPhoto";
        r8 = NUM; // 0x7f0e015a float:1.887574E38 double:1.0531623276E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x169c:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageNoText";
        r7 = NUM; // 0x7f0e0720 float:1.8878737E38 double:1.053163058E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x0455 }
        r11[r8] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x0455 }
        r7 = "Message";
        r8 = NUM; // 0x7f0e0632 float:1.8878254E38 double:1.05316294E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x0455 }
    L_0x16ba:
        r16 = r7;
        r7 = r1;
    L_0x16bd:
        r1 = 0;
        goto L_0x16f6;
    L_0x16bf:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageText";
        r7 = NUM; // 0x7f0e072a float:1.8878757E38 double:1.0531630627E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x0455 }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0455 }
        r8[r11] = r14;	 Catch:{ all -> 0x0455 }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x0455 }
        r7 = r15[r11];	 Catch:{ all -> 0x0455 }
        goto L_0x16ba;
    L_0x16dc:
        if (r1 == 0) goto L_0x16f2;
    L_0x16de:
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0455 }
        r1.<init>();	 Catch:{ all -> 0x0455 }
        r7 = "unhandled loc_key = ";
        r1.append(r7);	 Catch:{ all -> 0x0455 }
        r1.append(r9);	 Catch:{ all -> 0x0455 }
        r1 = r1.toString();	 Catch:{ all -> 0x0455 }
        org.telegram.messenger.FileLog.w(r1);	 Catch:{ all -> 0x0455 }
    L_0x16f2:
        r1 = 0;
        r7 = 0;
    L_0x16f4:
        r16 = 0;
    L_0x16f6:
        if (r7 == 0) goto L_0x1793;
    L_0x16f8:
        r8 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ all -> 0x179b }
        r8.<init>();	 Catch:{ all -> 0x179b }
        r8.id = r10;	 Catch:{ all -> 0x179b }
        r8.random_id = r3;	 Catch:{ all -> 0x179b }
        if (r16 == 0) goto L_0x1706;
    L_0x1703:
        r3 = r16;
        goto L_0x1707;
    L_0x1706:
        r3 = r7;
    L_0x1707:
        r8.message = r3;	 Catch:{ all -> 0x179b }
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r3 = r37 / r3;
        r4 = (int) r3;	 Catch:{ all -> 0x179b }
        r8.date = r4;	 Catch:{ all -> 0x179b }
        if (r6 == 0) goto L_0x1719;
    L_0x1712:
        r3 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage;	 Catch:{ all -> 0x0455 }
        r3.<init>();	 Catch:{ all -> 0x0455 }
        r8.action = r3;	 Catch:{ all -> 0x0455 }
    L_0x1719:
        if (r5 == 0) goto L_0x1722;
    L_0x171b:
        r3 = r8.flags;	 Catch:{ all -> 0x0455 }
        r4 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r3 = r3 | r4;
        r8.flags = r3;	 Catch:{ all -> 0x0455 }
    L_0x1722:
        r8.dialog_id = r12;	 Catch:{ all -> 0x179b }
        if (r2 == 0) goto L_0x1732;
    L_0x1726:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChannel;	 Catch:{ all -> 0x0455 }
        r3.<init>();	 Catch:{ all -> 0x0455 }
        r8.to_id = r3;	 Catch:{ all -> 0x0455 }
        r3 = r8.to_id;	 Catch:{ all -> 0x0455 }
        r3.channel_id = r2;	 Catch:{ all -> 0x0455 }
        goto L_0x174f;
    L_0x1732:
        if (r23 == 0) goto L_0x1742;
    L_0x1734:
        r2 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ all -> 0x0455 }
        r2.<init>();	 Catch:{ all -> 0x0455 }
        r8.to_id = r2;	 Catch:{ all -> 0x0455 }
        r2 = r8.to_id;	 Catch:{ all -> 0x0455 }
        r3 = r23;
        r2.chat_id = r3;	 Catch:{ all -> 0x0455 }
        goto L_0x174f;
    L_0x1742:
        r2 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ all -> 0x179b }
        r2.<init>();	 Catch:{ all -> 0x179b }
        r8.to_id = r2;	 Catch:{ all -> 0x179b }
        r2 = r8.to_id;	 Catch:{ all -> 0x179b }
        r3 = r25;
        r2.user_id = r3;	 Catch:{ all -> 0x179b }
    L_0x174f:
        r2 = r8.flags;	 Catch:{ all -> 0x179b }
        r2 = r2 | 256;
        r8.flags = r2;	 Catch:{ all -> 0x179b }
        r2 = r21;
        r8.from_id = r2;	 Catch:{ all -> 0x179b }
        if (r20 != 0) goto L_0x1760;
    L_0x175b:
        if (r6 == 0) goto L_0x175e;
    L_0x175d:
        goto L_0x1760;
    L_0x175e:
        r2 = 0;
        goto L_0x1761;
    L_0x1760:
        r2 = 1;
    L_0x1761:
        r8.mentioned = r2;	 Catch:{ all -> 0x179b }
        r2 = r19;
        r8.silent = r2;	 Catch:{ all -> 0x179b }
        r4 = r24;
        r8.from_scheduled = r4;	 Catch:{ all -> 0x179b }
        r2 = new org.telegram.messenger.MessageObject;	 Catch:{ all -> 0x179b }
        r19 = r2;
        r20 = r29;
        r21 = r8;
        r22 = r7;
        r23 = r30;
        r24 = r31;
        r25 = r1;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27);	 Catch:{ all -> 0x179b }
        r1 = new java.util.ArrayList;	 Catch:{ all -> 0x179b }
        r1.<init>();	 Catch:{ all -> 0x179b }
        r1.add(r2);	 Catch:{ all -> 0x179b }
        r2 = org.telegram.messenger.NotificationsController.getInstance(r29);	 Catch:{ all -> 0x179b }
        r3 = r35;
        r4 = r3.countDownLatch;	 Catch:{ all -> 0x17c1 }
        r5 = 1;
        r2.processNewMessages(r1, r5, r5, r4);	 Catch:{ all -> 0x17c1 }
        goto L_0x17b5;
    L_0x1793:
        r3 = r35;
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x17c1 }
        r1.countDown();	 Catch:{ all -> 0x17c1 }
        goto L_0x17b5;
    L_0x179b:
        r0 = move-exception;
        r3 = r35;
        goto L_0x17c2;
    L_0x179f:
        r0 = move-exception;
        r3 = r35;
        goto L_0x17d6;
    L_0x17a3:
        r3 = r35;
        r29 = r15;
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x17c1 }
        r1.countDown();	 Catch:{ all -> 0x17c1 }
        goto L_0x17b5;
    L_0x17ad:
        r0 = move-exception;
        r3 = r1;
        goto L_0x17d6;
    L_0x17b0:
        r3 = r1;
        r28 = r14;
        r29 = r15;
    L_0x17b5:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r29);	 Catch:{ all -> 0x17c1 }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r29);	 Catch:{ all -> 0x17c1 }
        r1.resumeNetworkMaybe();	 Catch:{ all -> 0x17c1 }
        goto L_0x18da;
    L_0x17c1:
        r0 = move-exception;
    L_0x17c2:
        r1 = r0;
        r14 = r28;
        r15 = r29;
        goto L_0x1886;
    L_0x17c9:
        r0 = move-exception;
        r3 = r1;
        r28 = r14;
        r29 = r15;
        r1 = r0;
        goto L_0x1886;
    L_0x17d2:
        r0 = move-exception;
        r3 = r1;
        r28 = r7;
    L_0x17d6:
        r29 = r15;
        goto L_0x1883;
    L_0x17da:
        r3 = r1;
        r28 = r7;
        r29 = r15;
        r1 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ;	 Catch:{ all -> 0x17f0 }
        r1.<init>(r15);	 Catch:{ all -> 0x17ed }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);	 Catch:{ all -> 0x187d }
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x187d }
        r1.countDown();	 Catch:{ all -> 0x187d }
        return;
    L_0x17ed:
        r0 = move-exception;
        goto L_0x1883;
    L_0x17f0:
        r0 = move-exception;
        r15 = r29;
        goto L_0x1883;
    L_0x17f5:
        r3 = r1;
        r28 = r7;
        r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification;	 Catch:{ all -> 0x187d }
        r1.<init>();	 Catch:{ all -> 0x187d }
        r2 = 0;
        r1.popup = r2;	 Catch:{ all -> 0x187d }
        r2 = 2;
        r1.flags = r2;	 Catch:{ all -> 0x187d }
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6 = r37 / r6;
        r2 = (int) r6;	 Catch:{ all -> 0x187d }
        r1.inbox_date = r2;	 Catch:{ all -> 0x187d }
        r2 = "message";
        r2 = r5.getString(r2);	 Catch:{ all -> 0x187d }
        r1.message = r2;	 Catch:{ all -> 0x187d }
        r2 = "announcement";
        r1.type = r2;	 Catch:{ all -> 0x187d }
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;	 Catch:{ all -> 0x187d }
        r2.<init>();	 Catch:{ all -> 0x187d }
        r1.media = r2;	 Catch:{ all -> 0x187d }
        r2 = new org.telegram.tgnet.TLRPC$TL_updates;	 Catch:{ all -> 0x187d }
        r2.<init>();	 Catch:{ all -> 0x187d }
        r4 = r2.updates;	 Catch:{ all -> 0x187d }
        r4.add(r1);	 Catch:{ all -> 0x187d }
        r1 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ all -> 0x187d }
        r4 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo;	 Catch:{ all -> 0x183e }
        r4.<init>(r15, r2);	 Catch:{ all -> 0x183e }
        r1.postRunnable(r4);	 Catch:{ all -> 0x187d }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ all -> 0x187d }
        r1.resumeNetworkMaybe();	 Catch:{ all -> 0x187d }
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x187d }
        r1.countDown();	 Catch:{ all -> 0x187d }
        return;
    L_0x183e:
        r0 = move-exception;
        goto L_0x1883;
    L_0x1840:
        r3 = r1;
        r28 = r7;
        r1 = "dc";
        r1 = r11.getInt(r1);	 Catch:{ all -> 0x187d }
        r2 = "addr";
        r2 = r11.getString(r2);	 Catch:{ all -> 0x187d }
        r4 = ":";
        r2 = r2.split(r4);	 Catch:{ all -> 0x187d }
        r4 = r2.length;	 Catch:{ all -> 0x187d }
        r5 = 2;
        if (r4 == r5) goto L_0x185f;
    L_0x1859:
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x187d }
        r1.countDown();	 Catch:{ all -> 0x187d }
        return;
    L_0x185f:
        r4 = 0;
        r4 = r2[r4];	 Catch:{ all -> 0x187d }
        r5 = 1;
        r2 = r2[r5];	 Catch:{ all -> 0x187d }
        r2 = java.lang.Integer.parseInt(r2);	 Catch:{ all -> 0x187d }
        r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ all -> 0x187d }
        r5.applyDatacenterAddress(r1, r4, r2);	 Catch:{ all -> 0x187d }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ all -> 0x187d }
        r1.resumeNetworkMaybe();	 Catch:{ all -> 0x187d }
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x187d }
        r1.countDown();	 Catch:{ all -> 0x187d }
        return;
    L_0x187d:
        r0 = move-exception;
        goto L_0x1883;
    L_0x187f:
        r0 = move-exception;
        r3 = r1;
        r28 = r7;
    L_0x1883:
        r1 = r0;
        r14 = r28;
    L_0x1886:
        r2 = -1;
        goto L_0x18a2;
    L_0x1888:
        r0 = move-exception;
        r3 = r1;
        r28 = r7;
        r1 = r0;
        r14 = r28;
        r2 = -1;
        goto L_0x18a1;
    L_0x1891:
        r0 = move-exception;
        r3 = r1;
        r28 = r7;
        r1 = r0;
        r14 = r28;
        r2 = -1;
        r9 = 0;
        goto L_0x18a1;
    L_0x189b:
        r0 = move-exception;
        r3 = r1;
        r1 = r0;
        r2 = -1;
        r9 = 0;
        r14 = 0;
    L_0x18a1:
        r15 = -1;
    L_0x18a2:
        if (r15 == r2) goto L_0x18b4;
    L_0x18a4:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15);
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);
        r2.resumeNetworkMaybe();
        r2 = r3.countDownLatch;
        r2.countDown();
        goto L_0x18b7;
    L_0x18b4:
        r35.onDecryptError();
    L_0x18b7:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x18d7;
    L_0x18bb:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "error in loc_key = ";
        r2.append(r4);
        r2.append(r9);
        r4 = " json ";
        r2.append(r4);
        r2.append(r14);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.e(r2);
    L_0x18d7:
        org.telegram.messenger.FileLog.e(r1);
    L_0x18da:
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
