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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v26, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v4, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v9, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v8, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v9, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v10, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v11, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v25, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v60, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v46, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v3, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v4, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v39, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v52, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v43, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v46, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v106, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v78, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v79, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v80, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v133, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v138, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v84, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v87, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v149, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v154, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v166, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v78, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v169, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v79, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v82, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v175, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v166, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v167, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v179, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v158, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v182, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v188, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v191, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v194, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v197, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v101, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v200, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v203, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v206, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v200, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v7, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v8, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v9, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v210, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v207, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v209, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v213, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v217, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v221, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v134, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v223, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v138, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v227, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v229, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v141, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v10, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v11, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v12, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v232, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v234, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v214, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v240, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v217, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v166, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v169, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v148, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v149, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v257, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v181, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v153, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v154, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v155, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v263, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v158, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v269, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v273, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v275, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v166, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v167, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v196, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v279, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v169, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v170, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v281, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v172, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v201, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v173, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v285, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v174, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v175, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v287, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v177, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v206, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v178, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v270, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v291, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v293, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v181, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v277, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v295, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v213, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v284, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v299, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v216, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v303, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v190, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v305, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v307, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v196, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v198, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v311, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v200, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v313, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v201, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v315, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v317, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v206, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v207, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v278, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v280, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v282, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v324, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v286, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v288, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v244, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v329, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v292, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v294, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v334, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v298, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v252, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v300, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v339, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v304, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v256, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v257, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v306, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v258, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v344, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v260, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v310, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v262, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v312, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v263, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v264, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v349, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v316, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v266, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v318, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v268, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v269, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v323, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v324, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v325, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v328, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v6, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v79, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:1000:0x1d5d, code lost:
        onDecryptError();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1003:0x1d64, code lost:
        org.telegram.messenger.FileLog.e("error in loc_key = " + r4 + " json " + r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1014:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:127:0x01f0, code lost:
        if (r2 == 0) goto L_0x1ce7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x01f2, code lost:
        if (r2 == 1) goto L_0x1c9e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x01f4, code lost:
        if (r2 == 2) goto L_0x1c8d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x01f6, code lost:
        if (r2 == 3) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x0200, code lost:
        if (r11.has("channel_id") == false) goto L_0x020b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:?, code lost:
        r2 = r11.getInt("channel_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:0x0208, code lost:
        r3 = (long) (-r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x020b, code lost:
        r3 = 0;
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0214, code lost:
        if (r11.has("from_id") == false) goto L_0x0228;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:?, code lost:
        r3 = r11.getInt("from_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x021c, code lost:
        r14 = r7;
        r6 = r3;
        r3 = (long) r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:0x0224, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:0x0225, code lost:
        r14 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x0226, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x0228, code lost:
        r14 = r7;
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x0230, code lost:
        if (r11.has("chat_id") == false) goto L_0x0242;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:159:?, code lost:
        r3 = r11.getInt("chat_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x0238, code lost:
        r12 = r3;
        r3 = (long) (-r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:161:0x0240, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x0242, code lost:
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x0249, code lost:
        if (r11.has("encryption_id") == false) goto L_0x0255;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x0251, code lost:
        r3 = ((long) r11.getInt("encryption_id")) << 32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x025b, code lost:
        if (r11.has("schedule") == false) goto L_0x0267;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x0263, code lost:
        if (r11.getInt("schedule") != 1) goto L_0x0267;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:179:0x0265, code lost:
        r13 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x0267, code lost:
        r13 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:0x026a, code lost:
        if (r3 != 0) goto L_0x0279;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x0272, code lost:
        if ("ENCRYPTED_MESSAGE".equals(r9) == false) goto L_0x0279;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:0x0274, code lost:
        r3 = -4294967296L;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x027b, code lost:
        if (r3 == 0) goto L_0x1c3d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x0285, code lost:
        if ("READ_HISTORY".equals(r9) == false) goto L_0x02f1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:?, code lost:
        r5 = r11.getInt("max_id");
        r7 = new java.util.ArrayList();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x0294, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x02b0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x0296, code lost:
        org.telegram.messenger.FileLog.d("GCM received read notification max_id = " + r5 + " for dialogId = " + r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x02b0, code lost:
        if (r2 == 0) goto L_0x02bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x02b2, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox();
        r3.channel_id = r2;
        r3.max_id = r5;
        r7.add(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x02bf, code lost:
        r2 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x02c4, code lost:
        if (r6 == 0) goto L_0x02d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x02c6, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r2.peer = r3;
        r3.user_id = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x02d0, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r2.peer = r3;
        r3.chat_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x02d9, code lost:
        r2.max_id = r5;
        r7.add(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x02de, code lost:
        org.telegram.messenger.MessagesController.getInstance(r15).processUpdateArray(r7, (java.util.ArrayList<org.telegram.tgnet.TLRPC$User>) null, (java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat>) null, false, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x02f9, code lost:
        if ("MESSAGE_DELETED".equals(r9) == false) goto L_0x035e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:?, code lost:
        r5 = r11.getString("messages").split(",");
        r6 = new android.util.SparseArray();
        r7 = new java.util.ArrayList();
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x0311, code lost:
        if (r8 >= r5.length) goto L_0x031f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x0313, code lost:
        r7.add(org.telegram.messenger.Utilities.parseInt(r5[r8]));
        r8 = r8 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x031f, code lost:
        r6.put(r2, r7);
        org.telegram.messenger.NotificationsController.getInstance(r15).removeDeletedMessagesFromNotifications(r6);
        org.telegram.messenger.MessagesController.getInstance(r15).deleteMessagesByPush(r3, r7, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x0332, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1c3d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:218:0x0334, code lost:
        org.telegram.messenger.FileLog.d("GCM received " + r9 + " for dialogId = " + r3 + " mids = " + android.text.TextUtils.join(",", r7));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x0362, code lost:
        if (android.text.TextUtils.isEmpty(r9) != false) goto L_0x1c3d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x036a, code lost:
        if (r11.has("msg_id") == false) goto L_0x0375;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:?, code lost:
        r7 = r11.getInt("msg_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x0372, code lost:
        r29 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x0375, code lost:
        r29 = r14;
        r7 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x037a, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x037e, code lost:
        if (r11.has("random_id") == false) goto L_0x039c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x038e, code lost:
        r22 = r13;
        r13 = org.telegram.messenger.Utilities.parseLong(r11.getString("random_id")).longValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x0395, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x0396, code lost:
        r3 = r1;
        r4 = r9;
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x039c, code lost:
        r22 = r13;
        r13 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x03a0, code lost:
        if (r7 == 0) goto L_0x03e6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:?, code lost:
        r23 = r6;
        r1 = org.telegram.messenger.MessagesController.getInstance(r15).dialogs_read_inbox_max.get(java.lang.Long.valueOf(r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x03b4, code lost:
        if (r1 != null) goto L_0x03d3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x03b6, code lost:
        r1 = java.lang.Integer.valueOf(org.telegram.messenger.MessagesStorage.getInstance(r15).getDialogReadMax(false, r3));
        r24 = r12;
        org.telegram.messenger.MessagesController.getInstance(r15).dialogs_read_inbox_max.put(java.lang.Long.valueOf(r3), r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x03d3, code lost:
        r24 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x03d9, code lost:
        if (r7 <= r1.intValue()) goto L_0x03fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x03dc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x03dd, code lost:
        r2 = -1;
        r3 = r43;
        r1 = r0;
        r4 = r9;
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x03e6, code lost:
        r23 = r6;
        r24 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x03ec, code lost:
        if (r13 == 0) goto L_0x03fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x03f6, code lost:
        if (org.telegram.messenger.MessagesStorage.getInstance(r15).checkMessageByRandomId(r13) != false) goto L_0x03fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x03f8, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x03fa, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x03fb, code lost:
        if (r1 == false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x0400, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:?, code lost:
        r1 = r11.optInt("chat_from_id", 0);
        r12 = r11.optInt("chat_from_broadcast_id", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x040a, code lost:
        r30 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x040e, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:?, code lost:
        r15 = r11.optInt("chat_from_group_id", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x0412, code lost:
        if (r1 != 0) goto L_0x041b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x0414, code lost:
        if (r15 == 0) goto L_0x0417;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x0417, code lost:
        r25 = r1;
        r6 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x041b, code lost:
        r25 = r1;
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x0424, code lost:
        if (r11.has("mention") == false) goto L_0x043d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x042c, code lost:
        if (r11.getInt("mention") == 0) goto L_0x043d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:275:0x042e, code lost:
        r26 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0431, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:277:0x0432, code lost:
        r2 = -1;
        r3 = r43;
        r1 = r0;
        r4 = r9;
        r14 = r29;
        r15 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x043d, code lost:
        r26 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0445, code lost:
        if (r11.has("silent") == false) goto L_0x0452;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x044d, code lost:
        if (r11.getInt("silent") == 0) goto L_0x0452;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x044f, code lost:
        r27 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x0452, code lost:
        r27 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x045a, code lost:
        if (r5.has("loc_args") == false) goto L_0x047a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:?, code lost:
        r1 = r5.getJSONArray("loc_args");
        r5 = r1.length();
        r28 = r12;
        r12 = new java.lang.String[r5];
        r31 = r15;
        r15 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x046d, code lost:
        if (r15 >= r5) goto L_0x0478;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x046f, code lost:
        r12[r15] = r1.getString(r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x0475, code lost:
        r15 = r15 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x0478, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x047a, code lost:
        r28 = r12;
        r31 = r15;
        r1 = 0;
        r12 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:?, code lost:
        r5 = r12[r1];
        r1 = r11.has("edit_date");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x048e, code lost:
        if (r9.startsWith("CHAT_") == false) goto L_0x04c1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x0494, code lost:
        if (org.telegram.messenger.UserObject.isReplyUser(r3) == false) goto L_0x04ae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x0496, code lost:
        r5 = r5 + " @ " + r12[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x04ae, code lost:
        if (r2 == 0) goto L_0x04b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x04b0, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x04b2, code lost:
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x04b6, code lost:
        r15 = false;
        r33 = false;
        r41 = r11;
        r11 = r5;
        r5 = r12[1];
        r32 = r41;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x04c7, code lost:
        if (r9.startsWith("PINNED_") == false) goto L_0x04d5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x04c9, code lost:
        if (r2 == 0) goto L_0x04cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x04cb, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x04cd, code lost:
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x04ce, code lost:
        r32 = r11;
        r11 = null;
        r15 = false;
        r33 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x04db, code lost:
        if (r9.startsWith("CHANNEL_") == false) goto L_0x04e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x04dd, code lost:
        r11 = null;
        r15 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x04e0, code lost:
        r11 = null;
        r15 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x04e2, code lost:
        r32 = false;
        r33 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x04e8, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x0511;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x04ea, code lost:
        r34 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:?, code lost:
        r5 = new java.lang.StringBuilder();
        r35 = r1;
        r5.append("GCM received message notification ");
        r5.append(r9);
        r5.append(" for dialogId = ");
        r5.append(r3);
        r5.append(" mid = ");
        r5.append(r7);
        org.telegram.messenger.FileLog.d(r5.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x0511, code lost:
        r35 = r1;
        r34 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x0519, code lost:
        switch(r9.hashCode()) {
            case -2100047043: goto L_0x0a42;
            case -2091498420: goto L_0x0a37;
            case -2053872415: goto L_0x0a2c;
            case -2039746363: goto L_0x0a21;
            case -2023218804: goto L_0x0a16;
            case -1979538588: goto L_0x0a0b;
            case -1979536003: goto L_0x0a00;
            case -1979535888: goto L_0x09f5;
            case -1969004705: goto L_0x09ea;
            case -1946699248: goto L_0x09de;
            case -1646640058: goto L_0x09d2;
            case -1528047021: goto L_0x09c6;
            case -1493579426: goto L_0x09ba;
            case -1482481933: goto L_0x09ae;
            case -1480102982: goto L_0x09a3;
            case -1478041834: goto L_0x0997;
            case -1474543101: goto L_0x098c;
            case -1465695932: goto L_0x0980;
            case -1374906292: goto L_0x0974;
            case -1372940586: goto L_0x0968;
            case -1264245338: goto L_0x095c;
            case -1236154001: goto L_0x0950;
            case -1236086700: goto L_0x0944;
            case -1236077786: goto L_0x0938;
            case -1235796237: goto L_0x092c;
            case -1235760759: goto L_0x0920;
            case -1235686303: goto L_0x0915;
            case -1198046100: goto L_0x090a;
            case -1124254527: goto L_0x08fe;
            case -1085137927: goto L_0x08f2;
            case -1084856378: goto L_0x08e6;
            case -1084820900: goto L_0x08da;
            case -1084746444: goto L_0x08ce;
            case -819729482: goto L_0x08c2;
            case -772141857: goto L_0x08b6;
            case -638310039: goto L_0x08aa;
            case -590403924: goto L_0x089e;
            case -589196239: goto L_0x0892;
            case -589193654: goto L_0x0886;
            case -589193539: goto L_0x087a;
            case -440169325: goto L_0x086e;
            case -412748110: goto L_0x0862;
            case -228518075: goto L_0x0856;
            case -213586509: goto L_0x084a;
            case -115582002: goto L_0x083e;
            case -112621464: goto L_0x0832;
            case -108522133: goto L_0x0826;
            case -107572034: goto L_0x081b;
            case -40534265: goto L_0x080f;
            case 65254746: goto L_0x0803;
            case 141040782: goto L_0x07f7;
            case 202550149: goto L_0x07eb;
            case 309993049: goto L_0x07df;
            case 309995634: goto L_0x07d3;
            case 309995749: goto L_0x07c7;
            case 320532812: goto L_0x07bb;
            case 328933854: goto L_0x07af;
            case 331340546: goto L_0x07a3;
            case 342406591: goto L_0x0797;
            case 344816990: goto L_0x078b;
            case 346878138: goto L_0x077f;
            case 350376871: goto L_0x0773;
            case 608430149: goto L_0x0767;
            case 615714517: goto L_0x075c;
            case 715508879: goto L_0x0750;
            case 728985323: goto L_0x0744;
            case 731046471: goto L_0x0738;
            case 734545204: goto L_0x072c;
            case 802032552: goto L_0x0720;
            case 991498806: goto L_0x0714;
            case 1007364121: goto L_0x0708;
            case 1019850010: goto L_0x06fc;
            case 1019917311: goto L_0x06f0;
            case 1019926225: goto L_0x06e4;
            case 1020207774: goto L_0x06d8;
            case 1020243252: goto L_0x06cc;
            case 1020317708: goto L_0x06c0;
            case 1060282259: goto L_0x06b4;
            case 1060349560: goto L_0x06a8;
            case 1060358474: goto L_0x069c;
            case 1060640023: goto L_0x0690;
            case 1060675501: goto L_0x0684;
            case 1060749957: goto L_0x0679;
            case 1073049781: goto L_0x066d;
            case 1078101399: goto L_0x0661;
            case 1110103437: goto L_0x0655;
            case 1160762272: goto L_0x0649;
            case 1172918249: goto L_0x063d;
            case 1234591620: goto L_0x0631;
            case 1281128640: goto L_0x0625;
            case 1281131225: goto L_0x0619;
            case 1281131340: goto L_0x060d;
            case 1310789062: goto L_0x0602;
            case 1333118583: goto L_0x05f6;
            case 1361447897: goto L_0x05ea;
            case 1498266155: goto L_0x05de;
            case 1533804208: goto L_0x05d2;
            case 1540131626: goto L_0x05c6;
            case 1547988151: goto L_0x05ba;
            case 1561464595: goto L_0x05ae;
            case 1563525743: goto L_0x05a2;
            case 1567024476: goto L_0x0596;
            case 1810705077: goto L_0x058a;
            case 1815177512: goto L_0x057e;
            case 1954774321: goto L_0x0572;
            case 1963241394: goto L_0x0566;
            case 2014789757: goto L_0x055a;
            case 2022049433: goto L_0x054e;
            case 2034984710: goto L_0x0542;
            case 2048733346: goto L_0x0536;
            case 2099392181: goto L_0x052a;
            case 2140162142: goto L_0x051e;
            default: goto L_0x051c;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x0524, code lost:
        if (r9.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:339:0x0526, code lost:
        r1 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x0530, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x0532, code lost:
        r1 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x053c, code lost:
        if (r9.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x053e, code lost:
        r1 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x0548, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PLAYLIST") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x054a, code lost:
        r1 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x0554, code lost:
        if (r9.equals("PINNED_CONTACT") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:351:0x0556, code lost:
        r1 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x0560, code lost:
        if (r9.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x0562, code lost:
        r1 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x056c, code lost:
        if (r9.equals("LOCKED_MESSAGE") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x056e, code lost:
        r1 = 'j';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x0578, code lost:
        if (r9.equals("CHAT_MESSAGE_PLAYLIST") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x057a, code lost:
        r1 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x0584, code lost:
        if (r9.equals("CHANNEL_MESSAGES") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x0586, code lost:
        r1 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x0590, code lost:
        if (r9.equals("MESSAGE_INVOICE") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x0592, code lost:
        r1 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x059c, code lost:
        if (r9.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x059e, code lost:
        r1 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x05a8, code lost:
        if (r9.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x05aa, code lost:
        r1 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x05b4, code lost:
        if (r9.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:375:0x05b6, code lost:
        r1 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x05c0, code lost:
        if (r9.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x05c2, code lost:
        r1 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x05cc, code lost:
        if (r9.equals("MESSAGE_PLAYLIST") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:381:0x05ce, code lost:
        r1 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x05d8, code lost:
        if (r9.equals("MESSAGE_VIDEOS") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x05da, code lost:
        r1 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x05e4, code lost:
        if (r9.equals("PHONE_CALL_MISSED") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x05e6, code lost:
        r1 = 'o';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x05f0, code lost:
        if (r9.equals("MESSAGE_PHOTOS") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x05f2, code lost:
        r1 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x05fc, code lost:
        if (r9.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x05fe, code lost:
        r1 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x0608, code lost:
        if (r9.equals("MESSAGE_NOTEXT") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x060a, code lost:
        r1 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x0613, code lost:
        if (r9.equals("MESSAGE_GIF") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x0615, code lost:
        r1 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x061f, code lost:
        if (r9.equals("MESSAGE_GEO") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x0621, code lost:
        r1 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x062b, code lost:
        if (r9.equals("MESSAGE_DOC") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x062d, code lost:
        r1 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x0637, code lost:
        if (r9.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x0639, code lost:
        r1 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x0643, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x0645, code lost:
        r1 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x064f, code lost:
        if (r9.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x0651, code lost:
        r1 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x065b, code lost:
        if (r9.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x065d, code lost:
        r1 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x0667, code lost:
        if (r9.equals("CHAT_TITLE_EDITED") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x0669, code lost:
        r1 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x0673, code lost:
        if (r9.equals("PINNED_NOTEXT") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x0675, code lost:
        r1 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x067f, code lost:
        if (r9.equals("MESSAGE_TEXT") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:426:0x0681, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x068a, code lost:
        if (r9.equals("MESSAGE_QUIZ") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x068c, code lost:
        r1 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x0696, code lost:
        if (r9.equals("MESSAGE_POLL") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:432:0x0698, code lost:
        r1 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x06a2, code lost:
        if (r9.equals("MESSAGE_GAME") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x06a4, code lost:
        r1 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x06ae, code lost:
        if (r9.equals("MESSAGE_FWDS") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x06b0, code lost:
        r1 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x06ba, code lost:
        if (r9.equals("MESSAGE_DOCS") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x06bc, code lost:
        r1 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x06c6, code lost:
        if (r9.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:444:0x06c8, code lost:
        r1 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x06d2, code lost:
        if (r9.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x06d4, code lost:
        r1 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x06de, code lost:
        if (r9.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x06e0, code lost:
        r1 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x06ea, code lost:
        if (r9.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x06ec, code lost:
        r1 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x06f6, code lost:
        if (r9.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:456:0x06f8, code lost:
        r1 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:458:0x0702, code lost:
        if (r9.equals("CHAT_MESSAGE_DOCS") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x0704, code lost:
        r1 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x070e, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x0710, code lost:
        r1 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x071a, code lost:
        if (r9.equals("PINNED_GEOLIVE") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x071c, code lost:
        r1 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x0726, code lost:
        if (r9.equals("MESSAGE_CONTACT") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x0728, code lost:
        r1 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x0732, code lost:
        if (r9.equals("PINNED_VIDEO") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x0734, code lost:
        r1 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x073e, code lost:
        if (r9.equals("PINNED_ROUND") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x0740, code lost:
        r1 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x074a, code lost:
        if (r9.equals("PINNED_PHOTO") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x074c, code lost:
        r1 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0756, code lost:
        if (r9.equals("PINNED_AUDIO") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:480:0x0758, code lost:
        r1 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x0762, code lost:
        if (r9.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x0764, code lost:
        r1 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x076d, code lost:
        if (r9.equals("CHAT_VOICECHAT_INVITE_YOU") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:486:0x076f, code lost:
        r1 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x0779, code lost:
        if (r9.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x077b, code lost:
        r1 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0785, code lost:
        if (r9.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x0787, code lost:
        r1 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x0791, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x0793, code lost:
        r1 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x079d, code lost:
        if (r9.equals("CHAT_VOICECHAT_END") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x079f, code lost:
        r1 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x07a9, code lost:
        if (r9.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x07ab, code lost:
        r1 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x07b5, code lost:
        if (r9.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x07b7, code lost:
        r1 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x07c1, code lost:
        if (r9.equals("MESSAGES") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x07c3, code lost:
        r1 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x07cd, code lost:
        if (r9.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x07cf, code lost:
        r1 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x07d9, code lost:
        if (r9.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x07db, code lost:
        r1 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x07e5, code lost:
        if (r9.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x07e7, code lost:
        r1 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x07f1, code lost:
        if (r9.equals("CHAT_VOICECHAT_INVITE") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:519:0x07f3, code lost:
        r1 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x07fd, code lost:
        if (r9.equals("CHAT_LEFT") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x07ff, code lost:
        r1 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0809, code lost:
        if (r9.equals("CHAT_ADD_YOU") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x080b, code lost:
        r1 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0815, code lost:
        if (r9.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x0817, code lost:
        r1 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x0821, code lost:
        if (r9.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0823, code lost:
        r1 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x082c, code lost:
        if (r9.equals("AUTH_REGION") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x082e, code lost:
        r1 = 'i';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x0838, code lost:
        if (r9.equals("CONTACT_JOINED") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x083a, code lost:
        r1 = 'g';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x0844, code lost:
        if (r9.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:540:0x0846, code lost:
        r1 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x0850, code lost:
        if (r9.equals("ENCRYPTION_REQUEST") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x0852, code lost:
        r1 = 'k';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x085c, code lost:
        if (r9.equals("MESSAGE_GEOLIVE") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x085e, code lost:
        r1 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x0868, code lost:
        if (r9.equals("CHAT_DELETE_YOU") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x086a, code lost:
        r1 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x0874, code lost:
        if (r9.equals("AUTH_UNKNOWN") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:552:0x0876, code lost:
        r1 = 'h';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x0880, code lost:
        if (r9.equals("PINNED_GIF") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x0882, code lost:
        r1 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x088c, code lost:
        if (r9.equals("PINNED_GEO") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x088e, code lost:
        r1 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x0898, code lost:
        if (r9.equals("PINNED_DOC") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x089a, code lost:
        r1 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x08a4, code lost:
        if (r9.equals("PINNED_GAME_SCORE") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x08a6, code lost:
        r1 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x08b0, code lost:
        if (r9.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x08b2, code lost:
        r1 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x08bc, code lost:
        if (r9.equals("PHONE_CALL_REQUEST") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x08be, code lost:
        r1 = 'm';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x08c8, code lost:
        if (r9.equals("PINNED_STICKER") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x08ca, code lost:
        r1 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x08d4, code lost:
        if (r9.equals("PINNED_TEXT") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:576:0x08d6, code lost:
        r1 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x08e0, code lost:
        if (r9.equals("PINNED_QUIZ") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x08e2, code lost:
        r1 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x08ec, code lost:
        if (r9.equals("PINNED_POLL") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:582:0x08ee, code lost:
        r1 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x08f8, code lost:
        if (r9.equals("PINNED_GAME") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x08fa, code lost:
        r1 = 'b';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x0904, code lost:
        if (r9.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:588:0x0906, code lost:
        r1 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x0910, code lost:
        if (r9.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x0912, code lost:
        r1 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x091b, code lost:
        if (r9.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x091d, code lost:
        r1 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x0926, code lost:
        if (r9.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0928, code lost:
        r1 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0932, code lost:
        if (r9.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:600:0x0934, code lost:
        r1 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x093e, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0940, code lost:
        r1 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x094a, code lost:
        if (r9.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x094c, code lost:
        r1 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x0956, code lost:
        if (r9.equals("CHANNEL_MESSAGE_DOCS") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x0958, code lost:
        r1 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x0962, code lost:
        if (r9.equals("PINNED_INVOICE") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:612:0x0964, code lost:
        r1 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x096e, code lost:
        if (r9.equals("CHAT_RETURNED") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x0970, code lost:
        r1 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x097a, code lost:
        if (r9.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:618:0x097c, code lost:
        r1 = 'f';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0986, code lost:
        if (r9.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x0988, code lost:
        r1 = 'l';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x0992, code lost:
        if (r9.equals("MESSAGE_VIDEO") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x0994, code lost:
        r1 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x099d, code lost:
        if (r9.equals("MESSAGE_ROUND") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:627:0x099f, code lost:
        r1 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x09a9, code lost:
        if (r9.equals("MESSAGE_PHOTO") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x09ab, code lost:
        r1 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x09b4, code lost:
        if (r9.equals("MESSAGE_MUTED") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x09b6, code lost:
        r1 = 'n';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x09c0, code lost:
        if (r9.equals("MESSAGE_AUDIO") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x09c2, code lost:
        r1 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x09cc, code lost:
        if (r9.equals("CHAT_MESSAGES") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x09ce, code lost:
        r1 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x09d8, code lost:
        if (r9.equals("CHAT_VOICECHAT_START") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x09da, code lost:
        r1 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x09e4, code lost:
        if (r9.equals("CHAT_JOINED") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x09e6, code lost:
        r1 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x09f0, code lost:
        if (r9.equals("CHAT_ADD_MEMBER") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x09f2, code lost:
        r1 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x09fb, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x09fd, code lost:
        r1 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0a06, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0a08, code lost:
        r1 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0a11, code lost:
        if (r9.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0a13, code lost:
        r1 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0a1c, code lost:
        if (r9.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0a1e, code lost:
        r1 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0a27, code lost:
        if (r9.equals("MESSAGE_STICKER") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x0a29, code lost:
        r1 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x0a32, code lost:
        if (r9.equals("CHAT_CREATED") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0a34, code lost:
        r1 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0a3d, code lost:
        if (r9.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0a3f, code lost:
        r1 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:671:0x0a48, code lost:
        if (r9.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0a4a, code lost:
        r1 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:673:0x0a4d, code lost:
        r1 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0a4e, code lost:
        r18 = r7;
        r36 = r15;
        r37 = r11;
        r38 = r2;
        r39 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0a68, code lost:
        switch(r1) {
            case 0: goto L_0x1b29;
            case 1: goto L_0x1b29;
            case 2: goto L_0x1b09;
            case 3: goto L_0x1aec;
            case 4: goto L_0x1acf;
            case 5: goto L_0x1ab2;
            case 6: goto L_0x1a94;
            case 7: goto L_0x1a7e;
            case 8: goto L_0x1a60;
            case 9: goto L_0x1a42;
            case 10: goto L_0x19e7;
            case 11: goto L_0x19c9;
            case 12: goto L_0x19a6;
            case 13: goto L_0x1983;
            case 14: goto L_0x1960;
            case 15: goto L_0x1942;
            case 16: goto L_0x1924;
            case 17: goto L_0x1906;
            case 18: goto L_0x18e3;
            case 19: goto L_0x18c4;
            case 20: goto L_0x18c4;
            case 21: goto L_0x18a1;
            case 22: goto L_0x187b;
            case 23: goto L_0x1857;
            case 24: goto L_0x1834;
            case 25: goto L_0x1811;
            case 26: goto L_0x17ec;
            case 27: goto L_0x17d6;
            case 28: goto L_0x17b8;
            case 29: goto L_0x179a;
            case 30: goto L_0x177c;
            case 31: goto L_0x175e;
            case 32: goto L_0x1740;
            case 33: goto L_0x16e5;
            case 34: goto L_0x16c7;
            case 35: goto L_0x16a4;
            case 36: goto L_0x1681;
            case 37: goto L_0x165e;
            case 38: goto L_0x1640;
            case 39: goto L_0x1622;
            case 40: goto L_0x1604;
            case 41: goto L_0x15e6;
            case 42: goto L_0x15bc;
            case 43: goto L_0x1598;
            case 44: goto L_0x1574;
            case 45: goto L_0x1550;
            case 46: goto L_0x152a;
            case 47: goto L_0x1515;
            case 48: goto L_0x14f4;
            case 49: goto L_0x14d1;
            case 50: goto L_0x14ae;
            case 51: goto L_0x148b;
            case 52: goto L_0x1468;
            case 53: goto L_0x1445;
            case 54: goto L_0x13cc;
            case 55: goto L_0x13a9;
            case 56: goto L_0x1381;
            case 57: goto L_0x1359;
            case 58: goto L_0x1331;
            case 59: goto L_0x130e;
            case 60: goto L_0x12eb;
            case 61: goto L_0x12c8;
            case 62: goto L_0x12a0;
            case 63: goto L_0x127c;
            case 64: goto L_0x1254;
            case 65: goto L_0x123a;
            case 66: goto L_0x123a;
            case 67: goto L_0x1220;
            case 68: goto L_0x1206;
            case 69: goto L_0x11e7;
            case 70: goto L_0x11cd;
            case 71: goto L_0x11ae;
            case 72: goto L_0x1194;
            case 73: goto L_0x117a;
            case 74: goto L_0x1160;
            case 75: goto L_0x1146;
            case 76: goto L_0x112c;
            case 77: goto L_0x1112;
            case 78: goto L_0x10f8;
            case 79: goto L_0x10cd;
            case 80: goto L_0x10a4;
            case 81: goto L_0x107b;
            case 82: goto L_0x1052;
            case 83: goto L_0x1027;
            case 84: goto L_0x100d;
            case 85: goto L_0x0fb8;
            case 86: goto L_0x0f6d;
            case 87: goto L_0x0var_;
            case 88: goto L_0x0ed7;
            case 89: goto L_0x0e8c;
            case 90: goto L_0x0e41;
            case 91: goto L_0x0d8a;
            case 92: goto L_0x0d3f;
            case 93: goto L_0x0cea;
            case 94: goto L_0x0CLASSNAME;
            case 95: goto L_0x0CLASSNAME;
            case 96: goto L_0x0bfb;
            case 97: goto L_0x0bb5;
            case 98: goto L_0x0b6c;
            case 99: goto L_0x0b23;
            case 100: goto L_0x0ada;
            case 101: goto L_0x0a91;
            case 102: goto L_0x0a75;
            case 103: goto L_0x0a71;
            case 104: goto L_0x0a71;
            case 105: goto L_0x0a71;
            case 106: goto L_0x0a71;
            case 107: goto L_0x0a71;
            case 108: goto L_0x0a71;
            case 109: goto L_0x0a71;
            case 110: goto L_0x0a71;
            case 111: goto L_0x0a71;
            default: goto L_0x0a6b;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0a6b, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:679:0x0a71, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:?, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
        r34 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
        r5 = true;
        r16 = null;
        r2 = r1;
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0a93, code lost:
        if (r3 <= 0) goto L_0x0aad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:685:0x0a95, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0aad, code lost:
        if (r6 == false) goto L_0x0ac7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x0aaf, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x0ac7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0adc, code lost:
        if (r3 <= 0) goto L_0x0af6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x0ade, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0af6, code lost:
        if (r6 == false) goto L_0x0b10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0af8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0b10, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0b25, code lost:
        if (r3 <= 0) goto L_0x0b3f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0b27, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0b3f, code lost:
        if (r6 == false) goto L_0x0b59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0b41, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0b59, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0b6e, code lost:
        if (r3 <= 0) goto L_0x0b88;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0b70, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0b88, code lost:
        if (r6 == false) goto L_0x0ba2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0b8a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0ba2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0bb7, code lost:
        if (r3 <= 0) goto L_0x0bd0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0bb9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0bd0, code lost:
        if (r6 == false) goto L_0x0be9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0bd2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0be9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0bfd, code lost:
        if (r3 <= 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0bff, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0CLASSNAME, code lost:
        if (r6 == false) goto L_0x0c2f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0c2f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0CLASSNAME, code lost:
        r2 = r1;
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:0x0CLASSNAME, code lost:
        if (r3 <= 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0CLASSNAME, code lost:
        if (r6 == false) goto L_0x0c7e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0c7e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0CLASSNAME, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:727:0x0CLASSNAME, code lost:
        if (r3 <= 0) goto L_0x0cb3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0c9b, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0cb3, code lost:
        if (r6 == false) goto L_0x0cd2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0cb5, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x0cd2, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0cea, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0cee, code lost:
        if (r3 <= 0) goto L_0x0d08;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0cf0, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x0d08, code lost:
        if (r6 == false) goto L_0x0d27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0d0a, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0d27, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0d3f, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x0d43, code lost:
        if (r3 <= 0) goto L_0x0d5d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0d45, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x0d5d, code lost:
        if (r6 == false) goto L_0x0d77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0d5f, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x0d77, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x0d8a, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x0d8e, code lost:
        if (r3 <= 0) goto L_0x0dc7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x0d92, code lost:
        if (r12.length <= 1) goto L_0x0db4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0d9a, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x0db4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x0d9c, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x0db4, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x0dc7, code lost:
        if (r6 == false) goto L_0x0e0a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x0dcb, code lost:
        if (r12.length <= 2) goto L_0x0df2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x0dd3, code lost:
        if (android.text.TextUtils.isEmpty(r12[2]) != false) goto L_0x0df2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x0dd5, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x0df2, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x0e0c, code lost:
        if (r12.length <= 1) goto L_0x0e2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x0e14, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x0e2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x0e16, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x0e2e, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:0x0e41, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x0e45, code lost:
        if (r3 <= 0) goto L_0x0e5f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x0e47, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x0e5f, code lost:
        if (r6 == false) goto L_0x0e79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x0e61, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x0e79, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x0e8c, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x0e90, code lost:
        if (r3 <= 0) goto L_0x0eaa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x0e92, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x0eaa, code lost:
        if (r6 == false) goto L_0x0ec4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x0eac, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x0ec4, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x0ed7, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x0edb, code lost:
        if (r3 <= 0) goto L_0x0ef5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x0edd, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x0ef5, code lost:
        if (r6 == false) goto L_0x0f0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x0ef7, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x0f0f, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x0var_, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x0var_, code lost:
        if (r3 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x0var_, code lost:
        if (r6 == false) goto L_0x0f5a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x0f5a, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x0f6d, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x0var_, code lost:
        if (r3 <= 0) goto L_0x0f8b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x0f8b, code lost:
        if (r6 == false) goto L_0x0fa5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x0f8d, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x0fa5, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x0fb8, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x0fbc, code lost:
        if (r3 <= 0) goto L_0x0fd6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x0fbe, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x0fd6, code lost:
        if (r6 == false) goto L_0x0ff5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x0fd8, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x0ff5, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x100d, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x1027, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x1052, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x107b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x10a4, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x10cd, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x10f8, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x1112, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x112c, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x1146, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x1160, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x117a, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x1194, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupEndedCall", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x11ae, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x11cd, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x11e7, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x1206, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x1220, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x123a, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x1254, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x127c, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r12[0], r12[1], r12[2], r12[3]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x12a0, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x12c8, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x12eb, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x130e, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x1331, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x1359, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("PollQuiz", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x1381, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x13a9, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x13cc, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x13d0, code lost:
        if (r12.length <= 2) goto L_0x1412;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x13d8, code lost:
        if (android.text.TextUtils.isEmpty(r12[2]) != false) goto L_0x1412;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x13da, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r12[0], r12[1], r12[2]);
        r5 = r12[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x1412, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r12[0], r12[1]);
        r5 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x1445, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x1468, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x148b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x14ae, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x14d1, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x14f4, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r12[0], r12[1], r12[2]);
        r5 = r12[2];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x1515, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x152a, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x1550, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x1574, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x1598, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x15bc, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x15e6, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x1604, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x1622, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:851:0x1640, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x165e, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x1681, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x16a4, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x16c7, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:856:0x16e5, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x16e9, code lost:
        if (r12.length <= 1) goto L_0x1726;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x16f1, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x1726;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x16f3, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r12[0], r12[1]);
        r5 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x1726, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:862:0x1740, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:863:0x175e, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:864:0x177c, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:865:0x179a, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:866:0x17b8, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x17d6, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x17e9, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x17ec, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x1811, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:871:0x1834, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x1857, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x187b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x18a1, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x18c4, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x18e3, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x1906, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x1924, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x1942, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x1960, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x1983, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x19a6, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x19c9, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x19e7, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:0x19eb, code lost:
        if (r12.length <= 1) goto L_0x1a28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x19f3, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x1a28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x19f5, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r12[0], r12[1]);
        r5 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x1a28, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x1a42, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x1a60, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x1a7e, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x1a94, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x1ab2, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x1acf, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x1aec, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:897:0x1b09, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x1b25, code lost:
        r16 = r5;
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:899:0x1b29, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r12[0], r12[1]);
        r5 = r12[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:0x1b44, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1b5a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x1b46, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x1b5a, code lost:
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:0x1b5b, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:0x1b5c, code lost:
        r16 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x1b5e, code lost:
        if (r2 == null) goto L_0x1c2c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:?, code lost:
        r6 = new org.telegram.tgnet.TLRPC$TL_message();
        r6.id = r1;
        r6.random_id = r39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x1b6b, code lost:
        if (r16 == null) goto L_0x1b70;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:909:0x1b6d, code lost:
        r1 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:910:0x1b70, code lost:
        r1 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:0x1b71, code lost:
        r6.message = r1;
        r6.date = (int) (r45 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:0x1b7a, code lost:
        if (r33 == false) goto L_0x1b83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:?, code lost:
        r6.action = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:0x1b83, code lost:
        if (r32 == false) goto L_0x1b8c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x1b85, code lost:
        r6.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:918:?, code lost:
        r6.dialog_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:0x1b8e, code lost:
        if (r38 == 0) goto L_0x1b9e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:?, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r6.peer_id = r1;
        r1.channel_id = r38;
        r3 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x1b9e, code lost:
        if (r24 == 0) goto L_0x1bac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x1ba0, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r6.peer_id = r1;
        r3 = r24;
        r1.chat_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x1bac, code lost:
        r3 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:?, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r6.peer_id = r1;
        r1.user_id = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x1bb9, code lost:
        r6.flags |= 256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:928:0x1bbf, code lost:
        if (r31 == 0) goto L_0x1bcb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:?, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r6.from_id = r1;
        r1.chat_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x1bcb, code lost:
        if (r28 == 0) goto L_0x1bd9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:932:0x1bcd, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r6.from_id = r1;
        r1.channel_id = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x1bd9, code lost:
        if (r25 == 0) goto L_0x1be7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x1bdb, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r6.from_id = r1;
        r1.user_id = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:?, code lost:
        r6.from_id = r6.peer_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:0x1beb, code lost:
        if (r26 != false) goto L_0x1bf2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:0x1bed, code lost:
        if (r33 == false) goto L_0x1bf0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:0x1bf0, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:0x1bf2, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:0x1bf3, code lost:
        r6.mentioned = r1;
        r6.silent = r27;
        r6.from_scheduled = r22;
        r19 = new org.telegram.messenger.MessageObject(r30, r6, r2, r34, r37, r5, r36, r32, r35);
        r2 = new java.util.ArrayList();
        r2.add(r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:943:0x1CLASSNAME, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:944:0x1CLASSNAME, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:?, code lost:
        org.telegram.messenger.NotificationsController.getInstance(r30).processNewMessages(r2, true, true, r3.countDownLatch);
        r8 = false;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:946:0x1c2c, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x1c2f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x1CLASSNAME, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:0x1CLASSNAME, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x1CLASSNAME, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x1CLASSNAME, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x1c3a, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x1c3b, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:0x1c3d, code lost:
        r3 = r1;
        r29 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x1CLASSNAME, code lost:
        r30 = r15;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:0x1CLASSNAME, code lost:
        r8 = true;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:957:0x1CLASSNAME, code lost:
        if (r8 == false) goto L_0x1c4a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:0x1CLASSNAME, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x1c4a, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r30);
        org.telegram.tgnet.ConnectionsManager.getInstance(r30).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x1CLASSNAME, code lost:
        r0 = th;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:962:0x1c5f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:0x1CLASSNAME, code lost:
        r3 = r1;
        r29 = r14;
        r30 = r15;
        r1 = r0;
        r4 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:964:0x1CLASSNAME, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:965:0x1c6a, code lost:
        r3 = r1;
        r29 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:966:0x1c6d, code lost:
        r30 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:967:0x1CLASSNAME, code lost:
        r3 = r1;
        r29 = r7;
        r30 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:0x1c7a, code lost:
        r15 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:972:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1(r15));
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:0x1CLASSNAME, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:974:0x1CLASSNAME, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:975:0x1CLASSNAME, code lost:
        r15 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:976:0x1c8d, code lost:
        r29 = r7;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0(r15));
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:977:0x1c9d, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:978:0x1c9e, code lost:
        r3 = r1;
        r29 = r7;
        r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification();
        r1.popup = false;
        r1.flags = 2;
        r1.inbox_date = (int) (r45 / 1000);
        r1.message = r5.getString("message");
        r1.type = "announcement";
        r1.media = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty();
        r2 = new org.telegram.tgnet.TLRPC$TL_updates();
        r2.updates.add(r1);
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3(r15, r2));
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:979:0x1ce6, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:980:0x1ce7, code lost:
        r3 = r1;
        r29 = r7;
        r1 = r11.getInt("dc");
        r2 = r11.getString("addr").split(":");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:981:0x1cfe, code lost:
        if (r2.length == 2) goto L_0x1d06;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:982:0x1d00, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:983:0x1d05, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:984:0x1d06, code lost:
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).applyDatacenterAddress(r1, r2[0], java.lang.Integer.parseInt(r2[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:985:0x1d23, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:986:0x1d24, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:999:0x1d4d, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15);
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1000:0x1d5d  */
    /* JADX WARNING: Removed duplicated region for block: B:1003:0x1d64  */
    /* JADX WARNING: Removed duplicated region for block: B:999:0x1d4d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$onMessageReceived$3(java.util.Map r44, long r45) {
        /*
            r43 = this;
            r1 = r43
            r2 = r44
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x000d
            java.lang.String r3 = "GCM START PROCESSING"
            org.telegram.messenger.FileLog.d(r3)
        L_0x000d:
            java.lang.String r5 = "p"
            java.lang.Object r5 = r2.get(r5)     // Catch:{ all -> 0x1d44 }
            boolean r6 = r5 instanceof java.lang.String     // Catch:{ all -> 0x1d44 }
            if (r6 != 0) goto L_0x002d
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0024 }
            if (r2 == 0) goto L_0x0020
            java.lang.String r2 = "GCM DECRYPT ERROR 1"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x0024 }
        L_0x0020:
            r43.onDecryptError()     // Catch:{ all -> 0x0024 }
            return
        L_0x0024:
            r0 = move-exception
            r3 = r1
            r2 = -1
            r4 = 0
            r14 = 0
        L_0x0029:
            r15 = -1
        L_0x002a:
            r1 = r0
            goto L_0x1d4b
        L_0x002d:
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ all -> 0x1d44 }
            r6 = 8
            byte[] r5 = android.util.Base64.decode(r5, r6)     // Catch:{ all -> 0x1d44 }
            org.telegram.tgnet.NativeByteBuffer r7 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x1d44 }
            int r8 = r5.length     // Catch:{ all -> 0x1d44 }
            r7.<init>((int) r8)     // Catch:{ all -> 0x1d44 }
            r7.writeBytes((byte[]) r5)     // Catch:{ all -> 0x1d44 }
            r8 = 0
            r7.position(r8)     // Catch:{ all -> 0x1d44 }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1d44 }
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
            byte[] r9 = new byte[r6]     // Catch:{ all -> 0x1d44 }
            r10 = 1
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1d44 }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1d44 }
            boolean r11 = java.util.Arrays.equals(r11, r9)     // Catch:{ all -> 0x1d44 }
            r12 = 3
            r13 = 2
            if (r11 != 0) goto L_0x0092
            r43.onDecryptError()     // Catch:{ all -> 0x0024 }
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
            byte[] r9 = new byte[r9]     // Catch:{ all -> 0x1d44 }
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1d44 }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1d44 }
            org.telegram.messenger.MessageKeyData r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13)     // Catch:{ all -> 0x1d44 }
            java.nio.ByteBuffer r14 = r7.buffer     // Catch:{ all -> 0x1d44 }
            byte[] r15 = r11.aesKey     // Catch:{ all -> 0x1d44 }
            byte[] r11 = r11.aesIv     // Catch:{ all -> 0x1d44 }
            r17 = 0
            r18 = 0
            r19 = 24
            int r5 = r5.length     // Catch:{ all -> 0x1d44 }
            int r20 = r5 + -24
            r16 = r11
            org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x1d44 }
            byte[] r21 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1d44 }
            r22 = 96
            r23 = 32
            java.nio.ByteBuffer r5 = r7.buffer     // Catch:{ all -> 0x1d44 }
            r25 = 24
            int r26 = r5.limit()     // Catch:{ all -> 0x1d44 }
            r24 = r5
            byte[] r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26)     // Catch:{ all -> 0x1d44 }
            boolean r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6)     // Catch:{ all -> 0x1d44 }
            if (r5 != 0) goto L_0x00e8
            r43.onDecryptError()     // Catch:{ all -> 0x0024 }
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
            int r5 = r7.readInt32(r10)     // Catch:{ all -> 0x1d44 }
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x1d44 }
            r7.readBytes(r5, r10)     // Catch:{ all -> 0x1d44 }
            java.lang.String r7 = new java.lang.String     // Catch:{ all -> 0x1d44 }
            r7.<init>(r5)     // Catch:{ all -> 0x1d44 }
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ all -> 0x1d3a }
            r5.<init>(r7)     // Catch:{ all -> 0x1d3a }
            java.lang.String r9 = "loc_key"
            boolean r9 = r5.has(r9)     // Catch:{ all -> 0x1d3a }
            if (r9 == 0) goto L_0x0111
            java.lang.String r9 = "loc_key"
            java.lang.String r9 = r5.getString(r9)     // Catch:{ all -> 0x010a }
            goto L_0x0113
        L_0x010a:
            r0 = move-exception
            r3 = r1
            r14 = r7
            r2 = -1
            r4 = 0
            goto L_0x0029
        L_0x0111:
            java.lang.String r9 = ""
        L_0x0113:
            java.lang.String r11 = "custom"
            java.lang.Object r11 = r5.get(r11)     // Catch:{ all -> 0x1d30 }
            boolean r11 = r11 instanceof org.json.JSONObject     // Catch:{ all -> 0x1d30 }
            if (r11 == 0) goto L_0x012b
            java.lang.String r11 = "custom"
            org.json.JSONObject r11 = r5.getJSONObject(r11)     // Catch:{ all -> 0x0124 }
            goto L_0x0130
        L_0x0124:
            r0 = move-exception
            r3 = r1
            r14 = r7
            r4 = r9
            r2 = -1
            goto L_0x0029
        L_0x012b:
            org.json.JSONObject r11 = new org.json.JSONObject     // Catch:{ all -> 0x1d30 }
            r11.<init>()     // Catch:{ all -> 0x1d30 }
        L_0x0130:
            java.lang.String r14 = "user_id"
            boolean r14 = r5.has(r14)     // Catch:{ all -> 0x1d30 }
            if (r14 == 0) goto L_0x013f
            java.lang.String r14 = "user_id"
            java.lang.Object r14 = r5.get(r14)     // Catch:{ all -> 0x0124 }
            goto L_0x0140
        L_0x013f:
            r14 = 0
        L_0x0140:
            if (r14 != 0) goto L_0x014d
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x0124 }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x0124 }
            int r14 = r14.getClientUserId()     // Catch:{ all -> 0x0124 }
            goto L_0x0171
        L_0x014d:
            boolean r15 = r14 instanceof java.lang.Integer     // Catch:{ all -> 0x1d30 }
            if (r15 == 0) goto L_0x0158
            java.lang.Integer r14 = (java.lang.Integer) r14     // Catch:{ all -> 0x0124 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0124 }
            goto L_0x0171
        L_0x0158:
            boolean r15 = r14 instanceof java.lang.String     // Catch:{ all -> 0x1d30 }
            if (r15 == 0) goto L_0x0167
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ all -> 0x0124 }
            java.lang.Integer r14 = org.telegram.messenger.Utilities.parseInt(r14)     // Catch:{ all -> 0x0124 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0124 }
            goto L_0x0171
        L_0x0167:
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1d30 }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x1d30 }
            int r14 = r14.getClientUserId()     // Catch:{ all -> 0x1d30 }
        L_0x0171:
            int r15 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1d30 }
            r4 = 0
        L_0x0174:
            if (r4 >= r12) goto L_0x0188
            org.telegram.messenger.UserConfig r17 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x0124 }
            int r6 = r17.getClientUserId()     // Catch:{ all -> 0x0124 }
            if (r6 != r14) goto L_0x0183
            r15 = r4
            r4 = 1
            goto L_0x0189
        L_0x0183:
            int r4 = r4 + 1
            r6 = 8
            goto L_0x0174
        L_0x0188:
            r4 = 0
        L_0x0189:
            if (r4 != 0) goto L_0x019a
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0124 }
            if (r2 == 0) goto L_0x0194
            java.lang.String r2 = "GCM ACCOUNT NOT FOUND"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x0124 }
        L_0x0194:
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x0124 }
            r2.countDown()     // Catch:{ all -> 0x0124 }
            return
        L_0x019a:
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r15)     // Catch:{ all -> 0x1d26 }
            boolean r4 = r4.isClientActivated()     // Catch:{ all -> 0x1d26 }
            if (r4 != 0) goto L_0x01ba
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x01b3 }
            if (r2 == 0) goto L_0x01ad
            java.lang.String r2 = "GCM ACCOUNT NOT ACTIVATED"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x01b3 }
        L_0x01ad:
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x01b3 }
            r2.countDown()     // Catch:{ all -> 0x01b3 }
            return
        L_0x01b3:
            r0 = move-exception
            r3 = r1
            r14 = r7
        L_0x01b6:
            r4 = r9
        L_0x01b7:
            r2 = -1
            goto L_0x002a
        L_0x01ba:
            java.lang.String r4 = "google.sent_time"
            r2.get(r4)     // Catch:{ all -> 0x1d26 }
            int r2 = r9.hashCode()     // Catch:{ all -> 0x1d26 }
            switch(r2) {
                case -1963663249: goto L_0x01e5;
                case -920689527: goto L_0x01db;
                case 633004703: goto L_0x01d1;
                case 1365673842: goto L_0x01c7;
                default: goto L_0x01c6;
            }
        L_0x01c6:
            goto L_0x01ef
        L_0x01c7:
            java.lang.String r2 = "GEO_LIVE_PENDING"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01b3 }
            if (r2 == 0) goto L_0x01ef
            r2 = 3
            goto L_0x01f0
        L_0x01d1:
            java.lang.String r2 = "MESSAGE_ANNOUNCEMENT"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01b3 }
            if (r2 == 0) goto L_0x01ef
            r2 = 1
            goto L_0x01f0
        L_0x01db:
            java.lang.String r2 = "DC_UPDATE"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01b3 }
            if (r2 == 0) goto L_0x01ef
            r2 = 0
            goto L_0x01f0
        L_0x01e5:
            java.lang.String r2 = "SESSION_REVOKE"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01b3 }
            if (r2 == 0) goto L_0x01ef
            r2 = 2
            goto L_0x01f0
        L_0x01ef:
            r2 = -1
        L_0x01f0:
            if (r2 == 0) goto L_0x1ce7
            if (r2 == r10) goto L_0x1c9e
            if (r2 == r13) goto L_0x1c8d
            if (r2 == r12) goto L_0x1CLASSNAME
            java.lang.String r2 = "channel_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1CLASSNAME }
            r19 = 0
            if (r2 == 0) goto L_0x020b
            java.lang.String r2 = "channel_id"
            int r2 = r11.getInt(r2)     // Catch:{ all -> 0x01b3 }
            int r4 = -r2
            long r3 = (long) r4
            goto L_0x020e
        L_0x020b:
            r3 = r19
            r2 = 0
        L_0x020e:
            java.lang.String r14 = "from_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1CLASSNAME }
            if (r14 == 0) goto L_0x0228
            java.lang.String r3 = "from_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x0224 }
            r14 = r7
            long r6 = (long) r3
            r41 = r6
            r6 = r3
            r3 = r41
            goto L_0x022a
        L_0x0224:
            r0 = move-exception
            r14 = r7
        L_0x0226:
            r3 = r1
            goto L_0x01b6
        L_0x0228:
            r14 = r7
            r6 = 0
        L_0x022a:
            java.lang.String r7 = "chat_id"
            boolean r7 = r11.has(r7)     // Catch:{ all -> 0x1c5f }
            if (r7 == 0) goto L_0x0242
            java.lang.String r3 = "chat_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x0240 }
            int r4 = -r3
            long r12 = (long) r4
            r41 = r12
            r12 = r3
            r3 = r41
            goto L_0x0243
        L_0x0240:
            r0 = move-exception
            goto L_0x0226
        L_0x0242:
            r12 = 0
        L_0x0243:
            java.lang.String r13 = "encryption_id"
            boolean r13 = r11.has(r13)     // Catch:{ all -> 0x1c5f }
            if (r13 == 0) goto L_0x0255
            java.lang.String r3 = "encryption_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x0240 }
            long r3 = (long) r3
            r13 = 32
            long r3 = r3 << r13
        L_0x0255:
            java.lang.String r13 = "schedule"
            boolean r13 = r11.has(r13)     // Catch:{ all -> 0x1c5f }
            if (r13 == 0) goto L_0x0267
            java.lang.String r13 = "schedule"
            int r13 = r11.getInt(r13)     // Catch:{ all -> 0x0240 }
            if (r13 != r10) goto L_0x0267
            r13 = 1
            goto L_0x0268
        L_0x0267:
            r13 = 0
        L_0x0268:
            int r21 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r21 != 0) goto L_0x0279
            java.lang.String r7 = "ENCRYPTED_MESSAGE"
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x0240 }
            if (r7 == 0) goto L_0x0279
            r3 = -4294967296(0xfffffffvar_, double:NaN)
        L_0x0279:
            int r7 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r7 == 0) goto L_0x1c3d
            java.lang.String r7 = "READ_HISTORY"
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x1c5f }
            java.lang.String r10 = " for dialogId = "
            if (r7 == 0) goto L_0x02f1
            java.lang.String r5 = "max_id"
            int r5 = r11.getInt(r5)     // Catch:{ all -> 0x0240 }
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ all -> 0x0240 }
            r7.<init>()     // Catch:{ all -> 0x0240 }
            boolean r8 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0240 }
            if (r8 == 0) goto L_0x02b0
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0240 }
            r8.<init>()     // Catch:{ all -> 0x0240 }
            java.lang.String r11 = "GCM received read notification max_id = "
            r8.append(r11)     // Catch:{ all -> 0x0240 }
            r8.append(r5)     // Catch:{ all -> 0x0240 }
            r8.append(r10)     // Catch:{ all -> 0x0240 }
            r8.append(r3)     // Catch:{ all -> 0x0240 }
            java.lang.String r3 = r8.toString()     // Catch:{ all -> 0x0240 }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ all -> 0x0240 }
        L_0x02b0:
            if (r2 == 0) goto L_0x02bf
            org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox r3 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox     // Catch:{ all -> 0x0240 }
            r3.<init>()     // Catch:{ all -> 0x0240 }
            r3.channel_id = r2     // Catch:{ all -> 0x0240 }
            r3.max_id = r5     // Catch:{ all -> 0x0240 }
            r7.add(r3)     // Catch:{ all -> 0x0240 }
            goto L_0x02de
        L_0x02bf:
            org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox r2 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox     // Catch:{ all -> 0x0240 }
            r2.<init>()     // Catch:{ all -> 0x0240 }
            if (r6 == 0) goto L_0x02d0
            org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x0240 }
            r3.<init>()     // Catch:{ all -> 0x0240 }
            r2.peer = r3     // Catch:{ all -> 0x0240 }
            r3.user_id = r6     // Catch:{ all -> 0x0240 }
            goto L_0x02d9
        L_0x02d0:
            org.telegram.tgnet.TLRPC$TL_peerChat r3 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x0240 }
            r3.<init>()     // Catch:{ all -> 0x0240 }
            r2.peer = r3     // Catch:{ all -> 0x0240 }
            r3.chat_id = r12     // Catch:{ all -> 0x0240 }
        L_0x02d9:
            r2.max_id = r5     // Catch:{ all -> 0x0240 }
            r7.add(r2)     // Catch:{ all -> 0x0240 }
        L_0x02de:
            org.telegram.messenger.MessagesController r16 = org.telegram.messenger.MessagesController.getInstance(r15)     // Catch:{ all -> 0x0240 }
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r17 = r7
            r16.processUpdateArray(r17, r18, r19, r20, r21)     // Catch:{ all -> 0x0240 }
            goto L_0x1c3d
        L_0x02f1:
            java.lang.String r7 = "MESSAGE_DELETED"
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x1c5f }
            java.lang.String r8 = "messages"
            if (r7 == 0) goto L_0x035e
            java.lang.String r5 = r11.getString(r8)     // Catch:{ all -> 0x0240 }
            java.lang.String r6 = ","
            java.lang.String[] r5 = r5.split(r6)     // Catch:{ all -> 0x0240 }
            android.util.SparseArray r6 = new android.util.SparseArray     // Catch:{ all -> 0x0240 }
            r6.<init>()     // Catch:{ all -> 0x0240 }
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ all -> 0x0240 }
            r7.<init>()     // Catch:{ all -> 0x0240 }
            r8 = 0
        L_0x0310:
            int r11 = r5.length     // Catch:{ all -> 0x0240 }
            if (r8 >= r11) goto L_0x031f
            r11 = r5[r8]     // Catch:{ all -> 0x0240 }
            java.lang.Integer r11 = org.telegram.messenger.Utilities.parseInt(r11)     // Catch:{ all -> 0x0240 }
            r7.add(r11)     // Catch:{ all -> 0x0240 }
            int r8 = r8 + 1
            goto L_0x0310
        L_0x031f:
            r6.put(r2, r7)     // Catch:{ all -> 0x0240 }
            org.telegram.messenger.NotificationsController r5 = org.telegram.messenger.NotificationsController.getInstance(r15)     // Catch:{ all -> 0x0240 }
            r5.removeDeletedMessagesFromNotifications(r6)     // Catch:{ all -> 0x0240 }
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r15)     // Catch:{ all -> 0x0240 }
            r5.deleteMessagesByPush(r3, r7, r2)     // Catch:{ all -> 0x0240 }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0240 }
            if (r2 == 0) goto L_0x1c3d
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0240 }
            r2.<init>()     // Catch:{ all -> 0x0240 }
            java.lang.String r5 = "GCM received "
            r2.append(r5)     // Catch:{ all -> 0x0240 }
            r2.append(r9)     // Catch:{ all -> 0x0240 }
            r2.append(r10)     // Catch:{ all -> 0x0240 }
            r2.append(r3)     // Catch:{ all -> 0x0240 }
            java.lang.String r3 = " mids = "
            r2.append(r3)     // Catch:{ all -> 0x0240 }
            java.lang.String r3 = ","
            java.lang.String r3 = android.text.TextUtils.join(r3, r7)     // Catch:{ all -> 0x0240 }
            r2.append(r3)     // Catch:{ all -> 0x0240 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0240 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x0240 }
            goto L_0x1c3d
        L_0x035e:
            boolean r7 = android.text.TextUtils.isEmpty(r9)     // Catch:{ all -> 0x1c5f }
            if (r7 != 0) goto L_0x1c3d
            java.lang.String r7 = "msg_id"
            boolean r7 = r11.has(r7)     // Catch:{ all -> 0x1c5f }
            if (r7 == 0) goto L_0x0375
            java.lang.String r7 = "msg_id"
            int r7 = r11.getInt(r7)     // Catch:{ all -> 0x0240 }
            r29 = r14
            goto L_0x0378
        L_0x0375:
            r29 = r14
            r7 = 0
        L_0x0378:
            java.lang.String r14 = "random_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1c3a }
            if (r14 == 0) goto L_0x039c
            java.lang.String r14 = "random_id"
            java.lang.String r14 = r11.getString(r14)     // Catch:{ all -> 0x0395 }
            java.lang.Long r14 = org.telegram.messenger.Utilities.parseLong(r14)     // Catch:{ all -> 0x0395 }
            long r22 = r14.longValue()     // Catch:{ all -> 0x0395 }
            r41 = r22
            r22 = r13
            r13 = r41
            goto L_0x03a0
        L_0x0395:
            r0 = move-exception
            r3 = r1
            r4 = r9
            r14 = r29
            goto L_0x01b7
        L_0x039c:
            r22 = r13
            r13 = r19
        L_0x03a0:
            if (r7 == 0) goto L_0x03e6
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r15)     // Catch:{ all -> 0x03dc }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r1.dialogs_read_inbox_max     // Catch:{ all -> 0x03dc }
            r23 = r6
            java.lang.Long r6 = java.lang.Long.valueOf(r3)     // Catch:{ all -> 0x03dc }
            java.lang.Object r1 = r1.get(r6)     // Catch:{ all -> 0x03dc }
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch:{ all -> 0x03dc }
            if (r1 != 0) goto L_0x03d3
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r15)     // Catch:{ all -> 0x03dc }
            r6 = 0
            int r1 = r1.getDialogReadMax(r6, r3)     // Catch:{ all -> 0x03dc }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x03dc }
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r15)     // Catch:{ all -> 0x03dc }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r6 = r6.dialogs_read_inbox_max     // Catch:{ all -> 0x03dc }
            r24 = r12
            java.lang.Long r12 = java.lang.Long.valueOf(r3)     // Catch:{ all -> 0x03dc }
            r6.put(r12, r1)     // Catch:{ all -> 0x03dc }
            goto L_0x03d5
        L_0x03d3:
            r24 = r12
        L_0x03d5:
            int r1 = r1.intValue()     // Catch:{ all -> 0x03dc }
            if (r7 <= r1) goto L_0x03fa
            goto L_0x03f8
        L_0x03dc:
            r0 = move-exception
            r2 = -1
            r3 = r43
            r1 = r0
            r4 = r9
            r14 = r29
            goto L_0x1d4b
        L_0x03e6:
            r23 = r6
            r24 = r12
            int r1 = (r13 > r19 ? 1 : (r13 == r19 ? 0 : -1))
            if (r1 == 0) goto L_0x03fa
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r15)     // Catch:{ all -> 0x03dc }
            boolean r1 = r1.checkMessageByRandomId(r13)     // Catch:{ all -> 0x03dc }
            if (r1 != 0) goto L_0x03fa
        L_0x03f8:
            r1 = 1
            goto L_0x03fb
        L_0x03fa:
            r1 = 0
        L_0x03fb:
            if (r1 == 0) goto L_0x1CLASSNAME
            java.lang.String r1 = "chat_from_id"
            r6 = 0
            int r1 = r11.optInt(r1, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r12 = "chat_from_broadcast_id"
            int r12 = r11.optInt(r12, r6)     // Catch:{ all -> 0x1CLASSNAME }
            r30 = r15
            java.lang.String r15 = "chat_from_group_id"
            int r15 = r11.optInt(r15, r6)     // Catch:{ all -> 0x1c2f }
            if (r1 != 0) goto L_0x041b
            if (r15 == 0) goto L_0x0417
            goto L_0x041b
        L_0x0417:
            r25 = r1
            r6 = 0
            goto L_0x041e
        L_0x041b:
            r25 = r1
            r6 = 1
        L_0x041e:
            java.lang.String r1 = "mention"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x1c2f }
            if (r1 == 0) goto L_0x043d
            java.lang.String r1 = "mention"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x043d
            r26 = 1
            goto L_0x043f
        L_0x0431:
            r0 = move-exception
            r2 = -1
            r3 = r43
            r1 = r0
            r4 = r9
            r14 = r29
            r15 = r30
            goto L_0x1d4b
        L_0x043d:
            r26 = 0
        L_0x043f:
            java.lang.String r1 = "silent"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x1c2f }
            if (r1 == 0) goto L_0x0452
            java.lang.String r1 = "silent"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0452
            r27 = 1
            goto L_0x0454
        L_0x0452:
            r27 = 0
        L_0x0454:
            java.lang.String r1 = "loc_args"
            boolean r1 = r5.has(r1)     // Catch:{ all -> 0x1c2f }
            if (r1 == 0) goto L_0x047a
            java.lang.String r1 = "loc_args"
            org.json.JSONArray r1 = r5.getJSONArray(r1)     // Catch:{ all -> 0x0431 }
            int r5 = r1.length()     // Catch:{ all -> 0x0431 }
            r28 = r12
            java.lang.String[] r12 = new java.lang.String[r5]     // Catch:{ all -> 0x0431 }
            r31 = r15
            r15 = 0
        L_0x046d:
            if (r15 >= r5) goto L_0x0478
            java.lang.String r32 = r1.getString(r15)     // Catch:{ all -> 0x0431 }
            r12[r15] = r32     // Catch:{ all -> 0x0431 }
            int r15 = r15 + 1
            goto L_0x046d
        L_0x0478:
            r1 = 0
            goto L_0x0480
        L_0x047a:
            r28 = r12
            r31 = r15
            r1 = 0
            r12 = 0
        L_0x0480:
            r5 = r12[r1]     // Catch:{ all -> 0x1c2f }
            java.lang.String r1 = "edit_date"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x1c2f }
            java.lang.String r11 = "CHAT_"
            boolean r11 = r9.startsWith(r11)     // Catch:{ all -> 0x1c2f }
            if (r11 == 0) goto L_0x04c1
            boolean r11 = org.telegram.messenger.UserObject.isReplyUser((long) r3)     // Catch:{ all -> 0x0431 }
            if (r11 == 0) goto L_0x04ae
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x0431 }
            r11.<init>()     // Catch:{ all -> 0x0431 }
            r11.append(r5)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = " @ "
            r11.append(r5)     // Catch:{ all -> 0x0431 }
            r5 = 1
            r15 = r12[r5]     // Catch:{ all -> 0x0431 }
            r11.append(r15)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = r11.toString()     // Catch:{ all -> 0x0431 }
            goto L_0x04e0
        L_0x04ae:
            if (r2 == 0) goto L_0x04b2
            r11 = 1
            goto L_0x04b3
        L_0x04b2:
            r11 = 0
        L_0x04b3:
            r15 = 1
            r32 = r12[r15]     // Catch:{ all -> 0x0431 }
            r15 = 0
            r33 = 0
            r41 = r11
            r11 = r5
            r5 = r32
            r32 = r41
            goto L_0x04e6
        L_0x04c1:
            java.lang.String r11 = "PINNED_"
            boolean r11 = r9.startsWith(r11)     // Catch:{ all -> 0x1c2f }
            if (r11 == 0) goto L_0x04d5
            if (r2 == 0) goto L_0x04cd
            r11 = 1
            goto L_0x04ce
        L_0x04cd:
            r11 = 0
        L_0x04ce:
            r32 = r11
            r11 = 0
            r15 = 0
            r33 = 1
            goto L_0x04e6
        L_0x04d5:
            java.lang.String r11 = "CHANNEL_"
            boolean r11 = r9.startsWith(r11)     // Catch:{ all -> 0x1c2f }
            if (r11 == 0) goto L_0x04e0
            r11 = 0
            r15 = 1
            goto L_0x04e2
        L_0x04e0:
            r11 = 0
            r15 = 0
        L_0x04e2:
            r32 = 0
            r33 = 0
        L_0x04e6:
            boolean r34 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1c2f }
            if (r34 == 0) goto L_0x0511
            r34 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0431 }
            r5.<init>()     // Catch:{ all -> 0x0431 }
            r35 = r1
            java.lang.String r1 = "GCM received message notification "
            r5.append(r1)     // Catch:{ all -> 0x0431 }
            r5.append(r9)     // Catch:{ all -> 0x0431 }
            r5.append(r10)     // Catch:{ all -> 0x0431 }
            r5.append(r3)     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = " mid = "
            r5.append(r1)     // Catch:{ all -> 0x0431 }
            r5.append(r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = r5.toString()     // Catch:{ all -> 0x0431 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x0431 }
            goto L_0x0515
        L_0x0511:
            r35 = r1
            r34 = r5
        L_0x0515:
            int r1 = r9.hashCode()     // Catch:{ all -> 0x1c2f }
            switch(r1) {
                case -2100047043: goto L_0x0a42;
                case -2091498420: goto L_0x0a37;
                case -2053872415: goto L_0x0a2c;
                case -2039746363: goto L_0x0a21;
                case -2023218804: goto L_0x0a16;
                case -1979538588: goto L_0x0a0b;
                case -1979536003: goto L_0x0a00;
                case -1979535888: goto L_0x09f5;
                case -1969004705: goto L_0x09ea;
                case -1946699248: goto L_0x09de;
                case -1646640058: goto L_0x09d2;
                case -1528047021: goto L_0x09c6;
                case -1493579426: goto L_0x09ba;
                case -1482481933: goto L_0x09ae;
                case -1480102982: goto L_0x09a3;
                case -1478041834: goto L_0x0997;
                case -1474543101: goto L_0x098c;
                case -1465695932: goto L_0x0980;
                case -1374906292: goto L_0x0974;
                case -1372940586: goto L_0x0968;
                case -1264245338: goto L_0x095c;
                case -1236154001: goto L_0x0950;
                case -1236086700: goto L_0x0944;
                case -1236077786: goto L_0x0938;
                case -1235796237: goto L_0x092c;
                case -1235760759: goto L_0x0920;
                case -1235686303: goto L_0x0915;
                case -1198046100: goto L_0x090a;
                case -1124254527: goto L_0x08fe;
                case -1085137927: goto L_0x08f2;
                case -1084856378: goto L_0x08e6;
                case -1084820900: goto L_0x08da;
                case -1084746444: goto L_0x08ce;
                case -819729482: goto L_0x08c2;
                case -772141857: goto L_0x08b6;
                case -638310039: goto L_0x08aa;
                case -590403924: goto L_0x089e;
                case -589196239: goto L_0x0892;
                case -589193654: goto L_0x0886;
                case -589193539: goto L_0x087a;
                case -440169325: goto L_0x086e;
                case -412748110: goto L_0x0862;
                case -228518075: goto L_0x0856;
                case -213586509: goto L_0x084a;
                case -115582002: goto L_0x083e;
                case -112621464: goto L_0x0832;
                case -108522133: goto L_0x0826;
                case -107572034: goto L_0x081b;
                case -40534265: goto L_0x080f;
                case 65254746: goto L_0x0803;
                case 141040782: goto L_0x07f7;
                case 202550149: goto L_0x07eb;
                case 309993049: goto L_0x07df;
                case 309995634: goto L_0x07d3;
                case 309995749: goto L_0x07c7;
                case 320532812: goto L_0x07bb;
                case 328933854: goto L_0x07af;
                case 331340546: goto L_0x07a3;
                case 342406591: goto L_0x0797;
                case 344816990: goto L_0x078b;
                case 346878138: goto L_0x077f;
                case 350376871: goto L_0x0773;
                case 608430149: goto L_0x0767;
                case 615714517: goto L_0x075c;
                case 715508879: goto L_0x0750;
                case 728985323: goto L_0x0744;
                case 731046471: goto L_0x0738;
                case 734545204: goto L_0x072c;
                case 802032552: goto L_0x0720;
                case 991498806: goto L_0x0714;
                case 1007364121: goto L_0x0708;
                case 1019850010: goto L_0x06fc;
                case 1019917311: goto L_0x06f0;
                case 1019926225: goto L_0x06e4;
                case 1020207774: goto L_0x06d8;
                case 1020243252: goto L_0x06cc;
                case 1020317708: goto L_0x06c0;
                case 1060282259: goto L_0x06b4;
                case 1060349560: goto L_0x06a8;
                case 1060358474: goto L_0x069c;
                case 1060640023: goto L_0x0690;
                case 1060675501: goto L_0x0684;
                case 1060749957: goto L_0x0679;
                case 1073049781: goto L_0x066d;
                case 1078101399: goto L_0x0661;
                case 1110103437: goto L_0x0655;
                case 1160762272: goto L_0x0649;
                case 1172918249: goto L_0x063d;
                case 1234591620: goto L_0x0631;
                case 1281128640: goto L_0x0625;
                case 1281131225: goto L_0x0619;
                case 1281131340: goto L_0x060d;
                case 1310789062: goto L_0x0602;
                case 1333118583: goto L_0x05f6;
                case 1361447897: goto L_0x05ea;
                case 1498266155: goto L_0x05de;
                case 1533804208: goto L_0x05d2;
                case 1540131626: goto L_0x05c6;
                case 1547988151: goto L_0x05ba;
                case 1561464595: goto L_0x05ae;
                case 1563525743: goto L_0x05a2;
                case 1567024476: goto L_0x0596;
                case 1810705077: goto L_0x058a;
                case 1815177512: goto L_0x057e;
                case 1954774321: goto L_0x0572;
                case 1963241394: goto L_0x0566;
                case 2014789757: goto L_0x055a;
                case 2022049433: goto L_0x054e;
                case 2034984710: goto L_0x0542;
                case 2048733346: goto L_0x0536;
                case 2099392181: goto L_0x052a;
                case 2140162142: goto L_0x051e;
                default: goto L_0x051c;
            }
        L_0x051c:
            goto L_0x0a4d
        L_0x051e:
            java.lang.String r1 = "CHAT_MESSAGE_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 60
            goto L_0x0a4e
        L_0x052a:
            java.lang.String r1 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 43
            goto L_0x0a4e
        L_0x0536:
            java.lang.String r1 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 28
            goto L_0x0a4e
        L_0x0542:
            java.lang.String r1 = "CHANNEL_MESSAGE_PLAYLIST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 45
            goto L_0x0a4e
        L_0x054e:
            java.lang.String r1 = "PINNED_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 93
            goto L_0x0a4e
        L_0x055a:
            java.lang.String r1 = "CHAT_PHOTO_EDITED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 68
            goto L_0x0a4e
        L_0x0566:
            java.lang.String r1 = "LOCKED_MESSAGE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 106(0x6a, float:1.49E-43)
            goto L_0x0a4e
        L_0x0572:
            java.lang.String r1 = "CHAT_MESSAGE_PLAYLIST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 82
            goto L_0x0a4e
        L_0x057e:
            java.lang.String r1 = "CHANNEL_MESSAGES"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 47
            goto L_0x0a4e
        L_0x058a:
            java.lang.String r1 = "MESSAGE_INVOICE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 21
            goto L_0x0a4e
        L_0x0596:
            java.lang.String r1 = "CHAT_MESSAGE_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 51
            goto L_0x0a4e
        L_0x05a2:
            java.lang.String r1 = "CHAT_MESSAGE_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 52
            goto L_0x0a4e
        L_0x05ae:
            java.lang.String r1 = "CHAT_MESSAGE_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 50
            goto L_0x0a4e
        L_0x05ba:
            java.lang.String r1 = "CHAT_MESSAGE_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 55
            goto L_0x0a4e
        L_0x05c6:
            java.lang.String r1 = "MESSAGE_PLAYLIST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 25
            goto L_0x0a4e
        L_0x05d2:
            java.lang.String r1 = "MESSAGE_VIDEOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 24
            goto L_0x0a4e
        L_0x05de:
            java.lang.String r1 = "PHONE_CALL_MISSED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 111(0x6f, float:1.56E-43)
            goto L_0x0a4e
        L_0x05ea:
            java.lang.String r1 = "MESSAGE_PHOTOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 23
            goto L_0x0a4e
        L_0x05f6:
            java.lang.String r1 = "CHAT_MESSAGE_VIDEOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 81
            goto L_0x0a4e
        L_0x0602:
            java.lang.String r1 = "MESSAGE_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 2
            goto L_0x0a4e
        L_0x060d:
            java.lang.String r1 = "MESSAGE_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 17
            goto L_0x0a4e
        L_0x0619:
            java.lang.String r1 = "MESSAGE_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 15
            goto L_0x0a4e
        L_0x0625:
            java.lang.String r1 = "MESSAGE_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 9
            goto L_0x0a4e
        L_0x0631:
            java.lang.String r1 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 63
            goto L_0x0a4e
        L_0x063d:
            java.lang.String r1 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 39
            goto L_0x0a4e
        L_0x0649:
            java.lang.String r1 = "CHAT_MESSAGE_PHOTOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 80
            goto L_0x0a4e
        L_0x0655:
            java.lang.String r1 = "CHAT_MESSAGE_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 49
            goto L_0x0a4e
        L_0x0661:
            java.lang.String r1 = "CHAT_TITLE_EDITED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 67
            goto L_0x0a4e
        L_0x066d:
            java.lang.String r1 = "PINNED_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 86
            goto L_0x0a4e
        L_0x0679:
            java.lang.String r1 = "MESSAGE_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 0
            goto L_0x0a4e
        L_0x0684:
            java.lang.String r1 = "MESSAGE_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 13
            goto L_0x0a4e
        L_0x0690:
            java.lang.String r1 = "MESSAGE_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 14
            goto L_0x0a4e
        L_0x069c:
            java.lang.String r1 = "MESSAGE_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 18
            goto L_0x0a4e
        L_0x06a8:
            java.lang.String r1 = "MESSAGE_FWDS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 22
            goto L_0x0a4e
        L_0x06b4:
            java.lang.String r1 = "MESSAGE_DOCS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 26
            goto L_0x0a4e
        L_0x06c0:
            java.lang.String r1 = "CHAT_MESSAGE_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 48
            goto L_0x0a4e
        L_0x06cc:
            java.lang.String r1 = "CHAT_MESSAGE_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 57
            goto L_0x0a4e
        L_0x06d8:
            java.lang.String r1 = "CHAT_MESSAGE_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 58
            goto L_0x0a4e
        L_0x06e4:
            java.lang.String r1 = "CHAT_MESSAGE_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 62
            goto L_0x0a4e
        L_0x06f0:
            java.lang.String r1 = "CHAT_MESSAGE_FWDS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 79
            goto L_0x0a4e
        L_0x06fc:
            java.lang.String r1 = "CHAT_MESSAGE_DOCS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 83
            goto L_0x0a4e
        L_0x0708:
            java.lang.String r1 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 20
            goto L_0x0a4e
        L_0x0714:
            java.lang.String r1 = "PINNED_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 97
            goto L_0x0a4e
        L_0x0720:
            java.lang.String r1 = "MESSAGE_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 12
            goto L_0x0a4e
        L_0x072c:
            java.lang.String r1 = "PINNED_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 88
            goto L_0x0a4e
        L_0x0738:
            java.lang.String r1 = "PINNED_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 89
            goto L_0x0a4e
        L_0x0744:
            java.lang.String r1 = "PINNED_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 87
            goto L_0x0a4e
        L_0x0750:
            java.lang.String r1 = "PINNED_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 92
            goto L_0x0a4e
        L_0x075c:
            java.lang.String r1 = "MESSAGE_PHOTO_SECRET"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 4
            goto L_0x0a4e
        L_0x0767:
            java.lang.String r1 = "CHAT_VOICECHAT_INVITE_YOU"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 73
            goto L_0x0a4e
        L_0x0773:
            java.lang.String r1 = "CHANNEL_MESSAGE_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 30
            goto L_0x0a4e
        L_0x077f:
            java.lang.String r1 = "CHANNEL_MESSAGE_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 31
            goto L_0x0a4e
        L_0x078b:
            java.lang.String r1 = "CHANNEL_MESSAGE_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 29
            goto L_0x0a4e
        L_0x0797:
            java.lang.String r1 = "CHAT_VOICECHAT_END"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 72
            goto L_0x0a4e
        L_0x07a3:
            java.lang.String r1 = "CHANNEL_MESSAGE_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 34
            goto L_0x0a4e
        L_0x07af:
            java.lang.String r1 = "CHAT_MESSAGE_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 54
            goto L_0x0a4e
        L_0x07bb:
            java.lang.String r1 = "MESSAGES"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 27
            goto L_0x0a4e
        L_0x07c7:
            java.lang.String r1 = "CHAT_MESSAGE_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 61
            goto L_0x0a4e
        L_0x07d3:
            java.lang.String r1 = "CHAT_MESSAGE_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 59
            goto L_0x0a4e
        L_0x07df:
            java.lang.String r1 = "CHAT_MESSAGE_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 53
            goto L_0x0a4e
        L_0x07eb:
            java.lang.String r1 = "CHAT_VOICECHAT_INVITE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 71
            goto L_0x0a4e
        L_0x07f7:
            java.lang.String r1 = "CHAT_LEFT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 76
            goto L_0x0a4e
        L_0x0803:
            java.lang.String r1 = "CHAT_ADD_YOU"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 66
            goto L_0x0a4e
        L_0x080f:
            java.lang.String r1 = "CHAT_DELETE_MEMBER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 74
            goto L_0x0a4e
        L_0x081b:
            java.lang.String r1 = "MESSAGE_SCREENSHOT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 7
            goto L_0x0a4e
        L_0x0826:
            java.lang.String r1 = "AUTH_REGION"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 105(0x69, float:1.47E-43)
            goto L_0x0a4e
        L_0x0832:
            java.lang.String r1 = "CONTACT_JOINED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 103(0x67, float:1.44E-43)
            goto L_0x0a4e
        L_0x083e:
            java.lang.String r1 = "CHAT_MESSAGE_INVOICE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 64
            goto L_0x0a4e
        L_0x084a:
            java.lang.String r1 = "ENCRYPTION_REQUEST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 107(0x6b, float:1.5E-43)
            goto L_0x0a4e
        L_0x0856:
            java.lang.String r1 = "MESSAGE_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 16
            goto L_0x0a4e
        L_0x0862:
            java.lang.String r1 = "CHAT_DELETE_YOU"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 75
            goto L_0x0a4e
        L_0x086e:
            java.lang.String r1 = "AUTH_UNKNOWN"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 104(0x68, float:1.46E-43)
            goto L_0x0a4e
        L_0x087a:
            java.lang.String r1 = "PINNED_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 101(0x65, float:1.42E-43)
            goto L_0x0a4e
        L_0x0886:
            java.lang.String r1 = "PINNED_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 96
            goto L_0x0a4e
        L_0x0892:
            java.lang.String r1 = "PINNED_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 90
            goto L_0x0a4e
        L_0x089e:
            java.lang.String r1 = "PINNED_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 99
            goto L_0x0a4e
        L_0x08aa:
            java.lang.String r1 = "CHANNEL_MESSAGE_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 33
            goto L_0x0a4e
        L_0x08b6:
            java.lang.String r1 = "PHONE_CALL_REQUEST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 109(0x6d, float:1.53E-43)
            goto L_0x0a4e
        L_0x08c2:
            java.lang.String r1 = "PINNED_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 91
            goto L_0x0a4e
        L_0x08ce:
            java.lang.String r1 = "PINNED_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 85
            goto L_0x0a4e
        L_0x08da:
            java.lang.String r1 = "PINNED_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 94
            goto L_0x0a4e
        L_0x08e6:
            java.lang.String r1 = "PINNED_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 95
            goto L_0x0a4e
        L_0x08f2:
            java.lang.String r1 = "PINNED_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 98
            goto L_0x0a4e
        L_0x08fe:
            java.lang.String r1 = "CHAT_MESSAGE_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 56
            goto L_0x0a4e
        L_0x090a:
            java.lang.String r1 = "MESSAGE_VIDEO_SECRET"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 6
            goto L_0x0a4e
        L_0x0915:
            java.lang.String r1 = "CHANNEL_MESSAGE_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 1
            goto L_0x0a4e
        L_0x0920:
            java.lang.String r1 = "CHANNEL_MESSAGE_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 36
            goto L_0x0a4e
        L_0x092c:
            java.lang.String r1 = "CHANNEL_MESSAGE_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 37
            goto L_0x0a4e
        L_0x0938:
            java.lang.String r1 = "CHANNEL_MESSAGE_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 41
            goto L_0x0a4e
        L_0x0944:
            java.lang.String r1 = "CHANNEL_MESSAGE_FWDS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 42
            goto L_0x0a4e
        L_0x0950:
            java.lang.String r1 = "CHANNEL_MESSAGE_DOCS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 46
            goto L_0x0a4e
        L_0x095c:
            java.lang.String r1 = "PINNED_INVOICE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 100
            goto L_0x0a4e
        L_0x0968:
            java.lang.String r1 = "CHAT_RETURNED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 77
            goto L_0x0a4e
        L_0x0974:
            java.lang.String r1 = "ENCRYPTED_MESSAGE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 102(0x66, float:1.43E-43)
            goto L_0x0a4e
        L_0x0980:
            java.lang.String r1 = "ENCRYPTION_ACCEPT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 108(0x6c, float:1.51E-43)
            goto L_0x0a4e
        L_0x098c:
            java.lang.String r1 = "MESSAGE_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 5
            goto L_0x0a4e
        L_0x0997:
            java.lang.String r1 = "MESSAGE_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 8
            goto L_0x0a4e
        L_0x09a3:
            java.lang.String r1 = "MESSAGE_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 3
            goto L_0x0a4e
        L_0x09ae:
            java.lang.String r1 = "MESSAGE_MUTED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 110(0x6e, float:1.54E-43)
            goto L_0x0a4e
        L_0x09ba:
            java.lang.String r1 = "MESSAGE_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 11
            goto L_0x0a4e
        L_0x09c6:
            java.lang.String r1 = "CHAT_MESSAGES"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 84
            goto L_0x0a4e
        L_0x09d2:
            java.lang.String r1 = "CHAT_VOICECHAT_START"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 70
            goto L_0x0a4e
        L_0x09de:
            java.lang.String r1 = "CHAT_JOINED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 78
            goto L_0x0a4e
        L_0x09ea:
            java.lang.String r1 = "CHAT_ADD_MEMBER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 69
            goto L_0x0a4e
        L_0x09f5:
            java.lang.String r1 = "CHANNEL_MESSAGE_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 40
            goto L_0x0a4e
        L_0x0a00:
            java.lang.String r1 = "CHANNEL_MESSAGE_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 38
            goto L_0x0a4e
        L_0x0a0b:
            java.lang.String r1 = "CHANNEL_MESSAGE_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 32
            goto L_0x0a4e
        L_0x0a16:
            java.lang.String r1 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 44
            goto L_0x0a4e
        L_0x0a21:
            java.lang.String r1 = "MESSAGE_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 10
            goto L_0x0a4e
        L_0x0a2c:
            java.lang.String r1 = "CHAT_CREATED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 65
            goto L_0x0a4e
        L_0x0a37:
            java.lang.String r1 = "CHANNEL_MESSAGE_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 35
            goto L_0x0a4e
        L_0x0a42:
            java.lang.String r1 = "MESSAGE_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0431 }
            if (r1 == 0) goto L_0x0a4d
            r1 = 19
            goto L_0x0a4e
        L_0x0a4d:
            r1 = -1
        L_0x0a4e:
            java.lang.String r5 = "MusicFiles"
            java.lang.String r10 = "Videos"
            r18 = r7
            java.lang.String r7 = "Photos"
            r36 = r15
            java.lang.String r15 = " "
            r37 = r11
            java.lang.String r11 = "NotificationGroupFew"
            r38 = r2
            java.lang.String r2 = "NotificationMessageFew"
            r39 = r13
            java.lang.String r14 = "ChannelMessageFew"
            java.lang.String r13 = "AttachSticker"
            switch(r1) {
                case 0: goto L_0x1b29;
                case 1: goto L_0x1b29;
                case 2: goto L_0x1b09;
                case 3: goto L_0x1aec;
                case 4: goto L_0x1acf;
                case 5: goto L_0x1ab2;
                case 6: goto L_0x1a94;
                case 7: goto L_0x1a7e;
                case 8: goto L_0x1a60;
                case 9: goto L_0x1a42;
                case 10: goto L_0x19e7;
                case 11: goto L_0x19c9;
                case 12: goto L_0x19a6;
                case 13: goto L_0x1983;
                case 14: goto L_0x1960;
                case 15: goto L_0x1942;
                case 16: goto L_0x1924;
                case 17: goto L_0x1906;
                case 18: goto L_0x18e3;
                case 19: goto L_0x18c4;
                case 20: goto L_0x18c4;
                case 21: goto L_0x18a1;
                case 22: goto L_0x187b;
                case 23: goto L_0x1857;
                case 24: goto L_0x1834;
                case 25: goto L_0x1811;
                case 26: goto L_0x17ec;
                case 27: goto L_0x17d6;
                case 28: goto L_0x17b8;
                case 29: goto L_0x179a;
                case 30: goto L_0x177c;
                case 31: goto L_0x175e;
                case 32: goto L_0x1740;
                case 33: goto L_0x16e5;
                case 34: goto L_0x16c7;
                case 35: goto L_0x16a4;
                case 36: goto L_0x1681;
                case 37: goto L_0x165e;
                case 38: goto L_0x1640;
                case 39: goto L_0x1622;
                case 40: goto L_0x1604;
                case 41: goto L_0x15e6;
                case 42: goto L_0x15bc;
                case 43: goto L_0x1598;
                case 44: goto L_0x1574;
                case 45: goto L_0x1550;
                case 46: goto L_0x152a;
                case 47: goto L_0x1515;
                case 48: goto L_0x14f4;
                case 49: goto L_0x14d1;
                case 50: goto L_0x14ae;
                case 51: goto L_0x148b;
                case 52: goto L_0x1468;
                case 53: goto L_0x1445;
                case 54: goto L_0x13cc;
                case 55: goto L_0x13a9;
                case 56: goto L_0x1381;
                case 57: goto L_0x1359;
                case 58: goto L_0x1331;
                case 59: goto L_0x130e;
                case 60: goto L_0x12eb;
                case 61: goto L_0x12c8;
                case 62: goto L_0x12a0;
                case 63: goto L_0x127c;
                case 64: goto L_0x1254;
                case 65: goto L_0x123a;
                case 66: goto L_0x123a;
                case 67: goto L_0x1220;
                case 68: goto L_0x1206;
                case 69: goto L_0x11e7;
                case 70: goto L_0x11cd;
                case 71: goto L_0x11ae;
                case 72: goto L_0x1194;
                case 73: goto L_0x117a;
                case 74: goto L_0x1160;
                case 75: goto L_0x1146;
                case 76: goto L_0x112c;
                case 77: goto L_0x1112;
                case 78: goto L_0x10f8;
                case 79: goto L_0x10cd;
                case 80: goto L_0x10a4;
                case 81: goto L_0x107b;
                case 82: goto L_0x1052;
                case 83: goto L_0x1027;
                case 84: goto L_0x100d;
                case 85: goto L_0x0fb8;
                case 86: goto L_0x0f6d;
                case 87: goto L_0x0var_;
                case 88: goto L_0x0ed7;
                case 89: goto L_0x0e8c;
                case 90: goto L_0x0e41;
                case 91: goto L_0x0d8a;
                case 92: goto L_0x0d3f;
                case 93: goto L_0x0cea;
                case 94: goto L_0x0CLASSNAME;
                case 95: goto L_0x0CLASSNAME;
                case 96: goto L_0x0bfb;
                case 97: goto L_0x0bb5;
                case 98: goto L_0x0b6c;
                case 99: goto L_0x0b23;
                case 100: goto L_0x0ada;
                case 101: goto L_0x0a91;
                case 102: goto L_0x0a75;
                case 103: goto L_0x0a71;
                case 104: goto L_0x0a71;
                case 105: goto L_0x0a71;
                case 106: goto L_0x0a71;
                case 107: goto L_0x0a71;
                case 108: goto L_0x0a71;
                case 109: goto L_0x0a71;
                case 110: goto L_0x0a71;
                case 111: goto L_0x0a71;
                default: goto L_0x0a6b;
            }
        L_0x0a6b:
            r1 = r18
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1c2f }
            goto L_0x1b44
        L_0x0a71:
            r1 = r18
            goto L_0x1b5a
        L_0x0a75:
            java.lang.String r1 = "YouHaveNewMessage"
            r2 = 2131628532(0x7f0e11f4, float:1.888436E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = "SecretChatName"
            r5 = 2131627509(0x7f0e0df5, float:1.8882284E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r5)     // Catch:{ all -> 0x0431 }
            r34 = r2
            r5 = 1
            r16 = 0
            r2 = r1
            r1 = r18
            goto L_0x1b5e
        L_0x0a91:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0aad
            java.lang.String r1 = "NotificationActionPinnedGifUser"
            r2 = 2131626449(0x7f0e09d1, float:1.8880135E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0aad:
            if (r6 == 0) goto L_0x0ac7
            java.lang.String r1 = "NotificationActionPinnedGif"
            r2 = 2131626447(0x7f0e09cf, float:1.888013E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0ac7:
            java.lang.String r1 = "NotificationActionPinnedGifChannel"
            r2 = 2131626448(0x7f0e09d0, float:1.8880132E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0431 }
            r6[r5] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0ada:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0af6
            java.lang.String r1 = "NotificationActionPinnedInvoiceUser"
            r2 = 2131626452(0x7f0e09d4, float:1.888014E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0af6:
            if (r6 == 0) goto L_0x0b10
            java.lang.String r1 = "NotificationActionPinnedInvoice"
            r2 = 2131626450(0x7f0e09d2, float:1.8880137E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0b10:
            java.lang.String r1 = "NotificationActionPinnedInvoiceChannel"
            r2 = 2131626451(0x7f0e09d3, float:1.8880139E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0431 }
            r6[r5] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0b23:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0b3f
            java.lang.String r1 = "NotificationActionPinnedGameScoreUser"
            r2 = 2131626439(0x7f0e09c7, float:1.8880114E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0b3f:
            if (r6 == 0) goto L_0x0b59
            java.lang.String r1 = "NotificationActionPinnedGameScore"
            r2 = 2131626437(0x7f0e09c5, float:1.888011E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0b59:
            java.lang.String r1 = "NotificationActionPinnedGameScoreChannel"
            r2 = 2131626438(0x7f0e09c6, float:1.8880112E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0431 }
            r6[r5] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0b6c:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0b88
            java.lang.String r1 = "NotificationActionPinnedGameUser"
            r2 = 2131626440(0x7f0e09c8, float:1.8880116E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0b88:
            if (r6 == 0) goto L_0x0ba2
            java.lang.String r1 = "NotificationActionPinnedGame"
            r2 = 2131626435(0x7f0e09c3, float:1.8880106E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0ba2:
            java.lang.String r1 = "NotificationActionPinnedGameChannel"
            r2 = 2131626436(0x7f0e09c4, float:1.8880108E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0431 }
            r6[r5] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0bb5:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0bd0
            java.lang.String r1 = "NotificationActionPinnedGeoLiveUser"
            r2 = 2131626445(0x7f0e09cd, float:1.8880126E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0bd0:
            if (r6 == 0) goto L_0x0be9
            java.lang.String r1 = "NotificationActionPinnedGeoLive"
            r2 = 2131626443(0x7f0e09cb, float:1.8880122E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0be9:
            java.lang.String r1 = "NotificationActionPinnedGeoLiveChannel"
            r2 = 2131626444(0x7f0e09cc, float:1.8880124E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0431 }
            r6[r5] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0bfb:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGeoUser"
            r2 = 2131626446(0x7f0e09ce, float:1.8880128E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            if (r6 == 0) goto L_0x0c2f
            java.lang.String r1 = "NotificationActionPinnedGeo"
            r2 = 2131626441(0x7f0e09c9, float:1.8880118E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0c2f:
            java.lang.String r1 = "NotificationActionPinnedGeoChannel"
            r2 = 2131626442(0x7f0e09ca, float:1.888012E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0431 }
            r6[r5] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0431 }
        L_0x0CLASSNAME:
            r2 = r1
            r1 = r18
            goto L_0x1b5b
        L_0x0CLASSNAME:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedPollUser"
            r2 = 2131626464(0x7f0e09e0, float:1.8880165E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            if (r6 == 0) goto L_0x0c7e
            java.lang.String r1 = "NotificationActionPinnedPoll2"
            r2 = 2131626462(0x7f0e09de, float:1.888016E38)
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 2
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r8 = 1
            r5[r8] = r7     // Catch:{ all -> 0x0431 }
            r7 = r12[r8]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0c7e:
            java.lang.String r1 = "NotificationActionPinnedPollChannel2"
            r2 = 2131626463(0x7f0e09df, float:1.8880163E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0cb3
            java.lang.String r2 = "NotificationActionPinnedQuizUser"
            r5 = 2131626467(0x7f0e09e3, float:1.8880171E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0cb3:
            if (r6 == 0) goto L_0x0cd2
            java.lang.String r2 = "NotificationActionPinnedQuiz2"
            r5 = 2131626465(0x7f0e09e1, float:1.8880167E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r10 = 1
            r6[r10] = r8     // Catch:{ all -> 0x0431 }
            r8 = r12[r10]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0cd2:
            java.lang.String r2 = "NotificationActionPinnedQuizChannel2"
            r5 = 2131626466(0x7f0e09e2, float:1.888017E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0cea:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0d08
            java.lang.String r2 = "NotificationActionPinnedContactUser"
            r5 = 2131626431(0x7f0e09bf, float:1.8880098E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0d08:
            if (r6 == 0) goto L_0x0d27
            java.lang.String r2 = "NotificationActionPinnedContact2"
            r5 = 2131626429(0x7f0e09bd, float:1.8880094E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r10 = 1
            r6[r10] = r8     // Catch:{ all -> 0x0431 }
            r8 = r12[r10]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0d27:
            java.lang.String r2 = "NotificationActionPinnedContactChannel2"
            r5 = 2131626430(0x7f0e09be, float:1.8880096E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0d3f:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0d5d
            java.lang.String r2 = "NotificationActionPinnedVoiceUser"
            r5 = 2131626485(0x7f0e09f5, float:1.8880208E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0d5d:
            if (r6 == 0) goto L_0x0d77
            java.lang.String r2 = "NotificationActionPinnedVoice"
            r5 = 2131626483(0x7f0e09f3, float:1.8880203E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0d77:
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            r5 = 2131626484(0x7f0e09f4, float:1.8880205E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0d8a:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0dc7
            int r2 = r12.length     // Catch:{ all -> 0x0431 }
            r5 = 1
            if (r2 <= r5) goto L_0x0db4
            r2 = r12[r5]     // Catch:{ all -> 0x0431 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0431 }
            if (r2 != 0) goto L_0x0db4
            java.lang.String r2 = "NotificationActionPinnedStickerEmojiUser"
            r5 = 2131626475(0x7f0e09eb, float:1.8880187E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0db4:
            java.lang.String r2 = "NotificationActionPinnedStickerUser"
            r5 = 2131626476(0x7f0e09ec, float:1.888019E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0dc7:
            if (r6 == 0) goto L_0x0e0a
            int r2 = r12.length     // Catch:{ all -> 0x0431 }
            r5 = 2
            if (r2 <= r5) goto L_0x0df2
            r2 = r12[r5]     // Catch:{ all -> 0x0431 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0431 }
            if (r2 != 0) goto L_0x0df2
            java.lang.String r2 = "NotificationActionPinnedStickerEmoji"
            r5 = 2131626473(0x7f0e09e9, float:1.8880183E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r10 = 1
            r6[r10] = r8     // Catch:{ all -> 0x0431 }
            r8 = r12[r10]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0df2:
            java.lang.String r2 = "NotificationActionPinnedSticker"
            r5 = 2131626471(0x7f0e09e7, float:1.888018E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0e0a:
            int r2 = r12.length     // Catch:{ all -> 0x0431 }
            r5 = 1
            if (r2 <= r5) goto L_0x0e2e
            r2 = r12[r5]     // Catch:{ all -> 0x0431 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0431 }
            if (r2 != 0) goto L_0x0e2e
            java.lang.String r2 = "NotificationActionPinnedStickerEmojiChannel"
            r5 = 2131626474(0x7f0e09ea, float:1.8880185E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0e2e:
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            r5 = 2131626472(0x7f0e09e8, float:1.8880181E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0e41:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e5f
            java.lang.String r2 = "NotificationActionPinnedFileUser"
            r5 = 2131626434(0x7f0e09c2, float:1.8880104E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0e5f:
            if (r6 == 0) goto L_0x0e79
            java.lang.String r2 = "NotificationActionPinnedFile"
            r5 = 2131626432(0x7f0e09c0, float:1.88801E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0e79:
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            r5 = 2131626433(0x7f0e09c1, float:1.8880102E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0e8c:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0eaa
            java.lang.String r2 = "NotificationActionPinnedRoundUser"
            r5 = 2131626470(0x7f0e09e6, float:1.8880177E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0eaa:
            if (r6 == 0) goto L_0x0ec4
            java.lang.String r2 = "NotificationActionPinnedRound"
            r5 = 2131626468(0x7f0e09e4, float:1.8880173E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0ec4:
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            r5 = 2131626469(0x7f0e09e5, float:1.8880175E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0ed7:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0ef5
            java.lang.String r2 = "NotificationActionPinnedVideoUser"
            r5 = 2131626482(0x7f0e09f2, float:1.8880201E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0ef5:
            if (r6 == 0) goto L_0x0f0f
            java.lang.String r2 = "NotificationActionPinnedVideo"
            r5 = 2131626480(0x7f0e09f0, float:1.8880197E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0f0f:
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            r5 = 2131626481(0x7f0e09f1, float:1.88802E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0var_:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0var_
            java.lang.String r2 = "NotificationActionPinnedPhotoUser"
            r5 = 2131626461(0x7f0e09dd, float:1.8880159E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0var_:
            if (r6 == 0) goto L_0x0f5a
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            r5 = 2131626459(0x7f0e09db, float:1.8880155E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0f5a:
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            r5 = 2131626460(0x7f0e09dc, float:1.8880157E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0f6d:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0f8b
            java.lang.String r2 = "NotificationActionPinnedNoTextUser"
            r5 = 2131626458(0x7f0e09da, float:1.8880153E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0f8b:
            if (r6 == 0) goto L_0x0fa5
            java.lang.String r2 = "NotificationActionPinnedNoText"
            r5 = 2131626456(0x7f0e09d8, float:1.8880149E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0fa5:
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            r5 = 2131626457(0x7f0e09d9, float:1.888015E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0fb8:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0fd6
            java.lang.String r2 = "NotificationActionPinnedTextUser"
            r5 = 2131626479(0x7f0e09ef, float:1.8880195E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0fd6:
            if (r6 == 0) goto L_0x0ff5
            java.lang.String r2 = "NotificationActionPinnedText"
            r5 = 2131626477(0x7f0e09ed, float:1.8880191E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x0ff5:
            java.lang.String r2 = "NotificationActionPinnedTextChannel"
            r5 = 2131626478(0x7f0e09ee, float:1.8880193E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x100d:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAlbum"
            r5 = 2131626494(0x7f0e09fe, float:1.8880226E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x17e9
        L_0x1027:
            r1 = r18
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0431 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0431 }
            r2[r5] = r6     // Catch:{ all -> 0x0431 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0431 }
            r2[r5] = r6     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "Files"
            r6 = 2
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0431 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)     // Catch:{ all -> 0x0431 }
            r2[r6] = r5     // Catch:{ all -> 0x0431 }
            r5 = 2131626497(0x7f0e0a01, float:1.8880232E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r5, r2)     // Catch:{ all -> 0x0431 }
            goto L_0x17e9
        L_0x1052:
            r1 = r18
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r2[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r2[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 2
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0431 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)     // Catch:{ all -> 0x0431 }
            r2[r6] = r5     // Catch:{ all -> 0x0431 }
            r5 = 2131626497(0x7f0e0a01, float:1.8880232E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r5, r2)     // Catch:{ all -> 0x0431 }
            goto L_0x17e9
        L_0x107b:
            r1 = r18
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0431 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0431 }
            r2[r5] = r6     // Catch:{ all -> 0x0431 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0431 }
            r2[r5] = r6     // Catch:{ all -> 0x0431 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0431 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x0431 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0431 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r10, r6)     // Catch:{ all -> 0x0431 }
            r2[r5] = r6     // Catch:{ all -> 0x0431 }
            r5 = 2131626497(0x7f0e0a01, float:1.8880232E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r5, r2)     // Catch:{ all -> 0x0431 }
            goto L_0x17e9
        L_0x10a4:
            r1 = r18
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0431 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0431 }
            r2[r5] = r6     // Catch:{ all -> 0x0431 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0431 }
            r2[r5] = r6     // Catch:{ all -> 0x0431 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0431 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x0431 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0431 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r7, r6)     // Catch:{ all -> 0x0431 }
            r2[r5] = r6     // Catch:{ all -> 0x0431 }
            r5 = 2131626497(0x7f0e0a01, float:1.8880232E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r5, r2)     // Catch:{ all -> 0x0431 }
            goto L_0x17e9
        L_0x10cd:
            r1 = r18
            java.lang.String r2 = "NotificationGroupForwardedFew"
            r5 = 2131626498(0x7f0e0a02, float:1.8880234E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r10 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r10     // Catch:{ all -> 0x0431 }
            r7 = 1
            r10 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r10     // Catch:{ all -> 0x0431 }
            r7 = 2
            r10 = r12[r7]     // Catch:{ all -> 0x0431 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt(r10)     // Catch:{ all -> 0x0431 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x0431 }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r10)     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x17e9
        L_0x10f8:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            r5 = 2131626493(0x7f0e09fd, float:1.8880224E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x1112:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAddSelf"
            r5 = 2131626492(0x7f0e09fc, float:1.8880222E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x112c:
            r1 = r18
            java.lang.String r2 = "NotificationGroupLeftMember"
            r5 = 2131626503(0x7f0e0a07, float:1.8880244E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x1146:
            r1 = r18
            java.lang.String r2 = "NotificationGroupKickYou"
            r5 = 2131626502(0x7f0e0a06, float:1.8880242E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x1160:
            r1 = r18
            java.lang.String r2 = "NotificationGroupKickMember"
            r5 = 2131626501(0x7f0e0a05, float:1.888024E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x117a:
            r1 = r18
            java.lang.String r2 = "NotificationGroupInvitedYouToCall"
            r5 = 2131626500(0x7f0e0a04, float:1.8880238E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x1194:
            r1 = r18
            java.lang.String r2 = "NotificationGroupEndedCall"
            r5 = 2131626496(0x7f0e0a00, float:1.888023E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x11ae:
            r1 = r18
            java.lang.String r2 = "NotificationGroupInvitedToCall"
            r5 = 2131626499(0x7f0e0a03, float:1.8880236E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x11cd:
            r1 = r18
            java.lang.String r2 = "NotificationGroupCreatedCall"
            r5 = 2131626495(0x7f0e09ff, float:1.8880228E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x11e7:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAddMember"
            r5 = 2131626491(0x7f0e09fb, float:1.888022E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x1206:
            r1 = r18
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            r5 = 2131626489(0x7f0e09f9, float:1.8880216E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x1220:
            r1 = r18
            java.lang.String r2 = "NotificationEditedGroupName"
            r5 = 2131626488(0x7f0e09f8, float:1.8880214E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x123a:
            r1 = r18
            java.lang.String r2 = "NotificationInvitedToGroup"
            r5 = 2131626508(0x7f0e0a0c, float:1.8880254E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x1254:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupInvoice"
            r5 = 2131626525(0x7f0e0a1d, float:1.8880289E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "PaymentInvoice"
            r6 = 2131626916(0x7f0e0ba4, float:1.8881082E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x127c:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupGameScored"
            r5 = 2131626523(0x7f0e0a1b, float:1.8880285E38)
            r6 = 4
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r8 = 0
            r10 = r12[r8]     // Catch:{ all -> 0x0431 }
            r6[r8] = r10     // Catch:{ all -> 0x0431 }
            r8 = 1
            r10 = r12[r8]     // Catch:{ all -> 0x0431 }
            r6[r8] = r10     // Catch:{ all -> 0x0431 }
            r8 = 2
            r10 = r12[r8]     // Catch:{ all -> 0x0431 }
            r6[r8] = r10     // Catch:{ all -> 0x0431 }
            r7 = 3
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x12a0:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupGame"
            r5 = 2131626522(0x7f0e0a1a, float:1.8880283E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachGame"
            r6 = 2131624391(0x7f0e01c7, float:1.887596E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x12c8:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupGif"
            r5 = 2131626524(0x7f0e0a1c, float:1.8880287E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachGif"
            r6 = 2131624392(0x7f0e01c8, float:1.8875962E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x12eb:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupLiveLocation"
            r5 = 2131626526(0x7f0e0a1e, float:1.888029E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachLiveLocation"
            r6 = 2131624397(0x7f0e01cd, float:1.8875973E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x130e:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupMap"
            r5 = 2131626527(0x7f0e0a1f, float:1.8880293E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachLocation"
            r6 = 2131624401(0x7f0e01d1, float:1.887598E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1331:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupPoll2"
            r5 = 2131626531(0x7f0e0a23, float:1.88803E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "Poll"
            r6 = 2131627083(0x7f0e0c4b, float:1.888142E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1359:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupQuiz2"
            r5 = 2131626532(0x7f0e0a24, float:1.8880303E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "PollQuiz"
            r6 = 2131627090(0x7f0e0CLASSNAME, float:1.8881435E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1381:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupContact2"
            r5 = 2131626520(0x7f0e0a18, float:1.8880279E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachContact"
            r6 = 2131624387(0x7f0e01c3, float:1.8875952E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x13a9:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupAudio"
            r5 = 2131626519(0x7f0e0a17, float:1.8880276E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachAudio"
            r6 = 2131624385(0x7f0e01c1, float:1.8875948E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x13cc:
            r1 = r18
            int r2 = r12.length     // Catch:{ all -> 0x0431 }
            r5 = 2
            if (r2 <= r5) goto L_0x1412
            r2 = r12[r5]     // Catch:{ all -> 0x0431 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0431 }
            if (r2 != 0) goto L_0x1412
            java.lang.String r2 = "NotificationMessageGroupStickerEmoji"
            r5 = 2131626535(0x7f0e0a27, float:1.8880309E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0431 }
            r5.<init>()     // Catch:{ all -> 0x0431 }
            r6 = r12[r7]     // Catch:{ all -> 0x0431 }
            r5.append(r6)     // Catch:{ all -> 0x0431 }
            r5.append(r15)     // Catch:{ all -> 0x0431 }
            r6 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)     // Catch:{ all -> 0x0431 }
            r5.append(r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1412:
            java.lang.String r2 = "NotificationMessageGroupSticker"
            r5 = 2131626534(0x7f0e0a26, float:1.8880307E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0431 }
            r5.<init>()     // Catch:{ all -> 0x0431 }
            r6 = r12[r7]     // Catch:{ all -> 0x0431 }
            r5.append(r6)     // Catch:{ all -> 0x0431 }
            r5.append(r15)     // Catch:{ all -> 0x0431 }
            r6 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)     // Catch:{ all -> 0x0431 }
            r5.append(r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1445:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupDocument"
            r5 = 2131626521(0x7f0e0a19, float:1.888028E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachDocument"
            r6 = 2131624390(0x7f0e01c6, float:1.8875958E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1468:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupRound"
            r5 = 2131626533(0x7f0e0a25, float:1.8880305E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachRound"
            r6 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x148b:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupVideo"
            r5 = 2131626537(0x7f0e0a29, float:1.8880313E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachVideo"
            r6 = 2131624411(0x7f0e01db, float:1.8876E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x14ae:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupPhoto"
            r5 = 2131626530(0x7f0e0a22, float:1.8880299E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachPhoto"
            r6 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x14d1:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupNoText"
            r5 = 2131626529(0x7f0e0a21, float:1.8880297E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "Message"
            r6 = 2131626212(0x7f0e08e4, float:1.8879654E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x14f4:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupText"
            r5 = 2131626536(0x7f0e0a28, float:1.888031E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            r5 = r12[r7]     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1515:
            r1 = r18
            java.lang.String r2 = "ChannelMessageAlbum"
            r5 = 2131624754(0x7f0e0332, float:1.8876697E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            goto L_0x17e9
        L_0x152a:
            r1 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0431 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0431 }
            r2[r5] = r6     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "Files"
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0431 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)     // Catch:{ all -> 0x0431 }
            r2[r6] = r5     // Catch:{ all -> 0x0431 }
            r5 = 2131624758(0x7f0e0336, float:1.8876705E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x0431 }
            goto L_0x17e9
        L_0x1550:
            r1 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r2[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0431 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)     // Catch:{ all -> 0x0431 }
            r2[r6] = r5     // Catch:{ all -> 0x0431 }
            r5 = 2131624758(0x7f0e0336, float:1.8876705E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x0431 }
            goto L_0x17e9
        L_0x1574:
            r1 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0431 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0431 }
            r2[r5] = r6     // Catch:{ all -> 0x0431 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0431 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x0431 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0431 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r10, r6)     // Catch:{ all -> 0x0431 }
            r2[r5] = r6     // Catch:{ all -> 0x0431 }
            r5 = 2131624758(0x7f0e0336, float:1.8876705E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x0431 }
            goto L_0x17e9
        L_0x1598:
            r1 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0431 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0431 }
            r2[r5] = r6     // Catch:{ all -> 0x0431 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0431 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x0431 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0431 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r7, r6)     // Catch:{ all -> 0x0431 }
            r2[r5] = r6     // Catch:{ all -> 0x0431 }
            r5 = 2131624758(0x7f0e0336, float:1.8876705E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x0431 }
            goto L_0x17e9
        L_0x15bc:
            r1 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0431 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0431 }
            r2[r5] = r6     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "ForwardedMessageCount"
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0431 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = r5.toLowerCase()     // Catch:{ all -> 0x0431 }
            r2[r6] = r5     // Catch:{ all -> 0x0431 }
            r5 = 2131624758(0x7f0e0336, float:1.8876705E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x0431 }
            goto L_0x17e9
        L_0x15e6:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGame"
            r5 = 2131626516(0x7f0e0a14, float:1.888027E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachGame"
            r6 = 2131624391(0x7f0e01c7, float:1.887596E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1604:
            r1 = r18
            java.lang.String r2 = "ChannelMessageGIF"
            r5 = 2131624759(0x7f0e0337, float:1.8876707E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachGif"
            r6 = 2131624392(0x7f0e01c8, float:1.8875962E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1622:
            r1 = r18
            java.lang.String r2 = "ChannelMessageLiveLocation"
            r5 = 2131624760(0x7f0e0338, float:1.8876709E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachLiveLocation"
            r6 = 2131624397(0x7f0e01cd, float:1.8875973E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1640:
            r1 = r18
            java.lang.String r2 = "ChannelMessageMap"
            r5 = 2131624761(0x7f0e0339, float:1.887671E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachLocation"
            r6 = 2131624401(0x7f0e01d1, float:1.887598E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x165e:
            r1 = r18
            java.lang.String r2 = "ChannelMessagePoll2"
            r5 = 2131624765(0x7f0e033d, float:1.8876719E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "Poll"
            r6 = 2131627083(0x7f0e0c4b, float:1.888142E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1681:
            r1 = r18
            java.lang.String r2 = "ChannelMessageQuiz2"
            r5 = 2131624766(0x7f0e033e, float:1.887672E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "QuizPoll"
            r6 = 2131627264(0x7f0e0d00, float:1.8881788E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x16a4:
            r1 = r18
            java.lang.String r2 = "ChannelMessageContact2"
            r5 = 2131624756(0x7f0e0334, float:1.88767E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachContact"
            r6 = 2131624387(0x7f0e01c3, float:1.8875952E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x16c7:
            r1 = r18
            java.lang.String r2 = "ChannelMessageAudio"
            r5 = 2131624755(0x7f0e0333, float:1.8876699E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachAudio"
            r6 = 2131624385(0x7f0e01c1, float:1.8875948E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x16e5:
            r1 = r18
            int r2 = r12.length     // Catch:{ all -> 0x0431 }
            r5 = 1
            if (r2 <= r5) goto L_0x1726
            r2 = r12[r5]     // Catch:{ all -> 0x0431 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0431 }
            if (r2 != 0) goto L_0x1726
            java.lang.String r2 = "ChannelMessageStickerEmoji"
            r5 = 2131624769(0x7f0e0341, float:1.8876727E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0431 }
            r5.<init>()     // Catch:{ all -> 0x0431 }
            r6 = r12[r7]     // Catch:{ all -> 0x0431 }
            r5.append(r6)     // Catch:{ all -> 0x0431 }
            r5.append(r15)     // Catch:{ all -> 0x0431 }
            r6 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)     // Catch:{ all -> 0x0431 }
            r5.append(r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1726:
            java.lang.String r2 = "ChannelMessageSticker"
            r5 = 2131624768(0x7f0e0340, float:1.8876725E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            r5 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1740:
            r1 = r18
            java.lang.String r2 = "ChannelMessageDocument"
            r5 = 2131624757(0x7f0e0335, float:1.8876703E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachDocument"
            r6 = 2131624390(0x7f0e01c6, float:1.8875958E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x175e:
            r1 = r18
            java.lang.String r2 = "ChannelMessageRound"
            r5 = 2131624767(0x7f0e033f, float:1.8876723E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachRound"
            r6 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x177c:
            r1 = r18
            java.lang.String r2 = "ChannelMessageVideo"
            r5 = 2131624770(0x7f0e0342, float:1.887673E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachVideo"
            r6 = 2131624411(0x7f0e01db, float:1.8876E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x179a:
            r1 = r18
            java.lang.String r2 = "ChannelMessagePhoto"
            r5 = 2131624764(0x7f0e033c, float:1.8876717E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachPhoto"
            r6 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x17b8:
            r1 = r18
            java.lang.String r2 = "ChannelMessageNoText"
            r5 = 2131624763(0x7f0e033b, float:1.8876715E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "Message"
            r6 = 2131626212(0x7f0e08e4, float:1.8879654E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x17d6:
            r1 = r18
            java.lang.String r2 = "NotificationMessageAlbum"
            r5 = 2131626510(0x7f0e0a0e, float:1.8880258E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
        L_0x17e9:
            r5 = 1
            goto L_0x1b5c
        L_0x17ec:
            r1 = r18
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            java.lang.String r6 = "Files"
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ all -> 0x0431 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0431 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r6, r8)     // Catch:{ all -> 0x0431 }
            r5[r7] = r6     // Catch:{ all -> 0x0431 }
            r6 = 2131626514(0x7f0e0a12, float:1.8880266E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x17e9
        L_0x1811:
            r1 = r18
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ all -> 0x0431 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r8)     // Catch:{ all -> 0x0431 }
            r6[r7] = r5     // Catch:{ all -> 0x0431 }
            r5 = 2131626514(0x7f0e0a12, float:1.8880266E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x17e9
        L_0x1834:
            r1 = r18
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0431 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x0431 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r10, r7)     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 2131626514(0x7f0e0a12, float:1.8880266E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x17e9
        L_0x1857:
            r1 = r18
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r5[r6] = r8     // Catch:{ all -> 0x0431 }
            r6 = 1
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ all -> 0x0431 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0431 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r8)     // Catch:{ all -> 0x0431 }
            r5[r6] = r7     // Catch:{ all -> 0x0431 }
            r6 = 2131626514(0x7f0e0a12, float:1.8880266E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x17e9
        L_0x187b:
            r1 = r18
            java.lang.String r2 = "NotificationMessageForwardFew"
            r5 = 2131626515(0x7f0e0a13, float:1.8880268E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r10 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r10     // Catch:{ all -> 0x0431 }
            r7 = 1
            r10 = r12[r7]     // Catch:{ all -> 0x0431 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt(r10)     // Catch:{ all -> 0x0431 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x0431 }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r10)     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x17e9
        L_0x18a1:
            r1 = r18
            java.lang.String r2 = "NotificationMessageInvoice"
            r5 = 2131626538(0x7f0e0a2a, float:1.8880315E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "PaymentInvoice"
            r6 = 2131626916(0x7f0e0ba4, float:1.8881082E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x18c4:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGameScored"
            r5 = 2131626517(0x7f0e0a15, float:1.8880272E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x18e3:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGame"
            r5 = 2131626516(0x7f0e0a14, float:1.888027E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachGame"
            r6 = 2131624391(0x7f0e01c7, float:1.887596E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1906:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGif"
            r5 = 2131626518(0x7f0e0a16, float:1.8880274E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachGif"
            r6 = 2131624392(0x7f0e01c8, float:1.8875962E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1924:
            r1 = r18
            java.lang.String r2 = "NotificationMessageLiveLocation"
            r5 = 2131626539(0x7f0e0a2b, float:1.8880317E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachLiveLocation"
            r6 = 2131624397(0x7f0e01cd, float:1.8875973E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1942:
            r1 = r18
            java.lang.String r2 = "NotificationMessageMap"
            r5 = 2131626540(0x7f0e0a2c, float:1.888032E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachLocation"
            r6 = 2131624401(0x7f0e01d1, float:1.887598E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1960:
            r1 = r18
            java.lang.String r2 = "NotificationMessagePoll2"
            r5 = 2131626544(0x7f0e0a30, float:1.8880327E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "Poll"
            r6 = 2131627083(0x7f0e0c4b, float:1.888142E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1983:
            r1 = r18
            java.lang.String r2 = "NotificationMessageQuiz2"
            r5 = 2131626545(0x7f0e0a31, float:1.888033E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "QuizPoll"
            r6 = 2131627264(0x7f0e0d00, float:1.8881788E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x19a6:
            r1 = r18
            java.lang.String r2 = "NotificationMessageContact2"
            r5 = 2131626512(0x7f0e0a10, float:1.8880262E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachContact"
            r6 = 2131624387(0x7f0e01c3, float:1.8875952E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x19c9:
            r1 = r18
            java.lang.String r2 = "NotificationMessageAudio"
            r5 = 2131626511(0x7f0e0a0f, float:1.888026E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachAudio"
            r6 = 2131624385(0x7f0e01c1, float:1.8875948E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x19e7:
            r1 = r18
            int r2 = r12.length     // Catch:{ all -> 0x0431 }
            r5 = 1
            if (r2 <= r5) goto L_0x1a28
            r2 = r12[r5]     // Catch:{ all -> 0x0431 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0431 }
            if (r2 != 0) goto L_0x1a28
            java.lang.String r2 = "NotificationMessageStickerEmoji"
            r5 = 2131626552(0x7f0e0a38, float:1.8880343E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0431 }
            r5.<init>()     // Catch:{ all -> 0x0431 }
            r6 = r12[r7]     // Catch:{ all -> 0x0431 }
            r5.append(r6)     // Catch:{ all -> 0x0431 }
            r5.append(r15)     // Catch:{ all -> 0x0431 }
            r6 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)     // Catch:{ all -> 0x0431 }
            r5.append(r6)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1a28:
            java.lang.String r2 = "NotificationMessageSticker"
            r5 = 2131626551(0x7f0e0a37, float:1.8880341E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            r5 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r5)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1a42:
            r1 = r18
            java.lang.String r2 = "NotificationMessageDocument"
            r5 = 2131626513(0x7f0e0a11, float:1.8880264E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachDocument"
            r6 = 2131624390(0x7f0e01c6, float:1.8875958E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1a60:
            r1 = r18
            java.lang.String r2 = "NotificationMessageRound"
            r5 = 2131626546(0x7f0e0a32, float:1.8880331E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachRound"
            r6 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1a7e:
            r1 = r18
            java.lang.String r2 = "ActionTakeScreenshoot"
            r5 = 2131624164(0x7f0e00e4, float:1.88755E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r5)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "un1"
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = r2.replace(r5, r7)     // Catch:{ all -> 0x0431 }
            goto L_0x1b5b
        L_0x1a94:
            r1 = r18
            java.lang.String r2 = "NotificationMessageSDVideo"
            r5 = 2131626548(0x7f0e0a34, float:1.8880335E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachDestructingVideo"
            r6 = 2131624389(0x7f0e01c5, float:1.8875956E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1ab2:
            r1 = r18
            java.lang.String r2 = "NotificationMessageVideo"
            r5 = 2131626554(0x7f0e0a3a, float:1.8880347E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachVideo"
            r6 = 2131624411(0x7f0e01db, float:1.8876E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1acf:
            r1 = r18
            java.lang.String r2 = "NotificationMessageSDPhoto"
            r5 = 2131626547(0x7f0e0a33, float:1.8880333E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachDestructingPhoto"
            r6 = 2131624388(0x7f0e01c4, float:1.8875954E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1aec:
            r1 = r18
            java.lang.String r2 = "NotificationMessagePhoto"
            r5 = 2131626543(0x7f0e0a2f, float:1.8880325E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "AttachPhoto"
            r6 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1b09:
            r1 = r18
            java.lang.String r2 = "NotificationMessageNoText"
            r5 = 2131626542(0x7f0e0a2e, float:1.8880323E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0431 }
            r7[r6] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "Message"
            r6 = 2131626212(0x7f0e08e4, float:1.8879654E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0431 }
        L_0x1b25:
            r16 = r5
            r5 = 0
            goto L_0x1b5e
        L_0x1b29:
            r1 = r18
            java.lang.String r2 = "NotificationMessageText"
            r5 = 2131626553(0x7f0e0a39, float:1.8880345E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0431 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0431 }
            r6[r7] = r8     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0431 }
            r5 = r12[r7]     // Catch:{ all -> 0x0431 }
            goto L_0x1b25
        L_0x1b44:
            if (r2 == 0) goto L_0x1b5a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0431 }
            r2.<init>()     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "unhandled loc_key = "
            r2.append(r5)     // Catch:{ all -> 0x0431 }
            r2.append(r9)     // Catch:{ all -> 0x0431 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0431 }
            org.telegram.messenger.FileLog.w(r2)     // Catch:{ all -> 0x0431 }
        L_0x1b5a:
            r2 = 0
        L_0x1b5b:
            r5 = 0
        L_0x1b5c:
            r16 = 0
        L_0x1b5e:
            if (r2 == 0) goto L_0x1c2c
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x1c2f }
            r6.<init>()     // Catch:{ all -> 0x1c2f }
            r6.id = r1     // Catch:{ all -> 0x1c2f }
            r7 = r39
            r6.random_id = r7     // Catch:{ all -> 0x1c2f }
            if (r16 == 0) goto L_0x1b70
            r1 = r16
            goto L_0x1b71
        L_0x1b70:
            r1 = r2
        L_0x1b71:
            r6.message = r1     // Catch:{ all -> 0x1c2f }
            r7 = 1000(0x3e8, double:4.94E-321)
            long r7 = r45 / r7
            int r1 = (int) r7     // Catch:{ all -> 0x1c2f }
            r6.date = r1     // Catch:{ all -> 0x1c2f }
            if (r33 == 0) goto L_0x1b83
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r1 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x0431 }
            r1.<init>()     // Catch:{ all -> 0x0431 }
            r6.action = r1     // Catch:{ all -> 0x0431 }
        L_0x1b83:
            if (r32 == 0) goto L_0x1b8c
            int r1 = r6.flags     // Catch:{ all -> 0x0431 }
            r7 = -2147483648(0xfffffffvar_, float:-0.0)
            r1 = r1 | r7
            r6.flags = r1     // Catch:{ all -> 0x0431 }
        L_0x1b8c:
            r6.dialog_id = r3     // Catch:{ all -> 0x1c2f }
            if (r38 == 0) goto L_0x1b9e
            org.telegram.tgnet.TLRPC$TL_peerChannel r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x0431 }
            r1.<init>()     // Catch:{ all -> 0x0431 }
            r6.peer_id = r1     // Catch:{ all -> 0x0431 }
            r8 = r38
            r1.channel_id = r8     // Catch:{ all -> 0x0431 }
            r3 = r24
            goto L_0x1bb9
        L_0x1b9e:
            if (r24 == 0) goto L_0x1bac
            org.telegram.tgnet.TLRPC$TL_peerChat r1 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x0431 }
            r1.<init>()     // Catch:{ all -> 0x0431 }
            r6.peer_id = r1     // Catch:{ all -> 0x0431 }
            r3 = r24
            r1.chat_id = r3     // Catch:{ all -> 0x0431 }
            goto L_0x1bb9
        L_0x1bac:
            r3 = r24
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1c2f }
            r1.<init>()     // Catch:{ all -> 0x1c2f }
            r6.peer_id = r1     // Catch:{ all -> 0x1c2f }
            r8 = r23
            r1.user_id = r8     // Catch:{ all -> 0x1c2f }
        L_0x1bb9:
            int r1 = r6.flags     // Catch:{ all -> 0x1c2f }
            r1 = r1 | 256(0x100, float:3.59E-43)
            r6.flags = r1     // Catch:{ all -> 0x1c2f }
            if (r31 == 0) goto L_0x1bcb
            org.telegram.tgnet.TLRPC$TL_peerChat r1 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x0431 }
            r1.<init>()     // Catch:{ all -> 0x0431 }
            r6.from_id = r1     // Catch:{ all -> 0x0431 }
            r1.chat_id = r3     // Catch:{ all -> 0x0431 }
            goto L_0x1beb
        L_0x1bcb:
            if (r28 == 0) goto L_0x1bd9
            org.telegram.tgnet.TLRPC$TL_peerChannel r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x0431 }
            r1.<init>()     // Catch:{ all -> 0x0431 }
            r6.from_id = r1     // Catch:{ all -> 0x0431 }
            r3 = r28
            r1.channel_id = r3     // Catch:{ all -> 0x0431 }
            goto L_0x1beb
        L_0x1bd9:
            if (r25 == 0) goto L_0x1be7
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x0431 }
            r1.<init>()     // Catch:{ all -> 0x0431 }
            r6.from_id = r1     // Catch:{ all -> 0x0431 }
            r3 = r25
            r1.user_id = r3     // Catch:{ all -> 0x0431 }
            goto L_0x1beb
        L_0x1be7:
            org.telegram.tgnet.TLRPC$Peer r1 = r6.peer_id     // Catch:{ all -> 0x1c2f }
            r6.from_id = r1     // Catch:{ all -> 0x1c2f }
        L_0x1beb:
            if (r26 != 0) goto L_0x1bf2
            if (r33 == 0) goto L_0x1bf0
            goto L_0x1bf2
        L_0x1bf0:
            r1 = 0
            goto L_0x1bf3
        L_0x1bf2:
            r1 = 1
        L_0x1bf3:
            r6.mentioned = r1     // Catch:{ all -> 0x1c2f }
            r1 = r27
            r6.silent = r1     // Catch:{ all -> 0x1c2f }
            r13 = r22
            r6.from_scheduled = r13     // Catch:{ all -> 0x1c2f }
            org.telegram.messenger.MessageObject r1 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x1c2f }
            r19 = r1
            r20 = r30
            r21 = r6
            r22 = r2
            r23 = r34
            r24 = r37
            r25 = r5
            r26 = r36
            r27 = r32
            r28 = r35
            r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27, r28)     // Catch:{ all -> 0x1c2f }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x1c2f }
            r2.<init>()     // Catch:{ all -> 0x1c2f }
            r2.add(r1)     // Catch:{ all -> 0x1c2f }
            org.telegram.messenger.NotificationsController r1 = org.telegram.messenger.NotificationsController.getInstance(r30)     // Catch:{ all -> 0x1c2f }
            r3 = r43
            java.util.concurrent.CountDownLatch r4 = r3.countDownLatch     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            r1.processNewMessages(r2, r5, r5, r4)     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 0
            goto L_0x1CLASSNAME
        L_0x1c2c:
            r3 = r43
            goto L_0x1CLASSNAME
        L_0x1c2f:
            r0 = move-exception
            r3 = r43
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r0 = move-exception
            r3 = r43
            goto L_0x1c6d
        L_0x1CLASSNAME:
            r3 = r43
            goto L_0x1CLASSNAME
        L_0x1c3a:
            r0 = move-exception
            r3 = r1
            goto L_0x1c6d
        L_0x1c3d:
            r3 = r1
            r29 = r14
        L_0x1CLASSNAME:
            r30 = r15
        L_0x1CLASSNAME:
            r8 = 1
        L_0x1CLASSNAME:
            if (r8 == 0) goto L_0x1c4a
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1CLASSNAME }
            r1.countDown()     // Catch:{ all -> 0x1CLASSNAME }
        L_0x1c4a:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r30)     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r30)     // Catch:{ all -> 0x1CLASSNAME }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1d83
        L_0x1CLASSNAME:
            r0 = move-exception
        L_0x1CLASSNAME:
            r1 = r0
            r4 = r9
            r14 = r29
            r15 = r30
            goto L_0x1d2e
        L_0x1c5f:
            r0 = move-exception
            r3 = r1
            r29 = r14
            r30 = r15
            r1 = r0
            r4 = r9
            goto L_0x1d2e
        L_0x1CLASSNAME:
            r0 = move-exception
            r3 = r1
            r29 = r7
        L_0x1c6d:
            r30 = r15
            goto L_0x1d2a
        L_0x1CLASSNAME:
            r3 = r1
            r29 = r7
            r30 = r15
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1 r2 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda1     // Catch:{ all -> 0x1CLASSNAME }
            r15 = r30
            r2.<init>(r15)     // Catch:{ all -> 0x1d24 }
            r1.postRunnable(r2)     // Catch:{ all -> 0x1d24 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d24 }
            r1.countDown()     // Catch:{ all -> 0x1d24 }
            return
        L_0x1CLASSNAME:
            r0 = move-exception
            r15 = r30
            goto L_0x1d2a
        L_0x1c8d:
            r3 = r1
            r29 = r7
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0 r1 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda0     // Catch:{ all -> 0x1d24 }
            r1.<init>(r15)     // Catch:{ all -> 0x1d24 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ all -> 0x1d24 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d24 }
            r1.countDown()     // Catch:{ all -> 0x1d24 }
            return
        L_0x1c9e:
            r3 = r1
            r29 = r7
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x1d24 }
            r1.<init>()     // Catch:{ all -> 0x1d24 }
            r2 = 0
            r1.popup = r2     // Catch:{ all -> 0x1d24 }
            r2 = 2
            r1.flags = r2     // Catch:{ all -> 0x1d24 }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r6 = r45 / r6
            int r2 = (int) r6     // Catch:{ all -> 0x1d24 }
            r1.inbox_date = r2     // Catch:{ all -> 0x1d24 }
            java.lang.String r2 = "message"
            java.lang.String r2 = r5.getString(r2)     // Catch:{ all -> 0x1d24 }
            r1.message = r2     // Catch:{ all -> 0x1d24 }
            java.lang.String r2 = "announcement"
            r1.type = r2     // Catch:{ all -> 0x1d24 }
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ all -> 0x1d24 }
            r2.<init>()     // Catch:{ all -> 0x1d24 }
            r1.media = r2     // Catch:{ all -> 0x1d24 }
            org.telegram.tgnet.TLRPC$TL_updates r2 = new org.telegram.tgnet.TLRPC$TL_updates     // Catch:{ all -> 0x1d24 }
            r2.<init>()     // Catch:{ all -> 0x1d24 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r4 = r2.updates     // Catch:{ all -> 0x1d24 }
            r4.add(r1)     // Catch:{ all -> 0x1d24 }
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1d24 }
            org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3 r4 = new org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda3     // Catch:{ all -> 0x1d24 }
            r4.<init>(r15, r2)     // Catch:{ all -> 0x1d24 }
            r1.postRunnable(r4)     // Catch:{ all -> 0x1d24 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x1d24 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1d24 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d24 }
            r1.countDown()     // Catch:{ all -> 0x1d24 }
            return
        L_0x1ce7:
            r3 = r1
            r29 = r7
            java.lang.String r1 = "dc"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x1d24 }
            java.lang.String r2 = "addr"
            java.lang.String r2 = r11.getString(r2)     // Catch:{ all -> 0x1d24 }
            java.lang.String r4 = ":"
            java.lang.String[] r2 = r2.split(r4)     // Catch:{ all -> 0x1d24 }
            int r4 = r2.length     // Catch:{ all -> 0x1d24 }
            r5 = 2
            if (r4 == r5) goto L_0x1d06
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d24 }
            r1.countDown()     // Catch:{ all -> 0x1d24 }
            return
        L_0x1d06:
            r4 = 0
            r4 = r2[r4]     // Catch:{ all -> 0x1d24 }
            r5 = 1
            r2 = r2[r5]     // Catch:{ all -> 0x1d24 }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ all -> 0x1d24 }
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x1d24 }
            r5.applyDatacenterAddress(r1, r4, r2)     // Catch:{ all -> 0x1d24 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x1d24 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1d24 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d24 }
            r1.countDown()     // Catch:{ all -> 0x1d24 }
            return
        L_0x1d24:
            r0 = move-exception
            goto L_0x1d2a
        L_0x1d26:
            r0 = move-exception
            r3 = r1
            r29 = r7
        L_0x1d2a:
            r1 = r0
            r4 = r9
            r14 = r29
        L_0x1d2e:
            r2 = -1
            goto L_0x1d4b
        L_0x1d30:
            r0 = move-exception
            r3 = r1
            r29 = r7
            r1 = r0
            r4 = r9
            r14 = r29
            r2 = -1
            goto L_0x1d4a
        L_0x1d3a:
            r0 = move-exception
            r3 = r1
            r29 = r7
            r1 = r0
            r14 = r29
            r2 = -1
            r4 = 0
            goto L_0x1d4a
        L_0x1d44:
            r0 = move-exception
            r3 = r1
            r1 = r0
            r2 = -1
            r4 = 0
            r14 = 0
        L_0x1d4a:
            r15 = -1
        L_0x1d4b:
            if (r15 == r2) goto L_0x1d5d
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15)
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)
            r2.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r2 = r3.countDownLatch
            r2.countDown()
            goto L_0x1d60
        L_0x1d5d:
            r43.onDecryptError()
        L_0x1d60:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x1d80
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "error in loc_key = "
            r2.append(r5)
            r2.append(r4)
            java.lang.String r4 = " json "
            r2.append(r4)
            r2.append(r14)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r2)
        L_0x1d80:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1d83:
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
