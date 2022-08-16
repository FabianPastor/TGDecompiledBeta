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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v9, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v45, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v20, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v53, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v44, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v45, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v14, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v47, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v22, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v18, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v21, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v24, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v44, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v45, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v46, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v47, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v48, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v98, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v154, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v161, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v167, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v169, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v171, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v177, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v183, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v32, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v73, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v76, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v79, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v35, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v38, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v201, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v205, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v207, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v209, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v217, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v221, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v44, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v226, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v143, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v47, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v228, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v231, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v235, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v48, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v101, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v238, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v241, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v244, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v108, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v248, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v252, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v256, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v259, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v265, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v270, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v273, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v278, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v134, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v283, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v138, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v286, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v141, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v289, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v143, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v292, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v295, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v298, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v148, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v149, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v301, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v304, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v153, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v156, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v141, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v162, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v143, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v168, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v173, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v73, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v175, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v78, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v317, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v177, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v321, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v178, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v82, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v331, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v209, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v190, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v339, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v214, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v347, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v196, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v198, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v174, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v200, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v355, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v201, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v363, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v206, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v207, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v229, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v208, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v209, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v210, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v206, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v371, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v212, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v375, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v213, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v214, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v211, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v216, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v217, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v382, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v221, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v223, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v396, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v226, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v227, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v206, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v229, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v232, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v404, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v213, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v252, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v412, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v244, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v256, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v422, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v252, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v430, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v266, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v256, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v257, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v438, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v258, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v260, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v262, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v446, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v263, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v264, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v276, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v266, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v454, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v268, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v269, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v281, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v270, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v272, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v462, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v273, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v274, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v287, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v275, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v276, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v391, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v392, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v393, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v396, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v10, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v11, resolved type: java.lang.String[]} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x021c, code lost:
        if (r14 == 0) goto L_0x1cc8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x021e, code lost:
        if (r14 == 1) goto L_0x1c7e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x0220, code lost:
        if (r14 == 2) goto L_0x1c6d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x0222, code lost:
        if (r14 == 3) goto L_0x1CLASSNAME;
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
        if (r11.has("chat_id") == false) goto L_0x027d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:?, code lost:
        r3 = r11.getLong("chat_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x026a, code lost:
        r32 = r9;
        r51 = r3;
        r3 = -r3;
        r8 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0273, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x0274, code lost:
        r32 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x0276, code lost:
        r1 = r0;
        r4 = r29;
        r5 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x027d, code lost:
        r32 = r9;
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x0286, code lost:
        if (r11.has("encryption_id") == false) goto L_0x0296;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:?, code lost:
        r3 = org.telegram.messenger.DialogObject.makeEncryptedDialogId((long) r11.getInt("encryption_id"));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x0294, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:159:0x029c, code lost:
        if (r11.has("schedule") == false) goto L_0x02a8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:163:0x02a4, code lost:
        if (r11.getInt("schedule") != 1) goto L_0x02a8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:164:0x02a6, code lost:
        r14 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:165:0x02a8, code lost:
        r14 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:167:0x02ab, code lost:
        if (r3 != 0) goto L_0x02c0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x02ad, code lost:
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x02b5, code lost:
        if ("ENCRYPTED_MESSAGE".equals(r10) == false) goto L_0x02c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x02b7, code lost:
        r3 = org.telegram.messenger.NotificationsController.globalSecretChatId;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x02ba, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x02c0, code lost:
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x02c4, code lost:
        if (r3 == 0) goto L_0x1c1f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:181:0x02ce, code lost:
        if ("READ_HISTORY".equals(r10) == false) goto L_0x0351;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:?, code lost:
        r2 = r11.getInt("max_id");
        r11 = new java.util.ArrayList();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:0x02dd, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x02fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x02df, code lost:
        org.telegram.messenger.FileLog.d(r1 + " received read notification max_id = " + r2 + " for dialogId = " + r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x0300, code lost:
        if (r5 == 0) goto L_0x0319;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x0302, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox();
        r1.channel_id = r5;
        r1.max_id = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x030b, code lost:
        r2 = -1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:?, code lost:
        r1.still_unread_count = -1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:?, code lost:
        r11.add(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x0312, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x0313, code lost:
        r1 = r0;
        r5 = r10;
        r4 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x0319, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox();
        r3 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x0324, code lost:
        if (r3 == 0) goto L_0x0330;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x0326, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r1.peer = r5;
        r5.user_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x0330, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r1.peer = r3;
        r3.chat_id = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x0339, code lost:
        r1.max_id = r2;
        r11.add(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x033e, code lost:
        org.telegram.messenger.MessagesController.getInstance(r29).processUpdateArray(r11, (java.util.ArrayList<org.telegram.tgnet.TLRPC$User>) null, (java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat>) null, false, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x0351, code lost:
        r13 = r14;
        r14 = r30;
        r31 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x035c, code lost:
        r20 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x0360, code lost:
        if ("MESSAGE_DELETED".equals(r10) == false) goto L_0x03cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:?, code lost:
        r2 = r11.getString("messages").split(",");
        r7 = new androidx.collection.LongSparseArray();
        r8 = new java.util.ArrayList();
        r9 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x0378, code lost:
        if (r9 >= r2.length) goto L_0x0386;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x037a, code lost:
        r8.add(org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2[r9]));
        r9 = r9 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x0386, code lost:
        r7.put(-r5, r8);
        org.telegram.messenger.NotificationsController.getInstance(r29).removeDeletedMessagesFromNotifications(r7);
        org.telegram.messenger.MessagesController.getInstance(r29).deleteMessagesByPush(r3, r8, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x03a0, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x03a2, code lost:
        org.telegram.messenger.FileLog.d(r1 + " received " + r10 + " for dialogId = " + r3 + " mids = " + android.text.TextUtils.join(",", r8));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x03d3, code lost:
        if (android.text.TextUtils.isEmpty(r10) != false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x03db, code lost:
        if (r11.has("msg_id") == false) goto L_0x03e6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:220:0x03dd, code lost:
        r7 = r11.getInt("msg_id");
        r24 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x03e6, code lost:
        r24 = r14;
        r7 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x03ef, code lost:
        if (r11.has("random_id") == false) goto L_0x0400;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:0x03f1, code lost:
        r14 = org.telegram.messenger.Utilities.parseLong(r11.getString("random_id")).longValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:225:0x0400, code lost:
        r14 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x0402, code lost:
        if (r7 == 0) goto L_0x0441;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x0404, code lost:
        r26 = r8;
        r8 = org.telegram.messenger.MessagesController.getInstance(r29).dialogs_read_inbox_max.get(java.lang.Long.valueOf(r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x0416, code lost:
        if (r8 != null) goto L_0x0435;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x0418, code lost:
        r8 = java.lang.Integer.valueOf(org.telegram.messenger.MessagesStorage.getInstance(r29).getDialogReadMax(false, r3));
        r28 = "messages";
        org.telegram.messenger.MessagesController.getInstance(r29).dialogs_read_inbox_max.put(java.lang.Long.valueOf(r3), r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x0435, code lost:
        r28 = "messages";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x043b, code lost:
        if (r7 <= r8.intValue()) goto L_0x043f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x043d, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x043f, code lost:
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:0x0441, code lost:
        r26 = r8;
        r28 = "messages";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x0449, code lost:
        if (r14 == 0) goto L_0x043f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x0453, code lost:
        if (org.telegram.messenger.MessagesStorage.getInstance(r29).checkMessageByRandomId(r14) != false) goto L_0x043f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x045c, code lost:
        if (r10.startsWith("REACT_") != false) goto L_0x0464;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x0462, code lost:
        if (r10.startsWith("CHAT_REACT_") == false) goto L_0x0465;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x0464, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x0465, code lost:
        if (r8 == false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x0467, code lost:
        r33 = r14;
        r8 = r11.optLong("chat_from_id", 0);
        r32 = "CHAT_REACT_";
        r35 = " for dialogId = ";
        r36 = r11.optLong("chat_from_broadcast_id", 0);
        r12 = r11.optLong("chat_from_group_id", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x0485, code lost:
        if (r8 != 0) goto L_0x048e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x0489, code lost:
        if (r12 == 0) goto L_0x048c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x048c, code lost:
        r14 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x048e, code lost:
        r14 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x0495, code lost:
        if (r11.has("mention") == false) goto L_0x04a2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x049d, code lost:
        if (r11.getInt("mention") == 0) goto L_0x04a2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x049f, code lost:
        r38 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x04a2, code lost:
        r38 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x04aa, code lost:
        if (r11.has("silent") == false) goto L_0x04b7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x04b2, code lost:
        if (r11.getInt("silent") == 0) goto L_0x04b7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x04b4, code lost:
        r39 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x04b7, code lost:
        r39 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x04b9, code lost:
        r40 = r8;
        r8 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x04c3, code lost:
        if (r8.has("loc_args") == false) goto L_0x04e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x04c5, code lost:
        r8 = r8.getJSONArray("loc_args");
        r9 = r8.length();
        r15 = new java.lang.String[r9];
        r42 = r12;
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x04d4, code lost:
        if (r12 >= r9) goto L_0x04df;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x04d6, code lost:
        r15[r12] = r8.getString(r12);
        r12 = r12 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x04df, code lost:
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:273:0x04e1, code lost:
        r42 = r12;
        r8 = 0;
        r15 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x04e5, code lost:
        r9 = r15[r8];
        r8 = r11.has("edit_date");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:275:0x04f3, code lost:
        if (r10.startsWith("CHAT_") == false) goto L_0x0529;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:277:0x04f9, code lost:
        if (org.telegram.messenger.UserObject.isReplyUser(r3) == false) goto L_0x0513;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x04fb, code lost:
        r9 = r9 + " @ " + r15[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0517, code lost:
        if (r5 == 0) goto L_0x051b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x0519, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x051b, code lost:
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x051c, code lost:
        r12 = false;
        r16 = false;
        r51 = r11;
        r11 = r9;
        r9 = r15[1];
        r13 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x052f, code lost:
        if (r10.startsWith("PINNED_") == false) goto L_0x0540;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x0535, code lost:
        if (r5 == 0) goto L_0x0539;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x0537, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x0539, code lost:
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x053a, code lost:
        r13 = r11;
        r11 = null;
        r12 = false;
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x0546, code lost:
        if (r10.startsWith("CHANNEL_") == false) goto L_0x054b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x0548, code lost:
        r11 = null;
        r12 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x054b, code lost:
        r11 = null;
        r12 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x054d, code lost:
        r13 = false;
        r16 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x0552, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x057e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x0554, code lost:
        r44 = r9;
        org.telegram.messenger.FileLog.d(r1 + " received message notification " + r10 + r35 + r3 + " mid = " + r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x057e, code lost:
        r44 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x0584, code lost:
        if (r10.startsWith("REACT_") != false) goto L_0x1b11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x0586, code lost:
        r1 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x058c, code lost:
        if (r10.startsWith(r1) == false) goto L_0x0592;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x058e, code lost:
        r32 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x0596, code lost:
        switch(r10.hashCode()) {
            case -2100047043: goto L_0x0aee;
            case -2091498420: goto L_0x0ae3;
            case -2053872415: goto L_0x0ad8;
            case -2039746363: goto L_0x0acd;
            case -2023218804: goto L_0x0ac2;
            case -1979538588: goto L_0x0ab7;
            case -1979536003: goto L_0x0aac;
            case -1979535888: goto L_0x0aa1;
            case -1969004705: goto L_0x0a96;
            case -1946699248: goto L_0x0a8b;
            case -1717283471: goto L_0x0a7f;
            case -1646640058: goto L_0x0a73;
            case -1528047021: goto L_0x0a67;
            case -1507149394: goto L_0x0a5a;
            case -1493579426: goto L_0x0a4e;
            case -1482481933: goto L_0x0a42;
            case -1480102982: goto L_0x0a35;
            case -1478041834: goto L_0x0a29;
            case -1474543101: goto L_0x0a1e;
            case -1465695932: goto L_0x0a12;
            case -1374906292: goto L_0x0a06;
            case -1372940586: goto L_0x09fa;
            case -1264245338: goto L_0x09ee;
            case -1236154001: goto L_0x09e2;
            case -1236086700: goto L_0x09d6;
            case -1236077786: goto L_0x09ca;
            case -1235796237: goto L_0x09be;
            case -1235760759: goto L_0x09b2;
            case -1235686303: goto L_0x09a5;
            case -1198046100: goto L_0x099a;
            case -1124254527: goto L_0x098e;
            case -1085137927: goto L_0x0982;
            case -1084856378: goto L_0x0976;
            case -1084820900: goto L_0x096a;
            case -1084746444: goto L_0x095e;
            case -819729482: goto L_0x0952;
            case -772141857: goto L_0x0946;
            case -638310039: goto L_0x093a;
            case -590403924: goto L_0x092e;
            case -589196239: goto L_0x0922;
            case -589193654: goto L_0x0916;
            case -589193539: goto L_0x090a;
            case -440169325: goto L_0x08fe;
            case -412748110: goto L_0x08f2;
            case -228518075: goto L_0x08e6;
            case -213586509: goto L_0x08da;
            case -115582002: goto L_0x08ce;
            case -112621464: goto L_0x08c2;
            case -108522133: goto L_0x08b6;
            case -107572034: goto L_0x08a8;
            case -40534265: goto L_0x089c;
            case 52369421: goto L_0x0890;
            case 65254746: goto L_0x0884;
            case 141040782: goto L_0x0878;
            case 202550149: goto L_0x086c;
            case 309993049: goto L_0x0860;
            case 309995634: goto L_0x0854;
            case 309995749: goto L_0x0848;
            case 320532812: goto L_0x083c;
            case 328933854: goto L_0x0830;
            case 331340546: goto L_0x0824;
            case 342406591: goto L_0x0818;
            case 344816990: goto L_0x080c;
            case 346878138: goto L_0x0800;
            case 350376871: goto L_0x07f4;
            case 608430149: goto L_0x07e8;
            case 615714517: goto L_0x07dd;
            case 715508879: goto L_0x07d1;
            case 728985323: goto L_0x07c5;
            case 731046471: goto L_0x07b9;
            case 734545204: goto L_0x07ad;
            case 802032552: goto L_0x07a1;
            case 991498806: goto L_0x0795;
            case 1007364121: goto L_0x0789;
            case 1019850010: goto L_0x077d;
            case 1019917311: goto L_0x0771;
            case 1019926225: goto L_0x0765;
            case 1020207774: goto L_0x0759;
            case 1020243252: goto L_0x074d;
            case 1020317708: goto L_0x0741;
            case 1060282259: goto L_0x0735;
            case 1060349560: goto L_0x0729;
            case 1060358474: goto L_0x071d;
            case 1060640023: goto L_0x0711;
            case 1060675501: goto L_0x0705;
            case 1060749957: goto L_0x06f8;
            case 1073049781: goto L_0x06ec;
            case 1078101399: goto L_0x06e0;
            case 1110103437: goto L_0x06d4;
            case 1160762272: goto L_0x06c8;
            case 1172918249: goto L_0x06bc;
            case 1234591620: goto L_0x06b0;
            case 1281128640: goto L_0x06a4;
            case 1281131225: goto L_0x0698;
            case 1281131340: goto L_0x068c;
            case 1310789062: goto L_0x067f;
            case 1333118583: goto L_0x0673;
            case 1361447897: goto L_0x0667;
            case 1498266155: goto L_0x065b;
            case 1533804208: goto L_0x064f;
            case 1540131626: goto L_0x0643;
            case 1547988151: goto L_0x0637;
            case 1561464595: goto L_0x062b;
            case 1563525743: goto L_0x061f;
            case 1567024476: goto L_0x0613;
            case 1810705077: goto L_0x0607;
            case 1815177512: goto L_0x05fb;
            case 1954774321: goto L_0x05ef;
            case 1963241394: goto L_0x05e3;
            case 2014789757: goto L_0x05d7;
            case 2022049433: goto L_0x05cb;
            case 2034984710: goto L_0x05bf;
            case 2048733346: goto L_0x05b3;
            case 2099392181: goto L_0x05a7;
            case 2140162142: goto L_0x059b;
            default: goto L_0x0599;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x05a1, code lost:
        if (r10.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x05a3, code lost:
        r9 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x05ad, code lost:
        if (r10.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x05af, code lost:
        r9 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x05b9, code lost:
        if (r10.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x05bb, code lost:
        r9 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x05c5, code lost:
        if (r10.equals("CHANNEL_MESSAGE_PLAYLIST") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x05c7, code lost:
        r9 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x05d1, code lost:
        if (r10.equals("PINNED_CONTACT") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x05d3, code lost:
        r9 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x05dd, code lost:
        if (r10.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x05df, code lost:
        r9 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x05e9, code lost:
        if (r10.equals("LOCKED_MESSAGE") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x05eb, code lost:
        r9 = 'm';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:0x05f5, code lost:
        if (r10.equals("CHAT_MESSAGE_PLAYLIST") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x05f7, code lost:
        r9 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:333:0x0601, code lost:
        if (r10.equals("CHANNEL_MESSAGES") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x0603, code lost:
        r9 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x060d, code lost:
        if (r10.equals("MESSAGE_INVOICE") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x060f, code lost:
        r9 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:339:0x0619, code lost:
        if (r10.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x061b, code lost:
        r9 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x0625, code lost:
        if (r10.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x0627, code lost:
        r9 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x0631, code lost:
        if (r10.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x0633, code lost:
        r9 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x063d, code lost:
        if (r10.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x063f, code lost:
        r9 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:351:0x0649, code lost:
        if (r10.equals("MESSAGE_PLAYLIST") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x064b, code lost:
        r9 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x0655, code lost:
        if (r10.equals("MESSAGE_VIDEOS") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x0657, code lost:
        r9 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x0661, code lost:
        if (r10.equals("PHONE_CALL_MISSED") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x0663, code lost:
        r9 = 'r';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x066d, code lost:
        if (r10.equals("MESSAGE_PHOTOS") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x066f, code lost:
        r9 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x0679, code lost:
        if (r10.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x067b, code lost:
        r9 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x0685, code lost:
        if (r10.equals("MESSAGE_NOTEXT") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x0687, code lost:
        r32 = r1;
        r9 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x0692, code lost:
        if (r10.equals("MESSAGE_GIF") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x0694, code lost:
        r9 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x069e, code lost:
        if (r10.equals("MESSAGE_GEO") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x06a0, code lost:
        r9 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:375:0x06aa, code lost:
        if (r10.equals("MESSAGE_DOC") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x06ac, code lost:
        r9 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x06b6, code lost:
        if (r10.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x06b8, code lost:
        r9 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:381:0x06c2, code lost:
        if (r10.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x06c4, code lost:
        r9 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x06ce, code lost:
        if (r10.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x06d0, code lost:
        r9 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x06da, code lost:
        if (r10.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x06dc, code lost:
        r9 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x06e6, code lost:
        if (r10.equals("CHAT_TITLE_EDITED") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x06e8, code lost:
        r9 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x06f2, code lost:
        if (r10.equals("PINNED_NOTEXT") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x06f4, code lost:
        r9 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x06fe, code lost:
        if (r10.equals("MESSAGE_TEXT") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x0700, code lost:
        r32 = r1;
        r9 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x070b, code lost:
        if (r10.equals("MESSAGE_QUIZ") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:400:0x070d, code lost:
        r9 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x0717, code lost:
        if (r10.equals("MESSAGE_POLL") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0719, code lost:
        r9 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x0723, code lost:
        if (r10.equals("MESSAGE_GAME") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x0725, code lost:
        r9 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x072f, code lost:
        if (r10.equals("MESSAGE_FWDS") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x0731, code lost:
        r9 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x073b, code lost:
        if (r10.equals("MESSAGE_DOCS") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x073d, code lost:
        r9 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x0747, code lost:
        if (r10.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x0749, code lost:
        r9 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x0753, code lost:
        if (r10.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x0755, code lost:
        r9 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x075f, code lost:
        if (r10.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x0761, code lost:
        r9 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x076b, code lost:
        if (r10.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x076d, code lost:
        r9 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:426:0x0777, code lost:
        if (r10.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x0779, code lost:
        r9 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x0783, code lost:
        if (r10.equals("CHAT_MESSAGE_DOCS") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x0785, code lost:
        r9 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:432:0x078f, code lost:
        if (r10.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x0791, code lost:
        r9 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x079b, code lost:
        if (r10.equals("PINNED_GEOLIVE") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x079d, code lost:
        r9 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x07a7, code lost:
        if (r10.equals("MESSAGE_CONTACT") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x07a9, code lost:
        r9 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x07b3, code lost:
        if (r10.equals("PINNED_VIDEO") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x07b5, code lost:
        r9 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:444:0x07bf, code lost:
        if (r10.equals("PINNED_ROUND") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x07c1, code lost:
        r9 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x07cb, code lost:
        if (r10.equals("PINNED_PHOTO") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x07cd, code lost:
        r9 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x07d7, code lost:
        if (r10.equals("PINNED_AUDIO") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x07d9, code lost:
        r9 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x07e3, code lost:
        if (r10.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x07e5, code lost:
        r9 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:456:0x07ee, code lost:
        if (r10.equals("CHAT_VOICECHAT_INVITE_YOU") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x07f0, code lost:
        r9 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x07fa, code lost:
        if (r10.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:460:0x07fc, code lost:
        r9 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x0806, code lost:
        if (r10.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x0808, code lost:
        r9 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x0812, code lost:
        if (r10.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x0814, code lost:
        r9 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x081e, code lost:
        if (r10.equals("CHAT_VOICECHAT_END") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x0820, code lost:
        r9 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x082a, code lost:
        if (r10.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:472:0x082c, code lost:
        r9 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x0836, code lost:
        if (r10.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0838, code lost:
        r9 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x0842, code lost:
        if (r10.equals("MESSAGES") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x0844, code lost:
        r9 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:480:0x084e, code lost:
        if (r10.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x0850, code lost:
        r9 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x085a, code lost:
        if (r10.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x085c, code lost:
        r9 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:486:0x0866, code lost:
        if (r10.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x0868, code lost:
        r9 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x0872, code lost:
        if (r10.equals("CHAT_VOICECHAT_INVITE") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x0874, code lost:
        r9 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x087e, code lost:
        if (r10.equals("CHAT_LEFT") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x0880, code lost:
        r9 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x088a, code lost:
        if (r10.equals("CHAT_ADD_YOU") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x088c, code lost:
        r9 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x0896, code lost:
        if (r10.equals("REACT_TEXT") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x0898, code lost:
        r9 = 'i';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x08a2, code lost:
        if (r10.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x08a4, code lost:
        r9 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x08ae, code lost:
        if (r10.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x08b0, code lost:
        r32 = r1;
        r9 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x08bc, code lost:
        if (r10.equals("AUTH_REGION") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x08be, code lost:
        r9 = 'l';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x08c8, code lost:
        if (r10.equals("CONTACT_JOINED") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x08ca, code lost:
        r9 = 'j';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x08d4, code lost:
        if (r10.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x08d6, code lost:
        r9 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x08e0, code lost:
        if (r10.equals("ENCRYPTION_REQUEST") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x08e2, code lost:
        r9 = 'n';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:519:0x08ec, code lost:
        if (r10.equals("MESSAGE_GEOLIVE") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x08ee, code lost:
        r9 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x08f8, code lost:
        if (r10.equals("CHAT_DELETE_YOU") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x08fa, code lost:
        r9 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x0904, code lost:
        if (r10.equals("AUTH_UNKNOWN") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x0906, code lost:
        r9 = 'k';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x0910, code lost:
        if (r10.equals("PINNED_GIF") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0912, code lost:
        r9 = 'g';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x091c, code lost:
        if (r10.equals("PINNED_GEO") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x091e, code lost:
        r9 = 'b';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x0928, code lost:
        if (r10.equals("PINNED_DOC") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x092a, code lost:
        r9 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x0934, code lost:
        if (r10.equals("PINNED_GAME_SCORE") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:538:0x0936, code lost:
        r9 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:540:0x0940, code lost:
        if (r10.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0942, code lost:
        r9 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x094c, code lost:
        if (r10.equals("PHONE_CALL_REQUEST") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x094e, code lost:
        r9 = 'p';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x0958, code lost:
        if (r10.equals("PINNED_STICKER") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x095a, code lost:
        r9 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x0964, code lost:
        if (r10.equals("PINNED_TEXT") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x0966, code lost:
        r9 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:552:0x0970, code lost:
        if (r10.equals("PINNED_QUIZ") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x0972, code lost:
        r9 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x097c, code lost:
        if (r10.equals("PINNED_POLL") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x097e, code lost:
        r9 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x0988, code lost:
        if (r10.equals("PINNED_GAME") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x098a, code lost:
        r9 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x0994, code lost:
        if (r10.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x0996, code lost:
        r9 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x09a0, code lost:
        if (r10.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x09a2, code lost:
        r9 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x09ab, code lost:
        if (r10.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x09ad, code lost:
        r32 = r1;
        r9 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x09b8, code lost:
        if (r10.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x09ba, code lost:
        r9 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x09c4, code lost:
        if (r10.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x09c6, code lost:
        r9 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:576:0x09d0, code lost:
        if (r10.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x09d2, code lost:
        r9 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x09dc, code lost:
        if (r10.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x09de, code lost:
        r9 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:582:0x09e8, code lost:
        if (r10.equals("CHANNEL_MESSAGE_DOCS") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x09ea, code lost:
        r9 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x09f4, code lost:
        if (r10.equals("PINNED_INVOICE") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x09f6, code lost:
        r9 = 'f';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:588:0x0a00, code lost:
        if (r10.equals("CHAT_RETURNED") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x0a02, code lost:
        r9 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x0a0c, code lost:
        if (r10.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x0a0e, code lost:
        r9 = 'h';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x0a18, code lost:
        if (r10.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x0a1a, code lost:
        r9 = 'o';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0a24, code lost:
        if (r10.equals("MESSAGE_VIDEO") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x0a26, code lost:
        r9 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:600:0x0a2f, code lost:
        if (r10.equals("MESSAGE_ROUND") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0a31, code lost:
        r9 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0a3b, code lost:
        if (r10.equals("MESSAGE_PHOTO") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x0a3d, code lost:
        r32 = r1;
        r9 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x0a48, code lost:
        if (r10.equals("MESSAGE_MUTED") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x0a4a, code lost:
        r9 = 'q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x0a54, code lost:
        if (r10.equals("MESSAGE_AUDIO") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x0a56, code lost:
        r9 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:612:0x0a60, code lost:
        if (r10.equals("MESSAGE_RECURRING_PAY") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x0a62, code lost:
        r32 = r1;
        r9 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x0a6d, code lost:
        if (r10.equals("CHAT_MESSAGES") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x0a6f, code lost:
        r9 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:618:0x0a79, code lost:
        if (r10.equals("CHAT_VOICECHAT_START") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x0a7b, code lost:
        r9 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x0a85, code lost:
        if (r10.equals("CHAT_REQ_JOINED") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x0a87, code lost:
        r9 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x0a91, code lost:
        if (r10.equals("CHAT_JOINED") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:625:0x0a93, code lost:
        r9 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:627:0x0a9c, code lost:
        if (r10.equals("CHAT_ADD_MEMBER") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x0a9e, code lost:
        r9 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x0aa7, code lost:
        if (r10.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x0aa9, code lost:
        r9 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x0ab2, code lost:
        if (r10.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x0ab4, code lost:
        r9 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x0abd, code lost:
        if (r10.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x0abf, code lost:
        r9 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x0ac8, code lost:
        if (r10.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x0aca, code lost:
        r9 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0ad3, code lost:
        if (r10.equals("MESSAGE_STICKER") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0ad5, code lost:
        r9 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x0ade, code lost:
        if (r10.equals("CHAT_CREATED") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x0ae0, code lost:
        r9 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x0ae9, code lost:
        if (r10.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0aeb, code lost:
        r9 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x0af4, code lost:
        if (r10.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0af6, code lost:
        r9 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0af8, code lost:
        r32 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0afb, code lost:
        r32 = r1;
        r9 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0afe, code lost:
        r17 = "REACT_";
        r35 = r8;
        r45 = r12;
        r46 = r11;
        r47 = r5;
        r49 = r13;
        r50 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0b1e, code lost:
        switch(r9) {
            case 0: goto L_0x1adb;
            case 1: goto L_0x1abd;
            case 2: goto L_0x1abd;
            case 3: goto L_0x1aa4;
            case 4: goto L_0x1a8b;
            case 5: goto L_0x1a72;
            case 6: goto L_0x1a59;
            case 7: goto L_0x1a3f;
            case 8: goto L_0x1a2c;
            case 9: goto L_0x1a12;
            case 10: goto L_0x19f8;
            case 11: goto L_0x19a3;
            case 12: goto L_0x1989;
            case 13: goto L_0x196a;
            case 14: goto L_0x194b;
            case 15: goto L_0x192c;
            case 16: goto L_0x1912;
            case 17: goto L_0x18f8;
            case 18: goto L_0x18de;
            case 19: goto L_0x18bf;
            case 20: goto L_0x18a3;
            case 21: goto L_0x18a3;
            case 22: goto L_0x1884;
            case 23: goto L_0x185d;
            case 24: goto L_0x183a;
            case 25: goto L_0x1818;
            case 26: goto L_0x17f6;
            case 27: goto L_0x17d4;
            case 28: goto L_0x17c1;
            case 29: goto L_0x17a7;
            case 30: goto L_0x178d;
            case 31: goto L_0x1773;
            case 32: goto L_0x1759;
            case 33: goto L_0x173f;
            case 34: goto L_0x16ea;
            case 35: goto L_0x16d0;
            case 36: goto L_0x16b1;
            case 37: goto L_0x1692;
            case 38: goto L_0x1673;
            case 39: goto L_0x1659;
            case 40: goto L_0x163f;
            case 41: goto L_0x1625;
            case 42: goto L_0x160b;
            case 43: goto L_0x15e1;
            case 44: goto L_0x15be;
            case 45: goto L_0x159b;
            case 46: goto L_0x1578;
            case 47: goto L_0x1555;
            case 48: goto L_0x1543;
            case 49: goto L_0x1525;
            case 50: goto L_0x1506;
            case 51: goto L_0x14e7;
            case 52: goto L_0x14c8;
            case 53: goto L_0x14a9;
            case 54: goto L_0x148a;
            case 55: goto L_0x1417;
            case 56: goto L_0x13f8;
            case 57: goto L_0x13d4;
            case 58: goto L_0x13b0;
            case 59: goto L_0x138c;
            case 60: goto L_0x136d;
            case 61: goto L_0x134e;
            case 62: goto L_0x132f;
            case 63: goto L_0x130b;
            case 64: goto L_0x12ea;
            case 65: goto L_0x12c6;
            case 66: goto L_0x12af;
            case 67: goto L_0x12af;
            case 68: goto L_0x1298;
            case 69: goto L_0x1281;
            case 70: goto L_0x1265;
            case 71: goto L_0x124e;
            case 72: goto L_0x1232;
            case 73: goto L_0x121b;
            case 74: goto L_0x1204;
            case 75: goto L_0x11ed;
            case 76: goto L_0x11d6;
            case 77: goto L_0x11bf;
            case 78: goto L_0x11a8;
            case 79: goto L_0x1191;
            case 80: goto L_0x117a;
            case 81: goto L_0x114d;
            case 82: goto L_0x1124;
            case 83: goto L_0x10fb;
            case 84: goto L_0x10d2;
            case 85: goto L_0x10a9;
            case 86: goto L_0x1092;
            case 87: goto L_0x1040;
            case 88: goto L_0x0ff8;
            case 89: goto L_0x0fb0;
            case 90: goto L_0x0var_;
            case 91: goto L_0x0var_;
            case 92: goto L_0x0ed8;
            case 93: goto L_0x0e27;
            case 94: goto L_0x0ddf;
            case 95: goto L_0x0d8d;
            case 96: goto L_0x0d3b;
            case 97: goto L_0x0ce9;
            case 98: goto L_0x0ca1;
            case 99: goto L_0x0CLASSNAME;
            case 100: goto L_0x0CLASSNAME;
            case 101: goto L_0x0bc9;
            case 102: goto L_0x0b81;
            case 103: goto L_0x0b39;
            case 104: goto L_0x0b25;
            case 105: goto L_0x1b0f;
            case 106: goto L_0x1b0f;
            case 107: goto L_0x1b0f;
            case 108: goto L_0x1b0f;
            case 109: goto L_0x1b0f;
            case 110: goto L_0x1b0f;
            case 111: goto L_0x1b0f;
            case 112: goto L_0x1b0f;
            case 113: goto L_0x1b0f;
            case 114: goto L_0x1b0f;
            default: goto L_0x0b21;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0b25, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", org.telegram.messenger.R.string.YouHaveNewMessage);
        r44 = org.telegram.messenger.LocaleController.getString("SecretChatName", org.telegram.messenger.R.string.SecretChatName);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x0b3d, code lost:
        if (r3 <= 0) goto L_0x0b56;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0b3f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", org.telegram.messenger.R.string.NotificationActionPinnedGifUser, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x0b56, code lost:
        if (r14 == false) goto L_0x0b6f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0b58, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", org.telegram.messenger.R.string.NotificationActionPinnedGif, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x0b6f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", org.telegram.messenger.R.string.NotificationActionPinnedGifChannel, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x0b85, code lost:
        if (r3 <= 0) goto L_0x0b9e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0b87, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceUser", org.telegram.messenger.R.string.NotificationActionPinnedInvoiceUser, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0b9e, code lost:
        if (r14 == false) goto L_0x0bb7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x0ba0, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", org.telegram.messenger.R.string.NotificationActionPinnedInvoice, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:671:0x0bb7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", org.telegram.messenger.R.string.NotificationActionPinnedInvoiceChannel, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:673:0x0bcd, code lost:
        if (r3 <= 0) goto L_0x0be6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0bcf, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreUser", org.telegram.messenger.R.string.NotificationActionPinnedGameScoreUser, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0be6, code lost:
        if (r14 == false) goto L_0x0bff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0be8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", org.telegram.messenger.R.string.NotificationActionPinnedGameScore, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x0bff, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", org.telegram.messenger.R.string.NotificationActionPinnedGameScoreChannel, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:679:0x0CLASSNAME, code lost:
        if (r3 <= 0) goto L_0x0c2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", org.telegram.messenger.R.string.NotificationActionPinnedGameUser, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0c2e, code lost:
        if (r14 == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", org.telegram.messenger.R.string.NotificationActionPinnedGame, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", org.telegram.messenger.R.string.NotificationActionPinnedGameChannel, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:685:0x0c5d, code lost:
        if (r3 <= 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0c5f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveUser, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x0CLASSNAME, code lost:
        if (r14 == false) goto L_0x0c8f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", org.telegram.messenger.R.string.NotificationActionPinnedGeoLive, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0c8f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveChannel, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x0ca5, code lost:
        if (r3 <= 0) goto L_0x0cbe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0ca7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", org.telegram.messenger.R.string.NotificationActionPinnedGeoUser, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0cbe, code lost:
        if (r14 == false) goto L_0x0cd7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0cc0, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", org.telegram.messenger.R.string.NotificationActionPinnedGeo, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0cd7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", org.telegram.messenger.R.string.NotificationActionPinnedGeoChannel, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0ced, code lost:
        if (r3 <= 0) goto L_0x0d06;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0cef, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", org.telegram.messenger.R.string.NotificationActionPinnedPollUser, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0d06, code lost:
        if (r14 == false) goto L_0x0d24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0d08, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", org.telegram.messenger.R.string.NotificationActionPinnedPoll2, r15[0], r15[2], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0d24, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", org.telegram.messenger.R.string.NotificationActionPinnedPollChannel2, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0d3f, code lost:
        if (r3 <= 0) goto L_0x0d58;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0d41, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", org.telegram.messenger.R.string.NotificationActionPinnedQuizUser, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0d58, code lost:
        if (r14 == false) goto L_0x0d76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0d5a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", org.telegram.messenger.R.string.NotificationActionPinnedQuiz2, r15[0], r15[2], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0d76, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", org.telegram.messenger.R.string.NotificationActionPinnedQuizChannel2, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0d91, code lost:
        if (r3 <= 0) goto L_0x0daa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0d93, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", org.telegram.messenger.R.string.NotificationActionPinnedContactUser, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0daa, code lost:
        if (r14 == false) goto L_0x0dc8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0dac, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", org.telegram.messenger.R.string.NotificationActionPinnedContact2, r15[0], r15[2], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0dc8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", org.telegram.messenger.R.string.NotificationActionPinnedContactChannel2, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0de3, code lost:
        if (r3 <= 0) goto L_0x0dfc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0de5, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", org.telegram.messenger.R.string.NotificationActionPinnedVoiceUser, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0dfc, code lost:
        if (r14 == false) goto L_0x0e15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0dfe, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", org.telegram.messenger.R.string.NotificationActionPinnedVoice, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0e15, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", org.telegram.messenger.R.string.NotificationActionPinnedVoiceChannel, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:0x0e2b, code lost:
        if (r3 <= 0) goto L_0x0e62;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0e2f, code lost:
        if (r15.length <= 1) goto L_0x0e50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0e37, code lost:
        if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x0e50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0e39, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiUser, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:727:0x0e50, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", org.telegram.messenger.R.string.NotificationActionPinnedStickerUser, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0e62, code lost:
        if (r14 == false) goto L_0x0ea3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0e66, code lost:
        if (r15.length <= 2) goto L_0x0e8c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0e6e, code lost:
        if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x0e8c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0e70, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", org.telegram.messenger.R.string.NotificationActionPinnedStickerEmoji, r15[0], r15[2], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0e8c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", org.telegram.messenger.R.string.NotificationActionPinnedSticker, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0ea5, code lost:
        if (r15.length <= 1) goto L_0x0ec6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0ead, code lost:
        if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x0ec6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x0eaf, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiChannel, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0ec6, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", org.telegram.messenger.R.string.NotificationActionPinnedStickerChannel, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0edc, code lost:
        if (r3 <= 0) goto L_0x0ef5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x0ede, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", org.telegram.messenger.R.string.NotificationActionPinnedFileUser, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x0ef5, code lost:
        if (r14 == false) goto L_0x0f0e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x0ef7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", org.telegram.messenger.R.string.NotificationActionPinnedFile, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x0f0e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", org.telegram.messenger.R.string.NotificationActionPinnedFileChannel, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x0var_, code lost:
        if (r3 <= 0) goto L_0x0f3d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", org.telegram.messenger.R.string.NotificationActionPinnedRoundUser, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x0f3d, code lost:
        if (r14 == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x0f3f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", org.telegram.messenger.R.string.NotificationActionPinnedRound, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", org.telegram.messenger.R.string.NotificationActionPinnedRoundChannel, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x0f6c, code lost:
        if (r3 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x0f6e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", org.telegram.messenger.R.string.NotificationActionPinnedVideoUser, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x0var_, code lost:
        if (r14 == false) goto L_0x0f9e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", org.telegram.messenger.R.string.NotificationActionPinnedVideo, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x0f9e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", org.telegram.messenger.R.string.NotificationActionPinnedVideoChannel, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x0fb4, code lost:
        if (r3 <= 0) goto L_0x0fcd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x0fb6, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", org.telegram.messenger.R.string.NotificationActionPinnedPhotoUser, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x0fcd, code lost:
        if (r14 == false) goto L_0x0fe6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x0fcf, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", org.telegram.messenger.R.string.NotificationActionPinnedPhoto, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x0fe6, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", org.telegram.messenger.R.string.NotificationActionPinnedPhotoChannel, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x0ffc, code lost:
        if (r3 <= 0) goto L_0x1015;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x0ffe, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", org.telegram.messenger.R.string.NotificationActionPinnedNoTextUser, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x1015, code lost:
        if (r14 == false) goto L_0x102e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x1017, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", org.telegram.messenger.R.string.NotificationActionPinnedNoText, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x102e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedNoTextChannel, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x1044, code lost:
        if (r3 <= 0) goto L_0x105d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x1046, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", org.telegram.messenger.R.string.NotificationActionPinnedTextUser, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x105d, code lost:
        if (r14 == false) goto L_0x107b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x105f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.R.string.NotificationActionPinnedText, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x107b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedTextChannel, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x1092, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", org.telegram.messenger.R.string.NotificationGroupAlbum, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x10a9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", org.telegram.messenger.R.string.NotificationGroupFew, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r15[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x10d2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", org.telegram.messenger.R.string.NotificationGroupFew, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r15[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x10fb, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", org.telegram.messenger.R.string.NotificationGroupFew, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r15[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x1124, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", org.telegram.messenger.R.string.NotificationGroupFew, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r15[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x114d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", org.telegram.messenger.R.string.NotificationGroupForwardedFew, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString(r28, org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r15[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x117a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("UserAcceptedToGroupPushWithGroup", org.telegram.messenger.R.string.UserAcceptedToGroupPushWithGroup, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x1191, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", org.telegram.messenger.R.string.NotificationGroupAddSelfMega, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x11a8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", org.telegram.messenger.R.string.NotificationGroupAddSelf, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x11bf, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", org.telegram.messenger.R.string.NotificationGroupLeftMember, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x11d6, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", org.telegram.messenger.R.string.NotificationGroupKickYou, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x11ed, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", org.telegram.messenger.R.string.NotificationGroupKickMember, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x1204, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", org.telegram.messenger.R.string.NotificationGroupInvitedYouToCall, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x121b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupEndedCall", org.telegram.messenger.R.string.NotificationGroupEndedCall, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x1232, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", org.telegram.messenger.R.string.NotificationGroupInvitedToCall, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x124e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", org.telegram.messenger.R.string.NotificationGroupCreatedCall, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x1265, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", org.telegram.messenger.R.string.NotificationGroupAddMember, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x1281, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", org.telegram.messenger.R.string.NotificationEditedGroupPhoto, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x1298, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", org.telegram.messenger.R.string.NotificationEditedGroupName, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x12af, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", org.telegram.messenger.R.string.NotificationInvitedToGroup, r15[0], r15[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x12c6, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", org.telegram.messenger.R.string.NotificationMessageGroupInvoice, r15[0], r15[1], r15[2]);
        r1 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", org.telegram.messenger.R.string.PaymentInvoice);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x12ea, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", org.telegram.messenger.R.string.NotificationMessageGroupGameScored, r15[0], r15[1], r15[2], r15[3]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x130b, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", org.telegram.messenger.R.string.NotificationMessageGroupGame, r15[0], r15[1], r15[2]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachGame", org.telegram.messenger.R.string.AttachGame);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x132f, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", org.telegram.messenger.R.string.NotificationMessageGroupGif, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachGif", org.telegram.messenger.R.string.AttachGif);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x134e, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", org.telegram.messenger.R.string.NotificationMessageGroupLiveLocation, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", org.telegram.messenger.R.string.AttachLiveLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x136d, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", org.telegram.messenger.R.string.NotificationMessageGroupMap, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachLocation", org.telegram.messenger.R.string.AttachLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x138c, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", org.telegram.messenger.R.string.NotificationMessageGroupPoll2, r15[0], r15[1], r15[2]);
        r1 = org.telegram.messenger.LocaleController.getString("Poll", org.telegram.messenger.R.string.Poll);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x13b0, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", org.telegram.messenger.R.string.NotificationMessageGroupQuiz2, r15[0], r15[1], r15[2]);
        r1 = org.telegram.messenger.LocaleController.getString("PollQuiz", org.telegram.messenger.R.string.PollQuiz);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x13d4, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", org.telegram.messenger.R.string.NotificationMessageGroupContact2, r15[0], r15[1], r15[2]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachContact", org.telegram.messenger.R.string.AttachContact);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x13f8, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", org.telegram.messenger.R.string.NotificationMessageGroupAudio, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachAudio", org.telegram.messenger.R.string.AttachAudio);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x1419, code lost:
        if (r15.length <= 2) goto L_0x1459;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x1421, code lost:
        if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x1459;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x1423, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", org.telegram.messenger.R.string.NotificationMessageGroupStickerEmoji, r15[0], r15[1], r15[2]);
        r1 = r15[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x1459, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", org.telegram.messenger.R.string.NotificationMessageGroupSticker, r15[0], r15[1]);
        r1 = r15[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x148a, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", org.telegram.messenger.R.string.NotificationMessageGroupDocument, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachDocument", org.telegram.messenger.R.string.AttachDocument);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x14a9, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", org.telegram.messenger.R.string.NotificationMessageGroupRound, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachRound", org.telegram.messenger.R.string.AttachRound);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x14c8, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", org.telegram.messenger.R.string.NotificationMessageGroupVideo, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachVideo", org.telegram.messenger.R.string.AttachVideo);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x14e7, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", org.telegram.messenger.R.string.NotificationMessageGroupPhoto, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachPhoto", org.telegram.messenger.R.string.AttachPhoto);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x1506, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", org.telegram.messenger.R.string.NotificationMessageGroupNoText, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("Message", org.telegram.messenger.R.string.Message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x1525, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", org.telegram.messenger.R.string.NotificationMessageGroupText, r15[0], r15[1], r15[2]);
        r1 = r15[2];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x1543, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", org.telegram.messenger.R.string.ChannelMessageAlbum, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x1555, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", org.telegram.messenger.R.string.ChannelMessageFew, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r15[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x1578, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", org.telegram.messenger.R.string.ChannelMessageFew, r15[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r15[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x159b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", org.telegram.messenger.R.string.ChannelMessageFew, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r15[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x15be, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", org.telegram.messenger.R.string.ChannelMessageFew, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r15[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x15e1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", org.telegram.messenger.R.string.ChannelMessageFew, r15[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r15[1]).intValue(), new java.lang.Object[0]).toLowerCase());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x160b, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", org.telegram.messenger.R.string.NotificationMessageGame, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachGame", org.telegram.messenger.R.string.AttachGame);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x1625, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", org.telegram.messenger.R.string.ChannelMessageGIF, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachGif", org.telegram.messenger.R.string.AttachGif);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x163f, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", org.telegram.messenger.R.string.ChannelMessageLiveLocation, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", org.telegram.messenger.R.string.AttachLiveLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x1659, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", org.telegram.messenger.R.string.ChannelMessageMap, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachLocation", org.telegram.messenger.R.string.AttachLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x1673, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", org.telegram.messenger.R.string.ChannelMessagePoll2, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("Poll", org.telegram.messenger.R.string.Poll);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x1692, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", org.telegram.messenger.R.string.ChannelMessageQuiz2, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("QuizPoll", org.telegram.messenger.R.string.QuizPoll);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x16b1, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", org.telegram.messenger.R.string.ChannelMessageContact2, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachContact", org.telegram.messenger.R.string.AttachContact);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x16d0, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", org.telegram.messenger.R.string.ChannelMessageAudio, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachAudio", org.telegram.messenger.R.string.AttachAudio);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x16ec, code lost:
        if (r15.length <= 1) goto L_0x1727;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x16f4, code lost:
        if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x1727;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x16f6, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", org.telegram.messenger.R.string.ChannelMessageStickerEmoji, r15[0], r15[1]);
        r1 = r15[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x1727, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", org.telegram.messenger.R.string.ChannelMessageSticker, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x173f, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", org.telegram.messenger.R.string.ChannelMessageDocument, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachDocument", org.telegram.messenger.R.string.AttachDocument);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x1759, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", org.telegram.messenger.R.string.ChannelMessageRound, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachRound", org.telegram.messenger.R.string.AttachRound);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x1773, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", org.telegram.messenger.R.string.ChannelMessageVideo, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachVideo", org.telegram.messenger.R.string.AttachVideo);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x178d, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", org.telegram.messenger.R.string.ChannelMessagePhoto, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachPhoto", org.telegram.messenger.R.string.AttachPhoto);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x17a7, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", org.telegram.messenger.R.string.ChannelMessageNoText, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("Message", org.telegram.messenger.R.string.Message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x17c1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", org.telegram.messenger.R.string.NotificationMessageAlbum, r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x17d1, code lost:
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x17d4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", org.telegram.messenger.R.string.NotificationMessageFew, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r15[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x17f6, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", org.telegram.messenger.R.string.NotificationMessageFew, r15[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r15[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x1818, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", org.telegram.messenger.R.string.NotificationMessageFew, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r15[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x183a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", org.telegram.messenger.R.string.NotificationMessageFew, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r15[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x185d, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", org.telegram.messenger.R.string.NotificationMessageForwardFew, r15[0], org.telegram.messenger.LocaleController.formatPluralString(r28, org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r15[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:851:0x1884, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", org.telegram.messenger.R.string.NotificationMessageInvoice, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", org.telegram.messenger.R.string.PaymentInvoice);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x18a3, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", org.telegram.messenger.R.string.NotificationMessageGameScored, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x18bf, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", org.telegram.messenger.R.string.NotificationMessageGame, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachGame", org.telegram.messenger.R.string.AttachGame);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x18de, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", org.telegram.messenger.R.string.NotificationMessageGif, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachGif", org.telegram.messenger.R.string.AttachGif);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x18f8, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", org.telegram.messenger.R.string.NotificationMessageLiveLocation, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", org.telegram.messenger.R.string.AttachLiveLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:856:0x1912, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", org.telegram.messenger.R.string.NotificationMessageMap, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachLocation", org.telegram.messenger.R.string.AttachLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x192c, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", org.telegram.messenger.R.string.NotificationMessagePoll2, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("Poll", org.telegram.messenger.R.string.Poll);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x194b, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", org.telegram.messenger.R.string.NotificationMessageQuiz2, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("QuizPoll", org.telegram.messenger.R.string.QuizPoll);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x196a, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", org.telegram.messenger.R.string.NotificationMessageContact2, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachContact", org.telegram.messenger.R.string.AttachContact);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x1989, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", org.telegram.messenger.R.string.NotificationMessageAudio, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachAudio", org.telegram.messenger.R.string.AttachAudio);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:862:0x19a5, code lost:
        if (r15.length <= 1) goto L_0x19e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:864:0x19ad, code lost:
        if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x19e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:865:0x19af, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", org.telegram.messenger.R.string.NotificationMessageStickerEmoji, r15[0], r15[1]);
        r1 = r15[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:866:0x19e0, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", org.telegram.messenger.R.string.NotificationMessageSticker, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x19f8, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", org.telegram.messenger.R.string.NotificationMessageDocument, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachDocument", org.telegram.messenger.R.string.AttachDocument);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x1a12, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", org.telegram.messenger.R.string.NotificationMessageRound, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachRound", org.telegram.messenger.R.string.AttachRound);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x1a2c, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", org.telegram.messenger.R.string.ActionTakeScreenshoot).replace("un1", r15[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x1a3f, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", org.telegram.messenger.R.string.NotificationMessageSDVideo, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", org.telegram.messenger.R.string.AttachDestructingVideo);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:871:0x1a59, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", org.telegram.messenger.R.string.NotificationMessageVideo, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachVideo", org.telegram.messenger.R.string.AttachVideo);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x1a72, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", org.telegram.messenger.R.string.NotificationMessageSDPhoto, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", org.telegram.messenger.R.string.AttachDestructingPhoto);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x1a8b, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", org.telegram.messenger.R.string.NotificationMessagePhoto, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachPhoto", org.telegram.messenger.R.string.AttachPhoto);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x1aa4, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", org.telegram.messenger.R.string.NotificationMessageNoText, r15[0]);
        r1 = org.telegram.messenger.LocaleController.getString("Message", org.telegram.messenger.R.string.Message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x1abd, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", org.telegram.messenger.R.string.NotificationMessageText, r15[0], r15[1]);
        r1 = r15[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x1ad4, code lost:
        r2 = false;
        r51 = r5;
        r5 = r1;
        r1 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x1adb, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRecurringPay", org.telegram.messenger.R.string.NotificationMessageRecurringPay, r15[0], r15[1]);
        r1 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", org.telegram.messenger.R.string.PaymentInvoice);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x1af9, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1b0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x1afb, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x1b0f, code lost:
        r1 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x1b11, code lost:
        r17 = "REACT_";
        r47 = r5;
        r50 = r7;
        r35 = r8;
        r46 = r11;
        r45 = r12;
        r49 = r13;
        r1 = getReactedText(r10, r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x1b23, code lost:
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x1b24, code lost:
        r5 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x1b25, code lost:
        if (r1 == null) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:0x1b27, code lost:
        r6 = new org.telegram.tgnet.TLRPC$TL_message();
        r6.id = r50;
        r6.random_id = r33;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x1b34, code lost:
        if (r5 == null) goto L_0x1b37;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x1b37, code lost:
        r5 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x1b38, code lost:
        r6.message = r5;
        r6.date = (int) (r55 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x1b41, code lost:
        if (r16 == false) goto L_0x1b4a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x1b43, code lost:
        r6.action = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x1b4a, code lost:
        if (r49 == false) goto L_0x1b53;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x1b4c, code lost:
        r6.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x1b53, code lost:
        r6.dialog_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x1b59, code lost:
        if (r47 == 0) goto L_0x1b69;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x1b5b, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r6.peer_id = r3;
        r3.channel_id = r47;
        r12 = r26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x1b6d, code lost:
        if (r26 == 0) goto L_0x1b7b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:899:0x1b6f, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r6.peer_id = r3;
        r12 = r26;
        r3.chat_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:0x1b7b, code lost:
        r12 = r26;
        r3 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r6.peer_id = r3;
        r3.user_id = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x1b88, code lost:
        r6.flags |= 256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x1b92, code lost:
        if (r42 == 0) goto L_0x1b9e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:0x1b94, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r6.from_id = r3;
        r3.chat_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x1ba2, code lost:
        if (r36 == 0) goto L_0x1bb0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:906:0x1ba4, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r6.from_id = r3;
        r3.channel_id = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x1bb4, code lost:
        if (r40 == 0) goto L_0x1bc2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:909:0x1bb6, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r6.from_id = r3;
        r3.user_id = r40;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:910:0x1bc2, code lost:
        r6.from_id = r6.peer_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:0x1bc6, code lost:
        if (r38 != false) goto L_0x1bcd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:0x1bc8, code lost:
        if (r16 == false) goto L_0x1bcb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:0x1bcb, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:0x1bcd, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x1bce, code lost:
        r6.mentioned = r3;
        r6.silent = r39;
        r6.from_scheduled = r20;
        r19 = new org.telegram.messenger.MessageObject(r29, r6, r1, r44, r46, r2, r45, r49, r35);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:917:0x1bf7, code lost:
        if (r10.startsWith(r17) != false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:0x1bff, code lost:
        if (r10.startsWith(r32) == false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x1CLASSNAME, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x1CLASSNAME, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x1CLASSNAME, code lost:
        r19.isReactionPush = r1;
        r1 = new java.util.ArrayList();
        r1.add(r19);
        org.telegram.messenger.NotificationsController.getInstance(r29).processNewMessages(r1, true, true, countDownLatch);
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x1c1b, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x1c1c, code lost:
        r31 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x1c1f, code lost:
        r31 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x1CLASSNAME, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:928:0x1CLASSNAME, code lost:
        if (r8 == false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:0x1CLASSNAME, code lost:
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x1CLASSNAME, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r29);
        org.telegram.tgnet.ConnectionsManager.getInstance(r29).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x1CLASSNAME, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:932:0x1CLASSNAME, code lost:
        r1 = r0;
        r5 = r10;
        r4 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x1c3c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x1c3d, code lost:
        r31 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:935:0x1c3f, code lost:
        r10 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:0x1CLASSNAME, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:0x1CLASSNAME, code lost:
        r31 = r7;
        r10 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:0x1CLASSNAME, code lost:
        r1 = r0;
        r5 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x1CLASSNAME, code lost:
        r4 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:0x1c4c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:0x1c4d, code lost:
        r29 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:0x1CLASSNAME, code lost:
        r29 = r4;
        r31 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:0x1c5a, code lost:
        r4 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda0(r4));
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x1CLASSNAME, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:0x1CLASSNAME, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x1CLASSNAME, code lost:
        r4 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x1c6d, code lost:
        r31 = r7;
        r10 = r9;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda1(r4));
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x1c7d, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x1c7e, code lost:
        r31 = r7;
        r10 = r9;
        r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification();
        r1.popup = false;
        r1.flags = 2;
        r1.inbox_date = (int) (r55 / 1000);
        r1.message = r6.getString("message");
        r1.type = "announcement";
        r1.media = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty();
        r2 = new org.telegram.tgnet.TLRPC$TL_updates();
        r2.updates.add(r1);
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda3(r4, r2));
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).resumeNetworkMaybe();
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:0x1cc7, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x1cc8, code lost:
        r31 = r7;
        r10 = r9;
        r1 = r11.getInt("dc");
        r2 = r11.getString("addr").split(":");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:0x1cdf, code lost:
        if (r2.length == 2) goto L_0x1ce7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:957:0x1ce1, code lost:
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:0x1ce6, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x1ce7, code lost:
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).applyDatacenterAddress(r1, r2[0], java.lang.Integer.parseInt(r2[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).resumeNetworkMaybe();
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x1d04, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:0x1d05, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:962:0x1d06, code lost:
        r1 = r0;
        r5 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:0x1d08, code lost:
        r7 = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:976:0x1d2c, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r4);
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).resumeNetworkMaybe();
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:977:0x1d3c, code lost:
        onDecryptError();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:980:0x1d43, code lost:
        org.telegram.messenger.FileLog.e("error in loc_key = " + r5 + " json " + r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:?, code lost:
        return;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:976:0x1d2c  */
    /* JADX WARNING: Removed duplicated region for block: B:977:0x1d3c  */
    /* JADX WARNING: Removed duplicated region for block: B:980:0x1d43  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$processRemoteMessage$7(java.lang.String r53, java.lang.String r54, long r55) {
        /*
            r1 = r53
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
            r6 = r54
            byte[] r6 = android.util.Base64.decode(r6, r4)     // Catch:{ all -> 0x1d24 }
            org.telegram.tgnet.NativeByteBuffer r7 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x1d24 }
            int r8 = r6.length     // Catch:{ all -> 0x1d24 }
            r7.<init>((int) r8)     // Catch:{ all -> 0x1d24 }
            r7.writeBytes((byte[]) r6)     // Catch:{ all -> 0x1d24 }
            r8 = 0
            r7.position(r8)     // Catch:{ all -> 0x1d24 }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1d24 }
            if (r9 != 0) goto L_0x0046
            byte[] r9 = new byte[r4]     // Catch:{ all -> 0x1d24 }
            org.telegram.messenger.SharedConfig.pushAuthKeyId = r9     // Catch:{ all -> 0x1d24 }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1d24 }
            byte[] r9 = org.telegram.messenger.Utilities.computeSHA1((byte[]) r9)     // Catch:{ all -> 0x1d24 }
            int r10 = r9.length     // Catch:{ all -> 0x1d24 }
            int r10 = r10 - r4
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1d24 }
            java.lang.System.arraycopy(r9, r10, r11, r8, r4)     // Catch:{ all -> 0x1d24 }
        L_0x0046:
            byte[] r9 = new byte[r4]     // Catch:{ all -> 0x1d24 }
            r10 = 1
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1d24 }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1d24 }
            boolean r11 = java.util.Arrays.equals(r11, r9)     // Catch:{ all -> 0x1d24 }
            r12 = 3
            r13 = 2
            if (r11 != 0) goto L_0x0090
            onDecryptError()     // Catch:{ all -> 0x1d24 }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1d24 }
            if (r2 == 0) goto L_0x008f
            java.util.Locale r2 = java.util.Locale.US     // Catch:{ all -> 0x1d24 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x1d24 }
            r4.<init>()     // Catch:{ all -> 0x1d24 }
            r4.append(r1)     // Catch:{ all -> 0x1d24 }
            java.lang.String r1 = " DECRYPT ERROR 2 k1=%s k2=%s, key=%s"
            r4.append(r1)     // Catch:{ all -> 0x1d24 }
            java.lang.String r1 = r4.toString()     // Catch:{ all -> 0x1d24 }
            java.lang.Object[] r4 = new java.lang.Object[r12]     // Catch:{ all -> 0x1d24 }
            byte[] r6 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1d24 }
            java.lang.String r6 = org.telegram.messenger.Utilities.bytesToHex(r6)     // Catch:{ all -> 0x1d24 }
            r4[r8] = r6     // Catch:{ all -> 0x1d24 }
            java.lang.String r6 = org.telegram.messenger.Utilities.bytesToHex(r9)     // Catch:{ all -> 0x1d24 }
            r4[r10] = r6     // Catch:{ all -> 0x1d24 }
            byte[] r6 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1d24 }
            java.lang.String r6 = org.telegram.messenger.Utilities.bytesToHex(r6)     // Catch:{ all -> 0x1d24 }
            r4[r13] = r6     // Catch:{ all -> 0x1d24 }
            java.lang.String r1 = java.lang.String.format(r2, r1, r4)     // Catch:{ all -> 0x1d24 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x1d24 }
        L_0x008f:
            return
        L_0x0090:
            r9 = 16
            byte[] r9 = new byte[r9]     // Catch:{ all -> 0x1d24 }
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1d24 }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1d24 }
            org.telegram.messenger.MessageKeyData r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13)     // Catch:{ all -> 0x1d24 }
            java.nio.ByteBuffer r14 = r7.buffer     // Catch:{ all -> 0x1d24 }
            byte[] r15 = r11.aesKey     // Catch:{ all -> 0x1d24 }
            byte[] r11 = r11.aesIv     // Catch:{ all -> 0x1d24 }
            r17 = 0
            r18 = 0
            r19 = 24
            int r6 = r6.length     // Catch:{ all -> 0x1d24 }
            int r20 = r6 + -24
            r16 = r11
            org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x1d24 }
            byte[] r21 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1d24 }
            r22 = 96
            r23 = 32
            java.nio.ByteBuffer r6 = r7.buffer     // Catch:{ all -> 0x1d24 }
            r25 = 24
            int r26 = r6.limit()     // Catch:{ all -> 0x1d24 }
            r24 = r6
            byte[] r6 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26)     // Catch:{ all -> 0x1d24 }
            boolean r6 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r6, r4)     // Catch:{ all -> 0x1d24 }
            if (r6 != 0) goto L_0x00f5
            onDecryptError()     // Catch:{ all -> 0x1d24 }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1d24 }
            if (r2 == 0) goto L_0x00f4
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x1d24 }
            r2.<init>()     // Catch:{ all -> 0x1d24 }
            r2.append(r1)     // Catch:{ all -> 0x1d24 }
            java.lang.String r1 = " DECRYPT ERROR 3, key = %s"
            r2.append(r1)     // Catch:{ all -> 0x1d24 }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x1d24 }
            java.lang.Object[] r2 = new java.lang.Object[r10]     // Catch:{ all -> 0x1d24 }
            byte[] r4 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1d24 }
            java.lang.String r4 = org.telegram.messenger.Utilities.bytesToHex(r4)     // Catch:{ all -> 0x1d24 }
            r2[r8] = r4     // Catch:{ all -> 0x1d24 }
            java.lang.String r1 = java.lang.String.format(r1, r2)     // Catch:{ all -> 0x1d24 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x1d24 }
        L_0x00f4:
            return
        L_0x00f5:
            int r6 = r7.readInt32(r10)     // Catch:{ all -> 0x1d24 }
            byte[] r6 = new byte[r6]     // Catch:{ all -> 0x1d24 }
            r7.readBytes(r6, r10)     // Catch:{ all -> 0x1d24 }
            java.lang.String r7 = new java.lang.String     // Catch:{ all -> 0x1d24 }
            r7.<init>(r6)     // Catch:{ all -> 0x1d24 }
            org.json.JSONObject r6 = new org.json.JSONObject     // Catch:{ all -> 0x1d1c }
            r6.<init>(r7)     // Catch:{ all -> 0x1d1c }
            java.lang.String r9 = "loc_key"
            boolean r9 = r6.has(r9)     // Catch:{ all -> 0x1d1c }
            if (r9 == 0) goto L_0x011a
            java.lang.String r9 = "loc_key"
            java.lang.String r9 = r6.getString(r9)     // Catch:{ all -> 0x0117 }
            goto L_0x011c
        L_0x0117:
            r0 = move-exception
            goto L_0x1d1f
        L_0x011a:
            java.lang.String r9 = ""
        L_0x011c:
            java.lang.String r11 = "custom"
            java.lang.Object r11 = r6.get(r11)     // Catch:{ all -> 0x1d13 }
            boolean r11 = r11 instanceof org.json.JSONObject     // Catch:{ all -> 0x1d13 }
            if (r11 == 0) goto L_0x0132
            java.lang.String r11 = "custom"
            org.json.JSONObject r11 = r6.getJSONObject(r11)     // Catch:{ all -> 0x012d }
            goto L_0x0137
        L_0x012d:
            r0 = move-exception
            r1 = r0
            r5 = r9
            goto L_0x1d19
        L_0x0132:
            org.json.JSONObject r11 = new org.json.JSONObject     // Catch:{ all -> 0x1d13 }
            r11.<init>()     // Catch:{ all -> 0x1d13 }
        L_0x0137:
            java.lang.String r14 = "user_id"
            boolean r14 = r6.has(r14)     // Catch:{ all -> 0x1d13 }
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
            boolean r15 = r14 instanceof java.lang.Long     // Catch:{ all -> 0x1d13 }
            if (r15 == 0) goto L_0x015f
            java.lang.Long r14 = (java.lang.Long) r14     // Catch:{ all -> 0x012d }
            long r14 = r14.longValue()     // Catch:{ all -> 0x012d }
            goto L_0x0184
        L_0x015f:
            boolean r15 = r14 instanceof java.lang.Integer     // Catch:{ all -> 0x1d13 }
            if (r15 == 0) goto L_0x016b
            java.lang.Integer r14 = (java.lang.Integer) r14     // Catch:{ all -> 0x012d }
            int r14 = r14.intValue()     // Catch:{ all -> 0x012d }
        L_0x0169:
            long r14 = (long) r14
            goto L_0x0184
        L_0x016b:
            boolean r15 = r14 instanceof java.lang.String     // Catch:{ all -> 0x1d13 }
            if (r15 == 0) goto L_0x017a
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ all -> 0x012d }
            java.lang.Integer r14 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14)     // Catch:{ all -> 0x012d }
            int r14 = r14.intValue()     // Catch:{ all -> 0x012d }
            goto L_0x0169
        L_0x017a:
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1d13 }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x1d13 }
            long r14 = r14.getClientUserId()     // Catch:{ all -> 0x1d13 }
        L_0x0184:
            int r16 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1d13 }
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
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x1d0b }
            boolean r14 = r14.isClientActivated()     // Catch:{ all -> 0x1d0b }
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
            goto L_0x1d11
        L_0x01eb:
            int r14 = r9.hashCode()     // Catch:{ all -> 0x1d0b }
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
            if (r14 == 0) goto L_0x1cc8
            if (r14 == r10) goto L_0x1c7e
            if (r14 == r13) goto L_0x1c6d
            if (r14 == r12) goto L_0x1CLASSNAME
            java.lang.String r14 = "channel_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1c4c }
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
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1CLASSNAME }
            if (r14 == 0) goto L_0x025a
            java.lang.String r3 = "from_id"
            long r3 = r11.getLong(r3)     // Catch:{ all -> 0x0255 }
            r30 = r3
            goto L_0x025c
        L_0x0255:
            r0 = move-exception
            r1 = r0
            r5 = r9
            goto L_0x1CLASSNAME
        L_0x025a:
            r30 = r12
        L_0x025c:
            java.lang.String r14 = "chat_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1CLASSNAME }
            if (r14 == 0) goto L_0x027d
            java.lang.String r3 = "chat_id"
            long r3 = r11.getLong(r3)     // Catch:{ all -> 0x0273 }
            r32 = r9
            long r8 = -r3
            r51 = r3
            r3 = r8
            r8 = r51
            goto L_0x0280
        L_0x0273:
            r0 = move-exception
            r32 = r9
        L_0x0276:
            r1 = r0
            r4 = r29
            r5 = r32
            goto L_0x1d11
        L_0x027d:
            r32 = r9
            r8 = r12
        L_0x0280:
            java.lang.String r14 = "encryption_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1c3c }
            if (r14 == 0) goto L_0x0296
            java.lang.String r3 = "encryption_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x0294 }
            long r3 = (long) r3     // Catch:{ all -> 0x0294 }
            long r3 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r3)     // Catch:{ all -> 0x0294 }
            goto L_0x0296
        L_0x0294:
            r0 = move-exception
            goto L_0x0276
        L_0x0296:
            java.lang.String r14 = "schedule"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1c3c }
            if (r14 == 0) goto L_0x02a8
            java.lang.String r14 = "schedule"
            int r14 = r11.getInt(r14)     // Catch:{ all -> 0x0294 }
            if (r14 != r10) goto L_0x02a8
            r14 = 1
            goto L_0x02a9
        L_0x02a8:
            r14 = 0
        L_0x02a9:
            int r21 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r21 != 0) goto L_0x02c0
            java.lang.String r15 = "ENCRYPTED_MESSAGE"
            r10 = r32
            boolean r15 = r15.equals(r10)     // Catch:{ all -> 0x02ba }
            if (r15 == 0) goto L_0x02c2
            long r3 = org.telegram.messenger.NotificationsController.globalSecretChatId     // Catch:{ all -> 0x02ba }
            goto L_0x02c2
        L_0x02ba:
            r0 = move-exception
            goto L_0x1CLASSNAME
        L_0x02bd:
            r0 = move-exception
            goto L_0x1c3f
        L_0x02c0:
            r10 = r32
        L_0x02c2:
            int r15 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r15 == 0) goto L_0x1c1f
            java.lang.String r15 = "READ_HISTORY"
            boolean r15 = r15.equals(r10)     // Catch:{ all -> 0x1c1b }
            java.lang.String r12 = " for dialogId = "
            if (r15 == 0) goto L_0x0351
            java.lang.String r2 = "max_id"
            int r2 = r11.getInt(r2)     // Catch:{ all -> 0x02ba }
            java.util.ArrayList r11 = new java.util.ArrayList     // Catch:{ all -> 0x02ba }
            r11.<init>()     // Catch:{ all -> 0x02ba }
            boolean r13 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x02ba }
            if (r13 == 0) goto L_0x02fc
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x02ba }
            r13.<init>()     // Catch:{ all -> 0x02ba }
            r13.append(r1)     // Catch:{ all -> 0x02ba }
            java.lang.String r1 = " received read notification max_id = "
            r13.append(r1)     // Catch:{ all -> 0x02ba }
            r13.append(r2)     // Catch:{ all -> 0x02ba }
            r13.append(r12)     // Catch:{ all -> 0x02ba }
            r13.append(r3)     // Catch:{ all -> 0x02ba }
            java.lang.String r1 = r13.toString()     // Catch:{ all -> 0x02ba }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x02ba }
        L_0x02fc:
            r3 = 0
            int r1 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x0319
            org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox r1 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox     // Catch:{ all -> 0x02ba }
            r1.<init>()     // Catch:{ all -> 0x02ba }
            r1.channel_id = r5     // Catch:{ all -> 0x02ba }
            r1.max_id = r2     // Catch:{ all -> 0x02ba }
            r2 = -1
            r1.still_unread_count = r2     // Catch:{ all -> 0x0312 }
            r11.add(r1)     // Catch:{ all -> 0x02ba }
            goto L_0x033e
        L_0x0312:
            r0 = move-exception
            r1 = r0
            r5 = r10
            r4 = r29
            goto L_0x1d2a
        L_0x0319:
            org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox r1 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox     // Catch:{ all -> 0x02ba }
            r1.<init>()     // Catch:{ all -> 0x02ba }
            r3 = r30
            r5 = 0
            int r12 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r12 == 0) goto L_0x0330
            org.telegram.tgnet.TLRPC$TL_peerUser r5 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x02ba }
            r5.<init>()     // Catch:{ all -> 0x02ba }
            r1.peer = r5     // Catch:{ all -> 0x02ba }
            r5.user_id = r3     // Catch:{ all -> 0x02ba }
            goto L_0x0339
        L_0x0330:
            org.telegram.tgnet.TLRPC$TL_peerChat r3 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x02ba }
            r3.<init>()     // Catch:{ all -> 0x02ba }
            r1.peer = r3     // Catch:{ all -> 0x02ba }
            r3.chat_id = r8     // Catch:{ all -> 0x02ba }
        L_0x0339:
            r1.max_id = r2     // Catch:{ all -> 0x02ba }
            r11.add(r1)     // Catch:{ all -> 0x02ba }
        L_0x033e:
            org.telegram.messenger.MessagesController r33 = org.telegram.messenger.MessagesController.getInstance(r29)     // Catch:{ all -> 0x02ba }
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r34 = r11
            r33.processUpdateArray(r34, r35, r36, r37, r38)     // Catch:{ all -> 0x02ba }
            goto L_0x1c1f
        L_0x0351:
            r13 = r14
            r14 = r30
            r31 = r7
            java.lang.String r7 = "MESSAGE_DELETED"
            boolean r7 = r7.equals(r10)     // Catch:{ all -> 0x1CLASSNAME }
            r20 = r13
            java.lang.String r13 = "messages"
            if (r7 == 0) goto L_0x03cf
            java.lang.String r2 = r11.getString(r13)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r7 = ","
            java.lang.String[] r2 = r2.split(r7)     // Catch:{ all -> 0x1CLASSNAME }
            androidx.collection.LongSparseArray r7 = new androidx.collection.LongSparseArray     // Catch:{ all -> 0x1CLASSNAME }
            r7.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            java.util.ArrayList r8 = new java.util.ArrayList     // Catch:{ all -> 0x1CLASSNAME }
            r8.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r9 = 0
        L_0x0377:
            int r11 = r2.length     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 >= r11) goto L_0x0386
            r11 = r2[r9]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r11 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r11)     // Catch:{ all -> 0x1CLASSNAME }
            r8.add(r11)     // Catch:{ all -> 0x1CLASSNAME }
            int r9 = r9 + 1
            goto L_0x0377
        L_0x0386:
            long r13 = -r5
            r7.put(r13, r8)     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.messenger.NotificationsController r2 = org.telegram.messenger.NotificationsController.getInstance(r29)     // Catch:{ all -> 0x1CLASSNAME }
            r2.removeDeletedMessagesFromNotifications(r7)     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.messenger.MessagesController r20 = org.telegram.messenger.MessagesController.getInstance(r29)     // Catch:{ all -> 0x1CLASSNAME }
            r21 = r3
            r23 = r8
            r24 = r5
            r20.deleteMessagesByPush(r21, r23, r24)     // Catch:{ all -> 0x1CLASSNAME }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1CLASSNAME }
            if (r2 == 0) goto L_0x1CLASSNAME
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x1CLASSNAME }
            r2.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r2.append(r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = " received "
            r2.append(r1)     // Catch:{ all -> 0x1CLASSNAME }
            r2.append(r10)     // Catch:{ all -> 0x1CLASSNAME }
            r2.append(r12)     // Catch:{ all -> 0x1CLASSNAME }
            r2.append(r3)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = " mids = "
            r2.append(r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = ","
            java.lang.String r1 = android.text.TextUtils.join(r1, r8)     // Catch:{ all -> 0x1CLASSNAME }
            r2.append(r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1CLASSNAME
        L_0x03cf:
            boolean r7 = android.text.TextUtils.isEmpty(r10)     // Catch:{ all -> 0x1CLASSNAME }
            if (r7 != 0) goto L_0x1CLASSNAME
            java.lang.String r7 = "msg_id"
            boolean r7 = r11.has(r7)     // Catch:{ all -> 0x1CLASSNAME }
            if (r7 == 0) goto L_0x03e6
            java.lang.String r7 = "msg_id"
            int r7 = r11.getInt(r7)     // Catch:{ all -> 0x1CLASSNAME }
            r24 = r14
            goto L_0x03e9
        L_0x03e6:
            r24 = r14
            r7 = 0
        L_0x03e9:
            java.lang.String r14 = "random_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1CLASSNAME }
            if (r14 == 0) goto L_0x0400
            java.lang.String r14 = "random_id"
            java.lang.String r14 = r11.getString(r14)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Long r14 = org.telegram.messenger.Utilities.parseLong(r14)     // Catch:{ all -> 0x1CLASSNAME }
            long r14 = r14.longValue()     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x0402
        L_0x0400:
            r14 = 0
        L_0x0402:
            if (r7 == 0) goto L_0x0441
            r26 = r8
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r29)     // Catch:{ all -> 0x1CLASSNAME }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r8 = r8.dialogs_read_inbox_max     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Long r9 = java.lang.Long.valueOf(r3)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Object r8 = r8.get(r9)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r8 = (java.lang.Integer) r8     // Catch:{ all -> 0x1CLASSNAME }
            if (r8 != 0) goto L_0x0435
            org.telegram.messenger.MessagesStorage r8 = org.telegram.messenger.MessagesStorage.getInstance(r29)     // Catch:{ all -> 0x1CLASSNAME }
            r9 = 0
            int r8 = r8.getDialogReadMax(r9, r3)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r29)     // Catch:{ all -> 0x1CLASSNAME }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r9 = r9.dialogs_read_inbox_max     // Catch:{ all -> 0x1CLASSNAME }
            r28 = r13
            java.lang.Long r13 = java.lang.Long.valueOf(r3)     // Catch:{ all -> 0x1CLASSNAME }
            r9.put(r13, r8)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x0437
        L_0x0435:
            r28 = r13
        L_0x0437:
            int r8 = r8.intValue()     // Catch:{ all -> 0x1CLASSNAME }
            if (r7 <= r8) goto L_0x043f
        L_0x043d:
            r8 = 1
            goto L_0x0456
        L_0x043f:
            r8 = 0
            goto L_0x0456
        L_0x0441:
            r26 = r8
            r28 = r13
            r8 = 0
            int r13 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1))
            if (r13 == 0) goto L_0x043f
            org.telegram.messenger.MessagesStorage r8 = org.telegram.messenger.MessagesStorage.getInstance(r29)     // Catch:{ all -> 0x1CLASSNAME }
            boolean r8 = r8.checkMessageByRandomId(r14)     // Catch:{ all -> 0x1CLASSNAME }
            if (r8 != 0) goto L_0x043f
            goto L_0x043d
        L_0x0456:
            boolean r9 = r10.startsWith(r2)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r13 = "CHAT_REACT_"
            if (r9 != 0) goto L_0x0464
            boolean r9 = r10.startsWith(r13)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0465
        L_0x0464:
            r8 = 1
        L_0x0465:
            if (r8 == 0) goto L_0x1CLASSNAME
            java.lang.String r8 = "chat_from_id"
            r33 = r14
            r14 = 0
            long r8 = r11.optLong(r8, r14)     // Catch:{ all -> 0x1CLASSNAME }
            r32 = r13
            java.lang.String r13 = "chat_from_broadcast_id"
            r35 = r12
            long r12 = r11.optLong(r13, r14)     // Catch:{ all -> 0x1CLASSNAME }
            r36 = r12
            java.lang.String r12 = "chat_from_group_id"
            long r12 = r11.optLong(r12, r14)     // Catch:{ all -> 0x1CLASSNAME }
            int r22 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1))
            if (r22 != 0) goto L_0x048e
            int r38 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r38 == 0) goto L_0x048c
            goto L_0x048e
        L_0x048c:
            r14 = 0
            goto L_0x048f
        L_0x048e:
            r14 = 1
        L_0x048f:
            java.lang.String r15 = "mention"
            boolean r15 = r11.has(r15)     // Catch:{ all -> 0x1CLASSNAME }
            if (r15 == 0) goto L_0x04a2
            java.lang.String r15 = "mention"
            int r15 = r11.getInt(r15)     // Catch:{ all -> 0x1CLASSNAME }
            if (r15 == 0) goto L_0x04a2
            r38 = 1
            goto L_0x04a4
        L_0x04a2:
            r38 = 0
        L_0x04a4:
            java.lang.String r15 = "silent"
            boolean r15 = r11.has(r15)     // Catch:{ all -> 0x1CLASSNAME }
            if (r15 == 0) goto L_0x04b7
            java.lang.String r15 = "silent"
            int r15 = r11.getInt(r15)     // Catch:{ all -> 0x1CLASSNAME }
            if (r15 == 0) goto L_0x04b7
            r39 = 1
            goto L_0x04b9
        L_0x04b7:
            r39 = 0
        L_0x04b9:
            java.lang.String r15 = "loc_args"
            r40 = r8
            r8 = r16
            boolean r9 = r8.has(r15)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x04e1
            java.lang.String r9 = "loc_args"
            org.json.JSONArray r8 = r8.getJSONArray(r9)     // Catch:{ all -> 0x1CLASSNAME }
            int r9 = r8.length()     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String[] r15 = new java.lang.String[r9]     // Catch:{ all -> 0x1CLASSNAME }
            r42 = r12
            r12 = 0
        L_0x04d4:
            if (r12 >= r9) goto L_0x04df
            java.lang.String r13 = r8.getString(r12)     // Catch:{ all -> 0x1CLASSNAME }
            r15[r12] = r13     // Catch:{ all -> 0x1CLASSNAME }
            int r12 = r12 + 1
            goto L_0x04d4
        L_0x04df:
            r8 = 0
            goto L_0x04e5
        L_0x04e1:
            r42 = r12
            r8 = 0
            r15 = 0
        L_0x04e5:
            r9 = r15[r8]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r8 = "edit_date"
            boolean r8 = r11.has(r8)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r11 = "CHAT_"
            boolean r11 = r10.startsWith(r11)     // Catch:{ all -> 0x1CLASSNAME }
            if (r11 == 0) goto L_0x0529
            boolean r11 = org.telegram.messenger.UserObject.isReplyUser((long) r3)     // Catch:{ all -> 0x1CLASSNAME }
            if (r11 == 0) goto L_0x0513
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x1CLASSNAME }
            r11.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r11.append(r9)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r9 = " @ "
            r11.append(r9)     // Catch:{ all -> 0x1CLASSNAME }
            r9 = 1
            r12 = r15[r9]     // Catch:{ all -> 0x1CLASSNAME }
            r11.append(r12)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r9 = r11.toString()     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x054b
        L_0x0513:
            r11 = 0
            int r13 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r13 == 0) goto L_0x051b
            r11 = 1
            goto L_0x051c
        L_0x051b:
            r11 = 0
        L_0x051c:
            r12 = 1
            r13 = r15[r12]     // Catch:{ all -> 0x1CLASSNAME }
            r12 = 0
            r16 = 0
            r51 = r11
            r11 = r9
            r9 = r13
            r13 = r51
            goto L_0x0550
        L_0x0529:
            java.lang.String r11 = "PINNED_"
            boolean r11 = r10.startsWith(r11)     // Catch:{ all -> 0x1CLASSNAME }
            if (r11 == 0) goto L_0x0540
            r11 = 0
            int r13 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r13 == 0) goto L_0x0539
            r11 = 1
            goto L_0x053a
        L_0x0539:
            r11 = 0
        L_0x053a:
            r13 = r11
            r11 = 0
            r12 = 0
            r16 = 1
            goto L_0x0550
        L_0x0540:
            java.lang.String r11 = "CHANNEL_"
            boolean r11 = r10.startsWith(r11)     // Catch:{ all -> 0x1CLASSNAME }
            if (r11 == 0) goto L_0x054b
            r11 = 0
            r12 = 1
            goto L_0x054d
        L_0x054b:
            r11 = 0
            r12 = 0
        L_0x054d:
            r13 = 0
            r16 = 0
        L_0x0550:
            boolean r44 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1CLASSNAME }
            if (r44 == 0) goto L_0x057e
            r44 = r9
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x1CLASSNAME }
            r9.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r9.append(r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = " received message notification "
            r9.append(r1)     // Catch:{ all -> 0x1CLASSNAME }
            r9.append(r10)     // Catch:{ all -> 0x1CLASSNAME }
            r1 = r35
            r9.append(r1)     // Catch:{ all -> 0x1CLASSNAME }
            r9.append(r3)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = " mid = "
            r9.append(r1)     // Catch:{ all -> 0x1CLASSNAME }
            r9.append(r7)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = r9.toString()     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x0580
        L_0x057e:
            r44 = r9
        L_0x0580:
            boolean r1 = r10.startsWith(r2)     // Catch:{ all -> 0x1CLASSNAME }
            if (r1 != 0) goto L_0x1b11
            r1 = r32
            boolean r9 = r10.startsWith(r1)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0592
            r32 = r1
            goto L_0x1b11
        L_0x0592:
            int r9 = r10.hashCode()     // Catch:{ all -> 0x1CLASSNAME }
            switch(r9) {
                case -2100047043: goto L_0x0aee;
                case -2091498420: goto L_0x0ae3;
                case -2053872415: goto L_0x0ad8;
                case -2039746363: goto L_0x0acd;
                case -2023218804: goto L_0x0ac2;
                case -1979538588: goto L_0x0ab7;
                case -1979536003: goto L_0x0aac;
                case -1979535888: goto L_0x0aa1;
                case -1969004705: goto L_0x0a96;
                case -1946699248: goto L_0x0a8b;
                case -1717283471: goto L_0x0a7f;
                case -1646640058: goto L_0x0a73;
                case -1528047021: goto L_0x0a67;
                case -1507149394: goto L_0x0a5a;
                case -1493579426: goto L_0x0a4e;
                case -1482481933: goto L_0x0a42;
                case -1480102982: goto L_0x0a35;
                case -1478041834: goto L_0x0a29;
                case -1474543101: goto L_0x0a1e;
                case -1465695932: goto L_0x0a12;
                case -1374906292: goto L_0x0a06;
                case -1372940586: goto L_0x09fa;
                case -1264245338: goto L_0x09ee;
                case -1236154001: goto L_0x09e2;
                case -1236086700: goto L_0x09d6;
                case -1236077786: goto L_0x09ca;
                case -1235796237: goto L_0x09be;
                case -1235760759: goto L_0x09b2;
                case -1235686303: goto L_0x09a5;
                case -1198046100: goto L_0x099a;
                case -1124254527: goto L_0x098e;
                case -1085137927: goto L_0x0982;
                case -1084856378: goto L_0x0976;
                case -1084820900: goto L_0x096a;
                case -1084746444: goto L_0x095e;
                case -819729482: goto L_0x0952;
                case -772141857: goto L_0x0946;
                case -638310039: goto L_0x093a;
                case -590403924: goto L_0x092e;
                case -589196239: goto L_0x0922;
                case -589193654: goto L_0x0916;
                case -589193539: goto L_0x090a;
                case -440169325: goto L_0x08fe;
                case -412748110: goto L_0x08f2;
                case -228518075: goto L_0x08e6;
                case -213586509: goto L_0x08da;
                case -115582002: goto L_0x08ce;
                case -112621464: goto L_0x08c2;
                case -108522133: goto L_0x08b6;
                case -107572034: goto L_0x08a8;
                case -40534265: goto L_0x089c;
                case 52369421: goto L_0x0890;
                case 65254746: goto L_0x0884;
                case 141040782: goto L_0x0878;
                case 202550149: goto L_0x086c;
                case 309993049: goto L_0x0860;
                case 309995634: goto L_0x0854;
                case 309995749: goto L_0x0848;
                case 320532812: goto L_0x083c;
                case 328933854: goto L_0x0830;
                case 331340546: goto L_0x0824;
                case 342406591: goto L_0x0818;
                case 344816990: goto L_0x080c;
                case 346878138: goto L_0x0800;
                case 350376871: goto L_0x07f4;
                case 608430149: goto L_0x07e8;
                case 615714517: goto L_0x07dd;
                case 715508879: goto L_0x07d1;
                case 728985323: goto L_0x07c5;
                case 731046471: goto L_0x07b9;
                case 734545204: goto L_0x07ad;
                case 802032552: goto L_0x07a1;
                case 991498806: goto L_0x0795;
                case 1007364121: goto L_0x0789;
                case 1019850010: goto L_0x077d;
                case 1019917311: goto L_0x0771;
                case 1019926225: goto L_0x0765;
                case 1020207774: goto L_0x0759;
                case 1020243252: goto L_0x074d;
                case 1020317708: goto L_0x0741;
                case 1060282259: goto L_0x0735;
                case 1060349560: goto L_0x0729;
                case 1060358474: goto L_0x071d;
                case 1060640023: goto L_0x0711;
                case 1060675501: goto L_0x0705;
                case 1060749957: goto L_0x06f8;
                case 1073049781: goto L_0x06ec;
                case 1078101399: goto L_0x06e0;
                case 1110103437: goto L_0x06d4;
                case 1160762272: goto L_0x06c8;
                case 1172918249: goto L_0x06bc;
                case 1234591620: goto L_0x06b0;
                case 1281128640: goto L_0x06a4;
                case 1281131225: goto L_0x0698;
                case 1281131340: goto L_0x068c;
                case 1310789062: goto L_0x067f;
                case 1333118583: goto L_0x0673;
                case 1361447897: goto L_0x0667;
                case 1498266155: goto L_0x065b;
                case 1533804208: goto L_0x064f;
                case 1540131626: goto L_0x0643;
                case 1547988151: goto L_0x0637;
                case 1561464595: goto L_0x062b;
                case 1563525743: goto L_0x061f;
                case 1567024476: goto L_0x0613;
                case 1810705077: goto L_0x0607;
                case 1815177512: goto L_0x05fb;
                case 1954774321: goto L_0x05ef;
                case 1963241394: goto L_0x05e3;
                case 2014789757: goto L_0x05d7;
                case 2022049433: goto L_0x05cb;
                case 2034984710: goto L_0x05bf;
                case 2048733346: goto L_0x05b3;
                case 2099392181: goto L_0x05a7;
                case 2140162142: goto L_0x059b;
                default: goto L_0x0599;
            }     // Catch:{ all -> 0x1CLASSNAME }
        L_0x0599:
            goto L_0x0afb
        L_0x059b:
            java.lang.String r9 = "CHAT_MESSAGE_GEOLIVE"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 61
            goto L_0x0af8
        L_0x05a7:
            java.lang.String r9 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 44
            goto L_0x0af8
        L_0x05b3:
            java.lang.String r9 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 29
            goto L_0x0af8
        L_0x05bf:
            java.lang.String r9 = "CHANNEL_MESSAGE_PLAYLIST"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 46
            goto L_0x0af8
        L_0x05cb:
            java.lang.String r9 = "PINNED_CONTACT"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 95
            goto L_0x0af8
        L_0x05d7:
            java.lang.String r9 = "CHAT_PHOTO_EDITED"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 69
            goto L_0x0af8
        L_0x05e3:
            java.lang.String r9 = "LOCKED_MESSAGE"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 109(0x6d, float:1.53E-43)
            goto L_0x0af8
        L_0x05ef:
            java.lang.String r9 = "CHAT_MESSAGE_PLAYLIST"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 84
            goto L_0x0af8
        L_0x05fb:
            java.lang.String r9 = "CHANNEL_MESSAGES"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 48
            goto L_0x0af8
        L_0x0607:
            java.lang.String r9 = "MESSAGE_INVOICE"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 22
            goto L_0x0af8
        L_0x0613:
            java.lang.String r9 = "CHAT_MESSAGE_VIDEO"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 52
            goto L_0x0af8
        L_0x061f:
            java.lang.String r9 = "CHAT_MESSAGE_ROUND"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 53
            goto L_0x0af8
        L_0x062b:
            java.lang.String r9 = "CHAT_MESSAGE_PHOTO"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 51
            goto L_0x0af8
        L_0x0637:
            java.lang.String r9 = "CHAT_MESSAGE_AUDIO"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 56
            goto L_0x0af8
        L_0x0643:
            java.lang.String r9 = "MESSAGE_PLAYLIST"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 26
            goto L_0x0af8
        L_0x064f:
            java.lang.String r9 = "MESSAGE_VIDEOS"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 25
            goto L_0x0af8
        L_0x065b:
            java.lang.String r9 = "PHONE_CALL_MISSED"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 114(0x72, float:1.6E-43)
            goto L_0x0af8
        L_0x0667:
            java.lang.String r9 = "MESSAGE_PHOTOS"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 24
            goto L_0x0af8
        L_0x0673:
            java.lang.String r9 = "CHAT_MESSAGE_VIDEOS"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 83
            goto L_0x0af8
        L_0x067f:
            java.lang.String r9 = "MESSAGE_NOTEXT"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r32 = r1
            r9 = 3
            goto L_0x0afe
        L_0x068c:
            java.lang.String r9 = "MESSAGE_GIF"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 18
            goto L_0x0af8
        L_0x0698:
            java.lang.String r9 = "MESSAGE_GEO"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 16
            goto L_0x0af8
        L_0x06a4:
            java.lang.String r9 = "MESSAGE_DOC"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 10
            goto L_0x0af8
        L_0x06b0:
            java.lang.String r9 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 64
            goto L_0x0af8
        L_0x06bc:
            java.lang.String r9 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 40
            goto L_0x0af8
        L_0x06c8:
            java.lang.String r9 = "CHAT_MESSAGE_PHOTOS"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 82
            goto L_0x0af8
        L_0x06d4:
            java.lang.String r9 = "CHAT_MESSAGE_NOTEXT"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 50
            goto L_0x0af8
        L_0x06e0:
            java.lang.String r9 = "CHAT_TITLE_EDITED"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 68
            goto L_0x0af8
        L_0x06ec:
            java.lang.String r9 = "PINNED_NOTEXT"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 88
            goto L_0x0af8
        L_0x06f8:
            java.lang.String r9 = "MESSAGE_TEXT"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r32 = r1
            r9 = 1
            goto L_0x0afe
        L_0x0705:
            java.lang.String r9 = "MESSAGE_QUIZ"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 14
            goto L_0x0af8
        L_0x0711:
            java.lang.String r9 = "MESSAGE_POLL"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 15
            goto L_0x0af8
        L_0x071d:
            java.lang.String r9 = "MESSAGE_GAME"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 19
            goto L_0x0af8
        L_0x0729:
            java.lang.String r9 = "MESSAGE_FWDS"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 23
            goto L_0x0af8
        L_0x0735:
            java.lang.String r9 = "MESSAGE_DOCS"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 27
            goto L_0x0af8
        L_0x0741:
            java.lang.String r9 = "CHAT_MESSAGE_TEXT"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 49
            goto L_0x0af8
        L_0x074d:
            java.lang.String r9 = "CHAT_MESSAGE_QUIZ"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 58
            goto L_0x0af8
        L_0x0759:
            java.lang.String r9 = "CHAT_MESSAGE_POLL"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 59
            goto L_0x0af8
        L_0x0765:
            java.lang.String r9 = "CHAT_MESSAGE_GAME"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 63
            goto L_0x0af8
        L_0x0771:
            java.lang.String r9 = "CHAT_MESSAGE_FWDS"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 81
            goto L_0x0af8
        L_0x077d:
            java.lang.String r9 = "CHAT_MESSAGE_DOCS"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 85
            goto L_0x0af8
        L_0x0789:
            java.lang.String r9 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 21
            goto L_0x0af8
        L_0x0795:
            java.lang.String r9 = "PINNED_GEOLIVE"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 99
            goto L_0x0af8
        L_0x07a1:
            java.lang.String r9 = "MESSAGE_CONTACT"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 13
            goto L_0x0af8
        L_0x07ad:
            java.lang.String r9 = "PINNED_VIDEO"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 90
            goto L_0x0af8
        L_0x07b9:
            java.lang.String r9 = "PINNED_ROUND"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 91
            goto L_0x0af8
        L_0x07c5:
            java.lang.String r9 = "PINNED_PHOTO"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 89
            goto L_0x0af8
        L_0x07d1:
            java.lang.String r9 = "PINNED_AUDIO"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 94
            goto L_0x0af8
        L_0x07dd:
            java.lang.String r9 = "MESSAGE_PHOTO_SECRET"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 5
            goto L_0x0af8
        L_0x07e8:
            java.lang.String r9 = "CHAT_VOICECHAT_INVITE_YOU"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 74
            goto L_0x0af8
        L_0x07f4:
            java.lang.String r9 = "CHANNEL_MESSAGE_VIDEO"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 31
            goto L_0x0af8
        L_0x0800:
            java.lang.String r9 = "CHANNEL_MESSAGE_ROUND"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 32
            goto L_0x0af8
        L_0x080c:
            java.lang.String r9 = "CHANNEL_MESSAGE_PHOTO"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 30
            goto L_0x0af8
        L_0x0818:
            java.lang.String r9 = "CHAT_VOICECHAT_END"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 73
            goto L_0x0af8
        L_0x0824:
            java.lang.String r9 = "CHANNEL_MESSAGE_AUDIO"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 35
            goto L_0x0af8
        L_0x0830:
            java.lang.String r9 = "CHAT_MESSAGE_STICKER"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 55
            goto L_0x0af8
        L_0x083c:
            java.lang.String r9 = "MESSAGES"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 28
            goto L_0x0af8
        L_0x0848:
            java.lang.String r9 = "CHAT_MESSAGE_GIF"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 62
            goto L_0x0af8
        L_0x0854:
            java.lang.String r9 = "CHAT_MESSAGE_GEO"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 60
            goto L_0x0af8
        L_0x0860:
            java.lang.String r9 = "CHAT_MESSAGE_DOC"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 54
            goto L_0x0af8
        L_0x086c:
            java.lang.String r9 = "CHAT_VOICECHAT_INVITE"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 72
            goto L_0x0af8
        L_0x0878:
            java.lang.String r9 = "CHAT_LEFT"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 77
            goto L_0x0af8
        L_0x0884:
            java.lang.String r9 = "CHAT_ADD_YOU"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 67
            goto L_0x0af8
        L_0x0890:
            java.lang.String r9 = "REACT_TEXT"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 105(0x69, float:1.47E-43)
            goto L_0x0af8
        L_0x089c:
            java.lang.String r9 = "CHAT_DELETE_MEMBER"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 75
            goto L_0x0af8
        L_0x08a8:
            java.lang.String r9 = "MESSAGE_SCREENSHOT"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r32 = r1
            r9 = 8
            goto L_0x0afe
        L_0x08b6:
            java.lang.String r9 = "AUTH_REGION"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 108(0x6c, float:1.51E-43)
            goto L_0x0af8
        L_0x08c2:
            java.lang.String r9 = "CONTACT_JOINED"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 106(0x6a, float:1.49E-43)
            goto L_0x0af8
        L_0x08ce:
            java.lang.String r9 = "CHAT_MESSAGE_INVOICE"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 65
            goto L_0x0af8
        L_0x08da:
            java.lang.String r9 = "ENCRYPTION_REQUEST"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 110(0x6e, float:1.54E-43)
            goto L_0x0af8
        L_0x08e6:
            java.lang.String r9 = "MESSAGE_GEOLIVE"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 17
            goto L_0x0af8
        L_0x08f2:
            java.lang.String r9 = "CHAT_DELETE_YOU"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 76
            goto L_0x0af8
        L_0x08fe:
            java.lang.String r9 = "AUTH_UNKNOWN"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 107(0x6b, float:1.5E-43)
            goto L_0x0af8
        L_0x090a:
            java.lang.String r9 = "PINNED_GIF"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 103(0x67, float:1.44E-43)
            goto L_0x0af8
        L_0x0916:
            java.lang.String r9 = "PINNED_GEO"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 98
            goto L_0x0af8
        L_0x0922:
            java.lang.String r9 = "PINNED_DOC"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 92
            goto L_0x0af8
        L_0x092e:
            java.lang.String r9 = "PINNED_GAME_SCORE"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 101(0x65, float:1.42E-43)
            goto L_0x0af8
        L_0x093a:
            java.lang.String r9 = "CHANNEL_MESSAGE_STICKER"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 34
            goto L_0x0af8
        L_0x0946:
            java.lang.String r9 = "PHONE_CALL_REQUEST"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 112(0x70, float:1.57E-43)
            goto L_0x0af8
        L_0x0952:
            java.lang.String r9 = "PINNED_STICKER"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 93
            goto L_0x0af8
        L_0x095e:
            java.lang.String r9 = "PINNED_TEXT"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 87
            goto L_0x0af8
        L_0x096a:
            java.lang.String r9 = "PINNED_QUIZ"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 96
            goto L_0x0af8
        L_0x0976:
            java.lang.String r9 = "PINNED_POLL"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 97
            goto L_0x0af8
        L_0x0982:
            java.lang.String r9 = "PINNED_GAME"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 100
            goto L_0x0af8
        L_0x098e:
            java.lang.String r9 = "CHAT_MESSAGE_CONTACT"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 57
            goto L_0x0af8
        L_0x099a:
            java.lang.String r9 = "MESSAGE_VIDEO_SECRET"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 7
            goto L_0x0af8
        L_0x09a5:
            java.lang.String r9 = "CHANNEL_MESSAGE_TEXT"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r32 = r1
            r9 = 2
            goto L_0x0afe
        L_0x09b2:
            java.lang.String r9 = "CHANNEL_MESSAGE_QUIZ"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 37
            goto L_0x0af8
        L_0x09be:
            java.lang.String r9 = "CHANNEL_MESSAGE_POLL"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 38
            goto L_0x0af8
        L_0x09ca:
            java.lang.String r9 = "CHANNEL_MESSAGE_GAME"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 42
            goto L_0x0af8
        L_0x09d6:
            java.lang.String r9 = "CHANNEL_MESSAGE_FWDS"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 43
            goto L_0x0af8
        L_0x09e2:
            java.lang.String r9 = "CHANNEL_MESSAGE_DOCS"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 47
            goto L_0x0af8
        L_0x09ee:
            java.lang.String r9 = "PINNED_INVOICE"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 102(0x66, float:1.43E-43)
            goto L_0x0af8
        L_0x09fa:
            java.lang.String r9 = "CHAT_RETURNED"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 78
            goto L_0x0af8
        L_0x0a06:
            java.lang.String r9 = "ENCRYPTED_MESSAGE"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 104(0x68, float:1.46E-43)
            goto L_0x0af8
        L_0x0a12:
            java.lang.String r9 = "ENCRYPTION_ACCEPT"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 111(0x6f, float:1.56E-43)
            goto L_0x0af8
        L_0x0a1e:
            java.lang.String r9 = "MESSAGE_VIDEO"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 6
            goto L_0x0af8
        L_0x0a29:
            java.lang.String r9 = "MESSAGE_ROUND"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 9
            goto L_0x0af8
        L_0x0a35:
            java.lang.String r9 = "MESSAGE_PHOTO"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r32 = r1
            r9 = 4
            goto L_0x0afe
        L_0x0a42:
            java.lang.String r9 = "MESSAGE_MUTED"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 113(0x71, float:1.58E-43)
            goto L_0x0af8
        L_0x0a4e:
            java.lang.String r9 = "MESSAGE_AUDIO"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 12
            goto L_0x0af8
        L_0x0a5a:
            java.lang.String r9 = "MESSAGE_RECURRING_PAY"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r32 = r1
            r9 = 0
            goto L_0x0afe
        L_0x0a67:
            java.lang.String r9 = "CHAT_MESSAGES"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 86
            goto L_0x0af8
        L_0x0a73:
            java.lang.String r9 = "CHAT_VOICECHAT_START"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 71
            goto L_0x0af8
        L_0x0a7f:
            java.lang.String r9 = "CHAT_REQ_JOINED"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 80
            goto L_0x0af8
        L_0x0a8b:
            java.lang.String r9 = "CHAT_JOINED"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 79
            goto L_0x0af8
        L_0x0a96:
            java.lang.String r9 = "CHAT_ADD_MEMBER"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 70
            goto L_0x0af8
        L_0x0aa1:
            java.lang.String r9 = "CHANNEL_MESSAGE_GIF"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 41
            goto L_0x0af8
        L_0x0aac:
            java.lang.String r9 = "CHANNEL_MESSAGE_GEO"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 39
            goto L_0x0af8
        L_0x0ab7:
            java.lang.String r9 = "CHANNEL_MESSAGE_DOC"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 33
            goto L_0x0af8
        L_0x0ac2:
            java.lang.String r9 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 45
            goto L_0x0af8
        L_0x0acd:
            java.lang.String r9 = "MESSAGE_STICKER"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 11
            goto L_0x0af8
        L_0x0ad8:
            java.lang.String r9 = "CHAT_CREATED"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 66
            goto L_0x0af8
        L_0x0ae3:
            java.lang.String r9 = "CHANNEL_MESSAGE_CONTACT"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 36
            goto L_0x0af8
        L_0x0aee:
            java.lang.String r9 = "MESSAGE_GAME_SCORE"
            boolean r9 = r10.equals(r9)     // Catch:{ all -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0afb
            r9 = 20
        L_0x0af8:
            r32 = r1
            goto L_0x0afe
        L_0x0afb:
            r32 = r1
            r9 = -1
        L_0x0afe:
            java.lang.String r1 = "Files"
            r17 = r2
            java.lang.String r2 = "MusicFiles"
            r35 = r8
            java.lang.String r8 = "Videos"
            r45 = r12
            java.lang.String r12 = "Photos"
            r46 = r11
            java.lang.String r11 = " "
            r47 = r5
            java.lang.String r5 = "NotificationGroupFew"
            java.lang.String r6 = "NotificationMessageFew"
            r49 = r13
            java.lang.String r13 = "ChannelMessageFew"
            r50 = r7
            java.lang.String r7 = "AttachSticker"
            switch(r9) {
                case 0: goto L_0x1adb;
                case 1: goto L_0x1abd;
                case 2: goto L_0x1abd;
                case 3: goto L_0x1aa4;
                case 4: goto L_0x1a8b;
                case 5: goto L_0x1a72;
                case 6: goto L_0x1a59;
                case 7: goto L_0x1a3f;
                case 8: goto L_0x1a2c;
                case 9: goto L_0x1a12;
                case 10: goto L_0x19f8;
                case 11: goto L_0x19a3;
                case 12: goto L_0x1989;
                case 13: goto L_0x196a;
                case 14: goto L_0x194b;
                case 15: goto L_0x192c;
                case 16: goto L_0x1912;
                case 17: goto L_0x18f8;
                case 18: goto L_0x18de;
                case 19: goto L_0x18bf;
                case 20: goto L_0x18a3;
                case 21: goto L_0x18a3;
                case 22: goto L_0x1884;
                case 23: goto L_0x185d;
                case 24: goto L_0x183a;
                case 25: goto L_0x1818;
                case 26: goto L_0x17f6;
                case 27: goto L_0x17d4;
                case 28: goto L_0x17c1;
                case 29: goto L_0x17a7;
                case 30: goto L_0x178d;
                case 31: goto L_0x1773;
                case 32: goto L_0x1759;
                case 33: goto L_0x173f;
                case 34: goto L_0x16ea;
                case 35: goto L_0x16d0;
                case 36: goto L_0x16b1;
                case 37: goto L_0x1692;
                case 38: goto L_0x1673;
                case 39: goto L_0x1659;
                case 40: goto L_0x163f;
                case 41: goto L_0x1625;
                case 42: goto L_0x160b;
                case 43: goto L_0x15e1;
                case 44: goto L_0x15be;
                case 45: goto L_0x159b;
                case 46: goto L_0x1578;
                case 47: goto L_0x1555;
                case 48: goto L_0x1543;
                case 49: goto L_0x1525;
                case 50: goto L_0x1506;
                case 51: goto L_0x14e7;
                case 52: goto L_0x14c8;
                case 53: goto L_0x14a9;
                case 54: goto L_0x148a;
                case 55: goto L_0x1417;
                case 56: goto L_0x13f8;
                case 57: goto L_0x13d4;
                case 58: goto L_0x13b0;
                case 59: goto L_0x138c;
                case 60: goto L_0x136d;
                case 61: goto L_0x134e;
                case 62: goto L_0x132f;
                case 63: goto L_0x130b;
                case 64: goto L_0x12ea;
                case 65: goto L_0x12c6;
                case 66: goto L_0x12af;
                case 67: goto L_0x12af;
                case 68: goto L_0x1298;
                case 69: goto L_0x1281;
                case 70: goto L_0x1265;
                case 71: goto L_0x124e;
                case 72: goto L_0x1232;
                case 73: goto L_0x121b;
                case 74: goto L_0x1204;
                case 75: goto L_0x11ed;
                case 76: goto L_0x11d6;
                case 77: goto L_0x11bf;
                case 78: goto L_0x11a8;
                case 79: goto L_0x1191;
                case 80: goto L_0x117a;
                case 81: goto L_0x114d;
                case 82: goto L_0x1124;
                case 83: goto L_0x10fb;
                case 84: goto L_0x10d2;
                case 85: goto L_0x10a9;
                case 86: goto L_0x1092;
                case 87: goto L_0x1040;
                case 88: goto L_0x0ff8;
                case 89: goto L_0x0fb0;
                case 90: goto L_0x0var_;
                case 91: goto L_0x0var_;
                case 92: goto L_0x0ed8;
                case 93: goto L_0x0e27;
                case 94: goto L_0x0ddf;
                case 95: goto L_0x0d8d;
                case 96: goto L_0x0d3b;
                case 97: goto L_0x0ce9;
                case 98: goto L_0x0ca1;
                case 99: goto L_0x0CLASSNAME;
                case 100: goto L_0x0CLASSNAME;
                case 101: goto L_0x0bc9;
                case 102: goto L_0x0b81;
                case 103: goto L_0x0b39;
                case 104: goto L_0x0b25;
                case 105: goto L_0x1b0f;
                case 106: goto L_0x1b0f;
                case 107: goto L_0x1b0f;
                case 108: goto L_0x1b0f;
                case 109: goto L_0x1b0f;
                case 110: goto L_0x1b0f;
                case 111: goto L_0x1b0f;
                case 112: goto L_0x1b0f;
                case 113: goto L_0x1b0f;
                case 114: goto L_0x1b0f;
                default: goto L_0x0b21;
            }
        L_0x0b21:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1af9
        L_0x0b25:
            java.lang.String r1 = "YouHaveNewMessage"
            int r2 = org.telegram.messenger.R.string.YouHaveNewMessage     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r2 = "SecretChatName"
            int r5 = org.telegram.messenger.R.string.SecretChatName     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            r44 = r2
            goto L_0x17d1
        L_0x0b39:
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0b56
            java.lang.String r1 = "NotificationActionPinnedGifUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGifUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0b56:
            if (r14 == 0) goto L_0x0b6f
            java.lang.String r2 = "NotificationActionPinnedGif"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGif     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0b6f:
            java.lang.String r1 = "NotificationActionPinnedGifChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGifChannel     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0b81:
            r1 = 0
            int r6 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x0b9e
            java.lang.String r1 = "NotificationActionPinnedInvoiceUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedInvoiceUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0b9e:
            if (r14 == 0) goto L_0x0bb7
            java.lang.String r2 = "NotificationActionPinnedInvoice"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedInvoice     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0bb7:
            java.lang.String r1 = "NotificationActionPinnedInvoiceChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedInvoiceChannel     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0bc9:
            r1 = 0
            int r6 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x0be6
            java.lang.String r1 = "NotificationActionPinnedGameScoreUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGameScoreUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0be6:
            if (r14 == 0) goto L_0x0bff
            java.lang.String r2 = "NotificationActionPinnedGameScore"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGameScore     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0bff:
            java.lang.String r1 = "NotificationActionPinnedGameScoreChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGameScoreChannel     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0CLASSNAME:
            r1 = 0
            int r6 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x0c2e
            java.lang.String r1 = "NotificationActionPinnedGameUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGameUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0c2e:
            if (r14 == 0) goto L_0x0CLASSNAME
            java.lang.String r2 = "NotificationActionPinnedGame"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGame     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0CLASSNAME:
            java.lang.String r1 = "NotificationActionPinnedGameChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGameChannel     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0CLASSNAME:
            r1 = 0
            int r6 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGeoLiveUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0CLASSNAME:
            if (r14 == 0) goto L_0x0c8f
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGeoLive     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0c8f:
            java.lang.String r1 = "NotificationActionPinnedGeoLiveChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveChannel     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0ca1:
            r1 = 0
            int r6 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x0cbe
            java.lang.String r1 = "NotificationActionPinnedGeoUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGeoUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0cbe:
            if (r14 == 0) goto L_0x0cd7
            java.lang.String r2 = "NotificationActionPinnedGeo"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGeo     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0cd7:
            java.lang.String r1 = "NotificationActionPinnedGeoChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGeoChannel     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0ce9:
            r1 = 0
            int r6 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x0d06
            java.lang.String r1 = "NotificationActionPinnedPollUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedPollUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0d06:
            if (r14 == 0) goto L_0x0d24
            java.lang.String r2 = "NotificationActionPinnedPoll2"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedPoll2     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 1
            r6[r8] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r7 = r15[r8]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0d24:
            java.lang.String r2 = "NotificationActionPinnedPollChannel2"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedPollChannel2     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0d3b:
            r1 = 0
            int r6 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x0d58
            java.lang.String r1 = "NotificationActionPinnedQuizUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedQuizUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0d58:
            if (r14 == 0) goto L_0x0d76
            java.lang.String r2 = "NotificationActionPinnedQuiz2"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedQuiz2     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 1
            r6[r8] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r7 = r15[r8]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0d76:
            java.lang.String r2 = "NotificationActionPinnedQuizChannel2"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedQuizChannel2     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0d8d:
            r1 = 0
            int r6 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x0daa
            java.lang.String r1 = "NotificationActionPinnedContactUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedContactUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0daa:
            if (r14 == 0) goto L_0x0dc8
            java.lang.String r2 = "NotificationActionPinnedContact2"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedContact2     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 1
            r6[r8] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r7 = r15[r8]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0dc8:
            java.lang.String r2 = "NotificationActionPinnedContactChannel2"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedContactChannel2     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0ddf:
            r1 = 0
            int r6 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x0dfc
            java.lang.String r1 = "NotificationActionPinnedVoiceUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedVoiceUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0dfc:
            if (r14 == 0) goto L_0x0e15
            java.lang.String r2 = "NotificationActionPinnedVoice"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedVoice     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0e15:
            java.lang.String r1 = "NotificationActionPinnedVoiceChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedVoiceChannel     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0e27:
            r1 = 0
            int r6 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x0e62
            int r1 = r15.length     // Catch:{ all -> 0x1CLASSNAME }
            r2 = 1
            if (r1 <= r2) goto L_0x0e50
            r1 = r15[r2]     // Catch:{ all -> 0x1CLASSNAME }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x1CLASSNAME }
            if (r1 != 0) goto L_0x0e50
            java.lang.String r1 = "NotificationActionPinnedStickerEmojiUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0e50:
            java.lang.String r1 = "NotificationActionPinnedStickerUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedStickerUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0e62:
            if (r14 == 0) goto L_0x0ea3
            int r2 = r15.length     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            if (r2 <= r1) goto L_0x0e8c
            r2 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x1CLASSNAME }
            if (r2 != 0) goto L_0x0e8c
            java.lang.String r2 = "NotificationActionPinnedStickerEmoji"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedStickerEmoji     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 1
            r6[r8] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r7 = r15[r8]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0e8c:
            java.lang.String r2 = "NotificationActionPinnedSticker"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedSticker     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0ea3:
            int r2 = r15.length     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            if (r2 <= r5) goto L_0x0ec6
            r2 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x1CLASSNAME }
            if (r2 != 0) goto L_0x0ec6
            java.lang.String r2 = "NotificationActionPinnedStickerEmojiChannel"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiChannel     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0ec6:
            java.lang.String r1 = "NotificationActionPinnedStickerChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedStickerChannel     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0ed8:
            r1 = 0
            int r6 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x0ef5
            java.lang.String r1 = "NotificationActionPinnedFileUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedFileUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0ef5:
            if (r14 == 0) goto L_0x0f0e
            java.lang.String r2 = "NotificationActionPinnedFile"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedFile     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0f0e:
            java.lang.String r1 = "NotificationActionPinnedFileChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedFileChannel     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0var_:
            r1 = 0
            int r6 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x0f3d
            java.lang.String r1 = "NotificationActionPinnedRoundUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedRoundUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0f3d:
            if (r14 == 0) goto L_0x0var_
            java.lang.String r2 = "NotificationActionPinnedRound"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedRound     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0var_:
            java.lang.String r1 = "NotificationActionPinnedRoundChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedRoundChannel     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0var_:
            r1 = 0
            int r6 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedVideoUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedVideoUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0var_:
            if (r14 == 0) goto L_0x0f9e
            java.lang.String r2 = "NotificationActionPinnedVideo"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedVideo     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0f9e:
            java.lang.String r1 = "NotificationActionPinnedVideoChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedVideoChannel     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0fb0:
            r1 = 0
            int r6 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x0fcd
            java.lang.String r1 = "NotificationActionPinnedPhotoUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedPhotoUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0fcd:
            if (r14 == 0) goto L_0x0fe6
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedPhoto     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0fe6:
            java.lang.String r1 = "NotificationActionPinnedPhotoChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedPhotoChannel     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x0ff8:
            r1 = 0
            int r6 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x1015
            java.lang.String r1 = "NotificationActionPinnedNoTextUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedNoTextUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x1015:
            if (r14 == 0) goto L_0x102e
            java.lang.String r2 = "NotificationActionPinnedNoText"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedNoText     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x102e:
            java.lang.String r1 = "NotificationActionPinnedNoTextChannel"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedNoTextChannel     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x1040:
            r1 = 0
            int r6 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x105d
            java.lang.String r1 = "NotificationActionPinnedTextUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedTextUser     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r5[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x105d:
            if (r14 == 0) goto L_0x107b
            java.lang.String r2 = "NotificationActionPinnedText"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedText     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x107b:
            java.lang.String r2 = "NotificationActionPinnedTextChannel"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedTextChannel     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x1092:
            java.lang.String r2 = "NotificationGroupAlbum"
            int r5 = org.telegram.messenger.R.string.NotificationGroupAlbum     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x17d1
        L_0x10a9:
            int r6 = org.telegram.messenger.R.string.NotificationGroupFew     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 0
            r9 = r15[r8]     // Catch:{ all -> 0x1CLASSNAME }
            r7[r8] = r9     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 1
            r9 = r15[r8]     // Catch:{ all -> 0x1CLASSNAME }
            r7[r8] = r9     // Catch:{ all -> 0x1CLASSNAME }
            r2 = 2
            r8 = r15[r2]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)     // Catch:{ all -> 0x1CLASSNAME }
            int r8 = r8.intValue()     // Catch:{ all -> 0x1CLASSNAME }
            r9 = 0
            java.lang.Object[] r11 = new java.lang.Object[r9]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r8, r11)     // Catch:{ all -> 0x1CLASSNAME }
            r7[r2] = r1     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r6, r7)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x17d1
        L_0x10d2:
            int r6 = org.telegram.messenger.R.string.NotificationGroupFew     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 0
            r9 = r15[r8]     // Catch:{ all -> 0x1CLASSNAME }
            r7[r8] = r9     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 1
            r9 = r15[r8]     // Catch:{ all -> 0x1CLASSNAME }
            r7[r8] = r9     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r8 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)     // Catch:{ all -> 0x1CLASSNAME }
            int r8 = r8.intValue()     // Catch:{ all -> 0x1CLASSNAME }
            r9 = 0
            java.lang.Object[] r11 = new java.lang.Object[r9]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r8, r11)     // Catch:{ all -> 0x1CLASSNAME }
            r7[r1] = r2     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r6, r7)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x17d1
        L_0x10fb:
            int r2 = org.telegram.messenger.R.string.NotificationGroupFew     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r9 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r9     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r9 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r9     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r7)     // Catch:{ all -> 0x1CLASSNAME }
            int r7 = r7.intValue()     // Catch:{ all -> 0x1CLASSNAME }
            r9 = 0
            java.lang.Object[] r11 = new java.lang.Object[r9]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r8, r7, r11)     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x17d1
        L_0x1124:
            int r2 = org.telegram.messenger.R.string.NotificationGroupFew     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r7)     // Catch:{ all -> 0x1CLASSNAME }
            int r7 = r7.intValue()     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 0
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r12, r7, r9)     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x17d1
        L_0x114d:
            java.lang.String r2 = "NotificationGroupForwardedFew"
            int r5 = org.telegram.messenger.R.string.NotificationGroupForwardedFew     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r7)     // Catch:{ all -> 0x1CLASSNAME }
            int r7 = r7.intValue()     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 0
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x1CLASSNAME }
            r8 = r28
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r8, r7, r9)     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x17d1
        L_0x117a:
            java.lang.String r2 = "UserAcceptedToGroupPushWithGroup"
            int r5 = org.telegram.messenger.R.string.UserAcceptedToGroupPushWithGroup     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x1191:
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            int r5 = org.telegram.messenger.R.string.NotificationGroupAddSelfMega     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x11a8:
            java.lang.String r2 = "NotificationGroupAddSelf"
            int r5 = org.telegram.messenger.R.string.NotificationGroupAddSelf     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x11bf:
            java.lang.String r2 = "NotificationGroupLeftMember"
            int r5 = org.telegram.messenger.R.string.NotificationGroupLeftMember     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x11d6:
            java.lang.String r2 = "NotificationGroupKickYou"
            int r5 = org.telegram.messenger.R.string.NotificationGroupKickYou     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x11ed:
            java.lang.String r2 = "NotificationGroupKickMember"
            int r5 = org.telegram.messenger.R.string.NotificationGroupKickMember     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x1204:
            java.lang.String r2 = "NotificationGroupInvitedYouToCall"
            int r5 = org.telegram.messenger.R.string.NotificationGroupInvitedYouToCall     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x121b:
            java.lang.String r2 = "NotificationGroupEndedCall"
            int r5 = org.telegram.messenger.R.string.NotificationGroupEndedCall     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x1232:
            java.lang.String r2 = "NotificationGroupInvitedToCall"
            int r5 = org.telegram.messenger.R.string.NotificationGroupInvitedToCall     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x124e:
            java.lang.String r2 = "NotificationGroupCreatedCall"
            int r5 = org.telegram.messenger.R.string.NotificationGroupCreatedCall     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x1265:
            java.lang.String r2 = "NotificationGroupAddMember"
            int r5 = org.telegram.messenger.R.string.NotificationGroupAddMember     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x1281:
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            int r5 = org.telegram.messenger.R.string.NotificationEditedGroupPhoto     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x1298:
            java.lang.String r2 = "NotificationEditedGroupName"
            int r5 = org.telegram.messenger.R.string.NotificationEditedGroupName     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x12af:
            java.lang.String r2 = "NotificationInvitedToGroup"
            int r5 = org.telegram.messenger.R.string.NotificationInvitedToGroup     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x12c6:
            java.lang.String r2 = "NotificationMessageGroupInvoice"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupInvoice     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "PaymentInvoice"
            int r2 = org.telegram.messenger.R.string.PaymentInvoice     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x12ea:
            java.lang.String r2 = "NotificationMessageGroupGameScored"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupGameScored     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 4
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 3
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x130b:
            java.lang.String r2 = "NotificationMessageGroupGame"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupGame     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachGame"
            int r2 = org.telegram.messenger.R.string.AttachGame     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x132f:
            java.lang.String r2 = "NotificationMessageGroupGif"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupGif     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachGif"
            int r2 = org.telegram.messenger.R.string.AttachGif     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x134e:
            java.lang.String r2 = "NotificationMessageGroupLiveLocation"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupLiveLocation     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachLiveLocation"
            int r2 = org.telegram.messenger.R.string.AttachLiveLocation     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x136d:
            java.lang.String r2 = "NotificationMessageGroupMap"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupMap     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachLocation"
            int r2 = org.telegram.messenger.R.string.AttachLocation     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x138c:
            java.lang.String r2 = "NotificationMessageGroupPoll2"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupPoll2     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "Poll"
            int r2 = org.telegram.messenger.R.string.Poll     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x13b0:
            java.lang.String r2 = "NotificationMessageGroupQuiz2"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupQuiz2     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "PollQuiz"
            int r2 = org.telegram.messenger.R.string.PollQuiz     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x13d4:
            java.lang.String r2 = "NotificationMessageGroupContact2"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupContact2     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachContact"
            int r2 = org.telegram.messenger.R.string.AttachContact     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x13f8:
            java.lang.String r2 = "NotificationMessageGroupAudio"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupAudio     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachAudio"
            int r2 = org.telegram.messenger.R.string.AttachAudio     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1417:
            int r2 = r15.length     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            if (r2 <= r1) goto L_0x1459
            r2 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x1CLASSNAME }
            if (r2 != 0) goto L_0x1459
            java.lang.String r2 = "NotificationMessageGroupStickerEmoji"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupStickerEmoji     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 0
            r9 = r15[r8]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r8] = r9     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 1
            r9 = r15[r8]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r8] = r9     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r8 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r8     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x1CLASSNAME }
            r2.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r1 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r2.append(r1)     // Catch:{ all -> 0x1CLASSNAME }
            r2.append(r11)     // Catch:{ all -> 0x1CLASSNAME }
            int r1 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r7, r1)     // Catch:{ all -> 0x1CLASSNAME }
            r2.append(r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1459:
            java.lang.String r2 = "NotificationMessageGroupSticker"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupSticker     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r8 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r8 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r8     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x1CLASSNAME }
            r1.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r2 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1.append(r2)     // Catch:{ all -> 0x1CLASSNAME }
            r1.append(r11)     // Catch:{ all -> 0x1CLASSNAME }
            int r2 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)     // Catch:{ all -> 0x1CLASSNAME }
            r1.append(r2)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x148a:
            java.lang.String r2 = "NotificationMessageGroupDocument"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupDocument     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachDocument"
            int r2 = org.telegram.messenger.R.string.AttachDocument     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x14a9:
            java.lang.String r2 = "NotificationMessageGroupRound"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupRound     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachRound"
            int r2 = org.telegram.messenger.R.string.AttachRound     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x14c8:
            java.lang.String r2 = "NotificationMessageGroupVideo"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupVideo     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachVideo"
            int r2 = org.telegram.messenger.R.string.AttachVideo     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x14e7:
            java.lang.String r2 = "NotificationMessageGroupPhoto"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupPhoto     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachPhoto"
            int r2 = org.telegram.messenger.R.string.AttachPhoto     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1506:
            java.lang.String r2 = "NotificationMessageGroupNoText"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupNoText     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "Message"
            int r2 = org.telegram.messenger.R.string.Message     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1525:
            java.lang.String r2 = "NotificationMessageGroupText"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupText     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x1CLASSNAME }
            r1 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1543:
            java.lang.String r1 = "ChannelMessageAlbum"
            int r2 = org.telegram.messenger.R.string.ChannelMessageAlbum     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x17d1
        L_0x1555:
            int r5 = org.telegram.messenger.R.string.ChannelMessageFew     // Catch:{ all -> 0x1CLASSNAME }
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r2[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)     // Catch:{ all -> 0x1CLASSNAME }
            int r8 = r8.intValue()     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r8, r9)     // Catch:{ all -> 0x1CLASSNAME }
            r2[r7] = r1     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r13, r5, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x17d1
        L_0x1578:
            int r5 = org.telegram.messenger.R.string.ChannelMessageFew     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)     // Catch:{ all -> 0x1CLASSNAME }
            int r8 = r8.intValue()     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r8, r9)     // Catch:{ all -> 0x1CLASSNAME }
            r1[r7] = r2     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r13, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x17d1
        L_0x159b:
            int r2 = org.telegram.messenger.R.string.ChannelMessageFew     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r6 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r5] = r6     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r7)     // Catch:{ all -> 0x1CLASSNAME }
            int r7 = r7.intValue()     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Object[] r9 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r8, r7, r9)     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r5     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r13, r2, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x17d1
        L_0x15be:
            int r2 = org.telegram.messenger.R.string.ChannelMessageFew     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r6 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r5] = r6     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r7)     // Catch:{ all -> 0x1CLASSNAME }
            int r7 = r7.intValue()     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Object[] r8 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r12, r7, r8)     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r5     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r13, r2, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x17d1
        L_0x15e1:
            int r2 = org.telegram.messenger.R.string.ChannelMessageFew     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r6 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r5] = r6     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r6 = "ForwardedMessageCount"
            r7 = 1
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)     // Catch:{ all -> 0x1CLASSNAME }
            int r7 = r7.intValue()     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Object[] r8 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r6, r7, r8)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = r5.toLowerCase()     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r1[r6] = r5     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r13, r2, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x17d1
        L_0x160b:
            java.lang.String r1 = "NotificationMessageGame"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGame     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachGame"
            int r2 = org.telegram.messenger.R.string.AttachGame     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1625:
            java.lang.String r1 = "ChannelMessageGIF"
            int r2 = org.telegram.messenger.R.string.ChannelMessageGIF     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachGif"
            int r2 = org.telegram.messenger.R.string.AttachGif     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x163f:
            java.lang.String r1 = "ChannelMessageLiveLocation"
            int r2 = org.telegram.messenger.R.string.ChannelMessageLiveLocation     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachLiveLocation"
            int r2 = org.telegram.messenger.R.string.AttachLiveLocation     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1659:
            java.lang.String r1 = "ChannelMessageMap"
            int r2 = org.telegram.messenger.R.string.ChannelMessageMap     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachLocation"
            int r2 = org.telegram.messenger.R.string.AttachLocation     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1673:
            java.lang.String r2 = "ChannelMessagePoll2"
            int r5 = org.telegram.messenger.R.string.ChannelMessagePoll2     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "Poll"
            int r2 = org.telegram.messenger.R.string.Poll     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1692:
            java.lang.String r2 = "ChannelMessageQuiz2"
            int r5 = org.telegram.messenger.R.string.ChannelMessageQuiz2     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "QuizPoll"
            int r2 = org.telegram.messenger.R.string.QuizPoll     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x16b1:
            java.lang.String r2 = "ChannelMessageContact2"
            int r5 = org.telegram.messenger.R.string.ChannelMessageContact2     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachContact"
            int r2 = org.telegram.messenger.R.string.AttachContact     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x16d0:
            java.lang.String r1 = "ChannelMessageAudio"
            int r2 = org.telegram.messenger.R.string.ChannelMessageAudio     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachAudio"
            int r2 = org.telegram.messenger.R.string.AttachAudio     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x16ea:
            int r2 = r15.length     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            if (r2 <= r5) goto L_0x1727
            r2 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x1CLASSNAME }
            if (r2 != 0) goto L_0x1727
            java.lang.String r2 = "ChannelMessageStickerEmoji"
            int r5 = org.telegram.messenger.R.string.ChannelMessageStickerEmoji     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r8 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r8 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r8     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x1CLASSNAME }
            r1.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r2 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1.append(r2)     // Catch:{ all -> 0x1CLASSNAME }
            r1.append(r11)     // Catch:{ all -> 0x1CLASSNAME }
            int r2 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)     // Catch:{ all -> 0x1CLASSNAME }
            r1.append(r2)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1727:
            java.lang.String r1 = "ChannelMessageSticker"
            int r2 = org.telegram.messenger.R.string.ChannelMessageSticker     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r8 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r8     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            int r1 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r7, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x173f:
            java.lang.String r1 = "ChannelMessageDocument"
            int r2 = org.telegram.messenger.R.string.ChannelMessageDocument     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachDocument"
            int r2 = org.telegram.messenger.R.string.AttachDocument     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1759:
            java.lang.String r1 = "ChannelMessageRound"
            int r2 = org.telegram.messenger.R.string.ChannelMessageRound     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachRound"
            int r2 = org.telegram.messenger.R.string.AttachRound     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1773:
            java.lang.String r1 = "ChannelMessageVideo"
            int r2 = org.telegram.messenger.R.string.ChannelMessageVideo     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachVideo"
            int r2 = org.telegram.messenger.R.string.AttachVideo     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x178d:
            java.lang.String r1 = "ChannelMessagePhoto"
            int r2 = org.telegram.messenger.R.string.ChannelMessagePhoto     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachPhoto"
            int r2 = org.telegram.messenger.R.string.AttachPhoto     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x17a7:
            java.lang.String r1 = "ChannelMessageNoText"
            int r2 = org.telegram.messenger.R.string.ChannelMessageNoText     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "Message"
            int r2 = org.telegram.messenger.R.string.Message     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x17c1:
            java.lang.String r1 = "NotificationMessageAlbum"
            int r2 = org.telegram.messenger.R.string.NotificationMessageAlbum     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
        L_0x17d1:
            r2 = 1
            goto L_0x1b24
        L_0x17d4:
            int r5 = org.telegram.messenger.R.string.NotificationMessageFew     // Catch:{ all -> 0x1CLASSNAME }
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r2[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 1
            r9 = r15[r8]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x1CLASSNAME }
            int r9 = r9.intValue()     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r9, r11)     // Catch:{ all -> 0x1CLASSNAME }
            r2[r8] = r1     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r6, r5, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x17d1
        L_0x17f6:
            int r5 = org.telegram.messenger.R.string.NotificationMessageFew     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 1
            r9 = r15[r8]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x1CLASSNAME }
            int r9 = r9.intValue()     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r9, r11)     // Catch:{ all -> 0x1CLASSNAME }
            r1[r8] = r2     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r6, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x17d1
        L_0x1818:
            int r2 = org.telegram.messenger.R.string.NotificationMessageFew     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r9 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x1CLASSNAME }
            int r9 = r9.intValue()     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Object[] r11 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r8, r9, r11)     // Catch:{ all -> 0x1CLASSNAME }
            r1[r7] = r5     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r6, r2, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x17d1
        L_0x183a:
            int r2 = org.telegram.messenger.R.string.NotificationMessageFew     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)     // Catch:{ all -> 0x1CLASSNAME }
            int r8 = r8.intValue()     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Object[] r9 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r12, r8, r9)     // Catch:{ all -> 0x1CLASSNAME }
            r1[r7] = r5     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r6, r2, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x17d1
        L_0x185d:
            r8 = r28
            java.lang.String r2 = "NotificationMessageForwardFew"
            int r5 = org.telegram.messenger.R.string.NotificationMessageForwardFew     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r9 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x1CLASSNAME }
            int r9 = r9.intValue()     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.Object[] r11 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r8, r9, r11)     // Catch:{ all -> 0x1CLASSNAME }
            r1[r7] = r6     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x17d1
        L_0x1884:
            java.lang.String r2 = "NotificationMessageInvoice"
            int r5 = org.telegram.messenger.R.string.NotificationMessageInvoice     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "PaymentInvoice"
            int r2 = org.telegram.messenger.R.string.PaymentInvoice     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x18a3:
            java.lang.String r2 = "NotificationMessageGameScored"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGameScored     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 0
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1
            r8 = r15[r7]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r7] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            r7 = r15[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r1] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r5, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x18bf:
            java.lang.String r2 = "NotificationMessageGame"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGame     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachGame"
            int r2 = org.telegram.messenger.R.string.AttachGame     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x18de:
            java.lang.String r1 = "NotificationMessageGif"
            int r2 = org.telegram.messenger.R.string.NotificationMessageGif     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachGif"
            int r2 = org.telegram.messenger.R.string.AttachGif     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x18f8:
            java.lang.String r1 = "NotificationMessageLiveLocation"
            int r2 = org.telegram.messenger.R.string.NotificationMessageLiveLocation     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachLiveLocation"
            int r2 = org.telegram.messenger.R.string.AttachLiveLocation     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1912:
            java.lang.String r1 = "NotificationMessageMap"
            int r2 = org.telegram.messenger.R.string.NotificationMessageMap     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachLocation"
            int r2 = org.telegram.messenger.R.string.AttachLocation     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x192c:
            java.lang.String r2 = "NotificationMessagePoll2"
            int r5 = org.telegram.messenger.R.string.NotificationMessagePoll2     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "Poll"
            int r2 = org.telegram.messenger.R.string.Poll     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x194b:
            java.lang.String r2 = "NotificationMessageQuiz2"
            int r5 = org.telegram.messenger.R.string.NotificationMessageQuiz2     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "QuizPoll"
            int r2 = org.telegram.messenger.R.string.QuizPoll     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x196a:
            java.lang.String r2 = "NotificationMessageContact2"
            int r5 = org.telegram.messenger.R.string.NotificationMessageContact2     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachContact"
            int r2 = org.telegram.messenger.R.string.AttachContact     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1989:
            java.lang.String r1 = "NotificationMessageAudio"
            int r2 = org.telegram.messenger.R.string.NotificationMessageAudio     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachAudio"
            int r2 = org.telegram.messenger.R.string.AttachAudio     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x19a3:
            int r2 = r15.length     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            if (r2 <= r5) goto L_0x19e0
            r2 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x1CLASSNAME }
            if (r2 != 0) goto L_0x19e0
            java.lang.String r2 = "NotificationMessageStickerEmoji"
            int r5 = org.telegram.messenger.R.string.NotificationMessageStickerEmoji     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r8 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r8     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r8 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r8     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x1CLASSNAME }
            r1.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r2 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1.append(r2)     // Catch:{ all -> 0x1CLASSNAME }
            r1.append(r11)     // Catch:{ all -> 0x1CLASSNAME }
            int r2 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)     // Catch:{ all -> 0x1CLASSNAME }
            r1.append(r2)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x19e0:
            java.lang.String r1 = "NotificationMessageSticker"
            int r2 = org.telegram.messenger.R.string.NotificationMessageSticker     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r8 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r8     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            int r1 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r7, r1)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x19f8:
            java.lang.String r1 = "NotificationMessageDocument"
            int r2 = org.telegram.messenger.R.string.NotificationMessageDocument     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachDocument"
            int r2 = org.telegram.messenger.R.string.AttachDocument     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1a12:
            java.lang.String r1 = "NotificationMessageRound"
            int r2 = org.telegram.messenger.R.string.NotificationMessageRound     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachRound"
            int r2 = org.telegram.messenger.R.string.AttachRound     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1a2c:
            java.lang.String r1 = "ActionTakeScreenshoot"
            int r2 = org.telegram.messenger.R.string.ActionTakeScreenshoot     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r2 = "un1"
            r5 = 0
            r6 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = r1.replace(r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b23
        L_0x1a3f:
            java.lang.String r1 = "NotificationMessageSDVideo"
            int r2 = org.telegram.messenger.R.string.NotificationMessageSDVideo     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachDestructingVideo"
            int r2 = org.telegram.messenger.R.string.AttachDestructingVideo     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1a59:
            java.lang.String r1 = "NotificationMessageVideo"
            int r2 = org.telegram.messenger.R.string.NotificationMessageVideo     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachVideo"
            int r2 = org.telegram.messenger.R.string.AttachVideo     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1a72:
            java.lang.String r1 = "NotificationMessageSDPhoto"
            int r2 = org.telegram.messenger.R.string.NotificationMessageSDPhoto     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachDestructingPhoto"
            int r2 = org.telegram.messenger.R.string.AttachDestructingPhoto     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1a8b:
            java.lang.String r1 = "NotificationMessagePhoto"
            int r2 = org.telegram.messenger.R.string.NotificationMessagePhoto     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "AttachPhoto"
            int r2 = org.telegram.messenger.R.string.AttachPhoto     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1aa4:
            java.lang.String r1 = "NotificationMessageNoText"
            int r2 = org.telegram.messenger.R.string.NotificationMessageNoText     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r5 = 0
            r7 = r15[r5]     // Catch:{ all -> 0x1CLASSNAME }
            r6[r5] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r2, r6)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "Message"
            int r2 = org.telegram.messenger.R.string.Message     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1abd:
            java.lang.String r2 = "NotificationMessageText"
            int r5 = org.telegram.messenger.R.string.NotificationMessageText     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            r1 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
        L_0x1ad4:
            r2 = 0
            r51 = r5
            r5 = r1
            r1 = r51
            goto L_0x1b25
        L_0x1adb:
            java.lang.String r2 = "NotificationMessageRecurringPay"
            int r5 = org.telegram.messenger.R.string.NotificationMessageRecurringPay     // Catch:{ all -> 0x1CLASSNAME }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 0
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            r6 = 1
            r7 = r15[r6]     // Catch:{ all -> 0x1CLASSNAME }
            r1[r6] = r7     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r2, r5, r1)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = "PaymentInvoice"
            int r2 = org.telegram.messenger.R.string.PaymentInvoice     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1ad4
        L_0x1af9:
            if (r1 == 0) goto L_0x1b0f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x1CLASSNAME }
            r1.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r2 = "unhandled loc_key = "
            r1.append(r2)     // Catch:{ all -> 0x1CLASSNAME }
            r1.append(r10)     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.messenger.FileLog.w(r1)     // Catch:{ all -> 0x1CLASSNAME }
        L_0x1b0f:
            r1 = 0
            goto L_0x1b23
        L_0x1b11:
            r17 = r2
            r47 = r5
            r50 = r7
            r35 = r8
            r46 = r11
            r45 = r12
            r49 = r13
            java.lang.String r1 = getReactedText(r10, r15)     // Catch:{ all -> 0x1CLASSNAME }
        L_0x1b23:
            r2 = 0
        L_0x1b24:
            r5 = 0
        L_0x1b25:
            if (r1 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x1CLASSNAME }
            r6.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r7 = r50
            r6.id = r7     // Catch:{ all -> 0x1CLASSNAME }
            r14 = r33
            r6.random_id = r14     // Catch:{ all -> 0x1CLASSNAME }
            if (r5 == 0) goto L_0x1b37
            goto L_0x1b38
        L_0x1b37:
            r5 = r1
        L_0x1b38:
            r6.message = r5     // Catch:{ all -> 0x1CLASSNAME }
            r7 = 1000(0x3e8, double:4.94E-321)
            long r7 = r55 / r7
            int r5 = (int) r7     // Catch:{ all -> 0x1CLASSNAME }
            r6.date = r5     // Catch:{ all -> 0x1CLASSNAME }
            if (r16 == 0) goto L_0x1b4a
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r5 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x1CLASSNAME }
            r5.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r6.action = r5     // Catch:{ all -> 0x1CLASSNAME }
        L_0x1b4a:
            if (r49 == 0) goto L_0x1b53
            int r5 = r6.flags     // Catch:{ all -> 0x1CLASSNAME }
            r7 = -2147483648(0xfffffffvar_, float:-0.0)
            r5 = r5 | r7
            r6.flags = r5     // Catch:{ all -> 0x1CLASSNAME }
        L_0x1b53:
            r6.dialog_id = r3     // Catch:{ all -> 0x1CLASSNAME }
            r3 = 0
            int r5 = (r47 > r3 ? 1 : (r47 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x1b69
            org.telegram.tgnet.TLRPC$TL_peerChannel r3 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x1CLASSNAME }
            r3.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r6.peer_id = r3     // Catch:{ all -> 0x1CLASSNAME }
            r12 = r47
            r3.channel_id = r12     // Catch:{ all -> 0x1CLASSNAME }
            r12 = r26
            goto L_0x1b88
        L_0x1b69:
            r3 = 0
            int r5 = (r26 > r3 ? 1 : (r26 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x1b7b
            org.telegram.tgnet.TLRPC$TL_peerChat r3 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x1CLASSNAME }
            r3.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r6.peer_id = r3     // Catch:{ all -> 0x1CLASSNAME }
            r12 = r26
            r3.chat_id = r12     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1b88
        L_0x1b7b:
            r12 = r26
            org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1CLASSNAME }
            r3.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r6.peer_id = r3     // Catch:{ all -> 0x1CLASSNAME }
            r4 = r24
            r3.user_id = r4     // Catch:{ all -> 0x1CLASSNAME }
        L_0x1b88:
            int r3 = r6.flags     // Catch:{ all -> 0x1CLASSNAME }
            r3 = r3 | 256(0x100, float:3.59E-43)
            r6.flags = r3     // Catch:{ all -> 0x1CLASSNAME }
            r3 = 0
            int r5 = (r42 > r3 ? 1 : (r42 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x1b9e
            org.telegram.tgnet.TLRPC$TL_peerChat r3 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x1CLASSNAME }
            r3.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r6.from_id = r3     // Catch:{ all -> 0x1CLASSNAME }
            r3.chat_id = r12     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1bc6
        L_0x1b9e:
            r3 = 0
            int r5 = (r36 > r3 ? 1 : (r36 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x1bb0
            org.telegram.tgnet.TLRPC$TL_peerChannel r3 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x1CLASSNAME }
            r3.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r6.from_id = r3     // Catch:{ all -> 0x1CLASSNAME }
            r4 = r36
            r3.channel_id = r4     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1bc6
        L_0x1bb0:
            r3 = 0
            int r5 = (r40 > r3 ? 1 : (r40 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x1bc2
            org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1CLASSNAME }
            r3.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r6.from_id = r3     // Catch:{ all -> 0x1CLASSNAME }
            r4 = r40
            r3.user_id = r4     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1bc6
        L_0x1bc2:
            org.telegram.tgnet.TLRPC$Peer r3 = r6.peer_id     // Catch:{ all -> 0x1CLASSNAME }
            r6.from_id = r3     // Catch:{ all -> 0x1CLASSNAME }
        L_0x1bc6:
            if (r38 != 0) goto L_0x1bcd
            if (r16 == 0) goto L_0x1bcb
            goto L_0x1bcd
        L_0x1bcb:
            r3 = 0
            goto L_0x1bce
        L_0x1bcd:
            r3 = 1
        L_0x1bce:
            r6.mentioned = r3     // Catch:{ all -> 0x1CLASSNAME }
            r3 = r39
            r6.silent = r3     // Catch:{ all -> 0x1CLASSNAME }
            r14 = r20
            r6.from_scheduled = r14     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.messenger.MessageObject r3 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x1CLASSNAME }
            r19 = r3
            r20 = r29
            r21 = r6
            r22 = r1
            r23 = r44
            r24 = r46
            r25 = r2
            r26 = r45
            r27 = r49
            r28 = r35
            r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27, r28)     // Catch:{ all -> 0x1CLASSNAME }
            r1 = r17
            boolean r1 = r10.startsWith(r1)     // Catch:{ all -> 0x1CLASSNAME }
            if (r1 != 0) goto L_0x1CLASSNAME
            r1 = r32
            boolean r1 = r10.startsWith(r1)     // Catch:{ all -> 0x1CLASSNAME }
            if (r1 == 0) goto L_0x1CLASSNAME
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r1 = 0
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r1 = 1
        L_0x1CLASSNAME:
            r3.isReactionPush = r1     // Catch:{ all -> 0x1CLASSNAME }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x1CLASSNAME }
            r1.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r1.add(r3)     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.messenger.NotificationsController r2 = org.telegram.messenger.NotificationsController.getInstance(r29)     // Catch:{ all -> 0x1CLASSNAME }
            java.util.concurrent.CountDownLatch r3 = countDownLatch     // Catch:{ all -> 0x1CLASSNAME }
            r4 = 1
            r2.processNewMessages(r1, r4, r4, r3)     // Catch:{ all -> 0x1CLASSNAME }
            r8 = 0
            goto L_0x1CLASSNAME
        L_0x1c1b:
            r0 = move-exception
            r31 = r7
            goto L_0x1CLASSNAME
        L_0x1c1f:
            r31 = r7
        L_0x1CLASSNAME:
            r8 = 1
        L_0x1CLASSNAME:
            if (r8 == 0) goto L_0x1CLASSNAME
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1CLASSNAME }
            r1.countDown()     // Catch:{ all -> 0x1CLASSNAME }
        L_0x1CLASSNAME:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r29)     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r29)     // Catch:{ all -> 0x1CLASSNAME }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1d62
        L_0x1CLASSNAME:
            r0 = move-exception
            r1 = r0
            r5 = r10
            r4 = r29
            goto L_0x1d08
        L_0x1c3c:
            r0 = move-exception
            r31 = r7
        L_0x1c3f:
            r10 = r32
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r0 = move-exception
            r31 = r7
            r10 = r9
        L_0x1CLASSNAME:
            r1 = r0
            r5 = r10
        L_0x1CLASSNAME:
            r4 = r29
            goto L_0x1d11
        L_0x1c4c:
            r0 = move-exception
            r29 = r4
            goto L_0x1d0c
        L_0x1CLASSNAME:
            r29 = r4
            r31 = r7
            r10 = r9
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda0 r2 = new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda0     // Catch:{ all -> 0x1CLASSNAME }
            r4 = r29
            r2.<init>(r4)     // Catch:{ all -> 0x1d05 }
            r1.postRunnable(r2)     // Catch:{ all -> 0x1d05 }
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1d05 }
            r1.countDown()     // Catch:{ all -> 0x1d05 }
            return
        L_0x1CLASSNAME:
            r0 = move-exception
            r4 = r29
            goto L_0x1d06
        L_0x1c6d:
            r31 = r7
            r10 = r9
            org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda1 r1 = new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda1     // Catch:{ all -> 0x1d05 }
            r1.<init>(r4)     // Catch:{ all -> 0x1d05 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ all -> 0x1d05 }
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1d05 }
            r1.countDown()     // Catch:{ all -> 0x1d05 }
            return
        L_0x1c7e:
            r8 = r6
            r31 = r7
            r10 = r9
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x1d05 }
            r1.<init>()     // Catch:{ all -> 0x1d05 }
            r2 = 0
            r1.popup = r2     // Catch:{ all -> 0x1d05 }
            r2 = 2
            r1.flags = r2     // Catch:{ all -> 0x1d05 }
            r2 = 1000(0x3e8, double:4.94E-321)
            long r2 = r55 / r2
            int r3 = (int) r2     // Catch:{ all -> 0x1d05 }
            r1.inbox_date = r3     // Catch:{ all -> 0x1d05 }
            java.lang.String r2 = "message"
            java.lang.String r2 = r8.getString(r2)     // Catch:{ all -> 0x1d05 }
            r1.message = r2     // Catch:{ all -> 0x1d05 }
            java.lang.String r2 = "announcement"
            r1.type = r2     // Catch:{ all -> 0x1d05 }
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ all -> 0x1d05 }
            r2.<init>()     // Catch:{ all -> 0x1d05 }
            r1.media = r2     // Catch:{ all -> 0x1d05 }
            org.telegram.tgnet.TLRPC$TL_updates r2 = new org.telegram.tgnet.TLRPC$TL_updates     // Catch:{ all -> 0x1d05 }
            r2.<init>()     // Catch:{ all -> 0x1d05 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r3 = r2.updates     // Catch:{ all -> 0x1d05 }
            r3.add(r1)     // Catch:{ all -> 0x1d05 }
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1d05 }
            org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda3 r3 = new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda3     // Catch:{ all -> 0x1d05 }
            r3.<init>(r4, r2)     // Catch:{ all -> 0x1d05 }
            r1.postRunnable(r3)     // Catch:{ all -> 0x1d05 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1d05 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1d05 }
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1d05 }
            r1.countDown()     // Catch:{ all -> 0x1d05 }
            return
        L_0x1cc8:
            r31 = r7
            r10 = r9
            java.lang.String r1 = "dc"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x1d05 }
            java.lang.String r2 = "addr"
            java.lang.String r2 = r11.getString(r2)     // Catch:{ all -> 0x1d05 }
            java.lang.String r3 = ":"
            java.lang.String[] r2 = r2.split(r3)     // Catch:{ all -> 0x1d05 }
            int r3 = r2.length     // Catch:{ all -> 0x1d05 }
            r5 = 2
            if (r3 == r5) goto L_0x1ce7
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1d05 }
            r1.countDown()     // Catch:{ all -> 0x1d05 }
            return
        L_0x1ce7:
            r3 = 0
            r3 = r2[r3]     // Catch:{ all -> 0x1d05 }
            r5 = 1
            r2 = r2[r5]     // Catch:{ all -> 0x1d05 }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ all -> 0x1d05 }
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1d05 }
            r5.applyDatacenterAddress(r1, r3, r2)     // Catch:{ all -> 0x1d05 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1d05 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1d05 }
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1d05 }
            r1.countDown()     // Catch:{ all -> 0x1d05 }
            return
        L_0x1d05:
            r0 = move-exception
        L_0x1d06:
            r1 = r0
            r5 = r10
        L_0x1d08:
            r7 = r31
            goto L_0x1d11
        L_0x1d0b:
            r0 = move-exception
        L_0x1d0c:
            r31 = r7
            r10 = r9
            r1 = r0
            r5 = r10
        L_0x1d11:
            r2 = -1
            goto L_0x1d2a
        L_0x1d13:
            r0 = move-exception
            r31 = r7
            r10 = r9
            r1 = r0
            r5 = r10
        L_0x1d19:
            r2 = -1
            r4 = -1
            goto L_0x1d2a
        L_0x1d1c:
            r0 = move-exception
            r31 = r7
        L_0x1d1f:
            r1 = r0
            r2 = -1
            r4 = -1
            r5 = 0
            goto L_0x1d2a
        L_0x1d24:
            r0 = move-exception
            r1 = r0
            r2 = -1
            r4 = -1
            r5 = 0
            r7 = 0
        L_0x1d2a:
            if (r4 == r2) goto L_0x1d3c
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r4)
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
            r2.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r2 = countDownLatch
            r2.countDown()
            goto L_0x1d3f
        L_0x1d3c:
            onDecryptError()
        L_0x1d3f:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x1d5f
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
        L_0x1d5f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1d62:
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
