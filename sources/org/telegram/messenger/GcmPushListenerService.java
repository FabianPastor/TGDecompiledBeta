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
    public /* synthetic */ void m71x1d2d684b(Map data, long time) {
        ApplicationLoader.postInitApplication();
        Utilities.stageQueue.postRunnable(new GcmPushListenerService$$ExternalSyntheticLambda6(this, data, time));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v8, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v8, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v9, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v11, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v29, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v77, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v40, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v114, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v44, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v46, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v48, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v82, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v48, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v165, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v153, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v168, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v176, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v175, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v188, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v196, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v200, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v198, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v119, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v206, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v205, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v209, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v124, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v218, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v127, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v223, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v221, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v130, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v228, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v230, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v232, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v233, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v237, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v78, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v241, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v79, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v245, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v82, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v249, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v253, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v155, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v256, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v227, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v259, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v267, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v269, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v271, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v273, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v101, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v275, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v277, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v279, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v281, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v283, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v285, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v287, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v289, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v108, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v291, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v293, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v293, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v296, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v297, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v300, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v299, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v303, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v301, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v306, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v303, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v309, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v305, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v312, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v307, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v315, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v309, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v318, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v311, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v321, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v134, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v313, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v324, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v315, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v327, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v138, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v317, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v330, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v141, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v319, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v333, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v143, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v321, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v336, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v323, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v339, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v325, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v342, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v148, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v149, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v150, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v327, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v329, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v255, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v330, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v332, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v260, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v154, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v333, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v335, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v266, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v156, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v336, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v338, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v273, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v274, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v158, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v339, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v371, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v341, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v375, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v343, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v166, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v345, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v381, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v167, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v347, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v293, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v169, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v349, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v388, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v170, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v351, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v391, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v172, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v173, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v353, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v301, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v174, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v355, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v398, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v175, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v357, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v401, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v177, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v178, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v359, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v309, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v361, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v408, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v181, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v363, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v411, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v365, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v317, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v367, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v418, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v369, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v421, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v371, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v325, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v373, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v428, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v190, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v375, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v431, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v377, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v333, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v380, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v383, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v438, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v196, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v382, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v441, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v198, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v385, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v392, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v444, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v200, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v201, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v387, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v344, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v390, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v401, resolved type: java.nio.CharBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v450, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v392, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v349, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v394, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v457, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v206, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v207, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v396, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v460, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v208, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v209, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v398, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v464, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v210, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v400, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v467, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v212, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v213, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v214, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v402, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v470, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v216, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v404, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v474, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v217, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v406, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v477, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v221, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v408, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v480, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v223, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v410, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v484, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v412, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v487, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v226, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v227, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v228, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v414, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v490, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v229, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v416, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v384, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v418, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v497, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v232, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v420, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v500, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v422, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v392, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v424, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v507, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v426, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v510, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v428, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v400, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v430, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v517, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v432, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v520, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v244, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v434, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v408, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v436, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v527, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v438, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v530, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v440, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v416, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v442, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v537, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v252, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v444, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v540, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v446, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v424, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v256, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v448, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v547, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v257, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v258, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v450, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v550, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v260, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v452, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v456, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v9, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v11, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v52, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v56, resolved type: java.lang.Long} */
    /* JADX WARNING: type inference failed for: r4v461 */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:1000:0x2357, code lost:
        if (r6 == false) goto L_0x235a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1002:0x235a, code lost:
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1003:0x235c, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1004:0x235d, code lost:
        r10.mentioned = r11;
        r10.silent = r58;
        r65 = r1;
        r1 = r47;
        r10.from_scheduled = r1;
        r30 = new org.telegram.messenger.MessageObject(r31, r10, r4, r8, r71, r9, r70, r74, r39);
        r47 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1005:0x2386, code lost:
        if (r5.startsWith(r63) != false) goto L_0x2393;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1009:0x238e, code lost:
        if (r5.startsWith(r16) == false) goto L_0x2391;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1010:0x2391, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1011:0x2393, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1013:?, code lost:
        r30.isReactionPush = r1;
        r1 = new java.util.ArrayList<>();
        r1.add(r30);
        r17 = r30;
        r25 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1014:0x23aa, code lost:
        r26 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1016:?, code lost:
        org.telegram.messenger.NotificationsController.getInstance(r31).processNewMessages(r1, true, true, r3.countDownLatch);
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1017:0x23b4, code lost:
        r25 = r4;
        r26 = r5;
        r15 = r13;
        r12 = r50;
        r50 = r52;
        r11 = r58;
        r52 = r72;
        r72 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1018:0x23c4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1019:0x23c5, code lost:
        r26 = r5;
        r1 = r0;
        r6 = r19;
        r4 = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1020:0x23ce, code lost:
        r3 = r77;
        r72 = r1;
        r26 = r5;
        r69 = r6;
        r57 = r8;
        r55 = r13;
        r12 = r50;
        r50 = r52;
        r52 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1021:0x23e1, code lost:
        r3 = r77;
        r72 = r1;
        r54 = r4;
        r26 = r5;
        r69 = r6;
        r12 = r50;
        r50 = r52;
        r52 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1022:0x23f2, code lost:
        r3 = r77;
        r72 = r1;
        r54 = r4;
        r26 = r5;
        r69 = r6;
        r52 = r9;
        r48 = true;
        r50 = r13;
        r49 = r15;
        r12 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1023:0x2405, code lost:
        r11 = r48;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1024:0x2407, code lost:
        if (r11 == false) goto L_0x240e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1025:0x2409, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1026:0x240e, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r31);
        org.telegram.tgnet.ConnectionsManager.getInstance(r31).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1027:0x2418, code lost:
        r6 = r19;
        r5 = r26;
        r4 = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1028:0x2420, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1029:0x2421, code lost:
        r1 = r0;
        r6 = r19;
        r5 = r26;
        r4 = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1030:0x2429, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1031:0x242a, code lost:
        r3 = r77;
        r26 = r5;
        r1 = r0;
        r6 = r19;
        r4 = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1061:?, code lost:
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
        if (r1 == 0) goto L_0x23f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x03bf, code lost:
        r48 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x03c3, code lost:
        if ("READ_HISTORY".equals(r5) == false) goto L_0x0454;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:?, code lost:
        r3 = r6.getInt("max_id");
        r12 = new java.util.ArrayList<>();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x03d2, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x03f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x03d4, code lost:
        r49 = r15;
        r15 = new java.lang.StringBuilder();
        r50 = r6;
        r15.append("GCM received read notification max_id = ");
        r15.append(r3);
        r15.append(" for dialogId = ");
        r15.append(r1);
        org.telegram.messenger.FileLog.d(r15.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x03f3, code lost:
        r50 = r6;
        r49 = r15;
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
        r72 = r1;
        r54 = r4;
        r26 = r5;
        r52 = r9;
        r69 = r50;
        r50 = r13;
        r12 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x0454, code lost:
        r50 = r6;
        r49 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x0460, code lost:
        if ("MESSAGE_DELETED".equals(r5) == false) goto L_0x04f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x0462, code lost:
        r6 = r50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:?, code lost:
        r3 = r6.getString("messages");
        r12 = r3.split(",");
        r15 = new androidx.collection.LongSparseArray<>();
        r17 = new java.util.ArrayList<>();
        r25 = r3;
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x0480, code lost:
        r50 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x0483, code lost:
        if (r3 >= r12.length) goto L_0x0497;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x0485, code lost:
        r8 = r17;
        r8.add(org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r12[r3]));
        r3 = r3 + 1;
        r17 = r8;
        r7 = r50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x0497, code lost:
        r8 = r17;
        r3 = r12;
        r52 = r13;
        r15.put(-r9, r8);
        org.telegram.messenger.NotificationsController.getInstance(r31).removeDeletedMessagesFromNotifications(r15);
        org.telegram.messenger.MessagesController.getInstance(r31).deleteMessagesByPush(r1, r8, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x04b6, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x04e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x04b8, code lost:
        org.telegram.messenger.FileLog.d("GCM received " + r5 + " for dialogId = " + r1 + " mids = " + android.text.TextUtils.join(",", r8));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x04e0, code lost:
        r3 = r77;
        r72 = r1;
        r54 = r4;
        r26 = r5;
        r69 = r6;
        r12 = r50;
        r50 = r52;
        r52 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x04f2, code lost:
        r52 = r13;
        r6 = r50;
        r50 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x04fc, code lost:
        if (android.text.TextUtils.isEmpty(r5) != false) goto L_0x23e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x0504, code lost:
        if (r6.has("msg_id") == false) goto L_0x050d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:?, code lost:
        r7 = r6.getInt("msg_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x050d, code lost:
        r7 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x0514, code lost:
        if (r6.has("random_id") == false) goto L_0x0525;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:?, code lost:
        r13 = org.telegram.messenger.Utilities.parseLong(r6.getString("random_id")).longValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x0525, code lost:
        r13 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x0528, code lost:
        if (r7 == 0) goto L_0x056d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:275:0x052a, code lost:
        r8 = org.telegram.messenger.MessagesController.getInstance(r31).dialogs_read_inbox_max.get(java.lang.Long.valueOf(r1));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x053c, code lost:
        if (r8 != null) goto L_0x055d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:277:0x053e, code lost:
        r35 = r8;
        r8 = java.lang.Integer.valueOf(org.telegram.messenger.MessagesStorage.getInstance(r31).getDialogReadMax(false, r1));
        r54 = r4;
        org.telegram.messenger.MessagesController.getInstance(r4).dialogs_read_inbox_max.put(java.lang.Long.valueOf(r1), r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x055d, code lost:
        r54 = r4;
        r35 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0565, code lost:
        if (r7 <= r8.intValue()) goto L_0x056a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x0567, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x056a, code lost:
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x056d, code lost:
        r54 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x0575, code lost:
        if (r13 == 0) goto L_0x0583;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x057f, code lost:
        if (org.telegram.messenger.MessagesStorage.getInstance(r23).checkMessageByRandomId(r13) != false) goto L_0x0583;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x0581, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x0583, code lost:
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x058b, code lost:
        if (r5.startsWith("REACT_") != false) goto L_0x0593;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x0591, code lost:
        if (r5.startsWith("CHAT_REACT_") == false) goto L_0x0594;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x0593, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x0594, code lost:
        if (r8 == false) goto L_0x23ce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x0596, code lost:
        r55 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:?, code lost:
        r57 = r6.optLong("chat_from_id", 0);
        r59 = r6.optLong("chat_from_broadcast_id", 0);
        r61 = r6.optLong("chat_from_group_id", 0);
        r63 = "REACT_";
        r3 = r57;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x05b8, code lost:
        if (r3 != 0) goto L_0x05c1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x05bc, code lost:
        if (r61 == 0) goto L_0x05bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x05bf, code lost:
        r13 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x05c1, code lost:
        r13 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x05c8, code lost:
        if (r6.has("mention") == false) goto L_0x05d4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x05d0, code lost:
        if (r6.getInt("mention") == 0) goto L_0x05d4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x05d2, code lost:
        r14 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x05d4, code lost:
        r14 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x05d5, code lost:
        r57 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x05dd, code lost:
        if (r6.has("silent") == false) goto L_0x05e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x05e5, code lost:
        if (r6.getInt("silent") == 0) goto L_0x05e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x05e7, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x05e9, code lost:
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x05ea, code lost:
        r58 = r8;
        r64 = r14;
        r14 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x05f6, code lost:
        if (r14.has("loc_args") == false) goto L_0x0619;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:?, code lost:
        r8 = r14.getJSONArray("loc_args");
        r49 = r14;
        r14 = new java.lang.String[r8.length()];
        r65 = r3;
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x060d, code lost:
        if (r3 >= r14.length) goto L_0x061f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x060f, code lost:
        r14[r3] = r8.getString(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x0615, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x0619, code lost:
        r65 = r3;
        r49 = r14;
        r14 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:?, code lost:
        r8 = r14[0];
        r39 = r6.has("edit_date");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x063e, code lost:
        if (r5.startsWith("CHAT_") == false) goto L_0x068a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x0644, code lost:
        if (org.telegram.messenger.UserObject.isReplyUser(r1) == false) goto L_0x066c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x0646, code lost:
        r3 = new java.lang.StringBuilder();
        r3.append(r8);
        r68 = null;
        r3.append(" @ ");
        r69 = r6;
        r3.append(r14[1]);
        r8 = r3.toString();
        r3 = null;
        r4 = false;
        r6 = false;
        r70 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x066c, code lost:
        r68 = null;
        r69 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x0674, code lost:
        if (r9 == 0) goto L_0x0678;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x0676, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x0678, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x0679, code lost:
        r36 = r3;
        r34 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:351:0x0680, code lost:
        r8 = r14[1];
        r3 = r34;
        r4 = r36;
        r6 = false;
        r70 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x068a, code lost:
        r68 = null;
        r69 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x0694, code lost:
        if (r5.startsWith("PINNED_") == false) goto L_0x06ac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x069a, code lost:
        if (r9 == 0) goto L_0x069e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x069c, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x069e, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x069f, code lost:
        r36 = r3;
        r3 = null;
        r4 = r36;
        r6 = true;
        r70 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x06b2, code lost:
        if (r5.startsWith("CHANNEL_") == false) goto L_0x06bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x06b4, code lost:
        r3 = null;
        r4 = false;
        r6 = false;
        r70 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x06bf, code lost:
        r3 = null;
        r4 = false;
        r6 = false;
        r70 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x06c9, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x06f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x06cb, code lost:
        r34 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:?, code lost:
        r8 = new java.lang.StringBuilder();
        r71 = r3;
        r8.append("GCM received message notification ");
        r8.append(r5);
        r8.append(" for dialogId = ");
        r8.append(r1);
        r8.append(" mid = ");
        r8.append(r7);
        org.telegram.messenger.FileLog.d(r8.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x06f2, code lost:
        r71 = r3;
        r34 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x06f6, code lost:
        r3 = r63;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x06fc, code lost:
        if (r5.startsWith(r3) != false) goto L_0x2266;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x0702, code lost:
        if (r5.startsWith("CHAT_REACT_") == false) goto L_0x070f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x0704, code lost:
        r63 = r3;
        r74 = r4;
        r72 = r9;
        r16 = "CHAT_REACT_";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x0713, code lost:
        switch(r5.hashCode()) {
            case -2100047043: goto L_0x0CLASSNAME;
            case -2091498420: goto L_0x0CLASSNAME;
            case -2053872415: goto L_0x0c4a;
            case -2039746363: goto L_0x0c3f;
            case -2023218804: goto L_0x0CLASSNAME;
            case -1979538588: goto L_0x0CLASSNAME;
            case -1979536003: goto L_0x0c1e;
            case -1979535888: goto L_0x0CLASSNAME;
            case -1969004705: goto L_0x0CLASSNAME;
            case -1946699248: goto L_0x0bfc;
            case -1717283471: goto L_0x0bf0;
            case -1646640058: goto L_0x0be4;
            case -1528047021: goto L_0x0bd8;
            case -1507149394: goto L_0x0bcd;
            case -1493579426: goto L_0x0bc1;
            case -1482481933: goto L_0x0bb5;
            case -1480102982: goto L_0x0baa;
            case -1478041834: goto L_0x0b9e;
            case -1474543101: goto L_0x0b93;
            case -1465695932: goto L_0x0b87;
            case -1374906292: goto L_0x0b7b;
            case -1372940586: goto L_0x0b6f;
            case -1264245338: goto L_0x0b63;
            case -1236154001: goto L_0x0b57;
            case -1236086700: goto L_0x0b4b;
            case -1236077786: goto L_0x0b3f;
            case -1235796237: goto L_0x0b33;
            case -1235760759: goto L_0x0b27;
            case -1235686303: goto L_0x0b1c;
            case -1198046100: goto L_0x0b11;
            case -1124254527: goto L_0x0b05;
            case -1085137927: goto L_0x0af9;
            case -1084856378: goto L_0x0aed;
            case -1084820900: goto L_0x0ae1;
            case -1084746444: goto L_0x0ad5;
            case -819729482: goto L_0x0ac9;
            case -772141857: goto L_0x0abd;
            case -638310039: goto L_0x0ab1;
            case -590403924: goto L_0x0aa5;
            case -589196239: goto L_0x0a99;
            case -589193654: goto L_0x0a8d;
            case -589193539: goto L_0x0a81;
            case -440169325: goto L_0x0a75;
            case -412748110: goto L_0x0a69;
            case -228518075: goto L_0x0a5d;
            case -213586509: goto L_0x0a51;
            case -115582002: goto L_0x0a45;
            case -112621464: goto L_0x0a39;
            case -108522133: goto L_0x0a2d;
            case -107572034: goto L_0x0a21;
            case -40534265: goto L_0x0a15;
            case 52369421: goto L_0x0a09;
            case 65254746: goto L_0x09fd;
            case 141040782: goto L_0x09f1;
            case 202550149: goto L_0x09e5;
            case 309993049: goto L_0x09d9;
            case 309995634: goto L_0x09cd;
            case 309995749: goto L_0x09c1;
            case 320532812: goto L_0x09b5;
            case 328933854: goto L_0x09a9;
            case 331340546: goto L_0x099d;
            case 342406591: goto L_0x0991;
            case 344816990: goto L_0x0985;
            case 346878138: goto L_0x0979;
            case 350376871: goto L_0x096d;
            case 608430149: goto L_0x0961;
            case 615714517: goto L_0x0956;
            case 715508879: goto L_0x094a;
            case 728985323: goto L_0x093e;
            case 731046471: goto L_0x0932;
            case 734545204: goto L_0x0926;
            case 802032552: goto L_0x091a;
            case 991498806: goto L_0x090e;
            case 1007364121: goto L_0x0902;
            case 1019850010: goto L_0x08f6;
            case 1019917311: goto L_0x08ea;
            case 1019926225: goto L_0x08de;
            case 1020207774: goto L_0x08d2;
            case 1020243252: goto L_0x08c6;
            case 1020317708: goto L_0x08ba;
            case 1060282259: goto L_0x08ae;
            case 1060349560: goto L_0x08a2;
            case 1060358474: goto L_0x0896;
            case 1060640023: goto L_0x088a;
            case 1060675501: goto L_0x087e;
            case 1060749957: goto L_0x0873;
            case 1073049781: goto L_0x0867;
            case 1078101399: goto L_0x085b;
            case 1110103437: goto L_0x084f;
            case 1160762272: goto L_0x0843;
            case 1172918249: goto L_0x0837;
            case 1234591620: goto L_0x082b;
            case 1281128640: goto L_0x081f;
            case 1281131225: goto L_0x0813;
            case 1281131340: goto L_0x0807;
            case 1310789062: goto L_0x07fc;
            case 1333118583: goto L_0x07f0;
            case 1361447897: goto L_0x07e4;
            case 1498266155: goto L_0x07d8;
            case 1533804208: goto L_0x07cc;
            case 1540131626: goto L_0x07c0;
            case 1547988151: goto L_0x07b4;
            case 1561464595: goto L_0x07a8;
            case 1563525743: goto L_0x079c;
            case 1567024476: goto L_0x0790;
            case 1810705077: goto L_0x0784;
            case 1815177512: goto L_0x0778;
            case 1954774321: goto L_0x076c;
            case 1963241394: goto L_0x0760;
            case 2014789757: goto L_0x0754;
            case 2022049433: goto L_0x0748;
            case 2034984710: goto L_0x073c;
            case 2048733346: goto L_0x0730;
            case 2099392181: goto L_0x0724;
            case 2140162142: goto L_0x0718;
            default: goto L_0x0716;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x071e, code lost:
        if (r5.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x0720, code lost:
        r8 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x072a, code lost:
        if (r5.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x072c, code lost:
        r8 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x0736, code lost:
        if (r5.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x0738, code lost:
        r8 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x0742, code lost:
        if (r5.equals("CHANNEL_MESSAGE_PLAYLIST") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x0744, code lost:
        r8 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x074e, code lost:
        if (r5.equals("PINNED_CONTACT") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x0750, code lost:
        r8 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x075a, code lost:
        if (r5.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x075c, code lost:
        r8 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x0766, code lost:
        if (r5.equals("LOCKED_MESSAGE") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x0768, code lost:
        r8 = 'm';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x0772, code lost:
        if (r5.equals("CHAT_MESSAGE_PLAYLIST") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x0774, code lost:
        r8 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x077e, code lost:
        if (r5.equals("CHANNEL_MESSAGES") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x0780, code lost:
        r8 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x078a, code lost:
        if (r5.equals("MESSAGE_INVOICE") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x078c, code lost:
        r8 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x0796, code lost:
        if (r5.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x0798, code lost:
        r8 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x07a2, code lost:
        if (r5.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x07a4, code lost:
        r8 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x07ae, code lost:
        if (r5.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x07b0, code lost:
        r8 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x07ba, code lost:
        if (r5.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x07bc, code lost:
        r8 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x07c6, code lost:
        if (r5.equals("MESSAGE_PLAYLIST") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:426:0x07c8, code lost:
        r8 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x07d2, code lost:
        if (r5.equals("MESSAGE_VIDEOS") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x07d4, code lost:
        r8 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x07de, code lost:
        if (r5.equals("PHONE_CALL_MISSED") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:432:0x07e0, code lost:
        r8 = 'r';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x07ea, code lost:
        if (r5.equals("MESSAGE_PHOTOS") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x07ec, code lost:
        r8 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x07f6, code lost:
        if (r5.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x07f8, code lost:
        r8 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x0802, code lost:
        if (r5.equals("MESSAGE_NOTEXT") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x0804, code lost:
        r8 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x080d, code lost:
        if (r5.equals("MESSAGE_GIF") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:444:0x080f, code lost:
        r8 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x0819, code lost:
        if (r5.equals("MESSAGE_GEO") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x081b, code lost:
        r8 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x0825, code lost:
        if (r5.equals("MESSAGE_DOC") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x0827, code lost:
        r8 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x0831, code lost:
        if (r5.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x0833, code lost:
        r8 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x083d, code lost:
        if (r5.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:456:0x083f, code lost:
        r8 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:458:0x0849, code lost:
        if (r5.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x084b, code lost:
        r8 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x0855, code lost:
        if (r5.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x0857, code lost:
        r8 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x0861, code lost:
        if (r5.equals("CHAT_TITLE_EDITED") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x0863, code lost:
        r8 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x086d, code lost:
        if (r5.equals("PINNED_NOTEXT") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x086f, code lost:
        r8 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x0879, code lost:
        if (r5.equals("MESSAGE_TEXT") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x087b, code lost:
        r8 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0884, code lost:
        if (r5.equals("MESSAGE_QUIZ") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x0886, code lost:
        r8 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x0890, code lost:
        if (r5.equals("MESSAGE_POLL") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x0892, code lost:
        r8 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x089c, code lost:
        if (r5.equals("MESSAGE_GAME") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:480:0x089e, code lost:
        r8 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x08a8, code lost:
        if (r5.equals("MESSAGE_FWDS") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x08aa, code lost:
        r8 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x08b4, code lost:
        if (r5.equals("MESSAGE_DOCS") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:486:0x08b6, code lost:
        r8 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x08c0, code lost:
        if (r5.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x08c2, code lost:
        r8 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x08cc, code lost:
        if (r5.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x08ce, code lost:
        r8 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x08d8, code lost:
        if (r5.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x08da, code lost:
        r8 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x08e4, code lost:
        if (r5.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x08e6, code lost:
        r8 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x08f0, code lost:
        if (r5.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x08f2, code lost:
        r8 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x08fc, code lost:
        if (r5.equals("CHAT_MESSAGE_DOCS") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x08fe, code lost:
        r8 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x0908, code lost:
        if (r5.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x090a, code lost:
        r8 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x0914, code lost:
        if (r5.equals("PINNED_GEOLIVE") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x0916, code lost:
        r8 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x0920, code lost:
        if (r5.equals("MESSAGE_CONTACT") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x0922, code lost:
        r8 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x092c, code lost:
        if (r5.equals("PINNED_VIDEO") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x092e, code lost:
        r8 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x0938, code lost:
        if (r5.equals("PINNED_ROUND") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:519:0x093a, code lost:
        r8 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x0944, code lost:
        if (r5.equals("PINNED_PHOTO") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x0946, code lost:
        r8 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0950, code lost:
        if (r5.equals("PINNED_AUDIO") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x0952, code lost:
        r8 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x095c, code lost:
        if (r5.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x095e, code lost:
        r8 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x0967, code lost:
        if (r5.equals("CHAT_VOICECHAT_INVITE_YOU") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0969, code lost:
        r8 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0973, code lost:
        if (r5.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x0975, code lost:
        r8 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x097f, code lost:
        if (r5.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x0981, code lost:
        r8 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x098b, code lost:
        if (r5.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:540:0x098d, code lost:
        r8 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x0997, code lost:
        if (r5.equals("CHAT_VOICECHAT_END") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x0999, code lost:
        r8 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x09a3, code lost:
        if (r5.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x09a5, code lost:
        r8 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x09af, code lost:
        if (r5.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x09b1, code lost:
        r8 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x09bb, code lost:
        if (r5.equals("MESSAGES") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:552:0x09bd, code lost:
        r8 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x09c7, code lost:
        if (r5.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x09c9, code lost:
        r8 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x09d3, code lost:
        if (r5.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x09d5, code lost:
        r8 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x09df, code lost:
        if (r5.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x09e1, code lost:
        r8 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x09eb, code lost:
        if (r5.equals("CHAT_VOICECHAT_INVITE") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x09ed, code lost:
        r8 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x09f7, code lost:
        if (r5.equals("CHAT_LEFT") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x09f9, code lost:
        r8 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x0a03, code lost:
        if (r5.equals("CHAT_ADD_YOU") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x0a05, code lost:
        r8 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x0a0f, code lost:
        if (r5.equals("REACT_TEXT") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x0a11, code lost:
        r8 = 'i';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x0a1b, code lost:
        if (r5.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:576:0x0a1d, code lost:
        r8 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x0a27, code lost:
        if (r5.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x0a29, code lost:
        r8 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x0a33, code lost:
        if (r5.equals("AUTH_REGION") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:582:0x0a35, code lost:
        r8 = 'l';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x0a3f, code lost:
        if (r5.equals("CONTACT_JOINED") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x0a41, code lost:
        r8 = 'j';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x0a4b, code lost:
        if (r5.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:588:0x0a4d, code lost:
        r8 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x0a57, code lost:
        if (r5.equals("ENCRYPTION_REQUEST") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x0a59, code lost:
        r8 = 'n';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0a63, code lost:
        if (r5.equals("MESSAGE_GEOLIVE") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x0a65, code lost:
        r8 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x0a6f, code lost:
        if (r5.equals("CHAT_DELETE_YOU") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0a71, code lost:
        r8 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0a7b, code lost:
        if (r5.equals("AUTH_UNKNOWN") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:600:0x0a7d, code lost:
        r8 = 'k';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0a87, code lost:
        if (r5.equals("PINNED_GIF") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0a89, code lost:
        r8 = 'g';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0a93, code lost:
        if (r5.equals("PINNED_GEO") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x0a95, code lost:
        r8 = 'b';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x0a9f, code lost:
        if (r5.equals("PINNED_DOC") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x0aa1, code lost:
        r8 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x0aab, code lost:
        if (r5.equals("PINNED_GAME_SCORE") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:612:0x0aad, code lost:
        r8 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x0ab7, code lost:
        if (r5.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x0ab9, code lost:
        r8 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x0ac3, code lost:
        if (r5.equals("PHONE_CALL_REQUEST") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:618:0x0ac5, code lost:
        r8 = 'p';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0acf, code lost:
        if (r5.equals("PINNED_STICKER") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x0ad1, code lost:
        r8 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x0adb, code lost:
        if (r5.equals("PINNED_TEXT") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x0add, code lost:
        r8 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x0ae7, code lost:
        if (r5.equals("PINNED_QUIZ") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:627:0x0ae9, code lost:
        r8 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x0af3, code lost:
        if (r5.equals("PINNED_POLL") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x0af5, code lost:
        r8 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x0aff, code lost:
        if (r5.equals("PINNED_GAME") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x0b01, code lost:
        r8 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x0b0b, code lost:
        if (r5.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x0b0d, code lost:
        r8 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x0b17, code lost:
        if (r5.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x0b19, code lost:
        r8 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x0b22, code lost:
        if (r5.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0b24, code lost:
        r8 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0b2d, code lost:
        if (r5.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x0b2f, code lost:
        r8 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x0b39, code lost:
        if (r5.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x0b3b, code lost:
        r8 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0b45, code lost:
        if (r5.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x0b47, code lost:
        r8 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0b51, code lost:
        if (r5.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0b53, code lost:
        r8 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0b5d, code lost:
        if (r5.equals("CHANNEL_MESSAGE_DOCS") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0b5f, code lost:
        r8 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0b69, code lost:
        if (r5.equals("PINNED_INVOICE") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0b6b, code lost:
        r8 = 'f';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0b75, code lost:
        if (r5.equals("CHAT_RETURNED") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x0b77, code lost:
        r8 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x0b81, code lost:
        if (r5.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0b83, code lost:
        r8 = 'h';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0b8d, code lost:
        if (r5.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0b8f, code lost:
        r8 = 'o';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:671:0x0b99, code lost:
        if (r5.equals("MESSAGE_VIDEO") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0b9b, code lost:
        r8 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0ba4, code lost:
        if (r5.equals("MESSAGE_ROUND") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0ba6, code lost:
        r8 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x0bb0, code lost:
        if (r5.equals("MESSAGE_PHOTO") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0bb2, code lost:
        r8 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0bbb, code lost:
        if (r5.equals("MESSAGE_MUTED") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0bbd, code lost:
        r8 = 'q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0bc7, code lost:
        if (r5.equals("MESSAGE_AUDIO") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0bc9, code lost:
        r8 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0bd3, code lost:
        if (r5.equals("MESSAGE_RECURRING_PAY") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x0bd5, code lost:
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0bde, code lost:
        if (r5.equals("CHAT_MESSAGES") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0be0, code lost:
        r8 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0bea, code lost:
        if (r5.equals("CHAT_VOICECHAT_START") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0bec, code lost:
        r8 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0bf6, code lost:
        if (r5.equals("CHAT_REQ_JOINED") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0bf8, code lost:
        r8 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0CLASSNAME, code lost:
        if (r5.equals("CHAT_JOINED") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0CLASSNAME, code lost:
        r8 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0c0e, code lost:
        if (r5.equals("CHAT_ADD_MEMBER") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0CLASSNAME, code lost:
        r8 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0CLASSNAME, code lost:
        if (r5.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0c1b, code lost:
        r8 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0CLASSNAME, code lost:
        if (r5.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0CLASSNAME, code lost:
        r8 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0c2f, code lost:
        if (r5.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0CLASSNAME, code lost:
        r8 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0c3a, code lost:
        if (r5.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0c3c, code lost:
        r8 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0CLASSNAME, code lost:
        if (r5.equals("MESSAGE_STICKER") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0CLASSNAME, code lost:
        r8 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0CLASSNAME, code lost:
        if (r5.equals("CHAT_CREATED") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0CLASSNAME, code lost:
        r8 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0c5b, code lost:
        if (r5.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0c5d, code lost:
        r8 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0CLASSNAME, code lost:
        if (r5.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0CLASSNAME, code lost:
        r8 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:727:0x0c6b, code lost:
        r8 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0c6c, code lost:
        r16 = "CHAT_REACT_";
        r63 = r3;
        r72 = r9;
        r74 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0CLASSNAME, code lost:
        switch(r8) {
            case 0: goto L_0x221e;
            case 1: goto L_0x21fb;
            case 2: goto L_0x21fb;
            case 3: goto L_0x21d5;
            case 4: goto L_0x21af;
            case 5: goto L_0x2189;
            case 6: goto L_0x2163;
            case 7: goto L_0x213d;
            case 8: goto L_0x2121;
            case 9: goto L_0x20fb;
            case 10: goto L_0x20d5;
            case 11: goto L_0x2067;
            case 12: goto L_0x2041;
            case 13: goto L_0x2016;
            case 14: goto L_0x1feb;
            case 15: goto L_0x1fc0;
            case 16: goto L_0x1f9a;
            case 17: goto L_0x1var_;
            case 18: goto L_0x1f4e;
            case 19: goto L_0x1var_;
            case 20: goto L_0x1efe;
            case 21: goto L_0x1efe;
            case 22: goto L_0x1ed3;
            case 23: goto L_0x1ea1;
            case 24: goto L_0x1e71;
            case 25: goto L_0x1e3f;
            case 26: goto L_0x1e0d;
            case 27: goto L_0x1ddb;
            case 28: goto L_0x1dbe;
            case 29: goto L_0x1d98;
            case 30: goto L_0x1d72;
            case 31: goto L_0x1d4c;
            case 32: goto L_0x1d26;
            case 33: goto L_0x1d00;
            case 34: goto L_0x1CLASSNAME;
            case 35: goto L_0x1c6c;
            case 36: goto L_0x1CLASSNAME;
            case 37: goto L_0x1CLASSNAME;
            case 38: goto L_0x1beb;
            case 39: goto L_0x1bc5;
            case 40: goto L_0x1b9f;
            case 41: goto L_0x1b79;
            case 42: goto L_0x1b53;
            case 43: goto L_0x1b1d;
            case 44: goto L_0x1aed;
            case 45: goto L_0x1abb;
            case 46: goto L_0x1a89;
            case 47: goto L_0x1a57;
            case 48: goto L_0x1a3a;
            case 49: goto L_0x1a11;
            case 50: goto L_0x19e6;
            case 51: goto L_0x19bb;
            case 52: goto L_0x1990;
            case 53: goto L_0x1965;
            case 54: goto L_0x193a;
            case 55: goto L_0x18ad;
            case 56: goto L_0x1882;
            case 57: goto L_0x1852;
            case 58: goto L_0x1822;
            case 59: goto L_0x17f2;
            case 60: goto L_0x17c7;
            case 61: goto L_0x179c;
            case 62: goto L_0x1771;
            case 63: goto L_0x1741;
            case 64: goto L_0x1717;
            case 65: goto L_0x16e7;
            case 66: goto L_0x16c7;
            case 67: goto L_0x16c7;
            case 68: goto L_0x16a7;
            case 69: goto L_0x1687;
            case 70: goto L_0x1662;
            case 71: goto L_0x1642;
            case 72: goto L_0x161d;
            case 73: goto L_0x15fd;
            case 74: goto L_0x15dd;
            case 75: goto L_0x15bd;
            case 76: goto L_0x159d;
            case 77: goto L_0x157d;
            case 78: goto L_0x155d;
            case 79: goto L_0x153d;
            case 80: goto L_0x151d;
            case 81: goto L_0x14e6;
            case 82: goto L_0x14b1;
            case 83: goto L_0x147a;
            case 84: goto L_0x1443;
            case 85: goto L_0x140c;
            case 86: goto L_0x13ea;
            case 87: goto L_0x137d;
            case 88: goto L_0x131a;
            case 89: goto L_0x12b7;
            case 90: goto L_0x1254;
            case 91: goto L_0x11f1;
            case 92: goto L_0x118e;
            case 93: goto L_0x10a7;
            case 94: goto L_0x1044;
            case 95: goto L_0x0fd7;
            case 96: goto L_0x0f6a;
            case 97: goto L_0x0efd;
            case 98: goto L_0x0e9a;
            case 99: goto L_0x0e37;
            case 100: goto L_0x0dd4;
            case 101: goto L_0x0d71;
            case 102: goto L_0x0d0e;
            case 103: goto L_0x0cab;
            case 104: goto L_0x0c8e;
            case 105: goto L_0x0c8b;
            case 106: goto L_0x0CLASSNAME;
            case 107: goto L_0x0CLASSNAME;
            case 108: goto L_0x0CLASSNAME;
            case 109: goto L_0x0CLASSNAME;
            case 110: goto L_0x0CLASSNAME;
            case 111: goto L_0x0CLASSNAME;
            case 112: goto L_0x0CLASSNAME;
            case 113: goto L_0x0CLASSNAME;
            case 114: goto L_0x0CLASSNAME;
            default: goto L_0x0CLASSNAME;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x0c8e, code lost:
        r3 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
        r8 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
        r4 = r3;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0caf, code lost:
        if (r1 <= 0) goto L_0x0cd1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0cb1, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x0cd1, code lost:
        if (r13 == false) goto L_0x0cf3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0cd3, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x0cf3, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r14[0]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x0d12, code lost:
        if (r1 <= 0) goto L_0x0d34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x0d14, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceUser", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x0d34, code lost:
        if (r13 == false) goto L_0x0d56;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x0d36, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x0d56, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r14[0]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0d75, code lost:
        if (r1 <= 0) goto L_0x0d97;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x0d77, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreUser", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x0d97, code lost:
        if (r13 == false) goto L_0x0db9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x0d99, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:0x0db9, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r14[0]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x0dd8, code lost:
        if (r1 <= 0) goto L_0x0dfa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x0dda, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x0dfa, code lost:
        if (r13 == false) goto L_0x0e1c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x0dfc, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x0e1c, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r14[0]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x0e3b, code lost:
        if (r1 <= 0) goto L_0x0e5d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x0e3d, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x0e5d, code lost:
        if (r13 == false) goto L_0x0e7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x0e5f, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:0x0e7f, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r14[0]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x0e9e, code lost:
        if (r1 <= 0) goto L_0x0ec0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x0ea0, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x0ec0, code lost:
        if (r13 == false) goto L_0x0ee2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x0ec2, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x0ee2, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r14[0]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x0var_, code lost:
        if (r1 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x0var_, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x0var_, code lost:
        if (r13 == false) goto L_0x0f4a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x0var_, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r14[0], r14[2], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x0f4a, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x0f6e, code lost:
        if (r1 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x0var_, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x0var_, code lost:
        if (r13 == false) goto L_0x0fb7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x0var_, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r14[0], r14[2], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x0fb7, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x0fdb, code lost:
        if (r1 <= 0) goto L_0x0ffd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x0fdd, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x0ffd, code lost:
        if (r13 == false) goto L_0x1024;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x0fff, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r14[0], r14[2], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x1024, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x1048, code lost:
        if (r1 <= 0) goto L_0x106a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x104a, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x106a, code lost:
        if (r13 == false) goto L_0x108c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x106c, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x108c, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r14[0]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x10ab, code lost:
        if (r1 <= 0) goto L_0x10f4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x10af, code lost:
        if (r14.length <= 1) goto L_0x10d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x10b7, code lost:
        if (android.text.TextUtils.isEmpty(r14[1]) != false) goto L_0x10d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x10b9, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x10d9, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r14[0]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x10f4, code lost:
        if (r13 == false) goto L_0x1147;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x10f8, code lost:
        if (r14.length <= 2) goto L_0x1127;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x1100, code lost:
        if (android.text.TextUtils.isEmpty(r14[2]) != false) goto L_0x1127;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x1102, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r14[0], r14[2], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x1127, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x1149, code lost:
        if (r14.length <= 1) goto L_0x1173;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x1151, code lost:
        if (android.text.TextUtils.isEmpty(r14[1]) != false) goto L_0x1173;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x1153, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x1173, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r14[0]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x1192, code lost:
        if (r1 <= 0) goto L_0x11b4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x1194, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x11b4, code lost:
        if (r13 == false) goto L_0x11d6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x11b6, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x11d6, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r14[0]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x11f5, code lost:
        if (r1 <= 0) goto L_0x1217;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x11f7, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x1217, code lost:
        if (r13 == false) goto L_0x1239;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x1219, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x1239, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r14[0]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x1258, code lost:
        if (r1 <= 0) goto L_0x127a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x125a, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x127a, code lost:
        if (r13 == false) goto L_0x129c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x127c, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x129c, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r14[0]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x12bb, code lost:
        if (r1 <= 0) goto L_0x12dd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x12bd, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x12dd, code lost:
        if (r13 == false) goto L_0x12ff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x12df, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x12ff, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r14[0]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x131e, code lost:
        if (r1 <= 0) goto L_0x1340;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x1320, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x1340, code lost:
        if (r13 == false) goto L_0x1362;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x1342, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x1362, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r14[0]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x1381, code lost:
        if (r1 <= 0) goto L_0x13a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x1383, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x13a3, code lost:
        if (r13 == false) goto L_0x13ca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:851:0x13a5, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r14[0], r14[1], r14[2]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x13ca, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x13ea, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x140c, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r14[0], r14[1], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14[2]).intValue(), new java.lang.Object[0]));
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x1443, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r14[0], r14[1], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14[2]).intValue(), new java.lang.Object[0]));
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:856:0x147a, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r14[0], r14[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14[2]).intValue(), new java.lang.Object[0]));
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x14b1, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r14[0], r14[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14[2]).intValue(), new java.lang.Object[0]));
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x14e6, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r14[0], r14[1], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14[2]).intValue(), new java.lang.Object[0]));
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x151d, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("UserAcceptedToGroupPushWithGroup", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x153d, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x155d, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:862:0x157d, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:863:0x159d, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:864:0x15bd, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:865:0x15dd, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:866:0x15fd, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupEndedCall", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x161d, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r14[0], r14[1], r14[2]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x1642, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x1662, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r14[0], r14[1], r14[2]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x1687, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:871:0x16a7, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x16c7, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r14[0], r14[1]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x16e7, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r14[0], r14[1], r14[2]);
        r68 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x1717, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r14[0], r14[1], r14[2], r14[3]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x1741, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r14[0], r14[1], r14[2]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x1771, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x179c, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x17c7, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x17f2, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r14[0], r14[1], r14[2]);
        r68 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x1822, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r14[0], r14[1], r14[2]);
        r68 = org.telegram.messenger.LocaleController.getString("PollQuiz", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x1852, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r14[0], r14[1], r14[2]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x1882, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x18af, code lost:
        if (r14.length <= 2) goto L_0x18fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x18b7, code lost:
        if (android.text.TextUtils.isEmpty(r14[2]) != false) goto L_0x18fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x18b9, code lost:
        r68 = r14[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r8 = r34;
        r9 = false;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r14[0], r14[1], r14[2]);
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x18fc, code lost:
        r68 = r14[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r8 = r34;
        r9 = false;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r14[0], r14[1]);
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x193a, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x1965, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x1990, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x19bb, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x19e6, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("Message", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x1a11, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r14[0], r14[1], r14[2]);
        r68 = r14[2];
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x1a3a, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r14[0]);
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x1a57, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14[1]).intValue(), new java.lang.Object[0]));
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:897:0x1a89, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14[1]).intValue(), new java.lang.Object[0]));
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x1abb, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14[1]).intValue(), new java.lang.Object[0]));
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:899:0x1aed, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14[1]).intValue(), new java.lang.Object[0]));
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:0x1b1d, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14[1]).intValue(), new java.lang.Object[0]).toLowerCase());
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x1b53, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x1b79, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:0x1b9f, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:0x1bc5, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x1beb, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:906:0x1CLASSNAME, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:0x1CLASSNAME, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x1c6c, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:910:0x1CLASSNAME, code lost:
        if (r14.length <= 1) goto L_0x1cdc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:0x1c9c, code lost:
        if (android.text.TextUtils.isEmpty(r14[1]) != false) goto L_0x1cdc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:913:0x1c9e, code lost:
        r68 = r14[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r8 = r34;
        r9 = false;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r14[0], r14[1]);
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:0x1cdc, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:0x1d00, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x1d26, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:917:0x1d4c, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:918:0x1d72, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:0x1d98, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("Message", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:920:0x1dbe, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r14[0]);
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x1ddb, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14[1]).intValue(), new java.lang.Object[0]));
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x1e0d, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14[1]).intValue(), new java.lang.Object[0]));
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x1e3f, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14[1]).intValue(), new java.lang.Object[0]));
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x1e71, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14[1]).intValue(), new java.lang.Object[0]));
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x1ea1, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r14[0], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14[1]).intValue(), new java.lang.Object[0]));
        r8 = r34;
        r9 = true;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x1ed3, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x1efe, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r14[0], r14[1], r14[2]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:928:0x1var_, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:0x1f4e, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x1var_, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x1f9a, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:932:0x1fc0, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x1feb, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x2016, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:935:0x2041, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:0x2069, code lost:
        if (r14.length <= 1) goto L_0x20b1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x2071, code lost:
        if (android.text.TextUtils.isEmpty(r14[1]) != false) goto L_0x20b1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:0x2073, code lost:
        r68 = r14[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r8 = r34;
        r9 = false;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r14[0], r14[1]);
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:0x20b1, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:0x20d5, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:943:0x20fb, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:944:0x2121, code lost:
        r4 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r14[0]);
        r8 = r34;
        r9 = false;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:0x213d, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:946:0x2163, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x2189, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x21af, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:0x21d5, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r14[0]);
        r68 = org.telegram.messenger.LocaleController.getString("Message", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x21fb, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r14[0], r14[1]);
        r68 = r14[1];
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x221e, code lost:
        r3 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRecurringPay", NUM, r14[0], r14[1]);
        r68 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
        r8 = r34;
        r9 = false;
        r4 = r3;
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x2247, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x225d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x2249, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:0x225d, code lost:
        r3 = r77;
        r8 = r34;
        r9 = false;
        r4 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x2266, code lost:
        r63 = r3;
        r74 = r4;
        r72 = r9;
        r16 = "CHAT_REACT_";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:0x226f, code lost:
        r3 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:?, code lost:
        r4 = r3.getReactedText(r5, r14);
        r8 = r34;
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x2279, code lost:
        if (r4 == null) goto L_0x23b4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x227b, code lost:
        r10 = new org.telegram.tgnet.TLRPC.TL_message();
        r10.id = r7;
        r11 = r13;
        r12 = r55;
        r10.random_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:0x2287, code lost:
        if (r68 == null) goto L_0x228c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:962:0x2289, code lost:
        r15 = r68;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:0x228c, code lost:
        r15 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:964:0x228d, code lost:
        r10.message = r15;
        r15 = r11;
        r55 = r12;
        r10.date = (int) (r79 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:965:0x2299, code lost:
        if (r6 == false) goto L_0x22ab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:967:?, code lost:
        r10.action = new org.telegram.tgnet.TLRPC.TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:968:0x22a3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:969:0x22a4, code lost:
        r1 = r0;
        r6 = r19;
        r4 = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:0x22ab, code lost:
        if (r74 == false) goto L_0x22b4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:971:0x22ad, code lost:
        r10.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:?, code lost:
        r10.dialog_id = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:975:0x22ba, code lost:
        if (r72 == 0) goto L_0x22d2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:977:?, code lost:
        r10.peer_id = new org.telegram.tgnet.TLRPC.TL_peerChannel();
        r12 = r72;
        r10.peer_id.channel_id = r12;
        r72 = r1;
        r1 = r52;
        r52 = r12;
        r12 = r50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:978:0x22d2, code lost:
        r12 = r72;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:979:0x22d8, code lost:
        if (r52 == 0) goto L_0x22ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:980:0x22da, code lost:
        r10.peer_id = new org.telegram.tgnet.TLRPC.TL_peerChat();
        r72 = r1;
        r1 = r52;
        r10.peer_id.chat_id = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:981:0x22e9, code lost:
        r52 = r12;
        r12 = r50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:982:0x22ee, code lost:
        r72 = r1;
        r1 = r52;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:984:?, code lost:
        r10.peer_id = new org.telegram.tgnet.TLRPC.TL_peerUser();
        r52 = r12;
        r10.peer_id.user_id = r50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:985:0x2301, code lost:
        r10.flags |= 256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:987:0x230b, code lost:
        if (r61 == 0) goto L_0x231d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:989:?, code lost:
        r10.from_id = new org.telegram.tgnet.TLRPC.TL_peerChat();
        r10.from_id.chat_id = r1;
        r50 = r1;
        r1 = r65;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:990:0x231d, code lost:
        r50 = r1;
        r1 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x2325, code lost:
        if (r1 == 0) goto L_0x2337;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:992:0x2327, code lost:
        r10.from_id = new org.telegram.tgnet.TLRPC.TL_peerChannel();
        r10.from_id.channel_id = r1;
        r59 = r1;
        r1 = r65;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:994:0x233b, code lost:
        if (r65 == 0) goto L_0x234d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:995:0x233d, code lost:
        r10.from_id = new org.telegram.tgnet.TLRPC.TL_peerUser();
        r59 = r1;
        r1 = r65;
        r10.from_id.user_id = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:996:0x234d, code lost:
        r59 = r1;
        r1 = r65;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:998:?, code lost:
        r10.from_id = r10.peer_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:999:0x2355, code lost:
        if (r64 != false) goto L_0x235c;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1046:0x2468  */
    /* JADX WARNING: Removed duplicated region for block: B:1047:0x2478  */
    /* JADX WARNING: Removed duplicated region for block: B:1050:0x247f  */
    /* renamed from: lambda$onMessageReceived$3$org-telegram-messenger-GcmPushListenerService  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m70xa7b3420a(java.util.Map r78, long r79) {
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
            java.lang.Object r8 = r2.get(r8)     // Catch:{ all -> 0x2460 }
            boolean r9 = r8 instanceof java.lang.String     // Catch:{ all -> 0x2460 }
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
            goto L_0x2465
        L_0x002e:
            r9 = r8
            java.lang.String r9 = (java.lang.String) r9     // Catch:{ all -> 0x2460 }
            r10 = 8
            byte[] r9 = android.util.Base64.decode(r9, r10)     // Catch:{ all -> 0x2460 }
            org.telegram.tgnet.NativeByteBuffer r11 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x2460 }
            int r12 = r9.length     // Catch:{ all -> 0x2460 }
            r11.<init>((int) r12)     // Catch:{ all -> 0x2460 }
            r11.writeBytes((byte[]) r9)     // Catch:{ all -> 0x2460 }
            r12 = 0
            r11.position(r12)     // Catch:{ all -> 0x2460 }
            byte[] r13 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x2460 }
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
            byte[] r13 = new byte[r10]     // Catch:{ all -> 0x2460 }
            r14 = 1
            r11.readBytes(r13, r14)     // Catch:{ all -> 0x2460 }
            byte[] r15 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x2460 }
            boolean r15 = java.util.Arrays.equals(r15, r13)     // Catch:{ all -> 0x2460 }
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
            byte[] r15 = new byte[r15]     // Catch:{ all -> 0x2460 }
            r11.readBytes(r15, r14)     // Catch:{ all -> 0x2460 }
            byte[] r7 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x2460 }
            org.telegram.messenger.MessageKeyData r7 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r7, r15, r14, r10)     // Catch:{ all -> 0x2460 }
            java.nio.ByteBuffer r10 = r11.buffer     // Catch:{ all -> 0x2460 }
            byte[] r14 = r7.aesKey     // Catch:{ all -> 0x2460 }
            byte[] r12 = r7.aesIv     // Catch:{ all -> 0x2460 }
            r21 = 0
            r22 = 0
            r23 = 24
            r27 = r4
            int r4 = r9.length     // Catch:{ all -> 0x245a }
            int r24 = r4 + -24
            r18 = r10
            r19 = r14
            r20 = r12
            org.telegram.messenger.Utilities.aesIgeEncryption(r18, r19, r20, r21, r22, r23, r24)     // Catch:{ all -> 0x245a }
            byte[] r28 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x245a }
            r29 = 96
            r30 = 32
            java.nio.ByteBuffer r4 = r11.buffer     // Catch:{ all -> 0x245a }
            r32 = 24
            java.nio.ByteBuffer r10 = r11.buffer     // Catch:{ all -> 0x245a }
            int r33 = r10.limit()     // Catch:{ all -> 0x245a }
            r31 = r4
            byte[] r4 = org.telegram.messenger.Utilities.computeSHA256(r28, r29, r30, r31, r32, r33)     // Catch:{ all -> 0x245a }
            r10 = 8
            r12 = 0
            boolean r14 = org.telegram.messenger.Utilities.arraysEquals(r15, r12, r4, r10)     // Catch:{ all -> 0x245a }
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
            goto L_0x2465
        L_0x00fe:
            r12 = 1
            int r14 = r11.readInt32(r12)     // Catch:{ all -> 0x245a }
            byte[] r10 = new byte[r14]     // Catch:{ all -> 0x245a }
            r11.readBytes(r10, r12)     // Catch:{ all -> 0x245a }
            java.lang.String r12 = new java.lang.String     // Catch:{ all -> 0x245a }
            r12.<init>(r10)     // Catch:{ all -> 0x245a }
            r6 = r12
            org.json.JSONObject r12 = new org.json.JSONObject     // Catch:{ all -> 0x2452 }
            r12.<init>(r6)     // Catch:{ all -> 0x2452 }
            r18 = r4
            java.lang.String r4 = "loc_key"
            boolean r4 = r12.has(r4)     // Catch:{ all -> 0x2452 }
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
            java.lang.Object r4 = r12.get(r4)     // Catch:{ all -> 0x2448 }
            r19 = r6
            boolean r6 = r4 instanceof org.json.JSONObject     // Catch:{ all -> 0x243e }
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
            goto L_0x2465
        L_0x0144:
            org.json.JSONObject r6 = new org.json.JSONObject     // Catch:{ all -> 0x243e }
            r6.<init>()     // Catch:{ all -> 0x243e }
        L_0x0149:
            r20 = r4
            java.lang.String r4 = "user_id"
            boolean r4 = r12.has(r4)     // Catch:{ all -> 0x243e }
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
            boolean r7 = r4 instanceof java.lang.Long     // Catch:{ all -> 0x243e }
            if (r7 == 0) goto L_0x0184
            r7 = r4
            java.lang.Long r7 = (java.lang.Long) r7     // Catch:{ all -> 0x013b }
            long r22 = r7.longValue()     // Catch:{ all -> 0x013b }
            r75 = r22
            r22 = r8
            r7 = r75
            goto L_0x01b0
        L_0x0184:
            boolean r7 = r4 instanceof java.lang.Integer     // Catch:{ all -> 0x243e }
            if (r7 == 0) goto L_0x0193
            r7 = r4
            java.lang.Integer r7 = (java.lang.Integer) r7     // Catch:{ all -> 0x013b }
            int r7 = r7.intValue()     // Catch:{ all -> 0x013b }
            r22 = r8
            long r7 = (long) r7
            goto L_0x01b0
        L_0x0193:
            r22 = r8
            boolean r7 = r4 instanceof java.lang.String     // Catch:{ all -> 0x243e }
            if (r7 == 0) goto L_0x01a6
            r7 = r4
            java.lang.String r7 = (java.lang.String) r7     // Catch:{ all -> 0x013b }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r7)     // Catch:{ all -> 0x013b }
            int r7 = r7.intValue()     // Catch:{ all -> 0x013b }
            long r7 = (long) r7
            goto L_0x01b0
        L_0x01a6:
            int r7 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x243e }
            org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r7)     // Catch:{ all -> 0x243e }
            long r7 = r7.getClientUserId()     // Catch:{ all -> 0x243e }
        L_0x01b0:
            int r23 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x243e }
            r24 = 0
            r28 = 0
            r29 = r4
            r4 = r28
        L_0x01ba:
            r28 = r9
            r9 = 4
            if (r4 >= r9) goto L_0x01d5
            org.telegram.messenger.UserConfig r30 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x013b }
            long r30 = r30.getClientUserId()     // Catch:{ all -> 0x013b }
            int r32 = (r30 > r7 ? 1 : (r30 == r7 ? 0 : -1))
            if (r32 != 0) goto L_0x01d0
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
            org.telegram.messenger.UserConfig r27 = org.telegram.messenger.UserConfig.getInstance(r31)     // Catch:{ all -> 0x2434 }
            boolean r27 = r27.isClientActivated()     // Catch:{ all -> 0x2434 }
            if (r27 != 0) goto L_0x020c
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
            goto L_0x2465
        L_0x020c:
            java.lang.String r9 = "google.sent_time"
            java.lang.Object r9 = r2.get(r9)     // Catch:{ all -> 0x2434 }
            int r27 = r5.hashCode()     // Catch:{ all -> 0x2434 }
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
            boolean r2 = r6.has(r2)     // Catch:{ all -> 0x2434 }
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
            boolean r2 = r6.has(r2)     // Catch:{ all -> 0x2434 }
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
            boolean r2 = r6.has(r2)     // Catch:{ all -> 0x2434 }
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
            goto L_0x2465
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
            boolean r1 = r6.has(r1)     // Catch:{ all -> 0x2429 }
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
            boolean r11 = r6.has(r11)     // Catch:{ all -> 0x2429 }
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
            int r32 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r32 != 0) goto L_0x03b2
            java.lang.String r11 = "ENCRYPTED_MESSAGE"
            boolean r11 = r11.equals(r5)     // Catch:{ all -> 0x0354 }
            if (r11 == 0) goto L_0x03b2
            long r11 = org.telegram.messenger.NotificationsController.globalSecretChatId     // Catch:{ all -> 0x0354 }
            r1 = r11
        L_0x03b2:
            r11 = 1
            r32 = 0
            int r12 = (r1 > r32 ? 1 : (r1 == r32 ? 0 : -1))
            if (r12 == 0) goto L_0x23f2
            java.lang.String r12 = "READ_HISTORY"
            boolean r12 = r12.equals(r5)     // Catch:{ all -> 0x2429 }
            r48 = r11
            java.lang.String r11 = " for dialogId = "
            if (r12 == 0) goto L_0x0454
            java.lang.String r3 = "max_id"
            int r3 = r6.getInt(r3)     // Catch:{ all -> 0x0354 }
            java.util.ArrayList r12 = new java.util.ArrayList     // Catch:{ all -> 0x0354 }
            r12.<init>()     // Catch:{ all -> 0x0354 }
            boolean r16 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0354 }
            if (r16 == 0) goto L_0x03f3
            r49 = r15
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ all -> 0x0354 }
            r15.<init>()     // Catch:{ all -> 0x0354 }
            r50 = r6
            java.lang.String r6 = "GCM received read notification max_id = "
            r15.append(r6)     // Catch:{ all -> 0x0354 }
            r15.append(r3)     // Catch:{ all -> 0x0354 }
            r15.append(r11)     // Catch:{ all -> 0x0354 }
            r15.append(r1)     // Catch:{ all -> 0x0354 }
            java.lang.String r6 = r15.toString()     // Catch:{ all -> 0x0354 }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ all -> 0x0354 }
            goto L_0x03f7
        L_0x03f3:
            r50 = r6
            r49 = r15
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
            r72 = r1
            r54 = r4
            r26 = r5
            r52 = r9
            r69 = r50
            r50 = r13
            r12 = r7
            goto L_0x2405
        L_0x0454:
            r50 = r6
            r49 = r15
            java.lang.String r6 = "MESSAGE_DELETED"
            boolean r6 = r6.equals(r5)     // Catch:{ all -> 0x2429 }
            java.lang.String r12 = "messages"
            if (r6 == 0) goto L_0x04f2
            r6 = r50
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
        L_0x0480:
            r50 = r7
            int r7 = r12.length     // Catch:{ all -> 0x0354 }
            if (r3 >= r7) goto L_0x0497
            r7 = r12[r3]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r7)     // Catch:{ all -> 0x0354 }
            r8 = r17
            r8.add(r7)     // Catch:{ all -> 0x0354 }
            int r3 = r3 + 1
            r17 = r8
            r7 = r50
            goto L_0x0480
        L_0x0497:
            r8 = r17
            r3 = r12
            r52 = r13
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
            if (r7 == 0) goto L_0x04e0
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
        L_0x04e0:
            r3 = r77
            r72 = r1
            r54 = r4
            r26 = r5
            r69 = r6
            r12 = r50
            r50 = r52
            r52 = r9
            goto L_0x2405
        L_0x04f2:
            r52 = r13
            r6 = r50
            r50 = r7
            boolean r7 = android.text.TextUtils.isEmpty(r5)     // Catch:{ all -> 0x2429 }
            if (r7 != 0) goto L_0x23e1
            java.lang.String r7 = "msg_id"
            boolean r7 = r6.has(r7)     // Catch:{ all -> 0x2429 }
            if (r7 == 0) goto L_0x050d
            java.lang.String r7 = "msg_id"
            int r7 = r6.getInt(r7)     // Catch:{ all -> 0x0354 }
            goto L_0x050e
        L_0x050d:
            r7 = 0
        L_0x050e:
            java.lang.String r8 = "random_id"
            boolean r8 = r6.has(r8)     // Catch:{ all -> 0x2429 }
            if (r8 == 0) goto L_0x0525
            java.lang.String r8 = "random_id"
            java.lang.String r8 = r6.getString(r8)     // Catch:{ all -> 0x0354 }
            java.lang.Long r8 = org.telegram.messenger.Utilities.parseLong(r8)     // Catch:{ all -> 0x0354 }
            long r13 = r8.longValue()     // Catch:{ all -> 0x0354 }
            goto L_0x0527
        L_0x0525:
            r13 = 0
        L_0x0527:
            r8 = 0
            if (r7 == 0) goto L_0x056d
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r31)     // Catch:{ all -> 0x0354 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r15 = r15.dialogs_read_inbox_max     // Catch:{ all -> 0x0354 }
            r34 = r8
            java.lang.Long r8 = java.lang.Long.valueOf(r1)     // Catch:{ all -> 0x0354 }
            java.lang.Object r8 = r15.get(r8)     // Catch:{ all -> 0x0354 }
            java.lang.Integer r8 = (java.lang.Integer) r8     // Catch:{ all -> 0x0354 }
            if (r8 != 0) goto L_0x055d
            org.telegram.messenger.MessagesStorage r15 = org.telegram.messenger.MessagesStorage.getInstance(r31)     // Catch:{ all -> 0x0354 }
            r35 = r8
            r8 = 0
            int r15 = r15.getDialogReadMax(r8, r1)     // Catch:{ all -> 0x0354 }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r15)     // Catch:{ all -> 0x0354 }
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r4)     // Catch:{ all -> 0x0354 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r15 = r15.dialogs_read_inbox_max     // Catch:{ all -> 0x0354 }
            r54 = r4
            java.lang.Long r4 = java.lang.Long.valueOf(r1)     // Catch:{ all -> 0x0354 }
            r15.put(r4, r8)     // Catch:{ all -> 0x0354 }
            goto L_0x0561
        L_0x055d:
            r54 = r4
            r35 = r8
        L_0x0561:
            int r4 = r8.intValue()     // Catch:{ all -> 0x0354 }
            if (r7 <= r4) goto L_0x056a
            r4 = 1
            r8 = r4
            goto L_0x056c
        L_0x056a:
            r8 = r34
        L_0x056c:
            goto L_0x0585
        L_0x056d:
            r54 = r4
            r34 = r8
            r32 = 0
            int r4 = (r13 > r32 ? 1 : (r13 == r32 ? 0 : -1))
            if (r4 == 0) goto L_0x0583
            org.telegram.messenger.MessagesStorage r4 = org.telegram.messenger.MessagesStorage.getInstance(r23)     // Catch:{ all -> 0x0354 }
            boolean r4 = r4.checkMessageByRandomId(r13)     // Catch:{ all -> 0x0354 }
            if (r4 != 0) goto L_0x0583
            r8 = 1
            goto L_0x0585
        L_0x0583:
            r8 = r34
        L_0x0585:
            boolean r4 = r5.startsWith(r3)     // Catch:{ all -> 0x2429 }
            java.lang.String r15 = "CHAT_REACT_"
            if (r4 != 0) goto L_0x0593
            boolean r4 = r5.startsWith(r15)     // Catch:{ all -> 0x0354 }
            if (r4 == 0) goto L_0x0594
        L_0x0593:
            r8 = 1
        L_0x0594:
            if (r8 == 0) goto L_0x23ce
            java.lang.String r4 = "chat_from_id"
            r55 = r13
            r13 = 0
            long r32 = r6.optLong(r4, r13)     // Catch:{ all -> 0x2429 }
            r57 = r32
            java.lang.String r4 = "chat_from_broadcast_id"
            long r32 = r6.optLong(r4, r13)     // Catch:{ all -> 0x2429 }
            r59 = r32
            java.lang.String r4 = "chat_from_group_id"
            long r32 = r6.optLong(r4, r13)     // Catch:{ all -> 0x2429 }
            r61 = r32
            r63 = r3
            r3 = r57
            int r32 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r32 != 0) goto L_0x05c1
            int r34 = (r61 > r13 ? 1 : (r61 == r13 ? 0 : -1))
            if (r34 == 0) goto L_0x05bf
            goto L_0x05c1
        L_0x05bf:
            r13 = 0
            goto L_0x05c2
        L_0x05c1:
            r13 = 1
        L_0x05c2:
            java.lang.String r14 = "mention"
            boolean r14 = r6.has(r14)     // Catch:{ all -> 0x2429 }
            if (r14 == 0) goto L_0x05d4
            java.lang.String r14 = "mention"
            int r14 = r6.getInt(r14)     // Catch:{ all -> 0x0354 }
            if (r14 == 0) goto L_0x05d4
            r14 = 1
            goto L_0x05d5
        L_0x05d4:
            r14 = 0
        L_0x05d5:
            r57 = r8
            java.lang.String r8 = "silent"
            boolean r8 = r6.has(r8)     // Catch:{ all -> 0x2429 }
            if (r8 == 0) goto L_0x05e9
            java.lang.String r8 = "silent"
            int r8 = r6.getInt(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x05e9
            r8 = 1
            goto L_0x05ea
        L_0x05e9:
            r8 = 0
        L_0x05ea:
            r58 = r8
            java.lang.String r8 = "loc_args"
            r64 = r14
            r14 = r49
            boolean r8 = r14.has(r8)     // Catch:{ all -> 0x2429 }
            if (r8 == 0) goto L_0x0619
            java.lang.String r8 = "loc_args"
            org.json.JSONArray r8 = r14.getJSONArray(r8)     // Catch:{ all -> 0x0354 }
            r49 = r14
            int r14 = r8.length()     // Catch:{ all -> 0x0354 }
            java.lang.String[] r14 = new java.lang.String[r14]     // Catch:{ all -> 0x0354 }
            r34 = 0
            r65 = r3
            r3 = r34
        L_0x060c:
            int r4 = r14.length     // Catch:{ all -> 0x0354 }
            if (r3 >= r4) goto L_0x0618
            java.lang.String r4 = r8.getString(r3)     // Catch:{ all -> 0x0354 }
            r14[r3] = r4     // Catch:{ all -> 0x0354 }
            int r3 = r3 + 1
            goto L_0x060c
        L_0x0618:
            goto L_0x061f
        L_0x0619:
            r65 = r3
            r49 = r14
            r3 = 0
            r14 = r3
        L_0x061f:
            r3 = 0
            r4 = 0
            r8 = 0
            r34 = r14[r8]     // Catch:{ all -> 0x2429 }
            r8 = r34
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r67 = r3
            java.lang.String r3 = "edit_date"
            boolean r39 = r6.has(r3)     // Catch:{ all -> 0x2429 }
            java.lang.String r3 = "CHAT_"
            boolean r3 = r5.startsWith(r3)     // Catch:{ all -> 0x2429 }
            if (r3 == 0) goto L_0x068a
            boolean r3 = org.telegram.messenger.UserObject.isReplyUser((long) r1)     // Catch:{ all -> 0x0354 }
            if (r3 == 0) goto L_0x066c
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0354 }
            r3.<init>()     // Catch:{ all -> 0x0354 }
            r3.append(r8)     // Catch:{ all -> 0x0354 }
            r68 = r4
            java.lang.String r4 = " @ "
            r3.append(r4)     // Catch:{ all -> 0x0354 }
            r69 = r6
            r4 = 1
            r6 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3.append(r6)     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0354 }
            r8 = r3
            r3 = r34
            r4 = r36
            r6 = r37
            r70 = r38
            goto L_0x06c7
        L_0x066c:
            r68 = r4
            r69 = r6
            r3 = 0
            int r6 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r6 == 0) goto L_0x0678
            r3 = 1
            goto L_0x0679
        L_0x0678:
            r3 = 0
        L_0x0679:
            r36 = r3
            r34 = r8
            r3 = 1
            r4 = r14[r3]     // Catch:{ all -> 0x0354 }
            r8 = r4
            r3 = r34
            r4 = r36
            r6 = r37
            r70 = r38
            goto L_0x06c7
        L_0x068a:
            r68 = r4
            r69 = r6
            java.lang.String r3 = "PINNED_"
            boolean r3 = r5.startsWith(r3)     // Catch:{ all -> 0x2429 }
            if (r3 == 0) goto L_0x06ac
            r3 = 0
            int r6 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r6 == 0) goto L_0x069e
            r3 = 1
            goto L_0x069f
        L_0x069e:
            r3 = 0
        L_0x069f:
            r36 = r3
            r37 = 1
            r3 = r34
            r4 = r36
            r6 = r37
            r70 = r38
            goto L_0x06c7
        L_0x06ac:
            java.lang.String r3 = "CHANNEL_"
            boolean r3 = r5.startsWith(r3)     // Catch:{ all -> 0x2429 }
            if (r3 == 0) goto L_0x06bf
            r38 = 1
            r3 = r34
            r4 = r36
            r6 = r37
            r70 = r38
            goto L_0x06c7
        L_0x06bf:
            r3 = r34
            r4 = r36
            r6 = r37
            r70 = r38
        L_0x06c7:
            boolean r34 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x2429 }
            if (r34 == 0) goto L_0x06f2
            r34 = r8
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0354 }
            r8.<init>()     // Catch:{ all -> 0x0354 }
            r71 = r3
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
            goto L_0x06f6
        L_0x06f2:
            r71 = r3
            r34 = r8
        L_0x06f6:
            r3 = r63
            boolean r8 = r5.startsWith(r3)     // Catch:{ all -> 0x2429 }
            if (r8 != 0) goto L_0x2266
            boolean r8 = r5.startsWith(r15)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x070f
            r63 = r3
            r74 = r4
            r72 = r9
            r16 = r15
            r12 = 0
            goto L_0x226f
        L_0x070f:
            int r8 = r5.hashCode()     // Catch:{ all -> 0x0354 }
            switch(r8) {
                case -2100047043: goto L_0x0CLASSNAME;
                case -2091498420: goto L_0x0CLASSNAME;
                case -2053872415: goto L_0x0c4a;
                case -2039746363: goto L_0x0c3f;
                case -2023218804: goto L_0x0CLASSNAME;
                case -1979538588: goto L_0x0CLASSNAME;
                case -1979536003: goto L_0x0c1e;
                case -1979535888: goto L_0x0CLASSNAME;
                case -1969004705: goto L_0x0CLASSNAME;
                case -1946699248: goto L_0x0bfc;
                case -1717283471: goto L_0x0bf0;
                case -1646640058: goto L_0x0be4;
                case -1528047021: goto L_0x0bd8;
                case -1507149394: goto L_0x0bcd;
                case -1493579426: goto L_0x0bc1;
                case -1482481933: goto L_0x0bb5;
                case -1480102982: goto L_0x0baa;
                case -1478041834: goto L_0x0b9e;
                case -1474543101: goto L_0x0b93;
                case -1465695932: goto L_0x0b87;
                case -1374906292: goto L_0x0b7b;
                case -1372940586: goto L_0x0b6f;
                case -1264245338: goto L_0x0b63;
                case -1236154001: goto L_0x0b57;
                case -1236086700: goto L_0x0b4b;
                case -1236077786: goto L_0x0b3f;
                case -1235796237: goto L_0x0b33;
                case -1235760759: goto L_0x0b27;
                case -1235686303: goto L_0x0b1c;
                case -1198046100: goto L_0x0b11;
                case -1124254527: goto L_0x0b05;
                case -1085137927: goto L_0x0af9;
                case -1084856378: goto L_0x0aed;
                case -1084820900: goto L_0x0ae1;
                case -1084746444: goto L_0x0ad5;
                case -819729482: goto L_0x0ac9;
                case -772141857: goto L_0x0abd;
                case -638310039: goto L_0x0ab1;
                case -590403924: goto L_0x0aa5;
                case -589196239: goto L_0x0a99;
                case -589193654: goto L_0x0a8d;
                case -589193539: goto L_0x0a81;
                case -440169325: goto L_0x0a75;
                case -412748110: goto L_0x0a69;
                case -228518075: goto L_0x0a5d;
                case -213586509: goto L_0x0a51;
                case -115582002: goto L_0x0a45;
                case -112621464: goto L_0x0a39;
                case -108522133: goto L_0x0a2d;
                case -107572034: goto L_0x0a21;
                case -40534265: goto L_0x0a15;
                case 52369421: goto L_0x0a09;
                case 65254746: goto L_0x09fd;
                case 141040782: goto L_0x09f1;
                case 202550149: goto L_0x09e5;
                case 309993049: goto L_0x09d9;
                case 309995634: goto L_0x09cd;
                case 309995749: goto L_0x09c1;
                case 320532812: goto L_0x09b5;
                case 328933854: goto L_0x09a9;
                case 331340546: goto L_0x099d;
                case 342406591: goto L_0x0991;
                case 344816990: goto L_0x0985;
                case 346878138: goto L_0x0979;
                case 350376871: goto L_0x096d;
                case 608430149: goto L_0x0961;
                case 615714517: goto L_0x0956;
                case 715508879: goto L_0x094a;
                case 728985323: goto L_0x093e;
                case 731046471: goto L_0x0932;
                case 734545204: goto L_0x0926;
                case 802032552: goto L_0x091a;
                case 991498806: goto L_0x090e;
                case 1007364121: goto L_0x0902;
                case 1019850010: goto L_0x08f6;
                case 1019917311: goto L_0x08ea;
                case 1019926225: goto L_0x08de;
                case 1020207774: goto L_0x08d2;
                case 1020243252: goto L_0x08c6;
                case 1020317708: goto L_0x08ba;
                case 1060282259: goto L_0x08ae;
                case 1060349560: goto L_0x08a2;
                case 1060358474: goto L_0x0896;
                case 1060640023: goto L_0x088a;
                case 1060675501: goto L_0x087e;
                case 1060749957: goto L_0x0873;
                case 1073049781: goto L_0x0867;
                case 1078101399: goto L_0x085b;
                case 1110103437: goto L_0x084f;
                case 1160762272: goto L_0x0843;
                case 1172918249: goto L_0x0837;
                case 1234591620: goto L_0x082b;
                case 1281128640: goto L_0x081f;
                case 1281131225: goto L_0x0813;
                case 1281131340: goto L_0x0807;
                case 1310789062: goto L_0x07fc;
                case 1333118583: goto L_0x07f0;
                case 1361447897: goto L_0x07e4;
                case 1498266155: goto L_0x07d8;
                case 1533804208: goto L_0x07cc;
                case 1540131626: goto L_0x07c0;
                case 1547988151: goto L_0x07b4;
                case 1561464595: goto L_0x07a8;
                case 1563525743: goto L_0x079c;
                case 1567024476: goto L_0x0790;
                case 1810705077: goto L_0x0784;
                case 1815177512: goto L_0x0778;
                case 1954774321: goto L_0x076c;
                case 1963241394: goto L_0x0760;
                case 2014789757: goto L_0x0754;
                case 2022049433: goto L_0x0748;
                case 2034984710: goto L_0x073c;
                case 2048733346: goto L_0x0730;
                case 2099392181: goto L_0x0724;
                case 2140162142: goto L_0x0718;
                default: goto L_0x0716;
            }     // Catch:{ all -> 0x0354 }
        L_0x0716:
            goto L_0x0c6b
        L_0x0718:
            java.lang.String r8 = "CHAT_MESSAGE_GEOLIVE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 61
            goto L_0x0c6c
        L_0x0724:
            java.lang.String r8 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 44
            goto L_0x0c6c
        L_0x0730:
            java.lang.String r8 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 29
            goto L_0x0c6c
        L_0x073c:
            java.lang.String r8 = "CHANNEL_MESSAGE_PLAYLIST"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 46
            goto L_0x0c6c
        L_0x0748:
            java.lang.String r8 = "PINNED_CONTACT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 95
            goto L_0x0c6c
        L_0x0754:
            java.lang.String r8 = "CHAT_PHOTO_EDITED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 69
            goto L_0x0c6c
        L_0x0760:
            java.lang.String r8 = "LOCKED_MESSAGE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 109(0x6d, float:1.53E-43)
            goto L_0x0c6c
        L_0x076c:
            java.lang.String r8 = "CHAT_MESSAGE_PLAYLIST"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 84
            goto L_0x0c6c
        L_0x0778:
            java.lang.String r8 = "CHANNEL_MESSAGES"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 48
            goto L_0x0c6c
        L_0x0784:
            java.lang.String r8 = "MESSAGE_INVOICE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 22
            goto L_0x0c6c
        L_0x0790:
            java.lang.String r8 = "CHAT_MESSAGE_VIDEO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 52
            goto L_0x0c6c
        L_0x079c:
            java.lang.String r8 = "CHAT_MESSAGE_ROUND"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 53
            goto L_0x0c6c
        L_0x07a8:
            java.lang.String r8 = "CHAT_MESSAGE_PHOTO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 51
            goto L_0x0c6c
        L_0x07b4:
            java.lang.String r8 = "CHAT_MESSAGE_AUDIO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 56
            goto L_0x0c6c
        L_0x07c0:
            java.lang.String r8 = "MESSAGE_PLAYLIST"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 26
            goto L_0x0c6c
        L_0x07cc:
            java.lang.String r8 = "MESSAGE_VIDEOS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 25
            goto L_0x0c6c
        L_0x07d8:
            java.lang.String r8 = "PHONE_CALL_MISSED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 114(0x72, float:1.6E-43)
            goto L_0x0c6c
        L_0x07e4:
            java.lang.String r8 = "MESSAGE_PHOTOS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 24
            goto L_0x0c6c
        L_0x07f0:
            java.lang.String r8 = "CHAT_MESSAGE_VIDEOS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 83
            goto L_0x0c6c
        L_0x07fc:
            java.lang.String r8 = "MESSAGE_NOTEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 3
            goto L_0x0c6c
        L_0x0807:
            java.lang.String r8 = "MESSAGE_GIF"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 18
            goto L_0x0c6c
        L_0x0813:
            java.lang.String r8 = "MESSAGE_GEO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 16
            goto L_0x0c6c
        L_0x081f:
            java.lang.String r8 = "MESSAGE_DOC"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 10
            goto L_0x0c6c
        L_0x082b:
            java.lang.String r8 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 64
            goto L_0x0c6c
        L_0x0837:
            java.lang.String r8 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 40
            goto L_0x0c6c
        L_0x0843:
            java.lang.String r8 = "CHAT_MESSAGE_PHOTOS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 82
            goto L_0x0c6c
        L_0x084f:
            java.lang.String r8 = "CHAT_MESSAGE_NOTEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 50
            goto L_0x0c6c
        L_0x085b:
            java.lang.String r8 = "CHAT_TITLE_EDITED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 68
            goto L_0x0c6c
        L_0x0867:
            java.lang.String r8 = "PINNED_NOTEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 88
            goto L_0x0c6c
        L_0x0873:
            java.lang.String r8 = "MESSAGE_TEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 1
            goto L_0x0c6c
        L_0x087e:
            java.lang.String r8 = "MESSAGE_QUIZ"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 14
            goto L_0x0c6c
        L_0x088a:
            java.lang.String r8 = "MESSAGE_POLL"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 15
            goto L_0x0c6c
        L_0x0896:
            java.lang.String r8 = "MESSAGE_GAME"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 19
            goto L_0x0c6c
        L_0x08a2:
            java.lang.String r8 = "MESSAGE_FWDS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 23
            goto L_0x0c6c
        L_0x08ae:
            java.lang.String r8 = "MESSAGE_DOCS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 27
            goto L_0x0c6c
        L_0x08ba:
            java.lang.String r8 = "CHAT_MESSAGE_TEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 49
            goto L_0x0c6c
        L_0x08c6:
            java.lang.String r8 = "CHAT_MESSAGE_QUIZ"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 58
            goto L_0x0c6c
        L_0x08d2:
            java.lang.String r8 = "CHAT_MESSAGE_POLL"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 59
            goto L_0x0c6c
        L_0x08de:
            java.lang.String r8 = "CHAT_MESSAGE_GAME"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 63
            goto L_0x0c6c
        L_0x08ea:
            java.lang.String r8 = "CHAT_MESSAGE_FWDS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 81
            goto L_0x0c6c
        L_0x08f6:
            java.lang.String r8 = "CHAT_MESSAGE_DOCS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 85
            goto L_0x0c6c
        L_0x0902:
            java.lang.String r8 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 21
            goto L_0x0c6c
        L_0x090e:
            java.lang.String r8 = "PINNED_GEOLIVE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 99
            goto L_0x0c6c
        L_0x091a:
            java.lang.String r8 = "MESSAGE_CONTACT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 13
            goto L_0x0c6c
        L_0x0926:
            java.lang.String r8 = "PINNED_VIDEO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 90
            goto L_0x0c6c
        L_0x0932:
            java.lang.String r8 = "PINNED_ROUND"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 91
            goto L_0x0c6c
        L_0x093e:
            java.lang.String r8 = "PINNED_PHOTO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 89
            goto L_0x0c6c
        L_0x094a:
            java.lang.String r8 = "PINNED_AUDIO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 94
            goto L_0x0c6c
        L_0x0956:
            java.lang.String r8 = "MESSAGE_PHOTO_SECRET"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 5
            goto L_0x0c6c
        L_0x0961:
            java.lang.String r8 = "CHAT_VOICECHAT_INVITE_YOU"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 74
            goto L_0x0c6c
        L_0x096d:
            java.lang.String r8 = "CHANNEL_MESSAGE_VIDEO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 31
            goto L_0x0c6c
        L_0x0979:
            java.lang.String r8 = "CHANNEL_MESSAGE_ROUND"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 32
            goto L_0x0c6c
        L_0x0985:
            java.lang.String r8 = "CHANNEL_MESSAGE_PHOTO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 30
            goto L_0x0c6c
        L_0x0991:
            java.lang.String r8 = "CHAT_VOICECHAT_END"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 73
            goto L_0x0c6c
        L_0x099d:
            java.lang.String r8 = "CHANNEL_MESSAGE_AUDIO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 35
            goto L_0x0c6c
        L_0x09a9:
            java.lang.String r8 = "CHAT_MESSAGE_STICKER"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 55
            goto L_0x0c6c
        L_0x09b5:
            java.lang.String r8 = "MESSAGES"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 28
            goto L_0x0c6c
        L_0x09c1:
            java.lang.String r8 = "CHAT_MESSAGE_GIF"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 62
            goto L_0x0c6c
        L_0x09cd:
            java.lang.String r8 = "CHAT_MESSAGE_GEO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 60
            goto L_0x0c6c
        L_0x09d9:
            java.lang.String r8 = "CHAT_MESSAGE_DOC"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 54
            goto L_0x0c6c
        L_0x09e5:
            java.lang.String r8 = "CHAT_VOICECHAT_INVITE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 72
            goto L_0x0c6c
        L_0x09f1:
            java.lang.String r8 = "CHAT_LEFT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 77
            goto L_0x0c6c
        L_0x09fd:
            java.lang.String r8 = "CHAT_ADD_YOU"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 67
            goto L_0x0c6c
        L_0x0a09:
            java.lang.String r8 = "REACT_TEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 105(0x69, float:1.47E-43)
            goto L_0x0c6c
        L_0x0a15:
            java.lang.String r8 = "CHAT_DELETE_MEMBER"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 75
            goto L_0x0c6c
        L_0x0a21:
            java.lang.String r8 = "MESSAGE_SCREENSHOT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 8
            goto L_0x0c6c
        L_0x0a2d:
            java.lang.String r8 = "AUTH_REGION"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 108(0x6c, float:1.51E-43)
            goto L_0x0c6c
        L_0x0a39:
            java.lang.String r8 = "CONTACT_JOINED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 106(0x6a, float:1.49E-43)
            goto L_0x0c6c
        L_0x0a45:
            java.lang.String r8 = "CHAT_MESSAGE_INVOICE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 65
            goto L_0x0c6c
        L_0x0a51:
            java.lang.String r8 = "ENCRYPTION_REQUEST"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 110(0x6e, float:1.54E-43)
            goto L_0x0c6c
        L_0x0a5d:
            java.lang.String r8 = "MESSAGE_GEOLIVE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 17
            goto L_0x0c6c
        L_0x0a69:
            java.lang.String r8 = "CHAT_DELETE_YOU"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 76
            goto L_0x0c6c
        L_0x0a75:
            java.lang.String r8 = "AUTH_UNKNOWN"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 107(0x6b, float:1.5E-43)
            goto L_0x0c6c
        L_0x0a81:
            java.lang.String r8 = "PINNED_GIF"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 103(0x67, float:1.44E-43)
            goto L_0x0c6c
        L_0x0a8d:
            java.lang.String r8 = "PINNED_GEO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 98
            goto L_0x0c6c
        L_0x0a99:
            java.lang.String r8 = "PINNED_DOC"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 92
            goto L_0x0c6c
        L_0x0aa5:
            java.lang.String r8 = "PINNED_GAME_SCORE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 101(0x65, float:1.42E-43)
            goto L_0x0c6c
        L_0x0ab1:
            java.lang.String r8 = "CHANNEL_MESSAGE_STICKER"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 34
            goto L_0x0c6c
        L_0x0abd:
            java.lang.String r8 = "PHONE_CALL_REQUEST"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 112(0x70, float:1.57E-43)
            goto L_0x0c6c
        L_0x0ac9:
            java.lang.String r8 = "PINNED_STICKER"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 93
            goto L_0x0c6c
        L_0x0ad5:
            java.lang.String r8 = "PINNED_TEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 87
            goto L_0x0c6c
        L_0x0ae1:
            java.lang.String r8 = "PINNED_QUIZ"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 96
            goto L_0x0c6c
        L_0x0aed:
            java.lang.String r8 = "PINNED_POLL"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 97
            goto L_0x0c6c
        L_0x0af9:
            java.lang.String r8 = "PINNED_GAME"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 100
            goto L_0x0c6c
        L_0x0b05:
            java.lang.String r8 = "CHAT_MESSAGE_CONTACT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 57
            goto L_0x0c6c
        L_0x0b11:
            java.lang.String r8 = "MESSAGE_VIDEO_SECRET"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 7
            goto L_0x0c6c
        L_0x0b1c:
            java.lang.String r8 = "CHANNEL_MESSAGE_TEXT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 2
            goto L_0x0c6c
        L_0x0b27:
            java.lang.String r8 = "CHANNEL_MESSAGE_QUIZ"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 37
            goto L_0x0c6c
        L_0x0b33:
            java.lang.String r8 = "CHANNEL_MESSAGE_POLL"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 38
            goto L_0x0c6c
        L_0x0b3f:
            java.lang.String r8 = "CHANNEL_MESSAGE_GAME"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 42
            goto L_0x0c6c
        L_0x0b4b:
            java.lang.String r8 = "CHANNEL_MESSAGE_FWDS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 43
            goto L_0x0c6c
        L_0x0b57:
            java.lang.String r8 = "CHANNEL_MESSAGE_DOCS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 47
            goto L_0x0c6c
        L_0x0b63:
            java.lang.String r8 = "PINNED_INVOICE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 102(0x66, float:1.43E-43)
            goto L_0x0c6c
        L_0x0b6f:
            java.lang.String r8 = "CHAT_RETURNED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 78
            goto L_0x0c6c
        L_0x0b7b:
            java.lang.String r8 = "ENCRYPTED_MESSAGE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 104(0x68, float:1.46E-43)
            goto L_0x0c6c
        L_0x0b87:
            java.lang.String r8 = "ENCRYPTION_ACCEPT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 111(0x6f, float:1.56E-43)
            goto L_0x0c6c
        L_0x0b93:
            java.lang.String r8 = "MESSAGE_VIDEO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 6
            goto L_0x0c6c
        L_0x0b9e:
            java.lang.String r8 = "MESSAGE_ROUND"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 9
            goto L_0x0c6c
        L_0x0baa:
            java.lang.String r8 = "MESSAGE_PHOTO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 4
            goto L_0x0c6c
        L_0x0bb5:
            java.lang.String r8 = "MESSAGE_MUTED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 113(0x71, float:1.58E-43)
            goto L_0x0c6c
        L_0x0bc1:
            java.lang.String r8 = "MESSAGE_AUDIO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 12
            goto L_0x0c6c
        L_0x0bcd:
            java.lang.String r8 = "MESSAGE_RECURRING_PAY"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 0
            goto L_0x0c6c
        L_0x0bd8:
            java.lang.String r8 = "CHAT_MESSAGES"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 86
            goto L_0x0c6c
        L_0x0be4:
            java.lang.String r8 = "CHAT_VOICECHAT_START"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 71
            goto L_0x0c6c
        L_0x0bf0:
            java.lang.String r8 = "CHAT_REQ_JOINED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 80
            goto L_0x0c6c
        L_0x0bfc:
            java.lang.String r8 = "CHAT_JOINED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 79
            goto L_0x0c6c
        L_0x0CLASSNAME:
            java.lang.String r8 = "CHAT_ADD_MEMBER"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 70
            goto L_0x0c6c
        L_0x0CLASSNAME:
            java.lang.String r8 = "CHANNEL_MESSAGE_GIF"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 41
            goto L_0x0c6c
        L_0x0c1e:
            java.lang.String r8 = "CHANNEL_MESSAGE_GEO"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 39
            goto L_0x0c6c
        L_0x0CLASSNAME:
            java.lang.String r8 = "CHANNEL_MESSAGE_DOC"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 33
            goto L_0x0c6c
        L_0x0CLASSNAME:
            java.lang.String r8 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 45
            goto L_0x0c6c
        L_0x0c3f:
            java.lang.String r8 = "MESSAGE_STICKER"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 11
            goto L_0x0c6c
        L_0x0c4a:
            java.lang.String r8 = "CHAT_CREATED"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 66
            goto L_0x0c6c
        L_0x0CLASSNAME:
            java.lang.String r8 = "CHANNEL_MESSAGE_CONTACT"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 36
            goto L_0x0c6c
        L_0x0CLASSNAME:
            java.lang.String r8 = "MESSAGE_GAME_SCORE"
            boolean r8 = r5.equals(r8)     // Catch:{ all -> 0x0354 }
            if (r8 == 0) goto L_0x0716
            r8 = 20
            goto L_0x0c6c
        L_0x0c6b:
            r8 = -1
        L_0x0c6c:
            java.lang.String r11 = "Photos"
            r16 = r15
            java.lang.String r15 = " "
            r63 = r3
            java.lang.String r3 = "NotificationGroupFew"
            r72 = r9
            java.lang.String r9 = "NotificationMessageFew"
            java.lang.String r10 = "ChannelMessageFew"
            r74 = r4
            java.lang.String r4 = "AttachSticker"
            switch(r8) {
                case 0: goto L_0x221e;
                case 1: goto L_0x21fb;
                case 2: goto L_0x21fb;
                case 3: goto L_0x21d5;
                case 4: goto L_0x21af;
                case 5: goto L_0x2189;
                case 6: goto L_0x2163;
                case 7: goto L_0x213d;
                case 8: goto L_0x2121;
                case 9: goto L_0x20fb;
                case 10: goto L_0x20d5;
                case 11: goto L_0x2067;
                case 12: goto L_0x2041;
                case 13: goto L_0x2016;
                case 14: goto L_0x1feb;
                case 15: goto L_0x1fc0;
                case 16: goto L_0x1f9a;
                case 17: goto L_0x1var_;
                case 18: goto L_0x1f4e;
                case 19: goto L_0x1var_;
                case 20: goto L_0x1efe;
                case 21: goto L_0x1efe;
                case 22: goto L_0x1ed3;
                case 23: goto L_0x1ea1;
                case 24: goto L_0x1e71;
                case 25: goto L_0x1e3f;
                case 26: goto L_0x1e0d;
                case 27: goto L_0x1ddb;
                case 28: goto L_0x1dbe;
                case 29: goto L_0x1d98;
                case 30: goto L_0x1d72;
                case 31: goto L_0x1d4c;
                case 32: goto L_0x1d26;
                case 33: goto L_0x1d00;
                case 34: goto L_0x1CLASSNAME;
                case 35: goto L_0x1c6c;
                case 36: goto L_0x1CLASSNAME;
                case 37: goto L_0x1CLASSNAME;
                case 38: goto L_0x1beb;
                case 39: goto L_0x1bc5;
                case 40: goto L_0x1b9f;
                case 41: goto L_0x1b79;
                case 42: goto L_0x1b53;
                case 43: goto L_0x1b1d;
                case 44: goto L_0x1aed;
                case 45: goto L_0x1abb;
                case 46: goto L_0x1a89;
                case 47: goto L_0x1a57;
                case 48: goto L_0x1a3a;
                case 49: goto L_0x1a11;
                case 50: goto L_0x19e6;
                case 51: goto L_0x19bb;
                case 52: goto L_0x1990;
                case 53: goto L_0x1965;
                case 54: goto L_0x193a;
                case 55: goto L_0x18ad;
                case 56: goto L_0x1882;
                case 57: goto L_0x1852;
                case 58: goto L_0x1822;
                case 59: goto L_0x17f2;
                case 60: goto L_0x17c7;
                case 61: goto L_0x179c;
                case 62: goto L_0x1771;
                case 63: goto L_0x1741;
                case 64: goto L_0x1717;
                case 65: goto L_0x16e7;
                case 66: goto L_0x16c7;
                case 67: goto L_0x16c7;
                case 68: goto L_0x16a7;
                case 69: goto L_0x1687;
                case 70: goto L_0x1662;
                case 71: goto L_0x1642;
                case 72: goto L_0x161d;
                case 73: goto L_0x15fd;
                case 74: goto L_0x15dd;
                case 75: goto L_0x15bd;
                case 76: goto L_0x159d;
                case 77: goto L_0x157d;
                case 78: goto L_0x155d;
                case 79: goto L_0x153d;
                case 80: goto L_0x151d;
                case 81: goto L_0x14e6;
                case 82: goto L_0x14b1;
                case 83: goto L_0x147a;
                case 84: goto L_0x1443;
                case 85: goto L_0x140c;
                case 86: goto L_0x13ea;
                case 87: goto L_0x137d;
                case 88: goto L_0x131a;
                case 89: goto L_0x12b7;
                case 90: goto L_0x1254;
                case 91: goto L_0x11f1;
                case 92: goto L_0x118e;
                case 93: goto L_0x10a7;
                case 94: goto L_0x1044;
                case 95: goto L_0x0fd7;
                case 96: goto L_0x0f6a;
                case 97: goto L_0x0efd;
                case 98: goto L_0x0e9a;
                case 99: goto L_0x0e37;
                case 100: goto L_0x0dd4;
                case 101: goto L_0x0d71;
                case 102: goto L_0x0d0e;
                case 103: goto L_0x0cab;
                case 104: goto L_0x0c8e;
                case 105: goto L_0x0c8b;
                case 106: goto L_0x0CLASSNAME;
                case 107: goto L_0x0CLASSNAME;
                case 108: goto L_0x0CLASSNAME;
                case 109: goto L_0x0CLASSNAME;
                case 110: goto L_0x0CLASSNAME;
                case 111: goto L_0x0CLASSNAME;
                case 112: goto L_0x0CLASSNAME;
                case 113: goto L_0x0CLASSNAME;
                case 114: goto L_0x0CLASSNAME;
                default: goto L_0x0CLASSNAME;
            }
        L_0x0CLASSNAME:
            r12 = 0
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0354 }
            goto L_0x2247
        L_0x0CLASSNAME:
            r12 = 0
            goto L_0x225d
        L_0x0c8b:
            r12 = 0
            goto L_0x225d
        L_0x0c8e:
            java.lang.String r3 = "YouHaveNewMessage"
            r4 = 2131629269(0x7f0e14d5, float:1.8885854E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "SecretChatName"
            r8 = 2131628146(0x7f0e1072, float:1.8883576E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r8 = r4
            r35 = 1
            r12 = 0
            r4 = r3
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0cab:
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x0cd1
            java.lang.String r3 = "NotificationActionPinnedGifUser"
            r4 = 2131626907(0x7f0e0b9b, float:1.8881063E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0cd1:
            if (r13 == 0) goto L_0x0cf3
            java.lang.String r3 = "NotificationActionPinnedGif"
            r4 = 2131626905(0x7f0e0b99, float:1.888106E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0cf3:
            java.lang.String r3 = "NotificationActionPinnedGifChannel"
            r4 = 2131626906(0x7f0e0b9a, float:1.8881061E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0d0e:
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x0d34
            java.lang.String r3 = "NotificationActionPinnedInvoiceUser"
            r4 = 2131626910(0x7f0e0b9e, float:1.888107E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0d34:
            if (r13 == 0) goto L_0x0d56
            java.lang.String r3 = "NotificationActionPinnedInvoice"
            r4 = 2131626908(0x7f0e0b9c, float:1.8881065E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0d56:
            java.lang.String r3 = "NotificationActionPinnedInvoiceChannel"
            r4 = 2131626909(0x7f0e0b9d, float:1.8881068E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0d71:
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x0d97
            java.lang.String r3 = "NotificationActionPinnedGameScoreUser"
            r4 = 2131626897(0x7f0e0b91, float:1.8881043E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0d97:
            if (r13 == 0) goto L_0x0db9
            java.lang.String r3 = "NotificationActionPinnedGameScore"
            r4 = 2131626895(0x7f0e0b8f, float:1.888104E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0db9:
            java.lang.String r3 = "NotificationActionPinnedGameScoreChannel"
            r4 = 2131626896(0x7f0e0b90, float:1.8881041E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0dd4:
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x0dfa
            java.lang.String r3 = "NotificationActionPinnedGameUser"
            r4 = 2131626898(0x7f0e0b92, float:1.8881045E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0dfa:
            if (r13 == 0) goto L_0x0e1c
            java.lang.String r3 = "NotificationActionPinnedGame"
            r4 = 2131626893(0x7f0e0b8d, float:1.8881035E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0e1c:
            java.lang.String r3 = "NotificationActionPinnedGameChannel"
            r4 = 2131626894(0x7f0e0b8e, float:1.8881037E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0e37:
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x0e5d
            java.lang.String r3 = "NotificationActionPinnedGeoLiveUser"
            r4 = 2131626903(0x7f0e0b97, float:1.8881055E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0e5d:
            if (r13 == 0) goto L_0x0e7f
            java.lang.String r3 = "NotificationActionPinnedGeoLive"
            r4 = 2131626901(0x7f0e0b95, float:1.8881051E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0e7f:
            java.lang.String r3 = "NotificationActionPinnedGeoLiveChannel"
            r4 = 2131626902(0x7f0e0b96, float:1.8881053E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0e9a:
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x0ec0
            java.lang.String r3 = "NotificationActionPinnedGeoUser"
            r4 = 2131626904(0x7f0e0b98, float:1.8881057E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0ec0:
            if (r13 == 0) goto L_0x0ee2
            java.lang.String r3 = "NotificationActionPinnedGeo"
            r4 = 2131626899(0x7f0e0b93, float:1.8881047E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0ee2:
            java.lang.String r3 = "NotificationActionPinnedGeoChannel"
            r4 = 2131626900(0x7f0e0b94, float:1.888105E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0efd:
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x0var_
            java.lang.String r3 = "NotificationActionPinnedPollUser"
            r4 = 2131626922(0x7f0e0baa, float:1.8881094E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0var_:
            if (r13 == 0) goto L_0x0f4a
            java.lang.String r3 = "NotificationActionPinnedPoll2"
            r4 = 2131626920(0x7f0e0ba8, float:1.888109E38)
            r8 = 3
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r11 = 1
            r8[r11] = r10     // Catch:{ all -> 0x0354 }
            r10 = r14[r11]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0f4a:
            java.lang.String r3 = "NotificationActionPinnedPollChannel2"
            r4 = 2131626921(0x7f0e0ba9, float:1.8881092E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0f6a:
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x0var_
            java.lang.String r3 = "NotificationActionPinnedQuizUser"
            r4 = 2131626925(0x7f0e0bad, float:1.88811E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0var_:
            if (r13 == 0) goto L_0x0fb7
            java.lang.String r3 = "NotificationActionPinnedQuiz2"
            r4 = 2131626923(0x7f0e0bab, float:1.8881096E38)
            r8 = 3
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r11 = 1
            r8[r11] = r10     // Catch:{ all -> 0x0354 }
            r10 = r14[r11]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0fb7:
            java.lang.String r3 = "NotificationActionPinnedQuizChannel2"
            r4 = 2131626924(0x7f0e0bac, float:1.8881098E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0fd7:
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x0ffd
            java.lang.String r3 = "NotificationActionPinnedContactUser"
            r4 = 2131626889(0x7f0e0b89, float:1.8881027E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x0ffd:
            if (r13 == 0) goto L_0x1024
            java.lang.String r3 = "NotificationActionPinnedContact2"
            r4 = 2131626887(0x7f0e0b87, float:1.8881023E38)
            r8 = 3
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r11 = 1
            r8[r11] = r10     // Catch:{ all -> 0x0354 }
            r10 = r14[r11]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1024:
            java.lang.String r3 = "NotificationActionPinnedContactChannel2"
            r4 = 2131626888(0x7f0e0b88, float:1.8881025E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1044:
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x106a
            java.lang.String r3 = "NotificationActionPinnedVoiceUser"
            r4 = 2131626943(0x7f0e0bbf, float:1.8881136E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x106a:
            if (r13 == 0) goto L_0x108c
            java.lang.String r3 = "NotificationActionPinnedVoice"
            r4 = 2131626941(0x7f0e0bbd, float:1.8881132E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x108c:
            java.lang.String r3 = "NotificationActionPinnedVoiceChannel"
            r4 = 2131626942(0x7f0e0bbe, float:1.8881134E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x10a7:
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x10f4
            int r3 = r14.length     // Catch:{ all -> 0x0354 }
            r4 = 1
            if (r3 <= r4) goto L_0x10d9
            r3 = r14[r4]     // Catch:{ all -> 0x0354 }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0354 }
            if (r3 != 0) goto L_0x10d9
            java.lang.String r3 = "NotificationActionPinnedStickerEmojiUser"
            r4 = 2131626933(0x7f0e0bb5, float:1.8881116E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x10d9:
            java.lang.String r3 = "NotificationActionPinnedStickerUser"
            r4 = 2131626934(0x7f0e0bb6, float:1.8881118E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x10f4:
            if (r13 == 0) goto L_0x1147
            int r3 = r14.length     // Catch:{ all -> 0x0354 }
            r4 = 2
            if (r3 <= r4) goto L_0x1127
            r3 = r14[r4]     // Catch:{ all -> 0x0354 }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0354 }
            if (r3 != 0) goto L_0x1127
            java.lang.String r3 = "NotificationActionPinnedStickerEmoji"
            r4 = 2131626931(0x7f0e0bb3, float:1.8881112E38)
            r8 = 3
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r11 = 1
            r8[r11] = r10     // Catch:{ all -> 0x0354 }
            r10 = r14[r11]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1127:
            java.lang.String r3 = "NotificationActionPinnedSticker"
            r4 = 2131626929(0x7f0e0bb1, float:1.8881108E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1147:
            int r3 = r14.length     // Catch:{ all -> 0x0354 }
            r4 = 1
            if (r3 <= r4) goto L_0x1173
            r3 = r14[r4]     // Catch:{ all -> 0x0354 }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0354 }
            if (r3 != 0) goto L_0x1173
            java.lang.String r3 = "NotificationActionPinnedStickerEmojiChannel"
            r4 = 2131626932(0x7f0e0bb4, float:1.8881114E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1173:
            java.lang.String r3 = "NotificationActionPinnedStickerChannel"
            r4 = 2131626930(0x7f0e0bb2, float:1.888111E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x118e:
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x11b4
            java.lang.String r3 = "NotificationActionPinnedFileUser"
            r4 = 2131626892(0x7f0e0b8c, float:1.8881033E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x11b4:
            if (r13 == 0) goto L_0x11d6
            java.lang.String r3 = "NotificationActionPinnedFile"
            r4 = 2131626890(0x7f0e0b8a, float:1.8881029E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x11d6:
            java.lang.String r3 = "NotificationActionPinnedFileChannel"
            r4 = 2131626891(0x7f0e0b8b, float:1.888103E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x11f1:
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x1217
            java.lang.String r3 = "NotificationActionPinnedRoundUser"
            r4 = 2131626928(0x7f0e0bb0, float:1.8881106E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1217:
            if (r13 == 0) goto L_0x1239
            java.lang.String r3 = "NotificationActionPinnedRound"
            r4 = 2131626926(0x7f0e0bae, float:1.8881102E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1239:
            java.lang.String r3 = "NotificationActionPinnedRoundChannel"
            r4 = 2131626927(0x7f0e0baf, float:1.8881104E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1254:
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x127a
            java.lang.String r3 = "NotificationActionPinnedVideoUser"
            r4 = 2131626940(0x7f0e0bbc, float:1.888113E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x127a:
            if (r13 == 0) goto L_0x129c
            java.lang.String r3 = "NotificationActionPinnedVideo"
            r4 = 2131626938(0x7f0e0bba, float:1.8881126E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x129c:
            java.lang.String r3 = "NotificationActionPinnedVideoChannel"
            r4 = 2131626939(0x7f0e0bbb, float:1.8881128E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x12b7:
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x12dd
            java.lang.String r3 = "NotificationActionPinnedPhotoUser"
            r4 = 2131626919(0x7f0e0ba7, float:1.8881088E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x12dd:
            if (r13 == 0) goto L_0x12ff
            java.lang.String r3 = "NotificationActionPinnedPhoto"
            r4 = 2131626917(0x7f0e0ba5, float:1.8881084E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x12ff:
            java.lang.String r3 = "NotificationActionPinnedPhotoChannel"
            r4 = 2131626918(0x7f0e0ba6, float:1.8881086E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x131a:
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x1340
            java.lang.String r3 = "NotificationActionPinnedNoTextUser"
            r4 = 2131626916(0x7f0e0ba4, float:1.8881082E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1340:
            if (r13 == 0) goto L_0x1362
            java.lang.String r3 = "NotificationActionPinnedNoText"
            r4 = 2131626914(0x7f0e0ba2, float:1.8881078E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1362:
            java.lang.String r3 = "NotificationActionPinnedNoTextChannel"
            r4 = 2131626915(0x7f0e0ba3, float:1.888108E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x137d:
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x13a3
            java.lang.String r3 = "NotificationActionPinnedTextUser"
            r4 = 2131626937(0x7f0e0bb9, float:1.8881124E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x13a3:
            if (r13 == 0) goto L_0x13ca
            java.lang.String r3 = "NotificationActionPinnedText"
            r4 = 2131626935(0x7f0e0bb7, float:1.888112E38)
            r8 = 3
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x13ca:
            java.lang.String r3 = "NotificationActionPinnedTextChannel"
            r4 = 2131626936(0x7f0e0bb8, float:1.8881122E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x13ea:
            java.lang.String r3 = "NotificationGroupAlbum"
            r4 = 2131626952(0x7f0e0bc8, float:1.8881155E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x140c:
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r4[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r4[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r8 = "Files"
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x0354 }
            int r9 = r9.intValue()     // Catch:{ all -> 0x0354 }
            r10 = 0
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ all -> 0x0354 }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r9, r11)     // Catch:{ all -> 0x0354 }
            r9 = 2
            r4[r9] = r8     // Catch:{ all -> 0x0354 }
            r8 = 2131626955(0x7f0e0bcb, float:1.888116E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r8, r4)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1443:
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r4[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r4[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r8 = "MusicFiles"
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x0354 }
            int r9 = r9.intValue()     // Catch:{ all -> 0x0354 }
            r10 = 0
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ all -> 0x0354 }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r9, r11)     // Catch:{ all -> 0x0354 }
            r9 = 2
            r4[r9] = r8     // Catch:{ all -> 0x0354 }
            r8 = 2131626955(0x7f0e0bcb, float:1.888116E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r8, r4)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x147a:
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r4[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r4[r8] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r8 = "Videos"
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x0354 }
            int r9 = r9.intValue()     // Catch:{ all -> 0x0354 }
            r10 = 0
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ all -> 0x0354 }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r9, r11)     // Catch:{ all -> 0x0354 }
            r9 = 2
            r4[r9] = r8     // Catch:{ all -> 0x0354 }
            r8 = 2131626955(0x7f0e0bcb, float:1.888116E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r8, r4)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x14b1:
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r4[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            r4[r8] = r9     // Catch:{ all -> 0x0354 }
            r8 = 2
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x0354 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0354 }
            r9 = 0
            java.lang.Object[] r10 = new java.lang.Object[r9]     // Catch:{ all -> 0x0354 }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r11, r8, r10)     // Catch:{ all -> 0x0354 }
            r9 = 2
            r4[r9] = r8     // Catch:{ all -> 0x0354 }
            r8 = 2131626955(0x7f0e0bcb, float:1.888116E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r8, r4)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x14e6:
            java.lang.String r3 = "NotificationGroupForwardedFew"
            r4 = 2131626956(0x7f0e0bcc, float:1.8881163E38)
            r8 = 3
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x0354 }
            int r9 = r9.intValue()     // Catch:{ all -> 0x0354 }
            r10 = 0
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ all -> 0x0354 }
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatPluralString(r12, r9, r11)     // Catch:{ all -> 0x0354 }
            r10 = 2
            r8[r10] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x151d:
            java.lang.String r3 = "UserAcceptedToGroupPushWithGroup"
            r4 = 2131628800(0x7f0e1300, float:1.8884903E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x153d:
            java.lang.String r3 = "NotificationGroupAddSelfMega"
            r4 = 2131626951(0x7f0e0bc7, float:1.8881153E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x155d:
            java.lang.String r3 = "NotificationGroupAddSelf"
            r4 = 2131626950(0x7f0e0bc6, float:1.888115E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x157d:
            java.lang.String r3 = "NotificationGroupLeftMember"
            r4 = 2131626961(0x7f0e0bd1, float:1.8881173E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x159d:
            java.lang.String r3 = "NotificationGroupKickYou"
            r4 = 2131626960(0x7f0e0bd0, float:1.888117E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x15bd:
            java.lang.String r3 = "NotificationGroupKickMember"
            r4 = 2131626959(0x7f0e0bcf, float:1.8881169E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x15dd:
            java.lang.String r3 = "NotificationGroupInvitedYouToCall"
            r4 = 2131626958(0x7f0e0bce, float:1.8881167E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x15fd:
            java.lang.String r3 = "NotificationGroupEndedCall"
            r4 = 2131626954(0x7f0e0bca, float:1.8881159E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x161d:
            java.lang.String r3 = "NotificationGroupInvitedToCall"
            r4 = 2131626957(0x7f0e0bcd, float:1.8881165E38)
            r8 = 3
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1642:
            java.lang.String r3 = "NotificationGroupCreatedCall"
            r4 = 2131626953(0x7f0e0bc9, float:1.8881157E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1662:
            java.lang.String r3 = "NotificationGroupAddMember"
            r4 = 2131626949(0x7f0e0bc5, float:1.8881149E38)
            r8 = 3
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1687:
            java.lang.String r3 = "NotificationEditedGroupPhoto"
            r4 = 2131626947(0x7f0e0bc3, float:1.8881145E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x16a7:
            java.lang.String r3 = "NotificationEditedGroupName"
            r4 = 2131626946(0x7f0e0bc2, float:1.8881143E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x16c7:
            java.lang.String r3 = "NotificationInvitedToGroup"
            r4 = 2131626966(0x7f0e0bd6, float:1.8881183E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x16e7:
            java.lang.String r3 = "NotificationMessageGroupInvoice"
            r4 = 2131626983(0x7f0e0be7, float:1.8881218E38)
            r8 = 3
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "PaymentInvoice"
            r8 = 2131627393(0x7f0e0d81, float:1.888205E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1717:
            java.lang.String r3 = "NotificationMessageGroupGameScored"
            r4 = 2131626981(0x7f0e0be5, float:1.8881214E38)
            r8 = 4
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 3
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1741:
            java.lang.String r3 = "NotificationMessageGroupGame"
            r4 = 2131626980(0x7f0e0be4, float:1.8881212E38)
            r8 = 3
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachGame"
            r8 = 2131624482(0x7f0e0222, float:1.8876145E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1771:
            java.lang.String r3 = "NotificationMessageGroupGif"
            r4 = 2131626982(0x7f0e0be6, float:1.8881216E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachGif"
            r8 = 2131624483(0x7f0e0223, float:1.8876147E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x179c:
            java.lang.String r3 = "NotificationMessageGroupLiveLocation"
            r4 = 2131626984(0x7f0e0be8, float:1.888122E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachLiveLocation"
            r8 = 2131624488(0x7f0e0228, float:1.8876157E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x17c7:
            java.lang.String r3 = "NotificationMessageGroupMap"
            r4 = 2131626985(0x7f0e0be9, float:1.8881222E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachLocation"
            r8 = 2131624492(0x7f0e022c, float:1.8876165E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x17f2:
            java.lang.String r3 = "NotificationMessageGroupPoll2"
            r4 = 2131626989(0x7f0e0bed, float:1.888123E38)
            r8 = 3
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Poll"
            r8 = 2131627577(0x7f0e0e39, float:1.8882422E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1822:
            java.lang.String r3 = "NotificationMessageGroupQuiz2"
            r4 = 2131626990(0x7f0e0bee, float:1.8881232E38)
            r8 = 3
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "PollQuiz"
            r8 = 2131627584(0x7f0e0e40, float:1.8882437E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1852:
            java.lang.String r3 = "NotificationMessageGroupContact2"
            r4 = 2131626978(0x7f0e0be2, float:1.8881207E38)
            r8 = 3
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachContact"
            r8 = 2131624478(0x7f0e021e, float:1.8876137E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1882:
            java.lang.String r3 = "NotificationMessageGroupAudio"
            r4 = 2131626977(0x7f0e0be1, float:1.8881205E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachAudio"
            r8 = 2131624476(0x7f0e021c, float:1.8876133E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x18ad:
            int r3 = r14.length     // Catch:{ all -> 0x0354 }
            r8 = 2
            if (r3 <= r8) goto L_0x18fc
            r3 = r14[r8]     // Catch:{ all -> 0x0354 }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0354 }
            if (r3 != 0) goto L_0x18fc
            java.lang.String r3 = "NotificationMessageGroupStickerEmoji"
            r8 = 2131626993(0x7f0e0bf1, float:1.8881238E38)
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
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r8, r9)     // Catch:{ all -> 0x0354 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0354 }
            r8.<init>()     // Catch:{ all -> 0x0354 }
            r9 = 2
            r9 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8.append(r9)     // Catch:{ all -> 0x0354 }
            r8.append(r15)     // Catch:{ all -> 0x0354 }
            r9 = 2131624505(0x7f0e0239, float:1.8876192E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r9)     // Catch:{ all -> 0x0354 }
            r8.append(r4)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = r8.toString()     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x18fc:
            java.lang.String r3 = "NotificationMessageGroupSticker"
            r8 = 2131626992(0x7f0e0bf0, float:1.8881236E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch:{ all -> 0x0354 }
            r10 = 0
            r11 = r14[r10]     // Catch:{ all -> 0x0354 }
            r9[r10] = r11     // Catch:{ all -> 0x0354 }
            r10 = 1
            r11 = r14[r10]     // Catch:{ all -> 0x0354 }
            r9[r10] = r11     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r8, r9)     // Catch:{ all -> 0x0354 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0354 }
            r8.<init>()     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8.append(r10)     // Catch:{ all -> 0x0354 }
            r8.append(r15)     // Catch:{ all -> 0x0354 }
            r9 = 2131624505(0x7f0e0239, float:1.8876192E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r9)     // Catch:{ all -> 0x0354 }
            r8.append(r4)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = r8.toString()     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x193a:
            java.lang.String r3 = "NotificationMessageGroupDocument"
            r4 = 2131626979(0x7f0e0be3, float:1.888121E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachDocument"
            r8 = 2131624481(0x7f0e0221, float:1.8876143E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1965:
            java.lang.String r3 = "NotificationMessageGroupRound"
            r4 = 2131626991(0x7f0e0bef, float:1.8881234E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachRound"
            r8 = 2131624504(0x7f0e0238, float:1.887619E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1990:
            java.lang.String r3 = "NotificationMessageGroupVideo"
            r4 = 2131626995(0x7f0e0bf3, float:1.8881242E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachVideo"
            r8 = 2131624508(0x7f0e023c, float:1.8876198E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x19bb:
            java.lang.String r3 = "NotificationMessageGroupPhoto"
            r4 = 2131626988(0x7f0e0bec, float:1.8881228E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachPhoto"
            r8 = 2131624502(0x7f0e0236, float:1.8876186E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x19e6:
            java.lang.String r3 = "NotificationMessageGroupNoText"
            r4 = 2131626987(0x7f0e0beb, float:1.8881226E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Message"
            r8 = 2131626622(0x7f0e0a7e, float:1.8880485E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1a11:
            java.lang.String r3 = "NotificationMessageGroupText"
            r4 = 2131626994(0x7f0e0bf2, float:1.888124E38)
            r8 = 3
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r4 = r14[r9]     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1a3a:
            java.lang.String r3 = "ChannelMessageAlbum"
            r4 = 2131624919(0x7f0e03d7, float:1.8877031E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1a57:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r4 = 0
            r8 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r8     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Files"
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x0354 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0354 }
            r9 = 0
            java.lang.Object[] r11 = new java.lang.Object[r9]     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r8, r11)     // Catch:{ all -> 0x0354 }
            r8 = 1
            r3[r8] = r4     // Catch:{ all -> 0x0354 }
            r4 = 2131624923(0x7f0e03db, float:1.887704E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r10, r4, r3)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1a89:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r4 = 0
            r8 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r8     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "MusicFiles"
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x0354 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0354 }
            r9 = 0
            java.lang.Object[] r11 = new java.lang.Object[r9]     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r8, r11)     // Catch:{ all -> 0x0354 }
            r8 = 1
            r3[r8] = r4     // Catch:{ all -> 0x0354 }
            r4 = 2131624923(0x7f0e03db, float:1.887704E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r10, r4, r3)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1abb:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r4 = 0
            r8 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r8     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Videos"
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x0354 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0354 }
            r9 = 0
            java.lang.Object[] r11 = new java.lang.Object[r9]     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r8, r11)     // Catch:{ all -> 0x0354 }
            r8 = 1
            r3[r8] = r4     // Catch:{ all -> 0x0354 }
            r4 = 2131624923(0x7f0e03db, float:1.887704E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r10, r4, r3)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1aed:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r4 = 0
            r8 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r8     // Catch:{ all -> 0x0354 }
            r4 = 1
            r8 = r14[r4]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)     // Catch:{ all -> 0x0354 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x0354 }
            r8 = 0
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r11, r4, r9)     // Catch:{ all -> 0x0354 }
            r8 = 1
            r3[r8] = r4     // Catch:{ all -> 0x0354 }
            r4 = 2131624923(0x7f0e03db, float:1.887704E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r10, r4, r3)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1b1d:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r4 = 0
            r8 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r8     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "ForwardedMessageCount"
            r8 = 1
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x0354 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0354 }
            r9 = 0
            java.lang.Object[] r11 = new java.lang.Object[r9]     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r8, r11)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = r4.toLowerCase()     // Catch:{ all -> 0x0354 }
            r8 = 1
            r3[r8] = r4     // Catch:{ all -> 0x0354 }
            r4 = 2131624923(0x7f0e03db, float:1.887704E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r10, r4, r3)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1b53:
            java.lang.String r3 = "NotificationMessageGame"
            r4 = 2131626974(0x7f0e0bde, float:1.88812E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachGame"
            r8 = 2131624482(0x7f0e0222, float:1.8876145E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1b79:
            java.lang.String r3 = "ChannelMessageGIF"
            r4 = 2131624924(0x7f0e03dc, float:1.8877041E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachGif"
            r8 = 2131624483(0x7f0e0223, float:1.8876147E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1b9f:
            java.lang.String r3 = "ChannelMessageLiveLocation"
            r4 = 2131624925(0x7f0e03dd, float:1.8877043E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachLiveLocation"
            r8 = 2131624488(0x7f0e0228, float:1.8876157E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1bc5:
            java.lang.String r3 = "ChannelMessageMap"
            r4 = 2131624926(0x7f0e03de, float:1.8877045E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachLocation"
            r8 = 2131624492(0x7f0e022c, float:1.8876165E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1beb:
            java.lang.String r3 = "ChannelMessagePoll2"
            r4 = 2131624930(0x7f0e03e2, float:1.8877054E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Poll"
            r8 = 2131627577(0x7f0e0e39, float:1.8882422E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1CLASSNAME:
            java.lang.String r3 = "ChannelMessageQuiz2"
            r4 = 2131624931(0x7f0e03e3, float:1.8877056E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "QuizPoll"
            r8 = 2131627823(0x7f0e0f2f, float:1.8882921E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1CLASSNAME:
            java.lang.String r3 = "ChannelMessageContact2"
            r4 = 2131624921(0x7f0e03d9, float:1.8877035E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachContact"
            r8 = 2131624478(0x7f0e021e, float:1.8876137E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1c6c:
            java.lang.String r3 = "ChannelMessageAudio"
            r4 = 2131624920(0x7f0e03d8, float:1.8877033E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachAudio"
            r8 = 2131624476(0x7f0e021c, float:1.8876133E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1CLASSNAME:
            int r3 = r14.length     // Catch:{ all -> 0x0354 }
            r8 = 1
            if (r3 <= r8) goto L_0x1cdc
            r3 = r14[r8]     // Catch:{ all -> 0x0354 }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0354 }
            if (r3 != 0) goto L_0x1cdc
            java.lang.String r3 = "ChannelMessageStickerEmoji"
            r8 = 2131624934(0x7f0e03e6, float:1.8877062E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch:{ all -> 0x0354 }
            r10 = 0
            r11 = r14[r10]     // Catch:{ all -> 0x0354 }
            r9[r10] = r11     // Catch:{ all -> 0x0354 }
            r10 = 1
            r11 = r14[r10]     // Catch:{ all -> 0x0354 }
            r9[r10] = r11     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r8, r9)     // Catch:{ all -> 0x0354 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0354 }
            r8.<init>()     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8.append(r10)     // Catch:{ all -> 0x0354 }
            r8.append(r15)     // Catch:{ all -> 0x0354 }
            r9 = 2131624505(0x7f0e0239, float:1.8876192E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r9)     // Catch:{ all -> 0x0354 }
            r8.append(r4)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = r8.toString()     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1cdc:
            java.lang.String r3 = "ChannelMessageSticker"
            r8 = 2131624933(0x7f0e03e5, float:1.887706E38)
            r9 = 1
            java.lang.Object[] r10 = new java.lang.Object[r9]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r11 = r14[r9]     // Catch:{ all -> 0x0354 }
            r10[r9] = r11     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r8, r10)     // Catch:{ all -> 0x0354 }
            r8 = 2131624505(0x7f0e0239, float:1.8876192E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1d00:
            java.lang.String r3 = "ChannelMessageDocument"
            r4 = 2131624922(0x7f0e03da, float:1.8877037E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachDocument"
            r8 = 2131624481(0x7f0e0221, float:1.8876143E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1d26:
            java.lang.String r3 = "ChannelMessageRound"
            r4 = 2131624932(0x7f0e03e4, float:1.8877058E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachRound"
            r8 = 2131624504(0x7f0e0238, float:1.887619E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1d4c:
            java.lang.String r3 = "ChannelMessageVideo"
            r4 = 2131624935(0x7f0e03e7, float:1.8877064E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachVideo"
            r8 = 2131624508(0x7f0e023c, float:1.8876198E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1d72:
            java.lang.String r3 = "ChannelMessagePhoto"
            r4 = 2131624929(0x7f0e03e1, float:1.8877052E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachPhoto"
            r8 = 2131624502(0x7f0e0236, float:1.8876186E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1d98:
            java.lang.String r3 = "ChannelMessageNoText"
            r4 = 2131624928(0x7f0e03e0, float:1.887705E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Message"
            r8 = 2131626622(0x7f0e0a7e, float:1.8880485E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1dbe:
            java.lang.String r3 = "NotificationMessageAlbum"
            r4 = 2131626968(0x7f0e0bd8, float:1.8881187E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1ddb:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r4 = 0
            r8 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r8     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Files"
            r8 = 1
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x0354 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0354 }
            r10 = 0
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r8, r11)     // Catch:{ all -> 0x0354 }
            r8 = 1
            r3[r8] = r4     // Catch:{ all -> 0x0354 }
            r4 = 2131626972(0x7f0e0bdc, float:1.8881195E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r9, r4, r3)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1e0d:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r4 = 0
            r8 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r8     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "MusicFiles"
            r8 = 1
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x0354 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0354 }
            r10 = 0
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r8, r11)     // Catch:{ all -> 0x0354 }
            r8 = 1
            r3[r8] = r4     // Catch:{ all -> 0x0354 }
            r4 = 2131626972(0x7f0e0bdc, float:1.8881195E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r9, r4, r3)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1e3f:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r4 = 0
            r8 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r8     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Videos"
            r8 = 1
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x0354 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0354 }
            r10 = 0
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r8, r11)     // Catch:{ all -> 0x0354 }
            r8 = 1
            r3[r8] = r4     // Catch:{ all -> 0x0354 }
            r4 = 2131626972(0x7f0e0bdc, float:1.8881195E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r9, r4, r3)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1e71:
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0354 }
            r4 = 0
            r8 = r14[r4]     // Catch:{ all -> 0x0354 }
            r3[r4] = r8     // Catch:{ all -> 0x0354 }
            r4 = 1
            r8 = r14[r4]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)     // Catch:{ all -> 0x0354 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x0354 }
            r8 = 0
            java.lang.Object[] r10 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r11, r4, r10)     // Catch:{ all -> 0x0354 }
            r8 = 1
            r3[r8] = r4     // Catch:{ all -> 0x0354 }
            r4 = 2131626972(0x7f0e0bdc, float:1.8881195E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r9, r4, r3)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1ea1:
            java.lang.String r3 = "NotificationMessageForwardFew"
            r4 = 2131626973(0x7f0e0bdd, float:1.8881197E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x0354 }
            int r9 = r9.intValue()     // Catch:{ all -> 0x0354 }
            r10 = 0
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ all -> 0x0354 }
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatPluralString(r12, r9, r11)     // Catch:{ all -> 0x0354 }
            r10 = 1
            r8[r10] = r9     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r35 = 1
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1ed3:
            java.lang.String r3 = "NotificationMessageInvoice"
            r4 = 2131626996(0x7f0e0bf4, float:1.8881244E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "PaymentInvoice"
            r8 = 2131627393(0x7f0e0d81, float:1.888205E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1efe:
            java.lang.String r3 = "NotificationMessageGameScored"
            r4 = 2131626975(0x7f0e0bdf, float:1.8881201E38)
            r8 = 3
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 2
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x1var_:
            java.lang.String r3 = "NotificationMessageGame"
            r4 = 2131626974(0x7f0e0bde, float:1.88812E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachGame"
            r8 = 2131624482(0x7f0e0222, float:1.8876145E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1f4e:
            java.lang.String r3 = "NotificationMessageGif"
            r4 = 2131626976(0x7f0e0be0, float:1.8881203E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachGif"
            r8 = 2131624483(0x7f0e0223, float:1.8876147E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1var_:
            java.lang.String r3 = "NotificationMessageLiveLocation"
            r4 = 2131626997(0x7f0e0bf5, float:1.8881246E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachLiveLocation"
            r8 = 2131624488(0x7f0e0228, float:1.8876157E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1f9a:
            java.lang.String r3 = "NotificationMessageMap"
            r4 = 2131626998(0x7f0e0bf6, float:1.8881248E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachLocation"
            r8 = 2131624492(0x7f0e022c, float:1.8876165E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1fc0:
            java.lang.String r3 = "NotificationMessagePoll2"
            r4 = 2131627002(0x7f0e0bfa, float:1.8881256E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Poll"
            r8 = 2131627577(0x7f0e0e39, float:1.8882422E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x1feb:
            java.lang.String r3 = "NotificationMessageQuiz2"
            r4 = 2131627003(0x7f0e0bfb, float:1.8881258E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "QuizPoll"
            r8 = 2131627823(0x7f0e0f2f, float:1.8882921E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x2016:
            java.lang.String r3 = "NotificationMessageContact2"
            r4 = 2131626970(0x7f0e0bda, float:1.8881191E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachContact"
            r8 = 2131624478(0x7f0e021e, float:1.8876137E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x2041:
            java.lang.String r3 = "NotificationMessageAudio"
            r4 = 2131626969(0x7f0e0bd9, float:1.888119E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachAudio"
            r8 = 2131624476(0x7f0e021c, float:1.8876133E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x2067:
            int r3 = r14.length     // Catch:{ all -> 0x0354 }
            r8 = 1
            if (r3 <= r8) goto L_0x20b1
            r3 = r14[r8]     // Catch:{ all -> 0x0354 }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0354 }
            if (r3 != 0) goto L_0x20b1
            java.lang.String r3 = "NotificationMessageStickerEmoji"
            r8 = 2131627011(0x7f0e0CLASSNAME, float:1.8881274E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch:{ all -> 0x0354 }
            r10 = 0
            r11 = r14[r10]     // Catch:{ all -> 0x0354 }
            r9[r10] = r11     // Catch:{ all -> 0x0354 }
            r10 = 1
            r11 = r14[r10]     // Catch:{ all -> 0x0354 }
            r9[r10] = r11     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r8, r9)     // Catch:{ all -> 0x0354 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0354 }
            r8.<init>()     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8.append(r10)     // Catch:{ all -> 0x0354 }
            r8.append(r15)     // Catch:{ all -> 0x0354 }
            r9 = 2131624505(0x7f0e0239, float:1.8876192E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r9)     // Catch:{ all -> 0x0354 }
            r8.append(r4)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = r8.toString()     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x20b1:
            java.lang.String r3 = "NotificationMessageSticker"
            r8 = 2131627010(0x7f0e0CLASSNAME, float:1.8881272E38)
            r9 = 1
            java.lang.Object[] r10 = new java.lang.Object[r9]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r11 = r14[r9]     // Catch:{ all -> 0x0354 }
            r10[r9] = r11     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r8, r10)     // Catch:{ all -> 0x0354 }
            r8 = 2131624505(0x7f0e0239, float:1.8876192E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x20d5:
            java.lang.String r3 = "NotificationMessageDocument"
            r4 = 2131626971(0x7f0e0bdb, float:1.8881193E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachDocument"
            r8 = 2131624481(0x7f0e0221, float:1.8876143E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x20fb:
            java.lang.String r3 = "NotificationMessageRound"
            r4 = 2131627005(0x7f0e0bfd, float:1.8881262E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachRound"
            r8 = 2131624504(0x7f0e0238, float:1.887619E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x2121:
            java.lang.String r3 = "ActionTakeScreenshoot"
            r4 = 2131624216(0x7f0e0118, float:1.8875605E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "un1"
            r8 = 0
            r9 = r14[r8]     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = r3.replace(r4, r9)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r4 = r3
            r8 = r34
            r9 = r35
            r3 = r77
            goto L_0x2279
        L_0x213d:
            java.lang.String r3 = "NotificationMessageSDVideo"
            r4 = 2131627007(0x7f0e0bff, float:1.8881266E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachDestructingVideo"
            r8 = 2131624480(0x7f0e0220, float:1.887614E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x2163:
            java.lang.String r3 = "NotificationMessageVideo"
            r4 = 2131627013(0x7f0e0CLASSNAME, float:1.8881278E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachVideo"
            r8 = 2131624508(0x7f0e023c, float:1.8876198E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x2189:
            java.lang.String r3 = "NotificationMessageSDPhoto"
            r4 = 2131627006(0x7f0e0bfe, float:1.8881264E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachDestructingPhoto"
            r8 = 2131624479(0x7f0e021f, float:1.8876139E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x21af:
            java.lang.String r3 = "NotificationMessagePhoto"
            r4 = 2131627001(0x7f0e0bf9, float:1.8881254E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "AttachPhoto"
            r8 = 2131624502(0x7f0e0236, float:1.8876186E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x21d5:
            java.lang.String r3 = "NotificationMessageNoText"
            r4 = 2131627000(0x7f0e0bf8, float:1.8881252E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r8 = 0
            r10 = r14[r8]     // Catch:{ all -> 0x0354 }
            r9[r8] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r9)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "Message"
            r8 = 2131626622(0x7f0e0a7e, float:1.8880485E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x21fb:
            java.lang.String r3 = "NotificationMessageText"
            r4 = 2131627012(0x7f0e0CLASSNAME, float:1.8881276E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r9 = 0
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            r4 = r14[r9]     // Catch:{ all -> 0x0354 }
            r12 = 0
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x221e:
            java.lang.String r3 = "NotificationMessageRecurringPay"
            r4 = 2131627004(0x7f0e0bfc, float:1.888126E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x0354 }
            r12 = 0
            r9 = r14[r12]     // Catch:{ all -> 0x0354 }
            r8[r12] = r9     // Catch:{ all -> 0x0354 }
            r9 = 1
            r10 = r14[r9]     // Catch:{ all -> 0x0354 }
            r8[r9] = r10     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r8)     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "PaymentInvoice"
            r8 = 2131627393(0x7f0e0d81, float:1.888205E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ all -> 0x0354 }
            r68 = r4
            r8 = r34
            r9 = r35
            r4 = r3
            r3 = r77
            goto L_0x2279
        L_0x2247:
            if (r3 == 0) goto L_0x225d
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0354 }
            r3.<init>()     // Catch:{ all -> 0x0354 }
            java.lang.String r4 = "unhandled loc_key = "
            r3.append(r4)     // Catch:{ all -> 0x0354 }
            r3.append(r5)     // Catch:{ all -> 0x0354 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0354 }
            org.telegram.messenger.FileLog.w(r3)     // Catch:{ all -> 0x0354 }
        L_0x225d:
            r3 = r77
            r8 = r34
            r9 = r35
            r4 = r67
            goto L_0x2279
        L_0x2266:
            r63 = r3
            r74 = r4
            r72 = r9
            r16 = r15
            r12 = 0
        L_0x226f:
            r3 = r77
            java.lang.String r4 = r3.getReactedText(r5, r14)     // Catch:{ all -> 0x23c4 }
            r8 = r34
            r9 = r35
        L_0x2279:
            if (r4 == 0) goto L_0x23b4
            org.telegram.tgnet.TLRPC$TL_message r10 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x23c4 }
            r10.<init>()     // Catch:{ all -> 0x23c4 }
            r10.id = r7     // Catch:{ all -> 0x23c4 }
            r11 = r13
            r12 = r55
            r10.random_id = r12     // Catch:{ all -> 0x23c4 }
            if (r68 == 0) goto L_0x228c
            r15 = r68
            goto L_0x228d
        L_0x228c:
            r15 = r4
        L_0x228d:
            r10.message = r15     // Catch:{ all -> 0x23c4 }
            r34 = 1000(0x3e8, double:4.94E-321)
            r15 = r11
            r55 = r12
            long r11 = r79 / r34
            int r12 = (int) r11     // Catch:{ all -> 0x23c4 }
            r10.date = r12     // Catch:{ all -> 0x23c4 }
            if (r6 == 0) goto L_0x22ab
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r11 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x22a3 }
            r11.<init>()     // Catch:{ all -> 0x22a3 }
            r10.action = r11     // Catch:{ all -> 0x22a3 }
            goto L_0x22ab
        L_0x22a3:
            r0 = move-exception
            r1 = r0
            r6 = r19
            r4 = r31
            goto L_0x2465
        L_0x22ab:
            if (r74 == 0) goto L_0x22b4
            int r11 = r10.flags     // Catch:{ all -> 0x22a3 }
            r12 = -2147483648(0xfffffffvar_, float:-0.0)
            r11 = r11 | r12
            r10.flags = r11     // Catch:{ all -> 0x22a3 }
        L_0x22b4:
            r10.dialog_id = r1     // Catch:{ all -> 0x23c4 }
            r11 = 0
            int r13 = (r72 > r11 ? 1 : (r72 == r11 ? 0 : -1))
            if (r13 == 0) goto L_0x22d2
            org.telegram.tgnet.TLRPC$TL_peerChannel r11 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x22a3 }
            r11.<init>()     // Catch:{ all -> 0x22a3 }
            r10.peer_id = r11     // Catch:{ all -> 0x22a3 }
            org.telegram.tgnet.TLRPC$Peer r11 = r10.peer_id     // Catch:{ all -> 0x22a3 }
            r12 = r72
            r11.channel_id = r12     // Catch:{ all -> 0x22a3 }
            r72 = r1
            r1 = r52
            r52 = r12
            r12 = r50
            goto L_0x2301
        L_0x22d2:
            r12 = r72
            r32 = 0
            int r11 = (r52 > r32 ? 1 : (r52 == r32 ? 0 : -1))
            if (r11 == 0) goto L_0x22ee
            org.telegram.tgnet.TLRPC$TL_peerChat r11 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x22a3 }
            r11.<init>()     // Catch:{ all -> 0x22a3 }
            r10.peer_id = r11     // Catch:{ all -> 0x22a3 }
            org.telegram.tgnet.TLRPC$Peer r11 = r10.peer_id     // Catch:{ all -> 0x22a3 }
            r72 = r1
            r1 = r52
            r11.chat_id = r1     // Catch:{ all -> 0x22a3 }
            r52 = r12
            r12 = r50
            goto L_0x2301
        L_0x22ee:
            r72 = r1
            r1 = r52
            org.telegram.tgnet.TLRPC$TL_peerUser r11 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x23c4 }
            r11.<init>()     // Catch:{ all -> 0x23c4 }
            r10.peer_id = r11     // Catch:{ all -> 0x23c4 }
            org.telegram.tgnet.TLRPC$Peer r11 = r10.peer_id     // Catch:{ all -> 0x23c4 }
            r52 = r12
            r12 = r50
            r11.user_id = r12     // Catch:{ all -> 0x23c4 }
        L_0x2301:
            int r11 = r10.flags     // Catch:{ all -> 0x23c4 }
            r11 = r11 | 256(0x100, float:3.59E-43)
            r10.flags = r11     // Catch:{ all -> 0x23c4 }
            r32 = 0
            int r11 = (r61 > r32 ? 1 : (r61 == r32 ? 0 : -1))
            if (r11 == 0) goto L_0x231d
            org.telegram.tgnet.TLRPC$TL_peerChat r11 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x22a3 }
            r11.<init>()     // Catch:{ all -> 0x22a3 }
            r10.from_id = r11     // Catch:{ all -> 0x22a3 }
            org.telegram.tgnet.TLRPC$Peer r11 = r10.from_id     // Catch:{ all -> 0x22a3 }
            r11.chat_id = r1     // Catch:{ all -> 0x22a3 }
            r50 = r1
            r1 = r65
            goto L_0x2355
        L_0x231d:
            r50 = r1
            r1 = r59
            r32 = 0
            int r11 = (r1 > r32 ? 1 : (r1 == r32 ? 0 : -1))
            if (r11 == 0) goto L_0x2337
            org.telegram.tgnet.TLRPC$TL_peerChannel r11 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x22a3 }
            r11.<init>()     // Catch:{ all -> 0x22a3 }
            r10.from_id = r11     // Catch:{ all -> 0x22a3 }
            org.telegram.tgnet.TLRPC$Peer r11 = r10.from_id     // Catch:{ all -> 0x22a3 }
            r11.channel_id = r1     // Catch:{ all -> 0x22a3 }
            r59 = r1
            r1 = r65
            goto L_0x2355
        L_0x2337:
            r32 = 0
            int r11 = (r65 > r32 ? 1 : (r65 == r32 ? 0 : -1))
            if (r11 == 0) goto L_0x234d
            org.telegram.tgnet.TLRPC$TL_peerUser r11 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x22a3 }
            r11.<init>()     // Catch:{ all -> 0x22a3 }
            r10.from_id = r11     // Catch:{ all -> 0x22a3 }
            org.telegram.tgnet.TLRPC$Peer r11 = r10.from_id     // Catch:{ all -> 0x22a3 }
            r59 = r1
            r1 = r65
            r11.user_id = r1     // Catch:{ all -> 0x22a3 }
            goto L_0x2355
        L_0x234d:
            r59 = r1
            r1 = r65
            org.telegram.tgnet.TLRPC$Peer r11 = r10.peer_id     // Catch:{ all -> 0x23c4 }
            r10.from_id = r11     // Catch:{ all -> 0x23c4 }
        L_0x2355:
            if (r64 != 0) goto L_0x235c
            if (r6 == 0) goto L_0x235a
            goto L_0x235c
        L_0x235a:
            r11 = 0
            goto L_0x235d
        L_0x235c:
            r11 = 1
        L_0x235d:
            r10.mentioned = r11     // Catch:{ all -> 0x23c4 }
            r11 = r58
            r10.silent = r11     // Catch:{ all -> 0x23c4 }
            r65 = r1
            r1 = r47
            r10.from_scheduled = r1     // Catch:{ all -> 0x23c4 }
            org.telegram.messenger.MessageObject r2 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x23c4 }
            r30 = r2
            r32 = r10
            r33 = r4
            r34 = r8
            r35 = r71
            r36 = r9
            r37 = r70
            r38 = r74
            r30.<init>(r31, r32, r33, r34, r35, r36, r37, r38, r39)     // Catch:{ all -> 0x23c4 }
            r47 = r1
            r1 = r63
            boolean r1 = r5.startsWith(r1)     // Catch:{ all -> 0x23c4 }
            if (r1 != 0) goto L_0x2393
            r1 = r16
            boolean r1 = r5.startsWith(r1)     // Catch:{ all -> 0x22a3 }
            if (r1 == 0) goto L_0x2391
            goto L_0x2393
        L_0x2391:
            r1 = 0
            goto L_0x2394
        L_0x2393:
            r1 = 1
        L_0x2394:
            r2.isReactionPush = r1     // Catch:{ all -> 0x23c4 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x23c4 }
            r1.<init>()     // Catch:{ all -> 0x23c4 }
            r1.add(r2)     // Catch:{ all -> 0x23c4 }
            r16 = 0
            r17 = r2
            org.telegram.messenger.NotificationsController r2 = org.telegram.messenger.NotificationsController.getInstance(r31)     // Catch:{ all -> 0x23c4 }
            r25 = r4
            java.util.concurrent.CountDownLatch r4 = r3.countDownLatch     // Catch:{ all -> 0x23c4 }
            r26 = r5
            r5 = 1
            r2.processNewMessages(r1, r5, r5, r4)     // Catch:{ all -> 0x2420 }
            r11 = r16
            goto L_0x2407
        L_0x23b4:
            r25 = r4
            r26 = r5
            r15 = r13
            r12 = r50
            r50 = r52
            r11 = r58
            r52 = r72
            r72 = r1
            goto L_0x2405
        L_0x23c4:
            r0 = move-exception
            r26 = r5
            r1 = r0
            r6 = r19
            r4 = r31
            goto L_0x2465
        L_0x23ce:
            r3 = r77
            r72 = r1
            r26 = r5
            r69 = r6
            r57 = r8
            r55 = r13
            r12 = r50
            r50 = r52
            r52 = r9
            goto L_0x2405
        L_0x23e1:
            r3 = r77
            r72 = r1
            r54 = r4
            r26 = r5
            r69 = r6
            r12 = r50
            r50 = r52
            r52 = r9
            goto L_0x2405
        L_0x23f2:
            r3 = r77
            r72 = r1
            r54 = r4
            r26 = r5
            r69 = r6
            r52 = r9
            r48 = r11
            r50 = r13
            r49 = r15
            r12 = r7
        L_0x2405:
            r11 = r48
        L_0x2407:
            if (r11 == 0) goto L_0x240e
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x2420 }
            r1.countDown()     // Catch:{ all -> 0x2420 }
        L_0x240e:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r31)     // Catch:{ all -> 0x2420 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r31)     // Catch:{ all -> 0x2420 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x2420 }
            r6 = r19
            r5 = r26
            r4 = r31
            goto L_0x249e
        L_0x2420:
            r0 = move-exception
            r1 = r0
            r6 = r19
            r5 = r26
            r4 = r31
            goto L_0x2465
        L_0x2429:
            r0 = move-exception
            r3 = r77
            r26 = r5
            r1 = r0
            r6 = r19
            r4 = r31
            goto L_0x2465
        L_0x2434:
            r0 = move-exception
            r3 = r1
            r26 = r5
            r1 = r0
            r6 = r19
            r4 = r31
            goto L_0x2465
        L_0x243e:
            r0 = move-exception
            r3 = r1
            r26 = r5
            r1 = r0
            r6 = r19
            r4 = r27
            goto L_0x2465
        L_0x2448:
            r0 = move-exception
            r3 = r1
            r26 = r5
            r19 = r6
            r1 = r0
            r4 = r27
            goto L_0x2465
        L_0x2452:
            r0 = move-exception
            r3 = r1
            r19 = r6
            r1 = r0
            r4 = r27
            goto L_0x2465
        L_0x245a:
            r0 = move-exception
            r3 = r1
            r1 = r0
            r4 = r27
            goto L_0x2465
        L_0x2460:
            r0 = move-exception
            r3 = r1
            r27 = r4
            r1 = r0
        L_0x2465:
            r2 = -1
            if (r4 == r2) goto L_0x2478
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r4)
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
            r2.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r2 = r3.countDownLatch
            r2.countDown()
            goto L_0x247b
        L_0x2478:
            r77.onDecryptError()
        L_0x247b:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x249b
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
        L_0x249b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x249e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.GcmPushListenerService.m70xa7b3420a(java.util.Map, long):void");
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
            r0 = 2131627784(0x7f0e0var_, float:1.8882842E38)
            java.lang.String r1 = "PushChatReactGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0188:
            r0 = 2131627785(0x7f0e0var_, float:1.8882844E38)
            java.lang.String r1 = "PushChatReactInvoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0192:
            r0 = 2131627781(0x7f0e0var_, float:1.8882836E38)
            java.lang.String r1 = "PushChatReactGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x019c:
            r0 = 2131627789(0x7f0e0f0d, float:1.8882852E38)
            java.lang.String r1 = "PushChatReactQuiz"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01a6:
            r0 = 2131627788(0x7f0e0f0c, float:1.888285E38)
            java.lang.String r1 = "PushChatReactPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01b0:
            r0 = 2131627783(0x7f0e0var_, float:1.888284E38)
            java.lang.String r1 = "PushChatReactGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01ba:
            r0 = 2131627782(0x7f0e0var_, float:1.8882838E38)
            java.lang.String r1 = "PushChatReactGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01c4:
            r0 = 2131627779(0x7f0e0var_, float:1.8882832E38)
            java.lang.String r1 = "PushChatReactContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01ce:
            r0 = 2131627778(0x7f0e0var_, float:1.888283E38)
            java.lang.String r1 = "PushChatReactAudio"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01d8:
            r0 = 2131627791(0x7f0e0f0f, float:1.8882856E38)
            java.lang.String r1 = "PushChatReactSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01e2:
            r0 = 2131627780(0x7f0e0var_, float:1.8882834E38)
            java.lang.String r1 = "PushChatReactDoc"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01ec:
            r0 = 2131627790(0x7f0e0f0e, float:1.8882854E38)
            java.lang.String r1 = "PushChatReactRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x01f6:
            r0 = 2131627793(0x7f0e0var_, float:1.888286E38)
            java.lang.String r1 = "PushChatReactVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0200:
            r0 = 2131627787(0x7f0e0f0b, float:1.8882848E38)
            java.lang.String r1 = "PushChatReactPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x020a:
            r0 = 2131627786(0x7f0e0f0a, float:1.8882846E38)
            java.lang.String r1 = "PushChatReactNotext"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0214:
            r0 = 2131627792(0x7f0e0var_, float:1.8882858E38)
            java.lang.String r1 = "PushChatReactText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x021e:
            r0 = 2131627800(0x7f0e0var_, float:1.8882875E38)
            java.lang.String r1 = "PushReactGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0228:
            r0 = 2131627801(0x7f0e0var_, float:1.8882877E38)
            java.lang.String r1 = "PushReactInvoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0232:
            r0 = 2131627797(0x7f0e0var_, float:1.8882869E38)
            java.lang.String r1 = "PushReactGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x023c:
            r0 = 2131627805(0x7f0e0f1d, float:1.8882885E38)
            java.lang.String r1 = "PushReactQuiz"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0246:
            r0 = 2131627804(0x7f0e0f1c, float:1.8882883E38)
            java.lang.String r1 = "PushReactPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0250:
            r0 = 2131627799(0x7f0e0var_, float:1.8882873E38)
            java.lang.String r1 = "PushReactGeoLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x025a:
            r0 = 2131627798(0x7f0e0var_, float:1.888287E38)
            java.lang.String r1 = "PushReactGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0264:
            r0 = 2131627795(0x7f0e0var_, float:1.8882865E38)
            java.lang.String r1 = "PushReactContect"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x026e:
            r0 = 2131627794(0x7f0e0var_, float:1.8882862E38)
            java.lang.String r1 = "PushReactAudio"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0278:
            r0 = 2131627807(0x7f0e0f1f, float:1.8882889E38)
            java.lang.String r1 = "PushReactSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0282:
            r0 = 2131627796(0x7f0e0var_, float:1.8882867E38)
            java.lang.String r1 = "PushReactDoc"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x028c:
            r0 = 2131627806(0x7f0e0f1e, float:1.8882887E38)
            java.lang.String r1 = "PushReactRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x0296:
            r0 = 2131627809(0x7f0e0var_, float:1.8882893E38)
            java.lang.String r1 = "PushReactVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x02a0:
            r0 = 2131627803(0x7f0e0f1b, float:1.888288E38)
            java.lang.String r1 = "PushReactPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x02aa:
            r0 = 2131627802(0x7f0e0f1a, float:1.8882879E38)
            java.lang.String r1 = "PushReactNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        L_0x02b4:
            r0 = 2131627808(0x7f0e0var_, float:1.888289E38)
            java.lang.String r1 = "PushReactText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.GcmPushListenerService.getReactedText(java.lang.String, java.lang.Object[]):java.lang.String");
    }

    private void onDecryptError() {
        for (int a = 0; a < 4; a++) {
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
            for (int a = 0; a < 4; a++) {
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
