package org.telegram.messenger;

import android.os.SystemClock;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.ConnectionsManager;

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
        AndroidUtilities.runOnUIThread(new Runnable(data, sentTime) {
            public final /* synthetic */ Map f$1;
            public final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                GcmPushListenerService.this.lambda$onMessageReceived$4$GcmPushListenerService(this.f$1, this.f$2);
            }
        });
        try {
            this.countDownLatch.await();
        } catch (Throwable unused) {
        }
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("finished GCM service, time = " + (SystemClock.elapsedRealtime() - elapsedRealtime));
        }
    }

    public /* synthetic */ void lambda$onMessageReceived$4$GcmPushListenerService(Map map, long j) {
        ApplicationLoader.postInitApplication();
        Utilities.stageQueue.postRunnable(new Runnable(map, j) {
            public final /* synthetic */ Map f$1;
            public final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                GcmPushListenerService.this.lambda$null$3$GcmPushListenerService(this.f$1, this.f$2);
            }
        });
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v28, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v9, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v10, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v11, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v33, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v67, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v45, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v46, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v47, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v43, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v47, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v48, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v14, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v126, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v78, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v79, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v82, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v148, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v149, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v86, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v167, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v171, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v167, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v82, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v177, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v170, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v173, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v185, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v189, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v195, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v198, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v201, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v101, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v204, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v207, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v210, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v200, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v108, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v218, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v208, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v210, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v212, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v214, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v216, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v226, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v134, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v135, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v228, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v141, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v34, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v148, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v149, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v153, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v154, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v167, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v155, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v170, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v158, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v173, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v166, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v167, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v169, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v170, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v272, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v172, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v173, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v174, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v279, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v263, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v175, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v177, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v178, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v269, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v181, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v273, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v275, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v190, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v277, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v279, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v283, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v207, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v287, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v210, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v291, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v213, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v295, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v216, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v299, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v6, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v7, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v243, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v244, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v245, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v250, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x01df, code lost:
        if (r2 == 0) goto L_0x18a4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x01e1, code lost:
        if (r2 == 1) goto L_0x185b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x01e3, code lost:
        if (r2 == 2) goto L_0x1847;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x01e5, code lost:
        if (r2 == 3) goto L_0x1823;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x01ef, code lost:
        if (r11.has("channel_id") == false) goto L_0x01fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:?, code lost:
        r2 = r11.getInt("channel_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x01f7, code lost:
        r3 = (long) (-r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x01fa, code lost:
        r3 = 0;
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x0203, code lost:
        if (r11.has("from_id") == false) goto L_0x0217;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:?, code lost:
        r3 = r11.getInt("from_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x020b, code lost:
        r14 = r7;
        r6 = r3;
        r3 = (long) r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x0213, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x0214, code lost:
        r14 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0215, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x0217, code lost:
        r14 = r7;
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:0x021f, code lost:
        if (r11.has("chat_id") == false) goto L_0x0231;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:?, code lost:
        r3 = r11.getInt("chat_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x0227, code lost:
        r12 = r3;
        r3 = (long) (-r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x022f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x0231, code lost:
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:159:0x0238, code lost:
        if (r11.has("encryption_id") == false) goto L_0x0244;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:163:0x0240, code lost:
        r3 = ((long) r11.getInt("encryption_id")) << 32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:167:0x024a, code lost:
        if (r11.has("schedule") == false) goto L_0x0256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x0252, code lost:
        if (r11.getInt("schedule") != 1) goto L_0x0256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x0254, code lost:
        r13 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x0256, code lost:
        r13 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:175:0x0259, code lost:
        if (r3 != 0) goto L_0x0268;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:177:0x0261, code lost:
        if ("ENCRYPTED_MESSAGE".equals(r9) == false) goto L_0x0268;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x0263, code lost:
        r3 = -4294967296L;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x026a, code lost:
        if (r3 == 0) goto L_0x17ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:0x0274, code lost:
        if ("READ_HISTORY".equals(r9) == false) goto L_0x02e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:?, code lost:
        r5 = r11.getInt("max_id");
        r7 = new java.util.ArrayList();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x0283, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x029f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x0285, code lost:
        org.telegram.messenger.FileLog.d("GCM received read notification max_id = " + r5 + " for dialogId = " + r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x029f, code lost:
        if (r2 == 0) goto L_0x02ae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x02a1, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox();
        r3.channel_id = r2;
        r3.max_id = r5;
        r7.add(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x02ae, code lost:
        r2 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x02b3, code lost:
        if (r6 == 0) goto L_0x02bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x02b5, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r2.peer = r3;
        r3.user_id = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x02bf, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r2.peer = r3;
        r3.chat_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x02c8, code lost:
        r2.max_id = r5;
        r7.add(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x02cd, code lost:
        org.telegram.messenger.MessagesController.getInstance(r15).processUpdateArray(r7, (java.util.ArrayList<org.telegram.tgnet.TLRPC$User>) null, (java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat>) null, false, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x02e8, code lost:
        if ("MESSAGE_DELETED".equals(r9) == false) goto L_0x034d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:?, code lost:
        r5 = r11.getString("messages").split(",");
        r6 = new android.util.SparseArray();
        r7 = new java.util.ArrayList();
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x0300, code lost:
        if (r8 >= r5.length) goto L_0x030e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x0302, code lost:
        r7.add(org.telegram.messenger.Utilities.parseInt(r5[r8]));
        r8 = r8 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x030e, code lost:
        r6.put(r2, r7);
        org.telegram.messenger.NotificationsController.getInstance(r15).removeDeletedMessagesFromNotifications(r6);
        org.telegram.messenger.MessagesController.getInstance(r15).deleteMessagesByPush(r3, r7, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x0321, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x17ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x0323, code lost:
        org.telegram.messenger.FileLog.d("GCM received " + r9 + " for dialogId = " + r3 + " mids = " + android.text.TextUtils.join(",", r7));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x0351, code lost:
        if (android.text.TextUtils.isEmpty(r9) != false) goto L_0x17ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x0359, code lost:
        if (r11.has("msg_id") == false) goto L_0x0364;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:?, code lost:
        r7 = r11.getInt("msg_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:220:0x0361, code lost:
        r28 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x0364, code lost:
        r28 = r14;
        r7 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x0369, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:225:0x036d, code lost:
        if (r11.has("random_id") == false) goto L_0x038b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x037d, code lost:
        r22 = r13;
        r13 = org.telegram.messenger.Utilities.parseLong(r11.getString("random_id")).longValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x0384, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x0385, code lost:
        r3 = r1;
        r4 = r9;
        r14 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x038b, code lost:
        r22 = r13;
        r13 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x038f, code lost:
        if (r7 == 0) goto L_0x03d5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:?, code lost:
        r23 = r6;
        r1 = (java.lang.Integer) org.telegram.messenger.MessagesController.getInstance(r15).dialogs_read_inbox_max.get(java.lang.Long.valueOf(r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x03a3, code lost:
        if (r1 != null) goto L_0x03c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x03a5, code lost:
        r1 = java.lang.Integer.valueOf(org.telegram.messenger.MessagesStorage.getInstance(r15).getDialogReadMax(false, r3));
        r24 = r12;
        org.telegram.messenger.MessagesController.getInstance(r15).dialogs_read_inbox_max.put(java.lang.Long.valueOf(r3), r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x03c2, code lost:
        r24 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x03c8, code lost:
        if (r7 <= r1.intValue()) goto L_0x03e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x03cb, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x03cc, code lost:
        r2 = -1;
        r3 = r40;
        r1 = r0;
        r4 = r9;
        r14 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x03d5, code lost:
        r23 = r6;
        r24 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x03db, code lost:
        if (r13 == 0) goto L_0x03e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x03e5, code lost:
        if (org.telegram.messenger.MessagesStorage.getInstance(r15).checkMessageByRandomId(r13) != false) goto L_0x03e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x03e7, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x03e9, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x03ea, code lost:
        if (r1 == false) goto L_0x17e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x03ee, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x03f2, code lost:
        if (r11.has("chat_from_id") == false) goto L_0x03fb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:?, code lost:
        r1 = r11.getInt("chat_from_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x03fb, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x0402, code lost:
        if (r11.has("mention") == false) goto L_0x040e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x040a, code lost:
        if (r11.getInt("mention") == 0) goto L_0x040e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x040c, code lost:
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x040e, code lost:
        r6 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x0415, code lost:
        if (r11.has("silent") == false) goto L_0x0423;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x041d, code lost:
        if (r11.getInt("silent") == 0) goto L_0x0423;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:277:0x041f, code lost:
        r29 = r15;
        r12 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0423, code lost:
        r29 = r15;
        r12 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0428, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x042c, code lost:
        if (r5.has("loc_args") == false) goto L_0x0458;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:?, code lost:
        r5 = r5.getJSONArray("loc_args");
        r15 = r5.length();
        r19 = r12;
        r12 = new java.lang.String[r15];
        r20 = r6;
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x043f, code lost:
        if (r6 >= r15) goto L_0x044a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x0441, code lost:
        r12[r6] = r5.getString(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x0447, code lost:
        r6 = r6 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x044a, code lost:
        r5 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x044c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x044d, code lost:
        r2 = -1;
        r3 = r40;
        r1 = r0;
        r4 = r9;
        r14 = r28;
        r15 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x0458, code lost:
        r20 = r6;
        r19 = r12;
        r5 = 0;
        r12 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:?, code lost:
        r6 = r12[r5];
        r27 = r11.has("edit_date");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x046c, code lost:
        if (r9.startsWith("CHAT_") == false) goto L_0x0480;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x046e, code lost:
        if (r2 == 0) goto L_0x0472;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x0470, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x0472, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x0476, code lost:
        r11 = false;
        r26 = false;
        r15 = r5;
        r5 = r6;
        r6 = r12[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x0486, code lost:
        if (r9.startsWith("PINNED_") == false) goto L_0x0491;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x0488, code lost:
        if (r1 == 0) goto L_0x048c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x048a, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x048c, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x048d, code lost:
        r15 = r5;
        r5 = null;
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x0497, code lost:
        if (r9.startsWith("CHANNEL_") == false) goto L_0x049f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x0499, code lost:
        r5 = null;
        r11 = false;
        r15 = false;
        r26 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x049f, code lost:
        r5 = null;
        r11 = false;
        r15 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x04a2, code lost:
        r26 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x04a6, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x04cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x04a8, code lost:
        r25 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:?, code lost:
        r6 = new java.lang.StringBuilder();
        r30 = r5;
        r6.append("GCM received message notification ");
        r6.append(r9);
        r6.append(" for dialogId = ");
        r6.append(r3);
        r6.append(" mid = ");
        r6.append(r7);
        org.telegram.messenger.FileLog.d(r6.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x04cf, code lost:
        r30 = r5;
        r25 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x04d7, code lost:
        switch(r9.hashCode()) {
            case -2100047043: goto L_0x0988;
            case -2091498420: goto L_0x097d;
            case -2053872415: goto L_0x0972;
            case -2039746363: goto L_0x0967;
            case -2023218804: goto L_0x095c;
            case -1979538588: goto L_0x0951;
            case -1979536003: goto L_0x0946;
            case -1979535888: goto L_0x093b;
            case -1969004705: goto L_0x0930;
            case -1946699248: goto L_0x0924;
            case -1528047021: goto L_0x0918;
            case -1493579426: goto L_0x090c;
            case -1482481933: goto L_0x0900;
            case -1480102982: goto L_0x08f5;
            case -1478041834: goto L_0x08e9;
            case -1474543101: goto L_0x08de;
            case -1465695932: goto L_0x08d2;
            case -1374906292: goto L_0x08c6;
            case -1372940586: goto L_0x08ba;
            case -1264245338: goto L_0x08ae;
            case -1236086700: goto L_0x08a2;
            case -1236077786: goto L_0x0896;
            case -1235796237: goto L_0x088a;
            case -1235760759: goto L_0x087e;
            case -1235686303: goto L_0x0873;
            case -1198046100: goto L_0x0868;
            case -1124254527: goto L_0x085c;
            case -1085137927: goto L_0x0850;
            case -1084856378: goto L_0x0844;
            case -1084820900: goto L_0x0838;
            case -1084746444: goto L_0x082c;
            case -819729482: goto L_0x0820;
            case -772141857: goto L_0x0814;
            case -638310039: goto L_0x0808;
            case -590403924: goto L_0x07fc;
            case -589196239: goto L_0x07f0;
            case -589193654: goto L_0x07e4;
            case -589193539: goto L_0x07d8;
            case -440169325: goto L_0x07cc;
            case -412748110: goto L_0x07c0;
            case -228518075: goto L_0x07b4;
            case -213586509: goto L_0x07a8;
            case -115582002: goto L_0x079c;
            case -112621464: goto L_0x0790;
            case -108522133: goto L_0x0784;
            case -107572034: goto L_0x0779;
            case -40534265: goto L_0x076d;
            case 65254746: goto L_0x0761;
            case 141040782: goto L_0x0755;
            case 309993049: goto L_0x0749;
            case 309995634: goto L_0x073d;
            case 309995749: goto L_0x0731;
            case 320532812: goto L_0x0725;
            case 328933854: goto L_0x0719;
            case 331340546: goto L_0x070d;
            case 344816990: goto L_0x0701;
            case 346878138: goto L_0x06f5;
            case 350376871: goto L_0x06e9;
            case 615714517: goto L_0x06de;
            case 715508879: goto L_0x06d2;
            case 728985323: goto L_0x06c6;
            case 731046471: goto L_0x06ba;
            case 734545204: goto L_0x06ae;
            case 802032552: goto L_0x06a2;
            case 991498806: goto L_0x0696;
            case 1007364121: goto L_0x068a;
            case 1019917311: goto L_0x067e;
            case 1019926225: goto L_0x0672;
            case 1020207774: goto L_0x0666;
            case 1020243252: goto L_0x065a;
            case 1020317708: goto L_0x064e;
            case 1060349560: goto L_0x0642;
            case 1060358474: goto L_0x0636;
            case 1060640023: goto L_0x062a;
            case 1060675501: goto L_0x061e;
            case 1060749957: goto L_0x0613;
            case 1073049781: goto L_0x0607;
            case 1078101399: goto L_0x05fb;
            case 1110103437: goto L_0x05ef;
            case 1160762272: goto L_0x05e3;
            case 1172918249: goto L_0x05d7;
            case 1234591620: goto L_0x05cb;
            case 1281128640: goto L_0x05bf;
            case 1281131225: goto L_0x05b3;
            case 1281131340: goto L_0x05a7;
            case 1310789062: goto L_0x059c;
            case 1333118583: goto L_0x0590;
            case 1361447897: goto L_0x0584;
            case 1498266155: goto L_0x0578;
            case 1533804208: goto L_0x056c;
            case 1547988151: goto L_0x0560;
            case 1561464595: goto L_0x0554;
            case 1563525743: goto L_0x0548;
            case 1567024476: goto L_0x053c;
            case 1810705077: goto L_0x0530;
            case 1815177512: goto L_0x0524;
            case 1963241394: goto L_0x0518;
            case 2014789757: goto L_0x050c;
            case 2022049433: goto L_0x0500;
            case 2048733346: goto L_0x04f4;
            case 2099392181: goto L_0x04e8;
            case 2140162142: goto L_0x04dc;
            default: goto L_0x04da;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x04e2, code lost:
        if (r9.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x04e4, code lost:
        r6 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x04ee, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x04f0, code lost:
        r6 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x04fa, code lost:
        if (r9.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x04fc, code lost:
        r6 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x0506, code lost:
        if (r9.equals("PINNED_CONTACT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x0508, code lost:
        r6 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x0512, code lost:
        if (r9.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x0514, code lost:
        r6 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x051e, code lost:
        if (r9.equals("LOCKED_MESSAGE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x0520, code lost:
        r6 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x052a, code lost:
        if (r9.equals("CHANNEL_MESSAGES") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x052c, code lost:
        r6 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x0536, code lost:
        if (r9.equals("MESSAGE_INVOICE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x0538, code lost:
        r6 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x0542, code lost:
        if (r9.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x0544, code lost:
        r6 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x054e, code lost:
        if (r9.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x0550, code lost:
        r6 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x055a, code lost:
        if (r9.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x055c, code lost:
        r6 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x0566, code lost:
        if (r9.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x0568, code lost:
        r6 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x0572, code lost:
        if (r9.equals("MESSAGE_VIDEOS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x0574, code lost:
        r6 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x057e, code lost:
        if (r9.equals("PHONE_CALL_MISSED") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x0580, code lost:
        r6 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x058a, code lost:
        if (r9.equals("MESSAGE_PHOTOS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x058c, code lost:
        r6 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x0596, code lost:
        if (r9.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x0598, code lost:
        r6 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x05a2, code lost:
        if (r9.equals("MESSAGE_NOTEXT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x05a4, code lost:
        r6 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x05ad, code lost:
        if (r9.equals("MESSAGE_GIF") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x05af, code lost:
        r6 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x05b9, code lost:
        if (r9.equals("MESSAGE_GEO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x05bb, code lost:
        r6 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x05c5, code lost:
        if (r9.equals("MESSAGE_DOC") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x05c7, code lost:
        r6 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x05d1, code lost:
        if (r9.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x05d3, code lost:
        r6 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x05dd, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x05df, code lost:
        r6 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x05e9, code lost:
        if (r9.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x05eb, code lost:
        r6 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x05f5, code lost:
        if (r9.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x05f7, code lost:
        r6 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:400:0x0601, code lost:
        if (r9.equals("CHAT_TITLE_EDITED") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x0603, code lost:
        r6 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x060d, code lost:
        if (r9.equals("PINNED_NOTEXT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x060f, code lost:
        r6 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x0619, code lost:
        if (r9.equals("MESSAGE_TEXT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x061b, code lost:
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x0624, code lost:
        if (r9.equals("MESSAGE_QUIZ") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x0626, code lost:
        r6 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0630, code lost:
        if (r9.equals("MESSAGE_POLL") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x0632, code lost:
        r6 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x063c, code lost:
        if (r9.equals("MESSAGE_GAME") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x063e, code lost:
        r6 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x0648, code lost:
        if (r9.equals("MESSAGE_FWDS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x064a, code lost:
        r6 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x0654, code lost:
        if (r9.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x0656, code lost:
        r6 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x0660, code lost:
        if (r9.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x0662, code lost:
        r6 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x066c, code lost:
        if (r9.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x066e, code lost:
        r6 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x0678, code lost:
        if (r9.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x067a, code lost:
        r6 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x0684, code lost:
        if (r9.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x0686, code lost:
        r6 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x0690, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x0692, code lost:
        r6 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x069c, code lost:
        if (r9.equals("PINNED_GEOLIVE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x069e, code lost:
        r6 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x06a8, code lost:
        if (r9.equals("MESSAGE_CONTACT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x06aa, code lost:
        r6 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x06b4, code lost:
        if (r9.equals("PINNED_VIDEO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x06b6, code lost:
        r6 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x06c0, code lost:
        if (r9.equals("PINNED_ROUND") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x06c2, code lost:
        r6 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x06cc, code lost:
        if (r9.equals("PINNED_PHOTO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x06ce, code lost:
        r6 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x06d8, code lost:
        if (r9.equals("PINNED_AUDIO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x06da, code lost:
        r6 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x06e4, code lost:
        if (r9.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:458:0x06e6, code lost:
        r6 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:460:0x06ef, code lost:
        if (r9.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x06f1, code lost:
        r6 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x06fb, code lost:
        if (r9.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x06fd, code lost:
        r6 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x0707, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x0709, code lost:
        r6 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x0713, code lost:
        if (r9.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x0715, code lost:
        r6 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:472:0x071f, code lost:
        if (r9.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0721, code lost:
        r6 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x072b, code lost:
        if (r9.equals("MESSAGES") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x072d, code lost:
        r6 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x0737, code lost:
        if (r9.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0739, code lost:
        r6 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x0743, code lost:
        if (r9.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x0745, code lost:
        r6 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x074f, code lost:
        if (r9.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x0751, code lost:
        r6 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x075b, code lost:
        if (r9.equals("CHAT_LEFT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x075d, code lost:
        r6 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x0767, code lost:
        if (r9.equals("CHAT_ADD_YOU") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0769, code lost:
        r6 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x0773, code lost:
        if (r9.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x0775, code lost:
        r6 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x077f, code lost:
        if (r9.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x0781, code lost:
        r6 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x078a, code lost:
        if (r9.equals("AUTH_REGION") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x078c, code lost:
        r6 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x0796, code lost:
        if (r9.equals("CONTACT_JOINED") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x0798, code lost:
        r6 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x07a2, code lost:
        if (r9.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x07a4, code lost:
        r6 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x07ae, code lost:
        if (r9.equals("ENCRYPTION_REQUEST") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x07b0, code lost:
        r6 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x07ba, code lost:
        if (r9.equals("MESSAGE_GEOLIVE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x07bc, code lost:
        r6 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x07c6, code lost:
        if (r9.equals("CHAT_DELETE_YOU") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x07c8, code lost:
        r6 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x07d2, code lost:
        if (r9.equals("AUTH_UNKNOWN") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x07d4, code lost:
        r6 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x07de, code lost:
        if (r9.equals("PINNED_GIF") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x07e0, code lost:
        r6 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x07ea, code lost:
        if (r9.equals("PINNED_GEO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x07ec, code lost:
        r6 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x07f6, code lost:
        if (r9.equals("PINNED_DOC") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x07f8, code lost:
        r6 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0802, code lost:
        if (r9.equals("PINNED_GAME_SCORE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x0804, code lost:
        r6 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x080e, code lost:
        if (r9.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0810, code lost:
        r6 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x081a, code lost:
        if (r9.equals("PHONE_CALL_REQUEST") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x081c, code lost:
        r6 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:538:0x0826, code lost:
        if (r9.equals("PINNED_STICKER") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x0828, code lost:
        r6 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0832, code lost:
        if (r9.equals("PINNED_TEXT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x0834, code lost:
        r6 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x083e, code lost:
        if (r9.equals("PINNED_QUIZ") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0840, code lost:
        r6 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x084a, code lost:
        if (r9.equals("PINNED_POLL") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x084c, code lost:
        r6 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x0856, code lost:
        if (r9.equals("PINNED_GAME") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x0858, code lost:
        r6 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x0862, code lost:
        if (r9.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x0864, code lost:
        r6 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x086e, code lost:
        if (r9.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x0870, code lost:
        r6 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x0879, code lost:
        if (r9.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x087b, code lost:
        r6 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x0884, code lost:
        if (r9.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x0886, code lost:
        r6 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x0890, code lost:
        if (r9.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x0892, code lost:
        r6 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x089c, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x089e, code lost:
        r6 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x08a8, code lost:
        if (r9.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x08aa, code lost:
        r6 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x08b4, code lost:
        if (r9.equals("PINNED_INVOICE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x08b6, code lost:
        r6 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x08c0, code lost:
        if (r9.equals("CHAT_RETURNED") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x08c2, code lost:
        r6 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x08cc, code lost:
        if (r9.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x08ce, code lost:
        r6 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x08d8, code lost:
        if (r9.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x08da, code lost:
        r6 = 'b';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x08e4, code lost:
        if (r9.equals("MESSAGE_VIDEO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x08e6, code lost:
        r6 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x08ef, code lost:
        if (r9.equals("MESSAGE_ROUND") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x08f1, code lost:
        r6 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x08fb, code lost:
        if (r9.equals("MESSAGE_PHOTO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x08fd, code lost:
        r6 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x0906, code lost:
        if (r9.equals("MESSAGE_MUTED") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x0908, code lost:
        r6 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x0912, code lost:
        if (r9.equals("MESSAGE_AUDIO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0914, code lost:
        r6 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x091e, code lost:
        if (r9.equals("CHAT_MESSAGES") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0920, code lost:
        r6 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x092a, code lost:
        if (r9.equals("CHAT_JOINED") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x092c, code lost:
        r6 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x0936, code lost:
        if (r9.equals("CHAT_ADD_MEMBER") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x0938, code lost:
        r6 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x0941, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x0943, code lost:
        r6 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x094c, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x094e, code lost:
        r6 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x0957, code lost:
        if (r9.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x0959, code lost:
        r6 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x0962, code lost:
        if (r9.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0964, code lost:
        r6 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x096d, code lost:
        if (r9.equals("MESSAGE_STICKER") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x096f, code lost:
        r6 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:625:0x0978, code lost:
        if (r9.equals("CHAT_CREATED") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x097a, code lost:
        r6 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x0983, code lost:
        if (r9.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x0985, code lost:
        r6 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x098e, code lost:
        if (r9.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x0990, code lost:
        r6 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x0993, code lost:
        r6 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x0994, code lost:
        r18 = r7;
        r31 = r2;
        r32 = r3;
        r34 = r15;
        r35 = r11;
        r36 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x09b4, code lost:
        switch(r6) {
            case 0: goto L_0x1710;
            case 1: goto L_0x1710;
            case 2: goto L_0x16ef;
            case 3: goto L_0x16d4;
            case 4: goto L_0x16b7;
            case 5: goto L_0x169c;
            case 6: goto L_0x167f;
            case 7: goto L_0x1669;
            case 8: goto L_0x164d;
            case 9: goto L_0x1631;
            case 10: goto L_0x15d6;
            case 11: goto L_0x15b8;
            case 12: goto L_0x1595;
            case 13: goto L_0x1572;
            case 14: goto L_0x154f;
            case 15: goto L_0x1531;
            case 16: goto L_0x1513;
            case 17: goto L_0x14f5;
            case 18: goto L_0x14d2;
            case 19: goto L_0x14b3;
            case 20: goto L_0x14b3;
            case 21: goto L_0x1490;
            case 22: goto L_0x146a;
            case 23: goto L_0x1444;
            case 24: goto L_0x141e;
            case 25: goto L_0x1409;
            case 26: goto L_0x13ed;
            case 27: goto L_0x13d1;
            case 28: goto L_0x13b5;
            case 29: goto L_0x1399;
            case 30: goto L_0x137d;
            case 31: goto L_0x1322;
            case 32: goto L_0x1304;
            case 33: goto L_0x12e1;
            case 34: goto L_0x12be;
            case 35: goto L_0x129b;
            case 36: goto L_0x127d;
            case 37: goto L_0x125f;
            case 38: goto L_0x1241;
            case 39: goto L_0x1223;
            case 40: goto L_0x11f9;
            case 41: goto L_0x11d5;
            case 42: goto L_0x11b1;
            case 43: goto L_0x119c;
            case 44: goto L_0x117b;
            case 45: goto L_0x115a;
            case 46: goto L_0x1139;
            case 47: goto L_0x1118;
            case 48: goto L_0x10f7;
            case 49: goto L_0x10d6;
            case 50: goto L_0x105d;
            case 51: goto L_0x103a;
            case 52: goto L_0x1012;
            case 53: goto L_0x0fea;
            case 54: goto L_0x0fc2;
            case 55: goto L_0x0f9f;
            case 56: goto L_0x0f7c;
            case 57: goto L_0x0var_;
            case 58: goto L_0x0var_;
            case 59: goto L_0x0f0d;
            case 60: goto L_0x0ee5;
            case 61: goto L_0x0ecb;
            case 62: goto L_0x0ecb;
            case 63: goto L_0x0eb1;
            case 64: goto L_0x0e97;
            case 65: goto L_0x0e78;
            case 66: goto L_0x0e5e;
            case 67: goto L_0x0e44;
            case 68: goto L_0x0e2a;
            case 69: goto L_0x0e10;
            case 70: goto L_0x0df6;
            case 71: goto L_0x0dcb;
            case 72: goto L_0x0da0;
            case 73: goto L_0x0d75;
            case 74: goto L_0x0d5b;
            case 75: goto L_0x0d22;
            case 76: goto L_0x0cf3;
            case 77: goto L_0x0cc4;
            case 78: goto L_0x0CLASSNAME;
            case 79: goto L_0x0CLASSNAME;
            case 80: goto L_0x0CLASSNAME;
            case 81: goto L_0x0bbb;
            case 82: goto L_0x0b8c;
            case 83: goto L_0x0b53;
            case 84: goto L_0x0b1a;
            case 85: goto L_0x0ae5;
            case 86: goto L_0x0ab7;
            case 87: goto L_0x0a8c;
            case 88: goto L_0x0a61;
            case 89: goto L_0x0a34;
            case 90: goto L_0x0a07;
            case 91: goto L_0x09da;
            case 92: goto L_0x09c1;
            case 93: goto L_0x09bd;
            case 94: goto L_0x09bd;
            case 95: goto L_0x09bd;
            case 96: goto L_0x09bd;
            case 97: goto L_0x09bd;
            case 98: goto L_0x09bd;
            case 99: goto L_0x09bd;
            case 100: goto L_0x09bd;
            case 101: goto L_0x09bd;
            default: goto L_0x09b7;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x09b7, code lost:
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x09bd, code lost:
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:?, code lost:
        r2 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
        r25 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x09d7, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x09da, code lost:
        if (r1 == 0) goto L_0x09f4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x09dc, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x09f4, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x0a07, code lost:
        if (r1 == 0) goto L_0x0a21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x0a09, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0a21, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0a34, code lost:
        if (r1 == 0) goto L_0x0a4e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x0a36, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0a4e, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0a61, code lost:
        if (r1 == 0) goto L_0x0a7a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0a63, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0a7a, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0a8c, code lost:
        if (r1 == 0) goto L_0x0aa5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0a8e, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0aa5, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0ab7, code lost:
        if (r1 == 0) goto L_0x0ad0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0ab9, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x0ad0, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0ae1, code lost:
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x0ae5, code lost:
        if (r1 == 0) goto L_0x0b03;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0ae7, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x0b03, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0b1a, code lost:
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x0b1c, code lost:
        if (r1 == 0) goto L_0x0b3b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0b1e, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0b3b, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x0b53, code lost:
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:671:0x0b55, code lost:
        if (r1 == 0) goto L_0x0b74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0b57, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:673:0x0b74, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0b8c, code lost:
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0b8e, code lost:
        if (r1 == 0) goto L_0x0ba8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0b90, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x0ba8, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0bbb, code lost:
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:679:0x0bbd, code lost:
        if (r1 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0bc1, code lost:
        if (r12.length <= 2) goto L_0x0be8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0bc9, code lost:
        if (android.text.TextUtils.isEmpty(r12[2]) != false) goto L_0x0be8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0bcb, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:685:0x0be8, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x0CLASSNAME, code lost:
        if (r12.length <= 1) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0c0a, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0c0c, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x0CLASSNAME, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0CLASSNAME, code lost:
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0CLASSNAME, code lost:
        if (r1 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0c3b, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0CLASSNAME, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0CLASSNAME, code lost:
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0CLASSNAME, code lost:
        if (r1 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0c6a, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0CLASSNAME, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0CLASSNAME, code lost:
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0CLASSNAME, code lost:
        if (r1 == 0) goto L_0x0cb1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0CLASSNAME, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0cb1, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0cc4, code lost:
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0cc6, code lost:
        if (r1 == 0) goto L_0x0ce0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0cc8, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0ce0, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0cf3, code lost:
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0cf5, code lost:
        if (r1 == 0) goto L_0x0d0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0cf7, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0d0f, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0d22, code lost:
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0d24, code lost:
        if (r1 == 0) goto L_0x0d43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0d26, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0d43, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0d5b, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0d75, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0da0, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0dcb, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0df6, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:0x0e10, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0e2a, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0e44, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0e5e, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0e78, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0e97, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:727:0x0eb1, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0ecb, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0ee5, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r12[0], r12[1], r12[2]);
        r2 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0f0d, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r12[0], r12[1], r12[2], r12[3]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x0var_, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r12[0], r12[1], r12[2]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0var_, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0f7c, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0f9f, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x0fc2, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r12[0], r12[1], r12[2]);
        r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0fea, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r12[0], r12[1], r12[2]);
        r2 = org.telegram.messenger.LocaleController.getString("PollQuiz", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x1012, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r12[0], r12[1], r12[2]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x103a, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x105d, code lost:
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x1061, code lost:
        if (r12.length <= 2) goto L_0x10a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x1069, code lost:
        if (android.text.TextUtils.isEmpty(r12[2]) != false) goto L_0x10a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x106b, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r12[0], r12[1], r12[2]);
        r2 = r12[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x10a3, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r12[0], r12[1]);
        r2 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x10d6, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x10f7, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x1118, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x1139, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x115a, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x117b, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r12[0], r12[1], r12[2]);
        r2 = r12[2];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x119c, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x11b1, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:0x11d5, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x11f9, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x1223, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x1241, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x125f, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x127d, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x129b, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x12be, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x12e1, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x1304, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x1322, code lost:
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x1326, code lost:
        if (r12.length <= 1) goto L_0x1363;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x132e, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x1363;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x1330, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r12[0], r12[1]);
        r2 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x1363, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x137d, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x1399, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x13b5, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x13d1, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x13ed, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x1409, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x141e, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x1444, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x146a, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x1490, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x14b3, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x14d2, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x14f5, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x1513, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x1531, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x154f, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x1572, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x1595, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x15b8, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x15d6, code lost:
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x15da, code lost:
        if (r12.length <= 1) goto L_0x1617;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x15e2, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x1617;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x15e4, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r12[0], r12[1]);
        r2 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x1617, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x1631, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x164d, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x1669, code lost:
        r6 = r18;
        r2 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x167f, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x169c, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x16b7, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x16d4, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x16ef, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x1709, code lost:
        r3 = false;
        r38 = r4;
        r4 = r2;
        r2 = r38;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x1710, code lost:
        r6 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r12[0], r12[1]);
        r2 = r12[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x172b, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1741;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x172d, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x1741, code lost:
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x1742, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x1743, code lost:
        r4 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x1744, code lost:
        if (r2 == null) goto L_0x17de;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:?, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_message();
        r5.id = r6;
        r5.random_id = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x1751, code lost:
        if (r4 == null) goto L_0x1754;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x1754, code lost:
        r4 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x1755, code lost:
        r5.message = r4;
        r5.date = (int) (r42 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x175e, code lost:
        if (r35 == false) goto L_0x1767;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:?, code lost:
        r5.action = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x1767, code lost:
        if (r34 == false) goto L_0x1770;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x1769, code lost:
        r5.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:?, code lost:
        r5.dialog_id = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x1774, code lost:
        if (r31 == 0) goto L_0x1782;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:?, code lost:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r5.to_id = r4;
        r4.channel_id = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x1782, code lost:
        if (r24 == 0) goto L_0x1790;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x1784, code lost:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r5.to_id = r4;
        r4.chat_id = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:?, code lost:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r5.to_id = r4;
        r4.user_id = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x179b, code lost:
        r5.flags |= 256;
        r5.from_id = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x17a3, code lost:
        if (r20 != false) goto L_0x17aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x17a5, code lost:
        if (r35 == false) goto L_0x17a8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x17a8, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x17aa, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x17ab, code lost:
        r5.mentioned = r1;
        r5.silent = r19;
        r5.from_scheduled = r22;
        r19 = new org.telegram.messenger.MessageObject(r29, r5, r2, r25, r30, r3, r26, r27);
        r2 = new java.util.ArrayList();
        r2.add(r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x17d4, code lost:
        r3 = r40;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x17d6, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:?, code lost:
        org.telegram.messenger.NotificationsController.getInstance(r29).processNewMessages(r2, true, true, r3.countDownLatch);
        r8 = false;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x17de, code lost:
        r3 = r40;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x17e1, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x17e2, code lost:
        r3 = r40;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x17e5, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x17e6, code lost:
        r3 = r40;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x17e9, code lost:
        r3 = r40;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x17ec, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x17ed, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x17ef, code lost:
        r3 = r1;
        r28 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x17f2, code lost:
        r29 = r15;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:851:0x17f4, code lost:
        r8 = true;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x17f5, code lost:
        if (r8 == false) goto L_0x17fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x17f7, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x17fc, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r29);
        org.telegram.tgnet.ConnectionsManager.getInstance(r29).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x1808, code lost:
        r0 = th;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x1811, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x1812, code lost:
        r3 = r1;
        r28 = r14;
        r29 = r15;
        r1 = r0;
        r4 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x181b, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x181c, code lost:
        r3 = r1;
        r28 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x181f, code lost:
        r29 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:862:0x1823, code lost:
        r3 = r1;
        r28 = r7;
        r29 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x182c, code lost:
        r15 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:871:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.$$Lambda$GcmPushListenerService$aCTiHH_b4RhBMfcCkAHdgReKcg4(r15));
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x1839, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x183a, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x183d, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x183e, code lost:
        r15 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x1842, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x1843, code lost:
        r15 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x1847, code lost:
        r3 = r1;
        r28 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:?, code lost:
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ(r15));
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x1857, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x1858, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x185b, code lost:
        r3 = r1;
        r28 = r7;
        r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification();
        r1.popup = false;
        r1.flags = 2;
        r1.inbox_date = (int) (r42 / 1000);
        r1.message = r5.getString("message");
        r1.type = "announcement";
        r1.media = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty();
        r2 = new org.telegram.tgnet.TLRPC$TL_updates();
        r2.updates.add(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo(r15, r2));
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x18a3, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x18a4, code lost:
        r3 = r1;
        r28 = r7;
        r1 = r11.getInt("dc");
        r2 = r11.getString("addr").split(":");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x18bb, code lost:
        if (r2.length == 2) goto L_0x18c3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x18bd, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x18c2, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x18c3, code lost:
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).applyDatacenterAddress(r1, r2[0], java.lang.Integer.parseInt(r2[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:897:0x18e0, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x18e1, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:?, code lost:
        return;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:870:0x1831, B:879:0x184a] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:911:0x190a  */
    /* JADX WARNING: Removed duplicated region for block: B:912:0x191a  */
    /* JADX WARNING: Removed duplicated region for block: B:915:0x1921  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$3$GcmPushListenerService(java.util.Map r41, long r42) {
        /*
            r40 = this;
            r1 = r40
            r2 = r41
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x000d
            java.lang.String r3 = "GCM START PROCESSING"
            org.telegram.messenger.FileLog.d(r3)
        L_0x000d:
            java.lang.String r5 = "p"
            java.lang.Object r5 = r2.get(r5)     // Catch:{ all -> 0x1901 }
            boolean r6 = r5 instanceof java.lang.String     // Catch:{ all -> 0x1901 }
            if (r6 != 0) goto L_0x002d
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0024 }
            if (r2 == 0) goto L_0x0020
            java.lang.String r2 = "GCM DECRYPT ERROR 1"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x0024 }
        L_0x0020:
            r40.onDecryptError()     // Catch:{ all -> 0x0024 }
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
            goto L_0x1908
        L_0x002d:
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ all -> 0x1901 }
            r6 = 8
            byte[] r5 = android.util.Base64.decode(r5, r6)     // Catch:{ all -> 0x1901 }
            org.telegram.tgnet.NativeByteBuffer r7 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x1901 }
            int r8 = r5.length     // Catch:{ all -> 0x1901 }
            r7.<init>((int) r8)     // Catch:{ all -> 0x1901 }
            r7.writeBytes((byte[]) r5)     // Catch:{ all -> 0x1901 }
            r8 = 0
            r7.position(r8)     // Catch:{ all -> 0x1901 }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1901 }
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
            byte[] r9 = new byte[r6]     // Catch:{ all -> 0x1901 }
            r10 = 1
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1901 }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1901 }
            boolean r11 = java.util.Arrays.equals(r11, r9)     // Catch:{ all -> 0x1901 }
            r12 = 3
            r13 = 2
            if (r11 != 0) goto L_0x0092
            r40.onDecryptError()     // Catch:{ all -> 0x0024 }
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
            byte[] r9 = new byte[r9]     // Catch:{ all -> 0x1901 }
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1901 }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1901 }
            org.telegram.messenger.MessageKeyData r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13)     // Catch:{ all -> 0x1901 }
            java.nio.ByteBuffer r14 = r7.buffer     // Catch:{ all -> 0x1901 }
            byte[] r15 = r11.aesKey     // Catch:{ all -> 0x1901 }
            byte[] r11 = r11.aesIv     // Catch:{ all -> 0x1901 }
            r17 = 0
            r18 = 0
            r19 = 24
            int r5 = r5.length     // Catch:{ all -> 0x1901 }
            int r20 = r5 + -24
            r16 = r11
            org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x1901 }
            byte[] r21 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1901 }
            r22 = 96
            r23 = 32
            java.nio.ByteBuffer r5 = r7.buffer     // Catch:{ all -> 0x1901 }
            r25 = 24
            java.nio.ByteBuffer r11 = r7.buffer     // Catch:{ all -> 0x1901 }
            int r26 = r11.limit()     // Catch:{ all -> 0x1901 }
            r24 = r5
            byte[] r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26)     // Catch:{ all -> 0x1901 }
            boolean r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6)     // Catch:{ all -> 0x1901 }
            if (r5 != 0) goto L_0x00ea
            r40.onDecryptError()     // Catch:{ all -> 0x0024 }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0024 }
            if (r2 == 0) goto L_0x00e9
            java.lang.String r2 = "GCM DECRYPT ERROR 3, key = %s"
            java.lang.Object[] r5 = new java.lang.Object[r10]     // Catch:{ all -> 0x0024 }
            byte[] r6 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x0024 }
            java.lang.String r6 = org.telegram.messenger.Utilities.bytesToHex(r6)     // Catch:{ all -> 0x0024 }
            r5[r8] = r6     // Catch:{ all -> 0x0024 }
            java.lang.String r2 = java.lang.String.format(r2, r5)     // Catch:{ all -> 0x0024 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x0024 }
        L_0x00e9:
            return
        L_0x00ea:
            int r5 = r7.readInt32(r10)     // Catch:{ all -> 0x1901 }
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x1901 }
            r7.readBytes(r5, r10)     // Catch:{ all -> 0x1901 }
            java.lang.String r7 = new java.lang.String     // Catch:{ all -> 0x1901 }
            r7.<init>(r5)     // Catch:{ all -> 0x1901 }
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ all -> 0x18f7 }
            r5.<init>(r7)     // Catch:{ all -> 0x18f7 }
            java.lang.String r9 = "loc_key"
            boolean r9 = r5.has(r9)     // Catch:{ all -> 0x18f7 }
            if (r9 == 0) goto L_0x0113
            java.lang.String r9 = "loc_key"
            java.lang.String r9 = r5.getString(r9)     // Catch:{ all -> 0x010c }
            goto L_0x0115
        L_0x010c:
            r0 = move-exception
            r3 = r1
            r14 = r7
            r2 = -1
            r4 = 0
            goto L_0x0029
        L_0x0113:
            java.lang.String r9 = ""
        L_0x0115:
            java.lang.String r11 = "custom"
            java.lang.Object r11 = r5.get(r11)     // Catch:{ all -> 0x18ed }
            boolean r11 = r11 instanceof org.json.JSONObject     // Catch:{ all -> 0x18ed }
            if (r11 == 0) goto L_0x012d
            java.lang.String r11 = "custom"
            org.json.JSONObject r11 = r5.getJSONObject(r11)     // Catch:{ all -> 0x0126 }
            goto L_0x0132
        L_0x0126:
            r0 = move-exception
            r3 = r1
            r14 = r7
            r4 = r9
            r2 = -1
            goto L_0x0029
        L_0x012d:
            org.json.JSONObject r11 = new org.json.JSONObject     // Catch:{ all -> 0x18ed }
            r11.<init>()     // Catch:{ all -> 0x18ed }
        L_0x0132:
            java.lang.String r14 = "user_id"
            boolean r14 = r5.has(r14)     // Catch:{ all -> 0x18ed }
            if (r14 == 0) goto L_0x0141
            java.lang.String r14 = "user_id"
            java.lang.Object r14 = r5.get(r14)     // Catch:{ all -> 0x0126 }
            goto L_0x0142
        L_0x0141:
            r14 = 0
        L_0x0142:
            if (r14 != 0) goto L_0x014f
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x0126 }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x0126 }
            int r14 = r14.getClientUserId()     // Catch:{ all -> 0x0126 }
            goto L_0x0173
        L_0x014f:
            boolean r15 = r14 instanceof java.lang.Integer     // Catch:{ all -> 0x18ed }
            if (r15 == 0) goto L_0x015a
            java.lang.Integer r14 = (java.lang.Integer) r14     // Catch:{ all -> 0x0126 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0126 }
            goto L_0x0173
        L_0x015a:
            boolean r15 = r14 instanceof java.lang.String     // Catch:{ all -> 0x18ed }
            if (r15 == 0) goto L_0x0169
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ all -> 0x0126 }
            java.lang.Integer r14 = org.telegram.messenger.Utilities.parseInt(r14)     // Catch:{ all -> 0x0126 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0126 }
            goto L_0x0173
        L_0x0169:
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x18ed }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x18ed }
            int r14 = r14.getClientUserId()     // Catch:{ all -> 0x18ed }
        L_0x0173:
            int r15 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x18ed }
            r4 = 0
        L_0x0176:
            if (r4 >= r12) goto L_0x0189
            org.telegram.messenger.UserConfig r17 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x0126 }
            int r6 = r17.getClientUserId()     // Catch:{ all -> 0x0126 }
            if (r6 != r14) goto L_0x0184
            r15 = r4
            goto L_0x0189
        L_0x0184:
            int r4 = r4 + 1
            r6 = 8
            goto L_0x0176
        L_0x0189:
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r15)     // Catch:{ all -> 0x18e3 }
            boolean r4 = r4.isClientActivated()     // Catch:{ all -> 0x18e3 }
            if (r4 != 0) goto L_0x01a9
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x01a2 }
            if (r2 == 0) goto L_0x019c
            java.lang.String r2 = "GCM ACCOUNT NOT ACTIVATED"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x01a2 }
        L_0x019c:
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x01a2 }
            r2.countDown()     // Catch:{ all -> 0x01a2 }
            return
        L_0x01a2:
            r0 = move-exception
            r3 = r1
            r14 = r7
        L_0x01a5:
            r4 = r9
        L_0x01a6:
            r2 = -1
            goto L_0x002a
        L_0x01a9:
            java.lang.String r4 = "google.sent_time"
            r2.get(r4)     // Catch:{ all -> 0x18e3 }
            int r2 = r9.hashCode()     // Catch:{ all -> 0x18e3 }
            switch(r2) {
                case -1963663249: goto L_0x01d4;
                case -920689527: goto L_0x01ca;
                case 633004703: goto L_0x01c0;
                case 1365673842: goto L_0x01b6;
                default: goto L_0x01b5;
            }
        L_0x01b5:
            goto L_0x01de
        L_0x01b6:
            java.lang.String r2 = "GEO_LIVE_PENDING"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01a2 }
            if (r2 == 0) goto L_0x01de
            r2 = 3
            goto L_0x01df
        L_0x01c0:
            java.lang.String r2 = "MESSAGE_ANNOUNCEMENT"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01a2 }
            if (r2 == 0) goto L_0x01de
            r2 = 1
            goto L_0x01df
        L_0x01ca:
            java.lang.String r2 = "DC_UPDATE"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01a2 }
            if (r2 == 0) goto L_0x01de
            r2 = 0
            goto L_0x01df
        L_0x01d4:
            java.lang.String r2 = "SESSION_REVOKE"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01a2 }
            if (r2 == 0) goto L_0x01de
            r2 = 2
            goto L_0x01df
        L_0x01de:
            r2 = -1
        L_0x01df:
            if (r2 == 0) goto L_0x18a4
            if (r2 == r10) goto L_0x185b
            if (r2 == r13) goto L_0x1847
            if (r2 == r12) goto L_0x1823
            java.lang.String r2 = "channel_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x181b }
            r19 = 0
            if (r2 == 0) goto L_0x01fa
            java.lang.String r2 = "channel_id"
            int r2 = r11.getInt(r2)     // Catch:{ all -> 0x01a2 }
            int r4 = -r2
            long r3 = (long) r4
            goto L_0x01fd
        L_0x01fa:
            r3 = r19
            r2 = 0
        L_0x01fd:
            java.lang.String r14 = "from_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x181b }
            if (r14 == 0) goto L_0x0217
            java.lang.String r3 = "from_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x0213 }
            r14 = r7
            long r6 = (long) r3
            r38 = r6
            r6 = r3
            r3 = r38
            goto L_0x0219
        L_0x0213:
            r0 = move-exception
            r14 = r7
        L_0x0215:
            r3 = r1
            goto L_0x01a5
        L_0x0217:
            r14 = r7
            r6 = 0
        L_0x0219:
            java.lang.String r7 = "chat_id"
            boolean r7 = r11.has(r7)     // Catch:{ all -> 0x1811 }
            if (r7 == 0) goto L_0x0231
            java.lang.String r3 = "chat_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x022f }
            int r4 = -r3
            long r12 = (long) r4
            r38 = r12
            r12 = r3
            r3 = r38
            goto L_0x0232
        L_0x022f:
            r0 = move-exception
            goto L_0x0215
        L_0x0231:
            r12 = 0
        L_0x0232:
            java.lang.String r13 = "encryption_id"
            boolean r13 = r11.has(r13)     // Catch:{ all -> 0x1811 }
            if (r13 == 0) goto L_0x0244
            java.lang.String r3 = "encryption_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x022f }
            long r3 = (long) r3
            r13 = 32
            long r3 = r3 << r13
        L_0x0244:
            java.lang.String r13 = "schedule"
            boolean r13 = r11.has(r13)     // Catch:{ all -> 0x1811 }
            if (r13 == 0) goto L_0x0256
            java.lang.String r13 = "schedule"
            int r13 = r11.getInt(r13)     // Catch:{ all -> 0x022f }
            if (r13 != r10) goto L_0x0256
            r13 = 1
            goto L_0x0257
        L_0x0256:
            r13 = 0
        L_0x0257:
            int r21 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r21 != 0) goto L_0x0268
            java.lang.String r7 = "ENCRYPTED_MESSAGE"
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x022f }
            if (r7 == 0) goto L_0x0268
            r3 = -4294967296(0xfffffffvar_, double:NaN)
        L_0x0268:
            int r7 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r7 == 0) goto L_0x17ef
            java.lang.String r7 = "READ_HISTORY"
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x1811 }
            java.lang.String r10 = " for dialogId = "
            if (r7 == 0) goto L_0x02e0
            java.lang.String r5 = "max_id"
            int r5 = r11.getInt(r5)     // Catch:{ all -> 0x022f }
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ all -> 0x022f }
            r7.<init>()     // Catch:{ all -> 0x022f }
            boolean r8 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x022f }
            if (r8 == 0) goto L_0x029f
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x022f }
            r8.<init>()     // Catch:{ all -> 0x022f }
            java.lang.String r11 = "GCM received read notification max_id = "
            r8.append(r11)     // Catch:{ all -> 0x022f }
            r8.append(r5)     // Catch:{ all -> 0x022f }
            r8.append(r10)     // Catch:{ all -> 0x022f }
            r8.append(r3)     // Catch:{ all -> 0x022f }
            java.lang.String r3 = r8.toString()     // Catch:{ all -> 0x022f }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ all -> 0x022f }
        L_0x029f:
            if (r2 == 0) goto L_0x02ae
            org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox r3 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox     // Catch:{ all -> 0x022f }
            r3.<init>()     // Catch:{ all -> 0x022f }
            r3.channel_id = r2     // Catch:{ all -> 0x022f }
            r3.max_id = r5     // Catch:{ all -> 0x022f }
            r7.add(r3)     // Catch:{ all -> 0x022f }
            goto L_0x02cd
        L_0x02ae:
            org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox r2 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox     // Catch:{ all -> 0x022f }
            r2.<init>()     // Catch:{ all -> 0x022f }
            if (r6 == 0) goto L_0x02bf
            org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x022f }
            r3.<init>()     // Catch:{ all -> 0x022f }
            r2.peer = r3     // Catch:{ all -> 0x022f }
            r3.user_id = r6     // Catch:{ all -> 0x022f }
            goto L_0x02c8
        L_0x02bf:
            org.telegram.tgnet.TLRPC$TL_peerChat r3 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x022f }
            r3.<init>()     // Catch:{ all -> 0x022f }
            r2.peer = r3     // Catch:{ all -> 0x022f }
            r3.chat_id = r12     // Catch:{ all -> 0x022f }
        L_0x02c8:
            r2.max_id = r5     // Catch:{ all -> 0x022f }
            r7.add(r2)     // Catch:{ all -> 0x022f }
        L_0x02cd:
            org.telegram.messenger.MessagesController r16 = org.telegram.messenger.MessagesController.getInstance(r15)     // Catch:{ all -> 0x022f }
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r17 = r7
            r16.processUpdateArray(r17, r18, r19, r20, r21)     // Catch:{ all -> 0x022f }
            goto L_0x17ef
        L_0x02e0:
            java.lang.String r7 = "MESSAGE_DELETED"
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x1811 }
            java.lang.String r8 = "messages"
            if (r7 == 0) goto L_0x034d
            java.lang.String r5 = r11.getString(r8)     // Catch:{ all -> 0x022f }
            java.lang.String r6 = ","
            java.lang.String[] r5 = r5.split(r6)     // Catch:{ all -> 0x022f }
            android.util.SparseArray r6 = new android.util.SparseArray     // Catch:{ all -> 0x022f }
            r6.<init>()     // Catch:{ all -> 0x022f }
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ all -> 0x022f }
            r7.<init>()     // Catch:{ all -> 0x022f }
            r8 = 0
        L_0x02ff:
            int r11 = r5.length     // Catch:{ all -> 0x022f }
            if (r8 >= r11) goto L_0x030e
            r11 = r5[r8]     // Catch:{ all -> 0x022f }
            java.lang.Integer r11 = org.telegram.messenger.Utilities.parseInt(r11)     // Catch:{ all -> 0x022f }
            r7.add(r11)     // Catch:{ all -> 0x022f }
            int r8 = r8 + 1
            goto L_0x02ff
        L_0x030e:
            r6.put(r2, r7)     // Catch:{ all -> 0x022f }
            org.telegram.messenger.NotificationsController r5 = org.telegram.messenger.NotificationsController.getInstance(r15)     // Catch:{ all -> 0x022f }
            r5.removeDeletedMessagesFromNotifications(r6)     // Catch:{ all -> 0x022f }
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r15)     // Catch:{ all -> 0x022f }
            r5.deleteMessagesByPush(r3, r7, r2)     // Catch:{ all -> 0x022f }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x022f }
            if (r2 == 0) goto L_0x17ef
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x022f }
            r2.<init>()     // Catch:{ all -> 0x022f }
            java.lang.String r5 = "GCM received "
            r2.append(r5)     // Catch:{ all -> 0x022f }
            r2.append(r9)     // Catch:{ all -> 0x022f }
            r2.append(r10)     // Catch:{ all -> 0x022f }
            r2.append(r3)     // Catch:{ all -> 0x022f }
            java.lang.String r3 = " mids = "
            r2.append(r3)     // Catch:{ all -> 0x022f }
            java.lang.String r3 = ","
            java.lang.String r3 = android.text.TextUtils.join(r3, r7)     // Catch:{ all -> 0x022f }
            r2.append(r3)     // Catch:{ all -> 0x022f }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x022f }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x022f }
            goto L_0x17ef
        L_0x034d:
            boolean r7 = android.text.TextUtils.isEmpty(r9)     // Catch:{ all -> 0x1811 }
            if (r7 != 0) goto L_0x17ef
            java.lang.String r7 = "msg_id"
            boolean r7 = r11.has(r7)     // Catch:{ all -> 0x1811 }
            if (r7 == 0) goto L_0x0364
            java.lang.String r7 = "msg_id"
            int r7 = r11.getInt(r7)     // Catch:{ all -> 0x022f }
            r28 = r14
            goto L_0x0367
        L_0x0364:
            r28 = r14
            r7 = 0
        L_0x0367:
            java.lang.String r14 = "random_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x17ec }
            if (r14 == 0) goto L_0x038b
            java.lang.String r14 = "random_id"
            java.lang.String r14 = r11.getString(r14)     // Catch:{ all -> 0x0384 }
            java.lang.Long r14 = org.telegram.messenger.Utilities.parseLong(r14)     // Catch:{ all -> 0x0384 }
            long r22 = r14.longValue()     // Catch:{ all -> 0x0384 }
            r38 = r22
            r22 = r13
            r13 = r38
            goto L_0x038f
        L_0x0384:
            r0 = move-exception
            r3 = r1
            r4 = r9
            r14 = r28
            goto L_0x01a6
        L_0x038b:
            r22 = r13
            r13 = r19
        L_0x038f:
            if (r7 == 0) goto L_0x03d5
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r15)     // Catch:{ all -> 0x03cb }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r1.dialogs_read_inbox_max     // Catch:{ all -> 0x03cb }
            r23 = r6
            java.lang.Long r6 = java.lang.Long.valueOf(r3)     // Catch:{ all -> 0x03cb }
            java.lang.Object r1 = r1.get(r6)     // Catch:{ all -> 0x03cb }
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch:{ all -> 0x03cb }
            if (r1 != 0) goto L_0x03c2
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r15)     // Catch:{ all -> 0x03cb }
            r6 = 0
            int r1 = r1.getDialogReadMax(r6, r3)     // Catch:{ all -> 0x03cb }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x03cb }
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r15)     // Catch:{ all -> 0x03cb }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r6 = r6.dialogs_read_inbox_max     // Catch:{ all -> 0x03cb }
            r24 = r12
            java.lang.Long r12 = java.lang.Long.valueOf(r3)     // Catch:{ all -> 0x03cb }
            r6.put(r12, r1)     // Catch:{ all -> 0x03cb }
            goto L_0x03c4
        L_0x03c2:
            r24 = r12
        L_0x03c4:
            int r1 = r1.intValue()     // Catch:{ all -> 0x03cb }
            if (r7 <= r1) goto L_0x03e9
            goto L_0x03e7
        L_0x03cb:
            r0 = move-exception
            r2 = -1
            r3 = r40
            r1 = r0
            r4 = r9
            r14 = r28
            goto L_0x1908
        L_0x03d5:
            r23 = r6
            r24 = r12
            int r1 = (r13 > r19 ? 1 : (r13 == r19 ? 0 : -1))
            if (r1 == 0) goto L_0x03e9
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r15)     // Catch:{ all -> 0x03cb }
            boolean r1 = r1.checkMessageByRandomId(r13)     // Catch:{ all -> 0x03cb }
            if (r1 != 0) goto L_0x03e9
        L_0x03e7:
            r1 = 1
            goto L_0x03ea
        L_0x03e9:
            r1 = 0
        L_0x03ea:
            if (r1 == 0) goto L_0x17e9
            java.lang.String r1 = "chat_from_id"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x17e5 }
            if (r1 == 0) goto L_0x03fb
            java.lang.String r1 = "chat_from_id"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x03cb }
            goto L_0x03fc
        L_0x03fb:
            r1 = 0
        L_0x03fc:
            java.lang.String r6 = "mention"
            boolean r6 = r11.has(r6)     // Catch:{ all -> 0x17e5 }
            if (r6 == 0) goto L_0x040e
            java.lang.String r6 = "mention"
            int r6 = r11.getInt(r6)     // Catch:{ all -> 0x03cb }
            if (r6 == 0) goto L_0x040e
            r6 = 1
            goto L_0x040f
        L_0x040e:
            r6 = 0
        L_0x040f:
            java.lang.String r12 = "silent"
            boolean r12 = r11.has(r12)     // Catch:{ all -> 0x17e5 }
            if (r12 == 0) goto L_0x0423
            java.lang.String r12 = "silent"
            int r12 = r11.getInt(r12)     // Catch:{ all -> 0x03cb }
            if (r12 == 0) goto L_0x0423
            r29 = r15
            r12 = 1
            goto L_0x0426
        L_0x0423:
            r29 = r15
            r12 = 0
        L_0x0426:
            java.lang.String r15 = "loc_args"
            boolean r15 = r5.has(r15)     // Catch:{ all -> 0x17e1 }
            if (r15 == 0) goto L_0x0458
            java.lang.String r15 = "loc_args"
            org.json.JSONArray r5 = r5.getJSONArray(r15)     // Catch:{ all -> 0x044c }
            int r15 = r5.length()     // Catch:{ all -> 0x044c }
            r19 = r12
            java.lang.String[] r12 = new java.lang.String[r15]     // Catch:{ all -> 0x044c }
            r20 = r6
            r6 = 0
        L_0x043f:
            if (r6 >= r15) goto L_0x044a
            java.lang.String r25 = r5.getString(r6)     // Catch:{ all -> 0x044c }
            r12[r6] = r25     // Catch:{ all -> 0x044c }
            int r6 = r6 + 1
            goto L_0x043f
        L_0x044a:
            r5 = 0
            goto L_0x045e
        L_0x044c:
            r0 = move-exception
            r2 = -1
            r3 = r40
            r1 = r0
            r4 = r9
            r14 = r28
            r15 = r29
            goto L_0x1908
        L_0x0458:
            r20 = r6
            r19 = r12
            r5 = 0
            r12 = 0
        L_0x045e:
            r6 = r12[r5]     // Catch:{ all -> 0x17e1 }
            java.lang.String r5 = "edit_date"
            boolean r27 = r11.has(r5)     // Catch:{ all -> 0x17e1 }
            java.lang.String r5 = "CHAT_"
            boolean r5 = r9.startsWith(r5)     // Catch:{ all -> 0x17e1 }
            if (r5 == 0) goto L_0x0480
            if (r2 == 0) goto L_0x0472
            r5 = 1
            goto L_0x0473
        L_0x0472:
            r5 = 0
        L_0x0473:
            r11 = 1
            r15 = r12[r11]     // Catch:{ all -> 0x044c }
            r11 = 0
            r26 = 0
            r38 = r15
            r15 = r5
            r5 = r6
            r6 = r38
            goto L_0x04a4
        L_0x0480:
            java.lang.String r5 = "PINNED_"
            boolean r5 = r9.startsWith(r5)     // Catch:{ all -> 0x17e1 }
            if (r5 == 0) goto L_0x0491
            if (r1 == 0) goto L_0x048c
            r5 = 1
            goto L_0x048d
        L_0x048c:
            r5 = 0
        L_0x048d:
            r15 = r5
            r5 = 0
            r11 = 1
            goto L_0x04a2
        L_0x0491:
            java.lang.String r5 = "CHANNEL_"
            boolean r5 = r9.startsWith(r5)     // Catch:{ all -> 0x17e1 }
            if (r5 == 0) goto L_0x049f
            r5 = 0
            r11 = 0
            r15 = 0
            r26 = 1
            goto L_0x04a4
        L_0x049f:
            r5 = 0
            r11 = 0
            r15 = 0
        L_0x04a2:
            r26 = 0
        L_0x04a4:
            boolean r25 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x17e1 }
            if (r25 == 0) goto L_0x04cf
            r25 = r6
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x044c }
            r6.<init>()     // Catch:{ all -> 0x044c }
            r30 = r5
            java.lang.String r5 = "GCM received message notification "
            r6.append(r5)     // Catch:{ all -> 0x044c }
            r6.append(r9)     // Catch:{ all -> 0x044c }
            r6.append(r10)     // Catch:{ all -> 0x044c }
            r6.append(r3)     // Catch:{ all -> 0x044c }
            java.lang.String r5 = " mid = "
            r6.append(r5)     // Catch:{ all -> 0x044c }
            r6.append(r7)     // Catch:{ all -> 0x044c }
            java.lang.String r5 = r6.toString()     // Catch:{ all -> 0x044c }
            org.telegram.messenger.FileLog.d(r5)     // Catch:{ all -> 0x044c }
            goto L_0x04d3
        L_0x04cf:
            r30 = r5
            r25 = r6
        L_0x04d3:
            int r5 = r9.hashCode()     // Catch:{ all -> 0x17e1 }
            switch(r5) {
                case -2100047043: goto L_0x0988;
                case -2091498420: goto L_0x097d;
                case -2053872415: goto L_0x0972;
                case -2039746363: goto L_0x0967;
                case -2023218804: goto L_0x095c;
                case -1979538588: goto L_0x0951;
                case -1979536003: goto L_0x0946;
                case -1979535888: goto L_0x093b;
                case -1969004705: goto L_0x0930;
                case -1946699248: goto L_0x0924;
                case -1528047021: goto L_0x0918;
                case -1493579426: goto L_0x090c;
                case -1482481933: goto L_0x0900;
                case -1480102982: goto L_0x08f5;
                case -1478041834: goto L_0x08e9;
                case -1474543101: goto L_0x08de;
                case -1465695932: goto L_0x08d2;
                case -1374906292: goto L_0x08c6;
                case -1372940586: goto L_0x08ba;
                case -1264245338: goto L_0x08ae;
                case -1236086700: goto L_0x08a2;
                case -1236077786: goto L_0x0896;
                case -1235796237: goto L_0x088a;
                case -1235760759: goto L_0x087e;
                case -1235686303: goto L_0x0873;
                case -1198046100: goto L_0x0868;
                case -1124254527: goto L_0x085c;
                case -1085137927: goto L_0x0850;
                case -1084856378: goto L_0x0844;
                case -1084820900: goto L_0x0838;
                case -1084746444: goto L_0x082c;
                case -819729482: goto L_0x0820;
                case -772141857: goto L_0x0814;
                case -638310039: goto L_0x0808;
                case -590403924: goto L_0x07fc;
                case -589196239: goto L_0x07f0;
                case -589193654: goto L_0x07e4;
                case -589193539: goto L_0x07d8;
                case -440169325: goto L_0x07cc;
                case -412748110: goto L_0x07c0;
                case -228518075: goto L_0x07b4;
                case -213586509: goto L_0x07a8;
                case -115582002: goto L_0x079c;
                case -112621464: goto L_0x0790;
                case -108522133: goto L_0x0784;
                case -107572034: goto L_0x0779;
                case -40534265: goto L_0x076d;
                case 65254746: goto L_0x0761;
                case 141040782: goto L_0x0755;
                case 309993049: goto L_0x0749;
                case 309995634: goto L_0x073d;
                case 309995749: goto L_0x0731;
                case 320532812: goto L_0x0725;
                case 328933854: goto L_0x0719;
                case 331340546: goto L_0x070d;
                case 344816990: goto L_0x0701;
                case 346878138: goto L_0x06f5;
                case 350376871: goto L_0x06e9;
                case 615714517: goto L_0x06de;
                case 715508879: goto L_0x06d2;
                case 728985323: goto L_0x06c6;
                case 731046471: goto L_0x06ba;
                case 734545204: goto L_0x06ae;
                case 802032552: goto L_0x06a2;
                case 991498806: goto L_0x0696;
                case 1007364121: goto L_0x068a;
                case 1019917311: goto L_0x067e;
                case 1019926225: goto L_0x0672;
                case 1020207774: goto L_0x0666;
                case 1020243252: goto L_0x065a;
                case 1020317708: goto L_0x064e;
                case 1060349560: goto L_0x0642;
                case 1060358474: goto L_0x0636;
                case 1060640023: goto L_0x062a;
                case 1060675501: goto L_0x061e;
                case 1060749957: goto L_0x0613;
                case 1073049781: goto L_0x0607;
                case 1078101399: goto L_0x05fb;
                case 1110103437: goto L_0x05ef;
                case 1160762272: goto L_0x05e3;
                case 1172918249: goto L_0x05d7;
                case 1234591620: goto L_0x05cb;
                case 1281128640: goto L_0x05bf;
                case 1281131225: goto L_0x05b3;
                case 1281131340: goto L_0x05a7;
                case 1310789062: goto L_0x059c;
                case 1333118583: goto L_0x0590;
                case 1361447897: goto L_0x0584;
                case 1498266155: goto L_0x0578;
                case 1533804208: goto L_0x056c;
                case 1547988151: goto L_0x0560;
                case 1561464595: goto L_0x0554;
                case 1563525743: goto L_0x0548;
                case 1567024476: goto L_0x053c;
                case 1810705077: goto L_0x0530;
                case 1815177512: goto L_0x0524;
                case 1963241394: goto L_0x0518;
                case 2014789757: goto L_0x050c;
                case 2022049433: goto L_0x0500;
                case 2048733346: goto L_0x04f4;
                case 2099392181: goto L_0x04e8;
                case 2140162142: goto L_0x04dc;
                default: goto L_0x04da;
            }
        L_0x04da:
            goto L_0x0993
        L_0x04dc:
            java.lang.String r5 = "CHAT_MESSAGE_GEOLIVE"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 56
            goto L_0x0994
        L_0x04e8:
            java.lang.String r5 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 41
            goto L_0x0994
        L_0x04f4:
            java.lang.String r5 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 26
            goto L_0x0994
        L_0x0500:
            java.lang.String r5 = "PINNED_CONTACT"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 83
            goto L_0x0994
        L_0x050c:
            java.lang.String r5 = "CHAT_PHOTO_EDITED"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 64
            goto L_0x0994
        L_0x0518:
            java.lang.String r5 = "LOCKED_MESSAGE"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 96
            goto L_0x0994
        L_0x0524:
            java.lang.String r5 = "CHANNEL_MESSAGES"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 43
            goto L_0x0994
        L_0x0530:
            java.lang.String r5 = "MESSAGE_INVOICE"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 21
            goto L_0x0994
        L_0x053c:
            java.lang.String r5 = "CHAT_MESSAGE_VIDEO"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 47
            goto L_0x0994
        L_0x0548:
            java.lang.String r5 = "CHAT_MESSAGE_ROUND"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 48
            goto L_0x0994
        L_0x0554:
            java.lang.String r5 = "CHAT_MESSAGE_PHOTO"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 46
            goto L_0x0994
        L_0x0560:
            java.lang.String r5 = "CHAT_MESSAGE_AUDIO"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 51
            goto L_0x0994
        L_0x056c:
            java.lang.String r5 = "MESSAGE_VIDEOS"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 24
            goto L_0x0994
        L_0x0578:
            java.lang.String r5 = "PHONE_CALL_MISSED"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 101(0x65, float:1.42E-43)
            goto L_0x0994
        L_0x0584:
            java.lang.String r5 = "MESSAGE_PHOTOS"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 23
            goto L_0x0994
        L_0x0590:
            java.lang.String r5 = "CHAT_MESSAGE_VIDEOS"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 73
            goto L_0x0994
        L_0x059c:
            java.lang.String r5 = "MESSAGE_NOTEXT"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 2
            goto L_0x0994
        L_0x05a7:
            java.lang.String r5 = "MESSAGE_GIF"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 17
            goto L_0x0994
        L_0x05b3:
            java.lang.String r5 = "MESSAGE_GEO"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 15
            goto L_0x0994
        L_0x05bf:
            java.lang.String r5 = "MESSAGE_DOC"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 9
            goto L_0x0994
        L_0x05cb:
            java.lang.String r5 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 59
            goto L_0x0994
        L_0x05d7:
            java.lang.String r5 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 37
            goto L_0x0994
        L_0x05e3:
            java.lang.String r5 = "CHAT_MESSAGE_PHOTOS"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 72
            goto L_0x0994
        L_0x05ef:
            java.lang.String r5 = "CHAT_MESSAGE_NOTEXT"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 45
            goto L_0x0994
        L_0x05fb:
            java.lang.String r5 = "CHAT_TITLE_EDITED"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 63
            goto L_0x0994
        L_0x0607:
            java.lang.String r5 = "PINNED_NOTEXT"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 76
            goto L_0x0994
        L_0x0613:
            java.lang.String r5 = "MESSAGE_TEXT"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 0
            goto L_0x0994
        L_0x061e:
            java.lang.String r5 = "MESSAGE_QUIZ"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 13
            goto L_0x0994
        L_0x062a:
            java.lang.String r5 = "MESSAGE_POLL"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 14
            goto L_0x0994
        L_0x0636:
            java.lang.String r5 = "MESSAGE_GAME"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 18
            goto L_0x0994
        L_0x0642:
            java.lang.String r5 = "MESSAGE_FWDS"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 22
            goto L_0x0994
        L_0x064e:
            java.lang.String r5 = "CHAT_MESSAGE_TEXT"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 44
            goto L_0x0994
        L_0x065a:
            java.lang.String r5 = "CHAT_MESSAGE_QUIZ"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 53
            goto L_0x0994
        L_0x0666:
            java.lang.String r5 = "CHAT_MESSAGE_POLL"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 54
            goto L_0x0994
        L_0x0672:
            java.lang.String r5 = "CHAT_MESSAGE_GAME"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 58
            goto L_0x0994
        L_0x067e:
            java.lang.String r5 = "CHAT_MESSAGE_FWDS"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 71
            goto L_0x0994
        L_0x068a:
            java.lang.String r5 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 20
            goto L_0x0994
        L_0x0696:
            java.lang.String r5 = "PINNED_GEOLIVE"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 87
            goto L_0x0994
        L_0x06a2:
            java.lang.String r5 = "MESSAGE_CONTACT"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 12
            goto L_0x0994
        L_0x06ae:
            java.lang.String r5 = "PINNED_VIDEO"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 78
            goto L_0x0994
        L_0x06ba:
            java.lang.String r5 = "PINNED_ROUND"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 79
            goto L_0x0994
        L_0x06c6:
            java.lang.String r5 = "PINNED_PHOTO"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 77
            goto L_0x0994
        L_0x06d2:
            java.lang.String r5 = "PINNED_AUDIO"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 82
            goto L_0x0994
        L_0x06de:
            java.lang.String r5 = "MESSAGE_PHOTO_SECRET"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 4
            goto L_0x0994
        L_0x06e9:
            java.lang.String r5 = "CHANNEL_MESSAGE_VIDEO"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 28
            goto L_0x0994
        L_0x06f5:
            java.lang.String r5 = "CHANNEL_MESSAGE_ROUND"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 29
            goto L_0x0994
        L_0x0701:
            java.lang.String r5 = "CHANNEL_MESSAGE_PHOTO"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 27
            goto L_0x0994
        L_0x070d:
            java.lang.String r5 = "CHANNEL_MESSAGE_AUDIO"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 32
            goto L_0x0994
        L_0x0719:
            java.lang.String r5 = "CHAT_MESSAGE_STICKER"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 50
            goto L_0x0994
        L_0x0725:
            java.lang.String r5 = "MESSAGES"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 25
            goto L_0x0994
        L_0x0731:
            java.lang.String r5 = "CHAT_MESSAGE_GIF"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 57
            goto L_0x0994
        L_0x073d:
            java.lang.String r5 = "CHAT_MESSAGE_GEO"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 55
            goto L_0x0994
        L_0x0749:
            java.lang.String r5 = "CHAT_MESSAGE_DOC"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 49
            goto L_0x0994
        L_0x0755:
            java.lang.String r5 = "CHAT_LEFT"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 68
            goto L_0x0994
        L_0x0761:
            java.lang.String r5 = "CHAT_ADD_YOU"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 62
            goto L_0x0994
        L_0x076d:
            java.lang.String r5 = "CHAT_DELETE_MEMBER"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 66
            goto L_0x0994
        L_0x0779:
            java.lang.String r5 = "MESSAGE_SCREENSHOT"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 7
            goto L_0x0994
        L_0x0784:
            java.lang.String r5 = "AUTH_REGION"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 95
            goto L_0x0994
        L_0x0790:
            java.lang.String r5 = "CONTACT_JOINED"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 93
            goto L_0x0994
        L_0x079c:
            java.lang.String r5 = "CHAT_MESSAGE_INVOICE"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 60
            goto L_0x0994
        L_0x07a8:
            java.lang.String r5 = "ENCRYPTION_REQUEST"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 97
            goto L_0x0994
        L_0x07b4:
            java.lang.String r5 = "MESSAGE_GEOLIVE"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 16
            goto L_0x0994
        L_0x07c0:
            java.lang.String r5 = "CHAT_DELETE_YOU"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 67
            goto L_0x0994
        L_0x07cc:
            java.lang.String r5 = "AUTH_UNKNOWN"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 94
            goto L_0x0994
        L_0x07d8:
            java.lang.String r5 = "PINNED_GIF"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 91
            goto L_0x0994
        L_0x07e4:
            java.lang.String r5 = "PINNED_GEO"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 86
            goto L_0x0994
        L_0x07f0:
            java.lang.String r5 = "PINNED_DOC"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 80
            goto L_0x0994
        L_0x07fc:
            java.lang.String r5 = "PINNED_GAME_SCORE"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 89
            goto L_0x0994
        L_0x0808:
            java.lang.String r5 = "CHANNEL_MESSAGE_STICKER"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 31
            goto L_0x0994
        L_0x0814:
            java.lang.String r5 = "PHONE_CALL_REQUEST"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 99
            goto L_0x0994
        L_0x0820:
            java.lang.String r5 = "PINNED_STICKER"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 81
            goto L_0x0994
        L_0x082c:
            java.lang.String r5 = "PINNED_TEXT"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 75
            goto L_0x0994
        L_0x0838:
            java.lang.String r5 = "PINNED_QUIZ"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 84
            goto L_0x0994
        L_0x0844:
            java.lang.String r5 = "PINNED_POLL"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 85
            goto L_0x0994
        L_0x0850:
            java.lang.String r5 = "PINNED_GAME"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 88
            goto L_0x0994
        L_0x085c:
            java.lang.String r5 = "CHAT_MESSAGE_CONTACT"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 52
            goto L_0x0994
        L_0x0868:
            java.lang.String r5 = "MESSAGE_VIDEO_SECRET"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 6
            goto L_0x0994
        L_0x0873:
            java.lang.String r5 = "CHANNEL_MESSAGE_TEXT"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 1
            goto L_0x0994
        L_0x087e:
            java.lang.String r5 = "CHANNEL_MESSAGE_QUIZ"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 34
            goto L_0x0994
        L_0x088a:
            java.lang.String r5 = "CHANNEL_MESSAGE_POLL"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 35
            goto L_0x0994
        L_0x0896:
            java.lang.String r5 = "CHANNEL_MESSAGE_GAME"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 39
            goto L_0x0994
        L_0x08a2:
            java.lang.String r5 = "CHANNEL_MESSAGE_FWDS"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 40
            goto L_0x0994
        L_0x08ae:
            java.lang.String r5 = "PINNED_INVOICE"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 90
            goto L_0x0994
        L_0x08ba:
            java.lang.String r5 = "CHAT_RETURNED"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 69
            goto L_0x0994
        L_0x08c6:
            java.lang.String r5 = "ENCRYPTED_MESSAGE"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 92
            goto L_0x0994
        L_0x08d2:
            java.lang.String r5 = "ENCRYPTION_ACCEPT"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 98
            goto L_0x0994
        L_0x08de:
            java.lang.String r5 = "MESSAGE_VIDEO"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 5
            goto L_0x0994
        L_0x08e9:
            java.lang.String r5 = "MESSAGE_ROUND"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 8
            goto L_0x0994
        L_0x08f5:
            java.lang.String r5 = "MESSAGE_PHOTO"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 3
            goto L_0x0994
        L_0x0900:
            java.lang.String r5 = "MESSAGE_MUTED"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 100
            goto L_0x0994
        L_0x090c:
            java.lang.String r5 = "MESSAGE_AUDIO"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 11
            goto L_0x0994
        L_0x0918:
            java.lang.String r5 = "CHAT_MESSAGES"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 74
            goto L_0x0994
        L_0x0924:
            java.lang.String r5 = "CHAT_JOINED"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 70
            goto L_0x0994
        L_0x0930:
            java.lang.String r5 = "CHAT_ADD_MEMBER"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 65
            goto L_0x0994
        L_0x093b:
            java.lang.String r5 = "CHANNEL_MESSAGE_GIF"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 38
            goto L_0x0994
        L_0x0946:
            java.lang.String r5 = "CHANNEL_MESSAGE_GEO"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 36
            goto L_0x0994
        L_0x0951:
            java.lang.String r5 = "CHANNEL_MESSAGE_DOC"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 30
            goto L_0x0994
        L_0x095c:
            java.lang.String r5 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 42
            goto L_0x0994
        L_0x0967:
            java.lang.String r5 = "MESSAGE_STICKER"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 10
            goto L_0x0994
        L_0x0972:
            java.lang.String r5 = "CHAT_CREATED"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 61
            goto L_0x0994
        L_0x097d:
            java.lang.String r5 = "CHANNEL_MESSAGE_CONTACT"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 33
            goto L_0x0994
        L_0x0988:
            java.lang.String r5 = "MESSAGE_GAME_SCORE"
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x044c }
            if (r5 == 0) goto L_0x0993
            r6 = 19
            goto L_0x0994
        L_0x0993:
            r6 = -1
        L_0x0994:
            java.lang.String r5 = "AttachDocument"
            java.lang.String r10 = "AttachRound"
            r18 = r7
            java.lang.String r7 = "AttachVideo"
            r31 = r2
            java.lang.String r2 = "AttachPhoto"
            r32 = r3
            java.lang.String r3 = "Message"
            java.lang.String r4 = "Videos"
            r34 = r15
            java.lang.String r15 = "Photos"
            r35 = r11
            java.lang.String r11 = "ChannelMessageFew"
            r36 = r13
            java.lang.String r13 = " "
            java.lang.String r14 = "AttachSticker"
            switch(r6) {
                case 0: goto L_0x1710;
                case 1: goto L_0x1710;
                case 2: goto L_0x16ef;
                case 3: goto L_0x16d4;
                case 4: goto L_0x16b7;
                case 5: goto L_0x169c;
                case 6: goto L_0x167f;
                case 7: goto L_0x1669;
                case 8: goto L_0x164d;
                case 9: goto L_0x1631;
                case 10: goto L_0x15d6;
                case 11: goto L_0x15b8;
                case 12: goto L_0x1595;
                case 13: goto L_0x1572;
                case 14: goto L_0x154f;
                case 15: goto L_0x1531;
                case 16: goto L_0x1513;
                case 17: goto L_0x14f5;
                case 18: goto L_0x14d2;
                case 19: goto L_0x14b3;
                case 20: goto L_0x14b3;
                case 21: goto L_0x1490;
                case 22: goto L_0x146a;
                case 23: goto L_0x1444;
                case 24: goto L_0x141e;
                case 25: goto L_0x1409;
                case 26: goto L_0x13ed;
                case 27: goto L_0x13d1;
                case 28: goto L_0x13b5;
                case 29: goto L_0x1399;
                case 30: goto L_0x137d;
                case 31: goto L_0x1322;
                case 32: goto L_0x1304;
                case 33: goto L_0x12e1;
                case 34: goto L_0x12be;
                case 35: goto L_0x129b;
                case 36: goto L_0x127d;
                case 37: goto L_0x125f;
                case 38: goto L_0x1241;
                case 39: goto L_0x1223;
                case 40: goto L_0x11f9;
                case 41: goto L_0x11d5;
                case 42: goto L_0x11b1;
                case 43: goto L_0x119c;
                case 44: goto L_0x117b;
                case 45: goto L_0x115a;
                case 46: goto L_0x1139;
                case 47: goto L_0x1118;
                case 48: goto L_0x10f7;
                case 49: goto L_0x10d6;
                case 50: goto L_0x105d;
                case 51: goto L_0x103a;
                case 52: goto L_0x1012;
                case 53: goto L_0x0fea;
                case 54: goto L_0x0fc2;
                case 55: goto L_0x0f9f;
                case 56: goto L_0x0f7c;
                case 57: goto L_0x0var_;
                case 58: goto L_0x0var_;
                case 59: goto L_0x0f0d;
                case 60: goto L_0x0ee5;
                case 61: goto L_0x0ecb;
                case 62: goto L_0x0ecb;
                case 63: goto L_0x0eb1;
                case 64: goto L_0x0e97;
                case 65: goto L_0x0e78;
                case 66: goto L_0x0e5e;
                case 67: goto L_0x0e44;
                case 68: goto L_0x0e2a;
                case 69: goto L_0x0e10;
                case 70: goto L_0x0df6;
                case 71: goto L_0x0dcb;
                case 72: goto L_0x0da0;
                case 73: goto L_0x0d75;
                case 74: goto L_0x0d5b;
                case 75: goto L_0x0d22;
                case 76: goto L_0x0cf3;
                case 77: goto L_0x0cc4;
                case 78: goto L_0x0CLASSNAME;
                case 79: goto L_0x0CLASSNAME;
                case 80: goto L_0x0CLASSNAME;
                case 81: goto L_0x0bbb;
                case 82: goto L_0x0b8c;
                case 83: goto L_0x0b53;
                case 84: goto L_0x0b1a;
                case 85: goto L_0x0ae5;
                case 86: goto L_0x0ab7;
                case 87: goto L_0x0a8c;
                case 88: goto L_0x0a61;
                case 89: goto L_0x0a34;
                case 90: goto L_0x0a07;
                case 91: goto L_0x09da;
                case 92: goto L_0x09c1;
                case 93: goto L_0x09bd;
                case 94: goto L_0x09bd;
                case 95: goto L_0x09bd;
                case 96: goto L_0x09bd;
                case 97: goto L_0x09bd;
                case 98: goto L_0x09bd;
                case 99: goto L_0x09bd;
                case 100: goto L_0x09bd;
                case 101: goto L_0x09bd;
                default: goto L_0x09b7;
            }
        L_0x09b7:
            r6 = r18
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x17e1 }
            goto L_0x172b
        L_0x09bd:
            r6 = r18
            goto L_0x1741
        L_0x09c1:
            java.lang.String r2 = "YouHaveNewMessage"
            r3 = 2131627514(0x7f0e0dfa, float:1.8882295E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            java.lang.String r3 = "SecretChatName"
            r4 = 2131626822(0x7f0e0b46, float:1.8880891E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ all -> 0x044c }
            r25 = r3
            r6 = r18
        L_0x09d7:
            r3 = 1
            goto L_0x1743
        L_0x09da:
            if (r1 == 0) goto L_0x09f4
            java.lang.String r2 = "NotificationActionPinnedGif"
            r3 = 2131625997(0x7f0e080d, float:1.8879218E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r6     // Catch:{ all -> 0x044c }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r6     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x0ae1
        L_0x09f4:
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            r3 = 2131625998(0x7f0e080e, float:1.887922E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r6     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            goto L_0x0ae1
        L_0x0a07:
            if (r1 == 0) goto L_0x0a21
            java.lang.String r2 = "NotificationActionPinnedInvoice"
            r3 = 2131625999(0x7f0e080f, float:1.8879222E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r6     // Catch:{ all -> 0x044c }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r6     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x0ae1
        L_0x0a21:
            java.lang.String r2 = "NotificationActionPinnedInvoiceChannel"
            r3 = 2131626000(0x7f0e0810, float:1.8879224E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r6     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            goto L_0x0ae1
        L_0x0a34:
            if (r1 == 0) goto L_0x0a4e
            java.lang.String r2 = "NotificationActionPinnedGameScore"
            r3 = 2131625991(0x7f0e0807, float:1.8879206E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r6     // Catch:{ all -> 0x044c }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r6     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x0ae1
        L_0x0a4e:
            java.lang.String r2 = "NotificationActionPinnedGameScoreChannel"
            r3 = 2131625992(0x7f0e0808, float:1.8879208E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r6     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            goto L_0x0ae1
        L_0x0a61:
            if (r1 == 0) goto L_0x0a7a
            java.lang.String r2 = "NotificationActionPinnedGame"
            r3 = 2131625989(0x7f0e0805, float:1.8879202E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r6     // Catch:{ all -> 0x044c }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r6     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x0ae1
        L_0x0a7a:
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            r3 = 2131625990(0x7f0e0806, float:1.8879204E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r6     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            goto L_0x0ae1
        L_0x0a8c:
            if (r1 == 0) goto L_0x0aa5
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            r3 = 2131625995(0x7f0e080b, float:1.8879214E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r6     // Catch:{ all -> 0x044c }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r6     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x0ae1
        L_0x0aa5:
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            r3 = 2131625996(0x7f0e080c, float:1.8879216E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r6     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            goto L_0x0ae1
        L_0x0ab7:
            if (r1 == 0) goto L_0x0ad0
            java.lang.String r2 = "NotificationActionPinnedGeo"
            r3 = 2131625993(0x7f0e0809, float:1.887921E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r6     // Catch:{ all -> 0x044c }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r6     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x0ae1
        L_0x0ad0:
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            r3 = 2131625994(0x7f0e080a, float:1.8879212E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r6     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
        L_0x0ae1:
            r6 = r18
            goto L_0x1742
        L_0x0ae5:
            if (r1 == 0) goto L_0x0b03
            java.lang.String r2 = "NotificationActionPinnedPoll2"
            r3 = 2131626007(0x7f0e0817, float:1.8879238E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r6     // Catch:{ all -> 0x044c }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x044c }
            r7 = 1
            r4[r7] = r6     // Catch:{ all -> 0x044c }
            r6 = r12[r7]     // Catch:{ all -> 0x044c }
            r4[r5] = r6     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x0ae1
        L_0x0b03:
            java.lang.String r2 = "NotificationActionPinnedPollChannel2"
            r3 = 2131626008(0x7f0e0818, float:1.887924E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r6     // Catch:{ all -> 0x044c }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r6     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x0ae1
        L_0x0b1a:
            r6 = r18
            if (r1 == 0) goto L_0x0b3b
            java.lang.String r2 = "NotificationActionPinnedQuiz2"
            r3 = 2131626009(0x7f0e0819, float:1.8879242E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 2
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r8 = 1
            r4[r8] = r7     // Catch:{ all -> 0x044c }
            r7 = r12[r8]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0b3b:
            java.lang.String r2 = "NotificationActionPinnedQuizChannel2"
            r3 = 2131626010(0x7f0e081a, float:1.8879244E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0b53:
            r6 = r18
            if (r1 == 0) goto L_0x0b74
            java.lang.String r2 = "NotificationActionPinnedContact2"
            r3 = 2131625985(0x7f0e0801, float:1.8879193E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 2
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r8 = 1
            r4[r8] = r7     // Catch:{ all -> 0x044c }
            r7 = r12[r8]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0b74:
            java.lang.String r2 = "NotificationActionPinnedContactChannel2"
            r3 = 2131625986(0x7f0e0802, float:1.8879195E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0b8c:
            r6 = r18
            if (r1 == 0) goto L_0x0ba8
            java.lang.String r2 = "NotificationActionPinnedVoice"
            r3 = 2131626021(0x7f0e0825, float:1.8879266E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0ba8:
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            r3 = 2131626022(0x7f0e0826, float:1.8879268E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0bbb:
            r6 = r18
            if (r1 == 0) goto L_0x0CLASSNAME
            int r2 = r12.length     // Catch:{ all -> 0x044c }
            r3 = 2
            if (r2 <= r3) goto L_0x0be8
            r2 = r12[r3]     // Catch:{ all -> 0x044c }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x044c }
            if (r2 != 0) goto L_0x0be8
            java.lang.String r2 = "NotificationActionPinnedStickerEmoji"
            r3 = 2131626015(0x7f0e081f, float:1.8879254E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 2
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r8 = 1
            r4[r8] = r7     // Catch:{ all -> 0x044c }
            r7 = r12[r8]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0be8:
            java.lang.String r2 = "NotificationActionPinnedSticker"
            r3 = 2131626013(0x7f0e081d, float:1.887925E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0CLASSNAME:
            int r2 = r12.length     // Catch:{ all -> 0x044c }
            r3 = 1
            if (r2 <= r3) goto L_0x0CLASSNAME
            r2 = r12[r3]     // Catch:{ all -> 0x044c }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x044c }
            if (r2 != 0) goto L_0x0CLASSNAME
            java.lang.String r2 = "NotificationActionPinnedStickerEmojiChannel"
            r3 = 2131626016(0x7f0e0820, float:1.8879256E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0CLASSNAME:
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            r3 = 2131626014(0x7f0e081e, float:1.8879252E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0CLASSNAME:
            r6 = r18
            if (r1 == 0) goto L_0x0CLASSNAME
            java.lang.String r2 = "NotificationActionPinnedFile"
            r3 = 2131625987(0x7f0e0803, float:1.8879197E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0CLASSNAME:
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            r3 = 2131625988(0x7f0e0804, float:1.88792E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0CLASSNAME:
            r6 = r18
            if (r1 == 0) goto L_0x0CLASSNAME
            java.lang.String r2 = "NotificationActionPinnedRound"
            r3 = 2131626011(0x7f0e081b, float:1.8879246E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0CLASSNAME:
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            r3 = 2131626012(0x7f0e081c, float:1.8879248E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0CLASSNAME:
            r6 = r18
            if (r1 == 0) goto L_0x0cb1
            java.lang.String r2 = "NotificationActionPinnedVideo"
            r3 = 2131626019(0x7f0e0823, float:1.8879262E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0cb1:
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            r3 = 2131626020(0x7f0e0824, float:1.8879264E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0cc4:
            r6 = r18
            if (r1 == 0) goto L_0x0ce0
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            r3 = 2131626005(0x7f0e0815, float:1.8879234E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0ce0:
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            r3 = 2131626006(0x7f0e0816, float:1.8879236E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0cf3:
            r6 = r18
            if (r1 == 0) goto L_0x0d0f
            java.lang.String r2 = "NotificationActionPinnedNoText"
            r3 = 2131626003(0x7f0e0813, float:1.887923E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0d0f:
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            r3 = 2131626004(0x7f0e0814, float:1.8879232E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0d22:
            r6 = r18
            if (r1 == 0) goto L_0x0d43
            java.lang.String r2 = "NotificationActionPinnedText"
            r3 = 2131626017(0x7f0e0821, float:1.8879258E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 2
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0d43:
            java.lang.String r2 = "NotificationActionPinnedTextChannel"
            r3 = 2131626018(0x7f0e0822, float:1.887926E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0d5b:
            r6 = r18
            java.lang.String r2 = "NotificationGroupAlbum"
            r3 = 2131626031(0x7f0e082f, float:1.8879287E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x09d7
        L_0x0d75:
            r6 = r18
            java.lang.String r2 = "NotificationGroupFew"
            r3 = 2131626032(0x7f0e0830, float:1.8879289E38)
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x044c }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x044c }
            r5[r7] = r8     // Catch:{ all -> 0x044c }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x044c }
            r5[r7] = r8     // Catch:{ all -> 0x044c }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x044c }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ all -> 0x044c }
            int r8 = r8.intValue()     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r8)     // Catch:{ all -> 0x044c }
            r5[r7] = r4     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            goto L_0x09d7
        L_0x0da0:
            r6 = r18
            java.lang.String r2 = "NotificationGroupFew"
            r3 = 2131626032(0x7f0e0830, float:1.8879289E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 2
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x044c }
            int r7 = r7.intValue()     // Catch:{ all -> 0x044c }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r15, r7)     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x09d7
        L_0x0dcb:
            r6 = r18
            java.lang.String r2 = "NotificationGroupForwardedFew"
            r3 = 2131626033(0x7f0e0831, float:1.887929E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 2
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x044c }
            int r7 = r7.intValue()     // Catch:{ all -> 0x044c }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r8, r7)     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x09d7
        L_0x0df6:
            r6 = r18
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            r3 = 2131626030(0x7f0e082e, float:1.8879285E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0e10:
            r6 = r18
            java.lang.String r2 = "NotificationGroupAddSelf"
            r3 = 2131626029(0x7f0e082d, float:1.8879283E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0e2a:
            r6 = r18
            java.lang.String r2 = "NotificationGroupLeftMember"
            r3 = 2131626036(0x7f0e0834, float:1.8879297E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0e44:
            r6 = r18
            java.lang.String r2 = "NotificationGroupKickYou"
            r3 = 2131626035(0x7f0e0833, float:1.8879295E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0e5e:
            r6 = r18
            java.lang.String r2 = "NotificationGroupKickMember"
            r3 = 2131626034(0x7f0e0832, float:1.8879293E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0e78:
            r6 = r18
            java.lang.String r2 = "NotificationGroupAddMember"
            r3 = 2131626028(0x7f0e082c, float:1.887928E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 2
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0e97:
            r6 = r18
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            r3 = 2131626026(0x7f0e082a, float:1.8879277E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0eb1:
            r6 = r18
            java.lang.String r2 = "NotificationEditedGroupName"
            r3 = 2131626025(0x7f0e0829, float:1.8879275E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0ecb:
            r6 = r18
            java.lang.String r2 = "NotificationInvitedToGroup"
            r3 = 2131626041(0x7f0e0839, float:1.8879307E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0ee5:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGroupInvoice"
            r3 = 2131626058(0x7f0e084a, float:1.8879341E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 2
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "PaymentInvoice"
            r3 = 2131626428(0x7f0e09bc, float:1.8880092E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x0f0d:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGroupGameScored"
            r3 = 2131626056(0x7f0e0848, float:1.8879337E38)
            r4 = 4
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r8 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r8     // Catch:{ all -> 0x044c }
            r5 = 1
            r8 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r8     // Catch:{ all -> 0x044c }
            r5 = 2
            r8 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r8     // Catch:{ all -> 0x044c }
            r5 = 3
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x0var_:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGroupGame"
            r3 = 2131626055(0x7f0e0847, float:1.8879335E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 2
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachGame"
            r3 = 2131624339(0x7f0e0193, float:1.8875855E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x0var_:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGroupGif"
            r3 = 2131626057(0x7f0e0849, float:1.887934E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachGif"
            r3 = 2131624340(0x7f0e0194, float:1.8875857E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x0f7c:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGroupLiveLocation"
            r3 = 2131626059(0x7f0e084b, float:1.8879343E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachLiveLocation"
            r3 = 2131624345(0x7f0e0199, float:1.8875867E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x0f9f:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGroupMap"
            r3 = 2131626060(0x7f0e084c, float:1.8879346E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachLocation"
            r3 = 2131624347(0x7f0e019b, float:1.8875871E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x0fc2:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGroupPoll2"
            r3 = 2131626064(0x7f0e0850, float:1.8879354E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 2
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "Poll"
            r3 = 2131626542(0x7f0e0a2e, float:1.8880323E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x0fea:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGroupQuiz2"
            r3 = 2131626065(0x7f0e0851, float:1.8879356E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 2
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "PollQuiz"
            r3 = 2131626549(0x7f0e0a35, float:1.8880337E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x1012:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGroupContact2"
            r3 = 2131626053(0x7f0e0845, float:1.8879331E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 2
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachContact"
            r3 = 2131624335(0x7f0e018f, float:1.8875847E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x103a:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGroupAudio"
            r3 = 2131626052(0x7f0e0844, float:1.887933E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachAudio"
            r3 = 2131624333(0x7f0e018d, float:1.8875843E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x105d:
            r6 = r18
            int r2 = r12.length     // Catch:{ all -> 0x044c }
            r3 = 2
            if (r2 <= r3) goto L_0x10a3
            r2 = r12[r3]     // Catch:{ all -> 0x044c }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x044c }
            if (r2 != 0) goto L_0x10a3
            java.lang.String r2 = "NotificationMessageGroupStickerEmoji"
            r3 = 2131626068(0x7f0e0854, float:1.8879362E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 2
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x044c }
            r2.<init>()     // Catch:{ all -> 0x044c }
            r3 = r12[r5]     // Catch:{ all -> 0x044c }
            r2.append(r3)     // Catch:{ all -> 0x044c }
            r2.append(r13)     // Catch:{ all -> 0x044c }
            r3 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)     // Catch:{ all -> 0x044c }
            r2.append(r3)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x10a3:
            java.lang.String r2 = "NotificationMessageGroupSticker"
            r3 = 2131626067(0x7f0e0853, float:1.887936E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x044c }
            r2.<init>()     // Catch:{ all -> 0x044c }
            r3 = r12[r5]     // Catch:{ all -> 0x044c }
            r2.append(r3)     // Catch:{ all -> 0x044c }
            r2.append(r13)     // Catch:{ all -> 0x044c }
            r3 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)     // Catch:{ all -> 0x044c }
            r2.append(r3)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x10d6:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGroupDocument"
            r3 = 2131626054(0x7f0e0846, float:1.8879333E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x044c }
            r4[r7] = r8     // Catch:{ all -> 0x044c }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x044c }
            r4[r7] = r8     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            r2 = 2131624338(0x7f0e0192, float:1.8875853E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x10f7:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGroupRound"
            r3 = 2131626066(0x7f0e0852, float:1.8879358E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            r2 = 2131624353(0x7f0e01a1, float:1.8875883E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x1118:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGroupVideo"
            r3 = 2131626070(0x7f0e0856, float:1.8879366E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r8 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r8     // Catch:{ all -> 0x044c }
            r5 = 1
            r8 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r8     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            r2 = 2131624357(0x7f0e01a5, float:1.8875891E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x1139:
            r6 = r18
            java.lang.String r3 = "NotificationMessageGroupPhoto"
            r4 = 2131626063(0x7f0e084f, float:1.8879352E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x044c }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x044c }
            r5[r7] = r8     // Catch:{ all -> 0x044c }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x044c }
            r5[r7] = r8     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r5)     // Catch:{ all -> 0x044c }
            r3 = 2131624351(0x7f0e019f, float:1.887588E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x115a:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGroupNoText"
            r4 = 2131626062(0x7f0e084e, float:1.887935E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x044c }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x044c }
            r5[r7] = r8     // Catch:{ all -> 0x044c }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x044c }
            r5[r7] = r8     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r4, r5)     // Catch:{ all -> 0x044c }
            r2 = 2131625814(0x7f0e0756, float:1.8878847E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x117b:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGroupText"
            r3 = 2131626069(0x7f0e0855, float:1.8879364E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 2
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            r2 = r12[r5]     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x119c:
            r6 = r18
            java.lang.String r2 = "ChannelMessageAlbum"
            r3 = 2131624630(0x7f0e02b6, float:1.8876445E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            goto L_0x09d7
        L_0x11b1:
            r6 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x044c }
            r3 = 0
            r5 = r12[r3]     // Catch:{ all -> 0x044c }
            r2[r3] = r5     // Catch:{ all -> 0x044c }
            r3 = 1
            r5 = r12[r3]     // Catch:{ all -> 0x044c }
            java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r5)     // Catch:{ all -> 0x044c }
            int r5 = r5.intValue()     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r5)     // Catch:{ all -> 0x044c }
            r2[r3] = r4     // Catch:{ all -> 0x044c }
            r3 = 2131624634(0x7f0e02ba, float:1.8876453E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r3, r2)     // Catch:{ all -> 0x044c }
            goto L_0x09d7
        L_0x11d5:
            r6 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x044c }
            r3 = 0
            r4 = r12[r3]     // Catch:{ all -> 0x044c }
            r2[r3] = r4     // Catch:{ all -> 0x044c }
            r3 = 1
            r4 = r12[r3]     // Catch:{ all -> 0x044c }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x044c }
            int r4 = r4.intValue()     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r15, r4)     // Catch:{ all -> 0x044c }
            r2[r3] = r4     // Catch:{ all -> 0x044c }
            r3 = 2131624634(0x7f0e02ba, float:1.8876453E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r3, r2)     // Catch:{ all -> 0x044c }
            goto L_0x09d7
        L_0x11f9:
            r6 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x044c }
            r3 = 0
            r4 = r12[r3]     // Catch:{ all -> 0x044c }
            r2[r3] = r4     // Catch:{ all -> 0x044c }
            java.lang.String r3 = "ForwardedMessageCount"
            r4 = 1
            r5 = r12[r4]     // Catch:{ all -> 0x044c }
            java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r5)     // Catch:{ all -> 0x044c }
            int r5 = r5.intValue()     // Catch:{ all -> 0x044c }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r5)     // Catch:{ all -> 0x044c }
            java.lang.String r3 = r3.toLowerCase()     // Catch:{ all -> 0x044c }
            r2[r4] = r3     // Catch:{ all -> 0x044c }
            r3 = 2131624634(0x7f0e02ba, float:1.8876453E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r3, r2)     // Catch:{ all -> 0x044c }
            goto L_0x09d7
        L_0x1223:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGame"
            r3 = 2131626049(0x7f0e0841, float:1.8879323E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachGame"
            r3 = 2131624339(0x7f0e0193, float:1.8875855E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x1241:
            r6 = r18
            java.lang.String r2 = "ChannelMessageGIF"
            r3 = 2131624635(0x7f0e02bb, float:1.8876455E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachGif"
            r3 = 2131624340(0x7f0e0194, float:1.8875857E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x125f:
            r6 = r18
            java.lang.String r2 = "ChannelMessageLiveLocation"
            r3 = 2131624636(0x7f0e02bc, float:1.8876457E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachLiveLocation"
            r3 = 2131624345(0x7f0e0199, float:1.8875867E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x127d:
            r6 = r18
            java.lang.String r2 = "ChannelMessageMap"
            r3 = 2131624637(0x7f0e02bd, float:1.887646E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachLocation"
            r3 = 2131624347(0x7f0e019b, float:1.8875871E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x129b:
            r6 = r18
            java.lang.String r2 = "ChannelMessagePoll2"
            r3 = 2131624641(0x7f0e02c1, float:1.8876467E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "Poll"
            r3 = 2131626542(0x7f0e0a2e, float:1.8880323E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x12be:
            r6 = r18
            java.lang.String r2 = "ChannelMessageQuiz2"
            r3 = 2131624642(0x7f0e02c2, float:1.887647E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "QuizPoll"
            r3 = 2131626634(0x7f0e0a8a, float:1.888051E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x12e1:
            r6 = r18
            java.lang.String r2 = "ChannelMessageContact2"
            r3 = 2131624632(0x7f0e02b8, float:1.887645E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachContact"
            r3 = 2131624335(0x7f0e018f, float:1.8875847E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x1304:
            r6 = r18
            java.lang.String r2 = "ChannelMessageAudio"
            r3 = 2131624631(0x7f0e02b7, float:1.8876447E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachAudio"
            r3 = 2131624333(0x7f0e018d, float:1.8875843E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x1322:
            r6 = r18
            int r2 = r12.length     // Catch:{ all -> 0x044c }
            r3 = 1
            if (r2 <= r3) goto L_0x1363
            r2 = r12[r3]     // Catch:{ all -> 0x044c }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x044c }
            if (r2 != 0) goto L_0x1363
            java.lang.String r2 = "ChannelMessageStickerEmoji"
            r3 = 2131624645(0x7f0e02c5, float:1.8876476E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x044c }
            r2.<init>()     // Catch:{ all -> 0x044c }
            r3 = r12[r5]     // Catch:{ all -> 0x044c }
            r2.append(r3)     // Catch:{ all -> 0x044c }
            r2.append(r13)     // Catch:{ all -> 0x044c }
            r3 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)     // Catch:{ all -> 0x044c }
            r2.append(r3)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x1363:
            java.lang.String r2 = "ChannelMessageSticker"
            r3 = 2131624644(0x7f0e02c4, float:1.8876474E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            r2 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r14, r2)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x137d:
            r6 = r18
            java.lang.String r2 = "ChannelMessageDocument"
            r3 = 2131624633(0x7f0e02b9, float:1.8876451E38)
            r4 = 1
            java.lang.Object[] r7 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r8 = r12[r4]     // Catch:{ all -> 0x044c }
            r7[r4] = r8     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r7)     // Catch:{ all -> 0x044c }
            r2 = 2131624338(0x7f0e0192, float:1.8875853E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x1399:
            r6 = r18
            java.lang.String r2 = "ChannelMessageRound"
            r3 = 2131624643(0x7f0e02c3, float:1.8876472E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            r2 = 2131624353(0x7f0e01a1, float:1.8875883E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x13b5:
            r6 = r18
            java.lang.String r2 = "ChannelMessageVideo"
            r3 = 2131624646(0x7f0e02c6, float:1.8876478E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r8 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r8     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            r2 = 2131624357(0x7f0e01a5, float:1.8875891E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x13d1:
            r6 = r18
            java.lang.String r3 = "ChannelMessagePhoto"
            r4 = 2131624640(0x7f0e02c0, float:1.8876465E38)
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x044c }
            r5 = 0
            r8 = r12[r5]     // Catch:{ all -> 0x044c }
            r7[r5] = r8     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r7)     // Catch:{ all -> 0x044c }
            r3 = 2131624351(0x7f0e019f, float:1.887588E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x13ed:
            r6 = r18
            java.lang.String r2 = "ChannelMessageNoText"
            r4 = 2131624639(0x7f0e02bf, float:1.8876463E38)
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x044c }
            r5 = 0
            r8 = r12[r5]     // Catch:{ all -> 0x044c }
            r7[r5] = r8     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r4, r7)     // Catch:{ all -> 0x044c }
            r2 = 2131625814(0x7f0e0756, float:1.8878847E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x1409:
            r6 = r18
            java.lang.String r2 = "NotificationMessageAlbum"
            r3 = 2131626043(0x7f0e083b, float:1.8879311E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            goto L_0x09d7
        L_0x141e:
            r6 = r18
            java.lang.String r2 = "NotificationMessageFew"
            r3 = 2131626047(0x7f0e083f, float:1.887932E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x044c }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x044c }
            r5[r7] = r8     // Catch:{ all -> 0x044c }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x044c }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ all -> 0x044c }
            int r8 = r8.intValue()     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r8)     // Catch:{ all -> 0x044c }
            r5[r7] = r4     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            goto L_0x09d7
        L_0x1444:
            r6 = r18
            java.lang.String r2 = "NotificationMessageFew"
            r3 = 2131626047(0x7f0e083f, float:1.887932E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x044c }
            int r7 = r7.intValue()     // Catch:{ all -> 0x044c }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r15, r7)     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x09d7
        L_0x146a:
            r6 = r18
            java.lang.String r2 = "NotificationMessageForwardFew"
            r3 = 2131626048(0x7f0e0840, float:1.8879321E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x044c }
            int r7 = r7.intValue()     // Catch:{ all -> 0x044c }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r8, r7)     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x09d7
        L_0x1490:
            r6 = r18
            java.lang.String r2 = "NotificationMessageInvoice"
            r3 = 2131626071(0x7f0e0857, float:1.8879368E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "PaymentInvoice"
            r3 = 2131626428(0x7f0e09bc, float:1.8880092E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x14b3:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGameScored"
            r3 = 2131626050(0x7f0e0842, float:1.8879325E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 2
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x14d2:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGame"
            r3 = 2131626049(0x7f0e0841, float:1.8879323E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachGame"
            r3 = 2131624339(0x7f0e0193, float:1.8875855E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x14f5:
            r6 = r18
            java.lang.String r2 = "NotificationMessageGif"
            r3 = 2131626051(0x7f0e0843, float:1.8879327E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachGif"
            r3 = 2131624340(0x7f0e0194, float:1.8875857E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x1513:
            r6 = r18
            java.lang.String r2 = "NotificationMessageLiveLocation"
            r3 = 2131626072(0x7f0e0858, float:1.887937E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachLiveLocation"
            r3 = 2131624345(0x7f0e0199, float:1.8875867E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x1531:
            r6 = r18
            java.lang.String r2 = "NotificationMessageMap"
            r3 = 2131626073(0x7f0e0859, float:1.8879372E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachLocation"
            r3 = 2131624347(0x7f0e019b, float:1.8875871E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x154f:
            r6 = r18
            java.lang.String r2 = "NotificationMessagePoll2"
            r3 = 2131626077(0x7f0e085d, float:1.887938E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "Poll"
            r3 = 2131626542(0x7f0e0a2e, float:1.8880323E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x1572:
            r6 = r18
            java.lang.String r2 = "NotificationMessageQuiz2"
            r3 = 2131626078(0x7f0e085e, float:1.8879382E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "QuizPoll"
            r3 = 2131626634(0x7f0e0a8a, float:1.888051E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x1595:
            r6 = r18
            java.lang.String r2 = "NotificationMessageContact2"
            r3 = 2131626045(0x7f0e083d, float:1.8879315E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachContact"
            r3 = 2131624335(0x7f0e018f, float:1.8875847E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x15b8:
            r6 = r18
            java.lang.String r2 = "NotificationMessageAudio"
            r3 = 2131626044(0x7f0e083c, float:1.8879313E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachAudio"
            r3 = 2131624333(0x7f0e018d, float:1.8875843E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x15d6:
            r6 = r18
            int r2 = r12.length     // Catch:{ all -> 0x044c }
            r3 = 1
            if (r2 <= r3) goto L_0x1617
            r2 = r12[r3]     // Catch:{ all -> 0x044c }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x044c }
            if (r2 != 0) goto L_0x1617
            java.lang.String r2 = "NotificationMessageStickerEmoji"
            r3 = 2131626085(0x7f0e0865, float:1.8879396E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x044c }
            r2.<init>()     // Catch:{ all -> 0x044c }
            r3 = r12[r5]     // Catch:{ all -> 0x044c }
            r2.append(r3)     // Catch:{ all -> 0x044c }
            r2.append(r13)     // Catch:{ all -> 0x044c }
            r3 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)     // Catch:{ all -> 0x044c }
            r2.append(r3)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x1617:
            java.lang.String r2 = "NotificationMessageSticker"
            r3 = 2131626084(0x7f0e0864, float:1.8879394E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            r2 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r14, r2)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x1631:
            r6 = r18
            java.lang.String r2 = "NotificationMessageDocument"
            r3 = 2131626046(0x7f0e083e, float:1.8879317E38)
            r4 = 1
            java.lang.Object[] r7 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r8 = r12[r4]     // Catch:{ all -> 0x044c }
            r7[r4] = r8     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r7)     // Catch:{ all -> 0x044c }
            r2 = 2131624338(0x7f0e0192, float:1.8875853E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x164d:
            r6 = r18
            java.lang.String r2 = "NotificationMessageRound"
            r3 = 2131626079(0x7f0e085f, float:1.8879384E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            r2 = 2131624353(0x7f0e01a1, float:1.8875883E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x1669:
            r6 = r18
            java.lang.String r2 = "ActionTakeScreenshoot"
            r3 = 2131624128(0x7f0e00c0, float:1.8875427E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            java.lang.String r3 = "un1"
            r4 = 0
            r5 = r12[r4]     // Catch:{ all -> 0x044c }
            java.lang.String r2 = r2.replace(r3, r5)     // Catch:{ all -> 0x044c }
            goto L_0x1742
        L_0x167f:
            r6 = r18
            java.lang.String r2 = "NotificationMessageSDVideo"
            r3 = 2131626081(0x7f0e0861, float:1.8879388E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachDestructingVideo"
            r3 = 2131624337(0x7f0e0191, float:1.887585E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x169c:
            r6 = r18
            java.lang.String r2 = "NotificationMessageVideo"
            r3 = 2131626087(0x7f0e0867, float:1.88794E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r8 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r8     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            r2 = 2131624357(0x7f0e01a5, float:1.8875891E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x16b7:
            r6 = r18
            java.lang.String r2 = "NotificationMessageSDPhoto"
            r3 = 2131626080(0x7f0e0860, float:1.8879386E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x044c }
            r5[r4] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = "AttachDestructingPhoto"
            r3 = 2131624336(0x7f0e0190, float:1.8875849E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x16d4:
            r6 = r18
            java.lang.String r3 = "NotificationMessagePhoto"
            r4 = 2131626076(0x7f0e085c, float:1.8879378E38)
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x044c }
            r5 = 0
            r8 = r12[r5]     // Catch:{ all -> 0x044c }
            r7[r5] = r8     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r3, r4, r7)     // Catch:{ all -> 0x044c }
            r3 = 2131624351(0x7f0e019f, float:1.887588E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x16ef:
            r6 = r18
            java.lang.String r2 = "NotificationMessageNoText"
            r4 = 2131626075(0x7f0e085b, float:1.8879376E38)
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x044c }
            r5 = 0
            r8 = r12[r5]     // Catch:{ all -> 0x044c }
            r7[r5] = r8     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r4, r7)     // Catch:{ all -> 0x044c }
            r2 = 2131625814(0x7f0e0756, float:1.8878847E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)     // Catch:{ all -> 0x044c }
        L_0x1709:
            r3 = 0
            r38 = r4
            r4 = r2
            r2 = r38
            goto L_0x1744
        L_0x1710:
            r6 = r18
            java.lang.String r2 = "NotificationMessageText"
            r3 = 2131626086(0x7f0e0866, float:1.8879398E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x044c }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            r5 = 1
            r7 = r12[r5]     // Catch:{ all -> 0x044c }
            r4[r5] = r7     // Catch:{ all -> 0x044c }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x044c }
            r2 = r12[r5]     // Catch:{ all -> 0x044c }
            goto L_0x1709
        L_0x172b:
            if (r2 == 0) goto L_0x1741
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x044c }
            r2.<init>()     // Catch:{ all -> 0x044c }
            java.lang.String r3 = "unhandled loc_key = "
            r2.append(r3)     // Catch:{ all -> 0x044c }
            r2.append(r9)     // Catch:{ all -> 0x044c }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x044c }
            org.telegram.messenger.FileLog.w(r2)     // Catch:{ all -> 0x044c }
        L_0x1741:
            r2 = 0
        L_0x1742:
            r3 = 0
        L_0x1743:
            r4 = 0
        L_0x1744:
            if (r2 == 0) goto L_0x17de
            org.telegram.tgnet.TLRPC$TL_message r5 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x17e1 }
            r5.<init>()     // Catch:{ all -> 0x17e1 }
            r5.id = r6     // Catch:{ all -> 0x17e1 }
            r6 = r36
            r5.random_id = r6     // Catch:{ all -> 0x17e1 }
            if (r4 == 0) goto L_0x1754
            goto L_0x1755
        L_0x1754:
            r4 = r2
        L_0x1755:
            r5.message = r4     // Catch:{ all -> 0x17e1 }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r6 = r42 / r6
            int r4 = (int) r6     // Catch:{ all -> 0x17e1 }
            r5.date = r4     // Catch:{ all -> 0x17e1 }
            if (r35 == 0) goto L_0x1767
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r4 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x044c }
            r4.<init>()     // Catch:{ all -> 0x044c }
            r5.action = r4     // Catch:{ all -> 0x044c }
        L_0x1767:
            if (r34 == 0) goto L_0x1770
            int r4 = r5.flags     // Catch:{ all -> 0x044c }
            r6 = -2147483648(0xfffffffvar_, float:-0.0)
            r4 = r4 | r6
            r5.flags = r4     // Catch:{ all -> 0x044c }
        L_0x1770:
            r6 = r32
            r5.dialog_id = r6     // Catch:{ all -> 0x17e1 }
            if (r31 == 0) goto L_0x1782
            org.telegram.tgnet.TLRPC$TL_peerChannel r4 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x044c }
            r4.<init>()     // Catch:{ all -> 0x044c }
            r5.to_id = r4     // Catch:{ all -> 0x044c }
            r8 = r31
            r4.channel_id = r8     // Catch:{ all -> 0x044c }
            goto L_0x179b
        L_0x1782:
            if (r24 == 0) goto L_0x1790
            org.telegram.tgnet.TLRPC$TL_peerChat r4 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x044c }
            r4.<init>()     // Catch:{ all -> 0x044c }
            r5.to_id = r4     // Catch:{ all -> 0x044c }
            r8 = r24
            r4.chat_id = r8     // Catch:{ all -> 0x044c }
            goto L_0x179b
        L_0x1790:
            org.telegram.tgnet.TLRPC$TL_peerUser r4 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x17e1 }
            r4.<init>()     // Catch:{ all -> 0x17e1 }
            r5.to_id = r4     // Catch:{ all -> 0x17e1 }
            r8 = r23
            r4.user_id = r8     // Catch:{ all -> 0x17e1 }
        L_0x179b:
            int r4 = r5.flags     // Catch:{ all -> 0x17e1 }
            r4 = r4 | 256(0x100, float:3.59E-43)
            r5.flags = r4     // Catch:{ all -> 0x17e1 }
            r5.from_id = r1     // Catch:{ all -> 0x17e1 }
            if (r20 != 0) goto L_0x17aa
            if (r35 == 0) goto L_0x17a8
            goto L_0x17aa
        L_0x17a8:
            r1 = 0
            goto L_0x17ab
        L_0x17aa:
            r1 = 1
        L_0x17ab:
            r5.mentioned = r1     // Catch:{ all -> 0x17e1 }
            r1 = r19
            r5.silent = r1     // Catch:{ all -> 0x17e1 }
            r13 = r22
            r5.from_scheduled = r13     // Catch:{ all -> 0x17e1 }
            org.telegram.messenger.MessageObject r1 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x17e1 }
            r19 = r1
            r20 = r29
            r21 = r5
            r22 = r2
            r23 = r25
            r24 = r30
            r25 = r3
            r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27)     // Catch:{ all -> 0x17e1 }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x17e1 }
            r2.<init>()     // Catch:{ all -> 0x17e1 }
            r2.add(r1)     // Catch:{ all -> 0x17e1 }
            org.telegram.messenger.NotificationsController r1 = org.telegram.messenger.NotificationsController.getInstance(r29)     // Catch:{ all -> 0x17e1 }
            r3 = r40
            java.util.concurrent.CountDownLatch r4 = r3.countDownLatch     // Catch:{ all -> 0x1808 }
            r5 = 1
            r1.processNewMessages(r2, r5, r5, r4)     // Catch:{ all -> 0x1808 }
            r8 = 0
            goto L_0x17f5
        L_0x17de:
            r3 = r40
            goto L_0x17f4
        L_0x17e1:
            r0 = move-exception
            r3 = r40
            goto L_0x1809
        L_0x17e5:
            r0 = move-exception
            r3 = r40
            goto L_0x181f
        L_0x17e9:
            r3 = r40
            goto L_0x17f2
        L_0x17ec:
            r0 = move-exception
            r3 = r1
            goto L_0x181f
        L_0x17ef:
            r3 = r1
            r28 = r14
        L_0x17f2:
            r29 = r15
        L_0x17f4:
            r8 = 1
        L_0x17f5:
            if (r8 == 0) goto L_0x17fc
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1808 }
            r1.countDown()     // Catch:{ all -> 0x1808 }
        L_0x17fc:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r29)     // Catch:{ all -> 0x1808 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r29)     // Catch:{ all -> 0x1808 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1808 }
            goto L_0x1940
        L_0x1808:
            r0 = move-exception
        L_0x1809:
            r1 = r0
            r4 = r9
            r14 = r28
            r15 = r29
            goto L_0x18eb
        L_0x1811:
            r0 = move-exception
            r3 = r1
            r28 = r14
            r29 = r15
            r1 = r0
            r4 = r9
            goto L_0x18eb
        L_0x181b:
            r0 = move-exception
            r3 = r1
            r28 = r7
        L_0x181f:
            r29 = r15
            goto L_0x18e7
        L_0x1823:
            r3 = r1
            r28 = r7
            r29 = r15
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1842 }
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$aCTiHH_b4RhBMfcCkAHdgReKcg4 r2 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$aCTiHH_b4RhBMfcCkAHdgReKcg4     // Catch:{ all -> 0x183d }
            r15 = r29
            r2.<init>(r15)     // Catch:{ all -> 0x183a }
            r1.postRunnable(r2)     // Catch:{ all -> 0x18e1 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x18e1 }
            r1.countDown()     // Catch:{ all -> 0x18e1 }
            return
        L_0x183a:
            r0 = move-exception
            goto L_0x18e7
        L_0x183d:
            r0 = move-exception
            r15 = r29
            goto L_0x18e7
        L_0x1842:
            r0 = move-exception
            r15 = r29
            goto L_0x18e7
        L_0x1847:
            r3 = r1
            r28 = r7
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ r1 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ     // Catch:{ all -> 0x1858 }
            r1.<init>(r15)     // Catch:{ all -> 0x1858 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ all -> 0x18e1 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x18e1 }
            r1.countDown()     // Catch:{ all -> 0x18e1 }
            return
        L_0x1858:
            r0 = move-exception
            goto L_0x18e7
        L_0x185b:
            r3 = r1
            r28 = r7
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x18e1 }
            r1.<init>()     // Catch:{ all -> 0x18e1 }
            r2 = 0
            r1.popup = r2     // Catch:{ all -> 0x18e1 }
            r2 = 2
            r1.flags = r2     // Catch:{ all -> 0x18e1 }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r6 = r42 / r6
            int r2 = (int) r6     // Catch:{ all -> 0x18e1 }
            r1.inbox_date = r2     // Catch:{ all -> 0x18e1 }
            java.lang.String r2 = "message"
            java.lang.String r2 = r5.getString(r2)     // Catch:{ all -> 0x18e1 }
            r1.message = r2     // Catch:{ all -> 0x18e1 }
            java.lang.String r2 = "announcement"
            r1.type = r2     // Catch:{ all -> 0x18e1 }
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ all -> 0x18e1 }
            r2.<init>()     // Catch:{ all -> 0x18e1 }
            r1.media = r2     // Catch:{ all -> 0x18e1 }
            org.telegram.tgnet.TLRPC$TL_updates r2 = new org.telegram.tgnet.TLRPC$TL_updates     // Catch:{ all -> 0x18e1 }
            r2.<init>()     // Catch:{ all -> 0x18e1 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r4 = r2.updates     // Catch:{ all -> 0x18e1 }
            r4.add(r1)     // Catch:{ all -> 0x18e1 }
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x18e1 }
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo r4 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo     // Catch:{ all -> 0x1858 }
            r4.<init>(r15, r2)     // Catch:{ all -> 0x1858 }
            r1.postRunnable(r4)     // Catch:{ all -> 0x18e1 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x18e1 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x18e1 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x18e1 }
            r1.countDown()     // Catch:{ all -> 0x18e1 }
            return
        L_0x18a4:
            r3 = r1
            r28 = r7
            java.lang.String r1 = "dc"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x18e1 }
            java.lang.String r2 = "addr"
            java.lang.String r2 = r11.getString(r2)     // Catch:{ all -> 0x18e1 }
            java.lang.String r4 = ":"
            java.lang.String[] r2 = r2.split(r4)     // Catch:{ all -> 0x18e1 }
            int r4 = r2.length     // Catch:{ all -> 0x18e1 }
            r5 = 2
            if (r4 == r5) goto L_0x18c3
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x18e1 }
            r1.countDown()     // Catch:{ all -> 0x18e1 }
            return
        L_0x18c3:
            r4 = 0
            r4 = r2[r4]     // Catch:{ all -> 0x18e1 }
            r5 = 1
            r2 = r2[r5]     // Catch:{ all -> 0x18e1 }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ all -> 0x18e1 }
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x18e1 }
            r5.applyDatacenterAddress(r1, r4, r2)     // Catch:{ all -> 0x18e1 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x18e1 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x18e1 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x18e1 }
            r1.countDown()     // Catch:{ all -> 0x18e1 }
            return
        L_0x18e1:
            r0 = move-exception
            goto L_0x18e7
        L_0x18e3:
            r0 = move-exception
            r3 = r1
            r28 = r7
        L_0x18e7:
            r1 = r0
            r4 = r9
            r14 = r28
        L_0x18eb:
            r2 = -1
            goto L_0x1908
        L_0x18ed:
            r0 = move-exception
            r3 = r1
            r28 = r7
            r1 = r0
            r4 = r9
            r14 = r28
            r2 = -1
            goto L_0x1907
        L_0x18f7:
            r0 = move-exception
            r3 = r1
            r28 = r7
            r1 = r0
            r14 = r28
            r2 = -1
            r4 = 0
            goto L_0x1907
        L_0x1901:
            r0 = move-exception
            r3 = r1
            r1 = r0
            r2 = -1
            r4 = 0
            r14 = 0
        L_0x1907:
            r15 = -1
        L_0x1908:
            if (r15 == r2) goto L_0x191a
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15)
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)
            r2.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r2 = r3.countDownLatch
            r2.countDown()
            goto L_0x191d
        L_0x191a:
            r40.onDecryptError()
        L_0x191d:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x193d
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
        L_0x193d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1940:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.GcmPushListenerService.lambda$null$3$GcmPushListenerService(java.util.Map, long):void");
    }

    static /* synthetic */ void lambda$null$1(int i) {
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
        AndroidUtilities.runOnUIThread(new Runnable(str) {
            public final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                GcmPushListenerService.lambda$onNewToken$5(this.f$0);
            }
        });
    }

    static /* synthetic */ void lambda$onNewToken$5(String str) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Refreshed token: " + str);
        }
        ApplicationLoader.postInitApplication();
        sendRegistrationToServer(str);
    }

    public static void sendRegistrationToServer(String str) {
        Utilities.stageQueue.postRunnable(new Runnable(str) {
            public final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                GcmPushListenerService.lambda$sendRegistrationToServer$7(this.f$0);
            }
        });
    }

    static /* synthetic */ void lambda$sendRegistrationToServer$7(String str) {
        ConnectionsManager.setRegId(str, SharedConfig.pushStringStatus);
        if (str != null) {
            SharedConfig.pushString = str;
            for (int i = 0; i < 3; i++) {
                UserConfig instance = UserConfig.getInstance(i);
                instance.registeredForPush = false;
                instance.saveConfig(false);
                if (instance.getClientUserId() != 0) {
                    AndroidUtilities.runOnUIThread(new Runnable(i, str) {
                        public final /* synthetic */ int f$0;
                        public final /* synthetic */ String f$1;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                        }

                        public final void run() {
                            MessagesController.getInstance(this.f$0).registerForPush(this.f$1);
                        }
                    });
                }
            }
        }
    }
}
