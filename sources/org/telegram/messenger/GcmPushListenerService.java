package org.telegram.messenger;

import android.os.SystemClock;
import android.text.TextUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;

public class GcmPushListenerService extends FirebaseMessagingService {
    public static final int NOTIFICATION_ID = 1;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Map data = message.getData();
        long time = message.getSentTime();
        long receiveTime = SystemClock.elapsedRealtime();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("GCM received data: " + data + " from: " + from);
        }
        AndroidUtilities.runOnUIThread(new GcmPushListenerService$$ExternalSyntheticLambda7(this, data, time));
        try {
            this.countDownLatch.await();
        } catch (Throwable th) {
        }
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("finished GCM service, time = " + (SystemClock.elapsedRealtime() - receiveTime));
        }
    }

    /* renamed from: lambda$onMessageReceived$4$org-telegram-messenger-GcmPushListenerService  reason: not valid java name */
    public /* synthetic */ void m69x1d2d684b(Map data, long time) {
        ApplicationLoader.postInitApplication();
        Utilities.stageQueue.postRunnable(new GcmPushListenerService$$ExternalSyntheticLambda6(this, data, time));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r74v3, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r74v4, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r74v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r74v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v23, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r74v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r74v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v13, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v35, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r74v12, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r74v13, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v40, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v41, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v45, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v47, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v51, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v55, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v59, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v44, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v63, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v45, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v67, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v22, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v70, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v46, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v74, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v47, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v78, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v48, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v82, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v73, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v86, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v90, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v94, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v98, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v102, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v106, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v110, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v45, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v114, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v118, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v122, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v126, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v70, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v130, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v132, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v135, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v137, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v140, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v141, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v142, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v145, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v147, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v150, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v154, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v158, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v162, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v166, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v170, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v167, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v174, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v78, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v173, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v178, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v174, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v182, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v82, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v186, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v190, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v190, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v195, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v194, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v196, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v200, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v198, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v205, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v202, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v210, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v206, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v215, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v210, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v220, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v214, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v93, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v223, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v221, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v107, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v226, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v229, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v228, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v111, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v229, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v237, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v115, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v232, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v245, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v119, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v235, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v253, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v237, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v257, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v240, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v241, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v261, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v263, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v246, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v265, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v101, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v251, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v269, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v256, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v273, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v275, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v261, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v277, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v266, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v281, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v283, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v272, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v285, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v273, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v287, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v225, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v279, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v289, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v291, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v284, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v293, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v295, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v289, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v297, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v299, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v294, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v301, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v303, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v299, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v305, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v307, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v304, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v309, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v311, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v309, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v313, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v315, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v314, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v317, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v319, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v319, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v321, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v323, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v134, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v321, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v325, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v327, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v138, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v326, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v329, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v331, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v141, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v328, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v333, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v335, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v143, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v330, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v337, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v339, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v332, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v341, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v343, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v148, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v149, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v334, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v345, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v347, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v336, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v349, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v351, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v153, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v154, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v338, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v353, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v355, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v155, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v340, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v357, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v359, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v158, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v342, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v361, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v363, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v344, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v365, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v367, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v346, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v369, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v371, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v348, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v373, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v375, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v166, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v350, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v377, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v379, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v167, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v352, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v381, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v383, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v169, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v170, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v354, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v385, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v387, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v172, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v173, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v356, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v389, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v358, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v235, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v359, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v391, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v361, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v240, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v362, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v393, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v364, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v244, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v245, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v365, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v395, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v367, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v250, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v368, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v397, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v399, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v175, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v370, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v401, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v404, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v177, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v178, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v373, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v406, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v408, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v181, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v375, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v410, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v412, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v377, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v414, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v266, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v380, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v419, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v421, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v382, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v423, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v425, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v384, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v427, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v274, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v387, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v432, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v434, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v190, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v389, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v436, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v438, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v391, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v440, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v282, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v394, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v445, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v447, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v196, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v396, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v449, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v451, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v198, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v398, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v453, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v290, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v401, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v458, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v460, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v200, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v201, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v403, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v462, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v464, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v405, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v466, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v298, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v408, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v471, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v473, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v206, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v410, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v475, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v477, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v207, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v208, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v412, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v479, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v306, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v209, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v416, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v484, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v417, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v486, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v210, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v420, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v488, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v490, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v212, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v213, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v423, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v492, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v424, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v494, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v214, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v216, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v427, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v496, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v317, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v217, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v430, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v500, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v431, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v502, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v434, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v504, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v322, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v437, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v509, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v511, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v221, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v439, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v513, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v515, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v223, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v441, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v517, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v520, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v226, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v444, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v522, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v524, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v227, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v228, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v229, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v446, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v526, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v528, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v448, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v530, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v533, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v232, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v451, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v535, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v537, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v453, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v539, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v541, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v455, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v543, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v546, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v458, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v548, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v550, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v460, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v552, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v554, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v244, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v462, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v556, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v357, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v465, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v561, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v563, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v467, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v565, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v567, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v469, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v569, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v365, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v472, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v574, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v576, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v252, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v474, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v578, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v580, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v476, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v582, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v373, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v256, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v479, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v587, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v589, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v257, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v258, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v481, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v591, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v593, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v260, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v483, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v595, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v381, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v486, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v600, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v602, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v262, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v263, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v488, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v604, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v606, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v264, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v490, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v608, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v389, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v266, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v493, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v613, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v615, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v268, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v495, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v617, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v619, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v269, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v270, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v497, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v621, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v397, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v500, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v626, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v628, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v272, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v273, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v502, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v630, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v632, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v274, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v275, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v504, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v634, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v509, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v636, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v637, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v638, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v639, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v408, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v641, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v14, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v16, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r74v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r74v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v75, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v79, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v409, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v410, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v684, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v688, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v689, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v690, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v691, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v692, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v694, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v695, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v696, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v697, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX WARNING: type inference failed for: r5v280 */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:1000:0x23c7, code lost:
        r16 = r1;
        r24 = r2;
        r74 = r3;
        r13 = r6;
        r8 = r51;
        r51 = r58;
        r7 = r63;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1001:0x23d7, code lost:
        r73 = r2;
        r62 = r3;
        r74 = r4;
        r67 = r5;
        r55 = r6;
        r69 = r8;
        r57 = r11;
        r8 = r51;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1002:0x23ea, code lost:
        r62 = r3;
        r74 = r4;
        r67 = r5;
        r69 = r8;
        r8 = r51;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1003:0x23f7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1004:0x23f8, code lost:
        r3 = r77;
        r74 = r4;
        r2 = r0;
        r5 = r18;
        r1 = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1005:0x2403, code lost:
        r48 = true;
        r62 = r3;
        r74 = r4;
        r67 = r5;
        r69 = r8;
        r49 = r10;
        r50 = r11;
        r53 = r12;
        r3 = r77;
        r8 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1006:0x2416, code lost:
        r1 = r48;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1007:0x2418, code lost:
        if (r1 == false) goto L_0x241f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1008:0x241a, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1009:0x241f, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r31);
        org.telegram.tgnet.ConnectionsManager.getInstance(r31).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1010:0x2429, code lost:
        r5 = r18;
        r1 = r31;
        r4 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1011:0x2431, code lost:
        r0 = th;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1012:0x2432, code lost:
        r2 = r0;
        r5 = r18;
        r1 = r31;
        r4 = r74;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1042:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:0x0244, code lost:
        switch(r2) {
            case 0: goto L_0x02bd;
            case 1: goto L_0x0273;
            case 2: goto L_0x0263;
            case 3: goto L_0x0251;
            default: goto L_0x0247;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:0x0247, code lost:
        r40 = r6;
        r17 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:?, code lost:
        r40 = r6;
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1(r3));
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x0262, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x0263, code lost:
        r40 = r6;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0(r3));
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x0272, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x0273, code lost:
        r40 = r6;
        r2 = new org.telegram.tgnet.TLRPC.TL_updateServiceNotification();
        r2.popup = false;
        r2.flags = 2;
        r2.inbox_date = (int) (r79 / 1000);
        r2.message = r11.getString("message");
        r2.type = "announcement";
        r2.media = new org.telegram.tgnet.TLRPC.TL_messageMediaEmpty();
        r6 = new org.telegram.tgnet.TLRPC.TL_updates();
        r6.updates.add(r2);
        r16 = r2;
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3(r3, r6));
        org.telegram.tgnet.ConnectionsManager.getInstance(r31).resumeNetworkMaybe();
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:157:0x02bc, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x02bd, code lost:
        r40 = r6;
        r2 = r5.getInt("dc");
        r6 = r5.getString("addr");
        r7 = r6.split(":");
        r16 = r6;
        r17 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:159:0x02d7, code lost:
        if (r7.length == 2) goto L_0x02df;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x02d9, code lost:
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:161:0x02de, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x02df, code lost:
        r24 = r7;
        org.telegram.tgnet.ConnectionsManager.getInstance(r31).applyDatacenterAddress(r2, r7[0], java.lang.Integer.parseInt(r7[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r31).resumeNetworkMaybe();
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:163:0x02fe, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x0303, code lost:
        if (r5.has("channel_id") == 0) goto L_0x0319;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x030b, code lost:
        r2 = r9;
        r6 = r5.getLong("channel_id");
        r75 = r6;
        r6 = -r6;
        r8 = r75;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x0319, code lost:
        r2 = r9;
        r6 = 0;
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x0324, code lost:
        r42 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:175:0x032c, code lost:
        if (r5.has("from_id") == false) goto L_0x033b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:179:0x0334, code lost:
        r6 = r5.getLong("from_id");
        r32 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x033b, code lost:
        r75 = r6;
        r6 = 0;
        r32 = r75;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x0349, code lost:
        if (r5.has("chat_id") == false) goto L_0x035e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x0351, code lost:
        r2 = r12;
        r43 = r13;
        r44 = r14;
        r45 = r15;
        r12 = r5.getLong("chat_id");
        r14 = -r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x035e, code lost:
        r2 = r12;
        r43 = r13;
        r44 = r14;
        r45 = r15;
        r12 = 0;
        r14 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x036b, code lost:
        r46 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x0373, code lost:
        if (r5.has("encryption_id") == false) goto L_0x0383;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:?, code lost:
        r32 = r14;
        r14 = org.telegram.messenger.DialogObject.makeEncryptedDialogId((long) r5.getInt("encryption_id"));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x0383, code lost:
        r32 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x038b, code lost:
        if (r5.has("schedule") == false) goto L_0x039c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x0393, code lost:
        r47 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x0396, code lost:
        if (r5.getInt("schedule") != 1) goto L_0x039a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x0398, code lost:
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x039a, code lost:
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x039c, code lost:
        r47 = r10;
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x039f, code lost:
        r10 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x03a4, code lost:
        if (r14 != 0) goto L_0x03b8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x03ac, code lost:
        if ("ENCRYPTED_MESSAGE".equals(r4) == false) goto L_0x03b8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x03b0, code lost:
        r14 = org.telegram.messenger.NotificationsController.globalSecretChatId;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:218:0x03b2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x03b3, code lost:
        r3 = r77;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x03bd, code lost:
        if (r14 == 0) goto L_0x2403;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x03c1, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:225:0x03c5, code lost:
        r48 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x03c9, code lost:
        if ("READ_HISTORY".equals(r4) == false) goto L_0x045e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:?, code lost:
        r2 = r5.getInt("max_id");
        r24 = new java.util.ArrayList<>();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x03da, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x03fb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x03dc, code lost:
        r49 = r10;
        r10 = new java.lang.StringBuilder();
        r50 = r11;
        r10.append("GCM received read notification max_id = ");
        r10.append(r2);
        r10.append(" for dialogId = ");
        r10.append(r14);
        org.telegram.messenger.FileLog.d(r10.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x03fb, code lost:
        r49 = r10;
        r50 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x0403, code lost:
        if (r8 == 0) goto L_0x0415;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:0x0405, code lost:
        r1 = new org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox();
        r1.channel_id = r8;
        r1.max_id = r2;
        r10 = r24;
        r10.add(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x0415, code lost:
        r10 = r24;
        r1 = new org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x0420, code lost:
        if (r6 == 0) goto L_0x042e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x0422, code lost:
        r1.peer = new org.telegram.tgnet.TLRPC.TL_peerUser();
        r1.peer.user_id = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x042e, code lost:
        r1.peer = new org.telegram.tgnet.TLRPC.TL_peerChat();
        r1.peer.chat_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x0439, code lost:
        r1.max_id = r2;
        r10.add(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x043e, code lost:
        org.telegram.messenger.MessagesController.getInstance(r3).processUpdateArray(r10, (java.util.ArrayList<org.telegram.tgnet.TLRPC.User>) null, (java.util.ArrayList<org.telegram.tgnet.TLRPC.Chat>) null, false, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x044f, code lost:
        r62 = r3;
        r74 = r4;
        r67 = r5;
        r69 = r8;
        r53 = r12;
        r3 = r77;
        r8 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x045e, code lost:
        r49 = r10;
        r50 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x046a, code lost:
        if ("MESSAGE_DELETED".equals(r4) == false) goto L_0x04f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:?, code lost:
        r2 = r5.getString("messages");
        r10 = r2.split(",");
        r11 = new androidx.collection.LongSparseArray<>();
        r24 = new java.util.ArrayList<>();
        r25 = r2;
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x0488, code lost:
        r51 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x048b, code lost:
        if (r2 >= r10.length) goto L_0x049f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x048d, code lost:
        r7 = r24;
        r7.add(org.telegram.messenger.Utilities.parseInt(r10[r2]));
        r2 = r2 + 1;
        r24 = r7;
        r6 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x049f, code lost:
        r7 = r24;
        r53 = r12;
        r11.put(-r8, r7);
        org.telegram.messenger.NotificationsController.getInstance(r31).removeDeletedMessagesFromNotifications(r11);
        org.telegram.messenger.MessagesController.getInstance(r31).deleteMessagesByPush(r14, r7, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x04bd, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x04e7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x04bf, code lost:
        org.telegram.messenger.FileLog.d("GCM received " + r4 + " for dialogId = " + r14 + " mids = " + android.text.TextUtils.join(",", r7));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x04e7, code lost:
        r62 = r3;
        r74 = r4;
        r67 = r5;
        r69 = r8;
        r8 = r51;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x04f5, code lost:
        r51 = r6;
        r53 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x04fd, code lost:
        if (android.text.TextUtils.isEmpty(r4) != false) goto L_0x23ea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x0505, code lost:
        if (r5.has("msg_id") == false) goto L_0x050e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:?, code lost:
        r2 = r5.getInt("msg_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x050e, code lost:
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x0515, code lost:
        if (r5.has("random_id") == false) goto L_0x0526;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:273:?, code lost:
        r6 = org.telegram.messenger.Utilities.parseLong(r5.getString("random_id")).longValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x0526, code lost:
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0529, code lost:
        if (r2 == 0) goto L_0x0568;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:277:0x052b, code lost:
        r12 = org.telegram.messenger.MessagesController.getInstance(r31).dialogs_read_inbox_max.get(java.lang.Long.valueOf(r14));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x053b, code lost:
        if (r12 != null) goto L_0x055b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x053d, code lost:
        r30 = false;
        r12 = java.lang.Integer.valueOf(org.telegram.messenger.MessagesStorage.getInstance(r31).getDialogReadMax(false, r14));
        org.telegram.messenger.MessagesController.getInstance(r3).dialogs_read_inbox_max.put(java.lang.Long.valueOf(r14), r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x055b, code lost:
        r30 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0561, code lost:
        if (r2 <= r12.intValue()) goto L_0x0565;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x0563, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x0565, code lost:
        r11 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x056e, code lost:
        if (r6 == 0) goto L_0x057c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x0578, code lost:
        if (org.telegram.messenger.MessagesStorage.getInstance(r22).checkMessageByRandomId(r6) != false) goto L_0x057c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x057a, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x057c, code lost:
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x057e, code lost:
        if (r11 == false) goto L_0x23d7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x0580, code lost:
        r55 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:?, code lost:
        r12 = r5.optLong("chat_from_id", 0);
        r7 = "messages";
        r57 = r11;
        r58 = r5.optLong("chat_from_broadcast_id", 0);
        r60 = r5.optLong("chat_from_group_id", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x05a1, code lost:
        if (r12 != 0) goto L_0x05aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x05a5, code lost:
        if (r60 == 0) goto L_0x05a8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x05a8, code lost:
        r6 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x05aa, code lost:
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x05b1, code lost:
        if (r5.has("mention") == false) goto L_0x05bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x05b9, code lost:
        if (r5.getInt("mention") == 0) goto L_0x05bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x05bb, code lost:
        r10 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x05bd, code lost:
        r10 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x05c4, code lost:
        if (r5.has("silent") == false) goto L_0x05d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x05cc, code lost:
        if (r5.getInt("silent") == 0) goto L_0x05d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x05ce, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x05d0, code lost:
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x05d1, code lost:
        r62 = r3;
        r63 = r11;
        r11 = r50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x05dd, code lost:
        if (r11.has("loc_args") == false) goto L_0x0604;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:?, code lost:
        r3 = r11.getJSONArray("loc_args");
        r50 = r11;
        r11 = new java.lang.String[r3.length()];
        r64 = r10;
        r10 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x05f3, code lost:
        r65 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x05f6, code lost:
        if (r10 >= r11.length) goto L_0x060c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x05f8, code lost:
        r11[r10] = r3.getString(r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:0x05fe, code lost:
        r10 = r10 + 1;
        r12 = r65;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x0604, code lost:
        r64 = r10;
        r50 = r11;
        r65 = r12;
        r11 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:?, code lost:
        r12 = r11[0];
        r13 = null;
        r39 = r5.has("edit_date");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x0629, code lost:
        if (r4.startsWith("CHAT_") == false) goto L_0x066f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x062f, code lost:
        if (org.telegram.messenger.UserObject.isReplyUser(r14) == false) goto L_0x0654;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:339:0x0631, code lost:
        r3 = new java.lang.StringBuilder();
        r3.append(r12);
        r67 = r5;
        r3.append(" @ ");
        r38 = null;
        r3.append(r11[1]);
        r12 = r3.toString();
        r3 = false;
        r5 = false;
        r10 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x0654, code lost:
        r67 = r5;
        r38 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x065c, code lost:
        if (r8 == 0) goto L_0x0660;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x065e, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x0660, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x0661, code lost:
        r34 = r3;
        r13 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x0667, code lost:
        r12 = r11[1];
        r3 = r34;
        r5 = false;
        r10 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x066f, code lost:
        r67 = r5;
        r38 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x0679, code lost:
        if (r4.startsWith("PINNED_") == false) goto L_0x068d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:351:0x067f, code lost:
        if (r8 == 0) goto L_0x0683;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x0681, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x0683, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x0684, code lost:
        r34 = r3;
        r5 = true;
        r10 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x0693, code lost:
        if (r4.startsWith("CHANNEL_") == false) goto L_0x069e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x0695, code lost:
        r3 = false;
        r5 = false;
        r10 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x069e, code lost:
        r3 = false;
        r5 = false;
        r10 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x06a6, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x06cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x06a8, code lost:
        r34 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:?, code lost:
        r12 = new java.lang.StringBuilder();
        r68 = r10;
        r12.append("GCM received message notification ");
        r12.append(r4);
        r12.append(" for dialogId = ");
        r12.append(r14);
        r12.append(" mid = ");
        r12.append(r2);
        org.telegram.messenger.FileLog.d(r12.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x06cf, code lost:
        r68 = r10;
        r34 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x06d7, code lost:
        switch(r4.hashCode()) {
            case -2100047043: goto L_0x0c0c;
            case -2091498420: goto L_0x0CLASSNAME;
            case -2053872415: goto L_0x0bf6;
            case -2039746363: goto L_0x0beb;
            case -2023218804: goto L_0x0be0;
            case -1979538588: goto L_0x0bd5;
            case -1979536003: goto L_0x0bca;
            case -1979535888: goto L_0x0bbf;
            case -1969004705: goto L_0x0bb4;
            case -1946699248: goto L_0x0ba8;
            case -1717283471: goto L_0x0b9c;
            case -1646640058: goto L_0x0b90;
            case -1528047021: goto L_0x0b84;
            case -1493579426: goto L_0x0b78;
            case -1482481933: goto L_0x0b6c;
            case -1480102982: goto L_0x0b61;
            case -1478041834: goto L_0x0b55;
            case -1474543101: goto L_0x0b4a;
            case -1465695932: goto L_0x0b3e;
            case -1374906292: goto L_0x0b32;
            case -1372940586: goto L_0x0b26;
            case -1264245338: goto L_0x0b1a;
            case -1236154001: goto L_0x0b0e;
            case -1236086700: goto L_0x0b02;
            case -1236077786: goto L_0x0af6;
            case -1235796237: goto L_0x0aea;
            case -1235760759: goto L_0x0ade;
            case -1235686303: goto L_0x0ad3;
            case -1198046100: goto L_0x0ac8;
            case -1124254527: goto L_0x0abc;
            case -1085137927: goto L_0x0ab0;
            case -1084856378: goto L_0x0aa4;
            case -1084820900: goto L_0x0a98;
            case -1084746444: goto L_0x0a8c;
            case -819729482: goto L_0x0a80;
            case -772141857: goto L_0x0a74;
            case -638310039: goto L_0x0a68;
            case -590403924: goto L_0x0a5c;
            case -589196239: goto L_0x0a50;
            case -589193654: goto L_0x0a44;
            case -589193539: goto L_0x0a38;
            case -440169325: goto L_0x0a2c;
            case -412748110: goto L_0x0a20;
            case -228518075: goto L_0x0a14;
            case -213586509: goto L_0x0a08;
            case -115582002: goto L_0x09fc;
            case -112621464: goto L_0x09f0;
            case -108522133: goto L_0x09e4;
            case -107572034: goto L_0x09d9;
            case -40534265: goto L_0x09cd;
            case 65254746: goto L_0x09c1;
            case 141040782: goto L_0x09b5;
            case 202550149: goto L_0x09a9;
            case 309993049: goto L_0x099d;
            case 309995634: goto L_0x0991;
            case 309995749: goto L_0x0985;
            case 320532812: goto L_0x0979;
            case 328933854: goto L_0x096d;
            case 331340546: goto L_0x0961;
            case 342406591: goto L_0x0955;
            case 344816990: goto L_0x0949;
            case 346878138: goto L_0x093d;
            case 350376871: goto L_0x0931;
            case 608430149: goto L_0x0925;
            case 615714517: goto L_0x091a;
            case 715508879: goto L_0x090e;
            case 728985323: goto L_0x0902;
            case 731046471: goto L_0x08f6;
            case 734545204: goto L_0x08ea;
            case 802032552: goto L_0x08de;
            case 991498806: goto L_0x08d2;
            case 1007364121: goto L_0x08c6;
            case 1019850010: goto L_0x08ba;
            case 1019917311: goto L_0x08ae;
            case 1019926225: goto L_0x08a2;
            case 1020207774: goto L_0x0896;
            case 1020243252: goto L_0x088a;
            case 1020317708: goto L_0x087e;
            case 1060282259: goto L_0x0872;
            case 1060349560: goto L_0x0866;
            case 1060358474: goto L_0x085a;
            case 1060640023: goto L_0x084e;
            case 1060675501: goto L_0x0842;
            case 1060749957: goto L_0x0837;
            case 1073049781: goto L_0x082b;
            case 1078101399: goto L_0x081f;
            case 1110103437: goto L_0x0813;
            case 1160762272: goto L_0x0807;
            case 1172918249: goto L_0x07fb;
            case 1234591620: goto L_0x07ef;
            case 1281128640: goto L_0x07e3;
            case 1281131225: goto L_0x07d7;
            case 1281131340: goto L_0x07cb;
            case 1310789062: goto L_0x07c0;
            case 1333118583: goto L_0x07b4;
            case 1361447897: goto L_0x07a8;
            case 1498266155: goto L_0x079c;
            case 1533804208: goto L_0x0790;
            case 1540131626: goto L_0x0784;
            case 1547988151: goto L_0x0778;
            case 1561464595: goto L_0x076c;
            case 1563525743: goto L_0x0760;
            case 1567024476: goto L_0x0754;
            case 1810705077: goto L_0x0748;
            case 1815177512: goto L_0x073c;
            case 1954774321: goto L_0x0730;
            case 1963241394: goto L_0x0724;
            case 2014789757: goto L_0x0718;
            case 2022049433: goto L_0x070c;
            case 2034984710: goto L_0x0700;
            case 2048733346: goto L_0x06f4;
            case 2099392181: goto L_0x06e8;
            case 2140162142: goto L_0x06dc;
            default: goto L_0x06da;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x06e2, code lost:
        if (r4.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x06e4, code lost:
        r1 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x06ee, code lost:
        if (r4.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:375:0x06f0, code lost:
        r1 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x06fa, code lost:
        if (r4.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x06fc, code lost:
        r1 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x0706, code lost:
        if (r4.equals("CHANNEL_MESSAGE_PLAYLIST") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:381:0x0708, code lost:
        r1 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x0712, code lost:
        if (r4.equals("PINNED_CONTACT") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x0714, code lost:
        r1 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x071e, code lost:
        if (r4.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x0720, code lost:
        r1 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x072a, code lost:
        if (r4.equals("LOCKED_MESSAGE") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x072c, code lost:
        r1 = 'k';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x0736, code lost:
        if (r4.equals("CHAT_MESSAGE_PLAYLIST") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x0738, code lost:
        r1 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x0742, code lost:
        if (r4.equals("CHANNEL_MESSAGES") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x0744, code lost:
        r1 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x074e, code lost:
        if (r4.equals("MESSAGE_INVOICE") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x0750, code lost:
        r1 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x075a, code lost:
        if (r4.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x075c, code lost:
        r1 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x0766, code lost:
        if (r4.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x0768, code lost:
        r1 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x0772, code lost:
        if (r4.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x0774, code lost:
        r1 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x077e, code lost:
        if (r4.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x0780, code lost:
        r1 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x078a, code lost:
        if (r4.equals("MESSAGE_PLAYLIST") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x078c, code lost:
        r1 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x0796, code lost:
        if (r4.equals("MESSAGE_VIDEOS") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x0798, code lost:
        r1 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x07a2, code lost:
        if (r4.equals("PHONE_CALL_MISSED") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x07a4, code lost:
        r1 = 'p';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x07ae, code lost:
        if (r4.equals("MESSAGE_PHOTOS") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x07b0, code lost:
        r1 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x07ba, code lost:
        if (r4.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:426:0x07bc, code lost:
        r1 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x07c6, code lost:
        if (r4.equals("MESSAGE_NOTEXT") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x07c8, code lost:
        r1 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x07d1, code lost:
        if (r4.equals("MESSAGE_GIF") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:432:0x07d3, code lost:
        r1 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x07dd, code lost:
        if (r4.equals("MESSAGE_GEO") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x07df, code lost:
        r1 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x07e9, code lost:
        if (r4.equals("MESSAGE_DOC") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x07eb, code lost:
        r1 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x07f5, code lost:
        if (r4.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x07f7, code lost:
        r1 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x0801, code lost:
        if (r4.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:444:0x0803, code lost:
        r1 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x080d, code lost:
        if (r4.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x080f, code lost:
        r1 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x0819, code lost:
        if (r4.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x081b, code lost:
        r1 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x0825, code lost:
        if (r4.equals("CHAT_TITLE_EDITED") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x0827, code lost:
        r1 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x0831, code lost:
        if (r4.equals("PINNED_NOTEXT") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:456:0x0833, code lost:
        r1 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:458:0x083d, code lost:
        if (r4.equals("MESSAGE_TEXT") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x083f, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x0848, code lost:
        if (r4.equals("MESSAGE_QUIZ") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x084a, code lost:
        r1 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x0854, code lost:
        if (r4.equals("MESSAGE_POLL") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x0856, code lost:
        r1 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x0860, code lost:
        if (r4.equals("MESSAGE_GAME") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x0862, code lost:
        r1 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x086c, code lost:
        if (r4.equals("MESSAGE_FWDS") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x086e, code lost:
        r1 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0878, code lost:
        if (r4.equals("MESSAGE_DOCS") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x087a, code lost:
        r1 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x0884, code lost:
        if (r4.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x0886, code lost:
        r1 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0890, code lost:
        if (r4.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:480:0x0892, code lost:
        r1 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x089c, code lost:
        if (r4.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x089e, code lost:
        r1 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x08a8, code lost:
        if (r4.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:486:0x08aa, code lost:
        r1 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x08b4, code lost:
        if (r4.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x08b6, code lost:
        r1 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x08c0, code lost:
        if (r4.equals("CHAT_MESSAGE_DOCS") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x08c2, code lost:
        r1 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x08cc, code lost:
        if (r4.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x08ce, code lost:
        r1 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x08d8, code lost:
        if (r4.equals("PINNED_GEOLIVE") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x08da, code lost:
        r1 = 'b';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x08e4, code lost:
        if (r4.equals("MESSAGE_CONTACT") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x08e6, code lost:
        r1 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x08f0, code lost:
        if (r4.equals("PINNED_VIDEO") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x08f2, code lost:
        r1 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x08fc, code lost:
        if (r4.equals("PINNED_ROUND") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x08fe, code lost:
        r1 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x0908, code lost:
        if (r4.equals("PINNED_PHOTO") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x090a, code lost:
        r1 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x0914, code lost:
        if (r4.equals("PINNED_AUDIO") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x0916, code lost:
        r1 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x0920, code lost:
        if (r4.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x0922, code lost:
        r1 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x092b, code lost:
        if (r4.equals("CHAT_VOICECHAT_INVITE_YOU") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:519:0x092d, code lost:
        r1 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x0937, code lost:
        if (r4.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x0939, code lost:
        r1 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0943, code lost:
        if (r4.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x0945, code lost:
        r1 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x094f, code lost:
        if (r4.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x0951, code lost:
        r1 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x095b, code lost:
        if (r4.equals("CHAT_VOICECHAT_END") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x095d, code lost:
        r1 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0967, code lost:
        if (r4.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x0969, code lost:
        r1 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x0973, code lost:
        if (r4.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x0975, code lost:
        r1 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x097f, code lost:
        if (r4.equals("MESSAGES") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:540:0x0981, code lost:
        r1 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x098b, code lost:
        if (r4.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x098d, code lost:
        r1 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0997, code lost:
        if (r4.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x0999, code lost:
        r1 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x09a3, code lost:
        if (r4.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x09a5, code lost:
        r1 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x09af, code lost:
        if (r4.equals("CHAT_VOICECHAT_INVITE") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:552:0x09b1, code lost:
        r1 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x09bb, code lost:
        if (r4.equals("CHAT_LEFT") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x09bd, code lost:
        r1 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x09c7, code lost:
        if (r4.equals("CHAT_ADD_YOU") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x09c9, code lost:
        r1 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x09d3, code lost:
        if (r4.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x09d5, code lost:
        r1 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x09df, code lost:
        if (r4.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x09e1, code lost:
        r1 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x09ea, code lost:
        if (r4.equals("AUTH_REGION") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x09ec, code lost:
        r1 = 'j';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x09f6, code lost:
        if (r4.equals("CONTACT_JOINED") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x09f8, code lost:
        r1 = 'h';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x0a02, code lost:
        if (r4.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x0a04, code lost:
        r1 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x0a0e, code lost:
        if (r4.equals("ENCRYPTION_REQUEST") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:576:0x0a10, code lost:
        r1 = 'l';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x0a1a, code lost:
        if (r4.equals("MESSAGE_GEOLIVE") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x0a1c, code lost:
        r1 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x0a26, code lost:
        if (r4.equals("CHAT_DELETE_YOU") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:582:0x0a28, code lost:
        r1 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x0a32, code lost:
        if (r4.equals("AUTH_UNKNOWN") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x0a34, code lost:
        r1 = 'i';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x0a3e, code lost:
        if (r4.equals("PINNED_GIF") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:588:0x0a40, code lost:
        r1 = 'f';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x0a4a, code lost:
        if (r4.equals("PINNED_GEO") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x0a4c, code lost:
        r1 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0a56, code lost:
        if (r4.equals("PINNED_DOC") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x0a58, code lost:
        r1 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x0a62, code lost:
        if (r4.equals("PINNED_GAME_SCORE") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0a64, code lost:
        r1 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0a6e, code lost:
        if (r4.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:600:0x0a70, code lost:
        r1 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0a7a, code lost:
        if (r4.equals("PHONE_CALL_REQUEST") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0a7c, code lost:
        r1 = 'n';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0a86, code lost:
        if (r4.equals("PINNED_STICKER") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x0a88, code lost:
        r1 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x0a92, code lost:
        if (r4.equals("PINNED_TEXT") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x0a94, code lost:
        r1 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x0a9e, code lost:
        if (r4.equals("PINNED_QUIZ") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:612:0x0aa0, code lost:
        r1 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x0aaa, code lost:
        if (r4.equals("PINNED_POLL") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x0aac, code lost:
        r1 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x0ab6, code lost:
        if (r4.equals("PINNED_GAME") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:618:0x0ab8, code lost:
        r1 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0ac2, code lost:
        if (r4.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x0ac4, code lost:
        r1 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x0ace, code lost:
        if (r4.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x0ad0, code lost:
        r1 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x0ad9, code lost:
        if (r4.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:627:0x0adb, code lost:
        r1 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x0ae4, code lost:
        if (r4.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x0ae6, code lost:
        r1 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x0af0, code lost:
        if (r4.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x0af2, code lost:
        r1 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x0afc, code lost:
        if (r4.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x0afe, code lost:
        r1 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x0b08, code lost:
        if (r4.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x0b0a, code lost:
        r1 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x0b14, code lost:
        if (r4.equals("CHANNEL_MESSAGE_DOCS") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0b16, code lost:
        r1 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0b20, code lost:
        if (r4.equals("PINNED_INVOICE") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x0b22, code lost:
        r1 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x0b2c, code lost:
        if (r4.equals("CHAT_RETURNED") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x0b2e, code lost:
        r1 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0b38, code lost:
        if (r4.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x0b3a, code lost:
        r1 = 'g';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0b44, code lost:
        if (r4.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0b46, code lost:
        r1 = 'm';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0b50, code lost:
        if (r4.equals("MESSAGE_VIDEO") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0b52, code lost:
        r1 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0b5b, code lost:
        if (r4.equals("MESSAGE_ROUND") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0b5d, code lost:
        r1 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0b67, code lost:
        if (r4.equals("MESSAGE_PHOTO") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x0b69, code lost:
        r1 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x0b72, code lost:
        if (r4.equals("MESSAGE_MUTED") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0b74, code lost:
        r1 = 'o';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0b7e, code lost:
        if (r4.equals("MESSAGE_AUDIO") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0b80, code lost:
        r1 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:671:0x0b8a, code lost:
        if (r4.equals("CHAT_MESSAGES") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0b8c, code lost:
        r1 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0b96, code lost:
        if (r4.equals("CHAT_VOICECHAT_START") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0b98, code lost:
        r1 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x0ba2, code lost:
        if (r4.equals("CHAT_REQ_JOINED") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0ba4, code lost:
        r1 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0bae, code lost:
        if (r4.equals("CHAT_JOINED") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0bb0, code lost:
        r1 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0bba, code lost:
        if (r4.equals("CHAT_ADD_MEMBER") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0bbc, code lost:
        r1 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0bc5, code lost:
        if (r4.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x0bc7, code lost:
        r1 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0bd0, code lost:
        if (r4.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0bd2, code lost:
        r1 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0bdb, code lost:
        if (r4.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0bdd, code lost:
        r1 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0be6, code lost:
        if (r4.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0be8, code lost:
        r1 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0bf1, code lost:
        if (r4.equals("MESSAGE_STICKER") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0bf3, code lost:
        r1 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0bfc, code lost:
        if (r4.equals("CHAT_CREATED") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0bfe, code lost:
        r1 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0CLASSNAME, code lost:
        if (r4.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0CLASSNAME, code lost:
        r1 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0CLASSNAME, code lost:
        if (r4.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0CLASSNAME, code lost:
        r1 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0CLASSNAME, code lost:
        r1 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0CLASSNAME, code lost:
        r26 = r13;
        r69 = r8;
        r71 = r3;
        r72 = r5;
        r73 = r2;
        r74 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0CLASSNAME, code lost:
        switch(r1) {
            case 0: goto L_0x2226;
            case 1: goto L_0x2226;
            case 2: goto L_0x2200;
            case 3: goto L_0x21da;
            case 4: goto L_0x21b4;
            case 5: goto L_0x218e;
            case 6: goto L_0x2168;
            case 7: goto L_0x214a;
            case 8: goto L_0x2124;
            case 9: goto L_0x20fe;
            case 10: goto L_0x2090;
            case 11: goto L_0x206a;
            case 12: goto L_0x203f;
            case 13: goto L_0x2014;
            case 14: goto L_0x1fe9;
            case 15: goto L_0x1fc3;
            case 16: goto L_0x1f9d;
            case 17: goto L_0x1var_;
            case 18: goto L_0x1f4c;
            case 19: goto L_0x1var_;
            case 20: goto L_0x1var_;
            case 21: goto L_0x1efa;
            case 22: goto L_0x1ec9;
            case 23: goto L_0x1e9a;
            case 24: goto L_0x1e6b;
            case 25: goto L_0x1e3c;
            case 26: goto L_0x1e0d;
            case 27: goto L_0x1dee;
            case 28: goto L_0x1dc8;
            case 29: goto L_0x1da2;
            case 30: goto L_0x1d7c;
            case 31: goto L_0x1d56;
            case 32: goto L_0x1d30;
            case 33: goto L_0x1cc2;
            case 34: goto L_0x1c9c;
            case 35: goto L_0x1CLASSNAME;
            case 36: goto L_0x1CLASSNAME;
            case 37: goto L_0x1c1b;
            case 38: goto L_0x1bf5;
            case 39: goto L_0x1bcf;
            case 40: goto L_0x1ba9;
            case 41: goto L_0x1b83;
            case 42: goto L_0x1b4e;
            case 43: goto L_0x1b1f;
            case 44: goto L_0x1af0;
            case 45: goto L_0x1ac1;
            case 46: goto L_0x1a92;
            case 47: goto L_0x1a73;
            case 48: goto L_0x1a4a;
            case 49: goto L_0x1a1f;
            case 50: goto L_0x19f4;
            case 51: goto L_0x19c9;
            case 52: goto L_0x199e;
            case 53: goto L_0x1973;
            case 54: goto L_0x18e6;
            case 55: goto L_0x18bb;
            case 56: goto L_0x188b;
            case 57: goto L_0x185b;
            case 58: goto L_0x182b;
            case 59: goto L_0x1800;
            case 60: goto L_0x17d5;
            case 61: goto L_0x17aa;
            case 62: goto L_0x177a;
            case 63: goto L_0x174e;
            case 64: goto L_0x171e;
            case 65: goto L_0x16fc;
            case 66: goto L_0x16fc;
            case 67: goto L_0x16da;
            case 68: goto L_0x16b8;
            case 69: goto L_0x1691;
            case 70: goto L_0x166f;
            case 71: goto L_0x1648;
            case 72: goto L_0x1626;
            case 73: goto L_0x1604;
            case 74: goto L_0x15e2;
            case 75: goto L_0x15c0;
            case 76: goto L_0x159e;
            case 77: goto L_0x157c;
            case 78: goto L_0x155a;
            case 79: goto L_0x1538;
            case 80: goto L_0x1502;
            case 81: goto L_0x14ce;
            case 82: goto L_0x149a;
            case 83: goto L_0x1466;
            case 84: goto L_0x1432;
            case 85: goto L_0x140e;
            case 86: goto L_0x139b;
            case 87: goto L_0x1332;
            case 88: goto L_0x12c9;
            case 89: goto L_0x1260;
            case 90: goto L_0x11f7;
            case 91: goto L_0x118e;
            case 92: goto L_0x109b;
            case 93: goto L_0x1032;
            case 94: goto L_0x0fbf;
            case 95: goto L_0x0f4c;
            case 96: goto L_0x0ed9;
            case 97: goto L_0x0e70;
            case 98: goto L_0x0e07;
            case 99: goto L_0x0d9e;
            case 100: goto L_0x0d35;
            case 101: goto L_0x0ccc;
            case 102: goto L_0x0CLASSNAME;
            case 103: goto L_0x0CLASSNAME;
            case 104: goto L_0x0c3e;
            case 105: goto L_0x0c3e;
            case 106: goto L_0x0c3e;
            case 107: goto L_0x0c3e;
            case 108: goto L_0x0c3e;
            case 109: goto L_0x0c3e;
            case 110: goto L_0x0c3e;
            case 111: goto L_0x0c3e;
            case 112: goto L_0x0c3e;
            default: goto L_0x0CLASSNAME;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0c3e, code lost:
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0CLASSNAME, code lost:
        r3 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
        r12 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
        r1 = r3;
        r2 = true;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0CLASSNAME, code lost:
        if (r14 <= 0) goto L_0x0c8b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0c8b, code lost:
        if (r6 == false) goto L_0x0caf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:0x0c8d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0caf, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r11[0]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0cd0, code lost:
        if (r14 <= 0) goto L_0x0cf4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0cd2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceUser", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0cf4, code lost:
        if (r6 == false) goto L_0x0d18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:727:0x0cf6, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0d18, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r11[0]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0d39, code lost:
        if (r14 <= 0) goto L_0x0d5d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x0d3b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreUser", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0d5d, code lost:
        if (r6 == false) goto L_0x0d81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0d5f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0d81, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r11[0]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0da2, code lost:
        if (r14 <= 0) goto L_0x0dc6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0da4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0dc6, code lost:
        if (r6 == false) goto L_0x0dea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x0dc8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0dea, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r11[0]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0e0b, code lost:
        if (r14 <= 0) goto L_0x0e2f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x0e0d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x0e2f, code lost:
        if (r6 == false) goto L_0x0e53;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x0e31, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x0e53, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r11[0]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x0e74, code lost:
        if (r14 <= 0) goto L_0x0e98;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0e76, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x0e98, code lost:
        if (r6 == false) goto L_0x0ebc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x0e9a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x0ebc, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r11[0]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x0edd, code lost:
        if (r14 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x0edf, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x0var_, code lost:
        if (r6 == false) goto L_0x0f2a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r11[0], r11[2], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x0f2a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x0var_, code lost:
        if (r14 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x0var_, code lost:
        if (r6 == false) goto L_0x0f9d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r11[0], r11[2], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x0f9d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x0fc3, code lost:
        if (r14 <= 0) goto L_0x0fe7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x0fc5, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x0fe7, code lost:
        if (r6 == false) goto L_0x1010;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x0fe9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r11[0], r11[2], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x1010, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x1036, code lost:
        if (r14 <= 0) goto L_0x105a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x1038, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x105a, code lost:
        if (r6 == false) goto L_0x107e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x105c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x107e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r11[0]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x109f, code lost:
        if (r14 <= 0) goto L_0x10ec;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x10a3, code lost:
        if (r11.length <= 1) goto L_0x10cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x10ab, code lost:
        if (android.text.TextUtils.isEmpty(r11[1]) != false) goto L_0x10cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x10ad, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x10cf, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r11[0]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x10ec, code lost:
        if (r6 == false) goto L_0x1143;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x10f0, code lost:
        if (r11.length <= 2) goto L_0x1121;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x10f8, code lost:
        if (android.text.TextUtils.isEmpty(r11[2]) != false) goto L_0x1121;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x10fa, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r11[0], r11[2], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x1121, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x1145, code lost:
        if (r11.length <= 1) goto L_0x1171;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x114d, code lost:
        if (android.text.TextUtils.isEmpty(r11[1]) != false) goto L_0x1171;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x114f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x1171, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r11[0]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x1192, code lost:
        if (r14 <= 0) goto L_0x11b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x1194, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x11b6, code lost:
        if (r6 == false) goto L_0x11da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x11b8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x11da, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r11[0]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x11fb, code lost:
        if (r14 <= 0) goto L_0x121f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x11fd, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x121f, code lost:
        if (r6 == false) goto L_0x1243;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x1221, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x1243, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r11[0]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x1264, code lost:
        if (r14 <= 0) goto L_0x1288;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x1266, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x1288, code lost:
        if (r6 == false) goto L_0x12ac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x128a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x12ac, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r11[0]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x12cd, code lost:
        if (r14 <= 0) goto L_0x12f1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x12cf, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x12f1, code lost:
        if (r6 == false) goto L_0x1315;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x12f3, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x1315, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r11[0]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x1336, code lost:
        if (r14 <= 0) goto L_0x135a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x1338, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x135a, code lost:
        if (r6 == false) goto L_0x137e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x135c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x137e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r11[0]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x139f, code lost:
        if (r14 <= 0) goto L_0x13c3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x13a1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x13c3, code lost:
        if (r6 == false) goto L_0x13ec;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x13c5, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r11[0], r11[1], r11[2]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x13ec, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x140e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x1432, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r11[0], r11[1], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r11[2]).intValue()));
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x1466, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r11[0], r11[1], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r11[2]).intValue()));
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x149a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r11[0], r11[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r11[2]).intValue()));
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x14ce, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r11[0], r11[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r11[2]).intValue()));
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x1502, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r11[0], r11[1], org.telegram.messenger.LocaleController.formatPluralString(r7, org.telegram.messenger.Utilities.parseInt(r11[2]).intValue()));
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x1538, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("UserAcceptedToGroupPushWithGroup", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x155a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x157c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x159e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x15c0, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x15e2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x1604, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x1626, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupEndedCall", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x1648, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r11[0], r11[1], r11[2]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x166f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x1691, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r11[0], r11[1], r11[2]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:851:0x16b8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x16da, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x16fc, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r11[0], r11[1]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x171e, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r11[0], r11[1], r11[2]);
        r10 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x174e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r11[0], r11[1], r11[2], r11[3]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:856:0x177a, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r11[0], r11[1], r11[2]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x17aa, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r11[0], r11[1]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x17d5, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r11[0], r11[1]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x1800, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r11[0], r11[1]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x182b, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r11[0], r11[1], r11[2]);
        r10 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x185b, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r11[0], r11[1], r11[2]);
        r10 = org.telegram.messenger.LocaleController.getString("PollQuiz", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:862:0x188b, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r11[0], r11[1], r11[2]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:863:0x18bb, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r11[0], r11[1]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:865:0x18e8, code lost:
        if (r11.length <= 2) goto L_0x1935;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x18f0, code lost:
        if (android.text.TextUtils.isEmpty(r11[2]) != false) goto L_0x1935;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x18f2, code lost:
        r10 = r11[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r11[0], r11[1], r11[2]);
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x1935, code lost:
        r10 = r11[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r11[0], r11[1]);
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x1973, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r11[0], r11[1]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:871:0x199e, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r11[0], r11[1]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x19c9, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r11[0], r11[1]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x19f4, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r11[0], r11[1]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x1a1f, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r11[0], r11[1]);
        r10 = org.telegram.messenger.LocaleController.getString("Message", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x1a4a, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r11[0], r11[1], r11[2]);
        r10 = r11[2];
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x1a73, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r11[0]);
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x1a92, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r11[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r11[1]).intValue()));
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x1ac1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r11[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r11[1]).intValue()));
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x1af0, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r11[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r11[1]).intValue()));
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x1b1f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r11[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r11[1]).intValue()));
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x1b4e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r11[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r11[1]).intValue()).toLowerCase());
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x1b83, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x1ba9, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x1bcf, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:0x1bf5, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x1c1b, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r11[0], r11[1]);
        r10 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x1CLASSNAME, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r11[0], r11[1]);
        r10 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x1CLASSNAME, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r11[0], r11[1]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x1c9c, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x1cc4, code lost:
        if (r11.length <= 1) goto L_0x1d0c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x1ccc, code lost:
        if (android.text.TextUtils.isEmpty(r11[1]) != false) goto L_0x1d0c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x1cce, code lost:
        r10 = r11[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r11[0], r11[1]);
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x1d0c, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x1d30, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:897:0x1d56, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x1d7c, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:899:0x1da2, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:0x1dc8, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("Message", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x1dee, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r11[0]);
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x1e0d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r11[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r11[1]).intValue()));
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:0x1e3c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r11[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r11[1]).intValue()));
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:0x1e6b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r11[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r11[1]).intValue()));
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x1e9a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r11[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r11[1]).intValue()));
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:906:0x1ec9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r11[0], org.telegram.messenger.LocaleController.formatPluralString(r7, org.telegram.messenger.Utilities.parseInt(r11[1]).intValue()));
        r3 = r1;
        r2 = true;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:0x1efa, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r11[0], r11[1]);
        r10 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x1var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r11[0], r11[1], r11[2]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:909:0x1f4c, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r11[0], r11[1]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:910:0x1var_, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:0x1f9d, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:0x1fc3, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:913:0x1fe9, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r11[0], r11[1]);
        r10 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:0x2014, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", NUM, r11[0], r11[1]);
        r10 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:0x203f, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r11[0], r11[1]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x206a, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:918:0x2092, code lost:
        if (r11.length <= 1) goto L_0x20da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:920:0x209a, code lost:
        if (android.text.TextUtils.isEmpty(r11[1]) != false) goto L_0x20da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x209c, code lost:
        r10 = r11[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r11[0], r11[1]);
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x20da, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x20fe, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x2124, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x214a, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r11[0]);
        r3 = r1;
        r2 = false;
        r12 = r34;
        r10 = r38;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x2168, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x218e, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:928:0x21b4, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:0x21da, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x2200, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r11[0]);
        r10 = org.telegram.messenger.LocaleController.getString("Message", NUM);
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x2226, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r11[0], r11[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:932:0x223f, code lost:
        r10 = r11[1];
        r1 = r3;
        r2 = false;
        r12 = r34;
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x2248, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x2249, code lost:
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:935:0x224d, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x227e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:?, code lost:
        r1 = new java.lang.StringBuilder();
        r1.append("unhandled loc_key = ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:0x2259, code lost:
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x225b, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:?, code lost:
        r1.append(r3);
        org.telegram.messenger.FileLog.w(r1.toString());
        r3 = r3;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:0x2266, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:0x2267, code lost:
        r2 = r0;
        r4 = r3;
        r5 = r18;
        r1 = r31;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:943:0x2271, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:944:0x2272, code lost:
        r2 = r0;
        r4 = r74;
        r5 = r18;
        r1 = r31;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:0x227e, code lost:
        r3 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:946:0x2280, code lost:
        r2 = false;
        r12 = r34;
        r1 = null;
        r10 = r38;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x2288, code lost:
        if (r1 == null) goto L_0x23c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x228a, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:?, code lost:
        r5 = new org.telegram.tgnet.TLRPC.TL_message();
        r7 = r73;
        r5.id = r7;
        r8 = r55;
        r5.random_id = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x2297, code lost:
        if (r10 == null) goto L_0x229b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x2299, code lost:
        r13 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x229b, code lost:
        r13 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x229c, code lost:
        r5.message = r13;
        r4 = r5;
        r4.date = (int) (r79 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:0x22a7, code lost:
        if (r72 == false) goto L_0x22b0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x22a9, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:?, code lost:
        r4.action = new org.telegram.tgnet.TLRPC.TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:957:0x22b0, code lost:
        if (r71 == false) goto L_0x22b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:0x22b2, code lost:
        r4.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:?, code lost:
        r4.dialog_id = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:962:0x22bf, code lost:
        if (r69 == 0) goto L_0x22da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:0x22c1, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:964:?, code lost:
        r4.peer_id = new org.telegram.tgnet.TLRPC.TL_peerChannel();
        r13 = r6;
        r73 = r7;
        r6 = r69;
        r4.peer_id.channel_id = r6;
        r69 = r6;
        r55 = r8;
        r8 = r51;
        r6 = r53;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:965:0x22da, code lost:
        r13 = r6;
        r73 = r7;
        r6 = r69;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:966:0x22e3, code lost:
        if (r53 == 0) goto L_0x22f9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:967:0x22e5, code lost:
        r4.peer_id = new org.telegram.tgnet.TLRPC.TL_peerChat();
        r69 = r6;
        r6 = r53;
        r4.peer_id.chat_id = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:968:0x22f4, code lost:
        r55 = r8;
        r8 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:969:0x22f9, code lost:
        r69 = r6;
        r6 = r53;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:971:?, code lost:
        r4.peer_id = new org.telegram.tgnet.TLRPC.TL_peerUser();
        r55 = r8;
        r4.peer_id.user_id = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:972:0x230c, code lost:
        r4.flags |= 256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:974:0x2316, code lost:
        if (r60 == 0) goto L_0x232a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:975:0x2318, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:976:?, code lost:
        r4.from_id = new org.telegram.tgnet.TLRPC.TL_peerChat();
        r4.from_id.chat_id = r6;
        r53 = r6;
        r51 = r58;
        r5 = r65;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:977:0x232a, code lost:
        r53 = r6;
        r5 = r58;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:978:0x2332, code lost:
        if (r5 == 0) goto L_0x2344;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:979:0x2334, code lost:
        r4.from_id = new org.telegram.tgnet.TLRPC.TL_peerChannel();
        r4.from_id.channel_id = r5;
        r51 = r5;
        r5 = r65;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:981:0x2348, code lost:
        if (r65 == 0) goto L_0x235a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:982:0x234a, code lost:
        r4.from_id = new org.telegram.tgnet.TLRPC.TL_peerUser();
        r51 = r5;
        r5 = r65;
        r4.from_id.user_id = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:983:0x235a, code lost:
        r51 = r5;
        r5 = r65;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:985:?, code lost:
        r4.from_id = r4.peer_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:986:0x2362, code lost:
        if (r64 != false) goto L_0x2369;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:987:0x2364, code lost:
        if (r72 == false) goto L_0x2367;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:989:0x2367, code lost:
        r7 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:990:0x2369, code lost:
        r7 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x236a, code lost:
        r4.mentioned = r7;
        r4.silent = r63;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:992:0x2370, code lost:
        r74 = r3;
        r3 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:994:?, code lost:
        r4.from_scheduled = r3;
        r27 = new java.util.ArrayList();
        r16 = r1;
        r1 = new org.telegram.messenger.MessageObject(r31, r4, r1, r12, r26, r2, r68, r71, r39);
        r24 = r2;
        r2 = r27;
        r2.add(r1);
        r30 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:995:0x23a7, code lost:
        r49 = r3;
        r32 = r4;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:996:0x23ad, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:997:?, code lost:
        r65 = r5;
        org.telegram.messenger.NotificationsController.getInstance(r31).processNewMessages(r2, true, true, r3.countDownLatch);
        r1 = false;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:998:0x23b9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:999:0x23ba, code lost:
        r74 = r3;
        r3 = r77;
        r2 = r0;
        r5 = r18;
        r1 = r31;
        r4 = r74;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1027:0x2474  */
    /* JADX WARNING: Removed duplicated region for block: B:1028:0x2484  */
    /* JADX WARNING: Removed duplicated region for block: B:1031:0x248b  */
    /* renamed from: lambda$onMessageReceived$3$org-telegram-messenger-GcmPushListenerService  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m68xa7b3420a(java.util.Map r78, long r79) {
        /*
            r77 = this;
            r1 = r77
            r2 = r78
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x000d
            java.lang.String r3 = "GCM START PROCESSING"
            org.telegram.messenger.FileLog.d(r3)
        L_0x000d:
            r3 = -1
            r4 = 0
            r5 = 0
            java.lang.String r7 = "p"
            java.lang.Object r7 = r2.get(r7)     // Catch:{ all -> 0x246a }
            boolean r8 = r7 instanceof java.lang.String     // Catch:{ all -> 0x246a }
            if (r8 != 0) goto L_0x0030
            boolean r8 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0027 }
            if (r8 == 0) goto L_0x0023
            java.lang.String r8 = "GCM DECRYPT ERROR 1"
            org.telegram.messenger.FileLog.d(r8)     // Catch:{ all -> 0x0027 }
        L_0x0023:
            r77.onDecryptError()     // Catch:{ all -> 0x0027 }
            return
        L_0x0027:
            r0 = move-exception
            r2 = r0
            r75 = r3
            r3 = r1
            r1 = r75
            goto L_0x2471
        L_0x0030:
            r8 = r7
            java.lang.String r8 = (java.lang.String) r8     // Catch:{ all -> 0x246a }
            r9 = 8
            byte[] r8 = android.util.Base64.decode(r8, r9)     // Catch:{ all -> 0x246a }
            org.telegram.tgnet.NativeByteBuffer r10 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x246a }
            int r11 = r8.length     // Catch:{ all -> 0x246a }
            r10.<init>((int) r11)     // Catch:{ all -> 0x246a }
            r10.writeBytes((byte[]) r8)     // Catch:{ all -> 0x246a }
            r11 = 0
            r10.position(r11)     // Catch:{ all -> 0x246a }
            byte[] r12 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x246a }
            if (r12 != 0) goto L_0x005b
            byte[] r12 = new byte[r9]     // Catch:{ all -> 0x0027 }
            org.telegram.messenger.SharedConfig.pushAuthKeyId = r12     // Catch:{ all -> 0x0027 }
            byte[] r12 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x0027 }
            byte[] r12 = org.telegram.messenger.Utilities.computeSHA1((byte[]) r12)     // Catch:{ all -> 0x0027 }
            int r13 = r12.length     // Catch:{ all -> 0x0027 }
            int r13 = r13 - r9
            byte[] r14 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x0027 }
            java.lang.System.arraycopy(r12, r13, r14, r11, r9)     // Catch:{ all -> 0x0027 }
        L_0x005b:
            byte[] r12 = new byte[r9]     // Catch:{ all -> 0x246a }
            r13 = 1
            r10.readBytes(r12, r13)     // Catch:{ all -> 0x246a }
            byte[] r14 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x246a }
            boolean r14 = java.util.Arrays.equals(r14, r12)     // Catch:{ all -> 0x246a }
            r15 = 3
            r6 = 2
            if (r14 != 0) goto L_0x0096
            r77.onDecryptError()     // Catch:{ all -> 0x0027 }
            boolean r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0027 }
            if (r9 == 0) goto L_0x0095
            java.util.Locale r9 = java.util.Locale.US     // Catch:{ all -> 0x0027 }
            java.lang.String r14 = "GCM DECRYPT ERROR 2 k1=%s k2=%s, key=%s"
            java.lang.Object[] r15 = new java.lang.Object[r15]     // Catch:{ all -> 0x0027 }
            byte[] r16 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x0027 }
            java.lang.String r16 = org.telegram.messenger.Utilities.bytesToHex(r16)     // Catch:{ all -> 0x0027 }
            r15[r11] = r16     // Catch:{ all -> 0x0027 }
            java.lang.String r11 = org.telegram.messenger.Utilities.bytesToHex(r12)     // Catch:{ all -> 0x0027 }
            r15[r13] = r11     // Catch:{ all -> 0x0027 }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x0027 }
            java.lang.String r11 = org.telegram.messenger.Utilities.bytesToHex(r11)     // Catch:{ all -> 0x0027 }
            r15[r6] = r11     // Catch:{ all -> 0x0027 }
            java.lang.String r6 = java.lang.String.format(r9, r14, r15)     // Catch:{ all -> 0x0027 }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ all -> 0x0027 }
        L_0x0095:
            return
        L_0x0096:
            r14 = 16
            byte[] r14 = new byte[r14]     // Catch:{ all -> 0x246a }
            r10.readBytes(r14, r13)     // Catch:{ all -> 0x246a }
            byte[] r15 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x246a }
            org.telegram.messenger.MessageKeyData r15 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r15, r14, r13, r6)     // Catch:{ all -> 0x246a }
            java.nio.ByteBuffer r6 = r10.buffer     // Catch:{ all -> 0x246a }
            byte[] r13 = r15.aesKey     // Catch:{ all -> 0x246a }
            byte[] r9 = r15.aesIv     // Catch:{ all -> 0x246a }
            r20 = 0
            r21 = 0
            r22 = 24
            int r11 = r8.length     // Catch:{ all -> 0x246a }
            int r23 = r11 + -24
            r17 = r6
            r18 = r13
            r19 = r9
            org.telegram.messenger.Utilities.aesIgeEncryption(r17, r18, r19, r20, r21, r22, r23)     // Catch:{ all -> 0x246a }
            byte[] r28 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x246a }
            r29 = 96
            r30 = 32
            java.nio.ByteBuffer r6 = r10.buffer     // Catch:{ all -> 0x246a }
            r32 = 24
            java.nio.ByteBuffer r9 = r10.buffer     // Catch:{ all -> 0x246a }
            int r33 = r9.limit()     // Catch:{ all -> 0x246a }
            r31 = r6
            byte[] r6 = org.telegram.messenger.Utilities.computeSHA256(r28, r29, r30, r31, r32, r33)     // Catch:{ all -> 0x246a }
            r9 = 8
            r11 = 0
            boolean r13 = org.telegram.messenger.Utilities.arraysEquals(r14, r11, r6, r9)     // Catch:{ all -> 0x246a }
            if (r13 != 0) goto L_0x00f8
            r77.onDecryptError()     // Catch:{ all -> 0x0027 }
            boolean r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0027 }
            if (r9 == 0) goto L_0x00f7
            java.lang.String r9 = "GCM DECRYPT ERROR 3, key = %s"
            r11 = 1
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x0027 }
            byte[] r13 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x0027 }
            java.lang.String r13 = org.telegram.messenger.Utilities.bytesToHex(r13)     // Catch:{ all -> 0x0027 }
            r16 = 0
            r11[r16] = r13     // Catch:{ all -> 0x0027 }
            java.lang.String r9 = java.lang.String.format(r9, r11)     // Catch:{ all -> 0x0027 }
            org.telegram.messenger.FileLog.d(r9)     // Catch:{ all -> 0x0027 }
        L_0x00f7:
            return
        L_0x00f8:
            r11 = 1
            int r13 = r10.readInt32(r11)     // Catch:{ all -> 0x246a }
            byte[] r9 = new byte[r13]     // Catch:{ all -> 0x246a }
            r10.readBytes(r9, r11)     // Catch:{ all -> 0x246a }
            java.lang.String r11 = new java.lang.String     // Catch:{ all -> 0x246a }
            r11.<init>(r9)     // Catch:{ all -> 0x246a }
            r5 = r11
            org.json.JSONObject r11 = new org.json.JSONObject     // Catch:{ all -> 0x2460 }
            r11.<init>(r5)     // Catch:{ all -> 0x2460 }
            r17 = r3
            java.lang.String r3 = "loc_key"
            boolean r3 = r11.has(r3)     // Catch:{ all -> 0x2458 }
            if (r3 == 0) goto L_0x0126
            java.lang.String r3 = "loc_key"
            java.lang.String r3 = r11.getString(r3)     // Catch:{ all -> 0x011f }
            r4 = r3
            goto L_0x0129
        L_0x011f:
            r0 = move-exception
            r2 = r0
            r3 = r1
            r1 = r17
            goto L_0x2471
        L_0x0126:
            java.lang.String r3 = ""
            r4 = r3
        L_0x0129:
            java.lang.String r3 = "custom"
            java.lang.Object r3 = r11.get(r3)     // Catch:{ all -> 0x244e }
            r18 = r5
            boolean r5 = r3 instanceof org.json.JSONObject     // Catch:{ all -> 0x2444 }
            if (r5 == 0) goto L_0x0145
            java.lang.String r5 = "custom"
            org.json.JSONObject r5 = r11.getJSONObject(r5)     // Catch:{ all -> 0x013c }
            goto L_0x014a
        L_0x013c:
            r0 = move-exception
            r2 = r0
            r3 = r1
            r1 = r17
            r5 = r18
            goto L_0x2471
        L_0x0145:
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ all -> 0x2444 }
            r5.<init>()     // Catch:{ all -> 0x2444 }
        L_0x014a:
            r19 = r3
            java.lang.String r3 = "user_id"
            boolean r3 = r11.has(r3)     // Catch:{ all -> 0x2444 }
            if (r3 == 0) goto L_0x015b
            java.lang.String r3 = "user_id"
            java.lang.Object r3 = r11.get(r3)     // Catch:{ all -> 0x013c }
            goto L_0x015c
        L_0x015b:
            r3 = 0
        L_0x015c:
            if (r3 != 0) goto L_0x0171
            int r20 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x013c }
            org.telegram.messenger.UserConfig r20 = org.telegram.messenger.UserConfig.getInstance(r20)     // Catch:{ all -> 0x013c }
            long r20 = r20.getClientUserId()     // Catch:{ all -> 0x013c }
            r75 = r20
            r20 = r6
            r21 = r7
            r6 = r75
            goto L_0x01b1
        L_0x0171:
            r20 = r6
            boolean r6 = r3 instanceof java.lang.Long     // Catch:{ all -> 0x2444 }
            if (r6 == 0) goto L_0x0185
            r6 = r3
            java.lang.Long r6 = (java.lang.Long) r6     // Catch:{ all -> 0x013c }
            long r21 = r6.longValue()     // Catch:{ all -> 0x013c }
            r75 = r21
            r21 = r7
            r6 = r75
            goto L_0x01b1
        L_0x0185:
            boolean r6 = r3 instanceof java.lang.Integer     // Catch:{ all -> 0x2444 }
            if (r6 == 0) goto L_0x0194
            r6 = r3
            java.lang.Integer r6 = (java.lang.Integer) r6     // Catch:{ all -> 0x013c }
            int r6 = r6.intValue()     // Catch:{ all -> 0x013c }
            r21 = r7
            long r6 = (long) r6
            goto L_0x01b1
        L_0x0194:
            r21 = r7
            boolean r6 = r3 instanceof java.lang.String     // Catch:{ all -> 0x2444 }
            if (r6 == 0) goto L_0x01a7
            r6 = r3
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ all -> 0x013c }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x013c }
            int r6 = r6.intValue()     // Catch:{ all -> 0x013c }
            long r6 = (long) r6
            goto L_0x01b1
        L_0x01a7:
            int r6 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x2444 }
            org.telegram.messenger.UserConfig r6 = org.telegram.messenger.UserConfig.getInstance(r6)     // Catch:{ all -> 0x2444 }
            long r6 = r6.getClientUserId()     // Catch:{ all -> 0x2444 }
        L_0x01b1:
            int r22 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x2444 }
            r23 = 0
            r28 = 0
            r29 = r3
            r3 = r28
        L_0x01bb:
            r28 = r8
            r8 = 3
            if (r3 >= r8) goto L_0x01d6
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r3)     // Catch:{ all -> 0x013c }
            long r30 = r8.getClientUserId()     // Catch:{ all -> 0x013c }
            int r8 = (r30 > r6 ? 1 : (r30 == r6 ? 0 : -1))
            if (r8 != 0) goto L_0x01d1
            r22 = r3
            r23 = 1
            goto L_0x01d6
        L_0x01d1:
            int r3 = r3 + 1
            r8 = r28
            goto L_0x01bb
        L_0x01d6:
            if (r23 != 0) goto L_0x01e7
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x013c }
            if (r3 == 0) goto L_0x01e1
            java.lang.String r3 = "GCM ACCOUNT NOT FOUND"
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ all -> 0x013c }
        L_0x01e1:
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch     // Catch:{ all -> 0x013c }
            r3.countDown()     // Catch:{ all -> 0x013c }
            return
        L_0x01e7:
            r31 = r22
            r3 = r22
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r31)     // Catch:{ all -> 0x243a }
            boolean r8 = r8.isClientActivated()     // Catch:{ all -> 0x243a }
            if (r8 != 0) goto L_0x020d
            boolean r8 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0204 }
            if (r8 == 0) goto L_0x01fe
            java.lang.String r8 = "GCM ACCOUNT NOT ACTIVATED"
            org.telegram.messenger.FileLog.d(r8)     // Catch:{ all -> 0x0204 }
        L_0x01fe:
            java.util.concurrent.CountDownLatch r8 = r1.countDownLatch     // Catch:{ all -> 0x0204 }
            r8.countDown()     // Catch:{ all -> 0x0204 }
            return
        L_0x0204:
            r0 = move-exception
            r2 = r0
            r3 = r1
        L_0x0207:
            r5 = r18
            r1 = r31
            goto L_0x2471
        L_0x020d:
            java.lang.String r8 = "google.sent_time"
            java.lang.Object r8 = r2.get(r8)     // Catch:{ all -> 0x243a }
            int r17 = r4.hashCode()     // Catch:{ all -> 0x243a }
            switch(r17) {
                case -1963663249: goto L_0x0239;
                case -920689527: goto L_0x022f;
                case 633004703: goto L_0x0225;
                case 1365673842: goto L_0x021b;
                default: goto L_0x021a;
            }
        L_0x021a:
            goto L_0x0243
        L_0x021b:
            java.lang.String r2 = "GEO_LIVE_PENDING"
            boolean r2 = r4.equals(r2)     // Catch:{ all -> 0x0204 }
            if (r2 == 0) goto L_0x021a
            r2 = 3
            goto L_0x0244
        L_0x0225:
            java.lang.String r2 = "MESSAGE_ANNOUNCEMENT"
            boolean r2 = r4.equals(r2)     // Catch:{ all -> 0x0204 }
            if (r2 == 0) goto L_0x021a
            r2 = 1
            goto L_0x0244
        L_0x022f:
            java.lang.String r2 = "DC_UPDATE"
            boolean r2 = r4.equals(r2)     // Catch:{ all -> 0x0204 }
            if (r2 == 0) goto L_0x021a
            r2 = 0
            goto L_0x0244
        L_0x0239:
            java.lang.String r2 = "SESSION_REVOKE"
            boolean r2 = r4.equals(r2)     // Catch:{ all -> 0x0204 }
            if (r2 == 0) goto L_0x021a
            r2 = 2
            goto L_0x0244
        L_0x0243:
            r2 = -1
        L_0x0244:
            switch(r2) {
                case 0: goto L_0x02bd;
                case 1: goto L_0x0273;
                case 2: goto L_0x0263;
                case 3: goto L_0x0251;
                default: goto L_0x0247;
            }
        L_0x0247:
            r40 = r6
            r17 = r8
            r6 = 0
            java.lang.String r2 = "channel_id"
            goto L_0x02ff
        L_0x0251:
            org.telegram.messenger.DispatchQueue r2 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x0204 }
            r40 = r6
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1 r6 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1     // Catch:{ all -> 0x0204 }
            r6.<init>(r3)     // Catch:{ all -> 0x0204 }
            r2.postRunnable(r6)     // Catch:{ all -> 0x0204 }
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x0204 }
            r2.countDown()     // Catch:{ all -> 0x0204 }
            return
        L_0x0263:
            r40 = r6
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0 r2 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0     // Catch:{ all -> 0x0204 }
            r2.<init>(r3)     // Catch:{ all -> 0x0204 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ all -> 0x0204 }
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x0204 }
            r2.countDown()     // Catch:{ all -> 0x0204 }
            return
        L_0x0273:
            r40 = r6
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r2 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x0204 }
            r2.<init>()     // Catch:{ all -> 0x0204 }
            r6 = 0
            r2.popup = r6     // Catch:{ all -> 0x0204 }
            r6 = 2
            r2.flags = r6     // Catch:{ all -> 0x0204 }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r6 = r79 / r6
            int r7 = (int) r6     // Catch:{ all -> 0x0204 }
            r2.inbox_date = r7     // Catch:{ all -> 0x0204 }
            java.lang.String r6 = "message"
            java.lang.String r6 = r11.getString(r6)     // Catch:{ all -> 0x0204 }
            r2.message = r6     // Catch:{ all -> 0x0204 }
            java.lang.String r6 = "announcement"
            r2.type = r6     // Catch:{ all -> 0x0204 }
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r6 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ all -> 0x0204 }
            r6.<init>()     // Catch:{ all -> 0x0204 }
            r2.media = r6     // Catch:{ all -> 0x0204 }
            org.telegram.tgnet.TLRPC$TL_updates r6 = new org.telegram.tgnet.TLRPC$TL_updates     // Catch:{ all -> 0x0204 }
            r6.<init>()     // Catch:{ all -> 0x0204 }
            java.util.ArrayList r7 = r6.updates     // Catch:{ all -> 0x0204 }
            r7.add(r2)     // Catch:{ all -> 0x0204 }
            org.telegram.messenger.DispatchQueue r7 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x0204 }
            r16 = r2
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3 r2 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3     // Catch:{ all -> 0x0204 }
            r2.<init>(r3, r6)     // Catch:{ all -> 0x0204 }
            r7.postRunnable(r2)     // Catch:{ all -> 0x0204 }
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r31)     // Catch:{ all -> 0x0204 }
            r2.resumeNetworkMaybe()     // Catch:{ all -> 0x0204 }
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x0204 }
            r2.countDown()     // Catch:{ all -> 0x0204 }
            return
        L_0x02bd:
            r40 = r6
            java.lang.String r2 = "dc"
            int r2 = r5.getInt(r2)     // Catch:{ all -> 0x0204 }
            java.lang.String r6 = "addr"
            java.lang.String r6 = r5.getString(r6)     // Catch:{ all -> 0x0204 }
            java.lang.String r7 = ":"
            java.lang.String[] r7 = r6.split(r7)     // Catch:{ all -> 0x0204 }
            r16 = r6
            int r6 = r7.length     // Catch:{ all -> 0x0204 }
            r17 = r8
            r8 = 2
            if (r6 == r8) goto L_0x02df
            java.util.concurrent.CountDownLatch r6 = r1.countDownLatch     // Catch:{ all -> 0x0204 }
            r6.countDown()     // Catch:{ all -> 0x0204 }
            return
        L_0x02df:
            r6 = 0
            r6 = r7[r6]     // Catch:{ all -> 0x0204 }
            r8 = 1
            r8 = r7[r8]     // Catch:{ all -> 0x0204 }
            int r8 = java.lang.Integer.parseInt(r8)     // Catch:{ all -> 0x0204 }
            r24 = r7
            org.telegram.tgnet.ConnectionsManager r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r31)     // Catch:{ all -> 0x0204 }
            r7.applyDatacenterAddress(r2, r6, r8)     // Catch:{ all -> 0x0204 }
            org.telegram.tgnet.ConnectionsManager r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r31)     // Catch:{ all -> 0x0204 }
            r7.resumeNetworkMaybe()     // Catch:{ all -> 0x0204 }
            java.util.concurrent.CountDownLatch r7 = r1.countDownLatch     // Catch:{ all -> 0x0204 }
            r7.countDown()     // Catch:{ all -> 0x0204 }
            return
        L_0x02ff:
            boolean r2 = r5.has(r2)     // Catch:{ all -> 0x243a }
            if (r2 == 0) goto L_0x0319
            java.lang.String r2 = "channel_id"
            long r32 = r5.getLong(r2)     // Catch:{ all -> 0x0204 }
            r34 = r32
            r32 = r6
            r2 = r9
            r6 = r34
            long r8 = -r6
            r75 = r6
            r6 = r8
            r8 = r75
            goto L_0x0324
        L_0x0319:
            r32 = r6
            r2 = r9
            r6 = 0
            r34 = r6
            r6 = r32
            r8 = r34
        L_0x0324:
            r42 = r2
            java.lang.String r2 = "from_id"
            boolean r2 = r5.has(r2)     // Catch:{ all -> 0x243a }
            if (r2 == 0) goto L_0x033b
            java.lang.String r2 = "from_id"
            long r32 = r5.getLong(r2)     // Catch:{ all -> 0x0204 }
            r6 = r32
            r75 = r6
            r32 = r75
            goto L_0x0343
        L_0x033b:
            r32 = 0
            r75 = r6
            r6 = r32
            r32 = r75
        L_0x0343:
            java.lang.String r2 = "chat_id"
            boolean r2 = r5.has(r2)     // Catch:{ all -> 0x243a }
            if (r2 == 0) goto L_0x035e
            java.lang.String r2 = "chat_id"
            long r34 = r5.getLong(r2)     // Catch:{ all -> 0x0204 }
            r36 = r34
            r2 = r12
            r43 = r13
            r44 = r14
            r45 = r15
            r12 = r36
            long r14 = -r12
            goto L_0x036b
        L_0x035e:
            r2 = r12
            r43 = r13
            r44 = r14
            r45 = r15
            r12 = 0
            r36 = r12
            r14 = r32
        L_0x036b:
            r46 = r2
            java.lang.String r2 = "encryption_id"
            boolean r2 = r5.has(r2)     // Catch:{ all -> 0x243a }
            if (r2 == 0) goto L_0x0383
            java.lang.String r2 = "encryption_id"
            int r2 = r5.getInt(r2)     // Catch:{ all -> 0x0204 }
            r32 = r14
            long r14 = (long) r2     // Catch:{ all -> 0x0204 }
            long r14 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r14)     // Catch:{ all -> 0x0204 }
            goto L_0x0385
        L_0x0383:
            r32 = r14
        L_0x0385:
            java.lang.String r2 = "schedule"
            boolean r2 = r5.has(r2)     // Catch:{ all -> 0x243a }
            if (r2 == 0) goto L_0x039c
            java.lang.String r2 = "schedule"
            int r2 = r5.getInt(r2)     // Catch:{ all -> 0x0204 }
            r47 = r10
            r10 = 1
            if (r2 != r10) goto L_0x039a
            r2 = 1
            goto L_0x039b
        L_0x039a:
            r2 = 0
        L_0x039b:
            goto L_0x039f
        L_0x039c:
            r47 = r10
            r2 = 0
        L_0x039f:
            r10 = r2
            r1 = 0
            int r30 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r30 != 0) goto L_0x03b8
            java.lang.String r1 = "ENCRYPTED_MESSAGE"
            boolean r1 = r1.equals(r4)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x03b8
            long r1 = org.telegram.messenger.NotificationsController.globalSecretChatId     // Catch:{ all -> 0x03b2 }
            r14 = r1
            goto L_0x03b8
        L_0x03b2:
            r0 = move-exception
            r3 = r77
            r2 = r0
            goto L_0x0207
        L_0x03b8:
            r1 = 1
            r32 = 0
            int r2 = (r14 > r32 ? 1 : (r14 == r32 ? 0 : -1))
            if (r2 == 0) goto L_0x2403
            java.lang.String r2 = "READ_HISTORY"
            boolean r2 = r2.equals(r4)     // Catch:{ all -> 0x23f7 }
            r48 = r1
            java.lang.String r1 = " for dialogId = "
            if (r2 == 0) goto L_0x045e
            java.lang.String r2 = "max_id"
            int r2 = r5.getInt(r2)     // Catch:{ all -> 0x03b2 }
            java.util.ArrayList r16 = new java.util.ArrayList     // Catch:{ all -> 0x03b2 }
            r16.<init>()     // Catch:{ all -> 0x03b2 }
            r24 = r16
            boolean r16 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x03b2 }
            if (r16 == 0) goto L_0x03fb
            r49 = r10
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x03b2 }
            r10.<init>()     // Catch:{ all -> 0x03b2 }
            r50 = r11
            java.lang.String r11 = "GCM received read notification max_id = "
            r10.append(r11)     // Catch:{ all -> 0x03b2 }
            r10.append(r2)     // Catch:{ all -> 0x03b2 }
            r10.append(r1)     // Catch:{ all -> 0x03b2 }
            r10.append(r14)     // Catch:{ all -> 0x03b2 }
            java.lang.String r1 = r10.toString()     // Catch:{ all -> 0x03b2 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x03b2 }
            goto L_0x03ff
        L_0x03fb:
            r49 = r10
            r50 = r11
        L_0x03ff:
            r10 = 0
            int r1 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r1 == 0) goto L_0x0415
            org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox r1 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox     // Catch:{ all -> 0x03b2 }
            r1.<init>()     // Catch:{ all -> 0x03b2 }
            r1.channel_id = r8     // Catch:{ all -> 0x03b2 }
            r1.max_id = r2     // Catch:{ all -> 0x03b2 }
            r10 = r24
            r10.add(r1)     // Catch:{ all -> 0x03b2 }
            goto L_0x043e
        L_0x0415:
            r10 = r24
            org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox r1 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox     // Catch:{ all -> 0x03b2 }
            r1.<init>()     // Catch:{ all -> 0x03b2 }
            r24 = 0
            int r11 = (r6 > r24 ? 1 : (r6 == r24 ? 0 : -1))
            if (r11 == 0) goto L_0x042e
            org.telegram.tgnet.TLRPC$TL_peerUser r11 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x03b2 }
            r11.<init>()     // Catch:{ all -> 0x03b2 }
            r1.peer = r11     // Catch:{ all -> 0x03b2 }
            org.telegram.tgnet.TLRPC$Peer r11 = r1.peer     // Catch:{ all -> 0x03b2 }
            r11.user_id = r6     // Catch:{ all -> 0x03b2 }
            goto L_0x0439
        L_0x042e:
            org.telegram.tgnet.TLRPC$TL_peerChat r11 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x03b2 }
            r11.<init>()     // Catch:{ all -> 0x03b2 }
            r1.peer = r11     // Catch:{ all -> 0x03b2 }
            org.telegram.tgnet.TLRPC$Peer r11 = r1.peer     // Catch:{ all -> 0x03b2 }
            r11.chat_id = r12     // Catch:{ all -> 0x03b2 }
        L_0x0439:
            r1.max_id = r2     // Catch:{ all -> 0x03b2 }
            r10.add(r1)     // Catch:{ all -> 0x03b2 }
        L_0x043e:
            org.telegram.messenger.MessagesController r34 = org.telegram.messenger.MessagesController.getInstance(r3)     // Catch:{ all -> 0x03b2 }
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r35 = r10
            r34.processUpdateArray(r35, r36, r37, r38, r39)     // Catch:{ all -> 0x03b2 }
            r62 = r3
            r74 = r4
            r67 = r5
            r69 = r8
            r53 = r12
            r3 = r77
            r8 = r6
            goto L_0x2416
        L_0x045e:
            r49 = r10
            r50 = r11
            java.lang.String r2 = "MESSAGE_DELETED"
            boolean r2 = r2.equals(r4)     // Catch:{ all -> 0x23f7 }
            java.lang.String r10 = "messages"
            if (r2 == 0) goto L_0x04f5
            java.lang.String r2 = r5.getString(r10)     // Catch:{ all -> 0x03b2 }
            java.lang.String r10 = ","
            java.lang.String[] r10 = r2.split(r10)     // Catch:{ all -> 0x03b2 }
            androidx.collection.LongSparseArray r11 = new androidx.collection.LongSparseArray     // Catch:{ all -> 0x03b2 }
            r11.<init>()     // Catch:{ all -> 0x03b2 }
            java.util.ArrayList r16 = new java.util.ArrayList     // Catch:{ all -> 0x03b2 }
            r16.<init>()     // Catch:{ all -> 0x03b2 }
            r24 = r16
            r16 = 0
            r25 = r2
            r2 = r16
        L_0x0488:
            r51 = r6
            int r6 = r10.length     // Catch:{ all -> 0x03b2 }
            if (r2 >= r6) goto L_0x049f
            r6 = r10[r2]     // Catch:{ all -> 0x03b2 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x03b2 }
            r7 = r24
            r7.add(r6)     // Catch:{ all -> 0x03b2 }
            int r2 = r2 + 1
            r24 = r7
            r6 = r51
            goto L_0x0488
        L_0x049f:
            r7 = r24
            r53 = r12
            long r12 = -r8
            r11.put(r12, r7)     // Catch:{ all -> 0x03b2 }
            org.telegram.messenger.NotificationsController r2 = org.telegram.messenger.NotificationsController.getInstance(r31)     // Catch:{ all -> 0x03b2 }
            r2.removeDeletedMessagesFromNotifications(r11)     // Catch:{ all -> 0x03b2 }
            org.telegram.messenger.MessagesController r32 = org.telegram.messenger.MessagesController.getInstance(r31)     // Catch:{ all -> 0x03b2 }
            r33 = r14
            r35 = r7
            r36 = r8
            r32.deleteMessagesByPush(r33, r35, r36)     // Catch:{ all -> 0x03b2 }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x03b2 }
            if (r2 == 0) goto L_0x04e7
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03b2 }
            r2.<init>()     // Catch:{ all -> 0x03b2 }
            java.lang.String r6 = "GCM received "
            r2.append(r6)     // Catch:{ all -> 0x03b2 }
            r2.append(r4)     // Catch:{ all -> 0x03b2 }
            r2.append(r1)     // Catch:{ all -> 0x03b2 }
            r2.append(r14)     // Catch:{ all -> 0x03b2 }
            java.lang.String r1 = " mids = "
            r2.append(r1)     // Catch:{ all -> 0x03b2 }
            java.lang.String r1 = ","
            java.lang.String r1 = android.text.TextUtils.join(r1, r7)     // Catch:{ all -> 0x03b2 }
            r2.append(r1)     // Catch:{ all -> 0x03b2 }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x03b2 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x03b2 }
        L_0x04e7:
            r62 = r3
            r74 = r4
            r67 = r5
            r69 = r8
            r8 = r51
            r3 = r77
            goto L_0x2416
        L_0x04f5:
            r51 = r6
            r53 = r12
            boolean r2 = android.text.TextUtils.isEmpty(r4)     // Catch:{ all -> 0x23f7 }
            if (r2 != 0) goto L_0x23ea
            java.lang.String r2 = "msg_id"
            boolean r2 = r5.has(r2)     // Catch:{ all -> 0x23f7 }
            if (r2 == 0) goto L_0x050e
            java.lang.String r2 = "msg_id"
            int r2 = r5.getInt(r2)     // Catch:{ all -> 0x03b2 }
            goto L_0x050f
        L_0x050e:
            r2 = 0
        L_0x050f:
            java.lang.String r6 = "random_id"
            boolean r6 = r5.has(r6)     // Catch:{ all -> 0x23f7 }
            if (r6 == 0) goto L_0x0526
            java.lang.String r6 = "random_id"
            java.lang.String r6 = r5.getString(r6)     // Catch:{ all -> 0x03b2 }
            java.lang.Long r6 = org.telegram.messenger.Utilities.parseLong(r6)     // Catch:{ all -> 0x03b2 }
            long r6 = r6.longValue()     // Catch:{ all -> 0x03b2 }
            goto L_0x0528
        L_0x0526:
            r6 = 0
        L_0x0528:
            r11 = 0
            if (r2 == 0) goto L_0x0568
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r31)     // Catch:{ all -> 0x03b2 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r12 = r12.dialogs_read_inbox_max     // Catch:{ all -> 0x03b2 }
            java.lang.Long r13 = java.lang.Long.valueOf(r14)     // Catch:{ all -> 0x03b2 }
            java.lang.Object r12 = r12.get(r13)     // Catch:{ all -> 0x03b2 }
            java.lang.Integer r12 = (java.lang.Integer) r12     // Catch:{ all -> 0x03b2 }
            if (r12 != 0) goto L_0x055b
            org.telegram.messenger.MessagesStorage r13 = org.telegram.messenger.MessagesStorage.getInstance(r31)     // Catch:{ all -> 0x03b2 }
            r30 = r11
            r11 = 0
            int r13 = r13.getDialogReadMax(r11, r14)     // Catch:{ all -> 0x03b2 }
            java.lang.Integer r11 = java.lang.Integer.valueOf(r13)     // Catch:{ all -> 0x03b2 }
            r12 = r11
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r3)     // Catch:{ all -> 0x03b2 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r11 = r11.dialogs_read_inbox_max     // Catch:{ all -> 0x03b2 }
            java.lang.Long r13 = java.lang.Long.valueOf(r14)     // Catch:{ all -> 0x03b2 }
            r11.put(r13, r12)     // Catch:{ all -> 0x03b2 }
            goto L_0x055d
        L_0x055b:
            r30 = r11
        L_0x055d:
            int r11 = r12.intValue()     // Catch:{ all -> 0x03b2 }
            if (r2 <= r11) goto L_0x0565
            r11 = 1
            goto L_0x0567
        L_0x0565:
            r11 = r30
        L_0x0567:
            goto L_0x057e
        L_0x0568:
            r30 = r11
            r11 = 0
            int r13 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r13 == 0) goto L_0x057c
            org.telegram.messenger.MessagesStorage r11 = org.telegram.messenger.MessagesStorage.getInstance(r22)     // Catch:{ all -> 0x03b2 }
            boolean r11 = r11.checkMessageByRandomId(r6)     // Catch:{ all -> 0x03b2 }
            if (r11 != 0) goto L_0x057c
            r11 = 1
            goto L_0x057e
        L_0x057c:
            r11 = r30
        L_0x057e:
            if (r11 == 0) goto L_0x23d7
            java.lang.String r12 = "chat_from_id"
            r55 = r6
            r6 = 0
            long r12 = r5.optLong(r12, r6)     // Catch:{ all -> 0x23f7 }
            java.lang.String r6 = "chat_from_broadcast_id"
            r7 = r10
            r57 = r11
            r10 = 0
            long r32 = r5.optLong(r6, r10)     // Catch:{ all -> 0x23f7 }
            r58 = r32
            java.lang.String r6 = "chat_from_group_id"
            long r32 = r5.optLong(r6, r10)     // Catch:{ all -> 0x23f7 }
            r60 = r32
            int r6 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r6 != 0) goto L_0x05aa
            int r6 = (r60 > r10 ? 1 : (r60 == r10 ? 0 : -1))
            if (r6 == 0) goto L_0x05a8
            goto L_0x05aa
        L_0x05a8:
            r6 = 0
            goto L_0x05ab
        L_0x05aa:
            r6 = 1
        L_0x05ab:
            java.lang.String r10 = "mention"
            boolean r10 = r5.has(r10)     // Catch:{ all -> 0x23f7 }
            if (r10 == 0) goto L_0x05bd
            java.lang.String r10 = "mention"
            int r10 = r5.getInt(r10)     // Catch:{ all -> 0x03b2 }
            if (r10 == 0) goto L_0x05bd
            r10 = 1
            goto L_0x05be
        L_0x05bd:
            r10 = 0
        L_0x05be:
            java.lang.String r11 = "silent"
            boolean r11 = r5.has(r11)     // Catch:{ all -> 0x23f7 }
            if (r11 == 0) goto L_0x05d0
            java.lang.String r11 = "silent"
            int r11 = r5.getInt(r11)     // Catch:{ all -> 0x03b2 }
            if (r11 == 0) goto L_0x05d0
            r11 = 1
            goto L_0x05d1
        L_0x05d0:
            r11 = 0
        L_0x05d1:
            r62 = r3
            java.lang.String r3 = "loc_args"
            r63 = r11
            r11 = r50
            boolean r3 = r11.has(r3)     // Catch:{ all -> 0x23f7 }
            if (r3 == 0) goto L_0x0604
            java.lang.String r3 = "loc_args"
            org.json.JSONArray r3 = r11.getJSONArray(r3)     // Catch:{ all -> 0x03b2 }
            r50 = r11
            int r11 = r3.length()     // Catch:{ all -> 0x03b2 }
            java.lang.String[] r11 = new java.lang.String[r11]     // Catch:{ all -> 0x03b2 }
            r30 = 0
            r64 = r10
            r10 = r30
        L_0x05f3:
            r65 = r12
            int r12 = r11.length     // Catch:{ all -> 0x03b2 }
            if (r10 >= r12) goto L_0x0603
            java.lang.String r12 = r3.getString(r10)     // Catch:{ all -> 0x03b2 }
            r11[r10] = r12     // Catch:{ all -> 0x03b2 }
            int r10 = r10 + 1
            r12 = r65
            goto L_0x05f3
        L_0x0603:
            goto L_0x060c
        L_0x0604:
            r64 = r10
            r50 = r11
            r65 = r12
            r3 = 0
            r11 = r3
        L_0x060c:
            r3 = 0
            r10 = 0
            r12 = 0
            r13 = r11[r12]     // Catch:{ all -> 0x23f7 }
            r12 = r13
            r13 = 0
            r30 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = r3
            java.lang.String r3 = "edit_date"
            boolean r39 = r5.has(r3)     // Catch:{ all -> 0x23f7 }
            java.lang.String r3 = "CHAT_"
            boolean r3 = r4.startsWith(r3)     // Catch:{ all -> 0x23f7 }
            if (r3 == 0) goto L_0x066f
            boolean r3 = org.telegram.messenger.UserObject.isReplyUser((long) r14)     // Catch:{ all -> 0x03b2 }
            if (r3 == 0) goto L_0x0654
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x03b2 }
            r3.<init>()     // Catch:{ all -> 0x03b2 }
            r3.append(r12)     // Catch:{ all -> 0x03b2 }
            r67 = r5
            java.lang.String r5 = " @ "
            r3.append(r5)     // Catch:{ all -> 0x03b2 }
            r38 = r10
            r5 = 1
            r10 = r11[r5]     // Catch:{ all -> 0x03b2 }
            r3.append(r10)     // Catch:{ all -> 0x03b2 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x03b2 }
            r12 = r3
            r3 = r34
            r5 = r35
            r10 = r36
            goto L_0x06a4
        L_0x0654:
            r67 = r5
            r38 = r10
            r32 = 0
            int r3 = (r8 > r32 ? 1 : (r8 == r32 ? 0 : -1))
            if (r3 == 0) goto L_0x0660
            r3 = 1
            goto L_0x0661
        L_0x0660:
            r3 = 0
        L_0x0661:
            r34 = r3
            r13 = r12
            r3 = 1
            r5 = r11[r3]     // Catch:{ all -> 0x03b2 }
            r12 = r5
            r3 = r34
            r5 = r35
            r10 = r36
            goto L_0x06a4
        L_0x066f:
            r67 = r5
            r38 = r10
            java.lang.String r3 = "PINNED_"
            boolean r3 = r4.startsWith(r3)     // Catch:{ all -> 0x23f7 }
            if (r3 == 0) goto L_0x068d
            r32 = 0
            int r3 = (r8 > r32 ? 1 : (r8 == r32 ? 0 : -1))
            if (r3 == 0) goto L_0x0683
            r3 = 1
            goto L_0x0684
        L_0x0683:
            r3 = 0
        L_0x0684:
            r34 = r3
            r35 = 1
            r5 = r35
            r10 = r36
            goto L_0x06a4
        L_0x068d:
            java.lang.String r3 = "CHANNEL_"
            boolean r3 = r4.startsWith(r3)     // Catch:{ all -> 0x23f7 }
            if (r3 == 0) goto L_0x069e
            r36 = 1
            r3 = r34
            r5 = r35
            r10 = r36
            goto L_0x06a4
        L_0x069e:
            r3 = r34
            r5 = r35
            r10 = r36
        L_0x06a4:
            boolean r34 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x23f7 }
            if (r34 == 0) goto L_0x06cf
            r34 = r12
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x03b2 }
            r12.<init>()     // Catch:{ all -> 0x03b2 }
            r68 = r10
            java.lang.String r10 = "GCM received message notification "
            r12.append(r10)     // Catch:{ all -> 0x03b2 }
            r12.append(r4)     // Catch:{ all -> 0x03b2 }
            r12.append(r1)     // Catch:{ all -> 0x03b2 }
            r12.append(r14)     // Catch:{ all -> 0x03b2 }
            java.lang.String r1 = " mid = "
            r12.append(r1)     // Catch:{ all -> 0x03b2 }
            r12.append(r2)     // Catch:{ all -> 0x03b2 }
            java.lang.String r1 = r12.toString()     // Catch:{ all -> 0x03b2 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x03b2 }
            goto L_0x06d3
        L_0x06cf:
            r68 = r10
            r34 = r12
        L_0x06d3:
            int r1 = r4.hashCode()     // Catch:{ all -> 0x23f7 }
            switch(r1) {
                case -2100047043: goto L_0x0c0c;
                case -2091498420: goto L_0x0CLASSNAME;
                case -2053872415: goto L_0x0bf6;
                case -2039746363: goto L_0x0beb;
                case -2023218804: goto L_0x0be0;
                case -1979538588: goto L_0x0bd5;
                case -1979536003: goto L_0x0bca;
                case -1979535888: goto L_0x0bbf;
                case -1969004705: goto L_0x0bb4;
                case -1946699248: goto L_0x0ba8;
                case -1717283471: goto L_0x0b9c;
                case -1646640058: goto L_0x0b90;
                case -1528047021: goto L_0x0b84;
                case -1493579426: goto L_0x0b78;
                case -1482481933: goto L_0x0b6c;
                case -1480102982: goto L_0x0b61;
                case -1478041834: goto L_0x0b55;
                case -1474543101: goto L_0x0b4a;
                case -1465695932: goto L_0x0b3e;
                case -1374906292: goto L_0x0b32;
                case -1372940586: goto L_0x0b26;
                case -1264245338: goto L_0x0b1a;
                case -1236154001: goto L_0x0b0e;
                case -1236086700: goto L_0x0b02;
                case -1236077786: goto L_0x0af6;
                case -1235796237: goto L_0x0aea;
                case -1235760759: goto L_0x0ade;
                case -1235686303: goto L_0x0ad3;
                case -1198046100: goto L_0x0ac8;
                case -1124254527: goto L_0x0abc;
                case -1085137927: goto L_0x0ab0;
                case -1084856378: goto L_0x0aa4;
                case -1084820900: goto L_0x0a98;
                case -1084746444: goto L_0x0a8c;
                case -819729482: goto L_0x0a80;
                case -772141857: goto L_0x0a74;
                case -638310039: goto L_0x0a68;
                case -590403924: goto L_0x0a5c;
                case -589196239: goto L_0x0a50;
                case -589193654: goto L_0x0a44;
                case -589193539: goto L_0x0a38;
                case -440169325: goto L_0x0a2c;
                case -412748110: goto L_0x0a20;
                case -228518075: goto L_0x0a14;
                case -213586509: goto L_0x0a08;
                case -115582002: goto L_0x09fc;
                case -112621464: goto L_0x09f0;
                case -108522133: goto L_0x09e4;
                case -107572034: goto L_0x09d9;
                case -40534265: goto L_0x09cd;
                case 65254746: goto L_0x09c1;
                case 141040782: goto L_0x09b5;
                case 202550149: goto L_0x09a9;
                case 309993049: goto L_0x099d;
                case 309995634: goto L_0x0991;
                case 309995749: goto L_0x0985;
                case 320532812: goto L_0x0979;
                case 328933854: goto L_0x096d;
                case 331340546: goto L_0x0961;
                case 342406591: goto L_0x0955;
                case 344816990: goto L_0x0949;
                case 346878138: goto L_0x093d;
                case 350376871: goto L_0x0931;
                case 608430149: goto L_0x0925;
                case 615714517: goto L_0x091a;
                case 715508879: goto L_0x090e;
                case 728985323: goto L_0x0902;
                case 731046471: goto L_0x08f6;
                case 734545204: goto L_0x08ea;
                case 802032552: goto L_0x08de;
                case 991498806: goto L_0x08d2;
                case 1007364121: goto L_0x08c6;
                case 1019850010: goto L_0x08ba;
                case 1019917311: goto L_0x08ae;
                case 1019926225: goto L_0x08a2;
                case 1020207774: goto L_0x0896;
                case 1020243252: goto L_0x088a;
                case 1020317708: goto L_0x087e;
                case 1060282259: goto L_0x0872;
                case 1060349560: goto L_0x0866;
                case 1060358474: goto L_0x085a;
                case 1060640023: goto L_0x084e;
                case 1060675501: goto L_0x0842;
                case 1060749957: goto L_0x0837;
                case 1073049781: goto L_0x082b;
                case 1078101399: goto L_0x081f;
                case 1110103437: goto L_0x0813;
                case 1160762272: goto L_0x0807;
                case 1172918249: goto L_0x07fb;
                case 1234591620: goto L_0x07ef;
                case 1281128640: goto L_0x07e3;
                case 1281131225: goto L_0x07d7;
                case 1281131340: goto L_0x07cb;
                case 1310789062: goto L_0x07c0;
                case 1333118583: goto L_0x07b4;
                case 1361447897: goto L_0x07a8;
                case 1498266155: goto L_0x079c;
                case 1533804208: goto L_0x0790;
                case 1540131626: goto L_0x0784;
                case 1547988151: goto L_0x0778;
                case 1561464595: goto L_0x076c;
                case 1563525743: goto L_0x0760;
                case 1567024476: goto L_0x0754;
                case 1810705077: goto L_0x0748;
                case 1815177512: goto L_0x073c;
                case 1954774321: goto L_0x0730;
                case 1963241394: goto L_0x0724;
                case 2014789757: goto L_0x0718;
                case 2022049433: goto L_0x070c;
                case 2034984710: goto L_0x0700;
                case 2048733346: goto L_0x06f4;
                case 2099392181: goto L_0x06e8;
                case 2140162142: goto L_0x06dc;
                default: goto L_0x06da;
            }
        L_0x06da:
            goto L_0x0CLASSNAME
        L_0x06dc:
            java.lang.String r1 = "CHAT_MESSAGE_GEOLIVE"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 60
            goto L_0x0CLASSNAME
        L_0x06e8:
            java.lang.String r1 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 43
            goto L_0x0CLASSNAME
        L_0x06f4:
            java.lang.String r1 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 28
            goto L_0x0CLASSNAME
        L_0x0700:
            java.lang.String r1 = "CHANNEL_MESSAGE_PLAYLIST"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 45
            goto L_0x0CLASSNAME
        L_0x070c:
            java.lang.String r1 = "PINNED_CONTACT"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 94
            goto L_0x0CLASSNAME
        L_0x0718:
            java.lang.String r1 = "CHAT_PHOTO_EDITED"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 68
            goto L_0x0CLASSNAME
        L_0x0724:
            java.lang.String r1 = "LOCKED_MESSAGE"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 107(0x6b, float:1.5E-43)
            goto L_0x0CLASSNAME
        L_0x0730:
            java.lang.String r1 = "CHAT_MESSAGE_PLAYLIST"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 83
            goto L_0x0CLASSNAME
        L_0x073c:
            java.lang.String r1 = "CHANNEL_MESSAGES"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 47
            goto L_0x0CLASSNAME
        L_0x0748:
            java.lang.String r1 = "MESSAGE_INVOICE"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 21
            goto L_0x0CLASSNAME
        L_0x0754:
            java.lang.String r1 = "CHAT_MESSAGE_VIDEO"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 51
            goto L_0x0CLASSNAME
        L_0x0760:
            java.lang.String r1 = "CHAT_MESSAGE_ROUND"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 52
            goto L_0x0CLASSNAME
        L_0x076c:
            java.lang.String r1 = "CHAT_MESSAGE_PHOTO"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 50
            goto L_0x0CLASSNAME
        L_0x0778:
            java.lang.String r1 = "CHAT_MESSAGE_AUDIO"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 55
            goto L_0x0CLASSNAME
        L_0x0784:
            java.lang.String r1 = "MESSAGE_PLAYLIST"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 25
            goto L_0x0CLASSNAME
        L_0x0790:
            java.lang.String r1 = "MESSAGE_VIDEOS"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 24
            goto L_0x0CLASSNAME
        L_0x079c:
            java.lang.String r1 = "PHONE_CALL_MISSED"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 112(0x70, float:1.57E-43)
            goto L_0x0CLASSNAME
        L_0x07a8:
            java.lang.String r1 = "MESSAGE_PHOTOS"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 23
            goto L_0x0CLASSNAME
        L_0x07b4:
            java.lang.String r1 = "CHAT_MESSAGE_VIDEOS"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 82
            goto L_0x0CLASSNAME
        L_0x07c0:
            java.lang.String r1 = "MESSAGE_NOTEXT"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 2
            goto L_0x0CLASSNAME
        L_0x07cb:
            java.lang.String r1 = "MESSAGE_GIF"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 17
            goto L_0x0CLASSNAME
        L_0x07d7:
            java.lang.String r1 = "MESSAGE_GEO"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 15
            goto L_0x0CLASSNAME
        L_0x07e3:
            java.lang.String r1 = "MESSAGE_DOC"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 9
            goto L_0x0CLASSNAME
        L_0x07ef:
            java.lang.String r1 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 63
            goto L_0x0CLASSNAME
        L_0x07fb:
            java.lang.String r1 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 39
            goto L_0x0CLASSNAME
        L_0x0807:
            java.lang.String r1 = "CHAT_MESSAGE_PHOTOS"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 81
            goto L_0x0CLASSNAME
        L_0x0813:
            java.lang.String r1 = "CHAT_MESSAGE_NOTEXT"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 49
            goto L_0x0CLASSNAME
        L_0x081f:
            java.lang.String r1 = "CHAT_TITLE_EDITED"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 67
            goto L_0x0CLASSNAME
        L_0x082b:
            java.lang.String r1 = "PINNED_NOTEXT"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 87
            goto L_0x0CLASSNAME
        L_0x0837:
            java.lang.String r1 = "MESSAGE_TEXT"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 0
            goto L_0x0CLASSNAME
        L_0x0842:
            java.lang.String r1 = "MESSAGE_QUIZ"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 13
            goto L_0x0CLASSNAME
        L_0x084e:
            java.lang.String r1 = "MESSAGE_POLL"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 14
            goto L_0x0CLASSNAME
        L_0x085a:
            java.lang.String r1 = "MESSAGE_GAME"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 18
            goto L_0x0CLASSNAME
        L_0x0866:
            java.lang.String r1 = "MESSAGE_FWDS"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 22
            goto L_0x0CLASSNAME
        L_0x0872:
            java.lang.String r1 = "MESSAGE_DOCS"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 26
            goto L_0x0CLASSNAME
        L_0x087e:
            java.lang.String r1 = "CHAT_MESSAGE_TEXT"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 48
            goto L_0x0CLASSNAME
        L_0x088a:
            java.lang.String r1 = "CHAT_MESSAGE_QUIZ"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 57
            goto L_0x0CLASSNAME
        L_0x0896:
            java.lang.String r1 = "CHAT_MESSAGE_POLL"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 58
            goto L_0x0CLASSNAME
        L_0x08a2:
            java.lang.String r1 = "CHAT_MESSAGE_GAME"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 62
            goto L_0x0CLASSNAME
        L_0x08ae:
            java.lang.String r1 = "CHAT_MESSAGE_FWDS"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 80
            goto L_0x0CLASSNAME
        L_0x08ba:
            java.lang.String r1 = "CHAT_MESSAGE_DOCS"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 84
            goto L_0x0CLASSNAME
        L_0x08c6:
            java.lang.String r1 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 20
            goto L_0x0CLASSNAME
        L_0x08d2:
            java.lang.String r1 = "PINNED_GEOLIVE"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 98
            goto L_0x0CLASSNAME
        L_0x08de:
            java.lang.String r1 = "MESSAGE_CONTACT"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 12
            goto L_0x0CLASSNAME
        L_0x08ea:
            java.lang.String r1 = "PINNED_VIDEO"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 89
            goto L_0x0CLASSNAME
        L_0x08f6:
            java.lang.String r1 = "PINNED_ROUND"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 90
            goto L_0x0CLASSNAME
        L_0x0902:
            java.lang.String r1 = "PINNED_PHOTO"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 88
            goto L_0x0CLASSNAME
        L_0x090e:
            java.lang.String r1 = "PINNED_AUDIO"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 93
            goto L_0x0CLASSNAME
        L_0x091a:
            java.lang.String r1 = "MESSAGE_PHOTO_SECRET"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 4
            goto L_0x0CLASSNAME
        L_0x0925:
            java.lang.String r1 = "CHAT_VOICECHAT_INVITE_YOU"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 73
            goto L_0x0CLASSNAME
        L_0x0931:
            java.lang.String r1 = "CHANNEL_MESSAGE_VIDEO"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 30
            goto L_0x0CLASSNAME
        L_0x093d:
            java.lang.String r1 = "CHANNEL_MESSAGE_ROUND"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 31
            goto L_0x0CLASSNAME
        L_0x0949:
            java.lang.String r1 = "CHANNEL_MESSAGE_PHOTO"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 29
            goto L_0x0CLASSNAME
        L_0x0955:
            java.lang.String r1 = "CHAT_VOICECHAT_END"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 72
            goto L_0x0CLASSNAME
        L_0x0961:
            java.lang.String r1 = "CHANNEL_MESSAGE_AUDIO"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 34
            goto L_0x0CLASSNAME
        L_0x096d:
            java.lang.String r1 = "CHAT_MESSAGE_STICKER"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 54
            goto L_0x0CLASSNAME
        L_0x0979:
            java.lang.String r1 = "MESSAGES"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 27
            goto L_0x0CLASSNAME
        L_0x0985:
            java.lang.String r1 = "CHAT_MESSAGE_GIF"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 61
            goto L_0x0CLASSNAME
        L_0x0991:
            java.lang.String r1 = "CHAT_MESSAGE_GEO"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 59
            goto L_0x0CLASSNAME
        L_0x099d:
            java.lang.String r1 = "CHAT_MESSAGE_DOC"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 53
            goto L_0x0CLASSNAME
        L_0x09a9:
            java.lang.String r1 = "CHAT_VOICECHAT_INVITE"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 71
            goto L_0x0CLASSNAME
        L_0x09b5:
            java.lang.String r1 = "CHAT_LEFT"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 76
            goto L_0x0CLASSNAME
        L_0x09c1:
            java.lang.String r1 = "CHAT_ADD_YOU"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 66
            goto L_0x0CLASSNAME
        L_0x09cd:
            java.lang.String r1 = "CHAT_DELETE_MEMBER"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 74
            goto L_0x0CLASSNAME
        L_0x09d9:
            java.lang.String r1 = "MESSAGE_SCREENSHOT"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 7
            goto L_0x0CLASSNAME
        L_0x09e4:
            java.lang.String r1 = "AUTH_REGION"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 106(0x6a, float:1.49E-43)
            goto L_0x0CLASSNAME
        L_0x09f0:
            java.lang.String r1 = "CONTACT_JOINED"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 104(0x68, float:1.46E-43)
            goto L_0x0CLASSNAME
        L_0x09fc:
            java.lang.String r1 = "CHAT_MESSAGE_INVOICE"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 64
            goto L_0x0CLASSNAME
        L_0x0a08:
            java.lang.String r1 = "ENCRYPTION_REQUEST"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 108(0x6c, float:1.51E-43)
            goto L_0x0CLASSNAME
        L_0x0a14:
            java.lang.String r1 = "MESSAGE_GEOLIVE"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 16
            goto L_0x0CLASSNAME
        L_0x0a20:
            java.lang.String r1 = "CHAT_DELETE_YOU"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 75
            goto L_0x0CLASSNAME
        L_0x0a2c:
            java.lang.String r1 = "AUTH_UNKNOWN"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 105(0x69, float:1.47E-43)
            goto L_0x0CLASSNAME
        L_0x0a38:
            java.lang.String r1 = "PINNED_GIF"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 102(0x66, float:1.43E-43)
            goto L_0x0CLASSNAME
        L_0x0a44:
            java.lang.String r1 = "PINNED_GEO"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 97
            goto L_0x0CLASSNAME
        L_0x0a50:
            java.lang.String r1 = "PINNED_DOC"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 91
            goto L_0x0CLASSNAME
        L_0x0a5c:
            java.lang.String r1 = "PINNED_GAME_SCORE"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 100
            goto L_0x0CLASSNAME
        L_0x0a68:
            java.lang.String r1 = "CHANNEL_MESSAGE_STICKER"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 33
            goto L_0x0CLASSNAME
        L_0x0a74:
            java.lang.String r1 = "PHONE_CALL_REQUEST"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 110(0x6e, float:1.54E-43)
            goto L_0x0CLASSNAME
        L_0x0a80:
            java.lang.String r1 = "PINNED_STICKER"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 92
            goto L_0x0CLASSNAME
        L_0x0a8c:
            java.lang.String r1 = "PINNED_TEXT"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 86
            goto L_0x0CLASSNAME
        L_0x0a98:
            java.lang.String r1 = "PINNED_QUIZ"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 95
            goto L_0x0CLASSNAME
        L_0x0aa4:
            java.lang.String r1 = "PINNED_POLL"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 96
            goto L_0x0CLASSNAME
        L_0x0ab0:
            java.lang.String r1 = "PINNED_GAME"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 99
            goto L_0x0CLASSNAME
        L_0x0abc:
            java.lang.String r1 = "CHAT_MESSAGE_CONTACT"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 56
            goto L_0x0CLASSNAME
        L_0x0ac8:
            java.lang.String r1 = "MESSAGE_VIDEO_SECRET"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 6
            goto L_0x0CLASSNAME
        L_0x0ad3:
            java.lang.String r1 = "CHANNEL_MESSAGE_TEXT"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 1
            goto L_0x0CLASSNAME
        L_0x0ade:
            java.lang.String r1 = "CHANNEL_MESSAGE_QUIZ"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 36
            goto L_0x0CLASSNAME
        L_0x0aea:
            java.lang.String r1 = "CHANNEL_MESSAGE_POLL"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 37
            goto L_0x0CLASSNAME
        L_0x0af6:
            java.lang.String r1 = "CHANNEL_MESSAGE_GAME"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 41
            goto L_0x0CLASSNAME
        L_0x0b02:
            java.lang.String r1 = "CHANNEL_MESSAGE_FWDS"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 42
            goto L_0x0CLASSNAME
        L_0x0b0e:
            java.lang.String r1 = "CHANNEL_MESSAGE_DOCS"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 46
            goto L_0x0CLASSNAME
        L_0x0b1a:
            java.lang.String r1 = "PINNED_INVOICE"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 101(0x65, float:1.42E-43)
            goto L_0x0CLASSNAME
        L_0x0b26:
            java.lang.String r1 = "CHAT_RETURNED"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 77
            goto L_0x0CLASSNAME
        L_0x0b32:
            java.lang.String r1 = "ENCRYPTED_MESSAGE"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 103(0x67, float:1.44E-43)
            goto L_0x0CLASSNAME
        L_0x0b3e:
            java.lang.String r1 = "ENCRYPTION_ACCEPT"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 109(0x6d, float:1.53E-43)
            goto L_0x0CLASSNAME
        L_0x0b4a:
            java.lang.String r1 = "MESSAGE_VIDEO"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 5
            goto L_0x0CLASSNAME
        L_0x0b55:
            java.lang.String r1 = "MESSAGE_ROUND"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 8
            goto L_0x0CLASSNAME
        L_0x0b61:
            java.lang.String r1 = "MESSAGE_PHOTO"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 3
            goto L_0x0CLASSNAME
        L_0x0b6c:
            java.lang.String r1 = "MESSAGE_MUTED"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 111(0x6f, float:1.56E-43)
            goto L_0x0CLASSNAME
        L_0x0b78:
            java.lang.String r1 = "MESSAGE_AUDIO"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 11
            goto L_0x0CLASSNAME
        L_0x0b84:
            java.lang.String r1 = "CHAT_MESSAGES"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 85
            goto L_0x0CLASSNAME
        L_0x0b90:
            java.lang.String r1 = "CHAT_VOICECHAT_START"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 70
            goto L_0x0CLASSNAME
        L_0x0b9c:
            java.lang.String r1 = "CHAT_REQ_JOINED"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 79
            goto L_0x0CLASSNAME
        L_0x0ba8:
            java.lang.String r1 = "CHAT_JOINED"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 78
            goto L_0x0CLASSNAME
        L_0x0bb4:
            java.lang.String r1 = "CHAT_ADD_MEMBER"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 69
            goto L_0x0CLASSNAME
        L_0x0bbf:
            java.lang.String r1 = "CHANNEL_MESSAGE_GIF"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 40
            goto L_0x0CLASSNAME
        L_0x0bca:
            java.lang.String r1 = "CHANNEL_MESSAGE_GEO"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 38
            goto L_0x0CLASSNAME
        L_0x0bd5:
            java.lang.String r1 = "CHANNEL_MESSAGE_DOC"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 32
            goto L_0x0CLASSNAME
        L_0x0be0:
            java.lang.String r1 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 44
            goto L_0x0CLASSNAME
        L_0x0beb:
            java.lang.String r1 = "MESSAGE_STICKER"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 10
            goto L_0x0CLASSNAME
        L_0x0bf6:
            java.lang.String r1 = "CHAT_CREATED"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 65
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.lang.String r1 = "CHANNEL_MESSAGE_CONTACT"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 35
            goto L_0x0CLASSNAME
        L_0x0c0c:
            java.lang.String r1 = "MESSAGE_GAME_SCORE"
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03b2 }
            if (r1 == 0) goto L_0x06da
            r1 = 19
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r1 = -1
        L_0x0CLASSNAME:
            java.lang.String r10 = "Files"
            java.lang.String r12 = "MusicFiles"
            r26 = r13
            java.lang.String r13 = "Videos"
            r69 = r8
            java.lang.String r8 = "Photos"
            java.lang.String r9 = " "
            r71 = r3
            java.lang.String r3 = "NotificationGroupFew"
            r72 = r5
            java.lang.String r5 = "NotificationMessageFew"
            r73 = r2
            java.lang.String r2 = "ChannelMessageFew"
            r74 = r4
            java.lang.String r4 = "AttachSticker"
            switch(r1) {
                case 0: goto L_0x2226;
                case 1: goto L_0x2226;
                case 2: goto L_0x2200;
                case 3: goto L_0x21da;
                case 4: goto L_0x21b4;
                case 5: goto L_0x218e;
                case 6: goto L_0x2168;
                case 7: goto L_0x214a;
                case 8: goto L_0x2124;
                case 9: goto L_0x20fe;
                case 10: goto L_0x2090;
                case 11: goto L_0x206a;
                case 12: goto L_0x203f;
                case 13: goto L_0x2014;
                case 14: goto L_0x1fe9;
                case 15: goto L_0x1fc3;
                case 16: goto L_0x1f9d;
                case 17: goto L_0x1var_;
                case 18: goto L_0x1f4c;
                case 19: goto L_0x1var_;
                case 20: goto L_0x1var_;
                case 21: goto L_0x1efa;
                case 22: goto L_0x1ec9;
                case 23: goto L_0x1e9a;
                case 24: goto L_0x1e6b;
                case 25: goto L_0x1e3c;
                case 26: goto L_0x1e0d;
                case 27: goto L_0x1dee;
                case 28: goto L_0x1dc8;
                case 29: goto L_0x1da2;
                case 30: goto L_0x1d7c;
                case 31: goto L_0x1d56;
                case 32: goto L_0x1d30;
                case 33: goto L_0x1cc2;
                case 34: goto L_0x1c9c;
                case 35: goto L_0x1CLASSNAME;
                case 36: goto L_0x1CLASSNAME;
                case 37: goto L_0x1c1b;
                case 38: goto L_0x1bf5;
                case 39: goto L_0x1bcf;
                case 40: goto L_0x1ba9;
                case 41: goto L_0x1b83;
                case 42: goto L_0x1b4e;
                case 43: goto L_0x1b1f;
                case 44: goto L_0x1af0;
                case 45: goto L_0x1ac1;
                case 46: goto L_0x1a92;
                case 47: goto L_0x1a73;
                case 48: goto L_0x1a4a;
                case 49: goto L_0x1a1f;
                case 50: goto L_0x19f4;
                case 51: goto L_0x19c9;
                case 52: goto L_0x199e;
                case 53: goto L_0x1973;
                case 54: goto L_0x18e6;
                case 55: goto L_0x18bb;
                case 56: goto L_0x188b;
                case 57: goto L_0x185b;
                case 58: goto L_0x182b;
                case 59: goto L_0x1800;
                case 60: goto L_0x17d5;
                case 61: goto L_0x17aa;
                case 62: goto L_0x177a;
                case 63: goto L_0x174e;
                case 64: goto L_0x171e;
                case 65: goto L_0x16fc;
                case 66: goto L_0x16fc;
                case 67: goto L_0x16da;
                case 68: goto L_0x16b8;
                case 69: goto L_0x1691;
                case 70: goto L_0x166f;
                case 71: goto L_0x1648;
                case 72: goto L_0x1626;
                case 73: goto L_0x1604;
                case 74: goto L_0x15e2;
                case 75: goto L_0x15c0;
                case 76: goto L_0x159e;
                case 77: goto L_0x157c;
                case 78: goto L_0x155a;
                case 79: goto L_0x1538;
                case 80: goto L_0x1502;
                case 81: goto L_0x14ce;
                case 82: goto L_0x149a;
                case 83: goto L_0x1466;
                case 84: goto L_0x1432;
                case 85: goto L_0x140e;
                case 86: goto L_0x139b;
                case 87: goto L_0x1332;
                case 88: goto L_0x12c9;
                case 89: goto L_0x1260;
                case 90: goto L_0x11f7;
                case 91: goto L_0x118e;
                case 92: goto L_0x109b;
                case 93: goto L_0x1032;
                case 94: goto L_0x0fbf;
                case 95: goto L_0x0f4c;
                case 96: goto L_0x0ed9;
                case 97: goto L_0x0e70;
                case 98: goto L_0x0e07;
                case 99: goto L_0x0d9e;
                case 100: goto L_0x0d35;
                case 101: goto L_0x0ccc;
                case 102: goto L_0x0CLASSNAME;
                case 103: goto L_0x0CLASSNAME;
                case 104: goto L_0x0c3e;
                case 105: goto L_0x0c3e;
                case 106: goto L_0x0c3e;
                case 107: goto L_0x0c3e;
                case 108: goto L_0x0c3e;
                case 109: goto L_0x0c3e;
                case 110: goto L_0x0c3e;
                case 111: goto L_0x0c3e;
                case 112: goto L_0x0c3e;
                default: goto L_0x0CLASSNAME;
            }
        L_0x0CLASSNAME:
            r4 = 0
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x2248 }
            goto L_0x224d
        L_0x0c3e:
            r3 = r74
            r4 = 0
            goto L_0x2280
        L_0x0CLASSNAME:
            java.lang.String r1 = "YouHaveNewMessage"
            r2 = 2131628719(0x7f0e12af, float:1.8884739E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "SecretChatName"
            r2 = 2131627669(0x7f0e0e95, float:1.8882609E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r12 = r1
            r30 = 1
            r1 = r3
            r2 = r30
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0CLASSNAME:
            r1 = 0
            int r3 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x0c8b
            java.lang.String r1 = "NotificationActionPinnedGifUser"
            r2 = 2131626589(0x7f0e0a5d, float:1.8880418E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0c8b:
            if (r6 == 0) goto L_0x0caf
            java.lang.String r1 = "NotificationActionPinnedGif"
            r2 = 2131626587(0x7f0e0a5b, float:1.8880414E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0caf:
            java.lang.String r1 = "NotificationActionPinnedGifChannel"
            r2 = 2131626588(0x7f0e0a5c, float:1.8880416E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0ccc:
            r1 = 0
            int r3 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x0cf4
            java.lang.String r1 = "NotificationActionPinnedInvoiceUser"
            r2 = 2131626592(0x7f0e0a60, float:1.8880425E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0cf4:
            if (r6 == 0) goto L_0x0d18
            java.lang.String r1 = "NotificationActionPinnedInvoice"
            r2 = 2131626590(0x7f0e0a5e, float:1.888042E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0d18:
            java.lang.String r1 = "NotificationActionPinnedInvoiceChannel"
            r2 = 2131626591(0x7f0e0a5f, float:1.8880423E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0d35:
            r1 = 0
            int r3 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x0d5d
            java.lang.String r1 = "NotificationActionPinnedGameScoreUser"
            r2 = 2131626579(0x7f0e0a53, float:1.8880398E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0d5d:
            if (r6 == 0) goto L_0x0d81
            java.lang.String r1 = "NotificationActionPinnedGameScore"
            r2 = 2131626577(0x7f0e0a51, float:1.8880394E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0d81:
            java.lang.String r1 = "NotificationActionPinnedGameScoreChannel"
            r2 = 2131626578(0x7f0e0a52, float:1.8880396E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0d9e:
            r1 = 0
            int r3 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x0dc6
            java.lang.String r1 = "NotificationActionPinnedGameUser"
            r2 = 2131626580(0x7f0e0a54, float:1.88804E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0dc6:
            if (r6 == 0) goto L_0x0dea
            java.lang.String r1 = "NotificationActionPinnedGame"
            r2 = 2131626575(0x7f0e0a4f, float:1.888039E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0dea:
            java.lang.String r1 = "NotificationActionPinnedGameChannel"
            r2 = 2131626576(0x7f0e0a50, float:1.8880392E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0e07:
            r1 = 0
            int r3 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x0e2f
            java.lang.String r1 = "NotificationActionPinnedGeoLiveUser"
            r2 = 2131626585(0x7f0e0a59, float:1.888041E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0e2f:
            if (r6 == 0) goto L_0x0e53
            java.lang.String r1 = "NotificationActionPinnedGeoLive"
            r2 = 2131626583(0x7f0e0a57, float:1.8880406E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0e53:
            java.lang.String r1 = "NotificationActionPinnedGeoLiveChannel"
            r2 = 2131626584(0x7f0e0a58, float:1.8880408E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0e70:
            r1 = 0
            int r3 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x0e98
            java.lang.String r1 = "NotificationActionPinnedGeoUser"
            r2 = 2131626586(0x7f0e0a5a, float:1.8880412E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0e98:
            if (r6 == 0) goto L_0x0ebc
            java.lang.String r1 = "NotificationActionPinnedGeo"
            r2 = 2131626581(0x7f0e0a55, float:1.8880402E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0ebc:
            java.lang.String r1 = "NotificationActionPinnedGeoChannel"
            r2 = 2131626582(0x7f0e0a56, float:1.8880404E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0ed9:
            r1 = 0
            int r3 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedPollUser"
            r2 = 2131626604(0x7f0e0a6c, float:1.8880449E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0var_:
            if (r6 == 0) goto L_0x0f2a
            java.lang.String r1 = "NotificationActionPinnedPoll2"
            r2 = 2131626602(0x7f0e0a6a, float:1.8880445E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 2
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r7 = 1
            r3[r7] = r5     // Catch:{ all -> 0x2248 }
            r5 = r11[r7]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0f2a:
            java.lang.String r1 = "NotificationActionPinnedPollChannel2"
            r2 = 2131626603(0x7f0e0a6b, float:1.8880447E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0f4c:
            r1 = 0
            int r3 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedQuizUser"
            r2 = 2131626607(0x7f0e0a6f, float:1.8880455E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0var_:
            if (r6 == 0) goto L_0x0f9d
            java.lang.String r1 = "NotificationActionPinnedQuiz2"
            r2 = 2131626605(0x7f0e0a6d, float:1.888045E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 2
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r7 = 1
            r3[r7] = r5     // Catch:{ all -> 0x2248 }
            r5 = r11[r7]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0f9d:
            java.lang.String r1 = "NotificationActionPinnedQuizChannel2"
            r2 = 2131626606(0x7f0e0a6e, float:1.8880453E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0fbf:
            r1 = 0
            int r3 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x0fe7
            java.lang.String r1 = "NotificationActionPinnedContactUser"
            r2 = 2131626571(0x7f0e0a4b, float:1.8880382E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x0fe7:
            if (r6 == 0) goto L_0x1010
            java.lang.String r1 = "NotificationActionPinnedContact2"
            r2 = 2131626569(0x7f0e0a49, float:1.8880378E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 2
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r7 = 1
            r3[r7] = r5     // Catch:{ all -> 0x2248 }
            r5 = r11[r7]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1010:
            java.lang.String r1 = "NotificationActionPinnedContactChannel2"
            r2 = 2131626570(0x7f0e0a4a, float:1.888038E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1032:
            r1 = 0
            int r3 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x105a
            java.lang.String r1 = "NotificationActionPinnedVoiceUser"
            r2 = 2131626625(0x7f0e0a81, float:1.8880491E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x105a:
            if (r6 == 0) goto L_0x107e
            java.lang.String r1 = "NotificationActionPinnedVoice"
            r2 = 2131626623(0x7f0e0a7f, float:1.8880487E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x107e:
            java.lang.String r1 = "NotificationActionPinnedVoiceChannel"
            r2 = 2131626624(0x7f0e0a80, float:1.888049E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x109b:
            r1 = 0
            int r3 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x10ec
            int r1 = r11.length     // Catch:{ all -> 0x2248 }
            r2 = 1
            if (r1 <= r2) goto L_0x10cf
            r1 = r11[r2]     // Catch:{ all -> 0x2248 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x2248 }
            if (r1 != 0) goto L_0x10cf
            java.lang.String r1 = "NotificationActionPinnedStickerEmojiUser"
            r2 = 2131626615(0x7f0e0a77, float:1.8880471E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x10cf:
            java.lang.String r1 = "NotificationActionPinnedStickerUser"
            r2 = 2131626616(0x7f0e0a78, float:1.8880473E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x10ec:
            if (r6 == 0) goto L_0x1143
            int r1 = r11.length     // Catch:{ all -> 0x2248 }
            r2 = 2
            if (r1 <= r2) goto L_0x1121
            r1 = r11[r2]     // Catch:{ all -> 0x2248 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x2248 }
            if (r1 != 0) goto L_0x1121
            java.lang.String r1 = "NotificationActionPinnedStickerEmoji"
            r2 = 2131626613(0x7f0e0a75, float:1.8880467E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 2
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r7 = 1
            r3[r7] = r5     // Catch:{ all -> 0x2248 }
            r5 = r11[r7]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1121:
            java.lang.String r1 = "NotificationActionPinnedSticker"
            r2 = 2131626611(0x7f0e0a73, float:1.8880463E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1143:
            int r1 = r11.length     // Catch:{ all -> 0x2248 }
            r2 = 1
            if (r1 <= r2) goto L_0x1171
            r1 = r11[r2]     // Catch:{ all -> 0x2248 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x2248 }
            if (r1 != 0) goto L_0x1171
            java.lang.String r1 = "NotificationActionPinnedStickerEmojiChannel"
            r2 = 2131626614(0x7f0e0a76, float:1.888047E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1171:
            java.lang.String r1 = "NotificationActionPinnedStickerChannel"
            r2 = 2131626612(0x7f0e0a74, float:1.8880465E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x118e:
            r1 = 0
            int r3 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x11b6
            java.lang.String r1 = "NotificationActionPinnedFileUser"
            r2 = 2131626574(0x7f0e0a4e, float:1.8880388E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x11b6:
            if (r6 == 0) goto L_0x11da
            java.lang.String r1 = "NotificationActionPinnedFile"
            r2 = 2131626572(0x7f0e0a4c, float:1.8880384E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x11da:
            java.lang.String r1 = "NotificationActionPinnedFileChannel"
            r2 = 2131626573(0x7f0e0a4d, float:1.8880386E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x11f7:
            r1 = 0
            int r3 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x121f
            java.lang.String r1 = "NotificationActionPinnedRoundUser"
            r2 = 2131626610(0x7f0e0a72, float:1.8880461E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x121f:
            if (r6 == 0) goto L_0x1243
            java.lang.String r1 = "NotificationActionPinnedRound"
            r2 = 2131626608(0x7f0e0a70, float:1.8880457E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1243:
            java.lang.String r1 = "NotificationActionPinnedRoundChannel"
            r2 = 2131626609(0x7f0e0a71, float:1.888046E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1260:
            r1 = 0
            int r3 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x1288
            java.lang.String r1 = "NotificationActionPinnedVideoUser"
            r2 = 2131626622(0x7f0e0a7e, float:1.8880485E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1288:
            if (r6 == 0) goto L_0x12ac
            java.lang.String r1 = "NotificationActionPinnedVideo"
            r2 = 2131626620(0x7f0e0a7c, float:1.8880481E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x12ac:
            java.lang.String r1 = "NotificationActionPinnedVideoChannel"
            r2 = 2131626621(0x7f0e0a7d, float:1.8880483E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x12c9:
            r1 = 0
            int r3 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x12f1
            java.lang.String r1 = "NotificationActionPinnedPhotoUser"
            r2 = 2131626601(0x7f0e0a69, float:1.8880443E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x12f1:
            if (r6 == 0) goto L_0x1315
            java.lang.String r1 = "NotificationActionPinnedPhoto"
            r2 = 2131626599(0x7f0e0a67, float:1.8880439E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1315:
            java.lang.String r1 = "NotificationActionPinnedPhotoChannel"
            r2 = 2131626600(0x7f0e0a68, float:1.888044E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1332:
            r1 = 0
            int r3 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x135a
            java.lang.String r1 = "NotificationActionPinnedNoTextUser"
            r2 = 2131626598(0x7f0e0a66, float:1.8880437E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x135a:
            if (r6 == 0) goto L_0x137e
            java.lang.String r1 = "NotificationActionPinnedNoText"
            r2 = 2131626596(0x7f0e0a64, float:1.8880433E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x137e:
            java.lang.String r1 = "NotificationActionPinnedNoTextChannel"
            r2 = 2131626597(0x7f0e0a65, float:1.8880435E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x139b:
            r1 = 0
            int r3 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x13c3
            java.lang.String r1 = "NotificationActionPinnedTextUser"
            r2 = 2131626619(0x7f0e0a7b, float:1.888048E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x13c3:
            if (r6 == 0) goto L_0x13ec
            java.lang.String r1 = "NotificationActionPinnedText"
            r2 = 2131626617(0x7f0e0a79, float:1.8880475E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 2
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x13ec:
            java.lang.String r1 = "NotificationActionPinnedTextChannel"
            r2 = 2131626618(0x7f0e0a7a, float:1.8880477E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x140e:
            java.lang.String r1 = "NotificationGroupAlbum"
            r2 = 2131626634(0x7f0e0a8a, float:1.888051E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1432:
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x2248 }
            r2 = 0
            r4 = r11[r2]     // Catch:{ all -> 0x2248 }
            r1[r2] = r4     // Catch:{ all -> 0x2248 }
            r2 = 1
            r4 = r11[r2]     // Catch:{ all -> 0x2248 }
            r1[r2] = r4     // Catch:{ all -> 0x2248 }
            r2 = 2
            r4 = r11[r2]     // Catch:{ all -> 0x2248 }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x2248 }
            int r2 = r2.intValue()     // Catch:{ all -> 0x2248 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r10, r2)     // Catch:{ all -> 0x2248 }
            r4 = 2
            r1[r4] = r2     // Catch:{ all -> 0x2248 }
            r2 = 2131626637(0x7f0e0a8d, float:1.8880516E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r2, r1)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1466:
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x2248 }
            r2 = 0
            r4 = r11[r2]     // Catch:{ all -> 0x2248 }
            r1[r2] = r4     // Catch:{ all -> 0x2248 }
            r2 = 1
            r4 = r11[r2]     // Catch:{ all -> 0x2248 }
            r1[r2] = r4     // Catch:{ all -> 0x2248 }
            r2 = 2
            r4 = r11[r2]     // Catch:{ all -> 0x2248 }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x2248 }
            int r2 = r2.intValue()     // Catch:{ all -> 0x2248 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r12, r2)     // Catch:{ all -> 0x2248 }
            r4 = 2
            r1[r4] = r2     // Catch:{ all -> 0x2248 }
            r2 = 2131626637(0x7f0e0a8d, float:1.8880516E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r2, r1)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x149a:
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x2248 }
            r2 = 0
            r4 = r11[r2]     // Catch:{ all -> 0x2248 }
            r1[r2] = r4     // Catch:{ all -> 0x2248 }
            r2 = 1
            r4 = r11[r2]     // Catch:{ all -> 0x2248 }
            r1[r2] = r4     // Catch:{ all -> 0x2248 }
            r2 = 2
            r4 = r11[r2]     // Catch:{ all -> 0x2248 }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x2248 }
            int r2 = r2.intValue()     // Catch:{ all -> 0x2248 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r13, r2)     // Catch:{ all -> 0x2248 }
            r4 = 2
            r1[r4] = r2     // Catch:{ all -> 0x2248 }
            r2 = 2131626637(0x7f0e0a8d, float:1.8880516E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r2, r1)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x14ce:
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x2248 }
            r2 = 0
            r4 = r11[r2]     // Catch:{ all -> 0x2248 }
            r1[r2] = r4     // Catch:{ all -> 0x2248 }
            r2 = 1
            r4 = r11[r2]     // Catch:{ all -> 0x2248 }
            r1[r2] = r4     // Catch:{ all -> 0x2248 }
            r2 = 2
            r4 = r11[r2]     // Catch:{ all -> 0x2248 }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x2248 }
            int r2 = r2.intValue()     // Catch:{ all -> 0x2248 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r8, r2)     // Catch:{ all -> 0x2248 }
            r4 = 2
            r1[r4] = r2     // Catch:{ all -> 0x2248 }
            r2 = 2131626637(0x7f0e0a8d, float:1.8880516E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r2, r1)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1502:
            java.lang.String r1 = "NotificationGroupForwardedFew"
            r2 = 2131626638(0x7f0e0a8e, float:1.8880518E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 2
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r5)     // Catch:{ all -> 0x2248 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x2248 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r7, r4)     // Catch:{ all -> 0x2248 }
            r5 = 2
            r3[r5] = r4     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1538:
            java.lang.String r1 = "UserAcceptedToGroupPushWithGroup"
            r2 = 2131628272(0x7f0e10f0, float:1.8883832E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x155a:
            java.lang.String r1 = "NotificationGroupAddSelfMega"
            r2 = 2131626633(0x7f0e0a89, float:1.8880508E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x157c:
            java.lang.String r1 = "NotificationGroupAddSelf"
            r2 = 2131626632(0x7f0e0a88, float:1.8880506E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x159e:
            java.lang.String r1 = "NotificationGroupLeftMember"
            r2 = 2131626643(0x7f0e0a93, float:1.8880528E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x15c0:
            java.lang.String r1 = "NotificationGroupKickYou"
            r2 = 2131626642(0x7f0e0a92, float:1.8880526E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x15e2:
            java.lang.String r1 = "NotificationGroupKickMember"
            r2 = 2131626641(0x7f0e0a91, float:1.8880524E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1604:
            java.lang.String r1 = "NotificationGroupInvitedYouToCall"
            r2 = 2131626640(0x7f0e0a90, float:1.8880522E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1626:
            java.lang.String r1 = "NotificationGroupEndedCall"
            r2 = 2131626636(0x7f0e0a8c, float:1.8880514E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1648:
            java.lang.String r1 = "NotificationGroupInvitedToCall"
            r2 = 2131626639(0x7f0e0a8f, float:1.888052E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 2
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x166f:
            java.lang.String r1 = "NotificationGroupCreatedCall"
            r2 = 2131626635(0x7f0e0a8b, float:1.8880512E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1691:
            java.lang.String r1 = "NotificationGroupAddMember"
            r2 = 2131626631(0x7f0e0a87, float:1.8880504E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 2
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x16b8:
            java.lang.String r1 = "NotificationEditedGroupPhoto"
            r2 = 2131626629(0x7f0e0a85, float:1.88805E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x16da:
            java.lang.String r1 = "NotificationEditedGroupName"
            r2 = 2131626628(0x7f0e0a84, float:1.8880498E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x16fc:
            java.lang.String r1 = "NotificationInvitedToGroup"
            r2 = 2131626648(0x7f0e0a98, float:1.8880538E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x171e:
            java.lang.String r1 = "NotificationMessageGroupInvoice"
            r2 = 2131626665(0x7f0e0aa9, float:1.8880573E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 2
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "PaymentInvoice"
            r2 = 2131627060(0x7f0e0CLASSNAME, float:1.8881374E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x174e:
            java.lang.String r1 = "NotificationMessageGroupGameScored"
            r2 = 2131626663(0x7f0e0aa7, float:1.8880569E38)
            r3 = 4
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 2
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 3
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x177a:
            java.lang.String r1 = "NotificationMessageGroupGame"
            r2 = 2131626662(0x7f0e0aa6, float:1.8880567E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 2
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachGame"
            r2 = 2131624406(0x7f0e01d6, float:1.887599E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x17aa:
            java.lang.String r1 = "NotificationMessageGroupGif"
            r2 = 2131626664(0x7f0e0aa8, float:1.888057E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachGif"
            r2 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x17d5:
            java.lang.String r1 = "NotificationMessageGroupLiveLocation"
            r2 = 2131626666(0x7f0e0aaa, float:1.8880575E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachLiveLocation"
            r2 = 2131624412(0x7f0e01dc, float:1.8876003E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1800:
            java.lang.String r1 = "NotificationMessageGroupMap"
            r2 = 2131626667(0x7f0e0aab, float:1.8880577E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachLocation"
            r2 = 2131624416(0x7f0e01e0, float:1.8876011E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x182b:
            java.lang.String r1 = "NotificationMessageGroupPoll2"
            r2 = 2131626671(0x7f0e0aaf, float:1.8880585E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 2
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "Poll"
            r2 = 2131627227(0x7f0e0cdb, float:1.8881712E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x185b:
            java.lang.String r1 = "NotificationMessageGroupQuiz2"
            r2 = 2131626672(0x7f0e0ab0, float:1.8880587E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 2
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "PollQuiz"
            r2 = 2131627234(0x7f0e0ce2, float:1.8881727E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x188b:
            java.lang.String r1 = "NotificationMessageGroupContact2"
            r2 = 2131626660(0x7f0e0aa4, float:1.8880562E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 2
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachContact"
            r2 = 2131624402(0x7f0e01d2, float:1.8875983E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x18bb:
            java.lang.String r1 = "NotificationMessageGroupAudio"
            r2 = 2131626659(0x7f0e0aa3, float:1.888056E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachAudio"
            r2 = 2131624400(0x7f0e01d0, float:1.8875979E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x18e6:
            int r1 = r11.length     // Catch:{ all -> 0x2248 }
            r2 = 2
            if (r1 <= r2) goto L_0x1935
            r1 = r11[r2]     // Catch:{ all -> 0x2248 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x2248 }
            if (r1 != 0) goto L_0x1935
            java.lang.String r1 = "NotificationMessageGroupStickerEmoji"
            r2 = 2131626675(0x7f0e0ab3, float:1.8880593E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r5 = 0
            r7 = r11[r5]     // Catch:{ all -> 0x2248 }
            r3[r5] = r7     // Catch:{ all -> 0x2248 }
            r5 = 1
            r7 = r11[r5]     // Catch:{ all -> 0x2248 }
            r3[r5] = r7     // Catch:{ all -> 0x2248 }
            r5 = 2
            r7 = r11[r5]     // Catch:{ all -> 0x2248 }
            r3[r5] = r7     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x2248 }
            r1.<init>()     // Catch:{ all -> 0x2248 }
            r2 = 2
            r2 = r11[r2]     // Catch:{ all -> 0x2248 }
            r1.append(r2)     // Catch:{ all -> 0x2248 }
            r1.append(r9)     // Catch:{ all -> 0x2248 }
            r2 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)     // Catch:{ all -> 0x2248 }
            r1.append(r2)     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1935:
            java.lang.String r1 = "NotificationMessageGroupSticker"
            r2 = 2131626674(0x7f0e0ab2, float:1.888059E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r5 = 0
            r7 = r11[r5]     // Catch:{ all -> 0x2248 }
            r3[r5] = r7     // Catch:{ all -> 0x2248 }
            r5 = 1
            r7 = r11[r5]     // Catch:{ all -> 0x2248 }
            r3[r5] = r7     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x2248 }
            r1.<init>()     // Catch:{ all -> 0x2248 }
            r2 = 1
            r5 = r11[r2]     // Catch:{ all -> 0x2248 }
            r1.append(r5)     // Catch:{ all -> 0x2248 }
            r1.append(r9)     // Catch:{ all -> 0x2248 }
            r2 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)     // Catch:{ all -> 0x2248 }
            r1.append(r2)     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1973:
            java.lang.String r1 = "NotificationMessageGroupDocument"
            r2 = 2131626661(0x7f0e0aa5, float:1.8880564E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachDocument"
            r2 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x199e:
            java.lang.String r1 = "NotificationMessageGroupRound"
            r2 = 2131626673(0x7f0e0ab1, float:1.8880589E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachRound"
            r2 = 2131624423(0x7f0e01e7, float:1.8876025E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x19c9:
            java.lang.String r1 = "NotificationMessageGroupVideo"
            r2 = 2131626677(0x7f0e0ab5, float:1.8880597E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachVideo"
            r2 = 2131624427(0x7f0e01eb, float:1.8876033E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x19f4:
            java.lang.String r1 = "NotificationMessageGroupPhoto"
            r2 = 2131626670(0x7f0e0aae, float:1.8880583E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachPhoto"
            r2 = 2131624421(0x7f0e01e5, float:1.8876021E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1a1f:
            java.lang.String r1 = "NotificationMessageGroupNoText"
            r2 = 2131626669(0x7f0e0aad, float:1.888058E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "Message"
            r2 = 2131626319(0x7f0e094f, float:1.887987E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1a4a:
            java.lang.String r1 = "NotificationMessageGroupText"
            r2 = 2131626676(0x7f0e0ab4, float:1.8880595E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 2
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r1 = r11[r4]     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1a73:
            java.lang.String r1 = "ChannelMessageAlbum"
            r2 = 2131624784(0x7f0e0350, float:1.8876757E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1a92:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r4 = r11[r3]     // Catch:{ all -> 0x2248 }
            r1[r3] = r4     // Catch:{ all -> 0x2248 }
            r3 = 1
            r4 = r11[r3]     // Catch:{ all -> 0x2248 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x2248 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x2248 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r10, r3)     // Catch:{ all -> 0x2248 }
            r4 = 1
            r1[r4] = r3     // Catch:{ all -> 0x2248 }
            r3 = 2131624788(0x7f0e0354, float:1.8876766E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r3, r1)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1ac1:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r4 = r11[r3]     // Catch:{ all -> 0x2248 }
            r1[r3] = r4     // Catch:{ all -> 0x2248 }
            r3 = 1
            r4 = r11[r3]     // Catch:{ all -> 0x2248 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x2248 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x2248 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r12, r3)     // Catch:{ all -> 0x2248 }
            r4 = 1
            r1[r4] = r3     // Catch:{ all -> 0x2248 }
            r3 = 2131624788(0x7f0e0354, float:1.8876766E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r3, r1)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1af0:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r4 = r11[r3]     // Catch:{ all -> 0x2248 }
            r1[r3] = r4     // Catch:{ all -> 0x2248 }
            r3 = 1
            r4 = r11[r3]     // Catch:{ all -> 0x2248 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x2248 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x2248 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r13, r3)     // Catch:{ all -> 0x2248 }
            r4 = 1
            r1[r4] = r3     // Catch:{ all -> 0x2248 }
            r3 = 2131624788(0x7f0e0354, float:1.8876766E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r3, r1)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1b1f:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r4 = r11[r3]     // Catch:{ all -> 0x2248 }
            r1[r3] = r4     // Catch:{ all -> 0x2248 }
            r3 = 1
            r4 = r11[r3]     // Catch:{ all -> 0x2248 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x2248 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x2248 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r8, r3)     // Catch:{ all -> 0x2248 }
            r4 = 1
            r1[r4] = r3     // Catch:{ all -> 0x2248 }
            r3 = 2131624788(0x7f0e0354, float:1.8876766E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r3, r1)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1b4e:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r4 = r11[r3]     // Catch:{ all -> 0x2248 }
            r1[r3] = r4     // Catch:{ all -> 0x2248 }
            java.lang.String r3 = "ForwardedMessageCount"
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r5)     // Catch:{ all -> 0x2248 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x2248 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r4)     // Catch:{ all -> 0x2248 }
            java.lang.String r3 = r3.toLowerCase()     // Catch:{ all -> 0x2248 }
            r4 = 1
            r1[r4] = r3     // Catch:{ all -> 0x2248 }
            r3 = 2131624788(0x7f0e0354, float:1.8876766E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r3, r1)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1b83:
            java.lang.String r1 = "NotificationMessageGame"
            r2 = 2131626656(0x7f0e0aa0, float:1.8880554E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachGame"
            r2 = 2131624406(0x7f0e01d6, float:1.887599E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1ba9:
            java.lang.String r1 = "ChannelMessageGIF"
            r2 = 2131624789(0x7f0e0355, float:1.8876768E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachGif"
            r2 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1bcf:
            java.lang.String r1 = "ChannelMessageLiveLocation"
            r2 = 2131624790(0x7f0e0356, float:1.887677E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachLiveLocation"
            r2 = 2131624412(0x7f0e01dc, float:1.8876003E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1bf5:
            java.lang.String r1 = "ChannelMessageMap"
            r2 = 2131624791(0x7f0e0357, float:1.8876772E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachLocation"
            r2 = 2131624416(0x7f0e01e0, float:1.8876011E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1c1b:
            java.lang.String r1 = "ChannelMessagePoll2"
            r2 = 2131624795(0x7f0e035b, float:1.887678E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "Poll"
            r2 = 2131627227(0x7f0e0cdb, float:1.8881712E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1CLASSNAME:
            java.lang.String r1 = "ChannelMessageQuiz2"
            r2 = 2131624796(0x7f0e035c, float:1.8876782E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "QuizPoll"
            r2 = 2131627408(0x7f0e0d90, float:1.888208E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1CLASSNAME:
            java.lang.String r1 = "ChannelMessageContact2"
            r2 = 2131624786(0x7f0e0352, float:1.8876762E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachContact"
            r2 = 2131624402(0x7f0e01d2, float:1.8875983E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1c9c:
            java.lang.String r1 = "ChannelMessageAudio"
            r2 = 2131624785(0x7f0e0351, float:1.887676E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachAudio"
            r2 = 2131624400(0x7f0e01d0, float:1.8875979E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1cc2:
            int r1 = r11.length     // Catch:{ all -> 0x2248 }
            r2 = 1
            if (r1 <= r2) goto L_0x1d0c
            r1 = r11[r2]     // Catch:{ all -> 0x2248 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x2248 }
            if (r1 != 0) goto L_0x1d0c
            java.lang.String r1 = "ChannelMessageStickerEmoji"
            r2 = 2131624799(0x7f0e035f, float:1.8876788E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r5 = 0
            r7 = r11[r5]     // Catch:{ all -> 0x2248 }
            r3[r5] = r7     // Catch:{ all -> 0x2248 }
            r5 = 1
            r7 = r11[r5]     // Catch:{ all -> 0x2248 }
            r3[r5] = r7     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x2248 }
            r1.<init>()     // Catch:{ all -> 0x2248 }
            r2 = 1
            r5 = r11[r2]     // Catch:{ all -> 0x2248 }
            r1.append(r5)     // Catch:{ all -> 0x2248 }
            r1.append(r9)     // Catch:{ all -> 0x2248 }
            r2 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)     // Catch:{ all -> 0x2248 }
            r1.append(r2)     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1d0c:
            java.lang.String r1 = "ChannelMessageSticker"
            r2 = 2131624798(0x7f0e035e, float:1.8876786E38)
            r3 = 1
            java.lang.Object[] r5 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r7 = r11[r3]     // Catch:{ all -> 0x2248 }
            r5[r3] = r7     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r1 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1d30:
            java.lang.String r1 = "ChannelMessageDocument"
            r2 = 2131624787(0x7f0e0353, float:1.8876764E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachDocument"
            r2 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1d56:
            java.lang.String r1 = "ChannelMessageRound"
            r2 = 2131624797(0x7f0e035d, float:1.8876784E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachRound"
            r2 = 2131624423(0x7f0e01e7, float:1.8876025E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1d7c:
            java.lang.String r1 = "ChannelMessageVideo"
            r2 = 2131624800(0x7f0e0360, float:1.887679E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachVideo"
            r2 = 2131624427(0x7f0e01eb, float:1.8876033E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1da2:
            java.lang.String r1 = "ChannelMessagePhoto"
            r2 = 2131624794(0x7f0e035a, float:1.8876778E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachPhoto"
            r2 = 2131624421(0x7f0e01e5, float:1.8876021E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1dc8:
            java.lang.String r1 = "ChannelMessageNoText"
            r2 = 2131624793(0x7f0e0359, float:1.8876776E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "Message"
            r2 = 2131626319(0x7f0e094f, float:1.887987E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1dee:
            java.lang.String r1 = "NotificationMessageAlbum"
            r2 = 2131626650(0x7f0e0a9a, float:1.8880542E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1e0d:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x2248 }
            r2 = 0
            r3 = r11[r2]     // Catch:{ all -> 0x2248 }
            r1[r2] = r3     // Catch:{ all -> 0x2248 }
            r2 = 1
            r3 = r11[r2]     // Catch:{ all -> 0x2248 }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x2248 }
            int r2 = r2.intValue()     // Catch:{ all -> 0x2248 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r10, r2)     // Catch:{ all -> 0x2248 }
            r3 = 1
            r1[r3] = r2     // Catch:{ all -> 0x2248 }
            r2 = 2131626654(0x7f0e0a9e, float:1.888055E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r2, r1)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1e3c:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x2248 }
            r2 = 0
            r3 = r11[r2]     // Catch:{ all -> 0x2248 }
            r1[r2] = r3     // Catch:{ all -> 0x2248 }
            r2 = 1
            r3 = r11[r2]     // Catch:{ all -> 0x2248 }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x2248 }
            int r2 = r2.intValue()     // Catch:{ all -> 0x2248 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r12, r2)     // Catch:{ all -> 0x2248 }
            r3 = 1
            r1[r3] = r2     // Catch:{ all -> 0x2248 }
            r2 = 2131626654(0x7f0e0a9e, float:1.888055E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r2, r1)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1e6b:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x2248 }
            r2 = 0
            r3 = r11[r2]     // Catch:{ all -> 0x2248 }
            r1[r2] = r3     // Catch:{ all -> 0x2248 }
            r2 = 1
            r3 = r11[r2]     // Catch:{ all -> 0x2248 }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x2248 }
            int r2 = r2.intValue()     // Catch:{ all -> 0x2248 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r13, r2)     // Catch:{ all -> 0x2248 }
            r3 = 1
            r1[r3] = r2     // Catch:{ all -> 0x2248 }
            r2 = 2131626654(0x7f0e0a9e, float:1.888055E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r2, r1)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1e9a:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x2248 }
            r2 = 0
            r3 = r11[r2]     // Catch:{ all -> 0x2248 }
            r1[r2] = r3     // Catch:{ all -> 0x2248 }
            r2 = 1
            r3 = r11[r2]     // Catch:{ all -> 0x2248 }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x2248 }
            int r2 = r2.intValue()     // Catch:{ all -> 0x2248 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r8, r2)     // Catch:{ all -> 0x2248 }
            r3 = 1
            r1[r3] = r2     // Catch:{ all -> 0x2248 }
            r2 = 2131626654(0x7f0e0a9e, float:1.888055E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r2, r1)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1ec9:
            java.lang.String r1 = "NotificationMessageForwardFew"
            r2 = 2131626655(0x7f0e0a9f, float:1.8880552E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r5)     // Catch:{ all -> 0x2248 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x2248 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r7, r4)     // Catch:{ all -> 0x2248 }
            r5 = 1
            r3[r5] = r4     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r30 = 1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1efa:
            java.lang.String r1 = "NotificationMessageInvoice"
            r2 = 2131626678(0x7f0e0ab6, float:1.8880599E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "PaymentInvoice"
            r2 = 2131627060(0x7f0e0CLASSNAME, float:1.8881374E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1var_:
            java.lang.String r1 = "NotificationMessageGameScored"
            r2 = 2131626657(0x7f0e0aa1, float:1.8880556E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 2
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1f4c:
            java.lang.String r1 = "NotificationMessageGame"
            r2 = 2131626656(0x7f0e0aa0, float:1.8880554E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachGame"
            r2 = 2131624406(0x7f0e01d6, float:1.887599E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1var_:
            java.lang.String r1 = "NotificationMessageGif"
            r2 = 2131626658(0x7f0e0aa2, float:1.8880558E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachGif"
            r2 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1f9d:
            java.lang.String r1 = "NotificationMessageLiveLocation"
            r2 = 2131626679(0x7f0e0ab7, float:1.88806E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachLiveLocation"
            r2 = 2131624412(0x7f0e01dc, float:1.8876003E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1fc3:
            java.lang.String r1 = "NotificationMessageMap"
            r2 = 2131626680(0x7f0e0ab8, float:1.8880603E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachLocation"
            r2 = 2131624416(0x7f0e01e0, float:1.8876011E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x1fe9:
            java.lang.String r1 = "NotificationMessagePoll2"
            r2 = 2131626684(0x7f0e0abc, float:1.8880611E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "Poll"
            r2 = 2131627227(0x7f0e0cdb, float:1.8881712E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x2014:
            java.lang.String r1 = "NotificationMessageQuiz2"
            r2 = 2131626685(0x7f0e0abd, float:1.8880613E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "QuizPoll"
            r2 = 2131627408(0x7f0e0d90, float:1.888208E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x203f:
            java.lang.String r1 = "NotificationMessageContact2"
            r2 = 2131626652(0x7f0e0a9c, float:1.8880546E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r4 = 1
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachContact"
            r2 = 2131624402(0x7f0e01d2, float:1.8875983E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x206a:
            java.lang.String r1 = "NotificationMessageAudio"
            r2 = 2131626651(0x7f0e0a9b, float:1.8880544E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachAudio"
            r2 = 2131624400(0x7f0e01d0, float:1.8875979E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x2090:
            int r1 = r11.length     // Catch:{ all -> 0x2248 }
            r2 = 1
            if (r1 <= r2) goto L_0x20da
            r1 = r11[r2]     // Catch:{ all -> 0x2248 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x2248 }
            if (r1 != 0) goto L_0x20da
            java.lang.String r1 = "NotificationMessageStickerEmoji"
            r2 = 2131626692(0x7f0e0ac4, float:1.8880627E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r5 = 0
            r7 = r11[r5]     // Catch:{ all -> 0x2248 }
            r3[r5] = r7     // Catch:{ all -> 0x2248 }
            r5 = 1
            r7 = r11[r5]     // Catch:{ all -> 0x2248 }
            r3[r5] = r7     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x2248 }
            r1.<init>()     // Catch:{ all -> 0x2248 }
            r2 = 1
            r5 = r11[r2]     // Catch:{ all -> 0x2248 }
            r1.append(r5)     // Catch:{ all -> 0x2248 }
            r1.append(r9)     // Catch:{ all -> 0x2248 }
            r2 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)     // Catch:{ all -> 0x2248 }
            r1.append(r2)     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x20da:
            java.lang.String r1 = "NotificationMessageSticker"
            r2 = 2131626691(0x7f0e0ac3, float:1.8880625E38)
            r3 = 1
            java.lang.Object[] r5 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r7 = r11[r3]     // Catch:{ all -> 0x2248 }
            r5[r3] = r7     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r1 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x20fe:
            java.lang.String r1 = "NotificationMessageDocument"
            r2 = 2131626653(0x7f0e0a9d, float:1.8880548E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachDocument"
            r2 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x2124:
            java.lang.String r1 = "NotificationMessageRound"
            r2 = 2131626686(0x7f0e0abe, float:1.8880615E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachRound"
            r2 = 2131624423(0x7f0e01e7, float:1.8876025E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x214a:
            java.lang.String r1 = "ActionTakeScreenshoot"
            r2 = 2131624172(0x7f0e00ec, float:1.8875516E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            java.lang.String r2 = "un1"
            r3 = 0
            r4 = r11[r3]     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = r1.replace(r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r2 = r30
            r12 = r34
            r10 = r38
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x2168:
            java.lang.String r1 = "NotificationMessageSDVideo"
            r2 = 2131626688(0x7f0e0ac0, float:1.888062E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachDestructingVideo"
            r2 = 2131624404(0x7f0e01d4, float:1.8875987E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x218e:
            java.lang.String r1 = "NotificationMessageVideo"
            r2 = 2131626694(0x7f0e0ac6, float:1.8880631E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachVideo"
            r2 = 2131624427(0x7f0e01eb, float:1.8876033E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x21b4:
            java.lang.String r1 = "NotificationMessageSDPhoto"
            r2 = 2131626687(0x7f0e0abf, float:1.8880617E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachDestructingPhoto"
            r2 = 2131624403(0x7f0e01d3, float:1.8875985E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x21da:
            java.lang.String r1 = "NotificationMessagePhoto"
            r2 = 2131626683(0x7f0e0abb, float:1.888061E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "AttachPhoto"
            r2 = 2131624421(0x7f0e01e5, float:1.8876021E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x2200:
            java.lang.String r1 = "NotificationMessageNoText"
            r2 = 2131626682(0x7f0e0aba, float:1.8880607E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r3 = 0
            r5 = r11[r3]     // Catch:{ all -> 0x2248 }
            r4[r3] = r5     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x2248 }
            r3 = r1
            java.lang.String r1 = "Message"
            r2 = 2131626319(0x7f0e094f, float:1.887987E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            r4 = 0
            goto L_0x2288
        L_0x2226:
            java.lang.String r1 = "NotificationMessageText"
            r2 = 2131626693(0x7f0e0ac5, float:1.888063E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x2248 }
            r4 = 0
            r5 = r11[r4]     // Catch:{ all -> 0x2248 }
            r3[r4] = r5     // Catch:{ all -> 0x2248 }
            r5 = 1
            r7 = r11[r5]     // Catch:{ all -> 0x2248 }
            r3[r5] = r7     // Catch:{ all -> 0x2248 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x2248 }
            r3 = r1
            r1 = r11[r5]     // Catch:{ all -> 0x2248 }
            r10 = r1
            r1 = r3
            r2 = r30
            r12 = r34
            r3 = r74
            goto L_0x2288
        L_0x2248:
            r0 = move-exception
            r3 = r77
            goto L_0x2432
        L_0x224d:
            if (r1 == 0) goto L_0x227e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x2271 }
            r1.<init>()     // Catch:{ all -> 0x2271 }
            java.lang.String r2 = "unhandled loc_key = "
            r1.append(r2)     // Catch:{ all -> 0x2271 }
            r3 = r74
            r1.append(r3)     // Catch:{ all -> 0x2266 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x2266 }
            org.telegram.messenger.FileLog.w(r1)     // Catch:{ all -> 0x2266 }
            goto L_0x2280
        L_0x2266:
            r0 = move-exception
            r2 = r0
            r4 = r3
            r5 = r18
            r1 = r31
            r3 = r77
            goto L_0x2471
        L_0x2271:
            r0 = move-exception
            r3 = r74
            r2 = r0
            r4 = r3
            r5 = r18
            r1 = r31
            r3 = r77
            goto L_0x2471
        L_0x227e:
            r3 = r74
        L_0x2280:
            r2 = r30
            r12 = r34
            r1 = r37
            r10 = r38
        L_0x2288:
            if (r1 == 0) goto L_0x23c7
            org.telegram.tgnet.TLRPC$TL_message r5 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x23b9 }
            r5.<init>()     // Catch:{ all -> 0x23b9 }
            r7 = r73
            r5.id = r7     // Catch:{ all -> 0x23b9 }
            r8 = r55
            r5.random_id = r8     // Catch:{ all -> 0x23b9 }
            if (r10 == 0) goto L_0x229b
            r13 = r10
            goto L_0x229c
        L_0x229b:
            r13 = r1
        L_0x229c:
            r5.message = r13     // Catch:{ all -> 0x23b9 }
            r34 = 1000(0x3e8, double:4.94E-321)
            r13 = r5
            long r4 = r79 / r34
            int r5 = (int) r4     // Catch:{ all -> 0x23b9 }
            r4 = r13
            r4.date = r5     // Catch:{ all -> 0x23b9 }
            if (r72 == 0) goto L_0x22b0
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r5 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x2266 }
            r5.<init>()     // Catch:{ all -> 0x2266 }
            r4.action = r5     // Catch:{ all -> 0x2266 }
        L_0x22b0:
            if (r71 == 0) goto L_0x22b9
            int r5 = r4.flags     // Catch:{ all -> 0x2266 }
            r13 = -2147483648(0xfffffffvar_, float:-0.0)
            r5 = r5 | r13
            r4.flags = r5     // Catch:{ all -> 0x2266 }
        L_0x22b9:
            r4.dialog_id = r14     // Catch:{ all -> 0x23b9 }
            r32 = 0
            int r5 = (r69 > r32 ? 1 : (r69 == r32 ? 0 : -1))
            if (r5 == 0) goto L_0x22da
            org.telegram.tgnet.TLRPC$TL_peerChannel r5 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x2266 }
            r5.<init>()     // Catch:{ all -> 0x2266 }
            r4.peer_id = r5     // Catch:{ all -> 0x2266 }
            org.telegram.tgnet.TLRPC$Peer r5 = r4.peer_id     // Catch:{ all -> 0x2266 }
            r13 = r6
            r73 = r7
            r6 = r69
            r5.channel_id = r6     // Catch:{ all -> 0x2266 }
            r69 = r6
            r55 = r8
            r8 = r51
            r6 = r53
            goto L_0x230c
        L_0x22da:
            r13 = r6
            r73 = r7
            r6 = r69
            r32 = 0
            int r5 = (r53 > r32 ? 1 : (r53 == r32 ? 0 : -1))
            if (r5 == 0) goto L_0x22f9
            org.telegram.tgnet.TLRPC$TL_peerChat r5 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x2266 }
            r5.<init>()     // Catch:{ all -> 0x2266 }
            r4.peer_id = r5     // Catch:{ all -> 0x2266 }
            org.telegram.tgnet.TLRPC$Peer r5 = r4.peer_id     // Catch:{ all -> 0x2266 }
            r69 = r6
            r6 = r53
            r5.chat_id = r6     // Catch:{ all -> 0x2266 }
            r55 = r8
            r8 = r51
            goto L_0x230c
        L_0x22f9:
            r69 = r6
            r6 = r53
            org.telegram.tgnet.TLRPC$TL_peerUser r5 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x23b9 }
            r5.<init>()     // Catch:{ all -> 0x23b9 }
            r4.peer_id = r5     // Catch:{ all -> 0x23b9 }
            org.telegram.tgnet.TLRPC$Peer r5 = r4.peer_id     // Catch:{ all -> 0x23b9 }
            r55 = r8
            r8 = r51
            r5.user_id = r8     // Catch:{ all -> 0x23b9 }
        L_0x230c:
            int r5 = r4.flags     // Catch:{ all -> 0x23b9 }
            r5 = r5 | 256(0x100, float:3.59E-43)
            r4.flags = r5     // Catch:{ all -> 0x23b9 }
            r32 = 0
            int r5 = (r60 > r32 ? 1 : (r60 == r32 ? 0 : -1))
            if (r5 == 0) goto L_0x232a
            org.telegram.tgnet.TLRPC$TL_peerChat r5 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x2266 }
            r5.<init>()     // Catch:{ all -> 0x2266 }
            r4.from_id = r5     // Catch:{ all -> 0x2266 }
            org.telegram.tgnet.TLRPC$Peer r5 = r4.from_id     // Catch:{ all -> 0x2266 }
            r5.chat_id = r6     // Catch:{ all -> 0x2266 }
            r53 = r6
            r51 = r58
            r5 = r65
            goto L_0x2362
        L_0x232a:
            r53 = r6
            r5 = r58
            r32 = 0
            int r7 = (r5 > r32 ? 1 : (r5 == r32 ? 0 : -1))
            if (r7 == 0) goto L_0x2344
            org.telegram.tgnet.TLRPC$TL_peerChannel r7 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x2266 }
            r7.<init>()     // Catch:{ all -> 0x2266 }
            r4.from_id = r7     // Catch:{ all -> 0x2266 }
            org.telegram.tgnet.TLRPC$Peer r7 = r4.from_id     // Catch:{ all -> 0x2266 }
            r7.channel_id = r5     // Catch:{ all -> 0x2266 }
            r51 = r5
            r5 = r65
            goto L_0x2362
        L_0x2344:
            r32 = 0
            int r7 = (r65 > r32 ? 1 : (r65 == r32 ? 0 : -1))
            if (r7 == 0) goto L_0x235a
            org.telegram.tgnet.TLRPC$TL_peerUser r7 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x2266 }
            r7.<init>()     // Catch:{ all -> 0x2266 }
            r4.from_id = r7     // Catch:{ all -> 0x2266 }
            org.telegram.tgnet.TLRPC$Peer r7 = r4.from_id     // Catch:{ all -> 0x2266 }
            r51 = r5
            r5 = r65
            r7.user_id = r5     // Catch:{ all -> 0x2266 }
            goto L_0x2362
        L_0x235a:
            r51 = r5
            r5 = r65
            org.telegram.tgnet.TLRPC$Peer r7 = r4.peer_id     // Catch:{ all -> 0x23b9 }
            r4.from_id = r7     // Catch:{ all -> 0x23b9 }
        L_0x2362:
            if (r64 != 0) goto L_0x2369
            if (r72 == 0) goto L_0x2367
            goto L_0x2369
        L_0x2367:
            r7 = 0
            goto L_0x236a
        L_0x2369:
            r7 = 1
        L_0x236a:
            r4.mentioned = r7     // Catch:{ all -> 0x23b9 }
            r7 = r63
            r4.silent = r7     // Catch:{ all -> 0x23b9 }
            r74 = r3
            r3 = r49
            r4.from_scheduled = r3     // Catch:{ all -> 0x2248 }
            org.telegram.messenger.MessageObject r16 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x2248 }
            r30 = r16
            r32 = r4
            r33 = r1
            r34 = r12
            r35 = r26
            r36 = r2
            r37 = r68
            r38 = r71
            r30.<init>(r31, r32, r33, r34, r35, r36, r37, r38, r39)     // Catch:{ all -> 0x2248 }
            r24 = r16
            java.util.ArrayList r16 = new java.util.ArrayList     // Catch:{ all -> 0x2248 }
            r16.<init>()     // Catch:{ all -> 0x2248 }
            r27 = r16
            r16 = r1
            r1 = r24
            r24 = r2
            r2 = r27
            r2.add(r1)     // Catch:{ all -> 0x2248 }
            r27 = 0
            r30 = r1
            org.telegram.messenger.NotificationsController r1 = org.telegram.messenger.NotificationsController.getInstance(r31)     // Catch:{ all -> 0x2248 }
            r49 = r3
            r32 = r4
            r3 = r77
            java.util.concurrent.CountDownLatch r4 = r3.countDownLatch     // Catch:{ all -> 0x2431 }
            r65 = r5
            r5 = 1
            r1.processNewMessages(r2, r5, r5, r4)     // Catch:{ all -> 0x2431 }
            r1 = r27
            goto L_0x2418
        L_0x23b9:
            r0 = move-exception
            r74 = r3
            r3 = r77
            r2 = r0
            r5 = r18
            r1 = r31
            r4 = r74
            goto L_0x2471
        L_0x23c7:
            r16 = r1
            r24 = r2
            r74 = r3
            r13 = r6
            r8 = r51
            r51 = r58
            r7 = r63
            r3 = r77
            goto L_0x2416
        L_0x23d7:
            r73 = r2
            r62 = r3
            r74 = r4
            r67 = r5
            r55 = r6
            r69 = r8
            r57 = r11
            r8 = r51
            r3 = r77
            goto L_0x2416
        L_0x23ea:
            r62 = r3
            r74 = r4
            r67 = r5
            r69 = r8
            r8 = r51
            r3 = r77
            goto L_0x2416
        L_0x23f7:
            r0 = move-exception
            r3 = r77
            r74 = r4
            r2 = r0
            r5 = r18
            r1 = r31
            goto L_0x2471
        L_0x2403:
            r48 = r1
            r62 = r3
            r74 = r4
            r67 = r5
            r69 = r8
            r49 = r10
            r50 = r11
            r53 = r12
            r3 = r77
            r8 = r6
        L_0x2416:
            r1 = r48
        L_0x2418:
            if (r1 == 0) goto L_0x241f
            java.util.concurrent.CountDownLatch r2 = r3.countDownLatch     // Catch:{ all -> 0x2431 }
            r2.countDown()     // Catch:{ all -> 0x2431 }
        L_0x241f:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r31)     // Catch:{ all -> 0x2431 }
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r31)     // Catch:{ all -> 0x2431 }
            r2.resumeNetworkMaybe()     // Catch:{ all -> 0x2431 }
            r5 = r18
            r1 = r31
            r4 = r74
            goto L_0x24aa
        L_0x2431:
            r0 = move-exception
        L_0x2432:
            r2 = r0
            r5 = r18
            r1 = r31
            r4 = r74
            goto L_0x2471
        L_0x243a:
            r0 = move-exception
            r3 = r1
            r74 = r4
            r2 = r0
            r5 = r18
            r1 = r31
            goto L_0x2471
        L_0x2444:
            r0 = move-exception
            r3 = r1
            r74 = r4
            r2 = r0
            r1 = r17
            r5 = r18
            goto L_0x2471
        L_0x244e:
            r0 = move-exception
            r3 = r1
            r74 = r4
            r18 = r5
            r2 = r0
            r1 = r17
            goto L_0x2471
        L_0x2458:
            r0 = move-exception
            r3 = r1
            r18 = r5
            r2 = r0
            r1 = r17
            goto L_0x2471
        L_0x2460:
            r0 = move-exception
            r17 = r3
            r18 = r5
            r3 = r1
            r2 = r0
            r1 = r17
            goto L_0x2471
        L_0x246a:
            r0 = move-exception
            r17 = r3
            r3 = r1
            r2 = r0
            r1 = r17
        L_0x2471:
            r6 = -1
            if (r1 == r6) goto L_0x2484
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r1)
            org.telegram.tgnet.ConnectionsManager r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            r6.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r6 = r3.countDownLatch
            r6.countDown()
            goto L_0x2487
        L_0x2484:
            r77.onDecryptError()
        L_0x2487:
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r6 == 0) goto L_0x24a7
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "error in loc_key = "
            r6.append(r7)
            r6.append(r4)
            java.lang.String r7 = " json "
            r6.append(r7)
            r6.append(r5)
            java.lang.String r6 = r6.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r6)
        L_0x24a7:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x24aa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.GcmPushListenerService.m68xa7b3420a(java.util.Map, long):void");
    }

    static /* synthetic */ void lambda$onMessageReceived$1(int accountFinal) {
        if (UserConfig.getInstance(accountFinal).getClientUserId() != 0) {
            UserConfig.getInstance(accountFinal).clearConfig();
            MessagesController.getInstance(accountFinal).performLogout(0);
        }
    }

    private void onDecryptError() {
        for (int a = 0; a < 3; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                ConnectionsManager.onInternalPushReceived(a);
                ConnectionsManager.getInstance(a).resumeNetworkMaybe();
            }
        }
        this.countDownLatch.countDown();
    }

    public void onNewToken(String token) {
        AndroidUtilities.runOnUIThread(new GcmPushListenerService$$ExternalSyntheticLambda4(token));
    }

    static /* synthetic */ void lambda$onNewToken$5(String token) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Refreshed token: " + token);
        }
        ApplicationLoader.postInitApplication();
        sendRegistrationToServer(token);
    }

    public static void sendRegistrationToServer(String token) {
        Utilities.stageQueue.postRunnable(new GcmPushListenerService$$ExternalSyntheticLambda5(token));
    }

    static /* synthetic */ void lambda$sendRegistrationToServer$9(String token) {
        ConnectionsManager.setRegId(token, SharedConfig.pushStringStatus);
        if (token != null) {
            boolean sendStat = false;
            if (!(SharedConfig.pushStringGetTimeStart == 0 || SharedConfig.pushStringGetTimeEnd == 0 || (SharedConfig.pushStatSent && TextUtils.equals(SharedConfig.pushString, token)))) {
                sendStat = true;
                SharedConfig.pushStatSent = false;
            }
            SharedConfig.pushString = token;
            for (int a = 0; a < 3; a++) {
                UserConfig userConfig = UserConfig.getInstance(a);
                userConfig.registeredForPush = false;
                userConfig.saveConfig(false);
                if (userConfig.getClientUserId() != 0) {
                    int currentAccount = a;
                    if (sendStat) {
                        TLRPC.TL_help_saveAppLog req = new TLRPC.TL_help_saveAppLog();
                        TLRPC.TL_inputAppEvent event = new TLRPC.TL_inputAppEvent();
                        event.time = (double) SharedConfig.pushStringGetTimeStart;
                        event.type = "fcm_token_request";
                        event.peer = 0;
                        event.data = new TLRPC.TL_jsonNull();
                        req.events.add(event);
                        TLRPC.TL_inputAppEvent event2 = new TLRPC.TL_inputAppEvent();
                        event2.time = (double) SharedConfig.pushStringGetTimeEnd;
                        event2.type = "fcm_token_response";
                        event2.peer = SharedConfig.pushStringGetTimeEnd - SharedConfig.pushStringGetTimeStart;
                        event2.data = new TLRPC.TL_jsonNull();
                        req.events.add(event2);
                        sendStat = false;
                        ConnectionsManager.getInstance(currentAccount).sendRequest(req, GcmPushListenerService$$ExternalSyntheticLambda9.INSTANCE);
                    }
                    AndroidUtilities.runOnUIThread(new GcmPushListenerService$$ExternalSyntheticLambda2(currentAccount, token));
                }
            }
        }
    }

    static /* synthetic */ void lambda$sendRegistrationToServer$6(TLRPC.TL_error error) {
        if (error != null) {
            SharedConfig.pushStatSent = true;
            SharedConfig.saveConfig();
        }
    }
}
