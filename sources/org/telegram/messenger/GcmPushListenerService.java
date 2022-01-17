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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v31, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v44, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v46, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v50, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v44, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v45, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v45, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v46, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v48, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v78, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v82, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v87, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v92, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v97, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v108, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v92, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v165, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v134, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v137, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v141, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v145, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v149, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v186, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v153, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v189, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v155, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v195, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v198, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v201, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v205, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v206, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v166, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v210, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v48, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v169, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v216, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v153, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v173, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v219, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v175, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v222, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v177, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v225, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v228, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v181, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v231, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v234, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v170, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v238, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v190, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v154, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v196, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v158, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v198, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v78, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v79, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v200, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v201, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v82, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v202, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v206, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v206, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v209, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v211, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v212, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v216, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v167, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v221, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v226, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v227, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v228, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v229, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v232, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v196, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v201, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v208, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v210, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v217, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v244, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v264, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v101, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v280, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v276, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v214, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v252, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v108, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v290, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v256, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v257, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v297, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v258, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v260, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v232, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v262, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v256, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v263, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v143, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v264, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v266, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v268, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v266, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v269, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v270, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v272, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v273, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v166, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v274, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v276, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v275, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v276, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v173, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v244, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v277, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v281, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v278, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v178, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v279, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v280, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v287, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v288, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v289, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v292, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v284, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v285, resolved type: java.lang.String[]} */
    /* JADX WARNING: type inference failed for: r1v7 */
    /* JADX WARNING: type inference failed for: r1v358 */
    /* JADX WARNING: type inference failed for: r1v359 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:1010:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x01ec, code lost:
        if (r2 == 0) goto L_0x1d92;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:117:0x01ee, code lost:
        if (r2 == 1) goto L_0x1d49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:118:0x01f0, code lost:
        if (r2 == 2) goto L_0x1d38;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x01f2, code lost:
        if (r2 == 3) goto L_0x1d1c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x01fc, code lost:
        if (r11.has("channel_id") == false) goto L_0x0211;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:0x01fe, code lost:
        r16 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:127:?, code lost:
        r6 = r11.getLong("channel_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x0206, code lost:
        r12 = -r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x0208, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x0209, code lost:
        r2 = r0;
        r14 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x0211, code lost:
        r16 = r7;
        r6 = 0;
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x021b, code lost:
        if (r11.has("from_id") == false) goto L_0x0227;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:?, code lost:
        r12 = r11.getLong("from_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:0x0223, code lost:
        r28 = r4;
        r3 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x0227, code lost:
        r28 = r4;
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0230, code lost:
        if (r11.has("chat_id") == false) goto L_0x024d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:?, code lost:
        r12 = r11.getLong("chat_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x0238, code lost:
        r29 = r9;
        r8 = r12;
        r12 = -r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:0x0241, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:0x0242, code lost:
        r29 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x0244, code lost:
        r2 = r0;
        r7 = r16;
        r4 = r28;
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x024d, code lost:
        r29 = r9;
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x0256, code lost:
        if (r11.has("encryption_id") == false) goto L_0x0266;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:159:?, code lost:
        r12 = org.telegram.messenger.DialogObject.makeEncryptedDialogId((long) r11.getInt("encryption_id"));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x0264, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:164:0x026c, code lost:
        if (r11.has("schedule") == false) goto L_0x0278;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x0274, code lost:
        if (r11.getInt("schedule") != 1) goto L_0x0278;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:169:0x0276, code lost:
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x0278, code lost:
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x027b, code lost:
        if (r12 != 0) goto L_0x028a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x027d, code lost:
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x0285, code lost:
        if ("ENCRYPTED_MESSAGE".equals(r14) == false) goto L_0x028c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:177:0x0287, code lost:
        r12 = org.telegram.messenger.NotificationsController.globalSecretChatId;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x028a, code lost:
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x0290, code lost:
        if (r12 == 0) goto L_0x1cf0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:183:0x029a, code lost:
        if ("READ_HISTORY".equals(r14) == false) goto L_0x030e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:?, code lost:
        r2 = r11.getInt("max_id");
        r5 = new java.util.ArrayList();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x02a9, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x02c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x02ab, code lost:
        org.telegram.messenger.FileLog.d("GCM received read notification max_id = " + r2 + " for dialogId = " + r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x02c9, code lost:
        if (r6 == 0) goto L_0x02d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x02cb, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox();
        r3.channel_id = r6;
        r3.max_id = r2;
        r5.add(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x02d8, code lost:
        r6 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x02e1, code lost:
        if (r3 == 0) goto L_0x02ed;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x02e3, code lost:
        r7 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r6.peer = r7;
        r7.user_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x02ed, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r6.peer = r3;
        r3.chat_id = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x02f6, code lost:
        r6.max_id = r2;
        r5.add(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x02fb, code lost:
        org.telegram.messenger.MessagesController.getInstance(r28).processUpdateArray(r5, (java.util.ArrayList<org.telegram.tgnet.TLRPC$User>) null, (java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat>) null, false, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x0314, code lost:
        r22 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x0318, code lost:
        if ("MESSAGE_DELETED".equals(r14) == false) goto L_0x0384;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:?, code lost:
        r2 = r11.getString("messages").split(",");
        r3 = new androidx.collection.LongSparseArray();
        r4 = new java.util.ArrayList();
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x0330, code lost:
        if (r8 >= r2.length) goto L_0x033e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x0332, code lost:
        r4.add(org.telegram.messenger.Utilities.parseInt(r2[r8]));
        r8 = r8 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x033e, code lost:
        r3.put(-r6, r4);
        org.telegram.messenger.NotificationsController.getInstance(r28).removeDeletedMessagesFromNotifications(r3);
        org.telegram.messenger.MessagesController.getInstance(r28).deleteMessagesByPush(r12, r4, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x0358, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1cf0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x035a, code lost:
        org.telegram.messenger.FileLog.d("GCM received " + r14 + " for dialogId = " + r12 + " mids = " + android.text.TextUtils.join(",", r4));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x0388, code lost:
        if (android.text.TextUtils.isEmpty(r14) != false) goto L_0x1cf0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x0390, code lost:
        if (r11.has("msg_id") == false) goto L_0x039b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x0392, code lost:
        r10 = r11.getInt("msg_id");
        r23 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x039b, code lost:
        r23 = r3;
        r10 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x03a4, code lost:
        if (r11.has("random_id") == false) goto L_0x03b5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x03a6, code lost:
        r3 = org.telegram.messenger.Utilities.parseLong(r11.getString("random_id")).longValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:218:0x03b5, code lost:
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x03b7, code lost:
        if (r10 == 0) goto L_0x03f9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:220:0x03b9, code lost:
        r25 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:?, code lost:
        r8 = org.telegram.messenger.MessagesController.getInstance(r28).dialogs_read_inbox_max.get(java.lang.Long.valueOf(r12));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x03cb, code lost:
        if (r8 != null) goto L_0x03e7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:0x03cd, code lost:
        r8 = java.lang.Integer.valueOf(org.telegram.messenger.MessagesStorage.getInstance(r28).getDialogReadMax(false, r12));
        org.telegram.messenger.MessagesController.getInstance(r28).dialogs_read_inbox_max.put(java.lang.Long.valueOf(r12), r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x03eb, code lost:
        if (r10 <= r8.intValue()) goto L_0x040d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x03ee, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x03ef, code lost:
        r3 = -1;
        r1 = r49;
        r2 = r0;
        r7 = r16;
        r4 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x03f9, code lost:
        r25 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x03ff, code lost:
        if (r3 == 0) goto L_0x040d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x0409, code lost:
        if (org.telegram.messenger.MessagesStorage.getInstance(r28).checkMessageByRandomId(r3) != false) goto L_0x040d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x040b, code lost:
        r1 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:0x040d, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x0410, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x0414, code lost:
        if (r14.startsWith("REACT_") != false) goto L_0x041e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:?, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x041c, code lost:
        if (r14.startsWith("CHAT_REACT_") == false) goto L_0x041f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x041e, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x041f, code lost:
        if (r1 == false) goto L_0x1ce7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x0421, code lost:
        r30 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:?, code lost:
        r3 = r11.optLong("chat_from_id", 0);
        r27 = "messages";
        r32 = r11.optLong("chat_from_broadcast_id", 0);
        r1 = r11.optLong("chat_from_group_id", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x043d, code lost:
        if (r3 != 0) goto L_0x0446;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x0441, code lost:
        if (r1 == 0) goto L_0x0444;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x0444, code lost:
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x0446, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x044d, code lost:
        if (r11.has("mention") == false) goto L_0x045a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x0455, code lost:
        if (r11.getInt("mention") == 0) goto L_0x045a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x0457, code lost:
        r29 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x045a, code lost:
        r29 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x0462, code lost:
        if (r11.has("silent") == false) goto L_0x046f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x046a, code lost:
        if (r11.getInt("silent") == 0) goto L_0x046f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x046c, code lost:
        r34 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x046f, code lost:
        r34 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0477, code lost:
        if (r5.has("loc_args") == false) goto L_0x0493;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:?, code lost:
        r5 = r5.getJSONArray("loc_args");
        r9 = r5.length();
        r35 = r3;
        r3 = new java.lang.String[r9];
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0488, code lost:
        if (r4 >= r9) goto L_0x0496;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x048a, code lost:
        r3[r4] = r5.getString(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0490, code lost:
        r4 = r4 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x0493, code lost:
        r35 = r3;
        r3 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:?, code lost:
        r5 = r3[0];
        r4 = r11.has("edit_date");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x04a5, code lost:
        if (r14.startsWith("CHAT_") == false) goto L_0x04dc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x04ab, code lost:
        if (org.telegram.messenger.UserObject.isReplyUser(r12) == false) goto L_0x04c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x04ad, code lost:
        r5 = r5 + " @ " + r3[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x04c9, code lost:
        if (r6 == 0) goto L_0x04cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x04cb, code lost:
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x04cd, code lost:
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x04d1, code lost:
        r11 = false;
        r38 = false;
        r47 = r9;
        r9 = r5;
        r5 = r3[1];
        r37 = r47;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x04e2, code lost:
        if (r14.startsWith("PINNED_") == false) goto L_0x04f4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x04e8, code lost:
        if (r6 == 0) goto L_0x04ec;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x04ea, code lost:
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x04ec, code lost:
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x04ed, code lost:
        r37 = r9;
        r9 = null;
        r11 = false;
        r38 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x04fa, code lost:
        if (r14.startsWith("CHANNEL_") == false) goto L_0x04ff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x04fc, code lost:
        r9 = null;
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x04ff, code lost:
        r9 = null;
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:311:0x0501, code lost:
        r37 = false;
        r38 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x0507, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x0530;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x0509, code lost:
        r39 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:?, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x0530, code lost:
        r40 = r4;
        r39 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x053a, code lost:
        if (r14.startsWith("REACT_") != false) goto L_0x1bf6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x0542, code lost:
        if (r14.startsWith("CHAT_REACT_") == false) goto L_0x0546;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x054a, code lost:
        switch(r14.hashCode()) {
            case -2100047043: goto L_0x0a8b;
            case -2091498420: goto L_0x0a80;
            case -2053872415: goto L_0x0a75;
            case -2039746363: goto L_0x0a6a;
            case -2023218804: goto L_0x0a5f;
            case -1979538588: goto L_0x0a54;
            case -1979536003: goto L_0x0a49;
            case -1979535888: goto L_0x0a3e;
            case -1969004705: goto L_0x0a33;
            case -1946699248: goto L_0x0a27;
            case -1717283471: goto L_0x0a1b;
            case -1646640058: goto L_0x0a0f;
            case -1528047021: goto L_0x0a03;
            case -1493579426: goto L_0x09f7;
            case -1482481933: goto L_0x09eb;
            case -1480102982: goto L_0x09e0;
            case -1478041834: goto L_0x09d4;
            case -1474543101: goto L_0x09c9;
            case -1465695932: goto L_0x09bd;
            case -1374906292: goto L_0x09b1;
            case -1372940586: goto L_0x09a5;
            case -1264245338: goto L_0x0999;
            case -1236154001: goto L_0x098d;
            case -1236086700: goto L_0x0981;
            case -1236077786: goto L_0x0975;
            case -1235796237: goto L_0x0969;
            case -1235760759: goto L_0x095d;
            case -1235686303: goto L_0x0952;
            case -1198046100: goto L_0x0947;
            case -1124254527: goto L_0x093b;
            case -1085137927: goto L_0x092f;
            case -1084856378: goto L_0x0923;
            case -1084820900: goto L_0x0917;
            case -1084746444: goto L_0x090b;
            case -819729482: goto L_0x08ff;
            case -772141857: goto L_0x08f3;
            case -638310039: goto L_0x08e7;
            case -590403924: goto L_0x08db;
            case -589196239: goto L_0x08cf;
            case -589193654: goto L_0x08c3;
            case -589193539: goto L_0x08b7;
            case -440169325: goto L_0x08ab;
            case -412748110: goto L_0x089f;
            case -228518075: goto L_0x0893;
            case -213586509: goto L_0x0887;
            case -115582002: goto L_0x087b;
            case -112621464: goto L_0x086f;
            case -108522133: goto L_0x0863;
            case -107572034: goto L_0x0858;
            case -40534265: goto L_0x084c;
            case 52369421: goto L_0x0840;
            case 65254746: goto L_0x0834;
            case 141040782: goto L_0x0828;
            case 202550149: goto L_0x081c;
            case 309993049: goto L_0x0810;
            case 309995634: goto L_0x0804;
            case 309995749: goto L_0x07f8;
            case 320532812: goto L_0x07ec;
            case 328933854: goto L_0x07e0;
            case 331340546: goto L_0x07d4;
            case 342406591: goto L_0x07c8;
            case 344816990: goto L_0x07bc;
            case 346878138: goto L_0x07b0;
            case 350376871: goto L_0x07a4;
            case 608430149: goto L_0x0798;
            case 615714517: goto L_0x078d;
            case 715508879: goto L_0x0781;
            case 728985323: goto L_0x0775;
            case 731046471: goto L_0x0769;
            case 734545204: goto L_0x075d;
            case 802032552: goto L_0x0751;
            case 991498806: goto L_0x0745;
            case 1007364121: goto L_0x0739;
            case 1019850010: goto L_0x072d;
            case 1019917311: goto L_0x0721;
            case 1019926225: goto L_0x0715;
            case 1020207774: goto L_0x0709;
            case 1020243252: goto L_0x06fd;
            case 1020317708: goto L_0x06f1;
            case 1060282259: goto L_0x06e5;
            case 1060349560: goto L_0x06d9;
            case 1060358474: goto L_0x06cd;
            case 1060640023: goto L_0x06c1;
            case 1060675501: goto L_0x06b5;
            case 1060749957: goto L_0x06aa;
            case 1073049781: goto L_0x069e;
            case 1078101399: goto L_0x0692;
            case 1110103437: goto L_0x0686;
            case 1160762272: goto L_0x067a;
            case 1172918249: goto L_0x066e;
            case 1234591620: goto L_0x0662;
            case 1281128640: goto L_0x0656;
            case 1281131225: goto L_0x064a;
            case 1281131340: goto L_0x063e;
            case 1310789062: goto L_0x0633;
            case 1333118583: goto L_0x0627;
            case 1361447897: goto L_0x061b;
            case 1498266155: goto L_0x060f;
            case 1533804208: goto L_0x0603;
            case 1540131626: goto L_0x05f7;
            case 1547988151: goto L_0x05eb;
            case 1561464595: goto L_0x05df;
            case 1563525743: goto L_0x05d3;
            case 1567024476: goto L_0x05c7;
            case 1810705077: goto L_0x05bb;
            case 1815177512: goto L_0x05af;
            case 1954774321: goto L_0x05a3;
            case 1963241394: goto L_0x0597;
            case 2014789757: goto L_0x058b;
            case 2022049433: goto L_0x057f;
            case 2034984710: goto L_0x0573;
            case 2048733346: goto L_0x0567;
            case 2099392181: goto L_0x055b;
            case 2140162142: goto L_0x054f;
            default: goto L_0x054d;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x0555, code lost:
        if (r14.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x0557, code lost:
        r4 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x0561, code lost:
        if (r14.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x0563, code lost:
        r4 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x056d, code lost:
        if (r14.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x056f, code lost:
        r4 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x0579, code lost:
        if (r14.equals("CHANNEL_MESSAGE_PLAYLIST") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x057b, code lost:
        r4 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x0585, code lost:
        if (r14.equals("PINNED_CONTACT") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x0587, code lost:
        r4 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x0591, code lost:
        if (r14.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x0593, code lost:
        r4 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x059d, code lost:
        if (r14.equals("LOCKED_MESSAGE") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x059f, code lost:
        r4 = 'l';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x05a9, code lost:
        if (r14.equals("CHAT_MESSAGE_PLAYLIST") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x05ab, code lost:
        r4 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x05b5, code lost:
        if (r14.equals("CHANNEL_MESSAGES") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x05b7, code lost:
        r4 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x05c1, code lost:
        if (r14.equals("MESSAGE_INVOICE") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x05c3, code lost:
        r4 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x05cd, code lost:
        if (r14.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x05cf, code lost:
        r4 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x05d9, code lost:
        if (r14.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x05db, code lost:
        r4 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x05e5, code lost:
        if (r14.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x05e7, code lost:
        r4 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x05f1, code lost:
        if (r14.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x05f3, code lost:
        r4 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x05fd, code lost:
        if (r14.equals("MESSAGE_PLAYLIST") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x05ff, code lost:
        r4 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x0609, code lost:
        if (r14.equals("MESSAGE_VIDEOS") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x060b, code lost:
        r4 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x0615, code lost:
        if (r14.equals("PHONE_CALL_MISSED") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x0617, code lost:
        r4 = 'q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x0621, code lost:
        if (r14.equals("MESSAGE_PHOTOS") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x0623, code lost:
        r4 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x062d, code lost:
        if (r14.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x062f, code lost:
        r4 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x0639, code lost:
        if (r14.equals("MESSAGE_NOTEXT") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x063b, code lost:
        r4 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x0644, code lost:
        if (r14.equals("MESSAGE_GIF") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x0646, code lost:
        r4 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x0650, code lost:
        if (r14.equals("MESSAGE_GEO") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x0652, code lost:
        r4 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x065c, code lost:
        if (r14.equals("MESSAGE_DOC") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x065e, code lost:
        r4 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:400:0x0668, code lost:
        if (r14.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x066a, code lost:
        r4 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0674, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x0676, code lost:
        r4 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x0680, code lost:
        if (r14.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x0682, code lost:
        r4 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x068c, code lost:
        if (r14.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x068e, code lost:
        r4 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0698, code lost:
        if (r14.equals("CHAT_TITLE_EDITED") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x069a, code lost:
        r4 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x06a4, code lost:
        if (r14.equals("PINNED_NOTEXT") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x06a6, code lost:
        r4 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x06b0, code lost:
        if (r14.equals("MESSAGE_TEXT") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x06b2, code lost:
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x06bb, code lost:
        if (r14.equals("MESSAGE_QUIZ") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x06bd, code lost:
        r4 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x06c7, code lost:
        if (r14.equals("MESSAGE_POLL") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x06c9, code lost:
        r4 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x06d3, code lost:
        if (r14.equals("MESSAGE_GAME") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x06d5, code lost:
        r4 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x06df, code lost:
        if (r14.equals("MESSAGE_FWDS") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x06e1, code lost:
        r4 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x06eb, code lost:
        if (r14.equals("MESSAGE_DOCS") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x06ed, code lost:
        r4 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x06f7, code lost:
        if (r14.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x06f9, code lost:
        r4 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x0703, code lost:
        if (r14.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x0705, code lost:
        r4 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x070f, code lost:
        if (r14.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x0711, code lost:
        r4 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x071b, code lost:
        if (r14.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x071d, code lost:
        r4 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x0727, code lost:
        if (r14.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x0729, code lost:
        r4 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x0733, code lost:
        if (r14.equals("CHAT_MESSAGE_DOCS") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x0735, code lost:
        r4 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x073f, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x0741, code lost:
        r4 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x074b, code lost:
        if (r14.equals("PINNED_GEOLIVE") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:458:0x074d, code lost:
        r4 = 'b';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:460:0x0757, code lost:
        if (r14.equals("MESSAGE_CONTACT") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x0759, code lost:
        r4 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x0763, code lost:
        if (r14.equals("PINNED_VIDEO") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x0765, code lost:
        r4 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x076f, code lost:
        if (r14.equals("PINNED_ROUND") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x0771, code lost:
        r4 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x077b, code lost:
        if (r14.equals("PINNED_PHOTO") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x077d, code lost:
        r4 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:472:0x0787, code lost:
        if (r14.equals("PINNED_AUDIO") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0789, code lost:
        r4 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0793, code lost:
        if (r14.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x0795, code lost:
        r4 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x079e, code lost:
        if (r14.equals("CHAT_VOICECHAT_INVITE_YOU") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x07a0, code lost:
        r4 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x07aa, code lost:
        if (r14.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x07ac, code lost:
        r4 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x07b6, code lost:
        if (r14.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x07b8, code lost:
        r4 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x07c2, code lost:
        if (r14.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x07c4, code lost:
        r4 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x07ce, code lost:
        if (r14.equals("CHAT_VOICECHAT_END") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x07d0, code lost:
        r4 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x07da, code lost:
        if (r14.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x07dc, code lost:
        r4 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x07e6, code lost:
        if (r14.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x07e8, code lost:
        r4 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x07f2, code lost:
        if (r14.equals("MESSAGES") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x07f4, code lost:
        r4 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x07fe, code lost:
        if (r14.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x0800, code lost:
        r4 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x080a, code lost:
        if (r14.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x080c, code lost:
        r4 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x0816, code lost:
        if (r14.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x0818, code lost:
        r4 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x0822, code lost:
        if (r14.equals("CHAT_VOICECHAT_INVITE") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x0824, code lost:
        r4 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x082e, code lost:
        if (r14.equals("CHAT_LEFT") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x0830, code lost:
        r4 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x083a, code lost:
        if (r14.equals("CHAT_ADD_YOU") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x083c, code lost:
        r4 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0846, code lost:
        if (r14.equals("REACT_TEXT") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x0848, code lost:
        r4 = 'h';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x0852, code lost:
        if (r14.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0854, code lost:
        r4 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x085e, code lost:
        if (r14.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0860, code lost:
        r4 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0869, code lost:
        if (r14.equals("AUTH_REGION") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x086b, code lost:
        r4 = 'k';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x0875, code lost:
        if (r14.equals("CONTACT_JOINED") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0877, code lost:
        r4 = 'i';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x0881, code lost:
        if (r14.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x0883, code lost:
        r4 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:538:0x088d, code lost:
        if (r14.equals("ENCRYPTION_REQUEST") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x088f, code lost:
        r4 = 'm';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0899, code lost:
        if (r14.equals("MESSAGE_GEOLIVE") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x089b, code lost:
        r4 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x08a5, code lost:
        if (r14.equals("CHAT_DELETE_YOU") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x08a7, code lost:
        r4 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x08b1, code lost:
        if (r14.equals("AUTH_UNKNOWN") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x08b3, code lost:
        r4 = 'j';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x08bd, code lost:
        if (r14.equals("PINNED_GIF") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x08bf, code lost:
        r4 = 'f';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x08c9, code lost:
        if (r14.equals("PINNED_GEO") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x08cb, code lost:
        r4 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x08d5, code lost:
        if (r14.equals("PINNED_DOC") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x08d7, code lost:
        r4 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x08e1, code lost:
        if (r14.equals("PINNED_GAME_SCORE") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x08e3, code lost:
        r4 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x08ed, code lost:
        if (r14.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x08ef, code lost:
        r4 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x08f9, code lost:
        if (r14.equals("PHONE_CALL_REQUEST") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x08fb, code lost:
        r4 = 'o';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x0905, code lost:
        if (r14.equals("PINNED_STICKER") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x0907, code lost:
        r4 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x0911, code lost:
        if (r14.equals("PINNED_TEXT") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x0913, code lost:
        r4 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x091d, code lost:
        if (r14.equals("PINNED_QUIZ") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x091f, code lost:
        r4 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x0929, code lost:
        if (r14.equals("PINNED_POLL") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x092b, code lost:
        r4 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x0935, code lost:
        if (r14.equals("PINNED_GAME") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x0937, code lost:
        r4 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x0941, code lost:
        if (r14.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x0943, code lost:
        r4 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x094d, code lost:
        if (r14.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x094f, code lost:
        r4 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x0958, code lost:
        if (r14.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x095a, code lost:
        r4 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x0963, code lost:
        if (r14.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0965, code lost:
        r4 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x096f, code lost:
        if (r14.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x0971, code lost:
        r4 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x097b, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x097d, code lost:
        r4 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0987, code lost:
        if (r14.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0989, code lost:
        r4 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x0993, code lost:
        if (r14.equals("CHANNEL_MESSAGE_DOCS") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0995, code lost:
        r4 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x099f, code lost:
        if (r14.equals("PINNED_INVOICE") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x09a1, code lost:
        r4 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x09ab, code lost:
        if (r14.equals("CHAT_RETURNED") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x09ad, code lost:
        r4 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x09b7, code lost:
        if (r14.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x09b9, code lost:
        r4 = 'g';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x09c3, code lost:
        if (r14.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x09c5, code lost:
        r4 = 'n';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x09cf, code lost:
        if (r14.equals("MESSAGE_VIDEO") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x09d1, code lost:
        r4 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x09da, code lost:
        if (r14.equals("MESSAGE_ROUND") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x09dc, code lost:
        r4 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:625:0x09e6, code lost:
        if (r14.equals("MESSAGE_PHOTO") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x09e8, code lost:
        r4 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x09f1, code lost:
        if (r14.equals("MESSAGE_MUTED") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x09f3, code lost:
        r4 = 'p';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x09fd, code lost:
        if (r14.equals("MESSAGE_AUDIO") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x09ff, code lost:
        r4 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x0a09, code lost:
        if (r14.equals("CHAT_MESSAGES") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x0a0b, code lost:
        r4 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x0a15, code lost:
        if (r14.equals("CHAT_VOICECHAT_START") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x0a17, code lost:
        r4 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x0a21, code lost:
        if (r14.equals("CHAT_REQ_JOINED") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x0a23, code lost:
        r4 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0a2d, code lost:
        if (r14.equals("CHAT_JOINED") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0a2f, code lost:
        r4 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x0a39, code lost:
        if (r14.equals("CHAT_ADD_MEMBER") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x0a3b, code lost:
        r4 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0a44, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0a46, code lost:
        r4 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0a4f, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0a51, code lost:
        r4 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0a5a, code lost:
        if (r14.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0a5c, code lost:
        r4 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0a65, code lost:
        if (r14.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0a67, code lost:
        r4 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x0a70, code lost:
        if (r14.equals("MESSAGE_STICKER") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0a72, code lost:
        r4 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0a7b, code lost:
        if (r14.equals("CHAT_CREATED") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x0a7d, code lost:
        r4 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x0a86, code lost:
        if (r14.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0a88, code lost:
        r4 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x0a91, code lost:
        if (r14.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0a96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:671:0x0a93, code lost:
        r4 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0a96, code lost:
        r4 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:673:0x0a97, code lost:
        r41 = r11;
        r42 = r9;
        r43 = r1;
        r45 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0aaf, code lost:
        switch(r4) {
            case 0: goto L_0x1bbc;
            case 1: goto L_0x1bbc;
            case 2: goto L_0x1b99;
            case 3: goto L_0x1b7c;
            case 4: goto L_0x1b5f;
            case 5: goto L_0x1b42;
            case 6: goto L_0x1b24;
            case 7: goto L_0x1b08;
            case 8: goto L_0x1aea;
            case 9: goto L_0x1acc;
            case 10: goto L_0x1a71;
            case 11: goto L_0x1a53;
            case 12: goto L_0x1a30;
            case 13: goto L_0x1a0d;
            case 14: goto L_0x19ea;
            case 15: goto L_0x19cc;
            case 16: goto L_0x19ae;
            case 17: goto L_0x1990;
            case 18: goto L_0x196d;
            case 19: goto L_0x194e;
            case 20: goto L_0x194e;
            case 21: goto L_0x192b;
            case 22: goto L_0x1903;
            case 23: goto L_0x18df;
            case 24: goto L_0x18bc;
            case 25: goto L_0x1899;
            case 26: goto L_0x1874;
            case 27: goto L_0x185e;
            case 28: goto L_0x1840;
            case 29: goto L_0x1822;
            case 30: goto L_0x1804;
            case 31: goto L_0x17e6;
            case 32: goto L_0x17c8;
            case 33: goto L_0x176d;
            case 34: goto L_0x174f;
            case 35: goto L_0x172c;
            case 36: goto L_0x1709;
            case 37: goto L_0x16e6;
            case 38: goto L_0x16c8;
            case 39: goto L_0x16aa;
            case 40: goto L_0x168c;
            case 41: goto L_0x166e;
            case 42: goto L_0x1644;
            case 43: goto L_0x1620;
            case 44: goto L_0x15fc;
            case 45: goto L_0x15d8;
            case 46: goto L_0x15b2;
            case 47: goto L_0x159d;
            case 48: goto L_0x157c;
            case 49: goto L_0x1559;
            case 50: goto L_0x1536;
            case 51: goto L_0x1513;
            case 52: goto L_0x14f0;
            case 53: goto L_0x14cd;
            case 54: goto L_0x1454;
            case 55: goto L_0x1431;
            case 56: goto L_0x1409;
            case 57: goto L_0x13e1;
            case 58: goto L_0x13b9;
            case 59: goto L_0x1396;
            case 60: goto L_0x1373;
            case 61: goto L_0x1350;
            case 62: goto L_0x1328;
            case 63: goto L_0x1304;
            case 64: goto L_0x12dc;
            case 65: goto L_0x12c2;
            case 66: goto L_0x12c2;
            case 67: goto L_0x12a8;
            case 68: goto L_0x128e;
            case 69: goto L_0x126f;
            case 70: goto L_0x1255;
            case 71: goto L_0x1236;
            case 72: goto L_0x121c;
            case 73: goto L_0x1202;
            case 74: goto L_0x11e8;
            case 75: goto L_0x11ce;
            case 76: goto L_0x11b4;
            case 77: goto L_0x119a;
            case 78: goto L_0x1180;
            case 79: goto L_0x1166;
            case 80: goto L_0x1139;
            case 81: goto L_0x1110;
            case 82: goto L_0x10e7;
            case 83: goto L_0x10be;
            case 84: goto L_0x1093;
            case 85: goto L_0x1079;
            case 86: goto L_0x1022;
            case 87: goto L_0x0fd5;
            case 88: goto L_0x0var_;
            case 89: goto L_0x0f3b;
            case 90: goto L_0x0eee;
            case 91: goto L_0x0ea1;
            case 92: goto L_0x0de8;
            case 93: goto L_0x0d9b;
            case 94: goto L_0x0d44;
            case 95: goto L_0x0ced;
            case 96: goto L_0x0c9b;
            case 97: goto L_0x0c4d;
            case 98: goto L_0x0CLASSNAME;
            case 99: goto L_0x0bb9;
            case 100: goto L_0x0b6e;
            case 101: goto L_0x0b23;
            case 102: goto L_0x0ad8;
            case 103: goto L_0x0abc;
            case 104: goto L_0x0ab8;
            case 105: goto L_0x0ab8;
            case 106: goto L_0x0ab8;
            case 107: goto L_0x0ab8;
            case 108: goto L_0x0ab8;
            case 109: goto L_0x0ab8;
            case 110: goto L_0x0ab8;
            case 111: goto L_0x0ab8;
            case 112: goto L_0x0ab8;
            case 113: goto L_0x0ab8;
            default: goto L_0x0ab2;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0ab2, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0ab8, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:679:0x0abc, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
        r4 = r22;
        r3 = true;
        r17 = null;
        r22 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0ad5, code lost:
        r2 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x0adc, code lost:
        if (r12 <= 0) goto L_0x0af6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0ade, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0af6, code lost:
        if (r8 == false) goto L_0x0b10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:685:0x0af8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0b10, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x0b27, code lost:
        if (r12 <= 0) goto L_0x0b41;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0b29, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0b41, code lost:
        if (r8 == false) goto L_0x0b5b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x0b43, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0b5b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0b72, code lost:
        if (r12 <= 0) goto L_0x0b8c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0b74, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0b8c, code lost:
        if (r8 == false) goto L_0x0ba6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0b8e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0ba6, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0bbd, code lost:
        if (r12 <= 0) goto L_0x0bd7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0bbf, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0bd7, code lost:
        if (r8 == false) goto L_0x0bf1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0bd9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0bf1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0CLASSNAME, code lost:
        if (r12 <= 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0c0a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0CLASSNAME, code lost:
        if (r8 == false) goto L_0x0c3b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0c3b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0CLASSNAME, code lost:
        if (r12 <= 0) goto L_0x0c6a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0c6a, code lost:
        if (r8 == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0c6c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0CLASSNAME, code lost:
        r2 = r1;
        r4 = r22;
        r22 = r39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0c9f, code lost:
        if (r12 <= 0) goto L_0x0cb8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0ca1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:0x0cb8, code lost:
        if (r8 == false) goto L_0x0cd6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0cba, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r3[0], r3[2], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0cd6, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0ced, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0cf3, code lost:
        if (r12 <= 0) goto L_0x0d0d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0cf5, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:727:0x0d0d, code lost:
        if (r8 == false) goto L_0x0d2c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0d0f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r3[0], r3[2], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0d2c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0d44, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x0d4a, code lost:
        if (r12 <= 0) goto L_0x0d64;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0d4c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0d64, code lost:
        if (r8 == false) goto L_0x0d83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0d66, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r3[0], r3[2], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x0d83, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0d9b, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0da1, code lost:
        if (r12 <= 0) goto L_0x0dbb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0da3, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x0dbb, code lost:
        if (r8 == false) goto L_0x0dd5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0dbd, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x0dd5, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0de8, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x0dee, code lost:
        if (r12 <= 0) goto L_0x0e27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x0df2, code lost:
        if (r3.length <= 1) goto L_0x0e14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x0dfa, code lost:
        if (android.text.TextUtils.isEmpty(r3[1]) != false) goto L_0x0e14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x0dfc, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0e14, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x0e27, code lost:
        if (r8 == false) goto L_0x0e6a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x0e2b, code lost:
        if (r3.length <= 2) goto L_0x0e52;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x0e33, code lost:
        if (android.text.TextUtils.isEmpty(r3[2]) != false) goto L_0x0e52;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x0e35, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r3[0], r3[2], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x0e52, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x0e6c, code lost:
        if (r3.length <= 1) goto L_0x0e8e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x0e74, code lost:
        if (android.text.TextUtils.isEmpty(r3[1]) != false) goto L_0x0e8e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x0e76, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x0e8e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x0ea1, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x0ea7, code lost:
        if (r12 <= 0) goto L_0x0ec1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:0x0ea9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x0ec1, code lost:
        if (r8 == false) goto L_0x0edb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x0ec3, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x0edb, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x0eee, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x0ef4, code lost:
        if (r12 <= 0) goto L_0x0f0e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x0ef6, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x0f0e, code lost:
        if (r8 == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x0f3b, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x0var_, code lost:
        if (r12 <= 0) goto L_0x0f5b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x0f5b, code lost:
        if (r8 == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x0f5d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x0var_, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x0f8e, code lost:
        if (r12 <= 0) goto L_0x0fa8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x0fa8, code lost:
        if (r8 == false) goto L_0x0fc2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x0faa, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x0fc2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x0fd5, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x0fdb, code lost:
        if (r12 <= 0) goto L_0x0ff5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x0fdd, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x0ff5, code lost:
        if (r8 == false) goto L_0x100f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x0ff7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x100f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x1022, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x1028, code lost:
        if (r12 <= 0) goto L_0x1042;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x102a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x1042, code lost:
        if (r8 == false) goto L_0x1061;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x1044, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r3[0], r3[1], r3[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x1061, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x1079, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x1093, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r3[0], r3[1], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r3[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x10be, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r3[0], r3[1], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r3[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x10e7, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r3[0], r3[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r3[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x1110, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r3[0], r3[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r3[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x1139, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r3[0], r3[1], org.telegram.messenger.LocaleController.formatPluralString(r27, org.telegram.messenger.Utilities.parseInt(r3[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x1166, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("UserAcceptedToGroupPushWithGroup", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x1180, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x119a, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x11b4, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x11ce, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x11e8, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x1202, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x121c, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupEndedCall", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x1236, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r3[0], r3[1], r3[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x1255, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x126f, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r3[0], r3[1], r3[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x128e, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x12a8, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x12c2, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r3[0], r3[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x12dc, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r3[0], r3[1], r3[2]);
        r2 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x1304, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r3[0], r3[1], r3[2], r3[3]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x1328, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r3[0], r3[1], r3[2]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x1350, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x1373, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x1396, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x13b9, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r3[0], r3[1], r3[2]);
        r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x13e1, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r3[0], r3[1], r3[2]);
        r2 = org.telegram.messenger.LocaleController.getString("PollQuiz", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x1409, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r3[0], r3[1], r3[2]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x1431, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x1454, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x1458, code lost:
        if (r3.length <= 2) goto L_0x149a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x1460, code lost:
        if (android.text.TextUtils.isEmpty(r3[2]) != false) goto L_0x149a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x1462, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r3[0], r3[1], r3[2]);
        r2 = r3[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x149a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r3[0], r3[1]);
        r2 = r3[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x14cd, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x14f0, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x1513, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x1536, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x1559, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x157c, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r3[0], r3[1], r3[2]);
        r2 = r3[2];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x159d, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x15b2, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x15d8, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x15fc, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x1620, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x1644, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x166e, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x168c, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x16aa, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x16c8, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:851:0x16e6, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x1709, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x172c, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x174f, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x176d, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:856:0x1771, code lost:
        if (r3.length <= 1) goto L_0x17ae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x1779, code lost:
        if (android.text.TextUtils.isEmpty(r3[1]) != false) goto L_0x17ae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x177b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r3[0], r3[1]);
        r2 = r3[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x17ae, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x17c8, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:862:0x17e6, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:863:0x1804, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:864:0x1822, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:865:0x1840, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:866:0x185e, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x1871, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x1874, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x1899, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x18bc, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:871:0x18df, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x1903, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r3[0], org.telegram.messenger.LocaleController.formatPluralString(r27, org.telegram.messenger.Utilities.parseInt(r3[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x192b, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x194e, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r3[0], r3[1], r3[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x196d, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x1990, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x19ae, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x19cc, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x19ea, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x1a0d, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x1a30, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r3[0], r3[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x1a53, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x1a71, code lost:
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x1a75, code lost:
        if (r3.length <= 1) goto L_0x1ab2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x1a7d, code lost:
        if (android.text.TextUtils.isEmpty(r3[1]) != false) goto L_0x1ab2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x1a7f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r3[0], r3[1]);
        r2 = r3[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x1ab2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x1acc, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x1aea, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x1b08, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r3[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x1b1c, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x1b1d, code lost:
        r17 = null;
        r2 = r1;
        r22 = r39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x1b24, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x1b42, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x1b5f, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:897:0x1b7c, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x1b99, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r3[0]);
        r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:899:0x1bb5, code lost:
        r17 = r2;
        r22 = r39;
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:0x1bbc, code lost:
        r4 = r22;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r3[0], r3[1]);
        r2 = r3[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x1bd7, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1bed;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x1bd9, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:0x1bed, code lost:
        r22 = r39;
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:0x1bf0, code lost:
        r3 = false;
        r17 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x1bf3, code lost:
        r1 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:906:0x1bf6, code lost:
        r43 = r1;
        r45 = r6;
        r42 = r9;
        r41 = r11;
        r4 = r22;
        r1 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:0x1CLASSNAME, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:?, code lost:
        r2 = r1.getReactedText(r14, r3);
        r22 = r39;
        r3 = false;
        r17 = null;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:909:0x1c0b, code lost:
        if (r2 == null) goto L_0x1cf0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:910:0x1c0d, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_message();
        r5.id = r10;
        r5.random_id = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:0x1CLASSNAME, code lost:
        if (r17 == null) goto L_0x1c1d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:0x1c1a, code lost:
        r6 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:913:0x1c1d, code lost:
        r6 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:0x1c1e, code lost:
        r5.message = r6;
        r5.date = (int) (r51 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:0x1CLASSNAME, code lost:
        if (r38 == false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x1CLASSNAME, code lost:
        r5.action = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:917:0x1CLASSNAME, code lost:
        if (r37 == false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:918:0x1CLASSNAME, code lost:
        r5.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:0x1CLASSNAME, code lost:
        r5.dialog_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:920:0x1c3f, code lost:
        if (r45 == 0) goto L_0x1c4f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x1CLASSNAME, code lost:
        r6 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r5.peer_id = r6;
        r6.channel_id = r45;
        r12 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x1CLASSNAME, code lost:
        if (r25 == 0) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x1CLASSNAME, code lost:
        r6 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r5.peer_id = r6;
        r12 = r25;
        r6.chat_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x1CLASSNAME, code lost:
        r12 = r25;
        r6 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r5.peer_id = r6;
        r6.user_id = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x1c6e, code lost:
        r5.flags |= 256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x1CLASSNAME, code lost:
        if (r43 == 0) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:928:0x1c7a, code lost:
        r6 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r5.from_id = r6;
        r6.chat_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x1CLASSNAME, code lost:
        if (r32 == 0) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x1c8a, code lost:
        r6 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r5.from_id = r6;
        r6.channel_id = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x1c9a, code lost:
        if (r35 == 0) goto L_0x1ca8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x1c9c, code lost:
        r6 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r5.from_id = r6;
        r6.user_id = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:935:0x1ca8, code lost:
        r5.from_id = r5.peer_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:0x1cac, code lost:
        if (r29 != false) goto L_0x1cb3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:0x1cae, code lost:
        if (r38 == false) goto L_0x1cb1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x1cb1, code lost:
        r6 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:0x1cb3, code lost:
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:0x1cb4, code lost:
        r5.mentioned = r6;
        r5.silent = r34;
        r5.from_scheduled = r4;
        r18 = new org.telegram.messenger.MessageObject(r28, r5, r2, r22, r42, r3, r41, r37, r40);
        r2 = new java.util.ArrayList();
        r2.add(r18);
        org.telegram.messenger.NotificationsController.getInstance(r28).processNewMessages(r2, true, true, r1.countDownLatch);
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:0x1ce7, code lost:
        r1 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:943:0x1cea, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:944:0x1ceb, code lost:
        r1 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:0x1cee, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:946:0x1cef, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x1cf0, code lost:
        r8 = true;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x1cf1, code lost:
        if (r8 == false) goto L_0x1cf8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:0x1cf3, code lost:
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x1cf8, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r28);
        org.telegram.tgnet.ConnectionsManager.getInstance(r28).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x1d04, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x1d05, code lost:
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x1d08, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:0x1d09, code lost:
        r14 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x1d0a, code lost:
        r2 = r0;
        r7 = r16;
        r4 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:0x1d11, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:957:0x1d12, code lost:
        r28 = r4;
        r14 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:0x1d17, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x1d18, code lost:
        r28 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x1d1c, code lost:
        r28 = r4;
        r16 = r7;
        r14 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:0x1d25, code lost:
        r4 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:965:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1(r4));
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:966:0x1d32, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:967:0x1d33, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:968:0x1d34, code lost:
        r4 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:969:0x1d38, code lost:
        r16 = r7;
        r14 = r9;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0(r4));
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:0x1d48, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:971:0x1d49, code lost:
        r16 = r7;
        r14 = r9;
        r2 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification();
        r2.popup = false;
        r2.flags = 2;
        r2.inbox_date = (int) (r51 / 1000);
        r2.message = r5.getString("message");
        r2.type = "announcement";
        r2.media = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty();
        r3 = new org.telegram.tgnet.TLRPC$TL_updates();
        r3.updates.add(r2);
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3(r4, r3));
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).resumeNetworkMaybe();
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:972:0x1d91, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:0x1d92, code lost:
        r16 = r7;
        r14 = r9;
        r2 = r11.getInt("dc");
        r3 = r11.getString("addr").split(":");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:974:0x1da9, code lost:
        if (r3.length == 2) goto L_0x1db1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:975:0x1dab, code lost:
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:976:0x1db0, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:977:0x1db1, code lost:
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).applyDatacenterAddress(r2, r3[0], java.lang.Integer.parseInt(r3[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).resumeNetworkMaybe();
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:978:0x1dce, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:979:0x1dcf, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:980:0x1dd0, code lost:
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:981:0x1dd1, code lost:
        r7 = r16;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:995:0x1df2  */
    /* JADX WARNING: Removed duplicated region for block: B:996:0x1e02  */
    /* JADX WARNING: Removed duplicated region for block: B:999:0x1e09  */
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
            java.lang.Object r5 = r2.get(r5)     // Catch:{ all -> 0x1dea }
            boolean r6 = r5 instanceof java.lang.String     // Catch:{ all -> 0x1dea }
            if (r6 != 0) goto L_0x0024
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1dea }
            if (r2 == 0) goto L_0x0020
            java.lang.String r2 = "GCM DECRYPT ERROR 1"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x1dea }
        L_0x0020:
            r49.onDecryptError()     // Catch:{ all -> 0x1dea }
            return
        L_0x0024:
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ all -> 0x1dea }
            r6 = 8
            byte[] r5 = android.util.Base64.decode(r5, r6)     // Catch:{ all -> 0x1dea }
            org.telegram.tgnet.NativeByteBuffer r7 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x1dea }
            int r8 = r5.length     // Catch:{ all -> 0x1dea }
            r7.<init>((int) r8)     // Catch:{ all -> 0x1dea }
            r7.writeBytes((byte[]) r5)     // Catch:{ all -> 0x1dea }
            r8 = 0
            r7.position(r8)     // Catch:{ all -> 0x1dea }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1dea }
            if (r9 != 0) goto L_0x004e
            byte[] r9 = new byte[r6]     // Catch:{ all -> 0x1dea }
            org.telegram.messenger.SharedConfig.pushAuthKeyId = r9     // Catch:{ all -> 0x1dea }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1dea }
            byte[] r9 = org.telegram.messenger.Utilities.computeSHA1((byte[]) r9)     // Catch:{ all -> 0x1dea }
            int r10 = r9.length     // Catch:{ all -> 0x1dea }
            int r10 = r10 - r6
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1dea }
            java.lang.System.arraycopy(r9, r10, r11, r8, r6)     // Catch:{ all -> 0x1dea }
        L_0x004e:
            byte[] r9 = new byte[r6]     // Catch:{ all -> 0x1dea }
            r10 = 1
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1dea }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1dea }
            boolean r11 = java.util.Arrays.equals(r11, r9)     // Catch:{ all -> 0x1dea }
            r12 = 3
            r13 = 2
            if (r11 != 0) goto L_0x0089
            r49.onDecryptError()     // Catch:{ all -> 0x1dea }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1dea }
            if (r2 == 0) goto L_0x0088
            java.util.Locale r2 = java.util.Locale.US     // Catch:{ all -> 0x1dea }
            java.lang.String r5 = "GCM DECRYPT ERROR 2 k1=%s k2=%s, key=%s"
            java.lang.Object[] r6 = new java.lang.Object[r12]     // Catch:{ all -> 0x1dea }
            byte[] r7 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1dea }
            java.lang.String r7 = org.telegram.messenger.Utilities.bytesToHex(r7)     // Catch:{ all -> 0x1dea }
            r6[r8] = r7     // Catch:{ all -> 0x1dea }
            java.lang.String r7 = org.telegram.messenger.Utilities.bytesToHex(r9)     // Catch:{ all -> 0x1dea }
            r6[r10] = r7     // Catch:{ all -> 0x1dea }
            byte[] r7 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1dea }
            java.lang.String r7 = org.telegram.messenger.Utilities.bytesToHex(r7)     // Catch:{ all -> 0x1dea }
            r6[r13] = r7     // Catch:{ all -> 0x1dea }
            java.lang.String r2 = java.lang.String.format(r2, r5, r6)     // Catch:{ all -> 0x1dea }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x1dea }
        L_0x0088:
            return
        L_0x0089:
            r9 = 16
            byte[] r9 = new byte[r9]     // Catch:{ all -> 0x1dea }
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1dea }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1dea }
            org.telegram.messenger.MessageKeyData r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13)     // Catch:{ all -> 0x1dea }
            java.nio.ByteBuffer r14 = r7.buffer     // Catch:{ all -> 0x1dea }
            byte[] r15 = r11.aesKey     // Catch:{ all -> 0x1dea }
            byte[] r11 = r11.aesIv     // Catch:{ all -> 0x1dea }
            r17 = 0
            r18 = 0
            r19 = 24
            int r5 = r5.length     // Catch:{ all -> 0x1dea }
            int r20 = r5 + -24
            r16 = r11
            org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x1dea }
            byte[] r21 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1dea }
            r22 = 96
            r23 = 32
            java.nio.ByteBuffer r5 = r7.buffer     // Catch:{ all -> 0x1dea }
            r25 = 24
            int r26 = r5.limit()     // Catch:{ all -> 0x1dea }
            r24 = r5
            byte[] r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26)     // Catch:{ all -> 0x1dea }
            boolean r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6)     // Catch:{ all -> 0x1dea }
            if (r5 != 0) goto L_0x00df
            r49.onDecryptError()     // Catch:{ all -> 0x1dea }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1dea }
            if (r2 == 0) goto L_0x00de
            java.lang.String r2 = "GCM DECRYPT ERROR 3, key = %s"
            java.lang.Object[] r5 = new java.lang.Object[r10]     // Catch:{ all -> 0x1dea }
            byte[] r6 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1dea }
            java.lang.String r6 = org.telegram.messenger.Utilities.bytesToHex(r6)     // Catch:{ all -> 0x1dea }
            r5[r8] = r6     // Catch:{ all -> 0x1dea }
            java.lang.String r2 = java.lang.String.format(r2, r5)     // Catch:{ all -> 0x1dea }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x1dea }
        L_0x00de:
            return
        L_0x00df:
            int r5 = r7.readInt32(r10)     // Catch:{ all -> 0x1dea }
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x1dea }
            r7.readBytes(r5, r10)     // Catch:{ all -> 0x1dea }
            java.lang.String r7 = new java.lang.String     // Catch:{ all -> 0x1dea }
            r7.<init>(r5)     // Catch:{ all -> 0x1dea }
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ all -> 0x1de3 }
            r5.<init>(r7)     // Catch:{ all -> 0x1de3 }
            java.lang.String r9 = "loc_key"
            boolean r9 = r5.has(r9)     // Catch:{ all -> 0x1de3 }
            if (r9 == 0) goto L_0x0104
            java.lang.String r9 = "loc_key"
            java.lang.String r9 = r5.getString(r9)     // Catch:{ all -> 0x0101 }
            goto L_0x0106
        L_0x0101:
            r0 = move-exception
            goto L_0x1de6
        L_0x0104:
            java.lang.String r9 = ""
        L_0x0106:
            java.lang.String r11 = "custom"
            java.lang.Object r11 = r5.get(r11)     // Catch:{ all -> 0x1ddb }
            boolean r11 = r11 instanceof org.json.JSONObject     // Catch:{ all -> 0x1ddb }
            if (r11 == 0) goto L_0x011c
            java.lang.String r11 = "custom"
            org.json.JSONObject r11 = r5.getJSONObject(r11)     // Catch:{ all -> 0x0117 }
            goto L_0x0121
        L_0x0117:
            r0 = move-exception
            r2 = r0
            r14 = r9
            goto L_0x1de0
        L_0x011c:
            org.json.JSONObject r11 = new org.json.JSONObject     // Catch:{ all -> 0x1ddb }
            r11.<init>()     // Catch:{ all -> 0x1ddb }
        L_0x0121:
            java.lang.String r14 = "user_id"
            boolean r14 = r5.has(r14)     // Catch:{ all -> 0x1ddb }
            if (r14 == 0) goto L_0x0130
            java.lang.String r14 = "user_id"
            java.lang.Object r14 = r5.get(r14)     // Catch:{ all -> 0x0117 }
            goto L_0x0131
        L_0x0130:
            r14 = 0
        L_0x0131:
            if (r14 != 0) goto L_0x013e
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x0117 }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x0117 }
            long r14 = r14.getClientUserId()     // Catch:{ all -> 0x0117 }
            goto L_0x016e
        L_0x013e:
            boolean r15 = r14 instanceof java.lang.Long     // Catch:{ all -> 0x1ddb }
            if (r15 == 0) goto L_0x0149
            java.lang.Long r14 = (java.lang.Long) r14     // Catch:{ all -> 0x0117 }
            long r14 = r14.longValue()     // Catch:{ all -> 0x0117 }
            goto L_0x016e
        L_0x0149:
            boolean r15 = r14 instanceof java.lang.Integer     // Catch:{ all -> 0x1ddb }
            if (r15 == 0) goto L_0x0155
            java.lang.Integer r14 = (java.lang.Integer) r14     // Catch:{ all -> 0x0117 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0117 }
        L_0x0153:
            long r14 = (long) r14
            goto L_0x016e
        L_0x0155:
            boolean r15 = r14 instanceof java.lang.String     // Catch:{ all -> 0x1ddb }
            if (r15 == 0) goto L_0x0164
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ all -> 0x0117 }
            java.lang.Integer r14 = org.telegram.messenger.Utilities.parseInt(r14)     // Catch:{ all -> 0x0117 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0117 }
            goto L_0x0153
        L_0x0164:
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1ddb }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x1ddb }
            long r14 = r14.getClientUserId()     // Catch:{ all -> 0x1ddb }
        L_0x016e:
            int r16 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1ddb }
            r4 = 0
        L_0x0171:
            if (r4 >= r12) goto L_0x0184
            org.telegram.messenger.UserConfig r18 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x0117 }
            long r18 = r18.getClientUserId()     // Catch:{ all -> 0x0117 }
            int r20 = (r18 > r14 ? 1 : (r18 == r14 ? 0 : -1))
            if (r20 != 0) goto L_0x0181
            r14 = 1
            goto L_0x0187
        L_0x0181:
            int r4 = r4 + 1
            goto L_0x0171
        L_0x0184:
            r4 = r16
            r14 = 0
        L_0x0187:
            if (r14 != 0) goto L_0x0198
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0117 }
            if (r2 == 0) goto L_0x0192
            java.lang.String r2 = "GCM ACCOUNT NOT FOUND"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x0117 }
        L_0x0192:
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x0117 }
            r2.countDown()     // Catch:{ all -> 0x0117 }
            return
        L_0x0198:
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x1dd4 }
            boolean r14 = r14.isClientActivated()     // Catch:{ all -> 0x1dd4 }
            if (r14 != 0) goto L_0x01b6
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x01b1 }
            if (r2 == 0) goto L_0x01ab
            java.lang.String r2 = "GCM ACCOUNT NOT ACTIVATED"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x01b1 }
        L_0x01ab:
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x01b1 }
            r2.countDown()     // Catch:{ all -> 0x01b1 }
            return
        L_0x01b1:
            r0 = move-exception
        L_0x01b2:
            r2 = r0
            r14 = r9
            goto L_0x1dd9
        L_0x01b6:
            java.lang.String r14 = "google.sent_time"
            r2.get(r14)     // Catch:{ all -> 0x1dd4 }
            int r2 = r9.hashCode()     // Catch:{ all -> 0x1dd4 }
            switch(r2) {
                case -1963663249: goto L_0x01e1;
                case -920689527: goto L_0x01d7;
                case 633004703: goto L_0x01cd;
                case 1365673842: goto L_0x01c3;
                default: goto L_0x01c2;
            }
        L_0x01c2:
            goto L_0x01eb
        L_0x01c3:
            java.lang.String r2 = "GEO_LIVE_PENDING"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01b1 }
            if (r2 == 0) goto L_0x01eb
            r2 = 3
            goto L_0x01ec
        L_0x01cd:
            java.lang.String r2 = "MESSAGE_ANNOUNCEMENT"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01b1 }
            if (r2 == 0) goto L_0x01eb
            r2 = 1
            goto L_0x01ec
        L_0x01d7:
            java.lang.String r2 = "DC_UPDATE"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01b1 }
            if (r2 == 0) goto L_0x01eb
            r2 = 0
            goto L_0x01ec
        L_0x01e1:
            java.lang.String r2 = "SESSION_REVOKE"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01b1 }
            if (r2 == 0) goto L_0x01eb
            r2 = 2
            goto L_0x01ec
        L_0x01eb:
            r2 = -1
        L_0x01ec:
            if (r2 == 0) goto L_0x1d92
            if (r2 == r10) goto L_0x1d49
            if (r2 == r13) goto L_0x1d38
            if (r2 == r12) goto L_0x1d1c
            java.lang.String r2 = "channel_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1d17 }
            r14 = 0
            if (r2 == 0) goto L_0x0211
            java.lang.String r2 = "channel_id"
            r16 = r7
            long r6 = r11.getLong(r2)     // Catch:{ all -> 0x0208 }
            long r12 = -r6
            goto L_0x0215
        L_0x0208:
            r0 = move-exception
            r2 = r0
            r14 = r9
            goto L_0x1dd1
        L_0x020d:
            r0 = move-exception
            r16 = r7
            goto L_0x01b2
        L_0x0211:
            r16 = r7
            r6 = r14
            r12 = r6
        L_0x0215:
            java.lang.String r2 = "from_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1d11 }
            if (r2 == 0) goto L_0x0227
            java.lang.String r2 = "from_id"
            long r12 = r11.getLong(r2)     // Catch:{ all -> 0x0208 }
            r28 = r4
            r3 = r12
            goto L_0x022a
        L_0x0227:
            r28 = r4
            r3 = r14
        L_0x022a:
            java.lang.String r2 = "chat_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1d08 }
            if (r2 == 0) goto L_0x024d
            java.lang.String r2 = "chat_id"
            long r12 = r11.getLong(r2)     // Catch:{ all -> 0x0241 }
            r29 = r9
            long r8 = -r12
            r47 = r8
            r8 = r12
            r12 = r47
            goto L_0x0250
        L_0x0241:
            r0 = move-exception
            r29 = r9
        L_0x0244:
            r2 = r0
            r7 = r16
            r4 = r28
            r14 = r29
            goto L_0x1dd9
        L_0x024d:
            r29 = r9
            r8 = r14
        L_0x0250:
            java.lang.String r2 = "encryption_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1d04 }
            if (r2 == 0) goto L_0x0266
            java.lang.String r2 = "encryption_id"
            int r2 = r11.getInt(r2)     // Catch:{ all -> 0x0264 }
            long r12 = (long) r2     // Catch:{ all -> 0x0264 }
            long r12 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r12)     // Catch:{ all -> 0x0264 }
            goto L_0x0266
        L_0x0264:
            r0 = move-exception
            goto L_0x0244
        L_0x0266:
            java.lang.String r2 = "schedule"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1d04 }
            if (r2 == 0) goto L_0x0278
            java.lang.String r2 = "schedule"
            int r2 = r11.getInt(r2)     // Catch:{ all -> 0x0264 }
            if (r2 != r10) goto L_0x0278
            r2 = 1
            goto L_0x0279
        L_0x0278:
            r2 = 0
        L_0x0279:
            int r20 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r20 != 0) goto L_0x028a
            java.lang.String r10 = "ENCRYPTED_MESSAGE"
            r14 = r29
            boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x1cee }
            if (r10 == 0) goto L_0x028c
            long r12 = org.telegram.messenger.NotificationsController.globalSecretChatId     // Catch:{ all -> 0x1cee }
            goto L_0x028c
        L_0x028a:
            r14 = r29
        L_0x028c:
            r20 = 0
            int r10 = (r12 > r20 ? 1 : (r12 == r20 ? 0 : -1))
            if (r10 == 0) goto L_0x1cf0
            java.lang.String r10 = "READ_HISTORY"
            boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x1cee }
            java.lang.String r15 = " for dialogId = "
            if (r10 == 0) goto L_0x030e
            java.lang.String r2 = "max_id"
            int r2 = r11.getInt(r2)     // Catch:{ all -> 0x1cee }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x1cee }
            r5.<init>()     // Catch:{ all -> 0x1cee }
            boolean r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1cee }
            if (r10 == 0) goto L_0x02c5
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x1cee }
            r10.<init>()     // Catch:{ all -> 0x1cee }
            java.lang.String r11 = "GCM received read notification max_id = "
            r10.append(r11)     // Catch:{ all -> 0x1cee }
            r10.append(r2)     // Catch:{ all -> 0x1cee }
            r10.append(r15)     // Catch:{ all -> 0x1cee }
            r10.append(r12)     // Catch:{ all -> 0x1cee }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x1cee }
            org.telegram.messenger.FileLog.d(r10)     // Catch:{ all -> 0x1cee }
        L_0x02c5:
            r10 = 0
            int r12 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r12 == 0) goto L_0x02d8
            org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox r3 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox     // Catch:{ all -> 0x1cee }
            r3.<init>()     // Catch:{ all -> 0x1cee }
            r3.channel_id = r6     // Catch:{ all -> 0x1cee }
            r3.max_id = r2     // Catch:{ all -> 0x1cee }
            r5.add(r3)     // Catch:{ all -> 0x1cee }
            goto L_0x02fb
        L_0x02d8:
            org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox r6 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox     // Catch:{ all -> 0x1cee }
            r6.<init>()     // Catch:{ all -> 0x1cee }
            r10 = 0
            int r7 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r7 == 0) goto L_0x02ed
            org.telegram.tgnet.TLRPC$TL_peerUser r7 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1cee }
            r7.<init>()     // Catch:{ all -> 0x1cee }
            r6.peer = r7     // Catch:{ all -> 0x1cee }
            r7.user_id = r3     // Catch:{ all -> 0x1cee }
            goto L_0x02f6
        L_0x02ed:
            org.telegram.tgnet.TLRPC$TL_peerChat r3 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x1cee }
            r3.<init>()     // Catch:{ all -> 0x1cee }
            r6.peer = r3     // Catch:{ all -> 0x1cee }
            r3.chat_id = r8     // Catch:{ all -> 0x1cee }
        L_0x02f6:
            r6.max_id = r2     // Catch:{ all -> 0x1cee }
            r5.add(r6)     // Catch:{ all -> 0x1cee }
        L_0x02fb:
            org.telegram.messenger.MessagesController r22 = org.telegram.messenger.MessagesController.getInstance(r28)     // Catch:{ all -> 0x1cee }
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r23 = r5
            r22.processUpdateArray(r23, r24, r25, r26, r27)     // Catch:{ all -> 0x1cee }
            goto L_0x1cf0
        L_0x030e:
            java.lang.String r10 = "MESSAGE_DELETED"
            boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x1cee }
            r22 = r2
            java.lang.String r2 = "messages"
            if (r10 == 0) goto L_0x0384
            java.lang.String r2 = r11.getString(r2)     // Catch:{ all -> 0x1cee }
            java.lang.String r3 = ","
            java.lang.String[] r2 = r2.split(r3)     // Catch:{ all -> 0x1cee }
            androidx.collection.LongSparseArray r3 = new androidx.collection.LongSparseArray     // Catch:{ all -> 0x1cee }
            r3.<init>()     // Catch:{ all -> 0x1cee }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x1cee }
            r4.<init>()     // Catch:{ all -> 0x1cee }
            r8 = 0
        L_0x032f:
            int r5 = r2.length     // Catch:{ all -> 0x1cee }
            if (r8 >= r5) goto L_0x033e
            r5 = r2[r8]     // Catch:{ all -> 0x1cee }
            java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r5)     // Catch:{ all -> 0x1cee }
            r4.add(r5)     // Catch:{ all -> 0x1cee }
            int r8 = r8 + 1
            goto L_0x032f
        L_0x033e:
            long r8 = -r6
            r3.put(r8, r4)     // Catch:{ all -> 0x1cee }
            org.telegram.messenger.NotificationsController r2 = org.telegram.messenger.NotificationsController.getInstance(r28)     // Catch:{ all -> 0x1cee }
            r2.removeDeletedMessagesFromNotifications(r3)     // Catch:{ all -> 0x1cee }
            org.telegram.messenger.MessagesController r20 = org.telegram.messenger.MessagesController.getInstance(r28)     // Catch:{ all -> 0x1cee }
            r21 = r12
            r23 = r4
            r24 = r6
            r20.deleteMessagesByPush(r21, r23, r24)     // Catch:{ all -> 0x1cee }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1cee }
            if (r2 == 0) goto L_0x1cf0
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x1cee }
            r2.<init>()     // Catch:{ all -> 0x1cee }
            java.lang.String r3 = "GCM received "
            r2.append(r3)     // Catch:{ all -> 0x1cee }
            r2.append(r14)     // Catch:{ all -> 0x1cee }
            r2.append(r15)     // Catch:{ all -> 0x1cee }
            r2.append(r12)     // Catch:{ all -> 0x1cee }
            java.lang.String r3 = " mids = "
            r2.append(r3)     // Catch:{ all -> 0x1cee }
            java.lang.String r3 = ","
            java.lang.String r3 = android.text.TextUtils.join(r3, r4)     // Catch:{ all -> 0x1cee }
            r2.append(r3)     // Catch:{ all -> 0x1cee }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x1cee }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x1cee }
            goto L_0x1cf0
        L_0x0384:
            boolean r10 = android.text.TextUtils.isEmpty(r14)     // Catch:{ all -> 0x1cee }
            if (r10 != 0) goto L_0x1cf0
            java.lang.String r10 = "msg_id"
            boolean r10 = r11.has(r10)     // Catch:{ all -> 0x1cee }
            if (r10 == 0) goto L_0x039b
            java.lang.String r10 = "msg_id"
            int r10 = r11.getInt(r10)     // Catch:{ all -> 0x1cee }
            r23 = r3
            goto L_0x039e
        L_0x039b:
            r23 = r3
            r10 = 0
        L_0x039e:
            java.lang.String r3 = "random_id"
            boolean r3 = r11.has(r3)     // Catch:{ all -> 0x1cee }
            if (r3 == 0) goto L_0x03b5
            java.lang.String r3 = "random_id"
            java.lang.String r3 = r11.getString(r3)     // Catch:{ all -> 0x1cee }
            java.lang.Long r3 = org.telegram.messenger.Utilities.parseLong(r3)     // Catch:{ all -> 0x1cee }
            long r3 = r3.longValue()     // Catch:{ all -> 0x1cee }
            goto L_0x03b7
        L_0x03b5:
            r3 = 0
        L_0x03b7:
            if (r10 == 0) goto L_0x03f9
            r25 = r8
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r28)     // Catch:{ all -> 0x03ee }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r8 = r8.dialogs_read_inbox_max     // Catch:{ all -> 0x03ee }
            java.lang.Long r9 = java.lang.Long.valueOf(r12)     // Catch:{ all -> 0x03ee }
            java.lang.Object r8 = r8.get(r9)     // Catch:{ all -> 0x03ee }
            java.lang.Integer r8 = (java.lang.Integer) r8     // Catch:{ all -> 0x03ee }
            if (r8 != 0) goto L_0x03e7
            org.telegram.messenger.MessagesStorage r8 = org.telegram.messenger.MessagesStorage.getInstance(r28)     // Catch:{ all -> 0x03ee }
            r9 = 0
            int r8 = r8.getDialogReadMax(r9, r12)     // Catch:{ all -> 0x03ee }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ all -> 0x03ee }
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r28)     // Catch:{ all -> 0x03ee }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r9 = r9.dialogs_read_inbox_max     // Catch:{ all -> 0x03ee }
            java.lang.Long r1 = java.lang.Long.valueOf(r12)     // Catch:{ all -> 0x03ee }
            r9.put(r1, r8)     // Catch:{ all -> 0x03ee }
        L_0x03e7:
            int r1 = r8.intValue()     // Catch:{ all -> 0x03ee }
            if (r10 <= r1) goto L_0x040d
            goto L_0x040b
        L_0x03ee:
            r0 = move-exception
            r3 = -1
            r1 = r49
            r2 = r0
            r7 = r16
            r4 = r28
            goto L_0x1df0
        L_0x03f9:
            r25 = r8
            r8 = 0
            int r1 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r1 == 0) goto L_0x040d
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r28)     // Catch:{ all -> 0x03ee }
            boolean r1 = r1.checkMessageByRandomId(r3)     // Catch:{ all -> 0x03ee }
            if (r1 != 0) goto L_0x040d
        L_0x040b:
            r1 = 1
            goto L_0x040e
        L_0x040d:
            r1 = 0
        L_0x040e:
            java.lang.String r8 = "REACT_"
            boolean r8 = r14.startsWith(r8)     // Catch:{ all -> 0x1cea }
            if (r8 != 0) goto L_0x041e
            java.lang.String r8 = "CHAT_REACT_"
            boolean r8 = r14.startsWith(r8)     // Catch:{ all -> 0x03ee }
            if (r8 == 0) goto L_0x041f
        L_0x041e:
            r1 = 1
        L_0x041f:
            if (r1 == 0) goto L_0x1ce7
            java.lang.String r1 = "chat_from_id"
            r30 = r3
            r8 = 0
            long r3 = r11.optLong(r1, r8)     // Catch:{ all -> 0x1cea }
            java.lang.String r1 = "chat_from_broadcast_id"
            r27 = r2
            long r1 = r11.optLong(r1, r8)     // Catch:{ all -> 0x1cea }
            r32 = r1
            java.lang.String r1 = "chat_from_group_id"
            long r1 = r11.optLong(r1, r8)     // Catch:{ all -> 0x1cea }
            int r20 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r20 != 0) goto L_0x0446
            int r29 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r29 == 0) goto L_0x0444
            goto L_0x0446
        L_0x0444:
            r8 = 0
            goto L_0x0447
        L_0x0446:
            r8 = 1
        L_0x0447:
            java.lang.String r9 = "mention"
            boolean r9 = r11.has(r9)     // Catch:{ all -> 0x1cea }
            if (r9 == 0) goto L_0x045a
            java.lang.String r9 = "mention"
            int r9 = r11.getInt(r9)     // Catch:{ all -> 0x03ee }
            if (r9 == 0) goto L_0x045a
            r29 = 1
            goto L_0x045c
        L_0x045a:
            r29 = 0
        L_0x045c:
            java.lang.String r9 = "silent"
            boolean r9 = r11.has(r9)     // Catch:{ all -> 0x1cea }
            if (r9 == 0) goto L_0x046f
            java.lang.String r9 = "silent"
            int r9 = r11.getInt(r9)     // Catch:{ all -> 0x03ee }
            if (r9 == 0) goto L_0x046f
            r34 = 1
            goto L_0x0471
        L_0x046f:
            r34 = 0
        L_0x0471:
            java.lang.String r9 = "loc_args"
            boolean r9 = r5.has(r9)     // Catch:{ all -> 0x1cea }
            if (r9 == 0) goto L_0x0493
            java.lang.String r9 = "loc_args"
            org.json.JSONArray r5 = r5.getJSONArray(r9)     // Catch:{ all -> 0x03ee }
            int r9 = r5.length()     // Catch:{ all -> 0x03ee }
            r35 = r3
            java.lang.String[] r3 = new java.lang.String[r9]     // Catch:{ all -> 0x03ee }
            r4 = 0
        L_0x0488:
            if (r4 >= r9) goto L_0x0496
            java.lang.String r37 = r5.getString(r4)     // Catch:{ all -> 0x03ee }
            r3[r4] = r37     // Catch:{ all -> 0x03ee }
            int r4 = r4 + 1
            goto L_0x0488
        L_0x0493:
            r35 = r3
            r3 = 0
        L_0x0496:
            r4 = 0
            r5 = r3[r4]     // Catch:{ all -> 0x1cea }
            java.lang.String r4 = "edit_date"
            boolean r4 = r11.has(r4)     // Catch:{ all -> 0x1cea }
            java.lang.String r9 = "CHAT_"
            boolean r9 = r14.startsWith(r9)     // Catch:{ all -> 0x1cea }
            if (r9 == 0) goto L_0x04dc
            boolean r9 = org.telegram.messenger.UserObject.isReplyUser((long) r12)     // Catch:{ all -> 0x03ee }
            if (r9 == 0) goto L_0x04c5
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ee }
            r9.<init>()     // Catch:{ all -> 0x03ee }
            r9.append(r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r5 = " @ "
            r9.append(r5)     // Catch:{ all -> 0x03ee }
            r5 = 1
            r11 = r3[r5]     // Catch:{ all -> 0x03ee }
            r9.append(r11)     // Catch:{ all -> 0x03ee }
            java.lang.String r5 = r9.toString()     // Catch:{ all -> 0x03ee }
            goto L_0x04ff
        L_0x04c5:
            r20 = 0
            int r9 = (r6 > r20 ? 1 : (r6 == r20 ? 0 : -1))
            if (r9 == 0) goto L_0x04cd
            r9 = 1
            goto L_0x04ce
        L_0x04cd:
            r9 = 0
        L_0x04ce:
            r11 = 1
            r37 = r3[r11]     // Catch:{ all -> 0x03ee }
            r11 = 0
            r38 = 0
            r47 = r9
            r9 = r5
            r5 = r37
            r37 = r47
            goto L_0x0505
        L_0x04dc:
            java.lang.String r9 = "PINNED_"
            boolean r9 = r14.startsWith(r9)     // Catch:{ all -> 0x1cea }
            if (r9 == 0) goto L_0x04f4
            r20 = 0
            int r9 = (r6 > r20 ? 1 : (r6 == r20 ? 0 : -1))
            if (r9 == 0) goto L_0x04ec
            r9 = 1
            goto L_0x04ed
        L_0x04ec:
            r9 = 0
        L_0x04ed:
            r37 = r9
            r9 = 0
            r11 = 0
            r38 = 1
            goto L_0x0505
        L_0x04f4:
            java.lang.String r9 = "CHANNEL_"
            boolean r9 = r14.startsWith(r9)     // Catch:{ all -> 0x1cea }
            if (r9 == 0) goto L_0x04ff
            r9 = 0
            r11 = 1
            goto L_0x0501
        L_0x04ff:
            r9 = 0
            r11 = 0
        L_0x0501:
            r37 = 0
            r38 = 0
        L_0x0505:
            boolean r39 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1cea }
            if (r39 == 0) goto L_0x0530
            r39 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ee }
            r5.<init>()     // Catch:{ all -> 0x03ee }
            r40 = r4
            java.lang.String r4 = "GCM received message notification "
            r5.append(r4)     // Catch:{ all -> 0x03ee }
            r5.append(r14)     // Catch:{ all -> 0x03ee }
            r5.append(r15)     // Catch:{ all -> 0x03ee }
            r5.append(r12)     // Catch:{ all -> 0x03ee }
            java.lang.String r4 = " mid = "
            r5.append(r4)     // Catch:{ all -> 0x03ee }
            r5.append(r10)     // Catch:{ all -> 0x03ee }
            java.lang.String r4 = r5.toString()     // Catch:{ all -> 0x03ee }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ all -> 0x03ee }
            goto L_0x0534
        L_0x0530:
            r40 = r4
            r39 = r5
        L_0x0534:
            java.lang.String r4 = "REACT_"
            boolean r4 = r14.startsWith(r4)     // Catch:{ all -> 0x1cea }
            if (r4 != 0) goto L_0x1bf6
            java.lang.String r4 = "CHAT_REACT_"
            boolean r4 = r14.startsWith(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0546
            goto L_0x1bf6
        L_0x0546:
            int r4 = r14.hashCode()     // Catch:{ all -> 0x03ee }
            switch(r4) {
                case -2100047043: goto L_0x0a8b;
                case -2091498420: goto L_0x0a80;
                case -2053872415: goto L_0x0a75;
                case -2039746363: goto L_0x0a6a;
                case -2023218804: goto L_0x0a5f;
                case -1979538588: goto L_0x0a54;
                case -1979536003: goto L_0x0a49;
                case -1979535888: goto L_0x0a3e;
                case -1969004705: goto L_0x0a33;
                case -1946699248: goto L_0x0a27;
                case -1717283471: goto L_0x0a1b;
                case -1646640058: goto L_0x0a0f;
                case -1528047021: goto L_0x0a03;
                case -1493579426: goto L_0x09f7;
                case -1482481933: goto L_0x09eb;
                case -1480102982: goto L_0x09e0;
                case -1478041834: goto L_0x09d4;
                case -1474543101: goto L_0x09c9;
                case -1465695932: goto L_0x09bd;
                case -1374906292: goto L_0x09b1;
                case -1372940586: goto L_0x09a5;
                case -1264245338: goto L_0x0999;
                case -1236154001: goto L_0x098d;
                case -1236086700: goto L_0x0981;
                case -1236077786: goto L_0x0975;
                case -1235796237: goto L_0x0969;
                case -1235760759: goto L_0x095d;
                case -1235686303: goto L_0x0952;
                case -1198046100: goto L_0x0947;
                case -1124254527: goto L_0x093b;
                case -1085137927: goto L_0x092f;
                case -1084856378: goto L_0x0923;
                case -1084820900: goto L_0x0917;
                case -1084746444: goto L_0x090b;
                case -819729482: goto L_0x08ff;
                case -772141857: goto L_0x08f3;
                case -638310039: goto L_0x08e7;
                case -590403924: goto L_0x08db;
                case -589196239: goto L_0x08cf;
                case -589193654: goto L_0x08c3;
                case -589193539: goto L_0x08b7;
                case -440169325: goto L_0x08ab;
                case -412748110: goto L_0x089f;
                case -228518075: goto L_0x0893;
                case -213586509: goto L_0x0887;
                case -115582002: goto L_0x087b;
                case -112621464: goto L_0x086f;
                case -108522133: goto L_0x0863;
                case -107572034: goto L_0x0858;
                case -40534265: goto L_0x084c;
                case 52369421: goto L_0x0840;
                case 65254746: goto L_0x0834;
                case 141040782: goto L_0x0828;
                case 202550149: goto L_0x081c;
                case 309993049: goto L_0x0810;
                case 309995634: goto L_0x0804;
                case 309995749: goto L_0x07f8;
                case 320532812: goto L_0x07ec;
                case 328933854: goto L_0x07e0;
                case 331340546: goto L_0x07d4;
                case 342406591: goto L_0x07c8;
                case 344816990: goto L_0x07bc;
                case 346878138: goto L_0x07b0;
                case 350376871: goto L_0x07a4;
                case 608430149: goto L_0x0798;
                case 615714517: goto L_0x078d;
                case 715508879: goto L_0x0781;
                case 728985323: goto L_0x0775;
                case 731046471: goto L_0x0769;
                case 734545204: goto L_0x075d;
                case 802032552: goto L_0x0751;
                case 991498806: goto L_0x0745;
                case 1007364121: goto L_0x0739;
                case 1019850010: goto L_0x072d;
                case 1019917311: goto L_0x0721;
                case 1019926225: goto L_0x0715;
                case 1020207774: goto L_0x0709;
                case 1020243252: goto L_0x06fd;
                case 1020317708: goto L_0x06f1;
                case 1060282259: goto L_0x06e5;
                case 1060349560: goto L_0x06d9;
                case 1060358474: goto L_0x06cd;
                case 1060640023: goto L_0x06c1;
                case 1060675501: goto L_0x06b5;
                case 1060749957: goto L_0x06aa;
                case 1073049781: goto L_0x069e;
                case 1078101399: goto L_0x0692;
                case 1110103437: goto L_0x0686;
                case 1160762272: goto L_0x067a;
                case 1172918249: goto L_0x066e;
                case 1234591620: goto L_0x0662;
                case 1281128640: goto L_0x0656;
                case 1281131225: goto L_0x064a;
                case 1281131340: goto L_0x063e;
                case 1310789062: goto L_0x0633;
                case 1333118583: goto L_0x0627;
                case 1361447897: goto L_0x061b;
                case 1498266155: goto L_0x060f;
                case 1533804208: goto L_0x0603;
                case 1540131626: goto L_0x05f7;
                case 1547988151: goto L_0x05eb;
                case 1561464595: goto L_0x05df;
                case 1563525743: goto L_0x05d3;
                case 1567024476: goto L_0x05c7;
                case 1810705077: goto L_0x05bb;
                case 1815177512: goto L_0x05af;
                case 1954774321: goto L_0x05a3;
                case 1963241394: goto L_0x0597;
                case 2014789757: goto L_0x058b;
                case 2022049433: goto L_0x057f;
                case 2034984710: goto L_0x0573;
                case 2048733346: goto L_0x0567;
                case 2099392181: goto L_0x055b;
                case 2140162142: goto L_0x054f;
                default: goto L_0x054d;
            }     // Catch:{ all -> 0x03ee }
        L_0x054d:
            goto L_0x0a96
        L_0x054f:
            java.lang.String r4 = "CHAT_MESSAGE_GEOLIVE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 60
            goto L_0x0a97
        L_0x055b:
            java.lang.String r4 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 43
            goto L_0x0a97
        L_0x0567:
            java.lang.String r4 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 28
            goto L_0x0a97
        L_0x0573:
            java.lang.String r4 = "CHANNEL_MESSAGE_PLAYLIST"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 45
            goto L_0x0a97
        L_0x057f:
            java.lang.String r4 = "PINNED_CONTACT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 94
            goto L_0x0a97
        L_0x058b:
            java.lang.String r4 = "CHAT_PHOTO_EDITED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 68
            goto L_0x0a97
        L_0x0597:
            java.lang.String r4 = "LOCKED_MESSAGE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 108(0x6c, float:1.51E-43)
            goto L_0x0a97
        L_0x05a3:
            java.lang.String r4 = "CHAT_MESSAGE_PLAYLIST"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 83
            goto L_0x0a97
        L_0x05af:
            java.lang.String r4 = "CHANNEL_MESSAGES"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 47
            goto L_0x0a97
        L_0x05bb:
            java.lang.String r4 = "MESSAGE_INVOICE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 21
            goto L_0x0a97
        L_0x05c7:
            java.lang.String r4 = "CHAT_MESSAGE_VIDEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 51
            goto L_0x0a97
        L_0x05d3:
            java.lang.String r4 = "CHAT_MESSAGE_ROUND"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 52
            goto L_0x0a97
        L_0x05df:
            java.lang.String r4 = "CHAT_MESSAGE_PHOTO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 50
            goto L_0x0a97
        L_0x05eb:
            java.lang.String r4 = "CHAT_MESSAGE_AUDIO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 55
            goto L_0x0a97
        L_0x05f7:
            java.lang.String r4 = "MESSAGE_PLAYLIST"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 25
            goto L_0x0a97
        L_0x0603:
            java.lang.String r4 = "MESSAGE_VIDEOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 24
            goto L_0x0a97
        L_0x060f:
            java.lang.String r4 = "PHONE_CALL_MISSED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 113(0x71, float:1.58E-43)
            goto L_0x0a97
        L_0x061b:
            java.lang.String r4 = "MESSAGE_PHOTOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 23
            goto L_0x0a97
        L_0x0627:
            java.lang.String r4 = "CHAT_MESSAGE_VIDEOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 82
            goto L_0x0a97
        L_0x0633:
            java.lang.String r4 = "MESSAGE_NOTEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 2
            goto L_0x0a97
        L_0x063e:
            java.lang.String r4 = "MESSAGE_GIF"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 17
            goto L_0x0a97
        L_0x064a:
            java.lang.String r4 = "MESSAGE_GEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 15
            goto L_0x0a97
        L_0x0656:
            java.lang.String r4 = "MESSAGE_DOC"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 9
            goto L_0x0a97
        L_0x0662:
            java.lang.String r4 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 63
            goto L_0x0a97
        L_0x066e:
            java.lang.String r4 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 39
            goto L_0x0a97
        L_0x067a:
            java.lang.String r4 = "CHAT_MESSAGE_PHOTOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 81
            goto L_0x0a97
        L_0x0686:
            java.lang.String r4 = "CHAT_MESSAGE_NOTEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 49
            goto L_0x0a97
        L_0x0692:
            java.lang.String r4 = "CHAT_TITLE_EDITED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 67
            goto L_0x0a97
        L_0x069e:
            java.lang.String r4 = "PINNED_NOTEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 87
            goto L_0x0a97
        L_0x06aa:
            java.lang.String r4 = "MESSAGE_TEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 0
            goto L_0x0a97
        L_0x06b5:
            java.lang.String r4 = "MESSAGE_QUIZ"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 13
            goto L_0x0a97
        L_0x06c1:
            java.lang.String r4 = "MESSAGE_POLL"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 14
            goto L_0x0a97
        L_0x06cd:
            java.lang.String r4 = "MESSAGE_GAME"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 18
            goto L_0x0a97
        L_0x06d9:
            java.lang.String r4 = "MESSAGE_FWDS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 22
            goto L_0x0a97
        L_0x06e5:
            java.lang.String r4 = "MESSAGE_DOCS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 26
            goto L_0x0a97
        L_0x06f1:
            java.lang.String r4 = "CHAT_MESSAGE_TEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 48
            goto L_0x0a97
        L_0x06fd:
            java.lang.String r4 = "CHAT_MESSAGE_QUIZ"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 57
            goto L_0x0a97
        L_0x0709:
            java.lang.String r4 = "CHAT_MESSAGE_POLL"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 58
            goto L_0x0a97
        L_0x0715:
            java.lang.String r4 = "CHAT_MESSAGE_GAME"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 62
            goto L_0x0a97
        L_0x0721:
            java.lang.String r4 = "CHAT_MESSAGE_FWDS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 80
            goto L_0x0a97
        L_0x072d:
            java.lang.String r4 = "CHAT_MESSAGE_DOCS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 84
            goto L_0x0a97
        L_0x0739:
            java.lang.String r4 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 20
            goto L_0x0a97
        L_0x0745:
            java.lang.String r4 = "PINNED_GEOLIVE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 98
            goto L_0x0a97
        L_0x0751:
            java.lang.String r4 = "MESSAGE_CONTACT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 12
            goto L_0x0a97
        L_0x075d:
            java.lang.String r4 = "PINNED_VIDEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 89
            goto L_0x0a97
        L_0x0769:
            java.lang.String r4 = "PINNED_ROUND"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 90
            goto L_0x0a97
        L_0x0775:
            java.lang.String r4 = "PINNED_PHOTO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 88
            goto L_0x0a97
        L_0x0781:
            java.lang.String r4 = "PINNED_AUDIO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 93
            goto L_0x0a97
        L_0x078d:
            java.lang.String r4 = "MESSAGE_PHOTO_SECRET"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 4
            goto L_0x0a97
        L_0x0798:
            java.lang.String r4 = "CHAT_VOICECHAT_INVITE_YOU"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 73
            goto L_0x0a97
        L_0x07a4:
            java.lang.String r4 = "CHANNEL_MESSAGE_VIDEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 30
            goto L_0x0a97
        L_0x07b0:
            java.lang.String r4 = "CHANNEL_MESSAGE_ROUND"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 31
            goto L_0x0a97
        L_0x07bc:
            java.lang.String r4 = "CHANNEL_MESSAGE_PHOTO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 29
            goto L_0x0a97
        L_0x07c8:
            java.lang.String r4 = "CHAT_VOICECHAT_END"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 72
            goto L_0x0a97
        L_0x07d4:
            java.lang.String r4 = "CHANNEL_MESSAGE_AUDIO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 34
            goto L_0x0a97
        L_0x07e0:
            java.lang.String r4 = "CHAT_MESSAGE_STICKER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 54
            goto L_0x0a97
        L_0x07ec:
            java.lang.String r4 = "MESSAGES"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 27
            goto L_0x0a97
        L_0x07f8:
            java.lang.String r4 = "CHAT_MESSAGE_GIF"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 61
            goto L_0x0a97
        L_0x0804:
            java.lang.String r4 = "CHAT_MESSAGE_GEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 59
            goto L_0x0a97
        L_0x0810:
            java.lang.String r4 = "CHAT_MESSAGE_DOC"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 53
            goto L_0x0a97
        L_0x081c:
            java.lang.String r4 = "CHAT_VOICECHAT_INVITE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 71
            goto L_0x0a97
        L_0x0828:
            java.lang.String r4 = "CHAT_LEFT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 76
            goto L_0x0a97
        L_0x0834:
            java.lang.String r4 = "CHAT_ADD_YOU"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 66
            goto L_0x0a97
        L_0x0840:
            java.lang.String r4 = "REACT_TEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 104(0x68, float:1.46E-43)
            goto L_0x0a97
        L_0x084c:
            java.lang.String r4 = "CHAT_DELETE_MEMBER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 74
            goto L_0x0a97
        L_0x0858:
            java.lang.String r4 = "MESSAGE_SCREENSHOT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 7
            goto L_0x0a97
        L_0x0863:
            java.lang.String r4 = "AUTH_REGION"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 107(0x6b, float:1.5E-43)
            goto L_0x0a97
        L_0x086f:
            java.lang.String r4 = "CONTACT_JOINED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 105(0x69, float:1.47E-43)
            goto L_0x0a97
        L_0x087b:
            java.lang.String r4 = "CHAT_MESSAGE_INVOICE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 64
            goto L_0x0a97
        L_0x0887:
            java.lang.String r4 = "ENCRYPTION_REQUEST"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 109(0x6d, float:1.53E-43)
            goto L_0x0a97
        L_0x0893:
            java.lang.String r4 = "MESSAGE_GEOLIVE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 16
            goto L_0x0a97
        L_0x089f:
            java.lang.String r4 = "CHAT_DELETE_YOU"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 75
            goto L_0x0a97
        L_0x08ab:
            java.lang.String r4 = "AUTH_UNKNOWN"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 106(0x6a, float:1.49E-43)
            goto L_0x0a97
        L_0x08b7:
            java.lang.String r4 = "PINNED_GIF"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 102(0x66, float:1.43E-43)
            goto L_0x0a97
        L_0x08c3:
            java.lang.String r4 = "PINNED_GEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 97
            goto L_0x0a97
        L_0x08cf:
            java.lang.String r4 = "PINNED_DOC"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 91
            goto L_0x0a97
        L_0x08db:
            java.lang.String r4 = "PINNED_GAME_SCORE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 100
            goto L_0x0a97
        L_0x08e7:
            java.lang.String r4 = "CHANNEL_MESSAGE_STICKER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 33
            goto L_0x0a97
        L_0x08f3:
            java.lang.String r4 = "PHONE_CALL_REQUEST"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 111(0x6f, float:1.56E-43)
            goto L_0x0a97
        L_0x08ff:
            java.lang.String r4 = "PINNED_STICKER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 92
            goto L_0x0a97
        L_0x090b:
            java.lang.String r4 = "PINNED_TEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 86
            goto L_0x0a97
        L_0x0917:
            java.lang.String r4 = "PINNED_QUIZ"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 95
            goto L_0x0a97
        L_0x0923:
            java.lang.String r4 = "PINNED_POLL"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 96
            goto L_0x0a97
        L_0x092f:
            java.lang.String r4 = "PINNED_GAME"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 99
            goto L_0x0a97
        L_0x093b:
            java.lang.String r4 = "CHAT_MESSAGE_CONTACT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 56
            goto L_0x0a97
        L_0x0947:
            java.lang.String r4 = "MESSAGE_VIDEO_SECRET"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 6
            goto L_0x0a97
        L_0x0952:
            java.lang.String r4 = "CHANNEL_MESSAGE_TEXT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 1
            goto L_0x0a97
        L_0x095d:
            java.lang.String r4 = "CHANNEL_MESSAGE_QUIZ"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 36
            goto L_0x0a97
        L_0x0969:
            java.lang.String r4 = "CHANNEL_MESSAGE_POLL"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 37
            goto L_0x0a97
        L_0x0975:
            java.lang.String r4 = "CHANNEL_MESSAGE_GAME"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 41
            goto L_0x0a97
        L_0x0981:
            java.lang.String r4 = "CHANNEL_MESSAGE_FWDS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 42
            goto L_0x0a97
        L_0x098d:
            java.lang.String r4 = "CHANNEL_MESSAGE_DOCS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 46
            goto L_0x0a97
        L_0x0999:
            java.lang.String r4 = "PINNED_INVOICE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 101(0x65, float:1.42E-43)
            goto L_0x0a97
        L_0x09a5:
            java.lang.String r4 = "CHAT_RETURNED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 77
            goto L_0x0a97
        L_0x09b1:
            java.lang.String r4 = "ENCRYPTED_MESSAGE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 103(0x67, float:1.44E-43)
            goto L_0x0a97
        L_0x09bd:
            java.lang.String r4 = "ENCRYPTION_ACCEPT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 110(0x6e, float:1.54E-43)
            goto L_0x0a97
        L_0x09c9:
            java.lang.String r4 = "MESSAGE_VIDEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 5
            goto L_0x0a97
        L_0x09d4:
            java.lang.String r4 = "MESSAGE_ROUND"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 8
            goto L_0x0a97
        L_0x09e0:
            java.lang.String r4 = "MESSAGE_PHOTO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 3
            goto L_0x0a97
        L_0x09eb:
            java.lang.String r4 = "MESSAGE_MUTED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 112(0x70, float:1.57E-43)
            goto L_0x0a97
        L_0x09f7:
            java.lang.String r4 = "MESSAGE_AUDIO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 11
            goto L_0x0a97
        L_0x0a03:
            java.lang.String r4 = "CHAT_MESSAGES"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 85
            goto L_0x0a97
        L_0x0a0f:
            java.lang.String r4 = "CHAT_VOICECHAT_START"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 70
            goto L_0x0a97
        L_0x0a1b:
            java.lang.String r4 = "CHAT_REQ_JOINED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 79
            goto L_0x0a97
        L_0x0a27:
            java.lang.String r4 = "CHAT_JOINED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 78
            goto L_0x0a97
        L_0x0a33:
            java.lang.String r4 = "CHAT_ADD_MEMBER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 69
            goto L_0x0a97
        L_0x0a3e:
            java.lang.String r4 = "CHANNEL_MESSAGE_GIF"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 40
            goto L_0x0a97
        L_0x0a49:
            java.lang.String r4 = "CHANNEL_MESSAGE_GEO"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 38
            goto L_0x0a97
        L_0x0a54:
            java.lang.String r4 = "CHANNEL_MESSAGE_DOC"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 32
            goto L_0x0a97
        L_0x0a5f:
            java.lang.String r4 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 44
            goto L_0x0a97
        L_0x0a6a:
            java.lang.String r4 = "MESSAGE_STICKER"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 10
            goto L_0x0a97
        L_0x0a75:
            java.lang.String r4 = "CHAT_CREATED"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 65
            goto L_0x0a97
        L_0x0a80:
            java.lang.String r4 = "CHANNEL_MESSAGE_CONTACT"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 35
            goto L_0x0a97
        L_0x0a8b:
            java.lang.String r4 = "MESSAGE_GAME_SCORE"
            boolean r4 = r14.equals(r4)     // Catch:{ all -> 0x03ee }
            if (r4 == 0) goto L_0x0a96
            r4 = 19
            goto L_0x0a97
        L_0x0a96:
            r4 = -1
        L_0x0a97:
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
                case 0: goto L_0x1bbc;
                case 1: goto L_0x1bbc;
                case 2: goto L_0x1b99;
                case 3: goto L_0x1b7c;
                case 4: goto L_0x1b5f;
                case 5: goto L_0x1b42;
                case 6: goto L_0x1b24;
                case 7: goto L_0x1b08;
                case 8: goto L_0x1aea;
                case 9: goto L_0x1acc;
                case 10: goto L_0x1a71;
                case 11: goto L_0x1a53;
                case 12: goto L_0x1a30;
                case 13: goto L_0x1a0d;
                case 14: goto L_0x19ea;
                case 15: goto L_0x19cc;
                case 16: goto L_0x19ae;
                case 17: goto L_0x1990;
                case 18: goto L_0x196d;
                case 19: goto L_0x194e;
                case 20: goto L_0x194e;
                case 21: goto L_0x192b;
                case 22: goto L_0x1903;
                case 23: goto L_0x18df;
                case 24: goto L_0x18bc;
                case 25: goto L_0x1899;
                case 26: goto L_0x1874;
                case 27: goto L_0x185e;
                case 28: goto L_0x1840;
                case 29: goto L_0x1822;
                case 30: goto L_0x1804;
                case 31: goto L_0x17e6;
                case 32: goto L_0x17c8;
                case 33: goto L_0x176d;
                case 34: goto L_0x174f;
                case 35: goto L_0x172c;
                case 36: goto L_0x1709;
                case 37: goto L_0x16e6;
                case 38: goto L_0x16c8;
                case 39: goto L_0x16aa;
                case 40: goto L_0x168c;
                case 41: goto L_0x166e;
                case 42: goto L_0x1644;
                case 43: goto L_0x1620;
                case 44: goto L_0x15fc;
                case 45: goto L_0x15d8;
                case 46: goto L_0x15b2;
                case 47: goto L_0x159d;
                case 48: goto L_0x157c;
                case 49: goto L_0x1559;
                case 50: goto L_0x1536;
                case 51: goto L_0x1513;
                case 52: goto L_0x14f0;
                case 53: goto L_0x14cd;
                case 54: goto L_0x1454;
                case 55: goto L_0x1431;
                case 56: goto L_0x1409;
                case 57: goto L_0x13e1;
                case 58: goto L_0x13b9;
                case 59: goto L_0x1396;
                case 60: goto L_0x1373;
                case 61: goto L_0x1350;
                case 62: goto L_0x1328;
                case 63: goto L_0x1304;
                case 64: goto L_0x12dc;
                case 65: goto L_0x12c2;
                case 66: goto L_0x12c2;
                case 67: goto L_0x12a8;
                case 68: goto L_0x128e;
                case 69: goto L_0x126f;
                case 70: goto L_0x1255;
                case 71: goto L_0x1236;
                case 72: goto L_0x121c;
                case 73: goto L_0x1202;
                case 74: goto L_0x11e8;
                case 75: goto L_0x11ce;
                case 76: goto L_0x11b4;
                case 77: goto L_0x119a;
                case 78: goto L_0x1180;
                case 79: goto L_0x1166;
                case 80: goto L_0x1139;
                case 81: goto L_0x1110;
                case 82: goto L_0x10e7;
                case 83: goto L_0x10be;
                case 84: goto L_0x1093;
                case 85: goto L_0x1079;
                case 86: goto L_0x1022;
                case 87: goto L_0x0fd5;
                case 88: goto L_0x0var_;
                case 89: goto L_0x0f3b;
                case 90: goto L_0x0eee;
                case 91: goto L_0x0ea1;
                case 92: goto L_0x0de8;
                case 93: goto L_0x0d9b;
                case 94: goto L_0x0d44;
                case 95: goto L_0x0ced;
                case 96: goto L_0x0c9b;
                case 97: goto L_0x0c4d;
                case 98: goto L_0x0CLASSNAME;
                case 99: goto L_0x0bb9;
                case 100: goto L_0x0b6e;
                case 101: goto L_0x0b23;
                case 102: goto L_0x0ad8;
                case 103: goto L_0x0abc;
                case 104: goto L_0x0ab8;
                case 105: goto L_0x0ab8;
                case 106: goto L_0x0ab8;
                case 107: goto L_0x0ab8;
                case 108: goto L_0x0ab8;
                case 109: goto L_0x0ab8;
                case 110: goto L_0x0ab8;
                case 111: goto L_0x0ab8;
                case 112: goto L_0x0ab8;
                case 113: goto L_0x0ab8;
                default: goto L_0x0ab2;
            }
        L_0x0ab2:
            r4 = r22
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x03ee }
            goto L_0x1bd7
        L_0x0ab8:
            r4 = r22
            goto L_0x1bed
        L_0x0abc:
            java.lang.String r1 = "YouHaveNewMessage"
            r2 = 2131628795(0x7f0e12fb, float:1.8884893E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "SecretChatName"
            r3 = 2131627737(0x7f0e0ed9, float:1.8882747E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            r4 = r22
            r3 = 1
            r17 = 0
            r22 = r2
        L_0x0ad5:
            r2 = r1
            goto L_0x1bf3
        L_0x0ad8:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0af6
            java.lang.String r1 = "NotificationActionPinnedGifUser"
            r2 = 2131626609(0x7f0e0a71, float:1.888046E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0af6:
            if (r8 == 0) goto L_0x0b10
            java.lang.String r1 = "NotificationActionPinnedGif"
            r2 = 2131626607(0x7f0e0a6f, float:1.8880455E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0b10:
            java.lang.String r1 = "NotificationActionPinnedGifChannel"
            r2 = 2131626608(0x7f0e0a70, float:1.8880457E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03ee }
            r5[r4] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0b23:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0b41
            java.lang.String r1 = "NotificationActionPinnedInvoiceUser"
            r2 = 2131626612(0x7f0e0a74, float:1.8880465E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0b41:
            if (r8 == 0) goto L_0x0b5b
            java.lang.String r1 = "NotificationActionPinnedInvoice"
            r2 = 2131626610(0x7f0e0a72, float:1.8880461E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0b5b:
            java.lang.String r1 = "NotificationActionPinnedInvoiceChannel"
            r2 = 2131626611(0x7f0e0a73, float:1.8880463E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03ee }
            r5[r4] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0b6e:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0b8c
            java.lang.String r1 = "NotificationActionPinnedGameScoreUser"
            r2 = 2131626599(0x7f0e0a67, float:1.8880439E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0b8c:
            if (r8 == 0) goto L_0x0ba6
            java.lang.String r1 = "NotificationActionPinnedGameScore"
            r2 = 2131626597(0x7f0e0a65, float:1.8880435E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0ba6:
            java.lang.String r1 = "NotificationActionPinnedGameScoreChannel"
            r2 = 2131626598(0x7f0e0a66, float:1.8880437E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03ee }
            r5[r4] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0bb9:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0bd7
            java.lang.String r1 = "NotificationActionPinnedGameUser"
            r2 = 2131626600(0x7f0e0a68, float:1.888044E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0bd7:
            if (r8 == 0) goto L_0x0bf1
            java.lang.String r1 = "NotificationActionPinnedGame"
            r2 = 2131626595(0x7f0e0a63, float:1.888043E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0bf1:
            java.lang.String r1 = "NotificationActionPinnedGameChannel"
            r2 = 2131626596(0x7f0e0a64, float:1.8880433E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03ee }
            r5[r4] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGeoLiveUser"
            r2 = 2131626605(0x7f0e0a6d, float:1.888045E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            if (r8 == 0) goto L_0x0c3b
            java.lang.String r1 = "NotificationActionPinnedGeoLive"
            r2 = 2131626603(0x7f0e0a6b, float:1.8880447E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0c3b:
            java.lang.String r1 = "NotificationActionPinnedGeoLiveChannel"
            r2 = 2131626604(0x7f0e0a6c, float:1.8880449E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03ee }
            r5[r4] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0c4d:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0c6a
            java.lang.String r1 = "NotificationActionPinnedGeoUser"
            r2 = 2131626606(0x7f0e0a6e, float:1.8880453E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0c6a:
            if (r8 == 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGeo"
            r2 = 2131626601(0x7f0e0a69, float:1.8880443E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.lang.String r1 = "NotificationActionPinnedGeoChannel"
            r2 = 2131626602(0x7f0e0a6a, float:1.8880445E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x03ee }
            r5[r4] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
        L_0x0CLASSNAME:
            r2 = r1
            r4 = r22
            r22 = r39
            goto L_0x1bf0
        L_0x0c9b:
            r1 = 0
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0cb8
            java.lang.String r1 = "NotificationActionPinnedPollUser"
            r2 = 2131626624(0x7f0e0a80, float:1.888049E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0cb8:
            if (r8 == 0) goto L_0x0cd6
            java.lang.String r1 = "NotificationActionPinnedPoll2"
            r2 = 2131626622(0x7f0e0a7e, float:1.8880485E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 2
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r7 = 1
            r4[r7] = r6     // Catch:{ all -> 0x03ee }
            r3 = r3[r7]     // Catch:{ all -> 0x03ee }
            r4[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0cd6:
            java.lang.String r1 = "NotificationActionPinnedPollChannel2"
            r2 = 2131626623(0x7f0e0a7f, float:1.8880487E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r4[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x03ee }
            goto L_0x0CLASSNAME
        L_0x0ced:
            r4 = r22
            r5 = 0
            int r2 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r2 <= 0) goto L_0x0d0d
            java.lang.String r1 = "NotificationActionPinnedQuizUser"
            r2 = 2131626627(0x7f0e0a83, float:1.8880496E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0d0d:
            if (r8 == 0) goto L_0x0d2c
            java.lang.String r2 = "NotificationActionPinnedQuiz2"
            r5 = 2131626625(0x7f0e0a81, float:1.8880491E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 2
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r8 = 1
            r1[r8] = r7     // Catch:{ all -> 0x03ee }
            r3 = r3[r8]     // Catch:{ all -> 0x03ee }
            r1[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0d2c:
            java.lang.String r1 = "NotificationActionPinnedQuizChannel2"
            r2 = 2131626626(0x7f0e0a82, float:1.8880494E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0d44:
            r4 = r22
            r5 = 0
            int r2 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r2 <= 0) goto L_0x0d64
            java.lang.String r1 = "NotificationActionPinnedContactUser"
            r2 = 2131626591(0x7f0e0a5f, float:1.8880423E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0d64:
            if (r8 == 0) goto L_0x0d83
            java.lang.String r2 = "NotificationActionPinnedContact2"
            r5 = 2131626589(0x7f0e0a5d, float:1.8880418E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 2
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r8 = 1
            r1[r8] = r7     // Catch:{ all -> 0x03ee }
            r3 = r3[r8]     // Catch:{ all -> 0x03ee }
            r1[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0d83:
            java.lang.String r1 = "NotificationActionPinnedContactChannel2"
            r2 = 2131626590(0x7f0e0a5e, float:1.888042E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0d9b:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0dbb
            java.lang.String r1 = "NotificationActionPinnedVoiceUser"
            r2 = 2131626645(0x7f0e0a95, float:1.8880532E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0dbb:
            if (r8 == 0) goto L_0x0dd5
            java.lang.String r1 = "NotificationActionPinnedVoice"
            r2 = 2131626643(0x7f0e0a93, float:1.8880528E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0dd5:
            java.lang.String r1 = "NotificationActionPinnedVoiceChannel"
            r2 = 2131626644(0x7f0e0a94, float:1.888053E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0de8:
            r4 = r22
            r5 = 0
            int r2 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e27
            int r1 = r3.length     // Catch:{ all -> 0x03ee }
            r2 = 1
            if (r1 <= r2) goto L_0x0e14
            r1 = r3[r2]     // Catch:{ all -> 0x03ee }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x03ee }
            if (r1 != 0) goto L_0x0e14
            java.lang.String r1 = "NotificationActionPinnedStickerEmojiUser"
            r2 = 2131626635(0x7f0e0a8b, float:1.8880512E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0e14:
            java.lang.String r1 = "NotificationActionPinnedStickerUser"
            r2 = 2131626636(0x7f0e0a8c, float:1.8880514E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0e27:
            if (r8 == 0) goto L_0x0e6a
            int r2 = r3.length     // Catch:{ all -> 0x03ee }
            r5 = 2
            if (r2 <= r5) goto L_0x0e52
            r2 = r3[r5]     // Catch:{ all -> 0x03ee }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x03ee }
            if (r2 != 0) goto L_0x0e52
            java.lang.String r2 = "NotificationActionPinnedStickerEmoji"
            r5 = 2131626633(0x7f0e0a89, float:1.8880508E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 2
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r8 = 1
            r1[r8] = r7     // Catch:{ all -> 0x03ee }
            r3 = r3[r8]     // Catch:{ all -> 0x03ee }
            r1[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0e52:
            java.lang.String r1 = "NotificationActionPinnedSticker"
            r2 = 2131626631(0x7f0e0a87, float:1.8880504E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0e6a:
            int r1 = r3.length     // Catch:{ all -> 0x03ee }
            r2 = 1
            if (r1 <= r2) goto L_0x0e8e
            r1 = r3[r2]     // Catch:{ all -> 0x03ee }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x03ee }
            if (r1 != 0) goto L_0x0e8e
            java.lang.String r1 = "NotificationActionPinnedStickerEmojiChannel"
            r2 = 2131626634(0x7f0e0a8a, float:1.888051E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0e8e:
            java.lang.String r1 = "NotificationActionPinnedStickerChannel"
            r2 = 2131626632(0x7f0e0a88, float:1.8880506E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0ea1:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0ec1
            java.lang.String r1 = "NotificationActionPinnedFileUser"
            r2 = 2131626594(0x7f0e0a62, float:1.8880429E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0ec1:
            if (r8 == 0) goto L_0x0edb
            java.lang.String r1 = "NotificationActionPinnedFile"
            r2 = 2131626592(0x7f0e0a60, float:1.8880425E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0edb:
            java.lang.String r1 = "NotificationActionPinnedFileChannel"
            r2 = 2131626593(0x7f0e0a61, float:1.8880427E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0eee:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0f0e
            java.lang.String r1 = "NotificationActionPinnedRoundUser"
            r2 = 2131626630(0x7f0e0a86, float:1.8880502E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0f0e:
            if (r8 == 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedRound"
            r2 = 2131626628(0x7f0e0a84, float:1.8880498E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0var_:
            java.lang.String r1 = "NotificationActionPinnedRoundChannel"
            r2 = 2131626629(0x7f0e0a85, float:1.88805E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0f3b:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0f5b
            java.lang.String r1 = "NotificationActionPinnedVideoUser"
            r2 = 2131626642(0x7f0e0a92, float:1.8880526E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0f5b:
            if (r8 == 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedVideo"
            r2 = 2131626640(0x7f0e0a90, float:1.8880522E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0var_:
            java.lang.String r1 = "NotificationActionPinnedVideoChannel"
            r2 = 2131626641(0x7f0e0a91, float:1.8880524E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0var_:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0fa8
            java.lang.String r1 = "NotificationActionPinnedPhotoUser"
            r2 = 2131626621(0x7f0e0a7d, float:1.8880483E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0fa8:
            if (r8 == 0) goto L_0x0fc2
            java.lang.String r1 = "NotificationActionPinnedPhoto"
            r2 = 2131626619(0x7f0e0a7b, float:1.888048E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0fc2:
            java.lang.String r1 = "NotificationActionPinnedPhotoChannel"
            r2 = 2131626620(0x7f0e0a7c, float:1.8880481E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0fd5:
            r4 = r22
            r1 = 0
            int r5 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0ff5
            java.lang.String r1 = "NotificationActionPinnedNoTextUser"
            r2 = 2131626618(0x7f0e0a7a, float:1.8880477E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x0ff5:
            if (r8 == 0) goto L_0x100f
            java.lang.String r1 = "NotificationActionPinnedNoText"
            r2 = 2131626616(0x7f0e0a78, float:1.8880473E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x100f:
            java.lang.String r1 = "NotificationActionPinnedNoTextChannel"
            r2 = 2131626617(0x7f0e0a79, float:1.8880475E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x1022:
            r4 = r22
            r5 = 0
            int r2 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r2 <= 0) goto L_0x1042
            java.lang.String r1 = "NotificationActionPinnedTextUser"
            r2 = 2131626639(0x7f0e0a8f, float:1.888052E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x1042:
            if (r8 == 0) goto L_0x1061
            java.lang.String r2 = "NotificationActionPinnedText"
            r5 = 2131626637(0x7f0e0a8d, float:1.8880516E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x1061:
            java.lang.String r1 = "NotificationActionPinnedTextChannel"
            r2 = 2131626638(0x7f0e0a8e, float:1.8880518E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x1079:
            r4 = r22
            java.lang.String r1 = "NotificationGroupAlbum"
            r2 = 2131626654(0x7f0e0a9e, float:1.888055E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1871
        L_0x1093:
            r4 = r22
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r1[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r1[r5] = r6     // Catch:{ all -> 0x03ee }
            java.lang.String r5 = "Files"
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03ee }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03ee }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)     // Catch:{ all -> 0x03ee }
            r1[r6] = r3     // Catch:{ all -> 0x03ee }
            r3 = 2131626657(0x7f0e0aa1, float:1.8880556E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r3, r1)     // Catch:{ all -> 0x03ee }
            goto L_0x1871
        L_0x10be:
            r4 = r22
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03ee }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03ee }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)     // Catch:{ all -> 0x03ee }
            r1[r6] = r3     // Catch:{ all -> 0x03ee }
            r3 = 2131626657(0x7f0e0aa1, float:1.8880556E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r3, r1)     // Catch:{ all -> 0x03ee }
            goto L_0x1871
        L_0x10e7:
            r4 = r22
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r1[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r1[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 2
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03ee }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03ee }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r15, r3)     // Catch:{ all -> 0x03ee }
            r1[r5] = r3     // Catch:{ all -> 0x03ee }
            r3 = 2131626657(0x7f0e0aa1, float:1.8880556E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r3, r1)     // Catch:{ all -> 0x03ee }
            goto L_0x1871
        L_0x1110:
            r4 = r22
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r1[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r1[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 2
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03ee }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03ee }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r11, r3)     // Catch:{ all -> 0x03ee }
            r1[r5] = r3     // Catch:{ all -> 0x03ee }
            r3 = 2131626657(0x7f0e0aa1, float:1.8880556E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r3, r1)     // Catch:{ all -> 0x03ee }
            goto L_0x1871
        L_0x1139:
            r4 = r22
            java.lang.String r2 = "NotificationGroupForwardedFew"
            r5 = 2131626658(0x7f0e0aa2, float:1.8880558E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03ee }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03ee }
            r7 = r27
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r7, r3)     // Catch:{ all -> 0x03ee }
            r1[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03ee }
            goto L_0x1871
        L_0x1166:
            r4 = r22
            java.lang.String r1 = "UserAcceptedToGroupPushWithGroup"
            r2 = 2131628348(0x7f0e113c, float:1.8883986E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x1180:
            r4 = r22
            java.lang.String r1 = "NotificationGroupAddSelfMega"
            r2 = 2131626653(0x7f0e0a9d, float:1.8880548E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x119a:
            r4 = r22
            java.lang.String r1 = "NotificationGroupAddSelf"
            r2 = 2131626652(0x7f0e0a9c, float:1.8880546E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x11b4:
            r4 = r22
            java.lang.String r1 = "NotificationGroupLeftMember"
            r2 = 2131626663(0x7f0e0aa7, float:1.8880569E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x11ce:
            r4 = r22
            java.lang.String r1 = "NotificationGroupKickYou"
            r2 = 2131626662(0x7f0e0aa6, float:1.8880567E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x11e8:
            r4 = r22
            java.lang.String r1 = "NotificationGroupKickMember"
            r2 = 2131626661(0x7f0e0aa5, float:1.8880564E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x1202:
            r4 = r22
            java.lang.String r1 = "NotificationGroupInvitedYouToCall"
            r2 = 2131626660(0x7f0e0aa4, float:1.8880562E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x121c:
            r4 = r22
            java.lang.String r1 = "NotificationGroupEndedCall"
            r2 = 2131626656(0x7f0e0aa0, float:1.8880554E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x1236:
            r4 = r22
            java.lang.String r2 = "NotificationGroupInvitedToCall"
            r5 = 2131626659(0x7f0e0aa3, float:1.888056E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x1255:
            r4 = r22
            java.lang.String r1 = "NotificationGroupCreatedCall"
            r2 = 2131626655(0x7f0e0a9f, float:1.8880552E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x126f:
            r4 = r22
            java.lang.String r2 = "NotificationGroupAddMember"
            r5 = 2131626651(0x7f0e0a9b, float:1.8880544E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x128e:
            r4 = r22
            java.lang.String r1 = "NotificationEditedGroupPhoto"
            r2 = 2131626649(0x7f0e0a99, float:1.888054E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x12a8:
            r4 = r22
            java.lang.String r1 = "NotificationEditedGroupName"
            r2 = 2131626648(0x7f0e0a98, float:1.8880538E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x12c2:
            r4 = r22
            java.lang.String r1 = "NotificationInvitedToGroup"
            r2 = 2131626668(0x7f0e0aac, float:1.8880579E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x12dc:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupInvoice"
            r5 = 2131626685(0x7f0e0abd, float:1.8880613E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "PaymentInvoice"
            r3 = 2131627080(0x7f0e0CLASSNAME, float:1.8881414E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1304:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupGameScored"
            r5 = 2131626683(0x7f0e0abb, float:1.888061E38)
            r6 = 4
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x03ee }
            r7 = 0
            r8 = r3[r7]     // Catch:{ all -> 0x03ee }
            r6[r7] = r8     // Catch:{ all -> 0x03ee }
            r7 = 1
            r8 = r3[r7]     // Catch:{ all -> 0x03ee }
            r6[r7] = r8     // Catch:{ all -> 0x03ee }
            r7 = 2
            r8 = r3[r7]     // Catch:{ all -> 0x03ee }
            r6[r7] = r8     // Catch:{ all -> 0x03ee }
            r1 = 3
            r3 = r3[r1]     // Catch:{ all -> 0x03ee }
            r6[r1] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x1328:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupGame"
            r5 = 2131626682(0x7f0e0aba, float:1.8880607E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachGame"
            r3 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1350:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupGif"
            r2 = 2131626684(0x7f0e0abc, float:1.8880611E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachGif"
            r3 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1373:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupLiveLocation"
            r2 = 2131626686(0x7f0e0abe, float:1.8880615E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachLiveLocation"
            r3 = 2131624413(0x7f0e01dd, float:1.8876005E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1396:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupMap"
            r2 = 2131626687(0x7f0e0abf, float:1.8880617E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachLocation"
            r3 = 2131624417(0x7f0e01e1, float:1.8876013E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x13b9:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupPoll2"
            r5 = 2131626691(0x7f0e0ac3, float:1.8880625E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "Poll"
            r3 = 2131627247(0x7f0e0cef, float:1.8881753E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x13e1:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupQuiz2"
            r5 = 2131626692(0x7f0e0ac4, float:1.8880627E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "PollQuiz"
            r3 = 2131627254(0x7f0e0cf6, float:1.8881767E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1409:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupContact2"
            r5 = 2131626680(0x7f0e0ab8, float:1.8880603E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachContact"
            r3 = 2131624403(0x7f0e01d3, float:1.8875985E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1431:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupAudio"
            r2 = 2131626679(0x7f0e0ab7, float:1.88806E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachAudio"
            r3 = 2131624401(0x7f0e01d1, float:1.887598E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1454:
            r4 = r22
            int r2 = r3.length     // Catch:{ all -> 0x03ee }
            r5 = 2
            if (r2 <= r5) goto L_0x149a
            r2 = r3[r5]     // Catch:{ all -> 0x03ee }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x03ee }
            if (r2 != 0) goto L_0x149a
            java.lang.String r2 = "NotificationMessageGroupStickerEmoji"
            r5 = 2131626695(0x7f0e0ac7, float:1.8880633E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r7 = 0
            r8 = r3[r7]     // Catch:{ all -> 0x03ee }
            r1[r7] = r8     // Catch:{ all -> 0x03ee }
            r7 = 1
            r8 = r3[r7]     // Catch:{ all -> 0x03ee }
            r1[r7] = r8     // Catch:{ all -> 0x03ee }
            r7 = 2
            r8 = r3[r7]     // Catch:{ all -> 0x03ee }
            r1[r7] = r8     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03ee }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ee }
            r2.<init>()     // Catch:{ all -> 0x03ee }
            r3 = r3[r7]     // Catch:{ all -> 0x03ee }
            r2.append(r3)     // Catch:{ all -> 0x03ee }
            r2.append(r9)     // Catch:{ all -> 0x03ee }
            r3 = 2131624425(0x7f0e01e9, float:1.887603E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)     // Catch:{ all -> 0x03ee }
            r2.append(r3)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x149a:
            java.lang.String r1 = "NotificationMessageGroupSticker"
            r2 = 2131626694(0x7f0e0ac6, float:1.8880631E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r7 = 0
            r8 = r3[r7]     // Catch:{ all -> 0x03ee }
            r5[r7] = r8     // Catch:{ all -> 0x03ee }
            r7 = 1
            r8 = r3[r7]     // Catch:{ all -> 0x03ee }
            r5[r7] = r8     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ee }
            r2.<init>()     // Catch:{ all -> 0x03ee }
            r3 = r3[r7]     // Catch:{ all -> 0x03ee }
            r2.append(r3)     // Catch:{ all -> 0x03ee }
            r2.append(r9)     // Catch:{ all -> 0x03ee }
            r3 = 2131624425(0x7f0e01e9, float:1.887603E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)     // Catch:{ all -> 0x03ee }
            r2.append(r3)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x14cd:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupDocument"
            r2 = 2131626681(0x7f0e0ab9, float:1.8880605E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachDocument"
            r3 = 2131624406(0x7f0e01d6, float:1.887599E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x14f0:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupRound"
            r2 = 2131626693(0x7f0e0ac5, float:1.888063E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachRound"
            r3 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1513:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupVideo"
            r2 = 2131626697(0x7f0e0ac9, float:1.8880638E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachVideo"
            r3 = 2131624428(0x7f0e01ec, float:1.8876035E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1536:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupPhoto"
            r2 = 2131626690(0x7f0e0ac2, float:1.8880623E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachPhoto"
            r3 = 2131624422(0x7f0e01e6, float:1.8876023E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1559:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGroupNoText"
            r2 = 2131626689(0x7f0e0ac1, float:1.8880621E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "Message"
            r3 = 2131626338(0x7f0e0962, float:1.887991E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x157c:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGroupText"
            r5 = 2131626696(0x7f0e0ac8, float:1.8880635E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 2
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03ee }
            r2 = r3[r6]     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x159d:
            r4 = r22
            java.lang.String r1 = "ChannelMessageAlbum"
            r2 = 2131624787(0x7f0e0353, float:1.8876764E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            goto L_0x1871
        L_0x15b2:
            r4 = r22
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r2 = 0
            r5 = r3[r2]     // Catch:{ all -> 0x03ee }
            r1[r2] = r5     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "Files"
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03ee }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r3)     // Catch:{ all -> 0x03ee }
            r1[r5] = r2     // Catch:{ all -> 0x03ee }
            r2 = 2131624791(0x7f0e0357, float:1.8876772E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r2, r1)     // Catch:{ all -> 0x03ee }
            goto L_0x1871
        L_0x15d8:
            r4 = r22
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r2 = 0
            r6 = r3[r2]     // Catch:{ all -> 0x03ee }
            r1[r2] = r6     // Catch:{ all -> 0x03ee }
            r2 = 1
            r3 = r3[r2]     // Catch:{ all -> 0x03ee }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03ee }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03ee }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)     // Catch:{ all -> 0x03ee }
            r1[r2] = r3     // Catch:{ all -> 0x03ee }
            r2 = 2131624791(0x7f0e0357, float:1.8876772E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r2, r1)     // Catch:{ all -> 0x03ee }
            goto L_0x1871
        L_0x15fc:
            r4 = r22
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r2 = 0
            r5 = r3[r2]     // Catch:{ all -> 0x03ee }
            r1[r2] = r5     // Catch:{ all -> 0x03ee }
            r2 = 1
            r3 = r3[r2]     // Catch:{ all -> 0x03ee }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03ee }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03ee }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r15, r3)     // Catch:{ all -> 0x03ee }
            r1[r2] = r3     // Catch:{ all -> 0x03ee }
            r2 = 2131624791(0x7f0e0357, float:1.8876772E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r2, r1)     // Catch:{ all -> 0x03ee }
            goto L_0x1871
        L_0x1620:
            r4 = r22
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r2 = 0
            r5 = r3[r2]     // Catch:{ all -> 0x03ee }
            r1[r2] = r5     // Catch:{ all -> 0x03ee }
            r2 = 1
            r3 = r3[r2]     // Catch:{ all -> 0x03ee }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03ee }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03ee }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r11, r3)     // Catch:{ all -> 0x03ee }
            r1[r2] = r3     // Catch:{ all -> 0x03ee }
            r2 = 2131624791(0x7f0e0357, float:1.8876772E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r2, r1)     // Catch:{ all -> 0x03ee }
            goto L_0x1871
        L_0x1644:
            r4 = r22
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r2 = 0
            r5 = r3[r2]     // Catch:{ all -> 0x03ee }
            r1[r2] = r5     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "ForwardedMessageCount"
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03ee }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r3)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = r2.toLowerCase()     // Catch:{ all -> 0x03ee }
            r1[r5] = r2     // Catch:{ all -> 0x03ee }
            r2 = 2131624791(0x7f0e0357, float:1.8876772E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r2, r1)     // Catch:{ all -> 0x03ee }
            goto L_0x1871
        L_0x166e:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGame"
            r2 = 2131626676(0x7f0e0ab4, float:1.8880595E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachGame"
            r3 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x168c:
            r4 = r22
            java.lang.String r1 = "ChannelMessageGIF"
            r2 = 2131624792(0x7f0e0358, float:1.8876774E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachGif"
            r3 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x16aa:
            r4 = r22
            java.lang.String r1 = "ChannelMessageLiveLocation"
            r2 = 2131624793(0x7f0e0359, float:1.8876776E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachLiveLocation"
            r3 = 2131624413(0x7f0e01dd, float:1.8876005E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x16c8:
            r4 = r22
            java.lang.String r1 = "ChannelMessageMap"
            r2 = 2131624794(0x7f0e035a, float:1.8876778E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachLocation"
            r3 = 2131624417(0x7f0e01e1, float:1.8876013E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x16e6:
            r4 = r22
            java.lang.String r1 = "ChannelMessagePoll2"
            r2 = 2131624798(0x7f0e035e, float:1.8876786E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "Poll"
            r3 = 2131627247(0x7f0e0cef, float:1.8881753E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1709:
            r4 = r22
            java.lang.String r1 = "ChannelMessageQuiz2"
            r2 = 2131624799(0x7f0e035f, float:1.8876788E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "QuizPoll"
            r3 = 2131627461(0x7f0e0dc5, float:1.8882187E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x172c:
            r4 = r22
            java.lang.String r1 = "ChannelMessageContact2"
            r2 = 2131624789(0x7f0e0355, float:1.8876768E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachContact"
            r3 = 2131624403(0x7f0e01d3, float:1.8875985E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x174f:
            r4 = r22
            java.lang.String r1 = "ChannelMessageAudio"
            r2 = 2131624788(0x7f0e0354, float:1.8876766E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachAudio"
            r3 = 2131624401(0x7f0e01d1, float:1.887598E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x176d:
            r4 = r22
            int r1 = r3.length     // Catch:{ all -> 0x03ee }
            r2 = 1
            if (r1 <= r2) goto L_0x17ae
            r1 = r3[r2]     // Catch:{ all -> 0x03ee }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x03ee }
            if (r1 != 0) goto L_0x17ae
            java.lang.String r1 = "ChannelMessageStickerEmoji"
            r2 = 2131624802(0x7f0e0362, float:1.8876794E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r7 = 0
            r8 = r3[r7]     // Catch:{ all -> 0x03ee }
            r5[r7] = r8     // Catch:{ all -> 0x03ee }
            r7 = 1
            r8 = r3[r7]     // Catch:{ all -> 0x03ee }
            r5[r7] = r8     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ee }
            r2.<init>()     // Catch:{ all -> 0x03ee }
            r3 = r3[r7]     // Catch:{ all -> 0x03ee }
            r2.append(r3)     // Catch:{ all -> 0x03ee }
            r2.append(r9)     // Catch:{ all -> 0x03ee }
            r3 = 2131624425(0x7f0e01e9, float:1.887603E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)     // Catch:{ all -> 0x03ee }
            r2.append(r3)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x17ae:
            java.lang.String r1 = "ChannelMessageSticker"
            r2 = 2131624801(0x7f0e0361, float:1.8876792E38)
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r7[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x03ee }
            r2 = 2131624425(0x7f0e01e9, float:1.887603E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x17c8:
            r4 = r22
            java.lang.String r1 = "ChannelMessageDocument"
            r2 = 2131624790(0x7f0e0356, float:1.887677E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachDocument"
            r3 = 2131624406(0x7f0e01d6, float:1.887599E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x17e6:
            r4 = r22
            java.lang.String r1 = "ChannelMessageRound"
            r2 = 2131624800(0x7f0e0360, float:1.887679E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachRound"
            r3 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1804:
            r4 = r22
            java.lang.String r1 = "ChannelMessageVideo"
            r2 = 2131624803(0x7f0e0363, float:1.8876796E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachVideo"
            r3 = 2131624428(0x7f0e01ec, float:1.8876035E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1822:
            r4 = r22
            java.lang.String r1 = "ChannelMessagePhoto"
            r2 = 2131624797(0x7f0e035d, float:1.8876784E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachPhoto"
            r3 = 2131624422(0x7f0e01e6, float:1.8876023E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1840:
            r4 = r22
            java.lang.String r1 = "ChannelMessageNoText"
            r2 = 2131624796(0x7f0e035c, float:1.8876782E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "Message"
            r3 = 2131626338(0x7f0e0962, float:1.887991E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x185e:
            r4 = r22
            java.lang.String r1 = "NotificationMessageAlbum"
            r2 = 2131626670(0x7f0e0aae, float:1.8880583E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
        L_0x1871:
            r3 = 1
            goto L_0x1b1d
        L_0x1874:
            r4 = r22
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r2[r5] = r6     // Catch:{ all -> 0x03ee }
            java.lang.String r5 = "Files"
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03ee }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03ee }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)     // Catch:{ all -> 0x03ee }
            r2[r6] = r3     // Catch:{ all -> 0x03ee }
            r3 = 2131626674(0x7f0e0ab2, float:1.888059E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r2)     // Catch:{ all -> 0x03ee }
            goto L_0x1871
        L_0x1899:
            r4 = r22
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r2[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03ee }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03ee }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)     // Catch:{ all -> 0x03ee }
            r2[r6] = r3     // Catch:{ all -> 0x03ee }
            r3 = 2131626674(0x7f0e0ab2, float:1.888059E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r2)     // Catch:{ all -> 0x03ee }
            goto L_0x1871
        L_0x18bc:
            r4 = r22
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r2[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03ee }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03ee }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r15, r3)     // Catch:{ all -> 0x03ee }
            r2[r5] = r3     // Catch:{ all -> 0x03ee }
            r3 = 2131626674(0x7f0e0ab2, float:1.888059E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r2)     // Catch:{ all -> 0x03ee }
            goto L_0x1871
        L_0x18df:
            r4 = r22
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r6 = r3[r5]     // Catch:{ all -> 0x03ee }
            r2[r5] = r6     // Catch:{ all -> 0x03ee }
            r5 = 1
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03ee }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03ee }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r11, r3)     // Catch:{ all -> 0x03ee }
            r2[r5] = r3     // Catch:{ all -> 0x03ee }
            r3 = 2131626674(0x7f0e0ab2, float:1.888059E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r2)     // Catch:{ all -> 0x03ee }
            goto L_0x1871
        L_0x1903:
            r4 = r22
            r7 = r27
            java.lang.String r1 = "NotificationMessageForwardFew"
            r2 = 2131626675(0x7f0e0ab3, float:1.8880593E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r8 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r8     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x03ee }
            int r3 = r3.intValue()     // Catch:{ all -> 0x03ee }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r7, r3)     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            goto L_0x1871
        L_0x192b:
            r4 = r22
            java.lang.String r1 = "NotificationMessageInvoice"
            r2 = 2131626698(0x7f0e0aca, float:1.888064E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "PaymentInvoice"
            r3 = 2131627080(0x7f0e0CLASSNAME, float:1.8881414E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x194e:
            r4 = r22
            java.lang.String r2 = "NotificationMessageGameScored"
            r5 = 2131626677(0x7f0e0ab5, float:1.8880597E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r1[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x03ee }
            goto L_0x1b1c
        L_0x196d:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGame"
            r2 = 2131626676(0x7f0e0ab4, float:1.8880595E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachGame"
            r3 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1990:
            r4 = r22
            java.lang.String r1 = "NotificationMessageGif"
            r2 = 2131626678(0x7f0e0ab6, float:1.8880599E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachGif"
            r3 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x19ae:
            r4 = r22
            java.lang.String r1 = "NotificationMessageLiveLocation"
            r2 = 2131626699(0x7f0e0acb, float:1.8880642E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachLiveLocation"
            r3 = 2131624413(0x7f0e01dd, float:1.8876005E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x19cc:
            r4 = r22
            java.lang.String r1 = "NotificationMessageMap"
            r2 = 2131626700(0x7f0e0acc, float:1.8880644E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachLocation"
            r3 = 2131624417(0x7f0e01e1, float:1.8876013E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x19ea:
            r4 = r22
            java.lang.String r1 = "NotificationMessagePoll2"
            r2 = 2131626704(0x7f0e0ad0, float:1.8880652E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "Poll"
            r3 = 2131627247(0x7f0e0cef, float:1.8881753E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1a0d:
            r4 = r22
            java.lang.String r1 = "NotificationMessageQuiz2"
            r2 = 2131626705(0x7f0e0ad1, float:1.8880654E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "QuizPoll"
            r3 = 2131627461(0x7f0e0dc5, float:1.8882187E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1a30:
            r4 = r22
            java.lang.String r1 = "NotificationMessageContact2"
            r2 = 2131626672(0x7f0e0ab0, float:1.8880587E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachContact"
            r3 = 2131624403(0x7f0e01d3, float:1.8875985E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1a53:
            r4 = r22
            java.lang.String r1 = "NotificationMessageAudio"
            r2 = 2131626671(0x7f0e0aaf, float:1.8880585E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachAudio"
            r3 = 2131624401(0x7f0e01d1, float:1.887598E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1a71:
            r4 = r22
            int r1 = r3.length     // Catch:{ all -> 0x03ee }
            r2 = 1
            if (r1 <= r2) goto L_0x1ab2
            r1 = r3[r2]     // Catch:{ all -> 0x03ee }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x03ee }
            if (r1 != 0) goto L_0x1ab2
            java.lang.String r1 = "NotificationMessageStickerEmoji"
            r2 = 2131626712(0x7f0e0ad8, float:1.8880668E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r7 = 0
            r8 = r3[r7]     // Catch:{ all -> 0x03ee }
            r5[r7] = r8     // Catch:{ all -> 0x03ee }
            r7 = 1
            r8 = r3[r7]     // Catch:{ all -> 0x03ee }
            r5[r7] = r8     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ee }
            r2.<init>()     // Catch:{ all -> 0x03ee }
            r3 = r3[r7]     // Catch:{ all -> 0x03ee }
            r2.append(r3)     // Catch:{ all -> 0x03ee }
            r2.append(r9)     // Catch:{ all -> 0x03ee }
            r3 = 2131624425(0x7f0e01e9, float:1.887603E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)     // Catch:{ all -> 0x03ee }
            r2.append(r3)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1ab2:
            java.lang.String r1 = "NotificationMessageSticker"
            r2 = 2131626711(0x7f0e0ad7, float:1.8880666E38)
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r7[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x03ee }
            r2 = 2131624425(0x7f0e01e9, float:1.887603E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1acc:
            r4 = r22
            java.lang.String r1 = "NotificationMessageDocument"
            r2 = 2131626673(0x7f0e0ab1, float:1.8880589E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachDocument"
            r3 = 2131624406(0x7f0e01d6, float:1.887599E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1aea:
            r4 = r22
            java.lang.String r1 = "NotificationMessageRound"
            r2 = 2131626706(0x7f0e0ad2, float:1.8880656E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachRound"
            r3 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1b08:
            r4 = r22
            java.lang.String r1 = "ActionTakeScreenshoot"
            r2 = 2131624173(0x7f0e00ed, float:1.8875518E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "un1"
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = r1.replace(r2, r3)     // Catch:{ all -> 0x03ee }
        L_0x1b1c:
            r3 = 0
        L_0x1b1d:
            r17 = 0
            r2 = r1
            r22 = r39
            goto L_0x1bf3
        L_0x1b24:
            r4 = r22
            java.lang.String r1 = "NotificationMessageSDVideo"
            r2 = 2131626708(0x7f0e0ad4, float:1.888066E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachDestructingVideo"
            r3 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1b42:
            r4 = r22
            java.lang.String r1 = "NotificationMessageVideo"
            r2 = 2131626714(0x7f0e0ada, float:1.8880672E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachVideo"
            r3 = 2131624428(0x7f0e01ec, float:1.8876035E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1b5f:
            r4 = r22
            java.lang.String r1 = "NotificationMessageSDPhoto"
            r2 = 2131626707(0x7f0e0ad3, float:1.8880658E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachDestructingPhoto"
            r3 = 2131624404(0x7f0e01d4, float:1.8875987E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1b7c:
            r4 = r22
            java.lang.String r1 = "NotificationMessagePhoto"
            r2 = 2131626703(0x7f0e0acf, float:1.888065E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "AttachPhoto"
            r3 = 2131624422(0x7f0e01e6, float:1.8876023E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1b99:
            r4 = r22
            java.lang.String r1 = "NotificationMessageNoText"
            r2 = 2131626702(0x7f0e0ace, float:1.8880648E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r5 = 0
            r3 = r3[r5]     // Catch:{ all -> 0x03ee }
            r6[r5] = r3     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "Message"
            r3 = 2131626338(0x7f0e0962, float:1.887991E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x03ee }
        L_0x1bb5:
            r17 = r2
            r22 = r39
            r3 = 0
            goto L_0x0ad5
        L_0x1bbc:
            r4 = r22
            java.lang.String r1 = "NotificationMessageText"
            r2 = 2131626713(0x7f0e0ad9, float:1.888067E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ee }
            r6 = 0
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            r6 = 1
            r7 = r3[r6]     // Catch:{ all -> 0x03ee }
            r5[r6] = r7     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x03ee }
            r2 = r3[r6]     // Catch:{ all -> 0x03ee }
            goto L_0x1bb5
        L_0x1bd7:
            if (r1 == 0) goto L_0x1bed
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ee }
            r1.<init>()     // Catch:{ all -> 0x03ee }
            java.lang.String r2 = "unhandled loc_key = "
            r1.append(r2)     // Catch:{ all -> 0x03ee }
            r1.append(r14)     // Catch:{ all -> 0x03ee }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x03ee }
            org.telegram.messenger.FileLog.w(r1)     // Catch:{ all -> 0x03ee }
        L_0x1bed:
            r22 = r39
            r2 = 0
        L_0x1bf0:
            r3 = 0
            r17 = 0
        L_0x1bf3:
            r1 = r49
            goto L_0x1c0b
        L_0x1bf6:
            r43 = r1
            r45 = r6
            r42 = r9
            r41 = r11
            r4 = r22
            r1 = r49
            java.lang.String r2 = r1.getReactedText(r14, r3)     // Catch:{ all -> 0x1cee }
            r22 = r39
            r3 = 0
            r17 = 0
        L_0x1c0b:
            if (r2 == 0) goto L_0x1cf0
            org.telegram.tgnet.TLRPC$TL_message r5 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x1cee }
            r5.<init>()     // Catch:{ all -> 0x1cee }
            r5.id = r10     // Catch:{ all -> 0x1cee }
            r6 = r30
            r5.random_id = r6     // Catch:{ all -> 0x1cee }
            if (r17 == 0) goto L_0x1c1d
            r6 = r17
            goto L_0x1c1e
        L_0x1c1d:
            r6 = r2
        L_0x1c1e:
            r5.message = r6     // Catch:{ all -> 0x1cee }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r6 = r51 / r6
            int r7 = (int) r6     // Catch:{ all -> 0x1cee }
            r5.date = r7     // Catch:{ all -> 0x1cee }
            if (r38 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r6 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x1cee }
            r6.<init>()     // Catch:{ all -> 0x1cee }
            r5.action = r6     // Catch:{ all -> 0x1cee }
        L_0x1CLASSNAME:
            if (r37 == 0) goto L_0x1CLASSNAME
            int r6 = r5.flags     // Catch:{ all -> 0x1cee }
            r7 = -2147483648(0xfffffffvar_, float:-0.0)
            r6 = r6 | r7
            r5.flags = r6     // Catch:{ all -> 0x1cee }
        L_0x1CLASSNAME:
            r5.dialog_id = r12     // Catch:{ all -> 0x1cee }
            r6 = 0
            int r8 = (r45 > r6 ? 1 : (r45 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x1c4f
            org.telegram.tgnet.TLRPC$TL_peerChannel r6 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x1cee }
            r6.<init>()     // Catch:{ all -> 0x1cee }
            r5.peer_id = r6     // Catch:{ all -> 0x1cee }
            r7 = r45
            r6.channel_id = r7     // Catch:{ all -> 0x1cee }
            r12 = r25
            goto L_0x1c6e
        L_0x1c4f:
            r6 = 0
            int r8 = (r25 > r6 ? 1 : (r25 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_peerChat r6 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x1cee }
            r6.<init>()     // Catch:{ all -> 0x1cee }
            r5.peer_id = r6     // Catch:{ all -> 0x1cee }
            r12 = r25
            r6.chat_id = r12     // Catch:{ all -> 0x1cee }
            goto L_0x1c6e
        L_0x1CLASSNAME:
            r12 = r25
            org.telegram.tgnet.TLRPC$TL_peerUser r6 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1cee }
            r6.<init>()     // Catch:{ all -> 0x1cee }
            r5.peer_id = r6     // Catch:{ all -> 0x1cee }
            r7 = r23
            r6.user_id = r7     // Catch:{ all -> 0x1cee }
        L_0x1c6e:
            int r6 = r5.flags     // Catch:{ all -> 0x1cee }
            r6 = r6 | 256(0x100, float:3.59E-43)
            r5.flags = r6     // Catch:{ all -> 0x1cee }
            r6 = 0
            int r8 = (r43 > r6 ? 1 : (r43 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_peerChat r6 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x1cee }
            r6.<init>()     // Catch:{ all -> 0x1cee }
            r5.from_id = r6     // Catch:{ all -> 0x1cee }
            r6.chat_id = r12     // Catch:{ all -> 0x1cee }
            goto L_0x1cac
        L_0x1CLASSNAME:
            r6 = 0
            int r8 = (r32 > r6 ? 1 : (r32 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_peerChannel r6 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x1cee }
            r6.<init>()     // Catch:{ all -> 0x1cee }
            r5.from_id = r6     // Catch:{ all -> 0x1cee }
            r7 = r32
            r6.channel_id = r7     // Catch:{ all -> 0x1cee }
            goto L_0x1cac
        L_0x1CLASSNAME:
            r6 = 0
            int r8 = (r35 > r6 ? 1 : (r35 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x1ca8
            org.telegram.tgnet.TLRPC$TL_peerUser r6 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1cee }
            r6.<init>()     // Catch:{ all -> 0x1cee }
            r5.from_id = r6     // Catch:{ all -> 0x1cee }
            r7 = r35
            r6.user_id = r7     // Catch:{ all -> 0x1cee }
            goto L_0x1cac
        L_0x1ca8:
            org.telegram.tgnet.TLRPC$Peer r6 = r5.peer_id     // Catch:{ all -> 0x1cee }
            r5.from_id = r6     // Catch:{ all -> 0x1cee }
        L_0x1cac:
            if (r29 != 0) goto L_0x1cb3
            if (r38 == 0) goto L_0x1cb1
            goto L_0x1cb3
        L_0x1cb1:
            r6 = 0
            goto L_0x1cb4
        L_0x1cb3:
            r6 = 1
        L_0x1cb4:
            r5.mentioned = r6     // Catch:{ all -> 0x1cee }
            r6 = r34
            r5.silent = r6     // Catch:{ all -> 0x1cee }
            r5.from_scheduled = r4     // Catch:{ all -> 0x1cee }
            org.telegram.messenger.MessageObject r4 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x1cee }
            r18 = r4
            r19 = r28
            r20 = r5
            r21 = r2
            r23 = r42
            r24 = r3
            r25 = r41
            r26 = r37
            r27 = r40
            r18.<init>(r19, r20, r21, r22, r23, r24, r25, r26, r27)     // Catch:{ all -> 0x1cee }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x1cee }
            r2.<init>()     // Catch:{ all -> 0x1cee }
            r2.add(r4)     // Catch:{ all -> 0x1cee }
            org.telegram.messenger.NotificationsController r3 = org.telegram.messenger.NotificationsController.getInstance(r28)     // Catch:{ all -> 0x1cee }
            java.util.concurrent.CountDownLatch r4 = r1.countDownLatch     // Catch:{ all -> 0x1cee }
            r5 = 1
            r3.processNewMessages(r2, r5, r5, r4)     // Catch:{ all -> 0x1cee }
            r8 = 0
            goto L_0x1cf1
        L_0x1ce7:
            r1 = r49
            goto L_0x1cf0
        L_0x1cea:
            r0 = move-exception
            r1 = r49
            goto L_0x1d0a
        L_0x1cee:
            r0 = move-exception
            goto L_0x1d0a
        L_0x1cf0:
            r8 = 1
        L_0x1cf1:
            if (r8 == 0) goto L_0x1cf8
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x1cee }
            r2.countDown()     // Catch:{ all -> 0x1cee }
        L_0x1cf8:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r28)     // Catch:{ all -> 0x1cee }
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r28)     // Catch:{ all -> 0x1cee }
            r2.resumeNetworkMaybe()     // Catch:{ all -> 0x1cee }
            goto L_0x1e28
        L_0x1d04:
            r0 = move-exception
            r14 = r29
            goto L_0x1d0a
        L_0x1d08:
            r0 = move-exception
            r14 = r9
        L_0x1d0a:
            r2 = r0
            r7 = r16
            r4 = r28
            goto L_0x1dd9
        L_0x1d11:
            r0 = move-exception
            r28 = r4
            r14 = r9
            goto L_0x1dd0
        L_0x1d17:
            r0 = move-exception
            r28 = r4
            goto L_0x1dd5
        L_0x1d1c:
            r28 = r4
            r16 = r7
            r14 = r9
            org.telegram.messenger.DispatchQueue r2 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1d33 }
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1 r3 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1     // Catch:{ all -> 0x1d33 }
            r4 = r28
            r3.<init>(r4)     // Catch:{ all -> 0x1dcf }
            r2.postRunnable(r3)     // Catch:{ all -> 0x1dcf }
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x1dcf }
            r2.countDown()     // Catch:{ all -> 0x1dcf }
            return
        L_0x1d33:
            r0 = move-exception
            r4 = r28
            goto L_0x1dd0
        L_0x1d38:
            r16 = r7
            r14 = r9
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0 r2 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0     // Catch:{ all -> 0x1dcf }
            r2.<init>(r4)     // Catch:{ all -> 0x1dcf }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ all -> 0x1dcf }
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x1dcf }
            r2.countDown()     // Catch:{ all -> 0x1dcf }
            return
        L_0x1d49:
            r16 = r7
            r14 = r9
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r2 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x1dcf }
            r2.<init>()     // Catch:{ all -> 0x1dcf }
            r3 = 0
            r2.popup = r3     // Catch:{ all -> 0x1dcf }
            r3 = 2
            r2.flags = r3     // Catch:{ all -> 0x1dcf }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r6 = r51 / r6
            int r3 = (int) r6     // Catch:{ all -> 0x1dcf }
            r2.inbox_date = r3     // Catch:{ all -> 0x1dcf }
            java.lang.String r3 = "message"
            java.lang.String r3 = r5.getString(r3)     // Catch:{ all -> 0x1dcf }
            r2.message = r3     // Catch:{ all -> 0x1dcf }
            java.lang.String r3 = "announcement"
            r2.type = r3     // Catch:{ all -> 0x1dcf }
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ all -> 0x1dcf }
            r3.<init>()     // Catch:{ all -> 0x1dcf }
            r2.media = r3     // Catch:{ all -> 0x1dcf }
            org.telegram.tgnet.TLRPC$TL_updates r3 = new org.telegram.tgnet.TLRPC$TL_updates     // Catch:{ all -> 0x1dcf }
            r3.<init>()     // Catch:{ all -> 0x1dcf }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r5 = r3.updates     // Catch:{ all -> 0x1dcf }
            r5.add(r2)     // Catch:{ all -> 0x1dcf }
            org.telegram.messenger.DispatchQueue r2 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1dcf }
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3 r5 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3     // Catch:{ all -> 0x1dcf }
            r5.<init>(r4, r3)     // Catch:{ all -> 0x1dcf }
            r2.postRunnable(r5)     // Catch:{ all -> 0x1dcf }
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1dcf }
            r2.resumeNetworkMaybe()     // Catch:{ all -> 0x1dcf }
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x1dcf }
            r2.countDown()     // Catch:{ all -> 0x1dcf }
            return
        L_0x1d92:
            r16 = r7
            r14 = r9
            java.lang.String r2 = "dc"
            int r2 = r11.getInt(r2)     // Catch:{ all -> 0x1dcf }
            java.lang.String r3 = "addr"
            java.lang.String r3 = r11.getString(r3)     // Catch:{ all -> 0x1dcf }
            java.lang.String r5 = ":"
            java.lang.String[] r3 = r3.split(r5)     // Catch:{ all -> 0x1dcf }
            int r5 = r3.length     // Catch:{ all -> 0x1dcf }
            r6 = 2
            if (r5 == r6) goto L_0x1db1
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x1dcf }
            r2.countDown()     // Catch:{ all -> 0x1dcf }
            return
        L_0x1db1:
            r5 = 0
            r5 = r3[r5]     // Catch:{ all -> 0x1dcf }
            r6 = 1
            r3 = r3[r6]     // Catch:{ all -> 0x1dcf }
            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ all -> 0x1dcf }
            org.telegram.tgnet.ConnectionsManager r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1dcf }
            r6.applyDatacenterAddress(r2, r5, r3)     // Catch:{ all -> 0x1dcf }
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1dcf }
            r2.resumeNetworkMaybe()     // Catch:{ all -> 0x1dcf }
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x1dcf }
            r2.countDown()     // Catch:{ all -> 0x1dcf }
            return
        L_0x1dcf:
            r0 = move-exception
        L_0x1dd0:
            r2 = r0
        L_0x1dd1:
            r7 = r16
            goto L_0x1dd9
        L_0x1dd4:
            r0 = move-exception
        L_0x1dd5:
            r16 = r7
            r14 = r9
            r2 = r0
        L_0x1dd9:
            r3 = -1
            goto L_0x1df0
        L_0x1ddb:
            r0 = move-exception
            r16 = r7
            r14 = r9
            r2 = r0
        L_0x1de0:
            r3 = -1
            r4 = -1
            goto L_0x1df0
        L_0x1de3:
            r0 = move-exception
            r16 = r7
        L_0x1de6:
            r2 = r0
            r3 = -1
            r4 = -1
            goto L_0x1def
        L_0x1dea:
            r0 = move-exception
            r2 = r0
            r3 = -1
            r4 = -1
            r7 = 0
        L_0x1def:
            r14 = 0
        L_0x1df0:
            if (r4 == r3) goto L_0x1e02
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r4)
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
            r3.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r3 = r1.countDownLatch
            r3.countDown()
            goto L_0x1e05
        L_0x1e02:
            r49.onDecryptError()
        L_0x1e05:
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x1e25
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "error in loc_key = "
            r3.append(r4)
            r3.append(r14)
            java.lang.String r4 = " json "
            r3.append(r4)
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r3)
        L_0x1e25:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x1e28:
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
