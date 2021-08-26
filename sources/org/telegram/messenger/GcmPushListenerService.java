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
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x01dd, code lost:
        if (r2 == 0) goto L_0x1cd4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x01df, code lost:
        if (r2 == 1) goto L_0x1c8b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x01e1, code lost:
        if (r2 == 2) goto L_0x1c7a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x01e3, code lost:
        if (r2 == 3) goto L_0x1c5e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x01ed, code lost:
        if (r11.has("channel_id") == false) goto L_0x01f8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:?, code lost:
        r2 = r11.getInt("channel_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x01f5, code lost:
        r3 = (long) (-r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x01f8, code lost:
        r3 = 0;
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x0201, code lost:
        if (r11.has("from_id") == false) goto L_0x0215;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:?, code lost:
        r3 = r11.getInt("from_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x0209, code lost:
        r14 = r7;
        r6 = r3;
        r3 = (long) r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x0211, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x0212, code lost:
        r14 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0213, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x0215, code lost:
        r14 = r7;
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:0x021d, code lost:
        if (r11.has("chat_id") == false) goto L_0x022f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:?, code lost:
        r3 = r11.getInt("chat_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x0225, code lost:
        r12 = r3;
        r3 = (long) (-r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x022d, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x022f, code lost:
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:159:0x0236, code lost:
        if (r11.has("encryption_id") == false) goto L_0x0242;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:163:0x023e, code lost:
        r3 = ((long) r11.getInt("encryption_id")) << 32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:167:0x0248, code lost:
        if (r11.has("schedule") == false) goto L_0x0254;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x0250, code lost:
        if (r11.getInt("schedule") != 1) goto L_0x0254;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x0252, code lost:
        r13 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x0254, code lost:
        r13 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:175:0x0257, code lost:
        if (r3 != 0) goto L_0x0266;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:177:0x025f, code lost:
        if ("ENCRYPTED_MESSAGE".equals(r9) == false) goto L_0x0266;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x0261, code lost:
        r3 = -4294967296L;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x0268, code lost:
        if (r3 == 0) goto L_0x1c2a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:0x0272, code lost:
        if ("READ_HISTORY".equals(r9) == false) goto L_0x02de;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:?, code lost:
        r5 = r11.getInt("max_id");
        r7 = new java.util.ArrayList();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x0281, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x029d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x0283, code lost:
        org.telegram.messenger.FileLog.d("GCM received read notification max_id = " + r5 + " for dialogId = " + r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x029d, code lost:
        if (r2 == 0) goto L_0x02ac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x029f, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox();
        r3.channel_id = r2;
        r3.max_id = r5;
        r7.add(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x02ac, code lost:
        r2 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x02b1, code lost:
        if (r6 == 0) goto L_0x02bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x02b3, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r2.peer = r3;
        r3.user_id = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x02bd, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r2.peer = r3;
        r3.chat_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x02c6, code lost:
        r2.max_id = r5;
        r7.add(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x02cb, code lost:
        org.telegram.messenger.MessagesController.getInstance(r15).processUpdateArray(r7, (java.util.ArrayList<org.telegram.tgnet.TLRPC$User>) null, (java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat>) null, false, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x02e6, code lost:
        if ("MESSAGE_DELETED".equals(r9) == false) goto L_0x034b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:?, code lost:
        r5 = r11.getString("messages").split(",");
        r6 = new android.util.SparseArray();
        r7 = new java.util.ArrayList();
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x02fe, code lost:
        if (r8 >= r5.length) goto L_0x030c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x0300, code lost:
        r7.add(org.telegram.messenger.Utilities.parseInt(r5[r8]));
        r8 = r8 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x030c, code lost:
        r6.put(r2, r7);
        org.telegram.messenger.NotificationsController.getInstance(r15).removeDeletedMessagesFromNotifications(r6);
        org.telegram.messenger.MessagesController.getInstance(r15).deleteMessagesByPush(r3, r7, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x031f, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1c2a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x0321, code lost:
        org.telegram.messenger.FileLog.d("GCM received " + r9 + " for dialogId = " + r3 + " mids = " + android.text.TextUtils.join(",", r7));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x034f, code lost:
        if (android.text.TextUtils.isEmpty(r9) != false) goto L_0x1c2a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x0357, code lost:
        if (r11.has("msg_id") == false) goto L_0x0362;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:?, code lost:
        r7 = r11.getInt("msg_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:220:0x035f, code lost:
        r29 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x0362, code lost:
        r29 = r14;
        r7 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x0367, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:225:0x036b, code lost:
        if (r11.has("random_id") == false) goto L_0x0389;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x037b, code lost:
        r22 = r13;
        r13 = org.telegram.messenger.Utilities.parseLong(r11.getString("random_id")).longValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x0382, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x0383, code lost:
        r3 = r1;
        r4 = r9;
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x0389, code lost:
        r22 = r13;
        r13 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x038d, code lost:
        if (r7 == 0) goto L_0x03d3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:?, code lost:
        r23 = r6;
        r1 = (java.lang.Integer) org.telegram.messenger.MessagesController.getInstance(r15).dialogs_read_inbox_max.get(java.lang.Long.valueOf(r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x03a1, code lost:
        if (r1 != null) goto L_0x03c0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x03a3, code lost:
        r1 = java.lang.Integer.valueOf(org.telegram.messenger.MessagesStorage.getInstance(r15).getDialogReadMax(false, r3));
        r24 = r12;
        org.telegram.messenger.MessagesController.getInstance(r15).dialogs_read_inbox_max.put(java.lang.Long.valueOf(r3), r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x03c0, code lost:
        r24 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x03c6, code lost:
        if (r7 <= r1.intValue()) goto L_0x03e7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x03c9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x03ca, code lost:
        r2 = -1;
        r3 = r43;
        r1 = r0;
        r4 = r9;
        r14 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x03d3, code lost:
        r23 = r6;
        r24 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x03d9, code lost:
        if (r13 == 0) goto L_0x03e7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x03e3, code lost:
        if (org.telegram.messenger.MessagesStorage.getInstance(r15).checkMessageByRandomId(r13) != false) goto L_0x03e7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x03e5, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x03e7, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x03e8, code lost:
        if (r1 == false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x03ed, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:?, code lost:
        r1 = r11.optInt("chat_from_id", 0);
        r12 = r11.optInt("chat_from_broadcast_id", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x03f7, code lost:
        r30 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x03fb, code lost:
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:?, code lost:
        r15 = r11.optInt("chat_from_group_id", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x03ff, code lost:
        if (r1 != 0) goto L_0x0408;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x0401, code lost:
        if (r15 == 0) goto L_0x0404;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x0404, code lost:
        r25 = r1;
        r6 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x0408, code lost:
        r25 = r1;
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x0411, code lost:
        if (r11.has("mention") == false) goto L_0x042a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x0419, code lost:
        if (r11.getInt("mention") == 0) goto L_0x042a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x041b, code lost:
        r26 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x041e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x041f, code lost:
        r2 = -1;
        r3 = r43;
        r1 = r0;
        r4 = r9;
        r14 = r29;
        r15 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x042a, code lost:
        r26 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:275:0x0432, code lost:
        if (r11.has("silent") == false) goto L_0x043f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x043a, code lost:
        if (r11.getInt("silent") == 0) goto L_0x043f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x043c, code lost:
        r27 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x043f, code lost:
        r27 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x0447, code lost:
        if (r5.has("loc_args") == false) goto L_0x0467;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:?, code lost:
        r1 = r5.getJSONArray("loc_args");
        r5 = r1.length();
        r28 = r12;
        r12 = new java.lang.String[r5];
        r31 = r15;
        r15 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x045a, code lost:
        if (r15 >= r5) goto L_0x0465;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x045c, code lost:
        r12[r15] = r1.getString(r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x0462, code lost:
        r15 = r15 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x0465, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x0467, code lost:
        r28 = r12;
        r31 = r15;
        r1 = 0;
        r12 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:?, code lost:
        r5 = r12[r1];
        r1 = r11.has("edit_date");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x047b, code lost:
        if (r9.startsWith("CHAT_") == false) goto L_0x04ae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x0481, code lost:
        if (org.telegram.messenger.UserObject.isReplyUser(r3) == false) goto L_0x049b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x0483, code lost:
        r5 = r5 + " @ " + r12[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x049b, code lost:
        if (r2 == 0) goto L_0x049f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x049d, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x049f, code lost:
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x04a3, code lost:
        r15 = false;
        r33 = false;
        r41 = r11;
        r11 = r5;
        r5 = r12[1];
        r32 = r41;
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
        r32 = r11;
        r11 = null;
        r15 = false;
        r33 = true;
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
            case -2100047043: goto L_0x0a2f;
            case -2091498420: goto L_0x0a24;
            case -2053872415: goto L_0x0a19;
            case -2039746363: goto L_0x0a0e;
            case -2023218804: goto L_0x0a03;
            case -1979538588: goto L_0x09f8;
            case -1979536003: goto L_0x09ed;
            case -1979535888: goto L_0x09e2;
            case -1969004705: goto L_0x09d7;
            case -1946699248: goto L_0x09cb;
            case -1646640058: goto L_0x09bf;
            case -1528047021: goto L_0x09b3;
            case -1493579426: goto L_0x09a7;
            case -1482481933: goto L_0x099b;
            case -1480102982: goto L_0x0990;
            case -1478041834: goto L_0x0984;
            case -1474543101: goto L_0x0979;
            case -1465695932: goto L_0x096d;
            case -1374906292: goto L_0x0961;
            case -1372940586: goto L_0x0955;
            case -1264245338: goto L_0x0949;
            case -1236154001: goto L_0x093d;
            case -1236086700: goto L_0x0931;
            case -1236077786: goto L_0x0925;
            case -1235796237: goto L_0x0919;
            case -1235760759: goto L_0x090d;
            case -1235686303: goto L_0x0902;
            case -1198046100: goto L_0x08f7;
            case -1124254527: goto L_0x08eb;
            case -1085137927: goto L_0x08df;
            case -1084856378: goto L_0x08d3;
            case -1084820900: goto L_0x08c7;
            case -1084746444: goto L_0x08bb;
            case -819729482: goto L_0x08af;
            case -772141857: goto L_0x08a3;
            case -638310039: goto L_0x0897;
            case -590403924: goto L_0x088b;
            case -589196239: goto L_0x087f;
            case -589193654: goto L_0x0873;
            case -589193539: goto L_0x0867;
            case -440169325: goto L_0x085b;
            case -412748110: goto L_0x084f;
            case -228518075: goto L_0x0843;
            case -213586509: goto L_0x0837;
            case -115582002: goto L_0x082b;
            case -112621464: goto L_0x081f;
            case -108522133: goto L_0x0813;
            case -107572034: goto L_0x0808;
            case -40534265: goto L_0x07fc;
            case 65254746: goto L_0x07f0;
            case 141040782: goto L_0x07e4;
            case 202550149: goto L_0x07d8;
            case 309993049: goto L_0x07cc;
            case 309995634: goto L_0x07c0;
            case 309995749: goto L_0x07b4;
            case 320532812: goto L_0x07a8;
            case 328933854: goto L_0x079c;
            case 331340546: goto L_0x0790;
            case 342406591: goto L_0x0784;
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
        if (r9.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x0513, code lost:
        r1 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x051d, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x051f, code lost:
        r1 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x0529, code lost:
        if (r9.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x052b, code lost:
        r1 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x0535, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PLAYLIST") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x0537, code lost:
        r1 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x0541, code lost:
        if (r9.equals("PINNED_CONTACT") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x0543, code lost:
        r1 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x054d, code lost:
        if (r9.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x054f, code lost:
        r1 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x0559, code lost:
        if (r9.equals("LOCKED_MESSAGE") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x055b, code lost:
        r1 = 'j';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x0565, code lost:
        if (r9.equals("CHAT_MESSAGE_PLAYLIST") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x0567, code lost:
        r1 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x0571, code lost:
        if (r9.equals("CHANNEL_MESSAGES") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x0573, code lost:
        r1 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x057d, code lost:
        if (r9.equals("MESSAGE_INVOICE") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x057f, code lost:
        r1 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x0589, code lost:
        if (r9.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x058b, code lost:
        r1 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x0595, code lost:
        if (r9.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x0597, code lost:
        r1 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x05a1, code lost:
        if (r9.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x05a3, code lost:
        r1 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x05ad, code lost:
        if (r9.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x05af, code lost:
        r1 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x05b9, code lost:
        if (r9.equals("MESSAGE_PLAYLIST") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x05bb, code lost:
        r1 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x05c5, code lost:
        if (r9.equals("MESSAGE_VIDEOS") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x05c7, code lost:
        r1 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x05d1, code lost:
        if (r9.equals("PHONE_CALL_MISSED") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x05d3, code lost:
        r1 = 'o';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x05dd, code lost:
        if (r9.equals("MESSAGE_PHOTOS") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x05df, code lost:
        r1 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x05e9, code lost:
        if (r9.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x05eb, code lost:
        r1 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x05f5, code lost:
        if (r9.equals("MESSAGE_NOTEXT") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x05f7, code lost:
        r1 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x0600, code lost:
        if (r9.equals("MESSAGE_GIF") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x0602, code lost:
        r1 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x060c, code lost:
        if (r9.equals("MESSAGE_GEO") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x060e, code lost:
        r1 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x0618, code lost:
        if (r9.equals("MESSAGE_DOC") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x061a, code lost:
        r1 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:400:0x0624, code lost:
        if (r9.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x0626, code lost:
        r1 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0630, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x0632, code lost:
        r1 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x063c, code lost:
        if (r9.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x063e, code lost:
        r1 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x0648, code lost:
        if (r9.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x064a, code lost:
        r1 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0654, code lost:
        if (r9.equals("CHAT_TITLE_EDITED") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x0656, code lost:
        r1 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x0660, code lost:
        if (r9.equals("PINNED_NOTEXT") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x0662, code lost:
        r1 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x066c, code lost:
        if (r9.equals("MESSAGE_TEXT") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x066e, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x0677, code lost:
        if (r9.equals("MESSAGE_QUIZ") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x0679, code lost:
        r1 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x0683, code lost:
        if (r9.equals("MESSAGE_POLL") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x0685, code lost:
        r1 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x068f, code lost:
        if (r9.equals("MESSAGE_GAME") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x0691, code lost:
        r1 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x069b, code lost:
        if (r9.equals("MESSAGE_FWDS") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x069d, code lost:
        r1 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x06a7, code lost:
        if (r9.equals("MESSAGE_DOCS") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x06a9, code lost:
        r1 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x06b3, code lost:
        if (r9.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x06b5, code lost:
        r1 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x06bf, code lost:
        if (r9.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x06c1, code lost:
        r1 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x06cb, code lost:
        if (r9.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x06cd, code lost:
        r1 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x06d7, code lost:
        if (r9.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x06d9, code lost:
        r1 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x06e3, code lost:
        if (r9.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x06e5, code lost:
        r1 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x06ef, code lost:
        if (r9.equals("CHAT_MESSAGE_DOCS") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x06f1, code lost:
        r1 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x06fb, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x06fd, code lost:
        r1 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x0707, code lost:
        if (r9.equals("PINNED_GEOLIVE") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:458:0x0709, code lost:
        r1 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:460:0x0713, code lost:
        if (r9.equals("MESSAGE_CONTACT") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x0715, code lost:
        r1 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x071f, code lost:
        if (r9.equals("PINNED_VIDEO") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x0721, code lost:
        r1 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x072b, code lost:
        if (r9.equals("PINNED_ROUND") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x072d, code lost:
        r1 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x0737, code lost:
        if (r9.equals("PINNED_PHOTO") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x0739, code lost:
        r1 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:472:0x0743, code lost:
        if (r9.equals("PINNED_AUDIO") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0745, code lost:
        r1 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x074f, code lost:
        if (r9.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x0751, code lost:
        r1 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x075a, code lost:
        if (r9.equals("CHAT_VOICECHAT_INVITE_YOU") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x075c, code lost:
        r1 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x0766, code lost:
        if (r9.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x0768, code lost:
        r1 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x0772, code lost:
        if (r9.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x0774, code lost:
        r1 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x077e, code lost:
        if (r9.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x0780, code lost:
        r1 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x078a, code lost:
        if (r9.equals("CHAT_VOICECHAT_END") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x078c, code lost:
        r1 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x0796, code lost:
        if (r9.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x0798, code lost:
        r1 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x07a2, code lost:
        if (r9.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x07a4, code lost:
        r1 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x07ae, code lost:
        if (r9.equals("MESSAGES") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x07b0, code lost:
        r1 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x07ba, code lost:
        if (r9.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x07bc, code lost:
        r1 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x07c6, code lost:
        if (r9.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x07c8, code lost:
        r1 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x07d2, code lost:
        if (r9.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x07d4, code lost:
        r1 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x07de, code lost:
        if (r9.equals("CHAT_VOICECHAT_INVITE") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x07e0, code lost:
        r1 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x07ea, code lost:
        if (r9.equals("CHAT_LEFT") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x07ec, code lost:
        r1 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x07f6, code lost:
        if (r9.equals("CHAT_ADD_YOU") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x07f8, code lost:
        r1 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0802, code lost:
        if (r9.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x0804, code lost:
        r1 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x080e, code lost:
        if (r9.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0810, code lost:
        r1 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x0819, code lost:
        if (r9.equals("AUTH_REGION") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x081b, code lost:
        r1 = 'i';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0825, code lost:
        if (r9.equals("CONTACT_JOINED") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x0827, code lost:
        r1 = 'g';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x0831, code lost:
        if (r9.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0833, code lost:
        r1 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x083d, code lost:
        if (r9.equals("ENCRYPTION_REQUEST") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x083f, code lost:
        r1 = 'k';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:538:0x0849, code lost:
        if (r9.equals("MESSAGE_GEOLIVE") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x084b, code lost:
        r1 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0855, code lost:
        if (r9.equals("CHAT_DELETE_YOU") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x0857, code lost:
        r1 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x0861, code lost:
        if (r9.equals("AUTH_UNKNOWN") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0863, code lost:
        r1 = 'h';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x086d, code lost:
        if (r9.equals("PINNED_GIF") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x086f, code lost:
        r1 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x0879, code lost:
        if (r9.equals("PINNED_GEO") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x087b, code lost:
        r1 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x0885, code lost:
        if (r9.equals("PINNED_DOC") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x0887, code lost:
        r1 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x0891, code lost:
        if (r9.equals("PINNED_GAME_SCORE") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x0893, code lost:
        r1 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x089d, code lost:
        if (r9.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x089f, code lost:
        r1 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x08a9, code lost:
        if (r9.equals("PHONE_CALL_REQUEST") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x08ab, code lost:
        r1 = 'm';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x08b5, code lost:
        if (r9.equals("PINNED_STICKER") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x08b7, code lost:
        r1 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x08c1, code lost:
        if (r9.equals("PINNED_TEXT") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x08c3, code lost:
        r1 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x08cd, code lost:
        if (r9.equals("PINNED_QUIZ") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x08cf, code lost:
        r1 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x08d9, code lost:
        if (r9.equals("PINNED_POLL") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x08db, code lost:
        r1 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x08e5, code lost:
        if (r9.equals("PINNED_GAME") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x08e7, code lost:
        r1 = 'b';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x08f1, code lost:
        if (r9.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x08f3, code lost:
        r1 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x08fd, code lost:
        if (r9.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x08ff, code lost:
        r1 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x0908, code lost:
        if (r9.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x090a, code lost:
        r1 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x0913, code lost:
        if (r9.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x0915, code lost:
        r1 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x091f, code lost:
        if (r9.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0921, code lost:
        r1 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x092b, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x092d, code lost:
        r1 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x0937, code lost:
        if (r9.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0939, code lost:
        r1 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0943, code lost:
        if (r9.equals("CHANNEL_MESSAGE_DOCS") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0945, code lost:
        r1 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x094f, code lost:
        if (r9.equals("PINNED_INVOICE") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0951, code lost:
        r1 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x095b, code lost:
        if (r9.equals("CHAT_RETURNED") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x095d, code lost:
        r1 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x0967, code lost:
        if (r9.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x0969, code lost:
        r1 = 'f';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x0973, code lost:
        if (r9.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x0975, code lost:
        r1 = 'l';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x097f, code lost:
        if (r9.equals("MESSAGE_VIDEO") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x0981, code lost:
        r1 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x098a, code lost:
        if (r9.equals("MESSAGE_ROUND") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x098c, code lost:
        r1 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x0996, code lost:
        if (r9.equals("MESSAGE_PHOTO") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x0998, code lost:
        r1 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:625:0x09a1, code lost:
        if (r9.equals("MESSAGE_MUTED") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x09a3, code lost:
        r1 = 'n';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x09ad, code lost:
        if (r9.equals("MESSAGE_AUDIO") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x09af, code lost:
        r1 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x09b9, code lost:
        if (r9.equals("CHAT_MESSAGES") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x09bb, code lost:
        r1 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x09c5, code lost:
        if (r9.equals("CHAT_VOICECHAT_START") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x09c7, code lost:
        r1 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x09d1, code lost:
        if (r9.equals("CHAT_JOINED") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x09d3, code lost:
        r1 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x09dd, code lost:
        if (r9.equals("CHAT_ADD_MEMBER") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x09df, code lost:
        r1 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x09e8, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x09ea, code lost:
        r1 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x09f3, code lost:
        if (r9.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x09f5, code lost:
        r1 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x09fe, code lost:
        if (r9.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0a00, code lost:
        r1 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0a09, code lost:
        if (r9.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0a0b, code lost:
        r1 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0a14, code lost:
        if (r9.equals("MESSAGE_STICKER") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0a16, code lost:
        r1 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0a1f, code lost:
        if (r9.equals("CHAT_CREATED") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0a21, code lost:
        r1 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x0a2a, code lost:
        if (r9.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0a2c, code lost:
        r1 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0a35, code lost:
        if (r9.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0a3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x0a37, code lost:
        r1 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0a3a, code lost:
        r1 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x0a3b, code lost:
        r18 = r7;
        r36 = r15;
        r37 = r11;
        r38 = r2;
        r39 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0a55, code lost:
        switch(r1) {
            case 0: goto L_0x1b16;
            case 1: goto L_0x1b16;
            case 2: goto L_0x1af6;
            case 3: goto L_0x1ad9;
            case 4: goto L_0x1abc;
            case 5: goto L_0x1a9f;
            case 6: goto L_0x1a81;
            case 7: goto L_0x1a6b;
            case 8: goto L_0x1a4d;
            case 9: goto L_0x1a2f;
            case 10: goto L_0x19d4;
            case 11: goto L_0x19b6;
            case 12: goto L_0x1993;
            case 13: goto L_0x1970;
            case 14: goto L_0x194d;
            case 15: goto L_0x192f;
            case 16: goto L_0x1911;
            case 17: goto L_0x18f3;
            case 18: goto L_0x18d0;
            case 19: goto L_0x18b1;
            case 20: goto L_0x18b1;
            case 21: goto L_0x188e;
            case 22: goto L_0x1868;
            case 23: goto L_0x1844;
            case 24: goto L_0x1821;
            case 25: goto L_0x17fe;
            case 26: goto L_0x17d9;
            case 27: goto L_0x17c3;
            case 28: goto L_0x17a5;
            case 29: goto L_0x1787;
            case 30: goto L_0x1769;
            case 31: goto L_0x174b;
            case 32: goto L_0x172d;
            case 33: goto L_0x16d2;
            case 34: goto L_0x16b4;
            case 35: goto L_0x1691;
            case 36: goto L_0x166e;
            case 37: goto L_0x164b;
            case 38: goto L_0x162d;
            case 39: goto L_0x160f;
            case 40: goto L_0x15f1;
            case 41: goto L_0x15d3;
            case 42: goto L_0x15a9;
            case 43: goto L_0x1585;
            case 44: goto L_0x1561;
            case 45: goto L_0x153d;
            case 46: goto L_0x1517;
            case 47: goto L_0x1502;
            case 48: goto L_0x14e1;
            case 49: goto L_0x14be;
            case 50: goto L_0x149b;
            case 51: goto L_0x1478;
            case 52: goto L_0x1455;
            case 53: goto L_0x1432;
            case 54: goto L_0x13b9;
            case 55: goto L_0x1396;
            case 56: goto L_0x136e;
            case 57: goto L_0x1346;
            case 58: goto L_0x131e;
            case 59: goto L_0x12fb;
            case 60: goto L_0x12d8;
            case 61: goto L_0x12b5;
            case 62: goto L_0x128d;
            case 63: goto L_0x1269;
            case 64: goto L_0x1241;
            case 65: goto L_0x1227;
            case 66: goto L_0x1227;
            case 67: goto L_0x120d;
            case 68: goto L_0x11f3;
            case 69: goto L_0x11d4;
            case 70: goto L_0x11ba;
            case 71: goto L_0x119b;
            case 72: goto L_0x1181;
            case 73: goto L_0x1167;
            case 74: goto L_0x114d;
            case 75: goto L_0x1133;
            case 76: goto L_0x1119;
            case 77: goto L_0x10ff;
            case 78: goto L_0x10e5;
            case 79: goto L_0x10ba;
            case 80: goto L_0x1091;
            case 81: goto L_0x1068;
            case 82: goto L_0x103f;
            case 83: goto L_0x1014;
            case 84: goto L_0x0ffa;
            case 85: goto L_0x0fa5;
            case 86: goto L_0x0f5a;
            case 87: goto L_0x0f0f;
            case 88: goto L_0x0ec4;
            case 89: goto L_0x0e79;
            case 90: goto L_0x0e2e;
            case 91: goto L_0x0d77;
            case 92: goto L_0x0d2c;
            case 93: goto L_0x0cd7;
            case 94: goto L_0x0CLASSNAME;
            case 95: goto L_0x0CLASSNAME;
            case 96: goto L_0x0be8;
            case 97: goto L_0x0ba2;
            case 98: goto L_0x0b59;
            case 99: goto L_0x0b10;
            case 100: goto L_0x0ac7;
            case 101: goto L_0x0a7e;
            case 102: goto L_0x0a62;
            case 103: goto L_0x0a5e;
            case 104: goto L_0x0a5e;
            case 105: goto L_0x0a5e;
            case 106: goto L_0x0a5e;
            case 107: goto L_0x0a5e;
            case 108: goto L_0x0a5e;
            case 109: goto L_0x0a5e;
            case 110: goto L_0x0a5e;
            case 111: goto L_0x0a5e;
            default: goto L_0x0a58;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0a58, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0a5e, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x0a80, code lost:
        if (r3 <= 0) goto L_0x0a9a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0a82, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:679:0x0a9a, code lost:
        if (r6 == false) goto L_0x0ab4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0a9c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0ab4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0ac9, code lost:
        if (r3 <= 0) goto L_0x0ae3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0acb, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:685:0x0ae3, code lost:
        if (r6 == false) goto L_0x0afd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0ae5, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x0afd, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0b12, code lost:
        if (r3 <= 0) goto L_0x0b2c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0b14, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x0b2c, code lost:
        if (r6 == false) goto L_0x0b46;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0b2e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0b46, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0b5b, code lost:
        if (r3 <= 0) goto L_0x0b75;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0b5d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0b75, code lost:
        if (r6 == false) goto L_0x0b8f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0b77, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0b8f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0ba4, code lost:
        if (r3 <= 0) goto L_0x0bbd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0ba6, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0bbd, code lost:
        if (r6 == false) goto L_0x0bd6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0bbf, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0bd6, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0bea, code lost:
        if (r3 <= 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0bec, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0CLASSNAME, code lost:
        if (r6 == false) goto L_0x0c1c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0c1c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0c2d, code lost:
        r2 = r1;
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0CLASSNAME, code lost:
        if (r3 <= 0) goto L_0x0c4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0c4d, code lost:
        if (r6 == false) goto L_0x0c6b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0c4f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0c6b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0CLASSNAME, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0CLASSNAME, code lost:
        if (r3 <= 0) goto L_0x0ca0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:0x0CLASSNAME, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0ca0, code lost:
        if (r6 == false) goto L_0x0cbf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0ca2, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0cbf, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0cd7, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0cdb, code lost:
        if (r3 <= 0) goto L_0x0cf5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:727:0x0cdd, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0cf5, code lost:
        if (r6 == false) goto L_0x0d14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0cf7, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0d14, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x0d2c, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0d30, code lost:
        if (r3 <= 0) goto L_0x0d4a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0d32, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0d4a, code lost:
        if (r6 == false) goto L_0x0d64;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x0d4c, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0d64, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0d77, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0d7b, code lost:
        if (r3 <= 0) goto L_0x0db4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0d7f, code lost:
        if (r12.length <= 1) goto L_0x0da1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0d87, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x0da1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x0d89, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x0da1, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x0db4, code lost:
        if (r6 == false) goto L_0x0df7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x0db8, code lost:
        if (r12.length <= 2) goto L_0x0ddf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0dc0, code lost:
        if (android.text.TextUtils.isEmpty(r12[2]) != false) goto L_0x0ddf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x0dc2, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r12[0], r12[2], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x0ddf, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:0x0df9, code lost:
        if (r12.length <= 1) goto L_0x0e1b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x0e01, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x0e1b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x0e03, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x0e1b, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x0e2e, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x0e32, code lost:
        if (r3 <= 0) goto L_0x0e4c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x0e34, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x0e4c, code lost:
        if (r6 == false) goto L_0x0e66;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x0e4e, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x0e66, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x0e79, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:0x0e7d, code lost:
        if (r3 <= 0) goto L_0x0e97;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x0e7f, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x0e97, code lost:
        if (r6 == false) goto L_0x0eb1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x0e99, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x0eb1, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x0ec4, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x0ec8, code lost:
        if (r3 <= 0) goto L_0x0ee2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x0eca, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x0ee2, code lost:
        if (r6 == false) goto L_0x0efc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x0ee4, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x0efc, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x0f0f, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x0var_, code lost:
        if (r3 <= 0) goto L_0x0f2d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x0f2d, code lost:
        if (r6 == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x0f2f, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x0f5a, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x0f5e, code lost:
        if (r3 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x0var_, code lost:
        if (r6 == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x0f7a, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x0fa5, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x0fa9, code lost:
        if (r3 <= 0) goto L_0x0fc3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x0fab, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x0fc3, code lost:
        if (r6 == false) goto L_0x0fe2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x0fc5, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x0fe2, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x0ffa, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x1014, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x103f, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x1068, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x1091, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x10ba, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r12[0], r12[1], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt(r12[2]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x10e5, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x10ff, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x1119, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x1133, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x114d, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x1167, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x1181, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupEndedCall", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x119b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x11ba, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x11d4, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x11f3, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x120d, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x1227, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r12[0], r12[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x1241, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x1269, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r12[0], r12[1], r12[2], r12[3]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x128d, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x12b5, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x12d8, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x12fb, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x131e, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x1346, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("PollQuiz", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x136e, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r12[0], r12[1], r12[2]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x1396, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x13b9, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x13bd, code lost:
        if (r12.length <= 2) goto L_0x13ff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x13c5, code lost:
        if (android.text.TextUtils.isEmpty(r12[2]) != false) goto L_0x13ff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x13c7, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r12[0], r12[1], r12[2]);
        r5 = r12[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x13ff, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r12[0], r12[1]);
        r5 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x1432, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x1455, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x1478, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x149b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x14be, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x14e1, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r12[0], r12[1], r12[2]);
        r5 = r12[2];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x1502, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x1517, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x153d, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x1561, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x1585, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x15a9, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x15d3, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x15f1, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x160f, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x162d, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x164b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x166e, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x1691, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x16b4, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x16d2, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x16d6, code lost:
        if (r12.length <= 1) goto L_0x1713;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x16de, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x1713;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x16e0, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r12[0], r12[1]);
        r5 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x1713, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x172d, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:856:0x174b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x1769, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x1787, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x17a5, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x17c3, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x17d6, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:862:0x17d9, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:863:0x17fe, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:864:0x1821, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:865:0x1844, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:866:0x1868, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r12[0], org.telegram.messenger.LocaleController.formatPluralString("messages", org.telegram.messenger.Utilities.parseInt(r12[1]).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x188e, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x18b1, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r12[0], r12[1], r12[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x18d0, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x18f3, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:871:0x1911, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x192f, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x194d, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x1970, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x1993, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r12[0], r12[1]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x19b6, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x19d4, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x19d8, code lost:
        if (r12.length <= 1) goto L_0x1a15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x19e0, code lost:
        if (android.text.TextUtils.isEmpty(r12[1]) != false) goto L_0x1a15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x19e2, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r12[0], r12[1]);
        r5 = r12[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x1a15, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x1a2f, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x1a4d, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:0x1a6b, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r12[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x1a81, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x1a9f, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x1abc, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x1ad9, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x1af6, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r12[0]);
        r5 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x1b12, code lost:
        r16 = r5;
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x1b16, code lost:
        r1 = r18;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r12[0], r12[1]);
        r5 = r12[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x1b31, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1b47;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x1b33, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x1b47, code lost:
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x1b48, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:897:0x1b49, code lost:
        r16 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x1b4b, code lost:
        if (r2 == null) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:?, code lost:
        r6 = new org.telegram.tgnet.TLRPC$TL_message();
        r6.id = r1;
        r6.random_id = r39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x1b58, code lost:
        if (r16 == null) goto L_0x1b5d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x1b5a, code lost:
        r1 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:0x1b5d, code lost:
        r1 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:0x1b5e, code lost:
        r6.message = r1;
        r6.date = (int) (r45 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x1b67, code lost:
        if (r33 == false) goto L_0x1b70;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:?, code lost:
        r6.action = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x1b70, code lost:
        if (r32 == false) goto L_0x1b79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:909:0x1b72, code lost:
        r6.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:?, code lost:
        r6.dialog_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:0x1b7b, code lost:
        if (r38 == 0) goto L_0x1b8b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:?, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r6.peer_id = r1;
        r1.channel_id = r38;
        r3 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:0x1b8b, code lost:
        if (r24 == 0) goto L_0x1b99;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x1b8d, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r6.peer_id = r1;
        r3 = r24;
        r1.chat_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:917:0x1b99, code lost:
        r3 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:?, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r6.peer_id = r1;
        r1.user_id = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:920:0x1ba6, code lost:
        r6.flags |= 256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x1bac, code lost:
        if (r31 == 0) goto L_0x1bb8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:?, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r6.from_id = r1;
        r1.chat_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x1bb8, code lost:
        if (r28 == 0) goto L_0x1bc6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x1bba, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r6.from_id = r1;
        r1.channel_id = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x1bc6, code lost:
        if (r25 == 0) goto L_0x1bd4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x1bc8, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r6.from_id = r1;
        r1.user_id = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:?, code lost:
        r6.from_id = r6.peer_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x1bd8, code lost:
        if (r26 != false) goto L_0x1bdf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x1bda, code lost:
        if (r33 == false) goto L_0x1bdd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x1bdd, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x1bdf, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:935:0x1be0, code lost:
        r6.mentioned = r1;
        r6.silent = r27;
        r6.from_scheduled = r22;
        r19 = new org.telegram.messenger.MessageObject(r30, r6, r2, r34, r37, r5, r36, r32, r35);
        r2 = new java.util.ArrayList();
        r2.add(r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:0x1c0f, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x1CLASSNAME, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:0x1c1c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:0x1c1d, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:0x1CLASSNAME, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:943:0x1CLASSNAME, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:944:0x1CLASSNAME, code lost:
        r3 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:0x1CLASSNAME, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:946:0x1CLASSNAME, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x1c2a, code lost:
        r3 = r1;
        r29 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x1c2d, code lost:
        r30 = r15;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:0x1c2f, code lost:
        r8 = true;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x1CLASSNAME, code lost:
        if (r8 == false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x1CLASSNAME, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x1CLASSNAME, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r30);
        org.telegram.tgnet.ConnectionsManager.getInstance(r30).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x1CLASSNAME, code lost:
        r0 = th;
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x1c4c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:0x1c4d, code lost:
        r3 = r1;
        r29 = r14;
        r30 = r15;
        r1 = r0;
        r4 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:957:0x1CLASSNAME, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:0x1CLASSNAME, code lost:
        r3 = r1;
        r29 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x1c5a, code lost:
        r30 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x1c5e, code lost:
        r3 = r1;
        r29 = r7;
        r30 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:0x1CLASSNAME, code lost:
        r15 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:965:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.$$Lambda$GcmPushListenerService$jVvuwILdG7mFvHrODBdZ4swec(r15));
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:966:0x1CLASSNAME, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:967:0x1CLASSNAME, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:968:0x1CLASSNAME, code lost:
        r15 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:969:0x1c7a, code lost:
        r29 = r7;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.$$Lambda$GcmPushListenerService$QuXd6CaqKEryz6R8VHHhEnJAw(r15));
        r1.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:0x1c8a, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:971:0x1c8b, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:972:0x1cd3, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:0x1cd4, code lost:
        r3 = r1;
        r29 = r7;
        r1 = r11.getInt("dc");
        r2 = r11.getString("addr").split(":");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:974:0x1ceb, code lost:
        if (r2.length == 2) goto L_0x1cf3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:975:0x1ced, code lost:
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:976:0x1cf2, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:977:0x1cf3, code lost:
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).applyDatacenterAddress(r1, r2[0], java.lang.Integer.parseInt(r2[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
        r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:978:0x1d10, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:979:0x1d11, code lost:
        r0 = th;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:992:0x1d3a  */
    /* JADX WARNING: Removed duplicated region for block: B:993:0x1d4a  */
    /* JADX WARNING: Removed duplicated region for block: B:996:0x1d51  */
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
            java.lang.Object r5 = r2.get(r5)     // Catch:{ all -> 0x1d31 }
            boolean r6 = r5 instanceof java.lang.String     // Catch:{ all -> 0x1d31 }
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
            goto L_0x1d38
        L_0x002d:
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ all -> 0x1d31 }
            r6 = 8
            byte[] r5 = android.util.Base64.decode(r5, r6)     // Catch:{ all -> 0x1d31 }
            org.telegram.tgnet.NativeByteBuffer r7 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x1d31 }
            int r8 = r5.length     // Catch:{ all -> 0x1d31 }
            r7.<init>((int) r8)     // Catch:{ all -> 0x1d31 }
            r7.writeBytes((byte[]) r5)     // Catch:{ all -> 0x1d31 }
            r8 = 0
            r7.position(r8)     // Catch:{ all -> 0x1d31 }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1d31 }
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
            byte[] r9 = new byte[r6]     // Catch:{ all -> 0x1d31 }
            r10 = 1
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1d31 }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1d31 }
            boolean r11 = java.util.Arrays.equals(r11, r9)     // Catch:{ all -> 0x1d31 }
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
            byte[] r9 = new byte[r9]     // Catch:{ all -> 0x1d31 }
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1d31 }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1d31 }
            org.telegram.messenger.MessageKeyData r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13)     // Catch:{ all -> 0x1d31 }
            java.nio.ByteBuffer r14 = r7.buffer     // Catch:{ all -> 0x1d31 }
            byte[] r15 = r11.aesKey     // Catch:{ all -> 0x1d31 }
            byte[] r11 = r11.aesIv     // Catch:{ all -> 0x1d31 }
            r17 = 0
            r18 = 0
            r19 = 24
            int r5 = r5.length     // Catch:{ all -> 0x1d31 }
            int r20 = r5 + -24
            r16 = r11
            org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x1d31 }
            byte[] r21 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1d31 }
            r22 = 96
            r23 = 32
            java.nio.ByteBuffer r5 = r7.buffer     // Catch:{ all -> 0x1d31 }
            r25 = 24
            int r26 = r5.limit()     // Catch:{ all -> 0x1d31 }
            r24 = r5
            byte[] r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26)     // Catch:{ all -> 0x1d31 }
            boolean r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6)     // Catch:{ all -> 0x1d31 }
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
            int r5 = r7.readInt32(r10)     // Catch:{ all -> 0x1d31 }
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x1d31 }
            r7.readBytes(r5, r10)     // Catch:{ all -> 0x1d31 }
            java.lang.String r7 = new java.lang.String     // Catch:{ all -> 0x1d31 }
            r7.<init>(r5)     // Catch:{ all -> 0x1d31 }
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ all -> 0x1d27 }
            r5.<init>(r7)     // Catch:{ all -> 0x1d27 }
            java.lang.String r9 = "loc_key"
            boolean r9 = r5.has(r9)     // Catch:{ all -> 0x1d27 }
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
            java.lang.Object r11 = r5.get(r11)     // Catch:{ all -> 0x1d1d }
            boolean r11 = r11 instanceof org.json.JSONObject     // Catch:{ all -> 0x1d1d }
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
            org.json.JSONObject r11 = new org.json.JSONObject     // Catch:{ all -> 0x1d1d }
            r11.<init>()     // Catch:{ all -> 0x1d1d }
        L_0x0130:
            java.lang.String r14 = "user_id"
            boolean r14 = r5.has(r14)     // Catch:{ all -> 0x1d1d }
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
            boolean r15 = r14 instanceof java.lang.Integer     // Catch:{ all -> 0x1d1d }
            if (r15 == 0) goto L_0x0158
            java.lang.Integer r14 = (java.lang.Integer) r14     // Catch:{ all -> 0x0124 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0124 }
            goto L_0x0171
        L_0x0158:
            boolean r15 = r14 instanceof java.lang.String     // Catch:{ all -> 0x1d1d }
            if (r15 == 0) goto L_0x0167
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ all -> 0x0124 }
            java.lang.Integer r14 = org.telegram.messenger.Utilities.parseInt(r14)     // Catch:{ all -> 0x0124 }
            int r14 = r14.intValue()     // Catch:{ all -> 0x0124 }
            goto L_0x0171
        L_0x0167:
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1d1d }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x1d1d }
            int r14 = r14.getClientUserId()     // Catch:{ all -> 0x1d1d }
        L_0x0171:
            int r15 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1d1d }
            r4 = 0
        L_0x0174:
            if (r4 >= r12) goto L_0x0187
            org.telegram.messenger.UserConfig r17 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x0124 }
            int r6 = r17.getClientUserId()     // Catch:{ all -> 0x0124 }
            if (r6 != r14) goto L_0x0182
            r15 = r4
            goto L_0x0187
        L_0x0182:
            int r4 = r4 + 1
            r6 = 8
            goto L_0x0174
        L_0x0187:
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r15)     // Catch:{ all -> 0x1d13 }
            boolean r4 = r4.isClientActivated()     // Catch:{ all -> 0x1d13 }
            if (r4 != 0) goto L_0x01a7
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x01a0 }
            if (r2 == 0) goto L_0x019a
            java.lang.String r2 = "GCM ACCOUNT NOT ACTIVATED"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x01a0 }
        L_0x019a:
            java.util.concurrent.CountDownLatch r2 = r1.countDownLatch     // Catch:{ all -> 0x01a0 }
            r2.countDown()     // Catch:{ all -> 0x01a0 }
            return
        L_0x01a0:
            r0 = move-exception
            r3 = r1
            r14 = r7
        L_0x01a3:
            r4 = r9
        L_0x01a4:
            r2 = -1
            goto L_0x002a
        L_0x01a7:
            java.lang.String r4 = "google.sent_time"
            r2.get(r4)     // Catch:{ all -> 0x1d13 }
            int r2 = r9.hashCode()     // Catch:{ all -> 0x1d13 }
            switch(r2) {
                case -1963663249: goto L_0x01d2;
                case -920689527: goto L_0x01c8;
                case 633004703: goto L_0x01be;
                case 1365673842: goto L_0x01b4;
                default: goto L_0x01b3;
            }
        L_0x01b3:
            goto L_0x01dc
        L_0x01b4:
            java.lang.String r2 = "GEO_LIVE_PENDING"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01a0 }
            if (r2 == 0) goto L_0x01dc
            r2 = 3
            goto L_0x01dd
        L_0x01be:
            java.lang.String r2 = "MESSAGE_ANNOUNCEMENT"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01a0 }
            if (r2 == 0) goto L_0x01dc
            r2 = 1
            goto L_0x01dd
        L_0x01c8:
            java.lang.String r2 = "DC_UPDATE"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01a0 }
            if (r2 == 0) goto L_0x01dc
            r2 = 0
            goto L_0x01dd
        L_0x01d2:
            java.lang.String r2 = "SESSION_REVOKE"
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x01a0 }
            if (r2 == 0) goto L_0x01dc
            r2 = 2
            goto L_0x01dd
        L_0x01dc:
            r2 = -1
        L_0x01dd:
            if (r2 == 0) goto L_0x1cd4
            if (r2 == r10) goto L_0x1c8b
            if (r2 == r13) goto L_0x1c7a
            if (r2 == r12) goto L_0x1c5e
            java.lang.String r2 = "channel_id"
            boolean r2 = r11.has(r2)     // Catch:{ all -> 0x1CLASSNAME }
            r19 = 0
            if (r2 == 0) goto L_0x01f8
            java.lang.String r2 = "channel_id"
            int r2 = r11.getInt(r2)     // Catch:{ all -> 0x01a0 }
            int r4 = -r2
            long r3 = (long) r4
            goto L_0x01fb
        L_0x01f8:
            r3 = r19
            r2 = 0
        L_0x01fb:
            java.lang.String r14 = "from_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1CLASSNAME }
            if (r14 == 0) goto L_0x0215
            java.lang.String r3 = "from_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x0211 }
            r14 = r7
            long r6 = (long) r3
            r41 = r6
            r6 = r3
            r3 = r41
            goto L_0x0217
        L_0x0211:
            r0 = move-exception
            r14 = r7
        L_0x0213:
            r3 = r1
            goto L_0x01a3
        L_0x0215:
            r14 = r7
            r6 = 0
        L_0x0217:
            java.lang.String r7 = "chat_id"
            boolean r7 = r11.has(r7)     // Catch:{ all -> 0x1c4c }
            if (r7 == 0) goto L_0x022f
            java.lang.String r3 = "chat_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x022d }
            int r4 = -r3
            long r12 = (long) r4
            r41 = r12
            r12 = r3
            r3 = r41
            goto L_0x0230
        L_0x022d:
            r0 = move-exception
            goto L_0x0213
        L_0x022f:
            r12 = 0
        L_0x0230:
            java.lang.String r13 = "encryption_id"
            boolean r13 = r11.has(r13)     // Catch:{ all -> 0x1c4c }
            if (r13 == 0) goto L_0x0242
            java.lang.String r3 = "encryption_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x022d }
            long r3 = (long) r3
            r13 = 32
            long r3 = r3 << r13
        L_0x0242:
            java.lang.String r13 = "schedule"
            boolean r13 = r11.has(r13)     // Catch:{ all -> 0x1c4c }
            if (r13 == 0) goto L_0x0254
            java.lang.String r13 = "schedule"
            int r13 = r11.getInt(r13)     // Catch:{ all -> 0x022d }
            if (r13 != r10) goto L_0x0254
            r13 = 1
            goto L_0x0255
        L_0x0254:
            r13 = 0
        L_0x0255:
            int r21 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r21 != 0) goto L_0x0266
            java.lang.String r7 = "ENCRYPTED_MESSAGE"
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x022d }
            if (r7 == 0) goto L_0x0266
            r3 = -4294967296(0xfffffffvar_, double:NaN)
        L_0x0266:
            int r7 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r7 == 0) goto L_0x1c2a
            java.lang.String r7 = "READ_HISTORY"
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x1c4c }
            java.lang.String r10 = " for dialogId = "
            if (r7 == 0) goto L_0x02de
            java.lang.String r5 = "max_id"
            int r5 = r11.getInt(r5)     // Catch:{ all -> 0x022d }
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ all -> 0x022d }
            r7.<init>()     // Catch:{ all -> 0x022d }
            boolean r8 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x022d }
            if (r8 == 0) goto L_0x029d
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x022d }
            r8.<init>()     // Catch:{ all -> 0x022d }
            java.lang.String r11 = "GCM received read notification max_id = "
            r8.append(r11)     // Catch:{ all -> 0x022d }
            r8.append(r5)     // Catch:{ all -> 0x022d }
            r8.append(r10)     // Catch:{ all -> 0x022d }
            r8.append(r3)     // Catch:{ all -> 0x022d }
            java.lang.String r3 = r8.toString()     // Catch:{ all -> 0x022d }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ all -> 0x022d }
        L_0x029d:
            if (r2 == 0) goto L_0x02ac
            org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox r3 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox     // Catch:{ all -> 0x022d }
            r3.<init>()     // Catch:{ all -> 0x022d }
            r3.channel_id = r2     // Catch:{ all -> 0x022d }
            r3.max_id = r5     // Catch:{ all -> 0x022d }
            r7.add(r3)     // Catch:{ all -> 0x022d }
            goto L_0x02cb
        L_0x02ac:
            org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox r2 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox     // Catch:{ all -> 0x022d }
            r2.<init>()     // Catch:{ all -> 0x022d }
            if (r6 == 0) goto L_0x02bd
            org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x022d }
            r3.<init>()     // Catch:{ all -> 0x022d }
            r2.peer = r3     // Catch:{ all -> 0x022d }
            r3.user_id = r6     // Catch:{ all -> 0x022d }
            goto L_0x02c6
        L_0x02bd:
            org.telegram.tgnet.TLRPC$TL_peerChat r3 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x022d }
            r3.<init>()     // Catch:{ all -> 0x022d }
            r2.peer = r3     // Catch:{ all -> 0x022d }
            r3.chat_id = r12     // Catch:{ all -> 0x022d }
        L_0x02c6:
            r2.max_id = r5     // Catch:{ all -> 0x022d }
            r7.add(r2)     // Catch:{ all -> 0x022d }
        L_0x02cb:
            org.telegram.messenger.MessagesController r16 = org.telegram.messenger.MessagesController.getInstance(r15)     // Catch:{ all -> 0x022d }
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r17 = r7
            r16.processUpdateArray(r17, r18, r19, r20, r21)     // Catch:{ all -> 0x022d }
            goto L_0x1c2a
        L_0x02de:
            java.lang.String r7 = "MESSAGE_DELETED"
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x1c4c }
            java.lang.String r8 = "messages"
            if (r7 == 0) goto L_0x034b
            java.lang.String r5 = r11.getString(r8)     // Catch:{ all -> 0x022d }
            java.lang.String r6 = ","
            java.lang.String[] r5 = r5.split(r6)     // Catch:{ all -> 0x022d }
            android.util.SparseArray r6 = new android.util.SparseArray     // Catch:{ all -> 0x022d }
            r6.<init>()     // Catch:{ all -> 0x022d }
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ all -> 0x022d }
            r7.<init>()     // Catch:{ all -> 0x022d }
            r8 = 0
        L_0x02fd:
            int r11 = r5.length     // Catch:{ all -> 0x022d }
            if (r8 >= r11) goto L_0x030c
            r11 = r5[r8]     // Catch:{ all -> 0x022d }
            java.lang.Integer r11 = org.telegram.messenger.Utilities.parseInt(r11)     // Catch:{ all -> 0x022d }
            r7.add(r11)     // Catch:{ all -> 0x022d }
            int r8 = r8 + 1
            goto L_0x02fd
        L_0x030c:
            r6.put(r2, r7)     // Catch:{ all -> 0x022d }
            org.telegram.messenger.NotificationsController r5 = org.telegram.messenger.NotificationsController.getInstance(r15)     // Catch:{ all -> 0x022d }
            r5.removeDeletedMessagesFromNotifications(r6)     // Catch:{ all -> 0x022d }
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r15)     // Catch:{ all -> 0x022d }
            r5.deleteMessagesByPush(r3, r7, r2)     // Catch:{ all -> 0x022d }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x022d }
            if (r2 == 0) goto L_0x1c2a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x022d }
            r2.<init>()     // Catch:{ all -> 0x022d }
            java.lang.String r5 = "GCM received "
            r2.append(r5)     // Catch:{ all -> 0x022d }
            r2.append(r9)     // Catch:{ all -> 0x022d }
            r2.append(r10)     // Catch:{ all -> 0x022d }
            r2.append(r3)     // Catch:{ all -> 0x022d }
            java.lang.String r3 = " mids = "
            r2.append(r3)     // Catch:{ all -> 0x022d }
            java.lang.String r3 = ","
            java.lang.String r3 = android.text.TextUtils.join(r3, r7)     // Catch:{ all -> 0x022d }
            r2.append(r3)     // Catch:{ all -> 0x022d }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x022d }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ all -> 0x022d }
            goto L_0x1c2a
        L_0x034b:
            boolean r7 = android.text.TextUtils.isEmpty(r9)     // Catch:{ all -> 0x1c4c }
            if (r7 != 0) goto L_0x1c2a
            java.lang.String r7 = "msg_id"
            boolean r7 = r11.has(r7)     // Catch:{ all -> 0x1c4c }
            if (r7 == 0) goto L_0x0362
            java.lang.String r7 = "msg_id"
            int r7 = r11.getInt(r7)     // Catch:{ all -> 0x022d }
            r29 = r14
            goto L_0x0365
        L_0x0362:
            r29 = r14
            r7 = 0
        L_0x0365:
            java.lang.String r14 = "random_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1CLASSNAME }
            if (r14 == 0) goto L_0x0389
            java.lang.String r14 = "random_id"
            java.lang.String r14 = r11.getString(r14)     // Catch:{ all -> 0x0382 }
            java.lang.Long r14 = org.telegram.messenger.Utilities.parseLong(r14)     // Catch:{ all -> 0x0382 }
            long r22 = r14.longValue()     // Catch:{ all -> 0x0382 }
            r41 = r22
            r22 = r13
            r13 = r41
            goto L_0x038d
        L_0x0382:
            r0 = move-exception
            r3 = r1
            r4 = r9
            r14 = r29
            goto L_0x01a4
        L_0x0389:
            r22 = r13
            r13 = r19
        L_0x038d:
            if (r7 == 0) goto L_0x03d3
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r15)     // Catch:{ all -> 0x03c9 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r1.dialogs_read_inbox_max     // Catch:{ all -> 0x03c9 }
            r23 = r6
            java.lang.Long r6 = java.lang.Long.valueOf(r3)     // Catch:{ all -> 0x03c9 }
            java.lang.Object r1 = r1.get(r6)     // Catch:{ all -> 0x03c9 }
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch:{ all -> 0x03c9 }
            if (r1 != 0) goto L_0x03c0
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r15)     // Catch:{ all -> 0x03c9 }
            r6 = 0
            int r1 = r1.getDialogReadMax(r6, r3)     // Catch:{ all -> 0x03c9 }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x03c9 }
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r15)     // Catch:{ all -> 0x03c9 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r6 = r6.dialogs_read_inbox_max     // Catch:{ all -> 0x03c9 }
            r24 = r12
            java.lang.Long r12 = java.lang.Long.valueOf(r3)     // Catch:{ all -> 0x03c9 }
            r6.put(r12, r1)     // Catch:{ all -> 0x03c9 }
            goto L_0x03c2
        L_0x03c0:
            r24 = r12
        L_0x03c2:
            int r1 = r1.intValue()     // Catch:{ all -> 0x03c9 }
            if (r7 <= r1) goto L_0x03e7
            goto L_0x03e5
        L_0x03c9:
            r0 = move-exception
            r2 = -1
            r3 = r43
            r1 = r0
            r4 = r9
            r14 = r29
            goto L_0x1d38
        L_0x03d3:
            r23 = r6
            r24 = r12
            int r1 = (r13 > r19 ? 1 : (r13 == r19 ? 0 : -1))
            if (r1 == 0) goto L_0x03e7
            org.telegram.messenger.MessagesStorage r1 = org.telegram.messenger.MessagesStorage.getInstance(r15)     // Catch:{ all -> 0x03c9 }
            boolean r1 = r1.checkMessageByRandomId(r13)     // Catch:{ all -> 0x03c9 }
            if (r1 != 0) goto L_0x03e7
        L_0x03e5:
            r1 = 1
            goto L_0x03e8
        L_0x03e7:
            r1 = 0
        L_0x03e8:
            if (r1 == 0) goto L_0x1CLASSNAME
            java.lang.String r1 = "chat_from_id"
            r6 = 0
            int r1 = r11.optInt(r1, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r12 = "chat_from_broadcast_id"
            int r12 = r11.optInt(r12, r6)     // Catch:{ all -> 0x1CLASSNAME }
            r30 = r15
            java.lang.String r15 = "chat_from_group_id"
            int r15 = r11.optInt(r15, r6)     // Catch:{ all -> 0x1c1c }
            if (r1 != 0) goto L_0x0408
            if (r15 == 0) goto L_0x0404
            goto L_0x0408
        L_0x0404:
            r25 = r1
            r6 = 0
            goto L_0x040b
        L_0x0408:
            r25 = r1
            r6 = 1
        L_0x040b:
            java.lang.String r1 = "mention"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x1c1c }
            if (r1 == 0) goto L_0x042a
            java.lang.String r1 = "mention"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x042a
            r26 = 1
            goto L_0x042c
        L_0x041e:
            r0 = move-exception
            r2 = -1
            r3 = r43
            r1 = r0
            r4 = r9
            r14 = r29
            r15 = r30
            goto L_0x1d38
        L_0x042a:
            r26 = 0
        L_0x042c:
            java.lang.String r1 = "silent"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x1c1c }
            if (r1 == 0) goto L_0x043f
            java.lang.String r1 = "silent"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x043f
            r27 = 1
            goto L_0x0441
        L_0x043f:
            r27 = 0
        L_0x0441:
            java.lang.String r1 = "loc_args"
            boolean r1 = r5.has(r1)     // Catch:{ all -> 0x1c1c }
            if (r1 == 0) goto L_0x0467
            java.lang.String r1 = "loc_args"
            org.json.JSONArray r1 = r5.getJSONArray(r1)     // Catch:{ all -> 0x041e }
            int r5 = r1.length()     // Catch:{ all -> 0x041e }
            r28 = r12
            java.lang.String[] r12 = new java.lang.String[r5]     // Catch:{ all -> 0x041e }
            r31 = r15
            r15 = 0
        L_0x045a:
            if (r15 >= r5) goto L_0x0465
            java.lang.String r32 = r1.getString(r15)     // Catch:{ all -> 0x041e }
            r12[r15] = r32     // Catch:{ all -> 0x041e }
            int r15 = r15 + 1
            goto L_0x045a
        L_0x0465:
            r1 = 0
            goto L_0x046d
        L_0x0467:
            r28 = r12
            r31 = r15
            r1 = 0
            r12 = 0
        L_0x046d:
            r5 = r12[r1]     // Catch:{ all -> 0x1c1c }
            java.lang.String r1 = "edit_date"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x1c1c }
            java.lang.String r11 = "CHAT_"
            boolean r11 = r9.startsWith(r11)     // Catch:{ all -> 0x1c1c }
            if (r11 == 0) goto L_0x04ae
            boolean r11 = org.telegram.messenger.UserObject.isReplyUser((long) r3)     // Catch:{ all -> 0x041e }
            if (r11 == 0) goto L_0x049b
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x041e }
            r11.<init>()     // Catch:{ all -> 0x041e }
            r11.append(r5)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = " @ "
            r11.append(r5)     // Catch:{ all -> 0x041e }
            r5 = 1
            r15 = r12[r5]     // Catch:{ all -> 0x041e }
            r11.append(r15)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = r11.toString()     // Catch:{ all -> 0x041e }
            goto L_0x04cd
        L_0x049b:
            if (r2 == 0) goto L_0x049f
            r11 = 1
            goto L_0x04a0
        L_0x049f:
            r11 = 0
        L_0x04a0:
            r15 = 1
            r32 = r12[r15]     // Catch:{ all -> 0x041e }
            r15 = 0
            r33 = 0
            r41 = r11
            r11 = r5
            r5 = r32
            r32 = r41
            goto L_0x04d3
        L_0x04ae:
            java.lang.String r11 = "PINNED_"
            boolean r11 = r9.startsWith(r11)     // Catch:{ all -> 0x1c1c }
            if (r11 == 0) goto L_0x04c2
            if (r2 == 0) goto L_0x04ba
            r11 = 1
            goto L_0x04bb
        L_0x04ba:
            r11 = 0
        L_0x04bb:
            r32 = r11
            r11 = 0
            r15 = 0
            r33 = 1
            goto L_0x04d3
        L_0x04c2:
            java.lang.String r11 = "CHANNEL_"
            boolean r11 = r9.startsWith(r11)     // Catch:{ all -> 0x1c1c }
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
            boolean r34 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1c1c }
            if (r34 == 0) goto L_0x04fe
            r34 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x041e }
            r5.<init>()     // Catch:{ all -> 0x041e }
            r35 = r1
            java.lang.String r1 = "GCM received message notification "
            r5.append(r1)     // Catch:{ all -> 0x041e }
            r5.append(r9)     // Catch:{ all -> 0x041e }
            r5.append(r10)     // Catch:{ all -> 0x041e }
            r5.append(r3)     // Catch:{ all -> 0x041e }
            java.lang.String r1 = " mid = "
            r5.append(r1)     // Catch:{ all -> 0x041e }
            r5.append(r7)     // Catch:{ all -> 0x041e }
            java.lang.String r1 = r5.toString()     // Catch:{ all -> 0x041e }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x041e }
            goto L_0x0502
        L_0x04fe:
            r35 = r1
            r34 = r5
        L_0x0502:
            int r1 = r9.hashCode()     // Catch:{ all -> 0x1c1c }
            switch(r1) {
                case -2100047043: goto L_0x0a2f;
                case -2091498420: goto L_0x0a24;
                case -2053872415: goto L_0x0a19;
                case -2039746363: goto L_0x0a0e;
                case -2023218804: goto L_0x0a03;
                case -1979538588: goto L_0x09f8;
                case -1979536003: goto L_0x09ed;
                case -1979535888: goto L_0x09e2;
                case -1969004705: goto L_0x09d7;
                case -1946699248: goto L_0x09cb;
                case -1646640058: goto L_0x09bf;
                case -1528047021: goto L_0x09b3;
                case -1493579426: goto L_0x09a7;
                case -1482481933: goto L_0x099b;
                case -1480102982: goto L_0x0990;
                case -1478041834: goto L_0x0984;
                case -1474543101: goto L_0x0979;
                case -1465695932: goto L_0x096d;
                case -1374906292: goto L_0x0961;
                case -1372940586: goto L_0x0955;
                case -1264245338: goto L_0x0949;
                case -1236154001: goto L_0x093d;
                case -1236086700: goto L_0x0931;
                case -1236077786: goto L_0x0925;
                case -1235796237: goto L_0x0919;
                case -1235760759: goto L_0x090d;
                case -1235686303: goto L_0x0902;
                case -1198046100: goto L_0x08f7;
                case -1124254527: goto L_0x08eb;
                case -1085137927: goto L_0x08df;
                case -1084856378: goto L_0x08d3;
                case -1084820900: goto L_0x08c7;
                case -1084746444: goto L_0x08bb;
                case -819729482: goto L_0x08af;
                case -772141857: goto L_0x08a3;
                case -638310039: goto L_0x0897;
                case -590403924: goto L_0x088b;
                case -589196239: goto L_0x087f;
                case -589193654: goto L_0x0873;
                case -589193539: goto L_0x0867;
                case -440169325: goto L_0x085b;
                case -412748110: goto L_0x084f;
                case -228518075: goto L_0x0843;
                case -213586509: goto L_0x0837;
                case -115582002: goto L_0x082b;
                case -112621464: goto L_0x081f;
                case -108522133: goto L_0x0813;
                case -107572034: goto L_0x0808;
                case -40534265: goto L_0x07fc;
                case 65254746: goto L_0x07f0;
                case 141040782: goto L_0x07e4;
                case 202550149: goto L_0x07d8;
                case 309993049: goto L_0x07cc;
                case 309995634: goto L_0x07c0;
                case 309995749: goto L_0x07b4;
                case 320532812: goto L_0x07a8;
                case 328933854: goto L_0x079c;
                case 331340546: goto L_0x0790;
                case 342406591: goto L_0x0784;
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
            goto L_0x0a3a
        L_0x050b:
            java.lang.String r1 = "CHAT_MESSAGE_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 60
            goto L_0x0a3b
        L_0x0517:
            java.lang.String r1 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 43
            goto L_0x0a3b
        L_0x0523:
            java.lang.String r1 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 28
            goto L_0x0a3b
        L_0x052f:
            java.lang.String r1 = "CHANNEL_MESSAGE_PLAYLIST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 45
            goto L_0x0a3b
        L_0x053b:
            java.lang.String r1 = "PINNED_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 93
            goto L_0x0a3b
        L_0x0547:
            java.lang.String r1 = "CHAT_PHOTO_EDITED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 68
            goto L_0x0a3b
        L_0x0553:
            java.lang.String r1 = "LOCKED_MESSAGE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 106(0x6a, float:1.49E-43)
            goto L_0x0a3b
        L_0x055f:
            java.lang.String r1 = "CHAT_MESSAGE_PLAYLIST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 82
            goto L_0x0a3b
        L_0x056b:
            java.lang.String r1 = "CHANNEL_MESSAGES"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 47
            goto L_0x0a3b
        L_0x0577:
            java.lang.String r1 = "MESSAGE_INVOICE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 21
            goto L_0x0a3b
        L_0x0583:
            java.lang.String r1 = "CHAT_MESSAGE_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 51
            goto L_0x0a3b
        L_0x058f:
            java.lang.String r1 = "CHAT_MESSAGE_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 52
            goto L_0x0a3b
        L_0x059b:
            java.lang.String r1 = "CHAT_MESSAGE_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 50
            goto L_0x0a3b
        L_0x05a7:
            java.lang.String r1 = "CHAT_MESSAGE_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 55
            goto L_0x0a3b
        L_0x05b3:
            java.lang.String r1 = "MESSAGE_PLAYLIST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 25
            goto L_0x0a3b
        L_0x05bf:
            java.lang.String r1 = "MESSAGE_VIDEOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 24
            goto L_0x0a3b
        L_0x05cb:
            java.lang.String r1 = "PHONE_CALL_MISSED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 111(0x6f, float:1.56E-43)
            goto L_0x0a3b
        L_0x05d7:
            java.lang.String r1 = "MESSAGE_PHOTOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 23
            goto L_0x0a3b
        L_0x05e3:
            java.lang.String r1 = "CHAT_MESSAGE_VIDEOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 81
            goto L_0x0a3b
        L_0x05ef:
            java.lang.String r1 = "MESSAGE_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 2
            goto L_0x0a3b
        L_0x05fa:
            java.lang.String r1 = "MESSAGE_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 17
            goto L_0x0a3b
        L_0x0606:
            java.lang.String r1 = "MESSAGE_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 15
            goto L_0x0a3b
        L_0x0612:
            java.lang.String r1 = "MESSAGE_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 9
            goto L_0x0a3b
        L_0x061e:
            java.lang.String r1 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 63
            goto L_0x0a3b
        L_0x062a:
            java.lang.String r1 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 39
            goto L_0x0a3b
        L_0x0636:
            java.lang.String r1 = "CHAT_MESSAGE_PHOTOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 80
            goto L_0x0a3b
        L_0x0642:
            java.lang.String r1 = "CHAT_MESSAGE_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 49
            goto L_0x0a3b
        L_0x064e:
            java.lang.String r1 = "CHAT_TITLE_EDITED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 67
            goto L_0x0a3b
        L_0x065a:
            java.lang.String r1 = "PINNED_NOTEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 86
            goto L_0x0a3b
        L_0x0666:
            java.lang.String r1 = "MESSAGE_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 0
            goto L_0x0a3b
        L_0x0671:
            java.lang.String r1 = "MESSAGE_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 13
            goto L_0x0a3b
        L_0x067d:
            java.lang.String r1 = "MESSAGE_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 14
            goto L_0x0a3b
        L_0x0689:
            java.lang.String r1 = "MESSAGE_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 18
            goto L_0x0a3b
        L_0x0695:
            java.lang.String r1 = "MESSAGE_FWDS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 22
            goto L_0x0a3b
        L_0x06a1:
            java.lang.String r1 = "MESSAGE_DOCS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 26
            goto L_0x0a3b
        L_0x06ad:
            java.lang.String r1 = "CHAT_MESSAGE_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 48
            goto L_0x0a3b
        L_0x06b9:
            java.lang.String r1 = "CHAT_MESSAGE_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 57
            goto L_0x0a3b
        L_0x06c5:
            java.lang.String r1 = "CHAT_MESSAGE_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 58
            goto L_0x0a3b
        L_0x06d1:
            java.lang.String r1 = "CHAT_MESSAGE_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 62
            goto L_0x0a3b
        L_0x06dd:
            java.lang.String r1 = "CHAT_MESSAGE_FWDS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 79
            goto L_0x0a3b
        L_0x06e9:
            java.lang.String r1 = "CHAT_MESSAGE_DOCS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 83
            goto L_0x0a3b
        L_0x06f5:
            java.lang.String r1 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 20
            goto L_0x0a3b
        L_0x0701:
            java.lang.String r1 = "PINNED_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 97
            goto L_0x0a3b
        L_0x070d:
            java.lang.String r1 = "MESSAGE_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 12
            goto L_0x0a3b
        L_0x0719:
            java.lang.String r1 = "PINNED_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 88
            goto L_0x0a3b
        L_0x0725:
            java.lang.String r1 = "PINNED_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 89
            goto L_0x0a3b
        L_0x0731:
            java.lang.String r1 = "PINNED_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 87
            goto L_0x0a3b
        L_0x073d:
            java.lang.String r1 = "PINNED_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 92
            goto L_0x0a3b
        L_0x0749:
            java.lang.String r1 = "MESSAGE_PHOTO_SECRET"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 4
            goto L_0x0a3b
        L_0x0754:
            java.lang.String r1 = "CHAT_VOICECHAT_INVITE_YOU"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 73
            goto L_0x0a3b
        L_0x0760:
            java.lang.String r1 = "CHANNEL_MESSAGE_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 30
            goto L_0x0a3b
        L_0x076c:
            java.lang.String r1 = "CHANNEL_MESSAGE_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 31
            goto L_0x0a3b
        L_0x0778:
            java.lang.String r1 = "CHANNEL_MESSAGE_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 29
            goto L_0x0a3b
        L_0x0784:
            java.lang.String r1 = "CHAT_VOICECHAT_END"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 72
            goto L_0x0a3b
        L_0x0790:
            java.lang.String r1 = "CHANNEL_MESSAGE_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 34
            goto L_0x0a3b
        L_0x079c:
            java.lang.String r1 = "CHAT_MESSAGE_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 54
            goto L_0x0a3b
        L_0x07a8:
            java.lang.String r1 = "MESSAGES"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 27
            goto L_0x0a3b
        L_0x07b4:
            java.lang.String r1 = "CHAT_MESSAGE_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 61
            goto L_0x0a3b
        L_0x07c0:
            java.lang.String r1 = "CHAT_MESSAGE_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 59
            goto L_0x0a3b
        L_0x07cc:
            java.lang.String r1 = "CHAT_MESSAGE_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 53
            goto L_0x0a3b
        L_0x07d8:
            java.lang.String r1 = "CHAT_VOICECHAT_INVITE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 71
            goto L_0x0a3b
        L_0x07e4:
            java.lang.String r1 = "CHAT_LEFT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 76
            goto L_0x0a3b
        L_0x07f0:
            java.lang.String r1 = "CHAT_ADD_YOU"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 66
            goto L_0x0a3b
        L_0x07fc:
            java.lang.String r1 = "CHAT_DELETE_MEMBER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 74
            goto L_0x0a3b
        L_0x0808:
            java.lang.String r1 = "MESSAGE_SCREENSHOT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 7
            goto L_0x0a3b
        L_0x0813:
            java.lang.String r1 = "AUTH_REGION"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 105(0x69, float:1.47E-43)
            goto L_0x0a3b
        L_0x081f:
            java.lang.String r1 = "CONTACT_JOINED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 103(0x67, float:1.44E-43)
            goto L_0x0a3b
        L_0x082b:
            java.lang.String r1 = "CHAT_MESSAGE_INVOICE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 64
            goto L_0x0a3b
        L_0x0837:
            java.lang.String r1 = "ENCRYPTION_REQUEST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 107(0x6b, float:1.5E-43)
            goto L_0x0a3b
        L_0x0843:
            java.lang.String r1 = "MESSAGE_GEOLIVE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 16
            goto L_0x0a3b
        L_0x084f:
            java.lang.String r1 = "CHAT_DELETE_YOU"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 75
            goto L_0x0a3b
        L_0x085b:
            java.lang.String r1 = "AUTH_UNKNOWN"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 104(0x68, float:1.46E-43)
            goto L_0x0a3b
        L_0x0867:
            java.lang.String r1 = "PINNED_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 101(0x65, float:1.42E-43)
            goto L_0x0a3b
        L_0x0873:
            java.lang.String r1 = "PINNED_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 96
            goto L_0x0a3b
        L_0x087f:
            java.lang.String r1 = "PINNED_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 90
            goto L_0x0a3b
        L_0x088b:
            java.lang.String r1 = "PINNED_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 99
            goto L_0x0a3b
        L_0x0897:
            java.lang.String r1 = "CHANNEL_MESSAGE_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 33
            goto L_0x0a3b
        L_0x08a3:
            java.lang.String r1 = "PHONE_CALL_REQUEST"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 109(0x6d, float:1.53E-43)
            goto L_0x0a3b
        L_0x08af:
            java.lang.String r1 = "PINNED_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 91
            goto L_0x0a3b
        L_0x08bb:
            java.lang.String r1 = "PINNED_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 85
            goto L_0x0a3b
        L_0x08c7:
            java.lang.String r1 = "PINNED_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 94
            goto L_0x0a3b
        L_0x08d3:
            java.lang.String r1 = "PINNED_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 95
            goto L_0x0a3b
        L_0x08df:
            java.lang.String r1 = "PINNED_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 98
            goto L_0x0a3b
        L_0x08eb:
            java.lang.String r1 = "CHAT_MESSAGE_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 56
            goto L_0x0a3b
        L_0x08f7:
            java.lang.String r1 = "MESSAGE_VIDEO_SECRET"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 6
            goto L_0x0a3b
        L_0x0902:
            java.lang.String r1 = "CHANNEL_MESSAGE_TEXT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 1
            goto L_0x0a3b
        L_0x090d:
            java.lang.String r1 = "CHANNEL_MESSAGE_QUIZ"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 36
            goto L_0x0a3b
        L_0x0919:
            java.lang.String r1 = "CHANNEL_MESSAGE_POLL"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 37
            goto L_0x0a3b
        L_0x0925:
            java.lang.String r1 = "CHANNEL_MESSAGE_GAME"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 41
            goto L_0x0a3b
        L_0x0931:
            java.lang.String r1 = "CHANNEL_MESSAGE_FWDS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 42
            goto L_0x0a3b
        L_0x093d:
            java.lang.String r1 = "CHANNEL_MESSAGE_DOCS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 46
            goto L_0x0a3b
        L_0x0949:
            java.lang.String r1 = "PINNED_INVOICE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 100
            goto L_0x0a3b
        L_0x0955:
            java.lang.String r1 = "CHAT_RETURNED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 77
            goto L_0x0a3b
        L_0x0961:
            java.lang.String r1 = "ENCRYPTED_MESSAGE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 102(0x66, float:1.43E-43)
            goto L_0x0a3b
        L_0x096d:
            java.lang.String r1 = "ENCRYPTION_ACCEPT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 108(0x6c, float:1.51E-43)
            goto L_0x0a3b
        L_0x0979:
            java.lang.String r1 = "MESSAGE_VIDEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 5
            goto L_0x0a3b
        L_0x0984:
            java.lang.String r1 = "MESSAGE_ROUND"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 8
            goto L_0x0a3b
        L_0x0990:
            java.lang.String r1 = "MESSAGE_PHOTO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 3
            goto L_0x0a3b
        L_0x099b:
            java.lang.String r1 = "MESSAGE_MUTED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 110(0x6e, float:1.54E-43)
            goto L_0x0a3b
        L_0x09a7:
            java.lang.String r1 = "MESSAGE_AUDIO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 11
            goto L_0x0a3b
        L_0x09b3:
            java.lang.String r1 = "CHAT_MESSAGES"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 84
            goto L_0x0a3b
        L_0x09bf:
            java.lang.String r1 = "CHAT_VOICECHAT_START"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 70
            goto L_0x0a3b
        L_0x09cb:
            java.lang.String r1 = "CHAT_JOINED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 78
            goto L_0x0a3b
        L_0x09d7:
            java.lang.String r1 = "CHAT_ADD_MEMBER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 69
            goto L_0x0a3b
        L_0x09e2:
            java.lang.String r1 = "CHANNEL_MESSAGE_GIF"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 40
            goto L_0x0a3b
        L_0x09ed:
            java.lang.String r1 = "CHANNEL_MESSAGE_GEO"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 38
            goto L_0x0a3b
        L_0x09f8:
            java.lang.String r1 = "CHANNEL_MESSAGE_DOC"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 32
            goto L_0x0a3b
        L_0x0a03:
            java.lang.String r1 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 44
            goto L_0x0a3b
        L_0x0a0e:
            java.lang.String r1 = "MESSAGE_STICKER"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 10
            goto L_0x0a3b
        L_0x0a19:
            java.lang.String r1 = "CHAT_CREATED"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 65
            goto L_0x0a3b
        L_0x0a24:
            java.lang.String r1 = "CHANNEL_MESSAGE_CONTACT"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 35
            goto L_0x0a3b
        L_0x0a2f:
            java.lang.String r1 = "MESSAGE_GAME_SCORE"
            boolean r1 = r9.equals(r1)     // Catch:{ all -> 0x041e }
            if (r1 == 0) goto L_0x0a3a
            r1 = 19
            goto L_0x0a3b
        L_0x0a3a:
            r1 = -1
        L_0x0a3b:
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
                case 0: goto L_0x1b16;
                case 1: goto L_0x1b16;
                case 2: goto L_0x1af6;
                case 3: goto L_0x1ad9;
                case 4: goto L_0x1abc;
                case 5: goto L_0x1a9f;
                case 6: goto L_0x1a81;
                case 7: goto L_0x1a6b;
                case 8: goto L_0x1a4d;
                case 9: goto L_0x1a2f;
                case 10: goto L_0x19d4;
                case 11: goto L_0x19b6;
                case 12: goto L_0x1993;
                case 13: goto L_0x1970;
                case 14: goto L_0x194d;
                case 15: goto L_0x192f;
                case 16: goto L_0x1911;
                case 17: goto L_0x18f3;
                case 18: goto L_0x18d0;
                case 19: goto L_0x18b1;
                case 20: goto L_0x18b1;
                case 21: goto L_0x188e;
                case 22: goto L_0x1868;
                case 23: goto L_0x1844;
                case 24: goto L_0x1821;
                case 25: goto L_0x17fe;
                case 26: goto L_0x17d9;
                case 27: goto L_0x17c3;
                case 28: goto L_0x17a5;
                case 29: goto L_0x1787;
                case 30: goto L_0x1769;
                case 31: goto L_0x174b;
                case 32: goto L_0x172d;
                case 33: goto L_0x16d2;
                case 34: goto L_0x16b4;
                case 35: goto L_0x1691;
                case 36: goto L_0x166e;
                case 37: goto L_0x164b;
                case 38: goto L_0x162d;
                case 39: goto L_0x160f;
                case 40: goto L_0x15f1;
                case 41: goto L_0x15d3;
                case 42: goto L_0x15a9;
                case 43: goto L_0x1585;
                case 44: goto L_0x1561;
                case 45: goto L_0x153d;
                case 46: goto L_0x1517;
                case 47: goto L_0x1502;
                case 48: goto L_0x14e1;
                case 49: goto L_0x14be;
                case 50: goto L_0x149b;
                case 51: goto L_0x1478;
                case 52: goto L_0x1455;
                case 53: goto L_0x1432;
                case 54: goto L_0x13b9;
                case 55: goto L_0x1396;
                case 56: goto L_0x136e;
                case 57: goto L_0x1346;
                case 58: goto L_0x131e;
                case 59: goto L_0x12fb;
                case 60: goto L_0x12d8;
                case 61: goto L_0x12b5;
                case 62: goto L_0x128d;
                case 63: goto L_0x1269;
                case 64: goto L_0x1241;
                case 65: goto L_0x1227;
                case 66: goto L_0x1227;
                case 67: goto L_0x120d;
                case 68: goto L_0x11f3;
                case 69: goto L_0x11d4;
                case 70: goto L_0x11ba;
                case 71: goto L_0x119b;
                case 72: goto L_0x1181;
                case 73: goto L_0x1167;
                case 74: goto L_0x114d;
                case 75: goto L_0x1133;
                case 76: goto L_0x1119;
                case 77: goto L_0x10ff;
                case 78: goto L_0x10e5;
                case 79: goto L_0x10ba;
                case 80: goto L_0x1091;
                case 81: goto L_0x1068;
                case 82: goto L_0x103f;
                case 83: goto L_0x1014;
                case 84: goto L_0x0ffa;
                case 85: goto L_0x0fa5;
                case 86: goto L_0x0f5a;
                case 87: goto L_0x0f0f;
                case 88: goto L_0x0ec4;
                case 89: goto L_0x0e79;
                case 90: goto L_0x0e2e;
                case 91: goto L_0x0d77;
                case 92: goto L_0x0d2c;
                case 93: goto L_0x0cd7;
                case 94: goto L_0x0CLASSNAME;
                case 95: goto L_0x0CLASSNAME;
                case 96: goto L_0x0be8;
                case 97: goto L_0x0ba2;
                case 98: goto L_0x0b59;
                case 99: goto L_0x0b10;
                case 100: goto L_0x0ac7;
                case 101: goto L_0x0a7e;
                case 102: goto L_0x0a62;
                case 103: goto L_0x0a5e;
                case 104: goto L_0x0a5e;
                case 105: goto L_0x0a5e;
                case 106: goto L_0x0a5e;
                case 107: goto L_0x0a5e;
                case 108: goto L_0x0a5e;
                case 109: goto L_0x0a5e;
                case 110: goto L_0x0a5e;
                case 111: goto L_0x0a5e;
                default: goto L_0x0a58;
            }
        L_0x0a58:
            r1 = r18
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1c1c }
            goto L_0x1b31
        L_0x0a5e:
            r1 = r18
            goto L_0x1b47
        L_0x0a62:
            java.lang.String r1 = "YouHaveNewMessage"
            r2 = 2131628364(0x7f0e114c, float:1.8884019E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x041e }
            java.lang.String r2 = "SecretChatName"
            r5 = 2131627401(0x7f0e0d89, float:1.8882065E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r5)     // Catch:{ all -> 0x041e }
            r34 = r2
            r5 = 1
            r16 = 0
            r2 = r1
            r1 = r18
            goto L_0x1b4b
        L_0x0a7e:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0a9a
            java.lang.String r1 = "NotificationActionPinnedGifUser"
            r2 = 2131626412(0x7f0e09ac, float:1.888006E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0a9a:
            if (r6 == 0) goto L_0x0ab4
            java.lang.String r1 = "NotificationActionPinnedGif"
            r2 = 2131626410(0x7f0e09aa, float:1.8880055E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0ab4:
            java.lang.String r1 = "NotificationActionPinnedGifChannel"
            r2 = 2131626411(0x7f0e09ab, float:1.8880057E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x041e }
            r6[r5] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0ac7:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0ae3
            java.lang.String r1 = "NotificationActionPinnedInvoiceUser"
            r2 = 2131626415(0x7f0e09af, float:1.8880066E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0ae3:
            if (r6 == 0) goto L_0x0afd
            java.lang.String r1 = "NotificationActionPinnedInvoice"
            r2 = 2131626413(0x7f0e09ad, float:1.8880061E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0afd:
            java.lang.String r1 = "NotificationActionPinnedInvoiceChannel"
            r2 = 2131626414(0x7f0e09ae, float:1.8880064E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x041e }
            r6[r5] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0b10:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0b2c
            java.lang.String r1 = "NotificationActionPinnedGameScoreUser"
            r2 = 2131626402(0x7f0e09a2, float:1.888004E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0b2c:
            if (r6 == 0) goto L_0x0b46
            java.lang.String r1 = "NotificationActionPinnedGameScore"
            r2 = 2131626400(0x7f0e09a0, float:1.8880035E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0b46:
            java.lang.String r1 = "NotificationActionPinnedGameScoreChannel"
            r2 = 2131626401(0x7f0e09a1, float:1.8880037E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x041e }
            r6[r5] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0b59:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0b75
            java.lang.String r1 = "NotificationActionPinnedGameUser"
            r2 = 2131626403(0x7f0e09a3, float:1.8880041E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0b75:
            if (r6 == 0) goto L_0x0b8f
            java.lang.String r1 = "NotificationActionPinnedGame"
            r2 = 2131626398(0x7f0e099e, float:1.8880031E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0b8f:
            java.lang.String r1 = "NotificationActionPinnedGameChannel"
            r2 = 2131626399(0x7f0e099f, float:1.8880033E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x041e }
            r6[r5] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0ba2:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0bbd
            java.lang.String r1 = "NotificationActionPinnedGeoLiveUser"
            r2 = 2131626408(0x7f0e09a8, float:1.8880051E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0bbd:
            if (r6 == 0) goto L_0x0bd6
            java.lang.String r1 = "NotificationActionPinnedGeoLive"
            r2 = 2131626406(0x7f0e09a6, float:1.8880047E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0bd6:
            java.lang.String r1 = "NotificationActionPinnedGeoLiveChannel"
            r2 = 2131626407(0x7f0e09a7, float:1.888005E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x041e }
            r6[r5] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0be8:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGeoUser"
            r2 = 2131626409(0x7f0e09a9, float:1.8880053E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0CLASSNAME:
            if (r6 == 0) goto L_0x0c1c
            java.lang.String r1 = "NotificationActionPinnedGeo"
            r2 = 2131626404(0x7f0e09a4, float:1.8880043E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0c1c:
            java.lang.String r1 = "NotificationActionPinnedGeoChannel"
            r2 = 2131626405(0x7f0e09a5, float:1.8880045E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r5 = 0
            r7 = r12[r5]     // Catch:{ all -> 0x041e }
            r6[r5] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x041e }
        L_0x0c2d:
            r2 = r1
            r1 = r18
            goto L_0x1b48
        L_0x0CLASSNAME:
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 <= 0) goto L_0x0c4d
            java.lang.String r1 = "NotificationActionPinnedPollUser"
            r2 = 2131626427(0x7f0e09bb, float:1.888009E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0c4d:
            if (r6 == 0) goto L_0x0c6b
            java.lang.String r1 = "NotificationActionPinnedPoll2"
            r2 = 2131626425(0x7f0e09b9, float:1.8880086E38)
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 2
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r8 = 1
            r5[r8] = r7     // Catch:{ all -> 0x041e }
            r7 = r12[r8]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0c6b:
            java.lang.String r1 = "NotificationActionPinnedPollChannel2"
            r2 = 2131626426(0x7f0e09ba, float:1.8880088E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x041e }
            goto L_0x0c2d
        L_0x0CLASSNAME:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0ca0
            java.lang.String r2 = "NotificationActionPinnedQuizUser"
            r5 = 2131626430(0x7f0e09be, float:1.8880096E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0ca0:
            if (r6 == 0) goto L_0x0cbf
            java.lang.String r2 = "NotificationActionPinnedQuiz2"
            r5 = 2131626428(0x7f0e09bc, float:1.8880092E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r10 = 1
            r6[r10] = r8     // Catch:{ all -> 0x041e }
            r8 = r12[r10]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0cbf:
            java.lang.String r2 = "NotificationActionPinnedQuizChannel2"
            r5 = 2131626429(0x7f0e09bd, float:1.8880094E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0cd7:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0cf5
            java.lang.String r2 = "NotificationActionPinnedContactUser"
            r5 = 2131626394(0x7f0e099a, float:1.8880023E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0cf5:
            if (r6 == 0) goto L_0x0d14
            java.lang.String r2 = "NotificationActionPinnedContact2"
            r5 = 2131626392(0x7f0e0998, float:1.8880019E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r10 = 1
            r6[r10] = r8     // Catch:{ all -> 0x041e }
            r8 = r12[r10]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0d14:
            java.lang.String r2 = "NotificationActionPinnedContactChannel2"
            r5 = 2131626393(0x7f0e0999, float:1.888002E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0d2c:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0d4a
            java.lang.String r2 = "NotificationActionPinnedVoiceUser"
            r5 = 2131626448(0x7f0e09d0, float:1.8880132E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0d4a:
            if (r6 == 0) goto L_0x0d64
            java.lang.String r2 = "NotificationActionPinnedVoice"
            r5 = 2131626446(0x7f0e09ce, float:1.8880128E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0d64:
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            r5 = 2131626447(0x7f0e09cf, float:1.888013E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0d77:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0db4
            int r2 = r12.length     // Catch:{ all -> 0x041e }
            r5 = 1
            if (r2 <= r5) goto L_0x0da1
            r2 = r12[r5]     // Catch:{ all -> 0x041e }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x041e }
            if (r2 != 0) goto L_0x0da1
            java.lang.String r2 = "NotificationActionPinnedStickerEmojiUser"
            r5 = 2131626438(0x7f0e09c6, float:1.8880112E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0da1:
            java.lang.String r2 = "NotificationActionPinnedStickerUser"
            r5 = 2131626439(0x7f0e09c7, float:1.8880114E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0db4:
            if (r6 == 0) goto L_0x0df7
            int r2 = r12.length     // Catch:{ all -> 0x041e }
            r5 = 2
            if (r2 <= r5) goto L_0x0ddf
            r2 = r12[r5]     // Catch:{ all -> 0x041e }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x041e }
            if (r2 != 0) goto L_0x0ddf
            java.lang.String r2 = "NotificationActionPinnedStickerEmoji"
            r5 = 2131626436(0x7f0e09c4, float:1.8880108E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r10 = 1
            r6[r10] = r8     // Catch:{ all -> 0x041e }
            r8 = r12[r10]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0ddf:
            java.lang.String r2 = "NotificationActionPinnedSticker"
            r5 = 2131626434(0x7f0e09c2, float:1.8880104E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0df7:
            int r2 = r12.length     // Catch:{ all -> 0x041e }
            r5 = 1
            if (r2 <= r5) goto L_0x0e1b
            r2 = r12[r5]     // Catch:{ all -> 0x041e }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x041e }
            if (r2 != 0) goto L_0x0e1b
            java.lang.String r2 = "NotificationActionPinnedStickerEmojiChannel"
            r5 = 2131626437(0x7f0e09c5, float:1.888011E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0e1b:
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            r5 = 2131626435(0x7f0e09c3, float:1.8880106E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0e2e:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e4c
            java.lang.String r2 = "NotificationActionPinnedFileUser"
            r5 = 2131626397(0x7f0e099d, float:1.888003E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0e4c:
            if (r6 == 0) goto L_0x0e66
            java.lang.String r2 = "NotificationActionPinnedFile"
            r5 = 2131626395(0x7f0e099b, float:1.8880025E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0e66:
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            r5 = 2131626396(0x7f0e099c, float:1.8880027E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0e79:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e97
            java.lang.String r2 = "NotificationActionPinnedRoundUser"
            r5 = 2131626433(0x7f0e09c1, float:1.8880102E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0e97:
            if (r6 == 0) goto L_0x0eb1
            java.lang.String r2 = "NotificationActionPinnedRound"
            r5 = 2131626431(0x7f0e09bf, float:1.8880098E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0eb1:
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            r5 = 2131626432(0x7f0e09c0, float:1.88801E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0ec4:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0ee2
            java.lang.String r2 = "NotificationActionPinnedVideoUser"
            r5 = 2131626445(0x7f0e09cd, float:1.8880126E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0ee2:
            if (r6 == 0) goto L_0x0efc
            java.lang.String r2 = "NotificationActionPinnedVideo"
            r5 = 2131626443(0x7f0e09cb, float:1.8880122E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0efc:
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            r5 = 2131626444(0x7f0e09cc, float:1.8880124E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0f0f:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0f2d
            java.lang.String r2 = "NotificationActionPinnedPhotoUser"
            r5 = 2131626424(0x7f0e09b8, float:1.8880084E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0f2d:
            if (r6 == 0) goto L_0x0var_
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            r5 = 2131626422(0x7f0e09b6, float:1.888008E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0var_:
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            r5 = 2131626423(0x7f0e09b7, float:1.8880082E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0f5a:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0var_
            java.lang.String r2 = "NotificationActionPinnedNoTextUser"
            r5 = 2131626421(0x7f0e09b5, float:1.8880078E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0var_:
            if (r6 == 0) goto L_0x0var_
            java.lang.String r2 = "NotificationActionPinnedNoText"
            r5 = 2131626419(0x7f0e09b3, float:1.8880074E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0var_:
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            r5 = 2131626420(0x7f0e09b4, float:1.8880076E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0fa5:
            r1 = r18
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 <= 0) goto L_0x0fc3
            java.lang.String r2 = "NotificationActionPinnedTextUser"
            r5 = 2131626442(0x7f0e09ca, float:1.888012E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0fc3:
            if (r6 == 0) goto L_0x0fe2
            java.lang.String r2 = "NotificationActionPinnedText"
            r5 = 2131626440(0x7f0e09c8, float:1.8880116E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0fe2:
            java.lang.String r2 = "NotificationActionPinnedTextChannel"
            r5 = 2131626441(0x7f0e09c9, float:1.8880118E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x0ffa:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAlbum"
            r5 = 2131626457(0x7f0e09d9, float:1.888015E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x17d6
        L_0x1014:
            r1 = r18
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x041e }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x041e }
            r2[r5] = r6     // Catch:{ all -> 0x041e }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x041e }
            r2[r5] = r6     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "Files"
            r6 = 2
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x041e }
            int r7 = r7.intValue()     // Catch:{ all -> 0x041e }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)     // Catch:{ all -> 0x041e }
            r2[r6] = r5     // Catch:{ all -> 0x041e }
            r5 = 2131626460(0x7f0e09dc, float:1.8880157E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r5, r2)     // Catch:{ all -> 0x041e }
            goto L_0x17d6
        L_0x103f:
            r1 = r18
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r2[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r2[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 2
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x041e }
            int r7 = r7.intValue()     // Catch:{ all -> 0x041e }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)     // Catch:{ all -> 0x041e }
            r2[r6] = r5     // Catch:{ all -> 0x041e }
            r5 = 2131626460(0x7f0e09dc, float:1.8880157E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r5, r2)     // Catch:{ all -> 0x041e }
            goto L_0x17d6
        L_0x1068:
            r1 = r18
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x041e }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x041e }
            r2[r5] = r6     // Catch:{ all -> 0x041e }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x041e }
            r2[r5] = r6     // Catch:{ all -> 0x041e }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x041e }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x041e }
            int r6 = r6.intValue()     // Catch:{ all -> 0x041e }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r10, r6)     // Catch:{ all -> 0x041e }
            r2[r5] = r6     // Catch:{ all -> 0x041e }
            r5 = 2131626460(0x7f0e09dc, float:1.8880157E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r5, r2)     // Catch:{ all -> 0x041e }
            goto L_0x17d6
        L_0x1091:
            r1 = r18
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x041e }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x041e }
            r2[r5] = r6     // Catch:{ all -> 0x041e }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x041e }
            r2[r5] = r6     // Catch:{ all -> 0x041e }
            r5 = 2
            r6 = r12[r5]     // Catch:{ all -> 0x041e }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x041e }
            int r6 = r6.intValue()     // Catch:{ all -> 0x041e }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r7, r6)     // Catch:{ all -> 0x041e }
            r2[r5] = r6     // Catch:{ all -> 0x041e }
            r5 = 2131626460(0x7f0e09dc, float:1.8880157E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r5, r2)     // Catch:{ all -> 0x041e }
            goto L_0x17d6
        L_0x10ba:
            r1 = r18
            java.lang.String r2 = "NotificationGroupForwardedFew"
            r5 = 2131626461(0x7f0e09dd, float:1.8880159E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r10 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r10     // Catch:{ all -> 0x041e }
            r7 = 1
            r10 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r10     // Catch:{ all -> 0x041e }
            r7 = 2
            r10 = r12[r7]     // Catch:{ all -> 0x041e }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt(r10)     // Catch:{ all -> 0x041e }
            int r10 = r10.intValue()     // Catch:{ all -> 0x041e }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r10)     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x17d6
        L_0x10e5:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            r5 = 2131626456(0x7f0e09d8, float:1.8880149E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x10ff:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAddSelf"
            r5 = 2131626455(0x7f0e09d7, float:1.8880147E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x1119:
            r1 = r18
            java.lang.String r2 = "NotificationGroupLeftMember"
            r5 = 2131626466(0x7f0e09e2, float:1.888017E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x1133:
            r1 = r18
            java.lang.String r2 = "NotificationGroupKickYou"
            r5 = 2131626465(0x7f0e09e1, float:1.8880167E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x114d:
            r1 = r18
            java.lang.String r2 = "NotificationGroupKickMember"
            r5 = 2131626464(0x7f0e09e0, float:1.8880165E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x1167:
            r1 = r18
            java.lang.String r2 = "NotificationGroupInvitedYouToCall"
            r5 = 2131626463(0x7f0e09df, float:1.8880163E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x1181:
            r1 = r18
            java.lang.String r2 = "NotificationGroupEndedCall"
            r5 = 2131626459(0x7f0e09db, float:1.8880155E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x119b:
            r1 = r18
            java.lang.String r2 = "NotificationGroupInvitedToCall"
            r5 = 2131626462(0x7f0e09de, float:1.888016E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x11ba:
            r1 = r18
            java.lang.String r2 = "NotificationGroupCreatedCall"
            r5 = 2131626458(0x7f0e09da, float:1.8880153E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x11d4:
            r1 = r18
            java.lang.String r2 = "NotificationGroupAddMember"
            r5 = 2131626454(0x7f0e09d6, float:1.8880145E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x11f3:
            r1 = r18
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            r5 = 2131626452(0x7f0e09d4, float:1.888014E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x120d:
            r1 = r18
            java.lang.String r2 = "NotificationEditedGroupName"
            r5 = 2131626451(0x7f0e09d3, float:1.8880139E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x1227:
            r1 = r18
            java.lang.String r2 = "NotificationInvitedToGroup"
            r5 = 2131626471(0x7f0e09e7, float:1.888018E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x1241:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupInvoice"
            r5 = 2131626488(0x7f0e09f8, float:1.8880214E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "PaymentInvoice"
            r6 = 2131626878(0x7f0e0b7e, float:1.8881005E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1269:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupGameScored"
            r5 = 2131626486(0x7f0e09f6, float:1.888021E38)
            r6 = 4
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r8 = 0
            r10 = r12[r8]     // Catch:{ all -> 0x041e }
            r6[r8] = r10     // Catch:{ all -> 0x041e }
            r8 = 1
            r10 = r12[r8]     // Catch:{ all -> 0x041e }
            r6[r8] = r10     // Catch:{ all -> 0x041e }
            r8 = 2
            r10 = r12[r8]     // Catch:{ all -> 0x041e }
            r6[r8] = r10     // Catch:{ all -> 0x041e }
            r7 = 3
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x128d:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupGame"
            r5 = 2131626485(0x7f0e09f5, float:1.8880208E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachGame"
            r6 = 2131624388(0x7f0e01c4, float:1.8875954E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x12b5:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupGif"
            r5 = 2131626487(0x7f0e09f7, float:1.8880212E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachGif"
            r6 = 2131624389(0x7f0e01c5, float:1.8875956E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x12d8:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupLiveLocation"
            r5 = 2131626489(0x7f0e09f9, float:1.8880216E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachLiveLocation"
            r6 = 2131624394(0x7f0e01ca, float:1.8875966E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x12fb:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupMap"
            r5 = 2131626490(0x7f0e09fa, float:1.8880218E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachLocation"
            r6 = 2131624398(0x7f0e01ce, float:1.8875975E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x131e:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupPoll2"
            r5 = 2131626494(0x7f0e09fe, float:1.8880226E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "Poll"
            r6 = 2131627045(0x7f0e0CLASSNAME, float:1.8881343E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1346:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupQuiz2"
            r5 = 2131626495(0x7f0e09ff, float:1.8880228E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "PollQuiz"
            r6 = 2131627052(0x7f0e0c2c, float:1.8881358E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x136e:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupContact2"
            r5 = 2131626483(0x7f0e09f3, float:1.8880203E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachContact"
            r6 = 2131624384(0x7f0e01c0, float:1.8875946E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1396:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupAudio"
            r5 = 2131626482(0x7f0e09f2, float:1.8880201E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachAudio"
            r6 = 2131624382(0x7f0e01be, float:1.8875942E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x13b9:
            r1 = r18
            int r2 = r12.length     // Catch:{ all -> 0x041e }
            r5 = 2
            if (r2 <= r5) goto L_0x13ff
            r2 = r12[r5]     // Catch:{ all -> 0x041e }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x041e }
            if (r2 != 0) goto L_0x13ff
            java.lang.String r2 = "NotificationMessageGroupStickerEmoji"
            r5 = 2131626498(0x7f0e0a02, float:1.8880234E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x041e }
            r5.<init>()     // Catch:{ all -> 0x041e }
            r6 = r12[r7]     // Catch:{ all -> 0x041e }
            r5.append(r6)     // Catch:{ all -> 0x041e }
            r5.append(r15)     // Catch:{ all -> 0x041e }
            r6 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)     // Catch:{ all -> 0x041e }
            r5.append(r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x13ff:
            java.lang.String r2 = "NotificationMessageGroupSticker"
            r5 = 2131626497(0x7f0e0a01, float:1.8880232E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x041e }
            r5.<init>()     // Catch:{ all -> 0x041e }
            r6 = r12[r7]     // Catch:{ all -> 0x041e }
            r5.append(r6)     // Catch:{ all -> 0x041e }
            r5.append(r15)     // Catch:{ all -> 0x041e }
            r6 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)     // Catch:{ all -> 0x041e }
            r5.append(r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1432:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupDocument"
            r5 = 2131626484(0x7f0e09f4, float:1.8880205E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachDocument"
            r6 = 2131624387(0x7f0e01c3, float:1.8875952E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1455:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupRound"
            r5 = 2131626496(0x7f0e0a00, float:1.888023E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachRound"
            r6 = 2131624404(0x7f0e01d4, float:1.8875987E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1478:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupVideo"
            r5 = 2131626500(0x7f0e0a04, float:1.8880238E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachVideo"
            r6 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x149b:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupPhoto"
            r5 = 2131626493(0x7f0e09fd, float:1.8880224E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachPhoto"
            r6 = 2131624402(0x7f0e01d2, float:1.8875983E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x14be:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupNoText"
            r5 = 2131626492(0x7f0e09fc, float:1.8880222E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "Message"
            r6 = 2131626181(0x7f0e08c5, float:1.887959E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x14e1:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGroupText"
            r5 = 2131626499(0x7f0e0a03, float:1.8880236E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            r5 = r12[r7]     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1502:
            r1 = r18
            java.lang.String r2 = "ChannelMessageAlbum"
            r5 = 2131624746(0x7f0e032a, float:1.887668E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            goto L_0x17d6
        L_0x1517:
            r1 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x041e }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x041e }
            r2[r5] = r6     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "Files"
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x041e }
            int r7 = r7.intValue()     // Catch:{ all -> 0x041e }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)     // Catch:{ all -> 0x041e }
            r2[r6] = r5     // Catch:{ all -> 0x041e }
            r5 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x041e }
            goto L_0x17d6
        L_0x153d:
            r1 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r2[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x041e }
            int r7 = r7.intValue()     // Catch:{ all -> 0x041e }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)     // Catch:{ all -> 0x041e }
            r2[r6] = r5     // Catch:{ all -> 0x041e }
            r5 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x041e }
            goto L_0x17d6
        L_0x1561:
            r1 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x041e }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x041e }
            r2[r5] = r6     // Catch:{ all -> 0x041e }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x041e }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x041e }
            int r6 = r6.intValue()     // Catch:{ all -> 0x041e }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r10, r6)     // Catch:{ all -> 0x041e }
            r2[r5] = r6     // Catch:{ all -> 0x041e }
            r5 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x041e }
            goto L_0x17d6
        L_0x1585:
            r1 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x041e }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x041e }
            r2[r5] = r6     // Catch:{ all -> 0x041e }
            r5 = 1
            r6 = r12[r5]     // Catch:{ all -> 0x041e }
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x041e }
            int r6 = r6.intValue()     // Catch:{ all -> 0x041e }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r7, r6)     // Catch:{ all -> 0x041e }
            r2[r5] = r6     // Catch:{ all -> 0x041e }
            r5 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x041e }
            goto L_0x17d6
        L_0x15a9:
            r1 = r18
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x041e }
            r5 = 0
            r6 = r12[r5]     // Catch:{ all -> 0x041e }
            r2[r5] = r6     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "ForwardedMessageCount"
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x041e }
            int r7 = r7.intValue()     // Catch:{ all -> 0x041e }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = r5.toLowerCase()     // Catch:{ all -> 0x041e }
            r2[r6] = r5     // Catch:{ all -> 0x041e }
            r5 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r5, r2)     // Catch:{ all -> 0x041e }
            goto L_0x17d6
        L_0x15d3:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGame"
            r5 = 2131626479(0x7f0e09ef, float:1.8880195E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachGame"
            r6 = 2131624388(0x7f0e01c4, float:1.8875954E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x15f1:
            r1 = r18
            java.lang.String r2 = "ChannelMessageGIF"
            r5 = 2131624751(0x7f0e032f, float:1.887669E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachGif"
            r6 = 2131624389(0x7f0e01c5, float:1.8875956E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x160f:
            r1 = r18
            java.lang.String r2 = "ChannelMessageLiveLocation"
            r5 = 2131624752(0x7f0e0330, float:1.8876693E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachLiveLocation"
            r6 = 2131624394(0x7f0e01ca, float:1.8875966E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x162d:
            r1 = r18
            java.lang.String r2 = "ChannelMessageMap"
            r5 = 2131624753(0x7f0e0331, float:1.8876695E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachLocation"
            r6 = 2131624398(0x7f0e01ce, float:1.8875975E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x164b:
            r1 = r18
            java.lang.String r2 = "ChannelMessagePoll2"
            r5 = 2131624757(0x7f0e0335, float:1.8876703E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "Poll"
            r6 = 2131627045(0x7f0e0CLASSNAME, float:1.8881343E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x166e:
            r1 = r18
            java.lang.String r2 = "ChannelMessageQuiz2"
            r5 = 2131624758(0x7f0e0336, float:1.8876705E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "QuizPoll"
            r6 = 2131627160(0x7f0e0CLASSNAME, float:1.8881577E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1691:
            r1 = r18
            java.lang.String r2 = "ChannelMessageContact2"
            r5 = 2131624748(0x7f0e032c, float:1.8876684E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachContact"
            r6 = 2131624384(0x7f0e01c0, float:1.8875946E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x16b4:
            r1 = r18
            java.lang.String r2 = "ChannelMessageAudio"
            r5 = 2131624747(0x7f0e032b, float:1.8876682E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachAudio"
            r6 = 2131624382(0x7f0e01be, float:1.8875942E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x16d2:
            r1 = r18
            int r2 = r12.length     // Catch:{ all -> 0x041e }
            r5 = 1
            if (r2 <= r5) goto L_0x1713
            r2 = r12[r5]     // Catch:{ all -> 0x041e }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x041e }
            if (r2 != 0) goto L_0x1713
            java.lang.String r2 = "ChannelMessageStickerEmoji"
            r5 = 2131624761(0x7f0e0339, float:1.887671E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x041e }
            r5.<init>()     // Catch:{ all -> 0x041e }
            r6 = r12[r7]     // Catch:{ all -> 0x041e }
            r5.append(r6)     // Catch:{ all -> 0x041e }
            r5.append(r15)     // Catch:{ all -> 0x041e }
            r6 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)     // Catch:{ all -> 0x041e }
            r5.append(r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1713:
            java.lang.String r2 = "ChannelMessageSticker"
            r5 = 2131624760(0x7f0e0338, float:1.8876709E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            r5 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r5)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x172d:
            r1 = r18
            java.lang.String r2 = "ChannelMessageDocument"
            r5 = 2131624749(0x7f0e032d, float:1.8876687E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachDocument"
            r6 = 2131624387(0x7f0e01c3, float:1.8875952E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x174b:
            r1 = r18
            java.lang.String r2 = "ChannelMessageRound"
            r5 = 2131624759(0x7f0e0337, float:1.8876707E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachRound"
            r6 = 2131624404(0x7f0e01d4, float:1.8875987E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1769:
            r1 = r18
            java.lang.String r2 = "ChannelMessageVideo"
            r5 = 2131624762(0x7f0e033a, float:1.8876713E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachVideo"
            r6 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1787:
            r1 = r18
            java.lang.String r2 = "ChannelMessagePhoto"
            r5 = 2131624756(0x7f0e0334, float:1.88767E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachPhoto"
            r6 = 2131624402(0x7f0e01d2, float:1.8875983E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x17a5:
            r1 = r18
            java.lang.String r2 = "ChannelMessageNoText"
            r5 = 2131624755(0x7f0e0333, float:1.8876699E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "Message"
            r6 = 2131626181(0x7f0e08c5, float:1.887959E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x17c3:
            r1 = r18
            java.lang.String r2 = "NotificationMessageAlbum"
            r5 = 2131626473(0x7f0e09e9, float:1.8880183E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
        L_0x17d6:
            r5 = 1
            goto L_0x1b49
        L_0x17d9:
            r1 = r18
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            java.lang.String r6 = "Files"
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ all -> 0x041e }
            int r8 = r8.intValue()     // Catch:{ all -> 0x041e }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r6, r8)     // Catch:{ all -> 0x041e }
            r5[r7] = r6     // Catch:{ all -> 0x041e }
            r6 = 2131626477(0x7f0e09ed, float:1.8880191E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r5)     // Catch:{ all -> 0x041e }
            goto L_0x17d6
        L_0x17fe:
            r1 = r18
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ all -> 0x041e }
            int r8 = r8.intValue()     // Catch:{ all -> 0x041e }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r8)     // Catch:{ all -> 0x041e }
            r6[r7] = r5     // Catch:{ all -> 0x041e }
            r5 = 2131626477(0x7f0e09ed, float:1.8880191E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x17d6
        L_0x1821:
            r1 = r18
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 1
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x041e }
            int r7 = r7.intValue()     // Catch:{ all -> 0x041e }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r10, r7)     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 2131626477(0x7f0e09ed, float:1.8880191E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r5)     // Catch:{ all -> 0x041e }
            goto L_0x17d6
        L_0x1844:
            r1 = r18
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r5[r6] = r8     // Catch:{ all -> 0x041e }
            r6 = 1
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ all -> 0x041e }
            int r8 = r8.intValue()     // Catch:{ all -> 0x041e }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r8)     // Catch:{ all -> 0x041e }
            r5[r6] = r7     // Catch:{ all -> 0x041e }
            r6 = 2131626477(0x7f0e09ed, float:1.8880191E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r5)     // Catch:{ all -> 0x041e }
            goto L_0x17d6
        L_0x1868:
            r1 = r18
            java.lang.String r2 = "NotificationMessageForwardFew"
            r5 = 2131626478(0x7f0e09ee, float:1.8880193E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r10 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r10     // Catch:{ all -> 0x041e }
            r7 = 1
            r10 = r12[r7]     // Catch:{ all -> 0x041e }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt(r10)     // Catch:{ all -> 0x041e }
            int r10 = r10.intValue()     // Catch:{ all -> 0x041e }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r10)     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x17d6
        L_0x188e:
            r1 = r18
            java.lang.String r2 = "NotificationMessageInvoice"
            r5 = 2131626501(0x7f0e0a05, float:1.888024E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "PaymentInvoice"
            r6 = 2131626878(0x7f0e0b7e, float:1.8881005E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x18b1:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGameScored"
            r5 = 2131626480(0x7f0e09f0, float:1.8880197E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 2
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x18d0:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGame"
            r5 = 2131626479(0x7f0e09ef, float:1.8880195E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachGame"
            r6 = 2131624388(0x7f0e01c4, float:1.8875954E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x18f3:
            r1 = r18
            java.lang.String r2 = "NotificationMessageGif"
            r5 = 2131626481(0x7f0e09f1, float:1.88802E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachGif"
            r6 = 2131624389(0x7f0e01c5, float:1.8875956E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1911:
            r1 = r18
            java.lang.String r2 = "NotificationMessageLiveLocation"
            r5 = 2131626502(0x7f0e0a06, float:1.8880242E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachLiveLocation"
            r6 = 2131624394(0x7f0e01ca, float:1.8875966E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x192f:
            r1 = r18
            java.lang.String r2 = "NotificationMessageMap"
            r5 = 2131626503(0x7f0e0a07, float:1.8880244E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachLocation"
            r6 = 2131624398(0x7f0e01ce, float:1.8875975E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x194d:
            r1 = r18
            java.lang.String r2 = "NotificationMessagePoll2"
            r5 = 2131626507(0x7f0e0a0b, float:1.8880252E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "Poll"
            r6 = 2131627045(0x7f0e0CLASSNAME, float:1.8881343E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1970:
            r1 = r18
            java.lang.String r2 = "NotificationMessageQuiz2"
            r5 = 2131626508(0x7f0e0a0c, float:1.8880254E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "QuizPoll"
            r6 = 2131627160(0x7f0e0CLASSNAME, float:1.8881577E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1993:
            r1 = r18
            java.lang.String r2 = "NotificationMessageContact2"
            r5 = 2131626475(0x7f0e09eb, float:1.8880187E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachContact"
            r6 = 2131624384(0x7f0e01c0, float:1.8875946E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x19b6:
            r1 = r18
            java.lang.String r2 = "NotificationMessageAudio"
            r5 = 2131626474(0x7f0e09ea, float:1.8880185E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachAudio"
            r6 = 2131624382(0x7f0e01be, float:1.8875942E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x19d4:
            r1 = r18
            int r2 = r12.length     // Catch:{ all -> 0x041e }
            r5 = 1
            if (r2 <= r5) goto L_0x1a15
            r2 = r12[r5]     // Catch:{ all -> 0x041e }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x041e }
            if (r2 != 0) goto L_0x1a15
            java.lang.String r2 = "NotificationMessageStickerEmoji"
            r5 = 2131626515(0x7f0e0a13, float:1.8880268E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x041e }
            r5.<init>()     // Catch:{ all -> 0x041e }
            r6 = r12[r7]     // Catch:{ all -> 0x041e }
            r5.append(r6)     // Catch:{ all -> 0x041e }
            r5.append(r15)     // Catch:{ all -> 0x041e }
            r6 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)     // Catch:{ all -> 0x041e }
            r5.append(r6)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1a15:
            java.lang.String r2 = "NotificationMessageSticker"
            r5 = 2131626514(0x7f0e0a12, float:1.8880266E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            r5 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r5)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1a2f:
            r1 = r18
            java.lang.String r2 = "NotificationMessageDocument"
            r5 = 2131626476(0x7f0e09ec, float:1.888019E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachDocument"
            r6 = 2131624387(0x7f0e01c3, float:1.8875952E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1a4d:
            r1 = r18
            java.lang.String r2 = "NotificationMessageRound"
            r5 = 2131626509(0x7f0e0a0d, float:1.8880256E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachRound"
            r6 = 2131624404(0x7f0e01d4, float:1.8875987E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1a6b:
            r1 = r18
            java.lang.String r2 = "ActionTakeScreenshoot"
            r5 = 2131624161(0x7f0e00e1, float:1.8875494E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r5)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "un1"
            r6 = 0
            r7 = r12[r6]     // Catch:{ all -> 0x041e }
            java.lang.String r2 = r2.replace(r5, r7)     // Catch:{ all -> 0x041e }
            goto L_0x1b48
        L_0x1a81:
            r1 = r18
            java.lang.String r2 = "NotificationMessageSDVideo"
            r5 = 2131626511(0x7f0e0a0f, float:1.888026E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachDestructingVideo"
            r6 = 2131624386(0x7f0e01c2, float:1.887595E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1a9f:
            r1 = r18
            java.lang.String r2 = "NotificationMessageVideo"
            r5 = 2131626517(0x7f0e0a15, float:1.8880272E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachVideo"
            r6 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1abc:
            r1 = r18
            java.lang.String r2 = "NotificationMessageSDPhoto"
            r5 = 2131626510(0x7f0e0a0e, float:1.8880258E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachDestructingPhoto"
            r6 = 2131624385(0x7f0e01c1, float:1.8875948E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1ad9:
            r1 = r18
            java.lang.String r2 = "NotificationMessagePhoto"
            r5 = 2131626506(0x7f0e0a0a, float:1.888025E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "AttachPhoto"
            r6 = 2131624402(0x7f0e01d2, float:1.8875983E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1af6:
            r1 = r18
            java.lang.String r2 = "NotificationMessageNoText"
            r5 = 2131626505(0x7f0e0a09, float:1.8880248E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r6 = 0
            r8 = r12[r6]     // Catch:{ all -> 0x041e }
            r7[r6] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "Message"
            r6 = 2131626181(0x7f0e08c5, float:1.887959E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ all -> 0x041e }
        L_0x1b12:
            r16 = r5
            r5 = 0
            goto L_0x1b4b
        L_0x1b16:
            r1 = r18
            java.lang.String r2 = "NotificationMessageText"
            r5 = 2131626516(0x7f0e0a14, float:1.888027E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x041e }
            r7 = 0
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            r7 = 1
            r8 = r12[r7]     // Catch:{ all -> 0x041e }
            r6[r7] = r8     // Catch:{ all -> 0x041e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x041e }
            r5 = r12[r7]     // Catch:{ all -> 0x041e }
            goto L_0x1b12
        L_0x1b31:
            if (r2 == 0) goto L_0x1b47
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x041e }
            r2.<init>()     // Catch:{ all -> 0x041e }
            java.lang.String r5 = "unhandled loc_key = "
            r2.append(r5)     // Catch:{ all -> 0x041e }
            r2.append(r9)     // Catch:{ all -> 0x041e }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x041e }
            org.telegram.messenger.FileLog.w(r2)     // Catch:{ all -> 0x041e }
        L_0x1b47:
            r2 = 0
        L_0x1b48:
            r5 = 0
        L_0x1b49:
            r16 = 0
        L_0x1b4b:
            if (r2 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x1c1c }
            r6.<init>()     // Catch:{ all -> 0x1c1c }
            r6.id = r1     // Catch:{ all -> 0x1c1c }
            r7 = r39
            r6.random_id = r7     // Catch:{ all -> 0x1c1c }
            if (r16 == 0) goto L_0x1b5d
            r1 = r16
            goto L_0x1b5e
        L_0x1b5d:
            r1 = r2
        L_0x1b5e:
            r6.message = r1     // Catch:{ all -> 0x1c1c }
            r7 = 1000(0x3e8, double:4.94E-321)
            long r7 = r45 / r7
            int r1 = (int) r7     // Catch:{ all -> 0x1c1c }
            r6.date = r1     // Catch:{ all -> 0x1c1c }
            if (r33 == 0) goto L_0x1b70
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r1 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x041e }
            r1.<init>()     // Catch:{ all -> 0x041e }
            r6.action = r1     // Catch:{ all -> 0x041e }
        L_0x1b70:
            if (r32 == 0) goto L_0x1b79
            int r1 = r6.flags     // Catch:{ all -> 0x041e }
            r7 = -2147483648(0xfffffffvar_, float:-0.0)
            r1 = r1 | r7
            r6.flags = r1     // Catch:{ all -> 0x041e }
        L_0x1b79:
            r6.dialog_id = r3     // Catch:{ all -> 0x1c1c }
            if (r38 == 0) goto L_0x1b8b
            org.telegram.tgnet.TLRPC$TL_peerChannel r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x041e }
            r1.<init>()     // Catch:{ all -> 0x041e }
            r6.peer_id = r1     // Catch:{ all -> 0x041e }
            r8 = r38
            r1.channel_id = r8     // Catch:{ all -> 0x041e }
            r3 = r24
            goto L_0x1ba6
        L_0x1b8b:
            if (r24 == 0) goto L_0x1b99
            org.telegram.tgnet.TLRPC$TL_peerChat r1 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x041e }
            r1.<init>()     // Catch:{ all -> 0x041e }
            r6.peer_id = r1     // Catch:{ all -> 0x041e }
            r3 = r24
            r1.chat_id = r3     // Catch:{ all -> 0x041e }
            goto L_0x1ba6
        L_0x1b99:
            r3 = r24
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1c1c }
            r1.<init>()     // Catch:{ all -> 0x1c1c }
            r6.peer_id = r1     // Catch:{ all -> 0x1c1c }
            r8 = r23
            r1.user_id = r8     // Catch:{ all -> 0x1c1c }
        L_0x1ba6:
            int r1 = r6.flags     // Catch:{ all -> 0x1c1c }
            r1 = r1 | 256(0x100, float:3.59E-43)
            r6.flags = r1     // Catch:{ all -> 0x1c1c }
            if (r31 == 0) goto L_0x1bb8
            org.telegram.tgnet.TLRPC$TL_peerChat r1 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x041e }
            r1.<init>()     // Catch:{ all -> 0x041e }
            r6.from_id = r1     // Catch:{ all -> 0x041e }
            r1.chat_id = r3     // Catch:{ all -> 0x041e }
            goto L_0x1bd8
        L_0x1bb8:
            if (r28 == 0) goto L_0x1bc6
            org.telegram.tgnet.TLRPC$TL_peerChannel r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x041e }
            r1.<init>()     // Catch:{ all -> 0x041e }
            r6.from_id = r1     // Catch:{ all -> 0x041e }
            r3 = r28
            r1.channel_id = r3     // Catch:{ all -> 0x041e }
            goto L_0x1bd8
        L_0x1bc6:
            if (r25 == 0) goto L_0x1bd4
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x041e }
            r1.<init>()     // Catch:{ all -> 0x041e }
            r6.from_id = r1     // Catch:{ all -> 0x041e }
            r3 = r25
            r1.user_id = r3     // Catch:{ all -> 0x041e }
            goto L_0x1bd8
        L_0x1bd4:
            org.telegram.tgnet.TLRPC$Peer r1 = r6.peer_id     // Catch:{ all -> 0x1c1c }
            r6.from_id = r1     // Catch:{ all -> 0x1c1c }
        L_0x1bd8:
            if (r26 != 0) goto L_0x1bdf
            if (r33 == 0) goto L_0x1bdd
            goto L_0x1bdf
        L_0x1bdd:
            r1 = 0
            goto L_0x1be0
        L_0x1bdf:
            r1 = 1
        L_0x1be0:
            r6.mentioned = r1     // Catch:{ all -> 0x1c1c }
            r1 = r27
            r6.silent = r1     // Catch:{ all -> 0x1c1c }
            r13 = r22
            r6.from_scheduled = r13     // Catch:{ all -> 0x1c1c }
            org.telegram.messenger.MessageObject r1 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x1c1c }
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
            r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27, r28)     // Catch:{ all -> 0x1c1c }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x1c1c }
            r2.<init>()     // Catch:{ all -> 0x1c1c }
            r2.add(r1)     // Catch:{ all -> 0x1c1c }
            org.telegram.messenger.NotificationsController r1 = org.telegram.messenger.NotificationsController.getInstance(r30)     // Catch:{ all -> 0x1c1c }
            r3 = r43
            java.util.concurrent.CountDownLatch r4 = r3.countDownLatch     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            r1.processNewMessages(r2, r5, r5, r4)     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 0
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r3 = r43
            goto L_0x1c2f
        L_0x1c1c:
            r0 = move-exception
            r3 = r43
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r0 = move-exception
            r3 = r43
            goto L_0x1c5a
        L_0x1CLASSNAME:
            r3 = r43
            goto L_0x1c2d
        L_0x1CLASSNAME:
            r0 = move-exception
            r3 = r1
            goto L_0x1c5a
        L_0x1c2a:
            r3 = r1
            r29 = r14
        L_0x1c2d:
            r30 = r15
        L_0x1c2f:
            r8 = 1
        L_0x1CLASSNAME:
            if (r8 == 0) goto L_0x1CLASSNAME
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1CLASSNAME }
            r1.countDown()     // Catch:{ all -> 0x1CLASSNAME }
        L_0x1CLASSNAME:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r30)     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r30)     // Catch:{ all -> 0x1CLASSNAME }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1d70
        L_0x1CLASSNAME:
            r0 = move-exception
        L_0x1CLASSNAME:
            r1 = r0
            r4 = r9
            r14 = r29
            r15 = r30
            goto L_0x1d1b
        L_0x1c4c:
            r0 = move-exception
            r3 = r1
            r29 = r14
            r30 = r15
            r1 = r0
            r4 = r9
            goto L_0x1d1b
        L_0x1CLASSNAME:
            r0 = move-exception
            r3 = r1
            r29 = r7
        L_0x1c5a:
            r30 = r15
            goto L_0x1d17
        L_0x1c5e:
            r3 = r1
            r29 = r7
            r30 = r15
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$jVvuwI-LdG7mFvHrO-DBdZ4swec r2 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$jVvuwI-LdG7mFvHrO-DBdZ4swec     // Catch:{ all -> 0x1CLASSNAME }
            r15 = r30
            r2.<init>(r15)     // Catch:{ all -> 0x1d11 }
            r1.postRunnable(r2)     // Catch:{ all -> 0x1d11 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d11 }
            r1.countDown()     // Catch:{ all -> 0x1d11 }
            return
        L_0x1CLASSNAME:
            r0 = move-exception
            r15 = r30
            goto L_0x1d17
        L_0x1c7a:
            r3 = r1
            r29 = r7
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$-QuXd6CaqKEr-yz6R8VHHhEnJAw r1 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$-QuXd6CaqKEr-yz6R8VHHhEnJAw     // Catch:{ all -> 0x1d11 }
            r1.<init>(r15)     // Catch:{ all -> 0x1d11 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ all -> 0x1d11 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d11 }
            r1.countDown()     // Catch:{ all -> 0x1d11 }
            return
        L_0x1c8b:
            r3 = r1
            r29 = r7
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x1d11 }
            r1.<init>()     // Catch:{ all -> 0x1d11 }
            r2 = 0
            r1.popup = r2     // Catch:{ all -> 0x1d11 }
            r2 = 2
            r1.flags = r2     // Catch:{ all -> 0x1d11 }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r6 = r45 / r6
            int r2 = (int) r6     // Catch:{ all -> 0x1d11 }
            r1.inbox_date = r2     // Catch:{ all -> 0x1d11 }
            java.lang.String r2 = "message"
            java.lang.String r2 = r5.getString(r2)     // Catch:{ all -> 0x1d11 }
            r1.message = r2     // Catch:{ all -> 0x1d11 }
            java.lang.String r2 = "announcement"
            r1.type = r2     // Catch:{ all -> 0x1d11 }
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ all -> 0x1d11 }
            r2.<init>()     // Catch:{ all -> 0x1d11 }
            r1.media = r2     // Catch:{ all -> 0x1d11 }
            org.telegram.tgnet.TLRPC$TL_updates r2 = new org.telegram.tgnet.TLRPC$TL_updates     // Catch:{ all -> 0x1d11 }
            r2.<init>()     // Catch:{ all -> 0x1d11 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r4 = r2.updates     // Catch:{ all -> 0x1d11 }
            r4.add(r1)     // Catch:{ all -> 0x1d11 }
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1d11 }
            org.telegram.messenger.-$$Lambda$GcmPushListenerService$y7ADJS7XfMNFfehioCWcBUPm0lQ r4 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$y7ADJS7XfMNFfehioCWcBUPm0lQ     // Catch:{ all -> 0x1d11 }
            r4.<init>(r15, r2)     // Catch:{ all -> 0x1d11 }
            r1.postRunnable(r4)     // Catch:{ all -> 0x1d11 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x1d11 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1d11 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d11 }
            r1.countDown()     // Catch:{ all -> 0x1d11 }
            return
        L_0x1cd4:
            r3 = r1
            r29 = r7
            java.lang.String r1 = "dc"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x1d11 }
            java.lang.String r2 = "addr"
            java.lang.String r2 = r11.getString(r2)     // Catch:{ all -> 0x1d11 }
            java.lang.String r4 = ":"
            java.lang.String[] r2 = r2.split(r4)     // Catch:{ all -> 0x1d11 }
            int r4 = r2.length     // Catch:{ all -> 0x1d11 }
            r5 = 2
            if (r4 == r5) goto L_0x1cf3
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d11 }
            r1.countDown()     // Catch:{ all -> 0x1d11 }
            return
        L_0x1cf3:
            r4 = 0
            r4 = r2[r4]     // Catch:{ all -> 0x1d11 }
            r5 = 1
            r2 = r2[r5]     // Catch:{ all -> 0x1d11 }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ all -> 0x1d11 }
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x1d11 }
            r5.applyDatacenterAddress(r1, r4, r2)     // Catch:{ all -> 0x1d11 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)     // Catch:{ all -> 0x1d11 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1d11 }
            java.util.concurrent.CountDownLatch r1 = r3.countDownLatch     // Catch:{ all -> 0x1d11 }
            r1.countDown()     // Catch:{ all -> 0x1d11 }
            return
        L_0x1d11:
            r0 = move-exception
            goto L_0x1d17
        L_0x1d13:
            r0 = move-exception
            r3 = r1
            r29 = r7
        L_0x1d17:
            r1 = r0
            r4 = r9
            r14 = r29
        L_0x1d1b:
            r2 = -1
            goto L_0x1d38
        L_0x1d1d:
            r0 = move-exception
            r3 = r1
            r29 = r7
            r1 = r0
            r4 = r9
            r14 = r29
            r2 = -1
            goto L_0x1d37
        L_0x1d27:
            r0 = move-exception
            r3 = r1
            r29 = r7
            r1 = r0
            r14 = r29
            r2 = -1
            r4 = 0
            goto L_0x1d37
        L_0x1d31:
            r0 = move-exception
            r3 = r1
            r1 = r0
            r2 = -1
            r4 = 0
            r14 = 0
        L_0x1d37:
            r15 = -1
        L_0x1d38:
            if (r15 == r2) goto L_0x1d4a
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15)
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)
            r2.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r2 = r3.countDownLatch
            r2.countDown()
            goto L_0x1d4d
        L_0x1d4a:
            r43.onDecryptError()
        L_0x1d4d:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x1d6d
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
        L_0x1d6d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1d70:
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
