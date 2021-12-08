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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v23, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v29, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v42, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v45, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v47, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v63, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v8, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v9, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v10, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v70, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v74, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v44, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v79, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v84, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v47, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v89, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v101, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v110, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v11, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v125, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v134, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v128, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v132, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v136, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v143, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v140, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v143, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v148, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v138, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v187, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v190, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v148, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v193, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v196, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v199, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v149, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v203, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v204, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v208, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v47, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v211, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v48, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v214, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v217, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v174, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v166, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v220, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v223, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v170, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v226, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v172, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v229, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v174, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v232, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v177, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v236, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v166, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v170, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v181, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v173, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v178, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v190, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v78, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v190, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v79, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v221, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v193, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v197, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v227, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v141, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v202, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v207, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v212, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v217, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v208, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v158, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v221, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v213, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v223, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v226, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v227, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v227, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v228, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v229, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v229, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v173, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v232, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v178, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v282, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v101, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v278, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v289, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v190, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v294, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v256, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v244, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v308, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v108, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v269, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v272, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v315, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v275, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v210, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v212, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v252, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v213, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v282, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v214, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v138, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v256, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v287, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v257, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v143, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v216, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v258, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v217, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v292, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v260, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v262, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v297, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v263, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v264, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v221, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v302, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v266, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v166, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v223, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v268, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v307, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v269, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v270, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v173, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v313, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v314, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v315, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v318, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v273, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v274, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v299, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v300, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v303, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:1000:0x1db0, code lost:
        r1 = r0;
        r7 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1013:0x1dd6, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r4);
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1014:0x1de6, code lost:
        onDecryptError();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1017:0x1ded, code lost:
        org.telegram.messenger.FileLog.e("error in loc_key = " + r14 + " json " + r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1028:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x01fb, code lost:
        if (r2 == 0) goto L_0x1d71;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x01fd, code lost:
        if (r2 == 1) goto L_0x1d27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x01ff, code lost:
        if (r2 == 2) goto L_0x1d15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x0201, code lost:
        if (r2 == 3) goto L_0x1cf8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x020b, code lost:
        if (r11.has("channel_id") == false) goto L_0x0221;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x020d, code lost:
        r16 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:?, code lost:
        r6 = r11.getLong("channel_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0215, code lost:
        r12 = -r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x0217, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x0218, code lost:
        r3 = r1;
        r14 = r9;
        r7 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x0221, code lost:
        r16 = r7;
        r6 = 0;
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x022b, code lost:
        if (r11.has("from_id") == false) goto L_0x0237;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:?, code lost:
        r12 = r11.getLong("from_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x0233, code lost:
        r28 = r4;
        r3 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x0237, code lost:
        r28 = r4;
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x0240, code lost:
        if (r11.has("chat_id") == false) goto L_0x025d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:163:?, code lost:
        r12 = r11.getLong("chat_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:164:0x0248, code lost:
        r29 = r9;
        r8 = r12;
        r12 = -r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:165:0x0251, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x0252, code lost:
        r29 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:167:0x0254, code lost:
        r3 = r1;
        r7 = r16;
        r4 = r28;
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x025d, code lost:
        r29 = r9;
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x0266, code lost:
        if (r11.has("encryption_id") == false) goto L_0x0276;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:175:?, code lost:
        r12 = org.telegram.messenger.DialogObject.makeEncryptedDialogId((long) r11.getInt("encryption_id"));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x0274, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x027c, code lost:
        if (r11.has("schedule") == false) goto L_0x0288;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x0284, code lost:
        if (r11.getInt("schedule") != 1) goto L_0x0288;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:0x0286, code lost:
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x0288, code lost:
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x028b, code lost:
        if (r12 != 0) goto L_0x02a6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x028d, code lost:
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x0295, code lost:
        if ("ENCRYPTED_MESSAGE".equals(r14) == false) goto L_0x02a8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x0297, code lost:
        r12 = org.telegram.messenger.NotificationsController.globalSecretChatId;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x029a, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x029f, code lost:
        r3 = r1;
        r7 = r16;
        r4 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x02a6, code lost:
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x02ac, code lost:
        if (r12 == 0) goto L_0x1cc5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x02b0, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x02b6, code lost:
        if ("READ_HISTORY".equals(r14) == false) goto L_0x032a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:?, code lost:
        r2 = r11.getInt("max_id");
        r5 = new java.util.ArrayList();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x02c5, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x02e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x02c7, code lost:
        org.telegram.messenger.FileLog.d("GCM received read notification max_id = " + r2 + " for dialogId = " + r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x02e5, code lost:
        if (r6 == 0) goto L_0x02f4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x02e7, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox();
        r3.channel_id = r6;
        r3.max_id = r2;
        r5.add(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x02f4, code lost:
        r6 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x02fd, code lost:
        if (r3 == 0) goto L_0x0309;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x02ff, code lost:
        r7 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r6.peer = r7;
        r7.user_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x0309, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r6.peer = r3;
        r3.chat_id = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x0312, code lost:
        r6.max_id = r2;
        r5.add(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x0317, code lost:
        org.telegram.messenger.MessagesController.getInstance(r28).processUpdateArray(r5, (java.util.ArrayList<org.telegram.tgnet.TLRPC$User>) null, (java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat>) null, false, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x0330, code lost:
        r22 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x0334, code lost:
        if ("MESSAGE_DELETED".equals(r14) == false) goto L_0x03a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:?, code lost:
        r2 = r11.getString("messages").split(",");
        r3 = new androidx.collection.LongSparseArray();
        r4 = new java.util.ArrayList();
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x034c, code lost:
        if (r8 >= r2.length) goto L_0x035a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x034e, code lost:
        r4.add(org.telegram.messenger.Utilities.parseInt(r2[r8]));
        r8 = r8 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x035a, code lost:
        r3.put(-r6, r4);
        org.telegram.messenger.NotificationsController.getInstance(r28).removeDeletedMessagesFromNotifications(r3);
        org.telegram.messenger.MessagesController.getInstance(r28).deleteMessagesByPush(r12, r4, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x0374, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1cc5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x0376, code lost:
        org.telegram.messenger.FileLog.d("GCM received " + r14 + " for dialogId = " + r12 + " mids = " + android.text.TextUtils.join(",", r4));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x03a4, code lost:
        if (android.text.TextUtils.isEmpty(r14) != false) goto L_0x1cc5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:0x03ac, code lost:
        if (r11.has("msg_id") == false) goto L_0x03b5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:?, code lost:
        r10 = r11.getInt("msg_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x03b5, code lost:
        r10 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x03b8, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x03bc, code lost:
        if (r11.has("random_id") == false) goto L_0x03de;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:?, code lost:
        r47 = r3;
        r3 = org.telegram.messenger.Utilities.parseLong(r11.getString("random_id")).longValue();
        r23 = r47;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x03d3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x03d4, code lost:
        r2 = -1;
        r3 = r49;
        r1 = r0;
        r7 = r16;
        r4 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x03de, code lost:
        r23 = r3;
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x03e2, code lost:
        if (r10 == 0) goto L_0x0419;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x03e4, code lost:
        r25 = r8;
        r1 = org.telegram.messenger.MessagesController.getInstance(r28).dialogs_read_inbox_max.get(java.lang.Long.valueOf(r12));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x03f6, code lost:
        if (r1 != null) goto L_0x0412;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x03f8, code lost:
        r1 = java.lang.Integer.valueOf(org.telegram.messenger.MessagesStorage.getInstance(r28).getDialogReadMax(false, r12));
        org.telegram.messenger.MessagesController.getInstance(r28).dialogs_read_inbox_max.put(java.lang.Long.valueOf(r12), r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x0416, code lost:
        if (r10 <= r1.intValue()) goto L_0x042d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x0419, code lost:
        r25 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x041f, code lost:
        if (r3 == 0) goto L_0x042d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x0429, code lost:
        if (org.telegram.messenger.MessagesStorage.getInstance(r28).checkMessageByRandomId(r3) != false) goto L_0x042d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x042b, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x042d, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x042e, code lost:
        if (r1 == false) goto L_0x1cbb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x0430, code lost:
        r30 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:?, code lost:
        r3 = r11.optLong("chat_from_id", 0);
        r27 = "messages";
        r32 = r11.optLong("chat_from_broadcast_id", 0);
        r1 = r11.optLong("chat_from_group_id", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x044c, code lost:
        if (r3 != 0) goto L_0x0455;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x0450, code lost:
        if (r1 == 0) goto L_0x0453;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x0453, code lost:
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x0455, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x045c, code lost:
        if (r11.has("mention") == false) goto L_0x0469;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0464, code lost:
        if (r11.getInt("mention") == 0) goto L_0x0469;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x0466, code lost:
        r29 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0469, code lost:
        r29 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x0471, code lost:
        if (r11.has("silent") == false) goto L_0x047e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x0479, code lost:
        if (r11.getInt("silent") == 0) goto L_0x047e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x047b, code lost:
        r34 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x047e, code lost:
        r34 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x0486, code lost:
        if (r5.has("loc_args") == false) goto L_0x04a2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:?, code lost:
        r5 = r5.getJSONArray("loc_args");
        r9 = r5.length();
        r35 = r3;
        r3 = new java.lang.String[r9];
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x0497, code lost:
        if (r4 >= r9) goto L_0x04a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x0499, code lost:
        r3[r4] = r5.getString(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x049f, code lost:
        r4 = r4 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x04a2, code lost:
        r35 = r3;
        r3 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:?, code lost:
        r5 = r3[0];
        r4 = r11.has("edit_date");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x04b4, code lost:
        if (r14.startsWith("CHAT_") == false) goto L_0x04eb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x04ba, code lost:
        if (org.telegram.messenger.UserObject.isReplyUser(r12) == false) goto L_0x04d4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x04bc, code lost:
        r5 = r5 + " @ " + r3[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:311:0x04d8, code lost:
        if (r6 == 0) goto L_0x04dc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x04da, code lost:
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x04dc, code lost:
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x04e0, code lost:
        r11 = false;
        r38 = false;
        r47 = r9;
        r9 = r5;
        r5 = r3[1];
        r37 = r47;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x04f1, code lost:
        if (r14.startsWith("PINNED_") == false) goto L_0x0503;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x04f7, code lost:
        if (r6 == 0) goto L_0x04fb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x04f9, code lost:
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x04fb, code lost:
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x04fc, code lost:
        r37 = r9;
        r9 = null;
        r11 = false;
        r38 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x0509, code lost:
        if (r14.startsWith("CHANNEL_") == false) goto L_0x050e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x050b, code lost:
        r9 = null;
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x050e, code lost:
        r9 = null;
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x0510, code lost:
        r37 = false;
        r38 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x0516, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x053f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x0518, code lost:
        r39 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:?, code lost:
        r5 = new java.lang.StringBuilder();
        r40 = r4;
        r5.append("GCM received message notification ");
        r5.append(r14);
        r5.append(" for dialogId = ");
        r5.append(r12);
        r5.append(" mid = ");
        r5.append(r10);
        org.telegram.messenger.FileLog.d(r5.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x053f, code lost:
        r40 = r4;
        r39 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x0547, code lost:
        switch(r14.hashCode()) {
            case -2100047043: goto L_0x0a7c;
            case -2091498420: goto L_0x0a71;
            case -2053872415: goto L_0x0a66;
            case -2039746363: goto L_0x0a5b;
            case -2023218804: goto L_0x0a50;
            case -1979538588: goto L_0x0a45;
            case -1979536003: goto L_0x0a3a;
            case -1979535888: goto L_0x0a2f;
            case -1969004705: goto L_0x0a24;
            case -1946699248: goto L_0x0a18;
            case -1717283471: goto L_0x0a0c;
            case -1646640058: goto L_0x0a00;
            case -1528047021: goto L_0x09f4;
            case -1493579426: goto L_0x09e8;
            case -1482481933: goto L_0x09dc;
            case -1480102982: goto L_0x09d1;
            case -1478041834: goto L_0x09c5;
            case -1474543101: goto L_0x09ba;
            case -1465695932: goto L_0x09ae;
            case -1374906292: goto L_0x09a2;
            case -1372940586: goto L_0x0996;
            case -1264245338: goto L_0x098a;
            case -1236154001: goto L_0x097e;
            case -1236086700: goto L_0x0972;
            case -1236077786: goto L_0x0966;
            case -1235796237: goto L_0x095a;
            case -1235760759: goto L_0x094e;
            case -1235686303: goto L_0x0943;
            case -1198046100: goto L_0x0938;
            case -1124254527: goto L_0x092c;
            case -1085137927: goto L_0x0920;
            case -1084856378: goto L_0x0914;
            case -1084820900: goto L_0x0908;
            case -1084746444: goto L_0x08fc;
            case -819729482: goto L_0x08f0;
            case -772141857: goto L_0x08e4;
            case -638310039: goto L_0x08d8;
            case -590403924: goto L_0x08cc;
            case -589196239: goto L_0x08c0;
            case -589193654: goto L_0x08b4;
            case -589193539: goto L_0x08a8;
            case -440169325: goto L_0x089c;
            case -412748110: goto L_0x0890;
            case -228518075: goto L_0x0884;
            case -213586509: goto L_0x0878;
            case -115582002: goto L_0x086c;
            case -112621464: goto L_0x0860;
            case -108522133: goto L_0x0854;
            case -107572034: goto L_0x0849;
            case -40534265: goto L_0x083d;
            case 65254746: goto L_0x0831;
            case 141040782: goto L_0x0825;
            case 202550149: goto L_0x0819;
            case 309993049: goto L_0x080d;
            case 309995634: goto L_0x0801;
            case 309995749: goto L_0x07f5;
            case 320532812: goto L_0x07e9;
            case 328933854: goto L_0x07dd;
            case 331340546: goto L_0x07d1;
            case 342406591: goto L_0x07c5;
            case 344816990: goto L_0x07b9;
            case 346878138: goto L_0x07ad;
            case 350376871: goto L_0x07a1;
            case 608430149: goto L_0x0795;
            case 615714517: goto L_0x078a;
            case 715508879: goto L_0x077e;
            case 728985323: goto L_0x0772;
            case 731046471: goto L_0x0766;
            case 734545204: goto L_0x075a;
            case 802032552: goto L_0x074e;
            case 991498806: goto L_0x0742;
            case 1007364121: goto L_0x0736;
            case 1019850010: goto L_0x072a;
            case 1019917311: goto L_0x071e;
            case 1019926225: goto L_0x0712;
            case 1020207774: goto L_0x0706;
            case 1020243252: goto L_0x06fa;
            case 1020317708: goto L_0x06ee;
            case 1060282259: goto L_0x06e2;
            case 1060349560: goto L_0x06d6;
            case 1060358474: goto L_0x06ca;
            case 1060640023: goto L_0x06be;
            case 1060675501: goto L_0x06b2;
            case 1060749957: goto L_0x06a7;
            case 1073049781: goto L_0x069b;
            case 1078101399: goto L_0x068f;
            case 1110103437: goto L_0x0683;
            case 1160762272: goto L_0x0677;
            case 1172918249: goto L_0x066b;
            case 1234591620: goto L_0x065f;
            case 1281128640: goto L_0x0653;
            case 1281131225: goto L_0x0647;
            case 1281131340: goto L_0x063b;
            case 1310789062: goto L_0x0630;
            case 1333118583: goto L_0x0624;
            case 1361447897: goto L_0x0618;
            case 1498266155: goto L_0x060c;
            case 1533804208: goto L_0x0600;
            case 1540131626: goto L_0x05f4;
            case 1547988151: goto L_0x05e8;
            case 1561464595: goto L_0x05dc;
            case 1563525743: goto L_0x05d0;
            case 1567024476: goto L_0x05c4;
            case 1810705077: goto L_0x05b8;
            case 1815177512: goto L_0x05ac;
            case 1954774321: goto L_0x05a0;
            case 1963241394: goto L_0x0594;
            case 2014789757: goto L_0x0588;
            case 2022049433: goto L_0x057c;
            case 2034984710: goto L_0x0570;
            case 2048733346: goto L_0x0564;
            case 2099392181: goto L_0x0558;
            case 2140162142: goto L_0x054c;
            default: goto L_0x054a;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x0552, code lost:
        if (r14.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x0554, code lost:
        r4 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x055e, code lost:
        if (r14.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x0560, code lost:
        r4 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x056a, code lost:
        if (r14.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x056c, code lost:
        r4 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:351:0x0576, code lost:
        if (r14.equals("CHANNEL_MESSAGE_PLAYLIST") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x0578, code lost:
        r4 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x0582, code lost:
        if (r14.equals("PINNED_CONTACT") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x0584, code lost:
        r4 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x058e, code lost:
        if (r14.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x0590, code lost:
        r4 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x059a, code lost:
        if (r14.equals("LOCKED_MESSAGE") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x059c, code lost:
        r4 = 'k';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x05a6, code lost:
        if (r14.equals("CHAT_MESSAGE_PLAYLIST") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x05a8, code lost:
        r4 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x05b2, code lost:
        if (r14.equals("CHANNEL_MESSAGES") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x05b4, code lost:
        r4 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x05be, code lost:
        if (r14.equals("MESSAGE_INVOICE") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x05c0, code lost:
        r4 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x05ca, code lost:
        if (r14.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x05cc, code lost:
        r4 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:375:0x05d6, code lost:
        if (r14.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x05d8, code lost:
        r4 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x05e2, code lost:
        if (r14.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x05e4, code lost:
        r4 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:381:0x05ee, code lost:
        if (r14.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x05f0, code lost:
        r4 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x05fa, code lost:
        if (r14.equals("MESSAGE_PLAYLIST") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x05fc, code lost:
        r4 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x0606, code lost:
        if (r14.equals("MESSAGE_VIDEOS") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x0608, code lost:
        r4 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x0612, code lost:
        if (r14.equals("PHONE_CALL_MISSED") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x0614, code lost:
        r4 = 'p';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x061e, code lost:
        if (r14.equals("MESSAGE_PHOTOS") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x0620, code lost:
        r4 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x062a, code lost:
        if (r14.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x062c, code lost:
        r4 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x0636, code lost:
        if (r14.equals("MESSAGE_NOTEXT") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:400:0x0638, code lost:
        r4 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x0641, code lost:
        if (r14.equals("MESSAGE_GIF") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0643, code lost:
        r4 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x064d, code lost:
        if (r14.equals("MESSAGE_GEO") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x064f, code lost:
        r4 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x0659, code lost:
        if (r14.equals("MESSAGE_DOC") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x065b, code lost:
        r4 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x0665, code lost:
        if (r14.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0667, code lost:
        r4 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x0671, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x0673, code lost:
        r4 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x067d, code lost:
        if (r14.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x067f, code lost:
        r4 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x0689, code lost:
        if (r14.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x068b, code lost:
        r4 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x0695, code lost:
        if (r14.equals("CHAT_TITLE_EDITED") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x0697, code lost:
        r4 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:426:0x06a1, code lost:
        if (r14.equals("PINNED_NOTEXT") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x06a3, code lost:
        r4 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x06ad, code lost:
        if (r14.equals("MESSAGE_TEXT") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x06af, code lost:
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:432:0x06b8, code lost:
        if (r14.equals("MESSAGE_QUIZ") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x06ba, code lost:
        r4 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x06c4, code lost:
        if (r14.equals("MESSAGE_POLL") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x06c6, code lost:
        r4 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x06d0, code lost:
        if (r14.equals("MESSAGE_GAME") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x06d2, code lost:
        r4 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x06dc, code lost:
        if (r14.equals("MESSAGE_FWDS") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x06de, code lost:
        r4 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:444:0x06e8, code lost:
        if (r14.equals("MESSAGE_DOCS") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x06ea, code lost:
        r4 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x06f4, code lost:
        if (r14.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x06f6, code lost:
        r4 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x0700, code lost:
        if (r14.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x0702, code lost:
        r4 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x070c, code lost:
        if (r14.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x070e, code lost:
        r4 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:456:0x0718, code lost:
        if (r14.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x071a, code lost:
        r4 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x0724, code lost:
        if (r14.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:460:0x0726, code lost:
        r4 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x0730, code lost:
        if (r14.equals("CHAT_MESSAGE_DOCS") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x0732, code lost:
        r4 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x073c, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x073e, code lost:
        r4 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x0748, code lost:
        if (r14.equals("PINNED_GEOLIVE") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x074a, code lost:
        r4 = 'b';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x0754, code lost:
        if (r14.equals("MESSAGE_CONTACT") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:472:0x0756, code lost:
        r4 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x0760, code lost:
        if (r14.equals("PINNED_VIDEO") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0762, code lost:
        r4 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x076c, code lost:
        if (r14.equals("PINNED_ROUND") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x076e, code lost:
        r4 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:480:0x0778, code lost:
        if (r14.equals("PINNED_PHOTO") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x077a, code lost:
        r4 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x0784, code lost:
        if (r14.equals("PINNED_AUDIO") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x0786, code lost:
        r4 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:486:0x0790, code lost:
        if (r14.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x0792, code lost:
        r4 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x079b, code lost:
        if (r14.equals("CHAT_VOICECHAT_INVITE_YOU") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x079d, code lost:
        r4 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x07a7, code lost:
        if (r14.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x07a9, code lost:
        r4 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x07b3, code lost:
        if (r14.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x07b5, code lost:
        r4 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x07bf, code lost:
        if (r14.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x07c1, code lost:
        r4 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x07cb, code lost:
        if (r14.equals("CHAT_VOICECHAT_END") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x07cd, code lost:
        r4 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x07d7, code lost:
        if (r14.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x07d9, code lost:
        r4 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x07e3, code lost:
        if (r14.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x07e5, code lost:
        r4 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x07ef, code lost:
        if (r14.equals("MESSAGES") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x07f1, code lost:
        r4 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x07fb, code lost:
        if (r14.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x07fd, code lost:
        r4 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x0807, code lost:
        if (r14.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x0809, code lost:
        r4 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:519:0x0813, code lost:
        if (r14.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0815, code lost:
        r4 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x081f, code lost:
        if (r14.equals("CHAT_VOICECHAT_INVITE") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x0821, code lost:
        r4 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x082b, code lost:
        if (r14.equals("CHAT_LEFT") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x082d, code lost:
        r4 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x0837, code lost:
        if (r14.equals("CHAT_ADD_YOU") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0839, code lost:
        r4 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0843, code lost:
        if (r14.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x0845, code lost:
        r4 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x084f, code lost:
        if (r14.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x0851, code lost:
        r4 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x085a, code lost:
        if (r14.equals("AUTH_REGION") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:538:0x085c, code lost:
        r4 = 'j';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:540:0x0866, code lost:
        if (r14.equals("CONTACT_JOINED") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0868, code lost:
        r4 = 'h';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x0872, code lost:
        if (r14.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x0874, code lost:
        r4 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x087e, code lost:
        if (r14.equals("ENCRYPTION_REQUEST") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x0880, code lost:
        r4 = 'l';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x088a, code lost:
        if (r14.equals("MESSAGE_GEOLIVE") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x088c, code lost:
        r4 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:552:0x0896, code lost:
        if (r14.equals("CHAT_DELETE_YOU") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x0898, code lost:
        r4 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x08a2, code lost:
        if (r14.equals("AUTH_UNKNOWN") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x08a4, code lost:
        r4 = 'i';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x08ae, code lost:
        if (r14.equals("PINNED_GIF") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x08b0, code lost:
        r4 = 'f';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x08ba, code lost:
        if (r14.equals("PINNED_GEO") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x08bc, code lost:
        r4 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x08c6, code lost:
        if (r14.equals("PINNED_DOC") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x08c8, code lost:
        r4 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x08d2, code lost:
        if (r14.equals("PINNED_GAME_SCORE") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x08d4, code lost:
        r4 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x08de, code lost:
        if (r14.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x08e0, code lost:
        r4 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x08ea, code lost:
        if (r14.equals("PHONE_CALL_REQUEST") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x08ec, code lost:
        r4 = 'n';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:576:0x08f6, code lost:
        if (r14.equals("PINNED_STICKER") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x08f8, code lost:
        r4 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x0902, code lost:
        if (r14.equals("PINNED_TEXT") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x0904, code lost:
        r4 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:582:0x090e, code lost:
        if (r14.equals("PINNED_QUIZ") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x0910, code lost:
        r4 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x091a, code lost:
        if (r14.equals("PINNED_POLL") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x091c, code lost:
        r4 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:588:0x0926, code lost:
        if (r14.equals("PINNED_GAME") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x0928, code lost:
        r4 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x0932, code lost:
        if (r14.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x0934, code lost:
        r4 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x093e, code lost:
        if (r14.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x0940, code lost:
        r4 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0949, code lost:
        if (r14.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x094b, code lost:
        r4 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:600:0x0954, code lost:
        if (r14.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0956, code lost:
        r4 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0960, code lost:
        if (r14.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x0962, code lost:
        r4 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x096c, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x096e, code lost:
        r4 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x0978, code lost:
        if (r14.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x097a, code lost:
        r4 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:612:0x0984, code lost:
        if (r14.equals("CHANNEL_MESSAGE_DOCS") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x0986, code lost:
        r4 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x0990, code lost:
        if (r14.equals("PINNED_INVOICE") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x0992, code lost:
        r4 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:618:0x099c, code lost:
        if (r14.equals("CHAT_RETURNED") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x099e, code lost:
        r4 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x09a8, code lost:
        if (r14.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x09aa, code lost:
        r4 = 'g';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x09b4, code lost:
        if (r14.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:625:0x09b6, code lost:
        r4 = 'm';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:627:0x09c0, code lost:
        if (r14.equals("MESSAGE_VIDEO") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x09c2, code lost:
        r4 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x09cb, code lost:
        if (r14.equals("MESSAGE_ROUND") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x09cd, code lost:
        r4 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x09d7, code lost:
        if (r14.equals("MESSAGE_PHOTO") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x09d9, code lost:
        r4 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x09e2, code lost:
        if (r14.equals("MESSAGE_MUTED") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x09e4, code lost:
        r4 = 'o';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x09ee, code lost:
        if (r14.equals("MESSAGE_AUDIO") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x09f0, code lost:
        r4 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x09fa, code lost:
        if (r14.equals("CHAT_MESSAGES") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x09fc, code lost:
        r4 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x0a06, code lost:
        if (r14.equals("CHAT_VOICECHAT_START") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x0a08, code lost:
        r4 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x0a12, code lost:
        if (r14.equals("CHAT_REQ_JOINED") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0a14, code lost:
        r4 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x0a1e, code lost:
        if (r14.equals("CHAT_JOINED") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0a20, code lost:
        r4 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0a2a, code lost:
        if (r14.equals("CHAT_ADD_MEMBER") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0a2c, code lost:
        r4 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0a35, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0a37, code lost:
        r4 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0a40, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x0a42, code lost:
        r4 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x0a4b, code lost:
        if (r14.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0a4d, code lost:
        r4 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0a56, code lost:
        if (r14.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x0a58, code lost:
        r4 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0a61, code lost:
        if (r14.equals("MESSAGE_STICKER") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x0a63, code lost:
        r4 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0a6c, code lost:
        if (r14.equals("CHAT_CREATED") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:673:0x0a6e, code lost:
        r4 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0a77, code lost:
        if (r14.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0a79, code lost:
        r4 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0a82, code lost:
        if (r14.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:679:0x0a84, code lost:
        r4 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0a87, code lost:
        r4 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0a88, code lost:
        r41 = r11;
        r42 = r9;
        r43 = r1;
        r45 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x0aa0, code lost:
        switch(r4) {
            case 0: goto L_0x1ba6;
            case 1: goto L_0x1ba6;
            case 2: goto L_0x1b84;
            case 3: goto L_0x1b67;
            case 4: goto L_0x1b4a;
            case 5: goto L_0x1b2d;
            case 6: goto L_0x1b0f;
            case 7: goto L_0x1af7;
            case 8: goto L_0x1ad9;
            case 9: goto L_0x1abb;
            case 10: goto L_0x1a60;
            case 11: goto L_0x1a42;
            case 12: goto L_0x1a1f;
            case 13: goto L_0x19fc;
            case 14: goto L_0x19d9;
            case 15: goto L_0x19bb;
            case 16: goto L_0x199d;
            case 17: goto L_0x197f;
            case 18: goto L_0x195c;
            case 19: goto L_0x193d;
            case 20: goto L_0x193d;
            case 21: goto L_0x191a;
            case 22: goto L_0x18f2;
            case 23: goto L_0x18ce;
            case 24: goto L_0x18ab;
            case 25: goto L_0x1888;
            case 26: goto L_0x1863;
            case 27: goto L_0x184b;
            case 28: goto L_0x182d;
            case 29: goto L_0x180f;
            case 30: goto L_0x17f1;
            case 31: goto L_0x17d3;
            case 32: goto L_0x17b5;
            case 33: goto L_0x175a;
            case 34: goto L_0x173c;
            case 35: goto L_0x1719;
            case 36: goto L_0x16f6;
            case 37: goto L_0x16d3;
            case 38: goto L_0x16b5;
            case 39: goto L_0x1697;
            case 40: goto L_0x1679;
            case 41: goto L_0x165b;
            case 42: goto L_0x1631;
            case 43: goto L_0x160d;
            case 44: goto L_0x15e9;
            case 45: goto L_0x15c5;
            case 46: goto L_0x159f;
            case 47: goto L_0x158a;
            case 48: goto L_0x1569;
            case 49: goto L_0x1546;
            case 50: goto L_0x1523;
            case 51: goto L_0x1500;
            case 52: goto L_0x14dd;
            case 53: goto L_0x14ba;
            case 54: goto L_0x1441;
            case 55: goto L_0x141e;
            case 56: goto L_0x13f6;
            case 57: goto L_0x13ce;
            case 58: goto L_0x13a6;
            case 59: goto L_0x1383;
            case 60: goto L_0x1360;
            case 61: goto L_0x133d;
            case 62: goto L_0x1315;
            case 63: goto L_0x12f1;
            case 64: goto L_0x12c9;
            case 65: goto L_0x12af;
            case 66: goto L_0x12af;
            case 67: goto L_0x1295;
            case 68: goto L_0x127b;
            case 69: goto L_0x125c;
            case 70: goto L_0x1242;
            case 71: goto L_0x1223;
            case 72: goto L_0x1209;
            case 73: goto L_0x11ef;
            case 74: goto L_0x11d5;
            case 75: goto L_0x11bb;
            case 76: goto L_0x11a1;
            case 77: goto L_0x1187;
            case 78: goto L_0x116d;
            case 79: goto L_0x1153;
            case 80: goto L_0x1126;
            case 81: goto L_0x10fd;
            case 82: goto L_0x10d4;
            case 83: goto L_0x10ab;
            case 84: goto L_0x1080;
            case 85: goto L_0x1066;
            case 86: goto L_0x100f;
            case 87: goto L_0x0fc2;
            case 88: goto L_0x0var_;
            case 89: goto L_0x0var_;
            case 90: goto L_0x0edb;
            case 91: goto L_0x0e8e;
            case 92: goto L_0x0dd5;
            case 93: goto L_0x0d88;
            case 94: goto L_0x0d31;
            case 95: goto L_0x0cda;
            case 96: goto L_0x0CLASSNAME;
            case 97: goto L_0x0c3d;
            case 98: goto L_0x0bf4;
            case 99: goto L_0x0ba9;
            case 100: goto L_0x0b5e;
            case 101: goto L_0x0b13;
            case 102: goto L_0x0ac8;
            case 103: goto L_0x0aad;
            case 104: goto L_0x0aa9;
            case 105: goto L_0x0aa9;
            case 106: goto L_0x0aa9;
            case 107: goto L_0x0aa9;
            case 108: goto L_0x0aa9;
            case 109: goto L_0x0aa9;
            case 110: goto L_0x0aa9;
            case 111: goto L_0x0aa9;
            case 112: goto L_0x0aa9;
            default: goto L_0x0aa3;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0aa3, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0aa9, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:?, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
        r4 = r22;
        r17 = null;
        r22 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x0acc, code lost:
        if (r12 <= 0) goto L_0x0ae6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0ace, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0ae6, code lost:
        if (r8 == false) goto L_0x0b00;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0ae8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0b00, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0b17, code lost:
        if (r12 <= 0) goto L_0x0b31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0b19, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0b31, code lost:
        if (r8 == false) goto L_0x0b4b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0b33, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0b4b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0b62, code lost:
        if (r12 <= 0) goto L_0x0b7c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0b64, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0b7c, code lost:
        if (r8 == false) goto L_0x0b96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0b7e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0b96, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0bad, code lost:
        if (r12 <= 0) goto L_0x0bc7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0baf, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0bc7, code lost:
        if (r8 == false) goto L_0x0be1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0bc9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0be1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0bf8, code lost:
        if (r12 <= 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0bfa, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0CLASSNAME, code lost:
        if (r8 == false) goto L_0x0c2b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0c2b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:0x0CLASSNAME, code lost:
        if (r12 <= 0) goto L_0x0c5a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0c5a, code lost:
        if (r8 == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0c5c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0CLASSNAME, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0c8c, code lost:
        if (r12 <= 0) goto L_0x0ca5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0c8e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0ca5, code lost:
        if (r8 == false) goto L_0x0cc3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x0ca7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r3[0], r3[2], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0cc3, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0cda, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0ce0, code lost:
        if (r12 <= 0) goto L_0x0cfa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x0ce2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0cfa, code lost:
        if (r8 == false) goto L_0x0d19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0cfc, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r3[0], r3[2], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0d19, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x0d31, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0d37, code lost:
        if (r12 <= 0) goto L_0x0d51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x0d39, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0d51, code lost:
        if (r8 == false) goto L_0x0d70;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x0d53, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r3[0], r3[2], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x0d70, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x0d88, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x0d8e, code lost:
        if (r12 <= 0) goto L_0x0da8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x0d90, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x0da8, code lost:
        if (r8 == false) goto L_0x0dc2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0daa, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x0dc2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x0dd5, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x0ddb, code lost:
        if (r12 <= 0) goto L_0x0e14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x0ddf, code lost:
        if (r3.length <= 1) goto L_0x0e01;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x0de7, code lost:
        if (android.text.TextUtils.isEmpty(r3[1]) != false) goto L_0x0e01;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x0de9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x0e01, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x0e14, code lost:
        if (r8 == false) goto L_0x0e57;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x0e18, code lost:
        if (r3.length <= 2) goto L_0x0e3f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x0e20, code lost:
        if (android.text.TextUtils.isEmpty(r3[2]) != false) goto L_0x0e3f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x0e22, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r3[0], r3[2], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:0x0e3f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x0e59, code lost:
        if (r3.length <= 1) goto L_0x0e7b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x0e61, code lost:
        if (android.text.TextUtils.isEmpty(r3[1]) != false) goto L_0x0e7b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x0e63, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x0e7b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x0e8e, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x0e94, code lost:
        if (r12 <= 0) goto L_0x0eae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x0e96, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x0eae, code lost:
        if (r8 == false) goto L_0x0ec8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x0eb0, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x0ec8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x0edb, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x0ee1, code lost:
        if (r12 <= 0) goto L_0x0efb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x0ee3, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x0efb, code lost:
        if (r8 == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x0efd, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x0var_, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x0f2e, code lost:
        if (r12 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x0var_, code lost:
        if (r8 == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x0f4a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x0var_, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x0f7b, code lost:
        if (r12 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x0f7d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x0var_, code lost:
        if (r8 == false) goto L_0x0faf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x0faf, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x0fc2, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x0fc8, code lost:
        if (r12 <= 0) goto L_0x0fe2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x0fca, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x0fe2, code lost:
        if (r8 == false) goto L_0x0ffc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x0fe4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x0ffc, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x100f, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x1015, code lost:
        if (r12 <= 0) goto L_0x102f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x1017, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x102f, code lost:
        if (r8 == false) goto L_0x104e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x1031, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r3[0], r3[1], r3[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x104e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x1066, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x1080, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r3[0], r3[1], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r3[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x10ab, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r3[0], r3[1], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r3[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x10d4, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r3[0], r3[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r3[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x10fd, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r3[0], r3[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r3[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x1126, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r3[0], r3[1], org.telegram.messenger.LocaleController.formatPluralString(r27, org.telegram.messenger.Utilities.parseInt(r3[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x1153, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("UserAcceptedToGroupPushWithGroup", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x116d, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x1187, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x11a1, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x11bb, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x11d5, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x11ef, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x1209, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupEndedCall", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x1223, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r3[0], r3[1], r3[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x1242, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x125c, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r3[0], r3[1], r3[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x127b, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x1295, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x12af, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x12c9, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r3[0], r3[1], r3[2]);
        r2 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x12f1, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r3[0], r3[1], r3[2], r3[3]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x1315, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r3[0], r3[1], r3[2]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x133d, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x1360, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x1383, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x13a6, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r3[0], r3[1], r3[2]);
        r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x13ce, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r3[0], r3[1], r3[2]);
        r2 = org.telegram.messenger.LocaleController.getString("PollQuiz", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x13f6, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r3[0], r3[1], r3[2]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x141e, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x1441, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x1445, code lost:
        if (r3.length <= 2) goto L_0x1487;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x144d, code lost:
        if (android.text.TextUtils.isEmpty(r3[2]) != false) goto L_0x1487;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x144f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r3[0], r3[1], r3[2]);
        r2 = r3[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x1487, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r3[0], r3[1]);
        r2 = r3[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x14ba, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x14dd, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x1500, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x1523, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x1546, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x1569, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r3[0], r3[1], r3[2]);
        r2 = r3[2];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x158a, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:851:0x159f, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x15c5, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x15e9, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x160d, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x1631, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:856:0x165b, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x1679, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x1697, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x16b5, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x16d3, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x16f6, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:862:0x1719, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:863:0x173c, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:864:0x175a, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:865:0x175e, code lost:
        if (r3.length <= 1) goto L_0x179b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x1766, code lost:
        if (android.text.TextUtils.isEmpty(r3[1]) != false) goto L_0x179b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x1768, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r3[0], r3[1]);
        r2 = r3[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x179b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x17b5, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:871:0x17d3, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x17f1, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x180f, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x182d, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x184b, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x185e, code lost:
        r22 = r39;
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x1863, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x1888, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x18ab, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x18ce, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x18f2, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString(r27, org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x191a, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x193d, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r3[0], r3[1], r3[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x195c, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:0x197f, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x199d, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x19bb, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x19d9, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x19fc, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x1a1f, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x1a42, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x1a60, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x1a64, code lost:
        if (r3.length <= 1) goto L_0x1aa1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x1a6c, code lost:
        if (android.text.TextUtils.isEmpty(r3[1]) != false) goto L_0x1aa1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x1a6e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r3[0], r3[1]);
        r2 = r3[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:897:0x1aa1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x1abb, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:899:0x1ad9, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:0x1af7, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x1b0b, code lost:
        r22 = r39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x1b0f, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:0x1b2d, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:0x1b4a, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x1b67, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:906:0x1b84, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:0x1ba0, code lost:
        r17 = r2;
        r22 = r39;
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x1ba6, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r3[0], r3[1]);
        r2 = r3[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:909:0x1bc1, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1bd7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:910:0x1bc3, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:0x1bd7, code lost:
        r22 = r39;
        r1 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:0x1bda, code lost:
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:913:0x1bdb, code lost:
        r17 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:0x1bdd, code lost:
        if (r1 == null) goto L_0x1cbb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:?, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_message();
        r3.id = r10;
        r3.random_id = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:917:0x1bea, code lost:
        if (r17 == null) goto L_0x1bef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:918:0x1bec, code lost:
        r5 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:0x1bef, code lost:
        r5 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:920:0x1bf0, code lost:
        r3.message = r5;
        r3.date = (int) (r51 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x1bf9, code lost:
        if (r38 == false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:?, code lost:
        r3.action = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x1CLASSNAME, code lost:
        if (r37 == false) goto L_0x1c0b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x1CLASSNAME, code lost:
        r3.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:?, code lost:
        r3.dialog_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:0x1CLASSNAME, code lost:
        if (r45 == 0) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:?, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r3.peer_id = r5;
        r5.channel_id = r45;
        r12 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x1CLASSNAME, code lost:
        if (r25 == 0) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x1CLASSNAME, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r3.peer_id = r5;
        r12 = r25;
        r5.chat_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:935:0x1CLASSNAME, code lost:
        r12 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:?, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r3.peer_id = r5;
        r5.user_id = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:0x1CLASSNAME, code lost:
        r3.flags |= 256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:0x1c4a, code lost:
        if (r43 == 0) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:?, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r3.from_id = r5;
        r5.chat_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:944:0x1c5a, code lost:
        if (r32 == 0) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:0x1c5c, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r3.from_id = r5;
        r5.channel_id = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x1c6c, code lost:
        if (r35 == 0) goto L_0x1c7a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x1c6e, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r3.from_id = r5;
        r5.user_id = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:?, code lost:
        r3.from_id = r3.peer_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x1c7e, code lost:
        if (r29 != false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x1CLASSNAME, code lost:
        if (r38 == false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:0x1CLASSNAME, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x1CLASSNAME, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:0x1CLASSNAME, code lost:
        r3.mentioned = r5;
        r3.silent = r34;
        r3.from_scheduled = r4;
        r18 = new org.telegram.messenger.MessageObject(r28, r3, r1, r22, r42, r2, r41, r37, r40);
        r1 = new java.util.ArrayList();
        r1.add(r18);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:957:0x1cb1, code lost:
        r3 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:0x1cb3, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:?, code lost:
        org.telegram.messenger.NotificationsController.getInstance(r28).processNewMessages(r1, true, true, r3.countDownLatch);
        r8 = false;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x1cbb, code lost:
        r3 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:0x1cbe, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:962:0x1cbf, code lost:
        r3 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:0x1cc2, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:964:0x1cc3, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:965:0x1cc5, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:966:0x1cc6, code lost:
        r8 = true;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:967:0x1cc7, code lost:
        if (r8 == false) goto L_0x1cce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:968:0x1cc9, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:969:0x1cce, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r28);
        org.telegram.tgnet.ConnectionsManager.getInstance(r28).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:0x1cda, code lost:
        r0 = th;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:971:0x1cdc, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:972:0x1cdd, code lost:
        r3 = r1;
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:0x1ce1, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:974:0x1ce2, code lost:
        r3 = r1;
        r14 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:975:0x1ce4, code lost:
        r1 = r0;
        r7 = r16;
        r4 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:976:0x1ceb, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:977:0x1cec, code lost:
        r3 = r1;
        r28 = r4;
        r14 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:978:0x1cf2, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:979:0x1cf3, code lost:
        r3 = r1;
        r28 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:980:0x1cf8, code lost:
        r3 = r1;
        r28 = r4;
        r16 = r7;
        r14 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:983:0x1d02, code lost:
        r4 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:985:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1(r4));
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:986:0x1d0f, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:987:0x1d10, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:988:0x1d11, code lost:
        r4 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:989:0x1d15, code lost:
        r16 = r7;
        r14 = r9;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0(r4));
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:990:0x1d26, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x1d27, code lost:
        r3 = r1;
        r16 = r7;
        r14 = r9;
        r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification();
        r1.popup = false;
        r1.flags = 2;
        r1.inbox_date = (int) (r51 / 1000);
        r1.message = r5.getString("message");
        r1.type = "announcement";
        r1.media = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty();
        r2 = new org.telegram.tgnet.TLRPC$TL_updates();
        r2.updates.add(r1);
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3(r4, r2));
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:992:0x1d70, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:993:0x1d71, code lost:
        r3 = r1;
        r16 = r7;
        r14 = r9;
        r1 = r11.getInt("dc");
        r2 = r11.getString("addr").split(":");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:994:0x1d89, code lost:
        if (r2.length == 2) goto L_0x1d91;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:995:0x1d8b, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:996:0x1d90, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:997:0x1d91, code lost:
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).applyDatacenterAddress(r1, r2[0], java.lang.Integer.parseInt(r2[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:998:0x1dae, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:999:0x1daf, code lost:
        r0 = th;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1013:0x1dd6  */
    /* JADX WARNING: Removed duplicated region for block: B:1014:0x1de6  */
    /* JADX WARNING: Removed duplicated region for block: B:1017:0x1ded  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$onMessageReceived$3(java.util.Map r50, long r51) {
        /*
            r49 = this;
            r1 = r49
            r2 = r50
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x000d
            java.lang.String r3 = "GCM START PROCESSING"
            org.telegram.messenger.FileLog.d(r3)
        L_0x000d:
            java.lang.String r5 = "p"
            java.lang.Object r5 = r2.get(r5)     // Catch:{ all -> 0x1dcd }
            boolean r6 = r5 instanceof java.lang.String     // Catch:{ all -> 0x1dcd }
            if (r6 != 0) goto L_0x002d
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0024 }
            if (r2 == 0) goto L_0x0020
            java.lang.String r2 = "GCM DECRYPT ERROR 1"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x0024 }
        L_0x0020:
            r49.onDecryptError()     // Catch:{ all -> 0x0024 }
            return
        L_0x0024:
            r0 = move-exception
            r3 = r1
            r2 = -1
            r4 = -1
            r7 = 0
        L_0x0029:
            r14 = 0
        L_0x002a:
            r1 = r0
            goto L_0x1dd4
        L_0x002d:
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ all -> 0x1dcd }
            r6 = 8
            byte[] r5 = android.util.Base64.decode(r5, r6)     // Catch:{ all -> 0x1dcd }
            org.telegram.tgnet.NativeByteBuffer r7 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x1dcd }
            int r8 = r5.length     // Catch:{ all -> 0x1dcd }
            r7.<init>((int) r8)     // Catch:{ all -> 0x1dcd }
            r7.writeBytes((byte[]) r5)     // Catch:{ all -> 0x1dcd }
            r8 = 0
            r7.position(r8)     // Catch:{ all -> 0x1dcd }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1dcd }
            if (r9 != 0) goto L_0x0057
            byte[] r9 = new byte[r6]     // Catch:{ all -> 0x0024 }
            org.telegram.messenger.SharedConfig.pushAuthKeyId = r9     // Catch:{ all -> 0x0024 }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x0024 }
            byte[] r9 = org.telegram.messenger.Utilities.computeSHA1((byte[]) r9)     // Catch:{ all -> 0x0024 }
            int r10 = r9.length     // Catch:{ all -> 0x0024 }
            int r10 = r10 - r6
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x0024 }
            java.lang.System.arraycopy(r9, r10, r11, r8, r6)     // Catch:{ all -> 0x0024 }
        L_0x0057:
            byte[] r9 = new byte[r6]     // Catch:{ all -> 0x1dcd }
            r10 = 1
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1dcd }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1dcd }
            boolean r11 = java.util.Arrays.equals(r11, r9)     // Catch:{ all -> 0x1dcd }
            r12 = 3
            r13 = 2
            if (r11 != 0) goto L_0x0092
            r49.onDecryptError()     // Catch:{ all -> 0x0024 }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0024 }
            if (r2 == 0) goto L_0x0091
            java.util.Locale r2 = java.util.Locale.US     // Catch:{ all -> 0x0024 }
            java.lang.String r5 = "GCM DECRYPT ERROR 2 k1=%s k2=%s, key=%s"
            java.lang.Object[] r6 = new java.lang.Object[r12]     // Catch:{ all -> 0x0024 }
            byte[] r7 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x0024 }
            java.lang.String r7 = org.telegram.messenger.Utilities.bytesToHex(r7)     // Catch:{ all -> 0x0024 }
            r6[r8] = r7     // Catch:{ all -> 0x0024 }
            java.lang.String r7 = org.telegram.messenger.Utilities.bytesToHex(r9)     // Catch:{ all -> 0x0024 }
            r6[r10] = r7     // Catch:{ all -> 0x0024 }
            byte[] r7 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x0024 }
            java.lang.String r7 = org.telegram.messenger.Utilities.bytesToHex(r7)     // Catch:{ all -> 0x0024 }
            r6[r13] = r7     // Catch:{ all -> 0x0024 }
            java.lang.String r2 = java.lang.String.format(r2, r5, r6)     // Catch:{ all -> 0x0024 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x0024 }
        L_0x0091:
            return
        L_0x0092:
            r9 = 16
            byte[] r9 = new byte[r9]     // Catch:{ all -> 0x1dcd }
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1dcd }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1dcd }
            org.telegram.messenger.MessageKeyData r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13)     // Catch:{ all -> 0x1dcd }
            java.nio.ByteBuffer r14 = r7.buffer     // Catch:{ all -> 0x1dcd }
            byte[] r15 = r11.aesKey     // Catch:{ all -> 0x1dcd }
            byte[] r11 = r11.aesIv     // Catch:{ all -> 0x1dcd }
            r17 = 0
            r18 = 0
            r19 = 24
            int r5 = r5.length     // Catch:{ all -> 0x1dcd }
            int r20 = r5 + -24
            r16 = r11
            org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x1dcd }
            byte[] r21 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1dcd }
            r22 = 96
            r23 = 32
            java.nio.ByteBuffer r5 = r7.buffer     // Catch:{ all -> 0x1dcd }
            r25 = 24
            int r26 = r5.limit()     // Catch:{ all -> 0x1dcd }
            r24 = r5
            byte[] r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26)     // Catch:{ all -> 0x1dcd }
            boolean r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6)     // Catch:{ all -> 0x1dcd }
            if (r5 != 0) goto L_0x00e8
            r49.onDecryptError()     // Catch:{ all -> 0x0024 }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0024 }
            if (r2 == 0) goto L_0x00e7
            java.lang.String r2 = "GCM DECRYPT ERROR 3, key = %s"
            java.lang.Object[] r5 = new java.lang.Object[r10]     // Catch:{ all -> 0x0024 }
            byte[] r6 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x0024 }
            java.lang.String r6 = org.telegram.messenger.Utilities.bytesToHex(r6)     // Catch:{ all -> 0x0024 }
            r5[r8] = r6     // Catch:{ all -> 0x0024 }
            java.lang.String r2 = java.lang.String.format(r2, r5)     // Catch:{ all -> 0x0024 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x0024 }
        L_0x00e7:
            return
        L_0x00e8:
            int r5 = r7.readInt32(r10)     // Catch:{ all -> 0x1dcd }
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x1dcd }
            r7.readBytes(r5, r10)     // Catch:{ all -> 0x1dcd }
            java.lang.String r7 = new java.lang.String     // Catch:{ all -> 0x1dcd }
            r7.<init>(r5)     // Catch:{ all -> 0x1dcd }
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ all -> 0x1dc5 }
            r5.<init>(r7)     // Catch:{ all -> 0x1dc5 }
            java.lang.String r9 = "loc_key"
            boolean r9 = r5.has(r9)     // Catch:{ all -> 0x1dc5 }
            if (r9 == 0) goto L_0x0110
            java.lang.String r9 = "loc_key"
            java.lang.String r9 = r5.getString(r9)     // Catch:{ all -> 0x010a }
            goto L_0x0112
        L_0x010a:
            r0 = move-exception
            r3 = r1
            r2 = -1
            r4 = -1
            goto L_0x0029
        L_0x0110:
            java.lang.String r9 = ""
        L_0x0112:
            java.lang.String r11 = "custom"
            java.lang.Object r11 = r5.get(r11)     // Catch:{ all -> 0x1dbc }
            boolean r11 = r11 instanceof org.json.JSONObject     // Catch:{ all -> 0x1dbc }
            if (r11 == 0) goto L_0x012a
            java.lang.String r11 = "custom"
            org.json.JSONObject r11 = r5.getJSONObject(r11)     // Catch:{ all -> 0x0123 }
            goto L_0x012f
        L_0x0123:
            r0 = move-exception
            r3 = r1
            r14 = r9
            r2 = -1
            r4 = -1
            goto L_0x002a
        L_0x012a:
            org.json.JSONObject r11 = new org.json.JSONObject     // Catch:{ all -> 0x1dbc }
            r11.<init>()     // Catch:{ all -> 0x1dbc }
        L_0x012f:
            java.lang.String r14 = "user_id"
            boolean r14 = r5.has(r14)     // Catch:{ all -> 0x1dbc }
            if (r14 == 0) goto L_0x013e
            java.lang.String r14 = "user_id"
            java.lang.Object r14 = r5.get(r14)     // Catch:{ all -> 0x0123 }
            goto L_0x013f
        L_0x013e:
            r14 = 0
        L_0x013f:
            if (r14 != 0) goto L_0x014c
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x0123 }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x0123 }
            long r14 = r14.getClientUserId()     // Catch:{ all -> 0x0123 }
            goto L_0x017c
        L_0x014c:
            boolean r15 = r14 instanceof java.lang.Long     // Catch:{ all -> 0x1dbc }
            if (r15 == 0) goto L_0x0157
            java.lang.Long r14 = (java.lang.Long) r14     // Catch:{ all -> 0x0123 }
            long r14 = r14.longValue()     // Catch:{ all -> 0x0123 }
            goto L_0x017c
        L_0x0157:
            boolean r15 = r14 instanceof java.lang.Integer     // Catch:{ all -> 0x1dbc }
            if (r15 == 0) goto L_0x0163
            java.lang.Integer r14 = (java.lang.Integer) r14     // Catch:{ all -> 0x0123 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0123 }
        L_0x0161:
            long r14 = (long) r14
            goto L_0x017c
        L_0x0163:
            boolean r15 = r14 instanceof java.lang.String     // Catch:{ all -> 0x1dbc }
            if (r15 == 0) goto L_0x0172
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ all -> 0x0123 }
            java.lang.Integer r14 = org.telegram.messenger.Utilities.parseInt(r14)     // Catch:{ all -> 0x0123 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0123 }
            goto L_0x0161
        L_0x0172:
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1dbc }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x1dbc }
            long r14 = r14.getClientUserId()     // Catch:{ all -> 0x1dbc }
        L_0x017c:
            int r16 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1dbc }
            r4 = 0
        L_0x017f:
            if (r4 >= r12) goto L_0x0192
            org.telegram.messenger.UserConfig r18 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x0123 }
            long r18 = r18.getClientUserId()     // Catch:{ all -> 0x0123 }
            int r20 = (r18 > r14 ? 1 : (r18 == r14 ? 0 : -1))
            if (r20 != 0) goto L_0x018f
            r14 = 1
            goto L_0x0195
        L_0x018f:
            int r4 = r4 + 1
            goto L_0x017f
        L_0x0192:
            r4 = r16
            r14 = 0
        L_0x0195:
            if (r14 != 0) goto L_0x01a6
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0123 }
            if (r2 == 0) goto L_0x01a0
            java.lang.String r2 = "GCM ACCOUNT NOT FOUND"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x0123 }
        L_0x01a0:
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x0123 }
            r2.countDown()     // Catch:{ all -> 0x0123 }
            return
        L_0x01a6:
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x1db4 }
            boolean r14 = r14.isClientActivated()     // Catch:{ all -> 0x1db4 }
            if (r14 != 0) goto L_0x01c5
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x01bf }
            if (r2 == 0) goto L_0x01b9
            java.lang.String r2 = "GCM ACCOUNT NOT ACTIVATED"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x01bf }
        L_0x01b9:
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x01bf }
            r2.countDown()     // Catch:{ all -> 0x01bf }
            return
        L_0x01bf:
            r0 = move-exception
        L_0x01c0:
            r3 = r1
            r14 = r9
        L_0x01c2:
            r2 = -1
            goto L_0x002a
        L_0x01c5:
            java.lang.String r14 = "google.sent_time"
            r2.get(r14)     // Catch:{ all -> 0x1db4 }
            int r2 = r9.hashCode()     // Catch:{ all -> 0x1db4 }
            switch(r2) {
                case -1963663249: goto L_0x01f0;
                case -920689527: goto L_0x01e6;
                case 633004703: goto L_0x01dc;
                case 1365673842: goto L_0x01d2;
                default: goto L_0x01d1;
            }
        L_0x01d1:
            goto L_0x01fa
        L_0x01d2:
            java.lang.String r2 = "GEO_LIVE_PENDING"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01bf }
            if (r2 == 0) goto L_0x01fa
            r2 = 3
            goto L_0x01fb
        L_0x01dc:
            java.lang.String r2 = "MESSAGE_ANNOUNCEMENT"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01bf }
            if (r2 == 0) goto L_0x01fa
            r2 = 1
            goto L_0x01fb
        L_0x01e6:
            java.lang.String r2 = "DC_UPDATE"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01bf }
            if (r2 == 0) goto L_0x01fa
            r2 = 0
            goto L_0x01fb
        L_0x01f0:
            java.lang.String r2 = "SESSION_REVOKE"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01bf }
            if (r2 == 0) goto L_0x01fa
            r2 = 2
            goto L_0x01fb
        L_0x01fa:
            r2 = -1
        L_0x01fb:
            if (r2 == 0) goto L_0x1d71
            if (r2 == r10) goto L_0x1d27
            if (r2 == r13) goto L_0x1d15
            if (r2 == r12) goto L_0x1cf8
            java.lang.String r2 = "channel_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1cf2 }
            r14 = 0
            if (r2 == 0) goto L_0x0221
            java.lang.String r2 = "channel_id"
            r16 = r7
            long r6 = r11.getLong(r2)     // Catch:{ all -> 0x0217 }
            long r12 = -r6
            goto L_0x0225
        L_0x0217:
            r0 = move-exception
            r3 = r1
            r14 = r9
            r7 = r16
            goto L_0x01c2
        L_0x021d:
            r0 = move-exception
            r16 = r7
            goto L_0x01c0
        L_0x0221:
            r16 = r7
            r6 = r14
            r12 = r6
        L_0x0225:
            java.lang.String r2 = "from_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1ceb }
            if (r2 == 0) goto L_0x0237
            java.lang.String r2 = "from_id"
            long r12 = r11.getLong(r2)     // Catch:{ all -> 0x0217 }
            r28 = r4
            r3 = r12
            goto L_0x023a
        L_0x0237:
            r28 = r4
            r3 = r14
        L_0x023a:
            java.lang.String r2 = "chat_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1ce1 }
            if (r2 == 0) goto L_0x025d
            java.lang.String r2 = "chat_id"
            long r12 = r11.getLong(r2)     // Catch:{ all -> 0x0251 }
            r29 = r9
            long r8 = -r12
            r47 = r8
            r8 = r12
            r12 = r47
            goto L_0x0260
        L_0x0251:
            r0 = move-exception
            r29 = r9
        L_0x0254:
            r3 = r1
            r7 = r16
            r4 = r28
            r14 = r29
            goto L_0x01c2
        L_0x025d:
            r29 = r9
            r8 = r14
        L_0x0260:
            java.lang.String r2 = "encryption_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1cdc }
            if (r2 == 0) goto L_0x0276
            java.lang.String r2 = "encryption_id"
            int r2 = r11.getInt(r2)     // Catch:{ all -> 0x0274 }
            long r12 = (long) r2     // Catch:{ all -> 0x0274 }
            long r12 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r12)     // Catch:{ all -> 0x0274 }
            goto L_0x0276
        L_0x0274:
            r0 = move-exception
            goto L_0x0254
        L_0x0276:
            java.lang.String r2 = "schedule"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1cdc }
            if (r2 == 0) goto L_0x0288
            java.lang.String r2 = "schedule"
            int r2 = r11.getInt(r2)     // Catch:{ all -> 0x0274 }
            if (r2 != r10) goto L_0x0288
            r2 = 1
            goto L_0x0289
        L_0x0288:
            r2 = 0
        L_0x0289:
            int r20 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r20 != 0) goto L_0x02a6
            java.lang.String r10 = "ENCRYPTED_MESSAGE"
            r14 = r29
            boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x029a }
            if (r10 == 0) goto L_0x02a8
            long r12 = org.telegram.messenger.NotificationsController.globalSecretChatId     // Catch:{ all -> 0x029a }
            goto L_0x02a8
        L_0x029a:
            r0 = move-exception
            goto L_0x029f
        L_0x029c:
            r0 = move-exception
            r14 = r29
        L_0x029f:
            r3 = r1
            r7 = r16
            r4 = r28
            goto L_0x01c2
        L_0x02a6:
            r14 = r29
        L_0x02a8:
            r20 = 0
            int r10 = (r12 > r20 ? 1 : (r12 == r20 ? 0 : -1))
            if (r10 == 0) goto L_0x1cc5
            java.lang.String r10 = "READ_HISTORY"
            boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x1cc2 }
            java.lang.String r15 = " for dialogId = "
            if (r10 == 0) goto L_0x032a
            java.lang.String r2 = "max_id"
            int r2 = r11.getInt(r2)     // Catch:{ all -> 0x029a }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x029a }
            r5.<init>()     // Catch:{ all -> 0x029a }
            boolean r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x029a }
            if (r10 == 0) goto L_0x02e1
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x029a }
            r10.<init>()     // Catch:{ all -> 0x029a }
            java.lang.String r11 = "GCM received read notification max_id = "
            r10.append(r11)     // Catch:{ all -> 0x029a }
            r10.append(r2)     // Catch:{ all -> 0x029a }
            r10.append(r15)     // Catch:{ all -> 0x029a }
            r10.append(r12)     // Catch:{ all -> 0x029a }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x029a }
            org.telegram.messenger.FileLog.d(r10)     // Catch:{ all -> 0x029a }
        L_0x02e1:
            r10 = 0
            int r12 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r12 == 0) goto L_0x02f4
            org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox r3 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox     // Catch:{ all -> 0x029a }
            r3.<init>()     // Catch:{ all -> 0x029a }
            r3.channel_id = r6     // Catch:{ all -> 0x029a }
            r3.max_id = r2     // Catch:{ all -> 0x029a }
            r5.add(r3)     // Catch:{ all -> 0x029a }
            goto L_0x0317
        L_0x02f4:
            org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox r6 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox     // Catch:{ all -> 0x029a }
            r6.<init>()     // Catch:{ all -> 0x029a }
            r10 = 0
            int r7 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r7 == 0) goto L_0x0309
            org.telegram.tgnet.TLRPC$TL_peerUser r7 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x029a }
            r7.<init>()     // Catch:{ all -> 0x029a }
            r6.peer = r7     // Catch:{ all -> 0x029a }
            r7.user_id = r3     // Catch:{ all -> 0x029a }
            goto L_0x0312
        L_0x0309:
            org.telegram.tgnet.TLRPC$TL_peerChat r3 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x029a }
            r3.<init>()     // Catch:{ all -> 0x029a }
            r6.peer = r3     // Catch:{ all -> 0x029a }
            r3.chat_id = r8     // Catch:{ all -> 0x029a }
        L_0x0312:
            r6.max_id = r2     // Catch:{ all -> 0x029a }
            r5.add(r6)     // Catch:{ all -> 0x029a }
        L_0x0317:
            org.telegram.messenger.MessagesController r22 = org.telegram.messenger.MessagesController.getInstance(r28)     // Catch:{ all -> 0x029a }
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r23 = r5
            r22.processUpdateArray(r23, r24, r25, r26, r27)     // Catch:{ all -> 0x029a }
            goto L_0x1cc5
        L_0x032a:
            java.lang.String r10 = "MESSAGE_DELETED"
            boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x1cc2 }
            r22 = r2
            java.lang.String r2 = "messages"
            if (r10 == 0) goto L_0x03a0
            java.lang.String r2 = r11.getString(r2)     // Catch:{ all -> 0x029a }
            java.lang.String r3 = ","
            java.lang.String[] r2 = r2.split(r3)     // Catch:{ all -> 0x029a }
            androidx.collection.LongSparseArray r3 = new androidx.collection.LongSparseArray     // Catch:{ all -> 0x029a }
            r3.<init>()     // Catch:{ all -> 0x029a }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x029a }
            r4.<init>()     // Catch:{ all -> 0x029a }
            r8 = 0
        L_0x034b:
            int r5 = r2.length     // Catch:{ all -> 0x029a }
            if (r8 >= r5) goto L_0x035a
            r5 = r2[r8]     // Catch:{ all -> 0x029a }
            java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r5)     // Catch:{ all -> 0x029a }
            r4.add(r5)     // Catch:{ all -> 0x029a }
            int r8 = r8 + 1
            goto L_0x034b
        L_0x035a:
            long r8 = -r6
            r3.put(r8, r4)     // Catch:{ all -> 0x029a }
            org.telegram.messenger.NotificationsController r2 = org.telegram.messenger.NotificationsController.getInstance(r28)     // Catch:{ all -> 0x029a }
            r2.removeDeletedMessagesFromNotifications(r3)     // Catch:{ all -> 0x029a }
            org.telegram.messenger.MessagesController r20 = org.telegram.messenger.MessagesController.getInstance(r28)     // Catch:{ all -> 0x029a }
            r21 = r12
            r23 = r4
            r24 = r6
            r20.deleteMessagesByPush(r21, r23, r24)     // Catch:{ all -> 0x029a }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x029a }
            if (r2 == 0) goto L_0x1cc5
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x029a }
            r2.<init>()     // Catch:{ all -> 0x029a }
            java.lang.String r3 = "GCM received "
            r2.append(r3)     // Catch:{ all -> 0x029a }
            r2.append(r14)     // Catch:{ all -> 0x029a }
            r2.append(r15)     // Catch:{ all -> 0x029a }
            r2.append(r12)     // Catch:{ all -> 0x029a }
            java.lang.String r3 = " mids = "
            r2.append(r3)     // Catch:{ all -> 0x029a }
            java.lang.String r3 = ","
            java.lang.String r3 = android.text.TextUtils.join(r3, r4)     // Catch:{ all -> 0x029a }
            r2.append(r3)     // Catch:{ all -> 0x029a }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x029a }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x029a }
            goto L_0x1cc5
        L_0x03a0:
            boolean r10 = android.text.TextUtils.isEmpty(r14)     // Catch:{ all -> 0x1cc2 }
            if (r10 != 0) goto L_0x1cc5
            java.lang.String r10 = "msg_id"
            boolean r10 = r11.has(r10)     // Catch:{ all -> 0x1cc2 }
            if (r10 == 0) goto L_0x03b5
            java.lang.String r10 = "msg_id"
            int r10 = r11.getInt(r10)     // Catch:{ all -> 0x029a }
            goto L_0x03b6
        L_0x03b5:
            r10 = 0
        L_0x03b6:
            java.lang.String r1 = "random_id"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x1cbe }
            if (r1 == 0) goto L_0x03de
            java.lang.String r1 = "random_id"
            java.lang.String r1 = r11.getString(r1)     // Catch:{ all -> 0x03d3 }
            java.lang.Long r1 = org.telegram.messenger.Utilities.parseLong(r1)     // Catch:{ all -> 0x03d3 }
            long r23 = r1.longValue()     // Catch:{ all -> 0x03d3 }
            r47 = r3
            r3 = r23
            r23 = r47
            goto L_0x03e2
        L_0x03d3:
            r0 = move-exception
            r2 = -1
            r3 = r49
            r1 = r0
            r7 = r16
            r4 = r28
            goto L_0x1dd4
        L_0x03de:
            r23 = r3
            r3 = 0
        L_0x03e2:
            if (r10 == 0) goto L_0x0419
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r28)     // Catch:{ all -> 0x03d3 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r1.dialogs_read_inbox_max     // Catch:{ all -> 0x03d3 }
            r25 = r8
            java.lang.Long r8 = java.lang.Long.valueOf(r12)     // Catch:{ all -> 0x03d3 }
            java.lang.Object r1 = r1.get(r8)     // Catch:{ all -> 0x03d3 }
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch:{ all -> 0x03d3 }
            if (r1 != 0) goto L_0x0412
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r28)     // Catch:{ all -> 0x03d3 }
            r8 = 0
            int r1 = r1.getDialogReadMax(r8, r12)     // Catch:{ all -> 0x03d3 }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x03d3 }
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r28)     // Catch:{ all -> 0x03d3 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r8 = r8.dialogs_read_inbox_max     // Catch:{ all -> 0x03d3 }
            java.lang.Long r9 = java.lang.Long.valueOf(r12)     // Catch:{ all -> 0x03d3 }
            r8.put(r9, r1)     // Catch:{ all -> 0x03d3 }
        L_0x0412:
            int r1 = r1.intValue()     // Catch:{ all -> 0x03d3 }
            if (r10 <= r1) goto L_0x042d
            goto L_0x042b
        L_0x0419:
            r25 = r8
            r8 = 0
            int r1 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r1 == 0) goto L_0x042d
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r28)     // Catch:{ all -> 0x03d3 }
            boolean r1 = r1.checkMessageByRandomId(r3)     // Catch:{ all -> 0x03d3 }
            if (r1 != 0) goto L_0x042d
        L_0x042b:
            r1 = 1
            goto L_0x042e
        L_0x042d:
            r1 = 0
        L_0x042e:
            if (r1 == 0) goto L_0x1cbb
            java.lang.String r1 = "chat_from_id"
            r30 = r3
            r8 = 0
            long r3 = r11.optLong(r1, r8)     // Catch:{ all -> 0x1cbe }
            java.lang.String r1 = "chat_from_broadcast_id"
            r27 = r2
            long r1 = r11.optLong(r1, r8)     // Catch:{ all -> 0x1cbe }
            r32 = r1
            java.lang.String r1 = "chat_from_group_id"
            long r1 = r11.optLong(r1, r8)     // Catch:{ all -> 0x1cbe }
            int r20 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r20 != 0) goto L_0x0455
            int r29 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r29 == 0) goto L_0x0453
            goto L_0x0455
        L_0x0453:
            r8 = 0
            goto L_0x0456
        L_0x0455:
            r8 = 1
        L_0x0456:
            java.lang.String r9 = "mention"
            boolean r9 = r11.has(r9)     // Catch:{ all -> 0x1cbe }
            if (r9 == 0) goto L_0x0469
            java.lang.String r9 = "mention"
            int r9 = r11.getInt(r9)     // Catch:{ all -> 0x03d3 }
            if (r9 == 0) goto L_0x0469
            r29 = 1
            goto L_0x046b
        L_0x0469:
            r29 = 0
        L_0x046b:
            java.lang.String r9 = "silent"
            boolean r9 = r11.has(r9)     // Catch:{ all -> 0x1cbe }
            if (r9 == 0) goto L_0x047e
            java.lang.String r9 = "silent"
            int r9 = r11.getInt(r9)     // Catch:{ all -> 0x03d3 }
            if (r9 == 0) goto L_0x047e
            r34 = 1
            goto L_0x0480
        L_0x047e:
            r34 = 0
        L_0x0480:
            java.lang.String r9 = "loc_args"
            boolean r9 = r5.has(r9)     // Catch:{ all -> 0x1cbe }
            if (r9 == 0) goto L_0x04a2
            java.lang.String r9 = "loc_args"
            org.json.JSONArray r5 = r5.getJSONArray(r9)     // Catch:{ all -> 0x03d3 }
            int r9 = r5.length()     // Catch:{ all -> 0x03d3 }
            r35 = r3
            java.lang.String[] r3 = new java.lang.String[r9]     // Catch:{ all -> 0x03d3 }
            r4 = 0
        L_0x0497:
            if (r4 >= r9) goto L_0x04a5
            java.lang.String r37 = r5.getString(r4)     // Catch:{ all -> 0x03d3 }
            r3[r4] = r37     // Catch:{ all -> 0x03d3 }
            int r4 = r4 + 1
            goto L_0x0497
        L_0x04a2:
            r35 = r3
            r3 = 0
        L_0x04a5:
            r4 = 0
            r5 = r3[r4]     // Catch:{ all -> 0x1cbe }
            java.lang.String r4 = "edit_date"
            boolean r4 = r11.has(r4)     // Catch:{ all -> 0x1cbe }
            java.lang.String r9 = "CHAT_"
            boolean r9 = r14.startsWith(r9)     // Catch:{ all -> 0x1cbe }
            if (r9 == 0) goto L_0x04eb
            boolean r9 = org.telegram.messenger.UserObject.isReplyUser((long) r12)     // Catch:{ all -> 0x03d3 }
            if (r9 == 0) goto L_0x04d4
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x03d3 }
            r9.<init>()     // Catch:{ all -> 0x03d3 }
            r9.append(r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r5 = " @ "
            r9.append(r5)     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r11 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r9.append(r11)     // Catch:{ all -> 0x03d3 }
            java.lang.String r5 = r9.toString()     // Catch:{ all -> 0x03d3 }
            goto L_0x050e
        L_0x04d4:
            r20 = 0
            int r9 = (r6 > r20 ? 1 : (r6 == r20 ? 0 : -1))
            if (r9 == 0) goto L_0x04dc
            r9 = 1
            goto L_0x04dd
        L_0x04dc:
            r9 = 0
        L_0x04dd:
            r11 = 1
            r37 = r3[r11]     // Catch:{ all -> 0x03d3 }
            r11 = 0
            r38 = 0
            r47 = r9
            r9 = r5
            r5 = r37
            r37 = r47
            goto L_0x0514
        L_0x04eb:
            java.lang.String r9 = "PINNED_"
            boolean r9 = r14.startsWith(r9)     // Catch:{ all -> 0x1cbe }
            if (r9 == 0) goto L_0x0503
            r20 = 0
            int r9 = (r6 > r20 ? 1 : (r6 == r20 ? 0 : -1))
            if (r9 == 0) goto L_0x04fb
            r9 = 1
            goto L_0x04fc
        L_0x04fb:
            r9 = 0
        L_0x04fc:
            r37 = r9
            r9 = 0
            r11 = 0
            r38 = 1
            goto L_0x0514
        L_0x0503:
            java.lang.String r9 = "CHANNEL_"
            boolean r9 = r14.startsWith(r9)     // Catch:{ all -> 0x1cbe }
            if (r9 == 0) goto L_0x050e
            r9 = 0
            r11 = 1
            goto L_0x0510
        L_0x050e:
            r9 = 0
            r11 = 0
        L_0x0510:
            r37 = 0
            r38 = 0
        L_0x0514:
            boolean r39 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1cbe }
            if (r39 == 0) goto L_0x053f
            r39 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x03d3 }
            r5.<init>()     // Catch:{ all -> 0x03d3 }
            r40 = r4
            java.lang.String r4 = "GCM received message notification "
            r5.append(r4)     // Catch:{ all -> 0x03d3 }
            r5.append(r14)     // Catch:{ all -> 0x03d3 }
            r5.append(r15)     // Catch:{ all -> 0x03d3 }
            r5.append(r12)     // Catch:{ all -> 0x03d3 }
            java.lang.String r4 = " mid = "
            r5.append(r4)     // Catch:{ all -> 0x03d3 }
            r5.append(r10)     // Catch:{ all -> 0x03d3 }
            java.lang.String r4 = r5.toString()     // Catch:{ all -> 0x03d3 }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ all -> 0x03d3 }
            goto L_0x0543
        L_0x053f:
            r40 = r4
            r39 = r5
        L_0x0543:
            int r4 = r14.hashCode()     // Catch:{ all -> 0x1cbe }
            switch(r4) {
                case -2100047043: goto L_0x0a7c;
                case -2091498420: goto L_0x0a71;
                case -2053872415: goto L_0x0a66;
                case -2039746363: goto L_0x0a5b;
                case -2023218804: goto L_0x0a50;
                case -1979538588: goto L_0x0a45;
                case -1979536003: goto L_0x0a3a;
                case -1979535888: goto L_0x0a2f;
                case -1969004705: goto L_0x0a24;
                case -1946699248: goto L_0x0a18;
                case -1717283471: goto L_0x0a0c;
                case -1646640058: goto L_0x0a00;
                case -1528047021: goto L_0x09f4;
                case -1493579426: goto L_0x09e8;
                case -1482481933: goto L_0x09dc;
                case -1480102982: goto L_0x09d1;
                case -1478041834: goto L_0x09c5;
                case -1474543101: goto L_0x09ba;
                case -1465695932: goto L_0x09ae;
                case -1374906292: goto L_0x09a2;
                case -1372940586: goto L_0x0996;
                case -1264245338: goto L_0x098a;
                case -1236154001: goto L_0x097e;
                case -1236086700: goto L_0x0972;
                case -1236077786: goto L_0x0966;
                case -1235796237: goto L_0x095a;
                case -1235760759: goto L_0x094e;
                case -1235686303: goto L_0x0943;
                case -1198046100: goto L_0x0938;
                case -1124254527: goto L_0x092c;
                case -1085137927: goto L_0x0920;
                case -1084856378: goto L_0x0914;
                case -1084820900: goto L_0x0908;
                case -1084746444: goto L_0x08fc;
                case -819729482: goto L_0x08f0;
                case -772141857: goto L_0x08e4;
                case -638310039: goto L_0x08d8;
                case -590403924: goto L_0x08cc;
                case -589196239: goto L_0x08c0;
                case -589193654: goto L_0x08b4;
                case -589193539: goto L_0x08a8;
                case -440169325: goto L_0x089c;
                case -412748110: goto L_0x0890;
                case -228518075: goto L_0x0884;
                case -213586509: goto L_0x0878;
                case -115582002: goto L_0x086c;
                case -112621464: goto L_0x0860;
                case -108522133: goto L_0x0854;
                case -107572034: goto L_0x0849;
                case -40534265: goto L_0x083d;
                case 65254746: goto L_0x0831;
                case 141040782: goto L_0x0825;
                case 202550149: goto L_0x0819;
                case 309993049: goto L_0x080d;
                case 309995634: goto L_0x0801;
                case 309995749: goto L_0x07f5;
                case 320532812: goto L_0x07e9;
                case 328933854: goto L_0x07dd;
                case 331340546: goto L_0x07d1;
                case 342406591: goto L_0x07c5;
                case 344816990: goto L_0x07b9;
                case 346878138: goto L_0x07ad;
                case 350376871: goto L_0x07a1;
                case 608430149: goto L_0x0795;
                case 615714517: goto L_0x078a;
                case 715508879: goto L_0x077e;
                case 728985323: goto L_0x0772;
                case 731046471: goto L_0x0766;
                case 734545204: goto L_0x075a;
                case 802032552: goto L_0x074e;
                case 991498806: goto L_0x0742;
                case 1007364121: goto L_0x0736;
                case 1019850010: goto L_0x072a;
                case 1019917311: goto L_0x071e;
                case 1019926225: goto L_0x0712;
                case 1020207774: goto L_0x0706;
                case 1020243252: goto L_0x06fa;
                case 1020317708: goto L_0x06ee;
                case 1060282259: goto L_0x06e2;
                case 1060349560: goto L_0x06d6;
                case 1060358474: goto L_0x06ca;
                case 1060640023: goto L_0x06be;
                case 1060675501: goto L_0x06b2;
                case 1060749957: goto L_0x06a7;
                case 1073049781: goto L_0x069b;
                case 1078101399: goto L_0x068f;
                case 1110103437: goto L_0x0683;
                case 1160762272: goto L_0x0677;
                case 1172918249: goto L_0x066b;
                case 1234591620: goto L_0x065f;
                case 1281128640: goto L_0x0653;
                case 1281131225: goto L_0x0647;
                case 1281131340: goto L_0x063b;
                case 1310789062: goto L_0x0630;
                case 1333118583: goto L_0x0624;
                case 1361447897: goto L_0x0618;
                case 1498266155: goto L_0x060c;
                case 1533804208: goto L_0x0600;
                case 1540131626: goto L_0x05f4;
                case 1547988151: goto L_0x05e8;
                case 1561464595: goto L_0x05dc;
                case 1563525743: goto L_0x05d0;
                case 1567024476: goto L_0x05c4;
                case 1810705077: goto L_0x05b8;
                case 1815177512: goto L_0x05ac;
                case 1954774321: goto L_0x05a0;
                case 1963241394: goto L_0x0594;
                case 2014789757: goto L_0x0588;
                case 2022049433: goto L_0x057c;
                case 2034984710: goto L_0x0570;
                case 2048733346: goto L_0x0564;
                case 2099392181: goto L_0x0558;
                case 2140162142: goto L_0x054c;
                default: goto L_0x054a;
            }
        L_0x054a:
            goto L_0x0a87
        L_0x054c:
            java.lang.String r4 = "CHAT_MESSAGE_GEOLIVE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 60
            goto L_0x0a88
        L_0x0558:
            java.lang.String r4 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 43
            goto L_0x0a88
        L_0x0564:
            java.lang.String r4 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 28
            goto L_0x0a88
        L_0x0570:
            java.lang.String r4 = "CHANNEL_MESSAGE_PLAYLIST"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 45
            goto L_0x0a88
        L_0x057c:
            java.lang.String r4 = "PINNED_CONTACT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 94
            goto L_0x0a88
        L_0x0588:
            java.lang.String r4 = "CHAT_PHOTO_EDITED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 68
            goto L_0x0a88
        L_0x0594:
            java.lang.String r4 = "LOCKED_MESSAGE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 107(0x6b, float:1.5E-43)
            goto L_0x0a88
        L_0x05a0:
            java.lang.String r4 = "CHAT_MESSAGE_PLAYLIST"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 83
            goto L_0x0a88
        L_0x05ac:
            java.lang.String r4 = "CHANNEL_MESSAGES"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 47
            goto L_0x0a88
        L_0x05b8:
            java.lang.String r4 = "MESSAGE_INVOICE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 21
            goto L_0x0a88
        L_0x05c4:
            java.lang.String r4 = "CHAT_MESSAGE_VIDEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 51
            goto L_0x0a88
        L_0x05d0:
            java.lang.String r4 = "CHAT_MESSAGE_ROUND"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 52
            goto L_0x0a88
        L_0x05dc:
            java.lang.String r4 = "CHAT_MESSAGE_PHOTO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 50
            goto L_0x0a88
        L_0x05e8:
            java.lang.String r4 = "CHAT_MESSAGE_AUDIO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 55
            goto L_0x0a88
        L_0x05f4:
            java.lang.String r4 = "MESSAGE_PLAYLIST"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 25
            goto L_0x0a88
        L_0x0600:
            java.lang.String r4 = "MESSAGE_VIDEOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 24
            goto L_0x0a88
        L_0x060c:
            java.lang.String r4 = "PHONE_CALL_MISSED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 112(0x70, float:1.57E-43)
            goto L_0x0a88
        L_0x0618:
            java.lang.String r4 = "MESSAGE_PHOTOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 23
            goto L_0x0a88
        L_0x0624:
            java.lang.String r4 = "CHAT_MESSAGE_VIDEOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 82
            goto L_0x0a88
        L_0x0630:
            java.lang.String r4 = "MESSAGE_NOTEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 2
            goto L_0x0a88
        L_0x063b:
            java.lang.String r4 = "MESSAGE_GIF"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 17
            goto L_0x0a88
        L_0x0647:
            java.lang.String r4 = "MESSAGE_GEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 15
            goto L_0x0a88
        L_0x0653:
            java.lang.String r4 = "MESSAGE_DOC"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 9
            goto L_0x0a88
        L_0x065f:
            java.lang.String r4 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 63
            goto L_0x0a88
        L_0x066b:
            java.lang.String r4 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 39
            goto L_0x0a88
        L_0x0677:
            java.lang.String r4 = "CHAT_MESSAGE_PHOTOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 81
            goto L_0x0a88
        L_0x0683:
            java.lang.String r4 = "CHAT_MESSAGE_NOTEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 49
            goto L_0x0a88
        L_0x068f:
            java.lang.String r4 = "CHAT_TITLE_EDITED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 67
            goto L_0x0a88
        L_0x069b:
            java.lang.String r4 = "PINNED_NOTEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 87
            goto L_0x0a88
        L_0x06a7:
            java.lang.String r4 = "MESSAGE_TEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 0
            goto L_0x0a88
        L_0x06b2:
            java.lang.String r4 = "MESSAGE_QUIZ"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 13
            goto L_0x0a88
        L_0x06be:
            java.lang.String r4 = "MESSAGE_POLL"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 14
            goto L_0x0a88
        L_0x06ca:
            java.lang.String r4 = "MESSAGE_GAME"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 18
            goto L_0x0a88
        L_0x06d6:
            java.lang.String r4 = "MESSAGE_FWDS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 22
            goto L_0x0a88
        L_0x06e2:
            java.lang.String r4 = "MESSAGE_DOCS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 26
            goto L_0x0a88
        L_0x06ee:
            java.lang.String r4 = "CHAT_MESSAGE_TEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 48
            goto L_0x0a88
        L_0x06fa:
            java.lang.String r4 = "CHAT_MESSAGE_QUIZ"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 57
            goto L_0x0a88
        L_0x0706:
            java.lang.String r4 = "CHAT_MESSAGE_POLL"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 58
            goto L_0x0a88
        L_0x0712:
            java.lang.String r4 = "CHAT_MESSAGE_GAME"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 62
            goto L_0x0a88
        L_0x071e:
            java.lang.String r4 = "CHAT_MESSAGE_FWDS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 80
            goto L_0x0a88
        L_0x072a:
            java.lang.String r4 = "CHAT_MESSAGE_DOCS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 84
            goto L_0x0a88
        L_0x0736:
            java.lang.String r4 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 20
            goto L_0x0a88
        L_0x0742:
            java.lang.String r4 = "PINNED_GEOLIVE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 98
            goto L_0x0a88
        L_0x074e:
            java.lang.String r4 = "MESSAGE_CONTACT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 12
            goto L_0x0a88
        L_0x075a:
            java.lang.String r4 = "PINNED_VIDEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 89
            goto L_0x0a88
        L_0x0766:
            java.lang.String r4 = "PINNED_ROUND"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 90
            goto L_0x0a88
        L_0x0772:
            java.lang.String r4 = "PINNED_PHOTO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 88
            goto L_0x0a88
        L_0x077e:
            java.lang.String r4 = "PINNED_AUDIO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 93
            goto L_0x0a88
        L_0x078a:
            java.lang.String r4 = "MESSAGE_PHOTO_SECRET"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 4
            goto L_0x0a88
        L_0x0795:
            java.lang.String r4 = "CHAT_VOICECHAT_INVITE_YOU"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 73
            goto L_0x0a88
        L_0x07a1:
            java.lang.String r4 = "CHANNEL_MESSAGE_VIDEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 30
            goto L_0x0a88
        L_0x07ad:
            java.lang.String r4 = "CHANNEL_MESSAGE_ROUND"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 31
            goto L_0x0a88
        L_0x07b9:
            java.lang.String r4 = "CHANNEL_MESSAGE_PHOTO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 29
            goto L_0x0a88
        L_0x07c5:
            java.lang.String r4 = "CHAT_VOICECHAT_END"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 72
            goto L_0x0a88
        L_0x07d1:
            java.lang.String r4 = "CHANNEL_MESSAGE_AUDIO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 34
            goto L_0x0a88
        L_0x07dd:
            java.lang.String r4 = "CHAT_MESSAGE_STICKER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 54
            goto L_0x0a88
        L_0x07e9:
            java.lang.String r4 = "MESSAGES"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 27
            goto L_0x0a88
        L_0x07f5:
            java.lang.String r4 = "CHAT_MESSAGE_GIF"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 61
            goto L_0x0a88
        L_0x0801:
            java.lang.String r4 = "CHAT_MESSAGE_GEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 59
            goto L_0x0a88
        L_0x080d:
            java.lang.String r4 = "CHAT_MESSAGE_DOC"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 53
            goto L_0x0a88
        L_0x0819:
            java.lang.String r4 = "CHAT_VOICECHAT_INVITE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 71
            goto L_0x0a88
        L_0x0825:
            java.lang.String r4 = "CHAT_LEFT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 76
            goto L_0x0a88
        L_0x0831:
            java.lang.String r4 = "CHAT_ADD_YOU"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 66
            goto L_0x0a88
        L_0x083d:
            java.lang.String r4 = "CHAT_DELETE_MEMBER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 74
            goto L_0x0a88
        L_0x0849:
            java.lang.String r4 = "MESSAGE_SCREENSHOT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 7
            goto L_0x0a88
        L_0x0854:
            java.lang.String r4 = "AUTH_REGION"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 106(0x6a, float:1.49E-43)
            goto L_0x0a88
        L_0x0860:
            java.lang.String r4 = "CONTACT_JOINED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 104(0x68, float:1.46E-43)
            goto L_0x0a88
        L_0x086c:
            java.lang.String r4 = "CHAT_MESSAGE_INVOICE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 64
            goto L_0x0a88
        L_0x0878:
            java.lang.String r4 = "ENCRYPTION_REQUEST"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 108(0x6c, float:1.51E-43)
            goto L_0x0a88
        L_0x0884:
            java.lang.String r4 = "MESSAGE_GEOLIVE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 16
            goto L_0x0a88
        L_0x0890:
            java.lang.String r4 = "CHAT_DELETE_YOU"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 75
            goto L_0x0a88
        L_0x089c:
            java.lang.String r4 = "AUTH_UNKNOWN"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 105(0x69, float:1.47E-43)
            goto L_0x0a88
        L_0x08a8:
            java.lang.String r4 = "PINNED_GIF"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 102(0x66, float:1.43E-43)
            goto L_0x0a88
        L_0x08b4:
            java.lang.String r4 = "PINNED_GEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 97
            goto L_0x0a88
        L_0x08c0:
            java.lang.String r4 = "PINNED_DOC"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 91
            goto L_0x0a88
        L_0x08cc:
            java.lang.String r4 = "PINNED_GAME_SCORE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 100
            goto L_0x0a88
        L_0x08d8:
            java.lang.String r4 = "CHANNEL_MESSAGE_STICKER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 33
            goto L_0x0a88
        L_0x08e4:
            java.lang.String r4 = "PHONE_CALL_REQUEST"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 110(0x6e, float:1.54E-43)
            goto L_0x0a88
        L_0x08f0:
            java.lang.String r4 = "PINNED_STICKER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 92
            goto L_0x0a88
        L_0x08fc:
            java.lang.String r4 = "PINNED_TEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 86
            goto L_0x0a88
        L_0x0908:
            java.lang.String r4 = "PINNED_QUIZ"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 95
            goto L_0x0a88
        L_0x0914:
            java.lang.String r4 = "PINNED_POLL"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 96
            goto L_0x0a88
        L_0x0920:
            java.lang.String r4 = "PINNED_GAME"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 99
            goto L_0x0a88
        L_0x092c:
            java.lang.String r4 = "CHAT_MESSAGE_CONTACT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 56
            goto L_0x0a88
        L_0x0938:
            java.lang.String r4 = "MESSAGE_VIDEO_SECRET"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 6
            goto L_0x0a88
        L_0x0943:
            java.lang.String r4 = "CHANNEL_MESSAGE_TEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 1
            goto L_0x0a88
        L_0x094e:
            java.lang.String r4 = "CHANNEL_MESSAGE_QUIZ"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 36
            goto L_0x0a88
        L_0x095a:
            java.lang.String r4 = "CHANNEL_MESSAGE_POLL"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 37
            goto L_0x0a88
        L_0x0966:
            java.lang.String r4 = "CHANNEL_MESSAGE_GAME"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 41
            goto L_0x0a88
        L_0x0972:
            java.lang.String r4 = "CHANNEL_MESSAGE_FWDS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 42
            goto L_0x0a88
        L_0x097e:
            java.lang.String r4 = "CHANNEL_MESSAGE_DOCS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 46
            goto L_0x0a88
        L_0x098a:
            java.lang.String r4 = "PINNED_INVOICE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 101(0x65, float:1.42E-43)
            goto L_0x0a88
        L_0x0996:
            java.lang.String r4 = "CHAT_RETURNED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 77
            goto L_0x0a88
        L_0x09a2:
            java.lang.String r4 = "ENCRYPTED_MESSAGE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 103(0x67, float:1.44E-43)
            goto L_0x0a88
        L_0x09ae:
            java.lang.String r4 = "ENCRYPTION_ACCEPT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 109(0x6d, float:1.53E-43)
            goto L_0x0a88
        L_0x09ba:
            java.lang.String r4 = "MESSAGE_VIDEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 5
            goto L_0x0a88
        L_0x09c5:
            java.lang.String r4 = "MESSAGE_ROUND"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 8
            goto L_0x0a88
        L_0x09d1:
            java.lang.String r4 = "MESSAGE_PHOTO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 3
            goto L_0x0a88
        L_0x09dc:
            java.lang.String r4 = "MESSAGE_MUTED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 111(0x6f, float:1.56E-43)
            goto L_0x0a88
        L_0x09e8:
            java.lang.String r4 = "MESSAGE_AUDIO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 11
            goto L_0x0a88
        L_0x09f4:
            java.lang.String r4 = "CHAT_MESSAGES"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 85
            goto L_0x0a88
        L_0x0a00:
            java.lang.String r4 = "CHAT_VOICECHAT_START"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 70
            goto L_0x0a88
        L_0x0a0c:
            java.lang.String r4 = "CHAT_REQ_JOINED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 79
            goto L_0x0a88
        L_0x0a18:
            java.lang.String r4 = "CHAT_JOINED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 78
            goto L_0x0a88
        L_0x0a24:
            java.lang.String r4 = "CHAT_ADD_MEMBER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 69
            goto L_0x0a88
        L_0x0a2f:
            java.lang.String r4 = "CHANNEL_MESSAGE_GIF"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 40
            goto L_0x0a88
        L_0x0a3a:
            java.lang.String r4 = "CHANNEL_MESSAGE_GEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 38
            goto L_0x0a88
        L_0x0a45:
            java.lang.String r4 = "CHANNEL_MESSAGE_DOC"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 32
            goto L_0x0a88
        L_0x0a50:
            java.lang.String r4 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 44
            goto L_0x0a88
        L_0x0a5b:
            java.lang.String r4 = "MESSAGE_STICKER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 10
            goto L_0x0a88
        L_0x0a66:
            java.lang.String r4 = "CHAT_CREATED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 65
            goto L_0x0a88
        L_0x0a71:
            java.lang.String r4 = "CHANNEL_MESSAGE_CONTACT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 35
            goto L_0x0a88
        L_0x0a7c:
            java.lang.String r4 = "MESSAGE_GAME_SCORE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d3 }
            if (r4 == 0) goto L_0x0a87
            r4 = 19
            goto L_0x0a88
        L_0x0a87:
            r4 = -1
        L_0x0a88:
            java.lang.String r5 = "MusicFiles"
            java.lang.String r15 = "Videos"
            r41 = r11
            java.lang.String r11 = "Photos"
            r42 = r9
            java.lang.String r9 = " "
            r43 = r1
            java.lang.String r2 = "NotificationGroupFew"
            java.lang.String r1 = "NotificationMessageFew"
            r45 = r6
            java.lang.String r7 = "ChannelMessageFew"
            java.lang.String r6 = "AttachSticker"
            switch(r4) {
                case 0: goto L_0x1ba6;
                case 1: goto L_0x1ba6;
                case 2: goto L_0x1b84;
                case 3: goto L_0x1b67;
                case 4: goto L_0x1b4a;
                case 5: goto L_0x1b2d;
                case 6: goto L_0x1b0f;
                case 7: goto L_0x1af7;
                case 8: goto L_0x1ad9;
                case 9: goto L_0x1abb;
                case 10: goto L_0x1a60;
                case 11: goto L_0x1a42;
                case 12: goto L_0x1a1f;
                case 13: goto L_0x19fc;
                case 14: goto L_0x19d9;
                case 15: goto L_0x19bb;
                case 16: goto L_0x199d;
                case 17: goto L_0x197f;
                case 18: goto L_0x195c;
                case 19: goto L_0x193d;
                case 20: goto L_0x193d;
                case 21: goto L_0x191a;
                case 22: goto L_0x18f2;
                case 23: goto L_0x18ce;
                case 24: goto L_0x18ab;
                case 25: goto L_0x1888;
                case 26: goto L_0x1863;
                case 27: goto L_0x184b;
                case 28: goto L_0x182d;
                case 29: goto L_0x180f;
                case 30: goto L_0x17f1;
                case 31: goto L_0x17d3;
                case 32: goto L_0x17b5;
                case 33: goto L_0x175a;
                case 34: goto L_0x173c;
                case 35: goto L_0x1719;
                case 36: goto L_0x16f6;
                case 37: goto L_0x16d3;
                case 38: goto L_0x16b5;
                case 39: goto L_0x1697;
                case 40: goto L_0x1679;
                case 41: goto L_0x165b;
                case 42: goto L_0x1631;
                case 43: goto L_0x160d;
                case 44: goto L_0x15e9;
                case 45: goto L_0x15c5;
                case 46: goto L_0x159f;
                case 47: goto L_0x158a;
                case 48: goto L_0x1569;
                case 49: goto L_0x1546;
                case 50: goto L_0x1523;
                case 51: goto L_0x1500;
                case 52: goto L_0x14dd;
                case 53: goto L_0x14ba;
                case 54: goto L_0x1441;
                case 55: goto L_0x141e;
                case 56: goto L_0x13f6;
                case 57: goto L_0x13ce;
                case 58: goto L_0x13a6;
                case 59: goto L_0x1383;
                case 60: goto L_0x1360;
                case 61: goto L_0x133d;
                case 62: goto L_0x1315;
                case 63: goto L_0x12f1;
                case 64: goto L_0x12c9;
                case 65: goto L_0x12af;
                case 66: goto L_0x12af;
                case 67: goto L_0x1295;
                case 68: goto L_0x127b;
                case 69: goto L_0x125c;
                case 70: goto L_0x1242;
                case 71: goto L_0x1223;
                case 72: goto L_0x1209;
                case 73: goto L_0x11ef;
                case 74: goto L_0x11d5;
                case 75: goto L_0x11bb;
                case 76: goto L_0x11a1;
                case 77: goto L_0x1187;
                case 78: goto L_0x116d;
                case 79: goto L_0x1153;
                case 80: goto L_0x1126;
                case 81: goto L_0x10fd;
                case 82: goto L_0x10d4;
                case 83: goto L_0x10ab;
                case 84: goto L_0x1080;
                case 85: goto L_0x1066;
                case 86: goto L_0x100f;
                case 87: goto L_0x0fc2;
                case 88: goto L_0x0var_;
                case 89: goto L_0x0var_;
                case 90: goto L_0x0edb;
                case 91: goto L_0x0e8e;
                case 92: goto L_0x0dd5;
                case 93: goto L_0x0d88;
                case 94: goto L_0x0d31;
                case 95: goto L_0x0cda;
                case 96: goto L_0x0CLASSNAME;
                case 97: goto L_0x0c3d;
                case 98: goto L_0x0bf4;
                case 99: goto L_0x0ba9;
                case 100: goto L_0x0b5e;
                case 101: goto L_0x0b13;
                case 102: goto L_0x0ac8;
                case 103: goto L_0x0aad;
                case 104: goto L_0x0aa9;
                case 105: goto L_0x0aa9;
                case 106: goto L_0x0aa9;
                case 107: goto L_0x0aa9;
                case 108: goto L_0x0aa9;
                case 109: goto L_0x0aa9;
                case 110: goto L_0x0aa9;
                case 111: goto L_0x0aa9;
                case 112: goto L_0x0aa9;
                default: goto L_0x0aa3;
            }
        L_0x0aa3:
            r4 = r22
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1cbe }
            goto L_0x1bc1
        L_0x0aa9:
            r4 = r22
            goto L_0x1bd7
        L_0x0aad:
            java.lang.String r1 = "YouHaveNewMessage"
            r2 = 2131628719(0x7f0e12af, float:1.8884739E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "SecretChatName"
            r3 = 2131627669(0x7f0e0e95, float:1.8882609E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            r4 = r22
            r17 = 0
            r22 = r2
            r2 = 1
            goto L_0x1bdd
        L_0x0ac8:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0ae6
            java.lang.String r1 = "NotificationActionPinnedGifUser"
            r2 = 2131626589(0x7f0e0a5d, float:1.8880418E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0ae6:
            if (r8 == 0) goto L_0x0b00
            java.lang.String r1 = "NotificationActionPinnedGif"
            r2 = 2131626587(0x7f0e0a5b, float:1.8880414E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0b00:
            java.lang.String r1 = "NotificationActionPinnedGifChannel"
            r2 = 2131626588(0x7f0e0a5c, float:1.8880416E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03d3 }
            r5[r4] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0b13:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0b31
            java.lang.String r1 = "NotificationActionPinnedInvoiceUser"
            r2 = 2131626592(0x7f0e0a60, float:1.8880425E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0b31:
            if (r8 == 0) goto L_0x0b4b
            java.lang.String r1 = "NotificationActionPinnedInvoice"
            r2 = 2131626590(0x7f0e0a5e, float:1.888042E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0b4b:
            java.lang.String r1 = "NotificationActionPinnedInvoiceChannel"
            r2 = 2131626591(0x7f0e0a5f, float:1.8880423E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03d3 }
            r5[r4] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0b5e:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0b7c
            java.lang.String r1 = "NotificationActionPinnedGameScoreUser"
            r2 = 2131626579(0x7f0e0a53, float:1.8880398E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0b7c:
            if (r8 == 0) goto L_0x0b96
            java.lang.String r1 = "NotificationActionPinnedGameScore"
            r2 = 2131626577(0x7f0e0a51, float:1.8880394E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0b96:
            java.lang.String r1 = "NotificationActionPinnedGameScoreChannel"
            r2 = 2131626578(0x7f0e0a52, float:1.8880396E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03d3 }
            r5[r4] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0ba9:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0bc7
            java.lang.String r1 = "NotificationActionPinnedGameUser"
            r2 = 2131626580(0x7f0e0a54, float:1.88804E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0bc7:
            if (r8 == 0) goto L_0x0be1
            java.lang.String r1 = "NotificationActionPinnedGame"
            r2 = 2131626575(0x7f0e0a4f, float:1.888039E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0be1:
            java.lang.String r1 = "NotificationActionPinnedGameChannel"
            r2 = 2131626576(0x7f0e0a50, float:1.8880392E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03d3 }
            r5[r4] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0bf4:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGeoLiveUser"
            r2 = 2131626585(0x7f0e0a59, float:1.888041E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            if (r8 == 0) goto L_0x0c2b
            java.lang.String r1 = "NotificationActionPinnedGeoLive"
            r2 = 2131626583(0x7f0e0a57, float:1.8880406E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0c2b:
            java.lang.String r1 = "NotificationActionPinnedGeoLiveChannel"
            r2 = 2131626584(0x7f0e0a58, float:1.8880408E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03d3 }
            r5[r4] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0c3d:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0c5a
            java.lang.String r1 = "NotificationActionPinnedGeoUser"
            r2 = 2131626586(0x7f0e0a5a, float:1.8880412E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0c5a:
            if (r8 == 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGeo"
            r2 = 2131626581(0x7f0e0a55, float:1.8880402E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.lang.String r1 = "NotificationActionPinnedGeoChannel"
            r2 = 2131626582(0x7f0e0a56, float:1.8880404E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03d3 }
            r5[r4] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
        L_0x0CLASSNAME:
            r4 = r22
            goto L_0x1b0b
        L_0x0CLASSNAME:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0ca5
            java.lang.String r1 = "NotificationActionPinnedPollUser"
            r2 = 2131626604(0x7f0e0a6c, float:1.8880449E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0ca5:
            if (r8 == 0) goto L_0x0cc3
            java.lang.String r1 = "NotificationActionPinnedPoll2"
            r2 = 2131626602(0x7f0e0a6a, float:1.8880445E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 2
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r7 = 1
            r4[r7] = r6     // Catch:{ all -> 0x03d3 }
            r3 = r3[r7]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0cc3:
            java.lang.String r1 = "NotificationActionPinnedPollChannel2"
            r2 = 2131626603(0x7f0e0a6b, float:1.8880447E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r4[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d3 }
            goto L_0x0CLASSNAME
        L_0x0cda:
            r4 = r22
            r5 = 0
            int r2 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r2 <= 0) goto L_0x0cfa
            java.lang.String r1 = "NotificationActionPinnedQuizUser"
            r2 = 2131626607(0x7f0e0a6f, float:1.8880455E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0cfa:
            if (r8 == 0) goto L_0x0d19
            java.lang.String r2 = "NotificationActionPinnedQuiz2"
            r5 = 2131626605(0x7f0e0a6d, float:1.888045E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 2
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r8 = 1
            r1[r8] = r7     // Catch:{ all -> 0x03d3 }
            r3 = r3[r8]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0d19:
            java.lang.String r1 = "NotificationActionPinnedQuizChannel2"
            r2 = 2131626606(0x7f0e0a6e, float:1.8880453E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0d31:
            r4 = r22
            r5 = 0
            int r2 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r2 <= 0) goto L_0x0d51
            java.lang.String r1 = "NotificationActionPinnedContactUser"
            r2 = 2131626571(0x7f0e0a4b, float:1.8880382E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0d51:
            if (r8 == 0) goto L_0x0d70
            java.lang.String r2 = "NotificationActionPinnedContact2"
            r5 = 2131626569(0x7f0e0a49, float:1.8880378E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 2
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r8 = 1
            r1[r8] = r7     // Catch:{ all -> 0x03d3 }
            r3 = r3[r8]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0d70:
            java.lang.String r1 = "NotificationActionPinnedContactChannel2"
            r2 = 2131626570(0x7f0e0a4a, float:1.888038E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0d88:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0da8
            java.lang.String r1 = "NotificationActionPinnedVoiceUser"
            r2 = 2131626625(0x7f0e0a81, float:1.8880491E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0da8:
            if (r8 == 0) goto L_0x0dc2
            java.lang.String r1 = "NotificationActionPinnedVoice"
            r2 = 2131626623(0x7f0e0a7f, float:1.8880487E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0dc2:
            java.lang.String r1 = "NotificationActionPinnedVoiceChannel"
            r2 = 2131626624(0x7f0e0a80, float:1.888049E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0dd5:
            r4 = r22
            r5 = 0
            int r2 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e14
            int r1 = r3.length     // Catch:{ all -> 0x03d3 }
            r2 = 1
            if (r1 <= r2) goto L_0x0e01
            r1 = r3[r2]     // Catch:{ all -> 0x03d3 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x03d3 }
            if (r1 != 0) goto L_0x0e01
            java.lang.String r1 = "NotificationActionPinnedStickerEmojiUser"
            r2 = 2131626615(0x7f0e0a77, float:1.8880471E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0e01:
            java.lang.String r1 = "NotificationActionPinnedStickerUser"
            r2 = 2131626616(0x7f0e0a78, float:1.8880473E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0e14:
            if (r8 == 0) goto L_0x0e57
            int r2 = r3.length     // Catch:{ all -> 0x03d3 }
            r5 = 2
            if (r2 <= r5) goto L_0x0e3f
            r2 = r3[r5]     // Catch:{ all -> 0x03d3 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x03d3 }
            if (r2 != 0) goto L_0x0e3f
            java.lang.String r2 = "NotificationActionPinnedStickerEmoji"
            r5 = 2131626613(0x7f0e0a75, float:1.8880467E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 2
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r8 = 1
            r1[r8] = r7     // Catch:{ all -> 0x03d3 }
            r3 = r3[r8]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0e3f:
            java.lang.String r1 = "NotificationActionPinnedSticker"
            r2 = 2131626611(0x7f0e0a73, float:1.8880463E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0e57:
            int r1 = r3.length     // Catch:{ all -> 0x03d3 }
            r2 = 1
            if (r1 <= r2) goto L_0x0e7b
            r1 = r3[r2]     // Catch:{ all -> 0x03d3 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x03d3 }
            if (r1 != 0) goto L_0x0e7b
            java.lang.String r1 = "NotificationActionPinnedStickerEmojiChannel"
            r2 = 2131626614(0x7f0e0a76, float:1.888047E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0e7b:
            java.lang.String r1 = "NotificationActionPinnedStickerChannel"
            r2 = 2131626612(0x7f0e0a74, float:1.8880465E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0e8e:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0eae
            java.lang.String r1 = "NotificationActionPinnedFileUser"
            r2 = 2131626574(0x7f0e0a4e, float:1.8880388E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0eae:
            if (r8 == 0) goto L_0x0ec8
            java.lang.String r1 = "NotificationActionPinnedFile"
            r2 = 2131626572(0x7f0e0a4c, float:1.8880384E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0ec8:
            java.lang.String r1 = "NotificationActionPinnedFileChannel"
            r2 = 2131626573(0x7f0e0a4d, float:1.8880386E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0edb:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0efb
            java.lang.String r1 = "NotificationActionPinnedRoundUser"
            r2 = 2131626610(0x7f0e0a72, float:1.8880461E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0efb:
            if (r8 == 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedRound"
            r2 = 2131626608(0x7f0e0a70, float:1.8880457E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0var_:
            java.lang.String r1 = "NotificationActionPinnedRoundChannel"
            r2 = 2131626609(0x7f0e0a71, float:1.888046E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0var_:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedVideoUser"
            r2 = 2131626622(0x7f0e0a7e, float:1.8880485E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0var_:
            if (r8 == 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedVideo"
            r2 = 2131626620(0x7f0e0a7c, float:1.8880481E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0var_:
            java.lang.String r1 = "NotificationActionPinnedVideoChannel"
            r2 = 2131626621(0x7f0e0a7d, float:1.8880483E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0var_:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedPhotoUser"
            r2 = 2131626601(0x7f0e0a69, float:1.8880443E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0var_:
            if (r8 == 0) goto L_0x0faf
            java.lang.String r1 = "NotificationActionPinnedPhoto"
            r2 = 2131626599(0x7f0e0a67, float:1.8880439E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0faf:
            java.lang.String r1 = "NotificationActionPinnedPhotoChannel"
            r2 = 2131626600(0x7f0e0a68, float:1.888044E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0fc2:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0fe2
            java.lang.String r1 = "NotificationActionPinnedNoTextUser"
            r2 = 2131626598(0x7f0e0a66, float:1.8880437E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0fe2:
            if (r8 == 0) goto L_0x0ffc
            java.lang.String r1 = "NotificationActionPinnedNoText"
            r2 = 2131626596(0x7f0e0a64, float:1.8880433E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x0ffc:
            java.lang.String r1 = "NotificationActionPinnedNoTextChannel"
            r2 = 2131626597(0x7f0e0a65, float:1.8880435E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x100f:
            r4 = r22
            r5 = 0
            int r2 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r2 <= 0) goto L_0x102f
            java.lang.String r1 = "NotificationActionPinnedTextUser"
            r2 = 2131626619(0x7f0e0a7b, float:1.888048E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x102f:
            if (r8 == 0) goto L_0x104e
            java.lang.String r2 = "NotificationActionPinnedText"
            r5 = 2131626617(0x7f0e0a79, float:1.8880475E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x104e:
            java.lang.String r1 = "NotificationActionPinnedTextChannel"
            r2 = 2131626618(0x7f0e0a7a, float:1.8880477E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x1066:
            r4 = r22
            java.lang.String r1 = "NotificationGroupAlbum"
            r2 = 2131626634(0x7f0e0a8a, float:1.888051E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x185e
        L_0x1080:
            r4 = r22
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r1[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r1[r5] = r6     // Catch:{ all -> 0x03d3 }
            java.lang.String r5 = "Files"
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d3 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d3 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)     // Catch:{ all -> 0x03d3 }
            r1[r6] = r3     // Catch:{ all -> 0x03d3 }
            r3 = 2131626637(0x7f0e0a8d, float:1.8880516E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r3, r1)     // Catch:{ all -> 0x03d3 }
            goto L_0x185e
        L_0x10ab:
            r4 = r22
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d3 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d3 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)     // Catch:{ all -> 0x03d3 }
            r1[r6] = r3     // Catch:{ all -> 0x03d3 }
            r3 = 2131626637(0x7f0e0a8d, float:1.8880516E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r3, r1)     // Catch:{ all -> 0x03d3 }
            goto L_0x185e
        L_0x10d4:
            r4 = r22
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r1[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r1[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 2
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d3 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d3 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r15, r3)     // Catch:{ all -> 0x03d3 }
            r1[r5] = r3     // Catch:{ all -> 0x03d3 }
            r3 = 2131626637(0x7f0e0a8d, float:1.8880516E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r3, r1)     // Catch:{ all -> 0x03d3 }
            goto L_0x185e
        L_0x10fd:
            r4 = r22
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r1[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r1[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 2
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d3 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d3 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r11, r3)     // Catch:{ all -> 0x03d3 }
            r1[r5] = r3     // Catch:{ all -> 0x03d3 }
            r3 = 2131626637(0x7f0e0a8d, float:1.8880516E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r3, r1)     // Catch:{ all -> 0x03d3 }
            goto L_0x185e
        L_0x1126:
            r4 = r22
            java.lang.String r2 = "NotificationGroupForwardedFew"
            r5 = 2131626638(0x7f0e0a8e, float:1.8880518E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d3 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d3 }
            r7 = r27
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r7, r3)     // Catch:{ all -> 0x03d3 }
            r1[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d3 }
            goto L_0x185e
        L_0x1153:
            r4 = r22
            java.lang.String r1 = "UserAcceptedToGroupPushWithGroup"
            r2 = 2131628272(0x7f0e10f0, float:1.8883832E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x116d:
            r4 = r22
            java.lang.String r1 = "NotificationGroupAddSelfMega"
            r2 = 2131626633(0x7f0e0a89, float:1.8880508E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x1187:
            r4 = r22
            java.lang.String r1 = "NotificationGroupAddSelf"
            r2 = 2131626632(0x7f0e0a88, float:1.8880506E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x11a1:
            r4 = r22
            java.lang.String r1 = "NotificationGroupLeftMember"
            r2 = 2131626643(0x7f0e0a93, float:1.8880528E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x11bb:
            r4 = r22
            java.lang.String r1 = "NotificationGroupKickYou"
            r2 = 2131626642(0x7f0e0a92, float:1.8880526E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x11d5:
            r4 = r22
            java.lang.String r1 = "NotificationGroupKickMember"
            r2 = 2131626641(0x7f0e0a91, float:1.8880524E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x11ef:
            r4 = r22
            java.lang.String r1 = "NotificationGroupInvitedYouToCall"
            r2 = 2131626640(0x7f0e0a90, float:1.8880522E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x1209:
            r4 = r22
            java.lang.String r1 = "NotificationGroupEndedCall"
            r2 = 2131626636(0x7f0e0a8c, float:1.8880514E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x1223:
            r4 = r22
            java.lang.String r2 = "NotificationGroupInvitedToCall"
            r5 = 2131626639(0x7f0e0a8f, float:1.888052E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x1242:
            r4 = r22
            java.lang.String r1 = "NotificationGroupCreatedCall"
            r2 = 2131626635(0x7f0e0a8b, float:1.8880512E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x125c:
            r4 = r22
            java.lang.String r2 = "NotificationGroupAddMember"
            r5 = 2131626631(0x7f0e0a87, float:1.8880504E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x127b:
            r4 = r22
            java.lang.String r1 = "NotificationEditedGroupPhoto"
            r2 = 2131626629(0x7f0e0a85, float:1.88805E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x1295:
            r4 = r22
            java.lang.String r1 = "NotificationEditedGroupName"
            r2 = 2131626628(0x7f0e0a84, float:1.8880498E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x12af:
            r4 = r22
            java.lang.String r1 = "NotificationInvitedToGroup"
            r2 = 2131626648(0x7f0e0a98, float:1.8880538E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x12c9:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupInvoice"
            r5 = 2131626665(0x7f0e0aa9, float:1.8880573E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "PaymentInvoice"
            r3 = 2131627060(0x7f0e0CLASSNAME, float:1.8881374E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x12f1:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupGameScored"
            r5 = 2131626663(0x7f0e0aa7, float:1.8880569E38)
            r6 = 4
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x03d3 }
            r7 = 0
            r8 = r3[r7]     // Catch:{ all -> 0x03d3 }
            r6[r7] = r8     // Catch:{ all -> 0x03d3 }
            r7 = 1
            r8 = r3[r7]     // Catch:{ all -> 0x03d3 }
            r6[r7] = r8     // Catch:{ all -> 0x03d3 }
            r7 = 2
            r8 = r3[r7]     // Catch:{ all -> 0x03d3 }
            r6[r7] = r8     // Catch:{ all -> 0x03d3 }
            r1 = 3
            r3 = r3[r1]     // Catch:{ all -> 0x03d3 }
            r6[r1] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x1315:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupGame"
            r5 = 2131626662(0x7f0e0aa6, float:1.8880567E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachGame"
            r3 = 2131624406(0x7f0e01d6, float:1.887599E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x133d:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupGif"
            r2 = 2131626664(0x7f0e0aa8, float:1.888057E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachGif"
            r3 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1360:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupLiveLocation"
            r2 = 2131626666(0x7f0e0aaa, float:1.8880575E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachLiveLocation"
            r3 = 2131624412(0x7f0e01dc, float:1.8876003E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1383:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupMap"
            r2 = 2131626667(0x7f0e0aab, float:1.8880577E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachLocation"
            r3 = 2131624416(0x7f0e01e0, float:1.8876011E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x13a6:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupPoll2"
            r5 = 2131626671(0x7f0e0aaf, float:1.8880585E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "Poll"
            r3 = 2131627227(0x7f0e0cdb, float:1.8881712E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x13ce:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupQuiz2"
            r5 = 2131626672(0x7f0e0ab0, float:1.8880587E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "PollQuiz"
            r3 = 2131627234(0x7f0e0ce2, float:1.8881727E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x13f6:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupContact2"
            r5 = 2131626660(0x7f0e0aa4, float:1.8880562E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachContact"
            r3 = 2131624402(0x7f0e01d2, float:1.8875983E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x141e:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupAudio"
            r2 = 2131626659(0x7f0e0aa3, float:1.888056E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachAudio"
            r3 = 2131624400(0x7f0e01d0, float:1.8875979E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1441:
            r4 = r22
            int r2 = r3.length     // Catch:{ all -> 0x03d3 }
            r5 = 2
            if (r2 <= r5) goto L_0x1487
            r2 = r3[r5]     // Catch:{ all -> 0x03d3 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x03d3 }
            if (r2 != 0) goto L_0x1487
            java.lang.String r2 = "NotificationMessageGroupStickerEmoji"
            r5 = 2131626675(0x7f0e0ab3, float:1.8880593E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r7 = 0
            r8 = r3[r7]     // Catch:{ all -> 0x03d3 }
            r1[r7] = r8     // Catch:{ all -> 0x03d3 }
            r7 = 1
            r8 = r3[r7]     // Catch:{ all -> 0x03d3 }
            r1[r7] = r8     // Catch:{ all -> 0x03d3 }
            r7 = 2
            r8 = r3[r7]     // Catch:{ all -> 0x03d3 }
            r1[r7] = r8     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d3 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03d3 }
            r2.<init>()     // Catch:{ all -> 0x03d3 }
            r3 = r3[r7]     // Catch:{ all -> 0x03d3 }
            r2.append(r3)     // Catch:{ all -> 0x03d3 }
            r2.append(r9)     // Catch:{ all -> 0x03d3 }
            r3 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)     // Catch:{ all -> 0x03d3 }
            r2.append(r3)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1487:
            java.lang.String r1 = "NotificationMessageGroupSticker"
            r2 = 2131626674(0x7f0e0ab2, float:1.888059E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r7 = 0
            r8 = r3[r7]     // Catch:{ all -> 0x03d3 }
            r5[r7] = r8     // Catch:{ all -> 0x03d3 }
            r7 = 1
            r8 = r3[r7]     // Catch:{ all -> 0x03d3 }
            r5[r7] = r8     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03d3 }
            r2.<init>()     // Catch:{ all -> 0x03d3 }
            r3 = r3[r7]     // Catch:{ all -> 0x03d3 }
            r2.append(r3)     // Catch:{ all -> 0x03d3 }
            r2.append(r9)     // Catch:{ all -> 0x03d3 }
            r3 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)     // Catch:{ all -> 0x03d3 }
            r2.append(r3)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x14ba:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupDocument"
            r2 = 2131626661(0x7f0e0aa5, float:1.8880564E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachDocument"
            r3 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x14dd:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupRound"
            r2 = 2131626673(0x7f0e0ab1, float:1.8880589E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachRound"
            r3 = 2131624423(0x7f0e01e7, float:1.8876025E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1500:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupVideo"
            r2 = 2131626677(0x7f0e0ab5, float:1.8880597E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachVideo"
            r3 = 2131624427(0x7f0e01eb, float:1.8876033E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1523:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupPhoto"
            r2 = 2131626670(0x7f0e0aae, float:1.8880583E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachPhoto"
            r3 = 2131624421(0x7f0e01e5, float:1.8876021E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1546:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupNoText"
            r2 = 2131626669(0x7f0e0aad, float:1.888058E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "Message"
            r3 = 2131626319(0x7f0e094f, float:1.887987E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1569:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupText"
            r5 = 2131626676(0x7f0e0ab4, float:1.8880595E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 2
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d3 }
            r2 = r3[r6]     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x158a:
            r4 = r22
            java.lang.String r1 = "ChannelMessageAlbum"
            r2 = 2131624784(0x7f0e0350, float:1.8876757E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            goto L_0x185e
        L_0x159f:
            r4 = r22
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r2 = 0
            r5 = r3[r2]     // Catch:{ all -> 0x03d3 }
            r1[r2] = r5     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "Files"
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d3 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r3)     // Catch:{ all -> 0x03d3 }
            r1[r5] = r2     // Catch:{ all -> 0x03d3 }
            r2 = 2131624788(0x7f0e0354, float:1.8876766E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r2, r1)     // Catch:{ all -> 0x03d3 }
            goto L_0x185e
        L_0x15c5:
            r4 = r22
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r2 = 0
            r6 = r3[r2]     // Catch:{ all -> 0x03d3 }
            r1[r2] = r6     // Catch:{ all -> 0x03d3 }
            r2 = 1
            r3 = r3[r2]     // Catch:{ all -> 0x03d3 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d3 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d3 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)     // Catch:{ all -> 0x03d3 }
            r1[r2] = r3     // Catch:{ all -> 0x03d3 }
            r2 = 2131624788(0x7f0e0354, float:1.8876766E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r2, r1)     // Catch:{ all -> 0x03d3 }
            goto L_0x185e
        L_0x15e9:
            r4 = r22
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r2 = 0
            r5 = r3[r2]     // Catch:{ all -> 0x03d3 }
            r1[r2] = r5     // Catch:{ all -> 0x03d3 }
            r2 = 1
            r3 = r3[r2]     // Catch:{ all -> 0x03d3 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d3 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d3 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r15, r3)     // Catch:{ all -> 0x03d3 }
            r1[r2] = r3     // Catch:{ all -> 0x03d3 }
            r2 = 2131624788(0x7f0e0354, float:1.8876766E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r2, r1)     // Catch:{ all -> 0x03d3 }
            goto L_0x185e
        L_0x160d:
            r4 = r22
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r2 = 0
            r5 = r3[r2]     // Catch:{ all -> 0x03d3 }
            r1[r2] = r5     // Catch:{ all -> 0x03d3 }
            r2 = 1
            r3 = r3[r2]     // Catch:{ all -> 0x03d3 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d3 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d3 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r11, r3)     // Catch:{ all -> 0x03d3 }
            r1[r2] = r3     // Catch:{ all -> 0x03d3 }
            r2 = 2131624788(0x7f0e0354, float:1.8876766E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r2, r1)     // Catch:{ all -> 0x03d3 }
            goto L_0x185e
        L_0x1631:
            r4 = r22
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r2 = 0
            r5 = r3[r2]     // Catch:{ all -> 0x03d3 }
            r1[r2] = r5     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "ForwardedMessageCount"
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d3 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r3)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = r2.toLowerCase()     // Catch:{ all -> 0x03d3 }
            r1[r5] = r2     // Catch:{ all -> 0x03d3 }
            r2 = 2131624788(0x7f0e0354, float:1.8876766E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r2, r1)     // Catch:{ all -> 0x03d3 }
            goto L_0x185e
        L_0x165b:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGame"
            r2 = 2131626656(0x7f0e0aa0, float:1.8880554E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachGame"
            r3 = 2131624406(0x7f0e01d6, float:1.887599E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1679:
            r4 = r22
            java.lang.String r1 = "ChannelMessageGIF"
            r2 = 2131624789(0x7f0e0355, float:1.8876768E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachGif"
            r3 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1697:
            r4 = r22
            java.lang.String r1 = "ChannelMessageLiveLocation"
            r2 = 2131624790(0x7f0e0356, float:1.887677E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachLiveLocation"
            r3 = 2131624412(0x7f0e01dc, float:1.8876003E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x16b5:
            r4 = r22
            java.lang.String r1 = "ChannelMessageMap"
            r2 = 2131624791(0x7f0e0357, float:1.8876772E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachLocation"
            r3 = 2131624416(0x7f0e01e0, float:1.8876011E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x16d3:
            r4 = r22
            java.lang.String r1 = "ChannelMessagePoll2"
            r2 = 2131624795(0x7f0e035b, float:1.887678E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "Poll"
            r3 = 2131627227(0x7f0e0cdb, float:1.8881712E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x16f6:
            r4 = r22
            java.lang.String r1 = "ChannelMessageQuiz2"
            r2 = 2131624796(0x7f0e035c, float:1.8876782E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "QuizPoll"
            r3 = 2131627408(0x7f0e0d90, float:1.888208E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1719:
            r4 = r22
            java.lang.String r1 = "ChannelMessageContact2"
            r2 = 2131624786(0x7f0e0352, float:1.8876762E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachContact"
            r3 = 2131624402(0x7f0e01d2, float:1.8875983E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x173c:
            r4 = r22
            java.lang.String r1 = "ChannelMessageAudio"
            r2 = 2131624785(0x7f0e0351, float:1.887676E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachAudio"
            r3 = 2131624400(0x7f0e01d0, float:1.8875979E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x175a:
            r4 = r22
            int r1 = r3.length     // Catch:{ all -> 0x03d3 }
            r2 = 1
            if (r1 <= r2) goto L_0x179b
            r1 = r3[r2]     // Catch:{ all -> 0x03d3 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x03d3 }
            if (r1 != 0) goto L_0x179b
            java.lang.String r1 = "ChannelMessageStickerEmoji"
            r2 = 2131624799(0x7f0e035f, float:1.8876788E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r7 = 0
            r8 = r3[r7]     // Catch:{ all -> 0x03d3 }
            r5[r7] = r8     // Catch:{ all -> 0x03d3 }
            r7 = 1
            r8 = r3[r7]     // Catch:{ all -> 0x03d3 }
            r5[r7] = r8     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03d3 }
            r2.<init>()     // Catch:{ all -> 0x03d3 }
            r3 = r3[r7]     // Catch:{ all -> 0x03d3 }
            r2.append(r3)     // Catch:{ all -> 0x03d3 }
            r2.append(r9)     // Catch:{ all -> 0x03d3 }
            r3 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)     // Catch:{ all -> 0x03d3 }
            r2.append(r3)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x179b:
            java.lang.String r1 = "ChannelMessageSticker"
            r2 = 2131624798(0x7f0e035e, float:1.8876786E38)
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r7[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x03d3 }
            r2 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x17b5:
            r4 = r22
            java.lang.String r1 = "ChannelMessageDocument"
            r2 = 2131624787(0x7f0e0353, float:1.8876764E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachDocument"
            r3 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x17d3:
            r4 = r22
            java.lang.String r1 = "ChannelMessageRound"
            r2 = 2131624797(0x7f0e035d, float:1.8876784E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachRound"
            r3 = 2131624423(0x7f0e01e7, float:1.8876025E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x17f1:
            r4 = r22
            java.lang.String r1 = "ChannelMessageVideo"
            r2 = 2131624800(0x7f0e0360, float:1.887679E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachVideo"
            r3 = 2131624427(0x7f0e01eb, float:1.8876033E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x180f:
            r4 = r22
            java.lang.String r1 = "ChannelMessagePhoto"
            r2 = 2131624794(0x7f0e035a, float:1.8876778E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachPhoto"
            r3 = 2131624421(0x7f0e01e5, float:1.8876021E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x182d:
            r4 = r22
            java.lang.String r1 = "ChannelMessageNoText"
            r2 = 2131624793(0x7f0e0359, float:1.8876776E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "Message"
            r3 = 2131626319(0x7f0e094f, float:1.887987E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x184b:
            r4 = r22
            java.lang.String r1 = "NotificationMessageAlbum"
            r2 = 2131626650(0x7f0e0a9a, float:1.8880542E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
        L_0x185e:
            r22 = r39
            r2 = 1
            goto L_0x1bdb
        L_0x1863:
            r4 = r22
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r2[r5] = r6     // Catch:{ all -> 0x03d3 }
            java.lang.String r5 = "Files"
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d3 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d3 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)     // Catch:{ all -> 0x03d3 }
            r2[r6] = r3     // Catch:{ all -> 0x03d3 }
            r3 = 2131626654(0x7f0e0a9e, float:1.888055E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r2)     // Catch:{ all -> 0x03d3 }
            goto L_0x185e
        L_0x1888:
            r4 = r22
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r2[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d3 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d3 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)     // Catch:{ all -> 0x03d3 }
            r2[r6] = r3     // Catch:{ all -> 0x03d3 }
            r3 = 2131626654(0x7f0e0a9e, float:1.888055E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r2)     // Catch:{ all -> 0x03d3 }
            goto L_0x185e
        L_0x18ab:
            r4 = r22
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r2[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d3 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d3 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r15, r3)     // Catch:{ all -> 0x03d3 }
            r2[r5] = r3     // Catch:{ all -> 0x03d3 }
            r3 = 2131626654(0x7f0e0a9e, float:1.888055E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r2)     // Catch:{ all -> 0x03d3 }
            goto L_0x185e
        L_0x18ce:
            r4 = r22
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r2[r5] = r6     // Catch:{ all -> 0x03d3 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d3 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d3 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r11, r3)     // Catch:{ all -> 0x03d3 }
            r2[r5] = r3     // Catch:{ all -> 0x03d3 }
            r3 = 2131626654(0x7f0e0a9e, float:1.888055E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r2)     // Catch:{ all -> 0x03d3 }
            goto L_0x185e
        L_0x18f2:
            r4 = r22
            r7 = r27
            java.lang.String r1 = "NotificationMessageForwardFew"
            r2 = 2131626655(0x7f0e0a9f, float:1.8880552E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r8 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r8     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d3 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d3 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r7, r3)     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            goto L_0x185e
        L_0x191a:
            r4 = r22
            java.lang.String r1 = "NotificationMessageInvoice"
            r2 = 2131626678(0x7f0e0ab6, float:1.8880599E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "PaymentInvoice"
            r3 = 2131627060(0x7f0e0CLASSNAME, float:1.8881374E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x193d:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGameScored"
            r5 = 2131626657(0x7f0e0aa1, float:1.8880556E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r1[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d3 }
            goto L_0x1b0b
        L_0x195c:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGame"
            r2 = 2131626656(0x7f0e0aa0, float:1.8880554E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachGame"
            r3 = 2131624406(0x7f0e01d6, float:1.887599E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x197f:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGif"
            r2 = 2131626658(0x7f0e0aa2, float:1.8880558E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachGif"
            r3 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x199d:
            r4 = r22
            java.lang.String r1 = "NotificationMessageLiveLocation"
            r2 = 2131626679(0x7f0e0ab7, float:1.88806E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachLiveLocation"
            r3 = 2131624412(0x7f0e01dc, float:1.8876003E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x19bb:
            r4 = r22
            java.lang.String r1 = "NotificationMessageMap"
            r2 = 2131626680(0x7f0e0ab8, float:1.8880603E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachLocation"
            r3 = 2131624416(0x7f0e01e0, float:1.8876011E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x19d9:
            r4 = r22
            java.lang.String r1 = "NotificationMessagePoll2"
            r2 = 2131626684(0x7f0e0abc, float:1.8880611E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "Poll"
            r3 = 2131627227(0x7f0e0cdb, float:1.8881712E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x19fc:
            r4 = r22
            java.lang.String r1 = "NotificationMessageQuiz2"
            r2 = 2131626685(0x7f0e0abd, float:1.8880613E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "QuizPoll"
            r3 = 2131627408(0x7f0e0d90, float:1.888208E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1a1f:
            r4 = r22
            java.lang.String r1 = "NotificationMessageContact2"
            r2 = 2131626652(0x7f0e0a9c, float:1.8880546E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachContact"
            r3 = 2131624402(0x7f0e01d2, float:1.8875983E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1a42:
            r4 = r22
            java.lang.String r1 = "NotificationMessageAudio"
            r2 = 2131626651(0x7f0e0a9b, float:1.8880544E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachAudio"
            r3 = 2131624400(0x7f0e01d0, float:1.8875979E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1a60:
            r4 = r22
            int r1 = r3.length     // Catch:{ all -> 0x03d3 }
            r2 = 1
            if (r1 <= r2) goto L_0x1aa1
            r1 = r3[r2]     // Catch:{ all -> 0x03d3 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x03d3 }
            if (r1 != 0) goto L_0x1aa1
            java.lang.String r1 = "NotificationMessageStickerEmoji"
            r2 = 2131626692(0x7f0e0ac4, float:1.8880627E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r7 = 0
            r8 = r3[r7]     // Catch:{ all -> 0x03d3 }
            r5[r7] = r8     // Catch:{ all -> 0x03d3 }
            r7 = 1
            r8 = r3[r7]     // Catch:{ all -> 0x03d3 }
            r5[r7] = r8     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03d3 }
            r2.<init>()     // Catch:{ all -> 0x03d3 }
            r3 = r3[r7]     // Catch:{ all -> 0x03d3 }
            r2.append(r3)     // Catch:{ all -> 0x03d3 }
            r2.append(r9)     // Catch:{ all -> 0x03d3 }
            r3 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)     // Catch:{ all -> 0x03d3 }
            r2.append(r3)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1aa1:
            java.lang.String r1 = "NotificationMessageSticker"
            r2 = 2131626691(0x7f0e0ac3, float:1.8880625E38)
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r7[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x03d3 }
            r2 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1abb:
            r4 = r22
            java.lang.String r1 = "NotificationMessageDocument"
            r2 = 2131626653(0x7f0e0a9d, float:1.8880548E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachDocument"
            r3 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1ad9:
            r4 = r22
            java.lang.String r1 = "NotificationMessageRound"
            r2 = 2131626686(0x7f0e0abe, float:1.8880615E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachRound"
            r3 = 2131624423(0x7f0e01e7, float:1.8876025E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1af7:
            r4 = r22
            java.lang.String r1 = "ActionTakeScreenshoot"
            r2 = 2131624172(0x7f0e00ec, float:1.8875516E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "un1"
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = r1.replace(r2, r3)     // Catch:{ all -> 0x03d3 }
        L_0x1b0b:
            r22 = r39
            goto L_0x1bda
        L_0x1b0f:
            r4 = r22
            java.lang.String r1 = "NotificationMessageSDVideo"
            r2 = 2131626688(0x7f0e0ac0, float:1.888062E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachDestructingVideo"
            r3 = 2131624404(0x7f0e01d4, float:1.8875987E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1b2d:
            r4 = r22
            java.lang.String r1 = "NotificationMessageVideo"
            r2 = 2131626694(0x7f0e0ac6, float:1.8880631E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachVideo"
            r3 = 2131624427(0x7f0e01eb, float:1.8876033E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1b4a:
            r4 = r22
            java.lang.String r1 = "NotificationMessageSDPhoto"
            r2 = 2131626687(0x7f0e0abf, float:1.8880617E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachDestructingPhoto"
            r3 = 2131624403(0x7f0e01d3, float:1.8875985E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1b67:
            r4 = r22
            java.lang.String r1 = "NotificationMessagePhoto"
            r2 = 2131626683(0x7f0e0abb, float:1.888061E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "AttachPhoto"
            r3 = 2131624421(0x7f0e01e5, float:1.8876021E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1b84:
            r4 = r22
            java.lang.String r1 = "NotificationMessageNoText"
            r2 = 2131626682(0x7f0e0aba, float:1.8880607E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d3 }
            r6[r5] = r3     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "Message"
            r3 = 2131626319(0x7f0e094f, float:1.887987E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d3 }
        L_0x1ba0:
            r17 = r2
            r22 = r39
            r2 = 0
            goto L_0x1bdd
        L_0x1ba6:
            r4 = r22
            java.lang.String r1 = "NotificationMessageText"
            r2 = 2131626693(0x7f0e0ac5, float:1.888063E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d3 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d3 }
            r5[r6] = r7     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d3 }
            r2 = r3[r6]     // Catch:{ all -> 0x03d3 }
            goto L_0x1ba0
        L_0x1bc1:
            if (r1 == 0) goto L_0x1bd7
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x03d3 }
            r1.<init>()     // Catch:{ all -> 0x03d3 }
            java.lang.String r2 = "unhandled loc_key = "
            r1.append(r2)     // Catch:{ all -> 0x03d3 }
            r1.append(r14)     // Catch:{ all -> 0x03d3 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x03d3 }
            org.telegram.messenger.FileLog.w(r1)     // Catch:{ all -> 0x03d3 }
        L_0x1bd7:
            r22 = r39
            r1 = 0
        L_0x1bda:
            r2 = 0
        L_0x1bdb:
            r17 = 0
        L_0x1bdd:
            if (r1 == 0) goto L_0x1cbb
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x1cbe }
            r3.<init>()     // Catch:{ all -> 0x1cbe }
            r3.id = r10     // Catch:{ all -> 0x1cbe }
            r5 = r30
            r3.random_id = r5     // Catch:{ all -> 0x1cbe }
            if (r17 == 0) goto L_0x1bef
            r5 = r17
            goto L_0x1bf0
        L_0x1bef:
            r5 = r1
        L_0x1bf0:
            r3.message = r5     // Catch:{ all -> 0x1cbe }
            r5 = 1000(0x3e8, double:4.94E-321)
            long r5 = r51 / r5
            int r6 = (int) r5     // Catch:{ all -> 0x1cbe }
            r3.date = r6     // Catch:{ all -> 0x1cbe }
            if (r38 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r5 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x03d3 }
            r5.<init>()     // Catch:{ all -> 0x03d3 }
            r3.action = r5     // Catch:{ all -> 0x03d3 }
        L_0x1CLASSNAME:
            if (r37 == 0) goto L_0x1c0b
            int r5 = r3.flags     // Catch:{ all -> 0x03d3 }
            r6 = -2147483648(0xfffffffvar_, float:-0.0)
            r5 = r5 | r6
            r3.flags = r5     // Catch:{ all -> 0x03d3 }
        L_0x1c0b:
            r3.dialog_id = r12     // Catch:{ all -> 0x1cbe }
            r5 = 0
            int r7 = (r45 > r5 ? 1 : (r45 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_peerChannel r5 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x03d3 }
            r5.<init>()     // Catch:{ all -> 0x03d3 }
            r3.peer_id = r5     // Catch:{ all -> 0x03d3 }
            r6 = r45
            r5.channel_id = r6     // Catch:{ all -> 0x03d3 }
            r12 = r25
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r5 = 0
            int r7 = (r25 > r5 ? 1 : (r25 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_peerChat r5 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x03d3 }
            r5.<init>()     // Catch:{ all -> 0x03d3 }
            r3.peer_id = r5     // Catch:{ all -> 0x03d3 }
            r12 = r25
            r5.chat_id = r12     // Catch:{ all -> 0x03d3 }
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r12 = r25
            org.telegram.tgnet.TLRPC$TL_peerUser r5 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1cbe }
            r5.<init>()     // Catch:{ all -> 0x1cbe }
            r3.peer_id = r5     // Catch:{ all -> 0x1cbe }
            r6 = r23
            r5.user_id = r6     // Catch:{ all -> 0x1cbe }
        L_0x1CLASSNAME:
            int r5 = r3.flags     // Catch:{ all -> 0x1cbe }
            r5 = r5 | 256(0x100, float:3.59E-43)
            r3.flags = r5     // Catch:{ all -> 0x1cbe }
            r5 = 0
            int r7 = (r43 > r5 ? 1 : (r43 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_peerChat r5 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x03d3 }
            r5.<init>()     // Catch:{ all -> 0x03d3 }
            r3.from_id = r5     // Catch:{ all -> 0x03d3 }
            r5.chat_id = r12     // Catch:{ all -> 0x03d3 }
            goto L_0x1c7e
        L_0x1CLASSNAME:
            r5 = 0
            int r7 = (r32 > r5 ? 1 : (r32 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_peerChannel r5 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x03d3 }
            r5.<init>()     // Catch:{ all -> 0x03d3 }
            r3.from_id = r5     // Catch:{ all -> 0x03d3 }
            r6 = r32
            r5.channel_id = r6     // Catch:{ all -> 0x03d3 }
            goto L_0x1c7e
        L_0x1CLASSNAME:
            r5 = 0
            int r7 = (r35 > r5 ? 1 : (r35 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x1c7a
            org.telegram.tgnet.TLRPC$TL_peerUser r5 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x03d3 }
            r5.<init>()     // Catch:{ all -> 0x03d3 }
            r3.from_id = r5     // Catch:{ all -> 0x03d3 }
            r6 = r35
            r5.user_id = r6     // Catch:{ all -> 0x03d3 }
            goto L_0x1c7e
        L_0x1c7a:
            org.telegram.tgnet.TLRPC$Peer r5 = r3.peer_id     // Catch:{ all -> 0x1cbe }
            r3.from_id = r5     // Catch:{ all -> 0x1cbe }
        L_0x1c7e:
            if (r29 != 0) goto L_0x1CLASSNAME
            if (r38 == 0) goto L_0x1CLASSNAME
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r5 = 0
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r5 = 1
        L_0x1CLASSNAME:
            r3.mentioned = r5     // Catch:{ all -> 0x1cbe }
            r5 = r34
            r3.silent = r5     // Catch:{ all -> 0x1cbe }
            r3.from_scheduled = r4     // Catch:{ all -> 0x1cbe }
            org.telegram.messenger.MessageObject r4 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x1cbe }
            r18 = r4
            r19 = r28
            r20 = r3
            r21 = r1
            r23 = r42
            r24 = r2
            r25 = r41
            r26 = r37
            r27 = r40
            r18.<init>(r19, r20, r21, r22, r23, r24, r25, r26, r27)     // Catch:{ all -> 0x1cbe }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x1cbe }
            r1.<init>()     // Catch:{ all -> 0x1cbe }
            r1.add(r4)     // Catch:{ all -> 0x1cbe }
            org.telegram.messenger.NotificationsController r2 = org.telegram.messenger.NotificationsController.getInstance(r28)     // Catch:{ all -> 0x1cbe }
            r3 = r49
            java.util.concurrent.CountDownLatch r4 = r3.countDownLatch     // Catch:{ all -> 0x1cda }
            r5 = 1
            r2.processNewMessages(r1, r5, r5, r4)     // Catch:{ all -> 0x1cda }
            r8 = 0
            goto L_0x1cc7
        L_0x1cbb:
            r3 = r49
            goto L_0x1cc6
        L_0x1cbe:
            r0 = move-exception
            r3 = r49
            goto L_0x1ce4
        L_0x1cc2:
            r0 = move-exception
            r3 = r1
            goto L_0x1ce4
        L_0x1cc5:
            r3 = r1
        L_0x1cc6:
            r8 = 1
        L_0x1cc7:
            if (r8 == 0) goto L_0x1cce
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1cda }
            r1.countDown()     // Catch:{ all -> 0x1cda }
        L_0x1cce:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r28)     // Catch:{ all -> 0x1cda }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r28)     // Catch:{ all -> 0x1cda }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1cda }
            goto L_0x1e0c
        L_0x1cda:
            r0 = move-exception
            goto L_0x1ce4
        L_0x1cdc:
            r0 = move-exception
            r3 = r1
            r14 = r29
            goto L_0x1ce4
        L_0x1ce1:
            r0 = move-exception
            r3 = r1
            r14 = r9
        L_0x1ce4:
            r1 = r0
            r7 = r16
            r4 = r28
            goto L_0x1dba
        L_0x1ceb:
            r0 = move-exception
            r3 = r1
            r28 = r4
            r14 = r9
            goto L_0x1db0
        L_0x1cf2:
            r0 = move-exception
            r3 = r1
            r28 = r4
            goto L_0x1db6
        L_0x1cf8:
            r3 = r1
            r28 = r4
            r16 = r7
            r14 = r9
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1d10 }
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1 r2 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1     // Catch:{ all -> 0x1d10 }
            r4 = r28
            r2.<init>(r4)     // Catch:{ all -> 0x1daf }
            r1.postRunnable(r2)     // Catch:{ all -> 0x1daf }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1daf }
            r1.countDown()     // Catch:{ all -> 0x1daf }
            return
        L_0x1d10:
            r0 = move-exception
            r4 = r28
            goto L_0x1db0
        L_0x1d15:
            r3 = r1
            r16 = r7
            r14 = r9
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0 r1 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0     // Catch:{ all -> 0x1daf }
            r1.<init>(r4)     // Catch:{ all -> 0x1daf }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ all -> 0x1daf }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1daf }
            r1.countDown()     // Catch:{ all -> 0x1daf }
            return
        L_0x1d27:
            r3 = r1
            r16 = r7
            r14 = r9
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x1daf }
            r1.<init>()     // Catch:{ all -> 0x1daf }
            r2 = 0
            r1.popup = r2     // Catch:{ all -> 0x1daf }
            r2 = 2
            r1.flags = r2     // Catch:{ all -> 0x1daf }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r6 = r51 / r6
            int r2 = (int) r6     // Catch:{ all -> 0x1daf }
            r1.inbox_date = r2     // Catch:{ all -> 0x1daf }
            java.lang.String r2 = "message"
            java.lang.String r2 = r5.getString(r2)     // Catch:{ all -> 0x1daf }
            r1.message = r2     // Catch:{ all -> 0x1daf }
            java.lang.String r2 = "announcement"
            r1.type = r2     // Catch:{ all -> 0x1daf }
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ all -> 0x1daf }
            r2.<init>()     // Catch:{ all -> 0x1daf }
            r1.media = r2     // Catch:{ all -> 0x1daf }
            org.telegram.tgnet.TLRPC$TL_updates r2 = new org.telegram.tgnet.TLRPC$TL_updates     // Catch:{ all -> 0x1daf }
            r2.<init>()     // Catch:{ all -> 0x1daf }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r5 = r2.updates     // Catch:{ all -> 0x1daf }
            r5.add(r1)     // Catch:{ all -> 0x1daf }
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1daf }
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3 r5 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3     // Catch:{ all -> 0x1daf }
            r5.<init>(r4, r2)     // Catch:{ all -> 0x1daf }
            r1.postRunnable(r5)     // Catch:{ all -> 0x1daf }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1daf }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1daf }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1daf }
            r1.countDown()     // Catch:{ all -> 0x1daf }
            return
        L_0x1d71:
            r3 = r1
            r16 = r7
            r14 = r9
            java.lang.String r1 = "dc"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x1daf }
            java.lang.String r2 = "addr"
            java.lang.String r2 = r11.getString(r2)     // Catch:{ all -> 0x1daf }
            java.lang.String r5 = ":"
            java.lang.String[] r2 = r2.split(r5)     // Catch:{ all -> 0x1daf }
            int r5 = r2.length     // Catch:{ all -> 0x1daf }
            r6 = 2
            if (r5 == r6) goto L_0x1d91
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1daf }
            r1.countDown()     // Catch:{ all -> 0x1daf }
            return
        L_0x1d91:
            r5 = 0
            r5 = r2[r5]     // Catch:{ all -> 0x1daf }
            r6 = 1
            r2 = r2[r6]     // Catch:{ all -> 0x1daf }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ all -> 0x1daf }
            org.telegram.tgnet.ConnectionsManager r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1daf }
            r6.applyDatacenterAddress(r1, r5, r2)     // Catch:{ all -> 0x1daf }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1daf }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1daf }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1daf }
            r1.countDown()     // Catch:{ all -> 0x1daf }
            return
        L_0x1daf:
            r0 = move-exception
        L_0x1db0:
            r1 = r0
            r7 = r16
            goto L_0x1dba
        L_0x1db4:
            r0 = move-exception
            r3 = r1
        L_0x1db6:
            r16 = r7
            r14 = r9
            r1 = r0
        L_0x1dba:
            r2 = -1
            goto L_0x1dd4
        L_0x1dbc:
            r0 = move-exception
            r3 = r1
            r16 = r7
            r14 = r9
            r1 = r0
            r2 = -1
            r4 = -1
            goto L_0x1dd4
        L_0x1dc5:
            r0 = move-exception
            r3 = r1
            r16 = r7
            r1 = r0
            r2 = -1
            r4 = -1
            goto L_0x1dd3
        L_0x1dcd:
            r0 = move-exception
            r3 = r1
            r1 = r0
            r2 = -1
            r4 = -1
            r7 = 0
        L_0x1dd3:
            r14 = 0
        L_0x1dd4:
            if (r4 == r2) goto L_0x1de6
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r4)
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
            r2.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r2 = r3.countDownLatch
            r2.countDown()
            goto L_0x1de9
        L_0x1de6:
            r49.onDecryptError()
        L_0x1de9:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x1e09
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "error in loc_key = "
            r2.append(r4)
            r2.append(r14)
            java.lang.String r4 = " json "
            r2.append(r4)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r2)
        L_0x1e09:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1e0c:
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
