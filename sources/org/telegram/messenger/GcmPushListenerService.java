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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v8, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v40, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v76, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v11, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v43, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v98, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v101, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v102, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v88, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v90, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v45, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v46, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v47, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v48, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v165, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v171, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v178, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v182, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v186, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v155, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v190, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v194, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v198, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v202, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v206, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v66, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v207, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v137, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v208, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v141, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v209, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v67, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v210, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v68, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v211, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v208, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v215, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v216, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v220, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v224, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v78, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v228, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v223, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v79, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v232, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v227, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v82, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v236, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v239, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v240, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v243, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v247, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v251, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v255, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v259, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v101, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v267, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v257, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v271, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v108, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v275, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v277, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v268, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v281, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v272, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v283, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v275, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v285, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v278, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v287, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v281, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v289, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v284, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v291, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v287, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v293, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v290, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v295, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v293, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v134, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v297, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v296, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v299, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v299, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v138, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v301, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v302, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v303, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v305, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v141, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v305, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v308, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v143, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v307, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v311, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v309, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v314, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v148, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v149, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v311, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v300, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v316, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v317, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v318, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v319, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v304, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v321, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v322, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v323, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v327, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v308, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v326, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v327, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v274, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v333, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v312, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v333, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v334, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v276, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v339, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v341, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v341, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v345, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v153, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v154, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v343, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v348, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v155, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v345, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v351, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v158, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v347, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v349, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v358, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v351, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v361, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v353, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v299, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v355, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v368, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v166, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v167, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v357, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v371, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v169, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v359, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v307, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v170, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v361, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v378, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v172, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v363, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v381, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v173, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v174, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v365, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v315, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v175, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v367, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v388, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v177, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v369, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v391, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v178, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v371, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v323, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v373, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v398, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v181, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v375, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v401, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v377, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v331, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v380, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v383, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v408, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v382, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v411, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v385, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v392, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v414, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v190, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v387, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v342, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v390, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v401, resolved type: android.text.PrecomputedText} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v420, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v392, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v347, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v196, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v394, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v427, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v198, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v396, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v430, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v200, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v398, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v434, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v201, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v400, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v437, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v402, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v440, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v206, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v207, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v404, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v444, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v208, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v209, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v406, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v447, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v210, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v212, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v408, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v450, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v213, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v214, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v410, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v454, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v216, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v412, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v457, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v217, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v414, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v460, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v221, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v416, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v382, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v418, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v467, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v223, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v420, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v470, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v226, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v422, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v390, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v227, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v424, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v477, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v228, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v229, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v426, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v480, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v428, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v398, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v232, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v430, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v487, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v432, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v490, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v434, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v406, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v436, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v497, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v438, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v500, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v440, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v414, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v442, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v507, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v244, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v444, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v510, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v446, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v422, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v448, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v517, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v450, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v520, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v452, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v456, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v530, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v9, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v11, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v52, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v56, resolved type: java.lang.Long} */
    /* JADX WARNING: type inference failed for: r4v461 */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:1000:0x2303, code lost:
        r9.mentioned = r1;
        r1 = r57;
        r9.silent = r1;
        r2 = r47;
        r9.from_scheduled = r2;
        r25 = new org.telegram.messenger.MessageObject(r31, r9, r4, r8, r70, r6, r69, r73, r39);
        r57 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1001:0x232c, code lost:
        if (r5.startsWith(r62) != false) goto L_0x2339;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1005:0x2334, code lost:
        if (r5.startsWith(r16) == false) goto L_0x2337;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1006:0x2337, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1007:0x2339, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1008:0x233a, code lost:
        r47 = r2;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1010:?, code lost:
        r2.isReactionPush = r1;
        r1 = new java.util.ArrayList<>();
        r1.add(r2);
        r17 = r2;
        r25 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1011:0x2354, code lost:
        r26 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1013:?, code lost:
        org.telegram.messenger.NotificationsController.getInstance(r31).processNewMessages(r1, true, true, r3.countDownLatch);
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1014:0x235e, code lost:
        r25 = r4;
        r26 = r5;
        r15 = r13;
        r12 = r58;
        r10 = r65;
        r58 = r49;
        r49 = r51;
        r51 = r54;
        r54 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1015:0x2370, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1016:0x2371, code lost:
        r26 = r5;
        r1 = r0;
        r6 = r19;
        r4 = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1017:0x237a, code lost:
        r3 = r77;
        r54 = r1;
        r26 = r5;
        r68 = r6;
        r56 = r8;
        r71 = r9;
        r64 = r30;
        r58 = r49;
        r49 = r51;
        r51 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1018:0x238f, code lost:
        r3 = r77;
        r54 = r1;
        r53 = r4;
        r26 = r5;
        r68 = r6;
        r71 = r9;
        r64 = r30;
        r58 = r49;
        r49 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1019:0x23a2, code lost:
        r3 = r77;
        r54 = r1;
        r53 = r4;
        r26 = r5;
        r68 = r6;
        r58 = r7;
        r71 = r9;
        r48 = true;
        r49 = r13;
        r64 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1020:0x23b6, code lost:
        r11 = r48;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1021:0x23b8, code lost:
        if (r11 == false) goto L_0x23bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1022:0x23ba, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1023:0x23bf, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r31);
        org.telegram.tgnet.ConnectionsManager.getInstance(r31).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1024:0x23c9, code lost:
        r6 = r19;
        r5 = r26;
        r4 = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1025:0x23d1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1026:0x23d2, code lost:
        r1 = r0;
        r6 = r19;
        r5 = r26;
        r4 = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1027:0x23da, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1028:0x23db, code lost:
        r3 = r77;
        r26 = r5;
        r1 = r0;
        r6 = r19;
        r4 = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1058:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x0243, code lost:
        switch(r2) {
            case 0: goto L_0x02b4;
            case 1: goto L_0x026c;
            case 2: goto L_0x025e;
            case 3: goto L_0x024e;
            default: goto L_0x0246;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:0x0246, code lost:
        r40 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1(r4));
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x025d, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x025e, code lost:
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0(r4));
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x026b, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x026c, code lost:
        r2 = new org.telegram.tgnet.TLRPC.TL_updateServiceNotification();
        r2.popup = false;
        r2.flags = 2;
        r40 = r7;
        r2.inbox_date = (int) (r79 / 1000);
        r2.message = r12.getString("message");
        r2.type = "announcement";
        r2.media = new org.telegram.tgnet.TLRPC.TL_messageMediaEmpty();
        r3 = new org.telegram.tgnet.TLRPC.TL_updates();
        r3.updates.add(r2);
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3(r4, r3));
        org.telegram.tgnet.ConnectionsManager.getInstance(r31).resumeNetworkMaybe();
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x02b3, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:157:0x02b4, code lost:
        r40 = r7;
        r2 = r6.getInt("dc");
        r3 = r6.getString("addr");
        r7 = r3.split(":");
        r16 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x02cc, code lost:
        if (r7.length == 2) goto L_0x02d4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:159:0x02ce, code lost:
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x02d3, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:161:0x02d4, code lost:
        r17 = r7;
        org.telegram.tgnet.ConnectionsManager.getInstance(r31).applyDatacenterAddress(r2, r7[0], java.lang.Integer.parseInt(r7[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r31).resumeNetworkMaybe();
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x02f3, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:165:0x02f8, code lost:
        if (r6.has("channel_id") == 0) goto L_0x0310;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:169:0x0300, code lost:
        r27 = r9;
        r2 = r10;
        r7 = r6.getLong("channel_id");
        r75 = r7;
        r7 = -r7;
        r9 = r75;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x0310, code lost:
        r27 = r9;
        r2 = r10;
        r7 = 0;
        r9 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x031d, code lost:
        r42 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x0325, code lost:
        if (r6.has("from_id") == false) goto L_0x0334;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x032d, code lost:
        r7 = r6.getLong("from_id");
        r32 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:179:0x0334, code lost:
        r75 = r7;
        r7 = 0;
        r32 = r75;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:183:0x0342, code lost:
        if (r6.has("chat_id") == false) goto L_0x035e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x034a, code lost:
        r44 = r13;
        r43 = r14;
        r13 = r6.getLong("chat_id");
        r1 = -r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x0354, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x0355, code lost:
        r3 = r77;
        r1 = r0;
        r6 = r19;
        r4 = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x035e, code lost:
        r44 = r13;
        r43 = r14;
        r1 = r32;
        r13 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x036a, code lost:
        r32 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x0372, code lost:
        if (r6.has("encryption_id") == false) goto L_0x0380;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:?, code lost:
        r1 = org.telegram.messenger.DialogObject.makeEncryptedDialogId((long) r6.getInt("encryption_id"));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x0380, code lost:
        r1 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x0382, code lost:
        r45 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x038a, code lost:
        if (r6.has("schedule") == false) goto L_0x039b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:?, code lost:
        r46 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x0395, code lost:
        if (r6.getInt("schedule") != 1) goto L_0x0399;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x0397, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x0399, code lost:
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x039b, code lost:
        r46 = r15;
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x039e, code lost:
        r47 = r11;
        r15 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x03a5, code lost:
        if (r1 != 0) goto L_0x03b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x03ad, code lost:
        if ("ENCRYPTED_MESSAGE".equals(r5) == false) goto L_0x03b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x03b1, code lost:
        r1 = org.telegram.messenger.NotificationsController.globalSecretChatId;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:218:0x03b7, code lost:
        if (r1 == 0) goto L_0x23a2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x03bf, code lost:
        r48 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x03c3, code lost:
        if ("READ_HISTORY".equals(r5) == false) goto L_0x0457;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:?, code lost:
        r3 = r6.getInt("max_id");
        r12 = new java.util.ArrayList<>();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x03d2, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x03f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x03d4, code lost:
        r30 = r15;
        r15 = new java.lang.StringBuilder();
        r49 = r6;
        r15.append("GCM received read notification max_id = ");
        r15.append(r3);
        r15.append(" for dialogId = ");
        r15.append(r1);
        org.telegram.messenger.FileLog.d(r15.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x03f3, code lost:
        r49 = r6;
        r30 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x03fb, code lost:
        if (r9 == 0) goto L_0x040b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x03fd, code lost:
        r6 = new org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox();
        r6.channel_id = r9;
        r6.max_id = r3;
        r12.add(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x040b, code lost:
        r6 = new org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x0414, code lost:
        if (r7 == 0) goto L_0x0422;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:0x0416, code lost:
        r6.peer = new org.telegram.tgnet.TLRPC.TL_peerUser();
        r6.peer.user_id = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x0422, code lost:
        r6.peer = new org.telegram.tgnet.TLRPC.TL_peerChat();
        r6.peer.chat_id = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x042d, code lost:
        r6.max_id = r3;
        r12.add(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x0432, code lost:
        org.telegram.messenger.MessagesController.getInstance(r4).processUpdateArray(r12, (java.util.ArrayList<org.telegram.tgnet.TLRPC.User>) null, (java.util.ArrayList<org.telegram.tgnet.TLRPC.Chat>) null, false, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x0443, code lost:
        r3 = r77;
        r54 = r1;
        r53 = r4;
        r26 = r5;
        r58 = r7;
        r71 = r9;
        r64 = r30;
        r68 = r49;
        r49 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x0457, code lost:
        r49 = r6;
        r30 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x0463, code lost:
        if ("MESSAGE_DELETED".equals(r5) == false) goto L_0x04f7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x0465, code lost:
        r6 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:?, code lost:
        r3 = r6.getString("messages");
        r12 = r3.split(",");
        r15 = new androidx.collection.LongSparseArray<>();
        r17 = new java.util.ArrayList<>();
        r25 = r3;
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x0483, code lost:
        r49 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x0486, code lost:
        if (r3 >= r12.length) goto L_0x049a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x0488, code lost:
        r8 = r17;
        r8.add(org.telegram.messenger.Utilities.parseInt(r12[r3]));
        r3 = r3 + 1;
        r17 = r8;
        r7 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x049a, code lost:
        r8 = r17;
        r3 = r12;
        r51 = r13;
        r15.put(-r9, r8);
        org.telegram.messenger.NotificationsController.getInstance(r31).removeDeletedMessagesFromNotifications(r15);
        org.telegram.messenger.MessagesController.getInstance(r31).deleteMessagesByPush(r1, r8, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x04b9, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x04e3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x04bb, code lost:
        org.telegram.messenger.FileLog.d("GCM received " + r5 + " for dialogId = " + r1 + " mids = " + android.text.TextUtils.join(",", r8));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x04e3, code lost:
        r3 = r77;
        r54 = r1;
        r53 = r4;
        r26 = r5;
        r68 = r6;
        r71 = r9;
        r64 = r30;
        r58 = r49;
        r49 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x04f7, code lost:
        r51 = r13;
        r6 = r49;
        r49 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x0501, code lost:
        if (android.text.TextUtils.isEmpty(r5) != false) goto L_0x238f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x0509, code lost:
        if (r6.has("msg_id") == false) goto L_0x0512;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:?, code lost:
        r7 = r6.getInt("msg_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x0512, code lost:
        r7 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x0519, code lost:
        if (r6.has("random_id") == false) goto L_0x052a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:?, code lost:
        r13 = org.telegram.messenger.Utilities.parseLong(r6.getString("random_id")).longValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x052a, code lost:
        r13 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x052d, code lost:
        if (r7 == 0) goto L_0x0572;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:275:0x052f, code lost:
        r8 = org.telegram.messenger.MessagesController.getInstance(r31).dialogs_read_inbox_max.get(java.lang.Long.valueOf(r1));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0541, code lost:
        if (r8 != null) goto L_0x0562;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:277:0x0543, code lost:
        r35 = r8;
        r8 = java.lang.Integer.valueOf(org.telegram.messenger.MessagesStorage.getInstance(r31).getDialogReadMax(false, r1));
        r53 = r4;
        org.telegram.messenger.MessagesController.getInstance(r4).dialogs_read_inbox_max.put(java.lang.Long.valueOf(r1), r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0562, code lost:
        r53 = r4;
        r35 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x056a, code lost:
        if (r7 <= r8.intValue()) goto L_0x056f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x056c, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x056f, code lost:
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x0572, code lost:
        r53 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x057a, code lost:
        if (r13 == 0) goto L_0x0588;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x0584, code lost:
        if (org.telegram.messenger.MessagesStorage.getInstance(r23).checkMessageByRandomId(r13) != false) goto L_0x0588;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x0586, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x0588, code lost:
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x0590, code lost:
        if (r5.startsWith("REACT_") != false) goto L_0x0598;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x0596, code lost:
        if (r5.startsWith("CHAT_REACT_") == false) goto L_0x0599;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x0598, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x0599, code lost:
        if (r8 == false) goto L_0x237a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x059b, code lost:
        r54 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:?, code lost:
        r56 = r6.optLong("chat_from_id", 0);
        r58 = r6.optLong("chat_from_broadcast_id", 0);
        r60 = r6.optLong("chat_from_group_id", 0);
        r62 = "REACT_";
        r3 = r56;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x05bd, code lost:
        if (r3 != 0) goto L_0x05c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x05c1, code lost:
        if (r60 == 0) goto L_0x05c4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x05c4, code lost:
        r13 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x05c6, code lost:
        r13 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x05cd, code lost:
        if (r6.has("mention") == false) goto L_0x05d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x05d5, code lost:
        if (r6.getInt("mention") == 0) goto L_0x05d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x05d7, code lost:
        r14 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x05d9, code lost:
        r14 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x05da, code lost:
        r56 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x05e2, code lost:
        if (r6.has("silent") == false) goto L_0x05ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x05ea, code lost:
        if (r6.getInt("silent") == 0) goto L_0x05ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x05ec, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x05ee, code lost:
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x05ef, code lost:
        r57 = r8;
        r63 = r14;
        r14 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x05fb, code lost:
        if (r14.has("loc_args") == false) goto L_0x061e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:?, code lost:
        r8 = r14.getJSONArray("loc_args");
        r64 = r14;
        r14 = new java.lang.String[r8.length()];
        r65 = r3;
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x0612, code lost:
        if (r3 >= r14.length) goto L_0x0624;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x0614, code lost:
        r14[r3] = r8.getString(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x061a, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x061e, code lost:
        r65 = r3;
        r64 = r14;
        r14 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:?, code lost:
        r8 = r14[0];
        r39 = r6.has("edit_date");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x0643, code lost:
        if (r5.startsWith("CHAT_") == false) goto L_0x068f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x0649, code lost:
        if (org.telegram.messenger.UserObject.isReplyUser(r1) == false) goto L_0x0671;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x064b, code lost:
        r3 = new java.lang.StringBuilder();
        r3.append(r8);
        r67 = null;
        r3.append(" @ ");
        r68 = r6;
        r3.append(r14[1]);
        r8 = r3.toString();
        r3 = null;
        r4 = false;
        r6 = false;
        r69 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x0671, code lost:
        r67 = null;
        r68 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x0679, code lost:
        if (r9 == 0) goto L_0x067d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x067b, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x067d, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x067e, code lost:
        r35 = r3;
        r30 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:351:0x0685, code lost:
        r8 = r14[1];
        r3 = r30;
        r4 = r35;
        r6 = false;
        r69 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x068f, code lost:
        r67 = null;
        r68 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x0699, code lost:
        if (r5.startsWith("PINNED_") == false) goto L_0x06b1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x069f, code lost:
        if (r9 == 0) goto L_0x06a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x06a1, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x06a3, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x06a4, code lost:
        r35 = r3;
        r3 = null;
        r4 = r35;
        r6 = true;
        r69 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x06b7, code lost:
        if (r5.startsWith("CHANNEL_") == false) goto L_0x06c4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x06b9, code lost:
        r3 = null;
        r4 = false;
        r6 = false;
        r69 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x06c4, code lost:
        r3 = null;
        r4 = false;
        r6 = false;
        r69 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x06ce, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x06f7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x06d0, code lost:
        r30 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:?, code lost:
        r8 = new java.lang.StringBuilder();
        r70 = r3;
        r8.append("GCM received message notification ");
        r8.append(r5);
        r8.append(" for dialogId = ");
        r8.append(r1);
        r8.append(" mid = ");
        r8.append(r7);
        org.telegram.messenger.FileLog.d(r8.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x06f7, code lost:
        r70 = r3;
        r30 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x06fb, code lost:
        r3 = r62;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x0701, code lost:
        if (r5.startsWith(r3) != false) goto L_0x2203;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x0707, code lost:
        if (r5.startsWith("CHAT_REACT_") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x0709, code lost:
        r62 = r3;
        r73 = r4;
        r74 = r6;
        r71 = r9;
        r16 = "CHAT_REACT_";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x071a, code lost:
        switch(r5.hashCode()) {
            case -2100047043: goto L_0x0c5b;
            case -2091498420: goto L_0x0CLASSNAME;
            case -2053872415: goto L_0x0CLASSNAME;
            case -2039746363: goto L_0x0c3a;
            case -2023218804: goto L_0x0c2f;
            case -1979538588: goto L_0x0CLASSNAME;
            case -1979536003: goto L_0x0CLASSNAME;
            case -1979535888: goto L_0x0c0e;
            case -1969004705: goto L_0x0CLASSNAME;
            case -1946699248: goto L_0x0bf7;
            case -1717283471: goto L_0x0beb;
            case -1646640058: goto L_0x0bdf;
            case -1528047021: goto L_0x0bd3;
            case -1493579426: goto L_0x0bc7;
            case -1482481933: goto L_0x0bbb;
            case -1480102982: goto L_0x0bb0;
            case -1478041834: goto L_0x0ba4;
            case -1474543101: goto L_0x0b99;
            case -1465695932: goto L_0x0b8d;
            case -1374906292: goto L_0x0b81;
            case -1372940586: goto L_0x0b75;
            case -1264245338: goto L_0x0b69;
            case -1236154001: goto L_0x0b5d;
            case -1236086700: goto L_0x0b51;
            case -1236077786: goto L_0x0b45;
            case -1235796237: goto L_0x0b39;
            case -1235760759: goto L_0x0b2d;
            case -1235686303: goto L_0x0b22;
            case -1198046100: goto L_0x0b17;
            case -1124254527: goto L_0x0b0b;
            case -1085137927: goto L_0x0aff;
            case -1084856378: goto L_0x0af3;
            case -1084820900: goto L_0x0ae7;
            case -1084746444: goto L_0x0adb;
            case -819729482: goto L_0x0acf;
            case -772141857: goto L_0x0ac3;
            case -638310039: goto L_0x0ab7;
            case -590403924: goto L_0x0aab;
            case -589196239: goto L_0x0a9f;
            case -589193654: goto L_0x0a93;
            case -589193539: goto L_0x0a87;
            case -440169325: goto L_0x0a7b;
            case -412748110: goto L_0x0a6f;
            case -228518075: goto L_0x0a63;
            case -213586509: goto L_0x0a57;
            case -115582002: goto L_0x0a4b;
            case -112621464: goto L_0x0a3f;
            case -108522133: goto L_0x0a33;
            case -107572034: goto L_0x0a28;
            case -40534265: goto L_0x0a1c;
            case 52369421: goto L_0x0a10;
            case 65254746: goto L_0x0a04;
            case 141040782: goto L_0x09f8;
            case 202550149: goto L_0x09ec;
            case 309993049: goto L_0x09e0;
            case 309995634: goto L_0x09d4;
            case 309995749: goto L_0x09c8;
            case 320532812: goto L_0x09bc;
            case 328933854: goto L_0x09b0;
            case 331340546: goto L_0x09a4;
            case 342406591: goto L_0x0998;
            case 344816990: goto L_0x098c;
            case 346878138: goto L_0x0980;
            case 350376871: goto L_0x0974;
            case 608430149: goto L_0x0968;
            case 615714517: goto L_0x095d;
            case 715508879: goto L_0x0951;
            case 728985323: goto L_0x0945;
            case 731046471: goto L_0x0939;
            case 734545204: goto L_0x092d;
            case 802032552: goto L_0x0921;
            case 991498806: goto L_0x0915;
            case 1007364121: goto L_0x0909;
            case 1019850010: goto L_0x08fd;
            case 1019917311: goto L_0x08f1;
            case 1019926225: goto L_0x08e5;
            case 1020207774: goto L_0x08d9;
            case 1020243252: goto L_0x08cd;
            case 1020317708: goto L_0x08c1;
            case 1060282259: goto L_0x08b5;
            case 1060349560: goto L_0x08a9;
            case 1060358474: goto L_0x089d;
            case 1060640023: goto L_0x0891;
            case 1060675501: goto L_0x0885;
            case 1060749957: goto L_0x087a;
            case 1073049781: goto L_0x086e;
            case 1078101399: goto L_0x0862;
            case 1110103437: goto L_0x0856;
            case 1160762272: goto L_0x084a;
            case 1172918249: goto L_0x083e;
            case 1234591620: goto L_0x0832;
            case 1281128640: goto L_0x0826;
            case 1281131225: goto L_0x081a;
            case 1281131340: goto L_0x080e;
            case 1310789062: goto L_0x0803;
            case 1333118583: goto L_0x07f7;
            case 1361447897: goto L_0x07eb;
            case 1498266155: goto L_0x07df;
            case 1533804208: goto L_0x07d3;
            case 1540131626: goto L_0x07c7;
            case 1547988151: goto L_0x07bb;
            case 1561464595: goto L_0x07af;
            case 1563525743: goto L_0x07a3;
            case 1567024476: goto L_0x0797;
            case 1810705077: goto L_0x078b;
            case 1815177512: goto L_0x077f;
            case 1954774321: goto L_0x0773;
            case 1963241394: goto L_0x0767;
            case 2014789757: goto L_0x075b;
            case 2022049433: goto L_0x074f;
            case 2034984710: goto L_0x0743;
            case 2048733346: goto L_0x0737;
            case 2099392181: goto L_0x072b;
            case 2140162142: goto L_0x071f;
            default: goto L_0x071d;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x0725, code lost:
        if (r5.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x0727, code lost:
        r8 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x0731, code lost:
        if (r5.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x0733, code lost:
        r8 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x073d, code lost:
        if (r5.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x073f, code lost:
        r8 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x0749, code lost:
        if (r5.equals("CHANNEL_MESSAGE_PLAYLIST") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x074b, code lost:
        r8 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x0755, code lost:
        if (r5.equals("PINNED_CONTACT") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x0757, code lost:
        r8 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x0761, code lost:
        if (r5.equals("CHAT_PHOTO_EDITED") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x0763, code lost:
        r8 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x076d, code lost:
        if (r5.equals("LOCKED_MESSAGE") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x076f, code lost:
        r8 = 'l';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x0779, code lost:
        if (r5.equals("CHAT_MESSAGE_PLAYLIST") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x077b, code lost:
        r8 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x0785, code lost:
        if (r5.equals("CHANNEL_MESSAGES") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x0787, code lost:
        r8 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x0791, code lost:
        if (r5.equals("MESSAGE_INVOICE") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x0793, code lost:
        r8 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x079d, code lost:
        if (r5.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x079f, code lost:
        r8 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x07a9, code lost:
        if (r5.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x07ab, code lost:
        r8 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x07b5, code lost:
        if (r5.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x07b7, code lost:
        r8 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x07c1, code lost:
        if (r5.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x07c3, code lost:
        r8 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x07cd, code lost:
        if (r5.equals("MESSAGE_PLAYLIST") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:426:0x07cf, code lost:
        r8 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x07d9, code lost:
        if (r5.equals("MESSAGE_VIDEOS") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x07db, code lost:
        r8 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x07e5, code lost:
        if (r5.equals("PHONE_CALL_MISSED") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:432:0x07e7, code lost:
        r8 = 'q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x07f1, code lost:
        if (r5.equals("MESSAGE_PHOTOS") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x07f3, code lost:
        r8 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x07fd, code lost:
        if (r5.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x07ff, code lost:
        r8 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x0809, code lost:
        if (r5.equals("MESSAGE_NOTEXT") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x080b, code lost:
        r8 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x0814, code lost:
        if (r5.equals("MESSAGE_GIF") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:444:0x0816, code lost:
        r8 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x0820, code lost:
        if (r5.equals("MESSAGE_GEO") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x0822, code lost:
        r8 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x082c, code lost:
        if (r5.equals("MESSAGE_DOC") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x082e, code lost:
        r8 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x0838, code lost:
        if (r5.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x083a, code lost:
        r8 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x0844, code lost:
        if (r5.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:456:0x0846, code lost:
        r8 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:458:0x0850, code lost:
        if (r5.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x0852, code lost:
        r8 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x085c, code lost:
        if (r5.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x085e, code lost:
        r8 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x0868, code lost:
        if (r5.equals("CHAT_TITLE_EDITED") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x086a, code lost:
        r8 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x0874, code lost:
        if (r5.equals("PINNED_NOTEXT") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x0876, code lost:
        r8 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x0880, code lost:
        if (r5.equals("MESSAGE_TEXT") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x0882, code lost:
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x088b, code lost:
        if (r5.equals("MESSAGE_QUIZ") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x088d, code lost:
        r8 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x0897, code lost:
        if (r5.equals("MESSAGE_POLL") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x0899, code lost:
        r8 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x08a3, code lost:
        if (r5.equals("MESSAGE_GAME") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:480:0x08a5, code lost:
        r8 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x08af, code lost:
        if (r5.equals("MESSAGE_FWDS") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x08b1, code lost:
        r8 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x08bb, code lost:
        if (r5.equals("MESSAGE_DOCS") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:486:0x08bd, code lost:
        r8 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x08c7, code lost:
        if (r5.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x08c9, code lost:
        r8 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x08d3, code lost:
        if (r5.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x08d5, code lost:
        r8 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x08df, code lost:
        if (r5.equals("CHAT_MESSAGE_POLL") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x08e1, code lost:
        r8 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x08eb, code lost:
        if (r5.equals("CHAT_MESSAGE_GAME") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x08ed, code lost:
        r8 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x08f7, code lost:
        if (r5.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x08f9, code lost:
        r8 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x0903, code lost:
        if (r5.equals("CHAT_MESSAGE_DOCS") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x0905, code lost:
        r8 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x090f, code lost:
        if (r5.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x0911, code lost:
        r8 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x091b, code lost:
        if (r5.equals("PINNED_GEOLIVE") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x091d, code lost:
        r8 = 'b';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x0927, code lost:
        if (r5.equals("MESSAGE_CONTACT") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x0929, code lost:
        r8 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x0933, code lost:
        if (r5.equals("PINNED_VIDEO") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x0935, code lost:
        r8 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x093f, code lost:
        if (r5.equals("PINNED_ROUND") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:519:0x0941, code lost:
        r8 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x094b, code lost:
        if (r5.equals("PINNED_PHOTO") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x094d, code lost:
        r8 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0957, code lost:
        if (r5.equals("PINNED_AUDIO") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x0959, code lost:
        r8 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0963, code lost:
        if (r5.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x0965, code lost:
        r8 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x096e, code lost:
        if (r5.equals("CHAT_VOICECHAT_INVITE_YOU") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0970, code lost:
        r8 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x097a, code lost:
        if (r5.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x097c, code lost:
        r8 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x0986, code lost:
        if (r5.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x0988, code lost:
        r8 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x0992, code lost:
        if (r5.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:540:0x0994, code lost:
        r8 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x099e, code lost:
        if (r5.equals("CHAT_VOICECHAT_END") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x09a0, code lost:
        r8 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x09aa, code lost:
        if (r5.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x09ac, code lost:
        r8 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x09b6, code lost:
        if (r5.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x09b8, code lost:
        r8 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x09c2, code lost:
        if (r5.equals("MESSAGES") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:552:0x09c4, code lost:
        r8 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x09ce, code lost:
        if (r5.equals("CHAT_MESSAGE_GIF") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x09d0, code lost:
        r8 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x09da, code lost:
        if (r5.equals("CHAT_MESSAGE_GEO") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x09dc, code lost:
        r8 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x09e6, code lost:
        if (r5.equals("CHAT_MESSAGE_DOC") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x09e8, code lost:
        r8 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x09f2, code lost:
        if (r5.equals("CHAT_VOICECHAT_INVITE") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x09f4, code lost:
        r8 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x09fe, code lost:
        if (r5.equals("CHAT_LEFT") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x0a00, code lost:
        r8 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x0a0a, code lost:
        if (r5.equals("CHAT_ADD_YOU") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x0a0c, code lost:
        r8 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x0a16, code lost:
        if (r5.equals("REACT_TEXT") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x0a18, code lost:
        r8 = 'h';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x0a22, code lost:
        if (r5.equals("CHAT_DELETE_MEMBER") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:576:0x0a24, code lost:
        r8 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x0a2e, code lost:
        if (r5.equals("MESSAGE_SCREENSHOT") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x0a30, code lost:
        r8 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x0a39, code lost:
        if (r5.equals("AUTH_REGION") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:582:0x0a3b, code lost:
        r8 = 'k';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x0a45, code lost:
        if (r5.equals("CONTACT_JOINED") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x0a47, code lost:
        r8 = 'i';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x0a51, code lost:
        if (r5.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:588:0x0a53, code lost:
        r8 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x0a5d, code lost:
        if (r5.equals("ENCRYPTION_REQUEST") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x0a5f, code lost:
        r8 = 'm';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0a69, code lost:
        if (r5.equals("MESSAGE_GEOLIVE") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x0a6b, code lost:
        r8 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x0a75, code lost:
        if (r5.equals("CHAT_DELETE_YOU") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0a77, code lost:
        r8 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0a81, code lost:
        if (r5.equals("AUTH_UNKNOWN") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:600:0x0a83, code lost:
        r8 = 'j';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0a8d, code lost:
        if (r5.equals("PINNED_GIF") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0a8f, code lost:
        r8 = 'f';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0a99, code lost:
        if (r5.equals("PINNED_GEO") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x0a9b, code lost:
        r8 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x0aa5, code lost:
        if (r5.equals("PINNED_DOC") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x0aa7, code lost:
        r8 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x0ab1, code lost:
        if (r5.equals("PINNED_GAME_SCORE") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:612:0x0ab3, code lost:
        r8 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x0abd, code lost:
        if (r5.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x0abf, code lost:
        r8 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x0ac9, code lost:
        if (r5.equals("PHONE_CALL_REQUEST") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:618:0x0acb, code lost:
        r8 = 'o';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0ad5, code lost:
        if (r5.equals("PINNED_STICKER") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x0ad7, code lost:
        r8 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x0ae1, code lost:
        if (r5.equals("PINNED_TEXT") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x0ae3, code lost:
        r8 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x0aed, code lost:
        if (r5.equals("PINNED_QUIZ") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:627:0x0aef, code lost:
        r8 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x0af9, code lost:
        if (r5.equals("PINNED_POLL") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x0afb, code lost:
        r8 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x0b05, code lost:
        if (r5.equals("PINNED_GAME") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x0b07, code lost:
        r8 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x0b11, code lost:
        if (r5.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x0b13, code lost:
        r8 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x0b1d, code lost:
        if (r5.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x0b1f, code lost:
        r8 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x0b28, code lost:
        if (r5.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0b2a, code lost:
        r8 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0b33, code lost:
        if (r5.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x0b35, code lost:
        r8 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x0b3f, code lost:
        if (r5.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x0b41, code lost:
        r8 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0b4b, code lost:
        if (r5.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x0b4d, code lost:
        r8 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0b57, code lost:
        if (r5.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0b59, code lost:
        r8 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0b63, code lost:
        if (r5.equals("CHANNEL_MESSAGE_DOCS") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0b65, code lost:
        r8 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0b6f, code lost:
        if (r5.equals("PINNED_INVOICE") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0b71, code lost:
        r8 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0b7b, code lost:
        if (r5.equals("CHAT_RETURNED") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x0b7d, code lost:
        r8 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x0b87, code lost:
        if (r5.equals("ENCRYPTED_MESSAGE") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0b89, code lost:
        r8 = 'g';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0b93, code lost:
        if (r5.equals("ENCRYPTION_ACCEPT") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0b95, code lost:
        r8 = 'n';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:671:0x0b9f, code lost:
        if (r5.equals("MESSAGE_VIDEO") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0ba1, code lost:
        r8 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0baa, code lost:
        if (r5.equals("MESSAGE_ROUND") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0bac, code lost:
        r8 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x0bb6, code lost:
        if (r5.equals("MESSAGE_PHOTO") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0bb8, code lost:
        r8 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0bc1, code lost:
        if (r5.equals("MESSAGE_MUTED") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0bc3, code lost:
        r8 = 'p';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0bcd, code lost:
        if (r5.equals("MESSAGE_AUDIO") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0bcf, code lost:
        r8 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0bd9, code lost:
        if (r5.equals("CHAT_MESSAGES") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x0bdb, code lost:
        r8 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0be5, code lost:
        if (r5.equals("CHAT_VOICECHAT_START") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0be7, code lost:
        r8 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0bf1, code lost:
        if (r5.equals("CHAT_REQ_JOINED") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0bf3, code lost:
        r8 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0bfd, code lost:
        if (r5.equals("CHAT_JOINED") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0bff, code lost:
        r8 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0CLASSNAME, code lost:
        if (r5.equals("CHAT_ADD_MEMBER") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0c0b, code lost:
        r8 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0CLASSNAME, code lost:
        if (r5.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0CLASSNAME, code lost:
        r8 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0c1f, code lost:
        if (r5.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0CLASSNAME, code lost:
        r8 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0c2a, code lost:
        if (r5.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0c2c, code lost:
        r8 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0CLASSNAME, code lost:
        if (r5.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0CLASSNAME, code lost:
        r8 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0CLASSNAME, code lost:
        if (r5.equals("MESSAGE_STICKER") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0CLASSNAME, code lost:
        r8 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0c4b, code lost:
        if (r5.equals("CHAT_CREATED") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0c4d, code lost:
        r8 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0CLASSNAME, code lost:
        if (r5.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0CLASSNAME, code lost:
        r8 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0CLASSNAME, code lost:
        if (r5.equals("MESSAGE_GAME_SCORE") == false) goto L_0x071d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0CLASSNAME, code lost:
        r8 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0CLASSNAME, code lost:
        r8 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0CLASSNAME, code lost:
        r16 = "CHAT_REACT_";
        r62 = r3;
        r71 = r9;
        r73 = r4;
        r74 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0c7f, code lost:
        switch(r8) {
            case 0: goto L_0x21c2;
            case 1: goto L_0x21c2;
            case 2: goto L_0x219d;
            case 3: goto L_0x2177;
            case 4: goto L_0x2151;
            case 5: goto L_0x212b;
            case 6: goto L_0x2105;
            case 7: goto L_0x20e9;
            case 8: goto L_0x20c3;
            case 9: goto L_0x209d;
            case 10: goto L_0x2030;
            case 11: goto L_0x200a;
            case 12: goto L_0x1fdf;
            case 13: goto L_0x1fb4;
            case 14: goto L_0x1var_;
            case 15: goto L_0x1var_;
            case 16: goto L_0x1f3d;
            case 17: goto L_0x1var_;
            case 18: goto L_0x1eec;
            case 19: goto L_0x1ec7;
            case 20: goto L_0x1ec7;
            case 21: goto L_0x1e9c;
            case 22: goto L_0x1e6d;
            case 23: goto L_0x1e40;
            case 24: goto L_0x1e13;
            case 25: goto L_0x1de4;
            case 26: goto L_0x1db5;
            case 27: goto L_0x1d98;
            case 28: goto L_0x1d72;
            case 29: goto L_0x1d4c;
            case 30: goto L_0x1d26;
            case 31: goto L_0x1d00;
            case 32: goto L_0x1cda;
            case 33: goto L_0x1c6d;
            case 34: goto L_0x1CLASSNAME;
            case 35: goto L_0x1c1c;
            case 36: goto L_0x1bf1;
            case 37: goto L_0x1bc6;
            case 38: goto L_0x1ba0;
            case 39: goto L_0x1b7a;
            case 40: goto L_0x1b54;
            case 41: goto L_0x1b2e;
            case 42: goto L_0x1afb;
            case 43: goto L_0x1ace;
            case 44: goto L_0x1aa1;
            case 45: goto L_0x1a72;
            case 46: goto L_0x1a43;
            case 47: goto L_0x1a26;
            case 48: goto L_0x19fd;
            case 49: goto L_0x19d2;
            case 50: goto L_0x19a7;
            case 51: goto L_0x197c;
            case 52: goto L_0x1951;
            case 53: goto L_0x1926;
            case 54: goto L_0x189b;
            case 55: goto L_0x1870;
            case 56: goto L_0x1840;
            case 57: goto L_0x1810;
            case 58: goto L_0x17e0;
            case 59: goto L_0x17b5;
            case 60: goto L_0x178a;
            case 61: goto L_0x175f;
            case 62: goto L_0x172f;
            case 63: goto L_0x1705;
            case 64: goto L_0x16d5;
            case 65: goto L_0x16b5;
            case 66: goto L_0x16b5;
            case 67: goto L_0x1695;
            case 68: goto L_0x1675;
            case 69: goto L_0x1650;
            case 70: goto L_0x1630;
            case 71: goto L_0x160b;
            case 72: goto L_0x15eb;
            case 73: goto L_0x15cb;
            case 74: goto L_0x15ab;
            case 75: goto L_0x158b;
            case 76: goto L_0x156b;
            case 77: goto L_0x154b;
            case 78: goto L_0x152b;
            case 79: goto L_0x150b;
            case 80: goto L_0x14d7;
            case 81: goto L_0x14a5;
            case 82: goto L_0x1473;
            case 83: goto L_0x143f;
            case 84: goto L_0x140b;
            case 85: goto L_0x13e9;
            case 86: goto L_0x137c;
            case 87: goto L_0x1319;
            case 88: goto L_0x12b6;
            case 89: goto L_0x1253;
            case 90: goto L_0x11f0;
            case 91: goto L_0x118d;
            case 92: goto L_0x10a6;
            case 93: goto L_0x1043;
            case 94: goto L_0x0fd6;
            case 95: goto L_0x0var_;
            case 96: goto L_0x0efc;
            case 97: goto L_0x0e99;
            case 98: goto L_0x0e36;
            case 99: goto L_0x0dd3;
            case 100: goto L_0x0d70;
            case 101: goto L_0x0d0d;
            case 102: goto L_0x0caa;
            case 103: goto L_0x0c8d;
            case 104: goto L_0x0c8a;
            case 105: goto L_0x0CLASSNAME;
            case 106: goto L_0x0CLASSNAME;
            case 107: goto L_0x0CLASSNAME;
            case 108: goto L_0x0CLASSNAME;
            case 109: goto L_0x0CLASSNAME;
            case 110: goto L_0x0CLASSNAME;
            case 111: goto L_0x0CLASSNAME;
            case 112: goto L_0x0CLASSNAME;
            case 113: goto L_0x0CLASSNAME;
            default: goto L_0x0CLASSNAME;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0c8d, code lost:
        r3 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
        r8 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
        r4 = r3;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0cae, code lost:
        if (r1 <= 0) goto L_0x0cd0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x0cb0, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0cd0, code lost:
        if (r13 == false) goto L_0x0cf2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0cd2, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0cf2, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r14[0]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0d11, code lost:
        if (r1 <= 0) goto L_0x0d33;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x0d13, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceUser", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0d33, code lost:
        if (r13 == false) goto L_0x0d55;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x0d35, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x0d55, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r14[0]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x0d74, code lost:
        if (r1 <= 0) goto L_0x0d96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x0d76, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreUser", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x0d96, code lost:
        if (r13 == false) goto L_0x0db8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0d98, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x0db8, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r14[0]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x0dd7, code lost:
        if (r1 <= 0) goto L_0x0df9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:0x0dd9, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x0df9, code lost:
        if (r13 == false) goto L_0x0e1b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x0dfb, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x0e1b, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r14[0]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x0e3a, code lost:
        if (r1 <= 0) goto L_0x0e5c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x0e3c, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x0e5c, code lost:
        if (r13 == false) goto L_0x0e7e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x0e5e, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x0e7e, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r14[0]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x0e9d, code lost:
        if (r1 <= 0) goto L_0x0ebf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:0x0e9f, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x0ebf, code lost:
        if (r13 == false) goto L_0x0ee1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x0ec1, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x0ee1, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r14[0]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x0var_, code lost:
        if (r1 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x0var_, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x0var_, code lost:
        if (r13 == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x0var_, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r14[0], r14[2], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x0var_, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x0f6d, code lost:
        if (r1 <= 0) goto L_0x0f8f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x0f6f, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x0f8f, code lost:
        if (r13 == false) goto L_0x0fb6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x0var_, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r14[0], r14[2], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x0fb6, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x0fda, code lost:
        if (r1 <= 0) goto L_0x0ffc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x0fdc, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x0ffc, code lost:
        if (r13 == false) goto L_0x1023;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x0ffe, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r14[0], r14[2], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x1023, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x1047, code lost:
        if (r1 <= 0) goto L_0x1069;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x1049, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x1069, code lost:
        if (r13 == false) goto L_0x108b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x106b, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x108b, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r14[0]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x10aa, code lost:
        if (r1 <= 0) goto L_0x10f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x10ae, code lost:
        if (r14.length <= 1) goto L_0x10d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x10b6, code lost:
        if (android.text.TextUtils.isEmpty(r14[1]) != false) goto L_0x10d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x10b8, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x10d8, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r14[0]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x10f3, code lost:
        if (r13 == false) goto L_0x1146;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x10f7, code lost:
        if (r14.length <= 2) goto L_0x1126;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x10ff, code lost:
        if (android.text.TextUtils.isEmpty(r14[2]) != false) goto L_0x1126;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x1101, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r14[0], r14[2], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x1126, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x1148, code lost:
        if (r14.length <= 1) goto L_0x1172;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x1150, code lost:
        if (android.text.TextUtils.isEmpty(r14[1]) != false) goto L_0x1172;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x1152, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x1172, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r14[0]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x1191, code lost:
        if (r1 <= 0) goto L_0x11b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x1193, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x11b3, code lost:
        if (r13 == false) goto L_0x11d5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x11b5, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x11d5, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r14[0]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x11f4, code lost:
        if (r1 <= 0) goto L_0x1216;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x11f6, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x1216, code lost:
        if (r13 == false) goto L_0x1238;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x1218, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x1238, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r14[0]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x1257, code lost:
        if (r1 <= 0) goto L_0x1279;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x1259, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x1279, code lost:
        if (r13 == false) goto L_0x129b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x127b, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x129b, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r14[0]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x12ba, code lost:
        if (r1 <= 0) goto L_0x12dc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x12bc, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x12dc, code lost:
        if (r13 == false) goto L_0x12fe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x12de, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x12fe, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r14[0]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x131d, code lost:
        if (r1 <= 0) goto L_0x133f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x131f, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x133f, code lost:
        if (r13 == false) goto L_0x1361;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x1341, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x1361, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r14[0]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x1380, code lost:
        if (r1 <= 0) goto L_0x13a2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x1382, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x13a2, code lost:
        if (r13 == false) goto L_0x13c9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x13a4, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r14[0], r14[1], r14[2]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x13c9, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x13e9, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:851:0x140b, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r14[0], r14[1], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r14[2]).intValue()));
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x143f, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r14[0], r14[1], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r14[2]).intValue()));
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x1473, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r14[0], r14[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r14[2]).intValue()));
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x14a5, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r14[0], r14[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r14[2]).intValue()));
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x14d7, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r14[0], r14[1], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt(r14[2]).intValue()));
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:856:0x150b, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("UserAcceptedToGroupPushWithGroup", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x152b, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x154b, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x156b, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x158b, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x15ab, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:862:0x15cb, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:863:0x15eb, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupEndedCall", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:864:0x160b, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r14[0], r14[1], r14[2]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:865:0x1630, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:866:0x1650, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r14[0], r14[1], r14[2]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x1675, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x1695, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x16b5, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r14[0], r14[1]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x16d5, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r14[0], r14[1], r14[2]);
        r67 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:871:0x1705, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r14[0], r14[1], r14[2], r14[3]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x172f, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r14[0], r14[1], r14[2]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x175f, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r14[0], r14[1]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x178a, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r14[0], r14[1]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x17b5, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r14[0], r14[1]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x17e0, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r14[0], r14[1], r14[2]);
        r67 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x1810, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r14[0], r14[1], r14[2]);
        r67 = org.telegram.messenger.LocaleController.getString("PollQuiz", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x1840, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r14[0], r14[1], r14[2]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x1870, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r14[0], r14[1]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x189d, code lost:
        if (r14.length <= 2) goto L_0x18e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x18a5, code lost:
        if (android.text.TextUtils.isEmpty(r14[2]) != false) goto L_0x18e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x18a7, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r14[0], r14[1], r14[2]);
        r67 = r14[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:0x18e9, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r14[0], r14[1]);
        r67 = r14[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x1926, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r14[0], r14[1]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x1951, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r14[0], r14[1]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x197c, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r14[0], r14[1]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x19a7, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r14[0], r14[1]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x19d2, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r14[0], r14[1]);
        r67 = org.telegram.messenger.LocaleController.getString("Message", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x19fd, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r14[0], r14[1], r14[2]);
        r67 = r14[2];
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x1a26, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r14[0]);
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x1a43, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r14[1]).intValue()));
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x1a72, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r14[1]).intValue()));
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x1aa1, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r14[1]).intValue()));
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x1ace, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r14[1]).intValue()));
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:897:0x1afb, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r14[1]).intValue()).toLowerCase());
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x1b2e, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:899:0x1b54, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:0x1b7a, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x1ba0, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x1bc6, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r14[0], r14[1]);
        r67 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:0x1bf1, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r14[0], r14[1]);
        r67 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:0x1c1c, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r14[0], r14[1]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x1CLASSNAME, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:0x1c6f, code lost:
        if (r14.length <= 1) goto L_0x1cb6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:909:0x1CLASSNAME, code lost:
        if (android.text.TextUtils.isEmpty(r14[1]) != false) goto L_0x1cb6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:910:0x1CLASSNAME, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r14[0], r14[1]);
        r67 = r14[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:0x1cb6, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:0x1cda, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:913:0x1d00, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:0x1d26, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:0x1d4c, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x1d72, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("Message", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:917:0x1d98, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r14[0]);
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:918:0x1db5, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r14[1]).intValue()));
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:0x1de4, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r14[1]).intValue()));
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:920:0x1e13, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r14[1]).intValue()));
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x1e40, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r14[1]).intValue()));
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x1e6d, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt(r14[1]).intValue()));
        r8 = r30;
        r6 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x1e9c, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r14[0], r14[1]);
        r67 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x1ec7, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r14[0], r14[1], r14[2]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x1eec, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r14[0], r14[1]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x1var_, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x1f3d, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:928:0x1var_, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:0x1var_, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r14[0], r14[1]);
        r67 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x1fb4, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", NUM, r14[0], r14[1]);
        r67 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x1fdf, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r14[0], r14[1]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:932:0x200a, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x2032, code lost:
        if (r14.length <= 1) goto L_0x2079;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:0x203a, code lost:
        if (android.text.TextUtils.isEmpty(r14[1]) != false) goto L_0x2079;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:0x203c, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r14[0], r14[1]);
        r67 = r14[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:0x2079, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x209d, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:0x20c3, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:0x20e9, code lost:
        r4 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r14[0]);
        r8 = r30;
        r6 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:0x2105, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:943:0x212b, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:944:0x2151, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:0x2177, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:946:0x219d, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r14[0]);
        r67 = org.telegram.messenger.LocaleController.getString("Message", NUM);
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x21c2, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r14[0], r14[1]);
        r67 = r14[1];
        r8 = r30;
        r6 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x21e4, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x21fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:0x21e6, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x21fa, code lost:
        r3 = r77;
        r8 = r30;
        r6 = false;
        r4 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x2203, code lost:
        r62 = r3;
        r73 = r4;
        r74 = r6;
        r71 = r9;
        r16 = "CHAT_REACT_";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x220e, code lost:
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:?, code lost:
        r4 = r3.getReactedText(r5, r14);
        r8 = r30;
        r6 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x2218, code lost:
        if (r4 == null) goto L_0x235e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:0x221a, code lost:
        r9 = new org.telegram.tgnet.TLRPC.TL_message();
        r9.id = r7;
        r10 = r54;
        r9.random_id = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:957:0x2225, code lost:
        if (r67 == null) goto L_0x222a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:0x2227, code lost:
        r15 = r67;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x222a, code lost:
        r15 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x222b, code lost:
        r9.message = r15;
        r15 = r13;
        r9.date = (int) (r79 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:0x2235, code lost:
        if (r74 == false) goto L_0x2247;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:?, code lost:
        r9.action = new org.telegram.tgnet.TLRPC.TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:964:0x223f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:965:0x2240, code lost:
        r1 = r0;
        r6 = r19;
        r4 = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:966:0x2247, code lost:
        if (r73 == false) goto L_0x2250;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:967:0x2249, code lost:
        r9.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:969:?, code lost:
        r9.dialog_id = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:971:0x2256, code lost:
        if (r71 == 0) goto L_0x2270;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:?, code lost:
        r9.peer_id = new org.telegram.tgnet.TLRPC.TL_peerChannel();
        r54 = r1;
        r1 = r71;
        r9.peer_id.channel_id = r1;
        r71 = r1;
        r1 = r51;
        r51 = r10;
        r10 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:974:0x2270, code lost:
        r54 = r1;
        r1 = r71;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:975:0x2278, code lost:
        if (r51 == 0) goto L_0x228e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:976:0x227a, code lost:
        r9.peer_id = new org.telegram.tgnet.TLRPC.TL_peerChat();
        r71 = r1;
        r1 = r51;
        r9.peer_id.chat_id = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:977:0x2289, code lost:
        r51 = r10;
        r10 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:978:0x228e, code lost:
        r71 = r1;
        r1 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:980:?, code lost:
        r9.peer_id = new org.telegram.tgnet.TLRPC.TL_peerUser();
        r51 = r10;
        r10 = r49;
        r9.peer_id.user_id = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:981:0x22a1, code lost:
        r9.flags |= 256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:983:0x22ab, code lost:
        if (r60 == 0) goto L_0x22c1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:985:?, code lost:
        r9.from_id = new org.telegram.tgnet.TLRPC.TL_peerChat();
        r9.from_id.chat_id = r1;
        r49 = r1;
        r12 = r58;
        r58 = r10;
        r10 = r65;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:986:0x22c1, code lost:
        r12 = r58;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:987:0x22c7, code lost:
        if (r12 == 0) goto L_0x22db;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:988:0x22c9, code lost:
        r49 = r1;
        r9.from_id = new org.telegram.tgnet.TLRPC.TL_peerChannel();
        r9.from_id.channel_id = r12;
        r58 = r10;
        r10 = r65;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:989:0x22db, code lost:
        r49 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:990:0x22e1, code lost:
        if (r65 == 0) goto L_0x22f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x22e3, code lost:
        r9.from_id = new org.telegram.tgnet.TLRPC.TL_peerUser();
        r58 = r10;
        r9.from_id.user_id = r65;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:992:0x22f3, code lost:
        r58 = r10;
        r10 = r65;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:994:?, code lost:
        r9.from_id = r9.peer_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:995:0x22fb, code lost:
        if (r63 != false) goto L_0x2302;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:996:0x22fd, code lost:
        if (r74 == false) goto L_0x2300;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:998:0x2300, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:999:0x2302, code lost:
        r1 = true;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1043:0x2419  */
    /* JADX WARNING: Removed duplicated region for block: B:1044:0x2429  */
    /* JADX WARNING: Removed duplicated region for block: B:1047:0x2430  */
    /* renamed from: lambda$onMessageReceived$3$org-telegram-messenger-GcmPushListenerService  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m68xa7b3420a(java.util.Map r78, long r79) {
        /*
            r77 = this;
            r1 = r77
            r2 = r78
            java.lang.String r3 = "REACT_"
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r4 == 0) goto L_0x000f
            java.lang.String r4 = "GCM START PROCESSING"
            org.telegram.messenger.FileLog.d(r4)
        L_0x000f:
            r4 = -1
            r5 = 0
            r6 = 0
            java.lang.String r8 = "p"
            java.lang.Object r8 = r2.get(r8)     // Catch:{ all -> 0x2411 }
            boolean r9 = r8 instanceof java.lang.String     // Catch:{ all -> 0x2411 }
            if (r9 != 0) goto L_0x002e
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0029 }
            if (r3 == 0) goto L_0x0025
            java.lang.String r3 = "GCM DECRYPT ERROR 1"
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ all -> 0x0029 }
        L_0x0025:
            r77.onDecryptError()     // Catch:{ all -> 0x0029 }
            return
        L_0x0029:
            r0 = move-exception
            r3 = r1
            r1 = r0
            goto L_0x2416
        L_0x002e:
            r9 = r8
            java.lang.String r9 = (java.lang.String) r9     // Catch:{ all -> 0x2411 }
            r10 = 8
            byte[] r9 = android.util.Base64.decode(r9, r10)     // Catch:{ all -> 0x2411 }
            org.telegram.tgnet.NativeByteBuffer r11 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x2411 }
            int r12 = r9.length     // Catch:{ all -> 0x2411 }
            r11.<init>((int) r12)     // Catch:{ all -> 0x2411 }
            r11.writeBytes((byte[]) r9)     // Catch:{ all -> 0x2411 }
            r12 = 0
            r11.position(r12)     // Catch:{ all -> 0x2411 }
            byte[] r13 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x2411 }
            if (r13 != 0) goto L_0x0059
            byte[] r13 = new byte[r10]     // Catch:{ all -> 0x0029 }
            org.telegram.messenger.SharedConfig.pushAuthKeyId = r13     // Catch:{ all -> 0x0029 }
            byte[] r13 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x0029 }
            byte[] r13 = org.telegram.messenger.Utilities.computeSHA1((byte[]) r13)     // Catch:{ all -> 0x0029 }
            int r14 = r13.length     // Catch:{ all -> 0x0029 }
            int r14 = r14 - r10
            byte[] r15 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x0029 }
            java.lang.System.arraycopy(r13, r14, r15, r12, r10)     // Catch:{ all -> 0x0029 }
        L_0x0059:
            byte[] r13 = new byte[r10]     // Catch:{ all -> 0x2411 }
            r14 = 1
            r11.readBytes(r13, r14)     // Catch:{ all -> 0x2411 }
            byte[] r15 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x2411 }
            boolean r15 = java.util.Arrays.equals(r15, r13)     // Catch:{ all -> 0x2411 }
            r7 = 3
            r10 = 2
            if (r15 != 0) goto L_0x0094
            r77.onDecryptError()     // Catch:{ all -> 0x0029 }
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0029 }
            if (r3 == 0) goto L_0x0093
            java.util.Locale r3 = java.util.Locale.US     // Catch:{ all -> 0x0029 }
            java.lang.String r15 = "GCM DECRYPT ERROR 2 k1=%s k2=%s, key=%s"
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x0029 }
            byte[] r16 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x0029 }
            java.lang.String r16 = org.telegram.messenger.Utilities.bytesToHex(r16)     // Catch:{ all -> 0x0029 }
            r7[r12] = r16     // Catch:{ all -> 0x0029 }
            java.lang.String r12 = org.telegram.messenger.Utilities.bytesToHex(r13)     // Catch:{ all -> 0x0029 }
            r7[r14] = r12     // Catch:{ all -> 0x0029 }
            byte[] r12 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x0029 }
            java.lang.String r12 = org.telegram.messenger.Utilities.bytesToHex(r12)     // Catch:{ all -> 0x0029 }
            r7[r10] = r12     // Catch:{ all -> 0x0029 }
            java.lang.String r3 = java.lang.String.format(r3, r15, r7)     // Catch:{ all -> 0x0029 }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ all -> 0x0029 }
        L_0x0093:
            return
        L_0x0094:
            r15 = 16
            byte[] r15 = new byte[r15]     // Catch:{ all -> 0x2411 }
            r11.readBytes(r15, r14)     // Catch:{ all -> 0x2411 }
            byte[] r7 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x2411 }
            org.telegram.messenger.MessageKeyData r7 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r7, r15, r14, r10)     // Catch:{ all -> 0x2411 }
            java.nio.ByteBuffer r10 = r11.buffer     // Catch:{ all -> 0x2411 }
            byte[] r14 = r7.aesKey     // Catch:{ all -> 0x2411 }
            byte[] r12 = r7.aesIv     // Catch:{ all -> 0x2411 }
            r21 = 0
            r22 = 0
            r23 = 24
            r27 = r4
            int r4 = r9.length     // Catch:{ all -> 0x240b }
            int r24 = r4 + -24
            r18 = r10
            r19 = r14
            r20 = r12
            org.telegram.messenger.Utilities.aesIgeEncryption(r18, r19, r20, r21, r22, r23, r24)     // Catch:{ all -> 0x240b }
            byte[] r28 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x240b }
            r29 = 96
            r30 = 32
            java.nio.ByteBuffer r4 = r11.buffer     // Catch:{ all -> 0x240b }
            r32 = 24
            java.nio.ByteBuffer r10 = r11.buffer     // Catch:{ all -> 0x240b }
            int r33 = r10.limit()     // Catch:{ all -> 0x240b }
            r31 = r4
            byte[] r4 = org.telegram.messenger.Utilities.computeSHA256(r28, r29, r30, r31, r32, r33)     // Catch:{ all -> 0x240b }
            r10 = 8
            r12 = 0
            boolean r14 = org.telegram.messenger.Utilities.arraysEquals(r15, r12, r4, r10)     // Catch:{ all -> 0x240b }
            if (r14 != 0) goto L_0x00fe
            r77.onDecryptError()     // Catch:{ all -> 0x00f7 }
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x00f7 }
            if (r3 == 0) goto L_0x00f6
            java.lang.String r3 = "GCM DECRYPT ERROR 3, key = %s"
            r10 = 1
            java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ all -> 0x00f7 }
            byte[] r12 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x00f7 }
            java.lang.String r12 = org.telegram.messenger.Utilities.bytesToHex(r12)     // Catch:{ all -> 0x00f7 }
            r14 = 0
            r10[r14] = r12     // Catch:{ all -> 0x00f7 }
            java.lang.String r3 = java.lang.String.format(r3, r10)     // Catch:{ all -> 0x00f7 }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ all -> 0x00f7 }
        L_0x00f6:
            return
        L_0x00f7:
            r0 = move-exception
            r3 = r1
            r4 = r27
            r1 = r0
            goto L_0x2416
        L_0x00fe:
            r12 = 1
            int r14 = r11.readInt32(r12)     // Catch:{ all -> 0x240b }
            byte[] r10 = new byte[r14]     // Catch:{ all -> 0x240b }
            r11.readBytes(r10, r12)     // Catch:{ all -> 0x240b }
            java.lang.String r12 = new java.lang.String     // Catch:{ all -> 0x240b }
            r12.<init>(r10)     // Catch:{ all -> 0x240b }
            r6 = r12
            org.json.JSONObject r12 = new org.json.JSONObject     // Catch:{ all -> 0x2403 }
            r12.<init>(r6)     // Catch:{ all -> 0x2403 }
            r18 = r4
            java.lang.String r4 = "loc_key"
            boolean r4 = r12.has(r4)     // Catch:{ all -> 0x2403 }
            if (r4 == 0) goto L_0x0125
            java.lang.String r4 = "loc_key"
            java.lang.String r4 = r12.getString(r4)     // Catch:{ all -> 0x00f7 }
            r5 = r4
            goto L_0x0128
        L_0x0125:
            java.lang.String r4 = ""
            r5 = r4
        L_0x0128:
            java.lang.String r4 = "custom"
            java.lang.Object r4 = r12.get(r4)     // Catch:{ all -> 0x23f9 }
            r19 = r6
            boolean r6 = r4 instanceof org.json.JSONObject     // Catch:{ all -> 0x23ef }
            if (r6 == 0) goto L_0x0144
            java.lang.String r6 = "custom"
            org.json.JSONObject r6 = r12.getJSONObject(r6)     // Catch:{ all -> 0x013b }
            goto L_0x0149
        L_0x013b:
            r0 = move-exception
            r3 = r1
            r6 = r19
            r4 = r27
            r1 = r0
            goto L_0x2416
        L_0x0144:
            org.json.JSONObject r6 = new org.json.JSONObject     // Catch:{ all -> 0x23ef }
            r6.<init>()     // Catch:{ all -> 0x23ef }
        L_0x0149:
            r20 = r4
            java.lang.String r4 = "user_id"
            boolean r4 = r12.has(r4)     // Catch:{ all -> 0x23ef }
            if (r4 == 0) goto L_0x015a
            java.lang.String r4 = "user_id"
            java.lang.Object r4 = r12.get(r4)     // Catch:{ all -> 0x013b }
            goto L_0x015b
        L_0x015a:
            r4 = 0
        L_0x015b:
            if (r4 != 0) goto L_0x0170
            int r21 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x013b }
            org.telegram.messenger.UserConfig r21 = org.telegram.messenger.UserConfig.getInstance(r21)     // Catch:{ all -> 0x013b }
            long r21 = r21.getClientUserId()     // Catch:{ all -> 0x013b }
            r75 = r21
            r21 = r7
            r22 = r8
            r7 = r75
            goto L_0x01b0
        L_0x0170:
            r21 = r7
            boolean r7 = r4 instanceof java.lang.Long     // Catch:{ all -> 0x23ef }
            if (r7 == 0) goto L_0x0184
            r7 = r4
            java.lang.Long r7 = (java.lang.Long) r7     // Catch:{ all -> 0x013b }
            long r22 = r7.longValue()     // Catch:{ all -> 0x013b }
            r75 = r22
            r22 = r8
            r7 = r75
            goto L_0x01b0
        L_0x0184:
            boolean r7 = r4 instanceof java.lang.Integer     // Catch:{ all -> 0x23ef }
            if (r7 == 0) goto L_0x0193
            r7 = r4
            java.lang.Integer r7 = (java.lang.Integer) r7     // Catch:{ all -> 0x013b }
            int r7 = r7.intValue()     // Catch:{ all -> 0x013b }
            r22 = r8
            long r7 = (long) r7
            goto L_0x01b0
        L_0x0193:
            r22 = r8
            boolean r7 = r4 instanceof java.lang.String     // Catch:{ all -> 0x23ef }
            if (r7 == 0) goto L_0x01a6
            r7 = r4
            java.lang.String r7 = (java.lang.String) r7     // Catch:{ all -> 0x013b }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x013b }
            int r7 = r7.intValue()     // Catch:{ all -> 0x013b }
            long r7 = (long) r7
            goto L_0x01b0
        L_0x01a6:
            int r7 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x23ef }
            org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r7)     // Catch:{ all -> 0x23ef }
            long r7 = r7.getClientUserId()     // Catch:{ all -> 0x23ef }
        L_0x01b0:
            int r23 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x23ef }
            r24 = 0
            r28 = 0
            r29 = r4
            r4 = r28
        L_0x01ba:
            r28 = r9
            r9 = 3
            if (r4 >= r9) goto L_0x01d5
            org.telegram.messenger.UserConfig r9 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x013b }
            long r30 = r9.getClientUserId()     // Catch:{ all -> 0x013b }
            int r9 = (r30 > r7 ? 1 : (r30 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x01d0
            r23 = r4
            r24 = 1
            goto L_0x01d5
        L_0x01d0:
            int r4 = r4 + 1
            r9 = r28
            goto L_0x01ba
        L_0x01d5:
            if (r24 != 0) goto L_0x01e6
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x013b }
            if (r3 == 0) goto L_0x01e0
            java.lang.String r3 = "GCM ACCOUNT NOT FOUND"
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ all -> 0x013b }
        L_0x01e0:
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch     // Catch:{ all -> 0x013b }
            r3.countDown()     // Catch:{ all -> 0x013b }
            return
        L_0x01e6:
            r31 = r23
            r4 = r23
            org.telegram.messenger.UserConfig r9 = org.telegram.messenger.UserConfig.getInstance(r31)     // Catch:{ all -> 0x23e5 }
            boolean r9 = r9.isClientActivated()     // Catch:{ all -> 0x23e5 }
            if (r9 != 0) goto L_0x020c
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0203 }
            if (r3 == 0) goto L_0x01fd
            java.lang.String r3 = "GCM ACCOUNT NOT ACTIVATED"
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ all -> 0x0203 }
        L_0x01fd:
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch     // Catch:{ all -> 0x0203 }
            r3.countDown()     // Catch:{ all -> 0x0203 }
            return
        L_0x0203:
            r0 = move-exception
            r3 = r1
            r6 = r19
            r4 = r31
            r1 = r0
            goto L_0x2416
        L_0x020c:
            java.lang.String r9 = "google.sent_time"
            java.lang.Object r9 = r2.get(r9)     // Catch:{ all -> 0x23e5 }
            int r27 = r5.hashCode()     // Catch:{ all -> 0x23e5 }
            switch(r27) {
                case -1963663249: goto L_0x0238;
                case -920689527: goto L_0x022e;
                case 633004703: goto L_0x0224;
                case 1365673842: goto L_0x021a;
                default: goto L_0x0219;
            }
        L_0x0219:
            goto L_0x0242
        L_0x021a:
            java.lang.String r2 = "GEO_LIVE_PENDING"
            boolean r2 = r5.equals(r2)     // Catch:{ all -> 0x0203 }
            if (r2 == 0) goto L_0x0219
            r2 = 3
            goto L_0x0243
        L_0x0224:
            java.lang.String r2 = "MESSAGE_ANNOUNCEMENT"
            boolean r2 = r5.equals(r2)     // Catch:{ all -> 0x0203 }
            if (r2 == 0) goto L_0x0219
            r2 = 1
            goto L_0x0243
        L_0x022e:
            java.lang.String r2 = "DC_UPDATE"
            boolean r2 = r5.equals(r2)     // Catch:{ all -> 0x0203 }
            if (r2 == 0) goto L_0x0219
            r2 = 0
            goto L_0x0243
        L_0x0238:
            java.lang.String r2 = "SESSION_REVOKE"
            boolean r2 = r5.equals(r2)     // Catch:{ all -> 0x0203 }
            if (r2 == 0) goto L_0x0219
            r2 = 2
            goto L_0x0243
        L_0x0242:
            r2 = -1
        L_0x0243:
            switch(r2) {
                case 0: goto L_0x02b4;
                case 1: goto L_0x026c;
                case 2: goto L_0x025e;
                case 3: goto L_0x024e;
                default: goto L_0x0246;
            }
        L_0x0246:
            r40 = r7
            r7 = 0
            java.lang.String r2 = "channel_id"
            goto L_0x02f4
        L_0x024e:
            org.telegram.messenger.DispatchQueue r2 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x0203 }
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1 r3 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1     // Catch:{ all -> 0x0203 }
            r3.<init>(r4)     // Catch:{ all -> 0x0203 }
            r2.postRunnable(r3)     // Catch:{ all -> 0x0203 }
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x0203 }
            r2.countDown()     // Catch:{ all -> 0x0203 }
            return
        L_0x025e:
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0 r2 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0     // Catch:{ all -> 0x0203 }
            r2.<init>(r4)     // Catch:{ all -> 0x0203 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ all -> 0x0203 }
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x0203 }
            r2.countDown()     // Catch:{ all -> 0x0203 }
            return
        L_0x026c:
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r2 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x0203 }
            r2.<init>()     // Catch:{ all -> 0x0203 }
            r3 = 0
            r2.popup = r3     // Catch:{ all -> 0x0203 }
            r3 = 2
            r2.flags = r3     // Catch:{ all -> 0x0203 }
            r16 = 1000(0x3e8, double:4.94E-321)
            r40 = r7
            long r7 = r79 / r16
            int r3 = (int) r7     // Catch:{ all -> 0x0203 }
            r2.inbox_date = r3     // Catch:{ all -> 0x0203 }
            java.lang.String r3 = "message"
            java.lang.String r3 = r12.getString(r3)     // Catch:{ all -> 0x0203 }
            r2.message = r3     // Catch:{ all -> 0x0203 }
            java.lang.String r3 = "announcement"
            r2.type = r3     // Catch:{ all -> 0x0203 }
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ all -> 0x0203 }
            r3.<init>()     // Catch:{ all -> 0x0203 }
            r2.media = r3     // Catch:{ all -> 0x0203 }
            org.telegram.tgnet.TLRPC$TL_updates r3 = new org.telegram.tgnet.TLRPC$TL_updates     // Catch:{ all -> 0x0203 }
            r3.<init>()     // Catch:{ all -> 0x0203 }
            java.util.ArrayList r7 = r3.updates     // Catch:{ all -> 0x0203 }
            r7.add(r2)     // Catch:{ all -> 0x0203 }
            org.telegram.messenger.DispatchQueue r7 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x0203 }
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3 r8 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3     // Catch:{ all -> 0x0203 }
            r8.<init>(r4, r3)     // Catch:{ all -> 0x0203 }
            r7.postRunnable(r8)     // Catch:{ all -> 0x0203 }
            org.telegram.tgnet.ConnectionsManager r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r31)     // Catch:{ all -> 0x0203 }
            r7.resumeNetworkMaybe()     // Catch:{ all -> 0x0203 }
            java.util.concurrent.CountDownLatch r7 = r1.countDownLatch     // Catch:{ all -> 0x0203 }
            r7.countDown()     // Catch:{ all -> 0x0203 }
            return
        L_0x02b4:
            r40 = r7
            java.lang.String r2 = "dc"
            int r2 = r6.getInt(r2)     // Catch:{ all -> 0x0203 }
            java.lang.String r3 = "addr"
            java.lang.String r3 = r6.getString(r3)     // Catch:{ all -> 0x0203 }
            java.lang.String r7 = ":"
            java.lang.String[] r7 = r3.split(r7)     // Catch:{ all -> 0x0203 }
            int r8 = r7.length     // Catch:{ all -> 0x0203 }
            r16 = r3
            r3 = 2
            if (r8 == r3) goto L_0x02d4
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch     // Catch:{ all -> 0x0203 }
            r3.countDown()     // Catch:{ all -> 0x0203 }
            return
        L_0x02d4:
            r3 = 0
            r3 = r7[r3]     // Catch:{ all -> 0x0203 }
            r8 = 1
            r8 = r7[r8]     // Catch:{ all -> 0x0203 }
            int r8 = java.lang.Integer.parseInt(r8)     // Catch:{ all -> 0x0203 }
            r17 = r7
            org.telegram.tgnet.ConnectionsManager r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r31)     // Catch:{ all -> 0x0203 }
            r7.applyDatacenterAddress(r2, r3, r8)     // Catch:{ all -> 0x0203 }
            org.telegram.tgnet.ConnectionsManager r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r31)     // Catch:{ all -> 0x0203 }
            r7.resumeNetworkMaybe()     // Catch:{ all -> 0x0203 }
            java.util.concurrent.CountDownLatch r7 = r1.countDownLatch     // Catch:{ all -> 0x0203 }
            r7.countDown()     // Catch:{ all -> 0x0203 }
            return
        L_0x02f4:
            boolean r2 = r6.has(r2)     // Catch:{ all -> 0x23e5 }
            if (r2 == 0) goto L_0x0310
            java.lang.String r2 = "channel_id"
            long r32 = r6.getLong(r2)     // Catch:{ all -> 0x0203 }
            r34 = r32
            r32 = r7
            r27 = r9
            r2 = r10
            r7 = r34
            long r9 = -r7
            r75 = r7
            r7 = r9
            r9 = r75
            goto L_0x031d
        L_0x0310:
            r32 = r7
            r27 = r9
            r2 = r10
            r7 = 0
            r34 = r7
            r7 = r32
            r9 = r34
        L_0x031d:
            r42 = r2
            java.lang.String r2 = "from_id"
            boolean r2 = r6.has(r2)     // Catch:{ all -> 0x23e5 }
            if (r2 == 0) goto L_0x0334
            java.lang.String r2 = "from_id"
            long r32 = r6.getLong(r2)     // Catch:{ all -> 0x0203 }
            r7 = r32
            r75 = r7
            r32 = r75
            goto L_0x033c
        L_0x0334:
            r32 = 0
            r75 = r7
            r7 = r32
            r32 = r75
        L_0x033c:
            java.lang.String r2 = "chat_id"
            boolean r2 = r6.has(r2)     // Catch:{ all -> 0x23e5 }
            if (r2 == 0) goto L_0x035e
            java.lang.String r2 = "chat_id"
            long r34 = r6.getLong(r2)     // Catch:{ all -> 0x0354 }
            r36 = r34
            r44 = r13
            r43 = r14
            r13 = r36
            long r1 = -r13
            goto L_0x036a
        L_0x0354:
            r0 = move-exception
            r3 = r77
            r1 = r0
            r6 = r19
            r4 = r31
            goto L_0x2416
        L_0x035e:
            r44 = r13
            r43 = r14
            r1 = 0
            r36 = r1
            r1 = r32
            r13 = r36
        L_0x036a:
            r32 = r1
            java.lang.String r1 = "encryption_id"
            boolean r1 = r6.has(r1)     // Catch:{ all -> 0x23da }
            if (r1 == 0) goto L_0x0380
            java.lang.String r1 = "encryption_id"
            int r1 = r6.getInt(r1)     // Catch:{ all -> 0x0354 }
            long r1 = (long) r1     // Catch:{ all -> 0x0354 }
            long r1 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r1)     // Catch:{ all -> 0x0354 }
            goto L_0x0382
        L_0x0380:
            r1 = r32
        L_0x0382:
            r45 = r11
            java.lang.String r11 = "schedule"
            boolean r11 = r6.has(r11)     // Catch:{ all -> 0x23da }
            if (r11 == 0) goto L_0x039b
            java.lang.String r11 = "schedule"
            int r11 = r6.getInt(r11)     // Catch:{ all -> 0x0354 }
            r46 = r15
            r15 = 1
            if (r11 != r15) goto L_0x0399
            r11 = 1
            goto L_0x039a
        L_0x0399:
            r11 = 0
        L_0x039a:
            goto L_0x039e
        L_0x039b:
            r46 = r15
            r11 = 0
        L_0x039e:
            r47 = r11
            r15 = r12
            r11 = 0
            int r30 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r30 != 0) goto L_0x03b2
            java.lang.String r11 = "ENCRYPTED_MESSAGE"
            boolean r11 = r11.equals(r5)     // Catch:{ all -> 0x0354 }
            if (r11 == 0) goto L_0x03b2
            long r11 = org.telegram.messenger.NotificationsController.globalSecretChatId     // Catch:{ all -> 0x0354 }
            r1 = r11
        L_0x03b2:
            r11 = 1
            r32 = 0
            int r12 = (r1 > r32 ? 1 : (r1 == r32 ? 0 : -1))
            if (r12 == 0) goto L_0x23a2
            java.lang.String r12 = "READ_HISTORY"
            boolean r12 = r12.equals(r5)     // Catch:{ all -> 0x23da }
            r48 = r11
            java.lang.String r11 = " for dialogId = "
            if (r12 == 0) goto L_0x0457
            java.lang.String r3 = "max_id"
            int r3 = r6.getInt(r3)     // Catch:{ all -> 0x0354 }
            java.util.ArrayList r12 = new java.util.ArrayList     // Catch:{ all -> 0x0354 }
            r12.<init>()     // Catch:{ all -> 0x0354 }
            boolean r16 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0354 }
            if (r16 == 0) goto L_0x03f3
            r30 = r15
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ all -> 0x0354 }
            r15.<init>()     // Catch:{ all -> 0x0354 }
            r49 = r6
            java.lang.String r6 = "GCM received read notification max_id = "
            r15.append(r6)     // Catch:{ all -> 0x0354 }
            r15.append(r3)     // Catch:{ all -> 0x0354 }
            r15.append(r11)     // Catch:{ all -> 0x0354 }
            r15.append(r1)     // Catch:{ all -> 0x0354 }
            java.lang.String r6 = r15.toString()     // Catch:{ all -> 0x0354 }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ all -> 0x0354 }
            goto L_0x03f7
        L_0x03f3:
            r49 = r6
            r30 = r15
        L_0x03f7:
            r16 = 0
            int r6 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1))
            if (r6 == 0) goto L_0x040b
            org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox r6 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox     // Catch:{ all -> 0x0354 }
            r6.<init>()     // Catch:{ all -> 0x0354 }
            r6.channel_id = r9     // Catch:{ all -> 0x0354 }
            r6.max_id = r3     // Catch:{ all -> 0x0354 }
            r12.add(r6)     // Catch:{ all -> 0x0354 }
            goto L_0x0432
        L_0x040b:
            org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox r6 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox     // Catch:{ all -> 0x0354 }
            r6.<init>()     // Catch:{ all -> 0x0354 }
            r16 = 0
            int r11 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1))
            if (r11 == 0) goto L_0x0422
            org.telegram.tgnet.TLRPC$TL_peerUser r11 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x0354 }
            r11.<init>()     // Catch:{ all -> 0x0354 }
            r6.peer = r11     // Catch:{ all -> 0x0354 }
            org.telegram.tgnet.TLRPC$Peer r11 = r6.peer     // Catch:{ all -> 0x0354 }
            r11.user_id = r7     // Catch:{ all -> 0x0354 }
            goto L_0x042d
        L_0x0422:
            org.telegram.tgnet.TLRPC$TL_peerChat r11 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x0354 }
            r11.<init>()     // Catch:{ all -> 0x0354 }
            r6.peer = r11     // Catch:{ all -> 0x0354 }
            org.telegram.tgnet.TLRPC$Peer r11 = r6.peer     // Catch:{ all -> 0x0354 }
            r11.chat_id = r13     // Catch:{ all -> 0x0354 }
        L_0x042d:
            r6.max_id = r3     // Catch:{ all -> 0x0354 }
            r12.add(r6)     // Catch:{ all -> 0x0354 }
        L_0x0432:
            org.telegram.messenger.MessagesController r34 = org.telegram.messenger.MessagesController.getInstance(r4)     // Catch:{ all -> 0x0354 }
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r35 = r12
            r34.processUpdateArray(r35, r36, r37, r38, r39)     // Catch:{ all -> 0x0354 }
            r3 = r77
            r54 = r1
            r53 = r4
            r26 = r5
            r58 = r7
            r71 = r9
            r64 = r30
            r68 = r49
            r49 = r13
            goto L_0x23b6
        L_0x0457:
            r49 = r6
            r30 = r15
            java.lang.String r6 = "MESSAGE_DELETED"
            boolean r6 = r6.equals(r5)     // Catch:{ all -> 0x23da }
            java.lang.String r12 = "messages"
            if (r6 == 0) goto L_0x04f7
            r6 = r49
            java.lang.String r3 = r6.getString(r12)     // Catch:{ all -> 0x0354 }
            java.lang.String r12 = ","
            java.lang.String[] r12 = r3.split(r12)     // Catch:{ all -> 0x0354 }
            androidx.collection.LongSparseArray r15 = new androidx.collection.LongSparseArray     // Catch:{ all -> 0x0354 }
            r15.<init>()     // Catch:{ all -> 0x0354 }
            java.util.ArrayList r16 = new java.util.ArrayList     // Catch:{ all -> 0x0354 }
            r16.<init>()     // Catch:{ all -> 0x0354 }
            r17 = r16
            r16 = 0
            r25 = r3
            r3 = r16
        L_0x0483:
            r49 = r7
            int r7 = r12.length     // Catch:{ all -> 0x0354 }
            if (r3 >= r7) goto L_0x049a
            r7 = r12[r3]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0354 }
            r8 = r17
            r8.add(r7)     // Catch:{ all -> 0x0354 }
            int r3 = r3 + 1
            r17 = r8
            r7 = r49
            goto L_0x0483
        L_0x049a:
            r8 = r17
            r3 = r12
            r51 = r13
            long r12 = -r9
            r15.put(r12, r8)     // Catch:{ all -> 0x0354 }
            org.telegram.messenger.NotificationsController r7 = org.telegram.messenger.NotificationsController.getInstance(r31)     // Catch:{ all -> 0x0354 }
            r7.removeDeletedMessagesFromNotifications(r15)     // Catch:{ all -> 0x0354 }
            org.telegram.messenger.MessagesController r32 = org.telegram.messenger.MessagesController.getInstance(r31)     // Catch:{ all -> 0x0354 }
            r33 = r1
            r35 = r8
            r36 = r9
            r32.deleteMessagesByPush(r33, r35, r36)     // Catch:{ all -> 0x0354 }
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0354 }
            if (r7 == 0) goto L_0x04e3
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0354 }
            r7.<init>()     // Catch:{ all -> 0x0354 }
            java.lang.String r12 = "GCM received "
            r7.append(r12)     // Catch:{ all -> 0x0354 }
            r7.append(r5)     // Catch:{ all -> 0x0354 }
            r7.append(r11)     // Catch:{ all -> 0x0354 }
            r7.append(r1)     // Catch:{ all -> 0x0354 }
            java.lang.String r11 = " mids = "
            r7.append(r11)     // Catch:{ all -> 0x0354 }
            java.lang.String r11 = ","
            java.lang.String r11 = android.text.TextUtils.join(r11, r8)     // Catch:{ all -> 0x0354 }
            r7.append(r11)     // Catch:{ all -> 0x0354 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0354 }
            org.telegram.messenger.FileLog.d(r7)     // Catch:{ all -> 0x0354 }
        L_0x04e3:
            r3 = r77
            r54 = r1
            r53 = r4
            r26 = r5
            r68 = r6
            r71 = r9
            r64 = r30
            r58 = r49
            r49 = r51
            goto L_0x23b6
        L_0x04f7:
            r51 = r13
            r6 = r49
            r49 = r7
            boolean r7 = android.text.TextUtils.isEmpty(r5)     // Catch:{ all -> 0x23da }
            if (r7 != 0) goto L_0x238f
            java.lang.String r7 = "msg_id"
            boolean r7 = r6.has(r7)     // Catch:{ all -> 0x23da }
            if (r7 == 0) goto L_0x0512
            java.lang.String r7 = "msg_id"
            int r7 = r6.getInt(r7)     // Catch:{ all -> 0x0354 }
            goto L_0x0513
        L_0x0512:
            r7 = 0
        L_0x0513:
            java.lang.String r8 = "random_id"
            boolean r8 = r6.has(r8)     // Catch:{ all -> 0x23da }
            if (r8 == 0) goto L_0x052a
            java.lang.String r8 = "random_id"
            java.lang.String r8 = r6.getString(r8)     // Catch:{ all -> 0x0354 }
            java.lang.Long r8 = org.telegram.messenger.Utilities.parseLong(r8)     // Catch:{ all -> 0x0354 }
            long r13 = r8.longValue()     // Catch:{ all -> 0x0354 }
            goto L_0x052c
        L_0x052a:
            r13 = 0
        L_0x052c:
            r8 = 0
            if (r7 == 0) goto L_0x0572
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r31)     // Catch:{ all -> 0x0354 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r15 = r15.dialogs_read_inbox_max     // Catch:{ all -> 0x0354 }
            r34 = r8
            java.lang.Long r8 = java.lang.Long.valueOf(r1)     // Catch:{ all -> 0x0354 }
            java.lang.Object r8 = r15.get(r8)     // Catch:{ all -> 0x0354 }
            java.lang.Integer r8 = (java.lang.Integer) r8     // Catch:{ all -> 0x0354 }
            if (r8 != 0) goto L_0x0562
            org.telegram.messenger.MessagesStorage r15 = org.telegram.messenger.MessagesStorage.getInstance(r31)     // Catch:{ all -> 0x0354 }
            r35 = r8
            r8 = 0
            int r15 = r15.getDialogReadMax(r8, r1)     // Catch:{ all -> 0x0354 }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r15)     // Catch:{ all -> 0x0354 }
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r4)     // Catch:{ all -> 0x0354 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r15 = r15.dialogs_read_inbox_max     // Catch:{ all -> 0x0354 }
            r53 = r4
            java.lang.Long r4 = java.lang.Long.valueOf(r1)     // Catch:{ all -> 0x0354 }
            r15.put(r4, r8)     // Catch:{ all -> 0x0354 }
            goto L_0x0566
        L_0x0562:
            r53 = r4
            r35 = r8
        L_0x0566:
            int r4 = r8.intValue()     // Catch:{ all -> 0x0354 }
            if (r7 <= r4) goto L_0x056f
            r4 = 1
            r8 = r4
            goto L_0x0571
        L_0x056f:
            r8 = r34
        L_0x0571:
            goto L_0x058a
        L_0x0572:
            r53 = r4
            r34 = r8
            r32 = 0
            int r4 = (r13 > r32 ? 1 : (r13 == r32 ? 0 : -1))
            if (r4 == 0) goto L_0x0588
            org.telegram.messenger.MessagesStorage r4 = org.telegram.messenger.MessagesStorage.getInstance(r23)     // Catch:{ all -> 0x0354 }
            boolean r4 = r4.checkMessageByRandomId(r13)     // Catch:{ all -> 0x0354 }
            if (r4 != 0) goto L_0x0588
            r8 = 1
            goto L_0x058a
        L_0x0588:
            r8 = r34
        L_0x058a:
            boolean r4 = r5.startsWith(r3)     // Catch:{ all -> 0x23da }
            java.lang.String r15 = "CHAT_REACT_"
            if (r4 != 0) goto L_0x0598
            boolean r4 = r5.startsWith(r15)     // Catch:{ all -> 0x0354 }
            if (r4 == 0) goto L_0x0599
        L_0x0598:
            r8 = 1
        L_0x0599:
            if (r8 == 0) goto L_0x237a
            java.lang.String r4 = "chat_from_id"
            r54 = r13
            r13 = 0
            long r32 = r6.optLong(r4, r13)     // Catch:{ all -> 0x23da }
            r56 = r32
            java.lang.String r4 = "chat_from_broadcast_id"
            long r32 = r6.optLong(r4, r13)     // Catch:{ all -> 0x23da }
            r58 = r32
            java.lang.String r4 = "chat_from_group_id"
            long r32 = r6.optLong(r4, r13)     // Catch:{ all -> 0x23da }
            r60 = r32
            r62 = r3
            r3 = r56
            int r32 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r32 != 0) goto L_0x05c6
            int r34 = (r60 > r13 ? 1 : (r60 == r13 ? 0 : -1))
            if (r34 == 0) goto L_0x05c4
            goto L_0x05c6
        L_0x05c4:
            r13 = 0
            goto L_0x05c7
        L_0x05c6:
            r13 = 1
        L_0x05c7:
            java.lang.String r14 = "mention"
            boolean r14 = r6.has(r14)     // Catch:{ all -> 0x23da }
            if (r14 == 0) goto L_0x05d9
            java.lang.String r14 = "mention"
            int r14 = r6.getInt(r14)     // Catch:{ all -> 0x0354 }
            if (r14 == 0) goto L_0x05d9
            r14 = 1
            goto L_0x05da
        L_0x05d9:
            r14 = 0
        L_0x05da:
            r56 = r8
            java.lang.String r8 = "silent"
            boolean r8 = r6.has(r8)     // Catch:{ all -> 0x23da }
            if (r8 == 0) goto L_0x05ee
            java.lang.String r8 = "silent"
            int r8 = r6.getInt(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x05ee
            r8 = 1
            goto L_0x05ef
        L_0x05ee:
            r8 = 0
        L_0x05ef:
            r57 = r8
            java.lang.String r8 = "loc_args"
            r63 = r14
            r14 = r30
            boolean r8 = r14.has(r8)     // Catch:{ all -> 0x23da }
            if (r8 == 0) goto L_0x061e
            java.lang.String r8 = "loc_args"
            org.json.JSONArray r8 = r14.getJSONArray(r8)     // Catch:{ all -> 0x0354 }
            r64 = r14
            int r14 = r8.length()     // Catch:{ all -> 0x0354 }
            java.lang.String[] r14 = new java.lang.String[r14]     // Catch:{ all -> 0x0354 }
            r30 = 0
            r65 = r3
            r3 = r30
        L_0x0611:
            int r4 = r14.length     // Catch:{ all -> 0x0354 }
            if (r3 >= r4) goto L_0x061d
            java.lang.String r4 = r8.getString(r3)     // Catch:{ all -> 0x0354 }
            r14[r3] = r4     // Catch:{ all -> 0x0354 }
            int r3 = r3 + 1
            goto L_0x0611
        L_0x061d:
            goto L_0x0624
        L_0x061e:
            r65 = r3
            r64 = r14
            r3 = 0
            r14 = r3
        L_0x0624:
            r3 = 0
            r4 = 0
            r8 = 0
            r30 = r14[r8]     // Catch:{ all -> 0x23da }
            r8 = r30
            r30 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = r3
            java.lang.String r3 = "edit_date"
            boolean r39 = r6.has(r3)     // Catch:{ all -> 0x23da }
            java.lang.String r3 = "CHAT_"
            boolean r3 = r5.startsWith(r3)     // Catch:{ all -> 0x23da }
            if (r3 == 0) goto L_0x068f
            boolean r3 = org.telegram.messenger.UserObject.isReplyUser((long) r1)     // Catch:{ all -> 0x0354 }
            if (r3 == 0) goto L_0x0671
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0354 }
            r3.<init>()     // Catch:{ all -> 0x0354 }
            r3.append(r8)     // Catch:{ all -> 0x0354 }
            r67 = r4
            java.lang.String r4 = " @ "
            r3.append(r4)     // Catch:{ all -> 0x0354 }
            r68 = r6
            r4 = 1
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3.append(r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0354 }
            r8 = r3
            r3 = r30
            r4 = r35
            r6 = r36
            r69 = r37
            goto L_0x06cc
        L_0x0671:
            r67 = r4
            r68 = r6
            r3 = 0
            int r6 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r6 == 0) goto L_0x067d
            r3 = 1
            goto L_0x067e
        L_0x067d:
            r3 = 0
        L_0x067e:
            r35 = r3
            r30 = r8
            r3 = 1
            r4 = r14[r3]     // Catch:{ all -> 0x0354 }
            r8 = r4
            r3 = r30
            r4 = r35
            r6 = r36
            r69 = r37
            goto L_0x06cc
        L_0x068f:
            r67 = r4
            r68 = r6
            java.lang.String r3 = "PINNED_"
            boolean r3 = r5.startsWith(r3)     // Catch:{ all -> 0x23da }
            if (r3 == 0) goto L_0x06b1
            r3 = 0
            int r6 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r6 == 0) goto L_0x06a3
            r3 = 1
            goto L_0x06a4
        L_0x06a3:
            r3 = 0
        L_0x06a4:
            r35 = r3
            r36 = 1
            r3 = r30
            r4 = r35
            r6 = r36
            r69 = r37
            goto L_0x06cc
        L_0x06b1:
            java.lang.String r3 = "CHANNEL_"
            boolean r3 = r5.startsWith(r3)     // Catch:{ all -> 0x23da }
            if (r3 == 0) goto L_0x06c4
            r37 = 1
            r3 = r30
            r4 = r35
            r6 = r36
            r69 = r37
            goto L_0x06cc
        L_0x06c4:
            r3 = r30
            r4 = r35
            r6 = r36
            r69 = r37
        L_0x06cc:
            boolean r30 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x23da }
            if (r30 == 0) goto L_0x06f7
            r30 = r8
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0354 }
            r8.<init>()     // Catch:{ all -> 0x0354 }
            r70 = r3
            java.lang.String r3 = "GCM received message notification "
            r8.append(r3)     // Catch:{ all -> 0x0354 }
            r8.append(r5)     // Catch:{ all -> 0x0354 }
            r8.append(r11)     // Catch:{ all -> 0x0354 }
            r8.append(r1)     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = " mid = "
            r8.append(r3)     // Catch:{ all -> 0x0354 }
            r8.append(r7)     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = r8.toString()     // Catch:{ all -> 0x0354 }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ all -> 0x0354 }
            goto L_0x06fb
        L_0x06f7:
            r70 = r3
            r30 = r8
        L_0x06fb:
            r3 = r62
            boolean r8 = r5.startsWith(r3)     // Catch:{ all -> 0x23da }
            if (r8 != 0) goto L_0x2203
            boolean r8 = r5.startsWith(r15)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r62 = r3
            r73 = r4
            r74 = r6
            r71 = r9
            r16 = r15
            r12 = 0
            goto L_0x220e
        L_0x0716:
            int r8 = r5.hashCode()     // Catch:{ all -> 0x0354 }
            switch(r8) {
                case -2100047043: goto L_0x0c5b;
                case -2091498420: goto L_0x0CLASSNAME;
                case -2053872415: goto L_0x0CLASSNAME;
                case -2039746363: goto L_0x0c3a;
                case -2023218804: goto L_0x0c2f;
                case -1979538588: goto L_0x0CLASSNAME;
                case -1979536003: goto L_0x0CLASSNAME;
                case -1979535888: goto L_0x0c0e;
                case -1969004705: goto L_0x0CLASSNAME;
                case -1946699248: goto L_0x0bf7;
                case -1717283471: goto L_0x0beb;
                case -1646640058: goto L_0x0bdf;
                case -1528047021: goto L_0x0bd3;
                case -1493579426: goto L_0x0bc7;
                case -1482481933: goto L_0x0bbb;
                case -1480102982: goto L_0x0bb0;
                case -1478041834: goto L_0x0ba4;
                case -1474543101: goto L_0x0b99;
                case -1465695932: goto L_0x0b8d;
                case -1374906292: goto L_0x0b81;
                case -1372940586: goto L_0x0b75;
                case -1264245338: goto L_0x0b69;
                case -1236154001: goto L_0x0b5d;
                case -1236086700: goto L_0x0b51;
                case -1236077786: goto L_0x0b45;
                case -1235796237: goto L_0x0b39;
                case -1235760759: goto L_0x0b2d;
                case -1235686303: goto L_0x0b22;
                case -1198046100: goto L_0x0b17;
                case -1124254527: goto L_0x0b0b;
                case -1085137927: goto L_0x0aff;
                case -1084856378: goto L_0x0af3;
                case -1084820900: goto L_0x0ae7;
                case -1084746444: goto L_0x0adb;
                case -819729482: goto L_0x0acf;
                case -772141857: goto L_0x0ac3;
                case -638310039: goto L_0x0ab7;
                case -590403924: goto L_0x0aab;
                case -589196239: goto L_0x0a9f;
                case -589193654: goto L_0x0a93;
                case -589193539: goto L_0x0a87;
                case -440169325: goto L_0x0a7b;
                case -412748110: goto L_0x0a6f;
                case -228518075: goto L_0x0a63;
                case -213586509: goto L_0x0a57;
                case -115582002: goto L_0x0a4b;
                case -112621464: goto L_0x0a3f;
                case -108522133: goto L_0x0a33;
                case -107572034: goto L_0x0a28;
                case -40534265: goto L_0x0a1c;
                case 52369421: goto L_0x0a10;
                case 65254746: goto L_0x0a04;
                case 141040782: goto L_0x09f8;
                case 202550149: goto L_0x09ec;
                case 309993049: goto L_0x09e0;
                case 309995634: goto L_0x09d4;
                case 309995749: goto L_0x09c8;
                case 320532812: goto L_0x09bc;
                case 328933854: goto L_0x09b0;
                case 331340546: goto L_0x09a4;
                case 342406591: goto L_0x0998;
                case 344816990: goto L_0x098c;
                case 346878138: goto L_0x0980;
                case 350376871: goto L_0x0974;
                case 608430149: goto L_0x0968;
                case 615714517: goto L_0x095d;
                case 715508879: goto L_0x0951;
                case 728985323: goto L_0x0945;
                case 731046471: goto L_0x0939;
                case 734545204: goto L_0x092d;
                case 802032552: goto L_0x0921;
                case 991498806: goto L_0x0915;
                case 1007364121: goto L_0x0909;
                case 1019850010: goto L_0x08fd;
                case 1019917311: goto L_0x08f1;
                case 1019926225: goto L_0x08e5;
                case 1020207774: goto L_0x08d9;
                case 1020243252: goto L_0x08cd;
                case 1020317708: goto L_0x08c1;
                case 1060282259: goto L_0x08b5;
                case 1060349560: goto L_0x08a9;
                case 1060358474: goto L_0x089d;
                case 1060640023: goto L_0x0891;
                case 1060675501: goto L_0x0885;
                case 1060749957: goto L_0x087a;
                case 1073049781: goto L_0x086e;
                case 1078101399: goto L_0x0862;
                case 1110103437: goto L_0x0856;
                case 1160762272: goto L_0x084a;
                case 1172918249: goto L_0x083e;
                case 1234591620: goto L_0x0832;
                case 1281128640: goto L_0x0826;
                case 1281131225: goto L_0x081a;
                case 1281131340: goto L_0x080e;
                case 1310789062: goto L_0x0803;
                case 1333118583: goto L_0x07f7;
                case 1361447897: goto L_0x07eb;
                case 1498266155: goto L_0x07df;
                case 1533804208: goto L_0x07d3;
                case 1540131626: goto L_0x07c7;
                case 1547988151: goto L_0x07bb;
                case 1561464595: goto L_0x07af;
                case 1563525743: goto L_0x07a3;
                case 1567024476: goto L_0x0797;
                case 1810705077: goto L_0x078b;
                case 1815177512: goto L_0x077f;
                case 1954774321: goto L_0x0773;
                case 1963241394: goto L_0x0767;
                case 2014789757: goto L_0x075b;
                case 2022049433: goto L_0x074f;
                case 2034984710: goto L_0x0743;
                case 2048733346: goto L_0x0737;
                case 2099392181: goto L_0x072b;
                case 2140162142: goto L_0x071f;
                default: goto L_0x071d;
            }     // Catch:{ all -> 0x0354 }
        L_0x071d:
            goto L_0x0CLASSNAME
        L_0x071f:
            java.lang.String r8 = "CHAT_MESSAGE_GEOLIVE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 60
            goto L_0x0CLASSNAME
        L_0x072b:
            java.lang.String r8 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 43
            goto L_0x0CLASSNAME
        L_0x0737:
            java.lang.String r8 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 28
            goto L_0x0CLASSNAME
        L_0x0743:
            java.lang.String r8 = "CHANNEL_MESSAGE_PLAYLIST"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 45
            goto L_0x0CLASSNAME
        L_0x074f:
            java.lang.String r8 = "PINNED_CONTACT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 94
            goto L_0x0CLASSNAME
        L_0x075b:
            java.lang.String r8 = "CHAT_PHOTO_EDITED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 68
            goto L_0x0CLASSNAME
        L_0x0767:
            java.lang.String r8 = "LOCKED_MESSAGE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 108(0x6c, float:1.51E-43)
            goto L_0x0CLASSNAME
        L_0x0773:
            java.lang.String r8 = "CHAT_MESSAGE_PLAYLIST"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 83
            goto L_0x0CLASSNAME
        L_0x077f:
            java.lang.String r8 = "CHANNEL_MESSAGES"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 47
            goto L_0x0CLASSNAME
        L_0x078b:
            java.lang.String r8 = "MESSAGE_INVOICE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 21
            goto L_0x0CLASSNAME
        L_0x0797:
            java.lang.String r8 = "CHAT_MESSAGE_VIDEO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 51
            goto L_0x0CLASSNAME
        L_0x07a3:
            java.lang.String r8 = "CHAT_MESSAGE_ROUND"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 52
            goto L_0x0CLASSNAME
        L_0x07af:
            java.lang.String r8 = "CHAT_MESSAGE_PHOTO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 50
            goto L_0x0CLASSNAME
        L_0x07bb:
            java.lang.String r8 = "CHAT_MESSAGE_AUDIO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 55
            goto L_0x0CLASSNAME
        L_0x07c7:
            java.lang.String r8 = "MESSAGE_PLAYLIST"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 25
            goto L_0x0CLASSNAME
        L_0x07d3:
            java.lang.String r8 = "MESSAGE_VIDEOS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 24
            goto L_0x0CLASSNAME
        L_0x07df:
            java.lang.String r8 = "PHONE_CALL_MISSED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 113(0x71, float:1.58E-43)
            goto L_0x0CLASSNAME
        L_0x07eb:
            java.lang.String r8 = "MESSAGE_PHOTOS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 23
            goto L_0x0CLASSNAME
        L_0x07f7:
            java.lang.String r8 = "CHAT_MESSAGE_VIDEOS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 82
            goto L_0x0CLASSNAME
        L_0x0803:
            java.lang.String r8 = "MESSAGE_NOTEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 2
            goto L_0x0CLASSNAME
        L_0x080e:
            java.lang.String r8 = "MESSAGE_GIF"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 17
            goto L_0x0CLASSNAME
        L_0x081a:
            java.lang.String r8 = "MESSAGE_GEO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 15
            goto L_0x0CLASSNAME
        L_0x0826:
            java.lang.String r8 = "MESSAGE_DOC"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 9
            goto L_0x0CLASSNAME
        L_0x0832:
            java.lang.String r8 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 63
            goto L_0x0CLASSNAME
        L_0x083e:
            java.lang.String r8 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 39
            goto L_0x0CLASSNAME
        L_0x084a:
            java.lang.String r8 = "CHAT_MESSAGE_PHOTOS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 81
            goto L_0x0CLASSNAME
        L_0x0856:
            java.lang.String r8 = "CHAT_MESSAGE_NOTEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 49
            goto L_0x0CLASSNAME
        L_0x0862:
            java.lang.String r8 = "CHAT_TITLE_EDITED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 67
            goto L_0x0CLASSNAME
        L_0x086e:
            java.lang.String r8 = "PINNED_NOTEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 87
            goto L_0x0CLASSNAME
        L_0x087a:
            java.lang.String r8 = "MESSAGE_TEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 0
            goto L_0x0CLASSNAME
        L_0x0885:
            java.lang.String r8 = "MESSAGE_QUIZ"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 13
            goto L_0x0CLASSNAME
        L_0x0891:
            java.lang.String r8 = "MESSAGE_POLL"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 14
            goto L_0x0CLASSNAME
        L_0x089d:
            java.lang.String r8 = "MESSAGE_GAME"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 18
            goto L_0x0CLASSNAME
        L_0x08a9:
            java.lang.String r8 = "MESSAGE_FWDS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 22
            goto L_0x0CLASSNAME
        L_0x08b5:
            java.lang.String r8 = "MESSAGE_DOCS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 26
            goto L_0x0CLASSNAME
        L_0x08c1:
            java.lang.String r8 = "CHAT_MESSAGE_TEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 48
            goto L_0x0CLASSNAME
        L_0x08cd:
            java.lang.String r8 = "CHAT_MESSAGE_QUIZ"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 57
            goto L_0x0CLASSNAME
        L_0x08d9:
            java.lang.String r8 = "CHAT_MESSAGE_POLL"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 58
            goto L_0x0CLASSNAME
        L_0x08e5:
            java.lang.String r8 = "CHAT_MESSAGE_GAME"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 62
            goto L_0x0CLASSNAME
        L_0x08f1:
            java.lang.String r8 = "CHAT_MESSAGE_FWDS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 80
            goto L_0x0CLASSNAME
        L_0x08fd:
            java.lang.String r8 = "CHAT_MESSAGE_DOCS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 84
            goto L_0x0CLASSNAME
        L_0x0909:
            java.lang.String r8 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 20
            goto L_0x0CLASSNAME
        L_0x0915:
            java.lang.String r8 = "PINNED_GEOLIVE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 98
            goto L_0x0CLASSNAME
        L_0x0921:
            java.lang.String r8 = "MESSAGE_CONTACT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 12
            goto L_0x0CLASSNAME
        L_0x092d:
            java.lang.String r8 = "PINNED_VIDEO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 89
            goto L_0x0CLASSNAME
        L_0x0939:
            java.lang.String r8 = "PINNED_ROUND"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 90
            goto L_0x0CLASSNAME
        L_0x0945:
            java.lang.String r8 = "PINNED_PHOTO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 88
            goto L_0x0CLASSNAME
        L_0x0951:
            java.lang.String r8 = "PINNED_AUDIO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 93
            goto L_0x0CLASSNAME
        L_0x095d:
            java.lang.String r8 = "MESSAGE_PHOTO_SECRET"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 4
            goto L_0x0CLASSNAME
        L_0x0968:
            java.lang.String r8 = "CHAT_VOICECHAT_INVITE_YOU"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 73
            goto L_0x0CLASSNAME
        L_0x0974:
            java.lang.String r8 = "CHANNEL_MESSAGE_VIDEO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 30
            goto L_0x0CLASSNAME
        L_0x0980:
            java.lang.String r8 = "CHANNEL_MESSAGE_ROUND"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 31
            goto L_0x0CLASSNAME
        L_0x098c:
            java.lang.String r8 = "CHANNEL_MESSAGE_PHOTO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 29
            goto L_0x0CLASSNAME
        L_0x0998:
            java.lang.String r8 = "CHAT_VOICECHAT_END"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 72
            goto L_0x0CLASSNAME
        L_0x09a4:
            java.lang.String r8 = "CHANNEL_MESSAGE_AUDIO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 34
            goto L_0x0CLASSNAME
        L_0x09b0:
            java.lang.String r8 = "CHAT_MESSAGE_STICKER"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 54
            goto L_0x0CLASSNAME
        L_0x09bc:
            java.lang.String r8 = "MESSAGES"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 27
            goto L_0x0CLASSNAME
        L_0x09c8:
            java.lang.String r8 = "CHAT_MESSAGE_GIF"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 61
            goto L_0x0CLASSNAME
        L_0x09d4:
            java.lang.String r8 = "CHAT_MESSAGE_GEO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 59
            goto L_0x0CLASSNAME
        L_0x09e0:
            java.lang.String r8 = "CHAT_MESSAGE_DOC"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 53
            goto L_0x0CLASSNAME
        L_0x09ec:
            java.lang.String r8 = "CHAT_VOICECHAT_INVITE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 71
            goto L_0x0CLASSNAME
        L_0x09f8:
            java.lang.String r8 = "CHAT_LEFT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 76
            goto L_0x0CLASSNAME
        L_0x0a04:
            java.lang.String r8 = "CHAT_ADD_YOU"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 66
            goto L_0x0CLASSNAME
        L_0x0a10:
            java.lang.String r8 = "REACT_TEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 104(0x68, float:1.46E-43)
            goto L_0x0CLASSNAME
        L_0x0a1c:
            java.lang.String r8 = "CHAT_DELETE_MEMBER"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 74
            goto L_0x0CLASSNAME
        L_0x0a28:
            java.lang.String r8 = "MESSAGE_SCREENSHOT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 7
            goto L_0x0CLASSNAME
        L_0x0a33:
            java.lang.String r8 = "AUTH_REGION"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 107(0x6b, float:1.5E-43)
            goto L_0x0CLASSNAME
        L_0x0a3f:
            java.lang.String r8 = "CONTACT_JOINED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 105(0x69, float:1.47E-43)
            goto L_0x0CLASSNAME
        L_0x0a4b:
            java.lang.String r8 = "CHAT_MESSAGE_INVOICE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 64
            goto L_0x0CLASSNAME
        L_0x0a57:
            java.lang.String r8 = "ENCRYPTION_REQUEST"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 109(0x6d, float:1.53E-43)
            goto L_0x0CLASSNAME
        L_0x0a63:
            java.lang.String r8 = "MESSAGE_GEOLIVE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 16
            goto L_0x0CLASSNAME
        L_0x0a6f:
            java.lang.String r8 = "CHAT_DELETE_YOU"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 75
            goto L_0x0CLASSNAME
        L_0x0a7b:
            java.lang.String r8 = "AUTH_UNKNOWN"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 106(0x6a, float:1.49E-43)
            goto L_0x0CLASSNAME
        L_0x0a87:
            java.lang.String r8 = "PINNED_GIF"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 102(0x66, float:1.43E-43)
            goto L_0x0CLASSNAME
        L_0x0a93:
            java.lang.String r8 = "PINNED_GEO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 97
            goto L_0x0CLASSNAME
        L_0x0a9f:
            java.lang.String r8 = "PINNED_DOC"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 91
            goto L_0x0CLASSNAME
        L_0x0aab:
            java.lang.String r8 = "PINNED_GAME_SCORE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 100
            goto L_0x0CLASSNAME
        L_0x0ab7:
            java.lang.String r8 = "CHANNEL_MESSAGE_STICKER"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 33
            goto L_0x0CLASSNAME
        L_0x0ac3:
            java.lang.String r8 = "PHONE_CALL_REQUEST"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 111(0x6f, float:1.56E-43)
            goto L_0x0CLASSNAME
        L_0x0acf:
            java.lang.String r8 = "PINNED_STICKER"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 92
            goto L_0x0CLASSNAME
        L_0x0adb:
            java.lang.String r8 = "PINNED_TEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 86
            goto L_0x0CLASSNAME
        L_0x0ae7:
            java.lang.String r8 = "PINNED_QUIZ"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 95
            goto L_0x0CLASSNAME
        L_0x0af3:
            java.lang.String r8 = "PINNED_POLL"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 96
            goto L_0x0CLASSNAME
        L_0x0aff:
            java.lang.String r8 = "PINNED_GAME"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 99
            goto L_0x0CLASSNAME
        L_0x0b0b:
            java.lang.String r8 = "CHAT_MESSAGE_CONTACT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 56
            goto L_0x0CLASSNAME
        L_0x0b17:
            java.lang.String r8 = "MESSAGE_VIDEO_SECRET"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 6
            goto L_0x0CLASSNAME
        L_0x0b22:
            java.lang.String r8 = "CHANNEL_MESSAGE_TEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 1
            goto L_0x0CLASSNAME
        L_0x0b2d:
            java.lang.String r8 = "CHANNEL_MESSAGE_QUIZ"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 36
            goto L_0x0CLASSNAME
        L_0x0b39:
            java.lang.String r8 = "CHANNEL_MESSAGE_POLL"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 37
            goto L_0x0CLASSNAME
        L_0x0b45:
            java.lang.String r8 = "CHANNEL_MESSAGE_GAME"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 41
            goto L_0x0CLASSNAME
        L_0x0b51:
            java.lang.String r8 = "CHANNEL_MESSAGE_FWDS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 42
            goto L_0x0CLASSNAME
        L_0x0b5d:
            java.lang.String r8 = "CHANNEL_MESSAGE_DOCS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 46
            goto L_0x0CLASSNAME
        L_0x0b69:
            java.lang.String r8 = "PINNED_INVOICE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 101(0x65, float:1.42E-43)
            goto L_0x0CLASSNAME
        L_0x0b75:
            java.lang.String r8 = "CHAT_RETURNED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 77
            goto L_0x0CLASSNAME
        L_0x0b81:
            java.lang.String r8 = "ENCRYPTED_MESSAGE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 103(0x67, float:1.44E-43)
            goto L_0x0CLASSNAME
        L_0x0b8d:
            java.lang.String r8 = "ENCRYPTION_ACCEPT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 110(0x6e, float:1.54E-43)
            goto L_0x0CLASSNAME
        L_0x0b99:
            java.lang.String r8 = "MESSAGE_VIDEO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 5
            goto L_0x0CLASSNAME
        L_0x0ba4:
            java.lang.String r8 = "MESSAGE_ROUND"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 8
            goto L_0x0CLASSNAME
        L_0x0bb0:
            java.lang.String r8 = "MESSAGE_PHOTO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 3
            goto L_0x0CLASSNAME
        L_0x0bbb:
            java.lang.String r8 = "MESSAGE_MUTED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 112(0x70, float:1.57E-43)
            goto L_0x0CLASSNAME
        L_0x0bc7:
            java.lang.String r8 = "MESSAGE_AUDIO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 11
            goto L_0x0CLASSNAME
        L_0x0bd3:
            java.lang.String r8 = "CHAT_MESSAGES"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 85
            goto L_0x0CLASSNAME
        L_0x0bdf:
            java.lang.String r8 = "CHAT_VOICECHAT_START"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 70
            goto L_0x0CLASSNAME
        L_0x0beb:
            java.lang.String r8 = "CHAT_REQ_JOINED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 79
            goto L_0x0CLASSNAME
        L_0x0bf7:
            java.lang.String r8 = "CHAT_JOINED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 78
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.lang.String r8 = "CHAT_ADD_MEMBER"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 69
            goto L_0x0CLASSNAME
        L_0x0c0e:
            java.lang.String r8 = "CHANNEL_MESSAGE_GIF"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 40
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.lang.String r8 = "CHANNEL_MESSAGE_GEO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 38
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.lang.String r8 = "CHANNEL_MESSAGE_DOC"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 32
            goto L_0x0CLASSNAME
        L_0x0c2f:
            java.lang.String r8 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 44
            goto L_0x0CLASSNAME
        L_0x0c3a:
            java.lang.String r8 = "MESSAGE_STICKER"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 10
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.lang.String r8 = "CHAT_CREATED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 65
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.lang.String r8 = "CHANNEL_MESSAGE_CONTACT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 35
            goto L_0x0CLASSNAME
        L_0x0c5b:
            java.lang.String r8 = "MESSAGE_GAME_SCORE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x071d
            r8 = 19
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r8 = -1
        L_0x0CLASSNAME:
            java.lang.String r11 = "Videos"
            r16 = r15
            java.lang.String r15 = "Photos"
            r62 = r3
            java.lang.String r3 = " "
            r71 = r9
            java.lang.String r10 = "NotificationGroupFew"
            java.lang.String r9 = "NotificationMessageFew"
            r73 = r4
            java.lang.String r4 = "ChannelMessageFew"
            r74 = r6
            java.lang.String r6 = "AttachSticker"
            switch(r8) {
                case 0: goto L_0x21c2;
                case 1: goto L_0x21c2;
                case 2: goto L_0x219d;
                case 3: goto L_0x2177;
                case 4: goto L_0x2151;
                case 5: goto L_0x212b;
                case 6: goto L_0x2105;
                case 7: goto L_0x20e9;
                case 8: goto L_0x20c3;
                case 9: goto L_0x209d;
                case 10: goto L_0x2030;
                case 11: goto L_0x200a;
                case 12: goto L_0x1fdf;
                case 13: goto L_0x1fb4;
                case 14: goto L_0x1var_;
                case 15: goto L_0x1var_;
                case 16: goto L_0x1f3d;
                case 17: goto L_0x1var_;
                case 18: goto L_0x1eec;
                case 19: goto L_0x1ec7;
                case 20: goto L_0x1ec7;
                case 21: goto L_0x1e9c;
                case 22: goto L_0x1e6d;
                case 23: goto L_0x1e40;
                case 24: goto L_0x1e13;
                case 25: goto L_0x1de4;
                case 26: goto L_0x1db5;
                case 27: goto L_0x1d98;
                case 28: goto L_0x1d72;
                case 29: goto L_0x1d4c;
                case 30: goto L_0x1d26;
                case 31: goto L_0x1d00;
                case 32: goto L_0x1cda;
                case 33: goto L_0x1c6d;
                case 34: goto L_0x1CLASSNAME;
                case 35: goto L_0x1c1c;
                case 36: goto L_0x1bf1;
                case 37: goto L_0x1bc6;
                case 38: goto L_0x1ba0;
                case 39: goto L_0x1b7a;
                case 40: goto L_0x1b54;
                case 41: goto L_0x1b2e;
                case 42: goto L_0x1afb;
                case 43: goto L_0x1ace;
                case 44: goto L_0x1aa1;
                case 45: goto L_0x1a72;
                case 46: goto L_0x1a43;
                case 47: goto L_0x1a26;
                case 48: goto L_0x19fd;
                case 49: goto L_0x19d2;
                case 50: goto L_0x19a7;
                case 51: goto L_0x197c;
                case 52: goto L_0x1951;
                case 53: goto L_0x1926;
                case 54: goto L_0x189b;
                case 55: goto L_0x1870;
                case 56: goto L_0x1840;
                case 57: goto L_0x1810;
                case 58: goto L_0x17e0;
                case 59: goto L_0x17b5;
                case 60: goto L_0x178a;
                case 61: goto L_0x175f;
                case 62: goto L_0x172f;
                case 63: goto L_0x1705;
                case 64: goto L_0x16d5;
                case 65: goto L_0x16b5;
                case 66: goto L_0x16b5;
                case 67: goto L_0x1695;
                case 68: goto L_0x1675;
                case 69: goto L_0x1650;
                case 70: goto L_0x1630;
                case 71: goto L_0x160b;
                case 72: goto L_0x15eb;
                case 73: goto L_0x15cb;
                case 74: goto L_0x15ab;
                case 75: goto L_0x158b;
                case 76: goto L_0x156b;
                case 77: goto L_0x154b;
                case 78: goto L_0x152b;
                case 79: goto L_0x150b;
                case 80: goto L_0x14d7;
                case 81: goto L_0x14a5;
                case 82: goto L_0x1473;
                case 83: goto L_0x143f;
                case 84: goto L_0x140b;
                case 85: goto L_0x13e9;
                case 86: goto L_0x137c;
                case 87: goto L_0x1319;
                case 88: goto L_0x12b6;
                case 89: goto L_0x1253;
                case 90: goto L_0x11f0;
                case 91: goto L_0x118d;
                case 92: goto L_0x10a6;
                case 93: goto L_0x1043;
                case 94: goto L_0x0fd6;
                case 95: goto L_0x0var_;
                case 96: goto L_0x0efc;
                case 97: goto L_0x0e99;
                case 98: goto L_0x0e36;
                case 99: goto L_0x0dd3;
                case 100: goto L_0x0d70;
                case 101: goto L_0x0d0d;
                case 102: goto L_0x0caa;
                case 103: goto L_0x0c8d;
                case 104: goto L_0x0c8a;
                case 105: goto L_0x0CLASSNAME;
                case 106: goto L_0x0CLASSNAME;
                case 107: goto L_0x0CLASSNAME;
                case 108: goto L_0x0CLASSNAME;
                case 109: goto L_0x0CLASSNAME;
                case 110: goto L_0x0CLASSNAME;
                case 111: goto L_0x0CLASSNAME;
                case 112: goto L_0x0CLASSNAME;
                case 113: goto L_0x0CLASSNAME;
                default: goto L_0x0CLASSNAME;
            }
        L_0x0CLASSNAME:
            r12 = 0
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0354 }
            goto L_0x21e4
        L_0x0CLASSNAME:
            r12 = 0
            goto L_0x21fa
        L_0x0c8a:
            r12 = 0
            goto L_0x21fa
        L_0x0c8d:
            java.lang.String r3 = "YouHaveNewMessage"
            r4 = 2131629035(0x7f0e13eb, float:1.888538E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "SecretChatName"
            r6 = 2131627935(0x7f0e0f9f, float:1.8883148E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r8 = r4
            r34 = 1
            r12 = 0
            r4 = r3
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0caa:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x0cd0
            java.lang.String r3 = "NotificationActionPinnedGifUser"
            r4 = 2131626751(0x7f0e0aff, float:1.8880747E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0cd0:
            if (r13 == 0) goto L_0x0cf2
            java.lang.String r3 = "NotificationActionPinnedGif"
            r4 = 2131626749(0x7f0e0afd, float:1.8880743E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0cf2:
            java.lang.String r3 = "NotificationActionPinnedGifChannel"
            r4 = 2131626750(0x7f0e0afe, float:1.8880745E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0d0d:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x0d33
            java.lang.String r3 = "NotificationActionPinnedInvoiceUser"
            r4 = 2131626754(0x7f0e0b02, float:1.8880753E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0d33:
            if (r13 == 0) goto L_0x0d55
            java.lang.String r3 = "NotificationActionPinnedInvoice"
            r4 = 2131626752(0x7f0e0b00, float:1.888075E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0d55:
            java.lang.String r3 = "NotificationActionPinnedInvoiceChannel"
            r4 = 2131626753(0x7f0e0b01, float:1.8880751E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0d70:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x0d96
            java.lang.String r3 = "NotificationActionPinnedGameScoreUser"
            r4 = 2131626741(0x7f0e0af5, float:1.8880727E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0d96:
            if (r13 == 0) goto L_0x0db8
            java.lang.String r3 = "NotificationActionPinnedGameScore"
            r4 = 2131626739(0x7f0e0af3, float:1.8880723E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0db8:
            java.lang.String r3 = "NotificationActionPinnedGameScoreChannel"
            r4 = 2131626740(0x7f0e0af4, float:1.8880725E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0dd3:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x0df9
            java.lang.String r3 = "NotificationActionPinnedGameUser"
            r4 = 2131626742(0x7f0e0af6, float:1.8880729E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0df9:
            if (r13 == 0) goto L_0x0e1b
            java.lang.String r3 = "NotificationActionPinnedGame"
            r4 = 2131626737(0x7f0e0af1, float:1.8880719E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0e1b:
            java.lang.String r3 = "NotificationActionPinnedGameChannel"
            r4 = 2131626738(0x7f0e0af2, float:1.888072E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0e36:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x0e5c
            java.lang.String r3 = "NotificationActionPinnedGeoLiveUser"
            r4 = 2131626747(0x7f0e0afb, float:1.8880739E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0e5c:
            if (r13 == 0) goto L_0x0e7e
            java.lang.String r3 = "NotificationActionPinnedGeoLive"
            r4 = 2131626745(0x7f0e0af9, float:1.8880735E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0e7e:
            java.lang.String r3 = "NotificationActionPinnedGeoLiveChannel"
            r4 = 2131626746(0x7f0e0afa, float:1.8880737E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0e99:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x0ebf
            java.lang.String r3 = "NotificationActionPinnedGeoUser"
            r4 = 2131626748(0x7f0e0afc, float:1.888074E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0ebf:
            if (r13 == 0) goto L_0x0ee1
            java.lang.String r3 = "NotificationActionPinnedGeo"
            r4 = 2131626743(0x7f0e0af7, float:1.888073E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0ee1:
            java.lang.String r3 = "NotificationActionPinnedGeoChannel"
            r4 = 2131626744(0x7f0e0af8, float:1.8880733E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0efc:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x0var_
            java.lang.String r3 = "NotificationActionPinnedPollUser"
            r4 = 2131626766(0x7f0e0b0e, float:1.8880777E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0var_:
            if (r13 == 0) goto L_0x0var_
            java.lang.String r3 = "NotificationActionPinnedPoll2"
            r4 = 2131626764(0x7f0e0b0c, float:1.8880773E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 2
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r10 = 1
            r6[r10] = r9     // Catch:{ all -> 0x0354 }
            r9 = r14[r10]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0var_:
            java.lang.String r3 = "NotificationActionPinnedPollChannel2"
            r4 = 2131626765(0x7f0e0b0d, float:1.8880775E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0var_:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x0f8f
            java.lang.String r3 = "NotificationActionPinnedQuizUser"
            r4 = 2131626769(0x7f0e0b11, float:1.8880784E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0f8f:
            if (r13 == 0) goto L_0x0fb6
            java.lang.String r3 = "NotificationActionPinnedQuiz2"
            r4 = 2131626767(0x7f0e0b0f, float:1.888078E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 2
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r10 = 1
            r6[r10] = r9     // Catch:{ all -> 0x0354 }
            r9 = r14[r10]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0fb6:
            java.lang.String r3 = "NotificationActionPinnedQuizChannel2"
            r4 = 2131626768(0x7f0e0b10, float:1.8880782E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0fd6:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x0ffc
            java.lang.String r3 = "NotificationActionPinnedContactUser"
            r4 = 2131626733(0x7f0e0aed, float:1.888071E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x0ffc:
            if (r13 == 0) goto L_0x1023
            java.lang.String r3 = "NotificationActionPinnedContact2"
            r4 = 2131626731(0x7f0e0aeb, float:1.8880706E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 2
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r10 = 1
            r6[r10] = r9     // Catch:{ all -> 0x0354 }
            r9 = r14[r10]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1023:
            java.lang.String r3 = "NotificationActionPinnedContactChannel2"
            r4 = 2131626732(0x7f0e0aec, float:1.8880709E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1043:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x1069
            java.lang.String r3 = "NotificationActionPinnedVoiceUser"
            r4 = 2131626787(0x7f0e0b23, float:1.888082E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1069:
            if (r13 == 0) goto L_0x108b
            java.lang.String r3 = "NotificationActionPinnedVoice"
            r4 = 2131626785(0x7f0e0b21, float:1.8880816E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x108b:
            java.lang.String r3 = "NotificationActionPinnedVoiceChannel"
            r4 = 2131626786(0x7f0e0b22, float:1.8880818E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x10a6:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x10f3
            int r3 = r14.length     // Catch:{ all -> 0x0354 }
            r4 = 1
            if (r3 <= r4) goto L_0x10d8
            r3 = r14[r4]     // Catch:{ all -> 0x0354 }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0354 }
            if (r3 != 0) goto L_0x10d8
            java.lang.String r3 = "NotificationActionPinnedStickerEmojiUser"
            r4 = 2131626777(0x7f0e0b19, float:1.88808E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x10d8:
            java.lang.String r3 = "NotificationActionPinnedStickerUser"
            r4 = 2131626778(0x7f0e0b1a, float:1.8880802E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x10f3:
            if (r13 == 0) goto L_0x1146
            int r3 = r14.length     // Catch:{ all -> 0x0354 }
            r4 = 2
            if (r3 <= r4) goto L_0x1126
            r3 = r14[r4]     // Catch:{ all -> 0x0354 }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0354 }
            if (r3 != 0) goto L_0x1126
            java.lang.String r3 = "NotificationActionPinnedStickerEmoji"
            r4 = 2131626775(0x7f0e0b17, float:1.8880796E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 2
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r10 = 1
            r6[r10] = r9     // Catch:{ all -> 0x0354 }
            r9 = r14[r10]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1126:
            java.lang.String r3 = "NotificationActionPinnedSticker"
            r4 = 2131626773(0x7f0e0b15, float:1.8880792E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1146:
            int r3 = r14.length     // Catch:{ all -> 0x0354 }
            r4 = 1
            if (r3 <= r4) goto L_0x1172
            r3 = r14[r4]     // Catch:{ all -> 0x0354 }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0354 }
            if (r3 != 0) goto L_0x1172
            java.lang.String r3 = "NotificationActionPinnedStickerEmojiChannel"
            r4 = 2131626776(0x7f0e0b18, float:1.8880798E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1172:
            java.lang.String r3 = "NotificationActionPinnedStickerChannel"
            r4 = 2131626774(0x7f0e0b16, float:1.8880794E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x118d:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x11b3
            java.lang.String r3 = "NotificationActionPinnedFileUser"
            r4 = 2131626736(0x7f0e0af0, float:1.8880717E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x11b3:
            if (r13 == 0) goto L_0x11d5
            java.lang.String r3 = "NotificationActionPinnedFile"
            r4 = 2131626734(0x7f0e0aee, float:1.8880713E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x11d5:
            java.lang.String r3 = "NotificationActionPinnedFileChannel"
            r4 = 2131626735(0x7f0e0aef, float:1.8880715E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x11f0:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x1216
            java.lang.String r3 = "NotificationActionPinnedRoundUser"
            r4 = 2131626772(0x7f0e0b14, float:1.888079E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1216:
            if (r13 == 0) goto L_0x1238
            java.lang.String r3 = "NotificationActionPinnedRound"
            r4 = 2131626770(0x7f0e0b12, float:1.8880786E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1238:
            java.lang.String r3 = "NotificationActionPinnedRoundChannel"
            r4 = 2131626771(0x7f0e0b13, float:1.8880788E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1253:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x1279
            java.lang.String r3 = "NotificationActionPinnedVideoUser"
            r4 = 2131626784(0x7f0e0b20, float:1.8880814E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1279:
            if (r13 == 0) goto L_0x129b
            java.lang.String r3 = "NotificationActionPinnedVideo"
            r4 = 2131626782(0x7f0e0b1e, float:1.888081E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x129b:
            java.lang.String r3 = "NotificationActionPinnedVideoChannel"
            r4 = 2131626783(0x7f0e0b1f, float:1.8880812E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x12b6:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x12dc
            java.lang.String r3 = "NotificationActionPinnedPhotoUser"
            r4 = 2131626763(0x7f0e0b0b, float:1.8880771E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x12dc:
            if (r13 == 0) goto L_0x12fe
            java.lang.String r3 = "NotificationActionPinnedPhoto"
            r4 = 2131626761(0x7f0e0b09, float:1.8880767E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x12fe:
            java.lang.String r3 = "NotificationActionPinnedPhotoChannel"
            r4 = 2131626762(0x7f0e0b0a, float:1.888077E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1319:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x133f
            java.lang.String r3 = "NotificationActionPinnedNoTextUser"
            r4 = 2131626760(0x7f0e0b08, float:1.8880765E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x133f:
            if (r13 == 0) goto L_0x1361
            java.lang.String r3 = "NotificationActionPinnedNoText"
            r4 = 2131626758(0x7f0e0b06, float:1.8880761E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1361:
            java.lang.String r3 = "NotificationActionPinnedNoTextChannel"
            r4 = 2131626759(0x7f0e0b07, float:1.8880763E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x137c:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x13a2
            java.lang.String r3 = "NotificationActionPinnedTextUser"
            r4 = 2131626781(0x7f0e0b1d, float:1.8880808E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x13a2:
            if (r13 == 0) goto L_0x13c9
            java.lang.String r3 = "NotificationActionPinnedText"
            r4 = 2131626779(0x7f0e0b1b, float:1.8880804E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 2
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x13c9:
            java.lang.String r3 = "NotificationActionPinnedTextChannel"
            r4 = 2131626780(0x7f0e0b1c, float:1.8880806E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x13e9:
            java.lang.String r3 = "NotificationGroupAlbum"
            r4 = 2131626796(0x7f0e0b2c, float:1.8880838E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x140b:
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r4 = 0
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r6     // Catch:{ all -> 0x0354 }
            r4 = 1
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r6     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Files"
            r6 = 2
            r8 = r14[r6]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ all -> 0x0354 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r6)     // Catch:{ all -> 0x0354 }
            r6 = 2
            r3[r6] = r4     // Catch:{ all -> 0x0354 }
            r4 = 2131626799(0x7f0e0b2f, float:1.8880844E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r10, r4, r3)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x143f:
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r4 = 0
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r6     // Catch:{ all -> 0x0354 }
            r4 = 1
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r6     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "MusicFiles"
            r6 = 2
            r8 = r14[r6]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ all -> 0x0354 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r6)     // Catch:{ all -> 0x0354 }
            r6 = 2
            r3[r6] = r4     // Catch:{ all -> 0x0354 }
            r4 = 2131626799(0x7f0e0b2f, float:1.8880844E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r10, r4, r3)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1473:
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r4 = 0
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r6     // Catch:{ all -> 0x0354 }
            r4 = 1
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r6     // Catch:{ all -> 0x0354 }
            r4 = 2
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x0354 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r11, r4)     // Catch:{ all -> 0x0354 }
            r6 = 2
            r3[r6] = r4     // Catch:{ all -> 0x0354 }
            r4 = 2131626799(0x7f0e0b2f, float:1.8880844E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r10, r4, r3)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x14a5:
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r4 = 0
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r6     // Catch:{ all -> 0x0354 }
            r4 = 1
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r6     // Catch:{ all -> 0x0354 }
            r4 = 2
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x0354 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r15, r4)     // Catch:{ all -> 0x0354 }
            r6 = 2
            r3[r6] = r4     // Catch:{ all -> 0x0354 }
            r4 = 2131626799(0x7f0e0b2f, float:1.8880844E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r10, r4, r3)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x14d7:
            java.lang.String r3 = "NotificationGroupForwardedFew"
            r4 = 2131626800(0x7f0e0b30, float:1.8880846E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 2
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r9)     // Catch:{ all -> 0x0354 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0354 }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r12, r8)     // Catch:{ all -> 0x0354 }
            r9 = 2
            r6[r9] = r8     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x150b:
            java.lang.String r3 = "UserAcceptedToGroupPushWithGroup"
            r4 = 2131628567(0x7f0e1217, float:1.888443E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x152b:
            java.lang.String r3 = "NotificationGroupAddSelfMega"
            r4 = 2131626795(0x7f0e0b2b, float:1.8880836E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x154b:
            java.lang.String r3 = "NotificationGroupAddSelf"
            r4 = 2131626794(0x7f0e0b2a, float:1.8880834E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x156b:
            java.lang.String r3 = "NotificationGroupLeftMember"
            r4 = 2131626805(0x7f0e0b35, float:1.8880857E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x158b:
            java.lang.String r3 = "NotificationGroupKickYou"
            r4 = 2131626804(0x7f0e0b34, float:1.8880855E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x15ab:
            java.lang.String r3 = "NotificationGroupKickMember"
            r4 = 2131626803(0x7f0e0b33, float:1.8880853E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x15cb:
            java.lang.String r3 = "NotificationGroupInvitedYouToCall"
            r4 = 2131626802(0x7f0e0b32, float:1.888085E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x15eb:
            java.lang.String r3 = "NotificationGroupEndedCall"
            r4 = 2131626798(0x7f0e0b2e, float:1.8880842E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x160b:
            java.lang.String r3 = "NotificationGroupInvitedToCall"
            r4 = 2131626801(0x7f0e0b31, float:1.8880848E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 2
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1630:
            java.lang.String r3 = "NotificationGroupCreatedCall"
            r4 = 2131626797(0x7f0e0b2d, float:1.888084E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1650:
            java.lang.String r3 = "NotificationGroupAddMember"
            r4 = 2131626793(0x7f0e0b29, float:1.8880832E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 2
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1675:
            java.lang.String r3 = "NotificationEditedGroupPhoto"
            r4 = 2131626791(0x7f0e0b27, float:1.8880828E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1695:
            java.lang.String r3 = "NotificationEditedGroupName"
            r4 = 2131626790(0x7f0e0b26, float:1.8880826E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x16b5:
            java.lang.String r3 = "NotificationInvitedToGroup"
            r4 = 2131626810(0x7f0e0b3a, float:1.8880867E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x16d5:
            java.lang.String r3 = "NotificationMessageGroupInvoice"
            r4 = 2131626827(0x7f0e0b4b, float:1.8880901E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 2
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "PaymentInvoice"
            r6 = 2131627235(0x7f0e0ce3, float:1.8881729E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1705:
            java.lang.String r3 = "NotificationMessageGroupGameScored"
            r4 = 2131626825(0x7f0e0b49, float:1.8880897E38)
            r6 = 4
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 2
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 3
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x172f:
            java.lang.String r3 = "NotificationMessageGroupGame"
            r4 = 2131626824(0x7f0e0b48, float:1.8880895E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 2
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachGame"
            r6 = 2131624423(0x7f0e01e7, float:1.8876025E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x175f:
            java.lang.String r3 = "NotificationMessageGroupGif"
            r4 = 2131626826(0x7f0e0b4a, float:1.88809E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachGif"
            r6 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x178a:
            java.lang.String r3 = "NotificationMessageGroupLiveLocation"
            r4 = 2131626828(0x7f0e0b4c, float:1.8880903E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachLiveLocation"
            r6 = 2131624429(0x7f0e01ed, float:1.8876037E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x17b5:
            java.lang.String r3 = "NotificationMessageGroupMap"
            r4 = 2131626829(0x7f0e0b4d, float:1.8880905E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachLocation"
            r6 = 2131624433(0x7f0e01f1, float:1.8876046E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x17e0:
            java.lang.String r3 = "NotificationMessageGroupPoll2"
            r4 = 2131626833(0x7f0e0b51, float:1.8880913E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 2
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Poll"
            r6 = 2131627414(0x7f0e0d96, float:1.8882092E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1810:
            java.lang.String r3 = "NotificationMessageGroupQuiz2"
            r4 = 2131626834(0x7f0e0b52, float:1.8880915E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 2
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "PollQuiz"
            r6 = 2131627421(0x7f0e0d9d, float:1.8882106E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1840:
            java.lang.String r3 = "NotificationMessageGroupContact2"
            r4 = 2131626822(0x7f0e0b46, float:1.8880891E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 2
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachContact"
            r6 = 2131624419(0x7f0e01e3, float:1.8876017E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1870:
            java.lang.String r3 = "NotificationMessageGroupAudio"
            r4 = 2131626821(0x7f0e0b45, float:1.888089E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachAudio"
            r6 = 2131624417(0x7f0e01e1, float:1.8876013E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x189b:
            int r4 = r14.length     // Catch:{ all -> 0x0354 }
            r8 = 2
            if (r4 <= r8) goto L_0x18e9
            r4 = r14[r8]     // Catch:{ all -> 0x0354 }
            boolean r4 = android.text.TextUtils.isEmpty(r4)     // Catch:{ all -> 0x0354 }
            if (r4 != 0) goto L_0x18e9
            java.lang.String r4 = "NotificationMessageGroupStickerEmoji"
            r8 = 2131626837(0x7f0e0b55, float:1.8880921E38)
            r9 = 3
            java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch:{ all -> 0x0354 }
            r10 = 0
            r11 = r14[r10]     // Catch:{ all -> 0x0354 }
            r9[r10] = r11     // Catch:{ all -> 0x0354 }
            r10 = 1
            r11 = r14[r10]     // Catch:{ all -> 0x0354 }
            r9[r10] = r11     // Catch:{ all -> 0x0354 }
            r10 = 2
            r11 = r14[r10]     // Catch:{ all -> 0x0354 }
            r9[r10] = r11     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r4, r8, r9)     // Catch:{ all -> 0x0354 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0354 }
            r8.<init>()     // Catch:{ all -> 0x0354 }
            r9 = 2
            r9 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8.append(r9)     // Catch:{ all -> 0x0354 }
            r8.append(r3)     // Catch:{ all -> 0x0354 }
            r3 = 2131624446(0x7f0e01fe, float:1.8876072E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)     // Catch:{ all -> 0x0354 }
            r8.append(r3)     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = r8.toString()     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x18e9:
            java.lang.String r4 = "NotificationMessageGroupSticker"
            r8 = 2131626836(0x7f0e0b54, float:1.888092E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch:{ all -> 0x0354 }
            r10 = 0
            r11 = r14[r10]     // Catch:{ all -> 0x0354 }
            r9[r10] = r11     // Catch:{ all -> 0x0354 }
            r10 = 1
            r11 = r14[r10]     // Catch:{ all -> 0x0354 }
            r9[r10] = r11     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r4, r8, r9)     // Catch:{ all -> 0x0354 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0354 }
            r8.<init>()     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8.append(r10)     // Catch:{ all -> 0x0354 }
            r8.append(r3)     // Catch:{ all -> 0x0354 }
            r3 = 2131624446(0x7f0e01fe, float:1.8876072E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)     // Catch:{ all -> 0x0354 }
            r8.append(r3)     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = r8.toString()     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1926:
            java.lang.String r3 = "NotificationMessageGroupDocument"
            r4 = 2131626823(0x7f0e0b47, float:1.8880893E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachDocument"
            r6 = 2131624422(0x7f0e01e6, float:1.8876023E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1951:
            java.lang.String r3 = "NotificationMessageGroupRound"
            r4 = 2131626835(0x7f0e0b53, float:1.8880917E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachRound"
            r6 = 2131624445(0x7f0e01fd, float:1.887607E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x197c:
            java.lang.String r3 = "NotificationMessageGroupVideo"
            r4 = 2131626839(0x7f0e0b57, float:1.8880926E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachVideo"
            r6 = 2131624449(0x7f0e0201, float:1.8876078E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x19a7:
            java.lang.String r3 = "NotificationMessageGroupPhoto"
            r4 = 2131626832(0x7f0e0b50, float:1.8880911E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachPhoto"
            r6 = 2131624443(0x7f0e01fb, float:1.8876066E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x19d2:
            java.lang.String r3 = "NotificationMessageGroupNoText"
            r4 = 2131626831(0x7f0e0b4f, float:1.888091E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Message"
            r6 = 2131626469(0x7f0e09e5, float:1.8880175E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x19fd:
            java.lang.String r3 = "NotificationMessageGroupText"
            r4 = 2131626838(0x7f0e0b56, float:1.8880923E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 2
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r4 = r14[r8]     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1a26:
            java.lang.String r3 = "ChannelMessageAlbum"
            r4 = 2131624847(0x7f0e038f, float:1.8876885E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1a43:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r8 = r14[r6]     // Catch:{ all -> 0x0354 }
            r3[r6] = r8     // Catch:{ all -> 0x0354 }
            java.lang.String r6 = "Files"
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r9)     // Catch:{ all -> 0x0354 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0354 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r6, r8)     // Catch:{ all -> 0x0354 }
            r8 = 1
            r3[r8] = r6     // Catch:{ all -> 0x0354 }
            r6 = 2131624851(0x7f0e0393, float:1.8876893E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r6, r3)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1a72:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r8 = r14[r6]     // Catch:{ all -> 0x0354 }
            r3[r6] = r8     // Catch:{ all -> 0x0354 }
            java.lang.String r6 = "MusicFiles"
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r9)     // Catch:{ all -> 0x0354 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0354 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r6, r8)     // Catch:{ all -> 0x0354 }
            r8 = 1
            r3[r8] = r6     // Catch:{ all -> 0x0354 }
            r6 = 2131624851(0x7f0e0393, float:1.8876893E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r6, r3)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1aa1:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r8 = r14[r6]     // Catch:{ all -> 0x0354 }
            r3[r6] = r8     // Catch:{ all -> 0x0354 }
            r6 = 1
            r8 = r14[r6]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ all -> 0x0354 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0354 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r11, r6)     // Catch:{ all -> 0x0354 }
            r8 = 1
            r3[r8] = r6     // Catch:{ all -> 0x0354 }
            r6 = 2131624851(0x7f0e0393, float:1.8876893E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r6, r3)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1ace:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r8 = r14[r6]     // Catch:{ all -> 0x0354 }
            r3[r6] = r8     // Catch:{ all -> 0x0354 }
            r6 = 1
            r8 = r14[r6]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ all -> 0x0354 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0354 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r15, r6)     // Catch:{ all -> 0x0354 }
            r8 = 1
            r3[r8] = r6     // Catch:{ all -> 0x0354 }
            r6 = 2131624851(0x7f0e0393, float:1.8876893E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r6, r3)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1afb:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r8 = r14[r6]     // Catch:{ all -> 0x0354 }
            r3[r6] = r8     // Catch:{ all -> 0x0354 }
            java.lang.String r6 = "ForwardedMessageCount"
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r9)     // Catch:{ all -> 0x0354 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0354 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r6, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r6 = r6.toLowerCase()     // Catch:{ all -> 0x0354 }
            r8 = 1
            r3[r8] = r6     // Catch:{ all -> 0x0354 }
            r6 = 2131624851(0x7f0e0393, float:1.8876893E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r6, r3)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1b2e:
            java.lang.String r3 = "NotificationMessageGame"
            r4 = 2131626818(0x7f0e0b42, float:1.8880883E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachGame"
            r6 = 2131624423(0x7f0e01e7, float:1.8876025E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1b54:
            java.lang.String r3 = "ChannelMessageGIF"
            r4 = 2131624852(0x7f0e0394, float:1.8876895E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachGif"
            r6 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1b7a:
            java.lang.String r3 = "ChannelMessageLiveLocation"
            r4 = 2131624853(0x7f0e0395, float:1.8876897E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachLiveLocation"
            r6 = 2131624429(0x7f0e01ed, float:1.8876037E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1ba0:
            java.lang.String r3 = "ChannelMessageMap"
            r4 = 2131624854(0x7f0e0396, float:1.88769E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachLocation"
            r6 = 2131624433(0x7f0e01f1, float:1.8876046E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1bc6:
            java.lang.String r3 = "ChannelMessagePoll2"
            r4 = 2131624858(0x7f0e039a, float:1.8876908E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Poll"
            r6 = 2131627414(0x7f0e0d96, float:1.8882092E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1bf1:
            java.lang.String r3 = "ChannelMessageQuiz2"
            r4 = 2131624859(0x7f0e039b, float:1.887691E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "QuizPoll"
            r6 = 2131627630(0x7f0e0e6e, float:1.888253E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1c1c:
            java.lang.String r3 = "ChannelMessageContact2"
            r4 = 2131624849(0x7f0e0391, float:1.887689E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachContact"
            r6 = 2131624419(0x7f0e01e3, float:1.8876017E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1CLASSNAME:
            java.lang.String r3 = "ChannelMessageAudio"
            r4 = 2131624848(0x7f0e0390, float:1.8876887E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachAudio"
            r6 = 2131624417(0x7f0e01e1, float:1.8876013E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1c6d:
            int r4 = r14.length     // Catch:{ all -> 0x0354 }
            r8 = 1
            if (r4 <= r8) goto L_0x1cb6
            r4 = r14[r8]     // Catch:{ all -> 0x0354 }
            boolean r4 = android.text.TextUtils.isEmpty(r4)     // Catch:{ all -> 0x0354 }
            if (r4 != 0) goto L_0x1cb6
            java.lang.String r4 = "ChannelMessageStickerEmoji"
            r8 = 2131624862(0x7f0e039e, float:1.8876916E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch:{ all -> 0x0354 }
            r10 = 0
            r11 = r14[r10]     // Catch:{ all -> 0x0354 }
            r9[r10] = r11     // Catch:{ all -> 0x0354 }
            r10 = 1
            r11 = r14[r10]     // Catch:{ all -> 0x0354 }
            r9[r10] = r11     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r4, r8, r9)     // Catch:{ all -> 0x0354 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0354 }
            r8.<init>()     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8.append(r10)     // Catch:{ all -> 0x0354 }
            r8.append(r3)     // Catch:{ all -> 0x0354 }
            r3 = 2131624446(0x7f0e01fe, float:1.8876072E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)     // Catch:{ all -> 0x0354 }
            r8.append(r3)     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = r8.toString()     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1cb6:
            java.lang.String r3 = "ChannelMessageSticker"
            r4 = 2131624861(0x7f0e039d, float:1.8876914E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r4 = 2131624446(0x7f0e01fe, float:1.8876072E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1cda:
            java.lang.String r3 = "ChannelMessageDocument"
            r4 = 2131624850(0x7f0e0392, float:1.8876891E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachDocument"
            r6 = 2131624422(0x7f0e01e6, float:1.8876023E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1d00:
            java.lang.String r3 = "ChannelMessageRound"
            r4 = 2131624860(0x7f0e039c, float:1.8876912E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachRound"
            r6 = 2131624445(0x7f0e01fd, float:1.887607E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1d26:
            java.lang.String r3 = "ChannelMessageVideo"
            r4 = 2131624863(0x7f0e039f, float:1.8876918E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachVideo"
            r6 = 2131624449(0x7f0e0201, float:1.8876078E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1d4c:
            java.lang.String r3 = "ChannelMessagePhoto"
            r4 = 2131624857(0x7f0e0399, float:1.8876906E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachPhoto"
            r6 = 2131624443(0x7f0e01fb, float:1.8876066E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1d72:
            java.lang.String r3 = "ChannelMessageNoText"
            r4 = 2131624856(0x7f0e0398, float:1.8876904E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Message"
            r6 = 2131626469(0x7f0e09e5, float:1.8880175E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1d98:
            java.lang.String r3 = "NotificationMessageAlbum"
            r4 = 2131626812(0x7f0e0b3c, float:1.888087E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1db5:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r4 = 0
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r6     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Files"
            r6 = 1
            r8 = r14[r6]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ all -> 0x0354 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r6)     // Catch:{ all -> 0x0354 }
            r6 = 1
            r3[r6] = r4     // Catch:{ all -> 0x0354 }
            r4 = 2131626816(0x7f0e0b40, float:1.8880879E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r9, r4, r3)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1de4:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r4 = 0
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r6     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "MusicFiles"
            r6 = 1
            r8 = r14[r6]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ all -> 0x0354 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r6)     // Catch:{ all -> 0x0354 }
            r6 = 1
            r3[r6] = r4     // Catch:{ all -> 0x0354 }
            r4 = 2131626816(0x7f0e0b40, float:1.8880879E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r9, r4, r3)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1e13:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r4 = 0
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r6     // Catch:{ all -> 0x0354 }
            r4 = 1
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x0354 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r11, r4)     // Catch:{ all -> 0x0354 }
            r6 = 1
            r3[r6] = r4     // Catch:{ all -> 0x0354 }
            r4 = 2131626816(0x7f0e0b40, float:1.8880879E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r9, r4, r3)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1e40:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r4 = 0
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r6     // Catch:{ all -> 0x0354 }
            r4 = 1
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x0354 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r15, r4)     // Catch:{ all -> 0x0354 }
            r6 = 1
            r3[r6] = r4     // Catch:{ all -> 0x0354 }
            r4 = 2131626816(0x7f0e0b40, float:1.8880879E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r9, r4, r3)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1e6d:
            java.lang.String r3 = "NotificationMessageForwardFew"
            r4 = 2131626817(0x7f0e0b41, float:1.888088E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r9)     // Catch:{ all -> 0x0354 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0354 }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r12, r8)     // Catch:{ all -> 0x0354 }
            r9 = 1
            r6[r9] = r8     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r34 = 1
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1e9c:
            java.lang.String r3 = "NotificationMessageInvoice"
            r4 = 2131626840(0x7f0e0b58, float:1.8880928E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "PaymentInvoice"
            r6 = 2131627235(0x7f0e0ce3, float:1.8881729E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1ec7:
            java.lang.String r3 = "NotificationMessageGameScored"
            r4 = 2131626819(0x7f0e0b43, float:1.8880885E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 2
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x1eec:
            java.lang.String r3 = "NotificationMessageGame"
            r4 = 2131626818(0x7f0e0b42, float:1.8880883E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachGame"
            r6 = 2131624423(0x7f0e01e7, float:1.8876025E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1var_:
            java.lang.String r3 = "NotificationMessageGif"
            r4 = 2131626820(0x7f0e0b44, float:1.8880887E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachGif"
            r6 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1f3d:
            java.lang.String r3 = "NotificationMessageLiveLocation"
            r4 = 2131626841(0x7f0e0b59, float:1.888093E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachLiveLocation"
            r6 = 2131624429(0x7f0e01ed, float:1.8876037E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1var_:
            java.lang.String r3 = "NotificationMessageMap"
            r4 = 2131626842(0x7f0e0b5a, float:1.8880932E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachLocation"
            r6 = 2131624433(0x7f0e01f1, float:1.8876046E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1var_:
            java.lang.String r3 = "NotificationMessagePoll2"
            r4 = 2131626846(0x7f0e0b5e, float:1.888094E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Poll"
            r6 = 2131627414(0x7f0e0d96, float:1.8882092E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1fb4:
            java.lang.String r3 = "NotificationMessageQuiz2"
            r4 = 2131626847(0x7f0e0b5f, float:1.8880942E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "QuizPoll"
            r6 = 2131627630(0x7f0e0e6e, float:1.888253E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x1fdf:
            java.lang.String r3 = "NotificationMessageContact2"
            r4 = 2131626814(0x7f0e0b3e, float:1.8880875E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachContact"
            r6 = 2131624419(0x7f0e01e3, float:1.8876017E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x200a:
            java.lang.String r3 = "NotificationMessageAudio"
            r4 = 2131626813(0x7f0e0b3d, float:1.8880873E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachAudio"
            r6 = 2131624417(0x7f0e01e1, float:1.8876013E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x2030:
            int r4 = r14.length     // Catch:{ all -> 0x0354 }
            r8 = 1
            if (r4 <= r8) goto L_0x2079
            r4 = r14[r8]     // Catch:{ all -> 0x0354 }
            boolean r4 = android.text.TextUtils.isEmpty(r4)     // Catch:{ all -> 0x0354 }
            if (r4 != 0) goto L_0x2079
            java.lang.String r4 = "NotificationMessageStickerEmoji"
            r8 = 2131626854(0x7f0e0b66, float:1.8880956E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch:{ all -> 0x0354 }
            r10 = 0
            r11 = r14[r10]     // Catch:{ all -> 0x0354 }
            r9[r10] = r11     // Catch:{ all -> 0x0354 }
            r10 = 1
            r11 = r14[r10]     // Catch:{ all -> 0x0354 }
            r9[r10] = r11     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r4, r8, r9)     // Catch:{ all -> 0x0354 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0354 }
            r8.<init>()     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8.append(r10)     // Catch:{ all -> 0x0354 }
            r8.append(r3)     // Catch:{ all -> 0x0354 }
            r3 = 2131624446(0x7f0e01fe, float:1.8876072E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)     // Catch:{ all -> 0x0354 }
            r8.append(r3)     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = r8.toString()     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x2079:
            java.lang.String r3 = "NotificationMessageSticker"
            r4 = 2131626853(0x7f0e0b65, float:1.8880954E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r4 = 2131624446(0x7f0e01fe, float:1.8876072E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x209d:
            java.lang.String r3 = "NotificationMessageDocument"
            r4 = 2131626815(0x7f0e0b3f, float:1.8880877E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachDocument"
            r6 = 2131624422(0x7f0e01e6, float:1.8876023E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x20c3:
            java.lang.String r3 = "NotificationMessageRound"
            r4 = 2131626848(0x7f0e0b60, float:1.8880944E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachRound"
            r6 = 2131624445(0x7f0e01fd, float:1.887607E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x20e9:
            java.lang.String r3 = "ActionTakeScreenshoot"
            r4 = 2131624175(0x7f0e00ef, float:1.8875522E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "un1"
            r6 = 0
            r8 = r14[r6]     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = r3.replace(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r30
            r6 = r34
            r3 = r77
            goto L_0x2218
        L_0x2105:
            java.lang.String r3 = "NotificationMessageSDVideo"
            r4 = 2131626850(0x7f0e0b62, float:1.8880948E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachDestructingVideo"
            r6 = 2131624421(0x7f0e01e5, float:1.8876021E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x212b:
            java.lang.String r3 = "NotificationMessageVideo"
            r4 = 2131626856(0x7f0e0b68, float:1.888096E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachVideo"
            r6 = 2131624449(0x7f0e0201, float:1.8876078E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x2151:
            java.lang.String r3 = "NotificationMessageSDPhoto"
            r4 = 2131626849(0x7f0e0b61, float:1.8880946E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachDestructingPhoto"
            r6 = 2131624420(0x7f0e01e4, float:1.887602E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x2177:
            java.lang.String r3 = "NotificationMessagePhoto"
            r4 = 2131626845(0x7f0e0b5d, float:1.8880938E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachPhoto"
            r6 = 2131624443(0x7f0e01fb, float:1.8876066E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x219d:
            java.lang.String r3 = "NotificationMessageNoText"
            r4 = 2131626844(0x7f0e0b5c, float:1.8880936E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r6 = 0
            r9 = r14[r6]     // Catch:{ all -> 0x0354 }
            r8[r6] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Message"
            r6 = 2131626469(0x7f0e09e5, float:1.8880175E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x21c2:
            java.lang.String r3 = "NotificationMessageText"
            r4 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0354 }
            r12 = 0
            r8 = r14[r12]     // Catch:{ all -> 0x0354 }
            r6[r12] = r8     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r6[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)     // Catch:{ all -> 0x0354 }
            r4 = r14[r8]     // Catch:{ all -> 0x0354 }
            r67 = r4
            r8 = r30
            r6 = r34
            r4 = r3
            r3 = r77
            goto L_0x2218
        L_0x21e4:
            if (r3 == 0) goto L_0x21fa
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0354 }
            r3.<init>()     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "unhandled loc_key = "
            r3.append(r4)     // Catch:{ all -> 0x0354 }
            r3.append(r5)     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0354 }
            org.telegram.messenger.FileLog.w(r3)     // Catch:{ all -> 0x0354 }
        L_0x21fa:
            r3 = r77
            r8 = r30
            r6 = r34
            r4 = r38
            goto L_0x2218
        L_0x2203:
            r62 = r3
            r73 = r4
            r74 = r6
            r71 = r9
            r16 = r15
            r12 = 0
        L_0x220e:
            r3 = r77
            java.lang.String r4 = r3.getReactedText(r5, r14)     // Catch:{ all -> 0x2370 }
            r8 = r30
            r6 = r34
        L_0x2218:
            if (r4 == 0) goto L_0x235e
            org.telegram.tgnet.TLRPC$TL_message r9 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x2370 }
            r9.<init>()     // Catch:{ all -> 0x2370 }
            r9.id = r7     // Catch:{ all -> 0x2370 }
            r10 = r54
            r9.random_id = r10     // Catch:{ all -> 0x2370 }
            if (r67 == 0) goto L_0x222a
            r15 = r67
            goto L_0x222b
        L_0x222a:
            r15 = r4
        L_0x222b:
            r9.message = r15     // Catch:{ all -> 0x2370 }
            r34 = 1000(0x3e8, double:4.94E-321)
            r15 = r13
            long r12 = r79 / r34
            int r13 = (int) r12     // Catch:{ all -> 0x2370 }
            r9.date = r13     // Catch:{ all -> 0x2370 }
            if (r74 == 0) goto L_0x2247
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r12 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x223f }
            r12.<init>()     // Catch:{ all -> 0x223f }
            r9.action = r12     // Catch:{ all -> 0x223f }
            goto L_0x2247
        L_0x223f:
            r0 = move-exception
            r1 = r0
            r6 = r19
            r4 = r31
            goto L_0x2416
        L_0x2247:
            if (r73 == 0) goto L_0x2250
            int r12 = r9.flags     // Catch:{ all -> 0x223f }
            r13 = -2147483648(0xfffffffvar_, float:-0.0)
            r12 = r12 | r13
            r9.flags = r12     // Catch:{ all -> 0x223f }
        L_0x2250:
            r9.dialog_id = r1     // Catch:{ all -> 0x2370 }
            r12 = 0
            int r17 = (r71 > r12 ? 1 : (r71 == r12 ? 0 : -1))
            if (r17 == 0) goto L_0x2270
            org.telegram.tgnet.TLRPC$TL_peerChannel r12 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x223f }
            r12.<init>()     // Catch:{ all -> 0x223f }
            r9.peer_id = r12     // Catch:{ all -> 0x223f }
            org.telegram.tgnet.TLRPC$Peer r12 = r9.peer_id     // Catch:{ all -> 0x223f }
            r54 = r1
            r1 = r71
            r12.channel_id = r1     // Catch:{ all -> 0x223f }
            r71 = r1
            r1 = r51
            r51 = r10
            r10 = r49
            goto L_0x22a1
        L_0x2270:
            r54 = r1
            r1 = r71
            r12 = 0
            int r17 = (r51 > r12 ? 1 : (r51 == r12 ? 0 : -1))
            if (r17 == 0) goto L_0x228e
            org.telegram.tgnet.TLRPC$TL_peerChat r12 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x223f }
            r12.<init>()     // Catch:{ all -> 0x223f }
            r9.peer_id = r12     // Catch:{ all -> 0x223f }
            org.telegram.tgnet.TLRPC$Peer r12 = r9.peer_id     // Catch:{ all -> 0x223f }
            r71 = r1
            r1 = r51
            r12.chat_id = r1     // Catch:{ all -> 0x223f }
            r51 = r10
            r10 = r49
            goto L_0x22a1
        L_0x228e:
            r71 = r1
            r1 = r51
            org.telegram.tgnet.TLRPC$TL_peerUser r12 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x2370 }
            r12.<init>()     // Catch:{ all -> 0x2370 }
            r9.peer_id = r12     // Catch:{ all -> 0x2370 }
            org.telegram.tgnet.TLRPC$Peer r12 = r9.peer_id     // Catch:{ all -> 0x2370 }
            r51 = r10
            r10 = r49
            r12.user_id = r10     // Catch:{ all -> 0x2370 }
        L_0x22a1:
            int r12 = r9.flags     // Catch:{ all -> 0x2370 }
            r12 = r12 | 256(0x100, float:3.59E-43)
            r9.flags = r12     // Catch:{ all -> 0x2370 }
            r12 = 0
            int r17 = (r60 > r12 ? 1 : (r60 == r12 ? 0 : -1))
            if (r17 == 0) goto L_0x22c1
            org.telegram.tgnet.TLRPC$TL_peerChat r12 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x223f }
            r12.<init>()     // Catch:{ all -> 0x223f }
            r9.from_id = r12     // Catch:{ all -> 0x223f }
            org.telegram.tgnet.TLRPC$Peer r12 = r9.from_id     // Catch:{ all -> 0x223f }
            r12.chat_id = r1     // Catch:{ all -> 0x223f }
            r49 = r1
            r12 = r58
            r58 = r10
            r10 = r65
            goto L_0x22fb
        L_0x22c1:
            r12 = r58
            r32 = 0
            int r17 = (r12 > r32 ? 1 : (r12 == r32 ? 0 : -1))
            if (r17 == 0) goto L_0x22db
            r49 = r1
            org.telegram.tgnet.TLRPC$TL_peerChannel r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x223f }
            r1.<init>()     // Catch:{ all -> 0x223f }
            r9.from_id = r1     // Catch:{ all -> 0x223f }
            org.telegram.tgnet.TLRPC$Peer r1 = r9.from_id     // Catch:{ all -> 0x223f }
            r1.channel_id = r12     // Catch:{ all -> 0x223f }
            r58 = r10
            r10 = r65
            goto L_0x22fb
        L_0x22db:
            r49 = r1
            r1 = 0
            int r17 = (r65 > r1 ? 1 : (r65 == r1 ? 0 : -1))
            if (r17 == 0) goto L_0x22f3
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x223f }
            r1.<init>()     // Catch:{ all -> 0x223f }
            r9.from_id = r1     // Catch:{ all -> 0x223f }
            org.telegram.tgnet.TLRPC$Peer r1 = r9.from_id     // Catch:{ all -> 0x223f }
            r58 = r10
            r10 = r65
            r1.user_id = r10     // Catch:{ all -> 0x223f }
            goto L_0x22fb
        L_0x22f3:
            r58 = r10
            r10 = r65
            org.telegram.tgnet.TLRPC$Peer r1 = r9.peer_id     // Catch:{ all -> 0x2370 }
            r9.from_id = r1     // Catch:{ all -> 0x2370 }
        L_0x22fb:
            if (r63 != 0) goto L_0x2302
            if (r74 == 0) goto L_0x2300
            goto L_0x2302
        L_0x2300:
            r1 = 0
            goto L_0x2303
        L_0x2302:
            r1 = 1
        L_0x2303:
            r9.mentioned = r1     // Catch:{ all -> 0x2370 }
            r1 = r57
            r9.silent = r1     // Catch:{ all -> 0x2370 }
            r2 = r47
            r9.from_scheduled = r2     // Catch:{ all -> 0x2370 }
            org.telegram.messenger.MessageObject r17 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x2370 }
            r30 = r17
            r32 = r9
            r33 = r4
            r34 = r8
            r35 = r70
            r36 = r6
            r37 = r69
            r38 = r73
            r30.<init>(r31, r32, r33, r34, r35, r36, r37, r38, r39)     // Catch:{ all -> 0x2370 }
            r25 = r17
            r57 = r1
            r1 = r62
            boolean r1 = r5.startsWith(r1)     // Catch:{ all -> 0x2370 }
            if (r1 != 0) goto L_0x2339
            r1 = r16
            boolean r1 = r5.startsWith(r1)     // Catch:{ all -> 0x223f }
            if (r1 == 0) goto L_0x2337
            goto L_0x2339
        L_0x2337:
            r1 = 0
            goto L_0x233a
        L_0x2339:
            r1 = 1
        L_0x233a:
            r47 = r2
            r2 = r25
            r2.isReactionPush = r1     // Catch:{ all -> 0x2370 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x2370 }
            r1.<init>()     // Catch:{ all -> 0x2370 }
            r1.add(r2)     // Catch:{ all -> 0x2370 }
            r16 = 0
            r17 = r2
            org.telegram.messenger.NotificationsController r2 = org.telegram.messenger.NotificationsController.getInstance(r31)     // Catch:{ all -> 0x2370 }
            r25 = r4
            java.util.concurrent.CountDownLatch r4 = r3.countDownLatch     // Catch:{ all -> 0x2370 }
            r26 = r5
            r5 = 1
            r2.processNewMessages(r1, r5, r5, r4)     // Catch:{ all -> 0x23d1 }
            r11 = r16
            goto L_0x23b8
        L_0x235e:
            r25 = r4
            r26 = r5
            r15 = r13
            r12 = r58
            r10 = r65
            r58 = r49
            r49 = r51
            r51 = r54
            r54 = r1
            goto L_0x23b6
        L_0x2370:
            r0 = move-exception
            r26 = r5
            r1 = r0
            r6 = r19
            r4 = r31
            goto L_0x2416
        L_0x237a:
            r3 = r77
            r54 = r1
            r26 = r5
            r68 = r6
            r56 = r8
            r71 = r9
            r64 = r30
            r58 = r49
            r49 = r51
            r51 = r13
            goto L_0x23b6
        L_0x238f:
            r3 = r77
            r54 = r1
            r53 = r4
            r26 = r5
            r68 = r6
            r71 = r9
            r64 = r30
            r58 = r49
            r49 = r51
            goto L_0x23b6
        L_0x23a2:
            r3 = r77
            r54 = r1
            r53 = r4
            r26 = r5
            r68 = r6
            r58 = r7
            r71 = r9
            r48 = r11
            r49 = r13
            r64 = r15
        L_0x23b6:
            r11 = r48
        L_0x23b8:
            if (r11 == 0) goto L_0x23bf
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x23d1 }
            r1.countDown()     // Catch:{ all -> 0x23d1 }
        L_0x23bf:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r31)     // Catch:{ all -> 0x23d1 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r31)     // Catch:{ all -> 0x23d1 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x23d1 }
            r6 = r19
            r5 = r26
            r4 = r31
            goto L_0x244f
        L_0x23d1:
            r0 = move-exception
            r1 = r0
            r6 = r19
            r5 = r26
            r4 = r31
            goto L_0x2416
        L_0x23da:
            r0 = move-exception
            r3 = r77
            r26 = r5
            r1 = r0
            r6 = r19
            r4 = r31
            goto L_0x2416
        L_0x23e5:
            r0 = move-exception
            r3 = r1
            r26 = r5
            r1 = r0
            r6 = r19
            r4 = r31
            goto L_0x2416
        L_0x23ef:
            r0 = move-exception
            r3 = r1
            r26 = r5
            r1 = r0
            r6 = r19
            r4 = r27
            goto L_0x2416
        L_0x23f9:
            r0 = move-exception
            r3 = r1
            r26 = r5
            r19 = r6
            r1 = r0
            r4 = r27
            goto L_0x2416
        L_0x2403:
            r0 = move-exception
            r3 = r1
            r19 = r6
            r1 = r0
            r4 = r27
            goto L_0x2416
        L_0x240b:
            r0 = move-exception
            r3 = r1
            r1 = r0
            r4 = r27
            goto L_0x2416
        L_0x2411:
            r0 = move-exception
            r3 = r1
            r27 = r4
            r1 = r0
        L_0x2416:
            r2 = -1
            if (r4 == r2) goto L_0x2429
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r4)
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
            r2.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r2 = r3.countDownLatch
            r2.countDown()
            goto L_0x242c
        L_0x2429:
            r77.onDecryptError()
        L_0x242c:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x244c
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r7 = "error in loc_key = "
            r2.append(r7)
            r2.append(r5)
            java.lang.String r7 = " json "
            r2.append(r7)
            r2.append(r6)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r2)
        L_0x244c:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x244f:
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

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getReactedText(java.lang.String r3, java.lang.Object[] r4) {
        /*
            r2 = this;
            int r0 = r3.hashCode()
            switch(r0) {
                case -2114646919: goto L_0x016d;
                case -1891797827: goto L_0x0162;
                case -1415696683: goto L_0x0157;
                case -1375264434: goto L_0x014d;
                case -1105974394: goto L_0x0142;
                case -861247200: goto L_0x0137;
                case -661458538: goto L_0x012c;
                case 51977938: goto L_0x0121;
                case 52259487: goto L_0x0116;
                case 52294965: goto L_0x010a;
                case 52369421: goto L_0x00ff;
                case 147425325: goto L_0x00f3;
                case 192842257: goto L_0x00e7;
                case 192844842: goto L_0x00db;
                case 192844957: goto L_0x00cf;
                case 591941181: goto L_0x00c4;
                case 635226735: goto L_0x00b8;
                case 648703179: goto L_0x00ac;
                case 650764327: goto L_0x00a0;
                case 654263060: goto L_0x0094;
                case 1149769750: goto L_0x0088;
                case 1606362326: goto L_0x007d;
                case 1619838770: goto L_0x0072;
                case 1621899918: goto L_0x0067;
                case 1625398651: goto L_0x005c;
                case 1664242232: goto L_0x0051;
                case 1664244817: goto L_0x0045;
                case 1664244932: goto L_0x0039;
                case 1683218969: goto L_0x002d;
                case 1683500518: goto L_0x0021;
                case 1683535996: goto L_0x0015;
                case 1683610452: goto L_0x0009;
                default: goto L_0x0007;
            }
        L_0x0007:
            goto L_0x0178
        L_0x0009:
            java.lang.String r0 = "CHAT_REACT_TEXT"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 16
            goto L_0x0179
        L_0x0015:
            java.lang.String r0 = "CHAT_REACT_QUIZ"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 28
            goto L_0x0179
        L_0x0021:
            java.lang.String r0 = "CHAT_REACT_POLL"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 27
            goto L_0x0179
        L_0x002d:
            java.lang.String r0 = "CHAT_REACT_GAME"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 29
            goto L_0x0179
        L_0x0039:
            java.lang.String r0 = "REACT_GIF"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 15
            goto L_0x0179
        L_0x0045:
            java.lang.String r0 = "REACT_GEO"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 9
            goto L_0x0179
        L_0x0051:
            java.lang.String r0 = "REACT_DOC"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 5
            goto L_0x0179
        L_0x005c:
            java.lang.String r0 = "REACT_VIDEO"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 3
            goto L_0x0179
        L_0x0067:
            java.lang.String r0 = "REACT_ROUND"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 4
            goto L_0x0179
        L_0x0072:
            java.lang.String r0 = "REACT_PHOTO"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 2
            goto L_0x0179
        L_0x007d:
            java.lang.String r0 = "REACT_AUDIO"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 7
            goto L_0x0179
        L_0x0088:
            java.lang.String r0 = "CHAT_REACT_GEOLIVE"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 26
            goto L_0x0179
        L_0x0094:
            java.lang.String r0 = "CHAT_REACT_VIDEO"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 19
            goto L_0x0179
        L_0x00a0:
            java.lang.String r0 = "CHAT_REACT_ROUND"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 20
            goto L_0x0179
        L_0x00ac:
            java.lang.String r0 = "CHAT_REACT_PHOTO"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 18
            goto L_0x0179
        L_0x00b8:
            java.lang.String r0 = "CHAT_REACT_AUDIO"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 23
            goto L_0x0179
        L_0x00c4:
            java.lang.String r0 = "REACT_STICKER"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 6
            goto L_0x0179
        L_0x00cf:
            java.lang.String r0 = "CHAT_REACT_GIF"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 31
            goto L_0x0179
        L_0x00db:
            java.lang.String r0 = "CHAT_REACT_GEO"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 25
            goto L_0x0179
        L_0x00e7:
            java.lang.String r0 = "CHAT_REACT_DOC"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 21
            goto L_0x0179
        L_0x00f3:
            java.lang.String r0 = "REACT_INVOICE"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 14
            goto L_0x0179
        L_0x00ff:
            java.lang.String r0 = "REACT_TEXT"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 0
            goto L_0x0179
        L_0x010a:
            java.lang.String r0 = "REACT_QUIZ"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 12
            goto L_0x0179
        L_0x0116:
            java.lang.String r0 = "REACT_POLL"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 11
            goto L_0x0179
        L_0x0121:
            java.lang.String r0 = "REACT_GAME"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 13
            goto L_0x0179
        L_0x012c:
            java.lang.String r0 = "CHAT_REACT_STICKER"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 22
            goto L_0x0179
        L_0x0137:
            java.lang.String r0 = "REACT_CONTACT"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 8
            goto L_0x0179
        L_0x0142:
            java.lang.String r0 = "CHAT_REACT_INVOICE"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 30
            goto L_0x0179
        L_0x014d:
            java.lang.String r0 = "REACT_NOTEXT"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 1
            goto L_0x0179
        L_0x0157:
            java.lang.String r0 = "CHAT_REACT_NOTEXT"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 17
            goto L_0x0179
        L_0x0162:
            java.lang.String r0 = "REACT_GEOLIVE"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 10
            goto L_0x0179
        L_0x016d:
            java.lang.String r0 = "CHAT_REACT_CONTACT"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 24
            goto L_0x0179
        L_0x0178:
            r0 = -1
        L_0x0179:
            switch(r0) {
                case 0: goto L_0x02b4;
                case 1: goto L_0x02aa;
                case 2: goto L_0x02a0;
                case 3: goto L_0x0296;
                case 4: goto L_0x028c;
                case 5: goto L_0x0282;
                case 6: goto L_0x0278;
                case 7: goto L_0x026e;
                case 8: goto L_0x0264;
                case 9: goto L_0x025a;
                case 10: goto L_0x0250;
                case 11: goto L_0x0246;
                case 12: goto L_0x023c;
                case 13: goto L_0x0232;
                case 14: goto L_0x0228;
                case 15: goto L_0x021e;
                case 16: goto L_0x0214;
                case 17: goto L_0x020a;
                case 18: goto L_0x0200;
                case 19: goto L_0x01f6;
                case 20: goto L_0x01ec;
                case 21: goto L_0x01e2;
                case 22: goto L_0x01d8;
                case 23: goto L_0x01ce;
                case 24: goto L_0x01c4;
                case 25: goto L_0x01ba;
                case 26: goto L_0x01b0;
                case 27: goto L_0x01a6;
                case 28: goto L_0x019c;
                case 29: goto L_0x0192;
                case 30: goto L_0x0188;
                case 31: goto L_0x017e;
                default: goto L_0x017c;
            }
        L_0x017c:
            r0 = 0
            return r0
        L_0x017e:
            r0 = 2131627591(0x7f0e0e47, float:1.888245E38)
            java.lang.String r1 = "PushChatReactGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0188:
            r0 = 2131627592(0x7f0e0e48, float:1.8882453E38)
            java.lang.String r1 = "PushChatReactInvoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0192:
            r0 = 2131627588(0x7f0e0e44, float:1.8882445E38)
            java.lang.String r1 = "PushChatReactGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x019c:
            r0 = 2131627596(0x7f0e0e4c, float:1.888246E38)
            java.lang.String r1 = "PushChatReactQuiz"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01a6:
            r0 = 2131627595(0x7f0e0e4b, float:1.8882459E38)
            java.lang.String r1 = "PushChatReactPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01b0:
            r0 = 2131627590(0x7f0e0e46, float:1.8882449E38)
            java.lang.String r1 = "PushChatReactGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01ba:
            r0 = 2131627589(0x7f0e0e45, float:1.8882447E38)
            java.lang.String r1 = "PushChatReactGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01c4:
            r0 = 2131627586(0x7f0e0e42, float:1.888244E38)
            java.lang.String r1 = "PushChatReactContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01ce:
            r0 = 2131627585(0x7f0e0e41, float:1.8882439E38)
            java.lang.String r1 = "PushChatReactAudio"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01d8:
            r0 = 2131627598(0x7f0e0e4e, float:1.8882465E38)
            java.lang.String r1 = "PushChatReactSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01e2:
            r0 = 2131627587(0x7f0e0e43, float:1.8882443E38)
            java.lang.String r1 = "PushChatReactDoc"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01ec:
            r0 = 2131627597(0x7f0e0e4d, float:1.8882463E38)
            java.lang.String r1 = "PushChatReactRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01f6:
            r0 = 2131627600(0x7f0e0e50, float:1.888247E38)
            java.lang.String r1 = "PushChatReactVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0200:
            r0 = 2131627594(0x7f0e0e4a, float:1.8882457E38)
            java.lang.String r1 = "PushChatReactPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x020a:
            r0 = 2131627593(0x7f0e0e49, float:1.8882455E38)
            java.lang.String r1 = "PushChatReactNotext"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0214:
            r0 = 2131627599(0x7f0e0e4f, float:1.8882467E38)
            java.lang.String r1 = "PushChatReactText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x021e:
            r0 = 2131627607(0x7f0e0e57, float:1.8882483E38)
            java.lang.String r1 = "PushReactGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0228:
            r0 = 2131627608(0x7f0e0e58, float:1.8882485E38)
            java.lang.String r1 = "PushReactInvoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0232:
            r0 = 2131627604(0x7f0e0e54, float:1.8882477E38)
            java.lang.String r1 = "PushReactGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x023c:
            r0 = 2131627612(0x7f0e0e5c, float:1.8882493E38)
            java.lang.String r1 = "PushReactQuiz"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0246:
            r0 = 2131627611(0x7f0e0e5b, float:1.8882491E38)
            java.lang.String r1 = "PushReactPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0250:
            r0 = 2131627606(0x7f0e0e56, float:1.8882481E38)
            java.lang.String r1 = "PushReactGeoLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x025a:
            r0 = 2131627605(0x7f0e0e55, float:1.888248E38)
            java.lang.String r1 = "PushReactGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0264:
            r0 = 2131627602(0x7f0e0e52, float:1.8882473E38)
            java.lang.String r1 = "PushReactContect"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x026e:
            r0 = 2131627601(0x7f0e0e51, float:1.8882471E38)
            java.lang.String r1 = "PushReactAudio"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0278:
            r0 = 2131627614(0x7f0e0e5e, float:1.8882497E38)
            java.lang.String r1 = "PushReactSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0282:
            r0 = 2131627603(0x7f0e0e53, float:1.8882475E38)
            java.lang.String r1 = "PushReactDoc"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x028c:
            r0 = 2131627613(0x7f0e0e5d, float:1.8882495E38)
            java.lang.String r1 = "PushReactRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0296:
            r0 = 2131627616(0x7f0e0e60, float:1.8882501E38)
            java.lang.String r1 = "PushReactVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x02a0:
            r0 = 2131627610(0x7f0e0e5a, float:1.888249E38)
            java.lang.String r1 = "PushReactPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x02aa:
            r0 = 2131627609(0x7f0e0e59, float:1.8882487E38)
            java.lang.String r1 = "PushReactNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x02b4:
            r0 = 2131627615(0x7f0e0e5f, float:1.88825E38)
            java.lang.String r1 = "PushReactText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.GcmPushListenerService.getReactedText(java.lang.String, java.lang.Object[]):java.lang.String");
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
