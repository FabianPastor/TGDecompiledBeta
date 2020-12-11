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

    /* access modifiers changed from: private */
    /* renamed from: lambda$onMessageReceived$4 */
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v81, resolved type: java.lang.String} */
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v10, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v11, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v12, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v209, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v232, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v212, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v238, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v164, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v167, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v143, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v148, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v149, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v153, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v154, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v155, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v158, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v273, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v166, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v277, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v167, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v279, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v169, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v170, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v283, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v172, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v173, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v285, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v174, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v175, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v268, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v289, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v177, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v178, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v291, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v275, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v293, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v181, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v282, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v297, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v214, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v301, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v188, resolved type: java.lang.Object[]} */
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v315, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v277, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v232, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v279, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v281, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v322, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v285, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v287, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v327, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v291, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v244, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v293, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v332, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v297, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v299, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v252, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v337, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v303, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v305, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v256, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v257, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v342, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v258, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v309, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v260, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v311, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v262, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v347, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v263, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v315, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v264, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v317, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v266, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v322, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v323, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v324, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v327, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v6, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v79, resolved type: org.telegram.messenger.GcmPushListenerService} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:1002:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x01df, code lost:
        if (r2 == 0) goto L_0x1cac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x01e1, code lost:
        if (r2 == 1) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x01e3, code lost:
        if (r2 == 2) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x01e5, code lost:
        if (r2 == 3) goto L_0x1CLASSNAME;
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
        if (r3 == 0) goto L_0x1CLASSNAME;
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
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x0323, code lost:
        org.telegram.messenger.FileLog.d("GCM received " + r9 + " for dialogId = " + r3 + " mids = " + android.text.TextUtils.join(",", r7));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x0351, code lost:
        if (android.text.TextUtils.isEmpty(r9) != false) goto L_0x1CLASSNAME;
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
        if (r1 == false) goto L_0x1bfc;
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
        r25 = r1;
        r6 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x040a, code lost:
        r25 = r1;
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x0413, code lost:
        if (r11.has("mention") == false) goto L_0x042c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x041b, code lost:
        if (r11.getInt("mention") == 0) goto L_0x042c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x041d, code lost:
        r26 = true;
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
        r26 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:275:0x0434, code lost:
        if (r11.has("silent") == false) goto L_0x0441;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x043c, code lost:
        if (r11.getInt("silent") == 0) goto L_0x0441;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x043e, code lost:
        r27 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x0441, code lost:
        r27 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x0449, code lost:
        if (r5.has("loc_args") == false) goto L_0x0469;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:?, code lost:
        r1 = r5.getJSONArray("loc_args");
        r5 = r1.length();
        r30 = r12;
        r12 = new java.lang.String[r5];
        r31 = r15;
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
        r30 = r12;
        r31 = r15;
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
        r33 = r11;
        r15 = false;
        r11 = r5;
        r5 = r12[1];
        r32 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x04b4, code lost:
        if (r9.startsWith("PINNED_") == false) goto L_0x04c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x04b6, code lost:
        if (r2 == 0) goto L_0x04ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:311:0x04b8, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x04ba, code lost:
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x04bb, code lost:
        r33 = r11;
        r11 = null;
        r15 = false;
        r32 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x04c8, code lost:
        if (r9.startsWith("CHANNEL_") == false) goto L_0x04cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x04ca, code lost:
        r11 = null;
        r15 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x04cd, code lost:
        r11 = null;
        r15 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x04cf, code lost:
        r32 = false;
        r33 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x04d5, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x04fe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x04d7, code lost:
        r34 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:?, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x04fe, code lost:
        r35 = r1;
        r34 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x0506, code lost:
        switch(r9.hashCode()) {
            case -2100047043: goto L_0x0a23;
            case -2091498420: goto L_0x0a18;
            case -2053872415: goto L_0x0a0d;
            case -2039746363: goto L_0x0a02;
            case -2023218804: goto L_0x09f7;
            case -1979538588: goto L_0x09ec;
            case -1979536003: goto L_0x09e1;
            case -1979535888: goto L_0x09d6;
            case -1969004705: goto L_0x09cb;
            case -1946699248: goto L_0x09bf;
            case -1646640058: goto L_0x09b3;
            case -1528047021: goto L_0x09a7;
            case -1493579426: goto L_0x099b;
            case -1482481933: goto L_0x098f;
            case -1480102982: goto L_0x0984;
            case -1478041834: goto L_0x0978;
            case -1474543101: goto L_0x096d;
            case -1465695932: goto L_0x0961;
            case -1374906292: goto L_0x0955;
            case -1372940586: goto L_0x0949;
            case -1264245338: goto L_0x093d;
            case -1236154001: goto L_0x0931;
            case -1236086700: goto L_0x0925;
            case -1236077786: goto L_0x0919;
            case -1235796237: goto L_0x090d;
            case -1235760759: goto L_0x0901;
            case -1235686303: goto L_0x08f6;
            case -1198046100: goto L_0x08eb;
            case -1124254527: goto L_0x08df;
            case -1085137927: goto L_0x08d3;
            case -1084856378: goto L_0x08c7;
            case -1084820900: goto L_0x08bb;
            case -1084746444: goto L_0x08af;
            case -819729482: goto L_0x08a3;
            case -772141857: goto L_0x0897;
            case -638310039: goto L_0x088b;
            case -590403924: goto L_0x087f;
            case -589196239: goto L_0x0873;
            case -589193654: goto L_0x0867;
            case -589193539: goto L_0x085b;
            case -440169325: goto L_0x084f;
            case -412748110: goto L_0x0843;
            case -228518075: goto L_0x0837;
            case -213586509: goto L_0x082b;
            case -115582002: goto L_0x081f;
            case -112621464: goto L_0x0813;
            case -108522133: goto L_0x0807;
            case -107572034: goto L_0x07fc;
            case -40534265: goto L_0x07f0;
            case 65254746: goto L_0x07e4;
            case 141040782: goto L_0x07d8;
            case 202550149: goto L_0x07cc;
            case 309993049: goto L_0x07c0;
            case 309995634: goto L_0x07b4;
            case 309995749: goto L_0x07a8;
            case 320532812: goto L_0x079c;
            case 328933854: goto L_0x0790;
            case 331340546: goto L_0x0784;
            case 344816990: goto L_0x0778;
            case 346878138: goto L_0x076c;
            case 350376871: goto L_0x0760;
            case 608430149: goto L_0x0754;
            case 615714517: goto L_0x0749;
            case 715508879: goto L_0x073d;
            case 728985323: goto L_0x0731;
            case 731046471: goto L_0x0725;
            case 734545204: goto L_0x0719;
            case 802032552: goto L_0x070d;
            case 991498806: goto L_0x0701;
            case 1007364121: goto L_0x06f5;
            case 1019850010: goto L_0x06e9;
            case 1019917311: goto L_0x06dd;
            case 1019926225: goto L_0x06d1;
            case 1020207774: goto L_0x06c5;
            case 1020243252: goto L_0x06b9;
            case 1020317708: goto L_0x06ad;
            case 1060282259: goto L_0x06a1;
            case 1060349560: goto L_0x0695;
            case 1060358474: goto L_0x0689;
            case 1060640023: goto L_0x067d;
            case 1060675501: goto L_0x0671;
            case 1060749957: goto L_0x0666;
            case 1073049781: goto L_0x065a;
            case 1078101399: goto L_0x064e;
            case 1110103437: goto L_0x0642;
            case 1160762272: goto L_0x0636;
            case 1172918249: goto L_0x062a;
            case 1234591620: goto L_0x061e;
            case 1281128640: goto L_0x0612;
            case 1281131225: goto L_0x0606;
            case 1281131340: goto L_0x05fa;
            case 1310789062: goto L_0x05ef;
            case 1333118583: goto L_0x05e3;
            case 1361447897: goto L_0x05d7;
            case 1498266155: goto L_0x05cb;
            case 1533804208: goto L_0x05bf;
            case 1540131626: goto L_0x05b3;
            case 1547988151: goto L_0x05a7;
            case 1561464595: goto L_0x059b;
            case 1563525743: goto L_0x058f;
            case 1567024476: goto L_0x0583;
            case 1810705077: goto L_0x0577;
            case 1815177512: goto L_0x056b;
            case 1954774321: goto L_0x055f;
            case 1963241394: goto L_0x0553;
            case 2014789757: goto L_0x0547;
            case 2022049433: goto L_0x053b;
            case 2034984710: goto L_0x052f;
            case 2048733346: goto L_0x0523;
            case 2099392181: goto L_0x0517;
            case 2140162142: goto L_0x050b;
            default: goto L_0x0509;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x0511, code lost:
        if (r9.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x0513, code lost:
        r1 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x051d, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x051f, code lost:
        r1 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x0529, code lost:
        if (r9.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x052b, code lost:
        r1 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x0535, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PLAYLIST") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x0537, code lost:
        r1 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x0541, code lost:
        if (r9.equals("PINNED_CONTACT") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x0543, code lost:
        r1 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x054d, code lost:
        if (r9.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x054f, code lost:
        r1 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x0559, code lost:
        if (r9.equals("LOCKED_MESSAGE") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x055b, code lost:
        r1 = 'i';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x0565, code lost:
        if (r9.equals("CHAT_MESSAGE_PLAYLIST") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x0567, code lost:
        r1 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x0571, code lost:
        if (r9.equals("CHANNEL_MESSAGES") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x0573, code lost:
        r1 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x057d, code lost:
        if (r9.equals("MESSAGE_INVOICE") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x057f, code lost:
        r1 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x0589, code lost:
        if (r9.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x058b, code lost:
        r1 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x0595, code lost:
        if (r9.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x0597, code lost:
        r1 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x05a1, code lost:
        if (r9.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x05a3, code lost:
        r1 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x05ad, code lost:
        if (r9.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x05af, code lost:
        r1 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x05b9, code lost:
        if (r9.equals("MESSAGE_PLAYLIST") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x05bb, code lost:
        r1 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x05c5, code lost:
        if (r9.equals("MESSAGE_VIDEOS") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x05c7, code lost:
        r1 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x05d1, code lost:
        if (r9.equals("PHONE_CALL_MISSED") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x05d3, code lost:
        r1 = 'n';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x05dd, code lost:
        if (r9.equals("MESSAGE_PHOTOS") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x05df, code lost:
        r1 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x05e9, code lost:
        if (r9.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x05eb, code lost:
        r1 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x05f5, code lost:
        if (r9.equals("MESSAGE_NOTEXT") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x05f7, code lost:
        r1 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x0600, code lost:
        if (r9.equals("MESSAGE_GIF") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x0602, code lost:
        r1 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x060c, code lost:
        if (r9.equals("MESSAGE_GEO") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x060e, code lost:
        r1 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x0618, code lost:
        if (r9.equals("MESSAGE_DOC") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x061a, code lost:
        r1 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:400:0x0624, code lost:
        if (r9.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x0626, code lost:
        r1 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0630, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x0632, code lost:
        r1 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x063c, code lost:
        if (r9.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x063e, code lost:
        r1 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x0648, code lost:
        if (r9.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x064a, code lost:
        r1 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0654, code lost:
        if (r9.equals("CHAT_TITLE_EDITED") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x0656, code lost:
        r1 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x0660, code lost:
        if (r9.equals("PINNED_NOTEXT") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x0662, code lost:
        r1 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x066c, code lost:
        if (r9.equals("MESSAGE_TEXT") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x066e, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x0677, code lost:
        if (r9.equals("MESSAGE_QUIZ") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x0679, code lost:
        r1 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x0683, code lost:
        if (r9.equals("MESSAGE_POLL") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x0685, code lost:
        r1 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x068f, code lost:
        if (r9.equals("MESSAGE_GAME") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x0691, code lost:
        r1 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x069b, code lost:
        if (r9.equals("MESSAGE_FWDS") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x069d, code lost:
        r1 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x06a7, code lost:
        if (r9.equals("MESSAGE_DOCS") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x06a9, code lost:
        r1 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x06b3, code lost:
        if (r9.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x06b5, code lost:
        r1 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x06bf, code lost:
        if (r9.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x06c1, code lost:
        r1 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x06cb, code lost:
        if (r9.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x06cd, code lost:
        r1 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x06d7, code lost:
        if (r9.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x06d9, code lost:
        r1 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x06e3, code lost:
        if (r9.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x06e5, code lost:
        r1 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x06ef, code lost:
        if (r9.equals("CHAT_MESSAGE_DOCS") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x06f1, code lost:
        r1 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x06fb, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x06fd, code lost:
        r1 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x0707, code lost:
        if (r9.equals("PINNED_GEOLIVE") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:458:0x0709, code lost:
        r1 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:460:0x0713, code lost:
        if (r9.equals("MESSAGE_CONTACT") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x0715, code lost:
        r1 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x071f, code lost:
        if (r9.equals("PINNED_VIDEO") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x0721, code lost:
        r1 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x072b, code lost:
        if (r9.equals("PINNED_ROUND") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x072d, code lost:
        r1 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x0737, code lost:
        if (r9.equals("PINNED_PHOTO") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x0739, code lost:
        r1 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:472:0x0743, code lost:
        if (r9.equals("PINNED_AUDIO") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0745, code lost:
        r1 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x074f, code lost:
        if (r9.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x0751, code lost:
        r1 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x075a, code lost:
        if (r9.equals("CHAT_VOICECHAT_INVITE_YOU") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x075c, code lost:
        r1 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x0766, code lost:
        if (r9.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x0768, code lost:
        r1 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x0772, code lost:
        if (r9.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x0774, code lost:
        r1 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x077e, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x0780, code lost:
        r1 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x078a, code lost:
        if (r9.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x078c, code lost:
        r1 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x0796, code lost:
        if (r9.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x0798, code lost:
        r1 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x07a2, code lost:
        if (r9.equals("MESSAGES") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x07a4, code lost:
        r1 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x07ae, code lost:
        if (r9.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x07b0, code lost:
        r1 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x07ba, code lost:
        if (r9.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x07bc, code lost:
        r1 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x07c6, code lost:
        if (r9.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x07c8, code lost:
        r1 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x07d2, code lost:
        if (r9.equals("CHAT_VOICECHAT_INVITE") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x07d4, code lost:
        r1 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x07de, code lost:
        if (r9.equals("CHAT_LEFT") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x07e0, code lost:
        r1 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x07ea, code lost:
        if (r9.equals("CHAT_ADD_YOU") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x07ec, code lost:
        r1 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x07f6, code lost:
        if (r9.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x07f8, code lost:
        r1 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0802, code lost:
        if (r9.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x0804, code lost:
        r1 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x080d, code lost:
        if (r9.equals("AUTH_REGION") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x080f, code lost:
        r1 = 'h';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x0819, code lost:
        if (r9.equals("CONTACT_JOINED") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x081b, code lost:
        r1 = 'f';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0825, code lost:
        if (r9.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x0827, code lost:
        r1 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x0831, code lost:
        if (r9.equals("ENCRYPTION_REQUEST") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0833, code lost:
        r1 = 'j';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x083d, code lost:
        if (r9.equals("MESSAGE_GEOLIVE") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x083f, code lost:
        r1 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:538:0x0849, code lost:
        if (r9.equals("CHAT_DELETE_YOU") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x084b, code lost:
        r1 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0855, code lost:
        if (r9.equals("AUTH_UNKNOWN") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x0857, code lost:
        r1 = 'g';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x0861, code lost:
        if (r9.equals("PINNED_GIF") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0863, code lost:
        r1 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x086d, code lost:
        if (r9.equals("PINNED_GEO") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x086f, code lost:
        r1 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x0879, code lost:
        if (r9.equals("PINNED_DOC") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x087b, code lost:
        r1 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x0885, code lost:
        if (r9.equals("PINNED_GAME_SCORE") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x0887, code lost:
        r1 = 'b';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x0891, code lost:
        if (r9.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x0893, code lost:
        r1 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x089d, code lost:
        if (r9.equals("PHONE_CALL_REQUEST") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x089f, code lost:
        r1 = 'l';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x08a9, code lost:
        if (r9.equals("PINNED_STICKER") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x08ab, code lost:
        r1 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x08b5, code lost:
        if (r9.equals("PINNED_TEXT") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x08b7, code lost:
        r1 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x08c1, code lost:
        if (r9.equals("PINNED_QUIZ") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x08c3, code lost:
        r1 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x08cd, code lost:
        if (r9.equals("PINNED_POLL") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x08cf, code lost:
        r1 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x08d9, code lost:
        if (r9.equals("PINNED_GAME") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x08db, code lost:
        r1 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x08e5, code lost:
        if (r9.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x08e7, code lost:
        r1 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x08f1, code lost:
        if (r9.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x08f3, code lost:
        r1 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x08fc, code lost:
        if (r9.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x08fe, code lost:
        r1 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x0907, code lost:
        if (r9.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x0909, code lost:
        r1 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x0913, code lost:
        if (r9.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x0915, code lost:
        r1 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x091f, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0921, code lost:
        r1 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x092b, code lost:
        if (r9.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x092d, code lost:
        r1 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x0937, code lost:
        if (r9.equals("CHANNEL_MESSAGE_DOCS") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0939, code lost:
        r1 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0943, code lost:
        if (r9.equals("PINNED_INVOICE") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0945, code lost:
        r1 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x094f, code lost:
        if (r9.equals("CHAT_RETURNED") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0951, code lost:
        r1 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x095b, code lost:
        if (r9.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x095d, code lost:
        r1 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x0967, code lost:
        if (r9.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x0969, code lost:
        r1 = 'k';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x0973, code lost:
        if (r9.equals("MESSAGE_VIDEO") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x0975, code lost:
        r1 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x097e, code lost:
        if (r9.equals("MESSAGE_ROUND") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x0980, code lost:
        r1 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x098a, code lost:
        if (r9.equals("MESSAGE_PHOTO") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x098c, code lost:
        r1 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x0995, code lost:
        if (r9.equals("MESSAGE_MUTED") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x0997, code lost:
        r1 = 'm';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:625:0x09a1, code lost:
        if (r9.equals("MESSAGE_AUDIO") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x09a3, code lost:
        r1 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x09ad, code lost:
        if (r9.equals("CHAT_MESSAGES") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x09af, code lost:
        r1 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x09b9, code lost:
        if (r9.equals("CHAT_VOICECHAT_START") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x09bb, code lost:
        r1 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x09c5, code lost:
        if (r9.equals("CHAT_JOINED") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x09c7, code lost:
        r1 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x09d1, code lost:
        if (r9.equals("CHAT_ADD_MEMBER") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x09d3, code lost:
        r1 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x09dc, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x09de, code lost:
        r1 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x09e7, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x09e9, code lost:
        r1 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x09f2, code lost:
        if (r9.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x09f4, code lost:
        r1 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x09fd, code lost:
        if (r9.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x09ff, code lost:
        r1 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0a08, code lost:
        if (r9.equals("MESSAGE_STICKER") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0a0a, code lost:
        r1 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0a13, code lost:
        if (r9.equals("CHAT_CREATED") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0a15, code lost:
        r1 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0a1e, code lost:
        if (r9.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0a20, code lost:
        r1 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x0a29, code lost:
        if (r9.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0a2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0a2b, code lost:
        r1 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x0a2e, code lost:
        r1 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0a2f, code lost:
        r18 = r7;
        r36 = r15;
        r37 = r11;
        r38 = r2;
        r39 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x0a49, code lost:
        switch(r1) {
            case 0: goto L_0x1af0;
            case 1: goto L_0x1af0;
            case 2: goto L_0x1ad0;
            case 3: goto L_0x1ab3;
            case 4: goto L_0x1a96;
            case 5: goto L_0x1a79;
            case 6: goto L_0x1a5b;
            case 7: goto L_0x1a45;
            case 8: goto L_0x1a27;
            case 9: goto L_0x1a09;
            case 10: goto L_0x19ae;
            case 11: goto L_0x1990;
            case 12: goto L_0x196d;
            case 13: goto L_0x194a;
            case 14: goto L_0x1927;
            case 15: goto L_0x1909;
            case 16: goto L_0x18eb;
            case 17: goto L_0x18cd;
            case 18: goto L_0x18aa;
            case 19: goto L_0x188b;
            case 20: goto L_0x188b;
            case 21: goto L_0x1868;
            case 22: goto L_0x1842;
            case 23: goto L_0x181e;
            case 24: goto L_0x17fb;
            case 25: goto L_0x17d8;
            case 26: goto L_0x17b3;
            case 27: goto L_0x179d;
            case 28: goto L_0x177f;
            case 29: goto L_0x1761;
            case 30: goto L_0x1743;
            case 31: goto L_0x1725;
            case 32: goto L_0x1707;
            case 33: goto L_0x16ac;
            case 34: goto L_0x168e;
            case 35: goto L_0x166b;
            case 36: goto L_0x1648;
            case 37: goto L_0x1625;
            case 38: goto L_0x1607;
            case 39: goto L_0x15e9;
            case 40: goto L_0x15cb;
            case 41: goto L_0x15ad;
            case 42: goto L_0x1583;
            case 43: goto L_0x155f;
            case 44: goto L_0x153b;
            case 45: goto L_0x1517;
            case 46: goto L_0x14f1;
            case 47: goto L_0x14dc;
            case 48: goto L_0x14bb;
            case 49: goto L_0x1498;
            case 50: goto L_0x1475;
            case 51: goto L_0x1452;
            case 52: goto L_0x142f;
            case 53: goto L_0x140c;
            case 54: goto L_0x1393;
            case 55: goto L_0x1370;
            case 56: goto L_0x1348;
            case 57: goto L_0x1320;
            case 58: goto L_0x12f8;
            case 59: goto L_0x12d5;
            case 60: goto L_0x12b2;
            case 61: goto L_0x128f;
            case 62: goto L_0x1267;
            case 63: goto L_0x1243;
            case 64: goto L_0x121b;
            case 65: goto L_0x1201;
            case 66: goto L_0x1201;
            case 67: goto L_0x11e7;
            case 68: goto L_0x11cd;
            case 69: goto L_0x11ae;
            case 70: goto L_0x1194;
            case 71: goto L_0x1175;
            case 72: goto L_0x115b;
            case 73: goto L_0x1141;
            case 74: goto L_0x1127;
            case 75: goto L_0x110d;
            case 76: goto L_0x10f3;
            case 77: goto L_0x10d9;
            case 78: goto L_0x10ae;
            case 79: goto L_0x1085;
            case 80: goto L_0x105c;
            case 81: goto L_0x1033;
            case 82: goto L_0x1008;
            case 83: goto L_0x0fee;
            case 84: goto L_0x0var_;
            case 85: goto L_0x0f4e;
            case 86: goto L_0x0var_;
            case 87: goto L_0x0eb8;
            case 88: goto L_0x0e6d;
            case 89: goto L_0x0e22;
            case 90: goto L_0x0d6b;
            case 91: goto L_0x0d20;
            case 92: goto L_0x0ccb;
            case 93: goto L_0x0CLASSNAME;
            case 94: goto L_0x0CLASSNAME;
            case 95: goto L_0x0bdc;
            case 96: goto L_0x0b96;
            case 97: goto L_0x0b4d;
            case 98: goto L_0x0b04;
            case 99: goto L_0x0abb;
            case 100: goto L_0x0a72;
            case 101: goto L_0x0a56;
            case 102: goto L_0x0a52;
            case 103: goto L_0x0a52;
            case 104: goto L_0x0a52;
            case 105: goto L_0x0a52;
            case 106: goto L_0x0a52;
            case 107: goto L_0x0a52;
            case 108: goto L_0x0a52;
            case 109: goto L_0x0a52;
            case 110: goto L_0x0a52;
            default: goto L_0x0a4c;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0a4c, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0a52, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:?, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
        r34 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
        r5 = true;
        r16 = null;
        r2 = r1;
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0a74, code lost:
        if (r3 <= 0) goto L_0x0a8e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0a76, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0a8e, code lost:
        if (r6 == false) goto L_0x0aa8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x0a90, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0aa8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0abd, code lost:
        if (r3 <= 0) goto L_0x0ad7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0abf, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x0ad7, code lost:
        if (r6 == false) goto L_0x0af1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0ad9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0af1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0b06, code lost:
        if (r3 <= 0) goto L_0x0b20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x0b08, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x0b20, code lost:
        if (r6 == false) goto L_0x0b3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0b22, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0b3a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0b4f, code lost:
        if (r3 <= 0) goto L_0x0b69;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0b51, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0b69, code lost:
        if (r6 == false) goto L_0x0b83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0b6b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0b83, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0b98, code lost:
        if (r3 <= 0) goto L_0x0bb1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0b9a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0bb1, code lost:
        if (r6 == false) goto L_0x0bca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0bb3, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0bca, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0bde, code lost:
        if (r3 <= 0) goto L_0x0bf7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0be0, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0bf7, code lost:
        if (r6 == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0bf9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0CLASSNAME, code lost:
        r2 = r1;
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0CLASSNAME, code lost:
        if (r3 <= 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0c2a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0CLASSNAME, code lost:
        if (r6 == false) goto L_0x0c5f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0c5f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0CLASSNAME, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0c7a, code lost:
        if (r3 <= 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0c7c, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0CLASSNAME, code lost:
        if (r6 == false) goto L_0x0cb3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0CLASSNAME, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:0x0cb3, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0ccb, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0ccf, code lost:
        if (r3 <= 0) goto L_0x0ce9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0cd1, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0ce9, code lost:
        if (r6 == false) goto L_0x0d08;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0ceb, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:727:0x0d08, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0d20, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0d24, code lost:
        if (r3 <= 0) goto L_0x0d3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0d26, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x0d3e, code lost:
        if (r6 == false) goto L_0x0d58;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0d40, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0d58, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0d6b, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x0d6f, code lost:
        if (r3 <= 0) goto L_0x0da8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0d73, code lost:
        if (r12.length <= 1) goto L_0x0d95;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x0d7b, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x0d95;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0d7d, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x0d95, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0da8, code lost:
        if (r6 == false) goto L_0x0deb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x0dac, code lost:
        if (r12.length <= 2) goto L_0x0dd3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x0db4, code lost:
        if (android.text.TextUtils.isEmpty(r12[2]) != false) goto L_0x0dd3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x0db6, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x0dd3, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x0ded, code lost:
        if (r12.length <= 1) goto L_0x0e0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x0df5, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x0e0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:0x0df7, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x0e0f, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x0e22, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x0e26, code lost:
        if (r3 <= 0) goto L_0x0e40;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x0e28, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x0e40, code lost:
        if (r6 == false) goto L_0x0e5a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x0e42, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x0e5a, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x0e6d, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x0e71, code lost:
        if (r3 <= 0) goto L_0x0e8b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x0e73, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x0e8b, code lost:
        if (r6 == false) goto L_0x0ea5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:0x0e8d, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x0ea5, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x0eb8, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x0ebc, code lost:
        if (r3 <= 0) goto L_0x0ed6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x0ebe, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x0ed6, code lost:
        if (r6 == false) goto L_0x0ef0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x0ed8, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x0ef0, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x0var_, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x0var_, code lost:
        if (r3 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x0var_, code lost:
        if (r6 == false) goto L_0x0f3b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x0f3b, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x0f4e, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x0var_, code lost:
        if (r3 <= 0) goto L_0x0f6c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x0f6c, code lost:
        if (r6 == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x0f6e, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x0var_, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x0f9d, code lost:
        if (r3 <= 0) goto L_0x0fb7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x0f9f, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x0fb7, code lost:
        if (r6 == false) goto L_0x0fd6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x0fb9, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x0fd6, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x0fee, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x1008, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x1033, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x105c, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x1085, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x10ae, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x10d9, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x10f3, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x110d, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x1127, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x1141, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x115b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x1175, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x1194, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x11ae, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x11cd, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x11e7, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x1201, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x121b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x1243, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r12[0], r12[1], r12[2], r12[3]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x1267, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x128f, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x12b2, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x12d5, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x12f8, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x1320, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("PollQuiz", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x1348, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x1370, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x1393, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x1397, code lost:
        if (r12.length <= 2) goto L_0x13d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x139f, code lost:
        if (android.text.TextUtils.isEmpty(r12[2]) != false) goto L_0x13d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x13a1, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r12[0], r12[1], r12[2]);
        r5 = r12[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x13d9, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r12[0], r12[1]);
        r5 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x140c, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x142f, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x1452, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x1475, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x1498, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x14bb, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r12[0], r12[1], r12[2]);
        r5 = r12[2];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x14dc, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x14f1, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x1517, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x153b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x155f, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x1583, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x15ad, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x15cb, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x15e9, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x1607, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x1625, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x1648, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x166b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x168e, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x16ac, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x16b0, code lost:
        if (r12.length <= 1) goto L_0x16ed;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x16b8, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x16ed;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x16ba, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r12[0], r12[1]);
        r5 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x16ed, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:851:0x1707, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x1725, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x1743, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x1761, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x177f, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:856:0x179d, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x17b0, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x17b3, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x17d8, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x17fb, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x181e, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:862:0x1842, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:863:0x1868, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:864:0x188b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:865:0x18aa, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:866:0x18cd, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x18eb, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x1909, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x1927, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x194a, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:871:0x196d, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x1990, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x19ae, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x19b2, code lost:
        if (r12.length <= 1) goto L_0x19ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x19ba, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x19ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x19bc, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r12[0], r12[1]);
        r5 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x19ef, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x1a09, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x1a27, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x1a45, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x1a5b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x1a79, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x1a96, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:0x1ab3, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x1ad0, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x1aec, code lost:
        r16 = r5;
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x1af0, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r12[0], r12[1]);
        r5 = r12[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x1b0b, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1b21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x1b0d, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x1b21, code lost:
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x1b22, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x1b23, code lost:
        r16 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x1b25, code lost:
        if (r2 == null) goto L_0x1bf1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:?, code lost:
        r6 = new org.telegram.tgnet.TLRPC$TL_message();
        r6.id = r1;
        r6.random_id = r39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:897:0x1b32, code lost:
        if (r16 == null) goto L_0x1b37;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x1b34, code lost:
        r1 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:899:0x1b37, code lost:
        r1 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:0x1b38, code lost:
        r6.message = r1;
        r6.date = (int) (r45 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x1b41, code lost:
        if (r32 == false) goto L_0x1b4a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:?, code lost:
        r6.action = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:0x1b4a, code lost:
        if (r33 == false) goto L_0x1b53;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x1b4c, code lost:
        r6.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:?, code lost:
        r6.dialog_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x1b55, code lost:
        if (r38 == 0) goto L_0x1b65;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:910:?, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r6.peer_id = r1;
        r1.channel_id = r38;
        r3 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:0x1b65, code lost:
        if (r24 == 0) goto L_0x1b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:0x1b67, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r6.peer_id = r1;
        r3 = r24;
        r1.chat_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:913:0x1b73, code lost:
        r3 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:?, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r6.peer_id = r1;
        r1.user_id = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x1b80, code lost:
        r6.flags |= 256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:917:0x1b86, code lost:
        if (r31 == 0) goto L_0x1b92;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:?, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r6.from_id = r1;
        r1.chat_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:920:0x1b92, code lost:
        if (r30 == 0) goto L_0x1ba0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x1b94, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r6.from_id = r1;
        r1.channel_id = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x1ba0, code lost:
        if (r25 == 0) goto L_0x1bae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x1ba2, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r6.from_id = r1;
        r1.user_id = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:?, code lost:
        r6.from_id = r6.peer_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x1bb2, code lost:
        if (r26 != false) goto L_0x1bb9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x1bb4, code lost:
        if (r32 == false) goto L_0x1bb7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:0x1bb7, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x1bb9, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x1bba, code lost:
        r6.mentioned = r1;
        r6.silent = r27;
        r6.from_scheduled = r22;
        r19 = new org.telegram.messenger.MessageObject(r29, r6, r2, r34, r37, r5, r36, r35);
        r2 = new java.util.ArrayList();
        r2.add(r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:932:0x1be7, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x1be9, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:?, code lost:
        org.telegram.messenger.NotificationsController.getInstance(r29).processNewMessages(r2, true, true, r3.countDownLatch);
        r8 = false;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:935:0x1bf1, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:0x1bf4, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:0x1bf5, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:0x1bf8, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x1bf9, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:0x1bfc, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:0x1bff, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:0x1CLASSNAME, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:943:0x1CLASSNAME, code lost:
        r3 = r1;
        r28 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:944:0x1CLASSNAME, code lost:
        r29 = r15;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:0x1CLASSNAME, code lost:
        r8 = true;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:946:0x1CLASSNAME, code lost:
        if (r8 == false) goto L_0x1c0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x1c0a, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x1c0f, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r29);
        org.telegram.tgnet.ConnectionsManager.getInstance(r29).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:0x1c1b, code lost:
        r0 = th;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x1CLASSNAME, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x1CLASSNAME, code lost:
        r3 = r1;
        r28 = r14;
        r29 = r15;
        r1 = r0;
        r4 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x1c2e, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:0x1c2f, code lost:
        r3 = r1;
        r28 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x1CLASSNAME, code lost:
        r29 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:0x1CLASSNAME, code lost:
        r3 = r1;
        r28 = r7;
        r29 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x1c3f, code lost:
        r15 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.$$Lambda$GcmPushListenerService$bTzLYDDbdXvsKFD45HxhDHO38c(r15));
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:962:0x1c4c, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:0x1c4d, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:964:0x1c4e, code lost:
        r15 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:965:0x1CLASSNAME, code lost:
        r28 = r7;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.$$Lambda$GcmPushListenerService$RGrsR9FuVutCk9KN9DfF2lxxXic(r15));
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:966:0x1CLASSNAME, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:967:0x1CLASSNAME, code lost:
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
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.$$Lambda$GcmPushListenerService$SOipFt3ZN2Z1O4ALoGyMsSsmBw(r15, r2));
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:968:0x1cab, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:969:0x1cac, code lost:
        r3 = r1;
        r28 = r7;
        r1 = r11.getInt("dc");
        r2 = r11.getString("addr").split(":");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:0x1cc3, code lost:
        if (r2.length == 2) goto L_0x1ccb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:971:0x1cc5, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:972:0x1cca, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:0x1ccb, code lost:
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).applyDatacenterAddress(r1, r2[0], java.lang.Integer.parseInt(r2[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:974:0x1ce8, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:975:0x1ce9, code lost:
        r0 = th;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:988:0x1d12  */
    /* JADX WARNING: Removed duplicated region for block: B:989:0x1d22  */
    /* JADX WARNING: Removed duplicated region for block: B:992:0x1d29  */
    /* renamed from: lambda$null$3 */
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
            java.lang.Object r5 = r2.get(r5)     // Catch:{ all -> 0x1d09 }
            boolean r6 = r5 instanceof java.lang.String     // Catch:{ all -> 0x1d09 }
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
            goto L_0x1d10
        L_0x002d:
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ all -> 0x1d09 }
            r6 = 8
            byte[] r5 = android.util.Base64.decode(r5, r6)     // Catch:{ all -> 0x1d09 }
            org.telegram.tgnet.NativeByteBuffer r7 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x1d09 }
            int r8 = r5.length     // Catch:{ all -> 0x1d09 }
            r7.<init>((int) r8)     // Catch:{ all -> 0x1d09 }
            r7.writeBytes((byte[]) r5)     // Catch:{ all -> 0x1d09 }
            r8 = 0
            r7.position(r8)     // Catch:{ all -> 0x1d09 }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1d09 }
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
            byte[] r9 = new byte[r6]     // Catch:{ all -> 0x1d09 }
            r10 = 1
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1d09 }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1d09 }
            boolean r11 = java.util.Arrays.equals(r11, r9)     // Catch:{ all -> 0x1d09 }
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
            byte[] r9 = new byte[r9]     // Catch:{ all -> 0x1d09 }
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1d09 }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1d09 }
            org.telegram.messenger.MessageKeyData r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13)     // Catch:{ all -> 0x1d09 }
            java.nio.ByteBuffer r14 = r7.buffer     // Catch:{ all -> 0x1d09 }
            byte[] r15 = r11.aesKey     // Catch:{ all -> 0x1d09 }
            byte[] r11 = r11.aesIv     // Catch:{ all -> 0x1d09 }
            r17 = 0
            r18 = 0
            r19 = 24
            int r5 = r5.length     // Catch:{ all -> 0x1d09 }
            int r20 = r5 + -24
            r16 = r11
            org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x1d09 }
            byte[] r21 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1d09 }
            r22 = 96
            r23 = 32
            java.nio.ByteBuffer r5 = r7.buffer     // Catch:{ all -> 0x1d09 }
            r25 = 24
            int r26 = r5.limit()     // Catch:{ all -> 0x1d09 }
            r24 = r5
            byte[] r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26)     // Catch:{ all -> 0x1d09 }
            boolean r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6)     // Catch:{ all -> 0x1d09 }
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
            int r5 = r7.readInt32(r10)     // Catch:{ all -> 0x1d09 }
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x1d09 }
            r7.readBytes(r5, r10)     // Catch:{ all -> 0x1d09 }
            java.lang.String r7 = new java.lang.String     // Catch:{ all -> 0x1d09 }
            r7.<init>(r5)     // Catch:{ all -> 0x1d09 }
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ all -> 0x1cff }
            r5.<init>(r7)     // Catch:{ all -> 0x1cff }
            java.lang.String r9 = "loc_key"
            boolean r9 = r5.has(r9)     // Catch:{ all -> 0x1cff }
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
            java.lang.Object r11 = r5.get(r11)     // Catch:{ all -> 0x1cf5 }
            boolean r11 = r11 instanceof org.json.JSONObject     // Catch:{ all -> 0x1cf5 }
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
            org.json.JSONObject r11 = new org.json.JSONObject     // Catch:{ all -> 0x1cf5 }
            r11.<init>()     // Catch:{ all -> 0x1cf5 }
        L_0x0130:
            java.lang.String r14 = "user_id"
            boolean r14 = r5.has(r14)     // Catch:{ all -> 0x1cf5 }
            if (r14 == 0) goto L_0x0141
            java.lang.String r14 = "user_id"
            java.lang.Object r14 = r5.get(r14)     // Catch:{ all -> 0x0124 }
            goto L_0x0142
        L_0x0141:
            r14 = 0
        L_0x0142:
            if (r14 != 0) goto L_0x014f
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x0124 }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x0124 }
            int r14 = r14.getClientUserId()     // Catch:{ all -> 0x0124 }
            goto L_0x0173
        L_0x014f:
            boolean r15 = r14 instanceof java.lang.Integer     // Catch:{ all -> 0x1cf5 }
            if (r15 == 0) goto L_0x015a
            java.lang.Integer r14 = (java.lang.Integer) r14     // Catch:{ all -> 0x0124 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0124 }
            goto L_0x0173
        L_0x015a:
            boolean r15 = r14 instanceof java.lang.String     // Catch:{ all -> 0x1cf5 }
            if (r15 == 0) goto L_0x0169
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ all -> 0x0124 }
            java.lang.Integer r14 = org.telegram.messenger.Utilities.parseInt(r14)     // Catch:{ all -> 0x0124 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0124 }
            goto L_0x0173
        L_0x0169:
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1cf5 }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x1cf5 }
            int r14 = r14.getClientUserId()     // Catch:{ all -> 0x1cf5 }
        L_0x0173:
            int r15 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1cf5 }
            r4 = 0
        L_0x0176:
            if (r4 >= r12) goto L_0x0189
            org.telegram.messenger.UserConfig r17 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x0124 }
            int r6 = r17.getClientUserId()     // Catch:{ all -> 0x0124 }
            if (r6 != r14) goto L_0x0184
            r15 = r4
            goto L_0x0189
        L_0x0184:
            int r4 = r4 + 1
            r6 = 8
            goto L_0x0176
        L_0x0189:
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r15)     // Catch:{ all -> 0x1ceb }
            boolean r4 = r4.isClientActivated()     // Catch:{ all -> 0x1ceb }
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
            r2.get(r4)     // Catch:{ all -> 0x1ceb }
            int r2 = r9.hashCode()     // Catch:{ all -> 0x1ceb }
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
            if (r2 == 0) goto L_0x1cac
            if (r2 == r10) goto L_0x1CLASSNAME
            if (r2 == r13) goto L_0x1CLASSNAME
            if (r2 == r12) goto L_0x1CLASSNAME
            java.lang.String r2 = "channel_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1c2e }
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
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1c2e }
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
            boolean r7 = r11.has(r7)     // Catch:{ all -> 0x1CLASSNAME }
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
            boolean r13 = r11.has(r13)     // Catch:{ all -> 0x1CLASSNAME }
            if (r13 == 0) goto L_0x0244
            java.lang.String r3 = "encryption_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x022f }
            long r3 = (long) r3
            r13 = 32
            long r3 = r3 << r13
        L_0x0244:
            java.lang.String r13 = "schedule"
            boolean r13 = r11.has(r13)     // Catch:{ all -> 0x1CLASSNAME }
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
            if (r7 == 0) goto L_0x1CLASSNAME
            java.lang.String r7 = "READ_HISTORY"
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
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
            goto L_0x1CLASSNAME
        L_0x02e0:
            java.lang.String r7 = "MESSAGE_DELETED"
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
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
            if (r2 == 0) goto L_0x1CLASSNAME
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
            goto L_0x1CLASSNAME
        L_0x034d:
            boolean r7 = android.text.TextUtils.isEmpty(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r7 != 0) goto L_0x1CLASSNAME
            java.lang.String r7 = "msg_id"
            boolean r7 = r11.has(r7)     // Catch:{ all -> 0x1CLASSNAME }
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
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1bff }
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
            goto L_0x1d10
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
            if (r1 == 0) goto L_0x1bfc
            java.lang.String r1 = "chat_from_id"
            r6 = 0
            int r1 = r11.optInt(r1, r6)     // Catch:{ all -> 0x1bf8 }
            java.lang.String r12 = "chat_from_broadcast_id"
            int r12 = r11.optInt(r12, r6)     // Catch:{ all -> 0x1bf8 }
            r29 = r15
            java.lang.String r15 = "chat_from_group_id"
            int r15 = r11.optInt(r15, r6)     // Catch:{ all -> 0x1bf4 }
            if (r1 != 0) goto L_0x040a
            if (r15 == 0) goto L_0x0406
            goto L_0x040a
        L_0x0406:
            r25 = r1
            r6 = 0
            goto L_0x040d
        L_0x040a:
            r25 = r1
            r6 = 1
        L_0x040d:
            java.lang.String r1 = "mention"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x1bf4 }
            if (r1 == 0) goto L_0x042c
            java.lang.String r1 = "mention"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x042c
            r26 = 1
            goto L_0x042e
        L_0x0420:
            r0 = move-exception
            r2 = -1
            r3 = r43
            r1 = r0
            r4 = r9
            r14 = r28
            r15 = r29
            goto L_0x1d10
        L_0x042c:
            r26 = 0
        L_0x042e:
            java.lang.String r1 = "silent"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x1bf4 }
            if (r1 == 0) goto L_0x0441
            java.lang.String r1 = "silent"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0441
            r27 = 1
            goto L_0x0443
        L_0x0441:
            r27 = 0
        L_0x0443:
            java.lang.String r1 = "loc_args"
            boolean r1 = r5.has(r1)     // Catch:{ all -> 0x1bf4 }
            if (r1 == 0) goto L_0x0469
            java.lang.String r1 = "loc_args"
            org.json.JSONArray r1 = r5.getJSONArray(r1)     // Catch:{ all -> 0x0420 }
            int r5 = r1.length()     // Catch:{ all -> 0x0420 }
            r30 = r12
            java.lang.String[] r12 = new java.lang.String[r5]     // Catch:{ all -> 0x0420 }
            r31 = r15
            r15 = 0
        L_0x045c:
            if (r15 >= r5) goto L_0x0467
            java.lang.String r32 = r1.getString(r15)     // Catch:{ all -> 0x0420 }
            r12[r15] = r32     // Catch:{ all -> 0x0420 }
            int r15 = r15 + 1
            goto L_0x045c
        L_0x0467:
            r1 = 0
            goto L_0x046f
        L_0x0469:
            r30 = r12
            r31 = r15
            r1 = 0
            r12 = 0
        L_0x046f:
            r5 = r12[r1]     // Catch:{ all -> 0x1bf4 }
            java.lang.String r1 = "edit_date"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x1bf4 }
            java.lang.String r11 = "CHAT_"
            boolean r11 = r9.startsWith(r11)     // Catch:{ all -> 0x1bf4 }
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
            goto L_0x04cd
        L_0x049d:
            if (r2 == 0) goto L_0x04a1
            r11 = 1
            goto L_0x04a2
        L_0x04a1:
            r11 = 0
        L_0x04a2:
            r15 = 1
            r32 = r12[r15]     // Catch:{ all -> 0x0420 }
            r33 = r11
            r15 = 0
            r11 = r5
            r5 = r32
            r32 = 0
            goto L_0x04d3
        L_0x04ae:
            java.lang.String r11 = "PINNED_"
            boolean r11 = r9.startsWith(r11)     // Catch:{ all -> 0x1bf4 }
            if (r11 == 0) goto L_0x04c2
            if (r2 == 0) goto L_0x04ba
            r11 = 1
            goto L_0x04bb
        L_0x04ba:
            r11 = 0
        L_0x04bb:
            r33 = r11
            r11 = 0
            r15 = 0
            r32 = 1
            goto L_0x04d3
        L_0x04c2:
            java.lang.String r11 = "CHANNEL_"
            boolean r11 = r9.startsWith(r11)     // Catch:{ all -> 0x1bf4 }
            if (r11 == 0) goto L_0x04cd
            r11 = 0
            r15 = 1
            goto L_0x04cf
        L_0x04cd:
            r11 = 0
            r15 = 0
        L_0x04cf:
            r32 = 0
            r33 = 0
        L_0x04d3:
            boolean r34 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1bf4 }
            if (r34 == 0) goto L_0x04fe
            r34 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0420 }
            r5.<init>()     // Catch:{ all -> 0x0420 }
            r35 = r1
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
            goto L_0x0502
        L_0x04fe:
            r35 = r1
            r34 = r5
        L_0x0502:
            int r1 = r9.hashCode()     // Catch:{ all -> 0x1bf4 }
            switch(r1) {
                case -2100047043: goto L_0x0a23;
                case -2091498420: goto L_0x0a18;
                case -2053872415: goto L_0x0a0d;
                case -2039746363: goto L_0x0a02;
                case -2023218804: goto L_0x09f7;
                case -1979538588: goto L_0x09ec;
                case -1979536003: goto L_0x09e1;
                case -1979535888: goto L_0x09d6;
                case -1969004705: goto L_0x09cb;
                case -1946699248: goto L_0x09bf;
                case -1646640058: goto L_0x09b3;
                case -1528047021: goto L_0x09a7;
                case -1493579426: goto L_0x099b;
                case -1482481933: goto L_0x098f;
                case -1480102982: goto L_0x0984;
                case -1478041834: goto L_0x0978;
                case -1474543101: goto L_0x096d;
                case -1465695932: goto L_0x0961;
                case -1374906292: goto L_0x0955;
                case -1372940586: goto L_0x0949;
                case -1264245338: goto L_0x093d;
                case -1236154001: goto L_0x0931;
                case -1236086700: goto L_0x0925;
                case -1236077786: goto L_0x0919;
                case -1235796237: goto L_0x090d;
                case -1235760759: goto L_0x0901;
                case -1235686303: goto L_0x08f6;
                case -1198046100: goto L_0x08eb;
                case -1124254527: goto L_0x08df;
                case -1085137927: goto L_0x08d3;
                case -1084856378: goto L_0x08c7;
                case -1084820900: goto L_0x08bb;
                case -1084746444: goto L_0x08af;
                case -819729482: goto L_0x08a3;
                case -772141857: goto L_0x0897;
                case -638310039: goto L_0x088b;
                case -590403924: goto L_0x087f;
                case -589196239: goto L_0x0873;
                case -589193654: goto L_0x0867;
                case -589193539: goto L_0x085b;
                case -440169325: goto L_0x084f;
                case -412748110: goto L_0x0843;
                case -228518075: goto L_0x0837;
                case -213586509: goto L_0x082b;
                case -115582002: goto L_0x081f;
                case -112621464: goto L_0x0813;
                case -108522133: goto L_0x0807;
                case -107572034: goto L_0x07fc;
                case -40534265: goto L_0x07f0;
                case 65254746: goto L_0x07e4;
                case 141040782: goto L_0x07d8;
                case 202550149: goto L_0x07cc;
                case 309993049: goto L_0x07c0;
                case 309995634: goto L_0x07b4;
                case 309995749: goto L_0x07a8;
                case 320532812: goto L_0x079c;
                case 328933854: goto L_0x0790;
                case 331340546: goto L_0x0784;
                case 344816990: goto L_0x0778;
                case 346878138: goto L_0x076c;
                case 350376871: goto L_0x0760;
                case 608430149: goto L_0x0754;
                case 615714517: goto L_0x0749;
                case 715508879: goto L_0x073d;
                case 728985323: goto L_0x0731;
                case 731046471: goto L_0x0725;
                case 734545204: goto L_0x0719;
                case 802032552: goto L_0x070d;
                case 991498806: goto L_0x0701;
                case 1007364121: goto L_0x06f5;
                case 1019850010: goto L_0x06e9;
                case 1019917311: goto L_0x06dd;
                case 1019926225: goto L_0x06d1;
                case 1020207774: goto L_0x06c5;
                case 1020243252: goto L_0x06b9;
                case 1020317708: goto L_0x06ad;
                case 1060282259: goto L_0x06a1;
                case 1060349560: goto L_0x0695;
                case 1060358474: goto L_0x0689;
                case 1060640023: goto L_0x067d;
                case 1060675501: goto L_0x0671;
                case 1060749957: goto L_0x0666;
                case 1073049781: goto L_0x065a;
                case 1078101399: goto L_0x064e;
                case 1110103437: goto L_0x0642;
                case 1160762272: goto L_0x0636;
                case 1172918249: goto L_0x062a;
                case 1234591620: goto L_0x061e;
                case 1281128640: goto L_0x0612;
                case 1281131225: goto L_0x0606;
                case 1281131340: goto L_0x05fa;
                case 1310789062: goto L_0x05ef;
                case 1333118583: goto L_0x05e3;
                case 1361447897: goto L_0x05d7;
                case 1498266155: goto L_0x05cb;
                case 1533804208: goto L_0x05bf;
                case 1540131626: goto L_0x05b3;
                case 1547988151: goto L_0x05a7;
                case 1561464595: goto L_0x059b;
                case 1563525743: goto L_0x058f;
                case 1567024476: goto L_0x0583;
                case 1810705077: goto L_0x0577;
                case 1815177512: goto L_0x056b;
                case 1954774321: goto L_0x055f;
                case 1963241394: goto L_0x0553;
                case 2014789757: goto L_0x0547;
                case 2022049433: goto L_0x053b;
                case 2034984710: goto L_0x052f;
                case 2048733346: goto L_0x0523;
                case 2099392181: goto L_0x0517;
                case 2140162142: goto L_0x050b;
                default: goto L_0x0509;
            }
        L_0x0509:
            goto L_0x0a2e
        L_0x050b:
            java.lang.String r1 = "CHAT_MESSAGE_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 60
            goto L_0x0a2f
        L_0x0517:
            java.lang.String r1 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 43
            goto L_0x0a2f
        L_0x0523:
            java.lang.String r1 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 28
            goto L_0x0a2f
        L_0x052f:
            java.lang.String r1 = "CHANNEL_MESSAGE_PLAYLIST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 45
            goto L_0x0a2f
        L_0x053b:
            java.lang.String r1 = "PINNED_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 92
            goto L_0x0a2f
        L_0x0547:
            java.lang.String r1 = "CHAT_PHOTO_EDITED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 68
            goto L_0x0a2f
        L_0x0553:
            java.lang.String r1 = "LOCKED_MESSAGE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 105(0x69, float:1.47E-43)
            goto L_0x0a2f
        L_0x055f:
            java.lang.String r1 = "CHAT_MESSAGE_PLAYLIST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 81
            goto L_0x0a2f
        L_0x056b:
            java.lang.String r1 = "CHANNEL_MESSAGES"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 47
            goto L_0x0a2f
        L_0x0577:
            java.lang.String r1 = "MESSAGE_INVOICE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 21
            goto L_0x0a2f
        L_0x0583:
            java.lang.String r1 = "CHAT_MESSAGE_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 51
            goto L_0x0a2f
        L_0x058f:
            java.lang.String r1 = "CHAT_MESSAGE_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 52
            goto L_0x0a2f
        L_0x059b:
            java.lang.String r1 = "CHAT_MESSAGE_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 50
            goto L_0x0a2f
        L_0x05a7:
            java.lang.String r1 = "CHAT_MESSAGE_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 55
            goto L_0x0a2f
        L_0x05b3:
            java.lang.String r1 = "MESSAGE_PLAYLIST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 25
            goto L_0x0a2f
        L_0x05bf:
            java.lang.String r1 = "MESSAGE_VIDEOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 24
            goto L_0x0a2f
        L_0x05cb:
            java.lang.String r1 = "PHONE_CALL_MISSED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 110(0x6e, float:1.54E-43)
            goto L_0x0a2f
        L_0x05d7:
            java.lang.String r1 = "MESSAGE_PHOTOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 23
            goto L_0x0a2f
        L_0x05e3:
            java.lang.String r1 = "CHAT_MESSAGE_VIDEOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 80
            goto L_0x0a2f
        L_0x05ef:
            java.lang.String r1 = "MESSAGE_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 2
            goto L_0x0a2f
        L_0x05fa:
            java.lang.String r1 = "MESSAGE_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 17
            goto L_0x0a2f
        L_0x0606:
            java.lang.String r1 = "MESSAGE_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 15
            goto L_0x0a2f
        L_0x0612:
            java.lang.String r1 = "MESSAGE_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 9
            goto L_0x0a2f
        L_0x061e:
            java.lang.String r1 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 63
            goto L_0x0a2f
        L_0x062a:
            java.lang.String r1 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 39
            goto L_0x0a2f
        L_0x0636:
            java.lang.String r1 = "CHAT_MESSAGE_PHOTOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 79
            goto L_0x0a2f
        L_0x0642:
            java.lang.String r1 = "CHAT_MESSAGE_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 49
            goto L_0x0a2f
        L_0x064e:
            java.lang.String r1 = "CHAT_TITLE_EDITED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 67
            goto L_0x0a2f
        L_0x065a:
            java.lang.String r1 = "PINNED_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 85
            goto L_0x0a2f
        L_0x0666:
            java.lang.String r1 = "MESSAGE_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 0
            goto L_0x0a2f
        L_0x0671:
            java.lang.String r1 = "MESSAGE_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 13
            goto L_0x0a2f
        L_0x067d:
            java.lang.String r1 = "MESSAGE_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 14
            goto L_0x0a2f
        L_0x0689:
            java.lang.String r1 = "MESSAGE_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 18
            goto L_0x0a2f
        L_0x0695:
            java.lang.String r1 = "MESSAGE_FWDS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 22
            goto L_0x0a2f
        L_0x06a1:
            java.lang.String r1 = "MESSAGE_DOCS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 26
            goto L_0x0a2f
        L_0x06ad:
            java.lang.String r1 = "CHAT_MESSAGE_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 48
            goto L_0x0a2f
        L_0x06b9:
            java.lang.String r1 = "CHAT_MESSAGE_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 57
            goto L_0x0a2f
        L_0x06c5:
            java.lang.String r1 = "CHAT_MESSAGE_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 58
            goto L_0x0a2f
        L_0x06d1:
            java.lang.String r1 = "CHAT_MESSAGE_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 62
            goto L_0x0a2f
        L_0x06dd:
            java.lang.String r1 = "CHAT_MESSAGE_FWDS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 78
            goto L_0x0a2f
        L_0x06e9:
            java.lang.String r1 = "CHAT_MESSAGE_DOCS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 82
            goto L_0x0a2f
        L_0x06f5:
            java.lang.String r1 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 20
            goto L_0x0a2f
        L_0x0701:
            java.lang.String r1 = "PINNED_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 96
            goto L_0x0a2f
        L_0x070d:
            java.lang.String r1 = "MESSAGE_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 12
            goto L_0x0a2f
        L_0x0719:
            java.lang.String r1 = "PINNED_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 87
            goto L_0x0a2f
        L_0x0725:
            java.lang.String r1 = "PINNED_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 88
            goto L_0x0a2f
        L_0x0731:
            java.lang.String r1 = "PINNED_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 86
            goto L_0x0a2f
        L_0x073d:
            java.lang.String r1 = "PINNED_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 91
            goto L_0x0a2f
        L_0x0749:
            java.lang.String r1 = "MESSAGE_PHOTO_SECRET"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 4
            goto L_0x0a2f
        L_0x0754:
            java.lang.String r1 = "CHAT_VOICECHAT_INVITE_YOU"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 72
            goto L_0x0a2f
        L_0x0760:
            java.lang.String r1 = "CHANNEL_MESSAGE_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 30
            goto L_0x0a2f
        L_0x076c:
            java.lang.String r1 = "CHANNEL_MESSAGE_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 31
            goto L_0x0a2f
        L_0x0778:
            java.lang.String r1 = "CHANNEL_MESSAGE_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 29
            goto L_0x0a2f
        L_0x0784:
            java.lang.String r1 = "CHANNEL_MESSAGE_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 34
            goto L_0x0a2f
        L_0x0790:
            java.lang.String r1 = "CHAT_MESSAGE_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 54
            goto L_0x0a2f
        L_0x079c:
            java.lang.String r1 = "MESSAGES"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 27
            goto L_0x0a2f
        L_0x07a8:
            java.lang.String r1 = "CHAT_MESSAGE_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 61
            goto L_0x0a2f
        L_0x07b4:
            java.lang.String r1 = "CHAT_MESSAGE_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 59
            goto L_0x0a2f
        L_0x07c0:
            java.lang.String r1 = "CHAT_MESSAGE_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 53
            goto L_0x0a2f
        L_0x07cc:
            java.lang.String r1 = "CHAT_VOICECHAT_INVITE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 71
            goto L_0x0a2f
        L_0x07d8:
            java.lang.String r1 = "CHAT_LEFT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 75
            goto L_0x0a2f
        L_0x07e4:
            java.lang.String r1 = "CHAT_ADD_YOU"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 66
            goto L_0x0a2f
        L_0x07f0:
            java.lang.String r1 = "CHAT_DELETE_MEMBER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 73
            goto L_0x0a2f
        L_0x07fc:
            java.lang.String r1 = "MESSAGE_SCREENSHOT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 7
            goto L_0x0a2f
        L_0x0807:
            java.lang.String r1 = "AUTH_REGION"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 104(0x68, float:1.46E-43)
            goto L_0x0a2f
        L_0x0813:
            java.lang.String r1 = "CONTACT_JOINED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 102(0x66, float:1.43E-43)
            goto L_0x0a2f
        L_0x081f:
            java.lang.String r1 = "CHAT_MESSAGE_INVOICE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 64
            goto L_0x0a2f
        L_0x082b:
            java.lang.String r1 = "ENCRYPTION_REQUEST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 106(0x6a, float:1.49E-43)
            goto L_0x0a2f
        L_0x0837:
            java.lang.String r1 = "MESSAGE_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 16
            goto L_0x0a2f
        L_0x0843:
            java.lang.String r1 = "CHAT_DELETE_YOU"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 74
            goto L_0x0a2f
        L_0x084f:
            java.lang.String r1 = "AUTH_UNKNOWN"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 103(0x67, float:1.44E-43)
            goto L_0x0a2f
        L_0x085b:
            java.lang.String r1 = "PINNED_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 100
            goto L_0x0a2f
        L_0x0867:
            java.lang.String r1 = "PINNED_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 95
            goto L_0x0a2f
        L_0x0873:
            java.lang.String r1 = "PINNED_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 89
            goto L_0x0a2f
        L_0x087f:
            java.lang.String r1 = "PINNED_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 98
            goto L_0x0a2f
        L_0x088b:
            java.lang.String r1 = "CHANNEL_MESSAGE_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 33
            goto L_0x0a2f
        L_0x0897:
            java.lang.String r1 = "PHONE_CALL_REQUEST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 108(0x6c, float:1.51E-43)
            goto L_0x0a2f
        L_0x08a3:
            java.lang.String r1 = "PINNED_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 90
            goto L_0x0a2f
        L_0x08af:
            java.lang.String r1 = "PINNED_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 84
            goto L_0x0a2f
        L_0x08bb:
            java.lang.String r1 = "PINNED_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 93
            goto L_0x0a2f
        L_0x08c7:
            java.lang.String r1 = "PINNED_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 94
            goto L_0x0a2f
        L_0x08d3:
            java.lang.String r1 = "PINNED_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 97
            goto L_0x0a2f
        L_0x08df:
            java.lang.String r1 = "CHAT_MESSAGE_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 56
            goto L_0x0a2f
        L_0x08eb:
            java.lang.String r1 = "MESSAGE_VIDEO_SECRET"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 6
            goto L_0x0a2f
        L_0x08f6:
            java.lang.String r1 = "CHANNEL_MESSAGE_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 1
            goto L_0x0a2f
        L_0x0901:
            java.lang.String r1 = "CHANNEL_MESSAGE_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 36
            goto L_0x0a2f
        L_0x090d:
            java.lang.String r1 = "CHANNEL_MESSAGE_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 37
            goto L_0x0a2f
        L_0x0919:
            java.lang.String r1 = "CHANNEL_MESSAGE_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 41
            goto L_0x0a2f
        L_0x0925:
            java.lang.String r1 = "CHANNEL_MESSAGE_FWDS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 42
            goto L_0x0a2f
        L_0x0931:
            java.lang.String r1 = "CHANNEL_MESSAGE_DOCS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 46
            goto L_0x0a2f
        L_0x093d:
            java.lang.String r1 = "PINNED_INVOICE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 99
            goto L_0x0a2f
        L_0x0949:
            java.lang.String r1 = "CHAT_RETURNED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 76
            goto L_0x0a2f
        L_0x0955:
            java.lang.String r1 = "ENCRYPTED_MESSAGE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 101(0x65, float:1.42E-43)
            goto L_0x0a2f
        L_0x0961:
            java.lang.String r1 = "ENCRYPTION_ACCEPT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 107(0x6b, float:1.5E-43)
            goto L_0x0a2f
        L_0x096d:
            java.lang.String r1 = "MESSAGE_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 5
            goto L_0x0a2f
        L_0x0978:
            java.lang.String r1 = "MESSAGE_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 8
            goto L_0x0a2f
        L_0x0984:
            java.lang.String r1 = "MESSAGE_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 3
            goto L_0x0a2f
        L_0x098f:
            java.lang.String r1 = "MESSAGE_MUTED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 109(0x6d, float:1.53E-43)
            goto L_0x0a2f
        L_0x099b:
            java.lang.String r1 = "MESSAGE_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 11
            goto L_0x0a2f
        L_0x09a7:
            java.lang.String r1 = "CHAT_MESSAGES"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 83
            goto L_0x0a2f
        L_0x09b3:
            java.lang.String r1 = "CHAT_VOICECHAT_START"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 70
            goto L_0x0a2f
        L_0x09bf:
            java.lang.String r1 = "CHAT_JOINED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 77
            goto L_0x0a2f
        L_0x09cb:
            java.lang.String r1 = "CHAT_ADD_MEMBER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 69
            goto L_0x0a2f
        L_0x09d6:
            java.lang.String r1 = "CHANNEL_MESSAGE_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 40
            goto L_0x0a2f
        L_0x09e1:
            java.lang.String r1 = "CHANNEL_MESSAGE_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 38
            goto L_0x0a2f
        L_0x09ec:
            java.lang.String r1 = "CHANNEL_MESSAGE_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 32
            goto L_0x0a2f
        L_0x09f7:
            java.lang.String r1 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 44
            goto L_0x0a2f
        L_0x0a02:
            java.lang.String r1 = "MESSAGE_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 10
            goto L_0x0a2f
        L_0x0a0d:
            java.lang.String r1 = "CHAT_CREATED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 65
            goto L_0x0a2f
        L_0x0a18:
            java.lang.String r1 = "CHANNEL_MESSAGE_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 35
            goto L_0x0a2f
        L_0x0a23:
            java.lang.String r1 = "MESSAGE_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a2e
            r1 = 19
            goto L_0x0a2f
        L_0x0a2e:
            r1 = -1
        L_0x0a2f:
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
                case 0: goto L_0x1af0;
                case 1: goto L_0x1af0;
                case 2: goto L_0x1ad0;
                case 3: goto L_0x1ab3;
                case 4: goto L_0x1a96;
                case 5: goto L_0x1a79;
                case 6: goto L_0x1a5b;
                case 7: goto L_0x1a45;
                case 8: goto L_0x1a27;
                case 9: goto L_0x1a09;
                case 10: goto L_0x19ae;
                case 11: goto L_0x1990;
                case 12: goto L_0x196d;
                case 13: goto L_0x194a;
                case 14: goto L_0x1927;
                case 15: goto L_0x1909;
                case 16: goto L_0x18eb;
                case 17: goto L_0x18cd;
                case 18: goto L_0x18aa;
                case 19: goto L_0x188b;
                case 20: goto L_0x188b;
                case 21: goto L_0x1868;
                case 22: goto L_0x1842;
                case 23: goto L_0x181e;
                case 24: goto L_0x17fb;
                case 25: goto L_0x17d8;
                case 26: goto L_0x17b3;
                case 27: goto L_0x179d;
                case 28: goto L_0x177f;
                case 29: goto L_0x1761;
                case 30: goto L_0x1743;
                case 31: goto L_0x1725;
                case 32: goto L_0x1707;
                case 33: goto L_0x16ac;
                case 34: goto L_0x168e;
                case 35: goto L_0x166b;
                case 36: goto L_0x1648;
                case 37: goto L_0x1625;
                case 38: goto L_0x1607;
                case 39: goto L_0x15e9;
                case 40: goto L_0x15cb;
                case 41: goto L_0x15ad;
                case 42: goto L_0x1583;
                case 43: goto L_0x155f;
                case 44: goto L_0x153b;
                case 45: goto L_0x1517;
                case 46: goto L_0x14f1;
                case 47: goto L_0x14dc;
                case 48: goto L_0x14bb;
                case 49: goto L_0x1498;
                case 50: goto L_0x1475;
                case 51: goto L_0x1452;
                case 52: goto L_0x142f;
                case 53: goto L_0x140c;
                case 54: goto L_0x1393;
                case 55: goto L_0x1370;
                case 56: goto L_0x1348;
                case 57: goto L_0x1320;
                case 58: goto L_0x12f8;
                case 59: goto L_0x12d5;
                case 60: goto L_0x12b2;
                case 61: goto L_0x128f;
                case 62: goto L_0x1267;
                case 63: goto L_0x1243;
                case 64: goto L_0x121b;
                case 65: goto L_0x1201;
                case 66: goto L_0x1201;
                case 67: goto L_0x11e7;
                case 68: goto L_0x11cd;
                case 69: goto L_0x11ae;
                case 70: goto L_0x1194;
                case 71: goto L_0x1175;
                case 72: goto L_0x115b;
                case 73: goto L_0x1141;
                case 74: goto L_0x1127;
                case 75: goto L_0x110d;
                case 76: goto L_0x10f3;
                case 77: goto L_0x10d9;
                case 78: goto L_0x10ae;
                case 79: goto L_0x1085;
                case 80: goto L_0x105c;
                case 81: goto L_0x1033;
                case 82: goto L_0x1008;
                case 83: goto L_0x0fee;
                case 84: goto L_0x0var_;
                case 85: goto L_0x0f4e;
                case 86: goto L_0x0var_;
                case 87: goto L_0x0eb8;
                case 88: goto L_0x0e6d;
                case 89: goto L_0x0e22;
                case 90: goto L_0x0d6b;
                case 91: goto L_0x0d20;
                case 92: goto L_0x0ccb;
                case 93: goto L_0x0CLASSNAME;
                case 94: goto L_0x0CLASSNAME;
                case 95: goto L_0x0bdc;
                case 96: goto L_0x0b96;
                case 97: goto L_0x0b4d;
                case 98: goto L_0x0b04;
                case 99: goto L_0x0abb;
                case 100: goto L_0x0a72;
                case 101: goto L_0x0a56;
                case 102: goto L_0x0a52;
                case 103: goto L_0x0a52;
                case 104: goto L_0x0a52;
                case 105: goto L_0x0a52;
                case 106: goto L_0x0a52;
                case 107: goto L_0x0a52;
                case 108: goto L_0x0a52;
                case 109: goto L_0x0a52;
                case 110: goto L_0x0a52;
                default: goto L_0x0a4c;
            }
        L_0x0a4c:
            r1 = r18
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1bf4 }
            goto L_0x1b0b
        L_0x0a52:
            r1 = r18
            goto L_0x1b21
        L_0x0a56:
            java.lang.String r1 = "YouHaveNewMessage"
            r2 = 2131627832(0x7f0e0var_, float:1.888294E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "SecretChatName"
            r5 = 2131627044(0x7f0e0CLASSNAME, float:1.8881341E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r5)     // Catch:{ all -> 0x0420 }
            r34 = r2
            r5 = 1
            r16 = 0
            r2 = r1
            r1 = r18
            goto L_0x1b25
        L_0x0a72:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0a8e
            java.lang.String r1 = "NotificationActionPinnedGifUser"
            r2 = 2131626138(0x7f0e089a, float:1.8879504E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0a8e:
            if (r6 == 0) goto L_0x0aa8
            java.lang.String r1 = "NotificationActionPinnedGif"
            r2 = 2131626136(0x7f0e0898, float:1.88795E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0aa8:
            java.lang.String r1 = "NotificationActionPinnedGifChannel"
            r2 = 2131626137(0x7f0e0899, float:1.8879502E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0420 }
            r6[r5] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0abb:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0ad7
            java.lang.String r1 = "NotificationActionPinnedInvoiceUser"
            r2 = 2131626141(0x7f0e089d, float:1.887951E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0ad7:
            if (r6 == 0) goto L_0x0af1
            java.lang.String r1 = "NotificationActionPinnedInvoice"
            r2 = 2131626139(0x7f0e089b, float:1.8879506E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0af1:
            java.lang.String r1 = "NotificationActionPinnedInvoiceChannel"
            r2 = 2131626140(0x7f0e089c, float:1.8879508E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0420 }
            r6[r5] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0b04:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0b20
            java.lang.String r1 = "NotificationActionPinnedGameScoreUser"
            r2 = 2131626128(0x7f0e0890, float:1.8879483E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0b20:
            if (r6 == 0) goto L_0x0b3a
            java.lang.String r1 = "NotificationActionPinnedGameScore"
            r2 = 2131626126(0x7f0e088e, float:1.887948E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0b3a:
            java.lang.String r1 = "NotificationActionPinnedGameScoreChannel"
            r2 = 2131626127(0x7f0e088f, float:1.8879481E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0420 }
            r6[r5] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0b4d:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0b69
            java.lang.String r1 = "NotificationActionPinnedGameUser"
            r2 = 2131626129(0x7f0e0891, float:1.8879485E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0b69:
            if (r6 == 0) goto L_0x0b83
            java.lang.String r1 = "NotificationActionPinnedGame"
            r2 = 2131626124(0x7f0e088c, float:1.8879475E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0b83:
            java.lang.String r1 = "NotificationActionPinnedGameChannel"
            r2 = 2131626125(0x7f0e088d, float:1.8879477E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0420 }
            r6[r5] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0b96:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0bb1
            java.lang.String r1 = "NotificationActionPinnedGeoLiveUser"
            r2 = 2131626134(0x7f0e0896, float:1.8879496E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0bb1:
            if (r6 == 0) goto L_0x0bca
            java.lang.String r1 = "NotificationActionPinnedGeoLive"
            r2 = 2131626132(0x7f0e0894, float:1.8879492E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0bca:
            java.lang.String r1 = "NotificationActionPinnedGeoLiveChannel"
            r2 = 2131626133(0x7f0e0895, float:1.8879494E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0420 }
            r6[r5] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0bdc:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0bf7
            java.lang.String r1 = "NotificationActionPinnedGeoUser"
            r2 = 2131626135(0x7f0e0897, float:1.8879498E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0bf7:
            if (r6 == 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGeo"
            r2 = 2131626130(0x7f0e0892, float:1.8879488E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.lang.String r1 = "NotificationActionPinnedGeoChannel"
            r2 = 2131626131(0x7f0e0893, float:1.887949E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0420 }
            r6[r5] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0420 }
        L_0x0CLASSNAME:
            r2 = r1
            r1 = r18
            goto L_0x1b22
        L_0x0CLASSNAME:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedPollUser"
            r2 = 2131626153(0x7f0e08a9, float:1.8879534E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            if (r6 == 0) goto L_0x0c5f
            java.lang.String r1 = "NotificationActionPinnedPoll2"
            r2 = 2131626151(0x7f0e08a7, float:1.887953E38)
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 2
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r8 = 1
            r5[r8] = r7     // Catch:{ all -> 0x0420 }
            r7 = r12[r8]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0c5f:
            java.lang.String r1 = "NotificationActionPinnedPollChannel2"
            r2 = 2131626152(0x7f0e08a8, float:1.8879532E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0CLASSNAME
            java.lang.String r2 = "NotificationActionPinnedQuizUser"
            r5 = 2131626156(0x7f0e08ac, float:1.887954E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0CLASSNAME:
            if (r6 == 0) goto L_0x0cb3
            java.lang.String r2 = "NotificationActionPinnedQuiz2"
            r5 = 2131626154(0x7f0e08aa, float:1.8879536E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r10 = 1
            r6[r10] = r8     // Catch:{ all -> 0x0420 }
            r8 = r12[r10]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0cb3:
            java.lang.String r2 = "NotificationActionPinnedQuizChannel2"
            r5 = 2131626155(0x7f0e08ab, float:1.8879538E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0ccb:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0ce9
            java.lang.String r2 = "NotificationActionPinnedContactUser"
            r5 = 2131626120(0x7f0e0888, float:1.8879467E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0ce9:
            if (r6 == 0) goto L_0x0d08
            java.lang.String r2 = "NotificationActionPinnedContact2"
            r5 = 2131626118(0x7f0e0886, float:1.8879463E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r10 = 1
            r6[r10] = r8     // Catch:{ all -> 0x0420 }
            r8 = r12[r10]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0d08:
            java.lang.String r2 = "NotificationActionPinnedContactChannel2"
            r5 = 2131626119(0x7f0e0887, float:1.8879465E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0d20:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0d3e
            java.lang.String r2 = "NotificationActionPinnedVoiceUser"
            r5 = 2131626174(0x7f0e08be, float:1.8879577E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0d3e:
            if (r6 == 0) goto L_0x0d58
            java.lang.String r2 = "NotificationActionPinnedVoice"
            r5 = 2131626172(0x7f0e08bc, float:1.8879573E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0d58:
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            r5 = 2131626173(0x7f0e08bd, float:1.8879575E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0d6b:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0da8
            int r2 = r12.length     // Catch:{ all -> 0x0420 }
            r5 = 1
            if (r2 <= r5) goto L_0x0d95
            r2 = r12[r5]     // Catch:{ all -> 0x0420 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0420 }
            if (r2 != 0) goto L_0x0d95
            java.lang.String r2 = "NotificationActionPinnedStickerEmojiUser"
            r5 = 2131626164(0x7f0e08b4, float:1.8879556E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0d95:
            java.lang.String r2 = "NotificationActionPinnedStickerUser"
            r5 = 2131626165(0x7f0e08b5, float:1.8879558E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0da8:
            if (r6 == 0) goto L_0x0deb
            int r2 = r12.length     // Catch:{ all -> 0x0420 }
            r5 = 2
            if (r2 <= r5) goto L_0x0dd3
            r2 = r12[r5]     // Catch:{ all -> 0x0420 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0420 }
            if (r2 != 0) goto L_0x0dd3
            java.lang.String r2 = "NotificationActionPinnedStickerEmoji"
            r5 = 2131626162(0x7f0e08b2, float:1.8879552E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r10 = 1
            r6[r10] = r8     // Catch:{ all -> 0x0420 }
            r8 = r12[r10]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0dd3:
            java.lang.String r2 = "NotificationActionPinnedSticker"
            r5 = 2131626160(0x7f0e08b0, float:1.8879548E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0deb:
            int r2 = r12.length     // Catch:{ all -> 0x0420 }
            r5 = 1
            if (r2 <= r5) goto L_0x0e0f
            r2 = r12[r5]     // Catch:{ all -> 0x0420 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0420 }
            if (r2 != 0) goto L_0x0e0f
            java.lang.String r2 = "NotificationActionPinnedStickerEmojiChannel"
            r5 = 2131626163(0x7f0e08b3, float:1.8879554E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0e0f:
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            r5 = 2131626161(0x7f0e08b1, float:1.887955E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0e22:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e40
            java.lang.String r2 = "NotificationActionPinnedFileUser"
            r5 = 2131626123(0x7f0e088b, float:1.8879473E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0e40:
            if (r6 == 0) goto L_0x0e5a
            java.lang.String r2 = "NotificationActionPinnedFile"
            r5 = 2131626121(0x7f0e0889, float:1.887947E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0e5a:
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            r5 = 2131626122(0x7f0e088a, float:1.8879471E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0e6d:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e8b
            java.lang.String r2 = "NotificationActionPinnedRoundUser"
            r5 = 2131626159(0x7f0e08af, float:1.8879546E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0e8b:
            if (r6 == 0) goto L_0x0ea5
            java.lang.String r2 = "NotificationActionPinnedRound"
            r5 = 2131626157(0x7f0e08ad, float:1.8879542E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0ea5:
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            r5 = 2131626158(0x7f0e08ae, float:1.8879544E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0eb8:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0ed6
            java.lang.String r2 = "NotificationActionPinnedVideoUser"
            r5 = 2131626171(0x7f0e08bb, float:1.887957E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0ed6:
            if (r6 == 0) goto L_0x0ef0
            java.lang.String r2 = "NotificationActionPinnedVideo"
            r5 = 2131626169(0x7f0e08b9, float:1.8879567E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0ef0:
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            r5 = 2131626170(0x7f0e08ba, float:1.8879569E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0var_:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0var_
            java.lang.String r2 = "NotificationActionPinnedPhotoUser"
            r5 = 2131626150(0x7f0e08a6, float:1.8879528E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0var_:
            if (r6 == 0) goto L_0x0f3b
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            r5 = 2131626148(0x7f0e08a4, float:1.8879524E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0f3b:
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            r5 = 2131626149(0x7f0e08a5, float:1.8879526E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0f4e:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0f6c
            java.lang.String r2 = "NotificationActionPinnedNoTextUser"
            r5 = 2131626147(0x7f0e08a3, float:1.8879522E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0f6c:
            if (r6 == 0) goto L_0x0var_
            java.lang.String r2 = "NotificationActionPinnedNoText"
            r5 = 2131626145(0x7f0e08a1, float:1.8879518E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0var_:
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            r5 = 2131626146(0x7f0e08a2, float:1.887952E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0var_:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0fb7
            java.lang.String r2 = "NotificationActionPinnedTextUser"
            r5 = 2131626168(0x7f0e08b8, float:1.8879565E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0fb7:
            if (r6 == 0) goto L_0x0fd6
            java.lang.String r2 = "NotificationActionPinnedText"
            r5 = 2131626166(0x7f0e08b6, float:1.887956E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0fd6:
            java.lang.String r2 = "NotificationActionPinnedTextChannel"
            r5 = 2131626167(0x7f0e08b7, float:1.8879563E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x0fee:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAlbum"
            r5 = 2131626183(0x7f0e08c7, float:1.8879595E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x17b0
        L_0x1008:
            r1 = r18
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r2[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r2[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "Files"
            r6 = 2
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0420 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)     // Catch:{ all -> 0x0420 }
            r2[r6] = r5     // Catch:{ all -> 0x0420 }
            r5 = 2131626185(0x7f0e08c9, float:1.88796E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17b0
        L_0x1033:
            r1 = r18
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r2[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r2[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 2
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0420 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)     // Catch:{ all -> 0x0420 }
            r2[r6] = r5     // Catch:{ all -> 0x0420 }
            r5 = 2131626185(0x7f0e08c9, float:1.88796E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17b0
        L_0x105c:
            r1 = r18
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r2[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r2[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x0420 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r10, r6)     // Catch:{ all -> 0x0420 }
            r2[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2131626185(0x7f0e08c9, float:1.88796E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17b0
        L_0x1085:
            r1 = r18
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r2[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r2[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x0420 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r7, r6)     // Catch:{ all -> 0x0420 }
            r2[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2131626185(0x7f0e08c9, float:1.88796E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17b0
        L_0x10ae:
            r1 = r18
            java.lang.String r2 = "NotificationGroupForwardedFew"
            r5 = 2131626186(0x7f0e08ca, float:1.8879601E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r10 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r10     // Catch:{ all -> 0x0420 }
            r7 = 1
            r10 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r10     // Catch:{ all -> 0x0420 }
            r7 = 2
            r10 = r12[r7]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt(r10)     // Catch:{ all -> 0x0420 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r10)     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x17b0
        L_0x10d9:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            r5 = 2131626182(0x7f0e08c6, float:1.8879593E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x10f3:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAddSelf"
            r5 = 2131626181(0x7f0e08c5, float:1.887959E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x110d:
            r1 = r18
            java.lang.String r2 = "NotificationGroupLeftMember"
            r5 = 2131626191(0x7f0e08cf, float:1.8879611E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x1127:
            r1 = r18
            java.lang.String r2 = "NotificationGroupKickYou"
            r5 = 2131626190(0x7f0e08ce, float:1.887961E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x1141:
            r1 = r18
            java.lang.String r2 = "NotificationGroupKickMember"
            r5 = 2131626189(0x7f0e08cd, float:1.8879607E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x115b:
            r1 = r18
            java.lang.String r2 = "NotificationGroupInvitedYouToCall"
            r5 = 2131626188(0x7f0e08cc, float:1.8879605E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x1175:
            r1 = r18
            java.lang.String r2 = "NotificationGroupInvitedToCall"
            r5 = 2131626187(0x7f0e08cb, float:1.8879603E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x1194:
            r1 = r18
            java.lang.String r2 = "NotificationGroupCreatedCall"
            r5 = 2131626184(0x7f0e08c8, float:1.8879597E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x11ae:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAddMember"
            r5 = 2131626180(0x7f0e08c4, float:1.8879589E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x11cd:
            r1 = r18
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            r5 = 2131626178(0x7f0e08c2, float:1.8879585E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x11e7:
            r1 = r18
            java.lang.String r2 = "NotificationEditedGroupName"
            r5 = 2131626177(0x7f0e08c1, float:1.8879583E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x1201:
            r1 = r18
            java.lang.String r2 = "NotificationInvitedToGroup"
            r5 = 2131626196(0x7f0e08d4, float:1.8879621E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x121b:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupInvoice"
            r5 = 2131626213(0x7f0e08e5, float:1.8879656E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "PaymentInvoice"
            r6 = 2131626593(0x7f0e0a61, float:1.8880427E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1243:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupGameScored"
            r5 = 2131626211(0x7f0e08e3, float:1.8879652E38)
            r6 = 4
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r8 = 0
            r10 = r12[r8]     // Catch:{ all -> 0x0420 }
            r6[r8] = r10     // Catch:{ all -> 0x0420 }
            r8 = 1
            r10 = r12[r8]     // Catch:{ all -> 0x0420 }
            r6[r8] = r10     // Catch:{ all -> 0x0420 }
            r8 = 2
            r10 = r12[r8]     // Catch:{ all -> 0x0420 }
            r6[r8] = r10     // Catch:{ all -> 0x0420 }
            r7 = 3
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x1267:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupGame"
            r5 = 2131626210(0x7f0e08e2, float:1.887965E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachGame"
            r6 = 2131624353(0x7f0e01a1, float:1.8875883E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x128f:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupGif"
            r5 = 2131626212(0x7f0e08e4, float:1.8879654E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachGif"
            r6 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x12b2:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupLiveLocation"
            r5 = 2131626214(0x7f0e08e6, float:1.8879658E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachLiveLocation"
            r6 = 2131624359(0x7f0e01a7, float:1.8875895E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x12d5:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupMap"
            r5 = 2131626215(0x7f0e08e7, float:1.887966E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachLocation"
            r6 = 2131624363(0x7f0e01ab, float:1.8875904E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x12f8:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupPoll2"
            r5 = 2131626219(0x7f0e08eb, float:1.8879668E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "Poll"
            r6 = 2131626729(0x7f0e0ae9, float:1.8880702E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1320:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupQuiz2"
            r5 = 2131626220(0x7f0e08ec, float:1.887967E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "PollQuiz"
            r6 = 2131626736(0x7f0e0af0, float:1.8880717E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1348:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupContact2"
            r5 = 2131626208(0x7f0e08e0, float:1.8879646E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachContact"
            r6 = 2131624349(0x7f0e019d, float:1.8875875E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1370:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupAudio"
            r5 = 2131626207(0x7f0e08df, float:1.8879644E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachAudio"
            r6 = 2131624347(0x7f0e019b, float:1.8875871E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1393:
            r1 = r18
            int r2 = r12.length     // Catch:{ all -> 0x0420 }
            r5 = 2
            if (r2 <= r5) goto L_0x13d9
            r2 = r12[r5]     // Catch:{ all -> 0x0420 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0420 }
            if (r2 != 0) goto L_0x13d9
            java.lang.String r2 = "NotificationMessageGroupStickerEmoji"
            r5 = 2131626223(0x7f0e08ef, float:1.8879676E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0420 }
            r5.<init>()     // Catch:{ all -> 0x0420 }
            r6 = r12[r7]     // Catch:{ all -> 0x0420 }
            r5.append(r6)     // Catch:{ all -> 0x0420 }
            r5.append(r15)     // Catch:{ all -> 0x0420 }
            r6 = 2131624370(0x7f0e01b2, float:1.8875918E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)     // Catch:{ all -> 0x0420 }
            r5.append(r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x13d9:
            java.lang.String r2 = "NotificationMessageGroupSticker"
            r5 = 2131626222(0x7f0e08ee, float:1.8879674E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0420 }
            r5.<init>()     // Catch:{ all -> 0x0420 }
            r6 = r12[r7]     // Catch:{ all -> 0x0420 }
            r5.append(r6)     // Catch:{ all -> 0x0420 }
            r5.append(r15)     // Catch:{ all -> 0x0420 }
            r6 = 2131624370(0x7f0e01b2, float:1.8875918E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)     // Catch:{ all -> 0x0420 }
            r5.append(r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x140c:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupDocument"
            r5 = 2131626209(0x7f0e08e1, float:1.8879648E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachDocument"
            r6 = 2131624352(0x7f0e01a0, float:1.8875881E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x142f:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupRound"
            r5 = 2131626221(0x7f0e08ed, float:1.8879672E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachRound"
            r6 = 2131624369(0x7f0e01b1, float:1.8875916E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1452:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupVideo"
            r5 = 2131626225(0x7f0e08f1, float:1.887968E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachVideo"
            r6 = 2131624373(0x7f0e01b5, float:1.8875924E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1475:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupPhoto"
            r5 = 2131626218(0x7f0e08ea, float:1.8879666E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachPhoto"
            r6 = 2131624367(0x7f0e01af, float:1.8875912E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1498:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupNoText"
            r5 = 2131626217(0x7f0e08e9, float:1.8879664E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "Message"
            r6 = 2131625921(0x7f0e07c1, float:1.8879064E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x14bb:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupText"
            r5 = 2131626224(0x7f0e08f0, float:1.8879678E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            r5 = r12[r7]     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x14dc:
            r1 = r18
            java.lang.String r2 = "ChannelMessageAlbum"
            r5 = 2131624663(0x7f0e02d7, float:1.8876512E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x17b0
        L_0x14f1:
            r1 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r2[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "Files"
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0420 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)     // Catch:{ all -> 0x0420 }
            r2[r6] = r5     // Catch:{ all -> 0x0420 }
            r5 = 2131624667(0x7f0e02db, float:1.887652E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17b0
        L_0x1517:
            r1 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r2[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0420 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)     // Catch:{ all -> 0x0420 }
            r2[r6] = r5     // Catch:{ all -> 0x0420 }
            r5 = 2131624667(0x7f0e02db, float:1.887652E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17b0
        L_0x153b:
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
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r10, r6)     // Catch:{ all -> 0x0420 }
            r2[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2131624667(0x7f0e02db, float:1.887652E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17b0
        L_0x155f:
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
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r7, r6)     // Catch:{ all -> 0x0420 }
            r2[r5] = r6     // Catch:{ all -> 0x0420 }
            r5 = 2131624667(0x7f0e02db, float:1.887652E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17b0
        L_0x1583:
            r1 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x0420 }
            r2[r5] = r6     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "ForwardedMessageCount"
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0420 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = r5.toLowerCase()     // Catch:{ all -> 0x0420 }
            r2[r6] = r5     // Catch:{ all -> 0x0420 }
            r5 = 2131624667(0x7f0e02db, float:1.887652E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17b0
        L_0x15ad:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGame"
            r5 = 2131626204(0x7f0e08dc, float:1.8879638E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachGame"
            r6 = 2131624353(0x7f0e01a1, float:1.8875883E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x15cb:
            r1 = r18
            java.lang.String r2 = "ChannelMessageGIF"
            r5 = 2131624668(0x7f0e02dc, float:1.8876522E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachGif"
            r6 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x15e9:
            r1 = r18
            java.lang.String r2 = "ChannelMessageLiveLocation"
            r5 = 2131624669(0x7f0e02dd, float:1.8876524E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachLiveLocation"
            r6 = 2131624359(0x7f0e01a7, float:1.8875895E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1607:
            r1 = r18
            java.lang.String r2 = "ChannelMessageMap"
            r5 = 2131624670(0x7f0e02de, float:1.8876526E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachLocation"
            r6 = 2131624363(0x7f0e01ab, float:1.8875904E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1625:
            r1 = r18
            java.lang.String r2 = "ChannelMessagePoll2"
            r5 = 2131624674(0x7f0e02e2, float:1.8876534E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "Poll"
            r6 = 2131626729(0x7f0e0ae9, float:1.8880702E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1648:
            r1 = r18
            java.lang.String r2 = "ChannelMessageQuiz2"
            r5 = 2131624675(0x7f0e02e3, float:1.8876536E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "QuizPoll"
            r6 = 2131626836(0x7f0e0b54, float:1.888092E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x166b:
            r1 = r18
            java.lang.String r2 = "ChannelMessageContact2"
            r5 = 2131624665(0x7f0e02d9, float:1.8876516E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachContact"
            r6 = 2131624349(0x7f0e019d, float:1.8875875E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x168e:
            r1 = r18
            java.lang.String r2 = "ChannelMessageAudio"
            r5 = 2131624664(0x7f0e02d8, float:1.8876514E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachAudio"
            r6 = 2131624347(0x7f0e019b, float:1.8875871E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x16ac:
            r1 = r18
            int r2 = r12.length     // Catch:{ all -> 0x0420 }
            r5 = 1
            if (r2 <= r5) goto L_0x16ed
            r2 = r12[r5]     // Catch:{ all -> 0x0420 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0420 }
            if (r2 != 0) goto L_0x16ed
            java.lang.String r2 = "ChannelMessageStickerEmoji"
            r5 = 2131624678(0x7f0e02e6, float:1.8876542E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0420 }
            r5.<init>()     // Catch:{ all -> 0x0420 }
            r6 = r12[r7]     // Catch:{ all -> 0x0420 }
            r5.append(r6)     // Catch:{ all -> 0x0420 }
            r5.append(r15)     // Catch:{ all -> 0x0420 }
            r6 = 2131624370(0x7f0e01b2, float:1.8875918E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)     // Catch:{ all -> 0x0420 }
            r5.append(r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x16ed:
            java.lang.String r2 = "ChannelMessageSticker"
            r5 = 2131624677(0x7f0e02e5, float:1.887654E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            r5 = 2131624370(0x7f0e01b2, float:1.8875918E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1707:
            r1 = r18
            java.lang.String r2 = "ChannelMessageDocument"
            r5 = 2131624666(0x7f0e02da, float:1.8876518E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachDocument"
            r6 = 2131624352(0x7f0e01a0, float:1.8875881E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1725:
            r1 = r18
            java.lang.String r2 = "ChannelMessageRound"
            r5 = 2131624676(0x7f0e02e4, float:1.8876538E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachRound"
            r6 = 2131624369(0x7f0e01b1, float:1.8875916E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1743:
            r1 = r18
            java.lang.String r2 = "ChannelMessageVideo"
            r5 = 2131624679(0x7f0e02e7, float:1.8876545E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachVideo"
            r6 = 2131624373(0x7f0e01b5, float:1.8875924E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1761:
            r1 = r18
            java.lang.String r2 = "ChannelMessagePhoto"
            r5 = 2131624673(0x7f0e02e1, float:1.8876532E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachPhoto"
            r6 = 2131624367(0x7f0e01af, float:1.8875912E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x177f:
            r1 = r18
            java.lang.String r2 = "ChannelMessageNoText"
            r5 = 2131624672(0x7f0e02e0, float:1.887653E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "Message"
            r6 = 2131625921(0x7f0e07c1, float:1.8879064E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x179d:
            r1 = r18
            java.lang.String r2 = "NotificationMessageAlbum"
            r5 = 2131626198(0x7f0e08d6, float:1.8879625E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
        L_0x17b0:
            r5 = 1
            goto L_0x1b23
        L_0x17b3:
            r1 = r18
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r6 = "Files"
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ all -> 0x0420 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r6, r8)     // Catch:{ all -> 0x0420 }
            r5[r7] = r6     // Catch:{ all -> 0x0420 }
            r6 = 2131626202(0x7f0e08da, float:1.8879634E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x17b0
        L_0x17d8:
            r1 = r18
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ all -> 0x0420 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r8)     // Catch:{ all -> 0x0420 }
            r6[r7] = r5     // Catch:{ all -> 0x0420 }
            r5 = 2131626202(0x7f0e08da, float:1.8879634E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x17b0
        L_0x17fb:
            r1 = r18
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0420 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r10, r7)     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 2131626202(0x7f0e08da, float:1.8879634E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x17b0
        L_0x181e:
            r1 = r18
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r5[r6] = r8     // Catch:{ all -> 0x0420 }
            r6 = 1
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ all -> 0x0420 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r8)     // Catch:{ all -> 0x0420 }
            r5[r6] = r7     // Catch:{ all -> 0x0420 }
            r6 = 2131626202(0x7f0e08da, float:1.8879634E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x17b0
        L_0x1842:
            r1 = r18
            java.lang.String r2 = "NotificationMessageForwardFew"
            r5 = 2131626203(0x7f0e08db, float:1.8879636E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r10 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r10     // Catch:{ all -> 0x0420 }
            r7 = 1
            r10 = r12[r7]     // Catch:{ all -> 0x0420 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt(r10)     // Catch:{ all -> 0x0420 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x0420 }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r10)     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x17b0
        L_0x1868:
            r1 = r18
            java.lang.String r2 = "NotificationMessageInvoice"
            r5 = 2131626226(0x7f0e08f2, float:1.8879682E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "PaymentInvoice"
            r6 = 2131626593(0x7f0e0a61, float:1.8880427E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x188b:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGameScored"
            r5 = 2131626205(0x7f0e08dd, float:1.887964E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x18aa:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGame"
            r5 = 2131626204(0x7f0e08dc, float:1.8879638E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachGame"
            r6 = 2131624353(0x7f0e01a1, float:1.8875883E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x18cd:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGif"
            r5 = 2131626206(0x7f0e08de, float:1.8879642E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachGif"
            r6 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x18eb:
            r1 = r18
            java.lang.String r2 = "NotificationMessageLiveLocation"
            r5 = 2131626227(0x7f0e08f3, float:1.8879684E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachLiveLocation"
            r6 = 2131624359(0x7f0e01a7, float:1.8875895E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1909:
            r1 = r18
            java.lang.String r2 = "NotificationMessageMap"
            r5 = 2131626228(0x7f0e08f4, float:1.8879686E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachLocation"
            r6 = 2131624363(0x7f0e01ab, float:1.8875904E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1927:
            r1 = r18
            java.lang.String r2 = "NotificationMessagePoll2"
            r5 = 2131626232(0x7f0e08f8, float:1.8879694E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "Poll"
            r6 = 2131626729(0x7f0e0ae9, float:1.8880702E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x194a:
            r1 = r18
            java.lang.String r2 = "NotificationMessageQuiz2"
            r5 = 2131626233(0x7f0e08f9, float:1.8879696E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "QuizPoll"
            r6 = 2131626836(0x7f0e0b54, float:1.888092E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x196d:
            r1 = r18
            java.lang.String r2 = "NotificationMessageContact2"
            r5 = 2131626200(0x7f0e08d8, float:1.887963E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachContact"
            r6 = 2131624349(0x7f0e019d, float:1.8875875E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1990:
            r1 = r18
            java.lang.String r2 = "NotificationMessageAudio"
            r5 = 2131626199(0x7f0e08d7, float:1.8879627E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachAudio"
            r6 = 2131624347(0x7f0e019b, float:1.8875871E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x19ae:
            r1 = r18
            int r2 = r12.length     // Catch:{ all -> 0x0420 }
            r5 = 1
            if (r2 <= r5) goto L_0x19ef
            r2 = r12[r5]     // Catch:{ all -> 0x0420 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0420 }
            if (r2 != 0) goto L_0x19ef
            java.lang.String r2 = "NotificationMessageStickerEmoji"
            r5 = 2131626240(0x7f0e0900, float:1.887971E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0420 }
            r5.<init>()     // Catch:{ all -> 0x0420 }
            r6 = r12[r7]     // Catch:{ all -> 0x0420 }
            r5.append(r6)     // Catch:{ all -> 0x0420 }
            r5.append(r15)     // Catch:{ all -> 0x0420 }
            r6 = 2131624370(0x7f0e01b2, float:1.8875918E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)     // Catch:{ all -> 0x0420 }
            r5.append(r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x19ef:
            java.lang.String r2 = "NotificationMessageSticker"
            r5 = 2131626239(0x7f0e08ff, float:1.8879709E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            r5 = 2131624370(0x7f0e01b2, float:1.8875918E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1a09:
            r1 = r18
            java.lang.String r2 = "NotificationMessageDocument"
            r5 = 2131626201(0x7f0e08d9, float:1.8879632E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachDocument"
            r6 = 2131624352(0x7f0e01a0, float:1.8875881E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1a27:
            r1 = r18
            java.lang.String r2 = "NotificationMessageRound"
            r5 = 2131626234(0x7f0e08fa, float:1.8879698E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachRound"
            r6 = 2131624369(0x7f0e01b1, float:1.8875916E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1a45:
            r1 = r18
            java.lang.String r2 = "ActionTakeScreenshoot"
            r5 = 2131624136(0x7f0e00c8, float:1.8875443E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r5)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "un1"
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = r2.replace(r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b22
        L_0x1a5b:
            r1 = r18
            java.lang.String r2 = "NotificationMessageSDVideo"
            r5 = 2131626236(0x7f0e08fc, float:1.8879702E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachDestructingVideo"
            r6 = 2131624351(0x7f0e019f, float:1.887588E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1a79:
            r1 = r18
            java.lang.String r2 = "NotificationMessageVideo"
            r5 = 2131626242(0x7f0e0902, float:1.8879715E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachVideo"
            r6 = 2131624373(0x7f0e01b5, float:1.8875924E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1a96:
            r1 = r18
            java.lang.String r2 = "NotificationMessageSDPhoto"
            r5 = 2131626235(0x7f0e08fb, float:1.88797E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachDestructingPhoto"
            r6 = 2131624350(0x7f0e019e, float:1.8875877E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1ab3:
            r1 = r18
            java.lang.String r2 = "NotificationMessagePhoto"
            r5 = 2131626231(0x7f0e08f7, float:1.8879692E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachPhoto"
            r6 = 2131624367(0x7f0e01af, float:1.8875912E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1ad0:
            r1 = r18
            java.lang.String r2 = "NotificationMessageNoText"
            r5 = 2131626230(0x7f0e08f6, float:1.887969E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "Message"
            r6 = 2131625921(0x7f0e07c1, float:1.8879064E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
        L_0x1aec:
            r16 = r5
            r5 = 0
            goto L_0x1b25
        L_0x1af0:
            r1 = r18
            java.lang.String r2 = "NotificationMessageText"
            r5 = 2131626241(0x7f0e0901, float:1.8879713E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            r5 = r12[r7]     // Catch:{ all -> 0x0420 }
            goto L_0x1aec
        L_0x1b0b:
            if (r2 == 0) goto L_0x1b21
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0420 }
            r2.<init>()     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "unhandled loc_key = "
            r2.append(r5)     // Catch:{ all -> 0x0420 }
            r2.append(r9)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0420 }
            org.telegram.messenger.FileLog.w(r2)     // Catch:{ all -> 0x0420 }
        L_0x1b21:
            r2 = 0
        L_0x1b22:
            r5 = 0
        L_0x1b23:
            r16 = 0
        L_0x1b25:
            if (r2 == 0) goto L_0x1bf1
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x1bf4 }
            r6.<init>()     // Catch:{ all -> 0x1bf4 }
            r6.id = r1     // Catch:{ all -> 0x1bf4 }
            r7 = r39
            r6.random_id = r7     // Catch:{ all -> 0x1bf4 }
            if (r16 == 0) goto L_0x1b37
            r1 = r16
            goto L_0x1b38
        L_0x1b37:
            r1 = r2
        L_0x1b38:
            r6.message = r1     // Catch:{ all -> 0x1bf4 }
            r7 = 1000(0x3e8, double:4.94E-321)
            long r7 = r45 / r7
            int r1 = (int) r7     // Catch:{ all -> 0x1bf4 }
            r6.date = r1     // Catch:{ all -> 0x1bf4 }
            if (r32 == 0) goto L_0x1b4a
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r1 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r6.action = r1     // Catch:{ all -> 0x0420 }
        L_0x1b4a:
            if (r33 == 0) goto L_0x1b53
            int r1 = r6.flags     // Catch:{ all -> 0x0420 }
            r7 = -2147483648(0xfffffffvar_, float:-0.0)
            r1 = r1 | r7
            r6.flags = r1     // Catch:{ all -> 0x0420 }
        L_0x1b53:
            r6.dialog_id = r3     // Catch:{ all -> 0x1bf4 }
            if (r38 == 0) goto L_0x1b65
            org.telegram.tgnet.TLRPC$TL_peerChannel r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r6.peer_id = r1     // Catch:{ all -> 0x0420 }
            r8 = r38
            r1.channel_id = r8     // Catch:{ all -> 0x0420 }
            r3 = r24
            goto L_0x1b80
        L_0x1b65:
            if (r24 == 0) goto L_0x1b73
            org.telegram.tgnet.TLRPC$TL_peerChat r1 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r6.peer_id = r1     // Catch:{ all -> 0x0420 }
            r3 = r24
            r1.chat_id = r3     // Catch:{ all -> 0x0420 }
            goto L_0x1b80
        L_0x1b73:
            r3 = r24
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1bf4 }
            r1.<init>()     // Catch:{ all -> 0x1bf4 }
            r6.peer_id = r1     // Catch:{ all -> 0x1bf4 }
            r8 = r23
            r1.user_id = r8     // Catch:{ all -> 0x1bf4 }
        L_0x1b80:
            int r1 = r6.flags     // Catch:{ all -> 0x1bf4 }
            r1 = r1 | 256(0x100, float:3.59E-43)
            r6.flags = r1     // Catch:{ all -> 0x1bf4 }
            if (r31 == 0) goto L_0x1b92
            org.telegram.tgnet.TLRPC$TL_peerChat r1 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r6.from_id = r1     // Catch:{ all -> 0x0420 }
            r1.chat_id = r3     // Catch:{ all -> 0x0420 }
            goto L_0x1bb2
        L_0x1b92:
            if (r30 == 0) goto L_0x1ba0
            org.telegram.tgnet.TLRPC$TL_peerChannel r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r6.from_id = r1     // Catch:{ all -> 0x0420 }
            r3 = r30
            r1.channel_id = r3     // Catch:{ all -> 0x0420 }
            goto L_0x1bb2
        L_0x1ba0:
            if (r25 == 0) goto L_0x1bae
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r6.from_id = r1     // Catch:{ all -> 0x0420 }
            r3 = r25
            r1.user_id = r3     // Catch:{ all -> 0x0420 }
            goto L_0x1bb2
        L_0x1bae:
            org.telegram.tgnet.TLRPC$Peer r1 = r6.peer_id     // Catch:{ all -> 0x1bf4 }
            r6.from_id = r1     // Catch:{ all -> 0x1bf4 }
        L_0x1bb2:
            if (r26 != 0) goto L_0x1bb9
            if (r32 == 0) goto L_0x1bb7
            goto L_0x1bb9
        L_0x1bb7:
            r1 = 0
            goto L_0x1bba
        L_0x1bb9:
            r1 = 1
        L_0x1bba:
            r6.mentioned = r1     // Catch:{ all -> 0x1bf4 }
            r1 = r27
            r6.silent = r1     // Catch:{ all -> 0x1bf4 }
            r13 = r22
            r6.from_scheduled = r13     // Catch:{ all -> 0x1bf4 }
            org.telegram.messenger.MessageObject r1 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x1bf4 }
            r19 = r1
            r20 = r29
            r21 = r6
            r22 = r2
            r23 = r34
            r24 = r37
            r25 = r5
            r26 = r36
            r27 = r35
            r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27)     // Catch:{ all -> 0x1bf4 }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x1bf4 }
            r2.<init>()     // Catch:{ all -> 0x1bf4 }
            r2.add(r1)     // Catch:{ all -> 0x1bf4 }
            org.telegram.messenger.NotificationsController r1 = org.telegram.messenger.NotificationsController.getInstance(r29)     // Catch:{ all -> 0x1bf4 }
            r3 = r43
            java.util.concurrent.CountDownLatch r4 = r3.countDownLatch     // Catch:{ all -> 0x1c1b }
            r5 = 1
            r1.processNewMessages(r2, r5, r5, r4)     // Catch:{ all -> 0x1c1b }
            r8 = 0
            goto L_0x1CLASSNAME
        L_0x1bf1:
            r3 = r43
            goto L_0x1CLASSNAME
        L_0x1bf4:
            r0 = move-exception
            r3 = r43
            goto L_0x1c1c
        L_0x1bf8:
            r0 = move-exception
            r3 = r43
            goto L_0x1CLASSNAME
        L_0x1bfc:
            r3 = r43
            goto L_0x1CLASSNAME
        L_0x1bff:
            r0 = move-exception
            r3 = r1
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r3 = r1
            r28 = r14
        L_0x1CLASSNAME:
            r29 = r15
        L_0x1CLASSNAME:
            r8 = 1
        L_0x1CLASSNAME:
            if (r8 == 0) goto L_0x1c0f
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1c1b }
            r1.countDown()     // Catch:{ all -> 0x1c1b }
        L_0x1c0f:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r29)     // Catch:{ all -> 0x1c1b }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r29)     // Catch:{ all -> 0x1c1b }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1c1b }
            goto L_0x1d48
        L_0x1c1b:
            r0 = move-exception
        L_0x1c1c:
            r1 = r0
            r4 = r9
            r14 = r28
            r15 = r29
            goto L_0x1cf3
        L_0x1CLASSNAME:
            r0 = move-exception
            r3 = r1
            r28 = r14
            r29 = r15
            r1 = r0
            r4 = r9
            goto L_0x1cf3
        L_0x1c2e:
            r0 = move-exception
            r3 = r1
            r28 = r7
        L_0x1CLASSNAME:
            r29 = r15
            goto L_0x1cef
        L_0x1CLASSNAME:
            r3 = r1
            r28 = r7
            r29 = r15
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1c4d }
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$bTzLYDDbdXvs-KFD45HxhDHO38c r2 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$bTzLYDDbdXvs-KFD45HxhDHO38c     // Catch:{ all -> 0x1c4d }
            r15 = r29
            r2.<init>(r15)     // Catch:{ all -> 0x1ce9 }
            r1.postRunnable(r2)     // Catch:{ all -> 0x1ce9 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1ce9 }
            r1.countDown()     // Catch:{ all -> 0x1ce9 }
            return
        L_0x1c4d:
            r0 = move-exception
            r15 = r29
            goto L_0x1cef
        L_0x1CLASSNAME:
            r3 = r1
            r28 = r7
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$RGrsR9FuVutCk9KN9DfF2lxxXic r1 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$RGrsR9FuVutCk9KN9DfF2lxxXic     // Catch:{ all -> 0x1ce9 }
            r1.<init>(r15)     // Catch:{ all -> 0x1ce9 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ all -> 0x1ce9 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1ce9 }
            r1.countDown()     // Catch:{ all -> 0x1ce9 }
            return
        L_0x1CLASSNAME:
            r3 = r1
            r28 = r7
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x1ce9 }
            r1.<init>()     // Catch:{ all -> 0x1ce9 }
            r2 = 0
            r1.popup = r2     // Catch:{ all -> 0x1ce9 }
            r2 = 2
            r1.flags = r2     // Catch:{ all -> 0x1ce9 }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r6 = r45 / r6
            int r2 = (int) r6     // Catch:{ all -> 0x1ce9 }
            r1.inbox_date = r2     // Catch:{ all -> 0x1ce9 }
            java.lang.String r2 = "message"
            java.lang.String r2 = r5.getString(r2)     // Catch:{ all -> 0x1ce9 }
            r1.message = r2     // Catch:{ all -> 0x1ce9 }
            java.lang.String r2 = "announcement"
            r1.type = r2     // Catch:{ all -> 0x1ce9 }
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ all -> 0x1ce9 }
            r2.<init>()     // Catch:{ all -> 0x1ce9 }
            r1.media = r2     // Catch:{ all -> 0x1ce9 }
            org.telegram.tgnet.TLRPC$TL_updates r2 = new org.telegram.tgnet.TLRPC$TL_updates     // Catch:{ all -> 0x1ce9 }
            r2.<init>()     // Catch:{ all -> 0x1ce9 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r4 = r2.updates     // Catch:{ all -> 0x1ce9 }
            r4.add(r1)     // Catch:{ all -> 0x1ce9 }
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1ce9 }
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$SOipFt3ZN2Z1O4A-LoGyMsSsmBw r4 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$SOipFt3ZN2Z1O4A-LoGyMsSsmBw     // Catch:{ all -> 0x1ce9 }
            r4.<init>(r15, r2)     // Catch:{ all -> 0x1ce9 }
            r1.postRunnable(r4)     // Catch:{ all -> 0x1ce9 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x1ce9 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1ce9 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1ce9 }
            r1.countDown()     // Catch:{ all -> 0x1ce9 }
            return
        L_0x1cac:
            r3 = r1
            r28 = r7
            java.lang.String r1 = "dc"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x1ce9 }
            java.lang.String r2 = "addr"
            java.lang.String r2 = r11.getString(r2)     // Catch:{ all -> 0x1ce9 }
            java.lang.String r4 = ":"
            java.lang.String[] r2 = r2.split(r4)     // Catch:{ all -> 0x1ce9 }
            int r4 = r2.length     // Catch:{ all -> 0x1ce9 }
            r5 = 2
            if (r4 == r5) goto L_0x1ccb
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1ce9 }
            r1.countDown()     // Catch:{ all -> 0x1ce9 }
            return
        L_0x1ccb:
            r4 = 0
            r4 = r2[r4]     // Catch:{ all -> 0x1ce9 }
            r5 = 1
            r2 = r2[r5]     // Catch:{ all -> 0x1ce9 }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ all -> 0x1ce9 }
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x1ce9 }
            r5.applyDatacenterAddress(r1, r4, r2)     // Catch:{ all -> 0x1ce9 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x1ce9 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1ce9 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1ce9 }
            r1.countDown()     // Catch:{ all -> 0x1ce9 }
            return
        L_0x1ce9:
            r0 = move-exception
            goto L_0x1cef
        L_0x1ceb:
            r0 = move-exception
            r3 = r1
            r28 = r7
        L_0x1cef:
            r1 = r0
            r4 = r9
            r14 = r28
        L_0x1cf3:
            r2 = -1
            goto L_0x1d10
        L_0x1cf5:
            r0 = move-exception
            r3 = r1
            r28 = r7
            r1 = r0
            r4 = r9
            r14 = r28
            r2 = -1
            goto L_0x1d0f
        L_0x1cff:
            r0 = move-exception
            r3 = r1
            r28 = r7
            r1 = r0
            r14 = r28
            r2 = -1
            r4 = 0
            goto L_0x1d0f
        L_0x1d09:
            r0 = move-exception
            r3 = r1
            r1 = r0
            r2 = -1
            r4 = 0
            r14 = 0
        L_0x1d0f:
            r15 = -1
        L_0x1d10:
            if (r15 == r2) goto L_0x1d22
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15)
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)
            r2.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r2 = r3.countDownLatch
            r2.countDown()
            goto L_0x1d25
        L_0x1d22:
            r43.onDecryptError()
        L_0x1d25:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x1d45
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
        L_0x1d45:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1d48:
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
