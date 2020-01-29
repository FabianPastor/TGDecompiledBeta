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
        long uptimeMillis = SystemClock.uptimeMillis();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("GCM received data: " + data + " from: " + from);
        }
        AndroidUtilities.runOnUIThread(new Runnable(data, sentTime) {
            private final /* synthetic */ Map f$1;
            private final /* synthetic */ long f$2;

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
            FileLog.d("finished GCM service, time = " + (SystemClock.uptimeMillis() - uptimeMillis));
        }
    }

    public /* synthetic */ void lambda$onMessageReceived$4$GcmPushListenerService(Map map, long j) {
        ApplicationLoader.postInitApplication();
        Utilities.stageQueue.postRunnable(new Runnable(map, j) {
            private final /* synthetic */ Map f$1;
            private final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                GcmPushListenerService.this.lambda$null$3$GcmPushListenerService(this.f$1, this.f$2);
            }
        });
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v26, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v14, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v19, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v59, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v1, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v2, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v44, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v45, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v46, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v47, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v48, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v3, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v16, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v19, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v22, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v48, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v103, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v4, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v5, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v66, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v108, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v69, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v72, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v78, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v79, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v82, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v6, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v7, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v95, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v3, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v172, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v6, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v177, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v10, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v11, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v7, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v8, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v44, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v11, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v47, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v50, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v213, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v148, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v239, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v192, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v190, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v196, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v198, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v200, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v201, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v206, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v207, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v208, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v209, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v211, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v212, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v74, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v78, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v79, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v84, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v85, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v86, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x01df, code lost:
        if (r2 == 0) goto L_0x185a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x01e1, code lost:
        if (r2 == 1) goto L_0x1811;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x01e3, code lost:
        if (r2 == 2) goto L_0x17fd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x01e5, code lost:
        if (r2 == 3) goto L_0x17d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:127:0x01ef, code lost:
        if (r11.has("channel_id") == false) goto L_0x01fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:?, code lost:
        r2 = r11.getInt("channel_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x01f7, code lost:
        r3 = (long) (-r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x01fa, code lost:
        r3 = 0;
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:0x0203, code lost:
        if (r11.has("from_id") == false) goto L_0x0217;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:?, code lost:
        r3 = r11.getInt("from_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x020b, code lost:
        r14 = r7;
        r6 = r3;
        r3 = (long) r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x0213, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x0214, code lost:
        r14 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x0215, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0217, code lost:
        r14 = r7;
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x021f, code lost:
        if (r11.has("chat_id") == false) goto L_0x022c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:?, code lost:
        r3 = r11.getInt("chat_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x0227, code lost:
        r12 = (long) (-r3);
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x022a, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x022c, code lost:
        r12 = r3;
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x0234, code lost:
        if (r11.has("encryption_id") == false) goto L_0x0240;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x023c, code lost:
        r12 = ((long) r11.getInt("encryption_id")) << 32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x0246, code lost:
        if (r11.has("schedule") == false) goto L_0x0252;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x024e, code lost:
        if (r11.getInt("schedule") != 1) goto L_0x0252;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x0250, code lost:
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x0252, code lost:
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x0255, code lost:
        if (r12 != 0) goto L_0x0264;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x025d, code lost:
        if ("ENCRYPTED_MESSAGE".equals(r9) == false) goto L_0x0264;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:177:0x025f, code lost:
        r12 = -4294967296L;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:179:0x0266, code lost:
        if (r12 == 0) goto L_0x17a6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x0270, code lost:
        if ("READ_HISTORY".equals(r9) == false) goto L_0x02e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:?, code lost:
        r4 = r11.getInt("max_id");
        r5 = new java.util.ArrayList();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x027f, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x029b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x0281, code lost:
        org.telegram.messenger.FileLog.d("GCM received read notification max_id = " + r4 + " for dialogId = " + r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x029b, code lost:
        if (r2 == 0) goto L_0x02aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x029d, code lost:
        r3 = new org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox();
        r3.channel_id = r2;
        r3.max_id = r4;
        r5.add(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x02aa, code lost:
        r2 = new org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x02af, code lost:
        if (r6 == 0) goto L_0x02bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x02b1, code lost:
        r2.peer = new org.telegram.tgnet.TLRPC.TL_peerUser();
        r2.peer.user_id = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x02bd, code lost:
        r2.peer = new org.telegram.tgnet.TLRPC.TL_peerChat();
        r2.peer.chat_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x02c8, code lost:
        r2.max_id = r4;
        r5.add(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x02cd, code lost:
        org.telegram.messenger.MessagesController.getInstance(r15).processUpdateArray(r5, (java.util.ArrayList<org.telegram.tgnet.TLRPC.User>) null, (java.util.ArrayList<org.telegram.tgnet.TLRPC.Chat>) null, false, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x02e8, code lost:
        if ("MESSAGE_DELETED".equals(r9) == false) goto L_0x034d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:?, code lost:
        r3 = r11.getString("messages").split(",");
        r4 = new android.util.SparseArray();
        r5 = new java.util.ArrayList();
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x0300, code lost:
        if (r6 >= r3.length) goto L_0x030e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x0302, code lost:
        r5.add(org.telegram.messenger.Utilities.parseInt(r3[r6]));
        r6 = r6 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x030e, code lost:
        r4.put(r2, r5);
        org.telegram.messenger.NotificationsController.getInstance(r15).removeDeletedMessagesFromNotifications(r4);
        org.telegram.messenger.MessagesController.getInstance(r15).deleteMessagesByPush(r12, r5, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x0321, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x17a6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x0323, code lost:
        org.telegram.messenger.FileLog.d("GCM received " + r9 + " for dialogId = " + r12 + " mids = " + android.text.TextUtils.join(",", r5));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x0351, code lost:
        if (android.text.TextUtils.isEmpty(r9) != false) goto L_0x17a6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x0359, code lost:
        if (r11.has("msg_id") == false) goto L_0x0364;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:218:?, code lost:
        r7 = r11.getInt("msg_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x0361, code lost:
        r29 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:220:0x0364, code lost:
        r29 = r14;
        r7 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x0369, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:0x036d, code lost:
        if (r11.has("random_id") == false) goto L_0x038b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x037d, code lost:
        r14 = r4;
        r22 = r3;
        r3 = org.telegram.messenger.Utilities.parseLong(r11.getString("random_id")).longValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x0385, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x0386, code lost:
        r3 = r1;
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x038b, code lost:
        r22 = r3;
        r14 = r4;
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x0390, code lost:
        if (r7 == 0) goto L_0x03d5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x0392, code lost:
        r23 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:?, code lost:
        r1 = org.telegram.messenger.MessagesController.getInstance(r15).dialogs_read_inbox_max.get(java.lang.Long.valueOf(r12));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x03a4, code lost:
        if (r1 != null) goto L_0x03c3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x03a6, code lost:
        r1 = java.lang.Integer.valueOf(org.telegram.messenger.MessagesStorage.getInstance(r15).getDialogReadMax(false, r12));
        r24 = r6;
        org.telegram.messenger.MessagesController.getInstance(r15).dialogs_read_inbox_max.put(java.lang.Long.valueOf(r12), r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x03c3, code lost:
        r24 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x03c9, code lost:
        if (r7 <= r1.intValue()) goto L_0x03e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x03cc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x03cd, code lost:
        r2 = -1;
        r3 = r35;
        r1 = r0;
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x03d5, code lost:
        r24 = r6;
        r23 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x03db, code lost:
        if (r3 == 0) goto L_0x03e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x03e5, code lost:
        if (org.telegram.messenger.MessagesStorage.getInstance(r15).checkMessageByRandomId(r3) != false) goto L_0x03e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x03e7, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x03e9, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x03ea, code lost:
        if (r1 == false) goto L_0x17a0;
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
        r30 = r15;
        r14 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0423, code lost:
        r30 = r15;
        r14 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0428, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x042c, code lost:
        if (r5.has("loc_args") == false) goto L_0x0458;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:?, code lost:
        r5 = r5.getJSONArray("loc_args");
        r15 = new java.lang.String[r5.length()];
        r20 = r6;
        r19 = r14;
        r14 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x0440, code lost:
        if (r14 >= r15.length) goto L_0x044b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x0442, code lost:
        r15[r14] = r5.getString(r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x0448, code lost:
        r14 = r14 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x044b, code lost:
        r5 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x044d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x044e, code lost:
        r2 = -1;
        r3 = r35;
        r1 = r0;
        r14 = r29;
        r15 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x0458, code lost:
        r20 = r6;
        r19 = r14;
        r5 = 0;
        r15 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:?, code lost:
        r6 = r15[r5];
        r27 = r11.has("edit_date");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x046c, code lost:
        if (r9.startsWith("CHAT_") == false) goto L_0x047c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x046e, code lost:
        if (r2 == 0) goto L_0x0472;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x0470, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x0472, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:?, code lost:
        r14 = r15[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x0476, code lost:
        r11 = r6;
        r26 = false;
        r6 = r5;
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x0482, code lost:
        if (r9.startsWith("PINNED_") == false) goto L_0x0490;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x0484, code lost:
        if (r1 == 0) goto L_0x0488;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x0486, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x0488, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:311:0x0489, code lost:
        r14 = r6;
        r11 = null;
        r26 = false;
        r6 = r5;
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x0490, code lost:
        r14 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x0497, code lost:
        if (r9.startsWith("CHANNEL_") == false) goto L_0x049f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x0499, code lost:
        r5 = false;
        r6 = false;
        r11 = null;
        r26 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x049f, code lost:
        r5 = false;
        r6 = false;
        r11 = null;
        r26 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x04a6, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x04cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x04a8, code lost:
        r25 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:?, code lost:
        r14 = new java.lang.StringBuilder();
        r31 = r11;
        r14.append("GCM received message notification ");
        r14.append(r9);
        r14.append(" for dialogId = ");
        r14.append(r12);
        r14.append(" mid = ");
        r14.append(r7);
        org.telegram.messenger.FileLog.d(r14.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x04cf, code lost:
        r31 = r11;
        r25 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x04d7, code lost:
        switch(r9.hashCode()) {
            case -2100047043: goto L_0x0958;
            case -2091498420: goto L_0x094d;
            case -2053872415: goto L_0x0942;
            case -2039746363: goto L_0x0937;
            case -2023218804: goto L_0x092c;
            case -1979538588: goto L_0x0921;
            case -1979536003: goto L_0x0916;
            case -1979535888: goto L_0x090b;
            case -1969004705: goto L_0x0900;
            case -1946699248: goto L_0x08f4;
            case -1528047021: goto L_0x08e8;
            case -1493579426: goto L_0x08dc;
            case -1482481933: goto L_0x08d0;
            case -1480102982: goto L_0x08c5;
            case -1478041834: goto L_0x08b9;
            case -1474543101: goto L_0x08ae;
            case -1465695932: goto L_0x08a2;
            case -1374906292: goto L_0x0896;
            case -1372940586: goto L_0x088a;
            case -1264245338: goto L_0x087e;
            case -1236086700: goto L_0x0872;
            case -1236077786: goto L_0x0866;
            case -1235796237: goto L_0x085a;
            case -1235686303: goto L_0x084f;
            case -1198046100: goto L_0x0844;
            case -1124254527: goto L_0x0838;
            case -1085137927: goto L_0x082c;
            case -1084856378: goto L_0x0820;
            case -1084746444: goto L_0x0814;
            case -819729482: goto L_0x0808;
            case -772141857: goto L_0x07fc;
            case -638310039: goto L_0x07f0;
            case -590403924: goto L_0x07e4;
            case -589196239: goto L_0x07d8;
            case -589193654: goto L_0x07cc;
            case -589193539: goto L_0x07c0;
            case -440169325: goto L_0x07b4;
            case -412748110: goto L_0x07a8;
            case -228518075: goto L_0x079c;
            case -213586509: goto L_0x0790;
            case -115582002: goto L_0x0784;
            case -112621464: goto L_0x0778;
            case -108522133: goto L_0x076c;
            case -107572034: goto L_0x0761;
            case -40534265: goto L_0x0755;
            case 65254746: goto L_0x0749;
            case 141040782: goto L_0x073d;
            case 309993049: goto L_0x0731;
            case 309995634: goto L_0x0725;
            case 309995749: goto L_0x0719;
            case 320532812: goto L_0x070d;
            case 328933854: goto L_0x0701;
            case 331340546: goto L_0x06f5;
            case 344816990: goto L_0x06e9;
            case 346878138: goto L_0x06dd;
            case 350376871: goto L_0x06d1;
            case 615714517: goto L_0x06c6;
            case 715508879: goto L_0x06ba;
            case 728985323: goto L_0x06ae;
            case 731046471: goto L_0x06a2;
            case 734545204: goto L_0x0696;
            case 802032552: goto L_0x068a;
            case 991498806: goto L_0x067e;
            case 1007364121: goto L_0x0672;
            case 1019917311: goto L_0x0666;
            case 1019926225: goto L_0x065a;
            case 1020207774: goto L_0x064e;
            case 1020317708: goto L_0x0642;
            case 1060349560: goto L_0x0636;
            case 1060358474: goto L_0x062a;
            case 1060640023: goto L_0x061e;
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
        if (r9.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x04e4, code lost:
        r10 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x04ee, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x04f0, code lost:
        r10 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x04fa, code lost:
        if (r9.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x04fc, code lost:
        r10 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x0506, code lost:
        if (r9.equals("PINNED_CONTACT") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x0508, code lost:
        r10 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x0512, code lost:
        if (r9.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x0514, code lost:
        r10 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x051e, code lost:
        if (r9.equals("LOCKED_MESSAGE") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x0520, code lost:
        r10 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x052a, code lost:
        if (r9.equals("CHANNEL_MESSAGES") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x052c, code lost:
        r10 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x0536, code lost:
        if (r9.equals("MESSAGE_INVOICE") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x0538, code lost:
        r10 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x0542, code lost:
        if (r9.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x0544, code lost:
        r10 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x054e, code lost:
        if (r9.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x0550, code lost:
        r10 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x055a, code lost:
        if (r9.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x055c, code lost:
        r10 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x0566, code lost:
        if (r9.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x0568, code lost:
        r10 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x0572, code lost:
        if (r9.equals("MESSAGE_VIDEOS") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x0574, code lost:
        r10 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x057e, code lost:
        if (r9.equals("PHONE_CALL_MISSED") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x0580, code lost:
        r10 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x058a, code lost:
        if (r9.equals("MESSAGE_PHOTOS") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x058c, code lost:
        r10 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x0596, code lost:
        if (r9.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x0598, code lost:
        r10 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x05a2, code lost:
        if (r9.equals("MESSAGE_NOTEXT") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x05a4, code lost:
        r10 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x05ad, code lost:
        if (r9.equals("MESSAGE_GIF") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x05af, code lost:
        r10 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x05b9, code lost:
        if (r9.equals("MESSAGE_GEO") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x05bb, code lost:
        r10 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x05c5, code lost:
        if (r9.equals("MESSAGE_DOC") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x05c7, code lost:
        r10 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x05d1, code lost:
        if (r9.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x05d3, code lost:
        r10 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x05dd, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x05df, code lost:
        r10 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x05e9, code lost:
        if (r9.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x05eb, code lost:
        r10 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x05f5, code lost:
        if (r9.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x05f7, code lost:
        r10 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:400:0x0601, code lost:
        if (r9.equals("CHAT_TITLE_EDITED") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x0603, code lost:
        r10 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x060d, code lost:
        if (r9.equals("PINNED_NOTEXT") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x060f, code lost:
        r10 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x0619, code lost:
        if (r9.equals("MESSAGE_TEXT") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x061b, code lost:
        r10 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x0624, code lost:
        if (r9.equals("MESSAGE_POLL") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x0626, code lost:
        r10 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0630, code lost:
        if (r9.equals("MESSAGE_GAME") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x0632, code lost:
        r10 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x063c, code lost:
        if (r9.equals("MESSAGE_FWDS") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x063e, code lost:
        r10 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x0648, code lost:
        if (r9.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x064a, code lost:
        r10 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x0654, code lost:
        if (r9.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x0656, code lost:
        r10 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x0660, code lost:
        if (r9.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x0662, code lost:
        r10 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x066c, code lost:
        if (r9.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x066e, code lost:
        r10 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x0678, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x067a, code lost:
        r10 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x0684, code lost:
        if (r9.equals("PINNED_GEOLIVE") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x0686, code lost:
        r10 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x0690, code lost:
        if (r9.equals("MESSAGE_CONTACT") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x0692, code lost:
        r10 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x069c, code lost:
        if (r9.equals("PINNED_VIDEO") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x069e, code lost:
        r10 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x06a8, code lost:
        if (r9.equals("PINNED_ROUND") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x06aa, code lost:
        r10 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x06b4, code lost:
        if (r9.equals("PINNED_PHOTO") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x06b6, code lost:
        r10 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x06c0, code lost:
        if (r9.equals("PINNED_AUDIO") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x06c2, code lost:
        r10 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x06cc, code lost:
        if (r9.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x06ce, code lost:
        r10 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x06d7, code lost:
        if (r9.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x06d9, code lost:
        r10 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x06e3, code lost:
        if (r9.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:458:0x06e5, code lost:
        r10 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:460:0x06ef, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x06f1, code lost:
        r10 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x06fb, code lost:
        if (r9.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x06fd, code lost:
        r10 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x0707, code lost:
        if (r9.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x0709, code lost:
        r10 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x0713, code lost:
        if (r9.equals("MESSAGES") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x0715, code lost:
        r10 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:472:0x071f, code lost:
        if (r9.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0721, code lost:
        r10 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x072b, code lost:
        if (r9.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x072d, code lost:
        r10 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x0737, code lost:
        if (r9.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0739, code lost:
        r10 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x0743, code lost:
        if (r9.equals("CHAT_LEFT") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x0745, code lost:
        r10 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x074f, code lost:
        if (r9.equals("CHAT_ADD_YOU") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x0751, code lost:
        r10 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x075b, code lost:
        if (r9.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x075d, code lost:
        r10 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x0767, code lost:
        if (r9.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0769, code lost:
        r10 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x0772, code lost:
        if (r9.equals("AUTH_REGION") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x0774, code lost:
        r10 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x077e, code lost:
        if (r9.equals("CONTACT_JOINED") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x0780, code lost:
        r10 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x078a, code lost:
        if (r9.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x078c, code lost:
        r10 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x0796, code lost:
        if (r9.equals("ENCRYPTION_REQUEST") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x0798, code lost:
        r10 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x07a2, code lost:
        if (r9.equals("MESSAGE_GEOLIVE") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x07a4, code lost:
        r10 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x07ae, code lost:
        if (r9.equals("CHAT_DELETE_YOU") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x07b0, code lost:
        r10 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x07ba, code lost:
        if (r9.equals("AUTH_UNKNOWN") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x07bc, code lost:
        r10 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x07c6, code lost:
        if (r9.equals("PINNED_GIF") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x07c8, code lost:
        r10 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x07d2, code lost:
        if (r9.equals("PINNED_GEO") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x07d4, code lost:
        r10 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x07de, code lost:
        if (r9.equals("PINNED_DOC") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x07e0, code lost:
        r10 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x07ea, code lost:
        if (r9.equals("PINNED_GAME_SCORE") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x07ec, code lost:
        r10 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x07f6, code lost:
        if (r9.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x07f8, code lost:
        r10 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0802, code lost:
        if (r9.equals("PHONE_CALL_REQUEST") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x0804, code lost:
        r10 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x080e, code lost:
        if (r9.equals("PINNED_STICKER") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0810, code lost:
        r10 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x081a, code lost:
        if (r9.equals("PINNED_TEXT") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x081c, code lost:
        r10 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:538:0x0826, code lost:
        if (r9.equals("PINNED_POLL") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x0828, code lost:
        r10 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0832, code lost:
        if (r9.equals("PINNED_GAME") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x0834, code lost:
        r10 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x083e, code lost:
        if (r9.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0840, code lost:
        r10 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x084a, code lost:
        if (r9.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x084c, code lost:
        r10 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x0855, code lost:
        if (r9.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x0857, code lost:
        r10 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x0860, code lost:
        if (r9.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x0862, code lost:
        r10 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x086c, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x086e, code lost:
        r10 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x0878, code lost:
        if (r9.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x087a, code lost:
        r10 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x0884, code lost:
        if (r9.equals("PINNED_INVOICE") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x0886, code lost:
        r10 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x0890, code lost:
        if (r9.equals("CHAT_RETURNED") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x0892, code lost:
        r10 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x089c, code lost:
        if (r9.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x089e, code lost:
        r10 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x08a8, code lost:
        if (r9.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x08aa, code lost:
        r10 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x08b4, code lost:
        if (r9.equals("MESSAGE_VIDEO") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x08b6, code lost:
        r10 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x08bf, code lost:
        if (r9.equals("MESSAGE_ROUND") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x08c1, code lost:
        r10 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x08cb, code lost:
        if (r9.equals("MESSAGE_PHOTO") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x08cd, code lost:
        r10 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x08d6, code lost:
        if (r9.equals("MESSAGE_MUTED") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x08d8, code lost:
        r10 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x08e2, code lost:
        if (r9.equals("MESSAGE_AUDIO") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x08e4, code lost:
        r10 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x08ee, code lost:
        if (r9.equals("CHAT_MESSAGES") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x08f0, code lost:
        r10 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x08fa, code lost:
        if (r9.equals("CHAT_JOINED") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x08fc, code lost:
        r10 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x0906, code lost:
        if (r9.equals("CHAT_ADD_MEMBER") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x0908, code lost:
        r10 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x0911, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0913, code lost:
        r10 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x091c, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x091e, code lost:
        r10 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x0927, code lost:
        if (r9.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0929, code lost:
        r10 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x0932, code lost:
        if (r9.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x0934, code lost:
        r10 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x093d, code lost:
        if (r9.equals("MESSAGE_STICKER") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x093f, code lost:
        r10 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x0948, code lost:
        if (r9.equals("CHAT_CREATED") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x094a, code lost:
        r10 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x0953, code lost:
        if (r9.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x0955, code lost:
        r10 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x095e, code lost:
        if (r9.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0960, code lost:
        r10 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x0963, code lost:
        r10 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x0964, code lost:
        r32 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x096c, code lost:
        switch(r10) {
            case 0: goto L_0x16be;
            case 1: goto L_0x16be;
            case 2: goto L_0x169b;
            case 3: goto L_0x167c;
            case 4: goto L_0x165d;
            case 5: goto L_0x163e;
            case 6: goto L_0x161e;
            case 7: goto L_0x1603;
            case 8: goto L_0x15e3;
            case 9: goto L_0x15c3;
            case 10: goto L_0x1564;
            case 11: goto L_0x1544;
            case 12: goto L_0x151f;
            case 13: goto L_0x14fa;
            case 14: goto L_0x14da;
            case 15: goto L_0x14ba;
            case 16: goto L_0x149a;
            case 17: goto L_0x1475;
            case 18: goto L_0x1454;
            case 19: goto L_0x1454;
            case 20: goto L_0x142f;
            case 21: goto L_0x1408;
            case 22: goto L_0x13df;
            case 23: goto L_0x13b6;
            case 24: goto L_0x139e;
            case 25: goto L_0x137e;
            case 26: goto L_0x135e;
            case 27: goto L_0x133e;
            case 28: goto L_0x131e;
            case 29: goto L_0x12fe;
            case 30: goto L_0x129f;
            case 31: goto L_0x127f;
            case 32: goto L_0x125a;
            case 33: goto L_0x1235;
            case 34: goto L_0x1215;
            case 35: goto L_0x11f5;
            case 36: goto L_0x11d5;
            case 37: goto L_0x11b5;
            case 38: goto L_0x1189;
            case 39: goto L_0x1161;
            case 40: goto L_0x1139;
            case 41: goto L_0x1122;
            case 42: goto L_0x10ff;
            case 43: goto L_0x10da;
            case 44: goto L_0x10b5;
            case 45: goto L_0x1090;
            case 46: goto L_0x106b;
            case 47: goto L_0x1046;
            case 48: goto L_0x0fc4;
            case 49: goto L_0x0f9d;
            case 50: goto L_0x0var_;
            case 51: goto L_0x0f4f;
            case 52: goto L_0x0f2d;
            case 53: goto L_0x0f0a;
            case 54: goto L_0x0ee7;
            case 55: goto L_0x0ebf;
            case 56: goto L_0x0e98;
            case 57: goto L_0x0e70;
            case 58: goto L_0x0e56;
            case 59: goto L_0x0e56;
            case 60: goto L_0x0e3c;
            case 61: goto L_0x0e22;
            case 62: goto L_0x0e03;
            case 63: goto L_0x0de9;
            case 64: goto L_0x0dcf;
            case 65: goto L_0x0db5;
            case 66: goto L_0x0d9b;
            case 67: goto L_0x0d81;
            case 68: goto L_0x0d53;
            case 69: goto L_0x0d26;
            case 70: goto L_0x0cf9;
            case 71: goto L_0x0cdd;
            case 72: goto L_0x0ca6;
            case 73: goto L_0x0CLASSNAME;
            case 74: goto L_0x0CLASSNAME;
            case 75: goto L_0x0c1c;
            case 76: goto L_0x0bed;
            case 77: goto L_0x0bbe;
            case 78: goto L_0x0b42;
            case 79: goto L_0x0b13;
            case 80: goto L_0x0ada;
            case 81: goto L_0x0aa5;
            case 82: goto L_0x0a75;
            case 83: goto L_0x0a4a;
            case 84: goto L_0x0a1f;
            case 85: goto L_0x09f2;
            case 86: goto L_0x09c5;
            case 87: goto L_0x0998;
            case 88: goto L_0x097d;
            case 89: goto L_0x0977;
            case 90: goto L_0x0977;
            case 91: goto L_0x0977;
            case 92: goto L_0x0977;
            case 93: goto L_0x0977;
            case 94: goto L_0x0977;
            case 95: goto L_0x0977;
            case 96: goto L_0x0977;
            case 97: goto L_0x0977;
            default: goto L_0x096f;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x096f, code lost:
        r21 = r1;
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:627:0x0977, code lost:
        r21 = r1;
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:?, code lost:
        r7 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
        r21 = r1;
        r25 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x0995, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x0998, code lost:
        if (r1 == 0) goto L_0x09b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x099a, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x09b2, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x09c5, code lost:
        if (r1 == 0) goto L_0x09df;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x09c7, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x09df, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x09f2, code lost:
        if (r1 == 0) goto L_0x0a0c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x09f4, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x0a0c, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x0a1f, code lost:
        if (r1 == 0) goto L_0x0a38;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0a21, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0a38, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0a4a, code lost:
        if (r1 == 0) goto L_0x0a63;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x0a4c, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x0a63, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x0a75, code lost:
        if (r1 == 0) goto L_0x0a8e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x0a77, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0a8e, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0a9f, code lost:
        r21 = r1;
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x0aa5, code lost:
        if (r1 == 0) goto L_0x0ac3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0aa7, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0ac3, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0ada, code lost:
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0adc, code lost:
        if (r1 == 0) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0ade, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0afb, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0b13, code lost:
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0b15, code lost:
        if (r1 == 0) goto L_0x0b2f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0b17, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x0b2f, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0b42, code lost:
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x0b44, code lost:
        if (r1 == 0) goto L_0x0b87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x0b48, code lost:
        if (r15.length <= 2) goto L_0x0b6f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x0b50, code lost:
        if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x0b6f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0b52, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0b6f, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:671:0x0b89, code lost:
        if (r15.length <= 1) goto L_0x0bab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:673:0x0b91, code lost:
        if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x0bab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0b93, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0bab, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0bbe, code lost:
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x0bc0, code lost:
        if (r1 == 0) goto L_0x0bda;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0bc2, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:679:0x0bda, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0bed, code lost:
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0bef, code lost:
        if (r1 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x0bf1, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0CLASSNAME, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0c1c, code lost:
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:685:0x0c1e, code lost:
        if (r1 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0CLASSNAME, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x0CLASSNAME, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x0CLASSNAME, code lost:
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0c4b, code lost:
        if (r1 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0c4d, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x0CLASSNAME, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0CLASSNAME, code lost:
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0CLASSNAME, code lost:
        if (r1 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0c7a, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0CLASSNAME, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0ca2, code lost:
        r21 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0ca6, code lost:
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0ca8, code lost:
        if (r1 == 0) goto L_0x0cc6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0caa, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0cc6, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0cdd, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0cf5, code lost:
        r21 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0cf9, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0d26, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0d53, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0d81, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0d9b, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0db5, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0dcf, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0de9, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0e03, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0e22, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0e3c, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0e56, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0e70, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r15[0], r15[1], r15[2]);
        r8 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0e98, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r15[0], r15[1], r15[2], r15[3]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0ebf, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r15[0], r15[1], r15[2]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0ee7, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r15[0], r15[1]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0f0a, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r15[0], r15[1]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0f2d, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r15[0], r15[1]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:0x0f4f, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r15[0], r15[1], r15[2]);
        r8 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0var_, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r15[0], r15[1], r15[2]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0f9d, code lost:
        r10 = r32;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r15[0], r15[1]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0fbe, code lost:
        r21 = r1;
        r16 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0fc4, code lost:
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0fc8, code lost:
        if (r15.length <= 2) goto L_0x100f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0fd0, code lost:
        if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x100f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0fd2, code lost:
        r21 = r1;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r15[0], r15[1], r15[2]);
        r7 = r15[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x100f, code lost:
        r21 = r1;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r15[0], r15[1]);
        r7 = r15[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x1046, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r15[0], r15[1]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x106b, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r15[0], r15[1]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x1090, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r15[0], r15[1]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x10b5, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r15[0], r15[1]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x10da, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r15[0], r15[1]);
        r7 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x10ff, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r15[0], r15[1], r15[2]);
        r7 = r15[2];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x1122, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x1139, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x1161, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x1189, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x11b5, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x11d5, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x11f5, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x1215, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x1235, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r15[0], r15[1]);
        r7 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x125a, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r15[0], r15[1]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x127f, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x129f, code lost:
        r21 = r1;
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x12a5, code lost:
        if (r15.length <= 1) goto L_0x12e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x12ad, code lost:
        if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x12e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x12af, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r15[0], r15[1]);
        r7 = r15[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:0x12e4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x12fe, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x131e, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x133e, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x135e, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x137e, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x139e, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x13b3, code lost:
        r7 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x13b6, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x13df, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x1408, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x142f, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r15[0], r15[1]);
        r7 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:0x1454, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x1475, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r15[0], r15[1]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x149a, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x14ba, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x14da, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x14fa, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r15[0], r15[1]);
        r7 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x151f, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r15[0], r15[1]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x1544, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x1564, code lost:
        r21 = r1;
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x156a, code lost:
        if (r15.length <= 1) goto L_0x15a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x1572, code lost:
        if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x15a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x1574, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r15[0], r15[1]);
        r7 = r15[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x15a9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x15c3, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x15e3, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x1603, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x161a, code lost:
        r7 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x161b, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x161e, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x163e, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x165d, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x167c, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x169b, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r15[0]);
        r7 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x16b9, code lost:
        r16 = r7;
        r7 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x16bc, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x16be, code lost:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r15[0], r15[1]);
        r7 = r15[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x16db, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x16f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x16dd, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x16f2, code lost:
        r1 = false;
        r7 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x16f4, code lost:
        r16 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x16f6, code lost:
        if (r7 == null) goto L_0x1795;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:?, code lost:
        r8 = new org.telegram.tgnet.TLRPC.TL_message();
        r8.id = r10;
        r8.random_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x1701, code lost:
        if (r16 == null) goto L_0x1706;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x1703, code lost:
        r3 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x1706, code lost:
        r3 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x1707, code lost:
        r8.message = r3;
        r8.date = (int) (r37 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x1710, code lost:
        if (r5 == false) goto L_0x1719;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:?, code lost:
        r8.action = new org.telegram.tgnet.TLRPC.TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x1719, code lost:
        if (r6 == false) goto L_0x1722;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x171b, code lost:
        r8.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:?, code lost:
        r8.dialog_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x1724, code lost:
        if (r2 == 0) goto L_0x1732;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:?, code lost:
        r8.to_id = new org.telegram.tgnet.TLRPC.TL_peerChannel();
        r8.to_id.channel_id = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x1732, code lost:
        if (r22 == 0) goto L_0x1742;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x1734, code lost:
        r8.to_id = new org.telegram.tgnet.TLRPC.TL_peerChat();
        r8.to_id.chat_id = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:?, code lost:
        r8.to_id = new org.telegram.tgnet.TLRPC.TL_peerUser();
        r8.to_id.user_id = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x174f, code lost:
        r8.flags |= 256;
        r8.from_id = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x1759, code lost:
        if (r20 != false) goto L_0x1760;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x175b, code lost:
        if (r5 == false) goto L_0x175e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x175e, code lost:
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x1760, code lost:
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x1761, code lost:
        r8.mentioned = r2;
        r8.silent = r19;
        r8.from_scheduled = r23;
        r19 = new org.telegram.messenger.MessageObject(r30, r8, r7, r25, r31, r1, r26, r27);
        r1 = new java.util.ArrayList();
        r1.add(r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x178a, code lost:
        r3 = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x178c, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:?, code lost:
        org.telegram.messenger.NotificationsController.getInstance(r30).processNewMessages(r1, true, true, r3.countDownLatch);
        r28 = false;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x1795, code lost:
        r3 = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x1798, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x1799, code lost:
        r3 = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x179c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x179d, code lost:
        r3 = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x17a0, code lost:
        r3 = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x17a3, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x17a4, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x17a6, code lost:
        r3 = r1;
        r29 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x17a9, code lost:
        r30 = r15;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x17ab, code lost:
        r28 = true;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x17ad, code lost:
        if (r28 == false) goto L_0x17b4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x17af, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x17b4, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r30);
        org.telegram.tgnet.ConnectionsManager.getInstance(r30).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x17c0, code lost:
        r0 = th;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x17c8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x17c9, code lost:
        r3 = r1;
        r29 = r14;
        r30 = r15;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x17d1, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x17d2, code lost:
        r3 = r1;
        r29 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x17d5, code lost:
        r30 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x17d9, code lost:
        r3 = r1;
        r29 = r7;
        r30 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x17e2, code lost:
        r15 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.$$Lambda$GcmPushListenerService$aCTiHH_b4RhBMfcCkAHdgReKcg4(r15));
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x17ef, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x17f0, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x17f3, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x17f4, code lost:
        r15 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:862:0x17f8, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:863:0x17f9, code lost:
        r15 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:864:0x17fd, code lost:
        r3 = r1;
        r29 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:?, code lost:
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ(r15));
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x180d, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x180e, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x1811, code lost:
        r3 = r1;
        r29 = r7;
        r1 = new org.telegram.tgnet.TLRPC.TL_updateServiceNotification();
        r1.popup = false;
        r1.flags = 2;
        r1.inbox_date = (int) (r37 / 1000);
        r1.message = r5.getString("message");
        r1.type = "announcement";
        r1.media = new org.telegram.tgnet.TLRPC.TL_messageMediaEmpty();
        r2 = new org.telegram.tgnet.TLRPC.TL_updates();
        r2.updates.add(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo(r15, r2));
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x1859, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x185a, code lost:
        r3 = r1;
        r29 = r7;
        r1 = r11.getInt("dc");
        r2 = r11.getString("addr").split(":");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x1871, code lost:
        if (r2.length == 2) goto L_0x1879;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x1873, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x1878, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x1879, code lost:
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).applyDatacenterAddress(r1, r2[0], java.lang.Integer.parseInt(r2[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x1896, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x1897, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:?, code lost:
        return;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:856:0x17e7, B:865:0x1800] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:897:0x18be  */
    /* JADX WARNING: Removed duplicated region for block: B:898:0x18ce  */
    /* JADX WARNING: Removed duplicated region for block: B:901:0x18d5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$3$GcmPushListenerService(java.util.Map r36, long r37) {
        /*
            r35 = this;
            r1 = r35
            r2 = r36
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x000d
            java.lang.String r3 = "GCM START PROCESSING"
            org.telegram.messenger.FileLog.d(r3)
        L_0x000d:
            java.lang.String r5 = "p"
            java.lang.Object r5 = r2.get(r5)     // Catch:{ all -> 0x18b5 }
            boolean r6 = r5 instanceof java.lang.String     // Catch:{ all -> 0x18b5 }
            if (r6 != 0) goto L_0x002d
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0024 }
            if (r2 == 0) goto L_0x0020
            java.lang.String r2 = "GCM DECRYPT ERROR 1"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x0024 }
        L_0x0020:
            r35.onDecryptError()     // Catch:{ all -> 0x0024 }
            return
        L_0x0024:
            r0 = move-exception
            r3 = r1
            r2 = -1
            r9 = 0
            r14 = 0
        L_0x0029:
            r15 = -1
        L_0x002a:
            r1 = r0
            goto L_0x18bc
        L_0x002d:
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ all -> 0x18b5 }
            r6 = 8
            byte[] r5 = android.util.Base64.decode(r5, r6)     // Catch:{ all -> 0x18b5 }
            org.telegram.tgnet.NativeByteBuffer r7 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x18b5 }
            int r8 = r5.length     // Catch:{ all -> 0x18b5 }
            r7.<init>((int) r8)     // Catch:{ all -> 0x18b5 }
            r7.writeBytes((byte[]) r5)     // Catch:{ all -> 0x18b5 }
            r8 = 0
            r7.position(r8)     // Catch:{ all -> 0x18b5 }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x18b5 }
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
            byte[] r9 = new byte[r6]     // Catch:{ all -> 0x18b5 }
            r10 = 1
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x18b5 }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x18b5 }
            boolean r11 = java.util.Arrays.equals(r11, r9)     // Catch:{ all -> 0x18b5 }
            r12 = 3
            r13 = 2
            if (r11 != 0) goto L_0x0092
            r35.onDecryptError()     // Catch:{ all -> 0x0024 }
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
            byte[] r9 = new byte[r9]     // Catch:{ all -> 0x18b5 }
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x18b5 }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x18b5 }
            org.telegram.messenger.MessageKeyData r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13)     // Catch:{ all -> 0x18b5 }
            java.nio.ByteBuffer r14 = r7.buffer     // Catch:{ all -> 0x18b5 }
            byte[] r15 = r11.aesKey     // Catch:{ all -> 0x18b5 }
            byte[] r11 = r11.aesIv     // Catch:{ all -> 0x18b5 }
            r17 = 0
            r18 = 0
            r19 = 24
            int r5 = r5.length     // Catch:{ all -> 0x18b5 }
            int r20 = r5 + -24
            r16 = r11
            org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x18b5 }
            byte[] r21 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x18b5 }
            r22 = 96
            r23 = 32
            java.nio.ByteBuffer r5 = r7.buffer     // Catch:{ all -> 0x18b5 }
            r25 = 24
            java.nio.ByteBuffer r11 = r7.buffer     // Catch:{ all -> 0x18b5 }
            int r26 = r11.limit()     // Catch:{ all -> 0x18b5 }
            r24 = r5
            byte[] r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26)     // Catch:{ all -> 0x18b5 }
            boolean r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6)     // Catch:{ all -> 0x18b5 }
            if (r5 != 0) goto L_0x00ea
            r35.onDecryptError()     // Catch:{ all -> 0x0024 }
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
            int r5 = r7.readInt32(r10)     // Catch:{ all -> 0x18b5 }
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x18b5 }
            r7.readBytes(r5, r10)     // Catch:{ all -> 0x18b5 }
            java.lang.String r7 = new java.lang.String     // Catch:{ all -> 0x18b5 }
            r7.<init>(r5)     // Catch:{ all -> 0x18b5 }
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ all -> 0x18ab }
            r5.<init>(r7)     // Catch:{ all -> 0x18ab }
            java.lang.String r9 = "loc_key"
            boolean r9 = r5.has(r9)     // Catch:{ all -> 0x18ab }
            if (r9 == 0) goto L_0x0113
            java.lang.String r9 = "loc_key"
            java.lang.String r9 = r5.getString(r9)     // Catch:{ all -> 0x010c }
            goto L_0x0115
        L_0x010c:
            r0 = move-exception
            r3 = r1
            r14 = r7
            r2 = -1
            r9 = 0
            goto L_0x0029
        L_0x0113:
            java.lang.String r9 = ""
        L_0x0115:
            java.lang.String r11 = "custom"
            java.lang.Object r11 = r5.get(r11)     // Catch:{ all -> 0x18a2 }
            boolean r11 = r11 instanceof org.json.JSONObject     // Catch:{ all -> 0x18a2 }
            if (r11 == 0) goto L_0x012c
            java.lang.String r11 = "custom"
            org.json.JSONObject r11 = r5.getJSONObject(r11)     // Catch:{ all -> 0x0126 }
            goto L_0x0131
        L_0x0126:
            r0 = move-exception
            r3 = r1
            r14 = r7
            r2 = -1
            goto L_0x0029
        L_0x012c:
            org.json.JSONObject r11 = new org.json.JSONObject     // Catch:{ all -> 0x18a2 }
            r11.<init>()     // Catch:{ all -> 0x18a2 }
        L_0x0131:
            java.lang.String r14 = "user_id"
            boolean r14 = r5.has(r14)     // Catch:{ all -> 0x18a2 }
            if (r14 == 0) goto L_0x0142
            java.lang.String r14 = "user_id"
            java.lang.Object r14 = r5.get(r14)     // Catch:{ all -> 0x0126 }
            goto L_0x0143
        L_0x0142:
            r14 = 0
        L_0x0143:
            if (r14 != 0) goto L_0x0150
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x0126 }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x0126 }
            int r14 = r14.getClientUserId()     // Catch:{ all -> 0x0126 }
            goto L_0x0174
        L_0x0150:
            boolean r15 = r14 instanceof java.lang.Integer     // Catch:{ all -> 0x18a2 }
            if (r15 == 0) goto L_0x015b
            java.lang.Integer r14 = (java.lang.Integer) r14     // Catch:{ all -> 0x0126 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0126 }
            goto L_0x0174
        L_0x015b:
            boolean r15 = r14 instanceof java.lang.String     // Catch:{ all -> 0x18a2 }
            if (r15 == 0) goto L_0x016a
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ all -> 0x0126 }
            java.lang.Integer r14 = org.telegram.messenger.Utilities.parseInt(r14)     // Catch:{ all -> 0x0126 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0126 }
            goto L_0x0174
        L_0x016a:
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x18a2 }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x18a2 }
            int r14 = r14.getClientUserId()     // Catch:{ all -> 0x18a2 }
        L_0x0174:
            int r15 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x18a2 }
            r4 = 0
        L_0x0177:
            if (r4 >= r12) goto L_0x018a
            org.telegram.messenger.UserConfig r17 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x0126 }
            int r6 = r17.getClientUserId()     // Catch:{ all -> 0x0126 }
            if (r6 != r14) goto L_0x0185
            r15 = r4
            goto L_0x018a
        L_0x0185:
            int r4 = r4 + 1
            r6 = 8
            goto L_0x0177
        L_0x018a:
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r15)     // Catch:{ all -> 0x1899 }
            boolean r4 = r4.isClientActivated()     // Catch:{ all -> 0x1899 }
            if (r4 != 0) goto L_0x01a9
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x01a3 }
            if (r2 == 0) goto L_0x019d
            java.lang.String r2 = "GCM ACCOUNT NOT ACTIVATED"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x01a3 }
        L_0x019d:
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x01a3 }
            r2.countDown()     // Catch:{ all -> 0x01a3 }
            return
        L_0x01a3:
            r0 = move-exception
            r3 = r1
            r14 = r7
        L_0x01a6:
            r2 = -1
            goto L_0x002a
        L_0x01a9:
            java.lang.String r4 = "google.sent_time"
            r2.get(r4)     // Catch:{ all -> 0x1899 }
            int r2 = r9.hashCode()     // Catch:{ all -> 0x1899 }
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
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01a3 }
            if (r2 == 0) goto L_0x01de
            r2 = 3
            goto L_0x01df
        L_0x01c0:
            java.lang.String r2 = "MESSAGE_ANNOUNCEMENT"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01a3 }
            if (r2 == 0) goto L_0x01de
            r2 = 1
            goto L_0x01df
        L_0x01ca:
            java.lang.String r2 = "DC_UPDATE"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01a3 }
            if (r2 == 0) goto L_0x01de
            r2 = 0
            goto L_0x01df
        L_0x01d4:
            java.lang.String r2 = "SESSION_REVOKE"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01a3 }
            if (r2 == 0) goto L_0x01de
            r2 = 2
            goto L_0x01df
        L_0x01de:
            r2 = -1
        L_0x01df:
            if (r2 == 0) goto L_0x185a
            if (r2 == r10) goto L_0x1811
            if (r2 == r13) goto L_0x17fd
            if (r2 == r12) goto L_0x17d9
            java.lang.String r2 = "channel_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x17d1 }
            r19 = 0
            if (r2 == 0) goto L_0x01fa
            java.lang.String r2 = "channel_id"
            int r2 = r11.getInt(r2)     // Catch:{ all -> 0x01a3 }
            int r4 = -r2
            long r3 = (long) r4
            goto L_0x01fd
        L_0x01fa:
            r3 = r19
            r2 = 0
        L_0x01fd:
            java.lang.String r14 = "from_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x17d1 }
            if (r14 == 0) goto L_0x0217
            java.lang.String r3 = "from_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x0213 }
            r14 = r7
            long r6 = (long) r3
            r33 = r6
            r6 = r3
            r3 = r33
            goto L_0x0219
        L_0x0213:
            r0 = move-exception
            r14 = r7
        L_0x0215:
            r3 = r1
            goto L_0x01a6
        L_0x0217:
            r14 = r7
            r6 = 0
        L_0x0219:
            java.lang.String r7 = "chat_id"
            boolean r7 = r11.has(r7)     // Catch:{ all -> 0x17c8 }
            if (r7 == 0) goto L_0x022c
            java.lang.String r3 = "chat_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x022a }
            int r4 = -r3
            long r12 = (long) r4
            goto L_0x022e
        L_0x022a:
            r0 = move-exception
            goto L_0x0215
        L_0x022c:
            r12 = r3
            r3 = 0
        L_0x022e:
            java.lang.String r4 = "encryption_id"
            boolean r4 = r11.has(r4)     // Catch:{ all -> 0x17c8 }
            if (r4 == 0) goto L_0x0240
            java.lang.String r4 = "encryption_id"
            int r4 = r11.getInt(r4)     // Catch:{ all -> 0x022a }
            long r12 = (long) r4
            r4 = 32
            long r12 = r12 << r4
        L_0x0240:
            java.lang.String r4 = "schedule"
            boolean r4 = r11.has(r4)     // Catch:{ all -> 0x17c8 }
            if (r4 == 0) goto L_0x0252
            java.lang.String r4 = "schedule"
            int r4 = r11.getInt(r4)     // Catch:{ all -> 0x022a }
            if (r4 != r10) goto L_0x0252
            r4 = 1
            goto L_0x0253
        L_0x0252:
            r4 = 0
        L_0x0253:
            int r21 = (r12 > r19 ? 1 : (r12 == r19 ? 0 : -1))
            if (r21 != 0) goto L_0x0264
            java.lang.String r7 = "ENCRYPTED_MESSAGE"
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x022a }
            if (r7 == 0) goto L_0x0264
            r12 = -4294967296(0xfffffffvar_, double:NaN)
        L_0x0264:
            int r7 = (r12 > r19 ? 1 : (r12 == r19 ? 0 : -1))
            if (r7 == 0) goto L_0x17a6
            java.lang.String r7 = "READ_HISTORY"
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x17c8 }
            java.lang.String r10 = " for dialogId = "
            if (r7 == 0) goto L_0x02e0
            java.lang.String r4 = "max_id"
            int r4 = r11.getInt(r4)     // Catch:{ all -> 0x022a }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x022a }
            r5.<init>()     // Catch:{ all -> 0x022a }
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x022a }
            if (r7 == 0) goto L_0x029b
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x022a }
            r7.<init>()     // Catch:{ all -> 0x022a }
            java.lang.String r8 = "GCM received read notification max_id = "
            r7.append(r8)     // Catch:{ all -> 0x022a }
            r7.append(r4)     // Catch:{ all -> 0x022a }
            r7.append(r10)     // Catch:{ all -> 0x022a }
            r7.append(r12)     // Catch:{ all -> 0x022a }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x022a }
            org.telegram.messenger.FileLog.d(r7)     // Catch:{ all -> 0x022a }
        L_0x029b:
            if (r2 == 0) goto L_0x02aa
            org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox r3 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox     // Catch:{ all -> 0x022a }
            r3.<init>()     // Catch:{ all -> 0x022a }
            r3.channel_id = r2     // Catch:{ all -> 0x022a }
            r3.max_id = r4     // Catch:{ all -> 0x022a }
            r5.add(r3)     // Catch:{ all -> 0x022a }
            goto L_0x02cd
        L_0x02aa:
            org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox r2 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox     // Catch:{ all -> 0x022a }
            r2.<init>()     // Catch:{ all -> 0x022a }
            if (r6 == 0) goto L_0x02bd
            org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x022a }
            r3.<init>()     // Catch:{ all -> 0x022a }
            r2.peer = r3     // Catch:{ all -> 0x022a }
            org.telegram.tgnet.TLRPC$Peer r3 = r2.peer     // Catch:{ all -> 0x022a }
            r3.user_id = r6     // Catch:{ all -> 0x022a }
            goto L_0x02c8
        L_0x02bd:
            org.telegram.tgnet.TLRPC$TL_peerChat r6 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x022a }
            r6.<init>()     // Catch:{ all -> 0x022a }
            r2.peer = r6     // Catch:{ all -> 0x022a }
            org.telegram.tgnet.TLRPC$Peer r6 = r2.peer     // Catch:{ all -> 0x022a }
            r6.chat_id = r3     // Catch:{ all -> 0x022a }
        L_0x02c8:
            r2.max_id = r4     // Catch:{ all -> 0x022a }
            r5.add(r2)     // Catch:{ all -> 0x022a }
        L_0x02cd:
            org.telegram.messenger.MessagesController r16 = org.telegram.messenger.MessagesController.getInstance(r15)     // Catch:{ all -> 0x022a }
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r17 = r5
            r16.processUpdateArray(r17, r18, r19, r20, r21)     // Catch:{ all -> 0x022a }
            goto L_0x17a6
        L_0x02e0:
            java.lang.String r7 = "MESSAGE_DELETED"
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x17c8 }
            java.lang.String r8 = "messages"
            if (r7 == 0) goto L_0x034d
            java.lang.String r3 = r11.getString(r8)     // Catch:{ all -> 0x022a }
            java.lang.String r4 = ","
            java.lang.String[] r3 = r3.split(r4)     // Catch:{ all -> 0x022a }
            android.util.SparseArray r4 = new android.util.SparseArray     // Catch:{ all -> 0x022a }
            r4.<init>()     // Catch:{ all -> 0x022a }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x022a }
            r5.<init>()     // Catch:{ all -> 0x022a }
            r6 = 0
        L_0x02ff:
            int r7 = r3.length     // Catch:{ all -> 0x022a }
            if (r6 >= r7) goto L_0x030e
            r7 = r3[r6]     // Catch:{ all -> 0x022a }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x022a }
            r5.add(r7)     // Catch:{ all -> 0x022a }
            int r6 = r6 + 1
            goto L_0x02ff
        L_0x030e:
            r4.put(r2, r5)     // Catch:{ all -> 0x022a }
            org.telegram.messenger.NotificationsController r3 = org.telegram.messenger.NotificationsController.getInstance(r15)     // Catch:{ all -> 0x022a }
            r3.removeDeletedMessagesFromNotifications(r4)     // Catch:{ all -> 0x022a }
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r15)     // Catch:{ all -> 0x022a }
            r3.deleteMessagesByPush(r12, r5, r2)     // Catch:{ all -> 0x022a }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x022a }
            if (r2 == 0) goto L_0x17a6
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x022a }
            r2.<init>()     // Catch:{ all -> 0x022a }
            java.lang.String r3 = "GCM received "
            r2.append(r3)     // Catch:{ all -> 0x022a }
            r2.append(r9)     // Catch:{ all -> 0x022a }
            r2.append(r10)     // Catch:{ all -> 0x022a }
            r2.append(r12)     // Catch:{ all -> 0x022a }
            java.lang.String r3 = " mids = "
            r2.append(r3)     // Catch:{ all -> 0x022a }
            java.lang.String r3 = ","
            java.lang.String r3 = android.text.TextUtils.join(r3, r5)     // Catch:{ all -> 0x022a }
            r2.append(r3)     // Catch:{ all -> 0x022a }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x022a }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x022a }
            goto L_0x17a6
        L_0x034d:
            boolean r7 = android.text.TextUtils.isEmpty(r9)     // Catch:{ all -> 0x17c8 }
            if (r7 != 0) goto L_0x17a6
            java.lang.String r7 = "msg_id"
            boolean r7 = r11.has(r7)     // Catch:{ all -> 0x17c8 }
            if (r7 == 0) goto L_0x0364
            java.lang.String r7 = "msg_id"
            int r7 = r11.getInt(r7)     // Catch:{ all -> 0x022a }
            r29 = r14
            goto L_0x0367
        L_0x0364:
            r29 = r14
            r7 = 0
        L_0x0367:
            java.lang.String r14 = "random_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x17a3 }
            if (r14 == 0) goto L_0x038b
            java.lang.String r14 = "random_id"
            java.lang.String r14 = r11.getString(r14)     // Catch:{ all -> 0x0385 }
            java.lang.Long r14 = org.telegram.messenger.Utilities.parseLong(r14)     // Catch:{ all -> 0x0385 }
            long r22 = r14.longValue()     // Catch:{ all -> 0x0385 }
            r14 = r4
            r33 = r22
            r22 = r3
            r3 = r33
            goto L_0x0390
        L_0x0385:
            r0 = move-exception
            r3 = r1
            r14 = r29
            goto L_0x01a6
        L_0x038b:
            r22 = r3
            r14 = r4
            r3 = r19
        L_0x0390:
            if (r7 == 0) goto L_0x03d5
            r23 = r14
            org.telegram.messenger.MessagesController r14 = org.telegram.messenger.MessagesController.getInstance(r15)     // Catch:{ all -> 0x03cc }
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r14 = r14.dialogs_read_inbox_max     // Catch:{ all -> 0x03cc }
            java.lang.Long r1 = java.lang.Long.valueOf(r12)     // Catch:{ all -> 0x03cc }
            java.lang.Object r1 = r14.get(r1)     // Catch:{ all -> 0x03cc }
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch:{ all -> 0x03cc }
            if (r1 != 0) goto L_0x03c3
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r15)     // Catch:{ all -> 0x03cc }
            r14 = 0
            int r1 = r1.getDialogReadMax(r14, r12)     // Catch:{ all -> 0x03cc }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x03cc }
            org.telegram.messenger.MessagesController r14 = org.telegram.messenger.MessagesController.getInstance(r15)     // Catch:{ all -> 0x03cc }
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r14 = r14.dialogs_read_inbox_max     // Catch:{ all -> 0x03cc }
            r24 = r6
            java.lang.Long r6 = java.lang.Long.valueOf(r12)     // Catch:{ all -> 0x03cc }
            r14.put(r6, r1)     // Catch:{ all -> 0x03cc }
            goto L_0x03c5
        L_0x03c3:
            r24 = r6
        L_0x03c5:
            int r1 = r1.intValue()     // Catch:{ all -> 0x03cc }
            if (r7 <= r1) goto L_0x03e9
            goto L_0x03e7
        L_0x03cc:
            r0 = move-exception
            r2 = -1
            r3 = r35
            r1 = r0
            r14 = r29
            goto L_0x18bc
        L_0x03d5:
            r24 = r6
            r23 = r14
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 == 0) goto L_0x03e9
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r15)     // Catch:{ all -> 0x03cc }
            boolean r1 = r1.checkMessageByRandomId(r3)     // Catch:{ all -> 0x03cc }
            if (r1 != 0) goto L_0x03e9
        L_0x03e7:
            r1 = 1
            goto L_0x03ea
        L_0x03e9:
            r1 = 0
        L_0x03ea:
            if (r1 == 0) goto L_0x17a0
            java.lang.String r1 = "chat_from_id"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x179c }
            if (r1 == 0) goto L_0x03fb
            java.lang.String r1 = "chat_from_id"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x03cc }
            goto L_0x03fc
        L_0x03fb:
            r1 = 0
        L_0x03fc:
            java.lang.String r6 = "mention"
            boolean r6 = r11.has(r6)     // Catch:{ all -> 0x179c }
            if (r6 == 0) goto L_0x040e
            java.lang.String r6 = "mention"
            int r6 = r11.getInt(r6)     // Catch:{ all -> 0x03cc }
            if (r6 == 0) goto L_0x040e
            r6 = 1
            goto L_0x040f
        L_0x040e:
            r6 = 0
        L_0x040f:
            java.lang.String r14 = "silent"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x179c }
            if (r14 == 0) goto L_0x0423
            java.lang.String r14 = "silent"
            int r14 = r11.getInt(r14)     // Catch:{ all -> 0x03cc }
            if (r14 == 0) goto L_0x0423
            r30 = r15
            r14 = 1
            goto L_0x0426
        L_0x0423:
            r30 = r15
            r14 = 0
        L_0x0426:
            java.lang.String r15 = "loc_args"
            boolean r15 = r5.has(r15)     // Catch:{ all -> 0x1798 }
            if (r15 == 0) goto L_0x0458
            java.lang.String r15 = "loc_args"
            org.json.JSONArray r5 = r5.getJSONArray(r15)     // Catch:{ all -> 0x044d }
            int r15 = r5.length()     // Catch:{ all -> 0x044d }
            java.lang.String[] r15 = new java.lang.String[r15]     // Catch:{ all -> 0x044d }
            r20 = r6
            r19 = r14
            r14 = 0
        L_0x043f:
            int r6 = r15.length     // Catch:{ all -> 0x044d }
            if (r14 >= r6) goto L_0x044b
            java.lang.String r6 = r5.getString(r14)     // Catch:{ all -> 0x044d }
            r15[r14] = r6     // Catch:{ all -> 0x044d }
            int r14 = r14 + 1
            goto L_0x043f
        L_0x044b:
            r5 = 0
            goto L_0x045e
        L_0x044d:
            r0 = move-exception
            r2 = -1
            r3 = r35
            r1 = r0
            r14 = r29
            r15 = r30
            goto L_0x18bc
        L_0x0458:
            r20 = r6
            r19 = r14
            r5 = 0
            r15 = 0
        L_0x045e:
            r6 = r15[r5]     // Catch:{ all -> 0x1798 }
            java.lang.String r5 = "edit_date"
            boolean r27 = r11.has(r5)     // Catch:{ all -> 0x1798 }
            java.lang.String r5 = "CHAT_"
            boolean r5 = r9.startsWith(r5)     // Catch:{ all -> 0x1798 }
            if (r5 == 0) goto L_0x047c
            if (r2 == 0) goto L_0x0472
            r5 = 1
            goto L_0x0473
        L_0x0472:
            r5 = 0
        L_0x0473:
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r11 = r6
            r26 = 0
            r6 = r5
            r5 = 0
            goto L_0x04a4
        L_0x047c:
            java.lang.String r5 = "PINNED_"
            boolean r5 = r9.startsWith(r5)     // Catch:{ all -> 0x1798 }
            if (r5 == 0) goto L_0x0490
            if (r1 == 0) goto L_0x0488
            r5 = 1
            goto L_0x0489
        L_0x0488:
            r5 = 0
        L_0x0489:
            r14 = r6
            r11 = 0
            r26 = 0
            r6 = r5
            r5 = 1
            goto L_0x04a4
        L_0x0490:
            java.lang.String r5 = "CHANNEL_"
            boolean r5 = r9.startsWith(r5)     // Catch:{ all -> 0x1798 }
            r14 = r6
            if (r5 == 0) goto L_0x049f
            r5 = 0
            r6 = 0
            r11 = 0
            r26 = 1
            goto L_0x04a4
        L_0x049f:
            r5 = 0
            r6 = 0
            r11 = 0
            r26 = 0
        L_0x04a4:
            boolean r25 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1798 }
            if (r25 == 0) goto L_0x04cf
            r25 = r14
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x044d }
            r14.<init>()     // Catch:{ all -> 0x044d }
            r31 = r11
            java.lang.String r11 = "GCM received message notification "
            r14.append(r11)     // Catch:{ all -> 0x044d }
            r14.append(r9)     // Catch:{ all -> 0x044d }
            r14.append(r10)     // Catch:{ all -> 0x044d }
            r14.append(r12)     // Catch:{ all -> 0x044d }
            java.lang.String r10 = " mid = "
            r14.append(r10)     // Catch:{ all -> 0x044d }
            r14.append(r7)     // Catch:{ all -> 0x044d }
            java.lang.String r10 = r14.toString()     // Catch:{ all -> 0x044d }
            org.telegram.messenger.FileLog.d(r10)     // Catch:{ all -> 0x044d }
            goto L_0x04d3
        L_0x04cf:
            r31 = r11
            r25 = r14
        L_0x04d3:
            int r10 = r9.hashCode()     // Catch:{ all -> 0x1798 }
            switch(r10) {
                case -2100047043: goto L_0x0958;
                case -2091498420: goto L_0x094d;
                case -2053872415: goto L_0x0942;
                case -2039746363: goto L_0x0937;
                case -2023218804: goto L_0x092c;
                case -1979538588: goto L_0x0921;
                case -1979536003: goto L_0x0916;
                case -1979535888: goto L_0x090b;
                case -1969004705: goto L_0x0900;
                case -1946699248: goto L_0x08f4;
                case -1528047021: goto L_0x08e8;
                case -1493579426: goto L_0x08dc;
                case -1482481933: goto L_0x08d0;
                case -1480102982: goto L_0x08c5;
                case -1478041834: goto L_0x08b9;
                case -1474543101: goto L_0x08ae;
                case -1465695932: goto L_0x08a2;
                case -1374906292: goto L_0x0896;
                case -1372940586: goto L_0x088a;
                case -1264245338: goto L_0x087e;
                case -1236086700: goto L_0x0872;
                case -1236077786: goto L_0x0866;
                case -1235796237: goto L_0x085a;
                case -1235686303: goto L_0x084f;
                case -1198046100: goto L_0x0844;
                case -1124254527: goto L_0x0838;
                case -1085137927: goto L_0x082c;
                case -1084856378: goto L_0x0820;
                case -1084746444: goto L_0x0814;
                case -819729482: goto L_0x0808;
                case -772141857: goto L_0x07fc;
                case -638310039: goto L_0x07f0;
                case -590403924: goto L_0x07e4;
                case -589196239: goto L_0x07d8;
                case -589193654: goto L_0x07cc;
                case -589193539: goto L_0x07c0;
                case -440169325: goto L_0x07b4;
                case -412748110: goto L_0x07a8;
                case -228518075: goto L_0x079c;
                case -213586509: goto L_0x0790;
                case -115582002: goto L_0x0784;
                case -112621464: goto L_0x0778;
                case -108522133: goto L_0x076c;
                case -107572034: goto L_0x0761;
                case -40534265: goto L_0x0755;
                case 65254746: goto L_0x0749;
                case 141040782: goto L_0x073d;
                case 309993049: goto L_0x0731;
                case 309995634: goto L_0x0725;
                case 309995749: goto L_0x0719;
                case 320532812: goto L_0x070d;
                case 328933854: goto L_0x0701;
                case 331340546: goto L_0x06f5;
                case 344816990: goto L_0x06e9;
                case 346878138: goto L_0x06dd;
                case 350376871: goto L_0x06d1;
                case 615714517: goto L_0x06c6;
                case 715508879: goto L_0x06ba;
                case 728985323: goto L_0x06ae;
                case 731046471: goto L_0x06a2;
                case 734545204: goto L_0x0696;
                case 802032552: goto L_0x068a;
                case 991498806: goto L_0x067e;
                case 1007364121: goto L_0x0672;
                case 1019917311: goto L_0x0666;
                case 1019926225: goto L_0x065a;
                case 1020207774: goto L_0x064e;
                case 1020317708: goto L_0x0642;
                case 1060349560: goto L_0x0636;
                case 1060358474: goto L_0x062a;
                case 1060640023: goto L_0x061e;
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
            goto L_0x0963
        L_0x04dc:
            java.lang.String r10 = "CHAT_MESSAGE_GEOLIVE"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 53
            goto L_0x0964
        L_0x04e8:
            java.lang.String r10 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 39
            goto L_0x0964
        L_0x04f4:
            java.lang.String r10 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 25
            goto L_0x0964
        L_0x0500:
            java.lang.String r10 = "PINNED_CONTACT"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 80
            goto L_0x0964
        L_0x050c:
            java.lang.String r10 = "CHAT_PHOTO_EDITED"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 61
            goto L_0x0964
        L_0x0518:
            java.lang.String r10 = "LOCKED_MESSAGE"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 92
            goto L_0x0964
        L_0x0524:
            java.lang.String r10 = "CHANNEL_MESSAGES"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 41
            goto L_0x0964
        L_0x0530:
            java.lang.String r10 = "MESSAGE_INVOICE"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 20
            goto L_0x0964
        L_0x053c:
            java.lang.String r10 = "CHAT_MESSAGE_VIDEO"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 45
            goto L_0x0964
        L_0x0548:
            java.lang.String r10 = "CHAT_MESSAGE_ROUND"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 46
            goto L_0x0964
        L_0x0554:
            java.lang.String r10 = "CHAT_MESSAGE_PHOTO"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 44
            goto L_0x0964
        L_0x0560:
            java.lang.String r10 = "CHAT_MESSAGE_AUDIO"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 49
            goto L_0x0964
        L_0x056c:
            java.lang.String r10 = "MESSAGE_VIDEOS"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 23
            goto L_0x0964
        L_0x0578:
            java.lang.String r10 = "PHONE_CALL_MISSED"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 97
            goto L_0x0964
        L_0x0584:
            java.lang.String r10 = "MESSAGE_PHOTOS"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 22
            goto L_0x0964
        L_0x0590:
            java.lang.String r10 = "CHAT_MESSAGE_VIDEOS"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 70
            goto L_0x0964
        L_0x059c:
            java.lang.String r10 = "MESSAGE_NOTEXT"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 2
            goto L_0x0964
        L_0x05a7:
            java.lang.String r10 = "MESSAGE_GIF"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 16
            goto L_0x0964
        L_0x05b3:
            java.lang.String r10 = "MESSAGE_GEO"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 14
            goto L_0x0964
        L_0x05bf:
            java.lang.String r10 = "MESSAGE_DOC"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 9
            goto L_0x0964
        L_0x05cb:
            java.lang.String r10 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 56
            goto L_0x0964
        L_0x05d7:
            java.lang.String r10 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 35
            goto L_0x0964
        L_0x05e3:
            java.lang.String r10 = "CHAT_MESSAGE_PHOTOS"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 69
            goto L_0x0964
        L_0x05ef:
            java.lang.String r10 = "CHAT_MESSAGE_NOTEXT"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 43
            goto L_0x0964
        L_0x05fb:
            java.lang.String r10 = "CHAT_TITLE_EDITED"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 60
            goto L_0x0964
        L_0x0607:
            java.lang.String r10 = "PINNED_NOTEXT"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 73
            goto L_0x0964
        L_0x0613:
            java.lang.String r10 = "MESSAGE_TEXT"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 0
            goto L_0x0964
        L_0x061e:
            java.lang.String r10 = "MESSAGE_POLL"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 13
            goto L_0x0964
        L_0x062a:
            java.lang.String r10 = "MESSAGE_GAME"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 17
            goto L_0x0964
        L_0x0636:
            java.lang.String r10 = "MESSAGE_FWDS"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 21
            goto L_0x0964
        L_0x0642:
            java.lang.String r10 = "CHAT_MESSAGE_TEXT"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 42
            goto L_0x0964
        L_0x064e:
            java.lang.String r10 = "CHAT_MESSAGE_POLL"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 51
            goto L_0x0964
        L_0x065a:
            java.lang.String r10 = "CHAT_MESSAGE_GAME"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 55
            goto L_0x0964
        L_0x0666:
            java.lang.String r10 = "CHAT_MESSAGE_FWDS"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 68
            goto L_0x0964
        L_0x0672:
            java.lang.String r10 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 19
            goto L_0x0964
        L_0x067e:
            java.lang.String r10 = "PINNED_GEOLIVE"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 83
            goto L_0x0964
        L_0x068a:
            java.lang.String r10 = "MESSAGE_CONTACT"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 12
            goto L_0x0964
        L_0x0696:
            java.lang.String r10 = "PINNED_VIDEO"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 75
            goto L_0x0964
        L_0x06a2:
            java.lang.String r10 = "PINNED_ROUND"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 76
            goto L_0x0964
        L_0x06ae:
            java.lang.String r10 = "PINNED_PHOTO"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 74
            goto L_0x0964
        L_0x06ba:
            java.lang.String r10 = "PINNED_AUDIO"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 79
            goto L_0x0964
        L_0x06c6:
            java.lang.String r10 = "MESSAGE_PHOTO_SECRET"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 4
            goto L_0x0964
        L_0x06d1:
            java.lang.String r10 = "CHANNEL_MESSAGE_VIDEO"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 27
            goto L_0x0964
        L_0x06dd:
            java.lang.String r10 = "CHANNEL_MESSAGE_ROUND"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 28
            goto L_0x0964
        L_0x06e9:
            java.lang.String r10 = "CHANNEL_MESSAGE_PHOTO"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 26
            goto L_0x0964
        L_0x06f5:
            java.lang.String r10 = "CHANNEL_MESSAGE_AUDIO"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 31
            goto L_0x0964
        L_0x0701:
            java.lang.String r10 = "CHAT_MESSAGE_STICKER"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 48
            goto L_0x0964
        L_0x070d:
            java.lang.String r10 = "MESSAGES"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 24
            goto L_0x0964
        L_0x0719:
            java.lang.String r10 = "CHAT_MESSAGE_GIF"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 54
            goto L_0x0964
        L_0x0725:
            java.lang.String r10 = "CHAT_MESSAGE_GEO"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 52
            goto L_0x0964
        L_0x0731:
            java.lang.String r10 = "CHAT_MESSAGE_DOC"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 47
            goto L_0x0964
        L_0x073d:
            java.lang.String r10 = "CHAT_LEFT"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 65
            goto L_0x0964
        L_0x0749:
            java.lang.String r10 = "CHAT_ADD_YOU"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 59
            goto L_0x0964
        L_0x0755:
            java.lang.String r10 = "CHAT_DELETE_MEMBER"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 63
            goto L_0x0964
        L_0x0761:
            java.lang.String r10 = "MESSAGE_SCREENSHOT"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 7
            goto L_0x0964
        L_0x076c:
            java.lang.String r10 = "AUTH_REGION"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 91
            goto L_0x0964
        L_0x0778:
            java.lang.String r10 = "CONTACT_JOINED"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 89
            goto L_0x0964
        L_0x0784:
            java.lang.String r10 = "CHAT_MESSAGE_INVOICE"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 57
            goto L_0x0964
        L_0x0790:
            java.lang.String r10 = "ENCRYPTION_REQUEST"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 93
            goto L_0x0964
        L_0x079c:
            java.lang.String r10 = "MESSAGE_GEOLIVE"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 15
            goto L_0x0964
        L_0x07a8:
            java.lang.String r10 = "CHAT_DELETE_YOU"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 64
            goto L_0x0964
        L_0x07b4:
            java.lang.String r10 = "AUTH_UNKNOWN"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 90
            goto L_0x0964
        L_0x07c0:
            java.lang.String r10 = "PINNED_GIF"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 87
            goto L_0x0964
        L_0x07cc:
            java.lang.String r10 = "PINNED_GEO"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 82
            goto L_0x0964
        L_0x07d8:
            java.lang.String r10 = "PINNED_DOC"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 77
            goto L_0x0964
        L_0x07e4:
            java.lang.String r10 = "PINNED_GAME_SCORE"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 85
            goto L_0x0964
        L_0x07f0:
            java.lang.String r10 = "CHANNEL_MESSAGE_STICKER"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 30
            goto L_0x0964
        L_0x07fc:
            java.lang.String r10 = "PHONE_CALL_REQUEST"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 95
            goto L_0x0964
        L_0x0808:
            java.lang.String r10 = "PINNED_STICKER"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 78
            goto L_0x0964
        L_0x0814:
            java.lang.String r10 = "PINNED_TEXT"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 72
            goto L_0x0964
        L_0x0820:
            java.lang.String r10 = "PINNED_POLL"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 81
            goto L_0x0964
        L_0x082c:
            java.lang.String r10 = "PINNED_GAME"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 84
            goto L_0x0964
        L_0x0838:
            java.lang.String r10 = "CHAT_MESSAGE_CONTACT"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 50
            goto L_0x0964
        L_0x0844:
            java.lang.String r10 = "MESSAGE_VIDEO_SECRET"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 6
            goto L_0x0964
        L_0x084f:
            java.lang.String r10 = "CHANNEL_MESSAGE_TEXT"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 1
            goto L_0x0964
        L_0x085a:
            java.lang.String r10 = "CHANNEL_MESSAGE_POLL"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 33
            goto L_0x0964
        L_0x0866:
            java.lang.String r10 = "CHANNEL_MESSAGE_GAME"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 37
            goto L_0x0964
        L_0x0872:
            java.lang.String r10 = "CHANNEL_MESSAGE_FWDS"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 38
            goto L_0x0964
        L_0x087e:
            java.lang.String r10 = "PINNED_INVOICE"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 86
            goto L_0x0964
        L_0x088a:
            java.lang.String r10 = "CHAT_RETURNED"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 66
            goto L_0x0964
        L_0x0896:
            java.lang.String r10 = "ENCRYPTED_MESSAGE"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 88
            goto L_0x0964
        L_0x08a2:
            java.lang.String r10 = "ENCRYPTION_ACCEPT"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 94
            goto L_0x0964
        L_0x08ae:
            java.lang.String r10 = "MESSAGE_VIDEO"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 5
            goto L_0x0964
        L_0x08b9:
            java.lang.String r10 = "MESSAGE_ROUND"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 8
            goto L_0x0964
        L_0x08c5:
            java.lang.String r10 = "MESSAGE_PHOTO"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 3
            goto L_0x0964
        L_0x08d0:
            java.lang.String r10 = "MESSAGE_MUTED"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 96
            goto L_0x0964
        L_0x08dc:
            java.lang.String r10 = "MESSAGE_AUDIO"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 11
            goto L_0x0964
        L_0x08e8:
            java.lang.String r10 = "CHAT_MESSAGES"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 71
            goto L_0x0964
        L_0x08f4:
            java.lang.String r10 = "CHAT_JOINED"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 67
            goto L_0x0964
        L_0x0900:
            java.lang.String r10 = "CHAT_ADD_MEMBER"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 62
            goto L_0x0964
        L_0x090b:
            java.lang.String r10 = "CHANNEL_MESSAGE_GIF"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 36
            goto L_0x0964
        L_0x0916:
            java.lang.String r10 = "CHANNEL_MESSAGE_GEO"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 34
            goto L_0x0964
        L_0x0921:
            java.lang.String r10 = "CHANNEL_MESSAGE_DOC"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 29
            goto L_0x0964
        L_0x092c:
            java.lang.String r10 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 40
            goto L_0x0964
        L_0x0937:
            java.lang.String r10 = "MESSAGE_STICKER"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 10
            goto L_0x0964
        L_0x0942:
            java.lang.String r10 = "CHAT_CREATED"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 58
            goto L_0x0964
        L_0x094d:
            java.lang.String r10 = "CHANNEL_MESSAGE_CONTACT"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 32
            goto L_0x0964
        L_0x0958:
            java.lang.String r10 = "MESSAGE_GAME_SCORE"
            boolean r10 = r9.equals(r10)     // Catch:{ all -> 0x044d }
            if (r10 == 0) goto L_0x0963
            r10 = 18
            goto L_0x0964
        L_0x0963:
            r10 = -1
        L_0x0964:
            java.lang.String r14 = "ChannelMessageFew"
            java.lang.String r11 = " "
            r32 = r7
            java.lang.String r7 = "AttachSticker"
            switch(r10) {
                case 0: goto L_0x16be;
                case 1: goto L_0x16be;
                case 2: goto L_0x169b;
                case 3: goto L_0x167c;
                case 4: goto L_0x165d;
                case 5: goto L_0x163e;
                case 6: goto L_0x161e;
                case 7: goto L_0x1603;
                case 8: goto L_0x15e3;
                case 9: goto L_0x15c3;
                case 10: goto L_0x1564;
                case 11: goto L_0x1544;
                case 12: goto L_0x151f;
                case 13: goto L_0x14fa;
                case 14: goto L_0x14da;
                case 15: goto L_0x14ba;
                case 16: goto L_0x149a;
                case 17: goto L_0x1475;
                case 18: goto L_0x1454;
                case 19: goto L_0x1454;
                case 20: goto L_0x142f;
                case 21: goto L_0x1408;
                case 22: goto L_0x13df;
                case 23: goto L_0x13b6;
                case 24: goto L_0x139e;
                case 25: goto L_0x137e;
                case 26: goto L_0x135e;
                case 27: goto L_0x133e;
                case 28: goto L_0x131e;
                case 29: goto L_0x12fe;
                case 30: goto L_0x129f;
                case 31: goto L_0x127f;
                case 32: goto L_0x125a;
                case 33: goto L_0x1235;
                case 34: goto L_0x1215;
                case 35: goto L_0x11f5;
                case 36: goto L_0x11d5;
                case 37: goto L_0x11b5;
                case 38: goto L_0x1189;
                case 39: goto L_0x1161;
                case 40: goto L_0x1139;
                case 41: goto L_0x1122;
                case 42: goto L_0x10ff;
                case 43: goto L_0x10da;
                case 44: goto L_0x10b5;
                case 45: goto L_0x1090;
                case 46: goto L_0x106b;
                case 47: goto L_0x1046;
                case 48: goto L_0x0fc4;
                case 49: goto L_0x0f9d;
                case 50: goto L_0x0var_;
                case 51: goto L_0x0f4f;
                case 52: goto L_0x0f2d;
                case 53: goto L_0x0f0a;
                case 54: goto L_0x0ee7;
                case 55: goto L_0x0ebf;
                case 56: goto L_0x0e98;
                case 57: goto L_0x0e70;
                case 58: goto L_0x0e56;
                case 59: goto L_0x0e56;
                case 60: goto L_0x0e3c;
                case 61: goto L_0x0e22;
                case 62: goto L_0x0e03;
                case 63: goto L_0x0de9;
                case 64: goto L_0x0dcf;
                case 65: goto L_0x0db5;
                case 66: goto L_0x0d9b;
                case 67: goto L_0x0d81;
                case 68: goto L_0x0d53;
                case 69: goto L_0x0d26;
                case 70: goto L_0x0cf9;
                case 71: goto L_0x0cdd;
                case 72: goto L_0x0ca6;
                case 73: goto L_0x0CLASSNAME;
                case 74: goto L_0x0CLASSNAME;
                case 75: goto L_0x0c1c;
                case 76: goto L_0x0bed;
                case 77: goto L_0x0bbe;
                case 78: goto L_0x0b42;
                case 79: goto L_0x0b13;
                case 80: goto L_0x0ada;
                case 81: goto L_0x0aa5;
                case 82: goto L_0x0a75;
                case 83: goto L_0x0a4a;
                case 84: goto L_0x0a1f;
                case 85: goto L_0x09f2;
                case 86: goto L_0x09c5;
                case 87: goto L_0x0998;
                case 88: goto L_0x097d;
                case 89: goto L_0x0977;
                case 90: goto L_0x0977;
                case 91: goto L_0x0977;
                case 92: goto L_0x0977;
                case 93: goto L_0x0977;
                case 94: goto L_0x0977;
                case 95: goto L_0x0977;
                case 96: goto L_0x0977;
                case 97: goto L_0x0977;
                default: goto L_0x096f;
            }
        L_0x096f:
            r21 = r1
            r10 = r32
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1798 }
            goto L_0x16db
        L_0x0977:
            r21 = r1
            r10 = r32
            goto L_0x16f2
        L_0x097d:
            java.lang.String r7 = "YouHaveNewMessage"
            r8 = 2131627180(0x7f0e0cac, float:1.8881617E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            java.lang.String r8 = "SecretChatName"
            r10 = 2131626450(0x7f0e09d2, float:1.8880137E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x044d }
            r21 = r1
            r25 = r8
            r10 = r32
        L_0x0995:
            r1 = 1
            goto L_0x16f4
        L_0x0998:
            if (r1 == 0) goto L_0x09b2
            java.lang.String r7 = "NotificationActionPinnedGif"
            r8 = 2131625694(0x7f0e06de, float:1.8878603E38)
            r10 = 2
            java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r10[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r10[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10)     // Catch:{ all -> 0x044d }
            goto L_0x0a9f
        L_0x09b2:
            java.lang.String r7 = "NotificationActionPinnedGifChannel"
            r8 = 2131625695(0x7f0e06df, float:1.8878605E38)
            r10 = 1
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ all -> 0x044d }
            r10 = 0
            r14 = r15[r10]     // Catch:{ all -> 0x044d }
            r11[r10] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0a9f
        L_0x09c5:
            if (r1 == 0) goto L_0x09df
            java.lang.String r7 = "NotificationActionPinnedInvoice"
            r8 = 2131625696(0x7f0e06e0, float:1.8878607E38)
            r10 = 2
            java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r10[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r10[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10)     // Catch:{ all -> 0x044d }
            goto L_0x0a9f
        L_0x09df:
            java.lang.String r7 = "NotificationActionPinnedInvoiceChannel"
            r8 = 2131625697(0x7f0e06e1, float:1.887861E38)
            r10 = 1
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ all -> 0x044d }
            r10 = 0
            r14 = r15[r10]     // Catch:{ all -> 0x044d }
            r11[r10] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0a9f
        L_0x09f2:
            if (r1 == 0) goto L_0x0a0c
            java.lang.String r7 = "NotificationActionPinnedGameScore"
            r8 = 2131625688(0x7f0e06d8, float:1.8878591E38)
            r10 = 2
            java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r10[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r10[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10)     // Catch:{ all -> 0x044d }
            goto L_0x0a9f
        L_0x0a0c:
            java.lang.String r7 = "NotificationActionPinnedGameScoreChannel"
            r8 = 2131625689(0x7f0e06d9, float:1.8878593E38)
            r10 = 1
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ all -> 0x044d }
            r10 = 0
            r14 = r15[r10]     // Catch:{ all -> 0x044d }
            r11[r10] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0a9f
        L_0x0a1f:
            if (r1 == 0) goto L_0x0a38
            java.lang.String r7 = "NotificationActionPinnedGame"
            r8 = 2131625686(0x7f0e06d6, float:1.8878587E38)
            r10 = 2
            java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r10[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r10[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10)     // Catch:{ all -> 0x044d }
            goto L_0x0a9f
        L_0x0a38:
            java.lang.String r7 = "NotificationActionPinnedGameChannel"
            r8 = 2131625687(0x7f0e06d7, float:1.887859E38)
            r10 = 1
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ all -> 0x044d }
            r10 = 0
            r14 = r15[r10]     // Catch:{ all -> 0x044d }
            r11[r10] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0a9f
        L_0x0a4a:
            if (r1 == 0) goto L_0x0a63
            java.lang.String r7 = "NotificationActionPinnedGeoLive"
            r8 = 2131625692(0x7f0e06dc, float:1.88786E38)
            r10 = 2
            java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r10[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r10[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10)     // Catch:{ all -> 0x044d }
            goto L_0x0a9f
        L_0x0a63:
            java.lang.String r7 = "NotificationActionPinnedGeoLiveChannel"
            r8 = 2131625693(0x7f0e06dd, float:1.8878601E38)
            r10 = 1
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ all -> 0x044d }
            r10 = 0
            r14 = r15[r10]     // Catch:{ all -> 0x044d }
            r11[r10] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0a9f
        L_0x0a75:
            if (r1 == 0) goto L_0x0a8e
            java.lang.String r7 = "NotificationActionPinnedGeo"
            r8 = 2131625690(0x7f0e06da, float:1.8878595E38)
            r10 = 2
            java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r10[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r10[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10)     // Catch:{ all -> 0x044d }
            goto L_0x0a9f
        L_0x0a8e:
            java.lang.String r7 = "NotificationActionPinnedGeoChannel"
            r8 = 2131625691(0x7f0e06db, float:1.8878597E38)
            r10 = 1
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ all -> 0x044d }
            r10 = 0
            r14 = r15[r10]     // Catch:{ all -> 0x044d }
            r11[r10] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
        L_0x0a9f:
            r21 = r1
            r10 = r32
            goto L_0x161b
        L_0x0aa5:
            if (r1 == 0) goto L_0x0ac3
            java.lang.String r7 = "NotificationActionPinnedPoll2"
            r8 = 2131625704(0x7f0e06e8, float:1.8878623E38)
            r10 = 3
            java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r10[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r10[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 2
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r10[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10)     // Catch:{ all -> 0x044d }
            goto L_0x0a9f
        L_0x0ac3:
            java.lang.String r7 = "NotificationActionPinnedPollChannel2"
            r8 = 2131625705(0x7f0e06e9, float:1.8878625E38)
            r10 = 2
            java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r10[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r10[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10)     // Catch:{ all -> 0x044d }
            goto L_0x0a9f
        L_0x0ada:
            r10 = r32
            if (r1 == 0) goto L_0x0afb
            java.lang.String r8 = "NotificationActionPinnedContact2"
            r11 = 2131625682(0x7f0e06d2, float:1.8878579E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x044d }
            r14 = 0
            r18 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r18     // Catch:{ all -> 0x044d }
            r14 = 1
            r18 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r18     // Catch:{ all -> 0x044d }
            r14 = 2
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0afb:
            java.lang.String r7 = "NotificationActionPinnedContactChannel2"
            r8 = 2131625683(0x7f0e06d3, float:1.887858E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0b13:
            r10 = r32
            if (r1 == 0) goto L_0x0b2f
            java.lang.String r7 = "NotificationActionPinnedVoice"
            r8 = 2131625716(0x7f0e06f4, float:1.8878648E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0b2f:
            java.lang.String r7 = "NotificationActionPinnedVoiceChannel"
            r8 = 2131625717(0x7f0e06f5, float:1.887865E38)
            r11 = 1
            java.lang.Object[] r14 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r11 = 0
            r15 = r15[r11]     // Catch:{ all -> 0x044d }
            r14[r11] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0b42:
            r10 = r32
            if (r1 == 0) goto L_0x0b87
            int r8 = r15.length     // Catch:{ all -> 0x044d }
            r11 = 2
            if (r8 <= r11) goto L_0x0b6f
            r8 = r15[r11]     // Catch:{ all -> 0x044d }
            boolean r8 = android.text.TextUtils.isEmpty(r8)     // Catch:{ all -> 0x044d }
            if (r8 != 0) goto L_0x0b6f
            java.lang.String r8 = "NotificationActionPinnedStickerEmoji"
            r11 = 2131625710(0x7f0e06ee, float:1.8878636E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x044d }
            r14 = 0
            r18 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r18     // Catch:{ all -> 0x044d }
            r14 = 1
            r18 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r18     // Catch:{ all -> 0x044d }
            r14 = 2
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0b6f:
            java.lang.String r7 = "NotificationActionPinnedSticker"
            r8 = 2131625708(0x7f0e06ec, float:1.8878632E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0b87:
            int r7 = r15.length     // Catch:{ all -> 0x044d }
            r8 = 1
            if (r7 <= r8) goto L_0x0bab
            r7 = r15[r8]     // Catch:{ all -> 0x044d }
            boolean r7 = android.text.TextUtils.isEmpty(r7)     // Catch:{ all -> 0x044d }
            if (r7 != 0) goto L_0x0bab
            java.lang.String r7 = "NotificationActionPinnedStickerEmojiChannel"
            r8 = 2131625711(0x7f0e06ef, float:1.8878638E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0bab:
            java.lang.String r7 = "NotificationActionPinnedStickerChannel"
            r8 = 2131625709(0x7f0e06ed, float:1.8878634E38)
            r11 = 1
            java.lang.Object[] r14 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r11 = 0
            r15 = r15[r11]     // Catch:{ all -> 0x044d }
            r14[r11] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0bbe:
            r10 = r32
            if (r1 == 0) goto L_0x0bda
            java.lang.String r7 = "NotificationActionPinnedFile"
            r8 = 2131625684(0x7f0e06d4, float:1.8878583E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0bda:
            java.lang.String r7 = "NotificationActionPinnedFileChannel"
            r8 = 2131625685(0x7f0e06d5, float:1.8878585E38)
            r11 = 1
            java.lang.Object[] r14 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r11 = 0
            r15 = r15[r11]     // Catch:{ all -> 0x044d }
            r14[r11] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0bed:
            r10 = r32
            if (r1 == 0) goto L_0x0CLASSNAME
            java.lang.String r7 = "NotificationActionPinnedRound"
            r8 = 2131625706(0x7f0e06ea, float:1.8878628E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0CLASSNAME:
            java.lang.String r7 = "NotificationActionPinnedRoundChannel"
            r8 = 2131625707(0x7f0e06eb, float:1.887863E38)
            r11 = 1
            java.lang.Object[] r14 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r11 = 0
            r15 = r15[r11]     // Catch:{ all -> 0x044d }
            r14[r11] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0c1c:
            r10 = r32
            if (r1 == 0) goto L_0x0CLASSNAME
            java.lang.String r7 = "NotificationActionPinnedVideo"
            r8 = 2131625714(0x7f0e06f2, float:1.8878644E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0CLASSNAME:
            java.lang.String r7 = "NotificationActionPinnedVideoChannel"
            r8 = 2131625715(0x7f0e06f3, float:1.8878646E38)
            r11 = 1
            java.lang.Object[] r14 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r11 = 0
            r15 = r15[r11]     // Catch:{ all -> 0x044d }
            r14[r11] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0CLASSNAME:
            r10 = r32
            if (r1 == 0) goto L_0x0CLASSNAME
            java.lang.String r7 = "NotificationActionPinnedPhoto"
            r8 = 2131625702(0x7f0e06e6, float:1.887862E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0CLASSNAME:
            java.lang.String r7 = "NotificationActionPinnedPhotoChannel"
            r8 = 2131625703(0x7f0e06e7, float:1.8878621E38)
            r11 = 1
            java.lang.Object[] r14 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r11 = 0
            r15 = r15[r11]     // Catch:{ all -> 0x044d }
            r14[r11] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0CLASSNAME:
            r10 = r32
            if (r1 == 0) goto L_0x0CLASSNAME
            java.lang.String r7 = "NotificationActionPinnedNoText"
            r8 = 2131625700(0x7f0e06e4, float:1.8878615E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0CLASSNAME:
            java.lang.String r7 = "NotificationActionPinnedNoTextChannel"
            r8 = 2131625701(0x7f0e06e5, float:1.8878617E38)
            r11 = 1
            java.lang.Object[] r14 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r11 = 0
            r15 = r15[r11]     // Catch:{ all -> 0x044d }
            r14[r11] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14)     // Catch:{ all -> 0x044d }
        L_0x0ca2:
            r21 = r1
            goto L_0x161b
        L_0x0ca6:
            r10 = r32
            if (r1 == 0) goto L_0x0cc6
            java.lang.String r8 = "NotificationActionPinnedText"
            r11 = 2131625712(0x7f0e06f0, float:1.887864E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x044d }
            r14 = 0
            r18 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r18     // Catch:{ all -> 0x044d }
            r14 = 1
            r18 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r18     // Catch:{ all -> 0x044d }
            r14 = 2
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0cc6:
            java.lang.String r7 = "NotificationActionPinnedTextChannel"
            r8 = 2131625713(0x7f0e06f1, float:1.8878642E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0cdd:
            r10 = r32
            java.lang.String r7 = "NotificationGroupAlbum"
            r8 = 2131625725(0x7f0e06fd, float:1.8878666E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
        L_0x0cf5:
            r21 = r1
            goto L_0x0995
        L_0x0cf9:
            r10 = r32
            java.lang.String r8 = "NotificationGroupFew"
            r11 = 2131625726(0x7f0e06fe, float:1.8878668E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x044d }
            r14 = 0
            r18 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r18     // Catch:{ all -> 0x044d }
            r14 = 1
            r18 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r18     // Catch:{ all -> 0x044d }
            java.lang.String r14 = "Videos"
            r17 = 2
            r15 = r15[r17]     // Catch:{ all -> 0x044d }
            java.lang.Integer r15 = org.telegram.messenger.Utilities.parseInt(r15)     // Catch:{ all -> 0x044d }
            int r15 = r15.intValue()     // Catch:{ all -> 0x044d }
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatPluralString(r14, r15)     // Catch:{ all -> 0x044d }
            r7[r17] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7)     // Catch:{ all -> 0x044d }
            goto L_0x0cf5
        L_0x0d26:
            r10 = r32
            java.lang.String r8 = "NotificationGroupFew"
            r11 = 2131625726(0x7f0e06fe, float:1.8878668E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x044d }
            r14 = 0
            r18 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r18     // Catch:{ all -> 0x044d }
            r14 = 1
            r18 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r18     // Catch:{ all -> 0x044d }
            java.lang.String r14 = "Photos"
            r17 = 2
            r15 = r15[r17]     // Catch:{ all -> 0x044d }
            java.lang.Integer r15 = org.telegram.messenger.Utilities.parseInt(r15)     // Catch:{ all -> 0x044d }
            int r15 = r15.intValue()     // Catch:{ all -> 0x044d }
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatPluralString(r14, r15)     // Catch:{ all -> 0x044d }
            r7[r17] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7)     // Catch:{ all -> 0x044d }
            goto L_0x0cf5
        L_0x0d53:
            r10 = r32
            java.lang.String r11 = "NotificationGroupForwardedFew"
            r14 = 2131625727(0x7f0e06ff, float:1.887867E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x044d }
            r18 = 0
            r21 = r15[r18]     // Catch:{ all -> 0x044d }
            r7[r18] = r21     // Catch:{ all -> 0x044d }
            r18 = 1
            r21 = r15[r18]     // Catch:{ all -> 0x044d }
            r7[r18] = r21     // Catch:{ all -> 0x044d }
            r17 = 2
            r15 = r15[r17]     // Catch:{ all -> 0x044d }
            java.lang.Integer r15 = org.telegram.messenger.Utilities.parseInt(r15)     // Catch:{ all -> 0x044d }
            int r15 = r15.intValue()     // Catch:{ all -> 0x044d }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r15)     // Catch:{ all -> 0x044d }
            r7[r17] = r8     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r11, r14, r7)     // Catch:{ all -> 0x044d }
            goto L_0x0cf5
        L_0x0d81:
            r10 = r32
            java.lang.String r7 = "NotificationGroupAddSelfMega"
            r8 = 2131625724(0x7f0e06fc, float:1.8878664E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0d9b:
            r10 = r32
            java.lang.String r7 = "NotificationGroupAddSelf"
            r8 = 2131625723(0x7f0e06fb, float:1.8878662E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0db5:
            r10 = r32
            java.lang.String r7 = "NotificationGroupLeftMember"
            r8 = 2131625730(0x7f0e0702, float:1.8878676E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0dcf:
            r10 = r32
            java.lang.String r7 = "NotificationGroupKickYou"
            r8 = 2131625729(0x7f0e0701, float:1.8878674E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0de9:
            r10 = r32
            java.lang.String r7 = "NotificationGroupKickMember"
            r8 = 2131625728(0x7f0e0700, float:1.8878672E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0e03:
            r10 = r32
            java.lang.String r8 = "NotificationGroupAddMember"
            r11 = 2131625722(0x7f0e06fa, float:1.887866E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x044d }
            r14 = 0
            r18 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r18     // Catch:{ all -> 0x044d }
            r14 = 1
            r18 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r18     // Catch:{ all -> 0x044d }
            r14 = 2
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0e22:
            r10 = r32
            java.lang.String r7 = "NotificationEditedGroupPhoto"
            r8 = 2131625721(0x7f0e06f9, float:1.8878658E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0e3c:
            r10 = r32
            java.lang.String r7 = "NotificationEditedGroupName"
            r8 = 2131625720(0x7f0e06f8, float:1.8878656E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0e56:
            r10 = r32
            java.lang.String r7 = "NotificationInvitedToGroup"
            r8 = 2131625735(0x7f0e0707, float:1.8878686E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0e70:
            r10 = r32
            java.lang.String r8 = "NotificationMessageGroupInvoice"
            r11 = 2131625752(0x7f0e0718, float:1.887872E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x044d }
            r14 = 0
            r16 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r16     // Catch:{ all -> 0x044d }
            r14 = 1
            r16 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r16     // Catch:{ all -> 0x044d }
            r14 = 2
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7)     // Catch:{ all -> 0x044d }
            java.lang.String r8 = "PaymentInvoice"
            r11 = 2131626110(0x7f0e087e, float:1.8879447E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0fbe
        L_0x0e98:
            r10 = r32
            java.lang.String r8 = "NotificationMessageGroupGameScored"
            r11 = 2131625750(0x7f0e0716, float:1.8878717E38)
            r14 = 4
            java.lang.Object[] r14 = new java.lang.Object[r14]     // Catch:{ all -> 0x044d }
            r18 = 0
            r21 = r15[r18]     // Catch:{ all -> 0x044d }
            r14[r18] = r21     // Catch:{ all -> 0x044d }
            r18 = 1
            r21 = r15[r18]     // Catch:{ all -> 0x044d }
            r14[r18] = r21     // Catch:{ all -> 0x044d }
            r17 = 2
            r18 = r15[r17]     // Catch:{ all -> 0x044d }
            r14[r17] = r18     // Catch:{ all -> 0x044d }
            r7 = 3
            r15 = r15[r7]     // Catch:{ all -> 0x044d }
            r14[r7] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r14)     // Catch:{ all -> 0x044d }
            goto L_0x0ca2
        L_0x0ebf:
            r10 = r32
            java.lang.String r8 = "NotificationMessageGroupGame"
            r11 = 2131625749(0x7f0e0715, float:1.8878715E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x044d }
            r14 = 0
            r16 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r16     // Catch:{ all -> 0x044d }
            r14 = 1
            r16 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r16     // Catch:{ all -> 0x044d }
            r14 = 2
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7)     // Catch:{ all -> 0x044d }
            java.lang.String r8 = "AttachGame"
            r11 = 2131624270(0x7f0e014e, float:1.8875715E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0fbe
        L_0x0ee7:
            r10 = r32
            java.lang.String r7 = "NotificationMessageGroupGif"
            r8 = 2131625751(0x7f0e0717, float:1.8878719E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r16 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r16     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r8 = "AttachGif"
            r11 = 2131624271(0x7f0e014f, float:1.8875717E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0fbe
        L_0x0f0a:
            r10 = r32
            java.lang.String r7 = "NotificationMessageGroupLiveLocation"
            r8 = 2131625753(0x7f0e0719, float:1.8878723E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r16 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r16     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r8 = "AttachLiveLocation"
            r11 = 2131624276(0x7f0e0154, float:1.8875727E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0fbe
        L_0x0f2d:
            r10 = r32
            java.lang.String r7 = "NotificationMessageGroupMap"
            r8 = 2131625754(0x7f0e071a, float:1.8878725E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r16 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r16     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r8 = "AttachLocation"
            r11 = 2131624278(0x7f0e0156, float:1.8875731E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0fbe
        L_0x0f4f:
            r10 = r32
            java.lang.String r8 = "NotificationMessageGroupPoll2"
            r11 = 2131625758(0x7f0e071e, float:1.8878733E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x044d }
            r14 = 0
            r16 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r16     // Catch:{ all -> 0x044d }
            r14 = 1
            r16 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r16     // Catch:{ all -> 0x044d }
            r14 = 2
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7)     // Catch:{ all -> 0x044d }
            java.lang.String r8 = "Poll"
            r11 = 2131626221(0x7f0e08ed, float:1.8879672E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0fbe
        L_0x0var_:
            r10 = r32
            java.lang.String r8 = "NotificationMessageGroupContact2"
            r11 = 2131625747(0x7f0e0713, float:1.887871E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x044d }
            r14 = 0
            r16 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r16     // Catch:{ all -> 0x044d }
            r14 = 1
            r16 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r16     // Catch:{ all -> 0x044d }
            r14 = 2
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r7[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7)     // Catch:{ all -> 0x044d }
            java.lang.String r8 = "AttachContact"
            r11 = 2131624266(0x7f0e014a, float:1.8875707E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r11)     // Catch:{ all -> 0x044d }
            goto L_0x0fbe
        L_0x0f9d:
            r10 = r32
            java.lang.String r7 = "NotificationMessageGroupAudio"
            r8 = 2131625746(0x7f0e0712, float:1.8878709E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r16 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r16     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r8 = "AttachAudio"
            r11 = 2131624264(0x7f0e0148, float:1.8875703E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r11)     // Catch:{ all -> 0x044d }
        L_0x0fbe:
            r21 = r1
            r16 = r8
            goto L_0x16bc
        L_0x0fc4:
            r10 = r32
            int r14 = r15.length     // Catch:{ all -> 0x044d }
            r8 = 2
            if (r14 <= r8) goto L_0x100f
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            boolean r8 = android.text.TextUtils.isEmpty(r14)     // Catch:{ all -> 0x044d }
            if (r8 != 0) goto L_0x100f
            java.lang.String r8 = "NotificationMessageGroupStickerEmoji"
            r14 = 3
            java.lang.Object[] r14 = new java.lang.Object[r14]     // Catch:{ all -> 0x044d }
            r18 = 0
            r21 = r15[r18]     // Catch:{ all -> 0x044d }
            r14[r18] = r21     // Catch:{ all -> 0x044d }
            r18 = 1
            r21 = r15[r18]     // Catch:{ all -> 0x044d }
            r14[r18] = r21     // Catch:{ all -> 0x044d }
            r17 = 2
            r18 = r15[r17]     // Catch:{ all -> 0x044d }
            r14[r17] = r18     // Catch:{ all -> 0x044d }
            r21 = r1
            r1 = 2131625761(0x7f0e0721, float:1.887874E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r8, r1, r14)     // Catch:{ all -> 0x044d }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x044d }
            r8.<init>()     // Catch:{ all -> 0x044d }
            r14 = r15[r17]     // Catch:{ all -> 0x044d }
            r8.append(r14)     // Catch:{ all -> 0x044d }
            r8.append(r11)     // Catch:{ all -> 0x044d }
            r11 = 2131624285(0x7f0e015d, float:1.8875745E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r11)     // Catch:{ all -> 0x044d }
            r8.append(r7)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = r8.toString()     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x100f:
            r21 = r1
            java.lang.String r1 = "NotificationMessageGroupSticker"
            r8 = 2131625760(0x7f0e0720, float:1.8878737E38)
            r14 = 2
            java.lang.Object[] r14 = new java.lang.Object[r14]     // Catch:{ all -> 0x044d }
            r16 = 0
            r17 = r15[r16]     // Catch:{ all -> 0x044d }
            r14[r16] = r17     // Catch:{ all -> 0x044d }
            r16 = 1
            r17 = r15[r16]     // Catch:{ all -> 0x044d }
            r14[r16] = r17     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14)     // Catch:{ all -> 0x044d }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x044d }
            r8.<init>()     // Catch:{ all -> 0x044d }
            r14 = r15[r16]     // Catch:{ all -> 0x044d }
            r8.append(r14)     // Catch:{ all -> 0x044d }
            r8.append(r11)     // Catch:{ all -> 0x044d }
            r11 = 2131624285(0x7f0e015d, float:1.8875745E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r11)     // Catch:{ all -> 0x044d }
            r8.append(r7)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = r8.toString()     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x1046:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageGroupDocument"
            r7 = 2131625748(0x7f0e0714, float:1.8878713E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachDocument"
            r8 = 2131624269(0x7f0e014d, float:1.8875713E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x106b:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageGroupRound"
            r7 = 2131625759(0x7f0e071f, float:1.8878735E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachRound"
            r8 = 2131624284(0x7f0e015c, float:1.8875743E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x1090:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageGroupVideo"
            r7 = 2131625763(0x7f0e0723, float:1.8878743E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachVideo"
            r8 = 2131624288(0x7f0e0160, float:1.8875751E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x10b5:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageGroupPhoto"
            r7 = 2131625757(0x7f0e071d, float:1.887873E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachPhoto"
            r8 = 2131624282(0x7f0e015a, float:1.887574E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x10da:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageGroupNoText"
            r7 = 2131625756(0x7f0e071c, float:1.8878729E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "Message"
            r8 = 2131625532(0x7f0e063c, float:1.8878275E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x10ff:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageGroupText"
            r8 = 2131625762(0x7f0e0722, float:1.8878741E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r7[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r7[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 2
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r7[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r7)     // Catch:{ all -> 0x044d }
            r7 = r15[r11]     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x1122:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "ChannelMessageAlbum"
            r7 = 2131624544(0x7f0e0260, float:1.887627E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            goto L_0x13b3
        L_0x1139:
            r21 = r1
            r10 = r32
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x044d }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x044d }
            r1[r7] = r8     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "Videos"
            r8 = 1
            r11 = r15[r8]     // Catch:{ all -> 0x044d }
            java.lang.Integer r11 = org.telegram.messenger.Utilities.parseInt(r11)     // Catch:{ all -> 0x044d }
            int r11 = r11.intValue()     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r11)     // Catch:{ all -> 0x044d }
            r1[r8] = r7     // Catch:{ all -> 0x044d }
            r7 = 2131624548(0x7f0e0264, float:1.8876279E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r14, r7, r1)     // Catch:{ all -> 0x044d }
            goto L_0x13b3
        L_0x1161:
            r21 = r1
            r10 = r32
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x044d }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x044d }
            r1[r7] = r8     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "Photos"
            r8 = 1
            r11 = r15[r8]     // Catch:{ all -> 0x044d }
            java.lang.Integer r11 = org.telegram.messenger.Utilities.parseInt(r11)     // Catch:{ all -> 0x044d }
            int r11 = r11.intValue()     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r11)     // Catch:{ all -> 0x044d }
            r1[r8] = r7     // Catch:{ all -> 0x044d }
            r7 = 2131624548(0x7f0e0264, float:1.8876279E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r14, r7, r1)     // Catch:{ all -> 0x044d }
            goto L_0x13b3
        L_0x1189:
            r21 = r1
            r10 = r32
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x044d }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x044d }
            r1[r7] = r8     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "ForwardedMessageCount"
            r8 = 1
            r11 = r15[r8]     // Catch:{ all -> 0x044d }
            java.lang.Integer r11 = org.telegram.messenger.Utilities.parseInt(r11)     // Catch:{ all -> 0x044d }
            int r11 = r11.intValue()     // Catch:{ all -> 0x044d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = r7.toLowerCase()     // Catch:{ all -> 0x044d }
            r1[r8] = r7     // Catch:{ all -> 0x044d }
            r7 = 2131624548(0x7f0e0264, float:1.8876279E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r14, r7, r1)     // Catch:{ all -> 0x044d }
            goto L_0x13b3
        L_0x11b5:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageGame"
            r7 = 2131625743(0x7f0e070f, float:1.8878703E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachGame"
            r8 = 2131624270(0x7f0e014e, float:1.8875715E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x11d5:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "ChannelMessageGIF"
            r7 = 2131624549(0x7f0e0265, float:1.887628E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachGif"
            r8 = 2131624271(0x7f0e014f, float:1.8875717E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x11f5:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "ChannelMessageLiveLocation"
            r7 = 2131624550(0x7f0e0266, float:1.8876283E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachLiveLocation"
            r8 = 2131624276(0x7f0e0154, float:1.8875727E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x1215:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "ChannelMessageMap"
            r7 = 2131624551(0x7f0e0267, float:1.8876285E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachLocation"
            r8 = 2131624278(0x7f0e0156, float:1.8875731E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x1235:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "ChannelMessagePoll2"
            r7 = 2131624555(0x7f0e026b, float:1.8876293E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "Poll"
            r8 = 2131626221(0x7f0e08ed, float:1.8879672E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x125a:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "ChannelMessageContact2"
            r7 = 2131624546(0x7f0e0262, float:1.8876275E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachContact"
            r8 = 2131624266(0x7f0e014a, float:1.8875707E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x127f:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "ChannelMessageAudio"
            r7 = 2131624545(0x7f0e0261, float:1.8876273E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachAudio"
            r8 = 2131624264(0x7f0e0148, float:1.8875703E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x129f:
            r21 = r1
            r10 = r32
            int r1 = r15.length     // Catch:{ all -> 0x044d }
            r8 = 1
            if (r1 <= r8) goto L_0x12e4
            r1 = r15[r8]     // Catch:{ all -> 0x044d }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x044d }
            if (r1 != 0) goto L_0x12e4
            java.lang.String r1 = "ChannelMessageStickerEmoji"
            r8 = 2131624558(0x7f0e026e, float:1.88763E38)
            r14 = 2
            java.lang.Object[] r14 = new java.lang.Object[r14]     // Catch:{ all -> 0x044d }
            r16 = 0
            r17 = r15[r16]     // Catch:{ all -> 0x044d }
            r14[r16] = r17     // Catch:{ all -> 0x044d }
            r16 = 1
            r17 = r15[r16]     // Catch:{ all -> 0x044d }
            r14[r16] = r17     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14)     // Catch:{ all -> 0x044d }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x044d }
            r8.<init>()     // Catch:{ all -> 0x044d }
            r14 = r15[r16]     // Catch:{ all -> 0x044d }
            r8.append(r14)     // Catch:{ all -> 0x044d }
            r8.append(r11)     // Catch:{ all -> 0x044d }
            r11 = 2131624285(0x7f0e015d, float:1.8875745E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r11)     // Catch:{ all -> 0x044d }
            r8.append(r7)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = r8.toString()     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x12e4:
            java.lang.String r1 = "ChannelMessageSticker"
            r8 = 2131624557(0x7f0e026d, float:1.8876297E38)
            r11 = 1
            java.lang.Object[] r14 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r11 = 0
            r15 = r15[r11]     // Catch:{ all -> 0x044d }
            r14[r11] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14)     // Catch:{ all -> 0x044d }
            r8 = 2131624285(0x7f0e015d, float:1.8875745E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x12fe:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "ChannelMessageDocument"
            r7 = 2131624547(0x7f0e0263, float:1.8876277E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachDocument"
            r8 = 2131624269(0x7f0e014d, float:1.8875713E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x131e:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "ChannelMessageRound"
            r7 = 2131624556(0x7f0e026c, float:1.8876295E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachRound"
            r8 = 2131624284(0x7f0e015c, float:1.8875743E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x133e:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "ChannelMessageVideo"
            r7 = 2131624559(0x7f0e026f, float:1.8876301E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachVideo"
            r8 = 2131624288(0x7f0e0160, float:1.8875751E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x135e:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "ChannelMessagePhoto"
            r7 = 2131624554(0x7f0e026a, float:1.887629E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachPhoto"
            r8 = 2131624282(0x7f0e015a, float:1.887574E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x137e:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "ChannelMessageNoText"
            r7 = 2131624553(0x7f0e0269, float:1.8876289E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "Message"
            r8 = 2131625532(0x7f0e063c, float:1.8878275E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x139e:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageAlbum"
            r7 = 2131625737(0x7f0e0709, float:1.887869E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
        L_0x13b3:
            r7 = r1
            goto L_0x0995
        L_0x13b6:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageFew"
            r7 = 2131625741(0x7f0e070d, float:1.8878699E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r11 = "Videos"
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            java.lang.Integer r15 = org.telegram.messenger.Utilities.parseInt(r15)     // Catch:{ all -> 0x044d }
            int r15 = r15.intValue()     // Catch:{ all -> 0x044d }
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r15)     // Catch:{ all -> 0x044d }
            r8[r14] = r11     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x13b3
        L_0x13df:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageFew"
            r7 = 2131625741(0x7f0e070d, float:1.8878699E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r11 = "Photos"
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            java.lang.Integer r15 = org.telegram.messenger.Utilities.parseInt(r15)     // Catch:{ all -> 0x044d }
            int r15 = r15.intValue()     // Catch:{ all -> 0x044d }
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r15)     // Catch:{ all -> 0x044d }
            r8[r14] = r11     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x13b3
        L_0x1408:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageForwardFew"
            r7 = 2131625742(0x7f0e070e, float:1.88787E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r14 = 0
            r17 = r15[r14]     // Catch:{ all -> 0x044d }
            r11[r14] = r17     // Catch:{ all -> 0x044d }
            r14 = 1
            r15 = r15[r14]     // Catch:{ all -> 0x044d }
            java.lang.Integer r15 = org.telegram.messenger.Utilities.parseInt(r15)     // Catch:{ all -> 0x044d }
            int r15 = r15.intValue()     // Catch:{ all -> 0x044d }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r15)     // Catch:{ all -> 0x044d }
            r11[r14] = r8     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            goto L_0x13b3
        L_0x142f:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageInvoice"
            r7 = 2131625764(0x7f0e0724, float:1.8878745E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "PaymentInvoice"
            r8 = 2131626110(0x7f0e087e, float:1.8879447E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x1454:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageGameScored"
            r8 = 2131625744(0x7f0e0710, float:1.8878705E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r7[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r7[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 2
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r7[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r7)     // Catch:{ all -> 0x044d }
            goto L_0x161a
        L_0x1475:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageGame"
            r7 = 2131625743(0x7f0e070f, float:1.8878703E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachGame"
            r8 = 2131624270(0x7f0e014e, float:1.8875715E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x149a:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageGif"
            r7 = 2131625745(0x7f0e0711, float:1.8878707E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachGif"
            r8 = 2131624271(0x7f0e014f, float:1.8875717E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x14ba:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageLiveLocation"
            r7 = 2131625765(0x7f0e0725, float:1.8878747E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachLiveLocation"
            r8 = 2131624276(0x7f0e0154, float:1.8875727E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x14da:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageMap"
            r7 = 2131625766(0x7f0e0726, float:1.887875E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachLocation"
            r8 = 2131624278(0x7f0e0156, float:1.8875731E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x14fa:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessagePoll2"
            r7 = 2131625770(0x7f0e072a, float:1.8878757E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "Poll"
            r8 = 2131626221(0x7f0e08ed, float:1.8879672E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x151f:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageContact2"
            r7 = 2131625739(0x7f0e070b, float:1.8878694E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachContact"
            r8 = 2131624266(0x7f0e014a, float:1.8875707E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x1544:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageAudio"
            r7 = 2131625738(0x7f0e070a, float:1.8878692E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachAudio"
            r8 = 2131624264(0x7f0e0148, float:1.8875703E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x1564:
            r21 = r1
            r10 = r32
            int r1 = r15.length     // Catch:{ all -> 0x044d }
            r8 = 1
            if (r1 <= r8) goto L_0x15a9
            r1 = r15[r8]     // Catch:{ all -> 0x044d }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x044d }
            if (r1 != 0) goto L_0x15a9
            java.lang.String r1 = "NotificationMessageStickerEmoji"
            r8 = 2131625777(0x7f0e0731, float:1.8878772E38)
            r14 = 2
            java.lang.Object[] r14 = new java.lang.Object[r14]     // Catch:{ all -> 0x044d }
            r16 = 0
            r17 = r15[r16]     // Catch:{ all -> 0x044d }
            r14[r16] = r17     // Catch:{ all -> 0x044d }
            r16 = 1
            r17 = r15[r16]     // Catch:{ all -> 0x044d }
            r14[r16] = r17     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14)     // Catch:{ all -> 0x044d }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x044d }
            r8.<init>()     // Catch:{ all -> 0x044d }
            r14 = r15[r16]     // Catch:{ all -> 0x044d }
            r8.append(r14)     // Catch:{ all -> 0x044d }
            r8.append(r11)     // Catch:{ all -> 0x044d }
            r11 = 2131624285(0x7f0e015d, float:1.8875745E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r11)     // Catch:{ all -> 0x044d }
            r8.append(r7)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = r8.toString()     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x15a9:
            java.lang.String r1 = "NotificationMessageSticker"
            r8 = 2131625776(0x7f0e0730, float:1.887877E38)
            r11 = 1
            java.lang.Object[] r14 = new java.lang.Object[r11]     // Catch:{ all -> 0x044d }
            r11 = 0
            r15 = r15[r11]     // Catch:{ all -> 0x044d }
            r14[r11] = r15     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14)     // Catch:{ all -> 0x044d }
            r8 = 2131624285(0x7f0e015d, float:1.8875745E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x15c3:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageDocument"
            r7 = 2131625740(0x7f0e070c, float:1.8878696E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachDocument"
            r8 = 2131624269(0x7f0e014d, float:1.8875713E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x15e3:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageRound"
            r7 = 2131625771(0x7f0e072b, float:1.887876E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachRound"
            r8 = 2131624284(0x7f0e015c, float:1.8875743E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x1603:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "ActionTakeScreenshoot"
            r7 = 2131624083(0x7f0e0093, float:1.8875336E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r7)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "un1"
            r8 = 0
            r11 = r15[r8]     // Catch:{ all -> 0x044d }
            java.lang.String r1 = r1.replace(r7, r11)     // Catch:{ all -> 0x044d }
        L_0x161a:
            r7 = r1
        L_0x161b:
            r1 = 0
            goto L_0x16f4
        L_0x161e:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageSDVideo"
            r7 = 2131625773(0x7f0e072d, float:1.8878763E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachDestructingVideo"
            r8 = 2131624268(0x7f0e014c, float:1.887571E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x163e:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageVideo"
            r7 = 2131625779(0x7f0e0733, float:1.8878776E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachVideo"
            r8 = 2131624288(0x7f0e0160, float:1.8875751E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x165d:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageSDPhoto"
            r7 = 2131625772(0x7f0e072c, float:1.8878761E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachDestructingPhoto"
            r8 = 2131624267(0x7f0e014b, float:1.8875709E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x167c:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessagePhoto"
            r7 = 2131625769(0x7f0e0729, float:1.8878755E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "AttachPhoto"
            r8 = 2131624282(0x7f0e015a, float:1.887574E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x169b:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageNoText"
            r7 = 2131625768(0x7f0e0728, float:1.8878753E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r8 = 0
            r14 = r15[r8]     // Catch:{ all -> 0x044d }
            r11[r8] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "Message"
            r8 = 2131625532(0x7f0e063c, float:1.8878275E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x044d }
        L_0x16b9:
            r16 = r7
            r7 = r1
        L_0x16bc:
            r1 = 0
            goto L_0x16f6
        L_0x16be:
            r21 = r1
            r10 = r32
            java.lang.String r1 = "NotificationMessageText"
            r7 = 2131625778(0x7f0e0732, float:1.8878774E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x044d }
            r11 = 0
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            r11 = 1
            r14 = r15[r11]     // Catch:{ all -> 0x044d }
            r8[r11] = r14     // Catch:{ all -> 0x044d }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8)     // Catch:{ all -> 0x044d }
            r7 = r15[r11]     // Catch:{ all -> 0x044d }
            goto L_0x16b9
        L_0x16db:
            if (r1 == 0) goto L_0x16f2
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x044d }
            r1.<init>()     // Catch:{ all -> 0x044d }
            java.lang.String r7 = "unhandled loc_key = "
            r1.append(r7)     // Catch:{ all -> 0x044d }
            r1.append(r9)     // Catch:{ all -> 0x044d }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x044d }
            org.telegram.messenger.FileLog.w(r1)     // Catch:{ all -> 0x044d }
        L_0x16f2:
            r1 = 0
            r7 = 0
        L_0x16f4:
            r16 = 0
        L_0x16f6:
            if (r7 == 0) goto L_0x1795
            org.telegram.tgnet.TLRPC$TL_message r8 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x1798 }
            r8.<init>()     // Catch:{ all -> 0x1798 }
            r8.id = r10     // Catch:{ all -> 0x1798 }
            r8.random_id = r3     // Catch:{ all -> 0x1798 }
            if (r16 == 0) goto L_0x1706
            r3 = r16
            goto L_0x1707
        L_0x1706:
            r3 = r7
        L_0x1707:
            r8.message = r3     // Catch:{ all -> 0x1798 }
            r3 = 1000(0x3e8, double:4.94E-321)
            long r3 = r37 / r3
            int r4 = (int) r3     // Catch:{ all -> 0x1798 }
            r8.date = r4     // Catch:{ all -> 0x1798 }
            if (r5 == 0) goto L_0x1719
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r3 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x044d }
            r3.<init>()     // Catch:{ all -> 0x044d }
            r8.action = r3     // Catch:{ all -> 0x044d }
        L_0x1719:
            if (r6 == 0) goto L_0x1722
            int r3 = r8.flags     // Catch:{ all -> 0x044d }
            r4 = -2147483648(0xfffffffvar_, float:-0.0)
            r3 = r3 | r4
            r8.flags = r3     // Catch:{ all -> 0x044d }
        L_0x1722:
            r8.dialog_id = r12     // Catch:{ all -> 0x1798 }
            if (r2 == 0) goto L_0x1732
            org.telegram.tgnet.TLRPC$TL_peerChannel r3 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x044d }
            r3.<init>()     // Catch:{ all -> 0x044d }
            r8.to_id = r3     // Catch:{ all -> 0x044d }
            org.telegram.tgnet.TLRPC$Peer r3 = r8.to_id     // Catch:{ all -> 0x044d }
            r3.channel_id = r2     // Catch:{ all -> 0x044d }
            goto L_0x174f
        L_0x1732:
            if (r22 == 0) goto L_0x1742
            org.telegram.tgnet.TLRPC$TL_peerChat r2 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x044d }
            r2.<init>()     // Catch:{ all -> 0x044d }
            r8.to_id = r2     // Catch:{ all -> 0x044d }
            org.telegram.tgnet.TLRPC$Peer r2 = r8.to_id     // Catch:{ all -> 0x044d }
            r3 = r22
            r2.chat_id = r3     // Catch:{ all -> 0x044d }
            goto L_0x174f
        L_0x1742:
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1798 }
            r2.<init>()     // Catch:{ all -> 0x1798 }
            r8.to_id = r2     // Catch:{ all -> 0x1798 }
            org.telegram.tgnet.TLRPC$Peer r2 = r8.to_id     // Catch:{ all -> 0x1798 }
            r3 = r24
            r2.user_id = r3     // Catch:{ all -> 0x1798 }
        L_0x174f:
            int r2 = r8.flags     // Catch:{ all -> 0x1798 }
            r2 = r2 | 256(0x100, float:3.59E-43)
            r8.flags = r2     // Catch:{ all -> 0x1798 }
            r2 = r21
            r8.from_id = r2     // Catch:{ all -> 0x1798 }
            if (r20 != 0) goto L_0x1760
            if (r5 == 0) goto L_0x175e
            goto L_0x1760
        L_0x175e:
            r2 = 0
            goto L_0x1761
        L_0x1760:
            r2 = 1
        L_0x1761:
            r8.mentioned = r2     // Catch:{ all -> 0x1798 }
            r2 = r19
            r8.silent = r2     // Catch:{ all -> 0x1798 }
            r4 = r23
            r8.from_scheduled = r4     // Catch:{ all -> 0x1798 }
            org.telegram.messenger.MessageObject r2 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x1798 }
            r19 = r2
            r20 = r30
            r21 = r8
            r22 = r7
            r23 = r25
            r24 = r31
            r25 = r1
            r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27)     // Catch:{ all -> 0x1798 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x1798 }
            r1.<init>()     // Catch:{ all -> 0x1798 }
            r1.add(r2)     // Catch:{ all -> 0x1798 }
            org.telegram.messenger.NotificationsController r2 = org.telegram.messenger.NotificationsController.getInstance(r30)     // Catch:{ all -> 0x1798 }
            r3 = r35
            java.util.concurrent.CountDownLatch r4 = r3.countDownLatch     // Catch:{ all -> 0x17c0 }
            r5 = 1
            r2.processNewMessages(r1, r5, r5, r4)     // Catch:{ all -> 0x17c0 }
            r28 = 0
            goto L_0x17ad
        L_0x1795:
            r3 = r35
            goto L_0x17ab
        L_0x1798:
            r0 = move-exception
            r3 = r35
            goto L_0x17c1
        L_0x179c:
            r0 = move-exception
            r3 = r35
            goto L_0x17d5
        L_0x17a0:
            r3 = r35
            goto L_0x17a9
        L_0x17a3:
            r0 = move-exception
            r3 = r1
            goto L_0x17d5
        L_0x17a6:
            r3 = r1
            r29 = r14
        L_0x17a9:
            r30 = r15
        L_0x17ab:
            r28 = 1
        L_0x17ad:
            if (r28 == 0) goto L_0x17b4
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x17c0 }
            r1.countDown()     // Catch:{ all -> 0x17c0 }
        L_0x17b4:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r30)     // Catch:{ all -> 0x17c0 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r30)     // Catch:{ all -> 0x17c0 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x17c0 }
            goto L_0x18f4
        L_0x17c0:
            r0 = move-exception
        L_0x17c1:
            r1 = r0
            r14 = r29
            r15 = r30
            goto L_0x18a0
        L_0x17c8:
            r0 = move-exception
            r3 = r1
            r29 = r14
            r30 = r15
            r1 = r0
            goto L_0x18a0
        L_0x17d1:
            r0 = move-exception
            r3 = r1
            r29 = r7
        L_0x17d5:
            r30 = r15
            goto L_0x189d
        L_0x17d9:
            r3 = r1
            r29 = r7
            r30 = r15
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x17f8 }
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$aCTiHH_b4RhBMfcCkAHdgReKcg4 r2 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$aCTiHH_b4RhBMfcCkAHdgReKcg4     // Catch:{ all -> 0x17f3 }
            r15 = r30
            r2.<init>(r15)     // Catch:{ all -> 0x17f0 }
            r1.postRunnable(r2)     // Catch:{ all -> 0x1897 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1897 }
            r1.countDown()     // Catch:{ all -> 0x1897 }
            return
        L_0x17f0:
            r0 = move-exception
            goto L_0x189d
        L_0x17f3:
            r0 = move-exception
            r15 = r30
            goto L_0x189d
        L_0x17f8:
            r0 = move-exception
            r15 = r30
            goto L_0x189d
        L_0x17fd:
            r3 = r1
            r29 = r7
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ r1 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ     // Catch:{ all -> 0x180e }
            r1.<init>(r15)     // Catch:{ all -> 0x180e }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ all -> 0x1897 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1897 }
            r1.countDown()     // Catch:{ all -> 0x1897 }
            return
        L_0x180e:
            r0 = move-exception
            goto L_0x189d
        L_0x1811:
            r3 = r1
            r29 = r7
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x1897 }
            r1.<init>()     // Catch:{ all -> 0x1897 }
            r2 = 0
            r1.popup = r2     // Catch:{ all -> 0x1897 }
            r2 = 2
            r1.flags = r2     // Catch:{ all -> 0x1897 }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r6 = r37 / r6
            int r2 = (int) r6     // Catch:{ all -> 0x1897 }
            r1.inbox_date = r2     // Catch:{ all -> 0x1897 }
            java.lang.String r2 = "message"
            java.lang.String r2 = r5.getString(r2)     // Catch:{ all -> 0x1897 }
            r1.message = r2     // Catch:{ all -> 0x1897 }
            java.lang.String r2 = "announcement"
            r1.type = r2     // Catch:{ all -> 0x1897 }
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ all -> 0x1897 }
            r2.<init>()     // Catch:{ all -> 0x1897 }
            r1.media = r2     // Catch:{ all -> 0x1897 }
            org.telegram.tgnet.TLRPC$TL_updates r2 = new org.telegram.tgnet.TLRPC$TL_updates     // Catch:{ all -> 0x1897 }
            r2.<init>()     // Catch:{ all -> 0x1897 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r4 = r2.updates     // Catch:{ all -> 0x1897 }
            r4.add(r1)     // Catch:{ all -> 0x1897 }
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1897 }
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo r4 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo     // Catch:{ all -> 0x180e }
            r4.<init>(r15, r2)     // Catch:{ all -> 0x180e }
            r1.postRunnable(r4)     // Catch:{ all -> 0x1897 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x1897 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1897 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1897 }
            r1.countDown()     // Catch:{ all -> 0x1897 }
            return
        L_0x185a:
            r3 = r1
            r29 = r7
            java.lang.String r1 = "dc"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x1897 }
            java.lang.String r2 = "addr"
            java.lang.String r2 = r11.getString(r2)     // Catch:{ all -> 0x1897 }
            java.lang.String r4 = ":"
            java.lang.String[] r2 = r2.split(r4)     // Catch:{ all -> 0x1897 }
            int r4 = r2.length     // Catch:{ all -> 0x1897 }
            r5 = 2
            if (r4 == r5) goto L_0x1879
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1897 }
            r1.countDown()     // Catch:{ all -> 0x1897 }
            return
        L_0x1879:
            r4 = 0
            r4 = r2[r4]     // Catch:{ all -> 0x1897 }
            r5 = 1
            r2 = r2[r5]     // Catch:{ all -> 0x1897 }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ all -> 0x1897 }
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x1897 }
            r5.applyDatacenterAddress(r1, r4, r2)     // Catch:{ all -> 0x1897 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x1897 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1897 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1897 }
            r1.countDown()     // Catch:{ all -> 0x1897 }
            return
        L_0x1897:
            r0 = move-exception
            goto L_0x189d
        L_0x1899:
            r0 = move-exception
            r3 = r1
            r29 = r7
        L_0x189d:
            r1 = r0
            r14 = r29
        L_0x18a0:
            r2 = -1
            goto L_0x18bc
        L_0x18a2:
            r0 = move-exception
            r3 = r1
            r29 = r7
            r1 = r0
            r14 = r29
            r2 = -1
            goto L_0x18bb
        L_0x18ab:
            r0 = move-exception
            r3 = r1
            r29 = r7
            r1 = r0
            r14 = r29
            r2 = -1
            r9 = 0
            goto L_0x18bb
        L_0x18b5:
            r0 = move-exception
            r3 = r1
            r1 = r0
            r2 = -1
            r9 = 0
            r14 = 0
        L_0x18bb:
            r15 = -1
        L_0x18bc:
            if (r15 == r2) goto L_0x18ce
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15)
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)
            r2.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r2 = r3.countDownLatch
            r2.countDown()
            goto L_0x18d1
        L_0x18ce:
            r35.onDecryptError()
        L_0x18d1:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x18f1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "error in loc_key = "
            r2.append(r4)
            r2.append(r9)
            java.lang.String r4 = " json "
            r2.append(r4)
            r2.append(r14)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r2)
        L_0x18f1:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x18f4:
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
            private final /* synthetic */ String f$0;

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
            private final /* synthetic */ String f$0;

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
                        private final /* synthetic */ int f$0;
                        private final /* synthetic */ String f$1;

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
