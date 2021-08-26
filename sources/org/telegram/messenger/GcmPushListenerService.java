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
                GcmPushListenerService.this.lambda$onMessageReceived$3$GcmPushListenerService(this.f$1, this.f$2);
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
    /* JADX WARNING: Code restructure failed: missing block: B:1006:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x01df, code lost:
        if (r2 == 0) goto L_0x1cda;
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
        r29 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x0364, code lost:
        r29 = r14;
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
        r14 = r29;
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
        r14 = r29;
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
        if (r1 == false) goto L_0x1c2a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x03ef, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:?, code lost:
        r1 = r11.optInt("chat_from_id", 0);
        r12 = r11.optInt("chat_from_broadcast_id", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x03f9, code lost:
        r30 = r15;
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
        r14 = r29;
        r15 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x042c, code lost:
        r26 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:275:0x0435, code lost:
        if (r11.has("silent") == false) goto L_0x0443;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x043e, code lost:
        if (r11.getInt("silent") == 0) goto L_0x0443;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0440, code lost:
        r27 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x0443, code lost:
        r27 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x044b, code lost:
        if (r5.has("loc_args") == false) goto L_0x046b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:?, code lost:
        r1 = r5.getJSONArray("loc_args");
        r5 = r1.length();
        r28 = r12;
        r12 = new java.lang.String[r5];
        r31 = r15;
        r15 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x045e, code lost:
        if (r15 >= r5) goto L_0x0469;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x0460, code lost:
        r12[r15] = r1.getString(r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x0466, code lost:
        r15 = r15 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x0469, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x046b, code lost:
        r28 = r12;
        r31 = r15;
        r1 = 0;
        r12 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:?, code lost:
        r5 = r12[r1];
        r1 = r11.has("edit_date");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x047f, code lost:
        if (r9.startsWith("CHAT_") == false) goto L_0x04b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x0485, code lost:
        if (org.telegram.messenger.UserObject.isReplyUser(r3) == false) goto L_0x049f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x0487, code lost:
        r5 = r5 + " @ " + r12[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x049f, code lost:
        if (r2 == 0) goto L_0x04a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x04a1, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x04a3, code lost:
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x04a7, code lost:
        r15 = false;
        r33 = false;
        r41 = r11;
        r11 = r5;
        r5 = r12[1];
        r32 = r41;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x04b8, code lost:
        if (r9.startsWith("PINNED_") == false) goto L_0x04c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x04ba, code lost:
        if (r2 == 0) goto L_0x04be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:311:0x04bc, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x04be, code lost:
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x04bf, code lost:
        r32 = r11;
        r11 = null;
        r15 = false;
        r33 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x04cc, code lost:
        if (r9.startsWith("CHANNEL_") == false) goto L_0x04d1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x04ce, code lost:
        r11 = null;
        r15 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x04d1, code lost:
        r11 = null;
        r15 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x04d3, code lost:
        r32 = false;
        r33 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x04d9, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x0502;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x04db, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x0502, code lost:
        r35 = r1;
        r34 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x050a, code lost:
        switch(r9.hashCode()) {
            case -2100047043: goto L_0x0a33;
            case -2091498420: goto L_0x0a28;
            case -2053872415: goto L_0x0a1d;
            case -2039746363: goto L_0x0a12;
            case -2023218804: goto L_0x0a07;
            case -1979538588: goto L_0x09fc;
            case -1979536003: goto L_0x09f1;
            case -1979535888: goto L_0x09e6;
            case -1969004705: goto L_0x09db;
            case -1946699248: goto L_0x09cf;
            case -1646640058: goto L_0x09c3;
            case -1528047021: goto L_0x09b7;
            case -1493579426: goto L_0x09ab;
            case -1482481933: goto L_0x099f;
            case -1480102982: goto L_0x0994;
            case -1478041834: goto L_0x0988;
            case -1474543101: goto L_0x097d;
            case -1465695932: goto L_0x0971;
            case -1374906292: goto L_0x0965;
            case -1372940586: goto L_0x0959;
            case -1264245338: goto L_0x094d;
            case -1236154001: goto L_0x0941;
            case -1236086700: goto L_0x0935;
            case -1236077786: goto L_0x0929;
            case -1235796237: goto L_0x091d;
            case -1235760759: goto L_0x0911;
            case -1235686303: goto L_0x0906;
            case -1198046100: goto L_0x08fb;
            case -1124254527: goto L_0x08ef;
            case -1085137927: goto L_0x08e3;
            case -1084856378: goto L_0x08d7;
            case -1084820900: goto L_0x08cb;
            case -1084746444: goto L_0x08bf;
            case -819729482: goto L_0x08b3;
            case -772141857: goto L_0x08a7;
            case -638310039: goto L_0x089b;
            case -590403924: goto L_0x088f;
            case -589196239: goto L_0x0883;
            case -589193654: goto L_0x0877;
            case -589193539: goto L_0x086b;
            case -440169325: goto L_0x085f;
            case -412748110: goto L_0x0853;
            case -228518075: goto L_0x0847;
            case -213586509: goto L_0x083b;
            case -115582002: goto L_0x082f;
            case -112621464: goto L_0x0823;
            case -108522133: goto L_0x0817;
            case -107572034: goto L_0x080c;
            case -40534265: goto L_0x0800;
            case 65254746: goto L_0x07f4;
            case 141040782: goto L_0x07e8;
            case 202550149: goto L_0x07dc;
            case 309993049: goto L_0x07d0;
            case 309995634: goto L_0x07c4;
            case 309995749: goto L_0x07b8;
            case 320532812: goto L_0x07ac;
            case 328933854: goto L_0x07a0;
            case 331340546: goto L_0x0794;
            case 342406591: goto L_0x0788;
            case 344816990: goto L_0x077c;
            case 346878138: goto L_0x0770;
            case 350376871: goto L_0x0764;
            case 608430149: goto L_0x0758;
            case 615714517: goto L_0x074d;
            case 715508879: goto L_0x0741;
            case 728985323: goto L_0x0735;
            case 731046471: goto L_0x0729;
            case 734545204: goto L_0x071d;
            case 802032552: goto L_0x0711;
            case 991498806: goto L_0x0705;
            case 1007364121: goto L_0x06f9;
            case 1019850010: goto L_0x06ed;
            case 1019917311: goto L_0x06e1;
            case 1019926225: goto L_0x06d5;
            case 1020207774: goto L_0x06c9;
            case 1020243252: goto L_0x06bd;
            case 1020317708: goto L_0x06b1;
            case 1060282259: goto L_0x06a5;
            case 1060349560: goto L_0x0699;
            case 1060358474: goto L_0x068d;
            case 1060640023: goto L_0x0681;
            case 1060675501: goto L_0x0675;
            case 1060749957: goto L_0x066a;
            case 1073049781: goto L_0x065e;
            case 1078101399: goto L_0x0652;
            case 1110103437: goto L_0x0646;
            case 1160762272: goto L_0x063a;
            case 1172918249: goto L_0x062e;
            case 1234591620: goto L_0x0622;
            case 1281128640: goto L_0x0616;
            case 1281131225: goto L_0x060a;
            case 1281131340: goto L_0x05fe;
            case 1310789062: goto L_0x05f3;
            case 1333118583: goto L_0x05e7;
            case 1361447897: goto L_0x05db;
            case 1498266155: goto L_0x05cf;
            case 1533804208: goto L_0x05c3;
            case 1540131626: goto L_0x05b7;
            case 1547988151: goto L_0x05ab;
            case 1561464595: goto L_0x059f;
            case 1563525743: goto L_0x0593;
            case 1567024476: goto L_0x0587;
            case 1810705077: goto L_0x057b;
            case 1815177512: goto L_0x056f;
            case 1954774321: goto L_0x0563;
            case 1963241394: goto L_0x0557;
            case 2014789757: goto L_0x054b;
            case 2022049433: goto L_0x053f;
            case 2034984710: goto L_0x0533;
            case 2048733346: goto L_0x0527;
            case 2099392181: goto L_0x051b;
            case 2140162142: goto L_0x050f;
            default: goto L_0x050d;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x0515, code lost:
        if (r9.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x0517, code lost:
        r1 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x0521, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x0523, code lost:
        r1 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x052d, code lost:
        if (r9.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x052f, code lost:
        r1 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x0539, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PLAYLIST") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x053b, code lost:
        r1 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x0545, code lost:
        if (r9.equals("PINNED_CONTACT") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x0547, code lost:
        r1 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x0551, code lost:
        if (r9.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x0553, code lost:
        r1 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x055d, code lost:
        if (r9.equals("LOCKED_MESSAGE") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x055f, code lost:
        r1 = 'j';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x0569, code lost:
        if (r9.equals("CHAT_MESSAGE_PLAYLIST") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x056b, code lost:
        r1 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x0575, code lost:
        if (r9.equals("CHANNEL_MESSAGES") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x0577, code lost:
        r1 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x0581, code lost:
        if (r9.equals("MESSAGE_INVOICE") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x0583, code lost:
        r1 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x058d, code lost:
        if (r9.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x058f, code lost:
        r1 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x0599, code lost:
        if (r9.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x059b, code lost:
        r1 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x05a5, code lost:
        if (r9.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x05a7, code lost:
        r1 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x05b1, code lost:
        if (r9.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x05b3, code lost:
        r1 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x05bd, code lost:
        if (r9.equals("MESSAGE_PLAYLIST") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x05bf, code lost:
        r1 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x05c9, code lost:
        if (r9.equals("MESSAGE_VIDEOS") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x05cb, code lost:
        r1 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x05d5, code lost:
        if (r9.equals("PHONE_CALL_MISSED") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x05d7, code lost:
        r1 = 'o';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x05e1, code lost:
        if (r9.equals("MESSAGE_PHOTOS") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x05e3, code lost:
        r1 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x05ed, code lost:
        if (r9.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x05ef, code lost:
        r1 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x05f9, code lost:
        if (r9.equals("MESSAGE_NOTEXT") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x05fb, code lost:
        r1 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x0604, code lost:
        if (r9.equals("MESSAGE_GIF") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x0606, code lost:
        r1 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x0610, code lost:
        if (r9.equals("MESSAGE_GEO") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x0612, code lost:
        r1 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x061c, code lost:
        if (r9.equals("MESSAGE_DOC") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x061e, code lost:
        r1 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:400:0x0628, code lost:
        if (r9.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x062a, code lost:
        r1 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0634, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x0636, code lost:
        r1 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x0640, code lost:
        if (r9.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x0642, code lost:
        r1 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x064c, code lost:
        if (r9.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x064e, code lost:
        r1 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0658, code lost:
        if (r9.equals("CHAT_TITLE_EDITED") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x065a, code lost:
        r1 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x0664, code lost:
        if (r9.equals("PINNED_NOTEXT") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x0666, code lost:
        r1 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x0670, code lost:
        if (r9.equals("MESSAGE_TEXT") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x0672, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x067b, code lost:
        if (r9.equals("MESSAGE_QUIZ") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x067d, code lost:
        r1 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x0687, code lost:
        if (r9.equals("MESSAGE_POLL") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x0689, code lost:
        r1 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x0693, code lost:
        if (r9.equals("MESSAGE_GAME") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x0695, code lost:
        r1 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x069f, code lost:
        if (r9.equals("MESSAGE_FWDS") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x06a1, code lost:
        r1 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x06ab, code lost:
        if (r9.equals("MESSAGE_DOCS") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x06ad, code lost:
        r1 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x06b7, code lost:
        if (r9.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x06b9, code lost:
        r1 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x06c3, code lost:
        if (r9.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x06c5, code lost:
        r1 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x06cf, code lost:
        if (r9.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x06d1, code lost:
        r1 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x06db, code lost:
        if (r9.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x06dd, code lost:
        r1 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x06e7, code lost:
        if (r9.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x06e9, code lost:
        r1 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x06f3, code lost:
        if (r9.equals("CHAT_MESSAGE_DOCS") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x06f5, code lost:
        r1 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x06ff, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x0701, code lost:
        r1 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x070b, code lost:
        if (r9.equals("PINNED_GEOLIVE") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:458:0x070d, code lost:
        r1 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:460:0x0717, code lost:
        if (r9.equals("MESSAGE_CONTACT") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x0719, code lost:
        r1 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x0723, code lost:
        if (r9.equals("PINNED_VIDEO") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x0725, code lost:
        r1 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x072f, code lost:
        if (r9.equals("PINNED_ROUND") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x0731, code lost:
        r1 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x073b, code lost:
        if (r9.equals("PINNED_PHOTO") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x073d, code lost:
        r1 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:472:0x0747, code lost:
        if (r9.equals("PINNED_AUDIO") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0749, code lost:
        r1 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0753, code lost:
        if (r9.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x0755, code lost:
        r1 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x075e, code lost:
        if (r9.equals("CHAT_VOICECHAT_INVITE_YOU") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0760, code lost:
        r1 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x076a, code lost:
        if (r9.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x076c, code lost:
        r1 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x0776, code lost:
        if (r9.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x0778, code lost:
        r1 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x0782, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x0784, code lost:
        r1 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x078e, code lost:
        if (r9.equals("CHAT_VOICECHAT_END") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0790, code lost:
        r1 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x079a, code lost:
        if (r9.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x079c, code lost:
        r1 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x07a6, code lost:
        if (r9.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x07a8, code lost:
        r1 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x07b2, code lost:
        if (r9.equals("MESSAGES") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x07b4, code lost:
        r1 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x07be, code lost:
        if (r9.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x07c0, code lost:
        r1 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x07ca, code lost:
        if (r9.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x07cc, code lost:
        r1 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x07d6, code lost:
        if (r9.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x07d8, code lost:
        r1 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x07e2, code lost:
        if (r9.equals("CHAT_VOICECHAT_INVITE") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x07e4, code lost:
        r1 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x07ee, code lost:
        if (r9.equals("CHAT_LEFT") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x07f0, code lost:
        r1 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x07fa, code lost:
        if (r9.equals("CHAT_ADD_YOU") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x07fc, code lost:
        r1 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0806, code lost:
        if (r9.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x0808, code lost:
        r1 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x0812, code lost:
        if (r9.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0814, code lost:
        r1 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x081d, code lost:
        if (r9.equals("AUTH_REGION") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x081f, code lost:
        r1 = 'i';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0829, code lost:
        if (r9.equals("CONTACT_JOINED") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x082b, code lost:
        r1 = 'g';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x0835, code lost:
        if (r9.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0837, code lost:
        r1 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x0841, code lost:
        if (r9.equals("ENCRYPTION_REQUEST") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x0843, code lost:
        r1 = 'k';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:538:0x084d, code lost:
        if (r9.equals("MESSAGE_GEOLIVE") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x084f, code lost:
        r1 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0859, code lost:
        if (r9.equals("CHAT_DELETE_YOU") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x085b, code lost:
        r1 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x0865, code lost:
        if (r9.equals("AUTH_UNKNOWN") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0867, code lost:
        r1 = 'h';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x0871, code lost:
        if (r9.equals("PINNED_GIF") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x0873, code lost:
        r1 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x087d, code lost:
        if (r9.equals("PINNED_GEO") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x087f, code lost:
        r1 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x0889, code lost:
        if (r9.equals("PINNED_DOC") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x088b, code lost:
        r1 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x0895, code lost:
        if (r9.equals("PINNED_GAME_SCORE") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x0897, code lost:
        r1 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x08a1, code lost:
        if (r9.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x08a3, code lost:
        r1 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x08ad, code lost:
        if (r9.equals("PHONE_CALL_REQUEST") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x08af, code lost:
        r1 = 'm';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x08b9, code lost:
        if (r9.equals("PINNED_STICKER") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x08bb, code lost:
        r1 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x08c5, code lost:
        if (r9.equals("PINNED_TEXT") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x08c7, code lost:
        r1 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x08d1, code lost:
        if (r9.equals("PINNED_QUIZ") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x08d3, code lost:
        r1 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x08dd, code lost:
        if (r9.equals("PINNED_POLL") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x08df, code lost:
        r1 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x08e9, code lost:
        if (r9.equals("PINNED_GAME") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x08eb, code lost:
        r1 = 'b';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x08f5, code lost:
        if (r9.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x08f7, code lost:
        r1 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x0901, code lost:
        if (r9.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x0903, code lost:
        r1 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x090c, code lost:
        if (r9.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x090e, code lost:
        r1 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x0917, code lost:
        if (r9.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x0919, code lost:
        r1 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x0923, code lost:
        if (r9.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0925, code lost:
        r1 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x092f, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x0931, code lost:
        r1 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x093b, code lost:
        if (r9.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x093d, code lost:
        r1 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0947, code lost:
        if (r9.equals("CHANNEL_MESSAGE_DOCS") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0949, code lost:
        r1 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x0953, code lost:
        if (r9.equals("PINNED_INVOICE") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0955, code lost:
        r1 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x095f, code lost:
        if (r9.equals("CHAT_RETURNED") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x0961, code lost:
        r1 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x096b, code lost:
        if (r9.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x096d, code lost:
        r1 = 'f';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x0977, code lost:
        if (r9.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x0979, code lost:
        r1 = 'l';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x0983, code lost:
        if (r9.equals("MESSAGE_VIDEO") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x0985, code lost:
        r1 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x098e, code lost:
        if (r9.equals("MESSAGE_ROUND") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0990, code lost:
        r1 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x099a, code lost:
        if (r9.equals("MESSAGE_PHOTO") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x099c, code lost:
        r1 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:625:0x09a5, code lost:
        if (r9.equals("MESSAGE_MUTED") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x09a7, code lost:
        r1 = 'n';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x09b1, code lost:
        if (r9.equals("MESSAGE_AUDIO") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x09b3, code lost:
        r1 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x09bd, code lost:
        if (r9.equals("CHAT_MESSAGES") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x09bf, code lost:
        r1 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x09c9, code lost:
        if (r9.equals("CHAT_VOICECHAT_START") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x09cb, code lost:
        r1 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x09d5, code lost:
        if (r9.equals("CHAT_JOINED") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x09d7, code lost:
        r1 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x09e1, code lost:
        if (r9.equals("CHAT_ADD_MEMBER") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x09e3, code lost:
        r1 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x09ec, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x09ee, code lost:
        r1 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x09f7, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x09f9, code lost:
        r1 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0a02, code lost:
        if (r9.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0a04, code lost:
        r1 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0a0d, code lost:
        if (r9.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0a0f, code lost:
        r1 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0a18, code lost:
        if (r9.equals("MESSAGE_STICKER") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0a1a, code lost:
        r1 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0a23, code lost:
        if (r9.equals("CHAT_CREATED") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0a25, code lost:
        r1 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x0a2e, code lost:
        if (r9.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0a30, code lost:
        r1 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0a39, code lost:
        if (r9.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x0a3b, code lost:
        r1 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0a3e, code lost:
        r1 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x0a3f, code lost:
        r18 = r7;
        r36 = r15;
        r37 = r11;
        r38 = r2;
        r39 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0a59, code lost:
        switch(r1) {
            case 0: goto L_0x1b1b;
            case 1: goto L_0x1b1b;
            case 2: goto L_0x1afb;
            case 3: goto L_0x1ade;
            case 4: goto L_0x1ac1;
            case 5: goto L_0x1aa4;
            case 6: goto L_0x1a86;
            case 7: goto L_0x1a6f;
            case 8: goto L_0x1a51;
            case 9: goto L_0x1a33;
            case 10: goto L_0x19d8;
            case 11: goto L_0x19ba;
            case 12: goto L_0x1997;
            case 13: goto L_0x1974;
            case 14: goto L_0x1951;
            case 15: goto L_0x1933;
            case 16: goto L_0x1915;
            case 17: goto L_0x18f7;
            case 18: goto L_0x18d4;
            case 19: goto L_0x18b5;
            case 20: goto L_0x18b5;
            case 21: goto L_0x1892;
            case 22: goto L_0x186c;
            case 23: goto L_0x1848;
            case 24: goto L_0x1825;
            case 25: goto L_0x1802;
            case 26: goto L_0x17dd;
            case 27: goto L_0x17c7;
            case 28: goto L_0x17a9;
            case 29: goto L_0x178b;
            case 30: goto L_0x176d;
            case 31: goto L_0x174f;
            case 32: goto L_0x1731;
            case 33: goto L_0x16d6;
            case 34: goto L_0x16b8;
            case 35: goto L_0x1695;
            case 36: goto L_0x1672;
            case 37: goto L_0x164f;
            case 38: goto L_0x1631;
            case 39: goto L_0x1613;
            case 40: goto L_0x15f5;
            case 41: goto L_0x15d7;
            case 42: goto L_0x15ad;
            case 43: goto L_0x1589;
            case 44: goto L_0x1565;
            case 45: goto L_0x1541;
            case 46: goto L_0x151b;
            case 47: goto L_0x1506;
            case 48: goto L_0x14e5;
            case 49: goto L_0x14c2;
            case 50: goto L_0x149f;
            case 51: goto L_0x147c;
            case 52: goto L_0x1459;
            case 53: goto L_0x1436;
            case 54: goto L_0x13bd;
            case 55: goto L_0x139a;
            case 56: goto L_0x1372;
            case 57: goto L_0x134a;
            case 58: goto L_0x1322;
            case 59: goto L_0x12ff;
            case 60: goto L_0x12dc;
            case 61: goto L_0x12b9;
            case 62: goto L_0x1291;
            case 63: goto L_0x126d;
            case 64: goto L_0x1245;
            case 65: goto L_0x122b;
            case 66: goto L_0x122b;
            case 67: goto L_0x1211;
            case 68: goto L_0x11f7;
            case 69: goto L_0x11d8;
            case 70: goto L_0x11be;
            case 71: goto L_0x119f;
            case 72: goto L_0x1185;
            case 73: goto L_0x116b;
            case 74: goto L_0x1151;
            case 75: goto L_0x1137;
            case 76: goto L_0x111d;
            case 77: goto L_0x1103;
            case 78: goto L_0x10e9;
            case 79: goto L_0x10be;
            case 80: goto L_0x1095;
            case 81: goto L_0x106c;
            case 82: goto L_0x1043;
            case 83: goto L_0x1018;
            case 84: goto L_0x0ffe;
            case 85: goto L_0x0fa9;
            case 86: goto L_0x0f5e;
            case 87: goto L_0x0var_;
            case 88: goto L_0x0ec8;
            case 89: goto L_0x0e7d;
            case 90: goto L_0x0e32;
            case 91: goto L_0x0d7b;
            case 92: goto L_0x0d30;
            case 93: goto L_0x0cdb;
            case 94: goto L_0x0CLASSNAME;
            case 95: goto L_0x0CLASSNAME;
            case 96: goto L_0x0bec;
            case 97: goto L_0x0ba6;
            case 98: goto L_0x0b5d;
            case 99: goto L_0x0b14;
            case 100: goto L_0x0acb;
            case 101: goto L_0x0a82;
            case 102: goto L_0x0a66;
            case 103: goto L_0x0a62;
            case 104: goto L_0x0a62;
            case 105: goto L_0x0a62;
            case 106: goto L_0x0a62;
            case 107: goto L_0x0a62;
            case 108: goto L_0x0a62;
            case 109: goto L_0x0a62;
            case 110: goto L_0x0a62;
            case 111: goto L_0x0a62;
            default: goto L_0x0a5c;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0a5c, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0a62, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:?, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
        r34 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
        r5 = true;
        r16 = null;
        r2 = r1;
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x0a84, code lost:
        if (r3 <= 0) goto L_0x0a9e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0a86, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:679:0x0a9e, code lost:
        if (r6 == false) goto L_0x0ab8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0aa0, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0ab8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0acd, code lost:
        if (r3 <= 0) goto L_0x0ae7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0acf, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:685:0x0ae7, code lost:
        if (r6 == false) goto L_0x0b01;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0ae9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x0b01, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0b16, code lost:
        if (r3 <= 0) goto L_0x0b30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0b18, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x0b30, code lost:
        if (r6 == false) goto L_0x0b4a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0b32, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0b4a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0b5f, code lost:
        if (r3 <= 0) goto L_0x0b79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0b61, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0b79, code lost:
        if (r6 == false) goto L_0x0b93;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0b7b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0b93, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0ba8, code lost:
        if (r3 <= 0) goto L_0x0bc1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0baa, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0bc1, code lost:
        if (r6 == false) goto L_0x0bda;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0bc3, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0bda, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0bee, code lost:
        if (r3 <= 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0bf0, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0CLASSNAME, code lost:
        if (r6 == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0CLASSNAME, code lost:
        r2 = r1;
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0CLASSNAME, code lost:
        if (r3 <= 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0c3a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0CLASSNAME, code lost:
        if (r6 == false) goto L_0x0c6f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0c6f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0CLASSNAME, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0c8a, code lost:
        if (r3 <= 0) goto L_0x0ca4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:0x0c8c, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0ca4, code lost:
        if (r6 == false) goto L_0x0cc3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0ca6, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0cc3, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0cdb, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0cdf, code lost:
        if (r3 <= 0) goto L_0x0cf9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:727:0x0ce1, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0cf9, code lost:
        if (r6 == false) goto L_0x0d18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0cfb, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0d18, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x0d30, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0d34, code lost:
        if (r3 <= 0) goto L_0x0d4e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0d36, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0d4e, code lost:
        if (r6 == false) goto L_0x0d68;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x0d50, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0d68, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0d7b, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0d7f, code lost:
        if (r3 <= 0) goto L_0x0db8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0d83, code lost:
        if (r12.length <= 1) goto L_0x0da5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0d8b, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x0da5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x0d8d, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x0da5, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x0db8, code lost:
        if (r6 == false) goto L_0x0dfb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x0dbc, code lost:
        if (r12.length <= 2) goto L_0x0de3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0dc4, code lost:
        if (android.text.TextUtils.isEmpty(r12[2]) != false) goto L_0x0de3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x0dc6, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x0de3, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:0x0dfd, code lost:
        if (r12.length <= 1) goto L_0x0e1f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x0e05, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x0e1f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x0e07, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x0e1f, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x0e32, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x0e36, code lost:
        if (r3 <= 0) goto L_0x0e50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x0e38, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x0e50, code lost:
        if (r6 == false) goto L_0x0e6a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x0e52, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x0e6a, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x0e7d, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:0x0e81, code lost:
        if (r3 <= 0) goto L_0x0e9b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x0e83, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x0e9b, code lost:
        if (r6 == false) goto L_0x0eb5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x0e9d, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x0eb5, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x0ec8, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x0ecc, code lost:
        if (r3 <= 0) goto L_0x0ee6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x0ece, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x0ee6, code lost:
        if (r6 == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x0ee8, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x0var_, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x0var_, code lost:
        if (r3 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x0var_, code lost:
        if (r6 == false) goto L_0x0f4b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x0f4b, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x0f5e, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x0var_, code lost:
        if (r3 <= 0) goto L_0x0f7c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x0f7c, code lost:
        if (r6 == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x0f7e, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x0fa9, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x0fad, code lost:
        if (r3 <= 0) goto L_0x0fc7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x0faf, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x0fc7, code lost:
        if (r6 == false) goto L_0x0fe6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x0fc9, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x0fe6, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x0ffe, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x1018, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x1043, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x106c, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x1095, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x10be, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x10e9, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x1103, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x111d, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x1137, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x1151, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x116b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x1185, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupEndedCall", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x119f, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x11be, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x11d8, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x11f7, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x1211, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x122b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x1245, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x126d, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r12[0], r12[1], r12[2], r12[3]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x1291, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x12b9, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x12dc, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x12ff, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x1322, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x134a, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("PollQuiz", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x1372, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x139a, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x13bd, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x13c1, code lost:
        if (r12.length <= 2) goto L_0x1403;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x13c9, code lost:
        if (android.text.TextUtils.isEmpty(r12[2]) != false) goto L_0x1403;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x13cb, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r12[0], r12[1], r12[2]);
        r5 = r12[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x1403, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r12[0], r12[1]);
        r5 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x1436, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x1459, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x147c, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x149f, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x14c2, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x14e5, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r12[0], r12[1], r12[2]);
        r5 = r12[2];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x1506, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x151b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x1541, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x1565, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x1589, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x15ad, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x15d7, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x15f5, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x1613, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x1631, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x164f, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x1672, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x1695, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x16b8, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x16d6, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x16da, code lost:
        if (r12.length <= 1) goto L_0x1717;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x16e2, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x1717;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x16e4, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r12[0], r12[1]);
        r5 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x1717, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x1731, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:856:0x174f, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x176d, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x178b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x17a9, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x17c7, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x17da, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:862:0x17dd, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:863:0x1802, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:864:0x1825, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:865:0x1848, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:866:0x186c, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x1892, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x18b5, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x18d4, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x18f7, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:871:0x1915, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x1933, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x1951, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x1974, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x1997, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x19ba, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x19d8, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x19dc, code lost:
        if (r12.length <= 1) goto L_0x1a19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x19e4, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x1a19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x19e6, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r12[0], r12[1]);
        r5 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x1a19, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x1a33, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x1a51, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:0x1a6f, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x1a86, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x1aa4, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x1ac1, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x1ade, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x1afb, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x1b17, code lost:
        r16 = r5;
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x1b1b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r12[0], r12[1]);
        r5 = r12[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x1b36, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1b4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x1b38, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x1b4d, code lost:
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x1b4e, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:897:0x1b4f, code lost:
        r16 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x1b51, code lost:
        if (r2 == null) goto L_0x1c1f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:?, code lost:
        r6 = new org.telegram.tgnet.TLRPC$TL_message();
        r6.id = r1;
        r6.random_id = r39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x1b5e, code lost:
        if (r16 == null) goto L_0x1b63;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x1b60, code lost:
        r1 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:0x1b63, code lost:
        r1 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:0x1b64, code lost:
        r6.message = r1;
        r6.date = (int) (r45 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x1b6d, code lost:
        if (r33 == false) goto L_0x1b76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:?, code lost:
        r6.action = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x1b76, code lost:
        if (r32 == false) goto L_0x1b7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:909:0x1b78, code lost:
        r6.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:?, code lost:
        r6.dialog_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:0x1b81, code lost:
        if (r38 == 0) goto L_0x1b91;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:?, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r6.peer_id = r1;
        r1.channel_id = r38;
        r3 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:0x1b91, code lost:
        if (r24 == 0) goto L_0x1b9f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x1b93, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r6.peer_id = r1;
        r3 = r24;
        r1.chat_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:917:0x1b9f, code lost:
        r3 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:?, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r6.peer_id = r1;
        r1.user_id = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:920:0x1bac, code lost:
        r6.flags |= 256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x1bb2, code lost:
        if (r31 == 0) goto L_0x1bbe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:?, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r6.from_id = r1;
        r1.chat_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x1bbe, code lost:
        if (r28 == 0) goto L_0x1bcc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x1bc0, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r6.from_id = r1;
        r1.channel_id = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x1bcc, code lost:
        if (r25 == 0) goto L_0x1bda;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x1bce, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r6.from_id = r1;
        r1.user_id = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:?, code lost:
        r6.from_id = r6.peer_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x1bde, code lost:
        if (r26 != false) goto L_0x1be5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x1be0, code lost:
        if (r33 == false) goto L_0x1be3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x1be3, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x1be5, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:935:0x1be6, code lost:
        r6.mentioned = r1;
        r6.silent = r27;
        r6.from_scheduled = r22;
        r19 = new org.telegram.messenger.MessageObject(r30, r6, r2, r34, r37, r5, r36, r32, r35);
        r2 = new java.util.ArrayList();
        r2.add(r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:0x1CLASSNAME, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:0x1CLASSNAME, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:?, code lost:
        org.telegram.messenger.NotificationsController.getInstance(r30).processNewMessages(r2, true, true, r3.countDownLatch);
        r8 = false;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x1c1f, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:0x1CLASSNAME, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:0x1CLASSNAME, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:0x1CLASSNAME, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:943:0x1CLASSNAME, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:944:0x1c2a, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:0x1c2d, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:946:0x1c2e, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x1CLASSNAME, code lost:
        r3 = r1;
        r29 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x1CLASSNAME, code lost:
        r30 = r15;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:0x1CLASSNAME, code lost:
        r8 = true;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x1CLASSNAME, code lost:
        if (r8 == false) goto L_0x1c3d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x1CLASSNAME, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x1c3d, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r30);
        org.telegram.tgnet.ConnectionsManager.getInstance(r30).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x1CLASSNAME, code lost:
        r0 = th;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x1CLASSNAME, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:0x1CLASSNAME, code lost:
        r3 = r1;
        r29 = r14;
        r30 = r15;
        r1 = r0;
        r4 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:957:0x1c5c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:0x1c5d, code lost:
        r3 = r1;
        r29 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x1CLASSNAME, code lost:
        r30 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x1CLASSNAME, code lost:
        r3 = r1;
        r29 = r7;
        r30 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:0x1c6d, code lost:
        r15 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:965:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.$$Lambda$GcmPushListenerService$jVvuwILdG7mFvHrODBdZ4swec(r15));
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:966:0x1c7a, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:967:0x1c7b, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:968:0x1c7c, code lost:
        r15 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:969:0x1CLASSNAME, code lost:
        r29 = r7;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.$$Lambda$GcmPushListenerService$QuXd6CaqKEryz6R8VHHhEnJAw(r15));
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:0x1CLASSNAME, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:971:0x1CLASSNAME, code lost:
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
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.$$Lambda$GcmPushListenerService$y7ADJS7XfMNFfehioCWcBUPm0lQ(r15, r2));
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:972:0x1cd9, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:0x1cda, code lost:
        r3 = r1;
        r29 = r7;
        r1 = r11.getInt("dc");
        r2 = r11.getString("addr").split(":");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:974:0x1cf1, code lost:
        if (r2.length == 2) goto L_0x1cf9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:975:0x1cf3, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:976:0x1cf8, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:977:0x1cf9, code lost:
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).applyDatacenterAddress(r1, r2[0], java.lang.Integer.parseInt(r2[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:978:0x1d16, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:979:0x1d17, code lost:
        r0 = th;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:992:0x1d40  */
    /* JADX WARNING: Removed duplicated region for block: B:993:0x1d50  */
    /* JADX WARNING: Removed duplicated region for block: B:996:0x1d57  */
    /* renamed from: lambda$onMessageReceived$3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$onMessageReceived$3$GcmPushListenerService(java.util.Map r44, long r45) {
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
            java.lang.Object r5 = r2.get(r5)     // Catch:{ all -> 0x1d37 }
            boolean r6 = r5 instanceof java.lang.String     // Catch:{ all -> 0x1d37 }
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
            goto L_0x1d3e
        L_0x002d:
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ all -> 0x1d37 }
            r6 = 8
            byte[] r5 = android.util.Base64.decode(r5, r6)     // Catch:{ all -> 0x1d37 }
            org.telegram.tgnet.NativeByteBuffer r7 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x1d37 }
            int r8 = r5.length     // Catch:{ all -> 0x1d37 }
            r7.<init>((int) r8)     // Catch:{ all -> 0x1d37 }
            r7.writeBytes((byte[]) r5)     // Catch:{ all -> 0x1d37 }
            r8 = 0
            r7.position(r8)     // Catch:{ all -> 0x1d37 }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1d37 }
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
            byte[] r9 = new byte[r6]     // Catch:{ all -> 0x1d37 }
            r10 = 1
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1d37 }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1d37 }
            boolean r11 = java.util.Arrays.equals(r11, r9)     // Catch:{ all -> 0x1d37 }
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
            byte[] r9 = new byte[r9]     // Catch:{ all -> 0x1d37 }
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1d37 }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1d37 }
            org.telegram.messenger.MessageKeyData r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13)     // Catch:{ all -> 0x1d37 }
            java.nio.ByteBuffer r14 = r7.buffer     // Catch:{ all -> 0x1d37 }
            byte[] r15 = r11.aesKey     // Catch:{ all -> 0x1d37 }
            byte[] r11 = r11.aesIv     // Catch:{ all -> 0x1d37 }
            r17 = 0
            r18 = 0
            r19 = 24
            int r5 = r5.length     // Catch:{ all -> 0x1d37 }
            int r20 = r5 + -24
            r16 = r11
            org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x1d37 }
            byte[] r21 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1d37 }
            r22 = 96
            r23 = 32
            java.nio.ByteBuffer r5 = r7.buffer     // Catch:{ all -> 0x1d37 }
            r25 = 24
            int r26 = r5.limit()     // Catch:{ all -> 0x1d37 }
            r24 = r5
            byte[] r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26)     // Catch:{ all -> 0x1d37 }
            boolean r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6)     // Catch:{ all -> 0x1d37 }
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
            int r5 = r7.readInt32(r10)     // Catch:{ all -> 0x1d37 }
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x1d37 }
            r7.readBytes(r5, r10)     // Catch:{ all -> 0x1d37 }
            java.lang.String r7 = new java.lang.String     // Catch:{ all -> 0x1d37 }
            r7.<init>(r5)     // Catch:{ all -> 0x1d37 }
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ all -> 0x1d2d }
            r5.<init>(r7)     // Catch:{ all -> 0x1d2d }
            java.lang.String r9 = "loc_key"
            boolean r9 = r5.has(r9)     // Catch:{ all -> 0x1d2d }
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
            java.lang.Object r11 = r5.get(r11)     // Catch:{ all -> 0x1d23 }
            boolean r11 = r11 instanceof org.json.JSONObject     // Catch:{ all -> 0x1d23 }
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
            org.json.JSONObject r11 = new org.json.JSONObject     // Catch:{ all -> 0x1d23 }
            r11.<init>()     // Catch:{ all -> 0x1d23 }
        L_0x0130:
            java.lang.String r14 = "user_id"
            boolean r14 = r5.has(r14)     // Catch:{ all -> 0x1d23 }
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
            boolean r15 = r14 instanceof java.lang.Integer     // Catch:{ all -> 0x1d23 }
            if (r15 == 0) goto L_0x015a
            java.lang.Integer r14 = (java.lang.Integer) r14     // Catch:{ all -> 0x0124 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0124 }
            goto L_0x0173
        L_0x015a:
            boolean r15 = r14 instanceof java.lang.String     // Catch:{ all -> 0x1d23 }
            if (r15 == 0) goto L_0x0169
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ all -> 0x0124 }
            java.lang.Integer r14 = org.telegram.messenger.Utilities.parseInt(r14)     // Catch:{ all -> 0x0124 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0124 }
            goto L_0x0173
        L_0x0169:
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1d23 }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x1d23 }
            int r14 = r14.getClientUserId()     // Catch:{ all -> 0x1d23 }
        L_0x0173:
            int r15 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1d23 }
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
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r15)     // Catch:{ all -> 0x1d19 }
            boolean r4 = r4.isClientActivated()     // Catch:{ all -> 0x1d19 }
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
            r2.get(r4)     // Catch:{ all -> 0x1d19 }
            int r2 = r9.hashCode()     // Catch:{ all -> 0x1d19 }
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
            if (r2 == 0) goto L_0x1cda
            if (r2 == r10) goto L_0x1CLASSNAME
            if (r2 == r13) goto L_0x1CLASSNAME
            if (r2 == r12) goto L_0x1CLASSNAME
            java.lang.String r2 = "channel_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1c5c }
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
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1c5c }
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
            r29 = r14
            goto L_0x0367
        L_0x0364:
            r29 = r14
            r7 = 0
        L_0x0367:
            java.lang.String r14 = "random_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1c2d }
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
            r14 = r29
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
            r14 = r29
            goto L_0x1d3e
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
            if (r1 == 0) goto L_0x1c2a
            java.lang.String r1 = "chat_from_id"
            r6 = 0
            int r1 = r11.optInt(r1, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r12 = "chat_from_broadcast_id"
            int r12 = r11.optInt(r12, r6)     // Catch:{ all -> 0x1CLASSNAME }
            r30 = r15
            java.lang.String r15 = "chat_from_group_id"
            int r15 = r11.optInt(r15, r6)     // Catch:{ all -> 0x1CLASSNAME }
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
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x1CLASSNAME }
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
            r14 = r29
            r15 = r30
            goto L_0x1d3e
        L_0x042c:
            r26 = 0
        L_0x042e:
            java.lang.String r1 = "silent"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x1CLASSNAME }
            if (r1 == 0) goto L_0x0443
            java.lang.String r1 = "silent"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0443
            r27 = 1
            goto L_0x0445
        L_0x0443:
            r27 = 0
        L_0x0445:
            java.lang.String r1 = "loc_args"
            boolean r1 = r5.has(r1)     // Catch:{ all -> 0x1CLASSNAME }
            if (r1 == 0) goto L_0x046b
            java.lang.String r1 = "loc_args"
            org.json.JSONArray r1 = r5.getJSONArray(r1)     // Catch:{ all -> 0x0420 }
            int r5 = r1.length()     // Catch:{ all -> 0x0420 }
            r28 = r12
            java.lang.String[] r12 = new java.lang.String[r5]     // Catch:{ all -> 0x0420 }
            r31 = r15
            r15 = 0
        L_0x045e:
            if (r15 >= r5) goto L_0x0469
            java.lang.String r32 = r1.getString(r15)     // Catch:{ all -> 0x0420 }
            r12[r15] = r32     // Catch:{ all -> 0x0420 }
            int r15 = r15 + 1
            goto L_0x045e
        L_0x0469:
            r1 = 0
            goto L_0x0471
        L_0x046b:
            r28 = r12
            r31 = r15
            r1 = 0
            r12 = 0
        L_0x0471:
            r5 = r12[r1]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "edit_date"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r11 = "CHAT_"
            boolean r11 = r9.startsWith(r11)     // Catch:{ all -> 0x1CLASSNAME }
            if (r11 == 0) goto L_0x04b2
            boolean r11 = org.telegram.messenger.UserObject.isReplyUser((long) r3)     // Catch:{ all -> 0x0420 }
            if (r11 == 0) goto L_0x049f
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x0420 }
            r11.<init>()     // Catch:{ all -> 0x0420 }
            r11.append(r5)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = " @ "
            r11.append(r5)     // Catch:{ all -> 0x0420 }
            r5 = 1
            r15 = r12[r5]     // Catch:{ all -> 0x0420 }
            r11.append(r15)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = r11.toString()     // Catch:{ all -> 0x0420 }
            goto L_0x04d1
        L_0x049f:
            if (r2 == 0) goto L_0x04a3
            r11 = 1
            goto L_0x04a4
        L_0x04a3:
            r11 = 0
        L_0x04a4:
            r15 = 1
            r32 = r12[r15]     // Catch:{ all -> 0x0420 }
            r15 = 0
            r33 = 0
            r41 = r11
            r11 = r5
            r5 = r32
            r32 = r41
            goto L_0x04d7
        L_0x04b2:
            java.lang.String r11 = "PINNED_"
            boolean r11 = r9.startsWith(r11)     // Catch:{ all -> 0x1CLASSNAME }
            if (r11 == 0) goto L_0x04c6
            if (r2 == 0) goto L_0x04be
            r11 = 1
            goto L_0x04bf
        L_0x04be:
            r11 = 0
        L_0x04bf:
            r32 = r11
            r11 = 0
            r15 = 0
            r33 = 1
            goto L_0x04d7
        L_0x04c6:
            java.lang.String r11 = "CHANNEL_"
            boolean r11 = r9.startsWith(r11)     // Catch:{ all -> 0x1CLASSNAME }
            if (r11 == 0) goto L_0x04d1
            r11 = 0
            r15 = 1
            goto L_0x04d3
        L_0x04d1:
            r11 = 0
            r15 = 0
        L_0x04d3:
            r32 = 0
            r33 = 0
        L_0x04d7:
            boolean r34 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1CLASSNAME }
            if (r34 == 0) goto L_0x0502
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
            goto L_0x0506
        L_0x0502:
            r35 = r1
            r34 = r5
        L_0x0506:
            int r1 = r9.hashCode()     // Catch:{ all -> 0x1CLASSNAME }
            switch(r1) {
                case -2100047043: goto L_0x0a33;
                case -2091498420: goto L_0x0a28;
                case -2053872415: goto L_0x0a1d;
                case -2039746363: goto L_0x0a12;
                case -2023218804: goto L_0x0a07;
                case -1979538588: goto L_0x09fc;
                case -1979536003: goto L_0x09f1;
                case -1979535888: goto L_0x09e6;
                case -1969004705: goto L_0x09db;
                case -1946699248: goto L_0x09cf;
                case -1646640058: goto L_0x09c3;
                case -1528047021: goto L_0x09b7;
                case -1493579426: goto L_0x09ab;
                case -1482481933: goto L_0x099f;
                case -1480102982: goto L_0x0994;
                case -1478041834: goto L_0x0988;
                case -1474543101: goto L_0x097d;
                case -1465695932: goto L_0x0971;
                case -1374906292: goto L_0x0965;
                case -1372940586: goto L_0x0959;
                case -1264245338: goto L_0x094d;
                case -1236154001: goto L_0x0941;
                case -1236086700: goto L_0x0935;
                case -1236077786: goto L_0x0929;
                case -1235796237: goto L_0x091d;
                case -1235760759: goto L_0x0911;
                case -1235686303: goto L_0x0906;
                case -1198046100: goto L_0x08fb;
                case -1124254527: goto L_0x08ef;
                case -1085137927: goto L_0x08e3;
                case -1084856378: goto L_0x08d7;
                case -1084820900: goto L_0x08cb;
                case -1084746444: goto L_0x08bf;
                case -819729482: goto L_0x08b3;
                case -772141857: goto L_0x08a7;
                case -638310039: goto L_0x089b;
                case -590403924: goto L_0x088f;
                case -589196239: goto L_0x0883;
                case -589193654: goto L_0x0877;
                case -589193539: goto L_0x086b;
                case -440169325: goto L_0x085f;
                case -412748110: goto L_0x0853;
                case -228518075: goto L_0x0847;
                case -213586509: goto L_0x083b;
                case -115582002: goto L_0x082f;
                case -112621464: goto L_0x0823;
                case -108522133: goto L_0x0817;
                case -107572034: goto L_0x080c;
                case -40534265: goto L_0x0800;
                case 65254746: goto L_0x07f4;
                case 141040782: goto L_0x07e8;
                case 202550149: goto L_0x07dc;
                case 309993049: goto L_0x07d0;
                case 309995634: goto L_0x07c4;
                case 309995749: goto L_0x07b8;
                case 320532812: goto L_0x07ac;
                case 328933854: goto L_0x07a0;
                case 331340546: goto L_0x0794;
                case 342406591: goto L_0x0788;
                case 344816990: goto L_0x077c;
                case 346878138: goto L_0x0770;
                case 350376871: goto L_0x0764;
                case 608430149: goto L_0x0758;
                case 615714517: goto L_0x074d;
                case 715508879: goto L_0x0741;
                case 728985323: goto L_0x0735;
                case 731046471: goto L_0x0729;
                case 734545204: goto L_0x071d;
                case 802032552: goto L_0x0711;
                case 991498806: goto L_0x0705;
                case 1007364121: goto L_0x06f9;
                case 1019850010: goto L_0x06ed;
                case 1019917311: goto L_0x06e1;
                case 1019926225: goto L_0x06d5;
                case 1020207774: goto L_0x06c9;
                case 1020243252: goto L_0x06bd;
                case 1020317708: goto L_0x06b1;
                case 1060282259: goto L_0x06a5;
                case 1060349560: goto L_0x0699;
                case 1060358474: goto L_0x068d;
                case 1060640023: goto L_0x0681;
                case 1060675501: goto L_0x0675;
                case 1060749957: goto L_0x066a;
                case 1073049781: goto L_0x065e;
                case 1078101399: goto L_0x0652;
                case 1110103437: goto L_0x0646;
                case 1160762272: goto L_0x063a;
                case 1172918249: goto L_0x062e;
                case 1234591620: goto L_0x0622;
                case 1281128640: goto L_0x0616;
                case 1281131225: goto L_0x060a;
                case 1281131340: goto L_0x05fe;
                case 1310789062: goto L_0x05f3;
                case 1333118583: goto L_0x05e7;
                case 1361447897: goto L_0x05db;
                case 1498266155: goto L_0x05cf;
                case 1533804208: goto L_0x05c3;
                case 1540131626: goto L_0x05b7;
                case 1547988151: goto L_0x05ab;
                case 1561464595: goto L_0x059f;
                case 1563525743: goto L_0x0593;
                case 1567024476: goto L_0x0587;
                case 1810705077: goto L_0x057b;
                case 1815177512: goto L_0x056f;
                case 1954774321: goto L_0x0563;
                case 1963241394: goto L_0x0557;
                case 2014789757: goto L_0x054b;
                case 2022049433: goto L_0x053f;
                case 2034984710: goto L_0x0533;
                case 2048733346: goto L_0x0527;
                case 2099392181: goto L_0x051b;
                case 2140162142: goto L_0x050f;
                default: goto L_0x050d;
            }
        L_0x050d:
            goto L_0x0a3e
        L_0x050f:
            java.lang.String r1 = "CHAT_MESSAGE_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 60
            goto L_0x0a3f
        L_0x051b:
            java.lang.String r1 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 43
            goto L_0x0a3f
        L_0x0527:
            java.lang.String r1 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 28
            goto L_0x0a3f
        L_0x0533:
            java.lang.String r1 = "CHANNEL_MESSAGE_PLAYLIST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 45
            goto L_0x0a3f
        L_0x053f:
            java.lang.String r1 = "PINNED_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 93
            goto L_0x0a3f
        L_0x054b:
            java.lang.String r1 = "CHAT_PHOTO_EDITED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 68
            goto L_0x0a3f
        L_0x0557:
            java.lang.String r1 = "LOCKED_MESSAGE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 106(0x6a, float:1.49E-43)
            goto L_0x0a3f
        L_0x0563:
            java.lang.String r1 = "CHAT_MESSAGE_PLAYLIST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 82
            goto L_0x0a3f
        L_0x056f:
            java.lang.String r1 = "CHANNEL_MESSAGES"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 47
            goto L_0x0a3f
        L_0x057b:
            java.lang.String r1 = "MESSAGE_INVOICE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 21
            goto L_0x0a3f
        L_0x0587:
            java.lang.String r1 = "CHAT_MESSAGE_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 51
            goto L_0x0a3f
        L_0x0593:
            java.lang.String r1 = "CHAT_MESSAGE_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 52
            goto L_0x0a3f
        L_0x059f:
            java.lang.String r1 = "CHAT_MESSAGE_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 50
            goto L_0x0a3f
        L_0x05ab:
            java.lang.String r1 = "CHAT_MESSAGE_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 55
            goto L_0x0a3f
        L_0x05b7:
            java.lang.String r1 = "MESSAGE_PLAYLIST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 25
            goto L_0x0a3f
        L_0x05c3:
            java.lang.String r1 = "MESSAGE_VIDEOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 24
            goto L_0x0a3f
        L_0x05cf:
            java.lang.String r1 = "PHONE_CALL_MISSED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 111(0x6f, float:1.56E-43)
            goto L_0x0a3f
        L_0x05db:
            java.lang.String r1 = "MESSAGE_PHOTOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 23
            goto L_0x0a3f
        L_0x05e7:
            java.lang.String r1 = "CHAT_MESSAGE_VIDEOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 81
            goto L_0x0a3f
        L_0x05f3:
            java.lang.String r1 = "MESSAGE_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 2
            goto L_0x0a3f
        L_0x05fe:
            java.lang.String r1 = "MESSAGE_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 17
            goto L_0x0a3f
        L_0x060a:
            java.lang.String r1 = "MESSAGE_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 15
            goto L_0x0a3f
        L_0x0616:
            java.lang.String r1 = "MESSAGE_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 9
            goto L_0x0a3f
        L_0x0622:
            java.lang.String r1 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 63
            goto L_0x0a3f
        L_0x062e:
            java.lang.String r1 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 39
            goto L_0x0a3f
        L_0x063a:
            java.lang.String r1 = "CHAT_MESSAGE_PHOTOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 80
            goto L_0x0a3f
        L_0x0646:
            java.lang.String r1 = "CHAT_MESSAGE_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 49
            goto L_0x0a3f
        L_0x0652:
            java.lang.String r1 = "CHAT_TITLE_EDITED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 67
            goto L_0x0a3f
        L_0x065e:
            java.lang.String r1 = "PINNED_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 86
            goto L_0x0a3f
        L_0x066a:
            java.lang.String r1 = "MESSAGE_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 0
            goto L_0x0a3f
        L_0x0675:
            java.lang.String r1 = "MESSAGE_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 13
            goto L_0x0a3f
        L_0x0681:
            java.lang.String r1 = "MESSAGE_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 14
            goto L_0x0a3f
        L_0x068d:
            java.lang.String r1 = "MESSAGE_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 18
            goto L_0x0a3f
        L_0x0699:
            java.lang.String r1 = "MESSAGE_FWDS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 22
            goto L_0x0a3f
        L_0x06a5:
            java.lang.String r1 = "MESSAGE_DOCS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 26
            goto L_0x0a3f
        L_0x06b1:
            java.lang.String r1 = "CHAT_MESSAGE_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 48
            goto L_0x0a3f
        L_0x06bd:
            java.lang.String r1 = "CHAT_MESSAGE_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 57
            goto L_0x0a3f
        L_0x06c9:
            java.lang.String r1 = "CHAT_MESSAGE_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 58
            goto L_0x0a3f
        L_0x06d5:
            java.lang.String r1 = "CHAT_MESSAGE_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 62
            goto L_0x0a3f
        L_0x06e1:
            java.lang.String r1 = "CHAT_MESSAGE_FWDS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 79
            goto L_0x0a3f
        L_0x06ed:
            java.lang.String r1 = "CHAT_MESSAGE_DOCS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 83
            goto L_0x0a3f
        L_0x06f9:
            java.lang.String r1 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 20
            goto L_0x0a3f
        L_0x0705:
            java.lang.String r1 = "PINNED_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 97
            goto L_0x0a3f
        L_0x0711:
            java.lang.String r1 = "MESSAGE_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 12
            goto L_0x0a3f
        L_0x071d:
            java.lang.String r1 = "PINNED_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 88
            goto L_0x0a3f
        L_0x0729:
            java.lang.String r1 = "PINNED_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 89
            goto L_0x0a3f
        L_0x0735:
            java.lang.String r1 = "PINNED_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 87
            goto L_0x0a3f
        L_0x0741:
            java.lang.String r1 = "PINNED_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 92
            goto L_0x0a3f
        L_0x074d:
            java.lang.String r1 = "MESSAGE_PHOTO_SECRET"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 4
            goto L_0x0a3f
        L_0x0758:
            java.lang.String r1 = "CHAT_VOICECHAT_INVITE_YOU"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 73
            goto L_0x0a3f
        L_0x0764:
            java.lang.String r1 = "CHANNEL_MESSAGE_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 30
            goto L_0x0a3f
        L_0x0770:
            java.lang.String r1 = "CHANNEL_MESSAGE_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 31
            goto L_0x0a3f
        L_0x077c:
            java.lang.String r1 = "CHANNEL_MESSAGE_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 29
            goto L_0x0a3f
        L_0x0788:
            java.lang.String r1 = "CHAT_VOICECHAT_END"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 72
            goto L_0x0a3f
        L_0x0794:
            java.lang.String r1 = "CHANNEL_MESSAGE_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 34
            goto L_0x0a3f
        L_0x07a0:
            java.lang.String r1 = "CHAT_MESSAGE_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 54
            goto L_0x0a3f
        L_0x07ac:
            java.lang.String r1 = "MESSAGES"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 27
            goto L_0x0a3f
        L_0x07b8:
            java.lang.String r1 = "CHAT_MESSAGE_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 61
            goto L_0x0a3f
        L_0x07c4:
            java.lang.String r1 = "CHAT_MESSAGE_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 59
            goto L_0x0a3f
        L_0x07d0:
            java.lang.String r1 = "CHAT_MESSAGE_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 53
            goto L_0x0a3f
        L_0x07dc:
            java.lang.String r1 = "CHAT_VOICECHAT_INVITE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 71
            goto L_0x0a3f
        L_0x07e8:
            java.lang.String r1 = "CHAT_LEFT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 76
            goto L_0x0a3f
        L_0x07f4:
            java.lang.String r1 = "CHAT_ADD_YOU"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 66
            goto L_0x0a3f
        L_0x0800:
            java.lang.String r1 = "CHAT_DELETE_MEMBER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 74
            goto L_0x0a3f
        L_0x080c:
            java.lang.String r1 = "MESSAGE_SCREENSHOT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 7
            goto L_0x0a3f
        L_0x0817:
            java.lang.String r1 = "AUTH_REGION"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 105(0x69, float:1.47E-43)
            goto L_0x0a3f
        L_0x0823:
            java.lang.String r1 = "CONTACT_JOINED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 103(0x67, float:1.44E-43)
            goto L_0x0a3f
        L_0x082f:
            java.lang.String r1 = "CHAT_MESSAGE_INVOICE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 64
            goto L_0x0a3f
        L_0x083b:
            java.lang.String r1 = "ENCRYPTION_REQUEST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 107(0x6b, float:1.5E-43)
            goto L_0x0a3f
        L_0x0847:
            java.lang.String r1 = "MESSAGE_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 16
            goto L_0x0a3f
        L_0x0853:
            java.lang.String r1 = "CHAT_DELETE_YOU"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 75
            goto L_0x0a3f
        L_0x085f:
            java.lang.String r1 = "AUTH_UNKNOWN"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 104(0x68, float:1.46E-43)
            goto L_0x0a3f
        L_0x086b:
            java.lang.String r1 = "PINNED_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 101(0x65, float:1.42E-43)
            goto L_0x0a3f
        L_0x0877:
            java.lang.String r1 = "PINNED_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 96
            goto L_0x0a3f
        L_0x0883:
            java.lang.String r1 = "PINNED_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 90
            goto L_0x0a3f
        L_0x088f:
            java.lang.String r1 = "PINNED_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 99
            goto L_0x0a3f
        L_0x089b:
            java.lang.String r1 = "CHANNEL_MESSAGE_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 33
            goto L_0x0a3f
        L_0x08a7:
            java.lang.String r1 = "PHONE_CALL_REQUEST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 109(0x6d, float:1.53E-43)
            goto L_0x0a3f
        L_0x08b3:
            java.lang.String r1 = "PINNED_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 91
            goto L_0x0a3f
        L_0x08bf:
            java.lang.String r1 = "PINNED_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 85
            goto L_0x0a3f
        L_0x08cb:
            java.lang.String r1 = "PINNED_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 94
            goto L_0x0a3f
        L_0x08d7:
            java.lang.String r1 = "PINNED_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 95
            goto L_0x0a3f
        L_0x08e3:
            java.lang.String r1 = "PINNED_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 98
            goto L_0x0a3f
        L_0x08ef:
            java.lang.String r1 = "CHAT_MESSAGE_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 56
            goto L_0x0a3f
        L_0x08fb:
            java.lang.String r1 = "MESSAGE_VIDEO_SECRET"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 6
            goto L_0x0a3f
        L_0x0906:
            java.lang.String r1 = "CHANNEL_MESSAGE_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 1
            goto L_0x0a3f
        L_0x0911:
            java.lang.String r1 = "CHANNEL_MESSAGE_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 36
            goto L_0x0a3f
        L_0x091d:
            java.lang.String r1 = "CHANNEL_MESSAGE_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 37
            goto L_0x0a3f
        L_0x0929:
            java.lang.String r1 = "CHANNEL_MESSAGE_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 41
            goto L_0x0a3f
        L_0x0935:
            java.lang.String r1 = "CHANNEL_MESSAGE_FWDS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 42
            goto L_0x0a3f
        L_0x0941:
            java.lang.String r1 = "CHANNEL_MESSAGE_DOCS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 46
            goto L_0x0a3f
        L_0x094d:
            java.lang.String r1 = "PINNED_INVOICE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 100
            goto L_0x0a3f
        L_0x0959:
            java.lang.String r1 = "CHAT_RETURNED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 77
            goto L_0x0a3f
        L_0x0965:
            java.lang.String r1 = "ENCRYPTED_MESSAGE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 102(0x66, float:1.43E-43)
            goto L_0x0a3f
        L_0x0971:
            java.lang.String r1 = "ENCRYPTION_ACCEPT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 108(0x6c, float:1.51E-43)
            goto L_0x0a3f
        L_0x097d:
            java.lang.String r1 = "MESSAGE_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 5
            goto L_0x0a3f
        L_0x0988:
            java.lang.String r1 = "MESSAGE_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 8
            goto L_0x0a3f
        L_0x0994:
            java.lang.String r1 = "MESSAGE_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 3
            goto L_0x0a3f
        L_0x099f:
            java.lang.String r1 = "MESSAGE_MUTED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 110(0x6e, float:1.54E-43)
            goto L_0x0a3f
        L_0x09ab:
            java.lang.String r1 = "MESSAGE_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 11
            goto L_0x0a3f
        L_0x09b7:
            java.lang.String r1 = "CHAT_MESSAGES"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 84
            goto L_0x0a3f
        L_0x09c3:
            java.lang.String r1 = "CHAT_VOICECHAT_START"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 70
            goto L_0x0a3f
        L_0x09cf:
            java.lang.String r1 = "CHAT_JOINED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 78
            goto L_0x0a3f
        L_0x09db:
            java.lang.String r1 = "CHAT_ADD_MEMBER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 69
            goto L_0x0a3f
        L_0x09e6:
            java.lang.String r1 = "CHANNEL_MESSAGE_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 40
            goto L_0x0a3f
        L_0x09f1:
            java.lang.String r1 = "CHANNEL_MESSAGE_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 38
            goto L_0x0a3f
        L_0x09fc:
            java.lang.String r1 = "CHANNEL_MESSAGE_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 32
            goto L_0x0a3f
        L_0x0a07:
            java.lang.String r1 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 44
            goto L_0x0a3f
        L_0x0a12:
            java.lang.String r1 = "MESSAGE_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 10
            goto L_0x0a3f
        L_0x0a1d:
            java.lang.String r1 = "CHAT_CREATED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 65
            goto L_0x0a3f
        L_0x0a28:
            java.lang.String r1 = "CHANNEL_MESSAGE_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 35
            goto L_0x0a3f
        L_0x0a33:
            java.lang.String r1 = "MESSAGE_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x0420 }
            if (r1 == 0) goto L_0x0a3e
            r1 = 19
            goto L_0x0a3f
        L_0x0a3e:
            r1 = -1
        L_0x0a3f:
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
                case 0: goto L_0x1b1b;
                case 1: goto L_0x1b1b;
                case 2: goto L_0x1afb;
                case 3: goto L_0x1ade;
                case 4: goto L_0x1ac1;
                case 5: goto L_0x1aa4;
                case 6: goto L_0x1a86;
                case 7: goto L_0x1a6f;
                case 8: goto L_0x1a51;
                case 9: goto L_0x1a33;
                case 10: goto L_0x19d8;
                case 11: goto L_0x19ba;
                case 12: goto L_0x1997;
                case 13: goto L_0x1974;
                case 14: goto L_0x1951;
                case 15: goto L_0x1933;
                case 16: goto L_0x1915;
                case 17: goto L_0x18f7;
                case 18: goto L_0x18d4;
                case 19: goto L_0x18b5;
                case 20: goto L_0x18b5;
                case 21: goto L_0x1892;
                case 22: goto L_0x186c;
                case 23: goto L_0x1848;
                case 24: goto L_0x1825;
                case 25: goto L_0x1802;
                case 26: goto L_0x17dd;
                case 27: goto L_0x17c7;
                case 28: goto L_0x17a9;
                case 29: goto L_0x178b;
                case 30: goto L_0x176d;
                case 31: goto L_0x174f;
                case 32: goto L_0x1731;
                case 33: goto L_0x16d6;
                case 34: goto L_0x16b8;
                case 35: goto L_0x1695;
                case 36: goto L_0x1672;
                case 37: goto L_0x164f;
                case 38: goto L_0x1631;
                case 39: goto L_0x1613;
                case 40: goto L_0x15f5;
                case 41: goto L_0x15d7;
                case 42: goto L_0x15ad;
                case 43: goto L_0x1589;
                case 44: goto L_0x1565;
                case 45: goto L_0x1541;
                case 46: goto L_0x151b;
                case 47: goto L_0x1506;
                case 48: goto L_0x14e5;
                case 49: goto L_0x14c2;
                case 50: goto L_0x149f;
                case 51: goto L_0x147c;
                case 52: goto L_0x1459;
                case 53: goto L_0x1436;
                case 54: goto L_0x13bd;
                case 55: goto L_0x139a;
                case 56: goto L_0x1372;
                case 57: goto L_0x134a;
                case 58: goto L_0x1322;
                case 59: goto L_0x12ff;
                case 60: goto L_0x12dc;
                case 61: goto L_0x12b9;
                case 62: goto L_0x1291;
                case 63: goto L_0x126d;
                case 64: goto L_0x1245;
                case 65: goto L_0x122b;
                case 66: goto L_0x122b;
                case 67: goto L_0x1211;
                case 68: goto L_0x11f7;
                case 69: goto L_0x11d8;
                case 70: goto L_0x11be;
                case 71: goto L_0x119f;
                case 72: goto L_0x1185;
                case 73: goto L_0x116b;
                case 74: goto L_0x1151;
                case 75: goto L_0x1137;
                case 76: goto L_0x111d;
                case 77: goto L_0x1103;
                case 78: goto L_0x10e9;
                case 79: goto L_0x10be;
                case 80: goto L_0x1095;
                case 81: goto L_0x106c;
                case 82: goto L_0x1043;
                case 83: goto L_0x1018;
                case 84: goto L_0x0ffe;
                case 85: goto L_0x0fa9;
                case 86: goto L_0x0f5e;
                case 87: goto L_0x0var_;
                case 88: goto L_0x0ec8;
                case 89: goto L_0x0e7d;
                case 90: goto L_0x0e32;
                case 91: goto L_0x0d7b;
                case 92: goto L_0x0d30;
                case 93: goto L_0x0cdb;
                case 94: goto L_0x0CLASSNAME;
                case 95: goto L_0x0CLASSNAME;
                case 96: goto L_0x0bec;
                case 97: goto L_0x0ba6;
                case 98: goto L_0x0b5d;
                case 99: goto L_0x0b14;
                case 100: goto L_0x0acb;
                case 101: goto L_0x0a82;
                case 102: goto L_0x0a66;
                case 103: goto L_0x0a62;
                case 104: goto L_0x0a62;
                case 105: goto L_0x0a62;
                case 106: goto L_0x0a62;
                case 107: goto L_0x0a62;
                case 108: goto L_0x0a62;
                case 109: goto L_0x0a62;
                case 110: goto L_0x0a62;
                case 111: goto L_0x0a62;
                default: goto L_0x0a5c;
            }
        L_0x0a5c:
            r1 = r18
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b36
        L_0x0a62:
            r1 = r18
            goto L_0x1b4d
        L_0x0a66:
            java.lang.String r1 = "YouHaveNewMessage"
            r2 = 2131628364(0x7f0e114c, float:1.8884019E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = "SecretChatName"
            r5 = 2131627401(0x7f0e0d89, float:1.8882065E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r5)     // Catch:{ all -> 0x0420 }
            r34 = r2
            r5 = 1
            r16 = 0
            r2 = r1
            r1 = r18
            goto L_0x1b51
        L_0x0a82:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0a9e
            java.lang.String r1 = "NotificationActionPinnedGifUser"
            r2 = 2131626412(0x7f0e09ac, float:1.888006E38)
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
        L_0x0a9e:
            if (r6 == 0) goto L_0x0ab8
            java.lang.String r1 = "NotificationActionPinnedGif"
            r2 = 2131626410(0x7f0e09aa, float:1.8880055E38)
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
        L_0x0ab8:
            java.lang.String r1 = "NotificationActionPinnedGifChannel"
            r2 = 2131626411(0x7f0e09ab, float:1.8880057E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0420 }
            r6[r5] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0acb:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0ae7
            java.lang.String r1 = "NotificationActionPinnedInvoiceUser"
            r2 = 2131626415(0x7f0e09af, float:1.8880066E38)
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
        L_0x0ae7:
            if (r6 == 0) goto L_0x0b01
            java.lang.String r1 = "NotificationActionPinnedInvoice"
            r2 = 2131626413(0x7f0e09ad, float:1.8880061E38)
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
        L_0x0b01:
            java.lang.String r1 = "NotificationActionPinnedInvoiceChannel"
            r2 = 2131626414(0x7f0e09ae, float:1.8880064E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0420 }
            r6[r5] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0b14:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0b30
            java.lang.String r1 = "NotificationActionPinnedGameScoreUser"
            r2 = 2131626402(0x7f0e09a2, float:1.888004E38)
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
        L_0x0b30:
            if (r6 == 0) goto L_0x0b4a
            java.lang.String r1 = "NotificationActionPinnedGameScore"
            r2 = 2131626400(0x7f0e09a0, float:1.8880035E38)
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
        L_0x0b4a:
            java.lang.String r1 = "NotificationActionPinnedGameScoreChannel"
            r2 = 2131626401(0x7f0e09a1, float:1.8880037E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0420 }
            r6[r5] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0b5d:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0b79
            java.lang.String r1 = "NotificationActionPinnedGameUser"
            r2 = 2131626403(0x7f0e09a3, float:1.8880041E38)
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
        L_0x0b79:
            if (r6 == 0) goto L_0x0b93
            java.lang.String r1 = "NotificationActionPinnedGame"
            r2 = 2131626398(0x7f0e099e, float:1.8880031E38)
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
        L_0x0b93:
            java.lang.String r1 = "NotificationActionPinnedGameChannel"
            r2 = 2131626399(0x7f0e099f, float:1.8880033E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0420 }
            r6[r5] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0ba6:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0bc1
            java.lang.String r1 = "NotificationActionPinnedGeoLiveUser"
            r2 = 2131626408(0x7f0e09a8, float:1.8880051E38)
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
        L_0x0bc1:
            if (r6 == 0) goto L_0x0bda
            java.lang.String r1 = "NotificationActionPinnedGeoLive"
            r2 = 2131626406(0x7f0e09a6, float:1.8880047E38)
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
        L_0x0bda:
            java.lang.String r1 = "NotificationActionPinnedGeoLiveChannel"
            r2 = 2131626407(0x7f0e09a7, float:1.888005E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0420 }
            r6[r5] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x0CLASSNAME
        L_0x0bec:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGeoUser"
            r2 = 2131626409(0x7f0e09a9, float:1.8880053E38)
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
            if (r6 == 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGeo"
            r2 = 2131626404(0x7f0e09a4, float:1.8880043E38)
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
            r2 = 2131626405(0x7f0e09a5, float:1.8880045E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x0420 }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x0420 }
            r6[r5] = r7     // Catch:{ all -> 0x0420 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x0420 }
        L_0x0CLASSNAME:
            r2 = r1
            r1 = r18
            goto L_0x1b4e
        L_0x0CLASSNAME:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedPollUser"
            r2 = 2131626427(0x7f0e09bb, float:1.888009E38)
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
            if (r6 == 0) goto L_0x0c6f
            java.lang.String r1 = "NotificationActionPinnedPoll2"
            r2 = 2131626425(0x7f0e09b9, float:1.8880086E38)
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
        L_0x0c6f:
            java.lang.String r1 = "NotificationActionPinnedPollChannel2"
            r2 = 2131626426(0x7f0e09ba, float:1.8880088E38)
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
            if (r2 <= 0) goto L_0x0ca4
            java.lang.String r2 = "NotificationActionPinnedQuizUser"
            r5 = 2131626430(0x7f0e09be, float:1.8880096E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0ca4:
            if (r6 == 0) goto L_0x0cc3
            java.lang.String r2 = "NotificationActionPinnedQuiz2"
            r5 = 2131626428(0x7f0e09bc, float:1.8880092E38)
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
            goto L_0x1b4e
        L_0x0cc3:
            java.lang.String r2 = "NotificationActionPinnedQuizChannel2"
            r5 = 2131626429(0x7f0e09bd, float:1.8880094E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0cdb:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0cf9
            java.lang.String r2 = "NotificationActionPinnedContactUser"
            r5 = 2131626394(0x7f0e099a, float:1.8880023E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0cf9:
            if (r6 == 0) goto L_0x0d18
            java.lang.String r2 = "NotificationActionPinnedContact2"
            r5 = 2131626392(0x7f0e0998, float:1.8880019E38)
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
            goto L_0x1b4e
        L_0x0d18:
            java.lang.String r2 = "NotificationActionPinnedContactChannel2"
            r5 = 2131626393(0x7f0e0999, float:1.888002E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0d30:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0d4e
            java.lang.String r2 = "NotificationActionPinnedVoiceUser"
            r5 = 2131626448(0x7f0e09d0, float:1.8880132E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0d4e:
            if (r6 == 0) goto L_0x0d68
            java.lang.String r2 = "NotificationActionPinnedVoice"
            r5 = 2131626446(0x7f0e09ce, float:1.8880128E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0d68:
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            r5 = 2131626447(0x7f0e09cf, float:1.888013E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0d7b:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0db8
            int r2 = r12.length     // Catch:{ all -> 0x0420 }
            r5 = 1
            if (r2 <= r5) goto L_0x0da5
            r2 = r12[r5]     // Catch:{ all -> 0x0420 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0420 }
            if (r2 != 0) goto L_0x0da5
            java.lang.String r2 = "NotificationActionPinnedStickerEmojiUser"
            r5 = 2131626438(0x7f0e09c6, float:1.8880112E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0da5:
            java.lang.String r2 = "NotificationActionPinnedStickerUser"
            r5 = 2131626439(0x7f0e09c7, float:1.8880114E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0db8:
            if (r6 == 0) goto L_0x0dfb
            int r2 = r12.length     // Catch:{ all -> 0x0420 }
            r5 = 2
            if (r2 <= r5) goto L_0x0de3
            r2 = r12[r5]     // Catch:{ all -> 0x0420 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0420 }
            if (r2 != 0) goto L_0x0de3
            java.lang.String r2 = "NotificationActionPinnedStickerEmoji"
            r5 = 2131626436(0x7f0e09c4, float:1.8880108E38)
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
            goto L_0x1b4e
        L_0x0de3:
            java.lang.String r2 = "NotificationActionPinnedSticker"
            r5 = 2131626434(0x7f0e09c2, float:1.8880104E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0dfb:
            int r2 = r12.length     // Catch:{ all -> 0x0420 }
            r5 = 1
            if (r2 <= r5) goto L_0x0e1f
            r2 = r12[r5]     // Catch:{ all -> 0x0420 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0420 }
            if (r2 != 0) goto L_0x0e1f
            java.lang.String r2 = "NotificationActionPinnedStickerEmojiChannel"
            r5 = 2131626437(0x7f0e09c5, float:1.888011E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0e1f:
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            r5 = 2131626435(0x7f0e09c3, float:1.8880106E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0e32:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e50
            java.lang.String r2 = "NotificationActionPinnedFileUser"
            r5 = 2131626397(0x7f0e099d, float:1.888003E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0e50:
            if (r6 == 0) goto L_0x0e6a
            java.lang.String r2 = "NotificationActionPinnedFile"
            r5 = 2131626395(0x7f0e099b, float:1.8880025E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0e6a:
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            r5 = 2131626396(0x7f0e099c, float:1.8880027E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0e7d:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e9b
            java.lang.String r2 = "NotificationActionPinnedRoundUser"
            r5 = 2131626433(0x7f0e09c1, float:1.8880102E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0e9b:
            if (r6 == 0) goto L_0x0eb5
            java.lang.String r2 = "NotificationActionPinnedRound"
            r5 = 2131626431(0x7f0e09bf, float:1.8880098E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0eb5:
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            r5 = 2131626432(0x7f0e09c0, float:1.88801E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0ec8:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0ee6
            java.lang.String r2 = "NotificationActionPinnedVideoUser"
            r5 = 2131626445(0x7f0e09cd, float:1.8880126E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0ee6:
            if (r6 == 0) goto L_0x0var_
            java.lang.String r2 = "NotificationActionPinnedVideo"
            r5 = 2131626443(0x7f0e09cb, float:1.8880122E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0var_:
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            r5 = 2131626444(0x7f0e09cc, float:1.8880124E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0var_:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0var_
            java.lang.String r2 = "NotificationActionPinnedPhotoUser"
            r5 = 2131626424(0x7f0e09b8, float:1.8880084E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0var_:
            if (r6 == 0) goto L_0x0f4b
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            r5 = 2131626422(0x7f0e09b6, float:1.888008E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0f4b:
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            r5 = 2131626423(0x7f0e09b7, float:1.8880082E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0f5e:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0f7c
            java.lang.String r2 = "NotificationActionPinnedNoTextUser"
            r5 = 2131626421(0x7f0e09b5, float:1.8880078E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0f7c:
            if (r6 == 0) goto L_0x0var_
            java.lang.String r2 = "NotificationActionPinnedNoText"
            r5 = 2131626419(0x7f0e09b3, float:1.8880074E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0var_:
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            r5 = 2131626420(0x7f0e09b4, float:1.8880076E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0fa9:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0fc7
            java.lang.String r2 = "NotificationActionPinnedTextUser"
            r5 = 2131626442(0x7f0e09ca, float:1.888012E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0fc7:
            if (r6 == 0) goto L_0x0fe6
            java.lang.String r2 = "NotificationActionPinnedText"
            r5 = 2131626440(0x7f0e09c8, float:1.8880116E38)
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
            goto L_0x1b4e
        L_0x0fe6:
            java.lang.String r2 = "NotificationActionPinnedTextChannel"
            r5 = 2131626441(0x7f0e09c9, float:1.8880118E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x0ffe:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAlbum"
            r5 = 2131626457(0x7f0e09d9, float:1.888015E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x17da
        L_0x1018:
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
            r5 = 2131626460(0x7f0e09dc, float:1.8880157E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17da
        L_0x1043:
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
            r5 = 2131626460(0x7f0e09dc, float:1.8880157E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17da
        L_0x106c:
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
            r5 = 2131626460(0x7f0e09dc, float:1.8880157E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17da
        L_0x1095:
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
            r5 = 2131626460(0x7f0e09dc, float:1.8880157E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17da
        L_0x10be:
            r1 = r18
            java.lang.String r2 = "NotificationGroupForwardedFew"
            r5 = 2131626461(0x7f0e09dd, float:1.8880159E38)
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
            goto L_0x17da
        L_0x10e9:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            r5 = 2131626456(0x7f0e09d8, float:1.8880149E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x1103:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAddSelf"
            r5 = 2131626455(0x7f0e09d7, float:1.8880147E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x111d:
            r1 = r18
            java.lang.String r2 = "NotificationGroupLeftMember"
            r5 = 2131626466(0x7f0e09e2, float:1.888017E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x1137:
            r1 = r18
            java.lang.String r2 = "NotificationGroupKickYou"
            r5 = 2131626465(0x7f0e09e1, float:1.8880167E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x1151:
            r1 = r18
            java.lang.String r2 = "NotificationGroupKickMember"
            r5 = 2131626464(0x7f0e09e0, float:1.8880165E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x116b:
            r1 = r18
            java.lang.String r2 = "NotificationGroupInvitedYouToCall"
            r5 = 2131626463(0x7f0e09df, float:1.8880163E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x1185:
            r1 = r18
            java.lang.String r2 = "NotificationGroupEndedCall"
            r5 = 2131626459(0x7f0e09db, float:1.8880155E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x119f:
            r1 = r18
            java.lang.String r2 = "NotificationGroupInvitedToCall"
            r5 = 2131626462(0x7f0e09de, float:1.888016E38)
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
            goto L_0x1b4e
        L_0x11be:
            r1 = r18
            java.lang.String r2 = "NotificationGroupCreatedCall"
            r5 = 2131626458(0x7f0e09da, float:1.8880153E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x11d8:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAddMember"
            r5 = 2131626454(0x7f0e09d6, float:1.8880145E38)
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
            goto L_0x1b4e
        L_0x11f7:
            r1 = r18
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            r5 = 2131626452(0x7f0e09d4, float:1.888014E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x1211:
            r1 = r18
            java.lang.String r2 = "NotificationEditedGroupName"
            r5 = 2131626451(0x7f0e09d3, float:1.8880139E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x122b:
            r1 = r18
            java.lang.String r2 = "NotificationInvitedToGroup"
            r5 = 2131626471(0x7f0e09e7, float:1.888018E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x0420 }
            r6[r7] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x1245:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupInvoice"
            r5 = 2131626488(0x7f0e09f8, float:1.8880214E38)
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
            r6 = 2131626878(0x7f0e0b7e, float:1.8881005E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x126d:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupGameScored"
            r5 = 2131626486(0x7f0e09f6, float:1.888021E38)
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
            goto L_0x1b4e
        L_0x1291:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupGame"
            r5 = 2131626485(0x7f0e09f5, float:1.8880208E38)
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
            r6 = 2131624388(0x7f0e01c4, float:1.8875954E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x12b9:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupGif"
            r5 = 2131626487(0x7f0e09f7, float:1.8880212E38)
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
            r6 = 2131624389(0x7f0e01c5, float:1.8875956E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x12dc:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupLiveLocation"
            r5 = 2131626489(0x7f0e09f9, float:1.8880216E38)
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
            r6 = 2131624394(0x7f0e01ca, float:1.8875966E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x12ff:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupMap"
            r5 = 2131626490(0x7f0e09fa, float:1.8880218E38)
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
            r6 = 2131624398(0x7f0e01ce, float:1.8875975E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1322:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupPoll2"
            r5 = 2131626494(0x7f0e09fe, float:1.8880226E38)
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
            r6 = 2131627045(0x7f0e0CLASSNAME, float:1.8881343E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x134a:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupQuiz2"
            r5 = 2131626495(0x7f0e09ff, float:1.8880228E38)
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
            r6 = 2131627052(0x7f0e0c2c, float:1.8881358E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1372:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupContact2"
            r5 = 2131626483(0x7f0e09f3, float:1.8880203E38)
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
            r6 = 2131624384(0x7f0e01c0, float:1.8875946E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x139a:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupAudio"
            r5 = 2131626482(0x7f0e09f2, float:1.8880201E38)
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
            r6 = 2131624382(0x7f0e01be, float:1.8875942E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x13bd:
            r1 = r18
            int r2 = r12.length     // Catch:{ all -> 0x0420 }
            r5 = 2
            if (r2 <= r5) goto L_0x1403
            r2 = r12[r5]     // Catch:{ all -> 0x0420 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0420 }
            if (r2 != 0) goto L_0x1403
            java.lang.String r2 = "NotificationMessageGroupStickerEmoji"
            r5 = 2131626498(0x7f0e0a02, float:1.8880234E38)
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
            r6 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)     // Catch:{ all -> 0x0420 }
            r5.append(r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1403:
            java.lang.String r2 = "NotificationMessageGroupSticker"
            r5 = 2131626497(0x7f0e0a01, float:1.8880232E38)
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
            r6 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)     // Catch:{ all -> 0x0420 }
            r5.append(r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1436:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupDocument"
            r5 = 2131626484(0x7f0e09f4, float:1.8880205E38)
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
            r6 = 2131624387(0x7f0e01c3, float:1.8875952E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1459:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupRound"
            r5 = 2131626496(0x7f0e0a00, float:1.888023E38)
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
            r6 = 2131624404(0x7f0e01d4, float:1.8875987E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x147c:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupVideo"
            r5 = 2131626500(0x7f0e0a04, float:1.8880238E38)
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
            r6 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x149f:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupPhoto"
            r5 = 2131626493(0x7f0e09fd, float:1.8880224E38)
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
            r6 = 2131624402(0x7f0e01d2, float:1.8875983E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x14c2:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupNoText"
            r5 = 2131626492(0x7f0e09fc, float:1.8880222E38)
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
            r6 = 2131626181(0x7f0e08c5, float:1.887959E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x14e5:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupText"
            r5 = 2131626499(0x7f0e0a03, float:1.8880236E38)
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
            goto L_0x1b17
        L_0x1506:
            r1 = r18
            java.lang.String r2 = "ChannelMessageAlbum"
            r5 = 2131624746(0x7f0e032a, float:1.887668E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x17da
        L_0x151b:
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
            r5 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17da
        L_0x1541:
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
            r5 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17da
        L_0x1565:
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
            r5 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17da
        L_0x1589:
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
            r5 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17da
        L_0x15ad:
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
            r5 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x0420 }
            goto L_0x17da
        L_0x15d7:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGame"
            r5 = 2131626479(0x7f0e09ef, float:1.8880195E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachGame"
            r6 = 2131624388(0x7f0e01c4, float:1.8875954E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x15f5:
            r1 = r18
            java.lang.String r2 = "ChannelMessageGIF"
            r5 = 2131624751(0x7f0e032f, float:1.887669E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachGif"
            r6 = 2131624389(0x7f0e01c5, float:1.8875956E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1613:
            r1 = r18
            java.lang.String r2 = "ChannelMessageLiveLocation"
            r5 = 2131624752(0x7f0e0330, float:1.8876693E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachLiveLocation"
            r6 = 2131624394(0x7f0e01ca, float:1.8875966E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1631:
            r1 = r18
            java.lang.String r2 = "ChannelMessageMap"
            r5 = 2131624753(0x7f0e0331, float:1.8876695E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachLocation"
            r6 = 2131624398(0x7f0e01ce, float:1.8875975E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x164f:
            r1 = r18
            java.lang.String r2 = "ChannelMessagePoll2"
            r5 = 2131624757(0x7f0e0335, float:1.8876703E38)
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
            r6 = 2131627045(0x7f0e0CLASSNAME, float:1.8881343E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1672:
            r1 = r18
            java.lang.String r2 = "ChannelMessageQuiz2"
            r5 = 2131624758(0x7f0e0336, float:1.8876705E38)
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
            r6 = 2131627160(0x7f0e0CLASSNAME, float:1.8881577E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1695:
            r1 = r18
            java.lang.String r2 = "ChannelMessageContact2"
            r5 = 2131624748(0x7f0e032c, float:1.8876684E38)
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
            r6 = 2131624384(0x7f0e01c0, float:1.8875946E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x16b8:
            r1 = r18
            java.lang.String r2 = "ChannelMessageAudio"
            r5 = 2131624747(0x7f0e032b, float:1.8876682E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachAudio"
            r6 = 2131624382(0x7f0e01be, float:1.8875942E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x16d6:
            r1 = r18
            int r2 = r12.length     // Catch:{ all -> 0x0420 }
            r5 = 1
            if (r2 <= r5) goto L_0x1717
            r2 = r12[r5]     // Catch:{ all -> 0x0420 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0420 }
            if (r2 != 0) goto L_0x1717
            java.lang.String r2 = "ChannelMessageStickerEmoji"
            r5 = 2131624761(0x7f0e0339, float:1.887671E38)
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
            r6 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)     // Catch:{ all -> 0x0420 }
            r5.append(r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1717:
            java.lang.String r2 = "ChannelMessageSticker"
            r5 = 2131624760(0x7f0e0338, float:1.8876709E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            r5 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1731:
            r1 = r18
            java.lang.String r2 = "ChannelMessageDocument"
            r5 = 2131624749(0x7f0e032d, float:1.8876687E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachDocument"
            r6 = 2131624387(0x7f0e01c3, float:1.8875952E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x174f:
            r1 = r18
            java.lang.String r2 = "ChannelMessageRound"
            r5 = 2131624759(0x7f0e0337, float:1.8876707E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachRound"
            r6 = 2131624404(0x7f0e01d4, float:1.8875987E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x176d:
            r1 = r18
            java.lang.String r2 = "ChannelMessageVideo"
            r5 = 2131624762(0x7f0e033a, float:1.8876713E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachVideo"
            r6 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x178b:
            r1 = r18
            java.lang.String r2 = "ChannelMessagePhoto"
            r5 = 2131624756(0x7f0e0334, float:1.88767E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachPhoto"
            r6 = 2131624402(0x7f0e01d2, float:1.8875983E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x17a9:
            r1 = r18
            java.lang.String r2 = "ChannelMessageNoText"
            r5 = 2131624755(0x7f0e0333, float:1.8876699E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "Message"
            r6 = 2131626181(0x7f0e08c5, float:1.887959E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x17c7:
            r1 = r18
            java.lang.String r2 = "NotificationMessageAlbum"
            r5 = 2131626473(0x7f0e09e9, float:1.8880183E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
        L_0x17da:
            r5 = 1
            goto L_0x1b4f
        L_0x17dd:
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
            r6 = 2131626477(0x7f0e09ed, float:1.8880191E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x17da
        L_0x1802:
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
            r5 = 2131626477(0x7f0e09ed, float:1.8880191E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x17da
        L_0x1825:
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
            r6 = 2131626477(0x7f0e09ed, float:1.8880191E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x17da
        L_0x1848:
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
            r6 = 2131626477(0x7f0e09ed, float:1.8880191E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x17da
        L_0x186c:
            r1 = r18
            java.lang.String r2 = "NotificationMessageForwardFew"
            r5 = 2131626478(0x7f0e09ee, float:1.8880193E38)
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
            goto L_0x17da
        L_0x1892:
            r1 = r18
            java.lang.String r2 = "NotificationMessageInvoice"
            r5 = 2131626501(0x7f0e0a05, float:1.888024E38)
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
            r6 = 2131626878(0x7f0e0b7e, float:1.8881005E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x18b5:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGameScored"
            r5 = 2131626480(0x7f0e09f0, float:1.8880197E38)
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
            goto L_0x1b4e
        L_0x18d4:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGame"
            r5 = 2131626479(0x7f0e09ef, float:1.8880195E38)
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
            r6 = 2131624388(0x7f0e01c4, float:1.8875954E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x18f7:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGif"
            r5 = 2131626481(0x7f0e09f1, float:1.88802E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachGif"
            r6 = 2131624389(0x7f0e01c5, float:1.8875956E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1915:
            r1 = r18
            java.lang.String r2 = "NotificationMessageLiveLocation"
            r5 = 2131626502(0x7f0e0a06, float:1.8880242E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachLiveLocation"
            r6 = 2131624394(0x7f0e01ca, float:1.8875966E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1933:
            r1 = r18
            java.lang.String r2 = "NotificationMessageMap"
            r5 = 2131626503(0x7f0e0a07, float:1.8880244E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachLocation"
            r6 = 2131624398(0x7f0e01ce, float:1.8875975E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1951:
            r1 = r18
            java.lang.String r2 = "NotificationMessagePoll2"
            r5 = 2131626507(0x7f0e0a0b, float:1.8880252E38)
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
            r6 = 2131627045(0x7f0e0CLASSNAME, float:1.8881343E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1974:
            r1 = r18
            java.lang.String r2 = "NotificationMessageQuiz2"
            r5 = 2131626508(0x7f0e0a0c, float:1.8880254E38)
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
            r6 = 2131627160(0x7f0e0CLASSNAME, float:1.8881577E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1997:
            r1 = r18
            java.lang.String r2 = "NotificationMessageContact2"
            r5 = 2131626475(0x7f0e09eb, float:1.8880187E38)
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
            r6 = 2131624384(0x7f0e01c0, float:1.8875946E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x19ba:
            r1 = r18
            java.lang.String r2 = "NotificationMessageAudio"
            r5 = 2131626474(0x7f0e09ea, float:1.8880185E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachAudio"
            r6 = 2131624382(0x7f0e01be, float:1.8875942E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x19d8:
            r1 = r18
            int r2 = r12.length     // Catch:{ all -> 0x0420 }
            r5 = 1
            if (r2 <= r5) goto L_0x1a19
            r2 = r12[r5]     // Catch:{ all -> 0x0420 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0420 }
            if (r2 != 0) goto L_0x1a19
            java.lang.String r2 = "NotificationMessageStickerEmoji"
            r5 = 2131626515(0x7f0e0a13, float:1.8880268E38)
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
            r6 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)     // Catch:{ all -> 0x0420 }
            r5.append(r6)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1a19:
            java.lang.String r2 = "NotificationMessageSticker"
            r5 = 2131626514(0x7f0e0a12, float:1.8880266E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            r5 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r5)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1a33:
            r1 = r18
            java.lang.String r2 = "NotificationMessageDocument"
            r5 = 2131626476(0x7f0e09ec, float:1.888019E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachDocument"
            r6 = 2131624387(0x7f0e01c3, float:1.8875952E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1a51:
            r1 = r18
            java.lang.String r2 = "NotificationMessageRound"
            r5 = 2131626509(0x7f0e0a0d, float:1.8880256E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachRound"
            r6 = 2131624404(0x7f0e01d4, float:1.8875987E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1a6f:
            r1 = r18
            java.lang.String r2 = "ActionTakeScreenshoot"
            r5 = 2131624161(0x7f0e00e1, float:1.8875494E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r5)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "un1"
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = r2.replace(r5, r7)     // Catch:{ all -> 0x0420 }
            goto L_0x1b4e
        L_0x1a86:
            r1 = r18
            java.lang.String r2 = "NotificationMessageSDVideo"
            r5 = 2131626511(0x7f0e0a0f, float:1.888026E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachDestructingVideo"
            r6 = 2131624386(0x7f0e01c2, float:1.887595E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1aa4:
            r1 = r18
            java.lang.String r2 = "NotificationMessageVideo"
            r5 = 2131626517(0x7f0e0a15, float:1.8880272E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachVideo"
            r6 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1ac1:
            r1 = r18
            java.lang.String r2 = "NotificationMessageSDPhoto"
            r5 = 2131626510(0x7f0e0a0e, float:1.8880258E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachDestructingPhoto"
            r6 = 2131624385(0x7f0e01c1, float:1.8875948E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1ade:
            r1 = r18
            java.lang.String r2 = "NotificationMessagePhoto"
            r5 = 2131626506(0x7f0e0a0a, float:1.888025E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "AttachPhoto"
            r6 = 2131624402(0x7f0e01d2, float:1.8875983E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
            goto L_0x1b17
        L_0x1afb:
            r1 = r18
            java.lang.String r2 = "NotificationMessageNoText"
            r5 = 2131626505(0x7f0e0a09, float:1.8880248E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0420 }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x0420 }
            r7[r6] = r8     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "Message"
            r6 = 2131626181(0x7f0e08c5, float:1.887959E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x0420 }
        L_0x1b17:
            r16 = r5
            r5 = 0
            goto L_0x1b51
        L_0x1b1b:
            r1 = r18
            java.lang.String r2 = "NotificationMessageText"
            r5 = 2131626516(0x7f0e0a14, float:1.888027E38)
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
            goto L_0x1b17
        L_0x1b36:
            if (r2 == 0) goto L_0x1b4d
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0420 }
            r2.<init>()     // Catch:{ all -> 0x0420 }
            java.lang.String r5 = "unhandled loc_key = "
            r2.append(r5)     // Catch:{ all -> 0x0420 }
            r2.append(r9)     // Catch:{ all -> 0x0420 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0420 }
            org.telegram.messenger.FileLog.w(r2)     // Catch:{ all -> 0x0420 }
        L_0x1b4d:
            r2 = 0
        L_0x1b4e:
            r5 = 0
        L_0x1b4f:
            r16 = 0
        L_0x1b51:
            if (r2 == 0) goto L_0x1c1f
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x1CLASSNAME }
            r6.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r6.id = r1     // Catch:{ all -> 0x1CLASSNAME }
            r7 = r39
            r6.random_id = r7     // Catch:{ all -> 0x1CLASSNAME }
            if (r16 == 0) goto L_0x1b63
            r1 = r16
            goto L_0x1b64
        L_0x1b63:
            r1 = r2
        L_0x1b64:
            r6.message = r1     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1000(0x3e8, double:4.94E-321)
            long r7 = r45 / r7
            int r1 = (int) r7     // Catch:{ all -> 0x1CLASSNAME }
            r6.date = r1     // Catch:{ all -> 0x1CLASSNAME }
            if (r33 == 0) goto L_0x1b76
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r1 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r6.action = r1     // Catch:{ all -> 0x0420 }
        L_0x1b76:
            if (r32 == 0) goto L_0x1b7f
            int r1 = r6.flags     // Catch:{ all -> 0x0420 }
            r7 = -2147483648(0xfffffffvar_, float:-0.0)
            r1 = r1 | r7
            r6.flags = r1     // Catch:{ all -> 0x0420 }
        L_0x1b7f:
            r6.dialog_id = r3     // Catch:{ all -> 0x1CLASSNAME }
            if (r38 == 0) goto L_0x1b91
            org.telegram.tgnet.TLRPC$TL_peerChannel r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r6.peer_id = r1     // Catch:{ all -> 0x0420 }
            r8 = r38
            r1.channel_id = r8     // Catch:{ all -> 0x0420 }
            r3 = r24
            goto L_0x1bac
        L_0x1b91:
            if (r24 == 0) goto L_0x1b9f
            org.telegram.tgnet.TLRPC$TL_peerChat r1 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r6.peer_id = r1     // Catch:{ all -> 0x0420 }
            r3 = r24
            r1.chat_id = r3     // Catch:{ all -> 0x0420 }
            goto L_0x1bac
        L_0x1b9f:
            r3 = r24
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1CLASSNAME }
            r1.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r6.peer_id = r1     // Catch:{ all -> 0x1CLASSNAME }
            r8 = r23
            r1.user_id = r8     // Catch:{ all -> 0x1CLASSNAME }
        L_0x1bac:
            int r1 = r6.flags     // Catch:{ all -> 0x1CLASSNAME }
            r1 = r1 | 256(0x100, float:3.59E-43)
            r6.flags = r1     // Catch:{ all -> 0x1CLASSNAME }
            if (r31 == 0) goto L_0x1bbe
            org.telegram.tgnet.TLRPC$TL_peerChat r1 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r6.from_id = r1     // Catch:{ all -> 0x0420 }
            r1.chat_id = r3     // Catch:{ all -> 0x0420 }
            goto L_0x1bde
        L_0x1bbe:
            if (r28 == 0) goto L_0x1bcc
            org.telegram.tgnet.TLRPC$TL_peerChannel r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r6.from_id = r1     // Catch:{ all -> 0x0420 }
            r3 = r28
            r1.channel_id = r3     // Catch:{ all -> 0x0420 }
            goto L_0x1bde
        L_0x1bcc:
            if (r25 == 0) goto L_0x1bda
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x0420 }
            r1.<init>()     // Catch:{ all -> 0x0420 }
            r6.from_id = r1     // Catch:{ all -> 0x0420 }
            r3 = r25
            r1.user_id = r3     // Catch:{ all -> 0x0420 }
            goto L_0x1bde
        L_0x1bda:
            org.telegram.tgnet.TLRPC$Peer r1 = r6.peer_id     // Catch:{ all -> 0x1CLASSNAME }
            r6.from_id = r1     // Catch:{ all -> 0x1CLASSNAME }
        L_0x1bde:
            if (r26 != 0) goto L_0x1be5
            if (r33 == 0) goto L_0x1be3
            goto L_0x1be5
        L_0x1be3:
            r1 = 0
            goto L_0x1be6
        L_0x1be5:
            r1 = 1
        L_0x1be6:
            r6.mentioned = r1     // Catch:{ all -> 0x1CLASSNAME }
            r1 = r27
            r6.silent = r1     // Catch:{ all -> 0x1CLASSNAME }
            r13 = r22
            r6.from_scheduled = r13     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.messenger.MessageObject r1 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x1CLASSNAME }
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
            r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27, r28)     // Catch:{ all -> 0x1CLASSNAME }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x1CLASSNAME }
            r2.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r2.add(r1)     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.messenger.NotificationsController r1 = org.telegram.messenger.NotificationsController.getInstance(r30)     // Catch:{ all -> 0x1CLASSNAME }
            r3 = r43
            java.util.concurrent.CountDownLatch r4 = r3.countDownLatch     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            r1.processNewMessages(r2, r5, r5, r4)     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 0
            goto L_0x1CLASSNAME
        L_0x1c1f:
            r3 = r43
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r0 = move-exception
            r3 = r43
            goto L_0x1c4a
        L_0x1CLASSNAME:
            r0 = move-exception
            r3 = r43
            goto L_0x1CLASSNAME
        L_0x1c2a:
            r3 = r43
            goto L_0x1CLASSNAME
        L_0x1c2d:
            r0 = move-exception
            r3 = r1
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r3 = r1
            r29 = r14
        L_0x1CLASSNAME:
            r30 = r15
        L_0x1CLASSNAME:
            r8 = 1
        L_0x1CLASSNAME:
            if (r8 == 0) goto L_0x1c3d
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1CLASSNAME }
            r1.countDown()     // Catch:{ all -> 0x1CLASSNAME }
        L_0x1c3d:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r30)     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r30)     // Catch:{ all -> 0x1CLASSNAME }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1d76
        L_0x1CLASSNAME:
            r0 = move-exception
        L_0x1c4a:
            r1 = r0
            r4 = r9
            r14 = r29
            r15 = r30
            goto L_0x1d21
        L_0x1CLASSNAME:
            r0 = move-exception
            r3 = r1
            r29 = r14
            r30 = r15
            r1 = r0
            r4 = r9
            goto L_0x1d21
        L_0x1c5c:
            r0 = move-exception
            r3 = r1
            r29 = r7
        L_0x1CLASSNAME:
            r30 = r15
            goto L_0x1d1d
        L_0x1CLASSNAME:
            r3 = r1
            r29 = r7
            r30 = r15
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1c7b }
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$jVvuwI-LdG7mFvHrO-DBdZ4swec r2 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$jVvuwI-LdG7mFvHrO-DBdZ4swec     // Catch:{ all -> 0x1c7b }
            r15 = r30
            r2.<init>(r15)     // Catch:{ all -> 0x1d17 }
            r1.postRunnable(r2)     // Catch:{ all -> 0x1d17 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d17 }
            r1.countDown()     // Catch:{ all -> 0x1d17 }
            return
        L_0x1c7b:
            r0 = move-exception
            r15 = r30
            goto L_0x1d1d
        L_0x1CLASSNAME:
            r3 = r1
            r29 = r7
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$-QuXd6CaqKEr-yz6R8VHHhEnJAw r1 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$-QuXd6CaqKEr-yz6R8VHHhEnJAw     // Catch:{ all -> 0x1d17 }
            r1.<init>(r15)     // Catch:{ all -> 0x1d17 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ all -> 0x1d17 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d17 }
            r1.countDown()     // Catch:{ all -> 0x1d17 }
            return
        L_0x1CLASSNAME:
            r3 = r1
            r29 = r7
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x1d17 }
            r1.<init>()     // Catch:{ all -> 0x1d17 }
            r2 = 0
            r1.popup = r2     // Catch:{ all -> 0x1d17 }
            r2 = 2
            r1.flags = r2     // Catch:{ all -> 0x1d17 }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r6 = r45 / r6
            int r2 = (int) r6     // Catch:{ all -> 0x1d17 }
            r1.inbox_date = r2     // Catch:{ all -> 0x1d17 }
            java.lang.String r2 = "message"
            java.lang.String r2 = r5.getString(r2)     // Catch:{ all -> 0x1d17 }
            r1.message = r2     // Catch:{ all -> 0x1d17 }
            java.lang.String r2 = "announcement"
            r1.type = r2     // Catch:{ all -> 0x1d17 }
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ all -> 0x1d17 }
            r2.<init>()     // Catch:{ all -> 0x1d17 }
            r1.media = r2     // Catch:{ all -> 0x1d17 }
            org.telegram.tgnet.TLRPC$TL_updates r2 = new org.telegram.tgnet.TLRPC$TL_updates     // Catch:{ all -> 0x1d17 }
            r2.<init>()     // Catch:{ all -> 0x1d17 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r4 = r2.updates     // Catch:{ all -> 0x1d17 }
            r4.add(r1)     // Catch:{ all -> 0x1d17 }
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1d17 }
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$y7ADJS7XfMNFfehioCWcBUPm0lQ r4 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$y7ADJS7XfMNFfehioCWcBUPm0lQ     // Catch:{ all -> 0x1d17 }
            r4.<init>(r15, r2)     // Catch:{ all -> 0x1d17 }
            r1.postRunnable(r4)     // Catch:{ all -> 0x1d17 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x1d17 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1d17 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d17 }
            r1.countDown()     // Catch:{ all -> 0x1d17 }
            return
        L_0x1cda:
            r3 = r1
            r29 = r7
            java.lang.String r1 = "dc"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x1d17 }
            java.lang.String r2 = "addr"
            java.lang.String r2 = r11.getString(r2)     // Catch:{ all -> 0x1d17 }
            java.lang.String r4 = ":"
            java.lang.String[] r2 = r2.split(r4)     // Catch:{ all -> 0x1d17 }
            int r4 = r2.length     // Catch:{ all -> 0x1d17 }
            r5 = 2
            if (r4 == r5) goto L_0x1cf9
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d17 }
            r1.countDown()     // Catch:{ all -> 0x1d17 }
            return
        L_0x1cf9:
            r4 = 0
            r4 = r2[r4]     // Catch:{ all -> 0x1d17 }
            r5 = 1
            r2 = r2[r5]     // Catch:{ all -> 0x1d17 }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ all -> 0x1d17 }
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x1d17 }
            r5.applyDatacenterAddress(r1, r4, r2)     // Catch:{ all -> 0x1d17 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x1d17 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1d17 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d17 }
            r1.countDown()     // Catch:{ all -> 0x1d17 }
            return
        L_0x1d17:
            r0 = move-exception
            goto L_0x1d1d
        L_0x1d19:
            r0 = move-exception
            r3 = r1
            r29 = r7
        L_0x1d1d:
            r1 = r0
            r4 = r9
            r14 = r29
        L_0x1d21:
            r2 = -1
            goto L_0x1d3e
        L_0x1d23:
            r0 = move-exception
            r3 = r1
            r29 = r7
            r1 = r0
            r4 = r9
            r14 = r29
            r2 = -1
            goto L_0x1d3d
        L_0x1d2d:
            r0 = move-exception
            r3 = r1
            r29 = r7
            r1 = r0
            r14 = r29
            r2 = -1
            r4 = 0
            goto L_0x1d3d
        L_0x1d37:
            r0 = move-exception
            r3 = r1
            r1 = r0
            r2 = -1
            r4 = 0
            r14 = 0
        L_0x1d3d:
            r15 = -1
        L_0x1d3e:
            if (r15 == r2) goto L_0x1d50
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15)
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)
            r2.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r2 = r3.countDownLatch
            r2.countDown()
            goto L_0x1d53
        L_0x1d50:
            r43.onDecryptError()
        L_0x1d53:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x1d73
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
        L_0x1d73:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1d76:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.GcmPushListenerService.lambda$onMessageReceived$3$GcmPushListenerService(java.util.Map, long):void");
    }

    static /* synthetic */ void lambda$onMessageReceived$1(int i) {
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
