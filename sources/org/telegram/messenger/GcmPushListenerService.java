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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v4, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v11, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v27, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v69, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v41, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v16, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v20, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v131, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v69, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v73, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v77, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v82, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v175, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v153, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v178, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v187, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v191, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v192, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v196, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v199, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v174, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v101, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v202, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v177, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v205, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v108, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v208, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v211, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v214, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v217, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v220, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v225, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v200, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v206, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v134, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v208, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v210, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v138, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v212, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v214, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v141, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v216, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v143, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v147, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v149, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v158, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v226, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v166, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v167, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v169, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v170, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v172, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v166, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v173, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v174, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v175, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v169, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v177, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v178, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v172, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v279, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v181, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v252, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v286, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v258, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v260, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v190, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v262, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v264, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v196, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v266, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v198, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v190, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v196, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v274, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v198, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v200, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v201, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v206, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v207, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v208, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v209, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v210, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v212, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v214, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v215, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v219, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v6, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v254, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v255, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v256, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v261, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x01df, code lost:
        if (r2 == 0) goto L_0x1901;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x01e1, code lost:
        if (r2 == 1) goto L_0x18b8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x01e3, code lost:
        if (r2 == 2) goto L_0x18a4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x01e5, code lost:
        if (r2 == 3) goto L_0x1880;
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
        if (r3 == 0) goto L_0x184c;
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
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x184c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x0323, code lost:
        org.telegram.messenger.FileLog.d("GCM received " + r9 + " for dialogId = " + r3 + " mids = " + android.text.TextUtils.join(",", r7));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x0351, code lost:
        if (android.text.TextUtils.isEmpty(r9) != false) goto L_0x184c;
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
        r3 = r43;
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
        if (r1 == false) goto L_0x1846;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x03ef, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:?, code lost:
        r1 = r11.optInt("chat_from_id", 0);
        r12 = r11.optInt("chat_from_broadcast_id", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x03f9, code lost:
        r29 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x03fd, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:?, code lost:
        r15 = r11.optInt("chat_from_group_id", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x0401, code lost:
        if (r1 != 0) goto L_0x040a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x0403, code lost:
        if (r15 == 0) goto L_0x0406;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x0406, code lost:
        r19 = r1;
        r6 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x040a, code lost:
        r19 = r1;
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x0413, code lost:
        if (r11.has("mention") == false) goto L_0x042c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x041b, code lost:
        if (r11.getInt("mention") == 0) goto L_0x042c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x041d, code lost:
        r20 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x0420, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x0421, code lost:
        r2 = -1;
        r3 = r43;
        r1 = r0;
        r4 = r9;
        r14 = r28;
        r15 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x042c, code lost:
        r20 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:275:0x0434, code lost:
        if (r11.has("silent") == false) goto L_0x0441;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x043c, code lost:
        if (r11.getInt("silent") == 0) goto L_0x0441;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x043e, code lost:
        r25 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x0441, code lost:
        r25 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x0449, code lost:
        if (r5.has("loc_args") == false) goto L_0x0469;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:?, code lost:
        r1 = r5.getJSONArray("loc_args");
        r5 = r1.length();
        r26 = r12;
        r12 = new java.lang.String[r5];
        r27 = r15;
        r15 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x045c, code lost:
        if (r15 >= r5) goto L_0x0467;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x045e, code lost:
        r12[r15] = r1.getString(r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x0464, code lost:
        r15 = r15 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x0467, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x0469, code lost:
        r26 = r12;
        r27 = r15;
        r1 = 0;
        r12 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:?, code lost:
        r5 = r12[r1];
        r1 = r11.has("edit_date");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x047d, code lost:
        if (r9.startsWith("CHAT_") == false) goto L_0x04ae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x0483, code lost:
        if (org.telegram.messenger.UserObject.isReplyUser(r3) == false) goto L_0x049d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x0485, code lost:
        r5 = r5 + " @ " + r12[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x049d, code lost:
        if (r2 == 0) goto L_0x04a1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x049f, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x04a1, code lost:
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x04a5, code lost:
        r31 = r11;
        r15 = false;
        r11 = r5;
        r5 = r12[1];
        r30 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x04b4, code lost:
        if (r9.startsWith("PINNED_") == false) goto L_0x04bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x04b6, code lost:
        r31 = r6;
        r11 = null;
        r15 = false;
        r30 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x04c3, code lost:
        if (r9.startsWith("CHANNEL_") == false) goto L_0x04c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x04c5, code lost:
        r11 = null;
        r15 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x04c8, code lost:
        r11 = null;
        r15 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x04ca, code lost:
        r30 = false;
        r31 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x04d0, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x04f9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x04d2, code lost:
        r32 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:?, code lost:
        r5 = new java.lang.StringBuilder();
        r33 = r1;
        r5.append("GCM received message notification ");
        r5.append(r9);
        r5.append(" for dialogId = ");
        r5.append(r3);
        r5.append(" mid = ");
        r5.append(r7);
        org.telegram.messenger.FileLog.d(r5.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x04f9, code lost:
        r33 = r1;
        r32 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x0501, code lost:
        switch(r9.hashCode()) {
            case -2100047043: goto L_0x09b2;
            case -2091498420: goto L_0x09a7;
            case -2053872415: goto L_0x099c;
            case -2039746363: goto L_0x0991;
            case -2023218804: goto L_0x0986;
            case -1979538588: goto L_0x097b;
            case -1979536003: goto L_0x0970;
            case -1979535888: goto L_0x0965;
            case -1969004705: goto L_0x095a;
            case -1946699248: goto L_0x094e;
            case -1528047021: goto L_0x0942;
            case -1493579426: goto L_0x0936;
            case -1482481933: goto L_0x092a;
            case -1480102982: goto L_0x091f;
            case -1478041834: goto L_0x0913;
            case -1474543101: goto L_0x0908;
            case -1465695932: goto L_0x08fc;
            case -1374906292: goto L_0x08f0;
            case -1372940586: goto L_0x08e4;
            case -1264245338: goto L_0x08d8;
            case -1236086700: goto L_0x08cc;
            case -1236077786: goto L_0x08c0;
            case -1235796237: goto L_0x08b4;
            case -1235760759: goto L_0x08a8;
            case -1235686303: goto L_0x089d;
            case -1198046100: goto L_0x0892;
            case -1124254527: goto L_0x0886;
            case -1085137927: goto L_0x087a;
            case -1084856378: goto L_0x086e;
            case -1084820900: goto L_0x0862;
            case -1084746444: goto L_0x0856;
            case -819729482: goto L_0x084a;
            case -772141857: goto L_0x083e;
            case -638310039: goto L_0x0832;
            case -590403924: goto L_0x0826;
            case -589196239: goto L_0x081a;
            case -589193654: goto L_0x080e;
            case -589193539: goto L_0x0802;
            case -440169325: goto L_0x07f6;
            case -412748110: goto L_0x07ea;
            case -228518075: goto L_0x07de;
            case -213586509: goto L_0x07d2;
            case -115582002: goto L_0x07c6;
            case -112621464: goto L_0x07ba;
            case -108522133: goto L_0x07ae;
            case -107572034: goto L_0x07a3;
            case -40534265: goto L_0x0797;
            case 65254746: goto L_0x078b;
            case 141040782: goto L_0x077f;
            case 309993049: goto L_0x0773;
            case 309995634: goto L_0x0767;
            case 309995749: goto L_0x075b;
            case 320532812: goto L_0x074f;
            case 328933854: goto L_0x0743;
            case 331340546: goto L_0x0737;
            case 344816990: goto L_0x072b;
            case 346878138: goto L_0x071f;
            case 350376871: goto L_0x0713;
            case 615714517: goto L_0x0708;
            case 715508879: goto L_0x06fc;
            case 728985323: goto L_0x06f0;
            case 731046471: goto L_0x06e4;
            case 734545204: goto L_0x06d8;
            case 802032552: goto L_0x06cc;
            case 991498806: goto L_0x06c0;
            case 1007364121: goto L_0x06b4;
            case 1019917311: goto L_0x06a8;
            case 1019926225: goto L_0x069c;
            case 1020207774: goto L_0x0690;
            case 1020243252: goto L_0x0684;
            case 1020317708: goto L_0x0678;
            case 1060349560: goto L_0x066c;
            case 1060358474: goto L_0x0660;
            case 1060640023: goto L_0x0654;
            case 1060675501: goto L_0x0648;
            case 1060749957: goto L_0x063d;
            case 1073049781: goto L_0x0631;
            case 1078101399: goto L_0x0625;
            case 1110103437: goto L_0x0619;
            case 1160762272: goto L_0x060d;
            case 1172918249: goto L_0x0601;
            case 1234591620: goto L_0x05f5;
            case 1281128640: goto L_0x05e9;
            case 1281131225: goto L_0x05dd;
            case 1281131340: goto L_0x05d1;
            case 1310789062: goto L_0x05c6;
            case 1333118583: goto L_0x05ba;
            case 1361447897: goto L_0x05ae;
            case 1498266155: goto L_0x05a2;
            case 1533804208: goto L_0x0596;
            case 1547988151: goto L_0x058a;
            case 1561464595: goto L_0x057e;
            case 1563525743: goto L_0x0572;
            case 1567024476: goto L_0x0566;
            case 1810705077: goto L_0x055a;
            case 1815177512: goto L_0x054e;
            case 1963241394: goto L_0x0542;
            case 2014789757: goto L_0x0536;
            case 2022049433: goto L_0x052a;
            case 2048733346: goto L_0x051e;
            case 2099392181: goto L_0x0512;
            case 2140162142: goto L_0x0506;
            default: goto L_0x0504;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x050c, code lost:
        if (r9.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x050e, code lost:
        r1 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x0518, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x051a, code lost:
        r1 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x0524, code lost:
        if (r9.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x0526, code lost:
        r1 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x0530, code lost:
        if (r9.equals("PINNED_CONTACT") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x0532, code lost:
        r1 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x053c, code lost:
        if (r9.equals("CHAT_PHOTO_EDITED") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x053e, code lost:
        r1 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x0548, code lost:
        if (r9.equals("LOCKED_MESSAGE") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x054a, code lost:
        r1 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x0554, code lost:
        if (r9.equals("CHANNEL_MESSAGES") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x0556, code lost:
        r1 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x0560, code lost:
        if (r9.equals("MESSAGE_INVOICE") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x0562, code lost:
        r1 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x056c, code lost:
        if (r9.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x056e, code lost:
        r1 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x0578, code lost:
        if (r9.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x057a, code lost:
        r1 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x0584, code lost:
        if (r9.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x0586, code lost:
        r1 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x0590, code lost:
        if (r9.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x0592, code lost:
        r1 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x059c, code lost:
        if (r9.equals("MESSAGE_VIDEOS") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x059e, code lost:
        r1 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x05a8, code lost:
        if (r9.equals("PHONE_CALL_MISSED") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x05aa, code lost:
        r1 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x05b4, code lost:
        if (r9.equals("MESSAGE_PHOTOS") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x05b6, code lost:
        r1 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x05c0, code lost:
        if (r9.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x05c2, code lost:
        r1 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x05cc, code lost:
        if (r9.equals("MESSAGE_NOTEXT") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x05ce, code lost:
        r1 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x05d7, code lost:
        if (r9.equals("MESSAGE_GIF") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x05d9, code lost:
        r1 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x05e3, code lost:
        if (r9.equals("MESSAGE_GEO") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x05e5, code lost:
        r1 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x05ef, code lost:
        if (r9.equals("MESSAGE_DOC") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x05f1, code lost:
        r1 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x05fb, code lost:
        if (r9.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x05fd, code lost:
        r1 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x0607, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x0609, code lost:
        r1 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x0613, code lost:
        if (r9.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x0615, code lost:
        r1 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x061f, code lost:
        if (r9.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x0621, code lost:
        r1 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:400:0x062b, code lost:
        if (r9.equals("CHAT_TITLE_EDITED") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x062d, code lost:
        r1 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0637, code lost:
        if (r9.equals("PINNED_NOTEXT") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x0639, code lost:
        r1 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x0643, code lost:
        if (r9.equals("MESSAGE_TEXT") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x0645, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x064e, code lost:
        if (r9.equals("MESSAGE_QUIZ") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x0650, code lost:
        r1 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x065a, code lost:
        if (r9.equals("MESSAGE_POLL") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x065c, code lost:
        r1 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x0666, code lost:
        if (r9.equals("MESSAGE_GAME") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x0668, code lost:
        r1 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x0672, code lost:
        if (r9.equals("MESSAGE_FWDS") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x0674, code lost:
        r1 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x067e, code lost:
        if (r9.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x0680, code lost:
        r1 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x068a, code lost:
        if (r9.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x068c, code lost:
        r1 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x0696, code lost:
        if (r9.equals("CHAT_MESSAGE_POLL") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x0698, code lost:
        r1 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x06a2, code lost:
        if (r9.equals("CHAT_MESSAGE_GAME") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x06a4, code lost:
        r1 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x06ae, code lost:
        if (r9.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x06b0, code lost:
        r1 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x06ba, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x06bc, code lost:
        r1 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x06c6, code lost:
        if (r9.equals("PINNED_GEOLIVE") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x06c8, code lost:
        r1 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x06d2, code lost:
        if (r9.equals("MESSAGE_CONTACT") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x06d4, code lost:
        r1 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x06de, code lost:
        if (r9.equals("PINNED_VIDEO") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x06e0, code lost:
        r1 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x06ea, code lost:
        if (r9.equals("PINNED_ROUND") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x06ec, code lost:
        r1 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x06f6, code lost:
        if (r9.equals("PINNED_PHOTO") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x06f8, code lost:
        r1 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x0702, code lost:
        if (r9.equals("PINNED_AUDIO") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x0704, code lost:
        r1 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x070e, code lost:
        if (r9.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:458:0x0710, code lost:
        r1 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:460:0x0719, code lost:
        if (r9.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x071b, code lost:
        r1 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x0725, code lost:
        if (r9.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x0727, code lost:
        r1 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x0731, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x0733, code lost:
        r1 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x073d, code lost:
        if (r9.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x073f, code lost:
        r1 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:472:0x0749, code lost:
        if (r9.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x074b, code lost:
        r1 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0755, code lost:
        if (r9.equals("MESSAGES") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x0757, code lost:
        r1 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x0761, code lost:
        if (r9.equals("CHAT_MESSAGE_GIF") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0763, code lost:
        r1 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x076d, code lost:
        if (r9.equals("CHAT_MESSAGE_GEO") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x076f, code lost:
        r1 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x0779, code lost:
        if (r9.equals("CHAT_MESSAGE_DOC") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x077b, code lost:
        r1 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x0785, code lost:
        if (r9.equals("CHAT_LEFT") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x0787, code lost:
        r1 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x0791, code lost:
        if (r9.equals("CHAT_ADD_YOU") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0793, code lost:
        r1 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x079d, code lost:
        if (r9.equals("CHAT_DELETE_MEMBER") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x079f, code lost:
        r1 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x07a9, code lost:
        if (r9.equals("MESSAGE_SCREENSHOT") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x07ab, code lost:
        r1 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x07b4, code lost:
        if (r9.equals("AUTH_REGION") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x07b6, code lost:
        r1 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x07c0, code lost:
        if (r9.equals("CONTACT_JOINED") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x07c2, code lost:
        r1 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x07cc, code lost:
        if (r9.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x07ce, code lost:
        r1 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x07d8, code lost:
        if (r9.equals("ENCRYPTION_REQUEST") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x07da, code lost:
        r1 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x07e4, code lost:
        if (r9.equals("MESSAGE_GEOLIVE") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x07e6, code lost:
        r1 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x07f0, code lost:
        if (r9.equals("CHAT_DELETE_YOU") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x07f2, code lost:
        r1 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x07fc, code lost:
        if (r9.equals("AUTH_UNKNOWN") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x07fe, code lost:
        r1 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0808, code lost:
        if (r9.equals("PINNED_GIF") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x080a, code lost:
        r1 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x0814, code lost:
        if (r9.equals("PINNED_GEO") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0816, code lost:
        r1 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x0820, code lost:
        if (r9.equals("PINNED_DOC") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0822, code lost:
        r1 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x082c, code lost:
        if (r9.equals("PINNED_GAME_SCORE") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x082e, code lost:
        r1 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x0838, code lost:
        if (r9.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x083a, code lost:
        r1 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x0844, code lost:
        if (r9.equals("PHONE_CALL_REQUEST") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x0846, code lost:
        r1 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:538:0x0850, code lost:
        if (r9.equals("PINNED_STICKER") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x0852, code lost:
        r1 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x085c, code lost:
        if (r9.equals("PINNED_TEXT") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x085e, code lost:
        r1 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x0868, code lost:
        if (r9.equals("PINNED_QUIZ") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x086a, code lost:
        r1 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x0874, code lost:
        if (r9.equals("PINNED_POLL") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x0876, code lost:
        r1 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x0880, code lost:
        if (r9.equals("PINNED_GAME") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x0882, code lost:
        r1 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x088c, code lost:
        if (r9.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x088e, code lost:
        r1 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x0898, code lost:
        if (r9.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x089a, code lost:
        r1 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x08a3, code lost:
        if (r9.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x08a5, code lost:
        r1 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x08ae, code lost:
        if (r9.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x08b0, code lost:
        r1 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x08ba, code lost:
        if (r9.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x08bc, code lost:
        r1 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x08c6, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x08c8, code lost:
        r1 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x08d2, code lost:
        if (r9.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x08d4, code lost:
        r1 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x08de, code lost:
        if (r9.equals("PINNED_INVOICE") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x08e0, code lost:
        r1 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x08ea, code lost:
        if (r9.equals("CHAT_RETURNED") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x08ec, code lost:
        r1 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x08f6, code lost:
        if (r9.equals("ENCRYPTED_MESSAGE") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x08f8, code lost:
        r1 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x0902, code lost:
        if (r9.equals("ENCRYPTION_ACCEPT") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x0904, code lost:
        r1 = 'b';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x090e, code lost:
        if (r9.equals("MESSAGE_VIDEO") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x0910, code lost:
        r1 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x0919, code lost:
        if (r9.equals("MESSAGE_ROUND") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x091b, code lost:
        r1 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x0925, code lost:
        if (r9.equals("MESSAGE_PHOTO") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0927, code lost:
        r1 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x0930, code lost:
        if (r9.equals("MESSAGE_MUTED") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x0932, code lost:
        r1 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x093c, code lost:
        if (r9.equals("MESSAGE_AUDIO") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x093e, code lost:
        r1 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0948, code lost:
        if (r9.equals("CHAT_MESSAGES") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x094a, code lost:
        r1 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x0954, code lost:
        if (r9.equals("CHAT_JOINED") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0956, code lost:
        r1 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x0960, code lost:
        if (r9.equals("CHAT_ADD_MEMBER") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x0962, code lost:
        r1 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x096b, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x096d, code lost:
        r1 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x0976, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x0978, code lost:
        r1 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x0981, code lost:
        if (r9.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x0983, code lost:
        r1 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x098c, code lost:
        if (r9.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x098e, code lost:
        r1 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x0997, code lost:
        if (r9.equals("MESSAGE_STICKER") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x0999, code lost:
        r1 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:625:0x09a2, code lost:
        if (r9.equals("CHAT_CREATED") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x09a4, code lost:
        r1 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x09ad, code lost:
        if (r9.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x09af, code lost:
        r1 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x09b8, code lost:
        if (r9.equals("MESSAGE_GAME_SCORE") == false) goto L_0x09bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x09ba, code lost:
        r1 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x09bd, code lost:
        r1 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x09be, code lost:
        r18 = r7;
        r34 = r15;
        r35 = r11;
        r36 = r2;
        r37 = r3;
        r39 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x09de, code lost:
        switch(r1) {
            case 0: goto L_0x173b;
            case 1: goto L_0x173b;
            case 2: goto L_0x171a;
            case 3: goto L_0x16ff;
            case 4: goto L_0x16e2;
            case 5: goto L_0x16c7;
            case 6: goto L_0x16aa;
            case 7: goto L_0x1694;
            case 8: goto L_0x1678;
            case 9: goto L_0x165c;
            case 10: goto L_0x1601;
            case 11: goto L_0x15e3;
            case 12: goto L_0x15c0;
            case 13: goto L_0x159d;
            case 14: goto L_0x157a;
            case 15: goto L_0x155c;
            case 16: goto L_0x153e;
            case 17: goto L_0x1520;
            case 18: goto L_0x14fd;
            case 19: goto L_0x14de;
            case 20: goto L_0x14de;
            case 21: goto L_0x14bb;
            case 22: goto L_0x1496;
            case 23: goto L_0x1471;
            case 24: goto L_0x144c;
            case 25: goto L_0x1436;
            case 26: goto L_0x141a;
            case 27: goto L_0x13fe;
            case 28: goto L_0x13e2;
            case 29: goto L_0x13c6;
            case 30: goto L_0x13aa;
            case 31: goto L_0x134f;
            case 32: goto L_0x1331;
            case 33: goto L_0x130e;
            case 34: goto L_0x12eb;
            case 35: goto L_0x12c8;
            case 36: goto L_0x12aa;
            case 37: goto L_0x128c;
            case 38: goto L_0x126e;
            case 39: goto L_0x1250;
            case 40: goto L_0x1226;
            case 41: goto L_0x1202;
            case 42: goto L_0x11de;
            case 43: goto L_0x11c9;
            case 44: goto L_0x11a8;
            case 45: goto L_0x1187;
            case 46: goto L_0x1166;
            case 47: goto L_0x1145;
            case 48: goto L_0x1124;
            case 49: goto L_0x1103;
            case 50: goto L_0x108a;
            case 51: goto L_0x1067;
            case 52: goto L_0x103f;
            case 53: goto L_0x1017;
            case 54: goto L_0x0fef;
            case 55: goto L_0x0fcc;
            case 56: goto L_0x0fa9;
            case 57: goto L_0x0var_;
            case 58: goto L_0x0f5e;
            case 59: goto L_0x0f3a;
            case 60: goto L_0x0var_;
            case 61: goto L_0x0ef8;
            case 62: goto L_0x0ef8;
            case 63: goto L_0x0ede;
            case 64: goto L_0x0ec4;
            case 65: goto L_0x0ea5;
            case 66: goto L_0x0e8b;
            case 67: goto L_0x0e71;
            case 68: goto L_0x0e57;
            case 69: goto L_0x0e3d;
            case 70: goto L_0x0e23;
            case 71: goto L_0x0df8;
            case 72: goto L_0x0dcd;
            case 73: goto L_0x0da2;
            case 74: goto L_0x0d88;
            case 75: goto L_0x0d4f;
            case 76: goto L_0x0d20;
            case 77: goto L_0x0cf1;
            case 78: goto L_0x0cc2;
            case 79: goto L_0x0CLASSNAME;
            case 80: goto L_0x0CLASSNAME;
            case 81: goto L_0x0be8;
            case 82: goto L_0x0bb9;
            case 83: goto L_0x0b80;
            case 84: goto L_0x0b47;
            case 85: goto L_0x0b12;
            case 86: goto L_0x0ae3;
            case 87: goto L_0x0ab8;
            case 88: goto L_0x0a8d;
            case 89: goto L_0x0a60;
            case 90: goto L_0x0a33;
            case 91: goto L_0x0a06;
            case 92: goto L_0x09eb;
            case 93: goto L_0x09e7;
            case 94: goto L_0x09e7;
            case 95: goto L_0x09e7;
            case 96: goto L_0x09e7;
            case 97: goto L_0x09e7;
            case 98: goto L_0x09e7;
            case 99: goto L_0x09e7;
            case 100: goto L_0x09e7;
            case 101: goto L_0x09e7;
            default: goto L_0x09e1;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x09e1, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x09e7, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:?, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
        r32 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
        r3 = true;
        r4 = null;
        r2 = r1;
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0a06, code lost:
        if (r6 == false) goto L_0x0a20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0a08, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x0a20, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x0a33, code lost:
        if (r6 == false) goto L_0x0a4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x0a35, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x0a4d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0a60, code lost:
        if (r6 == false) goto L_0x0a7a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0a62, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x0a7a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0a8d, code lost:
        if (r6 == false) goto L_0x0aa6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0a8f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0aa6, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0ab8, code lost:
        if (r6 == false) goto L_0x0ad1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0aba, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0ad1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0ae3, code lost:
        if (r6 == false) goto L_0x0afc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0ae5, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0afc, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x0b0d, code lost:
        r2 = r1;
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0b12, code lost:
        if (r6 == false) goto L_0x0b30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x0b14, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0b30, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x0b47, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0b49, code lost:
        if (r6 == false) goto L_0x0b68;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x0b4b, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0b68, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0b80, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x0b82, code lost:
        if (r6 == false) goto L_0x0ba1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:671:0x0b84, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0ba1, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:673:0x0bb9, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0bbb, code lost:
        if (r6 == false) goto L_0x0bd5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0bbd, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0bd5, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x0be8, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0bea, code lost:
        if (r6 == false) goto L_0x0c2d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0bee, code lost:
        if (r12.length <= 2) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x0bf6, code lost:
        if (android.text.TextUtils.isEmpty(r12[2]) != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0bf8, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0CLASSNAME, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0c2f, code lost:
        if (r12.length <= 1) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x0CLASSNAME, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0CLASSNAME, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0CLASSNAME, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x0CLASSNAME, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0CLASSNAME, code lost:
        if (r6 == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0CLASSNAME, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0CLASSNAME, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0CLASSNAME, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0CLASSNAME, code lost:
        if (r6 == false) goto L_0x0caf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0CLASSNAME, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0caf, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0cc2, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0cc4, code lost:
        if (r6 == false) goto L_0x0cde;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0cc6, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0cde, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0cf1, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0cf3, code lost:
        if (r6 == false) goto L_0x0d0d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0cf5, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0d0d, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0d20, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0d22, code lost:
        if (r6 == false) goto L_0x0d3c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0d24, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0d3c, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0d4f, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0d51, code lost:
        if (r6 == false) goto L_0x0d70;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0d53, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0d70, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0d88, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0da2, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0dcd, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0df8, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0e23, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0e3d, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:0x0e57, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0e71, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0e8b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0ea5, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0ec4, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0ede, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:727:0x0ef8, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0var_, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r12[0], r12[1], r12[2]);
        r2 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0f3a, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r12[0], r12[1], r12[2], r12[3]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0f5e, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r12[0], r12[1], r12[2]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x0var_, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0fa9, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0fcc, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0fef, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r12[0], r12[1], r12[2]);
        r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x1017, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r12[0], r12[1], r12[2]);
        r2 = org.telegram.messenger.LocaleController.getString("PollQuiz", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x103f, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r12[0], r12[1], r12[2]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x1067, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x108a, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x108e, code lost:
        if (r12.length <= 2) goto L_0x10d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x1096, code lost:
        if (android.text.TextUtils.isEmpty(r12[2]) != false) goto L_0x10d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x1098, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r12[0], r12[1], r12[2]);
        r2 = r12[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x10d0, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r12[0], r12[1]);
        r2 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x1103, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x1124, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x1145, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x1166, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x1187, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x11a8, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r12[0], r12[1], r12[2]);
        r2 = r12[2];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x11c9, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x11de, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x1202, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:0x1226, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x1250, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x126e, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x128c, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x12aa, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x12c8, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x12eb, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x130e, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x1331, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x134f, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x1353, code lost:
        if (r12.length <= 1) goto L_0x1390;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:0x135b, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x1390;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x135d, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r12[0], r12[1]);
        r2 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x1390, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x13aa, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x13c6, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x13e2, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x13fe, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x141a, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x1436, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x1449, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x144c, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x1471, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x1496, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x14bb, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x14de, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x14fd, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x1520, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x153e, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x155c, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x157a, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x159d, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x15c0, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r12[0], r12[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x15e3, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x1601, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x1605, code lost:
        if (r12.length <= 1) goto L_0x1642;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x160d, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x1642;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x160f, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r12[0], r12[1]);
        r2 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x1642, code lost:
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x165c, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x1678, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x1694, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x16aa, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x16c7, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x16e2, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x16ff, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x171a, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r12[0]);
        r2 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x1734, code lost:
        r3 = false;
        r41 = r4;
        r4 = r2;
        r2 = r41;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x173b, code lost:
        r1 = r18;
        r4 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r12[0], r12[1]);
        r2 = r12[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x1756, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x176c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x1758, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x176c, code lost:
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x176d, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x176e, code lost:
        r4 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x176f, code lost:
        if (r2 == null) goto L_0x183b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:?, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_message();
        r5.id = r1;
        r5.random_id = r39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x177c, code lost:
        if (r4 == null) goto L_0x177f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x177f, code lost:
        r4 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x1780, code lost:
        r5.message = r4;
        r5.date = (int) (r45 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x1789, code lost:
        if (r30 == false) goto L_0x1792;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:?, code lost:
        r5.action = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x1792, code lost:
        if (r31 == false) goto L_0x179b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x1794, code lost:
        r5.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:?, code lost:
        r5.dialog_id = r37;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x179f, code lost:
        if (r36 == 0) goto L_0x17af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:?, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r5.peer_id = r1;
        r1.channel_id = r36;
        r8 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x17af, code lost:
        if (r24 == 0) goto L_0x17bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x17b1, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r5.peer_id = r1;
        r8 = r24;
        r1.chat_id = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x17bd, code lost:
        r8 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:?, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r5.peer_id = r1;
        r1.user_id = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x17ca, code lost:
        r5.flags |= 256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x17d0, code lost:
        if (r27 == 0) goto L_0x17dc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:?, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r5.from_id = r1;
        r1.chat_id = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x17dc, code lost:
        if (r26 == 0) goto L_0x17ea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x17de, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r5.from_id = r1;
        r1.channel_id = r26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x17ea, code lost:
        if (r19 == 0) goto L_0x17f8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x17ec, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r5.from_id = r1;
        r1.user_id = r19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:?, code lost:
        r5.from_id = r5.peer_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x17fc, code lost:
        if (r20 != false) goto L_0x1803;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x17fe, code lost:
        if (r30 == false) goto L_0x1801;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x1801, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x1803, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x1804, code lost:
        r5.mentioned = r1;
        r5.silent = r25;
        r5.from_scheduled = r22;
        r19 = new org.telegram.messenger.MessageObject(r29, r5, r2, r32, r35, r3, r34, r33);
        r2 = new java.util.ArrayList();
        r2.add(r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x1831, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x1833, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:?, code lost:
        org.telegram.messenger.NotificationsController.getInstance(r29).processNewMessages(r2, true, true, r3.countDownLatch);
        r8 = false;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:851:0x183b, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x183e, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x183f, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x1842, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x1843, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:856:0x1846, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x1849, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x184a, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x184c, code lost:
        r3 = r1;
        r28 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x184f, code lost:
        r29 = r15;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x1851, code lost:
        r8 = true;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:862:0x1852, code lost:
        if (r8 == false) goto L_0x1859;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:863:0x1854, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:864:0x1859, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r29);
        org.telegram.tgnet.ConnectionsManager.getInstance(r29).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:865:0x1865, code lost:
        r0 = th;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x186e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x186f, code lost:
        r3 = r1;
        r28 = r14;
        r29 = r15;
        r1 = r0;
        r4 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x1878, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x1879, code lost:
        r3 = r1;
        r28 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:871:0x187c, code lost:
        r29 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x1880, code lost:
        r3 = r1;
        r28 = r7;
        r29 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x1889, code lost:
        r15 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.$$Lambda$GcmPushListenerService$aCTiHH_b4RhBMfcCkAHdgReKcg4(r15));
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x1896, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x1897, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x189a, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:0x189b, code lost:
        r15 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x189f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x18a0, code lost:
        r15 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x18a4, code lost:
        r3 = r1;
        r28 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:?, code lost:
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ(r15));
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x18b4, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x18b5, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x18b8, code lost:
        r3 = r1;
        r28 = r7;
        r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification();
        r1.popup = false;
        r1.flags = 2;
        r1.inbox_date = (int) (r45 / 1000);
        r1.message = r5.getString("message");
        r1.type = "announcement";
        r1.media = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty();
        r2 = new org.telegram.tgnet.TLRPC$TL_updates();
        r2.updates.add(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo(r15, r2));
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x1900, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x1901, code lost:
        r3 = r1;
        r28 = r7;
        r1 = r11.getInt("dc");
        r2 = r11.getString("addr").split(":");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:0x1918, code lost:
        if (r2.length == 2) goto L_0x1920;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:0x191a, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x191f, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:906:0x1920, code lost:
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).applyDatacenterAddress(r1, r2[0], java.lang.Integer.parseInt(r2[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:0x193d, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x193e, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:935:?, code lost:
        return;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:880:0x188e, B:889:0x18a7] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:921:0x1967  */
    /* JADX WARNING: Removed duplicated region for block: B:922:0x1977  */
    /* JADX WARNING: Removed duplicated region for block: B:925:0x197e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$3$GcmPushListenerService(java.util.Map r44, long r45) {
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
            java.lang.Object r5 = r2.get(r5)     // Catch:{ all -> 0x195e }
            boolean r6 = r5 instanceof java.lang.String     // Catch:{ all -> 0x195e }
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
            goto L_0x1965
        L_0x002d:
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ all -> 0x195e }
            r6 = 8
            byte[] r5 = android.util.Base64.decode(r5, r6)     // Catch:{ all -> 0x195e }
            org.telegram.tgnet.NativeByteBuffer r7 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x195e }
            int r8 = r5.length     // Catch:{ all -> 0x195e }
            r7.<init>((int) r8)     // Catch:{ all -> 0x195e }
            r7.writeBytes((byte[]) r5)     // Catch:{ all -> 0x195e }
            r8 = 0
            r7.position(r8)     // Catch:{ all -> 0x195e }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x195e }
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
            byte[] r9 = new byte[r6]     // Catch:{ all -> 0x195e }
            r10 = 1
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x195e }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x195e }
            boolean r11 = java.util.Arrays.equals(r11, r9)     // Catch:{ all -> 0x195e }
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
            byte[] r9 = new byte[r9]     // Catch:{ all -> 0x195e }
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x195e }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x195e }
            org.telegram.messenger.MessageKeyData r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13)     // Catch:{ all -> 0x195e }
            java.nio.ByteBuffer r14 = r7.buffer     // Catch:{ all -> 0x195e }
            byte[] r15 = r11.aesKey     // Catch:{ all -> 0x195e }
            byte[] r11 = r11.aesIv     // Catch:{ all -> 0x195e }
            r17 = 0
            r18 = 0
            r19 = 24
            int r5 = r5.length     // Catch:{ all -> 0x195e }
            int r20 = r5 + -24
            r16 = r11
            org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x195e }
            byte[] r21 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x195e }
            r22 = 96
            r23 = 32
            java.nio.ByteBuffer r5 = r7.buffer     // Catch:{ all -> 0x195e }
            r25 = 24
            java.nio.ByteBuffer r11 = r7.buffer     // Catch:{ all -> 0x195e }
            int r26 = r11.limit()     // Catch:{ all -> 0x195e }
            r24 = r5
            byte[] r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26)     // Catch:{ all -> 0x195e }
            boolean r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6)     // Catch:{ all -> 0x195e }
            if (r5 != 0) goto L_0x00ea
            r43.onDecryptError()     // Catch:{ all -> 0x0024 }
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
            int r5 = r7.readInt32(r10)     // Catch:{ all -> 0x195e }
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x195e }
            r7.readBytes(r5, r10)     // Catch:{ all -> 0x195e }
            java.lang.String r7 = new java.lang.String     // Catch:{ all -> 0x195e }
            r7.<init>(r5)     // Catch:{ all -> 0x195e }
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ all -> 0x1954 }
            r5.<init>(r7)     // Catch:{ all -> 0x1954 }
            java.lang.String r9 = "loc_key"
            boolean r9 = r5.has(r9)     // Catch:{ all -> 0x1954 }
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
            java.lang.Object r11 = r5.get(r11)     // Catch:{ all -> 0x194a }
            boolean r11 = r11 instanceof org.json.JSONObject     // Catch:{ all -> 0x194a }
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
            org.json.JSONObject r11 = new org.json.JSONObject     // Catch:{ all -> 0x194a }
            r11.<init>()     // Catch:{ all -> 0x194a }
        L_0x0132:
            java.lang.String r14 = "user_id"
            boolean r14 = r5.has(r14)     // Catch:{ all -> 0x194a }
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
            boolean r15 = r14 instanceof java.lang.Integer     // Catch:{ all -> 0x194a }
            if (r15 == 0) goto L_0x015a
            java.lang.Integer r14 = (java.lang.Integer) r14     // Catch:{ all -> 0x0126 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0126 }
            goto L_0x0173
        L_0x015a:
            boolean r15 = r14 instanceof java.lang.String     // Catch:{ all -> 0x194a }
            if (r15 == 0) goto L_0x0169
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ all -> 0x0126 }
            java.lang.Integer r14 = org.telegram.messenger.Utilities.parseInt(r14)     // Catch:{ all -> 0x0126 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0126 }
            goto L_0x0173
        L_0x0169:
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x194a }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x194a }
            int r14 = r14.getClientUserId()     // Catch:{ all -> 0x194a }
        L_0x0173:
            int r15 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x194a }
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
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r15)     // Catch:{ all -> 0x1940 }
            boolean r4 = r4.isClientActivated()     // Catch:{ all -> 0x1940 }
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
            r2.get(r4)     // Catch:{ all -> 0x1940 }
            int r2 = r9.hashCode()     // Catch:{ all -> 0x1940 }
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
            if (r2 == 0) goto L_0x1901
            if (r2 == r10) goto L_0x18b8
            if (r2 == r13) goto L_0x18a4
            if (r2 == r12) goto L_0x1880
            java.lang.String r2 = "channel_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1878 }
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
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1878 }
            if (r14 == 0) goto L_0x0217
            java.lang.String r3 = "from_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x0213 }
            r14 = r7
            long r6 = (long) r3
            r41 = r6
            r6 = r3
            r3 = r41
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
            boolean r7 = r11.has(r7)     // Catch:{ all -> 0x186e }
            if (r7 == 0) goto L_0x0231
            java.lang.String r3 = "chat_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x022f }
            int r4 = -r3
            long r12 = (long) r4
            r41 = r12
            r12 = r3
            r3 = r41
            goto L_0x0232
        L_0x022f:
            r0 = move-exception
            goto L_0x0215
        L_0x0231:
            r12 = 0
        L_0x0232:
            java.lang.String r13 = "encryption_id"
            boolean r13 = r11.has(r13)     // Catch:{ all -> 0x186e }
            if (r13 == 0) goto L_0x0244
            java.lang.String r3 = "encryption_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x022f }
            long r3 = (long) r3
            r13 = 32
            long r3 = r3 << r13
        L_0x0244:
            java.lang.String r13 = "schedule"
            boolean r13 = r11.has(r13)     // Catch:{ all -> 0x186e }
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
            if (r7 == 0) goto L_0x184c
            java.lang.String r7 = "READ_HISTORY"
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x186e }
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
            goto L_0x184c
        L_0x02e0:
            java.lang.String r7 = "MESSAGE_DELETED"
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x186e }
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
            if (r2 == 0) goto L_0x184c
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
            goto L_0x184c
        L_0x034d:
            boolean r7 = android.text.TextUtils.isEmpty(r9)     // Catch:{ all -> 0x186e }
            if (r7 != 0) goto L_0x184c
            java.lang.String r7 = "msg_id"
            boolean r7 = r11.has(r7)     // Catch:{ all -> 0x186e }
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
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1849 }
            if (r14 == 0) goto L_0x038b
            java.lang.String r14 = "random_id"
            java.lang.String r14 = r11.getString(r14)     // Catch:{ all -> 0x0384 }
            java.lang.Long r14 = org.telegram.messenger.Utilities.parseLong(r14)     // Catch:{ all -> 0x0384 }
            long r22 = r14.longValue()     // Catch:{ all -> 0x0384 }
            r41 = r22
            r22 = r13
            r13 = r41
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
            r3 = r43
            r1 = r0
            r4 = r9
            r14 = r28
            goto L_0x1965
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
            if (r1 == 0) goto L_0x1846
            java.lang.String r1 = "chat_from_id"
            r6 = 0
            int r1 = r11.optInt(r1, r6)     // Catch:{ all -> 0x1842 }
            java.lang.String r12 = "chat_from_broadcast_id"
            int r12 = r11.optInt(r12, r6)     // Catch:{ all -> 0x1842 }
            r29 = r15
            java.lang.String r15 = "chat_from_group_id"
            int r15 = r11.optInt(r15, r6)     // Catch:{ all -> 0x183e }
            if (r1 != 0) goto L_0x040a
            if (r15 == 0) goto L_0x0406
            goto L_0x040a
        L_0x0406:
            r19 = r1
            r6 = 0
            goto L_0x040d
        L_0x040a:
            r19 = r1
            r6 = 1
        L_0x040d:
            java.lang.String r1 = "mention"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x183e }
            if (r1 == 0) goto L_0x042c
            java.lang.String r1 = "mention"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x042c
            r20 = 1
            goto L_0x042e
        L_0x0420:
            r0 = move-exception
            r2 = -1
            r3 = r43
            r1 = r0
            r4 = r9
            r14 = r28
            r15 = r29
            goto L_0x1965
        L_0x042c:
            r20 = 0
        L_0x042e:
            java.lang.String r1 = "silent"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x183e }
            if (r1 == 0) goto L_0x0441
            java.lang.String r1 = "silent"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0441
            r25 = 1
            goto L_0x0443
        L_0x0441:
            r25 = 0
        L_0x0443:
            java.lang.String r1 = "loc_args"
            boolean r1 = r5.has(r1)     // Catch:{ all -> 0x183e }
            if (r1 == 0) goto L_0x0469
            java.lang.String r1 = "loc_args"
            org.json.JSONArray r1 = r5.getJSONArray(r1)     // Catch:{ all -> 0x0420 }
            int r5 = r1.length()     // Catch:{ all -> 0x0420 }
            r26 = r12
            java.lang.String[] r12 = new java.lang.String[r5]     // Catch:{ all -> 0x0420 }
            r27 = r15
            r15 = 0
        L_0x045c:
            if (r15 >= r5) goto L_0x0467
            java.lang.String r30 = r1.getString(r15)     // Catch:{ all -> 0x0420 }
            r12[r15] = r30     // Catch:{ all -> 0x0420 }
            int r15 = r15 + 1
            goto L_0x045c
        L_0x0467:
            r1 = 0
            goto L_0x046f
        L_0x0469:
            r26 = r12
            r27 = r15
            r1 = 0
            r12 = 0
        L_0x046f:
            r5 = r12[r1]     // Catch:{ all -> 0x183e }
            java.lang.String r1 = "edit_date"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x183e }
            java.lang.String r11 = "CHAT_"
            boolean r11 = r9.startsWith(r11)     // Catch:{ all -> 0x183e }
            if (r11 == 0) goto L_0x04ae
            boolean r11 = org.telegram.messenger.UserObject.isReplyUser((long) r3)     // Catch:{ all -> 0x0420 }
            if (r11 == 0) goto L_0x049d
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x0420 }
            r11.<init>()     // Catch:{ all -> 0x0420 }
            r11.append(r5)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = " @ "
            r11.append(r5)     // Catch:{ all -> 0x0420 }
            r5 = 1
            r15 = r12[r5]     // Catch:{ all -> 0x0420 }
            r11.append(r15)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = r11.toString()     // Catch:{ all -> 0x0420 }
            goto L_0x04c8
        L_0x049d:
            if (r2 == 0) goto L_0x04a1
            r11 = 1
            goto L_0x04a2
        L_0x04a1:
            r11 = 0
        L_0x04a2:
            r15 = 1
            r30 = r12[r15]     // Catch:{ all -> 0x0420 }
            r31 = r11
            r15 = 0
            r11 = r5
            r5 = r30
            r30 = 0
            goto L_0x04ce
        L_0x04ae:
            java.lang.String r11 = "PINNED_"
            boolean r11 = r9.startsWith(r11)     // Catch:{ all -> 0x183e }
            if (r11 == 0) goto L_0x04bd
            r31 = r6
            r11 = 0
            r15 = 0
            r30 = 1
            goto L_0x04ce
        L_0x04bd:
            java.lang.String r11 = "CHANNEL_"
            boolean r11 = r9.startsWith(r11)     // Catch:{ all -> 0x183e }
            if (r11 == 0) goto L_0x04c8
            r11 = 0
            r15 = 1
            goto L_0x04ca
        L_0x04c8:
            r11 = 0
            r15 = 0
        L_0x04ca:
            r30 = 0
            r31 = 0
        L_0x04ce:
            boolean r32 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x183e }
            if (r32 == 0) goto L_0x04f9
            r32 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0420 }
            r5.<init>()     // Catch:{ all -> 0x0420 }
            r33 = r1
            java.lang.String r1 = "GCM received message notification "
            r5.append(r1)     // Catch:{ all -> 0x0420 }
            r5.append(r9)     // Catch:{ all -> 0x0420 }
            r5.append(r10)     // Catch:{ all -> 0x0420 }
            r5.append(r3)     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = " mid = "
            r5.append(r1)     // Catch:{ all -> 0x0420 }
            r5.append(r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = r5.toString()     // Catch:{ all -> 0x0420 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x0420 }
            goto L_0x04fd
        L_0x04f9:
            r33 = r1
            r32 = r5
        L_0x04fd:
            int r1 = r9.hashCode()     // Catch:{ all -> 0x183e }
            switch(r1) {
                case -2100047043: goto L_0x09b2;
                case -2091498420: goto L_0x09a7;
                case -2053872415: goto L_0x099c;
                case -2039746363: goto L_0x0991;
                case -2023218804: goto L_0x0986;
                case -1979538588: goto L_0x097b;
                case -1979536003: goto L_0x0970;
                case -1979535888: goto L_0x0965;
                case -1969004705: goto L_0x095a;
                case -1946699248: goto L_0x094e;
                case -1528047021: goto L_0x0942;
                case -1493579426: goto L_0x0936;
                case -1482481933: goto L_0x092a;
                case -1480102982: goto L_0x091f;
                case -1478041834: goto L_0x0913;
                case -1474543101: goto L_0x0908;
                case -1465695932: goto L_0x08fc;
                case -1374906292: goto L_0x08f0;
                case -1372940586: goto L_0x08e4;
                case -1264245338: goto L_0x08d8;
                case -1236086700: goto L_0x08cc;
                case -1236077786: goto L_0x08c0;
                case -1235796237: goto L_0x08b4;
                case -1235760759: goto L_0x08a8;
                case -1235686303: goto L_0x089d;
                case -1198046100: goto L_0x0892;
                case -1124254527: goto L_0x0886;
                case -1085137927: goto L_0x087a;
                case -1084856378: goto L_0x086e;
                case -1084820900: goto L_0x0862;
                case -1084746444: goto L_0x0856;
                case -819729482: goto L_0x084a;
                case -772141857: goto L_0x083e;
                case -638310039: goto L_0x0832;
                case -590403924: goto L_0x0826;
                case -589196239: goto L_0x081a;
                case -589193654: goto L_0x080e;
                case -589193539: goto L_0x0802;
                case -440169325: goto L_0x07f6;
                case -412748110: goto L_0x07ea;
                case -228518075: goto L_0x07de;
                case -213586509: goto L_0x07d2;
                case -115582002: goto L_0x07c6;
                case -112621464: goto L_0x07ba;
                case -108522133: goto L_0x07ae;
                case -107572034: goto L_0x07a3;
                case -40534265: goto L_0x0797;
                case 65254746: goto L_0x078b;
                case 141040782: goto L_0x077f;
                case 309993049: goto L_0x0773;
                case 309995634: goto L_0x0767;
                case 309995749: goto L_0x075b;
                case 320532812: goto L_0x074f;
                case 328933854: goto L_0x0743;
                case 331340546: goto L_0x0737;
                case 344816990: goto L_0x072b;
                case 346878138: goto L_0x071f;
                case 350376871: goto L_0x0713;
                case 615714517: goto L_0x0708;
                case 715508879: goto L_0x06fc;
                case 728985323: goto L_0x06f0;
                case 731046471: goto L_0x06e4;
                case 734545204: goto L_0x06d8;
                case 802032552: goto L_0x06cc;
                case 991498806: goto L_0x06c0;
                case 1007364121: goto L_0x06b4;
                case 1019917311: goto L_0x06a8;
                case 1019926225: goto L_0x069c;
                case 1020207774: goto L_0x0690;
                case 1020243252: goto L_0x0684;
                case 1020317708: goto L_0x0678;
                case 1060349560: goto L_0x066c;
                case 1060358474: goto L_0x0660;
                case 1060640023: goto L_0x0654;
                case 1060675501: goto L_0x0648;
                case 1060749957: goto L_0x063d;
                case 1073049781: goto L_0x0631;
                case 1078101399: goto L_0x0625;
                case 1110103437: goto L_0x0619;
                case 1160762272: goto L_0x060d;
                case 1172918249: goto L_0x0601;
                case 1234591620: goto L_0x05f5;
                case 1281128640: goto L_0x05e9;
                case 1281131225: goto L_0x05dd;
                case 1281131340: goto L_0x05d1;
                case 1310789062: goto L_0x05c6;
                case 1333118583: goto L_0x05ba;
                case 1361447897: goto L_0x05ae;
                case 1498266155: goto L_0x05a2;
                case 1533804208: goto L_0x0596;
                case 1547988151: goto L_0x058a;
                case 1561464595: goto L_0x057e;
                case 1563525743: goto L_0x0572;
                case 1567024476: goto L_0x0566;
                case 1810705077: goto L_0x055a;
                case 1815177512: goto L_0x054e;
                case 1963241394: goto L_0x0542;
                case 2014789757: goto L_0x0536;
                case 2022049433: goto L_0x052a;
                case 2048733346: goto L_0x051e;
                case 2099392181: goto L_0x0512;
                case 2140162142: goto L_0x0506;
                default: goto L_0x0504;
            }
        L_0x0504:
            goto L_0x09bd
        L_0x0506:
            java.lang.String r1 = "CHAT_MESSAGE_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 56
            goto L_0x09be
        L_0x0512:
            java.lang.String r1 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 41
            goto L_0x09be
        L_0x051e:
            java.lang.String r1 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 26
            goto L_0x09be
        L_0x052a:
            java.lang.String r1 = "PINNED_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 83
            goto L_0x09be
        L_0x0536:
            java.lang.String r1 = "CHAT_PHOTO_EDITED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 64
            goto L_0x09be
        L_0x0542:
            java.lang.String r1 = "LOCKED_MESSAGE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 96
            goto L_0x09be
        L_0x054e:
            java.lang.String r1 = "CHANNEL_MESSAGES"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 43
            goto L_0x09be
        L_0x055a:
            java.lang.String r1 = "MESSAGE_INVOICE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 21
            goto L_0x09be
        L_0x0566:
            java.lang.String r1 = "CHAT_MESSAGE_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 47
            goto L_0x09be
        L_0x0572:
            java.lang.String r1 = "CHAT_MESSAGE_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 48
            goto L_0x09be
        L_0x057e:
            java.lang.String r1 = "CHAT_MESSAGE_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 46
            goto L_0x09be
        L_0x058a:
            java.lang.String r1 = "CHAT_MESSAGE_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 51
            goto L_0x09be
        L_0x0596:
            java.lang.String r1 = "MESSAGE_VIDEOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 24
            goto L_0x09be
        L_0x05a2:
            java.lang.String r1 = "PHONE_CALL_MISSED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 101(0x65, float:1.42E-43)
            goto L_0x09be
        L_0x05ae:
            java.lang.String r1 = "MESSAGE_PHOTOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 23
            goto L_0x09be
        L_0x05ba:
            java.lang.String r1 = "CHAT_MESSAGE_VIDEOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 73
            goto L_0x09be
        L_0x05c6:
            java.lang.String r1 = "MESSAGE_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 2
            goto L_0x09be
        L_0x05d1:
            java.lang.String r1 = "MESSAGE_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 17
            goto L_0x09be
        L_0x05dd:
            java.lang.String r1 = "MESSAGE_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 15
            goto L_0x09be
        L_0x05e9:
            java.lang.String r1 = "MESSAGE_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 9
            goto L_0x09be
        L_0x05f5:
            java.lang.String r1 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 59
            goto L_0x09be
        L_0x0601:
            java.lang.String r1 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 37
            goto L_0x09be
        L_0x060d:
            java.lang.String r1 = "CHAT_MESSAGE_PHOTOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 72
            goto L_0x09be
        L_0x0619:
            java.lang.String r1 = "CHAT_MESSAGE_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 45
            goto L_0x09be
        L_0x0625:
            java.lang.String r1 = "CHAT_TITLE_EDITED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 63
            goto L_0x09be
        L_0x0631:
            java.lang.String r1 = "PINNED_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 76
            goto L_0x09be
        L_0x063d:
            java.lang.String r1 = "MESSAGE_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 0
            goto L_0x09be
        L_0x0648:
            java.lang.String r1 = "MESSAGE_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 13
            goto L_0x09be
        L_0x0654:
            java.lang.String r1 = "MESSAGE_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 14
            goto L_0x09be
        L_0x0660:
            java.lang.String r1 = "MESSAGE_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 18
            goto L_0x09be
        L_0x066c:
            java.lang.String r1 = "MESSAGE_FWDS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 22
            goto L_0x09be
        L_0x0678:
            java.lang.String r1 = "CHAT_MESSAGE_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 44
            goto L_0x09be
        L_0x0684:
            java.lang.String r1 = "CHAT_MESSAGE_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 53
            goto L_0x09be
        L_0x0690:
            java.lang.String r1 = "CHAT_MESSAGE_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 54
            goto L_0x09be
        L_0x069c:
            java.lang.String r1 = "CHAT_MESSAGE_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 58
            goto L_0x09be
        L_0x06a8:
            java.lang.String r1 = "CHAT_MESSAGE_FWDS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 71
            goto L_0x09be
        L_0x06b4:
            java.lang.String r1 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 20
            goto L_0x09be
        L_0x06c0:
            java.lang.String r1 = "PINNED_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 87
            goto L_0x09be
        L_0x06cc:
            java.lang.String r1 = "MESSAGE_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 12
            goto L_0x09be
        L_0x06d8:
            java.lang.String r1 = "PINNED_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 78
            goto L_0x09be
        L_0x06e4:
            java.lang.String r1 = "PINNED_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 79
            goto L_0x09be
        L_0x06f0:
            java.lang.String r1 = "PINNED_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 77
            goto L_0x09be
        L_0x06fc:
            java.lang.String r1 = "PINNED_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 82
            goto L_0x09be
        L_0x0708:
            java.lang.String r1 = "MESSAGE_PHOTO_SECRET"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 4
            goto L_0x09be
        L_0x0713:
            java.lang.String r1 = "CHANNEL_MESSAGE_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 28
            goto L_0x09be
        L_0x071f:
            java.lang.String r1 = "CHANNEL_MESSAGE_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 29
            goto L_0x09be
        L_0x072b:
            java.lang.String r1 = "CHANNEL_MESSAGE_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 27
            goto L_0x09be
        L_0x0737:
            java.lang.String r1 = "CHANNEL_MESSAGE_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 32
            goto L_0x09be
        L_0x0743:
            java.lang.String r1 = "CHAT_MESSAGE_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 50
            goto L_0x09be
        L_0x074f:
            java.lang.String r1 = "MESSAGES"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 25
            goto L_0x09be
        L_0x075b:
            java.lang.String r1 = "CHAT_MESSAGE_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 57
            goto L_0x09be
        L_0x0767:
            java.lang.String r1 = "CHAT_MESSAGE_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 55
            goto L_0x09be
        L_0x0773:
            java.lang.String r1 = "CHAT_MESSAGE_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 49
            goto L_0x09be
        L_0x077f:
            java.lang.String r1 = "CHAT_LEFT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 68
            goto L_0x09be
        L_0x078b:
            java.lang.String r1 = "CHAT_ADD_YOU"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 62
            goto L_0x09be
        L_0x0797:
            java.lang.String r1 = "CHAT_DELETE_MEMBER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 66
            goto L_0x09be
        L_0x07a3:
            java.lang.String r1 = "MESSAGE_SCREENSHOT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 7
            goto L_0x09be
        L_0x07ae:
            java.lang.String r1 = "AUTH_REGION"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 95
            goto L_0x09be
        L_0x07ba:
            java.lang.String r1 = "CONTACT_JOINED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 93
            goto L_0x09be
        L_0x07c6:
            java.lang.String r1 = "CHAT_MESSAGE_INVOICE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 60
            goto L_0x09be
        L_0x07d2:
            java.lang.String r1 = "ENCRYPTION_REQUEST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 97
            goto L_0x09be
        L_0x07de:
            java.lang.String r1 = "MESSAGE_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 16
            goto L_0x09be
        L_0x07ea:
            java.lang.String r1 = "CHAT_DELETE_YOU"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 67
            goto L_0x09be
        L_0x07f6:
            java.lang.String r1 = "AUTH_UNKNOWN"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 94
            goto L_0x09be
        L_0x0802:
            java.lang.String r1 = "PINNED_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 91
            goto L_0x09be
        L_0x080e:
            java.lang.String r1 = "PINNED_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 86
            goto L_0x09be
        L_0x081a:
            java.lang.String r1 = "PINNED_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 80
            goto L_0x09be
        L_0x0826:
            java.lang.String r1 = "PINNED_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 89
            goto L_0x09be
        L_0x0832:
            java.lang.String r1 = "CHANNEL_MESSAGE_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 31
            goto L_0x09be
        L_0x083e:
            java.lang.String r1 = "PHONE_CALL_REQUEST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 99
            goto L_0x09be
        L_0x084a:
            java.lang.String r1 = "PINNED_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 81
            goto L_0x09be
        L_0x0856:
            java.lang.String r1 = "PINNED_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 75
            goto L_0x09be
        L_0x0862:
            java.lang.String r1 = "PINNED_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 84
            goto L_0x09be
        L_0x086e:
            java.lang.String r1 = "PINNED_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 85
            goto L_0x09be
        L_0x087a:
            java.lang.String r1 = "PINNED_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 88
            goto L_0x09be
        L_0x0886:
            java.lang.String r1 = "CHAT_MESSAGE_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 52
            goto L_0x09be
        L_0x0892:
            java.lang.String r1 = "MESSAGE_VIDEO_SECRET"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 6
            goto L_0x09be
        L_0x089d:
            java.lang.String r1 = "CHANNEL_MESSAGE_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 1
            goto L_0x09be
        L_0x08a8:
            java.lang.String r1 = "CHANNEL_MESSAGE_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 34
            goto L_0x09be
        L_0x08b4:
            java.lang.String r1 = "CHANNEL_MESSAGE_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 35
            goto L_0x09be
        L_0x08c0:
            java.lang.String r1 = "CHANNEL_MESSAGE_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 39
            goto L_0x09be
        L_0x08cc:
            java.lang.String r1 = "CHANNEL_MESSAGE_FWDS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 40
            goto L_0x09be
        L_0x08d8:
            java.lang.String r1 = "PINNED_INVOICE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 90
            goto L_0x09be
        L_0x08e4:
            java.lang.String r1 = "CHAT_RETURNED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 69
            goto L_0x09be
        L_0x08f0:
            java.lang.String r1 = "ENCRYPTED_MESSAGE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 92
            goto L_0x09be
        L_0x08fc:
            java.lang.String r1 = "ENCRYPTION_ACCEPT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 98
            goto L_0x09be
        L_0x0908:
            java.lang.String r1 = "MESSAGE_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 5
            goto L_0x09be
        L_0x0913:
            java.lang.String r1 = "MESSAGE_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 8
            goto L_0x09be
        L_0x091f:
            java.lang.String r1 = "MESSAGE_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 3
            goto L_0x09be
        L_0x092a:
            java.lang.String r1 = "MESSAGE_MUTED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 100
            goto L_0x09be
        L_0x0936:
            java.lang.String r1 = "MESSAGE_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 11
            goto L_0x09be
        L_0x0942:
            java.lang.String r1 = "CHAT_MESSAGES"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 74
            goto L_0x09be
        L_0x094e:
            java.lang.String r1 = "CHAT_JOINED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 70
            goto L_0x09be
        L_0x095a:
            java.lang.String r1 = "CHAT_ADD_MEMBER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 65
            goto L_0x09be
        L_0x0965:
            java.lang.String r1 = "CHANNEL_MESSAGE_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 38
            goto L_0x09be
        L_0x0970:
            java.lang.String r1 = "CHANNEL_MESSAGE_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 36
            goto L_0x09be
        L_0x097b:
            java.lang.String r1 = "CHANNEL_MESSAGE_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 30
            goto L_0x09be
        L_0x0986:
            java.lang.String r1 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 42
            goto L_0x09be
        L_0x0991:
            java.lang.String r1 = "MESSAGE_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 10
            goto L_0x09be
        L_0x099c:
            java.lang.String r1 = "CHAT_CREATED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 61
            goto L_0x09be
        L_0x09a7:
            java.lang.String r1 = "CHANNEL_MESSAGE_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 33
            goto L_0x09be
        L_0x09b2:
            java.lang.String r1 = "MESSAGE_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x09bd
            r1 = 19
            goto L_0x09be
        L_0x09bd:
            r1 = -1
        L_0x09be:
            java.lang.String r5 = "AttachDocument"
            java.lang.String r10 = "AttachRound"
            r18 = r7
            java.lang.String r7 = "AttachVideo"
            r34 = r15
            java.lang.String r15 = "AttachPhoto"
            r35 = r11
            java.lang.String r11 = "Message"
            r36 = r2
            java.lang.String r2 = "Videos"
            r37 = r3
            java.lang.String r3 = "Photos"
            java.lang.String r4 = "ChannelMessageFew"
            r39 = r13
            java.lang.String r13 = " "
            java.lang.String r14 = "AttachSticker"
            switch(r1) {
                case 0: goto L_0x173b;
                case 1: goto L_0x173b;
                case 2: goto L_0x171a;
                case 3: goto L_0x16ff;
                case 4: goto L_0x16e2;
                case 5: goto L_0x16c7;
                case 6: goto L_0x16aa;
                case 7: goto L_0x1694;
                case 8: goto L_0x1678;
                case 9: goto L_0x165c;
                case 10: goto L_0x1601;
                case 11: goto L_0x15e3;
                case 12: goto L_0x15c0;
                case 13: goto L_0x159d;
                case 14: goto L_0x157a;
                case 15: goto L_0x155c;
                case 16: goto L_0x153e;
                case 17: goto L_0x1520;
                case 18: goto L_0x14fd;
                case 19: goto L_0x14de;
                case 20: goto L_0x14de;
                case 21: goto L_0x14bb;
                case 22: goto L_0x1496;
                case 23: goto L_0x1471;
                case 24: goto L_0x144c;
                case 25: goto L_0x1436;
                case 26: goto L_0x141a;
                case 27: goto L_0x13fe;
                case 28: goto L_0x13e2;
                case 29: goto L_0x13c6;
                case 30: goto L_0x13aa;
                case 31: goto L_0x134f;
                case 32: goto L_0x1331;
                case 33: goto L_0x130e;
                case 34: goto L_0x12eb;
                case 35: goto L_0x12c8;
                case 36: goto L_0x12aa;
                case 37: goto L_0x128c;
                case 38: goto L_0x126e;
                case 39: goto L_0x1250;
                case 40: goto L_0x1226;
                case 41: goto L_0x1202;
                case 42: goto L_0x11de;
                case 43: goto L_0x11c9;
                case 44: goto L_0x11a8;
                case 45: goto L_0x1187;
                case 46: goto L_0x1166;
                case 47: goto L_0x1145;
                case 48: goto L_0x1124;
                case 49: goto L_0x1103;
                case 50: goto L_0x108a;
                case 51: goto L_0x1067;
                case 52: goto L_0x103f;
                case 53: goto L_0x1017;
                case 54: goto L_0x0fef;
                case 55: goto L_0x0fcc;
                case 56: goto L_0x0fa9;
                case 57: goto L_0x0var_;
                case 58: goto L_0x0f5e;
                case 59: goto L_0x0f3a;
                case 60: goto L_0x0var_;
                case 61: goto L_0x0ef8;
                case 62: goto L_0x0ef8;
                case 63: goto L_0x0ede;
                case 64: goto L_0x0ec4;
                case 65: goto L_0x0ea5;
                case 66: goto L_0x0e8b;
                case 67: goto L_0x0e71;
                case 68: goto L_0x0e57;
                case 69: goto L_0x0e3d;
                case 70: goto L_0x0e23;
                case 71: goto L_0x0df8;
                case 72: goto L_0x0dcd;
                case 73: goto L_0x0da2;
                case 74: goto L_0x0d88;
                case 75: goto L_0x0d4f;
                case 76: goto L_0x0d20;
                case 77: goto L_0x0cf1;
                case 78: goto L_0x0cc2;
                case 79: goto L_0x0CLASSNAME;
                case 80: goto L_0x0CLASSNAME;
                case 81: goto L_0x0be8;
                case 82: goto L_0x0bb9;
                case 83: goto L_0x0b80;
                case 84: goto L_0x0b47;
                case 85: goto L_0x0b12;
                case 86: goto L_0x0ae3;
                case 87: goto L_0x0ab8;
                case 88: goto L_0x0a8d;
                case 89: goto L_0x0a60;
                case 90: goto L_0x0a33;
                case 91: goto L_0x0a06;
                case 92: goto L_0x09eb;
                case 93: goto L_0x09e7;
                case 94: goto L_0x09e7;
                case 95: goto L_0x09e7;
                case 96: goto L_0x09e7;
                case 97: goto L_0x09e7;
                case 98: goto L_0x09e7;
                case 99: goto L_0x09e7;
                case 100: goto L_0x09e7;
                case 101: goto L_0x09e7;
                default: goto L_0x09e1;
            }
        L_0x09e1:
            r1 = r18
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x183e }
            goto L_0x1756
        L_0x09e7:
            r1 = r18
            goto L_0x176c
        L_0x09eb:
            java.lang.String r1 = "YouHaveNewMessage"
            r2 = 2131627605(0x7f0e0e55, float:1.888248E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "SecretChatName"
            r3 = 2131626891(0x7f0e0b8b, float:1.888103E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            r32 = r2
            r3 = 1
            r4 = 0
            r2 = r1
            r1 = r18
            goto L_0x176f
        L_0x0a06:
            if (r6 == 0) goto L_0x0a20
            java.lang.String r1 = "NotificationActionPinnedGif"
            r2 = 2131626040(0x7f0e0838, float:1.8879305E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r5 = r12[r4]     // Catch:{ all -> 0x0420 }
            r3[r4] = r5     // Catch:{ all -> 0x0420 }
            r4 = 1
            r5 = r12[r4]     // Catch:{ all -> 0x0420 }
            r3[r4] = r5     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x0b0d
        L_0x0a20:
            java.lang.String r1 = "NotificationActionPinnedGifChannel"
            r2 = 2131626041(0x7f0e0839, float:1.8879307E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x0420 }
            r3 = 0
            r5 = r12[r3]     // Catch:{ all -> 0x0420 }
            r4[r3] = r5     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x0b0d
        L_0x0a33:
            if (r6 == 0) goto L_0x0a4d
            java.lang.String r1 = "NotificationActionPinnedInvoice"
            r2 = 2131626042(0x7f0e083a, float:1.887931E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r5 = r12[r4]     // Catch:{ all -> 0x0420 }
            r3[r4] = r5     // Catch:{ all -> 0x0420 }
            r4 = 1
            r5 = r12[r4]     // Catch:{ all -> 0x0420 }
            r3[r4] = r5     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x0b0d
        L_0x0a4d:
            java.lang.String r1 = "NotificationActionPinnedInvoiceChannel"
            r2 = 2131626043(0x7f0e083b, float:1.8879311E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x0420 }
            r3 = 0
            r5 = r12[r3]     // Catch:{ all -> 0x0420 }
            r4[r3] = r5     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x0b0d
        L_0x0a60:
            if (r6 == 0) goto L_0x0a7a
            java.lang.String r1 = "NotificationActionPinnedGameScore"
            r2 = 2131626034(0x7f0e0832, float:1.8879293E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r5 = r12[r4]     // Catch:{ all -> 0x0420 }
            r3[r4] = r5     // Catch:{ all -> 0x0420 }
            r4 = 1
            r5 = r12[r4]     // Catch:{ all -> 0x0420 }
            r3[r4] = r5     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x0b0d
        L_0x0a7a:
            java.lang.String r1 = "NotificationActionPinnedGameScoreChannel"
            r2 = 2131626035(0x7f0e0833, float:1.8879295E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x0420 }
            r3 = 0
            r5 = r12[r3]     // Catch:{ all -> 0x0420 }
            r4[r3] = r5     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x0b0d
        L_0x0a8d:
            if (r6 == 0) goto L_0x0aa6
            java.lang.String r1 = "NotificationActionPinnedGame"
            r2 = 2131626032(0x7f0e0830, float:1.8879289E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r5 = r12[r4]     // Catch:{ all -> 0x0420 }
            r3[r4] = r5     // Catch:{ all -> 0x0420 }
            r4 = 1
            r5 = r12[r4]     // Catch:{ all -> 0x0420 }
            r3[r4] = r5     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x0b0d
        L_0x0aa6:
            java.lang.String r1 = "NotificationActionPinnedGameChannel"
            r2 = 2131626033(0x7f0e0831, float:1.887929E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x0420 }
            r3 = 0
            r5 = r12[r3]     // Catch:{ all -> 0x0420 }
            r4[r3] = r5     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x0b0d
        L_0x0ab8:
            if (r6 == 0) goto L_0x0ad1
            java.lang.String r1 = "NotificationActionPinnedGeoLive"
            r2 = 2131626038(0x7f0e0836, float:1.88793E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r5 = r12[r4]     // Catch:{ all -> 0x0420 }
            r3[r4] = r5     // Catch:{ all -> 0x0420 }
            r4 = 1
            r5 = r12[r4]     // Catch:{ all -> 0x0420 }
            r3[r4] = r5     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x0b0d
        L_0x0ad1:
            java.lang.String r1 = "NotificationActionPinnedGeoLiveChannel"
            r2 = 2131626039(0x7f0e0837, float:1.8879303E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x0420 }
            r3 = 0
            r5 = r12[r3]     // Catch:{ all -> 0x0420 }
            r4[r3] = r5     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x0b0d
        L_0x0ae3:
            if (r6 == 0) goto L_0x0afc
            java.lang.String r1 = "NotificationActionPinnedGeo"
            r2 = 2131626036(0x7f0e0834, float:1.8879297E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r5 = r12[r4]     // Catch:{ all -> 0x0420 }
            r3[r4] = r5     // Catch:{ all -> 0x0420 }
            r4 = 1
            r5 = r12[r4]     // Catch:{ all -> 0x0420 }
            r3[r4] = r5     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x0b0d
        L_0x0afc:
            java.lang.String r1 = "NotificationActionPinnedGeoChannel"
            r2 = 2131626037(0x7f0e0835, float:1.8879299E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ all -> 0x0420 }
            r3 = 0
            r5 = r12[r3]     // Catch:{ all -> 0x0420 }
            r4[r3] = r5     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)     // Catch:{ all -> 0x0420 }
        L_0x0b0d:
            r2 = r1
            r1 = r18
            goto L_0x176d
        L_0x0b12:
            if (r6 == 0) goto L_0x0b30
            java.lang.String r1 = "NotificationActionPinnedPoll2"
            r2 = 2131626050(0x7f0e0842, float:1.8879325E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r5 = r12[r4]     // Catch:{ all -> 0x0420 }
            r3[r4] = r5     // Catch:{ all -> 0x0420 }
            r4 = 2
            r5 = r12[r4]     // Catch:{ all -> 0x0420 }
            r6 = 1
            r3[r6] = r5     // Catch:{ all -> 0x0420 }
            r5 = r12[r6]     // Catch:{ all -> 0x0420 }
            r3[r4] = r5     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x0b0d
        L_0x0b30:
            java.lang.String r1 = "NotificationActionPinnedPollChannel2"
            r2 = 2131626051(0x7f0e0843, float:1.8879327E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r5 = r12[r4]     // Catch:{ all -> 0x0420 }
            r3[r4] = r5     // Catch:{ all -> 0x0420 }
            r4 = 1
            r5 = r12[r4]     // Catch:{ all -> 0x0420 }
            r3[r4] = r5     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x0b0d
        L_0x0b47:
            r1 = r18
            if (r6 == 0) goto L_0x0b68
            java.lang.String r2 = "NotificationActionPinnedQuiz2"
            r3 = 2131626052(0x7f0e0844, float:1.887933E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r7 = 1
            r4[r7] = r6     // Catch:{ all -> 0x0420 }
            r6 = r12[r7]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0b68:
            java.lang.String r2 = "NotificationActionPinnedQuizChannel2"
            r3 = 2131626053(0x7f0e0845, float:1.8879331E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0b80:
            r1 = r18
            if (r6 == 0) goto L_0x0ba1
            java.lang.String r2 = "NotificationActionPinnedContact2"
            r3 = 2131626028(0x7f0e082c, float:1.887928E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r7 = 1
            r4[r7] = r6     // Catch:{ all -> 0x0420 }
            r6 = r12[r7]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0ba1:
            java.lang.String r2 = "NotificationActionPinnedContactChannel2"
            r3 = 2131626029(0x7f0e082d, float:1.8879283E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0bb9:
            r1 = r18
            if (r6 == 0) goto L_0x0bd5
            java.lang.String r2 = "NotificationActionPinnedVoice"
            r3 = 2131626064(0x7f0e0850, float:1.8879354E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0bd5:
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            r3 = 2131626065(0x7f0e0851, float:1.8879356E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0be8:
            r1 = r18
            if (r6 == 0) goto L_0x0c2d
            int r2 = r12.length     // Catch:{ all -> 0x0420 }
            r3 = 2
            if (r2 <= r3) goto L_0x0CLASSNAME
            r2 = r12[r3]     // Catch:{ all -> 0x0420 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0420 }
            if (r2 != 0) goto L_0x0CLASSNAME
            java.lang.String r2 = "NotificationActionPinnedStickerEmoji"
            r3 = 2131626058(0x7f0e084a, float:1.8879341E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r7 = 1
            r4[r7] = r6     // Catch:{ all -> 0x0420 }
            r6 = r12[r7]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0CLASSNAME:
            java.lang.String r2 = "NotificationActionPinnedSticker"
            r3 = 2131626056(0x7f0e0848, float:1.8879337E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0c2d:
            int r2 = r12.length     // Catch:{ all -> 0x0420 }
            r3 = 1
            if (r2 <= r3) goto L_0x0CLASSNAME
            r2 = r12[r3]     // Catch:{ all -> 0x0420 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0420 }
            if (r2 != 0) goto L_0x0CLASSNAME
            java.lang.String r2 = "NotificationActionPinnedStickerEmojiChannel"
            r3 = 2131626059(0x7f0e084b, float:1.8879343E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0CLASSNAME:
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            r3 = 2131626057(0x7f0e0849, float:1.887934E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0CLASSNAME:
            r1 = r18
            if (r6 == 0) goto L_0x0CLASSNAME
            java.lang.String r2 = "NotificationActionPinnedFile"
            r3 = 2131626030(0x7f0e082e, float:1.8879285E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0CLASSNAME:
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            r3 = 2131626031(0x7f0e082f, float:1.8879287E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0CLASSNAME:
            r1 = r18
            if (r6 == 0) goto L_0x0caf
            java.lang.String r2 = "NotificationActionPinnedRound"
            r3 = 2131626054(0x7f0e0846, float:1.8879333E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0caf:
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            r3 = 2131626055(0x7f0e0847, float:1.8879335E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0cc2:
            r1 = r18
            if (r6 == 0) goto L_0x0cde
            java.lang.String r2 = "NotificationActionPinnedVideo"
            r3 = 2131626062(0x7f0e084e, float:1.887935E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0cde:
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            r3 = 2131626063(0x7f0e084f, float:1.8879352E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0cf1:
            r1 = r18
            if (r6 == 0) goto L_0x0d0d
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            r3 = 2131626048(0x7f0e0840, float:1.8879321E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0d0d:
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            r3 = 2131626049(0x7f0e0841, float:1.8879323E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0d20:
            r1 = r18
            if (r6 == 0) goto L_0x0d3c
            java.lang.String r2 = "NotificationActionPinnedNoText"
            r3 = 2131626046(0x7f0e083e, float:1.8879317E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0d3c:
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            r3 = 2131626047(0x7f0e083f, float:1.887932E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0d4f:
            r1 = r18
            if (r6 == 0) goto L_0x0d70
            java.lang.String r2 = "NotificationActionPinnedText"
            r3 = 2131626060(0x7f0e084c, float:1.8879346E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0d70:
            java.lang.String r2 = "NotificationActionPinnedTextChannel"
            r3 = 2131626061(0x7f0e084d, float:1.8879348E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0d88:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAlbum"
            r3 = 2131626074(0x7f0e085a, float:1.8879374E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x1449
        L_0x0da2:
            r1 = r18
            java.lang.String r3 = "NotificationGroupFew"
            r4 = 2131626075(0x7f0e085b, float:1.8879376E38)
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 2
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0420 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r7)     // Catch:{ all -> 0x0420 }
            r5[r6] = r2     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r4, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x1449
        L_0x0dcd:
            r1 = r18
            java.lang.String r2 = "NotificationGroupFew"
            r4 = 2131626075(0x7f0e085b, float:1.8879376E38)
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 2
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0420 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r7)     // Catch:{ all -> 0x0420 }
            r5[r6] = r3     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r4, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x1449
        L_0x0df8:
            r1 = r18
            java.lang.String r2 = "NotificationGroupForwardedFew"
            r3 = 2131626076(0x7f0e085c, float:1.8879378E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x0420 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r8, r6)     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x1449
        L_0x0e23:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            r3 = 2131626073(0x7f0e0859, float:1.8879372E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0e3d:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAddSelf"
            r3 = 2131626072(0x7f0e0858, float:1.887937E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0e57:
            r1 = r18
            java.lang.String r2 = "NotificationGroupLeftMember"
            r3 = 2131626079(0x7f0e085f, float:1.8879384E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0e71:
            r1 = r18
            java.lang.String r2 = "NotificationGroupKickYou"
            r3 = 2131626078(0x7f0e085e, float:1.8879382E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0e8b:
            r1 = r18
            java.lang.String r2 = "NotificationGroupKickMember"
            r3 = 2131626077(0x7f0e085d, float:1.887938E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0ea5:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAddMember"
            r3 = 2131626071(0x7f0e0857, float:1.8879368E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0ec4:
            r1 = r18
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            r3 = 2131626069(0x7f0e0855, float:1.8879364E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0ede:
            r1 = r18
            java.lang.String r2 = "NotificationEditedGroupName"
            r3 = 2131626068(0x7f0e0854, float:1.8879362E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0ef8:
            r1 = r18
            java.lang.String r2 = "NotificationInvitedToGroup"
            r3 = 2131626084(0x7f0e0864, float:1.8879394E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0var_:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupInvoice"
            r3 = 2131626101(0x7f0e0875, float:1.8879429E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "PaymentInvoice"
            r3 = 2131626477(0x7f0e09ed, float:1.8880191E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x0f3a:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupGameScored"
            r3 = 2131626099(0x7f0e0873, float:1.8879425E38)
            r4 = 4
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 3
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x0f5e:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupGame"
            r3 = 2131626098(0x7f0e0872, float:1.8879423E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachGame"
            r3 = 2131624339(0x7f0e0193, float:1.8875855E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x0var_:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupGif"
            r3 = 2131626100(0x7f0e0874, float:1.8879427E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachGif"
            r3 = 2131624340(0x7f0e0194, float:1.8875857E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x0fa9:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupLiveLocation"
            r3 = 2131626102(0x7f0e0876, float:1.887943E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachLiveLocation"
            r3 = 2131624345(0x7f0e0199, float:1.8875867E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x0fcc:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupMap"
            r3 = 2131626103(0x7f0e0877, float:1.8879433E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachLocation"
            r3 = 2131624347(0x7f0e019b, float:1.8875871E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x0fef:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupPoll2"
            r3 = 2131626107(0x7f0e087b, float:1.887944E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "Poll"
            r3 = 2131626592(0x7f0e0a60, float:1.8880425E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x1017:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupQuiz2"
            r3 = 2131626108(0x7f0e087c, float:1.8879443E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "PollQuiz"
            r3 = 2131626599(0x7f0e0a67, float:1.8880439E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x103f:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupContact2"
            r3 = 2131626096(0x7f0e0870, float:1.8879419E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachContact"
            r3 = 2131624335(0x7f0e018f, float:1.8875847E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x1067:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupAudio"
            r3 = 2131626095(0x7f0e086f, float:1.8879417E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachAudio"
            r3 = 2131624333(0x7f0e018d, float:1.8875843E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x108a:
            r1 = r18
            int r2 = r12.length     // Catch:{ all -> 0x0420 }
            r3 = 2
            if (r2 <= r3) goto L_0x10d0
            r2 = r12[r3]     // Catch:{ all -> 0x0420 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0420 }
            if (r2 != 0) goto L_0x10d0
            java.lang.String r2 = "NotificationMessageGroupStickerEmoji"
            r3 = 2131626111(0x7f0e087f, float:1.8879449E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0420 }
            r2.<init>()     // Catch:{ all -> 0x0420 }
            r3 = r12[r5]     // Catch:{ all -> 0x0420 }
            r2.append(r3)     // Catch:{ all -> 0x0420 }
            r2.append(r13)     // Catch:{ all -> 0x0420 }
            r3 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)     // Catch:{ all -> 0x0420 }
            r2.append(r3)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x10d0:
            java.lang.String r2 = "NotificationMessageGroupSticker"
            r3 = 2131626110(0x7f0e087e, float:1.8879447E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0420 }
            r2.<init>()     // Catch:{ all -> 0x0420 }
            r3 = r12[r5]     // Catch:{ all -> 0x0420 }
            r2.append(r3)     // Catch:{ all -> 0x0420 }
            r2.append(r13)     // Catch:{ all -> 0x0420 }
            r3 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)     // Catch:{ all -> 0x0420 }
            r2.append(r3)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x1103:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupDocument"
            r3 = 2131626097(0x7f0e0871, float:1.887942E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r4[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r4[r6] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            r2 = 2131624338(0x7f0e0192, float:1.8875853E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x1124:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupRound"
            r3 = 2131626109(0x7f0e087d, float:1.8879445E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            r2 = 2131624353(0x7f0e01a1, float:1.8875883E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x1145:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupVideo"
            r3 = 2131626113(0x7f0e0881, float:1.8879453E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            r2 = 2131624357(0x7f0e01a5, float:1.8875891E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x1166:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupPhoto"
            r3 = 2131626106(0x7f0e087a, float:1.8879439E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            r2 = 2131624351(0x7f0e019f, float:1.887588E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r15, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x1187:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupNoText"
            r3 = 2131626105(0x7f0e0879, float:1.8879437E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            r2 = 2131625845(0x7f0e0775, float:1.887891E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r11, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x11a8:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupText"
            r3 = 2131626112(0x7f0e0880, float:1.887945E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            r2 = r12[r5]     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x11c9:
            r1 = r18
            java.lang.String r2 = "ChannelMessageAlbum"
            r3 = 2131624640(0x7f0e02c0, float:1.8876465E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x1449
        L_0x11de:
            r1 = r18
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r3[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x0420 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r6)     // Catch:{ all -> 0x0420 }
            r3[r5] = r2     // Catch:{ all -> 0x0420 }
            r2 = 2131624644(0x7f0e02c4, float:1.8876474E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1449
        L_0x1202:
            r1 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r2[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x0420 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r6)     // Catch:{ all -> 0x0420 }
            r2[r5] = r3     // Catch:{ all -> 0x0420 }
            r3 = 2131624644(0x7f0e02c4, float:1.8876474E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r3, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1449
        L_0x1226:
            r1 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0420 }
            r3 = 0
            r5 = r12[r3]     // Catch:{ all -> 0x0420 }
            r2[r3] = r5     // Catch:{ all -> 0x0420 }
            java.lang.String r3 = "ForwardedMessageCount"
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x0420 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r3 = r3.toLowerCase()     // Catch:{ all -> 0x0420 }
            r2[r5] = r3     // Catch:{ all -> 0x0420 }
            r3 = 2131624644(0x7f0e02c4, float:1.8876474E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r3, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1449
        L_0x1250:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGame"
            r3 = 2131626092(0x7f0e086c, float:1.887941E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachGame"
            r3 = 2131624339(0x7f0e0193, float:1.8875855E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x126e:
            r1 = r18
            java.lang.String r2 = "ChannelMessageGIF"
            r3 = 2131624645(0x7f0e02c5, float:1.8876476E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachGif"
            r3 = 2131624340(0x7f0e0194, float:1.8875857E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x128c:
            r1 = r18
            java.lang.String r2 = "ChannelMessageLiveLocation"
            r3 = 2131624646(0x7f0e02c6, float:1.8876478E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachLiveLocation"
            r3 = 2131624345(0x7f0e0199, float:1.8875867E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x12aa:
            r1 = r18
            java.lang.String r2 = "ChannelMessageMap"
            r3 = 2131624647(0x7f0e02c7, float:1.887648E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachLocation"
            r3 = 2131624347(0x7f0e019b, float:1.8875871E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x12c8:
            r1 = r18
            java.lang.String r2 = "ChannelMessagePoll2"
            r3 = 2131624651(0x7f0e02cb, float:1.8876488E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "Poll"
            r3 = 2131626592(0x7f0e0a60, float:1.8880425E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x12eb:
            r1 = r18
            java.lang.String r2 = "ChannelMessageQuiz2"
            r3 = 2131624652(0x7f0e02cc, float:1.887649E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "QuizPoll"
            r3 = 2131626684(0x7f0e0abc, float:1.8880611E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x130e:
            r1 = r18
            java.lang.String r2 = "ChannelMessageContact2"
            r3 = 2131624642(0x7f0e02c2, float:1.887647E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachContact"
            r3 = 2131624335(0x7f0e018f, float:1.8875847E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x1331:
            r1 = r18
            java.lang.String r2 = "ChannelMessageAudio"
            r3 = 2131624641(0x7f0e02c1, float:1.8876467E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachAudio"
            r3 = 2131624333(0x7f0e018d, float:1.8875843E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x134f:
            r1 = r18
            int r2 = r12.length     // Catch:{ all -> 0x0420 }
            r3 = 1
            if (r2 <= r3) goto L_0x1390
            r2 = r12[r3]     // Catch:{ all -> 0x0420 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0420 }
            if (r2 != 0) goto L_0x1390
            java.lang.String r2 = "ChannelMessageStickerEmoji"
            r3 = 2131624655(0x7f0e02cf, float:1.8876496E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0420 }
            r2.<init>()     // Catch:{ all -> 0x0420 }
            r3 = r12[r5]     // Catch:{ all -> 0x0420 }
            r2.append(r3)     // Catch:{ all -> 0x0420 }
            r2.append(r13)     // Catch:{ all -> 0x0420 }
            r3 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)     // Catch:{ all -> 0x0420 }
            r2.append(r3)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x1390:
            java.lang.String r2 = "ChannelMessageSticker"
            r3 = 2131624654(0x7f0e02ce, float:1.8876494E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            r2 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r14, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x13aa:
            r1 = r18
            java.lang.String r2 = "ChannelMessageDocument"
            r3 = 2131624643(0x7f0e02c3, float:1.8876472E38)
            r4 = 1
            java.lang.Object[] r6 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x0420 }
            r6[r4] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r6)     // Catch:{ all -> 0x0420 }
            r2 = 2131624338(0x7f0e0192, float:1.8875853E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x13c6:
            r1 = r18
            java.lang.String r2 = "ChannelMessageRound"
            r3 = 2131624653(0x7f0e02cd, float:1.8876492E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            r2 = 2131624353(0x7f0e01a1, float:1.8875883E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x13e2:
            r1 = r18
            java.lang.String r2 = "ChannelMessageVideo"
            r3 = 2131624656(0x7f0e02d0, float:1.8876498E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            r2 = 2131624357(0x7f0e01a5, float:1.8875891E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x13fe:
            r1 = r18
            java.lang.String r2 = "ChannelMessagePhoto"
            r3 = 2131624650(0x7f0e02ca, float:1.8876486E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            r2 = 2131624351(0x7f0e019f, float:1.887588E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r15, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x141a:
            r1 = r18
            java.lang.String r2 = "ChannelMessageNoText"
            r3 = 2131624649(0x7f0e02c9, float:1.8876484E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            r2 = 2131625845(0x7f0e0775, float:1.887891E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r11, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x1436:
            r1 = r18
            java.lang.String r2 = "NotificationMessageAlbum"
            r3 = 2131626086(0x7f0e0866, float:1.8879398E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
        L_0x1449:
            r3 = 1
            goto L_0x176e
        L_0x144c:
            r1 = r18
            java.lang.String r3 = "NotificationMessageFew"
            r4 = 2131626090(0x7f0e086a, float:1.8879406E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0420 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r7)     // Catch:{ all -> 0x0420 }
            r5[r6] = r2     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r4, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x1449
        L_0x1471:
            r1 = r18
            java.lang.String r2 = "NotificationMessageFew"
            r4 = 2131626090(0x7f0e086a, float:1.8879406E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0420 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r7)     // Catch:{ all -> 0x0420 }
            r5[r6] = r3     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r4, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x1449
        L_0x1496:
            r1 = r18
            java.lang.String r2 = "NotificationMessageForwardFew"
            r3 = 2131626091(0x7f0e086b, float:1.8879408E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x0420 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r8, r6)     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x1449
        L_0x14bb:
            r1 = r18
            java.lang.String r2 = "NotificationMessageInvoice"
            r3 = 2131626114(0x7f0e0882, float:1.8879455E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "PaymentInvoice"
            r3 = 2131626477(0x7f0e09ed, float:1.8880191E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x14de:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGameScored"
            r3 = 2131626093(0x7f0e086d, float:1.8879412E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x14fd:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGame"
            r3 = 2131626092(0x7f0e086c, float:1.887941E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachGame"
            r3 = 2131624339(0x7f0e0193, float:1.8875855E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x1520:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGif"
            r3 = 2131626094(0x7f0e086e, float:1.8879414E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachGif"
            r3 = 2131624340(0x7f0e0194, float:1.8875857E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x153e:
            r1 = r18
            java.lang.String r2 = "NotificationMessageLiveLocation"
            r3 = 2131626115(0x7f0e0883, float:1.8879457E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachLiveLocation"
            r3 = 2131624345(0x7f0e0199, float:1.8875867E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x155c:
            r1 = r18
            java.lang.String r2 = "NotificationMessageMap"
            r3 = 2131626116(0x7f0e0884, float:1.887946E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachLocation"
            r3 = 2131624347(0x7f0e019b, float:1.8875871E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x157a:
            r1 = r18
            java.lang.String r2 = "NotificationMessagePoll2"
            r3 = 2131626120(0x7f0e0888, float:1.8879467E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "Poll"
            r3 = 2131626592(0x7f0e0a60, float:1.8880425E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x159d:
            r1 = r18
            java.lang.String r2 = "NotificationMessageQuiz2"
            r3 = 2131626121(0x7f0e0889, float:1.887947E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "QuizPoll"
            r3 = 2131626684(0x7f0e0abc, float:1.8880611E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x15c0:
            r1 = r18
            java.lang.String r2 = "NotificationMessageContact2"
            r3 = 2131626088(0x7f0e0868, float:1.8879402E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachContact"
            r3 = 2131624335(0x7f0e018f, float:1.8875847E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x15e3:
            r1 = r18
            java.lang.String r2 = "NotificationMessageAudio"
            r3 = 2131626087(0x7f0e0867, float:1.88794E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachAudio"
            r3 = 2131624333(0x7f0e018d, float:1.8875843E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x1601:
            r1 = r18
            int r2 = r12.length     // Catch:{ all -> 0x0420 }
            r3 = 1
            if (r2 <= r3) goto L_0x1642
            r2 = r12[r3]     // Catch:{ all -> 0x0420 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0420 }
            if (r2 != 0) goto L_0x1642
            java.lang.String r2 = "NotificationMessageStickerEmoji"
            r3 = 2131626128(0x7f0e0890, float:1.8879483E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0420 }
            r2.<init>()     // Catch:{ all -> 0x0420 }
            r3 = r12[r5]     // Catch:{ all -> 0x0420 }
            r2.append(r3)     // Catch:{ all -> 0x0420 }
            r2.append(r13)     // Catch:{ all -> 0x0420 }
            r3 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)     // Catch:{ all -> 0x0420 }
            r2.append(r3)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x1642:
            java.lang.String r2 = "NotificationMessageSticker"
            r3 = 2131626127(0x7f0e088f, float:1.8879481E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            r2 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r14, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x165c:
            r1 = r18
            java.lang.String r2 = "NotificationMessageDocument"
            r3 = 2131626089(0x7f0e0869, float:1.8879404E38)
            r4 = 1
            java.lang.Object[] r6 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r7 = r12[r4]     // Catch:{ all -> 0x0420 }
            r6[r4] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r6)     // Catch:{ all -> 0x0420 }
            r2 = 2131624338(0x7f0e0192, float:1.8875853E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x1678:
            r1 = r18
            java.lang.String r2 = "NotificationMessageRound"
            r3 = 2131626122(0x7f0e088a, float:1.8879471E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            r2 = 2131624353(0x7f0e01a1, float:1.8875883E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x1694:
            r1 = r18
            java.lang.String r2 = "ActionTakeScreenshoot"
            r3 = 2131624128(0x7f0e00c0, float:1.8875427E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            java.lang.String r3 = "un1"
            r4 = 0
            r5 = r12[r4]     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = r2.replace(r3, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x176d
        L_0x16aa:
            r1 = r18
            java.lang.String r2 = "NotificationMessageSDVideo"
            r3 = 2131626124(0x7f0e088c, float:1.8879475E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachDestructingVideo"
            r3 = 2131624337(0x7f0e0191, float:1.887585E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x16c7:
            r1 = r18
            java.lang.String r2 = "NotificationMessageVideo"
            r3 = 2131626130(0x7f0e0892, float:1.8879488E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            r2 = 2131624357(0x7f0e01a5, float:1.8875891E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x16e2:
            r1 = r18
            java.lang.String r2 = "NotificationMessageSDPhoto"
            r3 = 2131626123(0x7f0e088b, float:1.8879473E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "AttachDestructingPhoto"
            r3 = 2131624336(0x7f0e0190, float:1.8875849E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x16ff:
            r1 = r18
            java.lang.String r2 = "NotificationMessagePhoto"
            r3 = 2131626119(0x7f0e0887, float:1.8879465E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            r2 = 2131624351(0x7f0e019f, float:1.887588E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r15, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x171a:
            r1 = r18
            java.lang.String r2 = "NotificationMessageNoText"
            r3 = 2131626118(0x7f0e0886, float:1.8879463E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r4 = 0
            r6 = r12[r4]     // Catch:{ all -> 0x0420 }
            r5[r4] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5)     // Catch:{ all -> 0x0420 }
            r2 = 2131625845(0x7f0e0775, float:1.887891E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r11, r2)     // Catch:{ all -> 0x0420 }
        L_0x1734:
            r3 = 0
            r41 = r4
            r4 = r2
            r2 = r41
            goto L_0x176f
        L_0x173b:
            r1 = r18
            java.lang.String r2 = "NotificationMessageText"
            r3 = 2131626129(0x7f0e0891, float:1.8879485E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r4[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)     // Catch:{ all -> 0x0420 }
            r2 = r12[r5]     // Catch:{ all -> 0x0420 }
            goto L_0x1734
        L_0x1756:
            if (r2 == 0) goto L_0x176c
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0420 }
            r2.<init>()     // Catch:{ all -> 0x0420 }
            java.lang.String r3 = "unhandled loc_key = "
            r2.append(r3)     // Catch:{ all -> 0x0420 }
            r2.append(r9)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0420 }
            org.telegram.messenger.FileLog.w(r2)     // Catch:{ all -> 0x0420 }
        L_0x176c:
            r2 = 0
        L_0x176d:
            r3 = 0
        L_0x176e:
            r4 = 0
        L_0x176f:
            if (r2 == 0) goto L_0x183b
            org.telegram.tgnet.TLRPC$TL_message r5 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x183e }
            r5.<init>()     // Catch:{ all -> 0x183e }
            r5.id = r1     // Catch:{ all -> 0x183e }
            r6 = r39
            r5.random_id = r6     // Catch:{ all -> 0x183e }
            if (r4 == 0) goto L_0x177f
            goto L_0x1780
        L_0x177f:
            r4 = r2
        L_0x1780:
            r5.message = r4     // Catch:{ all -> 0x183e }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r6 = r45 / r6
            int r1 = (int) r6     // Catch:{ all -> 0x183e }
            r5.date = r1     // Catch:{ all -> 0x183e }
            if (r30 == 0) goto L_0x1792
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r1 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r5.action = r1     // Catch:{ all -> 0x0420 }
        L_0x1792:
            if (r31 == 0) goto L_0x179b
            int r1 = r5.flags     // Catch:{ all -> 0x0420 }
            r4 = -2147483648(0xfffffffvar_, float:-0.0)
            r1 = r1 | r4
            r5.flags = r1     // Catch:{ all -> 0x0420 }
        L_0x179b:
            r6 = r37
            r5.dialog_id = r6     // Catch:{ all -> 0x183e }
            if (r36 == 0) goto L_0x17af
            org.telegram.tgnet.TLRPC$TL_peerChannel r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r5.peer_id = r1     // Catch:{ all -> 0x0420 }
            r8 = r36
            r1.channel_id = r8     // Catch:{ all -> 0x0420 }
            r8 = r24
            goto L_0x17ca
        L_0x17af:
            if (r24 == 0) goto L_0x17bd
            org.telegram.tgnet.TLRPC$TL_peerChat r1 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r5.peer_id = r1     // Catch:{ all -> 0x0420 }
            r8 = r24
            r1.chat_id = r8     // Catch:{ all -> 0x0420 }
            goto L_0x17ca
        L_0x17bd:
            r8 = r24
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x183e }
            r1.<init>()     // Catch:{ all -> 0x183e }
            r5.peer_id = r1     // Catch:{ all -> 0x183e }
            r4 = r23
            r1.user_id = r4     // Catch:{ all -> 0x183e }
        L_0x17ca:
            int r1 = r5.flags     // Catch:{ all -> 0x183e }
            r1 = r1 | 256(0x100, float:3.59E-43)
            r5.flags = r1     // Catch:{ all -> 0x183e }
            if (r27 == 0) goto L_0x17dc
            org.telegram.tgnet.TLRPC$TL_peerChat r1 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r5.from_id = r1     // Catch:{ all -> 0x0420 }
            r1.chat_id = r8     // Catch:{ all -> 0x0420 }
            goto L_0x17fc
        L_0x17dc:
            if (r26 == 0) goto L_0x17ea
            org.telegram.tgnet.TLRPC$TL_peerChannel r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r5.from_id = r1     // Catch:{ all -> 0x0420 }
            r4 = r26
            r1.channel_id = r4     // Catch:{ all -> 0x0420 }
            goto L_0x17fc
        L_0x17ea:
            if (r19 == 0) goto L_0x17f8
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r5.from_id = r1     // Catch:{ all -> 0x0420 }
            r4 = r19
            r1.user_id = r4     // Catch:{ all -> 0x0420 }
            goto L_0x17fc
        L_0x17f8:
            org.telegram.tgnet.TLRPC$Peer r1 = r5.peer_id     // Catch:{ all -> 0x183e }
            r5.from_id = r1     // Catch:{ all -> 0x183e }
        L_0x17fc:
            if (r20 != 0) goto L_0x1803
            if (r30 == 0) goto L_0x1801
            goto L_0x1803
        L_0x1801:
            r1 = 0
            goto L_0x1804
        L_0x1803:
            r1 = 1
        L_0x1804:
            r5.mentioned = r1     // Catch:{ all -> 0x183e }
            r1 = r25
            r5.silent = r1     // Catch:{ all -> 0x183e }
            r13 = r22
            r5.from_scheduled = r13     // Catch:{ all -> 0x183e }
            org.telegram.messenger.MessageObject r1 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x183e }
            r19 = r1
            r20 = r29
            r21 = r5
            r22 = r2
            r23 = r32
            r24 = r35
            r25 = r3
            r26 = r34
            r27 = r33
            r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27)     // Catch:{ all -> 0x183e }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x183e }
            r2.<init>()     // Catch:{ all -> 0x183e }
            r2.add(r1)     // Catch:{ all -> 0x183e }
            org.telegram.messenger.NotificationsController r1 = org.telegram.messenger.NotificationsController.getInstance(r29)     // Catch:{ all -> 0x183e }
            r3 = r43
            java.util.concurrent.CountDownLatch r4 = r3.countDownLatch     // Catch:{ all -> 0x1865 }
            r5 = 1
            r1.processNewMessages(r2, r5, r5, r4)     // Catch:{ all -> 0x1865 }
            r8 = 0
            goto L_0x1852
        L_0x183b:
            r3 = r43
            goto L_0x1851
        L_0x183e:
            r0 = move-exception
            r3 = r43
            goto L_0x1866
        L_0x1842:
            r0 = move-exception
            r3 = r43
            goto L_0x187c
        L_0x1846:
            r3 = r43
            goto L_0x184f
        L_0x1849:
            r0 = move-exception
            r3 = r1
            goto L_0x187c
        L_0x184c:
            r3 = r1
            r28 = r14
        L_0x184f:
            r29 = r15
        L_0x1851:
            r8 = 1
        L_0x1852:
            if (r8 == 0) goto L_0x1859
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1865 }
            r1.countDown()     // Catch:{ all -> 0x1865 }
        L_0x1859:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r29)     // Catch:{ all -> 0x1865 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r29)     // Catch:{ all -> 0x1865 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1865 }
            goto L_0x199d
        L_0x1865:
            r0 = move-exception
        L_0x1866:
            r1 = r0
            r4 = r9
            r14 = r28
            r15 = r29
            goto L_0x1948
        L_0x186e:
            r0 = move-exception
            r3 = r1
            r28 = r14
            r29 = r15
            r1 = r0
            r4 = r9
            goto L_0x1948
        L_0x1878:
            r0 = move-exception
            r3 = r1
            r28 = r7
        L_0x187c:
            r29 = r15
            goto L_0x1944
        L_0x1880:
            r3 = r1
            r28 = r7
            r29 = r15
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x189f }
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$aCTiHH_b4RhBMfcCkAHdgReKcg4 r2 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$aCTiHH_b4RhBMfcCkAHdgReKcg4     // Catch:{ all -> 0x189a }
            r15 = r29
            r2.<init>(r15)     // Catch:{ all -> 0x1897 }
            r1.postRunnable(r2)     // Catch:{ all -> 0x193e }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x193e }
            r1.countDown()     // Catch:{ all -> 0x193e }
            return
        L_0x1897:
            r0 = move-exception
            goto L_0x1944
        L_0x189a:
            r0 = move-exception
            r15 = r29
            goto L_0x1944
        L_0x189f:
            r0 = move-exception
            r15 = r29
            goto L_0x1944
        L_0x18a4:
            r3 = r1
            r28 = r7
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ r1 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ     // Catch:{ all -> 0x18b5 }
            r1.<init>(r15)     // Catch:{ all -> 0x18b5 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ all -> 0x193e }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x193e }
            r1.countDown()     // Catch:{ all -> 0x193e }
            return
        L_0x18b5:
            r0 = move-exception
            goto L_0x1944
        L_0x18b8:
            r3 = r1
            r28 = r7
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x193e }
            r1.<init>()     // Catch:{ all -> 0x193e }
            r2 = 0
            r1.popup = r2     // Catch:{ all -> 0x193e }
            r2 = 2
            r1.flags = r2     // Catch:{ all -> 0x193e }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r6 = r45 / r6
            int r2 = (int) r6     // Catch:{ all -> 0x193e }
            r1.inbox_date = r2     // Catch:{ all -> 0x193e }
            java.lang.String r2 = "message"
            java.lang.String r2 = r5.getString(r2)     // Catch:{ all -> 0x193e }
            r1.message = r2     // Catch:{ all -> 0x193e }
            java.lang.String r2 = "announcement"
            r1.type = r2     // Catch:{ all -> 0x193e }
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ all -> 0x193e }
            r2.<init>()     // Catch:{ all -> 0x193e }
            r1.media = r2     // Catch:{ all -> 0x193e }
            org.telegram.tgnet.TLRPC$TL_updates r2 = new org.telegram.tgnet.TLRPC$TL_updates     // Catch:{ all -> 0x193e }
            r2.<init>()     // Catch:{ all -> 0x193e }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r4 = r2.updates     // Catch:{ all -> 0x193e }
            r4.add(r1)     // Catch:{ all -> 0x193e }
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x193e }
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo r4 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo     // Catch:{ all -> 0x18b5 }
            r4.<init>(r15, r2)     // Catch:{ all -> 0x18b5 }
            r1.postRunnable(r4)     // Catch:{ all -> 0x193e }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x193e }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x193e }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x193e }
            r1.countDown()     // Catch:{ all -> 0x193e }
            return
        L_0x1901:
            r3 = r1
            r28 = r7
            java.lang.String r1 = "dc"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x193e }
            java.lang.String r2 = "addr"
            java.lang.String r2 = r11.getString(r2)     // Catch:{ all -> 0x193e }
            java.lang.String r4 = ":"
            java.lang.String[] r2 = r2.split(r4)     // Catch:{ all -> 0x193e }
            int r4 = r2.length     // Catch:{ all -> 0x193e }
            r5 = 2
            if (r4 == r5) goto L_0x1920
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x193e }
            r1.countDown()     // Catch:{ all -> 0x193e }
            return
        L_0x1920:
            r4 = 0
            r4 = r2[r4]     // Catch:{ all -> 0x193e }
            r5 = 1
            r2 = r2[r5]     // Catch:{ all -> 0x193e }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ all -> 0x193e }
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x193e }
            r5.applyDatacenterAddress(r1, r4, r2)     // Catch:{ all -> 0x193e }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x193e }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x193e }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x193e }
            r1.countDown()     // Catch:{ all -> 0x193e }
            return
        L_0x193e:
            r0 = move-exception
            goto L_0x1944
        L_0x1940:
            r0 = move-exception
            r3 = r1
            r28 = r7
        L_0x1944:
            r1 = r0
            r4 = r9
            r14 = r28
        L_0x1948:
            r2 = -1
            goto L_0x1965
        L_0x194a:
            r0 = move-exception
            r3 = r1
            r28 = r7
            r1 = r0
            r4 = r9
            r14 = r28
            r2 = -1
            goto L_0x1964
        L_0x1954:
            r0 = move-exception
            r3 = r1
            r28 = r7
            r1 = r0
            r14 = r28
            r2 = -1
            r4 = 0
            goto L_0x1964
        L_0x195e:
            r0 = move-exception
            r3 = r1
            r1 = r0
            r2 = -1
            r4 = 0
            r14 = 0
        L_0x1964:
            r15 = -1
        L_0x1965:
            if (r15 == r2) goto L_0x1977
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15)
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)
            r2.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r2 = r3.countDownLatch
            r2.countDown()
            goto L_0x197a
        L_0x1977:
            r43.onDecryptError()
        L_0x197a:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x199a
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
        L_0x199a:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x199d:
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
