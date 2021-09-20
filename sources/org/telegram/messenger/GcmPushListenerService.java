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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v79, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v192, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v138, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v196, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v201, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v228, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v82, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v206, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v211, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v200, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v216, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v217, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v206, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v221, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v213, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v223, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v166, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v226, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v227, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v227, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v228, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v229, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v232, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v232, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v181, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v280, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v277, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v287, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v101, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v292, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v244, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v108, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v270, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v313, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v273, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v208, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v209, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v210, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v252, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v280, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v212, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v213, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v285, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v256, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v214, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v257, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v258, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v290, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v149, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v216, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v260, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v217, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v295, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v262, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v263, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v158, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v264, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v300, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v266, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v221, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v305, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v268, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v170, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v269, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v172, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v223, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v270, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v311, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v312, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v313, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v316, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v272, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v273, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v298, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v299, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v302, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:1024:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x01fd, code lost:
        if (r2 == 0) goto L_0x1d51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x01ff, code lost:
        if (r2 == 1) goto L_0x1d07;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x0201, code lost:
        if (r2 == 2) goto L_0x1cf5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x0203, code lost:
        if (r2 == 3) goto L_0x1cd8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x020d, code lost:
        if (r11.has("channel_id") == false) goto L_0x0223;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x020f, code lost:
        r16 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:?, code lost:
        r6 = r11.getLong("channel_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0217, code lost:
        r12 = -r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x0219, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x021a, code lost:
        r3 = r1;
        r14 = r9;
        r7 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x0223, code lost:
        r16 = r7;
        r6 = 0;
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x022d, code lost:
        if (r11.has("from_id") == false) goto L_0x0239;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:?, code lost:
        r12 = r11.getLong("from_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x0235, code lost:
        r28 = r4;
        r3 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x0239, code lost:
        r28 = r4;
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x0242, code lost:
        if (r11.has("chat_id") == false) goto L_0x025f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:163:?, code lost:
        r12 = r11.getLong("chat_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:164:0x024a, code lost:
        r29 = r9;
        r8 = r12;
        r12 = -r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:165:0x0253, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x0254, code lost:
        r29 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:167:0x0256, code lost:
        r3 = r1;
        r7 = r16;
        r4 = r28;
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x025f, code lost:
        r29 = r9;
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x0268, code lost:
        if (r11.has("encryption_id") == false) goto L_0x0278;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:175:?, code lost:
        r12 = org.telegram.messenger.DialogObject.makeEncryptedDialogId((long) r11.getInt("encryption_id"));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x0276, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x027e, code lost:
        if (r11.has("schedule") == false) goto L_0x028a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x0286, code lost:
        if (r11.getInt("schedule") != 1) goto L_0x028a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:0x0288, code lost:
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x028a, code lost:
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x028d, code lost:
        if (r12 != 0) goto L_0x02a8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x028f, code lost:
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x0297, code lost:
        if ("ENCRYPTED_MESSAGE".equals(r14) == false) goto L_0x02aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x0299, code lost:
        r12 = org.telegram.messenger.NotificationsController.globalSecretChatId;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x029c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x02a1, code lost:
        r3 = r1;
        r7 = r16;
        r4 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x02a8, code lost:
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x02ae, code lost:
        if (r12 == 0) goto L_0x1ca5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x02b2, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x02b8, code lost:
        if ("READ_HISTORY".equals(r14) == false) goto L_0x032c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:?, code lost:
        r2 = r11.getInt("max_id");
        r5 = new java.util.ArrayList();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x02c7, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x02e3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x02c9, code lost:
        org.telegram.messenger.FileLog.d("GCM received read notification max_id = " + r2 + " for dialogId = " + r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x02e7, code lost:
        if (r6 == 0) goto L_0x02f6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x02e9, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox();
        r3.channel_id = r6;
        r3.max_id = r2;
        r5.add(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x02f6, code lost:
        r6 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x02ff, code lost:
        if (r3 == 0) goto L_0x030b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x0301, code lost:
        r7 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r6.peer = r7;
        r7.user_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x030b, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r6.peer = r3;
        r3.chat_id = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x0314, code lost:
        r6.max_id = r2;
        r5.add(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x0319, code lost:
        org.telegram.messenger.MessagesController.getInstance(r28).processUpdateArray(r5, (java.util.ArrayList<org.telegram.tgnet.TLRPC$User>) null, (java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat>) null, false, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x0332, code lost:
        r22 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x0336, code lost:
        if ("MESSAGE_DELETED".equals(r14) == false) goto L_0x03a2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:?, code lost:
        r2 = r11.getString("messages").split(",");
        r3 = new androidx.collection.LongSparseArray();
        r4 = new java.util.ArrayList();
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x034e, code lost:
        if (r8 >= r2.length) goto L_0x035c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x0350, code lost:
        r4.add(org.telegram.messenger.Utilities.parseInt(r2[r8]));
        r8 = r8 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x035c, code lost:
        r3.put(-r6, r4);
        org.telegram.messenger.NotificationsController.getInstance(r28).removeDeletedMessagesFromNotifications(r3);
        org.telegram.messenger.MessagesController.getInstance(r28).deleteMessagesByPush(r12, r4, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x0376, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1ca5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x0378, code lost:
        org.telegram.messenger.FileLog.d("GCM received " + r14 + " for dialogId = " + r12 + " mids = " + android.text.TextUtils.join(",", r4));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x03a6, code lost:
        if (android.text.TextUtils.isEmpty(r14) != false) goto L_0x1ca5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:0x03ae, code lost:
        if (r11.has("msg_id") == false) goto L_0x03b7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:?, code lost:
        r10 = r11.getInt("msg_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x03b7, code lost:
        r10 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x03ba, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x03be, code lost:
        if (r11.has("random_id") == false) goto L_0x03e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:?, code lost:
        r47 = r3;
        r3 = org.telegram.messenger.Utilities.parseLong(r11.getString("random_id")).longValue();
        r23 = r47;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x03d5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x03d6, code lost:
        r2 = -1;
        r3 = r49;
        r1 = r0;
        r7 = r16;
        r4 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x03e0, code lost:
        r23 = r3;
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x03e4, code lost:
        if (r10 == 0) goto L_0x041b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x03e6, code lost:
        r25 = r8;
        r1 = org.telegram.messenger.MessagesController.getInstance(r28).dialogs_read_inbox_max.get(java.lang.Long.valueOf(r12));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x03f8, code lost:
        if (r1 != null) goto L_0x0414;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x03fa, code lost:
        r1 = java.lang.Integer.valueOf(org.telegram.messenger.MessagesStorage.getInstance(r28).getDialogReadMax(false, r12));
        org.telegram.messenger.MessagesController.getInstance(r28).dialogs_read_inbox_max.put(java.lang.Long.valueOf(r12), r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x0418, code lost:
        if (r10 <= r1.intValue()) goto L_0x042f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x041b, code lost:
        r25 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x0421, code lost:
        if (r3 == 0) goto L_0x042f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x042b, code lost:
        if (org.telegram.messenger.MessagesStorage.getInstance(r28).checkMessageByRandomId(r3) != false) goto L_0x042f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x042d, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x042f, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x0430, code lost:
        if (r1 == false) goto L_0x1c9b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x0432, code lost:
        r30 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:?, code lost:
        r3 = r11.optLong("chat_from_id", 0);
        r27 = "messages";
        r32 = r11.optLong("chat_from_broadcast_id", 0);
        r1 = r11.optLong("chat_from_group_id", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x044e, code lost:
        if (r3 != 0) goto L_0x0457;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x0452, code lost:
        if (r1 == 0) goto L_0x0455;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x0455, code lost:
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x0457, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x045e, code lost:
        if (r11.has("mention") == false) goto L_0x046b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0466, code lost:
        if (r11.getInt("mention") == 0) goto L_0x046b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x0468, code lost:
        r29 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x046b, code lost:
        r29 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x0474, code lost:
        if (r11.has("silent") == false) goto L_0x0482;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x047d, code lost:
        if (r11.getInt("silent") == 0) goto L_0x0482;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x047f, code lost:
        r34 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x0482, code lost:
        r34 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x048a, code lost:
        if (r5.has("loc_args") == false) goto L_0x04a6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:?, code lost:
        r5 = r5.getJSONArray("loc_args");
        r9 = r5.length();
        r35 = r3;
        r3 = new java.lang.String[r9];
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x049b, code lost:
        if (r4 >= r9) goto L_0x04a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x049d, code lost:
        r3[r4] = r5.getString(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x04a3, code lost:
        r4 = r4 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x04a6, code lost:
        r35 = r3;
        r3 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:?, code lost:
        r5 = r3[0];
        r4 = r11.has("edit_date");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x04b8, code lost:
        if (r14.startsWith("CHAT_") == false) goto L_0x04ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x04be, code lost:
        if (org.telegram.messenger.UserObject.isReplyUser(r12) == false) goto L_0x04d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x04c0, code lost:
        r5 = r5 + " @ " + r3[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:311:0x04dc, code lost:
        if (r6 == 0) goto L_0x04e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x04de, code lost:
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x04e0, code lost:
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x04e4, code lost:
        r11 = false;
        r38 = false;
        r47 = r9;
        r9 = r5;
        r5 = r3[1];
        r37 = r47;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x04f5, code lost:
        if (r14.startsWith("PINNED_") == false) goto L_0x0507;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x04fb, code lost:
        if (r6 == 0) goto L_0x04ff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x04fd, code lost:
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x04ff, code lost:
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x0500, code lost:
        r37 = r9;
        r9 = null;
        r11 = false;
        r38 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x050d, code lost:
        if (r14.startsWith("CHANNEL_") == false) goto L_0x0512;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x050f, code lost:
        r9 = null;
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x0512, code lost:
        r9 = null;
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x0514, code lost:
        r37 = false;
        r38 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x051a, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x0543;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x051c, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x0543, code lost:
        r40 = r4;
        r39 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x054b, code lost:
        switch(r14.hashCode()) {
            case -2100047043: goto L_0x0a74;
            case -2091498420: goto L_0x0a69;
            case -2053872415: goto L_0x0a5e;
            case -2039746363: goto L_0x0a53;
            case -2023218804: goto L_0x0a48;
            case -1979538588: goto L_0x0a3d;
            case -1979536003: goto L_0x0a32;
            case -1979535888: goto L_0x0a27;
            case -1969004705: goto L_0x0a1c;
            case -1946699248: goto L_0x0a10;
            case -1646640058: goto L_0x0a04;
            case -1528047021: goto L_0x09f8;
            case -1493579426: goto L_0x09ec;
            case -1482481933: goto L_0x09e0;
            case -1480102982: goto L_0x09d5;
            case -1478041834: goto L_0x09c9;
            case -1474543101: goto L_0x09be;
            case -1465695932: goto L_0x09b2;
            case -1374906292: goto L_0x09a6;
            case -1372940586: goto L_0x099a;
            case -1264245338: goto L_0x098e;
            case -1236154001: goto L_0x0982;
            case -1236086700: goto L_0x0976;
            case -1236077786: goto L_0x096a;
            case -1235796237: goto L_0x095e;
            case -1235760759: goto L_0x0952;
            case -1235686303: goto L_0x0947;
            case -1198046100: goto L_0x093c;
            case -1124254527: goto L_0x0930;
            case -1085137927: goto L_0x0924;
            case -1084856378: goto L_0x0918;
            case -1084820900: goto L_0x090c;
            case -1084746444: goto L_0x0900;
            case -819729482: goto L_0x08f4;
            case -772141857: goto L_0x08e8;
            case -638310039: goto L_0x08dc;
            case -590403924: goto L_0x08d0;
            case -589196239: goto L_0x08c4;
            case -589193654: goto L_0x08b8;
            case -589193539: goto L_0x08ac;
            case -440169325: goto L_0x08a0;
            case -412748110: goto L_0x0894;
            case -228518075: goto L_0x0888;
            case -213586509: goto L_0x087c;
            case -115582002: goto L_0x0870;
            case -112621464: goto L_0x0864;
            case -108522133: goto L_0x0858;
            case -107572034: goto L_0x084d;
            case -40534265: goto L_0x0841;
            case 65254746: goto L_0x0835;
            case 141040782: goto L_0x0829;
            case 202550149: goto L_0x081d;
            case 309993049: goto L_0x0811;
            case 309995634: goto L_0x0805;
            case 309995749: goto L_0x07f9;
            case 320532812: goto L_0x07ed;
            case 328933854: goto L_0x07e1;
            case 331340546: goto L_0x07d5;
            case 342406591: goto L_0x07c9;
            case 344816990: goto L_0x07bd;
            case 346878138: goto L_0x07b1;
            case 350376871: goto L_0x07a5;
            case 608430149: goto L_0x0799;
            case 615714517: goto L_0x078e;
            case 715508879: goto L_0x0782;
            case 728985323: goto L_0x0776;
            case 731046471: goto L_0x076a;
            case 734545204: goto L_0x075e;
            case 802032552: goto L_0x0752;
            case 991498806: goto L_0x0746;
            case 1007364121: goto L_0x073a;
            case 1019850010: goto L_0x072e;
            case 1019917311: goto L_0x0722;
            case 1019926225: goto L_0x0716;
            case 1020207774: goto L_0x070a;
            case 1020243252: goto L_0x06fe;
            case 1020317708: goto L_0x06f2;
            case 1060282259: goto L_0x06e6;
            case 1060349560: goto L_0x06da;
            case 1060358474: goto L_0x06ce;
            case 1060640023: goto L_0x06c2;
            case 1060675501: goto L_0x06b6;
            case 1060749957: goto L_0x06ab;
            case 1073049781: goto L_0x069f;
            case 1078101399: goto L_0x0693;
            case 1110103437: goto L_0x0687;
            case 1160762272: goto L_0x067b;
            case 1172918249: goto L_0x066f;
            case 1234591620: goto L_0x0663;
            case 1281128640: goto L_0x0657;
            case 1281131225: goto L_0x064b;
            case 1281131340: goto L_0x063f;
            case 1310789062: goto L_0x0634;
            case 1333118583: goto L_0x0628;
            case 1361447897: goto L_0x061c;
            case 1498266155: goto L_0x0610;
            case 1533804208: goto L_0x0604;
            case 1540131626: goto L_0x05f8;
            case 1547988151: goto L_0x05ec;
            case 1561464595: goto L_0x05e0;
            case 1563525743: goto L_0x05d4;
            case 1567024476: goto L_0x05c8;
            case 1810705077: goto L_0x05bc;
            case 1815177512: goto L_0x05b0;
            case 1954774321: goto L_0x05a4;
            case 1963241394: goto L_0x0598;
            case 2014789757: goto L_0x058c;
            case 2022049433: goto L_0x0580;
            case 2034984710: goto L_0x0574;
            case 2048733346: goto L_0x0568;
            case 2099392181: goto L_0x055c;
            case 2140162142: goto L_0x0550;
            default: goto L_0x054e;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x0556, code lost:
        if (r14.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x0558, code lost:
        r4 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x0562, code lost:
        if (r14.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x0564, code lost:
        r4 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x056e, code lost:
        if (r14.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x0570, code lost:
        r4 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:351:0x057a, code lost:
        if (r14.equals("CHANNEL_MESSAGE_PLAYLIST") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x057c, code lost:
        r4 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x0586, code lost:
        if (r14.equals("PINNED_CONTACT") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x0588, code lost:
        r4 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x0592, code lost:
        if (r14.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x0594, code lost:
        r4 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x059e, code lost:
        if (r14.equals("LOCKED_MESSAGE") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x05a0, code lost:
        r4 = 'j';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x05aa, code lost:
        if (r14.equals("CHAT_MESSAGE_PLAYLIST") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x05ac, code lost:
        r4 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x05b6, code lost:
        if (r14.equals("CHANNEL_MESSAGES") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x05b8, code lost:
        r4 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x05c2, code lost:
        if (r14.equals("MESSAGE_INVOICE") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x05c4, code lost:
        r4 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x05ce, code lost:
        if (r14.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x05d0, code lost:
        r4 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:375:0x05da, code lost:
        if (r14.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x05dc, code lost:
        r4 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x05e6, code lost:
        if (r14.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x05e8, code lost:
        r4 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:381:0x05f2, code lost:
        if (r14.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x05f4, code lost:
        r4 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x05fe, code lost:
        if (r14.equals("MESSAGE_PLAYLIST") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x0600, code lost:
        r4 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x060a, code lost:
        if (r14.equals("MESSAGE_VIDEOS") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x060c, code lost:
        r4 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x0616, code lost:
        if (r14.equals("PHONE_CALL_MISSED") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x0618, code lost:
        r4 = 'o';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x0622, code lost:
        if (r14.equals("MESSAGE_PHOTOS") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x0624, code lost:
        r4 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x062e, code lost:
        if (r14.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x0630, code lost:
        r4 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x063a, code lost:
        if (r14.equals("MESSAGE_NOTEXT") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:400:0x063c, code lost:
        r4 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x0645, code lost:
        if (r14.equals("MESSAGE_GIF") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0647, code lost:
        r4 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x0651, code lost:
        if (r14.equals("MESSAGE_GEO") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x0653, code lost:
        r4 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x065d, code lost:
        if (r14.equals("MESSAGE_DOC") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x065f, code lost:
        r4 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x0669, code lost:
        if (r14.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x066b, code lost:
        r4 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x0675, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x0677, code lost:
        r4 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x0681, code lost:
        if (r14.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x0683, code lost:
        r4 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x068d, code lost:
        if (r14.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x068f, code lost:
        r4 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x0699, code lost:
        if (r14.equals("CHAT_TITLE_EDITED") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x069b, code lost:
        r4 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:426:0x06a5, code lost:
        if (r14.equals("PINNED_NOTEXT") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x06a7, code lost:
        r4 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x06b1, code lost:
        if (r14.equals("MESSAGE_TEXT") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x06b3, code lost:
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:432:0x06bc, code lost:
        if (r14.equals("MESSAGE_QUIZ") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x06be, code lost:
        r4 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x06c8, code lost:
        if (r14.equals("MESSAGE_POLL") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x06ca, code lost:
        r4 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x06d4, code lost:
        if (r14.equals("MESSAGE_GAME") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x06d6, code lost:
        r4 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x06e0, code lost:
        if (r14.equals("MESSAGE_FWDS") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x06e2, code lost:
        r4 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:444:0x06ec, code lost:
        if (r14.equals("MESSAGE_DOCS") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x06ee, code lost:
        r4 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x06f8, code lost:
        if (r14.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x06fa, code lost:
        r4 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x0704, code lost:
        if (r14.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x0706, code lost:
        r4 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x0710, code lost:
        if (r14.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x0712, code lost:
        r4 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:456:0x071c, code lost:
        if (r14.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x071e, code lost:
        r4 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x0728, code lost:
        if (r14.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:460:0x072a, code lost:
        r4 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x0734, code lost:
        if (r14.equals("CHAT_MESSAGE_DOCS") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x0736, code lost:
        r4 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x0740, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x0742, code lost:
        r4 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x074c, code lost:
        if (r14.equals("PINNED_GEOLIVE") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x074e, code lost:
        r4 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x0758, code lost:
        if (r14.equals("MESSAGE_CONTACT") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:472:0x075a, code lost:
        r4 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x0764, code lost:
        if (r14.equals("PINNED_VIDEO") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0766, code lost:
        r4 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x0770, code lost:
        if (r14.equals("PINNED_ROUND") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x0772, code lost:
        r4 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:480:0x077c, code lost:
        if (r14.equals("PINNED_PHOTO") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x077e, code lost:
        r4 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x0788, code lost:
        if (r14.equals("PINNED_AUDIO") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x078a, code lost:
        r4 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:486:0x0794, code lost:
        if (r14.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x0796, code lost:
        r4 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x079f, code lost:
        if (r14.equals("CHAT_VOICECHAT_INVITE_YOU") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x07a1, code lost:
        r4 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x07ab, code lost:
        if (r14.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x07ad, code lost:
        r4 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x07b7, code lost:
        if (r14.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x07b9, code lost:
        r4 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x07c3, code lost:
        if (r14.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x07c5, code lost:
        r4 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x07cf, code lost:
        if (r14.equals("CHAT_VOICECHAT_END") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x07d1, code lost:
        r4 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x07db, code lost:
        if (r14.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x07dd, code lost:
        r4 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x07e7, code lost:
        if (r14.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x07e9, code lost:
        r4 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x07f3, code lost:
        if (r14.equals("MESSAGES") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x07f5, code lost:
        r4 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x07ff, code lost:
        if (r14.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x0801, code lost:
        r4 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x080b, code lost:
        if (r14.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x080d, code lost:
        r4 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:519:0x0817, code lost:
        if (r14.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0819, code lost:
        r4 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x0823, code lost:
        if (r14.equals("CHAT_VOICECHAT_INVITE") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x0825, code lost:
        r4 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x082f, code lost:
        if (r14.equals("CHAT_LEFT") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x0831, code lost:
        r4 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x083b, code lost:
        if (r14.equals("CHAT_ADD_YOU") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x083d, code lost:
        r4 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0847, code lost:
        if (r14.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x0849, code lost:
        r4 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x0853, code lost:
        if (r14.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x0855, code lost:
        r4 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x085e, code lost:
        if (r14.equals("AUTH_REGION") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:538:0x0860, code lost:
        r4 = 'i';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:540:0x086a, code lost:
        if (r14.equals("CONTACT_JOINED") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x086c, code lost:
        r4 = 'g';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x0876, code lost:
        if (r14.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x0878, code lost:
        r4 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x0882, code lost:
        if (r14.equals("ENCRYPTION_REQUEST") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x0884, code lost:
        r4 = 'k';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x088e, code lost:
        if (r14.equals("MESSAGE_GEOLIVE") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x0890, code lost:
        r4 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:552:0x089a, code lost:
        if (r14.equals("CHAT_DELETE_YOU") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x089c, code lost:
        r4 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x08a6, code lost:
        if (r14.equals("AUTH_UNKNOWN") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x08a8, code lost:
        r4 = 'h';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x08b2, code lost:
        if (r14.equals("PINNED_GIF") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x08b4, code lost:
        r4 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x08be, code lost:
        if (r14.equals("PINNED_GEO") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x08c0, code lost:
        r4 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x08ca, code lost:
        if (r14.equals("PINNED_DOC") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x08cc, code lost:
        r4 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x08d6, code lost:
        if (r14.equals("PINNED_GAME_SCORE") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x08d8, code lost:
        r4 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x08e2, code lost:
        if (r14.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x08e4, code lost:
        r4 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x08ee, code lost:
        if (r14.equals("PHONE_CALL_REQUEST") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x08f0, code lost:
        r4 = 'm';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:576:0x08fa, code lost:
        if (r14.equals("PINNED_STICKER") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x08fc, code lost:
        r4 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x0906, code lost:
        if (r14.equals("PINNED_TEXT") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x0908, code lost:
        r4 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:582:0x0912, code lost:
        if (r14.equals("PINNED_QUIZ") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x0914, code lost:
        r4 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x091e, code lost:
        if (r14.equals("PINNED_POLL") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x0920, code lost:
        r4 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:588:0x092a, code lost:
        if (r14.equals("PINNED_GAME") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x092c, code lost:
        r4 = 'b';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x0936, code lost:
        if (r14.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x0938, code lost:
        r4 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x0942, code lost:
        if (r14.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x0944, code lost:
        r4 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x094d, code lost:
        if (r14.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x094f, code lost:
        r4 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:600:0x0958, code lost:
        if (r14.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x095a, code lost:
        r4 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0964, code lost:
        if (r14.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x0966, code lost:
        r4 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x0970, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x0972, code lost:
        r4 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x097c, code lost:
        if (r14.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x097e, code lost:
        r4 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:612:0x0988, code lost:
        if (r14.equals("CHANNEL_MESSAGE_DOCS") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x098a, code lost:
        r4 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x0994, code lost:
        if (r14.equals("PINNED_INVOICE") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x0996, code lost:
        r4 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:618:0x09a0, code lost:
        if (r14.equals("CHAT_RETURNED") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x09a2, code lost:
        r4 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x09ac, code lost:
        if (r14.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x09ae, code lost:
        r4 = 'f';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x09b8, code lost:
        if (r14.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:625:0x09ba, code lost:
        r4 = 'l';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:627:0x09c4, code lost:
        if (r14.equals("MESSAGE_VIDEO") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x09c6, code lost:
        r4 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x09cf, code lost:
        if (r14.equals("MESSAGE_ROUND") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x09d1, code lost:
        r4 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x09db, code lost:
        if (r14.equals("MESSAGE_PHOTO") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x09dd, code lost:
        r4 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x09e6, code lost:
        if (r14.equals("MESSAGE_MUTED") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x09e8, code lost:
        r4 = 'n';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x09f2, code lost:
        if (r14.equals("MESSAGE_AUDIO") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x09f4, code lost:
        r4 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x09fe, code lost:
        if (r14.equals("CHAT_MESSAGES") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0a00, code lost:
        r4 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x0a0a, code lost:
        if (r14.equals("CHAT_VOICECHAT_START") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x0a0c, code lost:
        r4 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x0a16, code lost:
        if (r14.equals("CHAT_JOINED") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0a18, code lost:
        r4 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x0a22, code lost:
        if (r14.equals("CHAT_ADD_MEMBER") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0a24, code lost:
        r4 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0a2d, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0a2f, code lost:
        r4 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0a38, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0a3a, code lost:
        r4 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0a43, code lost:
        if (r14.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x0a45, code lost:
        r4 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x0a4e, code lost:
        if (r14.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0a50, code lost:
        r4 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0a59, code lost:
        if (r14.equals("MESSAGE_STICKER") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x0a5b, code lost:
        r4 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0a64, code lost:
        if (r14.equals("CHAT_CREATED") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x0a66, code lost:
        r4 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0a6f, code lost:
        if (r14.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:673:0x0a71, code lost:
        r4 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0a7a, code lost:
        if (r14.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0a7c, code lost:
        r4 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x0a7f, code lost:
        r4 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0a80, code lost:
        r41 = r11;
        r42 = r9;
        r43 = r1;
        r45 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:679:0x0a98, code lost:
        switch(r4) {
            case 0: goto L_0x1b85;
            case 1: goto L_0x1b85;
            case 2: goto L_0x1b63;
            case 3: goto L_0x1b46;
            case 4: goto L_0x1b29;
            case 5: goto L_0x1b0c;
            case 6: goto L_0x1aee;
            case 7: goto L_0x1ad5;
            case 8: goto L_0x1ab7;
            case 9: goto L_0x1a99;
            case 10: goto L_0x1a3e;
            case 11: goto L_0x1a20;
            case 12: goto L_0x19fd;
            case 13: goto L_0x19da;
            case 14: goto L_0x19b7;
            case 15: goto L_0x1999;
            case 16: goto L_0x197b;
            case 17: goto L_0x195d;
            case 18: goto L_0x193a;
            case 19: goto L_0x191b;
            case 20: goto L_0x191b;
            case 21: goto L_0x18f8;
            case 22: goto L_0x18d0;
            case 23: goto L_0x18ac;
            case 24: goto L_0x1889;
            case 25: goto L_0x1866;
            case 26: goto L_0x1841;
            case 27: goto L_0x1829;
            case 28: goto L_0x180b;
            case 29: goto L_0x17ed;
            case 30: goto L_0x17cf;
            case 31: goto L_0x17b1;
            case 32: goto L_0x1793;
            case 33: goto L_0x1738;
            case 34: goto L_0x171a;
            case 35: goto L_0x16f7;
            case 36: goto L_0x16d4;
            case 37: goto L_0x16b1;
            case 38: goto L_0x1693;
            case 39: goto L_0x1675;
            case 40: goto L_0x1657;
            case 41: goto L_0x1639;
            case 42: goto L_0x160f;
            case 43: goto L_0x15eb;
            case 44: goto L_0x15c7;
            case 45: goto L_0x15a3;
            case 46: goto L_0x157d;
            case 47: goto L_0x1568;
            case 48: goto L_0x1547;
            case 49: goto L_0x1524;
            case 50: goto L_0x1501;
            case 51: goto L_0x14de;
            case 52: goto L_0x14bb;
            case 53: goto L_0x1498;
            case 54: goto L_0x141f;
            case 55: goto L_0x13fc;
            case 56: goto L_0x13d4;
            case 57: goto L_0x13ac;
            case 58: goto L_0x1384;
            case 59: goto L_0x1361;
            case 60: goto L_0x133e;
            case 61: goto L_0x131b;
            case 62: goto L_0x12f3;
            case 63: goto L_0x12cf;
            case 64: goto L_0x12a7;
            case 65: goto L_0x128d;
            case 66: goto L_0x128d;
            case 67: goto L_0x1273;
            case 68: goto L_0x1259;
            case 69: goto L_0x123a;
            case 70: goto L_0x1220;
            case 71: goto L_0x1201;
            case 72: goto L_0x11e7;
            case 73: goto L_0x11cd;
            case 74: goto L_0x11b3;
            case 75: goto L_0x1199;
            case 76: goto L_0x117f;
            case 77: goto L_0x1165;
            case 78: goto L_0x114b;
            case 79: goto L_0x111e;
            case 80: goto L_0x10f5;
            case 81: goto L_0x10cc;
            case 82: goto L_0x10a3;
            case 83: goto L_0x1078;
            case 84: goto L_0x105e;
            case 85: goto L_0x1007;
            case 86: goto L_0x0fba;
            case 87: goto L_0x0f6d;
            case 88: goto L_0x0var_;
            case 89: goto L_0x0ed3;
            case 90: goto L_0x0e86;
            case 91: goto L_0x0dcd;
            case 92: goto L_0x0d80;
            case 93: goto L_0x0d29;
            case 94: goto L_0x0cd2;
            case 95: goto L_0x0CLASSNAME;
            case 96: goto L_0x0CLASSNAME;
            case 97: goto L_0x0bec;
            case 98: goto L_0x0ba1;
            case 99: goto L_0x0b56;
            case 100: goto L_0x0b0b;
            case 101: goto L_0x0ac0;
            case 102: goto L_0x0aa5;
            case 103: goto L_0x0aa1;
            case 104: goto L_0x0aa1;
            case 105: goto L_0x0aa1;
            case 106: goto L_0x0aa1;
            case 107: goto L_0x0aa1;
            case 108: goto L_0x0aa1;
            case 109: goto L_0x0aa1;
            case 110: goto L_0x0aa1;
            case 111: goto L_0x0aa1;
            default: goto L_0x0a9b;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0a9b, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0aa1, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:?, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
        r4 = r22;
        r17 = null;
        r22 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x0ac4, code lost:
        if (r12 <= 0) goto L_0x0ade;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0ac6, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0ade, code lost:
        if (r8 == false) goto L_0x0af8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x0ae0, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0af8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0b0f, code lost:
        if (r12 <= 0) goto L_0x0b29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0b11, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0b29, code lost:
        if (r8 == false) goto L_0x0b43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0b2b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0b43, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0b5a, code lost:
        if (r12 <= 0) goto L_0x0b74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0b5c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0b74, code lost:
        if (r8 == false) goto L_0x0b8e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0b76, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0b8e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0ba5, code lost:
        if (r12 <= 0) goto L_0x0bbf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0ba7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0bbf, code lost:
        if (r8 == false) goto L_0x0bd9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0bc1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0bd9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0bf0, code lost:
        if (r12 <= 0) goto L_0x0c0a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0bf2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0c0a, code lost:
        if (r8 == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0c0c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0CLASSNAME, code lost:
        if (r12 <= 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0c3b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0CLASSNAME, code lost:
        if (r8 == false) goto L_0x0c6b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0c6b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0c7c, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0CLASSNAME, code lost:
        if (r12 <= 0) goto L_0x0c9d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:727:0x0c9d, code lost:
        if (r8 == false) goto L_0x0cbb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0c9f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r3[0], r3[2], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0cbb, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0cd2, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x0cd8, code lost:
        if (r12 <= 0) goto L_0x0cf2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0cda, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0cf2, code lost:
        if (r8 == false) goto L_0x0d11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0cf4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r3[0], r3[2], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x0d11, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0d29, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0d2f, code lost:
        if (r12 <= 0) goto L_0x0d49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0d31, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x0d49, code lost:
        if (r8 == false) goto L_0x0d68;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0d4b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r3[0], r3[2], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x0d68, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0d80, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x0d86, code lost:
        if (r12 <= 0) goto L_0x0da0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x0d88, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x0da0, code lost:
        if (r8 == false) goto L_0x0dba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x0da2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x0dba, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x0dcd, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0dd3, code lost:
        if (r12 <= 0) goto L_0x0e0c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x0dd7, code lost:
        if (r3.length <= 1) goto L_0x0df9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:0x0ddf, code lost:
        if (android.text.TextUtils.isEmpty(r3[1]) != false) goto L_0x0df9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x0de1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x0df9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x0e0c, code lost:
        if (r8 == false) goto L_0x0e4f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x0e10, code lost:
        if (r3.length <= 2) goto L_0x0e37;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x0e18, code lost:
        if (android.text.TextUtils.isEmpty(r3[2]) != false) goto L_0x0e37;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x0e1a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r3[0], r3[2], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x0e37, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x0e51, code lost:
        if (r3.length <= 1) goto L_0x0e73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x0e59, code lost:
        if (android.text.TextUtils.isEmpty(r3[1]) != false) goto L_0x0e73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x0e5b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x0e73, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x0e86, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x0e8c, code lost:
        if (r12 <= 0) goto L_0x0ea6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x0e8e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x0ea6, code lost:
        if (r8 == false) goto L_0x0ec0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x0ea8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x0ec0, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x0ed3, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x0ed9, code lost:
        if (r12 <= 0) goto L_0x0ef3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x0edb, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x0ef3, code lost:
        if (r8 == false) goto L_0x0f0d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x0ef5, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x0f0d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x0var_, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x0var_, code lost:
        if (r12 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x0var_, code lost:
        if (r8 == false) goto L_0x0f5a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x0f5a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x0f6d, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x0var_, code lost:
        if (r12 <= 0) goto L_0x0f8d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x0f8d, code lost:
        if (r8 == false) goto L_0x0fa7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x0f8f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x0fa7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x0fba, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x0fc0, code lost:
        if (r12 <= 0) goto L_0x0fda;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x0fc2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x0fda, code lost:
        if (r8 == false) goto L_0x0ff4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x0fdc, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x0ff4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x1007, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x100d, code lost:
        if (r12 <= 0) goto L_0x1027;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x100f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x1027, code lost:
        if (r8 == false) goto L_0x1046;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x1029, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r3[0], r3[1], r3[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x1046, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x105e, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x1078, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r3[0], r3[1], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r3[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x10a3, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r3[0], r3[1], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r3[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x10cc, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r3[0], r3[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r3[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x10f5, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r3[0], r3[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r3[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x111e, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r3[0], r3[1], org.telegram.messenger.LocaleController.formatPluralString(r27, org.telegram.messenger.Utilities.parseInt(r3[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x114b, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x1165, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x117f, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x1199, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x11b3, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x11cd, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x11e7, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupEndedCall", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x1201, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r3[0], r3[1], r3[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x1220, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x123a, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r3[0], r3[1], r3[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x1259, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x1273, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x128d, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x12a7, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r3[0], r3[1], r3[2]);
        r2 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x12cf, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r3[0], r3[1], r3[2], r3[3]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x12f3, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r3[0], r3[1], r3[2]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x131b, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x133e, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x1361, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x1384, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r3[0], r3[1], r3[2]);
        r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x13ac, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r3[0], r3[1], r3[2]);
        r2 = org.telegram.messenger.LocaleController.getString("PollQuiz", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x13d4, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r3[0], r3[1], r3[2]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x13fc, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x141f, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x1423, code lost:
        if (r3.length <= 2) goto L_0x1465;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x142b, code lost:
        if (android.text.TextUtils.isEmpty(r3[2]) != false) goto L_0x1465;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x142d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r3[0], r3[1], r3[2]);
        r2 = r3[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x1465, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r3[0], r3[1]);
        r2 = r3[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x1498, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x14bb, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x14de, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x1501, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x1524, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x1547, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r3[0], r3[1], r3[2]);
        r2 = r3[2];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x1568, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x157d, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x15a3, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x15c7, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x15eb, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:851:0x160f, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x1639, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x1657, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x1675, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x1693, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:856:0x16b1, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x16d4, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x16f7, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x171a, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x1738, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x173c, code lost:
        if (r3.length <= 1) goto L_0x1779;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:863:0x1744, code lost:
        if (android.text.TextUtils.isEmpty(r3[1]) != false) goto L_0x1779;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:864:0x1746, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r3[0], r3[1]);
        r2 = r3[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:865:0x1779, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:866:0x1793, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x17b1, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x17cf, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x17ed, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x180b, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:871:0x1829, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x183c, code lost:
        r22 = r39;
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x1841, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x1866, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x1889, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x18ac, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x18d0, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString(r27, org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x18f8, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x191b, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r3[0], r3[1], r3[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x193a, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x195d, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x197b, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x1999, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x19b7, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:0x19da, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x19fd, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x1a20, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x1a3e, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x1a42, code lost:
        if (r3.length <= 1) goto L_0x1a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x1a4a, code lost:
        if (android.text.TextUtils.isEmpty(r3[1]) != false) goto L_0x1a7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x1a4c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r3[0], r3[1]);
        r2 = r3[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x1a7f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x1a99, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x1ab7, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x1ad5, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:897:0x1aea, code lost:
        r22 = r39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x1aee, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:899:0x1b0c, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:0x1b29, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x1b46, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x1b63, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:0x1b7f, code lost:
        r17 = r2;
        r22 = r39;
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:0x1b85, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r3[0], r3[1]);
        r2 = r3[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x1ba0, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1bb7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:906:0x1ba2, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:0x1bb7, code lost:
        r22 = r39;
        r1 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x1bba, code lost:
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:909:0x1bbb, code lost:
        r17 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:910:0x1bbd, code lost:
        if (r1 == null) goto L_0x1c9b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:?, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_message();
        r3.id = r10;
        r3.random_id = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:913:0x1bca, code lost:
        if (r17 == null) goto L_0x1bcf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:0x1bcc, code lost:
        r5 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:0x1bcf, code lost:
        r5 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x1bd0, code lost:
        r3.message = r5;
        r3.date = (int) (r51 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:917:0x1bd9, code lost:
        if (r38 == false) goto L_0x1be2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:?, code lost:
        r3.action = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:920:0x1be2, code lost:
        if (r37 == false) goto L_0x1beb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x1be4, code lost:
        r3.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:?, code lost:
        r3.dialog_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x1bf1, code lost:
        if (r45 == 0) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:?, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r3.peer_id = r5;
        r5.channel_id = r45;
        r12 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:0x1CLASSNAME, code lost:
        if (r25 == 0) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x1CLASSNAME, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r3.peer_id = r5;
        r12 = r25;
        r5.chat_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x1CLASSNAME, code lost:
        r12 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:?, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r3.peer_id = r5;
        r5.user_id = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x1CLASSNAME, code lost:
        r3.flags |= 256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:0x1c2a, code lost:
        if (r43 == 0) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:?, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r3.from_id = r5;
        r5.chat_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:0x1c3a, code lost:
        if (r32 == 0) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:0x1c3c, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r3.from_id = r5;
        r5.channel_id = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:943:0x1c4c, code lost:
        if (r35 == 0) goto L_0x1c5a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:944:0x1c4e, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r3.from_id = r5;
        r5.user_id = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:946:?, code lost:
        r3.from_id = r3.peer_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x1c5e, code lost:
        if (r29 != false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x1CLASSNAME, code lost:
        if (r38 == false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x1CLASSNAME, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x1CLASSNAME, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x1CLASSNAME, code lost:
        r3.mentioned = r5;
        r3.silent = r34;
        r3.from_scheduled = r4;
        r18 = new org.telegram.messenger.MessageObject(r28, r3, r1, r22, r42, r2, r41, r37, r40);
        r1 = new java.util.ArrayList();
        r1.add(r18);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x1CLASSNAME, code lost:
        r3 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:0x1CLASSNAME, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:?, code lost:
        org.telegram.messenger.NotificationsController.getInstance(r28).processNewMessages(r1, true, true, r3.countDownLatch);
        r8 = false;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:0x1c9b, code lost:
        r3 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:957:0x1c9e, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:0x1c9f, code lost:
        r3 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x1ca2, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x1ca3, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:0x1ca5, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:962:0x1ca6, code lost:
        r8 = true;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:0x1ca7, code lost:
        if (r8 == false) goto L_0x1cae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:964:0x1ca9, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:965:0x1cae, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r28);
        org.telegram.tgnet.ConnectionsManager.getInstance(r28).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:966:0x1cba, code lost:
        r0 = th;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:967:0x1cbc, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:968:0x1cbd, code lost:
        r3 = r1;
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:969:0x1cc1, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:0x1cc2, code lost:
        r3 = r1;
        r14 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:971:0x1cc4, code lost:
        r1 = r0;
        r7 = r16;
        r4 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:972:0x1ccb, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:0x1ccc, code lost:
        r3 = r1;
        r28 = r4;
        r14 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:974:0x1cd2, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:975:0x1cd3, code lost:
        r3 = r1;
        r28 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:976:0x1cd8, code lost:
        r3 = r1;
        r28 = r4;
        r16 = r7;
        r14 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:979:0x1ce2, code lost:
        r4 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:981:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1(r4));
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:982:0x1cef, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:983:0x1cf0, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:984:0x1cf1, code lost:
        r4 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:985:0x1cf5, code lost:
        r16 = r7;
        r14 = r9;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0(r4));
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:986:0x1d06, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:987:0x1d07, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:988:0x1d50, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:989:0x1d51, code lost:
        r3 = r1;
        r16 = r7;
        r14 = r9;
        r1 = r11.getInt("dc");
        r2 = r11.getString("addr").split(":");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:990:0x1d69, code lost:
        if (r2.length == 2) goto L_0x1d71;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x1d6b, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:992:0x1d70, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:993:0x1d71, code lost:
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).applyDatacenterAddress(r1, r2[0], java.lang.Integer.parseInt(r2[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:994:0x1d8e, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:995:0x1d8f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:996:0x1d90, code lost:
        r1 = r0;
        r7 = r16;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1009:0x1db6  */
    /* JADX WARNING: Removed duplicated region for block: B:1010:0x1dc6  */
    /* JADX WARNING: Removed duplicated region for block: B:1013:0x1dcd  */
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
            java.lang.Object r5 = r2.get(r5)     // Catch:{ all -> 0x1dad }
            boolean r6 = r5 instanceof java.lang.String     // Catch:{ all -> 0x1dad }
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
            goto L_0x1db4
        L_0x002d:
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ all -> 0x1dad }
            r6 = 8
            byte[] r5 = android.util.Base64.decode(r5, r6)     // Catch:{ all -> 0x1dad }
            org.telegram.tgnet.NativeByteBuffer r7 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x1dad }
            int r8 = r5.length     // Catch:{ all -> 0x1dad }
            r7.<init>((int) r8)     // Catch:{ all -> 0x1dad }
            r7.writeBytes((byte[]) r5)     // Catch:{ all -> 0x1dad }
            r8 = 0
            r7.position(r8)     // Catch:{ all -> 0x1dad }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1dad }
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
            byte[] r9 = new byte[r6]     // Catch:{ all -> 0x1dad }
            r10 = 1
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1dad }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1dad }
            boolean r11 = java.util.Arrays.equals(r11, r9)     // Catch:{ all -> 0x1dad }
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
            byte[] r9 = new byte[r9]     // Catch:{ all -> 0x1dad }
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1dad }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1dad }
            org.telegram.messenger.MessageKeyData r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13)     // Catch:{ all -> 0x1dad }
            java.nio.ByteBuffer r14 = r7.buffer     // Catch:{ all -> 0x1dad }
            byte[] r15 = r11.aesKey     // Catch:{ all -> 0x1dad }
            byte[] r11 = r11.aesIv     // Catch:{ all -> 0x1dad }
            r17 = 0
            r18 = 0
            r19 = 24
            int r5 = r5.length     // Catch:{ all -> 0x1dad }
            int r20 = r5 + -24
            r16 = r11
            org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x1dad }
            byte[] r21 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1dad }
            r22 = 96
            r23 = 32
            java.nio.ByteBuffer r5 = r7.buffer     // Catch:{ all -> 0x1dad }
            r25 = 24
            int r26 = r5.limit()     // Catch:{ all -> 0x1dad }
            r24 = r5
            byte[] r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26)     // Catch:{ all -> 0x1dad }
            boolean r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6)     // Catch:{ all -> 0x1dad }
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
            int r5 = r7.readInt32(r10)     // Catch:{ all -> 0x1dad }
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x1dad }
            r7.readBytes(r5, r10)     // Catch:{ all -> 0x1dad }
            java.lang.String r7 = new java.lang.String     // Catch:{ all -> 0x1dad }
            r7.<init>(r5)     // Catch:{ all -> 0x1dad }
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ all -> 0x1da5 }
            r5.<init>(r7)     // Catch:{ all -> 0x1da5 }
            java.lang.String r9 = "loc_key"
            boolean r9 = r5.has(r9)     // Catch:{ all -> 0x1da5 }
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
            java.lang.Object r11 = r5.get(r11)     // Catch:{ all -> 0x1d9c }
            boolean r11 = r11 instanceof org.json.JSONObject     // Catch:{ all -> 0x1d9c }
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
            org.json.JSONObject r11 = new org.json.JSONObject     // Catch:{ all -> 0x1d9c }
            r11.<init>()     // Catch:{ all -> 0x1d9c }
        L_0x012f:
            java.lang.String r14 = "user_id"
            boolean r14 = r5.has(r14)     // Catch:{ all -> 0x1d9c }
            if (r14 == 0) goto L_0x0140
            java.lang.String r14 = "user_id"
            java.lang.Object r14 = r5.get(r14)     // Catch:{ all -> 0x0123 }
            goto L_0x0141
        L_0x0140:
            r14 = 0
        L_0x0141:
            if (r14 != 0) goto L_0x014e
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x0123 }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x0123 }
            long r14 = r14.getClientUserId()     // Catch:{ all -> 0x0123 }
            goto L_0x017e
        L_0x014e:
            boolean r15 = r14 instanceof java.lang.Long     // Catch:{ all -> 0x1d9c }
            if (r15 == 0) goto L_0x0159
            java.lang.Long r14 = (java.lang.Long) r14     // Catch:{ all -> 0x0123 }
            long r14 = r14.longValue()     // Catch:{ all -> 0x0123 }
            goto L_0x017e
        L_0x0159:
            boolean r15 = r14 instanceof java.lang.Integer     // Catch:{ all -> 0x1d9c }
            if (r15 == 0) goto L_0x0165
            java.lang.Integer r14 = (java.lang.Integer) r14     // Catch:{ all -> 0x0123 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0123 }
        L_0x0163:
            long r14 = (long) r14
            goto L_0x017e
        L_0x0165:
            boolean r15 = r14 instanceof java.lang.String     // Catch:{ all -> 0x1d9c }
            if (r15 == 0) goto L_0x0174
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ all -> 0x0123 }
            java.lang.Integer r14 = org.telegram.messenger.Utilities.parseInt(r14)     // Catch:{ all -> 0x0123 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0123 }
            goto L_0x0163
        L_0x0174:
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1d9c }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x1d9c }
            long r14 = r14.getClientUserId()     // Catch:{ all -> 0x1d9c }
        L_0x017e:
            int r16 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1d9c }
            r4 = 0
        L_0x0181:
            if (r4 >= r12) goto L_0x0194
            org.telegram.messenger.UserConfig r18 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x0123 }
            long r18 = r18.getClientUserId()     // Catch:{ all -> 0x0123 }
            int r20 = (r18 > r14 ? 1 : (r18 == r14 ? 0 : -1))
            if (r20 != 0) goto L_0x0191
            r14 = 1
            goto L_0x0197
        L_0x0191:
            int r4 = r4 + 1
            goto L_0x0181
        L_0x0194:
            r4 = r16
            r14 = 0
        L_0x0197:
            if (r14 != 0) goto L_0x01a8
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0123 }
            if (r2 == 0) goto L_0x01a2
            java.lang.String r2 = "GCM ACCOUNT NOT FOUND"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x0123 }
        L_0x01a2:
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x0123 }
            r2.countDown()     // Catch:{ all -> 0x0123 }
            return
        L_0x01a8:
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x1d94 }
            boolean r14 = r14.isClientActivated()     // Catch:{ all -> 0x1d94 }
            if (r14 != 0) goto L_0x01c7
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
        L_0x01c2:
            r3 = r1
            r14 = r9
        L_0x01c4:
            r2 = -1
            goto L_0x002a
        L_0x01c7:
            java.lang.String r14 = "google.sent_time"
            r2.get(r14)     // Catch:{ all -> 0x1d94 }
            int r2 = r9.hashCode()     // Catch:{ all -> 0x1d94 }
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
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01c1 }
            if (r2 == 0) goto L_0x01fc
            r2 = 3
            goto L_0x01fd
        L_0x01de:
            java.lang.String r2 = "MESSAGE_ANNOUNCEMENT"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01c1 }
            if (r2 == 0) goto L_0x01fc
            r2 = 1
            goto L_0x01fd
        L_0x01e8:
            java.lang.String r2 = "DC_UPDATE"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01c1 }
            if (r2 == 0) goto L_0x01fc
            r2 = 0
            goto L_0x01fd
        L_0x01f2:
            java.lang.String r2 = "SESSION_REVOKE"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01c1 }
            if (r2 == 0) goto L_0x01fc
            r2 = 2
            goto L_0x01fd
        L_0x01fc:
            r2 = -1
        L_0x01fd:
            if (r2 == 0) goto L_0x1d51
            if (r2 == r10) goto L_0x1d07
            if (r2 == r13) goto L_0x1cf5
            if (r2 == r12) goto L_0x1cd8
            java.lang.String r2 = "channel_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1cd2 }
            r14 = 0
            if (r2 == 0) goto L_0x0223
            java.lang.String r2 = "channel_id"
            r16 = r7
            long r6 = r11.getLong(r2)     // Catch:{ all -> 0x0219 }
            long r12 = -r6
            goto L_0x0227
        L_0x0219:
            r0 = move-exception
            r3 = r1
            r14 = r9
            r7 = r16
            goto L_0x01c4
        L_0x021f:
            r0 = move-exception
            r16 = r7
            goto L_0x01c2
        L_0x0223:
            r16 = r7
            r6 = r14
            r12 = r6
        L_0x0227:
            java.lang.String r2 = "from_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1ccb }
            if (r2 == 0) goto L_0x0239
            java.lang.String r2 = "from_id"
            long r12 = r11.getLong(r2)     // Catch:{ all -> 0x0219 }
            r28 = r4
            r3 = r12
            goto L_0x023c
        L_0x0239:
            r28 = r4
            r3 = r14
        L_0x023c:
            java.lang.String r2 = "chat_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1cc1 }
            if (r2 == 0) goto L_0x025f
            java.lang.String r2 = "chat_id"
            long r12 = r11.getLong(r2)     // Catch:{ all -> 0x0253 }
            r29 = r9
            long r8 = -r12
            r47 = r8
            r8 = r12
            r12 = r47
            goto L_0x0262
        L_0x0253:
            r0 = move-exception
            r29 = r9
        L_0x0256:
            r3 = r1
            r7 = r16
            r4 = r28
            r14 = r29
            goto L_0x01c4
        L_0x025f:
            r29 = r9
            r8 = r14
        L_0x0262:
            java.lang.String r2 = "encryption_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1cbc }
            if (r2 == 0) goto L_0x0278
            java.lang.String r2 = "encryption_id"
            int r2 = r11.getInt(r2)     // Catch:{ all -> 0x0276 }
            long r12 = (long) r2     // Catch:{ all -> 0x0276 }
            long r12 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r12)     // Catch:{ all -> 0x0276 }
            goto L_0x0278
        L_0x0276:
            r0 = move-exception
            goto L_0x0256
        L_0x0278:
            java.lang.String r2 = "schedule"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1cbc }
            if (r2 == 0) goto L_0x028a
            java.lang.String r2 = "schedule"
            int r2 = r11.getInt(r2)     // Catch:{ all -> 0x0276 }
            if (r2 != r10) goto L_0x028a
            r2 = 1
            goto L_0x028b
        L_0x028a:
            r2 = 0
        L_0x028b:
            int r20 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r20 != 0) goto L_0x02a8
            java.lang.String r10 = "ENCRYPTED_MESSAGE"
            r14 = r29
            boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x029c }
            if (r10 == 0) goto L_0x02aa
            long r12 = org.telegram.messenger.NotificationsController.globalSecretChatId     // Catch:{ all -> 0x029c }
            goto L_0x02aa
        L_0x029c:
            r0 = move-exception
            goto L_0x02a1
        L_0x029e:
            r0 = move-exception
            r14 = r29
        L_0x02a1:
            r3 = r1
            r7 = r16
            r4 = r28
            goto L_0x01c4
        L_0x02a8:
            r14 = r29
        L_0x02aa:
            r20 = 0
            int r10 = (r12 > r20 ? 1 : (r12 == r20 ? 0 : -1))
            if (r10 == 0) goto L_0x1ca5
            java.lang.String r10 = "READ_HISTORY"
            boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x1ca2 }
            java.lang.String r15 = " for dialogId = "
            if (r10 == 0) goto L_0x032c
            java.lang.String r2 = "max_id"
            int r2 = r11.getInt(r2)     // Catch:{ all -> 0x029c }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x029c }
            r5.<init>()     // Catch:{ all -> 0x029c }
            boolean r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x029c }
            if (r10 == 0) goto L_0x02e3
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x029c }
            r10.<init>()     // Catch:{ all -> 0x029c }
            java.lang.String r11 = "GCM received read notification max_id = "
            r10.append(r11)     // Catch:{ all -> 0x029c }
            r10.append(r2)     // Catch:{ all -> 0x029c }
            r10.append(r15)     // Catch:{ all -> 0x029c }
            r10.append(r12)     // Catch:{ all -> 0x029c }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x029c }
            org.telegram.messenger.FileLog.d(r10)     // Catch:{ all -> 0x029c }
        L_0x02e3:
            r10 = 0
            int r12 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r12 == 0) goto L_0x02f6
            org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox r3 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox     // Catch:{ all -> 0x029c }
            r3.<init>()     // Catch:{ all -> 0x029c }
            r3.channel_id = r6     // Catch:{ all -> 0x029c }
            r3.max_id = r2     // Catch:{ all -> 0x029c }
            r5.add(r3)     // Catch:{ all -> 0x029c }
            goto L_0x0319
        L_0x02f6:
            org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox r6 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox     // Catch:{ all -> 0x029c }
            r6.<init>()     // Catch:{ all -> 0x029c }
            r10 = 0
            int r7 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r7 == 0) goto L_0x030b
            org.telegram.tgnet.TLRPC$TL_peerUser r7 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x029c }
            r7.<init>()     // Catch:{ all -> 0x029c }
            r6.peer = r7     // Catch:{ all -> 0x029c }
            r7.user_id = r3     // Catch:{ all -> 0x029c }
            goto L_0x0314
        L_0x030b:
            org.telegram.tgnet.TLRPC$TL_peerChat r3 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x029c }
            r3.<init>()     // Catch:{ all -> 0x029c }
            r6.peer = r3     // Catch:{ all -> 0x029c }
            r3.chat_id = r8     // Catch:{ all -> 0x029c }
        L_0x0314:
            r6.max_id = r2     // Catch:{ all -> 0x029c }
            r5.add(r6)     // Catch:{ all -> 0x029c }
        L_0x0319:
            org.telegram.messenger.MessagesController r22 = org.telegram.messenger.MessagesController.getInstance(r28)     // Catch:{ all -> 0x029c }
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r23 = r5
            r22.processUpdateArray(r23, r24, r25, r26, r27)     // Catch:{ all -> 0x029c }
            goto L_0x1ca5
        L_0x032c:
            java.lang.String r10 = "MESSAGE_DELETED"
            boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x1ca2 }
            r22 = r2
            java.lang.String r2 = "messages"
            if (r10 == 0) goto L_0x03a2
            java.lang.String r2 = r11.getString(r2)     // Catch:{ all -> 0x029c }
            java.lang.String r3 = ","
            java.lang.String[] r2 = r2.split(r3)     // Catch:{ all -> 0x029c }
            androidx.collection.LongSparseArray r3 = new androidx.collection.LongSparseArray     // Catch:{ all -> 0x029c }
            r3.<init>()     // Catch:{ all -> 0x029c }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x029c }
            r4.<init>()     // Catch:{ all -> 0x029c }
            r8 = 0
        L_0x034d:
            int r5 = r2.length     // Catch:{ all -> 0x029c }
            if (r8 >= r5) goto L_0x035c
            r5 = r2[r8]     // Catch:{ all -> 0x029c }
            java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r5)     // Catch:{ all -> 0x029c }
            r4.add(r5)     // Catch:{ all -> 0x029c }
            int r8 = r8 + 1
            goto L_0x034d
        L_0x035c:
            long r8 = -r6
            r3.put(r8, r4)     // Catch:{ all -> 0x029c }
            org.telegram.messenger.NotificationsController r2 = org.telegram.messenger.NotificationsController.getInstance(r28)     // Catch:{ all -> 0x029c }
            r2.removeDeletedMessagesFromNotifications(r3)     // Catch:{ all -> 0x029c }
            org.telegram.messenger.MessagesController r20 = org.telegram.messenger.MessagesController.getInstance(r28)     // Catch:{ all -> 0x029c }
            r21 = r12
            r23 = r4
            r24 = r6
            r20.deleteMessagesByPush(r21, r23, r24)     // Catch:{ all -> 0x029c }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x029c }
            if (r2 == 0) goto L_0x1ca5
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x029c }
            r2.<init>()     // Catch:{ all -> 0x029c }
            java.lang.String r3 = "GCM received "
            r2.append(r3)     // Catch:{ all -> 0x029c }
            r2.append(r14)     // Catch:{ all -> 0x029c }
            r2.append(r15)     // Catch:{ all -> 0x029c }
            r2.append(r12)     // Catch:{ all -> 0x029c }
            java.lang.String r3 = " mids = "
            r2.append(r3)     // Catch:{ all -> 0x029c }
            java.lang.String r3 = ","
            java.lang.String r3 = android.text.TextUtils.join(r3, r4)     // Catch:{ all -> 0x029c }
            r2.append(r3)     // Catch:{ all -> 0x029c }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x029c }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x029c }
            goto L_0x1ca5
        L_0x03a2:
            boolean r10 = android.text.TextUtils.isEmpty(r14)     // Catch:{ all -> 0x1ca2 }
            if (r10 != 0) goto L_0x1ca5
            java.lang.String r10 = "msg_id"
            boolean r10 = r11.has(r10)     // Catch:{ all -> 0x1ca2 }
            if (r10 == 0) goto L_0x03b7
            java.lang.String r10 = "msg_id"
            int r10 = r11.getInt(r10)     // Catch:{ all -> 0x029c }
            goto L_0x03b8
        L_0x03b7:
            r10 = 0
        L_0x03b8:
            java.lang.String r1 = "random_id"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x1c9e }
            if (r1 == 0) goto L_0x03e0
            java.lang.String r1 = "random_id"
            java.lang.String r1 = r11.getString(r1)     // Catch:{ all -> 0x03d5 }
            java.lang.Long r1 = org.telegram.messenger.Utilities.parseLong(r1)     // Catch:{ all -> 0x03d5 }
            long r23 = r1.longValue()     // Catch:{ all -> 0x03d5 }
            r47 = r3
            r3 = r23
            r23 = r47
            goto L_0x03e4
        L_0x03d5:
            r0 = move-exception
            r2 = -1
            r3 = r49
            r1 = r0
            r7 = r16
            r4 = r28
            goto L_0x1db4
        L_0x03e0:
            r23 = r3
            r3 = 0
        L_0x03e4:
            if (r10 == 0) goto L_0x041b
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r28)     // Catch:{ all -> 0x03d5 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r1.dialogs_read_inbox_max     // Catch:{ all -> 0x03d5 }
            r25 = r8
            java.lang.Long r8 = java.lang.Long.valueOf(r12)     // Catch:{ all -> 0x03d5 }
            java.lang.Object r1 = r1.get(r8)     // Catch:{ all -> 0x03d5 }
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch:{ all -> 0x03d5 }
            if (r1 != 0) goto L_0x0414
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r28)     // Catch:{ all -> 0x03d5 }
            r8 = 0
            int r1 = r1.getDialogReadMax(r8, r12)     // Catch:{ all -> 0x03d5 }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x03d5 }
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r28)     // Catch:{ all -> 0x03d5 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r8 = r8.dialogs_read_inbox_max     // Catch:{ all -> 0x03d5 }
            java.lang.Long r9 = java.lang.Long.valueOf(r12)     // Catch:{ all -> 0x03d5 }
            r8.put(r9, r1)     // Catch:{ all -> 0x03d5 }
        L_0x0414:
            int r1 = r1.intValue()     // Catch:{ all -> 0x03d5 }
            if (r10 <= r1) goto L_0x042f
            goto L_0x042d
        L_0x041b:
            r25 = r8
            r8 = 0
            int r1 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r1 == 0) goto L_0x042f
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r28)     // Catch:{ all -> 0x03d5 }
            boolean r1 = r1.checkMessageByRandomId(r3)     // Catch:{ all -> 0x03d5 }
            if (r1 != 0) goto L_0x042f
        L_0x042d:
            r1 = 1
            goto L_0x0430
        L_0x042f:
            r1 = 0
        L_0x0430:
            if (r1 == 0) goto L_0x1c9b
            java.lang.String r1 = "chat_from_id"
            r30 = r3
            r8 = 0
            long r3 = r11.optLong(r1, r8)     // Catch:{ all -> 0x1c9e }
            java.lang.String r1 = "chat_from_broadcast_id"
            r27 = r2
            long r1 = r11.optLong(r1, r8)     // Catch:{ all -> 0x1c9e }
            r32 = r1
            java.lang.String r1 = "chat_from_group_id"
            long r1 = r11.optLong(r1, r8)     // Catch:{ all -> 0x1c9e }
            int r20 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r20 != 0) goto L_0x0457
            int r29 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r29 == 0) goto L_0x0455
            goto L_0x0457
        L_0x0455:
            r8 = 0
            goto L_0x0458
        L_0x0457:
            r8 = 1
        L_0x0458:
            java.lang.String r9 = "mention"
            boolean r9 = r11.has(r9)     // Catch:{ all -> 0x1c9e }
            if (r9 == 0) goto L_0x046b
            java.lang.String r9 = "mention"
            int r9 = r11.getInt(r9)     // Catch:{ all -> 0x03d5 }
            if (r9 == 0) goto L_0x046b
            r29 = 1
            goto L_0x046d
        L_0x046b:
            r29 = 0
        L_0x046d:
            java.lang.String r9 = "silent"
            boolean r9 = r11.has(r9)     // Catch:{ all -> 0x1c9e }
            if (r9 == 0) goto L_0x0482
            java.lang.String r9 = "silent"
            int r9 = r11.getInt(r9)     // Catch:{ all -> 0x03d5 }
            if (r9 == 0) goto L_0x0482
            r34 = 1
            goto L_0x0484
        L_0x0482:
            r34 = 0
        L_0x0484:
            java.lang.String r9 = "loc_args"
            boolean r9 = r5.has(r9)     // Catch:{ all -> 0x1c9e }
            if (r9 == 0) goto L_0x04a6
            java.lang.String r9 = "loc_args"
            org.json.JSONArray r5 = r5.getJSONArray(r9)     // Catch:{ all -> 0x03d5 }
            int r9 = r5.length()     // Catch:{ all -> 0x03d5 }
            r35 = r3
            java.lang.String[] r3 = new java.lang.String[r9]     // Catch:{ all -> 0x03d5 }
            r4 = 0
        L_0x049b:
            if (r4 >= r9) goto L_0x04a9
            java.lang.String r37 = r5.getString(r4)     // Catch:{ all -> 0x03d5 }
            r3[r4] = r37     // Catch:{ all -> 0x03d5 }
            int r4 = r4 + 1
            goto L_0x049b
        L_0x04a6:
            r35 = r3
            r3 = 0
        L_0x04a9:
            r4 = 0
            r5 = r3[r4]     // Catch:{ all -> 0x1c9e }
            java.lang.String r4 = "edit_date"
            boolean r4 = r11.has(r4)     // Catch:{ all -> 0x1c9e }
            java.lang.String r9 = "CHAT_"
            boolean r9 = r14.startsWith(r9)     // Catch:{ all -> 0x1c9e }
            if (r9 == 0) goto L_0x04ef
            boolean r9 = org.telegram.messenger.UserObject.isReplyUser((long) r12)     // Catch:{ all -> 0x03d5 }
            if (r9 == 0) goto L_0x04d8
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x03d5 }
            r9.<init>()     // Catch:{ all -> 0x03d5 }
            r9.append(r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r5 = " @ "
            r9.append(r5)     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r11 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r9.append(r11)     // Catch:{ all -> 0x03d5 }
            java.lang.String r5 = r9.toString()     // Catch:{ all -> 0x03d5 }
            goto L_0x0512
        L_0x04d8:
            r20 = 0
            int r9 = (r6 > r20 ? 1 : (r6 == r20 ? 0 : -1))
            if (r9 == 0) goto L_0x04e0
            r9 = 1
            goto L_0x04e1
        L_0x04e0:
            r9 = 0
        L_0x04e1:
            r11 = 1
            r37 = r3[r11]     // Catch:{ all -> 0x03d5 }
            r11 = 0
            r38 = 0
            r47 = r9
            r9 = r5
            r5 = r37
            r37 = r47
            goto L_0x0518
        L_0x04ef:
            java.lang.String r9 = "PINNED_"
            boolean r9 = r14.startsWith(r9)     // Catch:{ all -> 0x1c9e }
            if (r9 == 0) goto L_0x0507
            r20 = 0
            int r9 = (r6 > r20 ? 1 : (r6 == r20 ? 0 : -1))
            if (r9 == 0) goto L_0x04ff
            r9 = 1
            goto L_0x0500
        L_0x04ff:
            r9 = 0
        L_0x0500:
            r37 = r9
            r9 = 0
            r11 = 0
            r38 = 1
            goto L_0x0518
        L_0x0507:
            java.lang.String r9 = "CHANNEL_"
            boolean r9 = r14.startsWith(r9)     // Catch:{ all -> 0x1c9e }
            if (r9 == 0) goto L_0x0512
            r9 = 0
            r11 = 1
            goto L_0x0514
        L_0x0512:
            r9 = 0
            r11 = 0
        L_0x0514:
            r37 = 0
            r38 = 0
        L_0x0518:
            boolean r39 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1c9e }
            if (r39 == 0) goto L_0x0543
            r39 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x03d5 }
            r5.<init>()     // Catch:{ all -> 0x03d5 }
            r40 = r4
            java.lang.String r4 = "GCM received message notification "
            r5.append(r4)     // Catch:{ all -> 0x03d5 }
            r5.append(r14)     // Catch:{ all -> 0x03d5 }
            r5.append(r15)     // Catch:{ all -> 0x03d5 }
            r5.append(r12)     // Catch:{ all -> 0x03d5 }
            java.lang.String r4 = " mid = "
            r5.append(r4)     // Catch:{ all -> 0x03d5 }
            r5.append(r10)     // Catch:{ all -> 0x03d5 }
            java.lang.String r4 = r5.toString()     // Catch:{ all -> 0x03d5 }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ all -> 0x03d5 }
            goto L_0x0547
        L_0x0543:
            r40 = r4
            r39 = r5
        L_0x0547:
            int r4 = r14.hashCode()     // Catch:{ all -> 0x1c9e }
            switch(r4) {
                case -2100047043: goto L_0x0a74;
                case -2091498420: goto L_0x0a69;
                case -2053872415: goto L_0x0a5e;
                case -2039746363: goto L_0x0a53;
                case -2023218804: goto L_0x0a48;
                case -1979538588: goto L_0x0a3d;
                case -1979536003: goto L_0x0a32;
                case -1979535888: goto L_0x0a27;
                case -1969004705: goto L_0x0a1c;
                case -1946699248: goto L_0x0a10;
                case -1646640058: goto L_0x0a04;
                case -1528047021: goto L_0x09f8;
                case -1493579426: goto L_0x09ec;
                case -1482481933: goto L_0x09e0;
                case -1480102982: goto L_0x09d5;
                case -1478041834: goto L_0x09c9;
                case -1474543101: goto L_0x09be;
                case -1465695932: goto L_0x09b2;
                case -1374906292: goto L_0x09a6;
                case -1372940586: goto L_0x099a;
                case -1264245338: goto L_0x098e;
                case -1236154001: goto L_0x0982;
                case -1236086700: goto L_0x0976;
                case -1236077786: goto L_0x096a;
                case -1235796237: goto L_0x095e;
                case -1235760759: goto L_0x0952;
                case -1235686303: goto L_0x0947;
                case -1198046100: goto L_0x093c;
                case -1124254527: goto L_0x0930;
                case -1085137927: goto L_0x0924;
                case -1084856378: goto L_0x0918;
                case -1084820900: goto L_0x090c;
                case -1084746444: goto L_0x0900;
                case -819729482: goto L_0x08f4;
                case -772141857: goto L_0x08e8;
                case -638310039: goto L_0x08dc;
                case -590403924: goto L_0x08d0;
                case -589196239: goto L_0x08c4;
                case -589193654: goto L_0x08b8;
                case -589193539: goto L_0x08ac;
                case -440169325: goto L_0x08a0;
                case -412748110: goto L_0x0894;
                case -228518075: goto L_0x0888;
                case -213586509: goto L_0x087c;
                case -115582002: goto L_0x0870;
                case -112621464: goto L_0x0864;
                case -108522133: goto L_0x0858;
                case -107572034: goto L_0x084d;
                case -40534265: goto L_0x0841;
                case 65254746: goto L_0x0835;
                case 141040782: goto L_0x0829;
                case 202550149: goto L_0x081d;
                case 309993049: goto L_0x0811;
                case 309995634: goto L_0x0805;
                case 309995749: goto L_0x07f9;
                case 320532812: goto L_0x07ed;
                case 328933854: goto L_0x07e1;
                case 331340546: goto L_0x07d5;
                case 342406591: goto L_0x07c9;
                case 344816990: goto L_0x07bd;
                case 346878138: goto L_0x07b1;
                case 350376871: goto L_0x07a5;
                case 608430149: goto L_0x0799;
                case 615714517: goto L_0x078e;
                case 715508879: goto L_0x0782;
                case 728985323: goto L_0x0776;
                case 731046471: goto L_0x076a;
                case 734545204: goto L_0x075e;
                case 802032552: goto L_0x0752;
                case 991498806: goto L_0x0746;
                case 1007364121: goto L_0x073a;
                case 1019850010: goto L_0x072e;
                case 1019917311: goto L_0x0722;
                case 1019926225: goto L_0x0716;
                case 1020207774: goto L_0x070a;
                case 1020243252: goto L_0x06fe;
                case 1020317708: goto L_0x06f2;
                case 1060282259: goto L_0x06e6;
                case 1060349560: goto L_0x06da;
                case 1060358474: goto L_0x06ce;
                case 1060640023: goto L_0x06c2;
                case 1060675501: goto L_0x06b6;
                case 1060749957: goto L_0x06ab;
                case 1073049781: goto L_0x069f;
                case 1078101399: goto L_0x0693;
                case 1110103437: goto L_0x0687;
                case 1160762272: goto L_0x067b;
                case 1172918249: goto L_0x066f;
                case 1234591620: goto L_0x0663;
                case 1281128640: goto L_0x0657;
                case 1281131225: goto L_0x064b;
                case 1281131340: goto L_0x063f;
                case 1310789062: goto L_0x0634;
                case 1333118583: goto L_0x0628;
                case 1361447897: goto L_0x061c;
                case 1498266155: goto L_0x0610;
                case 1533804208: goto L_0x0604;
                case 1540131626: goto L_0x05f8;
                case 1547988151: goto L_0x05ec;
                case 1561464595: goto L_0x05e0;
                case 1563525743: goto L_0x05d4;
                case 1567024476: goto L_0x05c8;
                case 1810705077: goto L_0x05bc;
                case 1815177512: goto L_0x05b0;
                case 1954774321: goto L_0x05a4;
                case 1963241394: goto L_0x0598;
                case 2014789757: goto L_0x058c;
                case 2022049433: goto L_0x0580;
                case 2034984710: goto L_0x0574;
                case 2048733346: goto L_0x0568;
                case 2099392181: goto L_0x055c;
                case 2140162142: goto L_0x0550;
                default: goto L_0x054e;
            }
        L_0x054e:
            goto L_0x0a7f
        L_0x0550:
            java.lang.String r4 = "CHAT_MESSAGE_GEOLIVE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 60
            goto L_0x0a80
        L_0x055c:
            java.lang.String r4 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 43
            goto L_0x0a80
        L_0x0568:
            java.lang.String r4 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 28
            goto L_0x0a80
        L_0x0574:
            java.lang.String r4 = "CHANNEL_MESSAGE_PLAYLIST"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 45
            goto L_0x0a80
        L_0x0580:
            java.lang.String r4 = "PINNED_CONTACT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 93
            goto L_0x0a80
        L_0x058c:
            java.lang.String r4 = "CHAT_PHOTO_EDITED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 68
            goto L_0x0a80
        L_0x0598:
            java.lang.String r4 = "LOCKED_MESSAGE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 106(0x6a, float:1.49E-43)
            goto L_0x0a80
        L_0x05a4:
            java.lang.String r4 = "CHAT_MESSAGE_PLAYLIST"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 82
            goto L_0x0a80
        L_0x05b0:
            java.lang.String r4 = "CHANNEL_MESSAGES"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 47
            goto L_0x0a80
        L_0x05bc:
            java.lang.String r4 = "MESSAGE_INVOICE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 21
            goto L_0x0a80
        L_0x05c8:
            java.lang.String r4 = "CHAT_MESSAGE_VIDEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 51
            goto L_0x0a80
        L_0x05d4:
            java.lang.String r4 = "CHAT_MESSAGE_ROUND"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 52
            goto L_0x0a80
        L_0x05e0:
            java.lang.String r4 = "CHAT_MESSAGE_PHOTO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 50
            goto L_0x0a80
        L_0x05ec:
            java.lang.String r4 = "CHAT_MESSAGE_AUDIO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 55
            goto L_0x0a80
        L_0x05f8:
            java.lang.String r4 = "MESSAGE_PLAYLIST"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 25
            goto L_0x0a80
        L_0x0604:
            java.lang.String r4 = "MESSAGE_VIDEOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 24
            goto L_0x0a80
        L_0x0610:
            java.lang.String r4 = "PHONE_CALL_MISSED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 111(0x6f, float:1.56E-43)
            goto L_0x0a80
        L_0x061c:
            java.lang.String r4 = "MESSAGE_PHOTOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 23
            goto L_0x0a80
        L_0x0628:
            java.lang.String r4 = "CHAT_MESSAGE_VIDEOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 81
            goto L_0x0a80
        L_0x0634:
            java.lang.String r4 = "MESSAGE_NOTEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 2
            goto L_0x0a80
        L_0x063f:
            java.lang.String r4 = "MESSAGE_GIF"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 17
            goto L_0x0a80
        L_0x064b:
            java.lang.String r4 = "MESSAGE_GEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 15
            goto L_0x0a80
        L_0x0657:
            java.lang.String r4 = "MESSAGE_DOC"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 9
            goto L_0x0a80
        L_0x0663:
            java.lang.String r4 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 63
            goto L_0x0a80
        L_0x066f:
            java.lang.String r4 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 39
            goto L_0x0a80
        L_0x067b:
            java.lang.String r4 = "CHAT_MESSAGE_PHOTOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 80
            goto L_0x0a80
        L_0x0687:
            java.lang.String r4 = "CHAT_MESSAGE_NOTEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 49
            goto L_0x0a80
        L_0x0693:
            java.lang.String r4 = "CHAT_TITLE_EDITED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 67
            goto L_0x0a80
        L_0x069f:
            java.lang.String r4 = "PINNED_NOTEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 86
            goto L_0x0a80
        L_0x06ab:
            java.lang.String r4 = "MESSAGE_TEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 0
            goto L_0x0a80
        L_0x06b6:
            java.lang.String r4 = "MESSAGE_QUIZ"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 13
            goto L_0x0a80
        L_0x06c2:
            java.lang.String r4 = "MESSAGE_POLL"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 14
            goto L_0x0a80
        L_0x06ce:
            java.lang.String r4 = "MESSAGE_GAME"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 18
            goto L_0x0a80
        L_0x06da:
            java.lang.String r4 = "MESSAGE_FWDS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 22
            goto L_0x0a80
        L_0x06e6:
            java.lang.String r4 = "MESSAGE_DOCS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 26
            goto L_0x0a80
        L_0x06f2:
            java.lang.String r4 = "CHAT_MESSAGE_TEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 48
            goto L_0x0a80
        L_0x06fe:
            java.lang.String r4 = "CHAT_MESSAGE_QUIZ"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 57
            goto L_0x0a80
        L_0x070a:
            java.lang.String r4 = "CHAT_MESSAGE_POLL"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 58
            goto L_0x0a80
        L_0x0716:
            java.lang.String r4 = "CHAT_MESSAGE_GAME"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 62
            goto L_0x0a80
        L_0x0722:
            java.lang.String r4 = "CHAT_MESSAGE_FWDS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 79
            goto L_0x0a80
        L_0x072e:
            java.lang.String r4 = "CHAT_MESSAGE_DOCS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 83
            goto L_0x0a80
        L_0x073a:
            java.lang.String r4 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 20
            goto L_0x0a80
        L_0x0746:
            java.lang.String r4 = "PINNED_GEOLIVE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 97
            goto L_0x0a80
        L_0x0752:
            java.lang.String r4 = "MESSAGE_CONTACT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 12
            goto L_0x0a80
        L_0x075e:
            java.lang.String r4 = "PINNED_VIDEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 88
            goto L_0x0a80
        L_0x076a:
            java.lang.String r4 = "PINNED_ROUND"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 89
            goto L_0x0a80
        L_0x0776:
            java.lang.String r4 = "PINNED_PHOTO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 87
            goto L_0x0a80
        L_0x0782:
            java.lang.String r4 = "PINNED_AUDIO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 92
            goto L_0x0a80
        L_0x078e:
            java.lang.String r4 = "MESSAGE_PHOTO_SECRET"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 4
            goto L_0x0a80
        L_0x0799:
            java.lang.String r4 = "CHAT_VOICECHAT_INVITE_YOU"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 73
            goto L_0x0a80
        L_0x07a5:
            java.lang.String r4 = "CHANNEL_MESSAGE_VIDEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 30
            goto L_0x0a80
        L_0x07b1:
            java.lang.String r4 = "CHANNEL_MESSAGE_ROUND"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 31
            goto L_0x0a80
        L_0x07bd:
            java.lang.String r4 = "CHANNEL_MESSAGE_PHOTO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 29
            goto L_0x0a80
        L_0x07c9:
            java.lang.String r4 = "CHAT_VOICECHAT_END"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 72
            goto L_0x0a80
        L_0x07d5:
            java.lang.String r4 = "CHANNEL_MESSAGE_AUDIO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 34
            goto L_0x0a80
        L_0x07e1:
            java.lang.String r4 = "CHAT_MESSAGE_STICKER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 54
            goto L_0x0a80
        L_0x07ed:
            java.lang.String r4 = "MESSAGES"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 27
            goto L_0x0a80
        L_0x07f9:
            java.lang.String r4 = "CHAT_MESSAGE_GIF"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 61
            goto L_0x0a80
        L_0x0805:
            java.lang.String r4 = "CHAT_MESSAGE_GEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 59
            goto L_0x0a80
        L_0x0811:
            java.lang.String r4 = "CHAT_MESSAGE_DOC"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 53
            goto L_0x0a80
        L_0x081d:
            java.lang.String r4 = "CHAT_VOICECHAT_INVITE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 71
            goto L_0x0a80
        L_0x0829:
            java.lang.String r4 = "CHAT_LEFT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 76
            goto L_0x0a80
        L_0x0835:
            java.lang.String r4 = "CHAT_ADD_YOU"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 66
            goto L_0x0a80
        L_0x0841:
            java.lang.String r4 = "CHAT_DELETE_MEMBER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 74
            goto L_0x0a80
        L_0x084d:
            java.lang.String r4 = "MESSAGE_SCREENSHOT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 7
            goto L_0x0a80
        L_0x0858:
            java.lang.String r4 = "AUTH_REGION"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 105(0x69, float:1.47E-43)
            goto L_0x0a80
        L_0x0864:
            java.lang.String r4 = "CONTACT_JOINED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 103(0x67, float:1.44E-43)
            goto L_0x0a80
        L_0x0870:
            java.lang.String r4 = "CHAT_MESSAGE_INVOICE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 64
            goto L_0x0a80
        L_0x087c:
            java.lang.String r4 = "ENCRYPTION_REQUEST"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 107(0x6b, float:1.5E-43)
            goto L_0x0a80
        L_0x0888:
            java.lang.String r4 = "MESSAGE_GEOLIVE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 16
            goto L_0x0a80
        L_0x0894:
            java.lang.String r4 = "CHAT_DELETE_YOU"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 75
            goto L_0x0a80
        L_0x08a0:
            java.lang.String r4 = "AUTH_UNKNOWN"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 104(0x68, float:1.46E-43)
            goto L_0x0a80
        L_0x08ac:
            java.lang.String r4 = "PINNED_GIF"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 101(0x65, float:1.42E-43)
            goto L_0x0a80
        L_0x08b8:
            java.lang.String r4 = "PINNED_GEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 96
            goto L_0x0a80
        L_0x08c4:
            java.lang.String r4 = "PINNED_DOC"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 90
            goto L_0x0a80
        L_0x08d0:
            java.lang.String r4 = "PINNED_GAME_SCORE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 99
            goto L_0x0a80
        L_0x08dc:
            java.lang.String r4 = "CHANNEL_MESSAGE_STICKER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 33
            goto L_0x0a80
        L_0x08e8:
            java.lang.String r4 = "PHONE_CALL_REQUEST"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 109(0x6d, float:1.53E-43)
            goto L_0x0a80
        L_0x08f4:
            java.lang.String r4 = "PINNED_STICKER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 91
            goto L_0x0a80
        L_0x0900:
            java.lang.String r4 = "PINNED_TEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 85
            goto L_0x0a80
        L_0x090c:
            java.lang.String r4 = "PINNED_QUIZ"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 94
            goto L_0x0a80
        L_0x0918:
            java.lang.String r4 = "PINNED_POLL"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 95
            goto L_0x0a80
        L_0x0924:
            java.lang.String r4 = "PINNED_GAME"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 98
            goto L_0x0a80
        L_0x0930:
            java.lang.String r4 = "CHAT_MESSAGE_CONTACT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 56
            goto L_0x0a80
        L_0x093c:
            java.lang.String r4 = "MESSAGE_VIDEO_SECRET"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 6
            goto L_0x0a80
        L_0x0947:
            java.lang.String r4 = "CHANNEL_MESSAGE_TEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 1
            goto L_0x0a80
        L_0x0952:
            java.lang.String r4 = "CHANNEL_MESSAGE_QUIZ"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 36
            goto L_0x0a80
        L_0x095e:
            java.lang.String r4 = "CHANNEL_MESSAGE_POLL"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 37
            goto L_0x0a80
        L_0x096a:
            java.lang.String r4 = "CHANNEL_MESSAGE_GAME"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 41
            goto L_0x0a80
        L_0x0976:
            java.lang.String r4 = "CHANNEL_MESSAGE_FWDS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 42
            goto L_0x0a80
        L_0x0982:
            java.lang.String r4 = "CHANNEL_MESSAGE_DOCS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 46
            goto L_0x0a80
        L_0x098e:
            java.lang.String r4 = "PINNED_INVOICE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 100
            goto L_0x0a80
        L_0x099a:
            java.lang.String r4 = "CHAT_RETURNED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 77
            goto L_0x0a80
        L_0x09a6:
            java.lang.String r4 = "ENCRYPTED_MESSAGE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 102(0x66, float:1.43E-43)
            goto L_0x0a80
        L_0x09b2:
            java.lang.String r4 = "ENCRYPTION_ACCEPT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 108(0x6c, float:1.51E-43)
            goto L_0x0a80
        L_0x09be:
            java.lang.String r4 = "MESSAGE_VIDEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 5
            goto L_0x0a80
        L_0x09c9:
            java.lang.String r4 = "MESSAGE_ROUND"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 8
            goto L_0x0a80
        L_0x09d5:
            java.lang.String r4 = "MESSAGE_PHOTO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 3
            goto L_0x0a80
        L_0x09e0:
            java.lang.String r4 = "MESSAGE_MUTED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 110(0x6e, float:1.54E-43)
            goto L_0x0a80
        L_0x09ec:
            java.lang.String r4 = "MESSAGE_AUDIO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 11
            goto L_0x0a80
        L_0x09f8:
            java.lang.String r4 = "CHAT_MESSAGES"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 84
            goto L_0x0a80
        L_0x0a04:
            java.lang.String r4 = "CHAT_VOICECHAT_START"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 70
            goto L_0x0a80
        L_0x0a10:
            java.lang.String r4 = "CHAT_JOINED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 78
            goto L_0x0a80
        L_0x0a1c:
            java.lang.String r4 = "CHAT_ADD_MEMBER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 69
            goto L_0x0a80
        L_0x0a27:
            java.lang.String r4 = "CHANNEL_MESSAGE_GIF"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 40
            goto L_0x0a80
        L_0x0a32:
            java.lang.String r4 = "CHANNEL_MESSAGE_GEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 38
            goto L_0x0a80
        L_0x0a3d:
            java.lang.String r4 = "CHANNEL_MESSAGE_DOC"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 32
            goto L_0x0a80
        L_0x0a48:
            java.lang.String r4 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 44
            goto L_0x0a80
        L_0x0a53:
            java.lang.String r4 = "MESSAGE_STICKER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 10
            goto L_0x0a80
        L_0x0a5e:
            java.lang.String r4 = "CHAT_CREATED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 65
            goto L_0x0a80
        L_0x0a69:
            java.lang.String r4 = "CHANNEL_MESSAGE_CONTACT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 35
            goto L_0x0a80
        L_0x0a74:
            java.lang.String r4 = "MESSAGE_GAME_SCORE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03d5 }
            if (r4 == 0) goto L_0x0a7f
            r4 = 19
            goto L_0x0a80
        L_0x0a7f:
            r4 = -1
        L_0x0a80:
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
                case 0: goto L_0x1b85;
                case 1: goto L_0x1b85;
                case 2: goto L_0x1b63;
                case 3: goto L_0x1b46;
                case 4: goto L_0x1b29;
                case 5: goto L_0x1b0c;
                case 6: goto L_0x1aee;
                case 7: goto L_0x1ad5;
                case 8: goto L_0x1ab7;
                case 9: goto L_0x1a99;
                case 10: goto L_0x1a3e;
                case 11: goto L_0x1a20;
                case 12: goto L_0x19fd;
                case 13: goto L_0x19da;
                case 14: goto L_0x19b7;
                case 15: goto L_0x1999;
                case 16: goto L_0x197b;
                case 17: goto L_0x195d;
                case 18: goto L_0x193a;
                case 19: goto L_0x191b;
                case 20: goto L_0x191b;
                case 21: goto L_0x18f8;
                case 22: goto L_0x18d0;
                case 23: goto L_0x18ac;
                case 24: goto L_0x1889;
                case 25: goto L_0x1866;
                case 26: goto L_0x1841;
                case 27: goto L_0x1829;
                case 28: goto L_0x180b;
                case 29: goto L_0x17ed;
                case 30: goto L_0x17cf;
                case 31: goto L_0x17b1;
                case 32: goto L_0x1793;
                case 33: goto L_0x1738;
                case 34: goto L_0x171a;
                case 35: goto L_0x16f7;
                case 36: goto L_0x16d4;
                case 37: goto L_0x16b1;
                case 38: goto L_0x1693;
                case 39: goto L_0x1675;
                case 40: goto L_0x1657;
                case 41: goto L_0x1639;
                case 42: goto L_0x160f;
                case 43: goto L_0x15eb;
                case 44: goto L_0x15c7;
                case 45: goto L_0x15a3;
                case 46: goto L_0x157d;
                case 47: goto L_0x1568;
                case 48: goto L_0x1547;
                case 49: goto L_0x1524;
                case 50: goto L_0x1501;
                case 51: goto L_0x14de;
                case 52: goto L_0x14bb;
                case 53: goto L_0x1498;
                case 54: goto L_0x141f;
                case 55: goto L_0x13fc;
                case 56: goto L_0x13d4;
                case 57: goto L_0x13ac;
                case 58: goto L_0x1384;
                case 59: goto L_0x1361;
                case 60: goto L_0x133e;
                case 61: goto L_0x131b;
                case 62: goto L_0x12f3;
                case 63: goto L_0x12cf;
                case 64: goto L_0x12a7;
                case 65: goto L_0x128d;
                case 66: goto L_0x128d;
                case 67: goto L_0x1273;
                case 68: goto L_0x1259;
                case 69: goto L_0x123a;
                case 70: goto L_0x1220;
                case 71: goto L_0x1201;
                case 72: goto L_0x11e7;
                case 73: goto L_0x11cd;
                case 74: goto L_0x11b3;
                case 75: goto L_0x1199;
                case 76: goto L_0x117f;
                case 77: goto L_0x1165;
                case 78: goto L_0x114b;
                case 79: goto L_0x111e;
                case 80: goto L_0x10f5;
                case 81: goto L_0x10cc;
                case 82: goto L_0x10a3;
                case 83: goto L_0x1078;
                case 84: goto L_0x105e;
                case 85: goto L_0x1007;
                case 86: goto L_0x0fba;
                case 87: goto L_0x0f6d;
                case 88: goto L_0x0var_;
                case 89: goto L_0x0ed3;
                case 90: goto L_0x0e86;
                case 91: goto L_0x0dcd;
                case 92: goto L_0x0d80;
                case 93: goto L_0x0d29;
                case 94: goto L_0x0cd2;
                case 95: goto L_0x0CLASSNAME;
                case 96: goto L_0x0CLASSNAME;
                case 97: goto L_0x0bec;
                case 98: goto L_0x0ba1;
                case 99: goto L_0x0b56;
                case 100: goto L_0x0b0b;
                case 101: goto L_0x0ac0;
                case 102: goto L_0x0aa5;
                case 103: goto L_0x0aa1;
                case 104: goto L_0x0aa1;
                case 105: goto L_0x0aa1;
                case 106: goto L_0x0aa1;
                case 107: goto L_0x0aa1;
                case 108: goto L_0x0aa1;
                case 109: goto L_0x0aa1;
                case 110: goto L_0x0aa1;
                case 111: goto L_0x0aa1;
                default: goto L_0x0a9b;
            }
        L_0x0a9b:
            r4 = r22
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1c9e }
            goto L_0x1ba0
        L_0x0aa1:
            r4 = r22
            goto L_0x1bb7
        L_0x0aa5:
            java.lang.String r1 = "YouHaveNewMessage"
            r2 = 2131628572(0x7f0e121c, float:1.888444E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "SecretChatName"
            r3 = 2131627542(0x7f0e0e16, float:1.8882351E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            r4 = r22
            r17 = 0
            r22 = r2
            r2 = 1
            goto L_0x1bbd
        L_0x0ac0:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0ade
            java.lang.String r1 = "NotificationActionPinnedGifUser"
            r2 = 2131626479(0x7f0e09ef, float:1.8880195E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0ade:
            if (r8 == 0) goto L_0x0af8
            java.lang.String r1 = "NotificationActionPinnedGif"
            r2 = 2131626477(0x7f0e09ed, float:1.8880191E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0af8:
            java.lang.String r1 = "NotificationActionPinnedGifChannel"
            r2 = 2131626478(0x7f0e09ee, float:1.8880193E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03d5 }
            r5[r4] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0b0b:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0b29
            java.lang.String r1 = "NotificationActionPinnedInvoiceUser"
            r2 = 2131626482(0x7f0e09f2, float:1.8880201E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0b29:
            if (r8 == 0) goto L_0x0b43
            java.lang.String r1 = "NotificationActionPinnedInvoice"
            r2 = 2131626480(0x7f0e09f0, float:1.8880197E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0b43:
            java.lang.String r1 = "NotificationActionPinnedInvoiceChannel"
            r2 = 2131626481(0x7f0e09f1, float:1.88802E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03d5 }
            r5[r4] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0b56:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0b74
            java.lang.String r1 = "NotificationActionPinnedGameScoreUser"
            r2 = 2131626469(0x7f0e09e5, float:1.8880175E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0b74:
            if (r8 == 0) goto L_0x0b8e
            java.lang.String r1 = "NotificationActionPinnedGameScore"
            r2 = 2131626467(0x7f0e09e3, float:1.8880171E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0b8e:
            java.lang.String r1 = "NotificationActionPinnedGameScoreChannel"
            r2 = 2131626468(0x7f0e09e4, float:1.8880173E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03d5 }
            r5[r4] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0ba1:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0bbf
            java.lang.String r1 = "NotificationActionPinnedGameUser"
            r2 = 2131626470(0x7f0e09e6, float:1.8880177E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0bbf:
            if (r8 == 0) goto L_0x0bd9
            java.lang.String r1 = "NotificationActionPinnedGame"
            r2 = 2131626465(0x7f0e09e1, float:1.8880167E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0bd9:
            java.lang.String r1 = "NotificationActionPinnedGameChannel"
            r2 = 2131626466(0x7f0e09e2, float:1.888017E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03d5 }
            r5[r4] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0bec:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0c0a
            java.lang.String r1 = "NotificationActionPinnedGeoLiveUser"
            r2 = 2131626475(0x7f0e09eb, float:1.8880187E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0c0a:
            if (r8 == 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGeoLive"
            r2 = 2131626473(0x7f0e09e9, float:1.8880183E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0CLASSNAME:
            java.lang.String r1 = "NotificationActionPinnedGeoLiveChannel"
            r2 = 2131626474(0x7f0e09ea, float:1.8880185E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03d5 }
            r5[r4] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0CLASSNAME:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGeoUser"
            r2 = 2131626476(0x7f0e09ec, float:1.888019E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0CLASSNAME:
            if (r8 == 0) goto L_0x0c6b
            java.lang.String r1 = "NotificationActionPinnedGeo"
            r2 = 2131626471(0x7f0e09e7, float:1.888018E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0c6b:
            java.lang.String r1 = "NotificationActionPinnedGeoChannel"
            r2 = 2131626472(0x7f0e09e8, float:1.8880181E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03d5 }
            r5[r4] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
        L_0x0c7c:
            r4 = r22
            goto L_0x1aea
        L_0x0CLASSNAME:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0c9d
            java.lang.String r1 = "NotificationActionPinnedPollUser"
            r2 = 2131626494(0x7f0e09fe, float:1.8880226E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0c9d:
            if (r8 == 0) goto L_0x0cbb
            java.lang.String r1 = "NotificationActionPinnedPoll2"
            r2 = 2131626492(0x7f0e09fc, float:1.8880222E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 2
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r7 = 1
            r4[r7] = r6     // Catch:{ all -> 0x03d5 }
            r3 = r3[r7]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0cbb:
            java.lang.String r1 = "NotificationActionPinnedPollChannel2"
            r2 = 2131626493(0x7f0e09fd, float:1.8880224E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r4[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03d5 }
            goto L_0x0c7c
        L_0x0cd2:
            r4 = r22
            r5 = 0
            int r2 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r2 <= 0) goto L_0x0cf2
            java.lang.String r1 = "NotificationActionPinnedQuizUser"
            r2 = 2131626497(0x7f0e0a01, float:1.8880232E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0cf2:
            if (r8 == 0) goto L_0x0d11
            java.lang.String r2 = "NotificationActionPinnedQuiz2"
            r5 = 2131626495(0x7f0e09ff, float:1.8880228E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 2
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r8 = 1
            r1[r8] = r7     // Catch:{ all -> 0x03d5 }
            r3 = r3[r8]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0d11:
            java.lang.String r1 = "NotificationActionPinnedQuizChannel2"
            r2 = 2131626496(0x7f0e0a00, float:1.888023E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0d29:
            r4 = r22
            r5 = 0
            int r2 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r2 <= 0) goto L_0x0d49
            java.lang.String r1 = "NotificationActionPinnedContactUser"
            r2 = 2131626461(0x7f0e09dd, float:1.8880159E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0d49:
            if (r8 == 0) goto L_0x0d68
            java.lang.String r2 = "NotificationActionPinnedContact2"
            r5 = 2131626459(0x7f0e09db, float:1.8880155E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 2
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r8 = 1
            r1[r8] = r7     // Catch:{ all -> 0x03d5 }
            r3 = r3[r8]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0d68:
            java.lang.String r1 = "NotificationActionPinnedContactChannel2"
            r2 = 2131626460(0x7f0e09dc, float:1.8880157E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0d80:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0da0
            java.lang.String r1 = "NotificationActionPinnedVoiceUser"
            r2 = 2131626515(0x7f0e0a13, float:1.8880268E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0da0:
            if (r8 == 0) goto L_0x0dba
            java.lang.String r1 = "NotificationActionPinnedVoice"
            r2 = 2131626513(0x7f0e0a11, float:1.8880264E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0dba:
            java.lang.String r1 = "NotificationActionPinnedVoiceChannel"
            r2 = 2131626514(0x7f0e0a12, float:1.8880266E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0dcd:
            r4 = r22
            r5 = 0
            int r2 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e0c
            int r1 = r3.length     // Catch:{ all -> 0x03d5 }
            r2 = 1
            if (r1 <= r2) goto L_0x0df9
            r1 = r3[r2]     // Catch:{ all -> 0x03d5 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x03d5 }
            if (r1 != 0) goto L_0x0df9
            java.lang.String r1 = "NotificationActionPinnedStickerEmojiUser"
            r2 = 2131626505(0x7f0e0a09, float:1.8880248E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0df9:
            java.lang.String r1 = "NotificationActionPinnedStickerUser"
            r2 = 2131626506(0x7f0e0a0a, float:1.888025E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0e0c:
            if (r8 == 0) goto L_0x0e4f
            int r2 = r3.length     // Catch:{ all -> 0x03d5 }
            r5 = 2
            if (r2 <= r5) goto L_0x0e37
            r2 = r3[r5]     // Catch:{ all -> 0x03d5 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x03d5 }
            if (r2 != 0) goto L_0x0e37
            java.lang.String r2 = "NotificationActionPinnedStickerEmoji"
            r5 = 2131626503(0x7f0e0a07, float:1.8880244E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 2
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r8 = 1
            r1[r8] = r7     // Catch:{ all -> 0x03d5 }
            r3 = r3[r8]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0e37:
            java.lang.String r1 = "NotificationActionPinnedSticker"
            r2 = 2131626501(0x7f0e0a05, float:1.888024E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0e4f:
            int r1 = r3.length     // Catch:{ all -> 0x03d5 }
            r2 = 1
            if (r1 <= r2) goto L_0x0e73
            r1 = r3[r2]     // Catch:{ all -> 0x03d5 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x03d5 }
            if (r1 != 0) goto L_0x0e73
            java.lang.String r1 = "NotificationActionPinnedStickerEmojiChannel"
            r2 = 2131626504(0x7f0e0a08, float:1.8880246E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0e73:
            java.lang.String r1 = "NotificationActionPinnedStickerChannel"
            r2 = 2131626502(0x7f0e0a06, float:1.8880242E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0e86:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0ea6
            java.lang.String r1 = "NotificationActionPinnedFileUser"
            r2 = 2131626464(0x7f0e09e0, float:1.8880165E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0ea6:
            if (r8 == 0) goto L_0x0ec0
            java.lang.String r1 = "NotificationActionPinnedFile"
            r2 = 2131626462(0x7f0e09de, float:1.888016E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0ec0:
            java.lang.String r1 = "NotificationActionPinnedFileChannel"
            r2 = 2131626463(0x7f0e09df, float:1.8880163E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0ed3:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0ef3
            java.lang.String r1 = "NotificationActionPinnedRoundUser"
            r2 = 2131626500(0x7f0e0a04, float:1.8880238E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0ef3:
            if (r8 == 0) goto L_0x0f0d
            java.lang.String r1 = "NotificationActionPinnedRound"
            r2 = 2131626498(0x7f0e0a02, float:1.8880234E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0f0d:
            java.lang.String r1 = "NotificationActionPinnedRoundChannel"
            r2 = 2131626499(0x7f0e0a03, float:1.8880236E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0var_:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedVideoUser"
            r2 = 2131626512(0x7f0e0a10, float:1.8880262E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0var_:
            if (r8 == 0) goto L_0x0f5a
            java.lang.String r1 = "NotificationActionPinnedVideo"
            r2 = 2131626510(0x7f0e0a0e, float:1.8880258E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0f5a:
            java.lang.String r1 = "NotificationActionPinnedVideoChannel"
            r2 = 2131626511(0x7f0e0a0f, float:1.888026E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0f6d:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0f8d
            java.lang.String r1 = "NotificationActionPinnedPhotoUser"
            r2 = 2131626491(0x7f0e09fb, float:1.888022E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0f8d:
            if (r8 == 0) goto L_0x0fa7
            java.lang.String r1 = "NotificationActionPinnedPhoto"
            r2 = 2131626489(0x7f0e09f9, float:1.8880216E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0fa7:
            java.lang.String r1 = "NotificationActionPinnedPhotoChannel"
            r2 = 2131626490(0x7f0e09fa, float:1.8880218E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0fba:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0fda
            java.lang.String r1 = "NotificationActionPinnedNoTextUser"
            r2 = 2131626488(0x7f0e09f8, float:1.8880214E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0fda:
            if (r8 == 0) goto L_0x0ff4
            java.lang.String r1 = "NotificationActionPinnedNoText"
            r2 = 2131626486(0x7f0e09f6, float:1.888021E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x0ff4:
            java.lang.String r1 = "NotificationActionPinnedNoTextChannel"
            r2 = 2131626487(0x7f0e09f7, float:1.8880212E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x1007:
            r4 = r22
            r5 = 0
            int r2 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r2 <= 0) goto L_0x1027
            java.lang.String r1 = "NotificationActionPinnedTextUser"
            r2 = 2131626509(0x7f0e0a0d, float:1.8880256E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x1027:
            if (r8 == 0) goto L_0x1046
            java.lang.String r2 = "NotificationActionPinnedText"
            r5 = 2131626507(0x7f0e0a0b, float:1.8880252E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x1046:
            java.lang.String r1 = "NotificationActionPinnedTextChannel"
            r2 = 2131626508(0x7f0e0a0c, float:1.8880254E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x105e:
            r4 = r22
            java.lang.String r1 = "NotificationGroupAlbum"
            r2 = 2131626524(0x7f0e0a1c, float:1.8880287E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x183c
        L_0x1078:
            r4 = r22
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r1[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r1[r5] = r6     // Catch:{ all -> 0x03d5 }
            java.lang.String r5 = "Files"
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d5 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d5 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)     // Catch:{ all -> 0x03d5 }
            r1[r6] = r3     // Catch:{ all -> 0x03d5 }
            r3 = 2131626527(0x7f0e0a1f, float:1.8880293E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r3, r1)     // Catch:{ all -> 0x03d5 }
            goto L_0x183c
        L_0x10a3:
            r4 = r22
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d5 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d5 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)     // Catch:{ all -> 0x03d5 }
            r1[r6] = r3     // Catch:{ all -> 0x03d5 }
            r3 = 2131626527(0x7f0e0a1f, float:1.8880293E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r3, r1)     // Catch:{ all -> 0x03d5 }
            goto L_0x183c
        L_0x10cc:
            r4 = r22
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r1[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r1[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 2
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d5 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d5 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r15, r3)     // Catch:{ all -> 0x03d5 }
            r1[r5] = r3     // Catch:{ all -> 0x03d5 }
            r3 = 2131626527(0x7f0e0a1f, float:1.8880293E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r3, r1)     // Catch:{ all -> 0x03d5 }
            goto L_0x183c
        L_0x10f5:
            r4 = r22
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r1[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r1[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 2
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d5 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d5 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r11, r3)     // Catch:{ all -> 0x03d5 }
            r1[r5] = r3     // Catch:{ all -> 0x03d5 }
            r3 = 2131626527(0x7f0e0a1f, float:1.8880293E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r3, r1)     // Catch:{ all -> 0x03d5 }
            goto L_0x183c
        L_0x111e:
            r4 = r22
            java.lang.String r2 = "NotificationGroupForwardedFew"
            r5 = 2131626528(0x7f0e0a20, float:1.8880295E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d5 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d5 }
            r7 = r27
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r7, r3)     // Catch:{ all -> 0x03d5 }
            r1[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d5 }
            goto L_0x183c
        L_0x114b:
            r4 = r22
            java.lang.String r1 = "NotificationGroupAddSelfMega"
            r2 = 2131626523(0x7f0e0a1b, float:1.8880285E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x1165:
            r4 = r22
            java.lang.String r1 = "NotificationGroupAddSelf"
            r2 = 2131626522(0x7f0e0a1a, float:1.8880283E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x117f:
            r4 = r22
            java.lang.String r1 = "NotificationGroupLeftMember"
            r2 = 2131626533(0x7f0e0a25, float:1.8880305E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x1199:
            r4 = r22
            java.lang.String r1 = "NotificationGroupKickYou"
            r2 = 2131626532(0x7f0e0a24, float:1.8880303E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x11b3:
            r4 = r22
            java.lang.String r1 = "NotificationGroupKickMember"
            r2 = 2131626531(0x7f0e0a23, float:1.88803E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x11cd:
            r4 = r22
            java.lang.String r1 = "NotificationGroupInvitedYouToCall"
            r2 = 2131626530(0x7f0e0a22, float:1.8880299E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x11e7:
            r4 = r22
            java.lang.String r1 = "NotificationGroupEndedCall"
            r2 = 2131626526(0x7f0e0a1e, float:1.888029E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x1201:
            r4 = r22
            java.lang.String r2 = "NotificationGroupInvitedToCall"
            r5 = 2131626529(0x7f0e0a21, float:1.8880297E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x1220:
            r4 = r22
            java.lang.String r1 = "NotificationGroupCreatedCall"
            r2 = 2131626525(0x7f0e0a1d, float:1.8880289E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x123a:
            r4 = r22
            java.lang.String r2 = "NotificationGroupAddMember"
            r5 = 2131626521(0x7f0e0a19, float:1.888028E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x1259:
            r4 = r22
            java.lang.String r1 = "NotificationEditedGroupPhoto"
            r2 = 2131626519(0x7f0e0a17, float:1.8880276E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x1273:
            r4 = r22
            java.lang.String r1 = "NotificationEditedGroupName"
            r2 = 2131626518(0x7f0e0a16, float:1.8880274E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x128d:
            r4 = r22
            java.lang.String r1 = "NotificationInvitedToGroup"
            r2 = 2131626538(0x7f0e0a2a, float:1.8880315E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x12a7:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupInvoice"
            r5 = 2131626555(0x7f0e0a3b, float:1.888035E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "PaymentInvoice"
            r3 = 2131626949(0x7f0e0bc5, float:1.8881149E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x12cf:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupGameScored"
            r5 = 2131626553(0x7f0e0a39, float:1.8880345E38)
            r6 = 4
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x03d5 }
            r7 = 0
            r8 = r3[r7]     // Catch:{ all -> 0x03d5 }
            r6[r7] = r8     // Catch:{ all -> 0x03d5 }
            r7 = 1
            r8 = r3[r7]     // Catch:{ all -> 0x03d5 }
            r6[r7] = r8     // Catch:{ all -> 0x03d5 }
            r7 = 2
            r8 = r3[r7]     // Catch:{ all -> 0x03d5 }
            r6[r7] = r8     // Catch:{ all -> 0x03d5 }
            r1 = 3
            r3 = r3[r1]     // Catch:{ all -> 0x03d5 }
            r6[r1] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x12f3:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupGame"
            r5 = 2131626552(0x7f0e0a38, float:1.8880343E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachGame"
            r3 = 2131624391(0x7f0e01c7, float:1.887596E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x131b:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupGif"
            r2 = 2131626554(0x7f0e0a3a, float:1.8880347E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachGif"
            r3 = 2131624392(0x7f0e01c8, float:1.8875962E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x133e:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupLiveLocation"
            r2 = 2131626556(0x7f0e0a3c, float:1.8880352E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachLiveLocation"
            r3 = 2131624397(0x7f0e01cd, float:1.8875973E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1361:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupMap"
            r2 = 2131626557(0x7f0e0a3d, float:1.8880354E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachLocation"
            r3 = 2131624401(0x7f0e01d1, float:1.887598E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1384:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupPoll2"
            r5 = 2131626561(0x7f0e0a41, float:1.8880362E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "Poll"
            r3 = 2131627116(0x7f0e0c6c, float:1.8881487E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x13ac:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupQuiz2"
            r5 = 2131626562(0x7f0e0a42, float:1.8880364E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "PollQuiz"
            r3 = 2131627123(0x7f0e0CLASSNAME, float:1.8881502E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x13d4:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupContact2"
            r5 = 2131626550(0x7f0e0a36, float:1.888034E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachContact"
            r3 = 2131624387(0x7f0e01c3, float:1.8875952E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x13fc:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupAudio"
            r2 = 2131626549(0x7f0e0a35, float:1.8880337E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachAudio"
            r3 = 2131624385(0x7f0e01c1, float:1.8875948E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x141f:
            r4 = r22
            int r2 = r3.length     // Catch:{ all -> 0x03d5 }
            r5 = 2
            if (r2 <= r5) goto L_0x1465
            r2 = r3[r5]     // Catch:{ all -> 0x03d5 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x03d5 }
            if (r2 != 0) goto L_0x1465
            java.lang.String r2 = "NotificationMessageGroupStickerEmoji"
            r5 = 2131626565(0x7f0e0a45, float:1.888037E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r7 = 0
            r8 = r3[r7]     // Catch:{ all -> 0x03d5 }
            r1[r7] = r8     // Catch:{ all -> 0x03d5 }
            r7 = 1
            r8 = r3[r7]     // Catch:{ all -> 0x03d5 }
            r1[r7] = r8     // Catch:{ all -> 0x03d5 }
            r7 = 2
            r8 = r3[r7]     // Catch:{ all -> 0x03d5 }
            r1[r7] = r8     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d5 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03d5 }
            r2.<init>()     // Catch:{ all -> 0x03d5 }
            r3 = r3[r7]     // Catch:{ all -> 0x03d5 }
            r2.append(r3)     // Catch:{ all -> 0x03d5 }
            r2.append(r9)     // Catch:{ all -> 0x03d5 }
            r3 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)     // Catch:{ all -> 0x03d5 }
            r2.append(r3)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1465:
            java.lang.String r1 = "NotificationMessageGroupSticker"
            r2 = 2131626564(0x7f0e0a44, float:1.8880368E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r7 = 0
            r8 = r3[r7]     // Catch:{ all -> 0x03d5 }
            r5[r7] = r8     // Catch:{ all -> 0x03d5 }
            r7 = 1
            r8 = r3[r7]     // Catch:{ all -> 0x03d5 }
            r5[r7] = r8     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03d5 }
            r2.<init>()     // Catch:{ all -> 0x03d5 }
            r3 = r3[r7]     // Catch:{ all -> 0x03d5 }
            r2.append(r3)     // Catch:{ all -> 0x03d5 }
            r2.append(r9)     // Catch:{ all -> 0x03d5 }
            r3 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)     // Catch:{ all -> 0x03d5 }
            r2.append(r3)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1498:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupDocument"
            r2 = 2131626551(0x7f0e0a37, float:1.8880341E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachDocument"
            r3 = 2131624390(0x7f0e01c6, float:1.8875958E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x14bb:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupRound"
            r2 = 2131626563(0x7f0e0a43, float:1.8880366E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachRound"
            r3 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x14de:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupVideo"
            r2 = 2131626567(0x7f0e0a47, float:1.8880374E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachVideo"
            r3 = 2131624411(0x7f0e01db, float:1.8876E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1501:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupPhoto"
            r2 = 2131626560(0x7f0e0a40, float:1.888036E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachPhoto"
            r3 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1524:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupNoText"
            r2 = 2131626559(0x7f0e0a3f, float:1.8880358E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "Message"
            r3 = 2131626229(0x7f0e08f5, float:1.8879688E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1547:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupText"
            r5 = 2131626566(0x7f0e0a46, float:1.8880372E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 2
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d5 }
            r2 = r3[r6]     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1568:
            r4 = r22
            java.lang.String r1 = "ChannelMessageAlbum"
            r2 = 2131624755(0x7f0e0333, float:1.8876699E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            goto L_0x183c
        L_0x157d:
            r4 = r22
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r2 = 0
            r5 = r3[r2]     // Catch:{ all -> 0x03d5 }
            r1[r2] = r5     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "Files"
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d5 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r3)     // Catch:{ all -> 0x03d5 }
            r1[r5] = r2     // Catch:{ all -> 0x03d5 }
            r2 = 2131624759(0x7f0e0337, float:1.8876707E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r2, r1)     // Catch:{ all -> 0x03d5 }
            goto L_0x183c
        L_0x15a3:
            r4 = r22
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r2 = 0
            r6 = r3[r2]     // Catch:{ all -> 0x03d5 }
            r1[r2] = r6     // Catch:{ all -> 0x03d5 }
            r2 = 1
            r3 = r3[r2]     // Catch:{ all -> 0x03d5 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d5 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d5 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)     // Catch:{ all -> 0x03d5 }
            r1[r2] = r3     // Catch:{ all -> 0x03d5 }
            r2 = 2131624759(0x7f0e0337, float:1.8876707E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r2, r1)     // Catch:{ all -> 0x03d5 }
            goto L_0x183c
        L_0x15c7:
            r4 = r22
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r2 = 0
            r5 = r3[r2]     // Catch:{ all -> 0x03d5 }
            r1[r2] = r5     // Catch:{ all -> 0x03d5 }
            r2 = 1
            r3 = r3[r2]     // Catch:{ all -> 0x03d5 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d5 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d5 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r15, r3)     // Catch:{ all -> 0x03d5 }
            r1[r2] = r3     // Catch:{ all -> 0x03d5 }
            r2 = 2131624759(0x7f0e0337, float:1.8876707E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r2, r1)     // Catch:{ all -> 0x03d5 }
            goto L_0x183c
        L_0x15eb:
            r4 = r22
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r2 = 0
            r5 = r3[r2]     // Catch:{ all -> 0x03d5 }
            r1[r2] = r5     // Catch:{ all -> 0x03d5 }
            r2 = 1
            r3 = r3[r2]     // Catch:{ all -> 0x03d5 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d5 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d5 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r11, r3)     // Catch:{ all -> 0x03d5 }
            r1[r2] = r3     // Catch:{ all -> 0x03d5 }
            r2 = 2131624759(0x7f0e0337, float:1.8876707E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r2, r1)     // Catch:{ all -> 0x03d5 }
            goto L_0x183c
        L_0x160f:
            r4 = r22
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r2 = 0
            r5 = r3[r2]     // Catch:{ all -> 0x03d5 }
            r1[r2] = r5     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "ForwardedMessageCount"
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d5 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r3)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = r2.toLowerCase()     // Catch:{ all -> 0x03d5 }
            r1[r5] = r2     // Catch:{ all -> 0x03d5 }
            r2 = 2131624759(0x7f0e0337, float:1.8876707E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r2, r1)     // Catch:{ all -> 0x03d5 }
            goto L_0x183c
        L_0x1639:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGame"
            r2 = 2131626546(0x7f0e0a32, float:1.8880331E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachGame"
            r3 = 2131624391(0x7f0e01c7, float:1.887596E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1657:
            r4 = r22
            java.lang.String r1 = "ChannelMessageGIF"
            r2 = 2131624760(0x7f0e0338, float:1.8876709E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachGif"
            r3 = 2131624392(0x7f0e01c8, float:1.8875962E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1675:
            r4 = r22
            java.lang.String r1 = "ChannelMessageLiveLocation"
            r2 = 2131624761(0x7f0e0339, float:1.887671E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachLiveLocation"
            r3 = 2131624397(0x7f0e01cd, float:1.8875973E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1693:
            r4 = r22
            java.lang.String r1 = "ChannelMessageMap"
            r2 = 2131624762(0x7f0e033a, float:1.8876713E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachLocation"
            r3 = 2131624401(0x7f0e01d1, float:1.887598E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x16b1:
            r4 = r22
            java.lang.String r1 = "ChannelMessagePoll2"
            r2 = 2131624766(0x7f0e033e, float:1.887672E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "Poll"
            r3 = 2131627116(0x7f0e0c6c, float:1.8881487E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x16d4:
            r4 = r22
            java.lang.String r1 = "ChannelMessageQuiz2"
            r2 = 2131624767(0x7f0e033f, float:1.8876723E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "QuizPoll"
            r3 = 2131627297(0x7f0e0d21, float:1.8881854E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x16f7:
            r4 = r22
            java.lang.String r1 = "ChannelMessageContact2"
            r2 = 2131624757(0x7f0e0335, float:1.8876703E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachContact"
            r3 = 2131624387(0x7f0e01c3, float:1.8875952E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x171a:
            r4 = r22
            java.lang.String r1 = "ChannelMessageAudio"
            r2 = 2131624756(0x7f0e0334, float:1.88767E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachAudio"
            r3 = 2131624385(0x7f0e01c1, float:1.8875948E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1738:
            r4 = r22
            int r1 = r3.length     // Catch:{ all -> 0x03d5 }
            r2 = 1
            if (r1 <= r2) goto L_0x1779
            r1 = r3[r2]     // Catch:{ all -> 0x03d5 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x03d5 }
            if (r1 != 0) goto L_0x1779
            java.lang.String r1 = "ChannelMessageStickerEmoji"
            r2 = 2131624770(0x7f0e0342, float:1.887673E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r7 = 0
            r8 = r3[r7]     // Catch:{ all -> 0x03d5 }
            r5[r7] = r8     // Catch:{ all -> 0x03d5 }
            r7 = 1
            r8 = r3[r7]     // Catch:{ all -> 0x03d5 }
            r5[r7] = r8     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03d5 }
            r2.<init>()     // Catch:{ all -> 0x03d5 }
            r3 = r3[r7]     // Catch:{ all -> 0x03d5 }
            r2.append(r3)     // Catch:{ all -> 0x03d5 }
            r2.append(r9)     // Catch:{ all -> 0x03d5 }
            r3 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)     // Catch:{ all -> 0x03d5 }
            r2.append(r3)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1779:
            java.lang.String r1 = "ChannelMessageSticker"
            r2 = 2131624769(0x7f0e0341, float:1.8876727E38)
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r7[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x03d5 }
            r2 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1793:
            r4 = r22
            java.lang.String r1 = "ChannelMessageDocument"
            r2 = 2131624758(0x7f0e0336, float:1.8876705E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachDocument"
            r3 = 2131624390(0x7f0e01c6, float:1.8875958E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x17b1:
            r4 = r22
            java.lang.String r1 = "ChannelMessageRound"
            r2 = 2131624768(0x7f0e0340, float:1.8876725E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachRound"
            r3 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x17cf:
            r4 = r22
            java.lang.String r1 = "ChannelMessageVideo"
            r2 = 2131624771(0x7f0e0343, float:1.8876731E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachVideo"
            r3 = 2131624411(0x7f0e01db, float:1.8876E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x17ed:
            r4 = r22
            java.lang.String r1 = "ChannelMessagePhoto"
            r2 = 2131624765(0x7f0e033d, float:1.8876719E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachPhoto"
            r3 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x180b:
            r4 = r22
            java.lang.String r1 = "ChannelMessageNoText"
            r2 = 2131624764(0x7f0e033c, float:1.8876717E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "Message"
            r3 = 2131626229(0x7f0e08f5, float:1.8879688E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1829:
            r4 = r22
            java.lang.String r1 = "NotificationMessageAlbum"
            r2 = 2131626540(0x7f0e0a2c, float:1.888032E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
        L_0x183c:
            r22 = r39
            r2 = 1
            goto L_0x1bbb
        L_0x1841:
            r4 = r22
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r2[r5] = r6     // Catch:{ all -> 0x03d5 }
            java.lang.String r5 = "Files"
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d5 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d5 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)     // Catch:{ all -> 0x03d5 }
            r2[r6] = r3     // Catch:{ all -> 0x03d5 }
            r3 = 2131626544(0x7f0e0a30, float:1.8880327E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r2)     // Catch:{ all -> 0x03d5 }
            goto L_0x183c
        L_0x1866:
            r4 = r22
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r2[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d5 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d5 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)     // Catch:{ all -> 0x03d5 }
            r2[r6] = r3     // Catch:{ all -> 0x03d5 }
            r3 = 2131626544(0x7f0e0a30, float:1.8880327E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r2)     // Catch:{ all -> 0x03d5 }
            goto L_0x183c
        L_0x1889:
            r4 = r22
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r2[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d5 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d5 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r15, r3)     // Catch:{ all -> 0x03d5 }
            r2[r5] = r3     // Catch:{ all -> 0x03d5 }
            r3 = 2131626544(0x7f0e0a30, float:1.8880327E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r2)     // Catch:{ all -> 0x03d5 }
            goto L_0x183c
        L_0x18ac:
            r4 = r22
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r2[r5] = r6     // Catch:{ all -> 0x03d5 }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d5 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d5 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r11, r3)     // Catch:{ all -> 0x03d5 }
            r2[r5] = r3     // Catch:{ all -> 0x03d5 }
            r3 = 2131626544(0x7f0e0a30, float:1.8880327E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r2)     // Catch:{ all -> 0x03d5 }
            goto L_0x183c
        L_0x18d0:
            r4 = r22
            r7 = r27
            java.lang.String r1 = "NotificationMessageForwardFew"
            r2 = 2131626545(0x7f0e0a31, float:1.888033E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r8 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r8     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03d5 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03d5 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r7, r3)     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            goto L_0x183c
        L_0x18f8:
            r4 = r22
            java.lang.String r1 = "NotificationMessageInvoice"
            r2 = 2131626568(0x7f0e0a48, float:1.8880376E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "PaymentInvoice"
            r3 = 2131626949(0x7f0e0bc5, float:1.8881149E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x191b:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGameScored"
            r5 = 2131626547(0x7f0e0a33, float:1.8880333E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r1[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03d5 }
            goto L_0x1aea
        L_0x193a:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGame"
            r2 = 2131626546(0x7f0e0a32, float:1.8880331E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachGame"
            r3 = 2131624391(0x7f0e01c7, float:1.887596E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x195d:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGif"
            r2 = 2131626548(0x7f0e0a34, float:1.8880335E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachGif"
            r3 = 2131624392(0x7f0e01c8, float:1.8875962E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x197b:
            r4 = r22
            java.lang.String r1 = "NotificationMessageLiveLocation"
            r2 = 2131626569(0x7f0e0a49, float:1.8880378E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachLiveLocation"
            r3 = 2131624397(0x7f0e01cd, float:1.8875973E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1999:
            r4 = r22
            java.lang.String r1 = "NotificationMessageMap"
            r2 = 2131626570(0x7f0e0a4a, float:1.888038E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachLocation"
            r3 = 2131624401(0x7f0e01d1, float:1.887598E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x19b7:
            r4 = r22
            java.lang.String r1 = "NotificationMessagePoll2"
            r2 = 2131626574(0x7f0e0a4e, float:1.8880388E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "Poll"
            r3 = 2131627116(0x7f0e0c6c, float:1.8881487E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x19da:
            r4 = r22
            java.lang.String r1 = "NotificationMessageQuiz2"
            r2 = 2131626575(0x7f0e0a4f, float:1.888039E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "QuizPoll"
            r3 = 2131627297(0x7f0e0d21, float:1.8881854E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x19fd:
            r4 = r22
            java.lang.String r1 = "NotificationMessageContact2"
            r2 = 2131626542(0x7f0e0a2e, float:1.8880323E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachContact"
            r3 = 2131624387(0x7f0e01c3, float:1.8875952E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1a20:
            r4 = r22
            java.lang.String r1 = "NotificationMessageAudio"
            r2 = 2131626541(0x7f0e0a2d, float:1.8880321E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachAudio"
            r3 = 2131624385(0x7f0e01c1, float:1.8875948E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1a3e:
            r4 = r22
            int r1 = r3.length     // Catch:{ all -> 0x03d5 }
            r2 = 1
            if (r1 <= r2) goto L_0x1a7f
            r1 = r3[r2]     // Catch:{ all -> 0x03d5 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x03d5 }
            if (r1 != 0) goto L_0x1a7f
            java.lang.String r1 = "NotificationMessageStickerEmoji"
            r2 = 2131626582(0x7f0e0a56, float:1.8880404E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r7 = 0
            r8 = r3[r7]     // Catch:{ all -> 0x03d5 }
            r5[r7] = r8     // Catch:{ all -> 0x03d5 }
            r7 = 1
            r8 = r3[r7]     // Catch:{ all -> 0x03d5 }
            r5[r7] = r8     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03d5 }
            r2.<init>()     // Catch:{ all -> 0x03d5 }
            r3 = r3[r7]     // Catch:{ all -> 0x03d5 }
            r2.append(r3)     // Catch:{ all -> 0x03d5 }
            r2.append(r9)     // Catch:{ all -> 0x03d5 }
            r3 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)     // Catch:{ all -> 0x03d5 }
            r2.append(r3)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1a7f:
            java.lang.String r1 = "NotificationMessageSticker"
            r2 = 2131626581(0x7f0e0a55, float:1.8880402E38)
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r7[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x03d5 }
            r2 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1a99:
            r4 = r22
            java.lang.String r1 = "NotificationMessageDocument"
            r2 = 2131626543(0x7f0e0a2f, float:1.8880325E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachDocument"
            r3 = 2131624390(0x7f0e01c6, float:1.8875958E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1ab7:
            r4 = r22
            java.lang.String r1 = "NotificationMessageRound"
            r2 = 2131626576(0x7f0e0a50, float:1.8880392E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachRound"
            r3 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1ad5:
            r4 = r22
            java.lang.String r1 = "ActionTakeScreenshoot"
            r2 = 2131624164(0x7f0e00e4, float:1.88755E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "un1"
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = r1.replace(r2, r3)     // Catch:{ all -> 0x03d5 }
        L_0x1aea:
            r22 = r39
            goto L_0x1bba
        L_0x1aee:
            r4 = r22
            java.lang.String r1 = "NotificationMessageSDVideo"
            r2 = 2131626578(0x7f0e0a52, float:1.8880396E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachDestructingVideo"
            r3 = 2131624389(0x7f0e01c5, float:1.8875956E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1b0c:
            r4 = r22
            java.lang.String r1 = "NotificationMessageVideo"
            r2 = 2131626584(0x7f0e0a58, float:1.8880408E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachVideo"
            r3 = 2131624411(0x7f0e01db, float:1.8876E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1b29:
            r4 = r22
            java.lang.String r1 = "NotificationMessageSDPhoto"
            r2 = 2131626577(0x7f0e0a51, float:1.8880394E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachDestructingPhoto"
            r3 = 2131624388(0x7f0e01c4, float:1.8875954E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1b46:
            r4 = r22
            java.lang.String r1 = "NotificationMessagePhoto"
            r2 = 2131626573(0x7f0e0a4d, float:1.8880386E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "AttachPhoto"
            r3 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1b63:
            r4 = r22
            java.lang.String r1 = "NotificationMessageNoText"
            r2 = 2131626572(0x7f0e0a4c, float:1.8880384E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03d5 }
            r6[r5] = r3     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "Message"
            r3 = 2131626229(0x7f0e08f5, float:1.8879688E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03d5 }
        L_0x1b7f:
            r17 = r2
            r22 = r39
            r2 = 0
            goto L_0x1bbd
        L_0x1b85:
            r4 = r22
            java.lang.String r1 = "NotificationMessageText"
            r2 = 2131626583(0x7f0e0a57, float:1.8880406E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03d5 }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03d5 }
            r5[r6] = r7     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03d5 }
            r2 = r3[r6]     // Catch:{ all -> 0x03d5 }
            goto L_0x1b7f
        L_0x1ba0:
            if (r1 == 0) goto L_0x1bb7
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x03d5 }
            r1.<init>()     // Catch:{ all -> 0x03d5 }
            java.lang.String r2 = "unhandled loc_key = "
            r1.append(r2)     // Catch:{ all -> 0x03d5 }
            r1.append(r14)     // Catch:{ all -> 0x03d5 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x03d5 }
            org.telegram.messenger.FileLog.w(r1)     // Catch:{ all -> 0x03d5 }
        L_0x1bb7:
            r22 = r39
            r1 = 0
        L_0x1bba:
            r2 = 0
        L_0x1bbb:
            r17 = 0
        L_0x1bbd:
            if (r1 == 0) goto L_0x1c9b
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x1c9e }
            r3.<init>()     // Catch:{ all -> 0x1c9e }
            r3.id = r10     // Catch:{ all -> 0x1c9e }
            r5 = r30
            r3.random_id = r5     // Catch:{ all -> 0x1c9e }
            if (r17 == 0) goto L_0x1bcf
            r5 = r17
            goto L_0x1bd0
        L_0x1bcf:
            r5 = r1
        L_0x1bd0:
            r3.message = r5     // Catch:{ all -> 0x1c9e }
            r5 = 1000(0x3e8, double:4.94E-321)
            long r5 = r51 / r5
            int r6 = (int) r5     // Catch:{ all -> 0x1c9e }
            r3.date = r6     // Catch:{ all -> 0x1c9e }
            if (r38 == 0) goto L_0x1be2
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r5 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x03d5 }
            r5.<init>()     // Catch:{ all -> 0x03d5 }
            r3.action = r5     // Catch:{ all -> 0x03d5 }
        L_0x1be2:
            if (r37 == 0) goto L_0x1beb
            int r5 = r3.flags     // Catch:{ all -> 0x03d5 }
            r6 = -2147483648(0xfffffffvar_, float:-0.0)
            r5 = r5 | r6
            r3.flags = r5     // Catch:{ all -> 0x03d5 }
        L_0x1beb:
            r3.dialog_id = r12     // Catch:{ all -> 0x1c9e }
            r5 = 0
            int r7 = (r45 > r5 ? 1 : (r45 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_peerChannel r5 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x03d5 }
            r5.<init>()     // Catch:{ all -> 0x03d5 }
            r3.peer_id = r5     // Catch:{ all -> 0x03d5 }
            r6 = r45
            r5.channel_id = r6     // Catch:{ all -> 0x03d5 }
            r12 = r25
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r5 = 0
            int r7 = (r25 > r5 ? 1 : (r25 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_peerChat r5 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x03d5 }
            r5.<init>()     // Catch:{ all -> 0x03d5 }
            r3.peer_id = r5     // Catch:{ all -> 0x03d5 }
            r12 = r25
            r5.chat_id = r12     // Catch:{ all -> 0x03d5 }
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r12 = r25
            org.telegram.tgnet.TLRPC$TL_peerUser r5 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1c9e }
            r5.<init>()     // Catch:{ all -> 0x1c9e }
            r3.peer_id = r5     // Catch:{ all -> 0x1c9e }
            r6 = r23
            r5.user_id = r6     // Catch:{ all -> 0x1c9e }
        L_0x1CLASSNAME:
            int r5 = r3.flags     // Catch:{ all -> 0x1c9e }
            r5 = r5 | 256(0x100, float:3.59E-43)
            r3.flags = r5     // Catch:{ all -> 0x1c9e }
            r5 = 0
            int r7 = (r43 > r5 ? 1 : (r43 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_peerChat r5 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x03d5 }
            r5.<init>()     // Catch:{ all -> 0x03d5 }
            r3.from_id = r5     // Catch:{ all -> 0x03d5 }
            r5.chat_id = r12     // Catch:{ all -> 0x03d5 }
            goto L_0x1c5e
        L_0x1CLASSNAME:
            r5 = 0
            int r7 = (r32 > r5 ? 1 : (r32 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_peerChannel r5 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x03d5 }
            r5.<init>()     // Catch:{ all -> 0x03d5 }
            r3.from_id = r5     // Catch:{ all -> 0x03d5 }
            r6 = r32
            r5.channel_id = r6     // Catch:{ all -> 0x03d5 }
            goto L_0x1c5e
        L_0x1CLASSNAME:
            r5 = 0
            int r7 = (r35 > r5 ? 1 : (r35 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x1c5a
            org.telegram.tgnet.TLRPC$TL_peerUser r5 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x03d5 }
            r5.<init>()     // Catch:{ all -> 0x03d5 }
            r3.from_id = r5     // Catch:{ all -> 0x03d5 }
            r6 = r35
            r5.user_id = r6     // Catch:{ all -> 0x03d5 }
            goto L_0x1c5e
        L_0x1c5a:
            org.telegram.tgnet.TLRPC$Peer r5 = r3.peer_id     // Catch:{ all -> 0x1c9e }
            r3.from_id = r5     // Catch:{ all -> 0x1c9e }
        L_0x1c5e:
            if (r29 != 0) goto L_0x1CLASSNAME
            if (r38 == 0) goto L_0x1CLASSNAME
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r5 = 0
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r5 = 1
        L_0x1CLASSNAME:
            r3.mentioned = r5     // Catch:{ all -> 0x1c9e }
            r5 = r34
            r3.silent = r5     // Catch:{ all -> 0x1c9e }
            r3.from_scheduled = r4     // Catch:{ all -> 0x1c9e }
            org.telegram.messenger.MessageObject r4 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x1c9e }
            r18 = r4
            r19 = r28
            r20 = r3
            r21 = r1
            r23 = r42
            r24 = r2
            r25 = r41
            r26 = r37
            r27 = r40
            r18.<init>(r19, r20, r21, r22, r23, r24, r25, r26, r27)     // Catch:{ all -> 0x1c9e }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x1c9e }
            r1.<init>()     // Catch:{ all -> 0x1c9e }
            r1.add(r4)     // Catch:{ all -> 0x1c9e }
            org.telegram.messenger.NotificationsController r2 = org.telegram.messenger.NotificationsController.getInstance(r28)     // Catch:{ all -> 0x1c9e }
            r3 = r49
            java.util.concurrent.CountDownLatch r4 = r3.countDownLatch     // Catch:{ all -> 0x1cba }
            r5 = 1
            r2.processNewMessages(r1, r5, r5, r4)     // Catch:{ all -> 0x1cba }
            r8 = 0
            goto L_0x1ca7
        L_0x1c9b:
            r3 = r49
            goto L_0x1ca6
        L_0x1c9e:
            r0 = move-exception
            r3 = r49
            goto L_0x1cc4
        L_0x1ca2:
            r0 = move-exception
            r3 = r1
            goto L_0x1cc4
        L_0x1ca5:
            r3 = r1
        L_0x1ca6:
            r8 = 1
        L_0x1ca7:
            if (r8 == 0) goto L_0x1cae
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1cba }
            r1.countDown()     // Catch:{ all -> 0x1cba }
        L_0x1cae:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r28)     // Catch:{ all -> 0x1cba }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r28)     // Catch:{ all -> 0x1cba }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1cba }
            goto L_0x1dec
        L_0x1cba:
            r0 = move-exception
            goto L_0x1cc4
        L_0x1cbc:
            r0 = move-exception
            r3 = r1
            r14 = r29
            goto L_0x1cc4
        L_0x1cc1:
            r0 = move-exception
            r3 = r1
            r14 = r9
        L_0x1cc4:
            r1 = r0
            r7 = r16
            r4 = r28
            goto L_0x1d9a
        L_0x1ccb:
            r0 = move-exception
            r3 = r1
            r28 = r4
            r14 = r9
            goto L_0x1d90
        L_0x1cd2:
            r0 = move-exception
            r3 = r1
            r28 = r4
            goto L_0x1d96
        L_0x1cd8:
            r3 = r1
            r28 = r4
            r16 = r7
            r14 = r9
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1cf0 }
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1 r2 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1     // Catch:{ all -> 0x1cf0 }
            r4 = r28
            r2.<init>(r4)     // Catch:{ all -> 0x1d8f }
            r1.postRunnable(r2)     // Catch:{ all -> 0x1d8f }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d8f }
            r1.countDown()     // Catch:{ all -> 0x1d8f }
            return
        L_0x1cf0:
            r0 = move-exception
            r4 = r28
            goto L_0x1d90
        L_0x1cf5:
            r3 = r1
            r16 = r7
            r14 = r9
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0 r1 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0     // Catch:{ all -> 0x1d8f }
            r1.<init>(r4)     // Catch:{ all -> 0x1d8f }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ all -> 0x1d8f }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d8f }
            r1.countDown()     // Catch:{ all -> 0x1d8f }
            return
        L_0x1d07:
            r3 = r1
            r16 = r7
            r14 = r9
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x1d8f }
            r1.<init>()     // Catch:{ all -> 0x1d8f }
            r2 = 0
            r1.popup = r2     // Catch:{ all -> 0x1d8f }
            r2 = 2
            r1.flags = r2     // Catch:{ all -> 0x1d8f }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r6 = r51 / r6
            int r2 = (int) r6     // Catch:{ all -> 0x1d8f }
            r1.inbox_date = r2     // Catch:{ all -> 0x1d8f }
            java.lang.String r2 = "message"
            java.lang.String r2 = r5.getString(r2)     // Catch:{ all -> 0x1d8f }
            r1.message = r2     // Catch:{ all -> 0x1d8f }
            java.lang.String r2 = "announcement"
            r1.type = r2     // Catch:{ all -> 0x1d8f }
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ all -> 0x1d8f }
            r2.<init>()     // Catch:{ all -> 0x1d8f }
            r1.media = r2     // Catch:{ all -> 0x1d8f }
            org.telegram.tgnet.TLRPC$TL_updates r2 = new org.telegram.tgnet.TLRPC$TL_updates     // Catch:{ all -> 0x1d8f }
            r2.<init>()     // Catch:{ all -> 0x1d8f }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r5 = r2.updates     // Catch:{ all -> 0x1d8f }
            r5.add(r1)     // Catch:{ all -> 0x1d8f }
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1d8f }
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3 r5 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3     // Catch:{ all -> 0x1d8f }
            r5.<init>(r4, r2)     // Catch:{ all -> 0x1d8f }
            r1.postRunnable(r5)     // Catch:{ all -> 0x1d8f }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1d8f }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1d8f }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d8f }
            r1.countDown()     // Catch:{ all -> 0x1d8f }
            return
        L_0x1d51:
            r3 = r1
            r16 = r7
            r14 = r9
            java.lang.String r1 = "dc"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x1d8f }
            java.lang.String r2 = "addr"
            java.lang.String r2 = r11.getString(r2)     // Catch:{ all -> 0x1d8f }
            java.lang.String r5 = ":"
            java.lang.String[] r2 = r2.split(r5)     // Catch:{ all -> 0x1d8f }
            int r5 = r2.length     // Catch:{ all -> 0x1d8f }
            r6 = 2
            if (r5 == r6) goto L_0x1d71
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d8f }
            r1.countDown()     // Catch:{ all -> 0x1d8f }
            return
        L_0x1d71:
            r5 = 0
            r5 = r2[r5]     // Catch:{ all -> 0x1d8f }
            r6 = 1
            r2 = r2[r6]     // Catch:{ all -> 0x1d8f }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ all -> 0x1d8f }
            org.telegram.tgnet.ConnectionsManager r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1d8f }
            r6.applyDatacenterAddress(r1, r5, r2)     // Catch:{ all -> 0x1d8f }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1d8f }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1d8f }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d8f }
            r1.countDown()     // Catch:{ all -> 0x1d8f }
            return
        L_0x1d8f:
            r0 = move-exception
        L_0x1d90:
            r1 = r0
            r7 = r16
            goto L_0x1d9a
        L_0x1d94:
            r0 = move-exception
            r3 = r1
        L_0x1d96:
            r16 = r7
            r14 = r9
            r1 = r0
        L_0x1d9a:
            r2 = -1
            goto L_0x1db4
        L_0x1d9c:
            r0 = move-exception
            r3 = r1
            r16 = r7
            r14 = r9
            r1 = r0
            r2 = -1
            r4 = -1
            goto L_0x1db4
        L_0x1da5:
            r0 = move-exception
            r3 = r1
            r16 = r7
            r1 = r0
            r2 = -1
            r4 = -1
            goto L_0x1db3
        L_0x1dad:
            r0 = move-exception
            r3 = r1
            r1 = r0
            r2 = -1
            r4 = -1
            r7 = 0
        L_0x1db3:
            r14 = 0
        L_0x1db4:
            if (r4 == r2) goto L_0x1dc6
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r4)
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
            r2.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r2 = r3.countDownLatch
            r2.countDown()
            goto L_0x1dc9
        L_0x1dc6:
            r49.onDecryptError()
        L_0x1dc9:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x1de9
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
        L_0x1de9:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1dec:
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
