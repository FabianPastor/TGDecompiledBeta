package org.telegram.messenger;

import android.os.SystemClock;
import android.text.TextUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_saveAppLog;
import org.telegram.tgnet.TLRPC$TL_inputAppEvent;
import org.telegram.tgnet.TLRPC$TL_jsonNull;

public class GcmPushListenerService extends FirebaseMessagingService {
    public static final int NOTIFICATION_ID = 1;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void onMessageReceived(RemoteMessage remoteMessage) {
        String from = remoteMessage.getFrom();
        Map<String, String> data = remoteMessage.getData();
        long sentTime = remoteMessage.getSentTime();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("GCM received data: " + data + " from: " + from);
        }
        AndroidUtilities.runOnUIThread(new GcmPushListenerService$$ExternalSyntheticLambda6(this, data, sentTime));
        try {
            this.countDownLatch.await();
        } catch (Throwable unused) {
        }
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("finished GCM service, time = " + (SystemClock.elapsedRealtime() - elapsedRealtime));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onMessageReceived$4(Map map, long j) {
        ApplicationLoader.postInitApplication();
        Utilities.stageQueue.postRunnable(new GcmPushListenerService$$ExternalSyntheticLambda7(this, map, j));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v9, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v29, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v69, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v50, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v79, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v96, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v100, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v104, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v107, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v110, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v11, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v105, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v141, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v143, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v146, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v78, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v150, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v153, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v138, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v82, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v156, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v188, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v191, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v194, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v166, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v197, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v200, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v204, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v159, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v207, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v174, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v210, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v101, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v178, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v216, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v219, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v222, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v225, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v108, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v228, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v231, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v190, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v235, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v141, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v143, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v196, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v149, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v198, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v153, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v155, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v200, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v201, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v167, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v206, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v169, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v134, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v207, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v211, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v216, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v221, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v221, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v226, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v232, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v141, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v143, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v196, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v141, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v201, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v208, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v148, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v244, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v149, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v217, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v267, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v228, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v154, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v155, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v252, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v274, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v281, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v158, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v256, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v257, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v258, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v260, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v167, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v262, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v169, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v263, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v170, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v264, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v257, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v82, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v266, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v45, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v268, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v269, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v270, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v272, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v273, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v273, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v274, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v277, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v275, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v279, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v276, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v277, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v283, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v278, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v285, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v279, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v280, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v289, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v281, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v291, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v282, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v283, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v295, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v284, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v297, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v285, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v643, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v644, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v645, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v648, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v287, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v288, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v393, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v395, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v396, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v397, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX WARNING: type inference failed for: r1v20 */
    /* JADX WARNING: type inference failed for: r1v354 */
    /* JADX WARNING: type inference failed for: r1v356 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:1000:0x1d9e, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1001:0x1d9f, code lost:
        org.telegram.tgnet.ConnectionsManager.getInstance(r12).applyDatacenterAddress(r3, r4[0], java.lang.Integer.parseInt(r4[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r12).resumeNetworkMaybe();
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1002:0x1dbc, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1003:0x1dbd, code lost:
        r0 = th;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1004:0x1dbe, code lost:
        r5 = r2;
        r6 = r33;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1035:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x01fc, code lost:
        if (r2 == 0) goto L_0x1d80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x01fe, code lost:
        if (r2 == 1) goto L_0x1d35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x0200, code lost:
        if (r2 == 2) goto L_0x1d24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x0202, code lost:
        if (r2 == 3) goto L_0x1d08;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x020a, code lost:
        if (r10.has("channel_id") == false) goto L_0x0218;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:?, code lost:
        r13 = r10.getLong("channel_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x0212, code lost:
        r2 = r8;
        r7 = -r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x0215, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x0218, code lost:
        r2 = r8;
        r7 = 0;
        r13 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:0x0223, code lost:
        if (r10.has("from_id") == false) goto L_0x0231;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:?, code lost:
        r7 = r10.getLong("from_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x022b, code lost:
        r33 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x022e, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x0231, code lost:
        r33 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x0239, code lost:
        if (r10.has("chat_id") == false) goto L_0x0248;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:?, code lost:
        r7 = r10.getLong("chat_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:0x0241, code lost:
        r4 = r7;
        r7 = -r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x0248, code lost:
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x0250, code lost:
        if (r10.has("encryption_id") == false) goto L_0x025d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:?, code lost:
        r7 = org.telegram.messenger.DialogObject.makeEncryptedDialogId((long) r10.getInt("encryption_id"));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x0263, code lost:
        if (r10.has("schedule") == false) goto L_0x026f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x026b, code lost:
        if (r10.getInt("schedule") != 1) goto L_0x026f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:167:0x026d, code lost:
        r15 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x026f, code lost:
        r15 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x0274, code lost:
        if (r7 != 0) goto L_0x0280;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x027c, code lost:
        if ("ENCRYPTED_MESSAGE".equals(r2) == false) goto L_0x0280;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x027e, code lost:
        r7 = org.telegram.messenger.NotificationsController.globalSecretChatId;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:175:0x0284, code lost:
        if (r7 == 0) goto L_0x1cdc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x028e, code lost:
        if ("READ_HISTORY".equals(r2) == false) goto L_0x0304;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:183:?, code lost:
        r3 = r10.getInt("max_id");
        r10 = new java.util.ArrayList();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x029d, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x02b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:0x029f, code lost:
        org.telegram.messenger.FileLog.d("GCM received read notification max_id = " + r3 + " for dialogId = " + r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x02bd, code lost:
        if (r13 == 0) goto L_0x02cc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x02bf, code lost:
        r4 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox();
        r4.channel_id = r13;
        r4.max_id = r3;
        r10.add(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x02cc, code lost:
        r7 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox();
        r8 = r33;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x02d7, code lost:
        if (r8 == 0) goto L_0x02e3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x02d9, code lost:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r7.peer = r4;
        r4.user_id = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x02e3, code lost:
        r8 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r7.peer = r8;
        r8.chat_id = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x02ec, code lost:
        r7.max_id = r3;
        r10.add(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x02f1, code lost:
        org.telegram.messenger.MessagesController.getInstance(r12).processUpdateArray(r10, (java.util.ArrayList<org.telegram.tgnet.TLRPC$User>) null, (java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat>) null, false, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x0304, code lost:
        r35 = r33;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x030c, code lost:
        r33 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x0310, code lost:
        if ("MESSAGE_DELETED".equals(r2) == false) goto L_0x037c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:?, code lost:
        r3 = r10.getString("messages").split(",");
        r4 = new androidx.collection.LongSparseArray();
        r5 = new java.util.ArrayList();
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x0328, code lost:
        if (r6 >= r3.length) goto L_0x0336;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x032a, code lost:
        r5.add(org.telegram.messenger.Utilities.parseInt(r3[r6]));
        r6 = r6 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x0336, code lost:
        r4.put(-r13, r5);
        org.telegram.messenger.NotificationsController.getInstance(r12).removeDeletedMessagesFromNotifications(r4);
        org.telegram.messenger.MessagesController.getInstance(r12).deleteMessagesByPush(r7, r5, r13);
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x0350, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1cde;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x0352, code lost:
        org.telegram.messenger.FileLog.d("GCM received " + r2 + " for dialogId = " + r7 + " mids = " + android.text.TextUtils.join(",", r5));
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x037c, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:?, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x0380, code lost:
        if (android.text.TextUtils.isEmpty(r2) != false) goto L_0x1cde;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x0388, code lost:
        if (r10.has("msg_id") == false) goto L_0x0393;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:?, code lost:
        r11 = r10.getInt("msg_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x0390, code lost:
        r23 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x0393, code lost:
        r23 = r15;
        r11 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x039c, code lost:
        if (r10.has("random_id") == false) goto L_0x03b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:225:0x03ac, code lost:
        r49 = r4;
        r4 = org.telegram.messenger.Utilities.parseLong(r10.getString("random_id")).longValue();
        r24 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x03b3, code lost:
        r24 = r4;
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x03b7, code lost:
        if (r11 == 0) goto L_0x03fd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:?, code lost:
        r1 = org.telegram.messenger.MessagesController.getInstance(r12).dialogs_read_inbox_max.get(java.lang.Long.valueOf(r7));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x03c9, code lost:
        if (r1 != null) goto L_0x03e8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x03cb, code lost:
        r1 = java.lang.Integer.valueOf(org.telegram.messenger.MessagesStorage.getInstance(r12).getDialogReadMax(false, r7));
        r26 = "messages";
        org.telegram.messenger.MessagesController.getInstance(r12).dialogs_read_inbox_max.put(java.lang.Long.valueOf(r7), r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x03e8, code lost:
        r26 = "messages";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x03ee, code lost:
        if (r11 <= r1.intValue()) goto L_0x03f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:0x03f0, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x03f2, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x03f4, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x03f5, code lost:
        r3 = -1;
        r1 = r51;
        r5 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x03f9, code lost:
        r6 = r33;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x03fd, code lost:
        r26 = "messages";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x0403, code lost:
        if (r4 == 0) goto L_0x03f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x040d, code lost:
        if (org.telegram.messenger.MessagesStorage.getInstance(r12).checkMessageByRandomId(r4) != false) goto L_0x03f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x0410, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x0416, code lost:
        if (r2.startsWith("REACT_") != false) goto L_0x041e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:?, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x041c, code lost:
        if (r2.startsWith("CHAT_REACT_") == false) goto L_0x041f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x041e, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x041f, code lost:
        if (r1 == false) goto L_0x1cd0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x0421, code lost:
        r1 = "chat_from_id";
        r27 = r4;
        r29 = r11;
        r6 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x042a, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:?, code lost:
        r11 = r10.optLong(r1, 0);
        r30 = r13;
        r13 = r10.optLong("chat_from_broadcast_id", 0);
        r37 = r10.optLong("chat_from_group_id", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x043e, code lost:
        if (r11 != 0) goto L_0x0447;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x0442, code lost:
        if (r37 == 0) goto L_0x0445;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x0445, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x0447, code lost:
        r1 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x044e, code lost:
        if (r10.has("mention") == false) goto L_0x0461;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x0456, code lost:
        if (r10.getInt("mention") == 0) goto L_0x0461;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x0458, code lost:
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x045a, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x045b, code lost:
        r3 = -1;
        r1 = r51;
        r5 = r2;
        r12 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x0461, code lost:
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:275:0x0468, code lost:
        if (r10.has("silent") == false) goto L_0x0476;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x0470, code lost:
        if (r10.getInt("silent") == 0) goto L_0x0476;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0472, code lost:
        r34 = r6;
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x0476, code lost:
        r34 = r6;
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0479, code lost:
        r32 = r5;
        r5 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x047f, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x0483, code lost:
        if (r5.has("loc_args") == false) goto L_0x04ac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:?, code lost:
        r5 = r5.getJSONArray("loc_args");
        r6 = r5.length();
        r16 = r4;
        r4 = new java.lang.String[r6];
        r39 = r11;
        r11 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x0496, code lost:
        if (r11 >= r6) goto L_0x04b1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x0498, code lost:
        r4[r11] = r5.getString(r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x049e, code lost:
        r11 = r11 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x04a1, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x04a2, code lost:
        r3 = -1;
        r1 = r51;
        r5 = r2;
        r6 = r33;
        r12 = r34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x04ac, code lost:
        r16 = r4;
        r39 = r11;
        r4 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:?, code lost:
        r6 = r4[0];
        r5 = r10.has("edit_date");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x04c0, code lost:
        if (r2.startsWith("CHAT_") == false) goto L_0x04f6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x04c6, code lost:
        if (org.telegram.messenger.UserObject.isReplyUser(r7) == false) goto L_0x04e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x04c8, code lost:
        r6 = r6 + " @ " + r4[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x04e4, code lost:
        if (r30 == 0) goto L_0x04e8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x04e6, code lost:
        r10 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x04e8, code lost:
        r10 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x04ec, code lost:
        r11 = false;
        r41 = false;
        r49 = r10;
        r10 = r6;
        r6 = r4[1];
        r12 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x04fc, code lost:
        if (r2.startsWith("PINNED_") == false) goto L_0x050d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x0502, code lost:
        if (r30 == 0) goto L_0x0506;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x0504, code lost:
        r10 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x0506, code lost:
        r10 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x0507, code lost:
        r12 = r10;
        r10 = null;
        r11 = false;
        r41 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x0513, code lost:
        if (r2.startsWith("CHANNEL_") == false) goto L_0x0518;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x0515, code lost:
        r10 = null;
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x0518, code lost:
        r10 = null;
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x051a, code lost:
        r12 = false;
        r41 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x051f, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x054a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x0521, code lost:
        r42 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:?, code lost:
        r6 = new java.lang.StringBuilder();
        r43 = r5;
        r6.append("GCM received message notification ");
        r6.append(r2);
        r6.append(" for dialogId = ");
        r6.append(r7);
        r6.append(" mid = ");
        r5 = r29;
        r6.append(r5);
        org.telegram.messenger.FileLog.d(r6.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x054a, code lost:
        r43 = r5;
        r42 = r6;
        r5 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x0554, code lost:
        if (r2.startsWith("REACT_") != false) goto L_0x1bbc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x055a, code lost:
        if (r2.startsWith("CHAT_REACT_") == false) goto L_0x056b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x055c, code lost:
        r1 = r51;
        r44 = "REACT_";
        r8 = r7;
        r46 = r10;
        r45 = r11;
        r47 = r13;
        r17 = "CHAT_REACT_";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x056f, code lost:
        switch(r2.hashCode()) {
            case -2100047043: goto L_0x0ab0;
            case -2091498420: goto L_0x0aa5;
            case -2053872415: goto L_0x0a9a;
            case -2039746363: goto L_0x0a8f;
            case -2023218804: goto L_0x0a84;
            case -1979538588: goto L_0x0a79;
            case -1979536003: goto L_0x0a6e;
            case -1979535888: goto L_0x0a63;
            case -1969004705: goto L_0x0a58;
            case -1946699248: goto L_0x0a4c;
            case -1717283471: goto L_0x0a40;
            case -1646640058: goto L_0x0a34;
            case -1528047021: goto L_0x0a28;
            case -1493579426: goto L_0x0a1c;
            case -1482481933: goto L_0x0a10;
            case -1480102982: goto L_0x0a05;
            case -1478041834: goto L_0x09f9;
            case -1474543101: goto L_0x09ee;
            case -1465695932: goto L_0x09e2;
            case -1374906292: goto L_0x09d6;
            case -1372940586: goto L_0x09ca;
            case -1264245338: goto L_0x09be;
            case -1236154001: goto L_0x09b2;
            case -1236086700: goto L_0x09a6;
            case -1236077786: goto L_0x099a;
            case -1235796237: goto L_0x098e;
            case -1235760759: goto L_0x0982;
            case -1235686303: goto L_0x0977;
            case -1198046100: goto L_0x096c;
            case -1124254527: goto L_0x0960;
            case -1085137927: goto L_0x0954;
            case -1084856378: goto L_0x0948;
            case -1084820900: goto L_0x093c;
            case -1084746444: goto L_0x0930;
            case -819729482: goto L_0x0924;
            case -772141857: goto L_0x0918;
            case -638310039: goto L_0x090c;
            case -590403924: goto L_0x0900;
            case -589196239: goto L_0x08f4;
            case -589193654: goto L_0x08e8;
            case -589193539: goto L_0x08dc;
            case -440169325: goto L_0x08d0;
            case -412748110: goto L_0x08c4;
            case -228518075: goto L_0x08b8;
            case -213586509: goto L_0x08ac;
            case -115582002: goto L_0x08a0;
            case -112621464: goto L_0x0894;
            case -108522133: goto L_0x0888;
            case -107572034: goto L_0x087d;
            case -40534265: goto L_0x0871;
            case 52369421: goto L_0x0865;
            case 65254746: goto L_0x0859;
            case 141040782: goto L_0x084d;
            case 202550149: goto L_0x0841;
            case 309993049: goto L_0x0835;
            case 309995634: goto L_0x0829;
            case 309995749: goto L_0x081d;
            case 320532812: goto L_0x0811;
            case 328933854: goto L_0x0805;
            case 331340546: goto L_0x07f9;
            case 342406591: goto L_0x07ed;
            case 344816990: goto L_0x07e1;
            case 346878138: goto L_0x07d5;
            case 350376871: goto L_0x07c9;
            case 608430149: goto L_0x07bd;
            case 615714517: goto L_0x07b2;
            case 715508879: goto L_0x07a6;
            case 728985323: goto L_0x079a;
            case 731046471: goto L_0x078e;
            case 734545204: goto L_0x0782;
            case 802032552: goto L_0x0776;
            case 991498806: goto L_0x076a;
            case 1007364121: goto L_0x075e;
            case 1019850010: goto L_0x0752;
            case 1019917311: goto L_0x0746;
            case 1019926225: goto L_0x073a;
            case 1020207774: goto L_0x072e;
            case 1020243252: goto L_0x0722;
            case 1020317708: goto L_0x0716;
            case 1060282259: goto L_0x070a;
            case 1060349560: goto L_0x06fe;
            case 1060358474: goto L_0x06f2;
            case 1060640023: goto L_0x06e6;
            case 1060675501: goto L_0x06da;
            case 1060749957: goto L_0x06cf;
            case 1073049781: goto L_0x06c3;
            case 1078101399: goto L_0x06b7;
            case 1110103437: goto L_0x06ab;
            case 1160762272: goto L_0x069f;
            case 1172918249: goto L_0x0693;
            case 1234591620: goto L_0x0687;
            case 1281128640: goto L_0x067b;
            case 1281131225: goto L_0x066f;
            case 1281131340: goto L_0x0663;
            case 1310789062: goto L_0x0658;
            case 1333118583: goto L_0x064c;
            case 1361447897: goto L_0x0640;
            case 1498266155: goto L_0x0634;
            case 1533804208: goto L_0x0628;
            case 1540131626: goto L_0x061c;
            case 1547988151: goto L_0x0610;
            case 1561464595: goto L_0x0604;
            case 1563525743: goto L_0x05f8;
            case 1567024476: goto L_0x05ec;
            case 1810705077: goto L_0x05e0;
            case 1815177512: goto L_0x05d4;
            case 1954774321: goto L_0x05c8;
            case 1963241394: goto L_0x05bc;
            case 2014789757: goto L_0x05b0;
            case 2022049433: goto L_0x05a4;
            case 2034984710: goto L_0x0598;
            case 2048733346: goto L_0x058c;
            case 2099392181: goto L_0x0580;
            case 2140162142: goto L_0x0574;
            default: goto L_0x0572;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x057a, code lost:
        if (r2.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x057c, code lost:
        r6 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x0586, code lost:
        if (r2.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x0588, code lost:
        r6 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x0592, code lost:
        if (r2.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x0594, code lost:
        r6 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x059e, code lost:
        if (r2.equals("CHANNEL_MESSAGE_PLAYLIST") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x05a0, code lost:
        r6 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x05aa, code lost:
        if (r2.equals("PINNED_CONTACT") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x05ac, code lost:
        r6 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x05b6, code lost:
        if (r2.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x05b8, code lost:
        r6 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x05c2, code lost:
        if (r2.equals("LOCKED_MESSAGE") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x05c4, code lost:
        r6 = 'l';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x05ce, code lost:
        if (r2.equals("CHAT_MESSAGE_PLAYLIST") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x05d0, code lost:
        r6 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x05da, code lost:
        if (r2.equals("CHANNEL_MESSAGES") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x05dc, code lost:
        r6 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x05e6, code lost:
        if (r2.equals("MESSAGE_INVOICE") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x05e8, code lost:
        r6 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x05f2, code lost:
        if (r2.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x05f4, code lost:
        r6 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x05fe, code lost:
        if (r2.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x0600, code lost:
        r6 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x060a, code lost:
        if (r2.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x060c, code lost:
        r6 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x0616, code lost:
        if (r2.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x0618, code lost:
        r6 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x0622, code lost:
        if (r2.equals("MESSAGE_PLAYLIST") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x0624, code lost:
        r6 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x062e, code lost:
        if (r2.equals("MESSAGE_VIDEOS") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x0630, code lost:
        r6 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x063a, code lost:
        if (r2.equals("PHONE_CALL_MISSED") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x063c, code lost:
        r6 = 'q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x0646, code lost:
        if (r2.equals("MESSAGE_PHOTOS") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x0648, code lost:
        r6 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x0652, code lost:
        if (r2.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x0654, code lost:
        r6 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x065e, code lost:
        if (r2.equals("MESSAGE_NOTEXT") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x0660, code lost:
        r6 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:400:0x0669, code lost:
        if (r2.equals("MESSAGE_GIF") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x066b, code lost:
        r6 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0675, code lost:
        if (r2.equals("MESSAGE_GEO") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x0677, code lost:
        r6 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x0681, code lost:
        if (r2.equals("MESSAGE_DOC") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x0683, code lost:
        r6 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x068d, code lost:
        if (r2.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x068f, code lost:
        r6 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0699, code lost:
        if (r2.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x069b, code lost:
        r6 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x06a5, code lost:
        if (r2.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x06a7, code lost:
        r6 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x06b1, code lost:
        if (r2.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x06b3, code lost:
        r6 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x06bd, code lost:
        if (r2.equals("CHAT_TITLE_EDITED") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x06bf, code lost:
        r6 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x06c9, code lost:
        if (r2.equals("PINNED_NOTEXT") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x06cb, code lost:
        r6 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x06d5, code lost:
        if (r2.equals("MESSAGE_TEXT") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x06d7, code lost:
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x06e0, code lost:
        if (r2.equals("MESSAGE_QUIZ") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x06e2, code lost:
        r6 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x06ec, code lost:
        if (r2.equals("MESSAGE_POLL") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x06ee, code lost:
        r6 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x06f8, code lost:
        if (r2.equals("MESSAGE_GAME") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x06fa, code lost:
        r6 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x0704, code lost:
        if (r2.equals("MESSAGE_FWDS") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x0706, code lost:
        r6 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x0710, code lost:
        if (r2.equals("MESSAGE_DOCS") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x0712, code lost:
        r6 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x071c, code lost:
        if (r2.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x071e, code lost:
        r6 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x0728, code lost:
        if (r2.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x072a, code lost:
        r6 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x0734, code lost:
        if (r2.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x0736, code lost:
        r6 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x0740, code lost:
        if (r2.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x0742, code lost:
        r6 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x074c, code lost:
        if (r2.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:458:0x074e, code lost:
        r6 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:460:0x0758, code lost:
        if (r2.equals("CHAT_MESSAGE_DOCS") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x075a, code lost:
        r6 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x0764, code lost:
        if (r2.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x0766, code lost:
        r6 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x0770, code lost:
        if (r2.equals("PINNED_GEOLIVE") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x0772, code lost:
        r6 = 'b';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x077c, code lost:
        if (r2.equals("MESSAGE_CONTACT") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x077e, code lost:
        r6 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:472:0x0788, code lost:
        if (r2.equals("PINNED_VIDEO") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x078a, code lost:
        r6 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0794, code lost:
        if (r2.equals("PINNED_ROUND") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x0796, code lost:
        r6 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x07a0, code lost:
        if (r2.equals("PINNED_PHOTO") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x07a2, code lost:
        r6 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x07ac, code lost:
        if (r2.equals("PINNED_AUDIO") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x07ae, code lost:
        r6 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x07b8, code lost:
        if (r2.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x07ba, code lost:
        r6 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x07c3, code lost:
        if (r2.equals("CHAT_VOICECHAT_INVITE_YOU") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x07c5, code lost:
        r6 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x07cf, code lost:
        if (r2.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x07d1, code lost:
        r6 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x07db, code lost:
        if (r2.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x07dd, code lost:
        r6 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x07e7, code lost:
        if (r2.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x07e9, code lost:
        r6 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x07f3, code lost:
        if (r2.equals("CHAT_VOICECHAT_END") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x07f5, code lost:
        r6 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x07ff, code lost:
        if (r2.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x0801, code lost:
        r6 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x080b, code lost:
        if (r2.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x080d, code lost:
        r6 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x0817, code lost:
        if (r2.equals("MESSAGES") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x0819, code lost:
        r6 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x0823, code lost:
        if (r2.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x0825, code lost:
        r6 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x082f, code lost:
        if (r2.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x0831, code lost:
        r6 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x083b, code lost:
        if (r2.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x083d, code lost:
        r6 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0847, code lost:
        if (r2.equals("CHAT_VOICECHAT_INVITE") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x0849, code lost:
        r6 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x0853, code lost:
        if (r2.equals("CHAT_LEFT") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0855, code lost:
        r6 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x085f, code lost:
        if (r2.equals("CHAT_ADD_YOU") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0861, code lost:
        r6 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x086b, code lost:
        if (r2.equals("REACT_TEXT") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x086d, code lost:
        r6 = 'h';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x0877, code lost:
        if (r2.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0879, code lost:
        r6 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x0883, code lost:
        if (r2.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x0885, code lost:
        r6 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:538:0x088e, code lost:
        if (r2.equals("AUTH_REGION") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x0890, code lost:
        r6 = 'k';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x089a, code lost:
        if (r2.equals("CONTACT_JOINED") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x089c, code lost:
        r6 = 'i';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x08a6, code lost:
        if (r2.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x08a8, code lost:
        r6 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x08b2, code lost:
        if (r2.equals("ENCRYPTION_REQUEST") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x08b4, code lost:
        r6 = 'm';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x08be, code lost:
        if (r2.equals("MESSAGE_GEOLIVE") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x08c0, code lost:
        r6 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x08ca, code lost:
        if (r2.equals("CHAT_DELETE_YOU") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x08cc, code lost:
        r6 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x08d6, code lost:
        if (r2.equals("AUTH_UNKNOWN") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x08d8, code lost:
        r6 = 'j';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x08e2, code lost:
        if (r2.equals("PINNED_GIF") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x08e4, code lost:
        r6 = 'f';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x08ee, code lost:
        if (r2.equals("PINNED_GEO") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x08f0, code lost:
        r6 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x08fa, code lost:
        if (r2.equals("PINNED_DOC") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x08fc, code lost:
        r6 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x0906, code lost:
        if (r2.equals("PINNED_GAME_SCORE") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x0908, code lost:
        r6 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x0912, code lost:
        if (r2.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x0914, code lost:
        r6 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x091e, code lost:
        if (r2.equals("PHONE_CALL_REQUEST") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x0920, code lost:
        r6 = 'o';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x092a, code lost:
        if (r2.equals("PINNED_STICKER") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x092c, code lost:
        r6 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x0936, code lost:
        if (r2.equals("PINNED_TEXT") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x0938, code lost:
        r6 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x0942, code lost:
        if (r2.equals("PINNED_QUIZ") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x0944, code lost:
        r6 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x094e, code lost:
        if (r2.equals("PINNED_POLL") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x0950, code lost:
        r6 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x095a, code lost:
        if (r2.equals("PINNED_GAME") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x095c, code lost:
        r6 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x0966, code lost:
        if (r2.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0968, code lost:
        r6 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x0972, code lost:
        if (r2.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x0974, code lost:
        r6 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x097d, code lost:
        if (r2.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x097f, code lost:
        r6 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0988, code lost:
        if (r2.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x098a, code lost:
        r6 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x0994, code lost:
        if (r2.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0996, code lost:
        r6 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x09a0, code lost:
        if (r2.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x09a2, code lost:
        r6 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x09ac, code lost:
        if (r2.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x09ae, code lost:
        r6 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x09b8, code lost:
        if (r2.equals("CHANNEL_MESSAGE_DOCS") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x09ba, code lost:
        r6 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x09c4, code lost:
        if (r2.equals("PINNED_INVOICE") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x09c6, code lost:
        r6 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x09d0, code lost:
        if (r2.equals("CHAT_RETURNED") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x09d2, code lost:
        r6 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x09dc, code lost:
        if (r2.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x09de, code lost:
        r6 = 'g';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:625:0x09e8, code lost:
        if (r2.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x09ea, code lost:
        r6 = 'n';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x09f4, code lost:
        if (r2.equals("MESSAGE_VIDEO") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x09f6, code lost:
        r6 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x09ff, code lost:
        if (r2.equals("MESSAGE_ROUND") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x0a01, code lost:
        r6 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x0a0b, code lost:
        if (r2.equals("MESSAGE_PHOTO") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x0a0d, code lost:
        r6 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x0a16, code lost:
        if (r2.equals("MESSAGE_MUTED") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x0a18, code lost:
        r6 = 'p';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x0a22, code lost:
        if (r2.equals("MESSAGE_AUDIO") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x0a24, code lost:
        r6 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0a2e, code lost:
        if (r2.equals("CHAT_MESSAGES") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0a30, code lost:
        r6 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x0a3a, code lost:
        if (r2.equals("CHAT_VOICECHAT_START") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x0a3c, code lost:
        r6 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0a46, code lost:
        if (r2.equals("CHAT_REQ_JOINED") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0a48, code lost:
        r6 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0a52, code lost:
        if (r2.equals("CHAT_JOINED") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0a54, code lost:
        r6 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0a5e, code lost:
        if (r2.equals("CHAT_ADD_MEMBER") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0a60, code lost:
        r6 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0a69, code lost:
        if (r2.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0a6b, code lost:
        r6 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x0a74, code lost:
        if (r2.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0a76, code lost:
        r6 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0a7f, code lost:
        if (r2.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x0a81, code lost:
        r6 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x0a8a, code lost:
        if (r2.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0a8c, code lost:
        r6 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x0a95, code lost:
        if (r2.equals("MESSAGE_STICKER") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:671:0x0a97, code lost:
        r6 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:673:0x0aa0, code lost:
        if (r2.equals("CHAT_CREATED") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0aa2, code lost:
        r6 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0aab, code lost:
        if (r2.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x0aad, code lost:
        r6 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:679:0x0ab6, code lost:
        if (r2.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0abb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0ab8, code lost:
        r6 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0abb, code lost:
        r6 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x0abc, code lost:
        r17 = "CHAT_REACT_";
        r44 = "REACT_";
        r45 = r11;
        r46 = r10;
        r47 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0ad5, code lost:
        switch(r6) {
            case 0: goto L_0x1b83;
            case 1: goto L_0x1b83;
            case 2: goto L_0x1b63;
            case 3: goto L_0x1b47;
            case 4: goto L_0x1b2b;
            case 5: goto L_0x1b0f;
            case 6: goto L_0x1af2;
            case 7: goto L_0x1ad7;
            case 8: goto L_0x1aba;
            case 9: goto L_0x1a9d;
            case 10: goto L_0x1a49;
            case 11: goto L_0x1a2c;
            case 12: goto L_0x1a0a;
            case 13: goto L_0x19e8;
            case 14: goto L_0x19c6;
            case 15: goto L_0x19a9;
            case 16: goto L_0x198c;
            case 17: goto L_0x196f;
            case 18: goto L_0x194d;
            case 19: goto L_0x192f;
            case 20: goto L_0x192f;
            case 21: goto L_0x190d;
            case 22: goto L_0x18e6;
            case 23: goto L_0x18c3;
            case 24: goto L_0x189f;
            case 25: goto L_0x187b;
            case 26: goto L_0x1857;
            case 27: goto L_0x1840;
            case 28: goto L_0x181e;
            case 29: goto L_0x1802;
            case 30: goto L_0x17e6;
            case 31: goto L_0x17ca;
            case 32: goto L_0x17ad;
            case 33: goto L_0x1759;
            case 34: goto L_0x173c;
            case 35: goto L_0x171a;
            case 36: goto L_0x16f8;
            case 37: goto L_0x16d6;
            case 38: goto L_0x16b9;
            case 39: goto L_0x169c;
            case 40: goto L_0x167f;
            case 41: goto L_0x1662;
            case 42: goto L_0x1639;
            case 43: goto L_0x1616;
            case 44: goto L_0x15f1;
            case 45: goto L_0x15cc;
            case 46: goto L_0x15a7;
            case 47: goto L_0x1590;
            case 48: goto L_0x1571;
            case 49: goto L_0x154a;
            case 50: goto L_0x1529;
            case 51: goto L_0x1508;
            case 52: goto L_0x14e7;
            case 53: goto L_0x14c5;
            case 54: goto L_0x1453;
            case 55: goto L_0x1431;
            case 56: goto L_0x140a;
            case 57: goto L_0x13e3;
            case 58: goto L_0x13bc;
            case 59: goto L_0x139a;
            case 60: goto L_0x1378;
            case 61: goto L_0x1356;
            case 62: goto L_0x132f;
            case 63: goto L_0x130d;
            case 64: goto L_0x12e6;
            case 65: goto L_0x12c8;
            case 66: goto L_0x12c8;
            case 67: goto L_0x12b0;
            case 68: goto L_0x1298;
            case 69: goto L_0x127b;
            case 70: goto L_0x1263;
            case 71: goto L_0x1245;
            case 72: goto L_0x122c;
            case 73: goto L_0x1213;
            case 74: goto L_0x11fa;
            case 75: goto L_0x11e1;
            case 76: goto L_0x11c8;
            case 77: goto L_0x11af;
            case 78: goto L_0x1196;
            case 79: goto L_0x117d;
            case 80: goto L_0x1151;
            case 81: goto L_0x1129;
            case 82: goto L_0x10ff;
            case 83: goto L_0x10d5;
            case 84: goto L_0x10ab;
            case 85: goto L_0x1092;
            case 86: goto L_0x103c;
            case 87: goto L_0x0ff0;
            case 88: goto L_0x0fa4;
            case 89: goto L_0x0var_;
            case 90: goto L_0x0f0c;
            case 91: goto L_0x0ec0;
            case 92: goto L_0x0e08;
            case 93: goto L_0x0dbc;
            case 94: goto L_0x0d66;
            case 95: goto L_0x0d10;
            case 96: goto L_0x0cbe;
            case 97: goto L_0x0CLASSNAME;
            case 98: goto L_0x0CLASSNAME;
            case 99: goto L_0x0bde;
            case 100: goto L_0x0b93;
            case 101: goto L_0x0b48;
            case 102: goto L_0x0afd;
            case 103: goto L_0x0ae0;
            case 104: goto L_0x0add;
            case 105: goto L_0x0add;
            case 106: goto L_0x0add;
            case 107: goto L_0x0add;
            case 108: goto L_0x0add;
            case 109: goto L_0x0add;
            case 110: goto L_0x0add;
            case 111: goto L_0x0add;
            case 112: goto L_0x0add;
            case 113: goto L_0x0add;
            default: goto L_0x0ad8;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0ad8, code lost:
        r8 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x0add, code lost:
        r8 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x0ae0, code lost:
        r26 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
        r42 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
        r8 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0af7, code lost:
        r22 = null;
        r29 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x0b01, code lost:
        if (r7 <= 0) goto L_0x0b1b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0b03, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0b1b, code lost:
        if (r1 == 0) goto L_0x0b35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0b1d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0b35, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0b4c, code lost:
        if (r7 <= 0) goto L_0x0b66;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0b4e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0b66, code lost:
        if (r1 == 0) goto L_0x0b80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0b68, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0b80, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0b97, code lost:
        if (r7 <= 0) goto L_0x0bb1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0b99, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0bb1, code lost:
        if (r1 == 0) goto L_0x0bcb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0bb3, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0bcb, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0be2, code lost:
        if (r7 <= 0) goto L_0x0bfc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0be4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0bfc, code lost:
        if (r1 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0bfe, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0c2d, code lost:
        if (r7 <= 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0c2f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0CLASSNAME, code lost:
        if (r1 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:0x0CLASSNAME, code lost:
        if (r7 <= 0) goto L_0x0c8f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0c8f, code lost:
        if (r1 == 0) goto L_0x0ca8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0ca8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0cb9, code lost:
        r26 = r1;
        r8 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0cc2, code lost:
        if (r7 <= 0) goto L_0x0cdb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0cc4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0cdb, code lost:
        if (r1 == 0) goto L_0x0cf9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x0cdd, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r4[0], r4[2], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0cf9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0d10, code lost:
        r10 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0d15, code lost:
        if (r10 <= 0) goto L_0x0d2f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x0d17, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0d2f, code lost:
        if (r1 == 0) goto L_0x0d4e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0d31, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r4[0], r4[2], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0d4e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x0d66, code lost:
        r10 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0d6b, code lost:
        if (r10 <= 0) goto L_0x0d85;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x0d6d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0d85, code lost:
        if (r1 == 0) goto L_0x0da4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x0d87, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r4[0], r4[2], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x0da4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x0dbc, code lost:
        r10 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x0dc1, code lost:
        if (r10 <= 0) goto L_0x0ddb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x0dc3, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x0ddb, code lost:
        if (r1 == 0) goto L_0x0df5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0ddd, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x0df5, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x0e08, code lost:
        r10 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x0e0d, code lost:
        if (r10 <= 0) goto L_0x0e46;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x0e11, code lost:
        if (r4.length <= 1) goto L_0x0e33;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x0e19, code lost:
        if (android.text.TextUtils.isEmpty(r4[1]) != false) goto L_0x0e33;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x0e1b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x0e33, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x0e46, code lost:
        if (r1 == 0) goto L_0x0e89;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x0e4a, code lost:
        if (r4.length <= 2) goto L_0x0e71;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x0e52, code lost:
        if (android.text.TextUtils.isEmpty(r4[2]) != false) goto L_0x0e71;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x0e54, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r4[0], r4[2], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:0x0e71, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x0e8b, code lost:
        if (r4.length <= 1) goto L_0x0ead;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x0e93, code lost:
        if (android.text.TextUtils.isEmpty(r4[1]) != false) goto L_0x0ead;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x0e95, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x0ead, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x0ec0, code lost:
        r10 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x0ec5, code lost:
        if (r10 <= 0) goto L_0x0edf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x0ec7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x0edf, code lost:
        if (r1 == 0) goto L_0x0ef9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x0ee1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x0ef9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x0f0c, code lost:
        r10 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x0var_, code lost:
        if (r10 <= 0) goto L_0x0f2b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x0f2b, code lost:
        if (r1 == 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x0f2d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x0var_, code lost:
        r10 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x0f5d, code lost:
        if (r10 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x0f5f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x0var_, code lost:
        if (r1 == 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x0fa4, code lost:
        r10 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x0fa9, code lost:
        if (r10 <= 0) goto L_0x0fc3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x0fab, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x0fc3, code lost:
        if (r1 == 0) goto L_0x0fdd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x0fc5, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x0fdd, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x0ff0, code lost:
        r10 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x0ff5, code lost:
        if (r10 <= 0) goto L_0x100f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x0ff7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x100f, code lost:
        if (r1 == 0) goto L_0x1029;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x1011, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x1029, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x103c, code lost:
        r10 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x1041, code lost:
        if (r10 <= 0) goto L_0x105b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x1043, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x105b, code lost:
        if (r1 == 0) goto L_0x107a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x105d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r4[0], r4[1], r4[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x107a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x1092, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x10ab, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r4[0], r4[1], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r4[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x10d5, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r4[0], r4[1], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r4[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x10ff, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r4[0], r4[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r4[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x1129, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r4[0], r4[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r4[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x1151, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r4[0], r4[1], org.telegram.messenger.LocaleController.formatPluralString(r26, org.telegram.messenger.Utilities.parseInt(r4[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x117d, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("UserAcceptedToGroupPushWithGroup", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x1196, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x11af, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x11c8, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x11e1, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x11fa, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x1213, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x122c, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupEndedCall", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x1245, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r4[0], r4[1], r4[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x1263, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x127b, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r4[0], r4[1], r4[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x1298, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x12b0, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x12c8, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x12df, code lost:
        r26 = r1;
        r8 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x12e2, code lost:
        r22 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x12e6, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r4[0], r4[1], r4[2]);
        r3 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x130d, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r4[0], r4[1], r4[2], r4[3]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x132f, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r4[0], r4[1], r4[2]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x1356, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x1378, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x139a, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x13bc, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r4[0], r4[1], r4[2]);
        r3 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x13e3, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r4[0], r4[1], r4[2]);
        r3 = org.telegram.messenger.LocaleController.getString("PollQuiz", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x140a, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r4[0], r4[1], r4[2]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x1431, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x1453, code lost:
        r10 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x1456, code lost:
        if (r4.length <= 2) goto L_0x1495;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x145e, code lost:
        if (android.text.TextUtils.isEmpty(r4[2]) != false) goto L_0x1495;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x1460, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r4[0], r4[1], r4[2]);
        r3 = r4[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x1495, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r4[0], r4[1]);
        r3 = r4[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x14c5, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x14e7, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x1508, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x1529, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x154a, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:851:0x156a, code lost:
        r26 = r1;
        r22 = r3;
        r8 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x1571, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r4[0], r4[1], r4[2]);
        r3 = r4[2];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x1590, code lost:
        r10 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x15a2, code lost:
        r26 = r1;
        r8 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x15a7, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r4[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:856:0x15cc, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r4[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x15f1, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r4[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x1616, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r4[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x1639, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r4[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x1662, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x167f, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:862:0x169c, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:863:0x16b9, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:864:0x16d6, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:865:0x16f8, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:866:0x171a, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x173c, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x1759, code lost:
        r6 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x175c, code lost:
        if (r4.length <= 1) goto L_0x1796;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:871:0x1764, code lost:
        if (android.text.TextUtils.isEmpty(r4[1]) != false) goto L_0x1796;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x1766, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r4[0], r4[1]);
        r3 = r4[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x1796, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x17ad, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x17ca, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x17e6, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x1802, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x181e, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x1839, code lost:
        r26 = r1;
        r22 = r3;
        r8 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x1840, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x1852, code lost:
        r26 = r1;
        r8 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x1857, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r4[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x187b, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r4[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x189f, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r4[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:0x18c3, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r4[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x18e6, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString(r26, org.telegram.messenger.Utilities.parseInt(r4[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x190d, code lost:
        r6 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x192f, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r4[0], r4[1], r4[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x194d, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x196f, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x198c, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x19a9, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x19c6, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x19e8, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x1a0a, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x1a2c, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:897:0x1a49, code lost:
        r8 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x1a4c, code lost:
        if (r4.length <= 1) goto L_0x1a86;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:0x1a54, code lost:
        if (android.text.TextUtils.isEmpty(r4[1]) != false) goto L_0x1a86;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x1a56, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r4[0], r4[1]);
        r3 = r4[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x1a86, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:0x1a9d, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:0x1aba, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x1ad7, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:906:0x1aea, code lost:
        r22 = null;
        r29 = false;
        r26 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:0x1af2, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x1b0f, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:909:0x1b2b, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:910:0x1b47, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:0x1b63, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:0x1b7e, code lost:
        r26 = r1;
        r22 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:913:0x1b83, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r4[0], r4[1]);
        r3 = r4[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:0x1b9d, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1bb3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:0x1b9f, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x1bb3, code lost:
        r22 = null;
        r26 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:917:0x1bb7, code lost:
        r29 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:918:0x1bb9, code lost:
        r1 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:0x1bbc, code lost:
        r44 = "REACT_";
        r8 = r7;
        r46 = r10;
        r45 = r11;
        r47 = r13;
        r17 = "CHAT_REACT_";
        r1 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:920:0x1bc9, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:?, code lost:
        r26 = r1.getReactedText(r2, r4);
        r22 = null;
        r29 = false;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x1bd3, code lost:
        if (r26 == null) goto L_0x1ce0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x1bd5, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_message();
        r3.id = r5;
        r3.random_id = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x1be0, code lost:
        if (r22 == null) goto L_0x1be5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x1be2, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x1be5, code lost:
        r4 = r26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x1be7, code lost:
        r3.message = r4;
        r3.date = (int) (r53 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:928:0x1bf0, code lost:
        if (r41 == false) goto L_0x1bf9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:0x1bf2, code lost:
        r3.action = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x1bf9, code lost:
        if (r12 == false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x1bfb, code lost:
        r3.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:932:0x1CLASSNAME, code lost:
        r3.dialog_id = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x1CLASSNAME, code lost:
        if (r30 == 0) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x1c0a, code lost:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r3.peer_id = r4;
        r4.channel_id = r30;
        r7 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:0x1c1c, code lost:
        if (r24 == 0) goto L_0x1c2a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:0x1c1e, code lost:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r3.peer_id = r4;
        r7 = r24;
        r4.chat_id = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:0x1c2a, code lost:
        r7 = r24;
        r4 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r3.peer_id = r4;
        r4.user_id = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x1CLASSNAME, code lost:
        r3.flags |= 256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:0x1CLASSNAME, code lost:
        if (r37 == 0) goto L_0x1c4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:0x1CLASSNAME, code lost:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r3.from_id = r4;
        r4.chat_id = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:943:0x1CLASSNAME, code lost:
        if (r47 == 0) goto L_0x1c5f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:944:0x1CLASSNAME, code lost:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r3.from_id = r4;
        r4.channel_id = r47;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:946:0x1CLASSNAME, code lost:
        if (r39 == 0) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x1CLASSNAME, code lost:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r3.from_id = r4;
        r4.user_id = r39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x1CLASSNAME, code lost:
        r3.from_id = r3.peer_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:0x1CLASSNAME, code lost:
        if (r16 != false) goto L_0x1c7c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x1CLASSNAME, code lost:
        if (r41 == false) goto L_0x1c7a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x1c7a, code lost:
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x1c7c, code lost:
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:0x1c7d, code lost:
        r3.mentioned = r4;
        r3.silent = r32;
        r3.from_scheduled = r23;
        r23 = new org.telegram.messenger.MessageObject(r34, r3, r26, r42, r46, r29, r45, r12, r43);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x1ca2, code lost:
        if (r2.startsWith(r44) != false) goto L_0x1caf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:957:0x1caa, code lost:
        if (r2.startsWith(r17) == false) goto L_0x1cad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x1cad, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x1caf, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:0x1cb0, code lost:
        r23.isReactionPush = r3;
        r3 = new java.util.ArrayList();
        r3.add(r23);
        org.telegram.messenger.NotificationsController.getInstance(r34).processNewMessages(r3, true, true, r1.countDownLatch);
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:962:0x1cc6, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:0x1cc7, code lost:
        r1 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:964:0x1cca, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:965:0x1ccb, code lost:
        r1 = r51;
        r34 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:966:0x1cd0, code lost:
        r1 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:967:0x1cd3, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:968:0x1cd4, code lost:
        r1 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:969:0x1cd7, code lost:
        r0 = th;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:0x1cd8, code lost:
        r34 = r12;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:971:0x1cdc, code lost:
        r33 = r6;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:972:0x1cde, code lost:
        r34 = r12;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:0x1ce0, code lost:
        r9 = true;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:974:0x1ce1, code lost:
        if (r9 == false) goto L_0x1ce8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:975:0x1ce3, code lost:
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:976:0x1ce8, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r34);
        org.telegram.tgnet.ConnectionsManager.getInstance(r34).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:977:0x1cf4, code lost:
        r0 = th;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:978:0x1cf5, code lost:
        r5 = r2;
        r6 = r33;
        r12 = r34;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:979:0x1cfc, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:980:0x1cfd, code lost:
        r33 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:981:0x1d00, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:982:0x1d01, code lost:
        r33 = r6;
        r2 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:983:0x1d04, code lost:
        r34 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:984:0x1d08, code lost:
        r33 = r6;
        r2 = r8;
        r34 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:987:0x1d11, code lost:
        r12 = r34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:989:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1(r12));
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:990:0x1d1e, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x1d1f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:992:0x1d20, code lost:
        r12 = r34;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:993:0x1d24, code lost:
        r33 = r6;
        r2 = r8;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0(r12));
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:994:0x1d34, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:995:0x1d35, code lost:
        r33 = r6;
        r2 = r8;
        r3 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification();
        r3.popup = false;
        r3.flags = 2;
        r3.inbox_date = (int) (r53 / 1000);
        r3.message = r16.getString("message");
        r3.type = "announcement";
        r3.media = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty();
        r4 = new org.telegram.tgnet.TLRPC$TL_updates();
        r4.updates.add(r3);
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3(r12, r4));
        org.telegram.tgnet.ConnectionsManager.getInstance(r12).resumeNetworkMaybe();
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:996:0x1d7f, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:997:0x1d80, code lost:
        r33 = r6;
        r2 = r8;
        r3 = r10.getInt("dc");
        r4 = r10.getString("addr").split(":");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:998:0x1d97, code lost:
        if (r4.length == 2) goto L_0x1d9f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:999:0x1d99, code lost:
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:200:0x0312, B:985:0x1d0d] */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1020:0x1de1  */
    /* JADX WARNING: Removed duplicated region for block: B:1021:0x1df1  */
    /* JADX WARNING: Removed duplicated region for block: B:1024:0x1df8  */
    /* JADX WARNING: Removed duplicated region for block: B:1026:0x0195 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0183 A[SYNTHETIC, Splitter:B:77:0x0183] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0198 A[Catch:{ all -> 0x011b }] */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x01a7 A[SYNTHETIC, Splitter:B:89:0x01a7] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$onMessageReceived$3(java.util.Map r52, long r53) {
        /*
            r51 = this;
            r1 = r51
            r2 = r52
            java.lang.String r3 = "REACT_"
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r4 == 0) goto L_0x000f
            java.lang.String r4 = "GCM START PROCESSING"
            org.telegram.messenger.FileLog.d(r4)
        L_0x000f:
            java.lang.String r6 = "p"
            java.lang.Object r6 = r2.get(r6)     // Catch:{ all -> 0x1dd9 }
            boolean r7 = r6 instanceof java.lang.String     // Catch:{ all -> 0x1dd9 }
            if (r7 != 0) goto L_0x0026
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1dd9 }
            if (r2 == 0) goto L_0x0022
            java.lang.String r2 = "GCM DECRYPT ERROR 1"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x1dd9 }
        L_0x0022:
            r51.onDecryptError()     // Catch:{ all -> 0x1dd9 }
            return
        L_0x0026:
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ all -> 0x1dd9 }
            r7 = 8
            byte[] r6 = android.util.Base64.decode(r6, r7)     // Catch:{ all -> 0x1dd9 }
            org.telegram.tgnet.NativeByteBuffer r8 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x1dd9 }
            int r9 = r6.length     // Catch:{ all -> 0x1dd9 }
            r8.<init>((int) r9)     // Catch:{ all -> 0x1dd9 }
            r8.writeBytes((byte[]) r6)     // Catch:{ all -> 0x1dd9 }
            r9 = 0
            r8.position(r9)     // Catch:{ all -> 0x1dd9 }
            byte[] r10 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1dd9 }
            if (r10 != 0) goto L_0x0050
            byte[] r10 = new byte[r7]     // Catch:{ all -> 0x1dd9 }
            org.telegram.messenger.SharedConfig.pushAuthKeyId = r10     // Catch:{ all -> 0x1dd9 }
            byte[] r10 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1dd9 }
            byte[] r10 = org.telegram.messenger.Utilities.computeSHA1((byte[]) r10)     // Catch:{ all -> 0x1dd9 }
            int r11 = r10.length     // Catch:{ all -> 0x1dd9 }
            int r11 = r11 - r7
            byte[] r12 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1dd9 }
            java.lang.System.arraycopy(r10, r11, r12, r9, r7)     // Catch:{ all -> 0x1dd9 }
        L_0x0050:
            byte[] r10 = new byte[r7]     // Catch:{ all -> 0x1dd9 }
            r11 = 1
            r8.readBytes(r10, r11)     // Catch:{ all -> 0x1dd9 }
            byte[] r12 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1dd9 }
            boolean r12 = java.util.Arrays.equals(r12, r10)     // Catch:{ all -> 0x1dd9 }
            r13 = 3
            r14 = 2
            if (r12 != 0) goto L_0x008b
            r51.onDecryptError()     // Catch:{ all -> 0x1dd9 }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1dd9 }
            if (r2 == 0) goto L_0x008a
            java.util.Locale r2 = java.util.Locale.US     // Catch:{ all -> 0x1dd9 }
            java.lang.String r3 = "GCM DECRYPT ERROR 2 k1=%s k2=%s, key=%s"
            java.lang.Object[] r6 = new java.lang.Object[r13]     // Catch:{ all -> 0x1dd9 }
            byte[] r7 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1dd9 }
            java.lang.String r7 = org.telegram.messenger.Utilities.bytesToHex(r7)     // Catch:{ all -> 0x1dd9 }
            r6[r9] = r7     // Catch:{ all -> 0x1dd9 }
            java.lang.String r7 = org.telegram.messenger.Utilities.bytesToHex(r10)     // Catch:{ all -> 0x1dd9 }
            r6[r11] = r7     // Catch:{ all -> 0x1dd9 }
            byte[] r7 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1dd9 }
            java.lang.String r7 = org.telegram.messenger.Utilities.bytesToHex(r7)     // Catch:{ all -> 0x1dd9 }
            r6[r14] = r7     // Catch:{ all -> 0x1dd9 }
            java.lang.String r2 = java.lang.String.format(r2, r3, r6)     // Catch:{ all -> 0x1dd9 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x1dd9 }
        L_0x008a:
            return
        L_0x008b:
            r10 = 16
            byte[] r10 = new byte[r10]     // Catch:{ all -> 0x1dd9 }
            r8.readBytes(r10, r11)     // Catch:{ all -> 0x1dd9 }
            byte[] r12 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1dd9 }
            org.telegram.messenger.MessageKeyData r12 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r12, r10, r11, r14)     // Catch:{ all -> 0x1dd9 }
            java.nio.ByteBuffer r15 = r8.buffer     // Catch:{ all -> 0x1dd9 }
            byte[] r5 = r12.aesKey     // Catch:{ all -> 0x1dd9 }
            byte[] r12 = r12.aesIv     // Catch:{ all -> 0x1dd9 }
            r18 = 0
            r19 = 0
            r20 = 24
            int r6 = r6.length     // Catch:{ all -> 0x1dd9 }
            int r21 = r6 + -24
            r16 = r5
            r17 = r12
            org.telegram.messenger.Utilities.aesIgeEncryption(r15, r16, r17, r18, r19, r20, r21)     // Catch:{ all -> 0x1dd9 }
            byte[] r23 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1dd9 }
            r24 = 96
            r25 = 32
            java.nio.ByteBuffer r5 = r8.buffer     // Catch:{ all -> 0x1dd9 }
            r27 = 24
            int r28 = r5.limit()     // Catch:{ all -> 0x1dd9 }
            r26 = r5
            byte[] r5 = org.telegram.messenger.Utilities.computeSHA256(r23, r24, r25, r26, r27, r28)     // Catch:{ all -> 0x1dd9 }
            boolean r5 = org.telegram.messenger.Utilities.arraysEquals(r10, r9, r5, r7)     // Catch:{ all -> 0x1dd9 }
            if (r5 != 0) goto L_0x00e3
            r51.onDecryptError()     // Catch:{ all -> 0x1dd9 }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1dd9 }
            if (r2 == 0) goto L_0x00e2
            java.lang.String r2 = "GCM DECRYPT ERROR 3, key = %s"
            java.lang.Object[] r3 = new java.lang.Object[r11]     // Catch:{ all -> 0x1dd9 }
            byte[] r5 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1dd9 }
            java.lang.String r5 = org.telegram.messenger.Utilities.bytesToHex(r5)     // Catch:{ all -> 0x1dd9 }
            r3[r9] = r5     // Catch:{ all -> 0x1dd9 }
            java.lang.String r2 = java.lang.String.format(r2, r3)     // Catch:{ all -> 0x1dd9 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x1dd9 }
        L_0x00e2:
            return
        L_0x00e3:
            int r5 = r8.readInt32(r11)     // Catch:{ all -> 0x1dd9 }
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x1dd9 }
            r8.readBytes(r5, r11)     // Catch:{ all -> 0x1dd9 }
            java.lang.String r6 = new java.lang.String     // Catch:{ all -> 0x1dd9 }
            r6.<init>(r5)     // Catch:{ all -> 0x1dd9 }
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ all -> 0x1dd2 }
            r5.<init>(r6)     // Catch:{ all -> 0x1dd2 }
            java.lang.String r8 = "loc_key"
            boolean r8 = r5.has(r8)     // Catch:{ all -> 0x1dd2 }
            if (r8 == 0) goto L_0x0108
            java.lang.String r8 = "loc_key"
            java.lang.String r8 = r5.getString(r8)     // Catch:{ all -> 0x0105 }
            goto L_0x010a
        L_0x0105:
            r0 = move-exception
            goto L_0x1dd5
        L_0x0108:
            java.lang.String r8 = ""
        L_0x010a:
            java.lang.String r10 = "custom"
            java.lang.Object r10 = r5.get(r10)     // Catch:{ all -> 0x1dc9 }
            boolean r10 = r10 instanceof org.json.JSONObject     // Catch:{ all -> 0x1dc9 }
            if (r10 == 0) goto L_0x0121
            java.lang.String r10 = "custom"
            org.json.JSONObject r10 = r5.getJSONObject(r10)     // Catch:{ all -> 0x011b }
            goto L_0x0126
        L_0x011b:
            r0 = move-exception
            r2 = r0
            r5 = r8
            r3 = -1
            goto L_0x1dde
        L_0x0121:
            org.json.JSONObject r10 = new org.json.JSONObject     // Catch:{ all -> 0x1dc9 }
            r10.<init>()     // Catch:{ all -> 0x1dc9 }
        L_0x0126:
            java.lang.String r12 = "user_id"
            boolean r12 = r5.has(r12)     // Catch:{ all -> 0x1dc9 }
            if (r12 == 0) goto L_0x0135
            java.lang.String r12 = "user_id"
            java.lang.Object r12 = r5.get(r12)     // Catch:{ all -> 0x011b }
            goto L_0x0136
        L_0x0135:
            r12 = 0
        L_0x0136:
            if (r12 != 0) goto L_0x0149
            int r12 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x011b }
            org.telegram.messenger.UserConfig r12 = org.telegram.messenger.UserConfig.getInstance(r12)     // Catch:{ all -> 0x011b }
            long r15 = r12.getClientUserId()     // Catch:{ all -> 0x011b }
        L_0x0142:
            r49 = r15
            r16 = r5
            r4 = r49
            goto L_0x017e
        L_0x0149:
            boolean r15 = r12 instanceof java.lang.Long     // Catch:{ all -> 0x1dc9 }
            if (r15 == 0) goto L_0x0154
            java.lang.Long r12 = (java.lang.Long) r12     // Catch:{ all -> 0x011b }
            long r15 = r12.longValue()     // Catch:{ all -> 0x011b }
            goto L_0x0142
        L_0x0154:
            boolean r15 = r12 instanceof java.lang.Integer     // Catch:{ all -> 0x1dc9 }
            if (r15 == 0) goto L_0x0162
            java.lang.Integer r12 = (java.lang.Integer) r12     // Catch:{ all -> 0x011b }
            int r12 = r12.intValue()     // Catch:{ all -> 0x011b }
            r16 = r5
            long r4 = (long) r12
            goto L_0x017e
        L_0x0162:
            r16 = r5
            boolean r4 = r12 instanceof java.lang.String     // Catch:{ all -> 0x1dc9 }
            if (r4 == 0) goto L_0x0174
            java.lang.String r12 = (java.lang.String) r12     // Catch:{ all -> 0x011b }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r12)     // Catch:{ all -> 0x011b }
            int r4 = r4.intValue()     // Catch:{ all -> 0x011b }
            long r4 = (long) r4
            goto L_0x017e
        L_0x0174:
            int r4 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1dc9 }
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x1dc9 }
            long r4 = r4.getClientUserId()     // Catch:{ all -> 0x1dc9 }
        L_0x017e:
            int r12 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1dc9 }
            r7 = 0
        L_0x0181:
            if (r7 >= r13) goto L_0x0195
            org.telegram.messenger.UserConfig r18 = org.telegram.messenger.UserConfig.getInstance(r7)     // Catch:{ all -> 0x011b }
            long r18 = r18.getClientUserId()     // Catch:{ all -> 0x011b }
            int r20 = (r18 > r4 ? 1 : (r18 == r4 ? 0 : -1))
            if (r20 != 0) goto L_0x0192
            r12 = r7
            r4 = 1
            goto L_0x0196
        L_0x0192:
            int r7 = r7 + 1
            goto L_0x0181
        L_0x0195:
            r4 = 0
        L_0x0196:
            if (r4 != 0) goto L_0x01a7
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x011b }
            if (r2 == 0) goto L_0x01a1
            java.lang.String r2 = "GCM ACCOUNT NOT FOUND"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x011b }
        L_0x01a1:
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x011b }
            r2.countDown()     // Catch:{ all -> 0x011b }
            return
        L_0x01a7:
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r12)     // Catch:{ all -> 0x1dc2 }
            boolean r4 = r4.isClientActivated()     // Catch:{ all -> 0x1dc2 }
            if (r4 != 0) goto L_0x01c6
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x01c0 }
            if (r2 == 0) goto L_0x01ba
            java.lang.String r2 = "GCM ACCOUNT NOT ACTIVATED"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x01c0 }
        L_0x01ba:
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x01c0 }
            r2.countDown()     // Catch:{ all -> 0x01c0 }
            return
        L_0x01c0:
            r0 = move-exception
            r2 = r0
            r5 = r8
            r3 = -1
            goto L_0x1ddf
        L_0x01c6:
            java.lang.String r4 = "google.sent_time"
            r2.get(r4)     // Catch:{ all -> 0x1dc2 }
            int r2 = r8.hashCode()     // Catch:{ all -> 0x1dc2 }
            switch(r2) {
                case -1963663249: goto L_0x01f1;
                case -920689527: goto L_0x01e7;
                case 633004703: goto L_0x01dd;
                case 1365673842: goto L_0x01d3;
                default: goto L_0x01d2;
            }
        L_0x01d2:
            goto L_0x01fb
        L_0x01d3:
            java.lang.String r2 = "GEO_LIVE_PENDING"
            boolean r2 = r8.equals(r2)     // Catch:{ all -> 0x01c0 }
            if (r2 == 0) goto L_0x01fb
            r2 = 3
            goto L_0x01fc
        L_0x01dd:
            java.lang.String r2 = "MESSAGE_ANNOUNCEMENT"
            boolean r2 = r8.equals(r2)     // Catch:{ all -> 0x01c0 }
            if (r2 == 0) goto L_0x01fb
            r2 = 1
            goto L_0x01fc
        L_0x01e7:
            java.lang.String r2 = "DC_UPDATE"
            boolean r2 = r8.equals(r2)     // Catch:{ all -> 0x01c0 }
            if (r2 == 0) goto L_0x01fb
            r2 = 0
            goto L_0x01fc
        L_0x01f1:
            java.lang.String r2 = "SESSION_REVOKE"
            boolean r2 = r8.equals(r2)     // Catch:{ all -> 0x01c0 }
            if (r2 == 0) goto L_0x01fb
            r2 = 2
            goto L_0x01fc
        L_0x01fb:
            r2 = -1
        L_0x01fc:
            if (r2 == 0) goto L_0x1d80
            if (r2 == r11) goto L_0x1d35
            if (r2 == r14) goto L_0x1d24
            if (r2 == r13) goto L_0x1d08
            java.lang.String r2 = "channel_id"
            boolean r2 = r10.has(r2)     // Catch:{ all -> 0x1d00 }
            if (r2 == 0) goto L_0x0218
            java.lang.String r2 = "channel_id"
            long r13 = r10.getLong(r2)     // Catch:{ all -> 0x0215 }
            r2 = r8
            long r7 = -r13
            goto L_0x021d
        L_0x0215:
            r0 = move-exception
            goto L_0x1dc5
        L_0x0218:
            r2 = r8
            r7 = 0
            r13 = 0
        L_0x021d:
            java.lang.String r15 = "from_id"
            boolean r15 = r10.has(r15)     // Catch:{ all -> 0x1cfc }
            if (r15 == 0) goto L_0x0231
            java.lang.String r7 = "from_id"
            long r7 = r10.getLong(r7)     // Catch:{ all -> 0x022e }
            r33 = r7
            goto L_0x0233
        L_0x022e:
            r0 = move-exception
            goto L_0x1dc6
        L_0x0231:
            r33 = 0
        L_0x0233:
            java.lang.String r15 = "chat_id"
            boolean r15 = r10.has(r15)     // Catch:{ all -> 0x1cfc }
            if (r15 == 0) goto L_0x0248
            java.lang.String r7 = "chat_id"
            long r7 = r10.getLong(r7)     // Catch:{ all -> 0x022e }
            long r4 = -r7
            r49 = r4
            r4 = r7
            r7 = r49
            goto L_0x024a
        L_0x0248:
            r4 = 0
        L_0x024a:
            java.lang.String r15 = "encryption_id"
            boolean r15 = r10.has(r15)     // Catch:{ all -> 0x1cfc }
            if (r15 == 0) goto L_0x025d
            java.lang.String r7 = "encryption_id"
            int r7 = r10.getInt(r7)     // Catch:{ all -> 0x022e }
            long r7 = (long) r7     // Catch:{ all -> 0x022e }
            long r7 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r7)     // Catch:{ all -> 0x022e }
        L_0x025d:
            java.lang.String r15 = "schedule"
            boolean r15 = r10.has(r15)     // Catch:{ all -> 0x1cfc }
            if (r15 == 0) goto L_0x026f
            java.lang.String r15 = "schedule"
            int r15 = r10.getInt(r15)     // Catch:{ all -> 0x022e }
            if (r15 != r11) goto L_0x026f
            r15 = 1
            goto L_0x0270
        L_0x026f:
            r15 = 0
        L_0x0270:
            r20 = 0
            int r23 = (r7 > r20 ? 1 : (r7 == r20 ? 0 : -1))
            if (r23 != 0) goto L_0x0280
            java.lang.String r11 = "ENCRYPTED_MESSAGE"
            boolean r11 = r11.equals(r2)     // Catch:{ all -> 0x022e }
            if (r11 == 0) goto L_0x0280
            long r7 = org.telegram.messenger.NotificationsController.globalSecretChatId     // Catch:{ all -> 0x022e }
        L_0x0280:
            r20 = 0
            int r11 = (r7 > r20 ? 1 : (r7 == r20 ? 0 : -1))
            if (r11 == 0) goto L_0x1cdc
            java.lang.String r11 = "READ_HISTORY"
            boolean r11 = r11.equals(r2)     // Catch:{ all -> 0x1cfc }
            java.lang.String r9 = " for dialogId = "
            if (r11 == 0) goto L_0x0304
            java.lang.String r3 = "max_id"
            int r3 = r10.getInt(r3)     // Catch:{ all -> 0x022e }
            java.util.ArrayList r10 = new java.util.ArrayList     // Catch:{ all -> 0x022e }
            r10.<init>()     // Catch:{ all -> 0x022e }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x022e }
            if (r11 == 0) goto L_0x02b9
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x022e }
            r11.<init>()     // Catch:{ all -> 0x022e }
            java.lang.String r15 = "GCM received read notification max_id = "
            r11.append(r15)     // Catch:{ all -> 0x022e }
            r11.append(r3)     // Catch:{ all -> 0x022e }
            r11.append(r9)     // Catch:{ all -> 0x022e }
            r11.append(r7)     // Catch:{ all -> 0x022e }
            java.lang.String r7 = r11.toString()     // Catch:{ all -> 0x022e }
            org.telegram.messenger.FileLog.d(r7)     // Catch:{ all -> 0x022e }
        L_0x02b9:
            r7 = 0
            int r9 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x02cc
            org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox r4 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox     // Catch:{ all -> 0x022e }
            r4.<init>()     // Catch:{ all -> 0x022e }
            r4.channel_id = r13     // Catch:{ all -> 0x022e }
            r4.max_id = r3     // Catch:{ all -> 0x022e }
            r10.add(r4)     // Catch:{ all -> 0x022e }
            goto L_0x02f1
        L_0x02cc:
            org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox r7 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox     // Catch:{ all -> 0x022e }
            r7.<init>()     // Catch:{ all -> 0x022e }
            r8 = r33
            r13 = 0
            int r11 = (r8 > r13 ? 1 : (r8 == r13 ? 0 : -1))
            if (r11 == 0) goto L_0x02e3
            org.telegram.tgnet.TLRPC$TL_peerUser r4 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x022e }
            r4.<init>()     // Catch:{ all -> 0x022e }
            r7.peer = r4     // Catch:{ all -> 0x022e }
            r4.user_id = r8     // Catch:{ all -> 0x022e }
            goto L_0x02ec
        L_0x02e3:
            org.telegram.tgnet.TLRPC$TL_peerChat r8 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x022e }
            r8.<init>()     // Catch:{ all -> 0x022e }
            r7.peer = r8     // Catch:{ all -> 0x022e }
            r8.chat_id = r4     // Catch:{ all -> 0x022e }
        L_0x02ec:
            r7.max_id = r3     // Catch:{ all -> 0x022e }
            r10.add(r7)     // Catch:{ all -> 0x022e }
        L_0x02f1:
            org.telegram.messenger.MessagesController r22 = org.telegram.messenger.MessagesController.getInstance(r12)     // Catch:{ all -> 0x022e }
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r23 = r10
            r22.processUpdateArray(r23, r24, r25, r26, r27)     // Catch:{ all -> 0x022e }
            goto L_0x1cdc
        L_0x0304:
            r35 = r33
            java.lang.String r11 = "MESSAGE_DELETED"
            boolean r11 = r11.equals(r2)     // Catch:{ all -> 0x1cfc }
            r33 = r6
            java.lang.String r6 = "messages"
            if (r11 == 0) goto L_0x037c
            java.lang.String r3 = r10.getString(r6)     // Catch:{ all -> 0x1dbd }
            java.lang.String r4 = ","
            java.lang.String[] r3 = r3.split(r4)     // Catch:{ all -> 0x1dbd }
            androidx.collection.LongSparseArray r4 = new androidx.collection.LongSparseArray     // Catch:{ all -> 0x1dbd }
            r4.<init>()     // Catch:{ all -> 0x1dbd }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x1dbd }
            r5.<init>()     // Catch:{ all -> 0x1dbd }
            r6 = 0
        L_0x0327:
            int r10 = r3.length     // Catch:{ all -> 0x1dbd }
            if (r6 >= r10) goto L_0x0336
            r10 = r3[r6]     // Catch:{ all -> 0x1dbd }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt(r10)     // Catch:{ all -> 0x1dbd }
            r5.add(r10)     // Catch:{ all -> 0x1dbd }
            int r6 = r6 + 1
            goto L_0x0327
        L_0x0336:
            long r10 = -r13
            r4.put(r10, r5)     // Catch:{ all -> 0x1dbd }
            org.telegram.messenger.NotificationsController r3 = org.telegram.messenger.NotificationsController.getInstance(r12)     // Catch:{ all -> 0x1dbd }
            r3.removeDeletedMessagesFromNotifications(r4)     // Catch:{ all -> 0x1dbd }
            org.telegram.messenger.MessagesController r23 = org.telegram.messenger.MessagesController.getInstance(r12)     // Catch:{ all -> 0x1dbd }
            r24 = r7
            r26 = r5
            r27 = r13
            r23.deleteMessagesByPush(r24, r26, r27)     // Catch:{ all -> 0x1dbd }
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1dbd }
            if (r3 == 0) goto L_0x1cde
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x1dbd }
            r3.<init>()     // Catch:{ all -> 0x1dbd }
            java.lang.String r4 = "GCM received "
            r3.append(r4)     // Catch:{ all -> 0x1dbd }
            r3.append(r2)     // Catch:{ all -> 0x1dbd }
            r3.append(r9)     // Catch:{ all -> 0x1dbd }
            r3.append(r7)     // Catch:{ all -> 0x1dbd }
            java.lang.String r4 = " mids = "
            r3.append(r4)     // Catch:{ all -> 0x1dbd }
            java.lang.String r4 = ","
            java.lang.String r4 = android.text.TextUtils.join(r4, r5)     // Catch:{ all -> 0x1dbd }
            r3.append(r4)     // Catch:{ all -> 0x1dbd }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x1dbd }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ all -> 0x1dbd }
            goto L_0x1cde
        L_0x037c:
            boolean r11 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x1cd7 }
            if (r11 != 0) goto L_0x1cde
            java.lang.String r11 = "msg_id"
            boolean r11 = r10.has(r11)     // Catch:{ all -> 0x1cd7 }
            if (r11 == 0) goto L_0x0393
            java.lang.String r11 = "msg_id"
            int r11 = r10.getInt(r11)     // Catch:{ all -> 0x1dbd }
            r23 = r15
            goto L_0x0396
        L_0x0393:
            r23 = r15
            r11 = 0
        L_0x0396:
            java.lang.String r15 = "random_id"
            boolean r15 = r10.has(r15)     // Catch:{ all -> 0x1cd7 }
            if (r15 == 0) goto L_0x03b3
            java.lang.String r15 = "random_id"
            java.lang.String r15 = r10.getString(r15)     // Catch:{ all -> 0x1dbd }
            java.lang.Long r15 = org.telegram.messenger.Utilities.parseLong(r15)     // Catch:{ all -> 0x1dbd }
            long r24 = r15.longValue()     // Catch:{ all -> 0x1dbd }
            r49 = r4
            r4 = r24
            r24 = r49
            goto L_0x03b7
        L_0x03b3:
            r24 = r4
            r4 = 0
        L_0x03b7:
            if (r11 == 0) goto L_0x03fd
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r12)     // Catch:{ all -> 0x03f4 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r15 = r15.dialogs_read_inbox_max     // Catch:{ all -> 0x03f4 }
            java.lang.Long r1 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x03f4 }
            java.lang.Object r1 = r15.get(r1)     // Catch:{ all -> 0x03f4 }
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch:{ all -> 0x03f4 }
            if (r1 != 0) goto L_0x03e8
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r12)     // Catch:{ all -> 0x03f4 }
            r15 = 0
            int r1 = r1.getDialogReadMax(r15, r7)     // Catch:{ all -> 0x03f4 }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x03f4 }
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r12)     // Catch:{ all -> 0x03f4 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r15 = r15.dialogs_read_inbox_max     // Catch:{ all -> 0x03f4 }
            r26 = r6
            java.lang.Long r6 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x03f4 }
            r15.put(r6, r1)     // Catch:{ all -> 0x03f4 }
            goto L_0x03ea
        L_0x03e8:
            r26 = r6
        L_0x03ea:
            int r1 = r1.intValue()     // Catch:{ all -> 0x03f4 }
            if (r11 <= r1) goto L_0x03f2
        L_0x03f0:
            r1 = 1
            goto L_0x0410
        L_0x03f2:
            r1 = 0
            goto L_0x0410
        L_0x03f4:
            r0 = move-exception
            r3 = -1
            r1 = r51
            r5 = r2
        L_0x03f9:
            r6 = r33
            goto L_0x1dd0
        L_0x03fd:
            r26 = r6
            r20 = 0
            int r1 = (r4 > r20 ? 1 : (r4 == r20 ? 0 : -1))
            if (r1 == 0) goto L_0x03f2
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r12)     // Catch:{ all -> 0x03f4 }
            boolean r1 = r1.checkMessageByRandomId(r4)     // Catch:{ all -> 0x03f4 }
            if (r1 != 0) goto L_0x03f2
            goto L_0x03f0
        L_0x0410:
            boolean r6 = r2.startsWith(r3)     // Catch:{ all -> 0x1cd3 }
            java.lang.String r15 = "CHAT_REACT_"
            if (r6 != 0) goto L_0x041e
            boolean r6 = r2.startsWith(r15)     // Catch:{ all -> 0x03f4 }
            if (r6 == 0) goto L_0x041f
        L_0x041e:
            r1 = 1
        L_0x041f:
            if (r1 == 0) goto L_0x1cd0
            java.lang.String r1 = "chat_from_id"
            r27 = r4
            r29 = r11
            r6 = r12
            r4 = 0
            long r11 = r10.optLong(r1, r4)     // Catch:{ all -> 0x1cca }
            java.lang.String r1 = "chat_from_broadcast_id"
            r30 = r13
            long r13 = r10.optLong(r1, r4)     // Catch:{ all -> 0x1cca }
            java.lang.String r1 = "chat_from_group_id"
            long r37 = r10.optLong(r1, r4)     // Catch:{ all -> 0x1cca }
            int r1 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r1 != 0) goto L_0x0447
            int r1 = (r37 > r4 ? 1 : (r37 == r4 ? 0 : -1))
            if (r1 == 0) goto L_0x0445
            goto L_0x0447
        L_0x0445:
            r1 = 0
            goto L_0x0448
        L_0x0447:
            r1 = 1
        L_0x0448:
            java.lang.String r4 = "mention"
            boolean r4 = r10.has(r4)     // Catch:{ all -> 0x1cca }
            if (r4 == 0) goto L_0x0461
            java.lang.String r4 = "mention"
            int r4 = r10.getInt(r4)     // Catch:{ all -> 0x045a }
            if (r4 == 0) goto L_0x0461
            r4 = 1
            goto L_0x0462
        L_0x045a:
            r0 = move-exception
            r3 = -1
            r1 = r51
            r5 = r2
            r12 = r6
            goto L_0x03f9
        L_0x0461:
            r4 = 0
        L_0x0462:
            java.lang.String r5 = "silent"
            boolean r5 = r10.has(r5)     // Catch:{ all -> 0x1cca }
            if (r5 == 0) goto L_0x0476
            java.lang.String r5 = "silent"
            int r5 = r10.getInt(r5)     // Catch:{ all -> 0x045a }
            if (r5 == 0) goto L_0x0476
            r34 = r6
            r5 = 1
            goto L_0x0479
        L_0x0476:
            r34 = r6
            r5 = 0
        L_0x0479:
            java.lang.String r6 = "loc_args"
            r32 = r5
            r5 = r16
            boolean r6 = r5.has(r6)     // Catch:{ all -> 0x1cc6 }
            if (r6 == 0) goto L_0x04ac
            java.lang.String r6 = "loc_args"
            org.json.JSONArray r5 = r5.getJSONArray(r6)     // Catch:{ all -> 0x04a1 }
            int r6 = r5.length()     // Catch:{ all -> 0x04a1 }
            r16 = r4
            java.lang.String[] r4 = new java.lang.String[r6]     // Catch:{ all -> 0x04a1 }
            r39 = r11
            r11 = 0
        L_0x0496:
            if (r11 >= r6) goto L_0x04b1
            java.lang.String r12 = r5.getString(r11)     // Catch:{ all -> 0x04a1 }
            r4[r11] = r12     // Catch:{ all -> 0x04a1 }
            int r11 = r11 + 1
            goto L_0x0496
        L_0x04a1:
            r0 = move-exception
            r3 = -1
            r1 = r51
            r5 = r2
            r6 = r33
            r12 = r34
            goto L_0x1dd0
        L_0x04ac:
            r16 = r4
            r39 = r11
            r4 = 0
        L_0x04b1:
            r5 = 0
            r6 = r4[r5]     // Catch:{ all -> 0x1cc6 }
            java.lang.String r5 = "edit_date"
            boolean r5 = r10.has(r5)     // Catch:{ all -> 0x1cc6 }
            java.lang.String r10 = "CHAT_"
            boolean r10 = r2.startsWith(r10)     // Catch:{ all -> 0x1cc6 }
            if (r10 == 0) goto L_0x04f6
            boolean r10 = org.telegram.messenger.UserObject.isReplyUser((long) r7)     // Catch:{ all -> 0x04a1 }
            if (r10 == 0) goto L_0x04e0
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x04a1 }
            r10.<init>()     // Catch:{ all -> 0x04a1 }
            r10.append(r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r6 = " @ "
            r10.append(r6)     // Catch:{ all -> 0x04a1 }
            r6 = 1
            r11 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r10.append(r11)     // Catch:{ all -> 0x04a1 }
            java.lang.String r6 = r10.toString()     // Catch:{ all -> 0x04a1 }
            goto L_0x0518
        L_0x04e0:
            r10 = 0
            int r12 = (r30 > r10 ? 1 : (r30 == r10 ? 0 : -1))
            if (r12 == 0) goto L_0x04e8
            r10 = 1
            goto L_0x04e9
        L_0x04e8:
            r10 = 0
        L_0x04e9:
            r11 = 1
            r12 = r4[r11]     // Catch:{ all -> 0x04a1 }
            r11 = 0
            r41 = 0
            r49 = r10
            r10 = r6
            r6 = r12
            r12 = r49
            goto L_0x051d
        L_0x04f6:
            java.lang.String r10 = "PINNED_"
            boolean r10 = r2.startsWith(r10)     // Catch:{ all -> 0x1cc6 }
            if (r10 == 0) goto L_0x050d
            r10 = 0
            int r12 = (r30 > r10 ? 1 : (r30 == r10 ? 0 : -1))
            if (r12 == 0) goto L_0x0506
            r10 = 1
            goto L_0x0507
        L_0x0506:
            r10 = 0
        L_0x0507:
            r12 = r10
            r10 = 0
            r11 = 0
            r41 = 1
            goto L_0x051d
        L_0x050d:
            java.lang.String r10 = "CHANNEL_"
            boolean r10 = r2.startsWith(r10)     // Catch:{ all -> 0x1cc6 }
            if (r10 == 0) goto L_0x0518
            r10 = 0
            r11 = 1
            goto L_0x051a
        L_0x0518:
            r10 = 0
            r11 = 0
        L_0x051a:
            r12 = 0
            r41 = 0
        L_0x051d:
            boolean r42 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1cc6 }
            if (r42 == 0) goto L_0x054a
            r42 = r6
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x04a1 }
            r6.<init>()     // Catch:{ all -> 0x04a1 }
            r43 = r5
            java.lang.String r5 = "GCM received message notification "
            r6.append(r5)     // Catch:{ all -> 0x04a1 }
            r6.append(r2)     // Catch:{ all -> 0x04a1 }
            r6.append(r9)     // Catch:{ all -> 0x04a1 }
            r6.append(r7)     // Catch:{ all -> 0x04a1 }
            java.lang.String r5 = " mid = "
            r6.append(r5)     // Catch:{ all -> 0x04a1 }
            r5 = r29
            r6.append(r5)     // Catch:{ all -> 0x04a1 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x04a1 }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x0550
        L_0x054a:
            r43 = r5
            r42 = r6
            r5 = r29
        L_0x0550:
            boolean r6 = r2.startsWith(r3)     // Catch:{ all -> 0x1cc6 }
            if (r6 != 0) goto L_0x1bbc
            boolean r6 = r2.startsWith(r15)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x056b
            r1 = r51
            r44 = r3
            r8 = r7
            r46 = r10
            r45 = r11
            r47 = r13
            r17 = r15
            goto L_0x1bc9
        L_0x056b:
            int r6 = r2.hashCode()     // Catch:{ all -> 0x04a1 }
            switch(r6) {
                case -2100047043: goto L_0x0ab0;
                case -2091498420: goto L_0x0aa5;
                case -2053872415: goto L_0x0a9a;
                case -2039746363: goto L_0x0a8f;
                case -2023218804: goto L_0x0a84;
                case -1979538588: goto L_0x0a79;
                case -1979536003: goto L_0x0a6e;
                case -1979535888: goto L_0x0a63;
                case -1969004705: goto L_0x0a58;
                case -1946699248: goto L_0x0a4c;
                case -1717283471: goto L_0x0a40;
                case -1646640058: goto L_0x0a34;
                case -1528047021: goto L_0x0a28;
                case -1493579426: goto L_0x0a1c;
                case -1482481933: goto L_0x0a10;
                case -1480102982: goto L_0x0a05;
                case -1478041834: goto L_0x09f9;
                case -1474543101: goto L_0x09ee;
                case -1465695932: goto L_0x09e2;
                case -1374906292: goto L_0x09d6;
                case -1372940586: goto L_0x09ca;
                case -1264245338: goto L_0x09be;
                case -1236154001: goto L_0x09b2;
                case -1236086700: goto L_0x09a6;
                case -1236077786: goto L_0x099a;
                case -1235796237: goto L_0x098e;
                case -1235760759: goto L_0x0982;
                case -1235686303: goto L_0x0977;
                case -1198046100: goto L_0x096c;
                case -1124254527: goto L_0x0960;
                case -1085137927: goto L_0x0954;
                case -1084856378: goto L_0x0948;
                case -1084820900: goto L_0x093c;
                case -1084746444: goto L_0x0930;
                case -819729482: goto L_0x0924;
                case -772141857: goto L_0x0918;
                case -638310039: goto L_0x090c;
                case -590403924: goto L_0x0900;
                case -589196239: goto L_0x08f4;
                case -589193654: goto L_0x08e8;
                case -589193539: goto L_0x08dc;
                case -440169325: goto L_0x08d0;
                case -412748110: goto L_0x08c4;
                case -228518075: goto L_0x08b8;
                case -213586509: goto L_0x08ac;
                case -115582002: goto L_0x08a0;
                case -112621464: goto L_0x0894;
                case -108522133: goto L_0x0888;
                case -107572034: goto L_0x087d;
                case -40534265: goto L_0x0871;
                case 52369421: goto L_0x0865;
                case 65254746: goto L_0x0859;
                case 141040782: goto L_0x084d;
                case 202550149: goto L_0x0841;
                case 309993049: goto L_0x0835;
                case 309995634: goto L_0x0829;
                case 309995749: goto L_0x081d;
                case 320532812: goto L_0x0811;
                case 328933854: goto L_0x0805;
                case 331340546: goto L_0x07f9;
                case 342406591: goto L_0x07ed;
                case 344816990: goto L_0x07e1;
                case 346878138: goto L_0x07d5;
                case 350376871: goto L_0x07c9;
                case 608430149: goto L_0x07bd;
                case 615714517: goto L_0x07b2;
                case 715508879: goto L_0x07a6;
                case 728985323: goto L_0x079a;
                case 731046471: goto L_0x078e;
                case 734545204: goto L_0x0782;
                case 802032552: goto L_0x0776;
                case 991498806: goto L_0x076a;
                case 1007364121: goto L_0x075e;
                case 1019850010: goto L_0x0752;
                case 1019917311: goto L_0x0746;
                case 1019926225: goto L_0x073a;
                case 1020207774: goto L_0x072e;
                case 1020243252: goto L_0x0722;
                case 1020317708: goto L_0x0716;
                case 1060282259: goto L_0x070a;
                case 1060349560: goto L_0x06fe;
                case 1060358474: goto L_0x06f2;
                case 1060640023: goto L_0x06e6;
                case 1060675501: goto L_0x06da;
                case 1060749957: goto L_0x06cf;
                case 1073049781: goto L_0x06c3;
                case 1078101399: goto L_0x06b7;
                case 1110103437: goto L_0x06ab;
                case 1160762272: goto L_0x069f;
                case 1172918249: goto L_0x0693;
                case 1234591620: goto L_0x0687;
                case 1281128640: goto L_0x067b;
                case 1281131225: goto L_0x066f;
                case 1281131340: goto L_0x0663;
                case 1310789062: goto L_0x0658;
                case 1333118583: goto L_0x064c;
                case 1361447897: goto L_0x0640;
                case 1498266155: goto L_0x0634;
                case 1533804208: goto L_0x0628;
                case 1540131626: goto L_0x061c;
                case 1547988151: goto L_0x0610;
                case 1561464595: goto L_0x0604;
                case 1563525743: goto L_0x05f8;
                case 1567024476: goto L_0x05ec;
                case 1810705077: goto L_0x05e0;
                case 1815177512: goto L_0x05d4;
                case 1954774321: goto L_0x05c8;
                case 1963241394: goto L_0x05bc;
                case 2014789757: goto L_0x05b0;
                case 2022049433: goto L_0x05a4;
                case 2034984710: goto L_0x0598;
                case 2048733346: goto L_0x058c;
                case 2099392181: goto L_0x0580;
                case 2140162142: goto L_0x0574;
                default: goto L_0x0572;
            }     // Catch:{ all -> 0x04a1 }
        L_0x0572:
            goto L_0x0abb
        L_0x0574:
            java.lang.String r6 = "CHAT_MESSAGE_GEOLIVE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 60
            goto L_0x0abc
        L_0x0580:
            java.lang.String r6 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 43
            goto L_0x0abc
        L_0x058c:
            java.lang.String r6 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 28
            goto L_0x0abc
        L_0x0598:
            java.lang.String r6 = "CHANNEL_MESSAGE_PLAYLIST"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 45
            goto L_0x0abc
        L_0x05a4:
            java.lang.String r6 = "PINNED_CONTACT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 94
            goto L_0x0abc
        L_0x05b0:
            java.lang.String r6 = "CHAT_PHOTO_EDITED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 68
            goto L_0x0abc
        L_0x05bc:
            java.lang.String r6 = "LOCKED_MESSAGE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 108(0x6c, float:1.51E-43)
            goto L_0x0abc
        L_0x05c8:
            java.lang.String r6 = "CHAT_MESSAGE_PLAYLIST"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 83
            goto L_0x0abc
        L_0x05d4:
            java.lang.String r6 = "CHANNEL_MESSAGES"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 47
            goto L_0x0abc
        L_0x05e0:
            java.lang.String r6 = "MESSAGE_INVOICE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 21
            goto L_0x0abc
        L_0x05ec:
            java.lang.String r6 = "CHAT_MESSAGE_VIDEO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 51
            goto L_0x0abc
        L_0x05f8:
            java.lang.String r6 = "CHAT_MESSAGE_ROUND"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 52
            goto L_0x0abc
        L_0x0604:
            java.lang.String r6 = "CHAT_MESSAGE_PHOTO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 50
            goto L_0x0abc
        L_0x0610:
            java.lang.String r6 = "CHAT_MESSAGE_AUDIO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 55
            goto L_0x0abc
        L_0x061c:
            java.lang.String r6 = "MESSAGE_PLAYLIST"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 25
            goto L_0x0abc
        L_0x0628:
            java.lang.String r6 = "MESSAGE_VIDEOS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 24
            goto L_0x0abc
        L_0x0634:
            java.lang.String r6 = "PHONE_CALL_MISSED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 113(0x71, float:1.58E-43)
            goto L_0x0abc
        L_0x0640:
            java.lang.String r6 = "MESSAGE_PHOTOS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 23
            goto L_0x0abc
        L_0x064c:
            java.lang.String r6 = "CHAT_MESSAGE_VIDEOS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 82
            goto L_0x0abc
        L_0x0658:
            java.lang.String r6 = "MESSAGE_NOTEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 2
            goto L_0x0abc
        L_0x0663:
            java.lang.String r6 = "MESSAGE_GIF"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 17
            goto L_0x0abc
        L_0x066f:
            java.lang.String r6 = "MESSAGE_GEO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 15
            goto L_0x0abc
        L_0x067b:
            java.lang.String r6 = "MESSAGE_DOC"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 9
            goto L_0x0abc
        L_0x0687:
            java.lang.String r6 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 63
            goto L_0x0abc
        L_0x0693:
            java.lang.String r6 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 39
            goto L_0x0abc
        L_0x069f:
            java.lang.String r6 = "CHAT_MESSAGE_PHOTOS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 81
            goto L_0x0abc
        L_0x06ab:
            java.lang.String r6 = "CHAT_MESSAGE_NOTEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 49
            goto L_0x0abc
        L_0x06b7:
            java.lang.String r6 = "CHAT_TITLE_EDITED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 67
            goto L_0x0abc
        L_0x06c3:
            java.lang.String r6 = "PINNED_NOTEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 87
            goto L_0x0abc
        L_0x06cf:
            java.lang.String r6 = "MESSAGE_TEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 0
            goto L_0x0abc
        L_0x06da:
            java.lang.String r6 = "MESSAGE_QUIZ"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 13
            goto L_0x0abc
        L_0x06e6:
            java.lang.String r6 = "MESSAGE_POLL"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 14
            goto L_0x0abc
        L_0x06f2:
            java.lang.String r6 = "MESSAGE_GAME"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 18
            goto L_0x0abc
        L_0x06fe:
            java.lang.String r6 = "MESSAGE_FWDS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 22
            goto L_0x0abc
        L_0x070a:
            java.lang.String r6 = "MESSAGE_DOCS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 26
            goto L_0x0abc
        L_0x0716:
            java.lang.String r6 = "CHAT_MESSAGE_TEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 48
            goto L_0x0abc
        L_0x0722:
            java.lang.String r6 = "CHAT_MESSAGE_QUIZ"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 57
            goto L_0x0abc
        L_0x072e:
            java.lang.String r6 = "CHAT_MESSAGE_POLL"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 58
            goto L_0x0abc
        L_0x073a:
            java.lang.String r6 = "CHAT_MESSAGE_GAME"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 62
            goto L_0x0abc
        L_0x0746:
            java.lang.String r6 = "CHAT_MESSAGE_FWDS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 80
            goto L_0x0abc
        L_0x0752:
            java.lang.String r6 = "CHAT_MESSAGE_DOCS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 84
            goto L_0x0abc
        L_0x075e:
            java.lang.String r6 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 20
            goto L_0x0abc
        L_0x076a:
            java.lang.String r6 = "PINNED_GEOLIVE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 98
            goto L_0x0abc
        L_0x0776:
            java.lang.String r6 = "MESSAGE_CONTACT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 12
            goto L_0x0abc
        L_0x0782:
            java.lang.String r6 = "PINNED_VIDEO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 89
            goto L_0x0abc
        L_0x078e:
            java.lang.String r6 = "PINNED_ROUND"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 90
            goto L_0x0abc
        L_0x079a:
            java.lang.String r6 = "PINNED_PHOTO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 88
            goto L_0x0abc
        L_0x07a6:
            java.lang.String r6 = "PINNED_AUDIO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 93
            goto L_0x0abc
        L_0x07b2:
            java.lang.String r6 = "MESSAGE_PHOTO_SECRET"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 4
            goto L_0x0abc
        L_0x07bd:
            java.lang.String r6 = "CHAT_VOICECHAT_INVITE_YOU"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 73
            goto L_0x0abc
        L_0x07c9:
            java.lang.String r6 = "CHANNEL_MESSAGE_VIDEO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 30
            goto L_0x0abc
        L_0x07d5:
            java.lang.String r6 = "CHANNEL_MESSAGE_ROUND"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 31
            goto L_0x0abc
        L_0x07e1:
            java.lang.String r6 = "CHANNEL_MESSAGE_PHOTO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 29
            goto L_0x0abc
        L_0x07ed:
            java.lang.String r6 = "CHAT_VOICECHAT_END"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 72
            goto L_0x0abc
        L_0x07f9:
            java.lang.String r6 = "CHANNEL_MESSAGE_AUDIO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 34
            goto L_0x0abc
        L_0x0805:
            java.lang.String r6 = "CHAT_MESSAGE_STICKER"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 54
            goto L_0x0abc
        L_0x0811:
            java.lang.String r6 = "MESSAGES"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 27
            goto L_0x0abc
        L_0x081d:
            java.lang.String r6 = "CHAT_MESSAGE_GIF"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 61
            goto L_0x0abc
        L_0x0829:
            java.lang.String r6 = "CHAT_MESSAGE_GEO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 59
            goto L_0x0abc
        L_0x0835:
            java.lang.String r6 = "CHAT_MESSAGE_DOC"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 53
            goto L_0x0abc
        L_0x0841:
            java.lang.String r6 = "CHAT_VOICECHAT_INVITE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 71
            goto L_0x0abc
        L_0x084d:
            java.lang.String r6 = "CHAT_LEFT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 76
            goto L_0x0abc
        L_0x0859:
            java.lang.String r6 = "CHAT_ADD_YOU"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 66
            goto L_0x0abc
        L_0x0865:
            java.lang.String r6 = "REACT_TEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 104(0x68, float:1.46E-43)
            goto L_0x0abc
        L_0x0871:
            java.lang.String r6 = "CHAT_DELETE_MEMBER"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 74
            goto L_0x0abc
        L_0x087d:
            java.lang.String r6 = "MESSAGE_SCREENSHOT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 7
            goto L_0x0abc
        L_0x0888:
            java.lang.String r6 = "AUTH_REGION"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 107(0x6b, float:1.5E-43)
            goto L_0x0abc
        L_0x0894:
            java.lang.String r6 = "CONTACT_JOINED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 105(0x69, float:1.47E-43)
            goto L_0x0abc
        L_0x08a0:
            java.lang.String r6 = "CHAT_MESSAGE_INVOICE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 64
            goto L_0x0abc
        L_0x08ac:
            java.lang.String r6 = "ENCRYPTION_REQUEST"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 109(0x6d, float:1.53E-43)
            goto L_0x0abc
        L_0x08b8:
            java.lang.String r6 = "MESSAGE_GEOLIVE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 16
            goto L_0x0abc
        L_0x08c4:
            java.lang.String r6 = "CHAT_DELETE_YOU"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 75
            goto L_0x0abc
        L_0x08d0:
            java.lang.String r6 = "AUTH_UNKNOWN"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 106(0x6a, float:1.49E-43)
            goto L_0x0abc
        L_0x08dc:
            java.lang.String r6 = "PINNED_GIF"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 102(0x66, float:1.43E-43)
            goto L_0x0abc
        L_0x08e8:
            java.lang.String r6 = "PINNED_GEO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 97
            goto L_0x0abc
        L_0x08f4:
            java.lang.String r6 = "PINNED_DOC"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 91
            goto L_0x0abc
        L_0x0900:
            java.lang.String r6 = "PINNED_GAME_SCORE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 100
            goto L_0x0abc
        L_0x090c:
            java.lang.String r6 = "CHANNEL_MESSAGE_STICKER"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 33
            goto L_0x0abc
        L_0x0918:
            java.lang.String r6 = "PHONE_CALL_REQUEST"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 111(0x6f, float:1.56E-43)
            goto L_0x0abc
        L_0x0924:
            java.lang.String r6 = "PINNED_STICKER"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 92
            goto L_0x0abc
        L_0x0930:
            java.lang.String r6 = "PINNED_TEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 86
            goto L_0x0abc
        L_0x093c:
            java.lang.String r6 = "PINNED_QUIZ"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 95
            goto L_0x0abc
        L_0x0948:
            java.lang.String r6 = "PINNED_POLL"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 96
            goto L_0x0abc
        L_0x0954:
            java.lang.String r6 = "PINNED_GAME"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 99
            goto L_0x0abc
        L_0x0960:
            java.lang.String r6 = "CHAT_MESSAGE_CONTACT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 56
            goto L_0x0abc
        L_0x096c:
            java.lang.String r6 = "MESSAGE_VIDEO_SECRET"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 6
            goto L_0x0abc
        L_0x0977:
            java.lang.String r6 = "CHANNEL_MESSAGE_TEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 1
            goto L_0x0abc
        L_0x0982:
            java.lang.String r6 = "CHANNEL_MESSAGE_QUIZ"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 36
            goto L_0x0abc
        L_0x098e:
            java.lang.String r6 = "CHANNEL_MESSAGE_POLL"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 37
            goto L_0x0abc
        L_0x099a:
            java.lang.String r6 = "CHANNEL_MESSAGE_GAME"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 41
            goto L_0x0abc
        L_0x09a6:
            java.lang.String r6 = "CHANNEL_MESSAGE_FWDS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 42
            goto L_0x0abc
        L_0x09b2:
            java.lang.String r6 = "CHANNEL_MESSAGE_DOCS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 46
            goto L_0x0abc
        L_0x09be:
            java.lang.String r6 = "PINNED_INVOICE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 101(0x65, float:1.42E-43)
            goto L_0x0abc
        L_0x09ca:
            java.lang.String r6 = "CHAT_RETURNED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 77
            goto L_0x0abc
        L_0x09d6:
            java.lang.String r6 = "ENCRYPTED_MESSAGE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 103(0x67, float:1.44E-43)
            goto L_0x0abc
        L_0x09e2:
            java.lang.String r6 = "ENCRYPTION_ACCEPT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 110(0x6e, float:1.54E-43)
            goto L_0x0abc
        L_0x09ee:
            java.lang.String r6 = "MESSAGE_VIDEO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 5
            goto L_0x0abc
        L_0x09f9:
            java.lang.String r6 = "MESSAGE_ROUND"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 8
            goto L_0x0abc
        L_0x0a05:
            java.lang.String r6 = "MESSAGE_PHOTO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 3
            goto L_0x0abc
        L_0x0a10:
            java.lang.String r6 = "MESSAGE_MUTED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 112(0x70, float:1.57E-43)
            goto L_0x0abc
        L_0x0a1c:
            java.lang.String r6 = "MESSAGE_AUDIO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 11
            goto L_0x0abc
        L_0x0a28:
            java.lang.String r6 = "CHAT_MESSAGES"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 85
            goto L_0x0abc
        L_0x0a34:
            java.lang.String r6 = "CHAT_VOICECHAT_START"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 70
            goto L_0x0abc
        L_0x0a40:
            java.lang.String r6 = "CHAT_REQ_JOINED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 79
            goto L_0x0abc
        L_0x0a4c:
            java.lang.String r6 = "CHAT_JOINED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 78
            goto L_0x0abc
        L_0x0a58:
            java.lang.String r6 = "CHAT_ADD_MEMBER"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 69
            goto L_0x0abc
        L_0x0a63:
            java.lang.String r6 = "CHANNEL_MESSAGE_GIF"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 40
            goto L_0x0abc
        L_0x0a6e:
            java.lang.String r6 = "CHANNEL_MESSAGE_GEO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 38
            goto L_0x0abc
        L_0x0a79:
            java.lang.String r6 = "CHANNEL_MESSAGE_DOC"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 32
            goto L_0x0abc
        L_0x0a84:
            java.lang.String r6 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 44
            goto L_0x0abc
        L_0x0a8f:
            java.lang.String r6 = "MESSAGE_STICKER"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 10
            goto L_0x0abc
        L_0x0a9a:
            java.lang.String r6 = "CHAT_CREATED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 65
            goto L_0x0abc
        L_0x0aa5:
            java.lang.String r6 = "CHANNEL_MESSAGE_CONTACT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 35
            goto L_0x0abc
        L_0x0ab0:
            java.lang.String r6 = "MESSAGE_GAME_SCORE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a1 }
            if (r6 == 0) goto L_0x0abb
            r6 = 19
            goto L_0x0abc
        L_0x0abb:
            r6 = -1
        L_0x0abc:
            java.lang.String r9 = "Photos"
            r17 = r15
            java.lang.String r15 = " "
            r44 = r3
            java.lang.String r3 = "NotificationGroupFew"
            r45 = r11
            java.lang.String r11 = "NotificationMessageFew"
            r46 = r10
            java.lang.String r10 = "ChannelMessageFew"
            r47 = r13
            r13 = 2131624435(0x7f0e01f3, float:1.887605E38)
            java.lang.String r14 = "AttachSticker"
            switch(r6) {
                case 0: goto L_0x1b83;
                case 1: goto L_0x1b83;
                case 2: goto L_0x1b63;
                case 3: goto L_0x1b47;
                case 4: goto L_0x1b2b;
                case 5: goto L_0x1b0f;
                case 6: goto L_0x1af2;
                case 7: goto L_0x1ad7;
                case 8: goto L_0x1aba;
                case 9: goto L_0x1a9d;
                case 10: goto L_0x1a49;
                case 11: goto L_0x1a2c;
                case 12: goto L_0x1a0a;
                case 13: goto L_0x19e8;
                case 14: goto L_0x19c6;
                case 15: goto L_0x19a9;
                case 16: goto L_0x198c;
                case 17: goto L_0x196f;
                case 18: goto L_0x194d;
                case 19: goto L_0x192f;
                case 20: goto L_0x192f;
                case 21: goto L_0x190d;
                case 22: goto L_0x18e6;
                case 23: goto L_0x18c3;
                case 24: goto L_0x189f;
                case 25: goto L_0x187b;
                case 26: goto L_0x1857;
                case 27: goto L_0x1840;
                case 28: goto L_0x181e;
                case 29: goto L_0x1802;
                case 30: goto L_0x17e6;
                case 31: goto L_0x17ca;
                case 32: goto L_0x17ad;
                case 33: goto L_0x1759;
                case 34: goto L_0x173c;
                case 35: goto L_0x171a;
                case 36: goto L_0x16f8;
                case 37: goto L_0x16d6;
                case 38: goto L_0x16b9;
                case 39: goto L_0x169c;
                case 40: goto L_0x167f;
                case 41: goto L_0x1662;
                case 42: goto L_0x1639;
                case 43: goto L_0x1616;
                case 44: goto L_0x15f1;
                case 45: goto L_0x15cc;
                case 46: goto L_0x15a7;
                case 47: goto L_0x1590;
                case 48: goto L_0x1571;
                case 49: goto L_0x154a;
                case 50: goto L_0x1529;
                case 51: goto L_0x1508;
                case 52: goto L_0x14e7;
                case 53: goto L_0x14c5;
                case 54: goto L_0x1453;
                case 55: goto L_0x1431;
                case 56: goto L_0x140a;
                case 57: goto L_0x13e3;
                case 58: goto L_0x13bc;
                case 59: goto L_0x139a;
                case 60: goto L_0x1378;
                case 61: goto L_0x1356;
                case 62: goto L_0x132f;
                case 63: goto L_0x130d;
                case 64: goto L_0x12e6;
                case 65: goto L_0x12c8;
                case 66: goto L_0x12c8;
                case 67: goto L_0x12b0;
                case 68: goto L_0x1298;
                case 69: goto L_0x127b;
                case 70: goto L_0x1263;
                case 71: goto L_0x1245;
                case 72: goto L_0x122c;
                case 73: goto L_0x1213;
                case 74: goto L_0x11fa;
                case 75: goto L_0x11e1;
                case 76: goto L_0x11c8;
                case 77: goto L_0x11af;
                case 78: goto L_0x1196;
                case 79: goto L_0x117d;
                case 80: goto L_0x1151;
                case 81: goto L_0x1129;
                case 82: goto L_0x10ff;
                case 83: goto L_0x10d5;
                case 84: goto L_0x10ab;
                case 85: goto L_0x1092;
                case 86: goto L_0x103c;
                case 87: goto L_0x0ff0;
                case 88: goto L_0x0fa4;
                case 89: goto L_0x0var_;
                case 90: goto L_0x0f0c;
                case 91: goto L_0x0ec0;
                case 92: goto L_0x0e08;
                case 93: goto L_0x0dbc;
                case 94: goto L_0x0d66;
                case 95: goto L_0x0d10;
                case 96: goto L_0x0cbe;
                case 97: goto L_0x0CLASSNAME;
                case 98: goto L_0x0CLASSNAME;
                case 99: goto L_0x0bde;
                case 100: goto L_0x0b93;
                case 101: goto L_0x0b48;
                case 102: goto L_0x0afd;
                case 103: goto L_0x0ae0;
                case 104: goto L_0x0add;
                case 105: goto L_0x0add;
                case 106: goto L_0x0add;
                case 107: goto L_0x0add;
                case 108: goto L_0x0add;
                case 109: goto L_0x0add;
                case 110: goto L_0x0add;
                case 111: goto L_0x0add;
                case 112: goto L_0x0add;
                case 113: goto L_0x0add;
                default: goto L_0x0ad8;
            }
        L_0x0ad8:
            r8 = r7
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x04a1 }
            goto L_0x1b9d
        L_0x0add:
            r8 = r7
            goto L_0x1bb3
        L_0x0ae0:
            java.lang.String r1 = "YouHaveNewMessage"
            r3 = 2131628927(0x7f0e137f, float:1.888516E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r3)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "SecretChatName"
            r4 = 2131627847(0x7f0e0var_, float:1.888297E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            r26 = r1
            r42 = r3
            r8 = r7
        L_0x0af7:
            r22 = 0
            r29 = 1
            goto L_0x1bb9
        L_0x0afd:
            r9 = 0
            int r3 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r3 <= 0) goto L_0x0b1b
            java.lang.String r1 = "NotificationActionPinnedGifUser"
            r3 = 2131626670(0x7f0e0aae, float:1.8880583E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0b1b:
            if (r1 == 0) goto L_0x0b35
            java.lang.String r1 = "NotificationActionPinnedGif"
            r3 = 2131626668(0x7f0e0aac, float:1.8880579E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0b35:
            java.lang.String r1 = "NotificationActionPinnedGifChannel"
            r3 = 2131626669(0x7f0e0aad, float:1.888058E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r9[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0b48:
            r9 = 0
            int r3 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r3 <= 0) goto L_0x0b66
            java.lang.String r1 = "NotificationActionPinnedInvoiceUser"
            r3 = 2131626673(0x7f0e0ab1, float:1.8880589E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0b66:
            if (r1 == 0) goto L_0x0b80
            java.lang.String r1 = "NotificationActionPinnedInvoice"
            r3 = 2131626671(0x7f0e0aaf, float:1.8880585E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0b80:
            java.lang.String r1 = "NotificationActionPinnedInvoiceChannel"
            r3 = 2131626672(0x7f0e0ab0, float:1.8880587E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r9[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0b93:
            r9 = 0
            int r3 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r3 <= 0) goto L_0x0bb1
            java.lang.String r1 = "NotificationActionPinnedGameScoreUser"
            r3 = 2131626660(0x7f0e0aa4, float:1.8880562E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0bb1:
            if (r1 == 0) goto L_0x0bcb
            java.lang.String r1 = "NotificationActionPinnedGameScore"
            r3 = 2131626658(0x7f0e0aa2, float:1.8880558E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0bcb:
            java.lang.String r1 = "NotificationActionPinnedGameScoreChannel"
            r3 = 2131626659(0x7f0e0aa3, float:1.888056E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r9[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0bde:
            r9 = 0
            int r3 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r3 <= 0) goto L_0x0bfc
            java.lang.String r1 = "NotificationActionPinnedGameUser"
            r3 = 2131626661(0x7f0e0aa5, float:1.8880564E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0bfc:
            if (r1 == 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGame"
            r3 = 2131626656(0x7f0e0aa0, float:1.8880554E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0CLASSNAME:
            java.lang.String r1 = "NotificationActionPinnedGameChannel"
            r3 = 2131626657(0x7f0e0aa1, float:1.8880556E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r9[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0CLASSNAME:
            r9 = 0
            int r3 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r3 <= 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGeoLiveUser"
            r3 = 2131626666(0x7f0e0aaa, float:1.8880575E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0CLASSNAME:
            if (r1 == 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGeoLive"
            r3 = 2131626664(0x7f0e0aa8, float:1.888057E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0CLASSNAME:
            java.lang.String r1 = "NotificationActionPinnedGeoLiveChannel"
            r3 = 2131626665(0x7f0e0aa9, float:1.8880573E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r9[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0CLASSNAME:
            r9 = 0
            int r3 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r3 <= 0) goto L_0x0c8f
            java.lang.String r1 = "NotificationActionPinnedGeoUser"
            r3 = 2131626667(0x7f0e0aab, float:1.8880577E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0c8f:
            if (r1 == 0) goto L_0x0ca8
            java.lang.String r1 = "NotificationActionPinnedGeo"
            r3 = 2131626662(0x7f0e0aa6, float:1.8880567E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0ca8:
            java.lang.String r1 = "NotificationActionPinnedGeoChannel"
            r3 = 2131626663(0x7f0e0aa7, float:1.8880569E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r9[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
        L_0x0cb9:
            r26 = r1
            r8 = r7
            goto L_0x12e2
        L_0x0cbe:
            r9 = 0
            int r3 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r3 <= 0) goto L_0x0cdb
            java.lang.String r1 = "NotificationActionPinnedPollUser"
            r3 = 2131626685(0x7f0e0abd, float:1.8880613E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0cdb:
            if (r1 == 0) goto L_0x0cf9
            java.lang.String r1 = "NotificationActionPinnedPoll2"
            r3 = 2131626683(0x7f0e0abb, float:1.888061E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 2
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r11 = 1
            r6[r11] = r10     // Catch:{ all -> 0x04a1 }
            r4 = r4[r11]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0cf9:
            java.lang.String r1 = "NotificationActionPinnedPollChannel2"
            r3 = 2131626684(0x7f0e0abc, float:1.8880611E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x0cb9
        L_0x0d10:
            r10 = r7
            r8 = 0
            int r3 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r3 <= 0) goto L_0x0d2f
            java.lang.String r1 = "NotificationActionPinnedQuizUser"
            r3 = 2131626688(0x7f0e0ac0, float:1.888062E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0d2f:
            if (r1 == 0) goto L_0x0d4e
            java.lang.String r1 = "NotificationActionPinnedQuiz2"
            r3 = 2131626686(0x7f0e0abe, float:1.8880615E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 2
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r6[r9] = r8     // Catch:{ all -> 0x04a1 }
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0d4e:
            java.lang.String r1 = "NotificationActionPinnedQuizChannel2"
            r3 = 2131626687(0x7f0e0abf, float:1.8880617E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0d66:
            r10 = r7
            r8 = 0
            int r3 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r3 <= 0) goto L_0x0d85
            java.lang.String r1 = "NotificationActionPinnedContactUser"
            r3 = 2131626652(0x7f0e0a9c, float:1.8880546E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0d85:
            if (r1 == 0) goto L_0x0da4
            java.lang.String r1 = "NotificationActionPinnedContact2"
            r3 = 2131626650(0x7f0e0a9a, float:1.8880542E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 2
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r6[r9] = r8     // Catch:{ all -> 0x04a1 }
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0da4:
            java.lang.String r1 = "NotificationActionPinnedContactChannel2"
            r3 = 2131626651(0x7f0e0a9b, float:1.8880544E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0dbc:
            r10 = r7
            r6 = 0
            int r3 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x0ddb
            java.lang.String r1 = "NotificationActionPinnedVoiceUser"
            r3 = 2131626706(0x7f0e0ad2, float:1.8880656E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0ddb:
            if (r1 == 0) goto L_0x0df5
            java.lang.String r1 = "NotificationActionPinnedVoice"
            r3 = 2131626704(0x7f0e0ad0, float:1.8880652E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0df5:
            java.lang.String r1 = "NotificationActionPinnedVoiceChannel"
            r3 = 2131626705(0x7f0e0ad1, float:1.8880654E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0e08:
            r10 = r7
            r8 = 0
            int r3 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r3 <= 0) goto L_0x0e46
            int r1 = r4.length     // Catch:{ all -> 0x04a1 }
            r3 = 1
            if (r1 <= r3) goto L_0x0e33
            r1 = r4[r3]     // Catch:{ all -> 0x04a1 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x04a1 }
            if (r1 != 0) goto L_0x0e33
            java.lang.String r1 = "NotificationActionPinnedStickerEmojiUser"
            r3 = 2131626696(0x7f0e0ac8, float:1.8880635E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0e33:
            java.lang.String r1 = "NotificationActionPinnedStickerUser"
            r3 = 2131626697(0x7f0e0ac9, float:1.8880638E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0e46:
            if (r1 == 0) goto L_0x0e89
            int r1 = r4.length     // Catch:{ all -> 0x04a1 }
            r3 = 2
            if (r1 <= r3) goto L_0x0e71
            r1 = r4[r3]     // Catch:{ all -> 0x04a1 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x04a1 }
            if (r1 != 0) goto L_0x0e71
            java.lang.String r1 = "NotificationActionPinnedStickerEmoji"
            r3 = 2131626694(0x7f0e0ac6, float:1.8880631E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 2
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r6[r9] = r8     // Catch:{ all -> 0x04a1 }
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0e71:
            java.lang.String r1 = "NotificationActionPinnedSticker"
            r3 = 2131626692(0x7f0e0ac4, float:1.8880627E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0e89:
            int r1 = r4.length     // Catch:{ all -> 0x04a1 }
            r3 = 1
            if (r1 <= r3) goto L_0x0ead
            r1 = r4[r3]     // Catch:{ all -> 0x04a1 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x04a1 }
            if (r1 != 0) goto L_0x0ead
            java.lang.String r1 = "NotificationActionPinnedStickerEmojiChannel"
            r3 = 2131626695(0x7f0e0ac7, float:1.8880633E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0ead:
            java.lang.String r1 = "NotificationActionPinnedStickerChannel"
            r3 = 2131626693(0x7f0e0ac5, float:1.888063E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0ec0:
            r10 = r7
            r6 = 0
            int r3 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x0edf
            java.lang.String r1 = "NotificationActionPinnedFileUser"
            r3 = 2131626655(0x7f0e0a9f, float:1.8880552E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0edf:
            if (r1 == 0) goto L_0x0ef9
            java.lang.String r1 = "NotificationActionPinnedFile"
            r3 = 2131626653(0x7f0e0a9d, float:1.8880548E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0ef9:
            java.lang.String r1 = "NotificationActionPinnedFileChannel"
            r3 = 2131626654(0x7f0e0a9e, float:1.888055E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0f0c:
            r10 = r7
            r6 = 0
            int r3 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x0f2b
            java.lang.String r1 = "NotificationActionPinnedRoundUser"
            r3 = 2131626691(0x7f0e0ac3, float:1.8880625E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0f2b:
            if (r1 == 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedRound"
            r3 = 2131626689(0x7f0e0ac1, float:1.8880621E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0var_:
            java.lang.String r1 = "NotificationActionPinnedRoundChannel"
            r3 = 2131626690(0x7f0e0ac2, float:1.8880623E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0var_:
            r10 = r7
            r6 = 0
            int r3 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedVideoUser"
            r3 = 2131626703(0x7f0e0acf, float:1.888065E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0var_:
            if (r1 == 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedVideo"
            r3 = 2131626701(0x7f0e0acd, float:1.8880646E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0var_:
            java.lang.String r1 = "NotificationActionPinnedVideoChannel"
            r3 = 2131626702(0x7f0e0ace, float:1.8880648E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0fa4:
            r10 = r7
            r6 = 0
            int r3 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x0fc3
            java.lang.String r1 = "NotificationActionPinnedPhotoUser"
            r3 = 2131626682(0x7f0e0aba, float:1.8880607E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0fc3:
            if (r1 == 0) goto L_0x0fdd
            java.lang.String r1 = "NotificationActionPinnedPhoto"
            r3 = 2131626680(0x7f0e0ab8, float:1.8880603E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0fdd:
            java.lang.String r1 = "NotificationActionPinnedPhotoChannel"
            r3 = 2131626681(0x7f0e0ab9, float:1.8880605E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x0ff0:
            r10 = r7
            r6 = 0
            int r3 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x100f
            java.lang.String r1 = "NotificationActionPinnedNoTextUser"
            r3 = 2131626679(0x7f0e0ab7, float:1.88806E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x100f:
            if (r1 == 0) goto L_0x1029
            java.lang.String r1 = "NotificationActionPinnedNoText"
            r3 = 2131626677(0x7f0e0ab5, float:1.8880597E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x1029:
            java.lang.String r1 = "NotificationActionPinnedNoTextChannel"
            r3 = 2131626678(0x7f0e0ab6, float:1.8880599E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x103c:
            r10 = r7
            r8 = 0
            int r3 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r3 <= 0) goto L_0x105b
            java.lang.String r1 = "NotificationActionPinnedTextUser"
            r3 = 2131626700(0x7f0e0acc, float:1.8880644E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x105b:
            if (r1 == 0) goto L_0x107a
            java.lang.String r1 = "NotificationActionPinnedText"
            r3 = 2131626698(0x7f0e0aca, float:1.888064E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x107a:
            java.lang.String r1 = "NotificationActionPinnedTextChannel"
            r3 = 2131626699(0x7f0e0acb, float:1.8880642E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x1092:
            r10 = r7
            java.lang.String r1 = "NotificationGroupAlbum"
            r3 = 2131626715(0x7f0e0adb, float:1.8880674E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x15a2
        L_0x10ab:
            r10 = r7
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r7 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r1[r6] = r7     // Catch:{ all -> 0x04a1 }
            r6 = 1
            r7 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r1[r6] = r7     // Catch:{ all -> 0x04a1 }
            java.lang.String r6 = "Files"
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x04a1 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a1 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r6, r4)     // Catch:{ all -> 0x04a1 }
            r1[r7] = r4     // Catch:{ all -> 0x04a1 }
            r4 = 2131626718(0x7f0e0ade, float:1.888068E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r4, r1)     // Catch:{ all -> 0x04a1 }
            goto L_0x15a2
        L_0x10d5:
            r10 = r7
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r7 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r1[r6] = r7     // Catch:{ all -> 0x04a1 }
            r6 = 1
            r7 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r1[r6] = r7     // Catch:{ all -> 0x04a1 }
            java.lang.String r6 = "MusicFiles"
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x04a1 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a1 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r6, r4)     // Catch:{ all -> 0x04a1 }
            r1[r7] = r4     // Catch:{ all -> 0x04a1 }
            r4 = 2131626718(0x7f0e0ade, float:1.888068E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r4, r1)     // Catch:{ all -> 0x04a1 }
            goto L_0x15a2
        L_0x10ff:
            r10 = r7
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r7 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r1[r6] = r7     // Catch:{ all -> 0x04a1 }
            r6 = 1
            r7 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r1[r6] = r7     // Catch:{ all -> 0x04a1 }
            java.lang.String r6 = "Videos"
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x04a1 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a1 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r6, r4)     // Catch:{ all -> 0x04a1 }
            r1[r7] = r4     // Catch:{ all -> 0x04a1 }
            r4 = 2131626718(0x7f0e0ade, float:1.888068E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r4, r1)     // Catch:{ all -> 0x04a1 }
            goto L_0x15a2
        L_0x1129:
            r10 = r7
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r7 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r1[r6] = r7     // Catch:{ all -> 0x04a1 }
            r6 = 1
            r7 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r1[r6] = r7     // Catch:{ all -> 0x04a1 }
            r6 = 2
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x04a1 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a1 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r9, r4)     // Catch:{ all -> 0x04a1 }
            r1[r6] = r4     // Catch:{ all -> 0x04a1 }
            r4 = 2131626718(0x7f0e0ade, float:1.888068E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r4, r1)     // Catch:{ all -> 0x04a1 }
            goto L_0x15a2
        L_0x1151:
            r10 = r7
            java.lang.String r1 = "NotificationGroupForwardedFew"
            r3 = 2131626719(0x7f0e0adf, float:1.8880682E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x04a1 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a1 }
            r8 = r26
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r8, r4)     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x15a2
        L_0x117d:
            r10 = r7
            java.lang.String r1 = "UserAcceptedToGroupPushWithGroup"
            r3 = 2131628466(0x7f0e11b2, float:1.8884225E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x1196:
            r10 = r7
            java.lang.String r1 = "NotificationGroupAddSelfMega"
            r3 = 2131626714(0x7f0e0ada, float:1.8880672E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x11af:
            r10 = r7
            java.lang.String r1 = "NotificationGroupAddSelf"
            r3 = 2131626713(0x7f0e0ad9, float:1.888067E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x11c8:
            r10 = r7
            java.lang.String r1 = "NotificationGroupLeftMember"
            r3 = 2131626724(0x7f0e0ae4, float:1.8880692E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x11e1:
            r10 = r7
            java.lang.String r1 = "NotificationGroupKickYou"
            r3 = 2131626723(0x7f0e0ae3, float:1.888069E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x11fa:
            r10 = r7
            java.lang.String r1 = "NotificationGroupKickMember"
            r3 = 2131626722(0x7f0e0ae2, float:1.8880688E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x1213:
            r10 = r7
            java.lang.String r1 = "NotificationGroupInvitedYouToCall"
            r3 = 2131626721(0x7f0e0ae1, float:1.8880686E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x122c:
            r10 = r7
            java.lang.String r1 = "NotificationGroupEndedCall"
            r3 = 2131626717(0x7f0e0add, float:1.8880678E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x1245:
            r10 = r7
            java.lang.String r1 = "NotificationGroupInvitedToCall"
            r3 = 2131626720(0x7f0e0ae0, float:1.8880684E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x1263:
            r10 = r7
            java.lang.String r1 = "NotificationGroupCreatedCall"
            r3 = 2131626716(0x7f0e0adc, float:1.8880676E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x127b:
            r10 = r7
            java.lang.String r1 = "NotificationGroupAddMember"
            r3 = 2131626712(0x7f0e0ad8, float:1.8880668E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x1298:
            r10 = r7
            java.lang.String r1 = "NotificationEditedGroupPhoto"
            r3 = 2131626710(0x7f0e0ad6, float:1.8880664E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x12b0:
            r10 = r7
            java.lang.String r1 = "NotificationEditedGroupName"
            r3 = 2131626709(0x7f0e0ad5, float:1.8880662E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x12c8:
            r10 = r7
            java.lang.String r1 = "NotificationInvitedToGroup"
            r3 = 2131626729(0x7f0e0ae9, float:1.8880702E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
        L_0x12df:
            r26 = r1
            r8 = r10
        L_0x12e2:
            r22 = 0
            goto L_0x1bb7
        L_0x12e6:
            r10 = r7
            java.lang.String r1 = "NotificationMessageGroupInvoice"
            r3 = 2131626746(0x7f0e0afa, float:1.8880737E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "PaymentInvoice"
            r4 = 2131627149(0x7f0e0c8d, float:1.8881554E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x156a
        L_0x130d:
            r10 = r7
            java.lang.String r1 = "NotificationMessageGroupGameScored"
            r3 = 2131626744(0x7f0e0af8, float:1.8880733E38)
            r6 = 4
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r8 = 0
            r9 = r4[r8]     // Catch:{ all -> 0x04a1 }
            r6[r8] = r9     // Catch:{ all -> 0x04a1 }
            r8 = 1
            r9 = r4[r8]     // Catch:{ all -> 0x04a1 }
            r6[r8] = r9     // Catch:{ all -> 0x04a1 }
            r8 = 2
            r9 = r4[r8]     // Catch:{ all -> 0x04a1 }
            r6[r8] = r9     // Catch:{ all -> 0x04a1 }
            r7 = 3
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x12df
        L_0x132f:
            r10 = r7
            java.lang.String r1 = "NotificationMessageGroupGame"
            r3 = 2131626743(0x7f0e0af7, float:1.888073E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachGame"
            r4 = 2131624412(0x7f0e01dc, float:1.8876003E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x156a
        L_0x1356:
            r10 = r7
            java.lang.String r1 = "NotificationMessageGroupGif"
            r3 = 2131626745(0x7f0e0af9, float:1.8880735E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachGif"
            r4 = 2131624413(0x7f0e01dd, float:1.8876005E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x156a
        L_0x1378:
            r10 = r7
            java.lang.String r1 = "NotificationMessageGroupLiveLocation"
            r3 = 2131626747(0x7f0e0afb, float:1.8880739E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachLiveLocation"
            r4 = 2131624418(0x7f0e01e2, float:1.8876015E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x156a
        L_0x139a:
            r10 = r7
            java.lang.String r1 = "NotificationMessageGroupMap"
            r3 = 2131626748(0x7f0e0afc, float:1.888074E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachLocation"
            r4 = 2131624422(0x7f0e01e6, float:1.8876023E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x156a
        L_0x13bc:
            r10 = r7
            java.lang.String r1 = "NotificationMessageGroupPoll2"
            r3 = 2131626752(0x7f0e0b00, float:1.888075E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "Poll"
            r4 = 2131627327(0x7f0e0d3f, float:1.8881915E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x156a
        L_0x13e3:
            r10 = r7
            java.lang.String r1 = "NotificationMessageGroupQuiz2"
            r3 = 2131626753(0x7f0e0b01, float:1.8880751E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "PollQuiz"
            r4 = 2131627334(0x7f0e0d46, float:1.888193E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x156a
        L_0x140a:
            r10 = r7
            java.lang.String r1 = "NotificationMessageGroupContact2"
            r3 = 2131626741(0x7f0e0af5, float:1.8880727E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachContact"
            r4 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x156a
        L_0x1431:
            r10 = r7
            java.lang.String r1 = "NotificationMessageGroupAudio"
            r3 = 2131626740(0x7f0e0af4, float:1.8880725E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachAudio"
            r4 = 2131624406(0x7f0e01d6, float:1.887599E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x156a
        L_0x1453:
            r10 = r7
            int r1 = r4.length     // Catch:{ all -> 0x04a1 }
            r3 = 2
            if (r1 <= r3) goto L_0x1495
            r1 = r4[r3]     // Catch:{ all -> 0x04a1 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x04a1 }
            if (r1 != 0) goto L_0x1495
            java.lang.String r1 = "NotificationMessageGroupStickerEmoji"
            r3 = 2131626756(0x7f0e0b04, float:1.8880757E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 2
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x04a1 }
            r3.<init>()     // Catch:{ all -> 0x04a1 }
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r3.append(r4)     // Catch:{ all -> 0x04a1 }
            r3.append(r15)     // Catch:{ all -> 0x04a1 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r14, r13)     // Catch:{ all -> 0x04a1 }
            r3.append(r4)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x04a1 }
            goto L_0x156a
        L_0x1495:
            java.lang.String r1 = "NotificationMessageGroupSticker"
            r3 = 2131626755(0x7f0e0b03, float:1.8880755E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x04a1 }
            r3.<init>()     // Catch:{ all -> 0x04a1 }
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r3.append(r4)     // Catch:{ all -> 0x04a1 }
            r3.append(r15)     // Catch:{ all -> 0x04a1 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r14, r13)     // Catch:{ all -> 0x04a1 }
            r3.append(r4)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x04a1 }
            goto L_0x156a
        L_0x14c5:
            r10 = r7
            java.lang.String r1 = "NotificationMessageGroupDocument"
            r3 = 2131626742(0x7f0e0af6, float:1.8880729E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachDocument"
            r4 = 2131624411(0x7f0e01db, float:1.8876E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x156a
        L_0x14e7:
            r10 = r7
            java.lang.String r1 = "NotificationMessageGroupRound"
            r3 = 2131626754(0x7f0e0b02, float:1.8880753E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachRound"
            r4 = 2131624434(0x7f0e01f2, float:1.8876048E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x156a
        L_0x1508:
            r10 = r7
            java.lang.String r1 = "NotificationMessageGroupVideo"
            r3 = 2131626758(0x7f0e0b06, float:1.8880761E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachVideo"
            r4 = 2131624438(0x7f0e01f6, float:1.8876056E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x156a
        L_0x1529:
            r10 = r7
            java.lang.String r1 = "NotificationMessageGroupPhoto"
            r3 = 2131626751(0x7f0e0aff, float:1.8880747E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachPhoto"
            r4 = 2131624432(0x7f0e01f0, float:1.8876044E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x156a
        L_0x154a:
            r10 = r7
            java.lang.String r1 = "NotificationMessageGroupNoText"
            r3 = 2131626750(0x7f0e0afe, float:1.8880745E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "Message"
            r4 = 2131626397(0x7f0e099d, float:1.888003E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
        L_0x156a:
            r26 = r1
            r22 = r3
            r8 = r10
            goto L_0x1bb7
        L_0x1571:
            r10 = r7
            java.lang.String r1 = "NotificationMessageGroupText"
            r3 = 2131626757(0x7f0e0b05, float:1.888076E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            r7 = 2
            r8 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r8     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            r3 = r4[r7]     // Catch:{ all -> 0x04a1 }
            goto L_0x156a
        L_0x1590:
            r10 = r7
            java.lang.String r1 = "ChannelMessageAlbum"
            r3 = 2131624801(0x7f0e0361, float:1.8876792E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
        L_0x15a2:
            r26 = r1
            r8 = r10
            goto L_0x0af7
        L_0x15a7:
            r6 = r7
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a1 }
            r3 = 0
            r8 = r4[r3]     // Catch:{ all -> 0x04a1 }
            r1[r3] = r8     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "Files"
            r8 = 1
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x04a1 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r4)     // Catch:{ all -> 0x04a1 }
            r1[r8] = r3     // Catch:{ all -> 0x04a1 }
            r3 = 2131624805(0x7f0e0365, float:1.88768E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r10, r3, r1)     // Catch:{ all -> 0x04a1 }
            goto L_0x1852
        L_0x15cc:
            r6 = r7
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a1 }
            r3 = 0
            r8 = r4[r3]     // Catch:{ all -> 0x04a1 }
            r1[r3] = r8     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "MusicFiles"
            r8 = 1
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x04a1 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r4)     // Catch:{ all -> 0x04a1 }
            r1[r8] = r3     // Catch:{ all -> 0x04a1 }
            r3 = 2131624805(0x7f0e0365, float:1.88768E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r10, r3, r1)     // Catch:{ all -> 0x04a1 }
            goto L_0x1852
        L_0x15f1:
            r6 = r7
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a1 }
            r3 = 0
            r8 = r4[r3]     // Catch:{ all -> 0x04a1 }
            r1[r3] = r8     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "Videos"
            r8 = 1
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x04a1 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r4)     // Catch:{ all -> 0x04a1 }
            r1[r8] = r3     // Catch:{ all -> 0x04a1 }
            r3 = 2131624805(0x7f0e0365, float:1.88768E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r10, r3, r1)     // Catch:{ all -> 0x04a1 }
            goto L_0x1852
        L_0x1616:
            r6 = r7
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a1 }
            r3 = 0
            r8 = r4[r3]     // Catch:{ all -> 0x04a1 }
            r1[r3] = r8     // Catch:{ all -> 0x04a1 }
            r3 = 1
            r4 = r4[r3]     // Catch:{ all -> 0x04a1 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x04a1 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a1 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r9, r4)     // Catch:{ all -> 0x04a1 }
            r1[r3] = r4     // Catch:{ all -> 0x04a1 }
            r3 = 2131624805(0x7f0e0365, float:1.88768E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r10, r3, r1)     // Catch:{ all -> 0x04a1 }
            goto L_0x1852
        L_0x1639:
            r6 = r7
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a1 }
            r3 = 0
            r8 = r4[r3]     // Catch:{ all -> 0x04a1 }
            r1[r3] = r8     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "ForwardedMessageCount"
            r8 = 1
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x04a1 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r4)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = r3.toLowerCase()     // Catch:{ all -> 0x04a1 }
            r1[r8] = r3     // Catch:{ all -> 0x04a1 }
            r3 = 2131624805(0x7f0e0365, float:1.88768E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r10, r3, r1)     // Catch:{ all -> 0x04a1 }
            goto L_0x1852
        L_0x1662:
            r6 = r7
            java.lang.String r1 = "NotificationMessageGame"
            r3 = 2131626737(0x7f0e0af1, float:1.8880719E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x04a1 }
            r8 = 0
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            r9[r8] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachGame"
            r4 = 2131624412(0x7f0e01dc, float:1.8876003E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1839
        L_0x167f:
            r6 = r7
            java.lang.String r1 = "ChannelMessageGIF"
            r3 = 2131624806(0x7f0e0366, float:1.8876802E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x04a1 }
            r8 = 0
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            r9[r8] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachGif"
            r4 = 2131624413(0x7f0e01dd, float:1.8876005E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1839
        L_0x169c:
            r6 = r7
            java.lang.String r1 = "ChannelMessageLiveLocation"
            r3 = 2131624807(0x7f0e0367, float:1.8876804E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x04a1 }
            r8 = 0
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            r9[r8] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachLiveLocation"
            r4 = 2131624418(0x7f0e01e2, float:1.8876015E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1839
        L_0x16b9:
            r6 = r7
            java.lang.String r1 = "ChannelMessageMap"
            r3 = 2131624808(0x7f0e0368, float:1.8876806E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x04a1 }
            r8 = 0
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            r9[r8] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachLocation"
            r4 = 2131624422(0x7f0e01e6, float:1.8876023E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1839
        L_0x16d6:
            r6 = r7
            java.lang.String r1 = "ChannelMessagePoll2"
            r3 = 2131624812(0x7f0e036c, float:1.8876814E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r8[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r8[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r8)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "Poll"
            r4 = 2131627327(0x7f0e0d3f, float:1.8881915E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1839
        L_0x16f8:
            r6 = r7
            java.lang.String r1 = "ChannelMessageQuiz2"
            r3 = 2131624813(0x7f0e036d, float:1.8876816E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r8[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r8[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r8)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "QuizPoll"
            r4 = 2131627543(0x7f0e0e17, float:1.8882353E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1839
        L_0x171a:
            r6 = r7
            java.lang.String r1 = "ChannelMessageContact2"
            r3 = 2131624803(0x7f0e0363, float:1.8876796E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r8[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r8[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r8)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachContact"
            r4 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1839
        L_0x173c:
            r6 = r7
            java.lang.String r1 = "ChannelMessageAudio"
            r3 = 2131624802(0x7f0e0362, float:1.8876794E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x04a1 }
            r8 = 0
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            r9[r8] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachAudio"
            r4 = 2131624406(0x7f0e01d6, float:1.887599E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1839
        L_0x1759:
            r6 = r7
            int r1 = r4.length     // Catch:{ all -> 0x04a1 }
            r3 = 1
            if (r1 <= r3) goto L_0x1796
            r1 = r4[r3]     // Catch:{ all -> 0x04a1 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x04a1 }
            if (r1 != 0) goto L_0x1796
            java.lang.String r1 = "ChannelMessageStickerEmoji"
            r3 = 2131624816(0x7f0e0370, float:1.8876822E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r8[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r8[r9] = r10     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r8)     // Catch:{ all -> 0x04a1 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x04a1 }
            r3.<init>()     // Catch:{ all -> 0x04a1 }
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r3.append(r4)     // Catch:{ all -> 0x04a1 }
            r3.append(r15)     // Catch:{ all -> 0x04a1 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r14, r13)     // Catch:{ all -> 0x04a1 }
            r3.append(r4)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x04a1 }
            goto L_0x1839
        L_0x1796:
            java.lang.String r1 = "ChannelMessageSticker"
            r3 = 2131624815(0x7f0e036f, float:1.887682E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x04a1 }
            r8 = 0
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            r9[r8] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r13)     // Catch:{ all -> 0x04a1 }
            goto L_0x1839
        L_0x17ad:
            r6 = r7
            java.lang.String r1 = "ChannelMessageDocument"
            r3 = 2131624804(0x7f0e0364, float:1.8876798E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x04a1 }
            r8 = 0
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            r9[r8] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachDocument"
            r4 = 2131624411(0x7f0e01db, float:1.8876E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1839
        L_0x17ca:
            r6 = r7
            java.lang.String r1 = "ChannelMessageRound"
            r3 = 2131624814(0x7f0e036e, float:1.8876818E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x04a1 }
            r8 = 0
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            r9[r8] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachRound"
            r4 = 2131624434(0x7f0e01f2, float:1.8876048E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1839
        L_0x17e6:
            r6 = r7
            java.lang.String r1 = "ChannelMessageVideo"
            r3 = 2131624817(0x7f0e0371, float:1.8876824E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x04a1 }
            r8 = 0
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            r9[r8] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachVideo"
            r4 = 2131624438(0x7f0e01f6, float:1.8876056E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1839
        L_0x1802:
            r6 = r7
            java.lang.String r1 = "ChannelMessagePhoto"
            r3 = 2131624811(0x7f0e036b, float:1.8876812E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x04a1 }
            r8 = 0
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            r9[r8] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachPhoto"
            r4 = 2131624432(0x7f0e01f0, float:1.8876044E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1839
        L_0x181e:
            r6 = r7
            java.lang.String r1 = "ChannelMessageNoText"
            r3 = 2131624810(0x7f0e036a, float:1.887681E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x04a1 }
            r8 = 0
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            r9[r8] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "Message"
            r4 = 2131626397(0x7f0e099d, float:1.888003E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
        L_0x1839:
            r26 = r1
            r22 = r3
            r8 = r6
            goto L_0x1bb7
        L_0x1840:
            r6 = r7
            java.lang.String r1 = "NotificationMessageAlbum"
            r3 = 2131626731(0x7f0e0aeb, float:1.8880706E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x04a1 }
            r8 = 0
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            r9[r8] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
        L_0x1852:
            r26 = r1
            r8 = r6
            goto L_0x0af7
        L_0x1857:
            r6 = r7
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a1 }
            r3 = 0
            r8 = r4[r3]     // Catch:{ all -> 0x04a1 }
            r1[r3] = r8     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "Files"
            r8 = 1
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x04a1 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r4)     // Catch:{ all -> 0x04a1 }
            r1[r8] = r3     // Catch:{ all -> 0x04a1 }
            r3 = 2131626735(0x7f0e0aef, float:1.8880715E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r11, r3, r1)     // Catch:{ all -> 0x04a1 }
            goto L_0x1852
        L_0x187b:
            r6 = r7
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a1 }
            r3 = 0
            r8 = r4[r3]     // Catch:{ all -> 0x04a1 }
            r1[r3] = r8     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "MusicFiles"
            r8 = 1
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x04a1 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r4)     // Catch:{ all -> 0x04a1 }
            r1[r8] = r3     // Catch:{ all -> 0x04a1 }
            r3 = 2131626735(0x7f0e0aef, float:1.8880715E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r11, r3, r1)     // Catch:{ all -> 0x04a1 }
            goto L_0x1852
        L_0x189f:
            r6 = r7
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a1 }
            r3 = 0
            r8 = r4[r3]     // Catch:{ all -> 0x04a1 }
            r1[r3] = r8     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "Videos"
            r8 = 1
            r4 = r4[r8]     // Catch:{ all -> 0x04a1 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x04a1 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r4)     // Catch:{ all -> 0x04a1 }
            r1[r8] = r3     // Catch:{ all -> 0x04a1 }
            r3 = 2131626735(0x7f0e0aef, float:1.8880715E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r11, r3, r1)     // Catch:{ all -> 0x04a1 }
            goto L_0x1852
        L_0x18c3:
            r6 = r7
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a1 }
            r3 = 0
            r8 = r4[r3]     // Catch:{ all -> 0x04a1 }
            r1[r3] = r8     // Catch:{ all -> 0x04a1 }
            r3 = 1
            r4 = r4[r3]     // Catch:{ all -> 0x04a1 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x04a1 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a1 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r9, r4)     // Catch:{ all -> 0x04a1 }
            r1[r3] = r4     // Catch:{ all -> 0x04a1 }
            r3 = 2131626735(0x7f0e0aef, float:1.8880715E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r11, r3, r1)     // Catch:{ all -> 0x04a1 }
            goto L_0x1852
        L_0x18e6:
            r6 = r7
            r8 = r26
            java.lang.String r1 = "NotificationMessageForwardFew"
            r3 = 2131626736(0x7f0e0af0, float:1.8880717E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch:{ all -> 0x04a1 }
            r10 = 0
            r11 = r4[r10]     // Catch:{ all -> 0x04a1 }
            r9[r10] = r11     // Catch:{ all -> 0x04a1 }
            r10 = 1
            r4 = r4[r10]     // Catch:{ all -> 0x04a1 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x04a1 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a1 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r8, r4)     // Catch:{ all -> 0x04a1 }
            r9[r10] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a1 }
            goto L_0x1852
        L_0x190d:
            r6 = r7
            java.lang.String r1 = "NotificationMessageInvoice"
            r3 = 2131626759(0x7f0e0b07, float:1.8880763E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x04a1 }
            r9 = 0
            r10 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r8[r9] = r10     // Catch:{ all -> 0x04a1 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a1 }
            r8[r9] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r8)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "PaymentInvoice"
            r4 = 2131627149(0x7f0e0c8d, float:1.8881554E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1839
        L_0x192f:
            r8 = r7
            java.lang.String r1 = "NotificationMessageGameScored"
            r3 = 2131626738(0x7f0e0af2, float:1.888072E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r10 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r10     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r10 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r10     // Catch:{ all -> 0x04a1 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            goto L_0x1aea
        L_0x194d:
            r8 = r7
            java.lang.String r1 = "NotificationMessageGame"
            r3 = 2131626737(0x7f0e0af1, float:1.8880719E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r10 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r10     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachGame"
            r4 = 2131624412(0x7f0e01dc, float:1.8876003E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1b7e
        L_0x196f:
            r8 = r7
            java.lang.String r1 = "NotificationMessageGif"
            r3 = 2131626739(0x7f0e0af3, float:1.8880723E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachGif"
            r4 = 2131624413(0x7f0e01dd, float:1.8876005E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1b7e
        L_0x198c:
            r8 = r7
            java.lang.String r1 = "NotificationMessageLiveLocation"
            r3 = 2131626760(0x7f0e0b08, float:1.8880765E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachLiveLocation"
            r4 = 2131624418(0x7f0e01e2, float:1.8876015E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1b7e
        L_0x19a9:
            r8 = r7
            java.lang.String r1 = "NotificationMessageMap"
            r3 = 2131626761(0x7f0e0b09, float:1.8880767E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachLocation"
            r4 = 2131624422(0x7f0e01e6, float:1.8876023E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1b7e
        L_0x19c6:
            r8 = r7
            java.lang.String r1 = "NotificationMessagePoll2"
            r3 = 2131626765(0x7f0e0b0d, float:1.8880775E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r10 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r10     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "Poll"
            r4 = 2131627327(0x7f0e0d3f, float:1.8881915E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1b7e
        L_0x19e8:
            r8 = r7
            java.lang.String r1 = "NotificationMessageQuiz2"
            r3 = 2131626766(0x7f0e0b0e, float:1.8880777E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r10 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r10     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "QuizPoll"
            r4 = 2131627543(0x7f0e0e17, float:1.8882353E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1b7e
        L_0x1a0a:
            r8 = r7
            java.lang.String r1 = "NotificationMessageContact2"
            r3 = 2131626733(0x7f0e0aed, float:1.888071E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r10 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r10     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachContact"
            r4 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1b7e
        L_0x1a2c:
            r8 = r7
            java.lang.String r1 = "NotificationMessageAudio"
            r3 = 2131626732(0x7f0e0aec, float:1.8880709E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachAudio"
            r4 = 2131624406(0x7f0e01d6, float:1.887599E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1b7e
        L_0x1a49:
            r8 = r7
            int r1 = r4.length     // Catch:{ all -> 0x04a1 }
            r3 = 1
            if (r1 <= r3) goto L_0x1a86
            r1 = r4[r3]     // Catch:{ all -> 0x04a1 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x04a1 }
            if (r1 != 0) goto L_0x1a86
            java.lang.String r1 = "NotificationMessageStickerEmoji"
            r3 = 2131626773(0x7f0e0b15, float:1.8880792E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r10 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r10     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r10 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r10     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x04a1 }
            r3.<init>()     // Catch:{ all -> 0x04a1 }
            r4 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r3.append(r4)     // Catch:{ all -> 0x04a1 }
            r3.append(r15)     // Catch:{ all -> 0x04a1 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r14, r13)     // Catch:{ all -> 0x04a1 }
            r3.append(r4)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x04a1 }
            goto L_0x1b7e
        L_0x1a86:
            java.lang.String r1 = "NotificationMessageSticker"
            r3 = 2131626772(0x7f0e0b14, float:1.888079E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r13)     // Catch:{ all -> 0x04a1 }
            goto L_0x1b7e
        L_0x1a9d:
            r8 = r7
            java.lang.String r1 = "NotificationMessageDocument"
            r3 = 2131626734(0x7f0e0aee, float:1.8880713E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachDocument"
            r4 = 2131624411(0x7f0e01db, float:1.8876E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1b7e
        L_0x1aba:
            r8 = r7
            java.lang.String r1 = "NotificationMessageRound"
            r3 = 2131626767(0x7f0e0b0f, float:1.888078E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachRound"
            r4 = 2131624434(0x7f0e01f2, float:1.8876048E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1b7e
        L_0x1ad7:
            r8 = r7
            java.lang.String r1 = "ActionTakeScreenshoot"
            r3 = 2131624174(0x7f0e00ee, float:1.887552E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r3)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "un1"
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = r1.replace(r3, r4)     // Catch:{ all -> 0x04a1 }
        L_0x1aea:
            r22 = 0
            r29 = 0
            r26 = r1
            goto L_0x1bb9
        L_0x1af2:
            r8 = r7
            java.lang.String r1 = "NotificationMessageSDVideo"
            r3 = 2131626769(0x7f0e0b11, float:1.8880784E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachDestructingVideo"
            r4 = 2131624410(0x7f0e01da, float:1.8875999E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1b7e
        L_0x1b0f:
            r8 = r7
            java.lang.String r1 = "NotificationMessageVideo"
            r3 = 2131626775(0x7f0e0b17, float:1.8880796E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachVideo"
            r4 = 2131624438(0x7f0e01f6, float:1.8876056E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1b7e
        L_0x1b2b:
            r8 = r7
            java.lang.String r1 = "NotificationMessageSDPhoto"
            r3 = 2131626768(0x7f0e0b10, float:1.8880782E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachDestructingPhoto"
            r4 = 2131624409(0x7f0e01d9, float:1.8875997E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1b7e
        L_0x1b47:
            r8 = r7
            java.lang.String r1 = "NotificationMessagePhoto"
            r3 = 2131626764(0x7f0e0b0c, float:1.8880773E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "AttachPhoto"
            r4 = 2131624432(0x7f0e01f0, float:1.8876044E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
            goto L_0x1b7e
        L_0x1b63:
            r8 = r7
            java.lang.String r1 = "NotificationMessageNoText"
            r3 = 2131626763(0x7f0e0b0b, float:1.8880771E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a1 }
            r7[r6] = r4     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "Message"
            r4 = 2131626397(0x7f0e099d, float:1.888003E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a1 }
        L_0x1b7e:
            r26 = r1
            r22 = r3
            goto L_0x1bb7
        L_0x1b83:
            r8 = r7
            java.lang.String r1 = "NotificationMessageText"
            r3 = 2131626774(0x7f0e0b16, float:1.8880794E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a1 }
            r7 = 0
            r10 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r10     // Catch:{ all -> 0x04a1 }
            r7 = 1
            r10 = r4[r7]     // Catch:{ all -> 0x04a1 }
            r6[r7] = r10     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a1 }
            r3 = r4[r7]     // Catch:{ all -> 0x04a1 }
            goto L_0x1b7e
        L_0x1b9d:
            if (r1 == 0) goto L_0x1bb3
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x04a1 }
            r1.<init>()     // Catch:{ all -> 0x04a1 }
            java.lang.String r3 = "unhandled loc_key = "
            r1.append(r3)     // Catch:{ all -> 0x04a1 }
            r1.append(r2)     // Catch:{ all -> 0x04a1 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x04a1 }
            org.telegram.messenger.FileLog.w(r1)     // Catch:{ all -> 0x04a1 }
        L_0x1bb3:
            r22 = 0
            r26 = 0
        L_0x1bb7:
            r29 = 0
        L_0x1bb9:
            r1 = r51
            goto L_0x1bd3
        L_0x1bbc:
            r44 = r3
            r8 = r7
            r46 = r10
            r45 = r11
            r47 = r13
            r17 = r15
            r1 = r51
        L_0x1bc9:
            java.lang.String r3 = r1.getReactedText(r2, r4)     // Catch:{ all -> 0x1cf4 }
            r26 = r3
            r22 = 0
            r29 = 0
        L_0x1bd3:
            if (r26 == 0) goto L_0x1ce0
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x1cf4 }
            r3.<init>()     // Catch:{ all -> 0x1cf4 }
            r3.id = r5     // Catch:{ all -> 0x1cf4 }
            r4 = r27
            r3.random_id = r4     // Catch:{ all -> 0x1cf4 }
            if (r22 == 0) goto L_0x1be5
            r4 = r22
            goto L_0x1be7
        L_0x1be5:
            r4 = r26
        L_0x1be7:
            r3.message = r4     // Catch:{ all -> 0x1cf4 }
            r4 = 1000(0x3e8, double:4.94E-321)
            long r4 = r53 / r4
            int r5 = (int) r4     // Catch:{ all -> 0x1cf4 }
            r3.date = r5     // Catch:{ all -> 0x1cf4 }
            if (r41 == 0) goto L_0x1bf9
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r4 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x1cf4 }
            r4.<init>()     // Catch:{ all -> 0x1cf4 }
            r3.action = r4     // Catch:{ all -> 0x1cf4 }
        L_0x1bf9:
            if (r12 == 0) goto L_0x1CLASSNAME
            int r4 = r3.flags     // Catch:{ all -> 0x1cf4 }
            r5 = -2147483648(0xfffffffvar_, float:-0.0)
            r4 = r4 | r5
            r3.flags = r4     // Catch:{ all -> 0x1cf4 }
        L_0x1CLASSNAME:
            r3.dialog_id = r8     // Catch:{ all -> 0x1cf4 }
            r4 = 0
            int r6 = (r30 > r4 ? 1 : (r30 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_peerChannel r4 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x1cf4 }
            r4.<init>()     // Catch:{ all -> 0x1cf4 }
            r3.peer_id = r4     // Catch:{ all -> 0x1cf4 }
            r13 = r30
            r4.channel_id = r13     // Catch:{ all -> 0x1cf4 }
            r7 = r24
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r4 = 0
            int r6 = (r24 > r4 ? 1 : (r24 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x1c2a
            org.telegram.tgnet.TLRPC$TL_peerChat r4 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x1cf4 }
            r4.<init>()     // Catch:{ all -> 0x1cf4 }
            r3.peer_id = r4     // Catch:{ all -> 0x1cf4 }
            r7 = r24
            r4.chat_id = r7     // Catch:{ all -> 0x1cf4 }
            goto L_0x1CLASSNAME
        L_0x1c2a:
            r7 = r24
            org.telegram.tgnet.TLRPC$TL_peerUser r4 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1cf4 }
            r4.<init>()     // Catch:{ all -> 0x1cf4 }
            r3.peer_id = r4     // Catch:{ all -> 0x1cf4 }
            r5 = r35
            r4.user_id = r5     // Catch:{ all -> 0x1cf4 }
        L_0x1CLASSNAME:
            int r4 = r3.flags     // Catch:{ all -> 0x1cf4 }
            r4 = r4 | 256(0x100, float:3.59E-43)
            r3.flags = r4     // Catch:{ all -> 0x1cf4 }
            r4 = 0
            int r6 = (r37 > r4 ? 1 : (r37 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x1c4d
            org.telegram.tgnet.TLRPC$TL_peerChat r4 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x1cf4 }
            r4.<init>()     // Catch:{ all -> 0x1cf4 }
            r3.from_id = r4     // Catch:{ all -> 0x1cf4 }
            r4.chat_id = r7     // Catch:{ all -> 0x1cf4 }
            goto L_0x1CLASSNAME
        L_0x1c4d:
            r4 = 0
            int r6 = (r47 > r4 ? 1 : (r47 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x1c5f
            org.telegram.tgnet.TLRPC$TL_peerChannel r4 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x1cf4 }
            r4.<init>()     // Catch:{ all -> 0x1cf4 }
            r3.from_id = r4     // Catch:{ all -> 0x1cf4 }
            r5 = r47
            r4.channel_id = r5     // Catch:{ all -> 0x1cf4 }
            goto L_0x1CLASSNAME
        L_0x1c5f:
            r4 = 0
            int r6 = (r39 > r4 ? 1 : (r39 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_peerUser r4 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1cf4 }
            r4.<init>()     // Catch:{ all -> 0x1cf4 }
            r3.from_id = r4     // Catch:{ all -> 0x1cf4 }
            r5 = r39
            r4.user_id = r5     // Catch:{ all -> 0x1cf4 }
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            org.telegram.tgnet.TLRPC$Peer r4 = r3.peer_id     // Catch:{ all -> 0x1cf4 }
            r3.from_id = r4     // Catch:{ all -> 0x1cf4 }
        L_0x1CLASSNAME:
            if (r16 != 0) goto L_0x1c7c
            if (r41 == 0) goto L_0x1c7a
            goto L_0x1c7c
        L_0x1c7a:
            r4 = 0
            goto L_0x1c7d
        L_0x1c7c:
            r4 = 1
        L_0x1c7d:
            r3.mentioned = r4     // Catch:{ all -> 0x1cf4 }
            r4 = r32
            r3.silent = r4     // Catch:{ all -> 0x1cf4 }
            r15 = r23
            r3.from_scheduled = r15     // Catch:{ all -> 0x1cf4 }
            org.telegram.messenger.MessageObject r4 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x1cf4 }
            r23 = r4
            r24 = r34
            r25 = r3
            r27 = r42
            r28 = r46
            r30 = r45
            r31 = r12
            r32 = r43
            r23.<init>(r24, r25, r26, r27, r28, r29, r30, r31, r32)     // Catch:{ all -> 0x1cf4 }
            r3 = r44
            boolean r3 = r2.startsWith(r3)     // Catch:{ all -> 0x1cf4 }
            if (r3 != 0) goto L_0x1caf
            r3 = r17
            boolean r3 = r2.startsWith(r3)     // Catch:{ all -> 0x1cf4 }
            if (r3 == 0) goto L_0x1cad
            goto L_0x1caf
        L_0x1cad:
            r3 = 0
            goto L_0x1cb0
        L_0x1caf:
            r3 = 1
        L_0x1cb0:
            r4.isReactionPush = r3     // Catch:{ all -> 0x1cf4 }
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ all -> 0x1cf4 }
            r3.<init>()     // Catch:{ all -> 0x1cf4 }
            r3.add(r4)     // Catch:{ all -> 0x1cf4 }
            org.telegram.messenger.NotificationsController r4 = org.telegram.messenger.NotificationsController.getInstance(r34)     // Catch:{ all -> 0x1cf4 }
            java.util.concurrent.CountDownLatch r5 = r1.countDownLatch     // Catch:{ all -> 0x1cf4 }
            r6 = 1
            r4.processNewMessages(r3, r6, r6, r5)     // Catch:{ all -> 0x1cf4 }
            r9 = 0
            goto L_0x1ce1
        L_0x1cc6:
            r0 = move-exception
            r1 = r51
            goto L_0x1cf5
        L_0x1cca:
            r0 = move-exception
            r1 = r51
            r34 = r6
            goto L_0x1cf5
        L_0x1cd0:
            r1 = r51
            goto L_0x1cde
        L_0x1cd3:
            r0 = move-exception
            r1 = r51
            goto L_0x1cd8
        L_0x1cd7:
            r0 = move-exception
        L_0x1cd8:
            r34 = r12
            goto L_0x1dbe
        L_0x1cdc:
            r33 = r6
        L_0x1cde:
            r34 = r12
        L_0x1ce0:
            r9 = 1
        L_0x1ce1:
            if (r9 == 0) goto L_0x1ce8
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch     // Catch:{ all -> 0x1cf4 }
            r3.countDown()     // Catch:{ all -> 0x1cf4 }
        L_0x1ce8:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r34)     // Catch:{ all -> 0x1cf4 }
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r34)     // Catch:{ all -> 0x1cf4 }
            r3.resumeNetworkMaybe()     // Catch:{ all -> 0x1cf4 }
            goto L_0x1e17
        L_0x1cf4:
            r0 = move-exception
        L_0x1cf5:
            r5 = r2
            r6 = r33
            r12 = r34
            goto L_0x1dc7
        L_0x1cfc:
            r0 = move-exception
            r33 = r6
            goto L_0x1d04
        L_0x1d00:
            r0 = move-exception
            r33 = r6
            r2 = r8
        L_0x1d04:
            r34 = r12
            goto L_0x1dc6
        L_0x1d08:
            r33 = r6
            r2 = r8
            r34 = r12
            org.telegram.messenger.DispatchQueue r3 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1d1f }
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1 r4 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1     // Catch:{ all -> 0x1d1f }
            r12 = r34
            r4.<init>(r12)     // Catch:{ all -> 0x1dbd }
            r3.postRunnable(r4)     // Catch:{ all -> 0x1dbd }
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch     // Catch:{ all -> 0x1dbd }
            r3.countDown()     // Catch:{ all -> 0x1dbd }
            return
        L_0x1d1f:
            r0 = move-exception
            r12 = r34
            goto L_0x1dbe
        L_0x1d24:
            r33 = r6
            r2 = r8
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0 r3 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0     // Catch:{ all -> 0x1dbd }
            r3.<init>(r12)     // Catch:{ all -> 0x1dbd }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)     // Catch:{ all -> 0x1dbd }
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch     // Catch:{ all -> 0x1dbd }
            r3.countDown()     // Catch:{ all -> 0x1dbd }
            return
        L_0x1d35:
            r33 = r6
            r2 = r8
            r5 = r16
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r3 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x1dbd }
            r3.<init>()     // Catch:{ all -> 0x1dbd }
            r4 = 0
            r3.popup = r4     // Catch:{ all -> 0x1dbd }
            r4 = 2
            r3.flags = r4     // Catch:{ all -> 0x1dbd }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r6 = r53 / r6
            int r4 = (int) r6     // Catch:{ all -> 0x1dbd }
            r3.inbox_date = r4     // Catch:{ all -> 0x1dbd }
            java.lang.String r4 = "message"
            java.lang.String r4 = r5.getString(r4)     // Catch:{ all -> 0x1dbd }
            r3.message = r4     // Catch:{ all -> 0x1dbd }
            java.lang.String r4 = "announcement"
            r3.type = r4     // Catch:{ all -> 0x1dbd }
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ all -> 0x1dbd }
            r4.<init>()     // Catch:{ all -> 0x1dbd }
            r3.media = r4     // Catch:{ all -> 0x1dbd }
            org.telegram.tgnet.TLRPC$TL_updates r4 = new org.telegram.tgnet.TLRPC$TL_updates     // Catch:{ all -> 0x1dbd }
            r4.<init>()     // Catch:{ all -> 0x1dbd }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r5 = r4.updates     // Catch:{ all -> 0x1dbd }
            r5.add(r3)     // Catch:{ all -> 0x1dbd }
            org.telegram.messenger.DispatchQueue r3 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1dbd }
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3 r5 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3     // Catch:{ all -> 0x1dbd }
            r5.<init>(r12, r4)     // Catch:{ all -> 0x1dbd }
            r3.postRunnable(r5)     // Catch:{ all -> 0x1dbd }
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r12)     // Catch:{ all -> 0x1dbd }
            r3.resumeNetworkMaybe()     // Catch:{ all -> 0x1dbd }
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch     // Catch:{ all -> 0x1dbd }
            r3.countDown()     // Catch:{ all -> 0x1dbd }
            return
        L_0x1d80:
            r33 = r6
            r2 = r8
            java.lang.String r3 = "dc"
            int r3 = r10.getInt(r3)     // Catch:{ all -> 0x1dbd }
            java.lang.String r4 = "addr"
            java.lang.String r4 = r10.getString(r4)     // Catch:{ all -> 0x1dbd }
            java.lang.String r5 = ":"
            java.lang.String[] r4 = r4.split(r5)     // Catch:{ all -> 0x1dbd }
            int r5 = r4.length     // Catch:{ all -> 0x1dbd }
            r6 = 2
            if (r5 == r6) goto L_0x1d9f
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch     // Catch:{ all -> 0x1dbd }
            r3.countDown()     // Catch:{ all -> 0x1dbd }
            return
        L_0x1d9f:
            r5 = 0
            r5 = r4[r5]     // Catch:{ all -> 0x1dbd }
            r6 = 1
            r4 = r4[r6]     // Catch:{ all -> 0x1dbd }
            int r4 = java.lang.Integer.parseInt(r4)     // Catch:{ all -> 0x1dbd }
            org.telegram.tgnet.ConnectionsManager r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r12)     // Catch:{ all -> 0x1dbd }
            r6.applyDatacenterAddress(r3, r5, r4)     // Catch:{ all -> 0x1dbd }
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r12)     // Catch:{ all -> 0x1dbd }
            r3.resumeNetworkMaybe()     // Catch:{ all -> 0x1dbd }
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch     // Catch:{ all -> 0x1dbd }
            r3.countDown()     // Catch:{ all -> 0x1dbd }
            return
        L_0x1dbd:
            r0 = move-exception
        L_0x1dbe:
            r5 = r2
            r6 = r33
            goto L_0x1dc7
        L_0x1dc2:
            r0 = move-exception
            r33 = r6
        L_0x1dc5:
            r2 = r8
        L_0x1dc6:
            r5 = r2
        L_0x1dc7:
            r3 = -1
            goto L_0x1dd0
        L_0x1dc9:
            r0 = move-exception
            r33 = r6
            r2 = r8
            r5 = r2
            r3 = -1
            r12 = -1
        L_0x1dd0:
            r2 = r0
            goto L_0x1ddf
        L_0x1dd2:
            r0 = move-exception
            r33 = r6
        L_0x1dd5:
            r2 = r0
            r3 = -1
            r5 = 0
            goto L_0x1dde
        L_0x1dd9:
            r0 = move-exception
            r2 = r0
            r3 = -1
            r5 = 0
            r6 = 0
        L_0x1dde:
            r12 = -1
        L_0x1ddf:
            if (r12 == r3) goto L_0x1df1
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r12)
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r12)
            r3.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch
            r3.countDown()
            goto L_0x1df4
        L_0x1df1:
            r51.onDecryptError()
        L_0x1df4:
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x1e14
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "error in loc_key = "
            r3.append(r4)
            r3.append(r5)
            java.lang.String r4 = " json "
            r3.append(r4)
            r3.append(r6)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r3)
        L_0x1e14:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x1e17:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.GcmPushListenerService.lambda$onMessageReceived$3(java.util.Map, long):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onMessageReceived$1(int i) {
        if (UserConfig.getInstance(i).getClientUserId() != 0) {
            UserConfig.getInstance(i).clearConfig();
            MessagesController.getInstance(i).performLogout(0);
        }
    }

    private String getReactedText(String str, Object[] objArr) {
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
                    c = 8;
                    break;
                }
                break;
            case 52294965:
                if (str.equals("REACT_QUIZ")) {
                    c = 9;
                    break;
                }
                break;
            case 52369421:
                if (str.equals("REACT_TEXT")) {
                    c = 10;
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
                    c = 12;
                    break;
                }
                break;
            case 192844842:
                if (str.equals("CHAT_REACT_GEO")) {
                    c = 13;
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
                return LocaleController.formatString("PushChatReactContact", NUM, objArr);
            case 1:
                return LocaleController.formatString("PushReactGeoLocation", NUM, objArr);
            case 2:
                return LocaleController.formatString("PushChatReactNotext", NUM, objArr);
            case 3:
                return LocaleController.formatString("PushReactNoText", NUM, objArr);
            case 4:
                return LocaleController.formatString("PushChatReactInvoice", NUM, objArr);
            case 5:
                return LocaleController.formatString("PushReactContect", NUM, objArr);
            case 6:
                return LocaleController.formatString("PushChatReactSticker", NUM, objArr);
            case 7:
                return LocaleController.formatString("PushReactGame", NUM, objArr);
            case 8:
                return LocaleController.formatString("PushReactPoll", NUM, objArr);
            case 9:
                return LocaleController.formatString("PushReactQuiz", NUM, objArr);
            case 10:
                return LocaleController.formatString("PushReactText", NUM, objArr);
            case 11:
                return LocaleController.formatString("PushReactInvoice", NUM, objArr);
            case 12:
                return LocaleController.formatString("PushChatReactDoc", NUM, objArr);
            case 13:
                return LocaleController.formatString("PushChatReactGeo", NUM, objArr);
            case 14:
                return LocaleController.formatString("PushChatReactGif", NUM, objArr);
            case 15:
                return LocaleController.formatString("PushReactSticker", NUM, objArr);
            case 16:
                return LocaleController.formatString("PushChatReactAudio", NUM, objArr);
            case 17:
                return LocaleController.formatString("PushChatReactPhoto", NUM, objArr);
            case 18:
                return LocaleController.formatString("PushChatReactRound", NUM, objArr);
            case 19:
                return LocaleController.formatString("PushChatReactVideo", NUM, objArr);
            case 20:
                return LocaleController.formatString("PushChatReactGeoLive", NUM, objArr);
            case 21:
                return LocaleController.formatString("PushReactAudio", NUM, objArr);
            case 22:
                return LocaleController.formatString("PushReactPhoto", NUM, objArr);
            case 23:
                return LocaleController.formatString("PushReactRound", NUM, objArr);
            case 24:
                return LocaleController.formatString("PushReactVideo", NUM, objArr);
            case 25:
                return LocaleController.formatString("PushReactDoc", NUM, objArr);
            case 26:
                return LocaleController.formatString("PushReactGeo", NUM, objArr);
            case 27:
                return LocaleController.formatString("PushReactGif", NUM, objArr);
            case 28:
                return LocaleController.formatString("PushChatReactGame", NUM, objArr);
            case 29:
                return LocaleController.formatString("PushChatReactPoll", NUM, objArr);
            case 30:
                return LocaleController.formatString("PushChatReactQuiz", NUM, objArr);
            case 31:
                return LocaleController.formatString("PushChatReactText", NUM, objArr);
            default:
                return null;
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
        AndroidUtilities.runOnUIThread(new GcmPushListenerService$$ExternalSyntheticLambda5(str));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onNewToken$5(String str) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Refreshed token: " + str);
        }
        ApplicationLoader.postInitApplication();
        sendRegistrationToServer(str);
    }

    public static void sendRegistrationToServer(String str) {
        Utilities.stageQueue.postRunnable(new GcmPushListenerService$$ExternalSyntheticLambda4(str));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendRegistrationToServer$9(String str) {
        boolean z;
        ConnectionsManager.setRegId(str, SharedConfig.pushStringStatus);
        if (str != null) {
            if (SharedConfig.pushStringGetTimeStart == 0 || SharedConfig.pushStringGetTimeEnd == 0 || (SharedConfig.pushStatSent && TextUtils.equals(SharedConfig.pushString, str))) {
                z = false;
            } else {
                SharedConfig.pushStatSent = false;
                z = true;
            }
            SharedConfig.pushString = str;
            for (int i = 0; i < 3; i++) {
                UserConfig instance = UserConfig.getInstance(i);
                instance.registeredForPush = false;
                instance.saveConfig(false);
                if (instance.getClientUserId() != 0) {
                    if (z) {
                        TLRPC$TL_help_saveAppLog tLRPC$TL_help_saveAppLog = new TLRPC$TL_help_saveAppLog();
                        TLRPC$TL_inputAppEvent tLRPC$TL_inputAppEvent = new TLRPC$TL_inputAppEvent();
                        tLRPC$TL_inputAppEvent.time = (double) SharedConfig.pushStringGetTimeStart;
                        tLRPC$TL_inputAppEvent.type = "fcm_token_request";
                        tLRPC$TL_inputAppEvent.peer = 0;
                        tLRPC$TL_inputAppEvent.data = new TLRPC$TL_jsonNull();
                        tLRPC$TL_help_saveAppLog.events.add(tLRPC$TL_inputAppEvent);
                        TLRPC$TL_inputAppEvent tLRPC$TL_inputAppEvent2 = new TLRPC$TL_inputAppEvent();
                        long j = SharedConfig.pushStringGetTimeEnd;
                        tLRPC$TL_inputAppEvent2.time = (double) j;
                        tLRPC$TL_inputAppEvent2.type = "fcm_token_response";
                        tLRPC$TL_inputAppEvent2.peer = j - SharedConfig.pushStringGetTimeStart;
                        tLRPC$TL_inputAppEvent2.data = new TLRPC$TL_jsonNull();
                        tLRPC$TL_help_saveAppLog.events.add(tLRPC$TL_inputAppEvent2);
                        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_help_saveAppLog, GcmPushListenerService$$ExternalSyntheticLambda9.INSTANCE);
                        z = false;
                    }
                    AndroidUtilities.runOnUIThread(new GcmPushListenerService$$ExternalSyntheticLambda2(i, str));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendRegistrationToServer$6(TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            SharedConfig.pushStatSent = true;
            SharedConfig.saveConfig();
        }
    }
}
