package org.telegram.messenger;

import android.os.SystemClock;
import android.text.TextUtils;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_saveAppLog;
import org.telegram.tgnet.TLRPC$TL_inputAppEvent;
import org.telegram.tgnet.TLRPC$TL_jsonNull;

public class PushListenerController {
    public static final int NOTIFICATION_ID = 1;
    public static final int PUSH_TYPE_FIREBASE = 2;
    public static final int PUSH_TYPE_HUAWEI = 13;
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public interface IPushListenerServiceProvider {
        String getLogTitle();

        int getPushType();

        boolean hasServices();

        void onRequestPushToken();
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PushType {
    }

    public static void sendRegistrationToServer(int i, String str) {
        Utilities.stageQueue.postRunnable(new PushListenerController$$ExternalSyntheticLambda4(str, i));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendRegistrationToServer$3(String str, int i) {
        boolean z;
        ConnectionsManager.setRegId(str, i, SharedConfig.pushStringStatus);
        if (str != null) {
            if (SharedConfig.pushStringGetTimeStart == 0 || SharedConfig.pushStringGetTimeEnd == 0 || (SharedConfig.pushStatSent && TextUtils.equals(SharedConfig.pushString, str))) {
                z = false;
            } else {
                SharedConfig.pushStatSent = false;
                z = true;
            }
            SharedConfig.pushString = str;
            SharedConfig.pushType = i;
            for (int i2 = 0; i2 < 4; i2++) {
                UserConfig instance = UserConfig.getInstance(i2);
                instance.registeredForPush = false;
                instance.saveConfig(false);
                if (instance.getClientUserId() != 0) {
                    if (z) {
                        String str2 = i == 2 ? "fcm" : "hcm";
                        TLRPC$TL_help_saveAppLog tLRPC$TL_help_saveAppLog = new TLRPC$TL_help_saveAppLog();
                        TLRPC$TL_inputAppEvent tLRPC$TL_inputAppEvent = new TLRPC$TL_inputAppEvent();
                        tLRPC$TL_inputAppEvent.time = (double) SharedConfig.pushStringGetTimeStart;
                        tLRPC$TL_inputAppEvent.type = str2 + "_token_request";
                        tLRPC$TL_inputAppEvent.peer = 0;
                        tLRPC$TL_inputAppEvent.data = new TLRPC$TL_jsonNull();
                        tLRPC$TL_help_saveAppLog.events.add(tLRPC$TL_inputAppEvent);
                        TLRPC$TL_inputAppEvent tLRPC$TL_inputAppEvent2 = new TLRPC$TL_inputAppEvent();
                        tLRPC$TL_inputAppEvent2.time = (double) SharedConfig.pushStringGetTimeEnd;
                        tLRPC$TL_inputAppEvent2.type = str2 + "_token_response";
                        tLRPC$TL_inputAppEvent2.peer = SharedConfig.pushStringGetTimeEnd - SharedConfig.pushStringGetTimeStart;
                        tLRPC$TL_inputAppEvent2.data = new TLRPC$TL_jsonNull();
                        tLRPC$TL_help_saveAppLog.events.add(tLRPC$TL_inputAppEvent2);
                        ConnectionsManager.getInstance(i2).sendRequest(tLRPC$TL_help_saveAppLog, PushListenerController$$ExternalSyntheticLambda8.INSTANCE);
                        z = false;
                    }
                    AndroidUtilities.runOnUIThread(new PushListenerController$$ExternalSyntheticLambda2(i2, i, str));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendRegistrationToServer$0(TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            SharedConfig.pushStatSent = true;
            SharedConfig.saveConfig();
        }
    }

    public static void processRemoteMessage(int i, String str, long j) {
        String str2 = i == 2 ? "FCM" : "HCM";
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d(str2 + " PRE START PROCESSING");
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        AndroidUtilities.runOnUIThread(new PushListenerController$$ExternalSyntheticLambda6(str2, str, j));
        try {
            countDownLatch.await();
        } catch (Throwable unused) {
        }
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("finished " + str2 + " service, time = " + (SystemClock.elapsedRealtime() - elapsedRealtime));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$processRemoteMessage$8(String str, String str2, long j) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d(str + " PRE INIT APP");
        }
        ApplicationLoader.postInitApplication();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d(str + " POST INIT APP");
        }
        Utilities.stageQueue.postRunnable(new PushListenerController$$ExternalSyntheticLambda5(str, str2, j));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v10, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v22, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v62, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v44, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v45, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v46, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v47, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v13, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v16, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v19, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v22, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v25, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v115, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v78, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v79, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v29, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v138, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v77, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v32, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v108, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v35, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v165, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v168, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v171, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v177, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v179, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v158, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v108, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v187, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v143, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v190, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v193, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v149, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v196, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v199, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v155, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v202, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v158, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v205, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v210, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v167, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v169, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v134, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v173, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v138, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v175, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v141, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v177, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v143, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v181, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v148, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v149, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v153, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v154, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v155, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v158, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v163, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v167, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v169, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v41, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v44, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v179, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v200, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v190, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v209, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v196, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v216, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v198, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v200, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v201, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v223, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v206, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v207, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v207, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v208, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v209, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v210, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v212, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v213, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v214, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v216, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v217, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v217, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v298, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v221, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v305, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v223, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v312, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v252, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v226, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v227, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v228, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v229, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v232, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v262, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v264, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v266, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v269, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v244, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v273, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v276, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v278, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v280, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v252, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v285, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v256, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v287, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v257, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v258, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v292, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v260, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v294, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v262, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v263, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v260, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v264, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v299, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v266, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v301, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v268, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v269, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v306, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v270, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v308, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v272, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v273, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v270, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v274, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v313, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v275, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v276, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v315, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v277, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v278, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v275, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v279, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v320, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v280, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v281, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v322, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v278, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v279, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v483, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v484, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v485, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v488, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v48, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v49, resolved type: java.lang.String[]} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x021c, code lost:
        if (r14 == 0) goto L_0x1e50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x021e, code lost:
        if (r14 == 1) goto L_0x1e06;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x0220, code lost:
        if (r14 == 2) goto L_0x1df5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x0222, code lost:
        if (r14 == 3) goto L_0x1dd9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:117:0x022c, code lost:
        if (r11.has("channel_id") == false) goto L_0x023e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:118:0x022e, code lost:
        r16 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:?, code lost:
        r5 = r11.getLong("channel_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x0236, code lost:
        r29 = r4;
        r3 = -r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x023a, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x023b, code lost:
        r29 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x023e, code lost:
        r29 = r4;
        r16 = r6;
        r3 = 0;
        r5 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x024a, code lost:
        if (r11.has("from_id") == false) goto L_0x025a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:?, code lost:
        r3 = r11.getLong("from_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x0252, code lost:
        r30 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x0255, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x0256, code lost:
        r1 = r0;
        r5 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x025a, code lost:
        r30 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:0x0262, code lost:
        if (r11.has("chat_id") == false) goto L_0x0275;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:?, code lost:
        r3 = r11.getLong("chat_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x026a, code lost:
        r14 = r9;
        r49 = r3;
        r3 = -r3;
        r8 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0272, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x0275, code lost:
        r14 = r9;
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:0x027d, code lost:
        if (r11.has("encryption_id") == false) goto L_0x028e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:?, code lost:
        r3 = org.telegram.messenger.DialogObject.makeEncryptedDialogId((long) r11.getInt("encryption_id"));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x028b, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:157:0x0294, code lost:
        if (r11.has("schedule") == false) goto L_0x02a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:161:0x029c, code lost:
        if (r11.getInt("schedule") != 1) goto L_0x02a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x029e, code lost:
        r15 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:163:0x02a0, code lost:
        r15 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:165:0x02a3, code lost:
        if (r3 != 0) goto L_0x02af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:167:0x02ab, code lost:
        if ("ENCRYPTED_MESSAGE".equals(r14) == false) goto L_0x02af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x02ad, code lost:
        r3 = org.telegram.messenger.NotificationsController.globalSecretChatId;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x02b1, code lost:
        if (r3 == 0) goto L_0x1da9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:175:0x02bb, code lost:
        if ("READ_HISTORY".equals(r14) == false) goto L_0x0334;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:?, code lost:
        r2 = r11.getInt("max_id");
        r10 = new java.util.ArrayList();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:179:0x02ca, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x02e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x02cc, code lost:
        org.telegram.messenger.FileLog.d(r1 + " received read notification max_id = " + r2 + " for dialogId = " + r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:0x02ed, code lost:
        if (r5 == 0) goto L_0x02fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:183:0x02ef, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox();
        r1.channel_id = r5;
        r1.max_id = r2;
        r10.add(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x02fc, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox();
        r3 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:0x0307, code lost:
        if (r3 == 0) goto L_0x0313;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x0309, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r1.peer = r5;
        r5.user_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x0313, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r1.peer = r3;
        r3.chat_id = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x031c, code lost:
        r1.max_id = r2;
        r10.add(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x0321, code lost:
        org.telegram.messenger.MessagesController.getInstance(r29).processUpdateArray(r10, (java.util.ArrayList<org.telegram.tgnet.TLRPC$User>) null, (java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat>) null, false, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x0334, code lost:
        r32 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x033e, code lost:
        if ("MESSAGE_DELETED".equals(r14) == false) goto L_0x03ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:?, code lost:
        r2 = r11.getString("messages").split(",");
        r8 = new androidx.collection.LongSparseArray();
        r9 = new java.util.ArrayList();
        r10 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x0356, code lost:
        if (r10 >= r2.length) goto L_0x0364;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x0358, code lost:
        r9.add(org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2[r10]));
        r10 = r10 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x0364, code lost:
        r8.put(-r5, r9);
        org.telegram.messenger.NotificationsController.getInstance(r29).removeDeletedMessagesFromNotifications(r8);
        org.telegram.messenger.MessagesController.getInstance(r29).deleteMessagesByPush(r3, r9, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x037e, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1da9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x0380, code lost:
        org.telegram.messenger.FileLog.d(r1 + " received " + r14 + " for dialogId = " + r3 + " mids = " + android.text.TextUtils.join(",", r9));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x03b1, code lost:
        if (android.text.TextUtils.isEmpty(r14) != false) goto L_0x1da9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x03b9, code lost:
        if (r11.has("msg_id") == false) goto L_0x03c4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:?, code lost:
        r10 = r11.getInt("msg_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x03c1, code lost:
        r30 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x03c4, code lost:
        r30 = r7;
        r10 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x03cd, code lost:
        if (r11.has("random_id") == false) goto L_0x03e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x03cf, code lost:
        r7 = org.telegram.messenger.Utilities.parseLong(r11.getString("random_id")).longValue();
        r23 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:218:0x03e4, code lost:
        r23 = r8;
        r7 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x03e8, code lost:
        if (r10 == 0) goto L_0x0427;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:220:0x03ea, code lost:
        r25 = r15;
        r9 = org.telegram.messenger.MessagesController.getInstance(r29).dialogs_read_inbox_max.get(java.lang.Long.valueOf(r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x03fc, code lost:
        if (r9 != null) goto L_0x041b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x03fe, code lost:
        r9 = java.lang.Integer.valueOf(org.telegram.messenger.MessagesStorage.getInstance(r29).getDialogReadMax(false, r3));
        r26 = "messages";
        org.telegram.messenger.MessagesController.getInstance(r29).dialogs_read_inbox_max.put(java.lang.Long.valueOf(r3), r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x041b, code lost:
        r26 = "messages";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:225:0x0421, code lost:
        if (r10 <= r9.intValue()) goto L_0x0425;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x0423, code lost:
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x0425, code lost:
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x0427, code lost:
        r26 = "messages";
        r25 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x042f, code lost:
        if (r7 == 0) goto L_0x0425;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x0439, code lost:
        if (org.telegram.messenger.MessagesStorage.getInstance(r29).checkMessageByRandomId(r7) != false) goto L_0x0425;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:0x0442, code lost:
        if (r14.startsWith("REACT_") != false) goto L_0x044a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x0448, code lost:
        if (r14.startsWith("CHAT_REACT_") == false) goto L_0x044b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x044a, code lost:
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x044b, code lost:
        if (r9 == false) goto L_0x1dab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x044d, code lost:
        r27 = r7;
        r9 = r11.optLong("chat_from_id", 0);
        r31 = r10;
        r34 = " for dialogId = ";
        r35 = r11.optLong("chat_from_broadcast_id", 0);
        r12 = r11.optLong("chat_from_group_id", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x046c, code lost:
        if (r9 != 0) goto L_0x0475;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x0470, code lost:
        if (r12 == 0) goto L_0x0473;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x0473, code lost:
        r7 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x0475, code lost:
        r7 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x047c, code lost:
        if (r11.has("mention") == false) goto L_0x0489;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x0484, code lost:
        if (r11.getInt("mention") == 0) goto L_0x0489;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x0486, code lost:
        r37 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x0489, code lost:
        r37 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x0491, code lost:
        if (r11.has("silent") == false) goto L_0x049e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x0499, code lost:
        if (r11.getInt("silent") == 0) goto L_0x049e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x049b, code lost:
        r38 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x049e, code lost:
        r38 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x04a0, code lost:
        r39 = r9;
        r9 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x04aa, code lost:
        if (r9.has("loc_args") == false) goto L_0x04c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x04ac, code lost:
        r8 = r9.getJSONArray("loc_args");
        r9 = r8.length();
        r10 = new java.lang.String[r9];
        r41 = r12;
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x04bb, code lost:
        if (r12 >= r9) goto L_0x04c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x04bd, code lost:
        r10[r12] = r8.getString(r12);
        r12 = r12 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x04c6, code lost:
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x04c8, code lost:
        r41 = r12;
        r8 = 0;
        r10 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x04cc, code lost:
        r9 = r10[r8];
        r8 = r11.has("edit_date");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x04da, code lost:
        if (r14.startsWith("CHAT_") == false) goto L_0x0510;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x04e0, code lost:
        if (org.telegram.messenger.UserObject.isReplyUser(r3) == false) goto L_0x04fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x04e2, code lost:
        r9 = r9 + " @ " + r10[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:273:0x04fe, code lost:
        if (r5 == 0) goto L_0x0502;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x0500, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:275:0x0502, code lost:
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0503, code lost:
        r12 = false;
        r16 = false;
        r49 = r11;
        r11 = r9;
        r9 = r10[1];
        r13 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0516, code lost:
        if (r14.startsWith("PINNED_") == false) goto L_0x0527;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x051c, code lost:
        if (r5 == 0) goto L_0x0520;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x051e, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0520, code lost:
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x0521, code lost:
        r13 = r11;
        r11 = null;
        r12 = false;
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x052d, code lost:
        if (r14.startsWith("CHANNEL_") == false) goto L_0x0532;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x052f, code lost:
        r11 = null;
        r12 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x0532, code lost:
        r11 = null;
        r12 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x0534, code lost:
        r13 = false;
        r16 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x0539, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x0567;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x053b, code lost:
        r43 = r9;
        r9 = new java.lang.StringBuilder();
        r9.append(r1);
        r9.append(" received message notification ");
        r9.append(r14);
        r9.append(r34);
        r9.append(r3);
        r9.append(" mid = ");
        r1 = r31;
        r9.append(r1);
        org.telegram.messenger.FileLog.d(r9.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x0567, code lost:
        r43 = r9;
        r1 = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x056f, code lost:
        if (r14.startsWith("REACT_") != false) goto L_0x1c9e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x0575, code lost:
        if (r14.startsWith("CHAT_REACT_") == false) goto L_0x0579;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x057d, code lost:
        switch(r14.hashCode()) {
            case -2100047043: goto L_0x0ad5;
            case -2091498420: goto L_0x0aca;
            case -2053872415: goto L_0x0abf;
            case -2039746363: goto L_0x0ab4;
            case -2023218804: goto L_0x0aa9;
            case -1979538588: goto L_0x0a9e;
            case -1979536003: goto L_0x0a93;
            case -1979535888: goto L_0x0a88;
            case -1969004705: goto L_0x0a7d;
            case -1946699248: goto L_0x0a72;
            case -1717283471: goto L_0x0a66;
            case -1646640058: goto L_0x0a5a;
            case -1528047021: goto L_0x0a4e;
            case -1507149394: goto L_0x0a41;
            case -1493579426: goto L_0x0a35;
            case -1482481933: goto L_0x0a29;
            case -1480102982: goto L_0x0a1c;
            case -1478041834: goto L_0x0a10;
            case -1474543101: goto L_0x0a05;
            case -1465695932: goto L_0x09f9;
            case -1374906292: goto L_0x09ed;
            case -1372940586: goto L_0x09e1;
            case -1264245338: goto L_0x09d5;
            case -1236154001: goto L_0x09c9;
            case -1236086700: goto L_0x09bd;
            case -1236077786: goto L_0x09b1;
            case -1235796237: goto L_0x09a5;
            case -1235760759: goto L_0x0999;
            case -1235686303: goto L_0x098c;
            case -1198046100: goto L_0x0981;
            case -1124254527: goto L_0x0975;
            case -1085137927: goto L_0x0969;
            case -1084856378: goto L_0x095d;
            case -1084820900: goto L_0x0951;
            case -1084746444: goto L_0x0945;
            case -819729482: goto L_0x0939;
            case -772141857: goto L_0x092d;
            case -638310039: goto L_0x0921;
            case -590403924: goto L_0x0915;
            case -589196239: goto L_0x0909;
            case -589193654: goto L_0x08fd;
            case -589193539: goto L_0x08f1;
            case -440169325: goto L_0x08e5;
            case -412748110: goto L_0x08d9;
            case -228518075: goto L_0x08cd;
            case -213586509: goto L_0x08c1;
            case -115582002: goto L_0x08b5;
            case -112621464: goto L_0x08a9;
            case -108522133: goto L_0x089d;
            case -107572034: goto L_0x088f;
            case -40534265: goto L_0x0883;
            case 52369421: goto L_0x0877;
            case 65254746: goto L_0x086b;
            case 141040782: goto L_0x085f;
            case 202550149: goto L_0x0853;
            case 309993049: goto L_0x0847;
            case 309995634: goto L_0x083b;
            case 309995749: goto L_0x082f;
            case 320532812: goto L_0x0823;
            case 328933854: goto L_0x0817;
            case 331340546: goto L_0x080b;
            case 342406591: goto L_0x07ff;
            case 344816990: goto L_0x07f3;
            case 346878138: goto L_0x07e7;
            case 350376871: goto L_0x07db;
            case 608430149: goto L_0x07cf;
            case 615714517: goto L_0x07c4;
            case 715508879: goto L_0x07b8;
            case 728985323: goto L_0x07ac;
            case 731046471: goto L_0x07a0;
            case 734545204: goto L_0x0794;
            case 802032552: goto L_0x0788;
            case 991498806: goto L_0x077c;
            case 1007364121: goto L_0x0770;
            case 1019850010: goto L_0x0764;
            case 1019917311: goto L_0x0758;
            case 1019926225: goto L_0x074c;
            case 1020207774: goto L_0x0740;
            case 1020243252: goto L_0x0734;
            case 1020317708: goto L_0x0728;
            case 1060282259: goto L_0x071c;
            case 1060349560: goto L_0x0710;
            case 1060358474: goto L_0x0704;
            case 1060640023: goto L_0x06f8;
            case 1060675501: goto L_0x06ec;
            case 1060749957: goto L_0x06df;
            case 1073049781: goto L_0x06d3;
            case 1078101399: goto L_0x06c7;
            case 1110103437: goto L_0x06bb;
            case 1160762272: goto L_0x06af;
            case 1172918249: goto L_0x06a3;
            case 1234591620: goto L_0x0697;
            case 1281128640: goto L_0x068b;
            case 1281131225: goto L_0x067f;
            case 1281131340: goto L_0x0673;
            case 1310789062: goto L_0x0666;
            case 1333118583: goto L_0x065a;
            case 1361447897: goto L_0x064e;
            case 1498266155: goto L_0x0642;
            case 1533804208: goto L_0x0636;
            case 1540131626: goto L_0x062a;
            case 1547988151: goto L_0x061e;
            case 1561464595: goto L_0x0612;
            case 1563525743: goto L_0x0606;
            case 1567024476: goto L_0x05fa;
            case 1810705077: goto L_0x05ee;
            case 1815177512: goto L_0x05e2;
            case 1954774321: goto L_0x05d6;
            case 1963241394: goto L_0x05ca;
            case 2014789757: goto L_0x05be;
            case 2022049433: goto L_0x05b2;
            case 2034984710: goto L_0x05a6;
            case 2048733346: goto L_0x059a;
            case 2099392181: goto L_0x058e;
            case 2140162142: goto L_0x0582;
            default: goto L_0x0580;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x0588, code lost:
        if (r14.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x058a, code lost:
        r9 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x0594, code lost:
        if (r14.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x0596, code lost:
        r9 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x05a0, code lost:
        if (r14.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x05a2, code lost:
        r9 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:311:0x05ac, code lost:
        if (r14.equals("CHANNEL_MESSAGE_PLAYLIST") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x05ae, code lost:
        r9 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x05b8, code lost:
        if (r14.equals("PINNED_CONTACT") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x05ba, code lost:
        r9 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x05c4, code lost:
        if (r14.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x05c6, code lost:
        r9 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x05d0, code lost:
        if (r14.equals("LOCKED_MESSAGE") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x05d2, code lost:
        r9 = 'm';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x05dc, code lost:
        if (r14.equals("CHAT_MESSAGE_PLAYLIST") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x05de, code lost:
        r9 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x05e8, code lost:
        if (r14.equals("CHANNEL_MESSAGES") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x05ea, code lost:
        r9 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x05f4, code lost:
        if (r14.equals("MESSAGE_INVOICE") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:0x05f6, code lost:
        r9 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x0600, code lost:
        if (r14.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:333:0x0602, code lost:
        r9 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x060c, code lost:
        if (r14.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x060e, code lost:
        r9 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x0618, code lost:
        if (r14.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:339:0x061a, code lost:
        r9 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x0624, code lost:
        if (r14.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x0626, code lost:
        r9 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x0630, code lost:
        if (r14.equals("MESSAGE_PLAYLIST") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x0632, code lost:
        r9 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x063c, code lost:
        if (r14.equals("MESSAGE_VIDEOS") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x063e, code lost:
        r9 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x0648, code lost:
        if (r14.equals("PHONE_CALL_MISSED") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:351:0x064a, code lost:
        r9 = 'r';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x0654, code lost:
        if (r14.equals("MESSAGE_PHOTOS") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x0656, code lost:
        r9 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x0660, code lost:
        if (r14.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x0662, code lost:
        r9 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x066c, code lost:
        if (r14.equals("MESSAGE_NOTEXT") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x066e, code lost:
        r17 = "CHAT_REACT_";
        r9 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x0679, code lost:
        if (r14.equals("MESSAGE_GIF") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x067b, code lost:
        r9 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x0685, code lost:
        if (r14.equals("MESSAGE_GEO") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x0687, code lost:
        r9 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x0691, code lost:
        if (r14.equals("MESSAGE_DOC") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x0693, code lost:
        r9 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x069d, code lost:
        if (r14.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x069f, code lost:
        r9 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x06a9, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:375:0x06ab, code lost:
        r9 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x06b5, code lost:
        if (r14.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x06b7, code lost:
        r9 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x06c1, code lost:
        if (r14.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:381:0x06c3, code lost:
        r9 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x06cd, code lost:
        if (r14.equals("CHAT_TITLE_EDITED") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x06cf, code lost:
        r9 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x06d9, code lost:
        if (r14.equals("PINNED_NOTEXT") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x06db, code lost:
        r9 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x06e5, code lost:
        if (r14.equals("MESSAGE_TEXT") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x06e7, code lost:
        r17 = "CHAT_REACT_";
        r9 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x06f2, code lost:
        if (r14.equals("MESSAGE_QUIZ") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x06f4, code lost:
        r9 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x06fe, code lost:
        if (r14.equals("MESSAGE_POLL") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x0700, code lost:
        r9 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x070a, code lost:
        if (r14.equals("MESSAGE_GAME") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x070c, code lost:
        r9 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x0716, code lost:
        if (r14.equals("MESSAGE_FWDS") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x0718, code lost:
        r9 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x0722, code lost:
        if (r14.equals("MESSAGE_DOCS") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x0724, code lost:
        r9 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x072e, code lost:
        if (r14.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x0730, code lost:
        r9 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x073a, code lost:
        if (r14.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x073c, code lost:
        r9 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x0746, code lost:
        if (r14.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x0748, code lost:
        r9 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x0752, code lost:
        if (r14.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x0754, code lost:
        r9 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x075e, code lost:
        if (r14.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x0760, code lost:
        r9 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x076a, code lost:
        if (r14.equals("CHAT_MESSAGE_DOCS") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x076c, code lost:
        r9 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x0776, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:426:0x0778, code lost:
        r9 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x0782, code lost:
        if (r14.equals("PINNED_GEOLIVE") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x0784, code lost:
        r9 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x078e, code lost:
        if (r14.equals("MESSAGE_CONTACT") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:432:0x0790, code lost:
        r9 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x079a, code lost:
        if (r14.equals("PINNED_VIDEO") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x079c, code lost:
        r9 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x07a6, code lost:
        if (r14.equals("PINNED_ROUND") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x07a8, code lost:
        r9 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x07b2, code lost:
        if (r14.equals("PINNED_PHOTO") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x07b4, code lost:
        r9 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x07be, code lost:
        if (r14.equals("PINNED_AUDIO") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:444:0x07c0, code lost:
        r9 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x07ca, code lost:
        if (r14.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x07cc, code lost:
        r9 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x07d5, code lost:
        if (r14.equals("CHAT_VOICECHAT_INVITE_YOU") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x07d7, code lost:
        r9 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x07e1, code lost:
        if (r14.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x07e3, code lost:
        r9 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x07ed, code lost:
        if (r14.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:456:0x07ef, code lost:
        r9 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:458:0x07f9, code lost:
        if (r14.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x07fb, code lost:
        r9 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x0805, code lost:
        if (r14.equals("CHAT_VOICECHAT_END") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x0807, code lost:
        r9 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x0811, code lost:
        if (r14.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x0813, code lost:
        r9 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x081d, code lost:
        if (r14.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x081f, code lost:
        r9 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x0829, code lost:
        if (r14.equals("MESSAGES") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x082b, code lost:
        r9 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0835, code lost:
        if (r14.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x0837, code lost:
        r9 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x0841, code lost:
        if (r14.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x0843, code lost:
        r9 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x084d, code lost:
        if (r14.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:480:0x084f, code lost:
        r9 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x0859, code lost:
        if (r14.equals("CHAT_VOICECHAT_INVITE") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x085b, code lost:
        r9 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x0865, code lost:
        if (r14.equals("CHAT_LEFT") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:486:0x0867, code lost:
        r9 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x0871, code lost:
        if (r14.equals("CHAT_ADD_YOU") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x0873, code lost:
        r9 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x087d, code lost:
        if (r14.equals("REACT_TEXT") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x087f, code lost:
        r9 = 'i';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x0889, code lost:
        if (r14.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x088b, code lost:
        r9 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x0895, code lost:
        if (r14.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x0897, code lost:
        r17 = "CHAT_REACT_";
        r9 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x08a3, code lost:
        if (r14.equals("AUTH_REGION") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x08a5, code lost:
        r9 = 'l';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x08af, code lost:
        if (r14.equals("CONTACT_JOINED") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x08b1, code lost:
        r9 = 'j';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x08bb, code lost:
        if (r14.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x08bd, code lost:
        r9 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x08c7, code lost:
        if (r14.equals("ENCRYPTION_REQUEST") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x08c9, code lost:
        r9 = 'n';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x08d3, code lost:
        if (r14.equals("MESSAGE_GEOLIVE") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x08d5, code lost:
        r9 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x08df, code lost:
        if (r14.equals("CHAT_DELETE_YOU") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x08e1, code lost:
        r9 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x08eb, code lost:
        if (r14.equals("AUTH_UNKNOWN") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:519:0x08ed, code lost:
        r9 = 'k';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x08f7, code lost:
        if (r14.equals("PINNED_GIF") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x08f9, code lost:
        r9 = 'g';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0903, code lost:
        if (r14.equals("PINNED_GEO") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x0905, code lost:
        r9 = 'b';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x090f, code lost:
        if (r14.equals("PINNED_DOC") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x0911, code lost:
        r9 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x091b, code lost:
        if (r14.equals("PINNED_GAME_SCORE") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x091d, code lost:
        r9 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0927, code lost:
        if (r14.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x0929, code lost:
        r9 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x0933, code lost:
        if (r14.equals("PHONE_CALL_REQUEST") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x0935, code lost:
        r9 = 'p';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x093f, code lost:
        if (r14.equals("PINNED_STICKER") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:540:0x0941, code lost:
        r9 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x094b, code lost:
        if (r14.equals("PINNED_TEXT") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x094d, code lost:
        r9 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0957, code lost:
        if (r14.equals("PINNED_QUIZ") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x0959, code lost:
        r9 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x0963, code lost:
        if (r14.equals("PINNED_POLL") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x0965, code lost:
        r9 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x096f, code lost:
        if (r14.equals("PINNED_GAME") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:552:0x0971, code lost:
        r9 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x097b, code lost:
        if (r14.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x097d, code lost:
        r9 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x0987, code lost:
        if (r14.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x0989, code lost:
        r9 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x0992, code lost:
        if (r14.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x0994, code lost:
        r17 = "CHAT_REACT_";
        r9 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x099f, code lost:
        if (r14.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x09a1, code lost:
        r9 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x09ab, code lost:
        if (r14.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x09ad, code lost:
        r9 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x09b7, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x09b9, code lost:
        r9 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x09c3, code lost:
        if (r14.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x09c5, code lost:
        r9 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x09cf, code lost:
        if (r14.equals("CHANNEL_MESSAGE_DOCS") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:576:0x09d1, code lost:
        r9 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x09db, code lost:
        if (r14.equals("PINNED_INVOICE") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x09dd, code lost:
        r9 = 'f';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x09e7, code lost:
        if (r14.equals("CHAT_RETURNED") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:582:0x09e9, code lost:
        r9 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x09f3, code lost:
        if (r14.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x09f5, code lost:
        r9 = 'h';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x09ff, code lost:
        if (r14.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:588:0x0a01, code lost:
        r9 = 'o';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x0a0b, code lost:
        if (r14.equals("MESSAGE_VIDEO") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x0a0d, code lost:
        r9 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0a16, code lost:
        if (r14.equals("MESSAGE_ROUND") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x0a18, code lost:
        r9 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x0a22, code lost:
        if (r14.equals("MESSAGE_PHOTO") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0a24, code lost:
        r17 = "CHAT_REACT_";
        r9 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0a2f, code lost:
        if (r14.equals("MESSAGE_MUTED") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:600:0x0a31, code lost:
        r9 = 'q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0a3b, code lost:
        if (r14.equals("MESSAGE_AUDIO") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0a3d, code lost:
        r9 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0a47, code lost:
        if (r14.equals("MESSAGE_RECURRING_PAY") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x0a49, code lost:
        r17 = "CHAT_REACT_";
        r9 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x0a54, code lost:
        if (r14.equals("CHAT_MESSAGES") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x0a56, code lost:
        r9 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x0a60, code lost:
        if (r14.equals("CHAT_VOICECHAT_START") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:612:0x0a62, code lost:
        r9 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x0a6c, code lost:
        if (r14.equals("CHAT_REQ_JOINED") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x0a6e, code lost:
        r9 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x0a78, code lost:
        if (r14.equals("CHAT_JOINED") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:618:0x0a7a, code lost:
        r9 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0a83, code lost:
        if (r14.equals("CHAT_ADD_MEMBER") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x0a85, code lost:
        r9 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x0a8e, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x0a90, code lost:
        r9 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x0a99, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:627:0x0a9b, code lost:
        r9 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x0aa4, code lost:
        if (r14.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x0aa6, code lost:
        r9 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x0aaf, code lost:
        if (r14.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x0ab1, code lost:
        r9 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x0aba, code lost:
        if (r14.equals("MESSAGE_STICKER") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x0abc, code lost:
        r9 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x0ac5, code lost:
        if (r14.equals("CHAT_CREATED") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x0ac7, code lost:
        r9 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x0ad0, code lost:
        if (r14.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0ad2, code lost:
        r9 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0adb, code lost:
        if (r14.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0ae2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x0add, code lost:
        r9 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x0adf, code lost:
        r17 = "CHAT_REACT_";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x0ae2, code lost:
        r17 = "CHAT_REACT_";
        r9 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x0ae5, code lost:
        r31 = "REACT_";
        r34 = r8;
        r44 = r12;
        r45 = r11;
        r46 = r5;
        r48 = r13;
        r51 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0b05, code lost:
        switch(r9) {
            case 0: goto L_0x1CLASSNAME;
            case 1: goto L_0x1CLASSNAME;
            case 2: goto L_0x1CLASSNAME;
            case 3: goto L_0x1CLASSNAME;
            case 4: goto L_0x1c0c;
            case 5: goto L_0x1bef;
            case 6: goto L_0x1bd1;
            case 7: goto L_0x1bb3;
            case 8: goto L_0x1b9c;
            case 9: goto L_0x1b7e;
            case 10: goto L_0x1b60;
            case 11: goto L_0x1b05;
            case 12: goto L_0x1ae7;
            case 13: goto L_0x1ac4;
            case 14: goto L_0x1aa1;
            case 15: goto L_0x1a7e;
            case 16: goto L_0x1a60;
            case 17: goto L_0x1a42;
            case 18: goto L_0x1a24;
            case 19: goto L_0x1a01;
            case 20: goto L_0x19e1;
            case 21: goto L_0x19e1;
            case 22: goto L_0x19be;
            case 23: goto L_0x1993;
            case 24: goto L_0x196c;
            case 25: goto L_0x1946;
            case 26: goto L_0x1920;
            case 27: goto L_0x18fa;
            case 28: goto L_0x18df;
            case 29: goto L_0x18bd;
            case 30: goto L_0x18a0;
            case 31: goto L_0x1883;
            case 32: goto L_0x1866;
            case 33: goto L_0x1848;
            case 34: goto L_0x17ef;
            case 35: goto L_0x17d1;
            case 36: goto L_0x17ae;
            case 37: goto L_0x178b;
            case 38: goto L_0x1768;
            case 39: goto L_0x174a;
            case 40: goto L_0x172c;
            case 41: goto L_0x170e;
            case 42: goto L_0x16f0;
            case 43: goto L_0x16c2;
            case 44: goto L_0x169b;
            case 45: goto L_0x1674;
            case 46: goto L_0x164d;
            case 47: goto L_0x1626;
            case 48: goto L_0x1610;
            case 49: goto L_0x15ee;
            case 50: goto L_0x15cb;
            case 51: goto L_0x15a8;
            case 52: goto L_0x1585;
            case 53: goto L_0x1562;
            case 54: goto L_0x153f;
            case 55: goto L_0x14c8;
            case 56: goto L_0x14a5;
            case 57: goto L_0x147d;
            case 58: goto L_0x1455;
            case 59: goto L_0x142d;
            case 60: goto L_0x140a;
            case 61: goto L_0x13e7;
            case 62: goto L_0x13c4;
            case 63: goto L_0x139c;
            case 64: goto L_0x1378;
            case 65: goto L_0x1350;
            case 66: goto L_0x1334;
            case 67: goto L_0x1334;
            case 68: goto L_0x131a;
            case 69: goto L_0x1300;
            case 70: goto L_0x12e1;
            case 71: goto L_0x12c7;
            case 72: goto L_0x12a7;
            case 73: goto L_0x128c;
            case 74: goto L_0x1271;
            case 75: goto L_0x1256;
            case 76: goto L_0x123b;
            case 77: goto L_0x1220;
            case 78: goto L_0x1205;
            case 79: goto L_0x11ea;
            case 80: goto L_0x11cf;
            case 81: goto L_0x119e;
            case 82: goto L_0x1171;
            case 83: goto L_0x1144;
            case 84: goto L_0x1117;
            case 85: goto L_0x10ea;
            case 86: goto L_0x10cf;
            case 87: goto L_0x1079;
            case 88: goto L_0x102d;
            case 89: goto L_0x0fe1;
            case 90: goto L_0x0var_;
            case 91: goto L_0x0var_;
            case 92: goto L_0x0efd;
            case 93: goto L_0x0e48;
            case 94: goto L_0x0dfc;
            case 95: goto L_0x0da6;
            case 96: goto L_0x0d50;
            case 97: goto L_0x0cfa;
            case 98: goto L_0x0cae;
            case 99: goto L_0x0CLASSNAME;
            case 100: goto L_0x0CLASSNAME;
            case 101: goto L_0x0bca;
            case 102: goto L_0x0b7e;
            case 103: goto L_0x0b2e;
            case 104: goto L_0x0b16;
            case 105: goto L_0x0b10;
            case 106: goto L_0x0b10;
            case 107: goto L_0x0b10;
            case 108: goto L_0x0b10;
            case 109: goto L_0x0b10;
            case 110: goto L_0x0b10;
            case 111: goto L_0x0b10;
            case 112: goto L_0x0b10;
            case 113: goto L_0x0b10;
            case 114: goto L_0x0b10;
            default: goto L_0x0b08;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0b08, code lost:
        r5 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0b10, code lost:
        r5 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0b16, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", org.telegram.messenger.R.string.YouHaveNewMessage);
        r43 = org.telegram.messenger.LocaleController.getString("SecretChatName", org.telegram.messenger.R.string.SecretChatName);
        r5 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0b32, code lost:
        if (r3 <= 0) goto L_0x0b4f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0b34, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", org.telegram.messenger.R.string.NotificationActionPinnedGifUser, r10[0], r10[1]);
        r5 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0b4f, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0b53, code lost:
        if (r7 == false) goto L_0x0b6c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0b55, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", org.telegram.messenger.R.string.NotificationActionPinnedGif, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x0b6c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", org.telegram.messenger.R.string.NotificationActionPinnedGifChannel, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0b7e, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x0b86, code lost:
        if (r3 <= 0) goto L_0x0b9f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0b88, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceUser", org.telegram.messenger.R.string.NotificationActionPinnedInvoiceUser, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x0b9f, code lost:
        if (r7 == false) goto L_0x0bb8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0ba1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", org.telegram.messenger.R.string.NotificationActionPinnedInvoice, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x0bb8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", org.telegram.messenger.R.string.NotificationActionPinnedInvoiceChannel, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0bca, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0bd2, code lost:
        if (r3 <= 0) goto L_0x0beb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x0bd4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreUser", org.telegram.messenger.R.string.NotificationActionPinnedGameScoreUser, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:671:0x0beb, code lost:
        if (r7 == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0bed, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", org.telegram.messenger.R.string.NotificationActionPinnedGameScore, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:673:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", org.telegram.messenger.R.string.NotificationActionPinnedGameScoreChannel, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0CLASSNAME, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0c1e, code lost:
        if (r3 <= 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", org.telegram.messenger.R.string.NotificationActionPinnedGameUser, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x0CLASSNAME, code lost:
        if (r7 == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", org.telegram.messenger.R.string.NotificationActionPinnedGame, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:679:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", org.telegram.messenger.R.string.NotificationActionPinnedGameChannel, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0CLASSNAME, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0c6a, code lost:
        if (r3 <= 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x0c6c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveUser, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0CLASSNAME, code lost:
        if (r7 == false) goto L_0x0c9c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", org.telegram.messenger.R.string.NotificationActionPinnedGeoLive, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:685:0x0c9c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveChannel, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0cae, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x0cb6, code lost:
        if (r3 <= 0) goto L_0x0ccf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x0cb8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", org.telegram.messenger.R.string.NotificationActionPinnedGeoUser, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0ccf, code lost:
        if (r7 == false) goto L_0x0ce8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0cd1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", org.telegram.messenger.R.string.NotificationActionPinnedGeo, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x0ce8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", org.telegram.messenger.R.string.NotificationActionPinnedGeoChannel, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0cfa, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0d02, code lost:
        if (r3 <= 0) goto L_0x0d1b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0d04, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", org.telegram.messenger.R.string.NotificationActionPinnedPollUser, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0d1b, code lost:
        if (r7 == false) goto L_0x0d39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0d1d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", org.telegram.messenger.R.string.NotificationActionPinnedPoll2, r10[0], r10[2], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0d39, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", org.telegram.messenger.R.string.NotificationActionPinnedPollChannel2, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0d50, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0d58, code lost:
        if (r3 <= 0) goto L_0x0d71;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0d5a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", org.telegram.messenger.R.string.NotificationActionPinnedQuizUser, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0d71, code lost:
        if (r7 == false) goto L_0x0d8f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0d73, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", org.telegram.messenger.R.string.NotificationActionPinnedQuiz2, r10[0], r10[2], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0d8f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", org.telegram.messenger.R.string.NotificationActionPinnedQuizChannel2, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0da6, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0dae, code lost:
        if (r3 <= 0) goto L_0x0dc7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0db0, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", org.telegram.messenger.R.string.NotificationActionPinnedContactUser, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0dc7, code lost:
        if (r7 == false) goto L_0x0de5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0dc9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", org.telegram.messenger.R.string.NotificationActionPinnedContact2, r10[0], r10[2], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0de5, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", org.telegram.messenger.R.string.NotificationActionPinnedContactChannel2, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0dfc, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0e04, code lost:
        if (r3 <= 0) goto L_0x0e1d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0e06, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", org.telegram.messenger.R.string.NotificationActionPinnedVoiceUser, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0e1d, code lost:
        if (r7 == false) goto L_0x0e36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0e1f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", org.telegram.messenger.R.string.NotificationActionPinnedVoice, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0e36, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", org.telegram.messenger.R.string.NotificationActionPinnedVoiceChannel, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0e48, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0e50, code lost:
        if (r3 <= 0) goto L_0x0e87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0e54, code lost:
        if (r10.length <= 1) goto L_0x0e75;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:0x0e5c, code lost:
        if (android.text.TextUtils.isEmpty(r10[1]) != false) goto L_0x0e75;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0e5e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiUser, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0e75, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", org.telegram.messenger.R.string.NotificationActionPinnedStickerUser, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0e87, code lost:
        if (r7 == false) goto L_0x0ec8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0e8b, code lost:
        if (r10.length <= 2) goto L_0x0eb1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0e93, code lost:
        if (android.text.TextUtils.isEmpty(r10[2]) != false) goto L_0x0eb1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0e95, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", org.telegram.messenger.R.string.NotificationActionPinnedStickerEmoji, r10[0], r10[2], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0eb1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", org.telegram.messenger.R.string.NotificationActionPinnedSticker, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0eca, code lost:
        if (r10.length <= 1) goto L_0x0eeb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0ed2, code lost:
        if (android.text.TextUtils.isEmpty(r10[1]) != false) goto L_0x0eeb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x0ed4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiChannel, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0eeb, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", org.telegram.messenger.R.string.NotificationActionPinnedStickerChannel, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0efd, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0var_, code lost:
        if (r3 <= 0) goto L_0x0f1e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", org.telegram.messenger.R.string.NotificationActionPinnedFileUser, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0f1e, code lost:
        if (r7 == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", org.telegram.messenger.R.string.NotificationActionPinnedFile, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", org.telegram.messenger.R.string.NotificationActionPinnedFileChannel, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x0var_, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x0var_, code lost:
        if (r3 <= 0) goto L_0x0f6a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", org.telegram.messenger.R.string.NotificationActionPinnedRoundUser, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x0f6a, code lost:
        if (r7 == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x0f6c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", org.telegram.messenger.R.string.NotificationActionPinnedRound, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", org.telegram.messenger.R.string.NotificationActionPinnedRoundChannel, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0var_, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x0f9d, code lost:
        if (r3 <= 0) goto L_0x0fb6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x0f9f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", org.telegram.messenger.R.string.NotificationActionPinnedVideoUser, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x0fb6, code lost:
        if (r7 == false) goto L_0x0fcf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:0x0fb8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", org.telegram.messenger.R.string.NotificationActionPinnedVideo, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x0fcf, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", org.telegram.messenger.R.string.NotificationActionPinnedVideoChannel, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x0fe1, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x0fe9, code lost:
        if (r3 <= 0) goto L_0x1002;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x0feb, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", org.telegram.messenger.R.string.NotificationActionPinnedPhotoUser, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x1002, code lost:
        if (r7 == false) goto L_0x101b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x1004, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", org.telegram.messenger.R.string.NotificationActionPinnedPhoto, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x101b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", org.telegram.messenger.R.string.NotificationActionPinnedPhotoChannel, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x102d, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x1035, code lost:
        if (r3 <= 0) goto L_0x104e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x1037, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", org.telegram.messenger.R.string.NotificationActionPinnedNoTextUser, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x104e, code lost:
        if (r7 == false) goto L_0x1067;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:0x1050, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", org.telegram.messenger.R.string.NotificationActionPinnedNoText, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x1067, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedNoTextChannel, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x1079, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x1081, code lost:
        if (r3 <= 0) goto L_0x109a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x1083, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", org.telegram.messenger.R.string.NotificationActionPinnedTextUser, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x109a, code lost:
        if (r7 == false) goto L_0x10b8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x109c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.R.string.NotificationActionPinnedText, r10[0], r10[1], r10[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x10b8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedTextChannel, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x10cf, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", org.telegram.messenger.R.string.NotificationGroupAlbum, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x10ea, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", org.telegram.messenger.R.string.NotificationGroupFew, r10[0], r10[1], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x1117, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", org.telegram.messenger.R.string.NotificationGroupFew, r10[0], r10[1], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x1144, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", org.telegram.messenger.R.string.NotificationGroupFew, r10[0], r10[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x1171, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", org.telegram.messenger.R.string.NotificationGroupFew, r10[0], r10[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x119e, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", org.telegram.messenger.R.string.NotificationGroupForwardedFew, r10[0], r10[1], org.telegram.messenger.LocaleController.formatPluralString(r26, org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x11cf, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("UserAcceptedToGroupPushWithGroup", org.telegram.messenger.R.string.UserAcceptedToGroupPushWithGroup, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x11ea, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", org.telegram.messenger.R.string.NotificationGroupAddSelfMega, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x1205, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", org.telegram.messenger.R.string.NotificationGroupAddSelf, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x1220, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", org.telegram.messenger.R.string.NotificationGroupLeftMember, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x123b, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", org.telegram.messenger.R.string.NotificationGroupKickYou, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x1256, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", org.telegram.messenger.R.string.NotificationGroupKickMember, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x1271, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", org.telegram.messenger.R.string.NotificationGroupInvitedYouToCall, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x128c, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupEndedCall", org.telegram.messenger.R.string.NotificationGroupEndedCall, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x12a7, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", org.telegram.messenger.R.string.NotificationGroupInvitedToCall, r10[0], r10[1], r10[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x12c7, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", org.telegram.messenger.R.string.NotificationGroupCreatedCall, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x12e1, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", org.telegram.messenger.R.string.NotificationGroupAddMember, r10[0], r10[1], r10[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x1300, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", org.telegram.messenger.R.string.NotificationEditedGroupPhoto, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x131a, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", org.telegram.messenger.R.string.NotificationEditedGroupName, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x1334, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", org.telegram.messenger.R.string.NotificationInvitedToGroup, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x134d, code lost:
        r5 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x1350, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", org.telegram.messenger.R.string.NotificationMessageGroupInvoice, r10[0], r10[1], r10[2]);
        r1 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", org.telegram.messenger.R.string.PaymentInvoice);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x1378, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", org.telegram.messenger.R.string.NotificationMessageGroupGameScored, r10[0], r10[1], r10[2], r10[3]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x139c, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", org.telegram.messenger.R.string.NotificationMessageGroupGame, r10[0], r10[1], r10[2]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachGame", org.telegram.messenger.R.string.AttachGame);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x13c4, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", org.telegram.messenger.R.string.NotificationMessageGroupGif, r10[0], r10[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachGif", org.telegram.messenger.R.string.AttachGif);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x13e7, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", org.telegram.messenger.R.string.NotificationMessageGroupLiveLocation, r10[0], r10[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", org.telegram.messenger.R.string.AttachLiveLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x140a, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", org.telegram.messenger.R.string.NotificationMessageGroupMap, r10[0], r10[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachLocation", org.telegram.messenger.R.string.AttachLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x142d, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", org.telegram.messenger.R.string.NotificationMessageGroupPoll2, r10[0], r10[1], r10[2]);
        r1 = org.telegram.messenger.LocaleController.getString("Poll", org.telegram.messenger.R.string.Poll);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x1455, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", org.telegram.messenger.R.string.NotificationMessageGroupQuiz2, r10[0], r10[1], r10[2]);
        r1 = org.telegram.messenger.LocaleController.getString("PollQuiz", org.telegram.messenger.R.string.PollQuiz);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x147d, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", org.telegram.messenger.R.string.NotificationMessageGroupContact2, r10[0], r10[1], r10[2]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachContact", org.telegram.messenger.R.string.AttachContact);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x14a5, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", org.telegram.messenger.R.string.NotificationMessageGroupAudio, r10[0], r10[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachAudio", org.telegram.messenger.R.string.AttachAudio);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x14c8, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x14ce, code lost:
        if (r10.length <= 2) goto L_0x150e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x14d6, code lost:
        if (android.text.TextUtils.isEmpty(r10[2]) != false) goto L_0x150e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x14d8, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", org.telegram.messenger.R.string.NotificationMessageGroupStickerEmoji, r10[0], r10[1], r10[2]);
        r1 = r10[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x150e, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", org.telegram.messenger.R.string.NotificationMessageGroupSticker, r10[0], r10[1]);
        r1 = r10[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x153f, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", org.telegram.messenger.R.string.NotificationMessageGroupDocument, r10[0], r10[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachDocument", org.telegram.messenger.R.string.AttachDocument);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x1562, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", org.telegram.messenger.R.string.NotificationMessageGroupRound, r10[0], r10[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachRound", org.telegram.messenger.R.string.AttachRound);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x1585, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", org.telegram.messenger.R.string.NotificationMessageGroupVideo, r10[0], r10[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachVideo", org.telegram.messenger.R.string.AttachVideo);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x15a8, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", org.telegram.messenger.R.string.NotificationMessageGroupPhoto, r10[0], r10[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachPhoto", org.telegram.messenger.R.string.AttachPhoto);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x15cb, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", org.telegram.messenger.R.string.NotificationMessageGroupNoText, r10[0], r10[1]);
        r1 = org.telegram.messenger.LocaleController.getString("Message", org.telegram.messenger.R.string.Message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x15ee, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", org.telegram.messenger.R.string.NotificationMessageGroupText, r10[0], r10[1], r10[2]);
        r1 = r10[2];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x1610, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", org.telegram.messenger.R.string.ChannelMessageAlbum, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x1626, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", org.telegram.messenger.R.string.ChannelMessageFew, r10[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x164d, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", org.telegram.messenger.R.string.ChannelMessageFew, r10[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x1674, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", org.telegram.messenger.R.string.ChannelMessageFew, r10[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x169b, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", org.telegram.messenger.R.string.ChannelMessageFew, r10[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x16c2, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", org.telegram.messenger.R.string.ChannelMessageFew, r10[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]).toLowerCase());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x16f0, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", org.telegram.messenger.R.string.NotificationMessageGame, r10[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachGame", org.telegram.messenger.R.string.AttachGame);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x170e, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", org.telegram.messenger.R.string.ChannelMessageGIF, r10[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachGif", org.telegram.messenger.R.string.AttachGif);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x172c, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", org.telegram.messenger.R.string.ChannelMessageLiveLocation, r10[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", org.telegram.messenger.R.string.AttachLiveLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x174a, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", org.telegram.messenger.R.string.ChannelMessageMap, r10[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachLocation", org.telegram.messenger.R.string.AttachLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x1768, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", org.telegram.messenger.R.string.ChannelMessagePoll2, r10[0], r10[1]);
        r1 = org.telegram.messenger.LocaleController.getString("Poll", org.telegram.messenger.R.string.Poll);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x178b, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", org.telegram.messenger.R.string.ChannelMessageQuiz2, r10[0], r10[1]);
        r1 = org.telegram.messenger.LocaleController.getString("QuizPoll", org.telegram.messenger.R.string.QuizPoll);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x17ae, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", org.telegram.messenger.R.string.ChannelMessageContact2, r10[0], r10[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachContact", org.telegram.messenger.R.string.AttachContact);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x17d1, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", org.telegram.messenger.R.string.ChannelMessageAudio, r10[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachAudio", org.telegram.messenger.R.string.AttachAudio);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x17ef, code lost:
        r6 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x17f5, code lost:
        if (r10.length <= 1) goto L_0x1830;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x17fd, code lost:
        if (android.text.TextUtils.isEmpty(r10[1]) != false) goto L_0x1830;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x17ff, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", org.telegram.messenger.R.string.ChannelMessageStickerEmoji, r10[0], r10[1]);
        r1 = r10[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x1830, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", org.telegram.messenger.R.string.ChannelMessageSticker, r10[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x1848, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", org.telegram.messenger.R.string.ChannelMessageDocument, r10[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachDocument", org.telegram.messenger.R.string.AttachDocument);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x1866, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", org.telegram.messenger.R.string.ChannelMessageRound, r10[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachRound", org.telegram.messenger.R.string.AttachRound);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x1883, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", org.telegram.messenger.R.string.ChannelMessageVideo, r10[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachVideo", org.telegram.messenger.R.string.AttachVideo);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x18a0, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", org.telegram.messenger.R.string.ChannelMessagePhoto, r10[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachPhoto", org.telegram.messenger.R.string.AttachPhoto);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x18bd, code lost:
        r6 = r17;
        r9 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", org.telegram.messenger.R.string.ChannelMessageNoText, r10[0]);
        r1 = org.telegram.messenger.LocaleController.getString("Message", org.telegram.messenger.R.string.Message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x18d9, code lost:
        r18 = r1;
        r1 = r5;
        r5 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x18df, code lost:
        r6 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", org.telegram.messenger.R.string.NotificationMessageAlbum, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x18f3, code lost:
        r5 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x18f4, code lost:
        r18 = null;
        r25 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x18fa, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", org.telegram.messenger.R.string.NotificationMessageFew, r10[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x1920, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", org.telegram.messenger.R.string.NotificationMessageFew, r10[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x1946, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", org.telegram.messenger.R.string.NotificationMessageFew, r10[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x196c, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", org.telegram.messenger.R.string.NotificationMessageFew, r10[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x1993, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", org.telegram.messenger.R.string.NotificationMessageForwardFew, r10[0], org.telegram.messenger.LocaleController.formatPluralString(r26, org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x19be, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", org.telegram.messenger.R.string.NotificationMessageInvoice, r10[0], r10[1]);
        r2 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", org.telegram.messenger.R.string.PaymentInvoice);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:851:0x19e1, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", org.telegram.messenger.R.string.NotificationMessageGameScored, r10[0], r10[1], r10[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x1a01, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", org.telegram.messenger.R.string.NotificationMessageGame, r10[0], r10[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGame", org.telegram.messenger.R.string.AttachGame);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x1a24, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", org.telegram.messenger.R.string.NotificationMessageGif, r10[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachGif", org.telegram.messenger.R.string.AttachGif);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x1a42, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", org.telegram.messenger.R.string.NotificationMessageLiveLocation, r10[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", org.telegram.messenger.R.string.AttachLiveLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x1a60, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", org.telegram.messenger.R.string.NotificationMessageMap, r10[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachLocation", org.telegram.messenger.R.string.AttachLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:856:0x1a7e, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", org.telegram.messenger.R.string.NotificationMessagePoll2, r10[0], r10[1]);
        r2 = org.telegram.messenger.LocaleController.getString("Poll", org.telegram.messenger.R.string.Poll);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x1aa1, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", org.telegram.messenger.R.string.NotificationMessageQuiz2, r10[0], r10[1]);
        r2 = org.telegram.messenger.LocaleController.getString("QuizPoll", org.telegram.messenger.R.string.QuizPoll);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x1ac4, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", org.telegram.messenger.R.string.NotificationMessageContact2, r10[0], r10[1]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachContact", org.telegram.messenger.R.string.AttachContact);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x1ae7, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", org.telegram.messenger.R.string.NotificationMessageAudio, r10[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachAudio", org.telegram.messenger.R.string.AttachAudio);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x1b05, code lost:
        r5 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x1b0b, code lost:
        if (r10.length <= 1) goto L_0x1b45;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:863:0x1b13, code lost:
        if (android.text.TextUtils.isEmpty(r10[1]) != false) goto L_0x1b45;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:864:0x1b15, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", org.telegram.messenger.R.string.NotificationMessageStickerEmoji, r10[0], r10[1]);
        r1 = r10[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:865:0x1b45, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", org.telegram.messenger.R.string.NotificationMessageSticker, r10[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:866:0x1b5b, code lost:
        r18 = r1;
        r1 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x1b60, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", org.telegram.messenger.R.string.NotificationMessageDocument, r10[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDocument", org.telegram.messenger.R.string.AttachDocument);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x1b7e, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", org.telegram.messenger.R.string.NotificationMessageRound, r10[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachRound", org.telegram.messenger.R.string.AttachRound);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x1b9c, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", org.telegram.messenger.R.string.ActionTakeScreenshoot).replace("un1", r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x1bb3, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", org.telegram.messenger.R.string.NotificationMessageSDVideo, r10[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", org.telegram.messenger.R.string.AttachDestructingVideo);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:871:0x1bd1, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", org.telegram.messenger.R.string.NotificationMessageVideo, r10[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachVideo", org.telegram.messenger.R.string.AttachVideo);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x1bef, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", org.telegram.messenger.R.string.NotificationMessageSDPhoto, r10[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", org.telegram.messenger.R.string.AttachDestructingPhoto);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x1c0c, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", org.telegram.messenger.R.string.NotificationMessagePhoto, r10[0]);
        r2 = org.telegram.messenger.LocaleController.getString("AttachPhoto", org.telegram.messenger.R.string.AttachPhoto);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x1CLASSNAME, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", org.telegram.messenger.R.string.NotificationMessageNoText, r10[0]);
        r2 = org.telegram.messenger.LocaleController.getString("Message", org.telegram.messenger.R.string.Message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x1CLASSNAME, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", org.telegram.messenger.R.string.NotificationMessageText, r10[0], r10[1]);
        r2 = r10[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x1CLASSNAME, code lost:
        r18 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x1CLASSNAME, code lost:
        r5 = r17;
        r9 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRecurringPay", org.telegram.messenger.R.string.NotificationMessageRecurringPay, r10[0], r10[1]);
        r2 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", org.telegram.messenger.R.string.PaymentInvoice);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x1CLASSNAME, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1c9c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x1CLASSNAME, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x1c9c, code lost:
        r1 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x1c9e, code lost:
        r51 = r1;
        r31 = "REACT_";
        r46 = r5;
        r34 = r8;
        r45 = r11;
        r44 = r12;
        r48 = r13;
        r5 = "CHAT_REACT_";
        r9 = r25;
        r1 = getReactedText(r14, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x1cb3, code lost:
        r18 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x1cb5, code lost:
        r25 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x1cb7, code lost:
        if (r1 == null) goto L_0x1dab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:0x1cb9, code lost:
        r2 = new org.telegram.tgnet.TLRPC$TL_message();
        r2.id = r51;
        r2.random_id = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x1cc6, code lost:
        if (r18 == null) goto L_0x1ccb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x1cc8, code lost:
        r6 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x1ccb, code lost:
        r6 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x1ccc, code lost:
        r2.message = r6;
        r2.date = (int) (r53 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x1cd5, code lost:
        if (r16 == false) goto L_0x1cde;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x1cd7, code lost:
        r2.action = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x1cde, code lost:
        if (r48 == false) goto L_0x1ce7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x1ce0, code lost:
        r2.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x1ce7, code lost:
        r2.dialog_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x1ced, code lost:
        if (r46 == 0) goto L_0x1cfd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x1cef, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r2.peer_id = r3;
        r3.channel_id = r46;
        r12 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x1d01, code lost:
        if (r23 == 0) goto L_0x1d0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:899:0x1d03, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r2.peer_id = r3;
        r12 = r23;
        r3.chat_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:0x1d0f, code lost:
        r12 = r23;
        r3 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r2.peer_id = r3;
        r3.user_id = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x1d1c, code lost:
        r2.flags |= 256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x1d26, code lost:
        if (r41 == 0) goto L_0x1d32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:0x1d28, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r2.from_id = r3;
        r3.chat_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x1d36, code lost:
        if (r35 == 0) goto L_0x1d44;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:906:0x1d38, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r2.from_id = r3;
        r3.channel_id = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x1d48, code lost:
        if (r39 == 0) goto L_0x1d56;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:909:0x1d4a, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r2.from_id = r3;
        r3.user_id = r39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:910:0x1d56, code lost:
        r2.from_id = r2.peer_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:0x1d5a, code lost:
        if (r37 != false) goto L_0x1d61;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:0x1d5c, code lost:
        if (r16 == false) goto L_0x1d5f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:0x1d5f, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:0x1d61, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x1d62, code lost:
        r2.mentioned = r3;
        r2.silent = r38;
        r2.from_scheduled = r9;
        r19 = new org.telegram.messenger.MessageObject(r29, r2, r1, r43, r45, r25, r44, r48, r34);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:917:0x1d87, code lost:
        if (r14.startsWith(r31) != false) goto L_0x1d92;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:0x1d8d, code lost:
        if (r14.startsWith(r5) == false) goto L_0x1d90;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x1d90, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x1d92, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x1d93, code lost:
        r19.isReactionPush = r1;
        r1 = new java.util.ArrayList();
        r1.add(r19);
        org.telegram.messenger.NotificationsController.getInstance(r29).processNewMessages(r1, true, true, countDownLatch);
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x1da9, code lost:
        r30 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x1dab, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x1dac, code lost:
        if (r8 == false) goto L_0x1db3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x1dae, code lost:
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:928:0x1db3, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r29);
        org.telegram.tgnet.ConnectionsManager.getInstance(r29).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:0x1dbf, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x1dc0, code lost:
        r1 = r0;
        r5 = r14;
        r4 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x1dc6, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:932:0x1dc7, code lost:
        r30 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x1dca, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x1dcb, code lost:
        r30 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:935:0x1dcd, code lost:
        r14 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:0x1dce, code lost:
        r1 = r0;
        r5 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:0x1dd0, code lost:
        r4 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:0x1dd4, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x1dd5, code lost:
        r29 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:0x1dd9, code lost:
        r29 = r4;
        r30 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:943:0x1de2, code lost:
        r4 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda0(r4));
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:946:0x1def, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x1df0, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x1df1, code lost:
        r4 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:0x1df5, code lost:
        r30 = r7;
        r14 = r9;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda1(r4));
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x1e05, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x1e06, code lost:
        r30 = r7;
        r14 = r9;
        r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification();
        r1.popup = false;
        r1.flags = 2;
        r1.inbox_date = (int) (r53 / 1000);
        r1.message = r6.getString("message");
        r1.type = "announcement";
        r1.media = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty();
        r2 = new org.telegram.tgnet.TLRPC$TL_updates();
        r2.updates.add(r1);
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda3(r4, r2));
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).resumeNetworkMaybe();
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x1e4f, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x1e50, code lost:
        r30 = r7;
        r14 = r9;
        r1 = r11.getInt("dc");
        r2 = r11.getString("addr").split(":");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:0x1e67, code lost:
        if (r2.length == 2) goto L_0x1e6f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x1e69, code lost:
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:0x1e6e, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:957:0x1e6f, code lost:
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).applyDatacenterAddress(r1, r2[0], java.lang.Integer.parseInt(r2[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).resumeNetworkMaybe();
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:0x1e8c, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x1e8d, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x1e8e, code lost:
        r1 = r0;
        r5 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:0x1e90, code lost:
        r7 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:989:?, code lost:
        return;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:974:0x1eb4  */
    /* JADX WARNING: Removed duplicated region for block: B:975:0x1ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:978:0x1ecb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$processRemoteMessage$7(java.lang.String r51, java.lang.String r52, long r53) {
        /*
            r1 = r51
            java.lang.String r2 = "REACT_"
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x001c
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r1)
            java.lang.String r4 = " START PROCESSING"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.d(r3)
        L_0x001c:
            r4 = 8
            r6 = r52
            byte[] r6 = android.util.Base64.decode(r6, r4)     // Catch:{ all -> 0x1eac }
            org.telegram.tgnet.NativeByteBuffer r7 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x1eac }
            int r8 = r6.length     // Catch:{ all -> 0x1eac }
            r7.<init>((int) r8)     // Catch:{ all -> 0x1eac }
            r7.writeBytes((byte[]) r6)     // Catch:{ all -> 0x1eac }
            r8 = 0
            r7.position(r8)     // Catch:{ all -> 0x1eac }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1eac }
            if (r9 != 0) goto L_0x0046
            byte[] r9 = new byte[r4]     // Catch:{ all -> 0x1eac }
            org.telegram.messenger.SharedConfig.pushAuthKeyId = r9     // Catch:{ all -> 0x1eac }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1eac }
            byte[] r9 = org.telegram.messenger.Utilities.computeSHA1((byte[]) r9)     // Catch:{ all -> 0x1eac }
            int r10 = r9.length     // Catch:{ all -> 0x1eac }
            int r10 = r10 - r4
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1eac }
            java.lang.System.arraycopy(r9, r10, r11, r8, r4)     // Catch:{ all -> 0x1eac }
        L_0x0046:
            byte[] r9 = new byte[r4]     // Catch:{ all -> 0x1eac }
            r10 = 1
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1eac }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1eac }
            boolean r11 = java.util.Arrays.equals(r11, r9)     // Catch:{ all -> 0x1eac }
            r12 = 3
            r13 = 2
            if (r11 != 0) goto L_0x0090
            onDecryptError()     // Catch:{ all -> 0x1eac }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1eac }
            if (r2 == 0) goto L_0x008f
            java.util.Locale r2 = java.util.Locale.US     // Catch:{ all -> 0x1eac }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x1eac }
            r4.<init>()     // Catch:{ all -> 0x1eac }
            r4.append(r1)     // Catch:{ all -> 0x1eac }
            java.lang.String r1 = " DECRYPT ERROR 2 k1=%s k2=%s, key=%s"
            r4.append(r1)     // Catch:{ all -> 0x1eac }
            java.lang.String r1 = r4.toString()     // Catch:{ all -> 0x1eac }
            java.lang.Object[] r4 = new java.lang.Object[r12]     // Catch:{ all -> 0x1eac }
            byte[] r6 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1eac }
            java.lang.String r6 = org.telegram.messenger.Utilities.bytesToHex(r6)     // Catch:{ all -> 0x1eac }
            r4[r8] = r6     // Catch:{ all -> 0x1eac }
            java.lang.String r6 = org.telegram.messenger.Utilities.bytesToHex(r9)     // Catch:{ all -> 0x1eac }
            r4[r10] = r6     // Catch:{ all -> 0x1eac }
            byte[] r6 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1eac }
            java.lang.String r6 = org.telegram.messenger.Utilities.bytesToHex(r6)     // Catch:{ all -> 0x1eac }
            r4[r13] = r6     // Catch:{ all -> 0x1eac }
            java.lang.String r1 = java.lang.String.format(r2, r1, r4)     // Catch:{ all -> 0x1eac }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x1eac }
        L_0x008f:
            return
        L_0x0090:
            r9 = 16
            byte[] r9 = new byte[r9]     // Catch:{ all -> 0x1eac }
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1eac }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1eac }
            org.telegram.messenger.MessageKeyData r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13)     // Catch:{ all -> 0x1eac }
            java.nio.ByteBuffer r14 = r7.buffer     // Catch:{ all -> 0x1eac }
            byte[] r15 = r11.aesKey     // Catch:{ all -> 0x1eac }
            byte[] r11 = r11.aesIv     // Catch:{ all -> 0x1eac }
            r17 = 0
            r18 = 0
            r19 = 24
            int r6 = r6.length     // Catch:{ all -> 0x1eac }
            int r20 = r6 + -24
            r16 = r11
            org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x1eac }
            byte[] r21 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1eac }
            r22 = 96
            r23 = 32
            java.nio.ByteBuffer r6 = r7.buffer     // Catch:{ all -> 0x1eac }
            r25 = 24
            int r26 = r6.limit()     // Catch:{ all -> 0x1eac }
            r24 = r6
            byte[] r6 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26)     // Catch:{ all -> 0x1eac }
            boolean r6 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r6, r4)     // Catch:{ all -> 0x1eac }
            if (r6 != 0) goto L_0x00f5
            onDecryptError()     // Catch:{ all -> 0x1eac }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1eac }
            if (r2 == 0) goto L_0x00f4
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x1eac }
            r2.<init>()     // Catch:{ all -> 0x1eac }
            r2.append(r1)     // Catch:{ all -> 0x1eac }
            java.lang.String r1 = " DECRYPT ERROR 3, key = %s"
            r2.append(r1)     // Catch:{ all -> 0x1eac }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x1eac }
            java.lang.Object[] r2 = new java.lang.Object[r10]     // Catch:{ all -> 0x1eac }
            byte[] r4 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1eac }
            java.lang.String r4 = org.telegram.messenger.Utilities.bytesToHex(r4)     // Catch:{ all -> 0x1eac }
            r2[r8] = r4     // Catch:{ all -> 0x1eac }
            java.lang.String r1 = java.lang.String.format(r1, r2)     // Catch:{ all -> 0x1eac }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x1eac }
        L_0x00f4:
            return
        L_0x00f5:
            int r6 = r7.readInt32(r10)     // Catch:{ all -> 0x1eac }
            byte[] r6 = new byte[r6]     // Catch:{ all -> 0x1eac }
            r7.readBytes(r6, r10)     // Catch:{ all -> 0x1eac }
            java.lang.String r7 = new java.lang.String     // Catch:{ all -> 0x1eac }
            r7.<init>(r6)     // Catch:{ all -> 0x1eac }
            org.json.JSONObject r6 = new org.json.JSONObject     // Catch:{ all -> 0x1ea4 }
            r6.<init>(r7)     // Catch:{ all -> 0x1ea4 }
            java.lang.String r9 = "loc_key"
            boolean r9 = r6.has(r9)     // Catch:{ all -> 0x1ea4 }
            if (r9 == 0) goto L_0x011a
            java.lang.String r9 = "loc_key"
            java.lang.String r9 = r6.getString(r9)     // Catch:{ all -> 0x0117 }
            goto L_0x011c
        L_0x0117:
            r0 = move-exception
            goto L_0x1ea7
        L_0x011a:
            java.lang.String r9 = ""
        L_0x011c:
            java.lang.String r11 = "custom"
            java.lang.Object r11 = r6.get(r11)     // Catch:{ all -> 0x1e9b }
            boolean r11 = r11 instanceof org.json.JSONObject     // Catch:{ all -> 0x1e9b }
            if (r11 == 0) goto L_0x0132
            java.lang.String r11 = "custom"
            org.json.JSONObject r11 = r6.getJSONObject(r11)     // Catch:{ all -> 0x012d }
            goto L_0x0137
        L_0x012d:
            r0 = move-exception
            r1 = r0
            r5 = r9
            goto L_0x1ea1
        L_0x0132:
            org.json.JSONObject r11 = new org.json.JSONObject     // Catch:{ all -> 0x1e9b }
            r11.<init>()     // Catch:{ all -> 0x1e9b }
        L_0x0137:
            java.lang.String r14 = "user_id"
            boolean r14 = r6.has(r14)     // Catch:{ all -> 0x1e9b }
            if (r14 == 0) goto L_0x0146
            java.lang.String r14 = "user_id"
            java.lang.Object r14 = r6.get(r14)     // Catch:{ all -> 0x012d }
            goto L_0x0147
        L_0x0146:
            r14 = 0
        L_0x0147:
            if (r14 != 0) goto L_0x0154
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x012d }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x012d }
            long r14 = r14.getClientUserId()     // Catch:{ all -> 0x012d }
            goto L_0x0184
        L_0x0154:
            boolean r15 = r14 instanceof java.lang.Long     // Catch:{ all -> 0x1e9b }
            if (r15 == 0) goto L_0x015f
            java.lang.Long r14 = (java.lang.Long) r14     // Catch:{ all -> 0x012d }
            long r14 = r14.longValue()     // Catch:{ all -> 0x012d }
            goto L_0x0184
        L_0x015f:
            boolean r15 = r14 instanceof java.lang.Integer     // Catch:{ all -> 0x1e9b }
            if (r15 == 0) goto L_0x016b
            java.lang.Integer r14 = (java.lang.Integer) r14     // Catch:{ all -> 0x012d }
            int r14 = r14.intValue()     // Catch:{ all -> 0x012d }
        L_0x0169:
            long r14 = (long) r14
            goto L_0x0184
        L_0x016b:
            boolean r15 = r14 instanceof java.lang.String     // Catch:{ all -> 0x1e9b }
            if (r15 == 0) goto L_0x017a
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ all -> 0x012d }
            java.lang.Integer r14 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14)     // Catch:{ all -> 0x012d }
            int r14 = r14.intValue()     // Catch:{ all -> 0x012d }
            goto L_0x0169
        L_0x017a:
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1e9b }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x1e9b }
            long r14 = r14.getClientUserId()     // Catch:{ all -> 0x1e9b }
        L_0x0184:
            int r16 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1e9b }
            r4 = 0
        L_0x0187:
            r5 = 4
            if (r4 >= r5) goto L_0x019b
            org.telegram.messenger.UserConfig r19 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x012d }
            long r19 = r19.getClientUserId()     // Catch:{ all -> 0x012d }
            int r21 = (r19 > r14 ? 1 : (r19 == r14 ? 0 : -1))
            if (r21 != 0) goto L_0x0198
            r14 = 1
            goto L_0x019e
        L_0x0198:
            int r4 = r4 + 1
            goto L_0x0187
        L_0x019b:
            r4 = r16
            r14 = 0
        L_0x019e:
            if (r14 != 0) goto L_0x01be
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x012d }
            if (r2 == 0) goto L_0x01b8
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x012d }
            r2.<init>()     // Catch:{ all -> 0x012d }
            r2.append(r1)     // Catch:{ all -> 0x012d }
            java.lang.String r1 = " ACCOUNT NOT FOUND"
            r2.append(r1)     // Catch:{ all -> 0x012d }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x012d }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x012d }
        L_0x01b8:
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x012d }
            r1.countDown()     // Catch:{ all -> 0x012d }
            return
        L_0x01be:
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x1e93 }
            boolean r14 = r14.isClientActivated()     // Catch:{ all -> 0x1e93 }
            if (r14 != 0) goto L_0x01eb
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x01e6 }
            if (r2 == 0) goto L_0x01e0
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e6 }
            r2.<init>()     // Catch:{ all -> 0x01e6 }
            r2.append(r1)     // Catch:{ all -> 0x01e6 }
            java.lang.String r1 = " ACCOUNT NOT ACTIVATED"
            r2.append(r1)     // Catch:{ all -> 0x01e6 }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x01e6 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x01e6 }
        L_0x01e0:
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x01e6 }
            r1.countDown()     // Catch:{ all -> 0x01e6 }
            return
        L_0x01e6:
            r0 = move-exception
        L_0x01e7:
            r1 = r0
            r5 = r9
            goto L_0x1e99
        L_0x01eb:
            int r14 = r9.hashCode()     // Catch:{ all -> 0x1e93 }
            switch(r14) {
                case -1963663249: goto L_0x0211;
                case -920689527: goto L_0x0207;
                case 633004703: goto L_0x01fd;
                case 1365673842: goto L_0x01f3;
                default: goto L_0x01f2;
            }
        L_0x01f2:
            goto L_0x021b
        L_0x01f3:
            java.lang.String r14 = "GEO_LIVE_PENDING"
            boolean r14 = r9.equals(r14)     // Catch:{ all -> 0x01e6 }
            if (r14 == 0) goto L_0x021b
            r14 = 3
            goto L_0x021c
        L_0x01fd:
            java.lang.String r14 = "MESSAGE_ANNOUNCEMENT"
            boolean r14 = r9.equals(r14)     // Catch:{ all -> 0x01e6 }
            if (r14 == 0) goto L_0x021b
            r14 = 1
            goto L_0x021c
        L_0x0207:
            java.lang.String r14 = "DC_UPDATE"
            boolean r14 = r9.equals(r14)     // Catch:{ all -> 0x01e6 }
            if (r14 == 0) goto L_0x021b
            r14 = 0
            goto L_0x021c
        L_0x0211:
            java.lang.String r14 = "SESSION_REVOKE"
            boolean r14 = r9.equals(r14)     // Catch:{ all -> 0x01e6 }
            if (r14 == 0) goto L_0x021b
            r14 = 2
            goto L_0x021c
        L_0x021b:
            r14 = -1
        L_0x021c:
            if (r14 == 0) goto L_0x1e50
            if (r14 == r10) goto L_0x1e06
            if (r14 == r13) goto L_0x1df5
            if (r14 == r12) goto L_0x1dd9
            java.lang.String r14 = "channel_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1dd4 }
            r12 = 0
            if (r14 == 0) goto L_0x023e
            java.lang.String r14 = "channel_id"
            r16 = r6
            long r5 = r11.getLong(r14)     // Catch:{ all -> 0x023a }
            r29 = r4
            long r3 = -r5
            goto L_0x0244
        L_0x023a:
            r0 = move-exception
            r29 = r4
            goto L_0x01e7
        L_0x023e:
            r29 = r4
            r16 = r6
            r3 = r12
            r5 = r3
        L_0x0244:
            java.lang.String r14 = "from_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1dca }
            if (r14 == 0) goto L_0x025a
            java.lang.String r3 = "from_id"
            long r3 = r11.getLong(r3)     // Catch:{ all -> 0x0255 }
            r30 = r3
            goto L_0x025c
        L_0x0255:
            r0 = move-exception
            r1 = r0
            r5 = r9
            goto L_0x1dd0
        L_0x025a:
            r30 = r12
        L_0x025c:
            java.lang.String r14 = "chat_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1dca }
            if (r14 == 0) goto L_0x0275
            java.lang.String r3 = "chat_id"
            long r3 = r11.getLong(r3)     // Catch:{ all -> 0x0272 }
            r14 = r9
            long r8 = -r3
            r49 = r3
            r3 = r8
            r8 = r49
            goto L_0x0277
        L_0x0272:
            r0 = move-exception
            goto L_0x1dcd
        L_0x0275:
            r14 = r9
            r8 = r12
        L_0x0277:
            java.lang.String r15 = "encryption_id"
            boolean r15 = r11.has(r15)     // Catch:{ all -> 0x1dc6 }
            if (r15 == 0) goto L_0x028e
            java.lang.String r3 = "encryption_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x028b }
            long r3 = (long) r3     // Catch:{ all -> 0x028b }
            long r3 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r3)     // Catch:{ all -> 0x028b }
            goto L_0x028e
        L_0x028b:
            r0 = move-exception
            goto L_0x1dce
        L_0x028e:
            java.lang.String r15 = "schedule"
            boolean r15 = r11.has(r15)     // Catch:{ all -> 0x1dc6 }
            if (r15 == 0) goto L_0x02a0
            java.lang.String r15 = "schedule"
            int r15 = r11.getInt(r15)     // Catch:{ all -> 0x028b }
            if (r15 != r10) goto L_0x02a0
            r15 = 1
            goto L_0x02a1
        L_0x02a0:
            r15 = 0
        L_0x02a1:
            int r21 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r21 != 0) goto L_0x02af
            java.lang.String r10 = "ENCRYPTED_MESSAGE"
            boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x028b }
            if (r10 == 0) goto L_0x02af
            long r3 = org.telegram.messenger.NotificationsController.globalSecretChatId     // Catch:{ all -> 0x028b }
        L_0x02af:
            int r10 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r10 == 0) goto L_0x1da9
            java.lang.String r10 = "READ_HISTORY"
            boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x1dc6 }
            java.lang.String r12 = " for dialogId = "
            if (r10 == 0) goto L_0x0334
            java.lang.String r2 = "max_id"
            int r2 = r11.getInt(r2)     // Catch:{ all -> 0x028b }
            java.util.ArrayList r10 = new java.util.ArrayList     // Catch:{ all -> 0x028b }
            r10.<init>()     // Catch:{ all -> 0x028b }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x028b }
            if (r11 == 0) goto L_0x02e9
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x028b }
            r11.<init>()     // Catch:{ all -> 0x028b }
            r11.append(r1)     // Catch:{ all -> 0x028b }
            java.lang.String r1 = " received read notification max_id = "
            r11.append(r1)     // Catch:{ all -> 0x028b }
            r11.append(r2)     // Catch:{ all -> 0x028b }
            r11.append(r12)     // Catch:{ all -> 0x028b }
            r11.append(r3)     // Catch:{ all -> 0x028b }
            java.lang.String r1 = r11.toString()     // Catch:{ all -> 0x028b }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x028b }
        L_0x02e9:
            r3 = 0
            int r1 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x02fc
            org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox r1 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox     // Catch:{ all -> 0x028b }
            r1.<init>()     // Catch:{ all -> 0x028b }
            r1.channel_id = r5     // Catch:{ all -> 0x028b }
            r1.max_id = r2     // Catch:{ all -> 0x028b }
            r10.add(r1)     // Catch:{ all -> 0x028b }
            goto L_0x0321
        L_0x02fc:
            org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox r1 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox     // Catch:{ all -> 0x028b }
            r1.<init>()     // Catch:{ all -> 0x028b }
            r3 = r30
            r5 = 0
            int r11 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r11 == 0) goto L_0x0313
            org.telegram.tgnet.TLRPC$TL_peerUser r5 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x028b }
            r5.<init>()     // Catch:{ all -> 0x028b }
            r1.peer = r5     // Catch:{ all -> 0x028b }
            r5.user_id = r3     // Catch:{ all -> 0x028b }
            goto L_0x031c
        L_0x0313:
            org.telegram.tgnet.TLRPC$TL_peerChat r3 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x028b }
            r3.<init>()     // Catch:{ all -> 0x028b }
            r1.peer = r3     // Catch:{ all -> 0x028b }
            r3.chat_id = r8     // Catch:{ all -> 0x028b }
        L_0x031c:
            r1.max_id = r2     // Catch:{ all -> 0x028b }
            r10.add(r1)     // Catch:{ all -> 0x028b }
        L_0x0321:
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r29)     // Catch:{ all -> 0x028b }
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r16 = r10
            r15.processUpdateArray(r16, r17, r18, r19, r20)     // Catch:{ all -> 0x028b }
            goto L_0x1da9
        L_0x0334:
            r32 = r30
            java.lang.String r10 = "MESSAGE_DELETED"
            boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x1dc6 }
            java.lang.String r13 = "messages"
            if (r10 == 0) goto L_0x03ad
            java.lang.String r2 = r11.getString(r13)     // Catch:{ all -> 0x028b }
            java.lang.String r8 = ","
            java.lang.String[] r2 = r2.split(r8)     // Catch:{ all -> 0x028b }
            androidx.collection.LongSparseArray r8 = new androidx.collection.LongSparseArray     // Catch:{ all -> 0x028b }
            r8.<init>()     // Catch:{ all -> 0x028b }
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ all -> 0x028b }
            r9.<init>()     // Catch:{ all -> 0x028b }
            r10 = 0
        L_0x0355:
            int r11 = r2.length     // Catch:{ all -> 0x028b }
            if (r10 >= r11) goto L_0x0364
            r11 = r2[r10]     // Catch:{ all -> 0x028b }
            java.lang.Integer r11 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r11)     // Catch:{ all -> 0x028b }
            r9.add(r11)     // Catch:{ all -> 0x028b }
            int r10 = r10 + 1
            goto L_0x0355
        L_0x0364:
            long r10 = -r5
            r8.put(r10, r9)     // Catch:{ all -> 0x028b }
            org.telegram.messenger.NotificationsController r2 = org.telegram.messenger.NotificationsController.getInstance(r29)     // Catch:{ all -> 0x028b }
            r2.removeDeletedMessagesFromNotifications(r8)     // Catch:{ all -> 0x028b }
            org.telegram.messenger.MessagesController r20 = org.telegram.messenger.MessagesController.getInstance(r29)     // Catch:{ all -> 0x028b }
            r21 = r3
            r23 = r9
            r24 = r5
            r20.deleteMessagesByPush(r21, r23, r24)     // Catch:{ all -> 0x028b }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x028b }
            if (r2 == 0) goto L_0x1da9
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x028b }
            r2.<init>()     // Catch:{ all -> 0x028b }
            r2.append(r1)     // Catch:{ all -> 0x028b }
            java.lang.String r1 = " received "
            r2.append(r1)     // Catch:{ all -> 0x028b }
            r2.append(r14)     // Catch:{ all -> 0x028b }
            r2.append(r12)     // Catch:{ all -> 0x028b }
            r2.append(r3)     // Catch:{ all -> 0x028b }
            java.lang.String r1 = " mids = "
            r2.append(r1)     // Catch:{ all -> 0x028b }
            java.lang.String r1 = ","
            java.lang.String r1 = android.text.TextUtils.join(r1, r9)     // Catch:{ all -> 0x028b }
            r2.append(r1)     // Catch:{ all -> 0x028b }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x028b }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x028b }
            goto L_0x1da9
        L_0x03ad:
            boolean r10 = android.text.TextUtils.isEmpty(r14)     // Catch:{ all -> 0x1dc6 }
            if (r10 != 0) goto L_0x1da9
            java.lang.String r10 = "msg_id"
            boolean r10 = r11.has(r10)     // Catch:{ all -> 0x1dc6 }
            if (r10 == 0) goto L_0x03c4
            java.lang.String r10 = "msg_id"
            int r10 = r11.getInt(r10)     // Catch:{ all -> 0x028b }
            r30 = r7
            goto L_0x03c7
        L_0x03c4:
            r30 = r7
            r10 = 0
        L_0x03c7:
            java.lang.String r7 = "random_id"
            boolean r7 = r11.has(r7)     // Catch:{ all -> 0x1dbf }
            if (r7 == 0) goto L_0x03e4
            java.lang.String r7 = "random_id"
            java.lang.String r7 = r11.getString(r7)     // Catch:{ all -> 0x1dbf }
            java.lang.Long r7 = org.telegram.messenger.Utilities.parseLong(r7)     // Catch:{ all -> 0x1dbf }
            long r23 = r7.longValue()     // Catch:{ all -> 0x1dbf }
            r49 = r8
            r7 = r23
            r23 = r49
            goto L_0x03e8
        L_0x03e4:
            r23 = r8
            r7 = 0
        L_0x03e8:
            if (r10 == 0) goto L_0x0427
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r29)     // Catch:{ all -> 0x1dbf }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r9 = r9.dialogs_read_inbox_max     // Catch:{ all -> 0x1dbf }
            r25 = r15
            java.lang.Long r15 = java.lang.Long.valueOf(r3)     // Catch:{ all -> 0x1dbf }
            java.lang.Object r9 = r9.get(r15)     // Catch:{ all -> 0x1dbf }
            java.lang.Integer r9 = (java.lang.Integer) r9     // Catch:{ all -> 0x1dbf }
            if (r9 != 0) goto L_0x041b
            org.telegram.messenger.MessagesStorage r9 = org.telegram.messenger.MessagesStorage.getInstance(r29)     // Catch:{ all -> 0x1dbf }
            r15 = 0
            int r9 = r9.getDialogReadMax(r15, r3)     // Catch:{ all -> 0x1dbf }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x1dbf }
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r29)     // Catch:{ all -> 0x1dbf }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r15 = r15.dialogs_read_inbox_max     // Catch:{ all -> 0x1dbf }
            r26 = r13
            java.lang.Long r13 = java.lang.Long.valueOf(r3)     // Catch:{ all -> 0x1dbf }
            r15.put(r13, r9)     // Catch:{ all -> 0x1dbf }
            goto L_0x041d
        L_0x041b:
            r26 = r13
        L_0x041d:
            int r9 = r9.intValue()     // Catch:{ all -> 0x1dbf }
            if (r10 <= r9) goto L_0x0425
        L_0x0423:
            r9 = 1
            goto L_0x043c
        L_0x0425:
            r9 = 0
            goto L_0x043c
        L_0x0427:
            r26 = r13
            r25 = r15
            r21 = 0
            int r9 = (r7 > r21 ? 1 : (r7 == r21 ? 0 : -1))
            if (r9 == 0) goto L_0x0425
            org.telegram.messenger.MessagesStorage r9 = org.telegram.messenger.MessagesStorage.getInstance(r29)     // Catch:{ all -> 0x1dbf }
            boolean r9 = r9.checkMessageByRandomId(r7)     // Catch:{ all -> 0x1dbf }
            if (r9 != 0) goto L_0x0425
            goto L_0x0423
        L_0x043c:
            boolean r13 = r14.startsWith(r2)     // Catch:{ all -> 0x1dbf }
            java.lang.String r15 = "CHAT_REACT_"
            if (r13 != 0) goto L_0x044a
            boolean r13 = r14.startsWith(r15)     // Catch:{ all -> 0x1dbf }
            if (r13 == 0) goto L_0x044b
        L_0x044a:
            r9 = 1
        L_0x044b:
            if (r9 == 0) goto L_0x1dab
            java.lang.String r9 = "chat_from_id"
            r27 = r7
            r13 = r10
            r7 = 0
            long r9 = r11.optLong(r9, r7)     // Catch:{ all -> 0x1dbf }
            r31 = r13
            java.lang.String r13 = "chat_from_broadcast_id"
            r34 = r12
            long r12 = r11.optLong(r13, r7)     // Catch:{ all -> 0x1dbf }
            r35 = r12
            java.lang.String r12 = "chat_from_group_id"
            long r12 = r11.optLong(r12, r7)     // Catch:{ all -> 0x1dbf }
            int r21 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r21 != 0) goto L_0x0475
            int r37 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r37 == 0) goto L_0x0473
            goto L_0x0475
        L_0x0473:
            r7 = 0
            goto L_0x0476
        L_0x0475:
            r7 = 1
        L_0x0476:
            java.lang.String r8 = "mention"
            boolean r8 = r11.has(r8)     // Catch:{ all -> 0x1dbf }
            if (r8 == 0) goto L_0x0489
            java.lang.String r8 = "mention"
            int r8 = r11.getInt(r8)     // Catch:{ all -> 0x1dbf }
            if (r8 == 0) goto L_0x0489
            r37 = 1
            goto L_0x048b
        L_0x0489:
            r37 = 0
        L_0x048b:
            java.lang.String r8 = "silent"
            boolean r8 = r11.has(r8)     // Catch:{ all -> 0x1dbf }
            if (r8 == 0) goto L_0x049e
            java.lang.String r8 = "silent"
            int r8 = r11.getInt(r8)     // Catch:{ all -> 0x1dbf }
            if (r8 == 0) goto L_0x049e
            r38 = 1
            goto L_0x04a0
        L_0x049e:
            r38 = 0
        L_0x04a0:
            java.lang.String r8 = "loc_args"
            r39 = r9
            r9 = r16
            boolean r8 = r9.has(r8)     // Catch:{ all -> 0x1dbf }
            if (r8 == 0) goto L_0x04c8
            java.lang.String r8 = "loc_args"
            org.json.JSONArray r8 = r9.getJSONArray(r8)     // Catch:{ all -> 0x1dbf }
            int r9 = r8.length()     // Catch:{ all -> 0x1dbf }
            java.lang.String[] r10 = new java.lang.String[r9]     // Catch:{ all -> 0x1dbf }
            r41 = r12
            r12 = 0
        L_0x04bb:
            if (r12 >= r9) goto L_0x04c6
            java.lang.String r13 = r8.getString(r12)     // Catch:{ all -> 0x1dbf }
            r10[r12] = r13     // Catch:{ all -> 0x1dbf }
            int r12 = r12 + 1
            goto L_0x04bb
        L_0x04c6:
            r8 = 0
            goto L_0x04cc
        L_0x04c8:
            r41 = r12
            r8 = 0
            r10 = 0
        L_0x04cc:
            r9 = r10[r8]     // Catch:{ all -> 0x1dbf }
            java.lang.String r8 = "edit_date"
            boolean r8 = r11.has(r8)     // Catch:{ all -> 0x1dbf }
            java.lang.String r11 = "CHAT_"
            boolean r11 = r14.startsWith(r11)     // Catch:{ all -> 0x1dbf }
            if (r11 == 0) goto L_0x0510
            boolean r11 = org.telegram.messenger.UserObject.isReplyUser((long) r3)     // Catch:{ all -> 0x1dbf }
            if (r11 == 0) goto L_0x04fa
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x1dbf }
            r11.<init>()     // Catch:{ all -> 0x1dbf }
            r11.append(r9)     // Catch:{ all -> 0x1dbf }
            java.lang.String r9 = " @ "
            r11.append(r9)     // Catch:{ all -> 0x1dbf }
            r9 = 1
            r12 = r10[r9]     // Catch:{ all -> 0x1dbf }
            r11.append(r12)     // Catch:{ all -> 0x1dbf }
            java.lang.String r9 = r11.toString()     // Catch:{ all -> 0x1dbf }
            goto L_0x0532
        L_0x04fa:
            r11 = 0
            int r13 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r13 == 0) goto L_0x0502
            r11 = 1
            goto L_0x0503
        L_0x0502:
            r11 = 0
        L_0x0503:
            r12 = 1
            r13 = r10[r12]     // Catch:{ all -> 0x1dbf }
            r12 = 0
            r16 = 0
            r49 = r11
            r11 = r9
            r9 = r13
            r13 = r49
            goto L_0x0537
        L_0x0510:
            java.lang.String r11 = "PINNED_"
            boolean r11 = r14.startsWith(r11)     // Catch:{ all -> 0x1dbf }
            if (r11 == 0) goto L_0x0527
            r11 = 0
            int r13 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r13 == 0) goto L_0x0520
            r11 = 1
            goto L_0x0521
        L_0x0520:
            r11 = 0
        L_0x0521:
            r13 = r11
            r11 = 0
            r12 = 0
            r16 = 1
            goto L_0x0537
        L_0x0527:
            java.lang.String r11 = "CHANNEL_"
            boolean r11 = r14.startsWith(r11)     // Catch:{ all -> 0x1dbf }
            if (r11 == 0) goto L_0x0532
            r11 = 0
            r12 = 1
            goto L_0x0534
        L_0x0532:
            r11 = 0
            r12 = 0
        L_0x0534:
            r13 = 0
            r16 = 0
        L_0x0537:
            boolean r43 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1dbf }
            if (r43 == 0) goto L_0x0567
            r43 = r9
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x1dbf }
            r9.<init>()     // Catch:{ all -> 0x1dbf }
            r9.append(r1)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = " received message notification "
            r9.append(r1)     // Catch:{ all -> 0x1dbf }
            r9.append(r14)     // Catch:{ all -> 0x1dbf }
            r1 = r34
            r9.append(r1)     // Catch:{ all -> 0x1dbf }
            r9.append(r3)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = " mid = "
            r9.append(r1)     // Catch:{ all -> 0x1dbf }
            r1 = r31
            r9.append(r1)     // Catch:{ all -> 0x1dbf }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x1dbf }
            org.telegram.messenger.FileLog.d(r9)     // Catch:{ all -> 0x1dbf }
            goto L_0x056b
        L_0x0567:
            r43 = r9
            r1 = r31
        L_0x056b:
            boolean r9 = r14.startsWith(r2)     // Catch:{ all -> 0x1dbf }
            if (r9 != 0) goto L_0x1c9e
            boolean r9 = r14.startsWith(r15)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0579
            goto L_0x1c9e
        L_0x0579:
            int r9 = r14.hashCode()     // Catch:{ all -> 0x1dbf }
            switch(r9) {
                case -2100047043: goto L_0x0ad5;
                case -2091498420: goto L_0x0aca;
                case -2053872415: goto L_0x0abf;
                case -2039746363: goto L_0x0ab4;
                case -2023218804: goto L_0x0aa9;
                case -1979538588: goto L_0x0a9e;
                case -1979536003: goto L_0x0a93;
                case -1979535888: goto L_0x0a88;
                case -1969004705: goto L_0x0a7d;
                case -1946699248: goto L_0x0a72;
                case -1717283471: goto L_0x0a66;
                case -1646640058: goto L_0x0a5a;
                case -1528047021: goto L_0x0a4e;
                case -1507149394: goto L_0x0a41;
                case -1493579426: goto L_0x0a35;
                case -1482481933: goto L_0x0a29;
                case -1480102982: goto L_0x0a1c;
                case -1478041834: goto L_0x0a10;
                case -1474543101: goto L_0x0a05;
                case -1465695932: goto L_0x09f9;
                case -1374906292: goto L_0x09ed;
                case -1372940586: goto L_0x09e1;
                case -1264245338: goto L_0x09d5;
                case -1236154001: goto L_0x09c9;
                case -1236086700: goto L_0x09bd;
                case -1236077786: goto L_0x09b1;
                case -1235796237: goto L_0x09a5;
                case -1235760759: goto L_0x0999;
                case -1235686303: goto L_0x098c;
                case -1198046100: goto L_0x0981;
                case -1124254527: goto L_0x0975;
                case -1085137927: goto L_0x0969;
                case -1084856378: goto L_0x095d;
                case -1084820900: goto L_0x0951;
                case -1084746444: goto L_0x0945;
                case -819729482: goto L_0x0939;
                case -772141857: goto L_0x092d;
                case -638310039: goto L_0x0921;
                case -590403924: goto L_0x0915;
                case -589196239: goto L_0x0909;
                case -589193654: goto L_0x08fd;
                case -589193539: goto L_0x08f1;
                case -440169325: goto L_0x08e5;
                case -412748110: goto L_0x08d9;
                case -228518075: goto L_0x08cd;
                case -213586509: goto L_0x08c1;
                case -115582002: goto L_0x08b5;
                case -112621464: goto L_0x08a9;
                case -108522133: goto L_0x089d;
                case -107572034: goto L_0x088f;
                case -40534265: goto L_0x0883;
                case 52369421: goto L_0x0877;
                case 65254746: goto L_0x086b;
                case 141040782: goto L_0x085f;
                case 202550149: goto L_0x0853;
                case 309993049: goto L_0x0847;
                case 309995634: goto L_0x083b;
                case 309995749: goto L_0x082f;
                case 320532812: goto L_0x0823;
                case 328933854: goto L_0x0817;
                case 331340546: goto L_0x080b;
                case 342406591: goto L_0x07ff;
                case 344816990: goto L_0x07f3;
                case 346878138: goto L_0x07e7;
                case 350376871: goto L_0x07db;
                case 608430149: goto L_0x07cf;
                case 615714517: goto L_0x07c4;
                case 715508879: goto L_0x07b8;
                case 728985323: goto L_0x07ac;
                case 731046471: goto L_0x07a0;
                case 734545204: goto L_0x0794;
                case 802032552: goto L_0x0788;
                case 991498806: goto L_0x077c;
                case 1007364121: goto L_0x0770;
                case 1019850010: goto L_0x0764;
                case 1019917311: goto L_0x0758;
                case 1019926225: goto L_0x074c;
                case 1020207774: goto L_0x0740;
                case 1020243252: goto L_0x0734;
                case 1020317708: goto L_0x0728;
                case 1060282259: goto L_0x071c;
                case 1060349560: goto L_0x0710;
                case 1060358474: goto L_0x0704;
                case 1060640023: goto L_0x06f8;
                case 1060675501: goto L_0x06ec;
                case 1060749957: goto L_0x06df;
                case 1073049781: goto L_0x06d3;
                case 1078101399: goto L_0x06c7;
                case 1110103437: goto L_0x06bb;
                case 1160762272: goto L_0x06af;
                case 1172918249: goto L_0x06a3;
                case 1234591620: goto L_0x0697;
                case 1281128640: goto L_0x068b;
                case 1281131225: goto L_0x067f;
                case 1281131340: goto L_0x0673;
                case 1310789062: goto L_0x0666;
                case 1333118583: goto L_0x065a;
                case 1361447897: goto L_0x064e;
                case 1498266155: goto L_0x0642;
                case 1533804208: goto L_0x0636;
                case 1540131626: goto L_0x062a;
                case 1547988151: goto L_0x061e;
                case 1561464595: goto L_0x0612;
                case 1563525743: goto L_0x0606;
                case 1567024476: goto L_0x05fa;
                case 1810705077: goto L_0x05ee;
                case 1815177512: goto L_0x05e2;
                case 1954774321: goto L_0x05d6;
                case 1963241394: goto L_0x05ca;
                case 2014789757: goto L_0x05be;
                case 2022049433: goto L_0x05b2;
                case 2034984710: goto L_0x05a6;
                case 2048733346: goto L_0x059a;
                case 2099392181: goto L_0x058e;
                case 2140162142: goto L_0x0582;
                default: goto L_0x0580;
            }     // Catch:{ all -> 0x1dbf }
        L_0x0580:
            goto L_0x0ae2
        L_0x0582:
            java.lang.String r9 = "CHAT_MESSAGE_GEOLIVE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 61
            goto L_0x0adf
        L_0x058e:
            java.lang.String r9 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 44
            goto L_0x0adf
        L_0x059a:
            java.lang.String r9 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 29
            goto L_0x0adf
        L_0x05a6:
            java.lang.String r9 = "CHANNEL_MESSAGE_PLAYLIST"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 46
            goto L_0x0adf
        L_0x05b2:
            java.lang.String r9 = "PINNED_CONTACT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 95
            goto L_0x0adf
        L_0x05be:
            java.lang.String r9 = "CHAT_PHOTO_EDITED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 69
            goto L_0x0adf
        L_0x05ca:
            java.lang.String r9 = "LOCKED_MESSAGE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 109(0x6d, float:1.53E-43)
            goto L_0x0adf
        L_0x05d6:
            java.lang.String r9 = "CHAT_MESSAGE_PLAYLIST"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 84
            goto L_0x0adf
        L_0x05e2:
            java.lang.String r9 = "CHANNEL_MESSAGES"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 48
            goto L_0x0adf
        L_0x05ee:
            java.lang.String r9 = "MESSAGE_INVOICE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 22
            goto L_0x0adf
        L_0x05fa:
            java.lang.String r9 = "CHAT_MESSAGE_VIDEO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 52
            goto L_0x0adf
        L_0x0606:
            java.lang.String r9 = "CHAT_MESSAGE_ROUND"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 53
            goto L_0x0adf
        L_0x0612:
            java.lang.String r9 = "CHAT_MESSAGE_PHOTO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 51
            goto L_0x0adf
        L_0x061e:
            java.lang.String r9 = "CHAT_MESSAGE_AUDIO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 56
            goto L_0x0adf
        L_0x062a:
            java.lang.String r9 = "MESSAGE_PLAYLIST"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 26
            goto L_0x0adf
        L_0x0636:
            java.lang.String r9 = "MESSAGE_VIDEOS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 25
            goto L_0x0adf
        L_0x0642:
            java.lang.String r9 = "PHONE_CALL_MISSED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 114(0x72, float:1.6E-43)
            goto L_0x0adf
        L_0x064e:
            java.lang.String r9 = "MESSAGE_PHOTOS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 24
            goto L_0x0adf
        L_0x065a:
            java.lang.String r9 = "CHAT_MESSAGE_VIDEOS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 83
            goto L_0x0adf
        L_0x0666:
            java.lang.String r9 = "MESSAGE_NOTEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r17 = r15
            r9 = 3
            goto L_0x0ae5
        L_0x0673:
            java.lang.String r9 = "MESSAGE_GIF"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 18
            goto L_0x0adf
        L_0x067f:
            java.lang.String r9 = "MESSAGE_GEO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 16
            goto L_0x0adf
        L_0x068b:
            java.lang.String r9 = "MESSAGE_DOC"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 10
            goto L_0x0adf
        L_0x0697:
            java.lang.String r9 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 64
            goto L_0x0adf
        L_0x06a3:
            java.lang.String r9 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 40
            goto L_0x0adf
        L_0x06af:
            java.lang.String r9 = "CHAT_MESSAGE_PHOTOS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 82
            goto L_0x0adf
        L_0x06bb:
            java.lang.String r9 = "CHAT_MESSAGE_NOTEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 50
            goto L_0x0adf
        L_0x06c7:
            java.lang.String r9 = "CHAT_TITLE_EDITED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 68
            goto L_0x0adf
        L_0x06d3:
            java.lang.String r9 = "PINNED_NOTEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 88
            goto L_0x0adf
        L_0x06df:
            java.lang.String r9 = "MESSAGE_TEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r17 = r15
            r9 = 1
            goto L_0x0ae5
        L_0x06ec:
            java.lang.String r9 = "MESSAGE_QUIZ"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 14
            goto L_0x0adf
        L_0x06f8:
            java.lang.String r9 = "MESSAGE_POLL"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 15
            goto L_0x0adf
        L_0x0704:
            java.lang.String r9 = "MESSAGE_GAME"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 19
            goto L_0x0adf
        L_0x0710:
            java.lang.String r9 = "MESSAGE_FWDS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 23
            goto L_0x0adf
        L_0x071c:
            java.lang.String r9 = "MESSAGE_DOCS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 27
            goto L_0x0adf
        L_0x0728:
            java.lang.String r9 = "CHAT_MESSAGE_TEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 49
            goto L_0x0adf
        L_0x0734:
            java.lang.String r9 = "CHAT_MESSAGE_QUIZ"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 58
            goto L_0x0adf
        L_0x0740:
            java.lang.String r9 = "CHAT_MESSAGE_POLL"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 59
            goto L_0x0adf
        L_0x074c:
            java.lang.String r9 = "CHAT_MESSAGE_GAME"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 63
            goto L_0x0adf
        L_0x0758:
            java.lang.String r9 = "CHAT_MESSAGE_FWDS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 81
            goto L_0x0adf
        L_0x0764:
            java.lang.String r9 = "CHAT_MESSAGE_DOCS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 85
            goto L_0x0adf
        L_0x0770:
            java.lang.String r9 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 21
            goto L_0x0adf
        L_0x077c:
            java.lang.String r9 = "PINNED_GEOLIVE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 99
            goto L_0x0adf
        L_0x0788:
            java.lang.String r9 = "MESSAGE_CONTACT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 13
            goto L_0x0adf
        L_0x0794:
            java.lang.String r9 = "PINNED_VIDEO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 90
            goto L_0x0adf
        L_0x07a0:
            java.lang.String r9 = "PINNED_ROUND"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 91
            goto L_0x0adf
        L_0x07ac:
            java.lang.String r9 = "PINNED_PHOTO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 89
            goto L_0x0adf
        L_0x07b8:
            java.lang.String r9 = "PINNED_AUDIO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 94
            goto L_0x0adf
        L_0x07c4:
            java.lang.String r9 = "MESSAGE_PHOTO_SECRET"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 5
            goto L_0x0adf
        L_0x07cf:
            java.lang.String r9 = "CHAT_VOICECHAT_INVITE_YOU"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 74
            goto L_0x0adf
        L_0x07db:
            java.lang.String r9 = "CHANNEL_MESSAGE_VIDEO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 31
            goto L_0x0adf
        L_0x07e7:
            java.lang.String r9 = "CHANNEL_MESSAGE_ROUND"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 32
            goto L_0x0adf
        L_0x07f3:
            java.lang.String r9 = "CHANNEL_MESSAGE_PHOTO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 30
            goto L_0x0adf
        L_0x07ff:
            java.lang.String r9 = "CHAT_VOICECHAT_END"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 73
            goto L_0x0adf
        L_0x080b:
            java.lang.String r9 = "CHANNEL_MESSAGE_AUDIO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 35
            goto L_0x0adf
        L_0x0817:
            java.lang.String r9 = "CHAT_MESSAGE_STICKER"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 55
            goto L_0x0adf
        L_0x0823:
            java.lang.String r9 = "MESSAGES"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 28
            goto L_0x0adf
        L_0x082f:
            java.lang.String r9 = "CHAT_MESSAGE_GIF"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 62
            goto L_0x0adf
        L_0x083b:
            java.lang.String r9 = "CHAT_MESSAGE_GEO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 60
            goto L_0x0adf
        L_0x0847:
            java.lang.String r9 = "CHAT_MESSAGE_DOC"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 54
            goto L_0x0adf
        L_0x0853:
            java.lang.String r9 = "CHAT_VOICECHAT_INVITE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 72
            goto L_0x0adf
        L_0x085f:
            java.lang.String r9 = "CHAT_LEFT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 77
            goto L_0x0adf
        L_0x086b:
            java.lang.String r9 = "CHAT_ADD_YOU"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 67
            goto L_0x0adf
        L_0x0877:
            java.lang.String r9 = "REACT_TEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 105(0x69, float:1.47E-43)
            goto L_0x0adf
        L_0x0883:
            java.lang.String r9 = "CHAT_DELETE_MEMBER"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 75
            goto L_0x0adf
        L_0x088f:
            java.lang.String r9 = "MESSAGE_SCREENSHOT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r17 = r15
            r9 = 8
            goto L_0x0ae5
        L_0x089d:
            java.lang.String r9 = "AUTH_REGION"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 108(0x6c, float:1.51E-43)
            goto L_0x0adf
        L_0x08a9:
            java.lang.String r9 = "CONTACT_JOINED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 106(0x6a, float:1.49E-43)
            goto L_0x0adf
        L_0x08b5:
            java.lang.String r9 = "CHAT_MESSAGE_INVOICE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 65
            goto L_0x0adf
        L_0x08c1:
            java.lang.String r9 = "ENCRYPTION_REQUEST"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 110(0x6e, float:1.54E-43)
            goto L_0x0adf
        L_0x08cd:
            java.lang.String r9 = "MESSAGE_GEOLIVE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 17
            goto L_0x0adf
        L_0x08d9:
            java.lang.String r9 = "CHAT_DELETE_YOU"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 76
            goto L_0x0adf
        L_0x08e5:
            java.lang.String r9 = "AUTH_UNKNOWN"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 107(0x6b, float:1.5E-43)
            goto L_0x0adf
        L_0x08f1:
            java.lang.String r9 = "PINNED_GIF"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 103(0x67, float:1.44E-43)
            goto L_0x0adf
        L_0x08fd:
            java.lang.String r9 = "PINNED_GEO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 98
            goto L_0x0adf
        L_0x0909:
            java.lang.String r9 = "PINNED_DOC"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 92
            goto L_0x0adf
        L_0x0915:
            java.lang.String r9 = "PINNED_GAME_SCORE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 101(0x65, float:1.42E-43)
            goto L_0x0adf
        L_0x0921:
            java.lang.String r9 = "CHANNEL_MESSAGE_STICKER"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 34
            goto L_0x0adf
        L_0x092d:
            java.lang.String r9 = "PHONE_CALL_REQUEST"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 112(0x70, float:1.57E-43)
            goto L_0x0adf
        L_0x0939:
            java.lang.String r9 = "PINNED_STICKER"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 93
            goto L_0x0adf
        L_0x0945:
            java.lang.String r9 = "PINNED_TEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 87
            goto L_0x0adf
        L_0x0951:
            java.lang.String r9 = "PINNED_QUIZ"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 96
            goto L_0x0adf
        L_0x095d:
            java.lang.String r9 = "PINNED_POLL"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 97
            goto L_0x0adf
        L_0x0969:
            java.lang.String r9 = "PINNED_GAME"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 100
            goto L_0x0adf
        L_0x0975:
            java.lang.String r9 = "CHAT_MESSAGE_CONTACT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 57
            goto L_0x0adf
        L_0x0981:
            java.lang.String r9 = "MESSAGE_VIDEO_SECRET"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 7
            goto L_0x0adf
        L_0x098c:
            java.lang.String r9 = "CHANNEL_MESSAGE_TEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r17 = r15
            r9 = 2
            goto L_0x0ae5
        L_0x0999:
            java.lang.String r9 = "CHANNEL_MESSAGE_QUIZ"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 37
            goto L_0x0adf
        L_0x09a5:
            java.lang.String r9 = "CHANNEL_MESSAGE_POLL"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 38
            goto L_0x0adf
        L_0x09b1:
            java.lang.String r9 = "CHANNEL_MESSAGE_GAME"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 42
            goto L_0x0adf
        L_0x09bd:
            java.lang.String r9 = "CHANNEL_MESSAGE_FWDS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 43
            goto L_0x0adf
        L_0x09c9:
            java.lang.String r9 = "CHANNEL_MESSAGE_DOCS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 47
            goto L_0x0adf
        L_0x09d5:
            java.lang.String r9 = "PINNED_INVOICE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 102(0x66, float:1.43E-43)
            goto L_0x0adf
        L_0x09e1:
            java.lang.String r9 = "CHAT_RETURNED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 78
            goto L_0x0adf
        L_0x09ed:
            java.lang.String r9 = "ENCRYPTED_MESSAGE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 104(0x68, float:1.46E-43)
            goto L_0x0adf
        L_0x09f9:
            java.lang.String r9 = "ENCRYPTION_ACCEPT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 111(0x6f, float:1.56E-43)
            goto L_0x0adf
        L_0x0a05:
            java.lang.String r9 = "MESSAGE_VIDEO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 6
            goto L_0x0adf
        L_0x0a10:
            java.lang.String r9 = "MESSAGE_ROUND"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 9
            goto L_0x0adf
        L_0x0a1c:
            java.lang.String r9 = "MESSAGE_PHOTO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r17 = r15
            r9 = 4
            goto L_0x0ae5
        L_0x0a29:
            java.lang.String r9 = "MESSAGE_MUTED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 113(0x71, float:1.58E-43)
            goto L_0x0adf
        L_0x0a35:
            java.lang.String r9 = "MESSAGE_AUDIO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 12
            goto L_0x0adf
        L_0x0a41:
            java.lang.String r9 = "MESSAGE_RECURRING_PAY"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r17 = r15
            r9 = 0
            goto L_0x0ae5
        L_0x0a4e:
            java.lang.String r9 = "CHAT_MESSAGES"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 86
            goto L_0x0adf
        L_0x0a5a:
            java.lang.String r9 = "CHAT_VOICECHAT_START"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 71
            goto L_0x0adf
        L_0x0a66:
            java.lang.String r9 = "CHAT_REQ_JOINED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 80
            goto L_0x0adf
        L_0x0a72:
            java.lang.String r9 = "CHAT_JOINED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 79
            goto L_0x0adf
        L_0x0a7d:
            java.lang.String r9 = "CHAT_ADD_MEMBER"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 70
            goto L_0x0adf
        L_0x0a88:
            java.lang.String r9 = "CHANNEL_MESSAGE_GIF"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 41
            goto L_0x0adf
        L_0x0a93:
            java.lang.String r9 = "CHANNEL_MESSAGE_GEO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 39
            goto L_0x0adf
        L_0x0a9e:
            java.lang.String r9 = "CHANNEL_MESSAGE_DOC"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 33
            goto L_0x0adf
        L_0x0aa9:
            java.lang.String r9 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 45
            goto L_0x0adf
        L_0x0ab4:
            java.lang.String r9 = "MESSAGE_STICKER"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 11
            goto L_0x0adf
        L_0x0abf:
            java.lang.String r9 = "CHAT_CREATED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 66
            goto L_0x0adf
        L_0x0aca:
            java.lang.String r9 = "CHANNEL_MESSAGE_CONTACT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 36
            goto L_0x0adf
        L_0x0ad5:
            java.lang.String r9 = "MESSAGE_GAME_SCORE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1dbf }
            if (r9 == 0) goto L_0x0ae2
            r9 = 20
        L_0x0adf:
            r17 = r15
            goto L_0x0ae5
        L_0x0ae2:
            r17 = r15
            r9 = -1
        L_0x0ae5:
            java.lang.String r15 = "Files"
            r31 = r2
            java.lang.String r2 = "MusicFiles"
            r34 = r8
            java.lang.String r8 = "Videos"
            r44 = r12
            java.lang.String r12 = "Photos"
            r45 = r11
            java.lang.String r11 = " "
            r46 = r5
            java.lang.String r5 = "NotificationGroupFew"
            java.lang.String r6 = "NotificationMessageFew"
            r48 = r13
            java.lang.String r13 = "ChannelMessageFew"
            r51 = r1
            java.lang.String r1 = "AttachSticker"
            switch(r9) {
                case 0: goto L_0x1CLASSNAME;
                case 1: goto L_0x1CLASSNAME;
                case 2: goto L_0x1CLASSNAME;
                case 3: goto L_0x1CLASSNAME;
                case 4: goto L_0x1c0c;
                case 5: goto L_0x1bef;
                case 6: goto L_0x1bd1;
                case 7: goto L_0x1bb3;
                case 8: goto L_0x1b9c;
                case 9: goto L_0x1b7e;
                case 10: goto L_0x1b60;
                case 11: goto L_0x1b05;
                case 12: goto L_0x1ae7;
                case 13: goto L_0x1ac4;
                case 14: goto L_0x1aa1;
                case 15: goto L_0x1a7e;
                case 16: goto L_0x1a60;
                case 17: goto L_0x1a42;
                case 18: goto L_0x1a24;
                case 19: goto L_0x1a01;
                case 20: goto L_0x19e1;
                case 21: goto L_0x19e1;
                case 22: goto L_0x19be;
                case 23: goto L_0x1993;
                case 24: goto L_0x196c;
                case 25: goto L_0x1946;
                case 26: goto L_0x1920;
                case 27: goto L_0x18fa;
                case 28: goto L_0x18df;
                case 29: goto L_0x18bd;
                case 30: goto L_0x18a0;
                case 31: goto L_0x1883;
                case 32: goto L_0x1866;
                case 33: goto L_0x1848;
                case 34: goto L_0x17ef;
                case 35: goto L_0x17d1;
                case 36: goto L_0x17ae;
                case 37: goto L_0x178b;
                case 38: goto L_0x1768;
                case 39: goto L_0x174a;
                case 40: goto L_0x172c;
                case 41: goto L_0x170e;
                case 42: goto L_0x16f0;
                case 43: goto L_0x16c2;
                case 44: goto L_0x169b;
                case 45: goto L_0x1674;
                case 46: goto L_0x164d;
                case 47: goto L_0x1626;
                case 48: goto L_0x1610;
                case 49: goto L_0x15ee;
                case 50: goto L_0x15cb;
                case 51: goto L_0x15a8;
                case 52: goto L_0x1585;
                case 53: goto L_0x1562;
                case 54: goto L_0x153f;
                case 55: goto L_0x14c8;
                case 56: goto L_0x14a5;
                case 57: goto L_0x147d;
                case 58: goto L_0x1455;
                case 59: goto L_0x142d;
                case 60: goto L_0x140a;
                case 61: goto L_0x13e7;
                case 62: goto L_0x13c4;
                case 63: goto L_0x139c;
                case 64: goto L_0x1378;
                case 65: goto L_0x1350;
                case 66: goto L_0x1334;
                case 67: goto L_0x1334;
                case 68: goto L_0x131a;
                case 69: goto L_0x1300;
                case 70: goto L_0x12e1;
                case 71: goto L_0x12c7;
                case 72: goto L_0x12a7;
                case 73: goto L_0x128c;
                case 74: goto L_0x1271;
                case 75: goto L_0x1256;
                case 76: goto L_0x123b;
                case 77: goto L_0x1220;
                case 78: goto L_0x1205;
                case 79: goto L_0x11ea;
                case 80: goto L_0x11cf;
                case 81: goto L_0x119e;
                case 82: goto L_0x1171;
                case 83: goto L_0x1144;
                case 84: goto L_0x1117;
                case 85: goto L_0x10ea;
                case 86: goto L_0x10cf;
                case 87: goto L_0x1079;
                case 88: goto L_0x102d;
                case 89: goto L_0x0fe1;
                case 90: goto L_0x0var_;
                case 91: goto L_0x0var_;
                case 92: goto L_0x0efd;
                case 93: goto L_0x0e48;
                case 94: goto L_0x0dfc;
                case 95: goto L_0x0da6;
                case 96: goto L_0x0d50;
                case 97: goto L_0x0cfa;
                case 98: goto L_0x0cae;
                case 99: goto L_0x0CLASSNAME;
                case 100: goto L_0x0CLASSNAME;
                case 101: goto L_0x0bca;
                case 102: goto L_0x0b7e;
                case 103: goto L_0x0b2e;
                case 104: goto L_0x0b16;
                case 105: goto L_0x0b10;
                case 106: goto L_0x0b10;
                case 107: goto L_0x0b10;
                case 108: goto L_0x0b10;
                case 109: goto L_0x0b10;
                case 110: goto L_0x0b10;
                case 111: goto L_0x0b10;
                case 112: goto L_0x0b10;
                case 113: goto L_0x0b10;
                case 114: goto L_0x0b10;
                default: goto L_0x0b08;
            }
        L_0x0b08:
            r5 = r17
            r9 = r25
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x0b10:
            r5 = r17
            r9 = r25
            goto L_0x1c9c
        L_0x0b16:
            java.lang.String r1 = "YouHaveNewMessage"
            int r2 = org.telegram.messenger.R.string.YouHaveNewMessage     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "SecretChatName"
            int r5 = org.telegram.messenger.R.string.SecretChatName     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r5)     // Catch:{ all -> 0x1dbf }
            r43 = r2
            r5 = r17
            r9 = r25
            goto L_0x18f4
        L_0x0b2e:
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0b4f
            java.lang.String r1 = "NotificationActionPinnedGifUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGifUser     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r6 = 0
            r7 = r10[r6]     // Catch:{ all -> 0x1dbf }
            r5[r6] = r7     // Catch:{ all -> 0x1dbf }
            r6 = 1
            r7 = r10[r6]     // Catch:{ all -> 0x1dbf }
            r5[r6] = r7     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            r5 = r17
            r9 = r25
            goto L_0x1cb3
        L_0x0b4f:
            r6 = r17
            r9 = r25
            if (r7 == 0) goto L_0x0b6c
            java.lang.String r1 = "NotificationActionPinnedGif"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGif     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0b6c:
            java.lang.String r1 = "NotificationActionPinnedGifChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGifChannel     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0b7e:
            r6 = r17
            r9 = r25
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0b9f
            java.lang.String r1 = "NotificationActionPinnedInvoiceUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedInvoiceUser     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0b9f:
            if (r7 == 0) goto L_0x0bb8
            java.lang.String r1 = "NotificationActionPinnedInvoice"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedInvoice     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0bb8:
            java.lang.String r1 = "NotificationActionPinnedInvoiceChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedInvoiceChannel     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0bca:
            r6 = r17
            r9 = r25
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0beb
            java.lang.String r1 = "NotificationActionPinnedGameScoreUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGameScoreUser     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0beb:
            if (r7 == 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGameScore"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGameScore     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0CLASSNAME:
            java.lang.String r1 = "NotificationActionPinnedGameScoreChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGameScoreChannel     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0CLASSNAME:
            r6 = r17
            r9 = r25
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGameUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGameUser     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0CLASSNAME:
            if (r7 == 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGame"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGame     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0CLASSNAME:
            java.lang.String r1 = "NotificationActionPinnedGameChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGameChannel     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0CLASSNAME:
            r6 = r17
            r9 = r25
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGeoLiveUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveUser     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0CLASSNAME:
            if (r7 == 0) goto L_0x0c9c
            java.lang.String r1 = "NotificationActionPinnedGeoLive"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGeoLive     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0c9c:
            java.lang.String r1 = "NotificationActionPinnedGeoLiveChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveChannel     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0cae:
            r6 = r17
            r9 = r25
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0ccf
            java.lang.String r1 = "NotificationActionPinnedGeoUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGeoUser     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0ccf:
            if (r7 == 0) goto L_0x0ce8
            java.lang.String r1 = "NotificationActionPinnedGeo"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGeo     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0ce8:
            java.lang.String r1 = "NotificationActionPinnedGeoChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGeoChannel     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0cfa:
            r6 = r17
            r9 = r25
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0d1b
            java.lang.String r1 = "NotificationActionPinnedPollUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedPollUser     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0d1b:
            if (r7 == 0) goto L_0x0d39
            java.lang.String r1 = "NotificationActionPinnedPoll2"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedPoll2     // Catch:{ all -> 0x1dbf }
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r11 = 1
            r5[r11] = r8     // Catch:{ all -> 0x1dbf }
            r8 = r10[r11]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0d39:
            java.lang.String r1 = "NotificationActionPinnedPollChannel2"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedPollChannel2     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0d50:
            r6 = r17
            r9 = r25
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0d71
            java.lang.String r1 = "NotificationActionPinnedQuizUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedQuizUser     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0d71:
            if (r7 == 0) goto L_0x0d8f
            java.lang.String r1 = "NotificationActionPinnedQuiz2"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedQuiz2     // Catch:{ all -> 0x1dbf }
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r11 = 1
            r5[r11] = r8     // Catch:{ all -> 0x1dbf }
            r8 = r10[r11]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0d8f:
            java.lang.String r1 = "NotificationActionPinnedQuizChannel2"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedQuizChannel2     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0da6:
            r6 = r17
            r9 = r25
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0dc7
            java.lang.String r1 = "NotificationActionPinnedContactUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedContactUser     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0dc7:
            if (r7 == 0) goto L_0x0de5
            java.lang.String r1 = "NotificationActionPinnedContact2"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedContact2     // Catch:{ all -> 0x1dbf }
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r11 = 1
            r5[r11] = r8     // Catch:{ all -> 0x1dbf }
            r8 = r10[r11]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0de5:
            java.lang.String r1 = "NotificationActionPinnedContactChannel2"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedContactChannel2     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0dfc:
            r6 = r17
            r9 = r25
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0e1d
            java.lang.String r1 = "NotificationActionPinnedVoiceUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedVoiceUser     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0e1d:
            if (r7 == 0) goto L_0x0e36
            java.lang.String r1 = "NotificationActionPinnedVoice"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedVoice     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0e36:
            java.lang.String r1 = "NotificationActionPinnedVoiceChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedVoiceChannel     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0e48:
            r6 = r17
            r9 = r25
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0e87
            int r1 = r10.length     // Catch:{ all -> 0x1dbf }
            r2 = 1
            if (r1 <= r2) goto L_0x0e75
            r1 = r10[r2]     // Catch:{ all -> 0x1dbf }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x1dbf }
            if (r1 != 0) goto L_0x0e75
            java.lang.String r1 = "NotificationActionPinnedStickerEmojiUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiUser     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0e75:
            java.lang.String r1 = "NotificationActionPinnedStickerUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedStickerUser     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0e87:
            if (r7 == 0) goto L_0x0ec8
            int r1 = r10.length     // Catch:{ all -> 0x1dbf }
            r2 = 2
            if (r1 <= r2) goto L_0x0eb1
            r1 = r10[r2]     // Catch:{ all -> 0x1dbf }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x1dbf }
            if (r1 != 0) goto L_0x0eb1
            java.lang.String r1 = "NotificationActionPinnedStickerEmoji"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedStickerEmoji     // Catch:{ all -> 0x1dbf }
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r11 = 1
            r5[r11] = r8     // Catch:{ all -> 0x1dbf }
            r8 = r10[r11]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0eb1:
            java.lang.String r1 = "NotificationActionPinnedSticker"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedSticker     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0ec8:
            int r1 = r10.length     // Catch:{ all -> 0x1dbf }
            r2 = 1
            if (r1 <= r2) goto L_0x0eeb
            r1 = r10[r2]     // Catch:{ all -> 0x1dbf }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x1dbf }
            if (r1 != 0) goto L_0x0eeb
            java.lang.String r1 = "NotificationActionPinnedStickerEmojiChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiChannel     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0eeb:
            java.lang.String r1 = "NotificationActionPinnedStickerChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedStickerChannel     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0efd:
            r6 = r17
            r9 = r25
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0f1e
            java.lang.String r1 = "NotificationActionPinnedFileUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedFileUser     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0f1e:
            if (r7 == 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedFile"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedFile     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0var_:
            java.lang.String r1 = "NotificationActionPinnedFileChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedFileChannel     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0var_:
            r6 = r17
            r9 = r25
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0f6a
            java.lang.String r1 = "NotificationActionPinnedRoundUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedRoundUser     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0f6a:
            if (r7 == 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedRound"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedRound     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0var_:
            java.lang.String r1 = "NotificationActionPinnedRoundChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedRoundChannel     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0var_:
            r6 = r17
            r9 = r25
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0fb6
            java.lang.String r1 = "NotificationActionPinnedVideoUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedVideoUser     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0fb6:
            if (r7 == 0) goto L_0x0fcf
            java.lang.String r1 = "NotificationActionPinnedVideo"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedVideo     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0fcf:
            java.lang.String r1 = "NotificationActionPinnedVideoChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedVideoChannel     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x0fe1:
            r6 = r17
            r9 = r25
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x1002
            java.lang.String r1 = "NotificationActionPinnedPhotoUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedPhotoUser     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x1002:
            if (r7 == 0) goto L_0x101b
            java.lang.String r1 = "NotificationActionPinnedPhoto"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedPhoto     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x101b:
            java.lang.String r1 = "NotificationActionPinnedPhotoChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedPhotoChannel     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x102d:
            r6 = r17
            r9 = r25
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x104e
            java.lang.String r1 = "NotificationActionPinnedNoTextUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedNoTextUser     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x104e:
            if (r7 == 0) goto L_0x1067
            java.lang.String r1 = "NotificationActionPinnedNoText"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedNoText     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x1067:
            java.lang.String r1 = "NotificationActionPinnedNoTextChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedNoTextChannel     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x1079:
            r6 = r17
            r9 = r25
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x109a
            java.lang.String r1 = "NotificationActionPinnedTextUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedTextUser     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x109a:
            if (r7 == 0) goto L_0x10b8
            java.lang.String r1 = "NotificationActionPinnedText"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedText     // Catch:{ all -> 0x1dbf }
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x10b8:
            java.lang.String r1 = "NotificationActionPinnedTextChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedTextChannel     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x10cf:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationGroupAlbum"
            int r2 = org.telegram.messenger.R.string.NotificationGroupAlbum     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x18f3
        L_0x10ea:
            r6 = r17
            r9 = r25
            int r2 = org.telegram.messenger.R.string.NotificationGroupFew     // Catch:{ all -> 0x1dbf }
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1dbf }
            r8 = 0
            r11 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r7[r8] = r11     // Catch:{ all -> 0x1dbf }
            r8 = 1
            r11 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r7[r8] = r11     // Catch:{ all -> 0x1dbf }
            r1 = 2
            r8 = r10[r1]     // Catch:{ all -> 0x1dbf }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)     // Catch:{ all -> 0x1dbf }
            int r8 = r8.intValue()     // Catch:{ all -> 0x1dbf }
            r10 = 0
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ all -> 0x1dbf }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r15, r8, r11)     // Catch:{ all -> 0x1dbf }
            r7[r1] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r2, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x18f3
        L_0x1117:
            r6 = r17
            r9 = r25
            int r1 = org.telegram.messenger.R.string.NotificationGroupFew     // Catch:{ all -> 0x1dbf }
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1dbf }
            r8 = 0
            r11 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r7[r8] = r11     // Catch:{ all -> 0x1dbf }
            r8 = 1
            r11 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r7[r8] = r11     // Catch:{ all -> 0x1dbf }
            r8 = 2
            r10 = r10[r8]     // Catch:{ all -> 0x1dbf }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1dbf }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1dbf }
            r11 = 0
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r10, r12)     // Catch:{ all -> 0x1dbf }
            r7[r8] = r2     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r1, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x18f3
        L_0x1144:
            r6 = r17
            r9 = r25
            int r1 = org.telegram.messenger.R.string.NotificationGroupFew     // Catch:{ all -> 0x1dbf }
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r11 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r2[r7] = r11     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r11 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r2[r7] = r11     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r10 = r10[r7]     // Catch:{ all -> 0x1dbf }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1dbf }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1dbf }
            r11 = 0
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1dbf }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r10, r12)     // Catch:{ all -> 0x1dbf }
            r2[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18f3
        L_0x1171:
            r6 = r17
            r9 = r25
            int r1 = org.telegram.messenger.R.string.NotificationGroupFew     // Catch:{ all -> 0x1dbf }
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r2[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r2[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)     // Catch:{ all -> 0x1dbf }
            int r8 = r8.intValue()     // Catch:{ all -> 0x1dbf }
            r10 = 0
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ all -> 0x1dbf }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r12, r8, r11)     // Catch:{ all -> 0x1dbf }
            r2[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18f3
        L_0x119e:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationGroupForwardedFew"
            int r2 = org.telegram.messenger.R.string.NotificationGroupForwardedFew     // Catch:{ all -> 0x1dbf }
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)     // Catch:{ all -> 0x1dbf }
            int r8 = r8.intValue()     // Catch:{ all -> 0x1dbf }
            r10 = 0
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ all -> 0x1dbf }
            r12 = r26
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r12, r8, r11)     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x18f3
        L_0x11cf:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "UserAcceptedToGroupPushWithGroup"
            int r2 = org.telegram.messenger.R.string.UserAcceptedToGroupPushWithGroup     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x11ea:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationGroupAddSelfMega"
            int r2 = org.telegram.messenger.R.string.NotificationGroupAddSelfMega     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x1205:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationGroupAddSelf"
            int r2 = org.telegram.messenger.R.string.NotificationGroupAddSelf     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x1220:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationGroupLeftMember"
            int r2 = org.telegram.messenger.R.string.NotificationGroupLeftMember     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x123b:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationGroupKickYou"
            int r2 = org.telegram.messenger.R.string.NotificationGroupKickYou     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x1256:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationGroupKickMember"
            int r2 = org.telegram.messenger.R.string.NotificationGroupKickMember     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x1271:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationGroupInvitedYouToCall"
            int r2 = org.telegram.messenger.R.string.NotificationGroupInvitedYouToCall     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x128c:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationGroupEndedCall"
            int r2 = org.telegram.messenger.R.string.NotificationGroupEndedCall     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x12a7:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationGroupInvitedToCall"
            int r2 = org.telegram.messenger.R.string.NotificationGroupInvitedToCall     // Catch:{ all -> 0x1dbf }
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x12c7:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationGroupCreatedCall"
            int r2 = org.telegram.messenger.R.string.NotificationGroupCreatedCall     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x12e1:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationGroupAddMember"
            int r2 = org.telegram.messenger.R.string.NotificationGroupAddMember     // Catch:{ all -> 0x1dbf }
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x1300:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationEditedGroupPhoto"
            int r2 = org.telegram.messenger.R.string.NotificationEditedGroupPhoto     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x131a:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationEditedGroupName"
            int r2 = org.telegram.messenger.R.string.NotificationEditedGroupName     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x1334:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationInvitedToGroup"
            int r2 = org.telegram.messenger.R.string.NotificationInvitedToGroup     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
        L_0x134d:
            r5 = r6
            goto L_0x1cb3
        L_0x1350:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGroupInvoice"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGroupInvoice     // Catch:{ all -> 0x1dbf }
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "PaymentInvoice"
            int r2 = org.telegram.messenger.R.string.PaymentInvoice     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x1378:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGroupGameScored"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGroupGameScored     // Catch:{ all -> 0x1dbf }
            r5 = 4
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 3
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x134d
        L_0x139c:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGroupGame"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGroupGame     // Catch:{ all -> 0x1dbf }
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachGame"
            int r2 = org.telegram.messenger.R.string.AttachGame     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x13c4:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGroupGif"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGroupGif     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachGif"
            int r2 = org.telegram.messenger.R.string.AttachGif     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x13e7:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGroupLiveLocation"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGroupLiveLocation     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachLiveLocation"
            int r2 = org.telegram.messenger.R.string.AttachLiveLocation     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x140a:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGroupMap"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGroupMap     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachLocation"
            int r2 = org.telegram.messenger.R.string.AttachLocation     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x142d:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGroupPoll2"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGroupPoll2     // Catch:{ all -> 0x1dbf }
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "Poll"
            int r2 = org.telegram.messenger.R.string.Poll     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x1455:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGroupQuiz2"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGroupQuiz2     // Catch:{ all -> 0x1dbf }
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "PollQuiz"
            int r2 = org.telegram.messenger.R.string.PollQuiz     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x147d:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGroupContact2"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGroupContact2     // Catch:{ all -> 0x1dbf }
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachContact"
            int r2 = org.telegram.messenger.R.string.AttachContact     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x14a5:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGroupAudio"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGroupAudio     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachAudio"
            int r2 = org.telegram.messenger.R.string.AttachAudio     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x14c8:
            r6 = r17
            r9 = r25
            int r2 = r10.length     // Catch:{ all -> 0x1dbf }
            r5 = 2
            if (r2 <= r5) goto L_0x150e
            r2 = r10[r5]     // Catch:{ all -> 0x1dbf }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x1dbf }
            if (r2 != 0) goto L_0x150e
            java.lang.String r2 = "NotificationMessageGroupStickerEmoji"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupStickerEmoji     // Catch:{ all -> 0x1dbf }
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1dbf }
            r8 = 0
            r12 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r7[r8] = r12     // Catch:{ all -> 0x1dbf }
            r8 = 1
            r12 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r7[r8] = r12     // Catch:{ all -> 0x1dbf }
            r8 = 2
            r12 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r7[r8] = r12     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x1dbf }
            r2.<init>()     // Catch:{ all -> 0x1dbf }
            r7 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r2.append(r7)     // Catch:{ all -> 0x1dbf }
            r2.append(r11)     // Catch:{ all -> 0x1dbf }
            int r7 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r7)     // Catch:{ all -> 0x1dbf }
            r2.append(r1)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x150e:
            java.lang.String r2 = "NotificationMessageGroupSticker"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupSticker     // Catch:{ all -> 0x1dbf }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1dbf }
            r8 = 0
            r12 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r7[r8] = r12     // Catch:{ all -> 0x1dbf }
            r8 = 1
            r12 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r7[r8] = r12     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x1dbf }
            r2.<init>()     // Catch:{ all -> 0x1dbf }
            r7 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r2.append(r7)     // Catch:{ all -> 0x1dbf }
            r2.append(r11)     // Catch:{ all -> 0x1dbf }
            int r7 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r7)     // Catch:{ all -> 0x1dbf }
            r2.append(r1)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x153f:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGroupDocument"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGroupDocument     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachDocument"
            int r2 = org.telegram.messenger.R.string.AttachDocument     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x1562:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGroupRound"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGroupRound     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachRound"
            int r2 = org.telegram.messenger.R.string.AttachRound     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x1585:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGroupVideo"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGroupVideo     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachVideo"
            int r2 = org.telegram.messenger.R.string.AttachVideo     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x15a8:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGroupPhoto"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGroupPhoto     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachPhoto"
            int r2 = org.telegram.messenger.R.string.AttachPhoto     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x15cb:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGroupNoText"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGroupNoText     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "Message"
            int r2 = org.telegram.messenger.R.string.Message     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x15ee:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGroupText"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGroupText     // Catch:{ all -> 0x1dbf }
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            r1 = r10[r7]     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x1610:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "ChannelMessageAlbum"
            int r2 = org.telegram.messenger.R.string.ChannelMessageAlbum     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x18f3
        L_0x1626:
            r6 = r17
            r9 = r25
            int r2 = org.telegram.messenger.R.string.ChannelMessageFew     // Catch:{ all -> 0x1dbf }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r7 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r1[r5] = r7     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)     // Catch:{ all -> 0x1dbf }
            int r8 = r8.intValue()     // Catch:{ all -> 0x1dbf }
            java.lang.Object[] r10 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r15, r8, r10)     // Catch:{ all -> 0x1dbf }
            r1[r7] = r5     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r13, r2, r1)     // Catch:{ all -> 0x1dbf }
            goto L_0x18f3
        L_0x164d:
            r6 = r17
            r9 = r25
            int r1 = org.telegram.messenger.R.string.ChannelMessageFew     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r8 = 1
            r10 = r10[r8]     // Catch:{ all -> 0x1dbf }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1dbf }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1dbf }
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r10, r11)     // Catch:{ all -> 0x1dbf }
            r5[r8] = r2     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r13, r1, r5)     // Catch:{ all -> 0x1dbf }
            goto L_0x18f3
        L_0x1674:
            r6 = r17
            r9 = r25
            int r1 = org.telegram.messenger.R.string.ChannelMessageFew     // Catch:{ all -> 0x1dbf }
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r7 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r2[r5] = r7     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r10 = r10[r7]     // Catch:{ all -> 0x1dbf }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1dbf }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1dbf }
            java.lang.Object[] r11 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r8, r10, r11)     // Catch:{ all -> 0x1dbf }
            r2[r7] = r5     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r13, r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18f3
        L_0x169b:
            r6 = r17
            r9 = r25
            int r1 = org.telegram.messenger.R.string.ChannelMessageFew     // Catch:{ all -> 0x1dbf }
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r7 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r2[r5] = r7     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)     // Catch:{ all -> 0x1dbf }
            int r8 = r8.intValue()     // Catch:{ all -> 0x1dbf }
            java.lang.Object[] r10 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r12, r8, r10)     // Catch:{ all -> 0x1dbf }
            r2[r7] = r5     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r13, r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18f3
        L_0x16c2:
            r6 = r17
            r9 = r25
            int r1 = org.telegram.messenger.R.string.ChannelMessageFew     // Catch:{ all -> 0x1dbf }
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r7 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r2[r5] = r7     // Catch:{ all -> 0x1dbf }
            java.lang.String r7 = "ForwardedMessageCount"
            r8 = 1
            r10 = r10[r8]     // Catch:{ all -> 0x1dbf }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1dbf }
            int r8 = r8.intValue()     // Catch:{ all -> 0x1dbf }
            java.lang.Object[] r10 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r7, r8, r10)     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = r5.toLowerCase()     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r2[r7] = r5     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r13, r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18f3
        L_0x16f0:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGame"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGame     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachGame"
            int r2 = org.telegram.messenger.R.string.AttachGame     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x170e:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "ChannelMessageGIF"
            int r2 = org.telegram.messenger.R.string.ChannelMessageGIF     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachGif"
            int r2 = org.telegram.messenger.R.string.AttachGif     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x172c:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "ChannelMessageLiveLocation"
            int r2 = org.telegram.messenger.R.string.ChannelMessageLiveLocation     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachLiveLocation"
            int r2 = org.telegram.messenger.R.string.AttachLiveLocation     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x174a:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "ChannelMessageMap"
            int r2 = org.telegram.messenger.R.string.ChannelMessageMap     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachLocation"
            int r2 = org.telegram.messenger.R.string.AttachLocation     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x1768:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "ChannelMessagePoll2"
            int r2 = org.telegram.messenger.R.string.ChannelMessagePoll2     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "Poll"
            int r2 = org.telegram.messenger.R.string.Poll     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x178b:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "ChannelMessageQuiz2"
            int r2 = org.telegram.messenger.R.string.ChannelMessageQuiz2     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "QuizPoll"
            int r2 = org.telegram.messenger.R.string.QuizPoll     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x17ae:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "ChannelMessageContact2"
            int r2 = org.telegram.messenger.R.string.ChannelMessageContact2     // Catch:{ all -> 0x1dbf }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r5[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachContact"
            int r2 = org.telegram.messenger.R.string.AttachContact     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x17d1:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "ChannelMessageAudio"
            int r2 = org.telegram.messenger.R.string.ChannelMessageAudio     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachAudio"
            int r2 = org.telegram.messenger.R.string.AttachAudio     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x17ef:
            r6 = r17
            r9 = r25
            int r2 = r10.length     // Catch:{ all -> 0x1dbf }
            r5 = 1
            if (r2 <= r5) goto L_0x1830
            r2 = r10[r5]     // Catch:{ all -> 0x1dbf }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x1dbf }
            if (r2 != 0) goto L_0x1830
            java.lang.String r2 = "ChannelMessageStickerEmoji"
            int r5 = org.telegram.messenger.R.string.ChannelMessageStickerEmoji     // Catch:{ all -> 0x1dbf }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1dbf }
            r8 = 0
            r12 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r7[r8] = r12     // Catch:{ all -> 0x1dbf }
            r8 = 1
            r12 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r7[r8] = r12     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x1dbf }
            r2.<init>()     // Catch:{ all -> 0x1dbf }
            r7 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r2.append(r7)     // Catch:{ all -> 0x1dbf }
            r2.append(r11)     // Catch:{ all -> 0x1dbf }
            int r7 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r7)     // Catch:{ all -> 0x1dbf }
            r2.append(r1)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x1830:
            java.lang.String r2 = "ChannelMessageSticker"
            int r5 = org.telegram.messenger.R.string.ChannelMessageSticker     // Catch:{ all -> 0x1dbf }
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r10 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r8[r7] = r10     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r8)     // Catch:{ all -> 0x1dbf }
            int r2 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x1848:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "ChannelMessageDocument"
            int r2 = org.telegram.messenger.R.string.ChannelMessageDocument     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachDocument"
            int r2 = org.telegram.messenger.R.string.AttachDocument     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x1866:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "ChannelMessageRound"
            int r2 = org.telegram.messenger.R.string.ChannelMessageRound     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachRound"
            int r2 = org.telegram.messenger.R.string.AttachRound     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x1883:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "ChannelMessageVideo"
            int r2 = org.telegram.messenger.R.string.ChannelMessageVideo     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachVideo"
            int r2 = org.telegram.messenger.R.string.AttachVideo     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x18a0:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "ChannelMessagePhoto"
            int r2 = org.telegram.messenger.R.string.ChannelMessagePhoto     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "AttachPhoto"
            int r2 = org.telegram.messenger.R.string.AttachPhoto     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18d9
        L_0x18bd:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "ChannelMessageNoText"
            int r2 = org.telegram.messenger.R.string.ChannelMessageNoText     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = "Message"
            int r2 = org.telegram.messenger.R.string.Message     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
        L_0x18d9:
            r18 = r1
            r1 = r5
            r5 = r6
            goto L_0x1cb5
        L_0x18df:
            r6 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageAlbum"
            int r2 = org.telegram.messenger.R.string.NotificationMessageAlbum     // Catch:{ all -> 0x1dbf }
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]     // Catch:{ all -> 0x1dbf }
            r5 = 0
            r8 = r10[r5]     // Catch:{ all -> 0x1dbf }
            r7[r5] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
        L_0x18f3:
            r5 = r6
        L_0x18f4:
            r18 = 0
            r25 = 1
            goto L_0x1cb7
        L_0x18fa:
            r5 = r17
            r9 = r25
            int r2 = org.telegram.messenger.R.string.NotificationMessageFew     // Catch:{ all -> 0x1dbf }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r1[r7] = r8     // Catch:{ all -> 0x1dbf }
            r8 = 1
            r10 = r10[r8]     // Catch:{ all -> 0x1dbf }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1dbf }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1dbf }
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ all -> 0x1dbf }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r15, r10, r11)     // Catch:{ all -> 0x1dbf }
            r1[r8] = r7     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r6, r2, r1)     // Catch:{ all -> 0x1dbf }
            goto L_0x18f4
        L_0x1920:
            r5 = r17
            r9 = r25
            int r1 = org.telegram.messenger.R.string.NotificationMessageFew     // Catch:{ all -> 0x1dbf }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1dbf }
            r8 = 0
            r11 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r7[r8] = r11     // Catch:{ all -> 0x1dbf }
            r11 = 1
            r10 = r10[r11]     // Catch:{ all -> 0x1dbf }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1dbf }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1dbf }
            java.lang.Object[] r12 = new java.lang.Object[r8]     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r10, r12)     // Catch:{ all -> 0x1dbf }
            r7[r11] = r2     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r6, r1, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x18f4
        L_0x1946:
            r5 = r17
            r9 = r25
            int r1 = org.telegram.messenger.R.string.NotificationMessageFew     // Catch:{ all -> 0x1dbf }
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r11 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r2[r7] = r11     // Catch:{ all -> 0x1dbf }
            r11 = 1
            r10 = r10[r11]     // Catch:{ all -> 0x1dbf }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1dbf }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1dbf }
            java.lang.Object[] r12 = new java.lang.Object[r7]     // Catch:{ all -> 0x1dbf }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r8, r10, r12)     // Catch:{ all -> 0x1dbf }
            r2[r11] = r7     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r6, r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18f4
        L_0x196c:
            r5 = r17
            r9 = r25
            int r1 = org.telegram.messenger.R.string.NotificationMessageFew     // Catch:{ all -> 0x1dbf }
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r2[r7] = r8     // Catch:{ all -> 0x1dbf }
            r8 = 1
            r10 = r10[r8]     // Catch:{ all -> 0x1dbf }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1dbf }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1dbf }
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ all -> 0x1dbf }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r12, r10, r11)     // Catch:{ all -> 0x1dbf }
            r2[r8] = r7     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r6, r1, r2)     // Catch:{ all -> 0x1dbf }
            goto L_0x18f4
        L_0x1993:
            r5 = r17
            r9 = r25
            r12 = r26
            java.lang.String r1 = "NotificationMessageForwardFew"
            int r2 = org.telegram.messenger.R.string.NotificationMessageForwardFew     // Catch:{ all -> 0x1dbf }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            r8 = 1
            r10 = r10[r8]     // Catch:{ all -> 0x1dbf }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1dbf }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1dbf }
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ all -> 0x1dbf }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r12, r10, r11)     // Catch:{ all -> 0x1dbf }
            r6[r8] = r7     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x18f4
        L_0x19be:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageInvoice"
            int r2 = org.telegram.messenger.R.string.NotificationMessageInvoice     // Catch:{ all -> 0x1dbf }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "PaymentInvoice"
            int r6 = org.telegram.messenger.R.string.PaymentInvoice     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x19e1:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGameScored"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGameScored     // Catch:{ all -> 0x1dbf }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 2
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1cb3
        L_0x1a01:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGame"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGame     // Catch:{ all -> 0x1dbf }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "AttachGame"
            int r6 = org.telegram.messenger.R.string.AttachGame     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x1a24:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageGif"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGif     // Catch:{ all -> 0x1dbf }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r6 = 0
            r8 = r10[r6]     // Catch:{ all -> 0x1dbf }
            r7[r6] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "AttachGif"
            int r6 = org.telegram.messenger.R.string.AttachGif     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x1a42:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageLiveLocation"
            int r2 = org.telegram.messenger.R.string.NotificationMessageLiveLocation     // Catch:{ all -> 0x1dbf }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r6 = 0
            r8 = r10[r6]     // Catch:{ all -> 0x1dbf }
            r7[r6] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "AttachLiveLocation"
            int r6 = org.telegram.messenger.R.string.AttachLiveLocation     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x1a60:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageMap"
            int r2 = org.telegram.messenger.R.string.NotificationMessageMap     // Catch:{ all -> 0x1dbf }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r6 = 0
            r8 = r10[r6]     // Catch:{ all -> 0x1dbf }
            r7[r6] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "AttachLocation"
            int r6 = org.telegram.messenger.R.string.AttachLocation     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x1a7e:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessagePoll2"
            int r2 = org.telegram.messenger.R.string.NotificationMessagePoll2     // Catch:{ all -> 0x1dbf }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "Poll"
            int r6 = org.telegram.messenger.R.string.Poll     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x1aa1:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageQuiz2"
            int r2 = org.telegram.messenger.R.string.NotificationMessageQuiz2     // Catch:{ all -> 0x1dbf }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "QuizPoll"
            int r6 = org.telegram.messenger.R.string.QuizPoll     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x1ac4:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageContact2"
            int r2 = org.telegram.messenger.R.string.NotificationMessageContact2     // Catch:{ all -> 0x1dbf }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "AttachContact"
            int r6 = org.telegram.messenger.R.string.AttachContact     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x1ae7:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageAudio"
            int r2 = org.telegram.messenger.R.string.NotificationMessageAudio     // Catch:{ all -> 0x1dbf }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r6 = 0
            r8 = r10[r6]     // Catch:{ all -> 0x1dbf }
            r7[r6] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "AttachAudio"
            int r6 = org.telegram.messenger.R.string.AttachAudio     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x1b05:
            r5 = r17
            r9 = r25
            int r2 = r10.length     // Catch:{ all -> 0x1dbf }
            r6 = 1
            if (r2 <= r6) goto L_0x1b45
            r2 = r10[r6]     // Catch:{ all -> 0x1dbf }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x1dbf }
            if (r2 != 0) goto L_0x1b45
            java.lang.String r2 = "NotificationMessageStickerEmoji"
            int r6 = org.telegram.messenger.R.string.NotificationMessageStickerEmoji     // Catch:{ all -> 0x1dbf }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1dbf }
            r8 = 0
            r12 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r7[r8] = r12     // Catch:{ all -> 0x1dbf }
            r8 = 1
            r12 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r7[r8] = r12     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x1dbf }
            r6.<init>()     // Catch:{ all -> 0x1dbf }
            r7 = r10[r8]     // Catch:{ all -> 0x1dbf }
            r6.append(r7)     // Catch:{ all -> 0x1dbf }
            r6.append(r11)     // Catch:{ all -> 0x1dbf }
            int r7 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r7)     // Catch:{ all -> 0x1dbf }
            r6.append(r1)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = r6.toString()     // Catch:{ all -> 0x1dbf }
            goto L_0x1b5b
        L_0x1b45:
            java.lang.String r2 = "NotificationMessageSticker"
            int r6 = org.telegram.messenger.R.string.NotificationMessageSticker     // Catch:{ all -> 0x1dbf }
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r10 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r8[r7] = r10     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r8)     // Catch:{ all -> 0x1dbf }
            int r6 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1dbf }
        L_0x1b5b:
            r18 = r1
            r1 = r2
            goto L_0x1cb5
        L_0x1b60:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageDocument"
            int r2 = org.telegram.messenger.R.string.NotificationMessageDocument     // Catch:{ all -> 0x1dbf }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r6 = 0
            r8 = r10[r6]     // Catch:{ all -> 0x1dbf }
            r7[r6] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "AttachDocument"
            int r6 = org.telegram.messenger.R.string.AttachDocument     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x1b7e:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageRound"
            int r2 = org.telegram.messenger.R.string.NotificationMessageRound     // Catch:{ all -> 0x1dbf }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r6 = 0
            r8 = r10[r6]     // Catch:{ all -> 0x1dbf }
            r7[r6] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "AttachRound"
            int r6 = org.telegram.messenger.R.string.AttachRound     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x1b9c:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "ActionTakeScreenshoot"
            int r2 = org.telegram.messenger.R.string.ActionTakeScreenshoot     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "un1"
            r6 = 0
            r7 = r10[r6]     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = r1.replace(r2, r7)     // Catch:{ all -> 0x1dbf }
            goto L_0x1cb3
        L_0x1bb3:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageSDVideo"
            int r2 = org.telegram.messenger.R.string.NotificationMessageSDVideo     // Catch:{ all -> 0x1dbf }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r6 = 0
            r8 = r10[r6]     // Catch:{ all -> 0x1dbf }
            r7[r6] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "AttachDestructingVideo"
            int r6 = org.telegram.messenger.R.string.AttachDestructingVideo     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x1bd1:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageVideo"
            int r2 = org.telegram.messenger.R.string.NotificationMessageVideo     // Catch:{ all -> 0x1dbf }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r6 = 0
            r8 = r10[r6]     // Catch:{ all -> 0x1dbf }
            r7[r6] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "AttachVideo"
            int r6 = org.telegram.messenger.R.string.AttachVideo     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x1bef:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageSDPhoto"
            int r2 = org.telegram.messenger.R.string.NotificationMessageSDPhoto     // Catch:{ all -> 0x1dbf }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r6 = 0
            r8 = r10[r6]     // Catch:{ all -> 0x1dbf }
            r7[r6] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "AttachDestructingPhoto"
            int r6 = org.telegram.messenger.R.string.AttachDestructingPhoto     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x1c0c:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessagePhoto"
            int r2 = org.telegram.messenger.R.string.NotificationMessagePhoto     // Catch:{ all -> 0x1dbf }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r6 = 0
            r8 = r10[r6]     // Catch:{ all -> 0x1dbf }
            r7[r6] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "AttachPhoto"
            int r6 = org.telegram.messenger.R.string.AttachPhoto     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageNoText"
            int r2 = org.telegram.messenger.R.string.NotificationMessageNoText     // Catch:{ all -> 0x1dbf }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r6 = 0
            r8 = r10[r6]     // Catch:{ all -> 0x1dbf }
            r7[r6] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r7)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "Message"
            int r6 = org.telegram.messenger.R.string.Message     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageText"
            int r2 = org.telegram.messenger.R.string.NotificationMessageText     // Catch:{ all -> 0x1dbf }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1dbf }
            r2 = r10[r7]     // Catch:{ all -> 0x1dbf }
        L_0x1CLASSNAME:
            r18 = r2
            goto L_0x1cb5
        L_0x1CLASSNAME:
            r5 = r17
            r9 = r25
            java.lang.String r1 = "NotificationMessageRecurringPay"
            int r2 = org.telegram.messenger.R.string.NotificationMessageRecurringPay     // Catch:{ all -> 0x1dbf }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1dbf }
            r7 = 0
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            r7 = 1
            r8 = r10[r7]     // Catch:{ all -> 0x1dbf }
            r6[r7] = r8     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "PaymentInvoice"
            int r6 = org.telegram.messenger.R.string.PaymentInvoice     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)     // Catch:{ all -> 0x1dbf }
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            if (r1 == 0) goto L_0x1c9c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x1dbf }
            r1.<init>()     // Catch:{ all -> 0x1dbf }
            java.lang.String r2 = "unhandled loc_key = "
            r1.append(r2)     // Catch:{ all -> 0x1dbf }
            r1.append(r14)     // Catch:{ all -> 0x1dbf }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x1dbf }
            org.telegram.messenger.FileLog.w(r1)     // Catch:{ all -> 0x1dbf }
        L_0x1c9c:
            r1 = 0
            goto L_0x1cb3
        L_0x1c9e:
            r51 = r1
            r31 = r2
            r46 = r5
            r34 = r8
            r45 = r11
            r44 = r12
            r48 = r13
            r5 = r15
            r9 = r25
            java.lang.String r1 = getReactedText(r14, r10)     // Catch:{ all -> 0x1dbf }
        L_0x1cb3:
            r18 = 0
        L_0x1cb5:
            r25 = 0
        L_0x1cb7:
            if (r1 == 0) goto L_0x1dab
            org.telegram.tgnet.TLRPC$TL_message r2 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x1dbf }
            r2.<init>()     // Catch:{ all -> 0x1dbf }
            r10 = r51
            r2.id = r10     // Catch:{ all -> 0x1dbf }
            r6 = r27
            r2.random_id = r6     // Catch:{ all -> 0x1dbf }
            if (r18 == 0) goto L_0x1ccb
            r6 = r18
            goto L_0x1ccc
        L_0x1ccb:
            r6 = r1
        L_0x1ccc:
            r2.message = r6     // Catch:{ all -> 0x1dbf }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r6 = r53 / r6
            int r7 = (int) r6     // Catch:{ all -> 0x1dbf }
            r2.date = r7     // Catch:{ all -> 0x1dbf }
            if (r16 == 0) goto L_0x1cde
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r6 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x1dbf }
            r6.<init>()     // Catch:{ all -> 0x1dbf }
            r2.action = r6     // Catch:{ all -> 0x1dbf }
        L_0x1cde:
            if (r48 == 0) goto L_0x1ce7
            int r6 = r2.flags     // Catch:{ all -> 0x1dbf }
            r7 = -2147483648(0xfffffffvar_, float:-0.0)
            r6 = r6 | r7
            r2.flags = r6     // Catch:{ all -> 0x1dbf }
        L_0x1ce7:
            r2.dialog_id = r3     // Catch:{ all -> 0x1dbf }
            r3 = 0
            int r6 = (r46 > r3 ? 1 : (r46 == r3 ? 0 : -1))
            if (r6 == 0) goto L_0x1cfd
            org.telegram.tgnet.TLRPC$TL_peerChannel r3 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x1dbf }
            r3.<init>()     // Catch:{ all -> 0x1dbf }
            r2.peer_id = r3     // Catch:{ all -> 0x1dbf }
            r12 = r46
            r3.channel_id = r12     // Catch:{ all -> 0x1dbf }
            r12 = r23
            goto L_0x1d1c
        L_0x1cfd:
            r3 = 0
            int r6 = (r23 > r3 ? 1 : (r23 == r3 ? 0 : -1))
            if (r6 == 0) goto L_0x1d0f
            org.telegram.tgnet.TLRPC$TL_peerChat r3 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x1dbf }
            r3.<init>()     // Catch:{ all -> 0x1dbf }
            r2.peer_id = r3     // Catch:{ all -> 0x1dbf }
            r12 = r23
            r3.chat_id = r12     // Catch:{ all -> 0x1dbf }
            goto L_0x1d1c
        L_0x1d0f:
            r12 = r23
            org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1dbf }
            r3.<init>()     // Catch:{ all -> 0x1dbf }
            r2.peer_id = r3     // Catch:{ all -> 0x1dbf }
            r6 = r32
            r3.user_id = r6     // Catch:{ all -> 0x1dbf }
        L_0x1d1c:
            int r3 = r2.flags     // Catch:{ all -> 0x1dbf }
            r3 = r3 | 256(0x100, float:3.59E-43)
            r2.flags = r3     // Catch:{ all -> 0x1dbf }
            r3 = 0
            int r6 = (r41 > r3 ? 1 : (r41 == r3 ? 0 : -1))
            if (r6 == 0) goto L_0x1d32
            org.telegram.tgnet.TLRPC$TL_peerChat r3 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x1dbf }
            r3.<init>()     // Catch:{ all -> 0x1dbf }
            r2.from_id = r3     // Catch:{ all -> 0x1dbf }
            r3.chat_id = r12     // Catch:{ all -> 0x1dbf }
            goto L_0x1d5a
        L_0x1d32:
            r3 = 0
            int r6 = (r35 > r3 ? 1 : (r35 == r3 ? 0 : -1))
            if (r6 == 0) goto L_0x1d44
            org.telegram.tgnet.TLRPC$TL_peerChannel r3 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x1dbf }
            r3.<init>()     // Catch:{ all -> 0x1dbf }
            r2.from_id = r3     // Catch:{ all -> 0x1dbf }
            r6 = r35
            r3.channel_id = r6     // Catch:{ all -> 0x1dbf }
            goto L_0x1d5a
        L_0x1d44:
            r3 = 0
            int r6 = (r39 > r3 ? 1 : (r39 == r3 ? 0 : -1))
            if (r6 == 0) goto L_0x1d56
            org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1dbf }
            r3.<init>()     // Catch:{ all -> 0x1dbf }
            r2.from_id = r3     // Catch:{ all -> 0x1dbf }
            r6 = r39
            r3.user_id = r6     // Catch:{ all -> 0x1dbf }
            goto L_0x1d5a
        L_0x1d56:
            org.telegram.tgnet.TLRPC$Peer r3 = r2.peer_id     // Catch:{ all -> 0x1dbf }
            r2.from_id = r3     // Catch:{ all -> 0x1dbf }
        L_0x1d5a:
            if (r37 != 0) goto L_0x1d61
            if (r16 == 0) goto L_0x1d5f
            goto L_0x1d61
        L_0x1d5f:
            r3 = 0
            goto L_0x1d62
        L_0x1d61:
            r3 = 1
        L_0x1d62:
            r2.mentioned = r3     // Catch:{ all -> 0x1dbf }
            r3 = r38
            r2.silent = r3     // Catch:{ all -> 0x1dbf }
            r2.from_scheduled = r9     // Catch:{ all -> 0x1dbf }
            org.telegram.messenger.MessageObject r3 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x1dbf }
            r19 = r3
            r20 = r29
            r21 = r2
            r22 = r1
            r23 = r43
            r24 = r45
            r26 = r44
            r27 = r48
            r28 = r34
            r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27, r28)     // Catch:{ all -> 0x1dbf }
            r1 = r31
            boolean r1 = r14.startsWith(r1)     // Catch:{ all -> 0x1dbf }
            if (r1 != 0) goto L_0x1d92
            boolean r1 = r14.startsWith(r5)     // Catch:{ all -> 0x1dbf }
            if (r1 == 0) goto L_0x1d90
            goto L_0x1d92
        L_0x1d90:
            r1 = 0
            goto L_0x1d93
        L_0x1d92:
            r1 = 1
        L_0x1d93:
            r3.isReactionPush = r1     // Catch:{ all -> 0x1dbf }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x1dbf }
            r1.<init>()     // Catch:{ all -> 0x1dbf }
            r1.add(r3)     // Catch:{ all -> 0x1dbf }
            org.telegram.messenger.NotificationsController r2 = org.telegram.messenger.NotificationsController.getInstance(r29)     // Catch:{ all -> 0x1dbf }
            java.util.concurrent.CountDownLatch r3 = countDownLatch     // Catch:{ all -> 0x1dbf }
            r4 = 1
            r2.processNewMessages(r1, r4, r4, r3)     // Catch:{ all -> 0x1dbf }
            r8 = 0
            goto L_0x1dac
        L_0x1da9:
            r30 = r7
        L_0x1dab:
            r8 = 1
        L_0x1dac:
            if (r8 == 0) goto L_0x1db3
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1dbf }
            r1.countDown()     // Catch:{ all -> 0x1dbf }
        L_0x1db3:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r29)     // Catch:{ all -> 0x1dbf }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r29)     // Catch:{ all -> 0x1dbf }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1dbf }
            goto L_0x1eea
        L_0x1dbf:
            r0 = move-exception
            r1 = r0
            r5 = r14
            r4 = r29
            goto L_0x1e90
        L_0x1dc6:
            r0 = move-exception
            r30 = r7
            goto L_0x1dce
        L_0x1dca:
            r0 = move-exception
            r30 = r7
        L_0x1dcd:
            r14 = r9
        L_0x1dce:
            r1 = r0
            r5 = r14
        L_0x1dd0:
            r4 = r29
            goto L_0x1e99
        L_0x1dd4:
            r0 = move-exception
            r29 = r4
            goto L_0x1e94
        L_0x1dd9:
            r29 = r4
            r30 = r7
            r14 = r9
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1df0 }
            org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda0 r2 = new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda0     // Catch:{ all -> 0x1df0 }
            r4 = r29
            r2.<init>(r4)     // Catch:{ all -> 0x1e8d }
            r1.postRunnable(r2)     // Catch:{ all -> 0x1e8d }
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1e8d }
            r1.countDown()     // Catch:{ all -> 0x1e8d }
            return
        L_0x1df0:
            r0 = move-exception
            r4 = r29
            goto L_0x1e8e
        L_0x1df5:
            r30 = r7
            r14 = r9
            org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda1 r1 = new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda1     // Catch:{ all -> 0x1e8d }
            r1.<init>(r4)     // Catch:{ all -> 0x1e8d }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ all -> 0x1e8d }
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1e8d }
            r1.countDown()     // Catch:{ all -> 0x1e8d }
            return
        L_0x1e06:
            r30 = r7
            r14 = r9
            r9 = r6
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x1e8d }
            r1.<init>()     // Catch:{ all -> 0x1e8d }
            r2 = 0
            r1.popup = r2     // Catch:{ all -> 0x1e8d }
            r2 = 2
            r1.flags = r2     // Catch:{ all -> 0x1e8d }
            r2 = 1000(0x3e8, double:4.94E-321)
            long r2 = r53 / r2
            int r3 = (int) r2     // Catch:{ all -> 0x1e8d }
            r1.inbox_date = r3     // Catch:{ all -> 0x1e8d }
            java.lang.String r2 = "message"
            java.lang.String r2 = r9.getString(r2)     // Catch:{ all -> 0x1e8d }
            r1.message = r2     // Catch:{ all -> 0x1e8d }
            java.lang.String r2 = "announcement"
            r1.type = r2     // Catch:{ all -> 0x1e8d }
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ all -> 0x1e8d }
            r2.<init>()     // Catch:{ all -> 0x1e8d }
            r1.media = r2     // Catch:{ all -> 0x1e8d }
            org.telegram.tgnet.TLRPC$TL_updates r2 = new org.telegram.tgnet.TLRPC$TL_updates     // Catch:{ all -> 0x1e8d }
            r2.<init>()     // Catch:{ all -> 0x1e8d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r3 = r2.updates     // Catch:{ all -> 0x1e8d }
            r3.add(r1)     // Catch:{ all -> 0x1e8d }
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1e8d }
            org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda3 r3 = new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda3     // Catch:{ all -> 0x1e8d }
            r3.<init>(r4, r2)     // Catch:{ all -> 0x1e8d }
            r1.postRunnable(r3)     // Catch:{ all -> 0x1e8d }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1e8d }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1e8d }
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1e8d }
            r1.countDown()     // Catch:{ all -> 0x1e8d }
            return
        L_0x1e50:
            r30 = r7
            r14 = r9
            java.lang.String r1 = "dc"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x1e8d }
            java.lang.String r2 = "addr"
            java.lang.String r2 = r11.getString(r2)     // Catch:{ all -> 0x1e8d }
            java.lang.String r3 = ":"
            java.lang.String[] r2 = r2.split(r3)     // Catch:{ all -> 0x1e8d }
            int r3 = r2.length     // Catch:{ all -> 0x1e8d }
            r5 = 2
            if (r3 == r5) goto L_0x1e6f
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1e8d }
            r1.countDown()     // Catch:{ all -> 0x1e8d }
            return
        L_0x1e6f:
            r3 = 0
            r3 = r2[r3]     // Catch:{ all -> 0x1e8d }
            r5 = 1
            r2 = r2[r5]     // Catch:{ all -> 0x1e8d }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ all -> 0x1e8d }
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1e8d }
            r5.applyDatacenterAddress(r1, r3, r2)     // Catch:{ all -> 0x1e8d }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1e8d }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1e8d }
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1e8d }
            r1.countDown()     // Catch:{ all -> 0x1e8d }
            return
        L_0x1e8d:
            r0 = move-exception
        L_0x1e8e:
            r1 = r0
            r5 = r14
        L_0x1e90:
            r7 = r30
            goto L_0x1e99
        L_0x1e93:
            r0 = move-exception
        L_0x1e94:
            r30 = r7
            r14 = r9
            r1 = r0
            r5 = r14
        L_0x1e99:
            r2 = -1
            goto L_0x1eb2
        L_0x1e9b:
            r0 = move-exception
            r30 = r7
            r14 = r9
            r1 = r0
            r5 = r14
        L_0x1ea1:
            r2 = -1
            r4 = -1
            goto L_0x1eb2
        L_0x1ea4:
            r0 = move-exception
            r30 = r7
        L_0x1ea7:
            r1 = r0
            r2 = -1
            r4 = -1
            r5 = 0
            goto L_0x1eb2
        L_0x1eac:
            r0 = move-exception
            r1 = r0
            r2 = -1
            r4 = -1
            r5 = 0
            r7 = 0
        L_0x1eb2:
            if (r4 == r2) goto L_0x1ec4
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r4)
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
            r2.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r2 = countDownLatch
            r2.countDown()
            goto L_0x1ec7
        L_0x1ec4:
            onDecryptError()
        L_0x1ec7:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x1ee7
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "error in loc_key = "
            r2.append(r3)
            r2.append(r5)
            java.lang.String r3 = " json "
            r2.append(r3)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r2)
        L_0x1ee7:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1eea:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.PushListenerController.lambda$processRemoteMessage$7(java.lang.String, java.lang.String, long):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$processRemoteMessage$5(int i) {
        if (UserConfig.getInstance(i).getClientUserId() != 0) {
            UserConfig.getInstance(i).clearConfig();
            MessagesController.getInstance(i).performLogout(0);
        }
    }

    private static String getReactedText(String str, Object[] objArr) {
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
                return LocaleController.formatString("PushChatReactContact", R.string.PushChatReactContact, objArr);
            case 1:
                return LocaleController.formatString("PushReactGeoLocation", R.string.PushReactGeoLocation, objArr);
            case 2:
                return LocaleController.formatString("PushChatReactNotext", R.string.PushChatReactNotext, objArr);
            case 3:
                return LocaleController.formatString("PushReactNoText", R.string.PushReactNoText, objArr);
            case 4:
                return LocaleController.formatString("PushChatReactInvoice", R.string.PushChatReactInvoice, objArr);
            case 5:
                return LocaleController.formatString("PushReactContect", R.string.PushReactContect, objArr);
            case 6:
                return LocaleController.formatString("PushChatReactSticker", R.string.PushChatReactSticker, objArr);
            case 7:
                return LocaleController.formatString("PushReactGame", R.string.PushReactGame, objArr);
            case 8:
                return LocaleController.formatString("PushReactPoll", R.string.PushReactPoll, objArr);
            case 9:
                return LocaleController.formatString("PushReactQuiz", R.string.PushReactQuiz, objArr);
            case 10:
                return LocaleController.formatString("PushReactText", R.string.PushReactText, objArr);
            case 11:
                return LocaleController.formatString("PushReactInvoice", R.string.PushReactInvoice, objArr);
            case 12:
                return LocaleController.formatString("PushChatReactDoc", R.string.PushChatReactDoc, objArr);
            case 13:
                return LocaleController.formatString("PushChatReactGeo", R.string.PushChatReactGeo, objArr);
            case 14:
                return LocaleController.formatString("PushChatReactGif", R.string.PushChatReactGif, objArr);
            case 15:
                return LocaleController.formatString("PushReactSticker", R.string.PushReactSticker, objArr);
            case 16:
                return LocaleController.formatString("PushChatReactAudio", R.string.PushChatReactAudio, objArr);
            case 17:
                return LocaleController.formatString("PushChatReactPhoto", R.string.PushChatReactPhoto, objArr);
            case 18:
                return LocaleController.formatString("PushChatReactRound", R.string.PushChatReactRound, objArr);
            case 19:
                return LocaleController.formatString("PushChatReactVideo", R.string.PushChatReactVideo, objArr);
            case 20:
                return LocaleController.formatString("PushChatReactGeoLive", R.string.PushChatReactGeoLive, objArr);
            case 21:
                return LocaleController.formatString("PushReactAudio", R.string.PushReactAudio, objArr);
            case 22:
                return LocaleController.formatString("PushReactPhoto", R.string.PushReactPhoto, objArr);
            case 23:
                return LocaleController.formatString("PushReactRound", R.string.PushReactRound, objArr);
            case 24:
                return LocaleController.formatString("PushReactVideo", R.string.PushReactVideo, objArr);
            case 25:
                return LocaleController.formatString("PushReactDoc", R.string.PushReactDoc, objArr);
            case 26:
                return LocaleController.formatString("PushReactGeo", R.string.PushReactGeo, objArr);
            case 27:
                return LocaleController.formatString("PushReactGif", R.string.PushReactGif, objArr);
            case 28:
                return LocaleController.formatString("PushChatReactGame", R.string.PushChatReactGame, objArr);
            case 29:
                return LocaleController.formatString("PushChatReactPoll", R.string.PushChatReactPoll, objArr);
            case 30:
                return LocaleController.formatString("PushChatReactQuiz", R.string.PushChatReactQuiz, objArr);
            case 31:
                return LocaleController.formatString("PushChatReactText", R.string.PushChatReactText, objArr);
            default:
                return null;
        }
    }

    private static void onDecryptError() {
        for (int i = 0; i < 4; i++) {
            if (UserConfig.getInstance(i).isClientActivated()) {
                ConnectionsManager.onInternalPushReceived(i);
                ConnectionsManager.getInstance(i).resumeNetworkMaybe();
            }
        }
        countDownLatch.countDown();
    }

    public static final class GooglePushListenerServiceProvider implements IPushListenerServiceProvider {
        public static final GooglePushListenerServiceProvider INSTANCE = new GooglePushListenerServiceProvider();
        private Boolean hasServices;

        public String getLogTitle() {
            return "Google Play Services";
        }

        public int getPushType() {
            return 2;
        }

        private GooglePushListenerServiceProvider() {
        }

        public void onRequestPushToken() {
            String str = SharedConfig.pushString;
            if (!TextUtils.isEmpty(str)) {
                if (BuildVars.DEBUG_PRIVATE_VERSION && BuildVars.LOGS_ENABLED) {
                    FileLog.d("FCM regId = " + str);
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("FCM Registration not found.");
            }
            Utilities.globalQueue.postRunnable(new PushListenerController$GooglePushListenerServiceProvider$$ExternalSyntheticLambda1(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onRequestPushToken$1() {
            try {
                SharedConfig.pushStringGetTimeStart = SystemClock.elapsedRealtime();
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new PushListenerController$GooglePushListenerServiceProvider$$ExternalSyntheticLambda0(this));
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onRequestPushToken$0(Task task) {
            SharedConfig.pushStringGetTimeEnd = SystemClock.elapsedRealtime();
            if (!task.isSuccessful()) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("Failed to get regid");
                }
                SharedConfig.pushStringStatus = "__FIREBASE_FAILED__";
                PushListenerController.sendRegistrationToServer(getPushType(), (String) null);
                return;
            }
            String str = (String) task.getResult();
            if (!TextUtils.isEmpty(str)) {
                PushListenerController.sendRegistrationToServer(getPushType(), str);
            }
        }

        public boolean hasServices() {
            if (this.hasServices == null) {
                try {
                    this.hasServices = Boolean.valueOf(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ApplicationLoader.applicationContext) == 0);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    this.hasServices = Boolean.FALSE;
                }
            }
            return this.hasServices.booleanValue();
        }
    }
}
