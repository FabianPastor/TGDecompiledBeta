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

    /* JADX WARNING: Removed duplicated region for block: B:290:0x0497  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x046e  */
    /* JADX WARNING: Removed duplicated region for block: B:586:0x0920 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:583:0x0915 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x090a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:577:0x08ff A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:574:0x08f4 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:571:0x08e9 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x08de A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x08d3 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:562:0x08c8 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:559:0x08bc A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:556:0x08b0 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:553:0x08a4 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:550:0x0898 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:547:0x088d A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:544:0x0881 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0876 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x086a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:535:0x085e A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:532:0x0852 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0846 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:526:0x083a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x082e A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x0822 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x0817 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:514:0x080c A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:511:0x0800 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x07f4 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x07e8 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x07dc A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:499:0x07d0 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x07c4 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x07b8 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:490:0x07ac A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x07a0 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:484:0x0794 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x0788 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:478:0x077c A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:475:0x0770 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x0764 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:469:0x0758 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x074c A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:463:0x0740 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x0734 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x0729 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x071d A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0711 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0705 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:445:0x06f9 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x06ed A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:439:0x06e1 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x06d5 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x06c9 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x06bd A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x06b1 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x06a5 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0699 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:418:0x068e A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0682 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:412:0x0676 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x066a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x065e A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0652 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0646 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:397:0x063a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x062e A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0622 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0616 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x060a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:382:0x05fe A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x05f2 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x05e6 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x05db A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:370:0x05cf A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x05c3 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x05b7 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x05ab A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:358:0x059f A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x0593 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:352:0x0587 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x057b A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x056f A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x0564 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:340:0x0558 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x054c A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x0540 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x0534 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:328:0x0528 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x051c A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x0510 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x0504 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x04f8 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x04ec A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x04e0 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x04d4 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x04c8 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x04bc A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x04b0 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x04a4 A:{SYNTHETIC, Splitter:B:294:0x04a4} */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x0424  */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x03fa A:{SYNTHETIC, Splitter:B:252:0x03fa} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0447 A:{SYNTHETIC, Splitter:B:273:0x0447} */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x043a  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x046e  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x0497  */
    /* JADX WARNING: Removed duplicated region for block: B:586:0x0920 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:583:0x0915 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x090a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:577:0x08ff A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:574:0x08f4 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:571:0x08e9 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x08de A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x08d3 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:562:0x08c8 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:559:0x08bc A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:556:0x08b0 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:553:0x08a4 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:550:0x0898 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:547:0x088d A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:544:0x0881 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0876 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x086a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:535:0x085e A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:532:0x0852 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0846 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:526:0x083a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x082e A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x0822 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x0817 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:514:0x080c A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:511:0x0800 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x07f4 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x07e8 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x07dc A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:499:0x07d0 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x07c4 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x07b8 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:490:0x07ac A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x07a0 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:484:0x0794 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x0788 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:478:0x077c A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:475:0x0770 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x0764 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:469:0x0758 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x074c A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:463:0x0740 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x0734 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x0729 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x071d A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0711 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0705 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:445:0x06f9 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x06ed A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:439:0x06e1 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x06d5 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x06c9 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x06bd A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x06b1 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x06a5 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0699 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:418:0x068e A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0682 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:412:0x0676 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x066a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x065e A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0652 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0646 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:397:0x063a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x062e A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0622 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0616 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x060a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:382:0x05fe A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x05f2 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x05e6 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x05db A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:370:0x05cf A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x05c3 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x05b7 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x05ab A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:358:0x059f A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x0593 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:352:0x0587 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x057b A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x056f A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x0564 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:340:0x0558 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x054c A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x0540 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x0534 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:328:0x0528 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x051c A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x0510 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x0504 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x04f8 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x04ec A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x04e0 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x04d4 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x04c8 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x04bc A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x04b0 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x04a4 A:{SYNTHETIC, Splitter:B:294:0x04a4} */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x03e3 A:{SYNTHETIC, Splitter:B:244:0x03e3} */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x03fa A:{SYNTHETIC, Splitter:B:252:0x03fa} */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x0424  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x043a  */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0447 A:{SYNTHETIC, Splitter:B:273:0x0447} */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x0497  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x046e  */
    /* JADX WARNING: Removed duplicated region for block: B:586:0x0920 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:583:0x0915 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x090a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:577:0x08ff A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:574:0x08f4 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:571:0x08e9 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x08de A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x08d3 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:562:0x08c8 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:559:0x08bc A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:556:0x08b0 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:553:0x08a4 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:550:0x0898 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:547:0x088d A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:544:0x0881 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0876 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x086a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:535:0x085e A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:532:0x0852 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0846 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:526:0x083a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x082e A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x0822 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x0817 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:514:0x080c A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:511:0x0800 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x07f4 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x07e8 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x07dc A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:499:0x07d0 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x07c4 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x07b8 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:490:0x07ac A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x07a0 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:484:0x0794 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x0788 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:478:0x077c A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:475:0x0770 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x0764 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:469:0x0758 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x074c A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:463:0x0740 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x0734 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x0729 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x071d A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0711 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0705 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:445:0x06f9 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x06ed A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:439:0x06e1 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x06d5 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x06c9 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x06bd A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x06b1 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x06a5 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0699 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:418:0x068e A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0682 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:412:0x0676 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x066a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x065e A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0652 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0646 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:397:0x063a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x062e A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0622 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0616 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x060a A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:382:0x05fe A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x05f2 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x05e6 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x05db A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:370:0x05cf A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x05c3 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x05b7 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x05ab A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:358:0x059f A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x0593 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:352:0x0587 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x057b A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x056f A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x0564 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:340:0x0558 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x054c A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x0540 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x0534 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:328:0x0528 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x051c A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x0510 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x0504 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x04f8 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x04ec A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x04e0 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x04d4 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x04c8 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x04bc A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x04b0 A:{Catch:{ all -> 0x0419 }} */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x04a4 A:{SYNTHETIC, Splitter:B:294:0x04a4} */
    /* JADX WARNING: Removed duplicated region for block: B:796:0x1701 A:{Catch:{ all -> 0x16f9, all -> 0x171c }} */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x03b8 A:{SYNTHETIC, Splitter:B:227:0x03b8} */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x03b8 A:{SYNTHETIC, Splitter:B:227:0x03b8} */
    /* JADX WARNING: Removed duplicated region for block: B:796:0x1701 A:{Catch:{ all -> 0x16f9, all -> 0x171c }} */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x0266 A:{SYNTHETIC, Splitter:B:168:0x0266} */
    /* JADX WARNING: Removed duplicated region for block: B:826:0x1796 A:{Catch:{ all -> 0x1743, all -> 0x17d3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x01e1  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x01e1  */
    /* JADX WARNING: Removed duplicated region for block: B:826:0x1796 A:{Catch:{ all -> 0x1743, all -> 0x17d3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:826:0x1796 A:{Catch:{ all -> 0x1743, all -> 0x17d3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x01e1  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x180a  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x17fa  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1811  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x17fa  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x180a  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1811  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x180a  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x17fa  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1811  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x17fa  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x180a  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1811  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x180a  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x17fa  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1811  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x17fa  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x180a  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1811  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x180a  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x17fa  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1811  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x17fa  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x180a  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1811  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x180a  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x17fa  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1811  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x17fa  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x180a  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1811  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x180a  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x17fa  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1811  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x17fa  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x180a  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1811  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x180a  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x17fa  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1811  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x17fa  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x180a  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1811  */
    /* JADX WARNING: Missing block: B:223:0x03b1, code skipped:
            if (org.telegram.messenger.MessagesStorage.getInstance(r15).checkMessageByRandomId(r3) == false) goto L_0x03b3;
     */
    /* JADX WARNING: Missing block: B:590:0x092c, code skipped:
            r14 = "Photos";
            r18 = r7;
            r7 = "ChannelMessageFew";
            r31 = r6;
            r6 = " ";
            r32 = r2;
            r2 = "AttachSticker";
     */
    /* JADX WARNING: Missing block: B:591:0x093a, code skipped:
            switch(r11) {
                case 0: goto L_0x161f;
                case 1: goto L_0x161f;
                case 2: goto L_0x15ff;
                case 3: goto L_0x15e2;
                case 4: goto L_0x15c5;
                case 5: goto L_0x15a8;
                case 6: goto L_0x158a;
                case 7: goto L_0x1574;
                case 8: goto L_0x1556;
                case 9: goto L_0x1538;
                case 10: goto L_0x14d8;
                case 11: goto L_0x14ba;
                case 12: goto L_0x1497;
                case 13: goto L_0x1474;
                case 14: goto L_0x1456;
                case 15: goto L_0x1438;
                case 16: goto L_0x141a;
                case 17: goto L_0x13f7;
                case 18: goto L_0x13d8;
                case 19: goto L_0x13d8;
                case 20: goto L_0x13b5;
                case 21: goto L_0x138f;
                case 22: goto L_0x1369;
                case 23: goto L_0x1341;
                case 24: goto L_0x132c;
                case 25: goto L_0x130e;
                case 26: goto L_0x12f0;
                case 27: goto L_0x12d2;
                case 28: goto L_0x12b4;
                case 29: goto L_0x1296;
                case 30: goto L_0x1239;
                case 31: goto L_0x121b;
                case 32: goto L_0x11f8;
                case 33: goto L_0x11d5;
                case 34: goto L_0x11b7;
                case 35: goto L_0x1199;
                case 36: goto L_0x117b;
                case 37: goto L_0x115d;
                case 38: goto L_0x1133;
                case 39: goto L_0x110f;
                case 40: goto L_0x10e9;
                case 41: goto L_0x10d4;
                case 42: goto L_0x10b3;
                case 43: goto L_0x1090;
                case 44: goto L_0x106d;
                case 45: goto L_0x104a;
                case 46: goto L_0x1027;
                case 47: goto L_0x1004;
                case 48: goto L_0x0var_;
                case 49: goto L_0x0var_;
                case 50: goto L_0x0var_;
                case 51: goto L_0x0var_;
                case 52: goto L_0x0eee;
                case 53: goto L_0x0ecb;
                case 54: goto L_0x0ea8;
                case 55: goto L_0x0e80;
                case 56: goto L_0x0e5c;
                case 57: goto L_0x0e34;
                case 58: goto L_0x0e1a;
                case 59: goto L_0x0e1a;
                case 60: goto L_0x0e00;
                case 61: goto L_0x0de6;
                case 62: goto L_0x0dc7;
                case 63: goto L_0x0dad;
                case 64: goto L_0x0d93;
                case 65: goto L_0x0d79;
                case 66: goto L_0x0d5f;
                case 67: goto L_0x0d45;
                case 68: goto L_0x0d1a;
                case 69: goto L_0x0cef;
                case 70: goto L_0x0cc2;
                case 71: goto L_0x0ca8;
                case 72: goto L_0x0c6f;
                case 73: goto L_0x0CLASSNAME;
                case 74: goto L_0x0CLASSNAME;
                case 75: goto L_0x0be2;
                case 76: goto L_0x0bb3;
                case 77: goto L_0x0b84;
                case 78: goto L_0x0b08;
                case 79: goto L_0x0ad9;
                case 80: goto L_0x0aa0;
                case 81: goto L_0x0a6b;
                case 82: goto L_0x0a3d;
                case 83: goto L_0x0a12;
                case 84: goto L_0x09e7;
                case 85: goto L_0x09ba;
                case 86: goto L_0x098d;
                case 87: goto L_0x0960;
                case 88: goto L_0x0947;
                case 89: goto L_0x0943;
                case 90: goto L_0x0943;
                case 91: goto L_0x0943;
                case 92: goto L_0x0943;
                case 93: goto L_0x0943;
                case 94: goto L_0x0943;
                case 95: goto L_0x0943;
                case 96: goto L_0x0943;
                case 97: goto L_0x0943;
                default: goto L_0x093d;
            };
     */
    /* JADX WARNING: Missing block: B:592:0x093d, code skipped:
            r11 = r18;
     */
    /* JADX WARNING: Missing block: B:595:0x0943, code skipped:
            r11 = r18;
     */
    /* JADX WARNING: Missing block: B:597:?, code skipped:
            r2 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
            r25 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
            r11 = r18;
     */
    /* JADX WARNING: Missing block: B:598:0x095d, code skipped:
            r6 = true;
     */
    /* JADX WARNING: Missing block: B:599:0x0960, code skipped:
            if (r8 == 0) goto L_0x097a;
     */
    /* JADX WARNING: Missing block: B:600:0x0962, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:601:0x097a, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:602:0x098d, code skipped:
            if (r8 == 0) goto L_0x09a7;
     */
    /* JADX WARNING: Missing block: B:603:0x098f, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:604:0x09a7, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:605:0x09ba, code skipped:
            if (r8 == 0) goto L_0x09d4;
     */
    /* JADX WARNING: Missing block: B:606:0x09bc, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:607:0x09d4, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:608:0x09e7, code skipped:
            if (r8 == 0) goto L_0x0a00;
     */
    /* JADX WARNING: Missing block: B:609:0x09e9, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:610:0x0a00, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:611:0x0a12, code skipped:
            if (r8 == 0) goto L_0x0a2b;
     */
    /* JADX WARNING: Missing block: B:612:0x0a14, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:613:0x0a2b, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:614:0x0a3d, code skipped:
            if (r8 == 0) goto L_0x0a56;
     */
    /* JADX WARNING: Missing block: B:615:0x0a3f, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:616:0x0a56, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:617:0x0a67, code skipped:
            r11 = r18;
     */
    /* JADX WARNING: Missing block: B:618:0x0a6b, code skipped:
            if (r8 == 0) goto L_0x0a89;
     */
    /* JADX WARNING: Missing block: B:619:0x0a6d, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:620:0x0a89, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:621:0x0aa0, code skipped:
            r11 = r18;
     */
    /* JADX WARNING: Missing block: B:622:0x0aa2, code skipped:
            if (r8 == 0) goto L_0x0ac1;
     */
    /* JADX WARNING: Missing block: B:623:0x0aa4, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:624:0x0ac1, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:625:0x0ad9, code skipped:
            r11 = r18;
     */
    /* JADX WARNING: Missing block: B:626:0x0adb, code skipped:
            if (r8 == 0) goto L_0x0af5;
     */
    /* JADX WARNING: Missing block: B:627:0x0add, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:628:0x0af5, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:629:0x0b08, code skipped:
            r11 = r18;
     */
    /* JADX WARNING: Missing block: B:630:0x0b0a, code skipped:
            if (r8 == 0) goto L_0x0b4d;
     */
    /* JADX WARNING: Missing block: B:632:0x0b0e, code skipped:
            if (r15.length <= 2) goto L_0x0b35;
     */
    /* JADX WARNING: Missing block: B:634:0x0b16, code skipped:
            if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x0b35;
     */
    /* JADX WARNING: Missing block: B:635:0x0b18, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:636:0x0b35, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:638:0x0b4f, code skipped:
            if (r15.length <= 1) goto L_0x0b71;
     */
    /* JADX WARNING: Missing block: B:640:0x0b57, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x0b71;
     */
    /* JADX WARNING: Missing block: B:641:0x0b59, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:642:0x0b71, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:643:0x0b84, code skipped:
            r11 = r18;
     */
    /* JADX WARNING: Missing block: B:644:0x0b86, code skipped:
            if (r8 == 0) goto L_0x0ba0;
     */
    /* JADX WARNING: Missing block: B:645:0x0b88, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:646:0x0ba0, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:647:0x0bb3, code skipped:
            r11 = r18;
     */
    /* JADX WARNING: Missing block: B:648:0x0bb5, code skipped:
            if (r8 == 0) goto L_0x0bcf;
     */
    /* JADX WARNING: Missing block: B:649:0x0bb7, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:650:0x0bcf, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:651:0x0be2, code skipped:
            r11 = r18;
     */
    /* JADX WARNING: Missing block: B:652:0x0be4, code skipped:
            if (r8 == 0) goto L_0x0bfe;
     */
    /* JADX WARNING: Missing block: B:653:0x0be6, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:654:0x0bfe, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:655:0x0CLASSNAME, code skipped:
            r11 = r18;
     */
    /* JADX WARNING: Missing block: B:656:0x0CLASSNAME, code skipped:
            if (r8 == 0) goto L_0x0c2d;
     */
    /* JADX WARNING: Missing block: B:657:0x0CLASSNAME, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:658:0x0c2d, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:659:0x0CLASSNAME, code skipped:
            r11 = r18;
     */
    /* JADX WARNING: Missing block: B:660:0x0CLASSNAME, code skipped:
            if (r8 == 0) goto L_0x0c5c;
     */
    /* JADX WARNING: Missing block: B:661:0x0CLASSNAME, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:662:0x0c5c, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:663:0x0c6f, code skipped:
            r11 = r18;
     */
    /* JADX WARNING: Missing block: B:664:0x0CLASSNAME, code skipped:
            if (r8 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Missing block: B:665:0x0CLASSNAME, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:666:0x0CLASSNAME, code skipped:
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:667:0x0ca8, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:668:0x0cc2, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:669:0x0cef, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString(r14, org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:670:0x0d1a, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString(r10, org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:671:0x0d45, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:672:0x0d5f, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:673:0x0d79, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:674:0x0d93, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:675:0x0dad, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:676:0x0dc7, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:677:0x0de6, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:678:0x0e00, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:679:0x0e1a, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:680:0x0e34, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r15[0], r15[1], r15[2]);
            r6 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Missing block: B:681:0x0e5c, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r15[0], r15[1], r15[2], r15[3]);
     */
    /* JADX WARNING: Missing block: B:682:0x0e80, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r15[0], r15[1], r15[2]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:683:0x0ea8, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r15[0], r15[1]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:684:0x0ecb, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r15[0], r15[1]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:685:0x0eee, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r15[0], r15[1]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:686:0x0var_, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r15[0], r15[1], r15[2]);
            r6 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:687:0x0var_, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r15[0], r15[1], r15[2]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:688:0x0var_, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r15[0], r15[1]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:689:0x0var_, code skipped:
            r11 = r18;
     */
    /* JADX WARNING: Missing block: B:690:0x0var_, code skipped:
            if (r15.length <= 2) goto L_0x0fcc;
     */
    /* JADX WARNING: Missing block: B:692:0x0var_, code skipped:
            if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x0fcc;
     */
    /* JADX WARNING: Missing block: B:693:0x0var_, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r15[0], r15[1], r15[2]);
            r10 = new java.lang.StringBuilder();
            r10.append(r15[2]);
            r10.append(r6);
            r10.append(org.telegram.messenger.LocaleController.getString(r2, NUM));
            r2 = r10.toString();
     */
    /* JADX WARNING: Missing block: B:694:0x0fcc, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r15[0], r15[1]);
            r10 = new java.lang.StringBuilder();
            r10.append(r15[1]);
            r10.append(r6);
            r10.append(org.telegram.messenger.LocaleController.getString(r2, NUM));
            r2 = r10.toString();
     */
    /* JADX WARNING: Missing block: B:695:0x0fff, code skipped:
            r16 = r2;
            r2 = r7;
     */
    /* JADX WARNING: Missing block: B:696:0x1004, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r15[0], r15[1]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:697:0x1027, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r15[0], r15[1]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:698:0x104a, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r15[0], r15[1]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:699:0x106d, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r15[0], r15[1]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:700:0x1090, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r15[0], r15[1]);
            r6 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:701:0x10b3, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r15[0], r15[1], r15[2]);
            r6 = r15[2];
     */
    /* JADX WARNING: Missing block: B:702:0x10d4, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:703:0x10e9, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString(r7, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:704:0x110f, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString(r7, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString(r14, org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:705:0x1133, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString(r7, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Missing block: B:706:0x115d, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:707:0x117b, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:708:0x1199, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:709:0x11b7, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:710:0x11d5, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r15[0], r15[1]);
            r6 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:711:0x11f8, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r15[0], r15[1]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:712:0x121b, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:713:0x1239, code skipped:
            r11 = r18;
     */
    /* JADX WARNING: Missing block: B:714:0x123d, code skipped:
            if (r15.length <= 1) goto L_0x127c;
     */
    /* JADX WARNING: Missing block: B:716:0x1245, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x127c;
     */
    /* JADX WARNING: Missing block: B:717:0x1247, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r15[0], r15[1]);
            r10 = new java.lang.StringBuilder();
            r10.append(r15[1]);
            r10.append(r6);
            r10.append(org.telegram.messenger.LocaleController.getString(r2, NUM));
            r2 = r10.toString();
     */
    /* JADX WARNING: Missing block: B:718:0x127c, code skipped:
            r6 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString(r2, NUM);
     */
    /* JADX WARNING: Missing block: B:719:0x1296, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:720:0x12b4, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:721:0x12d2, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:722:0x12f0, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:723:0x130e, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:724:0x132c, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:725:0x1341, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:726:0x1369, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString(r14, org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:727:0x138f, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString(r10, org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:728:0x13b5, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r15[0], r15[1]);
            r6 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Missing block: B:729:0x13d8, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:730:0x13f7, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r15[0], r15[1]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:731:0x141a, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:732:0x1438, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:733:0x1456, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:734:0x1474, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r15[0], r15[1]);
            r6 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:735:0x1497, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r15[0], r15[1]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:736:0x14ba, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:737:0x14d8, code skipped:
            r11 = r18;
     */
    /* JADX WARNING: Missing block: B:738:0x14dc, code skipped:
            if (r15.length <= 1) goto L_0x151b;
     */
    /* JADX WARNING: Missing block: B:740:0x14e4, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x151b;
     */
    /* JADX WARNING: Missing block: B:741:0x14e6, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r15[0], r15[1]);
            r10 = new java.lang.StringBuilder();
            r10.append(r15[1]);
            r10.append(r6);
            r10.append(org.telegram.messenger.LocaleController.getString(r2, NUM));
            r2 = r10.toString();
     */
    /* JADX WARNING: Missing block: B:742:0x151b, code skipped:
            r6 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r15[0]);
            r2 = org.telegram.messenger.LocaleController.getString(r2, NUM);
     */
    /* JADX WARNING: Missing block: B:743:0x1533, code skipped:
            r16 = r2;
            r2 = r6;
     */
    /* JADX WARNING: Missing block: B:744:0x1538, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:745:0x1556, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:746:0x1574, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r15[0]);
     */
    /* JADX WARNING: Missing block: B:747:0x158a, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:748:0x15a8, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:749:0x15c5, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:750:0x15e2, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:751:0x15ff, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r15[0]);
            r6 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:752:0x161b, code skipped:
            r16 = r6;
     */
    /* JADX WARNING: Missing block: B:753:0x161d, code skipped:
            r6 = false;
     */
    /* JADX WARNING: Missing block: B:754:0x161f, code skipped:
            r11 = r18;
            r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r15[0], r15[1]);
            r6 = r15[1];
     */
    /* JADX WARNING: Missing block: B:755:0x163a, code skipped:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1650;
     */
    /* JADX WARNING: Missing block: B:756:0x163c, code skipped:
            r2 = new java.lang.StringBuilder();
            r2.append("unhandled loc_key = ");
            r2.append(r9);
            org.telegram.messenger.FileLog.w(r2.toString());
     */
    /* JADX WARNING: Missing block: B:757:0x1650, code skipped:
            r2 = null;
     */
    /* JADX WARNING: Missing block: B:758:0x1651, code skipped:
            r6 = false;
     */
    /* JADX WARNING: Missing block: B:759:0x1652, code skipped:
            r16 = null;
     */
    /* JADX WARNING: Missing block: B:760:0x1654, code skipped:
            if (r2 == null) goto L_0x16f1;
     */
    /* JADX WARNING: Missing block: B:762:?, code skipped:
            r7 = new org.telegram.tgnet.TLRPC.TL_message();
            r7.id = r11;
            r7.random_id = r3;
     */
    /* JADX WARNING: Missing block: B:763:0x165f, code skipped:
            if (r16 == null) goto L_0x1664;
     */
    /* JADX WARNING: Missing block: B:764:0x1661, code skipped:
            r3 = r16;
     */
    /* JADX WARNING: Missing block: B:765:0x1664, code skipped:
            r3 = r2;
     */
    /* JADX WARNING: Missing block: B:766:0x1665, code skipped:
            r7.message = r3;
            r7.date = (int) (r37 / 1000);
     */
    /* JADX WARNING: Missing block: B:767:0x166e, code skipped:
            if (r5 == null) goto L_0x1677;
     */
    /* JADX WARNING: Missing block: B:769:?, code skipped:
            r7.action = new org.telegram.tgnet.TLRPC.TL_messageActionPinMessage();
     */
    /* JADX WARNING: Missing block: B:770:0x1677, code skipped:
            if (r1 == null) goto L_0x1680;
     */
    /* JADX WARNING: Missing block: B:771:0x1679, code skipped:
            r7.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Missing block: B:773:?, code skipped:
            r7.dialog_id = r12;
     */
    /* JADX WARNING: Missing block: B:774:0x1682, code skipped:
            if (r32 == 0) goto L_0x1692;
     */
    /* JADX WARNING: Missing block: B:776:?, code skipped:
            r7.to_id = new org.telegram.tgnet.TLRPC.TL_peerChannel();
            r7.to_id.channel_id = r32;
     */
    /* JADX WARNING: Missing block: B:777:0x1692, code skipped:
            if (r23 == 0) goto L_0x16a2;
     */
    /* JADX WARNING: Missing block: B:778:0x1694, code skipped:
            r7.to_id = new org.telegram.tgnet.TLRPC.TL_peerChat();
            r7.to_id.chat_id = r23;
     */
    /* JADX WARNING: Missing block: B:780:?, code skipped:
            r7.to_id = new org.telegram.tgnet.TLRPC.TL_peerUser();
            r7.to_id.user_id = r31;
     */
    /* JADX WARNING: Missing block: B:781:0x16af, code skipped:
            r7.flags |= 256;
            r7.from_id = r8;
     */
    /* JADX WARNING: Missing block: B:782:0x16b7, code skipped:
            if (r20 != null) goto L_0x16be;
     */
    /* JADX WARNING: Missing block: B:783:0x16b9, code skipped:
            if (r5 == null) goto L_0x16bc;
     */
    /* JADX WARNING: Missing block: B:785:0x16bc, code skipped:
            r1 = false;
     */
    /* JADX WARNING: Missing block: B:786:0x16be, code skipped:
            r1 = true;
     */
    /* JADX WARNING: Missing block: B:787:0x16bf, code skipped:
            r7.mentioned = r1;
            r7.silent = r19;
            r7.from_scheduled = r24;
            r19 = new org.telegram.messenger.MessageObject(r29, r7, r2, r25, r30, r6, r26, r27);
            r2 = new java.util.ArrayList();
            r2.add(r19);
     */
    /* JADX WARNING: Missing block: B:788:0x16e8, code skipped:
            r3 = r35;
     */
    /* JADX WARNING: Missing block: B:790:?, code skipped:
            org.telegram.messenger.NotificationsController.getInstance(r29).processNewMessages(r2, true, true, r3.countDownLatch);
     */
    /* JADX WARNING: Missing block: B:791:0x16f1, code skipped:
            r35.countDownLatch.countDown();
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
        r5 = r2.get(r5);	 Catch:{ all -> 0x17f1 }
        r6 = r5 instanceof java.lang.String;	 Catch:{ all -> 0x17f1 }
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
        goto L_0x17f8;
    L_0x002d:
        r5 = (java.lang.String) r5;	 Catch:{ all -> 0x17f1 }
        r6 = 8;
        r5 = android.util.Base64.decode(r5, r6);	 Catch:{ all -> 0x17f1 }
        r7 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ all -> 0x17f1 }
        r8 = r5.length;	 Catch:{ all -> 0x17f1 }
        r7.<init>(r8);	 Catch:{ all -> 0x17f1 }
        r7.writeBytes(r5);	 Catch:{ all -> 0x17f1 }
        r8 = 0;
        r7.position(r8);	 Catch:{ all -> 0x17f1 }
        r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ all -> 0x17f1 }
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
        r9 = new byte[r6];	 Catch:{ all -> 0x17f1 }
        r10 = 1;
        r7.readBytes(r9, r10);	 Catch:{ all -> 0x17f1 }
        r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ all -> 0x17f1 }
        r11 = java.util.Arrays.equals(r11, r9);	 Catch:{ all -> 0x17f1 }
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
        r9 = new byte[r9];	 Catch:{ all -> 0x17f1 }
        r7.readBytes(r9, r10);	 Catch:{ all -> 0x17f1 }
        r11 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ all -> 0x17f1 }
        r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13);	 Catch:{ all -> 0x17f1 }
        r14 = r7.buffer;	 Catch:{ all -> 0x17f1 }
        r15 = r11.aesKey;	 Catch:{ all -> 0x17f1 }
        r11 = r11.aesIv;	 Catch:{ all -> 0x17f1 }
        r17 = 0;
        r18 = 0;
        r19 = 24;
        r5 = r5.length;	 Catch:{ all -> 0x17f1 }
        r20 = r5 + -24;
        r16 = r11;
        org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20);	 Catch:{ all -> 0x17f1 }
        r21 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ all -> 0x17f1 }
        r22 = 96;
        r23 = 32;
        r5 = r7.buffer;	 Catch:{ all -> 0x17f1 }
        r25 = 24;
        r11 = r7.buffer;	 Catch:{ all -> 0x17f1 }
        r26 = r11.limit();	 Catch:{ all -> 0x17f1 }
        r24 = r5;
        r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26);	 Catch:{ all -> 0x17f1 }
        r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6);	 Catch:{ all -> 0x17f1 }
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
        r5 = r7.readInt32(r10);	 Catch:{ all -> 0x17f1 }
        r5 = new byte[r5];	 Catch:{ all -> 0x17f1 }
        r7.readBytes(r5, r10);	 Catch:{ all -> 0x17f1 }
        r7 = new java.lang.String;	 Catch:{ all -> 0x17f1 }
        r7.<init>(r5);	 Catch:{ all -> 0x17f1 }
        r5 = new org.json.JSONObject;	 Catch:{ all -> 0x17e7 }
        r5.<init>(r7);	 Catch:{ all -> 0x17e7 }
        r9 = "loc_key";
        r9 = r5.has(r9);	 Catch:{ all -> 0x17e7 }
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
        r11 = r5.get(r11);	 Catch:{ all -> 0x17de }
        r11 = r11 instanceof org.json.JSONObject;	 Catch:{ all -> 0x17de }
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
        r11 = new org.json.JSONObject;	 Catch:{ all -> 0x17de }
        r11.<init>();	 Catch:{ all -> 0x17de }
    L_0x0131:
        r14 = "user_id";
        r14 = r5.has(r14);	 Catch:{ all -> 0x17de }
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
        r15 = r14 instanceof java.lang.Integer;	 Catch:{ all -> 0x17de }
        if (r15 == 0) goto L_0x0159;
    L_0x0152:
        r14 = (java.lang.Integer) r14;	 Catch:{ all -> 0x0126 }
        r14 = r14.intValue();	 Catch:{ all -> 0x0126 }
        goto L_0x0172;
    L_0x0159:
        r15 = r14 instanceof java.lang.String;	 Catch:{ all -> 0x17de }
        if (r15 == 0) goto L_0x0168;
    L_0x015d:
        r14 = (java.lang.String) r14;	 Catch:{ all -> 0x0126 }
        r14 = org.telegram.messenger.Utilities.parseInt(r14);	 Catch:{ all -> 0x0126 }
        r14 = r14.intValue();	 Catch:{ all -> 0x0126 }
        goto L_0x0172;
    L_0x0168:
        r14 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ all -> 0x17de }
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);	 Catch:{ all -> 0x17de }
        r14 = r14.getClientUserId();	 Catch:{ all -> 0x17de }
    L_0x0172:
        r15 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ all -> 0x17de }
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
        r4 = org.telegram.messenger.UserConfig.getInstance(r15);	 Catch:{ all -> 0x17d5 }
        r4 = r4.isClientActivated();	 Catch:{ all -> 0x17d5 }
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
        r2.get(r4);	 Catch:{ all -> 0x17d5 }
        r2 = r9.hashCode();	 Catch:{ all -> 0x17d5 }
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
        r2 = r9.equals(r2);	 Catch:{ all -> 0x17d5 }
        if (r2 == 0) goto L_0x01de;
    L_0x01dc:
        r2 = 2;
        goto L_0x01df;
    L_0x01de:
        r2 = -1;
    L_0x01df:
        if (r2 == 0) goto L_0x1796;
    L_0x01e1:
        if (r2 == r10) goto L_0x174b;
    L_0x01e3:
        if (r2 == r13) goto L_0x1735;
    L_0x01e5:
        r2 = "channel_id";
        r2 = r11.has(r2);	 Catch:{ all -> 0x172d }
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
        r14 = r11.has(r14);	 Catch:{ all -> 0x172d }
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
        r7 = r11.has(r7);	 Catch:{ all -> 0x1724 }
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
        r4 = r11.has(r4);	 Catch:{ all -> 0x1724 }
        if (r4 == 0) goto L_0x023e;
    L_0x0234:
        r4 = "encryption_id";
        r4 = r11.getInt(r4);	 Catch:{ all -> 0x0228 }
        r12 = (long) r4;
        r4 = 32;
        r12 = r12 << r4;
    L_0x023e:
        r4 = "schedule";
        r4 = r11.has(r4);	 Catch:{ all -> 0x1724 }
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
        if (r7 == 0) goto L_0x170b;
    L_0x0266:
        r7 = "READ_HISTORY";
        r7 = r7.equals(r9);	 Catch:{ all -> 0x1724 }
        if (r7 == 0) goto L_0x02e3;
    L_0x026e:
        r4 = "max_id";
        r4 = r11.getInt(r4);	 Catch:{ all -> 0x0228 }
        r5 = new java.util.ArrayList;	 Catch:{ all -> 0x0228 }
        r5.<init>();	 Catch:{ all -> 0x0228 }
        r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x0228 }
        if (r7 == 0) goto L_0x0299;
    L_0x027d:
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0228 }
        r7.<init>();	 Catch:{ all -> 0x0228 }
        r8 = "GCM received read notification max_id = ";
        r7.append(r8);	 Catch:{ all -> 0x0228 }
        r7.append(r4);	 Catch:{ all -> 0x0228 }
        r8 = " for dialogId = ";
        r7.append(r8);	 Catch:{ all -> 0x0228 }
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
        goto L_0x170b;
    L_0x02e3:
        r7 = "MESSAGE_DELETED";
        r7 = r7.equals(r9);	 Catch:{ all -> 0x1724 }
        r10 = "messages";
        if (r7 == 0) goto L_0x0323;
    L_0x02ed:
        r3 = r11.getString(r10);	 Catch:{ all -> 0x0228 }
        r4 = ",";
        r3 = r3.split(r4);	 Catch:{ all -> 0x0228 }
        r4 = new android.util.SparseArray;	 Catch:{ all -> 0x0228 }
        r4.<init>();	 Catch:{ all -> 0x0228 }
        r5 = new java.util.ArrayList;	 Catch:{ all -> 0x0228 }
        r5.<init>();	 Catch:{ all -> 0x0228 }
    L_0x0301:
        r6 = r3.length;	 Catch:{ all -> 0x0228 }
        if (r8 >= r6) goto L_0x0310;
    L_0x0304:
        r6 = r3[r8];	 Catch:{ all -> 0x0228 }
        r6 = org.telegram.messenger.Utilities.parseInt(r6);	 Catch:{ all -> 0x0228 }
        r5.add(r6);	 Catch:{ all -> 0x0228 }
        r8 = r8 + 1;
        goto L_0x0301;
    L_0x0310:
        r4.put(r2, r5);	 Catch:{ all -> 0x0228 }
        r3 = org.telegram.messenger.NotificationsController.getInstance(r15);	 Catch:{ all -> 0x0228 }
        r3.removeDeletedMessagesFromNotifications(r4);	 Catch:{ all -> 0x0228 }
        r3 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ all -> 0x0228 }
        r3.deleteMessagesByPush(r12, r5, r2);	 Catch:{ all -> 0x0228 }
        goto L_0x170b;
    L_0x0323:
        r7 = android.text.TextUtils.isEmpty(r9);	 Catch:{ all -> 0x1724 }
        if (r7 != 0) goto L_0x170b;
    L_0x0329:
        r7 = "msg_id";
        r7 = r11.has(r7);	 Catch:{ all -> 0x1724 }
        if (r7 == 0) goto L_0x0338;
    L_0x0331:
        r7 = "msg_id";
        r7 = r11.getInt(r7);	 Catch:{ all -> 0x0228 }
        goto L_0x0339;
    L_0x0338:
        r7 = 0;
    L_0x0339:
        r8 = "random_id";
        r8 = r11.has(r8);	 Catch:{ all -> 0x1724 }
        if (r8 == 0) goto L_0x0357;
    L_0x0341:
        r8 = "random_id";
        r8 = r11.getString(r8);	 Catch:{ all -> 0x0228 }
        r8 = org.telegram.messenger.Utilities.parseLong(r8);	 Catch:{ all -> 0x0228 }
        r23 = r8.longValue();	 Catch:{ all -> 0x0228 }
        r8 = r4;
        r33 = r23;
        r23 = r3;
        r3 = r33;
        goto L_0x035c;
    L_0x0357:
        r23 = r3;
        r8 = r4;
        r3 = r19;
    L_0x035c:
        if (r7 == 0) goto L_0x03a1;
    L_0x035e:
        r28 = r14;
        r14 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ all -> 0x0398 }
        r14 = r14.dialogs_read_inbox_max;	 Catch:{ all -> 0x0398 }
        r1 = java.lang.Long.valueOf(r12);	 Catch:{ all -> 0x0398 }
        r1 = r14.get(r1);	 Catch:{ all -> 0x0398 }
        r1 = (java.lang.Integer) r1;	 Catch:{ all -> 0x0398 }
        if (r1 != 0) goto L_0x038f;
    L_0x0372:
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r15);	 Catch:{ all -> 0x0398 }
        r14 = 0;
        r1 = r1.getDialogReadMax(r14, r12);	 Catch:{ all -> 0x0398 }
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ all -> 0x0398 }
        r14 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ all -> 0x0398 }
        r14 = r14.dialogs_read_inbox_max;	 Catch:{ all -> 0x0398 }
        r24 = r8;
        r8 = java.lang.Long.valueOf(r12);	 Catch:{ all -> 0x0398 }
        r14.put(r8, r1);	 Catch:{ all -> 0x0398 }
        goto L_0x0391;
    L_0x038f:
        r24 = r8;
    L_0x0391:
        r1 = r1.intValue();	 Catch:{ all -> 0x0398 }
        if (r7 <= r1) goto L_0x03b5;
    L_0x0397:
        goto L_0x03b3;
    L_0x0398:
        r0 = move-exception;
        r2 = -1;
        r3 = r35;
        r1 = r0;
        r14 = r28;
        goto L_0x17f8;
    L_0x03a1:
        r24 = r8;
        r28 = r14;
        r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1));
        if (r1 == 0) goto L_0x03b5;
    L_0x03a9:
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r15);	 Catch:{ all -> 0x0398 }
        r1 = r1.checkMessageByRandomId(r3);	 Catch:{ all -> 0x0398 }
        if (r1 != 0) goto L_0x03b5;
    L_0x03b3:
        r8 = 1;
        goto L_0x03b6;
    L_0x03b5:
        r8 = 0;
    L_0x03b6:
        if (r8 == 0) goto L_0x1701;
    L_0x03b8:
        r1 = "chat_from_id";
        r1 = r11.has(r1);	 Catch:{ all -> 0x16fd }
        if (r1 == 0) goto L_0x03c7;
    L_0x03c0:
        r1 = "chat_from_id";
        r8 = r11.getInt(r1);	 Catch:{ all -> 0x0398 }
        goto L_0x03c8;
    L_0x03c7:
        r8 = 0;
    L_0x03c8:
        r1 = "mention";
        r1 = r11.has(r1);	 Catch:{ all -> 0x16fd }
        if (r1 == 0) goto L_0x03da;
    L_0x03d0:
        r1 = "mention";
        r1 = r11.getInt(r1);	 Catch:{ all -> 0x0398 }
        if (r1 == 0) goto L_0x03da;
    L_0x03d8:
        r1 = 1;
        goto L_0x03db;
    L_0x03da:
        r1 = 0;
    L_0x03db:
        r14 = "silent";
        r14 = r11.has(r14);	 Catch:{ all -> 0x16fd }
        if (r14 == 0) goto L_0x03ef;
    L_0x03e3:
        r14 = "silent";
        r14 = r11.getInt(r14);	 Catch:{ all -> 0x0398 }
        if (r14 == 0) goto L_0x03ef;
    L_0x03eb:
        r29 = r15;
        r14 = 1;
        goto L_0x03f2;
    L_0x03ef:
        r29 = r15;
        r14 = 0;
    L_0x03f2:
        r15 = "loc_args";
        r15 = r5.has(r15);	 Catch:{ all -> 0x16f9 }
        if (r15 == 0) goto L_0x0424;
    L_0x03fa:
        r15 = "loc_args";
        r5 = r5.getJSONArray(r15);	 Catch:{ all -> 0x0419 }
        r15 = r5.length();	 Catch:{ all -> 0x0419 }
        r15 = new java.lang.String[r15];	 Catch:{ all -> 0x0419 }
        r20 = r1;
        r19 = r14;
        r14 = 0;
    L_0x040b:
        r1 = r15.length;	 Catch:{ all -> 0x0419 }
        if (r14 >= r1) goto L_0x0417;
    L_0x040e:
        r1 = r5.getString(r14);	 Catch:{ all -> 0x0419 }
        r15[r14] = r1;	 Catch:{ all -> 0x0419 }
        r14 = r14 + 1;
        goto L_0x040b;
    L_0x0417:
        r1 = 0;
        goto L_0x042a;
    L_0x0419:
        r0 = move-exception;
        r2 = -1;
        r3 = r35;
        r1 = r0;
        r14 = r28;
        r15 = r29;
        goto L_0x17f8;
    L_0x0424:
        r20 = r1;
        r19 = r14;
        r1 = 0;
        r15 = 0;
    L_0x042a:
        r5 = r15[r1];	 Catch:{ all -> 0x16f9 }
        r1 = "edit_date";
        r27 = r11.has(r1);	 Catch:{ all -> 0x16f9 }
        r1 = "CHAT_";
        r1 = r9.startsWith(r1);	 Catch:{ all -> 0x16f9 }
        if (r1 == 0) goto L_0x0447;
    L_0x043a:
        if (r2 == 0) goto L_0x043e;
    L_0x043c:
        r1 = 1;
        goto L_0x043f;
    L_0x043e:
        r1 = 0;
    L_0x043f:
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x0419 }
        r11 = r5;
        r5 = 0;
    L_0x0444:
        r26 = 0;
        goto L_0x046a;
    L_0x0447:
        r1 = "PINNED_";
        r1 = r9.startsWith(r1);	 Catch:{ all -> 0x16f9 }
        if (r1 == 0) goto L_0x0457;
    L_0x044f:
        if (r8 == 0) goto L_0x0453;
    L_0x0451:
        r1 = 1;
        goto L_0x0454;
    L_0x0453:
        r1 = 0;
    L_0x0454:
        r14 = r5;
        r5 = 1;
        goto L_0x0468;
    L_0x0457:
        r1 = "CHANNEL_";
        r1 = r9.startsWith(r1);	 Catch:{ all -> 0x16f9 }
        r14 = r5;
        if (r1 == 0) goto L_0x0466;
    L_0x0460:
        r1 = 0;
        r5 = 0;
        r11 = 0;
        r26 = 1;
        goto L_0x046a;
    L_0x0466:
        r1 = 0;
        r5 = 0;
    L_0x0468:
        r11 = 0;
        goto L_0x0444;
    L_0x046a:
        r25 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x16f9 }
        if (r25 == 0) goto L_0x0497;
    L_0x046e:
        r25 = r14;
        r14 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0419 }
        r14.<init>();	 Catch:{ all -> 0x0419 }
        r30 = r11;
        r11 = "GCM received message notification ";
        r14.append(r11);	 Catch:{ all -> 0x0419 }
        r14.append(r9);	 Catch:{ all -> 0x0419 }
        r11 = " for dialogId = ";
        r14.append(r11);	 Catch:{ all -> 0x0419 }
        r14.append(r12);	 Catch:{ all -> 0x0419 }
        r11 = " mid = ";
        r14.append(r11);	 Catch:{ all -> 0x0419 }
        r14.append(r7);	 Catch:{ all -> 0x0419 }
        r11 = r14.toString();	 Catch:{ all -> 0x0419 }
        org.telegram.messenger.FileLog.d(r11);	 Catch:{ all -> 0x0419 }
        goto L_0x049b;
    L_0x0497:
        r30 = r11;
        r25 = r14;
    L_0x049b:
        r11 = r9.hashCode();	 Catch:{ all -> 0x16f9 }
        switch(r11) {
            case -2100047043: goto L_0x0920;
            case -2091498420: goto L_0x0915;
            case -2053872415: goto L_0x090a;
            case -2039746363: goto L_0x08ff;
            case -2023218804: goto L_0x08f4;
            case -1979538588: goto L_0x08e9;
            case -1979536003: goto L_0x08de;
            case -1979535888: goto L_0x08d3;
            case -1969004705: goto L_0x08c8;
            case -1946699248: goto L_0x08bc;
            case -1528047021: goto L_0x08b0;
            case -1493579426: goto L_0x08a4;
            case -1482481933: goto L_0x0898;
            case -1480102982: goto L_0x088d;
            case -1478041834: goto L_0x0881;
            case -1474543101: goto L_0x0876;
            case -1465695932: goto L_0x086a;
            case -1374906292: goto L_0x085e;
            case -1372940586: goto L_0x0852;
            case -1264245338: goto L_0x0846;
            case -1236086700: goto L_0x083a;
            case -1236077786: goto L_0x082e;
            case -1235796237: goto L_0x0822;
            case -1235686303: goto L_0x0817;
            case -1198046100: goto L_0x080c;
            case -1124254527: goto L_0x0800;
            case -1085137927: goto L_0x07f4;
            case -1084856378: goto L_0x07e8;
            case -1084746444: goto L_0x07dc;
            case -819729482: goto L_0x07d0;
            case -772141857: goto L_0x07c4;
            case -638310039: goto L_0x07b8;
            case -590403924: goto L_0x07ac;
            case -589196239: goto L_0x07a0;
            case -589193654: goto L_0x0794;
            case -589193539: goto L_0x0788;
            case -440169325: goto L_0x077c;
            case -412748110: goto L_0x0770;
            case -228518075: goto L_0x0764;
            case -213586509: goto L_0x0758;
            case -115582002: goto L_0x074c;
            case -112621464: goto L_0x0740;
            case -108522133: goto L_0x0734;
            case -107572034: goto L_0x0729;
            case -40534265: goto L_0x071d;
            case 65254746: goto L_0x0711;
            case 141040782: goto L_0x0705;
            case 309993049: goto L_0x06f9;
            case 309995634: goto L_0x06ed;
            case 309995749: goto L_0x06e1;
            case 320532812: goto L_0x06d5;
            case 328933854: goto L_0x06c9;
            case 331340546: goto L_0x06bd;
            case 344816990: goto L_0x06b1;
            case 346878138: goto L_0x06a5;
            case 350376871: goto L_0x0699;
            case 615714517: goto L_0x068e;
            case 715508879: goto L_0x0682;
            case 728985323: goto L_0x0676;
            case 731046471: goto L_0x066a;
            case 734545204: goto L_0x065e;
            case 802032552: goto L_0x0652;
            case 991498806: goto L_0x0646;
            case 1007364121: goto L_0x063a;
            case 1019917311: goto L_0x062e;
            case 1019926225: goto L_0x0622;
            case 1020207774: goto L_0x0616;
            case 1020317708: goto L_0x060a;
            case 1060349560: goto L_0x05fe;
            case 1060358474: goto L_0x05f2;
            case 1060640023: goto L_0x05e6;
            case 1060749957: goto L_0x05db;
            case 1073049781: goto L_0x05cf;
            case 1078101399: goto L_0x05c3;
            case 1110103437: goto L_0x05b7;
            case 1160762272: goto L_0x05ab;
            case 1172918249: goto L_0x059f;
            case 1234591620: goto L_0x0593;
            case 1281128640: goto L_0x0587;
            case 1281131225: goto L_0x057b;
            case 1281131340: goto L_0x056f;
            case 1310789062: goto L_0x0564;
            case 1333118583: goto L_0x0558;
            case 1361447897: goto L_0x054c;
            case 1498266155: goto L_0x0540;
            case 1533804208: goto L_0x0534;
            case 1547988151: goto L_0x0528;
            case 1561464595: goto L_0x051c;
            case 1563525743: goto L_0x0510;
            case 1567024476: goto L_0x0504;
            case 1810705077: goto L_0x04f8;
            case 1815177512: goto L_0x04ec;
            case 1963241394: goto L_0x04e0;
            case 2014789757: goto L_0x04d4;
            case 2022049433: goto L_0x04c8;
            case 2048733346: goto L_0x04bc;
            case 2099392181: goto L_0x04b0;
            case 2140162142: goto L_0x04a4;
            default: goto L_0x04a2;
        };
    L_0x04a2:
        goto L_0x092b;
    L_0x04a4:
        r11 = "CHAT_MESSAGE_GEOLIVE";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x04ac:
        r11 = 53;
        goto L_0x092c;
    L_0x04b0:
        r11 = "CHANNEL_MESSAGE_PHOTOS";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x04b8:
        r11 = 39;
        goto L_0x092c;
    L_0x04bc:
        r11 = "CHANNEL_MESSAGE_NOTEXT";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x04c4:
        r11 = 25;
        goto L_0x092c;
    L_0x04c8:
        r11 = "PINNED_CONTACT";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x04d0:
        r11 = 80;
        goto L_0x092c;
    L_0x04d4:
        r11 = "CHAT_PHOTO_EDITED";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x04dc:
        r11 = 61;
        goto L_0x092c;
    L_0x04e0:
        r11 = "LOCKED_MESSAGE";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x04e8:
        r11 = 92;
        goto L_0x092c;
    L_0x04ec:
        r11 = "CHANNEL_MESSAGES";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x04f4:
        r11 = 41;
        goto L_0x092c;
    L_0x04f8:
        r11 = "MESSAGE_INVOICE";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0500:
        r11 = 20;
        goto L_0x092c;
    L_0x0504:
        r11 = "CHAT_MESSAGE_VIDEO";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x050c:
        r11 = 45;
        goto L_0x092c;
    L_0x0510:
        r11 = "CHAT_MESSAGE_ROUND";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0518:
        r11 = 46;
        goto L_0x092c;
    L_0x051c:
        r11 = "CHAT_MESSAGE_PHOTO";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0524:
        r11 = 44;
        goto L_0x092c;
    L_0x0528:
        r11 = "CHAT_MESSAGE_AUDIO";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0530:
        r11 = 49;
        goto L_0x092c;
    L_0x0534:
        r11 = "MESSAGE_VIDEOS";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x053c:
        r11 = 23;
        goto L_0x092c;
    L_0x0540:
        r11 = "PHONE_CALL_MISSED";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0548:
        r11 = 97;
        goto L_0x092c;
    L_0x054c:
        r11 = "MESSAGE_PHOTOS";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0554:
        r11 = 22;
        goto L_0x092c;
    L_0x0558:
        r11 = "CHAT_MESSAGE_VIDEOS";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0560:
        r11 = 70;
        goto L_0x092c;
    L_0x0564:
        r11 = "MESSAGE_NOTEXT";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x056c:
        r11 = 2;
        goto L_0x092c;
    L_0x056f:
        r11 = "MESSAGE_GIF";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0577:
        r11 = 16;
        goto L_0x092c;
    L_0x057b:
        r11 = "MESSAGE_GEO";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0583:
        r11 = 14;
        goto L_0x092c;
    L_0x0587:
        r11 = "MESSAGE_DOC";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x058f:
        r11 = 9;
        goto L_0x092c;
    L_0x0593:
        r11 = "CHAT_MESSAGE_GAME_SCORE";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x059b:
        r11 = 56;
        goto L_0x092c;
    L_0x059f:
        r11 = "CHANNEL_MESSAGE_GEOLIVE";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x05a7:
        r11 = 35;
        goto L_0x092c;
    L_0x05ab:
        r11 = "CHAT_MESSAGE_PHOTOS";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x05b3:
        r11 = 69;
        goto L_0x092c;
    L_0x05b7:
        r11 = "CHAT_MESSAGE_NOTEXT";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x05bf:
        r11 = 43;
        goto L_0x092c;
    L_0x05c3:
        r11 = "CHAT_TITLE_EDITED";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x05cb:
        r11 = 60;
        goto L_0x092c;
    L_0x05cf:
        r11 = "PINNED_NOTEXT";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x05d7:
        r11 = 73;
        goto L_0x092c;
    L_0x05db:
        r11 = "MESSAGE_TEXT";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x05e3:
        r11 = 0;
        goto L_0x092c;
    L_0x05e6:
        r11 = "MESSAGE_POLL";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x05ee:
        r11 = 13;
        goto L_0x092c;
    L_0x05f2:
        r11 = "MESSAGE_GAME";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x05fa:
        r11 = 17;
        goto L_0x092c;
    L_0x05fe:
        r11 = "MESSAGE_FWDS";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0606:
        r11 = 21;
        goto L_0x092c;
    L_0x060a:
        r11 = "CHAT_MESSAGE_TEXT";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0612:
        r11 = 42;
        goto L_0x092c;
    L_0x0616:
        r11 = "CHAT_MESSAGE_POLL";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x061e:
        r11 = 51;
        goto L_0x092c;
    L_0x0622:
        r11 = "CHAT_MESSAGE_GAME";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x062a:
        r11 = 55;
        goto L_0x092c;
    L_0x062e:
        r11 = "CHAT_MESSAGE_FWDS";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0636:
        r11 = 68;
        goto L_0x092c;
    L_0x063a:
        r11 = "CHANNEL_MESSAGE_GAME_SCORE";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0642:
        r11 = 19;
        goto L_0x092c;
    L_0x0646:
        r11 = "PINNED_GEOLIVE";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x064e:
        r11 = 83;
        goto L_0x092c;
    L_0x0652:
        r11 = "MESSAGE_CONTACT";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x065a:
        r11 = 12;
        goto L_0x092c;
    L_0x065e:
        r11 = "PINNED_VIDEO";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0666:
        r11 = 75;
        goto L_0x092c;
    L_0x066a:
        r11 = "PINNED_ROUND";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0672:
        r11 = 76;
        goto L_0x092c;
    L_0x0676:
        r11 = "PINNED_PHOTO";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x067e:
        r11 = 74;
        goto L_0x092c;
    L_0x0682:
        r11 = "PINNED_AUDIO";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x068a:
        r11 = 79;
        goto L_0x092c;
    L_0x068e:
        r11 = "MESSAGE_PHOTO_SECRET";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0696:
        r11 = 4;
        goto L_0x092c;
    L_0x0699:
        r11 = "CHANNEL_MESSAGE_VIDEO";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x06a1:
        r11 = 27;
        goto L_0x092c;
    L_0x06a5:
        r11 = "CHANNEL_MESSAGE_ROUND";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x06ad:
        r11 = 28;
        goto L_0x092c;
    L_0x06b1:
        r11 = "CHANNEL_MESSAGE_PHOTO";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x06b9:
        r11 = 26;
        goto L_0x092c;
    L_0x06bd:
        r11 = "CHANNEL_MESSAGE_AUDIO";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x06c5:
        r11 = 31;
        goto L_0x092c;
    L_0x06c9:
        r11 = "CHAT_MESSAGE_STICKER";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x06d1:
        r11 = 48;
        goto L_0x092c;
    L_0x06d5:
        r11 = "MESSAGES";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x06dd:
        r11 = 24;
        goto L_0x092c;
    L_0x06e1:
        r11 = "CHAT_MESSAGE_GIF";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x06e9:
        r11 = 54;
        goto L_0x092c;
    L_0x06ed:
        r11 = "CHAT_MESSAGE_GEO";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x06f5:
        r11 = 52;
        goto L_0x092c;
    L_0x06f9:
        r11 = "CHAT_MESSAGE_DOC";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0701:
        r11 = 47;
        goto L_0x092c;
    L_0x0705:
        r11 = "CHAT_LEFT";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x070d:
        r11 = 65;
        goto L_0x092c;
    L_0x0711:
        r11 = "CHAT_ADD_YOU";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0719:
        r11 = 59;
        goto L_0x092c;
    L_0x071d:
        r11 = "CHAT_DELETE_MEMBER";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0725:
        r11 = 63;
        goto L_0x092c;
    L_0x0729:
        r11 = "MESSAGE_SCREENSHOT";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0731:
        r11 = 7;
        goto L_0x092c;
    L_0x0734:
        r11 = "AUTH_REGION";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x073c:
        r11 = 91;
        goto L_0x092c;
    L_0x0740:
        r11 = "CONTACT_JOINED";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0748:
        r11 = 89;
        goto L_0x092c;
    L_0x074c:
        r11 = "CHAT_MESSAGE_INVOICE";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0754:
        r11 = 57;
        goto L_0x092c;
    L_0x0758:
        r11 = "ENCRYPTION_REQUEST";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0760:
        r11 = 93;
        goto L_0x092c;
    L_0x0764:
        r11 = "MESSAGE_GEOLIVE";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x076c:
        r11 = 15;
        goto L_0x092c;
    L_0x0770:
        r11 = "CHAT_DELETE_YOU";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0778:
        r11 = 64;
        goto L_0x092c;
    L_0x077c:
        r11 = "AUTH_UNKNOWN";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0784:
        r11 = 90;
        goto L_0x092c;
    L_0x0788:
        r11 = "PINNED_GIF";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0790:
        r11 = 87;
        goto L_0x092c;
    L_0x0794:
        r11 = "PINNED_GEO";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x079c:
        r11 = 82;
        goto L_0x092c;
    L_0x07a0:
        r11 = "PINNED_DOC";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x07a8:
        r11 = 77;
        goto L_0x092c;
    L_0x07ac:
        r11 = "PINNED_GAME_SCORE";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x07b4:
        r11 = 85;
        goto L_0x092c;
    L_0x07b8:
        r11 = "CHANNEL_MESSAGE_STICKER";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x07c0:
        r11 = 30;
        goto L_0x092c;
    L_0x07c4:
        r11 = "PHONE_CALL_REQUEST";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x07cc:
        r11 = 95;
        goto L_0x092c;
    L_0x07d0:
        r11 = "PINNED_STICKER";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x07d8:
        r11 = 78;
        goto L_0x092c;
    L_0x07dc:
        r11 = "PINNED_TEXT";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x07e4:
        r11 = 72;
        goto L_0x092c;
    L_0x07e8:
        r11 = "PINNED_POLL";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x07f0:
        r11 = 81;
        goto L_0x092c;
    L_0x07f4:
        r11 = "PINNED_GAME";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x07fc:
        r11 = 84;
        goto L_0x092c;
    L_0x0800:
        r11 = "CHAT_MESSAGE_CONTACT";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0808:
        r11 = 50;
        goto L_0x092c;
    L_0x080c:
        r11 = "MESSAGE_VIDEO_SECRET";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0814:
        r11 = 6;
        goto L_0x092c;
    L_0x0817:
        r11 = "CHANNEL_MESSAGE_TEXT";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x081f:
        r11 = 1;
        goto L_0x092c;
    L_0x0822:
        r11 = "CHANNEL_MESSAGE_POLL";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x082a:
        r11 = 33;
        goto L_0x092c;
    L_0x082e:
        r11 = "CHANNEL_MESSAGE_GAME";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0836:
        r11 = 37;
        goto L_0x092c;
    L_0x083a:
        r11 = "CHANNEL_MESSAGE_FWDS";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0842:
        r11 = 38;
        goto L_0x092c;
    L_0x0846:
        r11 = "PINNED_INVOICE";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x084e:
        r11 = 86;
        goto L_0x092c;
    L_0x0852:
        r11 = "CHAT_RETURNED";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x085a:
        r11 = 66;
        goto L_0x092c;
    L_0x085e:
        r11 = "ENCRYPTED_MESSAGE";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0866:
        r11 = 88;
        goto L_0x092c;
    L_0x086a:
        r11 = "ENCRYPTION_ACCEPT";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0872:
        r11 = 94;
        goto L_0x092c;
    L_0x0876:
        r11 = "MESSAGE_VIDEO";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x087e:
        r11 = 5;
        goto L_0x092c;
    L_0x0881:
        r11 = "MESSAGE_ROUND";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0889:
        r11 = 8;
        goto L_0x092c;
    L_0x088d:
        r11 = "MESSAGE_PHOTO";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0895:
        r11 = 3;
        goto L_0x092c;
    L_0x0898:
        r11 = "MESSAGE_MUTED";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x08a0:
        r11 = 96;
        goto L_0x092c;
    L_0x08a4:
        r11 = "MESSAGE_AUDIO";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x08ac:
        r11 = 11;
        goto L_0x092c;
    L_0x08b0:
        r11 = "CHAT_MESSAGES";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x08b8:
        r11 = 71;
        goto L_0x092c;
    L_0x08bc:
        r11 = "CHAT_JOINED";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x08c4:
        r11 = 67;
        goto L_0x092c;
    L_0x08c8:
        r11 = "CHAT_ADD_MEMBER";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x08d0:
        r11 = 62;
        goto L_0x092c;
    L_0x08d3:
        r11 = "CHANNEL_MESSAGE_GIF";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x08db:
        r11 = 36;
        goto L_0x092c;
    L_0x08de:
        r11 = "CHANNEL_MESSAGE_GEO";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x08e6:
        r11 = 34;
        goto L_0x092c;
    L_0x08e9:
        r11 = "CHANNEL_MESSAGE_DOC";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x08f1:
        r11 = 29;
        goto L_0x092c;
    L_0x08f4:
        r11 = "CHANNEL_MESSAGE_VIDEOS";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x08fc:
        r11 = 40;
        goto L_0x092c;
    L_0x08ff:
        r11 = "MESSAGE_STICKER";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0907:
        r11 = 10;
        goto L_0x092c;
    L_0x090a:
        r11 = "CHAT_CREATED";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0912:
        r11 = 58;
        goto L_0x092c;
    L_0x0915:
        r11 = "CHANNEL_MESSAGE_CONTACT";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x091d:
        r11 = 32;
        goto L_0x092c;
    L_0x0920:
        r11 = "MESSAGE_GAME_SCORE";
        r11 = r9.equals(r11);	 Catch:{ all -> 0x0419 }
        if (r11 == 0) goto L_0x092b;
    L_0x0928:
        r11 = 18;
        goto L_0x092c;
    L_0x092b:
        r11 = -1;
    L_0x092c:
        r14 = "Photos";
        r18 = r7;
        r7 = "ChannelMessageFew";
        r31 = r6;
        r6 = " ";
        r32 = r2;
        r2 = "AttachSticker";
        switch(r11) {
            case 0: goto L_0x161f;
            case 1: goto L_0x161f;
            case 2: goto L_0x15ff;
            case 3: goto L_0x15e2;
            case 4: goto L_0x15c5;
            case 5: goto L_0x15a8;
            case 6: goto L_0x158a;
            case 7: goto L_0x1574;
            case 8: goto L_0x1556;
            case 9: goto L_0x1538;
            case 10: goto L_0x14d8;
            case 11: goto L_0x14ba;
            case 12: goto L_0x1497;
            case 13: goto L_0x1474;
            case 14: goto L_0x1456;
            case 15: goto L_0x1438;
            case 16: goto L_0x141a;
            case 17: goto L_0x13f7;
            case 18: goto L_0x13d8;
            case 19: goto L_0x13d8;
            case 20: goto L_0x13b5;
            case 21: goto L_0x138f;
            case 22: goto L_0x1369;
            case 23: goto L_0x1341;
            case 24: goto L_0x132c;
            case 25: goto L_0x130e;
            case 26: goto L_0x12f0;
            case 27: goto L_0x12d2;
            case 28: goto L_0x12b4;
            case 29: goto L_0x1296;
            case 30: goto L_0x1239;
            case 31: goto L_0x121b;
            case 32: goto L_0x11f8;
            case 33: goto L_0x11d5;
            case 34: goto L_0x11b7;
            case 35: goto L_0x1199;
            case 36: goto L_0x117b;
            case 37: goto L_0x115d;
            case 38: goto L_0x1133;
            case 39: goto L_0x110f;
            case 40: goto L_0x10e9;
            case 41: goto L_0x10d4;
            case 42: goto L_0x10b3;
            case 43: goto L_0x1090;
            case 44: goto L_0x106d;
            case 45: goto L_0x104a;
            case 46: goto L_0x1027;
            case 47: goto L_0x1004;
            case 48: goto L_0x0var_;
            case 49: goto L_0x0var_;
            case 50: goto L_0x0var_;
            case 51: goto L_0x0var_;
            case 52: goto L_0x0eee;
            case 53: goto L_0x0ecb;
            case 54: goto L_0x0ea8;
            case 55: goto L_0x0e80;
            case 56: goto L_0x0e5c;
            case 57: goto L_0x0e34;
            case 58: goto L_0x0e1a;
            case 59: goto L_0x0e1a;
            case 60: goto L_0x0e00;
            case 61: goto L_0x0de6;
            case 62: goto L_0x0dc7;
            case 63: goto L_0x0dad;
            case 64: goto L_0x0d93;
            case 65: goto L_0x0d79;
            case 66: goto L_0x0d5f;
            case 67: goto L_0x0d45;
            case 68: goto L_0x0d1a;
            case 69: goto L_0x0cef;
            case 70: goto L_0x0cc2;
            case 71: goto L_0x0ca8;
            case 72: goto L_0x0c6f;
            case 73: goto L_0x0CLASSNAME;
            case 74: goto L_0x0CLASSNAME;
            case 75: goto L_0x0be2;
            case 76: goto L_0x0bb3;
            case 77: goto L_0x0b84;
            case 78: goto L_0x0b08;
            case 79: goto L_0x0ad9;
            case 80: goto L_0x0aa0;
            case 81: goto L_0x0a6b;
            case 82: goto L_0x0a3d;
            case 83: goto L_0x0a12;
            case 84: goto L_0x09e7;
            case 85: goto L_0x09ba;
            case 86: goto L_0x098d;
            case 87: goto L_0x0960;
            case 88: goto L_0x0947;
            case 89: goto L_0x0943;
            case 90: goto L_0x0943;
            case 91: goto L_0x0943;
            case 92: goto L_0x0943;
            case 93: goto L_0x0943;
            case 94: goto L_0x0943;
            case 95: goto L_0x0943;
            case 96: goto L_0x0943;
            case 97: goto L_0x0943;
            default: goto L_0x093d;
        };
    L_0x093d:
        r11 = r18;
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x16f9 }
        goto L_0x163a;
    L_0x0943:
        r11 = r18;
        goto L_0x1650;
    L_0x0947:
        r2 = "YouHaveNewMessage";
        r6 = NUM; // 0x7f0d0b77 float:1.8748068E38 double:1.0531312276E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r6);	 Catch:{ all -> 0x0419 }
        r6 = "SecretChatName";
        r7 = NUM; // 0x7f0d0960 float:1.8746983E38 double:1.0531309633E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        r25 = r6;
        r11 = r18;
    L_0x095d:
        r6 = 1;
        goto L_0x1652;
    L_0x0960:
        if (r8 == 0) goto L_0x097a;
    L_0x0962:
        r2 = "NotificationActionPinnedGif";
        r6 = NUM; // 0x7f0d067e float:1.8745486E38 double:1.0531305987E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r11;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r11;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x0a67;
    L_0x097a:
        r2 = "NotificationActionPinnedGifChannel";
        r6 = NUM; // 0x7f0d067f float:1.8745488E38 double:1.053130599E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r11 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r11;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        goto L_0x0a67;
    L_0x098d:
        if (r8 == 0) goto L_0x09a7;
    L_0x098f:
        r2 = "NotificationActionPinnedInvoice";
        r6 = NUM; // 0x7f0d0680 float:1.874549E38 double:1.0531305997E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r11;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r11;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x0a67;
    L_0x09a7:
        r2 = "NotificationActionPinnedInvoiceChannel";
        r6 = NUM; // 0x7f0d0681 float:1.8745492E38 double:1.0531306E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r11 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r11;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        goto L_0x0a67;
    L_0x09ba:
        if (r8 == 0) goto L_0x09d4;
    L_0x09bc:
        r2 = "NotificationActionPinnedGameScore";
        r6 = NUM; // 0x7f0d0678 float:1.8745474E38 double:1.0531305957E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r11;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r11;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x0a67;
    L_0x09d4:
        r2 = "NotificationActionPinnedGameScoreChannel";
        r6 = NUM; // 0x7f0d0679 float:1.8745476E38 double:1.053130596E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r11 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r11;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        goto L_0x0a67;
    L_0x09e7:
        if (r8 == 0) goto L_0x0a00;
    L_0x09e9:
        r2 = "NotificationActionPinnedGame";
        r6 = NUM; // 0x7f0d0676 float:1.874547E38 double:1.0531305947E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r11;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r11;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x0a67;
    L_0x0a00:
        r2 = "NotificationActionPinnedGameChannel";
        r6 = NUM; // 0x7f0d0677 float:1.8745471E38 double:1.053130595E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r11 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r11;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        goto L_0x0a67;
    L_0x0a12:
        if (r8 == 0) goto L_0x0a2b;
    L_0x0a14:
        r2 = "NotificationActionPinnedGeoLive";
        r6 = NUM; // 0x7f0d067c float:1.8745482E38 double:1.0531305977E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r11;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r11;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x0a67;
    L_0x0a2b:
        r2 = "NotificationActionPinnedGeoLiveChannel";
        r6 = NUM; // 0x7f0d067d float:1.8745484E38 double:1.053130598E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r11 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r11;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        goto L_0x0a67;
    L_0x0a3d:
        if (r8 == 0) goto L_0x0a56;
    L_0x0a3f:
        r2 = "NotificationActionPinnedGeo";
        r6 = NUM; // 0x7f0d067a float:1.8745478E38 double:1.0531305967E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r11;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r11;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x0a67;
    L_0x0a56:
        r2 = "NotificationActionPinnedGeoChannel";
        r6 = NUM; // 0x7f0d067b float:1.874548E38 double:1.053130597E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r11 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r11;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
    L_0x0a67:
        r11 = r18;
        goto L_0x1651;
    L_0x0a6b:
        if (r8 == 0) goto L_0x0a89;
    L_0x0a6d:
        r2 = "NotificationActionPinnedPoll2";
        r6 = NUM; // 0x7f0d0688 float:1.8745506E38 double:1.0531306036E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r11;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r11;	 Catch:{ all -> 0x0419 }
        r10 = 2;
        r11 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r11;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x0a67;
    L_0x0a89:
        r2 = "NotificationActionPinnedPollChannel2";
        r6 = NUM; // 0x7f0d0689 float:1.8745508E38 double:1.053130604E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r11 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r11;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r11 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r11;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x0a67;
    L_0x0aa0:
        r11 = r18;
        if (r8 == 0) goto L_0x0ac1;
    L_0x0aa4:
        r2 = "NotificationActionPinnedContact2";
        r6 = NUM; // 0x7f0d0672 float:1.8745461E38 double:1.0531305928E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 2;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0ac1:
        r2 = "NotificationActionPinnedContactChannel2";
        r6 = NUM; // 0x7f0d0673 float:1.8745463E38 double:1.053130593E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0ad9:
        r11 = r18;
        if (r8 == 0) goto L_0x0af5;
    L_0x0add:
        r2 = "NotificationActionPinnedVoice";
        r6 = NUM; // 0x7f0d0694 float:1.874553E38 double:1.0531306096E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0af5:
        r2 = "NotificationActionPinnedVoiceChannel";
        r6 = NUM; // 0x7f0d0695 float:1.8745532E38 double:1.05313061E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0b08:
        r11 = r18;
        if (r8 == 0) goto L_0x0b4d;
    L_0x0b0c:
        r2 = r15.length;	 Catch:{ all -> 0x0419 }
        r6 = 2;
        if (r2 <= r6) goto L_0x0b35;
    L_0x0b10:
        r2 = r15[r6];	 Catch:{ all -> 0x0419 }
        r2 = android.text.TextUtils.isEmpty(r2);	 Catch:{ all -> 0x0419 }
        if (r2 != 0) goto L_0x0b35;
    L_0x0b18:
        r2 = "NotificationActionPinnedStickerEmoji";
        r6 = NUM; // 0x7f0d068e float:1.8745518E38 double:1.0531306066E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 2;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0b35:
        r2 = "NotificationActionPinnedSticker";
        r6 = NUM; // 0x7f0d068c float:1.8745514E38 double:1.0531306056E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0b4d:
        r2 = r15.length;	 Catch:{ all -> 0x0419 }
        r6 = 1;
        if (r2 <= r6) goto L_0x0b71;
    L_0x0b51:
        r2 = r15[r6];	 Catch:{ all -> 0x0419 }
        r2 = android.text.TextUtils.isEmpty(r2);	 Catch:{ all -> 0x0419 }
        if (r2 != 0) goto L_0x0b71;
    L_0x0b59:
        r2 = "NotificationActionPinnedStickerEmojiChannel";
        r6 = NUM; // 0x7f0d068f float:1.874552E38 double:1.053130607E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0b71:
        r2 = "NotificationActionPinnedStickerChannel";
        r6 = NUM; // 0x7f0d068d float:1.8745516E38 double:1.053130606E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0b84:
        r11 = r18;
        if (r8 == 0) goto L_0x0ba0;
    L_0x0b88:
        r2 = "NotificationActionPinnedFile";
        r6 = NUM; // 0x7f0d0674 float:1.8745465E38 double:1.0531305937E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0ba0:
        r2 = "NotificationActionPinnedFileChannel";
        r6 = NUM; // 0x7f0d0675 float:1.8745467E38 double:1.053130594E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0bb3:
        r11 = r18;
        if (r8 == 0) goto L_0x0bcf;
    L_0x0bb7:
        r2 = "NotificationActionPinnedRound";
        r6 = NUM; // 0x7f0d068a float:1.874551E38 double:1.0531306046E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0bcf:
        r2 = "NotificationActionPinnedRoundChannel";
        r6 = NUM; // 0x7f0d068b float:1.8745512E38 double:1.053130605E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0be2:
        r11 = r18;
        if (r8 == 0) goto L_0x0bfe;
    L_0x0be6:
        r2 = "NotificationActionPinnedVideo";
        r6 = NUM; // 0x7f0d0692 float:1.8745526E38 double:1.0531306086E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0bfe:
        r2 = "NotificationActionPinnedVideoChannel";
        r6 = NUM; // 0x7f0d0693 float:1.8745528E38 double:1.053130609E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0CLASSNAME:
        r11 = r18;
        if (r8 == 0) goto L_0x0c2d;
    L_0x0CLASSNAME:
        r2 = "NotificationActionPinnedPhoto";
        r6 = NUM; // 0x7f0d0686 float:1.8745502E38 double:1.0531306026E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0c2d:
        r2 = "NotificationActionPinnedPhotoChannel";
        r6 = NUM; // 0x7f0d0687 float:1.8745504E38 double:1.053130603E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0CLASSNAME:
        r11 = r18;
        if (r8 == 0) goto L_0x0c5c;
    L_0x0CLASSNAME:
        r2 = "NotificationActionPinnedNoText";
        r6 = NUM; // 0x7f0d0684 float:1.8745498E38 double:1.0531306016E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0c5c:
        r2 = "NotificationActionPinnedNoTextChannel";
        r6 = NUM; // 0x7f0d0685 float:1.87455E38 double:1.053130602E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0c6f:
        r11 = r18;
        if (r8 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r2 = "NotificationActionPinnedText";
        r6 = NUM; // 0x7f0d0690 float:1.8745522E38 double:1.0531306076E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 2;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0CLASSNAME:
        r2 = "NotificationActionPinnedTextChannel";
        r6 = NUM; // 0x7f0d0691 float:1.8745524E38 double:1.053130608E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0ca8:
        r11 = r18;
        r2 = "NotificationGroupAlbum";
        r6 = NUM; // 0x7f0d069d float:1.8745549E38 double:1.053130614E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x095d;
    L_0x0cc2:
        r11 = r18;
        r2 = "NotificationGroupFew";
        r6 = NUM; // 0x7f0d069e float:1.874555E38 double:1.0531306145E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = "Videos";
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x0419 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x0419 }
        r15 = r15.intValue();	 Catch:{ all -> 0x0419 }
        r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r15);	 Catch:{ all -> 0x0419 }
        r7[r14] = r10;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x095d;
    L_0x0cef:
        r11 = r18;
        r2 = "NotificationGroupFew";
        r6 = NUM; // 0x7f0d069e float:1.874555E38 double:1.0531306145E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r18 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r18;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r18 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r18;	 Catch:{ all -> 0x0419 }
        r10 = 2;
        r15 = r15[r10];	 Catch:{ all -> 0x0419 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x0419 }
        r15 = r15.intValue();	 Catch:{ all -> 0x0419 }
        r14 = org.telegram.messenger.LocaleController.formatPluralString(r14, r15);	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x095d;
    L_0x0d1a:
        r11 = r18;
        r2 = "NotificationGroupForwardedFew";
        r6 = NUM; // 0x7f0d069f float:1.8745553E38 double:1.053130615E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x0419 }
        r7[r14] = r18;	 Catch:{ all -> 0x0419 }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x0419 }
        r7[r14] = r18;	 Catch:{ all -> 0x0419 }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x0419 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x0419 }
        r15 = r15.intValue();	 Catch:{ all -> 0x0419 }
        r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r15);	 Catch:{ all -> 0x0419 }
        r7[r14] = r10;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x095d;
    L_0x0d45:
        r11 = r18;
        r2 = "NotificationGroupAddSelfMega";
        r6 = NUM; // 0x7f0d069c float:1.8745547E38 double:1.0531306135E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0d5f:
        r11 = r18;
        r2 = "NotificationGroupAddSelf";
        r6 = NUM; // 0x7f0d069b float:1.8745544E38 double:1.053130613E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0d79:
        r11 = r18;
        r2 = "NotificationGroupLeftMember";
        r6 = NUM; // 0x7f0d06a2 float:1.8745559E38 double:1.0531306165E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0d93:
        r11 = r18;
        r2 = "NotificationGroupKickYou";
        r6 = NUM; // 0x7f0d06a1 float:1.8745557E38 double:1.053130616E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0dad:
        r11 = r18;
        r2 = "NotificationGroupKickMember";
        r6 = NUM; // 0x7f0d06a0 float:1.8745555E38 double:1.0531306155E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0dc7:
        r11 = r18;
        r2 = "NotificationGroupAddMember";
        r6 = NUM; // 0x7f0d069a float:1.8745542E38 double:1.0531306125E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 2;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0de6:
        r11 = r18;
        r2 = "NotificationEditedGroupPhoto";
        r6 = NUM; // 0x7f0d0699 float:1.874554E38 double:1.053130612E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0e00:
        r11 = r18;
        r2 = "NotificationEditedGroupName";
        r6 = NUM; // 0x7f0d0698 float:1.8745538E38 double:1.0531306115E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0e1a:
        r11 = r18;
        r2 = "NotificationInvitedToGroup";
        r6 = NUM; // 0x7f0d06a3 float:1.874556E38 double:1.053130617E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0e34:
        r11 = r18;
        r2 = "NotificationMessageGroupInvoice";
        r6 = NUM; // 0x7f0d06b4 float:1.8745595E38 double:1.0531306254E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 2;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "PaymentInvoice";
        r7 = NUM; // 0x7f0d0817 float:1.8746315E38 double:1.053130801E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x0e5c:
        r11 = r18;
        r2 = "NotificationMessageGroupGameScored";
        r6 = NUM; // 0x7f0d06b2 float:1.8745591E38 double:1.0531306244E-314;
        r10 = 4;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x0419 }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x0419 }
        r10[r14] = r18;	 Catch:{ all -> 0x0419 }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x0419 }
        r10[r14] = r18;	 Catch:{ all -> 0x0419 }
        r14 = 2;
        r17 = r15[r14];	 Catch:{ all -> 0x0419 }
        r10[r14] = r17;	 Catch:{ all -> 0x0419 }
        r7 = 3;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x0e80:
        r11 = r18;
        r2 = "NotificationMessageGroupGame";
        r6 = NUM; // 0x7f0d06b1 float:1.874559E38 double:1.053130624E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 2;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "AttachGame";
        r7 = NUM; // 0x7f0d013f float:1.8742762E38 double:1.053129935E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x0ea8:
        r11 = r18;
        r2 = "NotificationMessageGroupGif";
        r6 = NUM; // 0x7f0d06b3 float:1.8745593E38 double:1.053130625E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "AttachGif";
        r7 = NUM; // 0x7f0d0140 float:1.8742764E38 double:1.0531299356E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x0ecb:
        r11 = r18;
        r2 = "NotificationMessageGroupLiveLocation";
        r6 = NUM; // 0x7f0d06b5 float:1.8745597E38 double:1.053130626E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "AttachLiveLocation";
        r7 = NUM; // 0x7f0d0145 float:1.8742774E38 double:1.053129938E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x0eee:
        r11 = r18;
        r2 = "NotificationMessageGroupMap";
        r6 = NUM; // 0x7f0d06b6 float:1.87456E38 double:1.0531306263E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "AttachLocation";
        r7 = NUM; // 0x7f0d0147 float:1.8742778E38 double:1.053129939E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x0var_:
        r11 = r18;
        r2 = "NotificationMessageGroupPoll2";
        r6 = NUM; // 0x7f0d06ba float:1.8745607E38 double:1.0531306283E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 2;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "Poll";
        r7 = NUM; // 0x7f0d0884 float:1.8746536E38 double:1.0531308546E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x0var_:
        r11 = r18;
        r2 = "NotificationMessageGroupContact2";
        r6 = NUM; // 0x7f0d06af float:1.8745585E38 double:1.053130623E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 2;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "AttachContact";
        r7 = NUM; // 0x7f0d013b float:1.8742754E38 double:1.053129933E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x0var_:
        r11 = r18;
        r2 = "NotificationMessageGroupAudio";
        r6 = NUM; // 0x7f0d06ae float:1.8745583E38 double:1.0531306224E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "AttachAudio";
        r7 = NUM; // 0x7f0d0139 float:1.874275E38 double:1.053129932E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x0var_:
        r11 = r18;
        r10 = r15.length;	 Catch:{ all -> 0x0419 }
        r14 = 2;
        if (r10 <= r14) goto L_0x0fcc;
    L_0x0f8a:
        r10 = r15[r14];	 Catch:{ all -> 0x0419 }
        r10 = android.text.TextUtils.isEmpty(r10);	 Catch:{ all -> 0x0419 }
        if (r10 != 0) goto L_0x0fcc;
    L_0x0var_:
        r10 = "NotificationMessageGroupStickerEmoji";
        r14 = NUM; // 0x7f0d06bd float:1.8745613E38 double:1.05313063E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r16 = 0;
        r18 = r15[r16];	 Catch:{ all -> 0x0419 }
        r7[r16] = r18;	 Catch:{ all -> 0x0419 }
        r16 = 1;
        r18 = r15[r16];	 Catch:{ all -> 0x0419 }
        r7[r16] = r18;	 Catch:{ all -> 0x0419 }
        r16 = 2;
        r17 = r15[r16];	 Catch:{ all -> 0x0419 }
        r7[r16] = r17;	 Catch:{ all -> 0x0419 }
        r7 = org.telegram.messenger.LocaleController.formatString(r10, r14, r7);	 Catch:{ all -> 0x0419 }
        r10 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0419 }
        r10.<init>();	 Catch:{ all -> 0x0419 }
        r14 = r15[r16];	 Catch:{ all -> 0x0419 }
        r10.append(r14);	 Catch:{ all -> 0x0419 }
        r10.append(r6);	 Catch:{ all -> 0x0419 }
        r6 = NUM; // 0x7f0d014e float:1.8742792E38 double:1.0531299426E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r6);	 Catch:{ all -> 0x0419 }
        r10.append(r2);	 Catch:{ all -> 0x0419 }
        r2 = r10.toString();	 Catch:{ all -> 0x0419 }
        goto L_0x0fff;
    L_0x0fcc:
        r7 = "NotificationMessageGroupSticker";
        r10 = NUM; // 0x7f0d06bc float:1.8745611E38 double:1.0531306293E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x0419 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ all -> 0x0419 }
        r14[r16] = r17;	 Catch:{ all -> 0x0419 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ all -> 0x0419 }
        r14[r16] = r17;	 Catch:{ all -> 0x0419 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r10, r14);	 Catch:{ all -> 0x0419 }
        r10 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0419 }
        r10.<init>();	 Catch:{ all -> 0x0419 }
        r14 = r15[r16];	 Catch:{ all -> 0x0419 }
        r10.append(r14);	 Catch:{ all -> 0x0419 }
        r10.append(r6);	 Catch:{ all -> 0x0419 }
        r6 = NUM; // 0x7f0d014e float:1.8742792E38 double:1.0531299426E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r6);	 Catch:{ all -> 0x0419 }
        r10.append(r2);	 Catch:{ all -> 0x0419 }
        r2 = r10.toString();	 Catch:{ all -> 0x0419 }
    L_0x0fff:
        r16 = r2;
        r2 = r7;
        goto L_0x161d;
    L_0x1004:
        r11 = r18;
        r2 = "NotificationMessageGroupDocument";
        r6 = NUM; // 0x7f0d06b0 float:1.8745587E38 double:1.0531306234E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "AttachDocument";
        r7 = NUM; // 0x7f0d013e float:1.874276E38 double:1.0531299347E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x1027:
        r11 = r18;
        r2 = "NotificationMessageGroupRound";
        r6 = NUM; // 0x7f0d06bb float:1.874561E38 double:1.053130629E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "AttachRound";
        r7 = NUM; // 0x7f0d014d float:1.874279E38 double:1.053129942E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x104a:
        r11 = r18;
        r2 = "NotificationMessageGroupVideo";
        r6 = NUM; // 0x7f0d06bf float:1.8745618E38 double:1.053130631E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "AttachVideo";
        r7 = NUM; // 0x7f0d0151 float:1.8742798E38 double:1.053129944E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x106d:
        r11 = r18;
        r2 = "NotificationMessageGroupPhoto";
        r6 = NUM; // 0x7f0d06b9 float:1.8745605E38 double:1.053130628E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "AttachPhoto";
        r7 = NUM; // 0x7f0d014b float:1.8742786E38 double:1.053129941E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x1090:
        r11 = r18;
        r2 = "NotificationMessageGroupNoText";
        r6 = NUM; // 0x7f0d06b8 float:1.8745603E38 double:1.0531306273E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "Message";
        r7 = NUM; // 0x7f0d05e6 float:1.8745177E38 double:1.0531305236E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x10b3:
        r11 = r18;
        r2 = "NotificationMessageGroupText";
        r6 = NUM; // 0x7f0d06be float:1.8745615E38 double:1.0531306303E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 2;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = r15[r10];	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x10d4:
        r11 = r18;
        r2 = "ChannelMessageAlbum";
        r6 = NUM; // 0x7f0d0242 float:1.8743287E38 double:1.053130063E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        goto L_0x095d;
    L_0x10e9:
        r11 = r18;
        r2 = 2;
        r2 = new java.lang.Object[r2];	 Catch:{ all -> 0x0419 }
        r6 = 0;
        r10 = r15[r6];	 Catch:{ all -> 0x0419 }
        r2[r6] = r10;	 Catch:{ all -> 0x0419 }
        r6 = "Videos";
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r14 = org.telegram.messenger.Utilities.parseInt(r14);	 Catch:{ all -> 0x0419 }
        r14 = r14.intValue();	 Catch:{ all -> 0x0419 }
        r6 = org.telegram.messenger.LocaleController.formatPluralString(r6, r14);	 Catch:{ all -> 0x0419 }
        r2[r10] = r6;	 Catch:{ all -> 0x0419 }
        r6 = NUM; // 0x7f0d0246 float:1.8743295E38 double:1.053130065E-314;
        r2 = org.telegram.messenger.LocaleController.formatString(r7, r6, r2);	 Catch:{ all -> 0x0419 }
        goto L_0x095d;
    L_0x110f:
        r11 = r18;
        r2 = 2;
        r2 = new java.lang.Object[r2];	 Catch:{ all -> 0x0419 }
        r6 = 0;
        r10 = r15[r6];	 Catch:{ all -> 0x0419 }
        r2[r6] = r10;	 Catch:{ all -> 0x0419 }
        r6 = 1;
        r10 = r15[r6];	 Catch:{ all -> 0x0419 }
        r10 = org.telegram.messenger.Utilities.parseInt(r10);	 Catch:{ all -> 0x0419 }
        r10 = r10.intValue();	 Catch:{ all -> 0x0419 }
        r10 = org.telegram.messenger.LocaleController.formatPluralString(r14, r10);	 Catch:{ all -> 0x0419 }
        r2[r6] = r10;	 Catch:{ all -> 0x0419 }
        r6 = NUM; // 0x7f0d0246 float:1.8743295E38 double:1.053130065E-314;
        r2 = org.telegram.messenger.LocaleController.formatString(r7, r6, r2);	 Catch:{ all -> 0x0419 }
        goto L_0x095d;
    L_0x1133:
        r11 = r18;
        r2 = 2;
        r2 = new java.lang.Object[r2];	 Catch:{ all -> 0x0419 }
        r6 = 0;
        r10 = r15[r6];	 Catch:{ all -> 0x0419 }
        r2[r6] = r10;	 Catch:{ all -> 0x0419 }
        r6 = "ForwardedMessageCount";
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r14 = org.telegram.messenger.Utilities.parseInt(r14);	 Catch:{ all -> 0x0419 }
        r14 = r14.intValue();	 Catch:{ all -> 0x0419 }
        r6 = org.telegram.messenger.LocaleController.formatPluralString(r6, r14);	 Catch:{ all -> 0x0419 }
        r6 = r6.toLowerCase();	 Catch:{ all -> 0x0419 }
        r2[r10] = r6;	 Catch:{ all -> 0x0419 }
        r6 = NUM; // 0x7f0d0246 float:1.8743295E38 double:1.053130065E-314;
        r2 = org.telegram.messenger.LocaleController.formatString(r7, r6, r2);	 Catch:{ all -> 0x0419 }
        goto L_0x095d;
    L_0x115d:
        r11 = r18;
        r2 = "NotificationMessageGame";
        r6 = NUM; // 0x7f0d06ab float:1.8745577E38 double:1.053130621E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachGame";
        r7 = NUM; // 0x7f0d013f float:1.8742762E38 double:1.053129935E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x117b:
        r11 = r18;
        r2 = "ChannelMessageGIF";
        r6 = NUM; // 0x7f0d0247 float:1.8743297E38 double:1.0531300656E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachGif";
        r7 = NUM; // 0x7f0d0140 float:1.8742764E38 double:1.0531299356E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x1199:
        r11 = r18;
        r2 = "ChannelMessageLiveLocation";
        r6 = NUM; // 0x7f0d0248 float:1.87433E38 double:1.053130066E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachLiveLocation";
        r7 = NUM; // 0x7f0d0145 float:1.8742774E38 double:1.053129938E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x11b7:
        r11 = r18;
        r2 = "ChannelMessageMap";
        r6 = NUM; // 0x7f0d0249 float:1.8743301E38 double:1.0531300666E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachLocation";
        r7 = NUM; // 0x7f0d0147 float:1.8742778E38 double:1.053129939E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x11d5:
        r11 = r18;
        r2 = "ChannelMessagePoll2";
        r6 = NUM; // 0x7f0d024d float:1.874331E38 double:1.0531300685E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "Poll";
        r7 = NUM; // 0x7f0d0884 float:1.8746536E38 double:1.0531308546E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x11f8:
        r11 = r18;
        r2 = "ChannelMessageContact2";
        r6 = NUM; // 0x7f0d0244 float:1.8743291E38 double:1.053130064E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "AttachContact";
        r7 = NUM; // 0x7f0d013b float:1.8742754E38 double:1.053129933E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x121b:
        r11 = r18;
        r2 = "ChannelMessageAudio";
        r6 = NUM; // 0x7f0d0243 float:1.874329E38 double:1.0531300636E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachAudio";
        r7 = NUM; // 0x7f0d0139 float:1.874275E38 double:1.053129932E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x1239:
        r11 = r18;
        r7 = r15.length;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        if (r7 <= r10) goto L_0x127c;
    L_0x123f:
        r7 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7 = android.text.TextUtils.isEmpty(r7);	 Catch:{ all -> 0x0419 }
        if (r7 != 0) goto L_0x127c;
    L_0x1247:
        r7 = "ChannelMessageStickerEmoji";
        r10 = NUM; // 0x7f0d0250 float:1.8743315E38 double:1.05313007E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x0419 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ all -> 0x0419 }
        r14[r16] = r17;	 Catch:{ all -> 0x0419 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ all -> 0x0419 }
        r14[r16] = r17;	 Catch:{ all -> 0x0419 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r10, r14);	 Catch:{ all -> 0x0419 }
        r10 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0419 }
        r10.<init>();	 Catch:{ all -> 0x0419 }
        r14 = r15[r16];	 Catch:{ all -> 0x0419 }
        r10.append(r14);	 Catch:{ all -> 0x0419 }
        r10.append(r6);	 Catch:{ all -> 0x0419 }
        r6 = NUM; // 0x7f0d014e float:1.8742792E38 double:1.0531299426E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r6);	 Catch:{ all -> 0x0419 }
        r10.append(r2);	 Catch:{ all -> 0x0419 }
        r2 = r10.toString();	 Catch:{ all -> 0x0419 }
        goto L_0x0fff;
    L_0x127c:
        r6 = "ChannelMessageSticker";
        r7 = NUM; // 0x7f0d024f float:1.8743313E38 double:1.0531300695E-314;
        r10 = 1;
        r14 = new java.lang.Object[r10];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r15 = r15[r10];	 Catch:{ all -> 0x0419 }
        r14[r10] = r15;	 Catch:{ all -> 0x0419 }
        r6 = org.telegram.messenger.LocaleController.formatString(r6, r7, r14);	 Catch:{ all -> 0x0419 }
        r7 = NUM; // 0x7f0d014e float:1.8742792E38 double:1.0531299426E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1533;
    L_0x1296:
        r11 = r18;
        r2 = "ChannelMessageDocument";
        r6 = NUM; // 0x7f0d0245 float:1.8743293E38 double:1.0531300646E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachDocument";
        r7 = NUM; // 0x7f0d013e float:1.874276E38 double:1.0531299347E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x12b4:
        r11 = r18;
        r2 = "ChannelMessageRound";
        r6 = NUM; // 0x7f0d024e float:1.8743311E38 double:1.053130069E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachRound";
        r7 = NUM; // 0x7f0d014d float:1.874279E38 double:1.053129942E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x12d2:
        r11 = r18;
        r2 = "ChannelMessageVideo";
        r6 = NUM; // 0x7f0d0251 float:1.8743317E38 double:1.0531300705E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachVideo";
        r7 = NUM; // 0x7f0d0151 float:1.8742798E38 double:1.053129944E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x12f0:
        r11 = r18;
        r2 = "ChannelMessagePhoto";
        r6 = NUM; // 0x7f0d024c float:1.8743307E38 double:1.053130068E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachPhoto";
        r7 = NUM; // 0x7f0d014b float:1.8742786E38 double:1.053129941E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x130e:
        r11 = r18;
        r2 = "ChannelMessageNoText";
        r6 = NUM; // 0x7f0d024b float:1.8743305E38 double:1.0531300676E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "Message";
        r7 = NUM; // 0x7f0d05e6 float:1.8745177E38 double:1.0531305236E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x132c:
        r11 = r18;
        r2 = "NotificationMessageAlbum";
        r6 = NUM; // 0x7f0d06a5 float:1.8745565E38 double:1.053130618E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        goto L_0x095d;
    L_0x1341:
        r11 = r18;
        r2 = "NotificationMessageFew";
        r6 = NUM; // 0x7f0d06a9 float:1.8745573E38 double:1.05313062E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = "Videos";
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0419 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x0419 }
        r15 = r15.intValue();	 Catch:{ all -> 0x0419 }
        r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r15);	 Catch:{ all -> 0x0419 }
        r7[r14] = r10;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x095d;
    L_0x1369:
        r11 = r18;
        r2 = "NotificationMessageFew";
        r6 = NUM; // 0x7f0d06a9 float:1.8745573E38 double:1.05313062E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r17 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r17;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r15 = r15[r10];	 Catch:{ all -> 0x0419 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x0419 }
        r15 = r15.intValue();	 Catch:{ all -> 0x0419 }
        r14 = org.telegram.messenger.LocaleController.formatPluralString(r14, r15);	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x095d;
    L_0x138f:
        r11 = r18;
        r2 = "NotificationMessageForwardFew";
        r6 = NUM; // 0x7f0d06aa float:1.8745575E38 double:1.0531306204E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x0419 }
        r7[r14] = r17;	 Catch:{ all -> 0x0419 }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x0419 }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x0419 }
        r15 = r15.intValue();	 Catch:{ all -> 0x0419 }
        r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r15);	 Catch:{ all -> 0x0419 }
        r7[r14] = r10;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x095d;
    L_0x13b5:
        r11 = r18;
        r2 = "NotificationMessageInvoice";
        r6 = NUM; // 0x7f0d06c0 float:1.874562E38 double:1.0531306313E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "PaymentInvoice";
        r7 = NUM; // 0x7f0d0817 float:1.8746315E38 double:1.053130801E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x13d8:
        r11 = r18;
        r2 = "NotificationMessageGameScored";
        r6 = NUM; // 0x7f0d06ac float:1.8745579E38 double:1.0531306214E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 2;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x13f7:
        r11 = r18;
        r2 = "NotificationMessageGame";
        r6 = NUM; // 0x7f0d06ab float:1.8745577E38 double:1.053130621E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "AttachGame";
        r7 = NUM; // 0x7f0d013f float:1.8742762E38 double:1.053129935E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x141a:
        r11 = r18;
        r2 = "NotificationMessageGif";
        r6 = NUM; // 0x7f0d06ad float:1.874558E38 double:1.053130622E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachGif";
        r7 = NUM; // 0x7f0d0140 float:1.8742764E38 double:1.0531299356E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x1438:
        r11 = r18;
        r2 = "NotificationMessageLiveLocation";
        r6 = NUM; // 0x7f0d06c1 float:1.8745622E38 double:1.053130632E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachLiveLocation";
        r7 = NUM; // 0x7f0d0145 float:1.8742774E38 double:1.053129938E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x1456:
        r11 = r18;
        r2 = "NotificationMessageMap";
        r6 = NUM; // 0x7f0d06c2 float:1.8745624E38 double:1.0531306323E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachLocation";
        r7 = NUM; // 0x7f0d0147 float:1.8742778E38 double:1.053129939E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x1474:
        r11 = r18;
        r2 = "NotificationMessagePoll2";
        r6 = NUM; // 0x7f0d06c6 float:1.8745632E38 double:1.0531306343E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "Poll";
        r7 = NUM; // 0x7f0d0884 float:1.8746536E38 double:1.0531308546E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x1497:
        r11 = r18;
        r2 = "NotificationMessageContact2";
        r6 = NUM; // 0x7f0d06a7 float:1.8745569E38 double:1.053130619E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = "AttachContact";
        r7 = NUM; // 0x7f0d013b float:1.8742754E38 double:1.053129933E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x14ba:
        r11 = r18;
        r2 = "NotificationMessageAudio";
        r6 = NUM; // 0x7f0d06a6 float:1.8745567E38 double:1.0531306184E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachAudio";
        r7 = NUM; // 0x7f0d0139 float:1.874275E38 double:1.053129932E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x14d8:
        r11 = r18;
        r7 = r15.length;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        if (r7 <= r10) goto L_0x151b;
    L_0x14de:
        r7 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7 = android.text.TextUtils.isEmpty(r7);	 Catch:{ all -> 0x0419 }
        if (r7 != 0) goto L_0x151b;
    L_0x14e6:
        r7 = "NotificationMessageStickerEmoji";
        r10 = NUM; // 0x7f0d06cd float:1.8745646E38 double:1.0531306377E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x0419 }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ all -> 0x0419 }
        r14[r16] = r17;	 Catch:{ all -> 0x0419 }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ all -> 0x0419 }
        r14[r16] = r17;	 Catch:{ all -> 0x0419 }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r10, r14);	 Catch:{ all -> 0x0419 }
        r10 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0419 }
        r10.<init>();	 Catch:{ all -> 0x0419 }
        r14 = r15[r16];	 Catch:{ all -> 0x0419 }
        r10.append(r14);	 Catch:{ all -> 0x0419 }
        r10.append(r6);	 Catch:{ all -> 0x0419 }
        r6 = NUM; // 0x7f0d014e float:1.8742792E38 double:1.0531299426E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r6);	 Catch:{ all -> 0x0419 }
        r10.append(r2);	 Catch:{ all -> 0x0419 }
        r2 = r10.toString();	 Catch:{ all -> 0x0419 }
        goto L_0x0fff;
    L_0x151b:
        r6 = "NotificationMessageSticker";
        r7 = NUM; // 0x7f0d06cc float:1.8745644E38 double:1.053130637E-314;
        r10 = 1;
        r14 = new java.lang.Object[r10];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r15 = r15[r10];	 Catch:{ all -> 0x0419 }
        r14[r10] = r15;	 Catch:{ all -> 0x0419 }
        r6 = org.telegram.messenger.LocaleController.formatString(r6, r7, r14);	 Catch:{ all -> 0x0419 }
        r7 = NUM; // 0x7f0d014e float:1.8742792E38 double:1.0531299426E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r7);	 Catch:{ all -> 0x0419 }
    L_0x1533:
        r16 = r2;
        r2 = r6;
        goto L_0x161d;
    L_0x1538:
        r11 = r18;
        r2 = "NotificationMessageDocument";
        r6 = NUM; // 0x7f0d06a8 float:1.874557E38 double:1.0531306194E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachDocument";
        r7 = NUM; // 0x7f0d013e float:1.874276E38 double:1.0531299347E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x1556:
        r11 = r18;
        r2 = "NotificationMessageRound";
        r6 = NUM; // 0x7f0d06c7 float:1.8745634E38 double:1.0531306347E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachRound";
        r7 = NUM; // 0x7f0d014d float:1.874279E38 double:1.053129942E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x1574:
        r11 = r18;
        r2 = "ActionTakeScreenshoot";
        r6 = NUM; // 0x7f0d008e float:1.8742403E38 double:1.0531298477E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r6);	 Catch:{ all -> 0x0419 }
        r6 = "un1";
        r7 = 0;
        r10 = r15[r7];	 Catch:{ all -> 0x0419 }
        r2 = r2.replace(r6, r10);	 Catch:{ all -> 0x0419 }
        goto L_0x1651;
    L_0x158a:
        r11 = r18;
        r2 = "NotificationMessageSDVideo";
        r6 = NUM; // 0x7f0d06c9 float:1.8745638E38 double:1.0531306357E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachDestructingVideo";
        r7 = NUM; // 0x7f0d013d float:1.8742758E38 double:1.053129934E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x15a8:
        r11 = r18;
        r2 = "NotificationMessageVideo";
        r6 = NUM; // 0x7f0d06cf float:1.874565E38 double:1.0531306387E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachVideo";
        r7 = NUM; // 0x7f0d0151 float:1.8742798E38 double:1.053129944E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x15c5:
        r11 = r18;
        r2 = "NotificationMessageSDPhoto";
        r6 = NUM; // 0x7f0d06c8 float:1.8745636E38 double:1.053130635E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachDestructingPhoto";
        r7 = NUM; // 0x7f0d013c float:1.8742756E38 double:1.0531299337E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x15e2:
        r11 = r18;
        r2 = "NotificationMessagePhoto";
        r6 = NUM; // 0x7f0d06c5 float:1.874563E38 double:1.053130634E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "AttachPhoto";
        r7 = NUM; // 0x7f0d014b float:1.8742786E38 double:1.053129941E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x15ff:
        r11 = r18;
        r2 = "NotificationMessageNoText";
        r6 = NUM; // 0x7f0d06c4 float:1.8745628E38 double:1.0531306333E-314;
        r7 = 1;
        r10 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r7 = 0;
        r14 = r15[r7];	 Catch:{ all -> 0x0419 }
        r10[r7] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r10);	 Catch:{ all -> 0x0419 }
        r6 = "Message";
        r7 = NUM; // 0x7f0d05e6 float:1.8745177E38 double:1.0531305236E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ all -> 0x0419 }
    L_0x161b:
        r16 = r6;
    L_0x161d:
        r6 = 0;
        goto L_0x1654;
    L_0x161f:
        r11 = r18;
        r2 = "NotificationMessageText";
        r6 = NUM; // 0x7f0d06ce float:1.8745648E38 double:1.053130638E-314;
        r7 = 2;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0419 }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r10 = 1;
        r14 = r15[r10];	 Catch:{ all -> 0x0419 }
        r7[r10] = r14;	 Catch:{ all -> 0x0419 }
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7);	 Catch:{ all -> 0x0419 }
        r6 = r15[r10];	 Catch:{ all -> 0x0419 }
        goto L_0x161b;
    L_0x163a:
        if (r2 == 0) goto L_0x1650;
    L_0x163c:
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0419 }
        r2.<init>();	 Catch:{ all -> 0x0419 }
        r6 = "unhandled loc_key = ";
        r2.append(r6);	 Catch:{ all -> 0x0419 }
        r2.append(r9);	 Catch:{ all -> 0x0419 }
        r2 = r2.toString();	 Catch:{ all -> 0x0419 }
        org.telegram.messenger.FileLog.w(r2);	 Catch:{ all -> 0x0419 }
    L_0x1650:
        r2 = 0;
    L_0x1651:
        r6 = 0;
    L_0x1652:
        r16 = 0;
    L_0x1654:
        if (r2 == 0) goto L_0x16f1;
    L_0x1656:
        r7 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ all -> 0x16f9 }
        r7.<init>();	 Catch:{ all -> 0x16f9 }
        r7.id = r11;	 Catch:{ all -> 0x16f9 }
        r7.random_id = r3;	 Catch:{ all -> 0x16f9 }
        if (r16 == 0) goto L_0x1664;
    L_0x1661:
        r3 = r16;
        goto L_0x1665;
    L_0x1664:
        r3 = r2;
    L_0x1665:
        r7.message = r3;	 Catch:{ all -> 0x16f9 }
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r3 = r37 / r3;
        r4 = (int) r3;	 Catch:{ all -> 0x16f9 }
        r7.date = r4;	 Catch:{ all -> 0x16f9 }
        if (r5 == 0) goto L_0x1677;
    L_0x1670:
        r3 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage;	 Catch:{ all -> 0x0419 }
        r3.<init>();	 Catch:{ all -> 0x0419 }
        r7.action = r3;	 Catch:{ all -> 0x0419 }
    L_0x1677:
        if (r1 == 0) goto L_0x1680;
    L_0x1679:
        r1 = r7.flags;	 Catch:{ all -> 0x0419 }
        r3 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r1 = r1 | r3;
        r7.flags = r1;	 Catch:{ all -> 0x0419 }
    L_0x1680:
        r7.dialog_id = r12;	 Catch:{ all -> 0x16f9 }
        if (r32 == 0) goto L_0x1692;
    L_0x1684:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel;	 Catch:{ all -> 0x0419 }
        r1.<init>();	 Catch:{ all -> 0x0419 }
        r7.to_id = r1;	 Catch:{ all -> 0x0419 }
        r1 = r7.to_id;	 Catch:{ all -> 0x0419 }
        r3 = r32;
        r1.channel_id = r3;	 Catch:{ all -> 0x0419 }
        goto L_0x16af;
    L_0x1692:
        if (r23 == 0) goto L_0x16a2;
    L_0x1694:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ all -> 0x0419 }
        r1.<init>();	 Catch:{ all -> 0x0419 }
        r7.to_id = r1;	 Catch:{ all -> 0x0419 }
        r1 = r7.to_id;	 Catch:{ all -> 0x0419 }
        r3 = r23;
        r1.chat_id = r3;	 Catch:{ all -> 0x0419 }
        goto L_0x16af;
    L_0x16a2:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ all -> 0x16f9 }
        r1.<init>();	 Catch:{ all -> 0x16f9 }
        r7.to_id = r1;	 Catch:{ all -> 0x16f9 }
        r1 = r7.to_id;	 Catch:{ all -> 0x16f9 }
        r3 = r31;
        r1.user_id = r3;	 Catch:{ all -> 0x16f9 }
    L_0x16af:
        r1 = r7.flags;	 Catch:{ all -> 0x16f9 }
        r1 = r1 | 256;
        r7.flags = r1;	 Catch:{ all -> 0x16f9 }
        r7.from_id = r8;	 Catch:{ all -> 0x16f9 }
        if (r20 != 0) goto L_0x16be;
    L_0x16b9:
        if (r5 == 0) goto L_0x16bc;
    L_0x16bb:
        goto L_0x16be;
    L_0x16bc:
        r1 = 0;
        goto L_0x16bf;
    L_0x16be:
        r1 = 1;
    L_0x16bf:
        r7.mentioned = r1;	 Catch:{ all -> 0x16f9 }
        r1 = r19;
        r7.silent = r1;	 Catch:{ all -> 0x16f9 }
        r4 = r24;
        r7.from_scheduled = r4;	 Catch:{ all -> 0x16f9 }
        r1 = new org.telegram.messenger.MessageObject;	 Catch:{ all -> 0x16f9 }
        r19 = r1;
        r20 = r29;
        r21 = r7;
        r22 = r2;
        r23 = r25;
        r24 = r30;
        r25 = r6;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27);	 Catch:{ all -> 0x16f9 }
        r2 = new java.util.ArrayList;	 Catch:{ all -> 0x16f9 }
        r2.<init>();	 Catch:{ all -> 0x16f9 }
        r2.add(r1);	 Catch:{ all -> 0x16f9 }
        r1 = org.telegram.messenger.NotificationsController.getInstance(r29);	 Catch:{ all -> 0x16f9 }
        r3 = r35;
        r4 = r3.countDownLatch;	 Catch:{ all -> 0x171c }
        r5 = 1;
        r1.processNewMessages(r2, r5, r5, r4);	 Catch:{ all -> 0x171c }
        goto L_0x1710;
    L_0x16f1:
        r3 = r35;
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x171c }
        r1.countDown();	 Catch:{ all -> 0x171c }
        goto L_0x1710;
    L_0x16f9:
        r0 = move-exception;
        r3 = r35;
        goto L_0x171d;
    L_0x16fd:
        r0 = move-exception;
        r3 = r35;
        goto L_0x1731;
    L_0x1701:
        r3 = r35;
        r29 = r15;
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x171c }
        r1.countDown();	 Catch:{ all -> 0x171c }
        goto L_0x1710;
    L_0x170b:
        r3 = r1;
        r28 = r14;
        r29 = r15;
    L_0x1710:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r29);	 Catch:{ all -> 0x171c }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r29);	 Catch:{ all -> 0x171c }
        r1.resumeNetworkMaybe();	 Catch:{ all -> 0x171c }
        goto L_0x1830;
    L_0x171c:
        r0 = move-exception;
    L_0x171d:
        r1 = r0;
        r14 = r28;
        r15 = r29;
        goto L_0x17dc;
    L_0x1724:
        r0 = move-exception;
        r3 = r1;
        r28 = r14;
        r29 = r15;
        r1 = r0;
        goto L_0x17dc;
    L_0x172d:
        r0 = move-exception;
        r3 = r1;
        r28 = r7;
    L_0x1731:
        r29 = r15;
        goto L_0x17d9;
    L_0x1735:
        r3 = r1;
        r28 = r7;
        r29 = r15;
        r1 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ;	 Catch:{ all -> 0x1746 }
        r1.<init>(r15);	 Catch:{ all -> 0x1743 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);	 Catch:{ all -> 0x17d3 }
        return;
    L_0x1743:
        r0 = move-exception;
        goto L_0x17d9;
    L_0x1746:
        r0 = move-exception;
        r15 = r29;
        goto L_0x17d9;
    L_0x174b:
        r3 = r1;
        r28 = r7;
        r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification;	 Catch:{ all -> 0x17d3 }
        r1.<init>();	 Catch:{ all -> 0x17d3 }
        r2 = 0;
        r1.popup = r2;	 Catch:{ all -> 0x17d3 }
        r2 = 2;
        r1.flags = r2;	 Catch:{ all -> 0x17d3 }
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6 = r37 / r6;
        r2 = (int) r6;	 Catch:{ all -> 0x17d3 }
        r1.inbox_date = r2;	 Catch:{ all -> 0x17d3 }
        r2 = "message";
        r2 = r5.getString(r2);	 Catch:{ all -> 0x17d3 }
        r1.message = r2;	 Catch:{ all -> 0x17d3 }
        r2 = "announcement";
        r1.type = r2;	 Catch:{ all -> 0x17d3 }
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;	 Catch:{ all -> 0x17d3 }
        r2.<init>();	 Catch:{ all -> 0x17d3 }
        r1.media = r2;	 Catch:{ all -> 0x17d3 }
        r2 = new org.telegram.tgnet.TLRPC$TL_updates;	 Catch:{ all -> 0x17d3 }
        r2.<init>();	 Catch:{ all -> 0x17d3 }
        r4 = r2.updates;	 Catch:{ all -> 0x17d3 }
        r4.add(r1);	 Catch:{ all -> 0x17d3 }
        r1 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ all -> 0x17d3 }
        r4 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo;	 Catch:{ all -> 0x1794 }
        r4.<init>(r15, r2);	 Catch:{ all -> 0x1794 }
        r1.postRunnable(r4);	 Catch:{ all -> 0x17d3 }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ all -> 0x17d3 }
        r1.resumeNetworkMaybe();	 Catch:{ all -> 0x17d3 }
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x17d3 }
        r1.countDown();	 Catch:{ all -> 0x17d3 }
        return;
    L_0x1794:
        r0 = move-exception;
        goto L_0x17d9;
    L_0x1796:
        r3 = r1;
        r28 = r7;
        r1 = "dc";
        r1 = r11.getInt(r1);	 Catch:{ all -> 0x17d3 }
        r2 = "addr";
        r2 = r11.getString(r2);	 Catch:{ all -> 0x17d3 }
        r4 = ":";
        r2 = r2.split(r4);	 Catch:{ all -> 0x17d3 }
        r4 = r2.length;	 Catch:{ all -> 0x17d3 }
        r5 = 2;
        if (r4 == r5) goto L_0x17b5;
    L_0x17af:
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x17d3 }
        r1.countDown();	 Catch:{ all -> 0x17d3 }
        return;
    L_0x17b5:
        r4 = 0;
        r4 = r2[r4];	 Catch:{ all -> 0x17d3 }
        r5 = 1;
        r2 = r2[r5];	 Catch:{ all -> 0x17d3 }
        r2 = java.lang.Integer.parseInt(r2);	 Catch:{ all -> 0x17d3 }
        r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ all -> 0x17d3 }
        r5.applyDatacenterAddress(r1, r4, r2);	 Catch:{ all -> 0x17d3 }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ all -> 0x17d3 }
        r1.resumeNetworkMaybe();	 Catch:{ all -> 0x17d3 }
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x17d3 }
        r1.countDown();	 Catch:{ all -> 0x17d3 }
        return;
    L_0x17d3:
        r0 = move-exception;
        goto L_0x17d9;
    L_0x17d5:
        r0 = move-exception;
        r3 = r1;
        r28 = r7;
    L_0x17d9:
        r1 = r0;
        r14 = r28;
    L_0x17dc:
        r2 = -1;
        goto L_0x17f8;
    L_0x17de:
        r0 = move-exception;
        r3 = r1;
        r28 = r7;
        r1 = r0;
        r14 = r28;
        r2 = -1;
        goto L_0x17f7;
    L_0x17e7:
        r0 = move-exception;
        r3 = r1;
        r28 = r7;
        r1 = r0;
        r14 = r28;
        r2 = -1;
        r9 = 0;
        goto L_0x17f7;
    L_0x17f1:
        r0 = move-exception;
        r3 = r1;
        r1 = r0;
        r2 = -1;
        r9 = 0;
        r14 = 0;
    L_0x17f7:
        r15 = -1;
    L_0x17f8:
        if (r15 == r2) goto L_0x180a;
    L_0x17fa:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15);
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);
        r2.resumeNetworkMaybe();
        r2 = r3.countDownLatch;
        r2.countDown();
        goto L_0x180d;
    L_0x180a:
        r35.onDecryptError();
    L_0x180d:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x182d;
    L_0x1811:
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
    L_0x182d:
        org.telegram.messenger.FileLog.e(r1);
    L_0x1830:
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
