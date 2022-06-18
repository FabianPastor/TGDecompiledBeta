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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v45, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v47, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v51, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v54, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v79, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v82, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v83, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v88, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v93, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v44, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v98, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v46, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v47, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v48, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v109, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v135, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v139, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v142, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v145, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v148, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v173, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v176, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v44, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v154, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v179, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v45, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v182, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v46, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v158, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v47, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v188, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v162, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v165, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v195, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v48, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v141, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v170, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v143, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v172, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v174, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v149, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v178, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v153, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v155, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v190, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v167, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v169, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v173, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v175, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v177, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v196, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v181, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v78, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v198, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v79, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v200, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v82, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v201, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v205, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v208, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v211, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v228, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v198, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v214, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v201, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v207, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v221, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v212, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v223, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v214, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v226, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v221, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v227, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v228, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v226, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v229, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v228, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v101, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v232, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v272, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v279, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v108, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v286, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v244, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v263, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v269, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v273, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v275, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v252, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v277, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v279, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v283, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v256, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v285, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v257, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v258, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v289, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v291, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v260, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v295, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v262, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v297, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v263, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v264, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v301, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v138, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v303, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v266, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v307, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v141, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v268, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v309, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v269, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v270, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v313, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v315, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v272, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v664, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v665, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v666, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v667, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v670, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v276, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v277, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v398, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v400, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v401, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v402, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX WARNING: type inference failed for: r1v20 */
    /* JADX WARNING: type inference failed for: r1v359 */
    /* JADX WARNING: type inference failed for: r1v361 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:1000:0x1d8e, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1001:0x1d8f, code lost:
        r33 = r6;
        r2 = r8;
        r3 = r10.getInt("dc");
        r4 = r10.getString("addr").split(":");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1002:0x1da6, code lost:
        if (r4.length == 2) goto L_0x1dae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1003:0x1da8, code lost:
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1004:0x1dad, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1005:0x1dae, code lost:
        org.telegram.tgnet.ConnectionsManager.getInstance(r12).applyDatacenterAddress(r3, r4[0], java.lang.Integer.parseInt(r4[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r12).resumeNetworkMaybe();
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1006:0x1dcb, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1007:0x1dcc, code lost:
        r0 = th;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1008:0x1dcd, code lost:
        r5 = r2;
        r6 = r33;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1039:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x01fd, code lost:
        if (r2 == 0) goto L_0x1d8f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x01ff, code lost:
        if (r2 == 1) goto L_0x1d44;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x0201, code lost:
        if (r2 == 2) goto L_0x1d33;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x0203, code lost:
        if (r2 == 3) goto L_0x1d17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:127:0x020b, code lost:
        if (r10.has("channel_id") == false) goto L_0x0219;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:?, code lost:
        r13 = r10.getLong("channel_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x0213, code lost:
        r2 = r8;
        r7 = -r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x0216, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x0219, code lost:
        r2 = r8;
        r7 = 0;
        r13 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x0224, code lost:
        if (r10.has("from_id") == false) goto L_0x0232;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:?, code lost:
        r7 = r10.getLong("from_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x022c, code lost:
        r33 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x022f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x0232, code lost:
        r33 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x023a, code lost:
        if (r10.has("chat_id") == false) goto L_0x0249;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:?, code lost:
        r7 = r10.getLong("chat_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x0242, code lost:
        r4 = r7;
        r7 = -r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x0249, code lost:
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x0251, code lost:
        if (r10.has("encryption_id") == false) goto L_0x025e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:159:?, code lost:
        r7 = org.telegram.messenger.DialogObject.makeEncryptedDialogId((long) r10.getInt("encryption_id"));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:163:0x0264, code lost:
        if (r10.has("schedule") == false) goto L_0x0270;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:167:0x026c, code lost:
        if (r10.getInt("schedule") != 1) goto L_0x0270;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x026e, code lost:
        r15 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:169:0x0270, code lost:
        r15 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x0275, code lost:
        if (r7 != 0) goto L_0x0281;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x027d, code lost:
        if ("ENCRYPTED_MESSAGE".equals(r2) == false) goto L_0x0281;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x027f, code lost:
        r7 = org.telegram.messenger.NotificationsController.globalSecretChatId;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x0285, code lost:
        if (r7 == 0) goto L_0x1ceb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:181:0x028f, code lost:
        if ("READ_HISTORY".equals(r2) == false) goto L_0x0305;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:?, code lost:
        r3 = r10.getInt("max_id");
        r10 = new java.util.ArrayList();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:0x029e, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x02ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x02a0, code lost:
        org.telegram.messenger.FileLog.d("GCM received read notification max_id = " + r3 + " for dialogId = " + r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x02be, code lost:
        if (r13 == 0) goto L_0x02cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x02c0, code lost:
        r4 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox();
        r4.channel_id = r13;
        r4.max_id = r3;
        r10.add(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x02cd, code lost:
        r7 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox();
        r8 = r33;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x02d8, code lost:
        if (r8 == 0) goto L_0x02e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x02da, code lost:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r7.peer = r4;
        r4.user_id = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x02e4, code lost:
        r8 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r7.peer = r8;
        r8.chat_id = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x02ed, code lost:
        r7.max_id = r3;
        r10.add(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x02f2, code lost:
        org.telegram.messenger.MessagesController.getInstance(r12).processUpdateArray(r10, (java.util.ArrayList<org.telegram.tgnet.TLRPC$User>) null, (java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat>) null, false, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x0305, code lost:
        r35 = r33;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x030d, code lost:
        r33 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x0311, code lost:
        if ("MESSAGE_DELETED".equals(r2) == false) goto L_0x037d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:?, code lost:
        r3 = r10.getString("messages").split(",");
        r4 = new androidx.collection.LongSparseArray();
        r5 = new java.util.ArrayList();
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x0329, code lost:
        if (r6 >= r3.length) goto L_0x0337;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x032b, code lost:
        r5.add(org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r3[r6]));
        r6 = r6 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x0337, code lost:
        r4.put(-r13, r5);
        org.telegram.messenger.NotificationsController.getInstance(r12).removeDeletedMessagesFromNotifications(r4);
        org.telegram.messenger.MessagesController.getInstance(r12).deleteMessagesByPush(r7, r5, r13);
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x0351, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1ced;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x0353, code lost:
        org.telegram.messenger.FileLog.d("GCM received " + r2 + " for dialogId = " + r7 + " mids = " + android.text.TextUtils.join(",", r5));
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x037d, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:?, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x0381, code lost:
        if (android.text.TextUtils.isEmpty(r2) != false) goto L_0x1ced;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x0389, code lost:
        if (r10.has("msg_id") == false) goto L_0x0394;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:?, code lost:
        r11 = r10.getInt("msg_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x0391, code lost:
        r25 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:218:0x0394, code lost:
        r25 = r15;
        r11 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x039d, code lost:
        if (r10.has("random_id") == false) goto L_0x03b4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x03ad, code lost:
        r49 = r4;
        r4 = org.telegram.messenger.Utilities.parseLong(r10.getString("random_id")).longValue();
        r26 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x03b4, code lost:
        r26 = r4;
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x03b8, code lost:
        if (r11 == 0) goto L_0x03fe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:?, code lost:
        r1 = org.telegram.messenger.MessagesController.getInstance(r12).dialogs_read_inbox_max.get(java.lang.Long.valueOf(r7));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x03ca, code lost:
        if (r1 != null) goto L_0x03e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x03cc, code lost:
        r1 = java.lang.Integer.valueOf(org.telegram.messenger.MessagesStorage.getInstance(r12).getDialogReadMax(false, r7));
        r28 = "messages";
        org.telegram.messenger.MessagesController.getInstance(r12).dialogs_read_inbox_max.put(java.lang.Long.valueOf(r7), r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x03e9, code lost:
        r28 = "messages";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:0x03ef, code lost:
        if (r11 <= r1.intValue()) goto L_0x03f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x03f1, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x03f3, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x03f5, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x03f6, code lost:
        r3 = -1;
        r1 = r51;
        r5 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x03fa, code lost:
        r6 = r33;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x03fe, code lost:
        r28 = "messages";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x0404, code lost:
        if (r4 == 0) goto L_0x03f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x040e, code lost:
        if (org.telegram.messenger.MessagesStorage.getInstance(r12).checkMessageByRandomId(r4) != false) goto L_0x03f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x0411, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x0417, code lost:
        if (r2.startsWith("REACT_") != false) goto L_0x041f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:?, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x041d, code lost:
        if (r2.startsWith("CHAT_REACT_") == false) goto L_0x0420;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x041f, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x0420, code lost:
        if (r1 == false) goto L_0x1cdf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x0422, code lost:
        r1 = "chat_from_id";
        r29 = r4;
        r31 = r11;
        r6 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x042b, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:?, code lost:
        r11 = r10.optLong(r1, 0);
        r37 = r13;
        r13 = r10.optLong("chat_from_broadcast_id", 0);
        r39 = r10.optLong("chat_from_group_id", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x043f, code lost:
        if (r11 != 0) goto L_0x0448;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x0443, code lost:
        if (r39 == 0) goto L_0x0446;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x0446, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x0448, code lost:
        r1 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x044f, code lost:
        if (r10.has("mention") == false) goto L_0x0462;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x0457, code lost:
        if (r10.getInt("mention") == 0) goto L_0x0462;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x0459, code lost:
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x045b, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x045c, code lost:
        r3 = -1;
        r1 = r51;
        r5 = r2;
        r12 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x0462, code lost:
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0469, code lost:
        if (r10.has("silent") == false) goto L_0x0477;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0471, code lost:
        if (r10.getInt("silent") == 0) goto L_0x0477;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x0473, code lost:
        r34 = r6;
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0477, code lost:
        r34 = r6;
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x047a, code lost:
        r32 = r5;
        r5 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x0480, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x0484, code lost:
        if (r5.has("loc_args") == false) goto L_0x04ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:?, code lost:
        r5 = r5.getJSONArray("loc_args");
        r6 = r5.length();
        r16 = r4;
        r4 = new java.lang.String[r6];
        r41 = r11;
        r11 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x0497, code lost:
        if (r11 >= r6) goto L_0x04b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x0499, code lost:
        r4[r11] = r5.getString(r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x049f, code lost:
        r11 = r11 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x04a2, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x04a3, code lost:
        r3 = -1;
        r1 = r51;
        r5 = r2;
        r6 = r33;
        r12 = r34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x04ad, code lost:
        r16 = r4;
        r41 = r11;
        r4 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:?, code lost:
        r6 = r4[0];
        r5 = r10.has("edit_date");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x04c1, code lost:
        if (r2.startsWith("CHAT_") == false) goto L_0x04f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x04c7, code lost:
        if (org.telegram.messenger.UserObject.isReplyUser(r7) == false) goto L_0x04e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x04c9, code lost:
        r6 = r6 + " @ " + r4[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x04e5, code lost:
        if (r37 == 0) goto L_0x04e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x04e7, code lost:
        r10 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x04e9, code lost:
        r10 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x04ed, code lost:
        r11 = r6;
        r43 = r10;
        r6 = r4[1];
        r10 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x04f9, code lost:
        if (r2.startsWith("PINNED_") == false) goto L_0x050a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x04ff, code lost:
        if (r37 == 0) goto L_0x0503;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x0501, code lost:
        r10 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x0503, code lost:
        r10 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x0504, code lost:
        r43 = r10;
        r10 = true;
        r11 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x0508, code lost:
        r12 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x0510, code lost:
        if (r2.startsWith("CHANNEL_") == false) goto L_0x0516;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x0512, code lost:
        r10 = false;
        r11 = null;
        r12 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x0516, code lost:
        r10 = false;
        r11 = null;
        r12 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x0519, code lost:
        r43 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x051d, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x0548;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x051f, code lost:
        r44 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:?, code lost:
        r6 = new java.lang.StringBuilder();
        r45 = r5;
        r6.append("GCM received message notification ");
        r6.append(r2);
        r6.append(" for dialogId = ");
        r6.append(r7);
        r6.append(" mid = ");
        r5 = r31;
        r6.append(r5);
        org.telegram.messenger.FileLog.d(r6.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:0x0548, code lost:
        r45 = r5;
        r44 = r6;
        r5 = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:333:0x0552, code lost:
        if (r2.startsWith("REACT_") != false) goto L_0x1bd0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x0558, code lost:
        if (r2.startsWith("CHAT_REACT_") == false) goto L_0x0566;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x055a, code lost:
        r1 = r51;
        r46 = "REACT_";
        r48 = r11;
        r47 = r12;
        r17 = "CHAT_REACT_";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:339:0x056a, code lost:
        switch(r2.hashCode()) {
            case -2100047043: goto L_0x0ab7;
            case -2091498420: goto L_0x0aac;
            case -2053872415: goto L_0x0aa1;
            case -2039746363: goto L_0x0a96;
            case -2023218804: goto L_0x0a8b;
            case -1979538588: goto L_0x0a80;
            case -1979536003: goto L_0x0a75;
            case -1979535888: goto L_0x0a6a;
            case -1969004705: goto L_0x0a5f;
            case -1946699248: goto L_0x0a53;
            case -1717283471: goto L_0x0a47;
            case -1646640058: goto L_0x0a3b;
            case -1528047021: goto L_0x0a2f;
            case -1507149394: goto L_0x0a24;
            case -1493579426: goto L_0x0a18;
            case -1482481933: goto L_0x0a0c;
            case -1480102982: goto L_0x0a01;
            case -1478041834: goto L_0x09f5;
            case -1474543101: goto L_0x09ea;
            case -1465695932: goto L_0x09de;
            case -1374906292: goto L_0x09d2;
            case -1372940586: goto L_0x09c6;
            case -1264245338: goto L_0x09ba;
            case -1236154001: goto L_0x09ae;
            case -1236086700: goto L_0x09a2;
            case -1236077786: goto L_0x0996;
            case -1235796237: goto L_0x098a;
            case -1235760759: goto L_0x097e;
            case -1235686303: goto L_0x0973;
            case -1198046100: goto L_0x0968;
            case -1124254527: goto L_0x095c;
            case -1085137927: goto L_0x0950;
            case -1084856378: goto L_0x0944;
            case -1084820900: goto L_0x0938;
            case -1084746444: goto L_0x092c;
            case -819729482: goto L_0x0920;
            case -772141857: goto L_0x0914;
            case -638310039: goto L_0x0908;
            case -590403924: goto L_0x08fc;
            case -589196239: goto L_0x08f0;
            case -589193654: goto L_0x08e4;
            case -589193539: goto L_0x08d8;
            case -440169325: goto L_0x08cc;
            case -412748110: goto L_0x08c0;
            case -228518075: goto L_0x08b4;
            case -213586509: goto L_0x08a8;
            case -115582002: goto L_0x089c;
            case -112621464: goto L_0x0890;
            case -108522133: goto L_0x0884;
            case -107572034: goto L_0x0878;
            case -40534265: goto L_0x086c;
            case 52369421: goto L_0x0860;
            case 65254746: goto L_0x0854;
            case 141040782: goto L_0x0848;
            case 202550149: goto L_0x083c;
            case 309993049: goto L_0x0830;
            case 309995634: goto L_0x0824;
            case 309995749: goto L_0x0818;
            case 320532812: goto L_0x080c;
            case 328933854: goto L_0x0800;
            case 331340546: goto L_0x07f4;
            case 342406591: goto L_0x07e8;
            case 344816990: goto L_0x07dc;
            case 346878138: goto L_0x07d0;
            case 350376871: goto L_0x07c4;
            case 608430149: goto L_0x07b8;
            case 615714517: goto L_0x07ad;
            case 715508879: goto L_0x07a1;
            case 728985323: goto L_0x0795;
            case 731046471: goto L_0x0789;
            case 734545204: goto L_0x077d;
            case 802032552: goto L_0x0771;
            case 991498806: goto L_0x0765;
            case 1007364121: goto L_0x0759;
            case 1019850010: goto L_0x074d;
            case 1019917311: goto L_0x0741;
            case 1019926225: goto L_0x0735;
            case 1020207774: goto L_0x0729;
            case 1020243252: goto L_0x071d;
            case 1020317708: goto L_0x0711;
            case 1060282259: goto L_0x0705;
            case 1060349560: goto L_0x06f9;
            case 1060358474: goto L_0x06ed;
            case 1060640023: goto L_0x06e1;
            case 1060675501: goto L_0x06d5;
            case 1060749957: goto L_0x06ca;
            case 1073049781: goto L_0x06be;
            case 1078101399: goto L_0x06b2;
            case 1110103437: goto L_0x06a6;
            case 1160762272: goto L_0x069a;
            case 1172918249: goto L_0x068e;
            case 1234591620: goto L_0x0682;
            case 1281128640: goto L_0x0676;
            case 1281131225: goto L_0x066a;
            case 1281131340: goto L_0x065e;
            case 1310789062: goto L_0x0653;
            case 1333118583: goto L_0x0647;
            case 1361447897: goto L_0x063b;
            case 1498266155: goto L_0x062f;
            case 1533804208: goto L_0x0623;
            case 1540131626: goto L_0x0617;
            case 1547988151: goto L_0x060b;
            case 1561464595: goto L_0x05ff;
            case 1563525743: goto L_0x05f3;
            case 1567024476: goto L_0x05e7;
            case 1810705077: goto L_0x05db;
            case 1815177512: goto L_0x05cf;
            case 1954774321: goto L_0x05c3;
            case 1963241394: goto L_0x05b7;
            case 2014789757: goto L_0x05ab;
            case 2022049433: goto L_0x059f;
            case 2034984710: goto L_0x0593;
            case 2048733346: goto L_0x0587;
            case 2099392181: goto L_0x057b;
            case 2140162142: goto L_0x056f;
            default: goto L_0x056d;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x0575, code lost:
        if (r2.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x0577, code lost:
        r6 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x0581, code lost:
        if (r2.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x0583, code lost:
        r6 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x058d, code lost:
        if (r2.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x058f, code lost:
        r6 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:351:0x0599, code lost:
        if (r2.equals("CHANNEL_MESSAGE_PLAYLIST") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x059b, code lost:
        r6 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x05a5, code lost:
        if (r2.equals("PINNED_CONTACT") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x05a7, code lost:
        r6 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x05b1, code lost:
        if (r2.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x05b3, code lost:
        r6 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x05bd, code lost:
        if (r2.equals("LOCKED_MESSAGE") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x05bf, code lost:
        r6 = 'm';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x05c9, code lost:
        if (r2.equals("CHAT_MESSAGE_PLAYLIST") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x05cb, code lost:
        r6 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x05d5, code lost:
        if (r2.equals("CHANNEL_MESSAGES") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x05d7, code lost:
        r6 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x05e1, code lost:
        if (r2.equals("MESSAGE_INVOICE") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x05e3, code lost:
        r6 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x05ed, code lost:
        if (r2.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x05ef, code lost:
        r6 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:375:0x05f9, code lost:
        if (r2.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x05fb, code lost:
        r6 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x0605, code lost:
        if (r2.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x0607, code lost:
        r6 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:381:0x0611, code lost:
        if (r2.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x0613, code lost:
        r6 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x061d, code lost:
        if (r2.equals("MESSAGE_PLAYLIST") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x061f, code lost:
        r6 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x0629, code lost:
        if (r2.equals("MESSAGE_VIDEOS") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x062b, code lost:
        r6 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x0635, code lost:
        if (r2.equals("PHONE_CALL_MISSED") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x0637, code lost:
        r6 = 'r';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x0641, code lost:
        if (r2.equals("MESSAGE_PHOTOS") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x0643, code lost:
        r6 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x064d, code lost:
        if (r2.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x064f, code lost:
        r6 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x0659, code lost:
        if (r2.equals("MESSAGE_NOTEXT") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:400:0x065b, code lost:
        r6 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x0664, code lost:
        if (r2.equals("MESSAGE_GIF") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0666, code lost:
        r6 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x0670, code lost:
        if (r2.equals("MESSAGE_GEO") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x0672, code lost:
        r6 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x067c, code lost:
        if (r2.equals("MESSAGE_DOC") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x067e, code lost:
        r6 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x0688, code lost:
        if (r2.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x068a, code lost:
        r6 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x0694, code lost:
        if (r2.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x0696, code lost:
        r6 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x06a0, code lost:
        if (r2.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x06a2, code lost:
        r6 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x06ac, code lost:
        if (r2.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x06ae, code lost:
        r6 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x06b8, code lost:
        if (r2.equals("CHAT_TITLE_EDITED") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x06ba, code lost:
        r6 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:426:0x06c4, code lost:
        if (r2.equals("PINNED_NOTEXT") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x06c6, code lost:
        r6 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x06d0, code lost:
        if (r2.equals("MESSAGE_TEXT") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x06d2, code lost:
        r6 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:432:0x06db, code lost:
        if (r2.equals("MESSAGE_QUIZ") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x06dd, code lost:
        r6 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x06e7, code lost:
        if (r2.equals("MESSAGE_POLL") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x06e9, code lost:
        r6 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x06f3, code lost:
        if (r2.equals("MESSAGE_GAME") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x06f5, code lost:
        r6 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x06ff, code lost:
        if (r2.equals("MESSAGE_FWDS") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x0701, code lost:
        r6 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:444:0x070b, code lost:
        if (r2.equals("MESSAGE_DOCS") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x070d, code lost:
        r6 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x0717, code lost:
        if (r2.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x0719, code lost:
        r6 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x0723, code lost:
        if (r2.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x0725, code lost:
        r6 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x072f, code lost:
        if (r2.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x0731, code lost:
        r6 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:456:0x073b, code lost:
        if (r2.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x073d, code lost:
        r6 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x0747, code lost:
        if (r2.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:460:0x0749, code lost:
        r6 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x0753, code lost:
        if (r2.equals("CHAT_MESSAGE_DOCS") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x0755, code lost:
        r6 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x075f, code lost:
        if (r2.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x0761, code lost:
        r6 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x076b, code lost:
        if (r2.equals("PINNED_GEOLIVE") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x076d, code lost:
        r6 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x0777, code lost:
        if (r2.equals("MESSAGE_CONTACT") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:472:0x0779, code lost:
        r6 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x0783, code lost:
        if (r2.equals("PINNED_VIDEO") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0785, code lost:
        r6 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x078f, code lost:
        if (r2.equals("PINNED_ROUND") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x0791, code lost:
        r6 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:480:0x079b, code lost:
        if (r2.equals("PINNED_PHOTO") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x079d, code lost:
        r6 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x07a7, code lost:
        if (r2.equals("PINNED_AUDIO") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x07a9, code lost:
        r6 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:486:0x07b3, code lost:
        if (r2.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x07b5, code lost:
        r6 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x07be, code lost:
        if (r2.equals("CHAT_VOICECHAT_INVITE_YOU") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x07c0, code lost:
        r6 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x07ca, code lost:
        if (r2.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x07cc, code lost:
        r6 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x07d6, code lost:
        if (r2.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x07d8, code lost:
        r6 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x07e2, code lost:
        if (r2.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x07e4, code lost:
        r6 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x07ee, code lost:
        if (r2.equals("CHAT_VOICECHAT_END") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x07f0, code lost:
        r6 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x07fa, code lost:
        if (r2.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x07fc, code lost:
        r6 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x0806, code lost:
        if (r2.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x0808, code lost:
        r6 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x0812, code lost:
        if (r2.equals("MESSAGES") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x0814, code lost:
        r6 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x081e, code lost:
        if (r2.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x0820, code lost:
        r6 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x082a, code lost:
        if (r2.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x082c, code lost:
        r6 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:519:0x0836, code lost:
        if (r2.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0838, code lost:
        r6 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x0842, code lost:
        if (r2.equals("CHAT_VOICECHAT_INVITE") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x0844, code lost:
        r6 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x084e, code lost:
        if (r2.equals("CHAT_LEFT") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x0850, code lost:
        r6 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x085a, code lost:
        if (r2.equals("CHAT_ADD_YOU") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x085c, code lost:
        r6 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0866, code lost:
        if (r2.equals("REACT_TEXT") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x0868, code lost:
        r6 = 'i';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x0872, code lost:
        if (r2.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x0874, code lost:
        r6 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x087e, code lost:
        if (r2.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:538:0x0880, code lost:
        r6 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:540:0x088a, code lost:
        if (r2.equals("AUTH_REGION") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x088c, code lost:
        r6 = 'l';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x0896, code lost:
        if (r2.equals("CONTACT_JOINED") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x0898, code lost:
        r6 = 'j';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x08a2, code lost:
        if (r2.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x08a4, code lost:
        r6 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x08ae, code lost:
        if (r2.equals("ENCRYPTION_REQUEST") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x08b0, code lost:
        r6 = 'n';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:552:0x08ba, code lost:
        if (r2.equals("MESSAGE_GEOLIVE") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x08bc, code lost:
        r6 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x08c6, code lost:
        if (r2.equals("CHAT_DELETE_YOU") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x08c8, code lost:
        r6 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x08d2, code lost:
        if (r2.equals("AUTH_UNKNOWN") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x08d4, code lost:
        r6 = 'k';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x08de, code lost:
        if (r2.equals("PINNED_GIF") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x08e0, code lost:
        r6 = 'g';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x08ea, code lost:
        if (r2.equals("PINNED_GEO") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x08ec, code lost:
        r6 = 'b';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x08f6, code lost:
        if (r2.equals("PINNED_DOC") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x08f8, code lost:
        r6 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x0902, code lost:
        if (r2.equals("PINNED_GAME_SCORE") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x0904, code lost:
        r6 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x090e, code lost:
        if (r2.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x0910, code lost:
        r6 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:576:0x091a, code lost:
        if (r2.equals("PHONE_CALL_REQUEST") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x091c, code lost:
        r6 = 'p';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x0926, code lost:
        if (r2.equals("PINNED_STICKER") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x0928, code lost:
        r6 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:582:0x0932, code lost:
        if (r2.equals("PINNED_TEXT") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x0934, code lost:
        r6 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x093e, code lost:
        if (r2.equals("PINNED_QUIZ") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x0940, code lost:
        r6 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:588:0x094a, code lost:
        if (r2.equals("PINNED_POLL") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x094c, code lost:
        r6 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x0956, code lost:
        if (r2.equals("PINNED_GAME") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x0958, code lost:
        r6 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x0962, code lost:
        if (r2.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x0964, code lost:
        r6 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x096e, code lost:
        if (r2.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x0970, code lost:
        r6 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:600:0x0979, code lost:
        if (r2.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x097b, code lost:
        r6 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0984, code lost:
        if (r2.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x0986, code lost:
        r6 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x0990, code lost:
        if (r2.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x0992, code lost:
        r6 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x099c, code lost:
        if (r2.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x099e, code lost:
        r6 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:612:0x09a8, code lost:
        if (r2.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x09aa, code lost:
        r6 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x09b4, code lost:
        if (r2.equals("CHANNEL_MESSAGE_DOCS") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x09b6, code lost:
        r6 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:618:0x09c0, code lost:
        if (r2.equals("PINNED_INVOICE") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x09c2, code lost:
        r6 = 'f';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x09cc, code lost:
        if (r2.equals("CHAT_RETURNED") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x09ce, code lost:
        r6 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x09d8, code lost:
        if (r2.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:625:0x09da, code lost:
        r6 = 'h';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:627:0x09e4, code lost:
        if (r2.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x09e6, code lost:
        r6 = 'o';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x09f0, code lost:
        if (r2.equals("MESSAGE_VIDEO") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x09f2, code lost:
        r6 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x09fb, code lost:
        if (r2.equals("MESSAGE_ROUND") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x09fd, code lost:
        r6 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x0a07, code lost:
        if (r2.equals("MESSAGE_PHOTO") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x0a09, code lost:
        r6 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x0a12, code lost:
        if (r2.equals("MESSAGE_MUTED") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x0a14, code lost:
        r6 = 'q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0a1e, code lost:
        if (r2.equals("MESSAGE_AUDIO") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0a20, code lost:
        r6 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x0a2a, code lost:
        if (r2.equals("MESSAGE_RECURRING_PAY") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x0a2c, code lost:
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x0a35, code lost:
        if (r2.equals("CHAT_MESSAGES") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0a37, code lost:
        r6 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x0a41, code lost:
        if (r2.equals("CHAT_VOICECHAT_START") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0a43, code lost:
        r6 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0a4d, code lost:
        if (r2.equals("CHAT_REQ_JOINED") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0a4f, code lost:
        r6 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0a59, code lost:
        if (r2.equals("CHAT_JOINED") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0a5b, code lost:
        r6 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0a65, code lost:
        if (r2.equals("CHAT_ADD_MEMBER") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x0a67, code lost:
        r6 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x0a70, code lost:
        if (r2.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0a72, code lost:
        r6 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0a7b, code lost:
        if (r2.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x0a7d, code lost:
        r6 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0a86, code lost:
        if (r2.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x0a88, code lost:
        r6 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0a91, code lost:
        if (r2.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:673:0x0a93, code lost:
        r6 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0a9c, code lost:
        if (r2.equals("MESSAGE_STICKER") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0a9e, code lost:
        r6 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0aa7, code lost:
        if (r2.equals("CHAT_CREATED") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:679:0x0aa9, code lost:
        r6 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0ab2, code lost:
        if (r2.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x0ab4, code lost:
        r6 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0abd, code lost:
        if (r2.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:685:0x0abf, code lost:
        r6 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0ac2, code lost:
        r6 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x0ac3, code lost:
        r17 = "CHAT_REACT_";
        r46 = "REACT_";
        r47 = r12;
        r48 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x0ad5, code lost:
        switch(r6) {
            case 0: goto L_0x1b93;
            case 1: goto L_0x1b7a;
            case 2: goto L_0x1b7a;
            case 3: goto L_0x1b5b;
            case 4: goto L_0x1b40;
            case 5: goto L_0x1b25;
            case 6: goto L_0x1b0a;
            case 7: goto L_0x1aef;
            case 8: goto L_0x1ad8;
            case 9: goto L_0x1abc;
            case 10: goto L_0x1aa0;
            case 11: goto L_0x1a47;
            case 12: goto L_0x1a2b;
            case 13: goto L_0x1a0a;
            case 14: goto L_0x19e9;
            case 15: goto L_0x19c8;
            case 16: goto L_0x19ac;
            case 17: goto L_0x1990;
            case 18: goto L_0x1974;
            case 19: goto L_0x1953;
            case 20: goto L_0x1936;
            case 21: goto L_0x1936;
            case 22: goto L_0x1915;
            case 23: goto L_0x18ed;
            case 24: goto L_0x18c7;
            case 25: goto L_0x18a2;
            case 26: goto L_0x187d;
            case 27: goto L_0x1858;
            case 28: goto L_0x1844;
            case 29: goto L_0x1828;
            case 30: goto L_0x180c;
            case 31: goto L_0x17f0;
            case 32: goto L_0x17d4;
            case 33: goto L_0x17b8;
            case 34: goto L_0x175f;
            case 35: goto L_0x1743;
            case 36: goto L_0x1722;
            case 37: goto L_0x1701;
            case 38: goto L_0x16e0;
            case 39: goto L_0x16c4;
            case 40: goto L_0x16a8;
            case 41: goto L_0x168c;
            case 42: goto L_0x1670;
            case 43: goto L_0x1645;
            case 44: goto L_0x161f;
            case 45: goto L_0x15f9;
            case 46: goto L_0x15d3;
            case 47: goto L_0x15ad;
            case 48: goto L_0x159a;
            case 49: goto L_0x157b;
            case 50: goto L_0x155a;
            case 51: goto L_0x1539;
            case 52: goto L_0x1518;
            case 53: goto L_0x14f7;
            case 54: goto L_0x14d6;
            case 55: goto L_0x145f;
            case 56: goto L_0x143a;
            case 57: goto L_0x1414;
            case 58: goto L_0x13ee;
            case 59: goto L_0x13c8;
            case 60: goto L_0x13a6;
            case 61: goto L_0x1384;
            case 62: goto L_0x1362;
            case 63: goto L_0x133b;
            case 64: goto L_0x1319;
            case 65: goto L_0x12f2;
            case 66: goto L_0x12d7;
            case 67: goto L_0x12d7;
            case 68: goto L_0x12bf;
            case 69: goto L_0x12a7;
            case 70: goto L_0x128a;
            case 71: goto L_0x1272;
            case 72: goto L_0x1254;
            case 73: goto L_0x123b;
            case 74: goto L_0x1222;
            case 75: goto L_0x1209;
            case 76: goto L_0x11f0;
            case 77: goto L_0x11d7;
            case 78: goto L_0x11be;
            case 79: goto L_0x11a5;
            case 80: goto L_0x118c;
            case 81: goto L_0x115d;
            case 82: goto L_0x1130;
            case 83: goto L_0x1103;
            case 84: goto L_0x10d7;
            case 85: goto L_0x10ab;
            case 86: goto L_0x108f;
            case 87: goto L_0x1039;
            case 88: goto L_0x0fed;
            case 89: goto L_0x0fa1;
            case 90: goto L_0x0var_;
            case 91: goto L_0x0var_;
            case 92: goto L_0x0ebd;
            case 93: goto L_0x0e05;
            case 94: goto L_0x0db9;
            case 95: goto L_0x0d63;
            case 96: goto L_0x0d0d;
            case 97: goto L_0x0cb8;
            case 98: goto L_0x0c6d;
            case 99: goto L_0x0CLASSNAME;
            case 100: goto L_0x0bd7;
            case 101: goto L_0x0b8c;
            case 102: goto L_0x0b41;
            case 103: goto L_0x0af6;
            case 104: goto L_0x0adc;
            case 105: goto L_0x1bc9;
            case 106: goto L_0x1bc9;
            case 107: goto L_0x1bc9;
            case 108: goto L_0x1bc9;
            case 109: goto L_0x1bc9;
            case 110: goto L_0x1bc9;
            case 111: goto L_0x1bc9;
            case 112: goto L_0x1bc9;
            case 113: goto L_0x1bc9;
            case 114: goto L_0x1bc9;
            default: goto L_0x0ad8;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x0adc, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
        r4 = true;
        r22 = null;
        r44 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0af3, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0afa, code lost:
        if (r7 <= 0) goto L_0x0b14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0afc, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0b14, code lost:
        if (r1 == 0) goto L_0x0b2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0b16, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0b2e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0b45, code lost:
        if (r7 <= 0) goto L_0x0b5f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0b47, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0b5f, code lost:
        if (r1 == 0) goto L_0x0b79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0b61, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0b79, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0b90, code lost:
        if (r7 <= 0) goto L_0x0baa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0b92, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0baa, code lost:
        if (r1 == 0) goto L_0x0bc4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0bac, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0bc4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0bdb, code lost:
        if (r7 <= 0) goto L_0x0bf5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0bdd, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0bf5, code lost:
        if (r1 == 0) goto L_0x0c0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0bf7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0c0f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0CLASSNAME, code lost:
        if (r7 <= 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0CLASSNAME, code lost:
        if (r1 == 0) goto L_0x0c5a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0c5a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0CLASSNAME, code lost:
        if (r7 <= 0) goto L_0x0c8b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0c8b, code lost:
        if (r1 == 0) goto L_0x0ca5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:727:0x0c8d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0ca5, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0cbc, code lost:
        if (r7 <= 0) goto L_0x0cd6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x0cbe, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0cd6, code lost:
        if (r1 == 0) goto L_0x0cf5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0cd8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r4[0], r4[2], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0cf5, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x0d0d, code lost:
        r8 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0d12, code lost:
        if (r8 <= 0) goto L_0x0d2c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0d14, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0d2c, code lost:
        if (r1 == 0) goto L_0x0d4b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x0d2e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r4[0], r4[2], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0d4b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x0d63, code lost:
        r8 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0d68, code lost:
        if (r8 <= 0) goto L_0x0d82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x0d6a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x0d82, code lost:
        if (r1 == 0) goto L_0x0da1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x0d84, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r4[0], r4[2], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x0da1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x0db9, code lost:
        r8 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x0dbe, code lost:
        if (r8 <= 0) goto L_0x0dd8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0dc0, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x0dd8, code lost:
        if (r1 == 0) goto L_0x0df2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x0dda, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x0df2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:0x0e05, code lost:
        r8 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x0e0a, code lost:
        if (r8 <= 0) goto L_0x0e43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x0e0e, code lost:
        if (r4.length <= 1) goto L_0x0e30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x0e16, code lost:
        if (android.text.TextUtils.isEmpty(r4[1]) != false) goto L_0x0e30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x0e18, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x0e30, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x0e43, code lost:
        if (r1 == 0) goto L_0x0e86;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x0e47, code lost:
        if (r4.length <= 2) goto L_0x0e6e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:0x0e4f, code lost:
        if (android.text.TextUtils.isEmpty(r4[2]) != false) goto L_0x0e6e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x0e51, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r4[0], r4[2], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x0e6e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x0e88, code lost:
        if (r4.length <= 1) goto L_0x0eaa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x0e90, code lost:
        if (android.text.TextUtils.isEmpty(r4[1]) != false) goto L_0x0eaa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x0e92, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x0eaa, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x0ebd, code lost:
        r8 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x0ec2, code lost:
        if (r8 <= 0) goto L_0x0edc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x0ec4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x0edc, code lost:
        if (r1 == 0) goto L_0x0ef6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x0ede, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x0ef6, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x0var_, code lost:
        r8 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x0f0e, code lost:
        if (r8 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x0var_, code lost:
        if (r1 == 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x0f2a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x0var_, code lost:
        r8 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x0f5a, code lost:
        if (r8 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x0f5c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x0var_, code lost:
        if (r1 == 0) goto L_0x0f8e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x0f8e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x0fa1, code lost:
        r8 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x0fa6, code lost:
        if (r8 <= 0) goto L_0x0fc0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x0fa8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x0fc0, code lost:
        if (r1 == 0) goto L_0x0fda;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x0fc2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x0fda, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x0fed, code lost:
        r8 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x0ff2, code lost:
        if (r8 <= 0) goto L_0x100c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x0ff4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x100c, code lost:
        if (r1 == 0) goto L_0x1026;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x100e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x1026, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x1039, code lost:
        r8 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x103e, code lost:
        if (r8 <= 0) goto L_0x1058;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x1040, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x1058, code lost:
        if (r1 == 0) goto L_0x1077;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x105a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r4[0], r4[1], r4[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x1077, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x108f, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x10a6, code lost:
        r3 = r1;
        r7 = r8;
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x10ab, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r4[0], r4[1], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x10d7, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r4[0], r4[1], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x1103, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r4[0], r4[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x1130, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r4[0], r4[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x115d, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r4[0], r4[1], org.telegram.messenger.LocaleController.formatPluralString(r28, org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x118c, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("UserAcceptedToGroupPushWithGroup", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x11a5, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x11be, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x11d7, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x11f0, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x1209, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x1222, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x123b, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupEndedCall", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x1254, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r4[0], r4[1], r4[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x1272, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x128a, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r4[0], r4[1], r4[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x12a7, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x12bf, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x12d7, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r4[0], r4[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x12ee, code lost:
        r3 = r1;
        r7 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x12f2, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r4[0], r4[1], r4[2]);
        r3 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x1319, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r4[0], r4[1], r4[2], r4[3]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x133b, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r4[0], r4[1], r4[2]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x1362, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x1384, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x13a6, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x13c8, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r4[0], r4[1], r4[2]);
        r3 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x13ee, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r4[0], r4[1], r4[2]);
        r3 = org.telegram.messenger.LocaleController.getString("PollQuiz", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x1414, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r4[0], r4[1], r4[2]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x143a, code lost:
        r8 = r7;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x145a, code lost:
        r22 = r3;
        r7 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x1461, code lost:
        if (r4.length <= 2) goto L_0x14a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x1469, code lost:
        if (android.text.TextUtils.isEmpty(r4[2]) != false) goto L_0x14a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x146b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r4[0], r4[1], r4[2]);
        r3 = r4[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x14a3, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r4[0], r4[1]);
        r3 = r4[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x14d6, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x14f7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:851:0x1518, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x1539, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x155a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x157b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r4[0], r4[1], r4[2]);
        r3 = r4[2];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x159a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:856:0x15ad, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x15d3, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x15f9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x161f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x1645, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4[1]).intValue(), new java.lang.Object[0]).toLowerCase());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x1670, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:862:0x168c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:863:0x16a8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:864:0x16c4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:865:0x16e0, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:866:0x1701, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x1722, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x1743, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x1761, code lost:
        if (r4.length <= 1) goto L_0x179e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x1769, code lost:
        if (android.text.TextUtils.isEmpty(r4[1]) != false) goto L_0x179e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x176b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r4[0], r4[1]);
        r3 = r4[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x179e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x17b8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x17d4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x17f0, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x180c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x1828, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x1844, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x1855, code lost:
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x1858, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x187d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x18a2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:0x18c7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x18ed, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r4[0], org.telegram.messenger.LocaleController.formatPluralString(r28, org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x1915, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x1936, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r4[0], r4[1], r4[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x1953, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x1974, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x1990, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x19ac, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x19c8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x19e9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x1a0a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x1a2b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x1a49, code lost:
        if (r4.length <= 1) goto L_0x1a86;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:0x1a51, code lost:
        if (android.text.TextUtils.isEmpty(r4[1]) != false) goto L_0x1a86;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x1a53, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r4[0], r4[1]);
        r3 = r4[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x1a86, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:0x1aa0, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:0x1abc, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x1ad8, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r4[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:906:0x1aea, code lost:
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:0x1aeb, code lost:
        r22 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x1aef, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:909:0x1b0a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:910:0x1b25, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:0x1b40, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:0x1b5b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r4[0]);
        r3 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:913:0x1b75, code lost:
        r22 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:0x1b77, code lost:
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:0x1b7a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r4[0], r4[1]);
        r3 = r4[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x1b93, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRecurringPay", NUM, r4[0], r4[1]);
        r3 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:917:0x1bb3, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1bc9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:918:0x1bb5, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:0x1bc9, code lost:
        r3 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:920:0x1bca, code lost:
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x1bcb, code lost:
        r22 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x1bcd, code lost:
        r1 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x1bd0, code lost:
        r46 = "REACT_";
        r48 = r11;
        r47 = r12;
        r17 = "CHAT_REACT_";
        r1 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x1bda, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:?, code lost:
        r3 = r1.getReactedText(r2, r4);
        r4 = false;
        r22 = null;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x1be1, code lost:
        if (r3 == null) goto L_0x1cef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x1be3, code lost:
        r6 = new org.telegram.tgnet.TLRPC$TL_message();
        r6.id = r5;
        r6.random_id = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:928:0x1bee, code lost:
        if (r22 == null) goto L_0x1bf3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:0x1bf0, code lost:
        r5 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x1bf3, code lost:
        r5 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x1bf4, code lost:
        r6.message = r5;
        r6.date = (int) (r53 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:932:0x1bfd, code lost:
        if (r10 == false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x1bff, code lost:
        r6.action = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x1CLASSNAME, code lost:
        if (r43 == false) goto L_0x1c0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:935:0x1CLASSNAME, code lost:
        r6.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:0x1c0f, code lost:
        r6.dialog_id = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:0x1CLASSNAME, code lost:
        if (r37 == 0) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:0x1CLASSNAME, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r6.peer_id = r5;
        r5.channel_id = r37;
        r7 = r26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:0x1CLASSNAME, code lost:
        if (r26 == 0) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:0x1c2b, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r6.peer_id = r5;
        r7 = r26;
        r5.chat_id = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:0x1CLASSNAME, code lost:
        r7 = r26;
        r5 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r6.peer_id = r5;
        r5.user_id = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:943:0x1CLASSNAME, code lost:
        r6.flags |= 256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:944:0x1c4e, code lost:
        if (r39 == 0) goto L_0x1c5a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:0x1CLASSNAME, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r6.from_id = r5;
        r5.chat_id = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x1c5e, code lost:
        if (r13 == 0) goto L_0x1c6a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x1CLASSNAME, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r6.from_id = r5;
        r5.channel_id = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x1c6e, code lost:
        if (r41 == 0) goto L_0x1c7c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x1CLASSNAME, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r6.from_id = r5;
        r5.user_id = r41;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x1c7c, code lost:
        r6.from_id = r6.peer_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x1CLASSNAME, code lost:
        if (r16 != false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:0x1CLASSNAME, code lost:
        if (r10 == false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:0x1CLASSNAME, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:957:0x1CLASSNAME, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:0x1CLASSNAME, code lost:
        r6.mentioned = r5;
        r6.silent = r32;
        r6.from_scheduled = r25;
        r23 = new org.telegram.messenger.MessageObject(r34, r6, r3, r44, r48, r4, r47, r43, r45);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x1cb1, code lost:
        if (r2.startsWith(r46) != false) goto L_0x1cbe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:0x1cb9, code lost:
        if (r2.startsWith(r17) == false) goto L_0x1cbc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:0x1cbc, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:964:0x1cbe, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:965:0x1cbf, code lost:
        r23.isReactionPush = r3;
        r3 = new java.util.ArrayList();
        r3.add(r23);
        org.telegram.messenger.NotificationsController.getInstance(r34).processNewMessages(r3, true, true, r1.countDownLatch);
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:966:0x1cd5, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:967:0x1cd6, code lost:
        r1 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:968:0x1cd9, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:969:0x1cda, code lost:
        r1 = r51;
        r34 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:0x1cdf, code lost:
        r1 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:971:0x1ce2, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:972:0x1ce3, code lost:
        r1 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:0x1ce6, code lost:
        r0 = th;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:974:0x1ce7, code lost:
        r34 = r12;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:975:0x1ceb, code lost:
        r33 = r6;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:976:0x1ced, code lost:
        r34 = r12;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:977:0x1cef, code lost:
        r9 = true;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:978:0x1cf0, code lost:
        if (r9 == false) goto L_0x1cf7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:979:0x1cf2, code lost:
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:980:0x1cf7, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r34);
        org.telegram.tgnet.ConnectionsManager.getInstance(r34).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:981:0x1d03, code lost:
        r0 = th;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:982:0x1d04, code lost:
        r5 = r2;
        r6 = r33;
        r12 = r34;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:983:0x1d0b, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:984:0x1d0c, code lost:
        r33 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:985:0x1d0f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:986:0x1d10, code lost:
        r33 = r6;
        r2 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:987:0x1d13, code lost:
        r34 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:988:0x1d17, code lost:
        r33 = r6;
        r2 = r8;
        r34 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x1d20, code lost:
        r12 = r34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:993:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1(r12));
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:994:0x1d2d, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:995:0x1d2e, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:996:0x1d2f, code lost:
        r12 = r34;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:997:0x1d33, code lost:
        r33 = r6;
        r2 = r8;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0(r12));
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:998:0x1d43, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:999:0x1d44, code lost:
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
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:201:0x0313, B:989:0x1d1c] */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1024:0x1df0  */
    /* JADX WARNING: Removed duplicated region for block: B:1025:0x1e00  */
    /* JADX WARNING: Removed duplicated region for block: B:1028:0x1e07  */
    /* JADX WARNING: Removed duplicated region for block: B:1030:0x0196 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0184 A[SYNTHETIC, Splitter:B:78:0x0184] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0199 A[Catch:{ all -> 0x011b }] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x01a8 A[SYNTHETIC, Splitter:B:90:0x01a8] */
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
            java.lang.Object r6 = r2.get(r6)     // Catch:{ all -> 0x1de8 }
            boolean r7 = r6 instanceof java.lang.String     // Catch:{ all -> 0x1de8 }
            if (r7 != 0) goto L_0x0026
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1de8 }
            if (r2 == 0) goto L_0x0022
            java.lang.String r2 = "GCM DECRYPT ERROR 1"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x1de8 }
        L_0x0022:
            r51.onDecryptError()     // Catch:{ all -> 0x1de8 }
            return
        L_0x0026:
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ all -> 0x1de8 }
            r7 = 8
            byte[] r6 = android.util.Base64.decode(r6, r7)     // Catch:{ all -> 0x1de8 }
            org.telegram.tgnet.NativeByteBuffer r8 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x1de8 }
            int r9 = r6.length     // Catch:{ all -> 0x1de8 }
            r8.<init>((int) r9)     // Catch:{ all -> 0x1de8 }
            r8.writeBytes((byte[]) r6)     // Catch:{ all -> 0x1de8 }
            r9 = 0
            r8.position(r9)     // Catch:{ all -> 0x1de8 }
            byte[] r10 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1de8 }
            if (r10 != 0) goto L_0x0050
            byte[] r10 = new byte[r7]     // Catch:{ all -> 0x1de8 }
            org.telegram.messenger.SharedConfig.pushAuthKeyId = r10     // Catch:{ all -> 0x1de8 }
            byte[] r10 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1de8 }
            byte[] r10 = org.telegram.messenger.Utilities.computeSHA1((byte[]) r10)     // Catch:{ all -> 0x1de8 }
            int r11 = r10.length     // Catch:{ all -> 0x1de8 }
            int r11 = r11 - r7
            byte[] r12 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1de8 }
            java.lang.System.arraycopy(r10, r11, r12, r9, r7)     // Catch:{ all -> 0x1de8 }
        L_0x0050:
            byte[] r10 = new byte[r7]     // Catch:{ all -> 0x1de8 }
            r11 = 1
            r8.readBytes(r10, r11)     // Catch:{ all -> 0x1de8 }
            byte[] r12 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1de8 }
            boolean r12 = java.util.Arrays.equals(r12, r10)     // Catch:{ all -> 0x1de8 }
            r13 = 3
            r14 = 2
            if (r12 != 0) goto L_0x008b
            r51.onDecryptError()     // Catch:{ all -> 0x1de8 }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1de8 }
            if (r2 == 0) goto L_0x008a
            java.util.Locale r2 = java.util.Locale.US     // Catch:{ all -> 0x1de8 }
            java.lang.String r3 = "GCM DECRYPT ERROR 2 k1=%s k2=%s, key=%s"
            java.lang.Object[] r6 = new java.lang.Object[r13]     // Catch:{ all -> 0x1de8 }
            byte[] r7 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1de8 }
            java.lang.String r7 = org.telegram.messenger.Utilities.bytesToHex(r7)     // Catch:{ all -> 0x1de8 }
            r6[r9] = r7     // Catch:{ all -> 0x1de8 }
            java.lang.String r7 = org.telegram.messenger.Utilities.bytesToHex(r10)     // Catch:{ all -> 0x1de8 }
            r6[r11] = r7     // Catch:{ all -> 0x1de8 }
            byte[] r7 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1de8 }
            java.lang.String r7 = org.telegram.messenger.Utilities.bytesToHex(r7)     // Catch:{ all -> 0x1de8 }
            r6[r14] = r7     // Catch:{ all -> 0x1de8 }
            java.lang.String r2 = java.lang.String.format(r2, r3, r6)     // Catch:{ all -> 0x1de8 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x1de8 }
        L_0x008a:
            return
        L_0x008b:
            r10 = 16
            byte[] r10 = new byte[r10]     // Catch:{ all -> 0x1de8 }
            r8.readBytes(r10, r11)     // Catch:{ all -> 0x1de8 }
            byte[] r12 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1de8 }
            org.telegram.messenger.MessageKeyData r12 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r12, r10, r11, r14)     // Catch:{ all -> 0x1de8 }
            java.nio.ByteBuffer r15 = r8.buffer     // Catch:{ all -> 0x1de8 }
            byte[] r5 = r12.aesKey     // Catch:{ all -> 0x1de8 }
            byte[] r12 = r12.aesIv     // Catch:{ all -> 0x1de8 }
            r18 = 0
            r19 = 0
            r20 = 24
            int r6 = r6.length     // Catch:{ all -> 0x1de8 }
            int r21 = r6 + -24
            r16 = r5
            r17 = r12
            org.telegram.messenger.Utilities.aesIgeEncryption(r15, r16, r17, r18, r19, r20, r21)     // Catch:{ all -> 0x1de8 }
            byte[] r23 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1de8 }
            r24 = 96
            r25 = 32
            java.nio.ByteBuffer r5 = r8.buffer     // Catch:{ all -> 0x1de8 }
            r27 = 24
            int r28 = r5.limit()     // Catch:{ all -> 0x1de8 }
            r26 = r5
            byte[] r5 = org.telegram.messenger.Utilities.computeSHA256(r23, r24, r25, r26, r27, r28)     // Catch:{ all -> 0x1de8 }
            boolean r5 = org.telegram.messenger.Utilities.arraysEquals(r10, r9, r5, r7)     // Catch:{ all -> 0x1de8 }
            if (r5 != 0) goto L_0x00e3
            r51.onDecryptError()     // Catch:{ all -> 0x1de8 }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1de8 }
            if (r2 == 0) goto L_0x00e2
            java.lang.String r2 = "GCM DECRYPT ERROR 3, key = %s"
            java.lang.Object[] r3 = new java.lang.Object[r11]     // Catch:{ all -> 0x1de8 }
            byte[] r5 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1de8 }
            java.lang.String r5 = org.telegram.messenger.Utilities.bytesToHex(r5)     // Catch:{ all -> 0x1de8 }
            r3[r9] = r5     // Catch:{ all -> 0x1de8 }
            java.lang.String r2 = java.lang.String.format(r2, r3)     // Catch:{ all -> 0x1de8 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x1de8 }
        L_0x00e2:
            return
        L_0x00e3:
            int r5 = r8.readInt32(r11)     // Catch:{ all -> 0x1de8 }
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x1de8 }
            r8.readBytes(r5, r11)     // Catch:{ all -> 0x1de8 }
            java.lang.String r6 = new java.lang.String     // Catch:{ all -> 0x1de8 }
            r6.<init>(r5)     // Catch:{ all -> 0x1de8 }
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ all -> 0x1de1 }
            r5.<init>(r6)     // Catch:{ all -> 0x1de1 }
            java.lang.String r8 = "loc_key"
            boolean r8 = r5.has(r8)     // Catch:{ all -> 0x1de1 }
            if (r8 == 0) goto L_0x0108
            java.lang.String r8 = "loc_key"
            java.lang.String r8 = r5.getString(r8)     // Catch:{ all -> 0x0105 }
            goto L_0x010a
        L_0x0105:
            r0 = move-exception
            goto L_0x1de4
        L_0x0108:
            java.lang.String r8 = ""
        L_0x010a:
            java.lang.String r10 = "custom"
            java.lang.Object r10 = r5.get(r10)     // Catch:{ all -> 0x1dd8 }
            boolean r10 = r10 instanceof org.json.JSONObject     // Catch:{ all -> 0x1dd8 }
            if (r10 == 0) goto L_0x0121
            java.lang.String r10 = "custom"
            org.json.JSONObject r10 = r5.getJSONObject(r10)     // Catch:{ all -> 0x011b }
            goto L_0x0126
        L_0x011b:
            r0 = move-exception
            r2 = r0
            r5 = r8
            r3 = -1
            goto L_0x1ded
        L_0x0121:
            org.json.JSONObject r10 = new org.json.JSONObject     // Catch:{ all -> 0x1dd8 }
            r10.<init>()     // Catch:{ all -> 0x1dd8 }
        L_0x0126:
            java.lang.String r12 = "user_id"
            boolean r12 = r5.has(r12)     // Catch:{ all -> 0x1dd8 }
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
            boolean r15 = r12 instanceof java.lang.Long     // Catch:{ all -> 0x1dd8 }
            if (r15 == 0) goto L_0x0154
            java.lang.Long r12 = (java.lang.Long) r12     // Catch:{ all -> 0x011b }
            long r15 = r12.longValue()     // Catch:{ all -> 0x011b }
            goto L_0x0142
        L_0x0154:
            boolean r15 = r12 instanceof java.lang.Integer     // Catch:{ all -> 0x1dd8 }
            if (r15 == 0) goto L_0x0162
            java.lang.Integer r12 = (java.lang.Integer) r12     // Catch:{ all -> 0x011b }
            int r12 = r12.intValue()     // Catch:{ all -> 0x011b }
            r16 = r5
            long r4 = (long) r12
            goto L_0x017e
        L_0x0162:
            r16 = r5
            boolean r4 = r12 instanceof java.lang.String     // Catch:{ all -> 0x1dd8 }
            if (r4 == 0) goto L_0x0174
            java.lang.String r12 = (java.lang.String) r12     // Catch:{ all -> 0x011b }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r12)     // Catch:{ all -> 0x011b }
            int r4 = r4.intValue()     // Catch:{ all -> 0x011b }
            long r4 = (long) r4
            goto L_0x017e
        L_0x0174:
            int r4 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1dd8 }
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x1dd8 }
            long r4 = r4.getClientUserId()     // Catch:{ all -> 0x1dd8 }
        L_0x017e:
            int r12 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1dd8 }
            r7 = 0
        L_0x0181:
            r15 = 4
            if (r7 >= r15) goto L_0x0196
            org.telegram.messenger.UserConfig r19 = org.telegram.messenger.UserConfig.getInstance(r7)     // Catch:{ all -> 0x011b }
            long r19 = r19.getClientUserId()     // Catch:{ all -> 0x011b }
            int r21 = (r19 > r4 ? 1 : (r19 == r4 ? 0 : -1))
            if (r21 != 0) goto L_0x0193
            r12 = r7
            r4 = 1
            goto L_0x0197
        L_0x0193:
            int r7 = r7 + 1
            goto L_0x0181
        L_0x0196:
            r4 = 0
        L_0x0197:
            if (r4 != 0) goto L_0x01a8
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x011b }
            if (r2 == 0) goto L_0x01a2
            java.lang.String r2 = "GCM ACCOUNT NOT FOUND"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x011b }
        L_0x01a2:
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x011b }
            r2.countDown()     // Catch:{ all -> 0x011b }
            return
        L_0x01a8:
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r12)     // Catch:{ all -> 0x1dd1 }
            boolean r4 = r4.isClientActivated()     // Catch:{ all -> 0x1dd1 }
            if (r4 != 0) goto L_0x01c7
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x01c1 }
            if (r2 == 0) goto L_0x01bb
            java.lang.String r2 = "GCM ACCOUNT NOT ACTIVATED"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x01c1 }
        L_0x01bb:
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x01c1 }
            r2.countDown()     // Catch:{ all -> 0x01c1 }
            return
        L_0x01c1:
            r0 = move-exception
            r2 = r0
            r5 = r8
            r3 = -1
            goto L_0x1dee
        L_0x01c7:
            java.lang.String r4 = "google.sent_time"
            r2.get(r4)     // Catch:{ all -> 0x1dd1 }
            int r2 = r8.hashCode()     // Catch:{ all -> 0x1dd1 }
            switch(r2) {
                case -1963663249: goto L_0x01f2;
                case -920689527: goto L_0x01e8;
                case 633004703: goto L_0x01de;
                case 1365673842: goto L_0x01d4;
                default: goto L_0x01d3;
            }
        L_0x01d3:
            goto L_0x01fc
        L_0x01d4:
            java.lang.String r2 = "GEO_LIVE_PENDING"
            boolean r2 = r8.equals(r2)     // Catch:{ all -> 0x01c1 }
            if (r2 == 0) goto L_0x01fc
            r2 = 3
            goto L_0x01fd
        L_0x01de:
            java.lang.String r2 = "MESSAGE_ANNOUNCEMENT"
            boolean r2 = r8.equals(r2)     // Catch:{ all -> 0x01c1 }
            if (r2 == 0) goto L_0x01fc
            r2 = 1
            goto L_0x01fd
        L_0x01e8:
            java.lang.String r2 = "DC_UPDATE"
            boolean r2 = r8.equals(r2)     // Catch:{ all -> 0x01c1 }
            if (r2 == 0) goto L_0x01fc
            r2 = 0
            goto L_0x01fd
        L_0x01f2:
            java.lang.String r2 = "SESSION_REVOKE"
            boolean r2 = r8.equals(r2)     // Catch:{ all -> 0x01c1 }
            if (r2 == 0) goto L_0x01fc
            r2 = 2
            goto L_0x01fd
        L_0x01fc:
            r2 = -1
        L_0x01fd:
            if (r2 == 0) goto L_0x1d8f
            if (r2 == r11) goto L_0x1d44
            if (r2 == r14) goto L_0x1d33
            if (r2 == r13) goto L_0x1d17
            java.lang.String r2 = "channel_id"
            boolean r2 = r10.has(r2)     // Catch:{ all -> 0x1d0f }
            if (r2 == 0) goto L_0x0219
            java.lang.String r2 = "channel_id"
            long r13 = r10.getLong(r2)     // Catch:{ all -> 0x0216 }
            r2 = r8
            long r7 = -r13
            goto L_0x021e
        L_0x0216:
            r0 = move-exception
            goto L_0x1dd4
        L_0x0219:
            r2 = r8
            r7 = 0
            r13 = 0
        L_0x021e:
            java.lang.String r15 = "from_id"
            boolean r15 = r10.has(r15)     // Catch:{ all -> 0x1d0b }
            if (r15 == 0) goto L_0x0232
            java.lang.String r7 = "from_id"
            long r7 = r10.getLong(r7)     // Catch:{ all -> 0x022f }
            r33 = r7
            goto L_0x0234
        L_0x022f:
            r0 = move-exception
            goto L_0x1dd5
        L_0x0232:
            r33 = 0
        L_0x0234:
            java.lang.String r15 = "chat_id"
            boolean r15 = r10.has(r15)     // Catch:{ all -> 0x1d0b }
            if (r15 == 0) goto L_0x0249
            java.lang.String r7 = "chat_id"
            long r7 = r10.getLong(r7)     // Catch:{ all -> 0x022f }
            long r4 = -r7
            r49 = r4
            r4 = r7
            r7 = r49
            goto L_0x024b
        L_0x0249:
            r4 = 0
        L_0x024b:
            java.lang.String r15 = "encryption_id"
            boolean r15 = r10.has(r15)     // Catch:{ all -> 0x1d0b }
            if (r15 == 0) goto L_0x025e
            java.lang.String r7 = "encryption_id"
            int r7 = r10.getInt(r7)     // Catch:{ all -> 0x022f }
            long r7 = (long) r7     // Catch:{ all -> 0x022f }
            long r7 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r7)     // Catch:{ all -> 0x022f }
        L_0x025e:
            java.lang.String r15 = "schedule"
            boolean r15 = r10.has(r15)     // Catch:{ all -> 0x1d0b }
            if (r15 == 0) goto L_0x0270
            java.lang.String r15 = "schedule"
            int r15 = r10.getInt(r15)     // Catch:{ all -> 0x022f }
            if (r15 != r11) goto L_0x0270
            r15 = 1
            goto L_0x0271
        L_0x0270:
            r15 = 0
        L_0x0271:
            r23 = 0
            int r21 = (r7 > r23 ? 1 : (r7 == r23 ? 0 : -1))
            if (r21 != 0) goto L_0x0281
            java.lang.String r11 = "ENCRYPTED_MESSAGE"
            boolean r11 = r11.equals(r2)     // Catch:{ all -> 0x022f }
            if (r11 == 0) goto L_0x0281
            long r7 = org.telegram.messenger.NotificationsController.globalSecretChatId     // Catch:{ all -> 0x022f }
        L_0x0281:
            r23 = 0
            int r11 = (r7 > r23 ? 1 : (r7 == r23 ? 0 : -1))
            if (r11 == 0) goto L_0x1ceb
            java.lang.String r11 = "READ_HISTORY"
            boolean r11 = r11.equals(r2)     // Catch:{ all -> 0x1d0b }
            java.lang.String r9 = " for dialogId = "
            if (r11 == 0) goto L_0x0305
            java.lang.String r3 = "max_id"
            int r3 = r10.getInt(r3)     // Catch:{ all -> 0x022f }
            java.util.ArrayList r10 = new java.util.ArrayList     // Catch:{ all -> 0x022f }
            r10.<init>()     // Catch:{ all -> 0x022f }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x022f }
            if (r11 == 0) goto L_0x02ba
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x022f }
            r11.<init>()     // Catch:{ all -> 0x022f }
            java.lang.String r15 = "GCM received read notification max_id = "
            r11.append(r15)     // Catch:{ all -> 0x022f }
            r11.append(r3)     // Catch:{ all -> 0x022f }
            r11.append(r9)     // Catch:{ all -> 0x022f }
            r11.append(r7)     // Catch:{ all -> 0x022f }
            java.lang.String r7 = r11.toString()     // Catch:{ all -> 0x022f }
            org.telegram.messenger.FileLog.d(r7)     // Catch:{ all -> 0x022f }
        L_0x02ba:
            r7 = 0
            int r9 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x02cd
            org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox r4 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox     // Catch:{ all -> 0x022f }
            r4.<init>()     // Catch:{ all -> 0x022f }
            r4.channel_id = r13     // Catch:{ all -> 0x022f }
            r4.max_id = r3     // Catch:{ all -> 0x022f }
            r10.add(r4)     // Catch:{ all -> 0x022f }
            goto L_0x02f2
        L_0x02cd:
            org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox r7 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox     // Catch:{ all -> 0x022f }
            r7.<init>()     // Catch:{ all -> 0x022f }
            r8 = r33
            r13 = 0
            int r11 = (r8 > r13 ? 1 : (r8 == r13 ? 0 : -1))
            if (r11 == 0) goto L_0x02e4
            org.telegram.tgnet.TLRPC$TL_peerUser r4 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x022f }
            r4.<init>()     // Catch:{ all -> 0x022f }
            r7.peer = r4     // Catch:{ all -> 0x022f }
            r4.user_id = r8     // Catch:{ all -> 0x022f }
            goto L_0x02ed
        L_0x02e4:
            org.telegram.tgnet.TLRPC$TL_peerChat r8 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x022f }
            r8.<init>()     // Catch:{ all -> 0x022f }
            r7.peer = r8     // Catch:{ all -> 0x022f }
            r8.chat_id = r4     // Catch:{ all -> 0x022f }
        L_0x02ed:
            r7.max_id = r3     // Catch:{ all -> 0x022f }
            r10.add(r7)     // Catch:{ all -> 0x022f }
        L_0x02f2:
            org.telegram.messenger.MessagesController r25 = org.telegram.messenger.MessagesController.getInstance(r12)     // Catch:{ all -> 0x022f }
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r26 = r10
            r25.processUpdateArray(r26, r27, r28, r29, r30)     // Catch:{ all -> 0x022f }
            goto L_0x1ceb
        L_0x0305:
            r35 = r33
            java.lang.String r11 = "MESSAGE_DELETED"
            boolean r11 = r11.equals(r2)     // Catch:{ all -> 0x1d0b }
            r33 = r6
            java.lang.String r6 = "messages"
            if (r11 == 0) goto L_0x037d
            java.lang.String r3 = r10.getString(r6)     // Catch:{ all -> 0x1dcc }
            java.lang.String r4 = ","
            java.lang.String[] r3 = r3.split(r4)     // Catch:{ all -> 0x1dcc }
            androidx.collection.LongSparseArray r4 = new androidx.collection.LongSparseArray     // Catch:{ all -> 0x1dcc }
            r4.<init>()     // Catch:{ all -> 0x1dcc }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x1dcc }
            r5.<init>()     // Catch:{ all -> 0x1dcc }
            r6 = 0
        L_0x0328:
            int r10 = r3.length     // Catch:{ all -> 0x1dcc }
            if (r6 >= r10) goto L_0x0337
            r10 = r3[r6]     // Catch:{ all -> 0x1dcc }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1dcc }
            r5.add(r10)     // Catch:{ all -> 0x1dcc }
            int r6 = r6 + 1
            goto L_0x0328
        L_0x0337:
            long r10 = -r13
            r4.put(r10, r5)     // Catch:{ all -> 0x1dcc }
            org.telegram.messenger.NotificationsController r3 = org.telegram.messenger.NotificationsController.getInstance(r12)     // Catch:{ all -> 0x1dcc }
            r3.removeDeletedMessagesFromNotifications(r4)     // Catch:{ all -> 0x1dcc }
            org.telegram.messenger.MessagesController r23 = org.telegram.messenger.MessagesController.getInstance(r12)     // Catch:{ all -> 0x1dcc }
            r24 = r7
            r26 = r5
            r27 = r13
            r23.deleteMessagesByPush(r24, r26, r27)     // Catch:{ all -> 0x1dcc }
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1dcc }
            if (r3 == 0) goto L_0x1ced
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x1dcc }
            r3.<init>()     // Catch:{ all -> 0x1dcc }
            java.lang.String r4 = "GCM received "
            r3.append(r4)     // Catch:{ all -> 0x1dcc }
            r3.append(r2)     // Catch:{ all -> 0x1dcc }
            r3.append(r9)     // Catch:{ all -> 0x1dcc }
            r3.append(r7)     // Catch:{ all -> 0x1dcc }
            java.lang.String r4 = " mids = "
            r3.append(r4)     // Catch:{ all -> 0x1dcc }
            java.lang.String r4 = ","
            java.lang.String r4 = android.text.TextUtils.join(r4, r5)     // Catch:{ all -> 0x1dcc }
            r3.append(r4)     // Catch:{ all -> 0x1dcc }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x1dcc }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ all -> 0x1dcc }
            goto L_0x1ced
        L_0x037d:
            boolean r11 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x1ce6 }
            if (r11 != 0) goto L_0x1ced
            java.lang.String r11 = "msg_id"
            boolean r11 = r10.has(r11)     // Catch:{ all -> 0x1ce6 }
            if (r11 == 0) goto L_0x0394
            java.lang.String r11 = "msg_id"
            int r11 = r10.getInt(r11)     // Catch:{ all -> 0x1dcc }
            r25 = r15
            goto L_0x0397
        L_0x0394:
            r25 = r15
            r11 = 0
        L_0x0397:
            java.lang.String r15 = "random_id"
            boolean r15 = r10.has(r15)     // Catch:{ all -> 0x1ce6 }
            if (r15 == 0) goto L_0x03b4
            java.lang.String r15 = "random_id"
            java.lang.String r15 = r10.getString(r15)     // Catch:{ all -> 0x1dcc }
            java.lang.Long r15 = org.telegram.messenger.Utilities.parseLong(r15)     // Catch:{ all -> 0x1dcc }
            long r26 = r15.longValue()     // Catch:{ all -> 0x1dcc }
            r49 = r4
            r4 = r26
            r26 = r49
            goto L_0x03b8
        L_0x03b4:
            r26 = r4
            r4 = 0
        L_0x03b8:
            if (r11 == 0) goto L_0x03fe
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r12)     // Catch:{ all -> 0x03f5 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r15 = r15.dialogs_read_inbox_max     // Catch:{ all -> 0x03f5 }
            java.lang.Long r1 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x03f5 }
            java.lang.Object r1 = r15.get(r1)     // Catch:{ all -> 0x03f5 }
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch:{ all -> 0x03f5 }
            if (r1 != 0) goto L_0x03e9
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r12)     // Catch:{ all -> 0x03f5 }
            r15 = 0
            int r1 = r1.getDialogReadMax(r15, r7)     // Catch:{ all -> 0x03f5 }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x03f5 }
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r12)     // Catch:{ all -> 0x03f5 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r15 = r15.dialogs_read_inbox_max     // Catch:{ all -> 0x03f5 }
            r28 = r6
            java.lang.Long r6 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x03f5 }
            r15.put(r6, r1)     // Catch:{ all -> 0x03f5 }
            goto L_0x03eb
        L_0x03e9:
            r28 = r6
        L_0x03eb:
            int r1 = r1.intValue()     // Catch:{ all -> 0x03f5 }
            if (r11 <= r1) goto L_0x03f3
        L_0x03f1:
            r1 = 1
            goto L_0x0411
        L_0x03f3:
            r1 = 0
            goto L_0x0411
        L_0x03f5:
            r0 = move-exception
            r3 = -1
            r1 = r51
            r5 = r2
        L_0x03fa:
            r6 = r33
            goto L_0x1ddf
        L_0x03fe:
            r28 = r6
            r23 = 0
            int r1 = (r4 > r23 ? 1 : (r4 == r23 ? 0 : -1))
            if (r1 == 0) goto L_0x03f3
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r12)     // Catch:{ all -> 0x03f5 }
            boolean r1 = r1.checkMessageByRandomId(r4)     // Catch:{ all -> 0x03f5 }
            if (r1 != 0) goto L_0x03f3
            goto L_0x03f1
        L_0x0411:
            boolean r6 = r2.startsWith(r3)     // Catch:{ all -> 0x1ce2 }
            java.lang.String r15 = "CHAT_REACT_"
            if (r6 != 0) goto L_0x041f
            boolean r6 = r2.startsWith(r15)     // Catch:{ all -> 0x03f5 }
            if (r6 == 0) goto L_0x0420
        L_0x041f:
            r1 = 1
        L_0x0420:
            if (r1 == 0) goto L_0x1cdf
            java.lang.String r1 = "chat_from_id"
            r29 = r4
            r31 = r11
            r6 = r12
            r4 = 0
            long r11 = r10.optLong(r1, r4)     // Catch:{ all -> 0x1cd9 }
            java.lang.String r1 = "chat_from_broadcast_id"
            r37 = r13
            long r13 = r10.optLong(r1, r4)     // Catch:{ all -> 0x1cd9 }
            java.lang.String r1 = "chat_from_group_id"
            long r39 = r10.optLong(r1, r4)     // Catch:{ all -> 0x1cd9 }
            int r1 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r1 != 0) goto L_0x0448
            int r1 = (r39 > r4 ? 1 : (r39 == r4 ? 0 : -1))
            if (r1 == 0) goto L_0x0446
            goto L_0x0448
        L_0x0446:
            r1 = 0
            goto L_0x0449
        L_0x0448:
            r1 = 1
        L_0x0449:
            java.lang.String r4 = "mention"
            boolean r4 = r10.has(r4)     // Catch:{ all -> 0x1cd9 }
            if (r4 == 0) goto L_0x0462
            java.lang.String r4 = "mention"
            int r4 = r10.getInt(r4)     // Catch:{ all -> 0x045b }
            if (r4 == 0) goto L_0x0462
            r4 = 1
            goto L_0x0463
        L_0x045b:
            r0 = move-exception
            r3 = -1
            r1 = r51
            r5 = r2
            r12 = r6
            goto L_0x03fa
        L_0x0462:
            r4 = 0
        L_0x0463:
            java.lang.String r5 = "silent"
            boolean r5 = r10.has(r5)     // Catch:{ all -> 0x1cd9 }
            if (r5 == 0) goto L_0x0477
            java.lang.String r5 = "silent"
            int r5 = r10.getInt(r5)     // Catch:{ all -> 0x045b }
            if (r5 == 0) goto L_0x0477
            r34 = r6
            r5 = 1
            goto L_0x047a
        L_0x0477:
            r34 = r6
            r5 = 0
        L_0x047a:
            java.lang.String r6 = "loc_args"
            r32 = r5
            r5 = r16
            boolean r6 = r5.has(r6)     // Catch:{ all -> 0x1cd5 }
            if (r6 == 0) goto L_0x04ad
            java.lang.String r6 = "loc_args"
            org.json.JSONArray r5 = r5.getJSONArray(r6)     // Catch:{ all -> 0x04a2 }
            int r6 = r5.length()     // Catch:{ all -> 0x04a2 }
            r16 = r4
            java.lang.String[] r4 = new java.lang.String[r6]     // Catch:{ all -> 0x04a2 }
            r41 = r11
            r11 = 0
        L_0x0497:
            if (r11 >= r6) goto L_0x04b2
            java.lang.String r12 = r5.getString(r11)     // Catch:{ all -> 0x04a2 }
            r4[r11] = r12     // Catch:{ all -> 0x04a2 }
            int r11 = r11 + 1
            goto L_0x0497
        L_0x04a2:
            r0 = move-exception
            r3 = -1
            r1 = r51
            r5 = r2
            r6 = r33
            r12 = r34
            goto L_0x1ddf
        L_0x04ad:
            r16 = r4
            r41 = r11
            r4 = 0
        L_0x04b2:
            r5 = 0
            r6 = r4[r5]     // Catch:{ all -> 0x1cd5 }
            java.lang.String r5 = "edit_date"
            boolean r5 = r10.has(r5)     // Catch:{ all -> 0x1cd5 }
            java.lang.String r10 = "CHAT_"
            boolean r10 = r2.startsWith(r10)     // Catch:{ all -> 0x1cd5 }
            if (r10 == 0) goto L_0x04f3
            boolean r10 = org.telegram.messenger.UserObject.isReplyUser((long) r7)     // Catch:{ all -> 0x04a2 }
            if (r10 == 0) goto L_0x04e1
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x04a2 }
            r10.<init>()     // Catch:{ all -> 0x04a2 }
            r10.append(r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r6 = " @ "
            r10.append(r6)     // Catch:{ all -> 0x04a2 }
            r6 = 1
            r11 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r10.append(r11)     // Catch:{ all -> 0x04a2 }
            java.lang.String r6 = r10.toString()     // Catch:{ all -> 0x04a2 }
            goto L_0x0516
        L_0x04e1:
            r10 = 0
            int r12 = (r37 > r10 ? 1 : (r37 == r10 ? 0 : -1))
            if (r12 == 0) goto L_0x04e9
            r10 = 1
            goto L_0x04ea
        L_0x04e9:
            r10 = 0
        L_0x04ea:
            r11 = 1
            r12 = r4[r11]     // Catch:{ all -> 0x04a2 }
            r11 = r6
            r43 = r10
            r6 = r12
            r10 = 0
            goto L_0x0508
        L_0x04f3:
            java.lang.String r10 = "PINNED_"
            boolean r10 = r2.startsWith(r10)     // Catch:{ all -> 0x1cd5 }
            if (r10 == 0) goto L_0x050a
            r10 = 0
            int r12 = (r37 > r10 ? 1 : (r37 == r10 ? 0 : -1))
            if (r12 == 0) goto L_0x0503
            r10 = 1
            goto L_0x0504
        L_0x0503:
            r10 = 0
        L_0x0504:
            r43 = r10
            r10 = 1
            r11 = 0
        L_0x0508:
            r12 = 0
            goto L_0x051b
        L_0x050a:
            java.lang.String r10 = "CHANNEL_"
            boolean r10 = r2.startsWith(r10)     // Catch:{ all -> 0x1cd5 }
            if (r10 == 0) goto L_0x0516
            r10 = 0
            r11 = 0
            r12 = 1
            goto L_0x0519
        L_0x0516:
            r10 = 0
            r11 = 0
            r12 = 0
        L_0x0519:
            r43 = 0
        L_0x051b:
            boolean r44 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1cd5 }
            if (r44 == 0) goto L_0x0548
            r44 = r6
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x04a2 }
            r6.<init>()     // Catch:{ all -> 0x04a2 }
            r45 = r5
            java.lang.String r5 = "GCM received message notification "
            r6.append(r5)     // Catch:{ all -> 0x04a2 }
            r6.append(r2)     // Catch:{ all -> 0x04a2 }
            r6.append(r9)     // Catch:{ all -> 0x04a2 }
            r6.append(r7)     // Catch:{ all -> 0x04a2 }
            java.lang.String r5 = " mid = "
            r6.append(r5)     // Catch:{ all -> 0x04a2 }
            r5 = r31
            r6.append(r5)     // Catch:{ all -> 0x04a2 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x04a2 }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x054e
        L_0x0548:
            r45 = r5
            r44 = r6
            r5 = r31
        L_0x054e:
            boolean r6 = r2.startsWith(r3)     // Catch:{ all -> 0x1cd5 }
            if (r6 != 0) goto L_0x1bd0
            boolean r6 = r2.startsWith(r15)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0566
            r1 = r51
            r46 = r3
            r48 = r11
            r47 = r12
            r17 = r15
            goto L_0x1bda
        L_0x0566:
            int r6 = r2.hashCode()     // Catch:{ all -> 0x04a2 }
            switch(r6) {
                case -2100047043: goto L_0x0ab7;
                case -2091498420: goto L_0x0aac;
                case -2053872415: goto L_0x0aa1;
                case -2039746363: goto L_0x0a96;
                case -2023218804: goto L_0x0a8b;
                case -1979538588: goto L_0x0a80;
                case -1979536003: goto L_0x0a75;
                case -1979535888: goto L_0x0a6a;
                case -1969004705: goto L_0x0a5f;
                case -1946699248: goto L_0x0a53;
                case -1717283471: goto L_0x0a47;
                case -1646640058: goto L_0x0a3b;
                case -1528047021: goto L_0x0a2f;
                case -1507149394: goto L_0x0a24;
                case -1493579426: goto L_0x0a18;
                case -1482481933: goto L_0x0a0c;
                case -1480102982: goto L_0x0a01;
                case -1478041834: goto L_0x09f5;
                case -1474543101: goto L_0x09ea;
                case -1465695932: goto L_0x09de;
                case -1374906292: goto L_0x09d2;
                case -1372940586: goto L_0x09c6;
                case -1264245338: goto L_0x09ba;
                case -1236154001: goto L_0x09ae;
                case -1236086700: goto L_0x09a2;
                case -1236077786: goto L_0x0996;
                case -1235796237: goto L_0x098a;
                case -1235760759: goto L_0x097e;
                case -1235686303: goto L_0x0973;
                case -1198046100: goto L_0x0968;
                case -1124254527: goto L_0x095c;
                case -1085137927: goto L_0x0950;
                case -1084856378: goto L_0x0944;
                case -1084820900: goto L_0x0938;
                case -1084746444: goto L_0x092c;
                case -819729482: goto L_0x0920;
                case -772141857: goto L_0x0914;
                case -638310039: goto L_0x0908;
                case -590403924: goto L_0x08fc;
                case -589196239: goto L_0x08f0;
                case -589193654: goto L_0x08e4;
                case -589193539: goto L_0x08d8;
                case -440169325: goto L_0x08cc;
                case -412748110: goto L_0x08c0;
                case -228518075: goto L_0x08b4;
                case -213586509: goto L_0x08a8;
                case -115582002: goto L_0x089c;
                case -112621464: goto L_0x0890;
                case -108522133: goto L_0x0884;
                case -107572034: goto L_0x0878;
                case -40534265: goto L_0x086c;
                case 52369421: goto L_0x0860;
                case 65254746: goto L_0x0854;
                case 141040782: goto L_0x0848;
                case 202550149: goto L_0x083c;
                case 309993049: goto L_0x0830;
                case 309995634: goto L_0x0824;
                case 309995749: goto L_0x0818;
                case 320532812: goto L_0x080c;
                case 328933854: goto L_0x0800;
                case 331340546: goto L_0x07f4;
                case 342406591: goto L_0x07e8;
                case 344816990: goto L_0x07dc;
                case 346878138: goto L_0x07d0;
                case 350376871: goto L_0x07c4;
                case 608430149: goto L_0x07b8;
                case 615714517: goto L_0x07ad;
                case 715508879: goto L_0x07a1;
                case 728985323: goto L_0x0795;
                case 731046471: goto L_0x0789;
                case 734545204: goto L_0x077d;
                case 802032552: goto L_0x0771;
                case 991498806: goto L_0x0765;
                case 1007364121: goto L_0x0759;
                case 1019850010: goto L_0x074d;
                case 1019917311: goto L_0x0741;
                case 1019926225: goto L_0x0735;
                case 1020207774: goto L_0x0729;
                case 1020243252: goto L_0x071d;
                case 1020317708: goto L_0x0711;
                case 1060282259: goto L_0x0705;
                case 1060349560: goto L_0x06f9;
                case 1060358474: goto L_0x06ed;
                case 1060640023: goto L_0x06e1;
                case 1060675501: goto L_0x06d5;
                case 1060749957: goto L_0x06ca;
                case 1073049781: goto L_0x06be;
                case 1078101399: goto L_0x06b2;
                case 1110103437: goto L_0x06a6;
                case 1160762272: goto L_0x069a;
                case 1172918249: goto L_0x068e;
                case 1234591620: goto L_0x0682;
                case 1281128640: goto L_0x0676;
                case 1281131225: goto L_0x066a;
                case 1281131340: goto L_0x065e;
                case 1310789062: goto L_0x0653;
                case 1333118583: goto L_0x0647;
                case 1361447897: goto L_0x063b;
                case 1498266155: goto L_0x062f;
                case 1533804208: goto L_0x0623;
                case 1540131626: goto L_0x0617;
                case 1547988151: goto L_0x060b;
                case 1561464595: goto L_0x05ff;
                case 1563525743: goto L_0x05f3;
                case 1567024476: goto L_0x05e7;
                case 1810705077: goto L_0x05db;
                case 1815177512: goto L_0x05cf;
                case 1954774321: goto L_0x05c3;
                case 1963241394: goto L_0x05b7;
                case 2014789757: goto L_0x05ab;
                case 2022049433: goto L_0x059f;
                case 2034984710: goto L_0x0593;
                case 2048733346: goto L_0x0587;
                case 2099392181: goto L_0x057b;
                case 2140162142: goto L_0x056f;
                default: goto L_0x056d;
            }     // Catch:{ all -> 0x04a2 }
        L_0x056d:
            goto L_0x0ac2
        L_0x056f:
            java.lang.String r6 = "CHAT_MESSAGE_GEOLIVE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 61
            goto L_0x0ac3
        L_0x057b:
            java.lang.String r6 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 44
            goto L_0x0ac3
        L_0x0587:
            java.lang.String r6 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 29
            goto L_0x0ac3
        L_0x0593:
            java.lang.String r6 = "CHANNEL_MESSAGE_PLAYLIST"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 46
            goto L_0x0ac3
        L_0x059f:
            java.lang.String r6 = "PINNED_CONTACT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 95
            goto L_0x0ac3
        L_0x05ab:
            java.lang.String r6 = "CHAT_PHOTO_EDITED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 69
            goto L_0x0ac3
        L_0x05b7:
            java.lang.String r6 = "LOCKED_MESSAGE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 109(0x6d, float:1.53E-43)
            goto L_0x0ac3
        L_0x05c3:
            java.lang.String r6 = "CHAT_MESSAGE_PLAYLIST"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 84
            goto L_0x0ac3
        L_0x05cf:
            java.lang.String r6 = "CHANNEL_MESSAGES"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 48
            goto L_0x0ac3
        L_0x05db:
            java.lang.String r6 = "MESSAGE_INVOICE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 22
            goto L_0x0ac3
        L_0x05e7:
            java.lang.String r6 = "CHAT_MESSAGE_VIDEO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 52
            goto L_0x0ac3
        L_0x05f3:
            java.lang.String r6 = "CHAT_MESSAGE_ROUND"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 53
            goto L_0x0ac3
        L_0x05ff:
            java.lang.String r6 = "CHAT_MESSAGE_PHOTO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 51
            goto L_0x0ac3
        L_0x060b:
            java.lang.String r6 = "CHAT_MESSAGE_AUDIO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 56
            goto L_0x0ac3
        L_0x0617:
            java.lang.String r6 = "MESSAGE_PLAYLIST"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 26
            goto L_0x0ac3
        L_0x0623:
            java.lang.String r6 = "MESSAGE_VIDEOS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 25
            goto L_0x0ac3
        L_0x062f:
            java.lang.String r6 = "PHONE_CALL_MISSED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 114(0x72, float:1.6E-43)
            goto L_0x0ac3
        L_0x063b:
            java.lang.String r6 = "MESSAGE_PHOTOS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 24
            goto L_0x0ac3
        L_0x0647:
            java.lang.String r6 = "CHAT_MESSAGE_VIDEOS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 83
            goto L_0x0ac3
        L_0x0653:
            java.lang.String r6 = "MESSAGE_NOTEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 3
            goto L_0x0ac3
        L_0x065e:
            java.lang.String r6 = "MESSAGE_GIF"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 18
            goto L_0x0ac3
        L_0x066a:
            java.lang.String r6 = "MESSAGE_GEO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 16
            goto L_0x0ac3
        L_0x0676:
            java.lang.String r6 = "MESSAGE_DOC"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 10
            goto L_0x0ac3
        L_0x0682:
            java.lang.String r6 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 64
            goto L_0x0ac3
        L_0x068e:
            java.lang.String r6 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 40
            goto L_0x0ac3
        L_0x069a:
            java.lang.String r6 = "CHAT_MESSAGE_PHOTOS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 82
            goto L_0x0ac3
        L_0x06a6:
            java.lang.String r6 = "CHAT_MESSAGE_NOTEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 50
            goto L_0x0ac3
        L_0x06b2:
            java.lang.String r6 = "CHAT_TITLE_EDITED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 68
            goto L_0x0ac3
        L_0x06be:
            java.lang.String r6 = "PINNED_NOTEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 88
            goto L_0x0ac3
        L_0x06ca:
            java.lang.String r6 = "MESSAGE_TEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 1
            goto L_0x0ac3
        L_0x06d5:
            java.lang.String r6 = "MESSAGE_QUIZ"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 14
            goto L_0x0ac3
        L_0x06e1:
            java.lang.String r6 = "MESSAGE_POLL"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 15
            goto L_0x0ac3
        L_0x06ed:
            java.lang.String r6 = "MESSAGE_GAME"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 19
            goto L_0x0ac3
        L_0x06f9:
            java.lang.String r6 = "MESSAGE_FWDS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 23
            goto L_0x0ac3
        L_0x0705:
            java.lang.String r6 = "MESSAGE_DOCS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 27
            goto L_0x0ac3
        L_0x0711:
            java.lang.String r6 = "CHAT_MESSAGE_TEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 49
            goto L_0x0ac3
        L_0x071d:
            java.lang.String r6 = "CHAT_MESSAGE_QUIZ"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 58
            goto L_0x0ac3
        L_0x0729:
            java.lang.String r6 = "CHAT_MESSAGE_POLL"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 59
            goto L_0x0ac3
        L_0x0735:
            java.lang.String r6 = "CHAT_MESSAGE_GAME"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 63
            goto L_0x0ac3
        L_0x0741:
            java.lang.String r6 = "CHAT_MESSAGE_FWDS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 81
            goto L_0x0ac3
        L_0x074d:
            java.lang.String r6 = "CHAT_MESSAGE_DOCS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 85
            goto L_0x0ac3
        L_0x0759:
            java.lang.String r6 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 21
            goto L_0x0ac3
        L_0x0765:
            java.lang.String r6 = "PINNED_GEOLIVE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 99
            goto L_0x0ac3
        L_0x0771:
            java.lang.String r6 = "MESSAGE_CONTACT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 13
            goto L_0x0ac3
        L_0x077d:
            java.lang.String r6 = "PINNED_VIDEO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 90
            goto L_0x0ac3
        L_0x0789:
            java.lang.String r6 = "PINNED_ROUND"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 91
            goto L_0x0ac3
        L_0x0795:
            java.lang.String r6 = "PINNED_PHOTO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 89
            goto L_0x0ac3
        L_0x07a1:
            java.lang.String r6 = "PINNED_AUDIO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 94
            goto L_0x0ac3
        L_0x07ad:
            java.lang.String r6 = "MESSAGE_PHOTO_SECRET"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 5
            goto L_0x0ac3
        L_0x07b8:
            java.lang.String r6 = "CHAT_VOICECHAT_INVITE_YOU"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 74
            goto L_0x0ac3
        L_0x07c4:
            java.lang.String r6 = "CHANNEL_MESSAGE_VIDEO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 31
            goto L_0x0ac3
        L_0x07d0:
            java.lang.String r6 = "CHANNEL_MESSAGE_ROUND"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 32
            goto L_0x0ac3
        L_0x07dc:
            java.lang.String r6 = "CHANNEL_MESSAGE_PHOTO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 30
            goto L_0x0ac3
        L_0x07e8:
            java.lang.String r6 = "CHAT_VOICECHAT_END"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 73
            goto L_0x0ac3
        L_0x07f4:
            java.lang.String r6 = "CHANNEL_MESSAGE_AUDIO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 35
            goto L_0x0ac3
        L_0x0800:
            java.lang.String r6 = "CHAT_MESSAGE_STICKER"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 55
            goto L_0x0ac3
        L_0x080c:
            java.lang.String r6 = "MESSAGES"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 28
            goto L_0x0ac3
        L_0x0818:
            java.lang.String r6 = "CHAT_MESSAGE_GIF"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 62
            goto L_0x0ac3
        L_0x0824:
            java.lang.String r6 = "CHAT_MESSAGE_GEO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 60
            goto L_0x0ac3
        L_0x0830:
            java.lang.String r6 = "CHAT_MESSAGE_DOC"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 54
            goto L_0x0ac3
        L_0x083c:
            java.lang.String r6 = "CHAT_VOICECHAT_INVITE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 72
            goto L_0x0ac3
        L_0x0848:
            java.lang.String r6 = "CHAT_LEFT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 77
            goto L_0x0ac3
        L_0x0854:
            java.lang.String r6 = "CHAT_ADD_YOU"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 67
            goto L_0x0ac3
        L_0x0860:
            java.lang.String r6 = "REACT_TEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 105(0x69, float:1.47E-43)
            goto L_0x0ac3
        L_0x086c:
            java.lang.String r6 = "CHAT_DELETE_MEMBER"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 75
            goto L_0x0ac3
        L_0x0878:
            java.lang.String r6 = "MESSAGE_SCREENSHOT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 8
            goto L_0x0ac3
        L_0x0884:
            java.lang.String r6 = "AUTH_REGION"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 108(0x6c, float:1.51E-43)
            goto L_0x0ac3
        L_0x0890:
            java.lang.String r6 = "CONTACT_JOINED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 106(0x6a, float:1.49E-43)
            goto L_0x0ac3
        L_0x089c:
            java.lang.String r6 = "CHAT_MESSAGE_INVOICE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 65
            goto L_0x0ac3
        L_0x08a8:
            java.lang.String r6 = "ENCRYPTION_REQUEST"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 110(0x6e, float:1.54E-43)
            goto L_0x0ac3
        L_0x08b4:
            java.lang.String r6 = "MESSAGE_GEOLIVE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 17
            goto L_0x0ac3
        L_0x08c0:
            java.lang.String r6 = "CHAT_DELETE_YOU"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 76
            goto L_0x0ac3
        L_0x08cc:
            java.lang.String r6 = "AUTH_UNKNOWN"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 107(0x6b, float:1.5E-43)
            goto L_0x0ac3
        L_0x08d8:
            java.lang.String r6 = "PINNED_GIF"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 103(0x67, float:1.44E-43)
            goto L_0x0ac3
        L_0x08e4:
            java.lang.String r6 = "PINNED_GEO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 98
            goto L_0x0ac3
        L_0x08f0:
            java.lang.String r6 = "PINNED_DOC"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 92
            goto L_0x0ac3
        L_0x08fc:
            java.lang.String r6 = "PINNED_GAME_SCORE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 101(0x65, float:1.42E-43)
            goto L_0x0ac3
        L_0x0908:
            java.lang.String r6 = "CHANNEL_MESSAGE_STICKER"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 34
            goto L_0x0ac3
        L_0x0914:
            java.lang.String r6 = "PHONE_CALL_REQUEST"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 112(0x70, float:1.57E-43)
            goto L_0x0ac3
        L_0x0920:
            java.lang.String r6 = "PINNED_STICKER"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 93
            goto L_0x0ac3
        L_0x092c:
            java.lang.String r6 = "PINNED_TEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 87
            goto L_0x0ac3
        L_0x0938:
            java.lang.String r6 = "PINNED_QUIZ"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 96
            goto L_0x0ac3
        L_0x0944:
            java.lang.String r6 = "PINNED_POLL"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 97
            goto L_0x0ac3
        L_0x0950:
            java.lang.String r6 = "PINNED_GAME"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 100
            goto L_0x0ac3
        L_0x095c:
            java.lang.String r6 = "CHAT_MESSAGE_CONTACT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 57
            goto L_0x0ac3
        L_0x0968:
            java.lang.String r6 = "MESSAGE_VIDEO_SECRET"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 7
            goto L_0x0ac3
        L_0x0973:
            java.lang.String r6 = "CHANNEL_MESSAGE_TEXT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 2
            goto L_0x0ac3
        L_0x097e:
            java.lang.String r6 = "CHANNEL_MESSAGE_QUIZ"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 37
            goto L_0x0ac3
        L_0x098a:
            java.lang.String r6 = "CHANNEL_MESSAGE_POLL"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 38
            goto L_0x0ac3
        L_0x0996:
            java.lang.String r6 = "CHANNEL_MESSAGE_GAME"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 42
            goto L_0x0ac3
        L_0x09a2:
            java.lang.String r6 = "CHANNEL_MESSAGE_FWDS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 43
            goto L_0x0ac3
        L_0x09ae:
            java.lang.String r6 = "CHANNEL_MESSAGE_DOCS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 47
            goto L_0x0ac3
        L_0x09ba:
            java.lang.String r6 = "PINNED_INVOICE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 102(0x66, float:1.43E-43)
            goto L_0x0ac3
        L_0x09c6:
            java.lang.String r6 = "CHAT_RETURNED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 78
            goto L_0x0ac3
        L_0x09d2:
            java.lang.String r6 = "ENCRYPTED_MESSAGE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 104(0x68, float:1.46E-43)
            goto L_0x0ac3
        L_0x09de:
            java.lang.String r6 = "ENCRYPTION_ACCEPT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 111(0x6f, float:1.56E-43)
            goto L_0x0ac3
        L_0x09ea:
            java.lang.String r6 = "MESSAGE_VIDEO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 6
            goto L_0x0ac3
        L_0x09f5:
            java.lang.String r6 = "MESSAGE_ROUND"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 9
            goto L_0x0ac3
        L_0x0a01:
            java.lang.String r6 = "MESSAGE_PHOTO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 4
            goto L_0x0ac3
        L_0x0a0c:
            java.lang.String r6 = "MESSAGE_MUTED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 113(0x71, float:1.58E-43)
            goto L_0x0ac3
        L_0x0a18:
            java.lang.String r6 = "MESSAGE_AUDIO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 12
            goto L_0x0ac3
        L_0x0a24:
            java.lang.String r6 = "MESSAGE_RECURRING_PAY"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 0
            goto L_0x0ac3
        L_0x0a2f:
            java.lang.String r6 = "CHAT_MESSAGES"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 86
            goto L_0x0ac3
        L_0x0a3b:
            java.lang.String r6 = "CHAT_VOICECHAT_START"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 71
            goto L_0x0ac3
        L_0x0a47:
            java.lang.String r6 = "CHAT_REQ_JOINED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 80
            goto L_0x0ac3
        L_0x0a53:
            java.lang.String r6 = "CHAT_JOINED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 79
            goto L_0x0ac3
        L_0x0a5f:
            java.lang.String r6 = "CHAT_ADD_MEMBER"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 70
            goto L_0x0ac3
        L_0x0a6a:
            java.lang.String r6 = "CHANNEL_MESSAGE_GIF"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 41
            goto L_0x0ac3
        L_0x0a75:
            java.lang.String r6 = "CHANNEL_MESSAGE_GEO"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 39
            goto L_0x0ac3
        L_0x0a80:
            java.lang.String r6 = "CHANNEL_MESSAGE_DOC"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 33
            goto L_0x0ac3
        L_0x0a8b:
            java.lang.String r6 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 45
            goto L_0x0ac3
        L_0x0a96:
            java.lang.String r6 = "MESSAGE_STICKER"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 11
            goto L_0x0ac3
        L_0x0aa1:
            java.lang.String r6 = "CHAT_CREATED"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 66
            goto L_0x0ac3
        L_0x0aac:
            java.lang.String r6 = "CHANNEL_MESSAGE_CONTACT"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 36
            goto L_0x0ac3
        L_0x0ab7:
            java.lang.String r6 = "MESSAGE_GAME_SCORE"
            boolean r6 = r2.equals(r6)     // Catch:{ all -> 0x04a2 }
            if (r6 == 0) goto L_0x0ac2
            r6 = 20
            goto L_0x0ac3
        L_0x0ac2:
            r6 = -1
        L_0x0ac3:
            java.lang.String r9 = " "
            r17 = r15
            java.lang.String r15 = "NotificationGroupFew"
            r46 = r3
            java.lang.String r3 = "NotificationMessageFew"
            r47 = r12
            java.lang.String r12 = "ChannelMessageFew"
            r48 = r11
            java.lang.String r11 = "AttachSticker"
            switch(r6) {
                case 0: goto L_0x1b93;
                case 1: goto L_0x1b7a;
                case 2: goto L_0x1b7a;
                case 3: goto L_0x1b5b;
                case 4: goto L_0x1b40;
                case 5: goto L_0x1b25;
                case 6: goto L_0x1b0a;
                case 7: goto L_0x1aef;
                case 8: goto L_0x1ad8;
                case 9: goto L_0x1abc;
                case 10: goto L_0x1aa0;
                case 11: goto L_0x1a47;
                case 12: goto L_0x1a2b;
                case 13: goto L_0x1a0a;
                case 14: goto L_0x19e9;
                case 15: goto L_0x19c8;
                case 16: goto L_0x19ac;
                case 17: goto L_0x1990;
                case 18: goto L_0x1974;
                case 19: goto L_0x1953;
                case 20: goto L_0x1936;
                case 21: goto L_0x1936;
                case 22: goto L_0x1915;
                case 23: goto L_0x18ed;
                case 24: goto L_0x18c7;
                case 25: goto L_0x18a2;
                case 26: goto L_0x187d;
                case 27: goto L_0x1858;
                case 28: goto L_0x1844;
                case 29: goto L_0x1828;
                case 30: goto L_0x180c;
                case 31: goto L_0x17f0;
                case 32: goto L_0x17d4;
                case 33: goto L_0x17b8;
                case 34: goto L_0x175f;
                case 35: goto L_0x1743;
                case 36: goto L_0x1722;
                case 37: goto L_0x1701;
                case 38: goto L_0x16e0;
                case 39: goto L_0x16c4;
                case 40: goto L_0x16a8;
                case 41: goto L_0x168c;
                case 42: goto L_0x1670;
                case 43: goto L_0x1645;
                case 44: goto L_0x161f;
                case 45: goto L_0x15f9;
                case 46: goto L_0x15d3;
                case 47: goto L_0x15ad;
                case 48: goto L_0x159a;
                case 49: goto L_0x157b;
                case 50: goto L_0x155a;
                case 51: goto L_0x1539;
                case 52: goto L_0x1518;
                case 53: goto L_0x14f7;
                case 54: goto L_0x14d6;
                case 55: goto L_0x145f;
                case 56: goto L_0x143a;
                case 57: goto L_0x1414;
                case 58: goto L_0x13ee;
                case 59: goto L_0x13c8;
                case 60: goto L_0x13a6;
                case 61: goto L_0x1384;
                case 62: goto L_0x1362;
                case 63: goto L_0x133b;
                case 64: goto L_0x1319;
                case 65: goto L_0x12f2;
                case 66: goto L_0x12d7;
                case 67: goto L_0x12d7;
                case 68: goto L_0x12bf;
                case 69: goto L_0x12a7;
                case 70: goto L_0x128a;
                case 71: goto L_0x1272;
                case 72: goto L_0x1254;
                case 73: goto L_0x123b;
                case 74: goto L_0x1222;
                case 75: goto L_0x1209;
                case 76: goto L_0x11f0;
                case 77: goto L_0x11d7;
                case 78: goto L_0x11be;
                case 79: goto L_0x11a5;
                case 80: goto L_0x118c;
                case 81: goto L_0x115d;
                case 82: goto L_0x1130;
                case 83: goto L_0x1103;
                case 84: goto L_0x10d7;
                case 85: goto L_0x10ab;
                case 86: goto L_0x108f;
                case 87: goto L_0x1039;
                case 88: goto L_0x0fed;
                case 89: goto L_0x0fa1;
                case 90: goto L_0x0var_;
                case 91: goto L_0x0var_;
                case 92: goto L_0x0ebd;
                case 93: goto L_0x0e05;
                case 94: goto L_0x0db9;
                case 95: goto L_0x0d63;
                case 96: goto L_0x0d0d;
                case 97: goto L_0x0cb8;
                case 98: goto L_0x0c6d;
                case 99: goto L_0x0CLASSNAME;
                case 100: goto L_0x0bd7;
                case 101: goto L_0x0b8c;
                case 102: goto L_0x0b41;
                case 103: goto L_0x0af6;
                case 104: goto L_0x0adc;
                case 105: goto L_0x1bc9;
                case 106: goto L_0x1bc9;
                case 107: goto L_0x1bc9;
                case 108: goto L_0x1bc9;
                case 109: goto L_0x1bc9;
                case 110: goto L_0x1bc9;
                case 111: goto L_0x1bc9;
                case 112: goto L_0x1bc9;
                case 113: goto L_0x1bc9;
                case 114: goto L_0x1bc9;
                default: goto L_0x0ad8;
            }
        L_0x0ad8:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x04a2 }
            goto L_0x1bb3
        L_0x0adc:
            java.lang.String r1 = "YouHaveNewMessage"
            r3 = 2131629266(0x7f0e14d2, float:1.8885848E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r3)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "SecretChatName"
            r4 = 2131628143(0x7f0e106f, float:1.888357E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            r4 = 1
            r22 = 0
            r44 = r3
        L_0x0af3:
            r3 = r1
            goto L_0x1bcd
        L_0x0af6:
            r11 = 0
            int r3 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r3 <= 0) goto L_0x0b14
            java.lang.String r1 = "NotificationActionPinnedGifUser"
            r3 = 2131626904(0x7f0e0b98, float:1.8881057E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0b14:
            if (r1 == 0) goto L_0x0b2e
            java.lang.String r1 = "NotificationActionPinnedGif"
            r3 = 2131626902(0x7f0e0b96, float:1.8881053E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0b2e:
            java.lang.String r1 = "NotificationActionPinnedGifChannel"
            r3 = 2131626903(0x7f0e0b97, float:1.8881055E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0b41:
            r11 = 0
            int r3 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r3 <= 0) goto L_0x0b5f
            java.lang.String r1 = "NotificationActionPinnedInvoiceUser"
            r3 = 2131626907(0x7f0e0b9b, float:1.8881063E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0b5f:
            if (r1 == 0) goto L_0x0b79
            java.lang.String r1 = "NotificationActionPinnedInvoice"
            r3 = 2131626905(0x7f0e0b99, float:1.888106E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0b79:
            java.lang.String r1 = "NotificationActionPinnedInvoiceChannel"
            r3 = 2131626906(0x7f0e0b9a, float:1.8881061E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0b8c:
            r11 = 0
            int r3 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r3 <= 0) goto L_0x0baa
            java.lang.String r1 = "NotificationActionPinnedGameScoreUser"
            r3 = 2131626894(0x7f0e0b8e, float:1.8881037E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0baa:
            if (r1 == 0) goto L_0x0bc4
            java.lang.String r1 = "NotificationActionPinnedGameScore"
            r3 = 2131626892(0x7f0e0b8c, float:1.8881033E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0bc4:
            java.lang.String r1 = "NotificationActionPinnedGameScoreChannel"
            r3 = 2131626893(0x7f0e0b8d, float:1.8881035E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0bd7:
            r11 = 0
            int r3 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r3 <= 0) goto L_0x0bf5
            java.lang.String r1 = "NotificationActionPinnedGameUser"
            r3 = 2131626895(0x7f0e0b8f, float:1.888104E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0bf5:
            if (r1 == 0) goto L_0x0c0f
            java.lang.String r1 = "NotificationActionPinnedGame"
            r3 = 2131626890(0x7f0e0b8a, float:1.8881029E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0c0f:
            java.lang.String r1 = "NotificationActionPinnedGameChannel"
            r3 = 2131626891(0x7f0e0b8b, float:1.888103E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0CLASSNAME:
            r11 = 0
            int r3 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r3 <= 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGeoLiveUser"
            r3 = 2131626900(0x7f0e0b94, float:1.888105E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0CLASSNAME:
            if (r1 == 0) goto L_0x0c5a
            java.lang.String r1 = "NotificationActionPinnedGeoLive"
            r3 = 2131626898(0x7f0e0b92, float:1.8881045E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0c5a:
            java.lang.String r1 = "NotificationActionPinnedGeoLiveChannel"
            r3 = 2131626899(0x7f0e0b93, float:1.8881047E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0c6d:
            r11 = 0
            int r3 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r3 <= 0) goto L_0x0c8b
            java.lang.String r1 = "NotificationActionPinnedGeoUser"
            r3 = 2131626901(0x7f0e0b95, float:1.8881051E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0c8b:
            if (r1 == 0) goto L_0x0ca5
            java.lang.String r1 = "NotificationActionPinnedGeo"
            r3 = 2131626896(0x7f0e0b90, float:1.8881041E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0ca5:
            java.lang.String r1 = "NotificationActionPinnedGeoChannel"
            r3 = 2131626897(0x7f0e0b91, float:1.8881043E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0cb8:
            r11 = 0
            int r3 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r3 <= 0) goto L_0x0cd6
            java.lang.String r1 = "NotificationActionPinnedPollUser"
            r3 = 2131626919(0x7f0e0ba7, float:1.8881088E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0cd6:
            if (r1 == 0) goto L_0x0cf5
            java.lang.String r1 = "NotificationActionPinnedPoll2"
            r3 = 2131626917(0x7f0e0ba5, float:1.8881084E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 2
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r12 = 1
            r6[r12] = r11     // Catch:{ all -> 0x04a2 }
            r4 = r4[r12]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0cf5:
            java.lang.String r1 = "NotificationActionPinnedPollChannel2"
            r3 = 2131626918(0x7f0e0ba6, float:1.8881086E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x0d0d:
            r8 = r7
            r11 = 0
            int r3 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r3 <= 0) goto L_0x0d2c
            java.lang.String r1 = "NotificationActionPinnedQuizUser"
            r3 = 2131626922(0x7f0e0baa, float:1.8881094E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0d2c:
            if (r1 == 0) goto L_0x0d4b
            java.lang.String r1 = "NotificationActionPinnedQuiz2"
            r3 = 2131626920(0x7f0e0ba8, float:1.888109E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 2
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r12 = 1
            r6[r12] = r11     // Catch:{ all -> 0x04a2 }
            r4 = r4[r12]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0d4b:
            java.lang.String r1 = "NotificationActionPinnedQuizChannel2"
            r3 = 2131626921(0x7f0e0ba9, float:1.8881092E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0d63:
            r8 = r7
            r11 = 0
            int r3 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r3 <= 0) goto L_0x0d82
            java.lang.String r1 = "NotificationActionPinnedContactUser"
            r3 = 2131626886(0x7f0e0b86, float:1.888102E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0d82:
            if (r1 == 0) goto L_0x0da1
            java.lang.String r1 = "NotificationActionPinnedContact2"
            r3 = 2131626884(0x7f0e0b84, float:1.8881017E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 2
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r12 = 1
            r6[r12] = r11     // Catch:{ all -> 0x04a2 }
            r4 = r4[r12]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0da1:
            java.lang.String r1 = "NotificationActionPinnedContactChannel2"
            r3 = 2131626885(0x7f0e0b85, float:1.8881019E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0db9:
            r8 = r7
            r6 = 0
            int r3 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x0dd8
            java.lang.String r1 = "NotificationActionPinnedVoiceUser"
            r3 = 2131626940(0x7f0e0bbc, float:1.888113E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0dd8:
            if (r1 == 0) goto L_0x0df2
            java.lang.String r1 = "NotificationActionPinnedVoice"
            r3 = 2131626938(0x7f0e0bba, float:1.8881126E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0df2:
            java.lang.String r1 = "NotificationActionPinnedVoiceChannel"
            r3 = 2131626939(0x7f0e0bbb, float:1.8881128E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r7[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0e05:
            r8 = r7
            r11 = 0
            int r3 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r3 <= 0) goto L_0x0e43
            int r1 = r4.length     // Catch:{ all -> 0x04a2 }
            r3 = 1
            if (r1 <= r3) goto L_0x0e30
            r1 = r4[r3]     // Catch:{ all -> 0x04a2 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x04a2 }
            if (r1 != 0) goto L_0x0e30
            java.lang.String r1 = "NotificationActionPinnedStickerEmojiUser"
            r3 = 2131626930(0x7f0e0bb2, float:1.888111E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0e30:
            java.lang.String r1 = "NotificationActionPinnedStickerUser"
            r3 = 2131626931(0x7f0e0bb3, float:1.8881112E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r7[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0e43:
            if (r1 == 0) goto L_0x0e86
            int r1 = r4.length     // Catch:{ all -> 0x04a2 }
            r3 = 2
            if (r1 <= r3) goto L_0x0e6e
            r1 = r4[r3]     // Catch:{ all -> 0x04a2 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x04a2 }
            if (r1 != 0) goto L_0x0e6e
            java.lang.String r1 = "NotificationActionPinnedStickerEmoji"
            r3 = 2131626928(0x7f0e0bb0, float:1.8881106E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 2
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r12 = 1
            r6[r12] = r11     // Catch:{ all -> 0x04a2 }
            r4 = r4[r12]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0e6e:
            java.lang.String r1 = "NotificationActionPinnedSticker"
            r3 = 2131626926(0x7f0e0bae, float:1.8881102E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0e86:
            int r1 = r4.length     // Catch:{ all -> 0x04a2 }
            r3 = 1
            if (r1 <= r3) goto L_0x0eaa
            r1 = r4[r3]     // Catch:{ all -> 0x04a2 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x04a2 }
            if (r1 != 0) goto L_0x0eaa
            java.lang.String r1 = "NotificationActionPinnedStickerEmojiChannel"
            r3 = 2131626929(0x7f0e0bb1, float:1.8881108E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0eaa:
            java.lang.String r1 = "NotificationActionPinnedStickerChannel"
            r3 = 2131626927(0x7f0e0baf, float:1.8881104E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r7[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0ebd:
            r8 = r7
            r6 = 0
            int r3 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x0edc
            java.lang.String r1 = "NotificationActionPinnedFileUser"
            r3 = 2131626889(0x7f0e0b89, float:1.8881027E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0edc:
            if (r1 == 0) goto L_0x0ef6
            java.lang.String r1 = "NotificationActionPinnedFile"
            r3 = 2131626887(0x7f0e0b87, float:1.8881023E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0ef6:
            java.lang.String r1 = "NotificationActionPinnedFileChannel"
            r3 = 2131626888(0x7f0e0b88, float:1.8881025E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r7[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0var_:
            r8 = r7
            r6 = 0
            int r3 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedRoundUser"
            r3 = 2131626925(0x7f0e0bad, float:1.88811E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0var_:
            if (r1 == 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedRound"
            r3 = 2131626923(0x7f0e0bab, float:1.8881096E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0var_:
            java.lang.String r1 = "NotificationActionPinnedRoundChannel"
            r3 = 2131626924(0x7f0e0bac, float:1.8881098E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r7[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0var_:
            r8 = r7
            r6 = 0
            int r3 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedVideoUser"
            r3 = 2131626937(0x7f0e0bb9, float:1.8881124E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0var_:
            if (r1 == 0) goto L_0x0f8e
            java.lang.String r1 = "NotificationActionPinnedVideo"
            r3 = 2131626935(0x7f0e0bb7, float:1.888112E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0f8e:
            java.lang.String r1 = "NotificationActionPinnedVideoChannel"
            r3 = 2131626936(0x7f0e0bb8, float:1.8881122E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r7[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0fa1:
            r8 = r7
            r6 = 0
            int r3 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x0fc0
            java.lang.String r1 = "NotificationActionPinnedPhotoUser"
            r3 = 2131626916(0x7f0e0ba4, float:1.8881082E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0fc0:
            if (r1 == 0) goto L_0x0fda
            java.lang.String r1 = "NotificationActionPinnedPhoto"
            r3 = 2131626914(0x7f0e0ba2, float:1.8881078E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0fda:
            java.lang.String r1 = "NotificationActionPinnedPhotoChannel"
            r3 = 2131626915(0x7f0e0ba3, float:1.888108E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r7[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x0fed:
            r8 = r7
            r6 = 0
            int r3 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x100c
            java.lang.String r1 = "NotificationActionPinnedNoTextUser"
            r3 = 2131626913(0x7f0e0ba1, float:1.8881076E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x100c:
            if (r1 == 0) goto L_0x1026
            java.lang.String r1 = "NotificationActionPinnedNoText"
            r3 = 2131626911(0x7f0e0b9f, float:1.8881072E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x1026:
            java.lang.String r1 = "NotificationActionPinnedNoTextChannel"
            r3 = 2131626912(0x7f0e0ba0, float:1.8881074E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r7[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r7)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x1039:
            r8 = r7
            r11 = 0
            int r3 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r3 <= 0) goto L_0x1058
            java.lang.String r1 = "NotificationActionPinnedTextUser"
            r3 = 2131626934(0x7f0e0bb6, float:1.8881118E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x1058:
            if (r1 == 0) goto L_0x1077
            java.lang.String r1 = "NotificationActionPinnedText"
            r3 = 2131626932(0x7f0e0bb4, float:1.8881114E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x1077:
            java.lang.String r1 = "NotificationActionPinnedTextChannel"
            r3 = 2131626933(0x7f0e0bb5, float:1.8881116E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x108f:
            r8 = r7
            java.lang.String r1 = "NotificationGroupAlbum"
            r3 = 2131626949(0x7f0e0bc5, float:1.8881149E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
        L_0x10a6:
            r3 = r1
            r7 = r8
            r4 = 1
            goto L_0x1bcb
        L_0x10ab:
            r8 = r7
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a2 }
            r3 = 0
            r6 = r4[r3]     // Catch:{ all -> 0x04a2 }
            r1[r3] = r6     // Catch:{ all -> 0x04a2 }
            r3 = 1
            r6 = r4[r3]     // Catch:{ all -> 0x04a2 }
            r1[r3] = r6     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "Files"
            r6 = 2
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4)     // Catch:{ all -> 0x04a2 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a2 }
            r7 = 0
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r4, r11)     // Catch:{ all -> 0x04a2 }
            r1[r6] = r3     // Catch:{ all -> 0x04a2 }
            r3 = 2131626952(0x7f0e0bc8, float:1.8881155E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r15, r3, r1)     // Catch:{ all -> 0x04a2 }
            goto L_0x10a6
        L_0x10d7:
            r8 = r7
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a2 }
            r3 = 0
            r6 = r4[r3]     // Catch:{ all -> 0x04a2 }
            r1[r3] = r6     // Catch:{ all -> 0x04a2 }
            r3 = 1
            r6 = r4[r3]     // Catch:{ all -> 0x04a2 }
            r1[r3] = r6     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "MusicFiles"
            r6 = 2
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4)     // Catch:{ all -> 0x04a2 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a2 }
            r7 = 0
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r4, r11)     // Catch:{ all -> 0x04a2 }
            r1[r6] = r3     // Catch:{ all -> 0x04a2 }
            r3 = 2131626952(0x7f0e0bc8, float:1.8881155E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r15, r3, r1)     // Catch:{ all -> 0x04a2 }
            goto L_0x10a6
        L_0x1103:
            r8 = r7
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a2 }
            r3 = 0
            r6 = r4[r3]     // Catch:{ all -> 0x04a2 }
            r1[r3] = r6     // Catch:{ all -> 0x04a2 }
            r3 = 1
            r6 = r4[r3]     // Catch:{ all -> 0x04a2 }
            r1[r3] = r6     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "Videos"
            r6 = 2
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4)     // Catch:{ all -> 0x04a2 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a2 }
            r7 = 0
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r4, r11)     // Catch:{ all -> 0x04a2 }
            r1[r6] = r3     // Catch:{ all -> 0x04a2 }
            r3 = 2131626952(0x7f0e0bc8, float:1.8881155E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r15, r3, r1)     // Catch:{ all -> 0x04a2 }
            goto L_0x10a6
        L_0x1130:
            r8 = r7
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a2 }
            r3 = 0
            r6 = r4[r3]     // Catch:{ all -> 0x04a2 }
            r1[r3] = r6     // Catch:{ all -> 0x04a2 }
            r3 = 1
            r6 = r4[r3]     // Catch:{ all -> 0x04a2 }
            r1[r3] = r6     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "Photos"
            r6 = 2
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4)     // Catch:{ all -> 0x04a2 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a2 }
            r7 = 0
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r4, r11)     // Catch:{ all -> 0x04a2 }
            r1[r6] = r3     // Catch:{ all -> 0x04a2 }
            r3 = 2131626952(0x7f0e0bc8, float:1.8881155E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r15, r3, r1)     // Catch:{ all -> 0x04a2 }
            goto L_0x10a6
        L_0x115d:
            r8 = r7
            java.lang.String r1 = "NotificationGroupForwardedFew"
            r3 = 2131626953(0x7f0e0bc9, float:1.8881157E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4)     // Catch:{ all -> 0x04a2 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a2 }
            r11 = 0
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x04a2 }
            r11 = r28
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r11, r4, r12)     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x10a6
        L_0x118c:
            r8 = r7
            java.lang.String r1 = "UserAcceptedToGroupPushWithGroup"
            r3 = 2131628797(0x7f0e12fd, float:1.8884897E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x11a5:
            r8 = r7
            java.lang.String r1 = "NotificationGroupAddSelfMega"
            r3 = 2131626948(0x7f0e0bc4, float:1.8881147E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x11be:
            r8 = r7
            java.lang.String r1 = "NotificationGroupAddSelf"
            r3 = 2131626947(0x7f0e0bc3, float:1.8881145E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x11d7:
            r8 = r7
            java.lang.String r1 = "NotificationGroupLeftMember"
            r3 = 2131626958(0x7f0e0bce, float:1.8881167E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x11f0:
            r8 = r7
            java.lang.String r1 = "NotificationGroupKickYou"
            r3 = 2131626957(0x7f0e0bcd, float:1.8881165E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x1209:
            r8 = r7
            java.lang.String r1 = "NotificationGroupKickMember"
            r3 = 2131626956(0x7f0e0bcc, float:1.8881163E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x1222:
            r8 = r7
            java.lang.String r1 = "NotificationGroupInvitedYouToCall"
            r3 = 2131626955(0x7f0e0bcb, float:1.888116E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x123b:
            r8 = r7
            java.lang.String r1 = "NotificationGroupEndedCall"
            r3 = 2131626951(0x7f0e0bc7, float:1.8881153E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x1254:
            r8 = r7
            java.lang.String r1 = "NotificationGroupInvitedToCall"
            r3 = 2131626954(0x7f0e0bca, float:1.8881159E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x1272:
            r8 = r7
            java.lang.String r1 = "NotificationGroupCreatedCall"
            r3 = 2131626950(0x7f0e0bc6, float:1.888115E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x128a:
            r8 = r7
            java.lang.String r1 = "NotificationGroupAddMember"
            r3 = 2131626946(0x7f0e0bc2, float:1.8881143E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x12a7:
            r8 = r7
            java.lang.String r1 = "NotificationEditedGroupPhoto"
            r3 = 2131626944(0x7f0e0bc0, float:1.8881138E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x12bf:
            r8 = r7
            java.lang.String r1 = "NotificationEditedGroupName"
            r3 = 2131626943(0x7f0e0bbf, float:1.8881136E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x12d7:
            r8 = r7
            java.lang.String r1 = "NotificationInvitedToGroup"
            r3 = 2131626963(0x7f0e0bd3, float:1.8881177E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
        L_0x12ee:
            r3 = r1
            r7 = r8
            goto L_0x1bca
        L_0x12f2:
            r8 = r7
            java.lang.String r1 = "NotificationMessageGroupInvoice"
            r3 = 2131626980(0x7f0e0be4, float:1.8881212E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "PaymentInvoice"
            r4 = 2131627390(0x7f0e0d7e, float:1.8882043E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x145a
        L_0x1319:
            r8 = r7
            java.lang.String r1 = "NotificationMessageGroupGameScored"
            r3 = 2131626978(0x7f0e0be2, float:1.8881207E38)
            r6 = 4
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r11 = 0
            r12 = r4[r11]     // Catch:{ all -> 0x04a2 }
            r6[r11] = r12     // Catch:{ all -> 0x04a2 }
            r11 = 1
            r12 = r4[r11]     // Catch:{ all -> 0x04a2 }
            r6[r11] = r12     // Catch:{ all -> 0x04a2 }
            r11 = 2
            r12 = r4[r11]     // Catch:{ all -> 0x04a2 }
            r6[r11] = r12     // Catch:{ all -> 0x04a2 }
            r7 = 3
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x12ee
        L_0x133b:
            r8 = r7
            java.lang.String r1 = "NotificationMessageGroupGame"
            r3 = 2131626977(0x7f0e0be1, float:1.8881205E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachGame"
            r4 = 2131624480(0x7f0e0220, float:1.887614E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x145a
        L_0x1362:
            r8 = r7
            java.lang.String r1 = "NotificationMessageGroupGif"
            r3 = 2131626979(0x7f0e0be3, float:1.888121E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachGif"
            r4 = 2131624481(0x7f0e0221, float:1.8876143E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x145a
        L_0x1384:
            r8 = r7
            java.lang.String r1 = "NotificationMessageGroupLiveLocation"
            r3 = 2131626981(0x7f0e0be5, float:1.8881214E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachLiveLocation"
            r4 = 2131624486(0x7f0e0226, float:1.8876153E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x145a
        L_0x13a6:
            r8 = r7
            java.lang.String r1 = "NotificationMessageGroupMap"
            r3 = 2131626982(0x7f0e0be6, float:1.8881216E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachLocation"
            r4 = 2131624490(0x7f0e022a, float:1.8876161E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x145a
        L_0x13c8:
            r8 = r7
            java.lang.String r1 = "NotificationMessageGroupPoll2"
            r3 = 2131626986(0x7f0e0bea, float:1.8881224E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "Poll"
            r4 = 2131627574(0x7f0e0e36, float:1.8882416E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x145a
        L_0x13ee:
            r8 = r7
            java.lang.String r1 = "NotificationMessageGroupQuiz2"
            r3 = 2131626987(0x7f0e0beb, float:1.8881226E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "PollQuiz"
            r4 = 2131627581(0x7f0e0e3d, float:1.888243E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x145a
        L_0x1414:
            r8 = r7
            java.lang.String r1 = "NotificationMessageGroupContact2"
            r3 = 2131626975(0x7f0e0bdf, float:1.8881201E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 2
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachContact"
            r4 = 2131624476(0x7f0e021c, float:1.8876133E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x145a
        L_0x143a:
            r8 = r7
            java.lang.String r1 = "NotificationMessageGroupAudio"
            r3 = 2131626974(0x7f0e0bde, float:1.88812E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r7 = 0
            r11 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r11     // Catch:{ all -> 0x04a2 }
            r7 = 1
            r4 = r4[r7]     // Catch:{ all -> 0x04a2 }
            r6[r7] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachAudio"
            r4 = 2131624474(0x7f0e021a, float:1.8876129E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
        L_0x145a:
            r22 = r3
            r7 = r8
            goto L_0x1b77
        L_0x145f:
            int r1 = r4.length     // Catch:{ all -> 0x04a2 }
            r3 = 2
            if (r1 <= r3) goto L_0x14a3
            r1 = r4[r3]     // Catch:{ all -> 0x04a2 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x04a2 }
            if (r1 != 0) goto L_0x14a3
            java.lang.String r1 = "NotificationMessageGroupStickerEmoji"
            r3 = 2131626990(0x7f0e0bee, float:1.8881232E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r12 = 0
            r15 = r4[r12]     // Catch:{ all -> 0x04a2 }
            r6[r12] = r15     // Catch:{ all -> 0x04a2 }
            r12 = 1
            r15 = r4[r12]     // Catch:{ all -> 0x04a2 }
            r6[r12] = r15     // Catch:{ all -> 0x04a2 }
            r12 = 2
            r15 = r4[r12]     // Catch:{ all -> 0x04a2 }
            r6[r12] = r15     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x04a2 }
            r3.<init>()     // Catch:{ all -> 0x04a2 }
            r4 = r4[r12]     // Catch:{ all -> 0x04a2 }
            r3.append(r4)     // Catch:{ all -> 0x04a2 }
            r3.append(r9)     // Catch:{ all -> 0x04a2 }
            r4 = 2131624503(0x7f0e0237, float:1.8876188E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r11, r4)     // Catch:{ all -> 0x04a2 }
            r3.append(r4)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x14a3:
            java.lang.String r1 = "NotificationMessageGroupSticker"
            r3 = 2131626989(0x7f0e0bed, float:1.888123E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r12 = 0
            r15 = r4[r12]     // Catch:{ all -> 0x04a2 }
            r6[r12] = r15     // Catch:{ all -> 0x04a2 }
            r12 = 1
            r15 = r4[r12]     // Catch:{ all -> 0x04a2 }
            r6[r12] = r15     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x04a2 }
            r3.<init>()     // Catch:{ all -> 0x04a2 }
            r4 = r4[r12]     // Catch:{ all -> 0x04a2 }
            r3.append(r4)     // Catch:{ all -> 0x04a2 }
            r3.append(r9)     // Catch:{ all -> 0x04a2 }
            r4 = 2131624503(0x7f0e0237, float:1.8876188E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r11, r4)     // Catch:{ all -> 0x04a2 }
            r3.append(r4)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x14d6:
            java.lang.String r1 = "NotificationMessageGroupDocument"
            r3 = 2131626976(0x7f0e0be0, float:1.8881203E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachDocument"
            r4 = 2131624479(0x7f0e021f, float:1.8876139E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x14f7:
            java.lang.String r1 = "NotificationMessageGroupRound"
            r3 = 2131626988(0x7f0e0bec, float:1.8881228E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachRound"
            r4 = 2131624502(0x7f0e0236, float:1.8876186E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1518:
            java.lang.String r1 = "NotificationMessageGroupVideo"
            r3 = 2131626992(0x7f0e0bf0, float:1.8881236E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachVideo"
            r4 = 2131624506(0x7f0e023a, float:1.8876194E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1539:
            java.lang.String r1 = "NotificationMessageGroupPhoto"
            r3 = 2131626985(0x7f0e0be9, float:1.8881222E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachPhoto"
            r4 = 2131624500(0x7f0e0234, float:1.8876181E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x155a:
            java.lang.String r1 = "NotificationMessageGroupNoText"
            r3 = 2131626984(0x7f0e0be8, float:1.888122E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "Message"
            r4 = 2131626619(0x7f0e0a7b, float:1.888048E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x157b:
            java.lang.String r3 = "NotificationMessageGroupText"
            r6 = 2131626991(0x7f0e0bef, float:1.8881234E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r1[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r1[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 2
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r1[r9] = r11     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r6, r1)     // Catch:{ all -> 0x04a2 }
            r3 = r4[r9]     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x159a:
            java.lang.String r1 = "ChannelMessageAlbum"
            r3 = 2131624917(0x7f0e03d5, float:1.8877027E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            goto L_0x1855
        L_0x15ad:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a2 }
            r3 = 0
            r6 = r4[r3]     // Catch:{ all -> 0x04a2 }
            r1[r3] = r6     // Catch:{ all -> 0x04a2 }
            java.lang.String r6 = "Files"
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4)     // Catch:{ all -> 0x04a2 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a2 }
            java.lang.Object[] r11 = new java.lang.Object[r3]     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r6, r4, r11)     // Catch:{ all -> 0x04a2 }
            r1[r9] = r3     // Catch:{ all -> 0x04a2 }
            r3 = 2131624921(0x7f0e03d9, float:1.8877035E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r12, r3, r1)     // Catch:{ all -> 0x04a2 }
            goto L_0x1855
        L_0x15d3:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a2 }
            r3 = 0
            r6 = r4[r3]     // Catch:{ all -> 0x04a2 }
            r1[r3] = r6     // Catch:{ all -> 0x04a2 }
            java.lang.String r6 = "MusicFiles"
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4)     // Catch:{ all -> 0x04a2 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a2 }
            java.lang.Object[] r11 = new java.lang.Object[r3]     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r6, r4, r11)     // Catch:{ all -> 0x04a2 }
            r1[r9] = r3     // Catch:{ all -> 0x04a2 }
            r3 = 2131624921(0x7f0e03d9, float:1.8877035E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r12, r3, r1)     // Catch:{ all -> 0x04a2 }
            goto L_0x1855
        L_0x15f9:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a2 }
            r3 = 0
            r6 = r4[r3]     // Catch:{ all -> 0x04a2 }
            r1[r3] = r6     // Catch:{ all -> 0x04a2 }
            java.lang.String r6 = "Videos"
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4)     // Catch:{ all -> 0x04a2 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a2 }
            java.lang.Object[] r11 = new java.lang.Object[r3]     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r6, r4, r11)     // Catch:{ all -> 0x04a2 }
            r1[r9] = r3     // Catch:{ all -> 0x04a2 }
            r3 = 2131624921(0x7f0e03d9, float:1.8877035E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r12, r3, r1)     // Catch:{ all -> 0x04a2 }
            goto L_0x1855
        L_0x161f:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a2 }
            r3 = 0
            r6 = r4[r3]     // Catch:{ all -> 0x04a2 }
            r1[r3] = r6     // Catch:{ all -> 0x04a2 }
            java.lang.String r6 = "Photos"
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4)     // Catch:{ all -> 0x04a2 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a2 }
            java.lang.Object[] r11 = new java.lang.Object[r3]     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r6, r4, r11)     // Catch:{ all -> 0x04a2 }
            r1[r9] = r3     // Catch:{ all -> 0x04a2 }
            r3 = 2131624921(0x7f0e03d9, float:1.8877035E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r12, r3, r1)     // Catch:{ all -> 0x04a2 }
            goto L_0x1855
        L_0x1645:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a2 }
            r3 = 0
            r6 = r4[r3]     // Catch:{ all -> 0x04a2 }
            r1[r3] = r6     // Catch:{ all -> 0x04a2 }
            java.lang.String r6 = "ForwardedMessageCount"
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4)     // Catch:{ all -> 0x04a2 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a2 }
            java.lang.Object[] r9 = new java.lang.Object[r3]     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r6, r4, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = r3.toLowerCase()     // Catch:{ all -> 0x04a2 }
            r4 = 1
            r1[r4] = r3     // Catch:{ all -> 0x04a2 }
            r3 = 2131624921(0x7f0e03d9, float:1.8877035E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r12, r3, r1)     // Catch:{ all -> 0x04a2 }
            goto L_0x1855
        L_0x1670:
            java.lang.String r1 = "NotificationMessageGame"
            r3 = 2131626971(0x7f0e0bdb, float:1.8881193E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachGame"
            r4 = 2131624480(0x7f0e0220, float:1.887614E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x168c:
            java.lang.String r1 = "ChannelMessageGIF"
            r3 = 2131624922(0x7f0e03da, float:1.8877037E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachGif"
            r4 = 2131624481(0x7f0e0221, float:1.8876143E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x16a8:
            java.lang.String r1 = "ChannelMessageLiveLocation"
            r3 = 2131624923(0x7f0e03db, float:1.887704E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachLiveLocation"
            r4 = 2131624486(0x7f0e0226, float:1.8876153E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x16c4:
            java.lang.String r1 = "ChannelMessageMap"
            r3 = 2131624924(0x7f0e03dc, float:1.8877041E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachLocation"
            r4 = 2131624490(0x7f0e022a, float:1.8876161E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x16e0:
            java.lang.String r1 = "ChannelMessagePoll2"
            r3 = 2131624928(0x7f0e03e0, float:1.887705E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "Poll"
            r4 = 2131627574(0x7f0e0e36, float:1.8882416E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1701:
            java.lang.String r1 = "ChannelMessageQuiz2"
            r3 = 2131624929(0x7f0e03e1, float:1.8877052E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "QuizPoll"
            r4 = 2131627820(0x7f0e0f2c, float:1.8882915E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1722:
            java.lang.String r1 = "ChannelMessageContact2"
            r3 = 2131624919(0x7f0e03d7, float:1.8877031E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachContact"
            r4 = 2131624476(0x7f0e021c, float:1.8876133E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1743:
            java.lang.String r1 = "ChannelMessageAudio"
            r3 = 2131624918(0x7f0e03d6, float:1.887703E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachAudio"
            r4 = 2131624474(0x7f0e021a, float:1.8876129E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x175f:
            int r1 = r4.length     // Catch:{ all -> 0x04a2 }
            r3 = 1
            if (r1 <= r3) goto L_0x179e
            r1 = r4[r3]     // Catch:{ all -> 0x04a2 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x04a2 }
            if (r1 != 0) goto L_0x179e
            java.lang.String r1 = "ChannelMessageStickerEmoji"
            r3 = 2131624932(0x7f0e03e4, float:1.8877058E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r12 = 0
            r15 = r4[r12]     // Catch:{ all -> 0x04a2 }
            r6[r12] = r15     // Catch:{ all -> 0x04a2 }
            r12 = 1
            r15 = r4[r12]     // Catch:{ all -> 0x04a2 }
            r6[r12] = r15     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x04a2 }
            r3.<init>()     // Catch:{ all -> 0x04a2 }
            r4 = r4[r12]     // Catch:{ all -> 0x04a2 }
            r3.append(r4)     // Catch:{ all -> 0x04a2 }
            r3.append(r9)     // Catch:{ all -> 0x04a2 }
            r4 = 2131624503(0x7f0e0237, float:1.8876188E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r11, r4)     // Catch:{ all -> 0x04a2 }
            r3.append(r4)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x179e:
            java.lang.String r1 = "ChannelMessageSticker"
            r3 = 2131624931(0x7f0e03e3, float:1.8877056E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            r3 = 2131624503(0x7f0e0237, float:1.8876188E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r3)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x17b8:
            java.lang.String r1 = "ChannelMessageDocument"
            r3 = 2131624920(0x7f0e03d8, float:1.8877033E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachDocument"
            r4 = 2131624479(0x7f0e021f, float:1.8876139E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x17d4:
            java.lang.String r1 = "ChannelMessageRound"
            r3 = 2131624930(0x7f0e03e2, float:1.8877054E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachRound"
            r4 = 2131624502(0x7f0e0236, float:1.8876186E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x17f0:
            java.lang.String r1 = "ChannelMessageVideo"
            r3 = 2131624933(0x7f0e03e5, float:1.887706E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachVideo"
            r4 = 2131624506(0x7f0e023a, float:1.8876194E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x180c:
            java.lang.String r1 = "ChannelMessagePhoto"
            r3 = 2131624927(0x7f0e03df, float:1.8877048E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachPhoto"
            r4 = 2131624500(0x7f0e0234, float:1.8876181E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1828:
            java.lang.String r1 = "ChannelMessageNoText"
            r3 = 2131624926(0x7f0e03de, float:1.8877045E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "Message"
            r4 = 2131626619(0x7f0e0a7b, float:1.888048E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1844:
            java.lang.String r1 = "NotificationMessageAlbum"
            r3 = 2131626965(0x7f0e0bd5, float:1.8881181E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
        L_0x1855:
            r4 = 1
            goto L_0x1aeb
        L_0x1858:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r9 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r1[r6] = r9     // Catch:{ all -> 0x04a2 }
            java.lang.String r9 = "Files"
            r11 = 1
            r4 = r4[r11]     // Catch:{ all -> 0x04a2 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4)     // Catch:{ all -> 0x04a2 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a2 }
            java.lang.Object[] r12 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r9, r4, r12)     // Catch:{ all -> 0x04a2 }
            r1[r11] = r4     // Catch:{ all -> 0x04a2 }
            r4 = 2131626969(0x7f0e0bd9, float:1.888119E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r4, r1)     // Catch:{ all -> 0x04a2 }
            goto L_0x1855
        L_0x187d:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r9 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r1[r6] = r9     // Catch:{ all -> 0x04a2 }
            java.lang.String r9 = "MusicFiles"
            r11 = 1
            r4 = r4[r11]     // Catch:{ all -> 0x04a2 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4)     // Catch:{ all -> 0x04a2 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a2 }
            java.lang.Object[] r12 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r9, r4, r12)     // Catch:{ all -> 0x04a2 }
            r1[r11] = r4     // Catch:{ all -> 0x04a2 }
            r4 = 2131626969(0x7f0e0bd9, float:1.888119E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r4, r1)     // Catch:{ all -> 0x04a2 }
            goto L_0x1855
        L_0x18a2:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r9 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r1[r6] = r9     // Catch:{ all -> 0x04a2 }
            java.lang.String r9 = "Videos"
            r11 = 1
            r4 = r4[r11]     // Catch:{ all -> 0x04a2 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4)     // Catch:{ all -> 0x04a2 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a2 }
            java.lang.Object[] r12 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r9, r4, r12)     // Catch:{ all -> 0x04a2 }
            r1[r11] = r4     // Catch:{ all -> 0x04a2 }
            r4 = 2131626969(0x7f0e0bd9, float:1.888119E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r4, r1)     // Catch:{ all -> 0x04a2 }
            goto L_0x1855
        L_0x18c7:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r9 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r1[r6] = r9     // Catch:{ all -> 0x04a2 }
            java.lang.String r9 = "Photos"
            r11 = 1
            r4 = r4[r11]     // Catch:{ all -> 0x04a2 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4)     // Catch:{ all -> 0x04a2 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a2 }
            java.lang.Object[] r12 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r9, r4, r12)     // Catch:{ all -> 0x04a2 }
            r1[r11] = r4     // Catch:{ all -> 0x04a2 }
            r4 = 2131626969(0x7f0e0bd9, float:1.888119E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r4, r1)     // Catch:{ all -> 0x04a2 }
            goto L_0x1855
        L_0x18ed:
            r11 = r28
            java.lang.String r1 = "NotificationMessageForwardFew"
            r3 = 2131626970(0x7f0e0bda, float:1.8881191E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r12 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r12     // Catch:{ all -> 0x04a2 }
            r12 = 1
            r4 = r4[r12]     // Catch:{ all -> 0x04a2 }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r4)     // Catch:{ all -> 0x04a2 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x04a2 }
            java.lang.Object[] r15 = new java.lang.Object[r9]     // Catch:{ all -> 0x04a2 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r11, r4, r15)     // Catch:{ all -> 0x04a2 }
            r6[r12] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            goto L_0x1855
        L_0x1915:
            java.lang.String r1 = "NotificationMessageInvoice"
            r3 = 2131626993(0x7f0e0bf1, float:1.8881238E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "PaymentInvoice"
            r4 = 2131627390(0x7f0e0d7e, float:1.8882043E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1936:
            java.lang.String r3 = "NotificationMessageGameScored"
            r6 = 2131626972(0x7f0e0bdc, float:1.8881195E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r1[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r1[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 2
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r1[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r6, r1)     // Catch:{ all -> 0x04a2 }
            goto L_0x1aea
        L_0x1953:
            java.lang.String r1 = "NotificationMessageGame"
            r3 = 2131626971(0x7f0e0bdb, float:1.8881193E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachGame"
            r4 = 2131624480(0x7f0e0220, float:1.887614E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1974:
            java.lang.String r1 = "NotificationMessageGif"
            r3 = 2131626973(0x7f0e0bdd, float:1.8881197E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachGif"
            r4 = 2131624481(0x7f0e0221, float:1.8876143E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1990:
            java.lang.String r1 = "NotificationMessageLiveLocation"
            r3 = 2131626994(0x7f0e0bf2, float:1.888124E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachLiveLocation"
            r4 = 2131624486(0x7f0e0226, float:1.8876153E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x19ac:
            java.lang.String r1 = "NotificationMessageMap"
            r3 = 2131626995(0x7f0e0bf3, float:1.8881242E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachLocation"
            r4 = 2131624490(0x7f0e022a, float:1.8876161E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x19c8:
            java.lang.String r1 = "NotificationMessagePoll2"
            r3 = 2131626999(0x7f0e0bf7, float:1.888125E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "Poll"
            r4 = 2131627574(0x7f0e0e36, float:1.8882416E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x19e9:
            java.lang.String r1 = "NotificationMessageQuiz2"
            r3 = 2131627000(0x7f0e0bf8, float:1.8881252E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "QuizPoll"
            r4 = 2131627820(0x7f0e0f2c, float:1.8882915E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1a0a:
            java.lang.String r1 = "NotificationMessageContact2"
            r3 = 2131626967(0x7f0e0bd7, float:1.8881185E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachContact"
            r4 = 2131624476(0x7f0e021c, float:1.8876133E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1a2b:
            java.lang.String r1 = "NotificationMessageAudio"
            r3 = 2131626966(0x7f0e0bd6, float:1.8881183E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachAudio"
            r4 = 2131624474(0x7f0e021a, float:1.8876129E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1a47:
            int r1 = r4.length     // Catch:{ all -> 0x04a2 }
            r3 = 1
            if (r1 <= r3) goto L_0x1a86
            r1 = r4[r3]     // Catch:{ all -> 0x04a2 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x04a2 }
            if (r1 != 0) goto L_0x1a86
            java.lang.String r1 = "NotificationMessageStickerEmoji"
            r3 = 2131627008(0x7f0e0CLASSNAME, float:1.8881268E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r12 = 0
            r15 = r4[r12]     // Catch:{ all -> 0x04a2 }
            r6[r12] = r15     // Catch:{ all -> 0x04a2 }
            r12 = 1
            r15 = r4[r12]     // Catch:{ all -> 0x04a2 }
            r6[r12] = r15     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x04a2 }
            r3.<init>()     // Catch:{ all -> 0x04a2 }
            r4 = r4[r12]     // Catch:{ all -> 0x04a2 }
            r3.append(r4)     // Catch:{ all -> 0x04a2 }
            r3.append(r9)     // Catch:{ all -> 0x04a2 }
            r4 = 2131624503(0x7f0e0237, float:1.8876188E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r11, r4)     // Catch:{ all -> 0x04a2 }
            r3.append(r4)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1a86:
            java.lang.String r1 = "NotificationMessageSticker"
            r3 = 2131627007(0x7f0e0bff, float:1.8881266E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            r3 = 2131624503(0x7f0e0237, float:1.8876188E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r3)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1aa0:
            java.lang.String r1 = "NotificationMessageDocument"
            r3 = 2131626968(0x7f0e0bd8, float:1.8881187E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachDocument"
            r4 = 2131624479(0x7f0e021f, float:1.8876139E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1abc:
            java.lang.String r1 = "NotificationMessageRound"
            r3 = 2131627002(0x7f0e0bfa, float:1.8881256E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachRound"
            r4 = 2131624502(0x7f0e0236, float:1.8876186E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1ad8:
            java.lang.String r1 = "ActionTakeScreenshoot"
            r3 = 2131624214(0x7f0e0116, float:1.8875601E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r3)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "un1"
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = r1.replace(r3, r4)     // Catch:{ all -> 0x04a2 }
        L_0x1aea:
            r4 = 0
        L_0x1aeb:
            r22 = 0
            goto L_0x0af3
        L_0x1aef:
            java.lang.String r1 = "NotificationMessageSDVideo"
            r3 = 2131627004(0x7f0e0bfc, float:1.888126E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachDestructingVideo"
            r4 = 2131624478(0x7f0e021e, float:1.8876137E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1b0a:
            java.lang.String r1 = "NotificationMessageVideo"
            r3 = 2131627010(0x7f0e0CLASSNAME, float:1.8881272E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachVideo"
            r4 = 2131624506(0x7f0e023a, float:1.8876194E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1b25:
            java.lang.String r1 = "NotificationMessageSDPhoto"
            r3 = 2131627003(0x7f0e0bfb, float:1.8881258E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachDestructingPhoto"
            r4 = 2131624477(0x7f0e021d, float:1.8876135E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1b40:
            java.lang.String r1 = "NotificationMessagePhoto"
            r3 = 2131626998(0x7f0e0bf6, float:1.8881248E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "AttachPhoto"
            r4 = 2131624500(0x7f0e0234, float:1.8876181E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1b5b:
            java.lang.String r1 = "NotificationMessageNoText"
            r3 = 2131626997(0x7f0e0bf5, float:1.8881246E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r6 = 0
            r4 = r4[r6]     // Catch:{ all -> 0x04a2 }
            r9[r6] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r9)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "Message"
            r4 = 2131626619(0x7f0e0a7b, float:1.888048E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
        L_0x1b75:
            r22 = r3
        L_0x1b77:
            r4 = 0
            goto L_0x0af3
        L_0x1b7a:
            java.lang.String r1 = "NotificationMessageText"
            r3 = 2131627009(0x7f0e0CLASSNAME, float:1.888127E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            r3 = r4[r9]     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1b93:
            java.lang.String r1 = "NotificationMessageRecurringPay"
            r3 = 2131627001(0x7f0e0bf9, float:1.8881254E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x04a2 }
            r9 = 0
            r11 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r11     // Catch:{ all -> 0x04a2 }
            r9 = 1
            r4 = r4[r9]     // Catch:{ all -> 0x04a2 }
            r6[r9] = r4     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r6)     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "PaymentInvoice"
            r4 = 2131627390(0x7f0e0d7e, float:1.8882043E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x04a2 }
            goto L_0x1b75
        L_0x1bb3:
            if (r1 == 0) goto L_0x1bc9
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x04a2 }
            r1.<init>()     // Catch:{ all -> 0x04a2 }
            java.lang.String r3 = "unhandled loc_key = "
            r1.append(r3)     // Catch:{ all -> 0x04a2 }
            r1.append(r2)     // Catch:{ all -> 0x04a2 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x04a2 }
            org.telegram.messenger.FileLog.w(r1)     // Catch:{ all -> 0x04a2 }
        L_0x1bc9:
            r3 = 0
        L_0x1bca:
            r4 = 0
        L_0x1bcb:
            r22 = 0
        L_0x1bcd:
            r1 = r51
            goto L_0x1be1
        L_0x1bd0:
            r46 = r3
            r48 = r11
            r47 = r12
            r17 = r15
            r1 = r51
        L_0x1bda:
            java.lang.String r3 = r1.getReactedText(r2, r4)     // Catch:{ all -> 0x1d03 }
            r4 = 0
            r22 = 0
        L_0x1be1:
            if (r3 == 0) goto L_0x1cef
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x1d03 }
            r6.<init>()     // Catch:{ all -> 0x1d03 }
            r6.id = r5     // Catch:{ all -> 0x1d03 }
            r11 = r29
            r6.random_id = r11     // Catch:{ all -> 0x1d03 }
            if (r22 == 0) goto L_0x1bf3
            r5 = r22
            goto L_0x1bf4
        L_0x1bf3:
            r5 = r3
        L_0x1bf4:
            r6.message = r5     // Catch:{ all -> 0x1d03 }
            r11 = 1000(0x3e8, double:4.94E-321)
            long r11 = r53 / r11
            int r5 = (int) r11     // Catch:{ all -> 0x1d03 }
            r6.date = r5     // Catch:{ all -> 0x1d03 }
            if (r10 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r5 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x1d03 }
            r5.<init>()     // Catch:{ all -> 0x1d03 }
            r6.action = r5     // Catch:{ all -> 0x1d03 }
        L_0x1CLASSNAME:
            if (r43 == 0) goto L_0x1c0f
            int r5 = r6.flags     // Catch:{ all -> 0x1d03 }
            r9 = -2147483648(0xfffffffvar_, float:-0.0)
            r5 = r5 | r9
            r6.flags = r5     // Catch:{ all -> 0x1d03 }
        L_0x1c0f:
            r6.dialog_id = r7     // Catch:{ all -> 0x1d03 }
            r7 = 0
            int r5 = (r37 > r7 ? 1 : (r37 == r7 ? 0 : -1))
            if (r5 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_peerChannel r5 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x1d03 }
            r5.<init>()     // Catch:{ all -> 0x1d03 }
            r6.peer_id = r5     // Catch:{ all -> 0x1d03 }
            r7 = r37
            r5.channel_id = r7     // Catch:{ all -> 0x1d03 }
            r7 = r26
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r7 = 0
            int r5 = (r26 > r7 ? 1 : (r26 == r7 ? 0 : -1))
            if (r5 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_peerChat r5 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x1d03 }
            r5.<init>()     // Catch:{ all -> 0x1d03 }
            r6.peer_id = r5     // Catch:{ all -> 0x1d03 }
            r7 = r26
            r5.chat_id = r7     // Catch:{ all -> 0x1d03 }
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r7 = r26
            org.telegram.tgnet.TLRPC$TL_peerUser r5 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1d03 }
            r5.<init>()     // Catch:{ all -> 0x1d03 }
            r6.peer_id = r5     // Catch:{ all -> 0x1d03 }
            r11 = r35
            r5.user_id = r11     // Catch:{ all -> 0x1d03 }
        L_0x1CLASSNAME:
            int r5 = r6.flags     // Catch:{ all -> 0x1d03 }
            r5 = r5 | 256(0x100, float:3.59E-43)
            r6.flags = r5     // Catch:{ all -> 0x1d03 }
            r11 = 0
            int r5 = (r39 > r11 ? 1 : (r39 == r11 ? 0 : -1))
            if (r5 == 0) goto L_0x1c5a
            org.telegram.tgnet.TLRPC$TL_peerChat r5 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x1d03 }
            r5.<init>()     // Catch:{ all -> 0x1d03 }
            r6.from_id = r5     // Catch:{ all -> 0x1d03 }
            r5.chat_id = r7     // Catch:{ all -> 0x1d03 }
            goto L_0x1CLASSNAME
        L_0x1c5a:
            r7 = 0
            int r5 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1))
            if (r5 == 0) goto L_0x1c6a
            org.telegram.tgnet.TLRPC$TL_peerChannel r5 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x1d03 }
            r5.<init>()     // Catch:{ all -> 0x1d03 }
            r6.from_id = r5     // Catch:{ all -> 0x1d03 }
            r5.channel_id = r13     // Catch:{ all -> 0x1d03 }
            goto L_0x1CLASSNAME
        L_0x1c6a:
            r7 = 0
            int r5 = (r41 > r7 ? 1 : (r41 == r7 ? 0 : -1))
            if (r5 == 0) goto L_0x1c7c
            org.telegram.tgnet.TLRPC$TL_peerUser r5 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1d03 }
            r5.<init>()     // Catch:{ all -> 0x1d03 }
            r6.from_id = r5     // Catch:{ all -> 0x1d03 }
            r7 = r41
            r5.user_id = r7     // Catch:{ all -> 0x1d03 }
            goto L_0x1CLASSNAME
        L_0x1c7c:
            org.telegram.tgnet.TLRPC$Peer r5 = r6.peer_id     // Catch:{ all -> 0x1d03 }
            r6.from_id = r5     // Catch:{ all -> 0x1d03 }
        L_0x1CLASSNAME:
            if (r16 != 0) goto L_0x1CLASSNAME
            if (r10 == 0) goto L_0x1CLASSNAME
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r5 = 0
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r5 = 1
        L_0x1CLASSNAME:
            r6.mentioned = r5     // Catch:{ all -> 0x1d03 }
            r5 = r32
            r6.silent = r5     // Catch:{ all -> 0x1d03 }
            r15 = r25
            r6.from_scheduled = r15     // Catch:{ all -> 0x1d03 }
            org.telegram.messenger.MessageObject r5 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x1d03 }
            r23 = r5
            r24 = r34
            r25 = r6
            r26 = r3
            r27 = r44
            r28 = r48
            r29 = r4
            r30 = r47
            r31 = r43
            r32 = r45
            r23.<init>(r24, r25, r26, r27, r28, r29, r30, r31, r32)     // Catch:{ all -> 0x1d03 }
            r3 = r46
            boolean r3 = r2.startsWith(r3)     // Catch:{ all -> 0x1d03 }
            if (r3 != 0) goto L_0x1cbe
            r3 = r17
            boolean r3 = r2.startsWith(r3)     // Catch:{ all -> 0x1d03 }
            if (r3 == 0) goto L_0x1cbc
            goto L_0x1cbe
        L_0x1cbc:
            r3 = 0
            goto L_0x1cbf
        L_0x1cbe:
            r3 = 1
        L_0x1cbf:
            r5.isReactionPush = r3     // Catch:{ all -> 0x1d03 }
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ all -> 0x1d03 }
            r3.<init>()     // Catch:{ all -> 0x1d03 }
            r3.add(r5)     // Catch:{ all -> 0x1d03 }
            org.telegram.messenger.NotificationsController r4 = org.telegram.messenger.NotificationsController.getInstance(r34)     // Catch:{ all -> 0x1d03 }
            java.util.concurrent.CountDownLatch r5 = r1.countDownLatch     // Catch:{ all -> 0x1d03 }
            r6 = 1
            r4.processNewMessages(r3, r6, r6, r5)     // Catch:{ all -> 0x1d03 }
            r9 = 0
            goto L_0x1cf0
        L_0x1cd5:
            r0 = move-exception
            r1 = r51
            goto L_0x1d04
        L_0x1cd9:
            r0 = move-exception
            r1 = r51
            r34 = r6
            goto L_0x1d04
        L_0x1cdf:
            r1 = r51
            goto L_0x1ced
        L_0x1ce2:
            r0 = move-exception
            r1 = r51
            goto L_0x1ce7
        L_0x1ce6:
            r0 = move-exception
        L_0x1ce7:
            r34 = r12
            goto L_0x1dcd
        L_0x1ceb:
            r33 = r6
        L_0x1ced:
            r34 = r12
        L_0x1cef:
            r9 = 1
        L_0x1cf0:
            if (r9 == 0) goto L_0x1cf7
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch     // Catch:{ all -> 0x1d03 }
            r3.countDown()     // Catch:{ all -> 0x1d03 }
        L_0x1cf7:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r34)     // Catch:{ all -> 0x1d03 }
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r34)     // Catch:{ all -> 0x1d03 }
            r3.resumeNetworkMaybe()     // Catch:{ all -> 0x1d03 }
            goto L_0x1e26
        L_0x1d03:
            r0 = move-exception
        L_0x1d04:
            r5 = r2
            r6 = r33
            r12 = r34
            goto L_0x1dd6
        L_0x1d0b:
            r0 = move-exception
            r33 = r6
            goto L_0x1d13
        L_0x1d0f:
            r0 = move-exception
            r33 = r6
            r2 = r8
        L_0x1d13:
            r34 = r12
            goto L_0x1dd5
        L_0x1d17:
            r33 = r6
            r2 = r8
            r34 = r12
            org.telegram.messenger.DispatchQueue r3 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1d2e }
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1 r4 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1     // Catch:{ all -> 0x1d2e }
            r12 = r34
            r4.<init>(r12)     // Catch:{ all -> 0x1dcc }
            r3.postRunnable(r4)     // Catch:{ all -> 0x1dcc }
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch     // Catch:{ all -> 0x1dcc }
            r3.countDown()     // Catch:{ all -> 0x1dcc }
            return
        L_0x1d2e:
            r0 = move-exception
            r12 = r34
            goto L_0x1dcd
        L_0x1d33:
            r33 = r6
            r2 = r8
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0 r3 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0     // Catch:{ all -> 0x1dcc }
            r3.<init>(r12)     // Catch:{ all -> 0x1dcc }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)     // Catch:{ all -> 0x1dcc }
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch     // Catch:{ all -> 0x1dcc }
            r3.countDown()     // Catch:{ all -> 0x1dcc }
            return
        L_0x1d44:
            r33 = r6
            r2 = r8
            r5 = r16
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r3 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x1dcc }
            r3.<init>()     // Catch:{ all -> 0x1dcc }
            r4 = 0
            r3.popup = r4     // Catch:{ all -> 0x1dcc }
            r4 = 2
            r3.flags = r4     // Catch:{ all -> 0x1dcc }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r6 = r53 / r6
            int r4 = (int) r6     // Catch:{ all -> 0x1dcc }
            r3.inbox_date = r4     // Catch:{ all -> 0x1dcc }
            java.lang.String r4 = "message"
            java.lang.String r4 = r5.getString(r4)     // Catch:{ all -> 0x1dcc }
            r3.message = r4     // Catch:{ all -> 0x1dcc }
            java.lang.String r4 = "announcement"
            r3.type = r4     // Catch:{ all -> 0x1dcc }
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ all -> 0x1dcc }
            r4.<init>()     // Catch:{ all -> 0x1dcc }
            r3.media = r4     // Catch:{ all -> 0x1dcc }
            org.telegram.tgnet.TLRPC$TL_updates r4 = new org.telegram.tgnet.TLRPC$TL_updates     // Catch:{ all -> 0x1dcc }
            r4.<init>()     // Catch:{ all -> 0x1dcc }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r5 = r4.updates     // Catch:{ all -> 0x1dcc }
            r5.add(r3)     // Catch:{ all -> 0x1dcc }
            org.telegram.messenger.DispatchQueue r3 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1dcc }
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3 r5 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3     // Catch:{ all -> 0x1dcc }
            r5.<init>(r12, r4)     // Catch:{ all -> 0x1dcc }
            r3.postRunnable(r5)     // Catch:{ all -> 0x1dcc }
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r12)     // Catch:{ all -> 0x1dcc }
            r3.resumeNetworkMaybe()     // Catch:{ all -> 0x1dcc }
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch     // Catch:{ all -> 0x1dcc }
            r3.countDown()     // Catch:{ all -> 0x1dcc }
            return
        L_0x1d8f:
            r33 = r6
            r2 = r8
            java.lang.String r3 = "dc"
            int r3 = r10.getInt(r3)     // Catch:{ all -> 0x1dcc }
            java.lang.String r4 = "addr"
            java.lang.String r4 = r10.getString(r4)     // Catch:{ all -> 0x1dcc }
            java.lang.String r5 = ":"
            java.lang.String[] r4 = r4.split(r5)     // Catch:{ all -> 0x1dcc }
            int r5 = r4.length     // Catch:{ all -> 0x1dcc }
            r6 = 2
            if (r5 == r6) goto L_0x1dae
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch     // Catch:{ all -> 0x1dcc }
            r3.countDown()     // Catch:{ all -> 0x1dcc }
            return
        L_0x1dae:
            r5 = 0
            r5 = r4[r5]     // Catch:{ all -> 0x1dcc }
            r6 = 1
            r4 = r4[r6]     // Catch:{ all -> 0x1dcc }
            int r4 = java.lang.Integer.parseInt(r4)     // Catch:{ all -> 0x1dcc }
            org.telegram.tgnet.ConnectionsManager r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r12)     // Catch:{ all -> 0x1dcc }
            r6.applyDatacenterAddress(r3, r5, r4)     // Catch:{ all -> 0x1dcc }
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r12)     // Catch:{ all -> 0x1dcc }
            r3.resumeNetworkMaybe()     // Catch:{ all -> 0x1dcc }
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch     // Catch:{ all -> 0x1dcc }
            r3.countDown()     // Catch:{ all -> 0x1dcc }
            return
        L_0x1dcc:
            r0 = move-exception
        L_0x1dcd:
            r5 = r2
            r6 = r33
            goto L_0x1dd6
        L_0x1dd1:
            r0 = move-exception
            r33 = r6
        L_0x1dd4:
            r2 = r8
        L_0x1dd5:
            r5 = r2
        L_0x1dd6:
            r3 = -1
            goto L_0x1ddf
        L_0x1dd8:
            r0 = move-exception
            r33 = r6
            r2 = r8
            r5 = r2
            r3 = -1
            r12 = -1
        L_0x1ddf:
            r2 = r0
            goto L_0x1dee
        L_0x1de1:
            r0 = move-exception
            r33 = r6
        L_0x1de4:
            r2 = r0
            r3 = -1
            r5 = 0
            goto L_0x1ded
        L_0x1de8:
            r0 = move-exception
            r2 = r0
            r3 = -1
            r5 = 0
            r6 = 0
        L_0x1ded:
            r12 = -1
        L_0x1dee:
            if (r12 == r3) goto L_0x1e00
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r12)
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r12)
            r3.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch
            r3.countDown()
            goto L_0x1e03
        L_0x1e00:
            r51.onDecryptError()
        L_0x1e03:
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x1e23
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
        L_0x1e23:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x1e26:
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
        for (int i = 0; i < 4; i++) {
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
            for (int i = 0; i < 4; i++) {
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
