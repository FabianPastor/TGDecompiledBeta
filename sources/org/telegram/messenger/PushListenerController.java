package org.telegram.messenger;

import android.os.SystemClock;
import android.text.TextUtils;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.PushListenerController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_saveAppLog;
import org.telegram.tgnet.TLRPC$TL_inputAppEvent;
import org.telegram.tgnet.TLRPC$TL_jsonNull;
import org.telegram.tgnet.TLRPC$TL_updates;
/* loaded from: classes.dex */
public class PushListenerController {
    public static final int NOTIFICATION_ID = 1;
    public static final int PUSH_TYPE_FIREBASE = 2;
    public static final int PUSH_TYPE_HUAWEI = 13;
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    /* loaded from: classes.dex */
    public interface IPushListenerServiceProvider {
        String getLogTitle();

        int getPushType();

        boolean hasServices();

        void onRequestPushToken();
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PushType {
    }

    public static void sendRegistrationToServer(final int i, final String str) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                PushListenerController.lambda$sendRegistrationToServer$3(str, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendRegistrationToServer$3(final String str, final int i) {
        boolean z;
        ConnectionsManager.setRegId(str, i, SharedConfig.pushStringStatus);
        if (str == null) {
            return;
        }
        if (SharedConfig.pushStringGetTimeStart == 0 || SharedConfig.pushStringGetTimeEnd == 0 || (SharedConfig.pushStatSent && TextUtils.equals(SharedConfig.pushString, str))) {
            z = false;
        } else {
            SharedConfig.pushStatSent = false;
            z = true;
        }
        SharedConfig.pushString = str;
        SharedConfig.pushType = i;
        for (final int i2 = 0; i2 < 4; i2++) {
            UserConfig userConfig = UserConfig.getInstance(i2);
            userConfig.registeredForPush = false;
            userConfig.saveConfig(false);
            if (userConfig.getClientUserId() != 0) {
                if (z) {
                    String str2 = i == 2 ? "fcm" : "hcm";
                    TLRPC$TL_help_saveAppLog tLRPC$TL_help_saveAppLog = new TLRPC$TL_help_saveAppLog();
                    TLRPC$TL_inputAppEvent tLRPC$TL_inputAppEvent = new TLRPC$TL_inputAppEvent();
                    tLRPC$TL_inputAppEvent.time = SharedConfig.pushStringGetTimeStart;
                    tLRPC$TL_inputAppEvent.type = str2 + "_token_request";
                    tLRPC$TL_inputAppEvent.peer = 0L;
                    tLRPC$TL_inputAppEvent.data = new TLRPC$TL_jsonNull();
                    tLRPC$TL_help_saveAppLog.events.add(tLRPC$TL_inputAppEvent);
                    TLRPC$TL_inputAppEvent tLRPC$TL_inputAppEvent2 = new TLRPC$TL_inputAppEvent();
                    tLRPC$TL_inputAppEvent2.time = SharedConfig.pushStringGetTimeEnd;
                    tLRPC$TL_inputAppEvent2.type = str2 + "_token_response";
                    tLRPC$TL_inputAppEvent2.peer = SharedConfig.pushStringGetTimeEnd - SharedConfig.pushStringGetTimeStart;
                    tLRPC$TL_inputAppEvent2.data = new TLRPC$TL_jsonNull();
                    tLRPC$TL_help_saveAppLog.events.add(tLRPC$TL_inputAppEvent2);
                    ConnectionsManager.getInstance(i2).sendRequest(tLRPC$TL_help_saveAppLog, PushListenerController$$ExternalSyntheticLambda8.INSTANCE);
                    z = false;
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        PushListenerController.lambda$sendRegistrationToServer$2(i2, i, str);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendRegistrationToServer$1(TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                PushListenerController.lambda$sendRegistrationToServer$0(TLRPC$TL_error.this);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendRegistrationToServer$0(TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            SharedConfig.pushStatSent = true;
            SharedConfig.saveConfig();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendRegistrationToServer$2(int i, int i2, String str) {
        MessagesController.getInstance(i).registerForPush(i2, str);
    }

    public static void processRemoteMessage(int i, final String str, final long j) {
        final String str2 = i == 2 ? "FCM" : "HCM";
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d(str2 + " PRE START PROCESSING");
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                PushListenerController.lambda$processRemoteMessage$8(str2, str, j);
            }
        });
        try {
            countDownLatch.await();
        } catch (Throwable unused) {
        }
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("finished " + str2 + " service, time = " + (SystemClock.elapsedRealtime() - elapsedRealtime));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$processRemoteMessage$8(final String str, final String str2, final long j) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d(str + " PRE INIT APP");
        }
        ApplicationLoader.postInitApplication();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d(str + " POST INIT APP");
        }
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                PushListenerController.lambda$processRemoteMessage$7(str, str2, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:196:0x04bf, code lost:
        if (r7 > r12.intValue()) goto L188;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:232:0x0545 A[Catch: all -> 0x1f6f, TryCatch #7 {all -> 0x1f6f, blocks: (B:161:0x0351, B:164:0x035f, B:165:0x0372, B:167:0x0375, B:168:0x0381, B:170:0x03a0, B:908:0x1f5e, B:909:0x1var_, B:171:0x03cd, B:173:0x03d6, B:174:0x03ee, B:176:0x03f1, B:177:0x040d, B:179:0x0424, B:180:0x044f, B:182:0x0455, B:184:0x045d, B:186:0x0465, B:188:0x046d, B:191:0x0488, B:193:0x049c, B:195:0x04bb, B:204:0x04da, B:207:0x04e2, B:211:0x04eb, B:218:0x0511, B:220:0x0519, B:224:0x0524, B:226:0x052c, B:230:0x053b, B:232:0x0545, B:234:0x0558, B:237:0x0568, B:239:0x0579, B:241:0x057f, B:259:0x05d9, B:261:0x05dd, B:263:0x0617, B:265:0x061f, B:859:0x1e4c, B:863:0x1e55, B:867:0x1e66, B:869:0x1e71, B:871:0x1e7a, B:872:0x1e81, B:874:0x1e89, B:879:0x1eb6, B:881:0x1ec2, B:894:0x1efc, B:896:0x1f1f, B:897:0x1var_, B:899:0x1var_, B:904:0x1var_, B:884:0x1ed2, B:887:0x1ee4, B:888:0x1ef0, B:877:0x1e9d, B:878:0x1ea9, B:268:0x0638, B:269:0x063c, B:621:0x0bc7, B:856:0x1e26, B:623:0x0bd3, B:626:0x0bf3, B:629:0x0CLASSNAME, B:630:0x0c2f, B:633:0x0c4d, B:635:0x0CLASSNAME, B:636:0x0c7d, B:639:0x0c9b, B:641:0x0cb4, B:642:0x0ccb, B:645:0x0ce9, B:647:0x0d02, B:648:0x0d19, B:651:0x0d37, B:653:0x0d50, B:654:0x0d67, B:657:0x0d85, B:659:0x0d9e, B:660:0x0db5, B:663:0x0dd3, B:665:0x0dec, B:666:0x0e08, B:669:0x0e2b, B:671:0x0e44, B:672:0x0e60, B:675:0x0e83, B:677:0x0e9c, B:678:0x0eb8, B:681:0x0edb, B:683:0x0ef4, B:684:0x0f0b, B:687:0x0var_, B:689:0x0f2d, B:691:0x0var_, B:692:0x0f4c, B:694:0x0var_, B:696:0x0var_, B:698:0x0f6c, B:699:0x0var_, B:700:0x0f9f, B:702:0x0fa3, B:704:0x0fab, B:705:0x0fc2, B:708:0x0fe0, B:710:0x0ff9, B:711:0x1010, B:714:0x102e, B:716:0x1047, B:717:0x105e, B:720:0x107c, B:722:0x1095, B:723:0x10ac, B:726:0x10ca, B:728:0x10e3, B:729:0x10fa, B:732:0x1118, B:734:0x1131, B:735:0x1148, B:738:0x1166, B:740:0x117f, B:741:0x119b, B:742:0x11b2, B:744:0x11d1, B:745:0x1201, B:746:0x122f, B:747:0x125e, B:748:0x128d, B:749:0x12c0, B:750:0x12dd, B:751:0x12fa, B:752:0x1317, B:753:0x1334, B:754:0x1351, B:755:0x136e, B:756:0x138b, B:757:0x13a8, B:758:0x13ca, B:759:0x13e7, B:760:0x1408, B:761:0x1424, B:762:0x1440, B:764:0x1460, B:765:0x148a, B:766:0x14b0, B:767:0x14da, B:768:0x14ff, B:769:0x1524, B:770:0x1549, B:771:0x1573, B:772:0x159c, B:773:0x15c5, B:775:0x15ee, B:777:0x15f8, B:779:0x1600, B:780:0x1636, B:781:0x1667, B:782:0x168c, B:783:0x16b0, B:784:0x16d4, B:785:0x16f8, B:787:0x171e, B:790:0x1746, B:791:0x175f, B:792:0x178a, B:793:0x17b3, B:794:0x17dc, B:795:0x1805, B:796:0x1835, B:797:0x1855, B:798:0x1875, B:799:0x1895, B:800:0x18b5, B:801:0x18da, B:802:0x18ff, B:803:0x1924, B:804:0x1944, B:806:0x194e, B:808:0x1956, B:809:0x1987, B:810:0x199f, B:811:0x19bf, B:812:0x19de, B:813:0x19fd, B:814:0x1a1c, B:817:0x1a41, B:821:0x1a5c, B:822:0x1a87, B:823:0x1aaf, B:824:0x1ad7, B:826:0x1b01, B:827:0x1b2e, B:828:0x1b53, B:829:0x1b75, B:830:0x1b9a, B:831:0x1bba, B:832:0x1bda, B:833:0x1bfa, B:834:0x1c1f, B:835:0x1CLASSNAME, B:836:0x1CLASSNAME, B:837:0x1CLASSNAME, B:839:0x1CLASSNAME, B:841:0x1c9b, B:842:0x1ccf, B:843:0x1ce7, B:844:0x1d07, B:845:0x1d27, B:846:0x1d40, B:847:0x1d60, B:848:0x1d7f, B:849:0x1d9e, B:850:0x1dbd, B:852:0x1dde, B:854:0x1e00, B:271:0x0641, B:274:0x064d, B:277:0x0659, B:280:0x0665, B:283:0x0671, B:286:0x067d, B:289:0x0689, B:292:0x0695, B:295:0x06a1, B:298:0x06ad, B:301:0x06b9, B:304:0x06c5, B:307:0x06d1, B:310:0x06dd, B:313:0x06e9, B:316:0x06f5, B:319:0x0701, B:322:0x070d, B:325:0x0719, B:328:0x0725, B:331:0x0732, B:334:0x073e, B:337:0x074a, B:340:0x0756, B:343:0x0762, B:346:0x076e, B:349:0x077a, B:352:0x0786, B:355:0x0792, B:358:0x079e, B:361:0x07ab, B:364:0x07b7, B:367:0x07c3, B:370:0x07cf, B:373:0x07db, B:376:0x07e7, B:379:0x07f3, B:382:0x07ff, B:385:0x080b, B:388:0x0817, B:391:0x0823, B:394:0x082f, B:397:0x083b, B:400:0x0847, B:403:0x0853, B:406:0x085f, B:409:0x086b, B:412:0x0877, B:415:0x0883, B:418:0x088e, B:421:0x089a, B:424:0x08a6, B:427:0x08b2, B:430:0x08be, B:433:0x08ca, B:436:0x08d6, B:439:0x08e2, B:442:0x08ee, B:445:0x08fa, B:448:0x0906, B:451:0x0912, B:454:0x091e, B:457:0x092a, B:460:0x0936, B:463:0x0942, B:466:0x094e, B:469:0x095c, B:472:0x0968, B:475:0x0974, B:478:0x0980, B:481:0x098c, B:484:0x0998, B:487:0x09a4, B:490:0x09b0, B:493:0x09bc, B:496:0x09c8, B:499:0x09d4, B:502:0x09e0, B:505:0x09ec, B:508:0x09f8, B:511:0x0a04, B:514:0x0a10, B:517:0x0a1c, B:520:0x0a28, B:523:0x0a34, B:526:0x0a40, B:529:0x0a4b, B:532:0x0a58, B:535:0x0a64, B:538:0x0a70, B:541:0x0a7c, B:544:0x0a88, B:547:0x0a94, B:550:0x0aa0, B:553:0x0aac, B:556:0x0ab8, B:559:0x0ac4, B:562:0x0acf, B:565:0x0adb, B:568:0x0ae8, B:571:0x0af4, B:574:0x0b00, B:577:0x0b0d, B:580:0x0b19, B:583:0x0b25, B:586:0x0b31, B:589:0x0b3c, B:592:0x0b47, B:595:0x0b52, B:598:0x0b5d, B:601:0x0b68, B:604:0x0b73, B:607:0x0b7e, B:610:0x0b89, B:613:0x0b94, B:246:0x05a0, B:247:0x05ad, B:254:0x05c6, B:201:0x04cf), top: B:974:0x0351 }] */
    /* JADX WARN: Removed duplicated region for block: B:236:0x0563  */
    /* JADX WARN: Removed duplicated region for block: B:239:0x0579 A[Catch: all -> 0x1f6f, TryCatch #7 {all -> 0x1f6f, blocks: (B:161:0x0351, B:164:0x035f, B:165:0x0372, B:167:0x0375, B:168:0x0381, B:170:0x03a0, B:908:0x1f5e, B:909:0x1var_, B:171:0x03cd, B:173:0x03d6, B:174:0x03ee, B:176:0x03f1, B:177:0x040d, B:179:0x0424, B:180:0x044f, B:182:0x0455, B:184:0x045d, B:186:0x0465, B:188:0x046d, B:191:0x0488, B:193:0x049c, B:195:0x04bb, B:204:0x04da, B:207:0x04e2, B:211:0x04eb, B:218:0x0511, B:220:0x0519, B:224:0x0524, B:226:0x052c, B:230:0x053b, B:232:0x0545, B:234:0x0558, B:237:0x0568, B:239:0x0579, B:241:0x057f, B:259:0x05d9, B:261:0x05dd, B:263:0x0617, B:265:0x061f, B:859:0x1e4c, B:863:0x1e55, B:867:0x1e66, B:869:0x1e71, B:871:0x1e7a, B:872:0x1e81, B:874:0x1e89, B:879:0x1eb6, B:881:0x1ec2, B:894:0x1efc, B:896:0x1f1f, B:897:0x1var_, B:899:0x1var_, B:904:0x1var_, B:884:0x1ed2, B:887:0x1ee4, B:888:0x1ef0, B:877:0x1e9d, B:878:0x1ea9, B:268:0x0638, B:269:0x063c, B:621:0x0bc7, B:856:0x1e26, B:623:0x0bd3, B:626:0x0bf3, B:629:0x0CLASSNAME, B:630:0x0c2f, B:633:0x0c4d, B:635:0x0CLASSNAME, B:636:0x0c7d, B:639:0x0c9b, B:641:0x0cb4, B:642:0x0ccb, B:645:0x0ce9, B:647:0x0d02, B:648:0x0d19, B:651:0x0d37, B:653:0x0d50, B:654:0x0d67, B:657:0x0d85, B:659:0x0d9e, B:660:0x0db5, B:663:0x0dd3, B:665:0x0dec, B:666:0x0e08, B:669:0x0e2b, B:671:0x0e44, B:672:0x0e60, B:675:0x0e83, B:677:0x0e9c, B:678:0x0eb8, B:681:0x0edb, B:683:0x0ef4, B:684:0x0f0b, B:687:0x0var_, B:689:0x0f2d, B:691:0x0var_, B:692:0x0f4c, B:694:0x0var_, B:696:0x0var_, B:698:0x0f6c, B:699:0x0var_, B:700:0x0f9f, B:702:0x0fa3, B:704:0x0fab, B:705:0x0fc2, B:708:0x0fe0, B:710:0x0ff9, B:711:0x1010, B:714:0x102e, B:716:0x1047, B:717:0x105e, B:720:0x107c, B:722:0x1095, B:723:0x10ac, B:726:0x10ca, B:728:0x10e3, B:729:0x10fa, B:732:0x1118, B:734:0x1131, B:735:0x1148, B:738:0x1166, B:740:0x117f, B:741:0x119b, B:742:0x11b2, B:744:0x11d1, B:745:0x1201, B:746:0x122f, B:747:0x125e, B:748:0x128d, B:749:0x12c0, B:750:0x12dd, B:751:0x12fa, B:752:0x1317, B:753:0x1334, B:754:0x1351, B:755:0x136e, B:756:0x138b, B:757:0x13a8, B:758:0x13ca, B:759:0x13e7, B:760:0x1408, B:761:0x1424, B:762:0x1440, B:764:0x1460, B:765:0x148a, B:766:0x14b0, B:767:0x14da, B:768:0x14ff, B:769:0x1524, B:770:0x1549, B:771:0x1573, B:772:0x159c, B:773:0x15c5, B:775:0x15ee, B:777:0x15f8, B:779:0x1600, B:780:0x1636, B:781:0x1667, B:782:0x168c, B:783:0x16b0, B:784:0x16d4, B:785:0x16f8, B:787:0x171e, B:790:0x1746, B:791:0x175f, B:792:0x178a, B:793:0x17b3, B:794:0x17dc, B:795:0x1805, B:796:0x1835, B:797:0x1855, B:798:0x1875, B:799:0x1895, B:800:0x18b5, B:801:0x18da, B:802:0x18ff, B:803:0x1924, B:804:0x1944, B:806:0x194e, B:808:0x1956, B:809:0x1987, B:810:0x199f, B:811:0x19bf, B:812:0x19de, B:813:0x19fd, B:814:0x1a1c, B:817:0x1a41, B:821:0x1a5c, B:822:0x1a87, B:823:0x1aaf, B:824:0x1ad7, B:826:0x1b01, B:827:0x1b2e, B:828:0x1b53, B:829:0x1b75, B:830:0x1b9a, B:831:0x1bba, B:832:0x1bda, B:833:0x1bfa, B:834:0x1c1f, B:835:0x1CLASSNAME, B:836:0x1CLASSNAME, B:837:0x1CLASSNAME, B:839:0x1CLASSNAME, B:841:0x1c9b, B:842:0x1ccf, B:843:0x1ce7, B:844:0x1d07, B:845:0x1d27, B:846:0x1d40, B:847:0x1d60, B:848:0x1d7f, B:849:0x1d9e, B:850:0x1dbd, B:852:0x1dde, B:854:0x1e00, B:271:0x0641, B:274:0x064d, B:277:0x0659, B:280:0x0665, B:283:0x0671, B:286:0x067d, B:289:0x0689, B:292:0x0695, B:295:0x06a1, B:298:0x06ad, B:301:0x06b9, B:304:0x06c5, B:307:0x06d1, B:310:0x06dd, B:313:0x06e9, B:316:0x06f5, B:319:0x0701, B:322:0x070d, B:325:0x0719, B:328:0x0725, B:331:0x0732, B:334:0x073e, B:337:0x074a, B:340:0x0756, B:343:0x0762, B:346:0x076e, B:349:0x077a, B:352:0x0786, B:355:0x0792, B:358:0x079e, B:361:0x07ab, B:364:0x07b7, B:367:0x07c3, B:370:0x07cf, B:373:0x07db, B:376:0x07e7, B:379:0x07f3, B:382:0x07ff, B:385:0x080b, B:388:0x0817, B:391:0x0823, B:394:0x082f, B:397:0x083b, B:400:0x0847, B:403:0x0853, B:406:0x085f, B:409:0x086b, B:412:0x0877, B:415:0x0883, B:418:0x088e, B:421:0x089a, B:424:0x08a6, B:427:0x08b2, B:430:0x08be, B:433:0x08ca, B:436:0x08d6, B:439:0x08e2, B:442:0x08ee, B:445:0x08fa, B:448:0x0906, B:451:0x0912, B:454:0x091e, B:457:0x092a, B:460:0x0936, B:463:0x0942, B:466:0x094e, B:469:0x095c, B:472:0x0968, B:475:0x0974, B:478:0x0980, B:481:0x098c, B:484:0x0998, B:487:0x09a4, B:490:0x09b0, B:493:0x09bc, B:496:0x09c8, B:499:0x09d4, B:502:0x09e0, B:505:0x09ec, B:508:0x09f8, B:511:0x0a04, B:514:0x0a10, B:517:0x0a1c, B:520:0x0a28, B:523:0x0a34, B:526:0x0a40, B:529:0x0a4b, B:532:0x0a58, B:535:0x0a64, B:538:0x0a70, B:541:0x0a7c, B:544:0x0a88, B:547:0x0a94, B:550:0x0aa0, B:553:0x0aac, B:556:0x0ab8, B:559:0x0ac4, B:562:0x0acf, B:565:0x0adb, B:568:0x0ae8, B:571:0x0af4, B:574:0x0b00, B:577:0x0b0d, B:580:0x0b19, B:583:0x0b25, B:586:0x0b31, B:589:0x0b3c, B:592:0x0b47, B:595:0x0b52, B:598:0x0b5d, B:601:0x0b68, B:604:0x0b73, B:607:0x0b7e, B:610:0x0b89, B:613:0x0b94, B:246:0x05a0, B:247:0x05ad, B:254:0x05c6, B:201:0x04cf), top: B:974:0x0351 }] */
    /* JADX WARN: Removed duplicated region for block: B:247:0x05ad A[Catch: all -> 0x1f6f, TryCatch #7 {all -> 0x1f6f, blocks: (B:161:0x0351, B:164:0x035f, B:165:0x0372, B:167:0x0375, B:168:0x0381, B:170:0x03a0, B:908:0x1f5e, B:909:0x1var_, B:171:0x03cd, B:173:0x03d6, B:174:0x03ee, B:176:0x03f1, B:177:0x040d, B:179:0x0424, B:180:0x044f, B:182:0x0455, B:184:0x045d, B:186:0x0465, B:188:0x046d, B:191:0x0488, B:193:0x049c, B:195:0x04bb, B:204:0x04da, B:207:0x04e2, B:211:0x04eb, B:218:0x0511, B:220:0x0519, B:224:0x0524, B:226:0x052c, B:230:0x053b, B:232:0x0545, B:234:0x0558, B:237:0x0568, B:239:0x0579, B:241:0x057f, B:259:0x05d9, B:261:0x05dd, B:263:0x0617, B:265:0x061f, B:859:0x1e4c, B:863:0x1e55, B:867:0x1e66, B:869:0x1e71, B:871:0x1e7a, B:872:0x1e81, B:874:0x1e89, B:879:0x1eb6, B:881:0x1ec2, B:894:0x1efc, B:896:0x1f1f, B:897:0x1var_, B:899:0x1var_, B:904:0x1var_, B:884:0x1ed2, B:887:0x1ee4, B:888:0x1ef0, B:877:0x1e9d, B:878:0x1ea9, B:268:0x0638, B:269:0x063c, B:621:0x0bc7, B:856:0x1e26, B:623:0x0bd3, B:626:0x0bf3, B:629:0x0CLASSNAME, B:630:0x0c2f, B:633:0x0c4d, B:635:0x0CLASSNAME, B:636:0x0c7d, B:639:0x0c9b, B:641:0x0cb4, B:642:0x0ccb, B:645:0x0ce9, B:647:0x0d02, B:648:0x0d19, B:651:0x0d37, B:653:0x0d50, B:654:0x0d67, B:657:0x0d85, B:659:0x0d9e, B:660:0x0db5, B:663:0x0dd3, B:665:0x0dec, B:666:0x0e08, B:669:0x0e2b, B:671:0x0e44, B:672:0x0e60, B:675:0x0e83, B:677:0x0e9c, B:678:0x0eb8, B:681:0x0edb, B:683:0x0ef4, B:684:0x0f0b, B:687:0x0var_, B:689:0x0f2d, B:691:0x0var_, B:692:0x0f4c, B:694:0x0var_, B:696:0x0var_, B:698:0x0f6c, B:699:0x0var_, B:700:0x0f9f, B:702:0x0fa3, B:704:0x0fab, B:705:0x0fc2, B:708:0x0fe0, B:710:0x0ff9, B:711:0x1010, B:714:0x102e, B:716:0x1047, B:717:0x105e, B:720:0x107c, B:722:0x1095, B:723:0x10ac, B:726:0x10ca, B:728:0x10e3, B:729:0x10fa, B:732:0x1118, B:734:0x1131, B:735:0x1148, B:738:0x1166, B:740:0x117f, B:741:0x119b, B:742:0x11b2, B:744:0x11d1, B:745:0x1201, B:746:0x122f, B:747:0x125e, B:748:0x128d, B:749:0x12c0, B:750:0x12dd, B:751:0x12fa, B:752:0x1317, B:753:0x1334, B:754:0x1351, B:755:0x136e, B:756:0x138b, B:757:0x13a8, B:758:0x13ca, B:759:0x13e7, B:760:0x1408, B:761:0x1424, B:762:0x1440, B:764:0x1460, B:765:0x148a, B:766:0x14b0, B:767:0x14da, B:768:0x14ff, B:769:0x1524, B:770:0x1549, B:771:0x1573, B:772:0x159c, B:773:0x15c5, B:775:0x15ee, B:777:0x15f8, B:779:0x1600, B:780:0x1636, B:781:0x1667, B:782:0x168c, B:783:0x16b0, B:784:0x16d4, B:785:0x16f8, B:787:0x171e, B:790:0x1746, B:791:0x175f, B:792:0x178a, B:793:0x17b3, B:794:0x17dc, B:795:0x1805, B:796:0x1835, B:797:0x1855, B:798:0x1875, B:799:0x1895, B:800:0x18b5, B:801:0x18da, B:802:0x18ff, B:803:0x1924, B:804:0x1944, B:806:0x194e, B:808:0x1956, B:809:0x1987, B:810:0x199f, B:811:0x19bf, B:812:0x19de, B:813:0x19fd, B:814:0x1a1c, B:817:0x1a41, B:821:0x1a5c, B:822:0x1a87, B:823:0x1aaf, B:824:0x1ad7, B:826:0x1b01, B:827:0x1b2e, B:828:0x1b53, B:829:0x1b75, B:830:0x1b9a, B:831:0x1bba, B:832:0x1bda, B:833:0x1bfa, B:834:0x1c1f, B:835:0x1CLASSNAME, B:836:0x1CLASSNAME, B:837:0x1CLASSNAME, B:839:0x1CLASSNAME, B:841:0x1c9b, B:842:0x1ccf, B:843:0x1ce7, B:844:0x1d07, B:845:0x1d27, B:846:0x1d40, B:847:0x1d60, B:848:0x1d7f, B:849:0x1d9e, B:850:0x1dbd, B:852:0x1dde, B:854:0x1e00, B:271:0x0641, B:274:0x064d, B:277:0x0659, B:280:0x0665, B:283:0x0671, B:286:0x067d, B:289:0x0689, B:292:0x0695, B:295:0x06a1, B:298:0x06ad, B:301:0x06b9, B:304:0x06c5, B:307:0x06d1, B:310:0x06dd, B:313:0x06e9, B:316:0x06f5, B:319:0x0701, B:322:0x070d, B:325:0x0719, B:328:0x0725, B:331:0x0732, B:334:0x073e, B:337:0x074a, B:340:0x0756, B:343:0x0762, B:346:0x076e, B:349:0x077a, B:352:0x0786, B:355:0x0792, B:358:0x079e, B:361:0x07ab, B:364:0x07b7, B:367:0x07c3, B:370:0x07cf, B:373:0x07db, B:376:0x07e7, B:379:0x07f3, B:382:0x07ff, B:385:0x080b, B:388:0x0817, B:391:0x0823, B:394:0x082f, B:397:0x083b, B:400:0x0847, B:403:0x0853, B:406:0x085f, B:409:0x086b, B:412:0x0877, B:415:0x0883, B:418:0x088e, B:421:0x089a, B:424:0x08a6, B:427:0x08b2, B:430:0x08be, B:433:0x08ca, B:436:0x08d6, B:439:0x08e2, B:442:0x08ee, B:445:0x08fa, B:448:0x0906, B:451:0x0912, B:454:0x091e, B:457:0x092a, B:460:0x0936, B:463:0x0942, B:466:0x094e, B:469:0x095c, B:472:0x0968, B:475:0x0974, B:478:0x0980, B:481:0x098c, B:484:0x0998, B:487:0x09a4, B:490:0x09b0, B:493:0x09bc, B:496:0x09c8, B:499:0x09d4, B:502:0x09e0, B:505:0x09ec, B:508:0x09f8, B:511:0x0a04, B:514:0x0a10, B:517:0x0a1c, B:520:0x0a28, B:523:0x0a34, B:526:0x0a40, B:529:0x0a4b, B:532:0x0a58, B:535:0x0a64, B:538:0x0a70, B:541:0x0a7c, B:544:0x0a88, B:547:0x0a94, B:550:0x0aa0, B:553:0x0aac, B:556:0x0ab8, B:559:0x0ac4, B:562:0x0acf, B:565:0x0adb, B:568:0x0ae8, B:571:0x0af4, B:574:0x0b00, B:577:0x0b0d, B:580:0x0b19, B:583:0x0b25, B:586:0x0b31, B:589:0x0b3c, B:592:0x0b47, B:595:0x0b52, B:598:0x0b5d, B:601:0x0b68, B:604:0x0b73, B:607:0x0b7e, B:610:0x0b89, B:613:0x0b94, B:246:0x05a0, B:247:0x05ad, B:254:0x05c6, B:201:0x04cf), top: B:974:0x0351 }] */
    /* JADX WARN: Removed duplicated region for block: B:261:0x05dd A[Catch: all -> 0x1f6f, TryCatch #7 {all -> 0x1f6f, blocks: (B:161:0x0351, B:164:0x035f, B:165:0x0372, B:167:0x0375, B:168:0x0381, B:170:0x03a0, B:908:0x1f5e, B:909:0x1var_, B:171:0x03cd, B:173:0x03d6, B:174:0x03ee, B:176:0x03f1, B:177:0x040d, B:179:0x0424, B:180:0x044f, B:182:0x0455, B:184:0x045d, B:186:0x0465, B:188:0x046d, B:191:0x0488, B:193:0x049c, B:195:0x04bb, B:204:0x04da, B:207:0x04e2, B:211:0x04eb, B:218:0x0511, B:220:0x0519, B:224:0x0524, B:226:0x052c, B:230:0x053b, B:232:0x0545, B:234:0x0558, B:237:0x0568, B:239:0x0579, B:241:0x057f, B:259:0x05d9, B:261:0x05dd, B:263:0x0617, B:265:0x061f, B:859:0x1e4c, B:863:0x1e55, B:867:0x1e66, B:869:0x1e71, B:871:0x1e7a, B:872:0x1e81, B:874:0x1e89, B:879:0x1eb6, B:881:0x1ec2, B:894:0x1efc, B:896:0x1f1f, B:897:0x1var_, B:899:0x1var_, B:904:0x1var_, B:884:0x1ed2, B:887:0x1ee4, B:888:0x1ef0, B:877:0x1e9d, B:878:0x1ea9, B:268:0x0638, B:269:0x063c, B:621:0x0bc7, B:856:0x1e26, B:623:0x0bd3, B:626:0x0bf3, B:629:0x0CLASSNAME, B:630:0x0c2f, B:633:0x0c4d, B:635:0x0CLASSNAME, B:636:0x0c7d, B:639:0x0c9b, B:641:0x0cb4, B:642:0x0ccb, B:645:0x0ce9, B:647:0x0d02, B:648:0x0d19, B:651:0x0d37, B:653:0x0d50, B:654:0x0d67, B:657:0x0d85, B:659:0x0d9e, B:660:0x0db5, B:663:0x0dd3, B:665:0x0dec, B:666:0x0e08, B:669:0x0e2b, B:671:0x0e44, B:672:0x0e60, B:675:0x0e83, B:677:0x0e9c, B:678:0x0eb8, B:681:0x0edb, B:683:0x0ef4, B:684:0x0f0b, B:687:0x0var_, B:689:0x0f2d, B:691:0x0var_, B:692:0x0f4c, B:694:0x0var_, B:696:0x0var_, B:698:0x0f6c, B:699:0x0var_, B:700:0x0f9f, B:702:0x0fa3, B:704:0x0fab, B:705:0x0fc2, B:708:0x0fe0, B:710:0x0ff9, B:711:0x1010, B:714:0x102e, B:716:0x1047, B:717:0x105e, B:720:0x107c, B:722:0x1095, B:723:0x10ac, B:726:0x10ca, B:728:0x10e3, B:729:0x10fa, B:732:0x1118, B:734:0x1131, B:735:0x1148, B:738:0x1166, B:740:0x117f, B:741:0x119b, B:742:0x11b2, B:744:0x11d1, B:745:0x1201, B:746:0x122f, B:747:0x125e, B:748:0x128d, B:749:0x12c0, B:750:0x12dd, B:751:0x12fa, B:752:0x1317, B:753:0x1334, B:754:0x1351, B:755:0x136e, B:756:0x138b, B:757:0x13a8, B:758:0x13ca, B:759:0x13e7, B:760:0x1408, B:761:0x1424, B:762:0x1440, B:764:0x1460, B:765:0x148a, B:766:0x14b0, B:767:0x14da, B:768:0x14ff, B:769:0x1524, B:770:0x1549, B:771:0x1573, B:772:0x159c, B:773:0x15c5, B:775:0x15ee, B:777:0x15f8, B:779:0x1600, B:780:0x1636, B:781:0x1667, B:782:0x168c, B:783:0x16b0, B:784:0x16d4, B:785:0x16f8, B:787:0x171e, B:790:0x1746, B:791:0x175f, B:792:0x178a, B:793:0x17b3, B:794:0x17dc, B:795:0x1805, B:796:0x1835, B:797:0x1855, B:798:0x1875, B:799:0x1895, B:800:0x18b5, B:801:0x18da, B:802:0x18ff, B:803:0x1924, B:804:0x1944, B:806:0x194e, B:808:0x1956, B:809:0x1987, B:810:0x199f, B:811:0x19bf, B:812:0x19de, B:813:0x19fd, B:814:0x1a1c, B:817:0x1a41, B:821:0x1a5c, B:822:0x1a87, B:823:0x1aaf, B:824:0x1ad7, B:826:0x1b01, B:827:0x1b2e, B:828:0x1b53, B:829:0x1b75, B:830:0x1b9a, B:831:0x1bba, B:832:0x1bda, B:833:0x1bfa, B:834:0x1c1f, B:835:0x1CLASSNAME, B:836:0x1CLASSNAME, B:837:0x1CLASSNAME, B:839:0x1CLASSNAME, B:841:0x1c9b, B:842:0x1ccf, B:843:0x1ce7, B:844:0x1d07, B:845:0x1d27, B:846:0x1d40, B:847:0x1d60, B:848:0x1d7f, B:849:0x1d9e, B:850:0x1dbd, B:852:0x1dde, B:854:0x1e00, B:271:0x0641, B:274:0x064d, B:277:0x0659, B:280:0x0665, B:283:0x0671, B:286:0x067d, B:289:0x0689, B:292:0x0695, B:295:0x06a1, B:298:0x06ad, B:301:0x06b9, B:304:0x06c5, B:307:0x06d1, B:310:0x06dd, B:313:0x06e9, B:316:0x06f5, B:319:0x0701, B:322:0x070d, B:325:0x0719, B:328:0x0725, B:331:0x0732, B:334:0x073e, B:337:0x074a, B:340:0x0756, B:343:0x0762, B:346:0x076e, B:349:0x077a, B:352:0x0786, B:355:0x0792, B:358:0x079e, B:361:0x07ab, B:364:0x07b7, B:367:0x07c3, B:370:0x07cf, B:373:0x07db, B:376:0x07e7, B:379:0x07f3, B:382:0x07ff, B:385:0x080b, B:388:0x0817, B:391:0x0823, B:394:0x082f, B:397:0x083b, B:400:0x0847, B:403:0x0853, B:406:0x085f, B:409:0x086b, B:412:0x0877, B:415:0x0883, B:418:0x088e, B:421:0x089a, B:424:0x08a6, B:427:0x08b2, B:430:0x08be, B:433:0x08ca, B:436:0x08d6, B:439:0x08e2, B:442:0x08ee, B:445:0x08fa, B:448:0x0906, B:451:0x0912, B:454:0x091e, B:457:0x092a, B:460:0x0936, B:463:0x0942, B:466:0x094e, B:469:0x095c, B:472:0x0968, B:475:0x0974, B:478:0x0980, B:481:0x098c, B:484:0x0998, B:487:0x09a4, B:490:0x09b0, B:493:0x09bc, B:496:0x09c8, B:499:0x09d4, B:502:0x09e0, B:505:0x09ec, B:508:0x09f8, B:511:0x0a04, B:514:0x0a10, B:517:0x0a1c, B:520:0x0a28, B:523:0x0a34, B:526:0x0a40, B:529:0x0a4b, B:532:0x0a58, B:535:0x0a64, B:538:0x0a70, B:541:0x0a7c, B:544:0x0a88, B:547:0x0a94, B:550:0x0aa0, B:553:0x0aac, B:556:0x0ab8, B:559:0x0ac4, B:562:0x0acf, B:565:0x0adb, B:568:0x0ae8, B:571:0x0af4, B:574:0x0b00, B:577:0x0b0d, B:580:0x0b19, B:583:0x0b25, B:586:0x0b31, B:589:0x0b3c, B:592:0x0b47, B:595:0x0b52, B:598:0x0b5d, B:601:0x0b68, B:604:0x0b73, B:607:0x0b7e, B:610:0x0b89, B:613:0x0b94, B:246:0x05a0, B:247:0x05ad, B:254:0x05c6, B:201:0x04cf), top: B:974:0x0351 }] */
    /* JADX WARN: Removed duplicated region for block: B:262:0x060e  */
    /* JADX WARN: Removed duplicated region for block: B:265:0x061f A[Catch: all -> 0x1f6f, TryCatch #7 {all -> 0x1f6f, blocks: (B:161:0x0351, B:164:0x035f, B:165:0x0372, B:167:0x0375, B:168:0x0381, B:170:0x03a0, B:908:0x1f5e, B:909:0x1var_, B:171:0x03cd, B:173:0x03d6, B:174:0x03ee, B:176:0x03f1, B:177:0x040d, B:179:0x0424, B:180:0x044f, B:182:0x0455, B:184:0x045d, B:186:0x0465, B:188:0x046d, B:191:0x0488, B:193:0x049c, B:195:0x04bb, B:204:0x04da, B:207:0x04e2, B:211:0x04eb, B:218:0x0511, B:220:0x0519, B:224:0x0524, B:226:0x052c, B:230:0x053b, B:232:0x0545, B:234:0x0558, B:237:0x0568, B:239:0x0579, B:241:0x057f, B:259:0x05d9, B:261:0x05dd, B:263:0x0617, B:265:0x061f, B:859:0x1e4c, B:863:0x1e55, B:867:0x1e66, B:869:0x1e71, B:871:0x1e7a, B:872:0x1e81, B:874:0x1e89, B:879:0x1eb6, B:881:0x1ec2, B:894:0x1efc, B:896:0x1f1f, B:897:0x1var_, B:899:0x1var_, B:904:0x1var_, B:884:0x1ed2, B:887:0x1ee4, B:888:0x1ef0, B:877:0x1e9d, B:878:0x1ea9, B:268:0x0638, B:269:0x063c, B:621:0x0bc7, B:856:0x1e26, B:623:0x0bd3, B:626:0x0bf3, B:629:0x0CLASSNAME, B:630:0x0c2f, B:633:0x0c4d, B:635:0x0CLASSNAME, B:636:0x0c7d, B:639:0x0c9b, B:641:0x0cb4, B:642:0x0ccb, B:645:0x0ce9, B:647:0x0d02, B:648:0x0d19, B:651:0x0d37, B:653:0x0d50, B:654:0x0d67, B:657:0x0d85, B:659:0x0d9e, B:660:0x0db5, B:663:0x0dd3, B:665:0x0dec, B:666:0x0e08, B:669:0x0e2b, B:671:0x0e44, B:672:0x0e60, B:675:0x0e83, B:677:0x0e9c, B:678:0x0eb8, B:681:0x0edb, B:683:0x0ef4, B:684:0x0f0b, B:687:0x0var_, B:689:0x0f2d, B:691:0x0var_, B:692:0x0f4c, B:694:0x0var_, B:696:0x0var_, B:698:0x0f6c, B:699:0x0var_, B:700:0x0f9f, B:702:0x0fa3, B:704:0x0fab, B:705:0x0fc2, B:708:0x0fe0, B:710:0x0ff9, B:711:0x1010, B:714:0x102e, B:716:0x1047, B:717:0x105e, B:720:0x107c, B:722:0x1095, B:723:0x10ac, B:726:0x10ca, B:728:0x10e3, B:729:0x10fa, B:732:0x1118, B:734:0x1131, B:735:0x1148, B:738:0x1166, B:740:0x117f, B:741:0x119b, B:742:0x11b2, B:744:0x11d1, B:745:0x1201, B:746:0x122f, B:747:0x125e, B:748:0x128d, B:749:0x12c0, B:750:0x12dd, B:751:0x12fa, B:752:0x1317, B:753:0x1334, B:754:0x1351, B:755:0x136e, B:756:0x138b, B:757:0x13a8, B:758:0x13ca, B:759:0x13e7, B:760:0x1408, B:761:0x1424, B:762:0x1440, B:764:0x1460, B:765:0x148a, B:766:0x14b0, B:767:0x14da, B:768:0x14ff, B:769:0x1524, B:770:0x1549, B:771:0x1573, B:772:0x159c, B:773:0x15c5, B:775:0x15ee, B:777:0x15f8, B:779:0x1600, B:780:0x1636, B:781:0x1667, B:782:0x168c, B:783:0x16b0, B:784:0x16d4, B:785:0x16f8, B:787:0x171e, B:790:0x1746, B:791:0x175f, B:792:0x178a, B:793:0x17b3, B:794:0x17dc, B:795:0x1805, B:796:0x1835, B:797:0x1855, B:798:0x1875, B:799:0x1895, B:800:0x18b5, B:801:0x18da, B:802:0x18ff, B:803:0x1924, B:804:0x1944, B:806:0x194e, B:808:0x1956, B:809:0x1987, B:810:0x199f, B:811:0x19bf, B:812:0x19de, B:813:0x19fd, B:814:0x1a1c, B:817:0x1a41, B:821:0x1a5c, B:822:0x1a87, B:823:0x1aaf, B:824:0x1ad7, B:826:0x1b01, B:827:0x1b2e, B:828:0x1b53, B:829:0x1b75, B:830:0x1b9a, B:831:0x1bba, B:832:0x1bda, B:833:0x1bfa, B:834:0x1c1f, B:835:0x1CLASSNAME, B:836:0x1CLASSNAME, B:837:0x1CLASSNAME, B:839:0x1CLASSNAME, B:841:0x1c9b, B:842:0x1ccf, B:843:0x1ce7, B:844:0x1d07, B:845:0x1d27, B:846:0x1d40, B:847:0x1d60, B:848:0x1d7f, B:849:0x1d9e, B:850:0x1dbd, B:852:0x1dde, B:854:0x1e00, B:271:0x0641, B:274:0x064d, B:277:0x0659, B:280:0x0665, B:283:0x0671, B:286:0x067d, B:289:0x0689, B:292:0x0695, B:295:0x06a1, B:298:0x06ad, B:301:0x06b9, B:304:0x06c5, B:307:0x06d1, B:310:0x06dd, B:313:0x06e9, B:316:0x06f5, B:319:0x0701, B:322:0x070d, B:325:0x0719, B:328:0x0725, B:331:0x0732, B:334:0x073e, B:337:0x074a, B:340:0x0756, B:343:0x0762, B:346:0x076e, B:349:0x077a, B:352:0x0786, B:355:0x0792, B:358:0x079e, B:361:0x07ab, B:364:0x07b7, B:367:0x07c3, B:370:0x07cf, B:373:0x07db, B:376:0x07e7, B:379:0x07f3, B:382:0x07ff, B:385:0x080b, B:388:0x0817, B:391:0x0823, B:394:0x082f, B:397:0x083b, B:400:0x0847, B:403:0x0853, B:406:0x085f, B:409:0x086b, B:412:0x0877, B:415:0x0883, B:418:0x088e, B:421:0x089a, B:424:0x08a6, B:427:0x08b2, B:430:0x08be, B:433:0x08ca, B:436:0x08d6, B:439:0x08e2, B:442:0x08ee, B:445:0x08fa, B:448:0x0906, B:451:0x0912, B:454:0x091e, B:457:0x092a, B:460:0x0936, B:463:0x0942, B:466:0x094e, B:469:0x095c, B:472:0x0968, B:475:0x0974, B:478:0x0980, B:481:0x098c, B:484:0x0998, B:487:0x09a4, B:490:0x09b0, B:493:0x09bc, B:496:0x09c8, B:499:0x09d4, B:502:0x09e0, B:505:0x09ec, B:508:0x09f8, B:511:0x0a04, B:514:0x0a10, B:517:0x0a1c, B:520:0x0a28, B:523:0x0a34, B:526:0x0a40, B:529:0x0a4b, B:532:0x0a58, B:535:0x0a64, B:538:0x0a70, B:541:0x0a7c, B:544:0x0a88, B:547:0x0a94, B:550:0x0aa0, B:553:0x0aac, B:556:0x0ab8, B:559:0x0ac4, B:562:0x0acf, B:565:0x0adb, B:568:0x0ae8, B:571:0x0af4, B:574:0x0b00, B:577:0x0b0d, B:580:0x0b19, B:583:0x0b25, B:586:0x0b31, B:589:0x0b3c, B:592:0x0b47, B:595:0x0b52, B:598:0x0b5d, B:601:0x0b68, B:604:0x0b73, B:607:0x0b7e, B:610:0x0b89, B:613:0x0b94, B:246:0x05a0, B:247:0x05ad, B:254:0x05c6, B:201:0x04cf), top: B:974:0x0351 }] */
    /* JADX WARN: Removed duplicated region for block: B:858:0x1e3c  */
    /* JADX WARN: Removed duplicated region for block: B:863:0x1e55 A[Catch: all -> 0x1f6f, TryCatch #7 {all -> 0x1f6f, blocks: (B:161:0x0351, B:164:0x035f, B:165:0x0372, B:167:0x0375, B:168:0x0381, B:170:0x03a0, B:908:0x1f5e, B:909:0x1var_, B:171:0x03cd, B:173:0x03d6, B:174:0x03ee, B:176:0x03f1, B:177:0x040d, B:179:0x0424, B:180:0x044f, B:182:0x0455, B:184:0x045d, B:186:0x0465, B:188:0x046d, B:191:0x0488, B:193:0x049c, B:195:0x04bb, B:204:0x04da, B:207:0x04e2, B:211:0x04eb, B:218:0x0511, B:220:0x0519, B:224:0x0524, B:226:0x052c, B:230:0x053b, B:232:0x0545, B:234:0x0558, B:237:0x0568, B:239:0x0579, B:241:0x057f, B:259:0x05d9, B:261:0x05dd, B:263:0x0617, B:265:0x061f, B:859:0x1e4c, B:863:0x1e55, B:867:0x1e66, B:869:0x1e71, B:871:0x1e7a, B:872:0x1e81, B:874:0x1e89, B:879:0x1eb6, B:881:0x1ec2, B:894:0x1efc, B:896:0x1f1f, B:897:0x1var_, B:899:0x1var_, B:904:0x1var_, B:884:0x1ed2, B:887:0x1ee4, B:888:0x1ef0, B:877:0x1e9d, B:878:0x1ea9, B:268:0x0638, B:269:0x063c, B:621:0x0bc7, B:856:0x1e26, B:623:0x0bd3, B:626:0x0bf3, B:629:0x0CLASSNAME, B:630:0x0c2f, B:633:0x0c4d, B:635:0x0CLASSNAME, B:636:0x0c7d, B:639:0x0c9b, B:641:0x0cb4, B:642:0x0ccb, B:645:0x0ce9, B:647:0x0d02, B:648:0x0d19, B:651:0x0d37, B:653:0x0d50, B:654:0x0d67, B:657:0x0d85, B:659:0x0d9e, B:660:0x0db5, B:663:0x0dd3, B:665:0x0dec, B:666:0x0e08, B:669:0x0e2b, B:671:0x0e44, B:672:0x0e60, B:675:0x0e83, B:677:0x0e9c, B:678:0x0eb8, B:681:0x0edb, B:683:0x0ef4, B:684:0x0f0b, B:687:0x0var_, B:689:0x0f2d, B:691:0x0var_, B:692:0x0f4c, B:694:0x0var_, B:696:0x0var_, B:698:0x0f6c, B:699:0x0var_, B:700:0x0f9f, B:702:0x0fa3, B:704:0x0fab, B:705:0x0fc2, B:708:0x0fe0, B:710:0x0ff9, B:711:0x1010, B:714:0x102e, B:716:0x1047, B:717:0x105e, B:720:0x107c, B:722:0x1095, B:723:0x10ac, B:726:0x10ca, B:728:0x10e3, B:729:0x10fa, B:732:0x1118, B:734:0x1131, B:735:0x1148, B:738:0x1166, B:740:0x117f, B:741:0x119b, B:742:0x11b2, B:744:0x11d1, B:745:0x1201, B:746:0x122f, B:747:0x125e, B:748:0x128d, B:749:0x12c0, B:750:0x12dd, B:751:0x12fa, B:752:0x1317, B:753:0x1334, B:754:0x1351, B:755:0x136e, B:756:0x138b, B:757:0x13a8, B:758:0x13ca, B:759:0x13e7, B:760:0x1408, B:761:0x1424, B:762:0x1440, B:764:0x1460, B:765:0x148a, B:766:0x14b0, B:767:0x14da, B:768:0x14ff, B:769:0x1524, B:770:0x1549, B:771:0x1573, B:772:0x159c, B:773:0x15c5, B:775:0x15ee, B:777:0x15f8, B:779:0x1600, B:780:0x1636, B:781:0x1667, B:782:0x168c, B:783:0x16b0, B:784:0x16d4, B:785:0x16f8, B:787:0x171e, B:790:0x1746, B:791:0x175f, B:792:0x178a, B:793:0x17b3, B:794:0x17dc, B:795:0x1805, B:796:0x1835, B:797:0x1855, B:798:0x1875, B:799:0x1895, B:800:0x18b5, B:801:0x18da, B:802:0x18ff, B:803:0x1924, B:804:0x1944, B:806:0x194e, B:808:0x1956, B:809:0x1987, B:810:0x199f, B:811:0x19bf, B:812:0x19de, B:813:0x19fd, B:814:0x1a1c, B:817:0x1a41, B:821:0x1a5c, B:822:0x1a87, B:823:0x1aaf, B:824:0x1ad7, B:826:0x1b01, B:827:0x1b2e, B:828:0x1b53, B:829:0x1b75, B:830:0x1b9a, B:831:0x1bba, B:832:0x1bda, B:833:0x1bfa, B:834:0x1c1f, B:835:0x1CLASSNAME, B:836:0x1CLASSNAME, B:837:0x1CLASSNAME, B:839:0x1CLASSNAME, B:841:0x1c9b, B:842:0x1ccf, B:843:0x1ce7, B:844:0x1d07, B:845:0x1d27, B:846:0x1d40, B:847:0x1d60, B:848:0x1d7f, B:849:0x1d9e, B:850:0x1dbd, B:852:0x1dde, B:854:0x1e00, B:271:0x0641, B:274:0x064d, B:277:0x0659, B:280:0x0665, B:283:0x0671, B:286:0x067d, B:289:0x0689, B:292:0x0695, B:295:0x06a1, B:298:0x06ad, B:301:0x06b9, B:304:0x06c5, B:307:0x06d1, B:310:0x06dd, B:313:0x06e9, B:316:0x06f5, B:319:0x0701, B:322:0x070d, B:325:0x0719, B:328:0x0725, B:331:0x0732, B:334:0x073e, B:337:0x074a, B:340:0x0756, B:343:0x0762, B:346:0x076e, B:349:0x077a, B:352:0x0786, B:355:0x0792, B:358:0x079e, B:361:0x07ab, B:364:0x07b7, B:367:0x07c3, B:370:0x07cf, B:373:0x07db, B:376:0x07e7, B:379:0x07f3, B:382:0x07ff, B:385:0x080b, B:388:0x0817, B:391:0x0823, B:394:0x082f, B:397:0x083b, B:400:0x0847, B:403:0x0853, B:406:0x085f, B:409:0x086b, B:412:0x0877, B:415:0x0883, B:418:0x088e, B:421:0x089a, B:424:0x08a6, B:427:0x08b2, B:430:0x08be, B:433:0x08ca, B:436:0x08d6, B:439:0x08e2, B:442:0x08ee, B:445:0x08fa, B:448:0x0906, B:451:0x0912, B:454:0x091e, B:457:0x092a, B:460:0x0936, B:463:0x0942, B:466:0x094e, B:469:0x095c, B:472:0x0968, B:475:0x0974, B:478:0x0980, B:481:0x098c, B:484:0x0998, B:487:0x09a4, B:490:0x09b0, B:493:0x09bc, B:496:0x09c8, B:499:0x09d4, B:502:0x09e0, B:505:0x09ec, B:508:0x09f8, B:511:0x0a04, B:514:0x0a10, B:517:0x0a1c, B:520:0x0a28, B:523:0x0a34, B:526:0x0a40, B:529:0x0a4b, B:532:0x0a58, B:535:0x0a64, B:538:0x0a70, B:541:0x0a7c, B:544:0x0a88, B:547:0x0a94, B:550:0x0aa0, B:553:0x0aac, B:556:0x0ab8, B:559:0x0ac4, B:562:0x0acf, B:565:0x0adb, B:568:0x0ae8, B:571:0x0af4, B:574:0x0b00, B:577:0x0b0d, B:580:0x0b19, B:583:0x0b25, B:586:0x0b31, B:589:0x0b3c, B:592:0x0b47, B:595:0x0b52, B:598:0x0b5d, B:601:0x0b68, B:604:0x0b73, B:607:0x0b7e, B:610:0x0b89, B:613:0x0b94, B:246:0x05a0, B:247:0x05ad, B:254:0x05c6, B:201:0x04cf), top: B:974:0x0351 }] */
    /* JADX WARN: Removed duplicated region for block: B:896:0x1f1f A[Catch: all -> 0x1f6f, TryCatch #7 {all -> 0x1f6f, blocks: (B:161:0x0351, B:164:0x035f, B:165:0x0372, B:167:0x0375, B:168:0x0381, B:170:0x03a0, B:908:0x1f5e, B:909:0x1var_, B:171:0x03cd, B:173:0x03d6, B:174:0x03ee, B:176:0x03f1, B:177:0x040d, B:179:0x0424, B:180:0x044f, B:182:0x0455, B:184:0x045d, B:186:0x0465, B:188:0x046d, B:191:0x0488, B:193:0x049c, B:195:0x04bb, B:204:0x04da, B:207:0x04e2, B:211:0x04eb, B:218:0x0511, B:220:0x0519, B:224:0x0524, B:226:0x052c, B:230:0x053b, B:232:0x0545, B:234:0x0558, B:237:0x0568, B:239:0x0579, B:241:0x057f, B:259:0x05d9, B:261:0x05dd, B:263:0x0617, B:265:0x061f, B:859:0x1e4c, B:863:0x1e55, B:867:0x1e66, B:869:0x1e71, B:871:0x1e7a, B:872:0x1e81, B:874:0x1e89, B:879:0x1eb6, B:881:0x1ec2, B:894:0x1efc, B:896:0x1f1f, B:897:0x1var_, B:899:0x1var_, B:904:0x1var_, B:884:0x1ed2, B:887:0x1ee4, B:888:0x1ef0, B:877:0x1e9d, B:878:0x1ea9, B:268:0x0638, B:269:0x063c, B:621:0x0bc7, B:856:0x1e26, B:623:0x0bd3, B:626:0x0bf3, B:629:0x0CLASSNAME, B:630:0x0c2f, B:633:0x0c4d, B:635:0x0CLASSNAME, B:636:0x0c7d, B:639:0x0c9b, B:641:0x0cb4, B:642:0x0ccb, B:645:0x0ce9, B:647:0x0d02, B:648:0x0d19, B:651:0x0d37, B:653:0x0d50, B:654:0x0d67, B:657:0x0d85, B:659:0x0d9e, B:660:0x0db5, B:663:0x0dd3, B:665:0x0dec, B:666:0x0e08, B:669:0x0e2b, B:671:0x0e44, B:672:0x0e60, B:675:0x0e83, B:677:0x0e9c, B:678:0x0eb8, B:681:0x0edb, B:683:0x0ef4, B:684:0x0f0b, B:687:0x0var_, B:689:0x0f2d, B:691:0x0var_, B:692:0x0f4c, B:694:0x0var_, B:696:0x0var_, B:698:0x0f6c, B:699:0x0var_, B:700:0x0f9f, B:702:0x0fa3, B:704:0x0fab, B:705:0x0fc2, B:708:0x0fe0, B:710:0x0ff9, B:711:0x1010, B:714:0x102e, B:716:0x1047, B:717:0x105e, B:720:0x107c, B:722:0x1095, B:723:0x10ac, B:726:0x10ca, B:728:0x10e3, B:729:0x10fa, B:732:0x1118, B:734:0x1131, B:735:0x1148, B:738:0x1166, B:740:0x117f, B:741:0x119b, B:742:0x11b2, B:744:0x11d1, B:745:0x1201, B:746:0x122f, B:747:0x125e, B:748:0x128d, B:749:0x12c0, B:750:0x12dd, B:751:0x12fa, B:752:0x1317, B:753:0x1334, B:754:0x1351, B:755:0x136e, B:756:0x138b, B:757:0x13a8, B:758:0x13ca, B:759:0x13e7, B:760:0x1408, B:761:0x1424, B:762:0x1440, B:764:0x1460, B:765:0x148a, B:766:0x14b0, B:767:0x14da, B:768:0x14ff, B:769:0x1524, B:770:0x1549, B:771:0x1573, B:772:0x159c, B:773:0x15c5, B:775:0x15ee, B:777:0x15f8, B:779:0x1600, B:780:0x1636, B:781:0x1667, B:782:0x168c, B:783:0x16b0, B:784:0x16d4, B:785:0x16f8, B:787:0x171e, B:790:0x1746, B:791:0x175f, B:792:0x178a, B:793:0x17b3, B:794:0x17dc, B:795:0x1805, B:796:0x1835, B:797:0x1855, B:798:0x1875, B:799:0x1895, B:800:0x18b5, B:801:0x18da, B:802:0x18ff, B:803:0x1924, B:804:0x1944, B:806:0x194e, B:808:0x1956, B:809:0x1987, B:810:0x199f, B:811:0x19bf, B:812:0x19de, B:813:0x19fd, B:814:0x1a1c, B:817:0x1a41, B:821:0x1a5c, B:822:0x1a87, B:823:0x1aaf, B:824:0x1ad7, B:826:0x1b01, B:827:0x1b2e, B:828:0x1b53, B:829:0x1b75, B:830:0x1b9a, B:831:0x1bba, B:832:0x1bda, B:833:0x1bfa, B:834:0x1c1f, B:835:0x1CLASSNAME, B:836:0x1CLASSNAME, B:837:0x1CLASSNAME, B:839:0x1CLASSNAME, B:841:0x1c9b, B:842:0x1ccf, B:843:0x1ce7, B:844:0x1d07, B:845:0x1d27, B:846:0x1d40, B:847:0x1d60, B:848:0x1d7f, B:849:0x1d9e, B:850:0x1dbd, B:852:0x1dde, B:854:0x1e00, B:271:0x0641, B:274:0x064d, B:277:0x0659, B:280:0x0665, B:283:0x0671, B:286:0x067d, B:289:0x0689, B:292:0x0695, B:295:0x06a1, B:298:0x06ad, B:301:0x06b9, B:304:0x06c5, B:307:0x06d1, B:310:0x06dd, B:313:0x06e9, B:316:0x06f5, B:319:0x0701, B:322:0x070d, B:325:0x0719, B:328:0x0725, B:331:0x0732, B:334:0x073e, B:337:0x074a, B:340:0x0756, B:343:0x0762, B:346:0x076e, B:349:0x077a, B:352:0x0786, B:355:0x0792, B:358:0x079e, B:361:0x07ab, B:364:0x07b7, B:367:0x07c3, B:370:0x07cf, B:373:0x07db, B:376:0x07e7, B:379:0x07f3, B:382:0x07ff, B:385:0x080b, B:388:0x0817, B:391:0x0823, B:394:0x082f, B:397:0x083b, B:400:0x0847, B:403:0x0853, B:406:0x085f, B:409:0x086b, B:412:0x0877, B:415:0x0883, B:418:0x088e, B:421:0x089a, B:424:0x08a6, B:427:0x08b2, B:430:0x08be, B:433:0x08ca, B:436:0x08d6, B:439:0x08e2, B:442:0x08ee, B:445:0x08fa, B:448:0x0906, B:451:0x0912, B:454:0x091e, B:457:0x092a, B:460:0x0936, B:463:0x0942, B:466:0x094e, B:469:0x095c, B:472:0x0968, B:475:0x0974, B:478:0x0980, B:481:0x098c, B:484:0x0998, B:487:0x09a4, B:490:0x09b0, B:493:0x09bc, B:496:0x09c8, B:499:0x09d4, B:502:0x09e0, B:505:0x09ec, B:508:0x09f8, B:511:0x0a04, B:514:0x0a10, B:517:0x0a1c, B:520:0x0a28, B:523:0x0a34, B:526:0x0a40, B:529:0x0a4b, B:532:0x0a58, B:535:0x0a64, B:538:0x0a70, B:541:0x0a7c, B:544:0x0a88, B:547:0x0a94, B:550:0x0aa0, B:553:0x0aac, B:556:0x0ab8, B:559:0x0ac4, B:562:0x0acf, B:565:0x0adb, B:568:0x0ae8, B:571:0x0af4, B:574:0x0b00, B:577:0x0b0d, B:580:0x0b19, B:583:0x0b25, B:586:0x0b31, B:589:0x0b3c, B:592:0x0b47, B:595:0x0b52, B:598:0x0b5d, B:601:0x0b68, B:604:0x0b73, B:607:0x0b7e, B:610:0x0b89, B:613:0x0b94, B:246:0x05a0, B:247:0x05ad, B:254:0x05c6, B:201:0x04cf), top: B:974:0x0351 }] */
    /* JADX WARN: Removed duplicated region for block: B:908:0x1f5e A[Catch: all -> 0x1f6f, TryCatch #7 {all -> 0x1f6f, blocks: (B:161:0x0351, B:164:0x035f, B:165:0x0372, B:167:0x0375, B:168:0x0381, B:170:0x03a0, B:908:0x1f5e, B:909:0x1var_, B:171:0x03cd, B:173:0x03d6, B:174:0x03ee, B:176:0x03f1, B:177:0x040d, B:179:0x0424, B:180:0x044f, B:182:0x0455, B:184:0x045d, B:186:0x0465, B:188:0x046d, B:191:0x0488, B:193:0x049c, B:195:0x04bb, B:204:0x04da, B:207:0x04e2, B:211:0x04eb, B:218:0x0511, B:220:0x0519, B:224:0x0524, B:226:0x052c, B:230:0x053b, B:232:0x0545, B:234:0x0558, B:237:0x0568, B:239:0x0579, B:241:0x057f, B:259:0x05d9, B:261:0x05dd, B:263:0x0617, B:265:0x061f, B:859:0x1e4c, B:863:0x1e55, B:867:0x1e66, B:869:0x1e71, B:871:0x1e7a, B:872:0x1e81, B:874:0x1e89, B:879:0x1eb6, B:881:0x1ec2, B:894:0x1efc, B:896:0x1f1f, B:897:0x1var_, B:899:0x1var_, B:904:0x1var_, B:884:0x1ed2, B:887:0x1ee4, B:888:0x1ef0, B:877:0x1e9d, B:878:0x1ea9, B:268:0x0638, B:269:0x063c, B:621:0x0bc7, B:856:0x1e26, B:623:0x0bd3, B:626:0x0bf3, B:629:0x0CLASSNAME, B:630:0x0c2f, B:633:0x0c4d, B:635:0x0CLASSNAME, B:636:0x0c7d, B:639:0x0c9b, B:641:0x0cb4, B:642:0x0ccb, B:645:0x0ce9, B:647:0x0d02, B:648:0x0d19, B:651:0x0d37, B:653:0x0d50, B:654:0x0d67, B:657:0x0d85, B:659:0x0d9e, B:660:0x0db5, B:663:0x0dd3, B:665:0x0dec, B:666:0x0e08, B:669:0x0e2b, B:671:0x0e44, B:672:0x0e60, B:675:0x0e83, B:677:0x0e9c, B:678:0x0eb8, B:681:0x0edb, B:683:0x0ef4, B:684:0x0f0b, B:687:0x0var_, B:689:0x0f2d, B:691:0x0var_, B:692:0x0f4c, B:694:0x0var_, B:696:0x0var_, B:698:0x0f6c, B:699:0x0var_, B:700:0x0f9f, B:702:0x0fa3, B:704:0x0fab, B:705:0x0fc2, B:708:0x0fe0, B:710:0x0ff9, B:711:0x1010, B:714:0x102e, B:716:0x1047, B:717:0x105e, B:720:0x107c, B:722:0x1095, B:723:0x10ac, B:726:0x10ca, B:728:0x10e3, B:729:0x10fa, B:732:0x1118, B:734:0x1131, B:735:0x1148, B:738:0x1166, B:740:0x117f, B:741:0x119b, B:742:0x11b2, B:744:0x11d1, B:745:0x1201, B:746:0x122f, B:747:0x125e, B:748:0x128d, B:749:0x12c0, B:750:0x12dd, B:751:0x12fa, B:752:0x1317, B:753:0x1334, B:754:0x1351, B:755:0x136e, B:756:0x138b, B:757:0x13a8, B:758:0x13ca, B:759:0x13e7, B:760:0x1408, B:761:0x1424, B:762:0x1440, B:764:0x1460, B:765:0x148a, B:766:0x14b0, B:767:0x14da, B:768:0x14ff, B:769:0x1524, B:770:0x1549, B:771:0x1573, B:772:0x159c, B:773:0x15c5, B:775:0x15ee, B:777:0x15f8, B:779:0x1600, B:780:0x1636, B:781:0x1667, B:782:0x168c, B:783:0x16b0, B:784:0x16d4, B:785:0x16f8, B:787:0x171e, B:790:0x1746, B:791:0x175f, B:792:0x178a, B:793:0x17b3, B:794:0x17dc, B:795:0x1805, B:796:0x1835, B:797:0x1855, B:798:0x1875, B:799:0x1895, B:800:0x18b5, B:801:0x18da, B:802:0x18ff, B:803:0x1924, B:804:0x1944, B:806:0x194e, B:808:0x1956, B:809:0x1987, B:810:0x199f, B:811:0x19bf, B:812:0x19de, B:813:0x19fd, B:814:0x1a1c, B:817:0x1a41, B:821:0x1a5c, B:822:0x1a87, B:823:0x1aaf, B:824:0x1ad7, B:826:0x1b01, B:827:0x1b2e, B:828:0x1b53, B:829:0x1b75, B:830:0x1b9a, B:831:0x1bba, B:832:0x1bda, B:833:0x1bfa, B:834:0x1c1f, B:835:0x1CLASSNAME, B:836:0x1CLASSNAME, B:837:0x1CLASSNAME, B:839:0x1CLASSNAME, B:841:0x1c9b, B:842:0x1ccf, B:843:0x1ce7, B:844:0x1d07, B:845:0x1d27, B:846:0x1d40, B:847:0x1d60, B:848:0x1d7f, B:849:0x1d9e, B:850:0x1dbd, B:852:0x1dde, B:854:0x1e00, B:271:0x0641, B:274:0x064d, B:277:0x0659, B:280:0x0665, B:283:0x0671, B:286:0x067d, B:289:0x0689, B:292:0x0695, B:295:0x06a1, B:298:0x06ad, B:301:0x06b9, B:304:0x06c5, B:307:0x06d1, B:310:0x06dd, B:313:0x06e9, B:316:0x06f5, B:319:0x0701, B:322:0x070d, B:325:0x0719, B:328:0x0725, B:331:0x0732, B:334:0x073e, B:337:0x074a, B:340:0x0756, B:343:0x0762, B:346:0x076e, B:349:0x077a, B:352:0x0786, B:355:0x0792, B:358:0x079e, B:361:0x07ab, B:364:0x07b7, B:367:0x07c3, B:370:0x07cf, B:373:0x07db, B:376:0x07e7, B:379:0x07f3, B:382:0x07ff, B:385:0x080b, B:388:0x0817, B:391:0x0823, B:394:0x082f, B:397:0x083b, B:400:0x0847, B:403:0x0853, B:406:0x085f, B:409:0x086b, B:412:0x0877, B:415:0x0883, B:418:0x088e, B:421:0x089a, B:424:0x08a6, B:427:0x08b2, B:430:0x08be, B:433:0x08ca, B:436:0x08d6, B:439:0x08e2, B:442:0x08ee, B:445:0x08fa, B:448:0x0906, B:451:0x0912, B:454:0x091e, B:457:0x092a, B:460:0x0936, B:463:0x0942, B:466:0x094e, B:469:0x095c, B:472:0x0968, B:475:0x0974, B:478:0x0980, B:481:0x098c, B:484:0x0998, B:487:0x09a4, B:490:0x09b0, B:493:0x09bc, B:496:0x09c8, B:499:0x09d4, B:502:0x09e0, B:505:0x09ec, B:508:0x09f8, B:511:0x0a04, B:514:0x0a10, B:517:0x0a1c, B:520:0x0a28, B:523:0x0a34, B:526:0x0a40, B:529:0x0a4b, B:532:0x0a58, B:535:0x0a64, B:538:0x0a70, B:541:0x0a7c, B:544:0x0a88, B:547:0x0a94, B:550:0x0aa0, B:553:0x0aac, B:556:0x0ab8, B:559:0x0ac4, B:562:0x0acf, B:565:0x0adb, B:568:0x0ae8, B:571:0x0af4, B:574:0x0b00, B:577:0x0b0d, B:580:0x0b19, B:583:0x0b25, B:586:0x0b31, B:589:0x0b3c, B:592:0x0b47, B:595:0x0b52, B:598:0x0b5d, B:601:0x0b68, B:604:0x0b73, B:607:0x0b7e, B:610:0x0b89, B:613:0x0b94, B:246:0x05a0, B:247:0x05ad, B:254:0x05c6, B:201:0x04cf), top: B:974:0x0351 }] */
    /* JADX WARN: Removed duplicated region for block: B:954:0x2064  */
    /* JADX WARN: Removed duplicated region for block: B:955:0x2074  */
    /* JADX WARN: Removed duplicated region for block: B:958:0x207b  */
    /* JADX WARN: Type inference failed for: r14v25 */
    /* JADX WARN: Type inference failed for: r14v28 */
    /* JADX WARN: Type inference failed for: r14v31 */
    /* JADX WARN: Type inference failed for: r14v34 */
    /* JADX WARN: Type inference failed for: r14v35 */
    /* JADX WARN: Type inference failed for: r14v36 */
    /* JADX WARN: Type inference failed for: r14v55 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static /* synthetic */ void lambda$processRemoteMessage$7(java.lang.String r52, java.lang.String r53, long r54) {
        /*
            Method dump skipped, instructions count: 9062
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.PushListenerController.lambda$processRemoteMessage$7(java.lang.String, java.lang.String, long):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$processRemoteMessage$4(int i, TLRPC$TL_updates tLRPC$TL_updates) {
        MessagesController.getInstance(i).processUpdates(tLRPC$TL_updates, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$processRemoteMessage$5(int i) {
        if (UserConfig.getInstance(i).getClientUserId() != 0) {
            UserConfig.getInstance(i).clearConfig();
            MessagesController.getInstance(i).performLogout(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$processRemoteMessage$6(int i) {
        LocationController.getInstance(i).setNewLocationEndWatchTime();
    }

    private static String getReactedText(String str, Object[] objArr) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2114646919:
                if (str.equals("CHAT_REACT_CONTACT")) {
                    c = 0;
                    break;
                }
                break;
            case -1891797827:
                if (str.equals("REACT_GEOLIVE")) {
                    c = 1;
                    break;
                }
                break;
            case -1415696683:
                if (str.equals("CHAT_REACT_NOTEXT")) {
                    c = 2;
                    break;
                }
                break;
            case -1375264434:
                if (str.equals("REACT_NOTEXT")) {
                    c = 3;
                    break;
                }
                break;
            case -1105974394:
                if (str.equals("CHAT_REACT_INVOICE")) {
                    c = 4;
                    break;
                }
                break;
            case -861247200:
                if (str.equals("REACT_CONTACT")) {
                    c = 5;
                    break;
                }
                break;
            case -661458538:
                if (str.equals("CHAT_REACT_STICKER")) {
                    c = 6;
                    break;
                }
                break;
            case 51977938:
                if (str.equals("REACT_GAME")) {
                    c = 7;
                    break;
                }
                break;
            case 52259487:
                if (str.equals("REACT_POLL")) {
                    c = '\b';
                    break;
                }
                break;
            case 52294965:
                if (str.equals("REACT_QUIZ")) {
                    c = '\t';
                    break;
                }
                break;
            case 52369421:
                if (str.equals("REACT_TEXT")) {
                    c = '\n';
                    break;
                }
                break;
            case 147425325:
                if (str.equals("REACT_INVOICE")) {
                    c = 11;
                    break;
                }
                break;
            case 192842257:
                if (str.equals("CHAT_REACT_DOC")) {
                    c = '\f';
                    break;
                }
                break;
            case 192844842:
                if (str.equals("CHAT_REACT_GEO")) {
                    c = '\r';
                    break;
                }
                break;
            case 192844957:
                if (str.equals("CHAT_REACT_GIF")) {
                    c = 14;
                    break;
                }
                break;
            case 591941181:
                if (str.equals("REACT_STICKER")) {
                    c = 15;
                    break;
                }
                break;
            case 635226735:
                if (str.equals("CHAT_REACT_AUDIO")) {
                    c = 16;
                    break;
                }
                break;
            case 648703179:
                if (str.equals("CHAT_REACT_PHOTO")) {
                    c = 17;
                    break;
                }
                break;
            case 650764327:
                if (str.equals("CHAT_REACT_ROUND")) {
                    c = 18;
                    break;
                }
                break;
            case 654263060:
                if (str.equals("CHAT_REACT_VIDEO")) {
                    c = 19;
                    break;
                }
                break;
            case 1149769750:
                if (str.equals("CHAT_REACT_GEOLIVE")) {
                    c = 20;
                    break;
                }
                break;
            case 1606362326:
                if (str.equals("REACT_AUDIO")) {
                    c = 21;
                    break;
                }
                break;
            case 1619838770:
                if (str.equals("REACT_PHOTO")) {
                    c = 22;
                    break;
                }
                break;
            case 1621899918:
                if (str.equals("REACT_ROUND")) {
                    c = 23;
                    break;
                }
                break;
            case 1625398651:
                if (str.equals("REACT_VIDEO")) {
                    c = 24;
                    break;
                }
                break;
            case 1664242232:
                if (str.equals("REACT_DOC")) {
                    c = 25;
                    break;
                }
                break;
            case 1664244817:
                if (str.equals("REACT_GEO")) {
                    c = 26;
                    break;
                }
                break;
            case 1664244932:
                if (str.equals("REACT_GIF")) {
                    c = 27;
                    break;
                }
                break;
            case 1683218969:
                if (str.equals("CHAT_REACT_GAME")) {
                    c = 28;
                    break;
                }
                break;
            case 1683500518:
                if (str.equals("CHAT_REACT_POLL")) {
                    c = 29;
                    break;
                }
                break;
            case 1683535996:
                if (str.equals("CHAT_REACT_QUIZ")) {
                    c = 30;
                    break;
                }
                break;
            case 1683610452:
                if (str.equals("CHAT_REACT_TEXT")) {
                    c = 31;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return LocaleController.formatString("PushChatReactContact", R.string.PushChatReactContact, objArr);
            case 1:
                return LocaleController.formatString("PushReactGeoLocation", R.string.PushReactGeoLocation, objArr);
            case 2:
                return LocaleController.formatString("PushChatReactNotext", R.string.PushChatReactNotext, objArr);
            case 3:
                return LocaleController.formatString("PushReactNoText", R.string.PushReactNoText, objArr);
            case 4:
                return LocaleController.formatString("PushChatReactInvoice", R.string.PushChatReactInvoice, objArr);
            case 5:
                return LocaleController.formatString("PushReactContect", R.string.PushReactContect, objArr);
            case 6:
                return LocaleController.formatString("PushChatReactSticker", R.string.PushChatReactSticker, objArr);
            case 7:
                return LocaleController.formatString("PushReactGame", R.string.PushReactGame, objArr);
            case '\b':
                return LocaleController.formatString("PushReactPoll", R.string.PushReactPoll, objArr);
            case '\t':
                return LocaleController.formatString("PushReactQuiz", R.string.PushReactQuiz, objArr);
            case '\n':
                return LocaleController.formatString("PushReactText", R.string.PushReactText, objArr);
            case 11:
                return LocaleController.formatString("PushReactInvoice", R.string.PushReactInvoice, objArr);
            case '\f':
                return LocaleController.formatString("PushChatReactDoc", R.string.PushChatReactDoc, objArr);
            case '\r':
                return LocaleController.formatString("PushChatReactGeo", R.string.PushChatReactGeo, objArr);
            case 14:
                return LocaleController.formatString("PushChatReactGif", R.string.PushChatReactGif, objArr);
            case 15:
                return LocaleController.formatString("PushReactSticker", R.string.PushReactSticker, objArr);
            case 16:
                return LocaleController.formatString("PushChatReactAudio", R.string.PushChatReactAudio, objArr);
            case 17:
                return LocaleController.formatString("PushChatReactPhoto", R.string.PushChatReactPhoto, objArr);
            case 18:
                return LocaleController.formatString("PushChatReactRound", R.string.PushChatReactRound, objArr);
            case 19:
                return LocaleController.formatString("PushChatReactVideo", R.string.PushChatReactVideo, objArr);
            case 20:
                return LocaleController.formatString("PushChatReactGeoLive", R.string.PushChatReactGeoLive, objArr);
            case 21:
                return LocaleController.formatString("PushReactAudio", R.string.PushReactAudio, objArr);
            case 22:
                return LocaleController.formatString("PushReactPhoto", R.string.PushReactPhoto, objArr);
            case 23:
                return LocaleController.formatString("PushReactRound", R.string.PushReactRound, objArr);
            case 24:
                return LocaleController.formatString("PushReactVideo", R.string.PushReactVideo, objArr);
            case 25:
                return LocaleController.formatString("PushReactDoc", R.string.PushReactDoc, objArr);
            case 26:
                return LocaleController.formatString("PushReactGeo", R.string.PushReactGeo, objArr);
            case 27:
                return LocaleController.formatString("PushReactGif", R.string.PushReactGif, objArr);
            case 28:
                return LocaleController.formatString("PushChatReactGame", R.string.PushChatReactGame, objArr);
            case 29:
                return LocaleController.formatString("PushChatReactPoll", R.string.PushChatReactPoll, objArr);
            case 30:
                return LocaleController.formatString("PushChatReactQuiz", R.string.PushChatReactQuiz, objArr);
            case 31:
                return LocaleController.formatString("PushChatReactText", R.string.PushChatReactText, objArr);
            default:
                return null;
        }
    }

    private static void onDecryptError() {
        for (int i = 0; i < 4; i++) {
            if (UserConfig.getInstance(i).isClientActivated()) {
                ConnectionsManager.onInternalPushReceived(i);
                ConnectionsManager.getInstance(i).resumeNetworkMaybe();
            }
        }
        countDownLatch.countDown();
    }

    /* loaded from: classes.dex */
    public static final class GooglePushListenerServiceProvider implements IPushListenerServiceProvider {
        public static final GooglePushListenerServiceProvider INSTANCE = new GooglePushListenerServiceProvider();
        private Boolean hasServices;

        @Override // org.telegram.messenger.PushListenerController.IPushListenerServiceProvider
        public String getLogTitle() {
            return "Google Play Services";
        }

        @Override // org.telegram.messenger.PushListenerController.IPushListenerServiceProvider
        public int getPushType() {
            return 2;
        }

        private GooglePushListenerServiceProvider() {
        }

        @Override // org.telegram.messenger.PushListenerController.IPushListenerServiceProvider
        public void onRequestPushToken() {
            String str = SharedConfig.pushString;
            if (!TextUtils.isEmpty(str)) {
                if (BuildVars.DEBUG_PRIVATE_VERSION && BuildVars.LOGS_ENABLED) {
                    FileLog.d("FCM regId = " + str);
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("FCM Registration not found.");
            }
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.PushListenerController$GooglePushListenerServiceProvider$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    PushListenerController.GooglePushListenerServiceProvider.this.lambda$onRequestPushToken$1();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onRequestPushToken$1() {
            try {
                SharedConfig.pushStringGetTimeStart = SystemClock.elapsedRealtime();
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener() { // from class: org.telegram.messenger.PushListenerController$GooglePushListenerServiceProvider$$ExternalSyntheticLambda0
                    @Override // com.google.android.gms.tasks.OnCompleteListener
                    public final void onComplete(Task task) {
                        PushListenerController.GooglePushListenerServiceProvider.this.lambda$onRequestPushToken$0(task);
                    }
                });
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onRequestPushToken$0(Task task) {
            SharedConfig.pushStringGetTimeEnd = SystemClock.elapsedRealtime();
            if (!task.isSuccessful()) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("Failed to get regid");
                }
                SharedConfig.pushStringStatus = "__FIREBASE_FAILED__";
                PushListenerController.sendRegistrationToServer(getPushType(), null);
                return;
            }
            String str = (String) task.getResult();
            if (TextUtils.isEmpty(str)) {
                return;
            }
            PushListenerController.sendRegistrationToServer(getPushType(), str);
        }

        @Override // org.telegram.messenger.PushListenerController.IPushListenerServiceProvider
        public boolean hasServices() {
            if (this.hasServices == null) {
                try {
                    this.hasServices = Boolean.valueOf(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ApplicationLoader.applicationContext) == 0);
                } catch (Exception e) {
                    FileLog.e(e);
                    this.hasServices = Boolean.FALSE;
                }
            }
            return this.hasServices.booleanValue();
        }
    }
}
